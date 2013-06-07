/*    */ package sun.org.mozilla.javascript.internal;
/*    */ 
/*    */ public class Undefined
/*    */ {
/* 46 */   public static final Object instance = new Undefined();
/*    */ 
/*    */   public Object readResolve()
/*    */   {
/* 54 */     return instance;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.org.mozilla.javascript.internal.Undefined
 * JD-Core Version:    0.6.2
 */