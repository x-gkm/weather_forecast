import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public abstract class WeatherWidget {
    HashMap<String, ImageIcon> icons;
    JLabel imageLabel;
    JLabel temperature;
    JFrame frame;

    public WeatherWidget() throws IOException {
        icons = loadIcons();

        imageLabel = new JLabel();
        temperature = new JLabel();
    }

    public void addTo(JFrame f) {
        frame = f;

        frame.add(imageLabel);
        frame.add(temperature);
    }

    public void remove() {
        frame.remove(imageLabel);
        frame.remove(temperature);

        frame = null;
    }

    public abstract void setWeatherData(WeatherData data);

    private static HashMap<String, ImageIcon> loadIcons() throws IOException {
        String[] names = new String[]{
                "clear",
                "clouds",
                "mist",
                "partly_cloudy",
                "rain",
                "snow",
                "thunderstorm",
        };

        HashMap<String, ImageIcon> result = new HashMap<>();
        for (String name : names) {
            result.put(name, new ImageIcon(ImageIO.read(new File("assets/" + name + ".png"))));
        }

        return result;
    }
}
