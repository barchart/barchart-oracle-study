/*    */ package sun.nio.ch;
/*    */ 
/*    */ import java.nio.channels.spi.AsynchronousChannelProvider;
/*    */ import java.security.AccessController;
/*    */ import sun.security.action.GetPropertyAction;
/*    */ 
/*    */ public class DefaultAsynchronousChannelProvider
/*    */ {
/*    */   public static AsynchronousChannelProvider create()
/*    */   {
/* 47 */     String str = (String)AccessController.doPrivileged(new GetPropertyAction("os.name"));
/*    */ 
/* 49 */     if (str.equals("SunOS"))
/* 50 */       return new SolarisAsynchronousChannelProvider();
/* 51 */     if (str.equals("Linux"))
/* 52 */       return new LinuxAsynchronousChannelProvider();
/* 53 */     if (str.contains("OS X"))
/* 54 */       return new BsdAsynchronousChannelProvider();
/* 55 */     throw new InternalError("platform not recognized");
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.DefaultAsynchronousChannelProvider
 * JD-Core Version:    0.6.2
 */