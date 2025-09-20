package com.minekarta.advancedcorehub.manager;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.minekarta.advancedcorehub.AdvancedCoreHub;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerVisibilityManagerTest {

    private ServerMock server;
    private AdvancedCoreHub plugin;
    private PlayerVisibilityManager playerVisibilityManager;
    private PlayerMock player;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(AdvancedCoreHub.class);
        playerVisibilityManager = plugin.getPlayerVisibilityManager();
        player = server.addPlayer();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testToggleVisibility() {
        boolean initialVisibility = playerVisibilityManager.isVanished(player);
        playerVisibilityManager.toggleVisibility(player);
        assertNotEquals(initialVisibility, playerVisibilityManager.isVanished(player));
        playerVisibilityManager.toggleVisibility(player);
        assertEquals(initialVisibility, playerVisibilityManager.isVanished(player));
    }

    @Test
    public void testSetVanished() {
        playerVisibilityManager.setVanished(player, true);
        assertTrue(playerVisibilityManager.isVanished(player));
        playerVisibilityManager.setVanished(player, false);
        assertFalse(playerVisibilityManager.isVanished(player));
    }

    @Test
    public void testIsVanished() {
        assertFalse(playerVisibilityManager.isVanished(player));
        playerVisibilityManager.setVanished(player, true);
        assertTrue(playerVisibilityManager.isVanished(player));
    }

    @Test
    public void testHandlePlayerJoin() {
        playerVisibilityManager.handlePlayerJoin(player);
        assertEquals(plugin.getConfig().getBoolean("player-visibility.default_state"), !playerVisibilityManager.isVanished(player));
        assertNotNull(player.getInventory().getItem(plugin.getConfig().getInt("player-visibility.item_slot")));
    }

    @Test
    public void testHandlePlayerQuit() {
        playerVisibilityManager.setVanished(player, true);
        assertTrue(playerVisibilityManager.isVanished(player));
        playerVisibilityManager.handlePlayerQuit(player);
        assertFalse(playerVisibilityManager.isVanished(player));
    }

    @Test
    public void testGiveVisibilityItem() {
        playerVisibilityManager.giveVisibilityItem(player);
        assertNotNull(player.getInventory().getItem(plugin.getConfig().getInt("player-visibility.item_slot")));
    }

    @Test
    public void testUpdateVisibilityItem() {
        playerVisibilityManager.setVanished(player, true);
        playerVisibilityManager.updateVisibilityItem(player);
        assertEquals(playerVisibilityManager.getHiddenItem(player), player.getInventory().getItem(plugin.getConfig().getInt("player-visibility.item_slot")));

        playerVisibilityManager.setVanished(player, false);
        playerVisibilityManager.updateVisibilityItem(player);
        assertEquals(playerVisibilityManager.getVisibleItem(player), player.getInventory().getItem(plugin.getConfig().getInt("player-visibility.item_slot")));
    }
}
