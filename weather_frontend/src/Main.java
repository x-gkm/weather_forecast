import javax.swing.*;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Main {
    public static void main(String[] args) throws IOException, ParseException {
        JFrame frame = new JFrame("Weather forecast app");
        frame.setSize(500, 300);
        frame.setLayout(null);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JTextField input = new JTextField();
        input.setBounds(10, 10, 200, 30);
        frame.add(input);

        JButton currentWeather = new JButton("Weather preview");
        currentWeather.setBounds(220, 10, 150, 30);
        frame.add(currentWeather);

        JButton forecast = new JButton("Forecast");
        forecast.setBounds(380, 10, 100, 30);
        frame.add(forecast);

        WeatherWidget weatherWidget = new MainWeatherWidget(10, 40);
        weatherWidget.addTo(frame);

        WeatherProxy proxy = new WeatherProxy();

        currentWeather.addActionListener(e -> {
            try {
                GeoInfo geoInfo = proxy.getGeoInfo(input.getText()).getFirst();
                WeatherData weatherData = proxy.getWeatherData(geoInfo.lat, geoInfo.lon);
                weatherWidget.setWeatherData(weatherData);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame,
                        "An error occurred while fetching data",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        forecast.addActionListener(e -> {
            try {
                JFrame forecastFrame = new JFrame("Weather forecast");
                forecastFrame.setSize(980, 900);
                forecastFrame.setLayout(null);
                forecastFrame.setResizable(false);

                GeoInfo geoInfo = proxy.getGeoInfo(input.getText()).getFirst();
                ArrayList<ArrayList<WeatherData>> grouped = WeatherData.groupByDay(proxy.getForecast(geoInfo.lat, geoInfo.lon));

                for (int i = 0; i < grouped.size(); i++) {
                    JLabel weekDay = new JLabel();
                    weekDay.setText(dayOfWeek(grouped.get(i).getFirst().time));
                    weekDay.setBounds(20, 107 + 130 * i, 80, 15);
                    forecastFrame.add(weekDay);
                    for (int j = 0; j < grouped.get(i).size(); j++) {
                        WeatherWidget widget = new SmallWeatherWidget(100 * (j + 1),50 +  130 * i);
                        widget.addTo(forecastFrame);
                        widget.setWeatherData(grouped.get(i).get(j));
                    }
                }

                forecastFrame.setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame,
                        "An error occurred while fetching data",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setVisible(true);
    }

    private static String dayOfWeek(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("EEEE");
        return dateFormat.format(date);
    }
}
