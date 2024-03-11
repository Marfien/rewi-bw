package dev.marfien.rewibw.shop;

import dev.marfien.rewibw.ResourceType;

public class ShopPrice {

    private final ResourceType type;
    private final byte amount;

    public ShopPrice(ResourceType type, byte amount) {
        this.type = type;
        this.amount = amount;
    }

    public ShopPrice(ResourceType type, int amount) {
        this.type = type;
        this.amount = (byte) amount;
    }

    public byte getAmount() {
        return this.amount;
    }

    public ResourceType getType() {
        return this.type;
    }

    public String toColoredString() {
        return this.type.getChatColor() + String.valueOf(this.amount) + " " + this.type.getTranslation();
    }

}
