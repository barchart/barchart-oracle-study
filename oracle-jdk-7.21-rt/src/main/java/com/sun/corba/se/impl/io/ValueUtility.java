/*     */ package com.sun.corba.se.impl.io;
/*     */ 
/*     */ import com.sun.corba.se.impl.util.RepositoryId;
/*     */ import com.sun.corba.se.impl.util.RepositoryIdCache;
/*     */ import com.sun.org.omg.CORBA.AttributeDescription;
/*     */ import com.sun.org.omg.CORBA.Initializer;
/*     */ import com.sun.org.omg.CORBA.OperationDescription;
/*     */ import com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescription;
/*     */ import com.sun.org.omg.CORBA._IDLTypeStub;
/*     */ import com.sun.org.omg.SendingContext.CodeBase;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.rmi.Remote;
/*     */ import java.util.Iterator;
/*     */ import java.util.Stack;
/*     */ import javax.rmi.CORBA.ValueHandler;
/*     */ import org.omg.CORBA.ORB;
/*     */ import org.omg.CORBA.TCKind;
/*     */ import org.omg.CORBA.TypeCode;
/*     */ import org.omg.CORBA.ValueMember;
/*     */ import sun.corba.JavaCorbaAccess;
/*     */ import sun.corba.SharedSecrets;
/*     */ 
/*     */ public class ValueUtility
/*     */ {
/*     */   public static final short PRIVATE_MEMBER = 0;
/*     */   public static final short PUBLIC_MEMBER = 1;
/*  60 */   private static final String[] primitiveConstants = { null, null, "S", "I", "S", "I", "F", "D", "Z", "C", "B", null, null, null, null, null, null, null, null, null, null, null, null, "J", "J", "D", "C", null, null, null, null, null, null };
/*     */ 
/*     */   public static String getSignature(ValueMember paramValueMember)
/*     */     throws ClassNotFoundException
/*     */   {
/* 112 */     if ((paramValueMember.type.kind().value() == 30) || (paramValueMember.type.kind().value() == 29) || (paramValueMember.type.kind().value() == 14))
/*     */     {
/* 115 */       Class localClass = RepositoryId.cache.getId(paramValueMember.id).getClassFromType();
/* 116 */       return ObjectStreamClass.getSignature(localClass);
/*     */     }
/*     */ 
/* 120 */     return primitiveConstants[paramValueMember.type.kind().value()];
/*     */   }
/*     */ 
/*     */   public static FullValueDescription translate(ORB paramORB, ObjectStreamClass paramObjectStreamClass, ValueHandler paramValueHandler)
/*     */   {
/* 128 */     FullValueDescription localFullValueDescription = new FullValueDescription();
/* 129 */     Class localClass1 = paramObjectStreamClass.forClass();
/*     */ 
/* 131 */     ValueHandlerImpl localValueHandlerImpl = (ValueHandlerImpl)paramValueHandler;
/* 132 */     String str = localValueHandlerImpl.createForAnyType(localClass1);
/*     */ 
/* 135 */     localFullValueDescription.name = localValueHandlerImpl.getUnqualifiedName(str);
/* 136 */     if (localFullValueDescription.name == null) {
/* 137 */       localFullValueDescription.name = "";
/*     */     }
/*     */ 
/* 140 */     localFullValueDescription.id = localValueHandlerImpl.getRMIRepositoryID(localClass1);
/* 141 */     if (localFullValueDescription.id == null) {
/* 142 */       localFullValueDescription.id = "";
/*     */     }
/*     */ 
/* 145 */     localFullValueDescription.is_abstract = ObjectStreamClassCorbaExt.isAbstractInterface(localClass1);
/*     */ 
/* 148 */     localFullValueDescription.is_custom = ((paramObjectStreamClass.hasWriteObject()) || (paramObjectStreamClass.isExternalizable()));
/*     */ 
/* 151 */     localFullValueDescription.defined_in = localValueHandlerImpl.getDefinedInId(str);
/* 152 */     if (localFullValueDescription.defined_in == null) {
/* 153 */       localFullValueDescription.defined_in = "";
/*     */     }
/*     */ 
/* 156 */     localFullValueDescription.version = localValueHandlerImpl.getSerialVersionUID(str);
/* 157 */     if (localFullValueDescription.version == null) {
/* 158 */       localFullValueDescription.version = "";
/*     */     }
/*     */ 
/* 161 */     localFullValueDescription.operations = new OperationDescription[0];
/*     */ 
/* 164 */     localFullValueDescription.attributes = new AttributeDescription[0];
/*     */ 
/* 168 */     IdentityKeyValueStack localIdentityKeyValueStack = new IdentityKeyValueStack(null);
/*     */ 
/* 170 */     localFullValueDescription.members = translateMembers(paramORB, paramObjectStreamClass, paramValueHandler, localIdentityKeyValueStack);
/*     */ 
/* 173 */     localFullValueDescription.initializers = new Initializer[0];
/*     */ 
/* 175 */     Class[] arrayOfClass = paramObjectStreamClass.forClass().getInterfaces();
/* 176 */     int i = 0;
/*     */ 
/* 179 */     localFullValueDescription.supported_interfaces = new String[arrayOfClass.length];
/* 180 */     for (int j = 0; j < arrayOfClass.length; 
/* 181 */       j++) {
/* 182 */       localFullValueDescription.supported_interfaces[j] = localValueHandlerImpl.createForAnyType(arrayOfClass[j]);
/*     */ 
/* 185 */       if ((!Remote.class.isAssignableFrom(arrayOfClass[j])) || (!Modifier.isPublic(arrayOfClass[j].getModifiers())))
/*     */       {
/* 187 */         i++;
/*     */       }
/*     */     }
/*     */ 
/* 191 */     localFullValueDescription.abstract_base_values = new String[i];
/* 192 */     for (j = 0; j < arrayOfClass.length; 
/* 193 */       j++) {
/* 194 */       if ((!Remote.class.isAssignableFrom(arrayOfClass[j])) || (!Modifier.isPublic(arrayOfClass[j].getModifiers())))
/*     */       {
/* 196 */         localFullValueDescription.abstract_base_values[j] = localValueHandlerImpl.createForAnyType(arrayOfClass[j]);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 201 */     localFullValueDescription.is_truncatable = false;
/*     */ 
/* 204 */     Class localClass2 = paramObjectStreamClass.forClass().getSuperclass();
/* 205 */     if (Serializable.class.isAssignableFrom(localClass2))
/* 206 */       localFullValueDescription.base_value = localValueHandlerImpl.getRMIRepositoryID(localClass2);
/*     */     else {
/* 208 */       localFullValueDescription.base_value = "";
/*     */     }
/*     */ 
/* 212 */     localFullValueDescription.type = paramORB.get_primitive_tc(TCKind.tk_value);
/*     */ 
/* 214 */     return localFullValueDescription;
/*     */   }
/*     */ 
/*     */   private static ValueMember[] translateMembers(ORB paramORB, ObjectStreamClass paramObjectStreamClass, ValueHandler paramValueHandler, IdentityKeyValueStack paramIdentityKeyValueStack)
/*     */   {
/* 223 */     ValueHandlerImpl localValueHandlerImpl = (ValueHandlerImpl)paramValueHandler;
/* 224 */     ObjectStreamField[] arrayOfObjectStreamField = paramObjectStreamClass.getFields();
/* 225 */     int i = arrayOfObjectStreamField.length;
/* 226 */     ValueMember[] arrayOfValueMember = new ValueMember[i];
/*     */ 
/* 229 */     for (int j = 0; j < i; j++) {
/* 230 */       String str = localValueHandlerImpl.getRMIRepositoryID(arrayOfObjectStreamField[j].getClazz());
/* 231 */       arrayOfValueMember[j] = new ValueMember();
/* 232 */       arrayOfValueMember[j].name = arrayOfObjectStreamField[j].getName();
/* 233 */       arrayOfValueMember[j].id = str;
/* 234 */       arrayOfValueMember[j].defined_in = localValueHandlerImpl.getDefinedInId(str);
/* 235 */       arrayOfValueMember[j].version = "1.0";
/* 236 */       arrayOfValueMember[j].type_def = new _IDLTypeStub();
/*     */ 
/* 238 */       if (arrayOfObjectStreamField[j].getField() == null)
/*     */       {
/* 244 */         arrayOfValueMember[j].access = 0;
/*     */       } else {
/* 246 */         int k = arrayOfObjectStreamField[j].getField().getModifiers();
/* 247 */         if (Modifier.isPublic(k))
/* 248 */           arrayOfValueMember[j].access = 1;
/*     */         else {
/* 250 */           arrayOfValueMember[j].access = 0;
/*     */         }
/*     */       }
/* 253 */       switch (arrayOfObjectStreamField[j].getTypeCode()) {
/*     */       case 'B':
/* 255 */         arrayOfValueMember[j].type = paramORB.get_primitive_tc(TCKind.tk_octet);
/* 256 */         break;
/*     */       case 'C':
/* 258 */         arrayOfValueMember[j].type = paramORB.get_primitive_tc(localValueHandlerImpl.getJavaCharTCKind());
/*     */ 
/* 260 */         break;
/*     */       case 'F':
/* 262 */         arrayOfValueMember[j].type = paramORB.get_primitive_tc(TCKind.tk_float);
/* 263 */         break;
/*     */       case 'D':
/* 265 */         arrayOfValueMember[j].type = paramORB.get_primitive_tc(TCKind.tk_double);
/* 266 */         break;
/*     */       case 'I':
/* 268 */         arrayOfValueMember[j].type = paramORB.get_primitive_tc(TCKind.tk_long);
/* 269 */         break;
/*     */       case 'J':
/* 271 */         arrayOfValueMember[j].type = paramORB.get_primitive_tc(TCKind.tk_longlong);
/* 272 */         break;
/*     */       case 'S':
/* 274 */         arrayOfValueMember[j].type = paramORB.get_primitive_tc(TCKind.tk_short);
/* 275 */         break;
/*     */       case 'Z':
/* 277 */         arrayOfValueMember[j].type = paramORB.get_primitive_tc(TCKind.tk_boolean);
/* 278 */         break;
/*     */       case 'E':
/*     */       case 'G':
/*     */       case 'H':
/*     */       case 'K':
/*     */       case 'L':
/*     */       case 'M':
/*     */       case 'N':
/*     */       case 'O':
/*     */       case 'P':
/*     */       case 'Q':
/*     */       case 'R':
/*     */       case 'T':
/*     */       case 'U':
/*     */       case 'V':
/*     */       case 'W':
/*     */       case 'X':
/*     */       case 'Y':
/*     */       default:
/* 284 */         arrayOfValueMember[j].type = createTypeCodeForClassInternal(paramORB, arrayOfObjectStreamField[j].getClazz(), localValueHandlerImpl, paramIdentityKeyValueStack);
/*     */ 
/* 286 */         arrayOfValueMember[j].id = localValueHandlerImpl.createForAnyType(arrayOfObjectStreamField[j].getType());
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 292 */     return arrayOfValueMember;
/*     */   }
/*     */ 
/*     */   private static boolean exists(String paramString, String[] paramArrayOfString) {
/* 296 */     for (int i = 0; i < paramArrayOfString.length; i++) {
/* 297 */       if (paramString.equals(paramArrayOfString[i]))
/* 298 */         return true;
/*     */     }
/* 300 */     return false;
/*     */   }
/*     */ 
/*     */   public static boolean isAssignableFrom(String paramString, FullValueDescription paramFullValueDescription, CodeBase paramCodeBase)
/*     */   {
/* 306 */     if (exists(paramString, paramFullValueDescription.supported_interfaces)) {
/* 307 */       return true;
/*     */     }
/* 309 */     if (paramString.equals(paramFullValueDescription.id)) {
/* 310 */       return true;
/*     */     }
/* 312 */     if ((paramFullValueDescription.base_value != null) && (!paramFullValueDescription.base_value.equals("")))
/*     */     {
/* 314 */       FullValueDescription localFullValueDescription = paramCodeBase.meta(paramFullValueDescription.base_value);
/*     */ 
/* 316 */       return isAssignableFrom(paramString, localFullValueDescription, paramCodeBase);
/*     */     }
/*     */ 
/* 319 */     return false;
/*     */   }
/*     */ 
/*     */   public static TypeCode createTypeCodeForClass(ORB paramORB, Class paramClass, ValueHandler paramValueHandler)
/*     */   {
/* 325 */     IdentityKeyValueStack localIdentityKeyValueStack = new IdentityKeyValueStack(null);
/*     */ 
/* 327 */     TypeCode localTypeCode = createTypeCodeForClassInternal(paramORB, paramClass, paramValueHandler, localIdentityKeyValueStack);
/* 328 */     return localTypeCode;
/*     */   }
/*     */ 
/*     */   private static TypeCode createTypeCodeForClassInternal(ORB paramORB, Class paramClass, ValueHandler paramValueHandler, IdentityKeyValueStack paramIdentityKeyValueStack)
/*     */   {
/* 337 */     TypeCode localTypeCode = null;
/* 338 */     String str = (String)paramIdentityKeyValueStack.get(paramClass);
/* 339 */     if (str != null) {
/* 340 */       return paramORB.create_recursive_tc(str);
/*     */     }
/* 342 */     str = paramValueHandler.getRMIRepositoryID(paramClass);
/* 343 */     if (str == null) str = "";
/*     */ 
/* 346 */     paramIdentityKeyValueStack.push(paramClass, str);
/* 347 */     localTypeCode = createTypeCodeInternal(paramORB, paramClass, paramValueHandler, str, paramIdentityKeyValueStack);
/* 348 */     paramIdentityKeyValueStack.pop();
/* 349 */     return localTypeCode;
/*     */   }
/*     */ 
/*     */   private static TypeCode createTypeCodeInternal(ORB paramORB, Class paramClass, ValueHandler paramValueHandler, String paramString, IdentityKeyValueStack paramIdentityKeyValueStack)
/*     */   {
/* 400 */     if (paramClass.isArray())
/*     */     {
/* 402 */       localObject = paramClass.getComponentType();
/*     */       TypeCode localTypeCode1;
/* 404 */       if (((Class)localObject).isPrimitive()) {
/* 405 */         localTypeCode1 = getPrimitiveTypeCodeForClass(paramORB, (Class)localObject, paramValueHandler);
/*     */       }
/*     */       else
/*     */       {
/* 410 */         localTypeCode1 = createTypeCodeForClassInternal(paramORB, (Class)localObject, paramValueHandler, paramIdentityKeyValueStack);
/*     */       }
/*     */ 
/* 413 */       localTypeCode2 = paramORB.create_sequence_tc(0, localTypeCode1);
/* 414 */       return paramORB.create_value_box_tc(paramString, "Sequence", localTypeCode2);
/* 415 */     }if (paramClass == String.class)
/*     */     {
/* 417 */       localObject = paramORB.create_string_tc(0);
/* 418 */       return paramORB.create_value_box_tc(paramString, "StringValue", (TypeCode)localObject);
/* 419 */     }if (Remote.class.isAssignableFrom(paramClass))
/* 420 */       return paramORB.get_primitive_tc(TCKind.tk_objref);
/* 421 */     if (org.omg.CORBA.Object.class.isAssignableFrom(paramClass)) {
/* 422 */       return paramORB.get_primitive_tc(TCKind.tk_objref);
/*     */     }
/*     */ 
/* 427 */     java.lang.Object localObject = ObjectStreamClass.lookup(paramClass);
/*     */ 
/* 429 */     if (localObject == null) {
/* 430 */       return paramORB.create_value_box_tc(paramString, "Value", paramORB.get_primitive_tc(TCKind.tk_value));
/*     */     }
/*     */ 
/* 435 */     short s = ((ObjectStreamClass)localObject).isCustomMarshaled() ? 1 : 0;
/*     */ 
/* 438 */     TypeCode localTypeCode2 = null;
/* 439 */     Class localClass = paramClass.getSuperclass();
/* 440 */     if ((localClass != null) && (Serializable.class.isAssignableFrom(localClass))) {
/* 441 */       localTypeCode2 = createTypeCodeForClassInternal(paramORB, localClass, paramValueHandler, paramIdentityKeyValueStack);
/*     */     }
/*     */ 
/* 445 */     ValueMember[] arrayOfValueMember = translateMembers(paramORB, (ObjectStreamClass)localObject, paramValueHandler, paramIdentityKeyValueStack);
/*     */ 
/* 447 */     return paramORB.create_value_tc(paramString, paramClass.getName(), s, localTypeCode2, arrayOfValueMember);
/*     */   }
/*     */ 
/*     */   public static TypeCode getPrimitiveTypeCodeForClass(ORB paramORB, Class paramClass, ValueHandler paramValueHandler)
/*     */   {
/* 454 */     if (paramClass == Integer.TYPE)
/* 455 */       return paramORB.get_primitive_tc(TCKind.tk_long);
/* 456 */     if (paramClass == Byte.TYPE)
/* 457 */       return paramORB.get_primitive_tc(TCKind.tk_octet);
/* 458 */     if (paramClass == Long.TYPE)
/* 459 */       return paramORB.get_primitive_tc(TCKind.tk_longlong);
/* 460 */     if (paramClass == Float.TYPE)
/* 461 */       return paramORB.get_primitive_tc(TCKind.tk_float);
/* 462 */     if (paramClass == Double.TYPE)
/* 463 */       return paramORB.get_primitive_tc(TCKind.tk_double);
/* 464 */     if (paramClass == Short.TYPE)
/* 465 */       return paramORB.get_primitive_tc(TCKind.tk_short);
/* 466 */     if (paramClass == Character.TYPE)
/* 467 */       return paramORB.get_primitive_tc(((ValueHandlerImpl)paramValueHandler).getJavaCharTCKind());
/* 468 */     if (paramClass == Boolean.TYPE) {
/* 469 */       return paramORB.get_primitive_tc(TCKind.tk_boolean);
/*     */     }
/*     */ 
/* 472 */     return paramORB.get_primitive_tc(TCKind.tk_any);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  97 */     SharedSecrets.setJavaCorbaAccess(new JavaCorbaAccess() {
/*     */       public ValueHandlerImpl newValueHandlerImpl() {
/*  99 */         return ValueHandlerImpl.getInstance();
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private static class IdentityKeyValueStack
/*     */   {
/* 367 */     Stack pairs = null;
/*     */ 
/*     */     java.lang.Object get(java.lang.Object paramObject) {
/* 370 */       if (this.pairs == null) {
/* 371 */         return null;
/*     */       }
/* 373 */       for (Iterator localIterator = this.pairs.iterator(); localIterator.hasNext(); ) {
/* 374 */         KeyValuePair localKeyValuePair = (KeyValuePair)localIterator.next();
/* 375 */         if (localKeyValuePair.key == paramObject) {
/* 376 */           return localKeyValuePair.value;
/*     */         }
/*     */       }
/* 379 */       return null;
/*     */     }
/*     */ 
/*     */     void push(java.lang.Object paramObject1, java.lang.Object paramObject2) {
/* 383 */       if (this.pairs == null) {
/* 384 */         this.pairs = new Stack();
/*     */       }
/* 386 */       this.pairs.push(new KeyValuePair(paramObject1, paramObject2));
/*     */     }
/*     */ 
/*     */     void pop() {
/* 390 */       this.pairs.pop();
/*     */     }
/*     */ 
/*     */     private static class KeyValuePair
/*     */     {
/*     */       java.lang.Object key;
/*     */       java.lang.Object value;
/*     */ 
/*     */       KeyValuePair(java.lang.Object paramObject1, java.lang.Object paramObject2)
/*     */       {
/* 359 */         this.key = paramObject1;
/* 360 */         this.value = paramObject2;
/*     */       }
/*     */       boolean equals(KeyValuePair paramKeyValuePair) {
/* 363 */         return paramKeyValuePair.key == this.key;
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.io.ValueUtility
 * JD-Core Version:    0.6.2
 */