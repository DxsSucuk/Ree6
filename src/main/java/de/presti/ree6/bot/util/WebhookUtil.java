package de.presti.ree6.bot.util;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookMessage;
import de.presti.ree6.bot.BotWorker;
import de.presti.ree6.logger.LogMessage;
import de.presti.ree6.main.Main;
import de.presti.ree6.sql.SQLSession;
import de.presti.ree6.sql.entities.webhook.*;
import de.presti.ree6.sql.entities.webhook.base.Webhook;
import de.presti.ree6.sql.entities.webhook.base.WebhookSocial;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Class to handle Webhook sends.
 */
@Slf4j
public class WebhookUtil {

    /**
     * Constructor should not be called, since it is a utility class that doesn't need an instance.
     *
     * @throws IllegalStateException it is a utility class.
     */
    private WebhookUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Send a Webhook-message to the wanted Webhook.
     *
     * @param message the MessageContent.
     * @param webhook the Webhook.
     */
    public static void sendWebhook(WebhookMessage message, Webhook webhook) {
        sendWebhook(null, message, webhook.getWebhookId(), webhook.getToken(), false);
    }

    /**
     * Send a Webhook-message to the wanted Webhook.
     *
     * @param loggerMessage the MessageContent, if it has been merged.
     * @param message       the MessageContent.
     * @param webhook       the Webhook.
     * @param isLog         is the Webhook Message a Log-Message?
     */
    public static void sendWebhook(LogMessage loggerMessage, WebhookMessage message, Webhook webhook, boolean isLog) {
        sendWebhook(loggerMessage, message, webhook.getWebhookId(), webhook.getToken(), isLog);
    }

    /**
     * Send a Webhook-message to the wanted Webhook.
     *
     * @param message the MessageContent.
     * @param webhook the Webhook.
     */
    public static void sendWebhook(WebhookMessage message, WebhookSocial webhook) {
        sendWebhook(null, message, webhook.getWebhookId(), webhook.getToken(), false);
    }

    /**
     * Send a Webhook-message to the wanted Webhook.
     *
     * @param loggerMessage the MessageContent, if it has been merged.
     * @param message       the MessageContent.
     * @param webhook       the Webhook.
     * @param isLog         is the Webhook Message a Log-Message?
     */
    public static void sendWebhook(LogMessage loggerMessage, WebhookMessage message, WebhookSocial webhook, boolean isLog) {
        sendWebhook(loggerMessage, message, webhook.getWebhookId(), webhook.getToken(), isLog);
    }


    /**
     * Send a Webhook-message to the wanted Webhook.
     *
     * @param loggerMessage the MessageContent, if it has been merged.
     * @param message       the MessageContent.
     * @param webhookId     the ID of the Webhook.
     * @param webhookToken  the Auth-Token of the Webhook.
     * @param isLog         is the Webhook Message a Log-Message?
     */
    public static void sendWebhook(LogMessage loggerMessage, WebhookMessage message, long webhookId, String webhookToken, boolean isLog) {
        Main.getInstance().logAnalytic("Received a Webhook to send. (Log-Typ: {})", isLog ? loggerMessage != null ? loggerMessage.getType().name() : "NONE-LOG" : "NONE-LOG");
        // Check if the given data is valid.
        if (webhookToken.contains("Not setup!") || webhookId == 0) return;

        // Check if the given data is in the Database.
        if (isLog) {
            SQLSession.getSqlConnector().getSqlWorker().existsLogData(webhookId, webhookToken).thenAccept(x -> {
                if (!x) {
                    // If not, inform about invalid send.
                    log.error("[Webhook] Invalid Webhook: {} - {}", webhookId, webhookToken);
                    return;
                } else {
                    // Check if the LoggerMessage is canceled.
                    if ((loggerMessage == null || loggerMessage.isCanceled())) {
                        // If so, inform about invalid send.
                        log.error("[Webhook] Got a Invalid or canceled LoggerMessage!");
                        return;
                    }

                    sendWebhookMessage(loggerMessage, message, webhookId, webhookToken, true);
                }
            });
        } else {
            sendWebhookMessage(loggerMessage, message, webhookId, webhookToken, false);
        }
    }

    private static void sendWebhookMessage(LogMessage loggerMessage, WebhookMessage message, long webhookId, String webhookToken, boolean isLog) {
        // Try sending a Webhook to the given data.
        try (WebhookClient wcl = WebhookClient.withId(webhookId, webhookToken)) {
            // Send the message and handle exceptions.
            wcl.send(message).exceptionally(throwable -> {
                // If error 404 comes, that means that the webhook is invalid.
                if (throwable.getMessage().contains("failure 404")) {

                    // Inform and delete invalid webhook.
                    if (isLog) {
                        SQLSession.getSqlConnector().getSqlWorker().deleteLogWebhook(webhookId, webhookToken);
                        log.error("[Webhook] Deleted invalid Webhook: {} - {}", webhookId, webhookToken);
                    } else {
                        // TODO:: this has become worst so please for the love of god find a better solution brain.
                        SQLSession.getSqlConnector().getSqlWorker().getEntity(new WebhookWelcome(), "FROM WebhookWelcome WHERE webhookId = :cid AND token = :token",
                                Map.of("cid", String.valueOf(webhookId), "token", webhookToken)).thenAccept(welcome -> {
                            if (welcome != null) {
                                SQLSession.getSqlConnector().getSqlWorker().deleteEntity(welcome);
                                log.error("[Webhook] Deleted invalid Webhook: {} - {}", webhookId, webhookToken);
                            } else {
                                SQLSession.getSqlConnector().getSqlWorker().getEntity(new WebhookYouTube(), "FROM WebhookYouTube WHERE webhookId = :cid AND token = :token",
                                        Map.of("cid", String.valueOf(webhookId), "token", webhookToken)).thenAccept(webhookYouTube -> {
                                    if (webhookYouTube != null) {
                                        SQLSession.getSqlConnector().getSqlWorker().deleteEntity(webhookYouTube);
                                        log.error("[Webhook] Deleted invalid Webhook: {} - {}", webhookId, webhookToken);
                                    } else {
                                        SQLSession.getSqlConnector().getSqlWorker().getEntity(new WebhookTwitter(), "FROM WebhookTwitter WHERE webhookId = :cid AND token = :token",
                                                Map.of("cid", String.valueOf(webhookId), "token", webhookToken)).thenAccept(webhookTwitter -> {
                                            if (webhookTwitter != null) {
                                                SQLSession.getSqlConnector().getSqlWorker().deleteEntity(webhookTwitter);
                                                log.error("[Webhook] Deleted invalid Webhook: {} - {}", webhookId, webhookToken);
                                            } else {
                                                SQLSession.getSqlConnector().getSqlWorker().getEntity(new WebhookTwitch(), "FROM WebhookTwitch WHERE webhookId = :cid AND token = :token",
                                                        Map.of("cid", String.valueOf(webhookId), "token", webhookToken)).thenAccept(webhookTwitch -> {

                                                    if (webhookTwitch != null) {
                                                        SQLSession.getSqlConnector().getSqlWorker().deleteEntity(webhookTwitch);
                                                        log.error("[Webhook] Deleted invalid Webhook: {} - {}", webhookId, webhookToken);
                                                    } else {
                                                        SQLSession.getSqlConnector().getSqlWorker().getEntity(new WebhookReddit(), "FROM WebhookReddit WHERE webhookId = :cid AND token = :token",
                                                                Map.of("cid", String.valueOf(webhookId), "token", webhookToken)).thenAccept(webhookReddit -> {

                                                            if (webhookReddit != null) {
                                                                SQLSession.getSqlConnector().getSqlWorker().deleteEntity(webhookReddit);
                                                                log.error("[Webhook] Deleted invalid Webhook: {} - {}", webhookId, webhookToken);
                                                            } else {
                                                                SQLSession.getSqlConnector().getSqlWorker().getEntity(new WebhookInstagram(), "FROM WebhookInstagram WHERE webhookId = :cid AND token = :token",
                                                                        Map.of("cid", String.valueOf(webhookId), "token", webhookToken)).thenAccept(webhookInstagram -> {

                                                                    if (webhookInstagram != null) {
                                                                        SQLSession.getSqlConnector().getSqlWorker().deleteEntity(webhookInstagram);
                                                                        log.error("[Webhook] Deleted invalid Webhook: {} - {}", webhookId, webhookToken);
                                                                    } else {
                                                                        SQLSession.getSqlConnector().getSqlWorker().getEntity(new WebhookTikTok(), "FROM WebhookTikTok WHERE webhookId = :cid AND token = :token", Map.of("cid", String.valueOf(webhookId), "token", webhookToken)).thenAccept(webhookTikTok -> {
                                                                            if (webhookTikTok != null) {
                                                                                log.error("[Webhook] Deleted invalid Webhook: {} - {}", webhookId, webhookToken);
                                                                            } else {
                                                                                SQLSession.getSqlConnector().getSqlWorker().getEntity(new RSSFeed(), "FROM WebhookTikTok WHERE webhookId = :cid AND token = :token", Map.of("cid", String.valueOf(webhookId), "token", webhookToken)).thenAccept(rssFeed -> {
                                                                                    if (rssFeed != null) {
                                                                                        log.error("[Webhook] Deleted invalid Webhook: {} - {}", webhookId, webhookToken);
                                                                                    } else {
                                                                                        log.error("[Webhook] Invalid Webhook: {} - {}, has not been deleted since it is not a Log-Webhook.", webhookId, webhookToken);
                                                                                    }
                                                                                });
                                                                            }
                                                                        });
                                                                    }
                                                                });
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                } else if (throwable.getMessage().contains("failure 400")) {
                    // If 404 inform that the Message had an invalid Body.
                    log.error("[Webhook] Invalid Body with LogTyp: {}", loggerMessage.getType().name());
                }
                return null;
            });
        } catch (Exception ex) {
            // Inform that this is an Invalid Webhook.
            log.error("[Webhook] Invalid Webhook: {} - {}", webhookId, webhookToken);
            log.error("[Webhook] Exception: ", ex);
        }
    }

    /**
     * Delete a Webhook entry from the Guild.
     *
     * @param guildId       the ID of the Guild.
     * @param webhookEntity the Webhook entity.
     */
    public static void deleteWebhook(long guildId, Webhook webhookEntity) {
        // Get the Guild from the ID.
        Guild guild = BotWorker.getShardManager().getGuildById(guildId);

        if (guild != null) {
            // Delete the existing Webhook.
            guild.retrieveWebhooks()
                    .queue(webhooks -> webhooks.stream().filter(webhook -> webhook.getToken() != null)
                            .filter(webhook -> webhook.getIdLong() == webhookEntity.getWebhookId() &&
                                    webhook.getToken().equalsIgnoreCase(webhookEntity.getToken()))
                            .forEach(webhook -> webhook.delete().queue()));
        }
    }
}