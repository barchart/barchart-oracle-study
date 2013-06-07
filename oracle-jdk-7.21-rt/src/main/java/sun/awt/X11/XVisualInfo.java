/*    */ package sun.awt.X11;
/*    */ 
/*    */ import sun.misc.Unsafe;
/*    */ import sun.util.logging.PlatformLogger;
/*    */ 
/*    */ public class XVisualInfo extends XWrapperBase
/*    */ {
/*  9 */   private Unsafe unsafe = XlibWrapper.unsafe;
/*    */   private final boolean should_free_memory;
/*    */   long pData;
/*    */ 
/*    */   public static int getSize()
/*    */   {
/* 11 */     return 40; } 
/* 12 */   public int getDataSize() { return getSize(); }
/*    */ 
/*    */   public long getPData()
/*    */   {
/* 16 */     return this.pData;
/*    */   }
/*    */ 
/*    */   public XVisualInfo(long paramLong) {
/* 20 */     log.finest("Creating");
/* 21 */     this.pData = paramLong;
/* 22 */     this.should_free_memory = false;
/*    */   }
/*    */ 
/*    */   public XVisualInfo()
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
/* 40 */   public long get_visual(int paramInt) { log.finest(""); return Native.getLong(this.pData + 0L) + paramInt * Native.getLongSize(); } 
/* 41 */   public long get_visual() { log.finest(""); return Native.getLong(this.pData + 0L); } 
/* 42 */   public void set_visual(long paramLong) { log.finest(""); Native.putLong(this.pData + 0L, paramLong); } 
/* 43 */   public long get_visualid() { log.finest(""); return Native.getLong(this.pData + 4L); } 
/* 44 */   public void set_visualid(long paramLong) { log.finest(""); Native.putLong(this.pData + 4L, paramLong); } 
/* 45 */   public int get_screen() { log.finest(""); return Native.getInt(this.pData + 8L); } 
/* 46 */   public void set_screen(int paramInt) { log.finest(""); Native.putInt(this.pData + 8L, paramInt); } 
/* 47 */   public int get_depth() { log.finest(""); return Native.getInt(this.pData + 12L); } 
/* 48 */   public void set_depth(int paramInt) { log.finest(""); Native.putInt(this.pData + 12L, paramInt); } 
/* 49 */   public int get_class() { log.finest(""); return Native.getInt(this.pData + 16L); } 
/* 50 */   public void set_class(int paramInt) { log.finest(""); Native.putInt(this.pData + 16L, paramInt); } 
/* 51 */   public long get_red_mask() { log.finest(""); return Native.getLong(this.pData + 20L); } 
/* 52 */   public void set_red_mask(long paramLong) { log.finest(""); Native.putLong(this.pData + 20L, paramLong); } 
/* 53 */   public long get_green_mask() { log.finest(""); return Native.getLong(this.pData + 24L); } 
/* 54 */   public void set_green_mask(long paramLong) { log.finest(""); Native.putLong(this.pData + 24L, paramLong); } 
/* 55 */   public long get_blue_mask() { log.finest(""); return Native.getLong(this.pData + 28L); } 
/* 56 */   public void set_blue_mask(long paramLong) { log.finest(""); Native.putLong(this.pData + 28L, paramLong); } 
/* 57 */   public int get_colormap_size() { log.finest(""); return Native.getInt(this.pData + 32L); } 
/* 58 */   public void set_colormap_size(int paramInt) { log.finest(""); Native.putInt(this.pData + 32L, paramInt); } 
/* 59 */   public int get_bits_per_rgb() { log.finest(""); return Native.getInt(this.pData + 36L); } 
/* 60 */   public void set_bits_per_rgb(int paramInt) { log.finest(""); Native.putInt(this.pData + 36L, paramInt); }
/*    */ 
/*    */   String getName()
/*    */   {
/* 64 */     return "XVisualInfo";
/*    */   }
/*    */ 
/*    */   String getFieldsAsString()
/*    */   {
/* 69 */     StringBuilder localStringBuilder = new StringBuilder(400);
/*    */ 
/* 71 */     localStringBuilder.append("visual = ").append(get_visual()).append(", ");
/* 72 */     localStringBuilder.append("visualid = ").append(get_visualid()).append(", ");
/* 73 */     localStringBuilder.append("screen = ").append(get_screen()).append(", ");
/* 74 */     localStringBuilder.append("depth = ").append(get_depth()).append(", ");
/* 75 */     localStringBuilder.append("class = ").append(get_class()).append(", ");
/* 76 */     localStringBuilder.append("red_mask = ").append(get_red_mask()).append(", ");
/* 77 */     localStringBuilder.append("green_mask = ").append(get_green_mask()).append(", ");
/* 78 */     localStringBuilder.append("blue_mask = ").append(get_blue_mask()).append(", ");
/* 79 */     localStringBuilder.append("colormap_size = ").append(get_colormap_size()).append(", ");
/* 80 */     localStringBuilder.append("bits_per_rgb = ").append(get_bits_per_rgb()).append(", ");
/* 81 */     return localStringBuilder.toString();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XVisualInfo
 * JD-Core Version:    0.6.2
 */