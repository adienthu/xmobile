package com.imaginea.instrumentation;

public class FragmentInstrumentationWrapper {
	
	public static void enableDebugLogging() {
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
			android.app.FragmentManager.enableDebugLogging(true);
		else
			android.support.v4.app.FragmentManager.enableDebugLogging(true);
	}
	
}
