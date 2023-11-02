/**
 * 
 */
package gov.noaa.pmel.dashboard.test.actions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import gov.noaa.ncei.oads.xml.v_a0_2_2s.OadsMetadataDocumentType;
import gov.noaa.pmel.dashboard.actions.OadsPdfGenerator;
import gov.noaa.pmel.dashboard.actions.OmePdfGenerator;
import gov.noaa.pmel.dashboard.handlers.DataFileHandler;
import gov.noaa.pmel.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.tws.util.JWhich;

/**
 * @author kamb
 *
 */
public class TestMetadataPdfGeneration {

    /**
     * @param args
     */
    public static void main(String[] args) {
            File[] files = new File[] {
                new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/WFCH20220628/WFCH20220628.xml"),
                new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/33RR20220501_A/33RR20220501/oap_metadata_33RR20220501.xml"),
                new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/33RR20220501_A/33RR20220501/Metadata_SOCAT_Revelle.OADS.xml"),
                new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/33RR20220501_A/33RR20220501/Metadata_SOCAT_Revelle.excel2OADS.xml"),
                new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/33RR20220501_A/33RR20220501/oap_metadata_Revelle.xml"),
                new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/33RR20220501_A/33RR20220501/oap_metadata_33RR20220501-1.xml"),
                new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/33RR20220501_A/33RR20220501/oap_metadata_33RR20220501-2.xml"),
                new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/33RR20220501_A/33RR20220501/oap_metadata_33RR20220501_33RR20220614_33RR20221115-2.xml"),
                new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/33RR20220501_A/33RR20220501/oap_metadata_33RR20220501_33RR20220614_33RR20221115-3.xml"),
                new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/33RR20220501_A/33RR20220501/oap_metadata_33RR20220501_33RR20220614_33RR20221115-1.xml"),
                new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/33RR20220501_A/33RR20220501/oap_metadata_33RR20220501_33RR20220614_33RR20221115-4.xml"),
                new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/316420210420_D/316420210420/oap_metadata_316420210420-20230822.2.xml"),
                new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/316420210420_D/316420210420/oap_metadata_316420210420-20230822.1.xml"),
                new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/316420210420_D/316420210420/oap_metadata_316420210420.xml"),
                new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/WFCH20220424/WFCH20220424.xml"),
                new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/WFCH20220708/WFCH20220708.xml"),
                new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/316420191006_C/316420191006/316420191006_oads.xml"),
                new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/91AA20220712/91AA20220712/oap_metadata_from_upload_XLSX.xml"),
                new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/91AA20220712/91AA20220712/Metadata_Siyabulela_Hamnca-for-SOCAT.OADS.xml"),
                new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/91AA20220712/91AA20220712/oap_metadata_from_excel2oap.xml"),
                new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/316420100908_C/oads/me_ome2oads_metadata.xml"),
                new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/316420100908_C/316420100908/oap_metadata_316420100908.xml"),
                new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/oap_metadata_316420210617.xml"),
                new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/WFCH20220430/WFCH20220430.xml"),
                new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/flagfiles/32KZ20060516/oap_metadata_32KZ20060516.xml"),
                new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/316420210617_/316420210617/oap_metadata_316420210617.xml"),
                new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/316420210617_/316420210617/oap_metadata_316420210617-hazy.xml"),
                new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/316420210617_/316420210617/oap_metadata_316420210617-1.xml"),
                new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/316420210617_/316420210617/oap_metadata_316420210617-dev.xml"),
                new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/33WA20230623/33WA20230623_oads.xml"),
                new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/oap_metadata_316420210420.xml"),
                new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/WFCH20220623/WFCH20220623.xml"),
                new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/WFCH20220615/WFCH20220615.xml"),
                new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/42LK19651019/MetadataDocs/42LK/42LK19651019/42LK19651019.xml"),
                new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/WFCH_B/WFCH/WFCH20220424/oap_metadata_WFCH20220424.xml"),
                new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/32KZ20060516/32KZ20060516_oads.xml"),
                new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/oap_metadata_316420200911.xml")
            };
//            String expocode = "49UP20040423"; // "33HH20220301";
//            String expocode = "316420210617";// "WFCH20220424"; // "316420210617"; // "33RR20220501"; // "33HH20220301";
//            String expoDirName = "316420210617_/316420210617"; // "WFCH_B/WFCH/WFCH20220424"; // "316420210617_/" + expocode;
//            File dataBaseDir = new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/");
//            File dataDir = new File(dataBaseDir, expoDirName);
        
            JWhich.which(OadsMetadataDocumentType.class.getName());
        try {
                System.setProperty("CATALINA_BASE", "/Users/kamb/workspace/socat.PMEL/dev_content");
                System.setProperty("UPLOAD_DASHBOARD_SERVER_NAME", "SocatUploadDashboard");
                File resourcesDir = new File("/Users/kamb/workspace/socat.PMEL/dev_content/content/SocatUploadDashboard/config"); 
                MetadataFileHandler mdFileHandler = DashboardConfigStore.get(false).getMetadataFileHandler();
                DataFileHandler dataFileHandler = DashboardConfigStore.get(false).getDataFileHandler();
                OadsPdfGenerator opg = new OadsPdfGenerator(resourcesDir, mdFileHandler, dataFileHandler);
            for (File xmlFile : files) {
//                String ome_xmlFileName = expocode + "_PI_OME.xml";
//                String ome_xslFileName = "omefo.xsl";
//                String oads_xmlFileName = "oap_metadata_"+expocode+".xml";
                String oads_xslFileName = "oadsfo.xsl";
                
//                String xmlFileName = oads_xmlFileName;
                String xslFileName = oads_xslFileName;
                String pdf_prefix = xslFileName.substring(0, xslFileName.indexOf('.'));
                
    //            File dataBaseDir = new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/omeexamplefilesfromaoml/33HH20220301");
//                File xmlFile = new File(dataDir, xmlFileName);
                File pdfFile = new File(xmlFile.getParentFile(), pdf_prefix + "_generated.pdf");
                File xslFile = new File(resourcesDir, xslFileName);
                String expocode = getExpocode(xmlFile);
                try {
                    opg.createOadsPdf(expocode, xmlFile, pdfFile, xslFile);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            // TODO: handle exception
        } finally {
            System.out.println("done");
            DashboardConfigStore.shutdown();
            System.out.println("done done");
        }
    }

    /**
     * @param xmlFile
     * @return
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    private static String getExpocode(File xmlFile) throws FileNotFoundException, IOException {
        String expocode = "";
        try ( BufferedReader in = new BufferedReader(new FileReader(xmlFile))) {
            String line;
            while ( expocode.isEmpty() && (line = in.readLine()) != null ) {
                if (line.toLowerCase().contains("<expocode>")) {
                    int start = line.indexOf('>') +1;
                    if ( start < 0 ) {
                        return line;
                    }
                    line = line.substring(start);
                    int end = line.indexOf('<');
                    if ( end > 0 ) {
                        line = line.substring(0, end);
                    }
                    expocode = line;
                }
            }
        } 
        return expocode;
    }

}
