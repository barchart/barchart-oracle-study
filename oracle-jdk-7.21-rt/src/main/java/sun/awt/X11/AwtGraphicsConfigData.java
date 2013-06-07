/*     */ package sun.awt.X11;
/*     */ 
/*     */ import sun.misc.Unsafe;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ public class AwtGraphicsConfigData extends XWrapperBase
/*     */ {
/*   9 */   private Unsafe unsafe = XlibWrapper.unsafe;
/*     */   private final boolean should_free_memory;
/*     */   long pData;
/*     */ 
/*     */   public static int getSize()
/*     */   {
/*  11 */     return 128; } 
/*  12 */   public int getDataSize() { return getSize(); }
/*     */ 
/*     */   public long getPData()
/*     */   {
/*  16 */     return this.pData;
/*     */   }
/*     */ 
/*     */   public AwtGraphicsConfigData(long paramLong) {
/*  20 */     log.finest("Creating");
/*  21 */     this.pData = paramLong;
/*  22 */     this.should_free_memory = false;
/*     */   }
/*     */ 
/*     */   public AwtGraphicsConfigData()
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
/*  40 */   public int get_awt_depth() { log.finest(""); return Native.getInt(this.pData + 0L); } 
/*  41 */   public void set_awt_depth(int paramInt) { log.finest(""); Native.putInt(this.pData + 0L, paramInt); } 
/*  42 */   public long get_awt_cmap() { log.finest(""); return Native.getLong(this.pData + 4L); } 
/*  43 */   public void set_awt_cmap(long paramLong) { log.finest(""); Native.putLong(this.pData + 4L, paramLong); } 
/*  44 */   public XVisualInfo get_awt_visInfo() { log.finest(""); return new XVisualInfo(this.pData + 8L); } 
/*  45 */   public int get_awt_num_colors() { log.finest(""); return Native.getInt(this.pData + 48L); } 
/*  46 */   public void set_awt_num_colors(int paramInt) { log.finest(""); Native.putInt(this.pData + 48L, paramInt); } 
/*  47 */   public awtImageData get_awtImage(int paramInt) { log.finest(""); return Native.getLong(this.pData + 52L) != 0L ? new awtImageData(Native.getLong(this.pData + 52L) + paramInt * 304) : null; } 
/*  48 */   public long get_awtImage() { log.finest(""); return Native.getLong(this.pData + 52L); } 
/*  49 */   public void set_awtImage(long paramLong) { log.finest(""); Native.putLong(this.pData + 52L, paramLong); } 
/*  50 */   public long get_AwtColorMatch(int paramInt) { log.finest(""); return Native.getLong(this.pData + 56L) + paramInt * Native.getLongSize(); } 
/*  51 */   public long get_AwtColorMatch() { log.finest(""); return Native.getLong(this.pData + 56L); } 
/*  52 */   public void set_AwtColorMatch(long paramLong) { log.finest(""); Native.putLong(this.pData + 56L, paramLong); } 
/*  53 */   public long get_monoImage(int paramInt) { log.finest(""); return Native.getLong(this.pData + 60L) + paramInt * Native.getLongSize(); } 
/*  54 */   public long get_monoImage() { log.finest(""); return Native.getLong(this.pData + 60L); } 
/*  55 */   public void set_monoImage(long paramLong) { log.finest(""); Native.putLong(this.pData + 60L, paramLong); } 
/*  56 */   public long get_monoPixmap() { log.finest(""); return Native.getLong(this.pData + 64L); } 
/*  57 */   public void set_monoPixmap(long paramLong) { log.finest(""); Native.putLong(this.pData + 64L, paramLong); } 
/*  58 */   public int get_monoPixmapWidth() { log.finest(""); return Native.getInt(this.pData + 68L); } 
/*  59 */   public void set_monoPixmapWidth(int paramInt) { log.finest(""); Native.putInt(this.pData + 68L, paramInt); } 
/*  60 */   public int get_monoPixmapHeight() { log.finest(""); return Native.getInt(this.pData + 72L); } 
/*  61 */   public void set_monoPixmapHeight(int paramInt) { log.finest(""); Native.putInt(this.pData + 72L, paramInt); } 
/*  62 */   public long get_monoPixmapGC() { log.finest(""); return Native.getLong(this.pData + 76L); } 
/*  63 */   public void set_monoPixmapGC(long paramLong) { log.finest(""); Native.putLong(this.pData + 76L, paramLong); } 
/*  64 */   public int get_pixelStride() { log.finest(""); return Native.getInt(this.pData + 80L); } 
/*  65 */   public void set_pixelStride(int paramInt) { log.finest(""); Native.putInt(this.pData + 80L, paramInt); } 
/*  66 */   public ColorData get_color_data(int paramInt) { log.finest(""); return Native.getLong(this.pData + 84L) != 0L ? new ColorData(Native.getLong(this.pData + 84L) + paramInt * 44) : null; } 
/*  67 */   public long get_color_data() { log.finest(""); return Native.getLong(this.pData + 84L); } 
/*  68 */   public void set_color_data(long paramLong) { log.finest(""); Native.putLong(this.pData + 84L, paramLong); } 
/*  69 */   public long get_glxInfo(int paramInt) { log.finest(""); return Native.getLong(this.pData + 88L) + paramInt * Native.getLongSize(); } 
/*  70 */   public long get_glxInfo() { log.finest(""); return Native.getLong(this.pData + 88L); } 
/*  71 */   public void set_glxInfo(long paramLong) { log.finest(""); Native.putLong(this.pData + 88L, paramLong); } 
/*  72 */   public int get_isTranslucencySupported() { log.finest(""); return Native.getInt(this.pData + 92L); } 
/*  73 */   public void set_isTranslucencySupported(int paramInt) { log.finest(""); Native.putInt(this.pData + 92L, paramInt); } 
/*  74 */   public XRenderPictFormat get_renderPictFormat() { log.finest(""); return new XRenderPictFormat(this.pData + 96L); }
/*     */ 
/*     */   String getName()
/*     */   {
/*  78 */     return "AwtGraphicsConfigData";
/*     */   }
/*     */ 
/*     */   String getFieldsAsString()
/*     */   {
/*  83 */     StringBuilder localStringBuilder = new StringBuilder(640);
/*     */ 
/*  85 */     localStringBuilder.append("awt_depth = ").append(get_awt_depth()).append(", ");
/*  86 */     localStringBuilder.append("awt_cmap = ").append(get_awt_cmap()).append(", ");
/*  87 */     localStringBuilder.append("awt_visInfo = ").append(get_awt_visInfo()).append(", ");
/*  88 */     localStringBuilder.append("awt_num_colors = ").append(get_awt_num_colors()).append(", ");
/*  89 */     localStringBuilder.append("awtImage = ").append(get_awtImage()).append(", ");
/*  90 */     localStringBuilder.append("AwtColorMatch = ").append(get_AwtColorMatch()).append(", ");
/*  91 */     localStringBuilder.append("monoImage = ").append(get_monoImage()).append(", ");
/*  92 */     localStringBuilder.append("monoPixmap = ").append(get_monoPixmap()).append(", ");
/*  93 */     localStringBuilder.append("monoPixmapWidth = ").append(get_monoPixmapWidth()).append(", ");
/*  94 */     localStringBuilder.append("monoPixmapHeight = ").append(get_monoPixmapHeight()).append(", ");
/*  95 */     localStringBuilder.append("monoPixmapGC = ").append(get_monoPixmapGC()).append(", ");
/*  96 */     localStringBuilder.append("pixelStride = ").append(get_pixelStride()).append(", ");
/*  97 */     localStringBuilder.append("color_data = ").append(get_color_data()).append(", ");
/*  98 */     localStringBuilder.append("glxInfo = ").append(get_glxInfo()).append(", ");
/*  99 */     localStringBuilder.append("isTranslucencySupported = ").append(get_isTranslucencySupported()).append(", ");
/* 100 */     localStringBuilder.append("renderPictFormat = ").append(get_renderPictFormat()).append(", ");
/* 101 */     return localStringBuilder.toString();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.AwtGraphicsConfigData
 * JD-Core Version:    0.6.2
 */