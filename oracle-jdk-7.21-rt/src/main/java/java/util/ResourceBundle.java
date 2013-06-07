/*      */ package java.util;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.lang.ref.Reference;
/*      */ import java.lang.ref.ReferenceQueue;
/*      */ import java.lang.ref.SoftReference;
/*      */ import java.lang.ref.WeakReference;
/*      */ import java.net.JarURLConnection;
/*      */ import java.net.URL;
/*      */ import java.net.URLConnection;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.security.PrivilegedActionException;
/*      */ import java.security.PrivilegedExceptionAction;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ import java.util.concurrent.ConcurrentMap;
/*      */ import java.util.jar.JarEntry;
/*      */ import sun.util.locale.BaseLocale;
/*      */ import sun.util.locale.LocaleObjectCache;
/*      */ 
/*      */ public abstract class ResourceBundle
/*      */ {
/*      */   private static final int INITIAL_CACHE_SIZE = 32;
/*  272 */   private static final ResourceBundle NONEXISTENT_BUNDLE = new ResourceBundle() {
/*  273 */     public Enumeration<String> getKeys() { return null; } 
/*  274 */     protected Object handleGetObject(String paramAnonymousString) { return null; } 
/*  275 */     public String toString() { return "NONEXISTENT_BUNDLE"; }
/*      */ 
/*  272 */   };
/*      */ 
/*  291 */   private static final ConcurrentMap<CacheKey, BundleReference> cacheList = new ConcurrentHashMap(32);
/*      */ 
/*  297 */   private static final ReferenceQueue referenceQueue = new ReferenceQueue();
/*      */ 
/*  304 */   protected ResourceBundle parent = null;
/*      */ 
/*  309 */   private Locale locale = null;
/*      */   private String name;
/*      */   private volatile boolean expired;
/*      */   private volatile CacheKey cacheKey;
/*      */   private volatile Set<String> keySet;
/*      */ 
/*      */   public final String getString(String paramString)
/*      */   {
/*  353 */     return (String)getObject(paramString);
/*      */   }
/*      */ 
/*      */   public final String[] getStringArray(String paramString)
/*      */   {
/*  370 */     return (String[])getObject(paramString);
/*      */   }
/*      */ 
/*      */   public final Object getObject(String paramString)
/*      */   {
/*  387 */     Object localObject = handleGetObject(paramString);
/*  388 */     if (localObject == null) {
/*  389 */       if (this.parent != null) {
/*  390 */         localObject = this.parent.getObject(paramString);
/*      */       }
/*  392 */       if (localObject == null) {
/*  393 */         throw new MissingResourceException("Can't find resource for bundle " + getClass().getName() + ", key " + paramString, getClass().getName(), paramString);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  399 */     return localObject;
/*      */   }
/*      */ 
/*      */   public Locale getLocale()
/*      */   {
/*  410 */     return this.locale;
/*      */   }
/*      */ 
/*      */   private static ClassLoader getLoader()
/*      */   {
/*  419 */     Class[] arrayOfClass = getClassContext();
/*      */ 
/*  421 */     Class localClass = arrayOfClass[2];
/*  422 */     Object localObject = localClass == null ? null : localClass.getClassLoader();
/*  423 */     if (localObject == null)
/*      */     {
/*  431 */       localObject = RBClassLoader.INSTANCE;
/*      */     }
/*  433 */     return localObject;
/*      */   }
/*      */ 
/*      */   private static native Class[] getClassContext();
/*      */ 
/*      */   protected void setParent(ResourceBundle paramResourceBundle)
/*      */   {
/*  480 */     assert (paramResourceBundle != NONEXISTENT_BUNDLE);
/*  481 */     this.parent = paramResourceBundle;
/*      */   }
/*      */ 
/*      */   public static final ResourceBundle getBundle(String paramString)
/*      */   {
/*  724 */     return getBundleImpl(paramString, Locale.getDefault(), getLoader(), Control.INSTANCE);
/*      */   }
/*      */ 
/*      */   public static final ResourceBundle getBundle(String paramString, Control paramControl)
/*      */   {
/*  766 */     return getBundleImpl(paramString, Locale.getDefault(), getLoader(), paramControl);
/*      */   }
/*      */ 
/*      */   public static final ResourceBundle getBundle(String paramString, Locale paramLocale)
/*      */   {
/*  796 */     return getBundleImpl(paramString, paramLocale, getLoader(), Control.INSTANCE);
/*      */   }
/*      */ 
/*      */   public static final ResourceBundle getBundle(String paramString, Locale paramLocale, Control paramControl)
/*      */   {
/*  841 */     return getBundleImpl(paramString, paramLocale, getLoader(), paramControl);
/*      */   }
/*      */ 
/*      */   public static ResourceBundle getBundle(String paramString, Locale paramLocale, ClassLoader paramClassLoader)
/*      */   {
/* 1025 */     if (paramClassLoader == null) {
/* 1026 */       throw new NullPointerException();
/*      */     }
/* 1028 */     return getBundleImpl(paramString, paramLocale, paramClassLoader, Control.INSTANCE);
/*      */   }
/*      */ 
/*      */   public static ResourceBundle getBundle(String paramString, Locale paramLocale, ClassLoader paramClassLoader, Control paramControl)
/*      */   {
/* 1243 */     if ((paramClassLoader == null) || (paramControl == null)) {
/* 1244 */       throw new NullPointerException();
/*      */     }
/* 1246 */     return getBundleImpl(paramString, paramLocale, paramClassLoader, paramControl);
/*      */   }
/*      */ 
/*      */   private static ResourceBundle getBundleImpl(String paramString, Locale paramLocale, ClassLoader paramClassLoader, Control paramControl)
/*      */   {
/* 1251 */     if ((paramLocale == null) || (paramControl == null)) {
/* 1252 */       throw new NullPointerException();
/*      */     }
/*      */ 
/* 1259 */     CacheKey localCacheKey = new CacheKey(paramString, paramLocale, paramClassLoader);
/* 1260 */     Object localObject1 = null;
/*      */ 
/* 1263 */     BundleReference localBundleReference = (BundleReference)cacheList.get(localCacheKey);
/* 1264 */     if (localBundleReference != null) {
/* 1265 */       localObject1 = (ResourceBundle)localBundleReference.get();
/* 1266 */       localBundleReference = null;
/*      */     }
/*      */ 
/* 1273 */     if ((isValidBundle((ResourceBundle)localObject1)) && (hasValidParentChain((ResourceBundle)localObject1))) {
/* 1274 */       return localObject1;
/*      */     }
/*      */ 
/* 1280 */     int i = (paramControl == Control.INSTANCE) || ((paramControl instanceof SingleFormatControl)) ? 1 : 0;
/*      */ 
/* 1282 */     List localList1 = paramControl.getFormats(paramString);
/* 1283 */     if ((i == 0) && (!checkList(localList1))) {
/* 1284 */       throw new IllegalArgumentException("Invalid Control: getFormats");
/*      */     }
/*      */ 
/* 1287 */     Object localObject2 = null;
/* 1288 */     for (Locale localLocale = paramLocale; 
/* 1289 */       localLocale != null; 
/* 1290 */       localLocale = paramControl.getFallbackLocale(paramString, localLocale)) {
/* 1291 */       List localList2 = paramControl.getCandidateLocales(paramString, localLocale);
/* 1292 */       if ((i == 0) && (!checkList(localList2))) {
/* 1293 */         throw new IllegalArgumentException("Invalid Control: getCandidateLocales");
/*      */       }
/*      */ 
/* 1296 */       localObject1 = findBundle(localCacheKey, localList2, localList1, 0, paramControl, (ResourceBundle)localObject2);
/*      */ 
/* 1303 */       if (isValidBundle((ResourceBundle)localObject1)) {
/* 1304 */         boolean bool = Locale.ROOT.equals(((ResourceBundle)localObject1).locale);
/* 1305 */         if ((!bool) || (((ResourceBundle)localObject1).locale.equals(paramLocale)) || ((localList2.size() == 1) && (((ResourceBundle)localObject1).locale.equals(localList2.get(0)))))
/*      */         {
/*      */           break;
/*      */         }
/*      */ 
/* 1314 */         if ((bool) && (localObject2 == null)) {
/* 1315 */           localObject2 = localObject1;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1320 */     if (localObject1 == null) {
/* 1321 */       if (localObject2 == null) {
/* 1322 */         throwMissingResourceException(paramString, paramLocale, localCacheKey.getCause());
/*      */       }
/* 1324 */       localObject1 = localObject2;
/*      */     }
/*      */ 
/* 1327 */     return localObject1;
/*      */   }
/*      */ 
/*      */   private static final boolean checkList(List paramList)
/*      */   {
/* 1335 */     boolean bool = (paramList != null) && (paramList.size() != 0);
/* 1336 */     if (bool) {
/* 1337 */       int i = paramList.size();
/* 1338 */       for (int j = 0; (bool) && (j < i); j++) {
/* 1339 */         bool = paramList.get(j) != null;
/*      */       }
/*      */     }
/* 1342 */     return bool;
/*      */   }
/*      */ 
/*      */   private static final ResourceBundle findBundle(CacheKey paramCacheKey, List<Locale> paramList, List<String> paramList1, int paramInt, Control paramControl, ResourceBundle paramResourceBundle)
/*      */   {
/* 1351 */     Locale localLocale = (Locale)paramList.get(paramInt);
/* 1352 */     ResourceBundle localResourceBundle1 = null;
/* 1353 */     if (paramInt != paramList.size() - 1) {
/* 1354 */       localResourceBundle1 = findBundle(paramCacheKey, paramList, paramList1, paramInt + 1, paramControl, paramResourceBundle);
/*      */     }
/* 1356 */     else if ((paramResourceBundle != null) && (Locale.ROOT.equals(localLocale)))
/* 1357 */       return paramResourceBundle;
/*      */     Reference localReference;
/* 1365 */     while ((localReference = referenceQueue.poll()) != null) {
/* 1366 */       cacheList.remove(((CacheKeyReference)localReference).getCacheKey());
/*      */     }
/*      */ 
/* 1370 */     boolean bool = false;
/*      */ 
/* 1374 */     paramCacheKey.setLocale(localLocale);
/* 1375 */     ResourceBundle localResourceBundle2 = findBundleInCache(paramCacheKey, paramControl);
/*      */     Object localObject1;
/* 1376 */     if (isValidBundle(localResourceBundle2)) {
/* 1377 */       bool = localResourceBundle2.expired;
/* 1378 */       if (!bool)
/*      */       {
/* 1384 */         if (localResourceBundle2.parent == localResourceBundle1) {
/* 1385 */           return localResourceBundle2;
/*      */         }
/*      */ 
/* 1389 */         localObject1 = (BundleReference)cacheList.get(paramCacheKey);
/* 1390 */         if ((localObject1 != null) && (((BundleReference)localObject1).get() == localResourceBundle2)) {
/* 1391 */           cacheList.remove(paramCacheKey, localObject1);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1396 */     if (localResourceBundle2 != NONEXISTENT_BUNDLE) {
/* 1397 */       localObject1 = (CacheKey)paramCacheKey.clone();
/*      */       try
/*      */       {
/* 1400 */         localResourceBundle2 = loadBundle(paramCacheKey, paramList1, paramControl, bool);
/* 1401 */         if (localResourceBundle2 != null) {
/* 1402 */           if (localResourceBundle2.parent == null) {
/* 1403 */             localResourceBundle2.setParent(localResourceBundle1);
/*      */           }
/* 1405 */           localResourceBundle2.locale = localLocale;
/* 1406 */           localResourceBundle2 = putBundleInCache(paramCacheKey, localResourceBundle2, paramControl);
/* 1407 */           return localResourceBundle2;
/*      */         }
/*      */ 
/* 1412 */         putBundleInCache(paramCacheKey, NONEXISTENT_BUNDLE, paramControl);
/*      */       } finally {
/* 1414 */         if ((((CacheKey)localObject1).getCause() instanceof InterruptedException)) {
/* 1415 */           Thread.currentThread().interrupt();
/*      */         }
/*      */       }
/*      */     }
/* 1419 */     return localResourceBundle1;
/*      */   }
/*      */ 
/*      */   private static final ResourceBundle loadBundle(CacheKey paramCacheKey, List<String> paramList, Control paramControl, boolean paramBoolean)
/*      */   {
/* 1429 */     Locale localLocale = paramCacheKey.getLocale();
/*      */ 
/* 1431 */     ResourceBundle localResourceBundle = null;
/* 1432 */     int i = paramList.size();
/* 1433 */     for (int j = 0; j < i; j++) {
/* 1434 */       String str = (String)paramList.get(j);
/*      */       try {
/* 1436 */         localResourceBundle = paramControl.newBundle(paramCacheKey.getName(), localLocale, str, paramCacheKey.getLoader(), paramBoolean);
/*      */       }
/*      */       catch (LinkageError localLinkageError)
/*      */       {
/* 1442 */         paramCacheKey.setCause(localLinkageError);
/*      */       } catch (Exception localException) {
/* 1444 */         paramCacheKey.setCause(localException);
/*      */       }
/* 1446 */       if (localResourceBundle != null)
/*      */       {
/* 1449 */         paramCacheKey.setFormat(str);
/* 1450 */         localResourceBundle.name = paramCacheKey.getName();
/* 1451 */         localResourceBundle.locale = localLocale;
/*      */ 
/* 1454 */         localResourceBundle.expired = false;
/* 1455 */         break;
/*      */       }
/*      */     }
/*      */ 
/* 1459 */     return localResourceBundle;
/*      */   }
/*      */ 
/*      */   private static final boolean isValidBundle(ResourceBundle paramResourceBundle) {
/* 1463 */     return (paramResourceBundle != null) && (paramResourceBundle != NONEXISTENT_BUNDLE);
/*      */   }
/*      */ 
/*      */   private static final boolean hasValidParentChain(ResourceBundle paramResourceBundle)
/*      */   {
/* 1471 */     long l1 = System.currentTimeMillis();
/* 1472 */     while (paramResourceBundle != null) {
/* 1473 */       if (paramResourceBundle.expired) {
/* 1474 */         return false;
/*      */       }
/* 1476 */       CacheKey localCacheKey = paramResourceBundle.cacheKey;
/* 1477 */       if (localCacheKey != null) {
/* 1478 */         long l2 = localCacheKey.expirationTime;
/* 1479 */         if ((l2 >= 0L) && (l2 <= l1)) {
/* 1480 */           return false;
/*      */         }
/*      */       }
/* 1483 */       paramResourceBundle = paramResourceBundle.parent;
/*      */     }
/* 1485 */     return true;
/*      */   }
/*      */ 
/*      */   private static final void throwMissingResourceException(String paramString, Locale paramLocale, Throwable paramThrowable)
/*      */   {
/* 1496 */     if ((paramThrowable instanceof MissingResourceException)) {
/* 1497 */       paramThrowable = null;
/*      */     }
/* 1499 */     throw new MissingResourceException("Can't find bundle for base name " + paramString + ", locale " + paramLocale, paramString + "_" + paramLocale, "", paramThrowable);
/*      */   }
/*      */ 
/*      */   private static final ResourceBundle findBundleInCache(CacheKey paramCacheKey, Control paramControl)
/*      */   {
/* 1518 */     BundleReference localBundleReference = (BundleReference)cacheList.get(paramCacheKey);
/* 1519 */     if (localBundleReference == null) {
/* 1520 */       return null;
/*      */     }
/* 1522 */     ResourceBundle localResourceBundle1 = (ResourceBundle)localBundleReference.get();
/* 1523 */     if (localResourceBundle1 == null) {
/* 1524 */       return null;
/*      */     }
/* 1526 */     ResourceBundle localResourceBundle2 = localResourceBundle1.parent;
/* 1527 */     assert (localResourceBundle2 != NONEXISTENT_BUNDLE);
/*      */ 
/* 1561 */     if ((localResourceBundle2 != null) && (localResourceBundle2.expired)) {
/* 1562 */       assert (localResourceBundle1 != NONEXISTENT_BUNDLE);
/* 1563 */       localResourceBundle1.expired = true;
/* 1564 */       localResourceBundle1.cacheKey = null;
/* 1565 */       cacheList.remove(paramCacheKey, localBundleReference);
/* 1566 */       localResourceBundle1 = null;
/*      */     } else {
/* 1568 */       CacheKey localCacheKey = localBundleReference.getCacheKey();
/* 1569 */       long l = localCacheKey.expirationTime;
/* 1570 */       if ((!localResourceBundle1.expired) && (l >= 0L) && (l <= System.currentTimeMillis()))
/*      */       {
/* 1573 */         if (localResourceBundle1 != NONEXISTENT_BUNDLE)
/*      */         {
/* 1576 */           synchronized (localResourceBundle1) {
/* 1577 */             l = localCacheKey.expirationTime;
/* 1578 */             if ((!localResourceBundle1.expired) && (l >= 0L) && (l <= System.currentTimeMillis()))
/*      */             {
/*      */               try {
/* 1581 */                 localResourceBundle1.expired = paramControl.needsReload(localCacheKey.getName(), localCacheKey.getLocale(), localCacheKey.getFormat(), localCacheKey.getLoader(), localResourceBundle1, localCacheKey.loadTime);
/*      */               }
/*      */               catch (Exception localException)
/*      */               {
/* 1588 */                 paramCacheKey.setCause(localException);
/*      */               }
/* 1590 */               if (localResourceBundle1.expired)
/*      */               {
/* 1595 */                 localResourceBundle1.cacheKey = null;
/* 1596 */                 cacheList.remove(paramCacheKey, localBundleReference);
/*      */               }
/*      */               else
/*      */               {
/* 1600 */                 setExpirationTime(localCacheKey, paramControl);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         else {
/* 1606 */           cacheList.remove(paramCacheKey, localBundleReference);
/* 1607 */           localResourceBundle1 = null;
/*      */         }
/*      */       }
/*      */     }
/* 1611 */     return localResourceBundle1;
/*      */   }
/*      */ 
/*      */   private static final ResourceBundle putBundleInCache(CacheKey paramCacheKey, ResourceBundle paramResourceBundle, Control paramControl)
/*      */   {
/* 1626 */     setExpirationTime(paramCacheKey, paramControl);
/* 1627 */     if (paramCacheKey.expirationTime != -1L) {
/* 1628 */       CacheKey localCacheKey = (CacheKey)paramCacheKey.clone();
/* 1629 */       BundleReference localBundleReference1 = new BundleReference(paramResourceBundle, referenceQueue, localCacheKey);
/* 1630 */       paramResourceBundle.cacheKey = localCacheKey;
/*      */ 
/* 1633 */       BundleReference localBundleReference2 = (BundleReference)cacheList.putIfAbsent(localCacheKey, localBundleReference1);
/*      */ 
/* 1637 */       if (localBundleReference2 != null) {
/* 1638 */         ResourceBundle localResourceBundle = (ResourceBundle)localBundleReference2.get();
/* 1639 */         if ((localResourceBundle != null) && (!localResourceBundle.expired))
/*      */         {
/* 1641 */           paramResourceBundle.cacheKey = null;
/* 1642 */           paramResourceBundle = localResourceBundle;
/*      */ 
/* 1645 */           localBundleReference1.clear();
/*      */         }
/*      */         else
/*      */         {
/* 1649 */           cacheList.put(localCacheKey, localBundleReference1);
/*      */         }
/*      */       }
/*      */     }
/* 1653 */     return paramResourceBundle;
/*      */   }
/*      */ 
/*      */   private static final void setExpirationTime(CacheKey paramCacheKey, Control paramControl) {
/* 1657 */     long l1 = paramControl.getTimeToLive(paramCacheKey.getName(), paramCacheKey.getLocale());
/*      */ 
/* 1659 */     if (l1 >= 0L)
/*      */     {
/* 1662 */       long l2 = System.currentTimeMillis();
/* 1663 */       paramCacheKey.loadTime = l2;
/* 1664 */       paramCacheKey.expirationTime = (l2 + l1);
/* 1665 */     } else if (l1 >= -2L) {
/* 1666 */       paramCacheKey.expirationTime = l1;
/*      */     } else {
/* 1668 */       throw new IllegalArgumentException("Invalid Control: TTL=" + l1);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static final void clearCache()
/*      */   {
/* 1680 */     clearCache(getLoader());
/*      */   }
/*      */ 
/*      */   public static final void clearCache(ClassLoader paramClassLoader)
/*      */   {
/* 1693 */     if (paramClassLoader == null) {
/* 1694 */       throw new NullPointerException();
/*      */     }
/* 1696 */     Set localSet = cacheList.keySet();
/* 1697 */     for (CacheKey localCacheKey : localSet)
/* 1698 */       if (localCacheKey.getLoader() == paramClassLoader)
/* 1699 */         localSet.remove(localCacheKey);
/*      */   }
/*      */ 
/*      */   protected abstract Object handleGetObject(String paramString);
/*      */ 
/*      */   public abstract Enumeration<String> getKeys();
/*      */ 
/*      */   public boolean containsKey(String paramString)
/*      */   {
/* 1737 */     if (paramString == null) {
/* 1738 */       throw new NullPointerException();
/*      */     }
/* 1740 */     for (ResourceBundle localResourceBundle = this; localResourceBundle != null; localResourceBundle = localResourceBundle.parent) {
/* 1741 */       if (localResourceBundle.handleKeySet().contains(paramString)) {
/* 1742 */         return true;
/*      */       }
/*      */     }
/* 1745 */     return false;
/*      */   }
/*      */ 
/*      */   public Set<String> keySet()
/*      */   {
/* 1757 */     HashSet localHashSet = new HashSet();
/* 1758 */     for (ResourceBundle localResourceBundle = this; localResourceBundle != null; localResourceBundle = localResourceBundle.parent) {
/* 1759 */       localHashSet.addAll(localResourceBundle.handleKeySet());
/*      */     }
/* 1761 */     return localHashSet;
/*      */   }
/*      */ 
/*      */   protected Set<String> handleKeySet()
/*      */   {
/* 1782 */     if (this.keySet == null) {
/* 1783 */       synchronized (this) {
/* 1784 */         if (this.keySet == null) {
/* 1785 */           HashSet localHashSet = new HashSet();
/* 1786 */           Enumeration localEnumeration = getKeys();
/* 1787 */           while (localEnumeration.hasMoreElements()) {
/* 1788 */             String str = (String)localEnumeration.nextElement();
/* 1789 */             if (handleGetObject(str) != null) {
/* 1790 */               localHashSet.add(str);
/*      */             }
/*      */           }
/* 1793 */           this.keySet = localHashSet;
/*      */         }
/*      */       }
/*      */     }
/* 1797 */     return this.keySet;
/*      */   }
/*      */ 
/*      */   private static final class BundleReference extends SoftReference<ResourceBundle>
/*      */     implements ResourceBundle.CacheKeyReference
/*      */   {
/*      */     private ResourceBundle.CacheKey cacheKey;
/*      */ 
/*      */     BundleReference(ResourceBundle paramResourceBundle, ReferenceQueue paramReferenceQueue, ResourceBundle.CacheKey paramCacheKey)
/*      */     {
/*  695 */       super(paramReferenceQueue);
/*  696 */       this.cacheKey = paramCacheKey;
/*      */     }
/*      */ 
/*      */     public ResourceBundle.CacheKey getCacheKey() {
/*  700 */       return this.cacheKey;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static final class CacheKey
/*      */     implements Cloneable
/*      */   {
/*      */     private String name;
/*      */     private Locale locale;
/*      */     private ResourceBundle.LoaderReference loaderRef;
/*      */     private String format;
/*      */     private volatile long loadTime;
/*      */     private volatile long expirationTime;
/*      */     private Throwable cause;
/*      */     private int hashCodeCache;
/*      */ 
/*      */     CacheKey(String paramString, Locale paramLocale, ClassLoader paramClassLoader)
/*      */     {
/*  519 */       this.name = paramString;
/*  520 */       this.locale = paramLocale;
/*  521 */       if (paramClassLoader == null)
/*  522 */         this.loaderRef = null;
/*      */       else {
/*  524 */         this.loaderRef = new ResourceBundle.LoaderReference(paramClassLoader, ResourceBundle.referenceQueue, this);
/*      */       }
/*  526 */       calculateHashCode();
/*      */     }
/*      */ 
/*      */     String getName() {
/*  530 */       return this.name;
/*      */     }
/*      */ 
/*      */     CacheKey setName(String paramString) {
/*  534 */       if (!this.name.equals(paramString)) {
/*  535 */         this.name = paramString;
/*  536 */         calculateHashCode();
/*      */       }
/*  538 */       return this;
/*      */     }
/*      */ 
/*      */     Locale getLocale() {
/*  542 */       return this.locale;
/*      */     }
/*      */ 
/*      */     CacheKey setLocale(Locale paramLocale) {
/*  546 */       if (!this.locale.equals(paramLocale)) {
/*  547 */         this.locale = paramLocale;
/*  548 */         calculateHashCode();
/*      */       }
/*  550 */       return this;
/*      */     }
/*      */ 
/*      */     ClassLoader getLoader() {
/*  554 */       return this.loaderRef != null ? (ClassLoader)this.loaderRef.get() : null;
/*      */     }
/*      */ 
/*      */     public boolean equals(Object paramObject) {
/*  558 */       if (this == paramObject)
/*  559 */         return true;
/*      */       try
/*      */       {
/*  562 */         CacheKey localCacheKey = (CacheKey)paramObject;
/*      */ 
/*  564 */         if (this.hashCodeCache != localCacheKey.hashCodeCache) {
/*  565 */           return false;
/*      */         }
/*      */ 
/*  568 */         if (!this.name.equals(localCacheKey.name)) {
/*  569 */           return false;
/*      */         }
/*      */ 
/*  572 */         if (!this.locale.equals(localCacheKey.locale)) {
/*  573 */           return false;
/*      */         }
/*      */ 
/*  576 */         if (this.loaderRef == null) {
/*  577 */           return localCacheKey.loaderRef == null;
/*      */         }
/*  579 */         ClassLoader localClassLoader = (ClassLoader)this.loaderRef.get();
/*  580 */         return (localCacheKey.loaderRef != null) && (localClassLoader != null) && (localClassLoader == localCacheKey.loaderRef.get());
/*      */       }
/*      */       catch (NullPointerException localNullPointerException)
/*      */       {
/*      */       }
/*      */       catch (ClassCastException localClassCastException)
/*      */       {
/*      */       }
/*      */ 
/*  589 */       return false;
/*      */     }
/*      */ 
/*      */     public int hashCode() {
/*  593 */       return this.hashCodeCache;
/*      */     }
/*      */ 
/*      */     private void calculateHashCode() {
/*  597 */       this.hashCodeCache = (this.name.hashCode() << 3);
/*  598 */       this.hashCodeCache ^= this.locale.hashCode();
/*  599 */       ClassLoader localClassLoader = getLoader();
/*  600 */       if (localClassLoader != null)
/*  601 */         this.hashCodeCache ^= localClassLoader.hashCode();
/*      */     }
/*      */ 
/*      */     public Object clone()
/*      */     {
/*      */       try {
/*  607 */         CacheKey localCacheKey = (CacheKey)super.clone();
/*  608 */         if (this.loaderRef != null) {
/*  609 */           localCacheKey.loaderRef = new ResourceBundle.LoaderReference((ClassLoader)this.loaderRef.get(), ResourceBundle.referenceQueue, localCacheKey);
/*      */         }
/*      */ 
/*  613 */         localCacheKey.cause = null;
/*  614 */         return localCacheKey;
/*      */       } catch (CloneNotSupportedException localCloneNotSupportedException) {
/*      */       }
/*  617 */       throw new InternalError();
/*      */     }
/*      */ 
/*      */     String getFormat()
/*      */     {
/*  622 */       return this.format;
/*      */     }
/*      */ 
/*      */     void setFormat(String paramString) {
/*  626 */       this.format = paramString;
/*      */     }
/*      */ 
/*      */     private void setCause(Throwable paramThrowable) {
/*  630 */       if (this.cause == null) {
/*  631 */         this.cause = paramThrowable;
/*      */       }
/*  635 */       else if ((this.cause instanceof ClassNotFoundException))
/*  636 */         this.cause = paramThrowable;
/*      */     }
/*      */ 
/*      */     private Throwable getCause()
/*      */     {
/*  642 */       return this.cause;
/*      */     }
/*      */ 
/*      */     public String toString() {
/*  646 */       String str = this.locale.toString();
/*  647 */       if (str.length() == 0) {
/*  648 */         if (this.locale.getVariant().length() != 0)
/*  649 */           str = "__" + this.locale.getVariant();
/*      */         else {
/*  651 */           str = "\"\"";
/*      */         }
/*      */       }
/*  654 */       return "CacheKey[" + this.name + ", lc=" + str + ", ldr=" + getLoader() + "(format=" + this.format + ")]";
/*      */     }
/*      */   }
/*      */ 
/*      */   private static abstract interface CacheKeyReference
/*      */   {
/*      */     public abstract ResourceBundle.CacheKey getCacheKey();
/*      */   }
/*      */ 
/*      */   public static class Control
/*      */   {
/* 1958 */     public static final List<String> FORMAT_DEFAULT = Collections.unmodifiableList(Arrays.asList(new String[] { "java.class", "java.properties" }));
/*      */ 
/* 1969 */     public static final List<String> FORMAT_CLASS = Collections.unmodifiableList(Arrays.asList(new String[] { "java.class" }));
/*      */ 
/* 1979 */     public static final List<String> FORMAT_PROPERTIES = Collections.unmodifiableList(Arrays.asList(new String[] { "java.properties" }));
/*      */     public static final long TTL_DONT_CACHE = -1L;
/*      */     public static final long TTL_NO_EXPIRATION_CONTROL = -2L;
/* 1998 */     private static final Control INSTANCE = new Control();
/*      */ 
/* 2306 */     private static final CandidateListCache CANDIDATES_CACHE = new CandidateListCache(null);
/*      */ 
/*      */     public static final Control getControl(List<String> paramList)
/*      */     {
/* 2031 */       if (paramList.equals(FORMAT_PROPERTIES)) {
/* 2032 */         return ResourceBundle.SingleFormatControl.access$800();
/*      */       }
/* 2034 */       if (paramList.equals(FORMAT_CLASS)) {
/* 2035 */         return ResourceBundle.SingleFormatControl.access$900();
/*      */       }
/* 2037 */       if (paramList.equals(FORMAT_DEFAULT)) {
/* 2038 */         return INSTANCE;
/*      */       }
/* 2040 */       throw new IllegalArgumentException();
/*      */     }
/*      */ 
/*      */     public static final Control getNoFallbackControl(List<String> paramList)
/*      */     {
/* 2066 */       if (paramList.equals(FORMAT_DEFAULT)) {
/* 2067 */         return ResourceBundle.NoFallbackControl.access$1000();
/*      */       }
/* 2069 */       if (paramList.equals(FORMAT_PROPERTIES)) {
/* 2070 */         return ResourceBundle.NoFallbackControl.access$1100();
/*      */       }
/* 2072 */       if (paramList.equals(FORMAT_CLASS)) {
/* 2073 */         return ResourceBundle.NoFallbackControl.access$1200();
/*      */       }
/* 2075 */       throw new IllegalArgumentException();
/*      */     }
/*      */ 
/*      */     public List<String> getFormats(String paramString)
/*      */     {
/* 2113 */       if (paramString == null) {
/* 2114 */         throw new NullPointerException();
/*      */       }
/* 2116 */       return FORMAT_DEFAULT;
/*      */     }
/*      */ 
/*      */     public List<Locale> getCandidateLocales(String paramString, Locale paramLocale)
/*      */     {
/* 2300 */       if (paramString == null) {
/* 2301 */         throw new NullPointerException();
/*      */       }
/* 2303 */       return new ArrayList((Collection)CANDIDATES_CACHE.get(paramLocale.getBaseLocale()));
/*      */     }
/*      */ 
/*      */     public Locale getFallbackLocale(String paramString, Locale paramLocale)
/*      */     {
/* 2455 */       if (paramString == null) {
/* 2456 */         throw new NullPointerException();
/*      */       }
/* 2458 */       Locale localLocale = Locale.getDefault();
/* 2459 */       return paramLocale.equals(localLocale) ? null : localLocale;
/*      */     }
/*      */ 
/*      */     public ResourceBundle newBundle(String paramString1, Locale paramLocale, String paramString2, ClassLoader paramClassLoader, boolean paramBoolean)
/*      */       throws IllegalAccessException, InstantiationException, IOException
/*      */     {
/* 2561 */       String str1 = toBundleName(paramString1, paramLocale);
/* 2562 */       Object localObject1 = null;
/* 2563 */       if (paramString2.equals("java.class")) {
/*      */         try {
/* 2565 */           Class localClass = paramClassLoader.loadClass(str1);
/*      */ 
/* 2570 */           if (ResourceBundle.class.isAssignableFrom(localClass))
/* 2571 */             localObject1 = (ResourceBundle)localClass.newInstance();
/*      */           else
/* 2573 */             throw new ClassCastException(localClass.getName() + " cannot be cast to ResourceBundle");
/*      */         }
/*      */         catch (ClassNotFoundException localClassNotFoundException) {
/*      */         }
/*      */       }
/* 2578 */       else if (paramString2.equals("java.properties")) {
/* 2579 */         final String str2 = toResourceName(str1, "properties");
/* 2580 */         final ClassLoader localClassLoader = paramClassLoader;
/* 2581 */         final boolean bool = paramBoolean;
/* 2582 */         InputStream localInputStream = null;
/*      */         try {
/* 2584 */           localInputStream = (InputStream)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*      */           {
/*      */             public InputStream run() throws IOException {
/* 2587 */               InputStream localInputStream = null;
/* 2588 */               if (bool) {
/* 2589 */                 URL localURL = localClassLoader.getResource(str2);
/* 2590 */                 if (localURL != null) {
/* 2591 */                   URLConnection localURLConnection = localURL.openConnection();
/* 2592 */                   if (localURLConnection != null)
/*      */                   {
/* 2595 */                     localURLConnection.setUseCaches(false);
/* 2596 */                     localInputStream = localURLConnection.getInputStream();
/*      */                   }
/*      */                 }
/*      */               } else {
/* 2600 */                 localInputStream = localClassLoader.getResourceAsStream(str2);
/*      */               }
/* 2602 */               return localInputStream;
/*      */             } } );
/*      */         }
/*      */         catch (PrivilegedActionException localPrivilegedActionException) {
/* 2606 */           throw ((IOException)localPrivilegedActionException.getException());
/*      */         }
/* 2608 */         if (localInputStream != null)
/*      */           try {
/* 2610 */             localObject1 = new PropertyResourceBundle(localInputStream);
/*      */           } finally {
/* 2612 */             localInputStream.close();
/*      */           }
/*      */       }
/*      */       else {
/* 2616 */         throw new IllegalArgumentException("unknown format: " + paramString2);
/*      */       }
/* 2618 */       return localObject1;
/*      */     }
/*      */ 
/*      */     public long getTimeToLive(String paramString, Locale paramLocale)
/*      */     {
/* 2668 */       if ((paramString == null) || (paramLocale == null)) {
/* 2669 */         throw new NullPointerException();
/*      */       }
/* 2671 */       return -2L;
/*      */     }
/*      */ 
/*      */     public boolean needsReload(String paramString1, Locale paramLocale, String paramString2, ClassLoader paramClassLoader, ResourceBundle paramResourceBundle, long paramLong)
/*      */     {
/* 2725 */       if (paramResourceBundle == null) {
/* 2726 */         throw new NullPointerException();
/*      */       }
/* 2728 */       if ((paramString2.equals("java.class")) || (paramString2.equals("java.properties"))) {
/* 2729 */         paramString2 = paramString2.substring(5);
/*      */       }
/* 2731 */       boolean bool = false;
/*      */       try {
/* 2733 */         String str = toResourceName(toBundleName(paramString1, paramLocale), paramString2);
/* 2734 */         URL localURL = paramClassLoader.getResource(str);
/* 2735 */         if (localURL != null) {
/* 2736 */           long l = 0L;
/* 2737 */           URLConnection localURLConnection = localURL.openConnection();
/* 2738 */           if (localURLConnection != null)
/*      */           {
/* 2740 */             localURLConnection.setUseCaches(false);
/* 2741 */             if ((localURLConnection instanceof JarURLConnection)) {
/* 2742 */               JarEntry localJarEntry = ((JarURLConnection)localURLConnection).getJarEntry();
/* 2743 */               if (localJarEntry != null) {
/* 2744 */                 l = localJarEntry.getTime();
/* 2745 */                 if (l == -1L)
/* 2746 */                   l = 0L;
/*      */               }
/*      */             }
/*      */             else {
/* 2750 */               l = localURLConnection.getLastModified();
/*      */             }
/*      */           }
/* 2753 */           bool = l >= paramLong;
/*      */         }
/*      */       } catch (NullPointerException localNullPointerException) {
/* 2756 */         throw localNullPointerException;
/*      */       }
/*      */       catch (Exception localException) {
/*      */       }
/* 2760 */       return bool;
/*      */     }
/*      */ 
/*      */     public String toBundleName(String paramString, Locale paramLocale)
/*      */     {
/* 2806 */       if (paramLocale == Locale.ROOT) {
/* 2807 */         return paramString;
/*      */       }
/*      */ 
/* 2810 */       String str1 = paramLocale.getLanguage();
/* 2811 */       String str2 = paramLocale.getScript();
/* 2812 */       String str3 = paramLocale.getCountry();
/* 2813 */       String str4 = paramLocale.getVariant();
/*      */ 
/* 2815 */       if ((str1 == "") && (str3 == "") && (str4 == "")) {
/* 2816 */         return paramString;
/*      */       }
/*      */ 
/* 2819 */       StringBuilder localStringBuilder = new StringBuilder(paramString);
/* 2820 */       localStringBuilder.append('_');
/* 2821 */       if (str2 != "") {
/* 2822 */         if (str4 != "")
/* 2823 */           localStringBuilder.append(str1).append('_').append(str2).append('_').append(str3).append('_').append(str4);
/* 2824 */         else if (str3 != "")
/* 2825 */           localStringBuilder.append(str1).append('_').append(str2).append('_').append(str3);
/*      */         else {
/* 2827 */           localStringBuilder.append(str1).append('_').append(str2);
/*      */         }
/*      */       }
/* 2830 */       else if (str4 != "")
/* 2831 */         localStringBuilder.append(str1).append('_').append(str3).append('_').append(str4);
/* 2832 */       else if (str3 != "")
/* 2833 */         localStringBuilder.append(str1).append('_').append(str3);
/*      */       else {
/* 2835 */         localStringBuilder.append(str1);
/*      */       }
/*      */ 
/* 2838 */       return localStringBuilder.toString();
/*      */     }
/*      */ 
/*      */     public final String toResourceName(String paramString1, String paramString2)
/*      */     {
/* 2863 */       StringBuilder localStringBuilder = new StringBuilder(paramString1.length() + 1 + paramString2.length());
/* 2864 */       localStringBuilder.append(paramString1.replace('.', '/')).append('.').append(paramString2);
/* 2865 */       return localStringBuilder.toString();
/*      */     }
/*      */ 
/*      */     private static class CandidateListCache extends LocaleObjectCache<BaseLocale, List<Locale>>
/*      */     {
/*      */       protected List<Locale> createObject(BaseLocale paramBaseLocale)
/*      */       {
/* 2310 */         String str1 = paramBaseLocale.getLanguage();
/* 2311 */         String str2 = paramBaseLocale.getScript();
/* 2312 */         String str3 = paramBaseLocale.getRegion();
/* 2313 */         String str4 = paramBaseLocale.getVariant();
/*      */ 
/* 2316 */         int i = 0;
/* 2317 */         int j = 0;
/* 2318 */         if (str1.equals("no"))
/* 2319 */           if ((str3.equals("NO")) && (str4.equals("NY"))) {
/* 2320 */             str4 = "";
/* 2321 */             j = 1;
/*      */           } else {
/* 2323 */             i = 1;
/*      */           }
/*      */         List localList;
/* 2326 */         if ((str1.equals("nb")) || (i != 0)) {
/* 2327 */           localList = getDefaultList("nb", str2, str3, str4);
/*      */ 
/* 2329 */           LinkedList localLinkedList = new LinkedList();
/* 2330 */           for (Locale localLocale : localList) {
/* 2331 */             localLinkedList.add(localLocale);
/* 2332 */             if (localLocale.getLanguage().length() == 0) {
/*      */               break;
/*      */             }
/* 2335 */             localLinkedList.add(Locale.getInstance("no", localLocale.getScript(), localLocale.getCountry(), localLocale.getVariant(), null));
/*      */           }
/*      */ 
/* 2338 */           return localLinkedList;
/* 2339 */         }if ((str1.equals("nn")) || (j != 0))
/*      */         {
/* 2341 */           localList = getDefaultList("nn", str2, str3, str4);
/* 2342 */           int k = localList.size() - 1;
/* 2343 */           localList.add(k++, Locale.getInstance("no", "NO", "NY"));
/* 2344 */           localList.add(k++, Locale.getInstance("no", "NO", ""));
/* 2345 */           localList.add(k++, Locale.getInstance("no", "", ""));
/* 2346 */           return localList;
/*      */         }
/*      */ 
/* 2349 */         if (str1.equals("zh")) {
/* 2350 */           if ((str2.length() == 0) && (str3.length() > 0))
/*      */           {
/* 2353 */             if ((str3.equals("TW")) || (str3.equals("HK")) || (str3.equals("MO")))
/* 2354 */               str2 = "Hant";
/* 2355 */             else if ((str3.equals("CN")) || (str3.equals("SG")))
/* 2356 */               str2 = "Hans";
/*      */           }
/* 2358 */           else if ((str2.length() > 0) && (str3.length() == 0))
/*      */           {
/* 2361 */             if (str2.equals("Hans"))
/* 2362 */               str3 = "CN";
/* 2363 */             else if (str2.equals("Hant")) {
/* 2364 */               str3 = "TW";
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/* 2369 */         return getDefaultList(str1, str2, str3, str4);
/*      */       }
/*      */ 
/*      */       private static List<Locale> getDefaultList(String paramString1, String paramString2, String paramString3, String paramString4) {
/* 2373 */         LinkedList localLinkedList1 = null;
/*      */ 
/* 2375 */         if (paramString4.length() > 0) {
/* 2376 */           localLinkedList1 = new LinkedList();
/* 2377 */           int i = paramString4.length();
/* 2378 */           while (i != -1) {
/* 2379 */             localLinkedList1.add(paramString4.substring(0, i));
/* 2380 */             i = paramString4.lastIndexOf('_', --i);
/*      */           }
/*      */         }
/*      */ 
/* 2384 */         LinkedList localLinkedList2 = new LinkedList();
/*      */         Iterator localIterator;
/* 2386 */         if (localLinkedList1 != null)
/* 2387 */           for (localIterator = localLinkedList1.iterator(); localIterator.hasNext(); ) { str = (String)localIterator.next();
/* 2388 */             localLinkedList2.add(Locale.getInstance(paramString1, paramString2, paramString3, str, null));
/*      */           }
/*      */         String str;
/* 2391 */         if (paramString3.length() > 0) {
/* 2392 */           localLinkedList2.add(Locale.getInstance(paramString1, paramString2, paramString3, "", null));
/*      */         }
/* 2394 */         if (paramString2.length() > 0) {
/* 2395 */           localLinkedList2.add(Locale.getInstance(paramString1, paramString2, "", "", null));
/*      */ 
/* 2399 */           if (localLinkedList1 != null) {
/* 2400 */             for (localIterator = localLinkedList1.iterator(); localIterator.hasNext(); ) { str = (String)localIterator.next();
/* 2401 */               localLinkedList2.add(Locale.getInstance(paramString1, "", paramString3, str, null));
/*      */             }
/*      */           }
/* 2404 */           if (paramString3.length() > 0) {
/* 2405 */             localLinkedList2.add(Locale.getInstance(paramString1, "", paramString3, "", null));
/*      */           }
/*      */         }
/* 2408 */         if (paramString1.length() > 0) {
/* 2409 */           localLinkedList2.add(Locale.getInstance(paramString1, "", "", "", null));
/*      */         }
/*      */ 
/* 2412 */         localLinkedList2.add(Locale.ROOT);
/*      */ 
/* 2414 */         return localLinkedList2;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static final class LoaderReference extends WeakReference<ClassLoader>
/*      */     implements ResourceBundle.CacheKeyReference
/*      */   {
/*      */     private ResourceBundle.CacheKey cacheKey;
/*      */ 
/*      */     LoaderReference(ClassLoader paramClassLoader, ReferenceQueue paramReferenceQueue, ResourceBundle.CacheKey paramCacheKey)
/*      */     {
/*  677 */       super(paramReferenceQueue);
/*  678 */       this.cacheKey = paramCacheKey;
/*      */     }
/*      */ 
/*      */     public ResourceBundle.CacheKey getCacheKey() {
/*  682 */       return this.cacheKey;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static final class NoFallbackControl extends ResourceBundle.SingleFormatControl
/*      */   {
/* 2891 */     private static final ResourceBundle.Control NO_FALLBACK = new NoFallbackControl(FORMAT_DEFAULT);
/*      */ 
/* 2894 */     private static final ResourceBundle.Control PROPERTIES_ONLY_NO_FALLBACK = new NoFallbackControl(FORMAT_PROPERTIES);
/*      */ 
/* 2897 */     private static final ResourceBundle.Control CLASS_ONLY_NO_FALLBACK = new NoFallbackControl(FORMAT_CLASS);
/*      */ 
/*      */     protected NoFallbackControl(List<String> paramList)
/*      */     {
/* 2901 */       super();
/*      */     }
/*      */ 
/*      */     public Locale getFallbackLocale(String paramString, Locale paramLocale) {
/* 2905 */       if ((paramString == null) || (paramLocale == null)) {
/* 2906 */         throw new NullPointerException();
/*      */       }
/* 2908 */       return null;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class RBClassLoader extends ClassLoader
/*      */   {
/*  442 */     private static final RBClassLoader INSTANCE = (RBClassLoader)AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public ResourceBundle.RBClassLoader run() {
/*  445 */         return new ResourceBundle.RBClassLoader(null);
/*      */       }
/*      */     });
/*      */ 
/*  448 */     private static final ClassLoader loader = ClassLoader.getSystemClassLoader();
/*      */ 
/*      */     public Class<?> loadClass(String paramString)
/*      */       throws ClassNotFoundException
/*      */     {
/*  453 */       if (loader != null) {
/*  454 */         return loader.loadClass(paramString);
/*      */       }
/*  456 */       return Class.forName(paramString);
/*      */     }
/*      */     public URL getResource(String paramString) {
/*  459 */       if (loader != null) {
/*  460 */         return loader.getResource(paramString);
/*      */       }
/*  462 */       return ClassLoader.getSystemResource(paramString);
/*      */     }
/*      */     public InputStream getResourceAsStream(String paramString) {
/*  465 */       if (loader != null) {
/*  466 */         return loader.getResourceAsStream(paramString);
/*      */       }
/*  468 */       return ClassLoader.getSystemResourceAsStream(paramString);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class SingleFormatControl extends ResourceBundle.Control
/*      */   {
/* 2870 */     private static final ResourceBundle.Control PROPERTIES_ONLY = new SingleFormatControl(FORMAT_PROPERTIES);
/*      */ 
/* 2873 */     private static final ResourceBundle.Control CLASS_ONLY = new SingleFormatControl(FORMAT_CLASS);
/*      */     private final List<String> formats;
/*      */ 
/*      */     protected SingleFormatControl(List<String> paramList)
/*      */     {
/* 2879 */       this.formats = paramList;
/*      */     }
/*      */ 
/*      */     public List<String> getFormats(String paramString) {
/* 2883 */       if (paramString == null) {
/* 2884 */         throw new NullPointerException();
/*      */       }
/* 2886 */       return this.formats;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.util.ResourceBundle
 * JD-Core Version:    0.6.2
 */