package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DETAILS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_HANDLE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID_STUDENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TUTORIAL_NAME;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.NavigationMode;
import seedu.address.model.student.NameContainsKeywordsPredicate;
import seedu.address.model.student.Student;
import seedu.address.testutil.EditStudentDescriptorBuilder;

/**
 * Contains helper methods for testing commands.
 */
public class CommandTestUtil {

    public static final String VALID_NAME_AMY = "Amy Bee";
    public static final String VALID_NAME_BOB = "Bob Choo";
    public static final String VALID_ID_AMY = "A0184759P";
    public static final String VALID_ID_BOB = "A0245659R";
    public static final String VALID_PHONE_AMY = "11111111";
    public static final String VALID_PHONE_BOB = "22222222";
    public static final String VALID_EMAIL_AMY = "amy@example.com";
    public static final String VALID_EMAIL_BOB = "bob@example.com";
    public static final String VALID_HANDLE_AMY = "@amy_bee";
    public static final String VALID_HANDLE_BOB = "@b0bch00";
    public static final String VALID_DETAILS_BOB = "";
    public static final String VALID_DETAILS_AMY = "Amy is very hardworking";
    public static final String VALID_TUTORIAL_1 = "CS2103-T1";
    public static final String VALID_TUTORIAL_2 = "CS2106-T37";

    public static final String NAME_DESC_AMY = " " + PREFIX_NAME + VALID_NAME_AMY;
    public static final String NAME_DESC_BOB = " " + PREFIX_NAME + VALID_NAME_BOB;
    public static final String ID_DESC_AMY = " " + PREFIX_ID_STUDENT + VALID_ID_AMY;
    public static final String ID_DESC_BOB = " " + PREFIX_ID_STUDENT + VALID_ID_BOB;
    public static final String PHONE_DESC_AMY = " " + PREFIX_PHONE + VALID_PHONE_AMY;
    public static final String PHONE_DESC_BOB = " " + PREFIX_PHONE + VALID_PHONE_BOB;
    public static final String EMAIL_DESC_AMY = " " + PREFIX_EMAIL + VALID_EMAIL_AMY;
    public static final String EMAIL_DESC_BOB = " " + PREFIX_EMAIL + VALID_EMAIL_BOB;
    public static final String HANDLE_DESC_AMY = " " + PREFIX_HANDLE + VALID_HANDLE_AMY;
    public static final String HANDLE_DESC_BOB = " " + PREFIX_HANDLE + VALID_HANDLE_BOB;
    public static final String DETAILS_DESC_BOB = " " + PREFIX_DETAILS + VALID_DETAILS_BOB;
    public static final String DETAILS_DESC_AMY = " " + PREFIX_HANDLE + VALID_DETAILS_AMY;
    public static final String TUTORIAL_DESC_1 = " " + PREFIX_TUTORIAL_NAME + VALID_TUTORIAL_1;
    public static final String TUTORIAL_DESC_2 = " " + PREFIX_TUTORIAL_NAME + VALID_TUTORIAL_2;

    public static final String INVALID_NAME_DESC = " " + PREFIX_NAME + "James&"; // '&' not allowed in names
    public static final String INVALID_ID_DESC = " " + PREFIX_ID_STUDENT + "a024$$m"; // not following format
    public static final String INVALID_PHONE_DESC = " " + PREFIX_PHONE + "911a"; // 'a' not allowed in phones
    public static final String INVALID_EMAIL_DESC = " " + PREFIX_EMAIL + "bob!yahoo"; // missing '@' symbol
    public static final String INVALID_HANDLE_DESC = " " + PREFIX_HANDLE + " "; // empty string not allowed for handle
    public static final String INVALID_TUTORIAL_DESC = " " + PREFIX_TUTORIAL_NAME + "Tu+or!a/"; // no special characters

    public static final String PREAMBLE_WHITESPACE = "\t  \r  \n";
    public static final String PREAMBLE_NON_EMPTY = "NonEmptyPreamble";

    public static final EditCommand.EditStudentDescriptor DESC_AMY;
    public static final EditCommand.EditStudentDescriptor DESC_BOB;

    static {
        DESC_AMY = new EditStudentDescriptorBuilder().withName(VALID_NAME_AMY).withStudentId(VALID_ID_AMY)
                        .withPhone(VALID_PHONE_AMY).withEmail(VALID_EMAIL_AMY).withHandle(VALID_HANDLE_AMY)
                        .withTutorials(VALID_TUTORIAL_1).build();
        DESC_BOB = new EditStudentDescriptorBuilder().withName(VALID_NAME_BOB).withStudentId(VALID_ID_BOB)
                        .withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_BOB).withHandle(VALID_HANDLE_BOB)
                        .withTutorials(VALID_TUTORIAL_2, VALID_TUTORIAL_1).build();
    }

    /**
     * Executes the given {@code command}, confirms that <br>
     * - the returned {@link CommandResult} matches {@code expectedCommandResult}
     * <br>
     * - the {@code actualModel} matches {@code expectedModel}
     */
    public static void assertCommandSuccess(Command command, Model actualModel, CommandResult expectedCommandResult,
                    Model expectedModel) {
        CommandResult result = assertDoesNotThrow((
        ) -> command.execute(actualModel));
        assertEquals(expectedCommandResult, result);
        assertEquals(expectedModel, actualModel);
    }

    /**
     * Convenience wrapper to
     * {@link #assertCommandSuccess(Command, Model, CommandResult, Model)} that
     * takes a string {@code expectedMessage} and {@code expectedNavigationMode}.
     */
    public static void assertCommandSuccess(Command command, Model actualModel, String expectedMessage,
                    NavigationMode expectedNavigationMode, Model expectedModel) {
        CommandResult expectedCommandResult = new CommandResult(expectedMessage, expectedNavigationMode);
        assertCommandSuccess(command, actualModel, expectedCommandResult, expectedModel);
    }

    /**
     * Convenience wrapper to
     * {@link #assertCommandSuccess(Command, Model, CommandResult, Model)} that
     * takes a string {@code expectedMessage}.
     */
    public static void assertCommandSuccess(Command command, Model actualModel, String expectedMessage,
                    Model expectedModel) {
        CommandResult expectedCommandResult = new CommandResult(expectedMessage);
        assertCommandSuccess(command, actualModel, expectedCommandResult, expectedModel);
    }

    /**
     * Executes the given {@code command}, confirms that <br>
     * - a {@code CommandException} is thrown <br>
     * - the CommandException message matches {@code expectedMessage} <br>
     * - the address book, filtered student list and selected student in
     * {@code actualModel} remain unchanged
     */
    public static void assertCommandFailure(Command command, Model actualModel, String expectedMessage) {
        // we are unable to defensively copy the model for comparison later, so we can
        // only do so by copying its components.
        AddressBook expectedAddressBook = new AddressBook(actualModel.getAddressBook());
        List<Student> expectedFilteredList = new ArrayList<>(actualModel.getFilteredStudentList());

        assertThrows(CommandException.class, expectedMessage, (
        ) -> command.execute(actualModel));
        assertEquals(expectedAddressBook, actualModel.getAddressBook());
        assertEquals(expectedFilteredList, actualModel.getFilteredStudentList());
    }
    /**
     * Updates {@code model}'s filtered list to show only the student at the given
     * {@code targetIndex} in the {@code model}'s address book.
     */
    public static void showStudentAtIndex(Model model, Index targetIndex) {
        assertTrue(targetIndex.getZeroBased() < model.getFilteredStudentList().size());

        Student student = model.getFilteredStudentList().get(targetIndex.getZeroBased());
        final String[] splitName = student.getName().fullName.split("\\s+");
        model.updateFilteredStudentList(new NameContainsKeywordsPredicate(Arrays.asList(splitName[0])));

        assertEquals(1, model.getFilteredStudentList().size());
    }

}
