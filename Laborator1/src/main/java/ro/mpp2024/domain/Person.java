package ro.mpp2024.domain;

public class Person extends Entity<Integer> {
    private String name;
    private String surname;
    private String cnp;

    public Person(String name, String surname, String cnp) {
        this.name = name;
        this.surname = surname;
        this.cnp = cnp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getCnp() {
        return cnp;
    }

    public void setCnp(String cnp) {
        this.cnp = cnp;
    }
}
