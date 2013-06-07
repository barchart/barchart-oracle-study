/*      */ package java.net;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.ObjectInputStream.GetField;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.ObjectOutputStream.PutField;
/*      */ import java.io.ObjectStreamException;
/*      */ import java.io.ObjectStreamField;
/*      */ import java.io.PrintStream;
/*      */ import java.io.Serializable;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedActionException;
/*      */ import java.security.PrivilegedExceptionAction;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Set;
/*      */ import sun.misc.Service;
/*      */ import sun.misc.Unsafe;
/*      */ import sun.net.InetAddressCachePolicy;
/*      */ import sun.net.spi.nameservice.NameService;
/*      */ import sun.net.spi.nameservice.NameServiceDescriptor;
/*      */ import sun.net.util.IPAddressUtil;
/*      */ import sun.security.action.GetBooleanAction;
/*      */ import sun.security.action.GetPropertyAction;
/*      */ import sun.security.action.LoadLibraryAction;
/*      */ 
/*      */ public class InetAddress
/*      */   implements Serializable
/*      */ {
/*      */   static final int IPv4 = 1;
/*      */   static final int IPv6 = 2;
/*      */   static transient boolean preferIPv6Address;
/*      */   final transient InetAddressHolder holder;
/*      */   private static List<NameService> nameServices;
/*  252 */   private transient String canonicalHostName = null;
/*      */   private static final long serialVersionUID = 3286316764910316507L;
/*      */   private static Cache addressCache;
/*      */   private static Cache negativeCache;
/*      */   private static boolean addressCacheInit;
/*      */   static InetAddress[] unknown_array;
/*      */   static InetAddressImpl impl;
/*      */   private static final HashMap<String, Void> lookupTable;
/*      */   private static InetAddress cachedLocalHost;
/*      */   private static long cacheTime;
/*      */   private static final long maxCacheTime = 5000L;
/*      */   private static final Object cacheLock;
/*      */   private static final long FIELDS_OFFSET;
/*      */   private static final Unsafe UNSAFE;
/* 1581 */   private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("hostName", String.class), new ObjectStreamField("address", Integer.TYPE), new ObjectStreamField("family", Integer.TYPE) };
/*      */ 
/*      */   InetAddressHolder holder()
/*      */   {
/*  245 */     return this.holder;
/*      */   }
/*      */ 
/*      */   InetAddress()
/*      */   {
/*  274 */     this.holder = new InetAddressHolder();
/*      */   }
/*      */ 
/*      */   private Object readResolve()
/*      */     throws ObjectStreamException
/*      */   {
/*  287 */     return new Inet4Address(holder().getHostName(), holder().getAddress());
/*      */   }
/*      */ 
/*      */   public boolean isMulticastAddress()
/*      */   {
/*  298 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isAnyLocalAddress()
/*      */   {
/*  308 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isLoopbackAddress()
/*      */   {
/*  319 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isLinkLocalAddress()
/*      */   {
/*  330 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isSiteLocalAddress()
/*      */   {
/*  341 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isMCGlobal()
/*      */   {
/*  353 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isMCNodeLocal()
/*      */   {
/*  365 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isMCLinkLocal()
/*      */   {
/*  377 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isMCSiteLocal()
/*      */   {
/*  389 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isMCOrgLocal()
/*      */   {
/*  402 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isReachable(int paramInt)
/*      */     throws IOException
/*      */   {
/*  427 */     return isReachable(null, 0, paramInt);
/*      */   }
/*      */ 
/*      */   public boolean isReachable(NetworkInterface paramNetworkInterface, int paramInt1, int paramInt2)
/*      */     throws IOException
/*      */   {
/*  463 */     if (paramInt1 < 0)
/*  464 */       throw new IllegalArgumentException("ttl can't be negative");
/*  465 */     if (paramInt2 < 0) {
/*  466 */       throw new IllegalArgumentException("timeout can't be negative");
/*      */     }
/*  468 */     return impl.isReachable(this, paramInt2, paramNetworkInterface, paramInt1);
/*      */   }
/*      */ 
/*      */   public String getHostName()
/*      */   {
/*  497 */     return getHostName(true);
/*      */   }
/*      */ 
/*      */   String getHostName(boolean paramBoolean)
/*      */   {
/*  524 */     if (holder().getHostName() == null) {
/*  525 */       holder().hostName = getHostFromNameService(this, paramBoolean);
/*      */     }
/*  527 */     return holder().getHostName();
/*      */   }
/*      */ 
/*      */   public String getCanonicalHostName()
/*      */   {
/*  552 */     if (this.canonicalHostName == null) {
/*  553 */       this.canonicalHostName = getHostFromNameService(this, true);
/*      */     }
/*      */ 
/*  556 */     return this.canonicalHostName;
/*      */   }
/*      */ 
/*      */   private static String getHostFromNameService(InetAddress paramInetAddress, boolean paramBoolean)
/*      */   {
/*  579 */     String str = null;
/*  580 */     for (NameService localNameService : nameServices) {
/*      */       try
/*      */       {
/*  583 */         str = localNameService.getHostByAddr(paramInetAddress.getAddress());
/*      */ 
/*  588 */         if (paramBoolean) {
/*  589 */           localObject = System.getSecurityManager();
/*  590 */           if (localObject != null) {
/*  591 */             ((SecurityManager)localObject).checkConnect(str, -1);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  600 */         Object localObject = getAllByName0(str, paramBoolean);
/*  601 */         boolean bool = false;
/*      */ 
/*  603 */         if (localObject != null) {
/*  604 */           for (int i = 0; (!bool) && (i < localObject.length); i++) {
/*  605 */             bool = paramInetAddress.equals(localObject[i]);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  610 */         if (!bool) {
/*  611 */           return paramInetAddress.getHostAddress();
/*      */         }
/*      */ 
/*      */       }
/*      */       catch (SecurityException localSecurityException)
/*      */       {
/*  618 */         str = paramInetAddress.getHostAddress();
/*      */       }
/*      */       catch (UnknownHostException localUnknownHostException) {
/*  621 */         str = paramInetAddress.getHostAddress();
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  626 */     return str;
/*      */   }
/*      */ 
/*      */   public byte[] getAddress()
/*      */   {
/*  637 */     return null;
/*      */   }
/*      */ 
/*      */   public String getHostAddress()
/*      */   {
/*  647 */     return null;
/*      */   }
/*      */ 
/*      */   public int hashCode()
/*      */   {
/*  656 */     return -1;
/*      */   }
/*      */ 
/*      */   public boolean equals(Object paramObject)
/*      */   {
/*  676 */     return false;
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/*  690 */     String str = holder().getHostName();
/*  691 */     return (str != null ? str : "") + "/" + getHostAddress();
/*      */   }
/*      */ 
/*      */   private static void cacheInitIfNeeded()
/*      */   {
/*  828 */     assert (Thread.holdsLock(addressCache));
/*  829 */     if (addressCacheInit) {
/*  830 */       return;
/*      */     }
/*  832 */     unknown_array = new InetAddress[1];
/*  833 */     unknown_array[0] = impl.anyLocalAddress();
/*      */ 
/*  835 */     addressCache.put(impl.anyLocalAddress().getHostName(), unknown_array);
/*      */ 
/*  838 */     addressCacheInit = true;
/*      */   }
/*      */ 
/*      */   private static void cacheAddresses(String paramString, InetAddress[] paramArrayOfInetAddress, boolean paramBoolean)
/*      */   {
/*  847 */     paramString = paramString.toLowerCase();
/*  848 */     synchronized (addressCache) {
/*  849 */       cacheInitIfNeeded();
/*  850 */       if (paramBoolean)
/*  851 */         addressCache.put(paramString, paramArrayOfInetAddress);
/*      */       else
/*  853 */         negativeCache.put(paramString, paramArrayOfInetAddress);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static InetAddress[] getCachedAddresses(String paramString)
/*      */   {
/*  863 */     paramString = paramString.toLowerCase();
/*      */ 
/*  867 */     synchronized (addressCache) {
/*  868 */       cacheInitIfNeeded();
/*      */ 
/*  870 */       CacheEntry localCacheEntry = addressCache.get(paramString);
/*  871 */       if (localCacheEntry == null) {
/*  872 */         localCacheEntry = negativeCache.get(paramString);
/*      */       }
/*      */ 
/*  875 */       if (localCacheEntry != null) {
/*  876 */         return localCacheEntry.addresses;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  881 */     return null;
/*      */   }
/*      */ 
/*      */   private static NameService createNSProvider(String paramString) {
/*  885 */     if (paramString == null) {
/*  886 */       return null;
/*      */     }
/*  888 */     Object localObject = null;
/*  889 */     if (paramString.equals("default"))
/*      */     {
/*  891 */       localObject = new NameService()
/*      */       {
/*      */         public InetAddress[] lookupAllHostAddr(String paramAnonymousString) throws UnknownHostException {
/*  894 */           return InetAddress.impl.lookupAllHostAddr(paramAnonymousString);
/*      */         }
/*      */ 
/*      */         public String getHostByAddr(byte[] paramAnonymousArrayOfByte) throws UnknownHostException {
/*  898 */           return InetAddress.impl.getHostByAddr(paramAnonymousArrayOfByte);
/*      */         } } ;
/*      */     }
/*      */     else {
/*  902 */       String str = paramString;
/*      */       try {
/*  904 */         localObject = (NameService)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*      */         {
/*      */           public NameService run() {
/*  907 */             Iterator localIterator = Service.providers(NameServiceDescriptor.class);
/*  908 */             while (localIterator.hasNext()) {
/*  909 */               NameServiceDescriptor localNameServiceDescriptor = (NameServiceDescriptor)localIterator.next();
/*      */ 
/*  911 */               if (this.val$providerName.equalsIgnoreCase(localNameServiceDescriptor.getType() + "," + localNameServiceDescriptor.getProviderName()))
/*      */               {
/*      */                 try
/*      */                 {
/*  915 */                   return localNameServiceDescriptor.createNameService();
/*      */                 } catch (Exception localException) {
/*  917 */                   localException.printStackTrace();
/*  918 */                   System.err.println("Cannot create name service:" + this.val$providerName + ": " + localException);
/*      */                 }
/*      */ 
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/*  925 */             return null;
/*      */           }
/*      */         });
/*      */       }
/*      */       catch (PrivilegedActionException localPrivilegedActionException)
/*      */       {
/*      */       }
/*      */     }
/*  933 */     return localObject;
/*      */   }
/*      */ 
/*      */   public static InetAddress getByAddress(String paramString, byte[] paramArrayOfByte)
/*      */     throws UnknownHostException
/*      */   {
/*  989 */     if ((paramString != null) && (paramString.length() > 0) && (paramString.charAt(0) == '[') && 
/*  990 */       (paramString.charAt(paramString.length() - 1) == ']')) {
/*  991 */       paramString = paramString.substring(1, paramString.length() - 1);
/*      */     }
/*      */ 
/*  994 */     if (paramArrayOfByte != null) {
/*  995 */       if (paramArrayOfByte.length == 4)
/*  996 */         return new Inet4Address(paramString, paramArrayOfByte);
/*  997 */       if (paramArrayOfByte.length == 16) {
/*  998 */         byte[] arrayOfByte = IPAddressUtil.convertFromIPv4MappedAddress(paramArrayOfByte);
/*      */ 
/* 1000 */         if (arrayOfByte != null) {
/* 1001 */           return new Inet4Address(paramString, arrayOfByte);
/*      */         }
/* 1003 */         return new Inet6Address(paramString, paramArrayOfByte);
/*      */       }
/*      */     }
/*      */ 
/* 1007 */     throw new UnknownHostException("addr is of illegal length");
/*      */   }
/*      */ 
/*      */   public static InetAddress getByName(String paramString)
/*      */     throws UnknownHostException
/*      */   {
/* 1041 */     return getAllByName(paramString)[0];
/*      */   }
/*      */ 
/*      */   private static InetAddress getByName(String paramString, InetAddress paramInetAddress)
/*      */     throws UnknownHostException
/*      */   {
/* 1047 */     return getAllByName(paramString, paramInetAddress)[0];
/*      */   }
/*      */ 
/*      */   public static InetAddress[] getAllByName(String paramString)
/*      */     throws UnknownHostException
/*      */   {
/* 1091 */     return getAllByName(paramString, null);
/*      */   }
/*      */ 
/*      */   private static InetAddress[] getAllByName(String paramString, InetAddress paramInetAddress)
/*      */     throws UnknownHostException
/*      */   {
/* 1097 */     if ((paramString == null) || (paramString.length() == 0)) {
/* 1098 */       InetAddress[] arrayOfInetAddress1 = new InetAddress[1];
/* 1099 */       arrayOfInetAddress1[0] = impl.loopbackAddress();
/* 1100 */       return arrayOfInetAddress1;
/*      */     }
/*      */ 
/* 1103 */     int i = 0;
/* 1104 */     if (paramString.charAt(0) == '[')
/*      */     {
/* 1106 */       if ((paramString.length() > 2) && (paramString.charAt(paramString.length() - 1) == ']')) {
/* 1107 */         paramString = paramString.substring(1, paramString.length() - 1);
/* 1108 */         i = 1;
/*      */       }
/*      */       else {
/* 1111 */         throw new UnknownHostException(paramString + ": invalid IPv6 address");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1116 */     if ((Character.digit(paramString.charAt(0), 16) != -1) || (paramString.charAt(0) == ':'))
/*      */     {
/* 1118 */       byte[] arrayOfByte = null;
/* 1119 */       int j = -1;
/* 1120 */       String str = null;
/*      */ 
/* 1122 */       arrayOfByte = IPAddressUtil.textToNumericFormatV4(paramString);
/* 1123 */       if (arrayOfByte == null)
/*      */       {
/*      */         int k;
/* 1127 */         if ((k = paramString.indexOf("%")) != -1) {
/* 1128 */           j = checkNumericZone(paramString);
/* 1129 */           if (j == -1) {
/* 1130 */             str = paramString.substring(k + 1);
/*      */           }
/*      */         }
/* 1133 */         arrayOfByte = IPAddressUtil.textToNumericFormatV6(paramString);
/* 1134 */       } else if (i != 0)
/*      */       {
/* 1136 */         throw new UnknownHostException("[" + paramString + "]");
/*      */       }
/* 1138 */       InetAddress[] arrayOfInetAddress2 = new InetAddress[1];
/* 1139 */       if (arrayOfByte != null) {
/* 1140 */         if (arrayOfByte.length == 4) {
/* 1141 */           arrayOfInetAddress2[0] = new Inet4Address(null, arrayOfByte);
/*      */         }
/* 1143 */         else if (str != null)
/* 1144 */           arrayOfInetAddress2[0] = new Inet6Address(null, arrayOfByte, str);
/*      */         else {
/* 1146 */           arrayOfInetAddress2[0] = new Inet6Address(null, arrayOfByte, j);
/*      */         }
/*      */ 
/* 1149 */         return arrayOfInetAddress2;
/*      */       }
/* 1151 */     } else if (i != 0)
/*      */     {
/* 1153 */       throw new UnknownHostException("[" + paramString + "]");
/*      */     }
/* 1155 */     return getAllByName0(paramString, paramInetAddress, true);
/*      */   }
/*      */ 
/*      */   public static InetAddress getLoopbackAddress()
/*      */   {
/* 1170 */     return impl.loopbackAddress();
/*      */   }
/*      */ 
/*      */   private static int checkNumericZone(String paramString)
/*      */     throws UnknownHostException
/*      */   {
/* 1182 */     int i = paramString.indexOf('%');
/* 1183 */     int j = paramString.length();
/* 1184 */     int m = 0;
/* 1185 */     if (i == -1) {
/* 1186 */       return -1;
/*      */     }
/* 1188 */     for (int n = i + 1; n < j; n++) {
/* 1189 */       char c = paramString.charAt(n);
/* 1190 */       if (c == ']') {
/* 1191 */         if (n != i + 1)
/*      */           break;
/* 1193 */         return -1;
/*      */       }
/*      */       int k;
/* 1197 */       if ((k = Character.digit(c, 10)) < 0) {
/* 1198 */         return -1;
/*      */       }
/* 1200 */       m = m * 10 + k;
/*      */     }
/* 1202 */     return m;
/*      */   }
/*      */ 
/*      */   private static InetAddress[] getAllByName0(String paramString)
/*      */     throws UnknownHostException
/*      */   {
/* 1208 */     return getAllByName0(paramString, true);
/*      */   }
/*      */ 
/*      */   static InetAddress[] getAllByName0(String paramString, boolean paramBoolean)
/*      */     throws UnknownHostException
/*      */   {
/* 1216 */     return getAllByName0(paramString, null, paramBoolean);
/*      */   }
/*      */ 
/*      */   private static InetAddress[] getAllByName0(String paramString, InetAddress paramInetAddress, boolean paramBoolean)
/*      */     throws UnknownHostException
/*      */   {
/* 1228 */     if (paramBoolean) {
/* 1229 */       localObject = System.getSecurityManager();
/* 1230 */       if (localObject != null) {
/* 1231 */         ((SecurityManager)localObject).checkConnect(paramString, -1);
/*      */       }
/*      */     }
/*      */ 
/* 1235 */     Object localObject = getCachedAddresses(paramString);
/*      */ 
/* 1238 */     if (localObject == null) {
/* 1239 */       localObject = getAddressesFromNameService(paramString, paramInetAddress);
/*      */     }
/*      */ 
/* 1242 */     if (localObject == unknown_array) {
/* 1243 */       throw new UnknownHostException(paramString);
/*      */     }
/* 1245 */     return (InetAddress[])((InetAddress[])localObject).clone();
/*      */   }
/*      */ 
/*      */   private static InetAddress[] getAddressesFromNameService(String paramString, InetAddress paramInetAddress)
/*      */     throws UnknownHostException
/*      */   {
/* 1251 */     Object localObject1 = null;
/* 1252 */     boolean bool = false;
/* 1253 */     Object localObject2 = null;
/*      */ 
/* 1273 */     if ((localObject1 = checkLookupTable(paramString)) == null)
/*      */     {
/*      */       try
/*      */       {
/* 1278 */         for (Iterator localIterator = nameServices.iterator(); localIterator.hasNext(); ) { localNameService = (NameService)localIterator.next();
/*      */           try
/*      */           {
/* 1286 */             localObject1 = localNameService.lookupAllHostAddr(paramString);
/* 1287 */             bool = true;
/*      */           }
/*      */           catch (UnknownHostException localUnknownHostException) {
/* 1290 */             if (paramString.equalsIgnoreCase("localhost")) {
/* 1291 */               InetAddress[] arrayOfInetAddress = { impl.loopbackAddress() };
/* 1292 */               localObject1 = arrayOfInetAddress;
/* 1293 */               bool = true;
/* 1294 */               break;
/*      */             }
/*      */ 
/* 1297 */             localObject1 = unknown_array;
/* 1298 */             bool = false;
/* 1299 */             localObject2 = localUnknownHostException;
/*      */           }
/*      */         }
/*      */         NameService localNameService;
/* 1305 */         if ((paramInetAddress != null) && (localObject1.length > 1) && (!localObject1[0].equals(paramInetAddress)))
/*      */         {
/* 1307 */           int i = 1;
/* 1308 */           while ((i < localObject1.length) && 
/* 1309 */             (!localObject1[i].equals(paramInetAddress))) {
/* 1308 */             i++;
/*      */           }
/*      */ 
/* 1314 */           if (i < localObject1.length) {
/* 1315 */             Object localObject3 = paramInetAddress;
/* 1316 */             for (int j = 0; j < i; j++) {
/* 1317 */               localNameService = localObject1[j];
/* 1318 */               localObject1[j] = localObject3;
/* 1319 */               localObject3 = localNameService;
/*      */             }
/* 1321 */             localObject1[i] = localObject3;
/*      */           }
/*      */         }
/*      */ 
/* 1325 */         cacheAddresses(paramString, (InetAddress[])localObject1, bool);
/*      */ 
/* 1327 */         if ((!bool) && (localObject2 != null)) {
/* 1328 */           throw localObject2;
/*      */         }
/*      */       }
/*      */       finally
/*      */       {
/* 1333 */         updateLookupTable(paramString);
/*      */       }
/*      */     }
/*      */ 
/* 1337 */     return localObject1;
/*      */   }
/*      */ 
/*      */   private static InetAddress[] checkLookupTable(String paramString)
/*      */   {
/* 1342 */     synchronized (lookupTable)
/*      */     {
/* 1346 */       if (!lookupTable.containsKey(paramString)) {
/* 1347 */         lookupTable.put(paramString, null);
/* 1348 */         return null;
/*      */       }
/*      */ 
/* 1354 */       while (lookupTable.containsKey(paramString)) {
/*      */         try {
/* 1356 */           lookupTable.wait();
/*      */         }
/*      */         catch (InterruptedException localInterruptedException)
/*      */         {
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1366 */     ??? = getCachedAddresses(paramString);
/* 1367 */     if (??? == null) {
/* 1368 */       synchronized (lookupTable) {
/* 1369 */         lookupTable.put(paramString, null);
/* 1370 */         return null;
/*      */       }
/*      */     }
/*      */ 
/* 1374 */     return ???;
/*      */   }
/*      */ 
/*      */   private static void updateLookupTable(String paramString) {
/* 1378 */     synchronized (lookupTable) {
/* 1379 */       lookupTable.remove(paramString);
/* 1380 */       lookupTable.notifyAll();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static InetAddress getByAddress(byte[] paramArrayOfByte)
/*      */     throws UnknownHostException
/*      */   {
/* 1402 */     return getByAddress(null, paramArrayOfByte);
/*      */   }
/*      */ 
/*      */   public static InetAddress getLocalHost()
/*      */     throws UnknownHostException
/*      */   {
/* 1435 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*      */     try {
/* 1437 */       String str = impl.getLocalHostName();
/*      */ 
/* 1439 */       if (localSecurityManager != null) {
/* 1440 */         localSecurityManager.checkConnect(str, -1);
/*      */       }
/*      */ 
/* 1443 */       if (str.equals("localhost")) {
/* 1444 */         return impl.loopbackAddress();
/*      */       }
/*      */ 
/* 1447 */       InetAddress localInetAddress = null;
/* 1448 */       synchronized (cacheLock) {
/* 1449 */         long l = System.currentTimeMillis();
/* 1450 */         if (cachedLocalHost != null) {
/* 1451 */           if (l - cacheTime < 5000L)
/* 1452 */             localInetAddress = cachedLocalHost;
/*      */           else {
/* 1454 */             cachedLocalHost = null;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1459 */         if (localInetAddress == null) {
/*      */           InetAddress[] arrayOfInetAddress;
/*      */           try {
/* 1462 */             arrayOfInetAddress = getAddressesFromNameService(str, null);
/*      */           }
/*      */           catch (UnknownHostException localUnknownHostException1)
/*      */           {
/* 1466 */             UnknownHostException localUnknownHostException2 = new UnknownHostException(str + ": " + localUnknownHostException1.getMessage());
/*      */ 
/* 1469 */             localUnknownHostException2.initCause(localUnknownHostException1);
/* 1470 */             throw localUnknownHostException2;
/*      */           }
/* 1472 */           cachedLocalHost = arrayOfInetAddress[0];
/* 1473 */           cacheTime = l;
/* 1474 */           localInetAddress = arrayOfInetAddress[0];
/*      */         }
/*      */       }
/* 1477 */       return localInetAddress; } catch (SecurityException localSecurityException) {
/*      */     }
/* 1479 */     return impl.loopbackAddress();
/*      */   }
/*      */ 
/*      */   private static native void init();
/*      */ 
/*      */   static InetAddress anyLocalAddress()
/*      */   {
/* 1494 */     return impl.anyLocalAddress();
/*      */   }
/*      */ 
/*      */   static InetAddressImpl loadImpl(String paramString)
/*      */   {
/* 1501 */     Object localObject = null;
/*      */ 
/* 1510 */     String str = (String)AccessController.doPrivileged(new GetPropertyAction("impl.prefix", ""));
/*      */     try
/*      */     {
/* 1513 */       localObject = Class.forName("java.net." + str + paramString).newInstance();
/*      */     } catch (ClassNotFoundException localClassNotFoundException) {
/* 1515 */       System.err.println("Class not found: java.net." + str + paramString + ":\ncheck impl.prefix property " + "in your properties file.");
/*      */     }
/*      */     catch (InstantiationException localInstantiationException)
/*      */     {
/* 1519 */       System.err.println("Could not instantiate: java.net." + str + paramString + ":\ncheck impl.prefix property " + "in your properties file.");
/*      */     }
/*      */     catch (IllegalAccessException localIllegalAccessException)
/*      */     {
/* 1523 */       System.err.println("Cannot access class: java.net." + str + paramString + ":\ncheck impl.prefix property " + "in your properties file.");
/*      */     }
/*      */ 
/* 1528 */     if (localObject == null) {
/*      */       try {
/* 1530 */         localObject = Class.forName(paramString).newInstance();
/*      */       } catch (Exception localException) {
/* 1532 */         throw new Error("System property impl.prefix incorrect");
/*      */       }
/*      */     }
/*      */ 
/* 1536 */     return (InetAddressImpl)localObject;
/*      */   }
/*      */ 
/*      */   private void readObjectNoData(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException
/*      */   {
/* 1541 */     if (getClass().getClassLoader() != null)
/* 1542 */       throw new SecurityException("invalid address type");
/*      */   }
/*      */ 
/*      */   private void readObject(ObjectInputStream paramObjectInputStream)
/*      */     throws IOException, ClassNotFoundException
/*      */   {
/* 1563 */     if (getClass().getClassLoader() != null) {
/* 1564 */       throw new SecurityException("invalid address type");
/*      */     }
/* 1566 */     ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
/* 1567 */     String str = (String)localGetField.get("hostName", null);
/* 1568 */     int i = localGetField.get("address", 0);
/* 1569 */     int j = localGetField.get("family", 0);
/* 1570 */     InetAddressHolder localInetAddressHolder = new InetAddressHolder(str, i, j);
/* 1571 */     UNSAFE.putObject(this, FIELDS_OFFSET, localInetAddressHolder);
/*      */   }
/*      */ 
/*      */   private void writeObject(ObjectOutputStream paramObjectOutputStream)
/*      */     throws IOException
/*      */   {
/* 1589 */     if (getClass().getClassLoader() != null) {
/* 1590 */       throw new SecurityException("invalid address type");
/*      */     }
/* 1592 */     ObjectOutputStream.PutField localPutField = paramObjectOutputStream.putFields();
/* 1593 */     localPutField.put("hostName", holder().hostName);
/* 1594 */     localPutField.put("address", holder().address);
/* 1595 */     localPutField.put("family", holder().family);
/* 1596 */     paramObjectOutputStream.writeFields();
/* 1597 */     paramObjectOutputStream.flush();
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  204 */     preferIPv6Address = false;
/*      */ 
/*  249 */     nameServices = null;
/*      */ 
/*  261 */     preferIPv6Address = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("java.net.preferIPv6Addresses"))).booleanValue();
/*      */ 
/*  263 */     AccessController.doPrivileged(new LoadLibraryAction("net"));
/*  264 */     init();
/*      */ 
/*  698 */     addressCache = new Cache(InetAddress.Cache.Type.Positive);
/*      */ 
/*  700 */     negativeCache = new Cache(InetAddress.Cache.Type.Negative);
/*      */ 
/*  702 */     addressCacheInit = false;
/*      */ 
/*  708 */     lookupTable = new HashMap();
/*      */ 
/*  938 */     impl = InetAddressImplFactory.create();
/*      */ 
/*  941 */     Object localObject = null;
/*  942 */     String str = "sun.net.spi.nameservice.provider.";
/*  943 */     int i = 1;
/*  944 */     nameServices = new ArrayList();
/*  945 */     localObject = (String)AccessController.doPrivileged(new GetPropertyAction(str + i));
/*      */     NameService localNameService;
/*  947 */     while (localObject != null) {
/*  948 */       localNameService = createNSProvider((String)localObject);
/*  949 */       if (localNameService != null) {
/*  950 */         nameServices.add(localNameService);
/*      */       }
/*  952 */       i++;
/*  953 */       localObject = (String)AccessController.doPrivileged(new GetPropertyAction(str + i));
/*      */     }
/*      */ 
/*  959 */     if (nameServices.size() == 0) {
/*  960 */       localNameService = createNSProvider("default");
/*  961 */       nameServices.add(localNameService);
/*      */     }
/*      */ 
/* 1405 */     cachedLocalHost = null;
/* 1406 */     cacheTime = 0L;
/*      */ 
/* 1408 */     cacheLock = new Object();
/*      */     try
/*      */     {
/* 1551 */       localObject = Unsafe.getUnsafe();
/* 1552 */       FIELDS_OFFSET = ((Unsafe)localObject).objectFieldOffset(InetAddress.class.getDeclaredField("holder"));
/*      */ 
/* 1555 */       UNSAFE = (Unsafe)localObject;
/*      */     } catch (ReflectiveOperationException localReflectiveOperationException) {
/* 1557 */       throw new Error(localReflectiveOperationException);
/*      */     }
/*      */   }
/*      */ 
/*      */   static final class Cache
/*      */   {
/*      */     private LinkedHashMap<String, InetAddress.CacheEntry> cache;
/*      */     private Type type;
/*      */ 
/*      */     public Cache(Type paramType)
/*      */     {
/*  738 */       this.type = paramType;
/*  739 */       this.cache = new LinkedHashMap();
/*      */     }
/*      */ 
/*      */     private int getPolicy() {
/*  743 */       if (this.type == Type.Positive) {
/*  744 */         return InetAddressCachePolicy.get();
/*      */       }
/*  746 */       return InetAddressCachePolicy.getNegative();
/*      */     }
/*      */ 
/*      */     public Cache put(String paramString, InetAddress[] paramArrayOfInetAddress)
/*      */     {
/*  756 */       int i = getPolicy();
/*  757 */       if (i == 0)
/*  758 */         return this;
/*      */       Iterator localIterator;
/*      */       String str;
/*  763 */       if (i != -1)
/*      */       {
/*  767 */         LinkedList localLinkedList = new LinkedList();
/*  768 */         long l2 = System.currentTimeMillis();
/*  769 */         for (localIterator = this.cache.keySet().iterator(); localIterator.hasNext(); ) { str = (String)localIterator.next();
/*  770 */           InetAddress.CacheEntry localCacheEntry2 = (InetAddress.CacheEntry)this.cache.get(str);
/*      */ 
/*  772 */           if ((localCacheEntry2.expiration < 0L) || (localCacheEntry2.expiration >= l2)) break;
/*  773 */           localLinkedList.add(str);
/*      */         }
/*      */ 
/*  779 */         for (localIterator = localLinkedList.iterator(); localIterator.hasNext(); ) { str = (String)localIterator.next();
/*  780 */           this.cache.remove(str);
/*      */         }
/*      */       }
/*      */       long l1;
/*  789 */       if (i == -1)
/*  790 */         l1 = -1L;
/*      */       else {
/*  792 */         l1 = System.currentTimeMillis() + i * 1000;
/*      */       }
/*  794 */       InetAddress.CacheEntry localCacheEntry1 = new InetAddress.CacheEntry(paramArrayOfInetAddress, l1);
/*  795 */       this.cache.put(paramString, localCacheEntry1);
/*  796 */       return this;
/*      */     }
/*      */ 
/*      */     public InetAddress.CacheEntry get(String paramString)
/*      */     {
/*  804 */       int i = getPolicy();
/*  805 */       if (i == 0) {
/*  806 */         return null;
/*      */       }
/*  808 */       InetAddress.CacheEntry localCacheEntry = (InetAddress.CacheEntry)this.cache.get(paramString);
/*      */ 
/*  811 */       if ((localCacheEntry != null) && (i != -1) && 
/*  812 */         (localCacheEntry.expiration >= 0L) && (localCacheEntry.expiration < System.currentTimeMillis()))
/*      */       {
/*  814 */         this.cache.remove(paramString);
/*  815 */         localCacheEntry = null;
/*      */       }
/*      */ 
/*  819 */       return localCacheEntry;
/*      */     }
/*      */ 
/*      */     static enum Type
/*      */     {
/*  732 */       Positive, Negative;
/*      */     }
/*      */   }
/*      */ 
/*      */   static final class CacheEntry
/*      */   {
/*      */     InetAddress[] addresses;
/*      */     long expiration;
/*      */ 
/*      */     CacheEntry(InetAddress[] paramArrayOfInetAddress, long paramLong)
/*      */     {
/*  716 */       this.addresses = paramArrayOfInetAddress;
/*  717 */       this.expiration = paramLong;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class InetAddressHolder
/*      */   {
/*      */     String hostName;
/*      */     int address;
/*      */     int family;
/*      */ 
/*      */     InetAddressHolder()
/*      */     {
/*      */     }
/*      */ 
/*      */     InetAddressHolder(String paramString, int paramInt1, int paramInt2)
/*      */     {
/*  211 */       this.hostName = paramString;
/*  212 */       this.address = paramInt1;
/*  213 */       this.family = paramInt2;
/*      */     }
/*      */ 
/*      */     String getHostName()
/*      */     {
/*  219 */       return this.hostName;
/*      */     }
/*      */ 
/*      */     int getAddress()
/*      */     {
/*  228 */       return this.address;
/*      */     }
/*      */ 
/*      */     int getFamily()
/*      */     {
/*  238 */       return this.family;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.net.InetAddress
 * JD-Core Version:    0.6.2
 */