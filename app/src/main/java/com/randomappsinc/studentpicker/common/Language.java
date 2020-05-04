package com.randomappsinc.studentpicker.common;

import androidx.annotation.IntDef;

@IntDef({
        Language.DEFAULT,
        Language.ENGLISH,
        Language.SPANISH,
        Language.FRENCH,
        Language.JAPANESE,
        Language.PORTUGUESE,
        Language.CHINESE,
        Language.GERMAN,
        Language.ITALIAN,
        Language.KOREAN,
        Language.HINDI,
        Language.BENGALI,
        Language.RUSSIAN,
        Language.NORWEGIAN
})
public @interface Language {
    int DEFAULT = -1;
    int ENGLISH = 0;
    int SPANISH = 1;
    int FRENCH = 2;
    int JAPANESE = 3;
    int PORTUGUESE = 4;
    int CHINESE = 5;
    int GERMAN = 6;
    int ITALIAN = 7;
    int KOREAN = 8;
    int HINDI = 9;
    int BENGALI = 10;
    int RUSSIAN = 11;
    int NORWEGIAN = 12;
}
