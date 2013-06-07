/*    */ package sun.reflect.generics.repository;
/*    */ 
/*    */ import sun.reflect.generics.factory.GenericsFactory;
/*    */ import sun.reflect.generics.tree.Tree;
/*    */ import sun.reflect.generics.visitor.Reifier;
/*    */ 
/*    */ public abstract class AbstractRepository<T extends Tree>
/*    */ {
/*    */   private GenericsFactory factory;
/*    */   private T tree;
/*    */ 
/*    */   private GenericsFactory getFactory()
/*    */   {
/* 48 */     return this.factory;
/*    */   }
/*    */ 
/*    */   protected T getTree()
/*    */   {
/* 54 */     return this.tree;
/*    */   }
/*    */ 
/*    */   protected Reifier getReifier()
/*    */   {
/* 62 */     return Reifier.make(getFactory());
/*    */   }
/*    */ 
/*    */   protected AbstractRepository(String paramString, GenericsFactory paramGenericsFactory)
/*    */   {
/* 74 */     this.tree = parse(paramString);
/* 75 */     this.factory = paramGenericsFactory;
/*    */   }
/*    */ 
/*    */   protected abstract T parse(String paramString);
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.reflect.generics.repository.AbstractRepository
 * JD-Core Version:    0.6.2
 */