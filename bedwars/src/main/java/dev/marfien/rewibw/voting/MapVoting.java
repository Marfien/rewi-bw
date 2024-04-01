package dev.marfien.rewibw.voting;

import dev.marfien.rewibw.Message;
import dev.marfien.rewibw.shared.config.PluginConfig;
import dev.marfien.rewibw.shared.gui.GuiInventory;
import dev.marfien.rewibw.util.Items;
import dev.marfien.rewibw.world.MapPool;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class MapVoting {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final Map<Player, GameMapInfo> votes = new HashMap<>();
    private static final Map<GameMapInfo, Integer> voteCount = new HashMap<>();
    private static List<GameMapInfo> mapCollection;
    private static GameMapInfo[] votables;

    private static int[] votableSlots;
    private static final GuiInventory gui = new GuiInventory(1, "§8Mapvoting");

    @Getter
    private static GameMapInfo winner;

    @Getter
    private static boolean running = false;
    
    public static void init(PluginConfig.VoteConfig config) {
        votableSlots = config.getInventorySlots();
        mapCollection = new ArrayList<>(MapPool.getMaps());
        votables = new GameMapInfo[Math.min(votableSlots.length, mapCollection.size())];
        
        chooseVotables();
    }

    private static void chooseVotables() {
        Collections.shuffle(mapCollection);
        for (int i = 0; i < votables.length; i++) {
            votables[i] = mapCollection.get(i);
        }

        for (int i = 0; i < votables.length; i++) {
            GameMapInfo map = votables[i];
            int slot = votableSlots[i];
            voteCount.put(map, 0);
            gui.setItem(slot, new VotingGuiItem(slot, map));
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

    public static void start() {
        chooseVotables();
        running = true;
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.getInventory().setItem(0, Items.VOTE_ITEM);
        }
    }

    public static void vote(Player player, GameMapInfo map) {
        GameMapInfo previousVote = votes.put(player, map);
        voteCount.put(map, voteCount.get(map) + 1);
        if (previousVote != null) {
            voteCount.put(previousVote, voteCount.get(previousVote) - 1);
        }
    }

    public static void removeVote(Player player) {
        GameMapInfo previousVote = votes.remove(player);
        if (previousVote != null) {
            voteCount.put(previousVote, voteCount.get(previousVote) - 1);
        }
    }

    public static GameMapInfo getOrChooseWinner() {
        if (winner == null) {
            setWinner(chooseWinner());
        }

        return winner;
    }

    public static void setWinner(GameMapInfo map) {
        reset();
        winner = map;

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(Message.VOTING_BROADCAST.format(map.getDisplayName()));
            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
        }

    }

    public static GameMapInfo chooseWinner() {
        if (votes.isEmpty() || voteCount.isEmpty()) {
            int randomIndex = ThreadLocalRandom.current().nextInt(votables.length);
            return votables[randomIndex];
        }

        return Collections.max(voteCount.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
    }

    public static int getVotes(GameMapInfo map) {
        return voteCount.get(map);
    }

    public static boolean hasVoted(Player player) {
        return votes.containsKey(player);
    }
}
