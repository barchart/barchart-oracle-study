/*     */ package javax.naming;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Vector;
/*     */ 
/*     */ final class NameImplEnumerator
/*     */   implements Enumeration
/*     */ {
/*     */   Vector vector;
/*     */   int count;
/*     */   int limit;
/*     */ 
/*     */   NameImplEnumerator(Vector paramVector, int paramInt1, int paramInt2)
/*     */   {
/* 715 */     this.vector = paramVector;
/* 716 */     this.count = paramInt1;
/* 717 */     this.limit = paramInt2;
/*     */   }
/*     */ 
/*     */   public boolean hasMoreElements() {
/* 721 */     return this.count < this.limit;
/*     */   }
/*     */ 
/*     */   public Object nextElement() {
/* 725 */     if (this.count < this.limit) {
/* 726 */       return this.vector.elementAt(this.count++);
/*     */     }
/* 728 */     throw new NoSuchElementException("NameImplEnumerator");
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.naming.NameImplEnumerator
 * JD-Core Version:    0.6.2
 */