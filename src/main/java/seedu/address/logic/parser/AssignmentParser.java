package seedu.address.logic.parser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddAssignmentCommand;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.DeleteAssignmentCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new {@link Command} object
 */
public class AssignmentParser implements Parser<Command> {

    public static final String COMMAND_WORD = "assignment";
    private final Map<String, Parser<? extends Command>> subcmds;
    private final String usage;

    AssignmentParser() {
        subcmds = new HashMap<>();

        subcmds.put(AddAssignmentCommand.COMMAND_WORD, new AddAssignmentCommandParser());
        subcmds.put(DeleteAssignmentCommand.COMMAND_WORD, new DeleteAssignmentCommandParser());

        usage = """
                        Usage: assignment COMMAND
                        COMMAND: %s""".formatted(String.join(", ", subcmds.keySet()));
    }

    @Override
    public Command parse(String arguments) throws ParseException {
        // Arguments does not have the word "tutorial" in it

        var cmd = arguments.trim().split(" ");
        var rest = Arrays.stream(cmd).skip(1).collect(Collectors.joining(" "));

        if (!subcmds.containsKey(cmd[0])) {
            throw new ParseException(Messages.MESSAGE_INVALID_COMMAND_FORMAT.formatted(usage));
        }

        return subcmds.get(cmd[0]).parse(rest);
    }
}
