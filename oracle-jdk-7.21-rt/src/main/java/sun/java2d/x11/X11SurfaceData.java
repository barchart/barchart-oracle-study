/*     */ package sun.java2d.x11;
/*     */ 
/*     */ import java.awt.Composite;
/*     */ import java.awt.GraphicsConfiguration;
/*     */ import java.awt.GraphicsDevice;
/*     */ import java.awt.GraphicsEnvironment;
/*     */ import java.awt.Image;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.color.ColorSpace;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.awt.image.ComponentColorModel;
/*     */ import java.awt.image.DirectColorModel;
/*     */ import java.awt.image.IndexColorModel;
/*     */ import java.awt.image.Raster;
/*     */ import java.io.PrintStream;
/*     */ import java.security.AccessController;
/*     */ import sun.awt.SunToolkit;
/*     */ import sun.awt.X11ComponentPeer;
/*     */ import sun.awt.X11GraphicsConfig;
/*     */ import sun.awt.image.PixelConverter.Ushort555Rgb;
/*     */ import sun.awt.image.PixelConverter.Ushort565Rgb;
/*     */ import sun.awt.image.PixelConverter.Xbgr;
/*     */ import sun.awt.image.PixelConverter.Xrgb;
/*     */ import sun.font.X11TextRenderer;
/*     */ import sun.java2d.InvalidPipeException;
/*     */ import sun.java2d.SunGraphics2D;
/*     */ import sun.java2d.SunGraphicsEnvironment;
/*     */ import sun.java2d.SurfaceData;
/*     */ import sun.java2d.SurfaceDataProxy;
/*     */ import sun.java2d.loops.CompositeType;
/*     */ import sun.java2d.loops.FontInfo;
/*     */ import sun.java2d.loops.GraphicsPrimitive;
/*     */ import sun.java2d.loops.RenderLoops;
/*     */ import sun.java2d.loops.SurfaceType;
/*     */ import sun.java2d.loops.XORComposite;
/*     */ import sun.java2d.pipe.PixelToShapeConverter;
/*     */ import sun.java2d.pipe.Region;
/*     */ import sun.java2d.pipe.TextPipe;
/*     */ import sun.java2d.pipe.ValidatePipe;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ 
/*     */ public abstract class X11SurfaceData extends XSurfaceData
/*     */ {
/*     */   X11ComponentPeer peer;
/*     */   X11GraphicsConfig graphicsConfig;
/*     */   private RenderLoops solidloops;
/*     */   protected int depth;
/*     */   public static final String DESC_INT_BGR_X11 = "Integer BGR Pixmap";
/*     */   public static final String DESC_INT_RGB_X11 = "Integer RGB Pixmap";
/*     */   public static final String DESC_4BYTE_ABGR_PRE_X11 = "4 byte ABGR Pixmap with pre-multplied alpha";
/*     */   public static final String DESC_INT_ARGB_PRE_X11 = "Integer ARGB Pixmap with pre-multiplied alpha";
/*     */   public static final String DESC_BYTE_IND_OPQ_X11 = "Byte Indexed Opaque Pixmap";
/*     */   public static final String DESC_INT_BGR_X11_BM = "Integer BGR Pixmap with 1-bit transp";
/*     */   public static final String DESC_INT_RGB_X11_BM = "Integer RGB Pixmap with 1-bit transp";
/*     */   public static final String DESC_BYTE_IND_X11_BM = "Byte Indexed Pixmap with 1-bit transp";
/*     */   public static final String DESC_BYTE_GRAY_X11 = "Byte Gray Opaque Pixmap";
/*     */   public static final String DESC_INDEX8_GRAY_X11 = "Index8 Gray Opaque Pixmap";
/*     */   public static final String DESC_BYTE_GRAY_X11_BM = "Byte Gray Opaque Pixmap with 1-bit transp";
/*     */   public static final String DESC_INDEX8_GRAY_X11_BM = "Index8 Gray Opaque Pixmap with 1-bit transp";
/*     */   public static final String DESC_3BYTE_RGB_X11 = "3 Byte RGB Pixmap";
/*     */   public static final String DESC_3BYTE_BGR_X11 = "3 Byte BGR Pixmap";
/*     */   public static final String DESC_3BYTE_RGB_X11_BM = "3 Byte RGB Pixmap with 1-bit transp";
/*     */   public static final String DESC_3BYTE_BGR_X11_BM = "3 Byte BGR Pixmap with 1-bit transp";
/*     */   public static final String DESC_USHORT_555_RGB_X11 = "Ushort 555 RGB Pixmap";
/*     */   public static final String DESC_USHORT_565_RGB_X11 = "Ushort 565 RGB Pixmap";
/*     */   public static final String DESC_USHORT_555_RGB_X11_BM = "Ushort 555 RGB Pixmap with 1-bit transp";
/*     */   public static final String DESC_USHORT_565_RGB_X11_BM = "Ushort 565 RGB Pixmap with 1-bit transp";
/*     */   public static final String DESC_USHORT_INDEXED_X11 = "Ushort Indexed Pixmap";
/*     */   public static final String DESC_USHORT_INDEXED_X11_BM = "Ushort Indexed Pixmap with 1-bit transp";
/* 137 */   public static final SurfaceType IntBgrX11 = SurfaceType.IntBgr.deriveSubType("Integer BGR Pixmap");
/*     */ 
/* 139 */   public static final SurfaceType IntRgbX11 = SurfaceType.IntRgb.deriveSubType("Integer RGB Pixmap");
/*     */ 
/* 142 */   public static final SurfaceType FourByteAbgrPreX11 = SurfaceType.FourByteAbgrPre.deriveSubType("4 byte ABGR Pixmap with pre-multplied alpha");
/*     */ 
/* 144 */   public static final SurfaceType IntArgbPreX11 = SurfaceType.IntArgbPre.deriveSubType("Integer ARGB Pixmap with pre-multiplied alpha");
/*     */ 
/* 147 */   public static final SurfaceType ThreeByteRgbX11 = SurfaceType.ThreeByteRgb.deriveSubType("3 Byte RGB Pixmap");
/*     */ 
/* 149 */   public static final SurfaceType ThreeByteBgrX11 = SurfaceType.ThreeByteBgr.deriveSubType("3 Byte BGR Pixmap");
/*     */ 
/* 152 */   public static final SurfaceType UShort555RgbX11 = SurfaceType.Ushort555Rgb.deriveSubType("Ushort 555 RGB Pixmap");
/*     */ 
/* 154 */   public static final SurfaceType UShort565RgbX11 = SurfaceType.Ushort565Rgb.deriveSubType("Ushort 565 RGB Pixmap");
/*     */ 
/* 157 */   public static final SurfaceType UShortIndexedX11 = SurfaceType.UshortIndexed.deriveSubType("Ushort Indexed Pixmap");
/*     */ 
/* 160 */   public static final SurfaceType ByteIndexedOpaqueX11 = SurfaceType.ByteIndexedOpaque.deriveSubType("Byte Indexed Opaque Pixmap");
/*     */ 
/* 163 */   public static final SurfaceType ByteGrayX11 = SurfaceType.ByteGray.deriveSubType("Byte Gray Opaque Pixmap");
/*     */ 
/* 165 */   public static final SurfaceType Index8GrayX11 = SurfaceType.Index8Gray.deriveSubType("Index8 Gray Opaque Pixmap");
/*     */ 
/* 169 */   public static final SurfaceType IntBgrX11_BM = SurfaceType.Custom.deriveSubType("Integer BGR Pixmap with 1-bit transp", PixelConverter.Xbgr.instance);
/*     */ 
/* 172 */   public static final SurfaceType IntRgbX11_BM = SurfaceType.Custom.deriveSubType("Integer RGB Pixmap with 1-bit transp", PixelConverter.Xrgb.instance);
/*     */ 
/* 176 */   public static final SurfaceType ThreeByteRgbX11_BM = SurfaceType.Custom.deriveSubType("3 Byte RGB Pixmap with 1-bit transp", PixelConverter.Xbgr.instance);
/*     */ 
/* 179 */   public static final SurfaceType ThreeByteBgrX11_BM = SurfaceType.Custom.deriveSubType("3 Byte BGR Pixmap with 1-bit transp", PixelConverter.Xrgb.instance);
/*     */ 
/* 183 */   public static final SurfaceType UShort555RgbX11_BM = SurfaceType.Custom.deriveSubType("Ushort 555 RGB Pixmap with 1-bit transp", PixelConverter.Ushort555Rgb.instance);
/*     */ 
/* 186 */   public static final SurfaceType UShort565RgbX11_BM = SurfaceType.Custom.deriveSubType("Ushort 565 RGB Pixmap with 1-bit transp", PixelConverter.Ushort565Rgb.instance);
/*     */ 
/* 190 */   public static final SurfaceType UShortIndexedX11_BM = SurfaceType.Custom.deriveSubType("Ushort Indexed Pixmap with 1-bit transp");
/*     */ 
/* 193 */   public static final SurfaceType ByteIndexedX11_BM = SurfaceType.Custom.deriveSubType("Byte Indexed Pixmap with 1-bit transp");
/*     */ 
/* 196 */   public static final SurfaceType ByteGrayX11_BM = SurfaceType.Custom.deriveSubType("Byte Gray Opaque Pixmap with 1-bit transp");
/*     */ 
/* 198 */   public static final SurfaceType Index8GrayX11_BM = SurfaceType.Custom.deriveSubType("Index8 Gray Opaque Pixmap with 1-bit transp");
/*     */ 
/* 202 */   private static Boolean accelerationEnabled = null;
/*     */   protected X11Renderer x11pipe;
/*     */   protected PixelToShapeConverter x11txpipe;
/*     */   protected static TextPipe x11textpipe;
/*     */   protected static boolean dgaAvailable;
/*     */   private long xgc;
/*     */   private Region validatedClip;
/*     */   private XORComposite validatedXorComp;
/*     */   private int xorpixelmod;
/*     */   private int validatedPixel;
/* 638 */   private boolean validatedExposures = true;
/*     */ 
/* 819 */   private static LazyPipe lazypipe = new LazyPipe();
/*     */ 
/*     */   private static native void initIDs(Class paramClass, boolean paramBoolean);
/*     */ 
/*     */   protected native void initSurface(int paramInt1, int paramInt2, int paramInt3, long paramLong);
/*     */ 
/*     */   public Raster getRaster(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/* 205 */     throw new InternalError("not implemented yet");
/*     */   }
/*     */ 
/*     */   public static native boolean isDgaAvailable();
/*     */ 
/*     */   private static native boolean isShmPMAvailable();
/*     */ 
/*     */   public static boolean isAccelerationEnabled()
/*     */   {
/* 262 */     if (accelerationEnabled == null)
/*     */     {
/* 264 */       if (GraphicsEnvironment.isHeadless()) {
/* 265 */         accelerationEnabled = Boolean.FALSE;
/*     */       } else {
/* 267 */         String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.pmoffscreen"));
/*     */ 
/* 270 */         if (str != null)
/*     */         {
/* 272 */           accelerationEnabled = Boolean.valueOf(str);
/*     */         } else {
/* 274 */           boolean bool = false;
/* 275 */           GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
/* 276 */           if ((localGraphicsEnvironment instanceof SunGraphicsEnvironment)) {
/* 277 */             bool = ((SunGraphicsEnvironment)localGraphicsEnvironment).isDisplayLocal();
/*     */           }
/*     */ 
/* 283 */           accelerationEnabled = Boolean.valueOf((!isDgaAvailable()) && ((!bool) || (isShmPMAvailable())));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 288 */     return accelerationEnabled.booleanValue();
/*     */   }
/*     */ 
/*     */   public SurfaceDataProxy makeProxyFor(SurfaceData paramSurfaceData)
/*     */   {
/* 293 */     return X11SurfaceDataProxy.createProxy(paramSurfaceData, this.graphicsConfig);
/*     */   }
/*     */ 
/*     */   public void validatePipe(SunGraphics2D paramSunGraphics2D) {
/* 297 */     if ((paramSunGraphics2D.antialiasHint != 2) && (paramSunGraphics2D.paintState <= 1) && ((paramSunGraphics2D.compositeState <= 0) || (paramSunGraphics2D.compositeState == 2)))
/*     */     {
/* 302 */       if (this.x11txpipe == null)
/*     */       {
/* 310 */         paramSunGraphics2D.drawpipe = lazypipe;
/* 311 */         paramSunGraphics2D.fillpipe = lazypipe;
/* 312 */         paramSunGraphics2D.shapepipe = lazypipe;
/* 313 */         paramSunGraphics2D.imagepipe = lazypipe;
/* 314 */         paramSunGraphics2D.textpipe = lazypipe;
/* 315 */         return;
/*     */       }
/*     */ 
/* 318 */       if (paramSunGraphics2D.clipState == 2)
/*     */       {
/* 323 */         super.validatePipe(paramSunGraphics2D);
/*     */       }
/* 325 */       else switch (paramSunGraphics2D.textAntialiasHint)
/*     */         {
/*     */         case 0:
/*     */         case 1:
/* 332 */           if (paramSunGraphics2D.compositeState == 0)
/* 333 */             paramSunGraphics2D.textpipe = x11textpipe;
/*     */           else {
/* 335 */             paramSunGraphics2D.textpipe = solidTextRenderer;
/*     */           }
/* 337 */           break;
/*     */         case 2:
/* 342 */           paramSunGraphics2D.textpipe = aaTextRenderer;
/* 343 */           break;
/*     */         default:
/* 346 */           switch (paramSunGraphics2D.getFontInfo().aaHint)
/*     */           {
/*     */           case 4:
/*     */           case 6:
/* 350 */             paramSunGraphics2D.textpipe = lcdTextRenderer;
/* 351 */             break;
/*     */           case 1:
/* 356 */             if (paramSunGraphics2D.compositeState == 0)
/* 357 */               paramSunGraphics2D.textpipe = x11textpipe;
/*     */             else {
/* 359 */               paramSunGraphics2D.textpipe = solidTextRenderer;
/*     */             }
/* 361 */             break;
/*     */           case 2:
/* 364 */             paramSunGraphics2D.textpipe = aaTextRenderer;
/* 365 */             break;
/*     */           case 3:
/*     */           case 5:
/*     */           default:
/* 368 */             paramSunGraphics2D.textpipe = solidTextRenderer;
/*     */           }
/*     */           break;
/*     */         }
/*     */ 
/* 373 */       if (paramSunGraphics2D.transformState >= 3) {
/* 374 */         paramSunGraphics2D.drawpipe = this.x11txpipe;
/* 375 */         paramSunGraphics2D.fillpipe = this.x11txpipe;
/* 376 */       } else if (paramSunGraphics2D.strokeState != 0) {
/* 377 */         paramSunGraphics2D.drawpipe = this.x11txpipe;
/* 378 */         paramSunGraphics2D.fillpipe = this.x11pipe;
/*     */       } else {
/* 380 */         paramSunGraphics2D.drawpipe = this.x11pipe;
/* 381 */         paramSunGraphics2D.fillpipe = this.x11pipe;
/*     */       }
/* 383 */       paramSunGraphics2D.shapepipe = this.x11pipe;
/* 384 */       paramSunGraphics2D.imagepipe = imagepipe;
/*     */ 
/* 391 */       if (paramSunGraphics2D.loops == null)
/*     */       {
/* 393 */         paramSunGraphics2D.loops = getRenderLoops(paramSunGraphics2D);
/*     */       }
/*     */     } else {
/* 396 */       super.validatePipe(paramSunGraphics2D);
/*     */     }
/*     */   }
/*     */ 
/*     */   public RenderLoops getRenderLoops(SunGraphics2D paramSunGraphics2D) {
/* 401 */     if ((paramSunGraphics2D.paintState <= 1) && (paramSunGraphics2D.compositeState <= 0))
/*     */     {
/* 404 */       return this.solidloops;
/*     */     }
/* 406 */     return super.getRenderLoops(paramSunGraphics2D);
/*     */   }
/*     */ 
/*     */   public GraphicsConfiguration getDeviceConfiguration() {
/* 410 */     return this.graphicsConfig;
/*     */   }
/*     */ 
/*     */   public static X11WindowSurfaceData createData(X11ComponentPeer paramX11ComponentPeer)
/*     */   {
/* 417 */     X11GraphicsConfig localX11GraphicsConfig = getGC(paramX11ComponentPeer);
/* 418 */     return new X11WindowSurfaceData(paramX11ComponentPeer, localX11GraphicsConfig, localX11GraphicsConfig.getSurfaceType());
/*     */   }
/*     */ 
/*     */   public static X11PixmapSurfaceData createData(X11GraphicsConfig paramX11GraphicsConfig, int paramInt1, int paramInt2, ColorModel paramColorModel, Image paramImage, long paramLong, int paramInt3)
/*     */   {
/* 430 */     return new X11PixmapSurfaceData(paramX11GraphicsConfig, paramInt1, paramInt2, paramImage, getSurfaceType(paramX11GraphicsConfig, paramInt3, true), paramColorModel, paramLong, paramInt3);
/*     */   }
/*     */ 
/*     */   protected X11SurfaceData(X11ComponentPeer paramX11ComponentPeer, X11GraphicsConfig paramX11GraphicsConfig, SurfaceType paramSurfaceType, ColorModel paramColorModel)
/*     */   {
/* 445 */     super(paramSurfaceType, paramColorModel);
/* 446 */     this.peer = paramX11ComponentPeer;
/* 447 */     this.graphicsConfig = paramX11GraphicsConfig;
/* 448 */     this.solidloops = this.graphicsConfig.getSolidLoops(paramSurfaceType);
/* 449 */     this.depth = paramColorModel.getPixelSize();
/* 450 */     initOps(paramX11ComponentPeer, this.graphicsConfig, this.depth);
/* 451 */     if (isAccelerationEnabled())
/* 452 */       setBlitProxyKey(paramX11GraphicsConfig.getProxyKey());
/*     */   }
/*     */ 
/*     */   public static X11GraphicsConfig getGC(X11ComponentPeer paramX11ComponentPeer)
/*     */   {
/* 457 */     if (paramX11ComponentPeer != null) {
/* 458 */       return (X11GraphicsConfig)paramX11ComponentPeer.getGraphicsConfiguration();
/*     */     }
/* 460 */     GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
/*     */ 
/* 462 */     GraphicsDevice localGraphicsDevice = localGraphicsEnvironment.getDefaultScreenDevice();
/* 463 */     return (X11GraphicsConfig)localGraphicsDevice.getDefaultConfiguration();
/*     */   }
/*     */ 
/*     */   public abstract boolean canSourceSendExposures(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
/*     */ 
/*     */   public boolean copyArea(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
/*     */   {
/* 484 */     if (this.x11pipe == null) {
/* 485 */       if (!isDrawableValid()) {
/* 486 */         return true;
/*     */       }
/* 488 */       makePipes();
/*     */     }
/* 490 */     CompositeType localCompositeType = paramSunGraphics2D.imageComp;
/* 491 */     if ((paramSunGraphics2D.transformState < 3) && ((CompositeType.SrcOverNoEa.equals(localCompositeType)) || (CompositeType.SrcNoEa.equals(localCompositeType))))
/*     */     {
/* 495 */       paramInt1 += paramSunGraphics2D.transX;
/* 496 */       paramInt2 += paramSunGraphics2D.transY;
/* 497 */       SunToolkit.awtLock();
/*     */       try {
/* 499 */         boolean bool = canSourceSendExposures(paramInt1, paramInt2, paramInt3, paramInt4);
/* 500 */         long l = getBlitGC(paramSunGraphics2D.getCompClip(), bool);
/* 501 */         this.x11pipe.devCopyArea(getNativeOps(), l, paramInt1, paramInt2, paramInt1 + paramInt5, paramInt2 + paramInt6, paramInt3, paramInt4);
/*     */       }
/*     */       finally
/*     */       {
/* 506 */         SunToolkit.awtUnlock();
/*     */       }
/* 508 */       return true;
/*     */     }
/* 510 */     return false;
/*     */   }
/*     */ 
/*     */   public static SurfaceType getSurfaceType(X11GraphicsConfig paramX11GraphicsConfig, int paramInt)
/*     */   {
/* 516 */     return getSurfaceType(paramX11GraphicsConfig, paramInt, false);
/*     */   }
/*     */ 
/*     */   public static SurfaceType getSurfaceType(X11GraphicsConfig paramX11GraphicsConfig, int paramInt, boolean paramBoolean)
/*     */   {
/* 523 */     int i = paramInt == 2 ? 1 : 0;
/*     */ 
/* 525 */     ColorModel localColorModel = paramX11GraphicsConfig.getColorModel();
/*     */     SurfaceType localSurfaceType;
/* 526 */     switch (localColorModel.getPixelSize()) {
/*     */     case 24:
/* 528 */       if (paramX11GraphicsConfig.getBitsPerPixel() == 24) {
/* 529 */         if ((localColorModel instanceof DirectColorModel))
/*     */         {
/* 537 */           localSurfaceType = i != 0 ? ThreeByteBgrX11_BM : ThreeByteBgrX11; break label563;
/*     */         }
/* 539 */         throw new InvalidPipeException("Unsupported bit depth/cm combo: " + localColorModel.getPixelSize() + ", " + localColorModel);
/*     */       }
/*     */ 
/*     */     case 32:
/* 548 */       if ((localColorModel instanceof DirectColorModel)) {
/* 549 */         if ((((SunToolkit)Toolkit.getDefaultToolkit()).isTranslucencyCapable(paramX11GraphicsConfig)) && (!paramBoolean))
/*     */         {
/* 552 */           localSurfaceType = IntArgbPreX11; break label563;
/*     */         }
/* 554 */         if (((DirectColorModel)localColorModel).getRedMask() == 16711680) {
/* 555 */           localSurfaceType = i != 0 ? IntRgbX11_BM : IntRgbX11; break label563;
/*     */         }
/*     */ 
/* 558 */         localSurfaceType = i != 0 ? IntBgrX11_BM : IntBgrX11; break label563;
/*     */       }
/*     */ 
/* 562 */       if ((localColorModel instanceof ComponentColorModel)) {
/* 563 */         localSurfaceType = FourByteAbgrPreX11; break label563;
/*     */       }
/*     */ 
/* 566 */       throw new InvalidPipeException("Unsupported bit depth/cm combo: " + localColorModel.getPixelSize() + ", " + localColorModel);
/*     */     case 15:
/* 573 */       localSurfaceType = i != 0 ? UShort555RgbX11_BM : UShort555RgbX11;
/* 574 */       break;
/*     */     case 16:
/* 576 */       if (((localColorModel instanceof DirectColorModel)) && (((DirectColorModel)localColorModel).getGreenMask() == 992))
/*     */       {
/* 580 */         localSurfaceType = i != 0 ? UShort555RgbX11_BM : UShort555RgbX11;
/*     */       }
/* 582 */       else localSurfaceType = i != 0 ? UShort565RgbX11_BM : UShort565RgbX11;
/*     */ 
/* 584 */       break;
/*     */     case 12:
/* 586 */       if ((localColorModel instanceof IndexColorModel)) {
/* 587 */         localSurfaceType = i != 0 ? UShortIndexedX11_BM : UShortIndexedX11; break label563;
/*     */       }
/*     */ 
/* 591 */       throw new InvalidPipeException("Unsupported bit depth: " + localColorModel.getPixelSize() + " cm=" + localColorModel);
/*     */     case 8:
/* 598 */       if ((localColorModel.getColorSpace().getType() == 6) && ((localColorModel instanceof ComponentColorModel)))
/*     */       {
/* 600 */         localSurfaceType = i != 0 ? ByteGrayX11_BM : ByteGrayX11;
/* 601 */       } else if (((localColorModel instanceof IndexColorModel)) && (isOpaqueGray((IndexColorModel)localColorModel)))
/*     */       {
/* 603 */         localSurfaceType = i != 0 ? Index8GrayX11_BM : Index8GrayX11;
/*     */       }
/* 605 */       else localSurfaceType = i != 0 ? ByteIndexedX11_BM : ByteIndexedOpaqueX11;
/*     */ 
/* 607 */       break;
/*     */     }
/* 609 */     throw new InvalidPipeException("Unsupported bit depth: " + localColorModel.getPixelSize());
/*     */ 
/* 613 */     label563: return localSurfaceType;
/*     */   }
/*     */ 
/*     */   public void invalidate() {
/* 617 */     if (isValid()) {
/* 618 */       setInvalid();
/* 619 */       super.invalidate();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static native void XSetCopyMode(long paramLong);
/*     */ 
/*     */   private static native void XSetXorMode(long paramLong);
/*     */ 
/*     */   private static native void XSetForeground(long paramLong, int paramInt);
/*     */ 
/*     */   public final long getRenderGC(Region paramRegion, int paramInt1, Composite paramComposite, int paramInt2)
/*     */   {
/* 644 */     return getGC(paramRegion, paramInt1, paramComposite, paramInt2, this.validatedExposures);
/*     */   }
/*     */ 
/*     */   public final long getBlitGC(Region paramRegion, boolean paramBoolean) {
/* 648 */     return getGC(paramRegion, 0, null, this.validatedPixel, paramBoolean);
/*     */   }
/*     */ 
/*     */   final long getGC(Region paramRegion, int paramInt1, Composite paramComposite, int paramInt2, boolean paramBoolean)
/*     */   {
/* 658 */     if (!isValid()) {
/* 659 */       throw new InvalidPipeException("bounds changed");
/*     */     }
/*     */ 
/* 663 */     if (paramRegion != this.validatedClip) {
/* 664 */       this.validatedClip = paramRegion;
/* 665 */       if (paramRegion != null) {
/* 666 */         XSetClip(this.xgc, paramRegion.getLoX(), paramRegion.getLoY(), paramRegion.getHiX(), paramRegion.getHiY(), paramRegion.isRectangular() ? null : paramRegion);
/*     */       }
/*     */       else
/*     */       {
/* 671 */         XResetClip(this.xgc);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 676 */     if (paramInt1 == 0) {
/* 677 */       if (this.validatedXorComp != null) {
/* 678 */         this.validatedXorComp = null;
/* 679 */         this.xorpixelmod = 0;
/* 680 */         XSetCopyMode(this.xgc);
/*     */       }
/*     */     }
/* 683 */     else if (this.validatedXorComp != paramComposite) {
/* 684 */       this.validatedXorComp = ((XORComposite)paramComposite);
/* 685 */       this.xorpixelmod = this.validatedXorComp.getXorPixel();
/* 686 */       XSetXorMode(this.xgc);
/*     */     }
/*     */ 
/* 691 */     paramInt2 ^= this.xorpixelmod;
/* 692 */     if (paramInt2 != this.validatedPixel) {
/* 693 */       this.validatedPixel = paramInt2;
/* 694 */       XSetForeground(this.xgc, paramInt2);
/*     */     }
/*     */ 
/* 697 */     if (this.validatedExposures != paramBoolean) {
/* 698 */       this.validatedExposures = paramBoolean;
/* 699 */       XSetGraphicsExposures(this.xgc, paramBoolean);
/*     */     }
/*     */ 
/* 702 */     return this.xgc;
/*     */   }
/*     */ 
/*     */   public synchronized void makePipes() {
/* 706 */     if (this.x11pipe == null) {
/* 707 */       SunToolkit.awtLock();
/*     */       try {
/* 709 */         this.xgc = XCreateGC(getNativeOps());
/*     */       } finally {
/* 711 */         SunToolkit.awtUnlock();
/*     */       }
/* 713 */       this.x11pipe = X11Renderer.getInstance();
/* 714 */       this.x11txpipe = new PixelToShapeConverter(this.x11pipe);
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 214 */     if ((!isX11SurfaceDataInitialized()) && (!GraphicsEnvironment.isHeadless()))
/*     */     {
/* 217 */       String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("javax.accessibility.screen_magnifier_present"));
/*     */ 
/* 219 */       boolean bool = (str1 == null) || (!"true".equals(str1));
/*     */ 
/* 221 */       initIDs(XORComposite.class, bool);
/*     */ 
/* 223 */       String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.xtextpipe"));
/*     */ 
/* 225 */       if ((str2 == null) || ("true".startsWith(str2))) {
/* 226 */         if ("true".equals(str2))
/*     */         {
/* 228 */           System.out.println("using X11 text renderer");
/*     */         }
/* 230 */         x11textpipe = new X11TextRenderer();
/* 231 */         if (GraphicsPrimitive.tracingEnabled())
/* 232 */           x11textpipe = ((X11TextRenderer)x11textpipe).traceWrap();
/*     */       }
/*     */       else {
/* 235 */         if ("false".equals(str2))
/*     */         {
/* 237 */           System.out.println("using DGA text renderer");
/*     */         }
/* 239 */         x11textpipe = solidTextRenderer;
/*     */       }
/*     */ 
/* 242 */       dgaAvailable = isDgaAvailable();
/*     */ 
/* 244 */       if (isAccelerationEnabled()) {
/* 245 */         X11PMBlitLoops.register();
/* 246 */         X11PMBlitBgLoops.register();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class LazyPipe extends ValidatePipe
/*     */   {
/*     */     public boolean validate(SunGraphics2D paramSunGraphics2D)
/*     */     {
/* 823 */       X11SurfaceData localX11SurfaceData = (X11SurfaceData)paramSunGraphics2D.surfaceData;
/* 824 */       if (!localX11SurfaceData.isDrawableValid()) {
/* 825 */         return false;
/*     */       }
/* 827 */       localX11SurfaceData.makePipes();
/* 828 */       return super.validate(paramSunGraphics2D);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class X11PixmapSurfaceData extends X11SurfaceData
/*     */   {
/*     */     Image offscreenImage;
/*     */     int width;
/*     */     int height;
/*     */     int transparency;
/*     */ 
/*     */     public X11PixmapSurfaceData(X11GraphicsConfig paramX11GraphicsConfig, int paramInt1, int paramInt2, Image paramImage, SurfaceType paramSurfaceType, ColorModel paramColorModel, long paramLong, int paramInt3)
/*     */     {
/* 764 */       super(paramX11GraphicsConfig, paramSurfaceType, paramColorModel);
/* 765 */       this.width = paramInt1;
/* 766 */       this.height = paramInt2;
/* 767 */       this.offscreenImage = paramImage;
/* 768 */       this.transparency = paramInt3;
/* 769 */       initSurface(this.depth, paramInt1, paramInt2, paramLong);
/* 770 */       makePipes();
/*     */     }
/*     */ 
/*     */     public SurfaceData getReplacement() {
/* 774 */       return restoreContents(this.offscreenImage);
/*     */     }
/*     */ 
/*     */     public int getTransparency()
/*     */     {
/* 786 */       return this.transparency;
/*     */     }
/*     */ 
/*     */     public Rectangle getBounds() {
/* 790 */       return new Rectangle(this.width, this.height);
/*     */     }
/*     */ 
/*     */     public boolean canSourceSendExposures(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */     {
/* 795 */       return (paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 + paramInt3 > this.width) || (paramInt2 + paramInt4 > this.height);
/*     */     }
/*     */ 
/*     */     public void flush()
/*     */     {
/* 807 */       invalidate();
/* 808 */       flushNativeSurface();
/*     */     }
/*     */ 
/*     */     public Object getDestination()
/*     */     {
/* 815 */       return this.offscreenImage;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class X11WindowSurfaceData extends X11SurfaceData
/*     */   {
/*     */     public X11WindowSurfaceData(X11ComponentPeer paramX11ComponentPeer, X11GraphicsConfig paramX11GraphicsConfig, SurfaceType paramSurfaceType)
/*     */     {
/* 722 */       super(paramX11GraphicsConfig, paramSurfaceType, paramX11ComponentPeer.getColorModel());
/* 723 */       if (isDrawableValid())
/* 724 */         makePipes();
/*     */     }
/*     */ 
/*     */     public SurfaceData getReplacement()
/*     */     {
/* 729 */       return this.peer.getSurfaceData();
/*     */     }
/*     */ 
/*     */     public Rectangle getBounds() {
/* 733 */       Rectangle localRectangle = this.peer.getBounds();
/* 734 */       localRectangle.x = (localRectangle.y = 0);
/* 735 */       return localRectangle;
/*     */     }
/*     */ 
/*     */     public boolean canSourceSendExposures(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */     {
/* 740 */       return true;
/*     */     }
/*     */ 
/*     */     public Object getDestination()
/*     */     {
/* 747 */       return this.peer.getTarget();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.x11.X11SurfaceData
 * JD-Core Version:    0.6.2
 */