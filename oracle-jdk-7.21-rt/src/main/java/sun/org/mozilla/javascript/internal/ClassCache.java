/*     */ package sun.org.mozilla.javascript.internal;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class ClassCache
/*     */ {
/*  55 */   private static final Object AKEY = "ClassCache";
/*  56 */   private volatile boolean cachingIsEnabled = true;
/*     */   private transient HashMap<Class<?>, JavaMembers> classTable;
/*     */   private transient HashMap<JavaAdapter.JavaAdapterSignature, Class<?>> classAdapterCache;
/*     */   private transient HashMap<Class<?>, Object> interfaceAdapterCache;
/*     */   private int generatedClassSerial;
/*     */   private Scriptable associatedScope;
/*     */ 
/*     */   public static ClassCache get(Scriptable paramScriptable)
/*     */   {
/*  78 */     ClassCache localClassCache = (ClassCache)ScriptableObject.getTopScopeValue(paramScriptable, AKEY);
/*     */ 
/*  80 */     if (localClassCache == null) {
/*  81 */       throw new RuntimeException("Can't find top level scope for ClassCache.get");
/*     */     }
/*     */ 
/*  84 */     return localClassCache;
/*     */   }
/*     */ 
/*     */   public boolean associate(ScriptableObject paramScriptableObject)
/*     */   {
/* 100 */     if (paramScriptableObject.getParentScope() != null)
/*     */     {
/* 102 */       throw new IllegalArgumentException();
/*     */     }
/* 104 */     if (this == paramScriptableObject.associateValue(AKEY, this)) {
/* 105 */       this.associatedScope = paramScriptableObject;
/* 106 */       return true;
/*     */     }
/* 108 */     return false;
/*     */   }
/*     */ 
/*     */   public synchronized void clearCaches()
/*     */   {
/* 116 */     this.classTable = null;
/* 117 */     this.classAdapterCache = null;
/* 118 */     this.interfaceAdapterCache = null;
/*     */   }
/*     */ 
/*     */   public final boolean isCachingEnabled()
/*     */   {
/* 127 */     return this.cachingIsEnabled;
/*     */   }
/*     */ 
/*     */   public synchronized void setCachingEnabled(boolean paramBoolean)
/*     */   {
/* 150 */     if (paramBoolean == this.cachingIsEnabled)
/* 151 */       return;
/* 152 */     if (!paramBoolean)
/* 153 */       clearCaches();
/* 154 */     this.cachingIsEnabled = paramBoolean;
/*     */   }
/*     */ 
/*     */   Map<Class<?>, JavaMembers> getClassCacheMap()
/*     */   {
/* 161 */     if (this.classTable == null) {
/* 162 */       this.classTable = new HashMap();
/*     */     }
/* 164 */     return this.classTable;
/*     */   }
/*     */ 
/*     */   Map<JavaAdapter.JavaAdapterSignature, Class<?>> getInterfaceAdapterCacheMap()
/*     */   {
/* 169 */     if (this.classAdapterCache == null) {
/* 170 */       this.classAdapterCache = new HashMap();
/*     */     }
/* 172 */     return this.classAdapterCache;
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public boolean isInvokerOptimizationEnabled()
/*     */   {
/* 182 */     return false;
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public synchronized void setInvokerOptimizationEnabled(boolean paramBoolean)
/*     */   {
/*     */   }
/*     */ 
/*     */   public final synchronized int newClassSerialNumber()
/*     */   {
/* 204 */     return ++this.generatedClassSerial;
/*     */   }
/*     */ 
/*     */   Object getInterfaceAdapter(Class<?> paramClass)
/*     */   {
/* 209 */     return this.interfaceAdapterCache == null ? null : this.interfaceAdapterCache.get(paramClass);
/*     */   }
/*     */ 
/*     */   synchronized void cacheInterfaceAdapter(Class<?> paramClass, Object paramObject)
/*     */   {
/* 216 */     if (this.cachingIsEnabled) {
/* 217 */       if (this.interfaceAdapterCache == null) {
/* 218 */         this.interfaceAdapterCache = new HashMap();
/*     */       }
/* 220 */       this.interfaceAdapterCache.put(paramClass, paramObject);
/*     */     }
/*     */   }
/*     */ 
/*     */   Scriptable getAssociatedScope() {
/* 225 */     return this.associatedScope;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.org.mozilla.javascript.internal.ClassCache
 * JD-Core Version:    0.6.2
 */