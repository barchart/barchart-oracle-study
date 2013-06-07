/*     */ package java.beans;
/*     */ 
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.util.Vector;
/*     */ 
/*     */ class ProxyPersistenceDelegate extends PersistenceDelegate
/*     */ {
/*     */   protected Expression instantiate(Object paramObject, Encoder paramEncoder)
/*     */   {
/* 155 */     Class localClass = paramObject.getClass();
/* 156 */     Proxy localProxy = (Proxy)paramObject;
/*     */ 
/* 159 */     InvocationHandler localInvocationHandler = Proxy.getInvocationHandler(localProxy);
/* 160 */     if ((localInvocationHandler instanceof EventHandler)) {
/* 161 */       EventHandler localEventHandler = (EventHandler)localInvocationHandler;
/* 162 */       Vector localVector = new Vector();
/* 163 */       localVector.add(localClass.getInterfaces()[0]);
/* 164 */       localVector.add(localEventHandler.getTarget());
/* 165 */       localVector.add(localEventHandler.getAction());
/* 166 */       if (localEventHandler.getEventPropertyName() != null) {
/* 167 */         localVector.add(localEventHandler.getEventPropertyName());
/*     */       }
/* 169 */       if (localEventHandler.getListenerMethodName() != null) {
/* 170 */         localVector.setSize(4);
/* 171 */         localVector.add(localEventHandler.getListenerMethodName());
/*     */       }
/* 173 */       return new Expression(paramObject, EventHandler.class, "create", localVector.toArray());
/*     */     }
/*     */ 
/* 178 */     return new Expression(paramObject, Proxy.class, "newProxyInstance", new Object[] { localClass.getClassLoader(), localClass.getInterfaces(), localInvocationHandler });
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.ProxyPersistenceDelegate
 * JD-Core Version:    0.6.2
 */