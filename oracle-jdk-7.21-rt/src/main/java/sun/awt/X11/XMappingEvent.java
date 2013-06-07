/*    */ package sun.awt.X11;
/*    */ 
/*    */ import sun.misc.Unsafe;
/*    */ import sun.util.logging.PlatformLogger;
/*    */ 
/*    */ public class XMappingEvent extends XWrapperBase
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
/*    */   public XMappingEvent(long paramLong) {
/* 20 */     log.finest("Creating");
/* 21 */     this.pData = paramLong;
/* 22 */     this.should_free_memory = false;
/*    */   }
/*    */ 
/*    */   public XMappingEvent()
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
/* 50 */   public int get_request() { log.finest(""); return Native.getInt(this.pData + 20L); } 
/* 51 */   public void set_request(int paramInt) { log.finest(""); Native.putInt(this.pData + 20L, paramInt); } 
/* 52 */   public int get_first_keycode() { log.finest(""); return Native.getInt(this.pData + 24L); } 
/* 53 */   public void set_first_keycode(int paramInt) { log.finest(""); Native.putInt(this.pData + 24L, paramInt); } 
/* 54 */   public int get_count() { log.finest(""); return Native.getInt(this.pData + 28L); } 
/* 55 */   public void set_count(int paramInt) { log.finest(""); Native.putInt(this.pData + 28L, paramInt); }
/*    */ 
/*    */   String getName()
/*    */   {
/* 59 */     return "XMappingEvent";
/*    */   }
/*    */ 
/*    */   String getFieldsAsString()
/*    */   {
/* 64 */     StringBuilder localStringBuilder = new StringBuilder(320);
/*    */ 
/* 66 */     localStringBuilder.append("type = ").append(XlibWrapper.eventToString[get_type()]).append(", ");
/* 67 */     localStringBuilder.append("serial = ").append(get_serial()).append(", ");
/* 68 */     localStringBuilder.append("send_event = ").append(get_send_event()).append(", ");
/* 69 */     localStringBuilder.append("display = ").append(get_display()).append(", ");
/* 70 */     localStringBuilder.append("window = ").append(getWindow(get_window())).append(", ");
/* 71 */     localStringBuilder.append("request = ").append(get_request()).append(", ");
/* 72 */     localStringBuilder.append("first_keycode = ").append(get_first_keycode()).append(", ");
/* 73 */     localStringBuilder.append("count = ").append(get_count()).append(", ");
/* 74 */     return localStringBuilder.toString();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XMappingEvent
 * JD-Core Version:    0.6.2
 */