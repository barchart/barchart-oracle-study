/*     */ package sun.java2d.cmm.lcms;
/*     */ 
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.SampleModel;
/*     */ import sun.awt.image.ByteComponentRaster;
/*     */ import sun.awt.image.IntegerComponentRaster;
/*     */ import sun.awt.image.ShortComponentRaster;
/*     */ 
/*     */ class LCMSImageLayout
/*     */ {
/*     */   public static final int SWAPFIRST = 16384;
/*     */   public static final int DOSWAP = 1024;
/*  63 */   public static final int PT_RGB_8 = CHANNELS_SH(3) | BYTES_SH(1);
/*     */ 
/*  66 */   public static final int PT_GRAY_8 = CHANNELS_SH(1) | BYTES_SH(1);
/*     */ 
/*  69 */   public static final int PT_GRAY_16 = CHANNELS_SH(1) | BYTES_SH(2);
/*     */ 
/*  72 */   public static final int PT_RGBA_8 = EXTRA_SH(1) | CHANNELS_SH(3) | BYTES_SH(1);
/*     */ 
/*  75 */   public static final int PT_ARGB_8 = EXTRA_SH(1) | CHANNELS_SH(3) | BYTES_SH(1) | 0x4000;
/*     */ 
/*  78 */   public static final int PT_BGR_8 = 0x400 | CHANNELS_SH(3) | BYTES_SH(1);
/*     */ 
/*  81 */   public static final int PT_ABGR_8 = 0x400 | EXTRA_SH(1) | CHANNELS_SH(3) | BYTES_SH(1);
/*     */ 
/*  84 */   public static final int PT_BGRA_8 = EXTRA_SH(1) | CHANNELS_SH(3) | BYTES_SH(1) | 0x400 | 0x4000;
/*     */   public static final int DT_BYTE = 0;
/*     */   public static final int DT_SHORT = 1;
/*     */   public static final int DT_INT = 2;
/*     */   public static final int DT_DOUBLE = 3;
/*  93 */   boolean isIntPacked = false;
/*     */   int pixelType;
/*     */   int dataType;
/*     */   int width;
/*     */   int height;
/*     */   int nextRowOffset;
/*     */   int offset;
/*     */   Object dataArray;
/*     */   private int dataArrayLength;
/*     */ 
/*     */   public static int BYTES_SH(int paramInt)
/*     */   {
/*  48 */     return paramInt;
/*     */   }
/*     */ 
/*     */   public static int EXTRA_SH(int paramInt) {
/*  52 */     return paramInt << 7;
/*     */   }
/*     */ 
/*     */   public static int CHANNELS_SH(int paramInt) {
/*  56 */     return paramInt << 3;
/*     */   }
/*     */ 
/*     */   private LCMSImageLayout(int paramInt1, int paramInt2, int paramInt3)
/*     */     throws LCMSImageLayout.ImageLayoutException
/*     */   {
/* 107 */     this.pixelType = paramInt2;
/* 108 */     this.width = paramInt1;
/* 109 */     this.height = 1;
/* 110 */     this.nextRowOffset = safeMult(paramInt3, paramInt1);
/* 111 */     this.offset = 0;
/*     */   }
/*     */ 
/*     */   private LCMSImageLayout(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */     throws LCMSImageLayout.ImageLayoutException
/*     */   {
/* 118 */     this.pixelType = paramInt3;
/* 119 */     this.width = paramInt1;
/* 120 */     this.height = paramInt2;
/* 121 */     this.nextRowOffset = safeMult(paramInt4, paramInt1);
/* 122 */     this.offset = 0;
/*     */   }
/*     */ 
/*     */   public LCMSImageLayout(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
/*     */     throws LCMSImageLayout.ImageLayoutException
/*     */   {
/* 129 */     this(paramInt1, paramInt2, paramInt3);
/* 130 */     this.dataType = 0;
/* 131 */     this.dataArray = paramArrayOfByte;
/* 132 */     this.dataArrayLength = paramArrayOfByte.length;
/*     */ 
/* 134 */     verify();
/*     */   }
/*     */ 
/*     */   public LCMSImageLayout(short[] paramArrayOfShort, int paramInt1, int paramInt2, int paramInt3)
/*     */     throws LCMSImageLayout.ImageLayoutException
/*     */   {
/* 140 */     this(paramInt1, paramInt2, paramInt3);
/* 141 */     this.dataType = 1;
/* 142 */     this.dataArray = paramArrayOfShort;
/* 143 */     this.dataArrayLength = (2 * paramArrayOfShort.length);
/*     */ 
/* 145 */     verify();
/*     */   }
/*     */ 
/*     */   public LCMSImageLayout(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3)
/*     */     throws LCMSImageLayout.ImageLayoutException
/*     */   {
/* 151 */     this(paramInt1, paramInt2, paramInt3);
/* 152 */     this.dataType = 2;
/* 153 */     this.dataArray = paramArrayOfInt;
/* 154 */     this.dataArrayLength = (4 * paramArrayOfInt.length);
/*     */ 
/* 156 */     verify();
/*     */   }
/*     */ 
/*     */   public LCMSImageLayout(double[] paramArrayOfDouble, int paramInt1, int paramInt2, int paramInt3)
/*     */     throws LCMSImageLayout.ImageLayoutException
/*     */   {
/* 162 */     this(paramInt1, paramInt2, paramInt3);
/* 163 */     this.dataType = 3;
/* 164 */     this.dataArray = paramArrayOfDouble;
/* 165 */     this.dataArrayLength = (8 * paramArrayOfDouble.length);
/*     */ 
/* 167 */     verify();
/*     */   }
/*     */ 
/*     */   public LCMSImageLayout(BufferedImage paramBufferedImage)
/*     */     throws LCMSImageLayout.ImageLayoutException
/*     */   {
/* 174 */     switch (paramBufferedImage.getType()) {
/*     */     case 1:
/* 176 */       this.pixelType = PT_ARGB_8;
/* 177 */       this.isIntPacked = true;
/* 178 */       break;
/*     */     case 2:
/* 180 */       this.pixelType = PT_ARGB_8;
/* 181 */       this.isIntPacked = true;
/* 182 */       break;
/*     */     case 4:
/* 184 */       this.pixelType = PT_ABGR_8;
/* 185 */       this.isIntPacked = true;
/* 186 */       break;
/*     */     case 5:
/* 188 */       this.pixelType = PT_BGR_8;
/* 189 */       break;
/*     */     case 6:
/* 191 */       this.pixelType = PT_ABGR_8;
/* 192 */       break;
/*     */     case 10:
/* 194 */       this.pixelType = PT_GRAY_8;
/* 195 */       break;
/*     */     case 11:
/* 197 */       this.pixelType = PT_GRAY_16;
/* 198 */       break;
/*     */     case 3:
/*     */     case 7:
/*     */     case 8:
/*     */     case 9:
/*     */     default:
/* 202 */       throw new IllegalArgumentException("CMMImageLayout - bad image type passed to constructor");
/*     */     }
/*     */ 
/* 206 */     this.width = paramBufferedImage.getWidth();
/* 207 */     this.height = paramBufferedImage.getHeight();
/*     */     ByteComponentRaster localByteComponentRaster;
/* 209 */     switch (paramBufferedImage.getType()) {
/*     */     case 1:
/*     */     case 2:
/*     */     case 4:
/* 213 */       IntegerComponentRaster localIntegerComponentRaster = (IntegerComponentRaster)paramBufferedImage.getRaster();
/*     */ 
/* 215 */       this.nextRowOffset = safeMult(4, localIntegerComponentRaster.getScanlineStride());
/*     */ 
/* 217 */       this.offset = safeMult(4, localIntegerComponentRaster.getDataOffset(0));
/*     */ 
/* 219 */       this.dataArray = localIntegerComponentRaster.getDataStorage();
/* 220 */       this.dataArrayLength = (4 * localIntegerComponentRaster.getDataStorage().length);
/* 221 */       this.dataType = 2;
/* 222 */       break;
/*     */     case 5:
/*     */     case 6:
/* 226 */       localByteComponentRaster = (ByteComponentRaster)paramBufferedImage.getRaster();
/* 227 */       this.nextRowOffset = localByteComponentRaster.getScanlineStride();
/* 228 */       int i = paramBufferedImage.getSampleModel().getNumBands() - 1;
/* 229 */       this.offset = localByteComponentRaster.getDataOffset(i);
/* 230 */       this.dataArray = localByteComponentRaster.getDataStorage();
/* 231 */       this.dataArrayLength = localByteComponentRaster.getDataStorage().length;
/* 232 */       this.dataType = 0;
/* 233 */       break;
/*     */     case 10:
/* 236 */       localByteComponentRaster = (ByteComponentRaster)paramBufferedImage.getRaster();
/* 237 */       this.nextRowOffset = localByteComponentRaster.getScanlineStride();
/* 238 */       this.offset = localByteComponentRaster.getDataOffset(0);
/* 239 */       this.dataArray = localByteComponentRaster.getDataStorage();
/* 240 */       this.dataArrayLength = localByteComponentRaster.getDataStorage().length;
/* 241 */       this.dataType = 0;
/* 242 */       break;
/*     */     case 11:
/* 245 */       ShortComponentRaster localShortComponentRaster = (ShortComponentRaster)paramBufferedImage.getRaster();
/* 246 */       this.nextRowOffset = safeMult(2, localShortComponentRaster.getScanlineStride());
/* 247 */       this.offset = safeMult(2, localShortComponentRaster.getDataOffset(0));
/* 248 */       this.dataArray = localShortComponentRaster.getDataStorage();
/* 249 */       this.dataArrayLength = (2 * localShortComponentRaster.getDataStorage().length);
/* 250 */       this.dataType = 1;
/*     */     case 3:
/*     */     case 7:
/*     */     case 8:
/* 253 */     case 9: } verify();
/*     */   }
/*     */ 
/*     */   public static boolean isSupported(BufferedImage paramBufferedImage) {
/* 257 */     switch (paramBufferedImage.getType()) {
/*     */     case 1:
/*     */     case 2:
/*     */     case 4:
/*     */     case 5:
/*     */     case 6:
/*     */     case 10:
/*     */     case 11:
/* 265 */       return true;
/*     */     case 3:
/*     */     case 7:
/*     */     case 8:
/* 267 */     case 9: } return false;
/*     */   }
/*     */ 
/*     */   private void verify() throws LCMSImageLayout.ImageLayoutException
/*     */   {
/* 272 */     if ((this.offset < 0) || (this.offset >= this.dataArrayLength)) {
/* 273 */       throw new ImageLayoutException("Invalid image layout");
/*     */     }
/*     */ 
/* 276 */     int i = safeMult(this.nextRowOffset, this.height - 1);
/*     */ 
/* 278 */     i = safeAdd(i, this.width - 1);
/*     */ 
/* 280 */     int j = safeAdd(this.offset, i);
/*     */ 
/* 282 */     if ((j < 0) || (j >= this.dataArrayLength))
/* 283 */       throw new ImageLayoutException("Invalid image layout");
/*     */   }
/*     */ 
/*     */   static int safeAdd(int paramInt1, int paramInt2) throws LCMSImageLayout.ImageLayoutException
/*     */   {
/* 288 */     long l = paramInt1;
/* 289 */     l += paramInt2;
/* 290 */     if ((l < -2147483648L) || (l > 2147483647L)) {
/* 291 */       throw new ImageLayoutException("Invalid image layout");
/*     */     }
/* 293 */     return (int)l;
/*     */   }
/*     */ 
/*     */   static int safeMult(int paramInt1, int paramInt2) throws LCMSImageLayout.ImageLayoutException {
/* 297 */     long l = paramInt1;
/* 298 */     l *= paramInt2;
/* 299 */     if ((l < -2147483648L) || (l > 2147483647L)) {
/* 300 */       throw new ImageLayoutException("Invalid image layout");
/*     */     }
/* 302 */     return (int)l;
/*     */   }
/*     */ 
/*     */   public static class ImageLayoutException extends Exception {
/*     */     public ImageLayoutException(String paramString) {
/* 307 */       super();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.cmm.lcms.LCMSImageLayout
 * JD-Core Version:    0.6.2
 */