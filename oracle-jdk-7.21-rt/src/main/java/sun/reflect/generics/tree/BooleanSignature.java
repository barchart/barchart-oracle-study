/*    */ package sun.reflect.generics.tree;
/*    */ 
/*    */ import sun.reflect.generics.visitor.TypeTreeVisitor;
/*    */ 
/*    */ public class BooleanSignature
/*    */   implements BaseType
/*    */ {
/* 32 */   private static BooleanSignature singleton = new BooleanSignature();
/*    */ 
/*    */   public static BooleanSignature make()
/*    */   {
/* 36 */     return singleton;
/*    */   }
/*    */   public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor) {
/* 39 */     paramTypeTreeVisitor.visitBooleanSignature(this);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.reflect.generics.tree.BooleanSignature
 * JD-Core Version:    0.6.2
 */