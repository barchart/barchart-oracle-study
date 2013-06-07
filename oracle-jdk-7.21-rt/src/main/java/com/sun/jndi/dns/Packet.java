/*     */ package com.sun.jndi.dns;
/*     */ 
/*     */ class Packet
/*     */ {
/*     */   byte[] buf;
/*     */ 
/*     */   Packet(int paramInt)
/*     */   {
/* 660 */     this.buf = new byte[paramInt];
/*     */   }
/*     */ 
/*     */   Packet(byte[] paramArrayOfByte, int paramInt) {
/* 664 */     this.buf = new byte[paramInt];
/* 665 */     System.arraycopy(paramArrayOfByte, 0, this.buf, 0, paramInt);
/*     */   }
/*     */ 
/*     */   void putInt(int paramInt1, int paramInt2) {
/* 669 */     this.buf[(paramInt2 + 0)] = ((byte)(paramInt1 >> 24));
/* 670 */     this.buf[(paramInt2 + 1)] = ((byte)(paramInt1 >> 16));
/* 671 */     this.buf[(paramInt2 + 2)] = ((byte)(paramInt1 >> 8));
/* 672 */     this.buf[(paramInt2 + 3)] = ((byte)paramInt1);
/*     */   }
/*     */ 
/*     */   void putShort(int paramInt1, int paramInt2) {
/* 676 */     this.buf[(paramInt2 + 0)] = ((byte)(paramInt1 >> 8));
/* 677 */     this.buf[(paramInt2 + 1)] = ((byte)paramInt1);
/*     */   }
/*     */ 
/*     */   void putByte(int paramInt1, int paramInt2) {
/* 681 */     this.buf[paramInt2] = ((byte)paramInt1);
/*     */   }
/*     */ 
/*     */   void putBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3) {
/* 685 */     System.arraycopy(paramArrayOfByte, paramInt1, this.buf, paramInt2, paramInt3);
/*     */   }
/*     */ 
/*     */   int length() {
/* 689 */     return this.buf.length;
/*     */   }
/*     */ 
/*     */   byte[] getData() {
/* 693 */     return this.buf;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jndi.dns.Packet
 * JD-Core Version:    0.6.2
 */