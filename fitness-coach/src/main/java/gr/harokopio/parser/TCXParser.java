package gr.harokopio.parser;
import gr.harokopio.model.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
public class TCXParser {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    public List<Activity> parse(String filePath) throws Exception {
        List<Activity> activities = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new FileInputStream(filePath));
        doc.getDocumentElement().normalize();

        NodeList activityList = doc.getElementsByTagName("Activity");

        for (int i = 0; i < activityList.getLength(); i++) {
            Element activityElement = (Element) activityList.item(i);
            Activity activity = parseActivity(activityElement);
            if (activity != null) {
                activities.add(activity);
            }
        }

        return activities;
    }
    private Activity parseActivity(Element activityElement) {
        try {
            String sport = activityElement.getAttribute("Sport");
            String idStr = getElementTextContent(activityElement, "Id");
            LocalDateTime startTime = LocalDateTime.parse(idStr, ISO_FORMATTER);

            Activity activity = createActivityBySport(sport, idStr, startTime);

            NodeList lapList = activityElement.getElementsByTagName("Lap");
            for (int i = 0; i < lapList.getLength(); i++) {
                Element lapElement = (Element) lapList.item(i);
                Lap lap = parseLap(lapElement);
                if (lap != null) {
                    activity.addLap(lap);
                }
            }

            return activity;
        } catch (Exception e) {
            System.err.println("Error parsing activity: " + e.getMessage());
            return null;
        }
    }
    private Activity createActivityBySport(String sport, String id, LocalDateTime startTime) {
        switch (sport.toLowerCase()) {
            case "running":
                return new Running(id, startTime);
            case "biking":
            case "cycling":
                return new Cycling(id, startTime);
            case "walking":
                return new Walking(id, startTime);
            case "swimming":
                return new Swimming(id, startTime);
            default:
                // Default σε Running αν δεν αναγνωρίζεται ο τύπος
                return new Running(id, startTime);
        }
    }
}
