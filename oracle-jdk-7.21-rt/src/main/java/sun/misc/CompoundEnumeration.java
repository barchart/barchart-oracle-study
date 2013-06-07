/*    */ package sun.misc;
/*    */ 
/*    */ import java.util.Enumeration;
/*    */ import java.util.NoSuchElementException;
/*    */ 
/*    */ public class CompoundEnumeration<E>
/*    */   implements Enumeration<E>
/*    */ {
/*    */   private Enumeration[] enums;
/* 37 */   private int index = 0;
/*    */ 
/*    */   public CompoundEnumeration(Enumeration[] paramArrayOfEnumeration) {
/* 40 */     this.enums = paramArrayOfEnumeration;
/*    */   }
/*    */ 
/*    */   private boolean next() {
/* 44 */     while (this.index < this.enums.length) {
/* 45 */       if ((this.enums[this.index] != null) && (this.enums[this.index].hasMoreElements())) {
/* 46 */         return true;
/*    */       }
/* 48 */       this.index += 1;
/*    */     }
/* 50 */     return false;
/*    */   }
/*    */ 
/*    */   public boolean hasMoreElements() {
/* 54 */     return next();
/*    */   }
/*    */ 
/*    */   public E nextElement() {
/* 58 */     if (!next()) {
/* 59 */       throw new NoSuchElementException();
/*    */     }
/* 61 */     return this.enums[this.index].nextElement();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.misc.CompoundEnumeration
 * JD-Core Version:    0.6.2
 */