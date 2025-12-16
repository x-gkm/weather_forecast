import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class MainWeatherWidget extends WeatherWidget {
    JLabel feelsLike;
    JLabel condition;

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
    public void addTo(JFrame f) {
        super.addTo(f);
        frame.add(feelsLike);
        frame.add(condition);
    }

    @Override
    public void remove() {
        frame.remove(feelsLike);
        frame.remove(condition);
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
