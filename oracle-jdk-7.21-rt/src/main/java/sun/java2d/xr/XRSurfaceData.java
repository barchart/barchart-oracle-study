/*     */ package sun.java2d.xr;
/*     */ 
/*     */ import java.awt.AlphaComposite;
/*     */ import java.awt.GraphicsConfiguration;
/*     */ import java.awt.GraphicsDevice;
/*     */ import java.awt.GraphicsEnvironment;
/*     */ import java.awt.Image;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.awt.image.DirectColorModel;
/*     */ import java.awt.image.Raster;
/*     */ import sun.awt.SunToolkit;
/*     */ import sun.awt.X11ComponentPeer;
/*     */ import sun.font.FontManagerNativeLibrary;
/*     */ import sun.java2d.InvalidPipeException;
/*     */ import sun.java2d.SunGraphics2D;
/*     */ import sun.java2d.SurfaceData;
/*     */ import sun.java2d.SurfaceDataProxy;
/*     */ import sun.java2d.jules.JulesPathBuf;
/*     */ import sun.java2d.jules.JulesShapePipe;
/*     */ import sun.java2d.loops.CompositeType;
/*     */ import sun.java2d.loops.MaskFill;
/*     */ import sun.java2d.loops.RenderLoops;
/*     */ import sun.java2d.loops.SurfaceType;
/*     */ import sun.java2d.loops.XORComposite;
/*     */ import sun.java2d.pipe.PixelToShapeConverter;
/*     */ import sun.java2d.pipe.Region;
/*     */ import sun.java2d.pipe.ShapeDrawPipe;
/*     */ import sun.java2d.pipe.TextPipe;
/*     */ import sun.java2d.pipe.ValidatePipe;
/*     */ import sun.java2d.x11.XSurfaceData;
/*     */ 
/*     */ public abstract class XRSurfaceData extends XSurfaceData
/*     */ {
/*     */   X11ComponentPeer peer;
/*     */   XRGraphicsConfig graphicsConfig;
/*     */   XRBackend renderQueue;
/*     */   private RenderLoops solidloops;
/*     */   protected int depth;
/*     */   public static final String DESC_BYTE_A8_X11 = "Byte A8 Pixmap";
/*     */   public static final String DESC_INT_RGB_X11 = "Integer RGB Pixmap";
/*     */   public static final String DESC_INT_ARGB_X11 = "Integer ARGB-Pre Pixmap";
/*  65 */   public static final SurfaceType ByteA8X11 = SurfaceType.ByteGray.deriveSubType("Byte A8 Pixmap");
/*     */ 
/*  67 */   public static final SurfaceType IntRgbX11 = SurfaceType.IntRgb.deriveSubType("Integer RGB Pixmap");
/*     */ 
/*  69 */   public static final SurfaceType IntArgbPreX11 = SurfaceType.IntArgbPre.deriveSubType("Integer ARGB-Pre Pixmap");
/*     */   protected XRRenderer xrpipe;
/*     */   protected PixelToShapeConverter xrtxpipe;
/*     */   protected TextPipe xrtextpipe;
/*     */   protected XRDrawImage xrDrawImage;
/*     */   protected ShapeDrawPipe aaShapePipe;
/*     */   protected PixelToShapeConverter aaPixelToShapeConv;
/*     */   private long xgc;
/* 386 */   private int validatedGCForegroundPixel = 0;
/*     */   private XORComposite validatedXorComp;
/*     */   private int xid;
/*     */   public int picture;
/*     */   public XRCompositeManager maskBuffer;
/*     */   private Region validatedClip;
/*     */   private Region validatedGCClip;
/* 394 */   private boolean validatedExposures = true;
/*     */ 
/* 396 */   boolean transformInUse = false;
/* 397 */   AffineTransform validatedSourceTransform = new AffineTransform();
/* 398 */   int validatedRepeat = 0;
/* 399 */   int validatedFilter = 0;
/*     */ 
/*     */   private static native void initIDs();
/*     */ 
/*     */   protected native void XRInitSurface(int paramInt1, int paramInt2, int paramInt3, long paramLong, int paramInt4);
/*     */ 
/*     */   native void initXRPicture(long paramLong, int paramInt);
/*     */ 
/*     */   native void freeXSDOPicture(long paramLong);
/*     */ 
/*     */   public Raster getRaster(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/*  72 */     throw new InternalError("not implemented yet");
/*     */   }
/*     */ 
/*     */   public static void initXRSurfaceData()
/*     */   {
/*  84 */     if (!isX11SurfaceDataInitialized()) {
/*  85 */       FontManagerNativeLibrary.load();
/*  86 */       initIDs();
/*  87 */       XRPMBlitLoops.register();
/*  88 */       XRMaskFill.register();
/*  89 */       XRMaskBlit.register();
/*     */ 
/*  91 */       setX11SurfaceDataInitialized();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected boolean isXRDrawableValid()
/*     */   {
/*     */     try
/*     */     {
/* 100 */       SunToolkit.awtLock();
/* 101 */       return isDrawableValid();
/*     */     } finally {
/* 103 */       SunToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public SurfaceDataProxy makeProxyFor(SurfaceData paramSurfaceData)
/*     */   {
/* 109 */     return XRSurfaceDataProxy.createProxy(paramSurfaceData, this.graphicsConfig);
/*     */   }
/*     */ 
/*     */   public void validatePipe(SunGraphics2D paramSunGraphics2D)
/*     */   {
/* 114 */     int i = 0;
/*     */     TextPipe localTextPipe;
/* 120 */     if ((paramSunGraphics2D.compositeState < 2) && ((paramSunGraphics2D.paintState < 5) || (paramSunGraphics2D.composite == null) || (!(paramSunGraphics2D.composite instanceof AlphaComposite)) || (((AlphaComposite)paramSunGraphics2D.composite).getAlpha() == 1.0F)))
/*     */     {
/* 126 */       localTextPipe = this.xrtextpipe;
/*     */     } else {
/* 128 */       super.validatePipe(paramSunGraphics2D);
/* 129 */       localTextPipe = paramSunGraphics2D.textpipe;
/* 130 */       i = 1;
/*     */     }
/*     */ 
/* 133 */     PixelToShapeConverter localPixelToShapeConverter = null;
/* 134 */     XRRenderer localXRRenderer = null;
/*     */ 
/* 139 */     if (paramSunGraphics2D.antialiasHint != 2) {
/* 140 */       if (paramSunGraphics2D.paintState <= 1) {
/* 141 */         if (paramSunGraphics2D.compositeState <= 2) {
/* 142 */           localPixelToShapeConverter = this.xrtxpipe;
/* 143 */           localXRRenderer = this.xrpipe;
/*     */         }
/* 145 */       } else if ((paramSunGraphics2D.compositeState <= 1) && 
/* 146 */         (XRPaints.isValid(paramSunGraphics2D))) {
/* 147 */         localPixelToShapeConverter = this.xrtxpipe;
/* 148 */         localXRRenderer = this.xrpipe;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 154 */     if ((paramSunGraphics2D.antialiasHint == 2) && (JulesPathBuf.isCairoAvailable()))
/*     */     {
/* 157 */       paramSunGraphics2D.shapepipe = this.aaShapePipe;
/* 158 */       paramSunGraphics2D.drawpipe = this.aaPixelToShapeConv;
/* 159 */       paramSunGraphics2D.fillpipe = this.aaPixelToShapeConv;
/*     */     }
/* 161 */     else if (localPixelToShapeConverter != null) {
/* 162 */       if (paramSunGraphics2D.transformState >= 3) {
/* 163 */         paramSunGraphics2D.drawpipe = localPixelToShapeConverter;
/* 164 */         paramSunGraphics2D.fillpipe = localPixelToShapeConverter;
/* 165 */       } else if (paramSunGraphics2D.strokeState != 0) {
/* 166 */         paramSunGraphics2D.drawpipe = localPixelToShapeConverter;
/* 167 */         paramSunGraphics2D.fillpipe = localXRRenderer;
/*     */       } else {
/* 169 */         paramSunGraphics2D.drawpipe = localXRRenderer;
/* 170 */         paramSunGraphics2D.fillpipe = localXRRenderer;
/*     */       }
/* 172 */       paramSunGraphics2D.shapepipe = localXRRenderer;
/*     */     }
/* 174 */     else if (i == 0) {
/* 175 */       super.validatePipe(paramSunGraphics2D);
/*     */     }
/*     */ 
/* 181 */     paramSunGraphics2D.textpipe = localTextPipe;
/*     */ 
/* 184 */     paramSunGraphics2D.imagepipe = this.xrDrawImage;
/*     */   }
/*     */ 
/*     */   protected MaskFill getMaskFill(SunGraphics2D paramSunGraphics2D) {
/* 188 */     if ((paramSunGraphics2D.paintState > 1) && (!XRPaints.isValid(paramSunGraphics2D)))
/*     */     {
/* 191 */       return null;
/*     */     }
/* 193 */     return super.getMaskFill(paramSunGraphics2D);
/*     */   }
/*     */ 
/*     */   public RenderLoops getRenderLoops(SunGraphics2D paramSunGraphics2D) {
/* 197 */     if ((paramSunGraphics2D.paintState <= 1) && (paramSunGraphics2D.compositeState <= 1))
/*     */     {
/* 200 */       return this.solidloops;
/*     */     }
/*     */ 
/* 203 */     return super.getRenderLoops(paramSunGraphics2D);
/*     */   }
/*     */ 
/*     */   public GraphicsConfiguration getDeviceConfiguration() {
/* 207 */     return this.graphicsConfig;
/*     */   }
/*     */ 
/*     */   public static XRWindowSurfaceData createData(X11ComponentPeer paramX11ComponentPeer)
/*     */   {
/* 214 */     XRGraphicsConfig localXRGraphicsConfig = getGC(paramX11ComponentPeer);
/* 215 */     return new XRWindowSurfaceData(paramX11ComponentPeer, localXRGraphicsConfig, localXRGraphicsConfig.getSurfaceType());
/*     */   }
/*     */ 
/*     */   public static XRPixmapSurfaceData createData(XRGraphicsConfig paramXRGraphicsConfig, int paramInt1, int paramInt2, ColorModel paramColorModel, Image paramImage, long paramLong, int paramInt3)
/*     */   {
/* 228 */     int i = paramInt3 > 1 ? 32 : 24;
/* 229 */     if (i == 24) {
/* 230 */       paramColorModel = new DirectColorModel(i, 16711680, 65280, 255);
/*     */     }
/*     */     else {
/* 233 */       paramColorModel = new DirectColorModel(i, 16711680, 65280, 255, -16777216);
/*     */     }
/*     */ 
/* 237 */     return new XRPixmapSurfaceData(paramXRGraphicsConfig, paramInt1, paramInt2, paramImage, getSurfaceType(paramXRGraphicsConfig, paramInt3), paramColorModel, paramLong, paramInt3, XRUtils.getPictureFormatForTransparency(paramInt3), i);
/*     */   }
/*     */ 
/*     */   protected XRSurfaceData(X11ComponentPeer paramX11ComponentPeer, XRGraphicsConfig paramXRGraphicsConfig, SurfaceType paramSurfaceType, ColorModel paramColorModel, int paramInt1, int paramInt2)
/*     */   {
/* 246 */     super(paramSurfaceType, paramColorModel);
/* 247 */     this.peer = paramX11ComponentPeer;
/* 248 */     this.graphicsConfig = paramXRGraphicsConfig;
/* 249 */     this.solidloops = this.graphicsConfig.getSolidLoops(paramSurfaceType);
/* 250 */     this.depth = paramInt1;
/* 251 */     initOps(paramX11ComponentPeer, this.graphicsConfig, paramInt1);
/*     */ 
/* 253 */     setBlitProxyKey(paramXRGraphicsConfig.getProxyKey());
/*     */   }
/*     */ 
/*     */   protected XRSurfaceData(XRBackend paramXRBackend) {
/* 257 */     super(IntRgbX11, new DirectColorModel(24, 16711680, 65280, 255));
/*     */ 
/* 259 */     this.renderQueue = paramXRBackend;
/*     */   }
/*     */ 
/*     */   public void initXRender(int paramInt)
/*     */   {
/*     */     try
/*     */     {
/* 269 */       SunToolkit.awtLock();
/* 270 */       initXRPicture(getNativeOps(), paramInt);
/* 271 */       this.renderQueue = XRCompositeManager.getInstance(this).getBackend();
/* 272 */       this.maskBuffer = XRCompositeManager.getInstance(this);
/*     */     } catch (Throwable localThrowable) {
/* 274 */       localThrowable.printStackTrace();
/*     */     } finally {
/* 276 */       SunToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static XRGraphicsConfig getGC(X11ComponentPeer paramX11ComponentPeer) {
/* 281 */     if (paramX11ComponentPeer != null) {
/* 282 */       return (XRGraphicsConfig)paramX11ComponentPeer.getGraphicsConfiguration();
/*     */     }
/* 284 */     GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
/*     */ 
/* 286 */     GraphicsDevice localGraphicsDevice = localGraphicsEnvironment.getDefaultScreenDevice();
/* 287 */     return (XRGraphicsConfig)localGraphicsDevice.getDefaultConfiguration();
/*     */   }
/*     */ 
/*     */   public abstract boolean canSourceSendExposures(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
/*     */ 
/*     */   public void validateCopyAreaGC(Region paramRegion, boolean paramBoolean)
/*     */   {
/* 310 */     if (this.validatedGCClip != paramRegion) {
/* 311 */       if (paramRegion != null)
/* 312 */         this.renderQueue.setGCClipRectangles(this.xgc, paramRegion);
/* 313 */       this.validatedGCClip = paramRegion;
/*     */     }
/*     */ 
/* 316 */     if (this.validatedExposures != paramBoolean) {
/* 317 */       this.validatedExposures = paramBoolean;
/* 318 */       this.renderQueue.setGCExposures(this.xgc, paramBoolean);
/*     */     }
/*     */ 
/* 321 */     if (this.validatedXorComp != null) {
/* 322 */       this.renderQueue.setGCMode(this.xgc, true);
/* 323 */       this.renderQueue.setGCForeground(this.xgc, this.validatedGCForegroundPixel);
/* 324 */       this.validatedXorComp = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean copyArea(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
/*     */   {
/* 330 */     if (this.xrpipe == null) {
/* 331 */       if (!isXRDrawableValid()) {
/* 332 */         return true;
/*     */       }
/* 334 */       makePipes();
/*     */     }
/* 336 */     CompositeType localCompositeType = paramSunGraphics2D.imageComp;
/* 337 */     if ((paramSunGraphics2D.transformState < 3) && ((CompositeType.SrcOverNoEa.equals(localCompositeType)) || (CompositeType.SrcNoEa.equals(localCompositeType))))
/*     */     {
/* 341 */       paramInt1 += paramSunGraphics2D.transX;
/* 342 */       paramInt2 += paramSunGraphics2D.transY;
/*     */       try {
/* 344 */         SunToolkit.awtLock();
/* 345 */         boolean bool = canSourceSendExposures(paramInt1, paramInt2, paramInt3, paramInt4);
/* 346 */         validateCopyAreaGC(paramSunGraphics2D.getCompClip(), bool);
/* 347 */         this.renderQueue.copyArea(this.xid, this.xid, this.xgc, paramInt1, paramInt2, paramInt3, paramInt4, paramInt1 + paramInt5, paramInt2 + paramInt6);
/*     */       } finally {
/* 349 */         SunToolkit.awtUnlock();
/*     */       }
/* 351 */       return true;
/*     */     }
/* 353 */     return false;
/*     */   }
/*     */ 
/*     */   public static SurfaceType getSurfaceType(XRGraphicsConfig paramXRGraphicsConfig, int paramInt)
/*     */   {
/* 362 */     SurfaceType localSurfaceType = null;
/*     */ 
/* 364 */     switch (paramInt) {
/*     */     case 1:
/* 366 */       localSurfaceType = IntRgbX11;
/* 367 */       break;
/*     */     case 2:
/*     */     case 3:
/* 371 */       localSurfaceType = IntArgbPreX11;
/*     */     }
/*     */ 
/* 375 */     return localSurfaceType;
/*     */   }
/*     */ 
/*     */   public void invalidate() {
/* 379 */     if (isValid()) {
/* 380 */       setInvalid();
/* 381 */       super.invalidate();
/*     */     }
/*     */   }
/*     */ 
/*     */   void validateAsSource(AffineTransform paramAffineTransform, int paramInt1, int paramInt2)
/*     */   {
/* 407 */     if (this.validatedClip != null) {
/* 408 */       this.validatedClip = null;
/* 409 */       this.renderQueue.setClipRectangles(this.picture, null);
/*     */     }
/*     */ 
/* 412 */     if ((this.validatedRepeat != paramInt1) && (paramInt1 != -1)) {
/* 413 */       this.validatedRepeat = paramInt1;
/* 414 */       this.renderQueue.setPictureRepeat(this.picture, paramInt1);
/*     */     }
/*     */ 
/* 417 */     if (paramAffineTransform == null) {
/* 418 */       if (this.transformInUse) {
/* 419 */         this.validatedSourceTransform.setToIdentity();
/* 420 */         this.renderQueue.setPictureTransform(this.picture, this.validatedSourceTransform);
/*     */ 
/* 422 */         this.transformInUse = false;
/*     */       }
/* 424 */     } else if ((!this.transformInUse) || ((this.transformInUse) && (!paramAffineTransform.equals(this.validatedSourceTransform))))
/*     */     {
/* 426 */       this.validatedSourceTransform.setTransform(paramAffineTransform.getScaleX(), paramAffineTransform.getShearY(), paramAffineTransform.getShearX(), paramAffineTransform.getScaleY(), paramAffineTransform.getTranslateX(), paramAffineTransform.getTranslateY());
/*     */ 
/* 432 */       this.renderQueue.setPictureTransform(this.picture, this.validatedSourceTransform);
/* 433 */       this.transformInUse = true;
/*     */     }
/*     */ 
/* 436 */     if ((paramInt2 != this.validatedFilter) && (paramInt2 != -1)) {
/* 437 */       this.renderQueue.setFilter(this.picture, paramInt2);
/* 438 */       this.validatedFilter = paramInt2;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void validateAsDestination(SunGraphics2D paramSunGraphics2D, Region paramRegion)
/*     */   {
/* 446 */     if (!isValid()) {
/* 447 */       throw new InvalidPipeException("bounds changed");
/*     */     }
/*     */ 
/* 450 */     int i = 0;
/* 451 */     if (paramRegion != this.validatedClip) {
/* 452 */       this.renderQueue.setClipRectangles(this.picture, paramRegion);
/* 453 */       this.validatedClip = paramRegion;
/* 454 */       i = 1;
/*     */     }
/*     */ 
/* 457 */     if ((paramSunGraphics2D != null) && (paramSunGraphics2D.compositeState == 2)) {
/* 458 */       if (this.validatedXorComp != paramSunGraphics2D.getComposite()) {
/* 459 */         this.validatedXorComp = ((XORComposite)paramSunGraphics2D.getComposite());
/* 460 */         int j = this.validatedXorComp.getXorPixel();
/* 461 */         this.renderQueue.setGCMode(this.xgc, false);
/*     */ 
/* 464 */         int k = paramSunGraphics2D.pixel;
/* 465 */         if (this.validatedGCForegroundPixel != k) {
/* 466 */           this.renderQueue.setGCForeground(this.xgc, k ^ j);
/* 467 */           this.validatedGCForegroundPixel = k;
/*     */         }
/*     */       }
/*     */ 
/* 471 */       if (i != 0)
/* 472 */         this.renderQueue.setGCClipRectangles(this.xgc, paramRegion);
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void makePipes()
/*     */   {
/* 481 */     if (this.xrpipe == null)
/*     */       try {
/* 483 */         SunToolkit.awtLock();
/* 484 */         this.xgc = XCreateGC(getNativeOps());
/*     */ 
/* 486 */         this.xrpipe = new XRRenderer(this.maskBuffer.getMaskBuffer());
/* 487 */         this.xrtxpipe = new PixelToShapeConverter(this.xrpipe);
/* 488 */         this.xrtextpipe = this.maskBuffer.getTextRenderer();
/* 489 */         this.xrDrawImage = new XRDrawImage();
/*     */ 
/* 491 */         if (JulesPathBuf.isCairoAvailable()) {
/* 492 */           this.aaShapePipe = new JulesShapePipe(XRCompositeManager.getInstance(this));
/*     */ 
/* 494 */           this.aaPixelToShapeConv = new PixelToShapeConverter(this.aaShapePipe);
/*     */         }
/*     */       } finally {
/* 497 */         SunToolkit.awtUnlock();
/*     */       }
/*     */   }
/*     */ 
/*     */   public long getGC()
/*     */   {
/* 655 */     return this.xgc;
/*     */   }
/*     */ 
/*     */   public int getPicture()
/*     */   {
/* 670 */     return this.picture;
/*     */   }
/*     */ 
/*     */   public int getXid() {
/* 674 */     return this.xid;
/*     */   }
/*     */ 
/*     */   public XRGraphicsConfig getGraphicsConfig() {
/* 678 */     return this.graphicsConfig;
/*     */   }
/*     */ 
/*     */   public static class LazyPipe extends ValidatePipe
/*     */   {
/*     */     public boolean validate(SunGraphics2D paramSunGraphics2D)
/*     */     {
/* 660 */       XRSurfaceData localXRSurfaceData = (XRSurfaceData)paramSunGraphics2D.surfaceData;
/* 661 */       if (!localXRSurfaceData.isXRDrawableValid()) {
/* 662 */         return false;
/*     */       }
/* 664 */       localXRSurfaceData.makePipes();
/* 665 */       return super.validate(paramSunGraphics2D);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class XRInternalSurfaceData extends XRSurfaceData
/*     */   {
/*     */     public XRInternalSurfaceData(XRBackend paramXRBackend, int paramInt, AffineTransform paramAffineTransform)
/*     */     {
/* 552 */       super();
/* 553 */       this.picture = paramInt;
/* 554 */       this.validatedSourceTransform = paramAffineTransform;
/*     */ 
/* 556 */       if (this.validatedSourceTransform != null)
/* 557 */         this.transformInUse = true;
/*     */     }
/*     */ 
/*     */     public boolean canSourceSendExposures(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */     {
/* 562 */       return false;
/*     */     }
/*     */ 
/*     */     public Rectangle getBounds() {
/* 566 */       return null;
/*     */     }
/*     */ 
/*     */     public Object getDestination() {
/* 570 */       return null;
/*     */     }
/*     */ 
/*     */     public SurfaceData getReplacement() {
/* 574 */       return null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class XRPixmapSurfaceData extends XRSurfaceData
/*     */   {
/*     */     Image offscreenImage;
/*     */     int width;
/*     */     int height;
/*     */     int transparency;
/*     */ 
/*     */     public XRPixmapSurfaceData(XRGraphicsConfig paramXRGraphicsConfig, int paramInt1, int paramInt2, Image paramImage, SurfaceType paramSurfaceType, ColorModel paramColorModel, long paramLong, int paramInt3, int paramInt4, int paramInt5) {
/* 589 */       super(paramXRGraphicsConfig, paramSurfaceType, paramColorModel, paramInt5, paramInt3);
/* 590 */       this.width = paramInt1;
/* 591 */       this.height = paramInt2;
/* 592 */       this.offscreenImage = paramImage;
/* 593 */       this.transparency = paramInt3;
/* 594 */       initSurface(paramInt5, paramInt1, paramInt2, paramLong, paramInt4);
/*     */ 
/* 596 */       initXRender(paramInt4);
/* 597 */       makePipes();
/*     */     }
/*     */ 
/*     */     public void initSurface(int paramInt1, int paramInt2, int paramInt3, long paramLong, int paramInt4)
/*     */     {
/*     */       try {
/* 603 */         SunToolkit.awtLock();
/* 604 */         XRInitSurface(paramInt1, paramInt2, paramInt3, paramLong, paramInt4);
/*     */       } finally {
/* 606 */         SunToolkit.awtUnlock();
/*     */       }
/*     */     }
/*     */ 
/*     */     public SurfaceData getReplacement() {
/* 611 */       return restoreContents(this.offscreenImage);
/*     */     }
/*     */ 
/*     */     public int getTransparency()
/*     */     {
/* 621 */       return this.transparency;
/*     */     }
/*     */ 
/*     */     public Rectangle getBounds() {
/* 625 */       return new Rectangle(this.width, this.height);
/*     */     }
/*     */ 
/*     */     public boolean canSourceSendExposures(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */     {
/* 630 */       return (paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 + paramInt3 > this.width) || (paramInt2 + paramInt4 > this.height);
/*     */     }
/*     */ 
/*     */     public void flush()
/*     */     {
/* 642 */       invalidate();
/* 643 */       flushNativeSurface();
/*     */     }
/*     */ 
/*     */     public Object getDestination()
/*     */     {
/* 650 */       return this.offscreenImage;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class XRWindowSurfaceData extends XRSurfaceData
/*     */   {
/*     */     public XRWindowSurfaceData(X11ComponentPeer paramX11ComponentPeer, XRGraphicsConfig paramXRGraphicsConfig, SurfaceType paramSurfaceType)
/*     */     {
/* 505 */       super(paramXRGraphicsConfig, paramSurfaceType, paramX11ComponentPeer.getColorModel(), paramX11ComponentPeer.getColorModel().getPixelSize(), 1);
/*     */ 
/* 508 */       if (isXRDrawableValid()) {
/* 509 */         initXRender(XRUtils.getPictureFormatForTransparency(1));
/*     */ 
/* 511 */         makePipes();
/*     */       }
/*     */     }
/*     */ 
/*     */     public SurfaceData getReplacement() {
/* 516 */       return this.peer.getSurfaceData();
/*     */     }
/*     */ 
/*     */     public Rectangle getBounds() {
/* 520 */       Rectangle localRectangle = this.peer.getBounds();
/* 521 */       localRectangle.x = (localRectangle.y = 0);
/* 522 */       return localRectangle;
/*     */     }
/*     */ 
/*     */     public boolean canSourceSendExposures(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */     {
/* 527 */       return true;
/*     */     }
/*     */ 
/*     */     public Object getDestination()
/*     */     {
/* 534 */       return this.peer.getTarget();
/*     */     }
/*     */ 
/*     */     public void invalidate() {
/*     */       try {
/* 539 */         SunToolkit.awtLock();
/* 540 */         freeXSDOPicture(getNativeOps());
/*     */       } finally {
/* 542 */         SunToolkit.awtUnlock();
/*     */       }
/*     */ 
/* 545 */       super.invalidate();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.xr.XRSurfaceData
 * JD-Core Version:    0.6.2
 */