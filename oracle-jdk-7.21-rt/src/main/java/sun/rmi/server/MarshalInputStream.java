/*     */ package sun.rmi.server;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectStreamClass;
/*     */ import java.io.StreamCorruptedException;
/*     */ import java.rmi.server.RMIClassLoader;
/*     */ import java.security.AccessControlException;
/*     */ import java.security.AccessController;
/*     */ import java.security.Permission;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ import sun.security.action.LoadLibraryAction;
/*     */ 
/*     */ public class MarshalInputStream extends ObjectInputStream
/*     */ {
/*  66 */   private static final boolean useCodebaseOnlyProperty = !((String)AccessController.doPrivileged(new GetPropertyAction("java.rmi.server.useCodebaseOnly", "true"))).equalsIgnoreCase("false");
/*     */ 
/*  73 */   protected static Map<String, Class<?>> permittedSunClasses = new HashMap(3);
/*     */ 
/*  77 */   private boolean skipDefaultResolveClass = false;
/*     */ 
/*  80 */   private final Map<Object, Runnable> doneCallbacks = new HashMap(3);
/*     */ 
/*  87 */   private boolean useCodebaseOnly = useCodebaseOnlyProperty;
/*     */ 
/*     */   public MarshalInputStream(InputStream paramInputStream)
/*     */     throws IOException, StreamCorruptedException
/*     */   {
/* 132 */     super(paramInputStream);
/*     */   }
/*     */ 
/*     */   public Runnable getDoneCallback(Object paramObject)
/*     */   {
/* 141 */     return (Runnable)this.doneCallbacks.get(paramObject);
/*     */   }
/*     */ 
/*     */   public void setDoneCallback(Object paramObject, Runnable paramRunnable)
/*     */   {
/* 151 */     this.doneCallbacks.put(paramObject, paramRunnable);
/*     */   }
/*     */ 
/*     */   public void done()
/*     */   {
/* 164 */     Iterator localIterator = this.doneCallbacks.values().iterator();
/* 165 */     while (localIterator.hasNext()) {
/* 166 */       Runnable localRunnable = (Runnable)localIterator.next();
/* 167 */       localRunnable.run();
/*     */     }
/* 169 */     this.doneCallbacks.clear();
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/* 176 */     done();
/* 177 */     super.close();
/*     */   }
/*     */ 
/*     */   protected Class resolveClass(ObjectStreamClass paramObjectStreamClass)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 192 */     Object localObject = readLocation();
/*     */ 
/* 194 */     String str1 = paramObjectStreamClass.getName();
/*     */ 
/* 206 */     ClassLoader localClassLoader = this.skipDefaultResolveClass ? null : latestUserDefinedLoader();
/*     */ 
/* 216 */     String str2 = null;
/* 217 */     if ((!this.useCodebaseOnly) && ((localObject instanceof String))) {
/* 218 */       str2 = (String)localObject;
/*     */     }
/*     */     try
/*     */     {
/* 222 */       return RMIClassLoader.loadClass(str2, str1, localClassLoader);
/*     */     }
/*     */     catch (AccessControlException localAccessControlException) {
/* 225 */       return checkSunClass(str1, localAccessControlException);
/*     */     }
/*     */     catch (ClassNotFoundException localClassNotFoundException1)
/*     */     {
/*     */       try
/*     */       {
/* 232 */         if ((Character.isLowerCase(str1.charAt(0))) && (str1.indexOf('.') == -1))
/*     */         {
/* 235 */           return super.resolveClass(paramObjectStreamClass);
/*     */         }
/*     */       } catch (ClassNotFoundException localClassNotFoundException2) {
/*     */       }
/* 239 */       throw localClassNotFoundException1;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected Class resolveProxyClass(String[] paramArrayOfString)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 253 */     Object localObject = readLocation();
/*     */ 
/* 255 */     ClassLoader localClassLoader = this.skipDefaultResolveClass ? null : latestUserDefinedLoader();
/*     */ 
/* 258 */     String str = null;
/* 259 */     if ((!this.useCodebaseOnly) && ((localObject instanceof String))) {
/* 260 */       str = (String)localObject;
/*     */     }
/*     */ 
/* 263 */     return RMIClassLoader.loadProxyClass(str, paramArrayOfString, localClassLoader);
/*     */   }
/*     */ 
/*     */   private static native ClassLoader latestUserDefinedLoader();
/*     */ 
/*     */   private Class checkSunClass(String paramString, AccessControlException paramAccessControlException)
/*     */     throws AccessControlException
/*     */   {
/* 281 */     Permission localPermission = paramAccessControlException.getPermission();
/* 282 */     String str = null;
/* 283 */     if (localPermission != null) {
/* 284 */       str = localPermission.getName();
/*     */     }
/*     */ 
/* 287 */     Class localClass = (Class)permittedSunClasses.get(paramString);
/*     */ 
/* 290 */     if ((str == null) || (localClass == null) || ((!str.equals("accessClassInPackage.sun.rmi.server")) && (!str.equals("accessClassInPackage.sun.rmi.registry"))))
/*     */     {
/* 295 */       throw paramAccessControlException;
/*     */     }
/*     */ 
/* 298 */     return localClass;
/*     */   }
/*     */ 
/*     */   protected Object readLocation()
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 309 */     return readObject();
/*     */   }
/*     */ 
/*     */   void skipDefaultResolveClass()
/*     */   {
/* 317 */     this.skipDefaultResolveClass = true;
/*     */   }
/*     */ 
/*     */   void useCodebaseOnly()
/*     */   {
/* 325 */     this.useCodebaseOnly = true;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/* 105 */       String str1 = "sun.rmi.server.Activation$ActivationSystemImpl_Stub";
/*     */ 
/* 107 */       String str2 = "sun.rmi.registry.RegistryImpl_Stub";
/*     */ 
/* 109 */       permittedSunClasses.put(str1, Class.forName(str1));
/* 110 */       permittedSunClasses.put(str2, Class.forName(str2));
/*     */     }
/*     */     catch (ClassNotFoundException localClassNotFoundException) {
/* 113 */       throw new NoClassDefFoundError("Missing system class: " + localClassNotFoundException.getMessage());
/*     */     }
/*     */ 
/* 122 */     AccessController.doPrivileged(new LoadLibraryAction("rmi"));
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.rmi.server.MarshalInputStream
 * JD-Core Version:    0.6.2
 */