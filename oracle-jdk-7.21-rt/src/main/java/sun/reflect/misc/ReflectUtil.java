/*     */ package sun.reflect.misc;
/*     */ 
/*     */ import java.lang.reflect.Modifier;
/*     */ import sun.reflect.Reflection;
/*     */ 
/*     */ public final class ReflectUtil
/*     */ {
/*     */   public static final String PROXY_PACKAGE = "com.sun.proxy";
/*     */ 
/*     */   public static Class forName(String paramString)
/*     */     throws ClassNotFoundException
/*     */   {
/*  39 */     checkPackageAccess(paramString);
/*  40 */     return Class.forName(paramString);
/*     */   }
/*     */ 
/*     */   public static Object newInstance(Class paramClass) throws InstantiationException, IllegalAccessException
/*     */   {
/*  45 */     checkPackageAccess(paramClass);
/*  46 */     return paramClass.newInstance();
/*     */   }
/*     */ 
/*     */   public static void ensureMemberAccess(Class paramClass1, Class paramClass2, Object paramObject, int paramInt)
/*     */     throws IllegalAccessException
/*     */   {
/*  59 */     if ((paramObject == null) && (Modifier.isProtected(paramInt))) {
/*  60 */       int i = paramInt;
/*  61 */       i &= -5;
/*  62 */       i |= 1;
/*     */ 
/*  67 */       Reflection.ensureMemberAccess(paramClass1, paramClass2, paramObject, i);
/*     */       try
/*     */       {
/*  76 */         i &= -2;
/*  77 */         Reflection.ensureMemberAccess(paramClass1, paramClass2, paramObject, i);
/*     */ 
/*  85 */         return;
/*     */       }
/*     */       catch (IllegalAccessException localIllegalAccessException)
/*     */       {
/*  91 */         if (isSubclassOf(paramClass1, paramClass2)) {
/*  92 */           return;
/*     */         }
/*  94 */         throw localIllegalAccessException;
/*     */       }
/*     */     }
/*     */ 
/*  98 */     Reflection.ensureMemberAccess(paramClass1, paramClass2, paramObject, paramInt);
/*     */   }
/*     */ 
/*     */   private static boolean isSubclassOf(Class paramClass1, Class paramClass2)
/*     */   {
/* 108 */     while (paramClass1 != null) {
/* 109 */       if (paramClass1 == paramClass2) {
/* 110 */         return true;
/*     */       }
/* 112 */       paramClass1 = paramClass1.getSuperclass();
/*     */     }
/* 114 */     return false;
/*     */   }
/*     */ 
/*     */   public static void checkPackageAccess(Class paramClass)
/*     */   {
/* 119 */     checkPackageAccess(paramClass.getName());
/*     */   }
/*     */ 
/*     */   public static void checkPackageAccess(String paramString) {
/* 123 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 124 */     if (localSecurityManager != null) {
/* 125 */       String str = paramString.replace('/', '.');
/* 126 */       if (str.startsWith("[")) {
/* 127 */         i = str.lastIndexOf('[') + 2;
/* 128 */         if ((i > 1) && (i < str.length())) {
/* 129 */           str = str.substring(i);
/*     */         }
/*     */       }
/* 132 */       int i = str.lastIndexOf('.');
/* 133 */       if (i != -1)
/* 134 */         localSecurityManager.checkPackageAccess(str.substring(0, i));
/*     */     }
/*     */   }
/*     */ 
/*     */   public static boolean isPackageAccessible(Class paramClass)
/*     */   {
/*     */     try {
/* 141 */       checkPackageAccess(paramClass);
/*     */     } catch (SecurityException localSecurityException) {
/* 143 */       return false;
/*     */     }
/* 145 */     return true;
/*     */   }
/*     */ 
/*     */   private static boolean isAncestor(ClassLoader paramClassLoader1, ClassLoader paramClassLoader2)
/*     */   {
/* 151 */     ClassLoader localClassLoader = paramClassLoader2;
/*     */     do {
/* 153 */       localClassLoader = localClassLoader.getParent();
/* 154 */       if (paramClassLoader1 == localClassLoader)
/* 155 */         return true;
/*     */     }
/* 157 */     while (localClassLoader != null);
/* 158 */     return false;
/*     */   }
/*     */ 
/*     */   public static boolean needsPackageAccessCheck(ClassLoader paramClassLoader1, ClassLoader paramClassLoader2)
/*     */   {
/* 173 */     if ((paramClassLoader1 == null) || (paramClassLoader1 == paramClassLoader2)) {
/* 174 */       return false;
/*     */     }
/* 176 */     if (paramClassLoader2 == null) {
/* 177 */       return true;
/*     */     }
/* 179 */     return !isAncestor(paramClassLoader1, paramClassLoader2);
/*     */   }
/*     */ 
/*     */   public static void checkProxyPackageAccess(ClassLoader paramClassLoader, Class<?>[] paramArrayOfClass)
/*     */   {
/* 194 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 195 */     if (localSecurityManager != null)
/* 196 */       for (Class<?> localClass : paramArrayOfClass) {
/* 197 */         ClassLoader localClassLoader = localClass.getClassLoader();
/* 198 */         if (needsPackageAccessCheck(paramClassLoader, localClassLoader))
/* 199 */           checkPackageAccess(localClass);
/*     */       }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.reflect.misc.ReflectUtil
 * JD-Core Version:    0.6.2
 */