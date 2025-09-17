package com.minekarta.advancedcorehub.cosmetics;

import java.util.List;

public record Gadget(
        String id,
        String material,
        String displayName,
        List<String> lore,
        String permission,
        int cooldown,
        List<String> actions
) {
}
