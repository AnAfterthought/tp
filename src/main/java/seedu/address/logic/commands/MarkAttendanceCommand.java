package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.NavigationMode;
import seedu.address.model.attendance.Attendance;
import seedu.address.model.student.Student;
import seedu.address.model.tutorial.Tutorial;
import seedu.address.model.uniquelist.exceptions.ItemNotFoundException;

/**
 * Marks a student as present.
 */
public class MarkAttendanceCommand extends Command {

    public static final String COMMAND_WORD = "mark";

    public static final String MESSAGE_USAGE = "Usage: attendance mark w/WEEK i/INDEX...";

    public static final String MESSAGE_SUCCESS = "Attendance marked!";

    public static final String MESSAGE_TUTORIAL_NOT_FOUND = "Tutorial not found";

    public static final String MESSAGE_INVALID_WEEK = "Weeks are from 3 to 13.\nIf you are making up for tutorials, "
                    + "enter the week that is being accounted for.";

    public static final String MESSAGE_ATTENDANCE_NOT_FOUND = "Attendance at index %d is out of bounds\n";

    public static final String MESSSAGE_SWITCHED_TO_ATTENDANCE = "Unable to mark attendance "
                    + "due to being in wrong view!\nListing all attendances";

    public static final String MESSAGE_INCORRECT_NUMBER_OF_WEEKS = "Week not detected at index %d\n";

    public static final int START_WEEK = 3;
    public static final int END_WEEK = 13;

    private final int week;
    private final List<Index> indices;

    /**
     * Creates a {@link MarkAttendanceCommand} to mark the specified attendance
     * {@code Attendance}
     */
    public MarkAttendanceCommand(int week, List<Index> indices) {
        this.week = week;
        this.indices = indices;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        assert week >= START_WEEK;
        assert week <= END_WEEK;

        List<Attendance> attendances = model.getFilteredAttendanceList();

        var errMsg = new StringBuilder();

        if (model.getNavigationMode() != NavigationMode.ATTENDANCE) {
            model.updateFilteredAttendanceList(Model.PREDICATE_SHOW_ALL_ATTENDANCES);
            return new CommandResult(MESSSAGE_SWITCHED_TO_ATTENDANCE, NavigationMode.ATTENDANCE);
        }

        for (Index index : indices) {
            if (index.getZeroBased() >= attendances.size()) {
                errMsg.append(MESSAGE_ATTENDANCE_NOT_FOUND.formatted(index.getOneBased()));
                continue;
            }
            Attendance attendanceToEdit = attendances.get(index.getZeroBased());
            assert model.hasAttendance(attendanceToEdit);

            if (week > attendanceToEdit.attendances().size()) {
                errMsg.append(MESSAGE_INCORRECT_NUMBER_OF_WEEKS.formatted(index.getOneBased()));
                continue;
            }

            Student studentToEdit = attendanceToEdit.student();
            assert model.hasStudent(studentToEdit);

            Tutorial tutorial = attendanceToEdit.tutorial();
            assert model.hasTutorial(tutorial);

            try {
                model.markAttendance(tutorial, week, studentToEdit);
            } catch (ItemNotFoundException e) {
                // This should not be possible
                throw new IllegalStateException(Messages.MESSAGE_UNKNOWN_ERROR);
            }
        }

        String msg = (errMsg.isEmpty()) ? MESSAGE_SUCCESS : "Warning: %s".formatted(errMsg.toString());

        assert model.check();
        return new CommandResult(msg, NavigationMode.ATTENDANCE);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof MarkAttendanceCommand otherMarkAttendanceCommand)) {
            return false;
        }

        return week == otherMarkAttendanceCommand.week && indices.equals(otherMarkAttendanceCommand.indices);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("week", week).add("indices", indices).toString();
    }
}
