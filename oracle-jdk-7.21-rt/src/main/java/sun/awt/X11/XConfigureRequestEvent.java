/*    */ package sun.awt.X11;
/*    */ 
/*    */ import sun.misc.Unsafe;
/*    */ import sun.util.logging.PlatformLogger;
/*    */ 
/*    */ public class XConfigureRequestEvent extends XWrapperBase
/*    */ {
/*  9 */   private Unsafe unsafe = XlibWrapper.unsafe;
/*    */   private final boolean should_free_memory;
/*    */   long pData;
/*    */ 
/*    */   public static int getSize()
/*    */   {
/* 11 */     return 56; } 
/* 12 */   public int getDataSize() { return getSize(); }
/*    */ 
/*    */   public long getPData()
/*    */   {
/* 16 */     return this.pData;
/*    */   }
/*    */ 
/*    */   public XConfigureRequestEvent(long paramLong) {
/* 20 */     log.finest("Creating");
/* 21 */     this.pData = paramLong;
/* 22 */     this.should_free_memory = false;
/*    */   }
/*    */ 
/*    */   public XConfigureRequestEvent()
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
/* 40 */   public int get_type() { log.finest(""); return Native.getInt(this.pData + 0L); } 
/* 41 */   public void set_type(int paramInt) { log.finest(""); Native.putInt(this.pData + 0L, paramInt); } 
/* 42 */   public long get_serial() { log.finest(""); return Native.getLong(this.pData + 4L); } 
/* 43 */   public void set_serial(long paramLong) { log.finest(""); Native.putLong(this.pData + 4L, paramLong); } 
/* 44 */   public boolean get_send_event() { log.finest(""); return Native.getBool(this.pData + 8L); } 
/* 45 */   public void set_send_event(boolean paramBoolean) { log.finest(""); Native.putBool(this.pData + 8L, paramBoolean); } 
/* 46 */   public long get_display() { log.finest(""); return Native.getLong(this.pData + 12L); } 
/* 47 */   public void set_display(long paramLong) { log.finest(""); Native.putLong(this.pData + 12L, paramLong); } 
/* 48 */   public long get_parent() { log.finest(""); return Native.getLong(this.pData + 16L); } 
/* 49 */   public void set_parent(long paramLong) { log.finest(""); Native.putLong(this.pData + 16L, paramLong); } 
/* 50 */   public long get_window() { log.finest(""); return Native.getLong(this.pData + 20L); } 
/* 51 */   public void set_window(long paramLong) { log.finest(""); Native.putLong(this.pData + 20L, paramLong); } 
/* 52 */   public int get_x() { log.finest(""); return Native.getInt(this.pData + 24L); } 
/* 53 */   public void set_x(int paramInt) { log.finest(""); Native.putInt(this.pData + 24L, paramInt); } 
/* 54 */   public int get_y() { log.finest(""); return Native.getInt(this.pData + 28L); } 
/* 55 */   public void set_y(int paramInt) { log.finest(""); Native.putInt(this.pData + 28L, paramInt); } 
/* 56 */   public int get_width() { log.finest(""); return Native.getInt(this.pData + 32L); } 
/* 57 */   public void set_width(int paramInt) { log.finest(""); Native.putInt(this.pData + 32L, paramInt); } 
/* 58 */   public int get_height() { log.finest(""); return Native.getInt(this.pData + 36L); } 
/* 59 */   public void set_height(int paramInt) { log.finest(""); Native.putInt(this.pData + 36L, paramInt); } 
/* 60 */   public int get_border_width() { log.finest(""); return Native.getInt(this.pData + 40L); } 
/* 61 */   public void set_border_width(int paramInt) { log.finest(""); Native.putInt(this.pData + 40L, paramInt); } 
/* 62 */   public long get_above() { log.finest(""); return Native.getLong(this.pData + 44L); } 
/* 63 */   public void set_above(long paramLong) { log.finest(""); Native.putLong(this.pData + 44L, paramLong); } 
/* 64 */   public int get_detail() { log.finest(""); return Native.getInt(this.pData + 48L); } 
/* 65 */   public void set_detail(int paramInt) { log.finest(""); Native.putInt(this.pData + 48L, paramInt); } 
/* 66 */   public long get_value_mask() { log.finest(""); return Native.getLong(this.pData + 52L); } 
/* 67 */   public void set_value_mask(long paramLong) { log.finest(""); Native.putLong(this.pData + 52L, paramLong); }
/*    */ 
/*    */   String getName()
/*    */   {
/* 71 */     return "XConfigureRequestEvent";
/*    */   }
/*    */ 
/*    */   String getFieldsAsString()
/*    */   {
/* 76 */     StringBuilder localStringBuilder = new StringBuilder(560);
/*    */ 
/* 78 */     localStringBuilder.append("type = ").append(XlibWrapper.eventToString[get_type()]).append(", ");
/* 79 */     localStringBuilder.append("serial = ").append(get_serial()).append(", ");
/* 80 */     localStringBuilder.append("send_event = ").append(get_send_event()).append(", ");
/* 81 */     localStringBuilder.append("display = ").append(get_display()).append(", ");
/* 82 */     localStringBuilder.append("parent = ").append(get_parent()).append(", ");
/* 83 */     localStringBuilder.append("window = ").append(getWindow(get_window())).append(", ");
/* 84 */     localStringBuilder.append("x = ").append(get_x()).append(", ");
/* 85 */     localStringBuilder.append("y = ").append(get_y()).append(", ");
/* 86 */     localStringBuilder.append("width = ").append(get_width()).append(", ");
/* 87 */     localStringBuilder.append("height = ").append(get_height()).append(", ");
/* 88 */     localStringBuilder.append("border_width = ").append(get_border_width()).append(", ");
/* 89 */     localStringBuilder.append("above = ").append(get_above()).append(", ");
/* 90 */     localStringBuilder.append("detail = ").append(get_detail()).append(", ");
/* 91 */     localStringBuilder.append("value_mask = ").append(get_value_mask()).append(", ");
/* 92 */     return localStringBuilder.toString();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XConfigureRequestEvent
 * JD-Core Version:    0.6.2
 */