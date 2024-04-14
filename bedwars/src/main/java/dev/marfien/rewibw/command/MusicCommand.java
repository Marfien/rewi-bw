package dev.marfien.rewibw.command;

import dev.marfien.rewibw.shared.CustomCommand;
import org.bukkit.Instrument;
import org.bukkit.Note;
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
        int octave = Integer.parseInt(args[0]);
        Note.Tone tone = Note.Tone.valueOf(args[1].toUpperCase());
        boolean sharp = Boolean.parseBoolean(args[2]);
        player.playNote(player.getLocation(), Instrument.PIANO, new Note(octave, tone, sharp));
    }

}
