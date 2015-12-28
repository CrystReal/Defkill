package com.streamcraft.Defkill.Models.classes;

import com.streamcraft.Defkill.Models.enums.ItemCostType;

/**
 * Created by Alex
 * Date: 01.11.13  23:25
 */
public class CostOfItem {
    private ItemCostType type = ItemCostType.EXP;
    private double cost = 0;

    public CostOfItem(ItemCostType t, double c) {
        this.type = t;
        this.cost = c;
    }

    public ItemCostType getType() {
        return type;
    }

    public double howMutch() {
        return cost;
    }
}
