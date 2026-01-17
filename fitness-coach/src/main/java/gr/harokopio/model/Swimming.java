package gr.harokopio.model;

import java.time.LocalDateTime;


public class Swimming extends Activity {

    public Swimming(String id, LocalDateTime startTime) {
        super(id, startTime);
    }

    @Override
    public String getSportType() {
        return "Swimming";
    }

    @Override
    public double getCalorieMultiplier() {
        return 9.0; // Περίπου 9.0 θερμίδες ανά kg ανά ώρα για κολύμπι
    }

    @Override
    public boolean hasDistance() {
        return true;
    }
}
