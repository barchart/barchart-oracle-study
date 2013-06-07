/*     */ package sun.org.mozilla.javascript.internal;
/*     */ 
/*     */ import java.lang.reflect.Member;
/*     */ import java.util.Iterator;
/*     */ 
/*     */ public abstract class VMBridge
/*     */ {
/*  49 */   static final VMBridge instance = makeInstance();
/*     */ 
/*     */   private static VMBridge makeInstance()
/*     */   {
/*  53 */     String[] arrayOfString = { "sun.org.mozilla.javascript.internal.VMBridge_custom", "sun.org.mozilla.javascript.internal.jdk15.VMBridge_jdk15", "sun.org.mozilla.javascript.internal.jdk13.VMBridge_jdk13", "sun.org.mozilla.javascript.internal.jdk11.VMBridge_jdk11" };
/*     */ 
/*  59 */     for (int i = 0; i != arrayOfString.length; i++) {
/*  60 */       String str = arrayOfString[i];
/*  61 */       Class localClass = Kit.classOrNull(str);
/*  62 */       if (localClass != null) {
/*  63 */         VMBridge localVMBridge = (VMBridge)Kit.newInstanceOrNull(localClass);
/*  64 */         if (localVMBridge != null) {
/*  65 */           return localVMBridge;
/*     */         }
/*     */       }
/*     */     }
/*  69 */     throw new IllegalStateException("Failed to create VMBridge instance");
/*     */   }
/*     */ 
/*     */   protected abstract Object getThreadContextHelper();
/*     */ 
/*     */   protected abstract Context getContext(Object paramObject);
/*     */ 
/*     */   protected abstract void setContext(Object paramObject, Context paramContext);
/*     */ 
/*     */   protected abstract ClassLoader getCurrentThreadClassLoader();
/*     */ 
/*     */   protected abstract boolean tryToMakeAccessible(Object paramObject);
/*     */ 
/*     */   protected Object getInterfaceProxyHelper(ContextFactory paramContextFactory, Class<?>[] paramArrayOfClass)
/*     */   {
/* 133 */     throw Context.reportRuntimeError("VMBridge.getInterfaceProxyHelper is not supported");
/*     */   }
/*     */ 
/*     */   protected Object newInterfaceProxy(Object paramObject1, ContextFactory paramContextFactory, InterfaceAdapter paramInterfaceAdapter, Object paramObject2, Scriptable paramScriptable)
/*     */   {
/* 156 */     throw Context.reportRuntimeError("VMBridge.newInterfaceProxy is not supported");
/*     */   }
/*     */ 
/*     */   protected abstract boolean isVarArgs(Member paramMember);
/*     */ 
/*     */   public Iterator<?> getJavaIterator(Context paramContext, Scriptable paramScriptable, Object paramObject)
/*     */   {
/* 174 */     if ((paramObject instanceof Wrapper)) {
/* 175 */       Object localObject = ((Wrapper)paramObject).unwrap();
/* 176 */       Iterator localIterator = null;
/* 177 */       if ((localObject instanceof Iterator))
/* 178 */         localIterator = (Iterator)localObject;
/* 179 */       return localIterator;
/*     */     }
/* 181 */     return null;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.org.mozilla.javascript.internal.VMBridge
 * JD-Core Version:    0.6.2
 */