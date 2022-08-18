package net.osial.osialpets.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemBuilder {

    private ItemStack item;
    private ItemMeta itemMeta;
    private List<String> nLore = new ArrayList<>();
    private boolean glowing = false;

    public ItemBuilder(String s)
    {
        if (s.startsWith("head-")) {
            String base64skin = s.substring(5);
            item = new ItemStack(Material.PLAYER_HEAD, 1);
            //Set the player head item to a custom head texture with the base64
            //skin string as the texture.
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            try {
                Class<?> gameProfileClass = Class.forName("com.mojang.authlib.GameProfile");
                Class<?> propertyClass = Class.forName("com.mojang.authlib.properties.Property");
                Class<?> propertyMapClass = Class.forName("com.google.common.collect.ForwardingMultimap");
                Constructor<?> gameProfileConstructor = gameProfileClass.getConstructor(UUID.class, String.class);
                Constructor<?> propertyConstructor = propertyClass.getConstructor(String.class, String.class);
                Object gameProfile = gameProfileConstructor.newInstance(UUID.randomUUID(), "");
                //Get property map from gameProfile with getProperties() method.
                Object propertyMap = gameProfileClass.getMethod("getProperties").invoke(gameProfile);
                //Put property into propertyMap with put() method.
                propertyMapClass.getMethod("put", Object.class, Object.class)
                        .invoke(propertyMap, "textures", propertyConstructor.newInstance("textures", base64skin));
                //Set profile field of meta to gameProfile.
                Field profileField = meta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(meta, gameProfile);
            } catch (Exception e) {
                e.printStackTrace();
            }
            item.setItemMeta(meta);
            itemMeta = item.getItemMeta();
        } else {
            Material material = Material.valueOf(s);
            item = new ItemStack(material, 1);
            itemMeta = item.getItemMeta();
        }
    }

    public ItemBuilder(Material material) {
        this.item = new ItemStack(material, 1);
        this.itemMeta = item.getItemMeta();
    }

    public ItemBuilder setAmount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemBuilder setName(String name) {
        itemMeta.setDisplayName(ColorUtil.color(name));
        item.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder setGlowing(boolean glowing) {
        this.glowing = glowing;
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        List<String> newLore = new ArrayList<>();
        for (String line : lore) {
            newLore.add(ColorUtil.color(line));
        }
        nLore = newLore;
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        List<String> newLore = new ArrayList<>();
        for (String line : lore) {
            ColorUtil.color(line);
        }
        nLore = newLore;
        return this;
    }

    public ItemStack build() {
        if (glowing) {
            itemMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
            item.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.DURABILITY, 1);
        }
        itemMeta.setLore(nLore);
        item.setItemMeta(itemMeta);
        return item;
    }

}
