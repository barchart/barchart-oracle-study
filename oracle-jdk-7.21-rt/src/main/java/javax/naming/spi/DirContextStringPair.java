/*     */ package javax.naming.spi;
/*     */ 
/*     */ import javax.naming.directory.DirContext;
/*     */ 
/*     */ class DirContextStringPair
/*     */ {
/*     */   DirContext ctx;
/*     */   String str;
/*     */ 
/*     */   DirContextStringPair(DirContext paramDirContext, String paramString)
/*     */   {
/* 322 */     this.ctx = paramDirContext;
/* 323 */     this.str = paramString;
/*     */   }
/*     */ 
/*     */   DirContext getDirContext() {
/* 327 */     return this.ctx;
/*     */   }
/*     */ 
/*     */   String getString() {
/* 331 */     return this.str;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.naming.spi.DirContextStringPair
 * JD-Core Version:    0.6.2
 */