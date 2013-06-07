/*      */ package sun.print;
/*      */ 
/*      */ import java.awt.Color;
/*      */ import java.awt.Frame;
/*      */ import java.awt.Graphics2D;
/*      */ import java.awt.GraphicsConfiguration;
/*      */ import java.awt.GraphicsDevice;
/*      */ import java.awt.GraphicsEnvironment;
/*      */ import java.awt.HeadlessException;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.Shape;
/*      */ import java.awt.geom.AffineTransform;
/*      */ import java.awt.geom.Point2D.Double;
/*      */ import java.awt.geom.Rectangle2D;
/*      */ import java.awt.geom.Rectangle2D.Double;
/*      */ import java.awt.image.BufferedImage;
/*      */ import java.awt.print.Book;
/*      */ import java.awt.print.PageFormat;
/*      */ import java.awt.print.Pageable;
/*      */ import java.awt.print.Paper;
/*      */ import java.awt.print.Printable;
/*      */ import java.awt.print.PrinterAbortException;
/*      */ import java.awt.print.PrinterException;
/*      */ import java.awt.print.PrinterJob;
/*      */ import java.io.File;
/*      */ import java.io.FilePermission;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintStream;
/*      */ import java.net.URI;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Locale;
/*      */ import javax.print.DocFlavor.SERVICE_FORMATTED;
/*      */ import javax.print.DocPrintJob;
/*      */ import javax.print.PrintException;
/*      */ import javax.print.PrintService;
/*      */ import javax.print.PrintServiceLookup;
/*      */ import javax.print.ServiceUI;
/*      */ import javax.print.StreamPrintService;
/*      */ import javax.print.StreamPrintServiceFactory;
/*      */ import javax.print.attribute.Attribute;
/*      */ import javax.print.attribute.HashPrintRequestAttributeSet;
/*      */ import javax.print.attribute.PrintRequestAttributeSet;
/*      */ import javax.print.attribute.standard.Copies;
/*      */ import javax.print.attribute.standard.Destination;
/*      */ import javax.print.attribute.standard.DialogTypeSelection;
/*      */ import javax.print.attribute.standard.Fidelity;
/*      */ import javax.print.attribute.standard.JobName;
/*      */ import javax.print.attribute.standard.JobSheets;
/*      */ import javax.print.attribute.standard.Media;
/*      */ import javax.print.attribute.standard.MediaPrintableArea;
/*      */ import javax.print.attribute.standard.MediaSize;
/*      */ import javax.print.attribute.standard.MediaSize.NA;
/*      */ import javax.print.attribute.standard.MediaSizeName;
/*      */ import javax.print.attribute.standard.OrientationRequested;
/*      */ import javax.print.attribute.standard.PageRanges;
/*      */ import javax.print.attribute.standard.PrinterIsAcceptingJobs;
/*      */ import javax.print.attribute.standard.PrinterState;
/*      */ import javax.print.attribute.standard.PrinterStateReason;
/*      */ import javax.print.attribute.standard.PrinterStateReasons;
/*      */ import javax.print.attribute.standard.RequestingUserName;
/*      */ import javax.print.attribute.standard.SheetCollate;
/*      */ import javax.print.attribute.standard.Sides;
/*      */ import sun.awt.image.ByteInterleavedRaster;
/*      */ import sun.security.action.GetPropertyAction;
/*      */ 
/*      */ public abstract class RasterPrinterJob extends PrinterJob
/*      */ {
/*      */   protected static final int PRINTER = 0;
/*      */   protected static final int FILE = 1;
/*      */   protected static final int STREAM = 2;
/*      */   private static final int MAX_BAND_SIZE = 4194304;
/*      */   private static final float DPI = 72.0F;
/*      */   private static final String FORCE_PIPE_PROP = "sun.java2d.print.pipeline";
/*      */   private static final String FORCE_RASTER = "raster";
/*      */   private static final String FORCE_PDL = "pdl";
/*      */   private static final String SHAPE_TEXT_PROP = "sun.java2d.print.shapetext";
/*  164 */   public static boolean forcePDL = false;
/*  165 */   public static boolean forceRaster = false;
/*  166 */   public static boolean shapeTextProp = false;
/*      */ 
/*  200 */   private int cachedBandWidth = 0;
/*  201 */   private int cachedBandHeight = 0;
/*  202 */   private BufferedImage cachedBand = null;
/*      */ 
/*  207 */   private int mNumCopies = 1;
/*      */ 
/*  216 */   private boolean mCollate = false;
/*      */ 
/*  226 */   private int mFirstPage = -1;
/*  227 */   private int mLastPage = -1;
/*      */   private Paper previousPaper;
/*  244 */   protected Pageable mDocument = new Book();
/*      */ 
/*  249 */   private String mDocName = "Java Printing";
/*      */ 
/*  256 */   protected boolean performingPrinting = false;
/*      */ 
/*  258 */   protected boolean userCancelled = false;
/*      */   private FilePermission printToFilePermission;
/*  268 */   private ArrayList redrawList = new ArrayList();
/*      */   private int copiesAttr;
/*      */   private String jobNameAttr;
/*      */   private String userNameAttr;
/*      */   private PageRanges pageRangesAttr;
/*      */   protected Sides sidesAttr;
/*      */   protected String destinationAttr;
/*  280 */   protected boolean noJobSheet = false;
/*  281 */   protected int mDestType = 1;
/*  282 */   protected String mDestination = "";
/*  283 */   protected boolean collateAttReq = false;
/*      */ 
/*  288 */   protected boolean landscapeRotates270 = false;
/*      */ 
/*  294 */   protected PrintRequestAttributeSet attributes = null;
/*      */   protected PrintService myService;
/* 1280 */   public static boolean debugPrint = false;
/*      */   private int deviceWidth;
/*      */   private int deviceHeight;
/*      */   private AffineTransform defaultDeviceTransform;
/*      */   private PrinterGraphicsConfig pgConfig;
/*      */ 
/*      */   protected abstract double getXRes();
/*      */ 
/*      */   protected abstract double getYRes();
/*      */ 
/*      */   protected abstract double getPhysicalPrintableX(Paper paramPaper);
/*      */ 
/*      */   protected abstract double getPhysicalPrintableY(Paper paramPaper);
/*      */ 
/*      */   protected abstract double getPhysicalPrintableWidth(Paper paramPaper);
/*      */ 
/*      */   protected abstract double getPhysicalPrintableHeight(Paper paramPaper);
/*      */ 
/*      */   protected abstract double getPhysicalPageWidth(Paper paramPaper);
/*      */ 
/*      */   protected abstract double getPhysicalPageHeight(Paper paramPaper);
/*      */ 
/*      */   protected abstract void startPage(PageFormat paramPageFormat, Printable paramPrintable, int paramInt, boolean paramBoolean)
/*      */     throws PrinterException;
/*      */ 
/*      */   protected abstract void endPage(PageFormat paramPageFormat, Printable paramPrintable, int paramInt)
/*      */     throws PrinterException;
/*      */ 
/*      */   protected abstract void printBand(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */     throws PrinterException;
/*      */ 
/*      */   public void saveState(AffineTransform paramAffineTransform, Shape paramShape, Rectangle2D paramRectangle2D, double paramDouble1, double paramDouble2)
/*      */   {
/*  411 */     GraphicsState localGraphicsState = new GraphicsState(null);
/*  412 */     localGraphicsState.theTransform = paramAffineTransform;
/*  413 */     localGraphicsState.theClip = paramShape;
/*  414 */     localGraphicsState.region = paramRectangle2D;
/*  415 */     localGraphicsState.sx = paramDouble1;
/*  416 */     localGraphicsState.sy = paramDouble2;
/*  417 */     this.redrawList.add(localGraphicsState);
/*      */   }
/*      */ 
/*      */   protected static PrintService lookupDefaultPrintService()
/*      */   {
/*  430 */     PrintService localPrintService = PrintServiceLookup.lookupDefaultPrintService();
/*      */ 
/*  433 */     if ((localPrintService != null) && (localPrintService.isDocFlavorSupported(DocFlavor.SERVICE_FORMATTED.PAGEABLE)) && (localPrintService.isDocFlavorSupported(DocFlavor.SERVICE_FORMATTED.PRINTABLE)))
/*      */     {
/*  438 */       return localPrintService;
/*      */     }
/*  440 */     PrintService[] arrayOfPrintService = PrintServiceLookup.lookupPrintServices(DocFlavor.SERVICE_FORMATTED.PAGEABLE, null);
/*      */ 
/*  443 */     if (arrayOfPrintService.length > 0) {
/*  444 */       return arrayOfPrintService[0];
/*      */     }
/*      */ 
/*  447 */     return null;
/*      */   }
/*      */ 
/*      */   public PrintService getPrintService()
/*      */   {
/*  458 */     if (this.myService == null) {
/*  459 */       PrintService localPrintService = PrintServiceLookup.lookupDefaultPrintService();
/*  460 */       if ((localPrintService != null) && (localPrintService.isDocFlavorSupported(DocFlavor.SERVICE_FORMATTED.PAGEABLE)))
/*      */       {
/*      */         try
/*      */         {
/*  464 */           setPrintService(localPrintService);
/*  465 */           this.myService = localPrintService;
/*      */         } catch (PrinterException localPrinterException1) {
/*      */         }
/*      */       }
/*  469 */       if (this.myService == null) {
/*  470 */         PrintService[] arrayOfPrintService = PrintServiceLookup.lookupPrintServices(DocFlavor.SERVICE_FORMATTED.PAGEABLE, null);
/*      */ 
/*  472 */         if (arrayOfPrintService.length > 0)
/*      */           try {
/*  474 */             setPrintService(arrayOfPrintService[0]);
/*  475 */             this.myService = arrayOfPrintService[0];
/*      */           }
/*      */           catch (PrinterException localPrinterException2) {
/*      */           }
/*      */       }
/*      */     }
/*  481 */     return this.myService;
/*      */   }
/*      */ 
/*      */   public void setPrintService(PrintService paramPrintService)
/*      */     throws PrinterException
/*      */   {
/*  497 */     if (paramPrintService == null)
/*  498 */       throw new PrinterException("Service cannot be null");
/*  499 */     if ((!(paramPrintService instanceof StreamPrintService)) && (paramPrintService.getName() == null))
/*      */     {
/*  501 */       throw new PrinterException("Null PrintService name.");
/*      */     }
/*      */ 
/*  505 */     PrinterState localPrinterState = (PrinterState)paramPrintService.getAttribute(PrinterState.class);
/*      */ 
/*  507 */     if (localPrinterState == PrinterState.STOPPED) {
/*  508 */       PrinterStateReasons localPrinterStateReasons = (PrinterStateReasons)paramPrintService.getAttribute(PrinterStateReasons.class);
/*      */ 
/*  511 */       if ((localPrinterStateReasons != null) && (localPrinterStateReasons.containsKey(PrinterStateReason.SHUTDOWN)))
/*      */       {
/*  514 */         throw new PrinterException("PrintService is no longer available.");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  519 */     if ((paramPrintService.isDocFlavorSupported(DocFlavor.SERVICE_FORMATTED.PAGEABLE)) && (paramPrintService.isDocFlavorSupported(DocFlavor.SERVICE_FORMATTED.PRINTABLE)))
/*      */     {
/*  523 */       this.myService = paramPrintService;
/*      */     }
/*  525 */     else throw new PrinterException("Not a 2D print service: " + paramPrintService);
/*      */   }
/*      */ 
/*      */   protected void updatePageAttributes(PrintService paramPrintService, PageFormat paramPageFormat)
/*      */   {
/*  533 */     if ((paramPrintService == null) || (paramPageFormat == null)) {
/*  534 */       return;
/*      */     }
/*      */ 
/*  537 */     float f1 = (float)Math.rint(paramPageFormat.getPaper().getWidth() * 25400.0D / 72.0D) / 25400.0F;
/*      */ 
/*  540 */     float f2 = (float)Math.rint(paramPageFormat.getPaper().getHeight() * 25400.0D / 72.0D) / 25400.0F;
/*      */ 
/*  548 */     Media[] arrayOfMedia = (Media[])paramPrintService.getSupportedAttributeValues(Media.class, null, null);
/*      */ 
/*  550 */     Object localObject = null;
/*      */     try {
/*  552 */       localObject = CustomMediaSizeName.findMedia(arrayOfMedia, f1, f2, 25400);
/*      */     }
/*      */     catch (IllegalArgumentException localIllegalArgumentException1) {
/*      */     }
/*  556 */     if ((localObject == null) || (!paramPrintService.isAttributeValueSupported((Attribute)localObject, null, null)))
/*      */     {
/*  558 */       localObject = (Media)paramPrintService.getDefaultAttributeValue(Media.class);
/*      */     }
/*      */     OrientationRequested localOrientationRequested;
/*  562 */     switch (paramPageFormat.getOrientation()) {
/*      */     case 0:
/*  564 */       localOrientationRequested = OrientationRequested.LANDSCAPE;
/*  565 */       break;
/*      */     case 2:
/*  567 */       localOrientationRequested = OrientationRequested.REVERSE_LANDSCAPE;
/*  568 */       break;
/*      */     default:
/*  570 */       localOrientationRequested = OrientationRequested.PORTRAIT;
/*      */     }
/*      */ 
/*  573 */     if (this.attributes == null) {
/*  574 */       this.attributes = new HashPrintRequestAttributeSet();
/*      */     }
/*  576 */     if (localObject != null) {
/*  577 */       this.attributes.add((Attribute)localObject);
/*      */     }
/*  579 */     this.attributes.add(localOrientationRequested);
/*      */ 
/*  581 */     float f3 = (float)(paramPageFormat.getPaper().getImageableX() / 72.0D);
/*  582 */     float f4 = (float)(paramPageFormat.getPaper().getImageableWidth() / 72.0D);
/*  583 */     float f5 = (float)(paramPageFormat.getPaper().getImageableY() / 72.0D);
/*  584 */     float f6 = (float)(paramPageFormat.getPaper().getImageableHeight() / 72.0D);
/*  585 */     if (f3 < 0.0F) f3 = 0.0F; if (f5 < 0.0F) f5 = 0.0F; try
/*      */     {
/*  587 */       this.attributes.add(new MediaPrintableArea(f3, f5, f4, f6, 25400));
/*      */     }
/*      */     catch (IllegalArgumentException localIllegalArgumentException2)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   public PageFormat pageDialog(PageFormat paramPageFormat)
/*      */     throws HeadlessException
/*      */   {
/*  617 */     if (GraphicsEnvironment.isHeadless()) {
/*  618 */       throw new HeadlessException();
/*      */     }
/*      */ 
/*  621 */     final GraphicsConfiguration localGraphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
/*      */ 
/*  625 */     PrintService localPrintService = (PrintService)AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public Object run()
/*      */       {
/*  629 */         PrintService localPrintService = RasterPrinterJob.this.getPrintService();
/*  630 */         if (localPrintService == null) {
/*  631 */           ServiceDialog.showNoPrintService(localGraphicsConfiguration);
/*  632 */           return null;
/*      */         }
/*  634 */         return localPrintService;
/*      */       }
/*      */     });
/*  638 */     if (localPrintService == null) {
/*  639 */       return paramPageFormat;
/*      */     }
/*  641 */     updatePageAttributes(localPrintService, paramPageFormat);
/*      */ 
/*  643 */     PageFormat localPageFormat = pageDialog(this.attributes);
/*      */ 
/*  645 */     if (localPageFormat == null) {
/*  646 */       return paramPageFormat;
/*      */     }
/*  648 */     return localPageFormat;
/*      */   }
/*      */ 
/*      */   public PageFormat pageDialog(PrintRequestAttributeSet paramPrintRequestAttributeSet)
/*      */     throws HeadlessException
/*      */   {
/*  658 */     if (GraphicsEnvironment.isHeadless()) {
/*  659 */       throw new HeadlessException();
/*      */     }
/*      */ 
/*  662 */     final GraphicsConfiguration localGraphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
/*      */ 
/*  665 */     Rectangle localRectangle = localGraphicsConfiguration.getBounds();
/*  666 */     int i = localRectangle.x + localRectangle.width / 3;
/*  667 */     int j = localRectangle.y + localRectangle.height / 3;
/*      */ 
/*  669 */     PrintService localPrintService = (PrintService)AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public Object run()
/*      */       {
/*  673 */         PrintService localPrintService = RasterPrinterJob.this.getPrintService();
/*  674 */         if (localPrintService == null) {
/*  675 */           ServiceDialog.showNoPrintService(localGraphicsConfiguration);
/*  676 */           return null;
/*      */         }
/*  678 */         return localPrintService;
/*      */       }
/*      */     });
/*  682 */     if (localPrintService == null) {
/*  683 */       return null;
/*      */     }
/*      */ 
/*  686 */     ServiceDialog localServiceDialog = new ServiceDialog(localGraphicsConfiguration, i, j, localPrintService, DocFlavor.SERVICE_FORMATTED.PAGEABLE, paramPrintRequestAttributeSet, (Frame)null);
/*      */ 
/*  689 */     localServiceDialog.show();
/*      */ 
/*  691 */     if (localServiceDialog.getStatus() == 1) {
/*  692 */       PrintRequestAttributeSet localPrintRequestAttributeSet = localServiceDialog.getAttributes();
/*      */ 
/*  694 */       SunAlternateMedia localSunAlternateMedia = SunAlternateMedia.class;
/*      */ 
/*  696 */       if ((paramPrintRequestAttributeSet.containsKey(localSunAlternateMedia)) && (!localPrintRequestAttributeSet.containsKey(localSunAlternateMedia)))
/*      */       {
/*  698 */         paramPrintRequestAttributeSet.remove(localSunAlternateMedia);
/*      */       }
/*  700 */       paramPrintRequestAttributeSet.addAll(localPrintRequestAttributeSet);
/*      */ 
/*  702 */       PageFormat localPageFormat = defaultPage();
/*      */ 
/*  704 */       OrientationRequested localOrientationRequested = (OrientationRequested)paramPrintRequestAttributeSet.get(OrientationRequested.class);
/*      */ 
/*  707 */       int k = 1;
/*  708 */       if (localOrientationRequested != null) {
/*  709 */         if (localOrientationRequested == OrientationRequested.REVERSE_LANDSCAPE)
/*  710 */           k = 2;
/*  711 */         else if (localOrientationRequested == OrientationRequested.LANDSCAPE) {
/*  712 */           k = 0;
/*      */         }
/*      */       }
/*  715 */       localPageFormat.setOrientation(k);
/*      */ 
/*  717 */       Object localObject = (Media)paramPrintRequestAttributeSet.get(Media.class);
/*  718 */       if (localObject == null) {
/*  719 */         localObject = (Media)localPrintService.getDefaultAttributeValue(Media.class);
/*      */       }
/*      */ 
/*  722 */       if (!(localObject instanceof MediaSizeName)) {
/*  723 */         localObject = MediaSizeName.NA_LETTER;
/*      */       }
/*  725 */       MediaSize localMediaSize = MediaSize.getMediaSizeForName((MediaSizeName)localObject);
/*      */ 
/*  727 */       if (localMediaSize == null) {
/*  728 */         localMediaSize = MediaSize.NA.LETTER;
/*      */       }
/*  730 */       Paper localPaper = new Paper();
/*  731 */       float[] arrayOfFloat = localMediaSize.getSize(1);
/*  732 */       double d1 = Math.rint(arrayOfFloat[0] * 72.0D / 25400.0D);
/*  733 */       double d2 = Math.rint(arrayOfFloat[1] * 72.0D / 25400.0D);
/*  734 */       localPaper.setSize(d1, d2);
/*  735 */       MediaPrintableArea localMediaPrintableArea = (MediaPrintableArea)paramPrintRequestAttributeSet.get(MediaPrintableArea.class);
/*      */       double d3;
/*      */       double d5;
/*      */       double d4;
/*      */       double d6;
/*  740 */       if (localMediaPrintableArea != null)
/*      */       {
/*  743 */         d3 = Math.rint(localMediaPrintableArea.getX(25400) * 72.0F);
/*      */ 
/*  745 */         d5 = Math.rint(localMediaPrintableArea.getY(25400) * 72.0F);
/*      */ 
/*  747 */         d4 = Math.rint(localMediaPrintableArea.getWidth(25400) * 72.0F);
/*      */ 
/*  749 */         d6 = Math.rint(localMediaPrintableArea.getHeight(25400) * 72.0F);
/*      */       }
/*      */       else
/*      */       {
/*  753 */         if (d1 >= 432.0D) {
/*  754 */           d3 = 72.0D;
/*  755 */           d4 = d1 - 144.0D;
/*      */         } else {
/*  757 */           d3 = d1 / 6.0D;
/*  758 */           d4 = d1 * 0.75D;
/*      */         }
/*  760 */         if (d2 >= 432.0D) {
/*  761 */           d5 = 72.0D;
/*  762 */           d6 = d2 - 144.0D;
/*      */         } else {
/*  764 */           d5 = d2 / 6.0D;
/*  765 */           d6 = d2 * 0.75D;
/*      */         }
/*      */       }
/*  768 */       localPaper.setImageableArea(d3, d5, d4, d6);
/*  769 */       localPageFormat.setPaper(localPaper);
/*      */ 
/*  771 */       return localPageFormat;
/*      */     }
/*  773 */     return null;
/*      */   }
/*      */ 
/*      */   public boolean printDialog(PrintRequestAttributeSet paramPrintRequestAttributeSet)
/*      */     throws HeadlessException
/*      */   {
/*  794 */     if (GraphicsEnvironment.isHeadless()) {
/*  795 */       throw new HeadlessException();
/*      */     }
/*      */ 
/*  799 */     DialogTypeSelection localDialogTypeSelection = (DialogTypeSelection)paramPrintRequestAttributeSet.get(DialogTypeSelection.class);
/*      */ 
/*  803 */     if (localDialogTypeSelection == DialogTypeSelection.NATIVE) {
/*  804 */       this.attributes = paramPrintRequestAttributeSet;
/*      */       try {
/*  806 */         debug_println("calling setAttributes in printDialog");
/*  807 */         setAttributes(paramPrintRequestAttributeSet);
/*      */       }
/*      */       catch (PrinterException localPrinterException1)
/*      */       {
/*      */       }
/*      */ 
/*  813 */       boolean bool = printDialog();
/*  814 */       this.attributes = paramPrintRequestAttributeSet;
/*  815 */       return bool;
/*      */     }
/*      */ 
/*  829 */     final GraphicsConfiguration localGraphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
/*      */ 
/*  833 */     PrintService localPrintService1 = (PrintService)AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public Object run()
/*      */       {
/*  837 */         PrintService localPrintService = RasterPrinterJob.this.getPrintService();
/*  838 */         if (localPrintService == null) {
/*  839 */           ServiceDialog.showNoPrintService(localGraphicsConfiguration);
/*  840 */           return null;
/*      */         }
/*  842 */         return localPrintService;
/*      */       }
/*      */     });
/*  846 */     if (localPrintService1 == null) {
/*  847 */       return false;
/*      */     }
/*      */ 
/*  851 */     StreamPrintServiceFactory[] arrayOfStreamPrintServiceFactory = null;
/*      */     Object localObject;
/*  852 */     if ((localPrintService1 instanceof StreamPrintService)) {
/*  853 */       arrayOfStreamPrintServiceFactory = lookupStreamPrintServices(null);
/*  854 */       localObject = new StreamPrintService[arrayOfStreamPrintServiceFactory.length];
/*  855 */       for (int i = 0; i < arrayOfStreamPrintServiceFactory.length; i++)
/*  856 */         localObject[i] = arrayOfStreamPrintServiceFactory[i].getPrintService(null);
/*      */     }
/*      */     else {
/*  859 */       localObject = (PrintService[])AccessController.doPrivileged(new PrivilegedAction()
/*      */       {
/*      */         public Object run()
/*      */         {
/*  863 */           PrintService[] arrayOfPrintService = PrinterJob.lookupPrintServices();
/*  864 */           return arrayOfPrintService;
/*      */         }
/*      */       });
/*  868 */       if ((localObject == null) || (localObject.length == 0))
/*      */       {
/*  873 */         localObject = new PrintService[1];
/*  874 */         localObject[0] = localPrintService1; } 
/*  878 */     }
/*      */ Rectangle localRectangle = localGraphicsConfiguration.getBounds();
/*  879 */     int j = localRectangle.x + localRectangle.width / 3;
/*  880 */     int k = localRectangle.y + localRectangle.height / 3;
/*      */     PrintService localPrintService2;
/*      */     try { localPrintService2 = ServiceUI.printDialog(localGraphicsConfiguration, j, k, (PrintService[])localObject, localPrintService1, DocFlavor.SERVICE_FORMATTED.PAGEABLE, paramPrintRequestAttributeSet); }
/*      */     catch (IllegalArgumentException localIllegalArgumentException)
/*      */     {
/*  889 */       localPrintService2 = ServiceUI.printDialog(localGraphicsConfiguration, j, k, (PrintService[])localObject, localObject[0], DocFlavor.SERVICE_FORMATTED.PAGEABLE, paramPrintRequestAttributeSet);
/*      */     }
/*      */ 
/*  895 */     if (localPrintService2 == null) {
/*  896 */       return false;
/*      */     }
/*      */ 
/*  899 */     if (!localPrintService1.equals(localPrintService2)) {
/*      */       try {
/*  901 */         setPrintService(localPrintService2);
/*      */       }
/*      */       catch (PrinterException localPrinterException2)
/*      */       {
/*  908 */         this.myService = localPrintService2;
/*      */       }
/*      */     }
/*  911 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean printDialog()
/*      */     throws HeadlessException
/*      */   {
/*  925 */     if (GraphicsEnvironment.isHeadless()) {
/*  926 */       throw new HeadlessException();
/*      */     }
/*      */ 
/*  929 */     HashPrintRequestAttributeSet localHashPrintRequestAttributeSet = new HashPrintRequestAttributeSet();
/*      */ 
/*  931 */     localHashPrintRequestAttributeSet.add(new Copies(getCopies()));
/*  932 */     localHashPrintRequestAttributeSet.add(new JobName(getJobName(), null));
/*  933 */     boolean bool = printDialog(localHashPrintRequestAttributeSet);
/*  934 */     if (bool) {
/*  935 */       JobName localJobName = (JobName)localHashPrintRequestAttributeSet.get(JobName.class);
/*  936 */       if (localJobName != null) {
/*  937 */         setJobName(localJobName.getValue());
/*      */       }
/*  939 */       Copies localCopies = (Copies)localHashPrintRequestAttributeSet.get(Copies.class);
/*  940 */       if (localCopies != null) {
/*  941 */         setCopies(localCopies.getValue());
/*      */       }
/*      */ 
/*  944 */       Destination localDestination1 = (Destination)localHashPrintRequestAttributeSet.get(Destination.class);
/*      */ 
/*  946 */       if (localDestination1 != null) {
/*      */         try {
/*  948 */           this.mDestType = 1;
/*  949 */           this.mDestination = new File(localDestination1.getURI()).getPath();
/*      */         } catch (Exception localException) {
/*  951 */           this.mDestination = "out.prn";
/*  952 */           PrintService localPrintService2 = getPrintService();
/*  953 */           if (localPrintService2 != null) {
/*  954 */             Destination localDestination2 = (Destination)localPrintService2.getDefaultAttributeValue(Destination.class);
/*      */ 
/*  956 */             if (localDestination2 != null)
/*  957 */               this.mDestination = new File(localDestination2.getURI()).getPath();
/*      */           }
/*      */         }
/*      */       }
/*      */       else {
/*  962 */         this.mDestType = 0;
/*  963 */         PrintService localPrintService1 = getPrintService();
/*  964 */         if (localPrintService1 != null) {
/*  965 */           this.mDestination = localPrintService1.getName();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  970 */     return bool;
/*      */   }
/*      */ 
/*      */   public void setPrintable(Printable paramPrintable)
/*      */   {
/*  980 */     setPageable(new OpenBook(defaultPage(new PageFormat()), paramPrintable));
/*      */   }
/*      */ 
/*      */   public void setPrintable(Printable paramPrintable, PageFormat paramPageFormat)
/*      */   {
/*  992 */     setPageable(new OpenBook(paramPageFormat, paramPrintable));
/*  993 */     updatePageAttributes(getPrintService(), paramPageFormat);
/*      */   }
/*      */ 
/*      */   public void setPageable(Pageable paramPageable)
/*      */     throws NullPointerException
/*      */   {
/* 1007 */     if (paramPageable != null) {
/* 1008 */       this.mDocument = paramPageable;
/*      */     }
/*      */     else
/* 1011 */       throw new NullPointerException();
/*      */   }
/*      */ 
/*      */   protected void initPrinter()
/*      */   {
/*      */   }
/*      */ 
/*      */   protected boolean isSupportedValue(Attribute paramAttribute, PrintRequestAttributeSet paramPrintRequestAttributeSet)
/*      */   {
/* 1021 */     PrintService localPrintService = getPrintService();
/* 1022 */     return (paramAttribute != null) && (localPrintService != null) && (localPrintService.isAttributeValueSupported(paramAttribute, DocFlavor.SERVICE_FORMATTED.PAGEABLE, paramPrintRequestAttributeSet));
/*      */   }
/*      */ 
/*      */   protected void setAttributes(PrintRequestAttributeSet paramPrintRequestAttributeSet)
/*      */     throws PrinterException
/*      */   {
/* 1035 */     setCollated(false);
/* 1036 */     this.sidesAttr = null;
/* 1037 */     this.pageRangesAttr = null;
/* 1038 */     this.copiesAttr = 0;
/* 1039 */     this.jobNameAttr = null;
/* 1040 */     this.userNameAttr = null;
/* 1041 */     this.destinationAttr = null;
/* 1042 */     this.collateAttReq = false;
/*      */ 
/* 1044 */     PrintService localPrintService = getPrintService();
/* 1045 */     if ((paramPrintRequestAttributeSet == null) || (localPrintService == null)) {
/* 1046 */       return;
/*      */     }
/*      */ 
/* 1049 */     int i = 0;
/* 1050 */     Fidelity localFidelity = (Fidelity)paramPrintRequestAttributeSet.get(Fidelity.class);
/* 1051 */     if ((localFidelity != null) && (localFidelity == Fidelity.FIDELITY_TRUE)) {
/* 1052 */       i = 1;
/*      */     }
/*      */ 
/* 1055 */     if (i == 1) {
/* 1056 */       localObject1 = localPrintService.getUnsupportedAttributes(DocFlavor.SERVICE_FORMATTED.PAGEABLE, paramPrintRequestAttributeSet);
/*      */ 
/* 1060 */       if (localObject1 != null) {
/* 1061 */         throw new PrinterException("Fidelity cannot be satisfied");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1071 */     Object localObject1 = (SheetCollate)paramPrintRequestAttributeSet.get(SheetCollate.class);
/*      */ 
/* 1073 */     if (isSupportedValue((Attribute)localObject1, paramPrintRequestAttributeSet)) {
/* 1074 */       setCollated(localObject1 == SheetCollate.COLLATED);
/*      */     }
/*      */ 
/* 1077 */     this.sidesAttr = ((Sides)paramPrintRequestAttributeSet.get(Sides.class));
/* 1078 */     if (!isSupportedValue(this.sidesAttr, paramPrintRequestAttributeSet)) {
/* 1079 */       this.sidesAttr = Sides.ONE_SIDED;
/*      */     }
/*      */ 
/* 1082 */     this.pageRangesAttr = ((PageRanges)paramPrintRequestAttributeSet.get(PageRanges.class));
/* 1083 */     if (!isSupportedValue(this.pageRangesAttr, paramPrintRequestAttributeSet)) {
/* 1084 */       this.pageRangesAttr = null;
/*      */     }
/* 1086 */     else if ((SunPageSelection)paramPrintRequestAttributeSet.get(SunPageSelection.class) == SunPageSelection.RANGE)
/*      */     {
/* 1089 */       localObject2 = this.pageRangesAttr.getMembers();
/*      */ 
/* 1091 */       setPageRange(localObject2[0][0] - 1, localObject2[0][1] - 1);
/*      */     } else {
/* 1093 */       setPageRange(-1, -1);
/*      */     }
/*      */ 
/* 1097 */     Object localObject2 = (Copies)paramPrintRequestAttributeSet.get(Copies.class);
/* 1098 */     if ((isSupportedValue((Attribute)localObject2, paramPrintRequestAttributeSet)) || ((i == 0) && (localObject2 != null)))
/*      */     {
/* 1100 */       this.copiesAttr = ((Copies)localObject2).getValue();
/* 1101 */       setCopies(this.copiesAttr);
/*      */     } else {
/* 1103 */       this.copiesAttr = getCopies();
/*      */     }
/*      */ 
/* 1106 */     Destination localDestination = (Destination)paramPrintRequestAttributeSet.get(Destination.class);
/*      */ 
/* 1109 */     if (isSupportedValue(localDestination, paramPrintRequestAttributeSet))
/*      */     {
/*      */       try
/*      */       {
/* 1114 */         this.destinationAttr = ("" + new File(localDestination.getURI().getSchemeSpecificPart()));
/*      */       }
/*      */       catch (Exception localException) {
/* 1117 */         localObject3 = (Destination)localPrintService.getDefaultAttributeValue(Destination.class);
/*      */ 
/* 1119 */         if (localObject3 != null) {
/* 1120 */           this.destinationAttr = ("" + new File(((Destination)localObject3).getURI().getSchemeSpecificPart()));
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1126 */     JobSheets localJobSheets = (JobSheets)paramPrintRequestAttributeSet.get(JobSheets.class);
/* 1127 */     if (localJobSheets != null) {
/* 1128 */       this.noJobSheet = (localJobSheets == JobSheets.NONE);
/*      */     }
/*      */ 
/* 1131 */     Object localObject3 = (JobName)paramPrintRequestAttributeSet.get(JobName.class);
/* 1132 */     if ((isSupportedValue((Attribute)localObject3, paramPrintRequestAttributeSet)) || ((i == 0) && (localObject3 != null)))
/*      */     {
/* 1134 */       this.jobNameAttr = ((JobName)localObject3).getValue();
/* 1135 */       setJobName(this.jobNameAttr);
/*      */     } else {
/* 1137 */       this.jobNameAttr = getJobName();
/*      */     }
/*      */ 
/* 1140 */     RequestingUserName localRequestingUserName = (RequestingUserName)paramPrintRequestAttributeSet.get(RequestingUserName.class);
/*      */ 
/* 1142 */     if ((isSupportedValue(localRequestingUserName, paramPrintRequestAttributeSet)) || ((i == 0) && (localRequestingUserName != null)))
/*      */     {
/* 1144 */       this.userNameAttr = localRequestingUserName.getValue();
/*      */     }
/*      */     else try {
/* 1147 */         this.userNameAttr = getUserName();
/*      */       } catch (SecurityException localSecurityException) {
/* 1149 */         this.userNameAttr = "";
/*      */       }
/*      */ 
/*      */ 
/* 1156 */     Media localMedia = (Media)paramPrintRequestAttributeSet.get(Media.class);
/* 1157 */     OrientationRequested localOrientationRequested = (OrientationRequested)paramPrintRequestAttributeSet.get(OrientationRequested.class);
/*      */ 
/* 1159 */     MediaPrintableArea localMediaPrintableArea = (MediaPrintableArea)paramPrintRequestAttributeSet.get(MediaPrintableArea.class);
/*      */ 
/* 1162 */     if (((localOrientationRequested != null) || (localMedia != null) || (localMediaPrintableArea != null)) && ((getPageable() instanceof OpenBook)))
/*      */     {
/* 1168 */       Pageable localPageable = getPageable();
/* 1169 */       Printable localPrintable = localPageable.getPrintable(0);
/* 1170 */       PageFormat localPageFormat = (PageFormat)localPageable.getPageFormat(0).clone();
/* 1171 */       Paper localPaper = localPageFormat.getPaper();
/*      */ 
/* 1176 */       if ((localMediaPrintableArea == null) && (localMedia != null) && (localPrintService.isAttributeCategorySupported(MediaPrintableArea.class)))
/*      */       {
/* 1179 */         Object localObject4 = localPrintService.getSupportedAttributeValues(MediaPrintableArea.class, null, paramPrintRequestAttributeSet);
/*      */ 
/* 1182 */         if (((localObject4 instanceof MediaPrintableArea[])) && (((MediaPrintableArea[])localObject4).length > 0))
/*      */         {
/* 1184 */           localMediaPrintableArea = ((MediaPrintableArea[])(MediaPrintableArea[])localObject4)[0];
/*      */         }
/*      */       }
/*      */ 
/* 1188 */       if ((isSupportedValue(localOrientationRequested, paramPrintRequestAttributeSet)) || ((i == 0) && (localOrientationRequested != null)))
/*      */       {
/*      */         int j;
/* 1191 */         if (localOrientationRequested.equals(OrientationRequested.REVERSE_LANDSCAPE))
/* 1192 */           j = 2;
/* 1193 */         else if (localOrientationRequested.equals(OrientationRequested.LANDSCAPE))
/* 1194 */           j = 0;
/*      */         else {
/* 1196 */           j = 1;
/*      */         }
/* 1198 */         localPageFormat.setOrientation(j);
/*      */       }
/*      */       Object localObject5;
/* 1201 */       if ((isSupportedValue(localMedia, paramPrintRequestAttributeSet)) || ((i == 0) && (localMedia != null)))
/*      */       {
/* 1203 */         if ((localMedia instanceof MediaSizeName)) {
/* 1204 */           localObject5 = (MediaSizeName)localMedia;
/* 1205 */           MediaSize localMediaSize = MediaSize.getMediaSizeForName((MediaSizeName)localObject5);
/* 1206 */           if (localMediaSize != null) {
/* 1207 */             float f1 = localMediaSize.getX(25400) * 72.0F;
/* 1208 */             float f2 = localMediaSize.getY(25400) * 72.0F;
/* 1209 */             localPaper.setSize(f1, f2);
/* 1210 */             if (localMediaPrintableArea == null) {
/* 1211 */               localPaper.setImageableArea(72.0D, 72.0D, f1 - 144.0D, f2 - 144.0D);
/*      */             }
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1219 */       if ((isSupportedValue(localMediaPrintableArea, paramPrintRequestAttributeSet)) || ((i == 0) && (localMediaPrintableArea != null)))
/*      */       {
/* 1221 */         localObject5 = localMediaPrintableArea.getPrintableArea(25400);
/*      */ 
/* 1223 */         for (int k = 0; k < localObject5.length; k++) {
/* 1224 */           localObject5[k] *= 72.0F;
/*      */         }
/* 1226 */         localPaper.setImageableArea(localObject5[0], localObject5[1], localObject5[2], localObject5[3]);
/*      */       }
/*      */ 
/* 1230 */       localPageFormat.setPaper(localPaper);
/* 1231 */       localPageFormat = validatePage(localPageFormat);
/* 1232 */       setPrintable(localPrintable, localPageFormat);
/*      */     }
/*      */     else
/*      */     {
/* 1236 */       this.attributes = paramPrintRequestAttributeSet;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void spoolToService(PrintService paramPrintService, PrintRequestAttributeSet paramPrintRequestAttributeSet)
/*      */     throws PrinterException
/*      */   {
/* 1252 */     if (paramPrintService == null) {
/* 1253 */       throw new PrinterException("No print service found.");
/*      */     }
/*      */ 
/* 1256 */     DocPrintJob localDocPrintJob = paramPrintService.createPrintJob();
/* 1257 */     PageableDoc localPageableDoc = new PageableDoc(getPageable());
/* 1258 */     if (paramPrintRequestAttributeSet == null)
/* 1259 */       paramPrintRequestAttributeSet = new HashPrintRequestAttributeSet();
/*      */     try
/*      */     {
/* 1262 */       localDocPrintJob.print(localPageableDoc, paramPrintRequestAttributeSet);
/*      */     } catch (PrintException localPrintException) {
/* 1264 */       throw new PrinterException(localPrintException.toString());
/*      */     }
/*      */   }
/*      */ 
/*      */   public void print()
/*      */     throws PrinterException
/*      */   {
/* 1277 */     print(this.attributes);
/*      */   }
/*      */ 
/*      */   protected void debug_println(String paramString)
/*      */   {
/* 1282 */     if (debugPrint)
/* 1283 */       System.out.println("RasterPrinterJob " + paramString + " " + this);
/*      */   }
/*      */ 
/*      */   public void print(PrintRequestAttributeSet paramPrintRequestAttributeSet)
/*      */     throws PrinterException
/*      */   {
/* 1302 */     PrintService localPrintService = getPrintService();
/* 1303 */     debug_println("psvc = " + localPrintService);
/* 1304 */     if (localPrintService == null) {
/* 1305 */       throw new PrinterException("No print service found.");
/*      */     }
/*      */ 
/* 1310 */     PrinterState localPrinterState = (PrinterState)localPrintService.getAttribute(PrinterState.class);
/*      */     Object localObject1;
/* 1312 */     if (localPrinterState == PrinterState.STOPPED) {
/* 1313 */       localObject1 = (PrinterStateReasons)localPrintService.getAttribute(PrinterStateReasons.class);
/*      */ 
/* 1316 */       if ((localObject1 != null) && (((PrinterStateReasons)localObject1).containsKey(PrinterStateReason.SHUTDOWN)))
/*      */       {
/* 1319 */         throw new PrinterException("PrintService is no longer available.");
/*      */       }
/*      */     }
/*      */ 
/* 1323 */     if ((PrinterIsAcceptingJobs)localPrintService.getAttribute(PrinterIsAcceptingJobs.class) == PrinterIsAcceptingJobs.NOT_ACCEPTING_JOBS)
/*      */     {
/* 1326 */       throw new PrinterException("Printer is not accepting job.");
/*      */     }
/*      */ 
/* 1329 */     if (((localPrintService instanceof SunPrinterJobService)) && (((SunPrinterJobService)localPrintService).usesClass(getClass())))
/*      */     {
/* 1331 */       setAttributes(paramPrintRequestAttributeSet);
/*      */ 
/* 1333 */       if (this.destinationAttr != null)
/*      */       {
/* 1338 */         localObject1 = new File(this.destinationAttr);
/*      */         try
/*      */         {
/* 1341 */           if (((File)localObject1).createNewFile())
/* 1342 */             ((File)localObject1).delete();
/*      */         }
/*      */         catch (IOException localIOException) {
/* 1345 */           throw new PrinterException("Cannot write to file:" + this.destinationAttr);
/*      */         }
/*      */         catch (SecurityException localSecurityException)
/*      */         {
/*      */         }
/*      */ 
/* 1354 */         File localFile = ((File)localObject1).getParentFile();
/* 1355 */         if (((((File)localObject1).exists()) && ((!((File)localObject1).isFile()) || (!((File)localObject1).canWrite()))) || ((localFile != null) && ((!localFile.exists()) || ((localFile.exists()) && (!localFile.canWrite())))))
/*      */         {
/* 1359 */           throw new PrinterException("Cannot write to file:" + this.destinationAttr);
/*      */         }
/*      */       }
/*      */     }
/*      */     else {
/* 1364 */       spoolToService(localPrintService, paramPrintRequestAttributeSet);
/* 1365 */       return;
/*      */     }
/*      */ 
/* 1369 */     initPrinter();
/*      */ 
/* 1371 */     int i = getCollatedCopies();
/* 1372 */     int j = getNoncollatedCopies();
/* 1373 */     debug_println("getCollatedCopies()  " + i + " getNoncollatedCopies() " + j);
/*      */ 
/* 1381 */     int k = this.mDocument.getNumberOfPages();
/* 1382 */     if (k == 0) {
/* 1383 */       return;
/*      */     }
/*      */ 
/* 1386 */     int m = getFirstPage();
/* 1387 */     int n = getLastPage();
/* 1388 */     if (n == -1) {
/* 1389 */       int i1 = this.mDocument.getNumberOfPages();
/* 1390 */       if (i1 != -1) {
/* 1391 */         n = this.mDocument.getNumberOfPages() - 1;
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/* 1396 */       synchronized (this) {
/* 1397 */         this.performingPrinting = true;
/* 1398 */         this.userCancelled = false;
/*      */       }
/*      */ 
/* 1401 */       startDoc();
/* 1402 */       if (isCancelled()) {
/* 1403 */         cancelDoc();
/*      */       }
/*      */ 
/* 1408 */       boolean bool = true;
/* 1409 */       if (paramPrintRequestAttributeSet != null) {
/* 1410 */         SunPageSelection localSunPageSelection = (SunPageSelection)paramPrintRequestAttributeSet.get(SunPageSelection.class);
/*      */ 
/* 1412 */         if ((localSunPageSelection != null) && (localSunPageSelection != SunPageSelection.RANGE)) {
/* 1413 */           bool = false;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1418 */       debug_println("after startDoc rangeSelected? " + bool + " numNonCollatedCopies " + j);
/*      */ 
/* 1435 */       for (int i2 = 0; i2 < i; i2++) {
/* 1436 */         int i3 = m; int i4 = 0;
/*      */ 
/* 1440 */         for (; ((i3 <= n) || (n == -1)) && (i4 == 0); 
/* 1440 */           i3++)
/*      */         {
/*      */           int i5;
/* 1443 */           if ((this.pageRangesAttr != null) && (bool)) {
/* 1444 */             i5 = this.pageRangesAttr.next(i3);
/* 1445 */             if (i5 == -1) {
/*      */               break;
/*      */             }
/* 1447 */             if (i5 != i3 + 1);
/*      */           }
/*      */           else
/*      */           {
/* 1452 */             for (i5 = 0; 
/* 1454 */               (i5 < j) && (i4 == 0); 
/* 1455 */               i5++)
/*      */             {
/* 1457 */               if (isCancelled()) {
/* 1458 */                 cancelDoc();
/*      */               }
/* 1460 */               debug_println("printPage " + i3);
/* 1461 */               i4 = printPage(this.mDocument, i3);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 1467 */       if (isCancelled()) {
/* 1468 */         cancelDoc();
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 1473 */       this.previousPaper = null;
/* 1474 */       synchronized (this) {
/* 1475 */         if (this.performingPrinting) {
/* 1476 */           endDoc();
/*      */         }
/* 1478 */         this.performingPrinting = false;
/* 1479 */         notify();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void validatePaper(Paper paramPaper1, Paper paramPaper2)
/*      */   {
/* 1494 */     if ((paramPaper1 == null) || (paramPaper2 == null)) {
/* 1495 */       return;
/*      */     }
/* 1497 */     double d1 = paramPaper1.getWidth();
/* 1498 */     double d2 = paramPaper1.getHeight();
/* 1499 */     double d3 = paramPaper1.getImageableX();
/* 1500 */     double d4 = paramPaper1.getImageableY();
/* 1501 */     double d5 = paramPaper1.getImageableWidth();
/* 1502 */     double d6 = paramPaper1.getImageableHeight();
/*      */ 
/* 1507 */     Paper localPaper = new Paper();
/* 1508 */     d1 = d1 > 0.0D ? d1 : localPaper.getWidth();
/* 1509 */     d2 = d2 > 0.0D ? d2 : localPaper.getHeight();
/* 1510 */     d3 = d3 > 0.0D ? d3 : localPaper.getImageableX();
/* 1511 */     d4 = d4 > 0.0D ? d4 : localPaper.getImageableY();
/* 1512 */     d5 = d5 > 0.0D ? d5 : localPaper.getImageableWidth();
/* 1513 */     d6 = d6 > 0.0D ? d6 : localPaper.getImageableHeight();
/*      */ 
/* 1517 */     if (d5 > d1) {
/* 1518 */       d5 = d1;
/*      */     }
/* 1520 */     if (d6 > d2) {
/* 1521 */       d6 = d2;
/*      */     }
/* 1523 */     if (d3 + d5 > d1) {
/* 1524 */       d3 = d1 - d5;
/*      */     }
/* 1526 */     if (d4 + d6 > d2) {
/* 1527 */       d4 = d2 - d6;
/*      */     }
/* 1529 */     paramPaper2.setSize(d1, d2);
/* 1530 */     paramPaper2.setImageableArea(d3, d4, d5, d6);
/*      */   }
/*      */ 
/*      */   public PageFormat defaultPage(PageFormat paramPageFormat)
/*      */   {
/* 1542 */     PageFormat localPageFormat = (PageFormat)paramPageFormat.clone();
/* 1543 */     localPageFormat.setOrientation(1);
/* 1544 */     Paper localPaper = new Paper();
/* 1545 */     double d1 = 72.0D;
/*      */ 
/* 1547 */     Media localMedia = null;
/*      */ 
/* 1549 */     PrintService localPrintService = getPrintService();
/*      */     double d2;
/*      */     double d3;
/* 1550 */     if (localPrintService != null)
/*      */     {
/* 1552 */       localMedia = (Media)localPrintService.getDefaultAttributeValue(Media.class);
/*      */ 
/* 1555 */       if (((localMedia instanceof MediaSizeName)) && ((localObject = MediaSize.getMediaSizeForName((MediaSizeName)localMedia)) != null))
/*      */       {
/* 1558 */         d2 = ((MediaSize)localObject).getX(25400) * d1;
/* 1559 */         d3 = ((MediaSize)localObject).getY(25400) * d1;
/* 1560 */         localPaper.setSize(d2, d3);
/* 1561 */         localPaper.setImageableArea(d1, d1, d2 - 2.0D * d1, d3 - 2.0D * d1);
/*      */ 
/* 1564 */         localPageFormat.setPaper(localPaper);
/* 1565 */         return localPageFormat;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1572 */     Object localObject = Locale.getDefault().getCountry();
/* 1573 */     if ((!Locale.getDefault().equals(Locale.ENGLISH)) && (localObject != null) && (!((String)localObject).equals(Locale.US.getCountry())) && (!((String)localObject).equals(Locale.CANADA.getCountry())))
/*      */     {
/* 1578 */       double d4 = 25.399999999999999D;
/* 1579 */       d2 = Math.rint(210.0D * d1 / d4);
/* 1580 */       d3 = Math.rint(297.0D * d1 / d4);
/* 1581 */       localPaper.setSize(d2, d3);
/* 1582 */       localPaper.setImageableArea(d1, d1, d2 - 2.0D * d1, d3 - 2.0D * d1);
/*      */     }
/*      */ 
/* 1587 */     localPageFormat.setPaper(localPaper);
/*      */ 
/* 1589 */     return localPageFormat;
/*      */   }
/*      */ 
/*      */   public PageFormat validatePage(PageFormat paramPageFormat)
/*      */   {
/* 1597 */     PageFormat localPageFormat = (PageFormat)paramPageFormat.clone();
/* 1598 */     Paper localPaper = new Paper();
/* 1599 */     validatePaper(localPageFormat.getPaper(), localPaper);
/* 1600 */     localPageFormat.setPaper(localPaper);
/*      */ 
/* 1602 */     return localPageFormat;
/*      */   }
/*      */ 
/*      */   public void setCopies(int paramInt)
/*      */   {
/* 1609 */     this.mNumCopies = paramInt;
/*      */   }
/*      */ 
/*      */   public int getCopies()
/*      */   {
/* 1616 */     return this.mNumCopies;
/*      */   }
/*      */ 
/*      */   protected int getCopiesInt()
/*      */   {
/* 1623 */     return this.copiesAttr > 0 ? this.copiesAttr : getCopies();
/*      */   }
/*      */ 
/*      */   public String getUserName()
/*      */   {
/* 1631 */     return System.getProperty("user.name");
/*      */   }
/*      */ 
/*      */   protected String getUserNameInt()
/*      */   {
/* 1638 */     if (this.userNameAttr != null)
/* 1639 */       return this.userNameAttr;
/*      */     try
/*      */     {
/* 1642 */       return getUserName(); } catch (SecurityException localSecurityException) {
/*      */     }
/* 1644 */     return "";
/*      */   }
/*      */ 
/*      */   public void setJobName(String paramString)
/*      */   {
/* 1654 */     if (paramString != null)
/* 1655 */       this.mDocName = paramString;
/*      */     else
/* 1657 */       throw new NullPointerException();
/*      */   }
/*      */ 
/*      */   public String getJobName()
/*      */   {
/* 1665 */     return this.mDocName;
/*      */   }
/*      */ 
/*      */   protected String getJobNameInt()
/*      */   {
/* 1672 */     return this.jobNameAttr != null ? this.jobNameAttr : getJobName();
/*      */   }
/*      */ 
/*      */   protected void setPageRange(int paramInt1, int paramInt2)
/*      */   {
/* 1683 */     if ((paramInt1 >= 0) && (paramInt2 >= 0)) {
/* 1684 */       this.mFirstPage = paramInt1;
/* 1685 */       this.mLastPage = paramInt2;
/* 1686 */       if (this.mLastPage < this.mFirstPage) this.mLastPage = this.mFirstPage; 
/*      */     }
/* 1688 */     else { this.mFirstPage = -1;
/* 1689 */       this.mLastPage = -1;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected int getFirstPage()
/*      */   {
/* 1698 */     return this.mFirstPage == -1 ? 0 : this.mFirstPage;
/*      */   }
/*      */ 
/*      */   protected int getLastPage()
/*      */   {
/* 1706 */     return this.mLastPage;
/*      */   }
/*      */ 
/*      */   protected void setCollated(boolean paramBoolean)
/*      */   {
/* 1718 */     this.mCollate = paramBoolean;
/* 1719 */     this.collateAttReq = true;
/*      */   }
/*      */ 
/*      */   protected boolean isCollated()
/*      */   {
/* 1727 */     return this.mCollate;
/*      */   }
/*      */ 
/*      */   protected abstract void startDoc()
/*      */     throws PrinterException;
/*      */ 
/*      */   protected abstract void endDoc()
/*      */     throws PrinterException;
/*      */ 
/*      */   protected abstract void abortDoc();
/*      */ 
/*      */   protected void cancelDoc()
/*      */     throws PrinterAbortException
/*      */   {
/* 1747 */     abortDoc();
/* 1748 */     synchronized (this) {
/* 1749 */       this.userCancelled = false;
/* 1750 */       this.performingPrinting = false;
/* 1751 */       notify();
/*      */     }
/* 1753 */     throw new PrinterAbortException();
/*      */   }
/*      */ 
/*      */   protected int getCollatedCopies()
/*      */   {
/* 1765 */     return isCollated() ? getCopiesInt() : 1;
/*      */   }
/*      */ 
/*      */   protected int getNoncollatedCopies()
/*      */   {
/* 1775 */     return isCollated() ? 1 : getCopiesInt();
/*      */   }
/*      */ 
/*      */   synchronized void setGraphicsConfigInfo(AffineTransform paramAffineTransform, double paramDouble1, double paramDouble2)
/*      */   {
/* 1790 */     Point2D.Double localDouble = new Point2D.Double(paramDouble1, paramDouble2);
/* 1791 */     paramAffineTransform.transform(localDouble, localDouble);
/*      */ 
/* 1793 */     if ((this.pgConfig == null) || (this.defaultDeviceTransform == null) || (!paramAffineTransform.equals(this.defaultDeviceTransform)) || (this.deviceWidth != (int)localDouble.getX()) || (this.deviceHeight != (int)localDouble.getY()))
/*      */     {
/* 1799 */       this.deviceWidth = ((int)localDouble.getX());
/* 1800 */       this.deviceHeight = ((int)localDouble.getY());
/* 1801 */       this.defaultDeviceTransform = paramAffineTransform;
/* 1802 */       this.pgConfig = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   synchronized PrinterGraphicsConfig getPrinterGraphicsConfig() {
/* 1807 */     if (this.pgConfig != null) {
/* 1808 */       return this.pgConfig;
/*      */     }
/* 1810 */     String str = "Printer Device";
/* 1811 */     PrintService localPrintService = getPrintService();
/* 1812 */     if (localPrintService != null) {
/* 1813 */       str = localPrintService.toString();
/*      */     }
/* 1815 */     this.pgConfig = new PrinterGraphicsConfig(str, this.defaultDeviceTransform, this.deviceWidth, this.deviceHeight);
/*      */ 
/* 1818 */     return this.pgConfig;
/*      */   }
/*      */ 
/*      */   protected int printPage(Pageable paramPageable, int paramInt)
/*      */     throws PrinterException
/*      */   {
/*      */     PageFormat localPageFormat2;
/*      */     PageFormat localPageFormat1;
/*      */     Printable localPrintable;
/*      */     try
/*      */     {
/* 1834 */       localPageFormat2 = paramPageable.getPageFormat(paramInt);
/* 1835 */       localPageFormat1 = (PageFormat)localPageFormat2.clone();
/* 1836 */       localPrintable = paramPageable.getPrintable(paramInt);
/*      */     } catch (Exception localException) {
/* 1838 */       PrinterException localPrinterException = new PrinterException("Error getting page or printable.[ " + localException + " ]");
/*      */ 
/* 1841 */       localPrinterException.initCause(localException);
/* 1842 */       throw localPrinterException;
/*      */     }
/*      */ 
/* 1848 */     Paper localPaper1 = localPageFormat1.getPaper();
/*      */ 
/* 1850 */     if ((localPageFormat1.getOrientation() != 1) && (this.landscapeRotates270))
/*      */     {
/* 1853 */       d1 = localPaper1.getImageableX();
/* 1854 */       d2 = localPaper1.getImageableY();
/* 1855 */       double d3 = localPaper1.getImageableWidth();
/* 1856 */       double d4 = localPaper1.getImageableHeight();
/* 1857 */       localPaper1.setImageableArea(localPaper1.getWidth() - d1 - d3, localPaper1.getHeight() - d2 - d4, d3, d4);
/*      */ 
/* 1860 */       localPageFormat1.setPaper(localPaper1);
/* 1861 */       if (localPageFormat1.getOrientation() == 0)
/* 1862 */         localPageFormat1.setOrientation(2);
/*      */       else {
/* 1864 */         localPageFormat1.setOrientation(0);
/*      */       }
/*      */     }
/*      */ 
/* 1868 */     double d1 = getXRes() / 72.0D;
/* 1869 */     double d2 = getYRes() / 72.0D;
/*      */ 
/* 1874 */     Rectangle2D.Double localDouble1 = new Rectangle2D.Double(localPaper1.getImageableX() * d1, localPaper1.getImageableY() * d2, localPaper1.getImageableWidth() * d1, localPaper1.getImageableHeight() * d2);
/*      */ 
/* 1884 */     AffineTransform localAffineTransform1 = new AffineTransform();
/*      */ 
/* 1889 */     AffineTransform localAffineTransform2 = new AffineTransform();
/* 1890 */     localAffineTransform2.scale(d1, d2);
/*      */ 
/* 1895 */     int i = (int)localDouble1.getWidth();
/* 1896 */     if (i % 4 != 0) {
/* 1897 */       i += 4 - i % 4;
/*      */     }
/* 1899 */     if (i <= 0) {
/* 1900 */       throw new PrinterException("Paper's imageable width is too small.");
/*      */     }
/*      */ 
/* 1903 */     int j = (int)localDouble1.getHeight();
/* 1904 */     if (j <= 0) {
/* 1905 */       throw new PrinterException("Paper's imageable height is too small.");
/*      */     }
/*      */ 
/* 1913 */     int k = 4194304 / i / 3;
/*      */ 
/* 1915 */     int m = (int)Math.rint(localPaper1.getImageableX() * d1);
/* 1916 */     int n = (int)Math.rint(localPaper1.getImageableY() * d2);
/*      */ 
/* 1927 */     AffineTransform localAffineTransform3 = new AffineTransform();
/* 1928 */     localAffineTransform3.translate(-m, n);
/* 1929 */     localAffineTransform3.translate(0.0D, k);
/* 1930 */     localAffineTransform3.scale(1.0D, -1.0D);
/*      */ 
/* 1940 */     BufferedImage localBufferedImage = new BufferedImage(1, 1, 5);
/*      */ 
/* 1947 */     PeekGraphics localPeekGraphics = createPeekGraphics(localBufferedImage.createGraphics(), this);
/*      */ 
/* 1950 */     Rectangle2D.Double localDouble2 = new Rectangle2D.Double(localPageFormat1.getImageableX(), localPageFormat1.getImageableY(), localPageFormat1.getImageableWidth(), localPageFormat1.getImageableHeight());
/*      */ 
/* 1955 */     localPeekGraphics.transform(localAffineTransform2);
/* 1956 */     localPeekGraphics.translate(-getPhysicalPrintableX(localPaper1) / d1, -getPhysicalPrintableY(localPaper1) / d2);
/*      */ 
/* 1958 */     localPeekGraphics.transform(new AffineTransform(localPageFormat1.getMatrix()));
/* 1959 */     initPrinterGraphics(localPeekGraphics, localDouble2);
/* 1960 */     AffineTransform localAffineTransform4 = localPeekGraphics.getTransform();
/*      */ 
/* 1971 */     setGraphicsConfigInfo(localAffineTransform2, localPaper1.getWidth(), localPaper1.getHeight());
/*      */ 
/* 1973 */     int i1 = localPrintable.print(localPeekGraphics, localPageFormat2, paramInt);
/* 1974 */     debug_println("pageResult " + i1);
/* 1975 */     if (i1 == 0) {
/* 1976 */       debug_println("startPage " + paramInt);
/*      */ 
/* 1983 */       Paper localPaper2 = localPageFormat1.getPaper();
/* 1984 */       boolean bool = (this.previousPaper == null) || (localPaper2.getWidth() != this.previousPaper.getWidth()) || (localPaper2.getHeight() != this.previousPaper.getHeight());
/*      */ 
/* 1988 */       this.previousPaper = localPaper2;
/*      */ 
/* 1990 */       startPage(localPageFormat1, localPrintable, paramInt, bool);
/* 1991 */       Graphics2D localGraphics2D1 = createPathGraphics(localPeekGraphics, this, localPrintable, localPageFormat1, paramInt);
/*      */       Object localObject1;
/*      */       Object localObject2;
/* 2001 */       if (localGraphics2D1 != null) {
/* 2002 */         localGraphics2D1.transform(localAffineTransform2);
/*      */ 
/* 2004 */         localGraphics2D1.translate(-getPhysicalPrintableX(localPaper1) / d1, -getPhysicalPrintableY(localPaper1) / d2);
/*      */ 
/* 2006 */         localGraphics2D1.transform(new AffineTransform(localPageFormat1.getMatrix()));
/* 2007 */         initPrinterGraphics(localGraphics2D1, localDouble2);
/*      */ 
/* 2009 */         this.redrawList.clear();
/*      */ 
/* 2011 */         localObject1 = localGraphics2D1.getTransform();
/*      */ 
/* 2013 */         localPrintable.print(localGraphics2D1, localPageFormat2, paramInt);
/*      */ 
/* 2015 */         for (int i2 = 0; i2 < this.redrawList.size(); i2++) {
/* 2016 */           localObject2 = (GraphicsState)this.redrawList.get(i2);
/* 2017 */           localGraphics2D1.setTransform((AffineTransform)localObject1);
/* 2018 */           ((PathGraphics)localGraphics2D1).redrawRegion(((GraphicsState)localObject2).region, ((GraphicsState)localObject2).sx, ((GraphicsState)localObject2).sy, ((GraphicsState)localObject2).theClip, ((GraphicsState)localObject2).theTransform);
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 2030 */         localObject1 = this.cachedBand;
/* 2031 */         if ((this.cachedBand == null) || (i != this.cachedBandWidth) || (k != this.cachedBandHeight))
/*      */         {
/* 2034 */           localObject1 = new BufferedImage(i, k, 5);
/*      */ 
/* 2036 */           this.cachedBand = ((BufferedImage)localObject1);
/* 2037 */           this.cachedBandWidth = i;
/* 2038 */           this.cachedBandHeight = k;
/*      */         }
/* 2040 */         Graphics2D localGraphics2D2 = ((BufferedImage)localObject1).createGraphics();
/*      */ 
/* 2042 */         localObject2 = new Rectangle2D.Double(0.0D, 0.0D, i, k);
/*      */ 
/* 2045 */         initPrinterGraphics(localGraphics2D2, (Rectangle2D)localObject2);
/*      */ 
/* 2047 */         ProxyGraphics2D localProxyGraphics2D = new ProxyGraphics2D(localGraphics2D2, this);
/*      */ 
/* 2050 */         Graphics2D localGraphics2D3 = ((BufferedImage)localObject1).createGraphics();
/* 2051 */         localGraphics2D3.setColor(Color.white);
/*      */ 
/* 2060 */         ByteInterleavedRaster localByteInterleavedRaster = (ByteInterleavedRaster)((BufferedImage)localObject1).getRaster();
/* 2061 */         byte[] arrayOfByte = localByteInterleavedRaster.getDataStorage();
/*      */ 
/* 2067 */         int i3 = n + j;
/*      */ 
/* 2074 */         int i4 = (int)getPhysicalPrintableX(localPaper1);
/* 2075 */         int i5 = (int)getPhysicalPrintableY(localPaper1);
/*      */ 
/* 2077 */         for (int i6 = 0; i6 <= j; 
/* 2078 */           i6 += k)
/*      */         {
/* 2084 */           localGraphics2D3.fillRect(0, 0, i, k);
/*      */ 
/* 2091 */           localGraphics2D2.setTransform(localAffineTransform1);
/* 2092 */           localGraphics2D2.transform(localAffineTransform3);
/* 2093 */           localAffineTransform3.translate(0.0D, -k);
/*      */ 
/* 2098 */           localGraphics2D2.transform(localAffineTransform2);
/* 2099 */           localGraphics2D2.transform(new AffineTransform(localPageFormat1.getMatrix()));
/*      */ 
/* 2101 */           Rectangle localRectangle = localGraphics2D2.getClipBounds();
/* 2102 */           localRectangle = localAffineTransform4.createTransformedShape(localRectangle).getBounds();
/*      */ 
/* 2104 */           if ((localRectangle == null) || ((localPeekGraphics.hitsDrawingArea(localRectangle)) && (i > 0) && (k > 0)))
/*      */           {
/* 2114 */             int i7 = m - i4;
/* 2115 */             if (i7 < 0) {
/* 2116 */               localGraphics2D2.translate(i7 / d1, 0.0D);
/* 2117 */               i7 = 0;
/*      */             }
/* 2119 */             int i8 = n + i6 - i5;
/* 2120 */             if (i8 < 0) {
/* 2121 */               localGraphics2D2.translate(0.0D, i8 / d2);
/* 2122 */               i8 = 0;
/*      */             }
/*      */ 
/* 2127 */             localProxyGraphics2D.setDelegate((Graphics2D)localGraphics2D2.create());
/* 2128 */             localPrintable.print(localProxyGraphics2D, localPageFormat2, paramInt);
/* 2129 */             localProxyGraphics2D.dispose();
/* 2130 */             printBand(arrayOfByte, i7, i8, i, k);
/*      */           }
/*      */         }
/*      */ 
/* 2134 */         localGraphics2D3.dispose();
/* 2135 */         localGraphics2D2.dispose();
/*      */       }
/*      */ 
/* 2138 */       debug_println("calling endPage " + paramInt);
/* 2139 */       endPage(localPageFormat1, localPrintable, paramInt);
/*      */     }
/*      */ 
/* 2142 */     return i1;
/*      */   }
/*      */ 
/*      */   public void cancel()
/*      */   {
/* 2153 */     synchronized (this) {
/* 2154 */       if (this.performingPrinting) {
/* 2155 */         this.userCancelled = true;
/*      */       }
/* 2157 */       notify();
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isCancelled()
/*      */   {
/* 2168 */     boolean bool = false;
/*      */ 
/* 2170 */     synchronized (this) {
/* 2171 */       bool = (this.performingPrinting) && (this.userCancelled);
/* 2172 */       notify();
/*      */     }
/*      */ 
/* 2175 */     return bool;
/*      */   }
/*      */ 
/*      */   protected Pageable getPageable()
/*      */   {
/* 2182 */     return this.mDocument;
/*      */   }
/*      */ 
/*      */   protected Graphics2D createPathGraphics(PeekGraphics paramPeekGraphics, PrinterJob paramPrinterJob, Printable paramPrintable, PageFormat paramPageFormat, int paramInt)
/*      */   {
/* 2203 */     return null;
/*      */   }
/*      */ 
/*      */   protected PeekGraphics createPeekGraphics(Graphics2D paramGraphics2D, PrinterJob paramPrinterJob)
/*      */   {
/* 2218 */     return new PeekGraphics(paramGraphics2D, paramPrinterJob);
/*      */   }
/*      */ 
/*      */   protected void initPrinterGraphics(Graphics2D paramGraphics2D, Rectangle2D paramRectangle2D)
/*      */   {
/* 2231 */     paramGraphics2D.setClip(paramRectangle2D);
/* 2232 */     paramGraphics2D.setPaint(Color.black);
/*      */   }
/*      */ 
/*      */   public boolean checkAllowedToPrintToFile()
/*      */   {
/*      */     try
/*      */     {
/* 2242 */       throwPrintToFile();
/* 2243 */       return true; } catch (SecurityException localSecurityException) {
/*      */     }
/* 2245 */     return false;
/*      */   }
/*      */ 
/*      */   private void throwPrintToFile()
/*      */   {
/* 2255 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 2256 */     if (localSecurityManager != null) {
/* 2257 */       if (this.printToFilePermission == null) {
/* 2258 */         this.printToFilePermission = new FilePermission("<<ALL FILES>>", "read,write");
/*      */       }
/*      */ 
/* 2261 */       localSecurityManager.checkPermission(this.printToFilePermission);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected String removeControlChars(String paramString)
/*      */   {
/* 2272 */     char[] arrayOfChar1 = paramString.toCharArray();
/* 2273 */     int i = arrayOfChar1.length;
/* 2274 */     char[] arrayOfChar2 = new char[i];
/* 2275 */     int j = 0;
/*      */ 
/* 2277 */     for (int k = 0; k < i; k++) {
/* 2278 */       int m = arrayOfChar1[k];
/* 2279 */       if ((m > 13) || (m < 9) || (m == 11) || (m == 12)) {
/* 2280 */         arrayOfChar2[(j++)] = m;
/*      */       }
/*      */     }
/* 2283 */     if (j == i) {
/* 2284 */       return paramString;
/*      */     }
/* 2286 */     return new String(arrayOfChar2, 0, j);
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  174 */     String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.print.pipeline"));
/*      */ 
/*  178 */     if (str1 != null) {
/*  179 */       if (str1.equalsIgnoreCase("pdl"))
/*  180 */         forcePDL = true;
/*  181 */       else if (str1.equalsIgnoreCase("raster")) {
/*  182 */         forceRaster = true;
/*      */       }
/*      */     }
/*      */ 
/*  186 */     String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.print.shapetext"));
/*      */ 
/*  190 */     if (str2 != null)
/*  191 */       shapeTextProp = true;
/*      */   }
/*      */ 
/*      */   private class GraphicsState
/*      */   {
/*      */     Rectangle2D region;
/*      */     Shape theClip;
/*      */     AffineTransform theTransform;
/*      */     double sx;
/*      */     double sy;
/*      */ 
/*      */     private GraphicsState()
/*      */     {
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.print.RasterPrinterJob
 * JD-Core Version:    0.6.2
 */