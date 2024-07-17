/**
 * 
 */
package gov.noaa.pmel.dashboard.actions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gov.noaa.pmel.oads.util.StringUtils;
import gov.noaa.pmel.tws.util.ApplicationConfiguration;
import gov.noaa.pmel.tws.util.ApplicationConfiguration.ConfigurationException;
import gov.noaa.pmel.tws.util.process.CommandRunner;

/**
 * @author kamb
 *
 */
public class FileXferService {

    private static Logger logger = LogManager.getLogger(FileXferService.class);
    
    private XFER_PROTOCOL _protocol;
    private FileTransferOp _transferOp;
    
    public static enum XFER_PROTOCOL {
        NONE, // do not send bundle
        EMAIL,
        SFTP,
        SCP,
        CP;
        
        public String value() {
            return this.name().toLowerCase();
        }
        public static XFER_PROTOCOL from(String name) {
            return XFER_PROTOCOL.valueOf(name.toUpperCase());
        }
    }
    
    static {
    	try {
    		File configDirFile = null;
    		String CATALINA_BASE = System.getProperty("CATALINA_BASE");
    		if ( !StringUtils.emptyOrNull(CATALINA_BASE)) {
	    		String configDir = CATALINA_BASE+"/content/SocatUploadDashboard/config";
	    		configDirFile = new File(configDir);
    		} else {
    			configDirFile = new File("../../content/SocatUploadDashboard/config");
    		}
    		if ( configDirFile.exists()) {
				ApplicationConfiguration.Initialize(configDirFile, "oap");
    		} else {
				ApplicationConfiguration.Initialize("oap");
    		}
		} catch (ConfigurationException e) {
			e.printStackTrace();
			System.err.println("WARNING!! Failed to initialize ApplicationConfiguration: " + e);
			System.err.println("**** Failed to initialize ApplicationConfiguration.\n"
							 + "Consider adding -Dconfiguration_dir=<path_to_configuration_dir> OR -DCATALINA_BASE=<path_to_tomcat_root>\n"
							 + "to the command line.\n"
							 + "For example: java -Dconfiguration_dir=content/SocatUploadDashboard/config -j sftpXfer.jar <file>");
		}
    }
        
    /**
     * @param stdId
     * @param archiveBundle
     * @param userRealName
     * @param userEmail
     * @throws Exception 
     */
    public static String putArchiveBundle(String datasetId, File archiveBundle) throws Exception {
//                                       , String userRealName, String userEmail) throws Exception {
        System.out.println("Submitting " + datasetId + " archive bundle " + archiveBundle + " to FTP site." );
        return new FileXferService().submitArchiveBundle(datasetId, 
//                                                         String.valueOf(submitRec.version()), 
                                                         archiveBundle); // , userRealName, userEmail);
    }

    public FileXferService(XFER_PROTOCOL protocol) {
        _protocol = protocol;
        _transferOp = getFileTransferOp(_protocol);
    }
    private FileXferService() {
        _protocol = XFER_PROTOCOL.from(ApplicationConfiguration.getProperty("oap.archive.mode", XFER_PROTOCOL.SFTP.name()));
        _transferOp = getFileTransferOp(_protocol);
    }
    
    /**
     * @param forProtocol
     * @return
     */
    private static FileTransferOp getFileTransferOp(XFER_PROTOCOL forProtocol) {
        switch (forProtocol) {
            case SFTP:
                return new SftpTransfer();
            case SCP:
                return new ScpTransfer();
            case CP:
                return new CpTransfer();
            default:
                throw new IllegalStateException("Unknown protocol:" + forProtocol);
        }
    }

    public String submitArchiveBundle(String stdId, 
//    								  String version, 
    								  File archiveBundle)  throws Exception {
        String targetDir = stdId + "/" ; //  + "/" + version + "/";
        String targetFile = stdId + "_bagit.zip";
        String targetFilePath = targetDir + targetFile;
        String command = _transferOp.getTransferCommand(archiveBundle, targetFilePath);
        logger.debug("xfer cmd: " + command);
        try ( ByteArrayOutputStream out = new ByteArrayOutputStream();
	          ByteArrayOutputStream err = new ByteArrayOutputStream(); ) {
	        CommandRunner runner = new CommandRunner(command, out, err);
	        int exitStatus = runner.runCommand();
	        String cmdOut = out.toString("utf8");
	        logger.debug("command output: "+ cmdOut);
	        String cmdErr = err.toString("utf8");
	        logger.debug("command error output: "+ cmdErr);
	        if ( exitStatus != 0 ) {
	            throw new Exception("Failed to transfer bagit file " + archiveBundle.getPath() 
	            					+ " : " + cmdErr);
	        }
            String hashFilePath = targetDir + stdId + "_bagit-sha256.txt";
            exitStatus = submitHash(archiveBundle, hashFilePath);
            if ( exitStatus != 0 ) {
            	logger.warn("**** Failed to transfer hash file.");
//                throw new Exception("Failed to transfer hash file for bag " + archiveBundle.getPath());
	        }
	        return targetFilePath;
        }
    }
        
    /**
     * @param archiveBundle
     * @return
     */
    private int submitHash(File archiveBundle, String hashFilePath) throws Exception {
        String fname = archiveBundle.getName();
        String fbase = fname.substring(0, fname.lastIndexOf('.'));
        File hashFile = new File(archiveBundle.getParentFile(), fbase+"-sha256.txt");
        String command = _transferOp.getTransferCommand(hashFile, hashFilePath);
        logger.debug("xfer cmd: " + command);
        CommandRunner runner = new CommandRunner(command);
        int exitStatus = runner.runCommand();
        return exitStatus;
    }

    public static void usage(Integer exit) {
		System.err.println("Usage: FileXfer <file> [expocode]");
		System.err.println("       Expocode required if filename not in format <expocode[_platform?]_bagit.zip");
		if ( exit != null ) {
			System.exit(exit);
		}
    }
    	
    public static void main(String[] args) {
        try {
        	if ( args.length == 0 ) {
        		usage(-1);
        	}
        	String filename = args[0];
        	File archiveBundle = new File(filename);
        	if ( !archiveBundle.exists()) {
        		throw new FileNotFoundException(archiveBundle + " not found.");
        	}
        	String bundleName = archiveBundle.getName();
        	String expocode = null;
        	if ( bundleName.contains("_bagit")) {
        		expocode = bundleName.substring(0, bundleName.indexOf('_'));
        	} else if ( args.length == 2 ) {
        		expocode = args[1];
        	} else {
        		throw new IllegalArgumentException("No expocode provided.");
        	}
//                File archiveBundle = new File("/local/tomcat/oap_content/OAPUploadDashboard/MetadataDocs/NEMO/NEMOPRE052012/extracted_NEMOPRE052012_OADS.xml");
            new FileXferService(XFER_PROTOCOL.SFTP).submitArchiveBundle(expocode, archiveBundle);
//                new FileXferService(XFER_PROTOCOL.SCP).submitArchiveBundle("datasetID", archiveBundle, "Real Name", "real.name@noaa.gov");
//                new FileXferService(XFER_PROTOCOL.CP).submitArchiveBundle("datasetID", archiveBundle, "Real Name", "real.name@noaa.gov");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-99);
        }
    }

}
