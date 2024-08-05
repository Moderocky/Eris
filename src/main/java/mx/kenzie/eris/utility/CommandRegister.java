package mx.kenzie.eris.utility;

import mx.kenzie.eris.Bot;
import mx.kenzie.eris.DiscordAPI;
import mx.kenzie.eris.api.command.CommandHandler;
import mx.kenzie.eris.api.entity.command.Command;
import mx.kenzie.eris.api.utility.LazyList;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CommandRegister {

    protected final Bot bot;
    protected final DiscordAPI api;
    protected final Map<Command, CommandHandler> commands;

    public CommandRegister(Bot bot, DiscordAPI api) {
        this.bot = bot;
        this.api = api;
        this.commands = new HashMap<>();
    }

    public void add(Command command, CommandHandler handler) {
        this.commands.put(command, handler);
    }

    public void add(Command command) {
        this.commands.put(command, null);
    }

    public LazyList<Command> register() {
        return this.register(null);
    }

    public <IGuild> LazyList<Command> register(IGuild guild) {
        final String id = (guild == null) ? null : api.getGuildId(guild);
        for (Command command : commands.keySet()) command.guild_id = id;
        final LazyList<Command> list = this.api.overwriteCommands(guild, commands.keySet().toArray(new Command[0]));
        this.commands.values().removeIf(Objects::isNull);
        this.bot.commands().putAll(commands);
        return list;
    }

}
