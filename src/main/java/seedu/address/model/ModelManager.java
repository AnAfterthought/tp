package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.attendance.Attendance;
import seedu.address.model.student.Student;
import seedu.address.model.submission.Submission;
import seedu.address.model.submission.SubmissionStatus;
import seedu.address.model.tutorial.Assignment;
import seedu.address.model.tutorial.Tutorial;
import seedu.address.model.tutorial.TutorialWithStudents;
import seedu.address.model.uniquelist.exceptions.DuplicateItemException;
import seedu.address.model.uniquelist.exceptions.ItemNotFoundException;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final AddressBook addressBook;
    private final UserPrefs userPrefs;
    private final FilteredList<Student> filteredStudents;
    private final FilteredList<Tutorial> filteredTutorials;
    private ObjectProperty<Student> student;
    private final FilteredList<Attendance> filteredAttendances;
    private final FilteredList<Submission> filteredSubmissions;

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.addressBook = new AddressBook(addressBook);
        this.userPrefs = new UserPrefs(userPrefs);
        student = new SimpleObjectProperty<>();
        filteredStudents = new FilteredList<>(this.addressBook.getStudentList());
        filteredTutorials = new FilteredList<>(this.addressBook.getTutorialList());
        filteredAttendances = new FilteredList<>(this.addressBook.getAttendanceList());
        filteredSubmissions = new FilteredList<>(this.addressBook.getSubmissionList());
    }

    public ModelManager() {
        this(new AddressBook(), new UserPrefs());
    }

    // =========== UserPrefs
    // ==================================================================================

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        this.userPrefs.resetData(userPrefs);
    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    @Override
    public NavigationMode getNavigationMode() {
        return userPrefs.getNavigationMode();
    }

    @Override
    public void setNavigationMode(NavigationMode navigationMode) {
        requireNonNull(navigationMode);
        userPrefs.setNavigationMode(navigationMode);
    }

    @Override
    public Path getAddressBookFilePath() {
        return userPrefs.getAddressBookFilePath();
    }

    @Override
    public void setAddressBookFilePath(Path addressBookFilePath) {
        requireNonNull(addressBookFilePath);
        userPrefs.setAddressBookFilePath(addressBookFilePath);
    }

    // =========== AddressBook
    // ================================================================================

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {
        this.addressBook.resetData(addressBook);
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    @Override
    public boolean hasStudent(Student student) {
        requireNonNull(student);
        return addressBook.hasStudent(student);
    }

    @Override
    public void deleteStudent(Student target) {
        addressBook.removeStudent(target);
    }

    @Override
    public ObjectProperty<Student> getSelectedStudent() {
        return student;
    }

    @Override
    public void setSelectedStudent(Student target) {
        student.set(target);
    }

    @Override
    public void addStudent(Student student) {
        addressBook.addStudent(student);
        updateFilteredStudentList(PREDICATE_SHOW_ALL_STUDENTS);
    }

    @Override
    public void setStudent(Student target, Student editedStudent) throws DuplicateItemException, ItemNotFoundException {
        requireAllNonNull(target, editedStudent);

        addressBook.setStudent(target, editedStudent);
    }

    @Override
    public void addTutorial(Tutorial tutorial) {
        addressBook.addTutorial(tutorial);
        updateFilteredTutorialList(PREDICATE_SHOW_ALL_TUTORIALS);
    }

    @Override
    public void setTutorial(Tutorial oldTut, Tutorial newTut) throws DuplicateItemException, ItemNotFoundException {
        requireAllNonNull(oldTut, newTut);
        addressBook.setTutorial(oldTut, newTut);
    }

    @Override
    public void deleteTutorial(Tutorial t) {
        addressBook.removeTutorial(t);
    }

    @Override
    public void deleteTutorialFromStudents(Tutorial tutorial) {
        addressBook.removeTutorialFromStudents(tutorial);
    }

    @Override
    public boolean hasTutorial(Tutorial tutorial) {
        return addressBook.hasTutorial(tutorial);
    }

    @Override
    public void setSubmissionStatus(String tutorialName, String assignmentName, String studentName,
                    SubmissionStatus status) throws ItemNotFoundException, CommandException {
        addressBook.setSubmissionStatus(tutorialName, assignmentName, studentName, status);
    }

    @Override
    public void addAssignment(Assignment assignment) throws ItemNotFoundException, DuplicateItemException {
        addressBook.addAssignment(assignment);
    }

    @Override
    public void addAttendance(Tutorial tutorial, Student student) throws ItemNotFoundException {
        addressBook.addAttendance(tutorial, student);
    }

    @Override
    public void markAttendance(Tutorial tutorial, int week, Student student) throws ItemNotFoundException {
        addressBook.markAttendance(tutorial, week, student);
    }

    @Override
    public void unmarkAttendance(Tutorial tutorial, int week, Student student) throws ItemNotFoundException {
        addressBook.unmarkAttendance(tutorial, week, student);
    }

    @Override
    public boolean hasAttendance(Attendance attendance) {
        return addressBook.hasAttendance(attendance);
    }

    // =========== Filtered Student List Accessors
    // =============================================================

    @Override
    public ObservableList<Student> getFilteredStudentList() {
        return filteredStudents;
    }

    @Override
    public void updateFilteredStudentList(Predicate<Student> predicate) {
        requireNonNull(predicate);
        filteredStudents.setPredicate(predicate);
    }

    @Override
    public void updateFilteredStudentsByTutorialList(Predicate<Tutorial> predicate) {
        requireNonNull(predicate);
        Predicate<Student> studentPredicate = student -> student.getTutorials().stream().anyMatch(predicate);
        filteredStudents.setPredicate(studentPredicate);
    }

    // =========== Filtered Tutorial List Accessors
    // =============================================================

    @Override
    public ObservableList<Tutorial> getFilteredTutorialList() {
        return filteredTutorials;
    }

    @Override
    public void updateFilteredTutorialList(Predicate<Tutorial> predicate) {
        requireNonNull(predicate);
        filteredTutorials.setPredicate(predicate);
    }

    public ObservableList<TutorialWithStudents> getFilteredTutorialWithStudents() {
        return FXCollections.observableArrayList();
    }

    @Override
    public List<Student> getStudentsInTutorial(Tutorial tutorial) {
        return this.addressBook.getStudentList().stream().filter(student -> student.getTutorials().contains(tutorial))
                        .collect(Collectors.toList());
    }

    @Override
    public void updateFilteredTutorialWithStudentsList(Predicate<Tutorial> predicate) {
        requireNonNull(predicate);
        List<TutorialWithStudents> tutorialWithStudentsList = addressBook.getTutorialList().stream()
                        .filter(predicate::test)
                        .map(tutorial -> new TutorialWithStudents(tutorial, getStudentsInTutorial(tutorial)))
                        .collect(Collectors.toList());

        Predicate<TutorialWithStudents> isInList = tutorialWithStudents -> tutorialWithStudentsList.stream()
                        .anyMatch(item -> item.equals(tutorialWithStudents));

    }

    @Override
    public boolean check() {
        return addressBook.check();
    }

    @Override
    public void addStudentToTutorial(Tutorial tutorial, Student student) throws ItemNotFoundException {
        addressBook.addStudentToTutorial(tutorial, student);
    }

    @Override
    public void removeStudentFromTutorial(Tutorial tutorial, Student student) throws ItemNotFoundException {
        addressBook.removeStudentFromTutorial(tutorial, student);
    }

    @Override
    public void removeAssignment(Assignment assignment) throws ItemNotFoundException {
        addressBook.removeAssignment(assignment);
    }

    // =========== Filtered Attendance List Accessors
    // =============================================================

    @Override
    public ObservableList<Attendance> getFilteredAttendanceList() {
        return filteredAttendances;
    }

    @Override
    public ObservableList<Submission> getFilteredSubmissionList() {
        return filteredSubmissions;
    }

    @Override
    public void updateFilteredAttendanceList(Predicate<Attendance> predicate) {
        requireNonNull(predicate);
        filteredAttendances.setPredicate(predicate);
    }

    @Override
    public void updateFilteredSubmissionList(Predicate<Submission> predicate) {
        requireNonNull(predicate);
        filteredSubmissions.setPredicate(predicate);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager)) {
            return false;
        }

        ModelManager otherModelManager = (ModelManager) other;
        return addressBook.equals(otherModelManager.addressBook) && userPrefs.equals(otherModelManager.userPrefs)
                        && filteredStudents.equals(otherModelManager.filteredStudents)
                        && filteredTutorials.equals(otherModelManager.filteredTutorials);
    }

}
