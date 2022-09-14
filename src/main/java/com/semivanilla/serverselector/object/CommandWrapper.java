package com.semivanilla.serverselector.object;

import com.semivanilla.serverselector.ServerSelector;
import net.badbird5907.blib.command.BukkitCommand;
import org.bukkit.Location;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandWrapper extends BukkitCommand {
    public CommandWrapper(String label, CommandExecutor executor, Plugin owner) {
        super(label, executor, owner);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws CommandException, IllegalArgumentException {
        return new ArrayList<>(ServerSelector.getInstance().getConfigs().stream().map(ServerConfig::getServer).collect(Collectors.toList()));
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args, @Nullable Location location) throws IllegalArgumentException {
        return tabComplete(sender, alias, args);
    }
}
