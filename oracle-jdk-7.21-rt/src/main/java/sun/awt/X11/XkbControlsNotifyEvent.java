/*    */ package sun.awt.X11;
/*    */ 
/*    */ import sun.misc.Unsafe;
/*    */ import sun.util.logging.PlatformLogger;
/*    */ 
/*    */ public class XkbControlsNotifyEvent extends XWrapperBase
/*    */ {
/*  9 */   private Unsafe unsafe = XlibWrapper.unsafe;
/*    */   private final boolean should_free_memory;
/*    */   long pData;
/*    */ 
/*    */   public static int getSize()
/*    */   {
/* 11 */     return 48; } 
/* 12 */   public int getDataSize() { return getSize(); }
/*    */ 
/*    */   public long getPData()
/*    */   {
/* 16 */     return this.pData;
/*    */   }
/*    */ 
/*    */   public XkbControlsNotifyEvent(long paramLong) {
/* 20 */     log.finest("Creating");
/* 21 */     this.pData = paramLong;
/* 22 */     this.should_free_memory = false;
/*    */   }
/*    */ 
/*    */   public XkbControlsNotifyEvent()
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
/* 54 */   public int get_changed_ctrls() { log.finest(""); return Native.getInt(this.pData + 28L); } 
/* 55 */   public void set_changed_ctrls(int paramInt) { log.finest(""); Native.putInt(this.pData + 28L, paramInt); } 
/* 56 */   public int get_enabled_ctrls() { log.finest(""); return Native.getInt(this.pData + 32L); } 
/* 57 */   public void set_enabled_ctrls(int paramInt) { log.finest(""); Native.putInt(this.pData + 32L, paramInt); } 
/* 58 */   public int get_enabled_ctrl_changes() { log.finest(""); return Native.getInt(this.pData + 36L); } 
/* 59 */   public void set_enabled_ctrl_changes(int paramInt) { log.finest(""); Native.putInt(this.pData + 36L, paramInt); } 
/* 60 */   public int get_num_groups() { log.finest(""); return Native.getInt(this.pData + 40L); } 
/* 61 */   public void set_num_groups(int paramInt) { log.finest(""); Native.putInt(this.pData + 40L, paramInt); } 
/* 62 */   public int get_keycode() { log.finest(""); return Native.getInt(this.pData + 44L); } 
/* 63 */   public void set_keycode(int paramInt) { log.finest(""); Native.putInt(this.pData + 44L, paramInt); } 
/* 64 */   public byte get_event_type() { log.finest(""); return Native.getByte(this.pData + 45L); } 
/* 65 */   public void set_event_type(byte paramByte) { log.finest(""); Native.putByte(this.pData + 45L, paramByte); } 
/* 66 */   public byte get_req_major() { log.finest(""); return Native.getByte(this.pData + 46L); } 
/* 67 */   public void set_req_major(byte paramByte) { log.finest(""); Native.putByte(this.pData + 46L, paramByte); } 
/* 68 */   public byte get_req_minor() { log.finest(""); return Native.getByte(this.pData + 47L); } 
/* 69 */   public void set_req_minor(byte paramByte) { log.finest(""); Native.putByte(this.pData + 47L, paramByte); }
/*    */ 
/*    */   String getName()
/*    */   {
/* 73 */     return "XkbControlsNotifyEvent";
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
/* 87 */     localStringBuilder.append("changed_ctrls = ").append(get_changed_ctrls()).append(", ");
/* 88 */     localStringBuilder.append("enabled_ctrls = ").append(get_enabled_ctrls()).append(", ");
/* 89 */     localStringBuilder.append("enabled_ctrl_changes = ").append(get_enabled_ctrl_changes()).append(", ");
/* 90 */     localStringBuilder.append("num_groups = ").append(get_num_groups()).append(", ");
/* 91 */     localStringBuilder.append("keycode = ").append(get_keycode()).append(", ");
/* 92 */     localStringBuilder.append("event_type = ").append(get_event_type()).append(", ");
/* 93 */     localStringBuilder.append("req_major = ").append(get_req_major()).append(", ");
/* 94 */     localStringBuilder.append("req_minor = ").append(get_req_minor()).append(", ");
/* 95 */     return localStringBuilder.toString();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XkbControlsNotifyEvent
 * JD-Core Version:    0.6.2
 */