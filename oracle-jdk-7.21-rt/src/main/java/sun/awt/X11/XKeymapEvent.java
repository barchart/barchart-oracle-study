/*     */ package sun.awt.X11;
/*     */ 
/*     */ import sun.misc.Unsafe;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ public class XKeymapEvent extends XWrapperBase
/*     */ {
/*   9 */   private Unsafe unsafe = XlibWrapper.unsafe;
/*     */   private final boolean should_free_memory;
/*     */   long pData;
/*     */ 
/*     */   public static int getSize()
/*     */   {
/*  11 */     return 52; } 
/*  12 */   public int getDataSize() { return getSize(); }
/*     */ 
/*     */   public long getPData()
/*     */   {
/*  16 */     return this.pData;
/*     */   }
/*     */ 
/*     */   public XKeymapEvent(long paramLong) {
/*  20 */     log.finest("Creating");
/*  21 */     this.pData = paramLong;
/*  22 */     this.should_free_memory = false;
/*     */   }
/*     */ 
/*     */   public XKeymapEvent()
/*     */   {
/*  27 */     log.finest("Creating");
/*  28 */     this.pData = this.unsafe.allocateMemory(getSize());
/*  29 */     this.should_free_memory = true;
/*     */   }
/*     */ 
/*     */   public void dispose()
/*     */   {
/*  34 */     log.finest("Disposing");
/*  35 */     if (this.should_free_memory) {
/*  36 */       log.finest("freeing memory");
/*  37 */       this.unsafe.freeMemory(this.pData);
/*     */     }
/*     */   }
/*  40 */   public int get_type() { log.finest(""); return Native.getInt(this.pData + 0L); } 
/*  41 */   public void set_type(int paramInt) { log.finest(""); Native.putInt(this.pData + 0L, paramInt); } 
/*  42 */   public long get_serial() { log.finest(""); return Native.getLong(this.pData + 4L); } 
/*  43 */   public void set_serial(long paramLong) { log.finest(""); Native.putLong(this.pData + 4L, paramLong); } 
/*  44 */   public boolean get_send_event() { log.finest(""); return Native.getBool(this.pData + 8L); } 
/*  45 */   public void set_send_event(boolean paramBoolean) { log.finest(""); Native.putBool(this.pData + 8L, paramBoolean); } 
/*  46 */   public long get_display() { log.finest(""); return Native.getLong(this.pData + 12L); } 
/*  47 */   public void set_display(long paramLong) { log.finest(""); Native.putLong(this.pData + 12L, paramLong); } 
/*  48 */   public long get_window() { log.finest(""); return Native.getLong(this.pData + 16L); } 
/*  49 */   public void set_window(long paramLong) { log.finest(""); Native.putLong(this.pData + 16L, paramLong); } 
/*  50 */   public byte get_key_vector(int paramInt) { log.finest(""); return Native.getByte(this.pData + 20L + paramInt * 1); } 
/*  51 */   public void set_key_vector(int paramInt, byte paramByte) { log.finest(""); Native.putByte(this.pData + 20L + paramInt * 1, paramByte); } 
/*  52 */   public long get_key_vector() { log.finest(""); return this.pData + 20L; }
/*     */ 
/*     */   String getName()
/*     */   {
/*  56 */     return "XKeymapEvent";
/*     */   }
/*     */ 
/*     */   String getFieldsAsString()
/*     */   {
/*  61 */     StringBuilder localStringBuilder = new StringBuilder(240);
/*     */ 
/*  63 */     localStringBuilder.append("type = ").append(XlibWrapper.eventToString[get_type()]).append(", ");
/*  64 */     localStringBuilder.append("serial = ").append(get_serial()).append(", ");
/*  65 */     localStringBuilder.append("send_event = ").append(get_send_event()).append(", ");
/*  66 */     localStringBuilder.append("display = ").append(get_display()).append(", ");
/*  67 */     localStringBuilder.append("window = ").append(getWindow(get_window())).append(", ");
/*  68 */     localStringBuilder.append("{").append(get_key_vector(0)).append(" ").append(get_key_vector(1)).append(" ").append(get_key_vector(2)).append(" ").append(get_key_vector(3)).append(" ").append(get_key_vector(4)).append(" ").append(get_key_vector(5)).append(" ").append(get_key_vector(6)).append(" ").append(get_key_vector(7)).append(" ").append(get_key_vector(8)).append(" ").append(get_key_vector(9)).append(" ").append(get_key_vector(10)).append(" ").append(get_key_vector(11)).append(" ").append(get_key_vector(12)).append(" ").append(get_key_vector(13)).append(" ").append(get_key_vector(14)).append(" ").append(get_key_vector(15)).append(" ").append(get_key_vector(16)).append(" ").append(get_key_vector(17)).append(" ").append(get_key_vector(18)).append(" ").append(get_key_vector(19)).append(" ").append(get_key_vector(20)).append(" ").append(get_key_vector(21)).append(" ").append(get_key_vector(22)).append(" ").append(get_key_vector(23)).append(" ").append(get_key_vector(24)).append(" ").append(get_key_vector(25)).append(" ").append(get_key_vector(26)).append(" ").append(get_key_vector(27)).append(" ").append(get_key_vector(28)).append(" ").append(get_key_vector(29)).append(" ").append(get_key_vector(30)).append(" ").append(get_key_vector(31)).append(" ").append("}");
/*     */ 
/* 101 */     return localStringBuilder.toString();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XKeymapEvent
 * JD-Core Version:    0.6.2
 */