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
    private Lap parseLap(Element lapElement) {
        try {
            String startTimeStr = lapElement.getAttribute("StartTime");
            LocalDateTime startTime = LocalDateTime.parse(startTimeStr, ISO_FORMATTER);
            Lap lap = new Lap(startTime);

            NodeList trackList = lapElement.getElementsByTagName("Track");
            for (int i = 0; i < trackList.getLength(); i++) {
                Element trackElement = (Element) trackList.item(i);
                Track track = parseTrack(trackElement);
                if (track != null) {
                    lap.addTrack(track);
                }
            }

            return lap;
        } catch (Exception e) {
            System.err.println("Error parsing lap: " + e.getMessage());
            return null;
        }
    }
    private Track parseTrack(Element trackElement) {
        Track track = new Track();

        NodeList trackpointList = trackElement.getElementsByTagName("Trackpoint");
        for (int i = 0; i < trackpointList.getLength(); i++) {
            Element trackpointElement = (Element) trackpointList.item(i);
            Trackpoint trackpoint = parseTrackpoint(trackpointElement);
            if (trackpoint != null) {
                track.addTrackpoint(trackpoint);
            }
        }

        return track.getTrackpoints().isEmpty() ? null : track;
    }
    private Trackpoint parseTrackpoint(Element trackpointElement) {
        try {
            String timeStr = getElementTextContent(trackpointElement, "Time");
            LocalDateTime time = LocalDateTime.parse(timeStr, ISO_FORMATTER);

            Element positionElement = (Element) trackpointElement.getElementsByTagName("Position").item(0);
            double latitude = 0;
            double longitude = 0;

            if (positionElement != null) {
                latitude = Double.parseDouble(getElementTextContent(positionElement, "LatitudeDegrees"));
                longitude = Double.parseDouble(getElementTextContent(positionElement, "LongitudeDegrees"));
            }

            double altitude = parseDouble(trackpointElement, "AltitudeMeters", 0.0);
            double distance = parseDouble(trackpointElement, "DistanceMeters", 0.0);

            Trackpoint trackpoint = new Trackpoint(time, latitude, longitude, altitude, distance);

            // Προαιρετικά πεδία
            Element hrElement = (Element) trackpointElement.getElementsByTagName("HeartRateBpm").item(0);
            if (hrElement != null) {
                String hrValue = getElementTextContent(hrElement, "Value");
                if (hrValue != null && !hrValue.isEmpty()) {
                    trackpoint.setHeartRateBpm(Integer.parseInt(hrValue));
                }
            }

            String cadenceStr = getElementTextContent(trackpointElement, "Cadence");
            if (cadenceStr != null && !cadenceStr.isEmpty()) {
                trackpoint.setCadence(Integer.parseInt(cadenceStr));
            }

            return trackpoint;
        } catch (Exception e) {
            System.err.println("Error parsing trackpoint: " + e.getMessage());
            return null;
        }
    }
    private String getElementTextContent(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            if (node.getChildNodes().getLength() > 0) {
                return node.getChildNodes().item(0).getNodeValue();
            }
        }
        return null;
    }
    private double parseDouble(Element parent, String tagName, double defaultValue) {
        String value = getElementTextContent(parent, tagName);
        if (value != null && !value.isEmpty()) {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
}
