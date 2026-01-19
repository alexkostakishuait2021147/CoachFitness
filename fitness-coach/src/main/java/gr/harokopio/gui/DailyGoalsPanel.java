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
import java.util.Map;
import java.util.Objects;
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

    public DailyGoalsPanel(FitnessCoachGUI var1) {
        this.mainGUI = var1;
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
        JPanel var1 = new JPanel(new FlowLayout(0));
        var1.setBorder(BorderFactory.createTitledBorder("Daily Calorie Goal"));
        var1.add(new JLabel("Goal:"));
        this.goalField = new JTextField(String.valueOf(this.dailyGoal), 8);
        var1.add(this.goalField);
        var1.add(new JLabel("kcal/day"));
        this.setGoalButton = new JButton("Set Goal");
        this.setGoalButton.addActionListener((var1x) -> this.onSetGoal());
        var1.add(this.setGoalButton);
        return var1;
    }

    private JScrollPane createTablePanel() {
        String[] var1 = new String[]{"Date", "Calories", "Goal", "Status"};
        this.tableModel = new DefaultTableModel(var1, 0) {
            {
                Objects.requireNonNull(DailyGoalsPanel.this);
            }

            public boolean isCellEditable(int var1, int var2) {
                return false;
            }
        };
        this.dailyTable = new JTable(this.tableModel);
        return new JScrollPane(this.dailyTable);
    }

    private JPanel createSummaryPanel() {
        JPanel var1 = new JPanel(new BorderLayout());
        var1.setBorder(BorderFactory.createTitledBorder("Summary"));
        var1.setPreferredSize(new Dimension(0, 150));
        this.summaryArea = new JTextArea();
        this.summaryArea.setEditable(false);
        this.summaryArea.setText("Set a daily goal and load activities to see your progress");
        var1.add(new JScrollPane(this.summaryArea), "Center");
        return var1;
    }

    private void onSetGoal() {
        try {
            int var1 = Integer.parseInt(this.goalField.getText().trim());
            if (var1 <= 0) {
                JOptionPane.showMessageDialog(this, "Goal must be greater than 0", "Validation Error", 0);
                return;
            }

            this.dailyGoal = var1;
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
        Map<LocalDate, Double> var1 = this.groupActivitiesByDate();
        ArrayList<LocalDate> var2 = new ArrayList<>(var1.keySet());
        Collections.sort(var2);

        for(LocalDate var4 : var2) {
            double var5 = (Double)var1.get(var4);
            Object[] var7 = new Object[]{var4.format(this.dateFormatter), String.format("%.0f", var5), this.dailyGoal, null};
            if (var5 >= (double)this.dailyGoal) {
                var7[3] = "✓ Met";
            } else if (var4.equals(LocalDate.now())) {
                int var8 = this.dailyGoal - (int)var5;
                var7[3] = "-" + var8;
            } else {
                var7[3] = "✗ Miss";
            }

            this.tableModel.addRow(var7);
        }

    }

    private void calculateDailySummary() {
        if (this.mainGUI.getActivities().isEmpty()) {
            this.summaryArea.setText("No activities loaded");
        } else {
            Map<LocalDate, Double> var1 = this.groupActivitiesByDate();            int var2 = var1.size();
            int var3 = 0;
            int var4 = 0;
            double var5 = (double)0.0F;
            LocalDate var7 = LocalDate.now();

            for(Map.Entry var9 : var1.entrySet()) {
                LocalDate var10 = (LocalDate)var9.getKey();
                double var11 = (Double)var9.getValue();
                if (var10.equals(var7)) {
                    var5 = var11;
                }

                if (var11 >= (double)this.dailyGoal) {
                    ++var3;
                } else if (!var10.equals(var7)) {
                    ++var4;
                }
            }

            StringBuilder var13 = new StringBuilder();
            var13.append("Total Days: ").append(var2).append("\n");
            var13.append("Days Goal Met: ").append(var3);
            if (var2 > 0) {
                var13.append(String.format(" (%.0f%%)", (double)var3 * (double)100.0F / (double)var2));
            }

            var13.append("\n");
            var13.append("Days Missed: ").append(var4);
            if (var2 > 0) {
                var13.append(String.format(" (%.0f%%)", (double)var4 * (double)100.0F / (double)var2));
            }

            var13.append("\n\n");
            if (var1.containsKey(var7)) {
                double var14 = var5 / (double)this.dailyGoal * (double)100.0F;
                var13.append(String.format("Today's Progress: %.0f/%d (%.0f%%)\n", var5, this.dailyGoal, var14));
                int var15 = this.dailyGoal - (int)var5;
                if (var15 > 0) {
                    var13.append("Remaining Today: ").append(var15).append(" kcal");
                } else {
                    var13.append("Today's goal achieved!");
                }
            } else {
                var13.append("No activities logged today\n");
                var13.append("Remaining Today: ").append(this.dailyGoal).append(" kcal");
            }

            this.summaryArea.setText(var13.toString());
        }
    }

    private Map<LocalDate, Double> groupActivitiesByDate() {
        HashMap var1 = new HashMap();

        for(Activity var3 : this.mainGUI.getActivities()) {
            LocalDate var4 = var3.getStartTime().toLocalDate();
            double var5 = this.calculateCalories(var3);
            var1.put(var4, (Double)var1.getOrDefault(var4, (double)0.0F) + var5);
        }

        return var1;
    }

    private double calculateCalories(Activity var1) {
        return this.mainGUI.isUseAdvancedCalories() && var1.getAverageHeartRate() > (double)0.0F ? var1.calculateCaloriesAdvanced(this.mainGUI.getUserProfile().getWeight(), this.mainGUI.getUserProfile().getAge(), this.mainGUI.getUserProfile().isMale()) : var1.calculateCaloriesSimple(this.mainGUI.getUserProfile().getWeight());
    }
}
