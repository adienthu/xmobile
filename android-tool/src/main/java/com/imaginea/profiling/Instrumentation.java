/*******************************************************************************
 * Copyright 2014 Asha
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *       http://www.apache.org/licenses/LICENSE-2.0
 * Owner : Asha - initial API and implementation
 * Project Name : Lib_PerformanceMonitoring
 * FileName :Instrumentation
 ******************************************************************************/
package com.imaginea.profiling;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.imaginea.instrumentation.Apktool;
import com.imaginea.instrumentation.Aspect;
import com.imaginea.instrumentation.Baksmali;
import com.imaginea.instrumentation.JarSigner;
import com.imaginea.instrumentation.SignatureInfo;
import com.imaginea.instrumentation.Utils;
import com.imaginea.instrumentation.Dex2Jar;

/**
 * The Class Instrumentation.
 */
public class Instrumentation {

    /** The instrumentation apk. */
    private final String INSTRUMENTATION_APK = "/Profiling.apk";
    private final String ASPECT_DEX2JAR = "/out.jar";
    private final int HONEYCOMB_VERSION = 13;

    /**
     * Convert dex2jar.
     * 
     * @param outputDir
     *            the output directory
     * @return 
     */
    public boolean convertDex2jar(final String inputDir, final String apkname) {
        // Dex to jar converter
        try {
            return Dex2Jar.convertDex2jar(inputDir + "/" + apkname);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * insert code2jar.
     * 
     * @param outputDir
     *            the output directory
     */
    public boolean injectCode2jar(final String inptuDir, final String apkName, final String outputDir, final String PackageName) {
        boolean ret = false;
        try {
             ret = Aspect.injectCode2jar(inptuDir+ "/" + apkName, outputDir + "/classes.dex", PackageName, apkName);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return ret;
    }
    
    /**
     * Jar2 dex converter.
     * 
     * @param workingDirectory
     *            the working directory
     * @param outputDir
     *            the output directory
     */
    public boolean jar2DexConverter(final String workingDirectory,
            final File outputDir) {
        // convert Dex to jar
    	boolean result = false;
        try {
            result = Dex2Jar.convertjar2dex(workingDirectory + ASPECT_DEX2JAR);
            // Now copy generated dex file to Instrumentation Package
            Utils.move(workingDirectory + "/classes.dex",
                    outputDir.getAbsolutePath());
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Repackage apk.
     * 
     * @param outputDir
     *            the output directory
     */
    public boolean repackageApk(final String outputDir) {
        /* Repackage the APK */
        try {
            return Apktool.repackage(outputDir, outputDir + INSTRUMENTATION_APK);
        } catch (final IOException e) {
            e.printStackTrace();
        }
		return false;
    }
    
    /**
     * Copy wrapper smali files
     */
    public boolean copyWrapperSmali(final String outputDir) {
		File srcSmaliDir = new File(Utils.getInstrumentationPath() + "wrapper");
		File destSmaliDir = new File(outputDir+"/smali");
		boolean fileCopySuccess = true;
		try {
			FileUtils.copyDirectory(srcSmaliDir, destSmaliDir);
		} catch (IOException e) {
			fileCopySuccess = false;
			e.printStackTrace();
		}
		return fileCopySuccess;
	}
    
    /**
     * Smali to dex
     */
    public boolean smaliToDex(final String outputDir) {
    	final String SMALI_PATH  = Utils.getInstrumentationPath() + "smali-2.0.3.jar";
		String[] cmd = new String[] {
                "java",
                "-jar",
                SMALI_PATH,
                outputDir+"/smali",
                "-o",
                outputDir+"/classes.dex" };
		 boolean smaliSuccess = Utils.execProcessBuilder(cmd);
		 return smaliSuccess;
    }
    
    /**
     * Sign and zipalign
     */
    public boolean signAndZipAlign(final String outputDir, final String sdkPath) {
    	final String keystoreLocation = Utils.getInstrumentationPath() + "profiling.keystore";
        final SignatureInfo paramSignatureInfo = new SignatureInfo(
                keystoreLocation, "pramati123", "Imaginea", "pramati123");
        boolean signingSuccess = false;
        try {
        	signingSuccess = JarSigner.signUsingJDKSigner(outputDir
                    + INSTRUMENTATION_APK, paramSignatureInfo, sdkPath);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return signingSuccess;
    }
    
    /**
     * Start instrumentation.
     * 
     * @param inputDir
     *            the input directory
     * @param outputDir
     *            the output directory
     * @param apkName
     *            the apk name
     */
    public boolean startInstrumentation(final File inputDir,
            final File outputDir, final String apkName, final String sdkPath,
            final String PackageName) {
        // Unpack the APK File
        if(!unPackApk(inputDir.getAbsolutePath(), outputDir.getAbsolutePath(),
                apkName)) {
        	return false;
        }
        
        // Determine if support APIs should be used while baksmaling
        String minSdkVersion = Utils.getApkMinSdkVersion(inputDir.getAbsolutePath() + "/" + apkName);
        boolean shudUseSupportAPI = Integer.parseInt(minSdkVersion, 16) < HONEYCOMB_VERSION;
        // Decompile using custom baksmali
        String dexPath = outputDir.getAbsolutePath()+"/classes.dex";
        String outDir = outputDir.getAbsolutePath()+"/smali";
        if(!Baksmali.decompileAndInstrument(dexPath, 15, outDir, shudUseSupportAPI)) {
        	return false;
        }
        
        // Copy wrapper code
        if(!copyWrapperSmali(outputDir.getAbsolutePath())) {
        	return false;
        }
        
        // Recompile to dex
        if(!smaliToDex(outputDir.getAbsolutePath())) {
        	return false;
        }

        // Repackage the APK
        if(!repackageApk(outputDir.getAbsolutePath())) {
        	return false;
        }

        // Sign the APK file and zipalign
        if(!signAndZipAlign(outputDir.getAbsolutePath(), sdkPath)) {
        	return false;
        }
        
        return true;
    }

    /**
     * Unpack apk.
     * 
     * @param inputDir
     *            the input directory
     * @param outputDir
     *            the output directory
     * @param apkName
     *            the apk name
     * @return 
     */
    public boolean unPackApk(final String inputDir, final String outputDir,
            final String apkName) {
        // Unpack the APK File
        try {
            return Apktool.unpackApk(inputDir + "/" + apkName, outputDir);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Unpack apk with sources.
     * 
     * @param inputDir
     *            the input directory
     * @param outputDir
     *            the output directory
     * @param apkName
     *            the apk name
     */
    public void unPackApkWithSource(final String inputDir, final String outputDir,
            final String apkName) {
        // Unpack the APK File
        try {
            Apktool.unpackApkWithSource(inputDir + "/" + apkName, outputDir);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
