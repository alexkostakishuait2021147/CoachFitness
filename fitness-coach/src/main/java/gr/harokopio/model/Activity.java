package gr.harokopio.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Αφηρημένη κλάση που αναπαριστά μια αθλητική δραστηριότητα.
 */
public abstract class Activity {
    private String id;
    private LocalDateTime startTime;
    private List<Lap> laps;

    public Activity(String id, LocalDateTime startTime) {
        this.id = id;
        this.startTime = startTime;
        this.laps = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public List<Lap> getLaps() {
        return laps;
    }

    public void addLap(Lap lap) {
        this.laps.add(lap);
    }

    /**
     * Επιστρέφει τον τύπο της δραστηριότητας (π.χ. "Running", "Cycling").
     */
    public abstract String getSportType();

    /**
     * Επιστρέφει τον πολλαπλασιαστή θερμίδων για την απλή μέθοδο υπολογισμού.
     */
    public abstract double getCalorieMultiplier();

    /**
     * Επιστρέφει αν η δραστηριότητα έχει απόσταση.
     */
    public abstract boolean hasDistance();

    /**
     * Υπολογίζει τη συνολική απόσταση της δραστηριότητας σε μέτρα.
     */
    public double getTotalDistance() {
        return laps.stream()
                .mapToDouble(Lap::getTotalDistance)
                .sum();
    }

    /**
     * Υπολογίζει τη συνολική διάρκεια της δραστηριότητας σε δευτερόλεπτα.
     */
    public long getTotalDuration() {
        return laps.stream()
                .mapToLong(Lap::getTotalDuration)
                .sum();
    }

    /**
     * Υπολογίζει τη μέση ωριαία ταχύτητα σε km/h.
     */
    public double getAverageSpeed() {
        double distanceKm = getTotalDistance() / 1000.0;
        double durationHours = getTotalDuration() / 3600.0;
        return durationHours > 0 ? distanceKm / durationHours : 0;
    }

    /**
     * Υπολογίζει τη μέση ταχύτητα ανά χιλιόμετρο σε min/km.
     */
    public double getAveragePace() {
        double distanceKm = getTotalDistance() / 1000.0;
        double durationMinutes = getTotalDuration() / 60.0;
        return distanceKm > 0 ? durationMinutes / distanceKm : 0;
    }

    /**
     * Υπολογίζει τον μέσο καρδιακό παλμό της δραστηριότητας.
     */
    public double getAverageHeartRate() {
        double sum = 0;
        int count = 0;

        for (Lap lap : laps) {
            double avg = lap.getAverageHeartRate();
            if (avg > 0) {
                sum += avg;
                count++;
            }
        }

        return count > 0 ? sum / count : 0;
    }

    /**
     * Υπολογίζει τον μέγιστο καρδιακό παλμό της δραστηριότητας.
     */
    public int getMaxHeartRate() {
        return laps.stream()
                .mapToInt(Lap::getMaxHeartRate)
                .max()
                .orElse(0);
    }

    /**
     * Υπολογίζει τον ελάχιστο καρδιακό παλμό της δραστηριότητας.
     */
    public int getMinHeartRate() {
        return laps.stream()
                .mapToInt(Lap::getMinHeartRate)
                .filter(hr -> hr > 0)
                .min()
                .orElse(0);
    }

    /**
     * Υπολογίζει τις θερμίδες με την απλή μέθοδο: C = μ * w * t
     * @param weight το βάρος του χρήστη σε kg
     * @return οι θερμίδες που καταναλώθηκαν
     */
    public double calculateCaloriesSimple(double weight) {
        double durationHours = getTotalDuration() / 3600.0;
        return getCalorieMultiplier() * weight * durationHours;
    }

    /**
     * Υπολογίζει τις θερμίδες με την προχωρημένη μέθοδο που χρησιμοποιεί καρδιακούς παλμούς.
     * @param weight το βάρος του χρήστη σε kg
     * @param age η ηλικία του χρήστη
     * @param isMale true αν ο χρήστης είναι άνδρας, false αν είναι γυναίκα
     * @return οι θερμίδες που καταναλώθηκαν
     */
    public double calculateCaloriesAdvanced(double weight, int age, boolean isMale) {
        double avgHeartRate = getAverageHeartRate();
        if (avgHeartRate == 0) {
            // Αν δεν υπάρχουν δεδομένα καρδιακών παλμών, χρησιμοποιούμε την απλή μέθοδο
            return calculateCaloriesSimple(weight);
        }

        double durationMinutes = getTotalDuration() / 60.0;

        if (isMale) {
            // Φόρμουλα για άνδρες
            return (-55.0969 + (0.6309 * avgHeartRate) + (0.1966 * weight) + (0.2017 * age)) * durationMinutes / 4.184;
        } else {
            // Φόρμουλα για γυναίκες
            return (-20.4022 + (0.4472 * avgHeartRate) + (0.1263 * weight) + (0.074 * age)) * durationMinutes / 4.184;
        }
    }
}
