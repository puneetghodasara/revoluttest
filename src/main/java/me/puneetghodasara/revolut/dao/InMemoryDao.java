package me.puneetghodasara.revolut.dao;

import me.puneetghodasara.revolut.entity.UserEntity;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class InMemoryDao<ID, Entity> {
    /**
     * {@link ConcurrentHashMap} will take care of synchronization that database can provides.
     */
    protected final Map<ID, Entity> storage = new ConcurrentHashMap<>();

    /**
     * Get Entity By ID
     * @param id
     * @return
     */
    public Optional<Entity> getById(final ID id) {
        return storage.entrySet()
                .stream()
                .filter(record -> record.getKey().equals(id))
                .map(Map.Entry::getValue)
                .findAny();
    }

    public Entity updateEntity(final ID id, final Entity entity) {
        storage.put(id, entity);
        return storage.get(id);
    }

    public void deleteById(final ID id) {
        storage.remove(id);
    }

    public Stream<Entity> getAll() {
        return storage.entrySet()
                .stream()
                .map(Map.Entry::getValue);

    }
}
