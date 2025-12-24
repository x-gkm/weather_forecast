import java.util.ArrayList;

public class WeatherWarnings {
    public boolean is_frost;
    public boolean is_flood;
    public boolean is_thunderstorm;

    public WeatherWarnings(ArrayList<WeatherData> datas) {
        for (WeatherData data : datas) {
            switch (data.icon) {
                case "snow":
                case "night_snow":
                    is_frost = true;
                    break;
                case "heavy_rain":
                    is_flood = true;
                    break;
                case "thunderstorm":
                case "night_thunderstorm":
                    is_thunderstorm = true;
                    break;
            }
        }
    }
}
