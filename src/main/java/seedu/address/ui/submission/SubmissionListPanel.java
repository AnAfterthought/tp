package seedu.address.ui.submission;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.student.Student;
import seedu.address.model.submission.Submission;
import seedu.address.model.tutorial.Tutorial;
import seedu.address.ui.UiPart;

/**
 * Panel containing the list of submissions.
 */
public class SubmissionListPanel extends UiPart<Region> {
    private static final String FXML = "SubmissionComponents/SubmissionListPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(SubmissionListPanel.class);

    @FXML
    private ListView<SubmissionInfo> submissionListView;

    record SubmissionInfo(Student student, Map<Tutorial, List<Submission>> tutorialSubmissions) {
        public List<Tutorial> getSortedTutorials() {
            return tutorialSubmissions.keySet().stream().sorted(Comparator.comparing(Tutorial::name))
                            .collect(Collectors.toList());
        }
    }

    /**
     * Creates an {@code SubmissionListPanel} with the given {@code ObservableList}.
     */
    public SubmissionListPanel(ObservableList<Submission> submissionList) {
        super(FXML);

        ObservableList<SubmissionInfo> submissionInfoList = FXCollections.observableArrayList();
        updateGroupedList(submissionList, submissionInfoList);

        submissionList.addListener((ListChangeListener<? super Submission>) change -> {
            updateGroupedList(submissionList, submissionInfoList);
        });

        submissionListView.setItems(submissionInfoList);
        submissionListView.setCellFactory(listView -> new SubmissionListViewCell());
    }

    /**
     * Updates the grouped list by organizing submissions hierarchically first by
     * student, then by tutorial.
     */
    private void updateGroupedList(ObservableList<Submission> submissionList,
                    ObservableList<SubmissionInfo> groupedList) {
        Map<Student, Map<Tutorial, List<Submission>>> studentMap = new HashMap<>();

        for (Submission submission : submissionList) {
            Student student = submission.student();
            Tutorial tutorial = submission.assignment().tutorial();

            studentMap.computeIfAbsent(student, k -> new HashMap<>()).computeIfAbsent(tutorial, k -> new ArrayList<>())
                            .add(submission);
        }

        List<SubmissionInfo> newGroupedList = new ArrayList<>();
        for (Map.Entry<Student, Map<Tutorial, List<Submission>>> entry : studentMap.entrySet()) {
            newGroupedList.add(new SubmissionInfo(entry.getKey(), entry.getValue()));
        }

        groupedList.setAll(newGroupedList);
    }

    /**
     * Custom {@code ListCell} that displays the graphics of an
     * {@code SubmissionInfo} using an {@code SubmissionListViewCell}.
     */
    class SubmissionListViewCell extends ListCell<SubmissionInfo> {
        @Override
        protected void updateItem(SubmissionInfo submissionInfo, boolean empty) {
            super.updateItem(submissionInfo, empty);

            if (empty || submissionInfo == null) {
                setGraphic(null);
                setText(null);
            } else {
                var card = new SubmissionCard(submissionInfo, getIndex() + 1).getRoot();
                setGraphic(card);
            }
        }
    }
}
