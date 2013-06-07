/*     */ package sun.print;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.URL;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import javax.print.attribute.standard.MediaPrintableArea;
/*     */ import javax.print.attribute.standard.MediaSize;
/*     */ import javax.print.attribute.standard.MediaSizeName;
/*     */ import javax.print.attribute.standard.MediaTray;
/*     */ import sun.security.action.LoadLibraryAction;
/*     */ 
/*     */ public class CUPSPrinter
/*     */ {
/*     */   private static final String debugPrefix = "CUPSPrinter>> ";
/*     */   private static final double PRINTER_DPI = 72.0D;
/*     */   private boolean initialized;
/*     */   private MediaPrintableArea[] cupsMediaPrintables;
/*     */   private MediaSizeName[] cupsMediaSNames;
/*     */   private CustomMediaSizeName[] cupsCustomMediaSNames;
/*     */   private MediaTray[] cupsMediaTrays;
/*  67 */   public int nPageSizes = 0;
/*  68 */   public int nTrays = 0;
/*     */   private String[] media;
/*     */   private float[] pageSizes;
/*     */   private String printer;
/*     */   private static boolean libFound;
/*     */   private static String cupsServer;
/*     */   private static int cupsPort;
/*     */ 
/*     */   private static native String getCupsServer();
/*     */ 
/*     */   private static native int getCupsPort();
/*     */ 
/*     */   private static native boolean canConnect(String paramString, int paramInt);
/*     */ 
/*     */   private static native boolean initIDs();
/*     */ 
/*     */   private static synchronized native String[] getMedia(String paramString);
/*     */ 
/*     */   private static synchronized native float[] getPageSizes(String paramString);
/*     */ 
/*     */   CUPSPrinter(String paramString)
/*     */   {
/*  90 */     if (paramString == null) {
/*  91 */       throw new IllegalArgumentException("null printer name");
/*     */     }
/*  93 */     this.printer = paramString;
/*  94 */     this.cupsMediaSNames = null;
/*  95 */     this.cupsMediaPrintables = null;
/*  96 */     this.cupsMediaTrays = null;
/*  97 */     this.initialized = false;
/*     */ 
/*  99 */     if (!libFound) {
/* 100 */       throw new RuntimeException("cups lib not found");
/*     */     }
/*     */ 
/* 103 */     this.media = getMedia(this.printer);
/* 104 */     if (this.media == null)
/*     */     {
/* 106 */       throw new RuntimeException("error getting PPD");
/*     */     }
/*     */ 
/* 110 */     this.pageSizes = getPageSizes(this.printer);
/* 111 */     if (this.pageSizes != null) {
/* 112 */       this.nPageSizes = (this.pageSizes.length / 6);
/*     */ 
/* 114 */       this.nTrays = (this.media.length / 2 - this.nPageSizes);
/* 115 */       assert (this.nTrays >= 0);
/*     */     }
/*     */   }
/*     */ 
/*     */   public MediaSizeName[] getMediaSizeNames()
/*     */   {
/* 125 */     initMedia();
/* 126 */     return this.cupsMediaSNames;
/*     */   }
/*     */ 
/*     */   public CustomMediaSizeName[] getCustomMediaSizeNames()
/*     */   {
/* 134 */     initMedia();
/* 135 */     return this.cupsCustomMediaSNames;
/*     */   }
/*     */ 
/*     */   public MediaPrintableArea[] getMediaPrintableArea()
/*     */   {
/* 143 */     initMedia();
/* 144 */     return this.cupsMediaPrintables;
/*     */   }
/*     */ 
/*     */   public MediaTray[] getMediaTrays()
/*     */   {
/* 151 */     initMedia();
/* 152 */     return this.cupsMediaTrays;
/*     */   }
/*     */ 
/*     */   private synchronized void initMedia()
/*     */   {
/* 160 */     if (this.initialized) {
/* 161 */       return;
/*     */     }
/* 163 */     this.initialized = true;
/*     */ 
/* 166 */     if (this.pageSizes == null) {
/* 167 */       return;
/*     */     }
/*     */ 
/* 170 */     this.cupsMediaPrintables = new MediaPrintableArea[this.nPageSizes];
/* 171 */     this.cupsMediaSNames = new MediaSizeName[this.nPageSizes];
/* 172 */     this.cupsCustomMediaSNames = new CustomMediaSizeName[this.nPageSizes];
/*     */ 
/* 179 */     for (int i = 0; i < this.nPageSizes; i++)
/*     */     {
/* 181 */       float f2 = (float)(this.pageSizes[(i * 6)] / 72.0D);
/* 182 */       float f1 = (float)(this.pageSizes[(i * 6 + 1)] / 72.0D);
/*     */ 
/* 184 */       float f3 = (float)(this.pageSizes[(i * 6 + 2)] / 72.0D);
/* 185 */       float f6 = (float)(this.pageSizes[(i * 6 + 3)] / 72.0D);
/* 186 */       float f5 = (float)(this.pageSizes[(i * 6 + 4)] / 72.0D);
/* 187 */       float f4 = (float)(this.pageSizes[(i * 6 + 5)] / 72.0D);
/*     */ 
/* 189 */       CustomMediaSizeName localCustomMediaSizeName = new CustomMediaSizeName(this.media[(i * 2)], this.media[(i * 2 + 1)], f2, f1);
/*     */ 
/* 193 */       if ((this.cupsMediaSNames[i] =  = localCustomMediaSizeName.getStandardMedia()) == null)
/*     */       {
/* 195 */         this.cupsMediaSNames[i] = localCustomMediaSizeName;
/*     */ 
/* 198 */         if ((f2 > 0.0D) && (f1 > 0.0D)) {
/* 199 */           new MediaSize(f2, f1, 25400, localCustomMediaSizeName);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 206 */       this.cupsCustomMediaSNames[i] = localCustomMediaSizeName;
/*     */ 
/* 208 */       MediaPrintableArea localMediaPrintableArea = null;
/*     */       try {
/* 210 */         localMediaPrintableArea = new MediaPrintableArea(f3, f4, f5, f6, 25400);
/*     */       }
/*     */       catch (IllegalArgumentException localIllegalArgumentException) {
/* 213 */         if ((f2 > 0.0F) && (f1 > 0.0F)) {
/* 214 */           localMediaPrintableArea = new MediaPrintableArea(0.0F, 0.0F, f2, f1, 25400);
/*     */         }
/*     */       }
/*     */ 
/* 218 */       this.cupsMediaPrintables[i] = localMediaPrintableArea;
/*     */     }
/*     */ 
/* 222 */     this.cupsMediaTrays = new MediaTray[this.nTrays];
/*     */ 
/* 225 */     for (int j = 0; j < this.nTrays; j++) {
/* 226 */       CustomMediaTray localCustomMediaTray = new CustomMediaTray(this.media[((this.nPageSizes + j) * 2)], this.media[((this.nPageSizes + j) * 2 + 1)]);
/*     */ 
/* 228 */       this.cupsMediaTrays[j] = localCustomMediaTray;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String getDefaultPrinter()
/*     */   {
/*     */     try
/*     */     {
/* 238 */       URL localURL = new URL("http", getServer(), getPort(), "");
/* 239 */       HttpURLConnection localHttpURLConnection = IPPPrintService.getIPPConnection(localURL);
/*     */ 
/* 242 */       if (localHttpURLConnection != null) {
/* 243 */         OutputStream localOutputStream = (OutputStream)AccessController.doPrivileged(new PrivilegedAction()
/*     */         {
/*     */           public Object run() {
/*     */             try {
/* 247 */               return this.val$urlConnection.getOutputStream();
/*     */             } catch (Exception localException) {
/*     */             }
/* 250 */             return null;
/*     */           }
/*     */         });
/* 254 */         if (localOutputStream == null) {
/* 255 */           return null;
/*     */         }
/*     */ 
/* 258 */         AttributeClass[] arrayOfAttributeClass = { AttributeClass.ATTRIBUTES_CHARSET, AttributeClass.ATTRIBUTES_NATURAL_LANGUAGE, new AttributeClass("requested-attributes", 68, "printer-name") };
/*     */ 
/* 266 */         if (IPPPrintService.writeIPPRequest(localOutputStream, "4001", arrayOfAttributeClass))
/*     */         {
/* 270 */           Object localObject = null;
/* 271 */           InputStream localInputStream = localHttpURLConnection.getInputStream();
/* 272 */           HashMap[] arrayOfHashMap = IPPPrintService.readIPPResponse(localInputStream);
/*     */ 
/* 274 */           localInputStream.close();
/*     */ 
/* 276 */           if ((arrayOfHashMap != null) && (arrayOfHashMap.length > 0)) {
/* 277 */             localObject = arrayOfHashMap[0];
/*     */           }
/*     */ 
/* 280 */           if (localObject == null) {
/* 281 */             localOutputStream.close();
/* 282 */             localHttpURLConnection.disconnect();
/*     */ 
/* 290 */             if (UnixPrintServiceLookup.isMac()) {
/* 291 */               return UnixPrintServiceLookup.getDefaultPrinterNameSysV();
/*     */             }
/*     */ 
/* 294 */             return null;
/*     */           }
/*     */ 
/* 298 */           AttributeClass localAttributeClass = (AttributeClass)localObject.get("printer-name");
/*     */ 
/* 301 */           if (localAttributeClass != null) {
/* 302 */             String str = localAttributeClass.getStringValue();
/* 303 */             localOutputStream.close();
/* 304 */             localHttpURLConnection.disconnect();
/* 305 */             return str;
/*     */           }
/*     */         }
/* 308 */         localOutputStream.close();
/* 309 */         localHttpURLConnection.disconnect();
/*     */       }
/*     */     } catch (Exception localException) {
/*     */     }
/* 313 */     return null;
/*     */   }
/*     */ 
/*     */   public static String[] getAllPrinters()
/*     */   {
/*     */     try
/*     */     {
/* 322 */       URL localURL = new URL("http", getServer(), getPort(), "");
/*     */ 
/* 324 */       HttpURLConnection localHttpURLConnection = IPPPrintService.getIPPConnection(localURL);
/*     */ 
/* 327 */       if (localHttpURLConnection != null) {
/* 328 */         OutputStream localOutputStream = (OutputStream)AccessController.doPrivileged(new PrivilegedAction()
/*     */         {
/*     */           public Object run() {
/*     */             try {
/* 332 */               return this.val$urlConnection.getOutputStream();
/*     */             } catch (Exception localException) {
/*     */             }
/* 335 */             return null;
/*     */           }
/*     */         });
/* 339 */         if (localOutputStream == null) {
/* 340 */           return null;
/*     */         }
/*     */ 
/* 343 */         AttributeClass[] arrayOfAttributeClass = { AttributeClass.ATTRIBUTES_CHARSET, AttributeClass.ATTRIBUTES_NATURAL_LANGUAGE, new AttributeClass("requested-attributes", 68, "printer-uri-supported") };
/*     */ 
/* 351 */         if (IPPPrintService.writeIPPRequest(localOutputStream, "4002", arrayOfAttributeClass))
/*     */         {
/* 354 */           InputStream localInputStream = localHttpURLConnection.getInputStream();
/* 355 */           HashMap[] arrayOfHashMap = IPPPrintService.readIPPResponse(localInputStream);
/*     */ 
/* 358 */           localInputStream.close();
/* 359 */           localOutputStream.close();
/* 360 */           localHttpURLConnection.disconnect();
/*     */ 
/* 362 */           if ((arrayOfHashMap == null) || (arrayOfHashMap.length == 0)) {
/* 363 */             return null;
/*     */           }
/*     */ 
/* 366 */           ArrayList localArrayList = new ArrayList();
/* 367 */           for (int i = 0; i < arrayOfHashMap.length; i++) {
/* 368 */             AttributeClass localAttributeClass = (AttributeClass)arrayOfHashMap[i].get("printer-uri-supported");
/*     */ 
/* 371 */             if (localAttributeClass != null) {
/* 372 */               String str = localAttributeClass.getStringValue();
/* 373 */               localArrayList.add(str);
/*     */             }
/*     */           }
/* 376 */           return (String[])localArrayList.toArray(new String[0]);
/*     */         }
/* 378 */         localOutputStream.close();
/* 379 */         localHttpURLConnection.disconnect();
/*     */       }
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/*     */     }
/* 385 */     return null;
/*     */   }
/*     */ 
/*     */   public static String getServer()
/*     */   {
/* 393 */     return cupsServer;
/*     */   }
/*     */ 
/*     */   public static int getPort()
/*     */   {
/* 400 */     return cupsPort;
/*     */   }
/*     */ 
/*     */   public static boolean isCupsRunning()
/*     */   {
/* 407 */     IPPPrintService.debug_println("CUPSPrinter>> libFound " + libFound);
/* 408 */     if (libFound) {
/* 409 */       IPPPrintService.debug_println("CUPSPrinter>> CUPS server " + getServer() + " port " + getPort());
/*     */ 
/* 411 */       return canConnect(getServer(), getPort());
/*     */     }
/* 413 */     return false;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  74 */     cupsServer = null;
/*  75 */     cupsPort = 0;
/*     */ 
/*  79 */     AccessController.doPrivileged(new LoadLibraryAction("awt"));
/*     */ 
/*  81 */     libFound = initIDs();
/*  82 */     if (libFound) {
/*  83 */       cupsServer = getCupsServer();
/*  84 */       cupsPort = getCupsPort();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.print.CUPSPrinter
 * JD-Core Version:    0.6.2
 */