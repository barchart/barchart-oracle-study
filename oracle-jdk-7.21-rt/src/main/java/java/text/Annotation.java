/*    */ package java.text;
/*    */ 
/*    */ public class Annotation
/*    */ {
/*    */   private Object value;
/*    */ 
/*    */   public Annotation(Object paramObject)
/*    */   {
/* 65 */     this.value = paramObject;
/*    */   }
/*    */ 
/*    */   public Object getValue()
/*    */   {
/* 72 */     return this.value;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 79 */     return getClass().getName() + "[value=" + this.value + "]";
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.text.Annotation
 * JD-Core Version:    0.6.2
 */