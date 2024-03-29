package com.semivanilla.serverselector.object;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.semivanilla.serverselector.ServerSelector;
import lombok.Getter;
import lombok.Setter;
import net.badbird5907.blib.menu.buttons.Button;
import net.badbird5907.blib.util.CC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class ServerConfig {
    private int slot;
    private Component name;
    private String server;
    private List<Component> lore;
    private Material material;
    private int playerCount = -1;
    private String permission;

    public ServerConfig(ConfigurationSection section) {
        slot = section.getInt("slot");
        material = Material.getMaterial(Objects.requireNonNull(section.getString("material")));
        name = MiniMessage.miniMessage().deserialize(Objects.requireNonNull(section.getString("name"))).decoration(TextDecoration.ITALIC, false);
        server = section.getString("proxy-server-name");
        lore = new ArrayList<>();
        section.getStringList("lore").forEach(
                line -> lore.add(MiniMessage.miniMessage().deserialize(line)
                        .decoration(TextDecoration.ITALIC, false)
                )
        );
        permission = section.getString("permission");
    }

    public void sendCountRequest() {
        if (Bukkit.getOnlinePlayers().size() <= 0) {
            return;
        }
        Player player = Bukkit.getOnlinePlayers().iterator().next();
        if (player == null) {
            return;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("PlayerCount");
        out.writeUTF(server);
        player.sendPluginMessage(ServerSelector.getInstance(), "BungeeCord", out.toByteArray());
        playerCount = -1;
    }

    public List<Component> getLore(Player player) {
        /*
        List<Component> list = new ArrayList<>(lore.stream().map(line -> line.replaceText(TextReplacementConfig.builder()
                        .match("%count%")
                        .replacement((playerCount < 0) ? "Pinging..." : playerCount + "").build()))
                .collect(Collectors.toList()));
         */
        List<Component> lore = new ArrayList<>();
        for (Component component : this.lore) {
            if (component instanceof TextComponent tc && permission != null && !permission.isEmpty()) {
                String contents = tc.content();
                if (contents.startsWith("hasperm:")) {
                    if (!player.hasPermission(permission)) {
                        continue;
                    }
                } else if (contents.startsWith("noperm:")) {
                    if (player.hasPermission(permission)) {
                        continue;
                    }
                }
                lore.add(replace(component));
            } else lore.add(replace(component));
        }
        return lore;
    }

    public Component replace(Component component) {
        return component.replaceText(TextReplacementConfig.builder()
                        .matchLiteral("%count%")
                        .replacement((playerCount < 0) ? "Pinging..." : playerCount + "")
                        .build())
                .replaceText(TextReplacementConfig.builder()
                        .matchLiteral("hasperm:")
                        .replacement("")
                        .build())
                .replaceText(TextReplacementConfig.builder()
                        .matchLiteral("noperm:")
                        .replacement("")
                        .build());
    }

    public Component getName() {
        return name.replaceText(TextReplacementConfig.builder()
                .matchLiteral("%count%")
                .replacement((playerCount < 0) ? ServerSelector.getInstance().getPinging() : Component.text(playerCount)).build());
    }

    public Button getButton() {
        return new Button() {
            @Override
            public ItemStack getItem(Player player) {
                ItemStack item = new ItemStack(material);
                ItemMeta meta = item.getItemMeta();
                meta.displayName(getName());
                List<Component> l = getLore(player);
                meta.lore(l);
                item.setItemMeta(meta);
                return item;
            }

            @Override
            public int getSlot() {
                return slot;
            }

            @Override
            public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
                if (permission != null && !permission.isEmpty() && !player.hasPermission(permission)) {
                    player.sendMessage(CC.RED + "You do not have permission to access this server.");
                    return;
                }
                player.closeInventory();
                ServerSelector.send(player, server);
            }
        };
    }
}
