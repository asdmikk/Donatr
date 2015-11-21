package org.donatr.donatr;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by mikkkarner on 21/11/15.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "fa7Hd02yBBrgtSM1VsyL5T9zyZzvrdMaa4loff75", "w0AJ0TKKjXzlX2fvwEgTgwDA8PYmMRq5edXOPdDH");

    }
}
