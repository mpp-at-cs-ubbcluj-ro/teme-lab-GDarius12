package ro.mpp2024.repository;

import ro.mpp2024.domain.Child;

import java.util.Optional;

public interface IChildRepository extends IRepository<Integer,Child> {
    Optional<Child> findOneByCnp (String cnp);

}
