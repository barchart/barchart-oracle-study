/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.AlphaComposite;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.GraphicsConfiguration;
/*     */ import java.awt.GraphicsDevice;
/*     */ import java.awt.Image;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.SystemColor;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.awt.image.DataBuffer;
/*     */ import java.awt.image.DataBufferByte;
/*     */ import java.awt.image.DataBufferInt;
/*     */ import java.awt.image.DataBufferUShort;
/*     */ import java.awt.image.Raster;
/*     */ import java.awt.image.WritableRaster;
/*     */ import java.util.List;
/*     */ import sun.awt.image.ImageRepresentation;
/*     */ import sun.awt.image.ToolkitImage;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ public class XIconWindow extends XBaseWindow
/*     */ {
/*  36 */   private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.X11.XIconWindow");
/*     */   XDecoratedPeer parent;
/*     */   Dimension size;
/*  39 */   long iconPixmap = 0L;
/*  40 */   long iconMask = 0L;
/*  41 */   int iconWidth = 0;
/*  42 */   int iconHeight = 0;
/*     */ 
/*  44 */   XIconWindow(XDecoratedPeer paramXDecoratedPeer) { super(new XCreateWindowParams(new Object[] { "parent", paramXDecoratedPeer, "delayed", Boolean.TRUE })); }
/*     */ 
/*     */ 
/*     */   void instantPreInit(XCreateWindowParams paramXCreateWindowParams)
/*     */   {
/*  50 */     super.instantPreInit(paramXCreateWindowParams);
/*  51 */     this.parent = ((XDecoratedPeer)paramXCreateWindowParams.get("parent"));
/*     */   }
/*     */ 
/*     */   private XIconSize[] getIconSizes()
/*     */   {
/*  58 */     XToolkit.awtLock();
/*     */     try {
/*  60 */       AwtGraphicsConfigData localAwtGraphicsConfigData = this.parent.getGraphicsConfigurationData();
/*  61 */       long l1 = localAwtGraphicsConfigData.get_awt_visInfo().get_screen();
/*  62 */       long l2 = XToolkit.getDisplay();
/*     */ 
/*  64 */       if (log.isLoggable(300)) log.finest(localAwtGraphicsConfigData.toString());
/*     */ 
/*  66 */       long l3 = XlibWrapper.XGetIconSizes(l2, XToolkit.getDefaultRootWindow(), XlibWrapper.larg1, XlibWrapper.iarg1);
/*     */ 
/*  69 */       if (l3 == 0L) {
/*  70 */         return null;
/*     */       }
/*  72 */       int i = Native.getInt(XlibWrapper.iarg1);
/*  73 */       long l4 = Native.getLong(XlibWrapper.larg1);
/*  74 */       log.finest("count = {1}, sizes_ptr = {0}", new Object[] { Long.valueOf(l4), Integer.valueOf(i) });
/*  75 */       XIconSize[] arrayOfXIconSize2 = new XIconSize[i];
/*  76 */       for (int j = 0; j < i; l4 += XIconSize.getSize()) {
/*  77 */         arrayOfXIconSize2[j] = new XIconSize(l4);
/*  78 */         log.finest("sizes_ptr[{1}] = {0}", new Object[] { arrayOfXIconSize2[j], Integer.valueOf(j) });
/*     */ 
/*  76 */         j++;
/*     */       }
/*     */ 
/*  80 */       return arrayOfXIconSize2;
/*     */     } finally {
/*  82 */       XToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   private Dimension calcIconSize(int paramInt1, int paramInt2) {
/*  87 */     if (XWM.getWMID() == 10)
/*     */     {
/*  90 */       log.finest("Returning ICE_WM icon size: 16x16");
/*  91 */       return new Dimension(16, 16);
/*     */     }
/*     */ 
/*  94 */     XIconSize[] arrayOfXIconSize = getIconSizes();
/*  95 */     log.finest("Icon sizes: {0}", arrayOfXIconSize);
/*  96 */     if (arrayOfXIconSize == null)
/*     */     {
/*  98 */       return new Dimension(16, 16);
/*     */     }
/* 100 */     boolean bool = false;
/* 101 */     int i = -1; int k = 0;
/* 102 */     int i1 = 0; int i2 = 0;
/* 103 */     for (int i3 = 0; i3 < arrayOfXIconSize.length; i3++) {
/* 104 */       if ((paramInt1 >= arrayOfXIconSize[i3].get_min_width()) && (paramInt1 <= arrayOfXIconSize[i3].get_max_width()) && (paramInt2 >= arrayOfXIconSize[i3].get_min_height()) && (paramInt2 <= arrayOfXIconSize[i3].get_max_height()))
/*     */       {
/* 108 */         bool = true;
/* 109 */         if (((paramInt1 - arrayOfXIconSize[i3].get_min_width()) % arrayOfXIconSize[i3].get_width_inc() == 0) && ((paramInt2 - arrayOfXIconSize[i3].get_min_height()) % arrayOfXIconSize[i3].get_height_inc() == 0))
/*     */         {
/* 114 */           i1 = paramInt1;
/* 115 */           i2 = paramInt2;
/* 116 */           i = 0;
/* 117 */           break;
/*     */         }
/* 119 */         k = paramInt1 - arrayOfXIconSize[i3].get_min_width();
/*     */         int n;
/* 120 */         if (k == 0) {
/* 121 */           n = paramInt1;
/*     */         } else {
/* 123 */           k %= arrayOfXIconSize[i3].get_width_inc();
/* 124 */           n = paramInt1 - k;
/*     */         }
/* 126 */         k = paramInt2 - arrayOfXIconSize[i3].get_min_height();
/*     */         int m;
/* 127 */         if (k == 0) {
/* 128 */           m = paramInt2;
/*     */         } else {
/* 130 */           k %= arrayOfXIconSize[i3].get_height_inc();
/* 131 */           m = paramInt2 - k;
/*     */         }
/* 133 */         int j = n * n + m * m;
/*     */ 
/* 135 */         if (i > j) {
/* 136 */           i1 = n;
/* 137 */           i2 = m;
/* 138 */           i = j;
/*     */         }
/*     */       }
/*     */     }
/* 142 */     if (log.isLoggable(300)) {
/* 143 */       log.finest("found=" + bool);
/*     */     }
/* 145 */     if (!bool) {
/* 146 */       if (log.isLoggable(300)) {
/* 147 */         log.finest("widthHint=" + paramInt1 + ", heightHint=" + paramInt2 + ", saveWidth=" + i1 + ", saveHeight=" + i2 + ", max_width=" + arrayOfXIconSize[0].get_max_width() + ", max_height=" + arrayOfXIconSize[0].get_max_height() + ", min_width=" + arrayOfXIconSize[0].get_min_width() + ", min_height=" + arrayOfXIconSize[0].get_min_height());
/*     */       }
/*     */ 
/* 155 */       if ((paramInt1 > arrayOfXIconSize[0].get_max_width()) || (paramInt2 > arrayOfXIconSize[0].get_max_height()))
/*     */       {
/* 160 */         i3 = paramInt1 - arrayOfXIconSize[0].get_max_width();
/* 161 */         int i4 = paramInt2 - arrayOfXIconSize[0].get_max_height();
/* 162 */         if (log.isLoggable(300)) {
/* 163 */           log.finest("wdiff=" + i3 + ", hdiff=" + i4);
/*     */         }
/* 165 */         if (i3 >= i4) {
/* 166 */           i1 = arrayOfXIconSize[0].get_max_width();
/* 167 */           i2 = (int)(arrayOfXIconSize[0].get_max_width() / paramInt1 * paramInt2);
/*     */         }
/*     */         else {
/* 170 */           i1 = (int)(arrayOfXIconSize[0].get_max_height() / paramInt2 * paramInt1);
/*     */ 
/* 172 */           i2 = arrayOfXIconSize[0].get_max_height();
/*     */         }
/* 174 */       } else if ((paramInt1 < arrayOfXIconSize[0].get_min_width()) || (paramInt2 < arrayOfXIconSize[0].get_min_height()))
/*     */       {
/* 178 */         i1 = (arrayOfXIconSize[0].get_min_width() + arrayOfXIconSize[0].get_max_width()) / 2;
/* 179 */         i2 = (arrayOfXIconSize[0].get_min_height() + arrayOfXIconSize[0].get_max_height()) / 2;
/*     */       }
/*     */       else {
/* 182 */         i1 = paramInt1;
/* 183 */         i2 = paramInt1;
/*     */       }
/*     */     }
/*     */ 
/* 187 */     XToolkit.awtLock();
/*     */     try {
/* 189 */       XlibWrapper.XFree(arrayOfXIconSize[0].pData);
/*     */     } finally {
/* 191 */       XToolkit.awtUnlock();
/*     */     }
/*     */ 
/* 194 */     if (log.isLoggable(300)) {
/* 195 */       log.finest("return " + i1 + "x" + i2);
/*     */     }
/* 197 */     return new Dimension(i1, i2);
/*     */   }
/*     */ 
/*     */   Dimension getIconSize(int paramInt1, int paramInt2)
/*     */   {
/* 204 */     if (this.size == null) {
/* 205 */       this.size = calcIconSize(paramInt1, paramInt2);
/*     */     }
/* 207 */     return this.size;
/*     */   }
/*     */ 
/*     */   void replaceImage(Image paramImage)
/*     */   {
/* 217 */     if (this.parent == null) {
/* 218 */       return;
/*     */     }
/*     */ 
/* 223 */     BufferedImage localBufferedImage = null;
/*     */     Object localObject1;
/*     */     Object localObject2;
/*     */     Object localObject3;
/* 224 */     if ((paramImage != null) && (this.iconWidth != 0) && (this.iconHeight != 0)) {
/* 225 */       localObject1 = this.parent.getGraphicsConfiguration().getDevice().getDefaultConfiguration();
/* 226 */       localObject2 = ((GraphicsConfiguration)localObject1).getColorModel();
/* 227 */       localObject3 = ((ColorModel)localObject2).createCompatibleWritableRaster(this.iconWidth, this.iconHeight);
/* 228 */       localBufferedImage = new BufferedImage((ColorModel)localObject2, (WritableRaster)localObject3, ((ColorModel)localObject2).isAlphaPremultiplied(), null);
/* 229 */       Graphics localGraphics = localBufferedImage.getGraphics();
/*     */       try
/*     */       {
/* 233 */         localGraphics.setColor(SystemColor.window);
/* 234 */         localGraphics.fillRect(0, 0, this.iconWidth, this.iconHeight);
/* 235 */         if ((localGraphics instanceof Graphics2D)) {
/* 236 */           ((Graphics2D)localGraphics).setComposite(AlphaComposite.Src);
/*     */         }
/* 238 */         localGraphics.drawImage(paramImage, 0, 0, this.iconWidth, this.iconHeight, null);
/*     */       } finally {
/* 240 */         localGraphics.dispose();
/*     */       }
/*     */     }
/*     */ 
/* 244 */     XToolkit.awtLock();
/*     */     try {
/* 246 */       if (this.iconPixmap != 0L) {
/* 247 */         XlibWrapper.XFreePixmap(XToolkit.getDisplay(), this.iconPixmap);
/* 248 */         this.iconPixmap = 0L;
/* 249 */         log.finest("Freed previous pixmap");
/*     */       }
/* 251 */       if ((localBufferedImage == null) || (this.iconWidth == 0) || (this.iconHeight == 0)) {
/*     */         return;
/*     */       }
/* 254 */       localObject1 = this.parent.getGraphicsConfigurationData();
/* 255 */       localObject2 = ((AwtGraphicsConfigData)localObject1).get_awtImage(0);
/* 256 */       localObject3 = ((AwtGraphicsConfigData)localObject1).get_awt_visInfo();
/* 257 */       this.iconPixmap = XlibWrapper.XCreatePixmap(XToolkit.getDisplay(), XlibWrapper.RootWindow(XToolkit.getDisplay(), ((XVisualInfo)localObject3).get_screen()), this.iconWidth, this.iconHeight, ((awtImageData)localObject2).get_Depth());
/*     */ 
/* 263 */       if (this.iconPixmap == 0L) {
/* 264 */         log.finest("Can't create new pixmap for icon");
/*     */       }
/*     */       else
/*     */       {
/* 268 */         long l1 = 0L;
/* 269 */         DataBuffer localDataBuffer = localBufferedImage.getData().getDataBuffer();
/* 270 */         if ((localDataBuffer instanceof DataBufferByte)) {
/* 271 */           byte[] arrayOfByte = ((DataBufferByte)localDataBuffer).getData();
/* 272 */           ColorData localColorData = ((AwtGraphicsConfigData)localObject1).get_color_data(0);
/* 273 */           k = localColorData.get_awt_numICMcolors();
/* 274 */           for (int m = 0; m < arrayOfByte.length; m++) {
/* 275 */             arrayOfByte[m] = (arrayOfByte[m] >= k ? 0 : localColorData.get_awt_icmLUT2Colors(arrayOfByte[m]));
/*     */           }
/*     */ 
/* 278 */           l1 = Native.toData(arrayOfByte);
/* 279 */         } else if ((localDataBuffer instanceof DataBufferInt)) {
/* 280 */           l1 = Native.toData(((DataBufferInt)localDataBuffer).getData());
/* 281 */         } else if ((localDataBuffer instanceof DataBufferUShort)) {
/* 282 */           l1 = Native.toData(((DataBufferUShort)localDataBuffer).getData());
/*     */         } else {
/* 284 */           throw new IllegalArgumentException("Unknown data buffer: " + localDataBuffer);
/*     */         }
/* 286 */         int i = ((awtImageData)localObject2).get_wsImageFormat().get_bits_per_pixel();
/* 287 */         int j = ((awtImageData)localObject2).get_wsImageFormat().get_scanline_pad();
/* 288 */         int k = paddedwidth(this.iconWidth * i, j) >> 3;
/* 289 */         if ((k << 3) / i < this.iconWidth) {
/* 290 */           log.finest("Image format doesn't fit to icon width");
/*     */         }
/*     */         else {
/* 293 */           long l2 = XlibWrapper.XCreateImage(XToolkit.getDisplay(), ((XVisualInfo)localObject3).get_visual(), ((awtImageData)localObject2).get_Depth(), 2, 0, l1, this.iconWidth, this.iconHeight, 32, k);
/*     */ 
/* 303 */           if (l2 == 0L) {
/* 304 */             log.finest("Can't create XImage for icon");
/* 305 */             XlibWrapper.XFreePixmap(XToolkit.getDisplay(), this.iconPixmap);
/* 306 */             this.iconPixmap = 0L;
/*     */           }
/*     */           else {
/* 309 */             log.finest("Created XImage for icon");
/*     */ 
/* 311 */             long l3 = XlibWrapper.XCreateGC(XToolkit.getDisplay(), this.iconPixmap, 0L, 0L);
/* 312 */             if (l3 == 0L) {
/* 313 */               log.finest("Can't create GC for pixmap");
/* 314 */               XlibWrapper.XFreePixmap(XToolkit.getDisplay(), this.iconPixmap);
/* 315 */               this.iconPixmap = 0L;
/*     */             }
/*     */             else {
/* 318 */               log.finest("Created GC for pixmap");
/*     */               try
/*     */               {
/* 321 */                 XlibWrapper.XPutImage(XToolkit.getDisplay(), this.iconPixmap, l3, l2, 0, 0, 0, 0, this.iconWidth, this.iconHeight);
/*     */               }
/*     */               finally {
/* 324 */                 XlibWrapper.XFreeGC(XToolkit.getDisplay(), l3); }  } 
/*     */           }
/*     */         }
/*     */       } } finally { XToolkit.awtUnlock(); }
/*     */ 
/*     */   }
/*     */ 
/*     */   void replaceMask(Image paramImage)
/*     */   {
/* 337 */     if (this.parent == null) {
/* 338 */       return;
/*     */     }
/*     */ 
/* 341 */     BufferedImage localBufferedImage = null;
/*     */     Object localObject1;
/* 342 */     if ((paramImage != null) && (this.iconWidth != 0) && (this.iconHeight != 0)) {
/* 343 */       localBufferedImage = new BufferedImage(this.iconWidth, this.iconHeight, 2);
/* 344 */       localObject1 = localBufferedImage.getGraphics();
/*     */       try {
/* 346 */         ((Graphics)localObject1).drawImage(paramImage, 0, 0, this.iconWidth, this.iconHeight, null);
/*     */       } finally {
/* 348 */         ((Graphics)localObject1).dispose();
/*     */       }
/*     */     }
/*     */ 
/* 352 */     XToolkit.awtLock();
/*     */     try {
/* 354 */       if (this.iconMask != 0L) {
/* 355 */         XlibWrapper.XFreePixmap(XToolkit.getDisplay(), this.iconMask);
/* 356 */         this.iconMask = 0L;
/* 357 */         log.finest("Freed previous mask");
/*     */       }
/* 359 */       if ((localBufferedImage == null) || (this.iconWidth == 0) || (this.iconHeight == 0)) {
/*     */         return;
/*     */       }
/* 362 */       localObject1 = this.parent.getGraphicsConfigurationData();
/* 363 */       awtImageData localawtImageData = ((AwtGraphicsConfigData)localObject1).get_awtImage(0);
/* 364 */       XVisualInfo localXVisualInfo = ((AwtGraphicsConfigData)localObject1).get_awt_visInfo();
/* 365 */       ColorModel localColorModel = localBufferedImage.getColorModel();
/* 366 */       DataBuffer localDataBuffer = localBufferedImage.getRaster().getDataBuffer();
/* 367 */       int i = 0;
/* 368 */       int j = this.iconWidth + 7 >> 3;
/* 369 */       byte[] arrayOfByte = new byte[j * this.iconHeight];
/* 370 */       int k = 0;
/* 371 */       for (int m = 0; m < this.iconHeight; m++) {
/* 372 */         int n = 0;
/* 373 */         int i1 = 0;
/* 374 */         for (int i2 = 0; i2 < this.iconWidth; i2++) {
/* 375 */           if (localColorModel.getAlpha(localDataBuffer.getElem(i)) != 0) {
/* 376 */             i1 += (1 << n);
/*     */           }
/* 378 */           n++;
/* 379 */           if (n == 8) {
/* 380 */             arrayOfByte[k] = ((byte)i1);
/* 381 */             i1 = 0;
/* 382 */             n = 0;
/* 383 */             k++;
/*     */           }
/* 385 */           i++;
/*     */         }
/*     */       }
/* 388 */       this.iconMask = XlibWrapper.XCreateBitmapFromData(XToolkit.getDisplay(), XlibWrapper.RootWindow(XToolkit.getDisplay(), localXVisualInfo.get_screen()), Native.toData(arrayOfByte), this.iconWidth, this.iconHeight);
/*     */     }
/*     */     finally
/*     */     {
/* 393 */       XToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   void setIconImages(List<XIconInfo> paramList)
/*     */   {
/* 402 */     if ((paramList == null) || (paramList.size() == 0)) return;
/*     */ 
/* 404 */     int i = 2147483647;
/* 405 */     Object localObject = null;
/* 406 */     for (XIconInfo localXIconInfo : paramList) {
/* 407 */       if (localXIconInfo.isValid()) {
/* 408 */         Image localImage = localXIconInfo.getImage();
/* 409 */         Dimension localDimension = calcIconSize(localImage.getWidth(null), localImage.getHeight(null));
/* 410 */         int j = Math.abs(localDimension.width - localImage.getWidth(null));
/* 411 */         int k = Math.abs(localImage.getHeight(null) - localDimension.height);
/*     */ 
/* 414 */         if (i >= j + k) {
/* 415 */           i = j + k;
/* 416 */           localObject = localImage;
/*     */         }
/*     */       }
/*     */     }
/* 420 */     if (localObject != null) {
/* 421 */       log.finer("Icon: {0}x{1}", new Object[] { Integer.valueOf(localObject.getWidth(null)), Integer.valueOf(localObject.getHeight(null)) });
/* 422 */       setIconImage(localObject);
/*     */     }
/*     */   }
/*     */ 
/*     */   void setIconImage(Image paramImage)
/*     */   {
/*     */     Object localObject1;
/* 427 */     if (paramImage == null)
/*     */     {
/* 429 */       replaceImage(null);
/* 430 */       replaceMask(null);
/*     */     }
/*     */     else
/*     */     {
/*     */       int i;
/*     */       int j;
/* 435 */       if ((paramImage instanceof ToolkitImage)) {
/* 436 */         localObject1 = ((ToolkitImage)paramImage).getImageRep();
/* 437 */         ((ImageRepresentation)localObject1).reconstruct(32);
/* 438 */         i = ((ImageRepresentation)localObject1).getWidth();
/* 439 */         j = ((ImageRepresentation)localObject1).getHeight();
/*     */       }
/*     */       else {
/* 442 */         i = paramImage.getWidth(null);
/* 443 */         j = paramImage.getHeight(null);
/*     */       }
/* 445 */       localObject1 = getIconSize(i, j);
/* 446 */       if (localObject1 != null) {
/* 447 */         log.finest("Icon size: {0}", new Object[] { localObject1 });
/* 448 */         this.iconWidth = ((Dimension)localObject1).width;
/* 449 */         this.iconHeight = ((Dimension)localObject1).height;
/*     */       } else {
/* 451 */         log.finest("Error calculating image size");
/* 452 */         this.iconWidth = 0;
/* 453 */         this.iconHeight = 0;
/*     */       }
/* 455 */       replaceImage(paramImage);
/* 456 */       replaceMask(paramImage);
/*     */     }
/*     */ 
/* 459 */     XToolkit.awtLock();
/*     */     try {
/* 461 */       AwtGraphicsConfigData localAwtGraphicsConfigData = this.parent.getGraphicsConfigurationData();
/* 462 */       awtImageData localawtImageData = localAwtGraphicsConfigData.get_awtImage(0);
/* 463 */       localObject1 = localAwtGraphicsConfigData.get_awt_visInfo();
/* 464 */       XWMHints localXWMHints = this.parent.getWMHints();
/* 465 */       this.window = localXWMHints.get_icon_window();
/* 466 */       if (this.window == 0L) {
/* 467 */         log.finest("Icon window wasn't set");
/* 468 */         XCreateWindowParams localXCreateWindowParams = getDelayedParams();
/* 469 */         localXCreateWindowParams.add("border pixel", Long.valueOf(XToolkit.getAwtDefaultFg()));
/* 470 */         localXCreateWindowParams.add("pixmap", this.iconPixmap);
/* 471 */         localXCreateWindowParams.add("color map", localAwtGraphicsConfigData.get_awt_cmap());
/* 472 */         localXCreateWindowParams.add("visual depth", localawtImageData.get_Depth());
/* 473 */         localXCreateWindowParams.add("visual class", 1);
/* 474 */         localXCreateWindowParams.add("visual", ((XVisualInfo)localObject1).get_visual());
/* 475 */         localXCreateWindowParams.add("value mask", 8201L);
/* 476 */         localXCreateWindowParams.add("parent window", XlibWrapper.RootWindow(XToolkit.getDisplay(), ((XVisualInfo)localObject1).get_screen()));
/* 477 */         localXCreateWindowParams.add("bounds", new Rectangle(0, 0, this.iconWidth, this.iconHeight));
/* 478 */         localXCreateWindowParams.remove("delayed");
/* 479 */         init(localXCreateWindowParams);
/* 480 */         if (getWindow() == 0L)
/* 481 */           log.finest("Can't create new icon window");
/*     */         else {
/* 483 */           log.finest("Created new icon window");
/*     */         }
/*     */       }
/* 486 */       if (getWindow() != 0L) {
/* 487 */         XlibWrapper.XSetWindowBackgroundPixmap(XToolkit.getDisplay(), getWindow(), this.iconPixmap);
/* 488 */         XlibWrapper.XClearWindow(XToolkit.getDisplay(), getWindow());
/*     */       }
/*     */ 
/* 491 */       long l = localXWMHints.get_flags() | 0x4 | 0x20;
/* 492 */       if (getWindow() != 0L) {
/* 493 */         l |= 8L;
/*     */       }
/* 495 */       localXWMHints.set_flags(l);
/* 496 */       localXWMHints.set_icon_pixmap(this.iconPixmap);
/* 497 */       localXWMHints.set_icon_mask(this.iconMask);
/* 498 */       localXWMHints.set_icon_window(getWindow());
/* 499 */       XlibWrapper.XSetWMHints(XToolkit.getDisplay(), this.parent.getShell(), localXWMHints.pData);
/* 500 */       log.finest("Set icon window hint");
/*     */     } finally {
/* 502 */       XToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   static int paddedwidth(int paramInt1, int paramInt2)
/*     */   {
/* 508 */     return paramInt1 + (paramInt2 - 1) & (paramInt2 - 1 ^ 0xFFFFFFFF);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XIconWindow
 * JD-Core Version:    0.6.2
 */