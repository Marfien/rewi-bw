package dev.marfien.rewibw.game.lobby;

import dev.marfien.rewibw.Message;
import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.shared.ItemBuilder;
import dev.marfien.rewibw.shared.config.MapConfig;
import dev.marfien.rewibw.shared.config.PluginConfig;
import dev.marfien.rewibw.shared.gui.GuiInventory;
import dev.marfien.rewibw.shared.gui.GuiItem;
import dev.marfien.rewibw.util.Items;
import dev.marfien.rewibw.world.MapPool;
import dev.marfien.rewibw.world.MapWorld;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MapVoting {

    private static final Logger LOGGER = RewiBWPlugin.getPluginLogger();

    private final Map<Player, String> votes = new HashMap<>();
    private final Map<String, Integer> voteCount = new HashMap<>();
    private final List<String> mapCollection;
    private final String[] votables;

    private final int[] votableSlots;

    private GuiInventory gui = new GuiInventory(1, "§8Mapvoting");

    @Getter
    private MapWorld winner;

    @Getter
    private boolean running = false;
    
    public MapVoting(PluginConfig.VoteConfig config) {
        this.votableSlots = config.getInventorySlots();
        this.mapCollection = new ArrayList<>(MapPool.getMapNames());
        this.votables = new String[Math.min(this.votableSlots.length, this.mapCollection.size())];
    }

    private void chooseVotables() throws IOException {
        Collections.shuffle(this.mapCollection);
        for (int i = 0; i < this.votables.length; i++) {
            this.votables[i] = this.mapCollection.get(i);
        }

        for (int i = 0; i < this.votables.length; i++) {
            String mapName = this.votables[i];
            int slot = this.votableSlots[i];
            this.voteCount.put(mapName, 0);

            Path sourcePath = MapPool.getSourcePath(mapName);
            MapConfig config = MapConfig.loader(sourcePath).load().require(MapConfig.class);

            if (config == null) throw new IOException("Map Config could not be loaded for map " + mapName);

            this.gui.setItem(slot, new VotingGuiItem(slot, mapName, config.getMap()));
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
        LOGGER.info("Map Voting reset");
    }

    @SneakyThrows(IOException.class)
    public void start() {
        chooseVotables();
        this.running = true;
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.getInventory().setItem(0, Items.VOTE_ITEM);
        }

        LOGGER.info("Map Voting started");
    }

    public void vote(Player player, String map) {
        String previousVote = this.votes.put(player, map);
        this.voteCount.put(map, this.voteCount.get(map) + 1);
        if (previousVote != null) {
            this.voteCount.put(previousVote, this.voteCount.get(previousVote) - 1);
        }
        LOGGER.log(Level.FINE, "Player {} changed vote from {} to {}", new Object[]{player.getName(), previousVote, map});
    }

    public void removeVote(Player player) {
        String previousVote = this.votes.remove(player);
        if (previousVote != null) {
            this.voteCount.put(previousVote, this.voteCount.get(previousVote) - 1);
        }
        LOGGER.log(Level.FINE, "Player {} removed vote for {}", new Object[]{player.getName(), previousVote});
    }

    public MapWorld getOrChooseWinner() throws IOException {
        if (this.winner != null) return this.winner;

        String mapName = chooseWinner();
        setWinner(MapPool.requestMap(mapName));

        return this.winner;
    }

    public void setWinner(MapWorld map) {
        reset();
        this.winner = map;
        LOGGER.log(Level.INFO, "Winner set to {} ({})", new Object[]{map.getConfig().getMap().getDisplayName(), map.getName()});

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(Message.VOTING_BROADCAST.format(map.getConfig().getMap().getDisplayName()));
            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
        }

    }

    public String chooseWinner() {
        if (this.votes.isEmpty() || this.voteCount.isEmpty()) {
            int randomIndex = ThreadLocalRandom.current().nextInt(this.votables.length);
            return this.votables[randomIndex];
        }

        String winner = Collections.max(this.voteCount.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
        LOGGER.log(Level.INFO, "Winner chosen: {}", winner);
        return winner;
    }

    public int getVotes(String map) {
        return this.voteCount.get(map);
    }

    public boolean hasVoted(Player player) {
        return this.votes.containsKey(player);
    }

    public void destroyGui() {
        this.gui.destory();
        this.gui = null;
    }

    @RequiredArgsConstructor
    public class VotingGuiItem implements GuiItem {

        private final int slot;
        private final ItemStack displayItem;
        private final String displayName;
        private final String mapName;

        public VotingGuiItem(int slot, String name, MapConfig.MapInfoConfig mapInfo) {
            this.slot = slot;
            this.mapName = name;
            this.displayName = mapInfo.getDisplayName();
            ItemBuilder displayIconBuilder = ItemBuilder.of(mapInfo.getIcon())
                    .setDisplayName(ChatColor.GRAY + this.displayName);

            if (mapInfo.getBuilder() != null) {
                displayIconBuilder.setLore("§8von " + mapInfo.getBuilder());
            }

            this.displayItem = displayIconBuilder.asItemStack();
        }

        @Override
        public ItemStack getDisplayItemFor(Player player) {
            return ItemBuilder.of(this.displayItem.clone())
                    .addLoreLines("", "§f" + getVotes(this.mapName) + " Votes")
                    .asItemStack();
        }

        @Override
        public void onClick(GuiInventory inventory, InventoryClickEvent click) {
            Player player = (Player) click.getWhoClicked();
            vote(player, this.mapName);
            inventory.update(this.slot);

            player.playSound(player.getLocation(), Sound.ENDERDRAGON_HIT, 1.0F, 1.0F);
            if (hasVoted(player)) {
                player.sendMessage(RewiBWPlugin.PREFIX + Message.VOTE_CHANGED.format(this.displayName));
            } else {
                player.sendMessage(RewiBWPlugin.PREFIX + Message.VOTE_CAST.format(this.displayName));
            }
            player.closeInventory();
        }
    }


}
