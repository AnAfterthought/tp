package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ASSIGNMENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TUTORIAL_IDX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TUTORIAL_NAME;

import java.util.Objects;
import java.util.stream.Stream;

import seedu.address.logic.commands.SetSubmissionCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.submission.SubmissionStatus;

/**
 * Parses input arguments and creates a new {@link SetSubmissionCommand} object
 */
public class SetSubmissionCommandParser implements Parser<SetSubmissionCommand> {

    public static final String MESSAGE_USAGE = "Usage: %s %s STATE %sTUTORIAL_NAME %sASSIGNMENT_NAME %sSTUDENT_ID..."
                    .formatted(SubmissionParser.COMMAND_WORD, SetSubmissionCommand.COMMAND_WORD, PREFIX_TUTORIAL_NAME,
                                    PREFIX_ASSIGNMENT, PREFIX_INDEX);

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

        var argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_TUTORIAL_IDX, PREFIX_ASSIGNMENT, PREFIX_INDEX);
        if (!argMultimap.allPresent(PREFIX_TUTORIAL_IDX, PREFIX_ASSIGNMENT, PREFIX_INDEX)) {
            throw new ParseException(MESSAGE_INVALID_COMMAND_FORMAT.formatted(MESSAGE_USAGE));
        }

        var status = SubmissionStatus.parse(argMultimap.getPreamble());

        var tutorialName = argMultimap.getValue(PREFIX_TUTORIAL_IDX).orElseThrow((
        ) -> new IllegalStateException(MESSAGE_UNKNOWN_COMMAND));
        var assignmentName = argMultimap.getValue(PREFIX_ASSIGNMENT).orElseThrow((
        ) -> new IllegalStateException(MESSAGE_UNKNOWN_COMMAND));

        if (argMultimap.getAllValues(PREFIX_INDEX).isEmpty()) {
            throw new ParseException(MESSAGE_INVALID_COMMAND_FORMAT.formatted(MESSAGE_USAGE));
        }

        var studentIdxList = argMultimap.getAllValues(PREFIX_INDEX).stream().flatMap(idx -> {
            try {
                return Stream.of(ParserUtil.parseStudentId(idx));
            } catch (ParseException e) {
                return Stream.empty();
            }
        }).toList();
        if (studentIdxList.isEmpty()) {
            throw new ParseException("Index in %s is not valid".formatted(PREFIX_INDEX));
        }

        return new SetSubmissionCommand(tutorialName, assignmentName, studentIdxList, status);
    }
}
