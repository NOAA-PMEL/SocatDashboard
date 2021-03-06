package gov.noaa.pmel.socatmetadata;

import gov.noaa.pmel.socatmetadata.util.NumericString;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.TimeZone;
import java.util.TreeSet;

/**
 * Information about the longitude, latitude, and time coverage of data in a dataset
 */
public class Coverage implements Cloneable, Serializable {

    private static final long serialVersionUID = -4506973102300330865L;

    public static final String LONGITUDE_UNITS = "dec deg E";
    public static final String LATITUDE_UNITS = "dec deg N";
    public static final String WGS84 = "WGS 84";

    /**
     * 1900-01-01 00:00:00 == Date(-2208988800000L)
     */
    public static final Date MIN_DATA_TIME;

    private static final DateFormat TIMESTAMP_PARSER;

    static {
        TIMESTAMP_PARSER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TIMESTAMP_PARSER.setTimeZone(TimeZone.getTimeZone("UTC"));
        TIMESTAMP_PARSER.setLenient(false);
        try {
            MIN_DATA_TIME = TIMESTAMP_PARSER.parse("1900-01-01 00:00:00");
        } catch ( ParseException ex ) {
            throw new RuntimeException(ex);
        }
    }

    protected NumericString westernLongitude;
    protected NumericString easternLongitude;
    protected NumericString southernLatitude;
    protected NumericString northernLatitude;
    protected Date earliestDataTime;
    protected Date latestDataTime;
    protected String spatialReference;
    protected TreeSet<String> geographicNames;

    /**
     * Create with empty longitudes, empty latitudes, invalid times,
     * the spatial reference set to WGS 84, and no geographic names.
     */
    public Coverage() {
        westernLongitude = new NumericString(null, LONGITUDE_UNITS);
        easternLongitude = new NumericString(null, LONGITUDE_UNITS);
        southernLatitude = new NumericString(null, LATITUDE_UNITS);
        northernLatitude = new NumericString(null, LATITUDE_UNITS);
        earliestDataTime = new Date(MIN_DATA_TIME.getTime() - 1);
        latestDataTime = new Date(MIN_DATA_TIME.getTime() - 1);
        spatialReference = WGS84;
        geographicNames = new TreeSet<String>();
    }

    /**
     * Create with the given values, the spatial reference set to WGS 84, and no geographic names.
     *
     * @param westernLongitude
     *         westernmost longitude in decimal degrees east; if null or blank, an empty numeric string is assigned
     * @param easternLongitude
     *         easternmost longitude in decimal degrees east; if null or blank, an empty numeric string is assigned
     * @param southernLatitude
     *         southernmost latitude in decimal degrees north; if null or blank, an empty numeric string is assigned
     * @param northernLatitude
     *         northernmost latitude in decimal degrees north; if null or blank, an empty numeric string is assigned
     * @param earliestDataTime
     *         UTC date and time of the earliest (oldest) data measurement in yyyy-MM-dd HH:mm:ss format;
     *         if null or blank, an invalid time is assigned
     * @param latestDataTime
     *         UTC date and time of the latest (newest) data measurement in yyyy-MM-dd HH:mm:ss format;
     *         if null or blank, an invalid time is assigned
     *
     * @throws IllegalArgumentException
     *         if any of the values, if not null or blank, for the longitudes, latitudes, or times are invalid
     *         (longitudes outside the range [-360, 360] are considered in invalid)
     */
    public Coverage(String westernLongitude, String easternLongitude, String southernLatitude, String northernLatitude,
            String earliestDataTime, String latestDataTime) throws IllegalArgumentException {
        this();
        setWesternLongitude(new NumericString(westernLongitude, LONGITUDE_UNITS));
        setEasternLongitude(new NumericString(easternLongitude, LONGITUDE_UNITS));
        setSouthernLatitude(new NumericString(southernLatitude, LATITUDE_UNITS));
        setNorthernLatitude(new NumericString(northernLatitude, LATITUDE_UNITS));
        Date dataTime;
        if ( (earliestDataTime != null) && !earliestDataTime.trim().isEmpty() ) {
            try {
                dataTime = TIMESTAMP_PARSER.parse(earliestDataTime.trim());
            } catch ( ParseException ex ) {
                throw new IllegalArgumentException("Invalid earliest data time: " + ex.getMessage());
            }
        }
        else
            dataTime = null;
        setEarliestDataTime(dataTime);
        if ( (latestDataTime != null) && !latestDataTime.trim().isEmpty() ) {
            try {
                dataTime = TIMESTAMP_PARSER.parse(latestDataTime.trim());
            } catch ( ParseException ex ) {
                throw new IllegalArgumentException("Invalid latest data time: " + ex.getMessage());
            }
        }
        else
            dataTime = null;
        setLatestDataTime(dataTime);
    }

    /**
     * @return list of field names that are currently invalid
     */
    public HashSet<String> invalidFieldNames() {
        HashSet<String> invalid = new HashSet<String>();
        if ( !westernLongitude.isValid() )
            invalid.add("westernLongitude");
        if ( !easternLongitude.isValid() )
            invalid.add("easternLongitude");

        if ( southernLatitude.isValid() && northernLatitude.isValid() ) {
            if ( southernLatitude.getNumericValue() > northernLatitude.getNumericValue() ) {
                invalid.add("southernLatitude");
                invalid.add("northernLatitude");
            }
        }
        else {
            if ( !southernLatitude.isValid() )
                invalid.add("southernLatitude");
            if ( !northernLatitude.isValid() )
                invalid.add("northernLatitude");
        }

        if ( earliestDataTime.before(MIN_DATA_TIME) || earliestDataTime.after(new Date()) ||
                earliestDataTime.after(latestDataTime) )
            invalid.add("earliestDataTime");
        if ( latestDataTime.before(MIN_DATA_TIME) || latestDataTime.after(new Date()) ||
                latestDataTime.before(earliestDataTime) )
            invalid.add("latestDataTime");
        return invalid;
    }

    /**
     * @return the western longitude limit; never null but may be empty. If not empty, the longitude is guaranteed
     *         to be a numeric value in the range [-360.0,360.0] and the units will be {@link #LONGITUDE_UNITS}.
     */
    public NumericString getWesternLongitude() {
        return westernLongitude.clone();
    }

    /**
     * @param westernLongitude
     *         assign as the western longitude limit; if null, an empty NumericString is assigned
     *
     * @throws IllegalArgumentException
     *         if the numeric string of the given longitude is not empty and not in the range [-360,360], or
     *         if the unit string of the given longitude is not {@link #LONGITUDE_UNITS}
     */
    public void setWesternLongitude(NumericString westernLongitude) throws IllegalArgumentException {
        if ( (westernLongitude != null) && westernLongitude.isValid() ) {
            double val = westernLongitude.getNumericValue();
            if ( (val < -360.0) || (val > 360.0) )
                throw new IllegalArgumentException("westernmost longitude is not in [-360.0,360.0]");
            if ( !LONGITUDE_UNITS.equals(westernLongitude.getUnitString()) )
                throw new IllegalArgumentException("westernmost longitude units are not " + LONGITUDE_UNITS);
            this.westernLongitude = westernLongitude.clone();
        }
        else
            this.westernLongitude = new NumericString(null, LONGITUDE_UNITS);
    }

    /**
     * @return the eastern longitude limit; never null but may be empty. If not empty, the longitude is guaranteed
     *         to be a numeric value in the range [-360.0,360.0] and the units will be {@link #LONGITUDE_UNITS}
     */
    public NumericString getEasternLongitude() {
        return easternLongitude.clone();
    }

    /**
     * @param easternLongitude
     *         assign as the eastern longitude limit; if null, an empty NumericString is assigned
     *
     * @throws IllegalArgumentException
     *         if the numeric string of the given longitude is not empty and not in the range [-360,360], or
     *         if the unit string of the given longitude is not {@link #LONGITUDE_UNITS}
     */
    public void setEasternLongitude(NumericString easternLongitude) throws IllegalArgumentException {
        if ( (easternLongitude != null) && easternLongitude.isValid() ) {
            double val = easternLongitude.getNumericValue();
            if ( (val < -360.0) || (val > 360.0) )
                throw new IllegalArgumentException("easternmost longitude is not in [-360.0,360.0]");
            if ( !LONGITUDE_UNITS.equals(easternLongitude.getUnitString()) )
                throw new IllegalArgumentException("easternmost longitude units are not " + LONGITUDE_UNITS);
            this.easternLongitude = easternLongitude.clone();
        }
        else
            this.easternLongitude = new NumericString(null, LONGITUDE_UNITS);
    }

    /**
     * @return the southern latitude limit; never null but may be empty. If not empty, the latitude is guaranteed
     *         to be a numeric value in the range [-90.0,90.0] and the units will be {@link #LATITUDE_UNITS}
     */
    public NumericString getSouthernLatitude() {
        return southernLatitude.clone();
    }

    /**
     * @param southernLatitude
     *         assign as the southern latitude limit; if null, an empty NumericString is assigned
     *
     * @throws IllegalArgumentException
     *         if the numeric string of the given latitude is not empty and not in the range [-90,90], or
     *         if the unit string of the given latitude is not {@link #LATITUDE_UNITS}
     */
    public void setSouthernLatitude(NumericString southernLatitude) throws IllegalArgumentException {
        if ( (southernLatitude != null) && southernLatitude.isValid() ) {
            double val = southernLatitude.getNumericValue();
            if ( (val < -90.0) || (val > 90.0) )
                throw new IllegalArgumentException("southernmost latitude is not in [-90.0,90.0]");
            if ( !LATITUDE_UNITS.equals(southernLatitude.getUnitString()) )
                throw new IllegalArgumentException("southernLatitude longitude units are not " + LATITUDE_UNITS);
            this.southernLatitude = southernLatitude.clone();
        }
        else
            this.southernLatitude = new NumericString(null, LATITUDE_UNITS);
    }

    /**
     * @return the northern latitude limit; never null but may be empty. If not empty, the latitude is guaranteed
     *         to be a numeric value in the range [-90.0,90.0] and the units will be {@link #LATITUDE_UNITS}
     */
    public NumericString getNorthernLatitude() {
        return northernLatitude.clone();
    }

    /**
     * @param northernLatitude
     *         assign as the northern latitude limit; if null, an empty NumericString is assigned
     *
     * @throws IllegalArgumentException
     *         if the numeric string of the given latitude is not empty and not in the range [-90,90], or
     *         if the unit string of the given latitude is not {@link #LATITUDE_UNITS}
     */
    public void setNorthernLatitude(NumericString northernLatitude) {
        if ( (northernLatitude != null) && northernLatitude.isValid() ) {
            double val = northernLatitude.getNumericValue();
            if ( (val < -90.0) || (val > 90.0) )
                throw new IllegalArgumentException("northernmost latitude is not in [-90.0,90.0]");
            if ( !LATITUDE_UNITS.equals(northernLatitude.getUnitString()) )
                throw new IllegalArgumentException("northernLatitude longitude units are not " + LATITUDE_UNITS);
            this.northernLatitude = northernLatitude.clone();
        }
        else
            this.northernLatitude = new NumericString(null, LATITUDE_UNITS);
    }

    /**
     * @return the earliest (oldest) data time value; never null but may be prior to {@link #MIN_DATA_TIME}
     */
    public Date getEarliestDataTime() {
        return new Date(earliestDataTime.getTime());
    }

    /**
     * @param earliestDataTime
     *         assign as the earliest (oldest) data time value;
     *         if null, a date prior to {@link #MIN_DATA_TIME} will be assigned
     */
    public void setEarliestDataTime(Date earliestDataTime) {
        if ( earliestDataTime != null )
            this.earliestDataTime = new Date(earliestDataTime.getTime());
        else
            this.earliestDataTime = new Date(MIN_DATA_TIME.getTime() - 1);
    }

    /**
     * @return the latest (newest) data time value; never null but may be prior to {@link #MIN_DATA_TIME}
     */
    public Date getLatestDataTime() {
        return new Date(latestDataTime.getTime());
    }

    /**
     * @param latestDataTime
     *         assign as the latest (newest) data time value;
     *         if null, a date prior to {@link #MIN_DATA_TIME} will be assigned
     */
    public void setLatestDataTime(Date latestDataTime) {
        if ( latestDataTime != null )
            this.latestDataTime = new Date(latestDataTime.getTime());
        else
            this.latestDataTime = new Date(MIN_DATA_TIME.getTime() - 1);
    }

    /**
     * @return the spatial reference; never null but may be empty
     */
    public String getSpatialReference() {
        return spatialReference;
    }

    /**
     * @param spatialReference
     *         assign as the spatial reference; if null, WGS 84 is assigned
     */
    public void setSpatialReference(String spatialReference) {
        this.spatialReference = (spatialReference != null) ? spatialReference.trim() : WGS84;
        if ( this.spatialReference.isEmpty() )
            this.spatialReference = WGS84;
    }

    /**
     * @return the set of geographic names; never null but may be empty.
     *         Any names given are guaranteed to be non-blank strings.
     */
    public TreeSet<String> getGeographicNames() {
        return new TreeSet<String>(geographicNames);
    }

    /**
     * Calls {@link #setGeographicNames(Iterable)}; added to satisfy JavaBean requirements.
     *
     * @param geographicNames
     *         assign as the list of geographic names; if null, an empty set is assigned
     *
     * @throws IllegalArgumentException
     *         if any name given is null or blank
     */
    public void setGeographicNames(TreeSet<String> geographicNames) throws IllegalArgumentException {
        setGeographicNames((Iterable<String>) geographicNames);
    }

    /**
     * @param geographicNames
     *         assign as the list of geographic names; if null, an empty set is assigned
     *
     * @throws IllegalArgumentException
     *         if any name given is null or blank
     */
    public void setGeographicNames(Iterable<String> geographicNames) throws IllegalArgumentException {
        this.geographicNames.clear();
        if ( geographicNames != null ) {
            for (String name : geographicNames) {
                if ( name == null )
                    throw new IllegalArgumentException("null geographic region name given");
                name = name.trim();
                if ( name.isEmpty() )
                    throw new IllegalArgumentException("blank geographic region name given");
                this.geographicNames.add(name);
            }
        }
    }

    @Override
    public Coverage clone() {
        Coverage coverage;
        try {
            coverage = (Coverage) super.clone();
        } catch ( CloneNotSupportedException ex ) {
            throw new RuntimeException(ex);
        }
        coverage.westernLongitude = westernLongitude.clone();
        coverage.easternLongitude = easternLongitude.clone();
        coverage.southernLatitude = southernLatitude.clone();
        coverage.northernLatitude = northernLatitude.clone();
        coverage.earliestDataTime = new Date(earliestDataTime.getTime());
        coverage.latestDataTime = new Date(latestDataTime.getTime());
        coverage.spatialReference = spatialReference;
        coverage.geographicNames = new TreeSet<String>(geographicNames);
        return coverage;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( null == obj )
            return false;
        if ( !(obj instanceof Coverage) )
            return false;

        Coverage other = (Coverage) obj;

        if ( !westernLongitude.equals(other.westernLongitude) )
            return false;
        if ( !easternLongitude.equals(other.easternLongitude) )
            return false;
        if ( !southernLatitude.equals(other.southernLatitude) )
            return false;
        if ( !northernLatitude.equals(other.northernLatitude) )
            return false;
        if ( !earliestDataTime.equals(other.earliestDataTime) )
            return false;
        if ( !latestDataTime.equals(other.latestDataTime) )
            return false;
        if ( !spatialReference.equals(other.spatialReference) )
            return false;
        if ( !geographicNames.equals(other.geographicNames) )
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = westernLongitude.hashCode();
        result = result * prime + easternLongitude.hashCode();
        result = result * prime + southernLatitude.hashCode();
        result = result * prime + northernLatitude.hashCode();
        result = result * prime + earliestDataTime.hashCode();
        result = result * prime + latestDataTime.hashCode();
        result = result * prime + spatialReference.hashCode();
        result = result * prime + geographicNames.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Coverage{" +
                "westernLongitude=" + westernLongitude +
                ", easternLongitude=" + easternLongitude +
                ", southernLatitude=" + southernLatitude +
                ", northernLatitude=" + northernLatitude +
                ", earliestDataTime=" + earliestDataTime +
                ", latestDataTime=" + latestDataTime +
                ", spatialReference='" + spatialReference + "'" +
                ", geographicNames=" + geographicNames +
                '}';
    }

}
