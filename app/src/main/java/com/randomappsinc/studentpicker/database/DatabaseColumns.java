package com.randomappsinc.studentpicker.database;

// Holds database column names
class DatabaseColumns {

    // Core columns
    static final String ID = "id";
    static final String LIST_ID = "list_id";
    static final String LIST_NAME = "list_name";
    static final String NAME = "name";
    static final String NAME_COUNT = "name_count";

    // Choosing settings columns
    static final String PRESENTATION_MODE = "presentation_mode";
    static final String WITH_REPLACEMENT = "with_replacement";
    static final String AUTOMATIC_TTS = "automatic_tts";
    static final String SHOW_AS_LIST = "show_as_list";
    static final String NUM_NAMES_CHOSEN = "num_names_chosen";
    static final String NAMES_HISTORY = "names_history";
    static final String CHOOSING_MESSAGE = "choosing_message";

    // LEGACY
    static final String PERSON_NAME_LEGACY = "student_name";
}
