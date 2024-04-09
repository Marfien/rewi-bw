package dev.marfien.rewibw.shared;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public interface CustomCommand extends CommandExecutor {

    @Override
    default boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        this.execute(sender, args);
        return true;
    }

    void execute(CommandSender sender, String[] args);

}
