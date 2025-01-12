package de.presti.ree6.commands.impl.community;

import de.presti.ree6.bot.BotConfig;
import de.presti.ree6.commands.Category;
import de.presti.ree6.commands.CommandEvent;
import de.presti.ree6.commands.interfaces.Command;
import de.presti.ree6.commands.interfaces.ICommand;
import de.presti.ree6.main.Main;
import de.presti.ree6.sql.SQLSession;
import de.presti.ree6.utils.apis.YouTubeAPIHandler;
import de.presti.ree6.utils.data.RegExUtil;
import de.presti.wrapper.entities.channel.ChannelResult;
import de.presti.wrapper.tiktok.TikTokWrapper;
import de.presti.wrapper.tiktok.entities.TikTokUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

import java.util.regex.Pattern;

/**
 * A Command to activate Notifications.
 */
@Command(name = "notifier", description = "command.description.notifier.default", category = Category.COMMUNITY)
public class Notifier implements ICommand {

    /**
     * The Pattern for Links.
     */
    private static final Pattern pattern = Pattern.compile(RegExUtil.URL_REGEX);

    /**
     * @inheritDoc
     */
    @Override
    public void onPerform(CommandEvent commandEvent) {
        if (!commandEvent.getGuild().getSelfMember().hasPermission(Permission.MANAGE_WEBHOOKS)) {
            commandEvent.reply(commandEvent.getResource("message.default.needPermission", Permission.MANAGE_WEBHOOKS.getName()));
            return;
        }

        if (!commandEvent.isSlashCommand()) {
            commandEvent.reply(commandEvent.getResource("command.perform.onlySlashSupported"));
            return;
        }

        if (!commandEvent.getMember().hasPermission(Permission.MANAGE_WEBHOOKS)) {
            commandEvent.reply(commandEvent.getResource("message.default.insufficientPermission", Permission.MANAGE_WEBHOOKS.getName()));
            return;
        }


        String command = commandEvent.getSubcommand();
        String commandGroup = commandEvent.getSubcommandGroup();
        OptionMapping urlMapping = commandEvent.getOption("url");
        OptionMapping nameMapping = commandEvent.getOption("name");
        OptionMapping channelMapping = commandEvent.getOption("channel");
        OptionMapping messageMapping = commandEvent.getOption("message");

        switch (commandGroup) {
            case "list" -> {
                StringBuilder end = new StringBuilder("\n");

                switch (command) {
                    case "instagram" ->
                            SQLSession.getSqlConnector().getSqlWorker().getAllInstagramUsers(commandEvent.getGuild().getIdLong()).subscribe(users -> {
                                for (String user : users) {
                                    end.append(user).append("\n");
                                }

                                commandEvent.reply(commandEvent.getResource("message.instagramNotifier.list", end.toString()), 10);
                            });

                    case "youtube" ->
                            SQLSession.getSqlConnector().getSqlWorker().getAllYouTubeChannels(commandEvent.getGuild().getIdLong()).subscribe(userList -> {
                                for (String users : userList) {

                                    ChannelResult channelResult = null;

                                    try {
                                        channelResult = YouTubeAPIHandler.getInstance().isValidChannelId(users) ?
                                                YouTubeAPIHandler.getInstance().getYouTubeChannelById(users) :
                                                YouTubeAPIHandler.getInstance().getYouTubeChannelBySearch(users);
                                    } catch (Exception ignore) {
                                    }

                                    end.append(users);

                                    if (channelResult != null) {
                                        end.append(" ").append("-").append(" ").append(channelResult.getTitle());
                                    }

                                    end.append("\n");
                                }

                                commandEvent.reply(commandEvent.getResource("message.youtubeNotifier.list", end.toString()), 10);
                            });

                    case "twitch" ->
                            SQLSession.getSqlConnector().getSqlWorker().getAllTwitchNames(commandEvent.getGuild().getIdLong()).subscribe(users -> {
                                for (String user : users) {
                                    end.append(user).append("\n");
                                }

                                commandEvent.reply(commandEvent.getResource("message.twitchNotifier.list", end.toString()), 10);
                            });

                    case "twitter" ->
                            SQLSession.getSqlConnector().getSqlWorker().getAllTwitterNames(commandEvent.getGuild().getIdLong()).subscribe(users -> {
                                for (String user : users) {
                                    end.append(user).append("\n");
                                }

                                commandEvent.reply(commandEvent.getResource("message.twitterNotifier.list", end.toString()), 10);
                            });

                    case "tiktok" ->
                            SQLSession.getSqlConnector().getSqlWorker().getAllTikTokNames(commandEvent.getGuild().getIdLong()).subscribe(users -> {
                                for (String user : users) {
                                    end.append(user);
                                    try {
                                        TikTokUser tikTokUser = TikTokWrapper.getUser(Long.parseLong(user), false);
                                        end.append(" ").append("-").append(" ").append(tikTokUser.getName());
                                    } catch (Exception ignore) {
                                    }
                                    end.append("\n");
                                }

                                commandEvent.reply(commandEvent.getResource("message.tiktokNotifier.list", end.toString()), 10);
                            });

                    case "reddit" ->
                            SQLSession.getSqlConnector().getSqlWorker().getAllSubreddits(commandEvent.getGuild().getIdLong()).subscribe(users -> {
                                for (String user : users) {
                                    end.append(user).append("\n");
                                }

                                commandEvent.reply(commandEvent.getResource("message.redditNotifier.list", end.toString()), 10);
                            });

                    case "rss" ->
                            SQLSession.getSqlConnector().getSqlWorker().getAllRSSUrls(commandEvent.getGuild().getIdLong()).subscribe(users -> {
                                for (String user : users) {
                                    end.append(user).append("\n");
                                }

                                commandEvent.reply(commandEvent.getResource("message.rssNotifier.list", end.toString()), 10);
                            });

                    default -> commandEvent.reply(commandEvent.getResource("message.default.invalidOption"));
                }
            }
            case "add" -> {
                switch (command) {
                    case "instagram" -> {
                        // Ignore this, because the Compiler would otherwise scream:
                        // "OMG YOU CANNOT INITIALIZE THE VARIABLE BELOW BECAUSE YOU CANT REACH IT!!!!"
                        if (true) {
                            commandEvent.reply("This feature is currently broken. Please wait for a fix.", 10);
                            return;
                        }

                        if (nameMapping == null || channelMapping == null) {
                            commandEvent.reply(commandEvent.getResource("message.default.invalidQuery"));
                            return;
                        }

                        if (!channelMapping.getAsChannel().getGuild().getId().equals(commandEvent.getGuild().getId())) {
                            commandEvent.reply(commandEvent.getResource("message.default.noMention.channel"), 5);
                            return;
                        }

                        String name = nameMapping.getAsString();
                        StandardGuildMessageChannel channel = channelMapping.getAsChannel().asStandardGuildMessageChannel();
                        channel.createWebhook(BotConfig.getBotName() + "-InstagramNotifier-" + name).queue(w -> {
                            if (messageMapping != null) {
                                SQLSession.getSqlConnector().getSqlWorker().addInstagramWebhook(commandEvent.getGuild().getIdLong(), channel.getIdLong(), w.getIdLong(), w.getToken(), name.toLowerCase(), messageMapping.getAsString());
                            } else {
                                SQLSession.getSqlConnector().getSqlWorker().addInstagramWebhook(commandEvent.getGuild().getIdLong(), channel.getIdLong(), w.getIdLong(), w.getToken(), name.toLowerCase());
                            }
                        });
                        commandEvent.reply(commandEvent.getResource("message.instagramNotifier.added", name), 5);

                        if (!Main.getInstance().getNotifier().getInstagramSonic().contains(name)) {
                            Main.getInstance().getNotifier().getInstagramSonic().add(name);
                        }
                    }

                    case "youtube" -> {
                        if (urlMapping == null || channelMapping == null) {
                            commandEvent.reply(commandEvent.getResource("message.default.invalidQuery"));
                            return;
                        }

                        if (!channelMapping.getAsChannel().getGuild().getId().equals(commandEvent.getGuild().getId())) {
                            commandEvent.reply(commandEvent.getResource("message.default.noMention.channel"), 5);
                            return;
                        }

                        String name = urlMapping.getAsString();

                        name = name.replaceAll("^(https?://)?(?:.*\\.)?(youtube\\.com)(?:$|\\/)", "")
                                .replace("channel/", "")
                                .replace("user/", "")
                                .replace("c/", "")
                                .replace("@", "");

                        ChannelResult channelResult;

                        try {
                            channelResult = YouTubeAPIHandler.getInstance().isValidChannelId(name) ?
                                    YouTubeAPIHandler.getInstance().getYouTubeChannelById(name) :
                                    YouTubeAPIHandler.getInstance().getYouTubeChannelBySearch(name);
                        } catch (Exception e) {
                            commandEvent.reply(commandEvent.getResource("message.default.invalidQuery", name), 5);
                            return;
                        }

                        if (channelResult == null) {
                            commandEvent.reply(commandEvent.getResource("message.default.invalidQuery", name), 5);
                            return;
                        }

                        name = channelResult.getTitle();

                        StandardGuildMessageChannel channel = channelMapping.getAsChannel().asStandardGuildMessageChannel();
                        channel.createWebhook(BotConfig.getBotName() + "-YoutubeNotifier-" + name).queue(w -> {
                            if (messageMapping != null) {
                                SQLSession.getSqlConnector().getSqlWorker().addYouTubeWebhook(commandEvent.getGuild().getIdLong(), channel.getIdLong(), w.getIdLong(), w.getToken(), channelResult.getId(), messageMapping.getAsString());
                            } else {
                                SQLSession.getSqlConnector().getSqlWorker().addYouTubeWebhook(commandEvent.getGuild().getIdLong(), channel.getIdLong(), w.getIdLong(), w.getToken(), channelResult.getId());
                            }
                        });
                        commandEvent.reply(commandEvent.getResource("message.youtubeNotifier.added", name), 5);

                        if (!Main.getInstance().getNotifier().getYouTubeSonic().contains(channelResult.getId())) {
                            Main.getInstance().getNotifier().getYouTubeSonic().add(channelResult.getId());
                        }
                    }

                    case "twitch" -> {
                        if (nameMapping == null || channelMapping == null) {
                            commandEvent.reply(commandEvent.getResource("message.default.invalidQuery"));
                            return;
                        }

                        if (!channelMapping.getAsChannel().getGuild().getId().equals(commandEvent.getGuild().getId())) {
                            commandEvent.reply(commandEvent.getResource("message.default.noMention.channel"), 5);
                            return;
                        }

                        String name = nameMapping.getAsString();
                        StandardGuildMessageChannel channel = channelMapping.getAsChannel().asStandardGuildMessageChannel();
                        channel.createWebhook(BotConfig.getBotName() + "-TwitchNotifier-" + name).queue(w -> {
                            if (messageMapping != null) {
                                SQLSession.getSqlConnector().getSqlWorker().addTwitchWebhook(commandEvent.getGuild().getIdLong(), channel.getIdLong(), w.getIdLong(), w.getToken(), name.toLowerCase(), messageMapping.getAsString());
                            } else {
                                SQLSession.getSqlConnector().getSqlWorker().addTwitchWebhook(commandEvent.getGuild().getIdLong(), channel.getIdLong(), w.getIdLong(), w.getToken(), name.toLowerCase());
                            }
                        });
                        commandEvent.reply(commandEvent.getResource("message.twitchNotifier.added", name), 5);

                        if (!Main.getInstance().getNotifier().getTwitchSonic().contains(name)) {
                            Main.getInstance().getNotifier().getTwitchSonic().add(name);
                        }
                    }

                    case "twitter" -> {
                        // Ignore this, because the Compiler would otherwise scream:
                        // "OMG YOU CANNOT INITIALIZE THE VARIABLE BELOW BECAUSE YOU CANT REACH IT!!!!"
                        if (true) {
                            commandEvent.reply("This feature is currently broken. Please wait for a fix.", 10);
                            return;
                        }

                        if (nameMapping == null || channelMapping == null) {
                            commandEvent.reply(commandEvent.getResource("message.default.invalidQuery"));
                            return;
                        }

                        if (!channelMapping.getAsChannel().getGuild().getId().equals(commandEvent.getGuild().getId())) {
                            commandEvent.reply(commandEvent.getResource("message.default.noMention.channel"), 5);
                            return;
                        }

                        String name = nameMapping.getAsString();
                        StandardGuildMessageChannel channel = channelMapping.getAsChannel().asStandardGuildMessageChannel();
                        channel.createWebhook(BotConfig.getBotName() + "-TwitterNotifier-" + name).queue(w -> {
                            if (messageMapping != null) {
                                SQLSession.getSqlConnector().getSqlWorker().addTwitterWebhook(commandEvent.getGuild().getIdLong(), channel.getIdLong(), w.getIdLong(), w.getToken(), name.toLowerCase(), messageMapping.getAsString());
                            } else {
                                SQLSession.getSqlConnector().getSqlWorker().addTwitterWebhook(commandEvent.getGuild().getIdLong(), channel.getIdLong(), w.getIdLong(), w.getToken(), name.toLowerCase());
                            }
                        });
                        commandEvent.reply(commandEvent.getResource("message.twitterNotifier.added", name), 5);

                        if (!Main.getInstance().getNotifier().getTwitterSonic().contains(name)) {
                            Main.getInstance().getNotifier().getTwitterSonic().add(name);
                        }
                    }

                    case "tiktok" -> {
                        if (nameMapping == null || channelMapping == null) {
                            commandEvent.reply(commandEvent.getResource("message.default.invalidQuery"));
                            return;
                        }

                        if (!channelMapping.getAsChannel().getGuild().getId().equals(commandEvent.getGuild().getId())) {
                            commandEvent.reply(commandEvent.getResource("message.default.noMention.channel"), 5);
                            return;
                        }

                        String name = nameMapping.getAsString();

                        if (name.startsWith("@")) {
                            name = name.substring(1);
                        }

                        try {
                            TikTokUser tikTokUser = TikTokWrapper.getUser(name, false);

                            StandardGuildMessageChannel channel = channelMapping.getAsChannel().asStandardGuildMessageChannel();

                            channel.createWebhook(BotConfig.getBotName() + "-TikTokNotifier-" + name).queue(w -> {
                                if (messageMapping != null) {
                                    SQLSession.getSqlConnector().getSqlWorker().addTikTokWebhook(commandEvent.getGuild().getIdLong(), channel.getIdLong(), w.getIdLong(), w.getToken(), tikTokUser.getId(), messageMapping.getAsString());
                                } else {
                                    SQLSession.getSqlConnector().getSqlWorker().addTikTokWebhook(commandEvent.getGuild().getIdLong(), channel.getIdLong(), w.getIdLong(), w.getToken(), tikTokUser.getId());
                                }
                            });
                            commandEvent.reply(commandEvent.getResource("message.tiktokNotifier.added", name), 5);

                            if (!Main.getInstance().getNotifier().getTikTokSonic().contains(Long.parseLong(tikTokUser.getId()))) {
                                Main.getInstance().getNotifier().getTikTokSonic().add(Long.parseLong(tikTokUser.getId()));
                            }
                        } catch (Exception e) {
                            commandEvent.reply(commandEvent.getResource("message.tiktokNotifier.invalidUser", name), 5);
                        }
                    }

                    case "reddit" -> {
                        if (nameMapping == null || channelMapping == null) {
                            commandEvent.reply(commandEvent.getResource("message.default.invalidQuery"));
                            return;
                        }

                        if (!channelMapping.getAsChannel().getGuild().getId().equals(commandEvent.getGuild().getId())) {
                            commandEvent.reply(commandEvent.getResource("message.default.noMention.channel"), 5);
                            return;
                        }

                        String name = nameMapping.getAsString();
                        StandardGuildMessageChannel channel = channelMapping.getAsChannel().asStandardGuildMessageChannel();
                        channel.createWebhook(BotConfig.getBotName() + "-RedditNotifier-" + name).queue(w -> {
                            if (messageMapping != null) {
                                SQLSession.getSqlConnector().getSqlWorker().addRedditWebhook(commandEvent.getGuild().getIdLong(), channel.getIdLong(), w.getIdLong(), w.getToken(), name.toLowerCase(), messageMapping.getAsString());
                            } else {
                                SQLSession.getSqlConnector().getSqlWorker().addRedditWebhook(commandEvent.getGuild().getIdLong(), channel.getIdLong(), w.getIdLong(), w.getToken(), name.toLowerCase());
                            }
                        });
                        commandEvent.reply(commandEvent.getResource("message.redditNotifier.added", name), 5);

                        if (!Main.getInstance().getNotifier().getRedditSonic().contains(name)) {
                            Main.getInstance().getNotifier().getRedditSonic().add(name);
                        }
                    }

                    case "rss" -> {
                        if (nameMapping == null || channelMapping == null) {
                            commandEvent.reply(commandEvent.getResource("message.default.invalidQuery"));
                            return;
                        }

                        if (!channelMapping.getAsChannel().getGuild().getId().equals(commandEvent.getGuild().getId())) {
                            commandEvent.reply(commandEvent.getResource("message.default.noMention.channel"), 5);
                            return;
                        }

                        String name = nameMapping.getAsString();

                        if (!pattern.matcher(name).matches()) {
                            commandEvent.reply(commandEvent.getResource("message.default.invalidUrl"), 5);
                            return;
                        }

                        StandardGuildMessageChannel channel = channelMapping.getAsChannel().asStandardGuildMessageChannel();
                        channel.createWebhook(BotConfig.getBotName() + "-RSSNotifier-" + name).queue(w ->
                                SQLSession.getSqlConnector().getSqlWorker().addRSSWebhook(commandEvent.getGuild().getIdLong(), channel.getIdLong(), w.getIdLong(), w.getToken(), name.toLowerCase()));
                        commandEvent.reply(commandEvent.getResource("message.rssNotifier.added", name), 5);

                        if (!Main.getInstance().getNotifier().getRssSonic().contains(name)) {
                            Main.getInstance().getNotifier().getRedditSonic().add(name);
                        }
                    }

                    default -> commandEvent.reply(commandEvent.getResource("message.default.invalidOption"));
                }
            }
            case "remove" -> {
                switch (command) {
                    case "instagram" -> {
                        if (nameMapping == null) {
                            commandEvent.reply(commandEvent.getResource("message.default.invalidQuery"));
                            return;
                        }

                        String name = nameMapping.getAsString();
                        SQLSession.getSqlConnector().getSqlWorker().removeInstagramWebhook(commandEvent.getGuild().getIdLong(), name);
                        commandEvent.reply(commandEvent.getResource("message.instagramNotifier.removed", name), 5);

                        if (Main.getInstance().getNotifier().getInstagramSonic().contains(name)) {
                            Main.getInstance().getNotifier().getInstagramSonic().remove(name);
                        }
                    }

                    case "youtube" -> {
                        if (nameMapping == null) {
                            commandEvent.reply(commandEvent.getResource("message.default.invalidQuery"));
                            return;
                        }

                        String name = nameMapping.getAsString();

                        name = name.replace("https://www.youtube.com/", "")
                                .replace("channel/", "")
                                .replace("user/", "")
                                .replace("c/", "")
                                .replace("@", "");

                        ChannelResult channelResult;
                        try {
                            channelResult = YouTubeAPIHandler.getInstance().isValidChannelId(name) ?
                                    YouTubeAPIHandler.getInstance().getYouTubeChannelById(name) :
                                    YouTubeAPIHandler.getInstance().getYouTubeChannelBySearch(name);
                        } catch (Exception e) {
                            commandEvent.reply(commandEvent.getResource("message.default.invalidQuery", name), 5);
                            return;
                        }

                        name = channelResult.getTitle();

                        SQLSession.getSqlConnector().getSqlWorker().removeYouTubeWebhook(commandEvent.getGuild().getIdLong(), channelResult.getId());
                        commandEvent.reply(commandEvent.getResource("message.youtubeNotifier.removed", name), 5);

                        if (Main.getInstance().getNotifier().getYouTubeSonic().contains(channelResult.getId())) {
                            Main.getInstance().getNotifier().getYouTubeSonic().remove(channelResult.getId());
                        }
                    }

                    case "twitch" -> {
                        if (nameMapping == null) {
                            commandEvent.reply(commandEvent.getResource("message.default.invalidQuery"));
                            return;
                        }

                        String name = nameMapping.getAsString();
                        SQLSession.getSqlConnector().getSqlWorker().removeTwitchWebhook(commandEvent.getGuild().getIdLong(), name);
                        commandEvent.reply(commandEvent.getResource("message.twitchNotifier.removed", name), 5);

                        if (Main.getInstance().getNotifier().getTwitchSonic().contains(name)) {
                            Main.getInstance().getNotifier().getTwitchSonic().remove(name);
                        }
                    }

                    case "twitter" -> {
                        if (nameMapping == null) {
                            commandEvent.reply(commandEvent.getResource("message.default.invalidQuery"));
                            return;
                        }

                        String name = nameMapping.getAsString();
                        SQLSession.getSqlConnector().getSqlWorker().removeTwitterWebhook(commandEvent.getGuild().getIdLong(), name);
                        commandEvent.reply(commandEvent.getResource("message.twitterNotifier.removed", name), 5);

                        if (Main.getInstance().getNotifier().getTwitterSonic().contains(name)) {
                            Main.getInstance().getNotifier().getTwitterSonic().remove(name);
                        }
                    }

                    case "tiktok" -> {
                        if (nameMapping == null) {
                            commandEvent.reply(commandEvent.getResource("message.default.invalidQuery"));
                            return;
                        }

                        String name = nameMapping.getAsString();

                        try {
                            TikTokUser tikTokUser = TikTokWrapper.getUser(name, false);

                            SQLSession.getSqlConnector().getSqlWorker().removeTikTokWebhook(commandEvent.getGuild().getIdLong(), tikTokUser.getId());

                            commandEvent.reply(commandEvent.getResource("message.tiktokNotifier.removed", name), 5);

                            if (Main.getInstance().getNotifier().getTikTokSonic().contains(Long.parseLong(tikTokUser.getId()))) {
                                Main.getInstance().getNotifier().getTikTokSonic().remove(Long.parseLong(tikTokUser.getId()));
                            }
                        } catch (Exception e) {
                            commandEvent.reply(commandEvent.getResource("message.tiktokNotifier.invalidUser", name), 5);
                        }
                    }

                    case "reddit" -> {
                        if (nameMapping == null) {
                            commandEvent.reply(commandEvent.getResource("message.default.invalidQuery"));
                            return;
                        }

                        String name = nameMapping.getAsString();
                        SQLSession.getSqlConnector().getSqlWorker().removeRedditWebhook(commandEvent.getGuild().getIdLong(), name);
                        commandEvent.reply(commandEvent.getResource("message.redditNotifier.removed", name), 5);

                        if (Main.getInstance().getNotifier().getRedditSonic().contains(name)) {
                            Main.getInstance().getNotifier().getRedditSonic().remove(name);
                        }
                    }

                    case "rss" -> {
                        if (nameMapping == null) {
                            commandEvent.reply(commandEvent.getResource("message.default.invalidQuery"));
                            return;
                        }

                        String name = nameMapping.getAsString();
                        SQLSession.getSqlConnector().getSqlWorker().removeRSSWebhook(commandEvent.getGuild().getIdLong(), name);
                        commandEvent.reply(commandEvent.getResource("message.rssNotifier.removed", name), 5);

                        if (Main.getInstance().getNotifier().getRssSonic().contains(name)) {
                            Main.getInstance().getNotifier().getRssSonic().remove(name);
                        }
                    }

                    default -> commandEvent.reply(commandEvent.getResource("message.default.invalidOption"));
                }
            }
            default -> commandEvent.reply(commandEvent.getResource("message.default.invalidOption"));
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public CommandData getCommandData() {
        return new CommandDataImpl("notifier", "command.description.notifier.default")
                .addSubcommandGroups(
                        new SubcommandGroupData("add", "command.description.notifier.add")
                                .addSubcommands(
                                        new SubcommandData("instagram", "command.description.instagramNotifier")
                                                .addOption(OptionType.STRING, "name", "The username.", true)
                                                .addOptions(new OptionData(OptionType.CHANNEL, "channel", "The channel.", true).setChannelTypes(ChannelType.NEWS, ChannelType.TEXT))
                                                .addOption(OptionType.STRING, "message", "Custom announcement message.", false),

                                        new SubcommandData("youtube", "command.description.youtubeNotifier")
                                                .addOption(OptionType.STRING, "url", "The YouTube channel url.", true)
                                                .addOptions(new OptionData(OptionType.CHANNEL, "channel", "The channel.", true).setChannelTypes(ChannelType.NEWS, ChannelType.TEXT))
                                                .addOption(OptionType.STRING, "message", "Custom announcement message.", false),

                                        new SubcommandData("twitch", "command.description.twitch")
                                                .addOption(OptionType.STRING, "name", "The Twitch channel name.", true)
                                                .addOptions(new OptionData(OptionType.CHANNEL, "channel", "The channel.", true).setChannelTypes(ChannelType.NEWS, ChannelType.TEXT))
                                                .addOption(OptionType.STRING, "message", "Custom announcement message.", false),

                                        new SubcommandData("twitter", "command.description.twitterNotifier")
                                                .addOption(OptionType.STRING, "name", "The Twitter username.", true)
                                                .addOptions(new OptionData(OptionType.CHANNEL, "channel", "The channel.", true).setChannelTypes(ChannelType.NEWS, ChannelType.TEXT))
                                                .addOption(OptionType.STRING, "message", "Custom announcement message.", false),

                                        new SubcommandData("tiktok", "command.description.tiktokNotifier")
                                                .addOption(OptionType.STRING, "name", "The TikTok username.", true)
                                                .addOptions(new OptionData(OptionType.CHANNEL, "channel", "The channel.", true).setChannelTypes(ChannelType.NEWS, ChannelType.TEXT))
                                                .addOption(OptionType.STRING, "message", "Custom announcement message.", false),

                                        new SubcommandData("reddit", "command.description.redditNotifier")
                                                .addOption(OptionType.STRING, "name", "The subreddit name.", true)
                                                .addOptions(new OptionData(OptionType.CHANNEL, "channel", "The channel.", true).setChannelTypes(ChannelType.NEWS, ChannelType.TEXT))
                                                .addOption(OptionType.STRING, "message", "Custom announcement message.", false),

                                        new SubcommandData("rss", "command.description.rssNotifier")
                                                .addOption(OptionType.STRING, "url", "The RSS url.", true)
                                                .addOptions(new OptionData(OptionType.CHANNEL, "channel", "The channel.", true).setChannelTypes(ChannelType.NEWS, ChannelType.TEXT))),
                        new SubcommandGroupData("remove", "command.description.notifier.remove")
                                .addSubcommands(
                                        new SubcommandData("instagram", "command.description.instagramNotifier")
                                                .addOption(OptionType.STRING, "name", "The username of the Notifier.", true),
                                        new SubcommandData("youtube", "command.description.youtubeNotifier")
                                                .addOption(OptionType.STRING, "name", "The YouTube channel name or id of the Notifier.", true),
                                        new SubcommandData("twitch", "command.description.twitch")
                                                .addOption(OptionType.STRING, "name", "The Twitch channel name of the Notifier.", true),
                                        new SubcommandData("twitter", "command.description.twitterNotifier")
                                                .addOption(OptionType.STRING, "name", "The Twitter username of the Notifier.", true),
                                        new SubcommandData("tiktok", "command.description.tiktokNotifier")
                                                .addOption(OptionType.STRING, "name", "The TikTok username of the Notifier.", true),
                                        new SubcommandData("reddit", "command.description.redditNotifier")
                                                .addOption(OptionType.STRING, "name", "The subreddit name of the Notifier.", true),
                                        new SubcommandData("rss", "command.description.rssNotifier")
                                                .addOption(OptionType.STRING, "name", "The RSS url of the Notifier.", true)),
                        new SubcommandGroupData("list", "command.description.notifier.list")
                                .addSubcommands(
                                        new SubcommandData("instagram", "command.description.instagramNotifier"),
                                        new SubcommandData("youtube", "command.description.youtubeNotifier"),
                                        new SubcommandData("twitch", "command.description.twitch"),
                                        new SubcommandData("twitter", "command.description.twitterNotifier"),
                                        new SubcommandData("tiktok", "command.description.tiktokNotifier"),
                                        new SubcommandData("reddit", "command.description.redditNotifier"),
                                        new SubcommandData("rss", "command.description.rssNotifier")));
    }

    /**
     * @inheritDoc
     */
    @Override
    public String[] getAlias() {
        return new String[]{};
    }
}
