/*    */ package java.util;
/*    */ 
/*    */ public class IllegalFormatFlagsException extends IllegalFormatException
/*    */ {
/*    */   private static final long serialVersionUID = 790824L;
/*    */   private String flags;
/*    */ 
/*    */   public IllegalFormatFlagsException(String paramString)
/*    */   {
/* 50 */     if (paramString == null)
/* 51 */       throw new NullPointerException();
/* 52 */     this.flags = paramString;
/*    */   }
/*    */ 
/*    */   public String getFlags()
/*    */   {
/* 61 */     return this.flags;
/*    */   }
/*    */ 
/*    */   public String getMessage() {
/* 65 */     return "Flags = '" + this.flags + "'";
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.util.IllegalFormatFlagsException
 * JD-Core Version:    0.6.2
 */