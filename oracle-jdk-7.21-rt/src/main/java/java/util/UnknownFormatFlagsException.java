/*    */ package java.util;
/*    */ 
/*    */ public class UnknownFormatFlagsException extends IllegalFormatException
/*    */ {
/*    */   private static final long serialVersionUID = 19370506L;
/*    */   private String flags;
/*    */ 
/*    */   public UnknownFormatFlagsException(String paramString)
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
/*    */   public String getMessage()
/*    */   {
/* 66 */     return "Flags = " + this.flags;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.util.UnknownFormatFlagsException
 * JD-Core Version:    0.6.2
 */