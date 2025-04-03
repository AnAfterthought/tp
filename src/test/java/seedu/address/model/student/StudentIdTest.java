package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class StudentIdTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, (
        ) -> new StudentID(null));
    }

    @Test
    public void constructor_invalidStudentId_throwsIllegalArgumentException() {
        String invalidStudentId = "";
        assertThrows(IllegalArgumentException.class, (
        ) -> new StudentID(invalidStudentId));
    }

    @Test
    public void isValidStudentId() {
        // null student IDs
        assertThrows(NullPointerException.class, (
        ) -> StudentID.isValidID(null));

        // invalid student IDs
        assertFalse(StudentID.isValidID("")); // empty string
        assertFalse(StudentID.isValidID(" ")); // spaces only
        assertFalse(StudentID.isValidID("A")); // "A" prefix only
        assertFalse(StudentID.isValidID("AO248596P")); // less than 7 numbers (note "O" not "0")
        assertFalse(StudentID.isValidID("A02348299I")); // more than 7 numbers
        assertFalse(StudentID.isValidID("A12348299")); // missing suffix
        assertFalse(StudentID.isValidID("A12348299s")); // small-letter suffix
        assertFalse(StudentID.isValidID("A0248I96P")); // alphabets within digits
        assertFalse(StudentID.isValidID("A0248 596P")); // spaces within digits

        // valid student IDs
        assertTrue(StudentID.isValidID("A0389574Y"));
        assertTrue(StudentID.isValidID("A2584730U"));
        assertTrue(StudentID.isValidID("A0217395Q"));
    }

    @Test
    public void equals() {
        StudentID studentId = new StudentID("A0217395Q");

        // same values -> ok
        assertEquals(studentId, new StudentID("A0217395Q"));

        // same object -> ok
        assertEquals(studentId, studentId);

        // null -> fail
        assertNotNull(studentId);

        // different types -> fail
        assertNotEquals(studentId, 5.0f);

        // different values -> fail
        assertNotEquals(studentId, new StudentID("A0389574Y"));
    }
}
