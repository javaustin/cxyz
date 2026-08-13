package com.carrotguy69.cxyz.utils;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class ItemUtils {

    public static ItemStack createColoredLeatherArmor(Material armorType, Color color) {
        ItemStack armor = new ItemStack(armorType);
        LeatherArmorMeta meta = (LeatherArmorMeta) Bukkit.getItemFactory().getItemMeta(armorType);
        assert meta != null;
        meta.setColor(color);
        armor.setItemMeta(meta);
        return armor;
    }

    public static void setItem(PlayerInventory inv, ItemStack itemStack, int slot) {
        if (slot < 0) {
                /*
                -1 = helmet
                -2 = chest
                -3 = leggings
                -4 = boots
                */

            switch (slot) {
                case -1:
                    inv.setItem(EquipmentSlot.HEAD, itemStack);
                    break;

                case -2:
                    inv.setItem(EquipmentSlot.CHEST, itemStack);
                    break;

                case -3:
                    inv.setItem(EquipmentSlot.LEGS,itemStack);
                    break;

                case -4:
                    inv.setItem(EquipmentSlot.FEET, itemStack);
                    break;
            }
        }
        else
            inv.setItem(slot, itemStack);
    }

}
