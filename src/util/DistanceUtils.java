package util;

public class DistanceUtils {
	
	public static double euclideanDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
    }
	
	public static double latlonDistance(double lat1, double lon1,
            double lat2, double lon2) {

        Double theta = lon1 - lon2;
        Double dist = (Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2)))
                + (Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta)));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        dist = dist * 1.609344 * 1000;

        return dist;
    }
	
	private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
