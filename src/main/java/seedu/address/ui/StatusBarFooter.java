package seedu.address.ui;

import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import seedu.address.model.NavigationMode;

/**
 * A ui for the status bar that is displayed at the footer of the application.
 */
public class StatusBarFooter extends UiPart<Region> {

    private static final String FXML = "StatusBarFooter.fxml";

    @FXML
    private Label saveLocationStatus;

    @FXML
    private Label navigationModeStatus;

    /**
     * Creates a {@code StatusBarFooter} with the given {@code Path}.
     */
    public StatusBarFooter(Path saveLocation, NavigationMode navigationMode) {
        super(FXML);
        saveLocationStatus.setText(Paths.get(".").resolve(saveLocation).toString());
        setNavigationMode(navigationMode);
    }

    /**
     * Sets the navigation mode status in the {@code StatusBarFooter} according to
     * the specified {@code NavigationMode}.
     */
    public void setNavigationMode(NavigationMode navigationMode) {
        navigationModeStatus.setText(navigationMode.toString());
    }
}
