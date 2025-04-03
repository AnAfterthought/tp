package seedu.address.logic.parser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddStudentToTutorialCommand;
import seedu.address.logic.commands.AddTutorialCommand;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.DeleteStudentFromTutorialCommand;
import seedu.address.logic.commands.DeleteTutorialCommand;
import seedu.address.logic.commands.FindTutorialCommand;
import seedu.address.logic.commands.ListTutorialCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new {@link Command} object
 */
public class TutorialParser implements Parser<Command> {

    private final Map<String, Parser<? extends Command>> subcmds;
    private final String usage;

    TutorialParser() {
        subcmds = new HashMap<>();

        // Add/List/Delete/Find tutorials.
        subcmds.put(AddTutorialCommand.COMMAND_WORD, new AddTutorialCommandParser());
        subcmds.put(ListTutorialCommand.COMMAND_WORD, new ListTutorialCommandParser());
        subcmds.put(DeleteTutorialCommand.COMMAND_WORD, new DeleteTutorialCommandParser());
        subcmds.put(FindTutorialCommand.COMMAND_WORD, new FindTutorialCommandParser());

        // Edit tutorials.
        subcmds.put(AddStudentToTutorialCommand.COMMAND_WORD, new AddStudentToTutorialCommandParser());
        subcmds.put(DeleteStudentFromTutorialCommand.COMMAND_WORD, new DeleteStudentFromTutorialCommandParser());

        usage = """
                        Usage: tutorial COMMAND
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

    public String getUsage() {
        return usage;
    }
}
