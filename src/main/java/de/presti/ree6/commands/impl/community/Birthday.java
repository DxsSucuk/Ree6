package de.presti.ree6.commands.impl.community;

import de.presti.ree6.commands.Category;
import de.presti.ree6.commands.CommandEvent;
import de.presti.ree6.commands.interfaces.Command;
import de.presti.ree6.commands.interfaces.ICommand;
import de.presti.ree6.main.Main;
import de.presti.ree6.utils.localization.LocalizationService;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.apache.commons.validator.GenericValidator;

/**
 * This command is used to let the bot remember your Birthday.
 */
@Command(name = "birthday", description = "Let the bot remember your Birthday.", category = Category.COMMUNITY)
public class Birthday implements ICommand {

    /**
     * @inheritDoc
     */
    @Override
    public void onPerform(CommandEvent commandEvent) {
        if (commandEvent.isSlashCommand()) {
            Main.getInstance().getCommandManager().sendMessage(
                    LocalizationService.get(commandEvent).get("COMMAND_SLASH_COMMAND_NOT_SUPPORTED"), commandEvent.getChannel(),
                    commandEvent.getInteractionHook()
            );
            return;
        }

        if (commandEvent.getArguments().length == 1) {
            if (commandEvent.getArguments()[0].equalsIgnoreCase("remove")) {
                Main.getInstance().getSqlConnector().getSqlWorker().removeBirthday(commandEvent.getGuild().getId(), commandEvent.getMember().getId());
                Main.getInstance().getCommandManager().sendMessage("Your Birthday has been removed!", 5, commandEvent.getChannel(), commandEvent.getInteractionHook());

            } else {
                Main.getInstance().getCommandManager().sendMessage("Please use " + Main.getInstance().getSqlConnector().getSqlWorker().getSetting(commandEvent.getGuild().getId(), "chatprefix").getStringValue() + "birthday add/remove [Birthday(day.month.year)] [@User]", 5, commandEvent.getChannel(), commandEvent.getInteractionHook());
            }
        }
        if (commandEvent.getArguments().length == 2) {
            if (commandEvent.getArguments()[0].equalsIgnoreCase("remove")) {
                if (commandEvent.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                    if (commandEvent.getMessage() != null &&
                            commandEvent.getMessage().getMentions().getMembers().isEmpty()) {
                        Main.getInstance().getCommandManager().sendMessage("Please mention a user!", 5, commandEvent.getChannel(), commandEvent.getInteractionHook());
                    } else {
                        Main.getInstance().getSqlConnector().getSqlWorker().removeBirthday(commandEvent.getGuild().getId(), commandEvent.getMessage().getMentions().getMembers().get(0).getId());
                        Main.getInstance().getCommandManager().sendMessage("The Birthday of <@" + commandEvent.getMessage().getMentions().getMembers().get(0).getId() + "> has been removed!", 5, commandEvent.getChannel(), commandEvent.getInteractionHook());
                    }
                } else {
                    Main.getInstance().getCommandManager().sendMessage("You don't have the permission to remove a Birthday!", 5, commandEvent.getChannel(), commandEvent.getInteractionHook());
                }
            } else if (commandEvent.getArguments()[0].equalsIgnoreCase("add")) {
                if (GenericValidator.isDate(commandEvent.getArguments()[1], "dd.MM.yyyy", true)) {
                    Main.getInstance().getSqlConnector().getSqlWorker().addBirthday(commandEvent.getGuild().getId(), commandEvent.getChannel().getId(), commandEvent.getMember().getId(), commandEvent.getArguments()[1]);
                    Main.getInstance().getCommandManager().sendMessage("Your Birthday has been added!", 5, commandEvent.getChannel(), commandEvent.getInteractionHook());
                } else {
                    Main.getInstance().getCommandManager().sendMessage("Please use a valid Date!\nNote that we use the the format dd.MM.yyyy (day.month.year)!", 5, commandEvent.getChannel(), commandEvent.getInteractionHook());
                }
            } else {
                Main.getInstance().getCommandManager().sendMessage("Please use " + Main.getInstance().getSqlConnector().getSqlWorker().getSetting(commandEvent.getGuild().getId(), "chatprefix").getStringValue() + "birthday add/remove [Birthday(day.month.year)] [@User]", 5, commandEvent.getChannel(), commandEvent.getInteractionHook());
            }
        } else if (commandEvent.getArguments().length == 3) {
            if (commandEvent.getArguments()[0].equalsIgnoreCase("add")) {
                if (GenericValidator.isDate(commandEvent.getArguments()[1], "dd.MM.yyyy", true)) {
                if (commandEvent.getMessage() != null &&
                        commandEvent.getMessage().getMentions().getMembers().isEmpty()) {
                    Main.getInstance().getCommandManager().sendMessage("Please mention a user!", 5, commandEvent.getChannel(), commandEvent.getInteractionHook());
                } else {
                    Main.getInstance().getSqlConnector().getSqlWorker().addBirthday(commandEvent.getGuild().getId(), commandEvent.getChannel().getId(), commandEvent.getMessage().getMentions().getMembers().get(0).getId(), commandEvent.getArguments()[1]);
                    Main.getInstance().getCommandManager().sendMessage("The Birthday of <@" + commandEvent.getMessage().getMentions().getMembers().get(0).getId() + "> has been added!", 5, commandEvent.getChannel(), commandEvent.getInteractionHook());
                }
                } else {
                    Main.getInstance().getCommandManager().sendMessage("Please use a valid Date!\nNote that we use the the format dd.MM.yyyy (day.month.year)!", 5, commandEvent.getChannel(), commandEvent.getInteractionHook());
                }
            } else {
                Main.getInstance().getCommandManager().sendMessage("Please use " + Main.getInstance().getSqlConnector().getSqlWorker().getSetting(commandEvent.getGuild().getId(), "chatprefix").getStringValue() + "birthday add/remove [Birthday(day.month.year)] [@User]", 5, commandEvent.getChannel(), commandEvent.getInteractionHook());
            }
        } else {
            Main.getInstance().getCommandManager().sendMessage("Please use " + Main.getInstance().getSqlConnector().getSqlWorker().getSetting(commandEvent.getGuild().getId(), "chatprefix").getStringValue() + "birthday add/remove [Birthday(day.month.year)] [@User]", 5, commandEvent.getChannel(), commandEvent.getInteractionHook());
        }
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
        return new String[]{"bday"};
    }
}
