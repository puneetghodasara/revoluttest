package me.puneetghodasara.revolut.dao;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryDao<ID, Entity> {
    /**
     * {@link ConcurrentHashMap} will take care of synchronization that database can provides.
     */
    protected final Map<ID, Entity> storage = new ConcurrentHashMap<>();
}
