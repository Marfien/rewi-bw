package dev.marfien.rewibw.command;

import dev.marfien.rewibw.shared.CustomCommand;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MusicCommand implements CustomCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cYou must be a player to use this command.");
            return;
        }

        Player player = (Player) sender;

        // 8 tackte pro sekunde
        new Thread(() -> {
            player.playNote(player.getLocation(), Instrument.PIANO, Note.sharp(1, Note.Tone.G));
            sleepTicks(10);
            player.playNote(player.getLocation(), Instrument.PIANO, Note.sharp(1, Note.Tone.G));
            sleepTicks(10);
            player.playNote(player.getLocation(), Instrument.PIANO, Note.sharp(1, Note.Tone.G));
            sleepTicks(2);
            player.playNote(player.getLocation(), Instrument.PIANO, Note.sharp(1, Note.Tone.E));
            sleepTicks(3);
            player.playNote(player.getLocation(), Instrument.PIANO, Note.sharp(0, Note.Tone.E));
            sleepTicks(5);
            player.playNote(player.getLocation(), Instrument.PIANO, Note.sharp(1, Note.Tone.F));
            sleepTicks(5);
            player.playNote(player.getLocation(), Instrument.PIANO, Note.sharp(1, Note.Tone.F));
            sleepTicks(5);
            player.playNote(player.getLocation(), Instrument.PIANO, Note.sharp(1, Note.Tone.G));
            sleepTicks(10);
            player.playNote(player.getLocation(), Instrument.PIANO, Note.sharp(1, Note.Tone.G));
            sleepTicks(10);
            player.playSound(player.getLocation(), Sound.FIREWORK_LAUNCH, 1, 1);
        }).start();
    }

    private static void sleepTicks(int ticks) {
        try {
            Thread.sleep(ticks * 50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
