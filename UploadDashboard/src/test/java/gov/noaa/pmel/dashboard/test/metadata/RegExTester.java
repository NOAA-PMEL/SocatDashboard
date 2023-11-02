/**
 * 
 */
package gov.noaa.pmel.dashboard.test.metadata;

/**
 * @author kamb
 *
 */
public class RegExTester {

    private static final String DIGIT_PULLER_REGX_0 = "([+]?)([-]?\\d*\\.?\\d*)(.*)";
    private static final String DIGIT_PULLER_REGX = "(\\D*?)([-]?\\d*\\.?\\d*)(\\D*)";
    private static final String P2 = "\\D*[-]?\\d*\\.?\\d*.*";
    private static final String P3 = "(\\D*)([-]?\\d*\\.?\\d*)(.*)";


    /**
     * @param args
     */
    public static void main(String[] args) {
        String s = "2 μatm in fCO2_SW";
        System.out.println(DIGIT_PULLER_REGX_0+":"+s.matches(DIGIT_PULLER_REGX_0));
        System.out.println(DIGIT_PULLER_REGX+":"+s.matches(DIGIT_PULLER_REGX));
        System.out.println(P2+":"+s.matches(P2));
        System.out.println(P3+":"+s.matches(P3));
    }

}
