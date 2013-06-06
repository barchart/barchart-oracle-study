package com.sun.deploy.security;

import com.sun.deploy.config.Config;
import com.sun.deploy.model.DeployCacheJarAccess;
import com.sun.deploy.model.ResourceProvider;
import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import com.sun.deploy.ui.AppInfo;
import com.sun.deploy.uitoolkit.ToolkitStore;
import com.sun.deploy.uitoolkit.ui.NativeMixedCodeDialog;
import com.sun.deploy.uitoolkit.ui.UIFactory;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class CPCallbackHandler
{
  private List childURLs = Collections.synchronizedList(new ArrayList());
  private HashMap assertJars = new HashMap();
  private DeployURLClassPathCallback pcb;
  private DeployURLClassPathCallback ccb;
  private Map resource2trust = new HashMap();
  private Map package2trust = new HashMap();
  private Map defaultCS = new HashMap();
  private Set trustedCS = new HashSet();
  private Set authenticatedCS = new HashSet();
  private Set unauthenticatedCS = new HashSet();
  static CodeSource untrustedCS = new CodeSource(null, (Certificate[])null);
  private static DeployCacheJarAccess jarAccess = ResourceProvider.get().getJarAccess();
  private CPCallbackClassLoaderIf parent;
  private CPCallbackClassLoaderIf child;

  public CPCallbackHandler(CPCallbackClassLoaderIf paramCPCallbackClassLoaderIf1, CPCallbackClassLoaderIf paramCPCallbackClassLoaderIf2)
  {
    this.parent = paramCPCallbackClassLoaderIf1;
    this.child = paramCPCallbackClassLoaderIf2;
    this.pcb = new ParentCallback(null);
    this.ccb = new ChildCallback(null);
  }

  public DeployURLClassPathCallback getParentCallback()
  {
    return this.pcb;
  }

  public DeployURLClassPathCallback getChildCallback()
  {
    return this.ccb;
  }

  public void checkUntrustedAccess()
  {
    if ((this.pcb != null) && ((this.pcb instanceof ParentCallback)))
      ((ParentCallback)this.pcb).check(null, false, false);
  }

  protected static boolean hasTrustedLibraryAssertion(JarFile paramJarFile)
  {
    try
    {
      Manifest localManifest = paramJarFile.getManifest();
      if (localManifest != null)
      {
        Attributes localAttributes = localManifest.getMainAttributes();
        if (localAttributes != null)
        {
          String str = localAttributes.getValue(new Attributes.Name("Trusted-Library"));
          boolean bool1 = false;
          if (str != null)
            bool1 = Boolean.valueOf(str.trim()).booleanValue();
          str = localAttributes.getValue(new Attributes.Name("X-Trusted-Library"));
          boolean bool2 = false;
          if (str != null)
            bool2 = Boolean.valueOf(str.trim()).booleanValue();
          if (bool2)
            Trace.println("old X-Trusted-Library assertion in JAR", TraceLevel.SECURITY);
          return (bool1) || (bool2);
        }
      }
    }
    catch (IOException localIOException)
    {
    }
    return false;
  }

  protected static boolean hasTrustedOnlyAssertion(JarFile paramJarFile)
  {
    try
    {
      Manifest localManifest = paramJarFile.getManifest();
      if (localManifest != null)
      {
        Attributes localAttributes = localManifest.getMainAttributes();
        if (localAttributes != null)
        {
          String str = localAttributes.getValue(new Attributes.Name("Trusted-Only"));
          boolean bool1 = false;
          if (str != null)
            bool1 = Boolean.valueOf(str.trim()).booleanValue();
          str = localAttributes.getValue(new Attributes.Name("X-Signed-Only"));
          boolean bool2 = false;
          if (str != null)
            bool2 = Boolean.valueOf(str.trim()).booleanValue();
          if (bool2)
            Trace.println("old X-Signed-Only assertion in JAR", TraceLevel.SECURITY);
          return (bool1) || (bool2);
        }
      }
    }
    catch (IOException localIOException)
    {
    }
    return false;
  }

  private synchronized String assertTrust(JarFile paramJarFile, CodeSource[] paramArrayOfCodeSource)
  {
    HashMap localHashMap1 = new HashMap();
    HashMap localHashMap2 = new HashMap();
    String str2 = null;
    for (int i = 0; i < paramArrayOfCodeSource.length; i++)
    {
      Enumeration localEnumeration = jarAccess.entryNames(paramJarFile, new CodeSource[] { paramArrayOfCodeSource[i] });
      while (localEnumeration.hasMoreElements())
      {
        String str1 = (String)localEnumeration.nextElement();
        if (str1.endsWith(".class"))
        {
          str1 = str1.replace('/', '.');
          str1 = getPackage(str1.substring(0, str1.length() - 6));
          localHashMap1.put(str1, paramArrayOfCodeSource[i]);
        }
        else if (!str1.endsWith("/"))
        {
          localHashMap2.put(str1, paramArrayOfCodeSource[i]);
        }
      }
    }
    Set localSet1 = localHashMap1.entrySet();
    Set localSet2 = localHashMap2.entrySet();
    Map.Entry[] arrayOfEntry1 = (Map.Entry[])localSet1.toArray(new Map.Entry[localSet1.size()]);
    Map.Entry[] arrayOfEntry2 = (Map.Entry[])localSet2.toArray(new Map.Entry[localSet2.size()]);
    int j = setTrust(this.resource2trust, arrayOfEntry2);
    if (j != -1)
      str2 = "untrusted resource \"" + (String)arrayOfEntry2[j].getKey() + "\" in class path";
    j = setTrust(this.package2trust, arrayOfEntry1);
    if (j != -1)
    {
      unwindTrust(this.resource2trust, arrayOfEntry2);
      str2 = "untrusted class package \"" + (String)arrayOfEntry1[j].getKey() + "\" in class path";
    }
    return str2;
  }

  private String getPackage(String paramString)
  {
    int i = paramString.lastIndexOf('.');
    return i == -1 ? "" : paramString.substring(0, i);
  }

  private int setTrust(Map paramMap, Map.Entry[] paramArrayOfEntry)
  {
    for (int i = 0; i < paramArrayOfEntry.length; i++)
    {
      CodeSource localCodeSource1 = setTrust(paramMap, (String)paramArrayOfEntry[i].getKey(), (CodeSource)paramArrayOfEntry[i].getValue());
      if (localCodeSource1 != null)
      {
        CodeSource localCodeSource2 = (CodeSource)paramArrayOfEntry[i].getValue();
        if ((!localCodeSource1.equals(localCodeSource2)) && (isTrusted(localCodeSource1) != isTrusted(localCodeSource2)))
          break;
        paramArrayOfEntry[i] = null;
      }
    }
    if (i == paramArrayOfEntry.length)
      return -1;
    unwindTrust(paramMap, paramArrayOfEntry, i);
    return i;
  }

  private CodeSource setTrust(Map paramMap, String paramString, CodeSource paramCodeSource)
  {
    CodeSource localCodeSource = (CodeSource)paramMap.get(paramString);
    if (localCodeSource == null)
    {
      paramMap.put(paramString, paramCodeSource);
      return null;
    }
    return localCodeSource;
  }

  private void unwindTrust(Map paramMap, Map.Entry[] paramArrayOfEntry, int paramInt)
  {
    if (paramInt == 0)
      return;
    paramInt--;
    while (paramInt >= 0)
    {
      if (paramArrayOfEntry[paramInt] != null)
        paramMap.remove(paramArrayOfEntry[paramInt].getKey());
      paramInt--;
    }
  }

  private void unwindTrust(Map paramMap, Map.Entry[] paramArrayOfEntry)
  {
    unwindTrust(paramMap, paramArrayOfEntry, paramArrayOfEntry.length);
  }

  private synchronized boolean checkPackage(String paramString, CodeSource paramCodeSource, Boolean paramBoolean)
  {
    CodeSource localCodeSource = setTrust(this.package2trust, paramString, paramCodeSource);
    return (localCodeSource == null) || (localCodeSource.equals(paramCodeSource)) || (isTrusted(localCodeSource) == paramBoolean);
  }

  private synchronized boolean checkResource(String paramString, CodeSource paramCodeSource, Boolean paramBoolean)
  {
    CodeSource localCodeSource = setTrust(this.resource2trust, paramString, paramCodeSource);
    return (localCodeSource == null) || (localCodeSource.equals(paramCodeSource)) || (isTrusted(localCodeSource) == paramBoolean);
  }

  private synchronized void mergeTrustedSources(CodeSource[] paramArrayOfCodeSource)
  {
    for (int i = 0; i < paramArrayOfCodeSource.length; i++)
      this.trustedCS.add(paramArrayOfCodeSource[i]);
  }

  private synchronized Boolean isTrusted(CodeSource paramCodeSource)
  {
    if (paramCodeSource == untrustedCS)
      return Boolean.FALSE;
    return Boolean.valueOf(this.trustedCS.contains(paramCodeSource));
  }

  private synchronized Boolean isAuthenticated(CodeSource paramCodeSource)
  {
    if (paramCodeSource == untrustedCS)
      return Boolean.FALSE;
    boolean bool = this.authenticatedCS.contains(paramCodeSource);
    if (bool)
      return Boolean.TRUE;
    if (!this.unauthenticatedCS.contains(paramCodeSource))
    {
      Iterator localIterator = this.trustedCS.iterator();
      while (localIterator.hasNext())
      {
        CodeSource localCodeSource1 = (CodeSource)localIterator.next();
        CodeSource localCodeSource2;
        if (Config.isJavaVersionAtLeast15())
          localCodeSource2 = new CodeSource(paramCodeSource.getLocation(), localCodeSource1.getCodeSigners());
        else
          localCodeSource2 = new CodeSource(paramCodeSource.getLocation(), localCodeSource1.getCertificates());
        if (localCodeSource2.equals(paramCodeSource))
        {
          this.authenticatedCS.add(paramCodeSource);
          return Boolean.TRUE;
        }
        this.unauthenticatedCS.add(paramCodeSource);
      }
    }
    return Boolean.FALSE;
  }

  private synchronized CodeSource getDefaultCodeSource(URL paramURL)
  {
    if (!this.trustedCS.isEmpty())
    {
      CodeSource localCodeSource = (CodeSource)this.defaultCS.get(paramURL);
      if (localCodeSource == null)
      {
        localCodeSource = new CodeSource(paramURL, (Certificate[])null);
        this.defaultCS.put(paramURL, localCodeSource);
      }
      return localCodeSource;
    }
    return untrustedCS;
  }

  private static int showMixedTrustDialog()
  {
    if (NativeMixedCodeDialog.isSupported())
    {
      String str1 = ResourceManager.getString("security.dialog.nativemixcode.title");
      String str2 = ResourceManager.getString("security.dialog.nativemixcode.masthead");
      String str3 = ResourceManager.getString("security.dialog.nativemixcode.message");
      String str4 = ResourceManager.getString("security.dialog.nativemixcode.info");
      String str5 = ResourceManager.getString("security.dialog.nativemixcode.blockBtnStr");
      String str6 = ResourceManager.getString("security.dialog.nativemixcode.dontBlockBtnStr");
      String str7 = ResourceManager.getString("security.dialog.nativemixcode.helpBtnStr");
      String str8 = ResourceManager.getString("security.dialog.nativemixcode.closeBtnStr");
      String str9 = ResourceManager.getString("security.dialog.nativemixcode.helpTitle");
      String str10 = ResourceManager.getString("security.dialog.nativemixcode.helpMessage");
      String str11 = ResourceManager.getString("security.dialog.nativemixcode.appLabelStr");
      AppInfo localAppInfo = new AppInfo();
      return NativeMixedCodeDialog.show(str1, str2, str3, str4, str5, str6, str7, str8, str9, str10, str11, localAppInfo.getTitle());
    }
    ToolkitStore.getUI();
    return ToolkitStore.getUI().showMessageDialog(null, null, 4, ResourceManager.getString("security.dialog.mixcode.title"), ResourceManager.getString("security.dialog.mixcode.header"), ResourceManager.getString("security.dialog.mixcode.question"), ResourceManager.getString("security.dialog.mixcode.alert"), ResourceManager.getString("security.dialog.mixcode.buttonYes"), ResourceManager.getString("security.dialog.mixcode.buttonNo"), null);
  }

  private class ChildCallback extends DeployURLClassPathCallback
  {
    private ChildCallback()
    {
    }

    public DeployURLClassPathCallback.Element openClassPathElement(JarFile paramJarFile, URL paramURL)
      throws IOException
    {
      CPCallbackHandler.ChildElement localChildElement = new CPCallbackHandler.ChildElement(CPCallbackHandler.this, paramJarFile, paramURL);
      if (CPCallbackHandler.this.childURLs.contains(paramURL))
      {
        if (paramJarFile != null)
          CPCallbackHandler.jarAccess.setEagerValidation(paramJarFile, true);
        return localChildElement;
      }
      localChildElement.skip(true);
      return localChildElement;
    }

    public DeployURLClassPathCallback.Element openClassPathElement(URL paramURL)
      throws IOException
    {
      return openClassPathElement(null, paramURL);
    }

    ChildCallback(CPCallbackHandler.1 arg2)
    {
      this();
    }
  }

  private class ChildElement extends DeployURLClassPathCallback.Element
  {
    boolean skip;
    Boolean trusted;
    Boolean authenticated;
    CodeSource cs;

    ChildElement(JarFile paramURL, URL arg3)
    {
      super(localURL);
      if (paramURL != null)
      {
        CodeSource[] arrayOfCodeSource = CPCallbackHandler.jarAccess.getCodeSources(paramURL, localURL);
        this.cs = ((arrayOfCodeSource != null) && (arrayOfCodeSource.length > 0) ? arrayOfCodeSource[0] : null);
      }
      else
      {
        this.cs = CPCallbackHandler.this.getDefaultCodeSource(localURL);
      }
      this.trusted = CPCallbackHandler.this.isTrusted(this.cs);
      if ((!this.trusted.booleanValue()) && (this.cs.getCertificates() != null))
        this.authenticated = CPCallbackHandler.this.isAuthenticated(this.cs);
      else
        this.authenticated = Boolean.FALSE;
    }

    public void checkResource(String paramString)
    {
      String str1 = null;
      if ((paramString == null) || (paramString.endsWith("/")))
        return;
      CodeSource localCodeSource;
      Boolean localBoolean1;
      Boolean localBoolean3;
      Boolean localBoolean2;
      if (this.jar != null)
      {
        localCodeSource = CPCallbackHandler.jarAccess.getCodeSource(this.jar, this.url, paramString);
        localBoolean1 = localCodeSource == this.cs ? this.trusted : CPCallbackHandler.this.isTrusted(localCodeSource);
        localBoolean3 = this.authenticated.booleanValue() ? this.authenticated : CPCallbackHandler.this.isAuthenticated(localCodeSource);
        if ((!localBoolean1.booleanValue()) && (this.trusted.booleanValue()) && (paramString.startsWith("META-INF/")))
          localBoolean2 = Boolean.TRUE;
        else
          localBoolean2 = localBoolean1;
      }
      else
      {
        localCodeSource = this.cs;
        localBoolean1 = this.trusted;
        localBoolean2 = localBoolean1;
        localBoolean3 = this.authenticated;
      }
      CPCallbackHandler.ParentCallback.access$200((CPCallbackHandler.ParentCallback)CPCallbackHandler.this.pcb, this.url, localBoolean2.booleanValue(), localBoolean3.booleanValue());
      if (paramString.endsWith(".class"))
      {
        str1 = paramString.replace('/', '.');
        str1 = CPCallbackHandler.this.getPackage(str1.substring(0, str1.length() - 6));
      }
      String str2;
      if (str1 != null)
      {
        if (!CPCallbackHandler.this.checkPackage(str1, localCodeSource, localBoolean1))
        {
          str2 = paramString.replace('/', '.').substring(0, paramString.length() - 6);
          String str3 = "class \"" + str2 + "\" does not match trust level of other classes in the same package";
          throw new SecurityException(str3);
        }
      }
      else if (!CPCallbackHandler.this.checkResource(paramString, localCodeSource, localBoolean1))
      {
        str2 = "resource \"" + paramString + "\" does not match trust level of other resources of the same name";
        throw new SecurityException(str2);
      }
    }

    void skip(boolean paramBoolean)
    {
      this.skip = paramBoolean;
    }

    public boolean skip()
    {
      return this.skip;
    }
  }

  private class ParentCallback extends DeployURLClassPathCallback
  {
    private boolean trustedChild;
    private boolean authenticatedChild;
    private boolean untrustedChild;
    private boolean trustedOnly;
    private boolean allowMixedTrust;
    private boolean checkMixedTrust;

    private ParentCallback()
    {
      if (Config.getMixcodeValue() == 0)
        this.checkMixedTrust = true;
      if ((!this.checkMixedTrust) && (Config.getMixcodeValue() == 1))
        this.allowMixedTrust = true;
    }

    public synchronized DeployURLClassPathCallback.Element openClassPathElement(JarFile paramJarFile, URL paramURL)
      throws IOException
    {
      CPCallbackHandler.jarAccess.setEagerValidation(paramJarFile, true);
      return strategy(paramJarFile, paramURL, CPCallbackHandler.jarAccess.getCodeSources(paramJarFile, paramURL));
    }

    public synchronized DeployURLClassPathCallback.Element openClassPathElement(URL paramURL)
      throws IOException
    {
      return strategy(null, paramURL, new CodeSource[] { new CodeSource(paramURL, (Certificate[])null) });
    }

    private DeployURLClassPathCallback.Element strategy(JarFile paramJarFile, URL paramURL, CodeSource[] paramArrayOfCodeSource)
    {
      int i = 0;
      int j = 0;
      boolean bool1 = this.trustedOnly;
      boolean bool2 = this.trustedChild;
      boolean bool3 = false;
      boolean bool4 = false;
      CPCallbackHandler.ParentElement localParentElement = new CPCallbackHandler.ParentElement(CPCallbackHandler.this, paramJarFile, paramURL);
      if (paramJarFile != null)
      {
        bool3 = CPCallbackHandler.hasTrustedLibraryAssertion(paramJarFile);
        bool4 = CPCallbackHandler.hasTrustedOnlyAssertion(paramJarFile);
      }
      if (bool4)
      {
        Trace.println(paramURL + " is asserting Trusted-Only", TraceLevel.SECURITY);
        if ((this.authenticatedChild) || (this.untrustedChild))
        {
          localParentElement.setPendingException("attempted to open Trusted-Only jar " + paramURL + " on sandboxed loader");
          return localParentElement;
        }
      }
      CodeSource[] arrayOfCodeSource1 = CPCallbackHandler.this.parent.getTrustedCodeSources(paramArrayOfCodeSource);
      if ((arrayOfCodeSource1 != null) && (arrayOfCodeSource1.length > 0))
      {
        i = 1;
        if (arrayOfCodeSource1.length == paramArrayOfCodeSource.length)
        {
          j = 1;
        }
        else
        {
          int k = 0;
          for (int m = paramArrayOfCodeSource.length - 1; m >= 0; m--)
            if (paramArrayOfCodeSource[m].getCertificates() == null)
            {
              CodeSource[] arrayOfCodeSource2 = { paramArrayOfCodeSource[m] };
              Enumeration localEnumeration = CPCallbackHandler.jarAccess.entryNames(paramJarFile, arrayOfCodeSource2);
              while (localEnumeration.hasMoreElements())
              {
                String str = (String)localEnumeration.nextElement();
                if (!str.startsWith("META-INF/"))
                {
                  k = 1;
                  break;
                }
              }
              if (k != 0)
                break;
            }
          j = k == 0 ? 1 : 0;
        }
        CPCallbackHandler.this.mergeTrustedSources(arrayOfCodeSource1);
      }
      if (j != 0)
      {
        if (bool3)
        {
          if ((this.authenticatedChild) || (this.untrustedChild))
          {
            localParentElement.setPendingException(CPCallbackHandler.this.assertTrust(paramJarFile, arrayOfCodeSource1));
            return localParentElement;
          }
          CPCallbackHandler.this.assertJars.put(paramJarFile, arrayOfCodeSource1);
          return localParentElement;
        }
        if ((bool4) && (!this.trustedOnly))
        {
          Trace.println(paramURL + " is newly asserting Trusted-Only", TraceLevel.SECURITY);
          bool1 = true;
        }
      }
      else
      {
        if (bool4)
        {
          localParentElement.setPendingException("attempted to open sandboxed jar " + paramURL + " as Trusted-Only");
          return localParentElement;
        }
        if (bool3)
        {
          localParentElement.setPendingException("attempted to open sandboxed jar " + paramURL + " as a Trusted-Library");
          return localParentElement;
        }
      }
      if ((i != 0) && (paramJarFile != null))
        bool2 = true;
      Object localObject;
      if ((bool2) && (this.untrustedChild))
      {
        localObject = checkAllowed(paramURL, (bool2) && (this.trustedChild));
        if (localObject != null)
        {
          localParentElement.setPendingException((String)localObject);
          return localParentElement;
        }
      }
      if ((this.authenticatedChild) || (this.untrustedChild))
      {
        if (!CPCallbackHandler.this.assertJars.isEmpty())
        {
          localObject = CPCallbackHandler.this.assertJars.entrySet().iterator();
          while (((Iterator)localObject).hasNext())
          {
            Map.Entry localEntry = (Map.Entry)((Iterator)localObject).next();
            CPCallbackHandler.this.assertTrust((JarFile)localEntry.getKey(), (CodeSource[])localEntry.getValue());
          }
          CPCallbackHandler.this.assertJars.clear();
        }
        if ((paramJarFile != null) && (i != 0))
        {
          localObject = CPCallbackHandler.this.assertTrust(paramJarFile, arrayOfCodeSource1);
          if (localObject != null)
          {
            localParentElement.setPendingException((String)localObject);
            return localParentElement;
          }
        }
      }
      else if ((paramJarFile != null) && (i != 0))
      {
        CPCallbackHandler.this.assertJars.put(paramJarFile, arrayOfCodeSource1);
      }
      CPCallbackHandler.this.childURLs.add(paramURL);
      this.trustedOnly = bool1;
      this.trustedChild = bool2;
      localParentElement.defer(true);
      return localParentElement;
    }

    private synchronized void check(URL paramURL, boolean paramBoolean1, boolean paramBoolean2)
    {
      boolean bool1 = this.trustedChild;
      boolean bool2 = this.untrustedChild;
      boolean bool3 = this.authenticatedChild;
      if ((!paramBoolean1) && (this.trustedOnly))
      {
        if (paramURL == null)
          throw new SecurityException("JavaScript attempted to access a resource with Trusted-Only active");
        throw new SecurityException("Trusted-Only loader attempted to load sandboxed resource from " + paramURL);
      }
      if (paramBoolean1)
        bool1 = true;
      else if (paramBoolean2)
        bool3 = true;
      else
        bool2 = true;
      Object localObject;
      if ((bool1) && (bool2))
      {
        localObject = checkAllowed(paramURL, (bool1) && (this.trustedChild));
        if (localObject != null)
          throw new SecurityException((String)localObject);
      }
      if (((bool3) || (bool2)) && (!CPCallbackHandler.this.assertJars.isEmpty()))
      {
        localObject = CPCallbackHandler.this.assertJars.entrySet().iterator();
        while (((Iterator)localObject).hasNext())
        {
          Map.Entry localEntry = (Map.Entry)((Iterator)localObject).next();
          CPCallbackHandler.this.assertTrust((JarFile)localEntry.getKey(), (CodeSource[])localEntry.getValue());
        }
        CPCallbackHandler.this.assertJars.clear();
      }
      this.trustedChild = bool1;
      this.authenticatedChild = bool3;
      this.untrustedChild = bool2;
    }

    private String checkAllowed(URL paramURL, boolean paramBoolean)
    {
      if (this.checkMixedTrust)
      {
        int i = CPCallbackHandler.access$900();
        ToolkitStore.getUI();
        if (i == 1)
          this.allowMixedTrust = true;
        this.checkMixedTrust = false;
      }
      if (!this.allowMixedTrust)
      {
        if (paramURL == null)
          return "JavaScript attempted to access a resource when trusted content exists";
        if (paramBoolean)
          return "trusted loader attempted to load sandboxed resource from " + paramURL;
        return "sandboxed loader attempted to load trusted resource from " + paramURL;
      }
      return null;
    }

    ParentCallback(CPCallbackHandler.1 arg2)
    {
      this();
    }
  }

  private class ParentElement extends DeployURLClassPathCallback.Element
  {
    String pendingException;
    boolean defer;

    ParentElement(JarFile paramURL, URL arg3)
    {
      super(localURL);
    }

    public void checkResource(String paramString)
    {
      if (this.pendingException != null)
        throw new SecurityException(this.pendingException);
      if (this.jar == null)
        throw new SecurityException("invalid class path element " + this.url + " on Trusted-Library loader");
    }

    void setPendingException(String paramString)
    {
      this.pendingException = paramString;
    }

    void defer(boolean paramBoolean)
    {
      this.defer = paramBoolean;
    }

    public boolean defer()
    {
      return this.defer;
    }

    public String toString()
    {
      return "defer: " + this.defer + ", pending: " + this.pendingException;
    }
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.security.CPCallbackHandler
 * JD-Core Version:    0.6.2
 */