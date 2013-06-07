/*    */ package sun.awt.X11;
/*    */ 
/*    */ import sun.misc.Unsafe;
/*    */ import sun.util.logging.PlatformLogger;
/*    */ 
/*    */ public class XSetWindowAttributes extends XWrapperBase
/*    */ {
/*  9 */   private Unsafe unsafe = XlibWrapper.unsafe;
/*    */   private final boolean should_free_memory;
/*    */   long pData;
/*    */ 
/*    */   public static int getSize()
/*    */   {
/* 11 */     return 60; } 
/* 12 */   public int getDataSize() { return getSize(); }
/*    */ 
/*    */   public long getPData()
/*    */   {
/* 16 */     return this.pData;
/*    */   }
/*    */ 
/*    */   public XSetWindowAttributes(long paramLong) {
/* 20 */     log.finest("Creating");
/* 21 */     this.pData = paramLong;
/* 22 */     this.should_free_memory = false;
/*    */   }
/*    */ 
/*    */   public XSetWindowAttributes()
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
/* 40 */   public long get_background_pixmap() { log.finest(""); return Native.getLong(this.pData + 0L); } 
/* 41 */   public void set_background_pixmap(long paramLong) { log.finest(""); Native.putLong(this.pData + 0L, paramLong); } 
/* 42 */   public long get_background_pixel() { log.finest(""); return Native.getLong(this.pData + 4L); } 
/* 43 */   public void set_background_pixel(long paramLong) { log.finest(""); Native.putLong(this.pData + 4L, paramLong); } 
/* 44 */   public long get_border_pixmap() { log.finest(""); return Native.getLong(this.pData + 8L); } 
/* 45 */   public void set_border_pixmap(long paramLong) { log.finest(""); Native.putLong(this.pData + 8L, paramLong); } 
/* 46 */   public long get_border_pixel() { log.finest(""); return Native.getLong(this.pData + 12L); } 
/* 47 */   public void set_border_pixel(long paramLong) { log.finest(""); Native.putLong(this.pData + 12L, paramLong); } 
/* 48 */   public int get_bit_gravity() { log.finest(""); return Native.getInt(this.pData + 16L); } 
/* 49 */   public void set_bit_gravity(int paramInt) { log.finest(""); Native.putInt(this.pData + 16L, paramInt); } 
/* 50 */   public int get_win_gravity() { log.finest(""); return Native.getInt(this.pData + 20L); } 
/* 51 */   public void set_win_gravity(int paramInt) { log.finest(""); Native.putInt(this.pData + 20L, paramInt); } 
/* 52 */   public int get_backing_store() { log.finest(""); return Native.getInt(this.pData + 24L); } 
/* 53 */   public void set_backing_store(int paramInt) { log.finest(""); Native.putInt(this.pData + 24L, paramInt); } 
/* 54 */   public long get_backing_planes() { log.finest(""); return Native.getLong(this.pData + 28L); } 
/* 55 */   public void set_backing_planes(long paramLong) { log.finest(""); Native.putLong(this.pData + 28L, paramLong); } 
/* 56 */   public long get_backing_pixel() { log.finest(""); return Native.getLong(this.pData + 32L); } 
/* 57 */   public void set_backing_pixel(long paramLong) { log.finest(""); Native.putLong(this.pData + 32L, paramLong); } 
/* 58 */   public boolean get_save_under() { log.finest(""); return Native.getBool(this.pData + 36L); } 
/* 59 */   public void set_save_under(boolean paramBoolean) { log.finest(""); Native.putBool(this.pData + 36L, paramBoolean); } 
/* 60 */   public long get_event_mask() { log.finest(""); return Native.getLong(this.pData + 40L); } 
/* 61 */   public void set_event_mask(long paramLong) { log.finest(""); Native.putLong(this.pData + 40L, paramLong); } 
/* 62 */   public long get_do_not_propagate_mask() { log.finest(""); return Native.getLong(this.pData + 44L); } 
/* 63 */   public void set_do_not_propagate_mask(long paramLong) { log.finest(""); Native.putLong(this.pData + 44L, paramLong); } 
/* 64 */   public boolean get_override_redirect() { log.finest(""); return Native.getBool(this.pData + 48L); } 
/* 65 */   public void set_override_redirect(boolean paramBoolean) { log.finest(""); Native.putBool(this.pData + 48L, paramBoolean); } 
/* 66 */   public long get_colormap() { log.finest(""); return Native.getLong(this.pData + 52L); } 
/* 67 */   public void set_colormap(long paramLong) { log.finest(""); Native.putLong(this.pData + 52L, paramLong); } 
/* 68 */   public long get_cursor() { log.finest(""); return Native.getLong(this.pData + 56L); } 
/* 69 */   public void set_cursor(long paramLong) { log.finest(""); Native.putLong(this.pData + 56L, paramLong); }
/*    */ 
/*    */   String getName()
/*    */   {
/* 73 */     return "XSetWindowAttributes";
/*    */   }
/*    */ 
/*    */   String getFieldsAsString()
/*    */   {
/* 78 */     StringBuilder localStringBuilder = new StringBuilder(600);
/*    */ 
/* 80 */     localStringBuilder.append("background_pixmap = ").append(get_background_pixmap()).append(", ");
/* 81 */     localStringBuilder.append("background_pixel = ").append(get_background_pixel()).append(", ");
/* 82 */     localStringBuilder.append("border_pixmap = ").append(get_border_pixmap()).append(", ");
/* 83 */     localStringBuilder.append("border_pixel = ").append(get_border_pixel()).append(", ");
/* 84 */     localStringBuilder.append("bit_gravity = ").append(get_bit_gravity()).append(", ");
/* 85 */     localStringBuilder.append("win_gravity = ").append(get_win_gravity()).append(", ");
/* 86 */     localStringBuilder.append("backing_store = ").append(get_backing_store()).append(", ");
/* 87 */     localStringBuilder.append("backing_planes = ").append(get_backing_planes()).append(", ");
/* 88 */     localStringBuilder.append("backing_pixel = ").append(get_backing_pixel()).append(", ");
/* 89 */     localStringBuilder.append("save_under = ").append(get_save_under()).append(", ");
/* 90 */     localStringBuilder.append("event_mask = ").append(get_event_mask()).append(", ");
/* 91 */     localStringBuilder.append("do_not_propagate_mask = ").append(get_do_not_propagate_mask()).append(", ");
/* 92 */     localStringBuilder.append("override_redirect = ").append(get_override_redirect()).append(", ");
/* 93 */     localStringBuilder.append("colormap = ").append(get_colormap()).append(", ");
/* 94 */     localStringBuilder.append("cursor = ").append(get_cursor()).append(", ");
/* 95 */     return localStringBuilder.toString();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XSetWindowAttributes
 * JD-Core Version:    0.6.2
 */