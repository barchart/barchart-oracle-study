/*     */ package sun.print;
/*     */ 
/*     */ import java.awt.print.PageFormat;
/*     */ import java.awt.print.Pageable;
/*     */ import java.awt.print.Paper;
/*     */ import java.awt.print.Printable;
/*     */ import java.awt.print.PrinterException;
/*     */ import java.awt.print.PrinterJob;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintStream;
/*     */ import java.io.Reader;
/*     */ import java.io.Writer;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.attribute.FileAttribute;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Vector;
/*     */ import javax.print.CancelablePrintJob;
/*     */ import javax.print.Doc;
/*     */ import javax.print.DocFlavor;
/*     */ import javax.print.DocFlavor.BYTE_ARRAY;
/*     */ import javax.print.DocFlavor.CHAR_ARRAY;
/*     */ import javax.print.DocFlavor.INPUT_STREAM;
/*     */ import javax.print.DocFlavor.READER;
/*     */ import javax.print.DocFlavor.SERVICE_FORMATTED;
/*     */ import javax.print.DocFlavor.STRING;
/*     */ import javax.print.DocFlavor.URL;
/*     */ import javax.print.PrintException;
/*     */ import javax.print.PrintService;
/*     */ import javax.print.attribute.Attribute;
/*     */ import javax.print.attribute.AttributeSetUtilities;
/*     */ import javax.print.attribute.DocAttributeSet;
/*     */ import javax.print.attribute.HashPrintJobAttributeSet;
/*     */ import javax.print.attribute.HashPrintRequestAttributeSet;
/*     */ import javax.print.attribute.PrintJobAttribute;
/*     */ import javax.print.attribute.PrintJobAttributeSet;
/*     */ import javax.print.attribute.PrintRequestAttribute;
/*     */ import javax.print.attribute.PrintRequestAttributeSet;
/*     */ import javax.print.attribute.standard.Copies;
/*     */ import javax.print.attribute.standard.Destination;
/*     */ import javax.print.attribute.standard.DocumentName;
/*     */ import javax.print.attribute.standard.Fidelity;
/*     */ import javax.print.attribute.standard.JobName;
/*     */ import javax.print.attribute.standard.JobOriginatingUserName;
/*     */ import javax.print.attribute.standard.JobSheets;
/*     */ import javax.print.attribute.standard.Media;
/*     */ import javax.print.attribute.standard.MediaSize;
/*     */ import javax.print.attribute.standard.MediaSize.NA;
/*     */ import javax.print.attribute.standard.MediaSizeName;
/*     */ import javax.print.attribute.standard.NumberUp;
/*     */ import javax.print.attribute.standard.OrientationRequested;
/*     */ import javax.print.attribute.standard.PrinterIsAcceptingJobs;
/*     */ import javax.print.attribute.standard.RequestingUserName;
/*     */ import javax.print.attribute.standard.Sides;
/*     */ import javax.print.event.PrintJobAttributeListener;
/*     */ import javax.print.event.PrintJobEvent;
/*     */ import javax.print.event.PrintJobListener;
/*     */ 
/*     */ public class UnixPrintJob
/*     */   implements CancelablePrintJob
/*     */ {
/*  87 */   private static String debugPrefix = "UnixPrintJob>> ";
/*     */   private transient Vector jobListeners;
/*     */   private transient Vector attrListeners;
/*     */   private transient Vector listenedAttributeSets;
/*     */   private PrintService service;
/*     */   private boolean fidelity;
/*  95 */   private boolean printing = false;
/*  96 */   private boolean printReturned = false;
/*  97 */   private PrintRequestAttributeSet reqAttrSet = null;
/*  98 */   private PrintJobAttributeSet jobAttrSet = null;
/*     */   private PrinterJob job;
/*     */   private Doc doc;
/* 105 */   private InputStream instream = null;
/* 106 */   private Reader reader = null;
/*     */ 
/* 109 */   private String jobName = "Java Printing";
/* 110 */   private int copies = 1;
/* 111 */   private MediaSizeName mediaName = MediaSizeName.NA_LETTER;
/* 112 */   private MediaSize mediaSize = MediaSize.NA.LETTER;
/* 113 */   private CustomMediaTray customTray = null;
/* 114 */   private OrientationRequested orient = OrientationRequested.PORTRAIT;
/* 115 */   private NumberUp nUp = null;
/* 116 */   private Sides sides = null;
/*     */ 
/* 916 */   private static int DESTPRINTER = 1;
/* 917 */   private static int DESTFILE = 2;
/* 918 */   private int mDestType = DESTPRINTER;
/*     */   private File spoolFile;
/*     */   private String mDestination;
/* 921 */   private String mOptions = "";
/* 922 */   private boolean mNoJobSheet = false;
/*     */ 
/*     */   UnixPrintJob(PrintService paramPrintService)
/*     */   {
/* 119 */     this.service = paramPrintService;
/* 120 */     this.mDestination = paramPrintService.getName();
/* 121 */     this.mDestType = DESTPRINTER;
/*     */   }
/*     */ 
/*     */   public PrintService getPrintService() {
/* 125 */     return this.service;
/*     */   }
/*     */ 
/*     */   public PrintJobAttributeSet getAttributes() {
/* 129 */     synchronized (this) {
/* 130 */       if (this.jobAttrSet == null)
/*     */       {
/* 132 */         HashPrintJobAttributeSet localHashPrintJobAttributeSet = new HashPrintJobAttributeSet();
/* 133 */         return AttributeSetUtilities.unmodifiableView(localHashPrintJobAttributeSet);
/*     */       }
/* 135 */       return this.jobAttrSet;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addPrintJobListener(PrintJobListener paramPrintJobListener)
/*     */   {
/* 141 */     synchronized (this) {
/* 142 */       if (paramPrintJobListener == null) {
/* 143 */         return;
/*     */       }
/* 145 */       if (this.jobListeners == null) {
/* 146 */         this.jobListeners = new Vector();
/*     */       }
/* 148 */       this.jobListeners.add(paramPrintJobListener);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removePrintJobListener(PrintJobListener paramPrintJobListener) {
/* 153 */     synchronized (this) {
/* 154 */       if ((paramPrintJobListener == null) || (this.jobListeners == null)) {
/* 155 */         return;
/*     */       }
/* 157 */       this.jobListeners.remove(paramPrintJobListener);
/* 158 */       if (this.jobListeners.isEmpty())
/* 159 */         this.jobListeners = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void closeDataStreams()
/*     */   {
/* 173 */     if (this.doc == null) {
/* 174 */       return;
/*     */     }
/*     */ 
/* 177 */     Object localObject1 = null;
/*     */     try
/*     */     {
/* 180 */       localObject1 = this.doc.getPrintData();
/*     */     } catch (IOException localIOException1) {
/* 182 */       return;
/*     */     }
/*     */ 
/* 185 */     if (this.instream != null) {
/*     */       try {
/* 187 */         this.instream.close();
/*     */       } catch (IOException localIOException2) {
/*     */       } finally {
/* 190 */         this.instream = null;
/*     */       }
/*     */     }
/* 193 */     else if (this.reader != null) {
/*     */       try {
/* 195 */         this.reader.close();
/*     */       } catch (IOException localIOException3) {
/*     */       } finally {
/* 198 */         this.reader = null;
/*     */       }
/*     */     }
/* 201 */     else if ((localObject1 instanceof InputStream))
/*     */       try {
/* 203 */         ((InputStream)localObject1).close();
/*     */       }
/*     */       catch (IOException localIOException4) {
/*     */       }
/* 207 */     else if ((localObject1 instanceof Reader))
/*     */       try {
/* 209 */         ((Reader)localObject1).close();
/*     */       }
/*     */       catch (IOException localIOException5)
/*     */       {
/*     */       }
/*     */   }
/*     */ 
/*     */   private void notifyEvent(int paramInt)
/*     */   {
/* 220 */     switch (paramInt) {
/*     */     case 101:
/*     */     case 102:
/*     */     case 103:
/*     */     case 105:
/*     */     case 106:
/* 226 */       closeDataStreams();
/*     */     case 104:
/*     */     }
/* 229 */     synchronized (this) {
/* 230 */       if (this.jobListeners != null)
/*     */       {
/* 232 */         PrintJobEvent localPrintJobEvent = new PrintJobEvent(this, paramInt);
/* 233 */         for (int i = 0; i < this.jobListeners.size(); i++) {
/* 234 */           PrintJobListener localPrintJobListener = (PrintJobListener)this.jobListeners.elementAt(i);
/* 235 */           switch (paramInt)
/*     */           {
/*     */           case 101:
/* 238 */             localPrintJobListener.printJobCanceled(localPrintJobEvent);
/* 239 */             break;
/*     */           case 103:
/* 242 */             localPrintJobListener.printJobFailed(localPrintJobEvent);
/* 243 */             break;
/*     */           case 106:
/* 246 */             localPrintJobListener.printDataTransferCompleted(localPrintJobEvent);
/* 247 */             break;
/*     */           case 105:
/* 250 */             localPrintJobListener.printJobNoMoreEvents(localPrintJobEvent);
/*     */           case 102:
/*     */           case 104:
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addPrintJobAttributeListener(PrintJobAttributeListener paramPrintJobAttributeListener, PrintJobAttributeSet paramPrintJobAttributeSet)
/*     */   {
/* 264 */     synchronized (this) {
/* 265 */       if (paramPrintJobAttributeListener == null) {
/* 266 */         return;
/*     */       }
/* 268 */       if (this.attrListeners == null) {
/* 269 */         this.attrListeners = new Vector();
/* 270 */         this.listenedAttributeSets = new Vector();
/*     */       }
/* 272 */       this.attrListeners.add(paramPrintJobAttributeListener);
/* 273 */       if (paramPrintJobAttributeSet == null) {
/* 274 */         paramPrintJobAttributeSet = new HashPrintJobAttributeSet();
/*     */       }
/* 276 */       this.listenedAttributeSets.add(paramPrintJobAttributeSet);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removePrintJobAttributeListener(PrintJobAttributeListener paramPrintJobAttributeListener)
/*     */   {
/* 282 */     synchronized (this) {
/* 283 */       if ((paramPrintJobAttributeListener == null) || (this.attrListeners == null)) {
/* 284 */         return;
/*     */       }
/* 286 */       int i = this.attrListeners.indexOf(paramPrintJobAttributeListener);
/* 287 */       if (i == -1) {
/* 288 */         return;
/*     */       }
/* 290 */       this.attrListeners.remove(i);
/* 291 */       this.listenedAttributeSets.remove(i);
/* 292 */       if (this.attrListeners.isEmpty()) {
/* 293 */         this.attrListeners = null;
/* 294 */         this.listenedAttributeSets = null;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void print(Doc paramDoc, PrintRequestAttributeSet paramPrintRequestAttributeSet)
/*     */     throws PrintException
/*     */   {
/* 303 */     synchronized (this) {
/* 304 */       if (this.printing) {
/* 305 */         throw new PrintException("already printing");
/*     */       }
/* 307 */       this.printing = true;
/*     */     }
/*     */ 
/* 311 */     if ((PrinterIsAcceptingJobs)this.service.getAttribute(PrinterIsAcceptingJobs.class) == PrinterIsAcceptingJobs.NOT_ACCEPTING_JOBS)
/*     */     {
/* 314 */       throw new PrintException("Printer is not accepting job.");
/*     */     }
/*     */ 
/* 317 */     this.doc = paramDoc;
/*     */ 
/* 319 */     ??? = paramDoc.getDocFlavor();
/*     */     Object localObject2;
/*     */     try {
/* 324 */       localObject2 = paramDoc.getPrintData();
/*     */     } catch (IOException localIOException1) {
/* 326 */       notifyEvent(103);
/* 327 */       throw new PrintException("can't get print data: " + localIOException1.toString());
/*     */     }
/*     */ 
/* 330 */     if ((??? == null) || (!this.service.isDocFlavorSupported((DocFlavor)???))) {
/* 331 */       notifyEvent(103);
/* 332 */       throw new PrintJobFlavorException("invalid flavor", (DocFlavor)???);
/*     */     }
/*     */ 
/* 335 */     initializeAttributeSets(paramDoc, paramPrintRequestAttributeSet);
/*     */ 
/* 337 */     getAttributeValues((DocFlavor)???);
/*     */ 
/* 340 */     if (((this.service instanceof IPPPrintService)) && (CUPSPrinter.isCupsRunning()))
/*     */     {
/* 343 */       IPPPrintService.debug_println(debugPrefix + "instanceof IPPPrintService");
/*     */ 
/* 346 */       if (this.mediaName != null) {
/* 347 */         localObject3 = ((IPPPrintService)this.service).findCustomMedia(this.mediaName);
/*     */ 
/* 349 */         if (localObject3 != null) {
/* 350 */           this.mOptions = (" media=" + ((CustomMediaSizeName)localObject3).getChoiceName());
/*     */         }
/*     */       }
/*     */ 
/* 354 */       if ((this.customTray != null) && ((this.customTray instanceof CustomMediaTray)))
/*     */       {
/* 356 */         localObject3 = this.customTray.getChoiceName();
/* 357 */         if (localObject3 != null) {
/* 358 */           this.mOptions = (this.mOptions + " media=" + (String)localObject3);
/*     */         }
/*     */       }
/*     */ 
/* 362 */       if (this.nUp != null) {
/* 363 */         this.mOptions = (this.mOptions + " number-up=" + this.nUp.getValue());
/*     */       }
/*     */ 
/* 366 */       if ((this.orient != OrientationRequested.PORTRAIT) && (??? != null) && (!((DocFlavor)???).equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE)))
/*     */       {
/* 369 */         this.mOptions = (this.mOptions + " orientation-requested=" + this.orient.getValue());
/*     */       }
/*     */ 
/* 372 */       if (this.sides != null) {
/* 373 */         this.mOptions = (this.mOptions + " sides=" + this.sides);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 378 */     IPPPrintService.debug_println(debugPrefix + "mOptions " + this.mOptions);
/* 379 */     Object localObject3 = ((DocFlavor)???).getRepresentationClassName();
/* 380 */     String str1 = ((DocFlavor)???).getParameter("charset");
/* 381 */     String str2 = "us-ascii";
/* 382 */     if ((str1 != null) && (!str1.equals(""))) {
/* 383 */       str2 = str1;
/*     */     }
/*     */ 
/* 386 */     if ((((DocFlavor)???).equals(DocFlavor.INPUT_STREAM.GIF)) || (((DocFlavor)???).equals(DocFlavor.INPUT_STREAM.JPEG)) || (((DocFlavor)???).equals(DocFlavor.INPUT_STREAM.PNG)) || (((DocFlavor)???).equals(DocFlavor.BYTE_ARRAY.GIF)) || (((DocFlavor)???).equals(DocFlavor.BYTE_ARRAY.JPEG)) || (((DocFlavor)???).equals(DocFlavor.BYTE_ARRAY.PNG)))
/*     */     {
/*     */       try
/*     */       {
/* 393 */         this.instream = paramDoc.getStreamForBytes();
/* 394 */         if (this.instream == null) {
/* 395 */           notifyEvent(103);
/* 396 */           throw new PrintException("No stream for data");
/*     */         }
/* 398 */         if ((!(this.service instanceof IPPPrintService)) || (!((IPPPrintService)this.service).isIPPSupportedImages(((DocFlavor)???).getMimeType())))
/*     */         {
/* 401 */           printableJob(new ImagePrinter(this.instream));
/* 402 */           ((UnixPrintService)this.service).wakeNotifier();
/* 403 */           return;
/*     */         }
/*     */       } catch (ClassCastException localClassCastException1) {
/* 406 */         notifyEvent(103);
/* 407 */         throw new PrintException(localClassCastException1);
/*     */       } catch (IOException localIOException2) {
/* 409 */         notifyEvent(103);
/* 410 */         throw new PrintException(localIOException2);
/*     */       }
/* 412 */     } else if ((((DocFlavor)???).equals(DocFlavor.URL.GIF)) || (((DocFlavor)???).equals(DocFlavor.URL.JPEG)) || (((DocFlavor)???).equals(DocFlavor.URL.PNG)))
/*     */     {
/*     */       try
/*     */       {
/* 416 */         URL localURL1 = (URL)localObject2;
/* 417 */         if (((this.service instanceof IPPPrintService)) && (((IPPPrintService)this.service).isIPPSupportedImages(((DocFlavor)???).getMimeType())))
/*     */         {
/* 420 */           this.instream = localURL1.openStream();
/*     */         } else {
/* 422 */           printableJob(new ImagePrinter(localURL1));
/* 423 */           ((UnixPrintService)this.service).wakeNotifier();
/* 424 */           return;
/*     */         }
/*     */       } catch (ClassCastException localClassCastException2) {
/* 427 */         notifyEvent(103);
/* 428 */         throw new PrintException(localClassCastException2);
/*     */       } catch (IOException localIOException3) {
/* 430 */         notifyEvent(103);
/* 431 */         throw new PrintException(localIOException3.toString());
/*     */       }
/* 433 */     } else if ((((DocFlavor)???).equals(DocFlavor.CHAR_ARRAY.TEXT_PLAIN)) || (((DocFlavor)???).equals(DocFlavor.READER.TEXT_PLAIN)) || (((DocFlavor)???).equals(DocFlavor.STRING.TEXT_PLAIN)))
/*     */     {
/*     */       try
/*     */       {
/* 437 */         this.reader = paramDoc.getReaderForText();
/* 438 */         if (this.reader == null) {
/* 439 */           notifyEvent(103);
/* 440 */           throw new PrintException("No reader for data");
/*     */         }
/*     */       } catch (IOException localIOException4) {
/* 443 */         notifyEvent(103);
/* 444 */         throw new PrintException(localIOException4.toString());
/*     */       }
/* 446 */     } else if ((((String)localObject3).equals("[B")) || (((String)localObject3).equals("java.io.InputStream")))
/*     */     {
/*     */       try {
/* 449 */         this.instream = paramDoc.getStreamForBytes();
/* 450 */         if (this.instream == null) {
/* 451 */           notifyEvent(103);
/* 452 */           throw new PrintException("No stream for data");
/*     */         }
/*     */       } catch (IOException localIOException5) {
/* 455 */         notifyEvent(103);
/* 456 */         throw new PrintException(localIOException5.toString());
/*     */       }
/* 458 */     } else if (((String)localObject3).equals("java.net.URL"))
/*     */     {
/* 469 */       URL localURL2 = (URL)localObject2;
/*     */       try {
/* 471 */         this.instream = localURL2.openStream();
/*     */       } catch (IOException localIOException8) {
/* 473 */         notifyEvent(103);
/* 474 */         throw new PrintException(localIOException8.toString());
/*     */       }
/*     */     } else { if (((String)localObject3).equals("java.awt.print.Pageable"))
/*     */         try {
/* 478 */           pageableJob((Pageable)paramDoc.getPrintData());
/* 479 */           if ((this.service instanceof IPPPrintService))
/* 480 */             ((IPPPrintService)this.service).wakeNotifier();
/*     */           else {
/* 482 */             ((UnixPrintService)this.service).wakeNotifier();
/*     */           }
/* 484 */           return;
/*     */         } catch (ClassCastException localClassCastException3) {
/* 486 */           notifyEvent(103);
/* 487 */           throw new PrintException(localClassCastException3);
/*     */         } catch (IOException localIOException6) {
/* 489 */           notifyEvent(103);
/* 490 */           throw new PrintException(localIOException6);
/*     */         }
/* 492 */       if (((String)localObject3).equals("java.awt.print.Printable")) {
/*     */         try {
/* 494 */           printableJob((Printable)paramDoc.getPrintData());
/* 495 */           if ((this.service instanceof IPPPrintService))
/* 496 */             ((IPPPrintService)this.service).wakeNotifier();
/*     */           else {
/* 498 */             ((UnixPrintService)this.service).wakeNotifier();
/*     */           }
/* 500 */           return;
/*     */         } catch (ClassCastException localClassCastException4) {
/* 502 */           notifyEvent(103);
/* 503 */           throw new PrintException(localClassCastException4);
/*     */         } catch (IOException localIOException7) {
/* 505 */           notifyEvent(103);
/* 506 */           throw new PrintException(localIOException7);
/*     */         }
/*     */       }
/* 509 */       notifyEvent(103);
/* 510 */       throw new PrintException("unrecognized class: " + (String)localObject3);
/*     */     }
/*     */ 
/* 514 */     PrinterOpener localPrinterOpener = new PrinterOpener(null);
/* 515 */     AccessController.doPrivileged(localPrinterOpener);
/* 516 */     if (localPrinterOpener.pex != null) {
/* 517 */       throw localPrinterOpener.pex;
/*     */     }
/* 519 */     OutputStream localOutputStream = localPrinterOpener.result;
/*     */ 
/* 529 */     BufferedWriter localBufferedWriter = null;
/*     */     Object localObject4;
/*     */     Object localObject6;
/*     */     Object localObject7;
/*     */     Object localObject5;
/* 530 */     if ((this.instream == null) && (this.reader != null)) {
/* 531 */       localObject4 = new BufferedReader(this.reader);
/* 532 */       localObject6 = new OutputStreamWriter(localOutputStream);
/* 533 */       localBufferedWriter = new BufferedWriter((Writer)localObject6);
/* 534 */       localObject7 = new char[1024];
/*     */       try
/*     */       {
/*     */         int i;
/* 538 */         while ((i = ((BufferedReader)localObject4).read((char[])localObject7, 0, localObject7.length)) >= 0) {
/* 539 */           localBufferedWriter.write((char[])localObject7, 0, i);
/*     */         }
/* 541 */         ((BufferedReader)localObject4).close();
/* 542 */         localBufferedWriter.flush();
/* 543 */         localBufferedWriter.close();
/*     */       } catch (IOException localIOException11) {
/* 545 */         notifyEvent(103);
/* 546 */         throw new PrintException(localIOException11);
/*     */       }
/* 548 */     } else if ((this.instream != null) && (((DocFlavor)???).getMediaType().equalsIgnoreCase("text")))
/*     */     {
/*     */       try
/*     */       {
/* 552 */         localObject4 = new InputStreamReader(this.instream, str2);
/*     */ 
/* 554 */         localObject6 = new BufferedReader((Reader)localObject4);
/* 555 */         localObject7 = new OutputStreamWriter(localOutputStream);
/* 556 */         localBufferedWriter = new BufferedWriter((Writer)localObject7);
/* 557 */         char[] arrayOfChar = new char[1024];
/*     */         int k;
/* 560 */         while ((k = ((BufferedReader)localObject6).read(arrayOfChar, 0, arrayOfChar.length)) >= 0) {
/* 561 */           localBufferedWriter.write(arrayOfChar, 0, k);
/*     */         }
/* 563 */         localBufferedWriter.flush();
/*     */       } catch (IOException localIOException10) {
/* 565 */         notifyEvent(103);
/* 566 */         throw new PrintException(localIOException10);
/*     */       } finally {
/*     */         try {
/* 569 */           if (localBufferedWriter != null)
/* 570 */             localBufferedWriter.close();
/*     */         } catch (IOException localIOException13) {
/*     */         }
/*     */       }
/*     */     }
/* 575 */     else if (this.instream != null) {
/* 576 */       localObject5 = new BufferedInputStream(this.instream);
/* 577 */       localObject6 = new BufferedOutputStream(localOutputStream);
/* 578 */       localObject7 = new byte[1024];
/* 579 */       int j = 0;
/*     */       try
/*     */       {
/* 582 */         while ((j = ((BufferedInputStream)localObject5).read((byte[])localObject7)) >= 0) {
/* 583 */           ((BufferedOutputStream)localObject6).write((byte[])localObject7, 0, j);
/*     */         }
/* 585 */         ((BufferedInputStream)localObject5).close();
/* 586 */         ((BufferedOutputStream)localObject6).flush();
/* 587 */         ((BufferedOutputStream)localObject6).close();
/*     */       } catch (IOException localIOException12) {
/* 589 */         notifyEvent(103);
/* 590 */         throw new PrintException(localIOException12);
/*     */       }
/*     */     }
/* 593 */     notifyEvent(106);
/*     */ 
/* 595 */     if (this.mDestType == DESTPRINTER) {
/* 596 */       localObject5 = new PrinterSpooler(null);
/* 597 */       AccessController.doPrivileged((PrivilegedAction)localObject5);
/* 598 */       if (((PrinterSpooler)localObject5).pex != null) {
/* 599 */         throw ((PrinterSpooler)localObject5).pex;
/*     */       }
/*     */     }
/* 602 */     notifyEvent(105);
/* 603 */     if ((this.service instanceof IPPPrintService))
/* 604 */       ((IPPPrintService)this.service).wakeNotifier();
/*     */     else
/* 606 */       ((UnixPrintService)this.service).wakeNotifier();
/*     */   }
/*     */ 
/*     */   public void printableJob(Printable paramPrintable) throws PrintException
/*     */   {
/*     */     try {
/* 612 */       synchronized (this) {
/* 613 */         if (this.job != null) {
/* 614 */           throw new PrintException("already printing");
/*     */         }
/* 616 */         this.job = new PSPrinterJob();
/*     */       }
/*     */ 
/* 619 */       this.job.setPrintService(getPrintService());
/* 620 */       this.job.setCopies(this.copies);
/* 621 */       this.job.setJobName(this.jobName);
/* 622 */       ??? = new PageFormat();
/* 623 */       if (this.mediaSize != null) {
/* 624 */         Paper localPaper = new Paper();
/* 625 */         localPaper.setSize(this.mediaSize.getX(25400) * 72.0D, this.mediaSize.getY(25400) * 72.0D);
/*     */ 
/* 627 */         localPaper.setImageableArea(72.0D, 72.0D, localPaper.getWidth() - 144.0D, localPaper.getHeight() - 144.0D);
/*     */ 
/* 629 */         ((PageFormat)???).setPaper(localPaper);
/*     */       }
/* 631 */       if (this.orient == OrientationRequested.REVERSE_LANDSCAPE)
/* 632 */         ((PageFormat)???).setOrientation(2);
/* 633 */       else if (this.orient == OrientationRequested.LANDSCAPE) {
/* 634 */         ((PageFormat)???).setOrientation(0);
/*     */       }
/* 636 */       this.job.setPrintable(paramPrintable, (PageFormat)???);
/* 637 */       this.job.print(this.reqAttrSet);
/* 638 */       notifyEvent(106);
/*     */     }
/*     */     catch (PrinterException localPrinterException) {
/* 641 */       notifyEvent(103);
/* 642 */       throw new PrintException(localPrinterException);
/*     */     } finally {
/* 644 */       this.printReturned = true;
/* 645 */       notifyEvent(105);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void pageableJob(Pageable paramPageable) throws PrintException {
/*     */     try {
/* 651 */       synchronized (this) {
/* 652 */         if (this.job != null) {
/* 653 */           throw new PrintException("already printing");
/*     */         }
/* 655 */         this.job = new PSPrinterJob();
/*     */       }
/*     */ 
/* 658 */       this.job.setPrintService(getPrintService());
/* 659 */       this.job.setCopies(this.copies);
/* 660 */       this.job.setJobName(this.jobName);
/* 661 */       this.job.setPageable(paramPageable);
/* 662 */       this.job.print(this.reqAttrSet);
/* 663 */       notifyEvent(106);
/*     */     }
/*     */     catch (PrinterException localPrinterException) {
/* 666 */       notifyEvent(103);
/* 667 */       throw new PrintException(localPrinterException);
/*     */     } finally {
/* 669 */       this.printReturned = true;
/* 670 */       notifyEvent(105);
/*     */     }
/*     */   }
/*     */ 
/*     */   private synchronized void initializeAttributeSets(Doc paramDoc, PrintRequestAttributeSet paramPrintRequestAttributeSet)
/*     */   {
/* 679 */     this.reqAttrSet = new HashPrintRequestAttributeSet();
/* 680 */     this.jobAttrSet = new HashPrintJobAttributeSet();
/*     */     Attribute[] arrayOfAttribute;
/* 683 */     if (paramPrintRequestAttributeSet != null) {
/* 684 */       this.reqAttrSet.addAll(paramPrintRequestAttributeSet);
/* 685 */       arrayOfAttribute = paramPrintRequestAttributeSet.toArray();
/* 686 */       for (int i = 0; i < arrayOfAttribute.length; i++) {
/* 687 */         if ((arrayOfAttribute[i] instanceof PrintJobAttribute)) {
/* 688 */           this.jobAttrSet.add(arrayOfAttribute[i]);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 693 */     DocAttributeSet localDocAttributeSet = paramDoc.getAttributes();
/* 694 */     if (localDocAttributeSet != null) {
/* 695 */       arrayOfAttribute = localDocAttributeSet.toArray();
/* 696 */       for (int j = 0; j < arrayOfAttribute.length; j++) {
/* 697 */         if ((arrayOfAttribute[j] instanceof PrintRequestAttribute)) {
/* 698 */           this.reqAttrSet.add(arrayOfAttribute[j]);
/*     */         }
/* 700 */         if ((arrayOfAttribute[j] instanceof PrintJobAttribute)) {
/* 701 */           this.jobAttrSet.add(arrayOfAttribute[j]);
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 707 */     String str = "";
/*     */     try {
/* 709 */       str = System.getProperty("user.name");
/*     */     }
/*     */     catch (SecurityException localSecurityException)
/*     */     {
/*     */     }
/*     */     Object localObject1;
/* 713 */     if ((str == null) || (str.equals(""))) {
/* 714 */       localObject1 = (RequestingUserName)paramPrintRequestAttributeSet.get(RequestingUserName.class);
/*     */ 
/* 716 */       if (localObject1 != null) {
/* 717 */         this.jobAttrSet.add(new JobOriginatingUserName(((RequestingUserName)localObject1).getValue(), ((RequestingUserName)localObject1).getLocale()));
/*     */       }
/*     */       else
/*     */       {
/* 721 */         this.jobAttrSet.add(new JobOriginatingUserName("", null));
/*     */       }
/*     */     } else {
/* 724 */       this.jobAttrSet.add(new JobOriginatingUserName(str, null));
/*     */     }
/*     */ 
/* 729 */     if (this.jobAttrSet.get(JobName.class) == null)
/*     */     {
/*     */       Object localObject2;
/* 731 */       if ((localDocAttributeSet != null) && (localDocAttributeSet.get(DocumentName.class) != null)) {
/* 732 */         localObject2 = (DocumentName)localDocAttributeSet.get(DocumentName.class);
/*     */ 
/* 734 */         localObject1 = new JobName(((DocumentName)localObject2).getValue(), ((DocumentName)localObject2).getLocale());
/* 735 */         this.jobAttrSet.add((Attribute)localObject1);
/*     */       } else {
/* 737 */         localObject2 = "JPS Job:" + paramDoc;
/*     */         try {
/* 739 */           Object localObject3 = paramDoc.getPrintData();
/* 740 */           if ((localObject3 instanceof URL))
/* 741 */             localObject2 = ((URL)paramDoc.getPrintData()).toString();
/*     */         }
/*     */         catch (IOException localIOException) {
/*     */         }
/* 745 */         localObject1 = new JobName((String)localObject2, null);
/* 746 */         this.jobAttrSet.add((Attribute)localObject1);
/*     */       }
/*     */     }
/*     */ 
/* 750 */     this.jobAttrSet = AttributeSetUtilities.unmodifiableView(this.jobAttrSet);
/*     */   }
/*     */ 
/*     */   private void getAttributeValues(DocFlavor paramDocFlavor)
/*     */     throws PrintException
/*     */   {
/* 757 */     if (this.reqAttrSet.get(Fidelity.class) == Fidelity.FIDELITY_TRUE)
/* 758 */       this.fidelity = true;
/*     */     else {
/* 760 */       this.fidelity = false;
/*     */     }
/*     */ 
/* 763 */     Attribute[] arrayOfAttribute = this.reqAttrSet.toArray();
/* 764 */     for (int i = 0; i < arrayOfAttribute.length; i++) {
/* 765 */       Attribute localAttribute = arrayOfAttribute[i];
/* 766 */       Class localClass = localAttribute.getCategory();
/* 767 */       if (this.fidelity == true) {
/* 768 */         if (!this.service.isAttributeCategorySupported(localClass)) {
/* 769 */           notifyEvent(103);
/* 770 */           throw new PrintJobAttributeException("unsupported category: " + localClass, localClass, null);
/*     */         }
/* 772 */         if (!this.service.isAttributeValueSupported(localAttribute, paramDocFlavor, null))
/*     */         {
/* 774 */           notifyEvent(103);
/* 775 */           throw new PrintJobAttributeException("unsupported attribute: " + localAttribute, null, localAttribute);
/*     */         }
/*     */       }
/*     */ 
/* 779 */       if (localClass == Destination.class) {
/* 780 */         URI localURI = ((Destination)localAttribute).getURI();
/* 781 */         if (!"file".equals(localURI.getScheme())) {
/* 782 */           notifyEvent(103);
/* 783 */           throw new PrintException("Not a file: URI");
/*     */         }
/*     */         try {
/* 786 */           this.mDestType = DESTFILE;
/* 787 */           this.mDestination = new File(localURI).getPath();
/*     */         } catch (Exception localException) {
/* 789 */           throw new PrintException(localException);
/*     */         }
/*     */ 
/* 792 */         SecurityManager localSecurityManager = System.getSecurityManager();
/* 793 */         if (localSecurityManager != null) {
/*     */           try {
/* 795 */             localSecurityManager.checkWrite(this.mDestination);
/*     */           } catch (SecurityException localSecurityException) {
/* 797 */             notifyEvent(103);
/* 798 */             throw new PrintException(localSecurityException);
/*     */           }
/*     */         }
/*     */       }
/* 802 */       else if (localClass == JobSheets.class) {
/* 803 */         if ((JobSheets)localAttribute == JobSheets.NONE)
/* 804 */           this.mNoJobSheet = true;
/*     */       }
/* 806 */       else if (localClass == JobName.class) {
/* 807 */         this.jobName = ((JobName)localAttribute).getValue();
/* 808 */       } else if (localClass == Copies.class) {
/* 809 */         this.copies = ((Copies)localAttribute).getValue();
/* 810 */       } else if (localClass == Media.class) {
/* 811 */         if ((localAttribute instanceof MediaSizeName)) {
/* 812 */           this.mediaName = ((MediaSizeName)localAttribute);
/* 813 */           IPPPrintService.debug_println(debugPrefix + "mediaName " + this.mediaName);
/*     */ 
/* 815 */           if (!this.service.isAttributeValueSupported(localAttribute, null, null))
/* 816 */             this.mediaSize = MediaSize.getMediaSizeForName(this.mediaName);
/*     */         }
/* 818 */         else if ((localAttribute instanceof CustomMediaTray)) {
/* 819 */           this.customTray = ((CustomMediaTray)localAttribute);
/*     */         }
/* 821 */       } else if (localClass == OrientationRequested.class) {
/* 822 */         this.orient = ((OrientationRequested)localAttribute);
/* 823 */       } else if (localClass == NumberUp.class) {
/* 824 */         this.nUp = ((NumberUp)localAttribute);
/* 825 */       } else if (localClass == Sides.class) {
/* 826 */         this.sides = ((Sides)localAttribute);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private String[] printExecCmd(String paramString1, String paramString2, boolean paramBoolean, String paramString3, int paramInt, String paramString4)
/*     */   {
/* 834 */     int i = 1;
/* 835 */     int j = 2;
/* 836 */     int k = 4;
/* 837 */     int m = 8;
/* 838 */     int n = 16;
/* 839 */     int i1 = 0;
/*     */ 
/* 841 */     int i2 = 2;
/* 842 */     int i3 = 0;
/*     */ 
/* 845 */     if ((paramString1 != null) && (!paramString1.equals("")) && (!paramString1.equals("lp"))) {
/* 846 */       i1 |= i;
/* 847 */       i2++;
/*     */     }
/* 849 */     if ((paramString2 != null) && (!paramString2.equals(""))) {
/* 850 */       i1 |= j;
/* 851 */       i2++;
/*     */     }
/* 853 */     if ((paramString3 != null) && (!paramString3.equals(""))) {
/* 854 */       i1 |= k;
/* 855 */       i2++;
/*     */     }
/* 857 */     if (paramInt > 1) {
/* 858 */       i1 |= m;
/* 859 */       i2++;
/*     */     }
/* 861 */     if (paramBoolean) {
/* 862 */       i1 |= n;
/* 863 */       i2++;
/*     */     }
/*     */     String[] arrayOfString;
/* 865 */     if (UnixPrintServiceLookup.osname.equals("SunOS")) {
/* 866 */       i2++;
/* 867 */       arrayOfString = new String[i2];
/* 868 */       arrayOfString[(i3++)] = "/usr/bin/lp";
/* 869 */       arrayOfString[(i3++)] = "-c";
/* 870 */       if ((i1 & i) != 0) {
/* 871 */         arrayOfString[(i3++)] = ("-d" + paramString1);
/*     */       }
/* 873 */       if ((i1 & k) != 0) {
/* 874 */         String str = "\"";
/* 875 */         arrayOfString[(i3++)] = ("-t " + str + paramString3 + str);
/*     */       }
/* 877 */       if ((i1 & m) != 0) {
/* 878 */         arrayOfString[(i3++)] = ("-n " + paramInt);
/*     */       }
/* 880 */       if ((i1 & n) != 0) {
/* 881 */         arrayOfString[(i3++)] = "-o nobanner";
/*     */       }
/* 883 */       if ((i1 & j) != 0)
/* 884 */         arrayOfString[(i3++)] = ("-o " + paramString2);
/*     */     }
/*     */     else {
/* 887 */       arrayOfString = new String[i2];
/* 888 */       arrayOfString[(i3++)] = "/usr/bin/lpr";
/* 889 */       if ((i1 & i) != 0) {
/* 890 */         arrayOfString[(i3++)] = ("-P" + paramString1);
/*     */       }
/* 892 */       if ((i1 & k) != 0) {
/* 893 */         arrayOfString[(i3++)] = ("-J " + paramString3);
/*     */       }
/* 895 */       if ((i1 & m) != 0) {
/* 896 */         arrayOfString[(i3++)] = ("-#" + paramInt);
/*     */       }
/* 898 */       if ((i1 & n) != 0) {
/* 899 */         arrayOfString[(i3++)] = "-h";
/*     */       }
/* 901 */       if ((i1 & j) != 0) {
/* 902 */         arrayOfString[(i3++)] = ("-o" + paramString2);
/*     */       }
/*     */     }
/* 905 */     arrayOfString[(i3++)] = paramString4;
/* 906 */     if (IPPPrintService.debugPrint) {
/* 907 */       System.out.println("UnixPrintJob>> execCmd");
/* 908 */       for (int i4 = 0; i4 < arrayOfString.length; i4++) {
/* 909 */         System.out.print(" " + arrayOfString[i4]);
/*     */       }
/* 911 */       System.out.println();
/*     */     }
/* 913 */     return arrayOfString;
/*     */   }
/*     */ 
/*     */   public void cancel()
/*     */     throws PrintException
/*     */   {
/* 992 */     synchronized (this) {
/* 993 */       if (!this.printing)
/* 994 */         throw new PrintException("Job is not yet submitted.");
/* 995 */       if ((this.job != null) && (!this.printReturned)) {
/* 996 */         this.job.cancel();
/* 997 */         notifyEvent(101);
/* 998 */         return;
/*     */       }
/* 1000 */       throw new PrintException("Job could not be cancelled.");
/*     */     }
/*     */   }
/*     */ 
/*     */   private class PrinterOpener
/*     */     implements PrivilegedAction
/*     */   {
/*     */     PrintException pex;
/*     */     OutputStream result;
/*     */ 
/*     */     private PrinterOpener()
/*     */     {
/*     */     }
/*     */ 
/*     */     public Object run()
/*     */     {
/*     */       try
/*     */       {
/* 932 */         if (UnixPrintJob.this.mDestType == UnixPrintJob.DESTFILE) {
/* 933 */           UnixPrintJob.this.spoolFile = new File(UnixPrintJob.this.mDestination);
/*     */         }
/*     */         else
/*     */         {
/* 940 */           UnixPrintJob.this.spoolFile = Files.createTempFile("javaprint", ".ps", new FileAttribute[0]).toFile();
/* 941 */           UnixPrintJob.this.spoolFile.deleteOnExit();
/*     */         }
/* 943 */         this.result = new FileOutputStream(UnixPrintJob.this.spoolFile);
/* 944 */         return this.result;
/*     */       }
/*     */       catch (IOException localIOException) {
/* 947 */         UnixPrintJob.this.notifyEvent(103);
/* 948 */         this.pex = new PrintException(localIOException);
/*     */       }
/* 950 */       return null;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class PrinterSpooler implements PrivilegedAction
/*     */   {
/*     */     PrintException pex;
/*     */ 
/*     */     private PrinterSpooler()
/*     */     {
/*     */     }
/*     */ 
/*     */     public Object run() {
/*     */       try {
/* 964 */         if ((UnixPrintJob.this.spoolFile == null) || (!UnixPrintJob.this.spoolFile.exists())) {
/* 965 */           this.pex = new PrintException("No spool file");
/* 966 */           UnixPrintJob.this.notifyEvent(103);
/* 967 */           return null;
/*     */         }
/* 969 */         Object localObject1 = UnixPrintJob.this.spoolFile.getAbsolutePath();
/* 970 */         String[] arrayOfString = UnixPrintJob.this.printExecCmd(UnixPrintJob.this.mDestination, UnixPrintJob.this.mOptions, UnixPrintJob.this.mNoJobSheet, UnixPrintJob.this.jobName, UnixPrintJob.this.copies, (String)localObject1);
/*     */ 
/* 973 */         Process localProcess = Runtime.getRuntime().exec(arrayOfString);
/* 974 */         localProcess.waitFor();
/* 975 */         UnixPrintJob.this.spoolFile.delete();
/* 976 */         UnixPrintJob.this.notifyEvent(106);
/*     */       } catch (IOException localIOException) {
/* 978 */         UnixPrintJob.this.notifyEvent(103);
/*     */ 
/* 980 */         this.pex = new PrintException(localIOException);
/*     */       } catch (InterruptedException localInterruptedException) {
/* 982 */         UnixPrintJob.this.notifyEvent(103);
/* 983 */         this.pex = new PrintException(localInterruptedException);
/*     */       } finally {
/* 985 */         UnixPrintJob.this.notifyEvent(105);
/*     */       }
/* 987 */       return null;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.print.UnixPrintJob
 * JD-Core Version:    0.6.2
 */