package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.logic.Messages.MESSAGE_ASSIGNMENT_NOT_FOUND;
import static seedu.address.logic.Messages.MESSAGE_TUTORIAL_INDEX_NOT_FOUND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_TUTORIAL;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.NavigationMode;
import seedu.address.model.UserPrefs;
import seedu.address.model.tutorial.Assignment;
import seedu.address.testutil.TypicalAddressBook;

public class DeleteAssignmentCommandTest {
    private static Model modelStub = new ModelManager(TypicalAddressBook.getTypicalAddressBook(), new UserPrefs());
    @BeforeEach
    void setUp() {
        modelStub = new ModelManager(TypicalAddressBook.getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void constructor_nullIndicesNullAssignment_throwsNullPointerException() {
        assertThrows(NullPointerException.class, (
        ) -> new DeleteAssignmentCommand(null, null));
    }

    @Test
    public void constructor_nullIndices_throwsNullPointerException() {
        assertThrows(NullPointerException.class, (
        ) -> new DeleteAssignmentCommand(null, new Assignment("new-assignment")));
    }

    @Test
    public void constructor_nullAssignment_throwsNullPointerException() {
        assertThrows(NullPointerException.class, (
        ) -> new DeleteAssignmentCommand(List.of(INDEX_FIRST_TUTORIAL), null));
    }

    @Test
    public void execute_outOfBoundsTutorialIndex_throwsCommandException() {
        Assignment assignment = new Assignment("new-assignment");
        int numTypicalTutorials = TypicalAddressBook.getTypicalAddressBook().getTutorialList().size();
        Index index = Index.fromOneBased(numTypicalTutorials + 1);
        List<Index> listOfOutOfBoundsIndex = List.of(index);
        DeleteAssignmentCommand deleteAssignmentCommand = new DeleteAssignmentCommand(listOfOutOfBoundsIndex,
                        assignment);

        modelStub.setNavigationMode(NavigationMode.TUTORIAL);
        assertThrows(CommandException.class, MESSAGE_TUTORIAL_INDEX_NOT_FOUND.formatted(index.getOneBased()), (
        ) -> deleteAssignmentCommand.execute(modelStub));
        modelStub.setNavigationMode(NavigationMode.STUDENT);
    }

    @Test
    public void execute_assignmentDoesNotExist_throwsCommandException() {
        var tut = modelStub.getFilteredTutorialList().get(INDEX_FIRST_TUTORIAL.getZeroBased());
        Assignment assignment = new Assignment("nonExistAssign", tut);
        DeleteAssignmentCommand deleteAssignmentCommand = new DeleteAssignmentCommand(List.of(INDEX_FIRST_TUTORIAL),
                        assignment);

        modelStub.setNavigationMode(NavigationMode.TUTORIAL);
        assertThrows(CommandException.class, MESSAGE_ASSIGNMENT_NOT_FOUND.formatted(assignment, tut), (
        ) -> deleteAssignmentCommand.execute(modelStub));
        modelStub.setNavigationMode(NavigationMode.STUDENT);
    }

    @Test
    public void execute_assignmentExists_success() throws Exception {
        modelStub.setNavigationMode(NavigationMode.TUTORIAL);
        Assignment assignment = new Assignment("Week 10 Tasks",
                        modelStub.getFilteredTutorialList().get(INDEX_FIRST_TUTORIAL.getZeroBased()));
        CommandResult commandResult = new DeleteAssignmentCommand(List.of(INDEX_FIRST_TUTORIAL), assignment)
                        .execute(modelStub);
        List<Assignment> assignments = modelStub.getAddressBook().getTutorialList()
                        .get(INDEX_FIRST_TUTORIAL.getZeroBased()).assignments();

        assertEquals(DeleteAssignmentCommand.MESSAGE_SUCCESS, commandResult.getFeedbackToUser());
        assertFalse(assignments.contains(assignment));

        modelStub.setNavigationMode(NavigationMode.STUDENT);
    }

    @Test
    public void execute_duplicateTutorialIndex_success() throws Exception {
        modelStub.setNavigationMode(NavigationMode.TUTORIAL);
        Assignment assignment = new Assignment("Week 10 Tasks",
                        modelStub.getFilteredTutorialList().get(INDEX_FIRST_TUTORIAL.getZeroBased()));
        CommandResult commandResult = new DeleteAssignmentCommand(List.of(Index.fromOneBased(1), Index.fromOneBased(1)),
                        assignment).execute(modelStub);
        List<Assignment> assignments = modelStub.getAddressBook().getTutorialList()
                        .get(INDEX_FIRST_TUTORIAL.getZeroBased()).assignments();

        assertEquals(DeleteAssignmentCommand.MESSAGE_SUCCESS, commandResult.getFeedbackToUser());
        assertFalse(assignments.contains(assignment));

        modelStub.setNavigationMode(NavigationMode.STUDENT);
    }
}
