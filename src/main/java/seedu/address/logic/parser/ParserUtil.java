package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.student.Details;
import seedu.address.model.student.Email;
import seedu.address.model.student.Name;
import seedu.address.model.student.Phone;
import seedu.address.model.student.StudentID;
import seedu.address.model.student.TelegramHandle;
import seedu.address.model.tutorial.Tutorial;

/**
 * Contains utility methods used for parsing strings in the various *Parser
 * classes.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX = "Index is not a non-zero unsigned integer.";
    public static final String MESSAGE_INVALID_WEEK = "Week is not a non-zero unsigned integer.";

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading
     * and trailing whitespaces will be trimmed.
     *
     * @throws ParseException
     *             if the specified index is invalid (not non-zero unsigned
     *             integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    /**
     * Parses a {@code String name} into a {@code Name}. Leading and trailing
     * whitespaces will be trimmed.
     *
     * @throws ParseException
     *             if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!Name.isValidName(trimmedName)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(trimmedName);
    }

    /**
     * Parses a {@code String phone} into a {@code Phone}. Leading and trailing
     * whitespaces will be trimmed.
     *
     * @throws ParseException
     *             if the given {@code phone} is invalid.
     */
    public static Phone parsePhone(String phone) throws ParseException {
        requireNonNull(phone);
        String trimmedPhone = phone.trim();
        if (!Phone.isValidPhone(trimmedPhone)) {
            throw new ParseException(Phone.MESSAGE_CONSTRAINTS);
        }
        return new Phone(trimmedPhone);
    }

    /**
     * Parses a {@code String email} into an {@code Email}. Leading and trailing
     * whitespaces will be trimmed.
     *
     * @throws ParseException
     *             if the given {@code email} is invalid.
     */
    public static Email parseEmail(String email) throws ParseException {
        requireNonNull(email);
        String trimmedEmail = email.trim();
        if (!Email.isValidEmail(trimmedEmail)) {
            throw new ParseException(Email.MESSAGE_CONSTRAINTS);
        }
        return new Email(trimmedEmail);
    }

    /**
     * Parses a {@code String tag} into a {@code Tag}. Leading and trailing
     * whitespaces will be trimmed.
     *
     * @throws ParseException
     *             if the given {@code tag} is invalid.
     */
    public static Tutorial parseTutorial(String tutorial) throws ParseException {
        requireNonNull(tutorial);
        String trimmedTutorial = tutorial.trim();
        if (!Tutorial.isValidName(trimmedTutorial)) {
            throw new ParseException("Tutorial name is not valid.");
        }
        return new Tutorial(trimmedTutorial);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>}.
     */
    public static Set<Tutorial> parseTutorials(Collection<String> tutorials) throws ParseException {
        requireNonNull(tutorials);
        final Set<Tutorial> tutorialSet = new HashSet<>();
        for (String tutorialName : tutorials) {
            tutorialSet.add(parseTutorial(tutorialName));
        }
        return tutorialSet;
    }

    /**
     * Parses a {@code String student ID} into an {@code StudentID}. Leading and
     * trailing whitespaces will be trimmed.
     *
     * @throws ParseException
     *             if the given {@code address} is invalid.
     */
    public static StudentID parseStudentId(String studentId) throws ParseException {
        requireNonNull(studentId);
        String trimmedStudentId = studentId.trim();
        if (!StudentID.isValidID(trimmedStudentId)) {
            throw new ParseException(StudentID.MESSAGE_CONSTRAINTS);
        }
        return new StudentID(trimmedStudentId);
    }

    /**
     * Parses a {@code String Telegram handle} into an {@code TelegramHandle}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException
     *             if the given {@code address} is invalid.
     */
    public static TelegramHandle parseTelegramHandle(String handle) throws ParseException {
        requireNonNull(handle);
        String trimmedHandle = handle.trim();
        if (!TelegramHandle.isValidHandle(trimmedHandle)) {
            throw new ParseException(TelegramHandle.MESSAGE_CONSTRAINTS);
        }
        return new TelegramHandle(trimmedHandle);
    }

    /**
     * Parses a {@code String details} into an {@code Details}. Leading and trailing
     * whitespaces will be trimmed.
     */
    public static Details parseDetails(String details) {
        requireNonNull(details);
        String trimmedDetails = details.trim();
        return new Details(trimmedDetails);
    }

    /**
     * Parses datetime string into {@link LocalDateTime} while supporting a few
     * formats of date time
     *
     * @param input
     * @return
     */
    public static LocalDateTime parseDateTime(String input) {
        final List<DateTimeFormatter> formatters = List.of(DateTimeFormatter.ISO_DATE_TIME, // 2023-03-15T10:15:30
                        DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm"), DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm"),
                        DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss"),
                        DateTimeFormatter.ofPattern("uuuu-MM-dd HHmmss"));

        return formatters.stream()
                        // The default behavior makes it such that 31 Feb will be rounded down to 28
                        // feb. With STRICT style, 31 feb will be rejected
                        .map(f -> f.withResolverStyle(ResolverStyle.STRICT)).flatMap(formatter -> {
                            try {
                                return Stream.of(LocalDateTime.parse(input, formatter));
                            } catch (DateTimeException e) {
                                return Stream.empty();
                            }
                        }).findAny().orElseThrow((
                        ) -> new IllegalArgumentException("Unknown date format '%s'".formatted(input)));
    }

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it for week.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException
     *             if the specified index is invalid (not non-zero unsigned
     *             integer).
     */
    public static Index parseWeek(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_WEEK);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }
}
