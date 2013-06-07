/*      */ package com.sun.corba.se.impl.io;
/*      */ 
/*      */ import com.sun.corba.se.impl.util.RepositoryId;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.DataOutputStream;
/*      */ import java.io.Externalizable;
/*      */ import java.io.IOException;
/*      */ import java.io.InvalidClassException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.io.Serializable;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.Field;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Member;
/*      */ import java.lang.reflect.Method;
/*      */ import java.lang.reflect.Modifier;
/*      */ import java.lang.reflect.Proxy;
/*      */ import java.security.AccessController;
/*      */ import java.security.DigestOutputStream;
/*      */ import java.security.MessageDigest;
/*      */ import java.security.NoSuchAlgorithmException;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.util.Arrays;
/*      */ import java.util.Comparator;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ import java.util.concurrent.ConcurrentMap;
/*      */ import org.omg.CORBA.ValueMember;
/*      */ import sun.corba.Bridge;
/*      */ 
/*      */ public class ObjectStreamClass
/*      */   implements Serializable
/*      */ {
/*      */   private static final boolean DEBUG_SVUID = false;
/*      */   public static final long kDefaultUID = -1L;
/*   84 */   private static Object[] noArgsList = new Object[0];
/*   85 */   private static Class[] noTypesList = new Class[0];
/*      */   private boolean isEnum;
/*   90 */   private static final Bridge bridge = (Bridge)AccessController.doPrivileged(new PrivilegedAction()
/*      */   {
/*      */     public Object run()
/*      */     {
/*   94 */       return Bridge.get();
/*      */     }
/*      */   });
/*      */ 
/*  432 */   private static final PersistentFieldsValue persistentFieldsValue = new PersistentFieldsValue();
/*      */   public static final int CLASS_MASK = 1553;
/*      */   public static final int FIELD_MASK = 223;
/*      */   public static final int METHOD_MASK = 3391;
/* 1403 */   private static ObjectStreamClassEntry[] descriptorFor = new ObjectStreamClassEntry[61];
/*      */   private String name;
/*      */   private ObjectStreamClass superclass;
/*      */   private boolean serializable;
/*      */   private boolean externalizable;
/*      */   private ObjectStreamField[] fields;
/*      */   private Class ofClass;
/*      */   boolean forProxyClass;
/* 1504 */   private long suid = -1L;
/* 1505 */   private String suidStr = null;
/*      */ 
/* 1510 */   private long actualSuid = -1L;
/* 1511 */   private String actualSuidStr = null;
/*      */   int primBytes;
/*      */   int objFields;
/* 1526 */   private boolean initialized = false;
/*      */ 
/* 1529 */   private Object lock = new Object();
/*      */   private boolean hasExternalizableBlockData;
/*      */   Method writeObjectMethod;
/*      */   Method readObjectMethod;
/*      */   private transient Method writeReplaceObjectMethod;
/*      */   private transient Method readResolveObjectMethod;
/*      */   private Constructor cons;
/* 1552 */   private String rmiiiopOptionalDataRepId = null;
/*      */   private ObjectStreamClass localClassDesc;
/* 1560 */   private static Method hasStaticInitializerMethod = null;
/*      */ 
/* 1614 */   private static Class classSerializable = null;
/* 1615 */   private static Class classExternalizable = null;
/*      */   private static final long serialVersionUID = -6120832682080437368L;
/* 1636 */   public static final ObjectStreamField[] NO_FIELDS = new ObjectStreamField[0];
/*      */ 
/* 1661 */   private static Comparator compareClassByName = new CompareClassByName(null);
/*      */ 
/* 1675 */   private static final Comparator compareObjStrFieldsByName = new CompareObjStrFieldsByName(null);
/*      */ 
/* 1690 */   private static Comparator compareMemberByName = new CompareMemberByName(null);
/*      */ 
/*      */   static final ObjectStreamClass lookup(Class paramClass)
/*      */   {
/*  105 */     ObjectStreamClass localObjectStreamClass = lookupInternal(paramClass);
/*  106 */     if ((localObjectStreamClass.isSerializable()) || (localObjectStreamClass.isExternalizable()))
/*  107 */       return localObjectStreamClass;
/*  108 */     return null;
/*      */   }
/*      */ 
/*      */   static ObjectStreamClass lookupInternal(Class paramClass)
/*      */   {
/*  120 */     ObjectStreamClass localObjectStreamClass1 = null;
/*  121 */     synchronized (descriptorFor)
/*      */     {
/*  123 */       localObjectStreamClass1 = findDescriptorFor(paramClass);
/*  124 */       if (localObjectStreamClass1 == null)
/*      */       {
/*  126 */         boolean bool1 = classSerializable.isAssignableFrom(paramClass);
/*      */ 
/*  131 */         ObjectStreamClass localObjectStreamClass2 = null;
/*  132 */         if (bool1) {
/*  133 */           Class localClass = paramClass.getSuperclass();
/*  134 */           if (localClass != null) {
/*  135 */             localObjectStreamClass2 = lookup(localClass);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  142 */         boolean bool2 = false;
/*  143 */         if (bool1) {
/*  144 */           bool2 = ((localObjectStreamClass2 != null) && (localObjectStreamClass2.isExternalizable())) || (classExternalizable.isAssignableFrom(paramClass));
/*      */ 
/*  147 */           if (bool2) {
/*  148 */             bool1 = false;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  155 */         localObjectStreamClass1 = new ObjectStreamClass(paramClass, localObjectStreamClass2, bool1, bool2);
/*      */       }
/*      */ 
/*  172 */       localObjectStreamClass1.init();
/*      */     }
/*  174 */     return localObjectStreamClass1;
/*      */   }
/*      */ 
/*      */   public final String getName()
/*      */   {
/*  181 */     return this.name;
/*      */   }
/*      */ 
/*      */   public static final long getSerialVersionUID(Class paramClass)
/*      */   {
/*  191 */     ObjectStreamClass localObjectStreamClass = lookup(paramClass);
/*  192 */     if (localObjectStreamClass != null)
/*      */     {
/*  194 */       return localObjectStreamClass.getSerialVersionUID();
/*      */     }
/*  196 */     return 0L;
/*      */   }
/*      */ 
/*      */   public final long getSerialVersionUID()
/*      */   {
/*  206 */     return this.suid;
/*      */   }
/*      */ 
/*      */   public final String getSerialVersionUIDStr()
/*      */   {
/*  216 */     if (this.suidStr == null)
/*  217 */       this.suidStr = Long.toHexString(this.suid).toUpperCase();
/*  218 */     return this.suidStr;
/*      */   }
/*      */ 
/*      */   public static final long getActualSerialVersionUID(Class paramClass)
/*      */   {
/*  226 */     ObjectStreamClass localObjectStreamClass = lookup(paramClass);
/*  227 */     if (localObjectStreamClass != null)
/*      */     {
/*  229 */       return localObjectStreamClass.getActualSerialVersionUID();
/*      */     }
/*  231 */     return 0L;
/*      */   }
/*      */ 
/*      */   public final long getActualSerialVersionUID()
/*      */   {
/*  238 */     return this.actualSuid;
/*      */   }
/*      */ 
/*      */   public final String getActualSerialVersionUIDStr()
/*      */   {
/*  245 */     if (this.actualSuidStr == null)
/*  246 */       this.actualSuidStr = Long.toHexString(this.actualSuid).toUpperCase();
/*  247 */     return this.actualSuidStr;
/*      */   }
/*      */ 
/*      */   public final Class forClass()
/*      */   {
/*  255 */     return this.ofClass;
/*      */   }
/*      */ 
/*      */   public ObjectStreamField[] getFields()
/*      */   {
/*  267 */     if (this.fields.length > 0) {
/*  268 */       ObjectStreamField[] arrayOfObjectStreamField = new ObjectStreamField[this.fields.length];
/*  269 */       System.arraycopy(this.fields, 0, arrayOfObjectStreamField, 0, this.fields.length);
/*  270 */       return arrayOfObjectStreamField;
/*      */     }
/*  272 */     return this.fields;
/*      */   }
/*      */ 
/*      */   public boolean hasField(ValueMember paramValueMember)
/*      */   {
/*      */     try
/*      */     {
/*  279 */       for (int i = 0; i < this.fields.length; i++) {
/*  280 */         if ((this.fields[i].getName().equals(paramValueMember.name)) && 
/*  281 */           (this.fields[i].getSignature().equals(ValueUtility.getSignature(paramValueMember))))
/*      */         {
/*  283 */           return true;
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Exception localException)
/*      */     {
/*      */     }
/*      */ 
/*  291 */     return false;
/*      */   }
/*      */ 
/*      */   final ObjectStreamField[] getFieldsNoCopy()
/*      */   {
/*  296 */     return this.fields;
/*      */   }
/*      */ 
/*      */   public final ObjectStreamField getField(String paramString)
/*      */   {
/*  307 */     for (int i = this.fields.length - 1; i >= 0; i--) {
/*  308 */       if (paramString.equals(this.fields[i].getName())) {
/*  309 */         return this.fields[i];
/*      */       }
/*      */     }
/*  312 */     return null;
/*      */   }
/*      */ 
/*      */   public Serializable writeReplace(Serializable paramSerializable) {
/*  316 */     if (this.writeReplaceObjectMethod != null) {
/*      */       try {
/*  318 */         return (Serializable)this.writeReplaceObjectMethod.invoke(paramSerializable, noArgsList);
/*      */       } catch (Throwable localThrowable) {
/*  320 */         throw new RuntimeException(localThrowable);
/*      */       }
/*      */     }
/*  323 */     return paramSerializable;
/*      */   }
/*      */ 
/*      */   public Object readResolve(Object paramObject) {
/*  327 */     if (this.readResolveObjectMethod != null) {
/*      */       try {
/*  329 */         return this.readResolveObjectMethod.invoke(paramObject, noArgsList);
/*      */       } catch (Throwable localThrowable) {
/*  331 */         throw new RuntimeException(localThrowable);
/*      */       }
/*      */     }
/*  334 */     return paramObject;
/*      */   }
/*      */ 
/*      */   public final String toString()
/*      */   {
/*  341 */     StringBuffer localStringBuffer = new StringBuffer();
/*      */ 
/*  343 */     localStringBuffer.append(this.name);
/*  344 */     localStringBuffer.append(": static final long serialVersionUID = ");
/*  345 */     localStringBuffer.append(Long.toString(this.suid));
/*  346 */     localStringBuffer.append("L;");
/*  347 */     return localStringBuffer.toString();
/*      */   }
/*      */ 
/*      */   private ObjectStreamClass(Class paramClass, ObjectStreamClass paramObjectStreamClass, boolean paramBoolean1, boolean paramBoolean2)
/*      */   {
/*  357 */     this.ofClass = paramClass;
/*      */ 
/*  359 */     if (Proxy.isProxyClass(paramClass)) {
/*  360 */       this.forProxyClass = true;
/*      */     }
/*      */ 
/*  363 */     this.name = paramClass.getName();
/*  364 */     this.isEnum = Enum.class.isAssignableFrom(paramClass);
/*  365 */     this.superclass = paramObjectStreamClass;
/*  366 */     this.serializable = paramBoolean1;
/*  367 */     if (!this.forProxyClass)
/*      */     {
/*  369 */       this.externalizable = paramBoolean2;
/*      */     }
/*      */ 
/*  377 */     insertDescriptorFor(this);
/*      */   }
/*      */ 
/*      */   private void init()
/*      */   {
/*  445 */     synchronized (this.lock)
/*      */     {
/*  448 */       if (this.initialized) {
/*  449 */         return;
/*      */       }
/*  451 */       final Class localClass = this.ofClass;
/*      */ 
/*  453 */       if ((!this.serializable) || (this.externalizable) || (this.forProxyClass) || (this.name.equals("java.lang.String")))
/*      */       {
/*  457 */         this.fields = NO_FIELDS;
/*  458 */       } else if (this.serializable)
/*      */       {
/*  461 */         AccessController.doPrivileged(new PrivilegedAction()
/*      */         {
/*      */           public Object run()
/*      */           {
/*  467 */             ObjectStreamClass.this.fields = ObjectStreamClass.persistentFieldsValue.get(localClass);
/*      */ 
/*  469 */             if (ObjectStreamClass.this.fields == null)
/*      */             {
/*  478 */               Field[] arrayOfField = localClass.getDeclaredFields();
/*      */ 
/*  480 */               int j = 0;
/*  481 */               ObjectStreamField[] arrayOfObjectStreamField = new ObjectStreamField[arrayOfField.length];
/*      */ 
/*  483 */               for (int k = 0; k < arrayOfField.length; k++) {
/*  484 */                 Field localField2 = arrayOfField[k];
/*  485 */                 int m = localField2.getModifiers();
/*  486 */                 if ((!Modifier.isStatic(m)) && (!Modifier.isTransient(m)))
/*      */                 {
/*  488 */                   localField2.setAccessible(true);
/*  489 */                   arrayOfObjectStreamField[(j++)] = new ObjectStreamField(localField2);
/*      */                 }
/*      */               }
/*      */ 
/*  493 */               ObjectStreamClass.this.fields = new ObjectStreamField[j];
/*  494 */               System.arraycopy(arrayOfObjectStreamField, 0, ObjectStreamClass.this.fields, 0, j);
/*      */             }
/*      */             else
/*      */             {
/*  500 */               for (int i = ObjectStreamClass.this.fields.length - 1; i >= 0; i--)
/*      */                 try {
/*  502 */                   Field localField1 = localClass.getDeclaredField(ObjectStreamClass.this.fields[i].getName());
/*  503 */                   if (ObjectStreamClass.this.fields[i].getType() == localField1.getType()) {
/*  504 */                     localField1.setAccessible(true);
/*  505 */                     ObjectStreamClass.this.fields[i].setField(localField1);
/*      */                   }
/*      */                 }
/*      */                 catch (NoSuchFieldException localNoSuchFieldException)
/*      */                 {
/*      */                 }
/*      */             }
/*  512 */             return null;
/*      */           }
/*      */         });
/*  516 */         if (this.fields.length > 1) {
/*  517 */           Arrays.sort(this.fields);
/*      */         }
/*      */ 
/*  520 */         computeFieldInfo();
/*      */       }
/*      */ 
/*  529 */       if ((isNonSerializable()) || (this.isEnum)) {
/*  530 */         this.suid = 0L;
/*      */       }
/*      */       else {
/*  533 */         AccessController.doPrivileged(new PrivilegedAction() {
/*      */           public Object run() {
/*  535 */             if (ObjectStreamClass.this.forProxyClass)
/*      */             {
/*  537 */               ObjectStreamClass.this.suid = 0L;
/*      */             }
/*      */             else try {
/*  540 */                 Field localField = localClass.getDeclaredField("serialVersionUID");
/*  541 */                 int i = localField.getModifiers();
/*      */ 
/*  543 */                 if ((Modifier.isStatic(i)) && (Modifier.isFinal(i))) {
/*  544 */                   localField.setAccessible(true);
/*  545 */                   ObjectStreamClass.this.suid = localField.getLong(localClass);
/*      */                 }
/*      */                 else
/*      */                 {
/*  549 */                   ObjectStreamClass.this.suid = ObjectStreamClass._computeSerialVersionUID(localClass);
/*      */                 }
/*      */               }
/*      */               catch (NoSuchFieldException localNoSuchFieldException)
/*      */               {
/*  554 */                 ObjectStreamClass.this.suid = ObjectStreamClass._computeSerialVersionUID(localClass);
/*      */               }
/*      */               catch (IllegalAccessException localIllegalAccessException)
/*      */               {
/*  558 */                 ObjectStreamClass.this.suid = ObjectStreamClass._computeSerialVersionUID(localClass);
/*      */               }
/*      */ 
/*      */ 
/*  562 */             ObjectStreamClass.this.writeReplaceObjectMethod = ObjectStreamClass.getInheritableMethod(localClass, "writeReplace", ObjectStreamClass.noTypesList, Object.class);
/*      */ 
/*  565 */             ObjectStreamClass.this.readResolveObjectMethod = ObjectStreamClass.getInheritableMethod(localClass, "readResolve", ObjectStreamClass.noTypesList, Object.class);
/*      */ 
/*  568 */             if (ObjectStreamClass.this.externalizable)
/*  569 */               ObjectStreamClass.this.cons = ObjectStreamClass.getExternalizableConstructor(localClass);
/*      */             else {
/*  571 */               ObjectStreamClass.this.cons = ObjectStreamClass.getSerializableConstructor(localClass);
/*      */             }
/*  573 */             if ((ObjectStreamClass.this.serializable) && (!ObjectStreamClass.this.forProxyClass))
/*      */             {
/*  578 */               ObjectStreamClass.this.writeObjectMethod = ObjectStreamClass.getPrivateMethod(localClass, "writeObject", new Class[] { ObjectOutputStream.class }, Void.TYPE);
/*      */ 
/*  580 */               ObjectStreamClass.this.readObjectMethod = ObjectStreamClass.getPrivateMethod(localClass, "readObject", new Class[] { ObjectInputStream.class }, Void.TYPE);
/*      */             }
/*      */ 
/*  583 */             return null;
/*      */           }
/*      */ 
/*      */         });
/*      */       }
/*      */ 
/*  589 */       this.actualSuid = computeStructuralUID(this, localClass);
/*      */ 
/*  594 */       if (hasWriteObject()) {
/*  595 */         this.rmiiiopOptionalDataRepId = computeRMIIIOPOptionalDataRepId();
/*      */       }
/*      */ 
/*  598 */       this.initialized = true;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static Method getPrivateMethod(Class paramClass1, String paramString, Class[] paramArrayOfClass, Class paramClass2)
/*      */   {
/*      */     try
/*      */     {
/*  612 */       Method localMethod = paramClass1.getDeclaredMethod(paramString, paramArrayOfClass);
/*  613 */       localMethod.setAccessible(true);
/*  614 */       int i = localMethod.getModifiers();
/*  615 */       return (localMethod.getReturnType() == paramClass2) && ((i & 0x8) == 0) && ((i & 0x2) != 0) ? localMethod : null;
/*      */     }
/*      */     catch (NoSuchMethodException localNoSuchMethodException) {
/*      */     }
/*  619 */     return null;
/*      */   }
/*      */ 
/*      */   private String computeRMIIIOPOptionalDataRepId()
/*      */   {
/*  635 */     StringBuffer localStringBuffer = new StringBuffer("RMI:org.omg.custom.");
/*  636 */     localStringBuffer.append(RepositoryId.convertToISOLatin1(getName()));
/*  637 */     localStringBuffer.append(':');
/*  638 */     localStringBuffer.append(getActualSerialVersionUIDStr());
/*  639 */     localStringBuffer.append(':');
/*  640 */     localStringBuffer.append(getSerialVersionUIDStr());
/*      */ 
/*  642 */     return localStringBuffer.toString();
/*      */   }
/*      */ 
/*      */   public final String getRMIIIOPOptionalDataRepId()
/*      */   {
/*  649 */     return this.rmiiiopOptionalDataRepId;
/*      */   }
/*      */ 
/*      */   ObjectStreamClass(String paramString, long paramLong)
/*      */   {
/*  659 */     this.name = paramString;
/*  660 */     this.suid = paramLong;
/*  661 */     this.superclass = null;
/*      */   }
/*      */ 
/*      */   final void setClass(Class paramClass)
/*      */     throws InvalidClassException
/*      */   {
/*  672 */     if (paramClass == null) {
/*  673 */       this.localClassDesc = null;
/*  674 */       this.ofClass = null;
/*  675 */       computeFieldInfo();
/*  676 */       return;
/*      */     }
/*      */ 
/*  679 */     this.localClassDesc = lookupInternal(paramClass);
/*  680 */     if (this.localClassDesc == null)
/*      */     {
/*  682 */       throw new InvalidClassException(paramClass.getName(), "Local class not compatible");
/*      */     }
/*  684 */     if (this.suid != this.localClassDesc.suid)
/*      */     {
/*  691 */       int i = (isNonSerializable()) || (this.localClassDesc.isNonSerializable()) ? 1 : 0;
/*      */ 
/*  703 */       int j = (paramClass.isArray()) && (!paramClass.getName().equals(this.name)) ? 1 : 0;
/*      */ 
/*  705 */       if ((j == 0) && (i == 0))
/*      */       {
/*  707 */         throw new InvalidClassException(paramClass.getName(), "Local class not compatible: stream classdesc serialVersionUID=" + this.suid + " local class serialVersionUID=" + this.localClassDesc.suid);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  715 */     if (!compareClassNames(this.name, paramClass.getName(), '.'))
/*      */     {
/*  717 */       throw new InvalidClassException(paramClass.getName(), "Incompatible local class name. Expected class name compatible with " + this.name);
/*      */     }
/*      */ 
/*  734 */     if ((this.serializable != this.localClassDesc.serializable) || (this.externalizable != this.localClassDesc.externalizable) || ((!this.serializable) && (!this.externalizable)))
/*      */     {
/*  739 */       throw new InvalidClassException(paramClass.getName(), "Serialization incompatible with Externalization");
/*      */     }
/*      */ 
/*  756 */     ObjectStreamField[] arrayOfObjectStreamField1 = (ObjectStreamField[])this.localClassDesc.fields;
/*      */ 
/*  758 */     ObjectStreamField[] arrayOfObjectStreamField2 = (ObjectStreamField[])this.fields;
/*      */ 
/*  761 */     int k = 0;
/*      */ 
/*  763 */     for (int m = 0; m < arrayOfObjectStreamField2.length; m++)
/*      */     {
/*  765 */       for (int n = k; n < arrayOfObjectStreamField1.length; n++) {
/*  766 */         if (arrayOfObjectStreamField2[m].getName().equals(arrayOfObjectStreamField1[n].getName()))
/*      */         {
/*  768 */           if ((arrayOfObjectStreamField2[m].isPrimitive()) && (!arrayOfObjectStreamField2[m].typeEquals(arrayOfObjectStreamField1[n])))
/*      */           {
/*  771 */             throw new InvalidClassException(paramClass.getName(), "The type of field " + arrayOfObjectStreamField2[m].getName() + " of class " + this.name + " is incompatible.");
/*      */           }
/*      */ 
/*  779 */           k = n;
/*      */ 
/*  781 */           arrayOfObjectStreamField2[m].setField(arrayOfObjectStreamField1[k].getField());
/*      */ 
/*  783 */           break;
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  789 */     computeFieldInfo();
/*      */ 
/*  792 */     this.ofClass = paramClass;
/*      */ 
/*  797 */     this.readObjectMethod = this.localClassDesc.readObjectMethod;
/*  798 */     this.readResolveObjectMethod = this.localClassDesc.readResolveObjectMethod;
/*      */   }
/*      */ 
/*      */   static boolean compareClassNames(String paramString1, String paramString2, char paramChar)
/*      */   {
/*  814 */     int i = paramString1.lastIndexOf(paramChar);
/*  815 */     if (i < 0) {
/*  816 */       i = 0;
/*      */     }
/*  818 */     int j = paramString2.lastIndexOf(paramChar);
/*  819 */     if (j < 0) {
/*  820 */       j = 0;
/*      */     }
/*  822 */     return paramString1.regionMatches(false, i, paramString2, j, paramString1.length() - i);
/*      */   }
/*      */ 
/*      */   final boolean typeEquals(ObjectStreamClass paramObjectStreamClass)
/*      */   {
/*  832 */     return (this.suid == paramObjectStreamClass.suid) && (compareClassNames(this.name, paramObjectStreamClass.name, '.'));
/*      */   }
/*      */ 
/*      */   final void setSuperclass(ObjectStreamClass paramObjectStreamClass)
/*      */   {
/*  840 */     this.superclass = paramObjectStreamClass;
/*      */   }
/*      */ 
/*      */   final ObjectStreamClass getSuperclass()
/*      */   {
/*  847 */     return this.superclass;
/*      */   }
/*      */ 
/*      */   final boolean hasReadObject()
/*      */   {
/*  854 */     return this.readObjectMethod != null;
/*      */   }
/*      */ 
/*      */   final boolean hasWriteObject()
/*      */   {
/*  861 */     return this.writeObjectMethod != null;
/*      */   }
/*      */ 
/*      */   final boolean isCustomMarshaled()
/*      */   {
/*  871 */     return (hasWriteObject()) || (isExternalizable()) || ((this.superclass != null) && (this.superclass.isCustomMarshaled()));
/*      */   }
/*      */ 
/*      */   boolean hasExternalizableBlockDataMode()
/*      */   {
/*  902 */     return this.hasExternalizableBlockData;
/*      */   }
/*      */ 
/*      */   Object newInstance()
/*      */     throws InstantiationException, InvocationTargetException, UnsupportedOperationException
/*      */   {
/*  918 */     if (this.cons != null) {
/*      */       try {
/*  920 */         return this.cons.newInstance(new Object[0]);
/*      */       }
/*      */       catch (IllegalAccessException localIllegalAccessException) {
/*  923 */         InternalError localInternalError = new InternalError();
/*  924 */         localInternalError.initCause(localIllegalAccessException);
/*  925 */         throw localInternalError;
/*      */       }
/*      */     }
/*  928 */     throw new UnsupportedOperationException();
/*      */   }
/*      */ 
/*      */   private static Constructor getExternalizableConstructor(Class paramClass)
/*      */   {
/*      */     try
/*      */     {
/*  939 */       Constructor localConstructor = paramClass.getDeclaredConstructor(new Class[0]);
/*  940 */       localConstructor.setAccessible(true);
/*  941 */       return (localConstructor.getModifiers() & 0x1) != 0 ? localConstructor : null;
/*      */     } catch (NoSuchMethodException localNoSuchMethodException) {
/*      */     }
/*  944 */     return null;
/*      */   }
/*      */ 
/*      */   private static Constructor getSerializableConstructor(Class paramClass)
/*      */   {
/*  954 */     Class localClass = paramClass;
/*  955 */     while (Serializable.class.isAssignableFrom(localClass)) {
/*  956 */       if ((localClass = localClass.getSuperclass()) == null)
/*  957 */         return null;
/*      */     }
/*      */     try
/*      */     {
/*  961 */       Constructor localConstructor = localClass.getDeclaredConstructor(new Class[0]);
/*  962 */       int i = localConstructor.getModifiers();
/*  963 */       if (((i & 0x2) != 0) || (((i & 0x5) == 0) && (!packageEquals(paramClass, localClass))))
/*      */       {
/*  967 */         return null;
/*      */       }
/*  969 */       localConstructor = bridge.newConstructorForSerialization(paramClass, localConstructor);
/*  970 */       localConstructor.setAccessible(true);
/*  971 */       return localConstructor; } catch (NoSuchMethodException localNoSuchMethodException) {
/*      */     }
/*  973 */     return null;
/*      */   }
/*      */ 
/*      */   final ObjectStreamClass localClassDescriptor()
/*      */   {
/*  981 */     return this.localClassDesc;
/*      */   }
/*      */ 
/*      */   boolean isSerializable()
/*      */   {
/*  988 */     return this.serializable;
/*      */   }
/*      */ 
/*      */   boolean isExternalizable()
/*      */   {
/*  995 */     return this.externalizable;
/*      */   }
/*      */ 
/*      */   boolean isNonSerializable() {
/*  999 */     return (!this.externalizable) && (!this.serializable);
/*      */   }
/*      */ 
/*      */   private void computeFieldInfo()
/*      */   {
/* 1008 */     this.primBytes = 0;
/* 1009 */     this.objFields = 0;
/*      */ 
/* 1011 */     for (int i = 0; i < this.fields.length; i++)
/* 1012 */       switch (this.fields[i].getTypeCode()) {
/*      */       case 'B':
/*      */       case 'Z':
/* 1015 */         this.primBytes += 1;
/* 1016 */         break;
/*      */       case 'C':
/*      */       case 'S':
/* 1019 */         this.primBytes += 2;
/* 1020 */         break;
/*      */       case 'F':
/*      */       case 'I':
/* 1024 */         this.primBytes += 4;
/* 1025 */         break;
/*      */       case 'D':
/*      */       case 'J':
/* 1028 */         this.primBytes += 8;
/* 1029 */         break;
/*      */       case 'L':
/*      */       case '[':
/* 1033 */         this.objFields += 1;
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
/*      */       case 'Y': }   } 
/* 1041 */   private static void msg(String paramString) { System.out.println(paramString); }
/*      */ 
/*      */ 
/*      */   private static long _computeSerialVersionUID(Class paramClass)
/*      */   {
/* 1069 */     ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(512);
/*      */ 
/* 1071 */     long l = 0L;
/*      */     try {
/* 1073 */       MessageDigest localMessageDigest = MessageDigest.getInstance("SHA");
/* 1074 */       localObject1 = new DigestOutputStream(localByteArrayOutputStream, localMessageDigest);
/* 1075 */       DataOutputStream localDataOutputStream = new DataOutputStream((OutputStream)localObject1);
/*      */ 
/* 1079 */       localDataOutputStream.writeUTF(paramClass.getName());
/*      */ 
/* 1081 */       int i = paramClass.getModifiers();
/* 1082 */       i &= 1553;
/*      */ 
/* 1092 */       Method[] arrayOfMethod = paramClass.getDeclaredMethods();
/* 1093 */       if ((i & 0x200) != 0) {
/* 1094 */         i &= -1025;
/* 1095 */         if (arrayOfMethod.length > 0) {
/* 1096 */           i |= 1024;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1101 */       i &= 1553;
/*      */ 
/* 1105 */       localDataOutputStream.writeInt(i);
/*      */ 
/* 1112 */       if (!paramClass.isArray())
/*      */       {
/* 1120 */         localObject2 = paramClass.getInterfaces();
/* 1121 */         Arrays.sort((Object[])localObject2, compareClassByName);
/*      */ 
/* 1123 */         for (j = 0; j < localObject2.length; j++)
/*      */         {
/* 1126 */           localDataOutputStream.writeUTF(localObject2[j].getName());
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1131 */       Object localObject2 = paramClass.getDeclaredFields();
/* 1132 */       Arrays.sort((Object[])localObject2, compareMemberByName);
/*      */ 
/* 1134 */       for (int j = 0; j < localObject2.length; j++) {
/* 1135 */         Object localObject3 = localObject2[j];
/*      */ 
/* 1140 */         int m = localObject3.getModifiers();
/* 1141 */         if ((!Modifier.isPrivate(m)) || ((!Modifier.isTransient(m)) && (!Modifier.isStatic(m))))
/*      */         {
/* 1147 */           localDataOutputStream.writeUTF(localObject3.getName());
/*      */ 
/* 1150 */           m &= 223;
/*      */ 
/* 1154 */           localDataOutputStream.writeInt(m);
/*      */ 
/* 1158 */           localDataOutputStream.writeUTF(getSignature(localObject3.getType()));
/*      */         }
/*      */       }
/* 1161 */       if (hasStaticInitializer(paramClass))
/*      */       {
/* 1164 */         localDataOutputStream.writeUTF("<clinit>");
/*      */ 
/* 1168 */         localDataOutputStream.writeInt(8);
/*      */ 
/* 1172 */         localDataOutputStream.writeUTF("()V");
/*      */       }
/*      */ 
/* 1181 */       MethodSignature[] arrayOfMethodSignature1 = MethodSignature.removePrivateAndSort(paramClass.getDeclaredConstructors());
/*      */       Object localObject4;
/*      */       String str;
/*      */       int i2;
/* 1183 */       for (int k = 0; k < arrayOfMethodSignature1.length; k++) {
/* 1184 */         MethodSignature localMethodSignature = arrayOfMethodSignature1[k];
/* 1185 */         localObject4 = "<init>";
/* 1186 */         str = localMethodSignature.signature;
/* 1187 */         str = str.replace('/', '.');
/*      */ 
/* 1190 */         localDataOutputStream.writeUTF((String)localObject4);
/*      */ 
/* 1193 */         i2 = localMethodSignature.member.getModifiers() & 0xD3F;
/*      */ 
/* 1197 */         localDataOutputStream.writeInt(i2);
/*      */ 
/* 1201 */         localDataOutputStream.writeUTF(str);
/*      */       }
/*      */ 
/* 1207 */       MethodSignature[] arrayOfMethodSignature2 = MethodSignature.removePrivateAndSort(arrayOfMethod);
/*      */ 
/* 1209 */       for (int n = 0; n < arrayOfMethodSignature2.length; n++) {
/* 1210 */         localObject4 = arrayOfMethodSignature2[n];
/* 1211 */         str = ((MethodSignature)localObject4).signature;
/* 1212 */         str = str.replace('/', '.');
/*      */ 
/* 1216 */         localDataOutputStream.writeUTF(((MethodSignature)localObject4).member.getName());
/*      */ 
/* 1219 */         i2 = ((MethodSignature)localObject4).member.getModifiers() & 0xD3F;
/*      */ 
/* 1223 */         localDataOutputStream.writeInt(i2);
/*      */ 
/* 1227 */         localDataOutputStream.writeUTF(str);
/*      */       }
/*      */ 
/* 1233 */       localDataOutputStream.flush();
/* 1234 */       byte[] arrayOfByte = localMessageDigest.digest();
/* 1235 */       for (int i1 = 0; i1 < Math.min(8, arrayOfByte.length); i1++)
/* 1236 */         l += ((arrayOfByte[i1] & 0xFF) << i1 * 8);
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 1240 */       l = -1L;
/*      */     } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
/* 1242 */       Object localObject1 = new SecurityException();
/* 1243 */       ((SecurityException)localObject1).initCause(localNoSuchAlgorithmException);
/* 1244 */       throw ((Throwable)localObject1);
/*      */     }
/*      */ 
/* 1247 */     return l;
/*      */   }
/*      */ 
/*      */   private static long computeStructuralUID(ObjectStreamClass paramObjectStreamClass, Class paramClass) {
/* 1251 */     ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(512);
/*      */ 
/* 1253 */     long l = 0L;
/*      */     try
/*      */     {
/* 1256 */       if ((!Serializable.class.isAssignableFrom(paramClass)) || (paramClass.isInterface()))
/*      */       {
/* 1258 */         return 0L;
/*      */       }
/*      */ 
/* 1261 */       if (Externalizable.class.isAssignableFrom(paramClass)) {
/* 1262 */         return 1L;
/*      */       }
/*      */ 
/* 1265 */       MessageDigest localMessageDigest = MessageDigest.getInstance("SHA");
/* 1266 */       localObject = new DigestOutputStream(localByteArrayOutputStream, localMessageDigest);
/* 1267 */       DataOutputStream localDataOutputStream = new DataOutputStream((OutputStream)localObject);
/*      */ 
/* 1270 */       Class localClass = paramClass.getSuperclass();
/* 1271 */       if (localClass != null)
/*      */       {
/* 1278 */         localDataOutputStream.writeLong(computeStructuralUID(lookup(localClass), localClass));
/*      */       }
/*      */ 
/* 1281 */       if (paramObjectStreamClass.hasWriteObject())
/* 1282 */         localDataOutputStream.writeInt(2);
/*      */       else {
/* 1284 */         localDataOutputStream.writeInt(1);
/*      */       }
/*      */ 
/* 1289 */       ObjectStreamField[] arrayOfObjectStreamField = paramObjectStreamClass.getFields();
/* 1290 */       if (arrayOfObjectStreamField.length > 1) {
/* 1291 */         Arrays.sort(arrayOfObjectStreamField, compareObjStrFieldsByName);
/*      */       }
/*      */ 
/* 1296 */       for (int i = 0; i < arrayOfObjectStreamField.length; i++) {
/* 1297 */         localDataOutputStream.writeUTF(arrayOfObjectStreamField[i].getName());
/* 1298 */         localDataOutputStream.writeUTF(arrayOfObjectStreamField[i].getSignature());
/*      */       }
/*      */ 
/* 1304 */       localDataOutputStream.flush();
/* 1305 */       byte[] arrayOfByte = localMessageDigest.digest();
/*      */ 
/* 1309 */       for (int j = 0; j < Math.min(8, arrayOfByte.length); j++)
/* 1310 */         l += ((arrayOfByte[j] & 0xFF) << j * 8);
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 1314 */       l = -1L;
/*      */     } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
/* 1316 */       Object localObject = new SecurityException();
/* 1317 */       ((SecurityException)localObject).initCause(localNoSuchAlgorithmException);
/* 1318 */       throw ((Throwable)localObject);
/*      */     }
/* 1320 */     return l;
/*      */   }
/*      */ 
/*      */   static String getSignature(Class paramClass)
/*      */   {
/* 1327 */     String str = null;
/* 1328 */     if (paramClass.isArray()) {
/* 1329 */       Class localClass = paramClass;
/* 1330 */       int i = 0;
/* 1331 */       while (localClass.isArray()) {
/* 1332 */         i++;
/* 1333 */         localClass = localClass.getComponentType();
/*      */       }
/* 1335 */       StringBuffer localStringBuffer = new StringBuffer();
/* 1336 */       for (int j = 0; j < i; j++) {
/* 1337 */         localStringBuffer.append("[");
/*      */       }
/* 1339 */       localStringBuffer.append(getSignature(localClass));
/* 1340 */       str = localStringBuffer.toString();
/* 1341 */     } else if (paramClass.isPrimitive()) {
/* 1342 */       if (paramClass == Integer.TYPE)
/* 1343 */         str = "I";
/* 1344 */       else if (paramClass == Byte.TYPE)
/* 1345 */         str = "B";
/* 1346 */       else if (paramClass == Long.TYPE)
/* 1347 */         str = "J";
/* 1348 */       else if (paramClass == Float.TYPE)
/* 1349 */         str = "F";
/* 1350 */       else if (paramClass == Double.TYPE)
/* 1351 */         str = "D";
/* 1352 */       else if (paramClass == Short.TYPE)
/* 1353 */         str = "S";
/* 1354 */       else if (paramClass == Character.TYPE)
/* 1355 */         str = "C";
/* 1356 */       else if (paramClass == Boolean.TYPE)
/* 1357 */         str = "Z";
/* 1358 */       else if (paramClass == Void.TYPE)
/* 1359 */         str = "V";
/*      */     }
/*      */     else {
/* 1362 */       str = "L" + paramClass.getName().replace('.', '/') + ";";
/*      */     }
/* 1364 */     return str;
/*      */   }
/*      */ 
/*      */   static String getSignature(Method paramMethod)
/*      */   {
/* 1371 */     StringBuffer localStringBuffer = new StringBuffer();
/*      */ 
/* 1373 */     localStringBuffer.append("(");
/*      */ 
/* 1375 */     Class[] arrayOfClass = paramMethod.getParameterTypes();
/* 1376 */     for (int i = 0; i < arrayOfClass.length; i++) {
/* 1377 */       localStringBuffer.append(getSignature(arrayOfClass[i]));
/*      */     }
/* 1379 */     localStringBuffer.append(")");
/* 1380 */     localStringBuffer.append(getSignature(paramMethod.getReturnType()));
/* 1381 */     return localStringBuffer.toString();
/*      */   }
/*      */ 
/*      */   static String getSignature(Constructor paramConstructor)
/*      */   {
/* 1388 */     StringBuffer localStringBuffer = new StringBuffer();
/*      */ 
/* 1390 */     localStringBuffer.append("(");
/*      */ 
/* 1392 */     Class[] arrayOfClass = paramConstructor.getParameterTypes();
/* 1393 */     for (int i = 0; i < arrayOfClass.length; i++) {
/* 1394 */       localStringBuffer.append(getSignature(arrayOfClass[i]));
/*      */     }
/* 1396 */     localStringBuffer.append(")V");
/* 1397 */     return localStringBuffer.toString();
/*      */   }
/*      */ 
/*      */   private static ObjectStreamClass findDescriptorFor(Class paramClass)
/*      */   {
/* 1414 */     int i = paramClass.hashCode();
/* 1415 */     int j = (i & 0x7FFFFFFF) % descriptorFor.length;
/*      */     ObjectStreamClassEntry localObjectStreamClassEntry1;
/* 1420 */     while (((localObjectStreamClassEntry1 = descriptorFor[j]) != null) && (localObjectStreamClassEntry1.get() == null)) {
/* 1421 */       descriptorFor[j] = localObjectStreamClassEntry1.next;
/*      */     }
/*      */ 
/* 1427 */     ObjectStreamClassEntry localObjectStreamClassEntry2 = localObjectStreamClassEntry1;
/* 1428 */     while (localObjectStreamClassEntry1 != null) {
/* 1429 */       ObjectStreamClass localObjectStreamClass = (ObjectStreamClass)localObjectStreamClassEntry1.get();
/* 1430 */       if (localObjectStreamClass == null)
/*      */       {
/* 1432 */         localObjectStreamClassEntry2.next = localObjectStreamClassEntry1.next;
/*      */       } else {
/* 1434 */         if (localObjectStreamClass.ofClass == paramClass)
/* 1435 */           return localObjectStreamClass;
/* 1436 */         localObjectStreamClassEntry2 = localObjectStreamClassEntry1;
/*      */       }
/* 1438 */       localObjectStreamClassEntry1 = localObjectStreamClassEntry1.next;
/*      */     }
/* 1440 */     return null;
/*      */   }
/*      */ 
/*      */   private static void insertDescriptorFor(ObjectStreamClass paramObjectStreamClass)
/*      */   {
/* 1448 */     if (findDescriptorFor(paramObjectStreamClass.ofClass) != null) {
/* 1449 */       return;
/*      */     }
/*      */ 
/* 1452 */     int i = paramObjectStreamClass.ofClass.hashCode();
/* 1453 */     int j = (i & 0x7FFFFFFF) % descriptorFor.length;
/* 1454 */     ObjectStreamClassEntry localObjectStreamClassEntry = new ObjectStreamClassEntry(paramObjectStreamClass);
/* 1455 */     localObjectStreamClassEntry.next = descriptorFor[j];
/* 1456 */     descriptorFor[j] = localObjectStreamClassEntry;
/*      */   }
/*      */ 
/*      */   private static Field[] getDeclaredFields(Class paramClass) {
/* 1460 */     return (Field[])AccessController.doPrivileged(new PrivilegedAction() {
/*      */       public Object run() {
/* 1462 */         return this.val$clz.getDeclaredFields();
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   private static boolean hasStaticInitializer(Class paramClass)
/*      */   {
/*      */     Object localObject;
/* 1566 */     if (hasStaticInitializerMethod == null) {
/* 1567 */       localObject = null;
/*      */       try
/*      */       {
/*      */         try
/*      */         {
/* 1577 */           localObject = Class.forName("sun.misc.ClassReflector");
/*      */         }
/*      */         catch (ClassNotFoundException localClassNotFoundException)
/*      */         {
/*      */         }
/*      */ 
/* 1583 */         if (localObject == null) {
/* 1584 */           localObject = java.io.ObjectStreamClass.class;
/*      */         }
/* 1586 */         hasStaticInitializerMethod = ((Class)localObject).getDeclaredMethod("hasStaticInitializer", new Class[] { Class.class });
/*      */       }
/*      */       catch (NoSuchMethodException localNoSuchMethodException)
/*      */       {
/*      */       }
/*      */ 
/* 1592 */       if (hasStaticInitializerMethod == null)
/*      */       {
/* 1594 */         throw new InternalError("Can't find hasStaticInitializer method on " + ((Class)localObject).getName());
/*      */       }
/*      */ 
/* 1597 */       hasStaticInitializerMethod.setAccessible(true);
/*      */     }
/*      */     try
/*      */     {
/* 1601 */       localObject = (Boolean)hasStaticInitializerMethod.invoke(null, new Object[] { paramClass });
/*      */ 
/* 1603 */       return ((Boolean)localObject).booleanValue();
/*      */     }
/*      */     catch (Exception localException) {
/* 1606 */       InternalError localInternalError = new InternalError("Error invoking hasStaticInitializer");
/* 1607 */       localInternalError.initCause(localException);
/* 1608 */       throw localInternalError;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static Method getInheritableMethod(Class paramClass1, String paramString, Class[] paramArrayOfClass, Class paramClass2)
/*      */   {
/* 1785 */     Method localMethod = null;
/* 1786 */     Class localClass = paramClass1;
/* 1787 */     while (localClass != null) {
/*      */       try {
/* 1789 */         localMethod = localClass.getDeclaredMethod(paramString, paramArrayOfClass);
/*      */       }
/*      */       catch (NoSuchMethodException localNoSuchMethodException) {
/* 1792 */         localClass = localClass.getSuperclass();
/*      */       }
/*      */     }
/*      */ 
/* 1796 */     if ((localMethod == null) || (localMethod.getReturnType() != paramClass2)) {
/* 1797 */       return null;
/*      */     }
/* 1799 */     localMethod.setAccessible(true);
/* 1800 */     int i = localMethod.getModifiers();
/* 1801 */     if ((i & 0x408) != 0)
/* 1802 */       return null;
/* 1803 */     if ((i & 0x5) != 0)
/* 1804 */       return localMethod;
/* 1805 */     if ((i & 0x2) != 0) {
/* 1806 */       return paramClass1 == localClass ? localMethod : null;
/*      */     }
/* 1808 */     return packageEquals(paramClass1, localClass) ? localMethod : null;
/*      */   }
/*      */ 
/*      */   private static boolean packageEquals(Class paramClass1, Class paramClass2)
/*      */   {
/* 1819 */     Package localPackage1 = paramClass1.getPackage(); Package localPackage2 = paramClass2.getPackage();
/* 1820 */     return (localPackage1 == localPackage2) || ((localPackage1 != null) && (localPackage1.equals(localPackage2)));
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*      */     try
/*      */     {
/* 1622 */       classSerializable = Class.forName("java.io.Serializable");
/* 1623 */       classExternalizable = Class.forName("java.io.Externalizable");
/*      */     } catch (Throwable localThrowable) {
/* 1625 */       System.err.println("Could not load java.io.Serializable or java.io.Externalizable.");
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class CompareClassByName
/*      */     implements Comparator
/*      */   {
/*      */     public int compare(Object paramObject1, Object paramObject2)
/*      */     {
/* 1666 */       Class localClass1 = (Class)paramObject1;
/* 1667 */       Class localClass2 = (Class)paramObject2;
/* 1668 */       return localClass1.getName().compareTo(localClass2.getName());
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class CompareMemberByName
/*      */     implements Comparator
/*      */   {
/*      */     public int compare(Object paramObject1, Object paramObject2)
/*      */     {
/* 1695 */       String str1 = ((Member)paramObject1).getName();
/* 1696 */       String str2 = ((Member)paramObject2).getName();
/*      */ 
/* 1698 */       if ((paramObject1 instanceof Method)) {
/* 1699 */         str1 = str1 + ObjectStreamClass.getSignature((Method)paramObject1);
/* 1700 */         str2 = str2 + ObjectStreamClass.getSignature((Method)paramObject2);
/* 1701 */       } else if ((paramObject1 instanceof Constructor)) {
/* 1702 */         str1 = str1 + ObjectStreamClass.getSignature((Constructor)paramObject1);
/* 1703 */         str2 = str2 + ObjectStreamClass.getSignature((Constructor)paramObject2);
/*      */       }
/* 1705 */       return str1.compareTo(str2);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class CompareObjStrFieldsByName
/*      */     implements Comparator
/*      */   {
/*      */     public int compare(Object paramObject1, Object paramObject2)
/*      */     {
/* 1680 */       ObjectStreamField localObjectStreamField1 = (ObjectStreamField)paramObject1;
/* 1681 */       ObjectStreamField localObjectStreamField2 = (ObjectStreamField)paramObject2;
/*      */ 
/* 1683 */       return localObjectStreamField1.getName().compareTo(localObjectStreamField2.getName());
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class MethodSignature
/*      */     implements Comparator
/*      */   {
/*      */     Member member;
/*      */     String signature;
/*      */ 
/*      */     static MethodSignature[] removePrivateAndSort(Member[] paramArrayOfMember)
/*      */     {
/* 1720 */       int i = 0;
/* 1721 */       for (int j = 0; j < paramArrayOfMember.length; j++) {
/* 1722 */         if (!Modifier.isPrivate(paramArrayOfMember[j].getModifiers())) {
/* 1723 */           i++;
/*      */         }
/*      */       }
/* 1726 */       MethodSignature[] arrayOfMethodSignature = new MethodSignature[i];
/* 1727 */       int k = 0;
/* 1728 */       for (int m = 0; m < paramArrayOfMember.length; m++) {
/* 1729 */         if (!Modifier.isPrivate(paramArrayOfMember[m].getModifiers())) {
/* 1730 */           arrayOfMethodSignature[k] = new MethodSignature(paramArrayOfMember[m]);
/* 1731 */           k++;
/*      */         }
/*      */       }
/* 1734 */       if (k > 0)
/* 1735 */         Arrays.sort(arrayOfMethodSignature, arrayOfMethodSignature[0]);
/* 1736 */       return arrayOfMethodSignature;
/*      */     }
/*      */ 
/*      */     public int compare(Object paramObject1, Object paramObject2)
/*      */     {
/* 1743 */       if (paramObject1 == paramObject2) {
/* 1744 */         return 0;
/*      */       }
/* 1746 */       MethodSignature localMethodSignature1 = (MethodSignature)paramObject1;
/* 1747 */       MethodSignature localMethodSignature2 = (MethodSignature)paramObject2;
/*      */       int i;
/* 1750 */       if (isConstructor()) {
/* 1751 */         i = localMethodSignature1.signature.compareTo(localMethodSignature2.signature);
/*      */       } else {
/* 1753 */         i = localMethodSignature1.member.getName().compareTo(localMethodSignature2.member.getName());
/* 1754 */         if (i == 0)
/* 1755 */           i = localMethodSignature1.signature.compareTo(localMethodSignature2.signature);
/*      */       }
/* 1757 */       return i;
/*      */     }
/*      */ 
/*      */     private final boolean isConstructor() {
/* 1761 */       return this.member instanceof Constructor;
/*      */     }
/*      */     private MethodSignature(Member paramMember) {
/* 1764 */       this.member = paramMember;
/* 1765 */       if (isConstructor())
/* 1766 */         this.signature = ObjectStreamClass.getSignature((Constructor)paramMember);
/*      */       else
/* 1768 */         this.signature = ObjectStreamClass.getSignature((Method)paramMember);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class ObjectStreamClassEntry
/*      */   {
/*      */     ObjectStreamClassEntry next;
/*      */     private ObjectStreamClass c;
/*      */ 
/*      */     ObjectStreamClassEntry(ObjectStreamClass paramObjectStreamClass)
/*      */     {
/* 1647 */       this.c = paramObjectStreamClass;
/*      */     }
/*      */ 
/*      */     public Object get()
/*      */     {
/* 1653 */       return this.c;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static final class PersistentFieldsValue
/*      */   {
/*  387 */     private final ConcurrentMap map = new ConcurrentHashMap();
/*  388 */     private static final Object NULL_VALUE = PersistentFieldsValue.class.getName() + ".NULL_VALUE";
/*      */ 
/*      */     ObjectStreamField[] get(Class paramClass)
/*      */     {
/*  394 */       Object localObject = this.map.get(paramClass);
/*  395 */       if (localObject == null) {
/*  396 */         localObject = computeValue(paramClass);
/*  397 */         this.map.putIfAbsent(paramClass, localObject);
/*      */       }
/*  399 */       return localObject == NULL_VALUE ? null : (ObjectStreamField[])localObject;
/*      */     }
/*      */ 
/*      */     private static Object computeValue(Class<?> paramClass) {
/*      */       try {
/*  404 */         Field localField = paramClass.getDeclaredField("serialPersistentFields");
/*  405 */         int i = localField.getModifiers();
/*  406 */         if ((Modifier.isPrivate(i)) && (Modifier.isStatic(i)) && (Modifier.isFinal(i)))
/*      */         {
/*  408 */           localField.setAccessible(true);
/*  409 */           java.io.ObjectStreamField[] arrayOfObjectStreamField = (java.io.ObjectStreamField[])localField.get(paramClass);
/*      */ 
/*  411 */           return translateFields(arrayOfObjectStreamField);
/*      */         }
/*      */       } catch (NoSuchFieldException localNoSuchFieldException) {
/*      */       } catch (IllegalAccessException localIllegalAccessException) {
/*      */       } catch (IllegalArgumentException localIllegalArgumentException) {
/*      */       } catch (ClassCastException localClassCastException) {  }
/*      */ 
/*  417 */       return NULL_VALUE;
/*      */     }
/*      */ 
/*      */     private static ObjectStreamField[] translateFields(java.io.ObjectStreamField[] paramArrayOfObjectStreamField)
/*      */     {
/*  422 */       ObjectStreamField[] arrayOfObjectStreamField = new ObjectStreamField[paramArrayOfObjectStreamField.length];
/*      */ 
/*  424 */       for (int i = 0; i < paramArrayOfObjectStreamField.length; i++) {
/*  425 */         arrayOfObjectStreamField[i] = new ObjectStreamField(paramArrayOfObjectStreamField[i].getName(), paramArrayOfObjectStreamField[i].getType());
/*      */       }
/*      */ 
/*  428 */       return arrayOfObjectStreamField;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.io.ObjectStreamClass
 * JD-Core Version:    0.6.2
 */