import javax.swing.*;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.awt.Dimension;
import java.awt.Image;

public class Main {
    public static void main(String[] args) throws IOException, ParseException {
        JFrame frame = new JFrame("Weather forecast app");
        frame.setSize(700, 300);
        frame.setLayout(null);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        JPanel sbtuPanel = new JPanel();
        sbtuPanel.setLayout(null);
        sbtuPanel.setBounds(500, 0, 200, 300);
        sbtuPanel.setBackground(new java.awt.Color(240, 240, 240));
        frame.add(sbtuPanel);
        
        ImageIcon logoIcon = new ImageIcon("weather_fronted/assests/SBTÜ.png");
        Image scaledImage = logoIcon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledImage));
        logoLabel.setBounds(40, 100, 120, 120);
        sbtuPanel.add(logoLabel);
        
        JLabel titleLabel = new JLabel("SBTÜ");
        titleLabel.setBounds(20, 20, 160, 30);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 20));
        sbtuPanel.add(titleLabel);

        JLabel subTitleLabel = new JLabel("Weather Forecast");
        subTitleLabel.setBounds(20, 55, 160, 20);
        subTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        subTitleLabel.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
        sbtuPanel.add(subTitleLabel);

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
        JLabel todayCityLabel = new JLabel("");
        todayCityLabel.setBounds(10, 205, 230, 20);
        frame.add(todayCityLabel);
        WeatherWidget tomorrowWidget = new MainWeatherWidget(250, 70);
        tomorrowWidget.addTo(frame);
        JLabel tomorrowCityLabel = new JLabel("");
        tomorrowCityLabel.setBounds(250, 205, 230, 20);
        frame.add(tomorrowCityLabel);

        WeatherProxy proxy = new WeatherProxy();

        currentWeather.addActionListener(e -> {
            try {
                GeoInfo geoInfo = getGeoInfo(input, frame, proxy);
                if (geoInfo == null) return;

                ArrayList<WeatherData> forecastList =
                        proxy.getForecast(geoInfo.lat, geoInfo.lon);
                ArrayList<ArrayList<WeatherData>> grouped =
                        WeatherData.groupByDay(forecastList);
                WeatherData today = proxy.getWeatherData(geoInfo.lat, geoInfo.lon);
                WeatherData tomorrow = grouped.get(1).getFirst();
                
                todayWidget.setWeatherData(today);
                tomorrowWidget.setWeatherData(tomorrow);
                
                String locationText = geoInfo.name;
                todayCityLabel.setText(locationText);
                tomorrowCityLabel.setText(locationText);

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
        return geoList.getFirst();
    }

    private static String dayOfWeek(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("EEEE");
        return dateFormat.format(date);
    }
}
