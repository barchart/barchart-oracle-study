package com.sun.deploy.security;

import com.sun.deploy.trace.Trace;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;

final class DeniedCertStore
  implements CertStore
{
  private KeyStore deniedKS = null;

  public void load()
    throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException
  {
    load(false);
  }

  public void load(boolean paramBoolean)
    throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException
  {
    if (this.deniedKS == null)
      try
      {
        this.deniedKS = KeyStore.getInstance("JKS");
        this.deniedKS.load(null, new char[0]);
      }
      catch (IOException localIOException)
      {
        Trace.msgSecurityPrintln(localIOException.getMessage());
      }
      catch (KeyStoreException localKeyStoreException)
      {
        Trace.msgSecurityPrintln(localKeyStoreException.getMessage());
      }
      catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
      {
        Trace.msgSecurityPrintln(localNoSuchAlgorithmException.getMessage());
      }
      catch (CertificateException localCertificateException)
      {
        Trace.msgSecurityPrintln(localCertificateException.getMessage());
      }
  }

  public void save()
    throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException
  {
  }

  public boolean add(Certificate paramCertificate)
    throws KeyStoreException
  {
    return add(paramCertificate, null, false);
  }

  public boolean add(Certificate paramCertificate, String paramString, boolean paramBoolean)
    throws KeyStoreException
  {
    return CertUtils.add(this.deniedKS, "deniedcertstore", paramCertificate, paramString, paramBoolean);
  }

  public boolean remove(Certificate paramCertificate)
    throws IOException, KeyStoreException
  {
    Trace.msgSecurityPrintln("deniedcertstore.cert.removing");
    String str = this.deniedKS.getCertificateAlias(paramCertificate);
    if (str != null)
      this.deniedKS.deleteEntry(str);
    Trace.msgSecurityPrintln("deniedcertstore.cert.removed");
    return true;
  }

  public boolean contains(Certificate paramCertificate)
    throws KeyStoreException
  {
    return contains(paramCertificate, null, false);
  }

  public boolean contains(Certificate paramCertificate, String paramString, boolean paramBoolean)
    throws KeyStoreException
  {
    return CertUtils.contains(this.deniedKS, paramCertificate, paramString, paramBoolean);
  }

  public boolean verify(Certificate paramCertificate)
    throws KeyStoreException
  {
    return false;
  }

  public Collection getCertificates()
    throws KeyStoreException
  {
    Trace.msgSecurityPrintln("deniedcertstore.cert.getcertificates");
    ArrayList localArrayList = new ArrayList();
    Enumeration localEnumeration = this.deniedKS.aliases();
    while (localEnumeration.hasMoreElements())
    {
      String str = (String)localEnumeration.nextElement();
      Certificate localCertificate = this.deniedKS.getCertificate(str);
      localArrayList.add(localCertificate);
    }
    return localArrayList;
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.security.DeniedCertStore
 * JD-Core Version:    0.6.2
 */