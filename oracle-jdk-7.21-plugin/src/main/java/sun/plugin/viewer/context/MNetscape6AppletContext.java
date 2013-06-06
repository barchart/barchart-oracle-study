package sun.plugin.viewer.context;

import com.sun.deploy.util.URLUtil;
import java.net.URL;
import sun.applet.AppletPanel;
import sun.plugin.navig.motif.Worker;

public class MNetscape6AppletContext extends NetscapeAppletContext
{
  public void doShowDocument(URL paramURL, String paramString)
  {
    if (!URLUtil.checkTargetURL(this.appletPanel.getDocumentBase(), paramURL))
      throw new SecurityException("ShowDocument url permission denied");
    if (this.instance >= 0)
      Worker.showDocument(this.instance, paramURL, paramString);
  }

  public void doShowStatus(String paramString)
  {
    if (this.instance >= 0)
      Worker.showStatus(this.instance, paramString);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.viewer.context.MNetscape6AppletContext
 * JD-Core Version:    0.6.2
 */