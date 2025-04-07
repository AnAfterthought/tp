package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INCORRECT_NAVIGATION_MODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DETAILS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_HANDLE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID_STUDENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_STUDENTS;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.NavigationMode;
import seedu.address.model.attendance.Attendance;
import seedu.address.model.student.Details;
import seedu.address.model.student.Email;
import seedu.address.model.student.Name;
import seedu.address.model.student.Phone;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentID;
import seedu.address.model.student.TelegramHandle;
import seedu.address.model.submission.Submission;
import seedu.address.model.tutorial.Tutorial;
import seedu.address.model.uniquelist.exceptions.DuplicateItemException;
import seedu.address.model.uniquelist.exceptions.ItemNotFoundException;

/**
 * Edits the details of an existing student in the address book.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the student identified "
                    + "by the index number used in the displayed student list. "
                    + "Existing values will be overwritten by the input values.\n"
                    + "Parameters: INDEX (must be a positive integer) " + "[" + PREFIX_NAME + "NAME] " + "["
                    + PREFIX_ID_STUDENT + "STUDENT_ID] " + "[" + PREFIX_PHONE + "PHONE] " + "[" + PREFIX_EMAIL
                    + "EMAIL] " + "[" + PREFIX_HANDLE + "HANDLE] " + "[" + PREFIX_DETAILS + "DETAILS]\n" + "["
                    + "Example: " + COMMAND_WORD + " 1 " + PREFIX_PHONE + "91234567 " + PREFIX_EMAIL
                    + "johndoe@example.com";

    public static final String MESSAGE_EDIT_STUDENT_SUCCESS = "Edited Student: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_STUDENT = "This student already exists in the address book.";
    public static final String MESSAGE_TUTORIAL_NOT_FOUND = "One of the tutorial groups the student "
                    + "is edited into does not exist: ";

    private final Index index;
    private final EditStudentDescriptor editStudentDescriptor;

    /**
     * @param index
     *            of the student in the filtered student list to edit
     * @param editStudentDescriptor
     *            details to edit the student with
     */
    public EditCommand(Index index, EditStudentDescriptor editStudentDescriptor) {
        requireNonNull(index);
        requireNonNull(editStudentDescriptor);

        this.index = index;
        this.editStudentDescriptor = new EditStudentDescriptor(editStudentDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.getNavigationMode() != NavigationMode.STUDENT) {
            return new CommandResult(MESSAGE_INCORRECT_NAVIGATION_MODE.formatted(NavigationMode.STUDENT),
                            NavigationMode.STUDENT);
        }

        List<Student> lastShownList = model.getFilteredStudentList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
        }

        Student studentToEdit = lastShownList.get(index.getZeroBased());
        Student editedStudent = createEditedStudent(studentToEdit, editStudentDescriptor);

        String resultMessage = MESSAGE_EDIT_STUDENT_SUCCESS;
        assert model.hasStudent(studentToEdit);
        try {
            model.setStudent(studentToEdit, editedStudent);
        } catch (DuplicateItemException e) {
            throw new CommandException(MESSAGE_DUPLICATE_STUDENT);
        } catch (ItemNotFoundException e) {
            // Student is guaranteed to exist
            throw new IllegalStateException(Messages.MESSAGE_UNKNOWN_ERROR);
        }
        model.updateFilteredStudentList(PREDICATE_SHOW_ALL_STUDENTS);

        assert model.check();
        return new CommandResult(String.format(resultMessage, Messages.format(editedStudent)));
    }

    /**
     * Creates and returns a {@code Student} with the details of
     * {@code studentToEdit} edited with {@code editStudentDescriptor}.
     */
    private static Student createEditedStudent(Student studentToEdit, EditStudentDescriptor editStudentDescriptor) {
        assert studentToEdit != null;

        Name updatedName = editStudentDescriptor.getName().orElse(studentToEdit.getName());
        StudentID updatedStudentId = editStudentDescriptor.getStudentId().orElse(studentToEdit.getStudentId());
        Phone updatedPhone = editStudentDescriptor.getPhone().orElse(studentToEdit.getPhone());
        Email updatedEmail = editStudentDescriptor.getEmail().orElse(studentToEdit.getEmail());
        TelegramHandle updatedHandle = editStudentDescriptor.getHandle().orElse(studentToEdit.getHandle());
        Details updatedDetails = editStudentDescriptor.getDetails().orElse(studentToEdit.getDetails());
        Set<Tutorial> updatedTutorials = editStudentDescriptor.getTutorials().orElse(studentToEdit.getTutorials());
        List<Attendance> updatedAttendances = studentToEdit.getAttendances();
        List<Submission> updatedSubmissions = studentToEdit.getSubmissions();

        return new Student(updatedName, updatedStudentId, updatedPhone, updatedEmail, updatedHandle, updatedTutorials,
                        updatedDetails, updatedAttendances, updatedSubmissions);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand)) {
            return false;
        }

        EditCommand otherEditCommand = (EditCommand) other;
        return index.equals(otherEditCommand.index)
                        && editStudentDescriptor.equals(otherEditCommand.editStudentDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("index", index).add("editStudentDescriptor", editStudentDescriptor)
                        .toString();
    }

    /**
     * Stores the details to edit the student with. Each non-empty field value will
     * replace the corresponding field value of the student.
     */
    public static class EditStudentDescriptor {
        private Name name;
        private StudentID studentId;
        private Phone phone;
        private Email email;
        private TelegramHandle handle;
        private Details details;
        private Set<Tutorial> tutorials;

        public EditStudentDescriptor() {
        }

        /**
         * Copy constructor. A defensive copy of {@code tags} is used internally.
         */
        public EditStudentDescriptor(EditStudentDescriptor toCopy) {
            setName(toCopy.name);
            setStudentId(toCopy.studentId);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setHandle(toCopy.handle);
            setDetails(toCopy.details);
            setTutorials(toCopy.tutorials);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, studentId, phone, email, handle, details, tutorials);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setStudentId(StudentID studentId) {
            this.studentId = studentId;
        }

        public Optional<StudentID> getStudentId() {
            return Optional.ofNullable(studentId);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        public void setHandle(TelegramHandle handle) {
            this.handle = handle;
        }

        public Optional<TelegramHandle> getHandle() {
            return Optional.ofNullable(handle);
        }

        public void setDetails(Details details) {
            this.details = details;
        }

        public Optional<Details> getDetails() {
            return Optional.ofNullable(details);
        }

        /**
         * Sets {@code tags} to this object's {@code tags}. A defensive copy of
         * {@code tags} is used internally.
         */
        public void setTutorials(Set<Tutorial> tutorials) {
            this.tutorials = (tutorials != null) ? new HashSet<>(tutorials) : null;
        }

        /**
         * Returns an unmodifiable tag set, which throws
         * {@code UnsupportedOperationException} if modification is attempted. Returns
         * {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tutorial>> getTutorials() {
            return (tutorials != null) ? Optional.of(Collections.unmodifiableSet(tutorials)) : Optional.empty();
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditStudentDescriptor)) {
                return false;
            }

            EditStudentDescriptor otherEditStudentDescriptor = (EditStudentDescriptor) other;
            return Objects.equals(name, otherEditStudentDescriptor.name)
                            && Objects.equals(studentId, otherEditStudentDescriptor.studentId)
                            && Objects.equals(phone, otherEditStudentDescriptor.phone)
                            && Objects.equals(email, otherEditStudentDescriptor.email)
                            && Objects.equals(handle, otherEditStudentDescriptor.handle);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this).add("name", name).add("studentId", studentId).add("phone", phone)
                            .add("email", email).add("handle", handle).add("details", details)
                            .add("tutorials", tutorials).toString();
        }
    }
}
