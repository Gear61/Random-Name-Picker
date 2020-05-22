package com.randomappsinc.studentpicker.common;

import androidx.annotation.StringDef;

@StringDef({
        PremiumFeature.IMPORT_FROM_CSV,
        PremiumFeature.SHARE_AS_TXT,
        PremiumFeature.SHARE_AS_CSV,
        PremiumFeature.CUSTOMIZE_CHOOSING_MESSAGE,
        PremiumFeature.SET_SPEECH_LANGUAGE,
        PremiumFeature.ADD_PHOTO_WITH_CAMERA,
        PremiumFeature.ADD_PHOTO_FROM_GALLERY
})
public @interface PremiumFeature {
    String IMPORT_FROM_CSV = "import_from_csv";
    String SHARE_AS_TXT = "share_as_txt";
    String SHARE_AS_CSV = "share_as_csv";
    String CUSTOMIZE_CHOOSING_MESSAGE = "customize_choosing_message";
    String SET_SPEECH_LANGUAGE = "set_speech_language";
    String ADD_PHOTO_WITH_CAMERA = "add_photo_with_camera";
    String ADD_PHOTO_FROM_GALLERY = "add_photo_from_gallery";
}
