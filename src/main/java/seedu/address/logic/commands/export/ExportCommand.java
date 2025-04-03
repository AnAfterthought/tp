package seedu.address.logic.commands.export;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;

import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.model.Model;
import seedu.address.model.student.Student;
import seedu.address.model.tutorial.Tutorial;
import seedu.address.storage.csv.CsvListStorage;

/**
 * Exports the address book.
 */
public class ExportCommand extends Command {

    public static final String COMMAND_WORD = "export";
    public static final String MESSAGE_SUCCESS = "Students and tutorials exported successfully!";

    public static final String FILE_OPS_ERROR_FORMAT = "Could not export data due to the following error: %s";

    public static final String FILE_OPS_PERMISSION_ERROR_FORMAT = "Could not export data to file %s due to "
                    + "insufficient permissions to write to the file or the folder.";

    /**
     * Creates an {@link ExportCommand} to export the specified list
     */
    public ExportCommand() {
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);

        Path addressBookFilePath = model.getAddressBookFilePath();

        Path exportStudentFilePath = addressBookFilePath.resolveSibling(ExportStudentsCommand.STUDENT_CSV_FILE);
        CsvListStorage<Student> exportStudentStorage = new CsvListStorage<Student>(exportStudentFilePath);

        Path exportTutorialFilePath = addressBookFilePath.resolveSibling(ExportTutorialsCommand.TUTORIAL_CSV_FILE);
        CsvListStorage<Tutorial> exportTutorialStorage = new CsvListStorage<Tutorial>(exportTutorialFilePath);

        try {

            exportStudentStorage.saveCsvList(model.getFilteredStudentList());
            exportTutorialStorage.saveCsvList(model.getFilteredTutorialList());

        } catch (AccessDeniedException e) {
            return new CommandResult(String.format(FILE_OPS_PERMISSION_ERROR_FORMAT, e.getMessage()));
        } catch (IOException ioe) {
            return new CommandResult(String.format(FILE_OPS_ERROR_FORMAT, ioe.getMessage()));
        }

        return new CommandResult(MESSAGE_SUCCESS);
    }

}
