package seedu.address.logic;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.parser.Prefix;
import seedu.address.model.student.Student;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format! \n%1$s";
    public static final String MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX = "The student index provided is invalid";
    public static final String MESSAGE_STUDENTS_LISTED_OVERVIEW = "%1$d students listed!";
    public static final String MESSAGE_TUTORIALS_LISTED_OVERVIEW = "%1$d tutorials listed!";
    public static final String MESSAGE_DUPLICATE_FIELDS = "Multiple values specified for the following "
                    + "single-valued field(s): ";
    public static final String MESSAGE_INVALID_NAVIGATION_MODE = "Invalid navigation mode provided";
    public static final String MESSAGE_INCORRECT_NAVIGATION_MODE = "Incorrect mode, correct mode is '%s',"
                    + " switching to it now";
    public static final String MESSAGE_UNKNOWN_ERROR = "Something went wrong";

    public static final String MESSAGE_TUTORIAL_NOT_FOUND = "Tutorial '%s' is not found";
    public static final String MESSAGE_TUTORIAL_INDEX_NOT_FOUND = "Tutorial index '%s' is not found";
    public static final String MESSAGE_ASSIGNMENT_NOT_FOUND = "Assignment '%s' is not found in Tutorial '%s'";
    public static final String MESSAGE_STUDENT_NOT_FOUND = "Student '%s' is not found";

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields = Stream.of(duplicatePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    /**
     * Formats the {@code Student} for display to the user.
     */
    public static String format(Student student) {
        final StringBuilder builder = new StringBuilder();
        builder.append(student.getName()).append("; Student ID: ").append(student.getStudentId()).append("; Phone: ")
                        .append(student.getPhone()).append("; Email: ").append(student.getEmail())
                        .append("; Telegram Handle: ").append(student.getHandle()).append("; Details: ")
                        .append(student.getDetails()).append("; Tutorials: ");
        student.getTutorials().forEach(builder::append);
        return builder.toString();
    }
}
