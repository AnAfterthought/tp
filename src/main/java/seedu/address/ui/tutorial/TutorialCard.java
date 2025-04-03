package seedu.address.ui.tutorial;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.tutorial.Tutorial;
import seedu.address.ui.UiPart;

/**
 * An UI component that displays information of a {@code Tutorial}.
 */
public class TutorialCard extends UiPart<Region> {

    private static final String FXML = "TutorialComponents/TutorialListCard.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved
     * keywords in JavaFX. As a consequence, UI elements' variable names cannot be
     * set to such keywords or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The
     *      issue on AddressBook level 4</a>
     */

    public final Tutorial tutorial;
    @FXML
    private HBox cardPane;
    @FXML
    private Label tutorialName;
    @FXML
    private Label tutorialId;
    @FXML
    private FlowPane assignments;

    /**
     * Creates a {@code TutorialCard} with the given {@code Tutorial} and index to
     * display.
     */
    public TutorialCard(Tutorial tutorial, int displayedIndex) {
        super(FXML);
        this.tutorial = tutorial;
        tutorialId.setText(displayedIndex + "");
        tutorialName.setText(tutorial.name());

        assignments.getChildren().clear();

        tutorial.assignments().stream()
                        .forEach(assignment -> assignments.getChildren().add(new Label(assignment.toString())));

        assignments.setManaged(!tutorial.assignments().isEmpty());
        assignments.setVisible(!tutorial.assignments().isEmpty());
    }
}
