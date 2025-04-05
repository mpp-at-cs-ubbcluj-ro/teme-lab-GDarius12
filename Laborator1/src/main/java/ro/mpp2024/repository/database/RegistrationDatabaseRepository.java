package ro.mpp2024.repository.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.mpp2024.domain.*;
import ro.mpp2024.repository.IRegistrationRepository;
import ro.mpp2024.repository.exception.RepositoryException;
import ro.mpp2024.utils.Calculeaza_Varsta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class RegistrationDatabaseRepository implements IRegistrationRepository {
    private JdbcUtils jdbcUtils;
    private Logger logger = LogManager.getLogger();

    public RegistrationDatabaseRepository(Properties properties){
        logger.traceEntry("Se initializeaza RegistrationDatabaseRepository cu proprietatile: {}", properties);

        jdbcUtils = new JdbcUtils(properties);
    }

    @Override
    public Optional<Registration> findOne(Integer integer) {
        logger.traceEntry("Gaseste o inregistrare cu id-ul {}", integer);

        Connection connection = jdbcUtils.getConnection();

        try(PreparedStatement preparedStatement = connection.prepareStatement(
                "select r.id as registrationId, r.personId, r.date, " +
                        "c.id as childId, c.name, c.surname, c.cnp, " +
                        "ev.id as eventId, ev.date as sprintDate, " +
                        "s.id as sprintId, s.distance, " +
                        "ag.id as ageGroupId, ag.lower, ag.upper " +
                        "from registrations r " +
                        "join children c on r.personId = c.id " +
                        "join registrationDetails rd on r.id = rd.registrationId " +
                        "join events ev on rd.eventId = ev.id " +
                        "join sprints s on ev.sprintId = s.id " +
                        "join eventDetails evd on evd.eventId = ev.id " +
                        "join ageGroups ag on asd.ageGroupId = ag.id " +
                        "where r.id = ?"
        )){
            preparedStatement.setInt(1, integer);
            try(ResultSet result = preparedStatement.executeQuery()){
                Registration registration = null;
                while(result.next()){
                    int registrationId = result.getInt("registrationId");
                    int childId = result.getInt("childId");
                    String name = result.getString("name");
                    String surname = result.getString("surname");
                    String cnp = result.getString("cnp");
                    LocalDateTime registrationDate = LocalDateTime.parse(result.getString("date"), Registration.DATE_TIME_FORMATTER);

                    Person person = new Child(name, surname, cnp);
                    person.setId(childId);
                    if (registration == null) {
                        registration = new Registration(person, new ArrayList<>(), registrationDate);
                        registration.setId(registrationId);
                    }
                    Sprint sprint = new Sprint(result.getFloat("distance"));
                    sprint.setId(result.getInt("sprintId"));
                    int eventId = result.getInt("eventId");
                    Event event = registration.getEvent().stream()
                            .filter(s -> s.getId().equals(eventId))
                            .findFirst()
                            .orElse(null);
                    if (event == null) {
                        event = new Event(
                                sprint,
                                new ArrayList<>(),
                                LocalDateTime.parse(result.getString("sprintDate"), Event.DATE_TIME_FORMATTER)
                        );
                        event.setId(eventId);
                        registration.getEvent().add(event);
                    }
                    AgeGroup ageGroup = new AgeGroup(
                            result.getInt("lower"),
                            result.getInt("upper")
                    );
                    ageGroup.setId(result.getInt("ageGroupId"));

                    if (!event.getAgeGroups().contains(ageGroup)) {
                        event.getAgeGroups().add(ageGroup);
                    }
                }
                return Optional.ofNullable(registration);
            }
        } catch (SQLException e) {
            logger.error(e);
        }

        return Optional.empty();
    }

    @Override
    public Iterable<Registration> findAll() {
        logger.traceEntry("Gaseste toate inregistrarile");

        List<Registration> registration = new ArrayList<>();

        Connection connection = jdbcUtils.getConnection();

        try(PreparedStatement preparedStatement = connection.prepareStatement(
                "select r.id as registrationId, r.personId, r.date, " +
                        "c.id as childId, c.name, c.surname, c.cnp, " +
                        "ev.id as eventId, ev.date as sprintDate, " +
                        "s.id as sprintId, s.distance, " +
                        "ag.id as ageGroupId, ag.lower, ag.upper " +
                        "from registrations r " +
                        "join children c on r.personId = c.id " +
                        "join registrationDetails rd on r.id = rd.registrationId " +
                        "join events ev on rd.eventId = ev.id " +
                        "join sprints s on ev.sprintId = s.id " +
                        "join eventDetails evd on evd.eventId = ev.id " +
                        "join ageGroups ag on asd.ageGroupId = ag.id")){
            try(ResultSet result = preparedStatement.executeQuery()){
                Map<Integer, Registration> registrationMap = new HashMap<>();

                while(result.next()){
                    int registrationId = result.getInt("registrationId");
                    int childId = result.getInt("childId");
                    String name = result.getString("name");
                    String surname = result.getString("surname");
                    String cnp = result.getString("cnp");
                    LocalDateTime registrationDate = LocalDateTime.parse(result.getString("date"), Registration.DATE_TIME_FORMATTER);

                    Person person = new Child(name, surname, cnp);
                    person.setId(childId);

                    Registration registrations = registrationMap.get(registrationId);
                    if (registrations == null) {
                        registrations = new Registration(person, new ArrayList<>(), registrationDate);
                        registrations.setId(registrationId);
                        registrationMap.put(registrationId, registrations);
                        registration.add(registrations);
                    }
                    Sprint sprint = new Sprint(result.getFloat("distance"));
                    sprint.setId(result.getInt("sprintId"));
                    int eventId = result.getInt("eventId");
                    Event event = registrations.getEvent().stream()
                            .filter(s -> s.getId().equals(eventId))
                            .findFirst()
                            .orElse(null);
                    if (event == null) {
                        event = new Event(
                                sprint,
                                new ArrayList<>(),
                                LocalDateTime.parse(result.getString("sprintDate"), Event.DATE_TIME_FORMATTER)
                        );
                        event.setId(eventId);
                        registrations.getEvent().add(event);
                    }

                    AgeGroup ageGroup = new AgeGroup(
                            result.getInt("lower"),
                            result.getInt("upper")
                    );
                    ageGroup.setId(result.getInt("ageGroupId"));

                    if (!event.getAgeGroups().contains(ageGroup)) {
                        event.getAgeGroups().add(ageGroup);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error(e);
        }

        return registration;
    }

    @Override
    public void save(Registration entity) {
        logger.traceEntry("Salvam copilul {}", entity);

        int age = Calculeaza_Varsta.Varsta_CNP(entity.getPerson().getCnp());
        for (var event : entity.getEvent()){
            boolean ok = false;
            for (var ageGroup : event.getAgeGroups()){
                if (ageGroup.getLower() <= age && ageGroup.getUpper() >= age) {
                    ok = true;
                    break;
                }
            }
            if (!ok){
                throw new RepositoryException("Persoana nu poate participa la acest sprint!");
            }
        }

        var registrationExist = findOneByPersonCnp(entity.getPerson().getCnp());
        if (registrationExist.isPresent()){
            if (registrationExist.get().getEvent().size() == Registration.numberOfEvents){
                throw new RepositoryException("Persoana s-a inscris deja la 2 sprinturi!");
            }
        }

        Connection connection = jdbcUtils.getConnection();
        try{
            int registrationId = 0;
            try(PreparedStatement preparedStatement = connection.prepareStatement("insert into registrations(personId, date) values (?, ?) returning id")) {
                logger.traceEntry("Salvam tabela inregistrarilor");
                preparedStatement.setInt(1, entity.getPerson().getId());
                preparedStatement.setString(2, String.valueOf(entity.getDateTime().format(Registration.DATE_TIME_FORMATTER)));
                try(ResultSet result = preparedStatement.executeQuery()){
                    if (result.next()){
                        registrationId = result.getInt("id");
                    }
                }
            }

            try(PreparedStatement preparedStatement = connection.prepareStatement("insert into registrationDetails(registrationId, availableSprintId) values (?, ?)")){
                logger.trace("Salvam in tabela registrationDetails ");
                preparedStatement.setInt(1, registrationId);
                for (var availableSprint : entity.getEvent()){
                    preparedStatement.setInt(2, availableSprint.getId());
                    preparedStatement.executeUpdate();
                }
            }

        } catch (SQLException e) {
            logger.error(e);
        }
    }

    @Override
    public void delete(Integer integer) {
        return;
    }

    @Override
    public void update(Registration entity) {
        return;
    }

    @Override
    public Optional<Registration> findOneByPersonCnp(String cnp) {
        logger.traceEntry("Cautam o inregistrare dupa id");

        Connection connection = jdbcUtils.getConnection();

        try(PreparedStatement preparedStatement = connection.prepareStatement(
                "select r.id as registrationId, r.personId, r.date, " +
                        "c.id as childId, c.name, c.surname, c.cnp, " +
                        "ev.id as eventId, ev.date as sprintDate, " +
                        "s.id as sprintId, s.distance, " +
                        "ag.id as ageGroupId, ag.lower, ag.upper " +
                        "from registrations r " +
                        "join children c on r.personId = c.id " +
                        "join registrationDetails rd on r.id = rd.registrationId " +
                        "join events ev on rd.eventId = ev.id " +
                        "join sprints s on ev.sprintId = s.id " +
                        "join eventDetails evd on evd.eventId = ev.id " +
                        "join ageGroups ag on asd.ageGroupId = ag.id " +
                        "where c.cnp = ?"
        )){
            preparedStatement.setString(1, cnp);
            try(ResultSet result = preparedStatement.executeQuery()){
                Registration registration = null;
                while(result.next()){
                    int registrationId = result.getInt("registrationId");
                    int childId = result.getInt("childId");
                    String name = result.getString("name");
                    String surname = result.getString("surname");
                    String ccnp = result.getString("cnp");
                    LocalDateTime registrationDate = LocalDateTime.parse(result.getString("date"), Registration.DATE_TIME_FORMATTER);

                    Person person = new Child(name, surname, ccnp);
                    person.setId(childId);
                    if (registration == null) {
                        registration = new Registration(person, new ArrayList<>(), registrationDate);
                        registration.setId(registrationId);
                    }
                    Sprint sprint = new Sprint(result.getFloat("distance"));
                    sprint.setId(result.getInt("sprintId"));
                    int eventId = result.getInt("evendId");
                    Event event = registration.getEvent().stream()
                            .filter(s -> s.getId().equals(eventId))
                            .findFirst()
                            .orElse(null);
                    if (event == null) {
                        event = new Event(
                                sprint,
                                new ArrayList<>(),
                                LocalDateTime.parse(result.getString("sprintDate"), Event.DATE_TIME_FORMATTER)
                        );
                        event.setId(eventId);
                        registration.getEvent().add(event);
                    }
                    AgeGroup ageGroup = new AgeGroup(
                            result.getInt("lower"),
                            result.getInt("upper")
                    );
                    ageGroup.setId(result.getInt("ageGroupId"));

                    if (!event.getAgeGroups().contains(ageGroup)) {
                        event.getAgeGroups().add(ageGroup);
                    }
                }

                return Optional.ofNullable(registration);
            }
        } catch (SQLException e) {
            logger.error(e);
        }

        return Optional.empty();
    }

    @Override
    public Iterable<Registration> findAllBySprintAndGroupAge(Sprint sprint, AgeGroup ageGroup) {
        logger.traceEntry("Gaseste toate inregistrarile dupa sprint si ageGroup");

        List<Registration> registration = new ArrayList<>();

        Connection connection = jdbcUtils.getConnection();

        try(PreparedStatement preparedStatement = connection.prepareStatement(
                "select r.id as registrationId, r.personId, r.date, " +
                        "c.id as childId, c.name, c.surname, c.cnp, " +
                        "ev.id as eventId, ev.date as sprintDate, " +
                        "s.id as sprintId, s.distance, " +
                        "ag.id as ageGroupId, ag.lower, ag.upper " +
                        "from registrations r " +
                        "join children c on r.personId = c.id " +
                        "join registrationDetails rd on r.id = rd.registrationId " +
                        "join events ev on rd.eventId = ev.id " +
                        "join sprints s on ev.sprintId = s.id " +
                        "join eventDetails evd on evd.eventId = ev.id " +
                        "join ageGroups ag on asd.ageGroupId = ag.id " +
                        "where s.id = ? and ag.id = ?")){
            preparedStatement.setInt(1, sprint.getId());
            preparedStatement.setInt(2, ageGroup.getId());
            try(ResultSet result = preparedStatement.executeQuery()){
                Map<Integer, Registration> registrationMap = new HashMap<>();

                while(result.next()){
                    int registrationId = result.getInt("registrationId");
                    int childId = result.getInt("childId");
                    String name = result.getString("name");
                    String surname = result.getString("surname");
                    String cnp = result.getString("cnp");
                    LocalDateTime registrationDate = LocalDateTime.parse(result.getString("date"), Registration.DATE_TIME_FORMATTER);
                    Person person = new Child(name, surname, cnp);
                    person.setId(childId);

                    Registration registrations = registrationMap.get(registrationId);
                    if (registrations == null) {
                        registrations = new Registration(person, new ArrayList<>(), registrationDate);
                        registrations.setId(registrationId);
                        registrationMap.put(registrationId, registrations);
                        registration.add(registrations);
                    }

                    Sprint sprint1 = new Sprint(result.getFloat("distance"));
                    sprint1.setId(result.getInt("sprintId"));

                    int eventId = result.getInt("eventId");
                    Event event = registrations.getEvent().stream()
                            .filter(s -> s.getId().equals(eventId))
                            .findFirst()
                            .orElse(null);

                    if (event == null) {
                        event = new Event(
                                sprint1,
                                new ArrayList<>(),
                                LocalDateTime.parse(result.getString("sprintDate"), Event.DATE_TIME_FORMATTER)
                        );
                        event.setId(eventId);
                        registrations.getEvent().add(event);
                    }

                    AgeGroup ageGroup1 = new AgeGroup(
                            result.getInt("lower"),
                            result.getInt("upper")
                    );
                    ageGroup1.setId(result.getInt("ageGroupId"));

                    if (!event.getAgeGroups().contains(ageGroup1)) {
                        event.getAgeGroups().add(ageGroup1);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error(e);
        }

        return registration;
    }
}
