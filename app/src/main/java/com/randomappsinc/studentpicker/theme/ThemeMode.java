package com.randomappsinc.studentpicker.theme;

import androidx.annotation.IntDef;

@IntDef({
        ThemeMode.LIGHT,
        ThemeMode.DARK,
        ThemeMode.FOLLOW_SYSTEM
})
public @interface ThemeMode {
    int LIGHT = 0;
    int DARK = 1;
    int FOLLOW_SYSTEM = 2;
}
