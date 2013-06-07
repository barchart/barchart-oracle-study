/*    */ package sun.awt.X11;
/*    */ 
/*    */ import sun.misc.Unsafe;
/*    */ import sun.util.logging.PlatformLogger;
/*    */ 
/*    */ public class XExtData extends XWrapperBase
/*    */ {
/*  9 */   private Unsafe unsafe = XlibWrapper.unsafe;
/*    */   private final boolean should_free_memory;
/*    */   long pData;
/*    */ 
/*    */   public static int getSize()
/*    */   {
/* 11 */     return 16; } 
/* 12 */   public int getDataSize() { return getSize(); }
/*    */ 
/*    */   public long getPData()
/*    */   {
/* 16 */     return this.pData;
/*    */   }
/*    */ 
/*    */   public XExtData(long paramLong) {
/* 20 */     log.finest("Creating");
/* 21 */     this.pData = paramLong;
/* 22 */     this.should_free_memory = false;
/*    */   }
/*    */ 
/*    */   public XExtData()
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
/* 40 */   public int get_number() { log.finest(""); return Native.getInt(this.pData + 0L); } 
/* 41 */   public void set_number(int paramInt) { log.finest(""); Native.putInt(this.pData + 0L, paramInt); } 
/* 42 */   public XExtData get_next(int paramInt) { log.finest(""); return Native.getLong(this.pData + 4L) != 0L ? new XExtData(Native.getLong(this.pData + 4L) + paramInt * 16) : null; } 
/* 43 */   public long get_next() { log.finest(""); return Native.getLong(this.pData + 4L); } 
/* 44 */   public void set_next(long paramLong) { log.finest(""); Native.putLong(this.pData + 4L, paramLong); } 
/* 45 */   public long get_free_private(int paramInt) { log.finest(""); return Native.getLong(this.pData + 8L) + paramInt * Native.getLongSize(); } 
/* 46 */   public long get_free_private() { log.finest(""); return Native.getLong(this.pData + 8L); } 
/* 47 */   public void set_free_private(long paramLong) { log.finest(""); Native.putLong(this.pData + 8L, paramLong); } 
/* 48 */   public long get_private_data(int paramInt) { log.finest(""); return Native.getLong(this.pData + 12L) + paramInt * Native.getLongSize(); } 
/* 49 */   public long get_private_data() { log.finest(""); return Native.getLong(this.pData + 12L); } 
/* 50 */   public void set_private_data(long paramLong) { log.finest(""); Native.putLong(this.pData + 12L, paramLong); }
/*    */ 
/*    */   String getName()
/*    */   {
/* 54 */     return "XExtData";
/*    */   }
/*    */ 
/*    */   String getFieldsAsString()
/*    */   {
/* 59 */     StringBuilder localStringBuilder = new StringBuilder(160);
/*    */ 
/* 61 */     localStringBuilder.append("number = ").append(get_number()).append(", ");
/* 62 */     localStringBuilder.append("next = ").append(get_next()).append(", ");
/* 63 */     localStringBuilder.append("free_private = ").append(get_free_private()).append(", ");
/* 64 */     localStringBuilder.append("private_data = ").append(get_private_data()).append(", ");
/* 65 */     return localStringBuilder.toString();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XExtData
 * JD-Core Version:    0.6.2
 */