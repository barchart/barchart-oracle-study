/*    */ package sun.awt.X11;
/*    */ 
/*    */ import sun.misc.Unsafe;
/*    */ import sun.util.logging.PlatformLogger;
/*    */ 
/*    */ public class Visual extends XWrapperBase
/*    */ {
/*  9 */   private Unsafe unsafe = XlibWrapper.unsafe;
/*    */   private final boolean should_free_memory;
/*    */   long pData;
/*    */ 
/*    */   public static int getSize()
/*    */   {
/* 11 */     return 32; } 
/* 12 */   public int getDataSize() { return getSize(); }
/*    */ 
/*    */   public long getPData()
/*    */   {
/* 16 */     return this.pData;
/*    */   }
/*    */ 
/*    */   public Visual(long paramLong) {
/* 20 */     log.finest("Creating");
/* 21 */     this.pData = paramLong;
/* 22 */     this.should_free_memory = false;
/*    */   }
/*    */ 
/*    */   public Visual()
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
/* 40 */   public XExtData get_ext_data(int paramInt) { log.finest(""); return Native.getLong(this.pData + 0L) != 0L ? new XExtData(Native.getLong(this.pData + 0L) + paramInt * 16) : null; } 
/* 41 */   public long get_ext_data() { log.finest(""); return Native.getLong(this.pData + 0L); } 
/* 42 */   public void set_ext_data(long paramLong) { log.finest(""); Native.putLong(this.pData + 0L, paramLong); } 
/* 43 */   public long get_visualid() { log.finest(""); return Native.getLong(this.pData + 4L); } 
/* 44 */   public void set_visualid(long paramLong) { log.finest(""); Native.putLong(this.pData + 4L, paramLong); } 
/* 45 */   public int get_class() { log.finest(""); return Native.getInt(this.pData + 8L); } 
/* 46 */   public void set_class(int paramInt) { log.finest(""); Native.putInt(this.pData + 8L, paramInt); } 
/* 47 */   public long get_red_mask() { log.finest(""); return Native.getLong(this.pData + 12L); } 
/* 48 */   public void set_red_mask(long paramLong) { log.finest(""); Native.putLong(this.pData + 12L, paramLong); } 
/* 49 */   public long get_green_mask() { log.finest(""); return Native.getLong(this.pData + 16L); } 
/* 50 */   public void set_green_mask(long paramLong) { log.finest(""); Native.putLong(this.pData + 16L, paramLong); } 
/* 51 */   public long get_blue_mask() { log.finest(""); return Native.getLong(this.pData + 20L); } 
/* 52 */   public void set_blue_mask(long paramLong) { log.finest(""); Native.putLong(this.pData + 20L, paramLong); } 
/* 53 */   public int get_bits_per_rgb() { log.finest(""); return Native.getInt(this.pData + 24L); } 
/* 54 */   public void set_bits_per_rgb(int paramInt) { log.finest(""); Native.putInt(this.pData + 24L, paramInt); } 
/* 55 */   public int get_map_entries() { log.finest(""); return Native.getInt(this.pData + 28L); } 
/* 56 */   public void set_map_entries(int paramInt) { log.finest(""); Native.putInt(this.pData + 28L, paramInt); }
/*    */ 
/*    */   String getName()
/*    */   {
/* 60 */     return "Visual";
/*    */   }
/*    */ 
/*    */   String getFieldsAsString()
/*    */   {
/* 65 */     StringBuilder localStringBuilder = new StringBuilder(320);
/*    */ 
/* 67 */     localStringBuilder.append("ext_data = ").append(get_ext_data()).append(", ");
/* 68 */     localStringBuilder.append("visualid = ").append(get_visualid()).append(", ");
/* 69 */     localStringBuilder.append("class = ").append(get_class()).append(", ");
/* 70 */     localStringBuilder.append("red_mask = ").append(get_red_mask()).append(", ");
/* 71 */     localStringBuilder.append("green_mask = ").append(get_green_mask()).append(", ");
/* 72 */     localStringBuilder.append("blue_mask = ").append(get_blue_mask()).append(", ");
/* 73 */     localStringBuilder.append("bits_per_rgb = ").append(get_bits_per_rgb()).append(", ");
/* 74 */     localStringBuilder.append("map_entries = ").append(get_map_entries()).append(", ");
/* 75 */     return localStringBuilder.toString();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.Visual
 * JD-Core Version:    0.6.2
 */