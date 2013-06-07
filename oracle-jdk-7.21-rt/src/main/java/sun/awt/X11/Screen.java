/*     */ package sun.awt.X11;
/*     */ 
/*     */ import sun.misc.Unsafe;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ public class Screen extends XWrapperBase
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
/*     */   public Screen(long paramLong) {
/*  20 */     log.finest("Creating");
/*  21 */     this.pData = paramLong;
/*  22 */     this.should_free_memory = false;
/*     */   }
/*     */ 
/*     */   public Screen()
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
/*  40 */   public XExtData get_ext_data(int paramInt) { log.finest(""); return Native.getLong(this.pData + 0L) != 0L ? new XExtData(Native.getLong(this.pData + 0L) + paramInt * 16) : null; } 
/*  41 */   public long get_ext_data() { log.finest(""); return Native.getLong(this.pData + 0L); } 
/*  42 */   public void set_ext_data(long paramLong) { log.finest(""); Native.putLong(this.pData + 0L, paramLong); } 
/*  43 */   public long get_display(int paramInt) { log.finest(""); return Native.getLong(this.pData + 4L) + paramInt * Native.getLongSize(); } 
/*  44 */   public long get_display() { log.finest(""); return Native.getLong(this.pData + 4L); } 
/*  45 */   public void set_display(long paramLong) { log.finest(""); Native.putLong(this.pData + 4L, paramLong); } 
/*  46 */   public long get_root() { log.finest(""); return Native.getLong(this.pData + 8L); } 
/*  47 */   public void set_root(long paramLong) { log.finest(""); Native.putLong(this.pData + 8L, paramLong); } 
/*  48 */   public int get_width() { log.finest(""); return Native.getInt(this.pData + 12L); } 
/*  49 */   public void set_width(int paramInt) { log.finest(""); Native.putInt(this.pData + 12L, paramInt); } 
/*  50 */   public int get_height() { log.finest(""); return Native.getInt(this.pData + 16L); } 
/*  51 */   public void set_height(int paramInt) { log.finest(""); Native.putInt(this.pData + 16L, paramInt); } 
/*  52 */   public int get_mwidth() { log.finest(""); return Native.getInt(this.pData + 20L); } 
/*  53 */   public void set_mwidth(int paramInt) { log.finest(""); Native.putInt(this.pData + 20L, paramInt); } 
/*  54 */   public int get_mheight() { log.finest(""); return Native.getInt(this.pData + 24L); } 
/*  55 */   public void set_mheight(int paramInt) { log.finest(""); Native.putInt(this.pData + 24L, paramInt); } 
/*  56 */   public int get_ndepths() { log.finest(""); return Native.getInt(this.pData + 28L); } 
/*  57 */   public void set_ndepths(int paramInt) { log.finest(""); Native.putInt(this.pData + 28L, paramInt); } 
/*  58 */   public Depth get_depths(int paramInt) { log.finest(""); return Native.getLong(this.pData + 32L) != 0L ? new Depth(Native.getLong(this.pData + 32L) + paramInt * 12) : null; } 
/*  59 */   public long get_depths() { log.finest(""); return Native.getLong(this.pData + 32L); } 
/*  60 */   public void set_depths(long paramLong) { log.finest(""); Native.putLong(this.pData + 32L, paramLong); } 
/*  61 */   public int get_root_depth() { log.finest(""); return Native.getInt(this.pData + 36L); } 
/*  62 */   public void set_root_depth(int paramInt) { log.finest(""); Native.putInt(this.pData + 36L, paramInt); } 
/*  63 */   public Visual get_root_visual(int paramInt) { log.finest(""); return Native.getLong(this.pData + 40L) != 0L ? new Visual(Native.getLong(this.pData + 40L) + paramInt * 32) : null; } 
/*  64 */   public long get_root_visual() { log.finest(""); return Native.getLong(this.pData + 40L); } 
/*  65 */   public void set_root_visual(long paramLong) { log.finest(""); Native.putLong(this.pData + 40L, paramLong); } 
/*  66 */   public long get_default_gc() { log.finest(""); return Native.getLong(this.pData + 44L); } 
/*  67 */   public void set_default_gc(long paramLong) { log.finest(""); Native.putLong(this.pData + 44L, paramLong); } 
/*  68 */   public long get_cmap() { log.finest(""); return Native.getLong(this.pData + 48L); } 
/*  69 */   public void set_cmap(long paramLong) { log.finest(""); Native.putLong(this.pData + 48L, paramLong); } 
/*  70 */   public long get_white_pixel() { log.finest(""); return Native.getLong(this.pData + 52L); } 
/*  71 */   public void set_white_pixel(long paramLong) { log.finest(""); Native.putLong(this.pData + 52L, paramLong); } 
/*  72 */   public long get_black_pixel() { log.finest(""); return Native.getLong(this.pData + 56L); } 
/*  73 */   public void set_black_pixel(long paramLong) { log.finest(""); Native.putLong(this.pData + 56L, paramLong); } 
/*  74 */   public int get_max_maps() { log.finest(""); return Native.getInt(this.pData + 60L); } 
/*  75 */   public void set_max_maps(int paramInt) { log.finest(""); Native.putInt(this.pData + 60L, paramInt); } 
/*  76 */   public int get_min_maps() { log.finest(""); return Native.getInt(this.pData + 64L); } 
/*  77 */   public void set_min_maps(int paramInt) { log.finest(""); Native.putInt(this.pData + 64L, paramInt); } 
/*  78 */   public int get_backing_store() { log.finest(""); return Native.getInt(this.pData + 68L); } 
/*  79 */   public void set_backing_store(int paramInt) { log.finest(""); Native.putInt(this.pData + 68L, paramInt); } 
/*  80 */   public boolean get_save_unders() { log.finest(""); return Native.getBool(this.pData + 72L); } 
/*  81 */   public void set_save_unders(boolean paramBoolean) { log.finest(""); Native.putBool(this.pData + 72L, paramBoolean); } 
/*  82 */   public long get_root_input_mask() { log.finest(""); return Native.getLong(this.pData + 76L); } 
/*  83 */   public void set_root_input_mask(long paramLong) { log.finest(""); Native.putLong(this.pData + 76L, paramLong); }
/*     */ 
/*     */   String getName()
/*     */   {
/*  87 */     return "Screen";
/*     */   }
/*     */ 
/*     */   String getFieldsAsString()
/*     */   {
/*  92 */     StringBuilder localStringBuilder = new StringBuilder(800);
/*     */ 
/*  94 */     localStringBuilder.append("ext_data = ").append(get_ext_data()).append(", ");
/*  95 */     localStringBuilder.append("display = ").append(get_display()).append(", ");
/*  96 */     localStringBuilder.append("root = ").append(get_root()).append(", ");
/*  97 */     localStringBuilder.append("width = ").append(get_width()).append(", ");
/*  98 */     localStringBuilder.append("height = ").append(get_height()).append(", ");
/*  99 */     localStringBuilder.append("mwidth = ").append(get_mwidth()).append(", ");
/* 100 */     localStringBuilder.append("mheight = ").append(get_mheight()).append(", ");
/* 101 */     localStringBuilder.append("ndepths = ").append(get_ndepths()).append(", ");
/* 102 */     localStringBuilder.append("depths = ").append(get_depths()).append(", ");
/* 103 */     localStringBuilder.append("root_depth = ").append(get_root_depth()).append(", ");
/* 104 */     localStringBuilder.append("root_visual = ").append(get_root_visual()).append(", ");
/* 105 */     localStringBuilder.append("default_gc = ").append(get_default_gc()).append(", ");
/* 106 */     localStringBuilder.append("cmap = ").append(get_cmap()).append(", ");
/* 107 */     localStringBuilder.append("white_pixel = ").append(get_white_pixel()).append(", ");
/* 108 */     localStringBuilder.append("black_pixel = ").append(get_black_pixel()).append(", ");
/* 109 */     localStringBuilder.append("max_maps = ").append(get_max_maps()).append(", ");
/* 110 */     localStringBuilder.append("min_maps = ").append(get_min_maps()).append(", ");
/* 111 */     localStringBuilder.append("backing_store = ").append(get_backing_store()).append(", ");
/* 112 */     localStringBuilder.append("save_unders = ").append(get_save_unders()).append(", ");
/* 113 */     localStringBuilder.append("root_input_mask = ").append(get_root_input_mask()).append(", ");
/* 114 */     return localStringBuilder.toString();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.Screen
 * JD-Core Version:    0.6.2
 */