package seedu.address.ui.student;

import java.util.Comparator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import seedu.address.model.student.Student;
import seedu.address.ui.UiPart;

/**
 * An UI component that displays specific information of a {@code Student}.
 */
public class StudentArea extends UiPart<Region> {

    private static final String FXML = "StudentComponents/StudentArea.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved
     * keywords in JavaFX. As a consequence, UI elements' variable names cannot be
     * set to such keywords or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The
     *      issue on AddressBook level 4</a>
     */

    private final Student student;

    @FXML
    private Label studentNameLabel;
    @FXML
    private Label studentIdLabel;
    @FXML
    private Label studentPhoneLabel;
    @FXML
    private Label studentEmailLabel;
    @FXML
    private Label studentHandleLabel;
    @FXML
    private Label studentDetailTextArea;
    @FXML
    private FlowPane tutorialsContainer;

    /**
     * Creates a {@code StudentCode} with the given default values to display.
     */
    public StudentArea() {
        super(FXML);
        this.student = null;
    }

    /**
     * Updates the displayed student information.
     *
     * @param student
     *            the new student object to display
     */
    public void updateStudent(Student student) {
        if (student != null) {
            studentNameLabel.setText(student.getName().fullName);
            studentIdLabel.setText(student.getStudentId().id);
            studentPhoneLabel.setText(student.getPhone().value);
            studentHandleLabel.setText(student.getHandle().handle);
            studentEmailLabel.setText(student.getEmail().value);
            studentDetailTextArea.setText(student.getDetails().value);
            tutorialsContainer.getChildren().clear();
            student.getTutorials().stream().sorted(Comparator.comparing(tutorial -> tutorial.name()))
                            .forEach(tutorial -> tutorialsContainer.getChildren().add(new Label(tutorial.name())));
        } else {
            studentNameLabel.setText("");
            studentIdLabel.setText("");
            studentPhoneLabel.setText("");
            studentHandleLabel.setText("");
            studentEmailLabel.setText("");
            studentDetailTextArea.setText("");
            tutorialsContainer.getChildren().clear();
        }
    }
}
