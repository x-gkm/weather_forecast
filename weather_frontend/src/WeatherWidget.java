import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.awt.Container;

public abstract class WeatherWidget {
    HashMap<String, ImageIcon> icons;
    JLabel imageLabel;
    JLabel temperature;
    Container container;

    public WeatherWidget() throws IOException {
        icons = loadIcons();

        imageLabel = new JLabel();
        temperature = new JLabel();
    }

    public void addTo(Container c) {
    	container = c;
        container.add(imageLabel);
        container.add(temperature);
    }

    public void remove() {
    	if (container != null) {
            container.remove(imageLabel);
            container.remove(temperature);
            container = null;
        }
    }

    public abstract void setWeatherData(WeatherData data);

    private static HashMap<String, ImageIcon> loadIcons() throws IOException {
        String[] names = new String[]{
                "sunny",
                "night_clear",
                "clouds",
                "mist",
                "night_mist",
                "partly_cloudy",
                "rain",
                "night_rainy",
                "snow",
                "night_snow",
                "thunderstorm",
                "night_thunderstorm",
                "night_cloudy",
                "heavy_rain",
        };

        HashMap<String, ImageIcon> result = new HashMap<>();
        for (String name : names) {
            result.put(name, new ImageIcon(ImageIO.read(new File("assets/" + name + ".png"))));
        }

        return result;
    }
}
