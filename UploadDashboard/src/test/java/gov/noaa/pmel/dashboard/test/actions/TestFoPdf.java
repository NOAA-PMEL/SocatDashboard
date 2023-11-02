/**
 * 
 */
package gov.noaa.pmel.dashboard.test.actions;

import org.apache.fop.apps.FopFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URI;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.Fop;
import org.apache.fop.apps.MimeConstants;

/**
 * 
 */
public class TestFoPdf {

    /**
     * @param args
     */
    public static void main(String[] args) {
        String foFileName = "/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/xtra/316420210617_/316420210617/oap_metadata_316420210617_fo.xml";
        File foFile = new File(foFileName);
        String pdfFileName = foFileName.substring(0, foFileName.lastIndexOf('.'))+".pdf";
        File pdfFile = new File(pdfFileName);
     try ( OutputStream out = new BufferedOutputStream(new FileOutputStream(pdfFile)); ){
         // Step 1: Construct a FopFactory by specifying a reference to the configuration file
         // (reuse if you plan to render multiple documents!)
         FopFactory fopFactory = FopFactory.newInstance(new URI("file://Users/kamb/workspace/socat.PMEL/dev_content/content/SocatUploadDashboard/config"));
    
         // Step 2: Set up output stream.
         // Note: Using BufferedOutputStream for performance reasons (helpful with FileOutputStreams).
         
    
             // Step 3: Construct fop with desired output format
             Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);
    
             // Step 4: Setup JAXP using identity transformer
             TransformerFactory factory = TransformerFactory.newInstance();
             Transformer transformer = factory.newTransformer(); // identity transformer
    
             // Step 5: Setup input and output for XSLT transformation
             // Setup input stream
             Source src = new StreamSource(foFile);
    
             // Resulting SAX events (the generated FO) must be piped through to FOP
             Result res = new SAXResult(fop.getDefaultHandler());
    
             // Step 6: Start XSLT transformation and FOP processing
             transformer.transform(src, res);
         } catch (Exception ex) {
             ex.printStackTrace();
             System.out.println(ex.getCause());
         }
    }

}
