package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_TUTORIAL_INDEX_NOT_FOUND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_TUTORIAL;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.NavigationMode;
import seedu.address.model.UserPrefs;
import seedu.address.model.tutorial.Assignment;
import seedu.address.model.tutorial.Tutorial;
import seedu.address.testutil.TypicalAddressBook;

public class AddAssignmentCommandTest {
    private static Model modelStub = new ModelManager(TypicalAddressBook.getTypicalAddressBook(), new UserPrefs());

    @BeforeEach
    void setUp() {
        modelStub = new ModelManager(TypicalAddressBook.getTypicalAddressBook(), new UserPrefs());
        modelStub.setNavigationMode(NavigationMode.TUTORIAL);
    }

    @AfterAll
    public static void tearDown() {
        modelStub.setNavigationMode(NavigationMode.STUDENT);
    }

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
        Index index = Index.fromOneBased(numTypicalTutorials + 1);
        List<Index> listOfOutOfBoundsIndex = List.of(index);

        AddAssignmentCommand addAssignmentCommand = new AddAssignmentCommand(listOfOutOfBoundsIndex,
                        new Assignment("new-assignment"));

        modelStub.setNavigationMode(NavigationMode.TUTORIAL);
        assertThrows(CommandException.class, MESSAGE_TUTORIAL_INDEX_NOT_FOUND.formatted(index.getOneBased()), (
        ) -> addAssignmentCommand.execute(modelStub));
        modelStub.setNavigationMode(NavigationMode.SUBMISSION);
    }

    @Test
    public void execute_duplicateAssignment_throwsCommandException() throws Exception {
        Assignment assignment = new Assignment("Week 10 Tasks");
        AddAssignmentCommand addAssignmentCommand = new AddAssignmentCommand(List.of(INDEX_FIRST_TUTORIAL), assignment);

        modelStub.setNavigationMode(NavigationMode.TUTORIAL);
        assertThrows(CommandException.class, AddAssignmentCommand.MESSAGE_DUPLICATE_ASSIGNMENT, (
        ) -> addAssignmentCommand.execute(modelStub));
        modelStub.setNavigationMode(NavigationMode.STUDENT);
    }

    @Test
    public void execute_assignmentAcceptedByModel_addSuccessful() throws Exception {
        modelStub.setNavigationMode(NavigationMode.TUTORIAL);
        Assignment assignment = new Assignment("new-assignment",
                        modelStub.getFilteredTutorialList().get(INDEX_FIRST_TUTORIAL.getZeroBased()));
        CommandResult commandResult = new AddAssignmentCommand(List.of(INDEX_FIRST_TUTORIAL), assignment)
                        .execute(modelStub);
        List<Tutorial> tutorials = modelStub.getAddressBook().getTutorialList();

        assertEquals(AddAssignmentCommand.MESSAGE_SUCCESS, commandResult.getFeedbackToUser());
        assertTrue(tutorials.get(INDEX_FIRST_TUTORIAL.getZeroBased()).assignments().containsIdentity(assignment));
        modelStub.setNavigationMode(NavigationMode.STUDENT);
    }

    @Test
    public void execute_duplicateTutorialIndex_success() throws Exception {
        // EP: Duplicate tutorial index

        modelStub.setNavigationMode(NavigationMode.TUTORIAL);
        Assignment assignment = new Assignment("new-assignment",
                        modelStub.getFilteredTutorialList().get(INDEX_FIRST_TUTORIAL.getZeroBased()));
        CommandResult commandResult = new AddAssignmentCommand(List.of(Index.fromOneBased(1), Index.fromOneBased(1)),
                        assignment).execute(modelStub);
        List<Tutorial> tutorials = modelStub.getAddressBook().getTutorialList();

        assertEquals(AddAssignmentCommand.MESSAGE_SUCCESS, commandResult.getFeedbackToUser());
        assertTrue(tutorials.get(INDEX_FIRST_TUTORIAL.getZeroBased()).assignments().containsIdentity(assignment));
        modelStub.setNavigationMode(NavigationMode.STUDENT);
    }
}
