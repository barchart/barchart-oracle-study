/*    */ package sun.reflect;
/*    */ 
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ 
/*    */ class DelegatingConstructorAccessorImpl extends ConstructorAccessorImpl
/*    */ {
/*    */   private ConstructorAccessorImpl delegate;
/*    */ 
/*    */   DelegatingConstructorAccessorImpl(ConstructorAccessorImpl paramConstructorAccessorImpl)
/*    */   {
/* 37 */     setDelegate(paramConstructorAccessorImpl);
/*    */   }
/*    */ 
/*    */   public Object newInstance(Object[] paramArrayOfObject)
/*    */     throws InstantiationException, IllegalArgumentException, InvocationTargetException
/*    */   {
/* 45 */     return this.delegate.newInstance(paramArrayOfObject);
/*    */   }
/*    */ 
/*    */   void setDelegate(ConstructorAccessorImpl paramConstructorAccessorImpl) {
/* 49 */     this.delegate = paramConstructorAccessorImpl;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.reflect.DelegatingConstructorAccessorImpl
 * JD-Core Version:    0.6.2
 */