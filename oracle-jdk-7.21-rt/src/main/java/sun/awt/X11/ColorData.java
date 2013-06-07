/*    */ package sun.awt.X11;
/*    */ 
/*    */ import sun.misc.Unsafe;
/*    */ import sun.util.logging.PlatformLogger;
/*    */ 
/*    */ public class ColorData extends XWrapperBase
/*    */ {
/*  9 */   private Unsafe unsafe = XlibWrapper.unsafe;
/*    */   private final boolean should_free_memory;
/*    */   long pData;
/*    */ 
/*    */   public static int getSize()
/*    */   {
/* 11 */     return 44; } 
/* 12 */   public int getDataSize() { return getSize(); }
/*    */ 
/*    */   public long getPData()
/*    */   {
/* 16 */     return this.pData;
/*    */   }
/*    */ 
/*    */   public ColorData(long paramLong) {
/* 20 */     log.finest("Creating");
/* 21 */     this.pData = paramLong;
/* 22 */     this.should_free_memory = false;
/*    */   }
/*    */ 
/*    */   public ColorData()
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
/* 40 */   public ColorEntry get_awt_Colors(int paramInt) { log.finest(""); return Native.getLong(this.pData + 0L) != 0L ? new ColorEntry(Native.getLong(this.pData + 0L) + paramInt * 4) : null; } 
/* 41 */   public long get_awt_Colors() { log.finest(""); return Native.getLong(this.pData + 0L); } 
/* 42 */   public void set_awt_Colors(long paramLong) { log.finest(""); Native.putLong(this.pData + 0L, paramLong); } 
/* 43 */   public int get_awt_numICMcolors() { log.finest(""); return Native.getInt(this.pData + 4L); } 
/* 44 */   public void set_awt_numICMcolors(int paramInt) { log.finest(""); Native.putInt(this.pData + 4L, paramInt); } 
/* 45 */   public int get_awt_icmLUT(int paramInt) { log.finest(""); return Native.getInt(Native.getLong(this.pData + 8L) + paramInt * 4); } 
/* 46 */   public long get_awt_icmLUT() { log.finest(""); return Native.getLong(this.pData + 8L); } 
/* 47 */   public void set_awt_icmLUT(long paramLong) { log.finest(""); Native.putLong(this.pData + 8L, paramLong); } 
/* 48 */   public byte get_awt_icmLUT2Colors(int paramInt) { log.finest(""); return Native.getByte(Native.getLong(this.pData + 12L) + paramInt * 1); } 
/* 49 */   public long get_awt_icmLUT2Colors() { log.finest(""); return Native.getLong(this.pData + 12L); } 
/* 50 */   public void set_awt_icmLUT2Colors(long paramLong) { log.finest(""); Native.putLong(this.pData + 12L, paramLong); } 
/* 51 */   public byte get_img_grays(int paramInt) { log.finest(""); return Native.getByte(Native.getLong(this.pData + 16L) + paramInt * 1); } 
/* 52 */   public long get_img_grays() { log.finest(""); return Native.getLong(this.pData + 16L); } 
/* 53 */   public void set_img_grays(long paramLong) { log.finest(""); Native.putLong(this.pData + 16L, paramLong); } 
/* 54 */   public byte get_img_clr_tbl(int paramInt) { log.finest(""); return Native.getByte(Native.getLong(this.pData + 20L) + paramInt * 1); } 
/* 55 */   public long get_img_clr_tbl() { log.finest(""); return Native.getLong(this.pData + 20L); } 
/* 56 */   public void set_img_clr_tbl(long paramLong) { log.finest(""); Native.putLong(this.pData + 20L, paramLong); } 
/* 57 */   public byte get_img_oda_red(int paramInt) { log.finest(""); return Native.getByte(Native.getLong(this.pData + 24L) + paramInt * 1); } 
/* 58 */   public long get_img_oda_red() { log.finest(""); return Native.getLong(this.pData + 24L); } 
/* 59 */   public void set_img_oda_red(long paramLong) { log.finest(""); Native.putLong(this.pData + 24L, paramLong); } 
/* 60 */   public byte get_img_oda_green(int paramInt) { log.finest(""); return Native.getByte(Native.getLong(this.pData + 28L) + paramInt * 1); } 
/* 61 */   public long get_img_oda_green() { log.finest(""); return Native.getLong(this.pData + 28L); } 
/* 62 */   public void set_img_oda_green(long paramLong) { log.finest(""); Native.putLong(this.pData + 28L, paramLong); } 
/* 63 */   public byte get_img_oda_blue(int paramInt) { log.finest(""); return Native.getByte(Native.getLong(this.pData + 32L) + paramInt * 1); } 
/* 64 */   public long get_img_oda_blue() { log.finest(""); return Native.getLong(this.pData + 32L); } 
/* 65 */   public void set_img_oda_blue(long paramLong) { log.finest(""); Native.putLong(this.pData + 32L, paramLong); } 
/* 66 */   public int get_pGrayInverseLutData(int paramInt) { log.finest(""); return Native.getInt(Native.getLong(this.pData + 36L) + paramInt * 4); } 
/* 67 */   public long get_pGrayInverseLutData() { log.finest(""); return Native.getLong(this.pData + 36L); } 
/* 68 */   public void set_pGrayInverseLutData(long paramLong) { log.finest(""); Native.putLong(this.pData + 36L, paramLong); } 
/* 69 */   public int get_screendata() { log.finest(""); return Native.getInt(this.pData + 40L); } 
/* 70 */   public void set_screendata(int paramInt) { log.finest(""); Native.putInt(this.pData + 40L, paramInt); }
/*    */ 
/*    */   String getName()
/*    */   {
/* 74 */     return "ColorData";
/*    */   }
/*    */ 
/*    */   String getFieldsAsString()
/*    */   {
/* 79 */     StringBuilder localStringBuilder = new StringBuilder(440);
/*    */ 
/* 81 */     localStringBuilder.append("awt_Colors = ").append(get_awt_Colors()).append(", ");
/* 82 */     localStringBuilder.append("awt_numICMcolors = ").append(get_awt_numICMcolors()).append(", ");
/* 83 */     localStringBuilder.append("awt_icmLUT = ").append(get_awt_icmLUT()).append(", ");
/* 84 */     localStringBuilder.append("awt_icmLUT2Colors = ").append(get_awt_icmLUT2Colors()).append(", ");
/* 85 */     localStringBuilder.append("img_grays = ").append(get_img_grays()).append(", ");
/* 86 */     localStringBuilder.append("img_clr_tbl = ").append(get_img_clr_tbl()).append(", ");
/* 87 */     localStringBuilder.append("img_oda_red = ").append(get_img_oda_red()).append(", ");
/* 88 */     localStringBuilder.append("img_oda_green = ").append(get_img_oda_green()).append(", ");
/* 89 */     localStringBuilder.append("img_oda_blue = ").append(get_img_oda_blue()).append(", ");
/* 90 */     localStringBuilder.append("pGrayInverseLutData = ").append(get_pGrayInverseLutData()).append(", ");
/* 91 */     localStringBuilder.append("screendata = ").append(get_screendata()).append(", ");
/* 92 */     return localStringBuilder.toString();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.ColorData
 * JD-Core Version:    0.6.2
 */