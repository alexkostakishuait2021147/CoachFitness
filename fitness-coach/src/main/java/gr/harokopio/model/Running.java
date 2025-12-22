package gr.harokopio.model;

import java.time.LocalDateTime;

/**
 * Αναπαριστά μια δραστηριότητα τρεξίματος.
 */
public class Running extends Activity {

    public Running(String id, LocalDateTime startTime) {
        super(id, startTime);
    }

    @Override
    public String getSportType() {
        return "Running";
    }

    @Override
    public double getCalorieMultiplier() {
        return 8.5; // Περίπου 8.5 θερμίδες ανά kg ανά ώρα για τρέξιμο
    }

    @Override
    public boolean hasDistance() {
        return true;
    }
}
