package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.NavigationMode;
import seedu.address.model.UserPrefs;
import seedu.address.model.attendance.Attendance;
import seedu.address.testutil.TypicalAddressBook;

public class AttendanceCommandTest {
    private static Model modelStub;
    private static final int START_WEEK = 3;
    private static final int END_WEEK = 13;
    private static final int TEST_WEEK = 5;
    private static final int NUMBER_OF_WEEKS = 11;
    private static final Index TEST_INDEX_ONE = Index.fromOneBased(1);
    private static final Index TEST_INDEX_TWO = Index.fromOneBased(2);

    @BeforeEach
    public void setUp() {
        modelStub = new ModelManager(TypicalAddressBook.getTypicalAddressBook(), new UserPrefs());
        modelStub.setNavigationMode(NavigationMode.ATTENDANCE);
    }

    private void execute_setAttendance_successfulSingle(boolean isMark) throws Exception {
        CommandResult commandResult;
        List<Integer> testAttendances = new ArrayList<>(Collections.nCopies(NUMBER_OF_WEEKS, 0));
        List<Index> indices = List.of(TEST_INDEX_ONE);
        testAttendances.set(1, 1);
        testAttendances.set(2, 1);

        if (isMark) {
            commandResult = new MarkAttendanceCommand(TEST_WEEK, indices).execute(modelStub);
            assertEquals(MarkAttendanceCommand.MESSAGE_SUCCESS, commandResult.getFeedbackToUser());
        } else {
            testAttendances.set(2, 0);
            commandResult = new UnmarkAttendanceCommand(TEST_WEEK, indices).execute(modelStub);
            assertEquals(UnmarkAttendanceCommand.MESSAGE_SUCCESS, commandResult.getFeedbackToUser());
        }
        Attendance attendance = modelStub.getAddressBook().getAttendanceList().get(0);
        assertArrayEquals(testAttendances.toArray(), attendance.attendances().toArray());
    }

    @Test
    public void execute_markAttendanceDuplicateIndices_successfulSingle() throws Exception {
        // EP: Duplicate indices

        CommandResult commandResult;
        List<Integer> expectedAttendances = new ArrayList<>(Collections.nCopies(NUMBER_OF_WEEKS, 0));
        expectedAttendances.set(1, 1);
        expectedAttendances.set(2, 1);

        List<Index> indices = List.of(Index.fromOneBased(1), Index.fromOneBased(1));

        // Week 5 == Index 2
        commandResult = new MarkAttendanceCommand(TEST_WEEK, indices).execute(modelStub);
        assertEquals(MarkAttendanceCommand.MESSAGE_SUCCESS, commandResult.getFeedbackToUser());

        Attendance attendance = modelStub.getAddressBook().getAttendanceList().get(0);
        assertArrayEquals(expectedAttendances.toArray(), attendance.attendances().toArray());
    }

    @Test
    public void execute_markAttendance_successfulSingle() throws Exception {
        execute_setAttendance_successfulSingle(true);
    }

    @Test
    public void execute_unmarkAttendance_successfulSingle() throws Exception {
        execute_setAttendance_successfulSingle(false);
    }

    @Test
    public void execute_unmarkAttendanceDuplicateIndices_successfulSingle() throws Exception {
        // EP: Duplicate indices

        CommandResult commandResult;
        List<Integer> expectedAttendances = new ArrayList<>(Collections.nCopies(NUMBER_OF_WEEKS, 0));
        expectedAttendances.set(1, 0);

        List<Index> indices = List.of(Index.fromOneBased(1), Index.fromOneBased(1));

        // Week 5 == Index 2
        commandResult = new UnmarkAttendanceCommand(4, indices).execute(modelStub);
        assertEquals(UnmarkAttendanceCommand.MESSAGE_SUCCESS, commandResult.getFeedbackToUser());

        Attendance attendance = modelStub.getAddressBook().getAttendanceList().get(0);
        assertArrayEquals(expectedAttendances.toArray(), attendance.attendances().toArray());
    }

    private void execute_setAttendance_successfulMultiple(boolean isMark) throws Exception {
        CommandResult commandResult;
        List<Integer> firstTestAttendances = new ArrayList<>(Collections.nCopies(NUMBER_OF_WEEKS, 0));
        List<Integer> secondTestAttendances = new ArrayList<>(Collections.nCopies(NUMBER_OF_WEEKS, 0));
        List<Index> indices = List.of(TEST_INDEX_ONE, TEST_INDEX_TWO);
        firstTestAttendances.set(1, 1);
        firstTestAttendances.set(2, 1);
        secondTestAttendances.set(2, 1);

        if (isMark) {
            commandResult = new MarkAttendanceCommand(TEST_WEEK, indices).execute(modelStub);
            assertEquals(MarkAttendanceCommand.MESSAGE_SUCCESS, commandResult.getFeedbackToUser());
        } else {
            firstTestAttendances.set(2, 0);
            secondTestAttendances.set(2, 0);
            commandResult = new UnmarkAttendanceCommand(TEST_WEEK, indices).execute(modelStub);
            assertEquals(UnmarkAttendanceCommand.MESSAGE_SUCCESS, commandResult.getFeedbackToUser());
        }

        Attendance attendanceOne = modelStub.getAddressBook().getAttendanceList().get(0);
        Attendance attendanceTwo = modelStub.getAddressBook().getAttendanceList().get(1);

        assertArrayEquals(firstTestAttendances.toArray(), attendanceOne.attendances().toArray());
        assertArrayEquals(secondTestAttendances.toArray(), attendanceTwo.attendances().toArray());
    }

    @Test
    public void execute_markAttendance_successfulMultiple() throws Exception {
        execute_setAttendance_successfulMultiple(true);
    }

    @Test
    public void execute_unmarkAttendance_successfulMultiple() throws Exception {
        execute_setAttendance_successfulMultiple(false);
    }

    private void execute_setAttendance_invalidWeek(int week, boolean isMark) throws Exception {
        List<Index> indices = List.of(TEST_INDEX_ONE);
        if (isMark) {
            MarkAttendanceCommand cmd = new MarkAttendanceCommand(week, indices);

            assertThrows(AssertionError.class, (
            ) -> cmd.execute(modelStub));
        } else {
            UnmarkAttendanceCommand cmd = new UnmarkAttendanceCommand(week, indices);

            assertThrows(AssertionError.class, (
            ) -> cmd.execute(modelStub));
        }
    }

    @Test
    public void execute_markAttendance_invalidWeekLow() throws Exception {
        execute_setAttendance_invalidWeek(START_WEEK - 1, true);
    }

    @Test
    public void execute_unmarkAttendance_invalidWeekLow() throws Exception {
        execute_setAttendance_invalidWeek(START_WEEK - 1, false);
    }

    @Test
    public void execute_markAttendance_invalidWeekHigh() throws Exception {
        execute_setAttendance_invalidWeek(END_WEEK + 1, true);
    }

    @Test
    public void execute_unmarkAttendance_invalidWeekHigh() throws Exception {
        execute_setAttendance_invalidWeek(END_WEEK + 1, false);
    }

    private void execute_setAttendance_successWithInvalidIndex(boolean isMark) throws Exception {
        CommandResult commandResult;
        String errorMsg = "Attendance at index 100 is out of bounds\n";
        List<Integer> testAttendances = new ArrayList<>(Collections.nCopies(NUMBER_OF_WEEKS, 0));
        List<Index> indices = List.of(TEST_INDEX_ONE, Index.fromOneBased(100));
        testAttendances.set(1, 1);
        testAttendances.set(2, 1);

        if (isMark) {
            commandResult = new MarkAttendanceCommand(TEST_WEEK, indices).execute(modelStub);
            assertEquals("Warning: %s".formatted(errorMsg), commandResult.getFeedbackToUser());
        } else {
            testAttendances.set(2, 0);
            commandResult = new UnmarkAttendanceCommand(TEST_WEEK, indices).execute(modelStub);
            assertEquals("Warning: %s".formatted(errorMsg), commandResult.getFeedbackToUser());
        }

        Attendance attendanceOne = modelStub.getAddressBook().getAttendanceList().get(0);

        assertArrayEquals(testAttendances.toArray(), attendanceOne.attendances().toArray());
    }

    @Test
    public void execute_markAttendance_successfWithInvalidIndex() throws Exception {
        execute_setAttendance_successWithInvalidIndex(true);
    }

    @Test
    public void execute_unmarkAttendance_successfWithInvalidIndex() throws Exception {
        execute_setAttendance_successWithInvalidIndex(false);
    }

    private void execute_equalsAttendance_successSameObject(Command command) {
        assertEquals(command, command);
    }

    @Test
    public void execute_markAttendance_successSameObject() {
        execute_equalsAttendance_successSameObject(new MarkAttendanceCommand(TEST_WEEK, List.of(TEST_INDEX_ONE)));
    }

    @Test
    public void execute_unmarkAttendance_successSameObject() {
        execute_equalsAttendance_successSameObject(new UnmarkAttendanceCommand(TEST_WEEK, List.of(TEST_INDEX_ONE)));
    }

    private void execute_equalsAttendance_successSameFields(Command command, Command other) {
        assertEquals(command, other);
    }

    @Test
    public void execute_markAttendance_successSameFields() {
        execute_equalsAttendance_successSameFields(new MarkAttendanceCommand(TEST_WEEK, List.of(TEST_INDEX_ONE)),
                        new MarkAttendanceCommand(TEST_WEEK, List.of(TEST_INDEX_ONE)));
    }

    @Test
    public void execute_unmarkAttendance_successSameFields() {
        execute_equalsAttendance_successSameFields(new UnmarkAttendanceCommand(TEST_WEEK, List.of(TEST_INDEX_ONE)),
                        new UnmarkAttendanceCommand(TEST_WEEK, List.of(TEST_INDEX_ONE)));
    }

    private void execute_equalsAttendance_failureNotSameClass(Command command, Command other) {
        assertNotEquals(command, other);
    }

    @Test
    public void execute_markAttendance_failureNotSameClass() {
        execute_equalsAttendance_failureNotSameClass(new MarkAttendanceCommand(TEST_WEEK, List.of(TEST_INDEX_ONE)),
                        new UnmarkAttendanceCommand(TEST_WEEK, List.of(TEST_INDEX_ONE)));
    }

    @Test
    public void execute_unmarkAttendance_failureNotSameClass() {
        execute_equalsAttendance_failureNotSameClass(new UnmarkAttendanceCommand(TEST_WEEK, List.of(TEST_INDEX_ONE)),
                        new MarkAttendanceCommand(TEST_WEEK, List.of(TEST_INDEX_ONE)));
    }

    private void execute_equalsAttendance_failureNotSameDetails(Command command, Command other) {
        assertNotEquals(command, other);
    }

    @Test
    public void execute_markAttendance_failureNotSameDetails() {
        MarkAttendanceCommand markAttendanceCommand = new MarkAttendanceCommand(TEST_WEEK, List.of(TEST_INDEX_ONE));
        execute_equalsAttendance_failureNotSameDetails(markAttendanceCommand,
                        new MarkAttendanceCommand(TEST_WEEK + 1, List.of(TEST_INDEX_ONE)));
        execute_equalsAttendance_failureNotSameDetails(markAttendanceCommand,
                        new MarkAttendanceCommand(TEST_WEEK, List.of(TEST_INDEX_TWO)));
    }

    @Test
    public void execute_unmarkAttendance_failureNotSameDetails() {
        UnmarkAttendanceCommand unmarkAttendanceCommand = new UnmarkAttendanceCommand(TEST_WEEK,
                        List.of(TEST_INDEX_ONE));
        execute_equalsAttendance_failureNotSameDetails(unmarkAttendanceCommand,
                        new UnmarkAttendanceCommand(TEST_WEEK + 1, List.of(TEST_INDEX_ONE)));
        execute_equalsAttendance_failureNotSameDetails(unmarkAttendanceCommand,
                        new UnmarkAttendanceCommand(TEST_WEEK, List.of(TEST_INDEX_TWO)));
    }

    private void execute_setAttendance_invalidMode(boolean isMark) throws Exception {
        List<Index> indices = List.of(TEST_INDEX_ONE);
        modelStub.setNavigationMode(NavigationMode.STUDENT);
        if (isMark) {
            CommandResult result = new MarkAttendanceCommand(TEST_WEEK, indices).execute(modelStub);
            assertEquals(MarkAttendanceCommand.MESSSAGE_SWITCHED_TO_ATTENDANCE, result.getFeedbackToUser());
        } else {
            CommandResult result = new UnmarkAttendanceCommand(TEST_WEEK, indices).execute(modelStub);
            assertEquals(UnmarkAttendanceCommand.MESSSAGE_SWITCHED_TO_ATTENDANCE, result.getFeedbackToUser());
        }
        modelStub.setNavigationMode(NavigationMode.ATTENDANCE);
    }

    @Test
    public void execute_markAttendance_invalidMode() throws Exception {
        execute_setAttendance_invalidMode(true);
    }

    @Test
    public void execute_unmarkAttendance_invalidMode() throws Exception {
        execute_setAttendance_invalidMode(false);
    }
}
