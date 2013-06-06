package com.sun.deploy.security;

import com.sun.deploy.trace.Trace;
import com.sun.deploy.util.SessionState;
import com.sun.deploy.util.SessionState.Client;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.AccessController;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;

public final class SessionCertStore
  implements CertStore, SessionState.Client
{
  private KeyStore sessionKS = null;
  private String name;
  private File sessionDataFolder;

  private SessionCertStore()
  {
  }

  public SessionCertStore(String paramString)
  {
    this.name = paramString;
    SessionState.register(this);
  }

  public void load()
    throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException
  {
    load(false);
  }

  public synchronized void load(boolean paramBoolean)
    throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException
  {
    Trace.msgSecurityPrintln("sessioncertstore.cert.loading");
    if (this.sessionKS == null)
      try
      {
        this.sessionKS = KeyStore.getInstance("JKS");
        this.sessionKS.load(null, new char[0]);
        if (this.sessionDataFolder != null)
        {
          importState(this.sessionDataFolder);
          this.sessionDataFolder = null;
        }
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
    Trace.msgSecurityPrintln("sessioncertstore.cert.loaded");
  }

  public synchronized void importState(File paramFile)
  {
    if (this.sessionKS == null)
    {
      this.sessionDataFolder = paramFile;
      return;
    }
    try
    {
      AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        private final File val$folder;

        public Object run()
          throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException
        {
          File localFile = new File(this.val$folder, SessionCertStore.this.name);
          if ((localFile != null) && (localFile.exists()))
          {
            FileInputStream localFileInputStream = new FileInputStream(localFile);
            BufferedInputStream localBufferedInputStream = new BufferedInputStream(localFileInputStream);
            SessionCertStore.this.sessionKS.load(localBufferedInputStream, new char[0]);
            localBufferedInputStream.close();
            localFileInputStream.close();
            localFile.delete();
          }
          return null;
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      Trace.ignored(localPrivilegedActionException.getException());
    }
  }

  public void exportState(File paramFile)
  {
    if (this.sessionKS == null)
      return;
    File localFile = new File(paramFile, this.name);
    try
    {
      AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        private final File val$outfile;

        public Object run()
          throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException
        {
          this.val$outfile.getParentFile().mkdirs();
          FileOutputStream localFileOutputStream = new FileOutputStream(this.val$outfile);
          BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(localFileOutputStream);
          char[] arrayOfChar = new char[0];
          SessionCertStore.this.sessionKS.store(localBufferedOutputStream, arrayOfChar);
          localBufferedOutputStream.close();
          localFileOutputStream.close();
          return null;
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      Trace.ignored(localPrivilegedActionException.getException());
    }
  }

  public void save()
    throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException
  {
    Trace.msgSecurityPrintln("sessioncertstore.cert.saving");
    Trace.msgSecurityPrintln("sessioncertstore.cert.saved");
  }

  public boolean add(Certificate paramCertificate)
    throws KeyStoreException
  {
    return add(paramCertificate, null, false);
  }

  public boolean add(Certificate paramCertificate, String paramString, boolean paramBoolean)
    throws KeyStoreException
  {
    return CertUtils.add(this.sessionKS, "sessioncertstore", paramCertificate, paramString, paramBoolean);
  }

  public boolean remove(Certificate paramCertificate)
    throws IOException, KeyStoreException
  {
    Trace.msgSecurityPrintln("sessioncertstore.cert.removing");
    String str = this.sessionKS.getCertificateAlias(paramCertificate);
    if (str != null)
      this.sessionKS.deleteEntry(str);
    Trace.msgSecurityPrintln("sessioncertstore.cert.removed");
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
    return CertUtils.contains(this.sessionKS, paramCertificate, paramString, paramBoolean);
  }

  public boolean verify(Certificate paramCertificate)
    throws KeyStoreException
  {
    Trace.msgSecurityPrintln("sessioncertstore.cert.canverify");
    return false;
  }

  public Collection getCertificates()
    throws KeyStoreException
  {
    Trace.msgSecurityPrintln("sessioncertstore.cert.getcertificates");
    ArrayList localArrayList = new ArrayList();
    Enumeration localEnumeration = this.sessionKS.aliases();
    while (localEnumeration.hasMoreElements())
    {
      String str = (String)localEnumeration.nextElement();
      Certificate localCertificate = this.sessionKS.getCertificate(str);
      localArrayList.add(localCertificate);
    }
    return localArrayList;
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.security.SessionCertStore
 * JD-Core Version:    0.6.2
 */