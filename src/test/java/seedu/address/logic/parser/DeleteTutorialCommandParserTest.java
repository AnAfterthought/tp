package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddTutorialCommand;
import seedu.address.logic.commands.DeleteTutorialCommand;
import seedu.address.model.tutorial.Tutorial;

/**
 * As we are only doing white-box testing, our test cases do not cover path
 * variations outside of the DeleteTutorialCommand code. For example, inputs "1"
 * and "1 abc" take the same path through the DeleteCommand, and therefore we
 * test only one of them. The path variation for those two cases occur inside
 * the ParserUtil, and therefore should be covered by the ParserUtilTest.
 */
public class DeleteTutorialCommandParserTest {

    private DeleteTutorialCommandParser parser = new DeleteTutorialCommandParser();

    @Test
    public void parse_validArgs_returnsDeleteTutorialCommand() {
        Tutorial expectedTutorial = new Tutorial("CS2103-F15");
        DeleteTutorialCommand expectedDeleteTutorialCommand = new DeleteTutorialCommand(expectedTutorial);

        assertParseSuccess(parser, "CS2103-F15", expectedDeleteTutorialCommand);
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        // tutorial name is empty
        assertParseFailure(parser, "",
                        String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteTutorialCommand.MESSAGE_USAGE));
        // tutorial name is invalid
        assertParseFailure(parser, "Tu+or!a/",
                        String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddTutorialCommand.MESSAGE_INVALID_NAME));
    }

    @Test
    public void parse_multipleArgs_throwsParseException() {
        // multiple valid tutorial names is invalid (because of space)
        assertParseFailure(parser, "CS2103-F15 CS2103-F14",
                        String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddTutorialCommand.MESSAGE_INVALID_NAME));
    }
}
