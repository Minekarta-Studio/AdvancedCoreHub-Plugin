package com.minekarta.advancedcorehub.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
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

import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.Set;

public class ItemBuilder {

    private ItemStack itemStack;
    private static HeadDatabaseAPI hdbApi;
    private static final Logger LOGGER = Bukkit.getLogger();

    public ItemBuilder(String materialString) {
        String lowerMaterialString = materialString.toLowerCase();

        if (lowerMaterialString.startsWith("headdatabase:") || lowerMaterialString.startsWith("hdb:")) {
            if (hdbApi == null && Bukkit.getPluginManager().isPluginEnabled("HeadDatabase")) {
                hdbApi = new HeadDatabaseAPI();
            }

            if (hdbApi == null) {
                LOGGER.warning("Tried to use a HeadDatabase item, but the HeadDatabase plugin is not enabled. Defaulting to PLAYER_HEAD.");
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

                // Create a Bukkit profile first to fetch the skin data
                org.bukkit.profile.PlayerProfile bukkitProfile = Bukkit.createPlayerProfile(null, playerName);

                // Then create a Paper profile and apply the properties
                PlayerProfile paperProfile = (PlayerProfile) bukkitProfile;
                skullMeta.setPlayerProfile(paperProfile);
                this.itemStack.setItemMeta(skullMeta);

            } catch (Exception e) {
                LOGGER.warning("Failed to parse player head name: " + materialString + ". Defaulting to PLAYER_HEAD.");
            }
        } else if (lowerMaterialString.startsWith("texture:")) {
            this.itemStack = new ItemStack(Material.PLAYER_HEAD);
            try {
                String base64Texture = materialString.substring(8);
                SkullMeta skullMeta = (SkullMeta) this.itemStack.getItemMeta();

                // Create a Paper profile directly and set the texture property
                PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
                Set<ProfileProperty> properties = profile.getProperties();
                properties.add(new ProfileProperty("textures", base64Texture));
                profile.setProperties(properties);

                skullMeta.setPlayerProfile(profile);
                this.itemStack.setItemMeta(skullMeta);
            } catch (Exception e) {
                LOGGER.warning("Failed to parse texture from Base64: " + materialString + ". Defaulting to PLAYER_HEAD.");
            }
        } else {
            try {
                Material material = Material.valueOf(materialString.toUpperCase());
                this.itemStack = new ItemStack(material);
            } catch (IllegalArgumentException e) {
                LOGGER.warning("Invalid material '" + materialString + "'. Defaulting to STONE.");
                this.itemStack = new ItemStack(Material.STONE);
            }
        }
    }

    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
    }

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack.clone();
    }

    public ItemBuilder setMaterial(Material material) {
        itemStack.setType(material);
        return this;
    }

    public ItemBuilder setDisplayName(Component name) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.displayName(name);
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder setLore(List<Component> lore) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.lore(lore);
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    public <T, Z> ItemBuilder addPdcValue(NamespacedKey key, PersistentDataType<T, Z> type, Z value) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(key, type, value);
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setUnbreakable(unbreakable);
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder setCustomModelData(int customModelData) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(customModelData);
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.addEnchant(enchantment, level, true);
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder addEnchantments(List<String> enchantments) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return this;

        for (String enchantmentString : enchantments) {
            try {
                String[] parts = enchantmentString.split(":");
                Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(parts[0].toLowerCase()));
                int level = parts.length > 1 ? Integer.parseInt(parts[1]) : 1;
                if (enchantment != null) {
                    meta.addEnchant(enchantment, level, true);
                } else {
                    LOGGER.warning("Invalid enchantment name: " + parts[0]);
                }
            } catch (Exception e) {
                LOGGER.warning("Failed to parse enchantment: " + enchantmentString);
            }
        }
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemStack build() {
        return itemStack;
    }
}
