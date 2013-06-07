/*     */ package sun.applet;
/*     */ 
/*     */ import java.lang.reflect.Field;
/*     */ import java.net.URLClassLoader;
/*     */ import java.security.AccessControlContext;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.ProtectionDomain;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Properties;
/*     */ import sun.awt.AWTSecurityManager;
/*     */ import sun.awt.AppContext;
/*     */ import sun.security.util.SecurityConstants;
/*     */ 
/*     */ public class AppletSecurity extends AWTSecurityManager
/*     */ {
/*     */   private AppContext mainAppContext;
/*  58 */   private static Field facc = null;
/*     */ 
/*  61 */   private static Field fcontext = null;
/*     */ 
/*  84 */   private HashSet restrictedPackages = new HashSet();
/*     */ 
/* 235 */   private boolean inThreadGroupCheck = false;
/*     */ 
/*     */   public AppletSecurity()
/*     */   {
/*  79 */     reset();
/*  80 */     this.mainAppContext = AppContext.getAppContext();
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */   {
/*  92 */     this.restrictedPackages.clear();
/*     */ 
/*  94 */     AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Object run()
/*     */       {
/*  98 */         Enumeration localEnumeration = System.getProperties().propertyNames();
/*     */ 
/* 100 */         while (localEnumeration.hasMoreElements())
/*     */         {
/* 102 */           String str1 = (String)localEnumeration.nextElement();
/*     */ 
/* 104 */           if ((str1 != null) && (str1.startsWith("package.restrict.access.")))
/*     */           {
/* 106 */             String str2 = System.getProperty(str1);
/*     */ 
/* 108 */             if ((str2 != null) && (str2.equalsIgnoreCase("true")))
/*     */             {
/* 110 */               String str3 = str1.substring(24);
/*     */ 
/* 113 */               AppletSecurity.this.restrictedPackages.add(str3);
/*     */             }
/*     */           }
/*     */         }
/* 117 */         return null;
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private AppletClassLoader currentAppletClassLoader()
/*     */   {
/* 128 */     ClassLoader localClassLoader1 = currentClassLoader();
/*     */ 
/* 130 */     if ((localClassLoader1 == null) || ((localClassLoader1 instanceof AppletClassLoader))) {
/* 131 */       return (AppletClassLoader)localClassLoader1;
/*     */     }
/*     */ 
/* 134 */     Class[] arrayOfClass = getClassContext();
/* 135 */     for (int i = 0; i < arrayOfClass.length; i++) {
/* 136 */       localClassLoader1 = arrayOfClass[i].getClassLoader();
/* 137 */       if ((localClassLoader1 instanceof AppletClassLoader)) {
/* 138 */         return (AppletClassLoader)localClassLoader1;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 148 */     for (i = 0; i < arrayOfClass.length; i++) {
/* 149 */       final ClassLoader localClassLoader2 = arrayOfClass[i].getClassLoader();
/*     */ 
/* 151 */       if ((localClassLoader2 instanceof URLClassLoader)) {
/* 152 */         localClassLoader1 = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
/*     */         {
/*     */           public Object run() {
/* 155 */             AccessControlContext localAccessControlContext = null;
/* 156 */             ProtectionDomain[] arrayOfProtectionDomain = null;
/*     */             try
/*     */             {
/* 159 */               localAccessControlContext = (AccessControlContext)AppletSecurity.facc.get(localClassLoader2);
/* 160 */               if (localAccessControlContext == null) {
/* 161 */                 return null;
/*     */               }
/*     */ 
/* 164 */               arrayOfProtectionDomain = (ProtectionDomain[])AppletSecurity.fcontext.get(localAccessControlContext);
/* 165 */               if (arrayOfProtectionDomain == null)
/* 166 */                 return null;
/*     */             }
/*     */             catch (Exception localException) {
/* 169 */               throw new UnsupportedOperationException(localException);
/*     */             }
/*     */ 
/* 172 */             for (int i = 0; i < arrayOfProtectionDomain.length; i++) {
/* 173 */               ClassLoader localClassLoader = arrayOfProtectionDomain[i].getClassLoader();
/*     */ 
/* 175 */               if ((localClassLoader instanceof AppletClassLoader)) {
/* 176 */                 return localClassLoader;
/*     */               }
/*     */             }
/*     */ 
/* 180 */             return null;
/*     */           }
/*     */         });
/* 184 */         if (localClassLoader1 != null) {
/* 185 */           return (AppletClassLoader)localClassLoader1;
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 191 */     localClassLoader1 = Thread.currentThread().getContextClassLoader();
/* 192 */     if ((localClassLoader1 instanceof AppletClassLoader)) {
/* 193 */       return (AppletClassLoader)localClassLoader1;
/*     */     }
/*     */ 
/* 196 */     return (AppletClassLoader)null;
/*     */   }
/*     */ 
/*     */   protected boolean inThreadGroup(ThreadGroup paramThreadGroup)
/*     */   {
/* 205 */     if (currentAppletClassLoader() == null) {
/* 206 */       return false;
/*     */     }
/* 208 */     return getThreadGroup().parentOf(paramThreadGroup);
/*     */   }
/*     */ 
/*     */   protected boolean inThreadGroup(Thread paramThread)
/*     */   {
/* 216 */     return inThreadGroup(paramThread.getThreadGroup());
/*     */   }
/*     */ 
/*     */   public void checkAccess(Thread paramThread)
/*     */   {
/* 230 */     if ((paramThread.getState() != Thread.State.TERMINATED) && (!inThreadGroup(paramThread)))
/* 231 */       checkPermission(SecurityConstants.MODIFY_THREAD_PERMISSION);
/*     */   }
/*     */ 
/*     */   public synchronized void checkAccess(ThreadGroup paramThreadGroup)
/*     */   {
/* 242 */     if (this.inThreadGroupCheck)
/*     */     {
/* 247 */       checkPermission(SecurityConstants.MODIFY_THREADGROUP_PERMISSION);
/*     */     }
/*     */     else try {
/* 250 */         this.inThreadGroupCheck = true;
/* 251 */         if (!inThreadGroup(paramThreadGroup))
/* 252 */           checkPermission(SecurityConstants.MODIFY_THREADGROUP_PERMISSION);
/*     */       }
/*     */       finally {
/* 255 */         this.inThreadGroupCheck = false;
/*     */       }
/*     */   }
/*     */ 
/*     */   public void checkPackageAccess(String paramString)
/*     */   {
/* 283 */     super.checkPackageAccess(paramString);
/*     */ 
/* 286 */     for (Iterator localIterator = this.restrictedPackages.iterator(); localIterator.hasNext(); )
/*     */     {
/* 288 */       String str = (String)localIterator.next();
/*     */ 
/* 293 */       if ((paramString.equals(str)) || (paramString.startsWith(str + ".")))
/*     */       {
/* 295 */         checkPermission(new RuntimePermission("accessClassInPackage." + paramString));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void checkAwtEventQueueAccess()
/*     */   {
/* 312 */     AppContext localAppContext = AppContext.getAppContext();
/* 313 */     AppletClassLoader localAppletClassLoader = currentAppletClassLoader();
/*     */ 
/* 315 */     if ((localAppContext == this.mainAppContext) && (localAppletClassLoader != null))
/*     */     {
/* 319 */       super.checkAwtEventQueueAccess();
/*     */     }
/*     */   }
/*     */ 
/*     */   public ThreadGroup getThreadGroup()
/*     */   {
/* 331 */     AppletClassLoader localAppletClassLoader = currentAppletClassLoader();
/* 332 */     ThreadGroup localThreadGroup = localAppletClassLoader == null ? null : localAppletClassLoader.getThreadGroup();
/*     */ 
/* 334 */     if (localThreadGroup != null) {
/* 335 */       return localThreadGroup;
/*     */     }
/* 337 */     return super.getThreadGroup();
/*     */   }
/*     */ 
/*     */   public AppContext getAppContext()
/*     */   {
/* 354 */     AppletClassLoader localAppletClassLoader = currentAppletClassLoader();
/*     */ 
/* 356 */     if (localAppletClassLoader == null) {
/* 357 */       return null;
/*     */     }
/* 359 */     AppContext localAppContext = localAppletClassLoader.getAppContext();
/*     */ 
/* 363 */     if (localAppContext == null) {
/* 364 */       throw new SecurityException("Applet classloader has invalid AppContext");
/*     */     }
/*     */ 
/* 367 */     return localAppContext;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  65 */       facc = URLClassLoader.class.getDeclaredField("acc");
/*  66 */       facc.setAccessible(true);
/*  67 */       fcontext = AccessControlContext.class.getDeclaredField("context");
/*  68 */       fcontext.setAccessible(true);
/*     */     } catch (NoSuchFieldException localNoSuchFieldException) {
/*  70 */       throw new UnsupportedOperationException(localNoSuchFieldException);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.applet.AppletSecurity
 * JD-Core Version:    0.6.2
 */