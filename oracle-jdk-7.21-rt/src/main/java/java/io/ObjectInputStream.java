/*      */ package java.io;
/*      */ 
/*      */ import java.lang.ref.ReferenceQueue;
/*      */ import java.lang.reflect.Array;
/*      */ import java.lang.reflect.Proxy;
/*      */ import java.security.AccessControlContext;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.security.PrivilegedActionException;
/*      */ import java.security.PrivilegedExceptionAction;
/*      */ import java.util.Arrays;
/*      */ import java.util.HashMap;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ import java.util.concurrent.ConcurrentMap;
/*      */ import sun.reflect.misc.ReflectUtil;
/*      */ 
/*      */ public class ObjectInputStream extends InputStream
/*      */   implements ObjectInput, ObjectStreamConstants
/*      */ {
/*      */   private static final int NULL_HANDLE = -1;
/*  213 */   private static final Object unsharedMarker = new Object();
/*      */ 
/*  216 */   private static final HashMap<String, Class<?>> primClasses = new HashMap(8, 1.0F);
/*      */   private final BlockDataInputStream bin;
/*      */   private final ValidationList vlist;
/*      */   private int depth;
/*      */   private boolean closed;
/*      */   private final HandleTable handles;
/*  252 */   private int passHandle = -1;
/*      */ 
/*  254 */   private boolean defaultDataEnd = false;
/*      */   private byte[] primVals;
/*      */   private final boolean enableOverride;
/*      */   private boolean enableResolve;
/*      */   private SerialCallbackContext curContext;
/*      */ 
/*      */   public ObjectInputStream(InputStream paramInputStream)
/*      */     throws IOException
/*      */   {
/*  294 */     verifySubclass();
/*  295 */     this.bin = new BlockDataInputStream(paramInputStream);
/*  296 */     this.handles = new HandleTable(10);
/*  297 */     this.vlist = new ValidationList();
/*  298 */     this.enableOverride = false;
/*  299 */     readStreamHeader();
/*  300 */     this.bin.setBlockDataMode(true);
/*      */   }
/*      */ 
/*      */   protected ObjectInputStream()
/*      */     throws IOException, SecurityException
/*      */   {
/*  320 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  321 */     if (localSecurityManager != null) {
/*  322 */       localSecurityManager.checkPermission(SUBCLASS_IMPLEMENTATION_PERMISSION);
/*      */     }
/*  324 */     this.bin = null;
/*  325 */     this.handles = null;
/*  326 */     this.vlist = null;
/*  327 */     this.enableOverride = true;
/*      */   }
/*      */ 
/*      */   public final Object readObject()
/*      */     throws IOException, ClassNotFoundException
/*      */   {
/*  363 */     if (this.enableOverride) {
/*  364 */       return readObjectOverride();
/*      */     }
/*      */ 
/*  368 */     int i = this.passHandle;
/*      */     try {
/*  370 */       Object localObject1 = readObject0(false);
/*  371 */       this.handles.markDependency(i, this.passHandle);
/*  372 */       ClassNotFoundException localClassNotFoundException = this.handles.lookupException(this.passHandle);
/*  373 */       if (localClassNotFoundException != null) {
/*  374 */         throw localClassNotFoundException;
/*      */       }
/*  376 */       if (this.depth == 0) {
/*  377 */         this.vlist.doCallbacks();
/*      */       }
/*  379 */       return localObject1;
/*      */     } finally {
/*  381 */       this.passHandle = i;
/*  382 */       if ((this.closed) && (this.depth == 0))
/*  383 */         clear();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected Object readObjectOverride()
/*      */     throws IOException, ClassNotFoundException
/*      */   {
/*  408 */     return null;
/*      */   }
/*      */ 
/*      */   public Object readUnshared()
/*      */     throws IOException, ClassNotFoundException
/*      */   {
/*  458 */     int i = this.passHandle;
/*      */     try {
/*  460 */       Object localObject1 = readObject0(true);
/*  461 */       this.handles.markDependency(i, this.passHandle);
/*  462 */       ClassNotFoundException localClassNotFoundException = this.handles.lookupException(this.passHandle);
/*  463 */       if (localClassNotFoundException != null) {
/*  464 */         throw localClassNotFoundException;
/*      */       }
/*  466 */       if (this.depth == 0) {
/*  467 */         this.vlist.doCallbacks();
/*      */       }
/*  469 */       return localObject1;
/*      */     } finally {
/*  471 */       this.passHandle = i;
/*  472 */       if ((this.closed) && (this.depth == 0))
/*  473 */         clear();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void defaultReadObject()
/*      */     throws IOException, ClassNotFoundException
/*      */   {
/*  493 */     if (this.curContext == null) {
/*  494 */       throw new NotActiveException("not in call to readObject");
/*      */     }
/*  496 */     Object localObject = this.curContext.getObj();
/*  497 */     ObjectStreamClass localObjectStreamClass = this.curContext.getDesc();
/*  498 */     this.bin.setBlockDataMode(false);
/*  499 */     defaultReadFields(localObject, localObjectStreamClass);
/*  500 */     this.bin.setBlockDataMode(true);
/*  501 */     if (!localObjectStreamClass.hasWriteObjectData())
/*      */     {
/*  507 */       this.defaultDataEnd = true;
/*      */     }
/*  509 */     ClassNotFoundException localClassNotFoundException = this.handles.lookupException(this.passHandle);
/*  510 */     if (localClassNotFoundException != null)
/*  511 */       throw localClassNotFoundException;
/*      */   }
/*      */ 
/*      */   public GetField readFields()
/*      */     throws IOException, ClassNotFoundException
/*      */   {
/*  531 */     if (this.curContext == null) {
/*  532 */       throw new NotActiveException("not in call to readObject");
/*      */     }
/*  534 */     Object localObject = this.curContext.getObj();
/*  535 */     ObjectStreamClass localObjectStreamClass = this.curContext.getDesc();
/*  536 */     this.bin.setBlockDataMode(false);
/*  537 */     GetFieldImpl localGetFieldImpl = new GetFieldImpl(localObjectStreamClass);
/*  538 */     localGetFieldImpl.readFields();
/*  539 */     this.bin.setBlockDataMode(true);
/*  540 */     if (!localObjectStreamClass.hasWriteObjectData())
/*      */     {
/*  546 */       this.defaultDataEnd = true;
/*      */     }
/*      */ 
/*  549 */     return localGetFieldImpl;
/*      */   }
/*      */ 
/*      */   public void registerValidation(ObjectInputValidation paramObjectInputValidation, int paramInt)
/*      */     throws NotActiveException, InvalidObjectException
/*      */   {
/*  571 */     if (this.depth == 0) {
/*  572 */       throw new NotActiveException("stream inactive");
/*      */     }
/*  574 */     this.vlist.register(paramObjectInputValidation, paramInt);
/*      */   }
/*      */ 
/*      */   protected Class<?> resolveClass(ObjectStreamClass paramObjectStreamClass)
/*      */     throws IOException, ClassNotFoundException
/*      */   {
/*  621 */     String str = paramObjectStreamClass.getName();
/*      */     try {
/*  623 */       return Class.forName(str, false, latestUserDefinedLoader());
/*      */     } catch (ClassNotFoundException localClassNotFoundException) {
/*  625 */       Class localClass = (Class)primClasses.get(str);
/*  626 */       if (localClass != null) {
/*  627 */         return localClass;
/*      */       }
/*  629 */       throw localClassNotFoundException;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected Class<?> resolveProxyClass(String[] paramArrayOfString)
/*      */     throws IOException, ClassNotFoundException
/*      */   {
/*  688 */     ClassLoader localClassLoader1 = latestUserDefinedLoader();
/*  689 */     ClassLoader localClassLoader2 = null;
/*  690 */     int i = 0;
/*      */ 
/*  693 */     Class[] arrayOfClass = new Class[paramArrayOfString.length];
/*  694 */     for (int j = 0; j < paramArrayOfString.length; j++) {
/*  695 */       Class localClass = Class.forName(paramArrayOfString[j], false, localClassLoader1);
/*  696 */       if ((localClass.getModifiers() & 0x1) == 0) {
/*  697 */         if (i != 0) {
/*  698 */           if (localClassLoader2 != localClass.getClassLoader())
/*  699 */             throw new IllegalAccessError("conflicting non-public interface class loaders");
/*      */         }
/*      */         else
/*      */         {
/*  703 */           localClassLoader2 = localClass.getClassLoader();
/*  704 */           i = 1;
/*      */         }
/*      */       }
/*  707 */       arrayOfClass[j] = localClass;
/*      */     }
/*      */     try {
/*  710 */       return Proxy.getProxyClass(i != 0 ? localClassLoader2 : localClassLoader1, arrayOfClass);
/*      */     }
/*      */     catch (IllegalArgumentException localIllegalArgumentException)
/*      */     {
/*  714 */       throw new ClassNotFoundException(null, localIllegalArgumentException);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected Object resolveObject(Object paramObject)
/*      */     throws IOException
/*      */   {
/*  746 */     return paramObject;
/*      */   }
/*      */ 
/*      */   protected boolean enableResolveObject(boolean paramBoolean)
/*      */     throws SecurityException
/*      */   {
/*  773 */     if (paramBoolean == this.enableResolve) {
/*  774 */       return paramBoolean;
/*      */     }
/*  776 */     if (paramBoolean) {
/*  777 */       SecurityManager localSecurityManager = System.getSecurityManager();
/*  778 */       if (localSecurityManager != null) {
/*  779 */         localSecurityManager.checkPermission(SUBSTITUTION_PERMISSION);
/*      */       }
/*      */     }
/*  782 */     this.enableResolve = paramBoolean;
/*  783 */     return !this.enableResolve;
/*      */   }
/*      */ 
/*      */   protected void readStreamHeader()
/*      */     throws IOException, StreamCorruptedException
/*      */   {
/*  799 */     short s1 = this.bin.readShort();
/*  800 */     short s2 = this.bin.readShort();
/*  801 */     if ((s1 != -21267) || (s2 != 5))
/*  802 */       throw new StreamCorruptedException(String.format("invalid stream header: %04X%04X", new Object[] { Short.valueOf(s1), Short.valueOf(s2) }));
/*      */   }
/*      */ 
/*      */   protected ObjectStreamClass readClassDescriptor()
/*      */     throws IOException, ClassNotFoundException
/*      */   {
/*  827 */     ObjectStreamClass localObjectStreamClass = new ObjectStreamClass();
/*  828 */     localObjectStreamClass.readNonProxy(this);
/*  829 */     return localObjectStreamClass;
/*      */   }
/*      */ 
/*      */   public int read()
/*      */     throws IOException
/*      */   {
/*  839 */     return this.bin.read();
/*      */   }
/*      */ 
/*      */   public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*      */     throws IOException
/*      */   {
/*  856 */     if (paramArrayOfByte == null) {
/*  857 */       throw new NullPointerException();
/*      */     }
/*  859 */     int i = paramInt1 + paramInt2;
/*  860 */     if ((paramInt1 < 0) || (paramInt2 < 0) || (i > paramArrayOfByte.length) || (i < 0)) {
/*  861 */       throw new IndexOutOfBoundsException();
/*      */     }
/*  863 */     return this.bin.read(paramArrayOfByte, paramInt1, paramInt2, false);
/*      */   }
/*      */ 
/*      */   public int available()
/*      */     throws IOException
/*      */   {
/*  874 */     return this.bin.available();
/*      */   }
/*      */ 
/*      */   public void close()
/*      */     throws IOException
/*      */   {
/*  888 */     this.closed = true;
/*  889 */     if (this.depth == 0) {
/*  890 */       clear();
/*      */     }
/*  892 */     this.bin.close();
/*      */   }
/*      */ 
/*      */   public boolean readBoolean()
/*      */     throws IOException
/*      */   {
/*  903 */     return this.bin.readBoolean();
/*      */   }
/*      */ 
/*      */   public byte readByte()
/*      */     throws IOException
/*      */   {
/*  914 */     return this.bin.readByte();
/*      */   }
/*      */ 
/*      */   public int readUnsignedByte()
/*      */     throws IOException
/*      */   {
/*  925 */     return this.bin.readUnsignedByte();
/*      */   }
/*      */ 
/*      */   public char readChar()
/*      */     throws IOException
/*      */   {
/*  936 */     return this.bin.readChar();
/*      */   }
/*      */ 
/*      */   public short readShort()
/*      */     throws IOException
/*      */   {
/*  947 */     return this.bin.readShort();
/*      */   }
/*      */ 
/*      */   public int readUnsignedShort()
/*      */     throws IOException
/*      */   {
/*  958 */     return this.bin.readUnsignedShort();
/*      */   }
/*      */ 
/*      */   public int readInt()
/*      */     throws IOException
/*      */   {
/*  969 */     return this.bin.readInt();
/*      */   }
/*      */ 
/*      */   public long readLong()
/*      */     throws IOException
/*      */   {
/*  980 */     return this.bin.readLong();
/*      */   }
/*      */ 
/*      */   public float readFloat()
/*      */     throws IOException
/*      */   {
/*  991 */     return this.bin.readFloat();
/*      */   }
/*      */ 
/*      */   public double readDouble()
/*      */     throws IOException
/*      */   {
/* 1002 */     return this.bin.readDouble();
/*      */   }
/*      */ 
/*      */   public void readFully(byte[] paramArrayOfByte)
/*      */     throws IOException
/*      */   {
/* 1013 */     this.bin.readFully(paramArrayOfByte, 0, paramArrayOfByte.length, false);
/*      */   }
/*      */ 
/*      */   public void readFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*      */     throws IOException
/*      */   {
/* 1026 */     int i = paramInt1 + paramInt2;
/* 1027 */     if ((paramInt1 < 0) || (paramInt2 < 0) || (i > paramArrayOfByte.length) || (i < 0)) {
/* 1028 */       throw new IndexOutOfBoundsException();
/*      */     }
/* 1030 */     this.bin.readFully(paramArrayOfByte, paramInt1, paramInt2, false);
/*      */   }
/*      */ 
/*      */   public int skipBytes(int paramInt)
/*      */     throws IOException
/*      */   {
/* 1041 */     return this.bin.skipBytes(paramInt);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public String readLine()
/*      */     throws IOException
/*      */   {
/* 1055 */     return this.bin.readLine();
/*      */   }
/*      */ 
/*      */   public String readUTF()
/*      */     throws IOException
/*      */   {
/* 1070 */     return this.bin.readUTF();
/*      */   }
/*      */ 
/*      */   private void verifySubclass()
/*      */   {
/* 1233 */     Class localClass = getClass();
/* 1234 */     if (localClass == ObjectInputStream.class) {
/* 1235 */       return;
/*      */     }
/* 1237 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 1238 */     if (localSecurityManager == null) {
/* 1239 */       return;
/*      */     }
/* 1241 */     ObjectStreamClass.processQueue(Caches.subclassAuditsQueue, Caches.subclassAudits);
/* 1242 */     ObjectStreamClass.WeakClassKey localWeakClassKey = new ObjectStreamClass.WeakClassKey(localClass, Caches.subclassAuditsQueue);
/* 1243 */     Boolean localBoolean = (Boolean)Caches.subclassAudits.get(localWeakClassKey);
/* 1244 */     if (localBoolean == null) {
/* 1245 */       localBoolean = Boolean.valueOf(auditSubclass(localClass));
/* 1246 */       Caches.subclassAudits.putIfAbsent(localWeakClassKey, localBoolean);
/*      */     }
/* 1248 */     if (localBoolean.booleanValue()) {
/* 1249 */       return;
/*      */     }
/* 1251 */     localSecurityManager.checkPermission(SUBCLASS_IMPLEMENTATION_PERMISSION);
/*      */   }
/*      */ 
/*      */   private static boolean auditSubclass(Class<?> paramClass)
/*      */   {
/* 1260 */     Boolean localBoolean = (Boolean)AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public Boolean run() {
/* 1263 */         for (Class localClass = this.val$subcl; 
/* 1264 */           localClass != ObjectInputStream.class; 
/* 1265 */           localClass = localClass.getSuperclass())
/*      */           try
/*      */           {
/* 1268 */             localClass.getDeclaredMethod("readUnshared", (Class[])null);
/*      */ 
/* 1270 */             return Boolean.FALSE;
/*      */           }
/*      */           catch (NoSuchMethodException localNoSuchMethodException1) {
/*      */             try {
/* 1274 */               localClass.getDeclaredMethod("readFields", (Class[])null);
/* 1275 */               return Boolean.FALSE;
/*      */             } catch (NoSuchMethodException localNoSuchMethodException2) {
/*      */             }
/*      */           }
/* 1279 */         return Boolean.TRUE;
/*      */       }
/*      */     });
/* 1283 */     return localBoolean.booleanValue();
/*      */   }
/*      */ 
/*      */   private void clear()
/*      */   {
/* 1290 */     this.handles.clear();
/* 1291 */     this.vlist.clear();
/*      */   }
/*      */ 
/*      */   private Object readObject0(boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/* 1298 */     boolean bool = this.bin.getBlockDataMode();
/*      */     int i;
/* 1299 */     if (bool) {
/* 1300 */       i = this.bin.currentBlockRemaining();
/* 1301 */       if (i > 0)
/* 1302 */         throw new OptionalDataException(i);
/* 1303 */       if (this.defaultDataEnd)
/*      */       {
/* 1310 */         throw new OptionalDataException(true);
/*      */       }
/* 1312 */       this.bin.setBlockDataMode(false);
/*      */     }
/*      */ 
/* 1316 */     while ((i = this.bin.peekByte()) == 121) {
/* 1317 */       this.bin.readByte();
/* 1318 */       handleReset();
/*      */     }
/*      */ 
/* 1321 */     this.depth += 1;
/*      */     try
/*      */     {
/*      */       Object localObject1;
/* 1323 */       switch (i) {
/*      */       case 112:
/* 1325 */         return readNull();
/*      */       case 113:
/* 1328 */         return readHandle(paramBoolean);
/*      */       case 118:
/* 1331 */         return readClass(paramBoolean);
/*      */       case 114:
/*      */       case 125:
/* 1335 */         return readClassDesc(paramBoolean);
/*      */       case 116:
/*      */       case 124:
/* 1339 */         return checkResolve(readString(paramBoolean));
/*      */       case 117:
/* 1342 */         return checkResolve(readArray(paramBoolean));
/*      */       case 126:
/* 1345 */         return checkResolve(readEnum(paramBoolean));
/*      */       case 115:
/* 1348 */         return checkResolve(readOrdinaryObject(paramBoolean));
/*      */       case 123:
/* 1351 */         localObject1 = readFatalException();
/* 1352 */         throw new WriteAbortedException("writing aborted", (Exception)localObject1);
/*      */       case 119:
/*      */       case 122:
/* 1356 */         if (bool) {
/* 1357 */           this.bin.setBlockDataMode(true);
/* 1358 */           this.bin.peek();
/* 1359 */           throw new OptionalDataException(this.bin.currentBlockRemaining());
/*      */         }
/*      */ 
/* 1362 */         throw new StreamCorruptedException("unexpected block data");
/*      */       case 120:
/* 1367 */         if (bool) {
/* 1368 */           throw new OptionalDataException(true);
/*      */         }
/* 1370 */         throw new StreamCorruptedException("unexpected end of block data");
/*      */       case 121:
/*      */       }
/*      */ 
/* 1375 */       throw new StreamCorruptedException(String.format("invalid type code: %02X", new Object[] { Byte.valueOf(i) }));
/*      */     }
/*      */     finally
/*      */     {
/* 1379 */       this.depth -= 1;
/* 1380 */       this.bin.setBlockDataMode(bool);
/*      */     }
/*      */   }
/*      */ 
/*      */   private Object checkResolve(Object paramObject)
/*      */     throws IOException
/*      */   {
/* 1393 */     if ((!this.enableResolve) || (this.handles.lookupException(this.passHandle) != null)) {
/* 1394 */       return paramObject;
/*      */     }
/* 1396 */     Object localObject = resolveObject(paramObject);
/* 1397 */     if (localObject != paramObject) {
/* 1398 */       this.handles.setObject(this.passHandle, localObject);
/*      */     }
/* 1400 */     return localObject;
/*      */   }
/*      */ 
/*      */   String readTypeString()
/*      */     throws IOException
/*      */   {
/* 1408 */     int i = this.passHandle;
/*      */     try {
/* 1410 */       byte b = this.bin.peekByte();
/*      */       String str;
/* 1411 */       switch (b) {
/*      */       case 112:
/* 1413 */         return (String)readNull();
/*      */       case 113:
/* 1416 */         return (String)readHandle(false);
/*      */       case 116:
/*      */       case 124:
/* 1420 */         return readString(false);
/*      */       }
/*      */ 
/* 1423 */       throw new StreamCorruptedException(String.format("invalid type code: %02X", new Object[] { Byte.valueOf(b) }));
/*      */     }
/*      */     finally
/*      */     {
/* 1427 */       this.passHandle = i;
/*      */     }
/*      */   }
/*      */ 
/*      */   private Object readNull()
/*      */     throws IOException
/*      */   {
/* 1435 */     if (this.bin.readByte() != 112) {
/* 1436 */       throw new InternalError();
/*      */     }
/* 1438 */     this.passHandle = -1;
/* 1439 */     return null;
/*      */   }
/*      */ 
/*      */   private Object readHandle(boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/* 1447 */     if (this.bin.readByte() != 113) {
/* 1448 */       throw new InternalError();
/*      */     }
/* 1450 */     this.passHandle = (this.bin.readInt() - 8257536);
/* 1451 */     if ((this.passHandle < 0) || (this.passHandle >= this.handles.size())) {
/* 1452 */       throw new StreamCorruptedException(String.format("invalid handle value: %08X", new Object[] { Integer.valueOf(this.passHandle + 8257536) }));
/*      */     }
/*      */ 
/* 1456 */     if (paramBoolean)
/*      */     {
/* 1458 */       throw new InvalidObjectException("cannot read back reference as unshared");
/*      */     }
/*      */ 
/* 1462 */     Object localObject = this.handles.lookupObject(this.passHandle);
/* 1463 */     if (localObject == unsharedMarker)
/*      */     {
/* 1465 */       throw new InvalidObjectException("cannot read back reference to unshared object");
/*      */     }
/*      */ 
/* 1468 */     return localObject;
/*      */   }
/*      */ 
/*      */   private Class readClass(boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/* 1478 */     if (this.bin.readByte() != 118) {
/* 1479 */       throw new InternalError();
/*      */     }
/* 1481 */     ObjectStreamClass localObjectStreamClass = readClassDesc(false);
/* 1482 */     Class localClass = localObjectStreamClass.forClass();
/* 1483 */     this.passHandle = this.handles.assign(paramBoolean ? unsharedMarker : localClass);
/*      */ 
/* 1485 */     ClassNotFoundException localClassNotFoundException = localObjectStreamClass.getResolveException();
/* 1486 */     if (localClassNotFoundException != null) {
/* 1487 */       this.handles.markException(this.passHandle, localClassNotFoundException);
/*      */     }
/*      */ 
/* 1490 */     this.handles.finish(this.passHandle);
/* 1491 */     return localClass;
/*      */   }
/*      */ 
/*      */   private ObjectStreamClass readClassDesc(boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/* 1503 */     byte b = this.bin.peekByte();
/* 1504 */     switch (b) {
/*      */     case 112:
/* 1506 */       return (ObjectStreamClass)readNull();
/*      */     case 113:
/* 1509 */       return (ObjectStreamClass)readHandle(paramBoolean);
/*      */     case 125:
/* 1512 */       return readProxyDesc(paramBoolean);
/*      */     case 114:
/* 1515 */       return readNonProxyDesc(paramBoolean);
/*      */     }
/*      */ 
/* 1518 */     throw new StreamCorruptedException(String.format("invalid type code: %02X", new Object[] { Byte.valueOf(b) }));
/*      */   }
/*      */ 
/*      */   private boolean isCustomSubclass()
/*      */   {
/* 1525 */     return getClass().getClassLoader() != ObjectInputStream.class.getClassLoader();
/*      */   }
/*      */ 
/*      */   private ObjectStreamClass readProxyDesc(boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/* 1538 */     if (this.bin.readByte() != 125) {
/* 1539 */       throw new InternalError();
/*      */     }
/*      */ 
/* 1542 */     ObjectStreamClass localObjectStreamClass = new ObjectStreamClass();
/* 1543 */     int i = this.handles.assign(paramBoolean ? unsharedMarker : localObjectStreamClass);
/* 1544 */     this.passHandle = -1;
/*      */ 
/* 1546 */     int j = this.bin.readInt();
/* 1547 */     String[] arrayOfString = new String[j];
/* 1548 */     for (int k = 0; k < j; k++) {
/* 1549 */       arrayOfString[k] = this.bin.readUTF();
/*      */     }
/*      */ 
/* 1552 */     Class localClass = null;
/* 1553 */     Object localObject = null;
/* 1554 */     this.bin.setBlockDataMode(true);
/*      */     try {
/* 1556 */       if ((localClass = resolveProxyClass(arrayOfString)) == null) {
/* 1557 */         localObject = new ClassNotFoundException("null class"); } else {
/* 1558 */         if (!Proxy.isProxyClass(localClass)) {
/* 1559 */           throw new InvalidClassException("Not a proxy");
/*      */         }
/*      */ 
/* 1564 */         ReflectUtil.checkProxyPackageAccess(getClass().getClassLoader(), localClass.getInterfaces());
/*      */       }
/*      */     }
/*      */     catch (ClassNotFoundException localClassNotFoundException)
/*      */     {
/* 1569 */       localObject = localClassNotFoundException;
/*      */     }
/* 1571 */     skipCustomData();
/*      */ 
/* 1573 */     localObjectStreamClass.initProxy(localClass, (ClassNotFoundException)localObject, readClassDesc(false));
/*      */ 
/* 1575 */     this.handles.finish(i);
/* 1576 */     this.passHandle = i;
/* 1577 */     return localObjectStreamClass;
/*      */   }
/*      */ 
/*      */   private ObjectStreamClass readNonProxyDesc(boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/* 1589 */     if (this.bin.readByte() != 114) {
/* 1590 */       throw new InternalError();
/*      */     }
/*      */ 
/* 1593 */     ObjectStreamClass localObjectStreamClass1 = new ObjectStreamClass();
/* 1594 */     int i = this.handles.assign(paramBoolean ? unsharedMarker : localObjectStreamClass1);
/* 1595 */     this.passHandle = -1;
/*      */ 
/* 1597 */     ObjectStreamClass localObjectStreamClass2 = null;
/*      */     try {
/* 1599 */       localObjectStreamClass2 = readClassDescriptor();
/*      */     } catch (ClassNotFoundException localClassNotFoundException1) {
/* 1601 */       throw ((IOException)new InvalidClassException("failed to read class descriptor").initCause(localClassNotFoundException1));
/*      */     }
/*      */ 
/* 1605 */     Class localClass = null;
/* 1606 */     Object localObject = null;
/* 1607 */     this.bin.setBlockDataMode(true);
/* 1608 */     boolean bool = isCustomSubclass();
/*      */     try {
/* 1610 */       if ((localClass = resolveClass(localObjectStreamClass2)) == null)
/* 1611 */         localObject = new ClassNotFoundException("null class");
/* 1612 */       else if (bool)
/* 1613 */         ReflectUtil.checkPackageAccess(localClass);
/*      */     }
/*      */     catch (ClassNotFoundException localClassNotFoundException2) {
/* 1616 */       localObject = localClassNotFoundException2;
/*      */     }
/* 1618 */     skipCustomData();
/*      */ 
/* 1620 */     localObjectStreamClass1.initNonProxy(localObjectStreamClass2, localClass, (ClassNotFoundException)localObject, readClassDesc(false));
/*      */ 
/* 1622 */     this.handles.finish(i);
/* 1623 */     this.passHandle = i;
/* 1624 */     return localObjectStreamClass1;
/*      */   }
/*      */ 
/*      */   private String readString(boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/* 1633 */     byte b = this.bin.readByte();
/*      */     String str;
/* 1634 */     switch (b) {
/*      */     case 116:
/* 1636 */       str = this.bin.readUTF();
/* 1637 */       break;
/*      */     case 124:
/* 1640 */       str = this.bin.readLongUTF();
/* 1641 */       break;
/*      */     default:
/* 1644 */       throw new StreamCorruptedException(String.format("invalid type code: %02X", new Object[] { Byte.valueOf(b) }));
/*      */     }
/*      */ 
/* 1647 */     this.passHandle = this.handles.assign(paramBoolean ? unsharedMarker : str);
/* 1648 */     this.handles.finish(this.passHandle);
/* 1649 */     return str;
/*      */   }
/*      */ 
/*      */   private Object readArray(boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/* 1657 */     if (this.bin.readByte() != 117) {
/* 1658 */       throw new InternalError();
/*      */     }
/*      */ 
/* 1661 */     ObjectStreamClass localObjectStreamClass = readClassDesc(false);
/* 1662 */     int i = this.bin.readInt();
/*      */ 
/* 1664 */     Object localObject = null;
/* 1665 */     Class localClass2 = null;
/*      */     Class localClass1;
/* 1666 */     if ((localClass1 = localObjectStreamClass.forClass()) != null) {
/* 1667 */       localClass2 = localClass1.getComponentType();
/* 1668 */       localObject = Array.newInstance(localClass2, i);
/*      */     }
/*      */ 
/* 1671 */     int j = this.handles.assign(paramBoolean ? unsharedMarker : localObject);
/* 1672 */     ClassNotFoundException localClassNotFoundException = localObjectStreamClass.getResolveException();
/* 1673 */     if (localClassNotFoundException != null) {
/* 1674 */       this.handles.markException(j, localClassNotFoundException);
/*      */     }
/*      */ 
/* 1677 */     if (localClass2 == null) {
/* 1678 */       for (int k = 0; k < i; k++)
/* 1679 */         readObject0(false);
/*      */     }
/* 1681 */     else if (localClass2.isPrimitive()) {
/* 1682 */       if (localClass2 == Integer.TYPE)
/* 1683 */         this.bin.readInts((int[])localObject, 0, i);
/* 1684 */       else if (localClass2 == Byte.TYPE)
/* 1685 */         this.bin.readFully((byte[])localObject, 0, i, true);
/* 1686 */       else if (localClass2 == Long.TYPE)
/* 1687 */         this.bin.readLongs((long[])localObject, 0, i);
/* 1688 */       else if (localClass2 == Float.TYPE)
/* 1689 */         this.bin.readFloats((float[])localObject, 0, i);
/* 1690 */       else if (localClass2 == Double.TYPE)
/* 1691 */         this.bin.readDoubles((double[])localObject, 0, i);
/* 1692 */       else if (localClass2 == Short.TYPE)
/* 1693 */         this.bin.readShorts((short[])localObject, 0, i);
/* 1694 */       else if (localClass2 == Character.TYPE)
/* 1695 */         this.bin.readChars((char[])localObject, 0, i);
/* 1696 */       else if (localClass2 == Boolean.TYPE)
/* 1697 */         this.bin.readBooleans((boolean[])localObject, 0, i);
/*      */       else
/* 1699 */         throw new InternalError();
/*      */     }
/*      */     else {
/* 1702 */       Object[] arrayOfObject = (Object[])localObject;
/* 1703 */       for (int m = 0; m < i; m++) {
/* 1704 */         arrayOfObject[m] = readObject0(false);
/* 1705 */         this.handles.markDependency(j, this.passHandle);
/*      */       }
/*      */     }
/*      */ 
/* 1709 */     this.handles.finish(j);
/* 1710 */     this.passHandle = j;
/* 1711 */     return localObject;
/*      */   }
/*      */ 
/*      */   private Enum readEnum(boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/* 1719 */     if (this.bin.readByte() != 126) {
/* 1720 */       throw new InternalError();
/*      */     }
/*      */ 
/* 1723 */     ObjectStreamClass localObjectStreamClass = readClassDesc(false);
/* 1724 */     if (!localObjectStreamClass.isEnum()) {
/* 1725 */       throw new InvalidClassException("non-enum class: " + localObjectStreamClass);
/*      */     }
/*      */ 
/* 1728 */     int i = this.handles.assign(paramBoolean ? unsharedMarker : null);
/* 1729 */     ClassNotFoundException localClassNotFoundException = localObjectStreamClass.getResolveException();
/* 1730 */     if (localClassNotFoundException != null) {
/* 1731 */       this.handles.markException(i, localClassNotFoundException);
/*      */     }
/*      */ 
/* 1734 */     String str = readString(false);
/* 1735 */     Enum localEnum = null;
/* 1736 */     Class localClass = localObjectStreamClass.forClass();
/* 1737 */     if (localClass != null) {
/*      */       try {
/* 1739 */         localEnum = Enum.valueOf(localClass, str);
/*      */       } catch (IllegalArgumentException localIllegalArgumentException) {
/* 1741 */         throw ((IOException)new InvalidObjectException("enum constant " + str + " does not exist in " + localClass).initCause(localIllegalArgumentException));
/*      */       }
/*      */ 
/* 1745 */       if (!paramBoolean) {
/* 1746 */         this.handles.setObject(i, localEnum);
/*      */       }
/*      */     }
/*      */ 
/* 1750 */     this.handles.finish(i);
/* 1751 */     this.passHandle = i;
/* 1752 */     return localEnum;
/*      */   }
/*      */ 
/*      */   private Object readOrdinaryObject(boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/* 1765 */     if (this.bin.readByte() != 115) {
/* 1766 */       throw new InternalError();
/*      */     }
/*      */ 
/* 1769 */     ObjectStreamClass localObjectStreamClass = readClassDesc(false);
/* 1770 */     localObjectStreamClass.checkDeserialize();
/*      */ 
/* 1772 */     Class localClass = localObjectStreamClass.forClass();
/* 1773 */     if ((localClass == String.class) || (localClass == Class.class) || (localClass == ObjectStreamClass.class))
/*      */     {
/* 1775 */       throw new InvalidClassException("invalid class descriptor");
/*      */     }
/*      */     Object localObject1;
/*      */     try
/*      */     {
/* 1780 */       localObject1 = localObjectStreamClass.isInstantiable() ? localObjectStreamClass.newInstance() : null;
/*      */     } catch (Exception localException) {
/* 1782 */       throw ((IOException)new InvalidClassException(localObjectStreamClass.forClass().getName(), "unable to create instance").initCause(localException));
/*      */     }
/*      */ 
/* 1787 */     this.passHandle = this.handles.assign(paramBoolean ? unsharedMarker : localObject1);
/* 1788 */     ClassNotFoundException localClassNotFoundException = localObjectStreamClass.getResolveException();
/* 1789 */     if (localClassNotFoundException != null) {
/* 1790 */       this.handles.markException(this.passHandle, localClassNotFoundException);
/*      */     }
/*      */ 
/* 1793 */     if (localObjectStreamClass.isExternalizable())
/* 1794 */       readExternalData((Externalizable)localObject1, localObjectStreamClass);
/*      */     else {
/* 1796 */       readSerialData(localObject1, localObjectStreamClass);
/*      */     }
/*      */ 
/* 1799 */     this.handles.finish(this.passHandle);
/*      */ 
/* 1801 */     if ((localObject1 != null) && (this.handles.lookupException(this.passHandle) == null) && (localObjectStreamClass.hasReadResolveMethod()))
/*      */     {
/* 1805 */       Object localObject2 = localObjectStreamClass.invokeReadResolve(localObject1);
/* 1806 */       if ((paramBoolean) && (localObject2.getClass().isArray())) {
/* 1807 */         localObject2 = cloneArray(localObject2);
/*      */       }
/* 1809 */       if (localObject2 != localObject1) {
/* 1810 */         this.handles.setObject(this.passHandle, localObject1 = localObject2);
/*      */       }
/*      */     }
/*      */ 
/* 1814 */     return localObject1;
/*      */   }
/*      */ 
/*      */   private void readExternalData(Externalizable paramExternalizable, ObjectStreamClass paramObjectStreamClass)
/*      */     throws IOException
/*      */   {
/* 1826 */     SerialCallbackContext localSerialCallbackContext = this.curContext;
/* 1827 */     this.curContext = null;
/*      */     try {
/* 1829 */       boolean bool = paramObjectStreamClass.hasBlockExternalData();
/* 1830 */       if (bool) {
/* 1831 */         this.bin.setBlockDataMode(true);
/*      */       }
/* 1833 */       if (paramExternalizable != null) {
/*      */         try {
/* 1835 */           paramExternalizable.readExternal(this);
/*      */         }
/*      */         catch (ClassNotFoundException localClassNotFoundException)
/*      */         {
/* 1844 */           this.handles.markException(this.passHandle, localClassNotFoundException);
/*      */         }
/*      */       }
/* 1847 */       if (bool)
/* 1848 */         skipCustomData();
/*      */     }
/*      */     finally {
/* 1851 */       this.curContext = localSerialCallbackContext;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void readSerialData(Object paramObject, ObjectStreamClass paramObjectStreamClass)
/*      */     throws IOException
/*      */   {
/* 1876 */     ObjectStreamClass.ClassDataSlot[] arrayOfClassDataSlot = paramObjectStreamClass.getClassDataLayout();
/* 1877 */     for (int i = 0; i < arrayOfClassDataSlot.length; i++) {
/* 1878 */       ObjectStreamClass localObjectStreamClass = arrayOfClassDataSlot[i].desc;
/*      */ 
/* 1880 */       if (arrayOfClassDataSlot[i].hasData) {
/* 1881 */         if ((paramObject != null) && (localObjectStreamClass.hasReadObjectMethod()) && (this.handles.lookupException(this.passHandle) == null))
/*      */         {
/* 1885 */           SerialCallbackContext localSerialCallbackContext = this.curContext;
/*      */           try
/*      */           {
/* 1888 */             this.curContext = new SerialCallbackContext(paramObject, localObjectStreamClass);
/*      */ 
/* 1890 */             this.bin.setBlockDataMode(true);
/* 1891 */             localObjectStreamClass.invokeReadObject(paramObject, this);
/*      */           }
/*      */           catch (ClassNotFoundException localClassNotFoundException)
/*      */           {
/* 1900 */             this.handles.markException(this.passHandle, localClassNotFoundException);
/*      */           } finally {
/* 1902 */             this.curContext.setUsed();
/* 1903 */             this.curContext = localSerialCallbackContext;
/*      */           }
/*      */ 
/* 1911 */           this.defaultDataEnd = false;
/*      */         } else {
/* 1913 */           defaultReadFields(paramObject, localObjectStreamClass);
/*      */         }
/* 1915 */         if (localObjectStreamClass.hasWriteObjectData())
/* 1916 */           skipCustomData();
/*      */         else {
/* 1918 */           this.bin.setBlockDataMode(false);
/*      */         }
/*      */       }
/* 1921 */       else if ((paramObject != null) && (localObjectStreamClass.hasReadObjectNoDataMethod()) && (this.handles.lookupException(this.passHandle) == null))
/*      */       {
/* 1925 */         localObjectStreamClass.invokeReadObjectNoData(paramObject);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void skipCustomData()
/*      */     throws IOException
/*      */   {
/* 1936 */     int i = this.passHandle;
/*      */     while (true) {
/* 1938 */       if (this.bin.getBlockDataMode()) {
/* 1939 */         this.bin.skipBlockData();
/* 1940 */         this.bin.setBlockDataMode(false);
/*      */       }
/* 1942 */       switch (this.bin.peekByte()) {
/*      */       case 119:
/*      */       case 122:
/* 1945 */         this.bin.setBlockDataMode(true);
/* 1946 */         break;
/*      */       case 120:
/* 1949 */         this.bin.readByte();
/* 1950 */         this.passHandle = i;
/* 1951 */         return;
/*      */       case 121:
/*      */       default:
/* 1954 */         readObject0(false);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void defaultReadFields(Object paramObject, ObjectStreamClass paramObjectStreamClass)
/*      */     throws IOException
/*      */   {
/* 1969 */     Class localClass = paramObjectStreamClass.forClass();
/* 1970 */     if ((localClass != null) && (paramObject != null) && (!localClass.isInstance(paramObject))) {
/* 1971 */       throw new ClassCastException();
/*      */     }
/*      */ 
/* 1974 */     int i = paramObjectStreamClass.getPrimDataSize();
/* 1975 */     if ((this.primVals == null) || (this.primVals.length < i)) {
/* 1976 */       this.primVals = new byte[i];
/*      */     }
/* 1978 */     this.bin.readFully(this.primVals, 0, i, false);
/* 1979 */     if (paramObject != null) {
/* 1980 */       paramObjectStreamClass.setPrimFieldValues(paramObject, this.primVals);
/*      */     }
/*      */ 
/* 1983 */     int j = this.passHandle;
/* 1984 */     ObjectStreamField[] arrayOfObjectStreamField = paramObjectStreamClass.getFields(false);
/* 1985 */     Object[] arrayOfObject = new Object[paramObjectStreamClass.getNumObjFields()];
/* 1986 */     int k = arrayOfObjectStreamField.length - arrayOfObject.length;
/* 1987 */     for (int m = 0; m < arrayOfObject.length; m++) {
/* 1988 */       ObjectStreamField localObjectStreamField = arrayOfObjectStreamField[(k + m)];
/* 1989 */       arrayOfObject[m] = readObject0(localObjectStreamField.isUnshared());
/* 1990 */       if (localObjectStreamField.getField() != null) {
/* 1991 */         this.handles.markDependency(j, this.passHandle);
/*      */       }
/*      */     }
/* 1994 */     if (paramObject != null) {
/* 1995 */       paramObjectStreamClass.setObjFieldValues(paramObject, arrayOfObject);
/*      */     }
/* 1997 */     this.passHandle = j;
/*      */   }
/*      */ 
/*      */   private IOException readFatalException()
/*      */     throws IOException
/*      */   {
/* 2006 */     if (this.bin.readByte() != 123) {
/* 2007 */       throw new InternalError();
/*      */     }
/* 2009 */     clear();
/* 2010 */     return (IOException)readObject0(false);
/*      */   }
/*      */ 
/*      */   private void handleReset()
/*      */     throws StreamCorruptedException
/*      */   {
/* 2019 */     if (this.depth > 0) {
/* 2020 */       throw new StreamCorruptedException("unexpected reset; recursion depth: " + this.depth);
/*      */     }
/*      */ 
/* 2023 */     clear();
/*      */   }
/*      */ 
/*      */   private static native void bytesToFloats(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3);
/*      */ 
/*      */   private static native void bytesToDoubles(byte[] paramArrayOfByte, int paramInt1, double[] paramArrayOfDouble, int paramInt2, int paramInt3);
/*      */ 
/*      */   private static native ClassLoader latestUserDefinedLoader();
/*      */ 
/*      */   private static Object cloneArray(Object paramObject)
/*      */   {
/* 3510 */     if ((paramObject instanceof Object[]))
/* 3511 */       return ((Object[])paramObject).clone();
/* 3512 */     if ((paramObject instanceof boolean[]))
/* 3513 */       return ((boolean[])paramObject).clone();
/* 3514 */     if ((paramObject instanceof byte[]))
/* 3515 */       return ((byte[])paramObject).clone();
/* 3516 */     if ((paramObject instanceof char[]))
/* 3517 */       return ((char[])paramObject).clone();
/* 3518 */     if ((paramObject instanceof double[]))
/* 3519 */       return ((double[])paramObject).clone();
/* 3520 */     if ((paramObject instanceof float[]))
/* 3521 */       return ((float[])paramObject).clone();
/* 3522 */     if ((paramObject instanceof int[]))
/* 3523 */       return ((int[])paramObject).clone();
/* 3524 */     if ((paramObject instanceof long[]))
/* 3525 */       return ((long[])paramObject).clone();
/* 3526 */     if ((paramObject instanceof short[])) {
/* 3527 */       return ((short[])paramObject).clone();
/*      */     }
/* 3529 */     throw new AssertionError();
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  219 */     primClasses.put("boolean", Boolean.TYPE);
/*  220 */     primClasses.put("byte", Byte.TYPE);
/*  221 */     primClasses.put("char", Character.TYPE);
/*  222 */     primClasses.put("short", Short.TYPE);
/*  223 */     primClasses.put("int", Integer.TYPE);
/*  224 */     primClasses.put("long", Long.TYPE);
/*  225 */     primClasses.put("float", Float.TYPE);
/*  226 */     primClasses.put("double", Double.TYPE);
/*  227 */     primClasses.put("void", Void.TYPE);
/*      */   }
/*      */ 
/*      */   private class BlockDataInputStream extends InputStream
/*      */     implements DataInput
/*      */   {
/*      */     private static final int MAX_BLOCK_SIZE = 1024;
/*      */     private static final int MAX_HEADER_SIZE = 5;
/*      */     private static final int CHAR_BUF_SIZE = 256;
/*      */     private static final int HEADER_BLOCKED = -2;
/* 2372 */     private final byte[] buf = new byte[1024];
/*      */ 
/* 2374 */     private final byte[] hbuf = new byte[5];
/*      */ 
/* 2376 */     private final char[] cbuf = new char[256];
/*      */ 
/* 2379 */     private boolean blkmode = false;
/*      */ 
/* 2383 */     private int pos = 0;
/*      */ 
/* 2385 */     private int end = -1;
/*      */ 
/* 2387 */     private int unread = 0;
/*      */     private final ObjectInputStream.PeekInputStream in;
/*      */     private final DataInputStream din;
/*      */ 
/*      */     BlockDataInputStream(InputStream arg2)
/*      */     {
/*      */       InputStream localInputStream;
/* 2399 */       this.in = new ObjectInputStream.PeekInputStream(localInputStream);
/* 2400 */       this.din = new DataInputStream(this);
/*      */     }
/*      */ 
/*      */     boolean setBlockDataMode(boolean paramBoolean)
/*      */       throws IOException
/*      */     {
/* 2411 */       if (this.blkmode == paramBoolean) {
/* 2412 */         return this.blkmode;
/*      */       }
/* 2414 */       if (paramBoolean) {
/* 2415 */         this.pos = 0;
/* 2416 */         this.end = 0;
/* 2417 */         this.unread = 0;
/* 2418 */       } else if (this.pos < this.end) {
/* 2419 */         throw new IllegalStateException("unread block data");
/*      */       }
/* 2421 */       this.blkmode = paramBoolean;
/* 2422 */       return !this.blkmode;
/*      */     }
/*      */ 
/*      */     boolean getBlockDataMode()
/*      */     {
/* 2430 */       return this.blkmode;
/*      */     }
/*      */ 
/*      */     void skipBlockData()
/*      */       throws IOException
/*      */     {
/* 2439 */       if (!this.blkmode) {
/* 2440 */         throw new IllegalStateException("not in block data mode");
/*      */       }
/* 2442 */       while (this.end >= 0)
/* 2443 */         refill();
/*      */     }
/*      */ 
/*      */     private int readBlockHeader(boolean paramBoolean)
/*      */       throws IOException
/*      */     {
/* 2455 */       if (ObjectInputStream.this.defaultDataEnd)
/*      */       {
/* 2462 */         return -1;
/*      */       }
/*      */       try {
/*      */         while (true) {
/* 2466 */           int i = paramBoolean ? 2147483647 : this.in.available();
/* 2467 */           if (i == 0) {
/* 2468 */             return -2;
/*      */           }
/*      */ 
/* 2471 */           int j = this.in.peek();
/* 2472 */           switch (j) {
/*      */           case 119:
/* 2474 */             if (i < 2) {
/* 2475 */               return -2;
/*      */             }
/* 2477 */             this.in.readFully(this.hbuf, 0, 2);
/* 2478 */             return this.hbuf[1] & 0xFF;
/*      */           case 122:
/* 2481 */             if (i < 5) {
/* 2482 */               return -2;
/*      */             }
/* 2484 */             this.in.readFully(this.hbuf, 0, 5);
/* 2485 */             int k = Bits.getInt(this.hbuf, 1);
/* 2486 */             if (k < 0) {
/* 2487 */               throw new StreamCorruptedException("illegal block data header length: " + k);
/*      */             }
/*      */ 
/* 2491 */             return k;
/*      */           case 121:
/* 2500 */             this.in.read();
/* 2501 */             ObjectInputStream.this.handleReset();
/* 2502 */             break;
/*      */           case 120:
/*      */           default:
/* 2505 */             if ((j >= 0) && ((j < 112) || (j > 126))) {
/* 2506 */               throw new StreamCorruptedException(String.format("invalid type code: %02X", new Object[] { Integer.valueOf(j) }));
/*      */             }
/*      */ 
/* 2510 */             return -1;
/*      */           }
/*      */         }
/*      */       } catch (EOFException localEOFException) {  }
/*      */ 
/* 2514 */       throw new StreamCorruptedException("unexpected EOF while reading block data header");
/*      */     }
/*      */ 
/*      */     private void refill()
/*      */       throws IOException
/*      */     {
/*      */       try
/*      */       {
/*      */         do
/*      */         {
/* 2529 */           this.pos = 0;
/*      */           int i;
/* 2530 */           if (this.unread > 0) {
/* 2531 */             i = this.in.read(this.buf, 0, Math.min(this.unread, 1024));
/*      */ 
/* 2533 */             if (i >= 0) {
/* 2534 */               this.end = i;
/* 2535 */               this.unread -= i;
/*      */             } else {
/* 2537 */               throw new StreamCorruptedException("unexpected EOF in middle of data block");
/*      */             }
/*      */           }
/*      */           else {
/* 2541 */             i = readBlockHeader(true);
/* 2542 */             if (i >= 0) {
/* 2543 */               this.end = 0;
/* 2544 */               this.unread = i;
/*      */             } else {
/* 2546 */               this.end = -1;
/* 2547 */               this.unread = 0;
/*      */             }
/*      */           }
/*      */         }
/* 2550 */         while (this.pos == this.end);
/*      */       } catch (IOException localIOException) {
/* 2552 */         this.pos = 0;
/* 2553 */         this.end = -1;
/* 2554 */         this.unread = 0;
/* 2555 */         throw localIOException;
/*      */       }
/*      */     }
/*      */ 
/*      */     int currentBlockRemaining()
/*      */     {
/* 2565 */       if (this.blkmode) {
/* 2566 */         return this.end >= 0 ? this.end - this.pos + this.unread : 0;
/*      */       }
/* 2568 */       throw new IllegalStateException();
/*      */     }
/*      */ 
/*      */     int peek()
/*      */       throws IOException
/*      */     {
/* 2578 */       if (this.blkmode) {
/* 2579 */         if (this.pos == this.end) {
/* 2580 */           refill();
/*      */         }
/* 2582 */         return this.end >= 0 ? this.buf[this.pos] & 0xFF : -1;
/*      */       }
/* 2584 */       return this.in.peek();
/*      */     }
/*      */ 
/*      */     byte peekByte()
/*      */       throws IOException
/*      */     {
/* 2594 */       int i = peek();
/* 2595 */       if (i < 0) {
/* 2596 */         throw new EOFException();
/*      */       }
/* 2598 */       return (byte)i;
/*      */     }
/*      */ 
/*      */     public int read()
/*      */       throws IOException
/*      */     {
/* 2611 */       if (this.blkmode) {
/* 2612 */         if (this.pos == this.end) {
/* 2613 */           refill();
/*      */         }
/* 2615 */         return this.end >= 0 ? this.buf[(this.pos++)] & 0xFF : -1;
/*      */       }
/* 2617 */       return this.in.read();
/*      */     }
/*      */ 
/*      */     public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException
/*      */     {
/* 2622 */       return read(paramArrayOfByte, paramInt1, paramInt2, false);
/*      */     }
/*      */ 
/*      */     public long skip(long paramLong) throws IOException {
/* 2626 */       long l = paramLong;
/* 2627 */       while (l > 0L)
/*      */       {
/*      */         int i;
/* 2628 */         if (this.blkmode) {
/* 2629 */           if (this.pos == this.end) {
/* 2630 */             refill();
/*      */           }
/* 2632 */           if (this.end < 0) {
/*      */             break;
/*      */           }
/* 2635 */           i = (int)Math.min(l, this.end - this.pos);
/* 2636 */           l -= i;
/* 2637 */           this.pos += i;
/*      */         } else {
/* 2639 */           i = (int)Math.min(l, 1024L);
/* 2640 */           if ((i = this.in.read(this.buf, 0, i)) < 0) {
/*      */             break;
/*      */           }
/* 2643 */           l -= i;
/*      */         }
/*      */       }
/* 2646 */       return paramLong - l;
/*      */     }
/*      */ 
/*      */     public int available() throws IOException {
/* 2650 */       if (this.blkmode) {
/* 2651 */         if ((this.pos == this.end) && (this.unread == 0))
/*      */         {
/* 2653 */           while ((i = readBlockHeader(false)) == 0);
/* 2654 */           switch (i) {
/*      */           case -2:
/* 2656 */             break;
/*      */           case -1:
/* 2659 */             this.pos = 0;
/* 2660 */             this.end = -1;
/* 2661 */             break;
/*      */           default:
/* 2664 */             this.pos = 0;
/* 2665 */             this.end = 0;
/* 2666 */             this.unread = i;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 2671 */         int i = this.unread > 0 ? Math.min(this.in.available(), this.unread) : 0;
/*      */ 
/* 2673 */         return this.end >= 0 ? this.end - this.pos + i : 0;
/*      */       }
/* 2675 */       return this.in.available();
/*      */     }
/*      */ 
/*      */     public void close() throws IOException
/*      */     {
/* 2680 */       if (this.blkmode) {
/* 2681 */         this.pos = 0;
/* 2682 */         this.end = -1;
/* 2683 */         this.unread = 0;
/*      */       }
/* 2685 */       this.in.close();
/*      */     }
/*      */ 
/*      */     int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean)
/*      */       throws IOException
/*      */     {
/* 2696 */       if (paramInt2 == 0)
/* 2697 */         return 0;
/*      */       int i;
/* 2698 */       if (this.blkmode) {
/* 2699 */         if (this.pos == this.end) {
/* 2700 */           refill();
/*      */         }
/* 2702 */         if (this.end < 0) {
/* 2703 */           return -1;
/*      */         }
/* 2705 */         i = Math.min(paramInt2, this.end - this.pos);
/* 2706 */         System.arraycopy(this.buf, this.pos, paramArrayOfByte, paramInt1, i);
/* 2707 */         this.pos += i;
/* 2708 */         return i;
/* 2709 */       }if (paramBoolean) {
/* 2710 */         i = this.in.read(this.buf, 0, Math.min(paramInt2, 1024));
/* 2711 */         if (i > 0) {
/* 2712 */           System.arraycopy(this.buf, 0, paramArrayOfByte, paramInt1, i);
/*      */         }
/* 2714 */         return i;
/*      */       }
/* 2716 */       return this.in.read(paramArrayOfByte, paramInt1, paramInt2);
/*      */     }
/*      */ 
/*      */     public void readFully(byte[] paramArrayOfByte)
/*      */       throws IOException
/*      */     {
/* 2729 */       readFully(paramArrayOfByte, 0, paramArrayOfByte.length, false);
/*      */     }
/*      */ 
/*      */     public void readFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
/* 2733 */       readFully(paramArrayOfByte, paramInt1, paramInt2, false);
/*      */     }
/*      */ 
/*      */     public void readFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean)
/*      */       throws IOException
/*      */     {
/* 2739 */       while (paramInt2 > 0) {
/* 2740 */         int i = read(paramArrayOfByte, paramInt1, paramInt2, paramBoolean);
/* 2741 */         if (i < 0) {
/* 2742 */           throw new EOFException();
/*      */         }
/* 2744 */         paramInt1 += i;
/* 2745 */         paramInt2 -= i;
/*      */       }
/*      */     }
/*      */ 
/*      */     public int skipBytes(int paramInt) throws IOException {
/* 2750 */       return this.din.skipBytes(paramInt);
/*      */     }
/*      */ 
/*      */     public boolean readBoolean() throws IOException {
/* 2754 */       int i = read();
/* 2755 */       if (i < 0) {
/* 2756 */         throw new EOFException();
/*      */       }
/* 2758 */       return i != 0;
/*      */     }
/*      */ 
/*      */     public byte readByte() throws IOException {
/* 2762 */       int i = read();
/* 2763 */       if (i < 0) {
/* 2764 */         throw new EOFException();
/*      */       }
/* 2766 */       return (byte)i;
/*      */     }
/*      */ 
/*      */     public int readUnsignedByte() throws IOException {
/* 2770 */       int i = read();
/* 2771 */       if (i < 0) {
/* 2772 */         throw new EOFException();
/*      */       }
/* 2774 */       return i;
/*      */     }
/*      */ 
/*      */     public char readChar() throws IOException {
/* 2778 */       if (!this.blkmode) {
/* 2779 */         this.pos = 0;
/* 2780 */         this.in.readFully(this.buf, 0, 2);
/* 2781 */       } else if (this.end - this.pos < 2) {
/* 2782 */         return this.din.readChar();
/*      */       }
/* 2784 */       char c = Bits.getChar(this.buf, this.pos);
/* 2785 */       this.pos += 2;
/* 2786 */       return c;
/*      */     }
/*      */ 
/*      */     public short readShort() throws IOException {
/* 2790 */       if (!this.blkmode) {
/* 2791 */         this.pos = 0;
/* 2792 */         this.in.readFully(this.buf, 0, 2);
/* 2793 */       } else if (this.end - this.pos < 2) {
/* 2794 */         return this.din.readShort();
/*      */       }
/* 2796 */       short s = Bits.getShort(this.buf, this.pos);
/* 2797 */       this.pos += 2;
/* 2798 */       return s;
/*      */     }
/*      */ 
/*      */     public int readUnsignedShort() throws IOException {
/* 2802 */       if (!this.blkmode) {
/* 2803 */         this.pos = 0;
/* 2804 */         this.in.readFully(this.buf, 0, 2);
/* 2805 */       } else if (this.end - this.pos < 2) {
/* 2806 */         return this.din.readUnsignedShort();
/*      */       }
/* 2808 */       int i = Bits.getShort(this.buf, this.pos) & 0xFFFF;
/* 2809 */       this.pos += 2;
/* 2810 */       return i;
/*      */     }
/*      */ 
/*      */     public int readInt() throws IOException {
/* 2814 */       if (!this.blkmode) {
/* 2815 */         this.pos = 0;
/* 2816 */         this.in.readFully(this.buf, 0, 4);
/* 2817 */       } else if (this.end - this.pos < 4) {
/* 2818 */         return this.din.readInt();
/*      */       }
/* 2820 */       int i = Bits.getInt(this.buf, this.pos);
/* 2821 */       this.pos += 4;
/* 2822 */       return i;
/*      */     }
/*      */ 
/*      */     public float readFloat() throws IOException {
/* 2826 */       if (!this.blkmode) {
/* 2827 */         this.pos = 0;
/* 2828 */         this.in.readFully(this.buf, 0, 4);
/* 2829 */       } else if (this.end - this.pos < 4) {
/* 2830 */         return this.din.readFloat();
/*      */       }
/* 2832 */       float f = Bits.getFloat(this.buf, this.pos);
/* 2833 */       this.pos += 4;
/* 2834 */       return f;
/*      */     }
/*      */ 
/*      */     public long readLong() throws IOException {
/* 2838 */       if (!this.blkmode) {
/* 2839 */         this.pos = 0;
/* 2840 */         this.in.readFully(this.buf, 0, 8);
/* 2841 */       } else if (this.end - this.pos < 8) {
/* 2842 */         return this.din.readLong();
/*      */       }
/* 2844 */       long l = Bits.getLong(this.buf, this.pos);
/* 2845 */       this.pos += 8;
/* 2846 */       return l;
/*      */     }
/*      */ 
/*      */     public double readDouble() throws IOException {
/* 2850 */       if (!this.blkmode) {
/* 2851 */         this.pos = 0;
/* 2852 */         this.in.readFully(this.buf, 0, 8);
/* 2853 */       } else if (this.end - this.pos < 8) {
/* 2854 */         return this.din.readDouble();
/*      */       }
/* 2856 */       double d = Bits.getDouble(this.buf, this.pos);
/* 2857 */       this.pos += 8;
/* 2858 */       return d;
/*      */     }
/*      */ 
/*      */     public String readUTF() throws IOException {
/* 2862 */       return readUTFBody(readUnsignedShort());
/*      */     }
/*      */ 
/*      */     public String readLine() throws IOException {
/* 2866 */       return this.din.readLine();
/*      */     }
/*      */ 
/*      */     void readBooleans(boolean[] paramArrayOfBoolean, int paramInt1, int paramInt2)
/*      */       throws IOException
/*      */     {
/* 2878 */       int j = paramInt1 + paramInt2;
/* 2879 */       while (paramInt1 < j)
/*      */       {
/*      */         int i;
/* 2880 */         if (!this.blkmode) {
/* 2881 */           int k = Math.min(j - paramInt1, 1024);
/* 2882 */           this.in.readFully(this.buf, 0, k);
/* 2883 */           i = paramInt1 + k;
/* 2884 */           this.pos = 0; } else {
/* 2885 */           if (this.end - this.pos < 1) {
/* 2886 */             paramArrayOfBoolean[(paramInt1++)] = this.din.readBoolean();
/* 2887 */             continue;
/*      */           }
/* 2889 */           i = Math.min(j, paramInt1 + this.end - this.pos);
/*      */         }
/*      */ 
/* 2892 */         while (paramInt1 < i)
/* 2893 */           paramArrayOfBoolean[(paramInt1++)] = Bits.getBoolean(this.buf, this.pos++);
/*      */       }
/*      */     }
/*      */ 
/*      */     void readChars(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException
/*      */     {
/* 2899 */       int j = paramInt1 + paramInt2;
/* 2900 */       while (paramInt1 < j)
/*      */       {
/*      */         int i;
/* 2901 */         if (!this.blkmode) {
/* 2902 */           int k = Math.min(j - paramInt1, 512);
/* 2903 */           this.in.readFully(this.buf, 0, k << 1);
/* 2904 */           i = paramInt1 + k;
/* 2905 */           this.pos = 0; } else {
/* 2906 */           if (this.end - this.pos < 2) {
/* 2907 */             paramArrayOfChar[(paramInt1++)] = this.din.readChar();
/* 2908 */             continue;
/*      */           }
/* 2910 */           i = Math.min(j, paramInt1 + (this.end - this.pos >> 1));
/*      */         }
/*      */ 
/* 2913 */         while (paramInt1 < i) {
/* 2914 */           paramArrayOfChar[(paramInt1++)] = Bits.getChar(this.buf, this.pos);
/* 2915 */           this.pos += 2;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     void readShorts(short[] paramArrayOfShort, int paramInt1, int paramInt2) throws IOException {
/* 2921 */       int j = paramInt1 + paramInt2;
/* 2922 */       while (paramInt1 < j)
/*      */       {
/*      */         int i;
/* 2923 */         if (!this.blkmode) {
/* 2924 */           int k = Math.min(j - paramInt1, 512);
/* 2925 */           this.in.readFully(this.buf, 0, k << 1);
/* 2926 */           i = paramInt1 + k;
/* 2927 */           this.pos = 0; } else {
/* 2928 */           if (this.end - this.pos < 2) {
/* 2929 */             paramArrayOfShort[(paramInt1++)] = this.din.readShort();
/* 2930 */             continue;
/*      */           }
/* 2932 */           i = Math.min(j, paramInt1 + (this.end - this.pos >> 1));
/*      */         }
/*      */ 
/* 2935 */         while (paramInt1 < i) {
/* 2936 */           paramArrayOfShort[(paramInt1++)] = Bits.getShort(this.buf, this.pos);
/* 2937 */           this.pos += 2;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     void readInts(int[] paramArrayOfInt, int paramInt1, int paramInt2) throws IOException {
/* 2943 */       int j = paramInt1 + paramInt2;
/* 2944 */       while (paramInt1 < j)
/*      */       {
/*      */         int i;
/* 2945 */         if (!this.blkmode) {
/* 2946 */           int k = Math.min(j - paramInt1, 256);
/* 2947 */           this.in.readFully(this.buf, 0, k << 2);
/* 2948 */           i = paramInt1 + k;
/* 2949 */           this.pos = 0; } else {
/* 2950 */           if (this.end - this.pos < 4) {
/* 2951 */             paramArrayOfInt[(paramInt1++)] = this.din.readInt();
/* 2952 */             continue;
/*      */           }
/* 2954 */           i = Math.min(j, paramInt1 + (this.end - this.pos >> 2));
/*      */         }
/*      */ 
/* 2957 */         while (paramInt1 < i) {
/* 2958 */           paramArrayOfInt[(paramInt1++)] = Bits.getInt(this.buf, this.pos);
/* 2959 */           this.pos += 4;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     void readFloats(float[] paramArrayOfFloat, int paramInt1, int paramInt2) throws IOException {
/* 2965 */       int j = paramInt1 + paramInt2;
/* 2966 */       while (paramInt1 < j)
/*      */       {
/*      */         int i;
/* 2967 */         if (!this.blkmode) {
/* 2968 */           i = Math.min(j - paramInt1, 256);
/* 2969 */           this.in.readFully(this.buf, 0, i << 2);
/* 2970 */           this.pos = 0; } else {
/* 2971 */           if (this.end - this.pos < 4) {
/* 2972 */             paramArrayOfFloat[(paramInt1++)] = this.din.readFloat();
/* 2973 */             continue;
/*      */           }
/* 2975 */           i = Math.min(j - paramInt1, this.end - this.pos >> 2);
/*      */         }
/*      */ 
/* 2978 */         ObjectInputStream.bytesToFloats(this.buf, this.pos, paramArrayOfFloat, paramInt1, i);
/* 2979 */         paramInt1 += i;
/* 2980 */         this.pos += (i << 2);
/*      */       }
/*      */     }
/*      */ 
/*      */     void readLongs(long[] paramArrayOfLong, int paramInt1, int paramInt2) throws IOException {
/* 2985 */       int j = paramInt1 + paramInt2;
/* 2986 */       while (paramInt1 < j)
/*      */       {
/*      */         int i;
/* 2987 */         if (!this.blkmode) {
/* 2988 */           int k = Math.min(j - paramInt1, 128);
/* 2989 */           this.in.readFully(this.buf, 0, k << 3);
/* 2990 */           i = paramInt1 + k;
/* 2991 */           this.pos = 0; } else {
/* 2992 */           if (this.end - this.pos < 8) {
/* 2993 */             paramArrayOfLong[(paramInt1++)] = this.din.readLong();
/* 2994 */             continue;
/*      */           }
/* 2996 */           i = Math.min(j, paramInt1 + (this.end - this.pos >> 3));
/*      */         }
/*      */ 
/* 2999 */         while (paramInt1 < i) {
/* 3000 */           paramArrayOfLong[(paramInt1++)] = Bits.getLong(this.buf, this.pos);
/* 3001 */           this.pos += 8;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     void readDoubles(double[] paramArrayOfDouble, int paramInt1, int paramInt2) throws IOException {
/* 3007 */       int j = paramInt1 + paramInt2;
/* 3008 */       while (paramInt1 < j)
/*      */       {
/*      */         int i;
/* 3009 */         if (!this.blkmode) {
/* 3010 */           i = Math.min(j - paramInt1, 128);
/* 3011 */           this.in.readFully(this.buf, 0, i << 3);
/* 3012 */           this.pos = 0; } else {
/* 3013 */           if (this.end - this.pos < 8) {
/* 3014 */             paramArrayOfDouble[(paramInt1++)] = this.din.readDouble();
/* 3015 */             continue;
/*      */           }
/* 3017 */           i = Math.min(j - paramInt1, this.end - this.pos >> 3);
/*      */         }
/*      */ 
/* 3020 */         ObjectInputStream.bytesToDoubles(this.buf, this.pos, paramArrayOfDouble, paramInt1, i);
/* 3021 */         paramInt1 += i;
/* 3022 */         this.pos += (i << 3);
/*      */       }
/*      */     }
/*      */ 
/*      */     String readLongUTF()
/*      */       throws IOException
/*      */     {
/* 3032 */       return readUTFBody(readLong());
/*      */     }
/*      */ 
/*      */     private String readUTFBody(long paramLong)
/*      */       throws IOException
/*      */     {
/* 3041 */       StringBuilder localStringBuilder = new StringBuilder();
/* 3042 */       if (!this.blkmode) {
/* 3043 */         this.end = (this.pos = 0);
/*      */       }
/*      */ 
/* 3046 */       while (paramLong > 0L) {
/* 3047 */         int i = this.end - this.pos;
/* 3048 */         if ((i >= 3) || (i == paramLong)) {
/* 3049 */           paramLong -= readUTFSpan(localStringBuilder, paramLong);
/*      */         }
/* 3051 */         else if (this.blkmode)
/*      */         {
/* 3053 */           paramLong -= readUTFChar(localStringBuilder, paramLong);
/*      */         }
/*      */         else {
/* 3056 */           if (i > 0) {
/* 3057 */             System.arraycopy(this.buf, this.pos, this.buf, 0, i);
/*      */           }
/* 3059 */           this.pos = 0;
/* 3060 */           this.end = ((int)Math.min(1024L, paramLong));
/* 3061 */           this.in.readFully(this.buf, i, this.end - i);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 3066 */       return localStringBuilder.toString();
/*      */     }
/*      */ 
/*      */     private long readUTFSpan(StringBuilder paramStringBuilder, long paramLong)
/*      */       throws IOException
/*      */     {
/* 3078 */       int i = 0;
/* 3079 */       int j = this.pos;
/* 3080 */       int k = Math.min(this.end - this.pos, 256);
/*      */ 
/* 3082 */       int m = this.pos + (paramLong > k ? k - 2 : (int)paramLong);
/* 3083 */       int n = 0;
/*      */       try
/*      */       {
/* 3086 */         while (this.pos < m)
/*      */         {
/* 3088 */           int i1 = this.buf[(this.pos++)] & 0xFF;
/*      */           int i2;
/* 3089 */           switch (i1 >> 4) {
/*      */           case 0:
/*      */           case 1:
/*      */           case 2:
/*      */           case 3:
/*      */           case 4:
/*      */           case 5:
/*      */           case 6:
/*      */           case 7:
/* 3098 */             this.cbuf[(i++)] = ((char)i1);
/* 3099 */             break;
/*      */           case 12:
/*      */           case 13:
/* 3103 */             i2 = this.buf[(this.pos++)];
/* 3104 */             if ((i2 & 0xC0) != 128) {
/* 3105 */               throw new UTFDataFormatException();
/*      */             }
/* 3107 */             this.cbuf[(i++)] = ((char)((i1 & 0x1F) << 6 | (i2 & 0x3F) << 0));
/*      */ 
/* 3109 */             break;
/*      */           case 14:
/* 3112 */             int i3 = this.buf[(this.pos + 1)];
/* 3113 */             i2 = this.buf[(this.pos + 0)];
/* 3114 */             this.pos += 2;
/* 3115 */             if (((i2 & 0xC0) != 128) || ((i3 & 0xC0) != 128)) {
/* 3116 */               throw new UTFDataFormatException();
/*      */             }
/* 3118 */             this.cbuf[(i++)] = ((char)((i1 & 0xF) << 12 | (i2 & 0x3F) << 6 | (i3 & 0x3F) << 0));
/*      */ 
/* 3121 */             break;
/*      */           case 8:
/*      */           case 9:
/*      */           case 10:
/*      */           case 11:
/*      */           default:
/* 3124 */             throw new UTFDataFormatException();
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 3130 */         if ((n != 0) || (this.pos - j > paramLong))
/*      */         {
/* 3136 */           this.pos = (j + (int)paramLong);
/*      */           throw new UTFDataFormatException();
/*      */         }
/*      */       }
/*      */       catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
/*      */       {
/* 3128 */         n = 1;
/*      */ 
/* 3130 */         if ((n != 0) || (this.pos - j > paramLong))
/*      */         {
/* 3136 */           this.pos = (j + (int)paramLong);
/*      */           throw new UTFDataFormatException();
/*      */         }
/*      */       }
/*      */       finally
/*      */       {
/* 3130 */         if ((n != 0) || (this.pos - j > paramLong))
/*      */         {
/* 3136 */           this.pos = (j + (int)paramLong);
/* 3137 */           throw new UTFDataFormatException();
/*      */         }
/*      */       }
/*      */ 
/* 3141 */       paramStringBuilder.append(this.cbuf, 0, i);
/* 3142 */       return this.pos - j;
/*      */     }
/*      */ 
/*      */     private int readUTFChar(StringBuilder paramStringBuilder, long paramLong)
/*      */       throws IOException
/*      */     {
/* 3156 */       int i = readByte() & 0xFF;
/*      */       int j;
/* 3157 */       switch (i >> 4) {
/*      */       case 0:
/*      */       case 1:
/*      */       case 2:
/*      */       case 3:
/*      */       case 4:
/*      */       case 5:
/*      */       case 6:
/*      */       case 7:
/* 3166 */         paramStringBuilder.append((char)i);
/* 3167 */         return 1;
/*      */       case 12:
/*      */       case 13:
/* 3171 */         if (paramLong < 2L) {
/* 3172 */           throw new UTFDataFormatException();
/*      */         }
/* 3174 */         j = readByte();
/* 3175 */         if ((j & 0xC0) != 128) {
/* 3176 */           throw new UTFDataFormatException();
/*      */         }
/* 3178 */         paramStringBuilder.append((char)((i & 0x1F) << 6 | (j & 0x3F) << 0));
/*      */ 
/* 3180 */         return 2;
/*      */       case 14:
/* 3183 */         if (paramLong < 3L) {
/* 3184 */           if (paramLong == 2L) {
/* 3185 */             readByte();
/*      */           }
/* 3187 */           throw new UTFDataFormatException();
/*      */         }
/* 3189 */         j = readByte();
/* 3190 */         int k = readByte();
/* 3191 */         if (((j & 0xC0) != 128) || ((k & 0xC0) != 128)) {
/* 3192 */           throw new UTFDataFormatException();
/*      */         }
/* 3194 */         paramStringBuilder.append((char)((i & 0xF) << 12 | (j & 0x3F) << 6 | (k & 0x3F) << 0));
/*      */ 
/* 3197 */         return 3;
/*      */       case 8:
/*      */       case 9:
/*      */       case 10:
/* 3200 */       case 11: } throw new UTFDataFormatException();
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class Caches
/*      */   {
/*  232 */     static final ConcurrentMap<ObjectStreamClass.WeakClassKey, Boolean> subclassAudits = new ConcurrentHashMap();
/*      */ 
/*  236 */     static final ReferenceQueue<Class<?>> subclassAuditsQueue = new ReferenceQueue();
/*      */   }
/*      */ 
/*      */   public static abstract class GetField
/*      */   {
/*      */     public abstract ObjectStreamClass getObjectStreamClass();
/*      */ 
/*      */     public abstract boolean defaulted(String paramString)
/*      */       throws IOException;
/*      */ 
/*      */     public abstract boolean get(String paramString, boolean paramBoolean)
/*      */       throws IOException;
/*      */ 
/*      */     public abstract byte get(String paramString, byte paramByte)
/*      */       throws IOException;
/*      */ 
/*      */     public abstract char get(String paramString, char paramChar)
/*      */       throws IOException;
/*      */ 
/*      */     public abstract short get(String paramString, short paramShort)
/*      */       throws IOException;
/*      */ 
/*      */     public abstract int get(String paramString, int paramInt)
/*      */       throws IOException;
/*      */ 
/*      */     public abstract long get(String paramString, long paramLong)
/*      */       throws IOException;
/*      */ 
/*      */     public abstract float get(String paramString, float paramFloat)
/*      */       throws IOException;
/*      */ 
/*      */     public abstract double get(String paramString, double paramDouble)
/*      */       throws IOException;
/*      */ 
/*      */     public abstract Object get(String paramString, Object paramObject)
/*      */       throws IOException;
/*      */   }
/*      */ 
/*      */   private class GetFieldImpl extends ObjectInputStream.GetField
/*      */   {
/*      */     private final ObjectStreamClass desc;
/*      */     private final byte[] primVals;
/*      */     private final Object[] objVals;
/*      */     private final int[] objHandles;
/*      */ 
/*      */     GetFieldImpl(ObjectStreamClass arg2)
/*      */     {
/*      */       Object localObject;
/* 2075 */       this.desc = localObject;
/* 2076 */       this.primVals = new byte[localObject.getPrimDataSize()];
/* 2077 */       this.objVals = new Object[localObject.getNumObjFields()];
/* 2078 */       this.objHandles = new int[this.objVals.length];
/*      */     }
/*      */ 
/*      */     public ObjectStreamClass getObjectStreamClass() {
/* 2082 */       return this.desc;
/*      */     }
/*      */ 
/*      */     public boolean defaulted(String paramString) throws IOException {
/* 2086 */       return getFieldOffset(paramString, null) < 0;
/*      */     }
/*      */ 
/*      */     public boolean get(String paramString, boolean paramBoolean) throws IOException {
/* 2090 */       int i = getFieldOffset(paramString, Boolean.TYPE);
/* 2091 */       return i >= 0 ? Bits.getBoolean(this.primVals, i) : paramBoolean;
/*      */     }
/*      */ 
/*      */     public byte get(String paramString, byte paramByte) throws IOException {
/* 2095 */       int i = getFieldOffset(paramString, Byte.TYPE);
/* 2096 */       return i >= 0 ? this.primVals[i] : paramByte;
/*      */     }
/*      */ 
/*      */     public char get(String paramString, char paramChar) throws IOException {
/* 2100 */       int i = getFieldOffset(paramString, Character.TYPE);
/* 2101 */       return i >= 0 ? Bits.getChar(this.primVals, i) : paramChar;
/*      */     }
/*      */ 
/*      */     public short get(String paramString, short paramShort) throws IOException {
/* 2105 */       int i = getFieldOffset(paramString, Short.TYPE);
/* 2106 */       return i >= 0 ? Bits.getShort(this.primVals, i) : paramShort;
/*      */     }
/*      */ 
/*      */     public int get(String paramString, int paramInt) throws IOException {
/* 2110 */       int i = getFieldOffset(paramString, Integer.TYPE);
/* 2111 */       return i >= 0 ? Bits.getInt(this.primVals, i) : paramInt;
/*      */     }
/*      */ 
/*      */     public float get(String paramString, float paramFloat) throws IOException {
/* 2115 */       int i = getFieldOffset(paramString, Float.TYPE);
/* 2116 */       return i >= 0 ? Bits.getFloat(this.primVals, i) : paramFloat;
/*      */     }
/*      */ 
/*      */     public long get(String paramString, long paramLong) throws IOException {
/* 2120 */       int i = getFieldOffset(paramString, Long.TYPE);
/* 2121 */       return i >= 0 ? Bits.getLong(this.primVals, i) : paramLong;
/*      */     }
/*      */ 
/*      */     public double get(String paramString, double paramDouble) throws IOException {
/* 2125 */       int i = getFieldOffset(paramString, Double.TYPE);
/* 2126 */       return i >= 0 ? Bits.getDouble(this.primVals, i) : paramDouble;
/*      */     }
/*      */ 
/*      */     public Object get(String paramString, Object paramObject) throws IOException {
/* 2130 */       int i = getFieldOffset(paramString, Object.class);
/* 2131 */       if (i >= 0) {
/* 2132 */         int j = this.objHandles[i];
/* 2133 */         ObjectInputStream.this.handles.markDependency(ObjectInputStream.this.passHandle, j);
/* 2134 */         return ObjectInputStream.this.handles.lookupException(j) == null ? this.objVals[i] : null;
/*      */       }
/*      */ 
/* 2137 */       return paramObject;
/*      */     }
/*      */ 
/*      */     void readFields()
/*      */       throws IOException
/*      */     {
/* 2145 */       ObjectInputStream.this.bin.readFully(this.primVals, 0, this.primVals.length, false);
/*      */ 
/* 2147 */       int i = ObjectInputStream.this.passHandle;
/* 2148 */       ObjectStreamField[] arrayOfObjectStreamField = this.desc.getFields(false);
/* 2149 */       int j = arrayOfObjectStreamField.length - this.objVals.length;
/* 2150 */       for (int k = 0; k < this.objVals.length; k++) {
/* 2151 */         this.objVals[k] = ObjectInputStream.this.readObject0(arrayOfObjectStreamField[(j + k)].isUnshared());
/*      */ 
/* 2153 */         this.objHandles[k] = ObjectInputStream.this.passHandle;
/*      */       }
/* 2155 */       ObjectInputStream.this.passHandle = i;
/*      */     }
/*      */ 
/*      */     private int getFieldOffset(String paramString, Class paramClass)
/*      */     {
/* 2168 */       ObjectStreamField localObjectStreamField = this.desc.getField(paramString, paramClass);
/* 2169 */       if (localObjectStreamField != null)
/* 2170 */         return localObjectStreamField.getOffset();
/* 2171 */       if (this.desc.getLocalDesc().getField(paramString, paramClass) != null) {
/* 2172 */         return -1;
/*      */       }
/* 2174 */       throw new IllegalArgumentException("no such field " + paramString + " with type " + paramClass);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class HandleTable
/*      */   {
/*      */     private static final byte STATUS_OK = 1;
/*      */     private static final byte STATUS_UNKNOWN = 2;
/*      */     private static final byte STATUS_EXCEPTION = 3;
/*      */     byte[] status;
/*      */     Object[] entries;
/*      */     HandleList[] deps;
/* 3248 */     int lowDep = -1;
/*      */ 
/* 3250 */     int size = 0;
/*      */ 
/*      */     HandleTable(int paramInt)
/*      */     {
/* 3256 */       this.status = new byte[paramInt];
/* 3257 */       this.entries = new Object[paramInt];
/* 3258 */       this.deps = new HandleList[paramInt];
/*      */     }
/*      */ 
/*      */     int assign(Object paramObject)
/*      */     {
/* 3268 */       if (this.size >= this.entries.length) {
/* 3269 */         grow();
/*      */       }
/* 3271 */       this.status[this.size] = 2;
/* 3272 */       this.entries[this.size] = paramObject;
/* 3273 */       return this.size++;
/*      */     }
/*      */ 
/*      */     void markDependency(int paramInt1, int paramInt2)
/*      */     {
/* 3283 */       if ((paramInt1 == -1) || (paramInt2 == -1)) {
/* 3284 */         return;
/*      */       }
/* 3286 */       switch (this.status[paramInt1])
/*      */       {
/*      */       case 2:
/* 3289 */         switch (this.status[paramInt2])
/*      */         {
/*      */         case 1:
/* 3292 */           break;
/*      */         case 3:
/* 3296 */           markException(paramInt1, (ClassNotFoundException)this.entries[paramInt2]);
/*      */ 
/* 3298 */           break;
/*      */         case 2:
/* 3302 */           if (this.deps[paramInt2] == null) {
/* 3303 */             this.deps[paramInt2] = new HandleList();
/*      */           }
/* 3305 */           this.deps[paramInt2].add(paramInt1);
/*      */ 
/* 3308 */           if ((this.lowDep < 0) || (this.lowDep > paramInt2))
/* 3309 */             this.lowDep = paramInt2; break;
/*      */         default:
/* 3314 */           throw new InternalError();
/*      */         }
/*      */ 
/*      */         break;
/*      */       case 3:
/* 3319 */         break;
/*      */       default:
/* 3322 */         throw new InternalError();
/*      */       }
/*      */     }
/*      */ 
/*      */     void markException(int paramInt, ClassNotFoundException paramClassNotFoundException)
/*      */     {
/* 3333 */       switch (this.status[paramInt]) {
/*      */       case 2:
/* 3335 */         this.status[paramInt] = 3;
/* 3336 */         this.entries[paramInt] = paramClassNotFoundException;
/*      */ 
/* 3339 */         HandleList localHandleList = this.deps[paramInt];
/* 3340 */         if (localHandleList != null) {
/* 3341 */           int i = localHandleList.size();
/* 3342 */           for (int j = 0; j < i; j++) {
/* 3343 */             markException(localHandleList.get(j), paramClassNotFoundException);
/*      */           }
/* 3345 */           this.deps[paramInt] = null;
/* 3346 */         }break;
/*      */       case 3:
/* 3350 */         break;
/*      */       default:
/* 3353 */         throw new InternalError();
/*      */       }
/*      */     }
/*      */ 
/*      */     void finish(int paramInt)
/*      */     {
/*      */       int i;
/* 3364 */       if (this.lowDep < 0)
/*      */       {
/* 3366 */         i = paramInt + 1;
/* 3367 */       } else if (this.lowDep >= paramInt)
/*      */       {
/* 3369 */         i = this.size;
/* 3370 */         this.lowDep = -1;
/*      */       }
/*      */       else {
/* 3373 */         return;
/*      */       }
/*      */ 
/* 3377 */       for (int j = paramInt; j < i; j++)
/* 3378 */         switch (this.status[j]) {
/*      */         case 2:
/* 3380 */           this.status[j] = 1;
/* 3381 */           this.deps[j] = null;
/* 3382 */           break;
/*      */         case 1:
/*      */         case 3:
/* 3386 */           break;
/*      */         default:
/* 3389 */           throw new InternalError();
/*      */         }
/*      */     }
/*      */ 
/*      */     void setObject(int paramInt, Object paramObject)
/*      */     {
/* 3401 */       switch (this.status[paramInt]) {
/*      */       case 1:
/*      */       case 2:
/* 3404 */         this.entries[paramInt] = paramObject;
/* 3405 */         break;
/*      */       case 3:
/* 3408 */         break;
/*      */       default:
/* 3411 */         throw new InternalError();
/*      */       }
/*      */     }
/*      */ 
/*      */     Object lookupObject(int paramInt)
/*      */     {
/* 3421 */       return (paramInt != -1) && (this.status[paramInt] != 3) ? this.entries[paramInt] : null;
/*      */     }
/*      */ 
/*      */     ClassNotFoundException lookupException(int paramInt)
/*      */     {
/* 3432 */       return (paramInt != -1) && (this.status[paramInt] == 3) ? (ClassNotFoundException)this.entries[paramInt] : null;
/*      */     }
/*      */ 
/*      */     void clear()
/*      */     {
/* 3441 */       Arrays.fill(this.status, 0, this.size, (byte)0);
/* 3442 */       Arrays.fill(this.entries, 0, this.size, null);
/* 3443 */       Arrays.fill(this.deps, 0, this.size, null);
/* 3444 */       this.lowDep = -1;
/* 3445 */       this.size = 0;
/*      */     }
/*      */ 
/*      */     int size()
/*      */     {
/* 3452 */       return this.size;
/*      */     }
/*      */ 
/*      */     private void grow()
/*      */     {
/* 3459 */       int i = (this.entries.length << 1) + 1;
/*      */ 
/* 3461 */       byte[] arrayOfByte = new byte[i];
/* 3462 */       Object[] arrayOfObject = new Object[i];
/* 3463 */       HandleList[] arrayOfHandleList = new HandleList[i];
/*      */ 
/* 3465 */       System.arraycopy(this.status, 0, arrayOfByte, 0, this.size);
/* 3466 */       System.arraycopy(this.entries, 0, arrayOfObject, 0, this.size);
/* 3467 */       System.arraycopy(this.deps, 0, arrayOfHandleList, 0, this.size);
/*      */ 
/* 3469 */       this.status = arrayOfByte;
/* 3470 */       this.entries = arrayOfObject;
/* 3471 */       this.deps = arrayOfHandleList;
/*      */     }
/*      */ 
/*      */     private static class HandleList
/*      */     {
/* 3478 */       private int[] list = new int[4];
/* 3479 */       private int size = 0;
/*      */ 
/*      */       public void add(int paramInt)
/*      */       {
/* 3485 */         if (this.size >= this.list.length) {
/* 3486 */           int[] arrayOfInt = new int[this.list.length << 1];
/* 3487 */           System.arraycopy(this.list, 0, arrayOfInt, 0, this.list.length);
/* 3488 */           this.list = arrayOfInt;
/*      */         }
/* 3490 */         this.list[(this.size++)] = paramInt;
/*      */       }
/*      */ 
/*      */       public int get(int paramInt) {
/* 3494 */         if (paramInt >= this.size) {
/* 3495 */           throw new ArrayIndexOutOfBoundsException();
/*      */         }
/* 3497 */         return this.list[paramInt];
/*      */       }
/*      */ 
/*      */       public int size() {
/* 3501 */         return this.size;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class PeekInputStream extends InputStream
/*      */   {
/*      */     private final InputStream in;
/* 2277 */     private int peekb = -1;
/*      */ 
/*      */     PeekInputStream(InputStream paramInputStream)
/*      */     {
/* 2283 */       this.in = paramInputStream;
/*      */     }
/*      */ 
/*      */     int peek()
/*      */       throws IOException
/*      */     {
/* 2291 */       return this.peekb = this.in.read();
/*      */     }
/*      */ 
/*      */     public int read() throws IOException {
/* 2295 */       if (this.peekb >= 0) {
/* 2296 */         int i = this.peekb;
/* 2297 */         this.peekb = -1;
/* 2298 */         return i;
/*      */       }
/* 2300 */       return this.in.read();
/*      */     }
/*      */ 
/*      */     public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException
/*      */     {
/* 2305 */       if (paramInt2 == 0)
/* 2306 */         return 0;
/* 2307 */       if (this.peekb < 0) {
/* 2308 */         return this.in.read(paramArrayOfByte, paramInt1, paramInt2);
/*      */       }
/* 2310 */       paramArrayOfByte[(paramInt1++)] = ((byte)this.peekb);
/* 2311 */       paramInt2--;
/* 2312 */       this.peekb = -1;
/* 2313 */       int i = this.in.read(paramArrayOfByte, paramInt1, paramInt2);
/* 2314 */       return i >= 0 ? i + 1 : 1;
/*      */     }
/*      */ 
/*      */     void readFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException
/*      */     {
/* 2319 */       int i = 0;
/* 2320 */       while (i < paramInt2) {
/* 2321 */         int j = read(paramArrayOfByte, paramInt1 + i, paramInt2 - i);
/* 2322 */         if (j < 0) {
/* 2323 */           throw new EOFException();
/*      */         }
/* 2325 */         i += j;
/*      */       }
/*      */     }
/*      */ 
/*      */     public long skip(long paramLong) throws IOException {
/* 2330 */       if (paramLong <= 0L) {
/* 2331 */         return 0L;
/*      */       }
/* 2333 */       int i = 0;
/* 2334 */       if (this.peekb >= 0) {
/* 2335 */         this.peekb = -1;
/* 2336 */         i++;
/* 2337 */         paramLong -= 1L;
/*      */       }
/* 2339 */       return i + skip(paramLong);
/*      */     }
/*      */ 
/*      */     public int available() throws IOException {
/* 2343 */       return this.in.available() + (this.peekb >= 0 ? 1 : 0);
/*      */     }
/*      */ 
/*      */     public void close() throws IOException {
/* 2347 */       this.in.close();
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class ValidationList
/*      */   {
/*      */     private Callback list;
/*      */ 
/*      */     void register(ObjectInputValidation paramObjectInputValidation, int paramInt)
/*      */       throws InvalidObjectException
/*      */     {
/* 2218 */       if (paramObjectInputValidation == null) {
/* 2219 */         throw new InvalidObjectException("null callback");
/*      */       }
/*      */ 
/* 2222 */       Object localObject = null; Callback localCallback = this.list;
/* 2223 */       while ((localCallback != null) && (paramInt < localCallback.priority)) {
/* 2224 */         localObject = localCallback;
/* 2225 */         localCallback = localCallback.next;
/*      */       }
/* 2227 */       AccessControlContext localAccessControlContext = AccessController.getContext();
/* 2228 */       if (localObject != null)
/* 2229 */         localObject.next = new Callback(paramObjectInputValidation, paramInt, localCallback, localAccessControlContext);
/*      */       else
/* 2231 */         this.list = new Callback(paramObjectInputValidation, paramInt, this.list, localAccessControlContext);
/*      */     }
/*      */ 
/*      */     void doCallbacks()
/*      */       throws InvalidObjectException
/*      */     {
/*      */       try
/*      */       {
/* 2244 */         while (this.list != null) {
/* 2245 */           AccessController.doPrivileged(new PrivilegedExceptionAction()
/*      */           {
/*      */             public Void run() throws InvalidObjectException
/*      */             {
/* 2249 */               ObjectInputStream.ValidationList.this.list.obj.validateObject();
/* 2250 */               return null;
/*      */             }
/*      */           }
/*      */           , this.list.acc);
/*      */ 
/* 2253 */           this.list = this.list.next;
/*      */         }
/*      */       } catch (PrivilegedActionException localPrivilegedActionException) {
/* 2256 */         this.list = null;
/* 2257 */         throw ((InvalidObjectException)localPrivilegedActionException.getException());
/*      */       }
/*      */     }
/*      */ 
/*      */     public void clear()
/*      */     {
/* 2265 */       this.list = null;
/*      */     }
/*      */ 
/*      */     private static class Callback
/*      */     {
/*      */       final ObjectInputValidation obj;
/*      */       final int priority;
/*      */       Callback next;
/*      */       final AccessControlContext acc;
/*      */ 
/*      */       Callback(ObjectInputValidation paramObjectInputValidation, int paramInt, Callback paramCallback, AccessControlContext paramAccessControlContext)
/*      */       {
/* 2195 */         this.obj = paramObjectInputValidation;
/* 2196 */         this.priority = paramInt;
/* 2197 */         this.next = paramCallback;
/* 2198 */         this.acc = paramAccessControlContext;
/*      */       }
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.io.ObjectInputStream
 * JD-Core Version:    0.6.2
 */