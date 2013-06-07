/*     */ package sun.invoke.util;
/*     */ 
/*     */ import java.lang.reflect.Modifier;
/*     */ 
/*     */ public class VerifyAccess
/*     */ {
/*     */   private static final int PACKAGE_ONLY = 0;
/*     */   private static final int PACKAGE_ALLOWED = 8;
/*     */   private static final int PROTECTED_OR_PACKAGE_ALLOWED = 12;
/*     */   private static final int ALL_ACCESS_MODES = 7;
/*     */   private static final boolean ALLOW_NESTMATE_ACCESS = false;
/*     */ 
/*     */   public static boolean isMemberAccessible(Class<?> paramClass1, Class<?> paramClass2, int paramInt1, Class<?> paramClass3, int paramInt2)
/*     */   {
/*  89 */     if (paramInt2 == 0) return false;
/*  90 */     assert (((paramInt2 & 0x1) != 0) && ((paramInt2 & 0xFFFFFFF0) == 0));
/*     */ 
/*  93 */     if (paramClass1 != paramClass2) {
/*  94 */       if (!isClassAccessible(paramClass1, paramClass3, paramInt2))
/*     */       {
/*  96 */         return false;
/*     */       }
/*  98 */       if (((paramInt1 & 0xF) == 12) && ((paramInt2 & 0xC) != 0))
/*     */       {
/* 101 */         if (!isRelatedClass(paramClass1, paramClass3)) {
/* 102 */           return isSamePackage(paramClass2, paramClass3);
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 108 */     if ((paramClass2 == paramClass3) && ((paramInt2 & 0x2) != 0))
/*     */     {
/* 110 */       return true;
/* 111 */     }switch (paramInt1 & 0x7) {
/*     */     case 1:
/* 113 */       if (paramClass1 != paramClass2) return true;
/* 114 */       return isClassAccessible(paramClass1, paramClass3, paramInt2);
/*     */     case 4:
/* 116 */       if (((paramInt2 & 0xC) != 0) && (isSamePackage(paramClass2, paramClass3)))
/*     */       {
/* 118 */         return true;
/* 119 */       }if (((paramInt2 & 0x4) != 0) && (isPublicSuperClass(paramClass2, paramClass3)))
/*     */       {
/* 121 */         return true;
/* 122 */       }return false;
/*     */     case 0:
/* 124 */       return ((paramInt2 & 0x8) != 0) && (isSamePackage(paramClass2, paramClass3));
/*     */     case 2:
/* 128 */       return false;
/*     */     case 3:
/*     */     }
/*     */ 
/* 132 */     throw new IllegalArgumentException("bad modifiers: " + Modifier.toString(paramInt1));
/*     */   }
/*     */ 
/*     */   static boolean isRelatedClass(Class<?> paramClass1, Class<?> paramClass2)
/*     */   {
/* 137 */     return (paramClass1 == paramClass2) || (paramClass1.isAssignableFrom(paramClass2)) || (paramClass2.isAssignableFrom(paramClass1));
/*     */   }
/*     */ 
/*     */   static boolean isPublicSuperClass(Class<?> paramClass1, Class<?> paramClass2)
/*     */   {
/* 143 */     return (Modifier.isPublic(paramClass1.getModifiers())) && (paramClass1.isAssignableFrom(paramClass2));
/*     */   }
/*     */ 
/*     */   public static boolean isClassAccessible(Class<?> paramClass1, Class<?> paramClass2, int paramInt)
/*     */   {
/* 159 */     if (paramInt == 0) return false;
/* 160 */     assert (((paramInt & 0x1) != 0) && ((paramInt & 0xFFFFFFF0) == 0));
/*     */ 
/* 162 */     int i = paramClass1.getModifiers();
/* 163 */     if (Modifier.isPublic(i))
/* 164 */       return true;
/* 165 */     if (((paramInt & 0x8) != 0) && (isSamePackage(paramClass2, paramClass1)))
/*     */     {
/* 167 */       return true;
/* 168 */     }return false;
/*     */   }
/*     */ 
/*     */   public static boolean isSamePackage(Class<?> paramClass1, Class<?> paramClass2)
/*     */   {
/* 178 */     assert ((!paramClass1.isArray()) && (!paramClass2.isArray()));
/* 179 */     if (paramClass1 == paramClass2)
/* 180 */       return true;
/* 181 */     if (paramClass1.getClassLoader() != paramClass2.getClassLoader())
/* 182 */       return false;
/* 183 */     String str1 = paramClass1.getName(); String str2 = paramClass2.getName();
/* 184 */     int i = str1.lastIndexOf('.');
/* 185 */     if (i != str2.lastIndexOf('.'))
/* 186 */       return false;
/* 187 */     for (int j = 0; j < i; j++) {
/* 188 */       if (str1.charAt(j) != str2.charAt(j))
/* 189 */         return false;
/*     */     }
/* 191 */     return true;
/*     */   }
/*     */ 
/*     */   public static String getPackageName(Class<?> paramClass)
/*     */   {
/* 197 */     assert (!paramClass.isArray());
/* 198 */     String str = paramClass.getName();
/* 199 */     int i = str.lastIndexOf('.');
/* 200 */     if (i < 0) return "";
/* 201 */     return str.substring(0, i);
/*     */   }
/*     */ 
/*     */   public static boolean isSamePackageMember(Class<?> paramClass1, Class<?> paramClass2)
/*     */   {
/* 212 */     if (paramClass1 == paramClass2)
/* 213 */       return true;
/* 214 */     if (!isSamePackage(paramClass1, paramClass2))
/* 215 */       return false;
/* 216 */     if (getOutermostEnclosingClass(paramClass1) != getOutermostEnclosingClass(paramClass2))
/* 217 */       return false;
/* 218 */     return true;
/*     */   }
/*     */ 
/*     */   private static Class<?> getOutermostEnclosingClass(Class<?> paramClass) {
/* 222 */     Object localObject1 = paramClass;
/* 223 */     for (Object localObject2 = paramClass; (localObject2 = ((Class)localObject2).getEnclosingClass()) != null; )
/* 224 */       localObject1 = localObject2;
/* 225 */     return localObject1;
/*     */   }
/*     */ 
/*     */   private static boolean loadersAreRelated(ClassLoader paramClassLoader1, ClassLoader paramClassLoader2, boolean paramBoolean)
/*     */   {
/* 230 */     if ((paramClassLoader1 == paramClassLoader2) || (paramClassLoader1 == null) || ((paramClassLoader2 == null) && (!paramBoolean)))
/*     */     {
/* 232 */       return true;
/*     */     }
/* 234 */     for (ClassLoader localClassLoader = paramClassLoader2; 
/* 235 */       localClassLoader != null; localClassLoader = localClassLoader.getParent()) {
/* 236 */       if (localClassLoader == paramClassLoader1) return true;
/*     */     }
/* 238 */     if (paramBoolean) return false;
/*     */ 
/* 240 */     for (localClassLoader = paramClassLoader1; 
/* 241 */       localClassLoader != null; localClassLoader = localClassLoader.getParent()) {
/* 242 */       if (localClassLoader == paramClassLoader2) return true;
/*     */     }
/* 244 */     return false;
/*     */   }
/*     */ 
/*     */   public static boolean classLoaderIsAncestor(Class<?> paramClass1, Class<?> paramClass2)
/*     */   {
/* 255 */     return loadersAreRelated(paramClass1.getClassLoader(), paramClass2.getClassLoader(), true);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.invoke.util.VerifyAccess
 * JD-Core Version:    0.6.2
 */