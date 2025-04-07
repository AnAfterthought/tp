package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.DETAILS_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.HANDLE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.HANDLE_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.ID_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.ID_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_EMAIL_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_HANDLE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_ID_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_PHONE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.PREAMBLE_NON_EMPTY;
import static seedu.address.logic.commands.CommandTestUtil.PREAMBLE_WHITESPACE;
import static seedu.address.logic.commands.CommandTestUtil.TUTORIAL_DESC_1;
import static seedu.address.logic.commands.CommandTestUtil.TUTORIAL_DESC_2;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_HANDLE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ID_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TUTORIAL_1;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TUTORIAL_2;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_HANDLE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID_STUDENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalAddressBook.AMY;
import static seedu.address.testutil.TypicalAddressBook.BOB;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddCommand;
import seedu.address.model.student.Email;
import seedu.address.model.student.Name;
import seedu.address.model.student.Phone;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentID;
import seedu.address.model.student.TelegramHandle;
import seedu.address.testutil.StudentBuilder;

public class AddCommandParserTest {
    private AddCommandParser parser = new AddCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        Student expectedStudent = new StudentBuilder(BOB).build();

        // whitespace only preamble
        assertParseSuccess(parser, PREAMBLE_WHITESPACE + NAME_DESC_BOB + ID_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                        + HANDLE_DESC_BOB + DETAILS_DESC_BOB, new AddCommand(expectedStudent));

        // multiple tags - all accepted
        Student expectedStudentMultipleTags = new StudentBuilder(BOB).withTutorials(VALID_TUTORIAL_1, VALID_TUTORIAL_2)
                        .build();
        assertParseSuccess(parser, NAME_DESC_BOB + ID_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + HANDLE_DESC_BOB
                        + DETAILS_DESC_BOB, new AddCommand(expectedStudentMultipleTags));
    }

    @Test
    public void parse_repeatedNonTagValue_failure() {
        String validExpectedStudentString = NAME_DESC_BOB + ID_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                        + HANDLE_DESC_BOB + TUTORIAL_DESC_1;

        // multiple names
        assertParseFailure(parser, NAME_DESC_AMY + validExpectedStudentString,
                        Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));

        // multiple student id
        assertParseFailure(parser, ID_DESC_AMY + validExpectedStudentString,
                        Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ID_STUDENT));

        // multiple phones
        assertParseFailure(parser, PHONE_DESC_AMY + validExpectedStudentString,
                        Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // multiple emails
        assertParseFailure(parser, EMAIL_DESC_AMY + validExpectedStudentString,
                        Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EMAIL));

        // multiple addresses
        assertParseFailure(parser, HANDLE_DESC_AMY + validExpectedStudentString,
                        Messages.getErrorMessageForDuplicatePrefixes(PREFIX_HANDLE));

        // multiple fields repeated
        assertParseFailure(parser,
                        validExpectedStudentString + ID_DESC_AMY + PHONE_DESC_AMY + EMAIL_DESC_AMY + NAME_DESC_AMY
                                        + HANDLE_DESC_AMY + validExpectedStudentString,
                        Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME, PREFIX_ID_STUDENT, PREFIX_HANDLE,
                                        PREFIX_EMAIL, PREFIX_PHONE));

        // invalid value followed by valid value

        // invalid name
        assertParseFailure(parser, INVALID_NAME_DESC + validExpectedStudentString,
                        Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));

        // invalid student id
        assertParseFailure(parser, INVALID_ID_DESC + validExpectedStudentString,
                        Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ID_STUDENT));

        // invalid email
        assertParseFailure(parser, INVALID_EMAIL_DESC + validExpectedStudentString,
                        Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EMAIL));

        // invalid phone
        assertParseFailure(parser, INVALID_PHONE_DESC + validExpectedStudentString,
                        Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // invalid address
        assertParseFailure(parser, INVALID_HANDLE_DESC + validExpectedStudentString,
                        Messages.getErrorMessageForDuplicatePrefixes(PREFIX_HANDLE));

        // valid value followed by invalid value

        // invalid name
        assertParseFailure(parser, validExpectedStudentString + INVALID_NAME_DESC,
                        Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));

        // invalid student id
        assertParseFailure(parser, validExpectedStudentString + INVALID_ID_DESC,
                        Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ID_STUDENT));

        // invalid email
        assertParseFailure(parser, validExpectedStudentString + INVALID_EMAIL_DESC,
                        Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EMAIL));

        // invalid phone
        assertParseFailure(parser, validExpectedStudentString + INVALID_PHONE_DESC,
                        Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // invalid address
        assertParseFailure(parser, validExpectedStudentString + INVALID_HANDLE_DESC,
                        Messages.getErrorMessageForDuplicatePrefixes(PREFIX_HANDLE));
    }

    @Test
    public void parse_optionalFieldsMissing_success() {
        // zero tags
        Student expectedStudent = new StudentBuilder(AMY).withTutorials().build();
        assertParseSuccess(parser, NAME_DESC_AMY + ID_DESC_AMY + PHONE_DESC_AMY + EMAIL_DESC_AMY + HANDLE_DESC_AMY,
                        new AddCommand(expectedStudent));
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);

        // missing name prefix
        assertParseFailure(parser, VALID_NAME_BOB + ID_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + HANDLE_DESC_BOB,
                        expectedMessage);

        // missing student id prefix
        assertParseFailure(parser, NAME_DESC_BOB + VALID_ID_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + HANDLE_DESC_BOB,
                        expectedMessage);

        // missing phone prefix
        assertParseFailure(parser, NAME_DESC_BOB + ID_DESC_BOB + VALID_PHONE_BOB + EMAIL_DESC_BOB + HANDLE_DESC_BOB,
                        expectedMessage);

        // missing email prefix
        assertParseFailure(parser, NAME_DESC_BOB + ID_DESC_BOB + PHONE_DESC_BOB + VALID_EMAIL_BOB + HANDLE_DESC_BOB,
                        expectedMessage);

        // missing address prefix
        assertParseFailure(parser, NAME_DESC_BOB + ID_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + VALID_HANDLE_BOB,
                        expectedMessage);

        // all prefixes missing
        assertParseFailure(parser, VALID_NAME_BOB + VALID_ID_BOB + VALID_PHONE_BOB + VALID_EMAIL_BOB + VALID_HANDLE_BOB,
                        expectedMessage);
    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid name
        assertParseFailure(parser, INVALID_NAME_DESC + ID_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + HANDLE_DESC_BOB
                        + TUTORIAL_DESC_2 + TUTORIAL_DESC_1, Name.MESSAGE_CONSTRAINTS);

        // invalid student id
        assertParseFailure(parser, NAME_DESC_BOB + INVALID_ID_DESC + PHONE_DESC_BOB + EMAIL_DESC_BOB + HANDLE_DESC_BOB
                        + TUTORIAL_DESC_2 + TUTORIAL_DESC_1, StudentID.MESSAGE_CONSTRAINTS);

        // invalid phone
        assertParseFailure(parser, NAME_DESC_BOB + ID_DESC_BOB + INVALID_PHONE_DESC + EMAIL_DESC_BOB + HANDLE_DESC_BOB
                        + TUTORIAL_DESC_2 + TUTORIAL_DESC_1, Phone.MESSAGE_CONSTRAINTS);

        // invalid email
        assertParseFailure(parser, NAME_DESC_BOB + ID_DESC_BOB + PHONE_DESC_BOB + INVALID_EMAIL_DESC + HANDLE_DESC_BOB
                        + TUTORIAL_DESC_2 + TUTORIAL_DESC_1, Email.MESSAGE_CONSTRAINTS);

        // invalid handle
        assertParseFailure(parser, NAME_DESC_BOB + ID_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + INVALID_HANDLE_DESC
                        + TUTORIAL_DESC_2 + TUTORIAL_DESC_1, TelegramHandle.MESSAGE_CONSTRAINTS);

        // two invalid values, only first invalid value reported
        assertParseFailure(parser,
                        INVALID_NAME_DESC + ID_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + INVALID_HANDLE_DESC,
                        Name.MESSAGE_CONSTRAINTS);

        // non-empty preamble
        assertParseFailure(parser,
                        PREAMBLE_NON_EMPTY + NAME_DESC_BOB + ID_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                                        + HANDLE_DESC_BOB + TUTORIAL_DESC_2 + TUTORIAL_DESC_1,
                        String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
    }
}
