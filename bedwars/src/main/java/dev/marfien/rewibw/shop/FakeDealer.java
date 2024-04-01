package dev.marfien.rewibw.shop;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import dev.marfien.rewibw.PlayerManager;
import dev.marfien.rewibw.fakeentities.FakePlayer;
import dev.marfien.rewibw.fakeentities.MobEquipment;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class FakeDealer extends FakePlayer {

    private static final GameProfile PROFILE = new GameProfile(null, "§6Dealer");

    static {
        PROFILE.getProperties().put("textures", new Property(
                "textures",
                "ewogICJ0aW1lc3RhbXAiIDogMTcwOTI4ODE5Mzc1NiwKICAicHJvZmlsZUlkIiA6ICJmNjgwMmEzZTkyNjk0YzI1ODk5YzE2MjMxZTlkYjMyYSIsCiAgInByb2ZpbGVOYW1lIiA6ICJzY2hvZW5zdGVuIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzZlYzc2YzVlMDllYzc3NzY0YzFmNTcxZjk5NmIxYzE4NzU1ZjgyYmQwMDIzM2MxYTUwNGFhODk2MDIwMmNmNTkiCiAgICB9CiAgfQp9",
                "Dy6YAyn5flLEta6kWc7UPevtc2cFvCu7QWS3T0uiIuC3KDTM7N7AGMos7W9OZYbYj405pnF2fmJX8ipT/P2bxe/HsuUcheUor7U/xDJzfTvt5zVSvdUNtzZ+crTGnUQFH7MkBPuxfe8NbvpAz0loTiMpPChVtSjKOnggcIv8uycKuNFcKPzWNK8PoUFMIqwwTnGkLbiXicMxHv6/PIr5MEVrKyX2T1xbWiFF9x7wqmVFnYwnfM7DXuJP8itMnJcCOIT9VSrRN1o1fMscvG8F8ZNKxWFIz4aCFkG3soawN0DI6ExP+C4kzgv7WjEvd3q3AylydK1XeGnsY76UwnLiwHMwgakKhodPQ1hQ6v3lE6Ex79KRvdOzVFc3SOQavBTEJ8Yf9SR0VMC9IaqeSzKkM6r93A4uAqaZicoU/4ZnxCIjgAmqkZjpW5idTDyqyWfLZbgAOrxaZJ0qqA5070RW7dATez+nd6sBpF1acSG7eiSdoUq3flOzkqRExKsRY3umioF5/D/whHnai7pNzYV6WmbOfpcnQDhNXlyhWeGR9id6lu4ZsCd3IB76Oh+uH13K66oYNQ73QTXPul0hvSlsLACKapGiQqKi8js42HYPWOecc3n/ooj9o6hnHuNFZ4gf9GXzBx7lWOLhdWobxb/BMmjGObHKL4iT4OqEQnYpbew="
        ));
    }

    public FakeDealer(Location location) {
        super(new MobEquipment(), location, false, PROFILE);
    }

    @Override
    public void onInteract(Player player) {
        if (PlayerManager.isSpectator(player)) return;
        Shop.open(player);
    }
}
