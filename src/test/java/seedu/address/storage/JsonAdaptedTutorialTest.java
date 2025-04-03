package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import seedu.address.model.tutorial.Tutorial;
import seedu.address.storage.json.JsonAdaptedTutorial;

public class JsonAdaptedTutorialTest {

    private static final String VALID_TUTORIAL_NAME = "CS2103-F15";
    private static final Tutorial VALID_TUTORIAL = new Tutorial(VALID_TUTORIAL_NAME);

    @Test
    public void toModelType_validTutorial_returnsTutorial() throws Exception {
        JsonAdaptedTutorial tutorial = new JsonAdaptedTutorial(VALID_TUTORIAL_NAME);
        assertEquals(VALID_TUTORIAL, tutorial.toModelType());
    }

}
