/*    */ package sun.awt.X11;
/*    */ 
/*    */ import sun.misc.Unsafe;
/*    */ import sun.util.logging.PlatformLogger;
/*    */ 
/*    */ public class Depth extends XWrapperBase
/*    */ {
/*  9 */   private Unsafe unsafe = XlibWrapper.unsafe;
/*    */   private final boolean should_free_memory;
/*    */   long pData;
/*    */ 
/*    */   public static int getSize()
/*    */   {
/* 11 */     return 12; } 
/* 12 */   public int getDataSize() { return getSize(); }
/*    */ 
/*    */   public long getPData()
/*    */   {
/* 16 */     return this.pData;
/*    */   }
/*    */ 
/*    */   public Depth(long paramLong) {
/* 20 */     log.finest("Creating");
/* 21 */     this.pData = paramLong;
/* 22 */     this.should_free_memory = false;
/*    */   }
/*    */ 
/*    */   public Depth()
/*    */   {
/* 27 */     log.finest("Creating");
/* 28 */     this.pData = this.unsafe.allocateMemory(getSize());
/* 29 */     this.should_free_memory = true;
/*    */   }
/*    */ 
/*    */   public void dispose()
/*    */   {
/* 34 */     log.finest("Disposing");
/* 35 */     if (this.should_free_memory) {
/* 36 */       log.finest("freeing memory");
/* 37 */       this.unsafe.freeMemory(this.pData);
/*    */     }
/*    */   }
/* 40 */   public int get_depth() { log.finest(""); return Native.getInt(this.pData + 0L); } 
/* 41 */   public void set_depth(int paramInt) { log.finest(""); Native.putInt(this.pData + 0L, paramInt); } 
/* 42 */   public int get_nvisuals() { log.finest(""); return Native.getInt(this.pData + 4L); } 
/* 43 */   public void set_nvisuals(int paramInt) { log.finest(""); Native.putInt(this.pData + 4L, paramInt); } 
/* 44 */   public Visual get_visuals(int paramInt) { log.finest(""); return Native.getLong(this.pData + 8L) != 0L ? new Visual(Native.getLong(this.pData + 8L) + paramInt * 32) : null; } 
/* 45 */   public long get_visuals() { log.finest(""); return Native.getLong(this.pData + 8L); } 
/* 46 */   public void set_visuals(long paramLong) { log.finest(""); Native.putLong(this.pData + 8L, paramLong); }
/*    */ 
/*    */   String getName()
/*    */   {
/* 50 */     return "Depth";
/*    */   }
/*    */ 
/*    */   String getFieldsAsString()
/*    */   {
/* 55 */     StringBuilder localStringBuilder = new StringBuilder(120);
/*    */ 
/* 57 */     localStringBuilder.append("depth = ").append(get_depth()).append(", ");
/* 58 */     localStringBuilder.append("nvisuals = ").append(get_nvisuals()).append(", ");
/* 59 */     localStringBuilder.append("visuals = ").append(get_visuals()).append(", ");
/* 60 */     return localStringBuilder.toString();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.Depth
 * JD-Core Version:    0.6.2
 */