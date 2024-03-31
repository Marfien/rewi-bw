package dev.marfien.rewibw.shop;

import dev.marfien.rewibw.ResourceType;
import lombok.Getter;

@Getter
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

    public String toColoredString() {
        return this.type.getChatColor() + String.valueOf(this.amount) + " " + this.type.getTranslation();
    }

}
