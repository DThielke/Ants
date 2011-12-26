package com.dthielke.ants;

public interface FoodSource extends Zone {
    public int getFood();

    public boolean hasFood();

    public void setFoodCount(int food);
}
