/*    */ package javax.lang.model.element;
/*    */ 
/*    */ public enum NestingKind
/*    */ {
/* 85 */   TOP_LEVEL, 
/* 86 */   MEMBER, 
/* 87 */   LOCAL, 
/* 88 */   ANONYMOUS;
/*    */ 
/*    */   public boolean isNested()
/*    */   {
/* 97 */     return this != TOP_LEVEL;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.lang.model.element.NestingKind
 * JD-Core Version:    0.6.2
 */