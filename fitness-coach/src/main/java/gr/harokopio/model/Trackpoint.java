package gr.harokopio.model;

import java.time.LocalDateTime;

/**
 * Αναπαριστά ένα σημείο μέτρησης σε μια αθλητική δραστηριότητα.
 * Περιέχει γεωγραφικές συντεταγμένες, υψόμετρο, απόσταση και άλλες μετρήσεις.
 */
public class Trackpoint {
    private LocalDateTime time;
    private double latitude;
    private double longitude;
    private double altitudeMeters;
    private double distanceMeters;
    private Integer heartRateBpm;
    private Integer cadence;

    public Trackpoint(LocalDateTime time, double latitude, double longitude,
                      double altitudeMeters, double distanceMeters) {
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitudeMeters = altitudeMeters;
        this.distanceMeters = distanceMeters;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitudeMeters() {
        return altitudeMeters;
    }

    public void setAltitudeMeters(double altitudeMeters) {
        this.altitudeMeters = altitudeMeters;
    }

    public double getDistanceMeters() {
        return distanceMeters;
    }

    public void setDistanceMeters(double distanceMeters) {
        this.distanceMeters = distanceMeters;
    }

    public Integer getHeartRateBpm() {
        return heartRateBpm;
    }

    public void setHeartRateBpm(Integer heartRateBpm) {
        this.heartRateBpm = heartRateBpm;
    }

    public Integer getCadence() {
        return cadence;
    }

    public void setCadence(Integer cadence) {
        this.cadence = cadence;
    }

    /**
     * Υπολογίζει την απόσταση από το προηγούμενο σημείο χρησιμοποιώντας τον τύπο Haversine.
     */
    public double calculateDistanceFrom(Trackpoint previous) {
        if (previous == null) {
            return 0;
        }

        final int R = 6371000; // Ακτίνα της Γης σε μέτρα

        double lat1 = Math.toRadians(previous.latitude);
        double lat2 = Math.toRadians(this.latitude);
        double deltaLat = Math.toRadians(this.latitude - previous.latitude);
        double deltaLon = Math.toRadians(this.longitude - previous.longitude);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    /**
     * Υπολογίζει τη χρονική διαφορά σε δευτερόλεπτα από το προηγούμενο σημείο.
     */
    public long calculateTimeFrom(Trackpoint previous) {
        if (previous == null) {
            return 0;
        }
        return java.time.Duration.between(previous.time, this.time).getSeconds();
    }
}
