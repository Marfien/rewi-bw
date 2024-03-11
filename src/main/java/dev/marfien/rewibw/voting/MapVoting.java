package dev.marfien.rewibw.voting;

import dev.marfien.rewibw.gui.GuiInventory;
import dev.marfien.rewibw.util.Items;
import dev.marfien.rewibw.world.GameMap;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class MapVoting {

    private final Map<Player, GameMap> votes = new HashMap<>();
    private final Map<GameMap, Integer> voteCount = new HashMap<>();
    private final List<GameMap> mapCollection;
    private final GameMap[] votables;

    private final int[] votableSlots;
    private final GuiInventory gui = new GuiInventory(1, "§8Mapvoting");

    @Getter
    private GameMap winner;

    @Getter
    private boolean running = false;

    public MapVoting(List<GameMap> mapCollection, int[] votableSlots) {
        this.mapCollection = new ArrayList<>(mapCollection);
        this.votableSlots = votableSlots;
        this.votables = new GameMap[Math.min(votableSlots.length, mapCollection.size())];

        this.chooseVotables();
    }

    private void chooseVotables() {
        Collections.shuffle(this.mapCollection);
        for (int i = 0; i < this.votables.length; i++) {
            this.votables[i] = this.mapCollection.get(i);
        }

        for (int i = 0; i < this.votables.length; i++) {
            GameMap map = this.votables[i];
            int slot = this.votableSlots[i];
            this.voteCount.put(map, 0);
            this.gui.setItem(slot, new VotingGuiItem(slot, map));
        }
    }

    public void openGui(Player player) {
        this.gui.openTo(player);
    }

    public void reset() {
        this.gui.closeAll();
        this.votes.clear();
        this.winner = null;
        this.running = false;
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.getInventory().remove(Items.VOTE_ITEM);
        }
    }

    public void start() {
        this.chooseVotables();
        this.running = true;
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.getInventory().setItem(0, Items.VOTE_ITEM);
        }
    }

    public void vote(Player player, GameMap map) {
        GameMap previousVote = this.votes.put(player, map);
        this.voteCount.put(map, this.voteCount.get(map) + 1);
        if (previousVote != null) {
            this.voteCount.put(previousVote, this.voteCount.get(previousVote) - 1);
        }
    }

    public void removeVote(Player player) {
        GameMap previousVote = this.votes.remove(player);
        if (previousVote != null) {
            this.voteCount.put(previousVote, this.voteCount.get(previousVote) - 1);
        }
    }

    public GameMap getOrChooseWinner() {
        if (this.winner == null) {
            this.setWinner(this.chooseWinner());
        }

        return this.winner;
    }

    public void setWinner(GameMap map) {
        this.reset();
        this.winner = map;

        String message = "\n" +
                "§f§m-----------§r §3Voting beendet §f§m-----------§r\n" +
                " \n" +
                "§7Map: §f" + map.getDisplayName() + "\n" +
                "§7Gebaut von: §f" + Optional.ofNullable(map.getBuilder()).orElse("RewisServerTeam") + "\n ";

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(message);
            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
        }

    }

    public GameMap chooseWinner() {
        if (this.votes.isEmpty() || this.voteCount.isEmpty()) {
            int randomIndex = ThreadLocalRandom.current().nextInt(this.votables.length);
            return this.votables[randomIndex];
        }

        return Collections.max(this.voteCount.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
    }

    public int getVotes(GameMap map) {
        return this.voteCount.get(map);
    }

    public boolean hasVoted(Player player) {
        return this.votes.containsKey(player);
    }
}
