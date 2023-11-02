/**
 *
 */
package gov.noaa.pmel.dashboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;
import gov.noaa.pmel.dashboard.client.UploadDashboard.PagesEnum;
import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.dashboard.shared.DashboardDatasetList;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;

import java.util.Date;

/**
 * @author Karl Smith
 */
public class OmeManagerPage extends CompositeWithUsername {

    private static final String TITLE_TEXT = "Edit OME Metadata";
    private static final String WELCOME_INTRO = "Logged in as ";
    private static final String LOGOUT_TEXT = "Logout";
    private static final String UPLOAD_TEXT = "Upload";
    private static final String CANCEL_TEXT = "Cancel";

    private static final String CRUISE_HTML_INTRO_PROLOGUE = "<p>" +
            "At this time, the system can upload CDIAC-style OME \"&lt;x_tags&gt;\" XML and OADS \"&lt;oads_metadata&gt;\"XML metadata files. " +
            "A new Online Metadata Editor is available at: " +
            "<a href='https://data.pmel.noaa.gov/sdig/socat/MetadataEditor.html' target='_blank'>https://data.pmel.noaa.gov/sdig/socat/MetadataEditor.html</a>" +
            "</p>" +
            "<p>" +
            "Please upload whatever other metadata files you have as \"Supplemental Documents\". " +
            "A spreadsheet for providing metadata can be obtained using the \"metadata template\" " +
            "link on the SOCAT info page at <a href=\"https://www.socat.info/index.php/data-upload-and-quality-control/\" " +
            "target=\"_blank\">https://www.socat.info/index.php/data-upload-and-quality-control/</a> " +
            "</p>" +
            "<p>" +
            "Dataset: <ul><li>";
    private static final String CRUISE_HTML_INTRO_EPILOGUE = "</li></ul></p>";

    private static final String NO_FILE_ERROR_MSG =
            "Please select an SOCAT OME XML metadata file to upload";
    private static final String NO_SCHEMA_SELECTED =
            "Please select the XML document schema.";

    private static final String OVERWRITE_WARNING_MSG =
            "The SOCAT OME XML metadata for this dataset will be overwritten.  Do you wish to proceed?";
    private static final String OVERWRITE_YES_TEXT = "Yes";
    private static final String OVERWRITE_NO_TEXT = "No";

    private static final String UNEXPLAINED_FAIL_MSG =
            "<h3>Upload failed.</h3>" +
                    "<p>Unexpectedly, no explanation of the failure was given</p>";
    private static final String EXPLAINED_FAIL_MSG_START =
            "<h3>Upload failed.</h3>" +
                    "<p><pre>\n";
    private static final String EXPLAINED_FAIL_MSG_END =
            "</pre></p>";

    interface OmeManagerPageUiBinder extends UiBinder<Widget,OmeManagerPage> {
    }

    private static OmeManagerPageUiBinder uiBinder =
            GWT.create(OmeManagerPageUiBinder.class);

    @UiField
    InlineLabel titleLabel;
    @UiField
    InlineLabel userInfoLabel;
    @UiField
    Button logoutButton;
    @UiField
    HTML introHtml;
    @UiField
    FormPanel uploadForm;
    @UiField
    FileUpload omeUpload;
    @UiField
    CaptionPanel settingsCaption;
    @UiField
    RadioButton omeRadio;
//    @UiField
//    RadioButton cdiacRadio;
    @UiField
    RadioButton oadsRadio;
    @UiField
    Hidden timestampToken;
    @UiField
    Hidden datasetIdsToken;
    @UiField
    Hidden omeToken;
    @UiField
    Hidden xmlToken;
    @UiField
    Button uploadButton;
    @UiField
    Button cancelButton;

    private DashboardDataset cruise;
    private DashboardAskPopup askOverwritePopup;

    // Singleton instance of this page
    private static OmeManagerPage singleton;

    OmeManagerPage() {
        initWidget(uiBinder.createAndBindUi(this));
        singleton = this;

        setUsername(null);
        cruise = null;
        askOverwritePopup = null;

        titleLabel.setText(TITLE_TEXT);
        logoutButton.setText(LOGOUT_TEXT);

        uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
        uploadForm.setMethod(FormPanel.METHOD_POST);
        uploadForm.setAction(GWT.getModuleBaseURL() + "MetadataUploadService");

        clearTokens();

        uploadButton.setText(UPLOAD_TEXT);
        cancelButton.setText(CANCEL_TEXT);
    }

    /**
     * Display the OME metadata upload page in the RootLayoutPanel for the given cruise.  Adds this page to the page
     * history.
     *
     * @param cruises
     *         add/replace the OME metadata for the cruise in this list
     */
    static void showPage(DashboardDatasetList cruises) {
        if ( singleton == null )
            singleton = new OmeManagerPage();
        singleton.updateDataset(cruises);
        UploadDashboard.updateCurrentPage(singleton);
        History.newItem(PagesEnum.EDIT_METADATA.name(), false);
    }

    /**
     * Redisplays the last version of this page if the username associated with this page matches the given username.
     */
    static void redisplayPage(String username) {
        if ( (username == null) || username.isEmpty() ||
                (singleton == null) || !singleton.getUsername().equals(username) ) {
            DatasetListPage.showPage();
        }
        else {
            UploadDashboard.updateCurrentPage(singleton);
        }
    }

    /**
     * Updates this page with the username and the cruise in the given set of cruise.
     *
     * @param cruises
     *         associate the uploaded OME metadata to the cruise in this set of cruises
     */
    private void updateDataset(DashboardDatasetList cruises) {
        // Update the current username
        setUsername(cruises.getUsername());
        userInfoLabel.setText(WELCOME_INTRO + getUsername());

        // Update the cruise associated with this page
        cruise = cruises.values().iterator().next();

        // Update the HTML intro naming the cruise
        introHtml.setHTML(CRUISE_HTML_INTRO_PROLOGUE +
                SafeHtmlUtils.htmlEscape(cruise.getDatasetId()) +
                CRUISE_HTML_INTRO_EPILOGUE);

        // Clear the hidden tokens just to be safe
        clearTokens();
    }

    /**
     * Clears all the Hidden tokens on the page.
     */
    private void clearTokens() {
        timestampToken.setValue("");
        datasetIdsToken.setValue("");
        omeToken.setValue("");
        xmlToken.setValue("");
        omeRadio.setValue(false);
        oadsRadio.setValue(false);
//        cdiacRadio.setValue(false);
    }

    /**
     * Assigns all the Hidden tokens on the page.
     */
    private void assignTokens() {
        String localTimestamp = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm Z").format(new Date());
        timestampToken.setValue(localTimestamp);
        datasetIdsToken.setValue("[ \"" + cruise.getDatasetId() + "\" ]");
        omeToken.setValue("true");
        xmlToken.setValue(getXmlSchema());
    }

    /**
     * @return
     */
    private String getXmlSchema() {
        if (omeRadio.getValue()) {
            return omeRadio.getFormValue();
        }
        if (oadsRadio.getValue()) {
            return oadsRadio.getFormValue();
        }
//        if (cdiacRadio.getValue()) {
//            return cdiacRadio.getFormValue();
//        }
        return null;
    }
    
    private boolean xmlSchemaSelected() {
        return getXmlSchema() != null;
    }

    @UiHandler("logoutButton")
    void logoutOnClick(ClickEvent event) {
        DashboardLogoutPage.showPage();
    }

    @UiHandler("cancelButton")
    void cancelButtonOnClick(ClickEvent event) {
        // Return to the cruise list page which might have been updated
        DatasetListPage.showPage();
    }

    @UiHandler("uploadButton")
    void uploadButtonOnClick(ClickEvent event) {
        // Make sure a file was selected
        String uploadFilename = DashboardUtils.baseName(omeUpload.getFilename());
        if ( uploadFilename.isEmpty() ) {
            UploadDashboard.showMessage(NO_FILE_ERROR_MSG);
            return;
        }
        if ( !xmlSchemaSelected()) {
            UploadDashboard.showMessage(NO_SCHEMA_SELECTED);
            return;
        }

        // If an overwrite will occur, ask for confirmation
        if ( !cruise.getOmeTimestamp().isEmpty() ) {
            if ( askOverwritePopup == null ) {
                askOverwritePopup = new DashboardAskPopup(OVERWRITE_YES_TEXT,
                        OVERWRITE_NO_TEXT, new AsyncCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        // Submit only if yes
                        if ( result == true ) {
                            assignTokens();
                            uploadForm.submit();
                        }
                    }

                    @Override
                    public void onFailure(Throwable ex) {
                        // Never called
                        ;
                    }
                });
            }
            askOverwritePopup.askQuestion(OVERWRITE_WARNING_MSG);
            return;
        }

        // Nothing overwritten, submit the form
        assignTokens();
        uploadForm.submit();
    }

    @UiHandler("uploadForm")
    void uploadFormOnSubmit(SubmitEvent event) {
        UploadDashboard.showWaitCursor();
    }

    @UiHandler("uploadForm")
    void uploadFormOnSubmitComplete(SubmitCompleteEvent event) {
        clearTokens();
        processResultMsg(event.getResults());
        // Restore the usual cursor
        UploadDashboard.showAutoCursor();
    }

    /**
     * Process the message returned from the upload of a dataset.
     *
     * @param resultMsg
     *         message returned from the upload of a dataset
     */
    private void processResultMsg(String resultMsg) {
        if ( resultMsg == null ) {
            UploadDashboard.showMessage(UNEXPLAINED_FAIL_MSG);
            return;
        }
        resultMsg = resultMsg.trim();
        if ( resultMsg.startsWith(DashboardUtils.SUCCESS_HEADER_TAG) ) {
            // cruise file created or updated; return to the cruise list,
            // having it request the updated cruises for the user from the server
            DatasetListPage.showPage();
        }
        else {
            // Unknown response, just display the entire message
            UploadDashboard.showMessage(EXPLAINED_FAIL_MSG_START +
                    SafeHtmlUtils.htmlEscape(resultMsg) + EXPLAINED_FAIL_MSG_END);
        }
    }

}
