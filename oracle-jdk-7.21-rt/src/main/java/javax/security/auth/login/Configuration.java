/*     */ package javax.security.auth.login;
/*     */ 
/*     */ import java.security.AccessController;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.NoSuchProviderException;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.security.Provider;
/*     */ import java.security.Security;
/*     */ import javax.security.auth.AuthPermission;
/*     */ import sun.security.jca.GetInstance;
/*     */ import sun.security.jca.GetInstance.Instance;
/*     */ 
/*     */ public abstract class Configuration
/*     */ {
/*     */   private static Configuration configuration;
/* 200 */   private static ClassLoader contextClassLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
/*     */   {
/*     */     public ClassLoader run() {
/* 203 */       return Thread.currentThread().getContextClassLoader();
/*     */     }
/*     */   });
/*     */ 
/*     */   private static void checkPermission(String paramString)
/*     */   {
/* 209 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 210 */     if (localSecurityManager != null)
/* 211 */       localSecurityManager.checkPermission(new AuthPermission("createLoginConfiguration." + paramString));
/*     */   }
/*     */ 
/*     */   public static Configuration getConfiguration()
/*     */   {
/* 239 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 240 */     if (localSecurityManager != null) {
/* 241 */       localSecurityManager.checkPermission(new AuthPermission("getLoginConfiguration"));
/*     */     }
/* 243 */     synchronized (Configuration.class) {
/* 244 */       if (configuration == null) {
/* 245 */         String str1 = null;
/* 246 */         str1 = (String)AccessController.doPrivileged(new PrivilegedAction()
/*     */         {
/*     */           public String run() {
/* 249 */             return Security.getProperty("login.configuration.provider");
/*     */           }
/*     */         });
/* 253 */         if (str1 == null) {
/* 254 */           str1 = "com.sun.security.auth.login.ConfigFile";
/*     */         }
/*     */         try
/*     */         {
/* 258 */           String str2 = str1;
/* 259 */           configuration = (Configuration)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*     */           {
/*     */             public Configuration run()
/*     */               throws ClassNotFoundException, InstantiationException, IllegalAccessException
/*     */             {
/* 264 */               return (Configuration)Class.forName(this.val$finalClass, true, Configuration.contextClassLoader).newInstance();
/*     */             }
/*     */ 
/*     */           });
/*     */         }
/*     */         catch (PrivilegedActionException localPrivilegedActionException)
/*     */         {
/* 271 */           Exception localException = localPrivilegedActionException.getException();
/* 272 */           if ((localException instanceof InstantiationException)) {
/* 273 */             throw ((SecurityException)new SecurityException("Configuration error:" + localException.getCause().getMessage() + "\n").initCause(localException.getCause()));
/*     */           }
/*     */ 
/* 279 */           throw ((SecurityException)new SecurityException("Configuration error: " + localException.toString() + "\n").initCause(localException));
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 287 */       return configuration;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void setConfiguration(Configuration paramConfiguration)
/*     */   {
/* 304 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 305 */     if (localSecurityManager != null)
/* 306 */       localSecurityManager.checkPermission(new AuthPermission("setLoginConfiguration"));
/* 307 */     configuration = paramConfiguration;
/*     */   }
/*     */ 
/*     */   public static Configuration getInstance(String paramString, Parameters paramParameters)
/*     */     throws NoSuchAlgorithmException
/*     */   {
/* 351 */     checkPermission(paramString);
/*     */     try {
/* 353 */       GetInstance.Instance localInstance = GetInstance.getInstance("Configuration", ConfigurationSpi.class, paramString, paramParameters);
/*     */ 
/* 358 */       return new ConfigDelegate((ConfigurationSpi)localInstance.impl, localInstance.provider, paramString, paramParameters, null);
/*     */     }
/*     */     catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
/*     */     {
/* 363 */       return handleException(localNoSuchAlgorithmException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static Configuration getInstance(String paramString1, Parameters paramParameters, String paramString2)
/*     */     throws NoSuchProviderException, NoSuchAlgorithmException
/*     */   {
/* 415 */     if ((paramString2 == null) || (paramString2.length() == 0)) {
/* 416 */       throw new IllegalArgumentException("missing provider");
/*     */     }
/*     */ 
/* 419 */     checkPermission(paramString1);
/*     */     try {
/* 421 */       GetInstance.Instance localInstance = GetInstance.getInstance("Configuration", ConfigurationSpi.class, paramString1, paramParameters, paramString2);
/*     */ 
/* 427 */       return new ConfigDelegate((ConfigurationSpi)localInstance.impl, localInstance.provider, paramString1, paramParameters, null);
/*     */     }
/*     */     catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
/*     */     {
/* 432 */       return handleException(localNoSuchAlgorithmException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static Configuration getInstance(String paramString, Parameters paramParameters, Provider paramProvider)
/*     */     throws NoSuchAlgorithmException
/*     */   {
/* 477 */     if (paramProvider == null) {
/* 478 */       throw new IllegalArgumentException("missing provider");
/*     */     }
/*     */ 
/* 481 */     checkPermission(paramString);
/*     */     try {
/* 483 */       GetInstance.Instance localInstance = GetInstance.getInstance("Configuration", ConfigurationSpi.class, paramString, paramParameters, paramProvider);
/*     */ 
/* 489 */       return new ConfigDelegate((ConfigurationSpi)localInstance.impl, localInstance.provider, paramString, paramParameters, null);
/*     */     }
/*     */     catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
/*     */     {
/* 494 */       return handleException(localNoSuchAlgorithmException);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static Configuration handleException(NoSuchAlgorithmException paramNoSuchAlgorithmException) throws NoSuchAlgorithmException
/*     */   {
/* 500 */     Throwable localThrowable = paramNoSuchAlgorithmException.getCause();
/* 501 */     if ((localThrowable instanceof IllegalArgumentException)) {
/* 502 */       throw ((IllegalArgumentException)localThrowable);
/*     */     }
/* 504 */     throw paramNoSuchAlgorithmException;
/*     */   }
/*     */ 
/*     */   public Provider getProvider()
/*     */   {
/* 519 */     return null;
/*     */   }
/*     */ 
/*     */   public String getType()
/*     */   {
/* 534 */     return null;
/*     */   }
/*     */ 
/*     */   public Parameters getParameters()
/*     */   {
/* 549 */     return null;
/*     */   }
/*     */ 
/*     */   public abstract AppConfigurationEntry[] getAppConfigurationEntry(String paramString);
/*     */ 
/*     */   public void refresh()
/*     */   {
/*     */   }
/*     */ 
/*     */   private static class ConfigDelegate extends Configuration
/*     */   {
/*     */     private ConfigurationSpi spi;
/*     */     private Provider p;
/*     */     private String type;
/*     */     private Configuration.Parameters params;
/*     */ 
/*     */     private ConfigDelegate(ConfigurationSpi paramConfigurationSpi, Provider paramProvider, String paramString, Configuration.Parameters paramParameters)
/*     */     {
/* 597 */       this.spi = paramConfigurationSpi;
/* 598 */       this.p = paramProvider;
/* 599 */       this.type = paramString;
/* 600 */       this.params = paramParameters;
/*     */     }
/*     */     public String getType() {
/* 603 */       return this.type;
/*     */     }
/* 605 */     public Configuration.Parameters getParameters() { return this.params; } 
/*     */     public Provider getProvider() {
/* 607 */       return this.p;
/*     */     }
/*     */     public AppConfigurationEntry[] getAppConfigurationEntry(String paramString) {
/* 610 */       return this.spi.engineGetAppConfigurationEntry(paramString);
/*     */     }
/*     */ 
/*     */     public void refresh() {
/* 614 */       this.spi.engineRefresh();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract interface Parameters
/*     */   {
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.security.auth.login.Configuration
 * JD-Core Version:    0.6.2
 */