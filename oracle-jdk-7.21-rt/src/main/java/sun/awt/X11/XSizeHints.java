/*     */ package sun.awt.X11;
/*     */ 
/*     */ import sun.misc.Unsafe;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ public class XSizeHints extends XWrapperBase
/*     */ {
/*   9 */   private Unsafe unsafe = XlibWrapper.unsafe;
/*     */   private final boolean should_free_memory;
/*     */   long pData;
/*     */ 
/*     */   public static int getSize()
/*     */   {
/*  11 */     return 72; } 
/*  12 */   public int getDataSize() { return getSize(); }
/*     */ 
/*     */   public long getPData()
/*     */   {
/*  16 */     return this.pData;
/*     */   }
/*     */ 
/*     */   public XSizeHints(long paramLong) {
/*  20 */     log.finest("Creating");
/*  21 */     this.pData = paramLong;
/*  22 */     this.should_free_memory = false;
/*     */   }
/*     */ 
/*     */   public XSizeHints()
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
/*  40 */   public long get_flags() { log.finest(""); return Native.getLong(this.pData + 0L); } 
/*  41 */   public void set_flags(long paramLong) { log.finest(""); Native.putLong(this.pData + 0L, paramLong); } 
/*  42 */   public int get_x() { log.finest(""); return Native.getInt(this.pData + 4L); } 
/*  43 */   public void set_x(int paramInt) { log.finest(""); Native.putInt(this.pData + 4L, paramInt); } 
/*  44 */   public int get_y() { log.finest(""); return Native.getInt(this.pData + 8L); } 
/*  45 */   public void set_y(int paramInt) { log.finest(""); Native.putInt(this.pData + 8L, paramInt); } 
/*  46 */   public int get_width() { log.finest(""); return Native.getInt(this.pData + 12L); } 
/*  47 */   public void set_width(int paramInt) { log.finest(""); Native.putInt(this.pData + 12L, paramInt); } 
/*  48 */   public int get_height() { log.finest(""); return Native.getInt(this.pData + 16L); } 
/*  49 */   public void set_height(int paramInt) { log.finest(""); Native.putInt(this.pData + 16L, paramInt); } 
/*  50 */   public int get_min_width() { log.finest(""); return Native.getInt(this.pData + 20L); } 
/*  51 */   public void set_min_width(int paramInt) { log.finest(""); Native.putInt(this.pData + 20L, paramInt); } 
/*  52 */   public int get_min_height() { log.finest(""); return Native.getInt(this.pData + 24L); } 
/*  53 */   public void set_min_height(int paramInt) { log.finest(""); Native.putInt(this.pData + 24L, paramInt); } 
/*  54 */   public int get_max_width() { log.finest(""); return Native.getInt(this.pData + 28L); } 
/*  55 */   public void set_max_width(int paramInt) { log.finest(""); Native.putInt(this.pData + 28L, paramInt); } 
/*  56 */   public int get_max_height() { log.finest(""); return Native.getInt(this.pData + 32L); } 
/*  57 */   public void set_max_height(int paramInt) { log.finest(""); Native.putInt(this.pData + 32L, paramInt); } 
/*  58 */   public int get_width_inc() { log.finest(""); return Native.getInt(this.pData + 36L); } 
/*  59 */   public void set_width_inc(int paramInt) { log.finest(""); Native.putInt(this.pData + 36L, paramInt); } 
/*  60 */   public int get_height_inc() { log.finest(""); return Native.getInt(this.pData + 40L); } 
/*  61 */   public void set_height_inc(int paramInt) { log.finest(""); Native.putInt(this.pData + 40L, paramInt); } 
/*  62 */   public int get_min_aspect_x() { log.finest(""); return Native.getInt(this.pData + 44L); } 
/*  63 */   public void set_min_aspect_x(int paramInt) { log.finest(""); Native.putInt(this.pData + 44L, paramInt); } 
/*  64 */   public int get_min_aspect_y() { log.finest(""); return Native.getInt(this.pData + 48L); } 
/*  65 */   public void set_min_aspect_y(int paramInt) { log.finest(""); Native.putInt(this.pData + 48L, paramInt); } 
/*  66 */   public int get_max_aspect_x() { log.finest(""); return Native.getInt(this.pData + 52L); } 
/*  67 */   public void set_max_aspect_x(int paramInt) { log.finest(""); Native.putInt(this.pData + 52L, paramInt); } 
/*  68 */   public int get_max_aspect_y() { log.finest(""); return Native.getInt(this.pData + 56L); } 
/*  69 */   public void set_max_aspect_y(int paramInt) { log.finest(""); Native.putInt(this.pData + 56L, paramInt); } 
/*  70 */   public int get_base_width() { log.finest(""); return Native.getInt(this.pData + 60L); } 
/*  71 */   public void set_base_width(int paramInt) { log.finest(""); Native.putInt(this.pData + 60L, paramInt); } 
/*  72 */   public int get_base_height() { log.finest(""); return Native.getInt(this.pData + 64L); } 
/*  73 */   public void set_base_height(int paramInt) { log.finest(""); Native.putInt(this.pData + 64L, paramInt); } 
/*  74 */   public int get_win_gravity() { log.finest(""); return Native.getInt(this.pData + 68L); } 
/*  75 */   public void set_win_gravity(int paramInt) { log.finest(""); Native.putInt(this.pData + 68L, paramInt); }
/*     */ 
/*     */   String getName()
/*     */   {
/*  79 */     return "XSizeHints";
/*     */   }
/*     */ 
/*     */   String getFieldsAsString()
/*     */   {
/*  84 */     StringBuilder localStringBuilder = new StringBuilder(720);
/*     */ 
/*  86 */     localStringBuilder.append("flags = ").append(get_flags()).append(", ");
/*  87 */     localStringBuilder.append("x = ").append(get_x()).append(", ");
/*  88 */     localStringBuilder.append("y = ").append(get_y()).append(", ");
/*  89 */     localStringBuilder.append("width = ").append(get_width()).append(", ");
/*  90 */     localStringBuilder.append("height = ").append(get_height()).append(", ");
/*  91 */     localStringBuilder.append("min_width = ").append(get_min_width()).append(", ");
/*  92 */     localStringBuilder.append("min_height = ").append(get_min_height()).append(", ");
/*  93 */     localStringBuilder.append("max_width = ").append(get_max_width()).append(", ");
/*  94 */     localStringBuilder.append("max_height = ").append(get_max_height()).append(", ");
/*  95 */     localStringBuilder.append("width_inc = ").append(get_width_inc()).append(", ");
/*  96 */     localStringBuilder.append("height_inc = ").append(get_height_inc()).append(", ");
/*  97 */     localStringBuilder.append("min_aspect_x = ").append(get_min_aspect_x()).append(", ");
/*  98 */     localStringBuilder.append("min_aspect_y = ").append(get_min_aspect_y()).append(", ");
/*  99 */     localStringBuilder.append("max_aspect_x = ").append(get_max_aspect_x()).append(", ");
/* 100 */     localStringBuilder.append("max_aspect_y = ").append(get_max_aspect_y()).append(", ");
/* 101 */     localStringBuilder.append("base_width = ").append(get_base_width()).append(", ");
/* 102 */     localStringBuilder.append("base_height = ").append(get_base_height()).append(", ");
/* 103 */     localStringBuilder.append("win_gravity = ").append(get_win_gravity()).append(", ");
/* 104 */     return localStringBuilder.toString();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XSizeHints
 * JD-Core Version:    0.6.2
 */