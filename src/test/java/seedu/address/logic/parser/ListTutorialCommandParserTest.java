package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ListTutorialCommand;

public class ListTutorialCommandParserTest {

    private ListTutorialCommandParser parser = new ListTutorialCommandParser();

    @Test
    public void parse_validArgs_returnsListTutorialCommand() {

        ListTutorialCommand expectedListTutorialCommand = new ListTutorialCommand();

        // no other arguments supplied
        assertParseSuccess(parser, "", expectedListTutorialCommand);
        // unnecessary arguments supplied
        assertParseSuccess(parser, "something", expectedListTutorialCommand);

    }

}
