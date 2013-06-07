/*      */ package java.awt.image;
/*      */ 
/*      */ import java.awt.Graphics;
/*      */ import java.awt.Graphics2D;
/*      */ import java.awt.GraphicsEnvironment;
/*      */ import java.awt.Image;
/*      */ import java.awt.Point;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.Transparency;
/*      */ import java.awt.color.ColorSpace;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Vector;
/*      */ import sun.awt.image.ByteComponentRaster;
/*      */ import sun.awt.image.BytePackedRaster;
/*      */ import sun.awt.image.IntegerComponentRaster;
/*      */ import sun.awt.image.OffScreenImageSource;
/*      */ import sun.awt.image.ShortComponentRaster;
/*      */ 
/*      */ public class BufferedImage extends Image
/*      */   implements WritableRenderedImage, Transparency
/*      */ {
/*   73 */   int imageType = 0;
/*      */   ColorModel colorModel;
/*      */   WritableRaster raster;
/*      */   OffScreenImageSource osis;
/*      */   Hashtable properties;
/*      */   boolean isAlphaPremultiplied;
/*      */   public static final int TYPE_CUSTOM = 0;
/*      */   public static final int TYPE_INT_RGB = 1;
/*      */   public static final int TYPE_INT_ARGB = 2;
/*      */   public static final int TYPE_INT_ARGB_PRE = 3;
/*      */   public static final int TYPE_INT_BGR = 4;
/*      */   public static final int TYPE_3BYTE_BGR = 5;
/*      */   public static final int TYPE_4BYTE_ABGR = 6;
/*      */   public static final int TYPE_4BYTE_ABGR_PRE = 7;
/*      */   public static final int TYPE_USHORT_565_RGB = 8;
/*      */   public static final int TYPE_USHORT_555_RGB = 9;
/*      */   public static final int TYPE_BYTE_GRAY = 10;
/*      */   public static final int TYPE_USHORT_GRAY = 11;
/*      */   public static final int TYPE_BYTE_BINARY = 12;
/*      */   public static final int TYPE_BYTE_INDEXED = 13;
/*      */   private static final int DCM_RED_MASK = 16711680;
/*      */   private static final int DCM_GREEN_MASK = 65280;
/*      */   private static final int DCM_BLUE_MASK = 255;
/*      */   private static final int DCM_ALPHA_MASK = -16777216;
/*      */   private static final int DCM_565_RED_MASK = 63488;
/*      */   private static final int DCM_565_GRN_MASK = 2016;
/*      */   private static final int DCM_565_BLU_MASK = 31;
/*      */   private static final int DCM_555_RED_MASK = 31744;
/*      */   private static final int DCM_555_GRN_MASK = 992;
/*      */   private static final int DCM_555_BLU_MASK = 31;
/*      */   private static final int DCM_BGR_RED_MASK = 255;
/*      */   private static final int DCM_BGR_GRN_MASK = 65280;
/*      */   private static final int DCM_BGR_BLU_MASK = 16711680;
/*      */ 
/*      */   private static native void initIDs();
/*      */ 
/*      */   public BufferedImage(int paramInt1, int paramInt2, int paramInt3)
/*      */   {
/*      */     Object localObject;
/*      */     int[] arrayOfInt1;
/*      */     int[] arrayOfInt2;
/*  320 */     switch (paramInt3)
/*      */     {
/*      */     case 1:
/*  323 */       this.colorModel = new DirectColorModel(24, 16711680, 65280, 255, 0);
/*      */ 
/*  329 */       this.raster = this.colorModel.createCompatibleWritableRaster(paramInt1, paramInt2);
/*      */ 
/*  332 */       break;
/*      */     case 2:
/*  336 */       this.colorModel = ColorModel.getRGBdefault();
/*      */ 
/*  338 */       this.raster = this.colorModel.createCompatibleWritableRaster(paramInt1, paramInt2);
/*      */ 
/*  341 */       break;
/*      */     case 3:
/*  345 */       this.colorModel = new DirectColorModel(ColorSpace.getInstance(1000), 32, 16711680, 65280, 255, -16777216, true, 3);
/*      */ 
/*  357 */       this.raster = this.colorModel.createCompatibleWritableRaster(paramInt1, paramInt2);
/*      */ 
/*  360 */       break;
/*      */     case 4:
/*  364 */       this.colorModel = new DirectColorModel(24, 255, 65280, 16711680);
/*      */ 
/*  369 */       this.raster = this.colorModel.createCompatibleWritableRaster(paramInt1, paramInt2);
/*      */ 
/*  372 */       break;
/*      */     case 5:
/*  376 */       localObject = ColorSpace.getInstance(1000);
/*  377 */       arrayOfInt1 = new int[] { 8, 8, 8 };
/*  378 */       arrayOfInt2 = new int[] { 2, 1, 0 };
/*  379 */       this.colorModel = new ComponentColorModel((ColorSpace)localObject, arrayOfInt1, false, false, 1, 0);
/*      */ 
/*  382 */       this.raster = Raster.createInterleavedRaster(0, paramInt1, paramInt2, paramInt1 * 3, 3, arrayOfInt2, null);
/*      */ 
/*  387 */       break;
/*      */     case 6:
/*  391 */       localObject = ColorSpace.getInstance(1000);
/*  392 */       arrayOfInt1 = new int[] { 8, 8, 8, 8 };
/*  393 */       arrayOfInt2 = new int[] { 3, 2, 1, 0 };
/*  394 */       this.colorModel = new ComponentColorModel((ColorSpace)localObject, arrayOfInt1, true, false, 3, 0);
/*      */ 
/*  397 */       this.raster = Raster.createInterleavedRaster(0, paramInt1, paramInt2, paramInt1 * 4, 4, arrayOfInt2, null);
/*      */ 
/*  402 */       break;
/*      */     case 7:
/*  406 */       localObject = ColorSpace.getInstance(1000);
/*  407 */       arrayOfInt1 = new int[] { 8, 8, 8, 8 };
/*  408 */       arrayOfInt2 = new int[] { 3, 2, 1, 0 };
/*  409 */       this.colorModel = new ComponentColorModel((ColorSpace)localObject, arrayOfInt1, true, true, 3, 0);
/*      */ 
/*  412 */       this.raster = Raster.createInterleavedRaster(0, paramInt1, paramInt2, paramInt1 * 4, 4, arrayOfInt2, null);
/*      */ 
/*  417 */       break;
/*      */     case 10:
/*  421 */       localObject = ColorSpace.getInstance(1003);
/*  422 */       arrayOfInt1 = new int[] { 8 };
/*  423 */       this.colorModel = new ComponentColorModel((ColorSpace)localObject, arrayOfInt1, false, true, 1, 0);
/*      */ 
/*  426 */       this.raster = this.colorModel.createCompatibleWritableRaster(paramInt1, paramInt2);
/*      */ 
/*  429 */       break;
/*      */     case 11:
/*  433 */       localObject = ColorSpace.getInstance(1003);
/*  434 */       arrayOfInt1 = new int[] { 16 };
/*  435 */       this.colorModel = new ComponentColorModel((ColorSpace)localObject, arrayOfInt1, false, true, 1, 1);
/*      */ 
/*  438 */       this.raster = this.colorModel.createCompatibleWritableRaster(paramInt1, paramInt2);
/*      */ 
/*  441 */       break;
/*      */     case 12:
/*  445 */       localObject = new byte[] { 0, -1 };
/*      */ 
/*  447 */       this.colorModel = new IndexColorModel(1, 2, (byte[])localObject, (byte[])localObject, (byte[])localObject);
/*  448 */       this.raster = Raster.createPackedRaster(0, paramInt1, paramInt2, 1, 1, null);
/*      */ 
/*  451 */       break;
/*      */     case 13:
/*  456 */       localObject = new int[256];
/*  457 */       int i = 0;
/*  458 */       for (int j = 0; j < 256; j += 51) {
/*  459 */         for (k = 0; k < 256; k += 51) {
/*  460 */           for (int m = 0; m < 256; m += 51) {
/*  461 */             localObject[(i++)] = (j << 16 | k << 8 | m);
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  466 */       j = 256 / (256 - i);
/*      */ 
/*  469 */       int k = j * 3;
/*  470 */       for (; i < 256; i++) {
/*  471 */         localObject[i] = (k << 16 | k << 8 | k);
/*  472 */         k += j;
/*      */       }
/*      */ 
/*  475 */       this.colorModel = new IndexColorModel(8, 256, (int[])localObject, 0, false, -1, 0);
/*      */ 
/*  477 */       this.raster = Raster.createInterleavedRaster(0, paramInt1, paramInt2, 1, null);
/*      */ 
/*  480 */       break;
/*      */     case 8:
/*  484 */       this.colorModel = new DirectColorModel(16, 63488, 2016, 31);
/*      */ 
/*  489 */       this.raster = this.colorModel.createCompatibleWritableRaster(paramInt1, paramInt2);
/*      */ 
/*  492 */       break;
/*      */     case 9:
/*  496 */       this.colorModel = new DirectColorModel(15, 31744, 992, 31);
/*      */ 
/*  501 */       this.raster = this.colorModel.createCompatibleWritableRaster(paramInt1, paramInt2);
/*      */ 
/*  504 */       break;
/*      */     default:
/*  507 */       throw new IllegalArgumentException("Unknown image type " + paramInt3);
/*      */     }
/*      */ 
/*  511 */     this.imageType = paramInt3;
/*      */   }
/*      */ 
/*      */   public BufferedImage(int paramInt1, int paramInt2, int paramInt3, IndexColorModel paramIndexColorModel)
/*      */   {
/*  541 */     if ((paramIndexColorModel.hasAlpha()) && (paramIndexColorModel.isAlphaPremultiplied())) {
/*  542 */       throw new IllegalArgumentException("This image types do not have premultiplied alpha.");
/*      */     }
/*      */ 
/*  546 */     switch (paramInt3)
/*      */     {
/*      */     case 12:
/*  549 */       int j = paramIndexColorModel.getMapSize();
/*      */       int i;
/*  550 */       if (j <= 2)
/*  551 */         i = 1;
/*  552 */       else if (j <= 4)
/*  553 */         i = 2;
/*  554 */       else if (j <= 16)
/*  555 */         i = 4;
/*      */       else {
/*  557 */         throw new IllegalArgumentException("Color map for TYPE_BYTE_BINARY must have no more than 16 entries");
/*      */       }
/*      */ 
/*  561 */       this.raster = Raster.createPackedRaster(0, paramInt1, paramInt2, 1, i, null);
/*      */ 
/*  563 */       break;
/*      */     case 13:
/*  566 */       this.raster = Raster.createInterleavedRaster(0, paramInt1, paramInt2, 1, null);
/*      */ 
/*  568 */       break;
/*      */     default:
/*  570 */       throw new IllegalArgumentException("Invalid image type (" + paramInt3 + ").  Image type must" + " be either TYPE_BYTE_BINARY or " + " TYPE_BYTE_INDEXED");
/*      */     }
/*      */ 
/*  576 */     if (!paramIndexColorModel.isCompatibleRaster(this.raster)) {
/*  577 */       throw new IllegalArgumentException("Incompatible image type and IndexColorModel");
/*      */     }
/*      */ 
/*  580 */     this.colorModel = paramIndexColorModel;
/*  581 */     this.imageType = paramInt3;
/*      */   }
/*      */ 
/*      */   public BufferedImage(ColorModel paramColorModel, WritableRaster paramWritableRaster, boolean paramBoolean, Hashtable<?, ?> paramHashtable)
/*      */   {
/*  627 */     if (!paramColorModel.isCompatibleRaster(paramWritableRaster)) {
/*  628 */       throw new IllegalArgumentException("Raster " + paramWritableRaster + " is incompatible with ColorModel " + paramColorModel);
/*      */     }
/*      */ 
/*  634 */     if ((paramWritableRaster.minX != 0) || (paramWritableRaster.minY != 0)) {
/*  635 */       throw new IllegalArgumentException("Raster " + paramWritableRaster + " has minX or minY not equal to zero: " + paramWritableRaster.minX + " " + paramWritableRaster.minY);
/*      */     }
/*      */ 
/*  641 */     this.colorModel = paramColorModel;
/*  642 */     this.raster = paramWritableRaster;
/*  643 */     this.properties = paramHashtable;
/*  644 */     int i = paramWritableRaster.getNumBands();
/*  645 */     boolean bool = paramColorModel.isAlphaPremultiplied();
/*      */ 
/*  650 */     coerceData(paramBoolean);
/*      */ 
/*  652 */     SampleModel localSampleModel = paramWritableRaster.getSampleModel();
/*  653 */     ColorSpace localColorSpace = paramColorModel.getColorSpace();
/*  654 */     int j = localColorSpace.getType();
/*  655 */     if (j != 5) {
/*  656 */       if ((j == 6) && ((paramColorModel instanceof ComponentColorModel)))
/*      */       {
/*  659 */         if (((localSampleModel instanceof ComponentSampleModel)) && (((ComponentSampleModel)localSampleModel).getPixelStride() != i))
/*      */         {
/*  661 */           this.imageType = 0;
/*  662 */         } else if (((paramWritableRaster instanceof ByteComponentRaster)) && (paramWritableRaster.getNumBands() == 1) && (paramColorModel.getComponentSize(0) == 8) && (((ByteComponentRaster)paramWritableRaster).getPixelStride() == 1))
/*      */         {
/*  666 */           this.imageType = 10;
/*  667 */         } else if (((paramWritableRaster instanceof ShortComponentRaster)) && (paramWritableRaster.getNumBands() == 1) && (paramColorModel.getComponentSize(0) == 16) && (((ShortComponentRaster)paramWritableRaster).getPixelStride() == 1))
/*      */         {
/*  671 */           this.imageType = 11;
/*      */         }
/*      */       }
/*  674 */       else this.imageType = 0;
/*      */       return;
/*      */     }
/*      */     Object localObject1;
/*      */     int k;
/*      */     Object localObject2;
/*      */     int i1;
/*  679 */     if (((paramWritableRaster instanceof IntegerComponentRaster)) && ((i == 3) || (i == 4)))
/*      */     {
/*  681 */       localObject1 = (IntegerComponentRaster)paramWritableRaster;
/*      */ 
/*  685 */       k = paramColorModel.getPixelSize();
/*  686 */       if ((((IntegerComponentRaster)localObject1).getPixelStride() == 1) && ((paramColorModel instanceof DirectColorModel)) && ((k == 32) || (k == 24)))
/*      */       {
/*  691 */         localObject2 = (DirectColorModel)paramColorModel;
/*  692 */         int m = ((DirectColorModel)localObject2).getRedMask();
/*  693 */         int n = ((DirectColorModel)localObject2).getGreenMask();
/*  694 */         i1 = ((DirectColorModel)localObject2).getBlueMask();
/*  695 */         if ((m == 16711680) && (n == 65280) && (i1 == 255))
/*      */         {
/*  698 */           if (((DirectColorModel)localObject2).getAlphaMask() == -16777216) {
/*  699 */             this.imageType = (bool ? 3 : 2);
/*      */           }
/*  705 */           else if (!((DirectColorModel)localObject2).hasAlpha()) {
/*  706 */             this.imageType = 1;
/*      */           }
/*      */ 
/*      */         }
/*  710 */         else if ((m == 255) && (n == 65280) && (i1 == 16711680))
/*      */         {
/*  712 */           if (!((DirectColorModel)localObject2).hasAlpha()) {
/*  713 */             this.imageType = 4;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  718 */     else if (((paramColorModel instanceof IndexColorModel)) && (i == 1) && ((!paramColorModel.hasAlpha()) || (!bool)))
/*      */     {
/*  721 */       localObject1 = (IndexColorModel)paramColorModel;
/*  722 */       k = ((IndexColorModel)localObject1).getPixelSize();
/*      */ 
/*  724 */       if ((paramWritableRaster instanceof BytePackedRaster)) {
/*  725 */         this.imageType = 12;
/*      */       }
/*  727 */       else if ((paramWritableRaster instanceof ByteComponentRaster)) {
/*  728 */         localObject2 = (ByteComponentRaster)paramWritableRaster;
/*  729 */         if ((((ByteComponentRaster)localObject2).getPixelStride() == 1) && (k <= 8)) {
/*  730 */           this.imageType = 13;
/*      */         }
/*      */       }
/*      */     }
/*  734 */     else if (((paramWritableRaster instanceof ShortComponentRaster)) && ((paramColorModel instanceof DirectColorModel)) && (i == 3) && (!paramColorModel.hasAlpha()))
/*      */     {
/*  739 */       localObject1 = (DirectColorModel)paramColorModel;
/*  740 */       if (((DirectColorModel)localObject1).getRedMask() == 63488) {
/*  741 */         if ((((DirectColorModel)localObject1).getGreenMask() == 2016) && (((DirectColorModel)localObject1).getBlueMask() == 31))
/*      */         {
/*  743 */           this.imageType = 8;
/*      */         }
/*      */       }
/*  746 */       else if ((((DirectColorModel)localObject1).getRedMask() == 31744) && 
/*  747 */         (((DirectColorModel)localObject1).getGreenMask() == 992) && (((DirectColorModel)localObject1).getBlueMask() == 31))
/*      */       {
/*  749 */         this.imageType = 9;
/*      */       }
/*      */ 
/*      */     }
/*  753 */     else if (((paramWritableRaster instanceof ByteComponentRaster)) && ((paramColorModel instanceof ComponentColorModel)) && ((paramWritableRaster.getSampleModel() instanceof PixelInterleavedSampleModel)) && ((i == 3) || (i == 4)))
/*      */     {
/*  758 */       localObject1 = (ComponentColorModel)paramColorModel;
/*  759 */       PixelInterleavedSampleModel localPixelInterleavedSampleModel = (PixelInterleavedSampleModel)paramWritableRaster.getSampleModel();
/*      */ 
/*  761 */       localObject2 = (ByteComponentRaster)paramWritableRaster;
/*  762 */       int[] arrayOfInt1 = localPixelInterleavedSampleModel.getBandOffsets();
/*  763 */       if (((ComponentColorModel)localObject1).getNumComponents() != i) {
/*  764 */         throw new RasterFormatException("Number of components in ColorModel (" + ((ComponentColorModel)localObject1).getNumComponents() + ") does not match # in " + " Raster (" + i + ")");
/*      */       }
/*      */ 
/*  770 */       int[] arrayOfInt2 = ((ComponentColorModel)localObject1).getComponentSize();
/*  771 */       i1 = 1;
/*  772 */       for (int i2 = 0; i2 < i; i2++) {
/*  773 */         if (arrayOfInt2[i2] != 8) {
/*  774 */           i1 = 0;
/*  775 */           break;
/*      */         }
/*      */       }
/*  778 */       if ((i1 != 0) && (arrayOfInt1[0] == i - 1) && (arrayOfInt1[1] == i - 2) && (arrayOfInt1[2] == i - 3))
/*      */       {
/*  783 */         if (i == 3) {
/*  784 */           this.imageType = 5;
/*      */         }
/*  786 */         else if (arrayOfInt1[3] == 0)
/*  787 */           this.imageType = (bool ? 7 : 6);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getType()
/*      */   {
/*  815 */     return this.imageType;
/*      */   }
/*      */ 
/*      */   public ColorModel getColorModel()
/*      */   {
/*  824 */     return this.colorModel;
/*      */   }
/*      */ 
/*      */   public WritableRaster getRaster()
/*      */   {
/*  833 */     return this.raster;
/*      */   }
/*      */ 
/*      */   public WritableRaster getAlphaRaster()
/*      */   {
/*  859 */     return this.colorModel.getAlphaRaster(this.raster);
/*      */   }
/*      */ 
/*      */   public int getRGB(int paramInt1, int paramInt2)
/*      */   {
/*  888 */     return this.colorModel.getRGB(this.raster.getDataElements(paramInt1, paramInt2, null));
/*      */   }
/*      */ 
/*      */   public int[] getRGB(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, int paramInt5, int paramInt6)
/*      */   {
/*  925 */     int i = paramInt5;
/*      */ 
/*  928 */     int k = this.raster.getNumBands();
/*  929 */     int m = this.raster.getDataBuffer().getDataType();
/*      */     Object localObject;
/*  930 */     switch (m) {
/*      */     case 0:
/*  932 */       localObject = new byte[k];
/*  933 */       break;
/*      */     case 1:
/*  935 */       localObject = new short[k];
/*  936 */       break;
/*      */     case 3:
/*  938 */       localObject = new int[k];
/*  939 */       break;
/*      */     case 4:
/*  941 */       localObject = new float[k];
/*  942 */       break;
/*      */     case 5:
/*  944 */       localObject = new double[k];
/*  945 */       break;
/*      */     case 2:
/*      */     default:
/*  947 */       throw new IllegalArgumentException("Unknown data buffer type: " + m);
/*      */     }
/*      */ 
/*  951 */     if (paramArrayOfInt == null) {
/*  952 */       paramArrayOfInt = new int[paramInt5 + paramInt4 * paramInt6];
/*      */     }
/*      */ 
/*  955 */     for (int n = paramInt2; n < paramInt2 + paramInt4; i += paramInt6) {
/*  956 */       int j = i;
/*  957 */       for (int i1 = paramInt1; i1 < paramInt1 + paramInt3; i1++)
/*  958 */         paramArrayOfInt[(j++)] = this.colorModel.getRGB(this.raster.getDataElements(i1, n, localObject));
/*  955 */       n++;
/*      */     }
/*      */ 
/*  964 */     return paramArrayOfInt;
/*      */   }
/*      */ 
/*      */   public synchronized void setRGB(int paramInt1, int paramInt2, int paramInt3)
/*      */   {
/*  988 */     this.raster.setDataElements(paramInt1, paramInt2, this.colorModel.getDataElements(paramInt3, null));
/*      */   }
/*      */ 
/*      */   public void setRGB(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, int paramInt5, int paramInt6)
/*      */   {
/* 1023 */     int i = paramInt5;
/*      */ 
/* 1025 */     Object localObject = null;
/*      */ 
/* 1027 */     for (int k = paramInt2; k < paramInt2 + paramInt4; i += paramInt6) {
/* 1028 */       int j = i;
/* 1029 */       for (int m = paramInt1; m < paramInt1 + paramInt3; m++) {
/* 1030 */         localObject = this.colorModel.getDataElements(paramArrayOfInt[(j++)], localObject);
/* 1031 */         this.raster.setDataElements(m, k, localObject);
/*      */       }
/* 1027 */       k++;
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getWidth()
/*      */   {
/* 1042 */     return this.raster.getWidth();
/*      */   }
/*      */ 
/*      */   public int getHeight()
/*      */   {
/* 1050 */     return this.raster.getHeight();
/*      */   }
/*      */ 
/*      */   public int getWidth(ImageObserver paramImageObserver)
/*      */   {
/* 1059 */     return this.raster.getWidth();
/*      */   }
/*      */ 
/*      */   public int getHeight(ImageObserver paramImageObserver)
/*      */   {
/* 1068 */     return this.raster.getHeight();
/*      */   }
/*      */ 
/*      */   public ImageProducer getSource()
/*      */   {
/* 1078 */     if (this.osis == null) {
/* 1079 */       if (this.properties == null) {
/* 1080 */         this.properties = new Hashtable();
/*      */       }
/* 1082 */       this.osis = new OffScreenImageSource(this, this.properties);
/*      */     }
/* 1084 */     return this.osis;
/*      */   }
/*      */ 
/*      */   public Object getProperty(String paramString, ImageObserver paramImageObserver)
/*      */   {
/* 1109 */     return getProperty(paramString);
/*      */   }
/*      */ 
/*      */   public Object getProperty(String paramString)
/*      */   {
/* 1120 */     if (paramString == null) {
/* 1121 */       throw new NullPointerException("null property name is not allowed");
/*      */     }
/* 1123 */     if (this.properties == null) {
/* 1124 */       return Image.UndefinedProperty;
/*      */     }
/* 1126 */     Object localObject = this.properties.get(paramString);
/* 1127 */     if (localObject == null) {
/* 1128 */       localObject = Image.UndefinedProperty;
/*      */     }
/* 1130 */     return localObject;
/*      */   }
/*      */ 
/*      */   public Graphics getGraphics()
/*      */   {
/* 1142 */     return createGraphics();
/*      */   }
/*      */ 
/*      */   public Graphics2D createGraphics()
/*      */   {
/* 1152 */     GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
/*      */ 
/* 1154 */     return localGraphicsEnvironment.createGraphics(this);
/*      */   }
/*      */ 
/*      */   public BufferedImage getSubimage(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/* 1173 */     return new BufferedImage(this.colorModel, this.raster.createWritableChild(paramInt1, paramInt2, paramInt3, paramInt4, 0, 0, null), this.colorModel.isAlphaPremultiplied(), this.properties);
/*      */   }
/*      */ 
/*      */   public boolean isAlphaPremultiplied()
/*      */   {
/* 1187 */     return this.colorModel.isAlphaPremultiplied();
/*      */   }
/*      */ 
/*      */   public void coerceData(boolean paramBoolean)
/*      */   {
/* 1199 */     if ((this.colorModel.hasAlpha()) && (this.colorModel.isAlphaPremultiplied() != paramBoolean))
/*      */     {
/* 1202 */       this.colorModel = this.colorModel.coerceData(this.raster, paramBoolean);
/*      */     }
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/* 1213 */     return "BufferedImage@" + Integer.toHexString(hashCode()) + ": type = " + this.imageType + " " + this.colorModel + " " + this.raster;
/*      */   }
/*      */ 
/*      */   public Vector<RenderedImage> getSources()
/*      */   {
/* 1234 */     return null;
/*      */   }
/*      */ 
/*      */   public String[] getPropertyNames()
/*      */   {
/* 1246 */     return null;
/*      */   }
/*      */ 
/*      */   public int getMinX()
/*      */   {
/* 1256 */     return this.raster.getMinX();
/*      */   }
/*      */ 
/*      */   public int getMinY()
/*      */   {
/* 1266 */     return this.raster.getMinY();
/*      */   }
/*      */ 
/*      */   public SampleModel getSampleModel()
/*      */   {
/* 1276 */     return this.raster.getSampleModel();
/*      */   }
/*      */ 
/*      */   public int getNumXTiles()
/*      */   {
/* 1285 */     return 1;
/*      */   }
/*      */ 
/*      */   public int getNumYTiles()
/*      */   {
/* 1294 */     return 1;
/*      */   }
/*      */ 
/*      */   public int getMinTileX()
/*      */   {
/* 1303 */     return 0;
/*      */   }
/*      */ 
/*      */   public int getMinTileY()
/*      */   {
/* 1312 */     return 0;
/*      */   }
/*      */ 
/*      */   public int getTileWidth()
/*      */   {
/* 1320 */     return this.raster.getWidth();
/*      */   }
/*      */ 
/*      */   public int getTileHeight()
/*      */   {
/* 1328 */     return this.raster.getHeight();
/*      */   }
/*      */ 
/*      */   public int getTileGridXOffset()
/*      */   {
/* 1338 */     return this.raster.getSampleModelTranslateX();
/*      */   }
/*      */ 
/*      */   public int getTileGridYOffset()
/*      */   {
/* 1348 */     return this.raster.getSampleModelTranslateY();
/*      */   }
/*      */ 
/*      */   public Raster getTile(int paramInt1, int paramInt2)
/*      */   {
/* 1366 */     if ((paramInt1 == 0) && (paramInt2 == 0)) {
/* 1367 */       return this.raster;
/*      */     }
/* 1369 */     throw new ArrayIndexOutOfBoundsException("BufferedImages only have one tile with index 0,0");
/*      */   }
/*      */ 
/*      */   public Raster getData()
/*      */   {
/* 1385 */     int i = this.raster.getWidth();
/* 1386 */     int j = this.raster.getHeight();
/* 1387 */     int k = this.raster.getMinX();
/* 1388 */     int m = this.raster.getMinY();
/* 1389 */     WritableRaster localWritableRaster = Raster.createWritableRaster(this.raster.getSampleModel(), new Point(this.raster.getSampleModelTranslateX(), this.raster.getSampleModelTranslateY()));
/*      */ 
/* 1394 */     Object localObject = null;
/*      */ 
/* 1396 */     for (int n = m; n < m + j; n++) {
/* 1397 */       localObject = this.raster.getDataElements(k, n, i, 1, localObject);
/* 1398 */       localWritableRaster.setDataElements(k, n, i, 1, localObject);
/*      */     }
/* 1400 */     return localWritableRaster;
/*      */   }
/*      */ 
/*      */   public Raster getData(Rectangle paramRectangle)
/*      */   {
/* 1415 */     SampleModel localSampleModel1 = this.raster.getSampleModel();
/* 1416 */     SampleModel localSampleModel2 = localSampleModel1.createCompatibleSampleModel(paramRectangle.width, paramRectangle.height);
/*      */ 
/* 1418 */     WritableRaster localWritableRaster = Raster.createWritableRaster(localSampleModel2, paramRectangle.getLocation());
/*      */ 
/* 1420 */     int i = paramRectangle.width;
/* 1421 */     int j = paramRectangle.height;
/* 1422 */     int k = paramRectangle.x;
/* 1423 */     int m = paramRectangle.y;
/*      */ 
/* 1425 */     Object localObject = null;
/*      */ 
/* 1427 */     for (int n = m; n < m + j; n++) {
/* 1428 */       localObject = this.raster.getDataElements(k, n, i, 1, localObject);
/* 1429 */       localWritableRaster.setDataElements(k, n, i, 1, localObject);
/*      */     }
/* 1431 */     return localWritableRaster;
/*      */   }
/*      */ 
/*      */   public WritableRaster copyData(WritableRaster paramWritableRaster)
/*      */   {
/* 1450 */     if (paramWritableRaster == null) {
/* 1451 */       return (WritableRaster)getData();
/*      */     }
/* 1453 */     int i = paramWritableRaster.getWidth();
/* 1454 */     int j = paramWritableRaster.getHeight();
/* 1455 */     int k = paramWritableRaster.getMinX();
/* 1456 */     int m = paramWritableRaster.getMinY();
/*      */ 
/* 1458 */     Object localObject = null;
/*      */ 
/* 1460 */     for (int n = m; n < m + j; n++) {
/* 1461 */       localObject = this.raster.getDataElements(k, n, i, 1, localObject);
/* 1462 */       paramWritableRaster.setDataElements(k, n, i, 1, localObject);
/*      */     }
/*      */ 
/* 1465 */     return paramWritableRaster;
/*      */   }
/*      */ 
/*      */   public void setData(Raster paramRaster)
/*      */   {
/* 1479 */     int i = paramRaster.getWidth();
/* 1480 */     int j = paramRaster.getHeight();
/* 1481 */     int k = paramRaster.getMinX();
/* 1482 */     int m = paramRaster.getMinY();
/*      */ 
/* 1484 */     int[] arrayOfInt = null;
/*      */ 
/* 1487 */     Rectangle localRectangle1 = new Rectangle(k, m, i, j);
/* 1488 */     Rectangle localRectangle2 = new Rectangle(0, 0, this.raster.width, this.raster.height);
/* 1489 */     Rectangle localRectangle3 = localRectangle1.intersection(localRectangle2);
/* 1490 */     if (localRectangle3.isEmpty()) {
/* 1491 */       return;
/*      */     }
/* 1493 */     i = localRectangle3.width;
/* 1494 */     j = localRectangle3.height;
/* 1495 */     k = localRectangle3.x;
/* 1496 */     m = localRectangle3.y;
/*      */ 
/* 1500 */     for (int n = m; n < m + j; n++) {
/* 1501 */       arrayOfInt = paramRaster.getPixels(k, n, i, 1, arrayOfInt);
/* 1502 */       this.raster.setPixels(k, n, i, 1, arrayOfInt);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addTileObserver(TileObserver paramTileObserver)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void removeTileObserver(TileObserver paramTileObserver)
/*      */   {
/*      */   }
/*      */ 
/*      */   public boolean isTileWritable(int paramInt1, int paramInt2)
/*      */   {
/* 1536 */     if ((paramInt1 == 0) && (paramInt2 == 0)) {
/* 1537 */       return true;
/*      */     }
/* 1539 */     throw new IllegalArgumentException("Only 1 tile in image");
/*      */   }
/*      */ 
/*      */   public Point[] getWritableTileIndices()
/*      */   {
/* 1551 */     Point[] arrayOfPoint = new Point[1];
/* 1552 */     arrayOfPoint[0] = new Point(0, 0);
/*      */ 
/* 1554 */     return arrayOfPoint;
/*      */   }
/*      */ 
/*      */   public boolean hasTileWriters()
/*      */   {
/* 1567 */     return true;
/*      */   }
/*      */ 
/*      */   public WritableRaster getWritableTile(int paramInt1, int paramInt2)
/*      */   {
/* 1580 */     return this.raster;
/*      */   }
/*      */ 
/*      */   public void releaseWritableTile(int paramInt1, int paramInt2)
/*      */   {
/*      */   }
/*      */ 
/*      */   public int getTransparency()
/*      */   {
/* 1607 */     return this.colorModel.getTransparency();
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  291 */     ColorModel.loadLibraries();
/*  292 */     initIDs();
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.image.BufferedImage
 * JD-Core Version:    0.6.2
 */