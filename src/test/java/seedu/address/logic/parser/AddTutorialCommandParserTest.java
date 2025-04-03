package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddTutorialCommand;
import seedu.address.model.tutorial.Tutorial;

public class AddTutorialCommandParserTest {
    private AddTutorialCommandParser parser = new AddTutorialCommandParser();

    @Test
    public void parse_validArgs_returnsAddTutorialCommand() {
        Tutorial expectedTutorial = new Tutorial("CS2103-F15");
        AddTutorialCommand expectedAddTutorialCommand = new AddTutorialCommand(expectedTutorial);

        assertParseSuccess(parser, "CS2103-F15", expectedAddTutorialCommand);
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        // tutorial name is empty
        assertParseFailure(parser, "", String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddTutorialCommand.MESSAGE_USAGE));
        // tutorial name is invalid
        assertParseFailure(parser, "Tu+or!a/",
                        String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddTutorialCommand.MESSAGE_INVALID_NAME));
    }

    @Test
    public void parse_multipleArgs_throwsParseException() {
        // multiple valid tutorial names is invalid (because of space)
        assertParseFailure(parser, "CS2103-F15 CS2103-F14",
                        String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddTutorialCommand.MESSAGE_MULTIPLE_NAMES));
    }
}
