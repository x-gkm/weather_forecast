import javax.swing.*;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class SmallWeatherWidget extends WeatherWidget {
    JLabel time;

    public SmallWeatherWidget(int x, int y) throws IOException {
        time = new JLabel();

        time.setHorizontalAlignment(SwingConstants.CENTER);
        temperature.setHorizontalAlignment(SwingConstants.CENTER);

        time.setBounds(x, y, 100, 15);
        imageLabel.setBounds(x, y + 15, 100, 100);
        temperature.setBounds(x, y + 115, 100, 15);
    }

    @Override
    public void addTo(JFrame f) {
        super.addTo(f);
        frame.add(time);
    }

    @Override
    public void remove() {
        frame.remove(time);
        super.remove();
    }

    @Override
    public void setWeatherData(WeatherData data) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

        time.setText(dateFormat.format(data.time));
        imageLabel.setIcon(icons.get(data.icon));
        temperature.setText(data.temperature + " °C");
    }
}
