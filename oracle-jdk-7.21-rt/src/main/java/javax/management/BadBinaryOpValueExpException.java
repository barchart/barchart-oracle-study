/*    */ package javax.management;
/*    */ 
/*    */ public class BadBinaryOpValueExpException extends Exception
/*    */ {
/*    */   private static final long serialVersionUID = 5068475589449021227L;
/*    */   private ValueExp exp;
/*    */ 
/*    */   public BadBinaryOpValueExpException(ValueExp paramValueExp)
/*    */   {
/* 55 */     this.exp = paramValueExp;
/*    */   }
/*    */ 
/*    */   public ValueExp getExp()
/*    */   {
/* 65 */     return this.exp;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 72 */     return "BadBinaryOpValueExpException: " + this.exp;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.management.BadBinaryOpValueExpException
 * JD-Core Version:    0.6.2
 */