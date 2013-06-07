/*     */ package sun.org.mozilla.javascript.internal;
/*     */ 
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import sun.org.mozilla.javascript.internal.xml.XMLLib.Factory;
/*     */ 
/*     */ public class ContextFactory
/*     */ {
/*     */   private static volatile boolean hasCustomGlobal;
/* 145 */   private static ContextFactory global = new ContextFactory();
/*     */   private volatile boolean sealed;
/*     */   private final Object listenersLock;
/*     */   private volatile Object listeners;
/*     */   private boolean disabledListening;
/*     */   private ClassLoader applicationClassLoader;
/*     */ 
/*     */   public ContextFactory()
/*     */   {
/* 149 */     this.listenersLock = new Object();
/*     */   }
/*     */ 
/*     */   public static ContextFactory getGlobal()
/*     */   {
/* 179 */     return global;
/*     */   }
/*     */ 
/*     */   public static boolean hasExplicitGlobal()
/*     */   {
/* 193 */     return hasCustomGlobal;
/*     */   }
/*     */ 
/*     */   public static synchronized void initGlobal(ContextFactory paramContextFactory)
/*     */   {
/* 205 */     if (paramContextFactory == null) {
/* 206 */       throw new IllegalArgumentException();
/*     */     }
/* 208 */     if (hasCustomGlobal) {
/* 209 */       throw new IllegalStateException();
/*     */     }
/* 211 */     hasCustomGlobal = true;
/* 212 */     global = paramContextFactory;
/*     */   }
/*     */ 
/*     */   public static synchronized GlobalSetter getGlobalSetter()
/*     */   {
/* 221 */     if (hasCustomGlobal) {
/* 222 */       throw new IllegalStateException();
/*     */     }
/* 224 */     hasCustomGlobal = true;
/*     */ 
/* 233 */     return new GlobalSetter()
/*     */     {
/*     */       public void setContextFactoryGlobal(ContextFactory paramAnonymousContextFactory)
/*     */       {
/* 227 */         ContextFactory.access$002(paramAnonymousContextFactory == null ? new ContextFactory() : paramAnonymousContextFactory);
/*     */       }
/*     */       public ContextFactory getContextFactoryGlobal() {
/* 230 */         return ContextFactory.global;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   protected Context makeContext()
/*     */   {
/* 247 */     return new Context(this);
/*     */   }
/*     */ 
/*     */   protected boolean hasFeature(Context paramContext, int paramInt)
/*     */   {
/*     */     int i;
/* 258 */     switch (paramInt)
/*     */     {
/*     */     case 1:
/* 271 */       i = paramContext.getLanguageVersion();
/* 272 */       return (i == 100) || (i == 110) || (i == 120);
/*     */     case 2:
/* 277 */       return false;
/*     */     case 3:
/* 280 */       return false;
/*     */     case 4:
/* 283 */       i = paramContext.getLanguageVersion();
/* 284 */       return i == 120;
/*     */     case 5:
/* 287 */       return true;
/*     */     case 6:
/* 290 */       i = paramContext.getLanguageVersion();
/* 291 */       return (i == 0) || (i >= 160);
/*     */     case 7:
/* 295 */       return false;
/*     */     case 8:
/* 298 */       return false;
/*     */     case 9:
/* 301 */       return false;
/*     */     case 10:
/* 304 */       return false;
/*     */     case 11:
/* 307 */       return false;
/*     */     case 12:
/* 310 */       return false;
/*     */     case 13:
/* 313 */       return false;
/*     */     }
/*     */ 
/* 316 */     throw new IllegalArgumentException(String.valueOf(paramInt));
/*     */   }
/*     */ 
/*     */   private boolean isDom3Present() {
/* 320 */     Class localClass = Kit.classOrNull("org.w3c.dom.Node");
/* 321 */     if (localClass == null) return false;
/*     */ 
/*     */     try
/*     */     {
/* 325 */       localClass.getMethod("getUserData", new Class[] { String.class });
/* 326 */       return true; } catch (NoSuchMethodException localNoSuchMethodException) {
/*     */     }
/* 328 */     return false;
/*     */   }
/*     */ 
/*     */   protected XMLLib.Factory getE4xImplementationFactory()
/*     */   {
/* 352 */     if (isDom3Present()) {
/* 353 */       return XMLLib.Factory.create("sun.org.mozilla.javascript.internal.xmlimpl.XMLLibImpl");
/*     */     }
/*     */ 
/* 356 */     if (Kit.classOrNull("org.apache.xmlbeans.XmlCursor") != null) {
/* 357 */       return XMLLib.Factory.create("sun.org.mozilla.javascript.internal.xml.impl.xmlbeans.XMLLibImpl");
/*     */     }
/*     */ 
/* 361 */     return null;
/*     */   }
/*     */ 
/*     */   protected GeneratedClassLoader createClassLoader(final ClassLoader paramClassLoader)
/*     */   {
/* 376 */     return (GeneratedClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public DefiningClassLoader run() {
/* 378 */         return new DefiningClassLoader(paramClassLoader);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public final ClassLoader getApplicationClassLoader()
/*     */   {
/* 391 */     return this.applicationClassLoader;
/*     */   }
/*     */ 
/*     */   public final void initApplicationClassLoader(ClassLoader paramClassLoader)
/*     */   {
/* 401 */     if (paramClassLoader == null) {
/* 402 */       throw new IllegalArgumentException("loader is null");
/*     */     }
/*     */ 
/* 410 */     if (this.applicationClassLoader != null) {
/* 411 */       throw new IllegalStateException("applicationClassLoader can only be set once");
/*     */     }
/* 413 */     checkNotSealed();
/*     */ 
/* 415 */     this.applicationClassLoader = paramClassLoader;
/*     */   }
/*     */ 
/*     */   protected Object doTopCall(Callable paramCallable, Context paramContext, Scriptable paramScriptable1, Scriptable paramScriptable2, Object[] paramArrayOfObject)
/*     */   {
/* 429 */     return paramCallable.call(paramContext, paramScriptable1, paramScriptable2, paramArrayOfObject);
/*     */   }
/*     */ 
/*     */   protected void observeInstructionCount(Context paramContext, int paramInt)
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void onContextCreated(Context paramContext)
/*     */   {
/* 443 */     Object localObject = this.listeners;
/* 444 */     for (int i = 0; ; i++) {
/* 445 */       Listener localListener = (Listener)Kit.getListener(localObject, i);
/* 446 */       if (localListener == null)
/*     */         break;
/* 448 */       localListener.contextCreated(paramContext);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void onContextReleased(Context paramContext)
/*     */   {
/* 454 */     Object localObject = this.listeners;
/* 455 */     for (int i = 0; ; i++) {
/* 456 */       Listener localListener = (Listener)Kit.getListener(localObject, i);
/* 457 */       if (localListener == null)
/*     */         break;
/* 459 */       localListener.contextReleased(paramContext);
/*     */     }
/*     */   }
/*     */ 
/*     */   public final void addListener(Listener paramListener)
/*     */   {
/* 465 */     checkNotSealed();
/* 466 */     synchronized (this.listenersLock) {
/* 467 */       if (this.disabledListening) {
/* 468 */         throw new IllegalStateException();
/*     */       }
/* 470 */       this.listeners = Kit.addListener(this.listeners, paramListener);
/*     */     }
/*     */   }
/*     */ 
/*     */   public final void removeListener(Listener paramListener)
/*     */   {
/* 476 */     checkNotSealed();
/* 477 */     synchronized (this.listenersLock) {
/* 478 */       if (this.disabledListening) {
/* 479 */         throw new IllegalStateException();
/*     */       }
/* 481 */       this.listeners = Kit.removeListener(this.listeners, paramListener);
/*     */     }
/*     */   }
/*     */ 
/*     */   final void disableContextListening()
/*     */   {
/* 491 */     checkNotSealed();
/* 492 */     synchronized (this.listenersLock) {
/* 493 */       this.disabledListening = true;
/* 494 */       this.listeners = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public final boolean isSealed()
/*     */   {
/* 504 */     return this.sealed;
/*     */   }
/*     */ 
/*     */   public final void seal()
/*     */   {
/* 514 */     checkNotSealed();
/* 515 */     this.sealed = true;
/*     */   }
/*     */ 
/*     */   protected final void checkNotSealed()
/*     */   {
/* 520 */     if (this.sealed) throw new IllegalStateException();
/*     */   }
/*     */ 
/*     */   public final Object call(ContextAction paramContextAction)
/*     */   {
/* 538 */     return Context.call(this, paramContextAction);
/*     */   }
/*     */ 
/*     */   public Context enterContext()
/*     */   {
/* 582 */     return enterContext(null);
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public final Context enter()
/*     */   {
/* 591 */     return enterContext(null);
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public final void exit()
/*     */   {
/* 599 */     Context.exit();
/*     */   }
/*     */ 
/*     */   public final Context enterContext(Context paramContext)
/*     */   {
/* 619 */     return Context.enter(paramContext, this);
/*     */   }
/*     */ 
/*     */   public static abstract interface GlobalSetter
/*     */   {
/*     */     public abstract void setContextFactoryGlobal(ContextFactory paramContextFactory);
/*     */ 
/*     */     public abstract ContextFactory getContextFactoryGlobal();
/*     */   }
/*     */ 
/*     */   public static abstract interface Listener
/*     */   {
/*     */     public abstract void contextCreated(Context paramContext);
/*     */ 
/*     */     public abstract void contextReleased(Context paramContext);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.org.mozilla.javascript.internal.ContextFactory
 * JD-Core Version:    0.6.2
 */