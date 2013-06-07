/*    */ package sun.awt.X11;
/*    */ 
/*    */ import sun.misc.Unsafe;
/*    */ import sun.util.logging.PlatformLogger;
/*    */ 
/*    */ public class XkbAccessXNotifyEvent extends XWrapperBase
/*    */ {
/*  9 */   private Unsafe unsafe = XlibWrapper.unsafe;
/*    */   private final boolean should_free_memory;
/*    */   long pData;
/*    */ 
/*    */   public static int getSize()
/*    */   {
/* 11 */     return 44; } 
/* 12 */   public int getDataSize() { return getSize(); }
/*    */ 
/*    */   public long getPData()
/*    */   {
/* 16 */     return this.pData;
/*    */   }
/*    */ 
/*    */   public XkbAccessXNotifyEvent(long paramLong) {
/* 20 */     log.finest("Creating");
/* 21 */     this.pData = paramLong;
/* 22 */     this.should_free_memory = false;
/*    */   }
/*    */ 
/*    */   public XkbAccessXNotifyEvent()
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
/* 54 */   public int get_detail() { log.finest(""); return Native.getInt(this.pData + 28L); } 
/* 55 */   public void set_detail(int paramInt) { log.finest(""); Native.putInt(this.pData + 28L, paramInt); } 
/* 56 */   public int get_keycode() { log.finest(""); return Native.getInt(this.pData + 32L); } 
/* 57 */   public void set_keycode(int paramInt) { log.finest(""); Native.putInt(this.pData + 32L, paramInt); } 
/* 58 */   public int get_sk_delay() { log.finest(""); return Native.getInt(this.pData + 36L); } 
/* 59 */   public void set_sk_delay(int paramInt) { log.finest(""); Native.putInt(this.pData + 36L, paramInt); } 
/* 60 */   public int get_debounce_delay() { log.finest(""); return Native.getInt(this.pData + 40L); } 
/* 61 */   public void set_debounce_delay(int paramInt) { log.finest(""); Native.putInt(this.pData + 40L, paramInt); }
/*    */ 
/*    */   String getName()
/*    */   {
/* 65 */     return "XkbAccessXNotifyEvent";
/*    */   }
/*    */ 
/*    */   String getFieldsAsString()
/*    */   {
/* 70 */     StringBuilder localStringBuilder = new StringBuilder(440);
/*    */ 
/* 72 */     localStringBuilder.append("type = ").append(XlibWrapper.eventToString[get_type()]).append(", ");
/* 73 */     localStringBuilder.append("serial = ").append(get_serial()).append(", ");
/* 74 */     localStringBuilder.append("send_event = ").append(get_send_event()).append(", ");
/* 75 */     localStringBuilder.append("display = ").append(get_display()).append(", ");
/* 76 */     localStringBuilder.append("time = ").append(get_time()).append(", ");
/* 77 */     localStringBuilder.append("xkb_type = ").append(get_xkb_type()).append(", ");
/* 78 */     localStringBuilder.append("device = ").append(get_device()).append(", ");
/* 79 */     localStringBuilder.append("detail = ").append(get_detail()).append(", ");
/* 80 */     localStringBuilder.append("keycode = ").append(get_keycode()).append(", ");
/* 81 */     localStringBuilder.append("sk_delay = ").append(get_sk_delay()).append(", ");
/* 82 */     localStringBuilder.append("debounce_delay = ").append(get_debounce_delay()).append(", ");
/* 83 */     return localStringBuilder.toString();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XkbAccessXNotifyEvent
 * JD-Core Version:    0.6.2
 */