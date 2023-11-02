/**
 * 
 */
package gov.noaa.pmel.dashboard.actions;

import java.io.IOException;

/**
 * @author kamb
 *
 */
public interface PdfGenerator {

    public void createPdf(String expocode) throws IllegalArgumentException, IOException;
}
