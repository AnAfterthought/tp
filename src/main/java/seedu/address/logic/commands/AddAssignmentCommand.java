package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_TUTORIAL_NOT_FOUND;

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

        for (var idx : tutorialIdxList) {
            var idxZeroBased = idx.getZeroBased();

            var tutorials = model.getFilteredTutorialList();
            if (idxZeroBased >= tutorials.size()) {
                throw new CommandException(MESSAGE_TUTORIAL_NOT_FOUND.formatted(idx.getOneBased()));
            }

            var tutorial = tutorials.get(idxZeroBased);
            if (tutorial == null) {
                throw new CommandException(MESSAGE_TUTORIAL_NOT_FOUND.formatted(idx.getOneBased()));
            }

            try {
                model.addAssignment(new Assignment(toAdd.name(), toAdd.dueDate(), tutorial));
            } catch (ItemNotFoundException e) {
                throw new CommandException(e.getMessage());
            } catch (DuplicateItemException e) {
                throw new CommandException(MESSAGE_DUPLICATE_ASSIGNMENT);
            }
        }

        assert model.check();
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
