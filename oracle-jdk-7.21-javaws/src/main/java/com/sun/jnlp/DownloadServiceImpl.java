package com.sun.jnlp;

import com.sun.applet2.preloader.CancelException;
import com.sun.applet2.preloader.Preloader;
import com.sun.applet2.preloader.event.ConfigEvent;
import com.sun.applet2.preloader.event.InitEvent;
import com.sun.deploy.cache.Cache;
import com.sun.deploy.model.Resource;
import com.sun.deploy.model.ResourceProvider;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import com.sun.deploy.uitoolkit.ToolkitStore;
import com.sun.deploy.uitoolkit.UIToolkit;
import com.sun.javaws.CacheUtil;
import com.sun.javaws.LaunchDownload;
import com.sun.javaws.jnl.ExtensionDesc;
import com.sun.javaws.jnl.JARDesc;
import com.sun.javaws.jnl.LaunchDesc;
import com.sun.javaws.jnl.LaunchDescFactory;
import com.sun.javaws.jnl.ResourceVisitor;
import com.sun.javaws.jnl.ResourcesDesc;
import com.sun.javaws.progress.CustomProgress2PreloaderAdapter;
import com.sun.javaws.progress.PreloaderDelegate;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;
import java.util.jar.JarFile;
import javax.jnlp.DownloadService;
import javax.jnlp.DownloadServiceListener;

public final class DownloadServiceImpl
  implements DownloadService
{
  private static DownloadServiceImpl _sharedInstance = null;
  private DownloadServiceListener _defaultProgressHelper = null;

  static synchronized void reset()
  {
    _sharedInstance = null;
  }

  public static synchronized DownloadServiceImpl getInstance()
  {
    initialize();
    return _sharedInstance;
  }

  public static synchronized void initialize()
  {
    if (_sharedInstance == null)
      _sharedInstance = new DownloadServiceImpl();
  }

  public DownloadServiceListener getDefaultProgressWindow()
  {
    if (this._defaultProgressHelper == null)
      this._defaultProgressHelper = ((DownloadServiceListener)AccessController.doPrivileged(new PrivilegedAction()
      {
        public Object run()
        {
          Preloader localPreloader = ToolkitStore.get().getDefaultPreloader();
          try
          {
            localPreloader.handleEvent(new ConfigEvent(3, JNLPClassLoaderUtil.getInstance().getLaunchDesc().getAppInfo()));
            localPreloader.handleEvent(new InitEvent(1));
          }
          catch (CancelException localCancelException)
          {
            Trace.ignoredException(localCancelException);
          }
          return new PreloaderDelegate(localPreloader);
        }
      }));
    return this._defaultProgressHelper;
  }

  public boolean isResourceCached(URL paramURL, String paramString)
  {
    Boolean localBoolean = (Boolean)AccessController.doPrivileged(new PrivilegedAction()
    {
      private final URL val$ref;
      private final String val$version;

      public Object run()
      {
        if ((DownloadServiceImpl.this.isResourceValid(this.val$ref, this.val$version)) && (ResourceProvider.get().isCached(this.val$ref, this.val$version)))
          return Boolean.TRUE;
        return Boolean.FALSE;
      }
    });
    return localBoolean.booleanValue();
  }

  public boolean isPartCached(String paramString)
  {
    return isPartCached(new String[] { paramString });
  }

  public boolean isPartCached(String[] paramArrayOfString)
  {
    Boolean localBoolean = (Boolean)AccessController.doPrivileged(new PrivilegedAction()
    {
      private final String[] val$parts;

      public Object run()
      {
        LaunchDesc localLaunchDesc = JNLPClassLoaderUtil.getInstance().getLaunchDesc();
        ResourcesDesc localResourcesDesc = localLaunchDesc.getResources();
        if (localResourcesDesc == null)
          return Boolean.FALSE;
        JARDesc[] arrayOfJARDesc = localResourcesDesc.getPartJars(this.val$parts);
        return new Boolean(DownloadServiceImpl.this.isJARInCache(arrayOfJARDesc, true));
      }
    });
    return localBoolean.booleanValue();
  }

  public boolean isExtensionPartCached(URL paramURL, String paramString1, String paramString2)
  {
    return isExtensionPartCached(paramURL, paramString1, new String[] { paramString2 });
  }

  public boolean isExtensionPartCached(URL paramURL, String paramString, String[] paramArrayOfString)
  {
    Boolean localBoolean = (Boolean)AccessController.doPrivileged(new PrivilegedAction()
    {
      private final URL val$ref;
      private final String val$version;
      private final String[] val$parts;

      public Object run()
      {
        LaunchDesc localLaunchDesc = JNLPClassLoaderUtil.getInstance().getLaunchDesc();
        ResourcesDesc localResourcesDesc = localLaunchDesc.getResources();
        if (localResourcesDesc == null)
          return Boolean.FALSE;
        JARDesc[] arrayOfJARDesc = localResourcesDesc.getExtensionPart(this.val$ref, this.val$version, this.val$parts);
        return new Boolean(DownloadServiceImpl.this.isJARInCache(arrayOfJARDesc, true));
      }
    });
    return localBoolean.booleanValue();
  }

  public void loadResource(URL paramURL, String paramString, DownloadServiceListener paramDownloadServiceListener)
    throws IOException
  {
    Trace.println(getClass().getName() + ".loadResource(" + paramURL + "," + paramDownloadServiceListener.getClass().getName() + ")");
    if (isResourceValid(paramURL, paramString))
      try
      {
        AccessController.doPrivileged(new PrivilegedExceptionAction()
        {
          private final DownloadServiceListener val$progress;
          private final URL val$ref;
          private final String val$version;

          public Object run()
            throws IOException
          {
            CustomProgress2PreloaderAdapter localCustomProgress2PreloaderAdapter = new CustomProgress2PreloaderAdapter(this.val$progress);
            PreloaderDelegate localPreloaderDelegate = DownloadServiceImpl.this.getProgressHelper(localCustomProgress2PreloaderAdapter);
            int i = ResourceProvider.get().incrementInternalUse();
            try
            {
              Object localObject1;
              if (this.val$ref.toString().endsWith(".jar"))
              {
                localObject1 = JNLPClassLoaderUtil.getInstance();
                ((JNLPClassLoaderIf)localObject1).addResource(this.val$ref, this.val$version, null);
                if (!DownloadServiceImpl.this.isResourceCached(this.val$ref, this.val$version))
                  LaunchDownload.downloadResource(((JNLPClassLoaderIf)localObject1).getLaunchDesc(), this.val$ref, this.val$version, localPreloaderDelegate, true);
              }
              else
              {
                localObject1 = ResourceProvider.get().getResource(this.val$ref, this.val$version);
                if (((Resource)localObject1).isJNLPFile())
                  DownloadServiceImpl.this.loadResourceRecursivly((Resource)localObject1, this.val$progress);
              }
            }
            catch (Exception localException)
            {
              throw new IOException(localException.getMessage());
            }
            finally
            {
              localPreloaderDelegate.forceFlushForTCK();
              ResourceProvider.get().decrementInternalUse(i);
            }
            return null;
          }
        });
      }
      catch (PrivilegedActionException localPrivilegedActionException)
      {
        throw ((IOException)localPrivilegedActionException.getException());
      }
  }

  private void loadResourceRecursivly(Resource paramResource, DownloadServiceListener paramDownloadServiceListener)
  {
    try
    {
      File localFile = new File(paramResource.getResourceFilename());
      URL localURL = new URL(paramResource.getURL());
      LaunchDesc localLaunchDesc = LaunchDescFactory.buildDescriptor(localFile, null, null, localURL);
      ResourcesDesc localResourcesDesc = localLaunchDesc.getResources();
      if (localResourcesDesc != null)
        localResourcesDesc.visit(new ResourceVisitor()
        {
          private final DownloadServiceListener val$progress;

          public void visitJARDesc(JARDesc paramAnonymousJARDesc)
          {
            try
            {
              DownloadServiceImpl.this.loadResource(paramAnonymousJARDesc.getLocation(), paramAnonymousJARDesc.getVersion(), this.val$progress);
            }
            catch (IOException localIOException)
            {
              Trace.ignored(localIOException);
            }
          }

          public void visitExtensionDesc(ExtensionDesc paramAnonymousExtensionDesc)
          {
            try
            {
              DownloadServiceImpl.this.loadResource(paramAnonymousExtensionDesc.getLocation(), paramAnonymousExtensionDesc.getVersion(), this.val$progress);
            }
            catch (IOException localIOException)
            {
              Trace.ignored(localIOException);
            }
          }
        });
    }
    catch (Exception localException)
    {
      Trace.ignored(localException);
    }
  }

  public void loadPart(String paramString, DownloadServiceListener paramDownloadServiceListener)
    throws IOException
  {
    loadPart(new String[] { paramString }, paramDownloadServiceListener);
  }

  public void loadPart(String[] paramArrayOfString, DownloadServiceListener paramDownloadServiceListener)
    throws IOException
  {
    Trace.println(getClass().getName() + ".loadPart(" + Arrays.asList(paramArrayOfString) + "," + paramDownloadServiceListener.getClass().getName() + ")", TraceLevel.TEMP);
    if (isPartCached(paramArrayOfString))
      return;
    try
    {
      AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        private final DownloadServiceListener val$progress;
        private final String[] val$parts;

        public Object run()
          throws IOException
        {
          CustomProgress2PreloaderAdapter localCustomProgress2PreloaderAdapter = new CustomProgress2PreloaderAdapter(this.val$progress);
          PreloaderDelegate localPreloaderDelegate = DownloadServiceImpl.this.getProgressHelper(localCustomProgress2PreloaderAdapter);
          try
          {
            LaunchDownload.downloadParts(JNLPClassLoaderUtil.getInstance().getLaunchDesc(), this.val$parts, localPreloaderDelegate, true);
          }
          catch (Exception localException)
          {
            throw new IOException(localException.getMessage());
          }
          finally
          {
            localPreloaderDelegate.forceFlushForTCK();
          }
          return null;
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw ((IOException)localPrivilegedActionException.getException());
    }
  }

  public void loadExtensionPart(URL paramURL, String paramString1, String paramString2, DownloadServiceListener paramDownloadServiceListener)
    throws IOException
  {
    loadExtensionPart(paramURL, paramString1, new String[] { paramString2 }, paramDownloadServiceListener);
  }

  public void loadExtensionPart(URL paramURL, String paramString, String[] paramArrayOfString, DownloadServiceListener paramDownloadServiceListener)
    throws IOException
  {
    try
    {
      Trace.println(getClass().getName() + ".loadExtensionPart(" + Arrays.asList(paramArrayOfString) + "," + paramDownloadServiceListener.getClass().getName() + ")");
      AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        private final DownloadServiceListener val$progress;
        private final URL val$ref;
        private final String val$version;
        private final String[] val$parts;

        public Object run()
          throws IOException
        {
          CustomProgress2PreloaderAdapter localCustomProgress2PreloaderAdapter = new CustomProgress2PreloaderAdapter(this.val$progress);
          PreloaderDelegate localPreloaderDelegate = DownloadServiceImpl.this.getProgressHelper(localCustomProgress2PreloaderAdapter);
          try
          {
            LaunchDownload.downloadExtensionPart(JNLPClassLoaderUtil.getInstance().getLaunchDesc(), this.val$ref, this.val$version, this.val$parts, localPreloaderDelegate, true);
          }
          catch (Exception localException)
          {
            throw new IOException(localException.getMessage());
          }
          finally
          {
            localPreloaderDelegate.forceFlushForTCK();
          }
          return null;
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw ((IOException)localPrivilegedActionException.getException());
    }
  }

  public void removeResource(URL paramURL, String paramString)
    throws IOException
  {
    try
    {
      AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        private final URL val$ref;
        private final String val$version;

        public Object run()
          throws IOException
        {
          if (DownloadServiceImpl.this.isResourceValid(this.val$ref, this.val$version))
          {
            Resource localResource = ResourceProvider.get().getResource(this.val$ref, this.val$version);
            if (this.val$ref.toString().endsWith("jnlp"))
              CacheUtil.remove(Cache.getCacheEntry(this.val$ref, this.val$version));
            ResourceProvider.get().markRetired(localResource, true);
          }
          return null;
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw ((IOException)localPrivilegedActionException.getException());
    }
  }

  public void removePart(String paramString)
    throws IOException
  {
    removePart(new String[] { paramString });
  }

  public void removePart(String[] paramArrayOfString)
    throws IOException
  {
    try
    {
      AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        private final String[] val$parts;

        public Object run()
          throws IOException
        {
          LaunchDesc localLaunchDesc = JNLPClassLoaderUtil.getInstance().getLaunchDesc();
          ResourcesDesc localResourcesDesc = localLaunchDesc.getResources();
          if (localResourcesDesc == null)
            return null;
          JARDesc[] arrayOfJARDesc = localResourcesDesc.getPartJars(this.val$parts);
          DownloadServiceImpl.this.removeJARFromCache(arrayOfJARDesc);
          return null;
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw ((IOException)localPrivilegedActionException.getException());
    }
  }

  public void removeExtensionPart(URL paramURL, String paramString1, String paramString2)
    throws IOException
  {
    removeExtensionPart(paramURL, paramString1, new String[] { paramString2 });
  }

  public void removeExtensionPart(URL paramURL, String paramString, String[] paramArrayOfString)
    throws IOException
  {
    try
    {
      AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        private final URL val$ref;
        private final String val$version;
        private final String[] val$parts;

        public Object run()
          throws IOException
        {
          LaunchDesc localLaunchDesc = JNLPClassLoaderUtil.getInstance().getLaunchDesc();
          ResourcesDesc localResourcesDesc = localLaunchDesc.getResources();
          if (localResourcesDesc == null)
            return null;
          JARDesc[] arrayOfJARDesc = localResourcesDesc.getExtensionPart(this.val$ref, this.val$version, this.val$parts);
          DownloadServiceImpl.this.removeJARFromCache(arrayOfJARDesc);
          return null;
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw ((IOException)localPrivilegedActionException.getException());
    }
  }

  private void removeJARFromCache(JARDesc[] paramArrayOfJARDesc)
    throws IOException
  {
    if (paramArrayOfJARDesc == null)
      return;
    if (paramArrayOfJARDesc.length == 0)
      return;
    for (int i = 0; i < paramArrayOfJARDesc.length; i++)
    {
      Resource localResource = ResourceProvider.get().getCachedResource(paramArrayOfJARDesc[i].getLocation(), paramArrayOfJARDesc[i].getVersion());
      ResourceProvider.get().markRetired(localResource, true);
    }
  }

  private boolean isJARInCache(JARDesc[] paramArrayOfJARDesc, boolean paramBoolean)
  {
    if (paramArrayOfJARDesc == null)
      return false;
    if (paramArrayOfJARDesc.length == 0)
      return false;
    boolean bool = true;
    for (int i = 0; i < paramArrayOfJARDesc.length; i++)
    {
      JarFile localJarFile = ResourceProvider.get().getCachedJarFile(paramArrayOfJARDesc[i].getLocation(), paramArrayOfJARDesc[i].getVersion());
      if (localJarFile != null)
      {
        if (!paramBoolean)
          return true;
      }
      else
        bool = false;
    }
    return bool;
  }

  private boolean isResourceValid(URL paramURL, String paramString)
  {
    LaunchDesc localLaunchDesc = JNLPClassLoaderUtil.getInstance().getLaunchDesc();
    JARDesc[] arrayOfJARDesc = localLaunchDesc.getResources().getEagerOrAllJarDescs(true);
    if (localLaunchDesc.getSecurityModel() != 0)
      return true;
    for (int i = 0; i < arrayOfJARDesc.length; i++)
      if ((paramURL.toString().equals(arrayOfJARDesc[i].getLocation().toString())) && ((paramString == null) || (paramString.equals(arrayOfJARDesc[i].getVersion()))))
        return true;
    URL localURL = localLaunchDesc.getCodebase();
    return (localURL != null) && (paramURL != null) && (paramURL.toString().startsWith(localURL.toString()));
  }

  private PreloaderDelegate getProgressHelper(CustomProgress2PreloaderAdapter paramCustomProgress2PreloaderAdapter)
  {
    return new PreloaderDelegate(paramCustomProgress2PreloaderAdapter);
  }
}

/* Location:           /home/user1/Temp/jvm/javaws.jar
 * Qualified Name:     com.sun.jnlp.DownloadServiceImpl
 * JD-Core Version:    0.6.2
 */