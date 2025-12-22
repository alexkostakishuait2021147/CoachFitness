package gr.harokopio.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Αναπαριστά έναν γύρο σε μια αθλητική δραστηριότητα.
 * Ένας γύρος αποτελείται από μία ή περισσότερες διαδρομές (Tracks).
 */
public class Lap {
    private LocalDateTime startTime;
    private List<Track> tracks;

    public Lap(LocalDateTime startTime) {
        this.startTime = startTime;
        this.tracks = new ArrayList<>();
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public void addTrack(Track track) {
        this.tracks.add(track);
    }

    /**
     * Υπολογίζει τη συνολική απόσταση του γύρου σε μέτρα.
     */
    public double getTotalDistance() {
        return tracks.stream()
                .mapToDouble(Track::getTotalDistance)
                .sum();
    }

    /**
     * Υπολογίζει τη συνολική διάρκεια του γύρου σε δευτερόλεπτα.
     */
    public long getTotalDuration() {
        return tracks.stream()
                .mapToLong(Track::getTotalDuration)
                .sum();
    }

    /**
     * Υπολογίζει τον μέσο καρδιακό παλμό του γύρου.
     */
    public double getAverageHeartRate() {
        double sum = 0;
        int count = 0;

        for (Track track : tracks) {
            double avg = track.getAverageHeartRate();
            if (avg > 0) {
                sum += avg;
                count++;
            }
        }

        return count > 0 ? sum / count : 0;
    }

    /**
     * Υπολογίζει τον μέγιστο καρδιακό παλμό του γύρου.
     */
    public int getMaxHeartRate() {
        return tracks.stream()
                .mapToInt(Track::getMaxHeartRate)
                .max()
                .orElse(0);
    }

    /**
     * Υπολογίζει τον ελάχιστο καρδιακό παλμό του γύρου.
     */
    public int getMinHeartRate() {
        return tracks.stream()
                .mapToInt(Track::getMinHeartRate)
                .filter(hr -> hr > 0)
                .min()
                .orElse(0);
    }
}
