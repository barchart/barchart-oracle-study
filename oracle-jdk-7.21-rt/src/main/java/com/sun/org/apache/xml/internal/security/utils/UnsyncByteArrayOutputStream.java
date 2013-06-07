/*    */ package com.sun.org.apache.xml.internal.security.utils;
/*    */ 
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ public class UnsyncByteArrayOutputStream extends OutputStream
/*    */ {
/*    */   private static final int INITIAL_SIZE = 8192;
/* 32 */   private static ThreadLocal bufCache = new ThreadLocal() {
/*    */     protected synchronized Object initialValue() {
/* 34 */       return new byte[8192];
/*    */     }
/* 32 */   };
/*    */   private byte[] buf;
/* 39 */   private int size = 8192;
/* 40 */   private int pos = 0;
/*    */ 
/*    */   public UnsyncByteArrayOutputStream() {
/* 43 */     this.buf = ((byte[])bufCache.get());
/*    */   }
/*    */ 
/*    */   public void write(byte[] paramArrayOfByte) {
/* 47 */     int i = this.pos + paramArrayOfByte.length;
/* 48 */     if (i > this.size) {
/* 49 */       expandSize(i);
/*    */     }
/* 51 */     System.arraycopy(paramArrayOfByte, 0, this.buf, this.pos, paramArrayOfByte.length);
/* 52 */     this.pos = i;
/*    */   }
/*    */ 
/*    */   public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
/* 56 */     int i = this.pos + paramInt2;
/* 57 */     if (i > this.size) {
/* 58 */       expandSize(i);
/*    */     }
/* 60 */     System.arraycopy(paramArrayOfByte, paramInt1, this.buf, this.pos, paramInt2);
/* 61 */     this.pos = i;
/*    */   }
/*    */ 
/*    */   public void write(int paramInt) {
/* 65 */     int i = this.pos + 1;
/* 66 */     if (i > this.size) {
/* 67 */       expandSize(i);
/*    */     }
/* 69 */     this.buf[(this.pos++)] = ((byte)paramInt);
/*    */   }
/*    */ 
/*    */   public byte[] toByteArray() {
/* 73 */     byte[] arrayOfByte = new byte[this.pos];
/* 74 */     System.arraycopy(this.buf, 0, arrayOfByte, 0, this.pos);
/* 75 */     return arrayOfByte;
/*    */   }
/*    */ 
/*    */   public void reset() {
/* 79 */     this.pos = 0;
/*    */   }
/*    */ 
/*    */   private void expandSize(int paramInt) {
/* 83 */     int i = this.size;
/* 84 */     while (paramInt > i) {
/* 85 */       i <<= 2;
/*    */     }
/* 87 */     byte[] arrayOfByte = new byte[i];
/* 88 */     System.arraycopy(this.buf, 0, arrayOfByte, 0, this.pos);
/* 89 */     this.buf = arrayOfByte;
/* 90 */     this.size = i;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.security.utils.UnsyncByteArrayOutputStream
 * JD-Core Version:    0.6.2
 */