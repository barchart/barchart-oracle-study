/*     */ package sun.invoke.util;
/*     */ 
/*     */ import java.lang.invoke.MethodType;
/*     */ import sun.invoke.empty.Empty;
/*     */ 
/*     */ public class VerifyType
/*     */ {
/* 104 */   private static final Class<?> NULL_CLASS = localClass;
/*     */ 
/*     */   public static boolean isNullConversion(Class<?> paramClass1, Class<?> paramClass2)
/*     */   {
/*  49 */     if (paramClass1 == paramClass2) return true;
/*     */ 
/*  51 */     if (paramClass2.isInterface()) paramClass2 = Object.class;
/*  52 */     if (paramClass1.isInterface()) paramClass1 = Object.class;
/*  53 */     if (paramClass1 == paramClass2) return true;
/*  54 */     if (paramClass2 == Void.TYPE) return true;
/*  55 */     if (isNullType(paramClass1)) return !paramClass2.isPrimitive();
/*  56 */     if (!paramClass1.isPrimitive()) return paramClass2.isAssignableFrom(paramClass1);
/*  57 */     if (!paramClass2.isPrimitive()) return false;
/*     */ 
/*  59 */     Wrapper localWrapper1 = Wrapper.forPrimitiveType(paramClass1);
/*  60 */     if (paramClass2 == Integer.TYPE) return localWrapper1.isSubwordOrInt();
/*  61 */     Wrapper localWrapper2 = Wrapper.forPrimitiveType(paramClass2);
/*  62 */     if (!localWrapper1.isSubwordOrInt()) return false;
/*  63 */     if (!localWrapper2.isSubwordOrInt()) return false;
/*  64 */     if ((!localWrapper2.isSigned()) && (localWrapper1.isSigned())) return false;
/*  65 */     return localWrapper2.bitWidth() > localWrapper1.bitWidth();
/*     */   }
/*     */ 
/*     */   public static boolean isNullReferenceConversion(Class<?> paramClass1, Class<?> paramClass2)
/*     */   {
/*  76 */     assert (!paramClass2.isPrimitive());
/*  77 */     if (paramClass2.isInterface()) return true;
/*  78 */     if (isNullType(paramClass1)) return true;
/*  79 */     return paramClass2.isAssignableFrom(paramClass1);
/*     */   }
/*     */ 
/*     */   public static boolean isNullType(Class<?> paramClass)
/*     */   {
/*  86 */     if (paramClass == null) return false;
/*  87 */     return (paramClass == NULL_CLASS) || (paramClass == Empty.class);
/*     */   }
/*     */ 
/*     */   public static boolean isNullConversion(MethodType paramMethodType1, MethodType paramMethodType2)
/*     */   {
/* 116 */     if (paramMethodType1 == paramMethodType2) return true;
/* 117 */     int i = paramMethodType1.parameterCount();
/* 118 */     if (i != paramMethodType2.parameterCount()) return false;
/* 119 */     for (int j = 0; j < i; j++)
/* 120 */       if (!isNullConversion(paramMethodType1.parameterType(j), paramMethodType2.parameterType(j)))
/* 121 */         return false;
/* 122 */     return isNullConversion(paramMethodType2.returnType(), paramMethodType1.returnType());
/*     */   }
/*     */ 
/*     */   public static int canPassUnchecked(Class<?> paramClass1, Class<?> paramClass2)
/*     */   {
/* 142 */     if (paramClass1 == paramClass2) {
/* 143 */       return 1;
/*     */     }
/* 145 */     if (paramClass2.isPrimitive()) {
/* 146 */       if (paramClass2 == Void.TYPE)
/*     */       {
/* 151 */         return 1;
/* 152 */       }if (paramClass1 == Void.TYPE)
/* 153 */         return 0;
/* 154 */       if (!paramClass1.isPrimitive())
/*     */       {
/* 156 */         return 0;
/* 157 */       }Wrapper localWrapper1 = Wrapper.forPrimitiveType(paramClass1);
/* 158 */       Wrapper localWrapper2 = Wrapper.forPrimitiveType(paramClass2);
/* 159 */       if ((localWrapper1.isSubwordOrInt()) && (localWrapper2.isSubwordOrInt())) {
/* 160 */         if (localWrapper1.bitWidth() >= localWrapper2.bitWidth())
/* 161 */           return -1;
/* 162 */         if ((!localWrapper2.isSigned()) && (localWrapper1.isSigned()))
/* 163 */           return -1;
/* 164 */         return 1;
/*     */       }
/* 166 */       if ((paramClass1 == Float.TYPE) || (paramClass2 == Float.TYPE)) {
/* 167 */         if ((paramClass1 == Double.TYPE) || (paramClass2 == Double.TYPE)) {
/* 168 */           return -1;
/*     */         }
/* 170 */         return 0;
/*     */       }
/*     */ 
/* 173 */       return 0;
/*     */     }
/* 175 */     if (paramClass1.isPrimitive())
/*     */     {
/* 178 */       return 0;
/*     */     }
/*     */ 
/* 184 */     if (isNullReferenceConversion(paramClass1, paramClass2))
/*     */     {
/* 186 */       return 1;
/*     */     }
/* 188 */     return -1;
/*     */   }
/*     */ 
/*     */   public static int canPassRaw(Class<?> paramClass1, Class<?> paramClass2) {
/* 192 */     if (paramClass2.isPrimitive()) {
/* 193 */       if (paramClass2 == Void.TYPE)
/*     */       {
/* 195 */         return 1;
/* 196 */       }if (paramClass1 == Void.TYPE)
/*     */       {
/* 200 */         return paramClass2 == Integer.TYPE ? 1 : 0;
/* 201 */       }if (isNullType(paramClass1))
/*     */       {
/* 205 */         return 1;
/* 206 */       }if (!paramClass1.isPrimitive())
/* 207 */         return 0;
/* 208 */       Wrapper localWrapper1 = Wrapper.forPrimitiveType(paramClass1);
/* 209 */       Wrapper localWrapper2 = Wrapper.forPrimitiveType(paramClass2);
/* 210 */       if (localWrapper1.stackSlots() == localWrapper2.stackSlots())
/* 211 */         return 1;
/* 212 */       if ((localWrapper1.isSubwordOrInt()) && (localWrapper2 == Wrapper.VOID))
/* 213 */         return 1;
/* 214 */       return 0;
/* 215 */     }if (paramClass1.isPrimitive()) {
/* 216 */       return 0;
/*     */     }
/*     */ 
/* 220 */     if (isNullReferenceConversion(paramClass1, paramClass2))
/* 221 */       return 1;
/* 222 */     return -1;
/*     */   }
/*     */ 
/*     */   public static boolean isSpreadArgType(Class<?> paramClass) {
/* 226 */     return paramClass.isArray();
/*     */   }
/*     */   public static Class<?> spreadArgElementType(Class<?> paramClass, int paramInt) {
/* 229 */     return paramClass.getComponentType();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  98 */     Class localClass = null;
/*     */     try {
/* 100 */       localClass = Class.forName("java.lang.Null");
/*     */     }
/*     */     catch (ClassNotFoundException localClassNotFoundException)
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.invoke.util.VerifyType
 * JD-Core Version:    0.6.2
 */