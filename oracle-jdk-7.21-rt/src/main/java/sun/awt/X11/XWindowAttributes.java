/*     */ package sun.awt.X11;
/*     */ 
/*     */ import sun.misc.Unsafe;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ public class XWindowAttributes extends XWrapperBase
/*     */ {
/*   9 */   private Unsafe unsafe = XlibWrapper.unsafe;
/*     */   private final boolean should_free_memory;
/*     */   long pData;
/*     */ 
/*     */   public static int getSize()
/*     */   {
/*  11 */     return 92; } 
/*  12 */   public int getDataSize() { return getSize(); }
/*     */ 
/*     */   public long getPData()
/*     */   {
/*  16 */     return this.pData;
/*     */   }
/*     */ 
/*     */   public XWindowAttributes(long paramLong) {
/*  20 */     log.finest("Creating");
/*  21 */     this.pData = paramLong;
/*  22 */     this.should_free_memory = false;
/*     */   }
/*     */ 
/*     */   public XWindowAttributes()
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
/*  40 */   public int get_x() { log.finest(""); return Native.getInt(this.pData + 0L); } 
/*  41 */   public void set_x(int paramInt) { log.finest(""); Native.putInt(this.pData + 0L, paramInt); } 
/*  42 */   public int get_y() { log.finest(""); return Native.getInt(this.pData + 4L); } 
/*  43 */   public void set_y(int paramInt) { log.finest(""); Native.putInt(this.pData + 4L, paramInt); } 
/*  44 */   public int get_width() { log.finest(""); return Native.getInt(this.pData + 8L); } 
/*  45 */   public void set_width(int paramInt) { log.finest(""); Native.putInt(this.pData + 8L, paramInt); } 
/*  46 */   public int get_height() { log.finest(""); return Native.getInt(this.pData + 12L); } 
/*  47 */   public void set_height(int paramInt) { log.finest(""); Native.putInt(this.pData + 12L, paramInt); } 
/*  48 */   public int get_border_width() { log.finest(""); return Native.getInt(this.pData + 16L); } 
/*  49 */   public void set_border_width(int paramInt) { log.finest(""); Native.putInt(this.pData + 16L, paramInt); } 
/*  50 */   public int get_depth() { log.finest(""); return Native.getInt(this.pData + 20L); } 
/*  51 */   public void set_depth(int paramInt) { log.finest(""); Native.putInt(this.pData + 20L, paramInt); } 
/*  52 */   public Visual get_visual(int paramInt) { log.finest(""); return Native.getLong(this.pData + 24L) != 0L ? new Visual(Native.getLong(this.pData + 24L) + paramInt * 32) : null; } 
/*  53 */   public long get_visual() { log.finest(""); return Native.getLong(this.pData + 24L); } 
/*  54 */   public void set_visual(long paramLong) { log.finest(""); Native.putLong(this.pData + 24L, paramLong); } 
/*  55 */   public long get_root() { log.finest(""); return Native.getLong(this.pData + 28L); } 
/*  56 */   public void set_root(long paramLong) { log.finest(""); Native.putLong(this.pData + 28L, paramLong); } 
/*  57 */   public int get_class() { log.finest(""); return Native.getInt(this.pData + 32L); } 
/*  58 */   public void set_class(int paramInt) { log.finest(""); Native.putInt(this.pData + 32L, paramInt); } 
/*  59 */   public int get_bit_gravity() { log.finest(""); return Native.getInt(this.pData + 36L); } 
/*  60 */   public void set_bit_gravity(int paramInt) { log.finest(""); Native.putInt(this.pData + 36L, paramInt); } 
/*  61 */   public int get_win_gravity() { log.finest(""); return Native.getInt(this.pData + 40L); } 
/*  62 */   public void set_win_gravity(int paramInt) { log.finest(""); Native.putInt(this.pData + 40L, paramInt); } 
/*  63 */   public int get_backing_store() { log.finest(""); return Native.getInt(this.pData + 44L); } 
/*  64 */   public void set_backing_store(int paramInt) { log.finest(""); Native.putInt(this.pData + 44L, paramInt); } 
/*  65 */   public long get_backing_planes() { log.finest(""); return Native.getLong(this.pData + 48L); } 
/*  66 */   public void set_backing_planes(long paramLong) { log.finest(""); Native.putLong(this.pData + 48L, paramLong); } 
/*  67 */   public long get_backing_pixel() { log.finest(""); return Native.getLong(this.pData + 52L); } 
/*  68 */   public void set_backing_pixel(long paramLong) { log.finest(""); Native.putLong(this.pData + 52L, paramLong); } 
/*  69 */   public boolean get_save_under() { log.finest(""); return Native.getBool(this.pData + 56L); } 
/*  70 */   public void set_save_under(boolean paramBoolean) { log.finest(""); Native.putBool(this.pData + 56L, paramBoolean); } 
/*  71 */   public long get_colormap() { log.finest(""); return Native.getLong(this.pData + 60L); } 
/*  72 */   public void set_colormap(long paramLong) { log.finest(""); Native.putLong(this.pData + 60L, paramLong); } 
/*  73 */   public boolean get_map_installed() { log.finest(""); return Native.getBool(this.pData + 64L); } 
/*  74 */   public void set_map_installed(boolean paramBoolean) { log.finest(""); Native.putBool(this.pData + 64L, paramBoolean); } 
/*  75 */   public int get_map_state() { log.finest(""); return Native.getInt(this.pData + 68L); } 
/*  76 */   public void set_map_state(int paramInt) { log.finest(""); Native.putInt(this.pData + 68L, paramInt); } 
/*  77 */   public long get_all_event_masks() { log.finest(""); return Native.getLong(this.pData + 72L); } 
/*  78 */   public void set_all_event_masks(long paramLong) { log.finest(""); Native.putLong(this.pData + 72L, paramLong); } 
/*  79 */   public long get_your_event_mask() { log.finest(""); return Native.getLong(this.pData + 76L); } 
/*  80 */   public void set_your_event_mask(long paramLong) { log.finest(""); Native.putLong(this.pData + 76L, paramLong); } 
/*  81 */   public long get_do_not_propagate_mask() { log.finest(""); return Native.getLong(this.pData + 80L); } 
/*  82 */   public void set_do_not_propagate_mask(long paramLong) { log.finest(""); Native.putLong(this.pData + 80L, paramLong); } 
/*  83 */   public boolean get_override_redirect() { log.finest(""); return Native.getBool(this.pData + 84L); } 
/*  84 */   public void set_override_redirect(boolean paramBoolean) { log.finest(""); Native.putBool(this.pData + 84L, paramBoolean); } 
/*  85 */   public Screen get_screen(int paramInt) { log.finest(""); return Native.getLong(this.pData + 88L) != 0L ? new Screen(Native.getLong(this.pData + 88L) + paramInt * 80) : null; } 
/*  86 */   public long get_screen() { log.finest(""); return Native.getLong(this.pData + 88L); } 
/*  87 */   public void set_screen(long paramLong) { log.finest(""); Native.putLong(this.pData + 88L, paramLong); }
/*     */ 
/*     */   String getName()
/*     */   {
/*  91 */     return "XWindowAttributes";
/*     */   }
/*     */ 
/*     */   String getFieldsAsString()
/*     */   {
/*  96 */     StringBuilder localStringBuilder = new StringBuilder(920);
/*     */ 
/*  98 */     localStringBuilder.append("x = ").append(get_x()).append(", ");
/*  99 */     localStringBuilder.append("y = ").append(get_y()).append(", ");
/* 100 */     localStringBuilder.append("width = ").append(get_width()).append(", ");
/* 101 */     localStringBuilder.append("height = ").append(get_height()).append(", ");
/* 102 */     localStringBuilder.append("border_width = ").append(get_border_width()).append(", ");
/* 103 */     localStringBuilder.append("depth = ").append(get_depth()).append(", ");
/* 104 */     localStringBuilder.append("visual = ").append(get_visual()).append(", ");
/* 105 */     localStringBuilder.append("root = ").append(get_root()).append(", ");
/* 106 */     localStringBuilder.append("class = ").append(get_class()).append(", ");
/* 107 */     localStringBuilder.append("bit_gravity = ").append(get_bit_gravity()).append(", ");
/* 108 */     localStringBuilder.append("win_gravity = ").append(get_win_gravity()).append(", ");
/* 109 */     localStringBuilder.append("backing_store = ").append(get_backing_store()).append(", ");
/* 110 */     localStringBuilder.append("backing_planes = ").append(get_backing_planes()).append(", ");
/* 111 */     localStringBuilder.append("backing_pixel = ").append(get_backing_pixel()).append(", ");
/* 112 */     localStringBuilder.append("save_under = ").append(get_save_under()).append(", ");
/* 113 */     localStringBuilder.append("colormap = ").append(get_colormap()).append(", ");
/* 114 */     localStringBuilder.append("map_installed = ").append(get_map_installed()).append(", ");
/* 115 */     localStringBuilder.append("map_state = ").append(get_map_state()).append(", ");
/* 116 */     localStringBuilder.append("all_event_masks = ").append(get_all_event_masks()).append(", ");
/* 117 */     localStringBuilder.append("your_event_mask = ").append(get_your_event_mask()).append(", ");
/* 118 */     localStringBuilder.append("do_not_propagate_mask = ").append(get_do_not_propagate_mask()).append(", ");
/* 119 */     localStringBuilder.append("override_redirect = ").append(get_override_redirect()).append(", ");
/* 120 */     localStringBuilder.append("screen = ").append(get_screen()).append(", ");
/* 121 */     return localStringBuilder.toString();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XWindowAttributes
 * JD-Core Version:    0.6.2
 */