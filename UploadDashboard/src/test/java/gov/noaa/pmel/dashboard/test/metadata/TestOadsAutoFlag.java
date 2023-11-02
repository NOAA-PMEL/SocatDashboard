/**
 * 
 */
package gov.noaa.pmel.dashboard.test.metadata;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gov.noaa.ncei.oads.xml.v_a0_2_2s.OadsMetadataDocumentType;
import gov.noaa.pmel.dashboard.actions.OadsPdfGenerator;
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
import gov.noaa.pmel.tws.util.StringUtils;

/**
 * @author kamb
 *
 */
public class TestOadsAutoFlag {

    static final String OME_FLAG = "-o";
    static final String SDI_FLAG = "-s";
    static final String PDF_FLAG = "-p";
    static final String DS_ID_FLAG = "-i";
    
    private static final String OME_PREFIX = "ome_";
    private static final String OADS_PREFIX = "oads_";
    
    static void usage() {
        usage(null); // ??? Or usage(0); ???
    }
    static void usage(Integer exitCode) {
        System.out.println("TestOadsAutoFlag [options] -i <dataset_id> <oads_xml_file>");
        System.out.println("\t-i <dataset_id>\t: expected expocode for the dataset" );
        System.out.println("    Options:");
        System.out.println("\t-o <ome_xml_file>\t: comapre auto-flag result from OME metadata." );
        System.out.println("\t-p [pdf_file_name]\t: generate PDF." );
        System.out.println("\t\t\t\t- Default name from XML file name .pdf");
        System.out.println("\t-s [sdi_metadata_name]\t: save SDI (SOCAT) metadata as text.");
        System.out.println("\t\t\t\t- Default name from XML file name .sdi");
        if ( exitCode != null ) {
            System.exit(exitCode.intValue());
        }
    }
    static String getArg(String flag, List<String> argsList) {
        String arg = null;
        if ( argsList.contains(flag)) {
            int aIdx = argsList.indexOf(flag)+1;
            if ( argsList.size() > aIdx ) {
                arg = argsList.get(aIdx);
            }
        }
        return arg;
    }

    private static String getRelatedFileName(String filePath, String suffix) {
        return getRelatedFileName(filePath, suffix, null);
    }
    private static String getRelatedFileName(String filePath, String suffix, String prefix) {
        String dotSuffix = suffix.startsWith(".") ? suffix : "." + suffix;
        String baseName;
        String dirName = filePath.contains(File.separator) ? 
                         filePath.substring(0,filePath.lastIndexOf(File.separatorChar)+1) :
                         "";
        if (StringUtils.emptyOrNull(filePath)) {
            baseName = "generated";
        } else {
            String fileName = filePath.contains(File.separator) ? 
                              filePath.substring(filePath.lastIndexOf(File.separatorChar)+1) :
                              filePath;
            int idx = fileName.lastIndexOf('.');
            baseName = idx > 0 ? fileName.substring(0, idx) : fileName;
        }
        if ( ! StringUtils.emptyOrNull(prefix)) {
            baseName = prefix + baseName;
        }
        String relatedName = baseName + dotSuffix;
        return dirName + relatedName;
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        String oadsXmlFileName = null;
        String datasetId = null;
        File oadsXmlFile = null;
        String omeXmlFileName = null;
        File omeXmlFile = null;
        boolean doOme = false;
        String sdiFileName = null;
        File sdiFile = null;
        boolean doSdi = false;
        String pdfFileName = null;
        File pdfFile = null;
        boolean doPdf = false;
        
        if ( args.length < 3 ) {
            usage(1);
        }
        oadsXmlFileName = args[args.length-1];
        oadsXmlFile = new File(oadsXmlFileName);
        if ( !oadsXmlFile.exists()) {
            System.err.println(oadsXmlFile.getAbsolutePath() + " NOT FOUND.");
            usage(-1);
        }
        
        List<String> argsList = new ArrayList<>(Arrays.asList(args));
        argsList.remove(argsList.size()-1); // remove oads_xml_file
        int dsIdx = argsList.indexOf(DS_ID_FLAG);
        if ( dsIdx < 0 ) {
            System.err.println("Missing dataset Id flag.");
            usage(-2);
        }
        if ( argsList.size() < dsIdx+1 || (datasetId = argsList.get(dsIdx+1)).startsWith("-")) {
            System.err.println("Missing dataset Id.");
            usage(-2);
        }
        if ( args.length > 2) {
            int oIdx = argsList.indexOf(OME_FLAG);
            if ( oIdx >= 0 ) {
                if ( argsList.size() > oIdx+1 && !(omeXmlFileName = argsList.get(oIdx+1)).startsWith("-")) {
                    omeXmlFileName = argsList.get(oIdx+1);
                    omeXmlFile = new File(omeXmlFileName);
                    doOme = true;
                } else {
                    usage(2);
                }
            }
            int pIdx = argsList.indexOf(PDF_FLAG);
            if ( pIdx >= 0 ) {
                if ( argsList.size() > pIdx+1 && !(pdfFileName = argsList.get(pIdx+1)).startsWith("-")) {
                    pdfFileName = argsList.get(pIdx+1);
                } else {
                    pdfFileName = getRelatedFileName(oadsXmlFileName, ".pdf");
                }
                pdfFile = new File(pdfFileName);
                doPdf = true;
            }
            int sIdx = argsList.indexOf(SDI_FLAG);
            if ( sIdx >= 0 ) {
                if ( argsList.size() > sIdx+1 && !(sdiFileName = argsList.get(sIdx+1)).startsWith("-")) {
                    sdiFileName = argsList.get(sIdx+1);
                } else {
                    sdiFileName = getRelatedFileName(oadsXmlFileName, ".sdi");
                }
                sdiFile = new File(sdiFileName);
                doSdi = true;
            }
        }
        System.out.println("Testing OADS file " + oadsXmlFile);
        try {
            OadsMetadataDocumentType doc = OadsXmlReader.read(oadsXmlFile);
            OadsOmeMetadata oadsMetadata = new OadsOmeMetadata();
            oadsMetadata.read(datasetId, oadsXmlFile);
            OadsSocatUtils osu = new OadsSocatUtils(oadsMetadata);
            SocatMetadata oadsSdi = osu.createSocatMetadata();
            try {
                DatasetQCStatus oadsStatus = OadsUtils.suggestDatasetQCFlag(oadsSdi, null);
                System.out.println("OADS autoQC status: " + oadsStatus);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if ( doSdi ) {
                System.out.println("Writing SDI to " + sdiFile.getAbsolutePath());
                try ( FileWriter out = new FileWriter(sdiFile)) {
                    out.write(oadsSdi.toString());
                    out.flush();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if ( doPdf ) {
                System.out.println("Writing PDF to " + pdfFile.getAbsolutePath());
                try {
                    OadsPdfGenerator opg = new OadsPdfGenerator();
                    File xslFile = getOadsXslFile();
                    opg.createOadsPdf(pdfFileName, oadsXmlFile, pdfFile, xslFile);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if ( doOme ) {
                System.out.println("Testing OME file " + omeXmlFile);
                try ( FileReader omeReader = new FileReader(omeXmlFile); ) {
                    SocatMetadata omeSdi = OmeUtils.createSdiMetadataFromCdiacOme(omeReader, null, null);
                }
                
                // ...
                if ( doPdf ) {
                    // get OME pdf name
                    // ...
                }
                if ( doSdi ) {
                    // ...
                }
            }
//            DashboardDatasetData dd2 = DashboardConfigStore.get(false)
//                                        .getDataFileHandler()
//                                        .assignDatasetDataFromInput(null, dataReader2,  dataFileFormat,
//                                                                    "me", 0, 10);
////            DatasetQCStatus status = OadsUtils.suggestDatasetQCFlag(doc, sdi, ddd, cdr);
//            System.out.println("OADS Status: "+ oadsStatus);
////            if ( !omeStatus.equals(oadsStatus) ) {
////                System.out.println("***** OmeUtils and Oads QC Status differ! ");
////            }
////            if ( !omeOadsStatus.equals(oadsStatus) ) {
////                System.out.println("***** OmeOadsUtils and Oads QC Status differ! ");
////            }
        } catch (Exception ex) {
            System.err.println("Failed to handle input XML:"+ex);
            ex.printStackTrace();
        }
    }
    
    /**
     * @return
     */
    private static File getOadsXslFile() {
        return new File("/Users/kamb/workspace/socat.PMEL/dev_content/content/SocatUploadDashboard/config/oadsfo.xsl");
    }

}
