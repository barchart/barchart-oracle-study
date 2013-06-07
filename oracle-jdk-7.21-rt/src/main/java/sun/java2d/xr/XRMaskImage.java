/*     */ package sun.java2d.xr;
/*     */ 
/*     */ import java.awt.Dimension;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.geom.NoninvertibleTransformException;
/*     */ 
/*     */ public class XRMaskImage
/*     */ {
/*     */   private static final int MASK_SCALE_FACTOR = 8;
/*     */   private static final int BLIT_MASK_SIZE = 8;
/*  43 */   Dimension blitMaskDimensions = new Dimension(8, 8);
/*     */   int blitMaskPixmap;
/*     */   int blitMaskPicture;
/*  46 */   int lastMaskWidth = 0;
/*  47 */   int lastMaskHeight = 0;
/*     */   AffineTransform lastMaskTransform;
/*     */   XRCompositeManager xrMgr;
/*     */   XRBackend con;
/*     */ 
/*     */   public XRMaskImage(XRCompositeManager paramXRCompositeManager, int paramInt)
/*     */   {
/*  54 */     this.xrMgr = paramXRCompositeManager;
/*  55 */     this.con = paramXRCompositeManager.getBackend();
/*     */ 
/*  57 */     initBlitMask(paramInt, 8, 8);
/*     */   }
/*     */ 
/*     */   public int prepareBlitMask(XRSurfaceData paramXRSurfaceData, AffineTransform paramAffineTransform, int paramInt1, int paramInt2)
/*     */   {
/*  68 */     int i = Math.max(paramInt1 / 8, 1);
/*  69 */     int j = Math.max(paramInt2 / 8, 1);
/*  70 */     paramAffineTransform.scale(paramInt1 / i, paramInt2 / j);
/*     */     try
/*     */     {
/*  73 */       paramAffineTransform.invert();
/*     */     } catch (NoninvertibleTransformException localNoninvertibleTransformException) {
/*  75 */       paramAffineTransform.setToIdentity();
/*     */     }
/*     */ 
/*  78 */     ensureBlitMaskSize(i, j);
/*     */ 
/*  80 */     if ((this.lastMaskTransform == null) || (!this.lastMaskTransform.equals(paramAffineTransform))) {
/*  81 */       this.con.setPictureTransform(this.blitMaskPicture, paramAffineTransform);
/*  82 */       this.lastMaskTransform = paramAffineTransform;
/*     */     }
/*     */ 
/*  85 */     if ((this.lastMaskWidth != i) || (this.lastMaskHeight != j))
/*     */     {
/*  87 */       if ((this.lastMaskWidth > i) || (this.lastMaskHeight > j)) {
/*  88 */         this.con.renderRectangle(this.blitMaskPicture, (byte)0, XRColor.NO_ALPHA, 0, 0, this.lastMaskWidth, this.lastMaskHeight);
/*     */       }
/*     */ 
/*  91 */       this.con.renderRectangle(this.blitMaskPicture, (byte)1, this.xrMgr.getAlphaColor(), 0, 0, i, j);
/*     */     }
/*     */ 
/*  94 */     this.lastMaskWidth = i;
/*  95 */     this.lastMaskHeight = j;
/*     */ 
/*  97 */     return this.blitMaskPicture;
/*     */   }
/*     */ 
/*     */   private void initBlitMask(int paramInt1, int paramInt2, int paramInt3) {
/* 101 */     int i = this.con.createPixmap(paramInt1, 8, paramInt2, paramInt3);
/* 102 */     int j = this.con.createPicture(i, 2);
/*     */ 
/* 105 */     if (this.blitMaskPixmap != 0) {
/* 106 */       this.con.freePixmap(this.blitMaskPixmap);
/* 107 */       this.con.freePicture(this.blitMaskPicture);
/*     */     }
/*     */ 
/* 110 */     this.blitMaskPixmap = i;
/* 111 */     this.blitMaskPicture = j;
/*     */ 
/* 113 */     this.con.renderRectangle(this.blitMaskPicture, (byte)0, XRColor.NO_ALPHA, 0, 0, paramInt2, paramInt3);
/*     */ 
/* 115 */     this.blitMaskDimensions.width = paramInt2;
/* 116 */     this.blitMaskDimensions.height = paramInt3;
/* 117 */     this.lastMaskWidth = 0;
/* 118 */     this.lastMaskHeight = 0;
/* 119 */     this.lastMaskTransform = null;
/*     */   }
/*     */ 
/*     */   private void ensureBlitMaskSize(int paramInt1, int paramInt2) {
/* 123 */     if ((paramInt1 > this.blitMaskDimensions.width) || (paramInt2 > this.blitMaskDimensions.height)) {
/* 124 */       int i = Math.max(paramInt1, this.blitMaskDimensions.width);
/* 125 */       int j = Math.max(paramInt2, this.blitMaskDimensions.height);
/* 126 */       initBlitMask(this.blitMaskPixmap, i, j);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.xr.XRMaskImage
 * JD-Core Version:    0.6.2
 */