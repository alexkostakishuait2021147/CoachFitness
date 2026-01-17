package gr.harokopio;

import gr.harokopio.model.*;
import gr.harokopio.parser.TCXParser;

import java.util.ArrayList;
import java.util.List;
public class FitnessCoach {

    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }

        // Ανάλυση των command-line arguments
        List<String> tcxFiles = new ArrayList<>();
        UserProfile userProfile = new UserProfile();
        boolean hasWeight = false;
        boolean hasAge = false;
        boolean hasGender = false;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (arg.equals("-w") || arg.equals("--weight")) {
                if (i + 1 < args.length) {
                    try {
                        userProfile.setWeight(Double.parseDouble(args[++i]));
                        hasWeight = true;
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid weight value: " + args[i]);

                    }
                }
            } else if (arg.equals("-a") || arg.equals("--age")) {
                if (i + 1 < args.length) {
                    try {
                        userProfile.setAge(Integer.parseInt(args[++i]));
                        hasAge = true;
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid age value: " + args[i]);

                    }
                }
            } else if (arg.equals("-g") || arg.equals("--gender")) {
                if (i + 1 < args.length) {
                    String gender = args[++i].toLowerCase();
                    if (gender.equals("male") || gender.equals("m")) {
                        userProfile.setMale(true);
                        hasGender = true;
                    } else if (gender.equals("female") || gender.equals("f")) {
                        userProfile.setMale(false);
                        hasGender = true;
                    } else {
                        System.err.println("Invalid gender value. Use 'male'/'m' or 'female'/'f'");
                        return;
                    }
                }
            } else if (arg.endsWith(".tcx")) {
                tcxFiles.add(arg);
            }
        }

        if (tcxFiles.isEmpty()) {
            System.err.println("Error: No .tcx files provided!");
            printUsage();

        }


        TCXParser parser = new TCXParser();
        List<Activity> allActivities = new ArrayList<>();

        for (
                String file : tcxFiles) {
            try {
                List<Activity> activities = parser.parse(file);
                allActivities.addAll(activities);
                System.out.println("Loaded " + activities.size() + " activity(ies) from: " + file);
            } catch (Exception e) {
                System.err.println("Error parsing file " + file + ": " + e.getMessage());
            }
        }

        if (allActivities.isEmpty()) {
            System.err.println("No activities found in the provided files.");

        }
        System.out.println("FITNESS COACH - ACTIVITY STATISTICS");

        // Εκτύπωση στατιστικών για κάθε δραστηριότητα
        for (
                Activity activity : allActivities) {
            printActivityStatistics(activity, userProfile, hasWeight, hasAge, hasGender);
            System.out.println();
        }

        // Εκτύπωση συνολικών στατιστικών αν υπάρχουν περισσότερες από μία δραστηριότητες
        if (allActivities.size() > 1) {
            printSummaryStatistics(allActivities, userProfile, hasWeight, hasAge, hasGender);
        }

    }

    /**
     * Εκτυπώνει τα στατιστικά μιας δραστηριότητας.
     */
    private static void printActivityStatistics(Activity activity, UserProfile profile, boolean hasWeight, boolean hasAge, boolean hasGender) {
        System.out.println("Activity: " + activity.getSportType());

        long totalSeconds = activity.getTotalDuration();
        System.out.println("Total Time: " + formatDuration(totalSeconds));

        if (activity.hasDistance()) {
            double distanceKm = activity.getTotalDistance() / 1000.0;
            System.out.printf("Total Distance: %.2f km%n", distanceKm);

            double avgSpeed = activity.getAverageSpeed();
            System.out.printf("Avg Speed: %.2f km/h%n", avgSpeed);

            double avgPace = activity.getAveragePace();
            System.out.printf("Avg Pace: %.2f min/km%n", avgPace);
        }

        double avgHR = activity.getAverageHeartRate();
        if (avgHR > 0) {
            System.out.printf("Avg Heart Rate: %.0f bpm%n", avgHR);
        }

        int maxHR = activity.getMaxHeartRate();
        if (maxHR > 0) {
            System.out.printf("Max Heart Rate: %d bpm%n", maxHR);
        }

        // Υπολογισμός θερμίδων αν υπάρχει βάρος
        if (hasWeight) {
            double calories;
            if (hasAge && hasGender && avgHR > 0) {
                // Χρήση προχωρημένης μεθόδου αν υπάρχουν όλα τα δεδομένα
                calories = activity.calculateCaloriesAdvanced(
                        profile.getWeight(),
                        profile.getAge(),
                        profile.isMale()
                );
                System.out.printf("Calories Burned: %.0f kcal (advanced formula)%n", calories);
            } else {
                // Χρήση απλής μεθόδου
                calories = activity.calculateCaloriesSimple(profile.getWeight());
                System.out.printf("Calories Burned: %.0f kcal (simple formula)%n", calories);
            }
        }
    }

    private static void printSummaryStatistics(List<Activity> activities, UserProfile profile,
                                               boolean hasWeight, boolean hasAge, boolean hasGender) {
        System.out.println("=".repeat(60));
        System.out.println("SUMMARY - ALL ACTIVITIES");
        System.out.println("=".repeat(60));

        long totalDuration = 0;
        double totalDistance = 0;
        double totalCalories = 0;
        int activitiesWithDistance = 0;

        for (Activity activity : activities) {
            totalDuration += activity.getTotalDuration();
            if (activity.hasDistance()) {
                totalDistance += activity.getTotalDistance();
                activitiesWithDistance++;
            }
            if (hasWeight) {
                if (hasAge && hasGender && activity.getAverageHeartRate() > 0) {
                    totalCalories += activity.calculateCaloriesAdvanced(
                            profile.getWeight(), profile.getAge(), profile.isMale()
                    );
                } else {
                    totalCalories += activity.calculateCaloriesSimple(profile.getWeight());
                }
            }
        }

        System.out.println("Total Activities: " + activities.size());
        System.out.println("Total Time: " + formatDuration(totalDuration));

        if (activitiesWithDistance > 0) {
            System.out.printf("Total Distance: %.2f km%n", totalDistance / 1000.0);
        }

        if (hasWeight) {
            System.out.printf("Total Calories Burned: %.0f kcal%n", totalCalories);
        }

        System.out.println();
    }

    private static String formatDuration(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, secs);
        } else {
            return String.format("%d:%02d", minutes, secs);
        }
    }

    private static void printUsage() {
        System.out.println("Usage: java -jar coach.jar [OPTIONS] <file1.tcx> [file2.tcx] ...");
        System.out.println("\nOptions:");
        System.out.println("  -w, --weight <kg>        User weight in kilograms");
        System.out.println("  -a, --age <years>        User age in years");
        System.out.println("  -g, --gender <m/f>       User gender (male/m or female/f)");
        System.out.println("\nExample:");
        System.out.println("  java -jar coach.jar run1.tcx");
        System.out.println("  java -jar coach.jar -w 70 run1.tcx run2.tcx");
        System.out.println("  java -jar coach.jar -w 65 -a 25 -g f activity.tcx");
    }

//test

}
