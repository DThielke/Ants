package com.dthielke.ants;

public interface AntFactory<T extends Ant> {
    public T create();
}
