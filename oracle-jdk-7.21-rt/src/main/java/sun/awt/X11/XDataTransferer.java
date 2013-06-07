/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.Image;
/*     */ import java.awt.datatransfer.DataFlavor;
/*     */ import java.awt.datatransfer.Transferable;
/*     */ import java.awt.datatransfer.UnsupportedFlavorException;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.awt.image.WritableRaster;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import javax.imageio.ImageIO;
/*     */ import javax.imageio.ImageTypeSpecifier;
/*     */ import javax.imageio.ImageWriter;
/*     */ import javax.imageio.spi.ImageWriterSpi;
/*     */ import sun.awt.datatransfer.DataTransferer;
/*     */ import sun.awt.datatransfer.ToolkitThreadBlockedHandler;
/*     */ 
/*     */ public class XDataTransferer extends DataTransferer
/*     */ {
/*  64 */   static final XAtom FILE_NAME_ATOM = XAtom.get("FILE_NAME");
/*  65 */   static final XAtom DT_NET_FILE_ATOM = XAtom.get("_DT_NETFILE");
/*  66 */   static final XAtom PNG_ATOM = XAtom.get("PNG");
/*  67 */   static final XAtom JFIF_ATOM = XAtom.get("JFIF");
/*  68 */   static final XAtom TARGETS_ATOM = XAtom.get("TARGETS");
/*  69 */   static final XAtom INCR_ATOM = XAtom.get("INCR");
/*  70 */   static final XAtom MULTIPLE_ATOM = XAtom.get("MULTIPLE");
/*     */   private static XDataTransferer transferer;
/* 370 */   private static ImageTypeSpecifier defaultSpecifier = null;
/*     */ 
/*     */   static XDataTransferer getInstanceImpl()
/*     */   {
/*  81 */     synchronized (XDataTransferer.class) {
/*  82 */       if (transferer == null) {
/*  83 */         transferer = new XDataTransferer();
/*     */       }
/*     */     }
/*  86 */     return transferer;
/*     */   }
/*     */ 
/*     */   public String getDefaultUnicodeEncoding() {
/*  90 */     return "iso-10646-ucs-2";
/*     */   }
/*     */ 
/*     */   public boolean isLocaleDependentTextFormat(long paramLong) {
/*  94 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isTextFormat(long paramLong) {
/*  98 */     return (super.isTextFormat(paramLong)) || (isMimeFormat(paramLong, "text"));
/*     */   }
/*     */ 
/*     */   protected String getCharsetForTextFormat(Long paramLong)
/*     */   {
/* 103 */     long l = paramLong.longValue();
/* 104 */     if (isMimeFormat(l, "text")) {
/* 105 */       String str1 = getNativeForFormat(l);
/* 106 */       DataFlavor localDataFlavor = new DataFlavor(str1, null);
/*     */ 
/* 109 */       if (!DataTransferer.doesSubtypeSupportCharset(localDataFlavor)) {
/* 110 */         return null;
/*     */       }
/* 112 */       String str2 = localDataFlavor.getParameter("charset");
/* 113 */       if (str2 != null) {
/* 114 */         return str2;
/*     */       }
/*     */     }
/* 117 */     return super.getCharsetForTextFormat(paramLong);
/*     */   }
/*     */ 
/*     */   protected boolean isURIListFormat(long paramLong) {
/* 121 */     String str = getNativeForFormat(paramLong);
/* 122 */     if (str == null)
/* 123 */       return false;
/*     */     try
/*     */     {
/* 126 */       DataFlavor localDataFlavor = new DataFlavor(str);
/* 127 */       if ((localDataFlavor.getPrimaryType().equals("text")) && (localDataFlavor.getSubType().equals("uri-list")))
/* 128 */         return true;
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/*     */     }
/* 133 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isFileFormat(long paramLong) {
/* 137 */     return (paramLong == FILE_NAME_ATOM.getAtom()) || (paramLong == DT_NET_FILE_ATOM.getAtom());
/*     */   }
/*     */ 
/*     */   public boolean isImageFormat(long paramLong)
/*     */   {
/* 142 */     return (paramLong == PNG_ATOM.getAtom()) || (paramLong == JFIF_ATOM.getAtom()) || (isMimeFormat(paramLong, "image"));
/*     */   }
/*     */ 
/*     */   protected Long getFormatForNativeAsLong(String paramString)
/*     */   {
/* 150 */     long l = XAtom.get(paramString).getAtom();
/* 151 */     return Long.valueOf(l);
/*     */   }
/*     */ 
/*     */   protected String getNativeForFormat(long paramLong) {
/* 155 */     return getTargetNameForAtom(paramLong);
/*     */   }
/*     */ 
/*     */   public ToolkitThreadBlockedHandler getToolkitThreadBlockedHandler() {
/* 159 */     return XToolkitThreadBlockedHandler.getToolkitThreadBlockedHandler();
/*     */   }
/*     */ 
/*     */   private String getTargetNameForAtom(long paramLong)
/*     */   {
/* 166 */     return XAtom.get(paramLong).getName();
/*     */   }
/*     */ 
/*     */   protected byte[] imageToPlatformBytes(Image paramImage, long paramLong) throws IOException
/*     */   {
/* 171 */     String str1 = null;
/* 172 */     if (paramLong == PNG_ATOM.getAtom())
/* 173 */       str1 = "image/png";
/* 174 */     else if (paramLong == JFIF_ATOM.getAtom())
/* 175 */       str1 = "image/jpeg";
/*     */     else {
/*     */       try
/*     */       {
/* 179 */         String str2 = getNativeForFormat(paramLong);
/* 180 */         DataFlavor localDataFlavor = new DataFlavor(str2);
/* 181 */         String str4 = localDataFlavor.getPrimaryType();
/* 182 */         if ("image".equals(str4))
/* 183 */           str1 = localDataFlavor.getPrimaryType() + "/" + localDataFlavor.getSubType();
/*     */       }
/*     */       catch (Exception localException)
/*     */       {
/*     */       }
/*     */     }
/* 189 */     if (str1 != null) {
/* 190 */       return imageToStandardBytes(paramImage, str1);
/*     */     }
/* 192 */     String str3 = getNativeForFormat(paramLong);
/* 193 */     throw new IOException("Translation to " + str3 + " is not supported.");
/*     */   }
/*     */ 
/*     */   protected ByteArrayOutputStream convertFileListToBytes(ArrayList<String> paramArrayList)
/*     */     throws IOException
/*     */   {
/* 201 */     ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
/* 202 */     for (int i = 0; i < paramArrayList.size(); i++)
/*     */     {
/* 204 */       byte[] arrayOfByte = ((String)paramArrayList.get(i)).getBytes();
/* 205 */       if (i != 0) localByteArrayOutputStream.write(0);
/* 206 */       localByteArrayOutputStream.write(arrayOfByte, 0, arrayOfByte.length);
/*     */     }
/* 208 */     return localByteArrayOutputStream;
/*     */   }
/*     */ 
/*     */   protected Image platformImageBytesOrStreamToImage(InputStream paramInputStream, byte[] paramArrayOfByte, long paramLong)
/*     */     throws IOException
/*     */   {
/* 219 */     String str1 = null;
/* 220 */     if (paramLong == PNG_ATOM.getAtom())
/* 221 */       str1 = "image/png";
/* 222 */     else if (paramLong == JFIF_ATOM.getAtom())
/* 223 */       str1 = "image/jpeg";
/*     */     else {
/*     */       try
/*     */       {
/* 227 */         String str2 = getNativeForFormat(paramLong);
/* 228 */         DataFlavor localDataFlavor = new DataFlavor(str2);
/* 229 */         String str4 = localDataFlavor.getPrimaryType();
/* 230 */         if ("image".equals(str4))
/* 231 */           str1 = localDataFlavor.getPrimaryType() + "/" + localDataFlavor.getSubType();
/*     */       }
/*     */       catch (Exception localException)
/*     */       {
/*     */       }
/*     */     }
/* 237 */     if (str1 != null) {
/* 238 */       return standardImageBytesOrStreamToImage(paramInputStream, paramArrayOfByte, str1);
/*     */     }
/* 240 */     String str3 = getNativeForFormat(paramLong);
/* 241 */     throw new IOException("Translation from " + str3 + " is not supported.");
/*     */   }
/*     */ 
/*     */   protected String[] dragQueryFile(byte[] paramArrayOfByte)
/*     */   {
/* 247 */     XToolkit.awtLock();
/*     */     try {
/* 249 */       return XlibWrapper.XTextPropertyToStringList(paramArrayOfByte, XAtom.get("STRING").getAtom());
/*     */     }
/*     */     finally {
/* 252 */       XToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected URI[] dragQueryURIs(InputStream paramInputStream, byte[] paramArrayOfByte, long paramLong, Transferable paramTransferable)
/*     */     throws IOException
/*     */   {
/* 262 */     String str1 = null;
/* 263 */     if ((paramTransferable != null) && (isLocaleDependentTextFormat(paramLong)) && (paramTransferable.isDataFlavorSupported(javaTextEncodingFlavor)))
/*     */     {
/*     */       try
/*     */       {
/* 267 */         str1 = new String((byte[])paramTransferable.getTransferData(javaTextEncodingFlavor), "UTF-8");
/*     */       }
/*     */       catch (UnsupportedFlavorException localUnsupportedFlavorException)
/*     */       {
/*     */       }
/*     */     }
/*     */     else {
/* 274 */       str1 = getCharsetForTextFormat(Long.valueOf(paramLong));
/*     */     }
/* 276 */     if (str1 == null)
/*     */     {
/* 278 */       str1 = getDefaultTextCharset();
/*     */     }
/*     */ 
/* 281 */     BufferedReader localBufferedReader = null;
/*     */     try {
/* 283 */       localBufferedReader = new BufferedReader(new InputStreamReader(paramInputStream, str1));
/*     */ 
/* 285 */       ArrayList localArrayList = new ArrayList();
/*     */       String str2;
/* 287 */       while ((str2 = localBufferedReader.readLine()) != null) {
/*     */         URI localURI;
/*     */         try { localURI = new URI(str2);
/*     */         } catch (URISyntaxException localURISyntaxException) {
/* 291 */           throw new IOException(localURISyntaxException);
/*     */         }
/* 293 */         localArrayList.add(localURI);
/*     */       }
/* 295 */       return (URI[])localArrayList.toArray(new URI[localArrayList.size()]);
/*     */     } finally {
/* 297 */       if (localBufferedReader != null)
/* 298 */         localBufferedReader.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean isMimeFormat(long paramLong, String paramString)
/*     */   {
/* 307 */     String str = getNativeForFormat(paramLong);
/*     */ 
/* 309 */     if (str == null) {
/* 310 */       return false;
/*     */     }
/*     */     try
/*     */     {
/* 314 */       DataFlavor localDataFlavor = new DataFlavor(str);
/* 315 */       if (paramString.equals(localDataFlavor.getPrimaryType())) {
/* 316 */         return true;
/*     */       }
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/*     */     }
/* 322 */     return false;
/*     */   }
/*     */ 
/*     */   public List getPlatformMappingsForNative(String paramString)
/*     */   {
/* 334 */     ArrayList localArrayList = new ArrayList();
/*     */ 
/* 336 */     if (paramString == null) {
/* 337 */       return localArrayList;
/*     */     }
/*     */ 
/* 340 */     DataFlavor localDataFlavor = null;
/*     */     try
/*     */     {
/* 343 */       localDataFlavor = new DataFlavor(paramString);
/*     */     }
/*     */     catch (Exception localException) {
/* 346 */       return localArrayList;
/*     */     }
/*     */ 
/* 349 */     Object localObject = localDataFlavor;
/* 350 */     String str1 = localDataFlavor.getPrimaryType();
/* 351 */     String str2 = str1 + "/" + localDataFlavor.getSubType();
/*     */ 
/* 356 */     if ("text".equals(str1)) {
/* 357 */       localObject = str1 + "/" + localDataFlavor.getSubType();
/* 358 */     } else if ("image".equals(str1)) {
/* 359 */       Iterator localIterator = ImageIO.getImageReadersByMIMEType(str2);
/* 360 */       if (localIterator.hasNext()) {
/* 361 */         localArrayList.add(DataFlavor.imageFlavor);
/*     */       }
/*     */     }
/*     */ 
/* 365 */     localArrayList.add(localObject);
/*     */ 
/* 367 */     return localArrayList;
/*     */   }
/*     */ 
/*     */   private ImageTypeSpecifier getDefaultImageTypeSpecifier()
/*     */   {
/* 373 */     if (defaultSpecifier == null) {
/* 374 */       ColorModel localColorModel = ColorModel.getRGBdefault();
/* 375 */       WritableRaster localWritableRaster = localColorModel.createCompatibleWritableRaster(10, 10);
/*     */ 
/* 378 */       BufferedImage localBufferedImage = new BufferedImage(localColorModel, localWritableRaster, localColorModel.isAlphaPremultiplied(), null);
/*     */ 
/* 382 */       defaultSpecifier = new ImageTypeSpecifier(localBufferedImage);
/*     */     }
/*     */ 
/* 385 */     return defaultSpecifier;
/*     */   }
/*     */ 
/*     */   public List getPlatformMappingsForFlavor(DataFlavor paramDataFlavor)
/*     */   {
/* 397 */     ArrayList localArrayList = new ArrayList(1);
/*     */ 
/* 399 */     if (paramDataFlavor == null) {
/* 400 */       return localArrayList;
/*     */     }
/*     */ 
/* 403 */     String str1 = paramDataFlavor.getParameter("charset");
/* 404 */     String str2 = paramDataFlavor.getPrimaryType() + "/" + paramDataFlavor.getSubType();
/* 405 */     String str3 = str2;
/*     */ 
/* 407 */     if ((str1 != null) && (DataTransferer.isFlavorCharsetTextType(paramDataFlavor))) {
/* 408 */       str3 = str3 + ";charset=" + str1;
/*     */     }
/*     */ 
/* 413 */     if ((paramDataFlavor.getRepresentationClass() != null) && ((paramDataFlavor.isRepresentationClassInputStream()) || (paramDataFlavor.isRepresentationClassByteBuffer()) || (byteArrayClass.equals(paramDataFlavor.getRepresentationClass()))))
/*     */     {
/* 417 */       localArrayList.add(str3);
/*     */     }
/*     */     Object localObject;
/* 420 */     if (DataFlavor.imageFlavor.equals(paramDataFlavor)) {
/* 421 */       localObject = ImageIO.getWriterMIMETypes();
/* 422 */       if (localObject != null)
/* 423 */         for (int i = 0; i < localObject.length; i++) {
/* 424 */           Iterator localIterator = ImageIO.getImageWritersByMIMEType(localObject[i]);
/*     */ 
/* 427 */           while (localIterator.hasNext()) {
/* 428 */             ImageWriter localImageWriter = (ImageWriter)localIterator.next();
/* 429 */             ImageWriterSpi localImageWriterSpi = localImageWriter.getOriginatingProvider();
/*     */ 
/* 432 */             if ((localImageWriterSpi != null) && (localImageWriterSpi.canEncodeImage(getDefaultImageTypeSpecifier())))
/*     */             {
/* 434 */               localArrayList.add(localObject[i]);
/* 435 */               break;
/*     */             }
/*     */           }
/*     */         }
/*     */     }
/* 440 */     else if (DataTransferer.isFlavorCharsetTextType(paramDataFlavor)) {
/* 441 */       localObject = DataTransferer.standardEncodings();
/*     */ 
/* 445 */       if (DataFlavor.stringFlavor.equals(paramDataFlavor)) {
/* 446 */         str2 = "text/plain";
/*     */       }
/*     */ 
/* 449 */       while (((Iterator)localObject).hasNext()) {
/* 450 */         String str4 = (String)((Iterator)localObject).next();
/* 451 */         if (!str4.equals(str1)) {
/* 452 */           localArrayList.add(str2 + ";charset=" + str4);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 457 */       if (!localArrayList.contains(str2)) {
/* 458 */         localArrayList.add(str2);
/*     */       }
/*     */     }
/*     */ 
/* 462 */     return localArrayList;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XDataTransferer
 * JD-Core Version:    0.6.2
 */