package dev.marfien.rewibw.fakemobs;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MobEquipment {
    private final ItemStack[] items = new ItemStack[5];

    public ItemStack getItemInHand() {
        return this.items[0];
    }

    public ItemStack getBoots() {
        return this.items[1];
    }

    public ItemStack getLeggings() {
        return this.items[2];
    }

    public ItemStack getChestPlate() {
        return this.items[3];
    }

    public ItemStack getHelmet() {
        return this.items[4];
    }

    public void setItemInHand(ItemStack item) {
        this.setSlot(0, item);
    }

    public void setBoots(ItemStack item) {
        this.setSlot(1, item);
    }

    public void setLeggings(ItemStack item) {
        this.setSlot(2, item);
    }

    public void setChestplate(ItemStack item) {
        this.setSlot(3, item);
    }

    public void setHelmet(ItemStack item) {
        this.setSlot(4, item);
    }

    public ItemStack getSlot(int slot) {
        return slot >= 0 && slot < this.items.length ? this.items[slot] : null;
    }

    public void setSlot(int slot, ItemStack item) {
        if (item != null && item.getType() == Material.AIR) {
            item = null;
        }

        if (slot >= 0 && slot < this.items.length) {
            this.items[slot] = item;
        }
    }

    public List<Packet<?>> createPackets(int entityId) {
        List<Packet<?>> packetList = new ArrayList<>();

        for (int i = 0; i < 5; ++i) {
            ItemStack stack = this.getSlot(i);
            PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(entityId, i, CraftItemStack.asNMSCopy(stack));
            packetList.add(packet);
        }

        return packetList;
    }

    public boolean isEmpty() {

        for (int i = 0; i < this.items.length; ++i) {
            ItemStack item = this.items[i];
            if (item != null && item.getType() != Material.AIR) {
                return false;
            }
        }

        return true;
    }

    public MobEquipment clone() {
        MobEquipment inv = new MobEquipment();

        for (int i = 0; i < 5; ++i) {
            inv.setSlot(i, this.getSlot(i));
        }

        return inv;
    }
}
