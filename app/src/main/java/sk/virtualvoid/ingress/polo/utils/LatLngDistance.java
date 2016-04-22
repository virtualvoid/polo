package sk.virtualvoid.ingress.polo.utils;

/**
 * Created by Juraj on 4/22/2016.
 */
@SuppressWarnings("SpellCheckingInspection")
public class LatLngDistance {
    private static final int EARTH_RADIUS = 6371;

    public static double km(double alat, double alon, double blat, double blon) {
        double radlat = (alat - blat) * Math.PI / 180;
        double radlon = (alon - blon) * Math.PI / 180;

        double sinradlat = Math.sin(radlat / 2);
        double sinradlon = Math.sin(radlon / 2);

        double a = sinradlat * sinradlat +
                Math.cos(alat * Math.PI / 180) * Math.cos(blat * Math.PI / 180) *
                        sinradlon * sinradlon;

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (EARTH_RADIUS * c);
    }

    public static double m(double alat, double alon, double blat, double blon) {
        return km(alat, alon, blat, blon) * 1000;
    }
}
