/*      */ package java.io;
/*      */ 
/*      */ import java.lang.ref.Reference;
/*      */ import java.lang.ref.ReferenceQueue;
/*      */ import java.lang.ref.SoftReference;
/*      */ import java.lang.ref.WeakReference;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.Field;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Member;
/*      */ import java.lang.reflect.Method;
/*      */ import java.lang.reflect.Proxy;
/*      */ import java.security.AccessController;
/*      */ import java.security.MessageDigest;
/*      */ import java.security.NoSuchAlgorithmException;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashSet;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ import java.util.concurrent.ConcurrentMap;
/*      */ import sun.misc.Unsafe;
/*      */ import sun.reflect.ReflectionFactory;
/*      */ import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;
/*      */ 
/*      */ public class ObjectStreamClass
/*      */   implements Serializable
/*      */ {
/*   72 */   public static final ObjectStreamField[] NO_FIELDS = new ObjectStreamField[0];
/*      */   private static final long serialVersionUID = -6120832682080437368L;
/*   76 */   private static final ObjectStreamField[] serialPersistentFields = NO_FIELDS;
/*      */ 
/*   80 */   private static final ReflectionFactory reflFactory = (ReflectionFactory)AccessController.doPrivileged(new ReflectionFactory.GetReflectionFactoryAction());
/*      */   private Class<?> cl;
/*      */   private String name;
/*      */   private volatile Long suid;
/*      */   private boolean isProxy;
/*      */   private boolean isEnum;
/*      */   private boolean serializable;
/*      */   private boolean externalizable;
/*      */   private boolean hasWriteObjectData;
/*  124 */   private boolean hasBlockExternalData = true;
/*      */   private ClassNotFoundException resolveEx;
/*      */   private ExceptionInfo deserializeEx;
/*      */   private ExceptionInfo serializeEx;
/*      */   private ExceptionInfo defaultSerializeEx;
/*      */   private ObjectStreamField[] fields;
/*      */   private int primDataSize;
/*      */   private int numObjFields;
/*      */   private FieldReflector fieldRefl;
/*      */   private volatile ClassDataSlot[] dataLayout;
/*      */   private Constructor cons;
/*      */   private Method writeObjectMethod;
/*      */   private Method readObjectMethod;
/*      */   private Method readObjectNoDataMethod;
/*      */   private Method writeReplaceMethod;
/*      */   private Method readResolveMethod;
/*      */   private ObjectStreamClass localDesc;
/*      */   private ObjectStreamClass superDesc;
/*      */ 
/*      */   private static native void initNative();
/*      */ 
/*      */   public static ObjectStreamClass lookup(Class<?> paramClass)
/*      */   {
/*  207 */     return lookup(paramClass, false);
/*      */   }
/*      */ 
/*      */   public static ObjectStreamClass lookupAny(Class<?> paramClass)
/*      */   {
/*  219 */     return lookup(paramClass, true);
/*      */   }
/*      */ 
/*      */   public String getName()
/*      */   {
/*  230 */     return this.name;
/*      */   }
/*      */ 
/*      */   public long getSerialVersionUID()
/*      */   {
/*  243 */     if (this.suid == null) {
/*  244 */       this.suid = ((Long)AccessController.doPrivileged(new PrivilegedAction()
/*      */       {
/*      */         public Long run() {
/*  247 */           return Long.valueOf(ObjectStreamClass.computeDefaultSUID(ObjectStreamClass.this.cl));
/*      */         }
/*      */       }));
/*      */     }
/*      */ 
/*  252 */     return this.suid.longValue();
/*      */   }
/*      */ 
/*      */   public Class<?> forClass()
/*      */   {
/*  262 */     return this.cl;
/*      */   }
/*      */ 
/*      */   public ObjectStreamField[] getFields()
/*      */   {
/*  274 */     return getFields(true);
/*      */   }
/*      */ 
/*      */   public ObjectStreamField getField(String paramString)
/*      */   {
/*  285 */     return getField(paramString, null);
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/*  292 */     return this.name + ": static final long serialVersionUID = " + getSerialVersionUID() + "L;";
/*      */   }
/*      */ 
/*      */   static ObjectStreamClass lookup(Class<?> paramClass, boolean paramBoolean)
/*      */   {
/*  305 */     if ((!paramBoolean) && (!Serializable.class.isAssignableFrom(paramClass))) {
/*  306 */       return null;
/*      */     }
/*  308 */     processQueue(Caches.localDescsQueue, Caches.localDescs);
/*  309 */     WeakClassKey localWeakClassKey = new WeakClassKey(paramClass, Caches.localDescsQueue);
/*  310 */     Reference localReference = (Reference)Caches.localDescs.get(localWeakClassKey);
/*  311 */     Object localObject1 = null;
/*  312 */     if (localReference != null) {
/*  313 */       localObject1 = localReference.get();
/*      */     }
/*  315 */     Object localObject2 = null;
/*  316 */     if (localObject1 == null) {
/*  317 */       EntryFuture localEntryFuture = new EntryFuture(null);
/*  318 */       SoftReference localSoftReference = new SoftReference(localEntryFuture);
/*      */       do {
/*  320 */         if (localReference != null) {
/*  321 */           Caches.localDescs.remove(localWeakClassKey, localReference);
/*      */         }
/*  323 */         localReference = (Reference)Caches.localDescs.putIfAbsent(localWeakClassKey, localSoftReference);
/*  324 */         if (localReference != null)
/*  325 */           localObject1 = localReference.get();
/*      */       }
/*  327 */       while ((localReference != null) && (localObject1 == null));
/*  328 */       if (localObject1 == null) {
/*  329 */         localObject2 = localEntryFuture;
/*      */       }
/*      */     }
/*      */ 
/*  333 */     if ((localObject1 instanceof ObjectStreamClass)) {
/*  334 */       return (ObjectStreamClass)localObject1;
/*      */     }
/*  336 */     if ((localObject1 instanceof EntryFuture)) {
/*  337 */       localObject2 = (EntryFuture)localObject1;
/*  338 */       if (((EntryFuture)localObject2).getOwner() == Thread.currentThread())
/*      */       {
/*  345 */         localObject1 = null;
/*      */       }
/*  347 */       else localObject1 = ((EntryFuture)localObject2).get();
/*      */     }
/*      */ 
/*  350 */     if (localObject1 == null) {
/*      */       try {
/*  352 */         localObject1 = new ObjectStreamClass(paramClass);
/*      */       } catch (Throwable localThrowable) {
/*  354 */         localObject1 = localThrowable;
/*      */       }
/*  356 */       if (((EntryFuture)localObject2).set(localObject1)) {
/*  357 */         Caches.localDescs.put(localWeakClassKey, new SoftReference(localObject1));
/*      */       }
/*      */       else {
/*  360 */         localObject1 = ((EntryFuture)localObject2).get();
/*      */       }
/*      */     }
/*      */ 
/*  364 */     if ((localObject1 instanceof ObjectStreamClass))
/*  365 */       return (ObjectStreamClass)localObject1;
/*  366 */     if ((localObject1 instanceof RuntimeException))
/*  367 */       throw ((RuntimeException)localObject1);
/*  368 */     if ((localObject1 instanceof Error)) {
/*  369 */       throw ((Error)localObject1);
/*      */     }
/*  371 */     throw new InternalError("unexpected entry: " + localObject1);
/*      */   }
/*      */ 
/*      */   private ObjectStreamClass(final Class<?> paramClass)
/*      */   {
/*  443 */     this.cl = paramClass;
/*  444 */     this.name = paramClass.getName();
/*  445 */     this.isProxy = Proxy.isProxyClass(paramClass);
/*  446 */     this.isEnum = Enum.class.isAssignableFrom(paramClass);
/*  447 */     this.serializable = Serializable.class.isAssignableFrom(paramClass);
/*  448 */     this.externalizable = Externalizable.class.isAssignableFrom(paramClass);
/*      */ 
/*  450 */     Class localClass = paramClass.getSuperclass();
/*  451 */     this.superDesc = (localClass != null ? lookup(localClass, false) : null);
/*  452 */     this.localDesc = this;
/*      */ 
/*  454 */     if (this.serializable) {
/*  455 */       AccessController.doPrivileged(new PrivilegedAction() {
/*      */         public Void run() {
/*  457 */           if (ObjectStreamClass.this.isEnum) {
/*  458 */             ObjectStreamClass.this.suid = Long.valueOf(0L);
/*  459 */             ObjectStreamClass.this.fields = ObjectStreamClass.NO_FIELDS;
/*  460 */             return null;
/*      */           }
/*  462 */           if (paramClass.isArray()) {
/*  463 */             ObjectStreamClass.this.fields = ObjectStreamClass.NO_FIELDS;
/*  464 */             return null;
/*      */           }
/*      */ 
/*  467 */           ObjectStreamClass.this.suid = ObjectStreamClass.getDeclaredSUID(paramClass);
/*      */           try {
/*  469 */             ObjectStreamClass.this.fields = ObjectStreamClass.getSerialFields(paramClass);
/*  470 */             ObjectStreamClass.this.computeFieldOffsets();
/*      */           } catch (InvalidClassException localInvalidClassException) {
/*  472 */             ObjectStreamClass.this.serializeEx = ObjectStreamClass.access$1102(ObjectStreamClass.this, new ObjectStreamClass.ExceptionInfo(localInvalidClassException.classname, localInvalidClassException.getMessage()));
/*      */ 
/*  474 */             ObjectStreamClass.this.fields = ObjectStreamClass.NO_FIELDS;
/*      */           }
/*      */ 
/*  477 */           if (ObjectStreamClass.this.externalizable) {
/*  478 */             ObjectStreamClass.this.cons = ObjectStreamClass.getExternalizableConstructor(paramClass);
/*      */           } else {
/*  480 */             ObjectStreamClass.this.cons = ObjectStreamClass.getSerializableConstructor(paramClass);
/*  481 */             ObjectStreamClass.this.writeObjectMethod = ObjectStreamClass.getPrivateMethod(paramClass, "writeObject", new Class[] { ObjectOutputStream.class }, Void.TYPE);
/*      */ 
/*  484 */             ObjectStreamClass.this.readObjectMethod = ObjectStreamClass.getPrivateMethod(paramClass, "readObject", new Class[] { ObjectInputStream.class }, Void.TYPE);
/*      */ 
/*  487 */             ObjectStreamClass.this.readObjectNoDataMethod = ObjectStreamClass.getPrivateMethod(paramClass, "readObjectNoData", null, Void.TYPE);
/*      */ 
/*  489 */             ObjectStreamClass.this.hasWriteObjectData = (ObjectStreamClass.this.writeObjectMethod != null);
/*      */           }
/*  491 */           ObjectStreamClass.this.writeReplaceMethod = ObjectStreamClass.getInheritableMethod(paramClass, "writeReplace", null, Object.class);
/*      */ 
/*  493 */           ObjectStreamClass.this.readResolveMethod = ObjectStreamClass.getInheritableMethod(paramClass, "readResolve", null, Object.class);
/*      */ 
/*  495 */           return null;
/*      */         } } );
/*      */     }
/*      */     else {
/*  499 */       this.suid = Long.valueOf(0L);
/*  500 */       this.fields = NO_FIELDS;
/*      */     }
/*      */     try
/*      */     {
/*  504 */       this.fieldRefl = getReflector(this.fields, this);
/*      */     }
/*      */     catch (InvalidClassException localInvalidClassException) {
/*  507 */       throw new InternalError();
/*      */     }
/*      */ 
/*  510 */     if (this.deserializeEx == null) {
/*  511 */       if (this.isEnum)
/*  512 */         this.deserializeEx = new ExceptionInfo(this.name, "enum type");
/*  513 */       else if (this.cons == null) {
/*  514 */         this.deserializeEx = new ExceptionInfo(this.name, "no valid constructor");
/*      */       }
/*      */     }
/*  517 */     for (int i = 0; i < this.fields.length; i++)
/*  518 */       if (this.fields[i].getField() == null)
/*  519 */         this.defaultSerializeEx = new ExceptionInfo(this.name, "unmatched serializable field(s) declared");
/*      */   }
/*      */ 
/*      */   ObjectStreamClass()
/*      */   {
/*      */   }
/*      */ 
/*      */   void initProxy(Class<?> paramClass, ClassNotFoundException paramClassNotFoundException, ObjectStreamClass paramObjectStreamClass)
/*      */     throws InvalidClassException
/*      */   {
/*  540 */     this.cl = paramClass;
/*  541 */     this.resolveEx = paramClassNotFoundException;
/*  542 */     this.superDesc = paramObjectStreamClass;
/*  543 */     this.isProxy = true;
/*  544 */     this.serializable = true;
/*  545 */     this.suid = Long.valueOf(0L);
/*  546 */     this.fields = NO_FIELDS;
/*      */ 
/*  548 */     if (paramClass != null) {
/*  549 */       this.localDesc = lookup(paramClass, true);
/*  550 */       if (!this.localDesc.isProxy) {
/*  551 */         throw new InvalidClassException("cannot bind proxy descriptor to a non-proxy class");
/*      */       }
/*      */ 
/*  554 */       this.name = this.localDesc.name;
/*  555 */       this.externalizable = this.localDesc.externalizable;
/*  556 */       this.cons = this.localDesc.cons;
/*  557 */       this.writeReplaceMethod = this.localDesc.writeReplaceMethod;
/*  558 */       this.readResolveMethod = this.localDesc.readResolveMethod;
/*  559 */       this.deserializeEx = this.localDesc.deserializeEx;
/*      */     }
/*  561 */     this.fieldRefl = getReflector(this.fields, this.localDesc);
/*      */   }
/*      */ 
/*      */   void initNonProxy(ObjectStreamClass paramObjectStreamClass1, Class<?> paramClass, ClassNotFoundException paramClassNotFoundException, ObjectStreamClass paramObjectStreamClass2)
/*      */     throws InvalidClassException
/*      */   {
/*  573 */     this.cl = paramClass;
/*  574 */     this.resolveEx = paramClassNotFoundException;
/*  575 */     this.superDesc = paramObjectStreamClass2;
/*  576 */     this.name = paramObjectStreamClass1.name;
/*  577 */     this.suid = Long.valueOf(paramObjectStreamClass1.getSerialVersionUID());
/*  578 */     this.isProxy = false;
/*  579 */     this.isEnum = paramObjectStreamClass1.isEnum;
/*  580 */     this.serializable = paramObjectStreamClass1.serializable;
/*  581 */     this.externalizable = paramObjectStreamClass1.externalizable;
/*  582 */     this.hasBlockExternalData = paramObjectStreamClass1.hasBlockExternalData;
/*  583 */     this.hasWriteObjectData = paramObjectStreamClass1.hasWriteObjectData;
/*  584 */     this.fields = paramObjectStreamClass1.fields;
/*  585 */     this.primDataSize = paramObjectStreamClass1.primDataSize;
/*  586 */     this.numObjFields = paramObjectStreamClass1.numObjFields;
/*      */ 
/*  588 */     if (paramClass != null) {
/*  589 */       this.localDesc = lookup(paramClass, true);
/*  590 */       if (this.localDesc.isProxy) {
/*  591 */         throw new InvalidClassException("cannot bind non-proxy descriptor to a proxy class");
/*      */       }
/*      */ 
/*  594 */       if (this.isEnum != this.localDesc.isEnum) {
/*  595 */         throw new InvalidClassException(this.isEnum ? "cannot bind enum descriptor to a non-enum class" : "cannot bind non-enum descriptor to an enum class");
/*      */       }
/*      */ 
/*  600 */       if ((this.serializable == this.localDesc.serializable) && (!paramClass.isArray()) && (this.suid.longValue() != this.localDesc.getSerialVersionUID()))
/*      */       {
/*  604 */         throw new InvalidClassException(this.localDesc.name, "local class incompatible: stream classdesc serialVersionUID = " + this.suid + ", local class serialVersionUID = " + this.localDesc.getSerialVersionUID());
/*      */       }
/*      */ 
/*  611 */       if (!classNamesEqual(this.name, this.localDesc.name)) {
/*  612 */         throw new InvalidClassException(this.localDesc.name, "local class name incompatible with stream class name \"" + this.name + "\"");
/*      */       }
/*      */ 
/*  617 */       if (!this.isEnum) {
/*  618 */         if ((this.serializable == this.localDesc.serializable) && (this.externalizable != this.localDesc.externalizable))
/*      */         {
/*  621 */           throw new InvalidClassException(this.localDesc.name, "Serializable incompatible with Externalizable");
/*      */         }
/*      */ 
/*  625 */         if ((this.serializable != this.localDesc.serializable) || (this.externalizable != this.localDesc.externalizable) || ((!this.serializable) && (!this.externalizable)))
/*      */         {
/*  629 */           this.deserializeEx = new ExceptionInfo(this.localDesc.name, "class invalid for deserialization");
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  634 */       this.cons = this.localDesc.cons;
/*  635 */       this.writeObjectMethod = this.localDesc.writeObjectMethod;
/*  636 */       this.readObjectMethod = this.localDesc.readObjectMethod;
/*  637 */       this.readObjectNoDataMethod = this.localDesc.readObjectNoDataMethod;
/*  638 */       this.writeReplaceMethod = this.localDesc.writeReplaceMethod;
/*  639 */       this.readResolveMethod = this.localDesc.readResolveMethod;
/*  640 */       if (this.deserializeEx == null) {
/*  641 */         this.deserializeEx = this.localDesc.deserializeEx;
/*      */       }
/*      */     }
/*  644 */     this.fieldRefl = getReflector(this.fields, this.localDesc);
/*      */ 
/*  646 */     this.fields = this.fieldRefl.getFields();
/*      */   }
/*      */ 
/*      */   void readNonProxy(ObjectInputStream paramObjectInputStream)
/*      */     throws IOException, ClassNotFoundException
/*      */   {
/*  658 */     this.name = paramObjectInputStream.readUTF();
/*  659 */     this.suid = Long.valueOf(paramObjectInputStream.readLong());
/*  660 */     this.isProxy = false;
/*      */ 
/*  662 */     int i = paramObjectInputStream.readByte();
/*  663 */     this.hasWriteObjectData = ((i & 0x1) != 0);
/*      */ 
/*  665 */     this.hasBlockExternalData = ((i & 0x8) != 0);
/*      */ 
/*  667 */     this.externalizable = ((i & 0x4) != 0);
/*      */ 
/*  669 */     int j = (i & 0x2) != 0 ? 1 : 0;
/*      */ 
/*  671 */     if ((this.externalizable) && (j != 0)) {
/*  672 */       throw new InvalidClassException(this.name, "serializable and externalizable flags conflict");
/*      */     }
/*      */ 
/*  675 */     this.serializable = ((this.externalizable) || (j != 0));
/*  676 */     this.isEnum = ((i & 0x10) != 0);
/*  677 */     if ((this.isEnum) && (this.suid.longValue() != 0L)) {
/*  678 */       throw new InvalidClassException(this.name, "enum descriptor has non-zero serialVersionUID: " + this.suid);
/*      */     }
/*      */ 
/*  682 */     int k = paramObjectInputStream.readShort();
/*  683 */     if ((this.isEnum) && (k != 0)) {
/*  684 */       throw new InvalidClassException(this.name, "enum descriptor has non-zero field count: " + k);
/*      */     }
/*      */ 
/*  687 */     this.fields = (k > 0 ? new ObjectStreamField[k] : NO_FIELDS);
/*      */ 
/*  689 */     for (int m = 0; m < k; m++) {
/*  690 */       int n = (char)paramObjectInputStream.readByte();
/*  691 */       String str1 = paramObjectInputStream.readUTF();
/*  692 */       String str2 = (n == 76) || (n == 91) ? paramObjectInputStream.readTypeString() : new String(new char[] { n });
/*      */       try
/*      */       {
/*  695 */         this.fields[m] = new ObjectStreamField(str1, str2, false);
/*      */       } catch (RuntimeException localRuntimeException) {
/*  697 */         throw ((IOException)new InvalidClassException(this.name, "invalid descriptor for field " + str1).initCause(localRuntimeException));
/*      */       }
/*      */     }
/*      */ 
/*  701 */     computeFieldOffsets();
/*      */   }
/*      */ 
/*      */   void writeNonProxy(ObjectOutputStream paramObjectOutputStream)
/*      */     throws IOException
/*      */   {
/*  708 */     paramObjectOutputStream.writeUTF(this.name);
/*  709 */     paramObjectOutputStream.writeLong(getSerialVersionUID());
/*      */ 
/*  711 */     int i = 0;
/*  712 */     if (this.externalizable) {
/*  713 */       i = (byte)(i | 0x4);
/*  714 */       j = paramObjectOutputStream.getProtocolVersion();
/*  715 */       if (j != 1)
/*  716 */         i = (byte)(i | 0x8);
/*      */     }
/*  718 */     else if (this.serializable) {
/*  719 */       i = (byte)(i | 0x2);
/*      */     }
/*  721 */     if (this.hasWriteObjectData) {
/*  722 */       i = (byte)(i | 0x1);
/*      */     }
/*  724 */     if (this.isEnum) {
/*  725 */       i = (byte)(i | 0x10);
/*      */     }
/*  727 */     paramObjectOutputStream.writeByte(i);
/*      */ 
/*  729 */     paramObjectOutputStream.writeShort(this.fields.length);
/*  730 */     for (int j = 0; j < this.fields.length; j++) {
/*  731 */       ObjectStreamField localObjectStreamField = this.fields[j];
/*  732 */       paramObjectOutputStream.writeByte(localObjectStreamField.getTypeCode());
/*  733 */       paramObjectOutputStream.writeUTF(localObjectStreamField.getName());
/*  734 */       if (!localObjectStreamField.isPrimitive())
/*  735 */         paramObjectOutputStream.writeTypeString(localObjectStreamField.getTypeString());
/*      */     }
/*      */   }
/*      */ 
/*      */   ClassNotFoundException getResolveException()
/*      */   {
/*  745 */     return this.resolveEx;
/*      */   }
/*      */ 
/*      */   void checkDeserialize()
/*      */     throws InvalidClassException
/*      */   {
/*  754 */     if (this.deserializeEx != null)
/*  755 */       throw this.deserializeEx.newInvalidClassException();
/*      */   }
/*      */ 
/*      */   void checkSerialize()
/*      */     throws InvalidClassException
/*      */   {
/*  765 */     if (this.serializeEx != null)
/*  766 */       throw this.serializeEx.newInvalidClassException();
/*      */   }
/*      */ 
/*      */   void checkDefaultSerialize()
/*      */     throws InvalidClassException
/*      */   {
/*  778 */     if (this.defaultSerializeEx != null)
/*  779 */       throw this.defaultSerializeEx.newInvalidClassException();
/*      */   }
/*      */ 
/*      */   ObjectStreamClass getSuperDesc()
/*      */   {
/*  789 */     return this.superDesc;
/*      */   }
/*      */ 
/*      */   ObjectStreamClass getLocalDesc()
/*      */   {
/*  799 */     return this.localDesc;
/*      */   }
/*      */ 
/*      */   ObjectStreamField[] getFields(boolean paramBoolean)
/*      */   {
/*  809 */     return paramBoolean ? (ObjectStreamField[])this.fields.clone() : this.fields;
/*      */   }
/*      */ 
/*      */   ObjectStreamField getField(String paramString, Class<?> paramClass)
/*      */   {
/*  819 */     for (int i = 0; i < this.fields.length; i++) {
/*  820 */       ObjectStreamField localObjectStreamField = this.fields[i];
/*  821 */       if (localObjectStreamField.getName().equals(paramString)) {
/*  822 */         if ((paramClass == null) || ((paramClass == Object.class) && (!localObjectStreamField.isPrimitive())))
/*      */         {
/*  825 */           return localObjectStreamField;
/*      */         }
/*  827 */         Class localClass = localObjectStreamField.getType();
/*  828 */         if ((localClass != null) && (paramClass.isAssignableFrom(localClass))) {
/*  829 */           return localObjectStreamField;
/*      */         }
/*      */       }
/*      */     }
/*  833 */     return null;
/*      */   }
/*      */ 
/*      */   boolean isProxy()
/*      */   {
/*  841 */     return this.isProxy;
/*      */   }
/*      */ 
/*      */   boolean isEnum()
/*      */   {
/*  849 */     return this.isEnum;
/*      */   }
/*      */ 
/*      */   boolean isExternalizable()
/*      */   {
/*  857 */     return this.externalizable;
/*      */   }
/*      */ 
/*      */   boolean isSerializable()
/*      */   {
/*  865 */     return this.serializable;
/*      */   }
/*      */ 
/*      */   boolean hasBlockExternalData()
/*      */   {
/*  873 */     return this.hasBlockExternalData;
/*      */   }
/*      */ 
/*      */   boolean hasWriteObjectData()
/*      */   {
/*  882 */     return this.hasWriteObjectData;
/*      */   }
/*      */ 
/*      */   boolean isInstantiable()
/*      */   {
/*  893 */     return this.cons != null;
/*      */   }
/*      */ 
/*      */   boolean hasWriteObjectMethod()
/*      */   {
/*  902 */     return this.writeObjectMethod != null;
/*      */   }
/*      */ 
/*      */   boolean hasReadObjectMethod()
/*      */   {
/*  911 */     return this.readObjectMethod != null;
/*      */   }
/*      */ 
/*      */   boolean hasReadObjectNoDataMethod()
/*      */   {
/*  920 */     return this.readObjectNoDataMethod != null;
/*      */   }
/*      */ 
/*      */   boolean hasWriteReplaceMethod()
/*      */   {
/*  928 */     return this.writeReplaceMethod != null;
/*      */   }
/*      */ 
/*      */   boolean hasReadResolveMethod()
/*      */   {
/*  936 */     return this.readResolveMethod != null;
/*      */   }
/*      */ 
/*      */   Object newInstance()
/*      */     throws InstantiationException, InvocationTargetException, UnsupportedOperationException
/*      */   {
/*  952 */     if (this.cons != null) {
/*      */       try {
/*  954 */         return this.cons.newInstance(new Object[0]);
/*      */       }
/*      */       catch (IllegalAccessException localIllegalAccessException) {
/*  957 */         throw new InternalError();
/*      */       }
/*      */     }
/*  960 */     throw new UnsupportedOperationException();
/*      */   }
/*      */ 
/*      */   void invokeWriteObject(Object paramObject, ObjectOutputStream paramObjectOutputStream)
/*      */     throws IOException, UnsupportedOperationException
/*      */   {
/*  973 */     if (this.writeObjectMethod != null)
/*      */       try {
/*  975 */         this.writeObjectMethod.invoke(paramObject, new Object[] { paramObjectOutputStream });
/*      */       } catch (InvocationTargetException localInvocationTargetException) {
/*  977 */         Throwable localThrowable = localInvocationTargetException.getTargetException();
/*  978 */         if ((localThrowable instanceof IOException)) {
/*  979 */           throw ((IOException)localThrowable);
/*      */         }
/*  981 */         throwMiscException(localThrowable);
/*      */       }
/*      */       catch (IllegalAccessException localIllegalAccessException)
/*      */       {
/*  985 */         throw new InternalError();
/*      */       }
/*      */     else
/*  988 */       throw new UnsupportedOperationException();
/*      */   }
/*      */ 
/*      */   void invokeReadObject(Object paramObject, ObjectInputStream paramObjectInputStream)
/*      */     throws ClassNotFoundException, IOException, UnsupportedOperationException
/*      */   {
/* 1002 */     if (this.readObjectMethod != null)
/*      */       try {
/* 1004 */         this.readObjectMethod.invoke(paramObject, new Object[] { paramObjectInputStream });
/*      */       } catch (InvocationTargetException localInvocationTargetException) {
/* 1006 */         Throwable localThrowable = localInvocationTargetException.getTargetException();
/* 1007 */         if ((localThrowable instanceof ClassNotFoundException))
/* 1008 */           throw ((ClassNotFoundException)localThrowable);
/* 1009 */         if ((localThrowable instanceof IOException)) {
/* 1010 */           throw ((IOException)localThrowable);
/*      */         }
/* 1012 */         throwMiscException(localThrowable);
/*      */       }
/*      */       catch (IllegalAccessException localIllegalAccessException)
/*      */       {
/* 1016 */         throw new InternalError();
/*      */       }
/*      */     else
/* 1019 */       throw new UnsupportedOperationException();
/*      */   }
/*      */ 
/*      */   void invokeReadObjectNoData(Object paramObject)
/*      */     throws IOException, UnsupportedOperationException
/*      */   {
/* 1032 */     if (this.readObjectNoDataMethod != null)
/*      */       try {
/* 1034 */         this.readObjectNoDataMethod.invoke(paramObject, (Object[])null);
/*      */       } catch (InvocationTargetException localInvocationTargetException) {
/* 1036 */         Throwable localThrowable = localInvocationTargetException.getTargetException();
/* 1037 */         if ((localThrowable instanceof ObjectStreamException)) {
/* 1038 */           throw ((ObjectStreamException)localThrowable);
/*      */         }
/* 1040 */         throwMiscException(localThrowable);
/*      */       }
/*      */       catch (IllegalAccessException localIllegalAccessException)
/*      */       {
/* 1044 */         throw new InternalError();
/*      */       }
/*      */     else
/* 1047 */       throw new UnsupportedOperationException();
/*      */   }
/*      */ 
/*      */   Object invokeWriteReplace(Object paramObject)
/*      */     throws IOException, UnsupportedOperationException
/*      */   {
/* 1060 */     if (this.writeReplaceMethod != null) {
/*      */       try {
/* 1062 */         return this.writeReplaceMethod.invoke(paramObject, (Object[])null);
/*      */       } catch (InvocationTargetException localInvocationTargetException) {
/* 1064 */         Throwable localThrowable = localInvocationTargetException.getTargetException();
/* 1065 */         if ((localThrowable instanceof ObjectStreamException)) {
/* 1066 */           throw ((ObjectStreamException)localThrowable);
/*      */         }
/* 1068 */         throwMiscException(localThrowable);
/* 1069 */         throw new InternalError();
/*      */       }
/*      */       catch (IllegalAccessException localIllegalAccessException)
/*      */       {
/* 1073 */         throw new InternalError();
/*      */       }
/*      */     }
/* 1076 */     throw new UnsupportedOperationException();
/*      */   }
/*      */ 
/*      */   Object invokeReadResolve(Object paramObject)
/*      */     throws IOException, UnsupportedOperationException
/*      */   {
/* 1089 */     if (this.readResolveMethod != null) {
/*      */       try {
/* 1091 */         return this.readResolveMethod.invoke(paramObject, (Object[])null);
/*      */       } catch (InvocationTargetException localInvocationTargetException) {
/* 1093 */         Throwable localThrowable = localInvocationTargetException.getTargetException();
/* 1094 */         if ((localThrowable instanceof ObjectStreamException)) {
/* 1095 */           throw ((ObjectStreamException)localThrowable);
/*      */         }
/* 1097 */         throwMiscException(localThrowable);
/* 1098 */         throw new InternalError();
/*      */       }
/*      */       catch (IllegalAccessException localIllegalAccessException)
/*      */       {
/* 1102 */         throw new InternalError();
/*      */       }
/*      */     }
/* 1105 */     throw new UnsupportedOperationException();
/*      */   }
/*      */ 
/*      */   ClassDataSlot[] getClassDataLayout()
/*      */     throws InvalidClassException
/*      */   {
/* 1137 */     if (this.dataLayout == null) {
/* 1138 */       this.dataLayout = getClassDataLayout0();
/*      */     }
/* 1140 */     return this.dataLayout;
/*      */   }
/*      */ 
/*      */   private ClassDataSlot[] getClassDataLayout0()
/*      */     throws InvalidClassException
/*      */   {
/* 1146 */     ArrayList localArrayList = new ArrayList();
/* 1147 */     Class localClass1 = this.cl; Class localClass2 = this.cl;
/*      */ 
/* 1150 */     while ((localClass2 != null) && (Serializable.class.isAssignableFrom(localClass2))) {
/* 1151 */       localClass2 = localClass2.getSuperclass();
/*      */     }
/*      */ 
/* 1154 */     for (Object localObject1 = this; localObject1 != null; localObject1 = ((ObjectStreamClass)localObject1).superDesc)
/*      */     {
/* 1157 */       String str = ((ObjectStreamClass)localObject1).cl != null ? ((ObjectStreamClass)localObject1).cl.getName() : ((ObjectStreamClass)localObject1).name;
/* 1158 */       Object localObject2 = null;
/* 1159 */       for (Class localClass3 = localClass1; localClass3 != localClass2; localClass3 = localClass3.getSuperclass()) {
/* 1160 */         if (str.equals(localClass3.getName())) {
/* 1161 */           localObject2 = localClass3;
/* 1162 */           break;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1167 */       if (localObject2 != null) {
/* 1168 */         for (localClass3 = localClass1; localClass3 != localObject2; localClass3 = localClass3.getSuperclass()) {
/* 1169 */           localArrayList.add(new ClassDataSlot(lookup(localClass3, true), false));
/*      */         }
/*      */ 
/* 1172 */         localClass1 = localObject2.getSuperclass();
/*      */       }
/*      */ 
/* 1176 */       localArrayList.add(new ClassDataSlot(((ObjectStreamClass)localObject1).getVariantFor(localObject2), true));
/*      */     }
/*      */ 
/* 1180 */     for (localObject1 = localClass1; localObject1 != localClass2; localObject1 = ((Class)localObject1).getSuperclass()) {
/* 1181 */       localArrayList.add(new ClassDataSlot(lookup((Class)localObject1, true), false));
/*      */     }
/*      */ 
/* 1186 */     Collections.reverse(localArrayList);
/* 1187 */     return (ClassDataSlot[])localArrayList.toArray(new ClassDataSlot[localArrayList.size()]);
/*      */   }
/*      */ 
/*      */   int getPrimDataSize()
/*      */   {
/* 1195 */     return this.primDataSize;
/*      */   }
/*      */ 
/*      */   int getNumObjFields()
/*      */   {
/* 1203 */     return this.numObjFields;
/*      */   }
/*      */ 
/*      */   void getPrimFieldValues(Object paramObject, byte[] paramArrayOfByte)
/*      */   {
/* 1213 */     this.fieldRefl.getPrimFieldValues(paramObject, paramArrayOfByte);
/*      */   }
/*      */ 
/*      */   void setPrimFieldValues(Object paramObject, byte[] paramArrayOfByte)
/*      */   {
/* 1223 */     this.fieldRefl.setPrimFieldValues(paramObject, paramArrayOfByte);
/*      */   }
/*      */ 
/*      */   void getObjFieldValues(Object paramObject, Object[] paramArrayOfObject)
/*      */   {
/* 1232 */     this.fieldRefl.getObjFieldValues(paramObject, paramArrayOfObject);
/*      */   }
/*      */ 
/*      */   void setObjFieldValues(Object paramObject, Object[] paramArrayOfObject)
/*      */   {
/* 1241 */     this.fieldRefl.setObjFieldValues(paramObject, paramArrayOfObject);
/*      */   }
/*      */ 
/*      */   private void computeFieldOffsets()
/*      */     throws InvalidClassException
/*      */   {
/* 1250 */     this.primDataSize = 0;
/* 1251 */     this.numObjFields = 0;
/* 1252 */     int i = -1;
/*      */ 
/* 1254 */     for (int j = 0; j < this.fields.length; j++) {
/* 1255 */       ObjectStreamField localObjectStreamField = this.fields[j];
/* 1256 */       switch (localObjectStreamField.getTypeCode()) {
/*      */       case 'B':
/*      */       case 'Z':
/* 1259 */         localObjectStreamField.setOffset(this.primDataSize++);
/* 1260 */         break;
/*      */       case 'C':
/*      */       case 'S':
/* 1264 */         localObjectStreamField.setOffset(this.primDataSize);
/* 1265 */         this.primDataSize += 2;
/* 1266 */         break;
/*      */       case 'F':
/*      */       case 'I':
/* 1270 */         localObjectStreamField.setOffset(this.primDataSize);
/* 1271 */         this.primDataSize += 4;
/* 1272 */         break;
/*      */       case 'D':
/*      */       case 'J':
/* 1276 */         localObjectStreamField.setOffset(this.primDataSize);
/* 1277 */         this.primDataSize += 8;
/* 1278 */         break;
/*      */       case 'L':
/*      */       case '[':
/* 1282 */         localObjectStreamField.setOffset(this.numObjFields++);
/* 1283 */         if (i == -1)
/* 1284 */           i = j; break;
/*      */       case 'E':
/*      */       case 'G':
/*      */       case 'H':
/*      */       case 'K':
/*      */       case 'M':
/*      */       case 'N':
/*      */       case 'O':
/*      */       case 'P':
/*      */       case 'Q':
/*      */       case 'R':
/*      */       case 'T':
/*      */       case 'U':
/*      */       case 'V':
/*      */       case 'W':
/*      */       case 'X':
/*      */       case 'Y':
/*      */       default:
/* 1289 */         throw new InternalError();
/*      */       }
/*      */     }
/* 1292 */     if ((i != -1) && (i + this.numObjFields != this.fields.length))
/*      */     {
/* 1295 */       throw new InvalidClassException(this.name, "illegal field order");
/*      */     }
/*      */   }
/*      */ 
/*      */   private ObjectStreamClass getVariantFor(Class<?> paramClass)
/*      */     throws InvalidClassException
/*      */   {
/* 1307 */     if (this.cl == paramClass) {
/* 1308 */       return this;
/*      */     }
/* 1310 */     ObjectStreamClass localObjectStreamClass = new ObjectStreamClass();
/* 1311 */     if (this.isProxy)
/* 1312 */       localObjectStreamClass.initProxy(paramClass, null, this.superDesc);
/*      */     else {
/* 1314 */       localObjectStreamClass.initNonProxy(this, paramClass, null, this.superDesc);
/*      */     }
/* 1316 */     return localObjectStreamClass;
/*      */   }
/*      */ 
/*      */   private static Constructor getExternalizableConstructor(Class<?> paramClass)
/*      */   {
/*      */     try
/*      */     {
/* 1326 */       Constructor localConstructor = paramClass.getDeclaredConstructor((Class[])null);
/* 1327 */       localConstructor.setAccessible(true);
/* 1328 */       return (localConstructor.getModifiers() & 0x1) != 0 ? localConstructor : null;
/*      */     } catch (NoSuchMethodException localNoSuchMethodException) {
/*      */     }
/* 1331 */     return null;
/*      */   }
/*      */ 
/*      */   private static Constructor getSerializableConstructor(Class<?> paramClass)
/*      */   {
/* 1341 */     Object localObject = paramClass;
/* 1342 */     while (Serializable.class.isAssignableFrom((Class)localObject)) {
/* 1343 */       if ((localObject = ((Class)localObject).getSuperclass()) == null)
/* 1344 */         return null;
/*      */     }
/*      */     try
/*      */     {
/* 1348 */       Constructor localConstructor = ((Class)localObject).getDeclaredConstructor((Class[])null);
/* 1349 */       int i = localConstructor.getModifiers();
/* 1350 */       if (((i & 0x2) != 0) || (((i & 0x5) == 0) && (!packageEquals(paramClass, (Class)localObject))))
/*      */       {
/* 1354 */         return null;
/*      */       }
/* 1356 */       localConstructor = reflFactory.newConstructorForSerialization(paramClass, localConstructor);
/* 1357 */       localConstructor.setAccessible(true);
/* 1358 */       return localConstructor; } catch (NoSuchMethodException localNoSuchMethodException) {
/*      */     }
/* 1360 */     return null;
/*      */   }
/*      */ 
/*      */   private static Method getInheritableMethod(Class<?> paramClass1, String paramString, Class<?>[] paramArrayOfClass, Class<?> paramClass2)
/*      */   {
/* 1374 */     Method localMethod = null;
/* 1375 */     Object localObject = paramClass1;
/* 1376 */     while (localObject != null) {
/*      */       try {
/* 1378 */         localMethod = ((Class)localObject).getDeclaredMethod(paramString, paramArrayOfClass);
/*      */       }
/*      */       catch (NoSuchMethodException localNoSuchMethodException) {
/* 1381 */         localObject = ((Class)localObject).getSuperclass();
/*      */       }
/*      */     }
/*      */ 
/* 1385 */     if ((localMethod == null) || (localMethod.getReturnType() != paramClass2)) {
/* 1386 */       return null;
/*      */     }
/* 1388 */     localMethod.setAccessible(true);
/* 1389 */     int i = localMethod.getModifiers();
/* 1390 */     if ((i & 0x408) != 0)
/* 1391 */       return null;
/* 1392 */     if ((i & 0x5) != 0)
/* 1393 */       return localMethod;
/* 1394 */     if ((i & 0x2) != 0) {
/* 1395 */       return paramClass1 == localObject ? localMethod : null;
/*      */     }
/* 1397 */     return packageEquals(paramClass1, (Class)localObject) ? localMethod : null;
/*      */   }
/*      */ 
/*      */   private static Method getPrivateMethod(Class<?> paramClass1, String paramString, Class<?>[] paramArrayOfClass, Class<?> paramClass2)
/*      */   {
/*      */     try
/*      */     {
/* 1411 */       Method localMethod = paramClass1.getDeclaredMethod(paramString, paramArrayOfClass);
/* 1412 */       localMethod.setAccessible(true);
/* 1413 */       int i = localMethod.getModifiers();
/* 1414 */       return (localMethod.getReturnType() == paramClass2) && ((i & 0x8) == 0) && ((i & 0x2) != 0) ? localMethod : null;
/*      */     }
/*      */     catch (NoSuchMethodException localNoSuchMethodException) {
/*      */     }
/* 1418 */     return null;
/*      */   }
/*      */ 
/*      */   private static boolean packageEquals(Class<?> paramClass1, Class<?> paramClass2)
/*      */   {
/* 1427 */     return (paramClass1.getClassLoader() == paramClass2.getClassLoader()) && (getPackageName(paramClass1).equals(getPackageName(paramClass2)));
/*      */   }
/*      */ 
/*      */   private static String getPackageName(Class<?> paramClass)
/*      */   {
/* 1435 */     String str = paramClass.getName();
/* 1436 */     int i = str.lastIndexOf('[');
/* 1437 */     if (i >= 0) {
/* 1438 */       str = str.substring(i + 2);
/*      */     }
/* 1440 */     i = str.lastIndexOf('.');
/* 1441 */     return i >= 0 ? str.substring(0, i) : "";
/*      */   }
/*      */ 
/*      */   private static boolean classNamesEqual(String paramString1, String paramString2)
/*      */   {
/* 1449 */     paramString1 = paramString1.substring(paramString1.lastIndexOf('.') + 1);
/* 1450 */     paramString2 = paramString2.substring(paramString2.lastIndexOf('.') + 1);
/* 1451 */     return paramString1.equals(paramString2);
/*      */   }
/*      */ 
/*      */   private static String getClassSignature(Class<?> paramClass)
/*      */   {
/* 1458 */     StringBuilder localStringBuilder = new StringBuilder();
/* 1459 */     while (paramClass.isArray()) {
/* 1460 */       localStringBuilder.append('[');
/* 1461 */       paramClass = paramClass.getComponentType();
/*      */     }
/* 1463 */     if (paramClass.isPrimitive()) {
/* 1464 */       if (paramClass == Integer.TYPE)
/* 1465 */         localStringBuilder.append('I');
/* 1466 */       else if (paramClass == Byte.TYPE)
/* 1467 */         localStringBuilder.append('B');
/* 1468 */       else if (paramClass == Long.TYPE)
/* 1469 */         localStringBuilder.append('J');
/* 1470 */       else if (paramClass == Float.TYPE)
/* 1471 */         localStringBuilder.append('F');
/* 1472 */       else if (paramClass == Double.TYPE)
/* 1473 */         localStringBuilder.append('D');
/* 1474 */       else if (paramClass == Short.TYPE)
/* 1475 */         localStringBuilder.append('S');
/* 1476 */       else if (paramClass == Character.TYPE)
/* 1477 */         localStringBuilder.append('C');
/* 1478 */       else if (paramClass == Boolean.TYPE)
/* 1479 */         localStringBuilder.append('Z');
/* 1480 */       else if (paramClass == Void.TYPE)
/* 1481 */         localStringBuilder.append('V');
/*      */       else
/* 1483 */         throw new InternalError();
/*      */     }
/*      */     else {
/* 1486 */       localStringBuilder.append('L' + paramClass.getName().replace('.', '/') + ';');
/*      */     }
/* 1488 */     return localStringBuilder.toString();
/*      */   }
/*      */ 
/*      */   private static String getMethodSignature(Class<?>[] paramArrayOfClass, Class<?> paramClass)
/*      */   {
/* 1497 */     StringBuilder localStringBuilder = new StringBuilder();
/* 1498 */     localStringBuilder.append('(');
/* 1499 */     for (int i = 0; i < paramArrayOfClass.length; i++) {
/* 1500 */       localStringBuilder.append(getClassSignature(paramArrayOfClass[i]));
/*      */     }
/* 1502 */     localStringBuilder.append(')');
/* 1503 */     localStringBuilder.append(getClassSignature(paramClass));
/* 1504 */     return localStringBuilder.toString();
/*      */   }
/*      */ 
/*      */   private static void throwMiscException(Throwable paramThrowable)
/*      */     throws IOException
/*      */   {
/* 1513 */     if ((paramThrowable instanceof RuntimeException))
/* 1514 */       throw ((RuntimeException)paramThrowable);
/* 1515 */     if ((paramThrowable instanceof Error)) {
/* 1516 */       throw ((Error)paramThrowable);
/*      */     }
/* 1518 */     IOException localIOException = new IOException("unexpected exception type");
/* 1519 */     localIOException.initCause(paramThrowable);
/* 1520 */     throw localIOException;
/*      */   }
/*      */ 
/*      */   private static ObjectStreamField[] getSerialFields(Class<?> paramClass)
/*      */     throws InvalidClassException
/*      */   {
/*      */     ObjectStreamField[] arrayOfObjectStreamField;
/* 1535 */     if ((Serializable.class.isAssignableFrom(paramClass)) && (!Externalizable.class.isAssignableFrom(paramClass)) && (!Proxy.isProxyClass(paramClass)) && (!paramClass.isInterface()))
/*      */     {
/* 1540 */       if ((arrayOfObjectStreamField = getDeclaredSerialFields(paramClass)) == null) {
/* 1541 */         arrayOfObjectStreamField = getDefaultSerialFields(paramClass);
/*      */       }
/* 1543 */       Arrays.sort(arrayOfObjectStreamField);
/*      */     } else {
/* 1545 */       arrayOfObjectStreamField = NO_FIELDS;
/*      */     }
/* 1547 */     return arrayOfObjectStreamField;
/*      */   }
/*      */ 
/*      */   private static ObjectStreamField[] getDeclaredSerialFields(Class<?> paramClass)
/*      */     throws InvalidClassException
/*      */   {
/* 1564 */     ObjectStreamField[] arrayOfObjectStreamField1 = null;
/*      */     try {
/* 1566 */       Field localField1 = paramClass.getDeclaredField("serialPersistentFields");
/* 1567 */       int i = 26;
/* 1568 */       if ((localField1.getModifiers() & i) == i) {
/* 1569 */         localField1.setAccessible(true);
/* 1570 */         arrayOfObjectStreamField1 = (ObjectStreamField[])localField1.get(null);
/*      */       }
/*      */     } catch (Exception localException) {
/*      */     }
/* 1574 */     if (arrayOfObjectStreamField1 == null)
/* 1575 */       return null;
/* 1576 */     if (arrayOfObjectStreamField1.length == 0) {
/* 1577 */       return NO_FIELDS;
/*      */     }
/*      */ 
/* 1580 */     ObjectStreamField[] arrayOfObjectStreamField2 = new ObjectStreamField[arrayOfObjectStreamField1.length];
/*      */ 
/* 1582 */     HashSet localHashSet = new HashSet(arrayOfObjectStreamField1.length);
/*      */ 
/* 1584 */     for (int j = 0; j < arrayOfObjectStreamField1.length; j++) {
/* 1585 */       ObjectStreamField localObjectStreamField = arrayOfObjectStreamField1[j];
/*      */ 
/* 1587 */       String str = localObjectStreamField.getName();
/* 1588 */       if (localHashSet.contains(str)) {
/* 1589 */         throw new InvalidClassException("multiple serializable fields named " + str);
/*      */       }
/*      */ 
/* 1592 */       localHashSet.add(str);
/*      */       try
/*      */       {
/* 1595 */         Field localField2 = paramClass.getDeclaredField(str);
/* 1596 */         if ((localField2.getType() == localObjectStreamField.getType()) && ((localField2.getModifiers() & 0x8) == 0))
/*      */         {
/* 1599 */           arrayOfObjectStreamField2[j] = new ObjectStreamField(localField2, localObjectStreamField.isUnshared(), true);
/*      */         }
/*      */       }
/*      */       catch (NoSuchFieldException localNoSuchFieldException) {
/*      */       }
/* 1604 */       if (arrayOfObjectStreamField2[j] == null) {
/* 1605 */         arrayOfObjectStreamField2[j] = new ObjectStreamField(str, localObjectStreamField.getType(), localObjectStreamField.isUnshared());
/*      */       }
/*      */     }
/*      */ 
/* 1609 */     return arrayOfObjectStreamField2;
/*      */   }
/*      */ 
/*      */   private static ObjectStreamField[] getDefaultSerialFields(Class<?> paramClass)
/*      */   {
/* 1619 */     Field[] arrayOfField = paramClass.getDeclaredFields();
/* 1620 */     ArrayList localArrayList = new ArrayList();
/* 1621 */     int i = 136;
/*      */ 
/* 1623 */     for (int j = 0; j < arrayOfField.length; j++) {
/* 1624 */       if ((arrayOfField[j].getModifiers() & i) == 0) {
/* 1625 */         localArrayList.add(new ObjectStreamField(arrayOfField[j], false, true));
/*      */       }
/*      */     }
/* 1628 */     j = localArrayList.size();
/* 1629 */     return j == 0 ? NO_FIELDS : (ObjectStreamField[])localArrayList.toArray(new ObjectStreamField[j]);
/*      */   }
/*      */ 
/*      */   private static Long getDeclaredSUID(Class<?> paramClass)
/*      */   {
/*      */     try
/*      */     {
/* 1639 */       Field localField = paramClass.getDeclaredField("serialVersionUID");
/* 1640 */       int i = 24;
/* 1641 */       if ((localField.getModifiers() & i) == i) {
/* 1642 */         localField.setAccessible(true);
/* 1643 */         return Long.valueOf(localField.getLong(null));
/*      */       }
/*      */     } catch (Exception localException) {
/*      */     }
/* 1647 */     return null;
/*      */   }
/*      */ 
/*      */   private static long computeDefaultSUID(Class<?> paramClass)
/*      */   {
/* 1654 */     if ((!Serializable.class.isAssignableFrom(paramClass)) || (Proxy.isProxyClass(paramClass)))
/*      */     {
/* 1656 */       return 0L;
/*      */     }
/*      */     try
/*      */     {
/* 1660 */       ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
/* 1661 */       DataOutputStream localDataOutputStream = new DataOutputStream(localByteArrayOutputStream);
/*      */ 
/* 1663 */       localDataOutputStream.writeUTF(paramClass.getName());
/*      */ 
/* 1665 */       int i = paramClass.getModifiers() & 0x611;
/*      */ 
/* 1673 */       Method[] arrayOfMethod = paramClass.getDeclaredMethods();
/* 1674 */       if ((i & 0x200) != 0) {
/* 1675 */         i = arrayOfMethod.length > 0 ? i | 0x400 : i & 0xFFFFFBFF;
/*      */       }
/*      */ 
/* 1679 */       localDataOutputStream.writeInt(i);
/*      */ 
/* 1681 */       if (!paramClass.isArray())
/*      */       {
/* 1687 */         localObject1 = paramClass.getInterfaces();
/* 1688 */         localObject2 = new String[localObject1.length];
/* 1689 */         for (j = 0; j < localObject1.length; j++) {
/* 1690 */           localObject2[j] = localObject1[j].getName();
/*      */         }
/* 1692 */         Arrays.sort((Object[])localObject2);
/* 1693 */         for (j = 0; j < localObject2.length; j++) {
/* 1694 */           localDataOutputStream.writeUTF(localObject2[j]);
/*      */         }
/*      */       }
/*      */ 
/* 1698 */       Object localObject1 = paramClass.getDeclaredFields();
/* 1699 */       Object localObject2 = new MemberSignature[localObject1.length];
/* 1700 */       for (int j = 0; j < localObject1.length; j++) {
/* 1701 */         localObject2[j] = new MemberSignature(localObject1[j]);
/*      */       }
/* 1703 */       Arrays.sort((Object[])localObject2, new Comparator() {
/*      */         public int compare(ObjectStreamClass.MemberSignature paramAnonymousMemberSignature1, ObjectStreamClass.MemberSignature paramAnonymousMemberSignature2) {
/* 1705 */           return paramAnonymousMemberSignature1.name.compareTo(paramAnonymousMemberSignature2.name);
/*      */         }
/*      */       });
/* 1708 */       for (j = 0; j < localObject2.length; j++) {
/* 1709 */         arrayOfMemberSignature1 = localObject2[j];
/* 1710 */         k = arrayOfMemberSignature1.member.getModifiers() & 0xDF;
/*      */ 
/* 1714 */         if (((k & 0x2) == 0) || ((k & 0x88) == 0))
/*      */         {
/* 1717 */           localDataOutputStream.writeUTF(arrayOfMemberSignature1.name);
/* 1718 */           localDataOutputStream.writeInt(k);
/* 1719 */           localDataOutputStream.writeUTF(arrayOfMemberSignature1.signature);
/*      */         }
/*      */       }
/*      */ 
/* 1723 */       if (hasStaticInitializer(paramClass)) {
/* 1724 */         localDataOutputStream.writeUTF("<clinit>");
/* 1725 */         localDataOutputStream.writeInt(8);
/* 1726 */         localDataOutputStream.writeUTF("()V");
/*      */       }
/*      */ 
/* 1729 */       Constructor[] arrayOfConstructor = paramClass.getDeclaredConstructors();
/* 1730 */       MemberSignature[] arrayOfMemberSignature1 = new MemberSignature[arrayOfConstructor.length];
/* 1731 */       for (int k = 0; k < arrayOfConstructor.length; k++) {
/* 1732 */         arrayOfMemberSignature1[k] = new MemberSignature(arrayOfConstructor[k]);
/*      */       }
/* 1734 */       Arrays.sort(arrayOfMemberSignature1, new Comparator() {
/*      */         public int compare(ObjectStreamClass.MemberSignature paramAnonymousMemberSignature1, ObjectStreamClass.MemberSignature paramAnonymousMemberSignature2) {
/* 1736 */           return paramAnonymousMemberSignature1.signature.compareTo(paramAnonymousMemberSignature2.signature);
/*      */         }
/*      */       });
/* 1739 */       for (k = 0; k < arrayOfMemberSignature1.length; k++) {
/* 1740 */         MemberSignature localMemberSignature = arrayOfMemberSignature1[k];
/* 1741 */         int n = localMemberSignature.member.getModifiers() & 0xD3F;
/*      */ 
/* 1746 */         if ((n & 0x2) == 0) {
/* 1747 */           localDataOutputStream.writeUTF("<init>");
/* 1748 */           localDataOutputStream.writeInt(n);
/* 1749 */           localDataOutputStream.writeUTF(localMemberSignature.signature.replace('/', '.'));
/*      */         }
/*      */       }
/*      */ 
/* 1753 */       MemberSignature[] arrayOfMemberSignature2 = new MemberSignature[arrayOfMethod.length];
/* 1754 */       for (int m = 0; m < arrayOfMethod.length; m++) {
/* 1755 */         arrayOfMemberSignature2[m] = new MemberSignature(arrayOfMethod[m]);
/*      */       }
/* 1757 */       Arrays.sort(arrayOfMemberSignature2, new Comparator() {
/*      */         public int compare(ObjectStreamClass.MemberSignature paramAnonymousMemberSignature1, ObjectStreamClass.MemberSignature paramAnonymousMemberSignature2) {
/* 1759 */           int i = paramAnonymousMemberSignature1.name.compareTo(paramAnonymousMemberSignature2.name);
/* 1760 */           if (i == 0) {
/* 1761 */             i = paramAnonymousMemberSignature1.signature.compareTo(paramAnonymousMemberSignature2.signature);
/*      */           }
/* 1763 */           return i;
/*      */         }
/*      */       });
/* 1766 */       for (m = 0; m < arrayOfMemberSignature2.length; m++) {
/* 1767 */         localObject3 = arrayOfMemberSignature2[m];
/* 1768 */         int i1 = ((MemberSignature)localObject3).member.getModifiers() & 0xD3F;
/*      */ 
/* 1773 */         if ((i1 & 0x2) == 0) {
/* 1774 */           localDataOutputStream.writeUTF(((MemberSignature)localObject3).name);
/* 1775 */           localDataOutputStream.writeInt(i1);
/* 1776 */           localDataOutputStream.writeUTF(((MemberSignature)localObject3).signature.replace('/', '.'));
/*      */         }
/*      */       }
/*      */ 
/* 1780 */       localDataOutputStream.flush();
/*      */ 
/* 1782 */       MessageDigest localMessageDigest = MessageDigest.getInstance("SHA");
/* 1783 */       Object localObject3 = localMessageDigest.digest(localByteArrayOutputStream.toByteArray());
/* 1784 */       long l = 0L;
/* 1785 */       for (int i2 = Math.min(localObject3.length, 8) - 1; i2 >= 0; i2--) {
/* 1786 */         l = l << 8 | localObject3[i2] & 0xFF;
/*      */       }
/* 1788 */       return l;
/*      */     } catch (IOException localIOException) {
/* 1790 */       throw new InternalError();
/*      */     } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
/* 1792 */       throw new SecurityException(localNoSuchAlgorithmException.getMessage());
/*      */     }
/*      */   }
/*      */ 
/*      */   private static native boolean hasStaticInitializer(Class<?> paramClass);
/*      */ 
/*      */   private static FieldReflector getReflector(ObjectStreamField[] paramArrayOfObjectStreamField, ObjectStreamClass paramObjectStreamClass)
/*      */     throws InvalidClassException
/*      */   {
/* 2095 */     Class localClass = (paramObjectStreamClass != null) && (paramArrayOfObjectStreamField.length > 0) ? paramObjectStreamClass.cl : null;
/*      */ 
/* 2097 */     processQueue(Caches.reflectorsQueue, Caches.reflectors);
/* 2098 */     FieldReflectorKey localFieldReflectorKey = new FieldReflectorKey(localClass, paramArrayOfObjectStreamField, Caches.reflectorsQueue);
/*      */ 
/* 2100 */     Reference localReference = (Reference)Caches.reflectors.get(localFieldReflectorKey);
/* 2101 */     Object localObject1 = null;
/* 2102 */     if (localReference != null) {
/* 2103 */       localObject1 = localReference.get();
/*      */     }
/* 2105 */     Object localObject2 = null;
/* 2106 */     if (localObject1 == null) {
/* 2107 */       EntryFuture localEntryFuture = new EntryFuture(null);
/* 2108 */       SoftReference localSoftReference = new SoftReference(localEntryFuture);
/*      */       do {
/* 2110 */         if (localReference != null) {
/* 2111 */           Caches.reflectors.remove(localFieldReflectorKey, localReference);
/*      */         }
/* 2113 */         localReference = (Reference)Caches.reflectors.putIfAbsent(localFieldReflectorKey, localSoftReference);
/* 2114 */         if (localReference != null)
/* 2115 */           localObject1 = localReference.get();
/*      */       }
/* 2117 */       while ((localReference != null) && (localObject1 == null));
/* 2118 */       if (localObject1 == null) {
/* 2119 */         localObject2 = localEntryFuture;
/*      */       }
/*      */     }
/*      */ 
/* 2123 */     if ((localObject1 instanceof FieldReflector))
/* 2124 */       return (FieldReflector)localObject1;
/* 2125 */     if ((localObject1 instanceof EntryFuture)) {
/* 2126 */       localObject1 = ((EntryFuture)localObject1).get();
/* 2127 */     } else if (localObject1 == null) {
/*      */       try {
/* 2129 */         localObject1 = new FieldReflector(matchFields(paramArrayOfObjectStreamField, paramObjectStreamClass));
/*      */       } catch (Throwable localThrowable) {
/* 2131 */         localObject1 = localThrowable;
/*      */       }
/* 2133 */       localObject2.set(localObject1);
/* 2134 */       Caches.reflectors.put(localFieldReflectorKey, new SoftReference(localObject1));
/*      */     }
/*      */ 
/* 2137 */     if ((localObject1 instanceof FieldReflector))
/* 2138 */       return (FieldReflector)localObject1;
/* 2139 */     if ((localObject1 instanceof InvalidClassException))
/* 2140 */       throw ((InvalidClassException)localObject1);
/* 2141 */     if ((localObject1 instanceof RuntimeException))
/* 2142 */       throw ((RuntimeException)localObject1);
/* 2143 */     if ((localObject1 instanceof Error)) {
/* 2144 */       throw ((Error)localObject1);
/*      */     }
/* 2146 */     throw new InternalError("unexpected entry: " + localObject1);
/*      */   }
/*      */ 
/*      */   private static ObjectStreamField[] matchFields(ObjectStreamField[] paramArrayOfObjectStreamField, ObjectStreamClass paramObjectStreamClass)
/*      */     throws InvalidClassException
/*      */   {
/* 2211 */     ObjectStreamField[] arrayOfObjectStreamField1 = paramObjectStreamClass != null ? paramObjectStreamClass.fields : NO_FIELDS;
/*      */ 
/* 2225 */     ObjectStreamField[] arrayOfObjectStreamField2 = new ObjectStreamField[paramArrayOfObjectStreamField.length];
/* 2226 */     for (int i = 0; i < paramArrayOfObjectStreamField.length; i++) {
/* 2227 */       ObjectStreamField localObjectStreamField1 = paramArrayOfObjectStreamField[i]; ObjectStreamField localObjectStreamField2 = null;
/* 2228 */       for (int j = 0; j < arrayOfObjectStreamField1.length; j++) {
/* 2229 */         ObjectStreamField localObjectStreamField3 = arrayOfObjectStreamField1[j];
/* 2230 */         if (localObjectStreamField1.getName().equals(localObjectStreamField3.getName())) {
/* 2231 */           if (((localObjectStreamField1.isPrimitive()) || (localObjectStreamField3.isPrimitive())) && (localObjectStreamField1.getTypeCode() != localObjectStreamField3.getTypeCode()))
/*      */           {
/* 2234 */             throw new InvalidClassException(paramObjectStreamClass.name, "incompatible types for field " + localObjectStreamField1.getName());
/*      */           }
/*      */ 
/* 2237 */           if (localObjectStreamField3.getField() != null) {
/* 2238 */             localObjectStreamField2 = new ObjectStreamField(localObjectStreamField3.getField(), localObjectStreamField3.isUnshared(), false);
/*      */           }
/*      */           else {
/* 2241 */             localObjectStreamField2 = new ObjectStreamField(localObjectStreamField3.getName(), localObjectStreamField3.getSignature(), localObjectStreamField3.isUnshared());
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 2246 */       if (localObjectStreamField2 == null) {
/* 2247 */         localObjectStreamField2 = new ObjectStreamField(localObjectStreamField1.getName(), localObjectStreamField1.getSignature(), false);
/*      */       }
/*      */ 
/* 2250 */       localObjectStreamField2.setOffset(localObjectStreamField1.getOffset());
/* 2251 */       arrayOfObjectStreamField2[i] = localObjectStreamField2;
/*      */     }
/* 2253 */     return arrayOfObjectStreamField2;
/*      */   }
/*      */ 
/*      */   static void processQueue(ReferenceQueue<Class<?>> paramReferenceQueue, ConcurrentMap<? extends WeakReference<Class<?>>, ?> paramConcurrentMap)
/*      */   {
/*      */     Reference localReference;
/* 2265 */     while ((localReference = paramReferenceQueue.poll()) != null)
/* 2266 */       paramConcurrentMap.remove(localReference);
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  194 */     initNative();
/*      */   }
/*      */ 
/*      */   private static class Caches
/*      */   {
/*   86 */     static final ConcurrentMap<ObjectStreamClass.WeakClassKey, Reference<?>> localDescs = new ConcurrentHashMap();
/*      */ 
/*   90 */     static final ConcurrentMap<ObjectStreamClass.FieldReflectorKey, Reference<?>> reflectors = new ConcurrentHashMap();
/*      */ 
/*   94 */     private static final ReferenceQueue<Class<?>> localDescsQueue = new ReferenceQueue();
/*      */ 
/*   97 */     private static final ReferenceQueue<Class<?>> reflectorsQueue = new ReferenceQueue();
/*      */   }
/*      */ 
/*      */   static class ClassDataSlot
/*      */   {
/*      */     final ObjectStreamClass desc;
/*      */     final boolean hasData;
/*      */ 
/*      */     ClassDataSlot(ObjectStreamClass paramObjectStreamClass, boolean paramBoolean)
/*      */     {
/* 1123 */       this.desc = paramObjectStreamClass;
/* 1124 */       this.hasData = paramBoolean;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class EntryFuture
/*      */   {
/*  385 */     private static final Object unset = new Object();
/*  386 */     private final Thread owner = Thread.currentThread();
/*  387 */     private Object entry = unset;
/*      */ 
/*      */     synchronized boolean set(Object paramObject)
/*      */     {
/*  397 */       if (this.entry != unset) {
/*  398 */         return false;
/*      */       }
/*  400 */       this.entry = paramObject;
/*  401 */       notifyAll();
/*  402 */       return true;
/*      */     }
/*      */ 
/*      */     synchronized Object get()
/*      */     {
/*  410 */       int i = 0;
/*  411 */       while (this.entry == unset) {
/*      */         try {
/*  413 */           wait();
/*      */         } catch (InterruptedException localInterruptedException) {
/*  415 */           i = 1;
/*      */         }
/*      */       }
/*  418 */       if (i != 0) {
/*  419 */         AccessController.doPrivileged(new PrivilegedAction()
/*      */         {
/*      */           public Void run() {
/*  422 */             Thread.currentThread().interrupt();
/*  423 */             return null;
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*  428 */       return this.entry;
/*      */     }
/*      */ 
/*      */     Thread getOwner()
/*      */     {
/*  435 */       return this.owner;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class ExceptionInfo
/*      */   {
/*      */     private final String className;
/*      */     private final String message;
/*      */ 
/*      */     ExceptionInfo(String paramString1, String paramString2)
/*      */     {
/*  137 */       this.className = paramString1;
/*  138 */       this.message = paramString2;
/*      */     }
/*      */ 
/*      */     InvalidClassException newInvalidClassException()
/*      */     {
/*  147 */       return new InvalidClassException(this.className, this.message);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class FieldReflector
/*      */   {
/* 1840 */     private static final Unsafe unsafe = Unsafe.getUnsafe();
/*      */     private final ObjectStreamField[] fields;
/*      */     private final int numPrimFields;
/*      */     private final long[] readKeys;
/*      */     private final long[] writeKeys;
/*      */     private final int[] offsets;
/*      */     private final char[] typeCodes;
/*      */     private final Class<?>[] types;
/*      */ 
/*      */     FieldReflector(ObjectStreamField[] paramArrayOfObjectStreamField)
/*      */     {
/* 1865 */       this.fields = paramArrayOfObjectStreamField;
/* 1866 */       int i = paramArrayOfObjectStreamField.length;
/* 1867 */       this.readKeys = new long[i];
/* 1868 */       this.writeKeys = new long[i];
/* 1869 */       this.offsets = new int[i];
/* 1870 */       this.typeCodes = new char[i];
/* 1871 */       ArrayList localArrayList = new ArrayList();
/* 1872 */       HashSet localHashSet = new HashSet();
/*      */ 
/* 1875 */       for (int j = 0; j < i; j++) {
/* 1876 */         ObjectStreamField localObjectStreamField = paramArrayOfObjectStreamField[j];
/* 1877 */         Field localField = localObjectStreamField.getField();
/* 1878 */         long l = localField != null ? unsafe.objectFieldOffset(localField) : -1L;
/*      */ 
/* 1880 */         this.readKeys[j] = l;
/* 1881 */         this.writeKeys[j] = (localHashSet.add(Long.valueOf(l)) ? l : -1L);
/*      */ 
/* 1883 */         this.offsets[j] = localObjectStreamField.getOffset();
/* 1884 */         this.typeCodes[j] = localObjectStreamField.getTypeCode();
/* 1885 */         if (!localObjectStreamField.isPrimitive()) {
/* 1886 */           localArrayList.add(localField != null ? localField.getType() : null);
/*      */         }
/*      */       }
/*      */ 
/* 1890 */       this.types = ((Class[])localArrayList.toArray(new Class[localArrayList.size()]));
/* 1891 */       this.numPrimFields = (i - this.types.length);
/*      */     }
/*      */ 
/*      */     ObjectStreamField[] getFields()
/*      */     {
/* 1901 */       return this.fields;
/*      */     }
/*      */ 
/*      */     void getPrimFieldValues(Object paramObject, byte[] paramArrayOfByte)
/*      */     {
/* 1910 */       if (paramObject == null) {
/* 1911 */         throw new NullPointerException();
/*      */       }
/*      */ 
/* 1917 */       for (int i = 0; i < this.numPrimFields; i++) {
/* 1918 */         long l = this.readKeys[i];
/* 1919 */         int j = this.offsets[i];
/* 1920 */         switch (this.typeCodes[i]) {
/*      */         case 'Z':
/* 1922 */           Bits.putBoolean(paramArrayOfByte, j, unsafe.getBoolean(paramObject, l));
/* 1923 */           break;
/*      */         case 'B':
/* 1926 */           paramArrayOfByte[j] = unsafe.getByte(paramObject, l);
/* 1927 */           break;
/*      */         case 'C':
/* 1930 */           Bits.putChar(paramArrayOfByte, j, unsafe.getChar(paramObject, l));
/* 1931 */           break;
/*      */         case 'S':
/* 1934 */           Bits.putShort(paramArrayOfByte, j, unsafe.getShort(paramObject, l));
/* 1935 */           break;
/*      */         case 'I':
/* 1938 */           Bits.putInt(paramArrayOfByte, j, unsafe.getInt(paramObject, l));
/* 1939 */           break;
/*      */         case 'F':
/* 1942 */           Bits.putFloat(paramArrayOfByte, j, unsafe.getFloat(paramObject, l));
/* 1943 */           break;
/*      */         case 'J':
/* 1946 */           Bits.putLong(paramArrayOfByte, j, unsafe.getLong(paramObject, l));
/* 1947 */           break;
/*      */         case 'D':
/* 1950 */           Bits.putDouble(paramArrayOfByte, j, unsafe.getDouble(paramObject, l));
/* 1951 */           break;
/*      */         case 'E':
/*      */         case 'G':
/*      */         case 'H':
/*      */         case 'K':
/*      */         case 'L':
/*      */         case 'M':
/*      */         case 'N':
/*      */         case 'O':
/*      */         case 'P':
/*      */         case 'Q':
/*      */         case 'R':
/*      */         case 'T':
/*      */         case 'U':
/*      */         case 'V':
/*      */         case 'W':
/*      */         case 'X':
/*      */         case 'Y':
/*      */         default:
/* 1954 */           throw new InternalError();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     void setPrimFieldValues(Object paramObject, byte[] paramArrayOfByte)
/*      */     {
/* 1965 */       if (paramObject == null) {
/* 1966 */         throw new NullPointerException();
/*      */       }
/* 1968 */       for (int i = 0; i < this.numPrimFields; i++) {
/* 1969 */         long l = this.writeKeys[i];
/* 1970 */         if (l != -1L)
/*      */         {
/* 1973 */           int j = this.offsets[i];
/* 1974 */           switch (this.typeCodes[i]) {
/*      */           case 'Z':
/* 1976 */             unsafe.putBoolean(paramObject, l, Bits.getBoolean(paramArrayOfByte, j));
/* 1977 */             break;
/*      */           case 'B':
/* 1980 */             unsafe.putByte(paramObject, l, paramArrayOfByte[j]);
/* 1981 */             break;
/*      */           case 'C':
/* 1984 */             unsafe.putChar(paramObject, l, Bits.getChar(paramArrayOfByte, j));
/* 1985 */             break;
/*      */           case 'S':
/* 1988 */             unsafe.putShort(paramObject, l, Bits.getShort(paramArrayOfByte, j));
/* 1989 */             break;
/*      */           case 'I':
/* 1992 */             unsafe.putInt(paramObject, l, Bits.getInt(paramArrayOfByte, j));
/* 1993 */             break;
/*      */           case 'F':
/* 1996 */             unsafe.putFloat(paramObject, l, Bits.getFloat(paramArrayOfByte, j));
/* 1997 */             break;
/*      */           case 'J':
/* 2000 */             unsafe.putLong(paramObject, l, Bits.getLong(paramArrayOfByte, j));
/* 2001 */             break;
/*      */           case 'D':
/* 2004 */             unsafe.putDouble(paramObject, l, Bits.getDouble(paramArrayOfByte, j));
/* 2005 */             break;
/*      */           case 'E':
/*      */           case 'G':
/*      */           case 'H':
/*      */           case 'K':
/*      */           case 'L':
/*      */           case 'M':
/*      */           case 'N':
/*      */           case 'O':
/*      */           case 'P':
/*      */           case 'Q':
/*      */           case 'R':
/*      */           case 'T':
/*      */           case 'U':
/*      */           case 'V':
/*      */           case 'W':
/*      */           case 'X':
/*      */           case 'Y':
/*      */           default:
/* 2008 */             throw new InternalError();
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     void getObjFieldValues(Object paramObject, Object[] paramArrayOfObject)
/*      */     {
/* 2019 */       if (paramObject == null) {
/* 2020 */         throw new NullPointerException();
/*      */       }
/*      */ 
/* 2026 */       for (int i = this.numPrimFields; i < this.fields.length; i++)
/* 2027 */         switch (this.typeCodes[i]) {
/*      */         case 'L':
/*      */         case '[':
/* 2030 */           paramArrayOfObject[this.offsets[i]] = unsafe.getObject(paramObject, this.readKeys[i]);
/* 2031 */           break;
/*      */         default:
/* 2034 */           throw new InternalError();
/*      */         }
/*      */     }
/*      */ 
/*      */     void setObjFieldValues(Object paramObject, Object[] paramArrayOfObject)
/*      */     {
/* 2047 */       if (paramObject == null) {
/* 2048 */         throw new NullPointerException();
/*      */       }
/* 2050 */       for (int i = this.numPrimFields; i < this.fields.length; i++) {
/* 2051 */         long l = this.writeKeys[i];
/* 2052 */         if (l != -1L)
/*      */         {
/* 2055 */           switch (this.typeCodes[i]) {
/*      */           case 'L':
/*      */           case '[':
/* 2058 */             Object localObject = paramArrayOfObject[this.offsets[i]];
/* 2059 */             if ((localObject != null) && (!this.types[(i - this.numPrimFields)].isInstance(localObject)))
/*      */             {
/* 2062 */               Field localField = this.fields[i].getField();
/* 2063 */               throw new ClassCastException("cannot assign instance of " + localObject.getClass().getName() + " to field " + localField.getDeclaringClass().getName() + "." + localField.getName() + " of type " + localField.getType().getName() + " in instance of " + paramObject.getClass().getName());
/*      */             }
/*      */ 
/* 2071 */             unsafe.putObject(paramObject, l, localObject);
/* 2072 */             break;
/*      */           default:
/* 2075 */             throw new InternalError();
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class FieldReflectorKey extends WeakReference<Class<?>>
/*      */   {
/*      */     private final String sigs;
/*      */     private final int hash;
/*      */     private final boolean nullClass;
/*      */ 
/*      */     FieldReflectorKey(Class<?> paramClass, ObjectStreamField[] paramArrayOfObjectStreamField, ReferenceQueue<Class<?>> paramReferenceQueue)
/*      */     {
/* 2163 */       super(paramReferenceQueue);
/* 2164 */       this.nullClass = (paramClass == null);
/* 2165 */       StringBuilder localStringBuilder = new StringBuilder();
/* 2166 */       for (int i = 0; i < paramArrayOfObjectStreamField.length; i++) {
/* 2167 */         ObjectStreamField localObjectStreamField = paramArrayOfObjectStreamField[i];
/* 2168 */         localStringBuilder.append(localObjectStreamField.getName()).append(localObjectStreamField.getSignature());
/*      */       }
/* 2170 */       this.sigs = localStringBuilder.toString();
/* 2171 */       this.hash = (System.identityHashCode(paramClass) + this.sigs.hashCode());
/*      */     }
/*      */ 
/*      */     public int hashCode() {
/* 2175 */       return this.hash;
/*      */     }
/*      */ 
/*      */     public boolean equals(Object paramObject) {
/* 2179 */       if (paramObject == this) {
/* 2180 */         return true;
/*      */       }
/*      */ 
/* 2183 */       if ((paramObject instanceof FieldReflectorKey)) {
/* 2184 */         FieldReflectorKey localFieldReflectorKey = (FieldReflectorKey)paramObject;
/*      */         Class localClass;
/* 2186 */         return (this.nullClass ? localFieldReflectorKey.nullClass : ((localClass = (Class)get()) != null) && (localClass == localFieldReflectorKey.get())) && (this.sigs.equals(localFieldReflectorKey.sigs));
/*      */       }
/*      */ 
/* 2191 */       return false;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class MemberSignature
/*      */   {
/*      */     public final Member member;
/*      */     public final String name;
/*      */     public final String signature;
/*      */ 
/*      */     public MemberSignature(Field paramField)
/*      */     {
/* 1813 */       this.member = paramField;
/* 1814 */       this.name = paramField.getName();
/* 1815 */       this.signature = ObjectStreamClass.getClassSignature(paramField.getType());
/*      */     }
/*      */ 
/*      */     public MemberSignature(Constructor paramConstructor) {
/* 1819 */       this.member = paramConstructor;
/* 1820 */       this.name = paramConstructor.getName();
/* 1821 */       this.signature = ObjectStreamClass.getMethodSignature(paramConstructor.getParameterTypes(), Void.TYPE);
/*      */     }
/*      */ 
/*      */     public MemberSignature(Method paramMethod)
/*      */     {
/* 1826 */       this.member = paramMethod;
/* 1827 */       this.name = paramMethod.getName();
/* 1828 */       this.signature = ObjectStreamClass.getMethodSignature(paramMethod.getParameterTypes(), paramMethod.getReturnType());
/*      */     }
/*      */   }
/*      */ 
/*      */   static class WeakClassKey extends WeakReference<Class<?>>
/*      */   {
/*      */     private final int hash;
/*      */ 
/*      */     WeakClassKey(Class<?> paramClass, ReferenceQueue<Class<?>> paramReferenceQueue)
/*      */     {
/* 2286 */       super(paramReferenceQueue);
/* 2287 */       this.hash = System.identityHashCode(paramClass);
/*      */     }
/*      */ 
/*      */     public int hashCode()
/*      */     {
/* 2294 */       return this.hash;
/*      */     }
/*      */ 
/*      */     public boolean equals(Object paramObject)
/*      */     {
/* 2304 */       if (paramObject == this) {
/* 2305 */         return true;
/*      */       }
/*      */ 
/* 2308 */       if ((paramObject instanceof WeakClassKey)) {
/* 2309 */         Object localObject = get();
/* 2310 */         return (localObject != null) && (localObject == ((WeakClassKey)paramObject).get());
/*      */       }
/*      */ 
/* 2313 */       return false;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.io.ObjectStreamClass
 * JD-Core Version:    0.6.2
 */