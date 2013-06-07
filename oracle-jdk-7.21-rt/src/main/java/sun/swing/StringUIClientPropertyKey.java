/*    */ package sun.swing;
/*    */ 
/*    */ public class StringUIClientPropertyKey
/*    */   implements UIClientPropertyKey
/*    */ {
/*    */   private final String key;
/*    */ 
/*    */   public StringUIClientPropertyKey(String paramString)
/*    */   {
/* 38 */     this.key = paramString;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 42 */     return this.key;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.swing.StringUIClientPropertyKey
 * JD-Core Version:    0.6.2
 */