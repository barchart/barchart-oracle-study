/*      */ package java.io;
/*      */ 
/*      */ import java.lang.ref.ReferenceQueue;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.List;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ import java.util.concurrent.ConcurrentMap;
/*      */ import sun.security.action.GetBooleanAction;
/*      */ 
/*      */ public class ObjectOutputStream extends OutputStream
/*      */   implements ObjectOutput, ObjectStreamConstants
/*      */ {
/*      */   private final BlockDataOutputStream bout;
/*      */   private final HandleTable handles;
/*      */   private final ReplaceTable subs;
/*  182 */   private int protocol = 2;
/*      */   private int depth;
/*      */   private byte[] primVals;
/*      */   private final boolean enableOverride;
/*      */   private boolean enableReplace;
/*      */   private SerialCallbackContext curContext;
/*      */   private PutFieldImpl curPut;
/*      */   private final DebugTraceInfoStack debugInfoStack;
/*  211 */   private static final boolean extendedDebugInfo = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.io.serialization.extendedDebugInfo"))).booleanValue();
/*      */ 
/*      */   public ObjectOutputStream(OutputStream paramOutputStream)
/*      */     throws IOException
/*      */   {
/*  240 */     verifySubclass();
/*  241 */     this.bout = new BlockDataOutputStream(paramOutputStream);
/*  242 */     this.handles = new HandleTable(10, 3.0F);
/*  243 */     this.subs = new ReplaceTable(10, 3.0F);
/*  244 */     this.enableOverride = false;
/*  245 */     writeStreamHeader();
/*  246 */     this.bout.setBlockDataMode(true);
/*  247 */     if (extendedDebugInfo)
/*  248 */       this.debugInfoStack = new DebugTraceInfoStack();
/*      */     else
/*  250 */       this.debugInfoStack = null;
/*      */   }
/*      */ 
/*      */   protected ObjectOutputStream()
/*      */     throws IOException, SecurityException
/*      */   {
/*  271 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  272 */     if (localSecurityManager != null) {
/*  273 */       localSecurityManager.checkPermission(SUBCLASS_IMPLEMENTATION_PERMISSION);
/*      */     }
/*  275 */     this.bout = null;
/*  276 */     this.handles = null;
/*  277 */     this.subs = null;
/*  278 */     this.enableOverride = true;
/*  279 */     this.debugInfoStack = null;
/*      */   }
/*      */ 
/*      */   public void useProtocolVersion(int paramInt)
/*      */     throws IOException
/*      */   {
/*  303 */     if (this.handles.size() != 0)
/*      */     {
/*  305 */       throw new IllegalStateException("stream non-empty");
/*      */     }
/*  307 */     switch (paramInt) {
/*      */     case 1:
/*      */     case 2:
/*  310 */       this.protocol = paramInt;
/*  311 */       break;
/*      */     default:
/*  314 */       throw new IllegalArgumentException("unknown version: " + paramInt);
/*      */     }
/*      */   }
/*      */ 
/*      */   public final void writeObject(Object paramObject)
/*      */     throws IOException
/*      */   {
/*  341 */     if (this.enableOverride) {
/*  342 */       writeObjectOverride(paramObject);
/*  343 */       return;
/*      */     }
/*      */     try {
/*  346 */       writeObject0(paramObject, false);
/*      */     } catch (IOException localIOException) {
/*  348 */       if (this.depth == 0) {
/*  349 */         writeFatalException(localIOException);
/*      */       }
/*  351 */       throw localIOException;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void writeObjectOverride(Object paramObject)
/*      */     throws IOException
/*      */   {
/*      */   }
/*      */ 
/*      */   public void writeUnshared(Object paramObject)
/*      */     throws IOException
/*      */   {
/*      */     try
/*      */     {
/*  413 */       writeObject0(paramObject, true);
/*      */     } catch (IOException localIOException) {
/*  415 */       if (this.depth == 0) {
/*  416 */         writeFatalException(localIOException);
/*      */       }
/*  418 */       throw localIOException;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void defaultWriteObject()
/*      */     throws IOException
/*      */   {
/*  432 */     if (this.curContext == null) {
/*  433 */       throw new NotActiveException("not in call to writeObject");
/*      */     }
/*  435 */     Object localObject = this.curContext.getObj();
/*  436 */     ObjectStreamClass localObjectStreamClass = this.curContext.getDesc();
/*  437 */     this.bout.setBlockDataMode(false);
/*  438 */     defaultWriteFields(localObject, localObjectStreamClass);
/*  439 */     this.bout.setBlockDataMode(true);
/*      */   }
/*      */ 
/*      */   public PutField putFields()
/*      */     throws IOException
/*      */   {
/*  453 */     if (this.curPut == null) {
/*  454 */       if (this.curContext == null) {
/*  455 */         throw new NotActiveException("not in call to writeObject");
/*      */       }
/*  457 */       Object localObject = this.curContext.getObj();
/*  458 */       ObjectStreamClass localObjectStreamClass = this.curContext.getDesc();
/*  459 */       this.curPut = new PutFieldImpl(localObjectStreamClass);
/*      */     }
/*  461 */     return this.curPut;
/*      */   }
/*      */ 
/*      */   public void writeFields()
/*      */     throws IOException
/*      */   {
/*  474 */     if (this.curPut == null) {
/*  475 */       throw new NotActiveException("no current PutField object");
/*      */     }
/*  477 */     this.bout.setBlockDataMode(false);
/*  478 */     this.curPut.writeFields();
/*  479 */     this.bout.setBlockDataMode(true);
/*      */   }
/*      */ 
/*      */   public void reset()
/*      */     throws IOException
/*      */   {
/*  493 */     if (this.depth != 0) {
/*  494 */       throw new IOException("stream active");
/*      */     }
/*  496 */     this.bout.setBlockDataMode(false);
/*  497 */     this.bout.writeByte(121);
/*  498 */     clear();
/*  499 */     this.bout.setBlockDataMode(true);
/*      */   }
/*      */ 
/*      */   protected void annotateClass(Class<?> paramClass)
/*      */     throws IOException
/*      */   {
/*      */   }
/*      */ 
/*      */   protected void annotateProxyClass(Class<?> paramClass)
/*      */     throws IOException
/*      */   {
/*      */   }
/*      */ 
/*      */   protected Object replaceObject(Object paramObject)
/*      */     throws IOException
/*      */   {
/*  584 */     return paramObject;
/*      */   }
/*      */ 
/*      */   protected boolean enableReplaceObject(boolean paramBoolean)
/*      */     throws SecurityException
/*      */   {
/*  610 */     if (paramBoolean == this.enableReplace) {
/*  611 */       return paramBoolean;
/*      */     }
/*  613 */     if (paramBoolean) {
/*  614 */       SecurityManager localSecurityManager = System.getSecurityManager();
/*  615 */       if (localSecurityManager != null) {
/*  616 */         localSecurityManager.checkPermission(SUBSTITUTION_PERMISSION);
/*      */       }
/*      */     }
/*  619 */     this.enableReplace = paramBoolean;
/*  620 */     return !this.enableReplace;
/*      */   }
/*      */ 
/*      */   protected void writeStreamHeader()
/*      */     throws IOException
/*      */   {
/*  632 */     this.bout.writeShort(-21267);
/*  633 */     this.bout.writeShort(5);
/*      */   }
/*      */ 
/*      */   protected void writeClassDescriptor(ObjectStreamClass paramObjectStreamClass)
/*      */     throws IOException
/*      */   {
/*  664 */     paramObjectStreamClass.writeNonProxy(this);
/*      */   }
/*      */ 
/*      */   public void write(int paramInt)
/*      */     throws IOException
/*      */   {
/*  675 */     this.bout.write(paramInt);
/*      */   }
/*      */ 
/*      */   public void write(byte[] paramArrayOfByte)
/*      */     throws IOException
/*      */   {
/*  686 */     this.bout.write(paramArrayOfByte, 0, paramArrayOfByte.length, false);
/*      */   }
/*      */ 
/*      */   public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*      */     throws IOException
/*      */   {
/*  698 */     if (paramArrayOfByte == null) {
/*  699 */       throw new NullPointerException();
/*      */     }
/*  701 */     int i = paramInt1 + paramInt2;
/*  702 */     if ((paramInt1 < 0) || (paramInt2 < 0) || (i > paramArrayOfByte.length) || (i < 0)) {
/*  703 */       throw new IndexOutOfBoundsException();
/*      */     }
/*  705 */     this.bout.write(paramArrayOfByte, paramInt1, paramInt2, false);
/*      */   }
/*      */ 
/*      */   public void flush()
/*      */     throws IOException
/*      */   {
/*  715 */     this.bout.flush();
/*      */   }
/*      */ 
/*      */   protected void drain()
/*      */     throws IOException
/*      */   {
/*  726 */     this.bout.drain();
/*      */   }
/*      */ 
/*      */   public void close()
/*      */     throws IOException
/*      */   {
/*  736 */     flush();
/*  737 */     clear();
/*  738 */     this.bout.close();
/*      */   }
/*      */ 
/*      */   public void writeBoolean(boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/*  749 */     this.bout.writeBoolean(paramBoolean);
/*      */   }
/*      */ 
/*      */   public void writeByte(int paramInt)
/*      */     throws IOException
/*      */   {
/*  760 */     this.bout.writeByte(paramInt);
/*      */   }
/*      */ 
/*      */   public void writeShort(int paramInt)
/*      */     throws IOException
/*      */   {
/*  771 */     this.bout.writeShort(paramInt);
/*      */   }
/*      */ 
/*      */   public void writeChar(int paramInt)
/*      */     throws IOException
/*      */   {
/*  782 */     this.bout.writeChar(paramInt);
/*      */   }
/*      */ 
/*      */   public void writeInt(int paramInt)
/*      */     throws IOException
/*      */   {
/*  793 */     this.bout.writeInt(paramInt);
/*      */   }
/*      */ 
/*      */   public void writeLong(long paramLong)
/*      */     throws IOException
/*      */   {
/*  804 */     this.bout.writeLong(paramLong);
/*      */   }
/*      */ 
/*      */   public void writeFloat(float paramFloat)
/*      */     throws IOException
/*      */   {
/*  815 */     this.bout.writeFloat(paramFloat);
/*      */   }
/*      */ 
/*      */   public void writeDouble(double paramDouble)
/*      */     throws IOException
/*      */   {
/*  826 */     this.bout.writeDouble(paramDouble);
/*      */   }
/*      */ 
/*      */   public void writeBytes(String paramString)
/*      */     throws IOException
/*      */   {
/*  837 */     this.bout.writeBytes(paramString);
/*      */   }
/*      */ 
/*      */   public void writeChars(String paramString)
/*      */     throws IOException
/*      */   {
/*  848 */     this.bout.writeChars(paramString);
/*      */   }
/*      */ 
/*      */   public void writeUTF(String paramString)
/*      */     throws IOException
/*      */   {
/*  865 */     this.bout.writeUTF(paramString);
/*      */   }
/*      */ 
/*      */   int getProtocolVersion()
/*      */   {
/* 1012 */     return this.protocol;
/*      */   }
/*      */ 
/*      */   void writeTypeString(String paramString)
/*      */     throws IOException
/*      */   {
/* 1021 */     if (paramString == null) {
/* 1022 */       writeNull();
/*      */     }
/*      */     else
/*      */     {
/*      */       int i;
/* 1023 */       if ((i = this.handles.lookup(paramString)) != -1)
/* 1024 */         writeHandle(i);
/*      */       else
/* 1026 */         writeString(paramString, false);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void verifySubclass()
/*      */   {
/* 1037 */     Class localClass = getClass();
/* 1038 */     if (localClass == ObjectOutputStream.class) {
/* 1039 */       return;
/*      */     }
/* 1041 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 1042 */     if (localSecurityManager == null) {
/* 1043 */       return;
/*      */     }
/* 1045 */     ObjectStreamClass.processQueue(Caches.subclassAuditsQueue, Caches.subclassAudits);
/* 1046 */     ObjectStreamClass.WeakClassKey localWeakClassKey = new ObjectStreamClass.WeakClassKey(localClass, Caches.subclassAuditsQueue);
/* 1047 */     Boolean localBoolean = (Boolean)Caches.subclassAudits.get(localWeakClassKey);
/* 1048 */     if (localBoolean == null) {
/* 1049 */       localBoolean = Boolean.valueOf(auditSubclass(localClass));
/* 1050 */       Caches.subclassAudits.putIfAbsent(localWeakClassKey, localBoolean);
/*      */     }
/* 1052 */     if (localBoolean.booleanValue()) {
/* 1053 */       return;
/*      */     }
/* 1055 */     localSecurityManager.checkPermission(SUBCLASS_IMPLEMENTATION_PERMISSION);
/*      */   }
/*      */ 
/*      */   private static boolean auditSubclass(Class paramClass)
/*      */   {
/* 1064 */     Boolean localBoolean = (Boolean)AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public Boolean run() {
/* 1067 */         for (Class localClass = this.val$subcl; 
/* 1068 */           localClass != ObjectOutputStream.class; 
/* 1069 */           localClass = localClass.getSuperclass())
/*      */           try
/*      */           {
/* 1072 */             localClass.getDeclaredMethod("writeUnshared", new Class[] { Object.class });
/*      */ 
/* 1074 */             return Boolean.FALSE;
/*      */           }
/*      */           catch (NoSuchMethodException localNoSuchMethodException1) {
/*      */             try {
/* 1078 */               localClass.getDeclaredMethod("putFields", (Class[])null);
/* 1079 */               return Boolean.FALSE;
/*      */             } catch (NoSuchMethodException localNoSuchMethodException2) {
/*      */             }
/*      */           }
/* 1083 */         return Boolean.TRUE;
/*      */       }
/*      */     });
/* 1087 */     return localBoolean.booleanValue();
/*      */   }
/*      */ 
/*      */   private void clear()
/*      */   {
/* 1094 */     this.subs.clear();
/* 1095 */     this.handles.clear();
/*      */   }
/*      */ 
/*      */   private void writeObject0(Object paramObject, boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/* 1104 */     boolean bool = this.bout.setBlockDataMode(false);
/* 1105 */     this.depth += 1;
/*      */     try
/*      */     {
/* 1109 */       if ((paramObject = this.subs.lookup(paramObject)) == null) {
/* 1110 */         writeNull();
/*      */       }
/*      */       else
/*      */       {
/*      */         int i;
/* 1112 */         if ((!paramBoolean) && ((i = this.handles.lookup(paramObject)) != -1)) {
/* 1113 */           writeHandle(i);
/*      */         }
/* 1115 */         else if ((paramObject instanceof Class)) {
/* 1116 */           writeClass((Class)paramObject, paramBoolean);
/*      */         }
/* 1118 */         else if ((paramObject instanceof ObjectStreamClass)) {
/* 1119 */           writeClassDesc((ObjectStreamClass)paramObject, paramBoolean);
/*      */         }
/*      */         else {
/* 1124 */           Object localObject1 = paramObject;
/* 1125 */           Object localObject2 = paramObject.getClass();
/*      */           ObjectStreamClass localObjectStreamClass;
/*      */           Object localObject3;
/*      */           while (true) {
/* 1130 */             localObjectStreamClass = ObjectStreamClass.lookup((Class)localObject2, true);
/* 1131 */             if ((!localObjectStreamClass.hasWriteReplaceMethod()) || ((paramObject = localObjectStreamClass.invokeWriteReplace(paramObject)) == null) || ((localObject3 = paramObject.getClass()) == localObject2))
/*      */             {
/*      */               break;
/*      */             }
/*      */ 
/* 1137 */             localObject2 = localObject3;
/*      */           }
/* 1139 */           if (this.enableReplace) {
/* 1140 */             localObject3 = replaceObject(paramObject);
/* 1141 */             if ((localObject3 != paramObject) && (localObject3 != null)) {
/* 1142 */               localObject2 = localObject3.getClass();
/* 1143 */               localObjectStreamClass = ObjectStreamClass.lookup((Class)localObject2, true);
/*      */             }
/* 1145 */             paramObject = localObject3;
/*      */           }
/*      */ 
/* 1149 */           if (paramObject != localObject1) {
/* 1150 */             this.subs.assign(localObject1, paramObject);
/* 1151 */             if (paramObject == null) { writeNull();
/*      */               return; }
/* 1154 */             if ((!paramBoolean) && ((i = this.handles.lookup(paramObject)) != -1)) { writeHandle(i);
/*      */               return; }
/* 1157 */             if ((paramObject instanceof Class)) { writeClass((Class)paramObject, paramBoolean);
/*      */               return; }
/* 1160 */             if ((paramObject instanceof ObjectStreamClass))
/*      */             {
/* 1161 */               writeClassDesc((ObjectStreamClass)paramObject, paramBoolean);
/*      */               return;
/*      */             }
/*      */           }
/* 1167 */           if ((paramObject instanceof String)) {
/* 1168 */             writeString((String)paramObject, paramBoolean);
/* 1169 */           } else if (((Class)localObject2).isArray()) {
/* 1170 */             writeArray(paramObject, localObjectStreamClass, paramBoolean);
/* 1171 */           } else if ((paramObject instanceof Enum)) {
/* 1172 */             writeEnum((Enum)paramObject, localObjectStreamClass, paramBoolean);
/* 1173 */           } else if ((paramObject instanceof Serializable)) {
/* 1174 */             writeOrdinaryObject(paramObject, localObjectStreamClass, paramBoolean);
/*      */           } else {
/* 1176 */             if (extendedDebugInfo) {
/* 1177 */               throw new NotSerializableException(((Class)localObject2).getName() + "\n" + this.debugInfoStack.toString());
/*      */             }
/*      */ 
/* 1180 */             throw new NotSerializableException(((Class)localObject2).getName());
/*      */           }
/*      */         }
/*      */       }
/*      */     } finally { this.depth -= 1;
/* 1185 */       this.bout.setBlockDataMode(bool);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void writeNull()
/*      */     throws IOException
/*      */   {
/* 1193 */     this.bout.writeByte(112);
/*      */   }
/*      */ 
/*      */   private void writeHandle(int paramInt)
/*      */     throws IOException
/*      */   {
/* 1200 */     this.bout.writeByte(113);
/* 1201 */     this.bout.writeInt(8257536 + paramInt);
/*      */   }
/*      */ 
/*      */   private void writeClass(Class paramClass, boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/* 1208 */     this.bout.writeByte(118);
/* 1209 */     writeClassDesc(ObjectStreamClass.lookup(paramClass, true), false);
/* 1210 */     this.handles.assign(paramBoolean ? null : paramClass);
/*      */   }
/*      */ 
/*      */   private void writeClassDesc(ObjectStreamClass paramObjectStreamClass, boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/* 1220 */     if (paramObjectStreamClass == null) {
/* 1221 */       writeNull();
/*      */     }
/*      */     else
/*      */     {
/*      */       int i;
/* 1222 */       if ((!paramBoolean) && ((i = this.handles.lookup(paramObjectStreamClass)) != -1))
/* 1223 */         writeHandle(i);
/* 1224 */       else if (paramObjectStreamClass.isProxy())
/* 1225 */         writeProxyDesc(paramObjectStreamClass, paramBoolean);
/*      */       else
/* 1227 */         writeNonProxyDesc(paramObjectStreamClass, paramBoolean);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void writeProxyDesc(ObjectStreamClass paramObjectStreamClass, boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/* 1237 */     this.bout.writeByte(125);
/* 1238 */     this.handles.assign(paramBoolean ? null : paramObjectStreamClass);
/*      */ 
/* 1240 */     Class localClass = paramObjectStreamClass.forClass();
/* 1241 */     Class[] arrayOfClass = localClass.getInterfaces();
/* 1242 */     this.bout.writeInt(arrayOfClass.length);
/* 1243 */     for (int i = 0; i < arrayOfClass.length; i++) {
/* 1244 */       this.bout.writeUTF(arrayOfClass[i].getName());
/*      */     }
/*      */ 
/* 1247 */     this.bout.setBlockDataMode(true);
/* 1248 */     annotateProxyClass(localClass);
/* 1249 */     this.bout.setBlockDataMode(false);
/* 1250 */     this.bout.writeByte(120);
/*      */ 
/* 1252 */     writeClassDesc(paramObjectStreamClass.getSuperDesc(), false);
/*      */   }
/*      */ 
/*      */   private void writeNonProxyDesc(ObjectStreamClass paramObjectStreamClass, boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/* 1262 */     this.bout.writeByte(114);
/* 1263 */     this.handles.assign(paramBoolean ? null : paramObjectStreamClass);
/*      */ 
/* 1265 */     if (this.protocol == 1)
/*      */     {
/* 1267 */       paramObjectStreamClass.writeNonProxy(this);
/*      */     }
/* 1269 */     else writeClassDescriptor(paramObjectStreamClass);
/*      */ 
/* 1272 */     Class localClass = paramObjectStreamClass.forClass();
/* 1273 */     this.bout.setBlockDataMode(true);
/* 1274 */     annotateClass(localClass);
/* 1275 */     this.bout.setBlockDataMode(false);
/* 1276 */     this.bout.writeByte(120);
/*      */ 
/* 1278 */     writeClassDesc(paramObjectStreamClass.getSuperDesc(), false);
/*      */   }
/*      */ 
/*      */   private void writeString(String paramString, boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/* 1286 */     this.handles.assign(paramBoolean ? null : paramString);
/* 1287 */     long l = this.bout.getUTFLength(paramString);
/* 1288 */     if (l <= 65535L) {
/* 1289 */       this.bout.writeByte(116);
/* 1290 */       this.bout.writeUTF(paramString, l);
/*      */     } else {
/* 1292 */       this.bout.writeByte(124);
/* 1293 */       this.bout.writeLongUTF(paramString, l);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void writeArray(Object paramObject, ObjectStreamClass paramObjectStreamClass, boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/* 1305 */     this.bout.writeByte(117);
/* 1306 */     writeClassDesc(paramObjectStreamClass, false);
/* 1307 */     this.handles.assign(paramBoolean ? null : paramObject);
/*      */ 
/* 1309 */     Class localClass = paramObjectStreamClass.forClass().getComponentType();
/*      */     Object localObject1;
/* 1310 */     if (localClass.isPrimitive()) {
/* 1311 */       if (localClass == Integer.TYPE) {
/* 1312 */         localObject1 = (int[])paramObject;
/* 1313 */         this.bout.writeInt(localObject1.length);
/* 1314 */         this.bout.writeInts((int[])localObject1, 0, localObject1.length);
/* 1315 */       } else if (localClass == Byte.TYPE) {
/* 1316 */         localObject1 = (byte[])paramObject;
/* 1317 */         this.bout.writeInt(localObject1.length);
/* 1318 */         this.bout.write((byte[])localObject1, 0, localObject1.length, true);
/* 1319 */       } else if (localClass == Long.TYPE) {
/* 1320 */         localObject1 = (long[])paramObject;
/* 1321 */         this.bout.writeInt(localObject1.length);
/* 1322 */         this.bout.writeLongs((long[])localObject1, 0, localObject1.length);
/* 1323 */       } else if (localClass == Float.TYPE) {
/* 1324 */         localObject1 = (float[])paramObject;
/* 1325 */         this.bout.writeInt(localObject1.length);
/* 1326 */         this.bout.writeFloats((float[])localObject1, 0, localObject1.length);
/* 1327 */       } else if (localClass == Double.TYPE) {
/* 1328 */         localObject1 = (double[])paramObject;
/* 1329 */         this.bout.writeInt(localObject1.length);
/* 1330 */         this.bout.writeDoubles((double[])localObject1, 0, localObject1.length);
/* 1331 */       } else if (localClass == Short.TYPE) {
/* 1332 */         localObject1 = (short[])paramObject;
/* 1333 */         this.bout.writeInt(localObject1.length);
/* 1334 */         this.bout.writeShorts((short[])localObject1, 0, localObject1.length);
/* 1335 */       } else if (localClass == Character.TYPE) {
/* 1336 */         localObject1 = (char[])paramObject;
/* 1337 */         this.bout.writeInt(localObject1.length);
/* 1338 */         this.bout.writeChars((char[])localObject1, 0, localObject1.length);
/* 1339 */       } else if (localClass == Boolean.TYPE) {
/* 1340 */         localObject1 = (boolean[])paramObject;
/* 1341 */         this.bout.writeInt(localObject1.length);
/* 1342 */         this.bout.writeBooleans((boolean[])localObject1, 0, localObject1.length);
/*      */       } else {
/* 1344 */         throw new InternalError();
/*      */       }
/*      */     } else {
/* 1347 */       localObject1 = (Object[])paramObject;
/* 1348 */       int i = localObject1.length;
/* 1349 */       this.bout.writeInt(i);
/* 1350 */       if (extendedDebugInfo) {
/* 1351 */         this.debugInfoStack.push("array (class \"" + paramObject.getClass().getName() + "\", size: " + i + ")");
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/* 1356 */         for (int j = 0; j < i; j++) {
/* 1357 */           if (extendedDebugInfo) {
/* 1358 */             this.debugInfoStack.push("element of array (index: " + j + ")");
/*      */           }
/*      */           try
/*      */           {
/* 1362 */             writeObject0(localObject1[j], false);
/*      */           }
/*      */           finally
/*      */           {
/*      */           }
/*      */         }
/*      */       }
/*      */       finally {
/* 1370 */         if (extendedDebugInfo)
/* 1371 */           this.debugInfoStack.pop();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void writeEnum(Enum paramEnum, ObjectStreamClass paramObjectStreamClass, boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/* 1385 */     this.bout.writeByte(126);
/* 1386 */     ObjectStreamClass localObjectStreamClass = paramObjectStreamClass.getSuperDesc();
/* 1387 */     writeClassDesc(localObjectStreamClass.forClass() == Enum.class ? paramObjectStreamClass : localObjectStreamClass, false);
/* 1388 */     this.handles.assign(paramBoolean ? null : paramEnum);
/* 1389 */     writeString(paramEnum.name(), false);
/*      */   }
/*      */ 
/*      */   private void writeOrdinaryObject(Object paramObject, ObjectStreamClass paramObjectStreamClass, boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/* 1402 */     if (extendedDebugInfo) {
/* 1403 */       this.debugInfoStack.push((this.depth == 1 ? "root " : "") + "object (class \"" + paramObject.getClass().getName() + "\", " + paramObject.toString() + ")");
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 1408 */       paramObjectStreamClass.checkSerialize();
/*      */ 
/* 1410 */       this.bout.writeByte(115);
/* 1411 */       writeClassDesc(paramObjectStreamClass, false);
/* 1412 */       this.handles.assign(paramBoolean ? null : paramObject);
/* 1413 */       if ((paramObjectStreamClass.isExternalizable()) && (!paramObjectStreamClass.isProxy()))
/* 1414 */         writeExternalData((Externalizable)paramObject);
/*      */       else
/* 1416 */         writeSerialData(paramObject, paramObjectStreamClass);
/*      */     }
/*      */     finally {
/* 1419 */       if (extendedDebugInfo)
/* 1420 */         this.debugInfoStack.pop();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void writeExternalData(Externalizable paramExternalizable)
/*      */     throws IOException
/*      */   {
/* 1430 */     PutFieldImpl localPutFieldImpl = this.curPut;
/* 1431 */     this.curPut = null;
/*      */ 
/* 1433 */     if (extendedDebugInfo) {
/* 1434 */       this.debugInfoStack.push("writeExternal data");
/*      */     }
/* 1436 */     SerialCallbackContext localSerialCallbackContext = this.curContext;
/*      */     try {
/* 1438 */       this.curContext = null;
/* 1439 */       if (this.protocol == 1) {
/* 1440 */         paramExternalizable.writeExternal(this);
/*      */       } else {
/* 1442 */         this.bout.setBlockDataMode(true);
/* 1443 */         paramExternalizable.writeExternal(this);
/* 1444 */         this.bout.setBlockDataMode(false);
/* 1445 */         this.bout.writeByte(120);
/*      */       }
/*      */     } finally {
/* 1448 */       this.curContext = localSerialCallbackContext;
/* 1449 */       if (extendedDebugInfo) {
/* 1450 */         this.debugInfoStack.pop();
/*      */       }
/*      */     }
/*      */ 
/* 1454 */     this.curPut = localPutFieldImpl;
/*      */   }
/*      */ 
/*      */   private void writeSerialData(Object paramObject, ObjectStreamClass paramObjectStreamClass)
/*      */     throws IOException
/*      */   {
/* 1464 */     ObjectStreamClass.ClassDataSlot[] arrayOfClassDataSlot = paramObjectStreamClass.getClassDataLayout();
/* 1465 */     for (int i = 0; i < arrayOfClassDataSlot.length; i++) {
/* 1466 */       ObjectStreamClass localObjectStreamClass = arrayOfClassDataSlot[i].desc;
/* 1467 */       if (localObjectStreamClass.hasWriteObjectMethod()) {
/* 1468 */         PutFieldImpl localPutFieldImpl = this.curPut;
/* 1469 */         this.curPut = null;
/* 1470 */         SerialCallbackContext localSerialCallbackContext = this.curContext;
/*      */ 
/* 1472 */         if (extendedDebugInfo) {
/* 1473 */           this.debugInfoStack.push("custom writeObject data (class \"" + localObjectStreamClass.getName() + "\")");
/*      */         }
/*      */ 
/*      */         try
/*      */         {
/* 1478 */           this.curContext = new SerialCallbackContext(paramObject, localObjectStreamClass);
/* 1479 */           this.bout.setBlockDataMode(true);
/* 1480 */           localObjectStreamClass.invokeWriteObject(paramObject, this);
/* 1481 */           this.bout.setBlockDataMode(false);
/* 1482 */           this.bout.writeByte(120);
/*      */         } finally {
/* 1484 */           this.curContext.setUsed();
/* 1485 */           this.curContext = localSerialCallbackContext;
/* 1486 */           if (extendedDebugInfo) {
/* 1487 */             this.debugInfoStack.pop();
/*      */           }
/*      */         }
/*      */ 
/* 1491 */         this.curPut = localPutFieldImpl;
/*      */       } else {
/* 1493 */         defaultWriteFields(paramObject, localObjectStreamClass);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void defaultWriteFields(Object paramObject, ObjectStreamClass paramObjectStreamClass)
/*      */     throws IOException
/*      */   {
/* 1507 */     paramObjectStreamClass.checkDefaultSerialize();
/*      */ 
/* 1509 */     int i = paramObjectStreamClass.getPrimDataSize();
/* 1510 */     if ((this.primVals == null) || (this.primVals.length < i)) {
/* 1511 */       this.primVals = new byte[i];
/*      */     }
/* 1513 */     paramObjectStreamClass.getPrimFieldValues(paramObject, this.primVals);
/* 1514 */     this.bout.write(this.primVals, 0, i, false);
/*      */ 
/* 1516 */     ObjectStreamField[] arrayOfObjectStreamField = paramObjectStreamClass.getFields(false);
/* 1517 */     Object[] arrayOfObject = new Object[paramObjectStreamClass.getNumObjFields()];
/* 1518 */     int j = arrayOfObjectStreamField.length - arrayOfObject.length;
/* 1519 */     paramObjectStreamClass.getObjFieldValues(paramObject, arrayOfObject);
/* 1520 */     for (int k = 0; k < arrayOfObject.length; k++) {
/* 1521 */       if (extendedDebugInfo) {
/* 1522 */         this.debugInfoStack.push("field (class \"" + paramObjectStreamClass.getName() + "\", name: \"" + arrayOfObjectStreamField[(j + k)].getName() + "\", type: \"" + arrayOfObjectStreamField[(j + k)].getType() + "\")");
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/* 1528 */         writeObject0(arrayOfObject[k], arrayOfObjectStreamField[(j + k)].isUnshared());
/*      */       }
/*      */       finally {
/* 1531 */         if (extendedDebugInfo)
/* 1532 */           this.debugInfoStack.pop();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void writeFatalException(IOException paramIOException)
/*      */     throws IOException
/*      */   {
/* 1553 */     clear();
/* 1554 */     boolean bool = this.bout.setBlockDataMode(false);
/*      */     try {
/* 1556 */       this.bout.writeByte(123);
/* 1557 */       writeObject0(paramIOException, false);
/* 1558 */       clear();
/*      */     } finally {
/* 1560 */       this.bout.setBlockDataMode(bool);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static native void floatsToBytes(float[] paramArrayOfFloat, int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3);
/*      */ 
/*      */   private static native void doublesToBytes(double[] paramArrayOfDouble, int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3);
/*      */ 
/*      */   private static class BlockDataOutputStream extends OutputStream
/*      */     implements DataOutput
/*      */   {
/*      */     private static final int MAX_BLOCK_SIZE = 1024;
/*      */     private static final int MAX_HEADER_SIZE = 5;
/*      */     private static final int CHAR_BUF_SIZE = 256;
/* 1730 */     private final byte[] buf = new byte[1024];
/*      */ 
/* 1732 */     private final byte[] hbuf = new byte[5];
/*      */ 
/* 1734 */     private final char[] cbuf = new char[256];
/*      */ 
/* 1737 */     private boolean blkmode = false;
/*      */ 
/* 1739 */     private int pos = 0;
/*      */     private final OutputStream out;
/*      */     private final DataOutputStream dout;
/*      */ 
/*      */     BlockDataOutputStream(OutputStream paramOutputStream)
/*      */     {
/* 1751 */       this.out = paramOutputStream;
/* 1752 */       this.dout = new DataOutputStream(this);
/*      */     }
/*      */ 
/*      */     boolean setBlockDataMode(boolean paramBoolean)
/*      */       throws IOException
/*      */     {
/* 1763 */       if (this.blkmode == paramBoolean) {
/* 1764 */         return this.blkmode;
/*      */       }
/* 1766 */       drain();
/* 1767 */       this.blkmode = paramBoolean;
/* 1768 */       return !this.blkmode;
/*      */     }
/*      */ 
/*      */     boolean getBlockDataMode()
/*      */     {
/* 1776 */       return this.blkmode;
/*      */     }
/*      */ 
/*      */     public void write(int paramInt)
/*      */       throws IOException
/*      */     {
/* 1787 */       if (this.pos >= 1024) {
/* 1788 */         drain();
/*      */       }
/* 1790 */       this.buf[(this.pos++)] = ((byte)paramInt);
/*      */     }
/*      */ 
/*      */     public void write(byte[] paramArrayOfByte) throws IOException {
/* 1794 */       write(paramArrayOfByte, 0, paramArrayOfByte.length, false);
/*      */     }
/*      */ 
/*      */     public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
/* 1798 */       write(paramArrayOfByte, paramInt1, paramInt2, false);
/*      */     }
/*      */ 
/*      */     public void flush() throws IOException {
/* 1802 */       drain();
/* 1803 */       this.out.flush();
/*      */     }
/*      */ 
/*      */     public void close() throws IOException {
/* 1807 */       flush();
/* 1808 */       this.out.close();
/*      */     }
/*      */ 
/*      */     void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean)
/*      */       throws IOException
/*      */     {
/* 1820 */       if ((!paramBoolean) && (!this.blkmode)) {
/* 1821 */         drain();
/* 1822 */         this.out.write(paramArrayOfByte, paramInt1, paramInt2);
/* 1823 */         return;
/*      */       }
/*      */ 
/* 1826 */       while (paramInt2 > 0) {
/* 1827 */         if (this.pos >= 1024) {
/* 1828 */           drain();
/*      */         }
/* 1830 */         if ((paramInt2 >= 1024) && (!paramBoolean) && (this.pos == 0))
/*      */         {
/* 1832 */           writeBlockHeader(1024);
/* 1833 */           this.out.write(paramArrayOfByte, paramInt1, 1024);
/* 1834 */           paramInt1 += 1024;
/* 1835 */           paramInt2 -= 1024;
/*      */         } else {
/* 1837 */           int i = Math.min(paramInt2, 1024 - this.pos);
/* 1838 */           System.arraycopy(paramArrayOfByte, paramInt1, this.buf, this.pos, i);
/* 1839 */           this.pos += i;
/* 1840 */           paramInt1 += i;
/* 1841 */           paramInt2 -= i;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     void drain()
/*      */       throws IOException
/*      */     {
/* 1851 */       if (this.pos == 0) {
/* 1852 */         return;
/*      */       }
/* 1854 */       if (this.blkmode) {
/* 1855 */         writeBlockHeader(this.pos);
/*      */       }
/* 1857 */       this.out.write(this.buf, 0, this.pos);
/* 1858 */       this.pos = 0;
/*      */     }
/*      */ 
/*      */     private void writeBlockHeader(int paramInt)
/*      */       throws IOException
/*      */     {
/* 1867 */       if (paramInt <= 255) {
/* 1868 */         this.hbuf[0] = 119;
/* 1869 */         this.hbuf[1] = ((byte)paramInt);
/* 1870 */         this.out.write(this.hbuf, 0, 2);
/*      */       } else {
/* 1872 */         this.hbuf[0] = 122;
/* 1873 */         Bits.putInt(this.hbuf, 1, paramInt);
/* 1874 */         this.out.write(this.hbuf, 0, 5);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void writeBoolean(boolean paramBoolean)
/*      */       throws IOException
/*      */     {
/* 1887 */       if (this.pos >= 1024) {
/* 1888 */         drain();
/*      */       }
/* 1890 */       Bits.putBoolean(this.buf, this.pos++, paramBoolean);
/*      */     }
/*      */ 
/*      */     public void writeByte(int paramInt) throws IOException {
/* 1894 */       if (this.pos >= 1024) {
/* 1895 */         drain();
/*      */       }
/* 1897 */       this.buf[(this.pos++)] = ((byte)paramInt);
/*      */     }
/*      */ 
/*      */     public void writeChar(int paramInt) throws IOException {
/* 1901 */       if (this.pos + 2 <= 1024) {
/* 1902 */         Bits.putChar(this.buf, this.pos, (char)paramInt);
/* 1903 */         this.pos += 2;
/*      */       } else {
/* 1905 */         this.dout.writeChar(paramInt);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void writeShort(int paramInt) throws IOException {
/* 1910 */       if (this.pos + 2 <= 1024) {
/* 1911 */         Bits.putShort(this.buf, this.pos, (short)paramInt);
/* 1912 */         this.pos += 2;
/*      */       } else {
/* 1914 */         this.dout.writeShort(paramInt);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void writeInt(int paramInt) throws IOException {
/* 1919 */       if (this.pos + 4 <= 1024) {
/* 1920 */         Bits.putInt(this.buf, this.pos, paramInt);
/* 1921 */         this.pos += 4;
/*      */       } else {
/* 1923 */         this.dout.writeInt(paramInt);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void writeFloat(float paramFloat) throws IOException {
/* 1928 */       if (this.pos + 4 <= 1024) {
/* 1929 */         Bits.putFloat(this.buf, this.pos, paramFloat);
/* 1930 */         this.pos += 4;
/*      */       } else {
/* 1932 */         this.dout.writeFloat(paramFloat);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void writeLong(long paramLong) throws IOException {
/* 1937 */       if (this.pos + 8 <= 1024) {
/* 1938 */         Bits.putLong(this.buf, this.pos, paramLong);
/* 1939 */         this.pos += 8;
/*      */       } else {
/* 1941 */         this.dout.writeLong(paramLong);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void writeDouble(double paramDouble) throws IOException {
/* 1946 */       if (this.pos + 8 <= 1024) {
/* 1947 */         Bits.putDouble(this.buf, this.pos, paramDouble);
/* 1948 */         this.pos += 8;
/*      */       } else {
/* 1950 */         this.dout.writeDouble(paramDouble);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void writeBytes(String paramString) throws IOException {
/* 1955 */       int i = paramString.length();
/* 1956 */       int j = 0;
/* 1957 */       int k = 0;
/* 1958 */       for (int m = 0; m < i; ) {
/* 1959 */         if (j >= k) {
/* 1960 */           j = 0;
/* 1961 */           k = Math.min(i - m, 256);
/* 1962 */           paramString.getChars(m, m + k, this.cbuf, 0);
/*      */         }
/* 1964 */         if (this.pos >= 1024) {
/* 1965 */           drain();
/*      */         }
/* 1967 */         int n = Math.min(k - j, 1024 - this.pos);
/* 1968 */         int i1 = this.pos + n;
/* 1969 */         while (this.pos < i1) {
/* 1970 */           this.buf[(this.pos++)] = ((byte)this.cbuf[(j++)]);
/*      */         }
/* 1972 */         m += n;
/*      */       }
/*      */     }
/*      */ 
/*      */     public void writeChars(String paramString) throws IOException {
/* 1977 */       int i = paramString.length();
/* 1978 */       for (int j = 0; j < i; ) {
/* 1979 */         int k = Math.min(i - j, 256);
/* 1980 */         paramString.getChars(j, j + k, this.cbuf, 0);
/* 1981 */         writeChars(this.cbuf, 0, k);
/* 1982 */         j += k;
/*      */       }
/*      */     }
/*      */ 
/*      */     public void writeUTF(String paramString) throws IOException {
/* 1987 */       writeUTF(paramString, getUTFLength(paramString));
/*      */     }
/*      */ 
/*      */     void writeBooleans(boolean[] paramArrayOfBoolean, int paramInt1, int paramInt2)
/*      */       throws IOException
/*      */     {
/* 2000 */       int i = paramInt1 + paramInt2;
/* 2001 */       while (paramInt1 < i) {
/* 2002 */         if (this.pos >= 1024) {
/* 2003 */           drain();
/*      */         }
/* 2005 */         int j = Math.min(i, paramInt1 + (1024 - this.pos));
/* 2006 */         while (paramInt1 < j)
/* 2007 */           Bits.putBoolean(this.buf, this.pos++, paramArrayOfBoolean[(paramInt1++)]);
/*      */       }
/*      */     }
/*      */ 
/*      */     void writeChars(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException
/*      */     {
/* 2013 */       int i = 1022;
/* 2014 */       int j = paramInt1 + paramInt2;
/* 2015 */       while (paramInt1 < j)
/* 2016 */         if (this.pos <= i) {
/* 2017 */           int k = 1024 - this.pos >> 1;
/* 2018 */           int m = Math.min(j, paramInt1 + k);
/* 2019 */           while (paramInt1 < m) {
/* 2020 */             Bits.putChar(this.buf, this.pos, paramArrayOfChar[(paramInt1++)]);
/* 2021 */             this.pos += 2;
/*      */           }
/*      */         } else {
/* 2024 */           this.dout.writeChar(paramArrayOfChar[(paramInt1++)]);
/*      */         }
/*      */     }
/*      */ 
/*      */     void writeShorts(short[] paramArrayOfShort, int paramInt1, int paramInt2) throws IOException
/*      */     {
/* 2030 */       int i = 1022;
/* 2031 */       int j = paramInt1 + paramInt2;
/* 2032 */       while (paramInt1 < j)
/* 2033 */         if (this.pos <= i) {
/* 2034 */           int k = 1024 - this.pos >> 1;
/* 2035 */           int m = Math.min(j, paramInt1 + k);
/* 2036 */           while (paramInt1 < m) {
/* 2037 */             Bits.putShort(this.buf, this.pos, paramArrayOfShort[(paramInt1++)]);
/* 2038 */             this.pos += 2;
/*      */           }
/*      */         } else {
/* 2041 */           this.dout.writeShort(paramArrayOfShort[(paramInt1++)]);
/*      */         }
/*      */     }
/*      */ 
/*      */     void writeInts(int[] paramArrayOfInt, int paramInt1, int paramInt2) throws IOException
/*      */     {
/* 2047 */       int i = 1020;
/* 2048 */       int j = paramInt1 + paramInt2;
/* 2049 */       while (paramInt1 < j)
/* 2050 */         if (this.pos <= i) {
/* 2051 */           int k = 1024 - this.pos >> 2;
/* 2052 */           int m = Math.min(j, paramInt1 + k);
/* 2053 */           while (paramInt1 < m) {
/* 2054 */             Bits.putInt(this.buf, this.pos, paramArrayOfInt[(paramInt1++)]);
/* 2055 */             this.pos += 4;
/*      */           }
/*      */         } else {
/* 2058 */           this.dout.writeInt(paramArrayOfInt[(paramInt1++)]);
/*      */         }
/*      */     }
/*      */ 
/*      */     void writeFloats(float[] paramArrayOfFloat, int paramInt1, int paramInt2) throws IOException
/*      */     {
/* 2064 */       int i = 1020;
/* 2065 */       int j = paramInt1 + paramInt2;
/* 2066 */       while (paramInt1 < j)
/* 2067 */         if (this.pos <= i) {
/* 2068 */           int k = 1024 - this.pos >> 2;
/* 2069 */           int m = Math.min(j - paramInt1, k);
/* 2070 */           ObjectOutputStream.floatsToBytes(paramArrayOfFloat, paramInt1, this.buf, this.pos, m);
/* 2071 */           paramInt1 += m;
/* 2072 */           this.pos += (m << 2);
/*      */         } else {
/* 2074 */           this.dout.writeFloat(paramArrayOfFloat[(paramInt1++)]);
/*      */         }
/*      */     }
/*      */ 
/*      */     void writeLongs(long[] paramArrayOfLong, int paramInt1, int paramInt2) throws IOException
/*      */     {
/* 2080 */       int i = 1016;
/* 2081 */       int j = paramInt1 + paramInt2;
/* 2082 */       while (paramInt1 < j)
/* 2083 */         if (this.pos <= i) {
/* 2084 */           int k = 1024 - this.pos >> 3;
/* 2085 */           int m = Math.min(j, paramInt1 + k);
/* 2086 */           while (paramInt1 < m) {
/* 2087 */             Bits.putLong(this.buf, this.pos, paramArrayOfLong[(paramInt1++)]);
/* 2088 */             this.pos += 8;
/*      */           }
/*      */         } else {
/* 2091 */           this.dout.writeLong(paramArrayOfLong[(paramInt1++)]);
/*      */         }
/*      */     }
/*      */ 
/*      */     void writeDoubles(double[] paramArrayOfDouble, int paramInt1, int paramInt2) throws IOException
/*      */     {
/* 2097 */       int i = 1016;
/* 2098 */       int j = paramInt1 + paramInt2;
/* 2099 */       while (paramInt1 < j)
/* 2100 */         if (this.pos <= i) {
/* 2101 */           int k = 1024 - this.pos >> 3;
/* 2102 */           int m = Math.min(j - paramInt1, k);
/* 2103 */           ObjectOutputStream.doublesToBytes(paramArrayOfDouble, paramInt1, this.buf, this.pos, m);
/* 2104 */           paramInt1 += m;
/* 2105 */           this.pos += (m << 3);
/*      */         } else {
/* 2107 */           this.dout.writeDouble(paramArrayOfDouble[(paramInt1++)]);
/*      */         }
/*      */     }
/*      */ 
/*      */     long getUTFLength(String paramString)
/*      */     {
/* 2116 */       int i = paramString.length();
/* 2117 */       long l = 0L;
/* 2118 */       for (int j = 0; j < i; ) {
/* 2119 */         int k = Math.min(i - j, 256);
/* 2120 */         paramString.getChars(j, j + k, this.cbuf, 0);
/* 2121 */         for (int m = 0; m < k; m++) {
/* 2122 */           int n = this.cbuf[m];
/* 2123 */           if ((n >= 1) && (n <= 127))
/* 2124 */             l += 1L;
/* 2125 */           else if (n > 2047)
/* 2126 */             l += 3L;
/*      */           else {
/* 2128 */             l += 2L;
/*      */           }
/*      */         }
/* 2131 */         j += k;
/*      */       }
/* 2133 */       return l;
/*      */     }
/*      */ 
/*      */     void writeUTF(String paramString, long paramLong)
/*      */       throws IOException
/*      */     {
/* 2143 */       if (paramLong > 65535L) {
/* 2144 */         throw new UTFDataFormatException();
/*      */       }
/* 2146 */       writeShort((int)paramLong);
/* 2147 */       if (paramLong == paramString.length())
/* 2148 */         writeBytes(paramString);
/*      */       else
/* 2150 */         writeUTFBody(paramString);
/*      */     }
/*      */ 
/*      */     void writeLongUTF(String paramString)
/*      */       throws IOException
/*      */     {
/* 2160 */       writeLongUTF(paramString, getUTFLength(paramString));
/*      */     }
/*      */ 
/*      */     void writeLongUTF(String paramString, long paramLong)
/*      */       throws IOException
/*      */     {
/* 2168 */       writeLong(paramLong);
/* 2169 */       if (paramLong == paramString.length())
/* 2170 */         writeBytes(paramString);
/*      */       else
/* 2172 */         writeUTFBody(paramString);
/*      */     }
/*      */ 
/*      */     private void writeUTFBody(String paramString)
/*      */       throws IOException
/*      */     {
/* 2181 */       int i = 1021;
/* 2182 */       int j = paramString.length();
/* 2183 */       for (int k = 0; k < j; ) {
/* 2184 */         int m = Math.min(j - k, 256);
/* 2185 */         paramString.getChars(k, k + m, this.cbuf, 0);
/* 2186 */         for (int n = 0; n < m; n++) {
/* 2187 */           int i1 = this.cbuf[n];
/* 2188 */           if (this.pos <= i) {
/* 2189 */             if ((i1 <= 127) && (i1 != 0)) {
/* 2190 */               this.buf[(this.pos++)] = ((byte)i1);
/* 2191 */             } else if (i1 > 2047) {
/* 2192 */               this.buf[(this.pos + 2)] = ((byte)(0x80 | i1 >> 0 & 0x3F));
/* 2193 */               this.buf[(this.pos + 1)] = ((byte)(0x80 | i1 >> 6 & 0x3F));
/* 2194 */               this.buf[(this.pos + 0)] = ((byte)(0xE0 | i1 >> 12 & 0xF));
/* 2195 */               this.pos += 3;
/*      */             } else {
/* 2197 */               this.buf[(this.pos + 1)] = ((byte)(0x80 | i1 >> 0 & 0x3F));
/* 2198 */               this.buf[(this.pos + 0)] = ((byte)(0xC0 | i1 >> 6 & 0x1F));
/* 2199 */               this.pos += 2;
/*      */             }
/*      */           }
/* 2202 */           else if ((i1 <= 127) && (i1 != 0)) {
/* 2203 */             write(i1);
/* 2204 */           } else if (i1 > 2047) {
/* 2205 */             write(0xE0 | i1 >> 12 & 0xF);
/* 2206 */             write(0x80 | i1 >> 6 & 0x3F);
/* 2207 */             write(0x80 | i1 >> 0 & 0x3F);
/*      */           } else {
/* 2209 */             write(0xC0 | i1 >> 6 & 0x1F);
/* 2210 */             write(0x80 | i1 >> 0 & 0x3F);
/*      */           }
/*      */         }
/*      */ 
/* 2214 */         k += m;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class Caches
/*      */   {
/*  167 */     static final ConcurrentMap<ObjectStreamClass.WeakClassKey, Boolean> subclassAudits = new ConcurrentHashMap();
/*      */ 
/*  171 */     static final ReferenceQueue<Class<?>> subclassAuditsQueue = new ReferenceQueue();
/*      */   }
/*      */ 
/*      */   private static class DebugTraceInfoStack
/*      */   {
/*      */     private final List<String> stack;
/*      */ 
/*      */     DebugTraceInfoStack()
/*      */     {
/* 2416 */       this.stack = new ArrayList();
/*      */     }
/*      */ 
/*      */     void clear()
/*      */     {
/* 2423 */       this.stack.clear();
/*      */     }
/*      */ 
/*      */     void pop()
/*      */     {
/* 2430 */       this.stack.remove(this.stack.size() - 1);
/*      */     }
/*      */ 
/*      */     void push(String paramString)
/*      */     {
/* 2437 */       this.stack.add("\t- " + paramString);
/*      */     }
/*      */ 
/*      */     public String toString()
/*      */     {
/* 2444 */       StringBuilder localStringBuilder = new StringBuilder();
/* 2445 */       if (!this.stack.isEmpty()) {
/* 2446 */         for (int i = this.stack.size(); i > 0; i--) {
/* 2447 */           localStringBuilder.append((String)this.stack.get(i - 1) + (i != 1 ? "\n" : ""));
/*      */         }
/*      */       }
/* 2450 */       return localStringBuilder.toString();
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class HandleTable
/*      */   {
/*      */     private int size;
/*      */     private int threshold;
/*      */     private final float loadFactor;
/*      */     private int[] spine;
/*      */     private int[] next;
/*      */     private Object[] objs;
/*      */ 
/*      */     HandleTable(int paramInt, float paramFloat)
/*      */     {
/* 2242 */       this.loadFactor = paramFloat;
/* 2243 */       this.spine = new int[paramInt];
/* 2244 */       this.next = new int[paramInt];
/* 2245 */       this.objs = new Object[paramInt];
/* 2246 */       this.threshold = ((int)(paramInt * paramFloat));
/* 2247 */       clear();
/*      */     }
/*      */ 
/*      */     int assign(Object paramObject)
/*      */     {
/* 2255 */       if (this.size >= this.next.length) {
/* 2256 */         growEntries();
/*      */       }
/* 2258 */       if (this.size >= this.threshold) {
/* 2259 */         growSpine();
/*      */       }
/* 2261 */       insert(paramObject, this.size);
/* 2262 */       return this.size++;
/*      */     }
/*      */ 
/*      */     int lookup(Object paramObject)
/*      */     {
/* 2270 */       if (this.size == 0) {
/* 2271 */         return -1;
/*      */       }
/* 2273 */       int i = hash(paramObject) % this.spine.length;
/* 2274 */       for (int j = this.spine[i]; j >= 0; j = this.next[j]) {
/* 2275 */         if (this.objs[j] == paramObject) {
/* 2276 */           return j;
/*      */         }
/*      */       }
/* 2279 */       return -1;
/*      */     }
/*      */ 
/*      */     void clear()
/*      */     {
/* 2286 */       Arrays.fill(this.spine, -1);
/* 2287 */       Arrays.fill(this.objs, 0, this.size, null);
/* 2288 */       this.size = 0;
/*      */     }
/*      */ 
/*      */     int size()
/*      */     {
/* 2295 */       return this.size;
/*      */     }
/*      */ 
/*      */     private void insert(Object paramObject, int paramInt)
/*      */     {
/* 2303 */       int i = hash(paramObject) % this.spine.length;
/* 2304 */       this.objs[paramInt] = paramObject;
/* 2305 */       this.next[paramInt] = this.spine[i];
/* 2306 */       this.spine[i] = paramInt;
/*      */     }
/*      */ 
/*      */     private void growSpine()
/*      */     {
/* 2314 */       this.spine = new int[(this.spine.length << 1) + 1];
/* 2315 */       this.threshold = ((int)(this.spine.length * this.loadFactor));
/* 2316 */       Arrays.fill(this.spine, -1);
/* 2317 */       for (int i = 0; i < this.size; i++)
/* 2318 */         insert(this.objs[i], i);
/*      */     }
/*      */ 
/*      */     private void growEntries()
/*      */     {
/* 2326 */       int i = (this.next.length << 1) + 1;
/* 2327 */       int[] arrayOfInt = new int[i];
/* 2328 */       System.arraycopy(this.next, 0, arrayOfInt, 0, this.size);
/* 2329 */       this.next = arrayOfInt;
/*      */ 
/* 2331 */       Object[] arrayOfObject = new Object[i];
/* 2332 */       System.arraycopy(this.objs, 0, arrayOfObject, 0, this.size);
/* 2333 */       this.objs = arrayOfObject;
/*      */     }
/*      */ 
/*      */     private int hash(Object paramObject)
/*      */     {
/* 2340 */       return System.identityHashCode(paramObject) & 0x7FFFFFFF;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static abstract class PutField
/*      */   {
/*      */     public abstract void put(String paramString, boolean paramBoolean);
/*      */ 
/*      */     public abstract void put(String paramString, byte paramByte);
/*      */ 
/*      */     public abstract void put(String paramString, char paramChar);
/*      */ 
/*      */     public abstract void put(String paramString, short paramShort);
/*      */ 
/*      */     public abstract void put(String paramString, int paramInt);
/*      */ 
/*      */     public abstract void put(String paramString, long paramLong);
/*      */ 
/*      */     public abstract void put(String paramString, float paramFloat);
/*      */ 
/*      */     public abstract void put(String paramString, double paramDouble);
/*      */ 
/*      */     public abstract void put(String paramString, Object paramObject);
/*      */ 
/*      */     @Deprecated
/*      */     public abstract void write(ObjectOutput paramObjectOutput)
/*      */       throws IOException;
/*      */   }
/*      */ 
/*      */   private class PutFieldImpl extends ObjectOutputStream.PutField
/*      */   {
/*      */     private final ObjectStreamClass desc;
/*      */     private final byte[] primVals;
/*      */     private final Object[] objVals;
/*      */ 
/*      */     PutFieldImpl(ObjectStreamClass arg2)
/*      */     {
/*      */       Object localObject;
/* 1597 */       this.desc = localObject;
/* 1598 */       this.primVals = new byte[localObject.getPrimDataSize()];
/* 1599 */       this.objVals = new Object[localObject.getNumObjFields()];
/*      */     }
/*      */ 
/*      */     public void put(String paramString, boolean paramBoolean) {
/* 1603 */       Bits.putBoolean(this.primVals, getFieldOffset(paramString, Boolean.TYPE), paramBoolean);
/*      */     }
/*      */ 
/*      */     public void put(String paramString, byte paramByte) {
/* 1607 */       this.primVals[getFieldOffset(paramString, Byte.TYPE)] = paramByte;
/*      */     }
/*      */ 
/*      */     public void put(String paramString, char paramChar) {
/* 1611 */       Bits.putChar(this.primVals, getFieldOffset(paramString, Character.TYPE), paramChar);
/*      */     }
/*      */ 
/*      */     public void put(String paramString, short paramShort) {
/* 1615 */       Bits.putShort(this.primVals, getFieldOffset(paramString, Short.TYPE), paramShort);
/*      */     }
/*      */ 
/*      */     public void put(String paramString, int paramInt) {
/* 1619 */       Bits.putInt(this.primVals, getFieldOffset(paramString, Integer.TYPE), paramInt);
/*      */     }
/*      */ 
/*      */     public void put(String paramString, float paramFloat) {
/* 1623 */       Bits.putFloat(this.primVals, getFieldOffset(paramString, Float.TYPE), paramFloat);
/*      */     }
/*      */ 
/*      */     public void put(String paramString, long paramLong) {
/* 1627 */       Bits.putLong(this.primVals, getFieldOffset(paramString, Long.TYPE), paramLong);
/*      */     }
/*      */ 
/*      */     public void put(String paramString, double paramDouble) {
/* 1631 */       Bits.putDouble(this.primVals, getFieldOffset(paramString, Double.TYPE), paramDouble);
/*      */     }
/*      */ 
/*      */     public void put(String paramString, Object paramObject) {
/* 1635 */       this.objVals[getFieldOffset(paramString, Object.class)] = paramObject;
/*      */     }
/*      */ 
/*      */     public void write(ObjectOutput paramObjectOutput)
/*      */       throws IOException
/*      */     {
/* 1655 */       if (ObjectOutputStream.this != paramObjectOutput) {
/* 1656 */         throw new IllegalArgumentException("wrong stream");
/*      */       }
/* 1658 */       paramObjectOutput.write(this.primVals, 0, this.primVals.length);
/*      */ 
/* 1660 */       ObjectStreamField[] arrayOfObjectStreamField = this.desc.getFields(false);
/* 1661 */       int i = arrayOfObjectStreamField.length - this.objVals.length;
/*      */ 
/* 1663 */       for (int j = 0; j < this.objVals.length; j++) {
/* 1664 */         if (arrayOfObjectStreamField[(i + j)].isUnshared()) {
/* 1665 */           throw new IOException("cannot write unshared object");
/*      */         }
/* 1667 */         paramObjectOutput.writeObject(this.objVals[j]);
/*      */       }
/*      */     }
/*      */ 
/*      */     void writeFields()
/*      */       throws IOException
/*      */     {
/* 1675 */       ObjectOutputStream.this.bout.write(this.primVals, 0, this.primVals.length, false);
/*      */ 
/* 1677 */       ObjectStreamField[] arrayOfObjectStreamField = this.desc.getFields(false);
/* 1678 */       int i = arrayOfObjectStreamField.length - this.objVals.length;
/* 1679 */       for (int j = 0; j < this.objVals.length; j++) {
/* 1680 */         if (ObjectOutputStream.extendedDebugInfo) {
/* 1681 */           ObjectOutputStream.this.debugInfoStack.push("field (class \"" + this.desc.getName() + "\", name: \"" + arrayOfObjectStreamField[(i + j)].getName() + "\", type: \"" + arrayOfObjectStreamField[(i + j)].getType() + "\")");
/*      */         }
/*      */ 
/*      */         try
/*      */         {
/* 1687 */           ObjectOutputStream.this.writeObject0(this.objVals[j], arrayOfObjectStreamField[(i + j)].isUnshared());
/*      */         }
/*      */         finally {
/* 1690 */           if (ObjectOutputStream.extendedDebugInfo)
/* 1691 */             ObjectOutputStream.this.debugInfoStack.pop();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     private int getFieldOffset(String paramString, Class paramClass)
/*      */     {
/* 1704 */       ObjectStreamField localObjectStreamField = this.desc.getField(paramString, paramClass);
/* 1705 */       if (localObjectStreamField == null) {
/* 1706 */         throw new IllegalArgumentException("no such field " + paramString + " with type " + paramClass);
/*      */       }
/*      */ 
/* 1709 */       return localObjectStreamField.getOffset();
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class ReplaceTable
/*      */   {
/*      */     private final ObjectOutputStream.HandleTable htab;
/*      */     private Object[] reps;
/*      */ 
/*      */     ReplaceTable(int paramInt, float paramFloat)
/*      */     {
/* 2359 */       this.htab = new ObjectOutputStream.HandleTable(paramInt, paramFloat);
/* 2360 */       this.reps = new Object[paramInt];
/*      */     }
/*      */ 
/*      */     void assign(Object paramObject1, Object paramObject2)
/*      */     {
/* 2367 */       int i = this.htab.assign(paramObject1);
/* 2368 */       while (i >= this.reps.length) {
/* 2369 */         grow();
/*      */       }
/* 2371 */       this.reps[i] = paramObject2;
/*      */     }
/*      */ 
/*      */     Object lookup(Object paramObject)
/*      */     {
/* 2379 */       int i = this.htab.lookup(paramObject);
/* 2380 */       return i >= 0 ? this.reps[i] : paramObject;
/*      */     }
/*      */ 
/*      */     void clear()
/*      */     {
/* 2387 */       Arrays.fill(this.reps, 0, this.htab.size(), null);
/* 2388 */       this.htab.clear();
/*      */     }
/*      */ 
/*      */     int size()
/*      */     {
/* 2395 */       return this.htab.size();
/*      */     }
/*      */ 
/*      */     private void grow()
/*      */     {
/* 2402 */       Object[] arrayOfObject = new Object[(this.reps.length << 1) + 1];
/* 2403 */       System.arraycopy(this.reps, 0, arrayOfObject, 0, this.reps.length);
/* 2404 */       this.reps = arrayOfObject;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.io.ObjectOutputStream
 * JD-Core Version:    0.6.2
 */