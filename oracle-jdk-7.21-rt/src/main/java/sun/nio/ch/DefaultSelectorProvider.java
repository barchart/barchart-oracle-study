/*    */ package sun.nio.ch;
/*    */ 
/*    */ import java.nio.channels.spi.SelectorProvider;
/*    */ import java.security.AccessController;
/*    */ import sun.security.action.GetPropertyAction;
/*    */ 
/*    */ public class DefaultSelectorProvider
/*    */ {
/*    */   public static SelectorProvider create()
/*    */   {
/* 48 */     String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("os.name"));
/*    */ 
/* 50 */     if ("SunOS".equals(str1)) {
/* 51 */       return new DevPollSelectorProvider();
/*    */     }
/*    */ 
/* 55 */     if ("Linux".equals(str1)) {
/* 56 */       String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("os.version"));
/*    */ 
/* 58 */       String[] arrayOfString = str2.split("\\.", 0);
/* 59 */       if (arrayOfString.length >= 2) {
/*    */         try {
/* 61 */           int i = Integer.parseInt(arrayOfString[0]);
/* 62 */           int j = Integer.parseInt(arrayOfString[1]);
/* 63 */           if ((i > 2) || ((i == 2) && (j >= 6))) {
/* 64 */             return new EPollSelectorProvider();
/*    */           }
/*    */         }
/*    */         catch (NumberFormatException localNumberFormatException)
/*    */         {
/*    */         }
/*    */       }
/*    */     }
/* 72 */     return new PollSelectorProvider();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.DefaultSelectorProvider
 * JD-Core Version:    0.6.2
 */