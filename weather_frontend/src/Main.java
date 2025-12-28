import javax.swing.*;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.awt.Dimension;

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
        
        JLabel todayLabel = new JLabel("TODAY");
        todayLabel.setBounds(90, 45, 100, 20);
        todayLabel.setHorizontalAlignment(SwingConstants.CENTER);
        todayLabel.setVisible(false); 
        frame.add(todayLabel);
        
        JLabel tomorrowLabel = new JLabel("TOMORROW");
        tomorrowLabel.setBounds(330, 45, 100, 20);
        tomorrowLabel.setHorizontalAlignment(SwingConstants.CENTER);
        tomorrowLabel.setVisible(false);
        frame.add(tomorrowLabel);
        
        WeatherWidget todayWidget = new MainWeatherWidget(10, 70);
        todayWidget.addTo(frame);
        WeatherWidget tomorrowWidget = new MainWeatherWidget(250, 70);
        tomorrowWidget.addTo(frame);

        WeatherProxy proxy = new WeatherProxy();

        currentWeather.addActionListener(e -> {
            try {
                GeoInfo geoInfo = getGeoInfo(input, frame, proxy);
                if (geoInfo == null) return;

                ArrayList<WeatherData> forecastList =
                        proxy.getForecast(geoInfo.lat, geoInfo.lon);
                ArrayList<ArrayList<WeatherData>> grouped =
                        WeatherData.groupByDay(forecastList);
                WeatherData today = grouped.get(0).getFirst();
                WeatherData tomorrow = grouped.get(1).getFirst();
                
                todayWidget.setWeatherData(today);
                tomorrowWidget.setWeatherData(tomorrow);

                todayLabel.setVisible(true);
                tomorrowLabel.setVisible(true);
            } catch (Exception ex) {
            	JOptionPane.showMessageDialog(frame,
                        "An error occurred while fetching data",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        forecast.addActionListener(e -> {
            try {
                GeoInfo geoInfo = getGeoInfo(input, frame, proxy);
                if (geoInfo == null) return;

                JFrame forecastFrame = new JFrame("Weather forecast");
                forecastFrame.setSize(980, 600);
                forecastFrame.setLayout(null);
                forecastFrame.setResizable(false);
                forecastFrame.setLocationRelativeTo(null);
                JPanel panel = new JPanel();
                
                panel.setLayout(null);
                panel.setPreferredSize(new Dimension(950, 900));
                               
                JScrollPane scrollPane = new JScrollPane(panel);
                scrollPane.setBounds(0, 0, 980, 600);
                forecastFrame.add(scrollPane);
                
                JScrollBar vertical = scrollPane.getVerticalScrollBar();
                vertical.setPreferredSize(new Dimension(20, 0));
                JScrollBar horizontal = scrollPane.getHorizontalScrollBar();
                horizontal.setPreferredSize(new Dimension(0, 20));
                ArrayList<ArrayList<WeatherData>> grouped = WeatherData.groupByDay(proxy.getForecast(geoInfo.lat, geoInfo.lon));

                for (int i = 0; i < grouped.size(); i++) {
                    JLabel weekDay = new JLabel();
                    weekDay.setText(dayOfWeek(grouped.get(i).getFirst().time));
                    weekDay.setBounds(20, 107 + 130 * i, 80, 15);
                    panel.add(weekDay);
                    for (int j = 0; j < grouped.get(i).size(); j++) {
                        WeatherWidget widget = new SmallWeatherWidget(100 * (j + 1),50 +  130 * i);
                        widget.addTo(panel);
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

    private static GeoInfo getGeoInfo(JTextField input, JFrame frame, WeatherProxy proxy) throws IOException {
        String city = input.getText().trim();

        if (city.isEmpty()) {
            JOptionPane.showMessageDialog(frame,
                    "Please enter a city name!",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        ArrayList<GeoInfo> geoList = proxy.getGeoInfo(city);

        if (geoList.isEmpty()) {
            JOptionPane.showMessageDialog(frame,
                    "City not found!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }
        GeoInfo geoInfo = geoList.getFirst();
        input.setText(geoInfo.name);
        return geoInfo;
    }

    private static String dayOfWeek(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("EEEE");
        return dateFormat.format(date);
    }
}
