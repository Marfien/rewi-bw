package dev.marfien.rewibw.game.lobby;

import com.mojang.authlib.GameProfile;
import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.fakeentities.FakeHologram;
import dev.marfien.rewibw.fakeentities.FakePlayer;
import dev.marfien.rewibw.fakeentities.MobEquipment;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.WorldSettings;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CpsTester extends FakePlayer {

    private final Map<Player, short[]> cpsSamples = new HashMap<>();

    private final FakeHologram hologram;

    public CpsTester(Location location) {
        super(new MobEquipment(), location, true, new GameProfile(UUID.randomUUID(), "§3Schlag mich!"));
        this.hologram = new FakeHologram(location.add(0, 2.2, 0), player -> {
            short[] cps = this.cpsSamples.get(player);
            return "§6CPS: §a" + (cps == null ? "0 §7& §a0" : cps[0] + " §7& §a" + cps[1]);
        }, "§6CPS: §a0 §7& §a0");
        RewiBWPlugin.getScheduler().runTaskTimer(() -> {
            this.hologram.updateMetadata();
            this.cpsSamples.clear();
        }, 0, 20);

        super.addAttackListener(player -> {
            cpsSamples.computeIfAbsent(player, ignored -> new short[2])[0] += 1;
            player.playSound(this.getLocation(), Sound.HURT_FLESH, 1, 1);
            super.playDamageAnimation(player);
        });
        super.addInteractListener(player -> cpsSamples.computeIfAbsent(player, ignored -> new short[2])[1] += 1);
    }

    @Override
    protected PacketPlayOutPlayerInfo.PlayerInfoData createPlayerInfoPacketData(Player player, PacketPlayOutPlayerInfo packet) {
        GameProfile playerProfile = ((CraftPlayer) player).getHandle().getProfile();
        GameProfile profile = new GameProfile(super.getProfile().getId(), super.getProfile().getName());
        playerProfile.getProperties().get("textures").forEach(property -> profile.getProperties().put("textures", property));
        return packet.new PlayerInfoData(profile, 1, WorldSettings.EnumGamemode.NOT_SET,
                CraftChatMessage.fromString(profile.getName())[0]);
    }

    @Override
    public void smoothUpdateFor(Player player) {
        super.smoothUpdateFor(player);
        this.hologram.smoothUpdateFor(player);
    }

    @Override
    public void loadFor(Player player) {
        super.loadFor(player);
        this.hologram.loadFor(player);
    }

    @Override
    public void unloadFor(Player player) {
        super.unloadFor(player);
        this.hologram.unloadFor(player);
    }

    @Override
    public void teleport(Location location) {
        super.teleport(location);
        this.hologram.teleport(location.clone().add(0, 2.2, 0));
    }
}
