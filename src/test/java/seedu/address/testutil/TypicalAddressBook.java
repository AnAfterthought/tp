package seedu.address.testutil;

import static seedu.address.logic.commands.CommandTestUtil.VALID_DETAILS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_HANDLE_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_HANDLE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ID_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ID_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;

import seedu.address.commons.util.JsonUtil;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.student.Student;
import seedu.address.model.submission.Submission;
import seedu.address.model.submission.SubmissionStatus;
import seedu.address.model.tutorial.Assignment;
import seedu.address.model.tutorial.Tutorial;
import seedu.address.model.uniquelist.exceptions.DuplicateItemException;
import seedu.address.model.uniquelist.exceptions.ItemNotFoundException;
import seedu.address.storage.json.JsonSerializableAddressBook;

/**
 * A utility class containing a list of {@code Student} objects to be used in
 * tests.
 */
public class TypicalAddressBook {

    public static final Tutorial T1 = new Tutorial("CS2103-T1");
    public static final Tutorial T2 = new Tutorial("CS2106-T02");
    public static final Tutorial T3 = new Tutorial("CS2106-T37");

    public static final Assignment T1_ASSIGN1 = new Assignment("Week 10 Tasks").setTutorial(T1);
    public static final Assignment T2_ASSIGN1 = new Assignment("Week 10 Tasks").setTutorial(T2);
    public static final Assignment T3_ASSIGN1 = new Assignment("Lab 1",
                    Optional.of(LocalDateTime.of(2025, 03, 29, 17, 20))).setTutorial(T3);

    public static final Student ALICE = new StudentBuilder().withName("Alice Pauline").withStudentId("A0743062E")
                    .withHandle("@alice_pauline").withEmail("alice@example.com").withPhone("94351253")
                    .appendTutorial(T1).build();
    public static final Student BENSON = new StudentBuilder().withName("Benson Meier").withStudentId("A0179034R")
                    .withHandle("@benson_meier").withEmail("johnd@example.com").withPhone("98765432")
                    .appendTutorial(T1, T2).build();
    public static final Student CARL = new StudentBuilder().withName("Carl Kurz").withPhone("95352563")
                    .withStudentId("A0388094Q").withEmail("heinz@example.com").withHandle("@carl_kurz").build();
    public static final Student DANIEL = new StudentBuilder().withName("Daniel Meier").withPhone("87652533")
                    .withStudentId("A0833488W").withEmail("cornelia@example.com").withHandle("@dannn_mayor")
                    .appendTutorial(T3).build();
    public static final Student ELLE = new StudentBuilder().withName("Elle Meyer").withPhone("9482224")
                    .withStudentId("A0172925M").withEmail("werner@example.com").withHandle("@ellem").build();
    public static final Student FIONA = new StudentBuilder().withName("Fiona Kunz").withPhone("9482427")
                    .withStudentId("A0005984F").withEmail("lydia@example.com").withHandle("@kunz_fiona")
                    .appendTutorial(T3).build();
    public static final Student GEORGE = new StudentBuilder().withName("George Best").withPhone("9482442")
                    .withStudentId("A0443446N").withEmail("anna@example.com").withHandle("@the_best_george").build();

    // Manually added
    public static final Student HOON = new StudentBuilder().withName("Hoon Meier").withPhone("8482424")
                    .withStudentId("A0478925C").withEmail("stefan@example.com").withHandle("@bee_hoon").build();
    public static final Student IDA = new StudentBuilder().withName("Ida Mueller").withPhone("8482131")
                    .withStudentId("A0840139C").withEmail("hans@example.com").withHandle("@ida_pro").build();

    // Manually added - Student's details found in {@code CommandTestUtil}
    public static final Student AMY = new StudentBuilder().withName(VALID_NAME_AMY).withPhone(VALID_PHONE_AMY)
                    .withStudentId(VALID_ID_AMY).withEmail(VALID_EMAIL_AMY).withHandle(VALID_HANDLE_AMY)
                    .appendTutorial(T3).build();
    public static final Student BOB = new StudentBuilder().withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
                    .withStudentId(VALID_ID_BOB).withEmail(VALID_EMAIL_BOB).withHandle(VALID_HANDLE_BOB)
                    .withDetails(VALID_DETAILS_BOB).appendTutorial(T3).build();

    public static final String KEYWORD_MATCHING_MEIER = "Meier"; // A keyword that matches MEIER

    private TypicalAddressBook() {
    } // prevents instantiation

    /**
     * Returns an {@code AddressBook} with all the typical information.
     */
    public static AddressBook getTypicalAddressBook() {
        var students = getTypicalStudents();
        var tutorials = getTypicalTutorials();

        AddressBook ab = new AddressBook();
        tutorials.forEach(ab::addTutorial);
        students.forEach(ab::addStudent);

        try {
            ab.setSubmissionStatus(new Submission(T1_ASSIGN1, ALICE, SubmissionStatus.SUBMITTED));
        } catch (ItemNotFoundException | CommandException e) {
            throw new RuntimeException(e);
        }

        try {
            ab.markAttendance(T1, 4, ALICE);
        } catch (ItemNotFoundException e) {
            throw new RuntimeException(e);
        }

        ab.populateSubmissions();

        return ab;
    }

    public static List<Student> getTypicalStudents() {
        return new ArrayList<>(Arrays.asList(ALICE, BENSON, CARL, DANIEL, ELLE, FIONA, GEORGE));
    }

    public static List<Tutorial> getTypicalTutorials() {
        var tutorial1 = new Tutorial(T1);
        try {
            tutorial1.addAssignment(T1_ASSIGN1);
        } catch (DuplicateItemException e) {
            throw new RuntimeException(e);
        }

        var tutorial2 = new Tutorial(T2);
        try {
            tutorial2.addAssignment(T2_ASSIGN1);
        } catch (DuplicateItemException e) {
            throw new RuntimeException(e);
        }

        var tutorial3 = new Tutorial(T3);
        try {
            tutorial3.addAssignment(T3_ASSIGN1);
        } catch (DuplicateItemException e) {
            throw new RuntimeException(e);
        }

        return List.of(tutorial1, tutorial2, tutorial3);
    }

    // Used to generate the test files; modify accordingly
    public static void main(String[] args) throws JsonProcessingException {
        System.out.println(JsonUtil.toJsonString(new JsonSerializableAddressBook(getTypicalAddressBook())));
    }
}
