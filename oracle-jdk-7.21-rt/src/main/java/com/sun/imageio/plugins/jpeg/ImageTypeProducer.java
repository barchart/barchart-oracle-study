/*      */ package com.sun.imageio.plugins.jpeg;
/*      */ 
/*      */ import javax.imageio.ImageTypeSpecifier;
/*      */ 
/*      */ class ImageTypeProducer
/*      */ {
/* 1739 */   private ImageTypeSpecifier type = null;
/* 1740 */   boolean failed = false;
/*      */   private int csCode;
/* 1762 */   private static final ImageTypeProducer[] defaultTypes = new ImageTypeProducer[12];
/*      */ 
/*      */   public ImageTypeProducer(int paramInt)
/*      */   {
/* 1744 */     this.csCode = paramInt;
/*      */   }
/*      */ 
/*      */   public ImageTypeProducer() {
/* 1748 */     this.csCode = -1;
/*      */   }
/*      */ 
/*      */   public synchronized ImageTypeSpecifier getType() {
/* 1752 */     if ((!this.failed) && (this.type == null)) {
/*      */       try {
/* 1754 */         this.type = produce();
/*      */       } catch (Throwable localThrowable) {
/* 1756 */         this.failed = true;
/*      */       }
/*      */     }
/* 1759 */     return this.type;
/*      */   }
/*      */ 
/*      */   public static synchronized ImageTypeProducer getTypeProducer(int paramInt)
/*      */   {
/* 1766 */     if ((paramInt < 0) || (paramInt >= 12)) {
/* 1767 */       return null;
/*      */     }
/* 1769 */     if (defaultTypes[paramInt] == null) {
/* 1770 */       defaultTypes[paramInt] = new ImageTypeProducer(paramInt);
/*      */     }
/* 1772 */     return defaultTypes[paramInt];
/*      */   }
/*      */ 
/*      */   protected ImageTypeSpecifier produce() {
/* 1776 */     switch (this.csCode) {
/*      */     case 1:
/* 1778 */       return ImageTypeSpecifier.createFromBufferedImageType(10);
/*      */     case 2:
/* 1781 */       return ImageTypeSpecifier.createInterleaved(JPEG.JCS.sRGB, JPEG.bOffsRGB, 0, false, false);
/*      */     case 6:
/* 1787 */       return ImageTypeSpecifier.createPacked(JPEG.JCS.sRGB, -16777216, 16711680, 65280, 255, 3, false);
/*      */     case 5:
/* 1795 */       if (JPEG.JCS.getYCC() != null) {
/* 1796 */         return ImageTypeSpecifier.createInterleaved(JPEG.JCS.getYCC(), JPEG.bandOffsets[2], 0, false, false);
/*      */       }
/*      */ 
/* 1803 */       return null;
/*      */     case 10:
/* 1806 */       if (JPEG.JCS.getYCC() != null) {
/* 1807 */         return ImageTypeSpecifier.createInterleaved(JPEG.JCS.getYCC(), JPEG.bandOffsets[3], 0, true, false);
/*      */       }
/*      */ 
/* 1814 */       return null;
/*      */     case 3:
/*      */     case 4:
/*      */     case 7:
/*      */     case 8:
/* 1817 */     case 9: } return null;
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.imageio.plugins.jpeg.ImageTypeProducer
 * JD-Core Version:    0.6.2
 */