/*    */ package javax.security.auth.callback;
/*    */ 
/*    */ public class UnsupportedCallbackException extends Exception
/*    */ {
/*    */   private static final long serialVersionUID = -6873556327655666839L;
/*    */   private Callback callback;
/*    */ 
/*    */   public UnsupportedCallbackException(Callback paramCallback)
/*    */   {
/* 52 */     this.callback = paramCallback;
/*    */   }
/*    */ 
/*    */   public UnsupportedCallbackException(Callback paramCallback, String paramString)
/*    */   {
/* 67 */     super(paramString);
/* 68 */     this.callback = paramCallback;
/*    */   }
/*    */ 
/*    */   public Callback getCallback()
/*    */   {
/* 79 */     return this.callback;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.security.auth.callback.UnsupportedCallbackException
 * JD-Core Version:    0.6.2
 */