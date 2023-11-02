/**
 * 
 */
package gov.noaa.pmel.dashboard.test.actions;

import java.io.File;

import gov.noaa.ncei.oads.xml.OadsMetadataDocument;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.OadsMetadataDocumentType;
import gov.noaa.pmel.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.dashboard.metadata.DashboardOmeMetadata;
import gov.noaa.pmel.dashboard.metadata.OmeUtils;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.dashboard.shared.DatasetQCStatus;
import gov.noaa.pmel.oads.xml.a0_2_2.OadsXmlReader;
import gov.noaa.pmel.socatmetadata.SocatMetadata;
import gov.noaa.pmel.tws.util.JWhich;

/**
 * 
 */
public class TestAutoFlag {

    /**
     * @param args
     */
    public static void main(String[] args) {
        
        String metadataFile = "";
        String datasetId = "";
        try {
            File xmlFile = new File(metadataFile);
//            MetadataFileHandler metadataHandler = DashboardConfigStore.get(false).getMetadataFileHandler();
//            DashboardOmeMetadata omedata = metadataHandler.getOmeFromFile(metadata);
            OadsMetadataDocumentType omd = OadsXmlReader.read(xmlFile);
            SocatMetadata sdiMetadata = null;
            JWhich.which(SocatMetadata.class.getName());
            DatasetQCStatus status = OmeUtils.suggestDatasetQCFlag(sdiMetadata);
            System.out.println("Status for "+ metadataFile +":" + status);

        } catch (Exception ex) {
            ex.printStackTrace();
            // TODO: handle exception
        }

    }

}
