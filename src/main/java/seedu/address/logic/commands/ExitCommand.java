package seedu.address.logic.commands;

import seedu.address.model.Model;
import seedu.address.model.NavigationMode;

/**
 * Terminates the program.
 */
public class ExitCommand extends Command {

    public static final String COMMAND_WORD = "exit";

    public static final String MESSAGE_EXIT_ACKNOWLEDGEMENT = "Exiting Address Book as requested ...";

    @Override
    public CommandResult execute(Model model) {

        assert model.check();
        return new CommandResult(MESSAGE_EXIT_ACKNOWLEDGEMENT, NavigationMode.UNCHANGED, false, true);
    }

}
