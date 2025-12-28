public class GeoInfo {
    public final String name;
    public final double lat;
    public final double lon;

    public GeoInfo(String name, double lat, double lon) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
    }

    public void displayInfo() {
        System.out.println("name: " + name);
        System.out.println("lat: " + lat);
        System.out.println("lon: " + lon);
    }
}
