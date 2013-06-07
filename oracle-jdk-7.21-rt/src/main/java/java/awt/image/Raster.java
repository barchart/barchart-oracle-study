/*      */ package java.awt.image;
/*      */ 
/*      */ import java.awt.Point;
/*      */ import java.awt.Rectangle;
/*      */ import sun.awt.image.ByteBandedRaster;
/*      */ import sun.awt.image.ByteInterleavedRaster;
/*      */ import sun.awt.image.BytePackedRaster;
/*      */ import sun.awt.image.IntegerInterleavedRaster;
/*      */ import sun.awt.image.ShortBandedRaster;
/*      */ import sun.awt.image.ShortInterleavedRaster;
/*      */ import sun.awt.image.SunWritableRaster;
/*      */ 
/*      */ public class Raster
/*      */ {
/*      */   protected SampleModel sampleModel;
/*      */   protected DataBuffer dataBuffer;
/*      */   protected int minX;
/*      */   protected int minY;
/*      */   protected int width;
/*      */   protected int height;
/*      */   protected int sampleModelTranslateX;
/*      */   protected int sampleModelTranslateY;
/*      */   protected int numBands;
/*      */   protected int numDataElements;
/*      */   protected Raster parent;
/*      */ 
/*      */   private static native void initIDs();
/*      */ 
/*      */   public static WritableRaster createInterleavedRaster(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Point paramPoint)
/*      */   {
/*  208 */     int[] arrayOfInt = new int[paramInt4];
/*  209 */     for (int i = 0; i < paramInt4; i++) {
/*  210 */       arrayOfInt[i] = i;
/*      */     }
/*  212 */     return createInterleavedRaster(paramInt1, paramInt2, paramInt3, paramInt2 * paramInt4, paramInt4, arrayOfInt, paramPoint);
/*      */   }
/*      */ 
/*      */   public static WritableRaster createInterleavedRaster(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int[] paramArrayOfInt, Point paramPoint)
/*      */   {
/*  260 */     int i = paramArrayOfInt.length;
/*      */ 
/*  262 */     int j = paramArrayOfInt[0];
/*  263 */     for (int k = 1; k < i; k++) {
/*  264 */       if (paramArrayOfInt[k] > j) {
/*  265 */         j = paramArrayOfInt[k];
/*      */       }
/*      */     }
/*  268 */     k = j + paramInt4 * (paramInt3 - 1) + paramInt5 * (paramInt2 - 1) + 1;
/*      */     Object localObject;
/*  269 */     switch (paramInt1) {
/*      */     case 0:
/*  271 */       localObject = new DataBufferByte(k);
/*  272 */       break;
/*      */     case 1:
/*  275 */       localObject = new DataBufferUShort(k);
/*  276 */       break;
/*      */     default:
/*  279 */       throw new IllegalArgumentException("Unsupported data type " + paramInt1);
/*      */     }
/*      */ 
/*  283 */     return createInterleavedRaster((DataBuffer)localObject, paramInt2, paramInt3, paramInt4, paramInt5, paramArrayOfInt, paramPoint);
/*      */   }
/*      */ 
/*      */   public static WritableRaster createBandedRaster(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Point paramPoint)
/*      */   {
/*  317 */     if (paramInt4 < 1) {
/*  318 */       throw new ArrayIndexOutOfBoundsException("Number of bands (" + paramInt4 + ") must" + " be greater than 0");
/*      */     }
/*      */ 
/*  322 */     int[] arrayOfInt1 = new int[paramInt4];
/*  323 */     int[] arrayOfInt2 = new int[paramInt4];
/*  324 */     for (int i = 0; i < paramInt4; i++) {
/*  325 */       arrayOfInt1[i] = i;
/*  326 */       arrayOfInt2[i] = 0;
/*      */     }
/*      */ 
/*  329 */     return createBandedRaster(paramInt1, paramInt2, paramInt3, paramInt2, arrayOfInt1, arrayOfInt2, paramPoint);
/*      */   }
/*      */ 
/*      */   public static WritableRaster createBandedRaster(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt1, int[] paramArrayOfInt2, Point paramPoint)
/*      */   {
/*  377 */     int i = paramArrayOfInt2.length;
/*      */ 
/*  379 */     if (paramArrayOfInt1 == null) {
/*  380 */       throw new ArrayIndexOutOfBoundsException("Bank indices array is null");
/*      */     }
/*      */ 
/*  383 */     if (paramArrayOfInt2 == null) {
/*  384 */       throw new ArrayIndexOutOfBoundsException("Band offsets array is null");
/*      */     }
/*      */ 
/*  389 */     int j = paramArrayOfInt1[0];
/*  390 */     int k = paramArrayOfInt2[0];
/*  391 */     for (int m = 1; m < i; m++) {
/*  392 */       if (paramArrayOfInt1[m] > j) {
/*  393 */         j = paramArrayOfInt1[m];
/*      */       }
/*  395 */       if (paramArrayOfInt2[m] > k) {
/*  396 */         k = paramArrayOfInt2[m];
/*      */       }
/*      */     }
/*  399 */     m = j + 1;
/*  400 */     int n = k + paramInt4 * (paramInt3 - 1) + (paramInt2 - 1) + 1;
/*      */     Object localObject;
/*  402 */     switch (paramInt1) {
/*      */     case 0:
/*  404 */       localObject = new DataBufferByte(n, m);
/*  405 */       break;
/*      */     case 1:
/*  408 */       localObject = new DataBufferUShort(n, m);
/*  409 */       break;
/*      */     case 3:
/*  412 */       localObject = new DataBufferInt(n, m);
/*  413 */       break;
/*      */     case 2:
/*      */     default:
/*  416 */       throw new IllegalArgumentException("Unsupported data type " + paramInt1);
/*      */     }
/*      */ 
/*  420 */     return createBandedRaster((DataBuffer)localObject, paramInt2, paramInt3, paramInt4, paramArrayOfInt1, paramArrayOfInt2, paramPoint);
/*      */   }
/*      */ 
/*      */   public static WritableRaster createPackedRaster(int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt, Point paramPoint)
/*      */   {
/*      */     Object localObject;
/*  460 */     switch (paramInt1) {
/*      */     case 0:
/*  462 */       localObject = new DataBufferByte(paramInt2 * paramInt3);
/*  463 */       break;
/*      */     case 1:
/*  466 */       localObject = new DataBufferUShort(paramInt2 * paramInt3);
/*  467 */       break;
/*      */     case 3:
/*  470 */       localObject = new DataBufferInt(paramInt2 * paramInt3);
/*  471 */       break;
/*      */     case 2:
/*      */     default:
/*  474 */       throw new IllegalArgumentException("Unsupported data type " + paramInt1);
/*      */     }
/*      */ 
/*  478 */     return createPackedRaster((DataBuffer)localObject, paramInt2, paramInt3, paramInt2, paramArrayOfInt, paramPoint);
/*      */   }
/*      */ 
/*      */   public static WritableRaster createPackedRaster(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, Point paramPoint)
/*      */   {
/*  532 */     if (paramInt4 <= 0) {
/*  533 */       throw new IllegalArgumentException("Number of bands (" + paramInt4 + ") must be greater than 0");
/*      */     }
/*      */ 
/*  537 */     if (paramInt5 <= 0) {
/*  538 */       throw new IllegalArgumentException("Bits per band (" + paramInt5 + ") must be greater than 0");
/*      */     }
/*      */ 
/*  542 */     if (paramInt4 != 1) {
/*  543 */       int[] arrayOfInt = new int[paramInt4];
/*  544 */       int i = (1 << paramInt5) - 1;
/*  545 */       int j = (paramInt4 - 1) * paramInt5;
/*      */ 
/*  548 */       if (j + paramInt5 > DataBuffer.getDataTypeSize(paramInt1)) {
/*  549 */         throw new IllegalArgumentException("bitsPerBand(" + paramInt5 + ") * bands is " + " greater than data type " + "size.");
/*      */       }
/*      */ 
/*  554 */       switch (paramInt1) {
/*      */       case 0:
/*      */       case 1:
/*      */       case 3:
/*  558 */         break;
/*      */       case 2:
/*      */       default:
/*  560 */         throw new IllegalArgumentException("Unsupported data type " + paramInt1);
/*      */       }
/*      */ 
/*  564 */       for (int k = 0; k < paramInt4; k++) {
/*  565 */         arrayOfInt[k] = (i << j);
/*  566 */         j -= paramInt5;
/*      */       }
/*      */ 
/*  569 */       return createPackedRaster(paramInt1, paramInt2, paramInt3, arrayOfInt, paramPoint);
/*      */     }
/*      */ 
/*  572 */     double d = paramInt2;
/*      */     Object localObject;
/*  573 */     switch (paramInt1) {
/*      */     case 0:
/*  575 */       localObject = new DataBufferByte((int)Math.ceil(d / (8 / paramInt5)) * paramInt3);
/*  576 */       break;
/*      */     case 1:
/*  579 */       localObject = new DataBufferUShort((int)Math.ceil(d / (16 / paramInt5)) * paramInt3);
/*  580 */       break;
/*      */     case 3:
/*  583 */       localObject = new DataBufferInt((int)Math.ceil(d / (32 / paramInt5)) * paramInt3);
/*  584 */       break;
/*      */     case 2:
/*      */     default:
/*  587 */       throw new IllegalArgumentException("Unsupported data type " + paramInt1);
/*      */     }
/*      */ 
/*  591 */     return createPackedRaster((DataBuffer)localObject, paramInt2, paramInt3, paramInt5, paramPoint);
/*      */   }
/*      */ 
/*      */   public static WritableRaster createInterleavedRaster(DataBuffer paramDataBuffer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, Point paramPoint)
/*      */   {
/*  636 */     if (paramDataBuffer == null) {
/*  637 */       throw new NullPointerException("DataBuffer cannot be null");
/*      */     }
/*  639 */     if (paramPoint == null) {
/*  640 */       paramPoint = new Point(0, 0);
/*      */     }
/*  642 */     int i = paramDataBuffer.getDataType();
/*      */ 
/*  644 */     PixelInterleavedSampleModel localPixelInterleavedSampleModel = new PixelInterleavedSampleModel(i, paramInt1, paramInt2, paramInt4, paramInt3, paramArrayOfInt);
/*      */ 
/*  649 */     switch (i) {
/*      */     case 0:
/*  651 */       return new ByteInterleavedRaster(localPixelInterleavedSampleModel, paramDataBuffer, paramPoint);
/*      */     case 1:
/*  654 */       return new ShortInterleavedRaster(localPixelInterleavedSampleModel, paramDataBuffer, paramPoint);
/*      */     }
/*      */ 
/*  657 */     throw new IllegalArgumentException("Unsupported data type " + i);
/*      */   }
/*      */ 
/*      */   public static WritableRaster createBandedRaster(DataBuffer paramDataBuffer, int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt1, int[] paramArrayOfInt2, Point paramPoint)
/*      */   {
/*  698 */     if (paramDataBuffer == null) {
/*  699 */       throw new NullPointerException("DataBuffer cannot be null");
/*      */     }
/*  701 */     if (paramPoint == null) {
/*  702 */       paramPoint = new Point(0, 0);
/*      */     }
/*  704 */     int i = paramDataBuffer.getDataType();
/*      */ 
/*  706 */     int j = paramArrayOfInt1.length;
/*  707 */     if (paramArrayOfInt2.length != j) {
/*  708 */       throw new IllegalArgumentException("bankIndices.length != bandOffsets.length");
/*      */     }
/*      */ 
/*  712 */     BandedSampleModel localBandedSampleModel = new BandedSampleModel(i, paramInt1, paramInt2, paramInt3, paramArrayOfInt1, paramArrayOfInt2);
/*      */ 
/*  717 */     switch (i) {
/*      */     case 0:
/*  719 */       return new ByteBandedRaster(localBandedSampleModel, paramDataBuffer, paramPoint);
/*      */     case 1:
/*  722 */       return new ShortBandedRaster(localBandedSampleModel, paramDataBuffer, paramPoint);
/*      */     case 3:
/*  725 */       return new SunWritableRaster(localBandedSampleModel, paramDataBuffer, paramPoint);
/*      */     case 2:
/*      */     }
/*  728 */     throw new IllegalArgumentException("Unsupported data type " + i);
/*      */   }
/*      */ 
/*      */   public static WritableRaster createPackedRaster(DataBuffer paramDataBuffer, int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt, Point paramPoint)
/*      */   {
/*  768 */     if (paramDataBuffer == null) {
/*  769 */       throw new NullPointerException("DataBuffer cannot be null");
/*      */     }
/*  771 */     if (paramPoint == null) {
/*  772 */       paramPoint = new Point(0, 0);
/*      */     }
/*  774 */     int i = paramDataBuffer.getDataType();
/*      */ 
/*  776 */     SinglePixelPackedSampleModel localSinglePixelPackedSampleModel = new SinglePixelPackedSampleModel(i, paramInt1, paramInt2, paramInt3, paramArrayOfInt);
/*      */ 
/*  780 */     switch (i) {
/*      */     case 0:
/*  782 */       return new ByteInterleavedRaster(localSinglePixelPackedSampleModel, paramDataBuffer, paramPoint);
/*      */     case 1:
/*  785 */       return new ShortInterleavedRaster(localSinglePixelPackedSampleModel, paramDataBuffer, paramPoint);
/*      */     case 3:
/*  788 */       return new IntegerInterleavedRaster(localSinglePixelPackedSampleModel, paramDataBuffer, paramPoint);
/*      */     case 2:
/*      */     }
/*  791 */     throw new IllegalArgumentException("Unsupported data type " + i);
/*      */   }
/*      */ 
/*      */   public static WritableRaster createPackedRaster(DataBuffer paramDataBuffer, int paramInt1, int paramInt2, int paramInt3, Point paramPoint)
/*      */   {
/*  828 */     if (paramDataBuffer == null) {
/*  829 */       throw new NullPointerException("DataBuffer cannot be null");
/*      */     }
/*  831 */     if (paramPoint == null) {
/*  832 */       paramPoint = new Point(0, 0);
/*      */     }
/*  834 */     int i = paramDataBuffer.getDataType();
/*      */ 
/*  836 */     if ((i != 0) && (i != 1) && (i != 3))
/*      */     {
/*  839 */       throw new IllegalArgumentException("Unsupported data type " + i);
/*      */     }
/*      */ 
/*  843 */     if (paramDataBuffer.getNumBanks() != 1) {
/*  844 */       throw new RasterFormatException("DataBuffer for packed Rasters must only have 1 bank.");
/*      */     }
/*      */ 
/*  849 */     MultiPixelPackedSampleModel localMultiPixelPackedSampleModel = new MultiPixelPackedSampleModel(i, paramInt1, paramInt2, paramInt3);
/*      */ 
/*  852 */     if ((i == 0) && ((paramInt3 == 1) || (paramInt3 == 2) || (paramInt3 == 4)))
/*      */     {
/*  854 */       return new BytePackedRaster(localMultiPixelPackedSampleModel, paramDataBuffer, paramPoint);
/*      */     }
/*  856 */     return new SunWritableRaster(localMultiPixelPackedSampleModel, paramDataBuffer, paramPoint);
/*      */   }
/*      */ 
/*      */   public static Raster createRaster(SampleModel paramSampleModel, DataBuffer paramDataBuffer, Point paramPoint)
/*      */   {
/*  885 */     if ((paramSampleModel == null) || (paramDataBuffer == null)) {
/*  886 */       throw new NullPointerException("SampleModel and DataBuffer cannot be null");
/*      */     }
/*      */ 
/*  889 */     if (paramPoint == null) {
/*  890 */       paramPoint = new Point(0, 0);
/*      */     }
/*  892 */     int i = paramSampleModel.getDataType();
/*      */ 
/*  894 */     if ((paramSampleModel instanceof PixelInterleavedSampleModel))
/*  895 */       switch (i) {
/*      */       case 0:
/*  897 */         return new ByteInterleavedRaster(paramSampleModel, paramDataBuffer, paramPoint);
/*      */       case 1:
/*  900 */         return new ShortInterleavedRaster(paramSampleModel, paramDataBuffer, paramPoint);
/*      */       }
/*  902 */     else if ((paramSampleModel instanceof SinglePixelPackedSampleModel))
/*  903 */       switch (i) {
/*      */       case 0:
/*  905 */         return new ByteInterleavedRaster(paramSampleModel, paramDataBuffer, paramPoint);
/*      */       case 1:
/*  908 */         return new ShortInterleavedRaster(paramSampleModel, paramDataBuffer, paramPoint);
/*      */       case 3:
/*  911 */         return new IntegerInterleavedRaster(paramSampleModel, paramDataBuffer, paramPoint);
/*      */       case 2:
/*      */       }
/*  913 */     else if (((paramSampleModel instanceof MultiPixelPackedSampleModel)) && (i == 0) && (paramSampleModel.getSampleSize(0) < 8))
/*      */     {
/*  916 */       return new BytePackedRaster(paramSampleModel, paramDataBuffer, paramPoint);
/*      */     }
/*      */ 
/*  921 */     return new Raster(paramSampleModel, paramDataBuffer, paramPoint);
/*      */   }
/*      */ 
/*      */   public static WritableRaster createWritableRaster(SampleModel paramSampleModel, Point paramPoint)
/*      */   {
/*  940 */     if (paramPoint == null) {
/*  941 */       paramPoint = new Point(0, 0);
/*      */     }
/*      */ 
/*  944 */     return createWritableRaster(paramSampleModel, paramSampleModel.createDataBuffer(), paramPoint);
/*      */   }
/*      */ 
/*      */   public static WritableRaster createWritableRaster(SampleModel paramSampleModel, DataBuffer paramDataBuffer, Point paramPoint)
/*      */   {
/*  971 */     if ((paramSampleModel == null) || (paramDataBuffer == null)) {
/*  972 */       throw new NullPointerException("SampleModel and DataBuffer cannot be null");
/*      */     }
/*  974 */     if (paramPoint == null) {
/*  975 */       paramPoint = new Point(0, 0);
/*      */     }
/*      */ 
/*  978 */     int i = paramSampleModel.getDataType();
/*      */ 
/*  980 */     if ((paramSampleModel instanceof PixelInterleavedSampleModel))
/*  981 */       switch (i) {
/*      */       case 0:
/*  983 */         return new ByteInterleavedRaster(paramSampleModel, paramDataBuffer, paramPoint);
/*      */       case 1:
/*  986 */         return new ShortInterleavedRaster(paramSampleModel, paramDataBuffer, paramPoint);
/*      */       }
/*  988 */     else if ((paramSampleModel instanceof SinglePixelPackedSampleModel))
/*  989 */       switch (i) {
/*      */       case 0:
/*  991 */         return new ByteInterleavedRaster(paramSampleModel, paramDataBuffer, paramPoint);
/*      */       case 1:
/*  994 */         return new ShortInterleavedRaster(paramSampleModel, paramDataBuffer, paramPoint);
/*      */       case 3:
/*  997 */         return new IntegerInterleavedRaster(paramSampleModel, paramDataBuffer, paramPoint);
/*      */       case 2:
/*      */       }
/*  999 */     else if (((paramSampleModel instanceof MultiPixelPackedSampleModel)) && (i == 0) && (paramSampleModel.getSampleSize(0) < 8))
/*      */     {
/* 1002 */       return new BytePackedRaster(paramSampleModel, paramDataBuffer, paramPoint);
/*      */     }
/*      */ 
/* 1007 */     return new SunWritableRaster(paramSampleModel, paramDataBuffer, paramPoint);
/*      */   }
/*      */ 
/*      */   protected Raster(SampleModel paramSampleModel, Point paramPoint)
/*      */   {
/* 1026 */     this(paramSampleModel, paramSampleModel.createDataBuffer(), new Rectangle(paramPoint.x, paramPoint.y, paramSampleModel.getWidth(), paramSampleModel.getHeight()), paramPoint, null);
/*      */   }
/*      */ 
/*      */   protected Raster(SampleModel paramSampleModel, DataBuffer paramDataBuffer, Point paramPoint)
/*      */   {
/* 1054 */     this(paramSampleModel, paramDataBuffer, new Rectangle(paramPoint.x, paramPoint.y, paramSampleModel.getWidth(), paramSampleModel.getHeight()), paramPoint, null);
/*      */   }
/*      */ 
/*      */   protected Raster(SampleModel paramSampleModel, DataBuffer paramDataBuffer, Rectangle paramRectangle, Point paramPoint, Raster paramRaster)
/*      */   {
/* 1096 */     if ((paramSampleModel == null) || (paramDataBuffer == null) || (paramRectangle == null) || (paramPoint == null))
/*      */     {
/* 1098 */       throw new NullPointerException("SampleModel, dataBuffer, aRegion and sampleModelTranslate cannot be null");
/*      */     }
/*      */ 
/* 1101 */     this.sampleModel = paramSampleModel;
/* 1102 */     this.dataBuffer = paramDataBuffer;
/* 1103 */     this.minX = paramRectangle.x;
/* 1104 */     this.minY = paramRectangle.y;
/* 1105 */     this.width = paramRectangle.width;
/* 1106 */     this.height = paramRectangle.height;
/* 1107 */     if ((this.width <= 0) || (this.height <= 0)) {
/* 1108 */       throw new RasterFormatException("negative or zero " + (this.width <= 0 ? "width" : "height"));
/*      */     }
/*      */ 
/* 1111 */     if (this.minX + this.width < this.minX) {
/* 1112 */       throw new RasterFormatException("overflow condition for X coordinates of Raster");
/*      */     }
/*      */ 
/* 1115 */     if (this.minY + this.height < this.minY) {
/* 1116 */       throw new RasterFormatException("overflow condition for Y coordinates of Raster");
/*      */     }
/*      */ 
/* 1120 */     this.sampleModelTranslateX = paramPoint.x;
/* 1121 */     this.sampleModelTranslateY = paramPoint.y;
/*      */ 
/* 1123 */     this.numBands = paramSampleModel.getNumBands();
/* 1124 */     this.numDataElements = paramSampleModel.getNumDataElements();
/* 1125 */     this.parent = paramRaster;
/*      */   }
/*      */ 
/*      */   public Raster getParent()
/*      */   {
/* 1134 */     return this.parent;
/*      */   }
/*      */ 
/*      */   public final int getSampleModelTranslateX()
/*      */   {
/* 1146 */     return this.sampleModelTranslateX;
/*      */   }
/*      */ 
/*      */   public final int getSampleModelTranslateY()
/*      */   {
/* 1158 */     return this.sampleModelTranslateY;
/*      */   }
/*      */ 
/*      */   public WritableRaster createCompatibleWritableRaster()
/*      */   {
/* 1168 */     return new SunWritableRaster(this.sampleModel, new Point(0, 0));
/*      */   }
/*      */ 
/*      */   public WritableRaster createCompatibleWritableRaster(int paramInt1, int paramInt2)
/*      */   {
/* 1182 */     if ((paramInt1 <= 0) || (paramInt2 <= 0)) {
/* 1183 */       throw new RasterFormatException("negative " + (paramInt1 <= 0 ? "width" : "height"));
/*      */     }
/*      */ 
/* 1187 */     SampleModel localSampleModel = this.sampleModel.createCompatibleSampleModel(paramInt1, paramInt2);
/*      */ 
/* 1189 */     return new SunWritableRaster(localSampleModel, new Point(0, 0));
/*      */   }
/*      */ 
/*      */   public WritableRaster createCompatibleWritableRaster(Rectangle paramRectangle)
/*      */   {
/* 1208 */     if (paramRectangle == null) {
/* 1209 */       throw new NullPointerException("Rect cannot be null");
/*      */     }
/* 1211 */     return createCompatibleWritableRaster(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height);
/*      */   }
/*      */ 
/*      */   public WritableRaster createCompatibleWritableRaster(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/* 1235 */     WritableRaster localWritableRaster = createCompatibleWritableRaster(paramInt3, paramInt4);
/* 1236 */     return localWritableRaster.createWritableChild(0, 0, paramInt3, paramInt4, paramInt1, paramInt2, null);
/*      */   }
/*      */ 
/*      */   public Raster createTranslatedChild(int paramInt1, int paramInt2)
/*      */   {
/* 1258 */     return createChild(this.minX, this.minY, this.width, this.height, paramInt1, paramInt2, null);
/*      */   }
/*      */ 
/*      */   public Raster createChild(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int[] paramArrayOfInt)
/*      */   {
/* 1318 */     if (paramInt1 < this.minX) {
/* 1319 */       throw new RasterFormatException("parentX lies outside raster");
/*      */     }
/* 1321 */     if (paramInt2 < this.minY) {
/* 1322 */       throw new RasterFormatException("parentY lies outside raster");
/*      */     }
/* 1324 */     if ((paramInt1 + paramInt3 < paramInt1) || (paramInt1 + paramInt3 > this.width + this.minX))
/*      */     {
/* 1326 */       throw new RasterFormatException("(parentX + width) is outside raster");
/*      */     }
/* 1328 */     if ((paramInt2 + paramInt4 < paramInt2) || (paramInt2 + paramInt4 > this.height + this.minY))
/*      */     {
/* 1330 */       throw new RasterFormatException("(parentY + height) is outside raster");
/*      */     }
/*      */     SampleModel localSampleModel;
/* 1339 */     if (paramArrayOfInt == null)
/* 1340 */       localSampleModel = this.sampleModel;
/*      */     else {
/* 1342 */       localSampleModel = this.sampleModel.createSubsetSampleModel(paramArrayOfInt);
/*      */     }
/*      */ 
/* 1345 */     int i = paramInt5 - paramInt1;
/* 1346 */     int j = paramInt6 - paramInt2;
/*      */ 
/* 1348 */     return new Raster(localSampleModel, getDataBuffer(), new Rectangle(paramInt5, paramInt6, paramInt3, paramInt4), new Point(this.sampleModelTranslateX + i, this.sampleModelTranslateY + j), this);
/*      */   }
/*      */ 
/*      */   public Rectangle getBounds()
/*      */   {
/* 1360 */     return new Rectangle(this.minX, this.minY, this.width, this.height);
/*      */   }
/*      */ 
/*      */   public final int getMinX()
/*      */   {
/* 1367 */     return this.minX;
/*      */   }
/*      */ 
/*      */   public final int getMinY()
/*      */   {
/* 1374 */     return this.minY;
/*      */   }
/*      */ 
/*      */   public final int getWidth()
/*      */   {
/* 1381 */     return this.width;
/*      */   }
/*      */ 
/*      */   public final int getHeight()
/*      */   {
/* 1388 */     return this.height;
/*      */   }
/*      */ 
/*      */   public final int getNumBands()
/*      */   {
/* 1395 */     return this.numBands;
/*      */   }
/*      */ 
/*      */   public final int getNumDataElements()
/*      */   {
/* 1410 */     return this.sampleModel.getNumDataElements();
/*      */   }
/*      */ 
/*      */   public final int getTransferType()
/*      */   {
/* 1426 */     return this.sampleModel.getTransferType();
/*      */   }
/*      */ 
/*      */   public DataBuffer getDataBuffer()
/*      */   {
/* 1433 */     return this.dataBuffer;
/*      */   }
/*      */ 
/*      */   public SampleModel getSampleModel()
/*      */   {
/* 1440 */     return this.sampleModel;
/*      */   }
/*      */ 
/*      */   public Object getDataElements(int paramInt1, int paramInt2, Object paramObject)
/*      */   {
/* 1469 */     return this.sampleModel.getDataElements(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramObject, this.dataBuffer);
/*      */   }
/*      */ 
/*      */   public Object getDataElements(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object paramObject)
/*      */   {
/* 1503 */     return this.sampleModel.getDataElements(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramInt3, paramInt4, paramObject, this.dataBuffer);
/*      */   }
/*      */ 
/*      */   public int[] getPixel(int paramInt1, int paramInt2, int[] paramArrayOfInt)
/*      */   {
/* 1522 */     return this.sampleModel.getPixel(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramArrayOfInt, this.dataBuffer);
/*      */   }
/*      */ 
/*      */   public float[] getPixel(int paramInt1, int paramInt2, float[] paramArrayOfFloat)
/*      */   {
/* 1542 */     return this.sampleModel.getPixel(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramArrayOfFloat, this.dataBuffer);
/*      */   }
/*      */ 
/*      */   public double[] getPixel(int paramInt1, int paramInt2, double[] paramArrayOfDouble)
/*      */   {
/* 1561 */     return this.sampleModel.getPixel(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramArrayOfDouble, this.dataBuffer);
/*      */   }
/*      */ 
/*      */   public int[] getPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt)
/*      */   {
/* 1583 */     return this.sampleModel.getPixels(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramInt3, paramInt4, paramArrayOfInt, this.dataBuffer);
/*      */   }
/*      */ 
/*      */   public float[] getPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, float[] paramArrayOfFloat)
/*      */   {
/* 1606 */     return this.sampleModel.getPixels(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramInt3, paramInt4, paramArrayOfFloat, this.dataBuffer);
/*      */   }
/*      */ 
/*      */   public double[] getPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, double[] paramArrayOfDouble)
/*      */   {
/* 1629 */     return this.sampleModel.getPixels(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramInt3, paramInt4, paramArrayOfDouble, this.dataBuffer);
/*      */   }
/*      */ 
/*      */   public int getSample(int paramInt1, int paramInt2, int paramInt3)
/*      */   {
/* 1651 */     return this.sampleModel.getSample(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramInt3, this.dataBuffer);
/*      */   }
/*      */ 
/*      */   public float getSampleFloat(int paramInt1, int paramInt2, int paramInt3)
/*      */   {
/* 1672 */     return this.sampleModel.getSampleFloat(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramInt3, this.dataBuffer);
/*      */   }
/*      */ 
/*      */   public double getSampleDouble(int paramInt1, int paramInt2, int paramInt3)
/*      */   {
/* 1693 */     return this.sampleModel.getSampleDouble(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramInt3, this.dataBuffer);
/*      */   }
/*      */ 
/*      */   public int[] getSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int[] paramArrayOfInt)
/*      */   {
/* 1719 */     return this.sampleModel.getSamples(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramInt3, paramInt4, paramInt5, paramArrayOfInt, this.dataBuffer);
/*      */   }
/*      */ 
/*      */   public float[] getSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, float[] paramArrayOfFloat)
/*      */   {
/* 1746 */     return this.sampleModel.getSamples(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramInt3, paramInt4, paramInt5, paramArrayOfFloat, this.dataBuffer);
/*      */   }
/*      */ 
/*      */   public double[] getSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, double[] paramArrayOfDouble)
/*      */   {
/* 1772 */     return this.sampleModel.getSamples(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramInt3, paramInt4, paramInt5, paramArrayOfDouble, this.dataBuffer);
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  172 */     ColorModel.loadLibraries();
/*  173 */     initIDs();
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.image.Raster
 * JD-Core Version:    0.6.2
 */