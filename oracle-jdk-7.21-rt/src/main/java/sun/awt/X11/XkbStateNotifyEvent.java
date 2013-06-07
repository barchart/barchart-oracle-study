/*     */ package sun.awt.X11;
/*     */ 
/*     */ import sun.misc.Unsafe;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ public class XkbStateNotifyEvent extends XWrapperBase
/*     */ {
/*   9 */   private Unsafe unsafe = XlibWrapper.unsafe;
/*     */   private final boolean should_free_memory;
/*     */   long pData;
/*     */ 
/*     */   public static int getSize()
/*     */   {
/*  11 */     return 80; } 
/*  12 */   public int getDataSize() { return getSize(); }
/*     */ 
/*     */   public long getPData()
/*     */   {
/*  16 */     return this.pData;
/*     */   }
/*     */ 
/*     */   public XkbStateNotifyEvent(long paramLong) {
/*  20 */     log.finest("Creating");
/*  21 */     this.pData = paramLong;
/*  22 */     this.should_free_memory = false;
/*     */   }
/*     */ 
/*     */   public XkbStateNotifyEvent()
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
/*  42 */   public long get_serial() { log.finest(""); return Native.getULong(this.pData + 4L); } 
/*  43 */   public void set_serial(long paramLong) { log.finest(""); Native.putULong(this.pData + 4L, paramLong); } 
/*  44 */   public boolean get_send_event() { log.finest(""); return Native.getBool(this.pData + 8L); } 
/*  45 */   public void set_send_event(boolean paramBoolean) { log.finest(""); Native.putBool(this.pData + 8L, paramBoolean); } 
/*  46 */   public long get_display() { log.finest(""); return Native.getLong(this.pData + 12L); } 
/*  47 */   public void set_display(long paramLong) { log.finest(""); Native.putLong(this.pData + 12L, paramLong); } 
/*  48 */   public long get_time() { log.finest(""); return Native.getULong(this.pData + 16L); } 
/*  49 */   public void set_time(long paramLong) { log.finest(""); Native.putULong(this.pData + 16L, paramLong); } 
/*  50 */   public int get_xkb_type() { log.finest(""); return Native.getInt(this.pData + 20L); } 
/*  51 */   public void set_xkb_type(int paramInt) { log.finest(""); Native.putInt(this.pData + 20L, paramInt); } 
/*  52 */   public int get_device() { log.finest(""); return Native.getInt(this.pData + 24L); } 
/*  53 */   public void set_device(int paramInt) { log.finest(""); Native.putInt(this.pData + 24L, paramInt); } 
/*  54 */   public int get_changed() { log.finest(""); return Native.getInt(this.pData + 28L); } 
/*  55 */   public void set_changed(int paramInt) { log.finest(""); Native.putInt(this.pData + 28L, paramInt); } 
/*  56 */   public int get_group() { log.finest(""); return Native.getInt(this.pData + 32L); } 
/*  57 */   public void set_group(int paramInt) { log.finest(""); Native.putInt(this.pData + 32L, paramInt); } 
/*  58 */   public int get_base_group() { log.finest(""); return Native.getInt(this.pData + 36L); } 
/*  59 */   public void set_base_group(int paramInt) { log.finest(""); Native.putInt(this.pData + 36L, paramInt); } 
/*  60 */   public int get_latched_group() { log.finest(""); return Native.getInt(this.pData + 40L); } 
/*  61 */   public void set_latched_group(int paramInt) { log.finest(""); Native.putInt(this.pData + 40L, paramInt); } 
/*  62 */   public int get_locked_group() { log.finest(""); return Native.getInt(this.pData + 44L); } 
/*  63 */   public void set_locked_group(int paramInt) { log.finest(""); Native.putInt(this.pData + 44L, paramInt); } 
/*  64 */   public int get_mods() { log.finest(""); return Native.getInt(this.pData + 48L); } 
/*  65 */   public void set_mods(int paramInt) { log.finest(""); Native.putInt(this.pData + 48L, paramInt); } 
/*  66 */   public int get_base_mods() { log.finest(""); return Native.getInt(this.pData + 52L); } 
/*  67 */   public void set_base_mods(int paramInt) { log.finest(""); Native.putInt(this.pData + 52L, paramInt); } 
/*  68 */   public int get_latched_mods() { log.finest(""); return Native.getInt(this.pData + 56L); } 
/*  69 */   public void set_latched_mods(int paramInt) { log.finest(""); Native.putInt(this.pData + 56L, paramInt); } 
/*  70 */   public int get_locked_mods() { log.finest(""); return Native.getInt(this.pData + 60L); } 
/*  71 */   public void set_locked_mods(int paramInt) { log.finest(""); Native.putInt(this.pData + 60L, paramInt); } 
/*  72 */   public int get_compat_state() { log.finest(""); return Native.getInt(this.pData + 64L); } 
/*  73 */   public void set_compat_state(int paramInt) { log.finest(""); Native.putInt(this.pData + 64L, paramInt); } 
/*  74 */   public byte get_grab_mods() { log.finest(""); return Native.getByte(this.pData + 68L); } 
/*  75 */   public void set_grab_mods(byte paramByte) { log.finest(""); Native.putByte(this.pData + 68L, paramByte); } 
/*  76 */   public byte get_compat_grab_mods() { log.finest(""); return Native.getByte(this.pData + 69L); } 
/*  77 */   public void set_compat_grab_mods(byte paramByte) { log.finest(""); Native.putByte(this.pData + 69L, paramByte); } 
/*  78 */   public byte get_lookup_mods() { log.finest(""); return Native.getByte(this.pData + 70L); } 
/*  79 */   public void set_lookup_mods(byte paramByte) { log.finest(""); Native.putByte(this.pData + 70L, paramByte); } 
/*  80 */   public byte get_compat_lookup_mods() { log.finest(""); return Native.getByte(this.pData + 71L); } 
/*  81 */   public void set_compat_lookup_mods(byte paramByte) { log.finest(""); Native.putByte(this.pData + 71L, paramByte); } 
/*  82 */   public int get_ptr_buttons() { log.finest(""); return Native.getInt(this.pData + 72L); } 
/*  83 */   public void set_ptr_buttons(int paramInt) { log.finest(""); Native.putInt(this.pData + 72L, paramInt); } 
/*  84 */   public int get_keycode() { log.finest(""); return Native.getInt(this.pData + 76L); } 
/*  85 */   public void set_keycode(int paramInt) { log.finest(""); Native.putInt(this.pData + 76L, paramInt); } 
/*  86 */   public byte get_event_type() { log.finest(""); return Native.getByte(this.pData + 77L); } 
/*  87 */   public void set_event_type(byte paramByte) { log.finest(""); Native.putByte(this.pData + 77L, paramByte); } 
/*  88 */   public byte get_req_major() { log.finest(""); return Native.getByte(this.pData + 78L); } 
/*  89 */   public void set_req_major(byte paramByte) { log.finest(""); Native.putByte(this.pData + 78L, paramByte); } 
/*  90 */   public byte get_req_minor() { log.finest(""); return Native.getByte(this.pData + 79L); } 
/*  91 */   public void set_req_minor(byte paramByte) { log.finest(""); Native.putByte(this.pData + 79L, paramByte); }
/*     */ 
/*     */   String getName()
/*     */   {
/*  95 */     return "XkbStateNotifyEvent";
/*     */   }
/*     */ 
/*     */   String getFieldsAsString()
/*     */   {
/* 100 */     StringBuilder localStringBuilder = new StringBuilder(1040);
/*     */ 
/* 102 */     localStringBuilder.append("type = ").append(XlibWrapper.eventToString[get_type()]).append(", ");
/* 103 */     localStringBuilder.append("serial = ").append(get_serial()).append(", ");
/* 104 */     localStringBuilder.append("send_event = ").append(get_send_event()).append(", ");
/* 105 */     localStringBuilder.append("display = ").append(get_display()).append(", ");
/* 106 */     localStringBuilder.append("time = ").append(get_time()).append(", ");
/* 107 */     localStringBuilder.append("xkb_type = ").append(get_xkb_type()).append(", ");
/* 108 */     localStringBuilder.append("device = ").append(get_device()).append(", ");
/* 109 */     localStringBuilder.append("changed = ").append(get_changed()).append(", ");
/* 110 */     localStringBuilder.append("group = ").append(get_group()).append(", ");
/* 111 */     localStringBuilder.append("base_group = ").append(get_base_group()).append(", ");
/* 112 */     localStringBuilder.append("latched_group = ").append(get_latched_group()).append(", ");
/* 113 */     localStringBuilder.append("locked_group = ").append(get_locked_group()).append(", ");
/* 114 */     localStringBuilder.append("mods = ").append(get_mods()).append(", ");
/* 115 */     localStringBuilder.append("base_mods = ").append(get_base_mods()).append(", ");
/* 116 */     localStringBuilder.append("latched_mods = ").append(get_latched_mods()).append(", ");
/* 117 */     localStringBuilder.append("locked_mods = ").append(get_locked_mods()).append(", ");
/* 118 */     localStringBuilder.append("compat_state = ").append(get_compat_state()).append(", ");
/* 119 */     localStringBuilder.append("grab_mods = ").append(get_grab_mods()).append(", ");
/* 120 */     localStringBuilder.append("compat_grab_mods = ").append(get_compat_grab_mods()).append(", ");
/* 121 */     localStringBuilder.append("lookup_mods = ").append(get_lookup_mods()).append(", ");
/* 122 */     localStringBuilder.append("compat_lookup_mods = ").append(get_compat_lookup_mods()).append(", ");
/* 123 */     localStringBuilder.append("ptr_buttons = ").append(get_ptr_buttons()).append(", ");
/* 124 */     localStringBuilder.append("keycode = ").append(get_keycode()).append(", ");
/* 125 */     localStringBuilder.append("event_type = ").append(get_event_type()).append(", ");
/* 126 */     localStringBuilder.append("req_major = ").append(get_req_major()).append(", ");
/* 127 */     localStringBuilder.append("req_minor = ").append(get_req_minor()).append(", ");
/* 128 */     return localStringBuilder.toString();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XkbStateNotifyEvent
 * JD-Core Version:    0.6.2
 */