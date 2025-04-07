package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.AddTutorialCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.DeleteTutorialCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditStudentDescriptor;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.ListTutorialCommand;
import seedu.address.logic.commands.TutorialCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.student.NameContainsKeywordsPredicate;
import seedu.address.model.student.Student;
import seedu.address.model.tutorial.StudentContainsTutorialKeywordsPredicate;
import seedu.address.testutil.EditStudentDescriptorBuilder;
import seedu.address.testutil.StudentBuilder;
import seedu.address.testutil.StudentUtil;

public class AddressBookParserTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parseCommand_add() throws Exception {
        Student student = new StudentBuilder().build();
        AddCommand command = (AddCommand) parser.parseCommand(StudentUtil.getAddCommand(student));
        assertEquals(new AddCommand(student), command);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " 3") instanceof ClearCommand);
    }

    @Test
    public void parseCommand_delete() throws Exception {
        DeleteCommand command = (DeleteCommand) parser
                        .parseCommand(DeleteCommand.COMMAND_WORD + " " + INDEX_FIRST_STUDENT.getOneBased());
        assertEquals(new DeleteCommand(INDEX_FIRST_STUDENT), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Student student = new StudentBuilder().build();
        EditStudentDescriptor descriptor = new EditStudentDescriptorBuilder(student).build();
        EditCommand command = (EditCommand) parser
                        .parseCommand(EditCommand.COMMAND_WORD + " " + INDEX_FIRST_STUDENT.getOneBased() + " "
                                        + StudentUtil.getEditStudentDescriptorDetails(descriptor));
        assertEquals(new EditCommand(INDEX_FIRST_STUDENT, descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_find() throws Exception {
        List<String> nameKeywords = Arrays.asList("foo", "bar", "baz");
        List<String> tutorialKeywords = Arrays.asList("t/t1", "t/t2");
        List<String> tutorials = Arrays.asList("t1", "t2");
        String input = FindCommand.COMMAND_WORD + " " + nameKeywords.stream().collect(Collectors.joining(" ")) + " "
                        + tutorialKeywords.stream().collect(Collectors.joining(" "));
        FindCommand command = (FindCommand) parser.parseCommand(input);
        NameContainsKeywordsPredicate namePredicate = new NameContainsKeywordsPredicate(nameKeywords);
        StudentContainsTutorialKeywordsPredicate tutorialPredicate = new StudentContainsTutorialKeywordsPredicate(
                        tutorials);
        assertEquals(new FindCommand(namePredicate, tutorialPredicate), command);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD) instanceof ListCommand);
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD + " 3") instanceof ListCommand);
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), (
        ) -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, (
        ) -> parser.parseCommand("unknownCommand"));
    }

    @Test
    public void parseCommand_tutorialListCommand_success() throws ParseException {
        String commandString = "%s %s".formatted(TutorialCommand.COMMAND_WORD, ListTutorialCommand.COMMAND_WORD);
        String commandStringExtraneous = "%s %s 3".formatted(TutorialCommand.COMMAND_WORD,
                        ListTutorialCommand.COMMAND_WORD);

        assertTrue(parser.parseCommand(commandString) instanceof ListTutorialCommand);
        assertTrue(parser.parseCommand(commandStringExtraneous) instanceof ListTutorialCommand);
    }

    @Test
    public void parseCommand_tutorial_noSubCommand() {
        var cmd = "tutorial";

        assertThrows(ParseException.class, (
        ) -> parser.parseCommand(cmd));
    }

    @Test
    public void parseCommand_tutorialAddCommand_success() throws ParseException {
        var tutorialName = "Tutorial-_-1234";
        var cmd = "%s %s %s".formatted(TutorialCommand.COMMAND_WORD, AddTutorialCommand.COMMAND_WORD, tutorialName);

        var actual = parser.parseCommand(cmd);
        assertTrue(actual instanceof AddTutorialCommand);
    }

    @Test
    public void parseCommand_tutorialAddCommand_invalidName() {
        var invalidTutName = "Tutorial-_%-Name";
        var cmd = "%s %s %s".formatted(TutorialCommand.COMMAND_WORD, AddTutorialCommand.COMMAND_WORD, invalidTutName);

        assertThrows(ParseException.class, (
        ) -> parser.parseCommand(cmd));
    }

    @Test
    public void parseCommand_tutorialDeleteCommand_success() throws ParseException {
        var tutorialName = "Tutorial-_-1234";
        var cmd = "%s %s %s".formatted(TutorialCommand.COMMAND_WORD, DeleteTutorialCommand.COMMAND_WORD, tutorialName);

        var actual = parser.parseCommand(cmd);
        assertTrue(actual instanceof DeleteTutorialCommand);
    }

    @Test
    public void parseCommand_tutorialDeleteCommand_invalidName() {
        var invalidTutName = "Tutorial-_%-Name";
        var cmd = "%s %s %s".formatted(TutorialCommand.COMMAND_WORD, DeleteTutorialCommand.COMMAND_WORD,
                        invalidTutName);

        assertThrows(ParseException.class, (
        ) -> parser.parseCommand(cmd));
    }

    @Test
    public void parseCommand_tutorialDeleteCommand_noName() {
        var cmd = "%s %s".formatted(TutorialCommand.COMMAND_WORD, DeleteTutorialCommand.COMMAND_WORD);

        assertThrows(ParseException.class, (
        ) -> parser.parseCommand(cmd));
    }
}
