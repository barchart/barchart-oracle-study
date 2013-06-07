/*    */ package java.lang;
/*    */ 
/*    */ public class ArrayIndexOutOfBoundsException extends IndexOutOfBoundsException
/*    */ {
/*    */   private static final long serialVersionUID = -5116101128118950844L;
/*    */ 
/*    */   public ArrayIndexOutOfBoundsException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public ArrayIndexOutOfBoundsException(int paramInt)
/*    */   {
/* 55 */     super("Array index out of range: " + paramInt);
/*    */   }
/*    */ 
/*    */   public ArrayIndexOutOfBoundsException(String paramString)
/*    */   {
/* 65 */     super(paramString);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.ArrayIndexOutOfBoundsException
 * JD-Core Version:    0.6.2
 */