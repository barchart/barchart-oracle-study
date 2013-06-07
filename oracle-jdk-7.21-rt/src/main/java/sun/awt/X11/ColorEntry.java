/*    */ package sun.awt.X11;
/*    */ 
/*    */ import sun.misc.Unsafe;
/*    */ import sun.util.logging.PlatformLogger;
/*    */ 
/*    */ public class ColorEntry extends XWrapperBase
/*    */ {
/*  9 */   private Unsafe unsafe = XlibWrapper.unsafe;
/*    */   private final boolean should_free_memory;
/*    */   long pData;
/*    */ 
/*    */   public static int getSize()
/*    */   {
/* 11 */     return 4; } 
/* 12 */   public int getDataSize() { return getSize(); }
/*    */ 
/*    */   public long getPData()
/*    */   {
/* 16 */     return this.pData;
/*    */   }
/*    */ 
/*    */   public ColorEntry(long paramLong) {
/* 20 */     log.finest("Creating");
/* 21 */     this.pData = paramLong;
/* 22 */     this.should_free_memory = false;
/*    */   }
/*    */ 
/*    */   public ColorEntry()
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
/* 40 */   public byte get_r() { log.finest(""); return Native.getByte(this.pData + 0L); } 
/* 41 */   public void set_r(byte paramByte) { log.finest(""); Native.putByte(this.pData + 0L, paramByte); } 
/* 42 */   public byte get_g() { log.finest(""); return Native.getByte(this.pData + 1L); } 
/* 43 */   public void set_g(byte paramByte) { log.finest(""); Native.putByte(this.pData + 1L, paramByte); } 
/* 44 */   public byte get_b() { log.finest(""); return Native.getByte(this.pData + 2L); } 
/* 45 */   public void set_b(byte paramByte) { log.finest(""); Native.putByte(this.pData + 2L, paramByte); } 
/* 46 */   public byte get_flags() { log.finest(""); return Native.getByte(this.pData + 3L); } 
/* 47 */   public void set_flags(byte paramByte) { log.finest(""); Native.putByte(this.pData + 3L, paramByte); }
/*    */ 
/*    */   String getName()
/*    */   {
/* 51 */     return "ColorEntry";
/*    */   }
/*    */ 
/*    */   String getFieldsAsString()
/*    */   {
/* 56 */     StringBuilder localStringBuilder = new StringBuilder(160);
/*    */ 
/* 58 */     localStringBuilder.append("r = ").append(get_r()).append(", ");
/* 59 */     localStringBuilder.append("g = ").append(get_g()).append(", ");
/* 60 */     localStringBuilder.append("b = ").append(get_b()).append(", ");
/* 61 */     localStringBuilder.append("flags = ").append(get_flags()).append(", ");
/* 62 */     return localStringBuilder.toString();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.ColorEntry
 * JD-Core Version:    0.6.2
 */