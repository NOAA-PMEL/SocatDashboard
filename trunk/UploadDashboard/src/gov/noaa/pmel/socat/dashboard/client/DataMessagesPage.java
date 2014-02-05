/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import gov.noaa.pmel.socat.dashboard.client.SocatUploadDashboard.PagesEnum;
import gov.noaa.pmel.socat.dashboard.shared.SCMessage;
import gov.noaa.pmel.socat.dashboard.shared.SCMessage.SCMsgSeverity;
import gov.noaa.pmel.socat.dashboard.shared.SCMessageList;
import gov.noaa.pmel.socat.dashboard.shared.SCMessagesService;
import gov.noaa.pmel.socat.dashboard.shared.SCMessagesServiceAsync;

import java.util.List;

import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

/**
 * @author Karl Smith
 */
public class DataMessagesPage extends Composite {

	private static final String WELCOME_INTRO = "Welcome ";
	private static final String LOGOUT_TEXT = "Logout";
	private static final String INTRO_HTML = 
			"<b>Automatically-Detected Data Problems<b>" +
			"<br /><br />" +
			"Problems automatically detected in the cruise";
	private static final String DISMISS_BUTTON_TEXT = "OK";

	private static final String SEVERITY_COLUMN_NAME = "Severity";
	private static final String ROW_NUMBER_COLUMN_NAME = "Row";
	private static final String COLUMN_NUMBER_COLUMN_NAME = "Column";
	private static final String COLUMN_NAME_COLUMN_NAME = "Column Name";
	private static final String EXPLANATION_COLUMN_NAME = "Explanation";

	private static final String ERROR_SEVERITY_TEXT = "Error";
	private static final String WARNING_SEVERITY_TEXT = "Warning";
	private static final String UNKNOWN_SEVERITY_TEXT = "Unknown";

	private static final String EMPTY_TABLE_TEXT = "No problems detected!";
	
	interface DataMessagesPageUiBinder extends UiBinder<Widget, DataMessagesPage> {
	}

	private static DataMessagesPageUiBinder uiBinder = 
			GWT.create(DataMessagesPageUiBinder.class);

	private static SCMessagesServiceAsync service = 
			GWT.create(SCMessagesService.class);

	@UiField InlineLabel userInfoLabel;
	@UiField Button logoutButton;
	@UiField HTML introHtml;
	@UiField DataGrid<SCMessage> messagesGrid;
	@UiField Button dismissButton;
	
	private String username;
	private ListDataProvider<SCMessage> listProvider;

	// The singleton instance of this page
	private static DataMessagesPage singleton;

	/**
	 * Creates an empty data messages page.  Do not call this 
	 * constructor; instead use the showPage static method 
	 * to show the singleton instance of this page with the
	 * latest data messages for a cruise from the server. 
	 */
	DataMessagesPage() {
		initWidget(uiBinder.createAndBindUi(this));
		username = "";
		logoutButton.setText(LOGOUT_TEXT);
		buildMessageListTable();
		dismissButton.setText(DISMISS_BUTTON_TEXT);
	}

	/**
	 * Display this page in the RootLayoutPanel showing the
	 * messages for cruise with the provided expocode.
	 * Adds this page to the page history.
	 */
	static void showPage(String cruiseExpocode) {
		service.getDataMessages(DashboardLoginPage.getUsername(), 
				DashboardLoginPage.getPasshash(), cruiseExpocode, 
				new AsyncCallback<SCMessageList>() {
			@Override
			public void onSuccess(SCMessageList msgList) {
				if ( (msgList == null) || 
					 msgList.getUsername().isEmpty() || 
					 ! msgList.getUsername().equals(
							 DashboardLoginPage.getUsername()) ) {
					SocatUploadDashboard.showMessage("Unexpected sanity " +
							"checker data problems list was returned");
					return;
				}
				if ( singleton == null )
					singleton = new DataMessagesPage();
				SocatUploadDashboard.updateCurrentPage(singleton);
				singleton.updateMessages(msgList);
				History.newItem(PagesEnum.DATA_MESSAGES.name(), false);
			}
			@Override
			public void onFailure(Throwable ex) {
				SocatUploadDashboard.showFailureMessage("Unexpected failure " +
						"obtaining the sanity checker data problems", ex);
			}
		});

	}

	/**
	 * Redisplays the last version of this page if the username
	 * associated with this page matches the current login username.
	 * 
	 * @param addToHistory
	 * 		if true, adds this page to the page history 
	 */
	static void redisplayPage(boolean addToHistory) {
		// If never shown before, or if the username does not match the 
		// current login username, show the login page instead
		if ( (singleton == null) || 
			 ! singleton.username.equals(DashboardLoginPage.getUsername()) ) {
			DashboardLoginPage.showPage(true);
		}
		else {
			SocatUploadDashboard.updateCurrentPage(singleton);
			if ( addToHistory )	
				History.newItem(PagesEnum.DATA_MESSAGES.name(), false);
		}
	}

	/**
	 * Update the cruise expocode and sanity checker messages with 
	 * that given in the provided SCMessageList.
	 * 
	 * @param msgList
	 * 		cruise expocode and set of messages to show 
	 */
	private void updateMessages(SCMessageList msgs) {
		// Assign the username and introduction message
		username = DashboardLoginPage.getUsername();
		userInfoLabel.setText(WELCOME_INTRO + username);
		introHtml.setHTML(INTRO_HTML + 
				"<ul><li>" + 
				SafeHtmlUtils.htmlEscape(msgs.getExpocode()) + 
				"</li></ul>");
		// Update the table by resetting the data in the data provider
		List<SCMessage> msgList = listProvider.getList();
		msgList.clear();
		msgList.addAll(msgs);
		messagesGrid.setRowCount(msgList.size());
		// Make sure the table is sorted according to the last specification
		ColumnSortEvent.fire(messagesGrid, messagesGrid.getColumnSortList());
	}

	/**
	 * Creates the messages table for this page.
	 */
	private void buildMessageListTable() {
		TextColumn<SCMessage> severityColumn = buildSeverityColumn();
		Column<SCMessage,Number> rowNumColumn = buildRowNumColumn();
		Column<SCMessage,Number> colNumColumn = buildColNumColumn();
		TextColumn<SCMessage> colNameColumn = buildColNameColumn();
		TextColumn<SCMessage> explanationColumn = buildExplanationColumn();

		messagesGrid.addColumn(severityColumn, SEVERITY_COLUMN_NAME);
		messagesGrid.addColumn(rowNumColumn, ROW_NUMBER_COLUMN_NAME);
		messagesGrid.addColumn(colNumColumn, COLUMN_NUMBER_COLUMN_NAME);
		messagesGrid.addColumn(colNameColumn, COLUMN_NAME_COLUMN_NAME);
		messagesGrid.addColumn(explanationColumn, EXPLANATION_COLUMN_NAME);

		// Set the minimum widths of the columns
		double tableWidth = 0.0;
		messagesGrid.setColumnWidth(severityColumn, 
				SocatUploadDashboard.NARROW_COLUMN_WIDTH, Style.Unit.EM);
		tableWidth += SocatUploadDashboard.NARROW_COLUMN_WIDTH;
		messagesGrid.setColumnWidth(rowNumColumn, 
				SocatUploadDashboard.NARROW_COLUMN_WIDTH, Style.Unit.EM);
		tableWidth += SocatUploadDashboard.NARROW_COLUMN_WIDTH;
		messagesGrid.setColumnWidth(colNumColumn, 
				SocatUploadDashboard.NARROW_COLUMN_WIDTH, Style.Unit.EM);
		tableWidth += SocatUploadDashboard.NARROW_COLUMN_WIDTH;
		messagesGrid.setColumnWidth(colNameColumn, 
				SocatUploadDashboard.NORMAL_COLUMN_WIDTH, Style.Unit.EM);
		tableWidth += SocatUploadDashboard.NORMAL_COLUMN_WIDTH;
		messagesGrid.setColumnWidth(explanationColumn, 
				2 * SocatUploadDashboard.FILENAME_COLUMN_WIDTH, Style.Unit.EM);
		tableWidth += 2 * SocatUploadDashboard.FILENAME_COLUMN_WIDTH;

		// Set the minimum width of the full table
		messagesGrid.setMinimumTableWidth(tableWidth, Style.Unit.EM);

		// Create the data provider for this table
		listProvider = new ListDataProvider<SCMessage>();
		listProvider.addDataDisplay(messagesGrid);

		// Make the columns sortable
		severityColumn.setSortable(true);
		rowNumColumn.setSortable(true);
		colNumColumn.setSortable(true);
		colNameColumn.setSortable(true);
		explanationColumn.setSortable(true);

		// Add a column sorting handler for these columns
		ListHandler<SCMessage> columnSortHandler = 
				new ListHandler<SCMessage>(listProvider.getList());
		columnSortHandler.setComparator(severityColumn,
				SCMessage.severityComparator);
		columnSortHandler.setComparator(rowNumColumn,
				SCMessage.rowNumComparator);
		columnSortHandler.setComparator(colNumColumn,
				SCMessage.colNumComparator);
		columnSortHandler.setComparator(colNameColumn,
				SCMessage.colNameComparator);
		columnSortHandler.setComparator(explanationColumn,
				SCMessage.explanationComparator);

		// Add the sort handler to the table, setting the default sorting
		// first by severity, then row number, then column number
		messagesGrid.addColumnSortHandler(columnSortHandler);
		messagesGrid.getColumnSortList().push(colNumColumn);
		messagesGrid.getColumnSortList().push(rowNumColumn);
		messagesGrid.getColumnSortList().push(severityColumn);

		// Set the contents if there are no rows
		messagesGrid.setEmptyTableWidget(new Label(EMPTY_TABLE_TEXT));

		// Following recommended to improve efficiency with IE
		messagesGrid.setSkipRowHoverCheck(false);
		messagesGrid.setSkipRowHoverFloatElementCheck(false);
		messagesGrid.setSkipRowHoverStyleUpdate(false);
	}

	private TextColumn<SCMessage> buildSeverityColumn() {
		return new TextColumn<SCMessage>() {
			@Override
			public String getValue(SCMessage msg) {
				if ( msg == null )
					return UNKNOWN_SEVERITY_TEXT;
				SCMsgSeverity severity = msg.getSeverity();
				if ( severity == SCMsgSeverity.WARNING )
					return WARNING_SEVERITY_TEXT;
				if ( severity == SCMsgSeverity.ERROR )
					return ERROR_SEVERITY_TEXT;
				return UNKNOWN_SEVERITY_TEXT;
			}
		};
	}

	private static final NumberFormat INT_NUMBER_FORMAT = 
			NumberFormat.getDecimalFormat().overrideFractionDigits(0);

	private Column<SCMessage,Number> buildRowNumColumn() {
		return new Column<SCMessage,Number>(new NumberCell(INT_NUMBER_FORMAT)) {
			@Override
			public Number getValue(SCMessage msg) {
				if ( msg == null )
					return -1;
				return Integer.valueOf(msg.getRowNumber());
			}
		};
	}

	private Column<SCMessage,Number> buildColNumColumn() {
		return new Column<SCMessage,Number>(new NumberCell(INT_NUMBER_FORMAT)) {
			@Override
			public Number getValue(SCMessage msg) {
				if ( msg == null )
					return -1;
				return Integer.valueOf(msg.getColNumber());
			}
		};
	}

	private TextColumn<SCMessage> buildColNameColumn() {
		return new TextColumn<SCMessage>() {
			@Override
			public String getValue(SCMessage msg) {
				if ( msg == null )
					return "";
				return msg.getColName();
			}
		};
	}

	private TextColumn<SCMessage> buildExplanationColumn() {
		return new TextColumn<SCMessage>() {
			@Override
			public String getValue(SCMessage msg) {
				if ( msg == null )
					return "";
				return msg.getExplanation();
			}
		};
	}

}