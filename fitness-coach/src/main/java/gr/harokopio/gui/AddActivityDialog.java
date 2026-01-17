package gr.harokopio.gui;
import gr.harokopio.model.*;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class AddActivityDialog extends JDialog {
    private FitnessCoachGUI mainGUI;

    private JComboBox<String> sportTypeCombo;
    private JSpinner dateSpinner;
    private JSpinner hourSpinner;
    private JSpinner minuteSpinner;
    private JTextField durationField;
    private JTextField distanceField;
    private JTextField heartRateField;

    private boolean activityAdded = false;

    public AddActivityDialog(JFrame parent, FitnessCoachGUI mainGUI) {
        super(parent, "Add Manual Activity", true);
        this.mainGUI = mainGUI;

        initializeComponents();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initializeComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        mainPanel.add(createSportTypePanel());
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createDateTimePanel());
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createDurationPanel());
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createDistancePanel());
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createHeartRatePanel());

        add(mainPanel, BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createSportTypePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel("Activity Type:"));

        String[] sportTypes = {"Running", "Biking", "Walking", "Swimming"};
        sportTypeCombo = new JComboBox<>(sportTypes);
        panel.add(sportTypeCombo);

        return panel;
    }

    private JPanel createDateTimePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Start Date & Time:"), gbc);

        gbc.gridx = 1;
        LocalDateTime now = LocalDateTime.now();

        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);
        panel.add(dateSpinner, gbc);

        gbc.gridx = 2;
        hourSpinner = new JSpinner(new SpinnerNumberModel(now.getHour(), 0, 23, 1));
        panel.add(hourSpinner, gbc);

        gbc.gridx = 3;
        panel.add(new JLabel(":"), gbc);

        gbc.gridx = 4;
        minuteSpinner = new JSpinner(new SpinnerNumberModel(now.getMinute(), 0, 59, 1));
        panel.add(minuteSpinner, gbc);

        return panel;
    }

    private JPanel createDurationPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel("Duration (HH:MM:SS):"));

        durationField = new JTextField("00:45:00", 10);
        panel.add(durationField);

        return panel;
    }

    private JPanel createDistancePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel("Distance (km):"));

        distanceField = new JTextField("5.0", 10);
        panel.add(distanceField);

        return panel;
    }

    private JPanel createHeartRatePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel("Avg Heart Rate (bpm):"));

        heartRateField = new JTextField("", 10);
        JLabel optionalLabel = new JLabel("(optional)");
        optionalLabel.setForeground(Color.GRAY);
        panel.add(heartRateField);
        panel.add(optionalLabel);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        JButton addButton = new JButton("Add Activity");
        addButton.addActionListener(e -> onAddActivity());

        panel.add(cancelButton);
        panel.add(addButton);

        return panel;
    }

    private void onAddActivity() {
        try {
            if (!validateInput()) {
                return;
            }

            Activity activity = createActivity();
            mainGUI.addActivity(activity);

            activityAdded = true;
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error creating activity: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateInput() {
        if (durationField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Duration is required",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (distanceField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Distance is required",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            parseDuration(durationField.getText().trim());
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                    "Invalid duration format. Use HH:MM:SS",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            double distance = Double.parseDouble(distanceField.getText().trim());
            if (distance <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Distance must be greater than 0",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Invalid distance value",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!heartRateField.getText().trim().isEmpty()) {
            try {
                int hr = Integer.parseInt(heartRateField.getText().trim());
                if (hr <= 0 || hr > 250) {
                    JOptionPane.showMessageDialog(this,
                            "Heart rate must be between 1 and 250",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Invalid heart rate value",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        return true;
    }

    private Activity createActivity() {
        String sportType = (String) sportTypeCombo.getSelectedItem();
        long durationSeconds = parseDuration(durationField.getText().trim());
        double distanceKm = Double.parseDouble(distanceField.getText().trim());
        double distanceMeters = distanceKm * 1000.0;

        Integer heartRate = null;
        if (!heartRateField.getText().trim().isEmpty()) {
            heartRate = Integer.parseInt(heartRateField.getText().trim());
        }

        java.util.Date date = (java.util.Date) dateSpinner.getValue();
        int hour = (Integer) hourSpinner.getValue();
        int minute = (Integer) minuteSpinner.getValue();

        LocalDateTime startTime = LocalDateTime.ofInstant(date.toInstant(), java.time.ZoneId.systemDefault())
                .withHour(hour).withMinute(minute).withSecond(0);
        LocalDateTime endTime = startTime.plusSeconds(durationSeconds);

        Trackpoint startPoint = new Trackpoint(startTime, 0.0, 0.0, 0.0, 0.0);
        if (heartRate != null) {
            startPoint.setHeartRateBpm(heartRate);
        }

        Trackpoint endPoint = new Trackpoint(endTime, 0.0, 0.0, 0.0, distanceMeters);
        if (heartRate != null) {
            endPoint.setHeartRateBpm(heartRate);
        }

        Track track = new Track();
        track.addTrackpoint(startPoint);
        track.addTrackpoint(endPoint);

        Lap lap = new Lap(startTime);
        lap.addTrack(track);

        String id = startTime.format(DateTimeFormatter.ISO_DATE_TIME);
        Activity activity;
        switch (sportType) {
            case "Running":
                activity = new Running(id, startTime);
                break;
            case "Biking":
                activity = new Cycling(id, startTime);
                break;
            case "Walking":
                activity = new Walking(id, startTime);
                break;
            case "Swimming":
                activity = new Swimming(id, startTime);
                break;
            default:
                activity = new Running(id, startTime);
        }

        activity.addLap(lap);

        return activity;
    }

    private long parseDuration(String duration) throws IllegalArgumentException {
        String[] parts = duration.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid duration format");
        }

        try {
            long hours = Long.parseLong(parts[0]);
            long minutes = Long.parseLong(parts[1]);
            long seconds = Long.parseLong(parts[2]);

            if (minutes < 0 || minutes >= 60 || seconds < 0 || seconds >= 60) {
                throw new IllegalArgumentException("Invalid minutes or seconds");
            }

            return hours * 3600 + minutes * 60 + seconds;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number in duration");
        }
    }

    public boolean isActivityAdded() {
        return activityAdded;
    }
}

