package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Objects;

import seedu.address.logic.commands.DeleteTutorialCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.tutorial.Tutorial;

/**
 * Parses input arguments and creates a new {@link DeleteTutorialCommand} object
 */
public class DeleteTutorialCommandParser implements Parser<DeleteTutorialCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the
     * DeleteTutorialCommand and returns an DeleteTutorialCommand object for
     * execution.
     *
     * @throws ParseException
     *             if the user input does not conform the expected format
     */
    public DeleteTutorialCommand parse(String tutorialName) throws ParseException {
        Objects.requireNonNull(tutorialName);

        if (tutorialName.isEmpty()) {
            throw new ParseException(MESSAGE_INVALID_COMMAND_FORMAT.formatted(DeleteTutorialCommand.MESSAGE_USAGE));
        }

        try {
            return new DeleteTutorialCommand(new Tutorial(tutorialName));
        } catch (Exception e) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, e.getMessage()));
        }
    }

}
