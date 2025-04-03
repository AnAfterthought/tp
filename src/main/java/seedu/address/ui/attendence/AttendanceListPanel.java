package seedu.address.ui.attendence;

import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.attendance.Attendance;
import seedu.address.ui.UiPart;

/**
 * Panel containing the list of attendances.
 */
public class AttendanceListPanel extends UiPart<Region> {
    private static final String FXML = "AttendanceComponents/AttendanceListPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(AttendanceListPanel.class);

    @FXML
    private ListView<Attendance> attendanceListView;

    /**
     * Creates an {@code AttendanceListPanel} with the given {@code ObservableList}.
     */
    public AttendanceListPanel(ObservableList<Attendance> attendanceList) {
        super(FXML);
        attendanceListView.setItems(attendanceList);
        attendanceListView.setCellFactory(listView -> new AttendanceListViewCell());
    }

    /**
     * Custom {@code ListCell} that displays the graphics of an {@code Attendance}
     * using an {@code AttendanceCard}.
     */
    class AttendanceListViewCell extends ListCell<Attendance> {
        @Override
        protected void updateItem(Attendance attendance, boolean empty) {
            super.updateItem(attendance, empty);

            if (empty || attendance == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new AttendanceCard(attendance, getIndex() + 1).getRoot());
            }
        }
    }

}
