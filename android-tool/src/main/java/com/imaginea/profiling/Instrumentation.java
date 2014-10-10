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

import com.imaginea.instrumentation.Apktool;
import com.imaginea.instrumentation.Aspect;
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
    private final String PROFILING_KEYSTORE = "/InstrumentationPackage/profiling.keystore";

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
        unPackApk(inputDir.getAbsolutePath(), outputDir.getAbsolutePath(),
                apkName);
        // Dex to jar converter
        convertDex2jar(inputDir.getAbsolutePath(), apkName);

        // Apply aspectJ and enable fragment log
        if(!injectCode2jar(inputDir.getAbsolutePath(), apkName, outputDir.getAbsolutePath(), PackageName))
            return false;

        // Gets the WorkingDir
        final String workingDirectory = System.getProperty("user.dir");
        // JAR to Dex converter
        jar2DexConverter(workingDirectory, outputDir);

        /* Repackage the APK */
        repackageApk(outputDir.getAbsolutePath());

        /* Sign the APK file */
        final String keystoreLocation = workingDirectory + PROFILING_KEYSTORE;
        final SignatureInfo paramSignatureInfo = new SignatureInfo(
                keystoreLocation, "pramati123", "Imaginea", "pramati123");
        try {
            JarSigner.signUsingJDKSigner(outputDir.getAbsolutePath()
                    + INSTRUMENTATION_APK, paramSignatureInfo, sdkPath);
        } catch (final IOException e) {
            e.printStackTrace();
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
