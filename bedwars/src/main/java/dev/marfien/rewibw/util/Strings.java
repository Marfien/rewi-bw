package dev.marfien.rewibw.util;

import lombok.experimental.UtilityClass;

import java.util.concurrent.ThreadLocalRandom;

@UtilityClass
public class Strings {

    private static final String GAMEID_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static String generateGameId() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            builder.append(GAMEID_CHARS.charAt(ThreadLocalRandom.current().nextInt(GAMEID_CHARS.length())));
        }
        return builder.toString();
    }

}
