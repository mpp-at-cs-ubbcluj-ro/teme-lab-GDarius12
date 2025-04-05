package ro.mpp2024.domain;

public class Employee extends Person {
    private String Phone;
    private String address;
    private String username;
    private String password;
    public Employee(String name,String surname,String cnp,String Phone,String address,String username,String password){
        super(name,surname,cnp);
        this.Phone=Phone;
        this.address=address;
        this.username=username;
        this.password=password;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String Phone) {
        this.Phone = Phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
