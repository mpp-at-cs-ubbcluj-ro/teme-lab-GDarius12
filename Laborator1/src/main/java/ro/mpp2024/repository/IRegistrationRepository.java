package ro.mpp2024.repository;
import ro.mpp2024.domain.AgeGroup;
import ro.mpp2024.domain.Registration;
import ro.mpp2024.domain.Sprint;

import java.util.Optional;

public interface IRegistrationRepository extends IRepository<Integer,Registration> {
    public Optional<Registration> findOneByPersonCnp(String cnp);
    public Iterable<Registration> findAllBySprintAndGroupAge(Sprint sprint, AgeGroup ageGroup);
}
