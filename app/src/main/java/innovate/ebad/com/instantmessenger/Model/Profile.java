package innovate.ebad.com.instantmessenger.Model;


public class Profile {

    private String firstName, lastName, pictureURI;
    private int age;

    public Profile() {

    }

    public Profile(String firstName, String lastName, String pictureURI, int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.pictureURI = pictureURI;
        this.age = age;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPictureURI() {
        return pictureURI;
    }

    public void setPictureURI(String pictureURI) {
        this.pictureURI = pictureURI;
    }

    public int getAge() {
        return age;
    }
}
