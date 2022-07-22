package de.presti.ree6.utils.data;

import de.presti.ree6.utils.others.RandomUtils;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class used to store Data for a shorter period of time.
 */
@SuppressWarnings("Java8MapApi")
public class ArrayUtil {

    /**
     * Constructor should not be called, since it is a utility class that doesn't need an instance.
     *
     * @throws IllegalStateException it is a utility class.
     */
    private ArrayUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * HashMap used to store message contents and their IDs, to show the content when the message gets deleted.
     */
    public static final Map<String, Message> messageIDwithMessage = new HashMap<>();

    /**
     * HashMap used to store user Ids that are associated with a message, to show the content when the message gets deleted.
     */
    public static final Map<String, User> messageIDwithUser = new HashMap<>();

    /**
     * HashMap used to store user Ids and their VC join time, to track VoiceXP.
     */
    public static final Map<User, Long> voiceJoined = new HashMap<>();

    /**
     * HashMap used to store a users Ids, to keep them from spamming commands.
     */
    public static final ArrayList<String> commandCooldown = new ArrayList<>();

    /**
     * HashMap used to store a users Ids, to keep them from earning XP with every message.
     */
    public static final ArrayList<Member> timeout = new ArrayList<>();

    /**
     * String Array used to store answer options, for later use by the 8Ball command.
     */
    public static final String[] answers = new String[]{"It is certain.", "It is decidedly so.", "Without a doubt.", "Yes – definitely.", "You may rely on it.",
            "As I see it, yes.", "Most likely.", "Outlook good.", "Yes.", "Signs point to yes", "Reply hazy, try again.", "Ask again later.", "Better not tell you now.",
            "Cannot predict now.", "Concentrate and ask again.", "Don't count on it.", "My reply is no.", "My sources say no.", "Outlook not so good.", "Very doubtful."};

    /**
     * an Arraylist containing every temporal Voice-channel Id.
     */
    public static ArrayList<String> temporalVoicechannel = new ArrayList<>();

    /**
     * Get a String fully of random Number by the given length.
     *
     * @param length the wanted Length.
     * @return the {@link String} with the wanted Length.
     */
    public static String getRandomString(int length) {
        StringBuilder end = new StringBuilder();

        for (int i = 0; i < length; i++) {
            end.append(RandomUtils.random.nextInt(9));
        }

        return end.toString();
    }

    /**
     * Get the User from that send a specific Message that has been deleted.
     *
     * @param id the ID of the Message.
     * @return the {@link User} that send the Message.
     */
    public static User getUserFromMessageList(String id) {
        if (!messageIDwithUser.containsKey(id)) {
            return null;
        } else {
            return messageIDwithUser.get(id);
        }
    }

    /**
     * Get the Message content, of a deleted Message, by the ID.
     *
     * @param id the ID of the Message.
     * @return the {@link Message} Entity of the deleted Message.
     */
    public static Message getMessageFromMessageList(String id) {
        if (!messageIDwithMessage.containsKey(id)) {
            return null;
        } else {
            return messageIDwithMessage.get(id);
        }
    }

    /**
     * Get the Message content, of a deleted Message, by the ID.
     *
     * @param id the ID of the Message.
     * @return the {@link Message} Entity of the deleted Message.
     */
    public static Message getMessageFromMessageListAndRemove(String id) {
        if (!messageIDwithMessage.containsKey(id)) {
            return null;
        } else {
            return messageIDwithMessage.remove(id);
        }
    }

    /**
     * Check if the channel is a temporal Voice-channel.
     *
     * @param channel the Voice-channel to check.
     */
    public static boolean isTemporalVoicechannel(AudioChannel channel) {
        return temporalVoicechannel.contains(channel.getId());
    }
}