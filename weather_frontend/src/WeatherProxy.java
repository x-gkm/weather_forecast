import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class WeatherProxy {
    private final Process process;
    private final BufferedWriter writer;
    private final BufferedReader reader;

    public WeatherProxy() throws IOException {
        process = new ProcessBuilder("../weather_proxy/target/release/weather_proxy")
                .redirectErrorStream(true)
                .start();

        writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    }

    private void send(String s) throws IOException {
        writer.write(s);
        writer.newLine();
        writer.flush();
    }

    public ArrayList<GeoInfo> getGeoInfo(String query) throws IOException {
        send("geocode " + query);
        int count = Integer.parseInt(reader.readLine());
        ArrayList<GeoInfo> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            GeoInfo geoInfo = new GeoInfo();
            geoInfo.name = reader.readLine();
            geoInfo.lat = Double.parseDouble(reader.readLine());
            geoInfo.lon = Double.parseDouble(reader.readLine());

            list.add(geoInfo);
        }

        return list;
    }

    private WeatherData readWeatherData() throws IOException, ParseException {
        WeatherData result = new WeatherData();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss XXX");

        result.time = format.parse(reader.readLine());
        result.icon = reader.readLine();
        result.temperature = Double.parseDouble(reader.readLine());
        result.feels_like = Double.parseDouble(reader.readLine());
        result.condition = reader.readLine();

        return result;
    }

    public WeatherData getWeatherData(double lat, double lon) throws IOException, ParseException {
        send("weather " + lat + " " + lon);
        return readWeatherData();
    }

    public ArrayList<WeatherData> getForecast(double lat, double lon) throws IOException, ParseException {
        send("forecast " + lat + " " + lon);
        int count = Integer.parseInt(reader.readLine());
        ArrayList<WeatherData> results = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            results.add(readWeatherData());
        }
        return results;
    }
}
