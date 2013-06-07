/*    */ package java.lang.management;
/*    */ 
/*    */ public enum MemoryType
/*    */ {
/* 43 */   HEAP("Heap memory"), 
/*    */ 
/* 56 */   NON_HEAP("Non-heap memory");
/*    */ 
/*    */   private final String description;
/*    */   private static final long serialVersionUID = 6992337162326171013L;
/*    */ 
/* 61 */   private MemoryType(String paramString) { this.description = paramString; }
/*    */ 
/*    */ 
/*    */   public String toString()
/*    */   {
/* 69 */     return this.description;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.management.MemoryType
 * JD-Core Version:    0.6.2
 */