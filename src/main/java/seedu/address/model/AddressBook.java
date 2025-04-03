package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_ASSIGNMENT_NOT_FOUND;
import static seedu.address.logic.Messages.MESSAGE_STUDENT_NOT_FOUND;
import static seedu.address.logic.Messages.MESSAGE_TUTORIAL_NOT_FOUND;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_ERROR;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.attendance.Attendance;
import seedu.address.model.student.Student;
import seedu.address.model.submission.Submission;
import seedu.address.model.submission.SubmissionStatus;
import seedu.address.model.tutorial.Assignment;
import seedu.address.model.tutorial.Tutorial;
import seedu.address.model.tutorial.TutorialWithStudents;
import seedu.address.model.uniquelist.UniqueList;
import seedu.address.model.uniquelist.exceptions.DuplicateItemException;
import seedu.address.model.uniquelist.exceptions.ItemNotFoundException;

/**
 * Wraps all data at the address-book level
 */
public class AddressBook implements ReadOnlyAddressBook {

    private final UniqueList<Student> students;
    private final UniqueList<Tutorial> tutorials;
    private final UniqueList<Attendance> attendances;
    private final UniqueList<Submission> submissions;

    /*
     * The 'unusual' code block below is a non-static initialization block,
     * sometimes used to avoid duplication between constructors. See
     * https://docs.oracle.com/javase/tutorial/java/javaOO/initial.html
     *
     * Note that non-static init blocks are not recommended to use. There are other
     * ways to avoid duplication among constructors.
     */
    {
        students = new UniqueList<>();
        tutorials = new UniqueList<>();
        attendances = new UniqueList<>();
        submissions = new UniqueList<>();
    }

    public AddressBook() {
    }

    /**
     * Makes a copy of an address book
     */
    public AddressBook(ReadOnlyAddressBook toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the student list with {@code students}.
     * {@code students} must not contain duplicate students.
     */
    public void setStudents(List<Student> students) throws DuplicateItemException {
        this.students.setAll(students);
    }

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);

        try {
            setStudents(newData.getStudentList());
            setTutorials(newData.getTutorialList());
            setAttendances(newData.getAttendanceList());
            setSubmissions(newData.getSubmissionList());
        } catch (DuplicateItemException e) {
            // Since it's coming from an address book, these errors shouldn't be thrown
            throw new IllegalStateException(MESSAGE_UNKNOWN_ERROR);
        }
    }

    //// student-level operations

    /**
     * Returns true if a student with the same identity as {@code student} exists in
     * the address book.
     */
    public boolean hasStudent(Student student) {
        requireNonNull(student);
        return students.containsIdentity(student);
    }

    /**
     * Adds a student to the address book. The student must not already exist in the
     * address book.
     */
    public void addStudent(Student p) {
        // Check that tutorial slots exists
        var student = p.clone();
        student.removeInvalidTutorials(new HashSet<>(tutorials));

        // Map the tutorials into those that exists in the address book
        var existingTutorials = student.getTutorials().stream().map(t -> tutorials.find(t).orElseThrow())
                        .collect(Collectors.toCollection(HashSet::new));
        student.setTutorials(existingTutorials);

        students.add(student);

        // If adding via GUI, this list should be empty so nothing will happen
        // If adding from JSON, this is needed for it to work
        for (var tutorial : student.getTutorials()) {
            assert tutorials.containsIdentity(tutorial);
            assert students.containsIdentity(student);
            try {
                addAttendance(tutorial, student);
            } catch (ItemNotFoundException e) {
                // Both tutorial and student will exist in the address book, this shouldn't
                // happen
                throw new IllegalStateException(MESSAGE_UNKNOWN_ERROR);
            }
        }
    }

    /**
     * Replaces the given student {@code target} in the list with
     * {@code editedstudent}. {@code target} must exist in the address book. The
     * student identity of {@code editedstudent} must not be the same as another
     * existing student in the address book.
     */
    public void setStudent(Student target, Student editedstudent) throws DuplicateItemException, ItemNotFoundException {
        requireNonNull(editedstudent);

        students.set(target, editedstudent);
    }

    /**
     * Removes {@code key} from this {@code AddressBook}. {@code key} must exist in
     * the address book.
     */
    public void removeStudent(Student key) {
        // Remove student from the following:
        // - submissions
        // - attendances

        removeStudentFromAttendances(key);
        removeStudentFromSubmissions(key);
        tutorials.forEach(t -> {
            t.removeStudent(key);
            try {
                tutorials.set(t, t);
            } catch (DuplicateItemException | ItemNotFoundException e) {
                throw new IllegalStateException(e);
            }
        });

        students.remove(key);
    }

    //// Tutorial operations

    /**
     * Adds a tutorial slot
     */
    public void addTutorial(Tutorial tutorial) {
        tutorials.add(new Tutorial(tutorial));
    }

    /**
     * Deletes a tutorial slot
     */
    public void removeTutorial(Tutorial tutorial) {
        removeTutorialFromSubmissions(tutorial);
        removeTutorialFromAttendances(tutorial);
        students.forEach(s -> {
            s.removeTutorial(tutorial);
            try {
                students.set(s, s);
            } catch (DuplicateItemException | ItemNotFoundException e) {
                throw new IllegalStateException(e);
            }
        });

        tutorials.remove(tutorial);
    }

    /**
     * Deletes a tutorial from all students that were allocated to it
     */
    public void removeTutorialFromStudents(Tutorial tutorial) {
        List<Student> currentList = this.getStudentList();

        for (Student student : currentList) {
            if (!student.hasTutorial(tutorial)) {
                continue;
            }
            Student editedstudent = student.clone();
            Set<Tutorial> newTutorials = new HashSet<>(editedstudent.getTutorials());
            newTutorials.remove(tutorial);
            editedstudent.setTutorials(newTutorials);

            // Assertions
            // - editedStudent always be unique
            assert students.contains(student);

            try {
                this.setStudent(student, editedstudent);
            } catch (DuplicateItemException | ItemNotFoundException e) {
                throw new IllegalStateException(MESSAGE_UNKNOWN_ERROR);
            }
        }
    }

    public void removeStudentFromSubmissions(Student stu) {
        submissions.removeIf(s -> s.student().hasSameIdentity(stu));
    }

    public void removeStudentFromAttendances(Student stu) {
        attendances.removeIf(a -> a.student().hasSameIdentity(stu));
    }

    public void removeTutorialFromSubmissions(Tutorial tut) {
        submissions.removeIf(s -> s.assignment().tutorial().hasSameIdentity(tut));
    }

    public void removeTutorialFromAttendances(Tutorial tut) {
        attendances.removeIf(a -> a.tutorial().hasSameIdentity(tut));
    }

    /**
     * Checks whether tutorial exists in the address book
     */
    public boolean hasTutorial(Tutorial tutorial) {
        requireNonNull(tutorial);
        return tutorials.containsIdentity(tutorial);
    }

    /**
     * Replaces the contents of the tutorial list with {@code tutorials}.
     * {@code tutorials} must not contain duplicate tutorials.
     */
    public void setTutorials(List<Tutorial> tutorials) throws DuplicateItemException {
        requireNonNull(tutorials);
        this.tutorials.setAll(tutorials);
    }

    public void setTutorial(Tutorial oldTut, Tutorial newTut) throws DuplicateItemException, ItemNotFoundException {
        requireNonNull(newTut);

        tutorials.set(oldTut, newTut);
    }

    /**
     * Adds a student to tutorial
     */
    public void addStudentToTutorial(Tutorial tutorial, Student student) throws ItemNotFoundException {
        assert tutorials.containsIdentity(tutorial);
        assert students.find(student).orElseThrow() == student;

        var existingTutorial = tutorials.find(tutorial).orElseThrow((
        ) -> new ItemNotFoundException(MESSAGE_TUTORIAL_NOT_FOUND.formatted(tutorial)));

        student.addTutorial(existingTutorial);

        addAttendance(existingTutorial, student);
        var submissionsToAdd = existingTutorial.assignments().stream()
                        .map(a -> new Submission(a, student, SubmissionStatus.NOT_SUBMITTED)).toList();
        submissionsToAdd.stream().forEach(submission -> {
            try {
                setSubmissionStatus(submission);
            } catch (ItemNotFoundException | CommandException e) {
                // Tutorial, assignment, and student should exist
                throw new IllegalStateException(e);
            }
        });

        try {
            students.set(student, student);
        } catch (DuplicateItemException | ItemNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Removes a student from tutorial
     */
    public void removeStudentFromTutorial(Tutorial tutorial, Student student) throws ItemNotFoundException {
        assert tutorials.containsIdentity(tutorial);
        assert students.find(student).orElseThrow() == student;

        var existingTutorial = tutorials.find(tutorial).orElseThrow((
        ) -> new ItemNotFoundException(MESSAGE_TUTORIAL_NOT_FOUND.formatted(tutorial)));

        submissions.removeIf(s -> s.student().hasSameIdentity(student)
                        && s.assignment().tutorial().hasSameIdentity(tutorial));
        attendances.removeIf(s -> s.student().hasSameIdentity(student) && s.tutorial().hasSameIdentity(tutorial));

        student.removeTutorial(existingTutorial);
        existingTutorial.assignments().forEach(a -> a.removeStudent(student));
        existingTutorial.removeStudent(student);

        try {
            students.set(student, student);
            tutorials.set(existingTutorial, existingTutorial);
        } catch (DuplicateItemException | ItemNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Adds an assignment to the addressbook, the tutorial information should be
     * within the assignment object
     */
    public void addAssignment(Assignment assignment) throws ItemNotFoundException, DuplicateItemException {
        requireNonNull(assignment);

        // Resolve tutorial
        assert assignment.tutorial() != null;
        var tut = tutorials.find(assignment.tutorial()).orElseThrow((
        ) -> new ItemNotFoundException(MESSAGE_TUTORIAL_NOT_FOUND.formatted(assignment.tutorial())));

        var addedAssignment = tut.addAssignment(assignment);

        // Handle submissions
        var studentsAffected = students.stream().filter(s -> s.getTutorials().contains(addedAssignment.tutorial()))
                        .toList();
        var newSubmissions = studentsAffected.stream()
                        .map(s -> new Submission(addedAssignment, s, SubmissionStatus.NOT_SUBMITTED)).toList();

        for (var s : newSubmissions) {
            try {
                setSubmissionStatus(s);
            } catch (ItemNotFoundException | CommandException e) {
                throw new IllegalStateException(MESSAGE_UNKNOWN_ERROR);
            }
        }

        tutorials.set(tut, tut);
    }

    /**
     * Removes assignment from the addressbook
     */
    public void removeAssignment(Assignment assignment) throws ItemNotFoundException {
        requireNonNull(assignment);

        // Resolve tutorial
        var tut = tutorials.find(assignment.tutorial()).orElseThrow((
        ) -> new ItemNotFoundException(MESSAGE_TUTORIAL_NOT_FOUND.formatted(assignment.tutorial())));

        if (tut.assignments().find(assignment).isEmpty()) {
            throw new ItemNotFoundException(MESSAGE_ASSIGNMENT_NOT_FOUND.formatted(assignment, tut));
        }

        tut.deleteAssignment(assignment);
        submissions.removeIf(s -> s.assignment().hasSameIdentity(assignment));
        students.stream().forEach(s -> s.removeAssignment(assignment));

        try {
            tutorials.set(tut, tut);
        } catch (DuplicateItemException e) {
            throw new IllegalStateException(MESSAGE_UNKNOWN_ERROR);
        }
    }

    public void setSubmissionStatus(String tutorialName, String assignmentName, String studentName,
                    SubmissionStatus status) throws ItemNotFoundException, CommandException {
        var tut = tutorials.find(new Tutorial(tutorialName)).orElseThrow((
        ) -> new ItemNotFoundException(MESSAGE_TUTORIAL_NOT_FOUND.formatted(tutorialName)));
        var assign = tut.findAssignment(new Assignment(assignmentName, tut)).orElseThrow((
        ) -> new ItemNotFoundException(MESSAGE_ASSIGNMENT_NOT_FOUND.formatted(assignmentName, tut)));

        var student = students.stream().filter(s -> s.getName().toString().equals(studentName)).findAny().orElseThrow((
        ) -> new ItemNotFoundException(MESSAGE_STUDENT_NOT_FOUND.formatted(studentName)));

        setSubmissionStatus(new Submission(assign, student, status));
    }

    /**
     * Sets submission status on a submission, identified by assignment, and student
     *
     * Note: Assignment also requires tutorial information
     */
    public void setSubmissionStatus(Submission submission) throws ItemNotFoundException, CommandException {
        // Resolve assignment
        var tut = tutorials.find(submission.assignment().tutorial()).orElseThrow((
        ) -> new ItemNotFoundException(MESSAGE_TUTORIAL_NOT_FOUND.formatted(submission.assignment().tutorial())));
        var assignment = tut.findAssignment(submission.assignment()).orElseThrow((
        ) -> new ItemNotFoundException(MESSAGE_ASSIGNMENT_NOT_FOUND.formatted(submission.assignment(), tut)));

        // Resolve student
        var studentInList = students.find(submission.student()).orElseThrow((
        ) -> new ItemNotFoundException(MESSAGE_STUDENT_NOT_FOUND.formatted(submission.student())));

        if (!studentInList.getTutorials().contains(tut)) {
            throw new CommandException("'%s' not in '%s'".formatted(studentInList.getName(), tut));
        }

        // Add to submissions list
        var newSubmission = new Submission(submission).setAssignment(assignment).setStudent(studentInList);

        var submissionInList = submissions.find(newSubmission);

        if (submissionInList.isEmpty()) {
            assignment.addSubmission(newSubmission);
            studentInList.addSubmission(newSubmission);
            submissions.add(newSubmission);
        } else {
            var existingSubmission = submissionInList.orElseThrow();
            existingSubmission.setStatus(submission.status());
            try {
                submissions.set(existingSubmission, existingSubmission);
            } catch (DuplicateItemException e) {
                // Since I'm replacing a submission with itself, there should be no duplicates.
                throw new IllegalStateException(MESSAGE_UNKNOWN_ERROR);
            }
        }
    }

    /**
     * Creates attendance record for a student in specified tutorial
     */
    public void addAttendance(Tutorial tutorial, Student student) throws ItemNotFoundException {
        setAttendance(new Attendance(tutorial, student));
    }

    private void setIndividualAttendance(Attendance attendance) throws ItemNotFoundException {
        try {
            attendances.set(attendance, attendance);
        } catch (DuplicateItemException e) {
            throw new IllegalStateException(MESSAGE_UNKNOWN_ERROR);
        }
    }

    /**
     * Sets a student's attendance for a tutorial slot on a given week
     */
    private void setAttendance(Tutorial tutorial, int week, Student student, boolean isPresent)
                    throws ItemNotFoundException {
        requireNonNull(tutorial);
        requireNonNull(student);

        for (Attendance attendance : attendances) {
            if (attendance.tutorial().hasSameIdentity(tutorial) && attendance.student().hasSameIdentity(student)) {
                attendance.setAttendance(week, isPresent);
                setIndividualAttendance(attendance);
                break;
            }
        }
    }

    public void setAttendance(Attendance attendance) throws ItemNotFoundException {
        // Fetch tutorial from tutorial list
        Tutorial tutorialFromList = tutorials.find(attendance.tutorial()).orElseThrow((
        ) -> new ItemNotFoundException(MESSAGE_TUTORIAL_NOT_FOUND.formatted(attendance.tutorial())));

        // Fetch student from student list
        Student studentFromList = students.find(attendance.student()).orElseThrow((
        ) -> new ItemNotFoundException(MESSAGE_STUDENT_NOT_FOUND.formatted(attendance.student())));

        // Check whether existing attendance exists in the addressbook
        var maybeAttendance = attendances.find(attendance);

        if (maybeAttendance.isEmpty()) {
            var newAttendance = new Attendance(attendance).setTutorial(tutorialFromList).setStudent(studentFromList);
            tutorialFromList.addAttendance(newAttendance);
            studentFromList.addAttendance(newAttendance);
            attendances.add(newAttendance);
        } else {
            var existingAttendance = maybeAttendance.orElseThrow();
            existingAttendance.setAttendances(attendance.attendances());
            try {
                attendances.set(existingAttendance, existingAttendance);
            } catch (DuplicateItemException e) {
                // You're setting the same thing to itself,
                // therefore duplicate item exception should not happen
                throw new IllegalStateException(MESSAGE_UNKNOWN_ERROR);
            }
        }
    }

    /**
     * Replaces the contents of the attendance list with {@code attendances}.
     */
    public void setAttendances(List<Attendance> attendances) throws DuplicateItemException {
        requireNonNull(attendances);
        this.attendances.setAll(attendances);
    }

    public void setSubmissions(List<Submission> submissions) throws DuplicateItemException {
        requireNonNull(submissions);
        this.submissions.setAll(submissions);
    }

    /**
     * Marks student as present
     */
    public void markAttendance(Tutorial tutorial, int week, Student student) throws ItemNotFoundException {
        setAttendance(tutorial, week, student, true);
    }

    /**
     * Unmarks a student's attendance
     */
    public void unmarkAttendance(Tutorial tutorial, int week, Student student) throws ItemNotFoundException {
        setAttendance(tutorial, week, student, false);
    }

    /**
     * Checks whether an attendance exists in the address book
     */
    public boolean hasAttendance(Attendance attendance) {
        requireNonNull(attendance);
        return attendances.containsIdentity(attendance);
    }

    /**
     * Creates a submission object for every assignment-student pair (if it doesn't
     * already exists)
     */
    public void populateSubmissions() {
        for (var student : students) {
            var assignments = student.getTutorials().stream().flatMap(t -> t.assignments().stream()).toList();

            var submissions = assignments.stream().map(a -> new Submission(a, student, SubmissionStatus.NOT_SUBMITTED))
                            .collect(Collectors.toCollection(ArrayList::new));

            submissions.removeIf(s -> this.submissions.find(s).isPresent());

            submissions.stream().forEach(s -> {
                try {
                    setSubmissionStatus(s);
                } catch (ItemNotFoundException | CommandException e) {
                    throw new IllegalStateException(e);
                }
            });
        }
    }

    //// util methods

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("students", students).add("tutorials", tutorials).toString();
    }

    @Override
    public ObservableList<Student> getStudentList() {
        return students.asUnmodifiableObservableList();
    }

    @Override
    public ObservableList<Tutorial> getTutorialList() {
        return tutorials.asUnmodifiableObservableList();
    }

    @Override
    public ObservableList<Attendance> getAttendanceList() {
        return attendances.asUnmodifiableObservableList();
    }

    @Override
    public ObservableList<Submission> getSubmissionList() {
        return submissions.asUnmodifiableObservableList();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddressBook otherAddressBook)) {
            return false;
        }

        return areListSame(submissions, otherAddressBook.submissions)
                        && areListSame(students, otherAddressBook.students)
                        && areListSame(tutorials, otherAddressBook.tutorials)
                        && areListSame(attendances, otherAddressBook.attendances);
    }

    @Override
    public int hashCode() {
        return students.hashCode();
    }

    /**
     * Returns an observable list of {@code TutorialWithStudents}, each pairing a
     * tutorial with the students enrolled in that tutorial.
     *
     * @return An observable list of {@code TutorialWithStudents}.
     */
    public ObservableList<TutorialWithStudents> getTutorialWithStudentsList() {
        return tutorials.stream().map(tutorial -> new TutorialWithStudents(tutorial, getStudentsInTutorial(tutorial)))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    /**
     * Retrieves the list of students enrolled in a specific tutorial. This method
     * is a placeholder and should be implemented to return the actual students
     * enrolled in the given tutorial.
     *
     * @param tutorial
     *            The tutorial for which the enrolled students are to be retrieved.
     * @return A list of students enrolled in the given tutorial.
     */
    private List<Student> getStudentsInTutorial(Tutorial tutorial) {
        return this.getStudentList().stream().filter(student -> student.getTutorials().contains(tutorial))
                        .collect(Collectors.toList());
    }

    /**
     * Checks whether the address book is valid.
     *
     * @throws IllegalStateException
     *             if the data in the address book is inconsistent
     */
    public boolean check() {
        // Students
        var studentsFromSubmissions = students.stream().flatMap(s -> s.getTutorials().stream()).toList();
        var studentsFromAttendances = submissions.stream().map(s -> s.assignment().tutorial()).toList();

        if (!(isSubsetOf(studentsFromSubmissions, tutorials) && isSubsetOf(studentsFromAttendances, tutorials))) {
            throw new IllegalStateException("Students are inconsistent");
        }

        // Tutorials
        var tutorialsFromStudents = students.stream().flatMap(s -> s.getTutorials().stream()).toList();
        var tutorialsFromSubmissions = submissions.stream().map(s -> s.assignment().tutorial()).toList();
        var tutorialsFromAttendances = attendances.stream().map(a -> a.tutorial()).toList();

        if (!(isSubsetOf(tutorialsFromStudents, tutorials) && isSubsetOf(tutorialsFromSubmissions, tutorials)
                        && isSubsetOf(tutorialsFromAttendances, tutorials))) {
            throw new IllegalStateException("Tutorials are inconsistent");
        }

        // Assignments
        var assignmentsFromTutorials = tutorials.stream().flatMap(t -> t.assignments().stream().peek(a -> {
            if (!a.tutorial().hasSameIdentity(t)) {
                throw new IllegalStateException("%s is not mapped to %s".formatted(a, t));
            }
        })).toList();
        var assignmentsFromSubmissions = submissions.stream().map(s -> s.assignment()).toList();

        if (!isSubsetOf(assignmentsFromSubmissions, assignmentsFromTutorials)) {
            throw new IllegalStateException("Assignments are inconsistent");
        }

        // Submissions
        var submissionsFromAssignments = assignmentsFromTutorials.stream()
                        .flatMap(a -> a.submissions().stream().peek(s -> {
                            if (!s.assignment().hasSameIdentity(a)) {
                                throw new IllegalStateException("%s is not mapped to %s".formatted(s, a));
                            }
                        })).toList();
        var submissionsFromStudents = students.stream().flatMap(s -> s.getSubmissions().stream()).toList();

        if (!areListSame(submissionsFromAssignments, submissionsFromStudents, submissions)) {
            throw new IllegalStateException("Submissions are inconsistent");
        }

        // Attendances
        var attendancesFromStudents = students.stream().flatMap(s -> s.getAttendances().stream()).toList();
        var attendancesFromTutorials = tutorials.stream().flatMap(t -> t.attendances().stream()).toList();

        if (!areListSame(attendancesFromStudents, attendancesFromTutorials, attendances)) {
            throw new IllegalStateException("Attendances are inconsistent");
        }

        // More checks (the more the merrier)
        for (var s : students) {
            for (var t : s.getTutorials()) {
                if (tutorials.contains(t)) {
                    continue;
                }
                throw new IllegalStateException("%s is not linked to %s".formatted(s, t));
            }
        }

        return true;
    }

    private static <T> boolean isSubsetOf(List<T> child, List<T> parent) {
        return new HashSet<>(parent).containsAll(child);
    }

    private static <T> boolean areListSame(List<T>... lists) {
        var hashSets = Arrays.stream(lists).map(HashSet::new).toList();

        for (int i = 0; i + 1 < hashSets.size(); i++) {
            if (!(hashSets.get(i).equals(hashSets.get(i + 1)) && lists[i].size() == lists[i + 1].size())) {
                return false;
            }
        }
        return true;
    }
}
