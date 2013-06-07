/*    */ package sun.awt.X11;
/*    */ 
/*    */ import sun.misc.Unsafe;
/*    */ import sun.util.logging.PlatformLogger;
/*    */ 
/*    */ public class XKeyEvent extends XWrapperBase
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
/*    */   public XKeyEvent(long paramLong) {
/* 20 */     log.finest("Creating");
/* 21 */     this.pData = paramLong;
/* 22 */     this.should_free_memory = false;
/*    */   }
/*    */ 
/*    */   public XKeyEvent()
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
/* 48 */   public long get_window() { log.finest(""); return Native.getLong(this.pData + 16L); } 
/* 49 */   public void set_window(long paramLong) { log.finest(""); Native.putLong(this.pData + 16L, paramLong); } 
/* 50 */   public long get_root() { log.finest(""); return Native.getLong(this.pData + 20L); } 
/* 51 */   public void set_root(long paramLong) { log.finest(""); Native.putLong(this.pData + 20L, paramLong); } 
/* 52 */   public long get_subwindow() { log.finest(""); return Native.getLong(this.pData + 24L); } 
/* 53 */   public void set_subwindow(long paramLong) { log.finest(""); Native.putLong(this.pData + 24L, paramLong); } 
/* 54 */   public long get_time() { log.finest(""); return Native.getULong(this.pData + 28L); } 
/* 55 */   public void set_time(long paramLong) { log.finest(""); Native.putULong(this.pData + 28L, paramLong); } 
/* 56 */   public int get_x() { log.finest(""); return Native.getInt(this.pData + 32L); } 
/* 57 */   public void set_x(int paramInt) { log.finest(""); Native.putInt(this.pData + 32L, paramInt); } 
/* 58 */   public int get_y() { log.finest(""); return Native.getInt(this.pData + 36L); } 
/* 59 */   public void set_y(int paramInt) { log.finest(""); Native.putInt(this.pData + 36L, paramInt); } 
/* 60 */   public int get_x_root() { log.finest(""); return Native.getInt(this.pData + 40L); } 
/* 61 */   public void set_x_root(int paramInt) { log.finest(""); Native.putInt(this.pData + 40L, paramInt); } 
/* 62 */   public int get_y_root() { log.finest(""); return Native.getInt(this.pData + 44L); } 
/* 63 */   public void set_y_root(int paramInt) { log.finest(""); Native.putInt(this.pData + 44L, paramInt); } 
/* 64 */   public int get_state() { log.finest(""); return Native.getInt(this.pData + 48L); } 
/* 65 */   public void set_state(int paramInt) { log.finest(""); Native.putInt(this.pData + 48L, paramInt); } 
/* 66 */   public int get_keycode() { log.finest(""); return Native.getInt(this.pData + 52L); } 
/* 67 */   public void set_keycode(int paramInt) { log.finest(""); Native.putInt(this.pData + 52L, paramInt); } 
/* 68 */   public boolean get_same_screen() { log.finest(""); return Native.getBool(this.pData + 56L); } 
/* 69 */   public void set_same_screen(boolean paramBoolean) { log.finest(""); Native.putBool(this.pData + 56L, paramBoolean); }
/*    */ 
/*    */   String getName()
/*    */   {
/* 73 */     return "XKeyEvent";
/*    */   }
/*    */ 
/*    */   String getFieldsAsString()
/*    */   {
/* 78 */     StringBuilder localStringBuilder = new StringBuilder(600);
/*    */ 
/* 80 */     localStringBuilder.append("type = ").append(XlibWrapper.eventToString[get_type()]).append(", ");
/* 81 */     localStringBuilder.append("serial = ").append(get_serial()).append(", ");
/* 82 */     localStringBuilder.append("send_event = ").append(get_send_event()).append(", ");
/* 83 */     localStringBuilder.append("display = ").append(get_display()).append(", ");
/* 84 */     localStringBuilder.append("window = ").append(getWindow(get_window())).append(", ");
/* 85 */     localStringBuilder.append("root = ").append(get_root()).append(", ");
/* 86 */     localStringBuilder.append("subwindow = ").append(get_subwindow()).append(", ");
/* 87 */     localStringBuilder.append("time = ").append(get_time()).append(", ");
/* 88 */     localStringBuilder.append("x = ").append(get_x()).append(", ");
/* 89 */     localStringBuilder.append("y = ").append(get_y()).append(", ");
/* 90 */     localStringBuilder.append("x_root = ").append(get_x_root()).append(", ");
/* 91 */     localStringBuilder.append("y_root = ").append(get_y_root()).append(", ");
/* 92 */     localStringBuilder.append("state = ").append(get_state()).append(", ");
/* 93 */     localStringBuilder.append("keycode = ").append(get_keycode()).append(", ");
/* 94 */     localStringBuilder.append("same_screen = ").append(get_same_screen()).append(", ");
/* 95 */     return localStringBuilder.toString();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XKeyEvent
 * JD-Core Version:    0.6.2
 */