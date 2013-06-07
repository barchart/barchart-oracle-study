/*      */ package sun.print;
/*      */ 
/*      */ import java.io.BufferedReader;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.DataInputStream;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.OutputStream;
/*      */ import java.io.OutputStreamWriter;
/*      */ import java.io.PrintStream;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.net.HttpURLConnection;
/*      */ import java.net.URI;
/*      */ import java.net.URISyntaxException;
/*      */ import java.net.URL;
/*      */ import java.nio.charset.Charset;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Locale;
/*      */ import javax.print.DocFlavor;
/*      */ import javax.print.DocFlavor.BYTE_ARRAY;
/*      */ import javax.print.DocFlavor.CHAR_ARRAY;
/*      */ import javax.print.DocFlavor.INPUT_STREAM;
/*      */ import javax.print.DocFlavor.READER;
/*      */ import javax.print.DocFlavor.SERVICE_FORMATTED;
/*      */ import javax.print.DocFlavor.STRING;
/*      */ import javax.print.DocFlavor.URL;
/*      */ import javax.print.DocPrintJob;
/*      */ import javax.print.PrintService;
/*      */ import javax.print.ServiceUIFactory;
/*      */ import javax.print.attribute.Attribute;
/*      */ import javax.print.attribute.AttributeSet;
/*      */ import javax.print.attribute.AttributeSetUtilities;
/*      */ import javax.print.attribute.EnumSyntax;
/*      */ import javax.print.attribute.HashAttributeSet;
/*      */ import javax.print.attribute.HashPrintServiceAttributeSet;
/*      */ import javax.print.attribute.PrintRequestAttribute;
/*      */ import javax.print.attribute.PrintServiceAttribute;
/*      */ import javax.print.attribute.PrintServiceAttributeSet;
/*      */ import javax.print.attribute.standard.Chromaticity;
/*      */ import javax.print.attribute.standard.ColorSupported;
/*      */ import javax.print.attribute.standard.Copies;
/*      */ import javax.print.attribute.standard.CopiesSupported;
/*      */ import javax.print.attribute.standard.Destination;
/*      */ import javax.print.attribute.standard.Fidelity;
/*      */ import javax.print.attribute.standard.Finishings;
/*      */ import javax.print.attribute.standard.JobName;
/*      */ import javax.print.attribute.standard.JobSheets;
/*      */ import javax.print.attribute.standard.Media;
/*      */ import javax.print.attribute.standard.MediaPrintableArea;
/*      */ import javax.print.attribute.standard.MediaSize;
/*      */ import javax.print.attribute.standard.MediaSize.ISO;
/*      */ import javax.print.attribute.standard.MediaSize.NA;
/*      */ import javax.print.attribute.standard.MediaSizeName;
/*      */ import javax.print.attribute.standard.MediaTray;
/*      */ import javax.print.attribute.standard.NumberUp;
/*      */ import javax.print.attribute.standard.OrientationRequested;
/*      */ import javax.print.attribute.standard.PDLOverrideSupported;
/*      */ import javax.print.attribute.standard.PageRanges;
/*      */ import javax.print.attribute.standard.PagesPerMinute;
/*      */ import javax.print.attribute.standard.PagesPerMinuteColor;
/*      */ import javax.print.attribute.standard.PrinterInfo;
/*      */ import javax.print.attribute.standard.PrinterIsAcceptingJobs;
/*      */ import javax.print.attribute.standard.PrinterLocation;
/*      */ import javax.print.attribute.standard.PrinterMakeAndModel;
/*      */ import javax.print.attribute.standard.PrinterMessageFromOperator;
/*      */ import javax.print.attribute.standard.PrinterMoreInfo;
/*      */ import javax.print.attribute.standard.PrinterMoreInfoManufacturer;
/*      */ import javax.print.attribute.standard.PrinterName;
/*      */ import javax.print.attribute.standard.PrinterState;
/*      */ import javax.print.attribute.standard.PrinterStateReasons;
/*      */ import javax.print.attribute.standard.PrinterURI;
/*      */ import javax.print.attribute.standard.QueuedJobCount;
/*      */ import javax.print.attribute.standard.RequestingUserName;
/*      */ import javax.print.attribute.standard.SheetCollate;
/*      */ import javax.print.attribute.standard.Sides;
/*      */ import javax.print.event.PrintServiceAttributeListener;
/*      */ import sun.security.action.GetPropertyAction;
/*      */ 
/*      */ public class IPPPrintService
/*      */   implements PrintService, SunPrinterJobService
/*      */ {
/*   80 */   public static final boolean debugPrint = "true".equalsIgnoreCase(str);
/*      */   private static final String debugPrefix = "IPPPrintService>> ";
/*      */   private static final String FORCE_PIPE_PROP = "sun.print.ippdebug";
/*      */   private String printer;
/*      */   private URI myURI;
/*      */   private URL myURL;
/*   86 */   private transient ServiceNotifier notifier = null;
/*      */ 
/*   88 */   private static int MAXCOPIES = 1000;
/*   89 */   private static short MAX_ATTRIBUTE_LENGTH = 255;
/*      */   private CUPSPrinter cps;
/*   92 */   private HttpURLConnection urlConnection = null;
/*      */   private DocFlavor[] supportedDocFlavors;
/*      */   private Class[] supportedCats;
/*      */   private MediaTray[] mediaTrays;
/*      */   private MediaSizeName[] mediaSizeNames;
/*      */   private CustomMediaSizeName[] customMediaSizeNames;
/*      */   private int defaultMediaIndex;
/*      */   private boolean isCupsPrinter;
/*      */   private boolean init;
/*      */   private Boolean isPS;
/*      */   private HashMap getAttMap;
/*  103 */   private boolean pngImagesAdded = false;
/*  104 */   private boolean gifImagesAdded = false;
/*  105 */   private boolean jpgImagesAdded = false;
/*      */   private static final byte STATUSCODE_SUCCESS = 0;
/*      */   private static final byte GRPTAG_OP_ATTRIBUTES = 1;
/*      */   private static final byte GRPTAG_JOB_ATTRIBUTES = 2;
/*      */   private static final byte GRPTAG_PRINTER_ATTRIBUTES = 4;
/*      */   private static final byte GRPTAG_END_ATTRIBUTES = 3;
/*      */   public static final String OP_GET_ATTRIBUTES = "000B";
/*      */   public static final String OP_CUPS_GET_DEFAULT = "4001";
/*      */   public static final String OP_CUPS_GET_PRINTERS = "4002";
/*  141 */   private static Object[] printReqAttribDefault = { Chromaticity.COLOR, new Copies(1), Fidelity.FIDELITY_FALSE, Finishings.NONE, new JobName("", Locale.getDefault()), JobSheets.NONE, MediaSizeName.NA_LETTER, new NumberUp(1), OrientationRequested.PORTRAIT, new PageRanges(1), new RequestingUserName("", Locale.getDefault()), Sides.ONE_SIDED };
/*      */ 
/*  174 */   private static Object[][] serviceAttributes = { { ColorSupported.class, "color-supported" }, { PagesPerMinute.class, "pages-per-minute" }, { PagesPerMinuteColor.class, "pages-per-minute-color" }, { PDLOverrideSupported.class, "pdl-override-supported" }, { PrinterInfo.class, "printer-info" }, { PrinterIsAcceptingJobs.class, "printer-is-accepting-jobs" }, { PrinterLocation.class, "printer-location" }, { PrinterMakeAndModel.class, "printer-make-and-model" }, { PrinterMessageFromOperator.class, "printer-message-from-operator" }, { PrinterMoreInfo.class, "printer-more-info" }, { PrinterMoreInfoManufacturer.class, "printer-more-info-manufacturer" }, { PrinterName.class, "printer-name" }, { PrinterState.class, "printer-state" }, { PrinterStateReasons.class, "printer-state-reasons" }, { PrinterURI.class, "printer-uri" }, { QueuedJobCount.class, "queued-job-count" } };
/*      */ 
/*  200 */   private static DocFlavor[] appPDF = { DocFlavor.BYTE_ARRAY.PDF, DocFlavor.INPUT_STREAM.PDF, DocFlavor.URL.PDF };
/*      */ 
/*  207 */   private static DocFlavor[] appPostScript = { DocFlavor.BYTE_ARRAY.POSTSCRIPT, DocFlavor.INPUT_STREAM.POSTSCRIPT, DocFlavor.URL.POSTSCRIPT };
/*      */ 
/*  214 */   private static DocFlavor[] appOctetStream = { DocFlavor.BYTE_ARRAY.AUTOSENSE, DocFlavor.INPUT_STREAM.AUTOSENSE, DocFlavor.URL.AUTOSENSE };
/*      */ 
/*  221 */   private static DocFlavor[] textPlain = { DocFlavor.BYTE_ARRAY.TEXT_PLAIN_UTF_8, DocFlavor.BYTE_ARRAY.TEXT_PLAIN_UTF_16, DocFlavor.BYTE_ARRAY.TEXT_PLAIN_UTF_16BE, DocFlavor.BYTE_ARRAY.TEXT_PLAIN_UTF_16LE, DocFlavor.BYTE_ARRAY.TEXT_PLAIN_US_ASCII, DocFlavor.INPUT_STREAM.TEXT_PLAIN_UTF_8, DocFlavor.INPUT_STREAM.TEXT_PLAIN_UTF_16, DocFlavor.INPUT_STREAM.TEXT_PLAIN_UTF_16BE, DocFlavor.INPUT_STREAM.TEXT_PLAIN_UTF_16LE, DocFlavor.INPUT_STREAM.TEXT_PLAIN_US_ASCII, DocFlavor.URL.TEXT_PLAIN_UTF_8, DocFlavor.URL.TEXT_PLAIN_UTF_16, DocFlavor.URL.TEXT_PLAIN_UTF_16BE, DocFlavor.URL.TEXT_PLAIN_UTF_16LE, DocFlavor.URL.TEXT_PLAIN_US_ASCII, DocFlavor.CHAR_ARRAY.TEXT_PLAIN, DocFlavor.STRING.TEXT_PLAIN, DocFlavor.READER.TEXT_PLAIN };
/*      */ 
/*  242 */   private static DocFlavor[] textPlainHost = { DocFlavor.BYTE_ARRAY.TEXT_PLAIN_HOST, DocFlavor.INPUT_STREAM.TEXT_PLAIN_HOST, DocFlavor.URL.TEXT_PLAIN_HOST };
/*      */ 
/*  249 */   private static DocFlavor[] imageJPG = { DocFlavor.BYTE_ARRAY.JPEG, DocFlavor.INPUT_STREAM.JPEG, DocFlavor.URL.JPEG };
/*      */ 
/*  256 */   private static DocFlavor[] imageGIF = { DocFlavor.BYTE_ARRAY.GIF, DocFlavor.INPUT_STREAM.GIF, DocFlavor.URL.GIF };
/*      */ 
/*  263 */   private static DocFlavor[] imagePNG = { DocFlavor.BYTE_ARRAY.PNG, DocFlavor.INPUT_STREAM.PNG, DocFlavor.URL.PNG };
/*      */ 
/*  270 */   private static DocFlavor[] textHtml = { DocFlavor.BYTE_ARRAY.TEXT_HTML_UTF_8, DocFlavor.BYTE_ARRAY.TEXT_HTML_UTF_16, DocFlavor.BYTE_ARRAY.TEXT_HTML_UTF_16BE, DocFlavor.BYTE_ARRAY.TEXT_HTML_UTF_16LE, DocFlavor.BYTE_ARRAY.TEXT_HTML_US_ASCII, DocFlavor.INPUT_STREAM.TEXT_HTML_UTF_8, DocFlavor.INPUT_STREAM.TEXT_HTML_UTF_16, DocFlavor.INPUT_STREAM.TEXT_HTML_UTF_16BE, DocFlavor.INPUT_STREAM.TEXT_HTML_UTF_16LE, DocFlavor.INPUT_STREAM.TEXT_HTML_US_ASCII, DocFlavor.URL.TEXT_HTML_UTF_8, DocFlavor.URL.TEXT_HTML_UTF_16, DocFlavor.URL.TEXT_HTML_UTF_16BE, DocFlavor.URL.TEXT_HTML_UTF_16LE, DocFlavor.URL.TEXT_HTML_US_ASCII };
/*      */ 
/*  295 */   private static DocFlavor[] textHtmlHost = { DocFlavor.BYTE_ARRAY.TEXT_HTML_HOST, DocFlavor.INPUT_STREAM.TEXT_HTML_HOST, DocFlavor.URL.TEXT_HTML_HOST };
/*      */ 
/*  303 */   private static DocFlavor[] appPCL = { DocFlavor.BYTE_ARRAY.PCL, DocFlavor.INPUT_STREAM.PCL, DocFlavor.URL.PCL };
/*      */ 
/*  311 */   private static Object[] allDocFlavors = { appPDF, appPostScript, appOctetStream, textPlain, imageJPG, imageGIF, imagePNG, textHtml, appPCL };
/*      */ 
/*      */   protected static void debug_println(String paramString)
/*      */   {
/*   68 */     if (debugPrint)
/*   69 */       System.out.println(paramString);
/*      */   }
/*      */ 
/*      */   IPPPrintService(String paramString, URL paramURL)
/*      */   {
/*  319 */     if ((paramString == null) || (paramURL == null)) {
/*  320 */       throw new IllegalArgumentException("null uri or printer name");
/*      */     }
/*  322 */     this.printer = paramString;
/*  323 */     this.supportedDocFlavors = null;
/*  324 */     this.supportedCats = null;
/*  325 */     this.mediaSizeNames = null;
/*  326 */     this.customMediaSizeNames = null;
/*  327 */     this.mediaTrays = null;
/*  328 */     this.myURL = paramURL;
/*  329 */     this.cps = null;
/*  330 */     this.isCupsPrinter = false;
/*  331 */     this.init = false;
/*  332 */     this.defaultMediaIndex = -1;
/*      */ 
/*  334 */     String str = this.myURL.getHost();
/*  335 */     if ((str != null) && (str.equals(CUPSPrinter.getServer()))) {
/*  336 */       this.isCupsPrinter = true;
/*      */       try {
/*  338 */         this.myURI = new URI("ipp://" + str + "/printers/" + this.printer);
/*      */ 
/*  340 */         debug_println("IPPPrintService>> IPPPrintService myURI : " + this.myURI);
/*      */       } catch (URISyntaxException localURISyntaxException) {
/*  342 */         throw new IllegalArgumentException("invalid url");
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   IPPPrintService(String paramString1, String paramString2, boolean paramBoolean)
/*      */   {
/*  349 */     if ((paramString1 == null) || (paramString2 == null)) {
/*  350 */       throw new IllegalArgumentException("null uri or printer name");
/*      */     }
/*  352 */     this.printer = paramString1;
/*  353 */     this.supportedDocFlavors = null;
/*  354 */     this.supportedCats = null;
/*  355 */     this.mediaSizeNames = null;
/*  356 */     this.customMediaSizeNames = null;
/*  357 */     this.mediaTrays = null;
/*  358 */     this.cps = null;
/*  359 */     this.init = false;
/*  360 */     this.defaultMediaIndex = -1;
/*      */     try {
/*  362 */       this.myURL = new URL(paramString2.replaceFirst("ipp", "http"));
/*      */     }
/*      */     catch (Exception localException) {
/*  365 */       debug_println("IPPPrintService>>  IPPPrintService, myURL=" + this.myURL + " Exception= " + localException);
/*      */     }
/*      */ 
/*  371 */     this.isCupsPrinter = paramBoolean;
/*      */     try {
/*  373 */       this.myURI = new URI(paramString2);
/*  374 */       debug_println("IPPPrintService>> IPPPrintService myURI : " + this.myURI);
/*      */     } catch (URISyntaxException localURISyntaxException) {
/*  376 */       throw new IllegalArgumentException("invalid uri");
/*      */     }
/*      */   }
/*      */ 
/*      */   private void initAttributes()
/*      */   {
/*  388 */     if (!this.init)
/*      */     {
/*  390 */       this.customMediaSizeNames = new CustomMediaSizeName[0];
/*      */ 
/*  392 */       if ((this.urlConnection = getIPPConnection(this.myURL)) == null) {
/*  393 */         this.mediaSizeNames = new MediaSizeName[0];
/*  394 */         this.mediaTrays = new MediaTray[0];
/*  395 */         debug_println("IPPPrintService>> initAttributes, NULL urlConnection ");
/*  396 */         this.init = true;
/*  397 */         return;
/*      */       }
/*      */ 
/*  401 */       opGetAttributes();
/*      */ 
/*  403 */       if (this.isCupsPrinter)
/*      */       {
/*      */         try
/*      */         {
/*  411 */           this.cps = new CUPSPrinter(this.printer);
/*  412 */           this.mediaSizeNames = this.cps.getMediaSizeNames();
/*  413 */           this.mediaTrays = this.cps.getMediaTrays();
/*  414 */           this.customMediaSizeNames = this.cps.getCustomMediaSizeNames();
/*  415 */           this.urlConnection.disconnect();
/*  416 */           this.init = true;
/*  417 */           return;
/*      */         } catch (Exception localException) {
/*  419 */           debug_println("IPPPrintService>> initAttributes, error creating CUPSPrinter e=" + localException);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  425 */       Media[] arrayOfMedia = (Media[])getSupportedMedia();
/*  426 */       ArrayList localArrayList1 = new ArrayList();
/*  427 */       ArrayList localArrayList2 = new ArrayList();
/*  428 */       for (int i = 0; i < arrayOfMedia.length; i++) {
/*  429 */         if ((arrayOfMedia[i] instanceof MediaSizeName))
/*  430 */           localArrayList1.add(arrayOfMedia[i]);
/*  431 */         else if ((arrayOfMedia[i] instanceof MediaTray)) {
/*  432 */           localArrayList2.add(arrayOfMedia[i]);
/*      */         }
/*      */       }
/*      */ 
/*  436 */       if (localArrayList1 != null) {
/*  437 */         this.mediaSizeNames = new MediaSizeName[localArrayList1.size()];
/*  438 */         this.mediaSizeNames = ((MediaSizeName[])localArrayList1.toArray(this.mediaSizeNames));
/*      */       }
/*      */ 
/*  441 */       if (localArrayList2 != null) {
/*  442 */         this.mediaTrays = new MediaTray[localArrayList2.size()];
/*  443 */         this.mediaTrays = ((MediaTray[])localArrayList2.toArray(this.mediaTrays));
/*      */       }
/*      */ 
/*  446 */       this.urlConnection.disconnect();
/*      */ 
/*  448 */       this.init = true;
/*      */     }
/*      */   }
/*      */ 
/*      */   public DocPrintJob createPrintJob()
/*      */   {
/*  454 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  455 */     if (localSecurityManager != null) {
/*  456 */       localSecurityManager.checkPrintJobAccess();
/*      */     }
/*      */ 
/*  459 */     return new UnixPrintJob(this);
/*      */   }
/*      */ 
/*      */   public synchronized Object getSupportedAttributeValues(Class<? extends Attribute> paramClass, DocFlavor paramDocFlavor, AttributeSet paramAttributeSet)
/*      */   {
/*  468 */     if (paramClass == null) {
/*  469 */       throw new NullPointerException("null category");
/*      */     }
/*  471 */     if (!Attribute.class.isAssignableFrom(paramClass)) {
/*  472 */       throw new IllegalArgumentException(paramClass + " does not implement Attribute");
/*      */     }
/*      */ 
/*  475 */     if (paramDocFlavor != null) {
/*  476 */       if (!isDocFlavorSupported(paramDocFlavor)) {
/*  477 */         throw new IllegalArgumentException(paramDocFlavor + " is an unsupported flavor");
/*      */       }
/*  479 */       if (isAutoSense(paramDocFlavor)) {
/*  480 */         return null;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  485 */     if (!isAttributeCategorySupported(paramClass)) {
/*  486 */       return null;
/*      */     }
/*      */ 
/*  490 */     if (!isDestinationSupported(paramDocFlavor, paramAttributeSet)) {
/*  491 */       return null;
/*      */     }
/*      */ 
/*  494 */     initAttributes();
/*      */     Object localObject1;
/*      */     Object localObject6;
/*  497 */     if ((paramClass == Copies.class) || (paramClass == CopiesSupported.class))
/*      */     {
/*  499 */       if ((paramDocFlavor == null) || ((!paramDocFlavor.equals(DocFlavor.INPUT_STREAM.POSTSCRIPT)) && (!paramDocFlavor.equals(DocFlavor.URL.POSTSCRIPT)) && (!paramDocFlavor.equals(DocFlavor.BYTE_ARRAY.POSTSCRIPT))))
/*      */       {
/*  503 */         localObject1 = new CopiesSupported(1, MAXCOPIES);
/*  504 */         Object localObject4 = this.getAttMap != null ? (AttributeClass)this.getAttMap.get(((CopiesSupported)localObject1).getName()) : null;
/*      */ 
/*  506 */         if (localObject4 != null) {
/*  507 */           localObject6 = localObject4.getIntRangeValue();
/*  508 */           localObject1 = new CopiesSupported(localObject6[0], localObject6[1]);
/*      */         }
/*  510 */         return localObject1;
/*      */       }
/*  512 */       return null;
/*      */     }
/*  514 */     if (paramClass == Chromaticity.class) {
/*  515 */       if ((paramDocFlavor == null) || (paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE)) || (paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) || (!isIPPSupportedImages(paramDocFlavor.getMimeType())))
/*      */       {
/*  519 */         localObject1 = new Chromaticity[1];
/*  520 */         localObject1[0] = Chromaticity.COLOR;
/*  521 */         return localObject1;
/*      */       }
/*  523 */       return null;
/*      */     }
/*  525 */     if (paramClass == Destination.class) {
/*  526 */       if ((paramDocFlavor == null) || (paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE)) || (paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)))
/*      */       {
/*      */         try
/*      */         {
/*  530 */           return new Destination(new File("out.ps").toURI());
/*      */         } catch (SecurityException localSecurityException1) {
/*      */           try {
/*  533 */             return new Destination(new URI("file:out.ps"));
/*      */           } catch (URISyntaxException localURISyntaxException) {
/*  535 */             return null;
/*      */           }
/*      */         }
/*      */       }
/*  539 */       return null;
/*      */     }
/*      */     Object localObject2;
/*  540 */     if (paramClass == Fidelity.class) {
/*  541 */       localObject2 = new Fidelity[2];
/*  542 */       localObject2[0] = Fidelity.FIDELITY_FALSE;
/*  543 */       localObject2[1] = Fidelity.FIDELITY_TRUE;
/*  544 */       return localObject2;
/*  545 */     }if (paramClass == Finishings.class) {
/*  546 */       localObject2 = this.getAttMap != null ? (AttributeClass)this.getAttMap.get("finishings-supported") : null;
/*      */ 
/*  549 */       if (localObject2 != null) {
/*  550 */         int[] arrayOfInt = ((AttributeClass)localObject2).getArrayOfIntValues();
/*  551 */         if ((arrayOfInt != null) && (arrayOfInt.length > 0)) {
/*  552 */           localObject6 = new Finishings[arrayOfInt.length];
/*  553 */           for (int n = 0; n < arrayOfInt.length; n++) {
/*  554 */             localObject6[n] = Finishings.NONE;
/*  555 */             Finishings[] arrayOfFinishings = (Finishings[])new ExtFinishing(100).getAll();
/*      */ 
/*  557 */             for (int i4 = 0; i4 < arrayOfFinishings.length; i4++) {
/*  558 */               if (arrayOfInt[n] == arrayOfFinishings[i4].getValue()) {
/*  559 */                 localObject6[n] = arrayOfFinishings[i4];
/*  560 */                 break;
/*      */               }
/*      */             }
/*      */           }
/*  564 */           return localObject6;
/*      */         }
/*      */       }
/*      */     } else { if (paramClass == JobName.class)
/*  568 */         return new JobName("Java Printing", null);
/*  569 */       if (paramClass == JobSheets.class) {
/*  570 */         localObject2 = new JobSheets[2];
/*  571 */         localObject2[0] = JobSheets.NONE;
/*  572 */         localObject2[1] = JobSheets.STANDARD;
/*  573 */         return localObject2;
/*      */       }
/*  575 */       if (paramClass == Media.class) {
/*  576 */         localObject2 = new Media[this.mediaSizeNames.length + this.mediaTrays.length];
/*      */ 
/*  579 */         for (int j = 0; j < this.mediaSizeNames.length; j++) {
/*  580 */           localObject2[j] = this.mediaSizeNames[j];
/*      */         }
/*      */ 
/*  583 */         for (j = 0; j < this.mediaTrays.length; j++) {
/*  584 */           localObject2[(j + this.mediaSizeNames.length)] = this.mediaTrays[j];
/*      */         }
/*      */ 
/*  587 */         if (localObject2.length == 0) {
/*  588 */           localObject2 = new Media[1];
/*  589 */           localObject2[0] = ((Media)getDefaultAttributeValue(Media.class));
/*      */         }
/*      */ 
/*  592 */         return localObject2;
/*      */       }
/*      */       Object localObject7;
/*      */       int i3;
/*  593 */       if (paramClass == MediaPrintableArea.class) {
/*  594 */         localObject2 = null;
/*  595 */         if (this.cps != null) {
/*  596 */           localObject2 = this.cps.getMediaPrintableArea();
/*      */         }
/*      */ 
/*  599 */         if (localObject2 == null) {
/*  600 */           localObject2 = new MediaPrintableArea[1];
/*  601 */           localObject2[0] = ((MediaPrintableArea)getDefaultAttributeValue(MediaPrintableArea.class));
/*      */         }
/*      */ 
/*  605 */         if ((paramAttributeSet == null) || (paramAttributeSet.size() == 0)) {
/*  606 */           ArrayList localArrayList = new ArrayList();
/*      */ 
/*  609 */           for (int m = 0; m < localObject2.length; m++) {
/*  610 */             if (localObject2[m] != null) {
/*  611 */               localArrayList.add(localObject2[m]);
/*      */             }
/*      */           }
/*  614 */           if (localArrayList.size() > 0) {
/*  615 */             localObject2 = new MediaPrintableArea[localArrayList.size()];
/*  616 */             localArrayList.toArray((Object[])localObject2);
/*      */           }
/*  618 */           return localObject2;
/*      */         }
/*      */ 
/*  621 */         int k = -1;
/*  622 */         localObject7 = (Media)paramAttributeSet.get(Media.class);
/*  623 */         if ((localObject7 != null) && ((localObject7 instanceof MediaSizeName))) {
/*  624 */           localObject8 = (MediaSizeName)localObject7;
/*      */ 
/*  628 */           if ((this.mediaSizeNames.length == 0) && (((MediaSizeName)localObject8).equals(getDefaultAttributeValue(Media.class))))
/*      */           {
/*  631 */             return localObject2;
/*      */           }
/*      */ 
/*  634 */           for (i3 = 0; i3 < this.mediaSizeNames.length; i3++) {
/*  635 */             if (((MediaSizeName)localObject8).equals(this.mediaSizeNames[i3])) {
/*  636 */               k = i3;
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*  641 */         if (k == -1) {
/*  642 */           return null;
/*      */         }
/*  644 */         Object localObject8 = new MediaPrintableArea[1];
/*  645 */         localObject8[0] = localObject2[k];
/*  646 */         return localObject8;
/*      */       }
/*      */       Object localObject5;
/*  648 */       if (paramClass == NumberUp.class) {
/*  649 */         localObject2 = this.getAttMap != null ? (AttributeClass)this.getAttMap.get("number-up-supported") : null;
/*      */ 
/*  651 */         if (localObject2 != null) {
/*  652 */           localObject5 = ((AttributeClass)localObject2).getArrayOfIntValues();
/*  653 */           if (localObject5 != null) {
/*  654 */             localObject7 = new NumberUp[localObject5.length];
/*  655 */             for (int i1 = 0; i1 < localObject5.length; i1++) {
/*  656 */               localObject7[i1] = new NumberUp(localObject5[i1]);
/*      */             }
/*  658 */             return localObject7;
/*      */           }
/*  660 */           return null;
/*      */         }
/*      */       } else {
/*  663 */         if (paramClass == OrientationRequested.class) {
/*  664 */           if ((paramDocFlavor != null) && ((paramDocFlavor.equals(DocFlavor.INPUT_STREAM.POSTSCRIPT)) || (paramDocFlavor.equals(DocFlavor.URL.POSTSCRIPT)) || (paramDocFlavor.equals(DocFlavor.BYTE_ARRAY.POSTSCRIPT))))
/*      */           {
/*  668 */             return null;
/*      */           }
/*      */ 
/*  671 */           int i = 0;
/*  672 */           localObject5 = null;
/*      */ 
/*  674 */           localObject7 = this.getAttMap != null ? (AttributeClass)this.getAttMap.get("orientation-requested-supported") : null;
/*      */           Object localObject9;
/*  677 */           if (localObject7 != null) {
/*  678 */             localObject9 = ((AttributeClass)localObject7).getArrayOfIntValues();
/*  679 */             if ((localObject9 != null) && (localObject9.length > 0)) {
/*  680 */               localObject5 = new OrientationRequested[localObject9.length];
/*      */ 
/*  682 */               for (i3 = 0; i3 < localObject9.length; i3++) {
/*  683 */                 switch (localObject9[i3]) {
/*      */                 case 3:
/*      */                 default:
/*  686 */                   localObject5[i3] = OrientationRequested.PORTRAIT;
/*  687 */                   break;
/*      */                 case 4:
/*  689 */                   localObject5[i3] = OrientationRequested.LANDSCAPE;
/*  690 */                   break;
/*      */                 case 5:
/*  692 */                   localObject5[i3] = OrientationRequested.REVERSE_LANDSCAPE;
/*      */ 
/*  694 */                   break;
/*      */                 case 6:
/*  696 */                   localObject5[i3] = OrientationRequested.REVERSE_PORTRAIT;
/*      */ 
/*  698 */                   i = 1;
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */ 
/*  704 */           if ((paramDocFlavor == null) || (paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE)) || (paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)))
/*      */           {
/*  708 */             if ((i != 0) && (paramDocFlavor == null)) {
/*  709 */               localObject9 = new OrientationRequested[4];
/*  710 */               localObject9[0] = OrientationRequested.PORTRAIT;
/*  711 */               localObject9[1] = OrientationRequested.LANDSCAPE;
/*  712 */               localObject9[2] = OrientationRequested.REVERSE_LANDSCAPE;
/*  713 */               localObject9[3] = OrientationRequested.REVERSE_PORTRAIT;
/*  714 */               return localObject9;
/*      */             }
/*  716 */             localObject9 = new OrientationRequested[3];
/*  717 */             localObject9[0] = OrientationRequested.PORTRAIT;
/*  718 */             localObject9[1] = OrientationRequested.LANDSCAPE;
/*  719 */             localObject9[2] = OrientationRequested.REVERSE_LANDSCAPE;
/*  720 */             return localObject9;
/*      */           }
/*      */ 
/*  723 */           return localObject5;
/*      */         }
/*      */         Object localObject3;
/*  725 */         if (paramClass == PageRanges.class) {
/*  726 */           if ((paramDocFlavor == null) || (paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE)) || (paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)))
/*      */           {
/*  729 */             localObject3 = new PageRanges[1];
/*  730 */             localObject3[0] = new PageRanges(1, 2147483647);
/*  731 */             return localObject3;
/*      */           }
/*      */ 
/*  734 */           return null;
/*      */         }
/*  736 */         if (paramClass == RequestingUserName.class) {
/*  737 */           localObject3 = "";
/*      */           try {
/*  739 */             localObject3 = System.getProperty("user.name", "");
/*      */           } catch (SecurityException localSecurityException2) {
/*      */           }
/*  742 */           return new RequestingUserName((String)localObject3, null);
/*  743 */         }if (paramClass == Sides.class)
/*      */         {
/*  749 */           localObject3 = this.getAttMap != null ? (AttributeClass)this.getAttMap.get("sides-supported") : null;
/*      */ 
/*  752 */           if (localObject3 != null) {
/*  753 */             String[] arrayOfString = ((AttributeClass)localObject3).getArrayOfStringValues();
/*  754 */             if ((arrayOfString != null) && (arrayOfString.length > 0)) {
/*  755 */               localObject7 = new Sides[arrayOfString.length];
/*  756 */               for (int i2 = 0; i2 < arrayOfString.length; i2++) {
/*  757 */                 if (arrayOfString[i2].endsWith("long-edge"))
/*  758 */                   localObject7[i2] = Sides.TWO_SIDED_LONG_EDGE;
/*  759 */                 else if (arrayOfString[i2].endsWith("short-edge"))
/*  760 */                   localObject7[i2] = Sides.TWO_SIDED_SHORT_EDGE;
/*      */                 else {
/*  762 */                   localObject7[i2] = Sides.ONE_SIDED;
/*      */                 }
/*      */               }
/*  765 */               return localObject7;
/*      */             }
/*      */           }
/*      */         }
/*      */       } }
/*  770 */     return null;
/*      */   }
/*      */ 
/*      */   public AttributeSet getUnsupportedAttributes(DocFlavor paramDocFlavor, AttributeSet paramAttributeSet)
/*      */   {
/*  788 */     if ((paramDocFlavor != null) && (!isDocFlavorSupported(paramDocFlavor))) {
/*  789 */       throw new IllegalArgumentException("flavor " + paramDocFlavor + "is not supported");
/*      */     }
/*      */ 
/*  793 */     if (paramAttributeSet == null) {
/*  794 */       return null;
/*      */     }
/*      */ 
/*  798 */     HashAttributeSet localHashAttributeSet = new HashAttributeSet();
/*  799 */     Attribute[] arrayOfAttribute = paramAttributeSet.toArray();
/*  800 */     for (int i = 0; i < arrayOfAttribute.length; i++)
/*      */       try {
/*  802 */         Attribute localAttribute = arrayOfAttribute[i];
/*  803 */         if (!isAttributeCategorySupported(localAttribute.getCategory()))
/*  804 */           localHashAttributeSet.add(localAttribute);
/*  805 */         else if (!isAttributeValueSupported(localAttribute, paramDocFlavor, paramAttributeSet))
/*      */         {
/*  807 */           localHashAttributeSet.add(localAttribute);
/*      */         }
/*      */       }
/*      */       catch (ClassCastException localClassCastException) {
/*      */       }
/*  812 */     if (localHashAttributeSet.isEmpty()) {
/*  813 */       return null;
/*      */     }
/*  815 */     return localHashAttributeSet;
/*      */   }
/*      */ 
/*      */   public synchronized DocFlavor[] getSupportedDocFlavors()
/*      */   {
/*      */     Object localObject;
/*  822 */     if (this.supportedDocFlavors != null) {
/*  823 */       int i = this.supportedDocFlavors.length;
/*  824 */       localObject = new DocFlavor[i];
/*  825 */       System.arraycopy(this.supportedDocFlavors, 0, localObject, 0, i);
/*  826 */       return localObject;
/*      */     }
/*  828 */     initAttributes();
/*      */ 
/*  830 */     if ((this.getAttMap != null) && (this.getAttMap.containsKey("document-format-supported")))
/*      */     {
/*  833 */       AttributeClass localAttributeClass = (AttributeClass)this.getAttMap.get("document-format-supported");
/*      */ 
/*  835 */       if (localAttributeClass != null)
/*      */       {
/*  837 */         int j = 0;
/*  838 */         String[] arrayOfString = localAttributeClass.getArrayOfStringValues();
/*      */ 
/*  840 */         HashSet localHashSet = new HashSet();
/*      */ 
/*  842 */         String str = DocFlavor.hostEncoding.toLowerCase(Locale.ENGLISH);
/*      */ 
/*  844 */         int m = (!str.equals("utf-8")) && (!str.equals("utf-16")) && (!str.equals("utf-16be")) && (!str.equals("utf-16le")) && (!str.equals("us-ascii")) ? 1 : 0;
/*      */ 
/*  848 */         for (int n = 0; n < arrayOfString.length; n++) {
/*  849 */           for (int k = 0; k < allDocFlavors.length; k++) {
/*  850 */             DocFlavor[] arrayOfDocFlavor1 = (DocFlavor[])allDocFlavors[k];
/*      */ 
/*  852 */             localObject = arrayOfDocFlavor1[0].getMimeType();
/*  853 */             if (((String)localObject).startsWith(arrayOfString[n]))
/*      */             {
/*  855 */               localHashSet.addAll(Arrays.asList(arrayOfDocFlavor1));
/*      */ 
/*  857 */               if ((((String)localObject).equals("text/plain")) && (m != 0))
/*      */               {
/*  859 */                 localHashSet.add(Arrays.asList(textPlainHost)); break;
/*  860 */               }if ((((String)localObject).equals("text/html")) && (m != 0))
/*      */               {
/*  862 */                 localHashSet.add(Arrays.asList(textHtmlHost)); break;
/*  863 */               }if (((String)localObject).equals("image/png")) {
/*  864 */                 this.pngImagesAdded = true; break;
/*  865 */               }if (((String)localObject).equals("image/gif")) {
/*  866 */                 this.gifImagesAdded = true; break;
/*  867 */               }if (((String)localObject).equals("image/jpeg")) {
/*  868 */                 this.jpgImagesAdded = true; break;
/*  869 */               }if (((String)localObject).indexOf("postscript") == -1) break;
/*  870 */               j = 1; break;
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*  877 */           if (k == allDocFlavors.length)
/*      */           {
/*  879 */             localHashSet.add(new DocFlavor.BYTE_ARRAY(arrayOfString[n]));
/*  880 */             localHashSet.add(new DocFlavor.INPUT_STREAM(arrayOfString[n]));
/*  881 */             localHashSet.add(new DocFlavor.URL(arrayOfString[n]));
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  887 */         if ((j != 0) || (this.isCupsPrinter))
/*      */         {
/*  893 */           localHashSet.add(DocFlavor.SERVICE_FORMATTED.PAGEABLE);
/*  894 */           localHashSet.add(DocFlavor.SERVICE_FORMATTED.PRINTABLE);
/*      */ 
/*  896 */           localHashSet.addAll(Arrays.asList(imageJPG));
/*  897 */           localHashSet.addAll(Arrays.asList(imagePNG));
/*  898 */           localHashSet.addAll(Arrays.asList(imageGIF));
/*      */         }
/*  900 */         this.supportedDocFlavors = new DocFlavor[localHashSet.size()];
/*  901 */         localHashSet.toArray(this.supportedDocFlavors);
/*  902 */         n = this.supportedDocFlavors.length;
/*  903 */         DocFlavor[] arrayOfDocFlavor2 = new DocFlavor[n];
/*  904 */         System.arraycopy(this.supportedDocFlavors, 0, arrayOfDocFlavor2, 0, n);
/*  905 */         return arrayOfDocFlavor2;
/*      */       }
/*      */     }
/*  908 */     return null;
/*      */   }
/*      */ 
/*      */   public boolean isDocFlavorSupported(DocFlavor paramDocFlavor)
/*      */   {
/*  913 */     if (this.supportedDocFlavors == null) {
/*  914 */       getSupportedDocFlavors();
/*      */     }
/*  916 */     if (this.supportedDocFlavors != null) {
/*  917 */       for (int i = 0; i < this.supportedDocFlavors.length; i++) {
/*  918 */         if (paramDocFlavor.equals(this.supportedDocFlavors[i])) {
/*  919 */           return true;
/*      */         }
/*      */       }
/*      */     }
/*  923 */     return false;
/*      */   }
/*      */ 
/*      */   public CustomMediaSizeName findCustomMedia(MediaSizeName paramMediaSizeName)
/*      */   {
/*  931 */     if (this.customMediaSizeNames == null) {
/*  932 */       return null;
/*      */     }
/*  934 */     for (int i = 0; i < this.customMediaSizeNames.length; i++) {
/*  935 */       CustomMediaSizeName localCustomMediaSizeName = this.customMediaSizeNames[i];
/*      */ 
/*  937 */       MediaSizeName localMediaSizeName = localCustomMediaSizeName.getStandardMedia();
/*  938 */       if (paramMediaSizeName.equals(localMediaSizeName)) {
/*  939 */         return this.customMediaSizeNames[i];
/*      */       }
/*      */     }
/*  942 */     return null;
/*      */   }
/*      */ 
/*      */   private Media getIPPMedia(String paramString)
/*      */   {
/*  950 */     CustomMediaSizeName localCustomMediaSizeName = new CustomMediaSizeName("sample", "", 0.0F, 0.0F);
/*      */ 
/*  952 */     Media[] arrayOfMedia1 = localCustomMediaSizeName.getSuperEnumTable();
/*  953 */     for (int i = 0; i < arrayOfMedia1.length; i++) {
/*  954 */       if (paramString.equals("" + arrayOfMedia1[i])) {
/*  955 */         return arrayOfMedia1[i];
/*      */       }
/*      */     }
/*  958 */     CustomMediaTray localCustomMediaTray = new CustomMediaTray("sample", "");
/*  959 */     Media[] arrayOfMedia2 = localCustomMediaTray.getSuperEnumTable();
/*  960 */     for (int j = 0; j < arrayOfMedia2.length; j++) {
/*  961 */       if (paramString.equals("" + arrayOfMedia2[j])) {
/*  962 */         return arrayOfMedia2[j];
/*      */       }
/*      */     }
/*  965 */     return null;
/*      */   }
/*      */ 
/*      */   private Media[] getSupportedMedia() {
/*  969 */     if ((this.getAttMap != null) && (this.getAttMap.containsKey("media-supported")))
/*      */     {
/*  972 */       AttributeClass localAttributeClass = (AttributeClass)this.getAttMap.get("media-supported");
/*      */ 
/*  975 */       if (localAttributeClass != null) {
/*  976 */         String[] arrayOfString = localAttributeClass.getArrayOfStringValues();
/*      */ 
/*  978 */         Media[] arrayOfMedia = new Media[arrayOfString.length];
/*      */ 
/*  980 */         for (int i = 0; i < arrayOfString.length; i++) {
/*  981 */           Media localMedia = getIPPMedia(arrayOfString[i]);
/*      */ 
/*  983 */           arrayOfMedia[i] = localMedia;
/*      */         }
/*  985 */         return arrayOfMedia;
/*      */       }
/*      */     }
/*  988 */     return new Media[0];
/*      */   }
/*      */ 
/*      */   public synchronized Class[] getSupportedAttributeCategories()
/*      */   {
/*  993 */     if (this.supportedCats != null) {
/*  994 */       return this.supportedCats;
/*      */     }
/*      */ 
/*  997 */     initAttributes();
/*      */ 
/*  999 */     ArrayList localArrayList = new ArrayList();
/*      */ 
/* 1002 */     for (int i = 0; i < printReqAttribDefault.length; i++) {
/* 1003 */       PrintRequestAttribute localPrintRequestAttribute = (PrintRequestAttribute)printReqAttribDefault[i];
/*      */ 
/* 1005 */       if ((this.getAttMap != null) && (this.getAttMap.containsKey(localPrintRequestAttribute.getName() + "-supported")))
/*      */       {
/* 1007 */         Class localClass = localPrintRequestAttribute.getCategory();
/* 1008 */         localArrayList.add(localClass);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1015 */     if (this.isCupsPrinter) {
/* 1016 */       if (!localArrayList.contains(Media.class)) {
/* 1017 */         localArrayList.add(Media.class);
/*      */       }
/*      */ 
/* 1022 */       localArrayList.add(MediaPrintableArea.class);
/*      */ 
/* 1025 */       localArrayList.add(Destination.class);
/*      */     }
/*      */ 
/* 1030 */     if ((this.getAttMap != null) && (this.getAttMap.containsKey("color-supported"))) {
/* 1031 */       localArrayList.add(Chromaticity.class);
/*      */     }
/* 1033 */     this.supportedCats = new Class[localArrayList.size()];
/* 1034 */     localArrayList.toArray(this.supportedCats);
/* 1035 */     return this.supportedCats;
/*      */   }
/*      */ 
/*      */   public boolean isAttributeCategorySupported(Class<? extends Attribute> paramClass)
/*      */   {
/* 1042 */     if (paramClass == null) {
/* 1043 */       throw new NullPointerException("null category");
/*      */     }
/* 1045 */     if (!Attribute.class.isAssignableFrom(paramClass)) {
/* 1046 */       throw new IllegalArgumentException(paramClass + " is not an Attribute");
/*      */     }
/*      */ 
/* 1050 */     if (this.supportedCats == null) {
/* 1051 */       getSupportedAttributeCategories();
/*      */     }
/*      */ 
/* 1058 */     if (paramClass == OrientationRequested.class) {
/* 1059 */       return true;
/*      */     }
/*      */ 
/* 1062 */     for (int i = 0; i < this.supportedCats.length; i++) {
/* 1063 */       if (paramClass == this.supportedCats[i]) {
/* 1064 */         return true;
/*      */       }
/*      */     }
/*      */ 
/* 1068 */     return false;
/*      */   }
/*      */ 
/*      */   public synchronized <T extends PrintServiceAttribute> T getAttribute(Class<T> paramClass)
/*      */   {
/* 1075 */     if (paramClass == null) {
/* 1076 */       throw new NullPointerException("category");
/*      */     }
/* 1078 */     if (!PrintServiceAttribute.class.isAssignableFrom(paramClass)) {
/* 1079 */       throw new IllegalArgumentException("Not a PrintServiceAttribute");
/*      */     }
/*      */ 
/* 1082 */     initAttributes();
/*      */ 
/* 1084 */     if (paramClass == PrinterName.class)
/* 1085 */       return new PrinterName(this.printer, null);
/*      */     Object localObject1;
/*      */     Object localObject2;
/* 1086 */     if (paramClass == QueuedJobCount.class) {
/* 1087 */       localObject1 = new QueuedJobCount(0);
/* 1088 */       localObject2 = this.getAttMap != null ? (AttributeClass)this.getAttMap.get(((QueuedJobCount)localObject1).getName()) : null;
/*      */ 
/* 1091 */       if (localObject2 != null) {
/* 1092 */         localObject1 = new QueuedJobCount(localObject2.getIntValue());
/*      */       }
/* 1094 */       return localObject1;
/* 1095 */     }if (paramClass == PrinterIsAcceptingJobs.class) {
/* 1096 */       localObject1 = PrinterIsAcceptingJobs.ACCEPTING_JOBS;
/*      */ 
/* 1098 */       localObject2 = this.getAttMap != null ? (AttributeClass)this.getAttMap.get(((PrinterIsAcceptingJobs)localObject1).getName()) : null;
/*      */ 
/* 1101 */       if ((localObject2 != null) && (localObject2.getByteValue() == 0)) {
/* 1102 */         localObject1 = PrinterIsAcceptingJobs.NOT_ACCEPTING_JOBS;
/*      */       }
/* 1104 */       return localObject1;
/* 1105 */     }if (paramClass == ColorSupported.class) {
/* 1106 */       localObject1 = ColorSupported.SUPPORTED;
/* 1107 */       localObject2 = this.getAttMap != null ? (AttributeClass)this.getAttMap.get(((ColorSupported)localObject1).getName()) : null;
/*      */ 
/* 1110 */       if ((localObject2 != null) && (localObject2.getByteValue() == 0)) {
/* 1111 */         localObject1 = ColorSupported.NOT_SUPPORTED;
/*      */       }
/* 1113 */       return localObject1;
/* 1114 */     }if (paramClass == PDLOverrideSupported.class)
/*      */     {
/* 1116 */       if (this.isCupsPrinter)
/*      */       {
/* 1118 */         return PDLOverrideSupported.NOT_ATTEMPTED;
/*      */       }
/*      */ 
/* 1121 */       return PDLOverrideSupported.NOT_ATTEMPTED;
/*      */     }
/*      */ 
/* 1124 */     return null;
/*      */   }
/*      */ 
/*      */   public synchronized PrintServiceAttributeSet getAttributes()
/*      */   {
/* 1131 */     this.init = false;
/* 1132 */     initAttributes();
/*      */ 
/* 1134 */     HashPrintServiceAttributeSet localHashPrintServiceAttributeSet = new HashPrintServiceAttributeSet();
/*      */ 
/* 1137 */     for (int i = 0; i < serviceAttributes.length; i++) {
/* 1138 */       String str = (String)serviceAttributes[i][1];
/* 1139 */       if ((this.getAttMap != null) && (this.getAttMap.containsKey(str))) {
/* 1140 */         Class localClass = (Class)serviceAttributes[i][0];
/* 1141 */         PrintServiceAttribute localPrintServiceAttribute = getAttribute(localClass);
/* 1142 */         if (localPrintServiceAttribute != null) {
/* 1143 */           localHashPrintServiceAttributeSet.add(localPrintServiceAttribute);
/*      */         }
/*      */       }
/*      */     }
/* 1147 */     return AttributeSetUtilities.unmodifiableView(localHashPrintServiceAttributeSet);
/*      */   }
/*      */ 
/*      */   public boolean isIPPSupportedImages(String paramString) {
/* 1151 */     if (this.supportedDocFlavors == null) {
/* 1152 */       getSupportedDocFlavors();
/*      */     }
/*      */ 
/* 1155 */     if ((paramString.equals("image/png")) && (this.pngImagesAdded))
/* 1156 */       return true;
/* 1157 */     if ((paramString.equals("image/gif")) && (this.gifImagesAdded))
/* 1158 */       return true;
/* 1159 */     if ((paramString.equals("image/jpeg")) && (this.jpgImagesAdded)) {
/* 1160 */       return true;
/*      */     }
/*      */ 
/* 1163 */     return false;
/*      */   }
/*      */ 
/*      */   private boolean isSupportedCopies(Copies paramCopies)
/*      */   {
/* 1168 */     CopiesSupported localCopiesSupported = (CopiesSupported)getSupportedAttributeValues(Copies.class, null, null);
/*      */ 
/* 1170 */     int[][] arrayOfInt = localCopiesSupported.getMembers();
/*      */     int i;
/*      */     int j;
/* 1172 */     if ((arrayOfInt.length > 0) && (arrayOfInt[0].length > 0)) {
/* 1173 */       i = arrayOfInt[0][0];
/* 1174 */       j = arrayOfInt[0][1];
/*      */     } else {
/* 1176 */       i = 1;
/* 1177 */       j = MAXCOPIES;
/*      */     }
/*      */ 
/* 1180 */     int k = paramCopies.getValue();
/* 1181 */     return (k >= i) && (k <= j);
/*      */   }
/*      */ 
/*      */   private boolean isAutoSense(DocFlavor paramDocFlavor) {
/* 1185 */     if ((paramDocFlavor.equals(DocFlavor.BYTE_ARRAY.AUTOSENSE)) || (paramDocFlavor.equals(DocFlavor.INPUT_STREAM.AUTOSENSE)) || (paramDocFlavor.equals(DocFlavor.URL.AUTOSENSE)))
/*      */     {
/* 1188 */       return true;
/*      */     }
/*      */ 
/* 1191 */     return false;
/*      */   }
/*      */ 
/*      */   private synchronized boolean isSupportedMediaTray(MediaTray paramMediaTray)
/*      */   {
/* 1196 */     initAttributes();
/*      */ 
/* 1198 */     if (this.mediaTrays != null) {
/* 1199 */       for (int i = 0; i < this.mediaTrays.length; i++) {
/* 1200 */         if (paramMediaTray.equals(this.mediaTrays[i])) {
/* 1201 */           return true;
/*      */         }
/*      */       }
/*      */     }
/* 1205 */     return false;
/*      */   }
/*      */ 
/*      */   private synchronized boolean isSupportedMedia(MediaSizeName paramMediaSizeName) {
/* 1209 */     initAttributes();
/*      */ 
/* 1211 */     if (paramMediaSizeName.equals((Media)getDefaultAttributeValue(Media.class))) {
/* 1212 */       return true;
/*      */     }
/* 1214 */     for (int i = 0; i < this.mediaSizeNames.length; i++) {
/* 1215 */       debug_println("IPPPrintService>> isSupportedMedia, mediaSizeNames[i] " + this.mediaSizeNames[i]);
/* 1216 */       if (paramMediaSizeName.equals(this.mediaSizeNames[i])) {
/* 1217 */         return true;
/*      */       }
/*      */     }
/* 1220 */     return false;
/*      */   }
/*      */ 
/*      */   private boolean isDestinationSupported(DocFlavor paramDocFlavor, AttributeSet paramAttributeSet)
/*      */   {
/* 1229 */     if ((paramAttributeSet != null) && (paramAttributeSet.get(Destination.class) != null) && (paramDocFlavor != null) && (!paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE)) && (!paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)))
/*      */     {
/* 1234 */       return false;
/*      */     }
/* 1236 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean isAttributeValueSupported(Attribute paramAttribute, DocFlavor paramDocFlavor, AttributeSet paramAttributeSet)
/*      */   {
/* 1243 */     if (paramAttribute == null) {
/* 1244 */       throw new NullPointerException("null attribute");
/*      */     }
/* 1246 */     if (paramDocFlavor != null) {
/* 1247 */       if (!isDocFlavorSupported(paramDocFlavor)) {
/* 1248 */         throw new IllegalArgumentException(paramDocFlavor + " is an unsupported flavor");
/*      */       }
/* 1250 */       if (isAutoSense(paramDocFlavor)) {
/* 1251 */         return false;
/*      */       }
/*      */     }
/* 1254 */     Class localClass = paramAttribute.getCategory();
/* 1255 */     if (!isAttributeCategorySupported(localClass)) {
/* 1256 */       return false;
/*      */     }
/*      */ 
/* 1260 */     if (!isDestinationSupported(paramDocFlavor, paramAttributeSet)) {
/* 1261 */       return false;
/*      */     }
/*      */ 
/* 1265 */     if (paramAttribute.getCategory() == Chromaticity.class) {
/* 1266 */       if ((paramDocFlavor == null) || (paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE)) || (paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) || (!isIPPSupportedImages(paramDocFlavor.getMimeType())))
/*      */       {
/* 1270 */         return paramAttribute == Chromaticity.COLOR;
/*      */       }
/* 1272 */       return false;
/*      */     }
/* 1274 */     if (paramAttribute.getCategory() == Copies.class)
/* 1275 */       return ((paramDocFlavor == null) || ((!paramDocFlavor.equals(DocFlavor.INPUT_STREAM.POSTSCRIPT)) && (!paramDocFlavor.equals(DocFlavor.URL.POSTSCRIPT)) && (!paramDocFlavor.equals(DocFlavor.BYTE_ARRAY.POSTSCRIPT)))) && (isSupportedCopies((Copies)paramAttribute));
/*      */     Object localObject;
/* 1281 */     if (paramAttribute.getCategory() == Destination.class) {
/* 1282 */       if ((paramDocFlavor == null) || (paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE)) || (paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)))
/*      */       {
/* 1285 */         localObject = ((Destination)paramAttribute).getURI();
/* 1286 */         if (("file".equals(((URI)localObject).getScheme())) && (!((URI)localObject).getSchemeSpecificPart().equals("")))
/*      */         {
/* 1288 */           return true;
/*      */         }
/*      */       }
/* 1291 */       return false;
/* 1292 */     }if (paramAttribute.getCategory() == Media.class) {
/* 1293 */       if ((paramAttribute instanceof MediaSizeName)) {
/* 1294 */         return isSupportedMedia((MediaSizeName)paramAttribute);
/*      */       }
/* 1296 */       if ((paramAttribute instanceof MediaTray))
/* 1297 */         return isSupportedMediaTray((MediaTray)paramAttribute);
/*      */     }
/* 1299 */     else if (paramAttribute.getCategory() == PageRanges.class) {
/* 1300 */       if ((paramDocFlavor != null) && (!paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE)) && (!paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)))
/*      */       {
/* 1303 */         return false;
/*      */       }
/* 1305 */     } else if (paramAttribute.getCategory() == SheetCollate.class) {
/* 1306 */       if ((paramDocFlavor != null) && (!paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE)) && (!paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)))
/*      */       {
/* 1309 */         return false;
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*      */       int i;
/* 1311 */       if (paramAttribute.getCategory() == Sides.class) {
/* 1312 */         localObject = (Sides[])getSupportedAttributeValues(Sides.class, paramDocFlavor, paramAttributeSet);
/*      */ 
/* 1317 */         if (localObject != null) {
/* 1318 */           for (i = 0; i < localObject.length; i++) {
/* 1319 */             if (localObject[i] == (Sides)paramAttribute) {
/* 1320 */               return true;
/*      */             }
/*      */           }
/*      */         }
/* 1324 */         return false;
/* 1325 */       }if (paramAttribute.getCategory() == OrientationRequested.class) {
/* 1326 */         localObject = (OrientationRequested[])getSupportedAttributeValues(OrientationRequested.class, paramDocFlavor, paramAttributeSet);
/*      */ 
/* 1332 */         if (localObject != null) {
/* 1333 */           for (i = 0; i < localObject.length; i++) {
/* 1334 */             if (localObject[i] == (OrientationRequested)paramAttribute) {
/* 1335 */               return true;
/*      */             }
/*      */           }
/*      */         }
/* 1339 */         return false;
/*      */       }
/*      */     }
/* 1341 */     return true;
/*      */   }
/*      */ 
/*      */   public synchronized Object getDefaultAttributeValue(Class<? extends Attribute> paramClass)
/*      */   {
/* 1348 */     if (paramClass == null) {
/* 1349 */       throw new NullPointerException("null category");
/*      */     }
/* 1351 */     if (!Attribute.class.isAssignableFrom(paramClass)) {
/* 1352 */       throw new IllegalArgumentException(paramClass + " is not an Attribute");
/*      */     }
/*      */ 
/* 1355 */     if (!isAttributeCategorySupported(paramClass)) {
/* 1356 */       return null;
/*      */     }
/*      */ 
/* 1359 */     initAttributes();
/*      */ 
/* 1361 */     String str1 = null;
/* 1362 */     for (int i = 0; i < printReqAttribDefault.length; i++) {
/* 1363 */       localPrintRequestAttribute = (PrintRequestAttribute)printReqAttribDefault[i];
/*      */ 
/* 1365 */       if (localPrintRequestAttribute.getCategory() == paramClass) {
/* 1366 */         str1 = localPrintRequestAttribute.getName();
/* 1367 */         break;
/*      */       }
/*      */     }
/* 1370 */     String str2 = str1 + "-default";
/* 1371 */     PrintRequestAttribute localPrintRequestAttribute = this.getAttMap != null ? (AttributeClass)this.getAttMap.get(str2) : null;
/*      */ 
/* 1374 */     if (paramClass == Copies.class) {
/* 1375 */       if (localPrintRequestAttribute != null) {
/* 1376 */         return new Copies(localPrintRequestAttribute.getIntValue());
/*      */       }
/* 1378 */       return new Copies(1);
/*      */     }
/* 1380 */     if (paramClass == Chromaticity.class)
/* 1381 */       return Chromaticity.COLOR;
/* 1382 */     if (paramClass == Destination.class)
/*      */       try {
/* 1384 */         return new Destination(new File("out.ps").toURI());
/*      */       } catch (SecurityException localSecurityException1) {
/*      */         try {
/* 1387 */           return new Destination(new URI("file:out.ps"));
/*      */         } catch (URISyntaxException localURISyntaxException) {
/* 1389 */           return null;
/*      */         }
/*      */       }
/* 1392 */     if (paramClass == Fidelity.class)
/* 1393 */       return Fidelity.FIDELITY_FALSE;
/* 1394 */     if (paramClass == Finishings.class)
/* 1395 */       return Finishings.NONE;
/* 1396 */     if (paramClass == JobName.class)
/* 1397 */       return new JobName("Java Printing", null);
/* 1398 */     if (paramClass == JobSheets.class) {
/* 1399 */       if ((localPrintRequestAttribute != null) && (localPrintRequestAttribute.getStringValue().equals("none")))
/*      */       {
/* 1401 */         return JobSheets.NONE;
/*      */       }
/* 1403 */       return JobSheets.STANDARD;
/*      */     }
/*      */     Object localObject;
/* 1405 */     if (paramClass == Media.class) {
/* 1406 */       this.defaultMediaIndex = 0;
/* 1407 */       if (this.mediaSizeNames.length == 0) {
/* 1408 */         localObject = Locale.getDefault().getCountry();
/* 1409 */         if ((localObject != null) && ((((String)localObject).equals("")) || (((String)localObject).equals(Locale.US.getCountry())) || (((String)localObject).equals(Locale.CANADA.getCountry()))))
/*      */         {
/* 1413 */           return MediaSizeName.NA_LETTER;
/*      */         }
/* 1415 */         return MediaSizeName.ISO_A4;
/*      */       }
/*      */ 
/* 1419 */       if (localPrintRequestAttribute != null) {
/* 1420 */         localObject = localPrintRequestAttribute.getStringValue();
/*      */         int j;
/* 1421 */         if (this.isCupsPrinter)
/* 1422 */           for (j = 0; j < this.customMediaSizeNames.length; j++)
/*      */           {
/* 1427 */             if (this.customMediaSizeNames[j].toString().indexOf((String)localObject) != -1)
/*      */             {
/* 1429 */               this.defaultMediaIndex = j;
/* 1430 */               return this.mediaSizeNames[this.defaultMediaIndex];
/*      */             }
/*      */           }
/*      */         else {
/* 1434 */           for (j = 0; j < this.mediaSizeNames.length; j++) {
/* 1435 */             if (this.mediaSizeNames[j].toString().indexOf((String)localObject) != -1) {
/* 1436 */               this.defaultMediaIndex = j;
/* 1437 */               return this.mediaSizeNames[this.defaultMediaIndex];
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 1442 */       return this.mediaSizeNames[this.defaultMediaIndex];
/*      */     }
/* 1444 */     if (paramClass == MediaPrintableArea.class)
/*      */     {
/* 1446 */       if ((this.cps != null) && ((localObject = this.cps.getMediaPrintableArea()) != null))
/*      */       {
/* 1448 */         if (this.defaultMediaIndex == -1)
/*      */         {
/* 1450 */           getDefaultAttributeValue(Media.class);
/*      */         }
/* 1452 */         return localObject[this.defaultMediaIndex];
/*      */       }
/* 1454 */       String str3 = Locale.getDefault().getCountry();
/*      */       float f1;
/*      */       float f2;
/* 1456 */       if ((str3 != null) && ((str3.equals("")) || (str3.equals(Locale.US.getCountry())) || (str3.equals(Locale.CANADA.getCountry()))))
/*      */       {
/* 1460 */         f1 = MediaSize.NA.LETTER.getX(25400) - 0.5F;
/* 1461 */         f2 = MediaSize.NA.LETTER.getY(25400) - 0.5F;
/*      */       } else {
/* 1463 */         f1 = MediaSize.ISO.A4.getX(25400) - 0.5F;
/* 1464 */         f2 = MediaSize.ISO.A4.getY(25400) - 0.5F;
/*      */       }
/* 1466 */       return new MediaPrintableArea(0.25F, 0.25F, f1, f2, 25400);
/*      */     }
/*      */ 
/* 1469 */     if (paramClass == NumberUp.class)
/* 1470 */       return new NumberUp(1);
/* 1471 */     if (paramClass == OrientationRequested.class) {
/* 1472 */       if (localPrintRequestAttribute != null) {
/* 1473 */         switch (localPrintRequestAttribute.getIntValue()) { case 3:
/*      */         default:
/* 1475 */           return OrientationRequested.PORTRAIT;
/*      */         case 4:
/* 1476 */           return OrientationRequested.LANDSCAPE;
/*      */         case 5:
/* 1477 */           return OrientationRequested.REVERSE_LANDSCAPE;
/* 1478 */         case 6: } return OrientationRequested.REVERSE_PORTRAIT;
/*      */       }
/*      */ 
/* 1481 */       return OrientationRequested.PORTRAIT;
/*      */     }
/* 1483 */     if (paramClass == PageRanges.class) {
/* 1484 */       if (localPrintRequestAttribute != null) {
/* 1485 */         localObject = localPrintRequestAttribute.getIntRangeValue();
/* 1486 */         return new PageRanges(localObject[0], localObject[1]);
/*      */       }
/* 1488 */       return new PageRanges(1, 2147483647);
/*      */     }
/* 1490 */     if (paramClass == RequestingUserName.class) {
/* 1491 */       localObject = "";
/*      */       try {
/* 1493 */         localObject = System.getProperty("user.name", "");
/*      */       } catch (SecurityException localSecurityException2) {
/*      */       }
/* 1496 */       return new RequestingUserName((String)localObject, null);
/* 1497 */     }if (paramClass == SheetCollate.class)
/* 1498 */       return SheetCollate.UNCOLLATED;
/* 1499 */     if (paramClass == Sides.class) {
/* 1500 */       if (localPrintRequestAttribute != null) {
/* 1501 */         if (localPrintRequestAttribute.getStringValue().endsWith("long-edge"))
/* 1502 */           return Sides.TWO_SIDED_LONG_EDGE;
/* 1503 */         if (localPrintRequestAttribute.getStringValue().endsWith("short-edge"))
/*      */         {
/* 1505 */           return Sides.TWO_SIDED_SHORT_EDGE;
/*      */         }
/*      */       }
/* 1508 */       return Sides.ONE_SIDED;
/*      */     }
/*      */ 
/* 1511 */     return null;
/*      */   }
/*      */ 
/*      */   public ServiceUIFactory getServiceUIFactory() {
/* 1515 */     return null;
/*      */   }
/*      */ 
/*      */   public void wakeNotifier() {
/* 1519 */     synchronized (this) {
/* 1520 */       if (this.notifier != null)
/* 1521 */         this.notifier.wake();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addPrintServiceAttributeListener(PrintServiceAttributeListener paramPrintServiceAttributeListener)
/*      */   {
/* 1528 */     synchronized (this) {
/* 1529 */       if (paramPrintServiceAttributeListener == null) {
/* 1530 */         return;
/*      */       }
/* 1532 */       if (this.notifier == null) {
/* 1533 */         this.notifier = new ServiceNotifier(this);
/*      */       }
/* 1535 */       this.notifier.addListener(paramPrintServiceAttributeListener);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void removePrintServiceAttributeListener(PrintServiceAttributeListener paramPrintServiceAttributeListener)
/*      */   {
/* 1541 */     synchronized (this) {
/* 1542 */       if ((paramPrintServiceAttributeListener == null) || (this.notifier == null)) {
/* 1543 */         return;
/*      */       }
/* 1545 */       this.notifier.removeListener(paramPrintServiceAttributeListener);
/* 1546 */       if (this.notifier.isEmpty()) {
/* 1547 */         this.notifier.stopNotifier();
/* 1548 */         this.notifier = null;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getName() {
/* 1554 */     return this.printer;
/*      */   }
/*      */ 
/*      */   public boolean usesClass(Class paramClass)
/*      */   {
/* 1559 */     return paramClass == PSPrinterJob.class;
/*      */   }
/*      */ 
/*      */   public static HttpURLConnection getIPPConnection(URL paramURL)
/*      */   {
/*      */     HttpURLConnection localHttpURLConnection;
/*      */     try {
/* 1566 */       localHttpURLConnection = (HttpURLConnection)paramURL.openConnection();
/*      */     } catch (IOException localIOException) {
/* 1568 */       return null;
/*      */     }
/* 1570 */     if (!(localHttpURLConnection instanceof HttpURLConnection)) {
/* 1571 */       return null;
/*      */     }
/* 1573 */     localHttpURLConnection.setUseCaches(false);
/* 1574 */     localHttpURLConnection.setDefaultUseCaches(false);
/* 1575 */     localHttpURLConnection.setDoInput(true);
/* 1576 */     localHttpURLConnection.setDoOutput(true);
/* 1577 */     localHttpURLConnection.setRequestProperty("Content-type", "application/ipp");
/* 1578 */     return localHttpURLConnection;
/*      */   }
/*      */ 
/*      */   public synchronized boolean isPostscript()
/*      */   {
/* 1583 */     if (this.isPS == null) {
/* 1584 */       this.isPS = Boolean.TRUE;
/* 1585 */       if (this.isCupsPrinter) {
/*      */         try {
/* 1587 */           this.urlConnection = getIPPConnection(new URL(this.myURL + ".ppd"));
/*      */ 
/* 1590 */           InputStream localInputStream = this.urlConnection.getInputStream();
/* 1591 */           if (localInputStream != null) {
/* 1592 */             BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(localInputStream, Charset.forName("ISO-8859-1")));
/*      */             String str;
/* 1596 */             while ((str = localBufferedReader.readLine()) != null) {
/* 1597 */               if (str.startsWith("*cupsFilter:"))
/* 1598 */                 this.isPS = Boolean.FALSE;
/*      */             }
/*      */           }
/*      */         }
/*      */         catch (IOException localIOException)
/*      */         {
/* 1604 */           debug_println(" isPostscript, e= " + localIOException);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1612 */     return this.isPS.booleanValue();
/*      */   }
/*      */ 
/*      */   private void opGetAttributes()
/*      */   {
/*      */     try {
/* 1618 */       debug_println("IPPPrintService>> opGetAttributes myURI " + this.myURI + " myURL " + this.myURL);
/*      */ 
/* 1620 */       AttributeClass[] arrayOfAttributeClass1 = { AttributeClass.ATTRIBUTES_CHARSET, AttributeClass.ATTRIBUTES_NATURAL_LANGUAGE };
/*      */ 
/* 1624 */       AttributeClass[] arrayOfAttributeClass2 = { AttributeClass.ATTRIBUTES_CHARSET, AttributeClass.ATTRIBUTES_NATURAL_LANGUAGE, new AttributeClass("printer-uri", 69, "" + this.myURI) };
/*      */ 
/* 1631 */       OutputStream localOutputStream = (OutputStream)AccessController.doPrivileged(new PrivilegedAction()
/*      */       {
/*      */         public Object run() {
/*      */           try {
/* 1635 */             return IPPPrintService.this.urlConnection.getOutputStream();
/*      */           } catch (Exception localException) {
/*      */           }
/* 1638 */           return null;
/*      */         }
/*      */       });
/* 1642 */       if (localOutputStream == null) {
/* 1643 */         return;
/*      */       }
/*      */ 
/* 1646 */       boolean bool = this.myURI == null ? writeIPPRequest(localOutputStream, "000B", arrayOfAttributeClass1) : writeIPPRequest(localOutputStream, "000B", arrayOfAttributeClass2);
/*      */ 
/* 1649 */       if (bool) {
/* 1650 */         InputStream localInputStream = null;
/* 1651 */         if ((localInputStream = this.urlConnection.getInputStream()) != null) {
/* 1652 */           HashMap[] arrayOfHashMap = readIPPResponse(localInputStream);
/*      */ 
/* 1654 */           if ((arrayOfHashMap != null) && (arrayOfHashMap.length > 0))
/* 1655 */             this.getAttMap = arrayOfHashMap[0];
/*      */         }
/*      */         else {
/* 1658 */           debug_println("IPPPrintService>> opGetAttributes - null input stream");
/*      */         }
/* 1660 */         localInputStream.close();
/*      */       }
/* 1662 */       localOutputStream.close();
/*      */     } catch (IOException localIOException) {
/* 1664 */       debug_println("IPPPrintService>> opGetAttributes - input/output stream: " + localIOException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static boolean writeIPPRequest(OutputStream paramOutputStream, String paramString, AttributeClass[] paramArrayOfAttributeClass)
/*      */   {
/*      */     OutputStreamWriter localOutputStreamWriter;
/*      */     try
/*      */     {
/* 1674 */       localOutputStreamWriter = new OutputStreamWriter(paramOutputStream, "UTF-8");
/*      */     } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/* 1676 */       debug_println("IPPPrintService>> writeIPPRequest, UTF-8 not supported? Exception: " + localUnsupportedEncodingException);
/* 1677 */       return false;
/*      */     }
/* 1679 */     debug_println("IPPPrintService>> writeIPPRequest, op code= " + paramString);
/* 1680 */     char[] arrayOfChar1 = new char[2];
/* 1681 */     arrayOfChar1[0] = ((char)Byte.parseByte(paramString.substring(0, 2), 16));
/* 1682 */     arrayOfChar1[1] = ((char)Byte.parseByte(paramString.substring(2, 4), 16));
/* 1683 */     char[] arrayOfChar2 = { '\001', '\001', '\000', '\001' };
/*      */     try {
/* 1685 */       localOutputStreamWriter.write(arrayOfChar2, 0, 2);
/* 1686 */       localOutputStreamWriter.write(arrayOfChar1, 0, 2);
/* 1687 */       arrayOfChar2[0] = '\000'; arrayOfChar2[1] = '\000';
/* 1688 */       localOutputStreamWriter.write(arrayOfChar2, 0, 4);
/*      */ 
/* 1690 */       arrayOfChar2[0] = '\001';
/* 1691 */       localOutputStreamWriter.write(arrayOfChar2[0]);
/*      */ 
/* 1697 */       for (int i = 0; i < paramArrayOfAttributeClass.length; i++) {
/* 1698 */         AttributeClass localAttributeClass = paramArrayOfAttributeClass[i];
/* 1699 */         localOutputStreamWriter.write(localAttributeClass.getType());
/*      */ 
/* 1701 */         char[] arrayOfChar3 = localAttributeClass.getLenChars();
/* 1702 */         localOutputStreamWriter.write(arrayOfChar3, 0, 2);
/* 1703 */         localOutputStreamWriter.write("" + localAttributeClass, 0, localAttributeClass.getName().length());
/*      */ 
/* 1706 */         if ((localAttributeClass.getType() >= 53) && (localAttributeClass.getType() <= 73))
/*      */         {
/* 1708 */           String str = (String)localAttributeClass.getObjectValue();
/* 1709 */           arrayOfChar2[0] = '\000'; arrayOfChar2[1] = ((char)str.length());
/* 1710 */           localOutputStreamWriter.write(arrayOfChar2, 0, 2);
/* 1711 */           localOutputStreamWriter.write(str, 0, str.length());
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1716 */       localOutputStreamWriter.write(3);
/* 1717 */       localOutputStreamWriter.flush();
/* 1718 */       localOutputStreamWriter.close();
/*      */     } catch (IOException localIOException) {
/* 1720 */       debug_println("IPPPrintService>> writeIPPRequest, IPPPrintService Exception in writeIPPRequest: " + localIOException);
/* 1721 */       return false;
/*      */     }
/* 1723 */     return true;
/*      */   }
/*      */ 
/*      */   public static HashMap[] readIPPResponse(InputStream paramInputStream)
/*      */   {
/* 1729 */     if (paramInputStream == null) {
/* 1730 */       return null;
/*      */     }
/*      */ 
/* 1733 */     byte[] arrayOfByte1 = new byte[MAX_ATTRIBUTE_LENGTH];
/*      */     try
/*      */     {
/* 1736 */       DataInputStream localDataInputStream = new DataInputStream(paramInputStream);
/*      */ 
/* 1739 */       if ((localDataInputStream.read(arrayOfByte1, 0, 8) > -1) && (arrayOfByte1[2] == 0))
/*      */       {
/* 1743 */         int i = 0;
/* 1744 */         int j = 0;
/* 1745 */         Object localObject = null;
/*      */ 
/* 1747 */         int k = 68;
/* 1748 */         ArrayList localArrayList = new ArrayList();
/* 1749 */         HashMap localHashMap = new HashMap();
/*      */ 
/* 1751 */         arrayOfByte1[0] = localDataInputStream.readByte();
/*      */ 
/* 1756 */         while ((arrayOfByte1[0] >= 1) && (arrayOfByte1[0] <= 4) && (arrayOfByte1[0] != 3)) {
/* 1757 */           debug_println("IPPPrintService>> readIPPResponse, checking group tag,  response[0]= " + arrayOfByte1[0]);
/*      */ 
/* 1760 */           ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
/*      */ 
/* 1762 */           i = 0;
/* 1763 */           localObject = null;
/*      */ 
/* 1766 */           arrayOfByte1[0] = localDataInputStream.readByte();
/*      */           byte[] arrayOfByte2;
/*      */           AttributeClass localAttributeClass;
/* 1767 */           while ((arrayOfByte1[0] >= 16) && (arrayOfByte1[0] <= 74))
/*      */           {
/* 1770 */             j = localDataInputStream.readShort();
/*      */ 
/* 1775 */             if ((j != 0) && (localObject != null))
/*      */             {
/* 1777 */               localByteArrayOutputStream.write(i);
/* 1778 */               localByteArrayOutputStream.flush();
/* 1779 */               localByteArrayOutputStream.close();
/* 1780 */               arrayOfByte2 = localByteArrayOutputStream.toByteArray();
/*      */ 
/* 1783 */               if (localHashMap.containsKey(localObject)) {
/* 1784 */                 localArrayList.add(localHashMap);
/* 1785 */                 localHashMap = new HashMap();
/*      */               }
/*      */ 
/* 1789 */               if (k >= 33) {
/* 1790 */                 localAttributeClass = new AttributeClass((String)localObject, k, arrayOfByte2);
/*      */ 
/* 1795 */                 localHashMap.put(localAttributeClass.getName(), localAttributeClass);
/* 1796 */                 debug_println("IPPPrintService>> readIPPResponse " + localAttributeClass);
/*      */               }
/*      */ 
/* 1799 */               localByteArrayOutputStream = new ByteArrayOutputStream();
/* 1800 */               i = 0;
/*      */             }
/*      */ 
/* 1803 */             if (i == 0) {
/* 1804 */               k = arrayOfByte1[0];
/*      */             }
/*      */ 
/* 1807 */             if (j != 0)
/*      */             {
/* 1810 */               if (j > MAX_ATTRIBUTE_LENGTH) {
/* 1811 */                 arrayOfByte1 = new byte[j];
/*      */               }
/* 1813 */               localDataInputStream.read(arrayOfByte1, 0, j);
/* 1814 */               localObject = new String(arrayOfByte1, 0, j);
/*      */             }
/*      */ 
/* 1817 */             j = localDataInputStream.readShort();
/*      */ 
/* 1819 */             localByteArrayOutputStream.write(j);
/*      */ 
/* 1821 */             if (j > MAX_ATTRIBUTE_LENGTH) {
/* 1822 */               arrayOfByte1 = new byte[j];
/*      */             }
/* 1824 */             localDataInputStream.read(arrayOfByte1, 0, j);
/*      */ 
/* 1826 */             localByteArrayOutputStream.write(arrayOfByte1, 0, j);
/* 1827 */             i++;
/*      */ 
/* 1829 */             arrayOfByte1[0] = localDataInputStream.readByte();
/*      */           }
/*      */ 
/* 1832 */           if (localObject != null) {
/* 1833 */             localByteArrayOutputStream.write(i);
/* 1834 */             localByteArrayOutputStream.flush();
/* 1835 */             localByteArrayOutputStream.close();
/*      */ 
/* 1838 */             if ((i != 0) && (localHashMap.containsKey(localObject)))
/*      */             {
/* 1840 */               localArrayList.add(localHashMap);
/* 1841 */               localHashMap = new HashMap();
/*      */             }
/*      */ 
/* 1844 */             arrayOfByte2 = localByteArrayOutputStream.toByteArray();
/*      */ 
/* 1846 */             localAttributeClass = new AttributeClass((String)localObject, k, arrayOfByte2);
/*      */ 
/* 1850 */             localHashMap.put(localAttributeClass.getName(), localAttributeClass);
/*      */           }
/*      */         }
/* 1853 */         localDataInputStream.close();
/* 1854 */         if ((localHashMap != null) && (localHashMap.size() > 0)) {
/* 1855 */           localArrayList.add(localHashMap);
/*      */         }
/* 1857 */         return (HashMap[])localArrayList.toArray(new HashMap[localArrayList.size()]);
/*      */       }
/*      */ 
/* 1860 */       debug_println("IPPPrintService>> readIPPResponse client error, IPP status code-" + Integer.toHexString(arrayOfByte1[2]) + " & " + Integer.toHexString(arrayOfByte1[3]));
/*      */ 
/* 1864 */       return null;
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 1868 */       debug_println("IPPPrintService>> readIPPResponse: " + localIOException);
/* 1869 */       if (debugPrint)
/* 1870 */         localIOException.printStackTrace();
/*      */     }
/* 1872 */     return null;
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/* 1878 */     return "IPP Printer : " + getName();
/*      */   }
/*      */ 
/*      */   public boolean equals(Object paramObject) {
/* 1882 */     return (paramObject == this) || (((paramObject instanceof IPPPrintService)) && (((IPPPrintService)paramObject).getName().equals(getName())));
/*      */   }
/*      */ 
/*      */   public int hashCode()
/*      */   {
/* 1888 */     return getClass().hashCode() + getName().hashCode();
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   76 */     String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.print.ippdebug"));
/*      */   }
/*      */ 
/*      */   private class ExtFinishing extends Finishings
/*      */   {
/*      */     ExtFinishing(int arg2)
/*      */     {
/*  776 */       super();
/*      */     }
/*      */ 
/*      */     EnumSyntax[] getAll() {
/*  780 */       EnumSyntax[] arrayOfEnumSyntax = super.getEnumValueTable();
/*  781 */       return arrayOfEnumSyntax;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.print.IPPPrintService
 * JD-Core Version:    0.6.2
 */