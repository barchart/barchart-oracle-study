/*     */ package javax.management;
/*     */ 
/*     */ import com.sun.jmx.defaults.JmxProperties;
/*     */ import com.sun.jmx.mbeanserver.GetPropertyAction;
/*     */ import java.security.AccessController;
/*     */ import java.util.ArrayList;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import javax.management.loading.ClassLoaderRepository;
/*     */ 
/*     */ public class MBeanServerFactory
/*     */ {
/* 100 */   private static MBeanServerBuilder builder = null;
/*     */ 
/* 431 */   private static final ArrayList<MBeanServer> mBeanServerList = new ArrayList();
/*     */ 
/*     */   public static void releaseMBeanServer(MBeanServer paramMBeanServer)
/*     */   {
/* 151 */     checkPermission("releaseMBeanServer");
/*     */ 
/* 153 */     removeMBeanServer(paramMBeanServer);
/*     */   }
/*     */ 
/*     */   public static MBeanServer createMBeanServer()
/*     */   {
/* 191 */     return createMBeanServer(null);
/*     */   }
/*     */ 
/*     */   public static MBeanServer createMBeanServer(String paramString)
/*     */   {
/* 228 */     checkPermission("createMBeanServer");
/*     */ 
/* 230 */     MBeanServer localMBeanServer = newMBeanServer(paramString);
/* 231 */     addMBeanServer(localMBeanServer);
/* 232 */     return localMBeanServer;
/*     */   }
/*     */ 
/*     */   public static MBeanServer newMBeanServer()
/*     */   {
/* 272 */     return newMBeanServer(null);
/*     */   }
/*     */ 
/*     */   public static MBeanServer newMBeanServer(String paramString)
/*     */   {
/* 311 */     checkPermission("newMBeanServer");
/*     */ 
/* 315 */     MBeanServerBuilder localMBeanServerBuilder = getNewMBeanServerBuilder();
/*     */ 
/* 318 */     synchronized (localMBeanServerBuilder) {
/* 319 */       MBeanServerDelegate localMBeanServerDelegate = localMBeanServerBuilder.newMBeanServerDelegate();
/*     */ 
/* 321 */       if (localMBeanServerDelegate == null)
/*     */       {
/* 325 */         throw new JMRuntimeException("MBeanServerBuilder.newMBeanServerDelegate() returned null");
/*     */       }
/* 327 */       MBeanServer localMBeanServer = localMBeanServerBuilder.newMBeanServer(paramString, null, localMBeanServerDelegate);
/*     */ 
/* 329 */       if (localMBeanServer == null)
/*     */       {
/* 332 */         throw new JMRuntimeException("MBeanServerBuilder.newMBeanServer() returned null");
/*     */       }
/* 334 */       return localMBeanServer;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static synchronized ArrayList<MBeanServer> findMBeanServer(String paramString)
/*     */   {
/* 360 */     checkPermission("findMBeanServer");
/*     */ 
/* 362 */     if (paramString == null) {
/* 363 */       return new ArrayList(mBeanServerList);
/*     */     }
/* 365 */     ArrayList localArrayList = new ArrayList();
/* 366 */     for (MBeanServer localMBeanServer : mBeanServerList) {
/* 367 */       String str = mBeanServerId(localMBeanServer);
/* 368 */       if (paramString.equals(str))
/* 369 */         localArrayList.add(localMBeanServer);
/*     */     }
/* 371 */     return localArrayList;
/*     */   }
/*     */ 
/*     */   public static ClassLoaderRepository getClassLoaderRepository(MBeanServer paramMBeanServer)
/*     */   {
/* 393 */     return paramMBeanServer.getClassLoaderRepository();
/*     */   }
/*     */ 
/*     */   private static String mBeanServerId(MBeanServer paramMBeanServer) {
/*     */     try {
/* 398 */       return (String)paramMBeanServer.getAttribute(MBeanServerDelegate.DELEGATE_NAME, "MBeanServerId");
/*     */     }
/*     */     catch (JMException localJMException) {
/* 401 */       JmxProperties.MISC_LOGGER.finest("Ignoring exception while getting MBeanServerId: " + localJMException);
/*     */     }
/* 403 */     return null;
/*     */   }
/*     */ 
/*     */   private static void checkPermission(String paramString)
/*     */     throws SecurityException
/*     */   {
/* 409 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 410 */     if (localSecurityManager != null) {
/* 411 */       MBeanServerPermission localMBeanServerPermission = new MBeanServerPermission(paramString);
/* 412 */       localSecurityManager.checkPermission(localMBeanServerPermission);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static synchronized void addMBeanServer(MBeanServer paramMBeanServer) {
/* 417 */     mBeanServerList.add(paramMBeanServer);
/*     */   }
/*     */ 
/*     */   private static synchronized void removeMBeanServer(MBeanServer paramMBeanServer) {
/* 421 */     boolean bool = mBeanServerList.remove(paramMBeanServer);
/* 422 */     if (!bool) {
/* 423 */       JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, MBeanServerFactory.class.getName(), "removeMBeanServer(MBeanServer)", "MBeanServer was not in list!");
/*     */ 
/* 427 */       throw new IllegalArgumentException("MBeanServer was not in list!");
/*     */     }
/*     */   }
/*     */ 
/*     */   private static Class<?> loadBuilderClass(String paramString)
/*     */     throws ClassNotFoundException
/*     */   {
/* 440 */     ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
/*     */ 
/* 443 */     if (localClassLoader != null)
/*     */     {
/* 445 */       return localClassLoader.loadClass(paramString);
/*     */     }
/*     */ 
/* 449 */     return Class.forName(paramString);
/*     */   }
/*     */ 
/*     */   private static MBeanServerBuilder newBuilder(Class<?> paramClass)
/*     */   {
/*     */     try
/*     */     {
/* 460 */       Object localObject = paramClass.newInstance();
/* 461 */       return (MBeanServerBuilder)localObject;
/*     */     } catch (RuntimeException localRuntimeException) {
/* 463 */       throw localRuntimeException;
/*     */     } catch (Exception localException) {
/* 465 */       String str = "Failed to instantiate a MBeanServerBuilder from " + paramClass + ": " + localException;
/*     */ 
/* 468 */       throw new JMRuntimeException(str, localException);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static synchronized void checkMBeanServerBuilder()
/*     */   {
/*     */     try
/*     */     {
/* 478 */       GetPropertyAction localGetPropertyAction = new GetPropertyAction("javax.management.builder.initial");
/*     */ 
/* 480 */       localObject1 = (String)AccessController.doPrivileged(localGetPropertyAction);
/*     */       try
/*     */       {
/*     */         Object localObject2;
/* 484 */         if ((localObject1 == null) || (((String)localObject1).length() == 0))
/* 485 */           localObject2 = MBeanServerBuilder.class;
/*     */         else {
/* 487 */           localObject2 = loadBuilderClass((String)localObject1);
/*     */         }
/*     */ 
/* 490 */         if (builder != null) {
/* 491 */           localObject3 = builder.getClass();
/* 492 */           if (localObject2 == localObject3) {
/* 493 */             return;
/*     */           }
/*     */         }
/*     */ 
/* 497 */         builder = newBuilder((Class)localObject2);
/*     */       } catch (ClassNotFoundException localClassNotFoundException) {
/* 499 */         Object localObject3 = "Failed to load MBeanServerBuilder class " + (String)localObject1 + ": " + localClassNotFoundException;
/*     */ 
/* 502 */         throw new JMRuntimeException((String)localObject3, localClassNotFoundException);
/*     */       }
/*     */     }
/*     */     catch (RuntimeException localRuntimeException)
/*     */     {
/*     */       Object localObject1;
/* 505 */       if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST)) {
/* 506 */         localObject1 = new StringBuilder().append("Failed to instantiate MBeanServerBuilder: ").append(localRuntimeException).append("\n\t\tCheck the value of the ").append("javax.management.builder.initial").append(" property.");
/*     */ 
/* 510 */         JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, MBeanServerFactory.class.getName(), "checkMBeanServerBuilder", ((StringBuilder)localObject1).toString());
/*     */       }
/*     */ 
/* 515 */       throw localRuntimeException;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static synchronized MBeanServerBuilder getNewMBeanServerBuilder()
/*     */   {
/* 538 */     checkMBeanServerBuilder();
/* 539 */     return builder;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.management.MBeanServerFactory
 * JD-Core Version:    0.6.2
 */