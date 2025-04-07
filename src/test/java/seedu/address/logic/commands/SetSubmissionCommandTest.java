package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.TypicalAddressBook.ALICE;
import static seedu.address.testutil.TypicalAddressBook.T1;
import static seedu.address.testutil.TypicalAddressBook.T1_ASSIGN1;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.submission.SubmissionStatus;
import seedu.address.testutil.TypicalAddressBook;

public class SetSubmissionCommandTest {
    private Model modelStub;

    @BeforeEach
    public void setUp() {
        modelStub = new ModelManager(TypicalAddressBook.getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_setSubmissionDuplicateStudent_success() {
        // EP: Duplicate student
        var cmd = new SetSubmissionCommand(T1.name(), T1_ASSIGN1.name(),
                        List.of(ALICE.getName().toString(), ALICE.getName().toString()), SubmissionStatus.SUBMITTED);
        var result = assertDoesNotThrow((
        ) -> cmd.execute(modelStub));

        assertEquals(result.getFeedbackToUser(),
                        "Successfully set submission status for '%s'".formatted(ALICE.getName()));
    }
}
