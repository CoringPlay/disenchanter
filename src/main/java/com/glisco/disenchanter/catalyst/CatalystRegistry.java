package com.glisco.disenchanter.catalyst;

import com.glisco.disenchanter.Disenchanter;
import com.glisco.disenchanter.compat.config.DisenchanterConfig;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public final class CatalystRegistry {

    private static final Map<Item, CatalystEntry> REGISTRY = new HashMap<>();

    public static void register(Item item, Catalyst catalyst, DisenchanterConfig.CatalystConfig config) {
        if (REGISTRY.containsKey(item)) throw new IllegalArgumentException("Attempted to register catalyst for item " + item + "twice");
        if (!config.enabled) return;

        REGISTRY.put(item, new CatalystEntry(catalyst, config.required_item_count));
    }

    public static void registerFromConfig(Item item, Catalyst catalyst) {
        register(item, catalyst, Disenchanter.getConfig().catalysts.get(Registry.ITEM.getId(item).toString()));
    }

    public static Catalyst get(ItemStack stack) {
        var entry = REGISTRY.get(stack.getItem());
        if (entry == null) return Catalyst.DEFAULT;
        if (stack.getCount() < entry.amount) return Catalyst.DEFAULT;
        return entry.catalyst;
    }

    public static int getRequiredItemCount(Catalyst catalyst) {
        final var candidate = REGISTRY.values().stream().filter(catalystEntry -> catalystEntry.catalyst == catalyst).findAny();
        return candidate.isEmpty() ? -1 : candidate.get().amount;
    }

    public static void forEach(BiConsumer<Item, CatalystEntry> action) {
        REGISTRY.forEach(action);
    }

    public static boolean isCatalyst(Item item) {
        return REGISTRY.containsKey(item);
    }

    public static List<Item> getCatalysts() {
        return new ArrayList<>(REGISTRY.keySet());
    }

    public static final record CatalystEntry(Catalyst catalyst, int amount) {}

}
