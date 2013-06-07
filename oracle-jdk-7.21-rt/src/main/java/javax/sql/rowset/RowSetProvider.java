/*     */ package javax.sql.rowset;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Iterator;
/*     */ import java.util.ServiceConfigurationError;
/*     */ import java.util.ServiceLoader;
/*     */ 
/*     */ public class RowSetProvider
/*     */ {
/*     */   private static final String ROWSET_DEBUG_PROPERTY = "javax.sql.rowset.RowSetProvider.debug";
/*     */   private static final String ROWSET_FACTORY_IMPL = "com.sun.rowset.RowSetFactoryImpl";
/*     */   private static final String ROWSET_FACTORY_NAME = "javax.sql.rowset.RowSetFactory";
/*  71 */   private static boolean debug = (str != null) && (!"false".equals(str));
/*     */ 
/*     */   public static RowSetFactory newFactory()
/*     */     throws SQLException
/*     */   {
/* 123 */     RowSetFactory localRowSetFactory = null;
/* 124 */     String str = null;
/*     */     try {
/* 126 */       trace("Checking for Rowset System Property...");
/* 127 */       str = getSystemProperty("javax.sql.rowset.RowSetFactory");
/* 128 */       if (str != null) {
/* 129 */         trace("Found system property, value=" + str);
/* 130 */         localRowSetFactory = (RowSetFactory)getFactoryClass(str, null, true).newInstance();
/*     */       }
/*     */     } catch (ClassNotFoundException localClassNotFoundException) {
/* 133 */       throw new SQLException("RowSetFactory: " + str + " not found", localClassNotFoundException);
/*     */     }
/*     */     catch (Exception localException) {
/* 136 */       throw new SQLException("RowSetFactory: " + str + " could not be instantiated: " + localException, localException);
/*     */     }
/*     */ 
/* 142 */     if (localRowSetFactory == null)
/*     */     {
/* 146 */       localRowSetFactory = loadViaServiceLoader();
/* 147 */       localRowSetFactory = localRowSetFactory == null ? newFactory("com.sun.rowset.RowSetFactoryImpl", null) : localRowSetFactory;
/*     */     }
/*     */ 
/* 150 */     return localRowSetFactory;
/*     */   }
/*     */ 
/*     */   public static RowSetFactory newFactory(String paramString, ClassLoader paramClassLoader)
/*     */     throws SQLException
/*     */   {
/* 182 */     trace("***In newInstance()");
/*     */     try {
/* 184 */       Class localClass = getFactoryClass(paramString, paramClassLoader, false);
/* 185 */       RowSetFactory localRowSetFactory = (RowSetFactory)localClass.newInstance();
/* 186 */       if (debug) {
/* 187 */         trace("Created new instance of " + localClass + " using ClassLoader: " + paramClassLoader);
/*     */       }
/*     */ 
/* 190 */       return localRowSetFactory;
/*     */     } catch (ClassNotFoundException localClassNotFoundException) {
/* 192 */       throw new SQLException("Provider " + paramString + " not found", localClassNotFoundException);
/*     */     }
/*     */     catch (Exception localException) {
/* 195 */       throw new SQLException("Provider " + paramString + " could not be instantiated: " + localException, localException);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static ClassLoader getContextClassLoader()
/*     */     throws SecurityException
/*     */   {
/* 207 */     return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public ClassLoader run() {
/* 210 */         ClassLoader localClassLoader = null;
/*     */ 
/* 212 */         localClassLoader = Thread.currentThread().getContextClassLoader();
/*     */ 
/* 214 */         if (localClassLoader == null) {
/* 215 */           localClassLoader = ClassLoader.getSystemClassLoader();
/*     */         }
/*     */ 
/* 218 */         return localClassLoader;
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private static Class getFactoryClass(String paramString, ClassLoader paramClassLoader, boolean paramBoolean)
/*     */     throws ClassNotFoundException
/*     */   {
/*     */     try
/*     */     {
/* 235 */       if (paramClassLoader == null) {
/* 236 */         paramClassLoader = getContextClassLoader();
/* 237 */         if (paramClassLoader == null) {
/* 238 */           throw new ClassNotFoundException();
/*     */         }
/* 240 */         return paramClassLoader.loadClass(paramString);
/*     */       }
/*     */ 
/* 243 */       return paramClassLoader.loadClass(paramString);
/*     */     }
/*     */     catch (ClassNotFoundException localClassNotFoundException) {
/* 246 */       if (paramBoolean)
/*     */       {
/* 248 */         return Class.forName(paramString, true, RowSetFactory.class.getClassLoader());
/*     */       }
/* 250 */       throw localClassNotFoundException;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static RowSetFactory loadViaServiceLoader()
/*     */     throws SQLException
/*     */   {
/* 260 */     Object localObject = null;
/*     */     try {
/* 262 */       trace("***in loadViaServiceLoader():");
/* 263 */       Iterator localIterator = ServiceLoader.load(RowSetFactory.class).iterator(); if (localIterator.hasNext()) { RowSetFactory localRowSetFactory = (RowSetFactory)localIterator.next();
/* 264 */         trace(" Loading done by the java.util.ServiceLoader :" + localRowSetFactory.getClass().getName());
/* 265 */         localObject = localRowSetFactory; }
/*     */     }
/*     */     catch (ServiceConfigurationError localServiceConfigurationError)
/*     */     {
/* 269 */       throw new SQLException("RowSetFactory: Error locating RowSetFactory using Service Loader API: " + localServiceConfigurationError, localServiceConfigurationError);
/*     */     }
/*     */ 
/* 273 */     return localObject;
/*     */   }
/*     */ 
/*     */   private static String getSystemProperty(String paramString)
/*     */   {
/* 285 */     String str = null;
/*     */     try {
/* 287 */       str = (String)AccessController.doPrivileged(new PrivilegedAction()
/*     */       {
/*     */         public String run() {
/* 290 */           return System.getProperty(this.val$propName);
/*     */         } } );
/*     */     }
/*     */     catch (SecurityException localSecurityException) {
/* 294 */       if (debug) {
/* 295 */         localSecurityException.printStackTrace();
/*     */       }
/*     */     }
/* 298 */     return str;
/*     */   }
/*     */ 
/*     */   private static void trace(String paramString)
/*     */   {
/* 307 */     if (debug)
/* 308 */       System.err.println("###RowSets: " + paramString);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  69 */     String str = getSystemProperty("javax.sql.rowset.RowSetProvider.debug");
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.sql.rowset.RowSetProvider
 * JD-Core Version:    0.6.2
 */