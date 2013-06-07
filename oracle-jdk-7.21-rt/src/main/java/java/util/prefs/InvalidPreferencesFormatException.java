/*    */ package java.util.prefs;
/*    */ 
/*    */ public class InvalidPreferencesFormatException extends Exception
/*    */ {
/*    */   private static final long serialVersionUID = -791715184232119669L;
/*    */ 
/*    */   public InvalidPreferencesFormatException(Throwable paramThrowable)
/*    */   {
/* 49 */     super(paramThrowable);
/*    */   }
/*    */ 
/*    */   public InvalidPreferencesFormatException(String paramString)
/*    */   {
/* 60 */     super(paramString);
/*    */   }
/*    */ 
/*    */   public InvalidPreferencesFormatException(String paramString, Throwable paramThrowable)
/*    */   {
/* 73 */     super(paramString, paramThrowable);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.util.prefs.InvalidPreferencesFormatException
 * JD-Core Version:    0.6.2
 */