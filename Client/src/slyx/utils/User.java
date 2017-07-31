package slyx.utils;

/**
 * Created by Antoine Janvier
 * on 31/07/17.
 */
public class User {
    private String firstname;
    private String lastname;
    private int age;
    private String email;
    private Gender gender;
    private String password;
    private boolean isConnected;

    public User(String firstname, String lastname, int age, String email, Gender gender, String password) {
        this.setFirstname(firstname);
        this.setLastname(lastname);
        this.setAge(age);
        this.setEmail(email);
        this.setGender(gender);
        this.setPassword(password);
        this.setConnected(false);
    }

    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }
    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public boolean isConnected() { return isConnected; }
    public void setConnected(boolean connected) { isConnected = connected; }
}
