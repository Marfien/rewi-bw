package dev.marfien.rewibw.shared;

public class InventoryUtil {

    public static int rowsNeeded(int itemCount) {
        int minRows = itemCount / 9;
        int floorDiv = minRows * 9;

        if (minRows == 0) return 1;

        return floorDiv == itemCount
                ? minRows
                : minRows + 1;
    }

    public static int[] calcCenterIndexes(int itemCount) {
        int[] indexes = new int[itemCount];

        int fullRows = itemCount / 9;
        int specialRowOffset = fullRows * 9;
        for (int i = 0; i < specialRowOffset; i++) {
            indexes[i] = i;
        }

        if (specialRowOffset == indexes.length) return indexes;

        int restCount = itemCount - specialRowOffset;
        boolean isEven = restCount % 2 == 0;
        int start = 5 - restCount / 2;

        int arrayOffset = 0;
        for (int slotOffset = 0; slotOffset < restCount + (isEven ? 1 : 0); slotOffset++) {
            // Skip the center slot if the rest is even
            if (start + slotOffset == 5 && isEven) continue;

            indexes[specialRowOffset + arrayOffset++] = specialRowOffset - 1 + start + slotOffset;
        }

        return indexes;
    }

}
