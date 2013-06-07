/*    */ package sun.awt.X11;
/*    */ 
/*    */ import sun.misc.Unsafe;
/*    */ import sun.util.logging.PlatformLogger;
/*    */ 
/*    */ public class XkbEvent extends XWrapperBase
/*    */ {
/*  9 */   private Unsafe unsafe = XlibWrapper.unsafe;
/*    */   private final boolean should_free_memory;
/*    */   long pData;
/*    */ 
/*    */   public static int getSize()
/*    */   {
/* 11 */     return 96; } 
/* 12 */   public int getDataSize() { return getSize(); }
/*    */ 
/*    */   public long getPData()
/*    */   {
/* 16 */     return this.pData;
/*    */   }
/*    */ 
/*    */   public XkbEvent(long paramLong) {
/* 20 */     log.finest("Creating");
/* 21 */     this.pData = paramLong;
/* 22 */     this.should_free_memory = false;
/*    */   }
/*    */ 
/*    */   public XkbEvent()
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
/* 42 */   public XkbAnyEvent get_any() { log.finest(""); return new XkbAnyEvent(this.pData + 0L); } 
/* 43 */   public XkbNewKeyboardNotifyEvent get_new_kbd() { log.finest(""); return new XkbNewKeyboardNotifyEvent(this.pData + 0L); } 
/* 44 */   public XkbMapNotifyEvent get_map() { log.finest(""); return new XkbMapNotifyEvent(this.pData + 0L); } 
/* 45 */   public XkbStateNotifyEvent get_state() { log.finest(""); return new XkbStateNotifyEvent(this.pData + 0L); } 
/* 46 */   public XkbControlsNotifyEvent get_ctrls() { log.finest(""); return new XkbControlsNotifyEvent(this.pData + 0L); } 
/* 47 */   public XkbIndicatorNotifyEvent get_indicators() { log.finest(""); return new XkbIndicatorNotifyEvent(this.pData + 0L); } 
/* 48 */   public XkbNamesNotifyEvent get_names() { log.finest(""); return new XkbNamesNotifyEvent(this.pData + 0L); } 
/* 49 */   public XkbCompatMapNotifyEvent get_compat() { log.finest(""); return new XkbCompatMapNotifyEvent(this.pData + 0L); } 
/* 50 */   public XkbBellNotifyEvent get_bell() { log.finest(""); return new XkbBellNotifyEvent(this.pData + 0L); } 
/* 51 */   public XkbActionMessageEvent get_message() { log.finest(""); return new XkbActionMessageEvent(this.pData + 0L); } 
/* 52 */   public XkbAccessXNotifyEvent get_accessx() { log.finest(""); return new XkbAccessXNotifyEvent(this.pData + 0L); } 
/* 53 */   public XkbExtensionDeviceNotifyEvent get_device() { log.finest(""); return new XkbExtensionDeviceNotifyEvent(this.pData + 0L); } 
/* 54 */   public XEvent get_core() { log.finest(""); return new XEvent(this.pData + 0L); }
/*    */ 
/*    */   String getName()
/*    */   {
/* 58 */     return "XkbEvent";
/*    */   }
/*    */ 
/*    */   String getFieldsAsString()
/*    */   {
/* 63 */     StringBuilder localStringBuilder = new StringBuilder(560);
/*    */ 
/* 65 */     localStringBuilder.append("type = ").append(XlibWrapper.eventToString[get_type()]).append(", ");
/* 66 */     localStringBuilder.append("any = ").append(get_any()).append(", ");
/* 67 */     localStringBuilder.append("new_kbd = ").append(get_new_kbd()).append(", ");
/* 68 */     localStringBuilder.append("map = ").append(get_map()).append(", ");
/* 69 */     localStringBuilder.append("state = ").append(get_state()).append(", ");
/* 70 */     localStringBuilder.append("ctrls = ").append(get_ctrls()).append(", ");
/* 71 */     localStringBuilder.append("indicators = ").append(get_indicators()).append(", ");
/* 72 */     localStringBuilder.append("names = ").append(get_names()).append(", ");
/* 73 */     localStringBuilder.append("compat = ").append(get_compat()).append(", ");
/* 74 */     localStringBuilder.append("bell = ").append(get_bell()).append(", ");
/* 75 */     localStringBuilder.append("message = ").append(get_message()).append(", ");
/* 76 */     localStringBuilder.append("accessx = ").append(get_accessx()).append(", ");
/* 77 */     localStringBuilder.append("device = ").append(get_device()).append(", ");
/* 78 */     localStringBuilder.append("core = ").append(get_core()).append(", ");
/* 79 */     return localStringBuilder.toString();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XkbEvent
 * JD-Core Version:    0.6.2
 */