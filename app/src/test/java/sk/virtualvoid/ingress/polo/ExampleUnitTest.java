package sk.virtualvoid.ingress.polo;

import org.junit.Test;

import sk.virtualvoid.ingress.polo.utils.LatLngDistance;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void distanceTest() throws Exception {
        double lat1 = 48.123, lon1 = 17.123;
        double lat2 = 48.456, lon2 = 17.456;

        double distance = LatLngDistance.km(lat1, lon1, lat2, lon2);

    }
}