package com.imaginea.xmobile.web;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.imaginea.profiling.ADBCommand.ProfilingData;
import com.imaginea.xmobile.profiling.ProfilingInfo;
import com.imaginea.xmobile.profiling.ProfilingService;


@Controller
@RequestMapping("/profile")
public class ProfilingController {
	@Autowired
	private ProfilingService profilingService;

	@RequestMapping(method=RequestMethod.POST, value="/androidpath/{androidSdkPath}")
	@ResponseStatus(HttpStatus.OK)
	public void markAndroidSdkPath(@PathVariable("androidSdkPath") String androidSdkPath) {
		profilingService.markAndroidPath(androidSdkPath);
	}

	@RequestMapping(method=RequestMethod.GET, value="/packages", params="!packageName", produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String[] showAllPackages() {
		return profilingService.showAllPackages();
	}

	@RequestMapping(method=RequestMethod.POST, value="/packages", params="packageName")
	@ResponseStatus(HttpStatus.OK)
	public void startProfiling(@RequestParam("packageName")String packageName){
		profilingService.startProfiling(packageName);
	}

	@RequestMapping(method=RequestMethod.GET, value="/stop", produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ProfilingInfo stopProfiling() {
		return profilingService.stopProfiling();
	}

	@RequestMapping(method=RequestMethod.GET, value="/connected")
	public @ResponseBody String connected() {
		return Boolean.toString(profilingService.isDeviceConnected());
	}
}
