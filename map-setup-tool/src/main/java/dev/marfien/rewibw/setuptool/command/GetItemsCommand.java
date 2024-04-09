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

    static ItemStack[] INVENTORY_CONTENTS = new ItemStack[36];
    static {
        INVENTORY_CONTENTS[0] = BronzeSpawnerAdder.ITEM;
        INVENTORY_CONTENTS[1] = SilverSpawnerAdder.ITEM;
        INVENTORY_CONTENTS[2] = GoldSpawnerAdder.ITEM;
        INVENTORY_CONTENTS[4] = ShopAdder.ITEM;
        INVENTORY_CONTENTS[5] = SpecSpawnSetter.ITEM;
        INVENTORY_CONTENTS[7] = TeamAdder.ITEM;
        INVENTORY_CONTENTS[8] = LocationRemover.ITEM;
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

        player.getInventory().setContents(INVENTORY_CONTENTS);
        return;
    }
}
