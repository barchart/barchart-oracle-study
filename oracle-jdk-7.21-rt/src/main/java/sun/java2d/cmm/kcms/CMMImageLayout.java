/*     */ package sun.java2d.cmm.kcms;
/*     */ 
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.awt.image.ComponentSampleModel;
/*     */ import java.awt.image.DataBufferByte;
/*     */ import java.awt.image.DataBufferInt;
/*     */ import java.awt.image.DataBufferUShort;
/*     */ import java.awt.image.Raster;
/*     */ import java.awt.image.SinglePixelPackedSampleModel;
/*     */ import java.awt.image.WritableRaster;
/*     */ import sun.awt.image.ByteComponentRaster;
/*     */ import sun.awt.image.IntegerComponentRaster;
/*     */ import sun.awt.image.ShortComponentRaster;
/*     */ 
/*     */ class CMMImageLayout
/*     */ {
/*     */   private static final int typeBase = 256;
/*     */   public static final int typeComponentUByte = 256;
/*     */   public static final int typeComponentUShort12 = 257;
/*     */   public static final int typeComponentUShort = 258;
/*     */   public static final int typePixelUByte = 259;
/*     */   public static final int typePixelUShort12 = 260;
/*     */   public static final int typePixelUShort = 261;
/*     */   public static final int typeShort555 = 262;
/*     */   public static final int typeShort565 = 263;
/*     */   public static final int typeInt101010 = 264;
/*     */   public static final int typeIntRGBPacked = 265;
/*     */   public int Type;
/*     */   public int NumCols;
/*     */   public int NumRows;
/*     */   public int OffsetColumn;
/*     */   public int OffsetRow;
/*     */   public int NumChannels;
/*     */   public Object[] chanData;
/*     */   public int[] DataOffsets;
/*     */   public int[] sampleInfo;
/*     */   private int[] dataArrayLength;
/*     */ 
/*     */   public CMMImageLayout(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws CMMImageLayout.ImageLayoutException
/*     */   {
/* 110 */     this.Type = 256;
/*     */ 
/* 112 */     this.chanData = new Object[paramInt2];
/* 113 */     this.DataOffsets = new int[paramInt2];
/* 114 */     this.dataArrayLength = new int[paramInt2];
/*     */ 
/* 116 */     this.NumCols = paramInt1;
/* 117 */     this.NumRows = 1;
/* 118 */     this.OffsetColumn = paramInt2;
/* 119 */     this.OffsetRow = (this.NumCols * this.OffsetColumn);
/* 120 */     this.NumChannels = paramInt2;
/* 121 */     for (int i = 0; i < paramInt2; i++) {
/* 122 */       this.chanData[i] = paramArrayOfByte;
/* 123 */       this.DataOffsets[i] = i;
/* 124 */       this.dataArrayLength[i] = paramArrayOfByte.length;
/*     */     }
/* 126 */     verify();
/*     */   }
/*     */ 
/*     */   public CMMImageLayout(short[] paramArrayOfShort, int paramInt1, int paramInt2)
/*     */     throws CMMImageLayout.ImageLayoutException
/*     */   {
/* 137 */     this.Type = 258;
/*     */ 
/* 139 */     this.chanData = new Object[paramInt2];
/* 140 */     this.DataOffsets = new int[paramInt2];
/* 141 */     this.dataArrayLength = new int[paramInt2];
/*     */ 
/* 143 */     this.NumCols = paramInt1;
/* 144 */     this.NumRows = 1;
/*     */ 
/* 146 */     this.OffsetColumn = safeMult(2, paramInt2);
/* 147 */     this.OffsetRow = (this.NumCols * this.OffsetColumn);
/* 148 */     this.NumChannels = paramInt2;
/* 149 */     for (int i = 0; i < paramInt2; i++) {
/* 150 */       this.chanData[i] = paramArrayOfShort;
/* 151 */       this.DataOffsets[i] = (i * 2);
/* 152 */       this.dataArrayLength[i] = (2 * paramArrayOfShort.length);
/*     */     }
/* 154 */     verify();
/*     */   }
/*     */ 
/*     */   public CMMImageLayout(BufferedImage paramBufferedImage)
/*     */     throws CMMImageLayout.ImageLayoutException
/*     */   {
/* 169 */     this.Type = paramBufferedImage.getType();
/* 170 */     this.NumCols = paramBufferedImage.getWidth();
/* 171 */     this.NumRows = paramBufferedImage.getHeight();
/*     */ 
/* 173 */     WritableRaster localWritableRaster = paramBufferedImage.getRaster();
/*     */     int i;
/*     */     Object localObject1;
/*     */     int j;
/*     */     Object localObject2;
/*     */     int k;
/* 177 */     switch (this.Type)
/*     */     {
/*     */     case 1:
/*     */     case 2:
/*     */     case 4:
/* 182 */       this.NumChannels = 3;
/*     */ 
/* 184 */       i = 3;
/* 185 */       if (this.Type == 2) {
/* 186 */         i = 4;
/*     */       }
/* 188 */       this.chanData = new Object[i];
/* 189 */       this.DataOffsets = new int[i];
/* 190 */       this.dataArrayLength = new int[i];
/* 191 */       this.sampleInfo = new int[i];
/*     */ 
/* 195 */       this.OffsetColumn = 4;
/*     */ 
/* 197 */       if ((localWritableRaster instanceof IntegerComponentRaster)) {
/* 198 */         localObject1 = (IntegerComponentRaster)localWritableRaster;
/*     */ 
/* 200 */         this.OffsetRow = safeMult(4, ((IntegerComponentRaster)localObject1).getScanlineStride());
/*     */ 
/* 203 */         j = safeMult(4, ((IntegerComponentRaster)localObject1).getDataOffset(0));
/*     */ 
/* 205 */         localObject2 = ((IntegerComponentRaster)localObject1).getDataStorage();
/*     */ 
/* 207 */         for (k = 0; k < 3; k++) {
/* 208 */           this.chanData[k] = localObject2;
/* 209 */           this.DataOffsets[k] = j;
/* 210 */           this.dataArrayLength[k] = (4 * localObject2.length);
/* 211 */           if (this.Type == 4)
/* 212 */             this.sampleInfo[k] = (3 - k);
/*     */           else {
/* 214 */             this.sampleInfo[k] = (k + 1);
/*     */           }
/*     */         }
/* 217 */         if (this.Type == 2) {
/* 218 */           this.chanData[3] = localObject2;
/* 219 */           this.DataOffsets[3] = j;
/* 220 */           this.dataArrayLength[3] = (4 * localObject2.length);
/* 221 */           this.sampleInfo[3] = 0;
/*     */         }
/*     */       } else {
/* 224 */         throw new ImageLayoutException("Incompatible raster type");
/*     */       }
/*     */ 
/*     */       break;
/*     */     case 5:
/*     */     case 6:
/* 231 */       this.NumChannels = 3;
/*     */ 
/* 233 */       if (this.Type == 5) {
/* 234 */         this.OffsetColumn = 3;
/* 235 */         i = 3;
/*     */       } else {
/* 237 */         this.OffsetColumn = 4;
/* 238 */         i = 4;
/*     */       }
/* 240 */       this.chanData = new Object[i];
/* 241 */       this.DataOffsets = new int[i];
/* 242 */       this.dataArrayLength = new int[i];
/*     */ 
/* 244 */       if ((localWritableRaster instanceof ByteComponentRaster)) {
/* 245 */         localObject1 = (ByteComponentRaster)localWritableRaster;
/* 246 */         this.OffsetRow = ((ByteComponentRaster)localObject1).getScanlineStride();
/* 247 */         j = ((ByteComponentRaster)localObject1).getDataOffset(0);
/* 248 */         localObject2 = ((ByteComponentRaster)localObject1).getDataStorage();
/* 249 */         for (k = 0; k < i; k++) {
/* 250 */           this.chanData[k] = localObject2;
/* 251 */           this.DataOffsets[k] = (j - k);
/* 252 */           this.dataArrayLength[k] = localObject2.length;
/*     */         }
/*     */       } else {
/* 255 */         throw new ImageLayoutException("Incompatible raster type");
/*     */       }
/*     */ 
/*     */       break;
/*     */     case 10:
/* 262 */       this.Type = 256;
/*     */ 
/* 264 */       this.NumChannels = 1;
/*     */ 
/* 266 */       this.chanData = new Object[1];
/* 267 */       this.DataOffsets = new int[1];
/* 268 */       this.dataArrayLength = new int[1];
/*     */ 
/* 270 */       this.OffsetColumn = 1;
/*     */ 
/* 272 */       if ((localWritableRaster instanceof ByteComponentRaster)) {
/* 273 */         localObject1 = (ByteComponentRaster)localWritableRaster;
/* 274 */         this.OffsetRow = ((ByteComponentRaster)localObject1).getScanlineStride();
/* 275 */         localObject2 = ((ByteComponentRaster)localObject1).getDataStorage();
/* 276 */         this.chanData[0] = localObject2;
/* 277 */         this.dataArrayLength[0] = localObject2.length;
/* 278 */         this.DataOffsets[0] = ((ByteComponentRaster)localObject1).getDataOffset(0);
/*     */       } else {
/* 280 */         throw new ImageLayoutException("Incompatible raster type");
/*     */       }
/*     */ 
/*     */       break;
/*     */     case 11:
/* 286 */       this.Type = 258;
/*     */ 
/* 288 */       this.NumChannels = 1;
/*     */ 
/* 290 */       this.chanData = new Object[1];
/* 291 */       this.DataOffsets = new int[1];
/* 292 */       this.dataArrayLength = new int[1];
/*     */ 
/* 294 */       this.OffsetColumn = 2;
/*     */ 
/* 296 */       if ((localWritableRaster instanceof ShortComponentRaster)) {
/* 297 */         localObject1 = (ShortComponentRaster)localWritableRaster;
/*     */ 
/* 299 */         this.OffsetRow = safeMult(2, ((ShortComponentRaster)localObject1).getScanlineStride());
/*     */ 
/* 301 */         this.DataOffsets[0] = safeMult(2, ((ShortComponentRaster)localObject1).getDataOffset(0));
/*     */ 
/* 303 */         localObject2 = ((ShortComponentRaster)localObject1).getDataStorage();
/* 304 */         this.chanData[0] = localObject2;
/* 305 */         this.dataArrayLength[0] = (2 * localObject2.length);
/*     */       } else {
/* 307 */         throw new ImageLayoutException("Incompatible raster type"); } break;
/*     */     case 3:
/*     */     case 7:
/*     */     case 8:
/*     */     case 9:
/*     */     default:
/* 313 */       throw new IllegalArgumentException("CMMImageLayout - bad image type passed to constructor");
/*     */     }
/*     */ 
/* 316 */     verify();
/*     */   }
/*     */ 
/*     */   public CMMImageLayout(BufferedImage paramBufferedImage, SinglePixelPackedSampleModel paramSinglePixelPackedSampleModel, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */     throws CMMImageLayout.ImageLayoutException
/*     */   {
/* 329 */     this.Type = 265;
/*     */ 
/* 331 */     this.NumChannels = 3;
/* 332 */     this.NumCols = paramBufferedImage.getWidth();
/* 333 */     this.NumRows = paramBufferedImage.getHeight();
/*     */ 
/* 335 */     int i = 3;
/* 336 */     if (paramInt4 >= 0) {
/* 337 */       i = 4;
/*     */     }
/* 339 */     this.chanData = new Object[i];
/* 340 */     this.DataOffsets = new int[i];
/* 341 */     this.dataArrayLength = new int[i];
/* 342 */     this.sampleInfo = new int[i];
/*     */ 
/* 346 */     this.OffsetColumn = 4;
/*     */ 
/* 348 */     int j = paramSinglePixelPackedSampleModel.getScanlineStride();
/*     */ 
/* 350 */     this.OffsetRow = safeMult(4, j);
/*     */ 
/* 352 */     WritableRaster localWritableRaster = paramBufferedImage.getRaster();
/* 353 */     DataBufferInt localDataBufferInt = (DataBufferInt)localWritableRaster.getDataBuffer();
/*     */ 
/* 356 */     int k = localWritableRaster.getSampleModelTranslateX();
/* 357 */     int m = localWritableRaster.getSampleModelTranslateY();
/*     */ 
/* 359 */     int n = safeMult(m, j);
/*     */ 
/* 361 */     int i1 = safeMult(4, k);
/*     */ 
/* 363 */     i1 = safeAdd(i1, n);
/*     */ 
/* 365 */     int i2 = safeAdd(localDataBufferInt.getOffset(), -i1);
/*     */ 
/* 367 */     int[] arrayOfInt = localDataBufferInt.getData();
/*     */ 
/* 369 */     for (int i3 = 0; i3 < i; i3++) {
/* 370 */       this.chanData[i3] = arrayOfInt;
/* 371 */       this.DataOffsets[i3] = i2;
/* 372 */       this.dataArrayLength[i3] = (arrayOfInt.length * 4);
/*     */     }
/* 374 */     this.sampleInfo[0] = paramInt1;
/* 375 */     this.sampleInfo[1] = paramInt2;
/* 376 */     this.sampleInfo[2] = paramInt3;
/* 377 */     if (paramInt4 >= 0) {
/* 378 */       this.sampleInfo[3] = paramInt4;
/*     */     }
/* 380 */     verify();
/*     */   }
/*     */ 
/*     */   public CMMImageLayout(BufferedImage paramBufferedImage, ComponentSampleModel paramComponentSampleModel)
/*     */     throws CMMImageLayout.ImageLayoutException
/*     */   {
/* 392 */     ColorModel localColorModel = paramBufferedImage.getColorModel();
/* 393 */     int i = localColorModel.getNumColorComponents();
/* 394 */     boolean bool = localColorModel.hasAlpha();
/* 395 */     WritableRaster localWritableRaster = paramBufferedImage.getRaster();
/* 396 */     int[] arrayOfInt1 = paramComponentSampleModel.getBankIndices();
/* 397 */     int[] arrayOfInt2 = paramComponentSampleModel.getBandOffsets();
/* 398 */     this.NumChannels = i;
/* 399 */     this.NumCols = paramBufferedImage.getWidth();
/* 400 */     this.NumRows = paramBufferedImage.getHeight();
/*     */ 
/* 402 */     if (bool) {
/* 403 */       i++;
/*     */     }
/* 405 */     this.chanData = new Object[i];
/* 406 */     this.DataOffsets = new int[i];
/* 407 */     this.dataArrayLength = new int[i];
/*     */ 
/* 410 */     int j = localWritableRaster.getSampleModelTranslateY();
/* 411 */     int k = localWritableRaster.getSampleModelTranslateX();
/* 412 */     int m = paramComponentSampleModel.getScanlineStride();
/* 413 */     int n = paramComponentSampleModel.getPixelStride();
/*     */ 
/* 415 */     int i1 = safeMult(m, j);
/*     */ 
/* 417 */     int i2 = safeMult(n, k);
/*     */ 
/* 419 */     i2 = safeAdd(i2, i1);
/*     */     Object localObject1;
/*     */     int[] arrayOfInt3;
/*     */     int i3;
/*     */     Object localObject2;
/*     */     int i4;
/* 421 */     switch (paramComponentSampleModel.getDataType())
/*     */     {
/*     */     case 0:
/* 424 */       this.Type = 256;
/* 425 */       this.OffsetColumn = n;
/* 426 */       this.OffsetRow = m;
/* 427 */       localObject1 = (DataBufferByte)localWritableRaster.getDataBuffer();
/* 428 */       arrayOfInt3 = ((DataBufferByte)localObject1).getOffsets();
/*     */ 
/* 430 */       for (i3 = 0; i3 < i; i3++) {
/* 431 */         localObject2 = ((DataBufferByte)localObject1).getData(arrayOfInt1[i3]);
/* 432 */         this.chanData[i3] = localObject2;
/* 433 */         this.dataArrayLength[i3] = localObject2.length;
/*     */ 
/* 435 */         i4 = safeAdd(arrayOfInt3[arrayOfInt1[i3]], -i2);
/*     */ 
/* 437 */         i4 = safeAdd(i4, arrayOfInt2[i3]);
/*     */ 
/* 439 */         this.DataOffsets[i3] = i4;
/*     */       }
/*     */ 
/* 442 */       break;
/*     */     case 1:
/* 446 */       this.Type = 258;
/*     */ 
/* 448 */       this.OffsetColumn = safeMult(2, n);
/*     */ 
/* 450 */       this.OffsetRow = safeMult(2, m);
/*     */ 
/* 452 */       localObject1 = (DataBufferUShort)localWritableRaster.getDataBuffer();
/*     */ 
/* 454 */       arrayOfInt3 = ((DataBufferUShort)localObject1).getOffsets();
/*     */ 
/* 456 */       for (i3 = 0; i3 < i; i3++) {
/* 457 */         localObject2 = ((DataBufferUShort)localObject1).getData(arrayOfInt1[i3]);
/* 458 */         this.chanData[i3] = localObject2;
/* 459 */         this.dataArrayLength[i3] = (localObject2.length * 2);
/*     */ 
/* 461 */         i4 = safeAdd(arrayOfInt3[arrayOfInt1[i3]], -i2);
/*     */ 
/* 463 */         i4 = safeAdd(i4, arrayOfInt2[i3]);
/*     */ 
/* 465 */         this.DataOffsets[i3] = safeMult(2, i4);
/*     */       }
/*     */ 
/* 468 */       break;
/*     */     default:
/* 472 */       throw new IllegalArgumentException("CMMImageLayout - bad image type passed to constructor");
/*     */     }
/*     */ 
/* 475 */     verify();
/*     */   }
/*     */ 
/*     */   public CMMImageLayout(Raster paramRaster, ComponentSampleModel paramComponentSampleModel)
/*     */     throws CMMImageLayout.ImageLayoutException
/*     */   {
/* 486 */     int i = paramRaster.getNumBands();
/* 487 */     int[] arrayOfInt1 = paramComponentSampleModel.getBankIndices();
/* 488 */     int[] arrayOfInt2 = paramComponentSampleModel.getBandOffsets();
/* 489 */     this.NumChannels = i;
/* 490 */     this.NumCols = paramRaster.getWidth();
/* 491 */     this.NumRows = paramRaster.getHeight();
/*     */ 
/* 493 */     this.chanData = new Object[i];
/* 494 */     this.DataOffsets = new int[i];
/* 495 */     this.dataArrayLength = new int[i];
/*     */ 
/* 497 */     int j = paramComponentSampleModel.getScanlineStride();
/* 498 */     int k = paramComponentSampleModel.getPixelStride();
/*     */ 
/* 501 */     int m = paramRaster.getMinX();
/* 502 */     int n = paramRaster.getMinY();
/*     */ 
/* 504 */     int i1 = paramRaster.getSampleModelTranslateX();
/* 505 */     int i2 = paramRaster.getSampleModelTranslateY();
/*     */ 
/* 507 */     int i3 = safeAdd(n, -i2);
/* 508 */     i3 = safeMult(i3, j);
/*     */ 
/* 510 */     int i4 = safeAdd(m, -i1);
/* 511 */     i4 = safeMult(i4, k);
/*     */ 
/* 513 */     i4 = safeAdd(i4, i3);
/*     */     Object localObject1;
/*     */     int[] arrayOfInt3;
/*     */     int i5;
/*     */     Object localObject2;
/*     */     int i6;
/* 515 */     switch (paramComponentSampleModel.getDataType())
/*     */     {
/*     */     case 0:
/* 518 */       this.Type = 256;
/* 519 */       this.OffsetColumn = k;
/* 520 */       this.OffsetRow = j;
/*     */ 
/* 522 */       localObject1 = (DataBufferByte)paramRaster.getDataBuffer();
/* 523 */       arrayOfInt3 = ((DataBufferByte)localObject1).getOffsets();
/* 524 */       for (i5 = 0; i5 < i; i5++) {
/* 525 */         localObject2 = ((DataBufferByte)localObject1).getData(arrayOfInt1[i5]);
/* 526 */         this.chanData[i5] = localObject2;
/* 527 */         this.dataArrayLength[i5] = localObject2.length;
/*     */ 
/* 529 */         i6 = safeAdd(arrayOfInt3[arrayOfInt1[i5]], i4);
/*     */ 
/* 531 */         this.DataOffsets[i5] = safeAdd(i6, arrayOfInt2[i5]);
/*     */       }
/*     */ 
/* 535 */       break;
/*     */     case 1:
/* 539 */       this.Type = 258;
/* 540 */       this.OffsetColumn = safeMult(2, k);
/*     */ 
/* 542 */       this.OffsetRow = safeMult(2, j);
/*     */ 
/* 544 */       localObject1 = (DataBufferUShort)paramRaster.getDataBuffer();
/*     */ 
/* 546 */       arrayOfInt3 = ((DataBufferUShort)localObject1).getOffsets();
/* 547 */       for (i5 = 0; i5 < i; i5++) {
/* 548 */         localObject2 = ((DataBufferUShort)localObject1).getData(arrayOfInt1[i5]);
/* 549 */         this.chanData[i5] = localObject2;
/* 550 */         this.dataArrayLength[i5] = (localObject2.length * 2);
/*     */ 
/* 553 */         i6 = safeAdd(arrayOfInt3[arrayOfInt1[i5]], i4);
/*     */ 
/* 555 */         i6 = safeAdd(i6, arrayOfInt2[i5]);
/*     */ 
/* 557 */         this.DataOffsets[i5] = safeMult(2, i6);
/*     */       }
/*     */ 
/* 561 */       break;
/*     */     default:
/* 565 */       throw new IllegalArgumentException("CMMImageLayout - bad image type passed to constructor");
/*     */     }
/*     */ 
/* 568 */     verify();
/*     */   }
/*     */ 
/*     */   private final void verify() throws CMMImageLayout.ImageLayoutException {
/* 572 */     int i = safeMult(this.OffsetRow, this.NumRows - 1);
/*     */ 
/* 574 */     i = safeAdd(i, this.NumCols - 1);
/*     */ 
/* 576 */     for (int j = 0; j < this.NumChannels; j++) {
/* 577 */       int k = this.DataOffsets[j];
/*     */ 
/* 579 */       if ((k < 0) || (k >= this.dataArrayLength[j])) {
/* 580 */         throw new ImageLayoutException("Invalid image layout");
/*     */       }
/* 582 */       k = safeAdd(k, i);
/*     */ 
/* 584 */       if ((k < 0) || (k >= this.dataArrayLength[j]))
/* 585 */         throw new ImageLayoutException("Invalid image layout");
/*     */     }
/*     */   }
/*     */ 
/*     */   static int safeAdd(int paramInt1, int paramInt2) throws CMMImageLayout.ImageLayoutException
/*     */   {
/* 591 */     long l = paramInt1;
/* 592 */     l += paramInt2;
/* 593 */     if ((l < -2147483648L) || (l > 2147483647L)) {
/* 594 */       throw new ImageLayoutException("Invalid image layout");
/*     */     }
/* 596 */     return (int)l;
/*     */   }
/*     */ 
/*     */   static int safeMult(int paramInt1, int paramInt2) throws CMMImageLayout.ImageLayoutException {
/* 600 */     long l = paramInt1;
/* 601 */     l *= paramInt2;
/* 602 */     if ((l < -2147483648L) || (l > 2147483647L)) {
/* 603 */       throw new ImageLayoutException("Invalid image layout");
/*     */     }
/* 605 */     return (int)l;
/*     */   }
/*     */ 
/*     */   public static class ImageLayoutException extends Exception
/*     */   {
/*     */     public ImageLayoutException(String paramString) {
/* 611 */       super();
/*     */     }
/*     */ 
/*     */     public ImageLayoutException(String paramString, Throwable paramThrowable) {
/* 615 */       super(paramThrowable);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.cmm.kcms.CMMImageLayout
 * JD-Core Version:    0.6.2
 */