/**
 *
 */
package gov.noaa.pmel.dashboard.actions;

import gov.noaa.pmel.dashboard.handlers.DataFileHandler;
import gov.noaa.pmel.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.dashboard.metadata.OmeMetadataInterface;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.tws.util.JWhich;

import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;

/**
 * Converts OME XML documents to a human-friendly PDF format.
 *
 * @author Karl Smith
 */
public class OadsPdfGenerator implements PdfGenerator {

    private MetadataFileHandler metadataHandler;
    private DataFileHandler cruiseHandler;
    private File fopResourcesDir;
//    private File xsltFile;
    private FopFactory fopFactory;
    private TransformerFactory transFactory;

    public OadsPdfGenerator() {
        // Because you shouldn't need all that other crap just to generate the PDF file...
    }
    /**
     * Converts OME XML documents provided by the given metadata file handler to a human-friendly PDF format.  Uses the
     * "omexml.xsl" stylesheet file, along with any other required resource files, in the given resources directory to
     * format the OME XML.
     *
     * @param resourcesDir
     *         directory containing the omexml.xsl stylesheet file as well as and other required Fop resource files
     * @param metaFileHandler
     *         handler for dashboard OME metadata files
     * @param cruiseFileHandler
     *         handler for dashboard cruise files - for adding the PDF as an additional document to a dataset; can be
     *         null, in which case this final step after generating the PDF will not be performed.
     *
     * @throws IllegalArgumentException
     *         if resourcesDir does not exist or is not a directory, or if the omexml.csl file under resourcesDir does
     *         not exist, or if metaFileHandler is null
     * @throws IOException
     *         if FopFactory.newInstance fails, or if the TranformerFactor.newIntance fails
     */
    public OadsPdfGenerator(File resourcesDir, MetadataFileHandler metaFileHandler,
            DataFileHandler cruiseFileHandler) throws IllegalArgumentException, IOException {
        if ( metaFileHandler == null )
            throw new IllegalArgumentException("MetadataFileHandler passed to OmePdfGenerator is null");
        metadataHandler = metaFileHandler;
        cruiseHandler = cruiseFileHandler;
        if ( !resourcesDir.isDirectory() )
            throw new IOException("Does not exist or is not a directory: " + resourcesDir.getPath());
        fopResourcesDir = resourcesDir;
//        xsltFile = new File(fopResourcesDir, "omefo.xsl");
//        if ( !xsltFile.canRead() )
//            throw new IllegalArgumentException("Cannot read XSL file: " + xsltFile.getPath());
        try {
            fopFactory = FopFactory.newInstance(fopResourcesDir.toURI());
        } catch ( Throwable ex ) {
            throw new IOException("Unable to create the FopFactory: " + ex.getMessage());
        }
        try {
            transFactory = TransformerFactory.newInstance();
        } catch ( Throwable ex ) {
            throw new IOException("Unable to create the TransformerFactory: " + ex.getMessage());
        }
    }

    public void createPdf(String expocode) throws IllegalArgumentException, IOException {
        createOadsPdf(expocode);
    }
    /**
     * Convert the PI-provided OME XML file for the given dataset to a human-friendly PDF file.
     *
     * @param expocode
     *         convert the PI-provided OME XML file for this dataset
     *
     * @throws IllegalArgumentException
     *         if the dataset is invalid, or if the PI-provided OME XML file does not exist, or if unable to add the PDF
     *         as an additional document to the dataset (if a DataFileHandler was provided in the constructor)
     * @throws IOException
     *         if the conversion fails
     */
    public void createOadsPdf(String expocode) throws IllegalArgumentException, IOException {
        String upperExpo = DashboardServerUtils.checkDatasetID(expocode);
        // Get the full path filename for the PI_OME.xml file
        File xmlFile = metadataHandler.getMetadataFile(upperExpo, DashboardUtils.PI_OME_FILENAME);
        if ( !xmlFile.exists() )
            throw new IllegalArgumentException("PI-provided OME XML file does not exist for " + upperExpo);
        // Get the information about this file
        DashboardMetadata mdata = metadataHandler.getMetadataInfo(upperExpo, DashboardUtils.PI_OME_FILENAME);
        // Get the full path filename for the PI_OME.pdf file
        File pdfFile = metadataHandler.getMetadataFile(upperExpo, DashboardUtils.PI_OME_PDF_FILENAME);
        // Output stream for the PDF that will be generated
        
        Class<? extends OmeMetadataInterface> mdClass = MetadataFileHandler.getMetadataClass(xmlFile);
        OmeMetadataInterface mdi;
        try {
            mdi = mdClass.getDeclaredConstructor().newInstance();
            String xsltFileName = mdi.getOmePdfXsltFileName();
            File xsltFile = new File(fopResourcesDir, xsltFileName);
            createOadsPdf(upperExpo, xmlFile, pdfFile, xsltFile);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException ex) {
            throw new IOException("Failed to generate OME PDF document for expocode " + expocode, ex);
        }
        
        // Add a properties file for the successfully generated PDF
        mdata.setFilename(DashboardUtils.PI_OME_PDF_FILENAME);
        // Commit the PDF to version control and save/commit the properties file for the PDF
        metadataHandler.saveMetadataInfo(mdata, upperExpo +
                ": PI_OME.pdf generated from the PI_OME.xml file", true);
        // Add the PDF as an additional document for this dataset
        if ( cruiseHandler != null ) {
            cruiseHandler.addAddlDocTitleToDataset(upperExpo, mdata);
        }
    }
    
    public void createOadsPdf(String upperExpo, File xmlFile, File pdfFile, File xsltFile) throws IOException {
        try ( BufferedOutputStream pdfOut = new BufferedOutputStream(new FileOutputStream(pdfFile)); ) {
            // Get the Fop for converting XSL-FORMAT into PDF to be written to the PDF output stream
            System.out.println(xmlFile +" : " + pdfFile);
            FOUserAgent foUserAgent;
            try {
                fopFactory = FopFactory.newInstance(xsltFile.getParentFile().toURI());
            } catch ( Throwable ex ) {
                throw new IOException("Unable to create the FopFactory: " + ex.getMessage());
            }
            try {
                foUserAgent = fopFactory.newFOUserAgent();
                foUserAgent.setTitle(upperExpo + " OADS Metadata");
            } catch ( Throwable ex ) {
                throw new IOException("Unable to create the FOUserAgent: " + ex.getMessage());
            }
            Fop fop;
            try {
                fop = foUserAgent.newFop(MimeConstants.MIME_PDF, pdfOut);
            } catch ( Throwable ex ) {
                throw new IOException("Unable to create the Fop: " + ex.getMessage());
            }
            // Get the Transformer to convert the PI_OME.xml to XSL-FORMAT using the omefo.xsl stylesheet
            StreamSource src = new StreamSource(xmlFile);
            Transformer transformer;
            try {        
                if ( !xsltFile.canRead() )
                    throw new IllegalArgumentException("Cannot read XSL file: " + xsltFile.getPath());
                try {
                    transFactory = TransformerFactory.newInstance();
                } catch ( Throwable ex ) {
                    throw new IOException("Unable to create the TransformerFactory: " + ex.getMessage());
                }
                transformer = transFactory.newTransformer(new StreamSource(xsltFile));
//                JWhich.which(transformer.getClass().getName());
            } catch ( Throwable ex ) {
                throw new IOException("Unable to create the Tranformer: " + ex.getMessage());
            }
            transformer.setParameter("versionParam", "2.0");
            // Create the Transformer output to go to the Fop input
            SAXResult res;
            try {
                res = new SAXResult(fop.getDefaultHandler());
            } catch ( Throwable ex ) {
                throw new IOException("Unable to get the default fop handler: " + ex.getMessage());
            }
            // Perform the transform, which then triggers the Fop to generate the PDF
            try {
                boolean dumpFo = false;  // XXX Debugging
                if ( dumpFo ) {
                    dumpMoFo(xmlFile, xsltFile);
                }
                transformer.transform(src, res);
            } catch ( Throwable ex ) {
                dumpMoFo(xmlFile, xsltFile);
                throw new IOException("Unable to to transform: " + ex.getMessage(), ex);
            }
        } catch ( Exception ex ) {
            System.out.println(xmlFile + ":"+ex);
//            if ( xmlFile.exists()) {
//                xmlFile.delete();
//            }
            throw new IOException("Cannot create a new PDF for the PI-provided OME: " + ex.getMessage());
        }
    }
    /**
     * @param xmlFile
     * @param xsltFile
     */
    private static void dumpMoFo(File xmlFile, File xsltFile) {
        String outFileName = xmlFile.getName().replace(".xml", "_fo.xml");
        File outFile = new File(xmlFile.getParent(), outFileName);
        System.out.println("using XSL file " + xsltFile.getAbsolutePath() + 
                           " to output FO file " + outFile.getAbsolutePath());
        try ( InputStream inXml = new FileInputStream(xmlFile);
              OutputStream outXfrm = new FileOutputStream(outFile); ) {
            TransformerFactory xfrmFactory = TransformerFactory.newInstance();
            Transformer xfrm = xfrmFactory.newTransformer(new StreamSource(xsltFile));
            xfrm.setParameter("versionParam", "2.0");
            JWhich.which(xfrm.getClass().getName());
            xfrm.transform(new StreamSource(inXml), new StreamResult(outXfrm));
        } catch (Exception ex) {
            System.out.println("Exception outputting FO file.");
            ex.printStackTrace();
        }
    }
}
