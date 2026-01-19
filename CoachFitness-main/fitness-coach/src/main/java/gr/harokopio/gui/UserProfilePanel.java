//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package gr.harokopio.gui;

import gr.harokopio.model.UserProfile;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

public class UserProfilePanel extends JPanel {
    private FitnessCoachGUI mainGUI;
    private JTextField weightField;
    private JSpinner ageSpinner;
    private JRadioButton maleRadio;
    private JRadioButton femaleRadio;
    private JRadioButton simpleMethodRadio;
    private JRadioButton advancedMethodRadio;
    private JButton saveButton;

    public UserProfilePanel(FitnessCoachGUI var1) {
        this.mainGUI = var1;
        this.initializeComponents();
        this.loadProfile();
    }

    private void initializeComponents() {
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JPanel var1 = new JPanel();
        var1.setLayout(new BoxLayout(var1, 1));
        var1.add(this.createPersonalInfoPanel());
        var1.add(Box.createVerticalStrut(20));
        var1.add(this.createCalorieMethodPanel());
        var1.add(Box.createVerticalStrut(20));
        var1.add(this.createButtonPanel());
        var1.add(Box.createVerticalStrut(20));
        var1.add(this.createNotePanel());
        this.add(var1, "North");
    }

    private JPanel createPersonalInfoPanel() {
        JPanel var1 = new JPanel();
        var1.setBorder(BorderFactory.createTitledBorder("Personal Information"));
        var1.setLayout(new GridBagLayout());
        GridBagConstraints var2 = new GridBagConstraints();
        var2.insets = new Insets(5, 5, 5, 5);
        var2.anchor = 17;
        var2.gridx = 0;
        var2.gridy = 0;
        var1.add(new JLabel("Weight (kg):"), var2);
        var2.gridx = 1;
        this.weightField = new JTextField("70.0", 10);
        var1.add(this.weightField, var2);
        var2.gridx = 0;
        var2.gridy = 1;
        var1.add(new JLabel("Age (years):"), var2);
        var2.gridx = 1;
        this.ageSpinner = new JSpinner(new SpinnerNumberModel(30, 1, 120, 1));
        var1.add(this.ageSpinner, var2);
        var2.gridx = 0;
        var2.gridy = 2;
        var1.add(new JLabel("Gender:"), var2);
        var2.gridx = 1;
        JPanel var3 = new JPanel(new FlowLayout(0));
        this.maleRadio = new JRadioButton("Male", true);
        this.femaleRadio = new JRadioButton("Female");
        ButtonGroup var4 = new ButtonGroup();
        var4.add(this.maleRadio);
        var4.add(this.femaleRadio);
        var3.add(this.maleRadio);
        var3.add(this.femaleRadio);
        var1.add(var3, var2);
        return var1;
    }

    private JPanel createCalorieMethodPanel() {
        JPanel var1 = new JPanel();
        var1.setBorder(BorderFactory.createTitledBorder("Calorie Calculation Method"));
        var1.setLayout(new BoxLayout(var1, 1));
        this.simpleMethodRadio = new JRadioButton("Simple (μ × weight × time)", true);
        this.advancedMethodRadio = new JRadioButton("Advanced (heart rate formula)");
        ButtonGroup var2 = new ButtonGroup();
        var2.add(this.simpleMethodRadio);
        var2.add(this.advancedMethodRadio);
        var1.add(this.simpleMethodRadio);
        var1.add(this.advancedMethodRadio);
        return var1;
    }

    private JPanel createButtonPanel() {
        JPanel var1 = new JPanel(new FlowLayout(1));
        this.saveButton = new JButton("Save Profile");
        this.saveButton.addActionListener((var1x) -> this.saveProfile());
        var1.add(this.saveButton);
        return var1;
    }

    private JPanel createNotePanel() {
        JPanel var1 = new JPanel(new BorderLayout());
        JLabel var2 = new JLabel("<html><i>Note: Profile settings are used for calorie calculations in the Activities tab.<br>Changes will be applied to all activities when you click Save Profile.</i></html>");
        var2.setForeground(Color.DARK_GRAY);
        var1.add(var2, "Center");
        return var1;
    }

    private void loadProfile() {
        UserProfile var1 = this.mainGUI.getUserProfile();
        this.weightField.setText(String.valueOf(var1.getWeight()));
        this.ageSpinner.setValue(var1.getAge());
        this.maleRadio.setSelected(var1.isMale());
        this.femaleRadio.setSelected(!var1.isMale());
        this.simpleMethodRadio.setSelected(!this.mainGUI.isUseAdvancedCalories());
        this.advancedMethodRadio.setSelected(this.mainGUI.isUseAdvancedCalories());
    }

    private void saveProfile() {
        try {
            double var1 = Double.parseDouble(this.weightField.getText().trim());
            int var3 = (Integer)this.ageSpinner.getValue();
            if (var1 <= (double)0.0F) {
                JOptionPane.showMessageDialog(this, "Weight must be greater than 0", "Validation Error", 0);
                return;
            }

            if (var3 < 1 || var3 > 120) {
                JOptionPane.showMessageDialog(this, "Age must be between 1 and 120", "Validation Error", 0);
                return;
            }

            UserProfile var4 = this.mainGUI.getUserProfile();
            var4.setWeight(var1);
            var4.setAge(var3);
            var4.setMale(this.maleRadio.isSelected());
            this.mainGUI.setUseAdvancedCalories(this.advancedMethodRadio.isSelected());
            this.mainGUI.onProfileUpdated();
            JOptionPane.showMessageDialog(this, "Profile saved successfully!", "Success", 1);
        } catch (NumberFormatException var5) {
            JOptionPane.showMessageDialog(this, "Invalid weight value. Please enter a valid number.", "Validation Error", 0);
        }

    }
}
