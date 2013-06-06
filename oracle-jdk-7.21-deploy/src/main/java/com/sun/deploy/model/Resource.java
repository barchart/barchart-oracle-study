package com.sun.deploy.model;

import java.io.File;
import java.security.CodeSigner;
import java.security.cert.Certificate;
import java.util.Map;
import java.util.jar.JarFile;

public abstract interface Resource
{
  public static final int STATE_INITIAL = 0;
  public static final int STATE_READY = 1;
  public static final int STATE_RETIRED = 2;
  public static final byte PREVERIFY_NOTDONE = 0;
  public static final byte PREVERIFY_SUCCEEDED = 1;
  public static final byte PREVERIFY_FAILED = 2;

  public abstract String getURL();

  public abstract String getVersion();

  public abstract long getLastModified();

  public abstract long getExpirationDate();

  public abstract int getContentLength();

  public abstract Map getHeaders();

  public abstract long getSize();

  public abstract String getResourceFilename();

  public abstract File getDataFile();

  public abstract int getState();

  public abstract boolean isJNLPFile();

  public abstract boolean isJarFile();

  public abstract JarFile getJarFile();

  public abstract long getValidationTimestamp();

  public abstract boolean isKnownToBeSigned();

  public abstract CodeSigner[] getCodeSigners();

  public abstract Certificate[] getCertificates();

  public abstract Map getCachedTrustedEntries();

  public abstract byte getClassesVerificationStatus();

  public abstract void updateValidationResults(boolean paramBoolean1, Map paramMap, long paramLong1, long paramLong2, boolean paramBoolean2);
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.model.Resource
 * JD-Core Version:    0.6.2
 */