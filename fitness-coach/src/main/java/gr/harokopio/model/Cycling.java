package gr.harokopio.model;

import java.time.LocalDateTime;

/**
 * Αναπαριστά μια δραστηριότητα ποδηλασίας.
 */
public class Cycling extends Activity {

    public Cycling(String id, LocalDateTime startTime) {
        super(id, startTime);
    }

    @Override
    public String getSportType() {
        return "Biking";
    }

    @Override
    public double getCalorieMultiplier() {
        return 7.0; // Περίπου 7.0 θερμίδες ανά kg ανά ώρα για ποδηλασία
    }

    @Override
    public boolean hasDistance() {
        return true;
    }
}
