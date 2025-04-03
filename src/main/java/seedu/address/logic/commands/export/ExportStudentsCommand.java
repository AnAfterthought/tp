package seedu.address.logic.commands.export;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.model.Model;
import seedu.address.model.student.Student;
import seedu.address.model.tutorial.Tutorial;
import seedu.address.storage.csv.CsvListStorage;
import seedu.address.storage.json.JsonAddressBookStorage;

/**
 * Exports student lists.
 */
public class ExportStudentsCommand extends Command {

    public static final String COMMAND_WORD = "students";

    public static final String MESSAGE_USAGE = "Usage: export students [TUTORIAL_NAME]";

    public static final String MESSAGE_SUCCESS = "Students (%s) exported successfully!";
    public static final String MESSAGE_TUTORIAL_DOES_NOT_EXIST = "%1$s doesn't exist";

    public static final String STUDENT_CSV_FILE = "students.csv";
    public static final String STUDENT_BELONGING_TO_CSV_FILE = "students-%s.csv";

    private static final Logger logger = LogsCenter.getLogger(JsonAddressBookStorage.class);

    private final Optional<Tutorial> tutorial;
    private List<Student> toExport;

    /**
     * Creates a {@link ExportStudentsCommand} to export all students
     */
    public ExportStudentsCommand() {
        this.tutorial = Optional.empty();
    }

    /**
     * Creates a {@link ExportStudentsCommand} to export students from specified
     * {@code Tutorial}
     */
    public ExportStudentsCommand(Tutorial tutorial) {
        requireNonNull(tutorial);
        this.tutorial = Optional.of(tutorial);
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);

        Path addressBookFilePath = model.getAddressBookFilePath();

        Path exportStudentFilePath;
        List<Student> studentList = new ArrayList<>();

        String message = "";

        if (tutorial.isPresent()) {
            exportStudentFilePath = addressBookFilePath
                            .resolveSibling(String.format(STUDENT_BELONGING_TO_CSV_FILE, tutorial.get().name()));
            if (model.hasTutorial(tutorial.get())) {
                studentList.addAll(model.getStudentsInTutorial(tutorial.get()));
            } else {
                return new CommandResult(String.format(MESSAGE_TUTORIAL_DOES_NOT_EXIST, tutorial.get().name()));
            }
            message = String.format(MESSAGE_SUCCESS, tutorial.get().name());
        } else {
            exportStudentFilePath = addressBookFilePath.resolveSibling(STUDENT_CSV_FILE);
            studentList.addAll(model.getFilteredStudentList());
            message = String.format(MESSAGE_SUCCESS, "all tutorials");
        }

        CsvListStorage<Student> exportStudentStorage = new CsvListStorage<Student>(exportStudentFilePath);
        try {
            exportStudentStorage.saveCsvList(studentList);
        } catch (AccessDeniedException e) {
            logger.info("Access Denied Error (" + exportStudentFilePath + "): " + e.getMessage());
            return new CommandResult(String.format(ExportCommand.FILE_OPS_PERMISSION_ERROR_FORMAT, e.getMessage()));
        } catch (IOException ioe) {
            logger.info("IO Exception (" + exportStudentFilePath + "): " + ioe.getMessage());
            return new CommandResult(String.format(ExportCommand.FILE_OPS_ERROR_FORMAT, ioe.getMessage()));
        }

        return new CommandResult(message);

    }

}
