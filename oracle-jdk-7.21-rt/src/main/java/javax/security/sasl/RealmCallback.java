/*    */ package javax.security.sasl;
/*    */ 
/*    */ import javax.security.auth.callback.TextInputCallback;
/*    */ 
/*    */ public class RealmCallback extends TextInputCallback
/*    */ {
/*    */   private static final long serialVersionUID = -4342673378785456908L;
/*    */ 
/*    */   public RealmCallback(String paramString)
/*    */   {
/* 49 */     super(paramString);
/*    */   }
/*    */ 
/*    */   public RealmCallback(String paramString1, String paramString2)
/*    */   {
/* 63 */     super(paramString1, paramString2);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.security.sasl.RealmCallback
 * JD-Core Version:    0.6.2
 */