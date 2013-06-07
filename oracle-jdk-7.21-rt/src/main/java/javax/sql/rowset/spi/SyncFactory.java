/*     */ package javax.sql.rowset.spi;
/*     */ 
/*     */ import com.sun.rowset.providers.RIOptimisticProvider;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.sql.SQLPermission;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Properties;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import javax.naming.Binding;
/*     */ import javax.naming.Context;
/*     */ import javax.naming.NamingEnumeration;
/*     */ import javax.naming.NamingException;
/*     */ import javax.naming.NotContextException;
/*     */ 
/*     */ public class SyncFactory
/*     */ {
/*     */   public static final String ROWSET_SYNC_PROVIDER = "rowset.provider.classname";
/*     */   public static final String ROWSET_SYNC_VENDOR = "rowset.provider.vendor";
/*     */   public static final String ROWSET_SYNC_PROVIDER_VERSION = "rowset.provider.version";
/* 231 */   private static String ROWSET_PROPERTIES = "rowset.properties";
/*     */ 
/* 235 */   private static String default_provider = "com.sun.rowset.providers.RIOptimisticProvider";
/*     */ 
/* 240 */   private static final SQLPermission SET_SYNCFACTORY_PERMISSION = new SQLPermission("setSyncFactory");
/*     */   private static Context ic;
/*     */   private static volatile Logger rsLogger;
/*     */   private static Level rsLevel;
/*     */   private static Hashtable implementations;
/* 264 */   private static Object logSync = new Object();
/*     */ 
/* 268 */   private static PrintWriter logWriter = null;
/*     */ 
/* 336 */   private static String colon = ":";
/* 337 */   private static String strFileSep = "/";
/*     */ 
/* 431 */   private static boolean debug = false;
/*     */ 
/* 436 */   private static int providerImplIndex = 0;
/*     */ 
/* 735 */   private static boolean lazyJNDICtxRefresh = false;
/*     */ 
/*     */   public static synchronized void registerProvider(String paramString)
/*     */     throws SyncFactoryException
/*     */   {
/* 300 */     ProviderImpl localProviderImpl = new ProviderImpl();
/* 301 */     localProviderImpl.setClassname(paramString);
/* 302 */     initMapIfNecessary();
/* 303 */     implementations.put(paramString, localProviderImpl);
/*     */   }
/*     */ 
/*     */   public static SyncFactory getSyncFactory()
/*     */   {
/* 318 */     return SyncFactoryHolder.factory;
/*     */   }
/*     */ 
/*     */   public static synchronized void unregisterProvider(String paramString)
/*     */     throws SyncFactoryException
/*     */   {
/* 331 */     initMapIfNecessary();
/* 332 */     if (implementations.containsKey(paramString))
/* 333 */       implementations.remove(paramString);
/*     */   }
/*     */ 
/*     */   private static synchronized void initMapIfNecessary()
/*     */     throws SyncFactoryException
/*     */   {
/* 344 */     Properties localProperties = new Properties();
/*     */ 
/* 346 */     if (implementations == null)
/*     */     {
/* 347 */       implementations = new Hashtable();
/*     */       Object localObject2;
/*     */       try
/*     */       {
/* 365 */         String str1 = System.getProperty("rowset.properties");
/* 366 */         if (str1 != null)
/*     */         {
/* 369 */           ROWSET_PROPERTIES = str1;
/* 370 */           localObject1 = new FileInputStream(ROWSET_PROPERTIES); localObject2 = null;
/*     */           try { localProperties.load((InputStream)localObject1); }
/*     */           catch (Throwable localThrowable2)
/*     */           {
/* 370 */             localObject2 = localThrowable2; throw localThrowable2;
/*     */           } finally {
/* 372 */             if (localObject1 != null) if (localObject2 != null) try { ((FileInputStream)localObject1).close(); } catch (Throwable localThrowable5) { ((Throwable)localObject2).addSuppressed(localThrowable5); } else ((FileInputStream)localObject1).close(); 
/*     */           }
/* 373 */           parseProperties(localProperties);
/*     */         }
/*     */ 
/* 379 */         ROWSET_PROPERTIES = "javax" + strFileSep + "sql" + strFileSep + "rowset" + strFileSep + "rowset.properties";
/*     */ 
/* 383 */         Object localObject1 = Thread.currentThread().getContextClassLoader();
/*     */ 
/* 385 */         localObject2 = localObject1 == null ? ClassLoader.getSystemResourceAsStream(ROWSET_PROPERTIES) : ((ClassLoader)localObject1).getResourceAsStream(ROWSET_PROPERTIES); Object localObject3 = null;
/*     */         try
/*     */         {
/* 388 */           if (localObject2 == null) {
/* 389 */             throw new SyncFactoryException("Resource " + ROWSET_PROPERTIES + " not found");
/*     */           }
/*     */ 
/* 392 */           localProperties.load((InputStream)localObject2);
/*     */         }
/*     */         catch (Throwable localThrowable4)
/*     */         {
/* 385 */           localObject3 = localThrowable4; throw localThrowable4;
/*     */         }
/*     */         finally
/*     */         {
/* 393 */           if (localObject2 != null) if (localObject3 != null) try { ((InputStream)localObject2).close(); } catch (Throwable localThrowable6) { localObject3.addSuppressed(localThrowable6); } else ((InputStream)localObject2).close();
/*     */         }
/* 395 */         parseProperties(localProperties);
/*     */       }
/*     */       catch (FileNotFoundException localFileNotFoundException)
/*     */       {
/* 400 */         throw new SyncFactoryException("Cannot locate properties file: " + localFileNotFoundException);
/*     */       } catch (IOException localIOException) {
/* 402 */         throw new SyncFactoryException("IOException: " + localIOException);
/*     */       }
/*     */ 
/* 409 */       localProperties.clear();
/* 410 */       String str2 = System.getProperty("rowset.provider.classname");
/*     */ 
/* 412 */       if (str2 != null) {
/* 413 */         int i = 0;
/* 414 */         if (str2.indexOf(colon) > 0) {
/* 415 */           localObject2 = new StringTokenizer(str2, colon);
/* 416 */           while (((StringTokenizer)localObject2).hasMoreElements()) {
/* 417 */             localProperties.put("rowset.provider.classname." + i, ((StringTokenizer)localObject2).nextToken());
/* 418 */             i++;
/*     */           }
/*     */         } else {
/* 421 */           localProperties.put("rowset.provider.classname", str2);
/*     */         }
/* 423 */         parseProperties(localProperties);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void parseProperties(Properties paramProperties)
/*     */   {
/* 444 */     ProviderImpl localProviderImpl = null;
/* 445 */     String str1 = null;
/* 446 */     String[] arrayOfString = null;
/*     */ 
/* 448 */     for (Enumeration localEnumeration = paramProperties.propertyNames(); localEnumeration.hasMoreElements(); )
/*     */     {
/* 450 */       String str2 = (String)localEnumeration.nextElement();
/*     */ 
/* 452 */       int i = str2.length();
/*     */ 
/* 454 */       if (str2.startsWith("rowset.provider.classname"))
/*     */       {
/* 456 */         localProviderImpl = new ProviderImpl();
/* 457 */         localProviderImpl.setIndex(providerImplIndex++);
/*     */ 
/* 459 */         if (i == "rowset.provider.classname".length())
/*     */         {
/* 461 */           arrayOfString = getPropertyNames(false);
/*     */         }
/*     */         else {
/* 464 */           arrayOfString = getPropertyNames(true, str2.substring(i - 1));
/*     */         }
/*     */ 
/* 467 */         str1 = paramProperties.getProperty(arrayOfString[0]);
/* 468 */         localProviderImpl.setClassname(str1);
/* 469 */         localProviderImpl.setVendor(paramProperties.getProperty(arrayOfString[1]));
/* 470 */         localProviderImpl.setVersion(paramProperties.getProperty(arrayOfString[2]));
/* 471 */         implementations.put(str1, localProviderImpl);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static String[] getPropertyNames(boolean paramBoolean)
/*     */   {
/* 480 */     return getPropertyNames(paramBoolean, null);
/*     */   }
/*     */ 
/*     */   private static String[] getPropertyNames(boolean paramBoolean, String paramString)
/*     */   {
/* 489 */     String str = ".";
/* 490 */     String[] arrayOfString = { "rowset.provider.classname", "rowset.provider.vendor", "rowset.provider.version" };
/*     */ 
/* 494 */     if (paramBoolean) {
/* 495 */       for (int i = 0; i < arrayOfString.length; i++) {
/* 496 */         arrayOfString[i] = (arrayOfString[i] + str + paramString);
/*     */       }
/*     */ 
/* 500 */       return arrayOfString;
/*     */     }
/* 502 */     return arrayOfString;
/*     */   }
/*     */ 
/*     */   private static void showImpl(ProviderImpl paramProviderImpl)
/*     */   {
/* 510 */     System.out.println("Provider implementation:");
/* 511 */     System.out.println("Classname: " + paramProviderImpl.getClassname());
/* 512 */     System.out.println("Vendor: " + paramProviderImpl.getVendor());
/* 513 */     System.out.println("Version: " + paramProviderImpl.getVersion());
/* 514 */     System.out.println("Impl index: " + paramProviderImpl.getIndex());
/*     */   }
/*     */ 
/*     */   public static SyncProvider getInstance(String paramString)
/*     */     throws SyncFactoryException
/*     */   {
/* 529 */     if (paramString == null) {
/* 530 */       throw new SyncFactoryException("The providerID cannot be null");
/*     */     }
/*     */ 
/* 533 */     initMapIfNecessary();
/* 534 */     initJNDIContext();
/*     */ 
/* 536 */     ProviderImpl localProviderImpl = (ProviderImpl)implementations.get(paramString);
/*     */ 
/* 538 */     if (localProviderImpl == null)
/*     */     {
/* 540 */       return new RIOptimisticProvider();
/*     */     }
/*     */ 
/* 544 */     Class localClass = null;
/*     */     try {
/* 546 */       ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
/*     */ 
/* 554 */       localClass = Class.forName(paramString, true, localClassLoader);
/*     */ 
/* 556 */       if (localClass != null) {
/* 557 */         return (SyncProvider)localClass.newInstance();
/*     */       }
/* 559 */       return new RIOptimisticProvider();
/*     */     }
/*     */     catch (IllegalAccessException localIllegalAccessException)
/*     */     {
/* 563 */       throw new SyncFactoryException("IllegalAccessException: " + localIllegalAccessException.getMessage());
/*     */     } catch (InstantiationException localInstantiationException) {
/* 565 */       throw new SyncFactoryException("InstantiationException: " + localInstantiationException.getMessage());
/*     */     } catch (ClassNotFoundException localClassNotFoundException) {
/* 567 */       throw new SyncFactoryException("ClassNotFoundException: " + localClassNotFoundException.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public static Enumeration<SyncProvider> getRegisteredProviders()
/*     */     throws SyncFactoryException
/*     */   {
/* 585 */     initMapIfNecessary();
/*     */ 
/* 588 */     return implementations.elements();
/*     */   }
/*     */ 
/*     */   public static void setLogger(Logger paramLogger)
/*     */   {
/* 614 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 615 */     if (localSecurityManager != null) {
/* 616 */       localSecurityManager.checkPermission(SET_SYNCFACTORY_PERMISSION);
/*     */     }
/*     */ 
/* 619 */     if (paramLogger == null) {
/* 620 */       throw new NullPointerException("You must provide a Logger");
/*     */     }
/* 622 */     rsLogger = paramLogger;
/*     */   }
/*     */ 
/*     */   public static void setLogger(Logger paramLogger, Level paramLevel)
/*     */   {
/* 653 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 654 */     if (localSecurityManager != null) {
/* 655 */       localSecurityManager.checkPermission(SET_SYNCFACTORY_PERMISSION);
/*     */     }
/*     */ 
/* 658 */     if (paramLogger == null) {
/* 659 */       throw new NullPointerException("You must provide a Logger");
/*     */     }
/* 661 */     paramLogger.setLevel(paramLevel);
/* 662 */     rsLogger = paramLogger;
/*     */   }
/*     */ 
/*     */   public static Logger getLogger()
/*     */     throws SyncFactoryException
/*     */   {
/* 673 */     Logger localLogger = rsLogger;
/*     */ 
/* 675 */     if (localLogger == null) {
/* 676 */       throw new SyncFactoryException("(SyncFactory) : No logger has been set");
/*     */     }
/*     */ 
/* 679 */     return localLogger;
/*     */   }
/*     */ 
/*     */   public static synchronized void setJNDIContext(Context paramContext)
/*     */     throws SyncFactoryException
/*     */   {
/* 702 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 703 */     if (localSecurityManager != null) {
/* 704 */       localSecurityManager.checkPermission(SET_SYNCFACTORY_PERMISSION);
/*     */     }
/* 706 */     if (paramContext == null) {
/* 707 */       throw new SyncFactoryException("Invalid JNDI context supplied");
/*     */     }
/* 709 */     ic = paramContext;
/*     */   }
/*     */ 
/*     */   private static synchronized void initJNDIContext()
/*     */     throws SyncFactoryException
/*     */   {
/* 719 */     if ((ic != null) && (!lazyJNDICtxRefresh))
/*     */       try {
/* 721 */         parseProperties(parseJNDIContext());
/* 722 */         lazyJNDICtxRefresh = true;
/*     */       } catch (NamingException localNamingException) {
/* 724 */         localNamingException.printStackTrace();
/* 725 */         throw new SyncFactoryException("SPI: NamingException: " + localNamingException.getExplanation());
/*     */       } catch (Exception localException) {
/* 727 */         localException.printStackTrace();
/* 728 */         throw new SyncFactoryException("SPI: Exception: " + localException.getMessage());
/*     */       }
/*     */   }
/*     */ 
/*     */   private static Properties parseJNDIContext()
/*     */     throws NamingException
/*     */   {
/* 743 */     NamingEnumeration localNamingEnumeration = ic.listBindings("");
/* 744 */     Properties localProperties = new Properties();
/*     */ 
/* 747 */     enumerateBindings(localNamingEnumeration, localProperties);
/*     */ 
/* 749 */     return localProperties;
/*     */   }
/*     */ 
/*     */   private static void enumerateBindings(NamingEnumeration paramNamingEnumeration, Properties paramProperties)
/*     */     throws NamingException
/*     */   {
/* 761 */     int i = 0;
/*     */     try
/*     */     {
/* 764 */       Binding localBinding = null;
/* 765 */       Object localObject = null;
/* 766 */       String str = null;
/* 767 */       while (paramNamingEnumeration.hasMore()) {
/* 768 */         localBinding = (Binding)paramNamingEnumeration.next();
/* 769 */         str = localBinding.getName();
/* 770 */         localObject = localBinding.getObject();
/*     */ 
/* 772 */         if (!(ic.lookup(str) instanceof Context))
/*     */         {
/* 774 */           if ((ic.lookup(str) instanceof SyncProvider)) {
/* 775 */             i = 1;
/*     */           }
/*     */         }
/*     */ 
/* 779 */         if (i != 0) {
/* 780 */           SyncProvider localSyncProvider = (SyncProvider)localObject;
/* 781 */           paramProperties.put("rowset.provider.classname", localSyncProvider.getProviderID());
/*     */ 
/* 783 */           i = 0;
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (NotContextException localNotContextException) {
/* 788 */       paramNamingEnumeration.next();
/*     */ 
/* 790 */       enumerateBindings(paramNamingEnumeration, paramProperties);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class SyncFactoryHolder
/*     */   {
/* 798 */     static final SyncFactory factory = new SyncFactory(null);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.sql.rowset.spi.SyncFactory
 * JD-Core Version:    0.6.2
 */