package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_TUTORIAL_NOT_FOUND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_TUTORIAL;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.tutorial.Assignment;
import seedu.address.model.tutorial.Tutorial;
import seedu.address.testutil.TypicalAddressBook;

public class AddAssignmentCommandTest {

    private static final Model modelStub = new ModelManager(TypicalAddressBook.getTypicalAddressBook(),
                    new UserPrefs());

    @Test
    public void constructor_nullTutorialIdxListNullAssignment_throwsNullPointerException() {
        assertThrows(NullPointerException.class, (
        ) -> new AddAssignmentCommand(null, null));
    }

    @Test
    public void constructor_nullTutorialIdxList_throwsNullPointerException() {
        assertThrows(NullPointerException.class, (
        ) -> new AddAssignmentCommand(null, new Assignment("new-assignment")));
    }

    @Test
    public void constructor_nullAssignment_throwsNullPointerException() {
        assertThrows(NullPointerException.class, (
        ) -> new AddAssignmentCommand(List.of(INDEX_FIRST_TUTORIAL), null));
    }

    @Test
    public void execute_outOfBoundsTutorialIndex_throwsCommandException() {
        int numTypicalTutorials = TypicalAddressBook.getTypicalAddressBook().getTutorialList().size();
        Index outOfBoundsIndex = Index.fromOneBased(numTypicalTutorials + 1);
        List<Index> listOfOutOfBoundsIndex = List.of(outOfBoundsIndex);

        AddAssignmentCommand addAssignmentCommand = new AddAssignmentCommand(listOfOutOfBoundsIndex,
                        new Assignment("new-assignment"));

        assertThrows(CommandException.class, MESSAGE_TUTORIAL_NOT_FOUND.formatted(outOfBoundsIndex.getOneBased()), (
        ) -> addAssignmentCommand.execute(modelStub));
    }

    @Test
    public void execute_duplicateAssignment_throwsCommandException() throws Exception {
        Assignment assignment = new Assignment("Week 10 Tasks");
        AddAssignmentCommand addAssignmentCommand = new AddAssignmentCommand(List.of(INDEX_FIRST_TUTORIAL), assignment);

        assertThrows(CommandException.class, AddAssignmentCommand.MESSAGE_DUPLICATE_ASSIGNMENT, (
        ) -> addAssignmentCommand.execute(modelStub));
    }

    @Test
    public void execute_assignmentAcceptedByModel_addSuccessful() throws Exception {
        Assignment assignment = new Assignment("new-assignment");
        CommandResult commandResult = new AddAssignmentCommand(List.of(INDEX_FIRST_TUTORIAL), assignment)
                        .execute(modelStub);
        List<Tutorial> tutorials = modelStub.getAddressBook().getTutorialList();

        assertEquals(AddAssignmentCommand.MESSAGE_SUCCESS, commandResult.getFeedbackToUser());
        assertTrue(tutorials.get(INDEX_FIRST_TUTORIAL.getZeroBased()).assignments().containsIdentity(assignment));
    }
}
