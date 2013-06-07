/*     */ package com.sun.beans.finder;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ abstract class AbstractFinder<T>
/*     */ {
/*     */   private final Class<?>[] args;
/*     */ 
/*     */   protected AbstractFinder(Class<?>[] paramArrayOfClass)
/*     */   {
/*  52 */     this.args = paramArrayOfClass;
/*     */   }
/*     */ 
/*     */   protected abstract Class<?>[] getParameters(T paramT);
/*     */ 
/*     */   protected abstract boolean isVarArgs(T paramT);
/*     */ 
/*     */   protected abstract boolean isValid(T paramT);
/*     */ 
/*     */   final T find(T[] paramArrayOfT)
/*     */     throws NoSuchMethodException
/*     */   {
/* 104 */     HashMap localHashMap = new HashMap();
/*     */ 
/* 106 */     Object localObject1 = null;
/* 107 */     Object localObject2 = null;
/* 108 */     int i = 0;
/*     */     T ?;
/*     */     Class[] arrayOfClass1;
/* 110 */     for (? : paramArrayOfT) {
/* 111 */       if (isValid(?)) {
/* 112 */         arrayOfClass1 = getParameters(?);
/* 113 */         if (arrayOfClass1.length == this.args.length) {
/* 114 */           PrimitiveWrapperMap.replacePrimitivesWithWrappers(arrayOfClass1);
/* 115 */           if (isAssignable(arrayOfClass1, this.args)) {
/* 116 */             if (localObject1 == null) {
/* 117 */               localObject1 = ?;
/* 118 */               localObject2 = arrayOfClass1;
/*     */             } else {
/* 120 */               boolean bool1 = isAssignable(localObject2, arrayOfClass1);
/* 121 */               boolean bool3 = isAssignable(arrayOfClass1, localObject2);
/*     */ 
/* 123 */               if (bool3 == bool1) {
/* 124 */                 i = 1;
/* 125 */               } else if (bool1) {
/* 126 */                 localObject1 = ?;
/* 127 */                 localObject2 = arrayOfClass1;
/* 128 */                 i = 0;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/* 133 */         if (isVarArgs(?)) {
/* 134 */           int m = arrayOfClass1.length - 1;
/* 135 */           if (m <= this.args.length) {
/* 136 */             Class[] arrayOfClass2 = new Class[this.args.length];
/* 137 */             System.arraycopy(arrayOfClass1, 0, arrayOfClass2, 0, m);
/* 138 */             if (m < this.args.length) {
/* 139 */               Class localClass = arrayOfClass1[m].getComponentType();
/* 140 */               if (localClass.isPrimitive()) {
/* 141 */                 localClass = PrimitiveWrapperMap.getType(localClass.getName());
/*     */               }
/* 143 */               for (int n = m; n < this.args.length; n++) {
/* 144 */                 arrayOfClass2[n] = localClass;
/*     */               }
/*     */             }
/* 147 */             localHashMap.put(?, arrayOfClass2);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 152 */     for (? : paramArrayOfT) {
/* 153 */       arrayOfClass1 = (Class[])localHashMap.get(?);
/* 154 */       if ((arrayOfClass1 != null) && 
/* 155 */         (isAssignable(arrayOfClass1, this.args))) {
/* 156 */         if (localObject1 == null) {
/* 157 */           localObject1 = ?;
/* 158 */           localObject2 = arrayOfClass1;
/*     */         } else {
/* 160 */           boolean bool2 = isAssignable(localObject2, arrayOfClass1);
/* 161 */           boolean bool4 = isAssignable(arrayOfClass1, localObject2);
/*     */ 
/* 163 */           if (bool4 == bool2) {
/* 164 */             if (localObject2 == localHashMap.get(localObject1))
/* 165 */               i = 1;
/*     */           }
/* 167 */           else if (bool2) {
/* 168 */             localObject1 = ?;
/* 169 */             localObject2 = arrayOfClass1;
/* 170 */             i = 0;
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 177 */     if (i != 0) {
/* 178 */       throw new NoSuchMethodException("Ambiguous methods are found");
/*     */     }
/* 180 */     if (localObject1 == null) {
/* 181 */       throw new NoSuchMethodException("Method is not found");
/*     */     }
/* 183 */     return localObject1;
/*     */   }
/*     */ 
/*     */   private boolean isAssignable(Class<?>[] paramArrayOfClass1, Class<?>[] paramArrayOfClass2)
/*     */   {
/* 204 */     for (int i = 0; i < this.args.length; i++) {
/* 205 */       if ((null != this.args[i]) && 
/* 206 */         (!paramArrayOfClass1[i].isAssignableFrom(paramArrayOfClass2[i]))) {
/* 207 */         return false;
/*     */       }
/*     */     }
/*     */ 
/* 211 */     return true;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.beans.finder.AbstractFinder
 * JD-Core Version:    0.6.2
 */