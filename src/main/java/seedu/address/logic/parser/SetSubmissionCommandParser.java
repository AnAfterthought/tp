package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ASSIGNMENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDENT_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TUTORIAL_IDX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TUTORIAL_NAME;

import java.util.Objects;

import seedu.address.logic.commands.SetSubmissionCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.submission.SubmissionStatus;

/**
 * Parses input arguments and creates a new {@link SetSubmissionCommand} object
 */
public class SetSubmissionCommandParser implements Parser<SetSubmissionCommand> {

    public static final String MESSAGE_USAGE = "Usage: %s %s STATE %sTUTORIAL_NAME %sASSIGNMENT_NAME %sSTUDENT_NAME..."
                    .formatted(SubmissionParser.COMMAND_WORD, SetSubmissionCommand.COMMAND_WORD, PREFIX_TUTORIAL_NAME,
                                    PREFIX_ASSIGNMENT, PREFIX_STUDENT_NAME);

    /**
     * Parses the given {@link String} of arguments in the context of the
     * {@link SetSubmissionCommand} and returns an {@link SetSubmissionCommand}
     * object for execution.
     *
     * @throws ParseException
     *             if the user input does not conform the expected format
     */
    public SetSubmissionCommand parse(String args) throws ParseException {
        Objects.requireNonNull(args);

        if (args.isEmpty()) {
            throw new ParseException(MESSAGE_INVALID_COMMAND_FORMAT.formatted(MESSAGE_USAGE));
        }

        var argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_TUTORIAL_IDX, PREFIX_ASSIGNMENT, PREFIX_STUDENT_NAME);
        if (!argMultimap.allPresent(PREFIX_TUTORIAL_IDX, PREFIX_ASSIGNMENT, PREFIX_STUDENT_NAME)) {
            throw new ParseException(MESSAGE_INVALID_COMMAND_FORMAT.formatted(MESSAGE_USAGE));
        }

        var status = SubmissionStatus.parse(argMultimap.getPreamble());

        var tutorialName = argMultimap.getValue(PREFIX_TUTORIAL_IDX).orElseThrow((
        ) -> new ParseException(MESSAGE_INVALID_COMMAND_FORMAT.formatted(MESSAGE_USAGE)));
        var assignmentName = argMultimap.getValue(PREFIX_ASSIGNMENT).orElseThrow((
        ) -> new ParseException(MESSAGE_INVALID_COMMAND_FORMAT.formatted(MESSAGE_USAGE)));

        var studentList = argMultimap.getAllValues(PREFIX_STUDENT_NAME);
        if (studentList.isEmpty()) {
            throw new ParseException(MESSAGE_INVALID_COMMAND_FORMAT.formatted(MESSAGE_USAGE));
        }

        return new SetSubmissionCommand(tutorialName, assignmentName, studentList, status);
    }
}
