package dev.marfien.rewibw.shared.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

public class PrefixedLoggerFactory {

    public static Logger getLogger(String prefix) {
        return LogManager.getLogger(prefix, new PrefixedMessageFactory(prefix));
    }

    public static Logger getLogger(Object object, String prefix) {
        return LogManager.getLogger(object, new PrefixedMessageFactory(prefix));
    }

    public static Logger getLogger(Class<?> clazz, String prefix) {
        return LogManager.getLogger(clazz, new PrefixedMessageFactory(prefix));
    }

    public static Logger getLogger(Plugin plugin) {
        PluginDescriptionFile description = plugin.getDescription();
        return LogManager.getLogger(plugin, new PrefixedMessageFactory(description.getPrefix() != null ? description.getPrefix() : description.getName()));
    }

}
