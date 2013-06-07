/*    */ package sun.font;
/*    */ 
/*    */ public class CreatedFontTracker
/*    */ {
/*    */   public static final int MAX_FILE_SIZE = 33554432;
/*    */   public static final int MAX_TOTAL_BYTES = 335544320;
/*    */   static int numBytes;
/*    */   static CreatedFontTracker tracker;
/*    */ 
/*    */   public static synchronized CreatedFontTracker getTracker()
/*    */   {
/* 37 */     if (tracker == null) {
/* 38 */       tracker = new CreatedFontTracker();
/*    */     }
/* 40 */     return tracker;
/*    */   }
/*    */ 
/*    */   public synchronized int getNumBytes() {
/* 44 */     return numBytes;
/*    */   }
/*    */ 
/*    */   public synchronized void addBytes(int paramInt) {
/* 48 */     numBytes += paramInt;
/*    */   }
/*    */ 
/*    */   public synchronized void subBytes(int paramInt) {
/* 52 */     numBytes -= paramInt;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.font.CreatedFontTracker
 * JD-Core Version:    0.6.2
 */