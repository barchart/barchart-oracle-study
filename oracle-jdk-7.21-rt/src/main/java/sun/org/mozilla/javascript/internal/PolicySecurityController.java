/*     */ package sun.org.mozilla.javascript.internal;
/*     */ 
/*     */ import java.lang.ref.SoftReference;
/*     */ import java.lang.reflect.UndeclaredThrowableException;
/*     */ import java.security.AccessController;
/*     */ import java.security.CodeSource;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.security.SecureClassLoader;
/*     */ import java.util.Map;
/*     */ import java.util.WeakHashMap;
/*     */ import sun.org.mozilla.classfile.internal.ClassFileWriter;
/*     */ 
/*     */ public class PolicySecurityController extends SecurityController
/*     */ {
/*  65 */   private static final byte[] secureCallerImplBytecode = loadBytecode();
/*     */ 
/*  72 */   private static final Map<CodeSource, Map<ClassLoader, SoftReference<SecureCaller>>> callers = new WeakHashMap();
/*     */ 
/*     */   public Class<?> getStaticSecurityDomainClassInternal()
/*     */   {
/*  77 */     return CodeSource.class;
/*     */   }
/*     */ 
/*     */   public GeneratedClassLoader createClassLoader(final ClassLoader paramClassLoader, final Object paramObject)
/*     */   {
/* 106 */     return (Loader)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Object run()
/*     */       {
/* 111 */         return new PolicySecurityController.Loader(paramClassLoader, (CodeSource)paramObject);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public Object getDynamicSecurityDomain(Object paramObject)
/*     */   {
/* 121 */     return paramObject;
/*     */   }
/*     */ 
/*     */   public Object callWithDomain(Object paramObject, final Context paramContext, Callable paramCallable, Scriptable paramScriptable1, Scriptable paramScriptable2, Object[] paramArrayOfObject)
/*     */   {
/* 131 */     final ClassLoader localClassLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Object run() {
/* 134 */         return paramContext.getApplicationClassLoader();
/*     */       }
/*     */     });
/* 137 */     final CodeSource localCodeSource = (CodeSource)paramObject;
/*     */     Object localObject1;
/* 139 */     synchronized (callers) {
/* 140 */       localObject1 = (Map)callers.get(localCodeSource);
/* 141 */       if (localObject1 == null) {
/* 142 */         localObject1 = new WeakHashMap();
/* 143 */         callers.put(localCodeSource, localObject1);
/*     */       }
/*     */     }
/*     */ 
/* 147 */     synchronized (localObject1) {
/* 148 */       SoftReference localSoftReference = (SoftReference)((Map)localObject1).get(localClassLoader);
/* 149 */       if (localSoftReference != null)
/* 150 */         ??? = (SecureCaller)localSoftReference.get();
/*     */       else {
/* 152 */         ??? = null;
/*     */       }
/* 154 */       if (??? == null)
/*     */       {
/*     */         try
/*     */         {
/* 160 */           ??? = (SecureCaller)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*     */           {
/*     */             public Object run()
/*     */               throws Exception
/*     */             {
/* 165 */               PolicySecurityController.Loader localLoader = new PolicySecurityController.Loader(localClassLoader, localCodeSource);
/*     */ 
/* 167 */               Class localClass = localLoader.defineClass(PolicySecurityController.SecureCaller.class.getName() + "Impl", PolicySecurityController.secureCallerImplBytecode);
/*     */ 
/* 170 */               return localClass.newInstance();
/*     */             }
/*     */           });
/* 173 */           ((Map)localObject1).put(localClassLoader, new SoftReference(???));
/*     */         }
/*     */         catch (PrivilegedActionException localPrivilegedActionException)
/*     */         {
/* 177 */           throw new UndeclaredThrowableException(localPrivilegedActionException.getCause());
/*     */         }
/*     */       }
/*     */     }
/* 181 */     return ((SecureCaller)???).call(paramCallable, paramContext, paramScriptable1, paramScriptable2, paramArrayOfObject);
/*     */   }
/*     */ 
/*     */   private static byte[] loadBytecode()
/*     */   {
/* 193 */     String str1 = SecureCaller.class.getName();
/* 194 */     ClassFileWriter localClassFileWriter = new ClassFileWriter(str1 + "Impl", str1, "<generated>");
/*     */ 
/* 197 */     localClassFileWriter.startMethod("<init>", "()V", (short)1);
/* 198 */     localClassFileWriter.addALoad(0);
/* 199 */     localClassFileWriter.addInvoke(183, str1, "<init>", "()V");
/*     */ 
/* 201 */     localClassFileWriter.add(177);
/* 202 */     localClassFileWriter.stopMethod((short)1);
/* 203 */     String str2 = "Lsun/org/mozilla/javascript/internal/Context;Lsun/org/mozilla/javascript/internal/Scriptable;Lsun/org/mozilla/javascript/internal/Scriptable;[Ljava/lang/Object;)Ljava/lang/Object;";
/*     */ 
/* 209 */     localClassFileWriter.startMethod("call", "(Lsun/org/mozilla/javascript/internal/Callable;" + str2, (short)17);
/*     */ 
/* 213 */     for (int i = 1; i < 6; i++) {
/* 214 */       localClassFileWriter.addALoad(i);
/*     */     }
/* 216 */     localClassFileWriter.addInvoke(185, "sun/org/mozilla/javascript/internal/Callable", "call", "(" + str2);
/*     */ 
/* 219 */     localClassFileWriter.add(176);
/* 220 */     localClassFileWriter.stopMethod((short)6);
/* 221 */     return localClassFileWriter.toByteArray();
/*     */   }
/*     */ 
/*     */   private static class Loader extends SecureClassLoader
/*     */     implements GeneratedClassLoader
/*     */   {
/*     */     private final CodeSource codeSource;
/*     */ 
/*     */     Loader(ClassLoader paramClassLoader, CodeSource paramCodeSource)
/*     */     {
/*  87 */       super();
/*  88 */       this.codeSource = paramCodeSource;
/*     */     }
/*     */ 
/*     */     public Class<?> defineClass(String paramString, byte[] paramArrayOfByte)
/*     */     {
/*  93 */       return defineClass(paramString, paramArrayOfByte, 0, paramArrayOfByte.length, this.codeSource);
/*     */     }
/*     */ 
/*     */     public void linkClass(Class<?> paramClass)
/*     */     {
/*  98 */       resolveClass(paramClass);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract class SecureCaller
/*     */   {
/*     */     public abstract Object call(Callable paramCallable, Context paramContext, Scriptable paramScriptable1, Scriptable paramScriptable2, Object[] paramArrayOfObject);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.org.mozilla.javascript.internal.PolicySecurityController
 * JD-Core Version:    0.6.2
 */