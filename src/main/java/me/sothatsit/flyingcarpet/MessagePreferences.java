package me.sothatsit.flyingcarpet;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Stores, per player, whether they want to receive the messages that are
 * sent when their flying carpet is toggled on or off.
 *
 * Only players who have disabled the messages are stored, in players.yml.
 */
public class MessagePreferences {

    private static final String KEY = "toggle-messages-disabled";

    private final FlyingCarpet plugin;
    private final File file;
    private final Set<UUID> disabled = new HashSet<>();

    public MessagePreferences(FlyingCarpet plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "players.yml");
    }

    public void load() {
        disabled.clear();

        if (!file.exists())
            return;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        for (String entry : config.getStringList(KEY)) {
            try {
                disabled.add(UUID.fromString(entry));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid UUID \"" + entry + "\" in players.yml");
            }
        }
    }

    public void save() {
        YamlConfiguration config = new YamlConfiguration();
        List<String> entries = new ArrayList<>();

        for (UUID uuid : disabled) {
            entries.add(uuid.toString());
        }

        config.set(KEY, entries);

        try {
            if (!plugin.getDataFolder().exists())
                plugin.getDataFolder().mkdirs();

            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save " + file, e);
        }
    }

    public boolean isEnabled(UUID uuid) {
        return !disabled.contains(uuid);
    }

    public void setEnabled(UUID uuid, boolean enabled) {
        boolean changed = enabled ? disabled.remove(uuid) : disabled.add(uuid);

        if (changed)
            save();
    }

    /**
     * Toggles the preference for the given player.
     *
     * @return whether the messages are now enabled
     */
    public boolean toggle(UUID uuid) {
        boolean enabled = !isEnabled(uuid);
        setEnabled(uuid, enabled);
        return enabled;
    }
}
