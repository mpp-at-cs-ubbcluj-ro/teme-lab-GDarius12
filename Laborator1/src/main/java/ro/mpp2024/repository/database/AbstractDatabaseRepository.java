package ro.mpp2024.repository.database;

import ro.mpp2024.domain.Entity;
import ro.mpp2024.repository.IRepository;

import java.util.Optional;

public abstract class AbstractDatabaseRepository<ID, E extends Entity<ID>> implements IRepository<ID, E> {
    protected String tableName;

    public AbstractDatabaseRepository(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public abstract Optional<E> findOne(ID id);

    @Override
    public abstract Iterable<E> findAll();

    @Override
    public abstract void save(E entity);

    @Override
    public abstract void delete(ID id);

    @Override
    public abstract void update(E entity);
}
