package gov.noaa.pmel.socatmetadata.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a numeric string value with units (optional).
 * Used when the numeric value of the string needs to be validated and used.
 */
@SuppressWarnings("unchecked")
public final class NumericString implements Cloneable, Serializable {

    private static final long serialVersionUID = -4326342003221951882L;

//    private static final String DIGIT_PULLER_REGX_0 = "([+]?)([-]?\\d*\\.?\\d*)(.*)";
    private static final String DIGIT_PULLER_REGX = "(\\D*?)([-]?\\d*\\.\\d*|\\d+).*";
    private static int DIGIT_GROUP = 2;
//    private static final String DIGIT_PULLER_REGX = "([+][/]?)?([-]?\\d*\\.?\\d*)(.*)";
//    private static final String DIGIT_PULLER_REGX = "([+-]?(/-)?\\d*\\.?\\d*)(.*)";
//    private static final String UNITS_PULLER_REGX = "[ \t\\(\\[\\{]+(\\)\\]\\}]+";
    
    static void test(String t) {
        Pattern pattern = Pattern.compile(DIGIT_PULLER_REGX);
        Matcher matcher = pattern.matcher(t);
        boolean found = matcher.find();
        boolean matches = matcher.matches();
        System.out.println(t + " found: " + found + " matches: " + matches);
    }
    private static Pattern DIGIT_PULLER;
//    private static Pattern UNITS_PULLER;
    private static List<String> charList;
    static {
        DIGIT_PULLER = Pattern.compile(DIGIT_PULLER_REGX);
//        UNITS_PULLER = Pattern.compile(UNITS_PULLER_REGX);
        charList = (List<String>)new ArrayList() {{
//            add(" ");
//            add("\t");
            add("{");
            add("[");
            add("(");
            add("}");
            add("]");
            add(")");
        }};
    }
    
    private String valueString;
    private String unitString;
    // numericValue is always assigned from parsing valueString
    private double numericValue;

    public static NumericString guess(String fromString, String withUnits) throws IllegalArgumentException {
        NumericString ns = guess(fromString);
        String givenUnits = withUnits != null ? withUnits.trim().toLowerCase() : null;
        if ( givenUnits != null ) {
            String guessedUnits = ns.unitString.toLowerCase();
            if ( ! guessedUnits.isEmpty()) {
                if ( guessedUnits.indexOf(givenUnits) < 0 ) {
                    throw new IllegalArgumentException("Specified Units of " + withUnits + 
                                                       " are not compatible with units string " + guessedUnits + 
                                                       " found in numeric string " + fromString);
                }
            }
            ns.setUnitString(withUnits);
        }
        
        return ns;
    }
    public static NumericString guess(String fromString) throws IllegalArgumentException {
        NumericString ns = null;
        if ( fromString == null || fromString.trim().isEmpty()) {
            ns = new NumericString();
        } else {
//            try {
                String val = pullDigits(fromString);
                String units = null;
                if ( val != null && ! val.trim().isEmpty()) {
                    int endx = fromString.indexOf(val)+val.length();
                    if ( endx < fromString.length()) {
                        String unitsStr = fromString.substring(endx).trim();
                        String[] parts = unitsStr.split("[\\(\\)\\[\\]\\{\\}]+");
                        StringBuffer uBldr = new StringBuffer();
                        int px = 0;
                        while ( px < parts.length ) {
                            if ( ! charList.contains(parts[px])) { 
                                uBldr.append(parts[px]);
                            }
                            px += 1;
                        }
                        units = uBldr.toString();
                    }
                }
                ns = new NumericString(val, units);
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
        }
        return ns;
    }
    
    /**
     * 
     * @param valueStr
     * @return
     * @throws IllegalArgumentException
     */
    private static String pullDigits(String valueStr) throws IllegalArgumentException {
        String numStr = null;
        if ( valueStr == null || valueStr.isEmpty()) {
            return null;
        }
        String checkStr = valueStr.trim();
        
        if ( checkStr.startsWith("+/-") ||
             checkStr.startsWith("+-") ||
             checkStr.startsWith("±")) {
            int idx = checkStr.indexOf('-') > 0 ? checkStr.indexOf('-') : 0;
            checkStr = checkStr.substring(idx+1).trim();
        }
//        System.out.println(valueStr +" : " + checkStr);
        Matcher m = DIGIT_PULLER.matcher(checkStr); 
        if ( m.matches()) {
//            for (int i = 1; i <= m.groupCount(); i++) {
//                System.out.println("  [" + i + "]:" + m.group(i));
//            }
            numStr = m.group(DIGIT_GROUP);
//            if ( m.group(NEGATIVE_GROUP) != null ) {
//                numStr = "-"+numStr;
//            }
            if ( !isValidNumber(numStr)) {
                guessError(valueStr, numStr);
            }
        } else {
            guessError(valueStr, numStr);
        }
        return numStr;
    }
    
    private static void guessError(String valueStr, String numStr) throws IllegalArgumentException {
        String msg = "WARNING: NumericString: Error pulling digits from value string \"" + valueStr +"\". Found: " + numStr;
        System.err.println(msg);
        throw new IllegalArgumentException(msg);
    }
    
    /**
     * Create with empty strings, and NaN as the associated numeric value
     */
    public NumericString() {
        valueString = "";
        unitString = "";
        numericValue = Double.NaN;
    }

    /**
     * @param value
     *         numeric string to assign after trimming;  if null or blank,
     *         an empty string is assigned and the associated numeric value is Double.NaN
     * @param unit
     *         unit string for the numeric value;  if null or blank, an empty string is assigned
     *
     * @throws IllegalArgumentException
     *         if the given numeric string, if not null or blank, does not represent a finite numeric value
     */
    public NumericString(String value, String unit) throws IllegalArgumentException {
        setValueString(value);
//        setValueString(pullDigits(value));
        setUnitString(unit);
    }
    
    private static boolean isValidNumber(String numStr) {
        try {
            double d = Double.parseDouble(numStr);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
    
    public static void main(String[] args) {
        String t = "286.42 ppm CO2";
        test(t);
        String s = "< 0.01";
//        System.out.println(s + ": pulled: " + pullDigits(s));
        System.out.println(s+":"+guess(s));
        s = "-123.34";
//        System.out.println(s + ": pulled: " + pullDigits(s));
        System.out.println(s+":"+guess(s));
        s = "123.45";
        System.out.println(s+":"+guess(s));
        s = "+123.45";
//        System.out.println(s + ": pulled: " + pullDigits(s));
        System.out.println(s+":"+guess(s));
        s = "123";
//        System.out.println(s + ": pulled: " + pullDigits(s));
        System.out.println(s+":"+guess(s));
        s = "- 123.34";
//        System.out.println(s + ": pulled: " + pullDigits(s));
        System.out.println(s+":"+guess(s));
        s = ".42";
//        System.out.println(s + ": pulled: " + pullDigits(s));
        System.out.println(s+":"+guess(s));
        s = "-.42";
//        System.out.println(s + ": pulled: " + pullDigits(s));
        System.out.println(s+":"+guess(s));
        s = "286.42 ppm CO2";
//        System.out.println(s + ": pulled: " + pullDigits(s));
        System.out.println(s+":"+guess(s));
        s = "286.42ppm";
//        System.out.println(s + ": pulled: " + pullDigits(s));
        System.out.println(s+":"+guess(s));
        s = "286 ppm";
//        System.out.println(s + ": pulled: " + pullDigits(s));
        System.out.println(s+":"+guess(s));
        s = "+/-42.2ppm";
//        System.out.println(s + ": pulled: " + pullDigits(s));
        System.out.println(s+":"+guess(s));
        s = "+/- 42.2ppm";
//        System.out.println(s + ": pulled: " + pullDigits(s));
        System.out.println(s+":"+guess(s));
        s = "±42.2ppm";
//        System.out.println(s + ": pulled: " + pullDigits(s));
        System.out.println(s+":"+guess(s));
        s = "±42.2°C";
//        System.out.println(s + ": pulled: " + pullDigits(s));
        System.out.println(s+":"+guess(s));
        s = "± 42.2 °C";
//        System.out.println(s + ": pulled: " + pullDigits(s));
        System.out.println(s+":"+guess(s));
        s = "±42.2(°C)";
//        System.out.println(s + ": pulled: " + pullDigits(s));
        System.out.println(s+":"+guess(s));
        s = "±42.2 (°C)";
//        System.out.println(s + ": pulled: " + pullDigits(s));
        System.out.println(s+":"+guess(s));
        s = "±42.2 [µmol / mol]";
//        System.out.println(s + ": pulled: " + pullDigits(s));
        System.out.println(s+":"+guess(s));
        s = "±42.2 ({°C})";
//        System.out.println(s + ": pulled: " + pullDigits(s));
        System.out.println(s+":"+guess(s));
        s = "± 42.2 o/oo";
//        System.out.println(s + ": pulled: " + pullDigits(s));
        System.out.println(s+":"+guess(s));
        s = "< -0.01 ";
//        System.out.println(s + ": pulled: " + pullDigits(s));
        System.out.println(s+":"+guess(s));
        s = "± something °C";
//      System.out.println(s + ": pulled: " + pullDigits(s));
        System.out.println(s+":"+guess(s));
    }

    /**
     * @return the numeric string; never null but may be empty.
     *         If not empty, guaranteed to represents a finite number.
     */
    public String getValueString() {
        return valueString;
    }

    /**
     * @param valueString
     *         numeric string to assign after trimming; if null or blank,
     *         an empty string is assigned and the associated numeric value is NaN
     *
     * @throws IllegalArgumentException
     *         if the given string, if not null or blank, does not represent a finite numeric value
     */
    public void setValueString(String valueString) throws IllegalArgumentException {
        this.valueString = (valueString != null) ? valueString.trim() : "";
        if ( !this.valueString.isEmpty() ) {
            try {
                this.numericValue = Double.parseDouble(this.valueString);
            } catch ( NumberFormatException ex ) {
                throw new IllegalArgumentException(ex);
            }
            if ( Double.isNaN(this.numericValue) )
                throw new IllegalArgumentException("value is NaN");
            if ( Double.isInfinite(this.numericValue) )
                throw new IllegalArgumentException("value is infinite");
        }
        else
            this.numericValue = Double.NaN;
    }

    /**
     * @return the unit string for the numeric value; never null but may be empty
     */
    public String getUnitString() {
        return unitString;
    }

    /**
     * @param unitString
     *         assign at the unit string for the numeric value; if null or blank, an empty string is assigned
     */
    public void setUnitString(String unitString) {
        this.unitString = (unitString != null) ? unitString.trim() : "";
    }

    /**
     * @return the numeric value represented by the numeric string
     */
    public double getNumericValue() {
        return numericValue;
    }

    /**
     * Does nothing; the numeric value is always assigned from parsing the string value.
     * This only exist to satisfy JavaBean requirements.
     */
    @SuppressWarnings("unused")
    private void setNumericValue(double numericValue) {
    }

    /**
     * @return if this represents a valid numeric string (not empty)
     */
    public boolean isValid() {
        if ( valueString.isEmpty() )
            return false;
        return true;
    }

    /**
     * @return if the numeric value represented by this string is a valid positive number
     */
    public boolean isPositive() {
        if ( valueString.isEmpty() )
            return false;
        return (numericValue > 0.0);
    }

    /**
     * @return if the numeric value represented by this string is a valid non-negative number
     */
    public boolean isNonNegative() {
        if ( valueString.isEmpty() )
            return false;
        return (numericValue >= 0.0);
    }

    /**
     * @return if the numeric value represented by this string is a valid non-positive number
     */
    public boolean isNonPositive() {
        if ( valueString.isEmpty() )
            return false;
        return (numericValue <= 0.0);
    }

    /**
     * @return if the numeric value represented by this string is a valid negative number
     */
    public boolean isNegative() {
        if ( valueString.isEmpty() )
            return false;
        return (numericValue < 0.0);
    }

    /**
     * @return the trimmed string resulting from concatenating the numeric string, a space, and the unit string
     */
    public String asOneString() {
        String repr = valueString + " " + unitString;
        return repr.trim();
    }

    @Override
    public NumericString clone() {
        NumericString dup;
        try {
            dup = (NumericString) super.clone();
        } catch ( CloneNotSupportedException ex ) {
            throw new RuntimeException(ex);
        }
        dup.valueString = valueString;
        dup.unitString = unitString;
        dup.numericValue = numericValue;
        return dup;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( !(obj instanceof NumericString) )
            return false;

        NumericString that = (NumericString) obj;

        if ( !valueString.equals(that.valueString) )
            return false;
        if ( !unitString.equals(that.unitString) )
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = valueString.hashCode();
        result = result * prime + unitString.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "NumericString{" +
                "valueString='" + valueString + '\'' +
                ", unitString='" + unitString + '\'' +
                '}';
    }

}
