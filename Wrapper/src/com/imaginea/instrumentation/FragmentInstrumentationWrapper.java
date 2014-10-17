package com.imaginea.instrumentation;

import android.util.Log;

public class FragmentInstrumentationWrapper {
	
	private final static String TAG = "x-mobile";
	
	public static void enableDebugLogging() {
//		Log.i(TAG, "Enabling FragmentManager debug logging...");
		android.app.FragmentManager.enableDebugLogging(true);
		android.support.v4.app.FragmentManager.enableDebugLogging(true);
	}
}
