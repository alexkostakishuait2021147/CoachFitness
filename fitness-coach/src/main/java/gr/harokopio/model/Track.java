package gr.harokopio.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Αναπαριστά μια διαδρομή που αποτελείται από μια σειρά σημείων (Trackpoints).
 */
public class Track {
    private List<Trackpoint> trackpoints;

    public Track() {
        this.trackpoints = new ArrayList<>();
    }

    public List<Trackpoint> getTrackpoints() {
        return trackpoints;
    }

    public void addTrackpoint(Trackpoint trackpoint) {
        this.trackpoints.add(trackpoint);
    }

    /**
     * Υπολογίζει τη συνολική απόσταση της διαδρομής σε μέτρα.
     */
    public double getTotalDistance() {
        if (trackpoints.isEmpty()) {
            return 0;
        }

        // Χρησιμοποιούμε την απόσταση από το τελευταίο σημείο (αθροιστικά)
        Trackpoint lastPoint = trackpoints.get(trackpoints.size() - 1);
        Trackpoint firstPoint = trackpoints.get(0);
        return lastPoint.getDistanceMeters() - firstPoint.getDistanceMeters();
    }

    /**
     * Υπολογίζει τη συνολική διάρκεια της διαδρομής σε δευτερόλεπτα.
     */
    public long getTotalDuration() {
        if (trackpoints.size() < 2) {
            return 0;
        }

        Trackpoint firstPoint = trackpoints.get(0);
        Trackpoint lastPoint = trackpoints.get(trackpoints.size() - 1);
        return java.time.Duration.between(firstPoint.getTime(), lastPoint.getTime()).getSeconds();
    }

    /**
     * Υπολογίζει τον μέσο καρδιακό παλμό της διαδρομής.
     */
    public double getAverageHeartRate() {
        int count = 0;
        int sum = 0;

        for (Trackpoint tp : trackpoints) {
            if (tp.getHeartRateBpm() != null) {
                sum += tp.getHeartRateBpm();
                count++;
            }
        }

        return count > 0 ? (double) sum / count : 0;
    }

    /**
     * Υπολογίζει τον μέγιστο καρδιακό παλμό της διαδρομής.
     */
    public int getMaxHeartRate() {
        int max = 0;

        for (Trackpoint tp : trackpoints) {
            if (tp.getHeartRateBpm() != null && tp.getHeartRateBpm() > max) {
                max = tp.getHeartRateBpm();
            }
        }

        return max;
    }

    /**
     * Υπολογίζει τον ελάχιστο καρδιακό παλμό της διαδρομής.
     */
    public int getMinHeartRate() {
        int min = Integer.MAX_VALUE;

        for (Trackpoint tp : trackpoints) {
            if (tp.getHeartRateBpm() != null && tp.getHeartRateBpm() < min) {
                min = tp.getHeartRateBpm();
            }
        }

        return min == Integer.MAX_VALUE ? 0 : min;
    }
}
