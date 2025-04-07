package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.NavigationMode;
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
    private final List<String> studentList;
    private final SubmissionStatus status;

    /**
     * Creates a {@link SetSubmissionCommand} object
     */
    public SetSubmissionCommand(String tutorialName, String assignmentName, List<String> studentList,
                    SubmissionStatus status) {
        Objects.requireNonNull(tutorialName);
        Objects.requireNonNull(assignmentName);
        Objects.requireNonNull(studentList);

        this.tutorialName = tutorialName;
        this.assignmentName = assignmentName;
        this.studentList = studentList;
        this.status = status;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        var result = new ArrayList<String>();
        var hasError = false;
        for (var studentName : new LinkedHashSet<>(studentList)) {
            try {
                model.setSubmissionStatus(tutorialName, assignmentName, studentName, status);
            } catch (ItemNotFoundException | CommandException e) {
                result.add(e.getMessage());
                hasError = true;
                continue;
            }

            result.add("Successfully set submission status for '%s'".formatted(studentName));
        }

        assert model.check();

        var msg = String.join("\n", result);
        if (hasError) {
            throw new CommandException(msg);
        }

        return new CommandResult(msg, NavigationMode.UNCHANGED);
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
                        && studentList.equals(otherAddCommand.studentList) && status == otherAddCommand.status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("tutorialName", tutorialName).add("assignmentName", assignmentName)
                        .add("studentIdxList", studentList).add("status", status).toString();

    }
}
