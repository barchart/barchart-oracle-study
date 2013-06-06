package com.sun.deploy.uitoolkit.impl.awt;

import com.sun.applet2.Applet2;
import com.sun.applet2.Applet2Context;
import com.sun.applet2.Applet2Host;
import com.sun.applet2.preloader.Preloader;
import com.sun.deploy.Environment;
import com.sun.deploy.appcontext.AppContext;
import com.sun.deploy.config.Config;
import com.sun.deploy.config.Platform;
import com.sun.deploy.net.JARSigningException;
import com.sun.deploy.perf.DeployPerfUtil;
import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import com.sun.deploy.uitoolkit.Applet2Adapter;
import com.sun.deploy.uitoolkit.ToolkitStore;
import com.sun.deploy.util.ReflectionUtil;
import com.sun.deploy.util.SystemUtils;
import com.sun.javaws.exceptions.ExitException;
import com.sun.javaws.progress.CustomProgress2PreloaderAdapter;
import com.sun.javaws.ui.LaunchErrorDialog;
import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.applet.AudioClip;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FocusTraversalPolicy;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.StreamCorruptedException;
import java.lang.ref.SoftReference;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.jnlp.DownloadServiceListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.RepaintManager;
import netscape.javascript.JSObject;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.ComponentAccessor;
import sun.awt.AWTAccessor.ContainerAccessor;
import sun.awt.EmbeddedFrame;
import sun.misc.Resource;
import sun.plugin.javascript.JSContext;
import sun.plugin2.applet.Applet2IllegalArgumentException;
import sun.plugin2.applet.Plugin2ClassLoader;
import sun.plugin2.applet2.Plugin2Host;
import sun.plugin2.util.ColorUtil;
import sun.plugin2.util.ColorUtil.ColorRGB;

public class AWTAppletAdapter extends Applet2Adapter
{
  private AppContext appletAppContext = null;
  private AppletStubImpl stub = null;
  private AppletContextImpl context = null;
  private Container parentContainer = null;
  Applet applet = null;
  private static final String APPCONTEXT_JAPPLET_USED_KEY = "JAppletUsedKey";
  private Container progressContainer = null;
  private Preloader preloader = null;
  Boolean jdk11Applet = null;
  Boolean jdk12Applet = null;

  public AWTAppletAdapter(Applet2Context paramApplet2Context)
  {
    super(paramApplet2Context);
    ToolkitStore.get();
    this.context = new AppletContextImpl(getApplet2Context(), null);
    this.stub = new AppletStubImpl(getApplet2Context());
  }

  public void setAppletAppContext(AppContext paramAppContext)
  {
    this.appletAppContext = paramAppContext;
    this.appletAppContext.put("AppletContextKey", this.context);
  }

  public synchronized void setParentContainer(com.sun.deploy.uitoolkit.Window paramWindow)
  {
    if (null != paramWindow)
    {
      assert ((paramWindow instanceof AWTFrameWindow));
      this.parentContainer = ((Container)paramWindow.getWindowObject());
    }
  }

  public synchronized void instantiateApplet(final Class paramClass)
    throws InstantiationException, IllegalAccessException
  {
    if (this.parentContainer == null)
      Trace.println("Instantiating applet without parent container!", TraceLevel.UI);
    final Applet[] arrayOfApplet = new Applet[1];
    final Error[] arrayOfError = new Error[1];
    arrayOfError[0] = null;
    Runnable local1 = new Runnable()
    {
      private final Class val$cls;
      private final Applet[] val$appletBox;
      private final Error[] val$errorBox;

      public void run()
      {
        try
        {
          Applet localApplet = (Applet)paramClass.newInstance();
          localApplet.setVisible(false);
          arrayOfApplet[0] = localApplet;
        }
        catch (InstantiationException localInstantiationException)
        {
          throw new RuntimeException(localInstantiationException);
        }
        catch (IllegalAccessException localIllegalAccessException)
        {
          throw new RuntimeException(localIllegalAccessException);
        }
        catch (Error localError)
        {
          arrayOfError[0] = localError;
        }
      }
    };
    if (this.parentContainer != null)
    {
      runOnEDTAndWait(this.parentContainer, local1);
      if (null != arrayOfError[0])
        throw new RuntimeException(arrayOfError[0]);
    }
    else
    {
      local1.run();
    }
    Applet localApplet = arrayOfApplet[0];
    setApplet(localApplet);
  }

  public void cleanup()
  {
    super.cleanup();
    if ((this.appletAppContext != null) && (this.appletAppContext.get("JAppletUsedKey") != null))
      RepaintManager.setCurrentManager(null);
  }

  private void setApplet(Applet paramApplet)
  {
    this.applet = paramApplet;
    if (paramApplet == null)
      Trace.ignored(new Exception("setApplet with null applet!"));
    if (this.parentContainer == null)
      Trace.println("Adapting applet without parent container!", TraceLevel.UI);
    if (paramApplet != null)
      findAppletJDKLevel(paramApplet.getClass());
    if (ReflectionUtil.isSubclassOf(paramApplet, "javax.swing.JApplet"))
    {
      AppContext localAppContext = this.appletAppContext;
      if (localAppContext != null)
        localAppContext.put("JAppletUsedKey", Boolean.TRUE);
    }
    if (paramApplet != null)
      paramApplet.setStub(this.stub);
  }

  public Applet2 getApplet2()
  {
    return null;
  }

  private void doClearParentContainer()
  {
    if (ReflectionUtil.instanceOf(this.parentContainer, "javax.swing.JFrame"))
      ((JFrame)this.parentContainer).getContentPane().removeAll();
    else if (this.parentContainer != null)
      this.parentContainer.removeAll();
  }

  public void doShowApplet()
  {
    Applet2Context localApplet2Context = getApplet2Context();
    final Dimension localDimension = new Dimension(localApplet2Context.getWidth(), localApplet2Context.getHeight());
    runOnEDTAndWait(this.applet, new Runnable()
    {
      private final Dimension val$size;

      public void run()
      {
        if ((AWTAppletAdapter.this.applet.getParent() != null) && (AWTAppletAdapter.this.applet.isVisible()))
          return;
        AWTAppletAdapter.this.applet.resize(localDimension);
        try
        {
          AWTAppletAdapter.this.doClearParentContainer();
          if (AWTAppletAdapter.this.parentContainer != null)
            AWTAppletAdapter.this.parentContainer.add(AWTAppletAdapter.this.applet, "Center");
        }
        catch (Error localError)
        {
          if ((!Config.isJavaVersionAtLeast15()) && ((AWTAppletAdapter.this.parentContainer instanceof JFrame)))
            ((JFrame)AWTAppletAdapter.this.parentContainer).getContentPane().add(AWTAppletAdapter.this.applet, "Center");
          else
            throw localError;
        }
        finally
        {
          DeployPerfUtil.put("Done switching from progress to Applet");
        }
      }
    });
  }

  public Object getStubObject()
  {
    return this.stub;
  }

  public void init()
  {
    markAlive(true);
    try
    {
      this.applet.init();
    }
    catch (Throwable localThrowable)
    {
      runOnEDT(this.parentContainer, new Runnable()
      {
        public void run()
        {
          if (AWTAppletAdapter.this.parentContainer != null)
            AWTAppletAdapter.this.parentContainer.remove(AWTAppletAdapter.this.applet);
        }
      });
      throw new RuntimeException(localThrowable);
    }
  }

  private void showApplet()
  {
    runOnEDT(this.applet, new Runnable()
    {
      public void run()
      {
        try
        {
          AWTAccessor.getContainerAccessor().validateUnconditionally(AWTAppletAdapter.this.parentContainer);
        }
        catch (Throwable localThrowable)
        {
          if (AWTAppletAdapter.this.parentContainer != null)
            AWTAppletAdapter.this.parentContainer.validate();
          AWTAppletAdapter.this.applet.validate();
        }
        AWTAppletAdapter.this.applet.setVisible(true);
        if (AWTAppletAdapter.this.hasInitialFocus())
          AWTAppletAdapter.this.setDefaultFocus();
      }
    });
  }

  public void start()
  {
    markAlive(true);
    try
    {
      showApplet();
      this.applet.start();
      runOnEDT(this.applet, new Runnable()
      {
        public void run()
        {
          try
          {
            AWTAccessor.getContainerAccessor().validateUnconditionally(AWTAppletAdapter.this.parentContainer);
          }
          catch (Throwable localThrowable)
          {
            if (AWTAppletAdapter.this.parentContainer != null)
            {
              AWTAppletAdapter.this.parentContainer.invalidate();
              AWTAppletAdapter.this.parentContainer.validate();
            }
          }
        }
      });
    }
    catch (Throwable localThrowable)
    {
      if (this.parentContainer != null)
        runOnEDT(this.parentContainer, new Runnable()
        {
          public void run()
          {
            AWTAppletAdapter.this.parentContainer.remove(AWTAppletAdapter.this.applet);
          }
        });
      throw new RuntimeException(localThrowable);
    }
  }

  public void stop()
  {
    runOnEDT(this.applet, new Runnable()
    {
      public void run()
      {
        AWTAppletAdapter.this.applet.setVisible(false);
      }
    });
    try
    {
      this.applet.stop();
    }
    finally
    {
      markAlive(false);
    }
  }

  public void destroy()
  {
    this.applet.destroy();
  }

  public void resize(final int paramInt1, final int paramInt2)
  {
    if (this.applet == null)
      return;
    runOnEDT(this.applet, new Runnable()
    {
      private final int val$width;
      private final int val$height;

      public void run()
      {
        if (AWTAppletAdapter.this.applet == null)
          return;
        AWTAppletAdapter.this.applet.resize(paramInt1, paramInt2);
        AWTAppletAdapter.this.applet.validate();
      }
    });
  }

  public void doClearAppletArea()
  {
    runOnEDT(this.parentContainer, new Runnable()
    {
      public void run()
      {
        AWTAppletAdapter.this.doClearParentContainer();
      }
    });
  }

  private void doContainerPrivileged(Container paramContainer, final Runnable paramRunnable)
  {
    AccessControlContext localAccessControlContext = AWTAccessor.getComponentAccessor().getAccessControlContext(paramContainer);
    AccessController.doPrivileged(new PrivilegedAction()
    {
      private final Runnable val$runnable;

      public Object run()
      {
        paramRunnable.run();
        return null;
      }
    }
    , localAccessControlContext);
  }

  private void runOnEDT(Container paramContainer, Runnable paramRunnable)
  {
    assert (paramContainer != null);
    if (EventQueue.isDispatchThread())
      doContainerPrivileged(paramContainer, paramRunnable);
    else
      OldPluginAWTUtil.invokeLater(paramContainer, paramRunnable);
  }

  private void runOnEDTAndWait(Container paramContainer, Runnable paramRunnable)
  {
    assert (paramContainer != null);
    if (EventQueue.isDispatchThread())
      doContainerPrivileged(paramContainer, paramRunnable);
    else
      try
      {
        OldPluginAWTUtil.invokeAndWait(paramContainer, paramRunnable);
      }
      catch (InterruptedException localInterruptedException)
      {
        Trace.ignoredException(localInterruptedException);
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        throw new RuntimeException(localInvocationTargetException);
      }
  }

  private void setDefaultFocus()
  {
    Component localComponent = null;
    if (this.parentContainer != null)
      if ((this.parentContainer instanceof java.awt.Window))
      {
        localComponent = getMostRecentFocusOwnerForWindow((java.awt.Window)this.parentContainer);
        if ((localComponent == this.parentContainer) || (localComponent == null))
          localComponent = this.parentContainer.getFocusTraversalPolicy().getInitialComponent((java.awt.Window)this.parentContainer);
      }
      else if (this.parentContainer.isFocusCycleRoot())
      {
        localComponent = this.parentContainer.getFocusTraversalPolicy().getDefaultComponent(this.parentContainer);
      }
    if (localComponent != null)
    {
      if ((this.parentContainer instanceof EmbeddedFrame))
        try
        {
          ((EmbeddedFrame)this.parentContainer).synthesizeWindowActivation(true);
        }
        catch (NoSuchMethodError localNoSuchMethodError)
        {
        }
        catch (Throwable localThrowable)
        {
          Trace.ignored(localThrowable);
        }
      localComponent.requestFocusInWindow();
    }
  }

  private Component getMostRecentFocusOwnerForWindow(java.awt.Window paramWindow)
  {
    Method localMethod = (Method)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        Method localMethod = null;
        try
        {
          localMethod = AWTAppletAdapter.class$java$awt$KeyboardFocusManager.getDeclaredMethod("getMostRecentFocusOwner", new Class[] { java.awt.Window.class });
          localMethod.setAccessible(true);
        }
        catch (Exception localException)
        {
          Trace.ignored(localException);
        }
        return localMethod;
      }
    });
    if (localMethod != null)
      try
      {
        return (Component)localMethod.invoke(null, new Object[] { paramWindow });
      }
      catch (Exception localException)
      {
        Trace.ignored(localException);
      }
    return paramWindow.getMostRecentFocusOwner();
  }

  private Container getSurfaceObject()
  {
    if (this.progressContainer == null)
    {
      AppContext localAppContext = this.appletAppContext;
      final Object[] arrayOfObject = new Object[1];
      if (localAppContext != null)
        try
        {
          localAppContext.invokeAndWait(new Runnable()
          {
            private final Object[] val$results;

            public void run()
            {
              arrayOfObject[0] = new JPanel(new BorderLayout());
            }
          });
          this.progressContainer = ((Container)arrayOfObject[0]);
        }
        catch (Exception localException)
        {
          Trace.ignored(localException);
        }
    }
    return this.progressContainer;
  }

  private void installProgressContainer()
  {
    final Container localContainer1 = this.parentContainer;
    Trace.println("install: " + localContainer1 + " " + this.progressContainer, TraceLevel.PRELOADER);
    if (localContainer1 == null)
      return;
    if (this.progressContainer != null)
    {
      final Container localContainer2 = this.progressContainer;
      runOnEDT(localContainer1, new Runnable()
      {
        private final Container val$parent;
        private final Container val$child;

        public void run()
        {
          Trace.println("Adding Custom Progress container to parent", TraceLevel.PRELOADER);
          localContainer1.add(localContainer2);
          try
          {
            AWTAccessor.getContainerAccessor().validateUnconditionally(localContainer1);
          }
          catch (Throwable localThrowable)
          {
            localContainer1.invalidate();
            localContainer1.validate();
          }
        }
      });
    }
  }

  private DownloadServiceListener createCustomProgress(Class paramClass)
    throws Exception
  {
    Class[] arrayOfClass1 = new Class[0];
    Class[] arrayOfClass2 = { new Object().getClass() };
    Class[] arrayOfClass3 = { new Object().getClass(), new Object().getClass() };
    Object localObject = null;
    Constructor localConstructor = null;
    try
    {
      localConstructor = paramClass.getConstructor(arrayOfClass3);
      localObject = new Object[2];
      localObject[0] = getSurfaceObject();
      localObject[1] = this.stub;
    }
    catch (Throwable localThrowable)
    {
      try
      {
        localConstructor = paramClass.getConstructor(arrayOfClass2);
        localObject = new Object[1];
        localObject[0] = getSurfaceObject();
      }
      catch (Exception localException1)
      {
        try
        {
          localConstructor = paramClass.getConstructor(arrayOfClass1);
          localObject = arrayOfClass1;
        }
        catch (Exception localException2)
        {
        }
      }
    }
    Trace.println("Using " + localObject.length + "-argument constructor", TraceLevel.PRELOADER);
    return (DownloadServiceListener)localConstructor.newInstance((Object[])localObject);
  }

  private synchronized void setPreloader(Preloader paramPreloader)
  {
    this.preloader = paramPreloader;
  }

  public synchronized Preloader getPreloader()
  {
    return this.preloader;
  }

  public Preloader instantiatePreloader(Class paramClass)
  {
    Trace.println("Requested to use preloader class: " + paramClass, TraceLevel.PRELOADER);
    if (paramClass != null)
      try
      {
        DownloadServiceListener localDownloadServiceListener = createCustomProgress(paramClass);
        if (localDownloadServiceListener != null)
        {
          localObject = new CustomProgress2PreloaderAdapter(localDownloadServiceListener, getApplet2Context());
          installProgressContainer();
          setPreloader((Preloader)localObject);
          return localObject;
        }
      }
      catch (Exception localException)
      {
        Trace.ignored(localException);
      }
    Object localObject = new AWTDefaultPreloader(getApplet2Context(), this.parentContainer);
    setPreloader((Preloader)localObject);
    return localObject;
  }

  public void doShowPreloader()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  private Color getBackgroundColor()
  {
    ColorUtil.ColorRGB localColorRGB = null;
    String str = this.stub.getParameter("boxbgcolor");
    if (str != null)
      localColorRGB = ColorUtil.createColorRGB("boxbgcolor", str);
    if (localColorRGB != null)
      return new Color(localColorRGB.rgb);
    return Color.white;
  }

  private Color getForegroundColor()
  {
    ColorUtil.ColorRGB localColorRGB = null;
    String str = this.stub.getParameter("boxfgcolor");
    if (str != null)
      localColorRGB = ColorUtil.createColorRGB("boxfgcolor", str);
    if (localColorRGB != null)
      return new Color(localColorRGB.rgb);
    return Color.red;
  }

  public void doShowError(final String paramString, Throwable paramThrowable, final boolean paramBoolean)
  {
    final String str = (paramThrowable instanceof JARSigningException) ? ResourceManager.getMessage("dialogfactory.security_error") : paramThrowable == null ? null : SystemUtils.getSimpleName(paramThrowable.getClass());
    if (this.parentContainer == null)
    {
      Environment.setEnvironmentType(1);
      LaunchErrorDialog.show(null, paramThrowable != null ? paramThrowable : new ExitException(new Exception("Launch error"), 3), true);
    }
    else
    {
      runOnEDT(this.parentContainer, new Runnable()
      {
        private final boolean val$offerReload;
        private final String val$title;
        private final String val$message;

        public void run()
        {
          Trace.println("Show default error panel", TraceLevel.UI);
          AWTAppletAdapter.this.parentContainer.setVisible(true);
          AWTErrorPanel localAWTErrorPanel = new AWTErrorPanel(AWTAppletAdapter.this.getBackgroundColor(), AWTAppletAdapter.this.getForegroundColor(), AWTAppletAdapter.this.getApplet2Host(), paramBoolean);
          localAWTErrorPanel.setMessage(str, paramString);
          AWTAppletAdapter.this.doClearParentContainer();
          AWTAppletAdapter.this.parentContainer.add(localAWTErrorPanel, "Center");
          try
          {
            AWTAccessor.getContainerAccessor().validateUnconditionally(AWTAppletAdapter.this.parentContainer);
          }
          catch (Throwable localThrowable)
          {
            AWTAppletAdapter.this.parentContainer.invalidate();
            AWTAppletAdapter.this.parentContainer.validate();
          }
        }
      });
    }
  }

  public Object getLiveConnectObject()
  {
    return this.applet;
  }

  public boolean isInstantiated()
  {
    return this.applet != null;
  }

  public Applet getApplet()
  {
    return this.applet;
  }

  public void abort()
  {
    if (Config.getPluginDebug())
      Trace.ignoredException(new Exception("ABORT " + hashCode()));
    this.applet = null;
  }

  private void findAppletJDKLevel(final Class paramClass)
  {
    synchronized (paramClass)
    {
      if ((this.jdk11Applet != null) || (this.jdk12Applet != null))
        return;
      this.jdk11Applet = Boolean.FALSE;
      this.jdk12Applet = Boolean.FALSE;
      String str1 = paramClass.getName();
      str1 = str1.replace('.', '/');
      final String str2 = str1 + ".class";
      byte[] arrayOfByte = new byte[8];
      try
      {
        InputStream localInputStream = (InputStream)AccessController.doPrivileged(new PrivilegedAction()
        {
          private final Class val$appletClass;
          private final String val$resourceName;

          public Object run()
          {
            return paramClass.getClassLoader().getResourceAsStream(str2);
          }
        });
        int i = localInputStream.read(arrayOfByte, 0, 8);
        localInputStream.close();
        if (i != 8)
          return;
      }
      catch (IOException localIOException)
      {
        return;
      }
      catch (NullPointerException localNullPointerException)
      {
        return;
      }
      int j = readShort(arrayOfByte, 6);
      if (j < 46)
        this.jdk11Applet = Boolean.TRUE;
      else if (j == 46)
        this.jdk12Applet = Boolean.TRUE;
    }
  }

  private boolean isJDK11Applet()
  {
    return this.jdk11Applet.booleanValue();
  }

  private boolean isJDK12Applet()
  {
    return this.jdk12Applet.booleanValue();
  }

  private static int readShort(byte[] paramArrayOfByte, int paramInt)
  {
    int i = readByte(paramArrayOfByte[paramInt]);
    int j = readByte(paramArrayOfByte[(paramInt + 1)]);
    return i << 8 | j;
  }

  private static int readByte(byte paramByte)
  {
    return paramByte & 0xFF;
  }

  private boolean hasInitialFocus()
  {
    if ((isJDK11Applet()) || (isJDK12Applet()))
      return false;
    String str = getApplet2Context().getParameter("initial_focus");
    if ((str != null) && (str.toLowerCase().equals("false")))
      return false;
    return !Platform.get().isNativeModalDialogUp();
  }

  private static Object createSerialApplet(Plugin2ClassLoader paramPlugin2ClassLoader, final String paramString)
    throws ClassNotFoundException, IOException
  {
    try
    {
      AccessControlContext localAccessControlContext = paramPlugin2ClassLoader.getACC();
      if (localAccessControlContext == null)
        throw new SecurityException();
      return AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        private final Plugin2ClassLoader val$loader;
        private final String val$serName;

        public Object run()
          throws IOException, PrivilegedActionException
        {
          Resource localResource = this.val$loader.getResourceAsResource(paramString);
          final ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(localResource.getBytes());
          AccessControlContext localAccessControlContext = this.val$loader.getACC(localResource);
          if (localAccessControlContext == null)
            throw new SecurityException();
          return AccessController.doPrivileged(new PrivilegedExceptionAction()
          {
            private final InputStream val$is;

            public Object run()
              throws IOException, ClassNotFoundException
            {
              AWTAppletAdapter.Applet2ObjectInputStream localApplet2ObjectInputStream = new AWTAppletAdapter.Applet2ObjectInputStream(localByteArrayInputStream, AWTAppletAdapter.16.this.val$loader);
              Object localObject = localApplet2ObjectInputStream.readObject();
              return localObject;
            }
          }
          , localAccessControlContext);
        }
      }
      , localAccessControlContext);
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      Throwable localThrowable = localPrivilegedActionException.getCause();
      if ((localThrowable instanceof PrivilegedActionException))
        localThrowable = localThrowable.getCause();
      if ((localThrowable instanceof IOException))
        throw ((IOException)localThrowable);
      if ((localThrowable instanceof ClassNotFoundException))
        throw ((ClassNotFoundException)localThrowable);
      throw new Error("Undeclared exception", localThrowable);
    }
  }

  public void instantiateSerialApplet(ClassLoader paramClassLoader, final String paramString)
  {
    final Applet[] arrayOfApplet = new Applet[1];
    final Error[] arrayOfError = new Error[1];
    final Plugin2ClassLoader localPlugin2ClassLoader = (Plugin2ClassLoader)paramClassLoader;
    Runnable local17 = new Runnable()
    {
      private final Applet[] val$appletBox;
      private final Plugin2ClassLoader val$pcl;
      private final String val$serName;
      private final Error[] val$errorBox;

      public void run()
      {
        try
        {
          arrayOfApplet[0] = ((Applet)AWTAppletAdapter.createSerialApplet(localPlugin2ClassLoader, paramString));
        }
        catch (ClassNotFoundException localClassNotFoundException)
        {
          throw new RuntimeException(localClassNotFoundException);
        }
        catch (IOException localIOException)
        {
          throw new RuntimeException(localIOException);
        }
        catch (Error localError)
        {
          arrayOfError[0] = localError;
        }
      }
    };
    runOnEDTAndWait(this.parentContainer, local17);
    if (arrayOfError[0] != null)
      throw new RuntimeException(arrayOfError[0]);
    setApplet(arrayOfApplet[0]);
  }

  static class Applet2ObjectInputStream extends ObjectInputStream
  {
    private ClassLoader loader;

    public Applet2ObjectInputStream(InputStream paramInputStream, ClassLoader paramClassLoader)
      throws IOException, StreamCorruptedException
    {
      super();
      if (paramClassLoader == null)
        throw new Applet2IllegalArgumentException("appletillegalargumentexception.objectinputstream");
      this.loader = paramClassLoader;
    }

    private Class primitiveType(char paramChar)
    {
      switch (paramChar)
      {
      case 'B':
        return Byte.TYPE;
      case 'C':
        return Character.TYPE;
      case 'D':
        return Double.TYPE;
      case 'F':
        return Float.TYPE;
      case 'I':
        return Integer.TYPE;
      case 'J':
        return Long.TYPE;
      case 'S':
        return Short.TYPE;
      case 'Z':
        return Boolean.TYPE;
      case 'E':
      case 'G':
      case 'H':
      case 'K':
      case 'L':
      case 'M':
      case 'N':
      case 'O':
      case 'P':
      case 'Q':
      case 'R':
      case 'T':
      case 'U':
      case 'V':
      case 'W':
      case 'X':
      case 'Y':
      }
      return null;
    }

    protected Class resolveClass(ObjectStreamClass paramObjectStreamClass)
      throws IOException, ClassNotFoundException
    {
      String str = paramObjectStreamClass.getName();
      if (str.startsWith("["))
      {
        for (int i = 1; str.charAt(i) == '['; i++);
        Class localClass;
        if (str.charAt(i) == 'L')
        {
          localClass = this.loader.loadClass(str.substring(i + 1, str.length() - 1));
        }
        else
        {
          if (str.length() != i + 1)
            throw new ClassNotFoundException(str);
          localClass = primitiveType(str.charAt(i));
        }
        int[] arrayOfInt = new int[i];
        for (int j = 0; j < i; j++)
          arrayOfInt[j] = 0;
        return Array.newInstance(localClass, arrayOfInt).getClass();
      }
      return this.loader.loadClass(str);
    }
  }

  private static class AppletContextImpl
    implements AppletContext, JSContext
  {
    private final Applet2Context ctx;
    private static final Map imageRefs = new HashMap();
    private static final Map audioClipStore = new HashMap();
    private static final Map streamStore = new HashMap();
    private static final int PERSIST_STREAM_MAX_SIZE = 65536;

    private AppletContextImpl(Applet2Context paramApplet2Context)
    {
      this.ctx = paramApplet2Context;
    }

    private AppletCompatibleHost getHost()
    {
      Applet2Host localApplet2Host = this.ctx.getHost();
      if ((localApplet2Host instanceof AppletCompatibleHost))
        return (AppletCompatibleHost)localApplet2Host;
      throw new IllegalStateException();
    }

    public AudioClip getAudioClip(URL paramURL)
    {
      if (paramURL == null)
        return null;
      System.getSecurityManager().checkConnect(paramURL.getHost(), paramURL.getPort());
      SoftReference localSoftReference;
      synchronized (audioClipStore)
      {
        HashMap localHashMap = (HashMap)audioClipStore.get(this.ctx.getCodeBase());
        if (localHashMap == null)
        {
          localHashMap = new HashMap();
          audioClipStore.put(this.ctx.getCodeBase(), localHashMap);
        }
        localSoftReference = (SoftReference)localHashMap.get(paramURL);
        if ((localSoftReference == null) || (localSoftReference.get() == null))
        {
          localSoftReference = new SoftReference(Applet2AudioClipFactory.createAudioClip(paramURL));
          localHashMap.put(paramURL, localSoftReference);
        }
      }
      ??? = (AudioClip)localSoftReference.get();
      Trace.msgPrintln("appletcontext.audio.loaded", new Object[] { paramURL }, TraceLevel.BASIC);
      return ???;
    }

    public Image getImage(URL paramURL)
    {
      if (paramURL == null)
        return null;
      System.getSecurityManager().checkConnect(paramURL.getHost(), paramURL.getPort());
      SoftReference localSoftReference;
      synchronized (imageRefs)
      {
        localSoftReference = (SoftReference)imageRefs.get(paramURL);
        if ((localSoftReference == null) || (localSoftReference.get() == null))
        {
          localSoftReference = new SoftReference(Applet2ImageFactory.createImage(paramURL));
          imageRefs.put(paramURL, localSoftReference);
        }
      }
      ??? = (Image)localSoftReference.get();
      Trace.msgPrintln("appletcontext.image.loaded", new Object[] { paramURL }, TraceLevel.BASIC);
      return ???;
    }

    public Applet getApplet(String paramString)
    {
      Applet2Adapter localApplet2Adapter = getHost().getApplet2Adapter(paramString);
      if ((localApplet2Adapter != null) && ((localApplet2Adapter instanceof AWTAppletAdapter)))
        return ((AWTAppletAdapter)localApplet2Adapter).getApplet();
      return null;
    }

    public Enumeration getApplets()
    {
      ArrayList localArrayList = new ArrayList();
      Enumeration localEnumeration = getHost().getApplet2Adapters();
      while (localEnumeration.hasMoreElements())
      {
        Applet2Adapter localApplet2Adapter = (Applet2Adapter)localEnumeration.nextElement();
        if ((localApplet2Adapter instanceof AWTAppletAdapter))
          localArrayList.add(((AWTAppletAdapter)localApplet2Adapter).getApplet());
      }
      return Collections.enumeration(localArrayList);
    }

    public void setStream(String paramString, InputStream paramInputStream)
      throws IOException
    {
      HashMap localHashMap;
      synchronized (streamStore)
      {
        localHashMap = (HashMap)streamStore.get(this.ctx.getCodeBase());
        if (localHashMap == null)
        {
          localHashMap = new HashMap();
          streamStore.put(this.ctx.getCodeBase(), localHashMap);
        }
      }
      synchronized (localHashMap)
      {
        if (paramInputStream != null)
        {
          byte[] arrayOfByte = (byte[])localHashMap.get(paramString);
          if (arrayOfByte == null)
          {
            int i = paramInputStream.available();
            if (i < 65536)
            {
              arrayOfByte = new byte[i];
              paramInputStream.read(arrayOfByte, 0, i);
              localHashMap.put(paramString, arrayOfByte);
            }
            else
            {
              throw new IOException("Stream size exceeds the maximum limit");
            }
          }
          else
          {
            localHashMap.remove(paramString);
            setStream(paramString, paramInputStream);
          }
        }
        else
        {
          localHashMap.remove(paramString);
        }
      }
    }

    public InputStream getStream(String paramString)
    {
      ByteArrayInputStream localByteArrayInputStream = null;
      HashMap localHashMap = (HashMap)streamStore.get(this.ctx.getCodeBase());
      if (localHashMap != null)
        synchronized (localHashMap)
        {
          byte[] arrayOfByte = (byte[])localHashMap.get(paramString);
          if (arrayOfByte != null)
            localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
        }
      return localByteArrayInputStream;
    }

    public Iterator getStreamKeys()
    {
      Iterator localIterator = null;
      HashMap localHashMap = (HashMap)streamStore.get(this.ctx.getCodeBase());
      if (localHashMap != null)
        synchronized (localHashMap)
        {
          localIterator = localHashMap.keySet().iterator();
        }
      return localIterator;
    }

    public void showDocument(URL paramURL)
    {
      getHost().showDocument(paramURL);
    }

    public void showDocument(URL paramURL, String paramString)
    {
      getHost().showDocument(paramURL, paramString);
    }

    public void showStatus(String paramString)
    {
      getHost().showStatus(paramString);
    }

    public JSObject getJSObject()
    {
      AppletCompatibleHost localAppletCompatibleHost = getHost();
      return (localAppletCompatibleHost instanceof Plugin2Host) ? ((Plugin2Host)localAppletCompatibleHost).getJSObject() : null;
    }

    public JSObject getOneWayJSObject()
    {
      AppletCompatibleHost localAppletCompatibleHost = getHost();
      return (localAppletCompatibleHost instanceof Plugin2Host) ? ((Plugin2Host)localAppletCompatibleHost).getOneWayJSObject() : null;
    }

    AppletContextImpl(Applet2Context paramApplet2Context, AWTAppletAdapter.1 param1)
    {
      this(paramApplet2Context);
    }
  }

  private class AppletStubImpl
    implements AppletStub
  {
    private final Applet2Context ctx;

    AppletStubImpl(Applet2Context arg2)
    {
      Object localObject;
      this.ctx = localObject;
    }

    public boolean isActive()
    {
      return this.ctx.isActive();
    }

    public URL getDocumentBase()
    {
      return AWTAppletAdapter.this.getApplet2Host().getDocumentBase();
    }

    public URL getCodeBase()
    {
      return this.ctx.getCodeBase();
    }

    public String getParameter(String paramString)
    {
      return this.ctx.getParameter(paramString);
    }

    public AppletContext getAppletContext()
    {
      return AWTAppletAdapter.this.context;
    }

    public void appletResize(final int paramInt1, final int paramInt2)
    {
      final Applet localApplet = AWTAppletAdapter.this.applet;
      if ((localApplet != null) && (AWTAppletAdapter.this.appletAppContext != null))
      {
        Runnable local1 = new Runnable()
        {
          private final Applet val$a;
          private final int val$width;
          private final int val$height;

          public void run()
          {
            localApplet.resize(paramInt1, paramInt2);
            localApplet.validate();
          }
        };
        if (EventQueue.isDispatchThread())
          local1.run();
        else
          OldPluginAWTUtil.invokeLater(localApplet, local1);
      }
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     com.sun.deploy.uitoolkit.impl.awt.AWTAppletAdapter
 * JD-Core Version:    0.6.2
 */