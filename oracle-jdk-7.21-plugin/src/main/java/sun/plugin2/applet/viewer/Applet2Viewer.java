package sun.plugin2.applet.viewer;

import com.sun.applet2.AppletParameters;
import com.sun.deploy.appcontext.AppContext;
import com.sun.deploy.uitoolkit.ToolkitStore;
import com.sun.deploy.uitoolkit.Window;
import com.sun.deploy.uitoolkit.WindowFactory;
import com.sun.deploy.util.JVMParameters;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;
import javax.swing.text.html.parser.ParserDelegator;
import sun.plugin2.applet.Applet2Environment;
import sun.plugin2.applet.Applet2Listener;
import sun.plugin2.applet.Applet2Manager;
import sun.plugin2.applet.Plugin2ConsoleController;
import sun.plugin2.applet.Plugin2Manager;
import sun.plugin2.applet.context.NoopExecutionContext;
import sun.plugin2.applet.viewer.util.AppletTagParser;
import sun.plugin2.util.SystemUtil;

public class Applet2Viewer
{
  private static final boolean DEBUG = SystemUtil.getenv("JPI_PLUGIN2_DEBUG") != null;

  public static void main(String[] paramArrayOfString)
    throws Exception
  {
    if (paramArrayOfString.length != 1)
    {
      System.out.println("Usage: Applet2Viewer [url to HTML page containing <applet> tag]");
      System.out.println("Views the first applet on the specified HTML page.");
      System.exit(1);
    }
    URL localURL = new URL(paramArrayOfString[0]);
    InputStream localInputStream = localURL.openStream();
    if (localInputStream == null)
      throw new RuntimeException("Error opening URL " + localURL);
    AppletTagParser localAppletTagParser = new AppletTagParser();
    new ParserDelegator().parse(new InputStreamReader(localInputStream), localAppletTagParser, true);
    localInputStream.close();
    if (!localAppletTagParser.foundApplet())
    {
      System.out.println("No applet found on web page");
      System.exit(1);
    }
    AppletParameters localAppletParameters = localAppletTagParser.getParameters();
    Exception localException1 = 512;
    int i = 512;
    try
    {
      localException1 = Integer.parseInt((String)localAppletParameters.get("width"));
    }
    catch (Exception localException2)
    {
    }
    try
    {
      i = Integer.parseInt((String)localAppletParameters.get("height"));
    }
    catch (Exception localException3)
    {
    }
    localException3 = localException1;
    final int j = i;
    JVMParameters localJVMParameters = new JVMParameters();
    localJVMParameters.parseBootClassPath(JVMParameters.getPlugInDependentJars());
    localJVMParameters.setDefault(true);
    JVMParameters.setRunningJVMParameters(localJVMParameters);
    System.out.println("Initializing Applet2Environment");
    Applet2Environment.initialize(null, false, false, new Plugin2ConsoleController(null, null), null, null);
    Applet2Manager localApplet2Manager = new Applet2Manager(null, null, false);
    localApplet2Manager.setAppletExecutionContext(new NoopExecutionContext(localAppletParameters, localURL.toExternalForm()));
    System.out.println("Starting applet with parameters:");
    Object localObject = localAppletParameters.keySet().iterator();
    while (((Iterator)localObject).hasNext())
    {
      String str1 = (String)((Iterator)localObject).next();
      String str2 = (String)localAppletParameters.get(str1);
      System.out.println("  " + str1 + " = " + str2);
    }
    localObject = localApplet2Manager.getAppletAppContext();
    ((AppContext)localObject).invokeLater(new Runnable()
    {
      private final Applet2Manager val$manager;
      private final int val$fw;
      private final int val$fh;

      public void run()
      {
        try
        {
          this.val$manager.initialize();
        }
        catch (Exception localException)
        {
          localException.printStackTrace();
          System.err.println("Error while initializing manager: " + localException + ", bail out");
          return;
        }
        Window localWindow = ToolkitStore.getWindowFactory().createWindow();
        final Frame localFrame = (Frame)localWindow.getWindowObject();
        localFrame.setTitle("Applet2Viewer");
        localFrame.addWindowListener(new WindowAdapter()
        {
          public void windowClosing(WindowEvent paramAnonymous2WindowEvent)
          {
            System.exit(0);
          }
        });
        localFrame.setLayout(new BorderLayout());
        this.val$manager.setAppletParent(localWindow);
        this.val$manager.addAppletListener(new Applet2Listener()
        {
          private final Frame val$f;

          public boolean appletSSVValidation(Plugin2Manager paramAnonymous2Plugin2Manager)
          {
            if (Applet2Viewer.DEBUG)
              System.out.println("Applet2Viewer.appletSSVValidation");
            return false;
          }

          public boolean isAppletRelaunchSupported()
          {
            return false;
          }

          public void appletJRERelaunch(Plugin2Manager paramAnonymous2Plugin2Manager, String paramAnonymous2String1, String paramAnonymous2String2)
          {
            if (Applet2Viewer.DEBUG)
            {
              System.out.println("Applet2Viewer.appletJRERelaunch:");
              System.out.println("\tjava_version   : " + paramAnonymous2String1);
              System.out.println("\tjava_arguments : " + paramAnonymous2String2);
              Exception localException = new Exception("Applet2Viewer.appletJRERelaunch: " + paramAnonymous2String1 + " ; " + paramAnonymous2String2);
              localException.printStackTrace(System.out);
            }
          }

          public void appletLoaded(Plugin2Manager paramAnonymous2Plugin2Manager)
          {
          }

          public void appletReady(Plugin2Manager paramAnonymous2Plugin2Manager)
          {
            Canvas localCanvas = new Canvas();
            localCanvas.setSize(Applet2Viewer.1.this.val$fw, Applet2Viewer.1.this.val$fh);
            localFrame.add(localCanvas, "Center");
            localFrame.pack();
            localFrame.setVisible(true);
            localFrame.remove(localCanvas);
          }

          public void appletErrorOccurred(Plugin2Manager paramAnonymous2Plugin2Manager)
          {
          }

          public String getBestJREVersion(Plugin2Manager paramAnonymous2Plugin2Manager, String paramAnonymous2String1, String paramAnonymous2String2)
          {
            return null;
          }
        });
        this.val$manager.start();
      }
    });
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.applet.viewer.Applet2Viewer
 * JD-Core Version:    0.6.2
 */