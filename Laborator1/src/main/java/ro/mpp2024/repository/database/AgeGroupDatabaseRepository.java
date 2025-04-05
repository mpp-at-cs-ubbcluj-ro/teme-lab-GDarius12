package ro.mpp2024.repository.database;

import ro.mpp2024.domain.AgeGroup;
import ro.mpp2024.repository.IAgeGroupRepository;

import java.util.Optional;

public class AgeGroupDatabaseRepository implements IAgeGroupRepository {
    @Override
    public Optional<AgeGroup> findOne(Integer integer){return Optional.empty();}
    @Override
    public Iterable<AgeGroup> findAll(){return null;}
    @Override
    public void save(AgeGroup entity){return;}
    @Override
    public void delete(Integer integer){return;}
    @Override
    public void update(AgeGroup entity){return;}
}
