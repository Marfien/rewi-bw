package dev.marfien.rewibw.voting;

import dev.marfien.rewibw.Message;
import dev.marfien.rewibw.shared.config.MapConfig;
import dev.marfien.rewibw.shared.config.PluginConfig;
import dev.marfien.rewibw.shared.gui.GuiInventory;
import dev.marfien.rewibw.util.Items;
import dev.marfien.rewibw.world.MapPool;
import dev.marfien.rewibw.world.MapWorld;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.ConfigurateException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class MapVoting {

    private static final Map<Player, String> votes = new HashMap<>();
    private static final Map<String, Integer> voteCount = new HashMap<>();
    private static List<String> mapCollection;
    private static String[] votables;

    private static int[] votableSlots;
    private static final GuiInventory gui = new GuiInventory(1, "§8Mapvoting");

    @Getter
    private static MapWorld winner;

    @Getter
    private static boolean running = false;
    
    public static void init(PluginConfig.VoteConfig config) {
        votableSlots = config.getInventorySlots();
        mapCollection = new ArrayList<>(MapPool.getMapNames());
        votables = new String[Math.min(votableSlots.length, mapCollection.size())];
    }

    private static void chooseVotables() throws IOException {
        Collections.shuffle(mapCollection);
        for (int i = 0; i < votables.length; i++) {
            votables[i] = mapCollection.get(i);
        }

        for (int i = 0; i < votables.length; i++) {
            String mapName = votables[i];
            int slot = votableSlots[i];
            voteCount.put(mapName, 0);

            Path sourcePath = MapPool.getSourcePath(mapName);
            MapConfig config = MapConfig.loader(sourcePath).load().get(MapConfig.class);

            if (config == null) throw new IOException("Map Config could not be loaded for map " + mapName);

            gui.setItem(slot, new VotingGuiItem(slot, mapName, config.getMap()));
        }
    }

    public static void openGui(Player player) {
        gui.openTo(player);
    }

    public static void reset() {
        gui.closeAll();
        votes.clear();
        winner = null;
        running = false;
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.getInventory().remove(Items.VOTE_ITEM);
        }
    }

    @SneakyThrows(IOException.class)
    public static void start() {
        chooseVotables();
        running = true;
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.getInventory().setItem(0, Items.VOTE_ITEM);
        }
    }

    public static void vote(Player player, String map) {
        String previousVote = votes.put(player, map);
        voteCount.put(map, voteCount.get(map) + 1);
        if (previousVote != null) {
            voteCount.put(previousVote, voteCount.get(previousVote) - 1);
        }
    }

    public static void removeVote(Player player) {
        String previousVote = votes.remove(player);
        if (previousVote != null) {
            voteCount.put(previousVote, voteCount.get(previousVote) - 1);
        }
    }

    public static MapWorld getOrChooseWinner() throws IOException {
        if (winner != null) return winner;

        String mapName = chooseWinner();
        winner = MapPool.requestMap(mapName);

        return winner;
    }

    public static void setWinner(MapWorld map) {
        reset();
        winner = map;

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(Message.VOTING_BROADCAST.format(map.getConfig().getMap().getDisplayName()));
            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
        }

    }

    public static String chooseWinner() {
        if (votes.isEmpty() || voteCount.isEmpty()) {
            int randomIndex = ThreadLocalRandom.current().nextInt(votables.length);
            return votables[randomIndex];
        }

        return Collections.max(voteCount.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
    }

    public static int getVotes(String map) {
        return voteCount.get(map);
    }

    public static boolean hasVoted(Player player) {
        return votes.containsKey(player);
    }
}
