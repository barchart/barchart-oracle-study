/*     */ package com.sun.beans.decoder;
/*     */ 
/*     */ import java.lang.reflect.Array;
/*     */ 
/*     */ final class ArrayElementHandler extends NewElementHandler
/*     */ {
/*     */   private Integer length;
/*     */ 
/*     */   public void addAttribute(String paramString1, String paramString2)
/*     */   {
/*  94 */     if (paramString1.equals("length"))
/*  95 */       this.length = Integer.valueOf(paramString2);
/*     */     else
/*  97 */       super.addAttribute(paramString1, paramString2);
/*     */   }
/*     */ 
/*     */   public void startElement()
/*     */   {
/* 107 */     if (this.length != null)
/* 108 */       getValueObject();
/*     */   }
/*     */ 
/*     */   protected ValueObject getValueObject(Class<?> paramClass, Object[] paramArrayOfObject)
/*     */   {
/* 121 */     if (paramClass == null) {
/* 122 */       paramClass = Object.class;
/*     */     }
/* 124 */     if (this.length != null) {
/* 125 */       return ValueObjectImpl.create(Array.newInstance(paramClass, this.length.intValue()));
/*     */     }
/* 127 */     Object localObject = Array.newInstance(paramClass, paramArrayOfObject.length);
/* 128 */     for (int i = 0; i < paramArrayOfObject.length; i++) {
/* 129 */       Array.set(localObject, i, paramArrayOfObject[i]);
/*     */     }
/* 131 */     return ValueObjectImpl.create(localObject);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.beans.decoder.ArrayElementHandler
 * JD-Core Version:    0.6.2
 */