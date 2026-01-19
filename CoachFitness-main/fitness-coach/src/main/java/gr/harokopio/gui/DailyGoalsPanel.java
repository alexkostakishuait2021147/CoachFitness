//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package gr.harokopio.gui;

import gr.harokopio.model.Activity;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class DailyGoalsPanel extends JPanel {
    private FitnessCoachGUI mainGUI;
    private JTextField goalField;
    private JButton setGoalButton;
    private JTable dailyTable;
    private DefaultTableModel tableModel;
    private JTextArea summaryArea;
    private int dailyGoal = 500;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public DailyGoalsPanel(FitnessCoachGUI mainGUI) {
        this.mainGUI = mainGUI;
        this.initializeComponents();
    }

    private void initializeComponents() {
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.add(this.createGoalInputPanel(), "North");
        this.add(this.createTablePanel(), "Center");
        this.add(this.createSummaryPanel(), "South");
    }

    private JPanel createGoalInputPanel() {
        JPanel panel = new JPanel(new FlowLayout(0));
        panel.setBorder(BorderFactory.createTitledBorder("Daily Calorie Goal"));
        panel.add(new JLabel("Goal:"));
        this.goalField = new JTextField(String.valueOf(this.dailyGoal), 8);
        panel.add(this.goalField);
        panel.add(new JLabel("kcal/day"));
        this.setGoalButton = new JButton("Set Goal");
        this.setGoalButton.addActionListener((e) -> this.onSetGoal());
        panel.add(this.setGoalButton);
        return panel;
    }

    private JScrollPane createTablePanel() {
        String[] columnNames = new String[]{"Date", "Calories", "Goal", "Status"};
        this.tableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.dailyTable = new JTable(this.tableModel);
        return new JScrollPane(this.dailyTable);
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Summary"));
        panel.setPreferredSize(new Dimension(0, 150));
        this.summaryArea = new JTextArea();
        this.summaryArea.setEditable(false);
        this.summaryArea.setText("Set a daily goal and load activities to see your progress");
        panel.add(new JScrollPane(this.summaryArea), "Center");
        return panel;
    }

    private void onSetGoal() {
        try {
            int goal = Integer.parseInt(this.goalField.getText().trim());
            if (goal <= 0) {
                JOptionPane.showMessageDialog(this, "Goal must be greater than 0", "Validation Error", 0);
                return;
            }

            this.dailyGoal = goal;
            this.refresh();
            JOptionPane.showMessageDialog(this, "Daily goal set to " + this.dailyGoal + " kcal", "Success", 1);
        } catch (NumberFormatException var2) {
            JOptionPane.showMessageDialog(this, "Invalid goal value. Please enter a number.", "Validation Error", 0);
        }

    }

    public void refresh() {
        this.refreshDailyBreakdown();
        this.calculateDailySummary();
    }

    private void refreshDailyBreakdown() {
        this.tableModel.setRowCount(0);
        Map<LocalDate, Double> dailyCalories = this.groupActivitiesByDate();
        List<LocalDate> sortedDates = new ArrayList(dailyCalories.keySet());
        Collections.sort(sortedDates);

        for(LocalDate date : sortedDates) {
            double calories = (Double)dailyCalories.get(date);
            Object[] row = new Object[4];
            row[0] = date.format(this.dateFormatter);
            row[1] = String.format("%.0f", calories);
            row[2] = this.dailyGoal;
            if (calories >= (double)this.dailyGoal) {
                row[3] = "✓ Met";
            } else if (date.equals(LocalDate.now())) {
                int remaining = this.dailyGoal - (int)calories;
                row[3] = "-" + remaining;
            } else {
                row[3] = "✗ Miss";
            }

            this.tableModel.addRow(row);
        }

    }

    private void calculateDailySummary() {
        if (this.mainGUI.getActivities().isEmpty()) {
            this.summaryArea.setText("No activities loaded");
        } else {
            Map<LocalDate, Double> dailyCalories = this.groupActivitiesByDate();
            int totalDays = dailyCalories.size();
            int daysMet = 0;
            int daysMissed = 0;
            double todayCalories = (double)0.0F;
            LocalDate today = LocalDate.now();

            for(Map.Entry<LocalDate, Double> entry : dailyCalories.entrySet()) {
                LocalDate date = (LocalDate)entry.getKey();
                double calories = (Double)entry.getValue();
                if (date.equals(today)) {
                    todayCalories = calories;
                }

                if (calories >= (double)this.dailyGoal) {
                    ++daysMet;
                } else if (!date.equals(today)) {
                    ++daysMissed;
                }
            }

            StringBuilder sb = new StringBuilder();
            sb.append("Total Days: ").append(totalDays).append("\n");
            sb.append("Days Goal Met: ").append(daysMet);
            if (totalDays > 0) {
                sb.append(String.format(" (%.0f%%)", (double)daysMet * (double)100.0F / (double)totalDays));
            }

            sb.append("\n");
            sb.append("Days Missed: ").append(daysMissed);
            if (totalDays > 0) {
                sb.append(String.format(" (%.0f%%)", (double)daysMissed * (double)100.0F / (double)totalDays));
            }

            sb.append("\n\n");
            if (dailyCalories.containsKey(today)) {
                double percentage = todayCalories / (double)this.dailyGoal * (double)100.0F;
                sb.append(String.format("Today's Progress: %.0f/%d (%.0f%%)\n", todayCalories, this.dailyGoal, percentage));
                int remaining = this.dailyGoal - (int)todayCalories;
                if (remaining > 0) {
                    sb.append("Remaining Today: ").append(remaining).append(" kcal");
                } else {
                    sb.append("Today's goal achieved!");
                }
            } else {
                sb.append("No activities logged today\n");
                sb.append("Remaining Today: ").append(this.dailyGoal).append(" kcal");
            }

            this.summaryArea.setText(sb.toString());
        }
    }

    private Map<LocalDate, Double> groupActivitiesByDate() {
        Map<LocalDate, Double> dailyCalories = new HashMap();

        for(Activity activity : this.mainGUI.getActivities()) {
            LocalDate date = activity.getStartTime().toLocalDate();
            double calories = this.calculateCalories(activity);
            dailyCalories.put(date, (Double)dailyCalories.getOrDefault(date, (double)0.0F) + calories);
        }

        return dailyCalories;
    }

    private double calculateCalories(Activity activity) {
        return this.mainGUI.isUseAdvancedCalories() && activity.getAverageHeartRate() > (double)0.0F ? activity.calculateCaloriesAdvanced(this.mainGUI.getUserProfile().getWeight(), this.mainGUI.getUserProfile().getAge(), this.mainGUI.getUserProfile().isMale()) : activity.calculateCaloriesSimple(this.mainGUI.getUserProfile().getWeight());
    }
}
