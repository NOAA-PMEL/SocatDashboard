/**
 * 
 */
package gov.noaa.pmel.dashboard.actions;

import java.io.File;
import java.io.IOException;

import gov.noaa.pmel.dashboard.actions.FileXferService.XFER_PROTOCOL;

/**
 * @author kamb
 *
 */
public class CpTransfer extends BaseTransferAgent implements FileTransferOp {

    
    public CpTransfer() {
        super(XFER_PROTOCOL.CP);
    }
    /* (non-Javadoc)
     * @see gov.noaa.pmel.dashboard.handlers.FileTransferOp#getTransferCommand()
     */
    @Override
    public String getTransferCommand(File transferFile, String targetFilePath) throws Exception {
        String destDir = getTargetDestinationDir(targetFilePath);
        return buildCommand(transferFile, destDir);
    }
    
    private String buildCommand(File transferFile, String destDir) throws IOException {
        StringBuilder transferCmd = new StringBuilder();
        String command = "cp";
        transferCmd.append("([ -e ").append(destDir).append(" ] || mkdir -p ").append(destDir).append(" ) && ")
                   .append(command).append(SPACE)
                   .append(transferFile.getCanonicalPath()).append(SPACE)
                   .append(destDir);

        return transferCmd.toString();
    }

}
