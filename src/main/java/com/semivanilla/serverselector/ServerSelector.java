package com.semivanilla.serverselector;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.semivanilla.serverselector.manager.CommandManager;
import com.semivanilla.serverselector.object.ServerConfig;
import lombok.Getter;
import net.badbird5907.blib.bLib;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ServerSelector extends JavaPlugin {
    @Getter
    private static ServerSelector instance;
    @Getter
    private List<ServerConfig> configs = new ArrayList<>();

    @Getter
    private boolean fill = true;

    @Getter
    private Material fillMaterial = Material.BLACK_STAINED_GLASS;

    @Getter
    private String menuName = "Server Selector";

    @Getter
    private int menuSize = 27;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        bLib.create(this);
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        saveDefaultConfig();
        loadConfigValues();
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    @Override
    public void onDisable() {
    }

    public void loadConfigValues() {
        configs = new ArrayList<>();
        ConfigurationSection section = getConfig().getConfigurationSection("menu.servers");
        Objects.requireNonNull(section).getValues(false).forEach(
                (key, value) -> configs.add(new ServerConfig(Objects.requireNonNull(section.getConfigurationSection(key))))
        );

        for (ServerConfig config : configs) {
            System.out.println(config.getMaterial() + " | " + config.getServer());
        }

        for (String aliases : getConfig().getStringList("aliases")) {
            CommandManager.getInstance().registerCommand(aliases);
        }

        fill = getConfig().getBoolean("menu.fill.enabled");
        fillMaterial = Material.getMaterial(Objects.requireNonNull(getConfig().getString("menu.fill.material")));
        menuName = getConfig().getString("menu.name");
        menuSize = getConfig().getInt("menu.size", 27);
    }

    public static void send(Player player, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);

        player.sendPluginMessage(ServerSelector.getInstance(), "BungeeCord", out.toByteArray());
    }
}
