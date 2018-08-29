package gov.noaa.pmel.sdimetadata.test;

import gov.noaa.pmel.sdimetadata.instrument.Instrument;
import gov.noaa.pmel.sdimetadata.instrument.Sensor;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

public class SensorTest {

    private static final String NAME = "Equilibrator headspace differential pressure sensor";
    private static final String ID = "Setra-239 #0003245";
    private static final String MANUFACTURER = "Setra";
    private static final String MODEL = "239";
    private static final String LOCATION = "Attached to equilibrator headspace";
    private static final ArrayList<String> ADDN_INFO = new ArrayList<String>(Arrays.asList(
            "Pressure reading from the Setra-270 on the exit of the analyzer was added to the differential pressure " +
                    "reading from Setra-239 attached to the equilibrator headspace to yield the equlibrator pressure.",
            "Some other comment just to have a second one."
    ));

    @Test
    public void testIsValid() {
        Sensor sensor = new Sensor();
        assertFalse(sensor.isValid());
        sensor.setName(NAME);
        assertFalse(sensor.isValid());
        sensor.setManufacturer(MANUFACTURER);
        sensor.setModel(MODEL);
        assertTrue(sensor.isValid());
    }

    @Test
    public void testClone() {
        Sensor sensor = new Sensor();
        Sensor dup = sensor.clone();
        assertEquals(sensor, dup);
        assertNotSame(sensor, dup);

        sensor.setName(NAME);
        sensor.setId(ID);
        sensor.setManufacturer(MANUFACTURER);
        sensor.setModel(MODEL);
        sensor.setLocation(LOCATION);
        sensor.setAddnInfo(ADDN_INFO);
        assertNotEquals(sensor, dup);

        dup = sensor.clone();
        assertEquals(sensor, dup);
        assertNotSame(sensor, dup);
        assertNotSame(sensor.getAddnInfo(), dup.getAddnInfo());
    }

    @Test
    public void testHashCodeEquals() {
        Sensor first = new Sensor();
        assertFalse(first.equals(null));
        assertFalse(first.equals(LOCATION));

        Sensor second = new Sensor();
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        Instrument other = new Instrument();
        assertFalse(first.equals(other));
        assertTrue(other.equals(second));

        first.setName(NAME);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setName(NAME);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        other.setName(NAME);
        assertFalse(first.equals(other));
        assertTrue(other.equals(second));

        first.setId(ID);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setId(ID);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        other.setId(ID);
        assertFalse(first.equals(other));
        assertTrue(other.equals(second));

        first.setManufacturer(MANUFACTURER);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setManufacturer(MANUFACTURER);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        other.setManufacturer(MANUFACTURER);
        assertFalse(first.equals(other));
        assertTrue(other.equals(second));

        first.setModel(MODEL);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setModel(MODEL);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        other.setModel(MODEL);
        assertFalse(first.equals(other));
        assertTrue(other.equals(second));

        first.setLocation(LOCATION);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setLocation(LOCATION);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        other.setLocation(LOCATION);
        assertFalse(first.equals(other));
        assertTrue(other.equals(second));

        first.setAddnInfo(ADDN_INFO);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setAddnInfo(ADDN_INFO);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        other.setAddnInfo(ADDN_INFO);
        assertFalse(first.equals(other));
        assertTrue(other.equals(second));
    }

}