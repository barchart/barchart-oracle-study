/*    */ package java.text;
/*    */ 
/*    */ public class ParseException extends Exception
/*    */ {
/*    */   private int errorOffset;
/*    */ 
/*    */   public ParseException(String paramString, int paramInt)
/*    */   {
/* 60 */     super(paramString);
/* 61 */     this.errorOffset = paramInt;
/*    */   }
/*    */ 
/*    */   public int getErrorOffset()
/*    */   {
/* 68 */     return this.errorOffset;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.text.ParseException
 * JD-Core Version:    0.6.2
 */