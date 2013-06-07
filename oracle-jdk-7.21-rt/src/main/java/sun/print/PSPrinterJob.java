/*      */ package sun.print;
/*      */ 
/*      */ import java.awt.Color;
/*      */ import java.awt.Component;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.Font;
/*      */ import java.awt.FontMetrics;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.Graphics2D;
/*      */ import java.awt.GraphicsEnvironment;
/*      */ import java.awt.HeadlessException;
/*      */ import java.awt.Shape;
/*      */ import java.awt.font.FontRenderContext;
/*      */ import java.awt.geom.AffineTransform;
/*      */ import java.awt.geom.PathIterator;
/*      */ import java.awt.geom.Rectangle2D;
/*      */ import java.awt.image.BufferedImage;
/*      */ import java.awt.print.PageFormat;
/*      */ import java.awt.print.Pageable;
/*      */ import java.awt.print.Paper;
/*      */ import java.awt.print.Printable;
/*      */ import java.awt.print.PrinterException;
/*      */ import java.awt.print.PrinterIOException;
/*      */ import java.awt.print.PrinterJob;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.BufferedOutputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.lang.reflect.Method;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.CharBuffer;
/*      */ import java.nio.charset.CharsetEncoder;
/*      */ import java.nio.charset.CoderMalfunctionError;
/*      */ import java.nio.file.Files;
/*      */ import java.nio.file.Path;
/*      */ import java.nio.file.attribute.FileAttribute;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Date;
/*      */ import java.util.Locale;
/*      */ import java.util.Properties;
/*      */ import javax.print.PrintService;
/*      */ import javax.print.StreamPrintService;
/*      */ import javax.print.attribute.HashPrintRequestAttributeSet;
/*      */ import javax.print.attribute.PrintRequestAttributeSet;
/*      */ import javax.print.attribute.standard.Copies;
/*      */ import javax.print.attribute.standard.Destination;
/*      */ import javax.print.attribute.standard.DialogTypeSelection;
/*      */ import javax.print.attribute.standard.JobName;
/*      */ import javax.print.attribute.standard.Sides;
/*      */ import sun.awt.CharsetString;
/*      */ import sun.awt.FontConfiguration;
/*      */ import sun.awt.FontDescriptor;
/*      */ import sun.awt.PlatformFont;
/*      */ import sun.awt.SunToolkit;
/*      */ import sun.font.Font2D;
/*      */ import sun.font.FontUtilities;
/*      */ 
/*      */ public class PSPrinterJob extends RasterPrinterJob
/*      */ {
/*      */   protected static final int FILL_EVEN_ODD = 1;
/*      */   protected static final int FILL_WINDING = 2;
/*      */   private static final int MAX_PSSTR = 65535;
/*      */   private static final int RED_MASK = 16711680;
/*      */   private static final int GREEN_MASK = 65280;
/*      */   private static final int BLUE_MASK = 255;
/*      */   private static final int RED_SHIFT = 16;
/*      */   private static final int GREEN_SHIFT = 8;
/*      */   private static final int BLUE_SHIFT = 0;
/*      */   private static final int LOWNIBBLE_MASK = 15;
/*      */   private static final int HINIBBLE_MASK = 240;
/*      */   private static final int HINIBBLE_SHIFT = 4;
/*  139 */   private static final byte[] hexDigits = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70 };
/*      */   private static final int PS_XRES = 300;
/*      */   private static final int PS_YRES = 300;
/*      */   private static final String ADOBE_PS_STR = "%!PS-Adobe-3.0";
/*      */   private static final String EOF_COMMENT = "%%EOF";
/*      */   private static final String PAGE_COMMENT = "%%Page: ";
/*      */   private static final String READIMAGEPROC = "/imStr 0 def /imageSrc {currentfile /ASCII85Decode filter /RunLengthDecode filter  imStr readstring pop } def";
/*      */   private static final String COPIES = "/#copies exch def";
/*      */   private static final String PAGE_SAVE = "/pgSave save def";
/*      */   private static final String PAGE_RESTORE = "pgSave restore";
/*      */   private static final String SHOWPAGE = "showpage";
/*      */   private static final String IMAGE_SAVE = "/imSave save def";
/*      */   private static final String IMAGE_STR = " string /imStr exch def";
/*      */   private static final String IMAGE_RESTORE = "imSave restore";
/*      */   private static final String COORD_PREP = " 0 exch translate 1 -1 scale[72 300 div 0 0 72 300 div 0 0]concat";
/*      */   private static final String SetFontName = "F";
/*      */   private static final String DrawStringName = "S";
/*      */   private static final String EVEN_ODD_FILL_STR = "EF";
/*      */   private static final String WINDING_FILL_STR = "WF";
/*      */   private static final String EVEN_ODD_CLIP_STR = "EC";
/*      */   private static final String WINDING_CLIP_STR = "WC";
/*      */   private static final String MOVETO_STR = " M";
/*      */   private static final String LINETO_STR = " L";
/*      */   private static final String CURVETO_STR = " C";
/*      */   private static final String GRESTORE_STR = "R";
/*      */   private static final String GSAVE_STR = "G";
/*      */   private static final String NEWPATH_STR = "N";
/*      */   private static final String CLOSEPATH_STR = "P";
/*      */   private static final String SETRGBCOLOR_STR = " SC";
/*      */   private static final String SETGRAY_STR = " SG";
/*      */   private int mDestType;
/*  258 */   private String mDestination = "lp";
/*      */ 
/*  260 */   private boolean mNoJobSheet = false;
/*      */   private String mOptions;
/*      */   private Font mLastFont;
/*      */   private Color mLastColor;
/*      */   private Shape mLastClip;
/*      */   private AffineTransform mLastTransform;
/*  273 */   private EPSPrinter epsPrinter = null;
/*      */   FontMetrics mCurMetrics;
/*      */   PrintStream mPSStream;
/*      */   File spoolFile;
/*  295 */   private String mFillOpStr = "WF";
/*      */ 
/*  302 */   private String mClipOpStr = "WC";
/*      */ 
/*  307 */   ArrayList mGStateStack = new ArrayList();
/*      */   private float mPenX;
/*      */   private float mPenY;
/*      */   private float mStartPathX;
/*      */   private float mStartPathY;
/*  334 */   private static Properties mFontProps = null;
/*      */ 
/*      */   private static Properties initProps()
/*      */   {
/*  357 */     String str1 = System.getProperty("java.home");
/*      */ 
/*  359 */     if (str1 != null) {
/*  360 */       String str2 = SunToolkit.getStartupLocale().getLanguage();
/*      */       try
/*      */       {
/*  363 */         File localFile = new File(str1 + File.separator + "lib" + File.separator + "psfontj2d.properties." + str2);
/*      */ 
/*  367 */         if (!localFile.canRead())
/*      */         {
/*  369 */           localFile = new File(str1 + File.separator + "lib" + File.separator + "psfont.properties." + str2);
/*      */ 
/*  372 */           if (!localFile.canRead())
/*      */           {
/*  374 */             localFile = new File(str1 + File.separator + "lib" + File.separator + "psfontj2d.properties");
/*      */ 
/*  377 */             if (!localFile.canRead())
/*      */             {
/*  379 */               localFile = new File(str1 + File.separator + "lib" + File.separator + "psfont.properties");
/*      */ 
/*  382 */               if (!localFile.canRead()) {
/*  383 */                 return (Properties)null;
/*      */               }
/*      */             }
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  390 */         BufferedInputStream localBufferedInputStream = new BufferedInputStream(new FileInputStream(localFile.getPath()));
/*      */ 
/*  392 */         Properties localProperties = new Properties();
/*  393 */         localProperties.load(localBufferedInputStream);
/*  394 */         localBufferedInputStream.close();
/*  395 */         return localProperties;
/*      */       } catch (Exception localException) {
/*  397 */         return (Properties)null;
/*      */       }
/*      */     }
/*  400 */     return (Properties)null;
/*      */   }
/*      */ 
/*      */   public boolean printDialog()
/*      */     throws HeadlessException
/*      */   {
/*  422 */     if (GraphicsEnvironment.isHeadless()) {
/*  423 */       throw new HeadlessException();
/*      */     }
/*      */ 
/*  426 */     if (this.attributes == null) {
/*  427 */       this.attributes = new HashPrintRequestAttributeSet();
/*      */     }
/*  429 */     this.attributes.add(new Copies(getCopies()));
/*  430 */     this.attributes.add(new JobName(getJobName(), null));
/*      */ 
/*  432 */     boolean bool = false;
/*  433 */     DialogTypeSelection localDialogTypeSelection = (DialogTypeSelection)this.attributes.get(DialogTypeSelection.class);
/*      */ 
/*  435 */     if (localDialogTypeSelection == DialogTypeSelection.NATIVE)
/*      */     {
/*  438 */       this.attributes.remove(DialogTypeSelection.class);
/*  439 */       bool = printDialog(this.attributes);
/*      */ 
/*  441 */       this.attributes.add(DialogTypeSelection.NATIVE);
/*      */     } else {
/*  443 */       bool = printDialog(this.attributes);
/*      */     }
/*      */ 
/*  446 */     if (bool) {
/*  447 */       JobName localJobName = (JobName)this.attributes.get(JobName.class);
/*  448 */       if (localJobName != null) {
/*  449 */         setJobName(localJobName.getValue());
/*      */       }
/*  451 */       Copies localCopies = (Copies)this.attributes.get(Copies.class);
/*  452 */       if (localCopies != null) {
/*  453 */         setCopies(localCopies.getValue());
/*      */       }
/*      */ 
/*  456 */       Destination localDestination = (Destination)this.attributes.get(Destination.class);
/*      */ 
/*  458 */       if (localDestination != null) {
/*      */         try {
/*  460 */           this.mDestType = 1;
/*  461 */           this.mDestination = new File(localDestination.getURI()).getPath();
/*      */         } catch (Exception localException) {
/*  463 */           this.mDestination = "out.ps";
/*      */         }
/*      */       } else {
/*  466 */         this.mDestType = 0;
/*  467 */         PrintService localPrintService = getPrintService();
/*  468 */         if (localPrintService != null) {
/*  469 */           this.mDestination = localPrintService.getName();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  474 */     return bool;
/*      */   }
/*      */ 
/*      */   protected void startDoc()
/*      */     throws PrinterException
/*      */   {
/*  492 */     if (this.epsPrinter == null)
/*      */     {
/*      */       Object localObject;
/*  493 */       if ((getPrintService() instanceof PSStreamPrintService)) {
/*  494 */         StreamPrintService localStreamPrintService = (StreamPrintService)getPrintService();
/*  495 */         this.mDestType = 2;
/*  496 */         if (localStreamPrintService.isDisposed()) {
/*  497 */           throw new PrinterException("service is disposed");
/*      */         }
/*  499 */         localObject = localStreamPrintService.getOutputStream();
/*  500 */         if (localObject == null)
/*  501 */           throw new PrinterException("Null output stream");
/*      */       }
/*      */       else
/*      */       {
/*  505 */         this.mNoJobSheet = this.noJobSheet;
/*  506 */         if (this.destinationAttr != null) {
/*  507 */           this.mDestType = 1;
/*  508 */           this.mDestination = this.destinationAttr;
/*      */         }
/*  510 */         if (this.mDestType == 1) {
/*      */           try {
/*  512 */             this.spoolFile = new File(this.mDestination);
/*  513 */             localObject = new FileOutputStream(this.spoolFile);
/*      */           } catch (IOException localIOException) {
/*  515 */             throw new PrinterIOException(localIOException);
/*      */           }
/*      */         } else {
/*  518 */           PrinterOpener localPrinterOpener = new PrinterOpener(null);
/*  519 */           AccessController.doPrivileged(localPrinterOpener);
/*  520 */           if (localPrinterOpener.pex != null) {
/*  521 */             throw localPrinterOpener.pex;
/*      */           }
/*  523 */           localObject = localPrinterOpener.result;
/*      */         }
/*      */       }
/*      */ 
/*  527 */       this.mPSStream = new PrintStream(new BufferedOutputStream((OutputStream)localObject));
/*  528 */       this.mPSStream.println("%!PS-Adobe-3.0");
/*      */     }
/*      */ 
/*  531 */     this.mPSStream.println("%%BeginProlog");
/*  532 */     this.mPSStream.println("/imStr 0 def /imageSrc {currentfile /ASCII85Decode filter /RunLengthDecode filter  imStr readstring pop } def");
/*  533 */     this.mPSStream.println("/BD {bind def} bind def");
/*  534 */     this.mPSStream.println("/D {def} BD");
/*  535 */     this.mPSStream.println("/C {curveto} BD");
/*  536 */     this.mPSStream.println("/L {lineto} BD");
/*  537 */     this.mPSStream.println("/M {moveto} BD");
/*  538 */     this.mPSStream.println("/R {grestore} BD");
/*  539 */     this.mPSStream.println("/G {gsave} BD");
/*  540 */     this.mPSStream.println("/N {newpath} BD");
/*  541 */     this.mPSStream.println("/P {closepath} BD");
/*  542 */     this.mPSStream.println("/EC {eoclip} BD");
/*  543 */     this.mPSStream.println("/WC {clip} BD");
/*  544 */     this.mPSStream.println("/EF {eofill} BD");
/*  545 */     this.mPSStream.println("/WF {fill} BD");
/*  546 */     this.mPSStream.println("/SG {setgray} BD");
/*  547 */     this.mPSStream.println("/SC {setrgbcolor} BD");
/*  548 */     this.mPSStream.println("/ISOF {");
/*  549 */     this.mPSStream.println("     dup findfont dup length 1 add dict begin {");
/*  550 */     this.mPSStream.println("             1 index /FID eq {pop pop} {D} ifelse");
/*  551 */     this.mPSStream.println("     } forall /Encoding ISOLatin1Encoding D");
/*  552 */     this.mPSStream.println("     currentdict end definefont");
/*  553 */     this.mPSStream.println("} BD");
/*  554 */     this.mPSStream.println("/NZ {dup 1 lt {pop 1} if} BD");
/*      */ 
/*  563 */     this.mPSStream.println("/S {");
/*  564 */     this.mPSStream.println("     moveto 1 index stringwidth pop NZ sub");
/*  565 */     this.mPSStream.println("     1 index length 1 sub NZ div 0");
/*  566 */     this.mPSStream.println("     3 2 roll ashow newpath} BD");
/*  567 */     this.mPSStream.println("/FL [");
/*  568 */     if (mFontProps == null) {
/*  569 */       this.mPSStream.println(" /Helvetica ISOF");
/*  570 */       this.mPSStream.println(" /Helvetica-Bold ISOF");
/*  571 */       this.mPSStream.println(" /Helvetica-Oblique ISOF");
/*  572 */       this.mPSStream.println(" /Helvetica-BoldOblique ISOF");
/*  573 */       this.mPSStream.println(" /Times-Roman ISOF");
/*  574 */       this.mPSStream.println(" /Times-Bold ISOF");
/*  575 */       this.mPSStream.println(" /Times-Italic ISOF");
/*  576 */       this.mPSStream.println(" /Times-BoldItalic ISOF");
/*  577 */       this.mPSStream.println(" /Courier ISOF");
/*  578 */       this.mPSStream.println(" /Courier-Bold ISOF");
/*  579 */       this.mPSStream.println(" /Courier-Oblique ISOF");
/*  580 */       this.mPSStream.println(" /Courier-BoldOblique ISOF");
/*      */     } else {
/*  582 */       int i = Integer.parseInt(mFontProps.getProperty("font.num", "9"));
/*  583 */       for (int j = 0; j < i; j++) {
/*  584 */         this.mPSStream.println("    /" + mFontProps.getProperty(new StringBuilder().append("font.").append(String.valueOf(j)).toString(), "Courier ISOF"));
/*      */       }
/*      */     }
/*      */ 
/*  588 */     this.mPSStream.println("] D");
/*      */ 
/*  590 */     this.mPSStream.println("/F {");
/*  591 */     this.mPSStream.println("     FL exch get exch scalefont");
/*  592 */     this.mPSStream.println("     [1 0 0 -1 0 0] makefont setfont} BD");
/*      */ 
/*  594 */     this.mPSStream.println("%%EndProlog");
/*      */ 
/*  596 */     this.mPSStream.println("%%BeginSetup");
/*  597 */     if (this.epsPrinter == null)
/*      */     {
/*  599 */       PageFormat localPageFormat = getPageable().getPageFormat(0);
/*  600 */       double d1 = localPageFormat.getPaper().getHeight();
/*  601 */       double d2 = localPageFormat.getPaper().getWidth();
/*      */ 
/*  605 */       this.mPSStream.print("<< /PageSize [" + d2 + " " + d1 + "]");
/*      */ 
/*  608 */       final PrintService localPrintService = getPrintService();
/*  609 */       Boolean localBoolean = (Boolean)AccessController.doPrivileged(new PrivilegedAction()
/*      */       {
/*      */         public Object run() {
/*      */           try {
/*  613 */             Class localClass = Class.forName("sun.print.IPPPrintService");
/*  614 */             if (localClass.isInstance(localPrintService)) {
/*  615 */               Method localMethod = localClass.getMethod("isPostscript", (Class[])null);
/*      */ 
/*  617 */               return (Boolean)localMethod.invoke(localPrintService, (Object[])null);
/*      */             }
/*      */           } catch (Throwable localThrowable) {
/*      */           }
/*  621 */           return Boolean.TRUE;
/*      */         }
/*      */       });
/*  625 */       if (localBoolean.booleanValue()) {
/*  626 */         this.mPSStream.print(" /DeferredMediaSelection true");
/*      */       }
/*      */ 
/*  629 */       this.mPSStream.print(" /ImagingBBox null /ManualFeed false");
/*  630 */       this.mPSStream.print(isCollated() ? " /Collate true" : "");
/*  631 */       this.mPSStream.print(" /NumCopies " + getCopiesInt());
/*      */ 
/*  633 */       if (this.sidesAttr != Sides.ONE_SIDED) {
/*  634 */         if (this.sidesAttr == Sides.TWO_SIDED_LONG_EDGE)
/*  635 */           this.mPSStream.print(" /Duplex true ");
/*  636 */         else if (this.sidesAttr == Sides.TWO_SIDED_SHORT_EDGE) {
/*  637 */           this.mPSStream.print(" /Duplex true /Tumble true ");
/*      */         }
/*      */       }
/*  640 */       this.mPSStream.println(" >> setpagedevice ");
/*      */     }
/*  642 */     this.mPSStream.println("%%EndSetup");
/*      */   }
/*      */ 
/*      */   protected void abortDoc()
/*      */   {
/*  709 */     if ((this.mPSStream != null) && (this.mDestType != 2)) {
/*  710 */       this.mPSStream.close();
/*      */     }
/*  712 */     AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public Object run()
/*      */       {
/*  716 */         if ((PSPrinterJob.this.spoolFile != null) && (PSPrinterJob.this.spoolFile.exists())) {
/*  717 */           PSPrinterJob.this.spoolFile.delete();
/*      */         }
/*  719 */         return null;
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   protected void endDoc()
/*      */     throws PrinterException
/*      */   {
/*  730 */     if (this.mPSStream != null) {
/*  731 */       this.mPSStream.println("%%EOF");
/*  732 */       this.mPSStream.flush();
/*  733 */       if (this.mDestType != 2) {
/*  734 */         this.mPSStream.close();
/*      */       }
/*      */     }
/*  737 */     if (this.mDestType == 0) {
/*  738 */       if (getPrintService() != null) {
/*  739 */         this.mDestination = getPrintService().getName();
/*      */       }
/*  741 */       PrinterSpooler localPrinterSpooler = new PrinterSpooler(null);
/*  742 */       AccessController.doPrivileged(localPrinterSpooler);
/*  743 */       if (localPrinterSpooler.pex != null)
/*  744 */         throw localPrinterSpooler.pex;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void startPage(PageFormat paramPageFormat, Printable paramPrintable, int paramInt, boolean paramBoolean)
/*      */     throws PrinterException
/*      */   {
/*  757 */     double d1 = paramPageFormat.getPaper().getHeight();
/*  758 */     double d2 = paramPageFormat.getPaper().getWidth();
/*  759 */     int i = paramInt + 1;
/*      */ 
/*  765 */     this.mGStateStack = new ArrayList();
/*  766 */     this.mGStateStack.add(new GState());
/*      */ 
/*  768 */     this.mPSStream.println("%%Page: " + i + " " + i);
/*      */ 
/*  772 */     if ((paramInt > 0) && (paramBoolean))
/*      */     {
/*  774 */       this.mPSStream.print("<< /PageSize [" + d2 + " " + d1 + "]");
/*      */ 
/*  777 */       final PrintService localPrintService = getPrintService();
/*  778 */       Boolean localBoolean = (Boolean)AccessController.doPrivileged(new PrivilegedAction()
/*      */       {
/*      */         public Object run()
/*      */         {
/*      */           try
/*      */           {
/*  784 */             Class localClass = Class.forName("sun.print.IPPPrintService");
/*      */ 
/*  786 */             if (localClass.isInstance(localPrintService)) {
/*  787 */               Method localMethod = localClass.getMethod("isPostscript", (Class[])null);
/*      */ 
/*  790 */               return (Boolean)localMethod.invoke(localPrintService, (Object[])null);
/*      */             }
/*      */           }
/*      */           catch (Throwable localThrowable)
/*      */           {
/*      */           }
/*  796 */           return Boolean.TRUE;
/*      */         }
/*      */       });
/*  801 */       if (localBoolean.booleanValue()) {
/*  802 */         this.mPSStream.print(" /DeferredMediaSelection true");
/*      */       }
/*  804 */       this.mPSStream.println(" >> setpagedevice");
/*      */     }
/*  806 */     this.mPSStream.println("/pgSave save def");
/*  807 */     this.mPSStream.println(d1 + " 0 exch translate 1 -1 scale[72 300 div 0 0 72 300 div 0 0]concat");
/*      */   }
/*      */ 
/*      */   protected void endPage(PageFormat paramPageFormat, Printable paramPrintable, int paramInt)
/*      */     throws PrinterException
/*      */   {
/*  818 */     this.mPSStream.println("pgSave restore");
/*  819 */     this.mPSStream.println("showpage");
/*      */   }
/*      */ 
/*      */   protected void drawImageBGR(byte[] paramArrayOfByte, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7, float paramFloat8, int paramInt1, int paramInt2)
/*      */   {
/*  843 */     setTransform(new AffineTransform());
/*  844 */     prepDrawing();
/*      */ 
/*  846 */     int i = (int)paramFloat7;
/*  847 */     int j = (int)paramFloat8;
/*      */ 
/*  849 */     this.mPSStream.println("/imSave save def");
/*      */ 
/*  853 */     int k = 3 * i;
/*  854 */     while (k > 65535) {
/*  855 */       k /= 2;
/*      */     }
/*      */ 
/*  858 */     this.mPSStream.println(k + " string /imStr exch def");
/*      */ 
/*  862 */     this.mPSStream.println("[" + paramFloat3 + " 0 " + "0 " + paramFloat4 + " " + paramFloat1 + " " + paramFloat2 + "]concat");
/*      */ 
/*  869 */     this.mPSStream.println(i + " " + j + " " + 8 + "[" + i + " 0 " + "0 " + j + " 0 " + 0 + "]" + "/imageSrc load false 3 colorimage");
/*      */ 
/*  877 */     int m = 0;
/*  878 */     byte[] arrayOfByte1 = new byte[i * 3];
/*      */     try
/*      */     {
/*  884 */       m = (int)paramFloat6 * paramInt1;
/*      */ 
/*  886 */       for (int n = 0; n < j; n++)
/*      */       {
/*  891 */         m += (int)paramFloat5;
/*      */ 
/*  893 */         m = swapBGRtoRGB(paramArrayOfByte, m, arrayOfByte1);
/*  894 */         byte[] arrayOfByte2 = rlEncode(arrayOfByte1);
/*  895 */         byte[] arrayOfByte3 = ascii85Encode(arrayOfByte2);
/*  896 */         this.mPSStream.write(arrayOfByte3);
/*  897 */         this.mPSStream.println("");
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/*      */     }
/*      */ 
/*  909 */     this.mPSStream.println("imSave restore");
/*      */   }
/*      */ 
/*      */   protected void printBand(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */     throws PrinterException
/*      */   {
/*  925 */     this.mPSStream.println("/imSave save def");
/*      */ 
/*  929 */     int i = 3 * paramInt3;
/*  930 */     while (i > 65535) {
/*  931 */       i /= 2;
/*      */     }
/*      */ 
/*  934 */     this.mPSStream.println(i + " string /imStr exch def");
/*      */ 
/*  938 */     this.mPSStream.println("[" + paramInt3 + " 0 " + "0 " + paramInt4 + " " + paramInt1 + " " + paramInt2 + "]concat");
/*      */ 
/*  945 */     this.mPSStream.println(paramInt3 + " " + paramInt4 + " " + 8 + "[" + paramInt3 + " 0 " + "0 " + -paramInt4 + " 0 " + paramInt4 + "]" + "/imageSrc load false 3 colorimage");
/*      */ 
/*  953 */     int j = 0;
/*  954 */     byte[] arrayOfByte1 = new byte[paramInt3 * 3];
/*      */     try
/*      */     {
/*  957 */       for (int k = 0; k < paramInt4; k++) {
/*  958 */         j = swapBGRtoRGB(paramArrayOfByte, j, arrayOfByte1);
/*  959 */         byte[] arrayOfByte2 = rlEncode(arrayOfByte1);
/*  960 */         byte[] arrayOfByte3 = ascii85Encode(arrayOfByte2);
/*  961 */         this.mPSStream.write(arrayOfByte3);
/*  962 */         this.mPSStream.println("");
/*      */       }
/*      */     }
/*      */     catch (IOException localIOException) {
/*  966 */       throw new PrinterIOException(localIOException);
/*      */     }
/*      */ 
/*  969 */     this.mPSStream.println("imSave restore");
/*      */   }
/*      */ 
/*      */   protected Graphics2D createPathGraphics(PeekGraphics paramPeekGraphics, PrinterJob paramPrinterJob, Printable paramPrintable, PageFormat paramPageFormat, int paramInt)
/*      */   {
/*  992 */     PeekMetrics localPeekMetrics = paramPeekGraphics.getMetrics();
/*      */     PSPathGraphics localPSPathGraphics;
/*  998 */     if ((!forcePDL) && ((forceRaster == true) || (localPeekMetrics.hasNonSolidColors()) || (localPeekMetrics.hasCompositing())))
/*      */     {
/* 1002 */       localPSPathGraphics = null;
/*      */     }
/*      */     else {
/* 1005 */       BufferedImage localBufferedImage = new BufferedImage(8, 8, 1);
/*      */ 
/* 1007 */       Graphics2D localGraphics2D = localBufferedImage.createGraphics();
/* 1008 */       boolean bool = !paramPeekGraphics.getAWTDrawingOnly();
/*      */ 
/* 1010 */       localPSPathGraphics = new PSPathGraphics(localGraphics2D, paramPrinterJob, paramPrintable, paramPageFormat, paramInt, bool);
/*      */     }
/*      */ 
/* 1015 */     return localPSPathGraphics;
/*      */   }
/*      */ 
/*      */   protected void selectClipPath()
/*      */   {
/* 1024 */     this.mPSStream.println(this.mClipOpStr);
/*      */   }
/*      */ 
/*      */   protected void setClip(Shape paramShape)
/*      */   {
/* 1029 */     this.mLastClip = paramShape;
/*      */   }
/*      */ 
/*      */   protected void setTransform(AffineTransform paramAffineTransform) {
/* 1033 */     this.mLastTransform = paramAffineTransform;
/*      */   }
/*      */ 
/*      */   protected boolean setFont(Font paramFont)
/*      */   {
/* 1041 */     this.mLastFont = paramFont;
/* 1042 */     return true;
/*      */   }
/*      */ 
/*      */   private int[] getPSFontIndexArray(Font paramFont, CharsetString[] paramArrayOfCharsetString)
/*      */   {
/* 1053 */     int[] arrayOfInt = null;
/*      */ 
/* 1055 */     if (mFontProps != null) {
/* 1056 */       arrayOfInt = new int[paramArrayOfCharsetString.length];
/*      */     }
/*      */ 
/* 1059 */     for (int i = 0; (i < paramArrayOfCharsetString.length) && (arrayOfInt != null); i++)
/*      */     {
/* 1063 */       CharsetString localCharsetString = paramArrayOfCharsetString[i];
/*      */ 
/* 1065 */       CharsetEncoder localCharsetEncoder = localCharsetString.fontDescriptor.encoder;
/* 1066 */       String str1 = localCharsetString.fontDescriptor.getFontCharsetName();
/*      */ 
/* 1073 */       if ("Symbol".equals(str1))
/* 1074 */         str1 = "symbol";
/* 1075 */       else if (("WingDings".equals(str1)) || ("X11Dingbats".equals(str1)))
/*      */       {
/* 1077 */         str1 = "dingbats";
/*      */       }
/* 1079 */       else str1 = makeCharsetName(str1, localCharsetString.charsetChars);
/*      */ 
/* 1082 */       int j = paramFont.getStyle() | FontUtilities.getFont2D(paramFont).getStyle();
/*      */ 
/* 1085 */       String str2 = FontConfiguration.getStyleString(j);
/*      */ 
/* 1091 */       String str3 = paramFont.getFamily().toLowerCase(Locale.ENGLISH);
/* 1092 */       str3 = str3.replace(' ', '_');
/* 1093 */       String str4 = mFontProps.getProperty(str3, "");
/*      */ 
/* 1098 */       String str5 = mFontProps.getProperty(str4 + "." + str1 + "." + str2, null);
/*      */ 
/* 1102 */       if (str5 != null)
/*      */       {
/*      */         try
/*      */         {
/* 1107 */           arrayOfInt[i] = Integer.parseInt(mFontProps.getProperty(str5));
/*      */         }
/*      */         catch (NumberFormatException localNumberFormatException)
/*      */         {
/* 1116 */           arrayOfInt = null;
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 1123 */         arrayOfInt = null;
/*      */       }
/*      */     }
/*      */ 
/* 1127 */     return arrayOfInt;
/*      */   }
/*      */ 
/*      */   private static String escapeParens(String paramString)
/*      */   {
/* 1132 */     if ((paramString.indexOf('(') == -1) && (paramString.indexOf(')') == -1)) {
/* 1133 */       return paramString;
/*      */     }
/* 1135 */     int i = 0;
/* 1136 */     int j = 0;
/* 1137 */     while ((j = paramString.indexOf('(', j)) != -1) {
/* 1138 */       i++;
/* 1139 */       j++;
/*      */     }
/* 1141 */     j = 0;
/* 1142 */     while ((j = paramString.indexOf(')', j)) != -1) {
/* 1143 */       i++;
/* 1144 */       j++;
/*      */     }
/* 1146 */     char[] arrayOfChar1 = paramString.toCharArray();
/* 1147 */     char[] arrayOfChar2 = new char[arrayOfChar1.length + i];
/* 1148 */     j = 0;
/* 1149 */     for (int k = 0; k < arrayOfChar1.length; k++) {
/* 1150 */       if ((arrayOfChar1[k] == '(') || (arrayOfChar1[k] == ')')) {
/* 1151 */         arrayOfChar2[(j++)] = '\\';
/*      */       }
/* 1153 */       arrayOfChar2[(j++)] = arrayOfChar1[k];
/*      */     }
/* 1155 */     return new String(arrayOfChar2);
/*      */   }
/*      */ 
/*      */   protected int platformFontCount(Font paramFont, String paramString)
/*      */   {
/* 1165 */     if (mFontProps == null) {
/* 1166 */       return 0;
/*      */     }
/* 1168 */     CharsetString[] arrayOfCharsetString = ((PlatformFont)paramFont.getPeer()).makeMultiCharsetString(paramString, false);
/*      */ 
/* 1170 */     if (arrayOfCharsetString == null)
/*      */     {
/* 1172 */       return 0;
/*      */     }
/* 1174 */     int[] arrayOfInt = getPSFontIndexArray(paramFont, arrayOfCharsetString);
/* 1175 */     return arrayOfInt == null ? 0 : arrayOfInt.length;
/*      */   }
/*      */ 
/*      */   protected boolean textOut(Graphics paramGraphics, String paramString, float paramFloat1, float paramFloat2, Font paramFont, FontRenderContext paramFontRenderContext, float paramFloat3)
/*      */   {
/* 1181 */     boolean bool = true;
/*      */ 
/* 1183 */     if (mFontProps == null) {
/* 1184 */       return false;
/*      */     }
/* 1186 */     prepDrawing();
/*      */ 
/* 1198 */     paramString = removeControlChars(paramString);
/* 1199 */     if (paramString.length() == 0) {
/* 1200 */       return true;
/*      */     }
/* 1202 */     CharsetString[] arrayOfCharsetString = ((PlatformFont)paramFont.getPeer()).makeMultiCharsetString(paramString, false);
/*      */ 
/* 1205 */     if (arrayOfCharsetString == null)
/*      */     {
/* 1207 */       return false;
/*      */     }
/*      */ 
/* 1215 */     int[] arrayOfInt = getPSFontIndexArray(paramFont, arrayOfCharsetString);
/* 1216 */     if (arrayOfInt != null)
/*      */     {
/* 1218 */       for (int i = 0; i < arrayOfCharsetString.length; i++) {
/* 1219 */         CharsetString localCharsetString = arrayOfCharsetString[i];
/* 1220 */         CharsetEncoder localCharsetEncoder = localCharsetString.fontDescriptor.encoder;
/*      */ 
/* 1222 */         StringBuffer localStringBuffer = new StringBuffer();
/* 1223 */         byte[] arrayOfByte = new byte[localCharsetString.length * 2];
/* 1224 */         int j = 0;
/*      */         try {
/* 1226 */           ByteBuffer localByteBuffer = ByteBuffer.wrap(arrayOfByte);
/* 1227 */           localCharsetEncoder.encode(CharBuffer.wrap(localCharsetString.charsetChars, localCharsetString.offset, localCharsetString.length), localByteBuffer, true);
/*      */ 
/* 1231 */           localByteBuffer.flip();
/* 1232 */           j = localByteBuffer.limit();
/*      */         } catch (IllegalStateException localIllegalStateException) {
/* 1234 */           continue;
/*      */         } catch (CoderMalfunctionError localCoderMalfunctionError) {
/* 1236 */           continue;
/*      */         }
/*      */         float f;
/* 1244 */         if ((arrayOfCharsetString.length == 1) && (paramFloat3 != 0.0F)) {
/* 1245 */           f = paramFloat3;
/*      */         } else {
/* 1247 */           Rectangle2D localRectangle2D = paramFont.getStringBounds(localCharsetString.charsetChars, localCharsetString.offset, localCharsetString.offset + localCharsetString.length, paramFontRenderContext);
/*      */ 
/* 1252 */           f = (float)localRectangle2D.getWidth();
/*      */         }
/*      */ 
/* 1256 */         if (f == 0.0F) {
/* 1257 */           return bool;
/*      */         }
/* 1259 */         localStringBuffer.append('<');
/* 1260 */         for (int k = 0; k < j; k++) {
/* 1261 */           int m = arrayOfByte[k];
/*      */ 
/* 1263 */           String str = Integer.toHexString(m);
/* 1264 */           int n = str.length();
/* 1265 */           if (n > 2)
/* 1266 */             str = str.substring(n - 2, n);
/* 1267 */           else if (n == 1)
/* 1268 */             str = "0" + str;
/* 1269 */           else if (n == 0) {
/* 1270 */             str = "00";
/*      */           }
/* 1272 */           localStringBuffer.append(str);
/*      */         }
/* 1274 */         localStringBuffer.append('>');
/*      */ 
/* 1279 */         getGState().emitPSFont(arrayOfInt[i], paramFont.getSize2D());
/*      */ 
/* 1282 */         this.mPSStream.println(localStringBuffer.toString() + " " + f + " " + paramFloat1 + " " + paramFloat2 + " " + "S");
/*      */ 
/* 1285 */         paramFloat1 += f;
/*      */       }
/*      */     }
/* 1288 */     else bool = false;
/*      */ 
/* 1292 */     return bool;
/*      */   }
/*      */ 
/*      */   protected void setFillMode(int paramInt)
/*      */   {
/* 1302 */     switch (paramInt)
/*      */     {
/*      */     case 1:
/* 1305 */       this.mFillOpStr = "EF";
/* 1306 */       this.mClipOpStr = "EC";
/* 1307 */       break;
/*      */     case 2:
/* 1310 */       this.mFillOpStr = "WF";
/* 1311 */       this.mClipOpStr = "WC";
/* 1312 */       break;
/*      */     default:
/* 1315 */       throw new IllegalArgumentException();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void setColor(Color paramColor)
/*      */   {
/* 1325 */     this.mLastColor = paramColor;
/*      */   }
/*      */ 
/*      */   protected void fillPath()
/*      */   {
/* 1334 */     this.mPSStream.println(this.mFillOpStr);
/*      */   }
/*      */ 
/*      */   protected void beginPath()
/*      */   {
/* 1342 */     prepDrawing();
/* 1343 */     this.mPSStream.println("N");
/*      */ 
/* 1345 */     this.mPenX = 0.0F;
/* 1346 */     this.mPenY = 0.0F;
/*      */   }
/*      */ 
/*      */   protected void closeSubpath()
/*      */   {
/* 1356 */     this.mPSStream.println("P");
/*      */ 
/* 1358 */     this.mPenX = this.mStartPathX;
/* 1359 */     this.mPenY = this.mStartPathY;
/*      */   }
/*      */ 
/*      */   protected void moveTo(float paramFloat1, float paramFloat2)
/*      */   {
/* 1369 */     this.mPSStream.println(trunc(paramFloat1) + " " + trunc(paramFloat2) + " M");
/*      */ 
/* 1376 */     this.mStartPathX = paramFloat1;
/* 1377 */     this.mStartPathY = paramFloat2;
/*      */ 
/* 1379 */     this.mPenX = paramFloat1;
/* 1380 */     this.mPenY = paramFloat2;
/*      */   }
/*      */ 
/*      */   protected void lineTo(float paramFloat1, float paramFloat2)
/*      */   {
/* 1388 */     this.mPSStream.println(trunc(paramFloat1) + " " + trunc(paramFloat2) + " L");
/*      */ 
/* 1390 */     this.mPenX = paramFloat1;
/* 1391 */     this.mPenY = paramFloat2;
/*      */   }
/*      */ 
/*      */   protected void bezierTo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
/*      */   {
/* 1408 */     this.mPSStream.println(trunc(paramFloat1) + " " + trunc(paramFloat2) + " " + trunc(paramFloat3) + " " + trunc(paramFloat4) + " " + trunc(paramFloat5) + " " + trunc(paramFloat6) + " C");
/*      */ 
/* 1414 */     this.mPenX = paramFloat5;
/* 1415 */     this.mPenY = paramFloat6;
/*      */   }
/*      */ 
/*      */   String trunc(float paramFloat) {
/* 1419 */     float f = Math.abs(paramFloat);
/* 1420 */     if ((f >= 1.0F) && (f <= 1000.0F)) {
/* 1421 */       paramFloat = Math.round(paramFloat * 1000.0F) / 1000.0F;
/*      */     }
/* 1423 */     return Float.toString(paramFloat);
/*      */   }
/*      */ 
/*      */   protected float getPenX()
/*      */   {
/* 1432 */     return this.mPenX;
/*      */   }
/*      */ 
/*      */   protected float getPenY()
/*      */   {
/* 1440 */     return this.mPenY;
/*      */   }
/*      */ 
/*      */   protected double getXRes()
/*      */   {
/* 1448 */     return 300.0D;
/*      */   }
/*      */ 
/*      */   protected double getYRes()
/*      */   {
/* 1455 */     return 300.0D;
/*      */   }
/*      */ 
/*      */   protected double getPhysicalPrintableX(Paper paramPaper)
/*      */   {
/* 1463 */     return 0.0D;
/*      */   }
/*      */ 
/*      */   protected double getPhysicalPrintableY(Paper paramPaper)
/*      */   {
/* 1472 */     return 0.0D;
/*      */   }
/*      */ 
/*      */   protected double getPhysicalPrintableWidth(Paper paramPaper) {
/* 1476 */     return paramPaper.getImageableWidth();
/*      */   }
/*      */ 
/*      */   protected double getPhysicalPrintableHeight(Paper paramPaper) {
/* 1480 */     return paramPaper.getImageableHeight();
/*      */   }
/*      */ 
/*      */   protected double getPhysicalPageWidth(Paper paramPaper) {
/* 1484 */     return paramPaper.getWidth();
/*      */   }
/*      */ 
/*      */   protected double getPhysicalPageHeight(Paper paramPaper) {
/* 1488 */     return paramPaper.getHeight();
/*      */   }
/*      */ 
/*      */   protected int getNoncollatedCopies()
/*      */   {
/* 1498 */     return 1;
/*      */   }
/*      */ 
/*      */   protected int getCollatedCopies() {
/* 1502 */     return 1;
/*      */   }
/*      */ 
/*      */   private String[] printExecCmd(String paramString1, String paramString2, boolean paramBoolean, String paramString3, int paramInt, String paramString4)
/*      */   {
/* 1508 */     int i = 1;
/* 1509 */     int j = 2;
/* 1510 */     int k = 4;
/* 1511 */     int m = 8;
/* 1512 */     int n = 16;
/* 1513 */     int i1 = 0;
/*      */ 
/* 1515 */     int i2 = 2;
/* 1516 */     int i3 = 0;
/*      */ 
/* 1518 */     if ((paramString1 != null) && (!paramString1.equals("")) && (!paramString1.equals("lp"))) {
/* 1519 */       i1 |= i;
/* 1520 */       i2++;
/*      */     }
/* 1522 */     if ((paramString2 != null) && (!paramString2.equals(""))) {
/* 1523 */       i1 |= j;
/* 1524 */       i2++;
/*      */     }
/* 1526 */     if ((paramString3 != null) && (!paramString3.equals(""))) {
/* 1527 */       i1 |= k;
/* 1528 */       i2++;
/*      */     }
/* 1530 */     if (paramInt > 1) {
/* 1531 */       i1 |= m;
/* 1532 */       i2++;
/*      */     }
/* 1534 */     if (paramBoolean) {
/* 1535 */       i1 |= n;
/* 1536 */       i2++;
/*      */     }
/*      */ 
/* 1539 */     String str = System.getProperty("os.name");
/*      */     String[] arrayOfString;
/* 1540 */     if ((str.equals("Linux")) || (str.contains("OS X"))) {
/* 1541 */       arrayOfString = new String[i2];
/* 1542 */       arrayOfString[(i3++)] = "/usr/bin/lpr";
/* 1543 */       if ((i1 & i) != 0) {
/* 1544 */         arrayOfString[(i3++)] = ("-P" + paramString1);
/*      */       }
/* 1546 */       if ((i1 & k) != 0) {
/* 1547 */         arrayOfString[(i3++)] = ("-J" + paramString3);
/*      */       }
/* 1549 */       if ((i1 & m) != 0) {
/* 1550 */         arrayOfString[(i3++)] = ("-#" + paramInt);
/*      */       }
/* 1552 */       if ((i1 & n) != 0) {
/* 1553 */         arrayOfString[(i3++)] = "-h";
/*      */       }
/* 1555 */       if ((i1 & j) != 0)
/* 1556 */         arrayOfString[(i3++)] = new String(paramString2);
/*      */     }
/*      */     else {
/* 1559 */       i2++;
/* 1560 */       arrayOfString = new String[i2];
/* 1561 */       arrayOfString[(i3++)] = "/usr/bin/lp";
/* 1562 */       arrayOfString[(i3++)] = "-c";
/* 1563 */       if ((i1 & i) != 0) {
/* 1564 */         arrayOfString[(i3++)] = ("-d" + paramString1);
/*      */       }
/* 1566 */       if ((i1 & k) != 0) {
/* 1567 */         arrayOfString[(i3++)] = ("-t" + paramString3);
/*      */       }
/* 1569 */       if ((i1 & m) != 0) {
/* 1570 */         arrayOfString[(i3++)] = ("-n" + paramInt);
/*      */       }
/* 1572 */       if ((i1 & n) != 0) {
/* 1573 */         arrayOfString[(i3++)] = "-o nobanner";
/*      */       }
/* 1575 */       if ((i1 & j) != 0) {
/* 1576 */         arrayOfString[(i3++)] = ("-o" + paramString2);
/*      */       }
/*      */     }
/* 1579 */     arrayOfString[(i3++)] = paramString4;
/* 1580 */     return arrayOfString;
/*      */   }
/*      */ 
/*      */   private static int swapBGRtoRGB(byte[] paramArrayOfByte1, int paramInt, byte[] paramArrayOfByte2) {
/* 1584 */     int i = 0;
/* 1585 */     while ((paramInt < paramArrayOfByte1.length - 2) && (i < paramArrayOfByte2.length - 2)) {
/* 1586 */       paramArrayOfByte2[(i++)] = paramArrayOfByte1[(paramInt + 2)];
/* 1587 */       paramArrayOfByte2[(i++)] = paramArrayOfByte1[(paramInt + 1)];
/* 1588 */       paramArrayOfByte2[(i++)] = paramArrayOfByte1[(paramInt + 0)];
/* 1589 */       paramInt += 3;
/*      */     }
/* 1591 */     return paramInt;
/*      */   }
/*      */ 
/*      */   private String makeCharsetName(String paramString, char[] paramArrayOfChar)
/*      */   {
/* 1601 */     if ((paramString.equals("Cp1252")) || (paramString.equals("ISO8859_1")))
/* 1602 */       return "latin1";
/*      */     int i;
/* 1603 */     if (paramString.equals("UTF8"))
/*      */     {
/* 1605 */       for (i = 0; i < paramArrayOfChar.length; i++) {
/* 1606 */         if (paramArrayOfChar[i] > 'ÿ') {
/* 1607 */           return paramString.toLowerCase();
/*      */         }
/*      */       }
/* 1610 */       return "latin1";
/* 1611 */     }if (paramString.startsWith("ISO8859"))
/*      */     {
/* 1613 */       for (i = 0; i < paramArrayOfChar.length; i++) {
/* 1614 */         if (paramArrayOfChar[i] > '') {
/* 1615 */           return paramString.toLowerCase();
/*      */         }
/*      */       }
/* 1618 */       return "latin1";
/*      */     }
/* 1620 */     return paramString.toLowerCase();
/*      */   }
/*      */ 
/*      */   private void prepDrawing()
/*      */   {
/* 1631 */     while ((!isOuterGState()) && ((!getGState().canSetClip(this.mLastClip)) || (!getGState().mTransform.equals(this.mLastTransform))))
/*      */     {
/* 1635 */       grestore();
/*      */     }
/*      */ 
/* 1641 */     getGState().emitPSColor(this.mLastColor);
/*      */ 
/* 1647 */     if (isOuterGState()) {
/* 1648 */       gsave();
/* 1649 */       getGState().emitTransform(this.mLastTransform);
/* 1650 */       getGState().emitPSClip(this.mLastClip);
/*      */     }
/*      */   }
/*      */ 
/*      */   private GState getGState()
/*      */   {
/* 1672 */     int i = this.mGStateStack.size();
/* 1673 */     return (GState)this.mGStateStack.get(i - 1);
/*      */   }
/*      */ 
/*      */   private void gsave()
/*      */   {
/* 1682 */     GState localGState = getGState();
/* 1683 */     this.mGStateStack.add(new GState(localGState));
/* 1684 */     this.mPSStream.println("G");
/*      */   }
/*      */ 
/*      */   private void grestore()
/*      */   {
/* 1693 */     int i = this.mGStateStack.size();
/* 1694 */     this.mGStateStack.remove(i - 1);
/* 1695 */     this.mPSStream.println("R");
/*      */   }
/*      */ 
/*      */   private boolean isOuterGState()
/*      */   {
/* 1704 */     return this.mGStateStack.size() == 1;
/*      */   }
/*      */ 
/*      */   void convertToPSPath(PathIterator paramPathIterator)
/*      */   {
/* 1805 */     float[] arrayOfFloat = new float[6];
/*      */     int j;
/* 1812 */     if (paramPathIterator.getWindingRule() == 0)
/* 1813 */       j = 1;
/*      */     else {
/* 1815 */       j = 2;
/*      */     }
/*      */ 
/* 1818 */     beginPath();
/*      */ 
/* 1820 */     setFillMode(j);
/*      */ 
/* 1822 */     while (!paramPathIterator.isDone()) {
/* 1823 */       int i = paramPathIterator.currentSegment(arrayOfFloat);
/*      */ 
/* 1825 */       switch (i) {
/*      */       case 0:
/* 1827 */         moveTo(arrayOfFloat[0], arrayOfFloat[1]);
/* 1828 */         break;
/*      */       case 1:
/* 1831 */         lineTo(arrayOfFloat[0], arrayOfFloat[1]);
/* 1832 */         break;
/*      */       case 2:
/* 1837 */         float f1 = getPenX();
/* 1838 */         float f2 = getPenY();
/* 1839 */         float f3 = f1 + (arrayOfFloat[0] - f1) * 2.0F / 3.0F;
/* 1840 */         float f4 = f2 + (arrayOfFloat[1] - f2) * 2.0F / 3.0F;
/* 1841 */         float f5 = arrayOfFloat[2] - (arrayOfFloat[2] - arrayOfFloat[0]) * 2.0F / 3.0F;
/* 1842 */         float f6 = arrayOfFloat[3] - (arrayOfFloat[3] - arrayOfFloat[1]) * 2.0F / 3.0F;
/* 1843 */         bezierTo(f3, f4, f5, f6, arrayOfFloat[2], arrayOfFloat[3]);
/*      */ 
/* 1846 */         break;
/*      */       case 3:
/* 1849 */         bezierTo(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2], arrayOfFloat[3], arrayOfFloat[4], arrayOfFloat[5]);
/*      */ 
/* 1852 */         break;
/*      */       case 4:
/* 1855 */         closeSubpath();
/*      */       }
/*      */ 
/* 1860 */       paramPathIterator.next();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void deviceFill(PathIterator paramPathIterator, Color paramColor, AffineTransform paramAffineTransform, Shape paramShape)
/*      */   {
/* 1872 */     setTransform(paramAffineTransform);
/* 1873 */     setClip(paramShape);
/* 1874 */     setColor(paramColor);
/* 1875 */     convertToPSPath(paramPathIterator);
/*      */ 
/* 1880 */     this.mPSStream.println("G");
/* 1881 */     selectClipPath();
/* 1882 */     fillPath();
/* 1883 */     this.mPSStream.println("R N");
/*      */   }
/*      */ 
/*      */   private byte[] rlEncode(byte[] paramArrayOfByte)
/*      */   {
/* 1905 */     int i = 0;
/* 1906 */     int j = 0;
/* 1907 */     int k = 0;
/* 1908 */     int m = 0;
/* 1909 */     byte[] arrayOfByte1 = new byte[paramArrayOfByte.length * 2 + 2];
/* 1910 */     while (i < paramArrayOfByte.length) {
/* 1911 */       if (m == 0) {
/* 1912 */         k = i++;
/* 1913 */         m = 1;
/*      */       }
/*      */ 
/* 1916 */       while ((m < 128) && (i < paramArrayOfByte.length) && (paramArrayOfByte[i] == paramArrayOfByte[k]))
/*      */       {
/* 1918 */         m++;
/* 1919 */         i++;
/*      */       }
/*      */ 
/* 1922 */       if (m > 1) {
/* 1923 */         arrayOfByte1[(j++)] = ((byte)(257 - m));
/* 1924 */         arrayOfByte1[(j++)] = paramArrayOfByte[k];
/* 1925 */         m = 0;
/*      */       }
/*      */       else
/*      */       {
/* 1930 */         while ((m < 128) && (i < paramArrayOfByte.length) && (paramArrayOfByte[i] != paramArrayOfByte[(i - 1)]))
/*      */         {
/* 1932 */           m++;
/* 1933 */           i++;
/*      */         }
/* 1935 */         arrayOfByte1[(j++)] = ((byte)(m - 1));
/* 1936 */         for (int n = k; n < k + m; n++) {
/* 1937 */           arrayOfByte1[(j++)] = paramArrayOfByte[n];
/*      */         }
/* 1939 */         m = 0;
/*      */       }
/*      */     }
/* 1941 */     arrayOfByte1[(j++)] = -128;
/* 1942 */     byte[] arrayOfByte2 = new byte[j];
/* 1943 */     System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, j);
/*      */ 
/* 1945 */     return arrayOfByte2;
/*      */   }
/*      */ 
/*      */   private byte[] ascii85Encode(byte[] paramArrayOfByte)
/*      */   {
/* 1952 */     byte[] arrayOfByte1 = new byte[(paramArrayOfByte.length + 4) * 5 / 4 + 2];
/* 1953 */     long l1 = 85L;
/* 1954 */     long l2 = l1 * l1;
/* 1955 */     long l3 = l1 * l2;
/* 1956 */     long l4 = l1 * l3;
/* 1957 */     int i = 33;
/*      */ 
/* 1959 */     int j = 0;
/* 1960 */     int k = 0;
/*      */     long l5;
/*      */     long l6;
/* 1963 */     while (j + 3 < paramArrayOfByte.length) {
/* 1964 */       l5 = ((paramArrayOfByte[(j++)] & 0xFF) << 24) + ((paramArrayOfByte[(j++)] & 0xFF) << 16) + ((paramArrayOfByte[(j++)] & 0xFF) << 8) + (paramArrayOfByte[(j++)] & 0xFF);
/*      */ 
/* 1968 */       if (l5 == 0L) {
/* 1969 */         arrayOfByte1[(k++)] = 122;
/*      */       } else {
/* 1971 */         l6 = l5;
/* 1972 */         arrayOfByte1[(k++)] = ((byte)(int)(l6 / l4 + i)); l6 %= l4;
/* 1973 */         arrayOfByte1[(k++)] = ((byte)(int)(l6 / l3 + i)); l6 %= l3;
/* 1974 */         arrayOfByte1[(k++)] = ((byte)(int)(l6 / l2 + i)); l6 %= l2;
/* 1975 */         arrayOfByte1[(k++)] = ((byte)(int)(l6 / l1 + i)); l6 %= l1;
/* 1976 */         arrayOfByte1[(k++)] = ((byte)(int)(l6 + i));
/*      */       }
/*      */     }
/*      */ 
/* 1980 */     if (j < paramArrayOfByte.length) {
/* 1981 */       int m = paramArrayOfByte.length - j;
/*      */ 
/* 1983 */       l5 = 0L;
/* 1984 */       while (j < paramArrayOfByte.length) {
/* 1985 */         l5 = (l5 << 8) + (paramArrayOfByte[(j++)] & 0xFF);
/*      */       }
/*      */ 
/* 1988 */       int n = 4 - m;
/* 1989 */       while (n-- > 0) {
/* 1990 */         l5 <<= 8;
/*      */       }
/* 1992 */       byte[] arrayOfByte3 = new byte[5];
/* 1993 */       l6 = l5;
/* 1994 */       arrayOfByte3[0] = ((byte)(int)(l6 / l4 + i)); l6 %= l4;
/* 1995 */       arrayOfByte3[1] = ((byte)(int)(l6 / l3 + i)); l6 %= l3;
/* 1996 */       arrayOfByte3[2] = ((byte)(int)(l6 / l2 + i)); l6 %= l2;
/* 1997 */       arrayOfByte3[3] = ((byte)(int)(l6 / l1 + i)); l6 %= l1;
/* 1998 */       arrayOfByte3[4] = ((byte)(int)(l6 + i));
/*      */ 
/* 2000 */       for (int i1 = 0; i1 < m + 1; i1++) {
/* 2001 */         arrayOfByte1[(k++)] = arrayOfByte3[i1];
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2006 */     arrayOfByte1[(k++)] = 126; arrayOfByte1[(k++)] = 62;
/*      */ 
/* 2018 */     byte[] arrayOfByte2 = new byte[k];
/* 2019 */     System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, k);
/* 2020 */     return arrayOfByte2;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  340 */     AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public Object run() {
/*  343 */         PSPrinterJob.access$002(PSPrinterJob.access$100());
/*  344 */         return null;
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public static class EPSPrinter
/*      */     implements Pageable
/*      */   {
/*      */     private PageFormat pf;
/*      */     private PSPrinterJob job;
/*      */     private int llx;
/*      */     private int lly;
/*      */     private int urx;
/*      */     private int ury;
/*      */     private Printable printable;
/*      */     private PrintStream stream;
/*      */     private String epsTitle;
/*      */ 
/*      */     public EPSPrinter(Printable paramPrintable, String paramString, PrintStream paramPrintStream, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */     {
/* 2145 */       this.printable = paramPrintable;
/* 2146 */       this.epsTitle = paramString;
/* 2147 */       this.stream = paramPrintStream;
/* 2148 */       this.llx = paramInt1;
/* 2149 */       this.lly = paramInt2;
/* 2150 */       this.urx = (this.llx + paramInt3);
/* 2151 */       this.ury = (this.lly + paramInt4);
/*      */ 
/* 2155 */       Paper localPaper = new Paper();
/* 2156 */       localPaper.setSize(paramInt3, paramInt4);
/* 2157 */       localPaper.setImageableArea(0.0D, 0.0D, paramInt3, paramInt4);
/* 2158 */       this.pf = new PageFormat();
/* 2159 */       this.pf.setPaper(localPaper);
/*      */     }
/*      */ 
/*      */     public void print() throws PrinterException {
/* 2163 */       this.stream.println("%!PS-Adobe-3.0 EPSF-3.0");
/* 2164 */       this.stream.println("%%BoundingBox: " + this.llx + " " + this.lly + " " + this.urx + " " + this.ury);
/*      */ 
/* 2166 */       this.stream.println("%%Title: " + this.epsTitle);
/* 2167 */       this.stream.println("%%Creator: Java Printing");
/* 2168 */       this.stream.println("%%CreationDate: " + new Date());
/* 2169 */       this.stream.println("%%EndComments");
/* 2170 */       this.stream.println("/pluginSave save def");
/* 2171 */       this.stream.println("mark");
/*      */ 
/* 2173 */       this.job = new PSPrinterJob();
/* 2174 */       this.job.epsPrinter = this;
/* 2175 */       this.job.mPSStream = this.stream;
/* 2176 */       this.job.mDestType = 2;
/*      */ 
/* 2178 */       this.job.startDoc();
/*      */       try {
/* 2180 */         this.job.printPage(this, 0);
/*      */       } catch (Throwable localThrowable) {
/* 2182 */         if ((localThrowable instanceof PrinterException)) {
/* 2183 */           throw ((PrinterException)localThrowable);
/*      */         }
/* 2185 */         throw new PrinterException(localThrowable.toString());
/*      */       }
/*      */       finally {
/* 2188 */         this.stream.println("cleartomark");
/* 2189 */         this.stream.println("pluginSave restore");
/* 2190 */         this.job.endDoc();
/*      */       }
/* 2192 */       this.stream.flush();
/*      */     }
/*      */ 
/*      */     public int getNumberOfPages() {
/* 2196 */       return 1;
/*      */     }
/*      */ 
/*      */     public PageFormat getPageFormat(int paramInt) {
/* 2200 */       if (paramInt > 0) {
/* 2201 */         throw new IndexOutOfBoundsException("pgIndex");
/*      */       }
/* 2203 */       return this.pf;
/*      */     }
/*      */ 
/*      */     public Printable getPrintable(int paramInt)
/*      */     {
/* 2208 */       if (paramInt > 0) {
/* 2209 */         throw new IndexOutOfBoundsException("pgIndex");
/*      */       }
/* 2211 */       return this.printable;
/*      */     }
/*      */   }
/*      */ 
/*      */   private class GState
/*      */   {
/*      */     Color mColor;
/*      */     Shape mClip;
/*      */     Font mFont;
/*      */     AffineTransform mTransform;
/*      */ 
/*      */     GState()
/*      */     {
/* 1719 */       this.mColor = Color.black;
/* 1720 */       this.mClip = null;
/* 1721 */       this.mFont = null;
/* 1722 */       this.mTransform = new AffineTransform();
/*      */     }
/*      */ 
/*      */     GState(GState arg2)
/*      */     {
/*      */       Object localObject;
/* 1726 */       this.mColor = localObject.mColor;
/* 1727 */       this.mClip = localObject.mClip;
/* 1728 */       this.mFont = localObject.mFont;
/* 1729 */       this.mTransform = localObject.mTransform;
/*      */     }
/*      */ 
/*      */     boolean canSetClip(Shape paramShape)
/*      */     {
/* 1734 */       return (this.mClip == null) || (this.mClip.equals(paramShape));
/*      */     }
/*      */ 
/*      */     void emitPSClip(Shape paramShape)
/*      */     {
/* 1739 */       if ((paramShape != null) && ((this.mClip == null) || (!this.mClip.equals(paramShape))))
/*      */       {
/* 1741 */         String str1 = PSPrinterJob.this.mFillOpStr;
/* 1742 */         String str2 = PSPrinterJob.this.mClipOpStr;
/* 1743 */         PSPrinterJob.this.convertToPSPath(paramShape.getPathIterator(new AffineTransform()));
/* 1744 */         PSPrinterJob.this.selectClipPath();
/* 1745 */         this.mClip = paramShape;
/*      */ 
/* 1747 */         PSPrinterJob.this.mClipOpStr = str1;
/* 1748 */         PSPrinterJob.this.mFillOpStr = str1;
/*      */       }
/*      */     }
/*      */ 
/*      */     void emitTransform(AffineTransform paramAffineTransform)
/*      */     {
/* 1754 */       if ((paramAffineTransform != null) && (!paramAffineTransform.equals(this.mTransform))) {
/* 1755 */         double[] arrayOfDouble = new double[6];
/* 1756 */         paramAffineTransform.getMatrix(arrayOfDouble);
/* 1757 */         PSPrinterJob.this.mPSStream.println("[" + (float)arrayOfDouble[0] + " " + (float)arrayOfDouble[1] + " " + (float)arrayOfDouble[2] + " " + (float)arrayOfDouble[3] + " " + (float)arrayOfDouble[4] + " " + (float)arrayOfDouble[5] + "] concat");
/*      */ 
/* 1765 */         this.mTransform = paramAffineTransform;
/*      */       }
/*      */     }
/*      */ 
/*      */     void emitPSColor(Color paramColor) {
/* 1770 */       if ((paramColor != null) && (!paramColor.equals(this.mColor))) {
/* 1771 */         float[] arrayOfFloat = paramColor.getRGBColorComponents(null);
/*      */ 
/* 1776 */         if ((arrayOfFloat[0] == arrayOfFloat[1]) && (arrayOfFloat[1] == arrayOfFloat[2])) {
/* 1777 */           PSPrinterJob.this.mPSStream.println(arrayOfFloat[0] + " SG");
/*      */         }
/*      */         else
/*      */         {
/* 1782 */           PSPrinterJob.this.mPSStream.println(arrayOfFloat[0] + " " + arrayOfFloat[1] + " " + arrayOfFloat[2] + " " + " SC");
/*      */         }
/*      */ 
/* 1788 */         this.mColor = paramColor;
/*      */       }
/*      */     }
/*      */ 
/*      */     void emitPSFont(int paramInt, float paramFloat)
/*      */     {
/* 1794 */       PSPrinterJob.this.mPSStream.println(paramFloat + " " + paramInt + " " + "F");
/*      */     }
/*      */   }
/*      */ 
/*      */   public static class PluginPrinter
/*      */     implements Printable
/*      */   {
/*      */     private PSPrinterJob.EPSPrinter epsPrinter;
/*      */     private Component applet;
/*      */     private PrintStream stream;
/*      */     private String epsTitle;
/*      */     private int bx;
/*      */     private int by;
/*      */     private int bw;
/*      */     private int bh;
/*      */     private int width;
/*      */     private int height;
/*      */ 
/*      */     public PluginPrinter(Component paramComponent, PrintStream paramPrintStream, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */     {
/* 2069 */       this.applet = paramComponent;
/* 2070 */       this.epsTitle = "Java Plugin Applet";
/* 2071 */       this.stream = paramPrintStream;
/* 2072 */       this.bx = paramInt1;
/* 2073 */       this.by = paramInt2;
/* 2074 */       this.bw = paramInt3;
/* 2075 */       this.bh = paramInt4;
/* 2076 */       this.width = paramComponent.size().width;
/* 2077 */       this.height = paramComponent.size().height;
/* 2078 */       this.epsPrinter = new PSPrinterJob.EPSPrinter(this, this.epsTitle, paramPrintStream, 0, 0, this.width, this.height);
/*      */     }
/*      */ 
/*      */     public void printPluginPSHeader()
/*      */     {
/* 2083 */       this.stream.println("%%BeginDocument: JavaPluginApplet");
/*      */     }
/*      */ 
/*      */     public void printPluginApplet() {
/*      */       try {
/* 2088 */         this.epsPrinter.print();
/*      */       } catch (PrinterException localPrinterException) {
/*      */       }
/*      */     }
/*      */ 
/*      */     public void printPluginPSTrailer() {
/* 2094 */       this.stream.println("%%EndDocument: JavaPluginApplet");
/* 2095 */       this.stream.flush();
/*      */     }
/*      */ 
/*      */     public void printAll() {
/* 2099 */       printPluginPSHeader();
/* 2100 */       printPluginApplet();
/* 2101 */       printPluginPSTrailer();
/*      */     }
/*      */ 
/*      */     public int print(Graphics paramGraphics, PageFormat paramPageFormat, int paramInt) {
/* 2105 */       if (paramInt > 0) {
/* 2106 */         return 1;
/*      */       }
/*      */ 
/* 2112 */       this.applet.printAll(paramGraphics);
/* 2113 */       return 0;
/*      */     }
/*      */   }
/*      */ 
/*      */   private class PrinterOpener
/*      */     implements PrivilegedAction
/*      */   {
/*      */     PrinterException pex;
/*      */     OutputStream result;
/*      */ 
/*      */     private PrinterOpener()
/*      */     {
/*      */     }
/*      */ 
/*      */     public Object run()
/*      */     {
/*      */       try
/*      */       {
/*  659 */         PSPrinterJob.this.spoolFile = Files.createTempFile("javaprint", ".ps", new FileAttribute[0]).toFile();
/*  660 */         PSPrinterJob.this.spoolFile.deleteOnExit();
/*      */ 
/*  662 */         this.result = new FileOutputStream(PSPrinterJob.this.spoolFile);
/*  663 */         return this.result;
/*      */       }
/*      */       catch (IOException localIOException) {
/*  666 */         this.pex = new PrinterIOException(localIOException);
/*      */       }
/*  668 */       return null;
/*      */     }
/*      */   }
/*      */ 
/*      */   private class PrinterSpooler implements PrivilegedAction
/*      */   {
/*      */     PrinterException pex;
/*      */ 
/*      */     private PrinterSpooler()
/*      */     {
/*      */     }
/*      */ 
/*      */     public Object run() {
/*      */       try {
/*  682 */         if ((PSPrinterJob.this.spoolFile == null) || (!PSPrinterJob.this.spoolFile.exists())) {
/*  683 */           this.pex = new PrinterException("No spool file");
/*  684 */           return null;
/*      */         }
/*  686 */         String str = PSPrinterJob.this.spoolFile.getAbsolutePath();
/*  687 */         String[] arrayOfString = PSPrinterJob.this.printExecCmd(PSPrinterJob.this.mDestination, PSPrinterJob.this.mOptions, PSPrinterJob.this.mNoJobSheet, PSPrinterJob.this.getJobNameInt(), 1, str);
/*      */ 
/*  691 */         Process localProcess = Runtime.getRuntime().exec(arrayOfString);
/*  692 */         localProcess.waitFor();
/*  693 */         PSPrinterJob.this.spoolFile.delete();
/*      */       }
/*      */       catch (IOException localIOException) {
/*  696 */         this.pex = new PrinterIOException(localIOException);
/*      */       } catch (InterruptedException localInterruptedException) {
/*  698 */         this.pex = new PrinterException(localInterruptedException.toString());
/*      */       }
/*  700 */       return null;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.print.PSPrinterJob
 * JD-Core Version:    0.6.2
 */