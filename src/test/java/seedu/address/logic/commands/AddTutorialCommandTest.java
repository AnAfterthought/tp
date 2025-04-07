package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.tutorial.Tutorial;
import seedu.address.testutil.TypicalAddressBook;

public class AddTutorialCommandTest {

    private static Model modelStub = new ModelManager(TypicalAddressBook.getTypicalAddressBook(), new UserPrefs());
    @BeforeEach
    void setUp() {
        modelStub = new ModelManager(TypicalAddressBook.getTypicalAddressBook(), new UserPrefs());

    }

    @Test
    public void constructor_nullTutorial_throwsNullPointerException() {
        assertThrows(NullPointerException.class, (
        ) -> new AddTutorialCommand(null));
    }

    @Test
    public void execute_tutorialAcceptedByModel_addSuccessful() throws Exception {
        var t = new Tutorial("new-tutorial");
        CommandResult commandResult = new AddTutorialCommand(t).execute(modelStub);
        var tutorials = modelStub.getAddressBook().getTutorialList();

        assertEquals(AddTutorialCommand.MESSAGE_SUCCESS.formatted(t), commandResult.getFeedbackToUser());
        assertTrue(tutorials.contains(t));
    }

    @Test
    public void execute_duplicateTutorial_throwsCommandException() {
        var cmd = new AddTutorialCommand(new Tutorial("CS2103-T1"));

        assertThrows(CommandException.class, AddTutorialCommand.MESSAGE_DUPLICATE_TUTORIAL, (
        ) -> cmd.execute(modelStub));
    }
}
