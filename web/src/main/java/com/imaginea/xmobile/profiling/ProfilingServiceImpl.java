package com.imaginea.xmobile.profiling;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.android.ddmlib.AndroidDebugBridge;
import com.imaginea.instrumentation.Utils;
import com.imaginea.profiling.ADBCommand.ProfilingData;
import com.imaginea.profiling.AndroidProfiler;

@Component
public class ProfilingServiceImpl implements ProfilingService {
	private AndroidProfiler androidProfiler;
	
	public ProfilingServiceImpl() {
		androidProfiler = new AndroidProfiler(SystemProperties.getAndroidSdkPath(), "");
	}
	
	@Override
	public String[] showAllPackages() {
		String packages = androidProfiler.showAllPackages();
		String[] packagesArr = packages.split("\\r?\\n\\r?\\n");
		return packagesArr;
	}

	@Override
	public int startProfiling(String packageName) {
		return androidProfiler.startUIPerformanceProfiler(packageName, false);
	}

	@Override
	public ProfilingInfo stopProfiling() {
		androidProfiler.stopProfiling();
		Map<String, ProfilingData> profilingMap = androidProfiler.getProfilingMap();
		
		ProfilingInfo profilingInfo = new ProfilingInfo();
		profilingInfo.setAppLaunchedFirstTime(androidProfiler.isAppLaunchedFirstTime());
		profilingInfo.setActivityGrades(androidProfiler.getActivityGrades());
		profilingInfo.setActivityStackScore(androidProfiler.getActivityStackScore());
		profilingInfo.setApplicationRating(androidProfiler.getApplicationRating());
		profilingInfo.setApplicationScore(androidProfiler.getApplicationScore());
		profilingInfo.setGcScore(androidProfiler.getGCScore());
		profilingInfo.setLaunchTimeScore(androidProfiler.getLaunchTimeScore());
		profilingInfo.setOverDrawScore(androidProfiler.getOverDrawScore());
		profilingInfo.setProfilingMap(profilingMap);
		
		return profilingInfo;
	}
	
	@Override
	public boolean isDeviceConnected() {
		AndroidDebugBridge.initIfNeeded(false);
        AndroidDebugBridge debugBridge = null;
		try {
			debugBridge = AndroidDebugBridge.createBridge(Utils.getADBPath(SystemProperties.getAndroidSdkPath()), true);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
       return debugBridge.getDevices().length > 0;
	}

	@Override
	public void markAndroidPath(String androidSdkPath) {
		SystemProperties.setAndroidSdkPath(androidSdkPath);
		androidProfiler = new AndroidProfiler(SystemProperties.getAndroidSdkPath(), "");
	}
}
