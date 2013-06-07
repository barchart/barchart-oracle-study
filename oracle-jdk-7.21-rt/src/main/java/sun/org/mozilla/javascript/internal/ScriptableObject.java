/*      */ package sun.org.mozilla.javascript.internal;
/*      */ 
/*      */ import java.lang.annotation.Annotation;
/*      */ import java.lang.reflect.AccessibleObject;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Member;
/*      */ import java.lang.reflect.Method;
/*      */ import java.lang.reflect.Modifier;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Map;
/*      */ import sun.org.mozilla.javascript.internal.annotations.JSConstructor;
/*      */ import sun.org.mozilla.javascript.internal.annotations.JSFunction;
/*      */ import sun.org.mozilla.javascript.internal.annotations.JSGetter;
/*      */ import sun.org.mozilla.javascript.internal.annotations.JSSetter;
/*      */ import sun.org.mozilla.javascript.internal.annotations.JSStaticFunction;
/*      */ import sun.org.mozilla.javascript.internal.debug.DebuggableObject;
/*      */ 
/*      */ public abstract class ScriptableObject
/*      */   implements Scriptable, DebuggableObject, ConstProperties
/*      */ {
/*      */   public static final int EMPTY = 0;
/*      */   public static final int READONLY = 1;
/*      */   public static final int DONTENUM = 2;
/*      */   public static final int PERMANENT = 4;
/*      */   public static final int UNINITIALIZED_CONST = 8;
/*      */   public static final int CONST = 13;
/*      */   private Scriptable prototypeObject;
/*      */   private Scriptable parentScopeObject;
/*  145 */   private static final Slot REMOVED = new Slot(null, 0, 1);
/*      */   private transient Slot[] slots;
/*      */   private int count;
/*      */   private transient Slot firstAdded;
/*      */   private transient Slot lastAdded;
/*  161 */   private transient Slot lastAccess = REMOVED;
/*      */   private volatile Map<Object, Object> associatedValues;
/*      */   private static final int SLOT_QUERY = 1;
/*      */   private static final int SLOT_MODIFY = 2;
/*      */   private static final int SLOT_REMOVE = 3;
/*      */   private static final int SLOT_MODIFY_GETTER_SETTER = 4;
/*      */   private static final int SLOT_MODIFY_CONST = 5;
/*      */   private static final int SLOT_CONVERT_ACCESSOR_TO_DATA = 6;
/*  172 */   private boolean isExtensible = true;
/*      */ 
/*      */   protected static ScriptableObject buildDataDescriptor(Scriptable paramScriptable, Object paramObject, int paramInt)
/*      */   {
/*  221 */     NativeObject localNativeObject = new NativeObject();
/*  222 */     ScriptRuntime.setObjectProtoAndParent(localNativeObject, paramScriptable);
/*      */ 
/*  224 */     localNativeObject.defineProperty("value", paramObject, 0);
/*  225 */     localNativeObject.defineProperty("writable", Boolean.valueOf((paramInt & 0x1) == 0), 0);
/*  226 */     localNativeObject.defineProperty("enumerable", Boolean.valueOf((paramInt & 0x2) == 0), 0);
/*  227 */     localNativeObject.defineProperty("configurable", Boolean.valueOf((paramInt & 0x4) == 0), 0);
/*  228 */     return localNativeObject;
/*      */   }
/*      */ 
/*      */   static void checkValidAttributes(int paramInt)
/*      */   {
/*  256 */     if ((paramInt & 0xFFFFFFF0) != 0)
/*  257 */       throw new IllegalArgumentException(String.valueOf(paramInt));
/*      */   }
/*      */ 
/*      */   public ScriptableObject()
/*      */   {
/*      */   }
/*      */ 
/*      */   public ScriptableObject(Scriptable paramScriptable1, Scriptable paramScriptable2)
/*      */   {
/*  267 */     if (paramScriptable1 == null) {
/*  268 */       throw new IllegalArgumentException();
/*      */     }
/*  270 */     this.parentScopeObject = paramScriptable1;
/*  271 */     this.prototypeObject = paramScriptable2;
/*      */   }
/*      */ 
/*      */   public String getTypeOf()
/*      */   {
/*  280 */     return avoidObjectDetection() ? "undefined" : "object";
/*      */   }
/*      */ 
/*      */   public abstract String getClassName();
/*      */ 
/*      */   public boolean has(String paramString, Scriptable paramScriptable)
/*      */   {
/*  301 */     return null != getSlot(paramString, 0, 1);
/*      */   }
/*      */ 
/*      */   public boolean has(int paramInt, Scriptable paramScriptable)
/*      */   {
/*  313 */     return null != getSlot(null, paramInt, 1);
/*      */   }
/*      */ 
/*      */   public Object get(String paramString, Scriptable paramScriptable)
/*      */   {
/*  328 */     return getImpl(paramString, 0, paramScriptable);
/*      */   }
/*      */ 
/*      */   public Object get(int paramInt, Scriptable paramScriptable)
/*      */   {
/*  340 */     return getImpl(null, paramInt, paramScriptable);
/*      */   }
/*      */ 
/*      */   public void put(String paramString, Scriptable paramScriptable, Object paramObject)
/*      */   {
/*  360 */     if (putImpl(paramString, 0, paramScriptable, paramObject, 0)) {
/*  361 */       return;
/*      */     }
/*  363 */     if (paramScriptable == this) throw Kit.codeBug();
/*  364 */     paramScriptable.put(paramString, paramScriptable, paramObject);
/*      */   }
/*      */ 
/*      */   public void put(int paramInt, Scriptable paramScriptable, Object paramObject)
/*      */   {
/*  376 */     if (putImpl(null, paramInt, paramScriptable, paramObject, 0)) {
/*  377 */       return;
/*      */     }
/*  379 */     if (paramScriptable == this) throw Kit.codeBug();
/*  380 */     paramScriptable.put(paramInt, paramScriptable, paramObject);
/*      */   }
/*      */ 
/*      */   public void delete(String paramString)
/*      */   {
/*  393 */     checkNotSealed(paramString, 0);
/*  394 */     accessSlot(paramString, 0, 3);
/*      */   }
/*      */ 
/*      */   public void delete(int paramInt)
/*      */   {
/*  407 */     checkNotSealed(null, paramInt);
/*  408 */     accessSlot(null, paramInt, 3);
/*      */   }
/*      */ 
/*      */   public void putConst(String paramString, Scriptable paramScriptable, Object paramObject)
/*      */   {
/*  428 */     if (putImpl(paramString, 0, paramScriptable, paramObject, 1)) {
/*  429 */       return;
/*      */     }
/*  431 */     if (paramScriptable == this) throw Kit.codeBug();
/*  432 */     if ((paramScriptable instanceof ConstProperties))
/*  433 */       ((ConstProperties)paramScriptable).putConst(paramString, paramScriptable, paramObject);
/*      */     else
/*  435 */       paramScriptable.put(paramString, paramScriptable, paramObject);
/*      */   }
/*      */ 
/*      */   public void defineConst(String paramString, Scriptable paramScriptable)
/*      */   {
/*  440 */     if (putImpl(paramString, 0, paramScriptable, Undefined.instance, 8)) {
/*  441 */       return;
/*      */     }
/*  443 */     if (paramScriptable == this) throw Kit.codeBug();
/*  444 */     if ((paramScriptable instanceof ConstProperties))
/*  445 */       ((ConstProperties)paramScriptable).defineConst(paramString, paramScriptable);
/*      */   }
/*      */ 
/*      */   public boolean isConst(String paramString)
/*      */   {
/*  455 */     Slot localSlot = getSlot(paramString, 0, 1);
/*  456 */     if (localSlot == null) {
/*  457 */       return false;
/*      */     }
/*  459 */     return (localSlot.getAttributes() & 0x5) == 5;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public final int getAttributes(String paramString, Scriptable paramScriptable)
/*      */   {
/*  469 */     return getAttributes(paramString);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public final int getAttributes(int paramInt, Scriptable paramScriptable)
/*      */   {
/*  478 */     return getAttributes(paramInt);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public final void setAttributes(String paramString, Scriptable paramScriptable, int paramInt)
/*      */   {
/*  488 */     setAttributes(paramString, paramInt);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void setAttributes(int paramInt1, Scriptable paramScriptable, int paramInt2)
/*      */   {
/*  498 */     setAttributes(paramInt1, paramInt2);
/*      */   }
/*      */ 
/*      */   public int getAttributes(String paramString)
/*      */   {
/*  518 */     return findAttributeSlot(paramString, 0, 1).getAttributes();
/*      */   }
/*      */ 
/*      */   public int getAttributes(int paramInt)
/*      */   {
/*  536 */     return findAttributeSlot(null, paramInt, 1).getAttributes();
/*      */   }
/*      */ 
/*      */   public void setAttributes(String paramString, int paramInt)
/*      */   {
/*  562 */     checkNotSealed(paramString, 0);
/*  563 */     findAttributeSlot(paramString, 0, 2).setAttributes(paramInt);
/*      */   }
/*      */ 
/*      */   public void setAttributes(int paramInt1, int paramInt2)
/*      */   {
/*  580 */     checkNotSealed(null, paramInt1);
/*  581 */     findAttributeSlot(null, paramInt1, 2).setAttributes(paramInt2);
/*      */   }
/*      */ 
/*      */   public void setGetterOrSetter(String paramString, int paramInt, Callable paramCallable, boolean paramBoolean)
/*      */   {
/*  590 */     setGetterOrSetter(paramString, paramInt, paramCallable, paramBoolean, false);
/*      */   }
/*      */ 
/*      */   private void setGetterOrSetter(String paramString, int paramInt, Callable paramCallable, boolean paramBoolean1, boolean paramBoolean2)
/*      */   {
/*  595 */     if ((paramString != null) && (paramInt != 0)) {
/*  596 */       throw new IllegalArgumentException(paramString);
/*      */     }
/*  598 */     if (!paramBoolean2)
/*  599 */       checkNotSealed(paramString, paramInt);
/*      */     GetterSlot localGetterSlot;
/*  603 */     if (isExtensible()) {
/*  604 */       localGetterSlot = (GetterSlot)getSlot(paramString, paramInt, 4);
/*      */     } else {
/*  606 */       localGetterSlot = (GetterSlot)getSlot(paramString, paramInt, 1);
/*  607 */       if (localGetterSlot == null) {
/*  608 */         return;
/*      */       }
/*      */     }
/*  611 */     if (!paramBoolean2) {
/*  612 */       localGetterSlot.checkNotReadonly();
/*      */     }
/*  614 */     if (paramBoolean1)
/*  615 */       localGetterSlot.setter = paramCallable;
/*      */     else {
/*  617 */       localGetterSlot.getter = paramCallable;
/*      */     }
/*  619 */     localGetterSlot.value = Undefined.instance;
/*      */   }
/*      */ 
/*      */   public Object getGetterOrSetter(String paramString, int paramInt, boolean paramBoolean)
/*      */   {
/*  637 */     if ((paramString != null) && (paramInt != 0))
/*  638 */       throw new IllegalArgumentException(paramString);
/*  639 */     Slot localSlot = getSlot(paramString, paramInt, 1);
/*  640 */     if (localSlot == null)
/*  641 */       return null;
/*  642 */     if ((localSlot instanceof GetterSlot)) {
/*  643 */       GetterSlot localGetterSlot = (GetterSlot)localSlot;
/*  644 */       Object localObject = paramBoolean ? localGetterSlot.setter : localGetterSlot.getter;
/*  645 */       return localObject != null ? localObject : Undefined.instance;
/*      */     }
/*  647 */     return Undefined.instance;
/*      */   }
/*      */ 
/*      */   protected boolean isGetterOrSetter(String paramString, int paramInt, boolean paramBoolean)
/*      */   {
/*  658 */     Slot localSlot = getSlot(paramString, paramInt, 1);
/*  659 */     if ((localSlot instanceof GetterSlot)) {
/*  660 */       if ((paramBoolean) && (((GetterSlot)localSlot).setter != null)) return true;
/*  661 */       if ((!paramBoolean) && (((GetterSlot)localSlot).getter != null)) return true;
/*      */     }
/*  663 */     return false;
/*      */   }
/*      */ 
/*      */   void addLazilyInitializedValue(String paramString, int paramInt1, LazilyLoadedCtor paramLazilyLoadedCtor, int paramInt2)
/*      */   {
/*  669 */     if ((paramString != null) && (paramInt1 != 0))
/*  670 */       throw new IllegalArgumentException(paramString);
/*  671 */     checkNotSealed(paramString, paramInt1);
/*  672 */     GetterSlot localGetterSlot = (GetterSlot)getSlot(paramString, paramInt1, 4);
/*      */ 
/*  674 */     localGetterSlot.setAttributes(paramInt2);
/*  675 */     localGetterSlot.getter = null;
/*  676 */     localGetterSlot.setter = null;
/*  677 */     localGetterSlot.value = paramLazilyLoadedCtor;
/*      */   }
/*      */ 
/*      */   public Scriptable getPrototype()
/*      */   {
/*  685 */     return this.prototypeObject;
/*      */   }
/*      */ 
/*      */   public void setPrototype(Scriptable paramScriptable)
/*      */   {
/*  693 */     this.prototypeObject = paramScriptable;
/*      */   }
/*      */ 
/*      */   public Scriptable getParentScope()
/*      */   {
/*  701 */     return this.parentScopeObject;
/*      */   }
/*      */ 
/*      */   public void setParentScope(Scriptable paramScriptable)
/*      */   {
/*  709 */     this.parentScopeObject = paramScriptable;
/*      */   }
/*      */ 
/*      */   public Object[] getIds()
/*      */   {
/*  724 */     return getIds(false);
/*      */   }
/*      */ 
/*      */   public Object[] getAllIds()
/*      */   {
/*  739 */     return getIds(true);
/*      */   }
/*      */ 
/*      */   public Object getDefaultValue(Class<?> paramClass)
/*      */   {
/*  758 */     return getDefaultValue(this, paramClass);
/*      */   }
/*      */ 
/*      */   public static Object getDefaultValue(Scriptable paramScriptable, Class<?> paramClass)
/*      */   {
/*  763 */     Context localContext = null;
/*  764 */     for (int i = 0; i < 2; i++)
/*      */     {
/*      */       int j;
/*  766 */       if (paramClass == ScriptRuntime.StringClass)
/*  767 */         j = i == 0 ? 1 : 0;
/*      */       else
/*  769 */         j = i == 1 ? 1 : 0;
/*      */       String str2;
/*      */       Object[] arrayOfObject;
/*  774 */       if (j != 0) {
/*  775 */         str2 = "toString";
/*  776 */         arrayOfObject = ScriptRuntime.emptyArgs;
/*      */       } else {
/*  778 */         str2 = "valueOf";
/*  779 */         arrayOfObject = new Object[1];
/*      */ 
/*  781 */         if (paramClass == null)
/*  782 */           localObject1 = "undefined";
/*  783 */         else if (paramClass == ScriptRuntime.StringClass)
/*  784 */           localObject1 = "string";
/*  785 */         else if (paramClass == ScriptRuntime.ScriptableClass)
/*  786 */           localObject1 = "object";
/*  787 */         else if (paramClass == ScriptRuntime.FunctionClass)
/*  788 */           localObject1 = "function";
/*  789 */         else if ((paramClass == ScriptRuntime.BooleanClass) || (paramClass == Boolean.TYPE))
/*      */         {
/*  792 */           localObject1 = "boolean";
/*  793 */         } else if ((paramClass == ScriptRuntime.NumberClass) || (paramClass == ScriptRuntime.ByteClass) || (paramClass == Byte.TYPE) || (paramClass == ScriptRuntime.ShortClass) || (paramClass == Short.TYPE) || (paramClass == ScriptRuntime.IntegerClass) || (paramClass == Integer.TYPE) || (paramClass == ScriptRuntime.FloatClass) || (paramClass == Float.TYPE) || (paramClass == ScriptRuntime.DoubleClass) || (paramClass == Double.TYPE))
/*      */         {
/*  805 */           localObject1 = "number";
/*      */         }
/*  807 */         else throw Context.reportRuntimeError1("msg.invalid.type", paramClass.toString());
/*      */ 
/*  810 */         arrayOfObject[0] = localObject1;
/*      */       }
/*  812 */       Object localObject1 = getProperty(paramScriptable, str2);
/*  813 */       if ((localObject1 instanceof Function))
/*      */       {
/*  815 */         Function localFunction = (Function)localObject1;
/*  816 */         if (localContext == null)
/*  817 */           localContext = Context.getContext();
/*  818 */         localObject1 = localFunction.call(localContext, localFunction.getParentScope(), paramScriptable, arrayOfObject);
/*  819 */         if (localObject1 != null) {
/*  820 */           if (!(localObject1 instanceof ConstProperties)) {
/*  821 */             return localObject1;
/*      */           }
/*  823 */           if ((paramClass == ScriptRuntime.ScriptableClass) || (paramClass == ScriptRuntime.FunctionClass))
/*      */           {
/*  826 */             return localObject1;
/*      */           }
/*  828 */           if ((j != 0) && ((localObject1 instanceof Wrapper)))
/*      */           {
/*  831 */             Object localObject2 = ((Wrapper)localObject1).unwrap();
/*  832 */             if ((localObject2 instanceof String))
/*  833 */               return localObject2;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  838 */     String str1 = paramClass == null ? "undefined" : paramClass.getName();
/*  839 */     throw ScriptRuntime.typeError1("msg.default.value", str1);
/*      */   }
/*      */ 
/*      */   public boolean hasInstance(Scriptable paramScriptable)
/*      */   {
/*  857 */     return ScriptRuntime.jsDelegatesTo(paramScriptable, this);
/*      */   }
/*      */ 
/*      */   public boolean avoidObjectDetection()
/*      */   {
/*  872 */     return false;
/*      */   }
/*      */ 
/*      */   protected Object equivalentValues(Object paramObject)
/*      */   {
/*  890 */     return this == paramObject ? Boolean.TRUE : ConstProperties.NOT_FOUND;
/*      */   }
/*      */ 
/*      */   public static <T extends Scriptable> void defineClass(Scriptable paramScriptable, Class<T> paramClass)
/*      */     throws IllegalAccessException, InstantiationException, InvocationTargetException
/*      */   {
/*  991 */     defineClass(paramScriptable, paramClass, false, false);
/*      */   }
/*      */ 
/*      */   public static <T extends Scriptable> void defineClass(Scriptable paramScriptable, Class<T> paramClass, boolean paramBoolean)
/*      */     throws IllegalAccessException, InstantiationException, InvocationTargetException
/*      */   {
/* 1022 */     defineClass(paramScriptable, paramClass, paramBoolean, false);
/*      */   }
/*      */ 
/*      */   public static <T extends Scriptable> String defineClass(Scriptable paramScriptable, Class<T> paramClass, boolean paramBoolean1, boolean paramBoolean2)
/*      */     throws IllegalAccessException, InstantiationException, InvocationTargetException
/*      */   {
/* 1058 */     BaseFunction localBaseFunction = buildClassCtor(paramScriptable, paramClass, paramBoolean1, paramBoolean2);
/*      */ 
/* 1060 */     if (localBaseFunction == null)
/* 1061 */       return null;
/* 1062 */     String str = localBaseFunction.getClassPrototype().getClassName();
/* 1063 */     defineProperty(paramScriptable, str, localBaseFunction, 2);
/* 1064 */     return str;
/*      */   }
/*      */ 
/*      */   static <T extends Scriptable> BaseFunction buildClassCtor(Scriptable paramScriptable, Class<T> paramClass, boolean paramBoolean1, boolean paramBoolean2)
/*      */     throws IllegalAccessException, InstantiationException, InvocationTargetException
/*      */   {
/* 1074 */     Method[] arrayOfMethod = FunctionObject.getMethodList(paramClass);
/* 1075 */     for (int i = 0; i < arrayOfMethod.length; i++) {
/* 1076 */       localObject1 = arrayOfMethod[i];
/* 1077 */       if (((Method)localObject1).getName().equals("init"))
/*      */       {
/* 1079 */         Class[] arrayOfClass = ((Method)localObject1).getParameterTypes();
/* 1080 */         if ((arrayOfClass.length == 3) && (arrayOfClass[0] == ScriptRuntime.ContextClass) && (arrayOfClass[1] == ScriptRuntime.ScriptableClass) && (arrayOfClass[2] == Boolean.TYPE) && (Modifier.isStatic(((Method)localObject1).getModifiers())))
/*      */         {
/* 1086 */           localObject2 = new Object[] { Context.getContext(), paramScriptable, paramBoolean1 ? Boolean.TRUE : Boolean.FALSE };
/*      */ 
/* 1088 */           ((Method)localObject1).invoke(null, (Object[])localObject2);
/* 1089 */           return null;
/*      */         }
/* 1091 */         if ((arrayOfClass.length == 1) && (arrayOfClass[0] == ScriptRuntime.ScriptableClass) && (Modifier.isStatic(((Method)localObject1).getModifiers())))
/*      */         {
/* 1095 */           localObject2 = new Object[] { paramScriptable };
/* 1096 */           ((Method)localObject1).invoke(null, (Object[])localObject2);
/* 1097 */           return null;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1105 */     Constructor[] arrayOfConstructor = paramClass.getConstructors();
/* 1106 */     Object localObject1 = null;
/* 1107 */     for (int j = 0; j < arrayOfConstructor.length; j++) {
/* 1108 */       if (arrayOfConstructor[j].getParameterTypes().length == 0) {
/* 1109 */         localObject1 = arrayOfConstructor[j];
/* 1110 */         break;
/*      */       }
/*      */     }
/* 1113 */     if (localObject1 == null) {
/* 1114 */       throw Context.reportRuntimeError1("msg.zero.arg.ctor", paramClass.getName());
/*      */     }
/*      */ 
/* 1118 */     Scriptable localScriptable1 = (ConstProperties)((Constructor)localObject1).newInstance(ScriptRuntime.emptyArgs);
/* 1119 */     Object localObject2 = localScriptable1.getClassName();
/*      */ 
/* 1123 */     Scriptable localScriptable2 = null;
/* 1124 */     if (paramBoolean2) {
/* 1125 */       Class localClass1 = paramClass.getSuperclass();
/* 1126 */       if ((ScriptRuntime.ScriptableClass.isAssignableFrom(localClass1)) && (!Modifier.isAbstract(localClass1.getModifiers())))
/*      */       {
/* 1129 */         Class localClass2 = extendsScriptable(localClass1);
/*      */ 
/* 1131 */         String str1 = defineClass(paramScriptable, localClass2, paramBoolean1, paramBoolean2);
/*      */ 
/* 1133 */         if (str1 != null) {
/* 1134 */           localScriptable2 = getClassPrototype(paramScriptable, str1);
/*      */         }
/*      */       }
/*      */     }
/* 1138 */     if (localScriptable2 == null) {
/* 1139 */       localScriptable2 = getObjectPrototype(paramScriptable);
/*      */     }
/* 1141 */     localScriptable1.setPrototype(localScriptable2);
/*      */ 
/* 1152 */     Object localObject3 = findAnnotatedMember(arrayOfMethod, JSConstructor.class);
/* 1153 */     if (localObject3 == null) {
/* 1154 */       localObject3 = findAnnotatedMember(arrayOfConstructor, JSConstructor.class);
/*      */     }
/* 1156 */     if (localObject3 == null) {
/* 1157 */       localObject3 = FunctionObject.findSingleMethod(arrayOfMethod, "jsConstructor");
/*      */     }
/* 1159 */     if (localObject3 == null) {
/* 1160 */       if (arrayOfConstructor.length == 1)
/* 1161 */         localObject3 = arrayOfConstructor[0];
/* 1162 */       else if (arrayOfConstructor.length == 2) {
/* 1163 */         if (arrayOfConstructor[0].getParameterTypes().length == 0)
/* 1164 */           localObject3 = arrayOfConstructor[1];
/* 1165 */         else if (arrayOfConstructor[1].getParameterTypes().length == 0)
/* 1166 */           localObject3 = arrayOfConstructor[0];
/*      */       }
/* 1168 */       if (localObject3 == null) {
/* 1169 */         throw Context.reportRuntimeError1("msg.ctor.multiple.parms", paramClass.getName());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1174 */     FunctionObject localFunctionObject1 = new FunctionObject((String)localObject2, (Member)localObject3, paramScriptable);
/* 1175 */     if (localFunctionObject1.isVarArgsMethod()) {
/* 1176 */       throw Context.reportRuntimeError1("msg.varargs.ctor", ((Member)localObject3).getName());
/*      */     }
/*      */ 
/* 1179 */     localFunctionObject1.initAsConstructor(paramScriptable, localScriptable1);
/*      */ 
/* 1181 */     Object localObject4 = null;
/* 1182 */     HashSet localHashSet = new HashSet(arrayOfMethod.length);
/* 1183 */     for (Method localMethod1 : arrayOfMethod)
/* 1184 */       if (localMethod1 != localObject3)
/*      */       {
/* 1187 */         Object localObject6 = localMethod1.getName();
/*      */         Object localObject7;
/* 1188 */         if (((String)localObject6).equals("finishInit")) {
/* 1189 */           localObject7 = localMethod1.getParameterTypes();
/* 1190 */           if ((localObject7.length == 3) && (localObject7[0] == ScriptRuntime.ScriptableClass) && (localObject7[1] == FunctionObject.class) && (localObject7[2] == ScriptRuntime.ScriptableClass) && (Modifier.isStatic(localMethod1.getModifiers())))
/*      */           {
/* 1196 */             localObject4 = localMethod1;
/* 1197 */             continue;
/*      */           }
/*      */         }
/*      */ 
/* 1201 */         if (((String)localObject6).indexOf('$') == -1)
/*      */         {
/* 1203 */           if (!((String)localObject6).equals("jsConstructor"))
/*      */           {
/* 1206 */             localObject7 = null;
/* 1207 */             String str2 = null;
/* 1208 */             if (localMethod1.isAnnotationPresent(JSFunction.class))
/* 1209 */               localObject7 = localMethod1.getAnnotation(JSFunction.class);
/* 1210 */             else if (localMethod1.isAnnotationPresent(JSStaticFunction.class))
/* 1211 */               localObject7 = localMethod1.getAnnotation(JSStaticFunction.class);
/* 1212 */             else if (localMethod1.isAnnotationPresent(JSGetter.class))
/* 1213 */               localObject7 = localMethod1.getAnnotation(JSGetter.class);
/* 1214 */             else if (localMethod1.isAnnotationPresent(JSSetter.class))
/*      */               {
/*      */                 continue;
/*      */               }
/* 1218 */             if (localObject7 == null) {
/* 1219 */               if (((String)localObject6).startsWith("jsFunction_"))
/* 1220 */                 str2 = "jsFunction_";
/* 1221 */               else if (((String)localObject6).startsWith("jsStaticFunction_"))
/* 1222 */                 str2 = "jsStaticFunction_";
/* 1223 */               else if (((String)localObject6).startsWith("jsGet_"))
/* 1224 */                 str2 = "jsGet_";
/* 1225 */               else if (localObject7 == null)
/*      */                 {
/*      */                   continue;
/*      */                 }
/*      */             }
/*      */ 
/* 1231 */             String str3 = getPropertyName((String)localObject6, str2, (Annotation)localObject7);
/* 1232 */             if (localHashSet.contains(str3)) {
/* 1233 */               throw Context.reportRuntimeError2("duplicate.defineClass.name", localObject6, str3);
/*      */             }
/*      */ 
/* 1236 */             localHashSet.add(str3);
/* 1237 */             localObject6 = str3;
/* 1238 */             if (((localObject7 instanceof JSGetter)) || (str2 == "jsGet_")) {
/* 1239 */               if (!(localScriptable1 instanceof ScriptableObject)) {
/* 1240 */                 throw Context.reportRuntimeError2("msg.extend.scriptable", localScriptable1.getClass().toString(), localObject6);
/*      */               }
/*      */ 
/* 1244 */               Method localMethod2 = findSetterMethod(arrayOfMethod, (String)localObject6, "jsSet_");
/* 1245 */               int i1 = 0x6 | (localMethod2 != null ? 0 : 1);
/*      */ 
/* 1249 */               ((ScriptableObject)localScriptable1).defineProperty((String)localObject6, null, localMethod1, localMethod2, i1);
/*      */             }
/*      */             else
/*      */             {
/* 1255 */               int n = ((localObject7 instanceof JSStaticFunction)) || (str2 == "jsStaticFunction_") ? 1 : 0;
/*      */ 
/* 1257 */               if ((n != 0) && (!Modifier.isStatic(localMethod1.getModifiers()))) {
/* 1258 */                 throw Context.reportRuntimeError("jsStaticFunction must be used with static method.");
/*      */               }
/*      */ 
/* 1262 */               FunctionObject localFunctionObject2 = new FunctionObject((String)localObject6, localMethod1, localScriptable1);
/* 1263 */               if (localFunctionObject2.isVarArgsConstructor()) {
/* 1264 */                 throw Context.reportRuntimeError1("msg.varargs.fun", ((Member)localObject3).getName());
/*      */               }
/*      */ 
/* 1267 */               defineProperty(n != 0 ? localFunctionObject1 : localScriptable1, (String)localObject6, localFunctionObject2, 2);
/* 1268 */               if (paramBoolean1)
/* 1269 */                 localFunctionObject2.sealObject();
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 1274 */     if (localObject4 != null) {
/* 1275 */       ??? = new Object[] { paramScriptable, localFunctionObject1, localScriptable1 };
/* 1276 */       localObject4.invoke(null, (Object[])???);
/*      */     }
/*      */ 
/* 1280 */     if (paramBoolean1) {
/* 1281 */       localFunctionObject1.sealObject();
/* 1282 */       if ((localScriptable1 instanceof ScriptableObject)) {
/* 1283 */         ((ScriptableObject)localScriptable1).sealObject();
/*      */       }
/*      */     }
/*      */ 
/* 1287 */     return localFunctionObject1;
/*      */   }
/*      */ 
/*      */   private static Member findAnnotatedMember(AccessibleObject[] paramArrayOfAccessibleObject, Class<? extends Annotation> paramClass)
/*      */   {
/* 1292 */     for (AccessibleObject localAccessibleObject : paramArrayOfAccessibleObject) {
/* 1293 */       if (localAccessibleObject.isAnnotationPresent(paramClass)) {
/* 1294 */         return (Member)localAccessibleObject;
/*      */       }
/*      */     }
/* 1297 */     return null;
/*      */   }
/*      */ 
/*      */   private static Method findSetterMethod(Method[] paramArrayOfMethod, String paramString1, String paramString2)
/*      */   {
/* 1303 */     String str = "set" + Character.toUpperCase(paramString1.charAt(0)) + paramString1.substring(1);
/*      */     Object localObject2;
/* 1306 */     for (Method localMethod : paramArrayOfMethod) {
/* 1307 */       localObject2 = (JSSetter)localMethod.getAnnotation(JSSetter.class);
/* 1308 */       if ((localObject2 != null) && (
/* 1309 */         (paramString1.equals(((JSSetter)localObject2).value())) || (("".equals(((JSSetter)localObject2).value())) && (str.equals(localMethod.getName())))))
/*      */       {
/* 1311 */         return localMethod;
/*      */       }
/*      */     }
/*      */ 
/* 1315 */     ??? = paramString2 + paramString1;
/* 1316 */     for (localObject2 : paramArrayOfMethod) {
/* 1317 */       if (((String)???).equals(((Method)localObject2).getName())) {
/* 1318 */         return localObject2;
/*      */       }
/*      */     }
/* 1321 */     return null;
/*      */   }
/*      */ 
/*      */   private static String getPropertyName(String paramString1, String paramString2, Annotation paramAnnotation)
/*      */   {
/* 1327 */     if (paramString2 != null) {
/* 1328 */       return paramString1.substring(paramString2.length());
/*      */     }
/* 1330 */     String str = null;
/* 1331 */     if ((paramAnnotation instanceof JSGetter)) {
/* 1332 */       str = ((JSGetter)paramAnnotation).value();
/* 1333 */       if (((str == null) || (str.length() == 0)) && 
/* 1334 */         (paramString1.length() > 3) && (paramString1.startsWith("get"))) {
/* 1335 */         str = paramString1.substring(3);
/* 1336 */         if (Character.isUpperCase(str.charAt(0))) {
/* 1337 */           if (str.length() == 1)
/* 1338 */             str = str.toLowerCase();
/* 1339 */           else if (!Character.isUpperCase(str.charAt(1))) {
/* 1340 */             str = Character.toLowerCase(str.charAt(0)) + str.substring(1);
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/* 1346 */     else if ((paramAnnotation instanceof JSFunction)) {
/* 1347 */       str = ((JSFunction)paramAnnotation).value();
/* 1348 */     } else if ((paramAnnotation instanceof JSStaticFunction)) {
/* 1349 */       str = ((JSStaticFunction)paramAnnotation).value();
/*      */     }
/* 1351 */     if ((str == null) || (str.length() == 0)) {
/* 1352 */       str = paramString1;
/*      */     }
/* 1354 */     return str;
/*      */   }
/*      */ 
/*      */   private static <T extends Scriptable> Class<T> extendsScriptable(Class<?> paramClass)
/*      */   {
/* 1360 */     if (ScriptRuntime.ScriptableClass.isAssignableFrom(paramClass))
/* 1361 */       return paramClass;
/* 1362 */     return null;
/*      */   }
/*      */ 
/*      */   public void defineProperty(String paramString, Object paramObject, int paramInt)
/*      */   {
/* 1378 */     checkNotSealed(paramString, 0);
/* 1379 */     put(paramString, this, paramObject);
/* 1380 */     setAttributes(paramString, paramInt);
/*      */   }
/*      */ 
/*      */   public static void defineProperty(Scriptable paramScriptable, String paramString, Object paramObject, int paramInt)
/*      */   {
/* 1393 */     if (!(paramScriptable instanceof ScriptableObject)) {
/* 1394 */       paramScriptable.put(paramString, paramScriptable, paramObject);
/* 1395 */       return;
/*      */     }
/* 1397 */     ScriptableObject localScriptableObject = (ScriptableObject)paramScriptable;
/* 1398 */     localScriptableObject.defineProperty(paramString, paramObject, paramInt);
/*      */   }
/*      */ 
/*      */   public static void defineConstProperty(Scriptable paramScriptable, String paramString)
/*      */   {
/* 1410 */     if ((paramScriptable instanceof ConstProperties)) {
/* 1411 */       ConstProperties localConstProperties = (ConstProperties)paramScriptable;
/* 1412 */       localConstProperties.defineConst(paramString, paramScriptable);
/*      */     } else {
/* 1414 */       defineProperty(paramScriptable, paramString, Undefined.instance, 13);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void defineProperty(String paramString, Class<?> paramClass, int paramInt)
/*      */   {
/* 1438 */     int i = paramString.length();
/* 1439 */     if (i == 0) throw new IllegalArgumentException();
/* 1440 */     char[] arrayOfChar = new char[3 + i];
/* 1441 */     paramString.getChars(0, i, arrayOfChar, 3);
/* 1442 */     arrayOfChar[3] = Character.toUpperCase(arrayOfChar[3]);
/* 1443 */     arrayOfChar[0] = 'g';
/* 1444 */     arrayOfChar[1] = 'e';
/* 1445 */     arrayOfChar[2] = 't';
/* 1446 */     String str1 = new String(arrayOfChar);
/* 1447 */     arrayOfChar[0] = 's';
/* 1448 */     String str2 = new String(arrayOfChar);
/*      */ 
/* 1450 */     Method[] arrayOfMethod = FunctionObject.getMethodList(paramClass);
/* 1451 */     Method localMethod1 = FunctionObject.findSingleMethod(arrayOfMethod, str1);
/* 1452 */     Method localMethod2 = FunctionObject.findSingleMethod(arrayOfMethod, str2);
/* 1453 */     if (localMethod2 == null)
/* 1454 */       paramInt |= 1;
/* 1455 */     defineProperty(paramString, null, localMethod1, localMethod2 == null ? null : localMethod2, paramInt);
/*      */   }
/*      */ 
/*      */   public void defineProperty(String paramString, Object paramObject, Method paramMethod1, Method paramMethod2, int paramInt)
/*      */   {
/* 1503 */     MemberBox localMemberBox1 = null;
/*      */     Object localObject;
/*      */     Class[] arrayOfClass;
/* 1504 */     if (paramMethod1 != null) {
/* 1505 */       localMemberBox1 = new MemberBox(paramMethod1);
/*      */       int i;
/* 1508 */       if (!Modifier.isStatic(paramMethod1.getModifiers())) {
/* 1509 */         i = paramObject != null ? 1 : 0;
/* 1510 */         localMemberBox1.delegateTo = paramObject;
/*      */       } else {
/* 1512 */         i = 1;
/*      */ 
/* 1515 */         localMemberBox1.delegateTo = Void.TYPE;
/*      */       }
/*      */ 
/* 1518 */       String str = null;
/* 1519 */       localObject = paramMethod1.getParameterTypes();
/* 1520 */       if (localObject.length == 0) {
/* 1521 */         if (i != 0)
/* 1522 */           str = "msg.obj.getter.parms";
/*      */       }
/* 1524 */       else if (localObject.length == 1) {
/* 1525 */         arrayOfClass = localObject[0];
/*      */ 
/* 1527 */         if ((arrayOfClass != ScriptRuntime.ScriptableClass) && (arrayOfClass != ScriptRuntime.ScriptableObjectClass))
/*      */         {
/* 1530 */           str = "msg.bad.getter.parms";
/* 1531 */         } else if (i == 0)
/* 1532 */           str = "msg.bad.getter.parms";
/*      */       }
/*      */       else {
/* 1535 */         str = "msg.bad.getter.parms";
/*      */       }
/* 1537 */       if (str != null) {
/* 1538 */         throw Context.reportRuntimeError1(str, paramMethod1.toString());
/*      */       }
/*      */     }
/*      */ 
/* 1542 */     MemberBox localMemberBox2 = null;
/* 1543 */     if (paramMethod2 != null) {
/* 1544 */       if (paramMethod2.getReturnType() != Void.TYPE) {
/* 1545 */         throw Context.reportRuntimeError1("msg.setter.return", paramMethod2.toString());
/*      */       }
/*      */ 
/* 1548 */       localMemberBox2 = new MemberBox(paramMethod2);
/*      */       int j;
/* 1551 */       if (!Modifier.isStatic(paramMethod2.getModifiers())) {
/* 1552 */         j = paramObject != null ? 1 : 0;
/* 1553 */         localMemberBox2.delegateTo = paramObject;
/*      */       } else {
/* 1555 */         j = 1;
/*      */ 
/* 1558 */         localMemberBox2.delegateTo = Void.TYPE;
/*      */       }
/*      */ 
/* 1561 */       localObject = null;
/* 1562 */       arrayOfClass = paramMethod2.getParameterTypes();
/* 1563 */       if (arrayOfClass.length == 1) {
/* 1564 */         if (j != 0)
/* 1565 */           localObject = "msg.setter2.expected";
/*      */       }
/* 1567 */       else if (arrayOfClass.length == 2) {
/* 1568 */         Class localClass = arrayOfClass[0];
/*      */ 
/* 1570 */         if ((localClass != ScriptRuntime.ScriptableClass) && (localClass != ScriptRuntime.ScriptableObjectClass))
/*      */         {
/* 1573 */           localObject = "msg.setter2.parms";
/* 1574 */         } else if (j == 0)
/* 1575 */           localObject = "msg.setter1.parms";
/*      */       }
/*      */       else {
/* 1578 */         localObject = "msg.setter.parms";
/*      */       }
/* 1580 */       if (localObject != null) {
/* 1581 */         throw Context.reportRuntimeError1((String)localObject, paramMethod2.toString());
/*      */       }
/*      */     }
/*      */ 
/* 1585 */     GetterSlot localGetterSlot = (GetterSlot)getSlot(paramString, 0, 4);
/*      */ 
/* 1587 */     localGetterSlot.setAttributes(paramInt);
/* 1588 */     localGetterSlot.getter = localMemberBox1;
/* 1589 */     localGetterSlot.setter = localMemberBox2;
/*      */   }
/*      */ 
/*      */   public void defineOwnProperties(Context paramContext, ScriptableObject paramScriptableObject) {
/* 1593 */     Object[] arrayOfObject1 = paramScriptableObject.getIds();
/*      */     Object localObject1;
/*      */     String str;
/*      */     Object localObject2;
/* 1594 */     for (localObject1 : arrayOfObject1) {
/* 1595 */       str = ScriptRuntime.toString(localObject1);
/* 1596 */       localObject2 = paramScriptableObject.get(localObject1);
/* 1597 */       ScriptableObject localScriptableObject = ensureScriptableObject(localObject2);
/* 1598 */       checkValidPropertyDefinition(getSlot(str, 0, 1), localScriptableObject);
/*      */     }
/* 1600 */     for (localObject1 : arrayOfObject1) {
/* 1601 */       str = ScriptRuntime.toString(localObject1);
/* 1602 */       localObject2 = (ScriptableObject)paramScriptableObject.get(localObject1);
/* 1603 */       defineOwnProperty(paramContext, str, (ScriptableObject)localObject2, false);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void defineOwnProperty(Context paramContext, Object paramObject, ScriptableObject paramScriptableObject)
/*      */   {
/* 1617 */     defineOwnProperty(paramContext, paramObject, paramScriptableObject, true);
/*      */   }
/*      */ 
/*      */   private void defineOwnProperty(Context paramContext, Object paramObject, ScriptableObject paramScriptableObject, boolean paramBoolean) {
/* 1621 */     Slot localSlot = getSlot(paramContext, paramObject, 1);
/*      */ 
/* 1623 */     if (paramBoolean)
/* 1624 */       checkValidPropertyDefinition(localSlot, paramScriptableObject);
/*      */     int i;
/* 1627 */     if (localSlot == null) {
/* 1628 */       localSlot = getSlot(paramContext, paramObject, 2);
/* 1629 */       i = applyDescriptorToAttributeBitset(7, paramScriptableObject);
/*      */     } else {
/* 1631 */       i = applyDescriptorToAttributeBitset(localSlot.getAttributes(), paramScriptableObject);
/*      */     }
/*      */ 
/* 1634 */     defineOwnProperty(paramContext, localSlot, paramScriptableObject, i);
/*      */   }
/*      */ 
/*      */   private void defineOwnProperty(Context paramContext, Slot paramSlot, ScriptableObject paramScriptableObject, int paramInt) {
/* 1638 */     String str = paramSlot.name;
/* 1639 */     int i = paramSlot.indexOrHash;
/*      */     Object localObject1;
/* 1641 */     if (isAccessorDescriptor(paramScriptableObject)) {
/* 1642 */       if (!(paramSlot instanceof GetterSlot)) {
/* 1643 */         paramSlot = getSlot(paramContext, str != null ? str : Integer.valueOf(i), 4);
/*      */       }
/*      */ 
/* 1646 */       localObject1 = (GetterSlot)paramSlot;
/*      */ 
/* 1648 */       Object localObject2 = getProperty(paramScriptableObject, "get");
/* 1649 */       if (localObject2 != NOT_FOUND) {
/* 1650 */         ((GetterSlot)localObject1).getter = localObject2;
/*      */       }
/* 1652 */       Object localObject3 = getProperty(paramScriptableObject, "set");
/* 1653 */       if (localObject3 != NOT_FOUND) {
/* 1654 */         ((GetterSlot)localObject1).setter = localObject3;
/*      */       }
/*      */ 
/* 1657 */       ((GetterSlot)localObject1).value = Undefined.instance;
/* 1658 */       ((GetterSlot)localObject1).setAttributes(paramInt);
/*      */     } else {
/* 1660 */       if (((paramSlot instanceof GetterSlot)) && (isDataDescriptor(paramScriptableObject))) {
/* 1661 */         paramSlot = getSlot(paramContext, str != null ? str : Integer.valueOf(i), 6);
/*      */       }
/*      */ 
/* 1664 */       localObject1 = getProperty(paramScriptableObject, "value");
/* 1665 */       if (localObject1 != NOT_FOUND) {
/* 1666 */         paramSlot.value = localObject1;
/*      */       }
/* 1668 */       paramSlot.setAttributes(paramInt);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void checkValidPropertyDefinition(Slot paramSlot, ScriptableObject paramScriptableObject) {
/* 1673 */     Object localObject1 = getProperty(paramScriptableObject, "get");
/* 1674 */     if ((localObject1 != NOT_FOUND) && (localObject1 != Undefined.instance) && (!(localObject1 instanceof Callable))) {
/* 1675 */       throw ScriptRuntime.notFunctionError(localObject1);
/*      */     }
/* 1677 */     Object localObject2 = getProperty(paramScriptableObject, "set");
/* 1678 */     if ((localObject2 != NOT_FOUND) && (localObject2 != Undefined.instance) && (!(localObject2 instanceof Callable))) {
/* 1679 */       throw ScriptRuntime.notFunctionError(localObject2);
/*      */     }
/* 1681 */     if ((isDataDescriptor(paramScriptableObject)) && (isAccessorDescriptor(paramScriptableObject))) {
/* 1682 */       throw ScriptRuntime.typeError0("msg.both.data.and.accessor.desc");
/*      */     }
/*      */ 
/* 1685 */     if (paramSlot == null) {
/* 1686 */       if (!isExtensible()) throw ScriptRuntime.typeError("msg.not.extensible"); 
/*      */     }
/* 1688 */     else { String str = paramSlot.name;
/* 1689 */       ScriptableObject localScriptableObject = getOwnPropertyDescriptor(Context.getContext(), str);
/* 1690 */       if (isFalse(localScriptableObject.get("configurable", localScriptableObject))) {
/* 1691 */         if (isTrue(getProperty(paramScriptableObject, "configurable")))
/* 1692 */           throw ScriptRuntime.typeError1("msg.change.configurable.false.to.true", str);
/* 1693 */         if (isTrue(localScriptableObject.get("enumerable", localScriptableObject)) != isTrue(getProperty(paramScriptableObject, "enumerable"))) {
/* 1694 */           throw ScriptRuntime.typeError1("msg.change.enumerable.with.configurable.false", str);
/*      */         }
/* 1696 */         if (!isGenericDescriptor(paramScriptableObject))
/*      */         {
/* 1698 */           if ((isDataDescriptor(paramScriptableObject)) && (isDataDescriptor(localScriptableObject))) {
/* 1699 */             if (isFalse(localScriptableObject.get("writable", localScriptableObject))) {
/* 1700 */               if (isTrue(getProperty(paramScriptableObject, "writable"))) {
/* 1701 */                 throw ScriptRuntime.typeError1("msg.change.writable.false.to.true.with.configurable.false", str);
/*      */               }
/* 1703 */               if (changes(localScriptableObject.get("value", localScriptableObject), getProperty(paramScriptableObject, "value")))
/* 1704 */                 throw ScriptRuntime.typeError1("msg.change.value.with.writable.false", str);
/*      */             }
/* 1706 */           } else if ((isAccessorDescriptor(paramScriptableObject)) && (isAccessorDescriptor(localScriptableObject))) {
/* 1707 */             if (changes(localScriptableObject.get("set", localScriptableObject), localObject2)) {
/* 1708 */               throw ScriptRuntime.typeError1("msg.change.setter.with.configurable.false", str);
/*      */             }
/* 1710 */             if (changes(localScriptableObject.get("get", localScriptableObject), localObject1))
/* 1711 */               throw ScriptRuntime.typeError1("msg.change.getter.with.configurable.false", str);
/*      */           } else {
/* 1713 */             if (isDataDescriptor(localScriptableObject)) {
/* 1714 */               throw ScriptRuntime.typeError1("msg.change.property.data.to.accessor.with.configurable.false", str);
/*      */             }
/* 1716 */             throw ScriptRuntime.typeError1("msg.change.property.accessor.to.data.with.configurable.false", str);
/*      */           }
/*      */         }
/*      */       } }
/*      */   }
/*      */ 
/*      */   protected static boolean isTrue(Object paramObject) {
/* 1723 */     return paramObject == NOT_FOUND ? false : ScriptRuntime.toBoolean(paramObject);
/*      */   }
/*      */ 
/*      */   protected static boolean isFalse(Object paramObject) {
/* 1727 */     return !isTrue(paramObject);
/*      */   }
/*      */ 
/*      */   private boolean changes(Object paramObject1, Object paramObject2) {
/* 1731 */     if (paramObject2 == NOT_FOUND) return false;
/* 1732 */     if (paramObject1 == NOT_FOUND) {
/* 1733 */       paramObject1 = Undefined.instance;
/*      */     }
/* 1735 */     return !ScriptRuntime.shallowEq(paramObject1, paramObject2);
/*      */   }
/*      */ 
/*      */   protected int applyDescriptorToAttributeBitset(int paramInt, ScriptableObject paramScriptableObject)
/*      */   {
/* 1741 */     Object localObject1 = getProperty(paramScriptableObject, "enumerable");
/* 1742 */     if (localObject1 != NOT_FOUND) {
/* 1743 */       paramInt = ScriptRuntime.toBoolean(localObject1) ? paramInt & 0xFFFFFFFD : paramInt | 0x2;
/*      */     }
/*      */ 
/* 1747 */     Object localObject2 = getProperty(paramScriptableObject, "writable");
/* 1748 */     if (localObject2 != NOT_FOUND) {
/* 1749 */       paramInt = ScriptRuntime.toBoolean(localObject2) ? paramInt & 0xFFFFFFFE : paramInt | 0x1;
/*      */     }
/*      */ 
/* 1753 */     Object localObject3 = getProperty(paramScriptableObject, "configurable");
/* 1754 */     if (localObject3 != NOT_FOUND) {
/* 1755 */       paramInt = ScriptRuntime.toBoolean(localObject3) ? paramInt & 0xFFFFFFFB : paramInt | 0x4;
/*      */     }
/*      */ 
/* 1759 */     return paramInt;
/*      */   }
/*      */ 
/*      */   protected boolean isDataDescriptor(ScriptableObject paramScriptableObject) {
/* 1763 */     return (hasProperty(paramScriptableObject, "value")) || (hasProperty(paramScriptableObject, "writable"));
/*      */   }
/*      */ 
/*      */   protected boolean isAccessorDescriptor(ScriptableObject paramScriptableObject) {
/* 1767 */     return (hasProperty(paramScriptableObject, "get")) || (hasProperty(paramScriptableObject, "set"));
/*      */   }
/*      */ 
/*      */   protected boolean isGenericDescriptor(ScriptableObject paramScriptableObject) {
/* 1771 */     return (!isDataDescriptor(paramScriptableObject)) && (!isAccessorDescriptor(paramScriptableObject));
/*      */   }
/*      */ 
/*      */   protected Scriptable ensureScriptable(Object paramObject) {
/* 1775 */     if (!(paramObject instanceof ConstProperties))
/* 1776 */       throw ScriptRuntime.typeError1("msg.arg.not.object", ScriptRuntime.typeof(paramObject));
/* 1777 */     return (ConstProperties)paramObject;
/*      */   }
/*      */ 
/*      */   protected ScriptableObject ensureScriptableObject(Object paramObject) {
/* 1781 */     if (!(paramObject instanceof ScriptableObject))
/* 1782 */       throw ScriptRuntime.typeError1("msg.arg.not.object", ScriptRuntime.typeof(paramObject));
/* 1783 */     return (ScriptableObject)paramObject;
/*      */   }
/*      */ 
/*      */   public void defineFunctionProperties(String[] paramArrayOfString, Class<?> paramClass, int paramInt)
/*      */   {
/* 1802 */     Method[] arrayOfMethod = FunctionObject.getMethodList(paramClass);
/* 1803 */     for (int i = 0; i < paramArrayOfString.length; i++) {
/* 1804 */       String str = paramArrayOfString[i];
/* 1805 */       Method localMethod = FunctionObject.findSingleMethod(arrayOfMethod, str);
/* 1806 */       if (localMethod == null) {
/* 1807 */         throw Context.reportRuntimeError2("msg.method.not.found", str, paramClass.getName());
/*      */       }
/*      */ 
/* 1810 */       FunctionObject localFunctionObject = new FunctionObject(str, localMethod, this);
/* 1811 */       defineProperty(str, localFunctionObject, paramInt);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static Scriptable getObjectPrototype(Scriptable paramScriptable)
/*      */   {
/* 1820 */     return getClassPrototype(paramScriptable, "Object");
/*      */   }
/*      */ 
/*      */   public static Scriptable getFunctionPrototype(Scriptable paramScriptable)
/*      */   {
/* 1828 */     return getClassPrototype(paramScriptable, "Function");
/*      */   }
/*      */ 
/*      */   public static Scriptable getArrayPrototype(Scriptable paramScriptable) {
/* 1832 */     return getClassPrototype(paramScriptable, "Array");
/*      */   }
/*      */ 
/*      */   public static Scriptable getClassPrototype(Scriptable paramScriptable, String paramString)
/*      */   {
/* 1853 */     paramScriptable = getTopLevelScope(paramScriptable);
/* 1854 */     Object localObject1 = getProperty(paramScriptable, paramString);
/*      */     Object localObject2;
/* 1856 */     if ((localObject1 instanceof BaseFunction)) {
/* 1857 */       localObject2 = ((BaseFunction)localObject1).getPrototypeProperty();
/* 1858 */     } else if ((localObject1 instanceof ConstProperties)) {
/* 1859 */       Scriptable localScriptable = (ConstProperties)localObject1;
/* 1860 */       localObject2 = localScriptable.get("prototype", localScriptable);
/*      */     } else {
/* 1862 */       return null;
/*      */     }
/* 1864 */     if ((localObject2 instanceof ConstProperties)) {
/* 1865 */       return (ConstProperties)localObject2;
/*      */     }
/* 1867 */     return null;
/*      */   }
/*      */ 
/*      */   public static Scriptable getTopLevelScope(Scriptable paramScriptable)
/*      */   {
/*      */     while (true)
/*      */     {
/* 1882 */       Scriptable localScriptable = paramScriptable.getParentScope();
/* 1883 */       if (localScriptable == null) {
/* 1884 */         return paramScriptable;
/*      */       }
/* 1886 */       paramScriptable = localScriptable;
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isExtensible() {
/* 1891 */     return this.isExtensible;
/*      */   }
/*      */ 
/*      */   public void preventExtensions() {
/* 1895 */     this.isExtensible = false;
/*      */   }
/*      */ 
/*      */   public synchronized void sealObject()
/*      */   {
/* 1908 */     if (this.count >= 0)
/*      */     {
/* 1910 */       Slot localSlot = this.firstAdded;
/* 1911 */       while (localSlot != null) {
/* 1912 */         if ((localSlot.value instanceof LazilyLoadedCtor)) {
/* 1913 */           LazilyLoadedCtor localLazilyLoadedCtor = (LazilyLoadedCtor)localSlot.value;
/*      */           try {
/* 1915 */             localLazilyLoadedCtor.init();
/*      */           } finally {
/* 1917 */             localSlot.value = localLazilyLoadedCtor.getValue();
/*      */           }
/*      */         }
/* 1920 */         localSlot = localSlot.orderedNext;
/*      */       }
/* 1922 */       this.count ^= -1;
/*      */     }
/*      */   }
/*      */ 
/*      */   public final boolean isSealed()
/*      */   {
/* 1934 */     return this.count < 0;
/*      */   }
/*      */ 
/*      */   private void checkNotSealed(String paramString, int paramInt)
/*      */   {
/* 1939 */     if (!isSealed()) {
/* 1940 */       return;
/*      */     }
/* 1942 */     String str = paramString != null ? paramString : Integer.toString(paramInt);
/* 1943 */     throw Context.reportRuntimeError1("msg.modify.sealed", str);
/*      */   }
/*      */ 
/*      */   public static Object getProperty(Scriptable paramScriptable, String paramString)
/*      */   {
/* 1960 */     Scriptable localScriptable = paramScriptable;
/*      */     Object localObject;
/*      */     do
/*      */     {
/* 1963 */       localObject = paramScriptable.get(paramString, localScriptable);
/* 1964 */       if (localObject != ConstProperties.NOT_FOUND)
/*      */         break;
/* 1966 */       paramScriptable = paramScriptable.getPrototype();
/* 1967 */     }while (paramScriptable != null);
/* 1968 */     return localObject;
/*      */   }
/*      */ 
/*      */   public static <T> T getTypedProperty(Scriptable paramScriptable, int paramInt, Class<T> paramClass)
/*      */   {
/* 1991 */     Object localObject = getProperty(paramScriptable, paramInt);
/* 1992 */     if (localObject == ConstProperties.NOT_FOUND) {
/* 1993 */       localObject = null;
/*      */     }
/* 1995 */     return paramClass.cast(Context.jsToJava(localObject, paramClass));
/*      */   }
/*      */ 
/*      */   public static Object getProperty(Scriptable paramScriptable, int paramInt)
/*      */   {
/* 2015 */     Scriptable localScriptable = paramScriptable;
/*      */     Object localObject;
/*      */     do
/*      */     {
/* 2018 */       localObject = paramScriptable.get(paramInt, localScriptable);
/* 2019 */       if (localObject != ConstProperties.NOT_FOUND)
/*      */         break;
/* 2021 */       paramScriptable = paramScriptable.getPrototype();
/* 2022 */     }while (paramScriptable != null);
/* 2023 */     return localObject;
/*      */   }
/*      */ 
/*      */   public static <T> T getTypedProperty(Scriptable paramScriptable, String paramString, Class<T> paramClass)
/*      */   {
/* 2043 */     Object localObject = getProperty(paramScriptable, paramString);
/* 2044 */     if (localObject == ConstProperties.NOT_FOUND) {
/* 2045 */       localObject = null;
/*      */     }
/* 2047 */     return paramClass.cast(Context.jsToJava(localObject, paramClass));
/*      */   }
/*      */ 
/*      */   public static boolean hasProperty(Scriptable paramScriptable, String paramString)
/*      */   {
/* 2063 */     return null != getBase(paramScriptable, paramString);
/*      */   }
/*      */ 
/*      */   public static void redefineProperty(Scriptable paramScriptable, String paramString, boolean paramBoolean)
/*      */   {
/* 2078 */     Scriptable localScriptable = getBase(paramScriptable, paramString);
/* 2079 */     if (localScriptable == null)
/* 2080 */       return;
/* 2081 */     if ((localScriptable instanceof ConstProperties)) {
/* 2082 */       ConstProperties localConstProperties = (ConstProperties)localScriptable;
/*      */ 
/* 2084 */       if (localConstProperties.isConst(paramString))
/* 2085 */         throw Context.reportRuntimeError1("msg.const.redecl", paramString);
/*      */     }
/* 2087 */     if (paramBoolean)
/* 2088 */       throw Context.reportRuntimeError1("msg.var.redecl", paramString);
/*      */   }
/*      */ 
/*      */   public static boolean hasProperty(Scriptable paramScriptable, int paramInt)
/*      */   {
/* 2103 */     return null != getBase(paramScriptable, paramInt);
/*      */   }
/*      */ 
/*      */   public static void putProperty(Scriptable paramScriptable, String paramString, Object paramObject)
/*      */   {
/* 2123 */     Scriptable localScriptable = getBase(paramScriptable, paramString);
/* 2124 */     if (localScriptable == null)
/* 2125 */       localScriptable = paramScriptable;
/* 2126 */     localScriptable.put(paramString, paramScriptable, paramObject);
/*      */   }
/*      */ 
/*      */   public static void putConstProperty(Scriptable paramScriptable, String paramString, Object paramObject)
/*      */   {
/* 2146 */     Scriptable localScriptable = getBase(paramScriptable, paramString);
/* 2147 */     if (localScriptable == null)
/* 2148 */       localScriptable = paramScriptable;
/* 2149 */     if ((localScriptable instanceof ConstProperties))
/* 2150 */       ((ConstProperties)localScriptable).putConst(paramString, paramScriptable, paramObject);
/*      */   }
/*      */ 
/*      */   public static void putProperty(Scriptable paramScriptable, int paramInt, Object paramObject)
/*      */   {
/* 2170 */     Scriptable localScriptable = getBase(paramScriptable, paramInt);
/* 2171 */     if (localScriptable == null)
/* 2172 */       localScriptable = paramScriptable;
/* 2173 */     localScriptable.put(paramInt, paramScriptable, paramObject);
/*      */   }
/*      */ 
/*      */   public static boolean deleteProperty(Scriptable paramScriptable, String paramString)
/*      */   {
/* 2189 */     Scriptable localScriptable = getBase(paramScriptable, paramString);
/* 2190 */     if (localScriptable == null)
/* 2191 */       return true;
/* 2192 */     localScriptable.delete(paramString);
/* 2193 */     return !localScriptable.has(paramString, paramScriptable);
/*      */   }
/*      */ 
/*      */   public static boolean deleteProperty(Scriptable paramScriptable, int paramInt)
/*      */   {
/* 2209 */     Scriptable localScriptable = getBase(paramScriptable, paramInt);
/* 2210 */     if (localScriptable == null)
/* 2211 */       return true;
/* 2212 */     localScriptable.delete(paramInt);
/* 2213 */     return !localScriptable.has(paramInt, paramScriptable);
/*      */   }
/*      */ 
/*      */   public static Object[] getPropertyIds(Scriptable paramScriptable)
/*      */   {
/* 2227 */     if (paramScriptable == null) {
/* 2228 */       return ScriptRuntime.emptyArgs;
/*      */     }
/* 2230 */     Object localObject = paramScriptable.getIds();
/* 2231 */     ObjToIntMap localObjToIntMap = null;
/*      */     while (true) {
/* 2233 */       paramScriptable = paramScriptable.getPrototype();
/* 2234 */       if (paramScriptable == null) {
/*      */         break;
/*      */       }
/* 2237 */       Object[] arrayOfObject = paramScriptable.getIds();
/* 2238 */       if (arrayOfObject.length != 0)
/*      */       {
/*      */         int i;
/* 2241 */         if (localObjToIntMap == null) {
/* 2242 */           if (localObject.length == 0) {
/* 2243 */             localObject = arrayOfObject;
/*      */           }
/*      */           else {
/* 2246 */             localObjToIntMap = new ObjToIntMap(localObject.length + arrayOfObject.length);
/* 2247 */             for (i = 0; i != localObject.length; i++) {
/* 2248 */               localObjToIntMap.intern(localObject[i]);
/*      */             }
/* 2250 */             localObject = null;
/*      */           }
/*      */         } else for (i = 0; i != arrayOfObject.length; i++)
/* 2253 */             localObjToIntMap.intern(arrayOfObject[i]);
/*      */       }
/*      */     }
/* 2256 */     if (localObjToIntMap != null) {
/* 2257 */       localObject = localObjToIntMap.getKeys();
/*      */     }
/* 2259 */     return localObject;
/*      */   }
/*      */ 
/*      */   public static Object callMethod(Scriptable paramScriptable, String paramString, Object[] paramArrayOfObject)
/*      */   {
/* 2273 */     return callMethod(null, paramScriptable, paramString, paramArrayOfObject);
/*      */   }
/*      */ 
/*      */   public static Object callMethod(Context paramContext, Scriptable paramScriptable, String paramString, Object[] paramArrayOfObject)
/*      */   {
/* 2287 */     Object localObject = getProperty(paramScriptable, paramString);
/* 2288 */     if (!(localObject instanceof Function)) {
/* 2289 */       throw ScriptRuntime.notFunctionError(paramScriptable, paramString);
/*      */     }
/* 2291 */     Function localFunction = (Function)localObject;
/*      */ 
/* 2299 */     Scriptable localScriptable = getTopLevelScope(paramScriptable);
/* 2300 */     if (paramContext != null) {
/* 2301 */       return localFunction.call(paramContext, localScriptable, paramScriptable, paramArrayOfObject);
/*      */     }
/* 2303 */     return Context.call(null, localFunction, localScriptable, paramScriptable, paramArrayOfObject);
/*      */   }
/*      */ 
/*      */   private static Scriptable getBase(Scriptable paramScriptable, String paramString)
/*      */   {
/*      */     do
/*      */     {
/* 2310 */       if (paramScriptable.has(paramString, paramScriptable))
/*      */         break;
/* 2312 */       paramScriptable = paramScriptable.getPrototype();
/* 2313 */     }while (paramScriptable != null);
/* 2314 */     return paramScriptable;
/*      */   }
/*      */ 
/*      */   private static Scriptable getBase(Scriptable paramScriptable, int paramInt)
/*      */   {
/*      */     do {
/* 2320 */       if (paramScriptable.has(paramInt, paramScriptable))
/*      */         break;
/* 2322 */       paramScriptable = paramScriptable.getPrototype();
/* 2323 */     }while (paramScriptable != null);
/* 2324 */     return paramScriptable;
/*      */   }
/*      */ 
/*      */   public final Object getAssociatedValue(Object paramObject)
/*      */   {
/* 2334 */     Map localMap = this.associatedValues;
/* 2335 */     if (localMap == null)
/* 2336 */       return null;
/* 2337 */     return localMap.get(paramObject);
/*      */   }
/*      */ 
/*      */   public static Object getTopScopeValue(Scriptable paramScriptable, Object paramObject)
/*      */   {
/* 2353 */     paramScriptable = getTopLevelScope(paramScriptable);
/*      */     do {
/* 2355 */       if ((paramScriptable instanceof ScriptableObject)) {
/* 2356 */         ScriptableObject localScriptableObject = (ScriptableObject)paramScriptable;
/* 2357 */         Object localObject = localScriptableObject.getAssociatedValue(paramObject);
/* 2358 */         if (localObject != null) {
/* 2359 */           return localObject;
/*      */         }
/*      */       }
/* 2362 */       paramScriptable = paramScriptable.getPrototype();
/* 2363 */     }while (paramScriptable != null);
/* 2364 */     return null;
/*      */   }
/*      */ 
/*      */   public final synchronized Object associateValue(Object paramObject1, Object paramObject2)
/*      */   {
/* 2383 */     if (paramObject2 == null) throw new IllegalArgumentException();
/* 2384 */     Object localObject = this.associatedValues;
/* 2385 */     if (localObject == null) {
/* 2386 */       localObject = new HashMap();
/* 2387 */       this.associatedValues = ((Map)localObject);
/*      */     }
/* 2389 */     return Kit.initHash((Map)localObject, paramObject1, paramObject2);
/*      */   }
/*      */ 
/*      */   private Object getImpl(String paramString, int paramInt, Scriptable paramScriptable)
/*      */   {
/* 2394 */     Slot localSlot = getSlot(paramString, paramInt, 1);
/* 2395 */     if (localSlot == null) {
/* 2396 */       return ConstProperties.NOT_FOUND;
/*      */     }
/* 2398 */     if (!(localSlot instanceof GetterSlot)) {
/* 2399 */       return localSlot.value;
/*      */     }
/* 2401 */     Object localObject1 = ((GetterSlot)localSlot).getter;
/*      */     Object localObject3;
/* 2402 */     if (localObject1 != null) {
/* 2403 */       if ((localObject1 instanceof MemberBox)) {
/* 2404 */         localObject2 = (MemberBox)localObject1;
/*      */         Object[] arrayOfObject;
/* 2407 */         if (((MemberBox)localObject2).delegateTo == null) {
/* 2408 */           localObject3 = paramScriptable;
/* 2409 */           arrayOfObject = ScriptRuntime.emptyArgs;
/*      */         } else {
/* 2411 */           localObject3 = ((MemberBox)localObject2).delegateTo;
/* 2412 */           arrayOfObject = new Object[] { paramScriptable };
/*      */         }
/* 2414 */         return ((MemberBox)localObject2).invoke(localObject3, arrayOfObject);
/*      */       }
/* 2416 */       localObject2 = (Function)localObject1;
/* 2417 */       localObject3 = Context.getContext();
/* 2418 */       return ((Function)localObject2).call((Context)localObject3, ((Function)localObject2).getParentScope(), paramScriptable, ScriptRuntime.emptyArgs);
/*      */     }
/*      */ 
/* 2422 */     Object localObject2 = localSlot.value;
/* 2423 */     if ((localObject2 instanceof LazilyLoadedCtor)) {
/* 2424 */       localObject3 = (LazilyLoadedCtor)localObject2;
/*      */       try {
/* 2426 */         ((LazilyLoadedCtor)localObject3).init();
/*      */       } finally {
/* 2428 */         localObject2 = ((LazilyLoadedCtor)localObject3).getValue();
/* 2429 */         localSlot.value = localObject2;
/*      */       }
/*      */     }
/* 2432 */     return localObject2;
/*      */   }
/*      */ 
/*      */   private boolean putImpl(String paramString, int paramInt1, Scriptable paramScriptable, Object paramObject, int paramInt2)
/*      */   {
/*      */     Slot localSlot;
/* 2450 */     if (this != paramScriptable) {
/* 2451 */       localSlot = getSlot(paramString, paramInt1, 1);
/* 2452 */       if (localSlot == null)
/* 2453 */         return false;
/*      */     }
/* 2455 */     else if (!isExtensible()) {
/* 2456 */       localSlot = getSlot(paramString, paramInt1, 1);
/* 2457 */       if (localSlot == null)
/* 2458 */         return true;
/*      */     }
/*      */     else {
/* 2461 */       checkNotSealed(paramString, paramInt1);
/*      */ 
/* 2463 */       if (paramInt2 != 0) {
/* 2464 */         localSlot = getSlot(paramString, paramInt1, 5);
/* 2465 */         int i = localSlot.getAttributes();
/* 2466 */         if ((i & 0x1) == 0)
/* 2467 */           throw Context.reportRuntimeError1("msg.var.redecl", paramString);
/* 2468 */         if ((i & 0x8) != 0) {
/* 2469 */           localSlot.value = paramObject;
/*      */ 
/* 2471 */           if (paramInt2 != 8)
/* 2472 */             localSlot.setAttributes(i & 0xFFFFFFF7);
/*      */         }
/* 2474 */         return true;
/*      */       }
/* 2476 */       localSlot = getSlot(paramString, paramInt1, 2);
/*      */     }
/* 2478 */     if ((localSlot instanceof GetterSlot)) {
/* 2479 */       GetterSlot localGetterSlot = (GetterSlot)localSlot;
/* 2480 */       Object localObject1 = localGetterSlot.setter;
/* 2481 */       if (localObject1 == null) {
/* 2482 */         if (localGetterSlot.getter != null) {
/* 2483 */           if (Context.getContext().hasFeature(11))
/*      */           {
/* 2486 */             throw ScriptRuntime.typeError1("msg.set.prop.no.setter", paramString);
/*      */           }
/*      */ 
/* 2490 */           return true;
/*      */         }
/*      */       } else {
/* 2493 */         Context localContext = Context.getContext();
/*      */         Object localObject2;
/* 2494 */         if ((localObject1 instanceof MemberBox)) {
/* 2495 */           localObject2 = (MemberBox)localObject1;
/* 2496 */           Class[] arrayOfClass = ((MemberBox)localObject2).argTypes;
/*      */ 
/* 2499 */           Class localClass = arrayOfClass[(arrayOfClass.length - 1)];
/* 2500 */           int j = FunctionObject.getTypeTag(localClass);
/* 2501 */           Object localObject3 = FunctionObject.convertArg(localContext, paramScriptable, paramObject, j);
/*      */           Object localObject4;
/*      */           Object[] arrayOfObject;
/* 2505 */           if (((MemberBox)localObject2).delegateTo == null) {
/* 2506 */             localObject4 = paramScriptable;
/* 2507 */             arrayOfObject = new Object[] { localObject3 };
/*      */           } else {
/* 2509 */             localObject4 = ((MemberBox)localObject2).delegateTo;
/* 2510 */             arrayOfObject = new Object[] { paramScriptable, localObject3 };
/*      */           }
/* 2512 */           ((MemberBox)localObject2).invoke(localObject4, arrayOfObject);
/*      */         } else {
/* 2514 */           localObject2 = (Function)localObject1;
/* 2515 */           ((Function)localObject2).call(localContext, ((Function)localObject2).getParentScope(), paramScriptable, new Object[] { paramObject });
/*      */         }
/*      */ 
/* 2518 */         return true;
/*      */       }
/* 2520 */     } else if ((localSlot.getAttributes() & 0x1) != 0) {
/* 2521 */       return true;
/*      */     }
/* 2523 */     if (this == paramScriptable) {
/* 2524 */       localSlot.value = paramObject;
/* 2525 */       return true;
/*      */     }
/* 2527 */     return false;
/*      */   }
/*      */ 
/*      */   private Slot findAttributeSlot(String paramString, int paramInt1, int paramInt2)
/*      */   {
/* 2533 */     Slot localSlot = getSlot(paramString, paramInt1, paramInt2);
/* 2534 */     if (localSlot == null) {
/* 2535 */       String str = paramString != null ? paramString : Integer.toString(paramInt1);
/* 2536 */       throw Context.reportRuntimeError1("msg.prop.not.found", str);
/*      */     }
/* 2538 */     return localSlot;
/*      */   }
/*      */ 
/*      */   private Slot getSlot(String paramString, int paramInt1, int paramInt2)
/*      */   {
/* 2554 */     Slot localSlot = this.lastAccess;
/* 2555 */     if (paramString != null ? 
/* 2556 */       paramString == localSlot.name : 
/* 2561 */       (localSlot.name == null) && (paramInt1 == localSlot.indexOrHash))
/*      */     {
/* 2565 */       if (!localSlot.wasDeleted)
/*      */       {
/* 2568 */         if ((paramInt2 != 4) || ((localSlot instanceof GetterSlot)))
/*      */         {
/* 2572 */           if ((paramInt2 != 6) || (!(localSlot instanceof GetterSlot)))
/*      */           {
/* 2576 */             return localSlot;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 2579 */     localSlot = accessSlot(paramString, paramInt1, paramInt2);
/* 2580 */     if (localSlot != null)
/*      */     {
/* 2582 */       this.lastAccess = localSlot;
/*      */     }
/* 2584 */     return localSlot;
/*      */   }
/*      */ 
/*      */   private Slot accessSlot(String paramString, int paramInt1, int paramInt2)
/*      */   {
/* 2589 */     int i = paramString != null ? paramString.hashCode() : paramInt1;
/*      */     int k;
/*      */     Object localObject1;
/*      */     Object localObject2;
/* 2591 */     if ((paramInt2 == 1) || (paramInt2 == 2) || (paramInt2 == 5) || (paramInt2 == 4) || (paramInt2 == 6))
/*      */     {
/* 2599 */       Slot[] arrayOfSlot = this.slots;
/* 2600 */       if (arrayOfSlot == null) {
/* 2601 */         if (paramInt2 == 1)
/* 2602 */           return null;
/*      */       } else {
/* 2604 */         int j = arrayOfSlot.length;
/* 2605 */         k = getSlotIndex(j, i);
/* 2606 */         Slot localSlot1 = arrayOfSlot[k];
/* 2607 */         while (localSlot1 != null) {
/* 2608 */           localObject1 = localSlot1.name;
/* 2609 */           if (localObject1 != null) {
/* 2610 */             if (localObject1 == paramString)
/*      */               break;
/* 2612 */             if ((paramString != null) && (i == localSlot1.indexOrHash) && 
/* 2613 */               (paramString.equals(localObject1)))
/*      */             {
/* 2617 */               localSlot1.name = paramString;
/* 2618 */               break;
/*      */             }
/*      */           } else {
/* 2621 */             if ((paramString == null) && (i == localSlot1.indexOrHash)) {
/*      */               break;
/*      */             }
/*      */           }
/* 2625 */           localSlot1 = localSlot1.next;
/*      */         }
/* 2627 */         if (paramInt2 == 1)
/* 2628 */           return localSlot1;
/* 2629 */         if (paramInt2 == 2) {
/* 2630 */           if (localSlot1 != null)
/* 2631 */             return localSlot1;
/* 2632 */         } else if (paramInt2 == 4) {
/* 2633 */           if ((localSlot1 instanceof GetterSlot))
/* 2634 */             return localSlot1;
/* 2635 */         } else if (paramInt2 == 5) {
/* 2636 */           if (localSlot1 != null)
/* 2637 */             return localSlot1;
/* 2638 */         } else if ((paramInt2 == 6) && 
/* 2639 */           (!(localSlot1 instanceof GetterSlot))) {
/* 2640 */           return localSlot1;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2647 */       synchronized (this)
/*      */       {
/* 2649 */         arrayOfSlot = this.slots;
/*      */ 
/* 2651 */         if (this.count == 0)
/*      */         {
/* 2653 */           arrayOfSlot = new Slot[5];
/* 2654 */           this.slots = arrayOfSlot;
/* 2655 */           k = getSlotIndex(arrayOfSlot.length, i);
/*      */         } else {
/* 2657 */           int m = arrayOfSlot.length;
/* 2658 */           k = getSlotIndex(m, i);
/* 2659 */           localObject1 = arrayOfSlot[k];
/* 2660 */           localObject2 = localObject1;
/* 2661 */           while ((localObject2 != null) && (
/* 2662 */             (((Slot)localObject2).indexOrHash != i) || ((((Slot)localObject2).name != paramString) && ((paramString == null) || (!paramString.equals(((Slot)localObject2).name))))))
/*      */           {
/* 2668 */             localObject1 = localObject2;
/* 2669 */             localObject2 = ((Slot)localObject2).next;
/*      */           }
/*      */ 
/* 2672 */           if (localObject2 != null)
/*      */           {
/*      */             Object localObject3;
/* 2683 */             if ((paramInt2 == 4) && (!(localObject2 instanceof GetterSlot))) {
/* 2684 */               localObject3 = new GetterSlot(paramString, i, ((Slot)localObject2).getAttributes());
/* 2685 */             } else if ((paramInt2 == 6) && ((localObject2 instanceof GetterSlot))) {
/* 2686 */               localObject3 = new Slot(paramString, i, ((Slot)localObject2).getAttributes()); } else {
/* 2687 */               if (paramInt2 == 5) {
/* 2688 */                 return null;
/*      */               }
/* 2690 */               return localObject2;
/*      */             }
/*      */ 
/* 2693 */             ((Slot)localObject3).value = ((Slot)localObject2).value;
/* 2694 */             ((Slot)localObject3).next = ((Slot)localObject2).next;
/*      */ 
/* 2696 */             if (this.lastAdded != null)
/* 2697 */               this.lastAdded.orderedNext = ((Slot)localObject3);
/* 2698 */             if (this.firstAdded == null)
/* 2699 */               this.firstAdded = ((Slot)localObject3);
/* 2700 */             this.lastAdded = ((Slot)localObject3);
/*      */ 
/* 2702 */             if (localObject1 == localObject2)
/* 2703 */               arrayOfSlot[k] = localObject3;
/*      */             else {
/* 2705 */               ((Slot)localObject1).next = ((Slot)localObject3);
/*      */             }
/*      */ 
/* 2708 */             ((Slot)localObject2).wasDeleted = true;
/* 2709 */             ((Slot)localObject2).value = null;
/* 2710 */             ((Slot)localObject2).name = null;
/* 2711 */             if (localObject2 == this.lastAccess) {
/* 2712 */               this.lastAccess = REMOVED;
/*      */             }
/* 2714 */             return localObject3;
/*      */           }
/*      */ 
/* 2717 */           if (4 * (this.count + 1) > 3 * arrayOfSlot.length) {
/* 2718 */             arrayOfSlot = new Slot[arrayOfSlot.length * 2 + 1];
/* 2719 */             copyTable(this.slots, arrayOfSlot, this.count);
/* 2720 */             this.slots = arrayOfSlot;
/* 2721 */             k = getSlotIndex(arrayOfSlot.length, i);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 2726 */         Slot localSlot2 = paramInt2 == 4 ? new GetterSlot(paramString, i, 0) : new Slot(paramString, i, 0);
/*      */ 
/* 2729 */         if (paramInt2 == 5)
/* 2730 */           localSlot2.setAttributes(13);
/* 2731 */         this.count += 1;
/*      */ 
/* 2733 */         if (this.lastAdded != null)
/* 2734 */           this.lastAdded.orderedNext = localSlot2;
/* 2735 */         if (this.firstAdded == null)
/* 2736 */           this.firstAdded = localSlot2;
/* 2737 */         this.lastAdded = localSlot2;
/*      */ 
/* 2739 */         addKnownAbsentSlot(arrayOfSlot, localSlot2, k);
/* 2740 */         return localSlot2;
/*      */       }
/*      */     }
/* 2743 */     if (paramInt2 == 3) {
/* 2744 */       synchronized (this) {
/* 2745 */         ??? = this.slots;
/* 2746 */         if (this.count != 0) {
/* 2747 */           k = this.slots.length;
/* 2748 */           int n = getSlotIndex(k, i);
/* 2749 */           localObject1 = ???[n];
/* 2750 */           localObject2 = localObject1;
/* 2751 */           while ((localObject2 != null) && (
/* 2752 */             (((Slot)localObject2).indexOrHash != i) || ((((Slot)localObject2).name != paramString) && ((paramString == null) || (!paramString.equals(((Slot)localObject2).name))))))
/*      */           {
/* 2758 */             localObject1 = localObject2;
/* 2759 */             localObject2 = ((Slot)localObject2).next;
/*      */           }
/* 2761 */           if ((localObject2 != null) && ((((Slot)localObject2).getAttributes() & 0x4) == 0)) {
/* 2762 */             this.count -= 1;
/*      */ 
/* 2764 */             if (localObject1 == localObject2)
/* 2765 */               ???[n] = ((Slot)localObject2).next;
/*      */             else {
/* 2767 */               ((Slot)localObject1).next = ((Slot)localObject2).next;
/*      */             }
/*      */ 
/* 2772 */             ((Slot)localObject2).wasDeleted = true;
/* 2773 */             ((Slot)localObject2).value = null;
/* 2774 */             ((Slot)localObject2).name = null;
/* 2775 */             if (localObject2 == this.lastAccess) {
/* 2776 */               this.lastAccess = REMOVED;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 2781 */       return null;
/*      */     }
/*      */ 
/* 2784 */     throw Kit.codeBug();
/*      */   }
/*      */ 
/*      */   private static int getSlotIndex(int paramInt1, int paramInt2)
/*      */   {
/* 2790 */     return (paramInt2 & 0x7FFFFFFF) % paramInt1;
/*      */   }
/*      */ 
/*      */   private static void copyTable(Slot[] paramArrayOfSlot1, Slot[] paramArrayOfSlot2, int paramInt)
/*      */   {
/* 2796 */     if (paramInt == 0) throw Kit.codeBug();
/*      */ 
/* 2798 */     int i = paramArrayOfSlot2.length;
/* 2799 */     int j = paramArrayOfSlot1.length;
/*      */     while (true) {
/* 2801 */       j--;
/* 2802 */       Object localObject = paramArrayOfSlot1[j];
/* 2803 */       while (localObject != null) {
/* 2804 */         int k = getSlotIndex(i, ((Slot)localObject).indexOrHash);
/* 2805 */         Slot localSlot = ((Slot)localObject).next;
/* 2806 */         addKnownAbsentSlot(paramArrayOfSlot2, (Slot)localObject, k);
/* 2807 */         ((Slot)localObject).next = null;
/* 2808 */         localObject = localSlot;
/* 2809 */         paramInt--; if (paramInt == 0)
/* 2810 */           return;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static void addKnownAbsentSlot(Slot[] paramArrayOfSlot, Slot paramSlot, int paramInt)
/*      */   {
/* 2823 */     if (paramArrayOfSlot[paramInt] == null) {
/* 2824 */       paramArrayOfSlot[paramInt] = paramSlot;
/*      */     } else {
/* 2826 */       Slot localSlot = paramArrayOfSlot[paramInt];
/* 2827 */       while (localSlot.next != null) {
/* 2828 */         localSlot = localSlot.next;
/*      */       }
/* 2830 */       localSlot.next = paramSlot;
/*      */     }
/*      */   }
/*      */ 
/*      */   Object[] getIds(boolean paramBoolean) {
/* 2835 */     Slot[] arrayOfSlot = this.slots;
/* 2836 */     Object[] arrayOfObject = ScriptRuntime.emptyArgs;
/* 2837 */     if (arrayOfSlot == null)
/* 2838 */       return arrayOfObject;
/* 2839 */     int i = 0;
/* 2840 */     Object localObject1 = this.firstAdded;
/* 2841 */     while ((localObject1 != null) && (((Slot)localObject1).wasDeleted))
/*      */     {
/* 2844 */       localObject1 = ((Slot)localObject1).orderedNext;
/*      */     }
/* 2846 */     this.firstAdded = ((Slot)localObject1);
/* 2847 */     if (localObject1 != null) {
/*      */       while (true) {
/* 2849 */         if ((paramBoolean) || ((((Slot)localObject1).getAttributes() & 0x2) == 0)) {
/* 2850 */           if (i == 0)
/* 2851 */             arrayOfObject = new Object[arrayOfSlot.length];
/* 2852 */           arrayOfObject[(i++)] = (((Slot)localObject1).name != null ? ((Slot)localObject1).name : Integer.valueOf(((Slot)localObject1).indexOrHash));
/*      */         }
/*      */ 
/* 2856 */         localObject2 = ((Slot)localObject1).orderedNext;
/* 2857 */         while ((localObject2 != null) && (((Slot)localObject2).wasDeleted))
/*      */         {
/* 2859 */           localObject2 = ((Slot)localObject2).orderedNext;
/*      */         }
/* 2861 */         ((Slot)localObject1).orderedNext = ((Slot)localObject2);
/* 2862 */         if (localObject2 == null) {
/*      */           break;
/*      */         }
/* 2865 */         localObject1 = localObject2;
/*      */       }
/*      */     }
/* 2868 */     this.lastAdded = ((Slot)localObject1);
/* 2869 */     if (i == arrayOfObject.length)
/* 2870 */       return arrayOfObject;
/* 2871 */     Object localObject2 = new Object[i];
/* 2872 */     System.arraycopy(arrayOfObject, 0, localObject2, 0, i);
/* 2873 */     return localObject2;
/*      */   }
/*      */ 
/*      */   protected ScriptableObject getOwnPropertyDescriptor(Context paramContext, Object paramObject) {
/* 2877 */     Slot localSlot = getSlot(paramContext, paramObject, 1);
/* 2878 */     if (localSlot == null) return null;
/* 2879 */     Scriptable localScriptable = getParentScope();
/* 2880 */     return localSlot.getPropertyDescriptor(paramContext, localScriptable == null ? this : localScriptable);
/*      */   }
/*      */ 
/*      */   protected Slot getSlot(Context paramContext, Object paramObject, int paramInt)
/*      */   {
/* 2885 */     String str = ScriptRuntime.toStringIdOrIndex(paramContext, paramObject);
/*      */     Slot localSlot;
/* 2886 */     if (str == null) {
/* 2887 */       int i = ScriptRuntime.lastIndexResult(paramContext);
/* 2888 */       localSlot = getSlot(null, i, paramInt);
/*      */     } else {
/* 2890 */       localSlot = getSlot(str, 0, paramInt);
/*      */     }
/* 2892 */     return localSlot;
/*      */   }
/*      */ 
/*      */   public int size()
/*      */   {
/* 2899 */     return this.count;
/*      */   }
/*      */ 
/*      */   public boolean isEmpty() {
/* 2903 */     return this.count == 0;
/*      */   }
/*      */ 
/*      */   public Object get(Object paramObject)
/*      */   {
/* 2908 */     Object localObject = null;
/* 2909 */     if ((paramObject instanceof String))
/* 2910 */       localObject = get((String)paramObject, this);
/* 2911 */     else if ((paramObject instanceof Number)) {
/* 2912 */       localObject = get(((Number)paramObject).intValue(), this);
/*      */     }
/* 2914 */     if ((localObject == ConstProperties.NOT_FOUND) || (localObject == Undefined.instance))
/* 2915 */       return null;
/* 2916 */     if ((localObject instanceof Wrapper)) {
/* 2917 */       return ((Wrapper)localObject).unwrap();
/*      */     }
/* 2919 */     return localObject;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  148 */     REMOVED.wasDeleted = true;
/*      */   }
/*      */ 
/*      */   private static final class GetterSlot extends ScriptableObject.Slot
/*      */   {
/*      */     Object getter;
/*      */     Object setter;
/*      */ 
/*      */     GetterSlot(String paramString, int paramInt1, int paramInt2)
/*      */     {
/*  239 */       super(paramInt1, paramInt2);
/*      */     }
/*      */ 
/*      */     ScriptableObject getPropertyDescriptor(Context paramContext, Scriptable paramScriptable)
/*      */     {
/*  244 */       ScriptableObject localScriptableObject = super.getPropertyDescriptor(paramContext, paramScriptable);
/*  245 */       localScriptableObject.delete("value");
/*  246 */       localScriptableObject.delete("writable");
/*  247 */       if (this.getter != null) localScriptableObject.defineProperty("get", this.getter, 0);
/*  248 */       if (this.setter != null) localScriptableObject.defineProperty("set", this.setter, 0);
/*  249 */       return localScriptableObject;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class Slot
/*      */   {
/*      */     String name;
/*      */     int indexOrHash;
/*      */     private volatile short attributes;
/*      */     volatile transient boolean wasDeleted;
/*      */     volatile Object value;
/*      */     volatile transient Slot next;
/*      */     volatile transient Slot orderedNext;
/*      */ 
/*      */     Slot(String paramString, int paramInt1, int paramInt2)
/*      */     {
/*  186 */       this.name = paramString;
/*  187 */       this.indexOrHash = paramInt1;
/*  188 */       this.attributes = ((short)paramInt2);
/*      */     }
/*      */ 
/*      */     final int getAttributes()
/*      */     {
/*  193 */       return this.attributes;
/*      */     }
/*      */ 
/*      */     final synchronized void setAttributes(int paramInt)
/*      */     {
/*  198 */       ScriptableObject.checkValidAttributes(paramInt);
/*  199 */       this.attributes = ((short)paramInt);
/*      */     }
/*      */ 
/*      */     final void checkNotReadonly()
/*      */     {
/*  204 */       if ((this.attributes & 0x1) != 0) {
/*  205 */         String str = this.name != null ? this.name : Integer.toString(this.indexOrHash);
/*      */ 
/*  207 */         throw Context.reportRuntimeError1("msg.modify.readonly", str);
/*      */       }
/*      */     }
/*      */ 
/*      */     ScriptableObject getPropertyDescriptor(Context paramContext, Scriptable paramScriptable) {
/*  212 */       return ScriptableObject.buildDataDescriptor(paramScriptable, this.value == null ? Undefined.instance : this.value, this.attributes);
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.org.mozilla.javascript.internal.ScriptableObject
 * JD-Core Version:    0.6.2
 */