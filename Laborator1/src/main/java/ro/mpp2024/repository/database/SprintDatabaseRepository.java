package ro.mpp2024.repository.database;

import ro.mpp2024.domain.Sprint;
import ro.mpp2024.repository.ISprintRepository;

import java.util.Optional;

public class SprintDatabaseRepository implements ISprintRepository {
    @Override
    public Optional<Sprint> findOne(Integer integer) {
        return Optional.empty();
    }

    @Override
    public Iterable<Sprint> findAll() {
        return null;
    }

    @Override
    public void save(Sprint entity) {
        return;
    }

    @Override
    public void delete(Integer integer) {
        return;
    }

    @Override
    public void update(Sprint entity) {
        return;
    }
}