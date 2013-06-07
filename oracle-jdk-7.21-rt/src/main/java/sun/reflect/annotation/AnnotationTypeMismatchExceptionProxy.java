/*    */ package sun.reflect.annotation;
/*    */ 
/*    */ import java.lang.annotation.AnnotationTypeMismatchException;
/*    */ import java.lang.reflect.Method;
/*    */ 
/*    */ class AnnotationTypeMismatchExceptionProxy extends ExceptionProxy
/*    */ {
/*    */   private static final long serialVersionUID = 7844069490309503934L;
/*    */   private Method member;
/*    */   private String foundType;
/*    */ 
/*    */   AnnotationTypeMismatchExceptionProxy(String paramString)
/*    */   {
/* 48 */     this.foundType = paramString;
/*    */   }
/*    */ 
/*    */   AnnotationTypeMismatchExceptionProxy setMember(Method paramMethod) {
/* 52 */     this.member = paramMethod;
/* 53 */     return this;
/*    */   }
/*    */ 
/*    */   protected RuntimeException generateException() {
/* 57 */     return new AnnotationTypeMismatchException(this.member, this.foundType);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.reflect.annotation.AnnotationTypeMismatchExceptionProxy
 * JD-Core Version:    0.6.2
 */