package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalAddressBook.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.submission.SubmissionStatus;

/**
 * Consists of tests for the SetSubmissionCommand class
 */
public class SetSubmissionCommandtest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    /**
     * Tests that should fail if the student is not in the tutorial slot
     */
    @Test
    public void execute_studentNotInTutorial_throwCommandException() throws CommandException {
        var cmd = new SetSubmissionCommand("CS2106-T02", "Week 10 Tasks", List.of("Alice Pauline"),
                        SubmissionStatus.NOT_SUBMITTED);

        var exception = assertThrows(CommandException.class, (
        ) -> cmd.execute(model));
        assertTrue(exception.getMessage().matches("'.*' not in '.*'"), "Actual: %s".formatted(exception.getMessage()));

    }

    /**
     * Tests that should fail if the assignment not in tutorial
     */
    @Test
    public void execute_assignmentNotInTutorial_throwCommandException() throws CommandException {
        var cmd = new SetSubmissionCommand("CS2106-T02", "Lab 1", List.of("Alice Pauline"),
                        SubmissionStatus.NOT_SUBMITTED);

        var exception = assertThrows(CommandException.class, (
        ) -> cmd.execute(model));
        assertTrue(exception.getMessage().matches("Assignment '.*' is not found in Tutorial '.*'"),
                        "Actual: %s".formatted(exception.getMessage()));

    }
}
