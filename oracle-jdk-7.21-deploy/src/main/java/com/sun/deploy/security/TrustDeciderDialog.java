package com.sun.deploy.security;

import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.ui.AppInfo;
import com.sun.deploy.uitoolkit.ToolkitStore;
import com.sun.deploy.uitoolkit.ui.UIFactory;
import java.net.URL;
import java.security.Principal;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;

class TrustDeciderDialog
{
  public static int showDialog(Certificate[] paramArrayOfCertificate, URL paramURL, int paramInt1, int paramInt2, boolean paramBoolean1, int paramInt3, Date paramDate, AppInfo paramAppInfo, boolean paramBoolean2)
    throws CertificateException
  {
    return showDialog(paramArrayOfCertificate, paramURL, paramInt1, paramInt2, paramBoolean1, paramInt3, paramDate, paramAppInfo, paramBoolean2, null, false);
  }

  public static int showDialog(Certificate[] paramArrayOfCertificate, URL paramURL, int paramInt1, int paramInt2, boolean paramBoolean1, int paramInt3, Date paramDate, AppInfo paramAppInfo, boolean paramBoolean2, String paramString)
    throws CertificateException
  {
    return showDialog(paramArrayOfCertificate, paramURL, paramInt1, paramInt2, paramBoolean1, paramInt3, paramDate, paramAppInfo, paramBoolean2, paramString, false);
  }

  public static int showDialog(Certificate[] paramArrayOfCertificate, URL paramURL, int paramInt1, int paramInt2, boolean paramBoolean1, int paramInt3, Date paramDate, AppInfo paramAppInfo, boolean paramBoolean2, String paramString, boolean paramBoolean3)
    throws CertificateException
  {
    try
    {
      URLClassPathControl.disable();
      if (((paramArrayOfCertificate[paramInt1] instanceof X509Certificate)) && ((paramArrayOfCertificate[(paramInt2 - 1)] instanceof X509Certificate)))
      {
        X509Certificate localX509Certificate1 = (X509Certificate)paramArrayOfCertificate[paramInt1];
        X509Certificate localX509Certificate2 = (X509Certificate)paramArrayOfCertificate[(paramInt2 - 1)];
        ArrayList localArrayList = CertUtils.getServername(localX509Certificate1);
        boolean bool = (paramString != null) && (!CertUtils.checkWildcardDomainList(paramString, localArrayList));
        Principal localPrincipal1 = localX509Certificate1.getSubjectDN();
        Principal localPrincipal2 = localX509Certificate2.getIssuerDN();
        String str1 = localPrincipal1.getName();
        String str2 = null;
        int j = str1.indexOf("CN=");
        int k = 0;
        if (j < 0)
          str2 = getMessage("security.dialog.unknown.subject");
        else
          try
          {
            j += 3;
            if (str1.charAt(j) == '"')
            {
              j += 1;
              k = str1.indexOf('"', j);
            }
            else
            {
              k = str1.indexOf(',', j);
            }
            if (k < 0)
              str2 = str1.substring(j);
            else
              str2 = str1.substring(j, k);
          }
          catch (IndexOutOfBoundsException localIndexOutOfBoundsException1)
          {
            str2 = getMessage("security.dialog.unknown.subject");
          }
        String str3 = localPrincipal2.getName();
        String str4 = null;
        j = str3.indexOf("O=");
        k = 0;
        if (j < 0)
          str4 = getMessage("security.dialog.unknown.issuer");
        else
          try
          {
            j += 2;
            if (str3.charAt(j) == '"')
            {
              j += 1;
              k = str3.indexOf('"', j);
            }
            else
            {
              k = str3.indexOf(',', j);
            }
            if (k < 0)
              str4 = str3.substring(j);
            else
              str4 = str3.substring(j, k);
          }
          catch (IndexOutOfBoundsException localIndexOutOfBoundsException2)
          {
            str4 = getMessage("security.dialog.unknown.issuer");
          }
        int m = doShowDialog(paramArrayOfCertificate, paramURL, paramInt1, paramInt2, paramBoolean1, paramInt3, paramDate, paramAppInfo, paramBoolean2, paramString, paramBoolean3, str4, str2, bool);
        return m;
      }
      int i = -1;
      return i;
    }
    finally
    {
      URLClassPathControl.enable();
    }
  }

  protected static int doShowDialog(Certificate[] paramArrayOfCertificate, URL paramURL, int paramInt1, int paramInt2, boolean paramBoolean1, int paramInt3, Date paramDate, AppInfo paramAppInfo, boolean paramBoolean2, String paramString1, boolean paramBoolean3, String paramString2, String paramString3, boolean paramBoolean4)
    throws CertificateException
  {
    int i = -1;
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    boolean bool = false;
    String str1 = null;
    String str2 = null;
    String str3 = paramBoolean2 ? getMessage("security.dialog.https.buttonContinue") : getMessage("security.dialog.signed.buttonContinue");
    String str4 = paramBoolean2 ? getMessage("security.dialog.https.buttonCancel") : getMessage("security.dialog.signed.buttonCancel");
    if ((!paramBoolean1) && (!paramBoolean4) && (paramInt3 == 0))
    {
      if (paramBoolean3)
      {
        str2 = getMessage("security.dialog.signed.caption");
        localArrayList1.add(getMessage("security.dialog.jnlpunsigned.sub"));
      }
      else
      {
        str2 = paramBoolean2 ? getMessage("security.dialog.verified.valid.https.caption") : getMessage("security.dialog.signed.caption");
        localArrayList2.add(paramBoolean2 ? getMessage("security.dialog.verified.valid.https.sub") : getMessage("security.dialog.valid.signed.risk"));
      }
      if (!paramBoolean2)
      {
        localArrayList2.add(getMessage("security.dialog.verified.https.publisher"));
        if (paramDate != null)
        {
          localObject1 = DateFormat.getDateTimeInstance(1, 1);
          localObject2 = ((DateFormat)localObject1).format(paramDate);
          localObject3 = new Object[] { localObject2 };
          localObject4 = new MessageFormat(getMessage("security.dialog.timestamp"));
          localArrayList2.add(((MessageFormat)localObject4).format(localObject3));
        }
      }
    }
    else
    {
      if (paramBoolean1)
      {
        bool = true;
        if (paramBoolean2)
        {
          str1 = getMessage("security.dialog.unverified.https.caption");
          localArrayList1.add(getMessage("security.dialog.unverified.https.generic"));
          localArrayList1.add(getMessage("security.dialog.unverified.https.sub"));
        }
        else
        {
          str1 = getMessage("security.dialog.signed.caption");
          localArrayList1.add(getMessage("security.dialog.unverified.signed.sub.new"));
          localArrayList1.add(getMessage("security.dialog.signed.moreinfo.generic2"));
          localArrayList1.add(getMessage("security.dialog.unverified.signed.publisher"));
        }
      }
      else if (!paramBoolean4)
      {
        if (paramBoolean2)
        {
          str1 = getMessage("security.dialog.unverified.https.caption");
        }
        else
        {
          str1 = getMessage("security.dialog.signed.caption");
          localArrayList2.add(getMessage("security.dialog.verified.signed.publisher"));
        }
      }
      switch (paramInt3)
      {
      case -1:
        if (localArrayList1.isEmpty())
          if (paramBoolean2)
            localArrayList1.add(getMessage("security.dialog.unverified.https.generic"));
          else
            localArrayList1.add(getMessage("security.dialog.expired.signed.sub"));
        localArrayList1.add(paramBoolean2 ? getMessage("security.dialog.expired.https.time") : getMessage("security.dialog.expired.signed.time"));
        break;
      case 1:
        if (localArrayList1.isEmpty())
          if (paramBoolean2)
            localArrayList1.add(getMessage("security.dialog.unverified.https.generic"));
          else
            localArrayList1.add(getMessage("security.dialog.notyet.signed.sub"));
        localArrayList1.add(paramBoolean2 ? getMessage("security.dialog.notyetvalid.https.time") : getMessage("security.dialog.notyetvalid.signed.time"));
        break;
      case 0:
      default:
        if ((!paramBoolean2) && (paramDate != null))
        {
          localObject1 = DateFormat.getDateTimeInstance(1, 1);
          localObject2 = ((DateFormat)localObject1).format(paramDate);
          localObject3 = new Object[] { localObject2 };
          localObject4 = new MessageFormat(getMessage("security.dialog.timestamp"));
          localArrayList2.add(((MessageFormat)localObject4).format(localObject3));
        }
        break;
      }
    }
    if (paramBoolean4)
    {
      localObject1 = new Object[] { paramString1, paramString3 };
      if (str1 == null)
        str1 = ResourceManager.getMessage("security.dialog.unverified.https.caption");
      if (localArrayList1.isEmpty())
        localArrayList1.add(getMessage("security.dialog.unverified.https.generic"));
      localArrayList1.add(ResourceManager.getFormattedMessage("security.dialog.hostname.mismatch.moreinfo", (Object[])localObject1));
    }
    if (!paramBoolean2)
    {
      localObject1 = getMessage("security.dialog.signed.moreinfo.generic");
      localObject2 = localArrayList2;
      if (!localArrayList1.isEmpty())
        localObject2 = localArrayList1;
      ((ArrayList)localObject2).add(1, localObject1);
    }
    else if (paramString1 != null)
    {
      paramAppInfo.setTitle(paramString1);
    }
    else
    {
      paramAppInfo.setTitle(paramString3);
    }
    if (paramBoolean3)
      localArrayList1.add(getMessage("security.dialog.jnlpunsigned.more"));
    Object localObject1 = str1 != null ? str1 : str2;
    Object localObject2 = null;
    Object localObject3 = null;
    Object localObject4 = null;
    int j;
    if (!localArrayList1.isEmpty())
    {
      localObject2 = new String[localArrayList1.size()];
      for (j = 0; j < localArrayList1.size(); j++)
        localObject2[j] = localArrayList1.get(j).toString();
    }
    if (!localArrayList2.isEmpty())
    {
      localObject3 = new String[localArrayList2.size()];
      for (j = 0; j < localArrayList2.size(); j++)
        localObject3[j] = localArrayList2.get(j).toString();
    }
    if ((localArrayList1.isEmpty()) && (localArrayList2.isEmpty()))
      throw new CertificateException(getMessage("security.dialog.exception.message"));
    if (!Trace.isAutomationEnabled())
    {
      if ((paramAppInfo.getType() == 3) && (localObject2 == null) && (!paramBoolean3))
      {
        localObject1 = getMessage("security.dialog.extension.caption");
        String str5 = getMessage("security.dialog.extension.buttonInstall");
        String[] arrayOfString = null;
        arrayOfString = new String[2];
        arrayOfString[0] = getMessage("security.dialog.extension.sub");
        MessageFormat localMessageFormat = new MessageFormat(getMessage("security.dialog.extension.warning"));
        Object[] arrayOfObject = { paramString3, paramString3, paramString3 };
        arrayOfString[1] = localMessageFormat.format(arrayOfObject);
        i = ToolkitStore.getUI().showSecurityDialog(paramAppInfo, getMessage("security.dialog.extension.title"), (String)localObject1, paramString3, paramURL, true, false, str5, str4, (String[])localObject2, arrayOfString, true, paramArrayOfCertificate, paramInt1, paramInt2, bool, paramBoolean2);
        ToolkitStore.getUI();
        if (i == 0)
        {
          ToolkitStore.getUI();
          i = 2;
        }
      }
      else
      {
        if (paramBoolean1)
          paramString3 = getMessage("security.dialog.notverified.subject").toUpperCase();
        if (localObject2 == null)
          localObject4 = getMessage("security.dialog.valid.caption");
        else
          localObject4 = getMessage("security.dialog.caption");
        i = ToolkitStore.getUI().showSecurityDialog(paramAppInfo, (String)localObject4, (String)localObject1, paramString3, paramURL, true, false, str3, str4, (String[])localObject2, (String[])localObject3, true, paramArrayOfCertificate, paramInt1, paramInt2, bool, paramBoolean2);
      }
    }
    else
    {
      Trace.msgSecurityPrintln("trustdecider.automation.trustcert");
      i = 0;
    }
    return i;
  }

  private static String getMessage(String paramString)
  {
    return ResourceManager.getMessage(paramString);
  }

  private static int getAcceleratorKey(String paramString)
  {
    return ResourceManager.getAcceleratorKey(paramString);
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.security.TrustDeciderDialog
 * JD-Core Version:    0.6.2
 */