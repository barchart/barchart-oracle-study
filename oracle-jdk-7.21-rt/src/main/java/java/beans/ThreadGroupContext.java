/*     */ package java.beans;
/*     */ 
/*     */ import com.sun.beans.finder.BeanInfoFinder;
/*     */ import com.sun.beans.finder.PropertyEditorFinder;
/*     */ import java.awt.GraphicsEnvironment;
/*     */ import java.util.Map;
/*     */ import java.util.WeakHashMap;
/*     */ 
/*     */ final class ThreadGroupContext
/*     */ {
/*  44 */   private static final WeakIdentityMap<ThreadGroupContext> contexts = new WeakIdentityMap();
/*     */   private volatile boolean isDesignTime;
/*     */   private volatile Boolean isGuiAvailable;
/*     */   private Map<Class<?>, BeanInfo> beanInfoCache;
/*     */   private BeanInfoFinder beanInfoFinder;
/*     */   private PropertyEditorFinder propertyEditorFinder;
/*     */ 
/*     */   static ThreadGroupContext getContext()
/*     */   {
/*  53 */     ThreadGroup localThreadGroup = Thread.currentThread().getThreadGroup();
/*  54 */     synchronized (contexts) {
/*  55 */       ThreadGroupContext localThreadGroupContext = (ThreadGroupContext)contexts.get(localThreadGroup);
/*  56 */       if (localThreadGroupContext == null) {
/*  57 */         localThreadGroupContext = new ThreadGroupContext();
/*  58 */         contexts.put(localThreadGroup, localThreadGroupContext);
/*     */       }
/*  60 */       return localThreadGroupContext;
/*     */     }
/*     */   }
/*     */ 
/*     */   boolean isDesignTime()
/*     */   {
/*  75 */     return this.isDesignTime;
/*     */   }
/*     */ 
/*     */   void setDesignTime(boolean paramBoolean) {
/*  79 */     this.isDesignTime = paramBoolean;
/*     */   }
/*     */ 
/*     */   boolean isGuiAvailable()
/*     */   {
/*  84 */     Boolean localBoolean = this.isGuiAvailable;
/*  85 */     return !GraphicsEnvironment.isHeadless() ? true : localBoolean != null ? localBoolean.booleanValue() : false;
/*     */   }
/*     */ 
/*     */   void setGuiAvailable(boolean paramBoolean)
/*     */   {
/*  91 */     this.isGuiAvailable = Boolean.valueOf(paramBoolean);
/*     */   }
/*     */ 
/*     */   BeanInfo getBeanInfo(Class<?> paramClass)
/*     */   {
/*  96 */     return this.beanInfoCache != null ? (BeanInfo)this.beanInfoCache.get(paramClass) : null;
/*     */   }
/*     */ 
/*     */   BeanInfo putBeanInfo(Class<?> paramClass, BeanInfo paramBeanInfo)
/*     */   {
/* 102 */     if (this.beanInfoCache == null) {
/* 103 */       this.beanInfoCache = new WeakHashMap();
/*     */     }
/* 105 */     return (BeanInfo)this.beanInfoCache.put(paramClass, paramBeanInfo);
/*     */   }
/*     */ 
/*     */   void removeBeanInfo(Class<?> paramClass) {
/* 109 */     if (this.beanInfoCache != null)
/* 110 */       this.beanInfoCache.remove(paramClass);
/*     */   }
/*     */ 
/*     */   void clearBeanInfoCache()
/*     */   {
/* 115 */     if (this.beanInfoCache != null)
/* 116 */       this.beanInfoCache.clear();
/*     */   }
/*     */ 
/*     */   synchronized BeanInfoFinder getBeanInfoFinder()
/*     */   {
/* 122 */     if (this.beanInfoFinder == null) {
/* 123 */       this.beanInfoFinder = new BeanInfoFinder();
/*     */     }
/* 125 */     return this.beanInfoFinder;
/*     */   }
/*     */ 
/*     */   synchronized PropertyEditorFinder getPropertyEditorFinder() {
/* 129 */     if (this.propertyEditorFinder == null) {
/* 130 */       this.propertyEditorFinder = new PropertyEditorFinder();
/*     */     }
/* 132 */     return this.propertyEditorFinder;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.ThreadGroupContext
 * JD-Core Version:    0.6.2
 */