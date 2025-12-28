import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.awt.Container;

public class MainWeatherWidget extends WeatherWidget {
    private final JLabel feelsLike;
    private final JLabel condition;

    public MainWeatherWidget(int x, int y) throws IOException {
        feelsLike = new JLabel();
        condition = new JLabel();

        temperature.setFont(new Font("Sans",Font.PLAIN, 22));

        imageLabel.setBounds(x, y, 100, 100);
        temperature.setBounds(x + 110, y, 205, 30);
        feelsLike.setBounds(x + 110, y + 30, 205, 15);
        condition.setBounds(x + 110, y + 45, 205, 15);
    }

    @Override
    public void addTo(Container c) {
    	super.addTo(c);
        c.add(feelsLike);
        c.add(condition);
    }

    @Override
    public void remove() {
    	if (container != null) {
            container.remove(feelsLike);
            container.remove(condition);
        }
        super.remove();
    }

    @Override
    public void setWeatherData(WeatherData data) {
        imageLabel.setIcon(icons.get(data.icon));
        temperature.setText(data.temperature + " °C");
        feelsLike.setText("feels like " + data.feels_like + " °C");
        condition.setText(data.condition);
    }
}
