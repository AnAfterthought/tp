package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddStudentToTutorialCommand;
import seedu.address.logic.commands.AddTutorialCommand;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.DeleteStudentFromTutorialCommand;
import seedu.address.logic.commands.DeleteTutorialCommand;
import seedu.address.logic.commands.FindTutorialCommand;
import seedu.address.logic.commands.ListTutorialCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class TutorialParserTest {
    private TutorialParser parser;

    @BeforeEach
    void setUp() {
        parser = new TutorialParser();
    }

    @Test
    void parse_validAddTutorialCommand_returnsAddTutorialCommand() throws ParseException {
        Command command = parser.parse("add CS2103-F15");
        assertTrue(command instanceof AddTutorialCommand);
    }

    @Test
    void parse_validListTutorialCommand_returnsListTutorialCommand() throws ParseException {
        Command command = parser.parse("list");
        assertTrue(command instanceof ListTutorialCommand);
    }

    @Test
    void parse_validDeleteTutorialCommand_returnsDeleteTutorialCommand() throws ParseException {
        Command command = parser.parse("delete 1");
        assertTrue(command instanceof DeleteTutorialCommand);
    }

    @Test
    void parse_validFindTutorialCommand_returnsFindTutorialCommand() throws ParseException {
        Command command = parser.parse("find CS2103-F15");
        assertTrue(command instanceof FindTutorialCommand);
    }

    @Test
    void parse_validAddStudentToTutorialCommand_returnsAddStudentToTutorialCommand() throws ParseException {
        Command command = parser.parse("add-student CS2103-F15 s/1");
        assertTrue(command instanceof AddStudentToTutorialCommand);
    }

    @Test
    void parse_validDeleteStudentFromTutorialCommand_returnsDeleteStudentFromTutorialCommand() throws ParseException {
        Command command = parser.parse("delete-student CS2103-F15 s/1");
        assertTrue(command instanceof DeleteStudentFromTutorialCommand);
    }

    @Test
    void parse_invalidCommand_throwsParseException() {
        ParseException exception = assertThrows(ParseException.class, (
        ) -> parser.parse("invalid-command something"));
        assertTrue(exception.getMessage().equals(Messages.MESSAGE_INVALID_COMMAND_FORMAT.formatted(parser.getUsage())));
    }
}
