package com.semivanilla.serverselector.menu;

import com.semivanilla.serverselector.ServerSelector;
import com.semivanilla.serverselector.object.ServerConfig;
import net.badbird5907.blib.menu.buttons.Button;
import net.badbird5907.blib.menu.buttons.PlaceholderButton;
import net.badbird5907.blib.menu.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ServersMenu extends Menu {
    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        List<Integer> slots = new ArrayList<>();
        for (ServerConfig config : ServerSelector.getInstance().getConfigs()) {
            slots.add(config.getSlot());
            buttons.add(config.getButton());
        }
        if (ServerSelector.getInstance().isFill()) {
            List<Integer> fillSlots = new ArrayList<>();
            for (int i = 0; i < ServerSelector.getInstance().getMenuSize(); i++) {
                if (!slots.contains(i)) {
                    fillSlots.add(i);
                }
            }
            int[] slotsArray = new int[fillSlots.size()];
            for (int i = 0; i < fillSlots.size(); i++) {
                slotsArray[i] = fillSlots.get(i);
            }
            buttons.add(new PlaceholderButton() {
                @Override
                public ItemStack getItem(Player player) {
                    ItemStack stack = super.getItem(player);
                    stack.setType(ServerSelector.getInstance().getFillMaterial());
                    return stack;
                }

                @Override
                public int[] getSlots() {
                    return slotsArray;
                }
            });
        }
        return buttons;
    }

    @Override
    public int getInventorySize(List<Button> buttons) {
        return ServerSelector.getInstance().getMenuSize();
    }

    @Override
    public String getName(Player player) {
        return ServerSelector.getInstance().getMenuName();
    }
}
