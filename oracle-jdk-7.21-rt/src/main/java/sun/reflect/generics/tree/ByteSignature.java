/*    */ package sun.reflect.generics.tree;
/*    */ 
/*    */ import sun.reflect.generics.visitor.TypeTreeVisitor;
/*    */ 
/*    */ public class ByteSignature
/*    */   implements BaseType
/*    */ {
/* 32 */   private static ByteSignature singleton = new ByteSignature();
/*    */ 
/*    */   public static ByteSignature make()
/*    */   {
/* 36 */     return singleton;
/*    */   }
/*    */   public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor) {
/* 39 */     paramTypeTreeVisitor.visitByteSignature(this);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.reflect.generics.tree.ByteSignature
 * JD-Core Version:    0.6.2
 */