/*    */ package java.nio.charset;
/*    */ 
/*    */ public class UnsupportedCharsetException extends IllegalArgumentException
/*    */ {
/*    */   private static final long serialVersionUID = 1490765524727386367L;
/*    */   private String charsetName;
/*    */ 
/*    */   public UnsupportedCharsetException(String paramString)
/*    */   {
/* 55 */     super(String.valueOf(paramString));
/* 56 */     this.charsetName = paramString;
/*    */   }
/*    */ 
/*    */   public String getCharsetName()
/*    */   {
/* 65 */     return this.charsetName;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.nio.charset.UnsupportedCharsetException
 * JD-Core Version:    0.6.2
 */