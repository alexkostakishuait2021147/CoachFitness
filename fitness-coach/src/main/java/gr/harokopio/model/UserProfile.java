package gr.harokopio.model;

/**
 * Αναπαριστά το προφίλ ενός χρήστη με τα προσωπικά του στοιχεία.
 */
public class UserProfile {
    private double weight; // σε kg
    private int age;
    private boolean isMale;

    public UserProfile() {
        this.weight = 70.0; // default
        this.age = 30; // default
        this.isMale = true; // default
    }

    public UserProfile(double weight, int age, boolean isMale) {
        this.weight = weight;
        this.age = age;
        this.isMale = isMale;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isMale() {
        return isMale;
    }

    public void setMale(boolean male) {
        isMale = male;
    }
}
