package de.presti.ree6.commands.impl.economy;

import de.presti.ree6.commands.Category;
import de.presti.ree6.commands.CommandEvent;
import de.presti.ree6.commands.interfaces.Command;
import de.presti.ree6.commands.interfaces.ICommand;
import de.presti.ree6.sql.SQLSession;
import de.presti.ree6.utils.data.EconomyUtil;
import de.presti.ree6.utils.others.RandomUtils;
import de.presti.ree6.utils.others.ThreadUtil;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.time.Duration;
import java.util.ArrayList;

/**
 * Work for money.
 */
@Command(name = "work", description = "command.description.work", category = Category.ECONOMY)
public class Work implements ICommand {

    /**
     * List of every User that is on cooldown.
     */
    ArrayList<String> workTimeout = new ArrayList<>();

    /**
     * @inheritDoc
     */
    @Override
    public void onPerform(CommandEvent commandEvent) {
        String entryString = commandEvent.getGuild().getIdLong() + "-" + commandEvent.getMember().getIdLong();

        SQLSession.getSqlConnector().getSqlWorker().getSetting(commandEvent.getGuild().getIdLong(), "configuration_work_delay").subscribe(value -> {
            long delay = Long.parseLong(value.get().getStringValue());

            if (workTimeout.contains(entryString)) {
                commandEvent.reply(commandEvent.getResource("message.work.cooldown", delay));
                return;
            }

            double min = Double.parseDouble(SQLSession.getSqlConnector().getSqlWorker().getSetting(commandEvent.getGuild().getIdLong(),
                    "configuration_work_min").block().get().getStringValue());

            double max = Double.parseDouble(SQLSession.getSqlConnector().getSqlWorker().getSetting(commandEvent.getGuild().getIdLong(),
                            "configuration_work_max").block().get().getStringValue());

            double amount = RandomUtils.round(RandomUtils.nextDouble(min, max), 2);

            EconomyUtil.getMoneyHolder(commandEvent.getMember()).subscribe(moneyHolder -> {
                if (moneyHolder == null) return;

                if (EconomyUtil.pay(null, moneyHolder, amount, false, false, true)) {
                    commandEvent.reply(commandEvent.getResource("message.work.success", EconomyUtil.formatMoney(amount)));
                } else {
                    commandEvent.reply(commandEvent.getResource("message.work.fail"));
                }

                workTimeout.add(entryString);
                ThreadUtil.createThread(x -> workTimeout.remove(entryString), Duration.ofSeconds(delay), false, false);
            });
        });
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
