package gr.harokopio.gui;
import gr.harokopio.model.Activity;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

public class ActivitiesPanel extends JPanel{
    private FitnessCoachGUI mainGUI;

    private JTable activitiesTable;
    private DefaultTableModel tableModel;
    private JPanel statsPanel;
    private JPanel summaryPanel;

    private DecimalFormat df = new DecimalFormat("#.##");
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public ActivitiesPanel(FitnessCoachGUI mainGUI) {
        this.mainGUI = mainGUI;
        initializeComponents();
    }
    private void initializeComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createButtonPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        statsPanel = createStatsPanel();
        summaryPanel = createSummaryPanel();

        rightPanel.add(statsPanel, BorderLayout.NORTH);
        rightPanel.add(summaryPanel, BorderLayout.CENTER);

        add(rightPanel, BorderLayout.EAST);
    }
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton loadButton = new JButton("Load TCX Files");
        loadButton.addActionListener(e -> mainGUI.loadTCXFiles());

        JButton addButton = new JButton("Add Activity");
        addButton.addActionListener(e -> mainGUI.addManualActivity());

        JButton clearButton = new JButton("Clear All");
        clearButton.addActionListener(e -> mainGUI.clearActivities());

        panel.add(loadButton);
        panel.add(addButton);
        panel.add(clearButton);

        return panel;
    }
    private JScrollPane createTablePanel() {
        String[] columnNames = {"Sport", "Date", "Time", "Duration", "Distance", "Avg HR"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        activitiesTable = new JTable(tableModel);
        activitiesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        activitiesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                displaySelectedActivityStats();
            }
        });

        return new JScrollPane(activitiesTable);
    }
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Selected Activity"));
        panel.setPreferredSize(new Dimension(300, 250));

        JTextArea statsArea = new JTextArea();
        statsArea.setEditable(false);
        statsArea.setText("Select an activity to view details");
        statsArea.setName("statsArea");

        panel.add(new JScrollPane(statsArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Overall Summary"));
        panel.setPreferredSize(new Dimension(300, 200));

        JTextArea summaryArea = new JTextArea();
        summaryArea.setEditable(false);
        summaryArea.setText("No activities loaded");
        summaryArea.setName("summaryArea");

        panel.add(new JScrollPane(summaryArea), BorderLayout.CENTER);
        return panel;
    }
    public void refresh() {
        refreshTable();
        displayOverallStats();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);

        for (Activity activity : mainGUI.getActivities()) {
            Object[] row = new Object[6];
            row[0] = activity.getSportType();
            row[1] = activity.getStartTime().format(dateFormatter);
            row[2] = activity.getStartTime().format(timeFormatter);
            row[3] = formatDuration(activity.getTotalDuration());
            row[4] = activity.hasDistance() ?
                    df.format(activity.getTotalDistance() / 1000.0) + " km" : "N/A";
            row[5] = activity.getAverageHeartRate() > 0 ?
                    String.format("%.0f bpm", activity.getAverageHeartRate()) : "N/A";

            tableModel.addRow(row);
        }
    }

    private void displaySelectedActivityStats() {
        int selectedRow = activitiesTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        Activity activity = mainGUI.getActivities().get(selectedRow);

        StringBuilder sb = new StringBuilder();
        sb.append("Activity: ").append(activity.getSportType()).append("\n\n");
        sb.append("Total Time: ").append(formatDuration(activity.getTotalDuration())).append("\n");

        if (activity.hasDistance()) {
            sb.append("Total Distance: ").append(df.format(activity.getTotalDistance() / 1000.0)).append(" km\n");
            sb.append("Avg Speed: ").append(df.format(activity.getAverageSpeed())).append(" km/h\n");
            sb.append("Avg Pace: ").append(df.format(activity.getAveragePace())).append(" min/km\n");
        }

        double avgHR = activity.getAverageHeartRate();
        if (avgHR > 0) {
            sb.append("Avg Heart Rate: ").append(String.format("%.0f", avgHR)).append(" bpm\n");
        }

        int maxHR = activity.getMaxHeartRate();
        if (maxHR > 0) {
            sb.append("Max Heart Rate: ").append(maxHR).append(" bpm\n");
        }

        double calories = calculateCalories(activity);
        if (calories > 0) {
            sb.append("\nCalories: ").append(String.format("%.0f", calories)).append(" kcal");
            sb.append(mainGUI.isUseAdvancedCalories() ? " (advanced)" : " (simple)");
        }

        JTextArea statsArea = findTextAreaByName(statsPanel, "statsArea");
        if (statsArea != null) {
            statsArea.setText(sb.toString());
        }
    }

    private void displayOverallStats() {
        if (mainGUI.getActivities().isEmpty()) {
            JTextArea summaryArea = findTextAreaByName(summaryPanel, "summaryArea");
            if (summaryArea != null) {
                summaryArea.setText("No activities loaded");
            }
            return;
        }

        long totalDuration = 0;
        double totalDistance = 0;
        double totalCalories = 0;
        int activitiesWithDistance = 0;

        for (Activity activity : mainGUI.getActivities()) {
            totalDuration += activity.getTotalDuration();
            if (activity.hasDistance()) {
                totalDistance += activity.getTotalDistance();
                activitiesWithDistance++;
            }
            totalCalories += calculateCalories(activity);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Total Activities: ").append(mainGUI.getActivities().size()).append("\n\n");
        sb.append("Total Time: ").append(formatDuration(totalDuration)).append("\n");

        if (activitiesWithDistance > 0) {
            sb.append("Total Distance: ").append(df.format(totalDistance / 1000.0)).append(" km\n");
        }

        if (totalCalories > 0) {
            sb.append("Total Calories: ").append(String.format("%.0f", totalCalories)).append(" kcal");
        }

        JTextArea summaryArea = findTextAreaByName(summaryPanel, "summaryArea");
        if (summaryArea != null) {
            summaryArea.setText(sb.toString());
        }
    }

    private double calculateCalories(Activity activity) {
        if (mainGUI.isUseAdvancedCalories() && activity.getAverageHeartRate() > 0) {
            return activity.calculateCaloriesAdvanced(
                    mainGUI.getUserProfile().getWeight(),
                    mainGUI.getUserProfile().getAge(),
                    mainGUI.getUserProfile().isMale()
            );
        } else {
            return activity.calculateCaloriesSimple(mainGUI.getUserProfile().getWeight());
        }
    }

    private String formatDuration(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, secs);
        } else {
            return String.format("%d:%02d", minutes, secs);
        }
    }

    private JTextArea findTextAreaByName(JPanel panel, String name) {
        Component[] components = ((JScrollPane) panel.getComponent(0)).getViewport().getComponents();
        for (Component comp : components) {
            if (comp instanceof JTextArea && name.equals(comp.getName())) {
                return (JTextArea) comp;
            }
        }
        return null;
    }
}

