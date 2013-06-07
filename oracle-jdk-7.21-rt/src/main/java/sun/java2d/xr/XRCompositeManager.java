/*     */ package sun.java2d.xr;
/*     */ 
/*     */ import java.awt.AlphaComposite;
/*     */ import java.awt.Composite;
/*     */ import java.awt.Paint;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.geom.NoninvertibleTransformException;
/*     */ import sun.font.XRTextRenderer;
/*     */ import sun.java2d.SunGraphics2D;
/*     */ import sun.java2d.jules.TrapezoidList;
/*     */ import sun.java2d.loops.XORComposite;
/*     */ 
/*     */ public class XRCompositeManager
/*     */ {
/*  45 */   private static boolean enableGradCache = true;
/*     */   private static XRCompositeManager instance;
/*     */   XRSurfaceData src;
/*     */   XRSurfaceData texture;
/*     */   XRSurfaceData gradient;
/*  51 */   int alphaMask = 0;
/*     */ 
/*  53 */   XRColor solidColor = new XRColor();
/*  54 */   float extraAlpha = 1.0F;
/*  55 */   byte compRule = 3;
/*  56 */   XRColor alphaColor = new XRColor();
/*     */   XRSurfaceData solidSrcPict;
/*     */   int alphaMaskPict;
/*     */   int gradCachePixmap;
/*     */   int gradCachePicture;
/*  63 */   boolean xorEnabled = false;
/*  64 */   int validatedPixel = 0;
/*     */   Composite validatedComp;
/*     */   Paint validatedPaint;
/*  67 */   float validatedExtraAlpha = 1.0F;
/*     */   XRBackend con;
/*     */   MaskTileManager maskBuffer;
/*     */   XRTextRenderer textRenderer;
/*     */   XRMaskImage maskImage;
/*     */ 
/*     */   public static synchronized XRCompositeManager getInstance(XRSurfaceData paramXRSurfaceData)
/*     */   {
/*  76 */     if (instance == null) {
/*  77 */       instance = new XRCompositeManager(paramXRSurfaceData);
/*     */     }
/*  79 */     return instance;
/*     */   }
/*     */ 
/*     */   private XRCompositeManager(XRSurfaceData paramXRSurfaceData) {
/*  83 */     this.con = new XRBackendNative();
/*     */ 
/*  86 */     String str = System.getProperty("sun.java2d.xrgradcache");
/*  87 */     enableGradCache = (str == null) || ((!str.equalsIgnoreCase("false")) && (!str.equalsIgnoreCase("f")));
/*     */ 
/*  91 */     XRPaints.register(this);
/*     */ 
/*  93 */     initResources(paramXRSurfaceData);
/*     */ 
/*  95 */     this.maskBuffer = new MaskTileManager(this, paramXRSurfaceData.getXid());
/*  96 */     this.textRenderer = new XRTextRenderer(this);
/*  97 */     this.maskImage = new XRMaskImage(this, paramXRSurfaceData.getXid());
/*     */   }
/*     */ 
/*     */   public void initResources(XRSurfaceData paramXRSurfaceData) {
/* 101 */     int i = paramXRSurfaceData.getXid();
/*     */ 
/* 103 */     int j = this.con.createPixmap(i, 32, 1, 1);
/* 104 */     int k = this.con.createPicture(j, 0);
/*     */ 
/* 106 */     this.con.setPictureRepeat(k, 1);
/* 107 */     this.con.renderRectangle(k, (byte)1, XRColor.FULL_ALPHA, 0, 0, 1, 1);
/*     */ 
/* 109 */     this.solidSrcPict = new XRSurfaceData.XRInternalSurfaceData(this.con, k, null);
/*     */ 
/* 111 */     setForeground(0);
/*     */ 
/* 113 */     int m = this.con.createPixmap(i, 8, 1, 1);
/* 114 */     this.alphaMaskPict = this.con.createPicture(m, 2);
/*     */ 
/* 116 */     this.con.setPictureRepeat(this.alphaMaskPict, 1);
/* 117 */     this.con.renderRectangle(this.alphaMaskPict, (byte)0, XRColor.NO_ALPHA, 0, 0, 1, 1);
/*     */ 
/* 120 */     if (enableGradCache) {
/* 121 */       this.gradCachePixmap = this.con.createPixmap(i, 32, 256, 256);
/*     */ 
/* 123 */       this.gradCachePicture = this.con.createPicture(this.gradCachePixmap, 0);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setForeground(int paramInt)
/*     */   {
/* 129 */     this.solidColor.setColorValues(paramInt, false);
/* 130 */     this.con.renderRectangle(this.solidSrcPict.picture, (byte)1, this.solidColor, 0, 0, 1, 1);
/*     */   }
/*     */ 
/*     */   public void setGradientPaint(XRSurfaceData paramXRSurfaceData)
/*     */   {
/* 135 */     if (this.gradient != null) {
/* 136 */       this.con.freePicture(this.gradient.picture);
/*     */     }
/* 138 */     this.gradient = paramXRSurfaceData;
/* 139 */     this.src = paramXRSurfaceData;
/*     */   }
/*     */ 
/*     */   public void setTexturePaint(XRSurfaceData paramXRSurfaceData) {
/* 143 */     this.texture = paramXRSurfaceData;
/* 144 */     this.src = paramXRSurfaceData;
/*     */   }
/*     */ 
/*     */   public void XRResetPaint() {
/* 148 */     this.src = this.solidSrcPict;
/*     */   }
/*     */ 
/*     */   public void validateCompositeState(Composite paramComposite, AffineTransform paramAffineTransform, Paint paramPaint, SunGraphics2D paramSunGraphics2D)
/*     */   {
/* 153 */     int i = (paramPaint != this.validatedPaint) || (paramPaint == null) ? 1 : 0;
/*     */ 
/* 156 */     if (paramComposite != this.validatedComp) {
/* 157 */       if (paramComposite != null) {
/* 158 */         setComposite(paramComposite);
/*     */       } else {
/* 160 */         paramComposite = AlphaComposite.getInstance(3);
/* 161 */         setComposite(paramComposite);
/*     */       }
/*     */ 
/* 165 */       i = 1;
/* 166 */       this.validatedComp = paramComposite;
/*     */     }
/*     */ 
/* 169 */     if ((paramSunGraphics2D != null) && (this.validatedPixel != paramSunGraphics2D.pixel)) {
/* 170 */       this.validatedPixel = paramSunGraphics2D.pixel;
/* 171 */       setForeground(this.validatedPixel);
/*     */     }
/*     */ 
/* 175 */     if (i != 0) {
/* 176 */       if ((paramPaint != null) && (paramSunGraphics2D != null) && (paramSunGraphics2D.paintState >= 2))
/*     */       {
/* 178 */         XRPaints.setPaint(paramSunGraphics2D, paramPaint);
/*     */       }
/* 180 */       else XRResetPaint();
/*     */ 
/* 182 */       this.validatedPaint = paramPaint;
/*     */     }
/*     */ 
/* 185 */     if (this.src != this.solidSrcPict) {
/* 186 */       AffineTransform localAffineTransform = (AffineTransform)paramAffineTransform.clone();
/*     */       try {
/* 188 */         localAffineTransform.invert();
/*     */       } catch (NoninvertibleTransformException localNoninvertibleTransformException) {
/* 190 */         localAffineTransform.setToIdentity();
/*     */       }
/* 192 */       this.src.validateAsSource(localAffineTransform, -1, -1);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void setComposite(Composite paramComposite) {
/* 197 */     if ((paramComposite instanceof AlphaComposite)) {
/* 198 */       AlphaComposite localAlphaComposite = (AlphaComposite)paramComposite;
/* 199 */       this.validatedExtraAlpha = localAlphaComposite.getAlpha();
/*     */ 
/* 201 */       this.compRule = XRUtils.j2dAlphaCompToXR(localAlphaComposite.getRule());
/* 202 */       this.extraAlpha = this.validatedExtraAlpha;
/*     */ 
/* 204 */       if (this.extraAlpha == 1.0F) {
/* 205 */         this.alphaMask = 0;
/* 206 */         this.alphaColor.alpha = XRColor.FULL_ALPHA.alpha;
/*     */       } else {
/* 208 */         this.alphaColor.alpha = XRColor.byteToXRColorValue((int)(this.extraAlpha * 255.0F));
/*     */ 
/* 210 */         this.alphaMask = this.alphaMaskPict;
/* 211 */         this.con.renderRectangle(this.alphaMaskPict, (byte)1, this.alphaColor, 0, 0, 1, 1);
/*     */       }
/*     */ 
/* 215 */       this.xorEnabled = false;
/* 216 */     } else if ((paramComposite instanceof XORComposite))
/*     */     {
/* 218 */       this.xorEnabled = true;
/*     */     } else {
/* 220 */       throw new InternalError("Composite accaleration not implemented for: " + paramComposite.getClass().getName());
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean maskRequired()
/*     */   {
/* 227 */     return (!this.xorEnabled) && ((this.src != this.solidSrcPict) || ((this.src == this.solidSrcPict) && (this.solidColor.alpha != 65535)) || (this.extraAlpha != 1.0F));
/*     */   }
/*     */ 
/*     */   public void XRComposite(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10, int paramInt11)
/*     */   {
/* 234 */     int i = paramInt1 == 0 ? this.src.picture : paramInt1;
/* 235 */     int j = paramInt4;
/* 236 */     int k = paramInt5;
/*     */ 
/* 238 */     if ((enableGradCache) && (this.gradient != null) && (i == this.gradient.picture))
/*     */     {
/* 240 */       this.con.renderComposite((byte)1, this.gradient.picture, 0, this.gradCachePicture, paramInt4, paramInt5, 0, 0, 0, 0, paramInt10, paramInt11);
/*     */ 
/* 243 */       j = 0;
/* 244 */       k = 0;
/* 245 */       i = this.gradCachePicture;
/*     */     }
/*     */ 
/* 248 */     this.con.renderComposite(this.compRule, i, paramInt2, paramInt3, j, k, paramInt6, paramInt7, paramInt8, paramInt9, paramInt10, paramInt11);
/*     */   }
/*     */ 
/*     */   public void XRCompositeTraps(int paramInt1, int paramInt2, int paramInt3, TrapezoidList paramTrapezoidList)
/*     */   {
/* 254 */     int i = 0;
/* 255 */     int j = 0;
/*     */ 
/* 257 */     if (paramTrapezoidList.getP1YLeft(0) < paramTrapezoidList.getP2YLeft(0)) {
/* 258 */       i = paramTrapezoidList.getP1XLeft(0);
/* 259 */       j = paramTrapezoidList.getP1YLeft(0);
/*     */     } else {
/* 261 */       i = paramTrapezoidList.getP2XLeft(0);
/* 262 */       j = paramTrapezoidList.getP2YLeft(0);
/*     */     }
/*     */ 
/* 265 */     i = (int)Math.floor(XRUtils.XFixedToDouble(i));
/*     */ 
/* 267 */     j = (int)Math.floor(XRUtils.XFixedToDouble(j));
/*     */ 
/* 270 */     this.con.renderCompositeTrapezoids(this.compRule, this.src.picture, 2, paramInt1, i, j, paramTrapezoidList);
/*     */   }
/*     */ 
/*     */   public void XRRenderRectangles(XRSurfaceData paramXRSurfaceData, GrowableRectArray paramGrowableRectArray)
/*     */   {
/* 276 */     if (this.xorEnabled)
/* 277 */       this.con.GCRectangles(paramXRSurfaceData.getXid(), paramXRSurfaceData.getGC(), paramGrowableRectArray);
/*     */     else
/* 279 */       this.con.renderRectangles(paramXRSurfaceData.getPicture(), this.compRule, this.solidColor, paramGrowableRectArray);
/*     */   }
/*     */ 
/*     */   public void compositeBlit(XRSurfaceData paramXRSurfaceData1, XRSurfaceData paramXRSurfaceData2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
/*     */   {
/* 285 */     this.con.renderComposite(this.compRule, paramXRSurfaceData1.picture, this.alphaMask, paramXRSurfaceData2.picture, paramInt1, paramInt2, 0, 0, paramInt3, paramInt4, paramInt5, paramInt6);
/*     */   }
/*     */ 
/*     */   public void compositeText(int paramInt1, int paramInt2, int paramInt3, GrowableEltArray paramGrowableEltArray)
/*     */   {
/* 291 */     this.con.XRenderCompositeText(this.compRule, this.src.picture, paramInt1, paramInt3, 0, 0, 0, 0, paramInt2, paramGrowableEltArray);
/*     */   }
/*     */ 
/*     */   public XRColor getMaskColor()
/*     */   {
/* 296 */     return !isTexturePaintActive() ? XRColor.FULL_ALPHA : getAlphaColor();
/*     */   }
/*     */ 
/*     */   public int getExtraAlphaMask() {
/* 300 */     return this.alphaMask;
/*     */   }
/*     */ 
/*     */   public boolean isTexturePaintActive() {
/* 304 */     return this.src == this.texture;
/*     */   }
/*     */ 
/*     */   public XRColor getAlphaColor() {
/* 308 */     return this.alphaColor;
/*     */   }
/*     */ 
/*     */   public XRBackend getBackend() {
/* 312 */     return this.con;
/*     */   }
/*     */ 
/*     */   public float getExtraAlpha() {
/* 316 */     return this.validatedExtraAlpha;
/*     */   }
/*     */ 
/*     */   public byte getCompRule() {
/* 320 */     return this.compRule;
/*     */   }
/*     */ 
/*     */   public XRTextRenderer getTextRenderer() {
/* 324 */     return this.textRenderer;
/*     */   }
/*     */ 
/*     */   public MaskTileManager getMaskBuffer() {
/* 328 */     return this.maskBuffer;
/*     */   }
/*     */ 
/*     */   public XRMaskImage getMaskImage() {
/* 332 */     return this.maskImage;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.xr.XRCompositeManager
 * JD-Core Version:    0.6.2
 */