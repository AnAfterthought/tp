package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INCORRECT_NAVIGATION_MODE;
import static seedu.address.logic.Messages.MESSAGE_TUTORIAL_INDEX_NOT_FOUND;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.NavigationMode;
import seedu.address.model.tutorial.Assignment;
import seedu.address.model.uniquelist.exceptions.DuplicateItemException;
import seedu.address.model.uniquelist.exceptions.ItemNotFoundException;

/**
 * Adds an assignment to a tutorial
 */
public class AddAssignmentCommand extends Command {

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_SUCCESS = "New assignment added";
    public static final String MESSAGE_INVALID_NAME = """
                    The only valid characters are: letters (A-Z, a-z), digits (0-9), underscores (_), hyphens (-)""";
    public static final String MESSAGE_DUPLICATE_ASSIGNMENT = "Assignment already exists in tutorial";

    private final Assignment toAdd;
    private final List<Index> tutorialIdxList;

    /**
     * Creates an {@link AddAssignmentCommand} to add the specified {@code Tutorial}
     */
    public AddAssignmentCommand(List<Index> tutorialIdxList, Assignment assignment) {
        requireNonNull(assignment);
        requireNonNull(tutorialIdxList);
        toAdd = assignment;
        this.tutorialIdxList = tutorialIdxList;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.getNavigationMode() != NavigationMode.TUTORIAL) {
            return new CommandResult(MESSAGE_INCORRECT_NAVIGATION_MODE.formatted(NavigationMode.TUTORIAL),
                            NavigationMode.TUTORIAL);
        }

        var msgs = new ArrayList<String>();
        var hasErrors = false;
        for (var idx : new LinkedHashSet<>(tutorialIdxList)) {
            var idxZeroBased = idx.getZeroBased();

            var tutorials = model.getFilteredTutorialList();
            if (idxZeroBased >= tutorials.size()) {
                msgs.add(MESSAGE_TUTORIAL_INDEX_NOT_FOUND.formatted(idx.getOneBased()));
                hasErrors = true;
                continue;
            }

            var tutorial = tutorials.get(idxZeroBased);
            if (tutorial == null) {
                msgs.add(MESSAGE_TUTORIAL_INDEX_NOT_FOUND.formatted(idx.getOneBased()));
                hasErrors = true;
                continue;
            }

            try {
                model.addAssignment(new Assignment(toAdd.name(), toAdd.dueDate(), tutorial));
            } catch (ItemNotFoundException e) {
                msgs.add(e.getMessage());
                hasErrors = true;
                continue;
            } catch (DuplicateItemException e) {
                msgs.add(MESSAGE_DUPLICATE_ASSIGNMENT);
                hasErrors = true;
                continue;
            }

            msgs.add("Successfully deleted assignment '%s' for tutorial '%s'".formatted(toAdd, tutorial));
        }

        assert model.check();

        if (hasErrors) {
            var res = String.join("\n", msgs);
            throw new CommandException(res);
        }

        return new CommandResult(MESSAGE_SUCCESS, NavigationMode.UNCHANGED);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddAssignmentCommand otherAddCommand)) {
            return false;
        }

        return toAdd.equals(otherAddCommand.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("toAdd", toAdd).toString();
    }
}
