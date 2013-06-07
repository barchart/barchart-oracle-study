/*    */ package sun.awt.X11;
/*    */ 
/*    */ import sun.misc.Unsafe;
/*    */ import sun.util.logging.PlatformLogger;
/*    */ 
/*    */ public class XkbActionMessageEvent extends XWrapperBase
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
/*    */   public XkbActionMessageEvent(long paramLong) {
/* 20 */     log.finest("Creating");
/* 21 */     this.pData = paramLong;
/* 22 */     this.should_free_memory = false;
/*    */   }
/*    */ 
/*    */   public XkbActionMessageEvent()
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
/* 54 */   public int get_keycode() { log.finest(""); return Native.getInt(this.pData + 28L); } 
/* 55 */   public void set_keycode(int paramInt) { log.finest(""); Native.putInt(this.pData + 28L, paramInt); } 
/* 56 */   public boolean get_press() { log.finest(""); return Native.getBool(this.pData + 32L); } 
/* 57 */   public void set_press(boolean paramBoolean) { log.finest(""); Native.putBool(this.pData + 32L, paramBoolean); } 
/* 58 */   public boolean get_key_event_follows() { log.finest(""); return Native.getBool(this.pData + 36L); } 
/* 59 */   public void set_key_event_follows(boolean paramBoolean) { log.finest(""); Native.putBool(this.pData + 36L, paramBoolean); } 
/* 60 */   public int get_group() { log.finest(""); return Native.getInt(this.pData + 40L); } 
/* 61 */   public void set_group(int paramInt) { log.finest(""); Native.putInt(this.pData + 40L, paramInt); } 
/* 62 */   public int get_mods() { log.finest(""); return Native.getInt(this.pData + 44L); } 
/* 63 */   public void set_mods(int paramInt) { log.finest(""); Native.putInt(this.pData + 44L, paramInt); } 
/* 64 */   public byte get_message(int paramInt) { log.finest(""); return Native.getByte(this.pData + 48L + paramInt * 1); } 
/* 65 */   public void set_message(int paramInt, byte paramByte) { log.finest(""); Native.putByte(this.pData + 48L + paramInt * 1, paramByte); } 
/* 66 */   public long get_message() { log.finest(""); return this.pData + 48L; }
/*    */ 
/*    */   String getName()
/*    */   {
/* 70 */     return "XkbActionMessageEvent";
/*    */   }
/*    */ 
/*    */   String getFieldsAsString()
/*    */   {
/* 75 */     StringBuilder localStringBuilder = new StringBuilder(520);
/*    */ 
/* 77 */     localStringBuilder.append("type = ").append(XlibWrapper.eventToString[get_type()]).append(", ");
/* 78 */     localStringBuilder.append("serial = ").append(get_serial()).append(", ");
/* 79 */     localStringBuilder.append("send_event = ").append(get_send_event()).append(", ");
/* 80 */     localStringBuilder.append("display = ").append(get_display()).append(", ");
/* 81 */     localStringBuilder.append("time = ").append(get_time()).append(", ");
/* 82 */     localStringBuilder.append("xkb_type = ").append(get_xkb_type()).append(", ");
/* 83 */     localStringBuilder.append("device = ").append(get_device()).append(", ");
/* 84 */     localStringBuilder.append("keycode = ").append(get_keycode()).append(", ");
/* 85 */     localStringBuilder.append("press = ").append(get_press()).append(", ");
/* 86 */     localStringBuilder.append("key_event_follows = ").append(get_key_event_follows()).append(", ");
/* 87 */     localStringBuilder.append("group = ").append(get_group()).append(", ");
/* 88 */     localStringBuilder.append("mods = ").append(get_mods()).append(", ");
/* 89 */     localStringBuilder.append("{").append(get_message(0)).append(" ").append(get_message(1)).append(" ").append(get_message(2)).append(" ").append(get_message(3)).append(" ").append(get_message(4)).append(" ").append(get_message(5)).append(" ").append(get_message(6)).append(" ").append("}");
/*    */ 
/* 97 */     return localStringBuilder.toString();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XkbActionMessageEvent
 * JD-Core Version:    0.6.2
 */