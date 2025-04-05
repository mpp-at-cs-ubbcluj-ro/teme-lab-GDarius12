package ro.mpp2024.repository;

import ro.mpp2024.domain.Employee;

import java.util.Optional;

public interface IEmployeeRepository extends IRepository<Integer,Employee> {
    Optional<Employee> findOneByUsername(String username);

}
