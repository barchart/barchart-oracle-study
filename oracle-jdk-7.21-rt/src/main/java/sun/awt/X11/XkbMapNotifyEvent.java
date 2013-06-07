/*     */ package sun.awt.X11;
/*     */ 
/*     */ import sun.misc.Unsafe;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ public class XkbMapNotifyEvent extends XWrapperBase
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
/*     */   public XkbMapNotifyEvent(long paramLong) {
/*  20 */     log.finest("Creating");
/*  21 */     this.pData = paramLong;
/*  22 */     this.should_free_memory = false;
/*     */   }
/*     */ 
/*     */   public XkbMapNotifyEvent()
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
/*  56 */   public int get_flags() { log.finest(""); return Native.getInt(this.pData + 32L); } 
/*  57 */   public void set_flags(int paramInt) { log.finest(""); Native.putInt(this.pData + 32L, paramInt); } 
/*  58 */   public int get_first_type() { log.finest(""); return Native.getInt(this.pData + 36L); } 
/*  59 */   public void set_first_type(int paramInt) { log.finest(""); Native.putInt(this.pData + 36L, paramInt); } 
/*  60 */   public int get_num_types() { log.finest(""); return Native.getInt(this.pData + 40L); } 
/*  61 */   public void set_num_types(int paramInt) { log.finest(""); Native.putInt(this.pData + 40L, paramInt); } 
/*  62 */   public int get_min_key_code() { log.finest(""); return Native.getInt(this.pData + 44L); } 
/*  63 */   public void set_min_key_code(int paramInt) { log.finest(""); Native.putInt(this.pData + 44L, paramInt); } 
/*  64 */   public int get_max_key_code() { log.finest(""); return Native.getInt(this.pData + 45L); } 
/*  65 */   public void set_max_key_code(int paramInt) { log.finest(""); Native.putInt(this.pData + 45L, paramInt); } 
/*  66 */   public int get_first_key_sym() { log.finest(""); return Native.getInt(this.pData + 46L); } 
/*  67 */   public void set_first_key_sym(int paramInt) { log.finest(""); Native.putInt(this.pData + 46L, paramInt); } 
/*  68 */   public int get_first_key_act() { log.finest(""); return Native.getInt(this.pData + 47L); } 
/*  69 */   public void set_first_key_act(int paramInt) { log.finest(""); Native.putInt(this.pData + 47L, paramInt); } 
/*  70 */   public int get_first_key_behavior() { log.finest(""); return Native.getInt(this.pData + 48L); } 
/*  71 */   public void set_first_key_behavior(int paramInt) { log.finest(""); Native.putInt(this.pData + 48L, paramInt); } 
/*  72 */   public int get_first_key_explicit() { log.finest(""); return Native.getInt(this.pData + 49L); } 
/*  73 */   public void set_first_key_explicit(int paramInt) { log.finest(""); Native.putInt(this.pData + 49L, paramInt); } 
/*  74 */   public int get_first_modmap_key() { log.finest(""); return Native.getInt(this.pData + 50L); } 
/*  75 */   public void set_first_modmap_key(int paramInt) { log.finest(""); Native.putInt(this.pData + 50L, paramInt); } 
/*  76 */   public int get_first_vmodmap_key() { log.finest(""); return Native.getInt(this.pData + 51L); } 
/*  77 */   public void set_first_vmodmap_key(int paramInt) { log.finest(""); Native.putInt(this.pData + 51L, paramInt); } 
/*  78 */   public int get_num_key_syms() { log.finest(""); return Native.getInt(this.pData + 52L); } 
/*  79 */   public void set_num_key_syms(int paramInt) { log.finest(""); Native.putInt(this.pData + 52L, paramInt); } 
/*  80 */   public int get_num_key_acts() { log.finest(""); return Native.getInt(this.pData + 56L); } 
/*  81 */   public void set_num_key_acts(int paramInt) { log.finest(""); Native.putInt(this.pData + 56L, paramInt); } 
/*  82 */   public int get_num_key_behaviors() { log.finest(""); return Native.getInt(this.pData + 60L); } 
/*  83 */   public void set_num_key_behaviors(int paramInt) { log.finest(""); Native.putInt(this.pData + 60L, paramInt); } 
/*  84 */   public int get_num_key_explicit() { log.finest(""); return Native.getInt(this.pData + 64L); } 
/*  85 */   public void set_num_key_explicit(int paramInt) { log.finest(""); Native.putInt(this.pData + 64L, paramInt); } 
/*  86 */   public int get_num_modmap_keys() { log.finest(""); return Native.getInt(this.pData + 68L); } 
/*  87 */   public void set_num_modmap_keys(int paramInt) { log.finest(""); Native.putInt(this.pData + 68L, paramInt); } 
/*  88 */   public int get_num_vmodmap_keys() { log.finest(""); return Native.getInt(this.pData + 72L); } 
/*  89 */   public void set_num_vmodmap_keys(int paramInt) { log.finest(""); Native.putInt(this.pData + 72L, paramInt); } 
/*  90 */   public int get_vmods() { log.finest(""); return Native.getInt(this.pData + 76L); } 
/*  91 */   public void set_vmods(int paramInt) { log.finest(""); Native.putInt(this.pData + 76L, paramInt); }
/*     */ 
/*     */   String getName()
/*     */   {
/*  95 */     return "XkbMapNotifyEvent";
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
/* 110 */     localStringBuilder.append("flags = ").append(get_flags()).append(", ");
/* 111 */     localStringBuilder.append("first_type = ").append(get_first_type()).append(", ");
/* 112 */     localStringBuilder.append("num_types = ").append(get_num_types()).append(", ");
/* 113 */     localStringBuilder.append("min_key_code = ").append(get_min_key_code()).append(", ");
/* 114 */     localStringBuilder.append("max_key_code = ").append(get_max_key_code()).append(", ");
/* 115 */     localStringBuilder.append("first_key_sym = ").append(get_first_key_sym()).append(", ");
/* 116 */     localStringBuilder.append("first_key_act = ").append(get_first_key_act()).append(", ");
/* 117 */     localStringBuilder.append("first_key_behavior = ").append(get_first_key_behavior()).append(", ");
/* 118 */     localStringBuilder.append("first_key_explicit = ").append(get_first_key_explicit()).append(", ");
/* 119 */     localStringBuilder.append("first_modmap_key = ").append(get_first_modmap_key()).append(", ");
/* 120 */     localStringBuilder.append("first_vmodmap_key = ").append(get_first_vmodmap_key()).append(", ");
/* 121 */     localStringBuilder.append("num_key_syms = ").append(get_num_key_syms()).append(", ");
/* 122 */     localStringBuilder.append("num_key_acts = ").append(get_num_key_acts()).append(", ");
/* 123 */     localStringBuilder.append("num_key_behaviors = ").append(get_num_key_behaviors()).append(", ");
/* 124 */     localStringBuilder.append("num_key_explicit = ").append(get_num_key_explicit()).append(", ");
/* 125 */     localStringBuilder.append("num_modmap_keys = ").append(get_num_modmap_keys()).append(", ");
/* 126 */     localStringBuilder.append("num_vmodmap_keys = ").append(get_num_vmodmap_keys()).append(", ");
/* 127 */     localStringBuilder.append("vmods = ").append(get_vmods()).append(", ");
/* 128 */     return localStringBuilder.toString();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XkbMapNotifyEvent
 * JD-Core Version:    0.6.2
 */