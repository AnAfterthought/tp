package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Objects;

import seedu.address.logic.commands.AddTutorialCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.tutorial.Tutorial;

/**
 * Parses input arguments and creates a new {@link AddTutorialCommand} object
 */
public class AddTutorialCommandParser implements Parser<AddTutorialCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the
     * AddTutorialCommand and returns an AddTutorialCommand object for execution.
     *
     * @throws ParseException
     *             if the user input does not conform the expected format
     */
    public AddTutorialCommand parse(String tutorialName) throws ParseException {
        Objects.requireNonNull(tutorialName);

        if (tutorialName.isEmpty()) {
            throw new ParseException(MESSAGE_INVALID_COMMAND_FORMAT.formatted(AddTutorialCommand.MESSAGE_USAGE));
        }

        try {
            return new AddTutorialCommand(new Tutorial(tutorialName));
        } catch (Exception e) {
            throw new ParseException(MESSAGE_INVALID_COMMAND_FORMAT.formatted(e.getMessage()));
        }
    }

}
