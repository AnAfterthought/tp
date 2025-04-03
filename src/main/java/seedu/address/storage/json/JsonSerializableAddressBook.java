package seedu.address.storage.json;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.student.Student;
import seedu.address.model.uniquelist.exceptions.DuplicateItemException;
import seedu.address.model.uniquelist.exceptions.ItemNotFoundException;

/**
 * An Immutable AddressBook that is serializable to JSON format.
 */
@JsonRootName(value = "addressbook")
public class JsonSerializableAddressBook {
    public static final String MESSAGE_DUPLICATE_TUTORIAL = "Tutorials list contains duplicate tutorial(s).";
    public static final String MESSAGE_DUPLICATE_STUDENT = "Students list contains duplicate student(s).";

    private static final Logger LOGGER = Logger.getLogger(JsonSerializableAddressBook.class.getName());

    private final List<JsonAdaptedStudent> students;
    private final List<JsonAdaptedTutorial> tutorials;
    private final List<JsonAdaptedSubmission> submissions;
    private final List<JsonAdaptedAttendance> attendances;
    private final List<JsonAdaptedAssignment> assignments;

    /**
     * Constructs a {@link JsonSerializableAddressBook}
     */
    @JsonCreator
    public JsonSerializableAddressBook(@JsonProperty("students") List<JsonAdaptedStudent> students,
                    @JsonProperty("tutorials") List<JsonAdaptedTutorial> tutorials,
                    @JsonProperty("submissions") List<JsonAdaptedSubmission> submissions,
                    @JsonProperty("attendances") List<JsonAdaptedAttendance> attendances,
                    @JsonProperty("assignments") List<JsonAdaptedAssignment> assignments) {
        this.students = students;
        this.tutorials = tutorials;
        this.submissions = submissions;
        this.attendances = attendances;
        this.assignments = assignments;
    }

    /**
     * Converts a given {@code ReadOnlyAddressBook} into this class for Jackson use.
     *
     * @param source
     *            future changes to this will not affect the created
     *            {@code JsonSerializableAddressBook}.
     */
    public JsonSerializableAddressBook(ReadOnlyAddressBook source) {
        this(source.getStudentList().stream().map(JsonAdaptedStudent::new).collect(Collectors.toList()),
                        source.getTutorialList().stream().map(JsonAdaptedTutorial::new).toList(),
                        source.getSubmissionList().stream().map(JsonAdaptedSubmission::new).toList(),
                        source.getAttendanceList().stream().map(JsonAdaptedAttendance::new).toList(),
                        source.getTutorialList().stream()
                                        .flatMap(t -> t.assignments().stream().map(JsonAdaptedAssignment::new))
                                        .toList());
    }

    /**
     * Converts this address book into the model's {@link AddressBook} object. It
     * will try it's best to add into the addressbook, any errors will be ignored.
     */
    public AddressBook toModelType() throws DataLoadingException {
        AddressBook addressBook = new AddressBook();

        // Here's the sequence that the files needs to be loaded in
        // Insert the following objects in this order: Tutorial, Student, Attendance,
        // Assignment, Submission,
        // Settle these relationships
        // 1. Student, Tutorial, Attendance
        // 3. Student, Assignment, Submission

        tutorials.stream().map(JsonAdaptedTutorial::toModelType).forEach(addressBook::addTutorial);

        for (JsonAdaptedStudent jsonAdaptedStudent : students) {
            Student student = null;
            try {
                student = jsonAdaptedStudent.toModelType();
            } catch (IllegalValueException e) {
                LOGGER.warning("Failed to load student: %s".formatted(jsonAdaptedStudent));
                throw new DataLoadingException(e); // This is only thrown to follow current test cases
            }
            addressBook.addStudent(student);
        }

        for (var assignmentJson : assignments) {
            try {
                var a = assignmentJson.toModelType();
                addressBook.addAssignment(a);
            } catch (ItemNotFoundException | DuplicateItemException e) {
                LOGGER.warning("Failed to add assignment %s: %s".formatted(assignmentJson, e.getMessage()));
            }
        }

        for (var attendanceJson : attendances) {
            try {
                // The problem with this object is that it's not pointing to the correct values
                var a = attendanceJson.toModelType();
                addressBook.setAttendance(a);
            } catch (IllegalValueException e) {
                LOGGER.warning("Failed to load attendance: %s".formatted(e.getMessage()));
            } catch (ItemNotFoundException e) {
                LOGGER.warning("Failed to add attendance: %s".formatted(e.getMessage()));
            }
        }

        for (var submissionJson : submissions) {
            try {
                var submit = submissionJson.toModelType();
                addressBook.setSubmissionStatus(submit);
            } catch (IllegalValueException e) {
                LOGGER.warning("Failed to load submission: %s".formatted(e.getMessage()));
            } catch (ItemNotFoundException e) {
                LOGGER.warning("Failed to add submission %s: %s".formatted(submissionJson, e.getMessage()));
            } catch (CommandException e) {
                LOGGER.warning("Failed to add submission %s: %s".formatted(submissionJson, e.getMessage()));
            }
        }

        assert addressBook.check();

        return addressBook;
    }

}
