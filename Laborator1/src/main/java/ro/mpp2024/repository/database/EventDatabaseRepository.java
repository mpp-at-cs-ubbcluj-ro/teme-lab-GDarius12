package ro.mpp2024.repository.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.mpp2024.domain.Event;
import ro.mpp2024.repository.IEventRepository;
import ro.mpp2024.domain.AgeGroup;
import ro.mpp2024.domain.Sprint;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class EventDatabaseRepository implements IEventRepository {
    private JdbcUtils jdbcUtils;
    private static final Logger logger = LogManager.getLogger();

    public EventDatabaseRepository(Properties properties){
        logger.info("Initializam EventDatabaseRepository");
        jdbcUtils = new JdbcUtils(properties);
    }
    @Override
    public Optional<Event> findOne(Integer integer){
        logger.traceEntry("Gaseste Event-ul dupa id: {}", integer);
        Connection connection=jdbcUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT ev.id AS eventId, ev.sprintId, ev.date, " +
                        "s.id AS sprintId, s.distance, " +
                        "ag.id AS ageGroupId, ag.lower, ag.upper " +
                        "FROM events ev " +
                        "JOIN sprints s ON ev.sprintId = s.id " +
                        "JOIN eventDetails evd ON evd.eventId = ev.id " +
                        "JOIN ageGroups ag ON evd.ageGroupId = ag.id " +
                        "WHERE ev.id = ?")) {

            preparedStatement.setInt(1, integer);
            try(ResultSet result = preparedStatement.executeQuery()){
                Event event = null;

                while (result.next()) {
                    int eventId = result.getInt("eventId");
                    int sprintId = result.getInt("sprintId");
                    float distance = result.getFloat("distance");

                    LocalDateTime dateTime = LocalDateTime.parse(result.getString("date"), Event.DATE_TIME_FORMATTER);

                    Sprint sprint = new Sprint(distance);
                    sprint.setId(sprintId);

                    AgeGroup ageGroup = new AgeGroup(
                            result.getInt("lower"),
                            result.getInt("upper")
                    );
                    ageGroup.setId(result.getInt("ageGroupId"));

                    if (event == null) {
                        event = new Event(sprint, new ArrayList<>(), dateTime);
                        event.setId(eventId);
                    }

                    event.getAgeGroups().add(ageGroup);
                }

                return Optional.ofNullable(event);
            }
        } catch (SQLException e) {
            logger.error(e);
        }

        return Optional.empty();
    }
    @Override
    public Iterable<Event> findAll() {
        logger.traceEntry("Gaseste toate sprinturile disponibile");

        Connection connection = jdbcUtils.getConnection();

        List<Event> availablesprint = new ArrayList<>();

        try(PreparedStatement preparedStatement = connection.prepareStatement(
                "select ev.id as eventId, ev.sprintId, ev.date, " +
                        "s.id as sprintId, s.distance, " +
                        "ag.id as ageGroupId, ag.lower, ag.upper " +
                        "from events ev " +
                        "join sprints s on evs.sprintId = s.id " +
                        "join eventDetails evd on evd.eventId = ev.id " +
                        "join ageGroups ag on asd.ageGroupId = ag.id")){
            try(ResultSet result = preparedStatement.executeQuery()){
                Map<Integer, Event> sprintMap = new HashMap<>();

                while(result.next()){
                    int eventId = result.getInt("eventId");
                    int sprintId = result.getInt("sprintId");
                    float distance = result.getFloat("distance");
                    LocalDateTime dateTime = LocalDateTime.parse(result.getString("date"), Event.DATE_TIME_FORMATTER);

                    Sprint sprint = new Sprint(distance);
                    sprint.setId(sprintId);

                    AgeGroup ageGroup = new AgeGroup(
                            result.getInt("lower"),
                            result.getInt("upper")
                    );
                    ageGroup.setId(result.getInt("ageGroupId"));

                    Event event = sprintMap.get(eventId);

                    if (event == null){
                        event = new Event(sprint, new ArrayList<>(), dateTime);
                        event.setId(eventId);
                        sprintMap.put(eventId, event);
                        availablesprint.add(event);
                    }

                    event.getAgeGroups().add(ageGroup);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
        }

        return availablesprint;
    }

    @Override
    public void save(Event entity) {
        return;
    }

    @Override
    public void delete(Integer integer) {
        return;
    }

    @Override
    public void update(Event entity) {
        return;
    }
}

