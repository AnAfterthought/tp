package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.NavigationMode;

/**
 * Creates a new submission
 */
public class ListSubmissionCommand extends Command {

    public static final String COMMAND_WORD = "list";

    public static final String MESSAGE_SUCCESS = "Successfully listed submissions";

    private final String tutorialName;
    private final String studentName;
    private final String assignmentName;

    /**
     * Creates a {@link SetSubmissionCommand} object
     */
    public ListSubmissionCommand(String tutorialName, String studentName, String assignmentName) {
        this.tutorialName = tutorialName;
        this.studentName = studentName;
        this.assignmentName = assignmentName;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        model.updateFilteredSubmissionList(s -> s.assignment().name().toLowerCase()
                        .startsWith(assignmentName.toLowerCase())
                        && s.assignment().tutorial().name().toLowerCase().startsWith(tutorialName.toLowerCase())
                        && s.student().getName().toString().toLowerCase().startsWith(studentName.toLowerCase()));

        assert model.check();
        return new CommandResult(MESSAGE_SUCCESS, NavigationMode.SUBMISSION);
    }

}
