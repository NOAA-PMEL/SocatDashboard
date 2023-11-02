/**
 * 
 */
package gov.noaa.pmel.dashboard.test.metadata;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import gov.noaa.ncei.oads.xml.v_a0_2_2s.OadsMetadataDocumentType;
import gov.noaa.pmel.dashboard.metadata.OadsOmeMetadata;
import gov.noaa.pmel.dashboard.metadata.OadsSocatUtils;
import gov.noaa.pmel.dashboard.metadata.OadsUtils;
import gov.noaa.pmel.dashboard.metadata.OmeUtils;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.shared.DashboardDatasetData;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DatasetQCStatus;
import gov.noaa.pmel.oads.xml.a0_2_2.OadsXmlReader;
import gov.noaa.pmel.socatmetadata.SocatMetadata;
import gov.noaa.pmel.socatmetadata.translate.CdiacReader;

/**
 * @author kamb
 *
 */
public class TestOadsUtils {

    /**
     * @param args
     */
    public static void main(String[] args) {
//        File omeXmlFile = new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/omeexamplefilesfromaoml/33HH20210512/33HH20210512_PI_OME.xml");
//        File oadsXmlFile = new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/omeexamplefilesfromaoml/33HH20210512/33HH20210512_ME-oads.xml");
//        File dataFile = new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/omeexamplefilesfromaoml/33HH20210512/bagit/data/33HH20210512.csv");
//        String format = DashboardUtils.COMMA_FORMAT_TAG;
        File omeXmlFile = new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/omeexamplefilesfromaoml/33HH20220301/33HH20220301_PI_OME.xml");
//        File oadsXmlFile = new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/omeexamplefilesfromaoml/33HH20220301/oap_metadata_33HH20220301-latest.xml");
        File oadsXmlFile = new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/33RR20220501/33RR20220501/Metadata_SOCAT_Revelle.excel2OADS.xml");
        File dataFile = new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/omeexamplefilesfromaoml/33HH20220301/33HH20220301.tsv");
        File fileDir = oadsXmlFile.getParentFile();
        File omeCdrSdiFile = new File(fileDir, "ome_cdr_sdi.text");
        File oadsSdiFile = new File(fileDir, "oads_sdi.text");
        String dataFileFormat = DashboardUtils.TAB_FORMAT_TAG;
        try ( FileReader omeReader = new  FileReader(omeXmlFile);
              FileReader dataReader1 = new FileReader(dataFile);
              FileReader dataReader2 = new FileReader(dataFile); ) {
            CdiacReader cdr = new CdiacReader(omeReader);
//            SocatMetadata omeCdrSdi = cdr.createSocatMetadata();
//            try ( FileWriter out = new FileWriter(omeCdrSdiFile)) {
//                out.write(omeCdrSdi.toString());
//                out.flush();
//            }
            DashboardDatasetData ddd = DashboardConfigStore.get(false)
                    .getDataFileHandler()
                    .assignDatasetDataFromInput(null, dataReader1,  dataFileFormat,
                                                "me", 0, 10);
            //DatasetQCStatus status = OadsUtils.suggestDatasetQCFlag(doc, sdi, ddd, cdr);
//            DatasetQCStatus omeStatus = OmeUtils.suggestDatasetQCFlag(omeCdrSdi, ddd);
//            System.out.println("OME Status: "+ omeStatus);
//            DatasetQCStatus omeOadsStatus = OadsUtils.suggestDatasetQCFlag(omeCdrSdi, ddd);
//            System.out.println("OME OADSUtils Status: "+ omeOadsStatus);
//            if ( !omeStatus.equals(omeOadsStatus) ) {
//                System.out.println("***** OmeUtils and OadsUtils QC Status differ! ");
//            }

            OadsMetadataDocumentType doc = OadsXmlReader.read(oadsXmlFile);
            OadsOmeMetadata oadsMetadata = new OadsOmeMetadata();
            oadsMetadata.read("33RR20220501", oadsXmlFile);
            OadsSocatUtils osu = new OadsSocatUtils(oadsMetadata);
            SocatMetadata oadsSdi = osu.createSocatMetadata();
//            try ( FileWriter out = new FileWriter(oadsSdiFile)) {
//                out.write(oadsSdi.toString());
//                out.flush();
//            }
            DashboardDatasetData dd2 = DashboardConfigStore.get(false)
                                        .getDataFileHandler()
                                        .assignDatasetDataFromInput(null, dataReader2,  dataFileFormat,
                                                                    "me", 0, 10);
//            DatasetQCStatus status = OadsUtils.suggestDatasetQCFlag(doc, sdi, ddd, cdr);
            DatasetQCStatus oadsStatus = OadsUtils.suggestDatasetQCFlag(oadsSdi, dd2);
            System.out.println("OADS Status: "+ oadsStatus);
//            if ( !omeStatus.equals(oadsStatus) ) {
//                System.out.println("***** OmeUtils and Oads QC Status differ! ");
//            }
//            if ( !omeOadsStatus.equals(oadsStatus) ) {
//                System.out.println("***** OmeOadsUtils and Oads QC Status differ! ");
//            }
        } catch (Exception ex) {
            ex.printStackTrace();
            // TODO: handle exception
        }

    }

}
