package de.presti.ree6.module.game.core.base;

import de.presti.ree6.module.game.core.GameSession;
import de.presti.ree6.utils.data.EconomyUtil;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;

/**
 * Interface for Games to implement.
 */
public interface IGame {

    /**
     * Called when the Game is created.
     */
    void createGame();

    /**
     * Called when the Game is started.
     */
    void startGame();

    /**
     * Called when a User wants to join the Game.
     *
     * @param user The User who wants to join.
     */
    void joinGame(GamePlayer user);

    /**
     * Called when a User wants to leave the Game.
     *
     * @param user The User who wants to leave.
     */
    void leaveGame(GamePlayer user);

    /**
     * Called when a Message is received.
     *
     * @param messageReactionEvent The Event.
     */
    default void onReactionReceive(GenericMessageReactionEvent messageReactionEvent) {
        throw new UnsupportedOperationException("This Game does not support Reaction Events!");
    }

    /**
     * Called when a Reaction is received.
     *
     * @param messageReceivedEvent The Event.
     */
    default void onMessageReceive(MessageReceivedEvent messageReceivedEvent) {
        throw new UnsupportedOperationException("This Game does not support Messages!");
    }

    /**
     * Called when a Button is clicked.
     *
     * @param buttonInteractionEvent The Event.
     */
    default void onButtonInteractionReceive(ButtonInteractionEvent buttonInteractionEvent) {
        throw new UnsupportedOperationException("This Game does not support Buttons!");
    }

    /**
     * Called when a User should be rewarded.
     *
     * @param gameSession The current Session.
     * @param player      The Player who should be rewarded.
     * @param parameter   Any additional Parameter.
     */
    default void rewardPlayer(GameSession gameSession, GamePlayer player, Object parameter) {
        if (parameter instanceof String parameterString) {
            try {
                parameter = Double.parseDouble(parameterString.replace(",", "."));
            } catch (Exception ignore) {
                // Parsing failed.
            }
        }
        if (parameter instanceof Double money) {
            EconomyUtil.getMoneyHolder(gameSession.getGuild().getIdLong(), player.getRelatedUserId(), true).subscribe(moneyHolder -> {
                if (moneyHolder == null) return;
                EconomyUtil.pay(null, moneyHolder, money, false, false, true);
            });
        }
    }

    /**
     * Called when the Game is stopped.
     */
    void stopGame();
}
