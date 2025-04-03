package seedu.address.ui;

import static seedu.address.logic.Messages.MESSAGE_INVALID_NAVIGATION_MODE;

import java.util.HashMap;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.NavigationMode;
import seedu.address.model.student.Student;
import seedu.address.ui.attendence.AttendanceListPanel;
import seedu.address.ui.misc.CommandBox;
import seedu.address.ui.misc.HelpWindow;
import seedu.address.ui.misc.ResultDisplay;
import seedu.address.ui.misc.StatusBarFooter;
import seedu.address.ui.student.StudentArea;
import seedu.address.ui.student.StudentListPanel;
import seedu.address.ui.submission.SubmissionListPanel;
import seedu.address.ui.tutorial.TutorialListPanel;

/**
 * The Main Window. Provides the basic application layout containing a menu bar
 * and space where other JavaFX elements can be placed.
 */
public class MainWindow extends UiPart<Stage> {

    private static final String FXML = "MainWindow.fxml";

    private final Logger logger = LogsCenter.getLogger(getClass());

    private Stage primaryStage;
    private Logic logic;

    // Independent Ui parts residing in this Ui container
    private StudentListPanel studentListPanel;
    private TutorialListPanel tutorialListPanel;
    private AttendanceListPanel attendanceListPanel;
    private SubmissionListPanel submissionListPanel;
    private ResultDisplay resultDisplay;
    private HelpWindow helpWindow;
    private StatusBarFooter statusBarFooter;

    @FXML
    private StackPane commandBoxPlaceholder;

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    private VBox studentList;

    @FXML
    private VBox tutorialList;

    @FXML
    private VBox studentArea;

    @FXML
    private VBox attendanceList;

    @FXML
    private VBox submissionList;

    @FXML
    private StackPane studentListPanelPlaceholder;

    @FXML
    private StackPane tutorialListPanelPlaceholder;

    @FXML
    private VBox studentAreaPlaceholder;

    @FXML
    private StackPane attendanceListPanelPlaceholder;

    @FXML
    private StackPane submissionListPanelPlaceholder;

    @FXML
    private StackPane resultDisplayPlaceholder;

    @FXML
    private StackPane statusbarPlaceholder;

    @FXML
    private SplitPane mainSplitPane;

    /**
     * Creates a {@code MainWindow} with the given {@code Stage} and {@code Logic}.
     */
    public MainWindow(Stage primaryStage, Logic logic) {
        super(FXML, primaryStage);

        // Set dependencies
        this.primaryStage = primaryStage;
        this.logic = logic;

        Font.loadFont(getClass().getResourceAsStream("/fonts/Livvic-Bold.ttf"), 12);
        Font.loadFont(getClass().getResourceAsStream("/fonts/Livvic-Regular.ttf"), 12);

        // Configure the UI
        setWindowDefaultSize(logic.getGuiSettings());
        setUiVisibilities(logic.getNavigationMode());

        setAccelerators();

        helpWindow = new HelpWindow();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private void setAccelerators() {
        setAccelerator(helpMenuItem, KeyCombination.valueOf("F1"));
    }

    /**
     * Sets the accelerator of a MenuItem.
     *
     * @param keyCombination
     *            the KeyCombination value of the accelerator
     */
    private void setAccelerator(MenuItem menuItem, KeyCombination keyCombination) {
        menuItem.setAccelerator(keyCombination);

        /*
         * TODO: the code below can be removed once the bug reported here
         * https://bugs.openjdk.java.net/browse/JDK-8131666 is fixed in later version of
         * SDK.
         *
         * According to the bug report, TextInputControl (TextField, TextArea) will
         * consume function-key events. Because CommandBox contains a TextField, and
         * ResultDisplay contains a TextArea, thus some accelerators (e.g F1) will not
         * work when the focus is in them because the key event is consumed by the
         * TextInputControl(s).
         *
         * For now, we add following event filter to capture such key events and open
         * help window purposely so to support accelerators even when focus is in
         * CommandBox or ResultDisplay.
         */
        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getTarget() instanceof TextInputControl && keyCombination.match(event)) {
                menuItem.getOnAction().handle(new ActionEvent());
                event.consume();
            }
        });
    }

    /**
     * Fills up all the placeholders of this window.
     */
    void fillInnerParts() {
        studentListPanel = new StudentListPanel(logic.getFilteredStudentList());
        studentListPanelPlaceholder.getChildren().add(studentListPanel.getRoot());

        tutorialListPanel = new TutorialListPanel(logic.getFilteredTutorialList());
        tutorialListPanelPlaceholder.getChildren().add(tutorialListPanel.getRoot());

        ObjectProperty<Student> selectedStudentProperty = logic.getSelectedStudent();
        StudentArea studentArea = new StudentArea();

        selectedStudentProperty.addListener((
                        observable, oldValue, newValue
        ) -> {
            logger.info("Student selection changed from " + oldValue + " to " + newValue);
            if (newValue != null) {
                studentArea.updateStudent(newValue);
            } else {
                studentArea.updateStudent(null);
            }
            studentAreaPlaceholder.getChildren().clear();
            studentAreaPlaceholder.getChildren().add(studentArea.getRoot());
        });

        attendanceListPanel = new AttendanceListPanel(logic.getFilteredAttendanceList());
        attendanceListPanelPlaceholder.getChildren().add(attendanceListPanel.getRoot());

        submissionListPanel = new SubmissionListPanel(logic.getFilteredSubmissionList());
        submissionListPanelPlaceholder.getChildren().add(submissionListPanel.getRoot());

        resultDisplay = new ResultDisplay();
        resultDisplayPlaceholder.getChildren().add(resultDisplay.getRoot());

        statusBarFooter = new StatusBarFooter(logic.getAddressBookFilePath(), logic.getNavigationMode());
        statusbarPlaceholder.getChildren().add(statusBarFooter.getRoot());

        CommandBox commandBox = new CommandBox(this::executeCommand);
        commandBoxPlaceholder.getChildren().add(commandBox.getRoot());
    }

    /**
     * Sets the default size based on {@code guiSettings}.
     */
    private void setWindowDefaultSize(GuiSettings guiSettings) {
        primaryStage.setHeight(guiSettings.getWindowHeight());
        primaryStage.setWidth(guiSettings.getWindowWidth());
        if (guiSettings.getWindowCoordinates() != null) {
            primaryStage.setX(guiSettings.getWindowCoordinates().getX());
            primaryStage.setY(guiSettings.getWindowCoordinates().getY());
        }
    }

    /**
     * Sets the visibility of the student and tutorial list UI elements according to
     * the specified navigation mode.
     */
    private void setUiVisibilities(NavigationMode navigationMode) {
        var modes = new HashMap<NavigationMode, VBox>();
        modes.put(NavigationMode.STUDENT, studentList);
        modes.put(NavigationMode.TUTORIAL, tutorialList);
        modes.put(NavigationMode.ATTENDANCE, attendanceList);
        modes.put(NavigationMode.SUBMISSION, submissionList);
        modes.put(NavigationMode.SINGLE_STUDENT, studentAreaPlaceholder);

        // Hide all modes
        if (navigationMode == NavigationMode.UNCHANGED) {
            return;
        }

        if (!modes.containsKey(navigationMode)) {
            throw new IllegalArgumentException(MESSAGE_INVALID_NAVIGATION_MODE);
        }

        for (var m : modes.entrySet()) {
            setElementVisibility(m.getValue(), m.getKey() == navigationMode);
        }
    }

    /**
     * Sets the visibility and manageability of the specified UI element.
     */
    private void setElementVisibility(Node element, boolean isVisible) {
        element.setVisible(isVisible);
        element.setManaged(isVisible);
    }

    /**
     * Sets the navigation mode in {@code GuiSettings} to the navigation mode
     * specified.
     */
    private void setNavigationMode(NavigationMode navigationMode) {
        logic.setNavigationMode(navigationMode);
    }

    /**
     * Sets the navigation mode to the specified mode and updates the visibilities
     * of UI elements, and updates the status bar footer accordingly, if needed.
     */
    private void handleMode(NavigationMode navigationMode) {
        if (navigationMode == NavigationMode.UNCHANGED) {
            return;
        }
        setNavigationMode(navigationMode);
        setUiVisibilities(navigationMode);
        statusBarFooter.setNavigationMode(navigationMode);
    }

    /**
     * Opens the help window or focuses on it if it's already opened.
     */
    @FXML
    public void handleHelp() {
        if (!helpWindow.isShowing()) {
            helpWindow.show();
        } else {
            helpWindow.focus();
        }
    }

    void show() {
        primaryStage.show();
        Platform.runLater((
        ) -> mainSplitPane.setDividerPosition(0, 0.8));
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        GuiSettings guiSettings = new GuiSettings(primaryStage.getWidth(), primaryStage.getHeight(),
                        (int) primaryStage.getX(), (int) primaryStage.getY(),
                        logic.getGuiSettings().getNavigationMode());
        logic.setGuiSettings(guiSettings);
        helpWindow.hide();
        primaryStage.hide();
    }

    public StudentListPanel getStudentListPanel() {
        return studentListPanel;
    }

    /**
     * Executes the command and returns the result.
     *
     * @see seedu.address.logic.Logic#execute(String)
     */
    private CommandResult executeCommand(String commandText) throws CommandException, ParseException {
        try {
            CommandResult commandResult = logic.execute(commandText);
            logger.info("Result: " + commandResult.getFeedbackToUser());
            resultDisplay.setFeedbackToUser(commandResult.getFeedbackToUser());

            handleMode(commandResult.getResultingMode());

            if (commandResult.isShowHelp()) {
                handleHelp();
            }

            if (commandResult.isExit()) {
                handleExit();
            }

            return commandResult;
        } catch (CommandException | ParseException e) {
            logger.info("An error occurred while executing command: " + commandText);
            resultDisplay.setFeedbackToUser(e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            logger.info("An unexpected error occurred while executing command: " + commandText);
            resultDisplay.setFeedbackToUser(e.getMessage());
            throw e;
        }
    }
}
