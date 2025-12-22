package gr.harokopio.model;

import java.time.LocalDateTime;

/**
 * Αναπαριστά μια δραστηριότητα περπατήματος.
 */
public class Walking extends Activity {

    public Walking(String id, LocalDateTime startTime) {
        super(id, startTime);
    }

    @Override
    public String getSportType() {
        return "Walking";
    }

    @Override
    public double getCalorieMultiplier() {
        return 3.5; // Περίπου 3.5 θερμίδες ανά kg ανά ώρα για περπάτημα
    }

    @Override
    public boolean hasDistance() {
        return true;
    }
}
