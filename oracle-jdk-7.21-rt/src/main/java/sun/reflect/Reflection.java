/*     */ package sun.reflect;
/*     */ 
/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Member;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class Reflection
/*     */ {
/*  50 */   private static volatile Map<Class, String[]> fieldFilterMap = localHashMap;
/*     */ 
/*  52 */   private static volatile Map<Class, String[]> methodFilterMap = new HashMap();
/*     */ 
/*     */   public static native Class getCallerClass(int paramInt);
/*     */ 
/*     */   private static native int getClassAccessFlags(Class paramClass);
/*     */ 
/*     */   public static boolean quickCheckMemberAccess(Class paramClass, int paramInt)
/*     */   {
/*  81 */     return Modifier.isPublic(getClassAccessFlags(paramClass) & paramInt);
/*     */   }
/*     */ 
/*     */   public static void ensureMemberAccess(Class paramClass1, Class paramClass2, Object paramObject, int paramInt)
/*     */     throws IllegalAccessException
/*     */   {
/*  90 */     if ((paramClass1 == null) || (paramClass2 == null)) {
/*  91 */       throw new InternalError();
/*     */     }
/*     */ 
/*  94 */     if (!verifyMemberAccess(paramClass1, paramClass2, paramObject, paramInt))
/*  95 */       throw new IllegalAccessException("Class " + paramClass1.getName() + " can not access a member of class " + paramClass2.getName() + " with modifiers \"" + Modifier.toString(paramInt) + "\"");
/*     */   }
/*     */ 
/*     */   public static boolean verifyMemberAccess(Class paramClass1, Class paramClass2, Object paramObject, int paramInt)
/*     */   {
/* 116 */     int i = 0;
/* 117 */     boolean bool = false;
/*     */ 
/* 119 */     if (paramClass1 == paramClass2)
/*     */     {
/* 121 */       return true;
/*     */     }
/*     */ 
/* 124 */     if (!Modifier.isPublic(getClassAccessFlags(paramClass2))) {
/* 125 */       bool = isSameClassPackage(paramClass1, paramClass2);
/* 126 */       i = 1;
/* 127 */       if (!bool) {
/* 128 */         return false;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 134 */     if (Modifier.isPublic(paramInt)) {
/* 135 */       return true;
/*     */     }
/*     */ 
/* 138 */     int j = 0;
/*     */ 
/* 140 */     if (Modifier.isProtected(paramInt))
/*     */     {
/* 142 */       if (isSubclassOf(paramClass1, paramClass2)) {
/* 143 */         j = 1;
/*     */       }
/*     */     }
/*     */ 
/* 147 */     if ((j == 0) && (!Modifier.isPrivate(paramInt))) {
/* 148 */       if (i == 0) {
/* 149 */         bool = isSameClassPackage(paramClass1, paramClass2);
/*     */ 
/* 151 */         i = 1;
/*     */       }
/*     */ 
/* 154 */       if (bool) {
/* 155 */         j = 1;
/*     */       }
/*     */     }
/*     */ 
/* 159 */     if (j == 0) {
/* 160 */       return false;
/*     */     }
/*     */ 
/* 163 */     if (Modifier.isProtected(paramInt))
/*     */     {
/* 165 */       Class localClass = paramObject == null ? paramClass2 : paramObject.getClass();
/* 166 */       if (localClass != paramClass1) {
/* 167 */         if (i == 0) {
/* 168 */           bool = isSameClassPackage(paramClass1, paramClass2);
/* 169 */           i = 1;
/*     */         }
/* 171 */         if ((!bool) && 
/* 172 */           (!isSubclassOf(localClass, paramClass1))) {
/* 173 */           return false;
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 179 */     return true;
/*     */   }
/*     */ 
/*     */   private static boolean isSameClassPackage(Class paramClass1, Class paramClass2) {
/* 183 */     return isSameClassPackage(paramClass1.getClassLoader(), paramClass1.getName(), paramClass2.getClassLoader(), paramClass2.getName());
/*     */   }
/*     */ 
/*     */   private static boolean isSameClassPackage(ClassLoader paramClassLoader1, String paramString1, ClassLoader paramClassLoader2, String paramString2)
/*     */   {
/* 192 */     if (paramClassLoader1 != paramClassLoader2) {
/* 193 */       return false;
/*     */     }
/* 195 */     int i = paramString1.lastIndexOf('.');
/* 196 */     int j = paramString2.lastIndexOf('.');
/* 197 */     if ((i == -1) || (j == -1))
/*     */     {
/* 200 */       return i == j;
/*     */     }
/* 202 */     int k = 0;
/* 203 */     int m = 0;
/*     */ 
/* 206 */     if (paramString1.charAt(k) == '[') {
/*     */       do
/* 208 */         k++;
/* 209 */       while (paramString1.charAt(k) == '[');
/* 210 */       if (paramString1.charAt(k) != 'L')
/*     */       {
/* 212 */         throw new InternalError("Illegal class name " + paramString1);
/*     */       }
/*     */     }
/* 215 */     if (paramString2.charAt(m) == '[') {
/*     */       do
/* 217 */         m++;
/* 218 */       while (paramString2.charAt(m) == '[');
/* 219 */       if (paramString2.charAt(m) != 'L')
/*     */       {
/* 221 */         throw new InternalError("Illegal class name " + paramString2);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 226 */     int n = i - k;
/* 227 */     int i1 = j - m;
/*     */ 
/* 229 */     if (n != i1) {
/* 230 */       return false;
/*     */     }
/* 232 */     return paramString1.regionMatches(false, k, paramString2, m, n);
/*     */   }
/*     */ 
/*     */   static boolean isSubclassOf(Class paramClass1, Class paramClass2)
/*     */   {
/* 240 */     while (paramClass1 != null) {
/* 241 */       if (paramClass1 == paramClass2) {
/* 242 */         return true;
/*     */       }
/* 244 */       paramClass1 = paramClass1.getSuperclass();
/*     */     }
/* 246 */     return false;
/*     */   }
/*     */ 
/*     */   public static synchronized void registerFieldsToFilter(Class paramClass, String[] paramArrayOfString)
/*     */   {
/* 252 */     fieldFilterMap = registerFilter(fieldFilterMap, paramClass, paramArrayOfString);
/*     */   }
/*     */ 
/*     */   public static synchronized void registerMethodsToFilter(Class paramClass, String[] paramArrayOfString)
/*     */   {
/* 259 */     methodFilterMap = registerFilter(methodFilterMap, paramClass, paramArrayOfString);
/*     */   }
/*     */ 
/*     */   private static Map<Class, String[]> registerFilter(Map<Class, String[]> paramMap, Class paramClass, String[] paramArrayOfString)
/*     */   {
/* 265 */     if (paramMap.get(paramClass) != null) {
/* 266 */       throw new IllegalArgumentException("Filter already registered: " + paramClass);
/*     */     }
/*     */ 
/* 269 */     paramMap = new HashMap(paramMap);
/* 270 */     paramMap.put(paramClass, paramArrayOfString);
/* 271 */     return paramMap;
/*     */   }
/*     */ 
/*     */   public static Field[] filterFields(Class paramClass, Field[] paramArrayOfField)
/*     */   {
/* 276 */     if (fieldFilterMap == null)
/*     */     {
/* 278 */       return paramArrayOfField;
/*     */     }
/* 280 */     return (Field[])filter(paramArrayOfField, (String[])fieldFilterMap.get(paramClass));
/*     */   }
/*     */ 
/*     */   public static Method[] filterMethods(Class paramClass, Method[] paramArrayOfMethod) {
/* 284 */     if (methodFilterMap == null)
/*     */     {
/* 286 */       return paramArrayOfMethod;
/*     */     }
/* 288 */     return (Method[])filter(paramArrayOfMethod, (String[])methodFilterMap.get(paramClass));
/*     */   }
/*     */ 
/*     */   private static Member[] filter(Member[] paramArrayOfMember, String[] paramArrayOfString) {
/* 292 */     if ((paramArrayOfString == null) || (paramArrayOfMember.length == 0)) {
/* 293 */       return paramArrayOfMember;
/*     */     }
/* 295 */     int i = 0;
/*     */     int n;
/* 296 */     for (Member localMember : paramArrayOfMember) {
/* 297 */       n = 0;
/* 298 */       for (Object localObject2 : paramArrayOfString) {
/* 299 */         if (localMember.getName() == localObject2) {
/* 300 */           n = 1;
/* 301 */           break;
/*     */         }
/*     */       }
/* 304 */       if (n == 0) {
/* 305 */         i++;
/*     */       }
/*     */     }
/* 308 */     ??? = (Member[])Array.newInstance(paramArrayOfMember[0].getClass(), i);
/*     */ 
/* 310 */     ??? = 0;
/* 311 */     for (??? : paramArrayOfMember) {
/* 312 */       ??? = 0;
/* 313 */       for (String str : paramArrayOfString) {
/* 314 */         if (((Member)???).getName() == str) {
/* 315 */           ??? = 1;
/* 316 */           break;
/*     */         }
/*     */       }
/* 319 */       if (??? == 0) {
/* 320 */         ???[(???++)] = ???;
/*     */       }
/*     */     }
/* 323 */     return ???;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  46 */     HashMap localHashMap = new HashMap();
/*  47 */     localHashMap.put(Reflection.class, new String[] { "fieldFilterMap", "methodFilterMap" });
/*     */ 
/*  49 */     localHashMap.put(System.class, new String[] { "security" });
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.reflect.Reflection
 * JD-Core Version:    0.6.2
 */