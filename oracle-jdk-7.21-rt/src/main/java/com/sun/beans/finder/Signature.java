/*     */ package com.sun.beans.finder;
/*     */ 
/*     */ final class Signature
/*     */ {
/*     */   private final Class<?> type;
/*     */   private final String name;
/*     */   private final Class<?>[] args;
/*     */   private volatile int code;
/*     */ 
/*     */   Signature(Class<?> paramClass, Class<?>[] paramArrayOfClass)
/*     */   {
/*  49 */     this(paramClass, null, paramArrayOfClass);
/*     */   }
/*     */ 
/*     */   Signature(Class<?> paramClass, String paramString, Class<?>[] paramArrayOfClass)
/*     */   {
/*  60 */     this.type = paramClass;
/*  61 */     this.name = paramString;
/*  62 */     this.args = paramArrayOfClass;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/*  75 */     if (this == paramObject) {
/*  76 */       return true;
/*     */     }
/*  78 */     if ((paramObject instanceof Signature)) {
/*  79 */       Signature localSignature = (Signature)paramObject;
/*  80 */       return (isEqual(localSignature.type, this.type)) && (isEqual(localSignature.name, this.name)) && (isEqual(localSignature.args, this.args));
/*     */     }
/*     */ 
/*  84 */     return false;
/*     */   }
/*     */ 
/*     */   private static boolean isEqual(Object paramObject1, Object paramObject2)
/*     */   {
/*  97 */     return paramObject1 == null ? false : paramObject2 == null ? true : paramObject1.equals(paramObject2);
/*     */   }
/*     */ 
/*     */   private static boolean isEqual(Class<?>[] paramArrayOfClass1, Class<?>[] paramArrayOfClass2)
/*     */   {
/* 112 */     if ((paramArrayOfClass1 == null) || (paramArrayOfClass2 == null)) {
/* 113 */       return paramArrayOfClass1 == paramArrayOfClass2;
/*     */     }
/* 115 */     if (paramArrayOfClass1.length != paramArrayOfClass2.length) {
/* 116 */       return false;
/*     */     }
/* 118 */     for (int i = 0; i < paramArrayOfClass1.length; i++) {
/* 119 */       if (!isEqual(paramArrayOfClass1[i], paramArrayOfClass2[i])) {
/* 120 */         return false;
/*     */       }
/*     */     }
/* 123 */     return true;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 138 */     if (this.code == 0) {
/* 139 */       int i = 17;
/* 140 */       i = addHashCode(i, this.type);
/* 141 */       i = addHashCode(i, this.name);
/*     */ 
/* 143 */       if (this.args != null) {
/* 144 */         for (Class localClass : this.args) {
/* 145 */           i = addHashCode(i, localClass);
/*     */         }
/*     */       }
/* 148 */       this.code = i;
/*     */     }
/* 150 */     return this.code;
/*     */   }
/*     */ 
/*     */   private static int addHashCode(int paramInt, Object paramObject)
/*     */   {
/* 164 */     paramInt *= 37;
/* 165 */     return paramObject != null ? paramInt + paramObject.hashCode() : paramInt;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.beans.finder.Signature
 * JD-Core Version:    0.6.2
 */