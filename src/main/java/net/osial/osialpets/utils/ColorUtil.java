package net.osial.osialpets.utils;

import net.md_5.bungee.api.ChatColor;

public class ColorUtil {

    public static String color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

}
