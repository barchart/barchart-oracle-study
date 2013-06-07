/*    */ package sun.applet;
/*    */ 
/*    */ public class AppletIllegalArgumentException extends IllegalArgumentException
/*    */ {
/* 35 */   private String key = null;
/*    */ 
/* 47 */   private static AppletMessageHandler amh = new AppletMessageHandler("appletillegalargumentexception");
/*    */ 
/*    */   public AppletIllegalArgumentException(String paramString)
/*    */   {
/* 38 */     super(paramString);
/* 39 */     this.key = paramString;
/*    */   }
/*    */ 
/*    */   public String getLocalizedMessage()
/*    */   {
/* 44 */     return amh.getMessage(this.key);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.applet.AppletIllegalArgumentException
 * JD-Core Version:    0.6.2
 */