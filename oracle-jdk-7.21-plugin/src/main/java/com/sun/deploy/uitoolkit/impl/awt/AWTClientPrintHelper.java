package com.sun.deploy.uitoolkit.impl.awt;

import com.sun.deploy.trace.Trace;
import com.sun.deploy.uitoolkit.Window;
import java.applet.Applet;
import java.lang.reflect.Method;
import sun.plugin2.applet.Plugin2Manager;
import sun.plugin2.main.client.PluginEmbeddedFrame;
import sun.plugin2.main.client.PrintBandDescriptor;
import sun.plugin2.message.Conversation;
import sun.plugin2.message.Pipe;
import sun.plugin2.message.PrintBandMessage;
import sun.plugin2.message.PrintBandReplyMessage;
import sun.plugin2.util.SystemUtil;

public class AWTClientPrintHelper
{
  static final Method print;

  private static boolean win32Print(Plugin2Manager paramPlugin2Manager, PluginEmbeddedFrame paramPluginEmbeddedFrame, Pipe paramPipe, int paramInt, long paramLong, boolean paramBoolean)
  {
    boolean bool1 = false;
    PrintBandReplyMessage localPrintBandReplyMessage = null;
    PrintBandDescriptor localPrintBandDescriptor = null;
    boolean bool2 = false;
    Conversation localConversation = paramPipe.beginConversation();
    try
    {
      do
      {
        if (!bool2)
        {
          Object[] arrayOfObject = new Object[2];
          arrayOfObject[0] = localPrintBandDescriptor;
          arrayOfObject[1] = Boolean.valueOf(paramBoolean);
          localPrintBandDescriptor = (PrintBandDescriptor)print.invoke(paramPluginEmbeddedFrame, arrayOfObject);
          if (localPrintBandDescriptor == null)
            break;
          bool2 = localPrintBandDescriptor.isLastBand();
        }
        paramPipe.send(new PrintBandMessage(localConversation, paramInt, paramLong, localPrintBandDescriptor.getData(), localPrintBandDescriptor.getOffset(), localPrintBandDescriptor.getSrcX(), localPrintBandDescriptor.getSrcY(), localPrintBandDescriptor.getSrcWidth(), localPrintBandDescriptor.getSrcHeight(), localPrintBandDescriptor.getDestX(), localPrintBandDescriptor.getDestY(), localPrintBandDescriptor.getDestWidth(), localPrintBandDescriptor.getDestHeight()));
        localPrintBandReplyMessage = (PrintBandReplyMessage)paramPipe.receive(0L, localConversation);
        if (localPrintBandReplyMessage != null)
        {
          if ((localPrintBandReplyMessage.getAppletID() != paramInt) && (localPrintBandReplyMessage.getDestY() != localPrintBandDescriptor.getDestY()))
            break;
          bool1 = localPrintBandReplyMessage.getRes();
        }
        if (bool2)
        {
          bool1 = true;
          break;
        }
        if (localPrintBandDescriptor == null)
          break;
      }
      while (paramPlugin2Manager.isAppletStarted());
    }
    catch (Throwable localThrowable)
    {
      Trace.ignored(localThrowable);
    }
    finally
    {
      paramPipe.endConversation(localConversation);
    }
    return bool1;
  }

  private static boolean unixPrint(PluginEmbeddedFrame paramPluginEmbeddedFrame, Pipe paramPipe, Applet paramApplet, int paramInt1, long paramLong, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    boolean bool = false;
    PrintBandReplyMessage localPrintBandReplyMessage = null;
    byte[] arrayOfByte = null;
    Conversation localConversation = paramPipe.beginConversation();
    try
    {
      Object[] arrayOfObject = new Object[5];
      arrayOfObject[0] = paramApplet;
      arrayOfObject[1] = new Integer(paramInt2);
      arrayOfObject[2] = new Integer(paramInt3);
      arrayOfObject[3] = new Integer(paramInt4);
      arrayOfObject[4] = new Integer(paramInt5);
      arrayOfByte = (byte[])print.invoke(paramPluginEmbeddedFrame, arrayOfObject);
      paramPipe.send(new PrintBandMessage(localConversation, paramInt1, paramLong, arrayOfByte, 0, 0, 0, 0, 0, 0, 0, 0, 0));
      localPrintBandReplyMessage = (PrintBandReplyMessage)paramPipe.receive(0L, localConversation);
      if (localPrintBandReplyMessage != null)
        bool = localPrintBandReplyMessage.getAppletID() != paramInt1 ? false : localPrintBandReplyMessage.getRes();
    }
    catch (Throwable localThrowable)
    {
      Trace.ignored(localThrowable);
    }
    finally
    {
      paramPipe.endConversation(localConversation);
    }
    return bool;
  }

  public static boolean print(Plugin2Manager paramPlugin2Manager, int paramInt1, Pipe paramPipe, long paramLong, boolean paramBoolean, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    if (null == paramPlugin2Manager)
      return false;
    if (null == paramPlugin2Manager.getAppletParent())
      return false;
    if (!paramPlugin2Manager.isAppletStarted())
      return false;
    Pipe localPipe = paramPipe;
    if (localPipe == null)
      return false;
    PluginEmbeddedFrame localPluginEmbeddedFrame = (PluginEmbeddedFrame)paramPlugin2Manager.getAppletParent().getWindowObject();
    switch (SystemUtil.getOSType())
    {
    case 1:
      return win32Print(paramPlugin2Manager, localPluginEmbeddedFrame, localPipe, paramInt1, paramLong, paramBoolean);
    case 2:
      AWTAppletAdapter localAWTAppletAdapter = (AWTAppletAdapter)paramPlugin2Manager.getApplet2Adapter();
      return unixPrint(localPluginEmbeddedFrame, localPipe, localAWTAppletAdapter.getApplet(), paramInt1, paramLong, paramInt2, paramInt3, paramInt4, paramInt5);
    }
    return false;
  }

  static
  {
    Method localMethod = null;
    try
    {
      Class[] arrayOfClass;
      switch (SystemUtil.getOSType())
      {
      case 1:
        arrayOfClass = new Class[] { PrintBandDescriptor.class, Boolean.TYPE };
        localMethod = PluginEmbeddedFrame.class.getMethod("printPlugin", arrayOfClass);
        break;
      case 2:
        arrayOfClass = new Class[] { Applet.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE };
        localMethod = PluginEmbeddedFrame.class.getMethod("printPlugin", arrayOfClass);
      }
    }
    catch (Exception localException)
    {
      Trace.ignoredException(localException);
      localMethod = null;
    }
    finally
    {
      print = localMethod;
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     com.sun.deploy.uitoolkit.impl.awt.AWTClientPrintHelper
 * JD-Core Version:    0.6.2
 */