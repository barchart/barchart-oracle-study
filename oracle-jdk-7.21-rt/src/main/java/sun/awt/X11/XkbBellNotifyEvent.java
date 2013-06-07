/*    */ package sun.awt.X11;
/*    */ 
/*    */ import sun.misc.Unsafe;
/*    */ import sun.util.logging.PlatformLogger;
/*    */ 
/*    */ public class XkbBellNotifyEvent extends XWrapperBase
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
/*    */   public XkbBellNotifyEvent(long paramLong) {
/* 20 */     log.finest("Creating");
/* 21 */     this.pData = paramLong;
/* 22 */     this.should_free_memory = false;
/*    */   }
/*    */ 
/*    */   public XkbBellNotifyEvent()
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
/* 42 */   public long get_serial() { log.finest(""); return Native.getULong(this.pData + 4L); } 
/* 43 */   public void set_serial(long paramLong) { log.finest(""); Native.putULong(this.pData + 4L, paramLong); } 
/* 44 */   public boolean get_send_event() { log.finest(""); return Native.getBool(this.pData + 8L); } 
/* 45 */   public void set_send_event(boolean paramBoolean) { log.finest(""); Native.putBool(this.pData + 8L, paramBoolean); } 
/* 46 */   public long get_display() { log.finest(""); return Native.getLong(this.pData + 12L); } 
/* 47 */   public void set_display(long paramLong) { log.finest(""); Native.putLong(this.pData + 12L, paramLong); } 
/* 48 */   public long get_time() { log.finest(""); return Native.getULong(this.pData + 16L); } 
/* 49 */   public void set_time(long paramLong) { log.finest(""); Native.putULong(this.pData + 16L, paramLong); } 
/* 50 */   public int get_xkb_type() { log.finest(""); return Native.getInt(this.pData + 20L); } 
/* 51 */   public void set_xkb_type(int paramInt) { log.finest(""); Native.putInt(this.pData + 20L, paramInt); } 
/* 52 */   public int get_device() { log.finest(""); return Native.getInt(this.pData + 24L); } 
/* 53 */   public void set_device(int paramInt) { log.finest(""); Native.putInt(this.pData + 24L, paramInt); } 
/* 54 */   public int get_percent() { log.finest(""); return Native.getInt(this.pData + 28L); } 
/* 55 */   public void set_percent(int paramInt) { log.finest(""); Native.putInt(this.pData + 28L, paramInt); } 
/* 56 */   public int get_pitch() { log.finest(""); return Native.getInt(this.pData + 32L); } 
/* 57 */   public void set_pitch(int paramInt) { log.finest(""); Native.putInt(this.pData + 32L, paramInt); } 
/* 58 */   public int get_duration() { log.finest(""); return Native.getInt(this.pData + 36L); } 
/* 59 */   public void set_duration(int paramInt) { log.finest(""); Native.putInt(this.pData + 36L, paramInt); } 
/* 60 */   public int get_bell_class() { log.finest(""); return Native.getInt(this.pData + 40L); } 
/* 61 */   public void set_bell_class(int paramInt) { log.finest(""); Native.putInt(this.pData + 40L, paramInt); } 
/* 62 */   public int get_bell_id() { log.finest(""); return Native.getInt(this.pData + 44L); } 
/* 63 */   public void set_bell_id(int paramInt) { log.finest(""); Native.putInt(this.pData + 44L, paramInt); } 
/* 64 */   public long get_name() { log.finest(""); return Native.getLong(this.pData + 48L); } 
/* 65 */   public void set_name(long paramLong) { log.finest(""); Native.putLong(this.pData + 48L, paramLong); } 
/* 66 */   public long get_window() { log.finest(""); return Native.getLong(this.pData + 52L); } 
/* 67 */   public void set_window(long paramLong) { log.finest(""); Native.putLong(this.pData + 52L, paramLong); } 
/* 68 */   public boolean get_event_only() { log.finest(""); return Native.getBool(this.pData + 56L); } 
/* 69 */   public void set_event_only(boolean paramBoolean) { log.finest(""); Native.putBool(this.pData + 56L, paramBoolean); }
/*    */ 
/*    */   String getName()
/*    */   {
/* 73 */     return "XkbBellNotifyEvent";
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
/* 84 */     localStringBuilder.append("time = ").append(get_time()).append(", ");
/* 85 */     localStringBuilder.append("xkb_type = ").append(get_xkb_type()).append(", ");
/* 86 */     localStringBuilder.append("device = ").append(get_device()).append(", ");
/* 87 */     localStringBuilder.append("percent = ").append(get_percent()).append(", ");
/* 88 */     localStringBuilder.append("pitch = ").append(get_pitch()).append(", ");
/* 89 */     localStringBuilder.append("duration = ").append(get_duration()).append(", ");
/* 90 */     localStringBuilder.append("bell_class = ").append(get_bell_class()).append(", ");
/* 91 */     localStringBuilder.append("bell_id = ").append(get_bell_id()).append(", ");
/* 92 */     localStringBuilder.append("name = ").append(XAtom.get(get_name())).append(", ");
/* 93 */     localStringBuilder.append("window = ").append(getWindow(get_window())).append(", ");
/* 94 */     localStringBuilder.append("event_only = ").append(get_event_only()).append(", ");
/* 95 */     return localStringBuilder.toString();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XkbBellNotifyEvent
 * JD-Core Version:    0.6.2
 */