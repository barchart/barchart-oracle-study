/*     */ package sun.reflect.annotation;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.Serializable;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.annotation.IncompleteAnnotationException;
/*     */ import java.lang.reflect.AccessibleObject;
/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Arrays;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ 
/*     */ class AnnotationInvocationHandler
/*     */   implements InvocationHandler, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 6182022883658399397L;
/*     */   private final Class<? extends Annotation> type;
/*     */   private final Map<String, Object> memberValues;
/* 287 */   private volatile transient Method[] memberMethods = null;
/*     */ 
/*     */   AnnotationInvocationHandler(Class<? extends Annotation> paramClass, Map<String, Object> paramMap)
/*     */   {
/*  48 */     this.type = paramClass;
/*  49 */     this.memberValues = paramMap;
/*     */   }
/*     */ 
/*     */   public Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject) {
/*  53 */     String str = paramMethod.getName();
/*  54 */     Class[] arrayOfClass = paramMethod.getParameterTypes();
/*     */ 
/*  57 */     if ((str.equals("equals")) && (arrayOfClass.length == 1) && (arrayOfClass[0] == Object.class))
/*     */     {
/*  59 */       return equalsImpl(paramArrayOfObject[0]);
/*  60 */     }assert (arrayOfClass.length == 0);
/*  61 */     if (str.equals("toString"))
/*  62 */       return toStringImpl();
/*  63 */     if (str.equals("hashCode"))
/*  64 */       return Integer.valueOf(hashCodeImpl());
/*  65 */     if (str.equals("annotationType")) {
/*  66 */       return this.type;
/*     */     }
/*     */ 
/*  69 */     Object localObject = this.memberValues.get(str);
/*     */ 
/*  71 */     if (localObject == null) {
/*  72 */       throw new IncompleteAnnotationException(this.type, str);
/*     */     }
/*  74 */     if ((localObject instanceof ExceptionProxy)) {
/*  75 */       throw ((ExceptionProxy)localObject).generateException();
/*     */     }
/*  77 */     if ((localObject.getClass().isArray()) && (Array.getLength(localObject) != 0)) {
/*  78 */       localObject = cloneArray(localObject);
/*     */     }
/*  80 */     return localObject;
/*     */   }
/*     */ 
/*     */   private Object cloneArray(Object paramObject)
/*     */   {
/*  88 */     Class localClass = paramObject.getClass();
/*     */ 
/*  90 */     if (localClass == [B.class) {
/*  91 */       localObject = (byte[])paramObject;
/*  92 */       return ((byte[])localObject).clone();
/*     */     }
/*  94 */     if (localClass == [C.class) {
/*  95 */       localObject = (char[])paramObject;
/*  96 */       return ((char[])localObject).clone();
/*     */     }
/*  98 */     if (localClass == [D.class) {
/*  99 */       localObject = (double[])paramObject;
/* 100 */       return ((double[])localObject).clone();
/*     */     }
/* 102 */     if (localClass == [F.class) {
/* 103 */       localObject = (float[])paramObject;
/* 104 */       return ((float[])localObject).clone();
/*     */     }
/* 106 */     if (localClass == [I.class) {
/* 107 */       localObject = (int[])paramObject;
/* 108 */       return ((int[])localObject).clone();
/*     */     }
/* 110 */     if (localClass == [J.class) {
/* 111 */       localObject = (long[])paramObject;
/* 112 */       return ((long[])localObject).clone();
/*     */     }
/* 114 */     if (localClass == [S.class) {
/* 115 */       localObject = (short[])paramObject;
/* 116 */       return ((short[])localObject).clone();
/*     */     }
/* 118 */     if (localClass == [Z.class) {
/* 119 */       localObject = (boolean[])paramObject;
/* 120 */       return ((boolean[])localObject).clone();
/*     */     }
/*     */ 
/* 123 */     Object localObject = (Object[])paramObject;
/* 124 */     return ((Object[])localObject).clone();
/*     */   }
/*     */ 
/*     */   private String toStringImpl()
/*     */   {
/* 132 */     StringBuffer localStringBuffer = new StringBuffer(128);
/* 133 */     localStringBuffer.append('@');
/* 134 */     localStringBuffer.append(this.type.getName());
/* 135 */     localStringBuffer.append('(');
/* 136 */     int i = 1;
/* 137 */     for (Map.Entry localEntry : this.memberValues.entrySet()) {
/* 138 */       if (i != 0)
/* 139 */         i = 0;
/*     */       else {
/* 141 */         localStringBuffer.append(", ");
/*     */       }
/* 143 */       localStringBuffer.append((String)localEntry.getKey());
/* 144 */       localStringBuffer.append('=');
/* 145 */       localStringBuffer.append(memberValueToString(localEntry.getValue()));
/*     */     }
/* 147 */     localStringBuffer.append(')');
/* 148 */     return localStringBuffer.toString();
/*     */   }
/*     */ 
/*     */   private static String memberValueToString(Object paramObject)
/*     */   {
/* 155 */     Class localClass = paramObject.getClass();
/* 156 */     if (!localClass.isArray())
/*     */     {
/* 158 */       return paramObject.toString();
/*     */     }
/* 160 */     if (localClass == [B.class)
/* 161 */       return Arrays.toString((byte[])paramObject);
/* 162 */     if (localClass == [C.class)
/* 163 */       return Arrays.toString((char[])paramObject);
/* 164 */     if (localClass == [D.class)
/* 165 */       return Arrays.toString((double[])paramObject);
/* 166 */     if (localClass == [F.class)
/* 167 */       return Arrays.toString((float[])paramObject);
/* 168 */     if (localClass == [I.class)
/* 169 */       return Arrays.toString((int[])paramObject);
/* 170 */     if (localClass == [J.class)
/* 171 */       return Arrays.toString((long[])paramObject);
/* 172 */     if (localClass == [S.class)
/* 173 */       return Arrays.toString((short[])paramObject);
/* 174 */     if (localClass == [Z.class)
/* 175 */       return Arrays.toString((boolean[])paramObject);
/* 176 */     return Arrays.toString((Object[])paramObject);
/*     */   }
/*     */ 
/*     */   private Boolean equalsImpl(Object paramObject)
/*     */   {
/* 183 */     if (paramObject == this) {
/* 184 */       return Boolean.valueOf(true);
/*     */     }
/* 186 */     if (!this.type.isInstance(paramObject))
/* 187 */       return Boolean.valueOf(false);
/* 188 */     for (Method localMethod : getMemberMethods()) {
/* 189 */       String str = localMethod.getName();
/* 190 */       Object localObject1 = this.memberValues.get(str);
/* 191 */       Object localObject2 = null;
/* 192 */       AnnotationInvocationHandler localAnnotationInvocationHandler = asOneOfUs(paramObject);
/* 193 */       if (localAnnotationInvocationHandler != null)
/* 194 */         localObject2 = localAnnotationInvocationHandler.memberValues.get(str);
/*     */       else {
/*     */         try {
/* 197 */           localObject2 = localMethod.invoke(paramObject, new Object[0]);
/*     */         } catch (InvocationTargetException localInvocationTargetException) {
/* 199 */           return Boolean.valueOf(false);
/*     */         } catch (IllegalAccessException localIllegalAccessException) {
/* 201 */           throw new AssertionError(localIllegalAccessException);
/*     */         }
/*     */       }
/* 204 */       if (!memberValueEquals(localObject1, localObject2))
/* 205 */         return Boolean.valueOf(false);
/*     */     }
/* 207 */     return Boolean.valueOf(true);
/*     */   }
/*     */ 
/*     */   private AnnotationInvocationHandler asOneOfUs(Object paramObject)
/*     */   {
/* 216 */     if (Proxy.isProxyClass(paramObject.getClass())) {
/* 217 */       InvocationHandler localInvocationHandler = Proxy.getInvocationHandler(paramObject);
/* 218 */       if ((localInvocationHandler instanceof AnnotationInvocationHandler))
/* 219 */         return (AnnotationInvocationHandler)localInvocationHandler;
/*     */     }
/* 221 */     return null;
/*     */   }
/*     */ 
/*     */   private static boolean memberValueEquals(Object paramObject1, Object paramObject2)
/*     */   {
/* 233 */     Class localClass = paramObject1.getClass();
/*     */ 
/* 237 */     if (!localClass.isArray()) {
/* 238 */       return paramObject1.equals(paramObject2);
/*     */     }
/*     */ 
/* 242 */     if (((paramObject1 instanceof Object[])) && ((paramObject2 instanceof Object[]))) {
/* 243 */       return Arrays.equals((Object[])paramObject1, (Object[])paramObject2);
/*     */     }
/*     */ 
/* 246 */     if (paramObject2.getClass() != localClass) {
/* 247 */       return false;
/*     */     }
/*     */ 
/* 250 */     if (localClass == [B.class)
/* 251 */       return Arrays.equals((byte[])paramObject1, (byte[])paramObject2);
/* 252 */     if (localClass == [C.class)
/* 253 */       return Arrays.equals((char[])paramObject1, (char[])paramObject2);
/* 254 */     if (localClass == [D.class)
/* 255 */       return Arrays.equals((double[])paramObject1, (double[])paramObject2);
/* 256 */     if (localClass == [F.class)
/* 257 */       return Arrays.equals((float[])paramObject1, (float[])paramObject2);
/* 258 */     if (localClass == [I.class)
/* 259 */       return Arrays.equals((int[])paramObject1, (int[])paramObject2);
/* 260 */     if (localClass == [J.class)
/* 261 */       return Arrays.equals((long[])paramObject1, (long[])paramObject2);
/* 262 */     if (localClass == [S.class)
/* 263 */       return Arrays.equals((short[])paramObject1, (short[])paramObject2);
/* 264 */     assert (localClass == [Z.class);
/* 265 */     return Arrays.equals((boolean[])paramObject1, (boolean[])paramObject2);
/*     */   }
/*     */ 
/*     */   private Method[] getMemberMethods()
/*     */   {
/* 275 */     if (this.memberMethods == null) {
/* 276 */       this.memberMethods = ((Method[])AccessController.doPrivileged(new PrivilegedAction()
/*     */       {
/*     */         public Method[] run() {
/* 279 */           Method[] arrayOfMethod = AnnotationInvocationHandler.this.type.getDeclaredMethods();
/* 280 */           AccessibleObject.setAccessible(arrayOfMethod, true);
/* 281 */           return arrayOfMethod;
/*     */         }
/*     */       }));
/*     */     }
/* 285 */     return this.memberMethods;
/*     */   }
/*     */ 
/*     */   private int hashCodeImpl()
/*     */   {
/* 293 */     int i = 0;
/* 294 */     for (Map.Entry localEntry : this.memberValues.entrySet()) {
/* 295 */       i += (127 * ((String)localEntry.getKey()).hashCode() ^ memberValueHashCode(localEntry.getValue()));
/*     */     }
/*     */ 
/* 298 */     return i;
/*     */   }
/*     */ 
/*     */   private static int memberValueHashCode(Object paramObject)
/*     */   {
/* 305 */     Class localClass = paramObject.getClass();
/* 306 */     if (!localClass.isArray())
/*     */     {
/* 308 */       return paramObject.hashCode();
/*     */     }
/* 310 */     if (localClass == [B.class)
/* 311 */       return Arrays.hashCode((byte[])paramObject);
/* 312 */     if (localClass == [C.class)
/* 313 */       return Arrays.hashCode((char[])paramObject);
/* 314 */     if (localClass == [D.class)
/* 315 */       return Arrays.hashCode((double[])paramObject);
/* 316 */     if (localClass == [F.class)
/* 317 */       return Arrays.hashCode((float[])paramObject);
/* 318 */     if (localClass == [I.class)
/* 319 */       return Arrays.hashCode((int[])paramObject);
/* 320 */     if (localClass == [J.class)
/* 321 */       return Arrays.hashCode((long[])paramObject);
/* 322 */     if (localClass == [S.class)
/* 323 */       return Arrays.hashCode((short[])paramObject);
/* 324 */     if (localClass == [Z.class)
/* 325 */       return Arrays.hashCode((boolean[])paramObject);
/* 326 */     return Arrays.hashCode((Object[])paramObject);
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException
/*     */   {
/* 331 */     paramObjectInputStream.defaultReadObject();
/*     */ 
/* 336 */     AnnotationType localAnnotationType = null;
/*     */     try {
/* 338 */       localAnnotationType = AnnotationType.getInstance(this.type);
/*     */     }
/*     */     catch (IllegalArgumentException localIllegalArgumentException) {
/* 341 */       return;
/*     */     }
/*     */ 
/* 344 */     Map localMap = localAnnotationType.memberTypes();
/*     */ 
/* 346 */     for (Map.Entry localEntry : this.memberValues.entrySet()) {
/* 347 */       String str = (String)localEntry.getKey();
/* 348 */       Class localClass = (Class)localMap.get(str);
/* 349 */       if (localClass != null) {
/* 350 */         Object localObject = localEntry.getValue();
/* 351 */         if ((!localClass.isInstance(localObject)) && (!(localObject instanceof ExceptionProxy)))
/*     */         {
/* 353 */           localEntry.setValue(new AnnotationTypeMismatchExceptionProxy(localObject.getClass() + "[" + localObject + "]").setMember((Method)localAnnotationType.members().get(str)));
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.reflect.annotation.AnnotationInvocationHandler
 * JD-Core Version:    0.6.2
 */