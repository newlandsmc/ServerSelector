package com.semivanilla.serverselector.object;

import com.semivanilla.serverselector.ServerSelector;
import lombok.Getter;
import lombok.Setter;
import net.badbird5907.blib.menu.buttons.Button;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
    }

    public Button getButton() {
        return new Button() {
            @Override
            public ItemStack getItem(Player player) {
                ItemStack item = new ItemStack(material);
                ItemMeta meta = item.getItemMeta();
                meta.displayName(name);
                meta.lore(lore);
                item.setItemMeta(meta);
                return item;
            }

            @Override
            public int getSlot() {
                return slot;
            }

            @Override
            public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
                player.closeInventory();
                ServerSelector.send(player, server);
            }
        };
    }
}
