package com.imaginea.instrumentation.test;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import com.imaginea.instrumentation.Baksmali;
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
	private final String OUTPUT_DIR = INPUT_DIR + "/out";
	private final String APK_NAME = "com.imdb.mobile-1.apk";//"SimpleRESTClient.apk";
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
		boolean fileCopySuccess = instrumentation.copyWrapperSmali(OUTPUT_DIR);
		Assert.assertTrue("smali file copying failed", fileCopySuccess);
	}
	
	@Test
	public void testSmaling() throws Exception {
		 boolean smaliSuccess = instrumentation.smaliToDex(OUTPUT_DIR);
		 Assert.assertTrue("smaling failed", smaliSuccess);	  
	}
	
	@Test
	public void testRepackageApk() throws Exception {
		boolean repackSuccess = instrumentation.repackageApk(OUTPUT_DIR);
		Assert.assertTrue("repackage failed", repackSuccess);
	}
	
	@Test
	public void testJarSigner() throws Exception {
		boolean signingSuccess = instrumentation.signAndZipAlign(OUTPUT_DIR, SDK_PATH);
		Assert.assertTrue("jar signing failed", signingSuccess);
	}
	
	@Test
	public void testFragmentInstrumentation() throws Exception {
		testUnPackApk();
		testBaksmaling();
		testCopyWrapperCode();
		testSmaling();
		testRepackageApk();
		testJarSigner();
	}

}
