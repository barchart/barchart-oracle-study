/*     */ package com.sun.naming.internal;
/*     */ 
/*     */ import java.util.List;
/*     */ import javax.naming.NamingException;
/*     */ 
/*     */ public final class FactoryEnumeration
/*     */ {
/*     */   private List factories;
/*  41 */   private int posn = 0;
/*     */   private ClassLoader loader;
/*     */ 
/*     */   FactoryEnumeration(List paramList, ClassLoader paramClassLoader)
/*     */   {
/*  63 */     this.factories = paramList;
/*  64 */     this.loader = paramClassLoader;
/*     */   }
/*     */ 
/*     */   public Object next() throws NamingException {
/*  68 */     synchronized (this.factories)
/*     */     {
/*  70 */       NamedWeakReference localNamedWeakReference = (NamedWeakReference)this.factories.get(this.posn++);
/*  71 */       Object localObject1 = localNamedWeakReference.get();
/*  72 */       if ((localObject1 != null) && (!(localObject1 instanceof Class))) {
/*  73 */         return localObject1;
/*     */       }
/*     */ 
/*  76 */       String str = localNamedWeakReference.getName();
/*     */       try
/*     */       {
/*  79 */         if (localObject1 == null) {
/*  80 */           localObject1 = Class.forName(str, true, this.loader);
/*     */         }
/*     */ 
/*  83 */         localObject1 = ((Class)localObject1).newInstance();
/*  84 */         localNamedWeakReference = new NamedWeakReference(localObject1, str);
/*  85 */         this.factories.set(this.posn - 1, localNamedWeakReference);
/*  86 */         return localObject1;
/*     */       } catch (ClassNotFoundException localClassNotFoundException) {
/*  88 */         localNamingException = new NamingException("No longer able to load " + str);
/*     */ 
/*  90 */         localNamingException.setRootCause(localClassNotFoundException);
/*  91 */         throw localNamingException;
/*     */       } catch (InstantiationException localInstantiationException) {
/*  93 */         localNamingException = new NamingException("Cannot instantiate " + localObject1);
/*     */ 
/*  95 */         localNamingException.setRootCause(localInstantiationException);
/*  96 */         throw localNamingException;
/*     */       } catch (IllegalAccessException localIllegalAccessException) {
/*  98 */         NamingException localNamingException = new NamingException("Cannot access " + localObject1);
/*  99 */         localNamingException.setRootCause(localIllegalAccessException);
/* 100 */         throw localNamingException;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean hasMore() {
/* 106 */     synchronized (this.factories) {
/* 107 */       return this.posn < this.factories.size();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.naming.internal.FactoryEnumeration
 * JD-Core Version:    0.6.2
 */