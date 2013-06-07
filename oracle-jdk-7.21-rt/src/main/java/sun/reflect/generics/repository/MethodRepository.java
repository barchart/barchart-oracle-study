/*    */ package sun.reflect.generics.repository;
/*    */ 
/*    */ import java.lang.reflect.Type;
/*    */ import sun.reflect.generics.factory.GenericsFactory;
/*    */ import sun.reflect.generics.tree.MethodTypeSignature;
/*    */ import sun.reflect.generics.tree.ReturnType;
/*    */ import sun.reflect.generics.visitor.Reifier;
/*    */ 
/*    */ public class MethodRepository extends ConstructorRepository
/*    */ {
/*    */   private Type returnType;
/*    */ 
/*    */   private MethodRepository(String paramString, GenericsFactory paramGenericsFactory)
/*    */   {
/* 46 */     super(paramString, paramGenericsFactory);
/*    */   }
/*    */ 
/*    */   public static MethodRepository make(String paramString, GenericsFactory paramGenericsFactory)
/*    */   {
/* 59 */     return new MethodRepository(paramString, paramGenericsFactory);
/*    */   }
/*    */ 
/*    */   public Type getReturnType()
/*    */   {
/* 65 */     if (this.returnType == null) {
/* 66 */       Reifier localReifier = getReifier();
/*    */ 
/* 68 */       ((MethodTypeSignature)getTree()).getReturnType().accept(localReifier);
/*    */ 
/* 70 */       this.returnType = localReifier.getResult();
/*    */     }
/* 72 */     return this.returnType;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.reflect.generics.repository.MethodRepository
 * JD-Core Version:    0.6.2
 */