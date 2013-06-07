/*    */ package javax.management;
/*    */ 
/*    */ public class BadAttributeValueExpException extends Exception
/*    */ {
/*    */   private static final long serialVersionUID = -3105272988410493376L;
/*    */   private Object val;
/*    */ 
/*    */   public BadAttributeValueExpException(Object paramObject)
/*    */   {
/* 54 */     this.val = paramObject;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 62 */     return "BadAttributeValueException: " + this.val;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.management.BadAttributeValueExpException
 * JD-Core Version:    0.6.2
 */