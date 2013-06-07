/*    */ package sun.nio.ch;
/*    */ 
/*    */ import sun.misc.Unsafe;
/*    */ 
/*    */ class AllocatedNativeObject extends NativeObject
/*    */ {
/*    */   AllocatedNativeObject(int paramInt, boolean paramBoolean)
/*    */   {
/* 53 */     super(paramInt, paramBoolean);
/*    */   }
/*    */ 
/*    */   synchronized void free()
/*    */   {
/* 60 */     if (this.allocationAddress != 0L) {
/* 61 */       unsafe.freeMemory(this.allocationAddress);
/* 62 */       this.allocationAddress = 0L;
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.AllocatedNativeObject
 * JD-Core Version:    0.6.2
 */