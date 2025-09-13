package com.minekarta.advancedcorehub.util;

import me.arcaniax.hdb.api.HeadDatabaseAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.logging.Logger;

public class ItemBuilder {

    private ItemStack itemStack;
    private ItemMeta itemMeta;
    private static HeadDatabaseAPI hdbApi;
    private static final Logger LOGGER = Bukkit.getLogger();


    public ItemBuilder(String materialString) {
        if (hdbApi == null && Bukkit.getPluginManager().isPluginEnabled("HeadDatabase")) {
            hdbApi = new HeadDatabaseAPI();
        }

        String lowerMaterialString = materialString.toLowerCase();

        if (lowerMaterialString.startsWith("headdatabase:") || lowerMaterialString.startsWith("hdb:")) {
            if (hdbApi == null) {
                LOGGER.warning("HeadDatabase is not enabled, but an item tried to use it. Defaulting to PLAYER_HEAD.");
                this.itemStack = new ItemStack(Material.PLAYER_HEAD);
            } else {
                try {
                    String id = materialString.split(":")[1];
                    this.itemStack = hdbApi.getItemHead(id);
                    if (this.itemStack == null) {
                        LOGGER.warning("Invalid HeadDatabase ID: " + id + ". Defaulting to PLAYER_HEAD.");
                        this.itemStack = new ItemStack(Material.PLAYER_HEAD);
                    }
                } catch (Exception e) {
                    LOGGER.warning("Failed to parse HeadDatabase item: " + materialString + ". Defaulting to PLAYER_HEAD.");
                    this.itemStack = new ItemStack(Material.PLAYER_HEAD);
                }
            }
        } else if (lowerMaterialString.startsWith("head:")) {
            this.itemStack = new ItemStack(Material.PLAYER_HEAD);
            try {
                String playerName = materialString.split(":")[1];
                SkullMeta skullMeta = (SkullMeta) this.itemStack.getItemMeta();
                // Note: setOwningPlayer is deprecated and may perform a blocking lookup.
                // Using it as a fallback since the modern PlayerProfile API is showing issues with the provided dependency.
                skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(playerName));
                this.itemStack.setItemMeta(skullMeta);
            } catch (Exception e) {
                LOGGER.warning("Failed to parse player head name: " + materialString + ". Defaulting to PLAYER_HEAD.");
            }
        } else {
            // NOTE: The 'texture:<base64>' feature was temporarily removed due to compilation issues with the ProfileProperty API.
            try {
                Material material = Material.valueOf(materialString.toUpperCase());
                this.itemStack = new ItemStack(material);
            } catch (IllegalArgumentException e) {
                LOGGER.warning("Invalid material '" + materialString + "'. Defaulting to STONE.");
                this.itemStack = new ItemStack(Material.STONE);
            }
        }
        this.itemMeta = this.itemStack.getItemMeta();
    }

    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
        this.itemMeta = this.itemStack.getItemMeta();
    }

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack.clone();
        this.itemMeta = this.itemStack.getItemMeta();
    }

    public ItemBuilder setDisplayName(Component name) {
        itemMeta.displayName(name);
        return this;
    }

    public ItemBuilder setLore(List<Component> lore) {
        itemMeta.lore(lore);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    public <T, Z> ItemBuilder addPdcValue(NamespacedKey key, PersistentDataType<T, Z> type, Z value) {
        itemMeta.getPersistentDataContainer().set(key, type, value);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        itemMeta.setUnbreakable(unbreakable);
        return this;
    }

    public ItemBuilder setCustomModelData(int customModelData) {
        itemMeta.setCustomModelData(customModelData);
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        itemMeta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder addEnchantments(List<String> enchantments) {
        for (String enchantmentString : enchantments) {
            try {
                String[] parts = enchantmentString.split(":");
                Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(parts[0].toLowerCase()));
                int level = parts.length > 1 ? Integer.parseInt(parts[1]) : 1;
                if (enchantment != null) {
                    addEnchantment(enchantment, level);
                } else {
                    LOGGER.warning("Invalid enchantment name: " + parts[0]);
                }
            } catch (Exception e) {
                LOGGER.warning("Failed to parse enchantment: " + enchantmentString);
            }
        }
        return this;
    }

    public ItemStack build() {
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
