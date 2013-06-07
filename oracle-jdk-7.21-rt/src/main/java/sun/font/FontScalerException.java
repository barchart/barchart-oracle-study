/*    */ package sun.font;
/*    */ 
/*    */ public class FontScalerException extends Exception
/*    */ {
/*    */   public FontScalerException()
/*    */   {
/* 30 */     super("Font scaler encountered runtime problem.");
/*    */   }
/*    */ 
/*    */   public FontScalerException(String paramString) {
/* 34 */     super(paramString);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.font.FontScalerException
 * JD-Core Version:    0.6.2
 */