/**
 * 
 */
package gov.noaa.pmel.dashboard.test.metadata;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import gov.noaa.pmel.dashboard.metadata.CdiacOmeMetadata;
import gov.noaa.pmel.dashboard.metadata.DashboardOmeMetadata;
import gov.noaa.pmel.dashboard.metadata.OadsOmeMetadata;
import gov.noaa.pmel.dashboard.metadata.OmeMetadataInterface;
import gov.noaa.pmel.dashboard.shared.DashboardDataset;

/**
 * @author kamb
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CompareOmeCdiac2OadsTest {

    static String datasetId = "33HH20220301";
    static String omeFileName = datasetId+"_PI_OME.xml";
    static String dataDirName = "/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/omeexamplefilesfromaoml/"+datasetId;
    static File dataDir = new File(dataDirName);
    static File omeFile = new File(dataDir, omeFileName);
    
//    static String convertedFileName = datasetId+"_OCADS_Cdiac2OocadsConverted.xml";
    
    static String devMeOadsFileName = datasetId+"_OADS_ME_dev.xml";
    static String prodMeOadsFileName = datasetId+"_OADS_ME_prod.xml";
    static String oadsFileName = devMeOadsFileName;
    
    static File oadsFile = new File(dataDir, oadsFileName);
    static OadsOmeMetadata oads;
    static CdiacOmeMetadata cdiac;
    
    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        cdiac = new CdiacOmeMetadata();
        cdiac.read(datasetId, omeFile);
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        oads = new OadsOmeMetadata(true);
        oads.read(datasetId, oadsFile);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    private void fullTest(OmeMetadataInterface oads) throws Exception {
        StringBuilder errors = new StringBuilder();
    }
    @Test
	public void testGetDataEndTime() {
        Double cdEnd = cdiac.getDataEndTime();
        Double oaEnd = oads.getDataEndTime();
        Date cdEndDate = new Date((long)(cdEnd.doubleValue()*1000));
        Date oaEndDate = new Date((long)(oaEnd.doubleValue()*1000));
        assertEquals("EndTime", cdEndDate, oaEndDate);
	}
    @Test
	public void testGetDatasetDOI() {
        assertEquals("DOI", cdiac.getDatasetDOI(), oads.getDatasetDOI());
	}
    @Test
	public void testGetDatasetId() {
        assertEquals("DatasetId", cdiac.getDatasetId(), oads.getDatasetId());
	}
    @Test
	public void testGetDatasetLink() {
        assertEquals("DatasetLink", cdiac.getDatasetLink(), oads.getDatasetLink());
	}
    @Test
	public void testGetDatasetName() {
        assertEquals("DatasetName", cdiac.getDatasetName(), oads.getDatasetName());
	}
    @Test
	public void testGetDataStartTime() {
        Double cdStart = cdiac.getDataStartTime();
        Double oaStart = oads.getDataStartTime();
        Date cdStartDate = new Date((long)(cdStart.doubleValue()*1000));
        Date oaStartDate = new Date((long)(oaStart.doubleValue()*1000));
        assertEquals("StartTime", cdStartDate, oaStartDate);
	}
    @Test
	public void testGetEasternLongitude() {
        assertEquals("EasternLongitude", cdiac.getEasternLongitude(), oads.getEasternLongitude());
	}
    @Test
	public void testGetInvestigators() {
        assertEquals("Investigators", cdiac.getInvestigators(), oads.getInvestigators());
	}
    @Test
	public void testGetNorthernLatitude() {
        assertEquals("NorthernLatitude", cdiac.getNorthernLatitude(), oads.getNorthernLatitude());
	}
    @Test
	public void testGetOrganizations() {
        assertEquals("Organizations", cdiac.getOrganizations(), oads.getOrganizations());
	}
    @Test
	public void testGetPlatformName() {
        assertEquals("PlatformName", cdiac.getPlatformName(), oads.getPlatformName());
	}
    @Test
	public void testGetPlatformType() {
        assertEquals("PlatformType", cdiac.getPlatformType(), oads.getPlatformType());
	}
    @Test
	public void testGetSouthernLatitude() {
        assertEquals("SouthernLatitude", cdiac.getSouthernLatitude(), oads.getSouthernLatitude());
	}
    @Test
	public void testGetWesternLongitude() {
        assertEquals("WesternLongitude", cdiac.getWesternLongitude(), oads.getWesternLongitude());
	}
    @Test
	public void testIsAcceptable() {
        assertEquals("isAcceptable", cdiac.isAcceptable(), oads.isAcceptable());
	}
    @Test
	public void testRead() {
        OadsOmeMetadata omd = new OadsOmeMetadata();
        try {
            omd.read(datasetId, oadsFile);
        } catch (Exception ex) {
            fail(ex.toString());
        }
	}
    @Test
	public void testSetDataEndTime() {
        Double endTime = new Double(((double)new Date().getTime())/1000);
        oads.setDataEndTime(endTime);
        assertEquals("EndTime not set", endTime, oads.getDataEndTime());
	}
    @Test
	public void testSetDatasetDOI() {
        String doi = "newDOI";
        oads.setDatasetDOI(doi);
        assertEquals("DOI not set", doi, oads.getDatasetDOI());
	}
    @Test
	public void testSetDatasetId() {
        String id = "newID";
        oads.setDatasetId(id);
        assertEquals("Id not set", id, oads.getDatasetId());
	}
    @Test
	public void testSetDatasetLink() {
        String link = "newLink";
        oads.setDatasetLink(link);
        assertEquals("Link not set", link, oads.getDatasetLink());
	}
    @Test
	public void testSetDatasetName() {
        String name = "newName";
        oads.setDatasetName(name);
        assertEquals("Name not set", name, oads.getDatasetName());
	}
    @Test
	public void testSetDataStartTime() {
        Double startTime = new Double(((double)new Date().getTime())/1000);
        oads.setDataStartTime(startTime);
        assertEquals("StartTime not set", startTime, oads.getDataStartTime());
	}
    @Test
	public void testSetEasternLongitude() {
        Double eastLon = new Double(42.42);
        oads.setEasternLongitude(eastLon);
        assertEquals("EasternLongitude not set", eastLon, oads.getEasternLongitude());
	}
    @Test
	public void testSetInvestigatorsAndOrganizations() {
        List<String> investigators = new ArrayList() {{
            add("Pi1Last, Pi1First"); add("Pi2Last, Pi2First");
        }}; 
        List<String> organizations = new ArrayList() {{
            add("org1"); add("org2");
        }};
        oads.setInvestigatorsAndOrganizations(investigators, organizations);
        Assert.assertArrayEquals("Investigators not properly set", 
                                 investigators.toArray(),
                                 oads.getInvestigators().toArray()); 
        Assert.assertArrayEquals("Organizations not properly set", 
                                 organizations.toArray(),
                                 oads.getOrganizations().toArray()); 
	}
    @Test
	public void testSetNorthernLatitude() {
        Double northLat = new Double(42.42);
        oads.setNorthernLatitude(northLat);
        assertEquals("NorternLatitude not set", northLat, oads.getNorthernLatitude());
	}
    @Test
	public void testSetPlatformName() {
        String platformName = "newName";
        oads.setPlatformName(platformName);
        assertEquals("PlatformName not set", platformName, oads.getPlatformName());
	}
    @Test
	public void testSetPlatformType() {
        String platformType = "newType";
        oads.setPlatformType(platformType);
        assertEquals("PlatformType not set", platformType, oads.getPlatformType());
	}
    @Test
	public void testSetSouthernLatitude() {
        Double southLat = new Double(-42.42);
        oads.setSouthernLatitude(southLat);
        assertEquals("SouternLatitude not set", southLat, oads.getSouthernLatitude());
	}
    @Test
	public void testSetWesternLongitude() {
        Double westLon = new Double(-42.42);
        oads.setWesternLongitude(westLon);
        assertEquals("WesternLongitude not set", westLon, oads.getWesternLongitude());
	}
    @Test
	public void testSuggestedDatasetStatus() {
//        DashboardOmeMetadata omeMetadata, DashboardDataset dataset
        fail("not implemented");
	}
    @Test
	public void testWrite() {
//        File mdataFile
        File testFile = new File(oadsFile.getParent(), oadsFileName+"-new");
        try {
            oads.write(testFile);
            assertTrue("Test file " + testFile + " not written.", testFile.exists());
            OadsOmeMetadata omd = new OadsOmeMetadata();
            omd.read(datasetId, testFile);
            assertEquals("Written datasetId does not match expected: " + datasetId, datasetId, omd.getDatasetId());
        } catch(Exception ex) {
            fail(ex.toString());
        }
	}
}
