package com.luzi82.randomwallpaper;

import java.io.UnsupportedEncodingException;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class Settings extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);

		// jni ver
		Preference jniVerPreference = findPreference("preference_info_about_lib_version");
		byte[] buf = new byte[15];
		LiveWallpaper.getVersion(buf);
		String libVer = "unknown";
		try {
			libVer = new String(buf, "ASCII");
		} catch (UnsupportedEncodingException e) {
		}
		jniVerPreference.setSummary(libVer);

		// email intent
		Preference emailPreference = findPreference("preference_info_about_email");
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setType("message/rfc882");
		emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { getResources()
				.getString(R.string.preference_info_about_email_address) });
		emailPreference.setIntent(Intent.createChooser(emailIntent,
				emailPreference.getTitle()));

	}

}
