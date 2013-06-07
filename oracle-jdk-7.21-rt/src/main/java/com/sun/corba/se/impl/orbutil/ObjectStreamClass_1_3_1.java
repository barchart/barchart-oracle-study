/*      */ package com.sun.corba.se.impl.orbutil;
/*      */ 
/*      */ import com.sun.corba.se.impl.io.ObjectStreamClass;
/*      */ import com.sun.corba.se.impl.io.ValueUtility;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.DataOutputStream;
/*      */ import java.io.Externalizable;
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.io.Serializable;
/*      */ import java.lang.reflect.Array;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.Field;
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
/*      */ import java.util.Hashtable;
/*      */ import org.omg.CORBA.ValueMember;
/*      */ 
/*      */ public class ObjectStreamClass_1_3_1
/*      */   implements Serializable
/*      */ {
/*      */   public static final long kDefaultUID = -1L;
/*   90 */   private static Object[] noArgsList = new Object[0];
/*   91 */   private static Class[] noTypesList = new Class[0];
/*      */   private static Hashtable translatedFields;
/*  990 */   private static ObjectStreamClassEntry[] descriptorFor = new ObjectStreamClassEntry[61];
/*      */   private String name;
/*      */   private ObjectStreamClass_1_3_1 superclass;
/*      */   private boolean serializable;
/*      */   private boolean externalizable;
/*      */   private ObjectStreamField[] fields;
/*      */   private Class ofClass;
/*      */   boolean forProxyClass;
/* 1091 */   private long suid = -1L;
/* 1092 */   private String suidStr = null;
/*      */ 
/* 1097 */   private long actualSuid = -1L;
/* 1098 */   private String actualSuidStr = null;
/*      */   int primBytes;
/*      */   int objFields;
/* 1108 */   private Object lock = new Object();
/*      */   private boolean hasWriteObjectMethod;
/*      */   private boolean hasExternalizableBlockData;
/*      */   Method writeObjectMethod;
/*      */   Method readObjectMethod;
/*      */   private transient Method writeReplaceObjectMethod;
/*      */   private transient Method readResolveObjectMethod;
/*      */   private ObjectStreamClass_1_3_1 localClassDesc;
/* 1134 */   private static Class classSerializable = null;
/* 1135 */   private static Class classExternalizable = null;
/*      */   private static final long serialVersionUID = -6120832682080437368L;
/* 1156 */   public static final ObjectStreamField[] NO_FIELDS = new ObjectStreamField[0];
/*      */ 
/* 1181 */   private static Comparator compareClassByName = new CompareClassByName(null);
/*      */ 
/* 1195 */   private static Comparator compareMemberByName = new CompareMemberByName(null);
/*      */ 
/*      */   static final ObjectStreamClass_1_3_1 lookup(Class paramClass)
/*      */   {
/*  101 */     ObjectStreamClass_1_3_1 localObjectStreamClass_1_3_1 = lookupInternal(paramClass);
/*  102 */     if ((localObjectStreamClass_1_3_1.isSerializable()) || (localObjectStreamClass_1_3_1.isExternalizable()))
/*  103 */       return localObjectStreamClass_1_3_1;
/*  104 */     return null;
/*      */   }
/*      */ 
/*      */   static ObjectStreamClass_1_3_1 lookupInternal(Class paramClass)
/*      */   {
/*  116 */     ObjectStreamClass_1_3_1 localObjectStreamClass_1_3_11 = null;
/*  117 */     synchronized (descriptorFor)
/*      */     {
/*  119 */       localObjectStreamClass_1_3_11 = findDescriptorFor(paramClass);
/*  120 */       if (localObjectStreamClass_1_3_11 != null) {
/*  121 */         return localObjectStreamClass_1_3_11;
/*      */       }
/*      */ 
/*  125 */       boolean bool1 = classSerializable.isAssignableFrom(paramClass);
/*      */ 
/*  129 */       ObjectStreamClass_1_3_1 localObjectStreamClass_1_3_12 = null;
/*  130 */       if (bool1) {
/*  131 */         Class localClass = paramClass.getSuperclass();
/*  132 */         if (localClass != null) {
/*  133 */           localObjectStreamClass_1_3_12 = lookup(localClass);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  140 */       boolean bool2 = false;
/*  141 */       if (bool1) {
/*  142 */         bool2 = ((localObjectStreamClass_1_3_12 != null) && (localObjectStreamClass_1_3_12.isExternalizable())) || (classExternalizable.isAssignableFrom(paramClass));
/*      */ 
/*  145 */         if (bool2) {
/*  146 */           bool1 = false;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  153 */       localObjectStreamClass_1_3_11 = new ObjectStreamClass_1_3_1(paramClass, localObjectStreamClass_1_3_12, bool1, bool2);
/*      */     }
/*      */ 
/*  156 */     localObjectStreamClass_1_3_11.init();
/*  157 */     return localObjectStreamClass_1_3_11;
/*      */   }
/*      */ 
/*      */   public final String getName()
/*      */   {
/*  164 */     return this.name;
/*      */   }
/*      */ 
/*      */   public static final long getSerialVersionUID(Class paramClass)
/*      */   {
/*  174 */     ObjectStreamClass_1_3_1 localObjectStreamClass_1_3_1 = lookup(paramClass);
/*  175 */     if (localObjectStreamClass_1_3_1 != null)
/*      */     {
/*  177 */       return localObjectStreamClass_1_3_1.getSerialVersionUID();
/*      */     }
/*  179 */     return 0L;
/*      */   }
/*      */ 
/*      */   public final long getSerialVersionUID()
/*      */   {
/*  189 */     return this.suid;
/*      */   }
/*      */ 
/*      */   public final String getSerialVersionUIDStr()
/*      */   {
/*  199 */     if (this.suidStr == null)
/*  200 */       this.suidStr = Long.toHexString(this.suid).toUpperCase();
/*  201 */     return this.suidStr;
/*      */   }
/*      */ 
/*      */   public static final long getActualSerialVersionUID(Class paramClass)
/*      */   {
/*  209 */     ObjectStreamClass_1_3_1 localObjectStreamClass_1_3_1 = lookup(paramClass);
/*  210 */     if (localObjectStreamClass_1_3_1 != null)
/*      */     {
/*  212 */       return localObjectStreamClass_1_3_1.getActualSerialVersionUID();
/*      */     }
/*  214 */     return 0L;
/*      */   }
/*      */ 
/*      */   public final long getActualSerialVersionUID()
/*      */   {
/*  221 */     return this.actualSuid;
/*      */   }
/*      */ 
/*      */   public final String getActualSerialVersionUIDStr()
/*      */   {
/*  228 */     if (this.actualSuidStr == null)
/*  229 */       this.actualSuidStr = Long.toHexString(this.actualSuid).toUpperCase();
/*  230 */     return this.actualSuidStr;
/*      */   }
/*      */ 
/*      */   public final Class forClass()
/*      */   {
/*  238 */     return this.ofClass;
/*      */   }
/*      */ 
/*      */   public ObjectStreamField[] getFields()
/*      */   {
/*  250 */     if (this.fields.length > 0) {
/*  251 */       ObjectStreamField[] arrayOfObjectStreamField = new ObjectStreamField[this.fields.length];
/*  252 */       System.arraycopy(this.fields, 0, arrayOfObjectStreamField, 0, this.fields.length);
/*  253 */       return arrayOfObjectStreamField;
/*      */     }
/*  255 */     return this.fields;
/*      */   }
/*      */ 
/*      */   public boolean hasField(ValueMember paramValueMember)
/*      */   {
/*  261 */     for (int i = 0; i < this.fields.length; i++)
/*      */       try {
/*  263 */         if (this.fields[i].getName().equals(paramValueMember.name))
/*      */         {
/*  265 */           if (this.fields[i].getSignature().equals(ValueUtility.getSignature(paramValueMember)))
/*  266 */             return true;
/*      */         }
/*      */       }
/*      */       catch (Throwable localThrowable) {
/*      */       }
/*  271 */     return false;
/*      */   }
/*      */ 
/*      */   final ObjectStreamField[] getFieldsNoCopy()
/*      */   {
/*  276 */     return this.fields;
/*      */   }
/*      */ 
/*      */   public final ObjectStreamField getField(String paramString)
/*      */   {
/*  287 */     for (int i = this.fields.length - 1; i >= 0; i--) {
/*  288 */       if (paramString.equals(this.fields[i].getName())) {
/*  289 */         return this.fields[i];
/*      */       }
/*      */     }
/*  292 */     return null;
/*      */   }
/*      */ 
/*      */   public Serializable writeReplace(Serializable paramSerializable) {
/*  296 */     if (this.writeReplaceObjectMethod != null) {
/*      */       try {
/*  298 */         return (Serializable)this.writeReplaceObjectMethod.invoke(paramSerializable, noArgsList);
/*      */       }
/*      */       catch (Throwable localThrowable) {
/*  301 */         throw new RuntimeException(localThrowable.getMessage());
/*      */       }
/*      */     }
/*  304 */     return paramSerializable;
/*      */   }
/*      */ 
/*      */   public Object readResolve(Object paramObject) {
/*  308 */     if (this.readResolveObjectMethod != null) {
/*      */       try {
/*  310 */         return this.readResolveObjectMethod.invoke(paramObject, noArgsList);
/*      */       }
/*      */       catch (Throwable localThrowable) {
/*  313 */         throw new RuntimeException(localThrowable.getMessage());
/*      */       }
/*      */     }
/*  316 */     return paramObject;
/*      */   }
/*      */ 
/*      */   public final String toString()
/*      */   {
/*  323 */     StringBuffer localStringBuffer = new StringBuffer();
/*      */ 
/*  325 */     localStringBuffer.append(this.name);
/*  326 */     localStringBuffer.append(": static final long serialVersionUID = ");
/*  327 */     localStringBuffer.append(Long.toString(this.suid));
/*  328 */     localStringBuffer.append("L;");
/*  329 */     return localStringBuffer.toString();
/*      */   }
/*      */ 
/*      */   private ObjectStreamClass_1_3_1(Class paramClass, ObjectStreamClass_1_3_1 paramObjectStreamClass_1_3_1, boolean paramBoolean1, boolean paramBoolean2)
/*      */   {
/*  339 */     this.ofClass = paramClass;
/*      */ 
/*  341 */     if (Proxy.isProxyClass(paramClass)) {
/*  342 */       this.forProxyClass = true;
/*      */     }
/*      */ 
/*  345 */     this.name = paramClass.getName();
/*  346 */     this.superclass = paramObjectStreamClass_1_3_1;
/*  347 */     this.serializable = paramBoolean1;
/*  348 */     if (!this.forProxyClass)
/*      */     {
/*  350 */       this.externalizable = paramBoolean2;
/*      */     }
/*      */ 
/*  358 */     insertDescriptorFor(this);
/*      */   }
/*      */ 
/*      */   private void init()
/*      */   {
/*  377 */     synchronized (this.lock)
/*      */     {
/*  379 */       final Class localClass = this.ofClass;
/*      */ 
/*  381 */       if (this.fields != null) {
/*  382 */         return;
/*      */       }
/*      */ 
/*  385 */       if ((!this.serializable) || (this.externalizable) || (this.forProxyClass) || (this.name.equals("java.lang.String")))
/*      */       {
/*  389 */         this.fields = NO_FIELDS;
/*  390 */       } else if (this.serializable)
/*      */       {
/*  394 */         AccessController.doPrivileged(new PrivilegedAction()
/*      */         {
/*      */           public Object run()
/*      */           {
/*      */             try
/*      */             {
/*  401 */               Field localField1 = localClass.getDeclaredField("serialPersistentFields");
/*      */ 
/*  404 */               localField1.setAccessible(true);
/*      */ 
/*  407 */               java.io.ObjectStreamField[] arrayOfObjectStreamField = (java.io.ObjectStreamField[])localField1.get(localClass);
/*      */ 
/*  409 */               int k = localField1.getModifiers();
/*  410 */               if ((Modifier.isPrivate(k)) && (Modifier.isStatic(k)) && (Modifier.isFinal(k)))
/*      */               {
/*  414 */                 ObjectStreamClass_1_3_1.this.fields = ((ObjectStreamField[])ObjectStreamClass_1_3_1.translateFields((Object[])localField1.get(localClass)));
/*      */               }
/*      */             } catch (NoSuchFieldException localNoSuchFieldException1) {
/*  417 */               ObjectStreamClass_1_3_1.this.fields = null;
/*      */             } catch (IllegalAccessException localIllegalAccessException) {
/*  419 */               ObjectStreamClass_1_3_1.this.fields = null;
/*      */             } catch (IllegalArgumentException localIllegalArgumentException) {
/*  421 */               ObjectStreamClass_1_3_1.this.fields = null;
/*      */             }
/*      */             catch (ClassCastException localClassCastException)
/*      */             {
/*  426 */               ObjectStreamClass_1_3_1.this.fields = null;
/*      */             }
/*      */ 
/*  430 */             if (ObjectStreamClass_1_3_1.this.fields == null)
/*      */             {
/*  439 */               Field[] arrayOfField = localClass.getDeclaredFields();
/*      */ 
/*  441 */               int j = 0;
/*  442 */               ObjectStreamField[] arrayOfObjectStreamField1 = new ObjectStreamField[arrayOfField.length];
/*      */ 
/*  444 */               for (int m = 0; m < arrayOfField.length; m++) {
/*  445 */                 int n = arrayOfField[m].getModifiers();
/*  446 */                 if ((!Modifier.isStatic(n)) && (!Modifier.isTransient(n)))
/*      */                 {
/*  448 */                   arrayOfObjectStreamField1[(j++)] = new ObjectStreamField(arrayOfField[m]);
/*      */                 }
/*      */               }
/*      */ 
/*  452 */               ObjectStreamClass_1_3_1.this.fields = new ObjectStreamField[j];
/*  453 */               System.arraycopy(arrayOfObjectStreamField1, 0, ObjectStreamClass_1_3_1.this.fields, 0, j);
/*      */             }
/*      */             else
/*      */             {
/*  459 */               for (int i = ObjectStreamClass_1_3_1.this.fields.length - 1; i >= 0; i--)
/*      */                 try {
/*  461 */                   Field localField2 = localClass.getDeclaredField(ObjectStreamClass_1_3_1.this.fields[i].getName());
/*  462 */                   if (ObjectStreamClass_1_3_1.this.fields[i].getType() == localField2.getType())
/*      */                   {
/*  464 */                     ObjectStreamClass_1_3_1.this.fields[i].setField(localField2);
/*      */                   }
/*      */                 }
/*      */                 catch (NoSuchFieldException localNoSuchFieldException2)
/*      */                 {
/*      */                 }
/*      */             }
/*  471 */             return null;
/*      */           }
/*      */         });
/*  475 */         if (this.fields.length > 1) {
/*  476 */           Arrays.sort(this.fields);
/*      */         }
/*      */ 
/*  479 */         computeFieldInfo();
/*      */       }
/*      */ 
/*  488 */       if (isNonSerializable()) {
/*  489 */         this.suid = 0L;
/*      */       }
/*      */       else {
/*  492 */         AccessController.doPrivileged(new PrivilegedAction()
/*      */         {
/*      */           public Object run()
/*      */           {
/*      */             int i;
/*  494 */             if (ObjectStreamClass_1_3_1.this.forProxyClass)
/*      */             {
/*  496 */               ObjectStreamClass_1_3_1.this.suid = 0L;
/*      */             }
/*      */             else try {
/*  499 */                 Field localField = localClass.getDeclaredField("serialVersionUID");
/*  500 */                 i = localField.getModifiers();
/*      */ 
/*  502 */                 if ((Modifier.isStatic(i)) && (Modifier.isFinal(i)))
/*      */                 {
/*  504 */                   localField.setAccessible(true);
/*  505 */                   ObjectStreamClass_1_3_1.this.suid = localField.getLong(localClass);
/*      */                 }
/*      */                 else
/*      */                 {
/*  511 */                   ObjectStreamClass_1_3_1.this.suid = ObjectStreamClass.getSerialVersionUID(localClass);
/*      */                 }
/*      */               }
/*      */               catch (NoSuchFieldException localNoSuchFieldException)
/*      */               {
/*  516 */                 ObjectStreamClass_1_3_1.this.suid = ObjectStreamClass.getSerialVersionUID(localClass);
/*      */               }
/*      */               catch (IllegalAccessException localIllegalAccessException)
/*      */               {
/*  520 */                 ObjectStreamClass_1_3_1.this.suid = ObjectStreamClass.getSerialVersionUID(localClass);
/*      */               }
/*      */ 
/*      */ 
/*      */             try
/*      */             {
/*  526 */               ObjectStreamClass_1_3_1.this.writeReplaceObjectMethod = localClass.getDeclaredMethod("writeReplace", ObjectStreamClass_1_3_1.noTypesList);
/*  527 */               if (Modifier.isStatic(ObjectStreamClass_1_3_1.this.writeReplaceObjectMethod.getModifiers()))
/*  528 */                 ObjectStreamClass_1_3_1.this.writeReplaceObjectMethod = null;
/*      */               else {
/*  530 */                 ObjectStreamClass_1_3_1.this.writeReplaceObjectMethod.setAccessible(true);
/*      */               }
/*      */             }
/*      */             catch (NoSuchMethodException localNoSuchMethodException1)
/*      */             {
/*      */             }
/*      */             try
/*      */             {
/*  538 */               ObjectStreamClass_1_3_1.this.readResolveObjectMethod = localClass.getDeclaredMethod("readResolve", ObjectStreamClass_1_3_1.noTypesList);
/*  539 */               if (Modifier.isStatic(ObjectStreamClass_1_3_1.this.readResolveObjectMethod.getModifiers()))
/*  540 */                 ObjectStreamClass_1_3_1.this.readResolveObjectMethod = null;
/*      */               else {
/*  542 */                 ObjectStreamClass_1_3_1.this.readResolveObjectMethod.setAccessible(true);
/*      */               }
/*      */ 
/*      */             }
/*      */             catch (NoSuchMethodException localNoSuchMethodException2)
/*      */             {
/*      */             }
/*      */ 
/*  554 */             if ((ObjectStreamClass_1_3_1.this.serializable) && (!ObjectStreamClass_1_3_1.this.forProxyClass))
/*      */             {
/*      */               try
/*      */               {
/*  561 */                 Class[] arrayOfClass1 = { ObjectOutputStream.class };
/*  562 */                 ObjectStreamClass_1_3_1.this.writeObjectMethod = localClass.getDeclaredMethod("writeObject", arrayOfClass1);
/*  563 */                 ObjectStreamClass_1_3_1.this.hasWriteObjectMethod = true;
/*  564 */                 i = ObjectStreamClass_1_3_1.this.writeObjectMethod.getModifiers();
/*      */ 
/*  567 */                 if ((!Modifier.isPrivate(i)) || (Modifier.isStatic(i)))
/*      */                 {
/*  569 */                   ObjectStreamClass_1_3_1.this.writeObjectMethod = null;
/*  570 */                   ObjectStreamClass_1_3_1.this.hasWriteObjectMethod = false;
/*      */                 }
/*      */ 
/*      */               }
/*      */               catch (NoSuchMethodException localNoSuchMethodException3)
/*      */               {
/*      */               }
/*      */ 
/*      */               try
/*      */               {
/*  581 */                 Class[] arrayOfClass2 = { ObjectInputStream.class };
/*  582 */                 ObjectStreamClass_1_3_1.this.readObjectMethod = localClass.getDeclaredMethod("readObject", arrayOfClass2);
/*  583 */                 i = ObjectStreamClass_1_3_1.this.readObjectMethod.getModifiers();
/*      */ 
/*  586 */                 if ((!Modifier.isPrivate(i)) || (Modifier.isStatic(i)))
/*      */                 {
/*  588 */                   ObjectStreamClass_1_3_1.this.readObjectMethod = null;
/*      */                 }
/*      */               }
/*      */               catch (NoSuchMethodException localNoSuchMethodException4)
/*      */               {
/*      */               }
/*      */             }
/*      */ 
/*  596 */             return null;
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*  601 */       this.actualSuid = computeStructuralUID(this, localClass);
/*      */     }
/*      */   }
/*      */ 
/*      */   ObjectStreamClass_1_3_1(String paramString, long paramLong)
/*      */   {
/*  613 */     this.name = paramString;
/*  614 */     this.suid = paramLong;
/*  615 */     this.superclass = null;
/*      */   }
/*      */ 
/*      */   private static Object[] translateFields(Object[] paramArrayOfObject) throws NoSuchFieldException
/*      */   {
/*      */     try {
/*  621 */       java.io.ObjectStreamField[] arrayOfObjectStreamField = (java.io.ObjectStreamField[])paramArrayOfObject;
/*  622 */       Object[] arrayOfObject1 = null;
/*      */ 
/*  624 */       if (translatedFields == null) {
/*  625 */         translatedFields = new Hashtable();
/*      */       }
/*  627 */       arrayOfObject1 = (Object[])translatedFields.get(arrayOfObjectStreamField);
/*      */ 
/*  629 */       if (arrayOfObject1 != null) {
/*  630 */         return arrayOfObject1;
/*      */       }
/*  632 */       ObjectStreamField localObjectStreamField = ObjectStreamField.class;
/*      */ 
/*  634 */       arrayOfObject1 = (Object[])Array.newInstance(localObjectStreamField, paramArrayOfObject.length);
/*  635 */       Object[] arrayOfObject2 = new Object[2];
/*  636 */       Class[] arrayOfClass = { String.class, Class.class };
/*  637 */       Constructor localConstructor = localObjectStreamField.getDeclaredConstructor(arrayOfClass);
/*  638 */       for (int i = arrayOfObjectStreamField.length - 1; i >= 0; i--) {
/*  639 */         arrayOfObject2[0] = arrayOfObjectStreamField[i].getName();
/*  640 */         arrayOfObject2[1] = arrayOfObjectStreamField[i].getType();
/*      */ 
/*  642 */         arrayOfObject1[i] = localConstructor.newInstance(arrayOfObject2);
/*      */       }
/*  644 */       translatedFields.put(arrayOfObjectStreamField, arrayOfObject1);
/*      */ 
/*  648 */       return (Object[])arrayOfObject1;
/*      */     } catch (Throwable localThrowable) {
/*      */     }
/*  651 */     throw new NoSuchFieldException();
/*      */   }
/*      */ 
/*      */   static boolean compareClassNames(String paramString1, String paramString2, char paramChar)
/*      */   {
/*  668 */     int i = paramString1.lastIndexOf(paramChar);
/*  669 */     if (i < 0) {
/*  670 */       i = 0;
/*      */     }
/*  672 */     int j = paramString2.lastIndexOf(paramChar);
/*  673 */     if (j < 0) {
/*  674 */       j = 0;
/*      */     }
/*  676 */     return paramString1.regionMatches(false, i, paramString2, j, paramString1.length() - i);
/*      */   }
/*      */ 
/*      */   final boolean typeEquals(ObjectStreamClass_1_3_1 paramObjectStreamClass_1_3_1)
/*      */   {
/*  686 */     return (this.suid == paramObjectStreamClass_1_3_1.suid) && (compareClassNames(this.name, paramObjectStreamClass_1_3_1.name, '.'));
/*      */   }
/*      */ 
/*      */   final void setSuperclass(ObjectStreamClass_1_3_1 paramObjectStreamClass_1_3_1)
/*      */   {
/*  694 */     this.superclass = paramObjectStreamClass_1_3_1;
/*      */   }
/*      */ 
/*      */   final ObjectStreamClass_1_3_1 getSuperclass()
/*      */   {
/*  701 */     return this.superclass;
/*      */   }
/*      */ 
/*      */   final boolean hasWriteObject()
/*      */   {
/*  708 */     return this.hasWriteObjectMethod;
/*      */   }
/*      */ 
/*      */   final boolean isCustomMarshaled() {
/*  712 */     return (hasWriteObject()) || (isExternalizable());
/*      */   }
/*      */ 
/*      */   boolean hasExternalizableBlockDataMode()
/*      */   {
/*  742 */     return this.hasExternalizableBlockData;
/*      */   }
/*      */ 
/*      */   final ObjectStreamClass_1_3_1 localClassDescriptor()
/*      */   {
/*  749 */     return this.localClassDesc;
/*      */   }
/*      */ 
/*      */   boolean isSerializable()
/*      */   {
/*  756 */     return this.serializable;
/*      */   }
/*      */ 
/*      */   boolean isExternalizable()
/*      */   {
/*  763 */     return this.externalizable;
/*      */   }
/*      */ 
/*      */   boolean isNonSerializable() {
/*  767 */     return (!this.externalizable) && (!this.serializable);
/*      */   }
/*      */ 
/*      */   private void computeFieldInfo()
/*      */   {
/*  776 */     this.primBytes = 0;
/*  777 */     this.objFields = 0;
/*      */ 
/*  779 */     for (int i = 0; i < this.fields.length; i++)
/*  780 */       switch (this.fields[i].getTypeCode()) {
/*      */       case 'B':
/*      */       case 'Z':
/*  783 */         this.primBytes += 1;
/*  784 */         break;
/*      */       case 'C':
/*      */       case 'S':
/*  787 */         this.primBytes += 2;
/*  788 */         break;
/*      */       case 'F':
/*      */       case 'I':
/*  792 */         this.primBytes += 4;
/*  793 */         break;
/*      */       case 'D':
/*      */       case 'J':
/*  796 */         this.primBytes += 8;
/*  797 */         break;
/*      */       case 'L':
/*      */       case '[':
/*  801 */         this.objFields += 1;
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
/*  808 */   private static long computeStructuralUID(ObjectStreamClass_1_3_1 paramObjectStreamClass_1_3_1, Class paramClass) { ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(512);
/*      */ 
/*  810 */     long l = 0L;
/*      */     try
/*      */     {
/*  813 */       if ((!Serializable.class.isAssignableFrom(paramClass)) || (paramClass.isInterface()))
/*      */       {
/*  815 */         return 0L;
/*      */       }
/*      */ 
/*  818 */       if (Externalizable.class.isAssignableFrom(paramClass)) {
/*  819 */         return 1L;
/*      */       }
/*      */ 
/*  822 */       MessageDigest localMessageDigest = MessageDigest.getInstance("SHA");
/*  823 */       DigestOutputStream localDigestOutputStream = new DigestOutputStream(localByteArrayOutputStream, localMessageDigest);
/*  824 */       DataOutputStream localDataOutputStream = new DataOutputStream(localDigestOutputStream);
/*      */ 
/*  827 */       Class localClass = paramClass.getSuperclass();
/*  828 */       if (localClass != null)
/*      */       {
/*  835 */         localDataOutputStream.writeLong(computeStructuralUID(lookup(localClass), localClass));
/*      */       }
/*      */ 
/*  838 */       if (paramObjectStreamClass_1_3_1.hasWriteObject())
/*  839 */         localDataOutputStream.writeInt(2);
/*      */       else {
/*  841 */         localDataOutputStream.writeInt(1);
/*      */       }
/*      */ 
/*  846 */       ObjectStreamField[] arrayOfObjectStreamField = paramObjectStreamClass_1_3_1.getFields();
/*      */ 
/*  851 */       int i = 0;
/*  852 */       for (int j = 0; j < arrayOfObjectStreamField.length; j++) {
/*  853 */         if (arrayOfObjectStreamField[j].getField() != null)
/*  854 */           i++;
/*      */       }
/*  856 */       Field[] arrayOfField = new Field[i];
/*  857 */       int k = 0; for (int m = 0; k < arrayOfObjectStreamField.length; k++) {
/*  858 */         if (arrayOfObjectStreamField[k].getField() != null) {
/*  859 */           arrayOfField[(m++)] = arrayOfObjectStreamField[k].getField();
/*      */         }
/*      */       }
/*      */ 
/*  863 */       if (arrayOfField.length > 1) {
/*  864 */         Arrays.sort(arrayOfField, compareMemberByName);
/*      */       }
/*  866 */       for (k = 0; k < arrayOfField.length; k++) {
/*  867 */         Field localField = arrayOfField[k];
/*      */ 
/*  872 */         int i1 = localField.getModifiers();
/*      */ 
/*  886 */         localDataOutputStream.writeUTF(localField.getName());
/*  887 */         localDataOutputStream.writeUTF(getSignature(localField.getType()));
/*      */       }
/*      */ 
/*  893 */       localDataOutputStream.flush();
/*  894 */       byte[] arrayOfByte = localMessageDigest.digest();
/*      */ 
/*  898 */       for (int n = 0; n < Math.min(8, arrayOfByte.length); n++)
/*  899 */         l += ((arrayOfByte[n] & 0xFF) << n * 8);
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/*  903 */       l = -1L;
/*      */     } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
/*  905 */       throw new SecurityException(localNoSuchAlgorithmException.getMessage());
/*      */     }
/*  907 */     return l;
/*      */   }
/*      */ 
/*      */   static String getSignature(Class paramClass)
/*      */   {
/*  914 */     String str = null;
/*  915 */     if (paramClass.isArray()) {
/*  916 */       Class localClass = paramClass;
/*  917 */       int i = 0;
/*  918 */       while (localClass.isArray()) {
/*  919 */         i++;
/*  920 */         localClass = localClass.getComponentType();
/*      */       }
/*  922 */       StringBuffer localStringBuffer = new StringBuffer();
/*  923 */       for (int j = 0; j < i; j++) {
/*  924 */         localStringBuffer.append("[");
/*      */       }
/*  926 */       localStringBuffer.append(getSignature(localClass));
/*  927 */       str = localStringBuffer.toString();
/*  928 */     } else if (paramClass.isPrimitive()) {
/*  929 */       if (paramClass == Integer.TYPE)
/*  930 */         str = "I";
/*  931 */       else if (paramClass == Byte.TYPE)
/*  932 */         str = "B";
/*  933 */       else if (paramClass == Long.TYPE)
/*  934 */         str = "J";
/*  935 */       else if (paramClass == Float.TYPE)
/*  936 */         str = "F";
/*  937 */       else if (paramClass == Double.TYPE)
/*  938 */         str = "D";
/*  939 */       else if (paramClass == Short.TYPE)
/*  940 */         str = "S";
/*  941 */       else if (paramClass == Character.TYPE)
/*  942 */         str = "C";
/*  943 */       else if (paramClass == Boolean.TYPE)
/*  944 */         str = "Z";
/*  945 */       else if (paramClass == Void.TYPE)
/*  946 */         str = "V";
/*      */     }
/*      */     else {
/*  949 */       str = "L" + paramClass.getName().replace('.', '/') + ";";
/*      */     }
/*  951 */     return str;
/*      */   }
/*      */ 
/*      */   static String getSignature(Method paramMethod)
/*      */   {
/*  958 */     StringBuffer localStringBuffer = new StringBuffer();
/*      */ 
/*  960 */     localStringBuffer.append("(");
/*      */ 
/*  962 */     Class[] arrayOfClass = paramMethod.getParameterTypes();
/*  963 */     for (int i = 0; i < arrayOfClass.length; i++) {
/*  964 */       localStringBuffer.append(getSignature(arrayOfClass[i]));
/*      */     }
/*  966 */     localStringBuffer.append(")");
/*  967 */     localStringBuffer.append(getSignature(paramMethod.getReturnType()));
/*  968 */     return localStringBuffer.toString();
/*      */   }
/*      */ 
/*      */   static String getSignature(Constructor paramConstructor)
/*      */   {
/*  975 */     StringBuffer localStringBuffer = new StringBuffer();
/*      */ 
/*  977 */     localStringBuffer.append("(");
/*      */ 
/*  979 */     Class[] arrayOfClass = paramConstructor.getParameterTypes();
/*  980 */     for (int i = 0; i < arrayOfClass.length; i++) {
/*  981 */       localStringBuffer.append(getSignature(arrayOfClass[i]));
/*      */     }
/*  983 */     localStringBuffer.append(")V");
/*  984 */     return localStringBuffer.toString();
/*      */   }
/*      */ 
/*      */   private static ObjectStreamClass_1_3_1 findDescriptorFor(Class paramClass)
/*      */   {
/* 1001 */     int i = paramClass.hashCode();
/* 1002 */     int j = (i & 0x7FFFFFFF) % descriptorFor.length;
/*      */     ObjectStreamClassEntry localObjectStreamClassEntry1;
/* 1007 */     while (((localObjectStreamClassEntry1 = descriptorFor[j]) != null) && (localObjectStreamClassEntry1.get() == null)) {
/* 1008 */       descriptorFor[j] = localObjectStreamClassEntry1.next;
/*      */     }
/*      */ 
/* 1014 */     ObjectStreamClassEntry localObjectStreamClassEntry2 = localObjectStreamClassEntry1;
/* 1015 */     while (localObjectStreamClassEntry1 != null) {
/* 1016 */       ObjectStreamClass_1_3_1 localObjectStreamClass_1_3_1 = (ObjectStreamClass_1_3_1)localObjectStreamClassEntry1.get();
/* 1017 */       if (localObjectStreamClass_1_3_1 == null)
/*      */       {
/* 1019 */         localObjectStreamClassEntry2.next = localObjectStreamClassEntry1.next;
/*      */       } else {
/* 1021 */         if (localObjectStreamClass_1_3_1.ofClass == paramClass)
/* 1022 */           return localObjectStreamClass_1_3_1;
/* 1023 */         localObjectStreamClassEntry2 = localObjectStreamClassEntry1;
/*      */       }
/* 1025 */       localObjectStreamClassEntry1 = localObjectStreamClassEntry1.next;
/*      */     }
/* 1027 */     return null;
/*      */   }
/*      */ 
/*      */   private static void insertDescriptorFor(ObjectStreamClass_1_3_1 paramObjectStreamClass_1_3_1)
/*      */   {
/* 1035 */     if (findDescriptorFor(paramObjectStreamClass_1_3_1.ofClass) != null) {
/* 1036 */       return;
/*      */     }
/*      */ 
/* 1039 */     int i = paramObjectStreamClass_1_3_1.ofClass.hashCode();
/* 1040 */     int j = (i & 0x7FFFFFFF) % descriptorFor.length;
/* 1041 */     ObjectStreamClassEntry localObjectStreamClassEntry = new ObjectStreamClassEntry(paramObjectStreamClass_1_3_1);
/* 1042 */     localObjectStreamClassEntry.next = descriptorFor[j];
/* 1043 */     descriptorFor[j] = localObjectStreamClassEntry;
/*      */   }
/*      */ 
/*      */   private static Field[] getDeclaredFields(Class paramClass) {
/* 1047 */     return (Field[])AccessController.doPrivileged(new PrivilegedAction() {
/*      */       public Object run() {
/* 1049 */         return this.val$clz.getDeclaredFields();
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*      */     try
/*      */     {
/* 1142 */       classSerializable = Class.forName("java.io.Serializable");
/* 1143 */       classExternalizable = Class.forName("java.io.Externalizable");
/*      */     } catch (Throwable localThrowable) {
/* 1145 */       System.err.println("Could not load java.io.Serializable or java.io.Externalizable.");
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class CompareClassByName
/*      */     implements Comparator
/*      */   {
/*      */     public int compare(Object paramObject1, Object paramObject2)
/*      */     {
/* 1186 */       Class localClass1 = (Class)paramObject1;
/* 1187 */       Class localClass2 = (Class)paramObject2;
/* 1188 */       return localClass1.getName().compareTo(localClass2.getName());
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class CompareMemberByName
/*      */     implements Comparator
/*      */   {
/*      */     public int compare(Object paramObject1, Object paramObject2)
/*      */     {
/* 1200 */       String str1 = ((Member)paramObject1).getName();
/* 1201 */       String str2 = ((Member)paramObject2).getName();
/*      */ 
/* 1203 */       if ((paramObject1 instanceof Method)) {
/* 1204 */         str1 = str1 + ObjectStreamClass_1_3_1.getSignature((Method)paramObject1);
/* 1205 */         str2 = str2 + ObjectStreamClass_1_3_1.getSignature((Method)paramObject2);
/* 1206 */       } else if ((paramObject1 instanceof Constructor)) {
/* 1207 */         str1 = str1 + ObjectStreamClass_1_3_1.getSignature((Constructor)paramObject1);
/* 1208 */         str2 = str2 + ObjectStreamClass_1_3_1.getSignature((Constructor)paramObject2);
/*      */       }
/* 1210 */       return str1.compareTo(str2);
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
/* 1225 */       int i = 0;
/* 1226 */       for (int j = 0; j < paramArrayOfMember.length; j++) {
/* 1227 */         if (!Modifier.isPrivate(paramArrayOfMember[j].getModifiers())) {
/* 1228 */           i++;
/*      */         }
/*      */       }
/* 1231 */       MethodSignature[] arrayOfMethodSignature = new MethodSignature[i];
/* 1232 */       int k = 0;
/* 1233 */       for (int m = 0; m < paramArrayOfMember.length; m++) {
/* 1234 */         if (!Modifier.isPrivate(paramArrayOfMember[m].getModifiers())) {
/* 1235 */           arrayOfMethodSignature[k] = new MethodSignature(paramArrayOfMember[m]);
/* 1236 */           k++;
/*      */         }
/*      */       }
/* 1239 */       if (k > 0)
/* 1240 */         Arrays.sort(arrayOfMethodSignature, arrayOfMethodSignature[0]);
/* 1241 */       return arrayOfMethodSignature;
/*      */     }
/*      */ 
/*      */     public int compare(Object paramObject1, Object paramObject2)
/*      */     {
/* 1248 */       if (paramObject1 == paramObject2) {
/* 1249 */         return 0;
/*      */       }
/* 1251 */       MethodSignature localMethodSignature1 = (MethodSignature)paramObject1;
/* 1252 */       MethodSignature localMethodSignature2 = (MethodSignature)paramObject2;
/*      */       int i;
/* 1255 */       if (isConstructor()) {
/* 1256 */         i = localMethodSignature1.signature.compareTo(localMethodSignature2.signature);
/*      */       } else {
/* 1258 */         i = localMethodSignature1.member.getName().compareTo(localMethodSignature2.member.getName());
/* 1259 */         if (i == 0)
/* 1260 */           i = localMethodSignature1.signature.compareTo(localMethodSignature2.signature);
/*      */       }
/* 1262 */       return i;
/*      */     }
/*      */ 
/*      */     private final boolean isConstructor() {
/* 1266 */       return this.member instanceof Constructor;
/*      */     }
/*      */     private MethodSignature(Member paramMember) {
/* 1269 */       this.member = paramMember;
/* 1270 */       if (isConstructor())
/* 1271 */         this.signature = ObjectStreamClass_1_3_1.getSignature((Constructor)paramMember);
/*      */       else
/* 1273 */         this.signature = ObjectStreamClass_1_3_1.getSignature((Method)paramMember);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class ObjectStreamClassEntry
/*      */   {
/*      */     ObjectStreamClassEntry next;
/*      */     private ObjectStreamClass_1_3_1 c;
/*      */ 
/*      */     ObjectStreamClassEntry(ObjectStreamClass_1_3_1 paramObjectStreamClass_1_3_1)
/*      */     {
/* 1167 */       this.c = paramObjectStreamClass_1_3_1;
/*      */     }
/*      */ 
/*      */     public Object get()
/*      */     {
/* 1173 */       return this.c;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.orbutil.ObjectStreamClass_1_3_1
 * JD-Core Version:    0.6.2
 */