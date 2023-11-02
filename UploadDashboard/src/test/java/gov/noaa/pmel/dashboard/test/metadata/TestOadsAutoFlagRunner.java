/**
 * 
 */
package gov.noaa.pmel.dashboard.test.metadata;

/**
 * @author kamb
 *
 */
public class TestOadsAutoFlagRunner {

    static String[][] testArgs = new String[][] {
            new String[] {
                   "/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/316420100908_C/316420100908/oap_metadata_316420100908.xml", // oads_xml_file
                   "/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/316420100908_C/316420100908/PI_OME.xml", // ome_xml_file
                   "316420100908" // dataset_id
            },
            new String[] {
                    "/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/WFCH20220623/WFCH20220623.xml",
                    "/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/WFCH20220623/MetadataDocs/WFCH/WFCH20220623/PI_OME.xml",
                    "WFCH20220623"
            },
            new String[] {
                    "/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/32KZ20060516/32KZ20060516_oads.xml",
                    "/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/32KZ20060516/MetadataDocs/32KZ/32KZ20060516/PI_OME.xml",
                    "32KZ20060516"
            },
            new String[] {
                    "/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/omeexamplefilesfromaoml/49UP20040423/49UP20040423.xml",
                    "/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/omeexamplefilesfromaoml/49UP20040423/49UP20040423_oads.xml",
                    "49UP20040423"
            }
        };
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
//            String oadsXmlFile = "/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/33RR20220501/33RR20220501/oap_metadata_33RR20220501_33RR20220614_33RR20221115-1.xml";
//            String oadsXmlFile = "/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/33RR20220501/33RR20220501/oap_metadata_33RR20220501-2.xml";
//            String oadsXmlFile = "/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/oap_metadata_316420200911.xml";
//            String oadsXmlFile = "/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/flagfiles/32KZ20060516/oap_metadata_32KZ20060516.xml";
//            String oadsXmlFile = "/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/omeexamplefilesfromaoml/33HH20220301/oap_metadata_33HH20220301-latest.xml";
//            String datasetId = "316420200911";
//            String datasetId = "32KZ20060516";
//            String datasetId = "33RR20220501";
//            String datasetId = "33HH20220301";
            for (int idx = 0; idx < testArgs.length; idx++) {
                String[] runArgs = testArgs[idx];
                String oadsFile = runArgs[0];
                String omeFile = runArgs[1];
                String datasetId = runArgs[2];
                String[] debugArgs = new String[] {
                    TestOadsAutoFlag.OME_FLAG,
                    omeFile,
    //                TestOadsAutoFlag.SDI_FLAG,
                    TestOadsAutoFlag.PDF_FLAG,
                    TestOadsAutoFlag.DS_ID_FLAG,
                    datasetId,
                    oadsFile
                };
                TestOadsAutoFlag.main(debugArgs);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
