/*    */ package sun.reflect.generics.tree;
/*    */ 
/*    */ import sun.reflect.generics.visitor.TypeTreeVisitor;
/*    */ 
/*    */ public class CharSignature
/*    */   implements BaseType
/*    */ {
/* 32 */   private static CharSignature singleton = new CharSignature();
/*    */ 
/*    */   public static CharSignature make()
/*    */   {
/* 36 */     return singleton;
/*    */   }
/*    */   public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor) {
/* 39 */     paramTypeTreeVisitor.visitCharSignature(this);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.reflect.generics.tree.CharSignature
 * JD-Core Version:    0.6.2
 */