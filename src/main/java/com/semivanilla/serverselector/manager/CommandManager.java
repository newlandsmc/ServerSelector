package com.semivanilla.serverselector.manager;

import com.semivanilla.serverselector.ServerSelector;
import com.semivanilla.serverselector.menu.ServersMenu;
import com.semivanilla.serverselector.object.ServerConfig;
import lombok.Getter;
import lombok.SneakyThrows;
import net.badbird5907.blib.command.BukkitCommand;
import net.badbird5907.blib.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class CommandManager implements CommandExecutor {
    @Getter
    private static final CommandManager instance = new CommandManager();
    private CommandMap map;

    public CommandManager() {
        if (Bukkit.getServer().getPluginManager() instanceof SimplePluginManager) {
            SimplePluginManager manager = (SimplePluginManager) Bukkit.getServer().getPluginManager();
            try {
                Field field = SimplePluginManager.class.getDeclaredField("commandMap");
                field.setAccessible(true);
                map = (CommandMap) field.get(manager);
            } catch (IllegalArgumentException | NoSuchFieldException | IllegalAccessException | SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    @SneakyThrows
    public void registerCommand(String command) {
        Constructor<BukkitCommand> cons = BukkitCommand.class.getDeclaredConstructor(String.class, CommandExecutor.class, Plugin.class);
        cons.setAccessible(true);
        org.bukkit.command.Command cmd = cons.newInstance(command, this, ServerSelector.getInstance());
        map.register(ServerSelector.getInstance().getName(), cmd);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("serverselector.reload")) {
                    ServerSelector.getInstance().reloadConfig();
                    ServerSelector.getInstance().loadConfigValues();
                    sender.sendMessage(CC.GREEN + "Config reloaded!");
                    return true;
                } else {
                    String server = args[0];
                    ServerConfig serverConfig = ServerSelector.getInstance().getConfigs().stream().filter(config -> config.getServer().equalsIgnoreCase(server)).findFirst().orElse(null);
                    if (serverConfig != null) {
                        ServerSelector.send(player, serverConfig.getServer());
                        return true;
                    }
                }
            }
            new ServersMenu().open(player);
        }
        return true;
    }
}
