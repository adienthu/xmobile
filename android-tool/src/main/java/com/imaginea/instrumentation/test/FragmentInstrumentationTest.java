package com.imaginea.instrumentation.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import com.imaginea.instrumentation.Baksmali;
import com.imaginea.instrumentation.JarSigner;
import com.imaginea.instrumentation.SignatureInfo;
import com.imaginea.instrumentation.Utils;
import com.imaginea.profiling.Instrumentation;

/**
 * Test the fragment instrumentation process. The following actions are tested:
 * - Unpacking APK
 * - Custom baksmaling that injects calls to wrapper code inside the app code  
 * - Copying the wrapper code
 * - Smaling the smali files to get classes.dex
 * - Repackaging the apk
 * - APK signing and zipalign
 * @author adityad
 *
 */
public class FragmentInstrumentationTest {
	
	private Instrumentation instrumentation;
	private final String INPUT_DIR = "/Users/adityad/InstrumentationPackage";
	private final String OUTPUT_DIR = INPUT_DIR + "/helloworld";
	private final String APK_NAME = "com.example.helloworld-1.apk";//"SimpleRESTClient.apk";
	private final String PACKAGE_NAME = "com.example.helloworld";
	private final String PROFILING_KEYSTORE = "/InstrumentationPackage/profiling.keystore";
	private final String INSTRUMENTATION_APK = "/Profiling.apk";
	private final String SDK_PATH = "/Users/adityad/Developer/adt-bundle-mac-x86_64-20140321/sdk";
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		instrumentation = new Instrumentation();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testUnPackApk() throws Exception {
		boolean unpackSuccess = Whitebox.invokeMethod(instrumentation, "unPackApk", INPUT_DIR, OUTPUT_DIR, APK_NAME);
		Assert.assertTrue("Unpack failed", unpackSuccess);
	}
	
	@Test
	public void testBaksmaling() throws Exception {
		boolean decompileSuccess = Baksmali.decompileAndInstrument(OUTPUT_DIR+"/classes.dex", 15, OUTPUT_DIR+"/smali");
		Assert.assertTrue("Baksmaling failed", decompileSuccess);
	}
	
	@Test
	public void testCopyWrapperCode() throws Exception {
		File srcSmaliDir = new File(Utils.getInstrumentationPath() + "wrapper/com/imaginea/instrumentation");
		File destSmaliDir = new File(OUTPUT_DIR+"/smali");
		destSmaliDir = new File(destSmaliDir, "com");
		destSmaliDir = new File(destSmaliDir, "imaginea");
		destSmaliDir = new File(destSmaliDir, "instrumentation");
		destSmaliDir.mkdirs();
		for (File file : srcSmaliDir.listFiles())
		{
			String src = file.getAbsolutePath();
			String dest = destSmaliDir.getAbsolutePath() + "/" + file.getName();
			Utils.fileCopy(src, dest);
		}
	}
	
	@Test
	public void testSmaling() throws Exception {
		final String SMALI_PATH  = Utils.getInstrumentationPath() + "smali-2.0.3.jar";
		String[] cmd = new String[] {
                "java",
                "-jar",
                SMALI_PATH,
                OUTPUT_DIR+"/smali",
                "-o",
                OUTPUT_DIR+"/classes.dex" };
		 boolean smaliSuccess = Utils.execProcessBuilder(cmd);
		 Assert.assertTrue("smaling failed", smaliSuccess);	  
	}
	
	@Test
	public void testRepackageApk() throws Exception {
		boolean repackSuccess = instrumentation.repackageApk(OUTPUT_DIR);
		Assert.assertTrue("repackage failed", repackSuccess);
	}
	
	@Test
	public void testJarSigner() throws Exception {
		final String keystoreLocation = Utils.getInstrumentationPath() + "profiling.keystore";
		final SignatureInfo paramSignatureInfo = new SignatureInfo(
                keystoreLocation, "pramati123", "Imaginea", "pramati123");
		boolean signingSuccess = false;
        try {
            signingSuccess = JarSigner.signUsingJDKSigner(OUTPUT_DIR
                    + INSTRUMENTATION_APK, paramSignatureInfo, SDK_PATH);
        } catch (final IOException e) {
            e.printStackTrace();
        }
		Assert.assertTrue("jar signing failed", signingSuccess);
	}

}
