package dev.marfien.rewibw;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Message {

    public static final Message GAME_ALREADY_RUNNING = bad("Das Spiel läuft bereits.");
    public static final Message FORCEMAP_COMMAND_TOO_LATE = bad("Du kannst in den letzten 10 Sekunden die Map nicht mehr ändern.");
    public static final Message FORCEMAP_COMMAND_USAGE = bad("Verwendung: /forcemap <Map>");

    public static final Message UNKNOWN_MAP = bad("Die Map §a%s§r existiert nicht.");
    public static final Message AVAILABLE_MAPS = system("Verfügbare Maps: §a%s");
    public static final Message MAP_CHANGED = system("Die Map wurde auf §a%s§r geändert.");

    public static final Message COUNTDOWN_STARTED = info("Der Countdown wurde gestartet.");
    public static final Message START_COMMAND_TOO_LATE = bad("§cDer Countdown kann nicht mehr verkürzt werden.");
    public static final Message START_COMMAND_COUNTDOWN_REDUCED = info("Der Countdown wurde verkürzt.");

    public static final Message LOBBY_IDLE = info("Damit die Runde startet, müssen noch §a%d§r Spieler beitreten.");
    public static final Message LOBBY_JOIN = system("%s§r hat den Server betreten §8(§a%d§8/§a" + RewiBWPlugin.getMaxPlayers() + "§8)");
    public static final Message LOBBY_LEAVE = system("%s§r hat den Server verlassen.");
    public static final Message COUNTDOWN_BEGAN = info("Der Countdown hat begonnen.");
    public static final Message GAME_STARTS_IN = info("Die Spielphase beginnt in §a%d§r Sekunden.");

    public static final Message GAME_ENDS_IN_MINUTES = system("Das Spiel endet in §a%d§r Minuten.");
    public static final Message GAME_ENDS_IN_SECONDS = system("Das Spiel endet in §a%d§r Sekunden.");

    public static final Message MOBILE_SHOP_NOT_GROUND = bad("Du kannst den mobilen Shop nur auf dem Boden platzieren.");

    public static final Message PARACHUTE_ON_GROUND = bad("Du stehst berets auf dem Boden.");
    public static final Message PARACHUTE_ALREADY_ACTIVE = bad("Du hast bereits einen aktiven Fallschirm.");
    public static final Message PARACHUTE_BROKE = bad("Dein Fallschirm ist kaputt gegangen.");

    public static final Message ALREADY_TELEPORTING = bad("Du bist bereits im Teleportier-Vorgang!");
    public static final Message TELEPORTER_BELOW_BLOCK = bad("Du musst unter freiem Himmel stehen!");
    public static final Message TELEPORT_CANCELLED = bad("Der Teleport wurde abgebrochen!");
    public static final Message TELEPORTER_LOST = bad("Du hast keinen Teleporter mehr!");

    public static final Message PLAYER_DIED = system("%s§r ist gestorben.");
    public static final Message PLAYER_KILLED = system("%s§r wurde von %s §r[§c❤%s§r] getötet.");

    public static final Message CANNOT_PLACE_BLOCKS_TEAMSPAWN = bad("Du kannst keine Blöcke auf einen Spawnpunkt setzen.");

    public static final Message WEB_COOLDOWN = bad("Du kannst nur alle 20 Sekunden ein Cobweb platzieren.");
    public static final Message SHOP_NOT_ENOUGH_RESOURCES = bad("Du hast nicht genug %s§r.");
    public static final Message OWN_BED_DESTROY = bad("Du kannst dein eigenes Bett nicht zerstören.");
    public static final Message TEAM_CHEST_DESTROY = bad("Eine Teamkiste wurde zerstört.");

    public static final Message SELECT_PERK = system("Du hast das Perk §a%s§r ausgewählt.");
    public static final Message ALREADY_IN_TEAM = bad("Du bist bereits in diesem Team!");
    public static final Message TEAM_JOINED = system("Du bist nun im Team %s§r.");
    public static final Message TEAM_FULL = bad("Das Team ist bereits voll!");
    public static final Message TEAM_ASSIGNED = system("Du wurdest dem Team %s§r zugewiesen.");
    public static final Message NO_TEAM_FOUND = bad("Es konnte kein Team für dich gefunden werden.");
    public static final Message TEAM_BROADCAST_HEADER = of("▆▆▆▆▆▆▆▆▆▆ §6§lTeams§r ▆▆▆▆▆▆▆▆▆▆", ChatColor.WHITE);
    public static final Message TEAM_BROADCAST_FORMAT = of("▆ %sTeam %s§r: §6%s", ChatColor.WHITE);
    public static final Message VOTING_BROADCAST = of(
            " \n" +
                    "          §o§cAbstimmung beendet! §r\n" +
                    "          §6Karte: §a%s\n" +
                    " ",
            ChatColor.WHITE);
    public static final Message VOTE_CHANGED = system("Du hast deine Stimme auf §a%s§r geändert.");
    public static final Message VOTE_CAST = system("Du hast für die Karte §a%s§r abgestimmt.");

    public static final Message BED_DESTROYED = system("Das Bett von Team %s§r wurde von %s§r zerstört.");
    public static final Message TEAM_ELIMINATED = system("Team %s§r wurde vernichtet.");
    public static final Message REMAINING_TEAMS = system("Es verbleiben §a%d§r Teams und §a%d§r Spieler.");

    public static final Message INGAME_LEAVE = system("%s§r hat das Spiel verlassen.");
    public static final Message SERVER_STOPPING = bad("Der Server stoppt in §a%d§r Sekunden.");
    public static final Message BROADCAST_WINNER = of("Team %s§r hat die Runde gewonnen!", ChatColor.AQUA);
    public static final Message NO_WINNER = info("Es konnte kein Team die Runde für sich entscheiden.");
    public static final Message BLOCK_OUT_OF_MAP = bad("Du kannst hier keine Blöcke platzieren!");
    public static final Message TEAM_CHEST_NO_ACCESS = bad("Diese Teamkiste gehört nicht deinem Team!");
    public static final Message PLAYER_NOT_FOUND = bad("Der Spieler konnte nicht gefunden werden.");
    public static final Message DEATH_TITLE = system("Du bist gestorben");
    public static final Message DEATH_TITLE_KILLED = system("Du wurdest getötet");
    public static final Message RESET_PERK = system("Dein Perk wurde zurückgesetzt.");
    public static final Message RESCUE_PLATFORM_ON_GROUND = bad("Du stehst auf dem Boden.");
    public static final Message SHOP_NOT_ENOUGH_SPACE = bad("Du hast nicht genug Platz in deinem Inventar.");
    public static final Message SPECTATOR_TARGET = of("Du bist nun in der Sicht von §a%s§r.", ChatColor.AQUA);

    private final String message;

    @Override
    public String toString() {
        return this.message;
    }

    public String format(Object... args) {
        return String.format(this.message, args);
    }

    public static Message good(String message) {
        return of(message, ChatColor.GREEN);
    }

    public static Message system(String message) {
        return of(message, ChatColor.GOLD);
    }

    public static Message info(String message) {
        return of(message, ChatColor.GRAY);
    }

    public static Message bad(String message) {
        return of(message, ChatColor.RED);
    }

    public static Message of(String message, ChatColor baseColor) {
        return new Message(baseColor + message.replace("§r", "§r" + baseColor));
    }

    public static void broadcast(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }

}
