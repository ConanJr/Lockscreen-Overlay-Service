package com.example.nusaraya.overlaybutton.Util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by nusaraya on 6/23/2016.
 */

public class PreferenceUtil {
    private static final String NAME = "LOCKSCREEN";
    public static final String ISLOCK = "ISLOCK";

    private static SharedPreferences mPref = null;

    public static void init(Context context) {
        mPref = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public static void setBoolean(String _key, boolean _value)
    {
        if( _key == null )
        {
            return;
        }

        if ( mPref != null )
        {
            SharedPreferences.Editor edit = mPref.edit();
            edit.putBoolean(_key, _value);
            edit.commit();
        }
    }

    public static boolean get(String _key)
    {
        if (mPref == null || !mPref.contains(_key) )
        {
            SharedPreferences.Editor edit = mPref.edit();
            edit.putBoolean(_key, false);
            edit.commit();
        }

        return mPref.getBoolean(_key, false);
    }
}
