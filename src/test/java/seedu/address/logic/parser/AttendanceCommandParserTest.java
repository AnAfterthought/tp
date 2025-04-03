package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.ListAttendanceCommand;
import seedu.address.logic.commands.MarkAttendanceCommand;
import seedu.address.logic.commands.UnmarkAttendanceCommand;

public class AttendanceCommandParserTest {
    private static final int DUMMY_INDEX_ONE = 1;
    private static final int DUMMY_INDEX_TWO = 2;
    private static final int START_WEEK = 3;
    private static final int END_WEEK = 13;
    private static final String MESSAGE_INVALID_WEEK = "Weeks are from 3 to 13.\nIf you are making up for tutorials, "
                    + "enter the week that is being accounted for.";

    private final AttendanceParser attendanceParser = new AttendanceParser();
    private final MarkAttendanceCommandParser markAttendanceParser = new MarkAttendanceCommandParser();
    private final UnmarkAttendanceCommandParser unmarkAttendanceParser = new UnmarkAttendanceCommandParser();
    private final ListAttendanceCommandParser listAttendanceParser = new ListAttendanceCommandParser();

    @Test
    public void parseCommand_attendanceCommand_failure() {
        String errorMsg = "Invalid command format! \nUsage: attendance COMMAND" + "\nCOMMAND: list, mark, unmark";
        assertParseFailure(attendanceParser, "", errorMsg);
        assertParseFailure(attendanceParser, "invalid", errorMsg);
    }

    @Test
    public void parseCommand_attendanceCommand_success() {
        assertParseSuccess(attendanceParser, "mark w/3 i/1",
                        new MarkAttendanceCommand(START_WEEK, List.of(Index.fromOneBased(DUMMY_INDEX_ONE))));
        assertParseSuccess(attendanceParser, "unmark w/3 i/1",
                        new UnmarkAttendanceCommand(START_WEEK, List.of(Index.fromOneBased(DUMMY_INDEX_ONE))));
    }

    private void parseCommand_attendanceCommand_success(Parser<? extends Command> parser, boolean isSingleIndex) {
        var cmd = "w/%d i/%d".formatted(START_WEEK, DUMMY_INDEX_ONE);
        List<Index> indices;
        Command actual;

        if (!isSingleIndex) {
            cmd += " i/%d".formatted(DUMMY_INDEX_TWO);
            indices = List.of(Index.fromOneBased(DUMMY_INDEX_ONE), Index.fromOneBased(DUMMY_INDEX_TWO));
        } else {
            indices = List.of(Index.fromOneBased(DUMMY_INDEX_ONE));
        }

        if (parser instanceof MarkAttendanceCommandParser) {
            actual = new MarkAttendanceCommand(START_WEEK, indices);
            assertParseSuccess(parser, cmd, actual);
        } else if (parser instanceof UnmarkAttendanceCommandParser) {
            actual = new UnmarkAttendanceCommand(START_WEEK, indices);
            assertParseSuccess(parser, cmd, actual);
        }
    }

    @Test
    public void parseCommand_attendanceCommand_successMultipleSpaces() {
        assertParseSuccess(attendanceParser, "  mark   w/3   i/1",
                        new MarkAttendanceCommand(START_WEEK, List.of(Index.fromOneBased(DUMMY_INDEX_ONE))));
        assertParseSuccess(attendanceParser, "  unmark   w/3   i/1",
                        new UnmarkAttendanceCommand(START_WEEK, List.of(Index.fromOneBased(DUMMY_INDEX_ONE))));
    }

    @Test
    public void parseCommand_markAttendanceCommand_singleSuccess() {
        parseCommand_attendanceCommand_success(markAttendanceParser, true);
    }

    @Test
    public void parseCommand_unmarkAttendanceCommand_singleSuccess() {
        parseCommand_attendanceCommand_success(unmarkAttendanceParser, true);
    }

    @Test
    public void parseCommand_markAttendanceCommand_multipleSuccess() {
        parseCommand_attendanceCommand_success(markAttendanceParser, false);
    }

    @Test
    public void parseCommand_unmarkAttendanceCommand_multipleSuccess() {
        parseCommand_attendanceCommand_success(unmarkAttendanceParser, false);
    }

    private void parseCommand_attendanceCommand_invalidWeek(Parser<? extends Command> parser, int week) {
        var cmd = "w/%d i/%d".formatted(week, DUMMY_INDEX_ONE);
        assertParseFailure(parser, cmd, MESSAGE_INVALID_WEEK);
    }

    @Test
    public void parseCommand_markAttendanceCommand_invalidWeekLow() {
        parseCommand_attendanceCommand_invalidWeek(markAttendanceParser, START_WEEK - 1);
    }

    @Test
    public void parseCommand_unmarkAttendanceCommand_invalidWeekLow() {
        parseCommand_attendanceCommand_invalidWeek(unmarkAttendanceParser, START_WEEK - 1);
    }

    @Test
    public void parseCommand_markAttendanceCommand_invalidWeekHigh() {
        parseCommand_attendanceCommand_invalidWeek(markAttendanceParser, END_WEEK + 1);
    }

    @Test
    public void parseCommand_unmarkAttendanceCommand_invalidWeekHigh() {
        parseCommand_attendanceCommand_invalidWeek(unmarkAttendanceParser, END_WEEK + 1);
    }

    private void parseCommand_attendanceCommand_invalidMultipleWeeks(Parser<? extends Command> parser) {
        var cmd = "w/%d w/%d i/%d".formatted(START_WEEK, END_WEEK, DUMMY_INDEX_ONE);
        assertParseFailure(parser, cmd, Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_WEEK));
    }

    @Test
    public void parseCommand_markAttendanceCommand_invalidMultipleWeeks() {
        parseCommand_attendanceCommand_invalidMultipleWeeks(markAttendanceParser);
    }

    @Test
    public void parseCommand_unmarkAttendanceCommand_invalidMultipleWeeks() {
        parseCommand_attendanceCommand_invalidMultipleWeeks(unmarkAttendanceParser);
    }

    private void parseCommand_attendanceCommand_invalidNoWeek(Parser<? extends Command> parser) {
        var cmd = "i/%d".formatted(DUMMY_INDEX_ONE);
        if (parser instanceof MarkAttendanceCommandParser) {
            assertParseFailure(parser, cmd,
                            String.format(MESSAGE_INVALID_COMMAND_FORMAT, MarkAttendanceCommand.MESSAGE_USAGE));
        } else if (parser instanceof UnmarkAttendanceCommandParser) {
            assertParseFailure(parser, cmd,
                            String.format(MESSAGE_INVALID_COMMAND_FORMAT, UnmarkAttendanceCommand.MESSAGE_USAGE));
        }
    }

    @Test
    public void parseCommand_markAttendanceCommand_invalidNoWeek() {
        parseCommand_attendanceCommand_invalidNoWeek(markAttendanceParser);
    }

    @Test
    public void parseCommand_unmarkAttendanceCommand_invalidNoWeek() {
        parseCommand_attendanceCommand_invalidNoWeek(unmarkAttendanceParser);
    }

    private void parseCommand_attendanceCommand_invalidNoIndex(Parser<? extends Command> parser) {
        var cmd = "w/%d".formatted(START_WEEK);
        if (parser instanceof MarkAttendanceCommandParser) {
            assertParseFailure(parser, cmd,
                            String.format(MESSAGE_INVALID_COMMAND_FORMAT, MarkAttendanceCommand.MESSAGE_USAGE));
        } else if (parser instanceof UnmarkAttendanceCommandParser) {
            assertParseFailure(parser, cmd,
                            String.format(MESSAGE_INVALID_COMMAND_FORMAT, UnmarkAttendanceCommand.MESSAGE_USAGE));
        }
    }

    @Test
    public void parseCommand_markAttendanceCommand_invalidNoIndex() {
        parseCommand_attendanceCommand_invalidNoIndex(markAttendanceParser);
    }

    @Test
    public void parseCommand_unmarkAttendanceCommand_invalidNoIndex() {
        parseCommand_attendanceCommand_invalidNoIndex(unmarkAttendanceParser);
    }

    private void parseCommand_attendanceCommand_invalidIndex(Parser<? extends Command> parser) {
        String invalidCmdOne = "w/%d i/a".formatted(START_WEEK);
        String invalidCmdTwo = "w/%d i/%d".formatted(START_WEEK, -DUMMY_INDEX_ONE);

        assertParseFailure(parser, invalidCmdOne, ParserUtil.MESSAGE_INVALID_INDEX);
        assertParseFailure(parser, invalidCmdTwo, ParserUtil.MESSAGE_INVALID_INDEX);
    }

    @Test
    public void parseCommand_markAttendanceCommand_invalidIndex() {
        parseCommand_attendanceCommand_invalidIndex(markAttendanceParser);
    }

    @Test
    public void parseCommand_unmarkAttendanceCommand_invalidIndex() {
        parseCommand_attendanceCommand_invalidIndex(unmarkAttendanceParser);
    }

    private void parseCommand_attendanceCommand_invalidWeekIndex(Parser<? extends Command> parser) {
        String invalidCmdOne = "w/a i/%d".formatted(DUMMY_INDEX_ONE);
        String invalidCmdTwo = "w/%d i/%d".formatted(-START_WEEK, DUMMY_INDEX_ONE);

        assertParseFailure(parser, invalidCmdOne, ParserUtil.MESSAGE_INVALID_WEEK);
        assertParseFailure(parser, invalidCmdTwo, ParserUtil.MESSAGE_INVALID_WEEK);
    }

    @Test
    public void parseCommand_markAttendanceCommand_invalidWeekIndex() {
        parseCommand_attendanceCommand_invalidWeekIndex(markAttendanceParser);
    }

    @Test
    public void parseCommand_unmarkAttendanceCommand_invalidWeekIndex() {
        parseCommand_attendanceCommand_invalidWeekIndex(unmarkAttendanceParser);
    }

    private void parseCommand_attendanceCommand_invalidWithPreamble(Parser<? extends Command> parser) {
        String cmd = "CS2103-T1 w/%d i/%d".formatted(START_WEEK, DUMMY_INDEX_ONE);
        if (parser instanceof MarkAttendanceCommandParser) {
            assertParseFailure(parser, cmd,
                            String.format(MESSAGE_INVALID_COMMAND_FORMAT, MarkAttendanceCommand.MESSAGE_USAGE));
        } else {
            assertParseFailure(parser, cmd,
                            String.format(MESSAGE_INVALID_COMMAND_FORMAT, UnmarkAttendanceCommand.MESSAGE_USAGE));
        }
    }

    @Test
    public void parseCommand_markAttendanceCommand_invalidWithPreamble() {
        parseCommand_attendanceCommand_invalidWithPreamble(markAttendanceParser);
    }

    @Test
    public void parseCommand_unmarkAttendanceCommand_invalidWithPreamble() {
        parseCommand_attendanceCommand_invalidWithPreamble(unmarkAttendanceParser);
    }

    @Test
    public void parseCommand_listAttendanceCommand_success() {
        ListAttendanceCommand listAttendanceCommand = new ListAttendanceCommand(
                        Optional.of(Index.fromOneBased(DUMMY_INDEX_ONE)));
        assertParseSuccess(listAttendanceParser, "1", listAttendanceCommand);
    }

    @Test
    public void parseCommand_listAttendanceCommand_successNoArg() {
        ListAttendanceCommand listAttendanceCommand = new ListAttendanceCommand(Optional.empty());
        assertParseSuccess(listAttendanceParser, "", listAttendanceCommand);
    }
}
