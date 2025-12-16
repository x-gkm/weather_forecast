import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class WeatherData {
    public String icon;
    public Date time;
    public double temperature;
    public double feels_like;
    public String condition;

    public void displayInfo() {
        System.out.println("time: " + time);
        System.out.println("temperature: " + temperature);
        System.out.println("feels like: " + feels_like);
    }

    public static ArrayList<ArrayList<WeatherData>> groupByDay(ArrayList<WeatherData> xs) {
        ArrayList<ArrayList<WeatherData>> results = new ArrayList<>();

        if (xs.isEmpty()) {
            return  results;
        }

        ArrayList<WeatherData> current = new ArrayList<>();
        current.add(xs.getFirst());
        results.add(current);

        if (xs.size() <= 1) {
            return results;
        }

        for (int i = 1; i < xs.size(); i++) {
            if (hourOfDay(xs.get(i).time) == 0) {
                current = new ArrayList<>();
                results.add(current);
            }
            current.add(xs.get(i));
        }

        return results;
    }

    private static int hourOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }
}
