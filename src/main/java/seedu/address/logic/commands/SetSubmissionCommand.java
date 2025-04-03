package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.NavigationMode;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentID;
import seedu.address.model.submission.SubmissionStatus;
import seedu.address.model.uniquelist.exceptions.ItemNotFoundException;

/**
 * Creates a new submission
 */
public class SetSubmissionCommand extends Command {

    public static final String COMMAND_WORD = "set";

    public static final String MESSAGE_SUCCESS = "Submission successfully set";
    private static final String MESSAGE_STUDENT_NOT_FOUND = "Cannot find student";

    private final String tutorialName;
    private final String assignmentName;
    private final List<StudentID> studentIdList;
    private final SubmissionStatus status;

    /**
     * Creates a {@link SetSubmissionCommand} object
     */
    public SetSubmissionCommand(String tutorialName, String assignmentName, List<StudentID> studentIdList,
                    SubmissionStatus status) {
        Objects.requireNonNull(tutorialName);
        Objects.requireNonNull(assignmentName);
        Objects.requireNonNull(studentIdList);

        this.tutorialName = tutorialName;
        this.assignmentName = assignmentName;
        this.studentIdList = studentIdList;
        this.status = status;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        var errMsg = new StringBuilder();

        for (var studentId : studentIdList) {
            Optional<Student> studentOptional = model.getFilteredStudentList().stream()
                            .filter(s -> s.getStudentId().equals(studentId)).findFirst();

            if (studentOptional.isEmpty()) {
                errMsg.append(String.format(MESSAGE_STUDENT_NOT_FOUND, studentId)).append("\n");
                continue;
            }

            Student student = studentOptional.get();

            try {
                model.setSubmissionStatus(tutorialName, assignmentName, student, status);
            } catch (ItemNotFoundException e) {
                throw new CommandException(e.getMessage());
            }
        }

        if (!errMsg.isEmpty()) {
            throw new CommandException("Warning: %s".formatted(errMsg.toString()));
        }

        return new CommandResult(MESSAGE_SUCCESS, NavigationMode.UNCHANGED);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof SetSubmissionCommand otherAddCommand)) {
            return false;
        }

        return tutorialName.equals(otherAddCommand.tutorialName)
                        && assignmentName.equals(otherAddCommand.assignmentName)
                        && studentIdList.equals(otherAddCommand.studentIdList) && status == otherAddCommand.status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("tutorialName", tutorialName).add("assignmentName", assignmentName)
                        .add("studentIdList", studentIdList).add("status", status).toString();

    }
}
