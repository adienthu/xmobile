package com.imaginea.instrumentation;

import com.google.common.collect.Lists;

//import com.littleeyelabs.utils.log.LOG;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.jf.baksmali.baksmali;
import org.jf.baksmali.baksmaliOptions;
import org.jf.baksmali.umbreyta;
import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.analysis.InlineMethodResolver;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.DexBackedOdexFile;

public class Baksmali
{
  public static final String TAG = "Baksmali";

  public static boolean decompileAndInstrument(String dexPath, int apiLevel, String outDir)
  {
    File localFile = new File(dexPath);
    if (!localFile.exists())
    {
      System.out.println("Can't find the file " + dexPath);
      return false;
    }
    DexBackedDexFile localDexBackedDexFile;
    try
    {
      localDexBackedDexFile = DexFileFactory.loadDexFile(localFile, apiLevel);
    }
    catch (IOException localIOException)
    {
      System.out.println("Couldn't load dex file" + dexPath);
      localIOException.printStackTrace();
      return false;
    }
    baksmaliOptions localbaksmaliOptions = new baksmaliOptions();
    int i = 0;
    if (localDexBackedDexFile.isOdexFile())
    {
      if (i == 0)
      {
        System.out.println("Can't handle odex files yet.");
        return false;
      }
    }
    else
      i = 0;
    if ((i != 0) || (localbaksmaliOptions.registerInfo != 0))
      if ((localDexBackedDexFile instanceof DexBackedOdexFile))
        localbaksmaliOptions.bootClassPathEntries = ((DexBackedOdexFile)localDexBackedDexFile).getDependencies();
      else
        localbaksmaliOptions.bootClassPathEntries = getDefaultBootClassPathForApi(apiLevel);
    if ((localbaksmaliOptions.inlineResolver == null) && ((localDexBackedDexFile instanceof DexBackedOdexFile)))
      localbaksmaliOptions.inlineResolver = InlineMethodResolver.createInlineMethodResolver(((DexBackedOdexFile)localDexBackedDexFile).getOdexVersion());
    if (localbaksmaliOptions.jobs <= 0)
    {
      localbaksmaliOptions.jobs = Runtime.getRuntime().availableProcessors();
      if (localbaksmaliOptions.jobs > 6)
        localbaksmaliOptions.jobs = 6;
    }
    localbaksmaliOptions.outputDirectory = outDir;
    localbaksmaliOptions.transform = true;
//    localbaksmaliOptions.outputDebugInfo = Platform.inDebugMode();
    localbaksmaliOptions.outputTransformInfo = true;
    return baksmali.disassembleDexFile(localDexBackedDexFile, localbaksmaliOptions);
  }

  private static List<String> getDefaultBootClassPathForApi(int paramInt)
  {
    if (paramInt < 9)
      return Lists.newArrayList(new String[] { "/system/framework/core.jar", "/system/framework/ext.jar", "/system/framework/framework.jar", "/system/framework/android.policy.jar", "/system/framework/services.jar" });
    if (paramInt < 12)
      return Lists.newArrayList(new String[] { "/system/framework/core.jar", "/system/framework/bouncycastle.jar", "/system/framework/ext.jar", "/system/framework/framework.jar", "/system/framework/android.policy.jar", "/system/framework/services.jar", "/system/framework/core-junit.jar" });
    if (paramInt < 14)
      return Lists.newArrayList(new String[] { "/system/framework/core.jar", "/system/framework/apache-xml.jar", "/system/framework/bouncycastle.jar", "/system/framework/ext.jar", "/system/framework/framework.jar", "/system/framework/android.policy.jar", "/system/framework/services.jar", "/system/framework/core-junit.jar" });
    if (paramInt < 16)
      return Lists.newArrayList(new String[] { "/system/framework/core.jar", "/system/framework/core-junit.jar", "/system/framework/bouncycastle.jar", "/system/framework/ext.jar", "/system/framework/framework.jar", "/system/framework/android.policy.jar", "/system/framework/services.jar", "/system/framework/apache-xml.jar", "/system/framework/filterfw.jar" });
    return Lists.newArrayList(new String[] { "/system/framework/core.jar", "/system/framework/core-junit.jar", "/system/framework/bouncycastle.jar", "/system/framework/ext.jar", "/system/framework/framework.jar", "/system/framework/telephony-common.jar", "/system/framework/mms-common.jar", "/system/framework/android.policy.jar", "/system/framework/services.jar", "/system/framework/apache-xml.jar" });
  }
}

/* Location:           /Users/adityad/Downloads/com.littleeyelabs.littleeye.instrumentation_2.4.0.0.jar
 * Qualified Name:     com.littleeyelabs.littleeye.instrumentation.Baksmali
 * JD-Core Version:    0.6.2
 */