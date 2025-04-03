package seedu.address.logic.commands.export;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.List;

import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.model.Model;
import seedu.address.model.tutorial.Tutorial;
import seedu.address.storage.csv.CsvListStorage;

/**
 * Exports the tutorial list.
 */
public class ExportTutorialsCommand extends Command {

    public static final String COMMAND_WORD = "tutorials";

    public static final String MESSAGE_USAGE = "Usage: export tutorials";

    public static final String MESSAGE_SUCCESS = "Tutorials exported successfully!";

    public static final String TUTORIAL_CSV_FILE = "tutorials.csv";

    private List<Tutorial> toExport;

    /**
     * Creates a {@link ExportStudentsCommand} to export all students
     */
    public ExportTutorialsCommand() {
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);

        Path addressBookFilePath = model.getAddressBookFilePath();
        Path exportTutorialFilePath = addressBookFilePath.resolveSibling(TUTORIAL_CSV_FILE);
        CsvListStorage<Tutorial> exportTutorialStorage = new CsvListStorage<Tutorial>(exportTutorialFilePath);

        try {
            exportTutorialStorage.saveCsvList(model.getFilteredTutorialList());
        } catch (AccessDeniedException e) {
            return new CommandResult(String.format(ExportCommand.FILE_OPS_PERMISSION_ERROR_FORMAT, e.getMessage()));
        } catch (IOException ioe) {
            return new CommandResult(String.format(ExportCommand.FILE_OPS_ERROR_FORMAT, ioe.getMessage()));
        }

        return new CommandResult(MESSAGE_SUCCESS);
    }

}
