package gr.harokopio.gui;

import gr.harokopio.model.Activity;
import gr.harokopio.model.UserProfile;
import gr.harokopio.parser.TCXParser;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FitnessCoachGUI {
    private JFrame mainFrame;
    private JTabbedPane tabbedPane;

    private ActivitiesPanel activitiesPanel;
    private UserProfilePanel userProfilePanel;
    private DailyGoalsPanel dailyGoalsPanel;

    private List<Activity> activities;
    private UserProfile userProfile;
    private boolean useAdvancedCalories;

    public FitnessCoachGUI() {
        activities = new ArrayList<>();
        userProfile = new UserProfile();
        useAdvancedCalories = false;

        initializeGUI();
    }

    private void initializeGUI() {
        mainFrame = new JFrame("Fitness Coach");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(900, 700);
        mainFrame.setLocationRelativeTo(null);

        createMenuBar();

        tabbedPane = new JTabbedPane();

        activitiesPanel = new ActivitiesPanel(this);
        userProfilePanel = new UserProfilePanel(this);
        dailyGoalsPanel = new DailyGoalsPanel(this);

        tabbedPane.addTab("Activities", activitiesPanel);
        tabbedPane.addTab("User Profile", userProfilePanel);
        tabbedPane.addTab("Daily Goals", dailyGoalsPanel);

        mainFrame.add(tabbedPane, BorderLayout.CENTER);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");

        JMenuItem loadTCXItem = new JMenuItem("Load TCX Files...");
        loadTCXItem.addActionListener(e -> loadTCXFiles());

        JMenuItem addActivityItem = new JMenuItem("Add Activity...");
        addActivityItem.addActionListener(e -> addManualActivity());

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(loadTCXItem);
        fileMenu.add(addActivityItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        mainFrame.setJMenuBar(menuBar);
    }

    public void loadTCXFiles() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("TCX Files", "tcx"));
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

        int result = fileChooser.showOpenDialog(mainFrame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File[] selectedFiles = fileChooser.getSelectedFiles();

            TCXParser parser = new TCXParser();
            for (File file : selectedFiles) {
                try {
                    List<Activity> parsedActivities = parser.parse(file.getAbsolutePath());
                    if (parsedActivities != null && !parsedActivities.isEmpty()) {
                        activities.addAll(parsedActivities);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(mainFrame,
                            "Error loading file: " + file.getName() + "\n" + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

            refreshAllPanels();
        }
    }

    public void addManualActivity() {
        AddActivityDialog dialog = new AddActivityDialog(mainFrame, this);
        dialog.setVisible(true);
    }

    public void addActivity(Activity activity) {
        activities.add(activity);
        refreshAllPanels();
    }

    public void clearActivities() {
        int confirm = JOptionPane.showConfirmDialog(mainFrame,
                "Are you sure you want to clear all activities?",
                "Confirm Clear",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            activities.clear();
            refreshAllPanels();
        }
    }

    public void refreshAllPanels() {
        SwingUtilities.invokeLater(() -> {
            activitiesPanel.refresh();
            dailyGoalsPanel.refresh();
        });
    }

    public void onProfileUpdated() {
        refreshAllPanels();
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(mainFrame,
                "Fitness Coach v1.0\n\n" +
                        "A fitness activity tracking application\n" +
                        "Harokopio University - OOP2 Project\n\n" +
                        "Built with Java Swing",
                "About Fitness Coach",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void show() {
        mainFrame.setVisible(true);
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public boolean isUseAdvancedCalories() {
        return useAdvancedCalories;
    }

    public void setUseAdvancedCalories(boolean useAdvancedCalories) {
        this.useAdvancedCalories = useAdvancedCalories;
    }

    public JFrame getMainFrame() {
        return mainFrame;
    }
}

