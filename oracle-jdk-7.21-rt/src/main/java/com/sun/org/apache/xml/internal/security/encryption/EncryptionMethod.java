package com.sun.org.apache.xml.internal.security.encryption;

import java.util.Iterator;
import org.w3c.dom.Element;

public abstract interface EncryptionMethod
{
  public abstract String getAlgorithm();

  public abstract int getKeySize();

  public abstract void setKeySize(int paramInt);

  public abstract byte[] getOAEPparams();

  public abstract void setOAEPparams(byte[] paramArrayOfByte);

  public abstract Iterator getEncryptionMethodInformation();

  public abstract void addEncryptionMethodInformation(Element paramElement);

  public abstract void removeEncryptionMethodInformation(Element paramElement);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.security.encryption.EncryptionMethod
 * JD-Core Version:    0.6.2
 */