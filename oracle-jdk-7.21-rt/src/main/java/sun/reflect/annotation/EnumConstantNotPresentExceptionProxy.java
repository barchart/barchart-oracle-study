/*    */ package sun.reflect.annotation;
/*    */ 
/*    */ public class EnumConstantNotPresentExceptionProxy extends ExceptionProxy
/*    */ {
/*    */   private static final long serialVersionUID = -604662101303187330L;
/*    */   Class<? extends Enum> enumType;
/*    */   String constName;
/*    */ 
/*    */   public EnumConstantNotPresentExceptionProxy(Class<? extends Enum> paramClass, String paramString)
/*    */   {
/* 42 */     this.enumType = paramClass;
/* 43 */     this.constName = paramString;
/*    */   }
/*    */ 
/*    */   protected RuntimeException generateException() {
/* 47 */     return new EnumConstantNotPresentException(this.enumType, this.constName);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.reflect.annotation.EnumConstantNotPresentExceptionProxy
 * JD-Core Version:    0.6.2
 */