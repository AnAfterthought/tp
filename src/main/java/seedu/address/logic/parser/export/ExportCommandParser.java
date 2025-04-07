package seedu.address.logic.parser.export;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.export.ExportCommand;
import seedu.address.logic.commands.export.ExportStudentsCommand;
import seedu.address.logic.commands.export.ExportTutorialsCommand;
import seedu.address.logic.parser.Parser;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new {@link ExportCommand} object
 */
public class ExportCommandParser implements Parser<Command> {

    private final Map<String, Parser<? extends Command>> subcmds;
    private final String usage;

    /**
     * Parses the given {@code String} of arguments in the context of the
     * ExportCommand and returns an ExportCommand object for execution.
     */
    public ExportCommandParser() {
        subcmds = new HashMap<>();

        // All Student Lists || List of Students belonging to a Tutorial
        subcmds.put(ExportStudentsCommand.COMMAND_WORD, new ExportStudentsCommandParser());
        subcmds.put(ExportTutorialsCommand.COMMAND_WORD, null); // temporary

        usage = """
                        Usage: export COMMAND
                        COMMAND: %s""".formatted(String.join(", ", subcmds.keySet()));
    }

    @Override
    public Command parse(String arguments) throws ParseException {

        // All Lists
        if (arguments.isEmpty()) {
            return new ExportCommand();
        }

        var cmd = arguments.trim().split(" ");

        // All Tutorials Lists
        if (cmd[0].equals(ExportTutorialsCommand.COMMAND_WORD)) {
            return new ExportTutorialsCommand();
        }

        var rest = Arrays.stream(cmd).skip(1).collect(Collectors.joining(" "));

        if (!subcmds.containsKey(cmd[0])) {
            throw new ParseException(Messages.MESSAGE_INVALID_COMMAND_FORMAT.formatted(usage));
        }

        return subcmds.get(cmd[0]).parse(rest);

    }
}
