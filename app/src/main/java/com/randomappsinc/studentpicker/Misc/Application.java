package com.randomappsinc.studentpicker.Misc;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

/**
 * Created by alexanderchiou on 7/19/15.
 */
public final class Application extends android.app.Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        Iconify.with(new FontAwesomeModule());
        instance = this;
    }

    public static Application instance;

    public static Application get() {
        return instance;
    }
}
