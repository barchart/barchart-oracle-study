package com.sun.deploy.security;

import com.sun.deploy.config.Config;
import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.uitoolkit.ToolkitStore;
import com.sun.deploy.uitoolkit.ui.UIFactory;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

public final class DeploySigningCertStore
  implements CertStore
{
  private static String _userFilename = Config.getUserTrustedCertificateFile();
  private static String _systemFilename = Config.getSystemTrustedCertificateFile();
  private static String _sandboxFilename = Config.getSandboxTrustedCertificateFile();
  private long _userLastModified = 0L;
  private long _sysLastModified = 0L;
  private long _sandLastModified = 0L;
  private KeyStore _deploymentUserCerts = CertUtils.createEmptyKeyStore();
  private KeyStore _deploymentSystemCerts = CertUtils.createEmptyKeyStore();
  private KeyStore _deploymentSandboxCerts = CertUtils.createEmptyKeyStore();
  private char[] keyPassphrase = new char[0];
  private boolean cancelFlag = false;
  private int certStoreType = 0;

  private DeploySigningCertStore(int paramInt)
  {
    this.certStoreType = paramInt;
  }

  public static CertStore getCertStore()
  {
    return new ImmutableCertStore(new DeploySigningCertStore(3));
  }

  public static CertStore getUserCertStore()
  {
    return new DeploySigningCertStore(1);
  }

  public static CertStore getSystemCertStore()
  {
    return new ImmutableCertStore(new DeploySigningCertStore(2));
  }

  public static CertStore getSandboxCertStore()
  {
    return new DeploySigningCertStore(4);
  }

  public void load()
    throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException
  {
    load(false);
  }

  public void load(boolean paramBoolean)
    throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException
  {
    long l;
    if (((this.certStoreType & 0x1) == 1) && (_userFilename != null))
    {
      l = CertUtils.getFileLastModified(_userFilename);
      if (l != this._userLastModified)
      {
        this._deploymentUserCerts = loadCertStore(_userFilename, paramBoolean);
        this._userLastModified = l;
      }
    }
    if (((this.certStoreType & 0x2) == 2) && (_systemFilename != null))
    {
      l = CertUtils.getFileLastModified(_systemFilename);
      if (l != this._sysLastModified)
      {
        this._deploymentSystemCerts = loadCertStore(_systemFilename, paramBoolean);
        this._sysLastModified = l;
      }
    }
    if (((this.certStoreType & 0x4) == 4) && (_sandboxFilename != null))
    {
      l = CertUtils.getFileLastModified(_sandboxFilename);
      if (l != this._sandLastModified)
      {
        this._deploymentSandboxCerts = loadCertStore(_sandboxFilename, paramBoolean);
        this._sandLastModified = l;
      }
    }
  }

  private KeyStore loadCertStore(String paramString, boolean paramBoolean)
    throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException
  {
    Trace.msgSecurityPrintln("deploycertstore.cert.loading", new Object[] { paramString });
    File localFile = new File(paramString);
    KeyStore localKeyStore = KeyStore.getInstance("JKS");
    localKeyStore.load(null, null);
    try
    {
      AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        private final File val$file;
        private final boolean val$integrityCheck;
        private final KeyStore val$keyStore;

        public Object run()
          throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException
        {
          if (this.val$file.exists())
          {
            FileInputStream localFileInputStream = new FileInputStream(this.val$file);
            BufferedInputStream localBufferedInputStream = new BufferedInputStream(localFileInputStream);
            if (this.val$integrityCheck)
            {
              DeploySigningCertStore.this.cancelFlag = false;
              this.val$keyStore.load(localBufferedInputStream, new char[0]);
            }
            else
            {
              this.val$keyStore.load(localBufferedInputStream, null);
            }
            localBufferedInputStream.close();
            localFileInputStream.close();
          }
          return null;
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      Exception localException = localPrivilegedActionException.getException();
      if ((localException instanceof IOException))
      {
        if (paramBoolean)
        {
          FileInputStream localFileInputStream = new FileInputStream(localFile);
          BufferedInputStream localBufferedInputStream = new BufferedInputStream(localFileInputStream);
          CredentialInfo localCredentialInfo = ToolkitStore.getUI().showPasswordDialog(null, ResourceManager.getMessage("password.dialog.title"), ResourceManager.getMessage("deploycertstore.password.dialog.text"), false, false, null, false, null);
          if (localCredentialInfo != null)
          {
            this.cancelFlag = false;
            this.keyPassphrase = localCredentialInfo.getPassword();
            localKeyStore.load(localBufferedInputStream, this.keyPassphrase);
          }
          else
          {
            this.cancelFlag = true;
          }
          localBufferedInputStream.close();
          localFileInputStream.close();
        }
        else
        {
          throw ((IOException)localException);
        }
      }
      else
      {
        if ((localException instanceof CertificateException))
          throw ((CertificateException)localException);
        if ((localException instanceof KeyStoreException))
          throw ((KeyStoreException)localException);
        if ((localException instanceof NoSuchAlgorithmException))
          throw ((NoSuchAlgorithmException)localException);
        Trace.securityPrintException(localPrivilegedActionException);
      }
    }
    Trace.msgSecurityPrintln("deploycertstore.cert.loaded", new Object[] { paramString });
    return localKeyStore;
  }

  public void save()
    throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException
  {
    String str = this.certStoreType == 4 ? _sandboxFilename : _userFilename;
    KeyStore localKeyStore = this.certStoreType == 4 ? this._deploymentSandboxCerts : this._deploymentUserCerts;
    Trace.msgSecurityPrintln("deploycertstore.cert.saving", new Object[] { str });
    try
    {
      AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        private final String val$filename;
        private final KeyStore val$ks;

        public Object run()
          throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException
        {
          File localFile = new File(this.val$filename);
          localFile.getParentFile().mkdirs();
          FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
          BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(localFileOutputStream);
          this.val$ks.store(localBufferedOutputStream, DeploySigningCertStore.this.keyPassphrase);
          localBufferedOutputStream.close();
          localFileOutputStream.close();
          return null;
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      Exception localException = localPrivilegedActionException.getException();
      if ((localException instanceof IOException))
        throw ((IOException)localException);
      if ((localException instanceof CertificateException))
        throw ((CertificateException)localException);
      if ((localException instanceof KeyStoreException))
        throw ((KeyStoreException)localException);
      if ((localException instanceof NoSuchAlgorithmException))
        throw ((NoSuchAlgorithmException)localException);
      Trace.securityPrintException(localPrivilegedActionException);
    }
    Trace.msgSecurityPrintln("deploycertstore.cert.saved", new Object[] { str });
  }

  public boolean add(Certificate paramCertificate)
    throws KeyStoreException
  {
    return add(paramCertificate, null, false);
  }

  public boolean add(Certificate paramCertificate, String paramString, boolean paramBoolean)
    throws KeyStoreException
  {
    String str;
    KeyStore localKeyStore;
    if (this.certStoreType == 4)
    {
      str = "deploymentsandboxcert";
      localKeyStore = this._deploymentSandboxCerts;
    }
    else
    {
      str = "deploymentusercert";
      localKeyStore = this._deploymentUserCerts;
    }
    return CertUtils.add(localKeyStore, str, paramCertificate, paramString, paramBoolean);
  }

  public boolean remove(Certificate paramCertificate)
    throws IOException, KeyStoreException
  {
    if (this.cancelFlag)
      return false;
    Trace.msgSecurityPrintln("deploycertstore.cert.removing");
    KeyStore localKeyStore = this.certStoreType == 4 ? this._deploymentSandboxCerts : this._deploymentUserCerts;
    String str = localKeyStore.getCertificateAlias(paramCertificate);
    if (str != null)
      localKeyStore.deleteEntry(str);
    Trace.msgSecurityPrintln("deploycertstore.cert.removed", new Object[] { str });
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
    if (this.certStoreType == 4)
      return CertUtils.contains(this._deploymentSandboxCerts, paramCertificate, paramString, paramBoolean);
    return (CertUtils.contains(this._deploymentUserCerts, paramCertificate, paramString, paramBoolean)) || (CertUtils.contains(this._deploymentSystemCerts, paramCertificate, paramString, paramBoolean));
  }

  public boolean verify(Certificate paramCertificate)
  {
    Trace.msgSecurityPrintln("deploycertstore.cert.canverify");
    return false;
  }

  public Collection getCertificates()
    throws KeyStoreException
  {
    HashSet localHashSet = new HashSet();
    if ((this.certStoreType & 0x1) == 1)
      localHashSet.addAll(getCertificates(1));
    if ((this.certStoreType & 0x2) == 2)
      localHashSet.addAll(getCertificates(2));
    if ((this.certStoreType & 0x4) == 4)
      localHashSet.addAll(getCertificates(4));
    return localHashSet;
  }

  private Collection getCertificates(int paramInt)
    throws KeyStoreException
  {
    Trace.msgSecurityPrintln("deploycertstore.cert.getcertificates");
    ArrayList localArrayList = new ArrayList();
    KeyStore localKeyStore = null;
    if (paramInt == 4)
      localKeyStore = this._deploymentSandboxCerts;
    else if (paramInt == 1)
      localKeyStore = this._deploymentUserCerts;
    else
      localKeyStore = this._deploymentSystemCerts;
    Enumeration localEnumeration = localKeyStore.aliases();
    TreeSet localTreeSet = new TreeSet();
    while (localEnumeration.hasMoreElements())
    {
      localObject = (String)localEnumeration.nextElement();
      localTreeSet.add(localObject);
    }
    Object localObject = localTreeSet.iterator();
    while (((Iterator)localObject).hasNext())
    {
      String str = (String)((Iterator)localObject).next();
      Certificate localCertificate = localKeyStore.getCertificate(str);
      localArrayList.add(localCertificate);
    }
    return localArrayList;
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.security.DeploySigningCertStore
 * JD-Core Version:    0.6.2
 */