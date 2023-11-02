/**
 * 
 */
package gov.noaa.pmel.socatmetadata.apps.test;

import java.io.File;

import gov.noaa.pmel.socatmetadata.apps.CdiacToOcadsConverter;

/**
 * @author kamb
 *
 */
public class CdiacToOcadsRunner {

    static String omeFileName = "49UP20040423.xml"; // "33HH20220301_PI_OME.xml";
    static String dataDirName = "/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/omeexamplefilesfromaoml"; // /33HH20220301";
    static File dataDir = new File(dataDirName);

    static String ocadsFileName = "49UP20040423_OCADS_Cdiac2OcadsConverted.xml";
    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            File omeFile = new File(dataDir, omeFileName);
            File ocadsFile = new File(dataDir, ocadsFileName);
            CdiacToOcadsConverter.main(new String[] {
                    omeFile.getPath(),
                    ocadsFile.getPath()
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
