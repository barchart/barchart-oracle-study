/*      */ package com.sun.imageio.plugins.jpeg;
/*      */ 
/*      */ import java.awt.Point;
/*      */ import java.awt.color.ColorSpace;
/*      */ import java.awt.color.ICC_ColorSpace;
/*      */ import java.awt.color.ICC_Profile;
/*      */ import java.awt.image.ColorModel;
/*      */ import java.io.IOException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.ListIterator;
/*      */ import javax.imageio.IIOException;
/*      */ import javax.imageio.ImageTypeSpecifier;
/*      */ import javax.imageio.ImageWriteParam;
/*      */ import javax.imageio.metadata.IIOInvalidTreeException;
/*      */ import javax.imageio.metadata.IIOMetadata;
/*      */ import javax.imageio.metadata.IIOMetadataNode;
/*      */ import javax.imageio.plugins.jpeg.JPEGHuffmanTable;
/*      */ import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
/*      */ import javax.imageio.stream.ImageInputStream;
/*      */ import javax.imageio.stream.ImageOutputStream;
/*      */ import org.w3c.dom.NamedNodeMap;
/*      */ import org.w3c.dom.Node;
/*      */ import org.w3c.dom.NodeList;
/*      */ 
/*      */ public class JPEGMetadata extends IIOMetadata
/*      */   implements Cloneable
/*      */ {
/*      */   private static final boolean debug = false;
/*   72 */   private List resetSequence = null;
/*      */ 
/*   80 */   private boolean inThumb = false;
/*      */   private boolean hasAlpha;
/*  103 */   List markerSequence = new ArrayList();
/*      */   final boolean isStream;
/*      */   private boolean transparencyDone;
/*      */ 
/*      */   JPEGMetadata(boolean paramBoolean1, boolean paramBoolean2)
/*      */   {
/*  119 */     super(true, "javax_imageio_jpeg_image_1.0", "com.sun.imageio.plugins.jpeg.JPEGImageMetadataFormat", null, null);
/*      */ 
/*  123 */     this.inThumb = paramBoolean2;
/*      */ 
/*  125 */     this.isStream = paramBoolean1;
/*  126 */     if (paramBoolean1) {
/*  127 */       this.nativeMetadataFormatName = "javax_imageio_jpeg_stream_1.0";
/*  128 */       this.nativeMetadataFormatClassName = "com.sun.imageio.plugins.jpeg.JPEGStreamMetadataFormat";
/*      */     }
/*      */   }
/*      */ 
/*      */   JPEGMetadata(boolean paramBoolean1, boolean paramBoolean2, ImageInputStream paramImageInputStream, JPEGImageReader paramJPEGImageReader)
/*      */     throws IOException
/*      */   {
/*  151 */     this(paramBoolean1, paramBoolean2);
/*      */ 
/*  153 */     JPEGBuffer localJPEGBuffer = new JPEGBuffer(paramImageInputStream);
/*      */ 
/*  155 */     localJPEGBuffer.loadBuf(0);
/*      */ 
/*  158 */     if (((localJPEGBuffer.buf[0] & 0xFF) != 255) || ((localJPEGBuffer.buf[1] & 0xFF) != 216) || ((localJPEGBuffer.buf[2] & 0xFF) != 255))
/*      */     {
/*  161 */       throw new IIOException("Image format error");
/*      */     }
/*      */ 
/*  164 */     int i = 0;
/*  165 */     localJPEGBuffer.bufAvail -= 2;
/*  166 */     localJPEGBuffer.bufPtr = 2;
/*  167 */     Object localObject = null;
/*  168 */     while (i == 0)
/*      */     {
/*  171 */       localJPEGBuffer.loadBuf(1);
/*      */ 
/*  176 */       localJPEGBuffer.scanForFF(paramJPEGImageReader);
/*      */       JFIFMarkerSegment localJFIFMarkerSegment;
/*  177 */       switch (localJPEGBuffer.buf[localJPEGBuffer.bufPtr] & 0xFF)
/*      */       {
/*      */       case 0:
/*  182 */         localJPEGBuffer.bufAvail -= 1;
/*  183 */         localJPEGBuffer.bufPtr += 1;
/*  184 */         break;
/*      */       case 192:
/*      */       case 193:
/*      */       case 194:
/*  188 */         if (paramBoolean1) {
/*  189 */           throw new IIOException("SOF not permitted in stream metadata");
/*      */         }
/*      */ 
/*  192 */         localObject = new SOFMarkerSegment(localJPEGBuffer);
/*  193 */         break;
/*      */       case 219:
/*  195 */         localObject = new DQTMarkerSegment(localJPEGBuffer);
/*  196 */         break;
/*      */       case 196:
/*  198 */         localObject = new DHTMarkerSegment(localJPEGBuffer);
/*  199 */         break;
/*      */       case 221:
/*  201 */         localObject = new DRIMarkerSegment(localJPEGBuffer);
/*  202 */         break;
/*      */       case 224:
/*  205 */         localJPEGBuffer.loadBuf(8);
/*  206 */         byte[] arrayOfByte = localJPEGBuffer.buf;
/*  207 */         int j = localJPEGBuffer.bufPtr;
/*  208 */         if ((arrayOfByte[(j + 3)] == 74) && (arrayOfByte[(j + 4)] == 70) && (arrayOfByte[(j + 5)] == 73) && (arrayOfByte[(j + 6)] == 70) && (arrayOfByte[(j + 7)] == 0))
/*      */         {
/*  213 */           if (this.inThumb) {
/*  214 */             paramJPEGImageReader.warningOccurred(1);
/*      */ 
/*  218 */             localJFIFMarkerSegment = new JFIFMarkerSegment(localJPEGBuffer);
/*      */           } else {
/*  220 */             if (paramBoolean1) {
/*  221 */               throw new IIOException("JFIF not permitted in stream metadata");
/*      */             }
/*  223 */             if (!this.markerSequence.isEmpty()) {
/*  224 */               throw new IIOException("JFIF APP0 must be first marker after SOI");
/*      */             }
/*      */ 
/*  227 */             localObject = new JFIFMarkerSegment(localJPEGBuffer);
/*      */           }
/*  229 */         } else if ((arrayOfByte[(j + 3)] == 74) && (arrayOfByte[(j + 4)] == 70) && (arrayOfByte[(j + 5)] == 88) && (arrayOfByte[(j + 6)] == 88) && (arrayOfByte[(j + 7)] == 0))
/*      */         {
/*  234 */           if (paramBoolean1) {
/*  235 */             throw new IIOException("JFXX not permitted in stream metadata");
/*      */           }
/*      */ 
/*  238 */           if (this.inThumb) {
/*  239 */             throw new IIOException("JFXX markers not allowed in JFIF JPEG thumbnail");
/*      */           }
/*      */ 
/*  242 */           localJFIFMarkerSegment = (JFIFMarkerSegment)findMarkerSegment(JFIFMarkerSegment.class, true);
/*      */ 
/*  245 */           if (localJFIFMarkerSegment == null) {
/*  246 */             throw new IIOException("JFXX encountered without prior JFIF!");
/*      */           }
/*      */ 
/*  249 */           localJFIFMarkerSegment.addJFXX(localJPEGBuffer, paramJPEGImageReader);
/*      */         }
/*      */         else {
/*  252 */           localObject = new MarkerSegment(localJPEGBuffer);
/*  253 */           ((MarkerSegment)localObject).loadData(localJPEGBuffer);
/*      */         }
/*  255 */         break;
/*      */       case 226:
/*  258 */         localJPEGBuffer.loadBuf(15);
/*  259 */         if ((localJPEGBuffer.buf[(localJPEGBuffer.bufPtr + 3)] == 73) && (localJPEGBuffer.buf[(localJPEGBuffer.bufPtr + 4)] == 67) && (localJPEGBuffer.buf[(localJPEGBuffer.bufPtr + 5)] == 67) && (localJPEGBuffer.buf[(localJPEGBuffer.bufPtr + 6)] == 95) && (localJPEGBuffer.buf[(localJPEGBuffer.bufPtr + 7)] == 80) && (localJPEGBuffer.buf[(localJPEGBuffer.bufPtr + 8)] == 82) && (localJPEGBuffer.buf[(localJPEGBuffer.bufPtr + 9)] == 79) && (localJPEGBuffer.buf[(localJPEGBuffer.bufPtr + 10)] == 70) && (localJPEGBuffer.buf[(localJPEGBuffer.bufPtr + 11)] == 73) && (localJPEGBuffer.buf[(localJPEGBuffer.bufPtr + 12)] == 76) && (localJPEGBuffer.buf[(localJPEGBuffer.bufPtr + 13)] == 69) && (localJPEGBuffer.buf[(localJPEGBuffer.bufPtr + 14)] == 0))
/*      */         {
/*  272 */           if (paramBoolean1) {
/*  273 */             throw new IIOException("ICC profiles not permitted in stream metadata");
/*      */           }
/*      */ 
/*  277 */           localJFIFMarkerSegment = (JFIFMarkerSegment)findMarkerSegment(JFIFMarkerSegment.class, true);
/*      */ 
/*  280 */           if (localJFIFMarkerSegment == null) {
/*  281 */             throw new IIOException("ICC APP2 encountered without prior JFIF!");
/*      */           }
/*      */ 
/*  284 */           localJFIFMarkerSegment.addICC(localJPEGBuffer);
/*      */         }
/*      */         else {
/*  287 */           localObject = new MarkerSegment(localJPEGBuffer);
/*  288 */           ((MarkerSegment)localObject).loadData(localJPEGBuffer);
/*      */         }
/*  290 */         break;
/*      */       case 238:
/*  293 */         localJPEGBuffer.loadBuf(8);
/*  294 */         if ((localJPEGBuffer.buf[(localJPEGBuffer.bufPtr + 3)] == 65) && (localJPEGBuffer.buf[(localJPEGBuffer.bufPtr + 4)] == 100) && (localJPEGBuffer.buf[(localJPEGBuffer.bufPtr + 5)] == 111) && (localJPEGBuffer.buf[(localJPEGBuffer.bufPtr + 6)] == 98) && (localJPEGBuffer.buf[(localJPEGBuffer.bufPtr + 7)] == 101))
/*      */         {
/*  299 */           if (paramBoolean1) {
/*  300 */             throw new IIOException("Adobe APP14 markers not permitted in stream metadata");
/*      */           }
/*      */ 
/*  303 */           localObject = new AdobeMarkerSegment(localJPEGBuffer);
/*      */         } else {
/*  305 */           localObject = new MarkerSegment(localJPEGBuffer);
/*  306 */           ((MarkerSegment)localObject).loadData(localJPEGBuffer);
/*      */         }
/*      */ 
/*  309 */         break;
/*      */       case 254:
/*  311 */         localObject = new COMMarkerSegment(localJPEGBuffer);
/*  312 */         break;
/*      */       case 218:
/*  314 */         if (paramBoolean1) {
/*  315 */           throw new IIOException("SOS not permitted in stream metadata");
/*      */         }
/*      */ 
/*  318 */         localObject = new SOSMarkerSegment(localJPEGBuffer);
/*  319 */         break;
/*      */       case 208:
/*      */       case 209:
/*      */       case 210:
/*      */       case 211:
/*      */       case 212:
/*      */       case 213:
/*      */       case 214:
/*      */       case 215:
/*  331 */         localJPEGBuffer.bufPtr += 1;
/*  332 */         localJPEGBuffer.bufAvail -= 1;
/*  333 */         break;
/*      */       case 217:
/*  335 */         i = 1;
/*  336 */         localJPEGBuffer.bufPtr += 1;
/*  337 */         localJPEGBuffer.bufAvail -= 1;
/*  338 */         break;
/*      */       default:
/*  340 */         localObject = new MarkerSegment(localJPEGBuffer);
/*  341 */         ((MarkerSegment)localObject).loadData(localJPEGBuffer);
/*  342 */         ((MarkerSegment)localObject).unknown = true;
/*      */       }
/*      */ 
/*  345 */       if (localObject != null) {
/*  346 */         this.markerSequence.add(localObject);
/*      */ 
/*  350 */         localObject = null;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  358 */     localJPEGBuffer.pushBack();
/*      */ 
/*  360 */     if (!isConsistent())
/*  361 */       throw new IIOException("Inconsistent metadata read from stream");
/*      */   }
/*      */ 
/*      */   JPEGMetadata(ImageWriteParam paramImageWriteParam, JPEGImageWriter paramJPEGImageWriter)
/*      */   {
/*  370 */     this(true, false);
/*      */ 
/*  372 */     JPEGImageWriteParam localJPEGImageWriteParam = null;
/*      */ 
/*  374 */     if ((paramImageWriteParam != null) && ((paramImageWriteParam instanceof JPEGImageWriteParam))) {
/*  375 */       localJPEGImageWriteParam = (JPEGImageWriteParam)paramImageWriteParam;
/*  376 */       if (!localJPEGImageWriteParam.areTablesSet()) {
/*  377 */         localJPEGImageWriteParam = null;
/*      */       }
/*      */     }
/*  380 */     if (localJPEGImageWriteParam != null) {
/*  381 */       this.markerSequence.add(new DQTMarkerSegment(localJPEGImageWriteParam.getQTables()));
/*  382 */       this.markerSequence.add(new DHTMarkerSegment(localJPEGImageWriteParam.getDCHuffmanTables(), localJPEGImageWriteParam.getACHuffmanTables()));
/*      */     }
/*      */     else
/*      */     {
/*  387 */       this.markerSequence.add(new DQTMarkerSegment(JPEG.getDefaultQTables()));
/*  388 */       this.markerSequence.add(new DHTMarkerSegment(JPEG.getDefaultHuffmanTables(true), JPEG.getDefaultHuffmanTables(false)));
/*      */     }
/*      */ 
/*  393 */     if (!isConsistent())
/*  394 */       throw new InternalError("Default stream metadata is inconsistent");
/*      */   }
/*      */ 
/*      */   JPEGMetadata(ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam, JPEGImageWriter paramJPEGImageWriter)
/*      */   {
/*  405 */     this(false, false);
/*      */ 
/*  407 */     int i = 1;
/*  408 */     int j = 0;
/*  409 */     int k = 0;
/*  410 */     boolean bool1 = true;
/*  411 */     int m = 0;
/*  412 */     boolean bool2 = false;
/*  413 */     boolean bool3 = false;
/*  414 */     boolean bool4 = false;
/*  415 */     int n = 1;
/*  416 */     int i1 = 1;
/*  417 */     float f = 0.75F;
/*  418 */     byte[] arrayOfByte = { 1, 2, 3, 4 };
/*  419 */     int i2 = 0;
/*      */ 
/*  421 */     ImageTypeSpecifier localImageTypeSpecifier = null;
/*      */ 
/*  423 */     if (paramImageWriteParam != null) {
/*  424 */       localImageTypeSpecifier = paramImageWriteParam.getDestinationType();
/*  425 */       if ((localImageTypeSpecifier != null) && 
/*  426 */         (paramImageTypeSpecifier != null))
/*      */       {
/*  428 */         paramJPEGImageWriter.warningOccurred(0);
/*      */ 
/*  430 */         localImageTypeSpecifier = null;
/*      */       }
/*      */ 
/*  434 */       if (paramImageWriteParam.canWriteProgressive())
/*      */       {
/*  437 */         if (paramImageWriteParam.getProgressiveMode() == 1) {
/*  438 */           bool2 = true;
/*  439 */           bool3 = true;
/*  440 */           i1 = 0;
/*      */         }
/*      */       }
/*      */ 
/*  444 */       if ((paramImageWriteParam instanceof JPEGImageWriteParam)) {
/*  445 */         localObject1 = (JPEGImageWriteParam)paramImageWriteParam;
/*  446 */         if (((JPEGImageWriteParam)localObject1).areTablesSet()) {
/*  447 */           n = 0;
/*  448 */           i1 = 0;
/*  449 */           if ((((JPEGImageWriteParam)localObject1).getDCHuffmanTables().length > 2) || (((JPEGImageWriteParam)localObject1).getACHuffmanTables().length > 2))
/*      */           {
/*  451 */             bool4 = true;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  456 */         if (!bool2) {
/*  457 */           bool3 = ((JPEGImageWriteParam)localObject1).getOptimizeHuffmanTables();
/*  458 */           if (bool3) {
/*  459 */             i1 = 0;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  468 */       if ((paramImageWriteParam.canWriteCompressed()) && 
/*  469 */         (paramImageWriteParam.getCompressionMode() == 2)) {
/*  470 */         f = paramImageWriteParam.getCompressionQuality();
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  477 */     Object localObject1 = null;
/*      */     Object localObject2;
/*      */     int i3;
/*      */     boolean bool5;
/*      */     int i4;
/*  478 */     if (localImageTypeSpecifier != null) {
/*  479 */       localObject2 = localImageTypeSpecifier.getColorModel();
/*  480 */       i2 = ((ColorModel)localObject2).getNumComponents();
/*  481 */       i3 = ((ColorModel)localObject2).getNumColorComponents() != i2 ? 1 : 0;
/*  482 */       bool5 = ((ColorModel)localObject2).hasAlpha();
/*  483 */       localObject1 = ((ColorModel)localObject2).getColorSpace();
/*  484 */       i4 = ((ColorSpace)localObject1).getType();
/*  485 */       switch (i4) {
/*      */       case 6:
/*  487 */         bool1 = false;
/*  488 */         if (i3 != 0)
/*  489 */           i = 0; break;
/*      */       case 13:
/*  493 */         if (localObject1 == JPEG.JCS.getYCC()) {
/*  494 */           i = 0;
/*  495 */           arrayOfByte[0] = 89;
/*  496 */           arrayOfByte[1] = 67;
/*  497 */           arrayOfByte[2] = 99;
/*  498 */           if (bool5)
/*  499 */             arrayOfByte[3] = 65;  } break;
/*      */       case 3:
/*  504 */         if (i3 != 0) {
/*  505 */           i = 0;
/*  506 */           if (!bool5) {
/*  507 */             j = 1;
/*  508 */             k = 2; }  } break;
/*      */       case 5:
/*  513 */         i = 0;
/*  514 */         j = 1;
/*  515 */         bool1 = false;
/*  516 */         arrayOfByte[0] = 82;
/*  517 */         arrayOfByte[1] = 71;
/*  518 */         arrayOfByte[2] = 66;
/*  519 */         if (bool5)
/*  520 */           arrayOfByte[3] = 65; break;
/*      */       default:
/*  526 */         i = 0;
/*  527 */         bool1 = false;
/*      */       }
/*  529 */     } else if (paramImageTypeSpecifier != null) {
/*  530 */       localObject2 = paramImageTypeSpecifier.getColorModel();
/*  531 */       i2 = ((ColorModel)localObject2).getNumComponents();
/*  532 */       i3 = ((ColorModel)localObject2).getNumColorComponents() != i2 ? 1 : 0;
/*  533 */       bool5 = ((ColorModel)localObject2).hasAlpha();
/*  534 */       localObject1 = ((ColorModel)localObject2).getColorSpace();
/*  535 */       i4 = ((ColorSpace)localObject1).getType();
/*  536 */       switch (i4) {
/*      */       case 6:
/*  538 */         bool1 = false;
/*  539 */         if (i3 != 0)
/*  540 */           i = 0; break;
/*      */       case 5:
/*  545 */         if (bool5)
/*  546 */           i = 0; break;
/*      */       case 13:
/*  550 */         i = 0;
/*  551 */         bool1 = false;
/*  552 */         if (localObject1.equals(ColorSpace.getInstance(1002))) {
/*  553 */           bool1 = true;
/*  554 */           j = 1;
/*  555 */           arrayOfByte[0] = 89;
/*  556 */           arrayOfByte[1] = 67;
/*  557 */           arrayOfByte[2] = 99;
/*  558 */           if (bool5)
/*  559 */             arrayOfByte[3] = 65;  } break;
/*      */       case 3:
/*  564 */         if (i3 != 0) {
/*  565 */           i = 0;
/*  566 */           if (!bool5) {
/*  567 */             j = 1;
/*  568 */             k = 2; }  } break;
/*      */       case 9:
/*  573 */         i = 0;
/*  574 */         j = 1;
/*  575 */         k = 2;
/*  576 */         break;
/*      */       case 4:
/*      */       case 7:
/*      */       case 8:
/*      */       case 10:
/*      */       case 11:
/*      */       case 12:
/*      */       default:
/*  581 */         i = 0;
/*  582 */         bool1 = false;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  588 */     if ((i != 0) && (JPEG.isNonStandardICC((ColorSpace)localObject1))) {
/*  589 */       m = 1;
/*      */     }
/*      */ 
/*  593 */     if (i != 0) {
/*  594 */       localObject2 = new JFIFMarkerSegment();
/*  595 */       this.markerSequence.add(localObject2);
/*  596 */       if (m != 0)
/*      */         try {
/*  598 */           ((JFIFMarkerSegment)localObject2).addICC((ICC_ColorSpace)localObject1);
/*      */         }
/*      */         catch (IOException localIOException) {
/*      */         }
/*      */     }
/*  603 */     if (j != 0) {
/*  604 */       this.markerSequence.add(new AdobeMarkerSegment(k));
/*      */     }
/*      */ 
/*  608 */     if (n != 0) {
/*  609 */       this.markerSequence.add(new DQTMarkerSegment(f, bool1));
/*      */     }
/*      */ 
/*  613 */     if (i1 != 0) {
/*  614 */       this.markerSequence.add(new DHTMarkerSegment(bool1));
/*      */     }
/*      */ 
/*  618 */     this.markerSequence.add(new SOFMarkerSegment(bool2, bool4, bool1, arrayOfByte, i2));
/*      */ 
/*  625 */     if (!bool2) {
/*  626 */       this.markerSequence.add(new SOSMarkerSegment(bool1, arrayOfByte, i2));
/*      */     }
/*      */ 
/*  632 */     if (!isConsistent())
/*  633 */       throw new InternalError("Default image metadata is inconsistent");
/*      */   }
/*      */ 
/*      */   MarkerSegment findMarkerSegment(int paramInt)
/*      */   {
/*  647 */     Iterator localIterator = this.markerSequence.iterator();
/*  648 */     while (localIterator.hasNext()) {
/*  649 */       MarkerSegment localMarkerSegment = (MarkerSegment)localIterator.next();
/*  650 */       if (localMarkerSegment.tag == paramInt) {
/*  651 */         return localMarkerSegment;
/*      */       }
/*      */     }
/*  654 */     return null;
/*      */   }
/*      */ 
/*      */   MarkerSegment findMarkerSegment(Class paramClass, boolean paramBoolean)
/*      */   {
/*      */     Object localObject;
/*      */     MarkerSegment localMarkerSegment;
/*  662 */     if (paramBoolean) {
/*  663 */       localObject = this.markerSequence.iterator();
/*  664 */       while (((Iterator)localObject).hasNext()) {
/*  665 */         localMarkerSegment = (MarkerSegment)((Iterator)localObject).next();
/*  666 */         if (paramClass.isInstance(localMarkerSegment))
/*  667 */           return localMarkerSegment;
/*      */       }
/*      */     }
/*      */     else {
/*  671 */       localObject = this.markerSequence.listIterator(this.markerSequence.size());
/*  672 */       while (((ListIterator)localObject).hasPrevious()) {
/*  673 */         localMarkerSegment = (MarkerSegment)((ListIterator)localObject).previous();
/*  674 */         if (paramClass.isInstance(localMarkerSegment)) {
/*  675 */           return localMarkerSegment;
/*      */         }
/*      */       }
/*      */     }
/*  679 */     return null;
/*      */   }
/*      */ 
/*      */   private int findMarkerSegmentPosition(Class paramClass, boolean paramBoolean)
/*      */   {
/*      */     ListIterator localListIterator;
/*      */     int i;
/*      */     MarkerSegment localMarkerSegment;
/*  687 */     if (paramBoolean) {
/*  688 */       localListIterator = this.markerSequence.listIterator();
/*  689 */       for (i = 0; localListIterator.hasNext(); i++) {
/*  690 */         localMarkerSegment = (MarkerSegment)localListIterator.next();
/*  691 */         if (paramClass.isInstance(localMarkerSegment))
/*  692 */           return i;
/*      */       }
/*      */     }
/*      */     else {
/*  696 */       localListIterator = this.markerSequence.listIterator(this.markerSequence.size());
/*  697 */       for (i = this.markerSequence.size() - 1; localListIterator.hasPrevious(); i--) {
/*  698 */         localMarkerSegment = (MarkerSegment)localListIterator.previous();
/*  699 */         if (paramClass.isInstance(localMarkerSegment)) {
/*  700 */           return i;
/*      */         }
/*      */       }
/*      */     }
/*  704 */     return -1;
/*      */   }
/*      */ 
/*      */   private int findLastUnknownMarkerSegmentPosition() {
/*  708 */     ListIterator localListIterator = this.markerSequence.listIterator(this.markerSequence.size());
/*  709 */     for (int i = this.markerSequence.size() - 1; localListIterator.hasPrevious(); i--) {
/*  710 */       MarkerSegment localMarkerSegment = (MarkerSegment)localListIterator.previous();
/*  711 */       if (localMarkerSegment.unknown == true) {
/*  712 */         return i;
/*      */       }
/*      */     }
/*  715 */     return -1;
/*      */   }
/*      */ 
/*      */   protected Object clone()
/*      */   {
/*  721 */     JPEGMetadata localJPEGMetadata = null;
/*      */     try {
/*  723 */       localJPEGMetadata = (JPEGMetadata)super.clone(); } catch (CloneNotSupportedException localCloneNotSupportedException) {
/*      */     }
/*  725 */     if (this.markerSequence != null) {
/*  726 */       localJPEGMetadata.markerSequence = cloneSequence();
/*      */     }
/*  728 */     localJPEGMetadata.resetSequence = null;
/*  729 */     return localJPEGMetadata;
/*      */   }
/*      */ 
/*      */   private List cloneSequence()
/*      */   {
/*  736 */     if (this.markerSequence == null) {
/*  737 */       return null;
/*      */     }
/*  739 */     ArrayList localArrayList = new ArrayList(this.markerSequence.size());
/*  740 */     Iterator localIterator = this.markerSequence.iterator();
/*  741 */     while (localIterator.hasNext()) {
/*  742 */       MarkerSegment localMarkerSegment = (MarkerSegment)localIterator.next();
/*  743 */       localArrayList.add(localMarkerSegment.clone());
/*      */     }
/*      */ 
/*  746 */     return localArrayList;
/*      */   }
/*      */ 
/*      */   public Node getAsTree(String paramString)
/*      */   {
/*  753 */     if (paramString == null) {
/*  754 */       throw new IllegalArgumentException("null formatName!");
/*      */     }
/*  756 */     if (this.isStream) {
/*  757 */       if (paramString.equals("javax_imageio_jpeg_stream_1.0"))
/*  758 */         return getNativeTree();
/*      */     }
/*      */     else {
/*  761 */       if (paramString.equals("javax_imageio_jpeg_image_1.0")) {
/*  762 */         return getNativeTree();
/*      */       }
/*  764 */       if (paramString.equals("javax_imageio_1.0"))
/*      */       {
/*  766 */         return getStandardTree();
/*      */       }
/*      */     }
/*  769 */     throw new IllegalArgumentException("Unsupported format name: " + paramString);
/*      */   }
/*      */ 
/*      */   IIOMetadataNode getNativeTree()
/*      */   {
/*  776 */     Iterator localIterator = this.markerSequence.iterator();
/*      */     Object localObject1;
/*      */     Object localObject2;
/*      */     Object localObject3;
/*  777 */     if (this.isStream) {
/*  778 */       localObject1 = new IIOMetadataNode("javax_imageio_jpeg_stream_1.0");
/*  779 */       localObject2 = localObject1;
/*      */     } else {
/*  781 */       localObject3 = new IIOMetadataNode("markerSequence");
/*  782 */       if (!this.inThumb) {
/*  783 */         localObject1 = new IIOMetadataNode("javax_imageio_jpeg_image_1.0");
/*  784 */         IIOMetadataNode localIIOMetadataNode = new IIOMetadataNode("JPEGvariety");
/*  785 */         ((IIOMetadataNode)localObject1).appendChild(localIIOMetadataNode);
/*  786 */         JFIFMarkerSegment localJFIFMarkerSegment = (JFIFMarkerSegment)findMarkerSegment(JFIFMarkerSegment.class, true);
/*      */ 
/*  788 */         if (localJFIFMarkerSegment != null) {
/*  789 */           localIterator.next();
/*  790 */           localIIOMetadataNode.appendChild(localJFIFMarkerSegment.getNativeNode());
/*      */         }
/*  792 */         ((IIOMetadataNode)localObject1).appendChild((Node)localObject3);
/*      */       } else {
/*  794 */         localObject1 = localObject3;
/*      */       }
/*  796 */       localObject2 = localObject3;
/*      */     }
/*  798 */     while (localIterator.hasNext()) {
/*  799 */       localObject3 = (MarkerSegment)localIterator.next();
/*  800 */       localObject2.appendChild(((MarkerSegment)localObject3).getNativeNode());
/*      */     }
/*  802 */     return localObject1;
/*      */   }
/*      */ 
/*      */   protected IIOMetadataNode getStandardChromaNode()
/*      */   {
/*  808 */     this.hasAlpha = false;
/*      */ 
/*  812 */     SOFMarkerSegment localSOFMarkerSegment = (SOFMarkerSegment)findMarkerSegment(SOFMarkerSegment.class, true);
/*      */ 
/*  814 */     if (localSOFMarkerSegment == null)
/*      */     {
/*  816 */       return null;
/*      */     }
/*      */ 
/*  819 */     IIOMetadataNode localIIOMetadataNode1 = new IIOMetadataNode("Chroma");
/*  820 */     IIOMetadataNode localIIOMetadataNode2 = new IIOMetadataNode("ColorSpaceType");
/*  821 */     localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
/*      */ 
/*  824 */     int i = localSOFMarkerSegment.componentSpecs.length;
/*      */ 
/*  826 */     IIOMetadataNode localIIOMetadataNode3 = new IIOMetadataNode("NumChannels");
/*  827 */     localIIOMetadataNode1.appendChild(localIIOMetadataNode3);
/*  828 */     localIIOMetadataNode3.setAttribute("value", Integer.toString(i));
/*      */ 
/*  831 */     if (findMarkerSegment(JFIFMarkerSegment.class, true) != null) {
/*  832 */       if (i == 1)
/*  833 */         localIIOMetadataNode2.setAttribute("name", "GRAY");
/*      */       else {
/*  835 */         localIIOMetadataNode2.setAttribute("name", "YCbCr");
/*      */       }
/*  837 */       return localIIOMetadataNode1;
/*      */     }
/*      */ 
/*  841 */     AdobeMarkerSegment localAdobeMarkerSegment = (AdobeMarkerSegment)findMarkerSegment(AdobeMarkerSegment.class, true);
/*      */ 
/*  843 */     if (localAdobeMarkerSegment != null) {
/*  844 */       switch (localAdobeMarkerSegment.transform) {
/*      */       case 2:
/*  846 */         localIIOMetadataNode2.setAttribute("name", "YCCK");
/*  847 */         break;
/*      */       case 1:
/*  849 */         localIIOMetadataNode2.setAttribute("name", "YCbCr");
/*  850 */         break;
/*      */       case 0:
/*  852 */         if (i == 3)
/*  853 */           localIIOMetadataNode2.setAttribute("name", "RGB");
/*  854 */         else if (i == 4) {
/*  855 */           localIIOMetadataNode2.setAttribute("name", "CMYK");
/*      */         }
/*      */         break;
/*      */       }
/*  859 */       return localIIOMetadataNode1;
/*      */     }
/*      */ 
/*  863 */     if (i < 3) {
/*  864 */       localIIOMetadataNode2.setAttribute("name", "GRAY");
/*  865 */       if (i == 2) {
/*  866 */         this.hasAlpha = true;
/*      */       }
/*  868 */       return localIIOMetadataNode1;
/*      */     }
/*      */ 
/*  871 */     int j = 1;
/*      */ 
/*  873 */     for (int k = 0; k < localSOFMarkerSegment.componentSpecs.length; k++) {
/*  874 */       m = localSOFMarkerSegment.componentSpecs[k].componentId;
/*  875 */       if ((m < 1) || (m >= localSOFMarkerSegment.componentSpecs.length)) {
/*  876 */         j = 0;
/*      */       }
/*      */     }
/*      */ 
/*  880 */     if (j != 0) {
/*  881 */       localIIOMetadataNode2.setAttribute("name", "YCbCr");
/*  882 */       if (i == 4) {
/*  883 */         this.hasAlpha = true;
/*      */       }
/*  885 */       return localIIOMetadataNode1;
/*      */     }
/*      */ 
/*  889 */     if ((localSOFMarkerSegment.componentSpecs[0].componentId == 82) && (localSOFMarkerSegment.componentSpecs[1].componentId == 71) && (localSOFMarkerSegment.componentSpecs[2].componentId == 66))
/*      */     {
/*  893 */       localIIOMetadataNode2.setAttribute("name", "RGB");
/*  894 */       if ((i == 4) && (localSOFMarkerSegment.componentSpecs[3].componentId == 65))
/*      */       {
/*  896 */         this.hasAlpha = true;
/*      */       }
/*  898 */       return localIIOMetadataNode1;
/*      */     }
/*      */ 
/*  901 */     if ((localSOFMarkerSegment.componentSpecs[0].componentId == 89) && (localSOFMarkerSegment.componentSpecs[1].componentId == 67) && (localSOFMarkerSegment.componentSpecs[2].componentId == 99))
/*      */     {
/*  905 */       localIIOMetadataNode2.setAttribute("name", "PhotoYCC");
/*  906 */       if ((i == 4) && (localSOFMarkerSegment.componentSpecs[3].componentId == 65))
/*      */       {
/*  908 */         this.hasAlpha = true;
/*      */       }
/*  910 */       return localIIOMetadataNode1;
/*      */     }
/*      */ 
/*  916 */     k = 0;
/*      */ 
/*  918 */     int m = localSOFMarkerSegment.componentSpecs[0].HsamplingFactor;
/*  919 */     int n = localSOFMarkerSegment.componentSpecs[0].VsamplingFactor;
/*      */ 
/*  921 */     for (int i1 = 1; i1 < localSOFMarkerSegment.componentSpecs.length; i1++) {
/*  922 */       if ((localSOFMarkerSegment.componentSpecs[i1].HsamplingFactor != m) || (localSOFMarkerSegment.componentSpecs[i1].VsamplingFactor != n))
/*      */       {
/*  924 */         k = 1;
/*  925 */         break;
/*      */       }
/*      */     }
/*      */ 
/*  929 */     if (k != 0) {
/*  930 */       localIIOMetadataNode2.setAttribute("name", "YCbCr");
/*  931 */       if (i == 4) {
/*  932 */         this.hasAlpha = true;
/*      */       }
/*  934 */       return localIIOMetadataNode1;
/*      */     }
/*      */ 
/*  938 */     if (i == 3)
/*  939 */       localIIOMetadataNode2.setAttribute("name", "RGB");
/*      */     else {
/*  941 */       localIIOMetadataNode2.setAttribute("name", "CMYK");
/*      */     }
/*      */ 
/*  944 */     return localIIOMetadataNode1;
/*      */   }
/*      */ 
/*      */   protected IIOMetadataNode getStandardCompressionNode()
/*      */   {
/*  949 */     IIOMetadataNode localIIOMetadataNode1 = new IIOMetadataNode("Compression");
/*      */ 
/*  952 */     IIOMetadataNode localIIOMetadataNode2 = new IIOMetadataNode("CompressionTypeName");
/*  953 */     localIIOMetadataNode2.setAttribute("value", "JPEG");
/*  954 */     localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
/*      */ 
/*  957 */     IIOMetadataNode localIIOMetadataNode3 = new IIOMetadataNode("Lossless");
/*  958 */     localIIOMetadataNode3.setAttribute("value", "FALSE");
/*  959 */     localIIOMetadataNode1.appendChild(localIIOMetadataNode3);
/*      */ 
/*  962 */     int i = 0;
/*  963 */     Iterator localIterator = this.markerSequence.iterator();
/*      */     Object localObject;
/*  964 */     while (localIterator.hasNext()) {
/*  965 */       localObject = (MarkerSegment)localIterator.next();
/*  966 */       if (((MarkerSegment)localObject).tag == 218) {
/*  967 */         i++;
/*      */       }
/*      */     }
/*  970 */     if (i != 0) {
/*  971 */       localObject = new IIOMetadataNode("NumProgressiveScans");
/*  972 */       ((IIOMetadataNode)localObject).setAttribute("value", Integer.toString(i));
/*  973 */       localIIOMetadataNode1.appendChild((Node)localObject);
/*      */     }
/*      */ 
/*  976 */     return localIIOMetadataNode1;
/*      */   }
/*      */ 
/*      */   protected IIOMetadataNode getStandardDimensionNode()
/*      */   {
/*  982 */     IIOMetadataNode localIIOMetadataNode1 = new IIOMetadataNode("Dimension");
/*  983 */     IIOMetadataNode localIIOMetadataNode2 = new IIOMetadataNode("ImageOrientation");
/*  984 */     localIIOMetadataNode2.setAttribute("value", "normal");
/*  985 */     localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
/*      */ 
/*  987 */     JFIFMarkerSegment localJFIFMarkerSegment = (JFIFMarkerSegment)findMarkerSegment(JFIFMarkerSegment.class, true);
/*      */ 
/*  989 */     if (localJFIFMarkerSegment != null)
/*      */     {
/*      */       float f1;
/*  993 */       if (localJFIFMarkerSegment.resUnits == 0)
/*      */       {
/*  995 */         f1 = localJFIFMarkerSegment.Xdensity / localJFIFMarkerSegment.Ydensity;
/*      */       }
/*      */       else {
/*  998 */         f1 = localJFIFMarkerSegment.Ydensity / localJFIFMarkerSegment.Xdensity;
/*      */       }
/* 1000 */       IIOMetadataNode localIIOMetadataNode3 = new IIOMetadataNode("PixelAspectRatio");
/* 1001 */       localIIOMetadataNode3.setAttribute("value", Float.toString(f1));
/* 1002 */       localIIOMetadataNode1.insertBefore(localIIOMetadataNode3, localIIOMetadataNode2);
/*      */ 
/* 1005 */       if (localJFIFMarkerSegment.resUnits != 0)
/*      */       {
/* 1007 */         float f2 = localJFIFMarkerSegment.resUnits == 1 ? 25.4F : 10.0F;
/*      */ 
/* 1009 */         IIOMetadataNode localIIOMetadataNode4 = new IIOMetadataNode("HorizontalPixelSize");
/*      */ 
/* 1011 */         localIIOMetadataNode4.setAttribute("value", Float.toString(f2 / localJFIFMarkerSegment.Xdensity));
/*      */ 
/* 1013 */         localIIOMetadataNode1.appendChild(localIIOMetadataNode4);
/*      */ 
/* 1015 */         IIOMetadataNode localIIOMetadataNode5 = new IIOMetadataNode("VerticalPixelSize");
/*      */ 
/* 1017 */         localIIOMetadataNode5.setAttribute("value", Float.toString(f2 / localJFIFMarkerSegment.Ydensity));
/*      */ 
/* 1019 */         localIIOMetadataNode1.appendChild(localIIOMetadataNode5);
/*      */       }
/*      */     }
/* 1022 */     return localIIOMetadataNode1;
/*      */   }
/*      */ 
/*      */   protected IIOMetadataNode getStandardTextNode() {
/* 1026 */     IIOMetadataNode localIIOMetadataNode1 = null;
/*      */ 
/* 1028 */     if (findMarkerSegment(254) != null) {
/* 1029 */       localIIOMetadataNode1 = new IIOMetadataNode("Text");
/* 1030 */       Iterator localIterator = this.markerSequence.iterator();
/* 1031 */       while (localIterator.hasNext()) {
/* 1032 */         MarkerSegment localMarkerSegment = (MarkerSegment)localIterator.next();
/* 1033 */         if (localMarkerSegment.tag == 254) {
/* 1034 */           COMMarkerSegment localCOMMarkerSegment = (COMMarkerSegment)localMarkerSegment;
/* 1035 */           IIOMetadataNode localIIOMetadataNode2 = new IIOMetadataNode("TextEntry");
/* 1036 */           localIIOMetadataNode2.setAttribute("keyword", "comment");
/* 1037 */           localIIOMetadataNode2.setAttribute("value", localCOMMarkerSegment.getComment());
/* 1038 */           localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
/*      */         }
/*      */       }
/*      */     }
/* 1042 */     return localIIOMetadataNode1;
/*      */   }
/*      */ 
/*      */   protected IIOMetadataNode getStandardTransparencyNode() {
/* 1046 */     IIOMetadataNode localIIOMetadataNode1 = null;
/* 1047 */     if (this.hasAlpha == true) {
/* 1048 */       localIIOMetadataNode1 = new IIOMetadataNode("Transparency");
/* 1049 */       IIOMetadataNode localIIOMetadataNode2 = new IIOMetadataNode("Alpha");
/* 1050 */       localIIOMetadataNode2.setAttribute("value", "nonpremultiplied");
/* 1051 */       localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
/*      */     }
/* 1053 */     return localIIOMetadataNode1;
/*      */   }
/*      */ 
/*      */   public boolean isReadOnly()
/*      */   {
/* 1059 */     return false;
/*      */   }
/*      */ 
/*      */   public void mergeTree(String paramString, Node paramNode) throws IIOInvalidTreeException
/*      */   {
/* 1064 */     if (paramString == null) {
/* 1065 */       throw new IllegalArgumentException("null formatName!");
/*      */     }
/* 1067 */     if (paramNode == null) {
/* 1068 */       throw new IllegalArgumentException("null root!");
/*      */     }
/* 1070 */     List localList = null;
/* 1071 */     if (this.resetSequence == null) {
/* 1072 */       this.resetSequence = cloneSequence();
/* 1073 */       localList = this.resetSequence;
/*      */     } else {
/* 1075 */       localList = cloneSequence();
/*      */     }
/* 1077 */     if ((this.isStream) && (paramString.equals("javax_imageio_jpeg_stream_1.0")))
/*      */     {
/* 1079 */       mergeNativeTree(paramNode);
/* 1080 */     } else if ((!this.isStream) && (paramString.equals("javax_imageio_jpeg_image_1.0")))
/*      */     {
/* 1082 */       mergeNativeTree(paramNode);
/* 1083 */     } else if ((!this.isStream) && (paramString.equals("javax_imageio_1.0")))
/*      */     {
/* 1086 */       mergeStandardTree(paramNode);
/*      */     }
/* 1088 */     else throw new IllegalArgumentException("Unsupported format name: " + paramString);
/*      */ 
/* 1091 */     if (!isConsistent()) {
/* 1092 */       this.markerSequence = localList;
/* 1093 */       throw new IIOInvalidTreeException("Merged tree is invalid; original restored", paramNode);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void mergeNativeTree(Node paramNode) throws IIOInvalidTreeException
/*      */   {
/* 1099 */     String str = paramNode.getNodeName();
/* 1100 */     if (str != (this.isStream ? "javax_imageio_jpeg_stream_1.0" : "javax_imageio_jpeg_image_1.0"))
/*      */     {
/* 1102 */       throw new IIOInvalidTreeException("Invalid root node name: " + str, paramNode);
/*      */     }
/*      */ 
/* 1105 */     if (paramNode.getChildNodes().getLength() != 2) {
/* 1106 */       throw new IIOInvalidTreeException("JPEGvariety and markerSequence nodes must be present", paramNode);
/*      */     }
/*      */ 
/* 1109 */     mergeJFIFsubtree(paramNode.getFirstChild());
/* 1110 */     mergeSequenceSubtree(paramNode.getLastChild());
/*      */   }
/*      */ 
/*      */   private void mergeJFIFsubtree(Node paramNode)
/*      */     throws IIOInvalidTreeException
/*      */   {
/* 1122 */     if (paramNode.getChildNodes().getLength() != 0) {
/* 1123 */       Node localNode = paramNode.getFirstChild();
/*      */ 
/* 1125 */       JFIFMarkerSegment localJFIFMarkerSegment = (JFIFMarkerSegment)findMarkerSegment(JFIFMarkerSegment.class, true);
/*      */ 
/* 1127 */       if (localJFIFMarkerSegment != null) {
/* 1128 */         localJFIFMarkerSegment.updateFromNativeNode(localNode, false);
/*      */       }
/*      */       else
/* 1131 */         this.markerSequence.add(0, new JFIFMarkerSegment(localNode));
/*      */     }
/*      */   }
/*      */ 
/*      */   private void mergeSequenceSubtree(Node paramNode)
/*      */     throws IIOInvalidTreeException
/*      */   {
/* 1138 */     NodeList localNodeList = paramNode.getChildNodes();
/* 1139 */     for (int i = 0; i < localNodeList.getLength(); i++) {
/* 1140 */       Node localNode = localNodeList.item(i);
/* 1141 */       String str = localNode.getNodeName();
/* 1142 */       if (str.equals("dqt"))
/* 1143 */         mergeDQTNode(localNode);
/* 1144 */       else if (str.equals("dht"))
/* 1145 */         mergeDHTNode(localNode);
/* 1146 */       else if (str.equals("dri"))
/* 1147 */         mergeDRINode(localNode);
/* 1148 */       else if (str.equals("com"))
/* 1149 */         mergeCOMNode(localNode);
/* 1150 */       else if (str.equals("app14Adobe"))
/* 1151 */         mergeAdobeNode(localNode);
/* 1152 */       else if (str.equals("unknown"))
/* 1153 */         mergeUnknownNode(localNode);
/* 1154 */       else if (str.equals("sof"))
/* 1155 */         mergeSOFNode(localNode);
/* 1156 */       else if (str.equals("sos"))
/* 1157 */         mergeSOSNode(localNode);
/*      */       else
/* 1159 */         throw new IIOInvalidTreeException("Invalid node: " + str, localNode);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void mergeDQTNode(Node paramNode)
/*      */     throws IIOInvalidTreeException
/*      */   {
/* 1183 */     ArrayList localArrayList = new ArrayList();
/* 1184 */     Iterator localIterator = this.markerSequence.iterator();
/*      */     Object localObject1;
/* 1185 */     while (localIterator.hasNext()) {
/* 1186 */       localObject1 = (MarkerSegment)localIterator.next();
/* 1187 */       if ((localObject1 instanceof DQTMarkerSegment))
/* 1188 */         localArrayList.add(localObject1);
/*      */     }
/*      */     int i;
/*      */     int k;
/* 1191 */     if (!localArrayList.isEmpty()) {
/* 1192 */       localObject1 = paramNode.getChildNodes();
/* 1193 */       for (i = 0; i < ((NodeList)localObject1).getLength(); i++) {
/* 1194 */         Node localNode = ((NodeList)localObject1).item(i);
/* 1195 */         k = MarkerSegment.getAttributeValue(localNode, null, "qtableId", 0, 3, true);
/*      */ 
/* 1200 */         Object localObject2 = null;
/* 1201 */         int m = -1;
/* 1202 */         for (int n = 0; n < localArrayList.size(); n++) {
/* 1203 */           DQTMarkerSegment localDQTMarkerSegment = (DQTMarkerSegment)localArrayList.get(n);
/* 1204 */           for (int i1 = 0; i1 < localDQTMarkerSegment.tables.size(); i1++) {
/* 1205 */             DQTMarkerSegment.Qtable localQtable = (DQTMarkerSegment.Qtable)localDQTMarkerSegment.tables.get(i1);
/*      */ 
/* 1207 */             if (k == localQtable.tableID) {
/* 1208 */               localObject2 = localDQTMarkerSegment;
/* 1209 */               m = i1;
/* 1210 */               break;
/*      */             }
/*      */           }
/* 1213 */           if (localObject2 != null) break;
/*      */         }
/* 1215 */         if (localObject2 != null) {
/* 1216 */           ((DQTMarkerSegment)localObject2).tables.set(m, ((DQTMarkerSegment)localObject2).getQtableFromNode(localNode));
/*      */         } else {
/* 1218 */           localObject2 = (DQTMarkerSegment)localArrayList.get(localArrayList.size() - 1);
/* 1219 */           ((DQTMarkerSegment)localObject2).tables.add(((DQTMarkerSegment)localObject2).getQtableFromNode(localNode));
/*      */         }
/*      */       }
/*      */     } else {
/* 1223 */       localObject1 = new DQTMarkerSegment(paramNode);
/* 1224 */       i = findMarkerSegmentPosition(DHTMarkerSegment.class, true);
/* 1225 */       int j = findMarkerSegmentPosition(SOFMarkerSegment.class, true);
/* 1226 */       k = findMarkerSegmentPosition(SOSMarkerSegment.class, true);
/* 1227 */       if (i != -1)
/* 1228 */         this.markerSequence.add(i, localObject1);
/* 1229 */       else if (j != -1)
/* 1230 */         this.markerSequence.add(j, localObject1);
/* 1231 */       else if (k != -1)
/* 1232 */         this.markerSequence.add(k, localObject1);
/*      */       else
/* 1234 */         this.markerSequence.add(localObject1);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void mergeDHTNode(Node paramNode)
/*      */     throws IIOInvalidTreeException
/*      */   {
/* 1259 */     ArrayList localArrayList = new ArrayList();
/* 1260 */     Iterator localIterator = this.markerSequence.iterator();
/*      */     Object localObject1;
/* 1261 */     while (localIterator.hasNext()) {
/* 1262 */       localObject1 = (MarkerSegment)localIterator.next();
/* 1263 */       if ((localObject1 instanceof DHTMarkerSegment))
/* 1264 */         localArrayList.add(localObject1);
/*      */     }
/*      */     int i;
/* 1267 */     if (!localArrayList.isEmpty()) {
/* 1268 */       localObject1 = paramNode.getChildNodes();
/* 1269 */       for (i = 0; i < ((NodeList)localObject1).getLength(); i++) {
/* 1270 */         Node localNode = ((NodeList)localObject1).item(i);
/* 1271 */         NamedNodeMap localNamedNodeMap = localNode.getAttributes();
/* 1272 */         int m = MarkerSegment.getAttributeValue(localNode, localNamedNodeMap, "htableId", 0, 3, true);
/*      */ 
/* 1277 */         int n = MarkerSegment.getAttributeValue(localNode, localNamedNodeMap, "class", 0, 1, true);
/*      */ 
/* 1282 */         Object localObject2 = null;
/* 1283 */         int i1 = -1;
/* 1284 */         for (int i2 = 0; i2 < localArrayList.size(); i2++) {
/* 1285 */           DHTMarkerSegment localDHTMarkerSegment = (DHTMarkerSegment)localArrayList.get(i2);
/* 1286 */           for (int i3 = 0; i3 < localDHTMarkerSegment.tables.size(); i3++) {
/* 1287 */             DHTMarkerSegment.Htable localHtable = (DHTMarkerSegment.Htable)localDHTMarkerSegment.tables.get(i3);
/*      */ 
/* 1289 */             if ((m == localHtable.tableID) && (n == localHtable.tableClass))
/*      */             {
/* 1291 */               localObject2 = localDHTMarkerSegment;
/* 1292 */               i1 = i3;
/* 1293 */               break;
/*      */             }
/*      */           }
/* 1296 */           if (localObject2 != null) break;
/*      */         }
/* 1298 */         if (localObject2 != null) {
/* 1299 */           ((DHTMarkerSegment)localObject2).tables.set(i1, ((DHTMarkerSegment)localObject2).getHtableFromNode(localNode));
/*      */         } else {
/* 1301 */           localObject2 = (DHTMarkerSegment)localArrayList.get(localArrayList.size() - 1);
/* 1302 */           ((DHTMarkerSegment)localObject2).tables.add(((DHTMarkerSegment)localObject2).getHtableFromNode(localNode));
/*      */         }
/*      */       }
/*      */     } else {
/* 1306 */       localObject1 = new DHTMarkerSegment(paramNode);
/* 1307 */       i = findMarkerSegmentPosition(DQTMarkerSegment.class, false);
/* 1308 */       int j = findMarkerSegmentPosition(SOFMarkerSegment.class, true);
/* 1309 */       int k = findMarkerSegmentPosition(SOSMarkerSegment.class, true);
/* 1310 */       if (i != -1)
/* 1311 */         this.markerSequence.add(i + 1, localObject1);
/* 1312 */       else if (j != -1)
/* 1313 */         this.markerSequence.add(j, localObject1);
/* 1314 */       else if (k != -1)
/* 1315 */         this.markerSequence.add(k, localObject1);
/*      */       else
/* 1317 */         this.markerSequence.add(localObject1);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void mergeDRINode(Node paramNode)
/*      */     throws IIOInvalidTreeException
/*      */   {
/* 1336 */     DRIMarkerSegment localDRIMarkerSegment1 = (DRIMarkerSegment)findMarkerSegment(DRIMarkerSegment.class, true);
/*      */ 
/* 1338 */     if (localDRIMarkerSegment1 != null) {
/* 1339 */       localDRIMarkerSegment1.updateFromNativeNode(paramNode, false);
/*      */     } else {
/* 1341 */       DRIMarkerSegment localDRIMarkerSegment2 = new DRIMarkerSegment(paramNode);
/* 1342 */       int i = findMarkerSegmentPosition(SOFMarkerSegment.class, true);
/* 1343 */       int j = findMarkerSegmentPosition(SOSMarkerSegment.class, true);
/* 1344 */       if (i != -1)
/* 1345 */         this.markerSequence.add(i, localDRIMarkerSegment2);
/* 1346 */       else if (j != -1)
/* 1347 */         this.markerSequence.add(j, localDRIMarkerSegment2);
/*      */       else
/* 1349 */         this.markerSequence.add(localDRIMarkerSegment2);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void mergeCOMNode(Node paramNode)
/*      */     throws IIOInvalidTreeException
/*      */   {
/* 1360 */     COMMarkerSegment localCOMMarkerSegment = new COMMarkerSegment(paramNode);
/* 1361 */     insertCOMMarkerSegment(localCOMMarkerSegment);
/*      */   }
/*      */ 
/*      */   private void insertCOMMarkerSegment(COMMarkerSegment paramCOMMarkerSegment)
/*      */   {
/* 1377 */     int i = findMarkerSegmentPosition(COMMarkerSegment.class, false);
/* 1378 */     int j = findMarkerSegment(JFIFMarkerSegment.class, true) != null ? 1 : 0;
/* 1379 */     int k = findMarkerSegmentPosition(AdobeMarkerSegment.class, true);
/* 1380 */     if (i != -1)
/* 1381 */       this.markerSequence.add(i + 1, paramCOMMarkerSegment);
/* 1382 */     else if (j != 0)
/* 1383 */       this.markerSequence.add(1, paramCOMMarkerSegment);
/* 1384 */     else if (k != -1)
/* 1385 */       this.markerSequence.add(k + 1, paramCOMMarkerSegment);
/*      */     else
/* 1387 */       this.markerSequence.add(0, paramCOMMarkerSegment);
/*      */   }
/*      */ 
/*      */   private void mergeAdobeNode(Node paramNode)
/*      */     throws IIOInvalidTreeException
/*      */   {
/* 1399 */     AdobeMarkerSegment localAdobeMarkerSegment1 = (AdobeMarkerSegment)findMarkerSegment(AdobeMarkerSegment.class, true);
/*      */ 
/* 1401 */     if (localAdobeMarkerSegment1 != null) {
/* 1402 */       localAdobeMarkerSegment1.updateFromNativeNode(paramNode, false);
/*      */     } else {
/* 1404 */       AdobeMarkerSegment localAdobeMarkerSegment2 = new AdobeMarkerSegment(paramNode);
/* 1405 */       insertAdobeMarkerSegment(localAdobeMarkerSegment2);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void insertAdobeMarkerSegment(AdobeMarkerSegment paramAdobeMarkerSegment)
/*      */   {
/* 1420 */     int i = findMarkerSegment(JFIFMarkerSegment.class, true) != null ? 1 : 0;
/*      */ 
/* 1422 */     int j = findLastUnknownMarkerSegmentPosition();
/* 1423 */     if (i != 0)
/* 1424 */       this.markerSequence.add(1, paramAdobeMarkerSegment);
/* 1425 */     else if (j != -1)
/* 1426 */       this.markerSequence.add(j + 1, paramAdobeMarkerSegment);
/*      */     else
/* 1428 */       this.markerSequence.add(0, paramAdobeMarkerSegment);
/*      */   }
/*      */ 
/*      */   private void mergeUnknownNode(Node paramNode)
/*      */     throws IIOInvalidTreeException
/*      */   {
/* 1446 */     MarkerSegment localMarkerSegment = new MarkerSegment(paramNode);
/* 1447 */     int i = findLastUnknownMarkerSegmentPosition();
/* 1448 */     int j = findMarkerSegment(JFIFMarkerSegment.class, true) != null ? 1 : 0;
/* 1449 */     int k = findMarkerSegmentPosition(AdobeMarkerSegment.class, true);
/* 1450 */     if (i != -1)
/* 1451 */       this.markerSequence.add(i + 1, localMarkerSegment);
/* 1452 */     else if (j != 0)
/* 1453 */       this.markerSequence.add(1, localMarkerSegment);
/* 1454 */     if (k != -1)
/* 1455 */       this.markerSequence.add(k, localMarkerSegment);
/*      */     else
/* 1457 */       this.markerSequence.add(0, localMarkerSegment);
/*      */   }
/*      */ 
/*      */   private void mergeSOFNode(Node paramNode)
/*      */     throws IIOInvalidTreeException
/*      */   {
/* 1474 */     SOFMarkerSegment localSOFMarkerSegment1 = (SOFMarkerSegment)findMarkerSegment(SOFMarkerSegment.class, true);
/*      */ 
/* 1476 */     if (localSOFMarkerSegment1 != null) {
/* 1477 */       localSOFMarkerSegment1.updateFromNativeNode(paramNode, false);
/*      */     } else {
/* 1479 */       SOFMarkerSegment localSOFMarkerSegment2 = new SOFMarkerSegment(paramNode);
/* 1480 */       int i = findMarkerSegmentPosition(SOSMarkerSegment.class, true);
/* 1481 */       if (i != -1)
/* 1482 */         this.markerSequence.add(i, localSOFMarkerSegment2);
/*      */       else
/* 1484 */         this.markerSequence.add(localSOFMarkerSegment2);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void mergeSOSNode(Node paramNode)
/*      */     throws IIOInvalidTreeException
/*      */   {
/* 1500 */     SOSMarkerSegment localSOSMarkerSegment1 = (SOSMarkerSegment)findMarkerSegment(SOSMarkerSegment.class, true);
/*      */ 
/* 1502 */     SOSMarkerSegment localSOSMarkerSegment2 = (SOSMarkerSegment)findMarkerSegment(SOSMarkerSegment.class, false);
/*      */ 
/* 1504 */     if (localSOSMarkerSegment1 != null) {
/* 1505 */       if (localSOSMarkerSegment1 != localSOSMarkerSegment2) {
/* 1506 */         throw new IIOInvalidTreeException("Can't merge SOS node into a tree with > 1 SOS node", paramNode);
/*      */       }
/*      */ 
/* 1509 */       localSOSMarkerSegment1.updateFromNativeNode(paramNode, false);
/*      */     } else {
/* 1511 */       this.markerSequence.add(new SOSMarkerSegment(paramNode));
/*      */     }
/*      */   }
/*      */ 
/*      */   private void mergeStandardTree(Node paramNode)
/*      */     throws IIOInvalidTreeException
/*      */   {
/* 1518 */     this.transparencyDone = false;
/* 1519 */     NodeList localNodeList = paramNode.getChildNodes();
/* 1520 */     for (int i = 0; i < localNodeList.getLength(); i++) {
/* 1521 */       Node localNode = localNodeList.item(i);
/* 1522 */       String str = localNode.getNodeName();
/* 1523 */       if (str.equals("Chroma"))
/* 1524 */         mergeStandardChromaNode(localNode, localNodeList);
/* 1525 */       else if (str.equals("Compression"))
/* 1526 */         mergeStandardCompressionNode(localNode);
/* 1527 */       else if (str.equals("Data"))
/* 1528 */         mergeStandardDataNode(localNode);
/* 1529 */       else if (str.equals("Dimension"))
/* 1530 */         mergeStandardDimensionNode(localNode);
/* 1531 */       else if (str.equals("Document"))
/* 1532 */         mergeStandardDocumentNode(localNode);
/* 1533 */       else if (str.equals("Text"))
/* 1534 */         mergeStandardTextNode(localNode);
/* 1535 */       else if (str.equals("Transparency"))
/* 1536 */         mergeStandardTransparencyNode(localNode);
/*      */       else
/* 1538 */         throw new IIOInvalidTreeException("Invalid node: " + str, localNode);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void mergeStandardChromaNode(Node paramNode, NodeList paramNodeList)
/*      */     throws IIOInvalidTreeException
/*      */   {
/* 1562 */     if (this.transparencyDone) {
/* 1563 */       throw new IIOInvalidTreeException("Transparency node must follow Chroma node", paramNode);
/*      */     }
/*      */ 
/* 1567 */     Node localNode = paramNode.getFirstChild();
/* 1568 */     if ((localNode == null) || (!localNode.getNodeName().equals("ColorSpaceType")))
/*      */     {
/* 1570 */       return;
/*      */     }
/*      */ 
/* 1573 */     String str = localNode.getAttributes().getNamedItem("name").getNodeValue();
/*      */ 
/* 1575 */     int i = 0;
/* 1576 */     int j = 0;
/* 1577 */     int k = 0;
/* 1578 */     int m = 0;
/* 1579 */     boolean bool1 = false;
/* 1580 */     byte[] arrayOfByte = { 1, 2, 3, 4 };
/* 1581 */     if (str.equals("GRAY")) {
/* 1582 */       i = 1;
/* 1583 */       j = 1;
/* 1584 */     } else if (str.equals("YCbCr")) {
/* 1585 */       i = 3;
/* 1586 */       j = 1;
/* 1587 */       bool1 = true;
/* 1588 */     } else if (str.equals("PhotoYCC")) {
/* 1589 */       i = 3;
/* 1590 */       k = 1;
/* 1591 */       m = 1;
/* 1592 */       arrayOfByte[0] = 89;
/* 1593 */       arrayOfByte[1] = 67;
/* 1594 */       arrayOfByte[2] = 99;
/* 1595 */     } else if (str.equals("RGB")) {
/* 1596 */       i = 3;
/* 1597 */       k = 1;
/* 1598 */       m = 0;
/* 1599 */       arrayOfByte[0] = 82;
/* 1600 */       arrayOfByte[1] = 71;
/* 1601 */       arrayOfByte[2] = 66;
/* 1602 */     } else if ((str.equals("XYZ")) || (str.equals("Lab")) || (str.equals("Luv")) || (str.equals("YxY")) || (str.equals("HSV")) || (str.equals("HLS")) || (str.equals("CMY")) || (str.equals("3CLR")))
/*      */     {
/* 1610 */       i = 3;
/* 1611 */     } else if (str.equals("YCCK")) {
/* 1612 */       i = 4;
/* 1613 */       k = 1;
/* 1614 */       m = 2;
/* 1615 */       bool1 = true;
/* 1616 */     } else if (str.equals("CMYK")) {
/* 1617 */       i = 4;
/* 1618 */       k = 1;
/* 1619 */       m = 0;
/* 1620 */     } else if (str.equals("4CLR")) {
/* 1621 */       i = 4;
/*      */     } else {
/* 1623 */       return;
/*      */     }
/*      */ 
/* 1626 */     boolean bool2 = false;
/* 1627 */     for (int n = 0; n < paramNodeList.getLength(); n++) {
/* 1628 */       localObject1 = paramNodeList.item(n);
/* 1629 */       if (((Node)localObject1).getNodeName().equals("Transparency")) {
/* 1630 */         bool2 = wantAlpha((Node)localObject1);
/* 1631 */         break;
/*      */       }
/*      */     }
/*      */ 
/* 1635 */     if (bool2) {
/* 1636 */       i++;
/* 1637 */       j = 0;
/* 1638 */       if (arrayOfByte[0] == 82) {
/* 1639 */         arrayOfByte[3] = 65;
/* 1640 */         k = 0;
/*      */       }
/*      */     }
/*      */ 
/* 1644 */     JFIFMarkerSegment localJFIFMarkerSegment = (JFIFMarkerSegment)findMarkerSegment(JFIFMarkerSegment.class, true);
/*      */ 
/* 1646 */     Object localObject1 = (AdobeMarkerSegment)findMarkerSegment(AdobeMarkerSegment.class, true);
/*      */ 
/* 1648 */     SOFMarkerSegment localSOFMarkerSegment = (SOFMarkerSegment)findMarkerSegment(SOFMarkerSegment.class, true);
/*      */ 
/* 1650 */     SOSMarkerSegment localSOSMarkerSegment = (SOSMarkerSegment)findMarkerSegment(SOSMarkerSegment.class, true);
/*      */ 
/* 1659 */     if ((localSOFMarkerSegment != null) && (localSOFMarkerSegment.tag == 194) && 
/* 1660 */       (localSOFMarkerSegment.componentSpecs.length != i) && (localSOSMarkerSegment != null)) {
/* 1661 */       return;
/*      */     }
/*      */ 
/* 1666 */     if ((j == 0) && (localJFIFMarkerSegment != null)) {
/* 1667 */       this.markerSequence.remove(localJFIFMarkerSegment);
/*      */     }
/*      */ 
/* 1671 */     if ((j != 0) && (!this.isStream)) {
/* 1672 */       this.markerSequence.add(0, new JFIFMarkerSegment());
/*      */     }
/*      */ 
/* 1677 */     if (k != 0) {
/* 1678 */       if ((localObject1 == null) && (!this.isStream)) {
/* 1679 */         localObject1 = new AdobeMarkerSegment(m);
/* 1680 */         insertAdobeMarkerSegment((AdobeMarkerSegment)localObject1);
/*      */       } else {
/* 1682 */         ((AdobeMarkerSegment)localObject1).transform = m;
/*      */       }
/* 1684 */     } else if (localObject1 != null) {
/* 1685 */       this.markerSequence.remove(localObject1);
/*      */     }
/*      */ 
/* 1688 */     int i1 = 0;
/* 1689 */     int i2 = 0;
/*      */ 
/* 1691 */     boolean bool3 = false;
/*      */ 
/* 1693 */     int[] arrayOfInt1 = { 0, 1, 1, 0 };
/* 1694 */     int[] arrayOfInt2 = { 0, 0, 0, 0 };
/*      */ 
/* 1696 */     int[] arrayOfInt3 = bool1 ? arrayOfInt1 : arrayOfInt2;
/*      */ 
/* 1701 */     SOFMarkerSegment.ComponentSpec[] arrayOfComponentSpec = null;
/*      */     Iterator localIterator1;
/*      */     Object localObject2;
/*      */     Object localObject3;
/* 1703 */     if (localSOFMarkerSegment != null) {
/* 1704 */       arrayOfComponentSpec = localSOFMarkerSegment.componentSpecs;
/* 1705 */       bool3 = localSOFMarkerSegment.tag == 194;
/*      */ 
/* 1708 */       this.markerSequence.set(this.markerSequence.indexOf(localSOFMarkerSegment), new SOFMarkerSegment(bool3, false, bool1, arrayOfByte, i));
/*      */ 
/* 1720 */       for (int i3 = 0; i3 < arrayOfComponentSpec.length; i3++) {
/* 1721 */         if (arrayOfComponentSpec[i3].QtableSelector != arrayOfInt3[i3]) {
/* 1722 */           i1 = 1;
/*      */         }
/*      */       }
/*      */ 
/* 1726 */       if (bool3)
/*      */       {
/* 1729 */         i3 = 0;
/* 1730 */         for (int i4 = 0; i4 < arrayOfComponentSpec.length; i4++) {
/* 1731 */           if (arrayOfByte[i4] != arrayOfComponentSpec[i4].componentId) {
/* 1732 */             i3 = 1;
/*      */           }
/*      */         }
/* 1735 */         if (i3 != 0)
/*      */         {
/* 1737 */           for (localIterator1 = this.markerSequence.iterator(); localIterator1.hasNext(); ) {
/* 1738 */             localObject2 = (MarkerSegment)localIterator1.next();
/* 1739 */             if ((localObject2 instanceof SOSMarkerSegment)) {
/* 1740 */               localObject3 = (SOSMarkerSegment)localObject2;
/* 1741 */               for (int i7 = 0; i7 < ((SOSMarkerSegment)localObject3).componentSpecs.length; i7++) {
/* 1742 */                 int i8 = localObject3.componentSpecs[i7].componentSelector;
/*      */ 
/* 1750 */                 for (int i9 = 0; i9 < arrayOfComponentSpec.length; i9++) {
/* 1751 */                   if (arrayOfComponentSpec[i9].componentId == i8) {
/* 1752 */                     localObject3.componentSpecs[i7].componentSelector = arrayOfByte[i9];
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/* 1761 */       else if (localSOSMarkerSegment != null)
/*      */       {
/* 1764 */         for (i3 = 0; i3 < localSOSMarkerSegment.componentSpecs.length; i3++) {
/* 1765 */           if ((localSOSMarkerSegment.componentSpecs[i3].dcHuffTable != arrayOfInt3[i3]) || (localSOSMarkerSegment.componentSpecs[i3].acHuffTable != arrayOfInt3[i3]))
/*      */           {
/* 1769 */             i2 = 1;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1774 */         this.markerSequence.set(this.markerSequence.indexOf(localSOSMarkerSegment), new SOSMarkerSegment(bool1, arrayOfByte, i));
/*      */       }
/*      */ 
/*      */     }
/* 1782 */     else if (this.isStream)
/*      */     {
/* 1784 */       i1 = 1;
/* 1785 */       i2 = 1;
/*      */     }
/*      */     ArrayList localArrayList;
/*      */     Object localObject4;
/*      */     Object localObject5;
/* 1789 */     if (i1 != 0) {
/* 1790 */       localArrayList = new ArrayList();
/* 1791 */       for (localIterator1 = this.markerSequence.iterator(); localIterator1.hasNext(); ) {
/* 1792 */         localObject2 = (MarkerSegment)localIterator1.next();
/* 1793 */         if ((localObject2 instanceof DQTMarkerSegment)) {
/* 1794 */           localArrayList.add(localObject2);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1800 */       if ((!localArrayList.isEmpty()) && (bool1))
/*      */       {
/* 1807 */         int i5 = 0;
/* 1808 */         for (localObject2 = localArrayList.iterator(); ((Iterator)localObject2).hasNext(); ) {
/* 1809 */           localObject3 = (DQTMarkerSegment)((Iterator)localObject2).next();
/* 1810 */           localObject4 = ((DQTMarkerSegment)localObject3).tables.iterator();
/* 1811 */           while (((Iterator)localObject4).hasNext()) {
/* 1812 */             localObject5 = (DQTMarkerSegment.Qtable)((Iterator)localObject4).next();
/*      */ 
/* 1814 */             if (((DQTMarkerSegment.Qtable)localObject5).tableID == 1) {
/* 1815 */               i5 = 1;
/*      */             }
/*      */           }
/*      */         }
/* 1819 */         if (i5 == 0)
/*      */         {
/* 1821 */           localObject2 = null;
/* 1822 */           for (localObject3 = localArrayList.iterator(); ((Iterator)localObject3).hasNext(); ) {
/* 1823 */             localObject4 = (DQTMarkerSegment)((Iterator)localObject3).next();
/* 1824 */             localObject5 = ((DQTMarkerSegment)localObject4).tables.iterator();
/* 1825 */             while (((Iterator)localObject5).hasNext()) {
/* 1826 */               DQTMarkerSegment.Qtable localQtable = (DQTMarkerSegment.Qtable)((Iterator)localObject5).next();
/*      */ 
/* 1828 */               if (localQtable.tableID == 0) {
/* 1829 */                 localObject2 = localQtable;
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/* 1837 */           localObject3 = (DQTMarkerSegment)localArrayList.get(localArrayList.size() - 1);
/*      */ 
/* 1839 */           ((DQTMarkerSegment)localObject3).tables.add(((DQTMarkerSegment)localObject3).getChromaForLuma((DQTMarkerSegment.Qtable)localObject2));
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1844 */     if (i2 != 0) {
/* 1845 */       localArrayList = new ArrayList();
/* 1846 */       for (Iterator localIterator2 = this.markerSequence.iterator(); localIterator2.hasNext(); ) {
/* 1847 */         localObject2 = (MarkerSegment)localIterator2.next();
/* 1848 */         if ((localObject2 instanceof DHTMarkerSegment)) {
/* 1849 */           localArrayList.add(localObject2);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1855 */       if ((!localArrayList.isEmpty()) && (bool1))
/*      */       {
/* 1861 */         int i6 = 0;
/* 1862 */         for (localObject2 = localArrayList.iterator(); ((Iterator)localObject2).hasNext(); ) {
/* 1863 */           localObject3 = (DHTMarkerSegment)((Iterator)localObject2).next();
/* 1864 */           localObject4 = ((DHTMarkerSegment)localObject3).tables.iterator();
/* 1865 */           while (((Iterator)localObject4).hasNext()) {
/* 1866 */             localObject5 = (DHTMarkerSegment.Htable)((Iterator)localObject4).next();
/*      */ 
/* 1868 */             if (((DHTMarkerSegment.Htable)localObject5).tableID == 1) {
/* 1869 */               i6 = 1;
/*      */             }
/*      */           }
/*      */         }
/* 1873 */         if (i6 == 0)
/*      */         {
/* 1876 */           localObject2 = (DHTMarkerSegment)localArrayList.get(localArrayList.size() - 1);
/*      */ 
/* 1878 */           ((DHTMarkerSegment)localObject2).addHtable(JPEGHuffmanTable.StdDCLuminance, true, 1);
/* 1879 */           ((DHTMarkerSegment)localObject2).addHtable(JPEGHuffmanTable.StdACLuminance, true, 1);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean wantAlpha(Node paramNode) {
/* 1886 */     boolean bool = false;
/* 1887 */     Node localNode = paramNode.getFirstChild();
/* 1888 */     if ((localNode.getNodeName().equals("Alpha")) && 
/* 1889 */       (localNode.hasAttributes())) {
/* 1890 */       String str = localNode.getAttributes().getNamedItem("value").getNodeValue();
/*      */ 
/* 1892 */       if (!str.equals("none")) {
/* 1893 */         bool = true;
/*      */       }
/*      */     }
/*      */ 
/* 1897 */     this.transparencyDone = true;
/* 1898 */     return bool;
/*      */   }
/*      */ 
/*      */   private void mergeStandardCompressionNode(Node paramNode)
/*      */     throws IIOInvalidTreeException
/*      */   {
/*      */   }
/*      */ 
/*      */   private void mergeStandardDataNode(Node paramNode)
/*      */     throws IIOInvalidTreeException
/*      */   {
/*      */   }
/*      */ 
/*      */   private void mergeStandardDimensionNode(Node paramNode)
/*      */     throws IIOInvalidTreeException
/*      */   {
/* 1917 */     JFIFMarkerSegment localJFIFMarkerSegment = (JFIFMarkerSegment)findMarkerSegment(JFIFMarkerSegment.class, true);
/*      */     Object localObject;
/* 1919 */     if (localJFIFMarkerSegment == null)
/*      */     {
/* 1924 */       int i = 0;
/* 1925 */       SOFMarkerSegment localSOFMarkerSegment = (SOFMarkerSegment)findMarkerSegment(SOFMarkerSegment.class, true);
/*      */ 
/* 1927 */       if (localSOFMarkerSegment != null) {
/* 1928 */         int k = localSOFMarkerSegment.componentSpecs.length;
/* 1929 */         if ((k == 1) || (k == 3)) {
/* 1930 */           i = 1;
/* 1931 */           for (int m = 0; m < localSOFMarkerSegment.componentSpecs.length; m++) {
/* 1932 */             if (localSOFMarkerSegment.componentSpecs[m].componentId != m + 1) {
/* 1933 */               i = 0;
/*      */             }
/*      */           }
/*      */ 
/* 1937 */           localObject = (AdobeMarkerSegment)findMarkerSegment(AdobeMarkerSegment.class, true);
/*      */ 
/* 1940 */           if (localObject != null) {
/* 1941 */             if (((AdobeMarkerSegment)localObject).transform != (k == 1 ? 0 : 1))
/*      */             {
/* 1944 */               i = 0;
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1951 */       if (i != 0) {
/* 1952 */         localJFIFMarkerSegment = new JFIFMarkerSegment();
/* 1953 */         this.markerSequence.add(0, localJFIFMarkerSegment);
/*      */       }
/*      */     }
/* 1956 */     if (localJFIFMarkerSegment != null) {
/* 1957 */       NodeList localNodeList = paramNode.getChildNodes();
/* 1958 */       for (int j = 0; j < localNodeList.getLength(); j++) {
/* 1959 */         Node localNode = localNodeList.item(j);
/* 1960 */         localObject = localNode.getAttributes();
/* 1961 */         String str1 = localNode.getNodeName();
/*      */         String str2;
/*      */         float f;
/* 1962 */         if (str1.equals("PixelAspectRatio")) {
/* 1963 */           str2 = ((NamedNodeMap)localObject).getNamedItem("value").getNodeValue();
/* 1964 */           f = Float.parseFloat(str2);
/* 1965 */           Point localPoint = findIntegerRatio(f);
/* 1966 */           localJFIFMarkerSegment.resUnits = 0;
/* 1967 */           localJFIFMarkerSegment.Xdensity = localPoint.x;
/* 1968 */           localJFIFMarkerSegment.Xdensity = localPoint.y;
/*      */         }
/*      */         else
/*      */         {
/*      */           int n;
/* 1969 */           if (str1.equals("HorizontalPixelSize")) {
/* 1970 */             str2 = ((NamedNodeMap)localObject).getNamedItem("value").getNodeValue();
/* 1971 */             f = Float.parseFloat(str2);
/*      */ 
/* 1973 */             n = (int)Math.round(1.0D / (f * 10.0D));
/* 1974 */             localJFIFMarkerSegment.resUnits = 2;
/* 1975 */             localJFIFMarkerSegment.Xdensity = n;
/* 1976 */           } else if (str1.equals("VerticalPixelSize")) {
/* 1977 */             str2 = ((NamedNodeMap)localObject).getNamedItem("value").getNodeValue();
/* 1978 */             f = Float.parseFloat(str2);
/*      */ 
/* 1980 */             n = (int)Math.round(1.0D / (f * 10.0D));
/* 1981 */             localJFIFMarkerSegment.resUnits = 2;
/* 1982 */             localJFIFMarkerSegment.Ydensity = n;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static Point findIntegerRatio(float paramFloat)
/*      */   {
/* 1994 */     float f1 = 0.005F;
/*      */ 
/* 1997 */     paramFloat = Math.abs(paramFloat);
/*      */ 
/* 2000 */     if (paramFloat <= f1) {
/* 2001 */       return new Point(1, 255);
/*      */     }
/*      */ 
/* 2005 */     if (paramFloat >= 255.0F) {
/* 2006 */       return new Point(255, 1);
/*      */     }
/*      */ 
/* 2010 */     int i = 0;
/* 2011 */     if (paramFloat < 1.0D) {
/* 2012 */       paramFloat = 1.0F / paramFloat;
/* 2013 */       i = 1;
/*      */     }
/*      */ 
/* 2017 */     int j = 1;
/* 2018 */     int k = Math.round(paramFloat);
/*      */ 
/* 2020 */     float f2 = k;
/* 2021 */     float f3 = Math.abs(paramFloat - f2);
/* 2022 */     while (f3 > f1)
/*      */     {
/* 2024 */       j++;
/* 2025 */       k = Math.round(j * paramFloat);
/* 2026 */       f2 = k / j;
/* 2027 */       f3 = Math.abs(paramFloat - f2);
/*      */     }
/* 2029 */     return i != 0 ? new Point(j, k) : new Point(k, j);
/*      */   }
/*      */ 
/*      */   private void mergeStandardDocumentNode(Node paramNode)
/*      */     throws IIOInvalidTreeException
/*      */   {
/*      */   }
/*      */ 
/*      */   private void mergeStandardTextNode(Node paramNode)
/*      */     throws IIOInvalidTreeException
/*      */   {
/* 2042 */     NodeList localNodeList = paramNode.getChildNodes();
/* 2043 */     for (int i = 0; i < localNodeList.getLength(); i++) {
/* 2044 */       Node localNode1 = localNodeList.item(i);
/* 2045 */       NamedNodeMap localNamedNodeMap = localNode1.getAttributes();
/* 2046 */       Node localNode2 = localNamedNodeMap.getNamedItem("compression");
/* 2047 */       int j = 1;
/*      */       String str;
/* 2048 */       if (localNode2 != null) {
/* 2049 */         str = localNode2.getNodeValue();
/* 2050 */         if (!str.equals("none")) {
/* 2051 */           j = 0;
/*      */         }
/*      */       }
/* 2054 */       if (j != 0) {
/* 2055 */         str = localNamedNodeMap.getNamedItem("value").getNodeValue();
/* 2056 */         COMMarkerSegment localCOMMarkerSegment = new COMMarkerSegment(str);
/* 2057 */         insertCOMMarkerSegment(localCOMMarkerSegment);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void mergeStandardTransparencyNode(Node paramNode)
/*      */     throws IIOInvalidTreeException
/*      */   {
/* 2068 */     if ((!this.transparencyDone) && (!this.isStream)) {
/* 2069 */       boolean bool1 = wantAlpha(paramNode);
/*      */ 
/* 2073 */       JFIFMarkerSegment localJFIFMarkerSegment = (JFIFMarkerSegment)findMarkerSegment(JFIFMarkerSegment.class, true);
/*      */ 
/* 2075 */       AdobeMarkerSegment localAdobeMarkerSegment = (AdobeMarkerSegment)findMarkerSegment(AdobeMarkerSegment.class, true);
/*      */ 
/* 2077 */       SOFMarkerSegment localSOFMarkerSegment = (SOFMarkerSegment)findMarkerSegment(SOFMarkerSegment.class, true);
/*      */ 
/* 2079 */       SOSMarkerSegment localSOSMarkerSegment = (SOSMarkerSegment)findMarkerSegment(SOSMarkerSegment.class, true);
/*      */ 
/* 2084 */       if ((localSOFMarkerSegment != null) && (localSOFMarkerSegment.tag == 194)) {
/* 2085 */         return;
/*      */       }
/*      */ 
/* 2090 */       if (localSOFMarkerSegment != null) {
/* 2091 */         int i = localSOFMarkerSegment.componentSpecs.length;
/* 2092 */         boolean bool2 = (i == 2) || (i == 4);
/*      */ 
/* 2094 */         if (bool2 != bool1)
/*      */         {
/*      */           SOFMarkerSegment.ComponentSpec[] arrayOfComponentSpec;
/*      */           int j;
/*      */           int k;
/* 2095 */           if (bool1) {
/* 2096 */             i++;
/* 2097 */             if (localJFIFMarkerSegment != null) {
/* 2098 */               this.markerSequence.remove(localJFIFMarkerSegment);
/*      */             }
/*      */ 
/* 2102 */             if (localAdobeMarkerSegment != null) {
/* 2103 */               localAdobeMarkerSegment.transform = 0;
/*      */             }
/*      */ 
/* 2107 */             arrayOfComponentSpec = new SOFMarkerSegment.ComponentSpec[i];
/*      */ 
/* 2109 */             for (j = 0; j < localSOFMarkerSegment.componentSpecs.length; j++) {
/* 2110 */               arrayOfComponentSpec[j] = localSOFMarkerSegment.componentSpecs[j];
/*      */             }
/* 2112 */             j = (byte)localSOFMarkerSegment.componentSpecs[0].componentId;
/* 2113 */             k = (byte)(j > 1 ? 65 : 4);
/* 2114 */             arrayOfComponentSpec[(i - 1)] = localSOFMarkerSegment.getComponentSpec(k, localSOFMarkerSegment.componentSpecs[0].HsamplingFactor, localSOFMarkerSegment.componentSpecs[0].QtableSelector);
/*      */ 
/* 2119 */             localSOFMarkerSegment.componentSpecs = arrayOfComponentSpec;
/*      */ 
/* 2122 */             SOSMarkerSegment.ScanComponentSpec[] arrayOfScanComponentSpec2 = new SOSMarkerSegment.ScanComponentSpec[i];
/*      */ 
/* 2124 */             for (int m = 0; m < localSOSMarkerSegment.componentSpecs.length; m++) {
/* 2125 */               arrayOfScanComponentSpec2[m] = localSOSMarkerSegment.componentSpecs[m];
/*      */             }
/* 2127 */             arrayOfScanComponentSpec2[(i - 1)] = localSOSMarkerSegment.getScanComponentSpec(k, 0);
/*      */ 
/* 2129 */             localSOSMarkerSegment.componentSpecs = arrayOfScanComponentSpec2;
/*      */           } else {
/* 2131 */             i--;
/*      */ 
/* 2133 */             arrayOfComponentSpec = new SOFMarkerSegment.ComponentSpec[i];
/*      */ 
/* 2135 */             for (j = 0; j < i; j++) {
/* 2136 */               arrayOfComponentSpec[j] = localSOFMarkerSegment.componentSpecs[j];
/*      */             }
/* 2138 */             localSOFMarkerSegment.componentSpecs = arrayOfComponentSpec;
/*      */ 
/* 2141 */             SOSMarkerSegment.ScanComponentSpec[] arrayOfScanComponentSpec1 = new SOSMarkerSegment.ScanComponentSpec[i];
/*      */ 
/* 2143 */             for (k = 0; k < i; k++) {
/* 2144 */               arrayOfScanComponentSpec1[k] = localSOSMarkerSegment.componentSpecs[k];
/*      */             }
/* 2146 */             localSOSMarkerSegment.componentSpecs = arrayOfScanComponentSpec1;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setFromTree(String paramString, Node paramNode)
/*      */     throws IIOInvalidTreeException
/*      */   {
/* 2156 */     if (paramString == null) {
/* 2157 */       throw new IllegalArgumentException("null formatName!");
/*      */     }
/* 2159 */     if (paramNode == null) {
/* 2160 */       throw new IllegalArgumentException("null root!");
/*      */     }
/* 2162 */     if ((this.isStream) && (paramString.equals("javax_imageio_jpeg_stream_1.0")))
/*      */     {
/* 2164 */       setFromNativeTree(paramNode);
/* 2165 */     } else if ((!this.isStream) && (paramString.equals("javax_imageio_jpeg_image_1.0")))
/*      */     {
/* 2167 */       setFromNativeTree(paramNode);
/* 2168 */     } else if ((!this.isStream) && (paramString.equals("javax_imageio_1.0")))
/*      */     {
/* 2172 */       super.setFromTree(paramString, paramNode);
/*      */     }
/* 2174 */     else throw new IllegalArgumentException("Unsupported format name: " + paramString);
/*      */   }
/*      */ 
/*      */   private void setFromNativeTree(Node paramNode)
/*      */     throws IIOInvalidTreeException
/*      */   {
/* 2180 */     if (this.resetSequence == null) {
/* 2181 */       this.resetSequence = this.markerSequence;
/*      */     }
/* 2183 */     this.markerSequence = new ArrayList();
/*      */ 
/* 2187 */     String str = paramNode.getNodeName();
/* 2188 */     if (str != (this.isStream ? "javax_imageio_jpeg_stream_1.0" : "javax_imageio_jpeg_image_1.0"))
/*      */     {
/* 2190 */       throw new IIOInvalidTreeException("Invalid root node name: " + str, paramNode);
/*      */     }
/*      */ 
/* 2193 */     if (!this.isStream) {
/* 2194 */       if (paramNode.getChildNodes().getLength() != 2) {
/* 2195 */         throw new IIOInvalidTreeException("JPEGvariety and markerSequence nodes must be present", paramNode);
/*      */       }
/*      */ 
/* 2199 */       localNode = paramNode.getFirstChild();
/*      */ 
/* 2201 */       if (localNode.getChildNodes().getLength() != 0) {
/* 2202 */         this.markerSequence.add(new JFIFMarkerSegment(localNode.getFirstChild()));
/*      */       }
/*      */     }
/*      */ 
/* 2206 */     Node localNode = this.isStream ? paramNode : paramNode.getLastChild();
/* 2207 */     setFromMarkerSequenceNode(localNode);
/*      */   }
/*      */ 
/*      */   void setFromMarkerSequenceNode(Node paramNode)
/*      */     throws IIOInvalidTreeException
/*      */   {
/* 2214 */     NodeList localNodeList = paramNode.getChildNodes();
/*      */ 
/* 2216 */     for (int i = 0; i < localNodeList.getLength(); i++) {
/* 2217 */       Node localNode = localNodeList.item(i);
/* 2218 */       String str = localNode.getNodeName();
/* 2219 */       if (str.equals("dqt"))
/* 2220 */         this.markerSequence.add(new DQTMarkerSegment(localNode));
/* 2221 */       else if (str.equals("dht"))
/* 2222 */         this.markerSequence.add(new DHTMarkerSegment(localNode));
/* 2223 */       else if (str.equals("dri"))
/* 2224 */         this.markerSequence.add(new DRIMarkerSegment(localNode));
/* 2225 */       else if (str.equals("com"))
/* 2226 */         this.markerSequence.add(new COMMarkerSegment(localNode));
/* 2227 */       else if (str.equals("app14Adobe"))
/* 2228 */         this.markerSequence.add(new AdobeMarkerSegment(localNode));
/* 2229 */       else if (str.equals("unknown"))
/* 2230 */         this.markerSequence.add(new MarkerSegment(localNode));
/* 2231 */       else if (str.equals("sof"))
/* 2232 */         this.markerSequence.add(new SOFMarkerSegment(localNode));
/* 2233 */       else if (str.equals("sos"))
/* 2234 */         this.markerSequence.add(new SOSMarkerSegment(localNode));
/*      */       else
/* 2236 */         throw new IIOInvalidTreeException("Invalid " + (this.isStream ? "stream " : "image ") + "child: " + str, localNode);
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean isConsistent()
/*      */   {
/* 2251 */     SOFMarkerSegment localSOFMarkerSegment = (SOFMarkerSegment)findMarkerSegment(SOFMarkerSegment.class, true);
/*      */ 
/* 2254 */     JFIFMarkerSegment localJFIFMarkerSegment = (JFIFMarkerSegment)findMarkerSegment(JFIFMarkerSegment.class, true);
/*      */ 
/* 2257 */     AdobeMarkerSegment localAdobeMarkerSegment = (AdobeMarkerSegment)findMarkerSegment(AdobeMarkerSegment.class, true);
/*      */ 
/* 2260 */     boolean bool = true;
/* 2261 */     if (!this.isStream) {
/* 2262 */       if (localSOFMarkerSegment != null)
/*      */       {
/* 2264 */         int i = localSOFMarkerSegment.componentSpecs.length;
/* 2265 */         int j = countScanBands();
/* 2266 */         if ((j != 0) && 
/* 2267 */           (j != i)) {
/* 2268 */           bool = false;
/*      */         }
/*      */ 
/* 2272 */         if (localJFIFMarkerSegment != null) {
/* 2273 */           if ((i != 1) && (i != 3)) {
/* 2274 */             bool = false;
/*      */           }
/* 2276 */           for (int k = 0; k < i; k++) {
/* 2277 */             if (localSOFMarkerSegment.componentSpecs[k].componentId != k + 1) {
/* 2278 */               bool = false;
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/* 2285 */           if ((localAdobeMarkerSegment != null) && (((i == 1) && (localAdobeMarkerSegment.transform != 0)) || ((i == 3) && (localAdobeMarkerSegment.transform != 1))))
/*      */           {
/* 2290 */             bool = false;
/*      */           }
/*      */         }
/*      */       }
/*      */       else {
/* 2295 */         SOSMarkerSegment localSOSMarkerSegment = (SOSMarkerSegment)findMarkerSegment(SOSMarkerSegment.class, true);
/*      */ 
/* 2298 */         if ((localJFIFMarkerSegment != null) || (localAdobeMarkerSegment != null) || (localSOFMarkerSegment != null) || (localSOSMarkerSegment != null))
/*      */         {
/* 2300 */           bool = false;
/*      */         }
/*      */       }
/*      */     }
/* 2304 */     return bool;
/*      */   }
/*      */ 
/*      */   private int countScanBands()
/*      */   {
/* 2312 */     ArrayList localArrayList = new ArrayList();
/* 2313 */     Iterator localIterator = this.markerSequence.iterator();
/* 2314 */     while (localIterator.hasNext()) {
/* 2315 */       MarkerSegment localMarkerSegment = (MarkerSegment)localIterator.next();
/* 2316 */       if ((localMarkerSegment instanceof SOSMarkerSegment)) {
/* 2317 */         SOSMarkerSegment localSOSMarkerSegment = (SOSMarkerSegment)localMarkerSegment;
/* 2318 */         SOSMarkerSegment.ScanComponentSpec[] arrayOfScanComponentSpec = localSOSMarkerSegment.componentSpecs;
/* 2319 */         for (int i = 0; i < arrayOfScanComponentSpec.length; i++) {
/* 2320 */           Integer localInteger = new Integer(arrayOfScanComponentSpec[i].componentSelector);
/* 2321 */           if (!localArrayList.contains(localInteger)) {
/* 2322 */             localArrayList.add(localInteger);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 2328 */     return localArrayList.size();
/*      */   }
/*      */ 
/*      */   void writeToStream(ImageOutputStream paramImageOutputStream, boolean paramBoolean1, boolean paramBoolean2, List paramList, ICC_Profile paramICC_Profile, boolean paramBoolean3, int paramInt, JPEGImageWriter paramJPEGImageWriter)
/*      */     throws IOException
/*      */   {
/* 2342 */     if (paramBoolean2)
/*      */     {
/* 2346 */       JFIFMarkerSegment.writeDefaultJFIF(paramImageOutputStream, paramList, paramICC_Profile, paramJPEGImageWriter);
/*      */ 
/* 2350 */       if ((!paramBoolean3) && (paramInt != -1))
/*      */       {
/* 2352 */         if ((paramInt != 0) && (paramInt != 1))
/*      */         {
/* 2355 */           paramBoolean3 = true;
/* 2356 */           paramJPEGImageWriter.warningOccurred(13);
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2362 */     Iterator localIterator = this.markerSequence.iterator();
/* 2363 */     while (localIterator.hasNext()) {
/* 2364 */       MarkerSegment localMarkerSegment = (MarkerSegment)localIterator.next();
/*      */       Object localObject;
/* 2365 */       if ((localMarkerSegment instanceof JFIFMarkerSegment)) {
/* 2366 */         if (!paramBoolean1) {
/* 2367 */           localObject = (JFIFMarkerSegment)localMarkerSegment;
/* 2368 */           ((JFIFMarkerSegment)localObject).writeWithThumbs(paramImageOutputStream, paramList, paramJPEGImageWriter);
/* 2369 */           if (paramICC_Profile != null)
/* 2370 */             JFIFMarkerSegment.writeICC(paramICC_Profile, paramImageOutputStream);
/*      */         }
/*      */       }
/* 2373 */       else if ((localMarkerSegment instanceof AdobeMarkerSegment)) {
/* 2374 */         if (!paramBoolean3)
/* 2375 */           if (paramInt != -1) {
/* 2376 */             localObject = (AdobeMarkerSegment)localMarkerSegment.clone();
/*      */ 
/* 2378 */             ((AdobeMarkerSegment)localObject).transform = paramInt;
/* 2379 */             ((AdobeMarkerSegment)localObject).write(paramImageOutputStream);
/* 2380 */           } else if (paramBoolean2)
/*      */           {
/* 2382 */             localObject = (AdobeMarkerSegment)localMarkerSegment;
/* 2383 */             if ((((AdobeMarkerSegment)localObject).transform == 0) || (((AdobeMarkerSegment)localObject).transform == 1))
/*      */             {
/* 2385 */               ((AdobeMarkerSegment)localObject).write(paramImageOutputStream);
/*      */             }
/* 2387 */             else paramJPEGImageWriter.warningOccurred(13);
/*      */           }
/*      */           else
/*      */           {
/* 2391 */             localMarkerSegment.write(paramImageOutputStream);
/*      */           }
/*      */       }
/*      */       else
/* 2395 */         localMarkerSegment.write(paramImageOutputStream);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void reset()
/*      */   {
/* 2403 */     if (this.resetSequence != null) {
/* 2404 */       this.markerSequence = this.resetSequence;
/* 2405 */       this.resetSequence = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void print() {
/* 2410 */     for (int i = 0; i < this.markerSequence.size(); i++) {
/* 2411 */       MarkerSegment localMarkerSegment = (MarkerSegment)this.markerSequence.get(i);
/* 2412 */       localMarkerSegment.print();
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.imageio.plugins.jpeg.JPEGMetadata
 * JD-Core Version:    0.6.2
 */