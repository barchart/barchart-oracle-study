package sun.plugin2.applet.viewer;

import com.sun.applet2.AppletParameters;
import com.sun.deploy.Environment;
import com.sun.deploy.appcontext.AppContext;
import com.sun.deploy.cache.Cache;
import com.sun.deploy.config.JREInfo;
import com.sun.deploy.config.JfxRuntime;
import com.sun.deploy.config.OSType;
import com.sun.deploy.config.Platform;
import com.sun.deploy.model.LocalApplicationProperties;
import com.sun.deploy.model.ResourceObject;
import com.sun.deploy.model.ResourceProvider;
import com.sun.deploy.si.SingleInstanceManager;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.uitoolkit.DragContext;
import com.sun.deploy.uitoolkit.DragHelper;
import com.sun.deploy.uitoolkit.DragListener;
import com.sun.deploy.uitoolkit.PluginUIToolkit;
import com.sun.deploy.uitoolkit.ToolkitStore;
import com.sun.deploy.uitoolkit.Window;
import com.sun.deploy.uitoolkit.WindowFactory;
import com.sun.deploy.util.JVMParameters;
import com.sun.deploy.util.SessionState;
import com.sun.javaws.Globals;
import com.sun.javaws.HtmlOptions;
import com.sun.javaws.IconUtil;
import com.sun.javaws.JnlpxArgs;
import com.sun.javaws.exceptions.ExitException;
import com.sun.javaws.exceptions.JreExecException;
import com.sun.javaws.jnl.InformationDesc;
import com.sun.javaws.jnl.JREDesc;
import com.sun.javaws.jnl.LaunchDesc;
import com.sun.javaws.jnl.LaunchDescFactory;
import com.sun.javaws.jnl.MatchJREIf;
import com.sun.javaws.ui.LaunchErrorDialog;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import javax.swing.text.html.parser.ParserDelegator;
import sun.plugin2.applet.Applet2Environment;
import sun.plugin2.applet.Applet2ExecutionContext;
import sun.plugin2.applet.Applet2Listener;
import sun.plugin2.applet.JNLP2Manager;
import sun.plugin2.applet.JNLP2Tag;
import sun.plugin2.applet.Plugin2ConsoleController;
import sun.plugin2.applet.Plugin2Manager;
import sun.plugin2.applet.context.InitialJNLPExecutionContext;
import sun.plugin2.applet.viewer.util.AppletTagParser;
import sun.plugin2.main.client.Applet2DragContext;
import sun.plugin2.main.client.DisconnectedExecutionContext;
import sun.plugin2.main.client.PluginMain;
import sun.plugin2.util.SystemUtil;

public class JNLP2Viewer
{
  private static final boolean DEBUG = SystemUtil.getenv("JPI_PLUGIN2_DEBUG") != null;
  private boolean _isDraggedApplet;
  private boolean _isAssociation;
  private boolean _isAppletDescApplet;
  private String _jnlpFile;

  public static void main(String[] paramArrayOfString)
    throws Exception
  {
    Environment.setEnvironmentType(0);
    ToolkitStore.setMode(1);
    new JNLP2Viewer().run(paramArrayOfString);
  }

  private void run(String[] paramArrayOfString)
    throws Exception
  {
    JNLP2Manager localJNLP2Manager = parseAndInitialize(paramArrayOfString);
    start(localJNLP2Manager);
  }

  JNLP2Manager parseAndInitialize(String[] paramArrayOfString)
    throws Exception
  {
    AppletParameters localAppletParameters = new AppletParameters();
    String str1 = null;
    URL localURL = null;
    String str2 = null;
    String str3 = null;
    String str4 = null;
    if ((paramArrayOfString.length > 5) || (paramArrayOfString.length < 1))
    {
      System.out.println("Usage: JNLP2Viewer [url to HTML page containing <applet> tag with " + JNLP2Tag.JNLP_HREF + " parameter,]");
      System.out.println("                   [url to a jnlp file direct.]");
      System.out.println("Views the first applet on the specified HTML page.");
      System.exit(1);
    }
    int i = 0;
    do
    {
      if ((paramArrayOfString[i].equals("-open")) || (paramArrayOfString[i].equals("-print")))
      {
        this._isAssociation = true;
        str4 = paramArrayOfString[(++i)];
        str1 = paramArrayOfString[(++i)];
      }
      else if (paramArrayOfString[i].equals("-codebase"))
      {
        this._isAppletDescApplet = true;
        str2 = paramArrayOfString[(++i)];
      }
      else if (paramArrayOfString[i].equals("-documentbase"))
      {
        this._isAppletDescApplet = true;
        str3 = paramArrayOfString[(++i)];
      }
      else if (paramArrayOfString[i].equals("-draggedApplet"))
      {
        this._isDraggedApplet = true;
      }
      else
      {
        str1 = paramArrayOfString[i];
      }
      i++;
    }
    while (i < paramArrayOfString.length);
    if (DEBUG)
      for (int j = 0; j < paramArrayOfString.length; j++)
        System.out.println("\tJNLP2Viewer args[" + j + "] = " + paramArrayOfString[j]);
    if ((this._isAppletDescApplet) || (this._isDraggedApplet) || (this._isAssociation))
    {
      LaunchDesc localLaunchDesc = null;
      try
      {
        localLaunchDesc = LaunchDescFactory.buildDescriptor(new File(str1), new URL(str2), null, null);
      }
      catch (Exception localException2)
      {
        Trace.ignoredException(localException2);
      }
      if (localLaunchDesc != null)
        localURL = localLaunchDesc.getInformation().getHome();
    }
    else
    {
      try
      {
        localURL = new URL(str1);
      }
      catch (Exception localException1)
      {
        if ((!str1.startsWith("http")) && (!str1.startsWith("file")))
          try
          {
            localURL = new URL("file:///" + str1);
            str1 = localURL.toString();
          }
          catch (Exception localException3)
          {
            localException3.printStackTrace(System.out);
          }
        else
          localException1.printStackTrace(System.out);
      }
      localObject1 = localURL.openStream();
      if (localObject1 == null)
        throw new RuntimeException("Error opening URL " + localURL);
      localObject2 = new AppletTagParser();
      new ParserDelegator().parse(new InputStreamReader((InputStream)localObject1), (HTMLEditorKit.ParserCallback)localObject2, true);
      ((InputStream)localObject1).close();
      if (!((AppletTagParser)localObject2).foundApplet())
      {
        if (DEBUG)
          System.out.println("No applet found on web page, try as JNLP direct");
      }
      else
      {
        localAppletParameters = ((AppletTagParser)localObject2).getParameters();
        try
        {
          str1 = (String)localAppletParameters.get(JNLP2Tag.JNLP_HREF);
        }
        catch (Exception localException4)
        {
        }
        if (str1 == null)
        {
          System.out.println("No <" + JNLP2Tag.JNLP_HREF + "> parameter given in applet tag, bail out\n");
          System.exit(1);
        }
        try
        {
          str2 = (String)localAppletParameters.get("java_codebase");
          if (str2 == null)
            str2 = (String)localAppletParameters.get("codebase");
        }
        catch (Exception localException5)
        {
        }
      }
    }
    Object localObject1 = new JVMParameters();
    ((JVMParameters)localObject1).parseTrustedOptions(JnlpxArgs.getVMArgs());
    ((JVMParameters)localObject1).parseBootClassPath(JVMParameters.getPlugInDependentJars());
    ((JVMParameters)localObject1).setDefault(true);
    JVMParameters.setRunningJVMParameters((JVMParameters)localObject1);
    if (DEBUG)
      System.out.println("Initializing Applet2Environment");
    Object localObject2 = new InitialJNLPExecutionContext(localAppletParameters);
    Applet2Environment.initialize(null, true, false, new Plugin2ConsoleController(null, null), (Applet2ExecutionContext)localObject2, null);
    JNLP2Manager.initializeExecutionEnvironment();
    if ((this._isAppletDescApplet) && (str3 != null))
      localURL = new URL(str3);
    LocalApplicationProperties localLocalApplicationProperties = null;
    ResourceObject localResourceObject = ResourceProvider.get().getResourceObject(str1);
    if ((this._isDraggedApplet) && (localResourceObject != null))
    {
      localLocalApplicationProperties = ResourceProvider.get().getLocalApplicationProperties(localResourceObject.getResourceURL(), localResourceObject.getResourceVersion(), true);
      if (localLocalApplicationProperties != null)
      {
        str2 = localLocalApplicationProperties.getCodebase();
        localURL = new URL(localLocalApplicationProperties.getDocumentBase());
      }
    }
    String str5 = null;
    if ((this._isAppletDescApplet) || (this._isDraggedApplet) || (this._isAssociation))
    {
      str5 = str1;
      if (localResourceObject != null)
        str1 = localResourceObject.getResourceURL().toString();
      else if (DEBUG)
        System.err.println("Unable to obtain the CacheEntry from file " + str1 + ".idx");
    }
    this._jnlpFile = str1;
    if (this._isAssociation)
    {
      localAppletParameters.put("_numargs", "1");
      localAppletParameters.put("_arg0", str4);
    }
    JNLP2Manager localJNLP2Manager = new JNLP2Manager(str2, localURL, str1, JnlpxArgs.getIsRelaunch());
    if ((this._isAppletDescApplet) || (this._isDraggedApplet) || (this._isAssociation))
      localJNLP2Manager.setCachedJNLPFilePath(str5);
    localJNLP2Manager.setAppletExecutionContext(new DisconnectedExecutionContext(localAppletParameters, localURL.toExternalForm(), (Applet2ExecutionContext)localObject2));
    return localJNLP2Manager;
  }

  private void start(final JNLP2Manager paramJNLP2Manager)
  {
    LocalApplicationProperties localLocalApplicationProperties = getLapFile();
    final boolean bool1;
    if (localLocalApplicationProperties != null)
      bool1 = localLocalApplicationProperties.isDraggedApplet();
    else
      bool1 = this._isDraggedApplet;
    final boolean bool2 = this._isAssociation;
    final boolean bool3 = this._isAppletDescApplet;
    final ThreadGroup localThreadGroup = Thread.currentThread().getThreadGroup();
    AppContext localAppContext = paramJNLP2Manager.getAppletAppContext();
    localAppContext.invokeLater(new Runnable()
    {
      private final JNLP2Manager val$manager;
      private final ThreadGroup val$mainThreadGroup;
      private final boolean val$isAppletDescApplet;
      private final boolean val$isDraggedApplet;
      private final boolean val$isAssociation;

      public void run()
      {
        try
        {
          paramJNLP2Manager.initialize();
        }
        catch (Exception localException1)
        {
          localException1.printStackTrace(System.out);
          System.err.println("Error while initializing manager: " + localException1 + ", bail out");
          Environment.setEnvironmentType(1);
          LaunchErrorDialog.show(null, localException1, true);
        }
        AppletParameters localAppletParameters = paramJNLP2Manager.getAppletExecutionContext().getAppletParameters();
        final LaunchDesc localLaunchDesc = paramJNLP2Manager.getLaunchDesc();
        int i = 512;
        int j = 512;
        try
        {
          i = Integer.parseInt((String)localAppletParameters.get("width"));
        }
        catch (Exception localException2)
        {
        }
        try
        {
          j = Integer.parseInt((String)localAppletParameters.get("height"));
        }
        catch (Exception localException3)
        {
        }
        if (JNLP2Viewer.DEBUG)
          System.out.println("Starting applet (" + i + "x" + j + ") with parameters:");
        if (JNLP2Viewer.DEBUG)
          localAppletParameters.dump();
        String str = null;
        if (localLaunchDesc != null)
          str = localLaunchDesc.getInformation().getTitle();
        if (str == null)
          str = "JNLPApplet2Viewer";
        paramJNLP2Manager.setAppletSize(i, j);
        final Window localWindow;
        final Frame localFrame;
        Object localObject;
        if (!localLaunchDesc.isFXApp())
        {
          localWindow = ToolkitStore.getWindowFactory().createWindow();
          localFrame = (Frame)localWindow.getWindowObject();
          localFrame.setTitle(str);
          boolean bool = paramJNLP2Manager.getUndecorated();
          localFrame.setUndecorated(bool);
          localFrame.addWindowListener(new WindowAdapter()
          {
            public void windowClosing(WindowEvent paramAnonymous2WindowEvent)
            {
              JNLP2Viewer.this.stopAndExit(JNLP2Viewer.1.this.val$mainThreadGroup, JNLP2Viewer.1.this.val$manager);
            }
          });
          paramJNLP2Manager.setAppletParent(localWindow);
          localFrame.pack();
          localObject = localFrame.getInsets();
          localFrame.setSize(i + ((Insets)localObject).left + ((Insets)localObject).right, j + ((Insets)localObject).right + ((Insets)localObject).top);
          if (bool3)
            localFrame.setResizable(false);
        }
        else
        {
          JNLP2Manager.setEmbeddedMode(false);
          localWindow = null;
          localFrame = null;
        }
        final Applet2DragContext localApplet2DragContext = Applet2DragContext.getDragContext(paramJNLP2Manager);
        if (bool1)
        {
          localObject = (PluginUIToolkit)ToolkitStore.get();
          ((PluginUIToolkit)localObject).getDragHelper().register(localApplet2DragContext, new DragListener()
          {
            public void appletDraggingToDesktop(DragContext paramAnonymous2DragContext)
            {
            }

            public void appletDroppedOntoDesktop(DragContext paramAnonymous2DragContext)
            {
            }

            public void appletExternalWindowClosed(DragContext paramAnonymous2DragContext)
            {
              JNLP2Viewer.this.stopAndExit(JNLP2Viewer.1.this.val$mainThreadGroup, JNLP2Viewer.1.this.val$manager);
            }
          });
        }
        paramJNLP2Manager.addAppletListener(new Applet2Listener()
        {
          private final LaunchDesc val$launchDesc;
          private final Frame val$appFrame;
          private final DragContext val$dragContext;
          private final Window val$appWindow;

          public boolean appletSSVValidation(Plugin2Manager paramAnonymous2Plugin2Manager)
            throws ExitException
          {
            boolean bool = PluginMain.performSSVValidation(paramAnonymous2Plugin2Manager);
            if ((!bool) && ((paramAnonymous2Plugin2Manager instanceof JNLP2Manager)))
            {
              JNLP2Manager localJNLP2Manager = (JNLP2Manager)paramAnonymous2Plugin2Manager;
              localJNLP2Manager.clearRelaunchException();
            }
            if (JNLP2Viewer.DEBUG)
              System.out.println("JNLP2Viewer.appletSSVValidation return: " + bool);
            return bool;
          }

          public boolean isAppletRelaunchSupported()
          {
            return true;
          }

          public void appletJRERelaunch(Plugin2Manager paramAnonymous2Plugin2Manager, String paramAnonymous2String1, String paramAnonymous2String2)
          {
            if (JNLP2Viewer.DEBUG)
            {
              System.out.println("JNLP2Viewer.appletJRERelaunch:");
              System.out.println("\tjava_version   : " + paramAnonymous2String1);
              System.out.println("\tjava_arguments : " + paramAnonymous2String2);
              System.out.println("\thostingManager : " + paramAnonymous2Plugin2Manager);
              Thread.dumpStack();
            }
            if ((paramAnonymous2Plugin2Manager instanceof JNLP2Manager))
            {
              JNLP2Manager localJNLP2Manager = (JNLP2Manager)paramAnonymous2Plugin2Manager;
              LaunchDesc localLaunchDesc = localJNLP2Manager.getLaunchDesc();
              MatchJREIf localMatchJREIf = localLaunchDesc.getJREMatcher();
              JVMParameters localJVMParameters = localMatchJREIf.getSelectedJVMParameters();
              JREInfo localJREInfo = localMatchJREIf.getSelectedJREInfo();
              JREDesc localJREDesc = localMatchJREIf.getSelectedJREDesc();
              String[] arrayOfString = new String[3];
              arrayOfString[0] = "-codebase";
              arrayOfString[1] = localJNLP2Manager.getCodeBase().toString();
              URL localURL = localLaunchDesc.getCanonicalHome();
              if ((localURL == null) && (JNLP2Viewer.this._jnlpFile != null))
                try
                {
                  localURL = new URL(JNLP2Viewer.this._jnlpFile);
                }
                catch (MalformedURLException localMalformedURLException)
                {
                  Trace.ignored(localMalformedURLException);
                }
              if (localURL != null)
              {
                File localFile = ResourceProvider.get().getCachedJNLPFile(localURL, localLaunchDesc.getVersion());
                if (localFile != null)
                  arrayOfString[2] = localFile.getAbsolutePath();
              }
              if (arrayOfString[2] == null)
                arrayOfString[2] = JNLP2Viewer.this._jnlpFile;
              try
              {
                relaunch(localJREInfo, localJREDesc, localLaunchDesc, arrayOfString, localJVMParameters, false, localJREInfo.getJfxRuntime(), localLaunchDesc.needFX());
              }
              catch (Exception localException)
              {
                Trace.ignoredException(localException);
              }
            }
          }

          private String[] insertApplicationArgs(String[] paramAnonymous2ArrayOfString)
          {
            String[] arrayOfString1 = Globals.getApplicationArgs();
            if (arrayOfString1 == null)
              return paramAnonymous2ArrayOfString;
            String[] arrayOfString2 = new String[arrayOfString1.length + paramAnonymous2ArrayOfString.length];
            for (int i = 0; i < arrayOfString1.length; i++)
              arrayOfString2[i] = arrayOfString1[i];
            for (int j = 0; j < paramAnonymous2ArrayOfString.length; j++)
              arrayOfString2[(i++)] = paramAnonymous2ArrayOfString[j];
            return arrayOfString2;
          }

          private void relaunch(JREInfo paramAnonymous2JREInfo, JREDesc paramAnonymous2JREDesc, LaunchDesc paramAnonymous2LaunchDesc, String[] paramAnonymous2ArrayOfString, JVMParameters paramAnonymous2JVMParameters, boolean paramAnonymous2Boolean1, JfxRuntime paramAnonymous2JfxRuntime, boolean paramAnonymous2Boolean2)
            throws ExitException
          {
            Object localObject2;
            if (OSType.isMac())
            {
              String str = paramAnonymous2LaunchDesc.getInformation().getTitle();
              Charset localCharset = Charset.forName("UTF-8");
              CharsetDecoder localCharsetDecoder = localCharset.newDecoder();
              CharsetEncoder localCharsetEncoder = localCharset.newEncoder();
              try
              {
                ByteBuffer localByteBuffer = localCharsetEncoder.encode(CharBuffer.wrap(str));
                CharBuffer localCharBuffer = localCharsetDecoder.decode(localByteBuffer);
                localObject1 = localCharBuffer.toString();
              }
              catch (CharacterCodingException localCharacterCodingException)
              {
                localObject1 = null;
              }
              if (str != null)
                System.setProperty("macosx.jnlpx.dock.name", (String)localObject1);
              localObject2 = IconUtil.getIconPath(paramAnonymous2LaunchDesc);
              if (localObject2 == null)
                localObject2 = Platform.get().getDefaultIconPath();
              if (localObject2 != null)
                System.setProperty("macosx.jnlpx.dock.icon", (String)localObject2);
            }
            long l1 = paramAnonymous2JREDesc.getMinHeap();
            long l2 = paramAnonymous2JREDesc.getMaxHeap();
            Object localObject1 = HtmlOptions.get();
            if (localObject1 != null)
              try
              {
                localObject2 = File.createTempFile("zzjnl", ".tmp");
                ((HtmlOptions)localObject1).export(new FileOutputStream((File)localObject2));
                paramAnonymous2ArrayOfString = new String[2];
                paramAnonymous2ArrayOfString[0] = "-nocodebase";
                paramAnonymous2ArrayOfString[1] = ((File)localObject2).getAbsolutePath();
              }
              catch (IOException localIOException1)
              {
                throw new ExitException("Failed to relaunch. Can not save launch file.", localIOException1);
              }
            File localFile = SessionState.save();
            if (localFile != null)
              System.setProperty("jnlpx.session.data", localFile.getAbsolutePath());
            try
            {
              paramAnonymous2ArrayOfString = insertApplicationArgs(paramAnonymous2ArrayOfString);
              JnlpxArgs.execProgram(paramAnonymous2JREInfo, paramAnonymous2ArrayOfString, l1, l2, paramAnonymous2JVMParameters, paramAnonymous2Boolean1, paramAnonymous2JfxRuntime, paramAnonymous2Boolean2);
            }
            catch (IOException localIOException2)
            {
              throw new ExitException(new JreExecException(paramAnonymous2JREInfo.getPath(), localIOException2), 3);
            }
            if (JnlpxArgs.shouldRemoveArgumentFile())
              JnlpxArgs.setShouldRemoveArgumentFile(String.valueOf(false));
            JNLP2Viewer.this.stopAndExit(JNLP2Viewer.1.this.val$mainThreadGroup, JNLP2Viewer.1.this.val$manager);
          }

          public void appletLoaded(Plugin2Manager paramAnonymous2Plugin2Manager)
          {
          }

          public void appletReady(Plugin2Manager paramAnonymous2Plugin2Manager)
          {
            if ((JNLP2Viewer.1.this.val$isAssociation) && (SingleInstanceManager.isServerRunning(localLaunchDesc.getCanonicalHome().toString())))
              SingleInstanceManager.connectToServer(localLaunchDesc.toString());
            if (localFrame != null)
              localFrame.setVisible(true);
            if (JNLP2Viewer.1.this.val$isDraggedApplet)
            {
              PluginUIToolkit localPluginUIToolkit = (PluginUIToolkit)ToolkitStore.get();
              localPluginUIToolkit.getDragHelper().makeDisconnected(localApplet2DragContext, localWindow);
            }
          }

          public void appletErrorOccurred(Plugin2Manager paramAnonymous2Plugin2Manager)
          {
          }

          public String getBestJREVersion(Plugin2Manager paramAnonymous2Plugin2Manager, String paramAnonymous2String1, String paramAnonymous2String2)
          {
            return null;
          }
        });
        paramJNLP2Manager.start();
      }
    });
  }

  private void stopAndExit(final ThreadGroup paramThreadGroup, final Plugin2Manager paramPlugin2Manager)
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      private final ThreadGroup val$group;
      private final Plugin2Manager val$manager;

      public Object run()
      {
        Thread localThread = new Thread(paramThreadGroup, new Runnable()
        {
          public void run()
          {
            JNLP2Viewer.2.this.val$manager.stop(null, null);
            Trace.flush();
            System.exit(0);
          }
        });
        localThread.setDaemon(true);
        localThread.start();
        return null;
      }
    });
  }

  private LocalApplicationProperties getLapFile()
  {
    LocalApplicationProperties localLocalApplicationProperties = null;
    try
    {
      URL localURL = new URL(this._jnlpFile);
      localLocalApplicationProperties = ResourceProvider.get().getLocalApplicationProperties(localURL, null, true);
    }
    catch (MalformedURLException localMalformedURLException)
    {
    }
    if (localLocalApplicationProperties == null)
      localLocalApplicationProperties = Cache.getLocalApplicationProperties(this._jnlpFile);
    return localLocalApplicationProperties;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.applet.viewer.JNLP2Viewer
 * JD-Core Version:    0.6.2
 */