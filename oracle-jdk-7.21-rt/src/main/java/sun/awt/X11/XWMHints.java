/*    */ package sun.awt.X11;
/*    */ 
/*    */ import sun.misc.Unsafe;
/*    */ import sun.util.logging.PlatformLogger;
/*    */ 
/*    */ public class XWMHints extends XWrapperBase
/*    */ {
/*  9 */   private Unsafe unsafe = XlibWrapper.unsafe;
/*    */   private final boolean should_free_memory;
/*    */   long pData;
/*    */ 
/*    */   public static int getSize()
/*    */   {
/* 11 */     return 36; } 
/* 12 */   public int getDataSize() { return getSize(); }
/*    */ 
/*    */   public long getPData()
/*    */   {
/* 16 */     return this.pData;
/*    */   }
/*    */ 
/*    */   public XWMHints(long paramLong) {
/* 20 */     log.finest("Creating");
/* 21 */     this.pData = paramLong;
/* 22 */     this.should_free_memory = false;
/*    */   }
/*    */ 
/*    */   public XWMHints()
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
/* 40 */   public long get_flags() { log.finest(""); return Native.getLong(this.pData + 0L); } 
/* 41 */   public void set_flags(long paramLong) { log.finest(""); Native.putLong(this.pData + 0L, paramLong); } 
/* 42 */   public int get_initial_state() { log.finest(""); return Native.getInt(this.pData + 8L); } 
/* 43 */   public void set_initial_state(int paramInt) { log.finest(""); Native.putInt(this.pData + 8L, paramInt); } 
/* 44 */   public long get_icon_pixmap(int paramInt) { log.finest(""); return Native.getLong(this.pData + 12L) + paramInt * Native.getLongSize(); } 
/* 45 */   public long get_icon_pixmap() { log.finest(""); return Native.getLong(this.pData + 12L); } 
/* 46 */   public void set_icon_pixmap(long paramLong) { log.finest(""); Native.putLong(this.pData + 12L, paramLong); } 
/* 47 */   public long get_icon_window() { log.finest(""); return Native.getLong(this.pData + 16L); } 
/* 48 */   public void set_icon_window(long paramLong) { log.finest(""); Native.putLong(this.pData + 16L, paramLong); } 
/* 49 */   public int get_icon_x() { log.finest(""); return Native.getInt(this.pData + 20L); } 
/* 50 */   public void set_icon_x(int paramInt) { log.finest(""); Native.putInt(this.pData + 20L, paramInt); } 
/* 51 */   public int get_icon_y() { log.finest(""); return Native.getInt(this.pData + 24L); } 
/* 52 */   public void set_icon_y(int paramInt) { log.finest(""); Native.putInt(this.pData + 24L, paramInt); } 
/* 53 */   public long get_icon_mask() { log.finest(""); return Native.getLong(this.pData + 28L); } 
/* 54 */   public void set_icon_mask(long paramLong) { log.finest(""); Native.putLong(this.pData + 28L, paramLong); } 
/* 55 */   public boolean get_input() { log.finest(""); return Native.getBool(this.pData + 4L); } 
/* 56 */   public void set_input(boolean paramBoolean) { log.finest(""); Native.putBool(this.pData + 4L, paramBoolean); } 
/* 57 */   public long get_window_group() { log.finest(""); return Native.getLong(this.pData + 32L); } 
/* 58 */   public void set_window_group(long paramLong) { log.finest(""); Native.putLong(this.pData + 32L, paramLong); }
/*    */ 
/*    */   String getName()
/*    */   {
/* 62 */     return "XWMHints";
/*    */   }
/*    */ 
/*    */   String getFieldsAsString()
/*    */   {
/* 67 */     StringBuilder localStringBuilder = new StringBuilder(360);
/*    */ 
/* 69 */     localStringBuilder.append("flags = ").append(get_flags()).append(", ");
/* 70 */     localStringBuilder.append("initial_state = ").append(get_initial_state()).append(", ");
/* 71 */     localStringBuilder.append("icon_pixmap = ").append(get_icon_pixmap()).append(", ");
/* 72 */     localStringBuilder.append("icon_window = ").append(get_icon_window()).append(", ");
/* 73 */     localStringBuilder.append("icon_x = ").append(get_icon_x()).append(", ");
/* 74 */     localStringBuilder.append("icon_y = ").append(get_icon_y()).append(", ");
/* 75 */     localStringBuilder.append("icon_mask = ").append(get_icon_mask()).append(", ");
/* 76 */     localStringBuilder.append("input = ").append(get_input()).append(", ");
/* 77 */     localStringBuilder.append("window_group = ").append(get_window_group()).append(", ");
/* 78 */     return localStringBuilder.toString();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XWMHints
 * JD-Core Version:    0.6.2
 */