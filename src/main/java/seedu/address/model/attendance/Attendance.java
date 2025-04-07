package seedu.address.model.attendance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.model.student.Student;
import seedu.address.model.tutorial.Tutorial;
import seedu.address.model.uniquelist.Identifiable;

/**
 * Represents an attendance record
 */
public record Attendance(Tutorial tutorial, Student student,
                List<Integer> attendances) implements Identifiable<Attendance> {
    private static final int NUMBER_OF_WEEKS = 11;
    private static final int ABSENT = 0;
    private static final int PRESENT = 1;
    private static final int DIFFERENCE = 3;

    public Attendance(Tutorial tutorial, Student student) {
        this(tutorial, student, new ArrayList<>(Collections.nCopies(NUMBER_OF_WEEKS, ABSENT)));
    }

    public Attendance {
        attendances = fixAttendances(attendances);
    }

    public Attendance(Attendance other) {
        this(other.tutorial, other.student, other.attendances);
    }

    private static List<Integer> fixAttendances(List<Integer> attendances) {
        return Stream.concat(attendances.stream(), Stream.generate((
        ) -> 0)).limit(NUMBER_OF_WEEKS).collect(Collectors.toCollection(ArrayList::new));
    }

    public Attendance setTutorial(Tutorial t) {
        return new Attendance(t, student, new ArrayList<>(attendances));
    }

    public Attendance setStudent(Student s) {
        return new Attendance(tutorial, s, new ArrayList<>(attendances));
    }

    /**
     * Marks or unmarks attendance for the specific week
     *
     * @param week
     *            Week of attendance
     * @param isPresent
     *            Value is true if mark as present, false if mark as absent
     */
    public void setAttendance(int week, boolean isPresent) {
        if (isPresent) {
            attendances.set(week - DIFFERENCE, PRESENT);
        } else {
            attendances.set(week - DIFFERENCE, ABSENT);
        }
    }

    public void setAttendances(List<Integer> attendances) {
        this.attendances.clear();
        this.attendances.addAll(attendances);
    }

    @Override
    public boolean hasSameIdentity(Attendance other) {
        if (other == null) {
            return false;
        }

        return this.tutorial.hasSameIdentity(other.tutorial) && this.student.hasSameIdentity(other.student);
    }
}
