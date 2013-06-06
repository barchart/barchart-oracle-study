package com.sun.deploy.cache;

import com.sun.deploy.config.Config;
import com.sun.deploy.security.JarVerifier;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import com.sun.deploy.util.JarUtil;
import java.net.URL;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

class JarSigningData
{
  private CodeSigner[] signers;
  private Map signerMap;
  private Map codeSourceCache;
  private Certificate[] certificates;
  private Map signerMapCert;
  private Map codeSourceCertCache;
  private final boolean hasOnlySignedEntries;
  private final boolean hasSingleCodeSource;
  private final boolean hasMissingSignedEntries;
  private static int[] emptySignerIndices = new int[0];

  JarSigningData(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    this.hasOnlySignedEntries = paramBoolean1;
    this.hasSingleCodeSource = paramBoolean2;
    this.hasMissingSignedEntries = paramBoolean3;
  }

  static JarSigningData create(JarVerifier paramJarVerifier)
  {
    JarSigningData localJarSigningData = new JarSigningData(paramJarVerifier.hasOnlySignedEntries(), paramJarVerifier.hasSingleCodeSource(), paramJarVerifier.hasMissingSignedEntries());
    List localList1 = paramJarVerifier.getSignerCerts();
    if (!localList1.isEmpty())
    {
      localJarSigningData.certificates = ((Certificate[])localList1.toArray(new Certificate[localList1.size()]));
      localJarSigningData.signerMapCert = paramJarVerifier.getSignerMapCert();
      localJarSigningData.codeSourceCertCache = paramJarVerifier.getCodeSourceCertCache();
    }
    List localList2 = paramJarVerifier.getSignersCS();
    if (!localList2.isEmpty())
    {
      CodeSigner[] arrayOfCodeSigner = new CodeSigner[localList2.size()];
      localJarSigningData.signers = ((CodeSigner[])localList2.toArray(arrayOfCodeSigner));
      localJarSigningData.signerMap = paramJarVerifier.getSignerMap();
      localJarSigningData.codeSourceCache = paramJarVerifier.getCodeSourceCache();
    }
    Trace.println("Create from verifier: " + localJarSigningData, TraceLevel.CACHE);
    return localJarSigningData;
  }

  void updateCertInfo(Certificate[] paramArrayOfCertificate, Map paramMap1, Map paramMap2)
  {
    this.certificates = paramArrayOfCertificate;
    this.signerMapCert = paramMap1;
    this.codeSourceCertCache = paramMap2;
  }

  void updateSignerInfo(CodeSigner[] paramArrayOfCodeSigner, Map paramMap1, Map paramMap2)
  {
    this.signers = paramArrayOfCodeSigner;
    this.signerMap = paramMap1;
    this.codeSourceCache = paramMap2;
  }

  boolean hasStrictSingleSigning()
  {
    return (this.hasOnlySignedEntries) && (this.hasSingleCodeSource) && (!this.hasMissingSignedEntries);
  }

  synchronized Map getCertificateMap()
  {
    return this.signerMapCert;
  }

  synchronized Map getSignerMap()
  {
    if (Config.isJavaVersionAtLeast15())
      return this.signerMap;
    return this.signerMapCert;
  }

  synchronized Map getCodeSourceCache()
  {
    if (Config.isJavaVersionAtLeast15())
      return this.codeSourceCache;
    return this.codeSourceCertCache;
  }

  synchronized boolean matchStrictSingleSigning(CodeSource[] paramArrayOfCodeSource)
  {
    if (!hasStrictSingleSigning())
      return false;
    if ((paramArrayOfCodeSource == null) || (paramArrayOfCodeSource.length != 1) || (paramArrayOfCodeSource[0] == null))
      return false;
    CodeSource localCodeSource1 = paramArrayOfCodeSource[0];
    Map localMap1 = getSignerMap();
    int[] arrayOfInt = (int[])(localMap1 != null ? localMap1.get(null) : null);
    Map localMap2 = getCodeSourceCache();
    CodeSource localCodeSource2 = (CodeSource)(localMap2 != null ? localMap2.get(arrayOfInt) : null);
    return localCodeSource1.equals(localCodeSource2);
  }

  synchronized Map getCodeSourceCertCache()
  {
    return this.codeSourceCertCache;
  }

  public synchronized CodeSigner[] getCodeSigners()
  {
    return this.signers;
  }

  public synchronized Certificate[] getCertificates()
  {
    return this.certificates;
  }

  synchronized CodeSource[] getCodeSources(URL paramURL)
  {
    Map localMap = getCodeSourceCache();
    Object localObject = localMap != null ? localMap.values() : null;
    CodeSource[] arrayOfCodeSource;
    if (localObject != null)
    {
      int i = localObject.size();
      if (this.hasOnlySignedEntries)
      {
        arrayOfCodeSource = (CodeSource[])localObject.toArray(new CodeSource[i]);
      }
      else
      {
        arrayOfCodeSource = (CodeSource[])localObject.toArray(new CodeSource[i + 1]);
        arrayOfCodeSource[i] = getUnsignedCS(paramURL);
      }
    }
    else
    {
      arrayOfCodeSource = new CodeSource[] { getUnsignedCS(paramURL) };
    }
    return arrayOfCodeSource;
  }

  synchronized CodeSource getCodeSource(URL paramURL, String paramString)
  {
    int[] arrayOfInt = getSignerIndices(paramString);
    if (arrayOfInt != null)
    {
      Map localMap = getCodeSourceCache();
      if (localMap != null)
        return (CodeSource)localMap.get(arrayOfInt);
    }
    return getUnsignedCS(paramURL);
  }

  private int[] findMatchingSignerIndices(CodeSource paramCodeSource)
  {
    Map localMap = getCodeSourceCache();
    if (localMap == null)
      return emptySignerIndices;
    Iterator localIterator = localMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      if ((localEntry.getValue() instanceof CodeSource))
      {
        CodeSource localCodeSource = (CodeSource)localEntry.getValue();
        if ((localCodeSource != null) && (localCodeSource.equals(paramCodeSource)))
          return (int[])localEntry.getKey();
      }
    }
    if (paramCodeSource.getCodeSigners() == null)
      return emptySignerIndices;
    return null;
  }

  private int[] getSignerIndices(String paramString)
  {
    if ((this.signerMap == null) || (this.signerMap.isEmpty()))
      return null;
    if (hasStrictSingleSigning())
    {
      if ((!JarUtil.isSigningRelated(paramString)) && (!paramString.endsWith("/")))
        return (int[])this.signerMap.get(null);
      return null;
    }
    return (int[])this.signerMap.get(paramString);
  }

  synchronized CodeSigner[] getCodeSigners(String paramString)
  {
    int[] arrayOfInt = getSignerIndices(paramString);
    CodeSigner[] arrayOfCodeSigner = null;
    if ((this.signers != null) && (arrayOfInt != null))
    {
      arrayOfCodeSigner = new CodeSigner[arrayOfInt.length];
      for (int i = 0; i < arrayOfInt.length; i++)
        if (this.signers != null)
          arrayOfCodeSigner[i] = this.signers[arrayOfInt[i]];
    }
    return arrayOfCodeSigner;
  }

  synchronized Certificate[] getCertificates(String paramString)
  {
    int[] arrayOfInt = getSignerIndices(paramString);
    if ((this.signers != null) && (arrayOfInt != null))
    {
      ArrayList localArrayList = new ArrayList();
      for (int i = 0; i < arrayOfInt.length; i++)
        localArrayList.addAll(this.signers[arrayOfInt[i]].getSignerCertPath().getCertificates());
      return (Certificate[])localArrayList.toArray(new Certificate[localArrayList.size()]);
    }
    return null;
  }

  synchronized Certificate[] getCertificates14(String paramString)
  {
    int[] arrayOfInt = getCertIndices(paramString);
    Certificate[] arrayOfCertificate = null;
    if ((this.certificates != null) && (arrayOfInt != null))
    {
      arrayOfCertificate = new Certificate[this.certificates.length];
      for (int i = 0; i < this.certificates.length; i++)
        arrayOfCertificate[i] = this.certificates[arrayOfInt[i]];
    }
    return arrayOfCertificate;
  }

  private synchronized int[] getCertIndices(String paramString)
  {
    Map localMap = getCertificateMap();
    if ((localMap == null) || (localMap.isEmpty()))
      return null;
    if (hasStrictSingleSigning())
    {
      if ((!JarUtil.isSigningRelated(paramString)) && (!paramString.endsWith("/")))
        return (int[])localMap.get(null);
      return null;
    }
    return (int[])localMap.get(paramString);
  }

  synchronized boolean collectEntryNamesBySigners(CodeSource[] paramArrayOfCodeSource, List paramList)
  {
    boolean bool = false;
    ArrayList localArrayList = new ArrayList(paramArrayOfCodeSource.length);
    Object localObject;
    for (int i = 0; i < paramArrayOfCodeSource.length; i++)
    {
      localObject = findMatchingSignerIndices(paramArrayOfCodeSource[i]);
      if (localObject != null)
        if (localObject.length > 0)
          localArrayList.add(localObject);
        else
          bool = true;
    }
    if (this.signerMap != null)
    {
      Iterator localIterator = this.signerMap.keySet().iterator();
      while (localIterator.hasNext())
      {
        localObject = (String)localIterator.next();
        if (localArrayList.contains(this.signerMap.get(localObject)))
          paramList.add(localObject);
        else
          Trace.println("collectEntryNamesBySigners: unmatched entry " + (String)localObject, TraceLevel.CACHE);
      }
    }
    return bool;
  }

  private static CodeSource getUnsignedCS(URL paramURL)
  {
    return new CodeSource(paramURL, (Certificate[])null);
  }

  public String toString()
  {
    return "JarSigningData{hasOnlySignedEntries=" + this.hasOnlySignedEntries + ", hasSingleCodeSource=" + this.hasSingleCodeSource + ", hasMissingSignedEntries=" + this.hasMissingSignedEntries + '}';
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.cache.JarSigningData
 * JD-Core Version:    0.6.2
 */