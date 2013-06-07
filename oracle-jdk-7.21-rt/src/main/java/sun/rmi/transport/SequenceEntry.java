/*     */ package sun.rmi.transport;
/*     */ 
/*     */ class SequenceEntry
/*     */ {
/*     */   long sequenceNum;
/*     */   boolean keep;
/*     */ 
/*     */   SequenceEntry(long paramLong)
/*     */   {
/* 459 */     this.sequenceNum = paramLong;
/* 460 */     this.keep = false;
/*     */   }
/*     */ 
/*     */   void retain(long paramLong) {
/* 464 */     this.sequenceNum = paramLong;
/* 465 */     this.keep = true;
/*     */   }
/*     */ 
/*     */   void update(long paramLong) {
/* 469 */     this.sequenceNum = paramLong;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.rmi.transport.SequenceEntry
 * JD-Core Version:    0.6.2
 */