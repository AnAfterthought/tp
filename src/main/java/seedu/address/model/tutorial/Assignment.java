package seedu.address.model.tutorial;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Objects;
import java.util.Optional;

import seedu.address.model.student.Student;
import seedu.address.model.submission.Submission;
import seedu.address.model.uniquelist.Identifiable;
import seedu.address.model.uniquelist.UniqueList;

/**
 * Object that represents an {@code Assignment}
 *
 * @param name
 *            name of assignment
 * @param dueDate
 *            due date of assignment
 */
public record Assignment(String name, Optional<LocalDateTime> dueDate, Tutorial tutorial,
                UniqueList<Submission> submissions) implements Identifiable<Assignment> {
    public Assignment(String name) {
        this(name, Optional.empty(), null, new UniqueList<>());
    }

    public Assignment(String name, Optional<LocalDateTime> dueDate) {
        this(name, dueDate, null, new UniqueList<>());
    }

    public Assignment(String name, Tutorial tutorial) {
        this(name, Optional.empty(), tutorial, new UniqueList<>());
    }

    public Assignment(String name, Optional<LocalDateTime> dueDate, Tutorial tutorial) {
        this(name, dueDate, tutorial, new UniqueList<>());
    }

    /**
     * Constructs new Assignment object
     */
    public Assignment {
        name = name.trim();
        if (!isNameValid(name)) {
            var msg = """
                            Assignment name can only consist of alphanumeric characters, \
                            hyphens (-), underscores (_) and spaces
                            Length must be between 1 and 35""".formatted(name);
            throw new IllegalArgumentException(msg);
        }
    }

    private boolean isNameValid(String name) {
        return name.matches("[a-zA-Z0-9-_ ]{1,35}");
    }

    public void addSubmission(Submission submission) {
        submissions.add(submission);
    }

    public Assignment setTutorial(Tutorial t) {
        return new Assignment(name, dueDate, t, new UniqueList<>(submissions));
    }

    @Override
    public boolean hasSameIdentity(Assignment other) {
        return name.equals(other.name) && tutorial.hasSameIdentity(other.tutorial);
    }

    @Override
    public String toString() {
        return dueDate.map(due -> {
            var formattedDate = due.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
            return "%s (Due: %s)".formatted(name, formattedDate);
        }).orElse(name);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Assignment otherAssignment)) {
            return false;
        }

        return this.name.equals(otherAssignment.name) && this.dueDate.equals(otherAssignment.dueDate)
                        && tutorial.equals(otherAssignment.tutorial);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, dueDate, tutorial);
    }

    public void removeStudent(Student student) {
        submissions.removeIf(s -> s.student().hasSameIdentity(student));
    }
}
