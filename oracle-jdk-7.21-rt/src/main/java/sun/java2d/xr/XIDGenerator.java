/*    */ package sun.java2d.xr;
/*    */ 
/*    */ public class XIDGenerator
/*    */ {
/*    */   private static final int XID_BUFFER_SIZE = 512;
/* 39 */   int[] xidBuffer = new int[512];
/* 40 */   int currentIndex = 512;
/*    */ 
/*    */   public int getNextXID()
/*    */   {
/* 44 */     if (this.currentIndex >= 512) {
/* 45 */       bufferXIDs(this.xidBuffer, this.xidBuffer.length);
/* 46 */       this.currentIndex = 0;
/*    */     }
/*    */ 
/* 49 */     return this.xidBuffer[(this.currentIndex++)];
/*    */   }
/*    */ 
/*    */   private static native void bufferXIDs(int[] paramArrayOfInt, int paramInt);
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.xr.XIDGenerator
 * JD-Core Version:    0.6.2
 */