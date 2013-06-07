/*     */ package com.sun.xml.internal.bind.v2.model.nav;
/*     */ 
/*     */ import com.sun.xml.internal.bind.v2.runtime.Location;
/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.GenericArrayType;
/*     */ import java.lang.reflect.GenericDeclaration;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.lang.reflect.ParameterizedType;
/*     */ import java.lang.reflect.Type;
/*     */ import java.lang.reflect.TypeVariable;
/*     */ import java.lang.reflect.WildcardType;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ 
/*     */ public final class ReflectionNavigator
/*     */   implements Navigator<Type, Class, Field, Method>
/*     */ {
/*  67 */   private static final TypeVisitor<Type, Class> baseClassFinder = new TypeVisitor()
/*     */   {
/*     */     public Type onClass(Class c, Class sup)
/*     */     {
/*  71 */       if (sup == c) {
/*  72 */         return sup;
/*     */       }
/*     */ 
/*  77 */       Type sc = c.getGenericSuperclass();
/*  78 */       if (sc != null) {
/*  79 */         Type r = (Type)visit(sc, sup);
/*  80 */         if (r != null) {
/*  81 */           return r;
/*     */         }
/*     */       }
/*     */ 
/*  85 */       for (Type i : c.getGenericInterfaces()) {
/*  86 */         Type r = (Type)visit(i, sup);
/*  87 */         if (r != null) {
/*  88 */           return r;
/*     */         }
/*     */       }
/*     */ 
/*  92 */       return null;
/*     */     }
/*     */ 
/*     */     public Type onParameterizdType(ParameterizedType p, Class sup) {
/*  96 */       Class raw = (Class)p.getRawType();
/*  97 */       if (raw == sup)
/*     */       {
/*  99 */         return p;
/*     */       }
/*     */ 
/* 102 */       Type r = raw.getGenericSuperclass();
/* 103 */       if (r != null) {
/* 104 */         r = (Type)visit(bind(r, raw, p), sup);
/*     */       }
/* 106 */       if (r != null) {
/* 107 */         return r;
/*     */       }
/* 109 */       for (Type i : raw.getGenericInterfaces()) {
/* 110 */         r = (Type)visit(bind(i, raw, p), sup);
/* 111 */         if (r != null) {
/* 112 */           return r;
/*     */         }
/*     */       }
/* 115 */       return null;
/*     */     }
/*     */ 
/*     */     public Type onGenericArray(GenericArrayType g, Class sup)
/*     */     {
/* 121 */       return null;
/*     */     }
/*     */ 
/*     */     public Type onVariable(TypeVariable v, Class sup) {
/* 125 */       return (Type)visit(v.getBounds()[0], sup);
/*     */     }
/*     */ 
/*     */     public Type onWildcard(WildcardType w, Class sup)
/*     */     {
/* 130 */       return null;
/*     */     }
/*     */ 
/*     */     private Type bind(Type t, GenericDeclaration decl, ParameterizedType args)
/*     */     {
/* 142 */       return (Type)ReflectionNavigator.binder.visit(t, new ReflectionNavigator.BinderArg(decl, args.getActualTypeArguments()));
/*     */     }
/*  67 */   };
/*     */ 
/* 170 */   private static final TypeVisitor<Type, BinderArg> binder = new TypeVisitor()
/*     */   {
/*     */     public Type onClass(Class c, ReflectionNavigator.BinderArg args) {
/* 173 */       return c;
/*     */     }
/*     */ 
/*     */     public Type onParameterizdType(ParameterizedType p, ReflectionNavigator.BinderArg args) {
/* 177 */       Type[] params = p.getActualTypeArguments();
/*     */ 
/* 179 */       boolean different = false;
/* 180 */       for (int i = 0; i < params.length; i++) {
/* 181 */         Type t = params[i];
/* 182 */         params[i] = ((Type)visit(t, args));
/* 183 */         different |= t != params[i];
/*     */       }
/*     */ 
/* 186 */       Type newOwner = p.getOwnerType();
/* 187 */       if (newOwner != null) {
/* 188 */         newOwner = (Type)visit(newOwner, args);
/*     */       }
/* 190 */       different |= p.getOwnerType() != newOwner;
/*     */ 
/* 192 */       if (!different) {
/* 193 */         return p;
/*     */       }
/*     */ 
/* 196 */       return new ParameterizedTypeImpl((Class)p.getRawType(), params, newOwner);
/*     */     }
/*     */ 
/*     */     public Type onGenericArray(GenericArrayType g, ReflectionNavigator.BinderArg types) {
/* 200 */       Type c = (Type)visit(g.getGenericComponentType(), types);
/* 201 */       if (c == g.getGenericComponentType()) {
/* 202 */         return g;
/*     */       }
/*     */ 
/* 205 */       return new GenericArrayTypeImpl(c);
/*     */     }
/*     */ 
/*     */     public Type onVariable(TypeVariable v, ReflectionNavigator.BinderArg types) {
/* 209 */       return types.replace(v);
/*     */     }
/*     */ 
/*     */     public Type onWildcard(WildcardType w, ReflectionNavigator.BinderArg types)
/*     */     {
/* 216 */       Type[] lb = w.getLowerBounds();
/* 217 */       Type[] ub = w.getUpperBounds();
/* 218 */       boolean diff = false;
/*     */ 
/* 220 */       for (int i = 0; i < lb.length; i++) {
/* 221 */         Type t = lb[i];
/* 222 */         lb[i] = ((Type)visit(t, types));
/* 223 */         diff |= t != lb[i];
/*     */       }
/*     */ 
/* 226 */       for (int i = 0; i < ub.length; i++) {
/* 227 */         Type t = ub[i];
/* 228 */         ub[i] = ((Type)visit(t, types));
/* 229 */         diff |= t != ub[i];
/*     */       }
/*     */ 
/* 232 */       if (!diff) {
/* 233 */         return w;
/*     */       }
/*     */ 
/* 236 */       return new WildcardTypeImpl(lb, ub);
/*     */     }
/* 170 */   };
/*     */ 
/* 343 */   private static final TypeVisitor<Class, Void> eraser = new TypeVisitor()
/*     */   {
/*     */     public Class onClass(Class c, Void _) {
/* 346 */       return c;
/*     */     }
/*     */ 
/*     */     public Class onParameterizdType(ParameterizedType p, Void _)
/*     */     {
/* 351 */       return (Class)visit(p.getRawType(), null);
/*     */     }
/*     */ 
/*     */     public Class onGenericArray(GenericArrayType g, Void _) {
/* 355 */       return Array.newInstance((Class)visit(g.getGenericComponentType(), null), 0).getClass();
/*     */     }
/*     */ 
/*     */     public Class onVariable(TypeVariable v, Void _)
/*     */     {
/* 361 */       return (Class)visit(v.getBounds()[0], null);
/*     */     }
/*     */ 
/*     */     public Class onWildcard(WildcardType w, Void _) {
/* 365 */       return (Class)visit(w.getUpperBounds()[0], null);
/*     */     }
/* 343 */   };
/*     */ 
/*     */   public Class getSuperClass(Class clazz)
/*     */   {
/*  58 */     if (clazz == Object.class) {
/*  59 */       return null;
/*     */     }
/*  61 */     Class sc = clazz.getSuperclass();
/*  62 */     if (sc == null) {
/*  63 */       sc = Object.class;
/*     */     }
/*  65 */     return sc;
/*     */   }
/*     */ 
/*     */   public Type getBaseClass(Type t, Class sup)
/*     */   {
/* 241 */     return (Type)baseClassFinder.visit(t, sup);
/*     */   }
/*     */ 
/*     */   public String getClassName(Class clazz) {
/* 245 */     return clazz.getName();
/*     */   }
/*     */ 
/*     */   public String getTypeName(Type type) {
/* 249 */     if ((type instanceof Class)) {
/* 250 */       Class c = (Class)type;
/* 251 */       if (c.isArray()) {
/* 252 */         return getTypeName(c.getComponentType()) + "[]";
/*     */       }
/* 254 */       return c.getName();
/*     */     }
/* 256 */     return type.toString();
/*     */   }
/*     */ 
/*     */   public String getClassShortName(Class clazz) {
/* 260 */     return clazz.getSimpleName();
/*     */   }
/*     */ 
/*     */   public Collection<? extends Field> getDeclaredFields(Class clazz) {
/* 264 */     return Arrays.asList(clazz.getDeclaredFields());
/*     */   }
/*     */ 
/*     */   public Field getDeclaredField(Class clazz, String fieldName) {
/*     */     try {
/* 269 */       return clazz.getDeclaredField(fieldName); } catch (NoSuchFieldException e) {
/*     */     }
/* 271 */     return null;
/*     */   }
/*     */ 
/*     */   public Collection<? extends Method> getDeclaredMethods(Class clazz)
/*     */   {
/* 276 */     return Arrays.asList(clazz.getDeclaredMethods());
/*     */   }
/*     */ 
/*     */   public Class getDeclaringClassForField(Field field) {
/* 280 */     return field.getDeclaringClass();
/*     */   }
/*     */ 
/*     */   public Class getDeclaringClassForMethod(Method method) {
/* 284 */     return method.getDeclaringClass();
/*     */   }
/*     */ 
/*     */   public Type getFieldType(Field field) {
/* 288 */     if (field.getType().isArray()) {
/* 289 */       Class c = field.getType().getComponentType();
/* 290 */       if (c.isPrimitive()) {
/* 291 */         return Array.newInstance(c, 0).getClass();
/*     */       }
/*     */     }
/* 294 */     return fix(field.getGenericType());
/*     */   }
/*     */ 
/*     */   public String getFieldName(Field field) {
/* 298 */     return field.getName();
/*     */   }
/*     */ 
/*     */   public String getMethodName(Method method) {
/* 302 */     return method.getName();
/*     */   }
/*     */ 
/*     */   public Type getReturnType(Method method) {
/* 306 */     return fix(method.getGenericReturnType());
/*     */   }
/*     */ 
/*     */   public Type[] getMethodParameters(Method method) {
/* 310 */     return method.getGenericParameterTypes();
/*     */   }
/*     */ 
/*     */   public boolean isStaticMethod(Method method) {
/* 314 */     return Modifier.isStatic(method.getModifiers());
/*     */   }
/*     */ 
/*     */   public boolean isFinalMethod(Method method) {
/* 318 */     return Modifier.isFinal(method.getModifiers());
/*     */   }
/*     */ 
/*     */   public boolean isSubClassOf(Type sub, Type sup) {
/* 322 */     return erasure(sup).isAssignableFrom(erasure(sub));
/*     */   }
/*     */ 
/*     */   public Class ref(Class c) {
/* 326 */     return c;
/*     */   }
/*     */ 
/*     */   public Class use(Class c) {
/* 330 */     return c;
/*     */   }
/*     */ 
/*     */   public Class asDecl(Type t) {
/* 334 */     return erasure(t);
/*     */   }
/*     */ 
/*     */   public Class asDecl(Class c) {
/* 338 */     return c;
/*     */   }
/*     */ 
/*     */   public <T> Class<T> erasure(Type t)
/*     */   {
/* 385 */     return (Class)eraser.visit(t, null);
/*     */   }
/*     */ 
/*     */   public boolean isAbstract(Class clazz) {
/* 389 */     return Modifier.isAbstract(clazz.getModifiers());
/*     */   }
/*     */ 
/*     */   public boolean isFinal(Class clazz) {
/* 393 */     return Modifier.isFinal(clazz.getModifiers());
/*     */   }
/*     */ 
/*     */   public Type createParameterizedType(Class rawType, Type[] arguments)
/*     */   {
/* 400 */     return new ParameterizedTypeImpl(rawType, arguments, null);
/*     */   }
/*     */ 
/*     */   public boolean isArray(Type t) {
/* 404 */     if ((t instanceof Class)) {
/* 405 */       Class c = (Class)t;
/* 406 */       return c.isArray();
/*     */     }
/* 408 */     if ((t instanceof GenericArrayType)) {
/* 409 */       return true;
/*     */     }
/* 411 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isArrayButNotByteArray(Type t) {
/* 415 */     if ((t instanceof Class)) {
/* 416 */       Class c = (Class)t;
/* 417 */       return (c.isArray()) && (c != [B.class);
/*     */     }
/* 419 */     if ((t instanceof GenericArrayType)) {
/* 420 */       t = ((GenericArrayType)t).getGenericComponentType();
/* 421 */       return t != Byte.TYPE;
/*     */     }
/* 423 */     return false;
/*     */   }
/*     */ 
/*     */   public Type getComponentType(Type t) {
/* 427 */     if ((t instanceof Class)) {
/* 428 */       Class c = (Class)t;
/* 429 */       return c.getComponentType();
/*     */     }
/* 431 */     if ((t instanceof GenericArrayType)) {
/* 432 */       return ((GenericArrayType)t).getGenericComponentType();
/*     */     }
/*     */ 
/* 435 */     throw new IllegalArgumentException();
/*     */   }
/*     */ 
/*     */   public Type getTypeArgument(Type type, int i) {
/* 439 */     if ((type instanceof ParameterizedType)) {
/* 440 */       ParameterizedType p = (ParameterizedType)type;
/* 441 */       return fix(p.getActualTypeArguments()[i]);
/*     */     }
/* 443 */     throw new IllegalArgumentException();
/*     */   }
/*     */ 
/*     */   public boolean isParameterizedType(Type type)
/*     */   {
/* 448 */     return type instanceof ParameterizedType;
/*     */   }
/*     */ 
/*     */   public boolean isPrimitive(Type type) {
/* 452 */     if ((type instanceof Class)) {
/* 453 */       Class c = (Class)type;
/* 454 */       return c.isPrimitive();
/*     */     }
/* 456 */     return false;
/*     */   }
/*     */ 
/*     */   public Type getPrimitive(Class primitiveType) {
/* 460 */     assert (primitiveType.isPrimitive());
/* 461 */     return primitiveType;
/*     */   }
/*     */ 
/*     */   public Location getClassLocation(final Class clazz) {
/* 465 */     return new Location()
/*     */     {
/*     */       public String toString()
/*     */       {
/* 469 */         return clazz.getName();
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public Location getFieldLocation(final Field field) {
/* 475 */     return new Location()
/*     */     {
/*     */       public String toString()
/*     */       {
/* 479 */         return field.toString();
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public Location getMethodLocation(final Method method) {
/* 485 */     return new Location()
/*     */     {
/*     */       public String toString()
/*     */       {
/* 489 */         return method.toString();
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public boolean hasDefaultConstructor(Class c) {
/*     */     try {
/* 496 */       c.getDeclaredConstructor(new Class[0]);
/* 497 */       return true; } catch (NoSuchMethodException e) {
/*     */     }
/* 499 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isStaticField(Field field)
/*     */   {
/* 504 */     return Modifier.isStatic(field.getModifiers());
/*     */   }
/*     */ 
/*     */   public boolean isPublicMethod(Method method) {
/* 508 */     return Modifier.isPublic(method.getModifiers());
/*     */   }
/*     */ 
/*     */   public boolean isPublicField(Field field) {
/* 512 */     return Modifier.isPublic(field.getModifiers());
/*     */   }
/*     */ 
/*     */   public boolean isEnum(Class c) {
/* 516 */     return Enum.class.isAssignableFrom(c);
/*     */   }
/*     */ 
/*     */   public Field[] getEnumConstants(Class clazz) {
/*     */     try {
/* 521 */       Object[] values = clazz.getEnumConstants();
/* 522 */       Field[] fields = new Field[values.length];
/* 523 */       for (int i = 0; i < values.length; i++) {
/* 524 */         fields[i] = clazz.getField(((Enum)values[i]).name());
/*     */       }
/* 526 */       return fields;
/*     */     }
/*     */     catch (NoSuchFieldException e) {
/* 529 */       throw new NoSuchFieldError(e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public Type getVoidType() {
/* 534 */     return Void.class;
/*     */   }
/*     */ 
/*     */   public String getPackageName(Class clazz) {
/* 538 */     String name = clazz.getName();
/* 539 */     int idx = name.lastIndexOf('.');
/* 540 */     if (idx < 0) {
/* 541 */       return "";
/*     */     }
/* 543 */     return name.substring(0, idx);
/*     */   }
/*     */ 
/*     */   public Class findClass(String className, Class referencePoint)
/*     */   {
/*     */     try {
/* 549 */       ClassLoader cl = referencePoint.getClassLoader();
/* 550 */       if (cl == null) {
/* 551 */         cl = ClassLoader.getSystemClassLoader();
/*     */       }
/* 553 */       return cl.loadClass(className); } catch (ClassNotFoundException e) {
/*     */     }
/* 555 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean isBridgeMethod(Method method)
/*     */   {
/* 560 */     return method.isBridge();
/*     */   }
/*     */ 
/*     */   public boolean isOverriding(Method method, Class base)
/*     */   {
/* 574 */     String name = method.getName();
/* 575 */     Class[] params = method.getParameterTypes();
/*     */ 
/* 577 */     while (base != null) {
/*     */       try {
/* 579 */         if (base.getDeclaredMethod(name, params) != null) {
/* 580 */           return true;
/*     */         }
/*     */       }
/*     */       catch (NoSuchMethodException e)
/*     */       {
/*     */       }
/* 586 */       base = base.getSuperclass();
/*     */     }
/*     */ 
/* 589 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isInterface(Class clazz) {
/* 593 */     return clazz.isInterface();
/*     */   }
/*     */ 
/*     */   public boolean isTransient(Field f) {
/* 597 */     return Modifier.isTransient(f.getModifiers());
/*     */   }
/*     */ 
/*     */   public boolean isInnerClass(Class clazz) {
/* 601 */     return (clazz.getEnclosingClass() != null) && (!Modifier.isStatic(clazz.getModifiers()));
/*     */   }
/*     */ 
/*     */   private Type fix(Type t)
/*     */   {
/* 611 */     if (!(t instanceof GenericArrayType)) {
/* 612 */       return t;
/*     */     }
/*     */ 
/* 615 */     GenericArrayType gat = (GenericArrayType)t;
/* 616 */     if ((gat.getGenericComponentType() instanceof Class)) {
/* 617 */       Class c = (Class)gat.getGenericComponentType();
/* 618 */       return Array.newInstance(c, 0).getClass();
/*     */     }
/*     */ 
/* 621 */     return t;
/*     */   }
/*     */ 
/*     */   private static class BinderArg
/*     */   {
/*     */     final TypeVariable[] params;
/*     */     final Type[] args;
/*     */ 
/*     */     BinderArg(TypeVariable[] params, Type[] args)
/*     */     {
/* 152 */       this.params = params;
/* 153 */       this.args = args;
/* 154 */       assert (params.length == args.length);
/*     */     }
/*     */ 
/*     */     public BinderArg(GenericDeclaration decl, Type[] args) {
/* 158 */       this(decl.getTypeParameters(), args);
/*     */     }
/*     */ 
/*     */     Type replace(TypeVariable v) {
/* 162 */       for (int i = 0; i < this.params.length; i++) {
/* 163 */         if (this.params[i].equals(v)) {
/* 164 */           return this.args[i];
/*     */         }
/*     */       }
/* 167 */       return v;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.model.nav.ReflectionNavigator
 * JD-Core Version:    0.6.2
 */