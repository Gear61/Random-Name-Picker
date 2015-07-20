package com.randomappsinc.studentpicker.Misc;

/**
 * Created by alexanderchiou on 7/19/15.
 */
public final class Application extends android.app.Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        instance = this;
    }

    public static Application instance;

    public static Application get() {
        return instance;
    }
}
