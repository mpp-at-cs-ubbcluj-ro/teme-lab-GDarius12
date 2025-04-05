package ro.mpp2024;

import ro.mpp2024.domain.*;
import ro.mpp2024.repository.IChildRepository;
import ro.mpp2024.repository.IEmployeeRepository;
import ro.mpp2024.repository.IEventRepository;
import ro.mpp2024.repository.database.ChildDatabaseRepository;
import ro.mpp2024.repository.database.EmployeeDatabaseRepository;
import ro.mpp2024.repository.database.EventDatabaseRepository;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        Properties props = new Properties();
        try {
            props.load(new FileReader("bd.config"));
        } catch (IOException e) {
            System.out.println("Nu s-a gasit fisierul bd.config " + e);
            return;
        }

        IChildRepository childRepository = new ChildDatabaseRepository(props);
        IEventRepository eventRepository = new EventDatabaseRepository(props);
        IEmployeeRepository employeeRepository = new EmployeeDatabaseRepository(props);

        testRepository(childRepository);
    }

    private static void testRepository(IChildRepository repo) {
        System.out.println("--- Test Repository ---");
        Child child = new Child("John", "Doe", "1234567890123");
        child.setId(1);
        repo.save(child);
        System.out.println("Added child: " + child);

        Optional<Child> foundChild = repo.findOne(child.getId());
        System.out.println("Found child: " + foundChild.orElse(null));

        child.setName("Jane");
        repo.update(child);
        System.out.println("Updated child: " + repo.findOne(child.getId()).orElse(null));

        repo.delete(child.getId());
        System.out.println("Deleted child, found: " + repo.findOne(child.getId()).orElse(null));
    }

}
