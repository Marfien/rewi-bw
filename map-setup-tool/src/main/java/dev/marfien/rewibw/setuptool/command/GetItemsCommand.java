package dev.marfien.rewibw.setuptool.command;

import dev.marfien.rewibw.setuptool.SetupSession;
import dev.marfien.rewibw.setuptool.SetupSessionManager;
import dev.marfien.rewibw.setuptool.item.*;
import dev.marfien.rewibw.shared.CustomCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class GetItemsCommand implements CustomCommand {

    static ItemStack[] contents = new ItemStack[36];
    static {
        contents[0] = BronzeSpawnerAdder.ITEM;
        contents[1] = SilverSpawnerAdder.ITEM;
        contents[2] = GoldSpawnerAdder.ITEM;
        contents[4] = ShopAdder.ITEM;
        contents[5] = SpecSpawnSetter.ITEM;
        contents[7] = TeamAdder.ITEM;
        contents[8] = LocationRemover.ITEM;
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("§cOnly players can use this command.");
            return;
        }

        Player player = (Player) commandSender;
        Optional<SetupSession> session = SetupSessionManager.getSession(player.getUniqueId());

        if (!session.isPresent()) {
            player.sendMessage("§cYou are not in a setup session.");
            return;
        }

        player.getInventory().setContents(contents);
    }
}
