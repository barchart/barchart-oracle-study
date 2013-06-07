/*     */ package sun.awt;
/*     */ 
/*     */ import com.sun.java.swing.plaf.gtk.GTKConstants.TextDirection;
/*     */ import java.awt.RenderingHints;
/*     */ import java.awt.color.ColorSpace;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.ComponentColorModel;
/*     */ import java.awt.image.DataBufferByte;
/*     */ import java.awt.image.Raster;
/*     */ import java.awt.image.WritableRaster;
/*     */ import java.security.AccessController;
/*     */ import java.util.Map;
/*     */ import sun.java2d.opengl.OGLRenderQueue;
/*     */ import sun.security.action.GetIntegerAction;
/*     */ 
/*     */ public abstract class UNIXToolkit extends SunToolkit
/*     */ {
/*  41 */   public static final Object GTK_LOCK = new Object();
/*     */ 
/*  43 */   private static final int[] BAND_OFFSETS = { 0, 1, 2 };
/*  44 */   private static final int[] BAND_OFFSETS_ALPHA = { 0, 1, 2, 3 };
/*     */   private static final int DEFAULT_DATATRANSFER_TIMEOUT = 10000;
/*     */   private Boolean nativeGTKAvailable;
/*     */   private Boolean nativeGTKLoaded;
/*  49 */   private BufferedImage tmpImage = null;
/*     */   public static final String FONTCONFIGAAHINT = "fontconfig/Antialias";
/*     */ 
/*     */   public static int getDatatransferTimeout()
/*     */   {
/*  52 */     Integer localInteger = (Integer)AccessController.doPrivileged(new GetIntegerAction("sun.awt.datatransfer.timeout"));
/*     */ 
/*  54 */     if ((localInteger == null) || (localInteger.intValue() <= 0)) {
/*  55 */       return 10000;
/*     */     }
/*  57 */     return localInteger.intValue();
/*     */   }
/*     */ 
/*     */   public boolean isNativeGTKAvailable()
/*     */   {
/*  72 */     synchronized (GTK_LOCK) {
/*  73 */       if (this.nativeGTKLoaded != null)
/*     */       {
/*  76 */         return this.nativeGTKLoaded.booleanValue();
/*     */       }
/*  78 */       if (this.nativeGTKAvailable != null)
/*     */       {
/*  81 */         return this.nativeGTKAvailable.booleanValue();
/*     */       }
/*     */ 
/*  84 */       boolean bool = check_gtk();
/*  85 */       this.nativeGTKAvailable = Boolean.valueOf(bool);
/*  86 */       return bool;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean loadGTK()
/*     */   {
/* 100 */     synchronized (GTK_LOCK) {
/* 101 */       if (this.nativeGTKLoaded == null) {
/* 102 */         boolean bool = load_gtk();
/* 103 */         this.nativeGTKLoaded = Boolean.valueOf(bool);
/*     */       }
/*     */     }
/* 106 */     return this.nativeGTKLoaded.booleanValue();
/*     */   }
/*     */ 
/*     */   protected Object lazilyLoadDesktopProperty(String paramString)
/*     */   {
/* 113 */     if (paramString.startsWith("gtk.icon.")) {
/* 114 */       return lazilyLoadGTKIcon(paramString);
/*     */     }
/* 116 */     return super.lazilyLoadDesktopProperty(paramString);
/*     */   }
/*     */ 
/*     */   protected Object lazilyLoadGTKIcon(String paramString)
/*     */   {
/* 129 */     Object localObject = this.desktopProperties.get(paramString);
/* 130 */     if (localObject != null) {
/* 131 */       return localObject;
/*     */     }
/*     */ 
/* 135 */     String[] arrayOfString = paramString.split("\\.");
/* 136 */     if (arrayOfString.length != 5) {
/* 137 */       return null;
/*     */     }
/*     */ 
/* 141 */     int i = 0;
/*     */     try {
/* 143 */       i = Integer.parseInt(arrayOfString[3]);
/*     */     } catch (NumberFormatException localNumberFormatException) {
/* 145 */       return null;
/*     */     }
/*     */ 
/* 149 */     GTKConstants.TextDirection localTextDirection = "ltr".equals(arrayOfString[4]) ? GTKConstants.TextDirection.LTR : GTKConstants.TextDirection.RTL;
/*     */ 
/* 153 */     BufferedImage localBufferedImage = getStockIcon(-1, arrayOfString[2], i, localTextDirection.ordinal(), null);
/* 154 */     if (localBufferedImage != null)
/*     */     {
/* 156 */       setDesktopProperty(paramString, localBufferedImage);
/*     */     }
/* 158 */     return localBufferedImage;
/*     */   }
/*     */ 
/*     */   public BufferedImage getGTKIcon(String paramString)
/*     */   {
/* 170 */     if (!loadGTK()) {
/* 171 */       return null;
/*     */     }
/*     */ 
/* 175 */     synchronized (GTK_LOCK) {
/* 176 */       if (!load_gtk_icon(paramString)) {
/* 177 */         this.tmpImage = null;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 182 */     return this.tmpImage;
/*     */   }
/*     */ 
/*     */   public BufferedImage getStockIcon(int paramInt1, String paramString1, int paramInt2, int paramInt3, String paramString2)
/*     */   {
/* 203 */     if (!loadGTK()) {
/* 204 */       return null;
/*     */     }
/*     */ 
/* 208 */     synchronized (GTK_LOCK) {
/* 209 */       if (!load_stock_icon(paramInt1, paramString1, paramInt2, paramInt3, paramString2)) {
/* 210 */         this.tmpImage = null;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 215 */     return this.tmpImage;
/*     */   }
/*     */ 
/*     */   public void loadIconCallback(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean)
/*     */   {
/* 228 */     this.tmpImage = null;
/*     */ 
/* 232 */     DataBufferByte localDataBufferByte = new DataBufferByte(paramArrayOfByte, paramInt3 * paramInt2);
/*     */ 
/* 234 */     WritableRaster localWritableRaster = Raster.createInterleavedRaster(localDataBufferByte, paramInt1, paramInt2, paramInt3, paramInt5, paramBoolean ? BAND_OFFSETS_ALPHA : BAND_OFFSETS, null);
/*     */ 
/* 237 */     ComponentColorModel localComponentColorModel = new ComponentColorModel(ColorSpace.getInstance(1000), paramBoolean, false, 3, 0);
/*     */ 
/* 243 */     this.tmpImage = new BufferedImage(localComponentColorModel, localWritableRaster, false, null);
/*     */   }
/*     */   private static native boolean check_gtk();
/*     */ 
/*     */   private static native boolean load_gtk();
/*     */ 
/*     */   private static native boolean unload_gtk();
/*     */ 
/*     */   private native boolean load_gtk_icon(String paramString);
/*     */ 
/*     */   private native boolean load_stock_icon(int paramInt1, String paramString1, int paramInt2, int paramInt3, String paramString2);
/*     */ 
/*     */   private native void nativeSync();
/*     */ 
/* 257 */   public void sync() { nativeSync();
/*     */ 
/* 259 */     OGLRenderQueue.sync();
/*     */   }
/*     */ 
/*     */   protected RenderingHints getDesktopAAHints()
/*     */   {
/* 271 */     Object localObject1 = getDesktopProperty("gnome.Xft/Antialias");
/*     */ 
/* 273 */     if (localObject1 == null)
/*     */     {
/* 278 */       localObject1 = getDesktopProperty("fontconfig/Antialias");
/* 279 */       if (localObject1 != null) {
/* 280 */         return new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, localObject1);
/*     */       }
/* 282 */       return null;
/*     */     }
/*     */ 
/* 291 */     boolean bool = Boolean.valueOf(((localObject1 instanceof Number)) && (((Number)localObject1).intValue() != 0)).booleanValue();
/*     */     Object localObject2;
/* 294 */     if (bool) {
/* 295 */       String str = (String)getDesktopProperty("gnome.Xft/RGBA");
/*     */ 
/* 298 */       if ((str == null) || (str.equals("none")))
/* 299 */         localObject2 = RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
/* 300 */       else if (str.equals("rgb"))
/* 301 */         localObject2 = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB;
/* 302 */       else if (str.equals("bgr"))
/* 303 */         localObject2 = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR;
/* 304 */       else if (str.equals("vrgb"))
/* 305 */         localObject2 = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB;
/* 306 */       else if (str.equals("vbgr")) {
/* 307 */         localObject2 = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VBGR;
/*     */       }
/*     */       else
/* 310 */         localObject2 = RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
/*     */     }
/*     */     else {
/* 313 */       localObject2 = RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT;
/*     */     }
/* 315 */     return new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, localObject2);
/*     */   }
/*     */ 
/*     */   private native boolean gtkCheckVersionImpl(int paramInt1, int paramInt2, int paramInt3);
/*     */ 
/*     */   public boolean checkGtkVersion(int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/* 335 */     if (loadGTK()) {
/* 336 */       return gtkCheckVersionImpl(paramInt1, paramInt2, paramInt3);
/*     */     }
/* 338 */     return false;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.UNIXToolkit
 * JD-Core Version:    0.6.2
 */