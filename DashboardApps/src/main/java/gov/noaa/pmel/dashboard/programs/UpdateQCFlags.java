package gov.noaa.pmel.dashboard.programs;

import gov.noaa.pmel.dashboard.handlers.DatabaseRequestHandler;
import gov.noaa.pmel.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.shared.DatasetQCStatus;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.TreeSet;

/**
 * Updates the QC flags in the full-data and decimated-data DSG files for cruises
 * to the flag obtained from the database.
 *
 * @author Karl Smith
 */
public class UpdateQCFlags {

    /**
     * @param args
     *         ExpocodesFile - update QC flags of these cruises
     */
    public static void main(String[] args) {
        if ( args.length != 1 ) {
            System.err.println("Arguments:  ExpocodesFile");
            System.err.println();
            System.err.println("Updates the QC flags in the full-data and decimated-data DSG ");
            System.err.println("files for cruises specified in ExpocodesFile to the flag obtained ");
            System.err.println("from the database.  The default dashboard configuration is ");
            System.err.println("used for this process. ");
            System.err.println();
            System.exit(1);
        }

        String expocodesFilename = args[0];

        boolean success = true;
        boolean updated = false;

        // Get the default dashboard configuration
        DashboardConfigStore configStore = null;
        try {
            configStore = DashboardConfigStore.get(false);
        } catch ( Exception ex ) {
            System.err.println("Problems reading the default dashboard configuration file: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }
        try {
            // Get the expocode of the cruises to update
            TreeSet<String> allExpocodes = new TreeSet<String>();
            try {
                BufferedReader expoReader =
                        new BufferedReader(new FileReader(expocodesFilename));
                try {
                    String dataline = expoReader.readLine();
                    while ( dataline != null ) {
                        dataline = dataline.trim();
                        if ( !(dataline.isEmpty() || dataline.startsWith("#")) )
                            allExpocodes.add(dataline);
                        dataline = expoReader.readLine();
                    }
                } finally {
                    expoReader.close();
                }
            } catch ( Exception ex ) {
                System.err.println("Error getting expocodes from " + expocodesFilename + ": " + ex.getMessage());
                ex.printStackTrace();
                System.exit(1);
            }

            DatabaseRequestHandler dbHandler = configStore.getDatabaseRequestHandler();
            DsgNcFileHandler dsgHandler = configStore.getDsgNcFileHandler();

            // update each of these cruises
            for (String expocode : allExpocodes) {
                DatasetQCStatus qcStatus;
                String qcFlag;
                String versionStatus;
                try {
                    qcStatus = dbHandler.getDatasetQCFlag(expocode);
                    qcFlag = qcStatus.dsgFlagString();
                    versionStatus = dbHandler.getVersionStatus(expocode);
                } catch ( Exception ex ) {
                    System.err.println("Error getting the database QC flag or version with status for " +
                            expocode + " : " + ex.getMessage());
                    success = false;
                    continue;
                }
                String oldQCFlag;
                String oldVersionStatus;
                try {
                    String[] oldFlagVersionStatus = dsgHandler.getDatasetQCFlagAndVersionStatus(expocode);
                    oldQCFlag = oldFlagVersionStatus[0];
                    oldVersionStatus = oldFlagVersionStatus[1];
                } catch ( Exception ex ) {
                    System.err.println("Error reading the current DSG QC flag or version with status for " +
                            expocode + " : " + ex.getMessage());
                    success = false;
                    continue;
                }
                try {
                    if ( !(qcFlag.equals(oldQCFlag) && versionStatus.equals(oldVersionStatus)) ) {
                        // Update the QC flag in the DSG files
                        dsgHandler.updateDatasetQCFlagAndVersionStatus(expocode, qcStatus, versionStatus);
                        System.out.println("Updated QC flag for " + expocode + " from '" + oldQCFlag +
                                "', v" + oldVersionStatus + " to '" + qcFlag + "', v" + versionStatus);

                        updated = true;
                    }
                } catch ( Exception ex ) {
                    System.err.println("Error updating the QC flag in the DSG files for " +
                            expocode + " : " + ex.getMessage());
                    success = false;
                }
            }
            if ( updated ) {
                dsgHandler.flagErddap(true, true);
            }
        } finally {
            DashboardConfigStore.shutdown();
        }
        if ( !success )
            System.exit(1);
        System.exit(0);
    }

}
