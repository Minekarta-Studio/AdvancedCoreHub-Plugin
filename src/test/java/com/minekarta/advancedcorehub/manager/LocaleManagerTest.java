package com.minekarta.advancedcorehub.manager;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;

import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public class LocaleManagerTest {

    @Mock private AdvancedCoreHub plugin;
    @Mock private FileManager fileManager;
    @Mock private FileConfiguration langConfig;
    @Mock private FileConfiguration mainConfig;
    @Mock private Player player;
    @Mock private Logger logger;
    @Mock private Server server;
    @Mock private PluginManager pluginManager;

    private LocaleManager localeManager;

    @BeforeEach
    void setUp() {
        // Manually instantiate the class under test
        localeManager = new LocaleManager(plugin, fileManager);

        // Mock the full chain of dependencies needed by localeManager.load()
        lenient().when(plugin.getConfig()).thenReturn(mainConfig);
        lenient().when(plugin.getServer()).thenReturn(server);
        lenient().when(server.getPluginManager()).thenReturn(pluginManager);
        lenient().when(plugin.getLogger()).thenReturn(logger);
        lenient().when(mainConfig.getString("language", "en")).thenReturn("en");

        // Mock player locale to prevent NPE in getRaw()
        lenient().when(player.getLocale()).thenReturn("en_US");

        // This call is now safe because the dependencies are mocked
        localeManager.load();

        // Mock file manager and language config for the actual test methods
        lenient().when(fileManager.getConfig(anyString())).thenReturn(langConfig);
    }

    @Test
    void testMessageFormatting() {
        // Arrange: Given a raw message with legacy placeholders and color codes
        String rawMessage = "&cHello, &l{0}&r. Welcome!";
        String expectedMessage = "Hello, Jules. Welcome!";
        when(langConfig.getString(eq("test-key"), anyString())).thenReturn(rawMessage);

        // Act: When we get the component
        Component resultComponent = localeManager.getComponent("test-key", player, "Jules");
        String plainResult = PlainComponentSerializer.plain().serialize(resultComponent);

        // Assert: The output should be correctly formatted, with colors stripped.
        assertEquals(expectedMessage, plainResult);
    }

    @Test
    void testMissingKey() {
        // Arrange: Given a key that does not exist, it should return the key as the message.
        String expectedMessage = "missing-key";
        when(langConfig.getString(eq("missing-key"), anyString())).thenReturn("missing-key");

        // Act: When we get the component
        Component resultComponent = localeManager.getComponent("missing-key", player);
        String plainResult = PlainComponentSerializer.plain().serialize(resultComponent);

        // Assert: The output should be the key itself.
        assertEquals(expectedMessage, plainResult);
    }
}
