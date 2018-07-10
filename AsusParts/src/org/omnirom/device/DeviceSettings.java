/*
* Copyright (C) 2016 The OmniROM Project
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
*/
package org.omnirom.device;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.util.Log;

public class DeviceSettings extends PreferenceActivity implements
        Preference.OnPreferenceChangeListener {

    public static final String KEY_VIBSTRENGTH = "vib_strength";
    public static final String KEY_SWIPE2WAKE = "swipe2wake";

    private VibratorStrengthPreference mVibratorStrength;
    private SwitchPreference mSwipe2Wake;

    private SharedPreferences mPrefs;

    private static final String SWIPE2WAKE_FILE = "/sys/devices/soc/78b7000.i2c/i2c-3/3-0038/swipeup_mode";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        addPreferencesFromResource(R.xml.main);

        mVibratorStrength = (VibratorStrengthPreference) findPreference(KEY_VIBSTRENGTH);
        if (mVibratorStrength != null) {
            mVibratorStrength.setEnabled(VibratorStrengthPreference.isSupported());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            return true;
        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void restore(Context context) {
        boolean swipe2WakeData =  PreferenceManager.getDefaultSharedPreferences(context).getBoolean(DeviceSettings.KEY_SWIPE2WAKE, false);
        Utils.writeValue(SWIPE2WAKE_FILE, swipe2WakeData ? "1" : "0");
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        mSwipe2Wake = (SwitchPreference) findPreference(KEY_SWIPE2WAKE);
        mSwipe2Wake.setChecked(mPrefs.getBoolean(DeviceSettings.KEY_SWIPE2WAKE, false));
        mSwipe2Wake.setOnPreferenceChangeListener(this);

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mSwipe2Wake) {
            Boolean enabled = (Boolean) newValue;
            mPrefs.edit().putBoolean(KEY_SWIPE2WAKE, enabled).commit();
            Utils.writeValue(SWIPE2WAKE_FILE, enabled ? "1" : "0");
        }
        return true;
    }
}
