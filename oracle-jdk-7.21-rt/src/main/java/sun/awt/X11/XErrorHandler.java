/*    */ package sun.awt.X11;
/*    */ 
/*    */ public abstract class XErrorHandler
/*    */ {
/*    */   public abstract int handleError(long paramLong, XErrorEvent paramXErrorEvent);
/*    */ 
/*    */   public static class IgnoreBadWindowHandler extends XErrorHandler.XBaseErrorHandler
/*    */   {
/* 59 */     private static IgnoreBadWindowHandler theInstance = new IgnoreBadWindowHandler();
/*    */ 
/*    */     public int handleError(long paramLong, XErrorEvent paramXErrorEvent)
/*    */     {
/* 53 */       if (paramXErrorEvent.get_error_code() == 3) {
/* 54 */         return 0;
/*    */       }
/* 56 */       return super.handleError(paramLong, paramXErrorEvent);
/*    */     }
/*    */ 
/*    */     public static IgnoreBadWindowHandler getInstance()
/*    */     {
/* 61 */       return theInstance;
/*    */     }
/*    */   }
/*    */ 
/*    */   public static class VerifyChangePropertyHandler extends XErrorHandler.XBaseErrorHandler
/*    */   {
/* 74 */     private static VerifyChangePropertyHandler theInstance = new VerifyChangePropertyHandler();
/*    */ 
/*    */     public int handleError(long paramLong, XErrorEvent paramXErrorEvent)
/*    */     {
/* 68 */       if (paramXErrorEvent.get_request_code() == 18) {
/* 69 */         return 0;
/*    */       }
/* 71 */       return super.handleError(paramLong, paramXErrorEvent);
/*    */     }
/*    */ 
/*    */     public static VerifyChangePropertyHandler getInstance()
/*    */     {
/* 76 */       return theInstance;
/*    */     }
/*    */   }
/*    */ 
/*    */   public static class XBaseErrorHandler extends XErrorHandler
/*    */   {
/*    */     public int handleError(long paramLong, XErrorEvent paramXErrorEvent)
/*    */     {
/* 41 */       return XToolkit.SAVED_ERROR_HANDLER(paramLong, paramXErrorEvent);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XErrorHandler
 * JD-Core Version:    0.6.2
 */