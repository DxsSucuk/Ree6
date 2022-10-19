package de.presti.ree6.commands.impl.fun;

import com.google.gson.JsonObject;
import de.presti.ree6.commands.Category;
import de.presti.ree6.commands.CommandEvent;
import de.presti.ree6.commands.interfaces.Command;
import de.presti.ree6.commands.interfaces.ICommand;
import de.presti.ree6.main.Main;
import de.presti.ree6.utils.data.Data;
import de.presti.ree6.utils.external.RequestUtility;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.awt.*;

/**
 * A command to show you are random WaiFu or Husbando.
 */
@Command(name = "waifu", description = "command.description.waifu", category = Category.FUN)
public class Waifu implements ICommand {

    /**
     * @inheritDoc
     */
    @Override
    public void onPerform(CommandEvent commandEvent) {
        JsonObject jsonObject = RequestUtility.requestJson(RequestUtility.Request.builder().url("https://api.dagpi.xyz/data/waifu").bearerAuth(Main.getInstance().getConfig().getConfiguration().getString("dagpi.apitoken")).build()).getAsJsonObject();

        EmbedBuilder em = new EmbedBuilder();

        if (!jsonObject.has("series")) {
            em.setTitle("Error!");
            em.setColor(Color.RED);
            em.setDescription(commandEvent.getResource("command.default.retrievalError"));
            em.setFooter(commandEvent.getMember().getUser().getAsTag() + " - " + Data.ADVERTISEMENT, commandEvent.getMember().getUser().getAvatarUrl());
            Main.getInstance().getCommandManager().sendMessage(em, commandEvent.getChannel(), commandEvent.getInteractionHook());
            return;
        }

        JsonObject jsonObject1 = jsonObject.get("series").getAsJsonObject();

        em.setImage((jsonObject.has("display_picture") ? jsonObject.get("display_picture").getAsString() : "https://images.ree6.de/notfound.png"));
        em.addField("**" + commandEvent.getResource("command.label.character") + "**", "``" + (jsonObject.has("name") ? jsonObject.get("name").getAsString() : commandEvent.getResource("command.default.retrievalError")) + "``", true);
        em.addField("**"  + commandEvent.getResource("command.label.from") + "**", "``" + (jsonObject1.has("name") ? jsonObject1.get("name").getAsString() : commandEvent.getResource("command.default.retrievalError")) + "``", true);
        if((jsonObject.has("nsfw") && jsonObject.get("nsfw").getAsBoolean())) {
            em.addField("**NSFW**", "", true);
        }
        em.setFooter(commandEvent.getMember().getUser().getAsTag() + " - " + Data.ADVERTISEMENT, commandEvent.getMember().getUser().getAvatarUrl());

        Main.getInstance().getCommandManager().sendMessage(em, commandEvent.getChannel(), commandEvent.getInteractionHook());
    }

    /**
     * @inheritDoc
     */
    @Override
    public CommandData getCommandData() {
        return null;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String[] getAlias() {
        return new String[0];
    }
}
