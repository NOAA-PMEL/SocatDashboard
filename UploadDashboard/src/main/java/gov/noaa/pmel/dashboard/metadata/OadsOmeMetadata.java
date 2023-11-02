/**
 * 
 */
package gov.noaa.pmel.dashboard.metadata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import gov.noaa.ncei.oads.xml.v_a0_2_2s.OadsMetadataDocumentType;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.PersonNameType;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.PersonType;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.PlatformType;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.TypedIdentifierType;
import gov.noaa.ncei.oads.xml.v_a0_2_2s.TypedStringType;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.dashboard.shared.DatasetQCStatus;
import gov.noaa.pmel.oads.util.StringUtils;
import gov.noaa.pmel.oads.xml.a0_2_2.OadsXmlReader;
import gov.noaa.pmel.oads.xml.a0_2_2.OadsXmlWriter;
import gov.noaa.pmel.socatmetadata.SocatMetadata;
import uk.ac.uea.socat.omemetadata.OmeMetadata;

/**
 * @author kamb
 *
 */
public class OadsOmeMetadata implements OmeMetadataInterface {
    
    private static final String OADS_OME_PDF_XSLT = "oadsfo.xsl";
    
    public static class NotImplementedException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        public NotImplementedException() {
            super();
        }
        public NotImplementedException(String msg) {
            super(msg);
        }
    }
    
    private Element _rootElement;
//    Element getXmlRoot() { return _rootElement; }
    
    private OadsMetadataDocumentType _oads;
    OadsMetadataDocumentType getOadsDocument() { return _oads; }
    
    private String _datasetDOI;
    private String _datasetId;
//    private String _expocode;
    private String _datasetName;
    private PlatformType _platform;
    
    private boolean _isTest = false;
    
    public OadsOmeMetadata() {
        // default
    }
    public OadsOmeMetadata(boolean isTest) {
        _isTest = isTest;
    }

    private static String nonNullString(String str) {
        if ( str == null )  { return ""; }
        return str.trim();
    }
    
    public void read(File mdataFile) throws IllegalArgumentException, FileNotFoundException {
        read(null, mdataFile);
    }
    @Override
    public void read(String datasetId, File mdataFile) throws IllegalArgumentException, FileNotFoundException {
        if ( !mdataFile.exists() )
            throw new FileNotFoundException("Metadata file " + mdataFile.getName() +
                    " does not exist for dataset " + datasetId);
        try {
            Document _xmlDoc = (new SAXBuilder()).build(mdataFile);
            _rootElement = _xmlDoc.getRootElement();
        } catch ( Exception ex ) {
            throw new IllegalArgumentException(ex);
        }

        try {
            _oads = OadsXmlReader.read(mdataFile);
        } catch ( Exception ex ) {
            throw new IllegalArgumentException(ex);
        }
        if ( ! StringUtils.emptyOrNull(datasetId)) { // XXX TODO: what happens if we don't do this
            String expocode = _getExpocode(datasetId);
            try {
                String stdId = DashboardServerUtils.checkDatasetID(expocode);
                // XXX TODO: What about multiple expocodes?  Check against all?
                // I know SOCAT shouldn't have that.
                if ( !StringUtils.emptyOrNull(datasetId) && !stdId.equalsIgnoreCase(datasetId) )
                    throw new IllegalArgumentException("Expocode \"" + expocode + 
                                                       "\" does not match datasetID \"" + datasetId + "\"");
                _datasetDOI = _oads.getDatasetDOI();
                _datasetId = stdId;
                _datasetName = nonNullString(_getDatasetName());
                _platform = _getPlatform();
            } catch ( IllegalArgumentException ex ) {
                throw new IllegalArgumentException("Invalid metadata in file " +
                        mdataFile.getName() + " for dataset " + datasetId + ": " +
                        ex.getMessage(), ex);
            }
        }
    }

    @Override
    public void write(File mdataFile) throws IOException {
        try {
            OadsXmlWriter.writeXml(_oads, mdataFile);
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public boolean isAcceptable() {
        // see OmeMetadata.isAcceptable();
        // checks for multiple entries in those elements that support mulitiple entries in OADS,
        // and checks for the minimal required metadata:
        // Expocode, PIs not empty, and vessel name.
        if ( _oads.getExpocodes().size() > 1 ) {
            System.err.println("Mulitple expocodes for dataset " + _datasetId);
            return false;
        }
        if ( _oads.getExpocodes().isEmpty()) {
            System.err.println("No expocode for dataset " + _datasetId);
            return false;
        }
        if ( _oads.getInvestigators().isEmpty()) {
            System.err.println("No investigators for dataset " + _datasetId);
            return false;
        }
        if ( _platform == null ||
                _platform.getName() == null ||
                _platform.getName().trim().isEmpty()) {
            System.err.println("No vessel name for dataset " + _datasetId);
            return false;
        }
        return true;
    }

    @Override
    public String getDatasetId() {
        if ( OmeMetadata.CONFLICT_STRING.equals(_datasetId) )
            _datasetId = null;
        return _datasetId;
    }
    private String _getExpocode(String datasetId) {
        List<String> expocodes = _oads.getExpocodes();
        if ( expocodes == null || expocodes.isEmpty()) {
            throw new IllegalArgumentException("No expocode found in metadata file.");
        }
        if ( expocodes.size() > 1 ) {
            System.err.println("Multiple ("+expocodes.size()+") expocodes found in metadata file:"+expocodes);
        }
        for (int i = 0; i < expocodes.size(); i++) {
            expocodes.set(i, DashboardServerUtils.checkDatasetID(expocodes.get(i)));
        }
        if ( !expocodes.contains(datasetId)) {
//            throw new IllegalArgumentException("metadata document does not contain expocode: " + datasetId);
            System.err.println("WARNING: Dataset does not contain specified datasetId: " + datasetId +
                               "\n\tUsing expocode " + expocodes.get(0));
            datasetId = expocodes.get(0);
        }
        String expocode = datasetId;
        return expocode;
    }

    @Override
    public void setDatasetId(String newId) {
        _datasetId = newId; 
        _oads.getExpocodes().set(0, newId);
    }

    @Override
    public String getDatasetName() {
        if ( OmeMetadata.CONFLICT_STRING.equals(_datasetName) )
            _datasetName = null;
        return _datasetName;
    }
    public String _getDatasetName() {
        List<TypedStringType> cruiseIds = _oads.getCruiseIds();
        if ( cruiseIds == null || cruiseIds.isEmpty()) {
            System.out.println("No cruiseId found in metadata file for dataset " + _datasetId);
            return _datasetId;
        }
        // XXX TODO Problem with OME->OADS translator chopping exp name into separate cruiseId elements
        // ACTUALLY, it's the MetadataEditor that is chopping it up when building oads metadata object.
//        if ( cruiseIds.size() > 1 ) {
//            throw new IllegalArgumentException("Multiple ("+cruiseIds.size()+ 
//                                               ") cruiseIds found in metadata file.");
//        }
//        String cruiseId = cruiseIds.get(0).getValue();
//        return cruiseId;
        StringBuilder nameBldr = new StringBuilder();
        String space = "";
        for (TypedStringType cruise : cruiseIds) {
            nameBldr.append(space).append(cruise.getValue());
            space = " ";
        }
        return nameBldr.toString();
    }
    @Override
    public void setDatasetName(String newName) {
        _datasetName = newName; 
        TypedStringType current = _oads.getCruiseIds().get(0);
        current.setValue(newName);
    }

    @Override
    public String getPlatformName() {
        return _platform.getName();
    }
    private PlatformType _getPlatform() {
        List<PlatformType> platforms = _oads.getPlatforms();
        if ( platforms == null || platforms.isEmpty()) {
            throw new IllegalArgumentException("No platform found in metadata file.");
        }
        if ( platforms.size() > 1 ) {
            throw new IllegalArgumentException("Multiple ("+platforms.size()+ 
                                               ") platforms found in metadata file.");
        }
        PlatformType platform = platforms.get(0);
        return platform;
    }

    @Override
    public void setPlatformName(String platformName) {
        _platform.setName(platformName);
    }

    @Override
    public String getPlatformType() {
        return _platform.getType(); // XXX TODO Unconstrained text?
    }

    @Override
    public void setPlatformType(String platformType) {
        _platform.setType(platformType);
    }

    @Override
    public ArrayList<String> getInvestigators() {
        List<PersonType> investigators = _oads.getInvestigators();
        ArrayList<String> investigatorNames = new ArrayList<>(investigators.size());
        for (PersonType p : investigators) {
            investigatorNames.add(p.getName().getLast()+", " + p.getName().getFirst());
        }
        return investigatorNames;
    }

    @Override
    public ArrayList<String> getOrganizations() {
        List<PersonType> investigators = _oads.getInvestigators();
        ArrayList<String> investigatorOrgs = new ArrayList<>(investigators.size());
        for (PersonType p : investigators) {
            investigatorOrgs.add(p.getOrganization());
        }
        return investigatorOrgs;
    }

    @Override
    // lossy
    public void setInvestigatorsAndOrganizations(List<String> investigators, List<String> organizations) {
        List<PersonType> newPis = new ArrayList<>(investigators.size());
        for (int i = 0; i < investigators.size(); i++) {
            String investigator = investigators.get(i);
            String org = organizations.get(i);
            String[] names;
            String first, last;
            if ( investigator.indexOf(',') > 0 ) {
                names = investigator.split(",");
                first = names[1].trim();
                last = names[0].trim();
            } else {
                names = investigator.split(" ");
                first = names[0].trim();
                last = "";
                if ( names.length > 1) {
                    StringBuilder lastBldr = new StringBuilder();
                    String space = "";
                    for (int j = 1; j <  names.length; j++) {
                        lastBldr.append(space).append(names[j]);
                        space = " ";
                    }
                }
            }
            System.out.println("Adding investigator " + first + " " + last);
            newPis.add(PersonType.builder()
                       .name(PersonNameType.builder()
                             .first(first)
                             .last(last)
                             .build())
                       .organization(org)
                       .build());
        }
        _oads.setInvestigators(newPis);
    }

    @Override
    public String getDatasetDOI() {
        return _datasetDOI;
    }

    @Override
    public void setDatasetDOI(String datasetDOI) {
        _datasetDOI = datasetDOI;
        _oads.setDatasetDOI(datasetDOI);
    }

    @Override
    public String getDatasetLink() {
//        throw new NotImplementedException();
//        System.out.println("getDatasetLink NOT IMPLEMENTED");
//        return "";
        return _oads.getCitation();
    }

    @Override
    public void setDatasetLink(String datasetLink) {
//        throw new NotImplementedException();
//        System.out.println("setDatasetLink NOT IMPLEMENTED");
        _oads.setCitation(datasetLink);
    }

    @Override
    public Double getWesternLongitude() {
        return _oads.getSpatialExtents().getBounds().getWesternBounds().doubleValue();
    }

    @Override
    public void setWesternLongitude(Double westernLongitude) {
        _oads.getSpatialExtents().getBounds().setWesternBounds(new BigDecimal(westernLongitude));
    }

    @Override
    public Double getEasternLongitude() {
        return _oads.getSpatialExtents().getBounds().getEasternBounds().doubleValue();
    }

    @Override
    public void setEasternLongitude(Double easternLongitude) {
        _oads.getSpatialExtents().getBounds().setEasternBounds(new BigDecimal(easternLongitude));
    }

    @Override
    public Double getSouthernLatitude() {
        return _oads.getSpatialExtents().getBounds().getSouthernBounds().doubleValue();
    }

    @Override
    public void setSouthernLatitude(Double southernLatitude) {
        _oads.getSpatialExtents().getBounds().setSouthernBounds(new BigDecimal(southernLatitude));
    }

    @Override
    public Double getNorthernLatitude() {
        return _oads.getSpatialExtents().getBounds().getNorthernBounds().doubleValue();
    }

    @Override
    public void setNorthernLatitude(Double northernLatitude) {
        _oads.getSpatialExtents().getBounds().setNorthernBounds(new BigDecimal(northernLatitude));
    }

    @Override
    public Double getDataStartTime() {
        return new Double(((double)_oads.getTemporalExtents().getStartDate().getTime())/1000);
    }

    @Override
    public void setDataStartTime(Double dataStartTime) {
        _oads.getTemporalExtents().setStartDate(new Date((long)(1000*dataStartTime)));
    }

    @Override
    public Double getDataEndTime() {
        return new Double(((double)_oads.getTemporalExtents().getEndDate().getTime())/1000);
    }

    @Override
    public void setDataEndTime(Double dataEndTime) {
        _oads.getTemporalExtents().setEndDate(new Date((long)(1000*dataEndTime)));
    }

    @Override
    public DatasetQCStatus suggestedDatasetStatus(DashboardOmeMetadata metadata,
                                                  DashboardDataset dataset) throws IllegalArgumentException {
//        throw new NotImplementedException();
        System.out.println("suggestedDatasetStatus() NOT FULLY IMPLEMENTED");
        OadsSocatUtils osu = new OadsSocatUtils((OadsOmeMetadata)metadata.omeMData);
        SocatMetadata socat = osu.createSocatMetadata();
//        System.out.println("OADS socat string: "+ socat);
//        File xmlFile = new File(dataset.getDatasetId()+"_oads.xml");
//        try {
//            SdiOmeMetadata.writeXml(xmlFile, socat);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        System.out.println("Wrote XML to " + xmlFile.getAbsolutePath());
        DatasetQCStatus status = OmeUtils.suggestDatasetQCFlag(socat, dataset);
        System.out.println(dataset.getDatasetId() + " : OADS status: " + status);
        return status;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            File dataDir = new File("/Users/kamb/workspace/oa_dashboard_test_data/SOCAT/omeexamplefilesfromaoml");
            String datasetId = "33HH20210512";
            String oadsFileName = "33HH20210512_ME-oads.xml";
            File oadsFile = new File(dataDir, oadsFileName);
            OadsOmeMetadata oomd = new OadsOmeMetadata();
            oomd.read(datasetId, oadsFile);
        } catch (Exception ex) {
            ex.printStackTrace();
            // TODO: handle exception
        }

    }
    @Override
    public String getOmePdfXsltFileName() {
        return OADS_OME_PDF_XSLT;
    }

}
