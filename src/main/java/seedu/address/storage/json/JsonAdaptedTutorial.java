package seedu.address.storage.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.model.tutorial.Tutorial;

/**
 * Jackson-friendly version of {@link Tutorial}.
 */
public class JsonAdaptedTutorial {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Tutorial's %s field is missing!";
    public static final String MESSAGE_INVALID_TUTORIAL_NAME = "Tutorial name is not valid.";

    private final String name;

    JsonAdaptedTutorial(Tutorial tutorial) {
        this(tutorial.name());
    }

    @JsonCreator
    public JsonAdaptedTutorial(@JsonProperty("name") String name) {
        this.name = name;
    }

    /**
     * Converts this Jackson-friendly adapted tutorial object into the model's
     * {@link Tutorial} object.
     */
    public Tutorial toModelType() {
        return new Tutorial(name);
    }

}
