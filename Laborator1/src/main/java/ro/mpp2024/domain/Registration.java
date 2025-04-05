package ro.mpp2024.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Registration extends Entity<Integer> {
    private Person person;
    private List<Event> event;
    private LocalDateTime dateTime;

    public Registration(Person person, List<Event> event, LocalDateTime dateTime) {
        this.person = person;
        this.event = event;
        this.dateTime = dateTime;
    }
    public static final Integer numberOfEvents = 2;
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public List<Event> getEvent() {
        return event;
    }

    public void setEvent(List<Event> event) {
        this.event = event;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
