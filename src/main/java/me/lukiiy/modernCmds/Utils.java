package me.lukiiy.modernCmds;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class Utils {

    // Coordinates or Relative Coordinates
    public static @Nullable Double parseCoordinates(@NotNull String coord, @NotNull Location location, @NotNull COORD_AXIS axis) {
        if (!coord.startsWith("~")) return doubleOrNull(coord);

        double offset = coord.length() > 1 ? doubleOrNull(coord.substring(1)) : 0;
        return axis.get(location) + offset;
    }

    // Materials by name or ID
    public static @Nullable Material getMaterial(@NotNull String input) {
        Material stringMat = Material.getMaterial(input.toUpperCase());
        if (stringMat != null) return stringMat;

        try {return Material.getMaterial(Integer.parseInt(input));}
        catch (NumberFormatException ignored) {}

        return null;
    }


    // ItemStack id + data parse
    public static @Nullable ItemStack basicItem(@NotNull String item) {
        try {
            String[] parts = item.split(":", 2);
            Material material = getMaterial(parts[0]);
            byte data = parts.length > 1 ? Byte.parseByte(parts[1]) : 0;
            return new ItemStack(material, data);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public enum COORD_AXIS {
        X(Location::getX),
        Y(Location::getY),
        Z(Location::getZ);

        private final Function<Location, Double> getter;
        COORD_AXIS(Function<Location, Double> getter) {
            this.getter = getter;
        }

        public double get(Location location) {
            return getter.apply(location);
        }
    }

    private static @Nullable Double doubleOrNull(String i) {
        try {
            return Double.parseDouble(i);
        }
        catch (NumberFormatException ignored) {
            return null;
        }
    }
}
