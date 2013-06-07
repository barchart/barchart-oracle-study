/*    */ package sun.awt.X11;
/*    */ 
/*    */ import sun.misc.Unsafe;
/*    */ import sun.util.logging.PlatformLogger;
/*    */ 
/*    */ public class XReparentEvent extends XWrapperBase
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
/*    */   public XReparentEvent(long paramLong) {
/* 20 */     log.finest("Creating");
/* 21 */     this.pData = paramLong;
/* 22 */     this.should_free_memory = false;
/*    */   }
/*    */ 
/*    */   public XReparentEvent()
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
/* 48 */   public long get_event() { log.finest(""); return Native.getLong(this.pData + 16L); } 
/* 49 */   public void set_event(long paramLong) { log.finest(""); Native.putLong(this.pData + 16L, paramLong); } 
/* 50 */   public long get_window() { log.finest(""); return Native.getLong(this.pData + 20L); } 
/* 51 */   public void set_window(long paramLong) { log.finest(""); Native.putLong(this.pData + 20L, paramLong); } 
/* 52 */   public long get_parent() { log.finest(""); return Native.getLong(this.pData + 24L); } 
/* 53 */   public void set_parent(long paramLong) { log.finest(""); Native.putLong(this.pData + 24L, paramLong); } 
/* 54 */   public int get_x() { log.finest(""); return Native.getInt(this.pData + 28L); } 
/* 55 */   public void set_x(int paramInt) { log.finest(""); Native.putInt(this.pData + 28L, paramInt); } 
/* 56 */   public int get_y() { log.finest(""); return Native.getInt(this.pData + 32L); } 
/* 57 */   public void set_y(int paramInt) { log.finest(""); Native.putInt(this.pData + 32L, paramInt); } 
/* 58 */   public boolean get_override_redirect() { log.finest(""); return Native.getBool(this.pData + 36L); } 
/* 59 */   public void set_override_redirect(boolean paramBoolean) { log.finest(""); Native.putBool(this.pData + 36L, paramBoolean); }
/*    */ 
/*    */   String getName()
/*    */   {
/* 63 */     return "XReparentEvent";
/*    */   }
/*    */ 
/*    */   String getFieldsAsString()
/*    */   {
/* 68 */     StringBuilder localStringBuilder = new StringBuilder(400);
/*    */ 
/* 70 */     localStringBuilder.append("type = ").append(XlibWrapper.eventToString[get_type()]).append(", ");
/* 71 */     localStringBuilder.append("serial = ").append(get_serial()).append(", ");
/* 72 */     localStringBuilder.append("send_event = ").append(get_send_event()).append(", ");
/* 73 */     localStringBuilder.append("display = ").append(get_display()).append(", ");
/* 74 */     localStringBuilder.append("event = ").append(get_event()).append(", ");
/* 75 */     localStringBuilder.append("window = ").append(getWindow(get_window())).append(", ");
/* 76 */     localStringBuilder.append("parent = ").append(get_parent()).append(", ");
/* 77 */     localStringBuilder.append("x = ").append(get_x()).append(", ");
/* 78 */     localStringBuilder.append("y = ").append(get_y()).append(", ");
/* 79 */     localStringBuilder.append("override_redirect = ").append(get_override_redirect()).append(", ");
/* 80 */     return localStringBuilder.toString();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XReparentEvent
 * JD-Core Version:    0.6.2
 */