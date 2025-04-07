package seedu.address.model.tutorial;

import java.util.Objects;
import java.util.Optional;

import seedu.address.logic.commands.AddTutorialCommand;
import seedu.address.model.attendance.Attendance;
import seedu.address.model.student.Student;
import seedu.address.model.uniquelist.Identifiable;
import seedu.address.model.uniquelist.UniqueList;
import seedu.address.model.uniquelist.exceptions.DuplicateItemException;

/**
 * Represents a tutorial
 */
public record Tutorial(String name, UniqueList<Assignment> assignments,
                UniqueList<Attendance> attendances) implements Identifiable<Tutorial> {

    public Tutorial(String name) {
        this(name, new UniqueList<>(), new UniqueList<>());
    }

    public Tutorial(String name, UniqueList<Assignment> assignments) {
        this(name, assignments, new UniqueList<>());
    }

    public Tutorial(Tutorial t) {
        this(t.name, new UniqueList<>(t.assignments), new UniqueList<>(t.attendances));
    }

    /**
     * Creates a new {@code Tutorial} object
     *
     * @param name
     *            name of the tutorial
     * @param assignments
     *            list of assignments
     */
    public Tutorial {
        Objects.requireNonNull(name);
        name = name.trim();
        if (!isValidName(name)) {
            throw new IllegalArgumentException(AddTutorialCommand.MESSAGE_INVALID_NAME);
        }
    }

    /**
     * Adds assignments to tutorial
     *
     * @param assignment
     *            Assignment to add
     */
    public Assignment addAssignment(Assignment assignment) throws DuplicateItemException {
        Objects.requireNonNull(assignment);

        var newAssignment = assignment.setTutorial(this);
        if (!assignments.add(newAssignment)) {
            throw new DuplicateItemException("");
        }

        return assignment;
    }

    /**
     * Deletes an assignment from the tutorial.
     *
     * @param assignment
     *            Assignment to delete
     */
    public boolean deleteAssignment(Assignment assignment) {
        Objects.requireNonNull(assignment);
        return assignments.remove(assignment);
    }

    public boolean hasAssignment(Assignment assignment) {
        return assignments.contains(assignment);
    }

    public Optional<Assignment> findAssignment(Assignment assignment) {
        return assignments.find(assignment);
    }

    /**
     * Checks whether the given name is valid
     */
    public static boolean isValidName(String name) {
        Objects.requireNonNull(name);
        final var pattern = "[a-zA-Z0-9_-]{1,15}";

        return name.matches(pattern);
    }

    /**
     * Adds attendance
     */
    public void addAttendance(Attendance attendance) {
        this.attendances.add(attendance);
    }

    /**
     * Removes information related to student when student is removed
     */
    public void removeStudent(Student student) {
        attendances.removeIf(a -> a.student().hasSameIdentity(student));
        assignments.forEach(a -> a.removeStudent(student));
    }

    @Override
    public boolean hasSameIdentity(Tutorial other) {
        if (other == null) {
            return false;
        }

        return this.name.equals(other.name);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Tutorial t)) {
            return false;
        }

        return this.name.equals(t.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
