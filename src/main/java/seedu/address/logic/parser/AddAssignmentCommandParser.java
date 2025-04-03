package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TUTORIAL_IDX;

import java.util.Objects;
import java.util.stream.Stream;

import seedu.address.logic.commands.AddAssignmentCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.tutorial.Assignment;

/**
 * Parses input arguments and creates a new {@link AddAssignmentCommand} object
 */
public class AddAssignmentCommandParser implements Parser<AddAssignmentCommand> {

    public static final String MESSAGE_USAGE = "Usage: assignment add ASSIGNMENT_NAME %sTUTORIAL_INDEX... [%sDUE_DATE]"
                    .formatted(PREFIX_TUTORIAL_IDX, PREFIX_DATE);
    /**
     * Parses the given {@code String} of arguments in the context of the
     * AddTutorialCommand and returns an AddTutorialCommand object for execution.
     *
     * @throws ParseException
     *             if the user input does not conform the expected format
     */
    public AddAssignmentCommand parse(String args) throws ParseException {
        Objects.requireNonNull(args);

        if (args.isEmpty()) {
            throw new ParseException(MESSAGE_INVALID_COMMAND_FORMAT.formatted(MESSAGE_USAGE));
        }

        var argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_TUTORIAL_IDX, PREFIX_DATE);

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_DATE);

        var idxList = argMultimap.getAllValues(PREFIX_TUTORIAL_IDX).stream().flatMap(idx -> {
            try {
                return Stream.of(ParserUtil.parseIndex(idx));
            } catch (ParseException e) {
                return Stream.empty();
            }
        }).toList();
        if (idxList.isEmpty()) {
            throw new ParseException(MESSAGE_INVALID_COMMAND_FORMAT.formatted(MESSAGE_USAGE));
        }

        var name = argMultimap.getPreamble();
        var dueDate = argMultimap.getValue(PREFIX_DATE).map(ParserUtil::parseDateTime);

        return new AddAssignmentCommand(idxList, new Assignment(name, dueDate));
    }
}
