/*     */ package sun.print;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.URL;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.attribute.FileAttribute;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Vector;
/*     */ import javax.print.DocFlavor;
/*     */ import javax.print.MultiDocPrintService;
/*     */ import javax.print.PrintService;
/*     */ import javax.print.PrintServiceLookup;
/*     */ import javax.print.attribute.Attribute;
/*     */ import javax.print.attribute.AttributeSet;
/*     */ import javax.print.attribute.HashPrintRequestAttributeSet;
/*     */ import javax.print.attribute.HashPrintServiceAttributeSet;
/*     */ import javax.print.attribute.PrintRequestAttribute;
/*     */ import javax.print.attribute.PrintRequestAttributeSet;
/*     */ import javax.print.attribute.PrintServiceAttribute;
/*     */ import javax.print.attribute.PrintServiceAttributeSet;
/*     */ import javax.print.attribute.standard.PrinterName;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ 
/*     */ public class UnixPrintServiceLookup extends PrintServiceLookup
/*     */   implements BackgroundServiceLookup, Runnable
/*     */ {
/*     */   private String defaultPrinter;
/*     */   private PrintService defaultPrintService;
/*     */   private PrintService[] printServices;
/*  71 */   private Vector lookupListeners = null;
/*  72 */   private static String debugPrefix = "UnixPrintServiceLookup>> ";
/*  73 */   private static boolean pollServices = true;
/*     */   private static final int DEFAULT_MINREFRESH = 120;
/*  75 */   private static int minRefreshTime = 120;
/*     */ 
/* 114 */   static String osname = (String)AccessController.doPrivileged(new GetPropertyAction("os.name"));
/*     */   static final int UNINITIALIZED = -1;
/*     */   static final int BSD_LPD = 0;
/*     */   static final int BSD_LPD_NG = 1;
/* 135 */   static int cmdIndex = -1;
/*     */ 
/* 137 */   String[] lpcFirstCom = { "/usr/sbin/lpc status | grep : | sed -ne '1,1 s/://p'", "/usr/sbin/lpc status | grep -E '^[ 0-9a-zA-Z_-]*@' | awk -F'@' '{print $1}'" };
/*     */ 
/* 142 */   String[] lpcAllCom = { "/usr/sbin/lpc status all | grep : | sed -e 's/://'", "/usr/sbin/lpc status all | grep -E '^[ 0-9a-zA-Z_-]*@' | awk -F'@' '{print $1}' | sort" };
/*     */ 
/* 147 */   String[] lpcNameCom = { "| grep : | sed -ne 's/://p'", "| grep -E '^[ 0-9a-zA-Z_-]*@' | awk -F'@' '{print $1}'" };
/*     */ 
/*     */   static boolean isMac()
/*     */   {
/* 119 */     return osname.contains("OS X");
/*     */   }
/*     */ 
/*     */   static boolean isSysV() {
/* 123 */     return osname.equals("SunOS");
/*     */   }
/*     */ 
/*     */   static boolean isBSD() {
/* 127 */     return (osname.equals("Linux")) || (osname.contains("OS X"));
/*     */   }
/*     */ 
/*     */   static int getBSDCommandIndex()
/*     */   {
/* 154 */     String str = "/usr/sbin/lpc status all";
/* 155 */     String[] arrayOfString = execCmd(str);
/*     */ 
/* 157 */     if ((arrayOfString == null) || (arrayOfString.length == 0)) {
/* 158 */       return 1;
/*     */     }
/*     */ 
/* 161 */     for (int i = 0; i < arrayOfString.length; i++) {
/* 162 */       if (arrayOfString[i].indexOf('@') != -1) {
/* 163 */         return 1;
/*     */       }
/*     */     }
/*     */ 
/* 167 */     return 0;
/*     */   }
/*     */ 
/*     */   public UnixPrintServiceLookup()
/*     */   {
/* 173 */     if (pollServices) {
/* 174 */       PrinterChangeListener localPrinterChangeListener = new PrinterChangeListener(null);
/* 175 */       localPrinterChangeListener.setDaemon(true);
/* 176 */       localPrinterChangeListener.start();
/* 177 */       IPPPrintService.debug_println(debugPrefix + "polling turned on");
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized PrintService[] getPrintServices()
/*     */   {
/* 187 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 188 */     if (localSecurityManager != null) {
/* 189 */       localSecurityManager.checkPrintJobAccess();
/*     */     }
/*     */ 
/* 192 */     if ((this.printServices == null) || (!pollServices)) {
/* 193 */       refreshServices();
/*     */     }
/* 195 */     if (this.printServices == null) {
/* 196 */       return new PrintService[0];
/*     */     }
/* 198 */     return this.printServices;
/*     */   }
/*     */ 
/*     */   public synchronized void refreshServices()
/*     */   {
/* 206 */     String[] arrayOfString1 = null;
/* 207 */     String[] arrayOfString2 = null;
/*     */ 
/* 209 */     getDefaultPrintService();
/* 210 */     if (CUPSPrinter.isCupsRunning()) {
/* 211 */       arrayOfString2 = CUPSPrinter.getAllPrinters();
/* 212 */       if ((arrayOfString2 != null) && (arrayOfString2.length > 0)) {
/* 213 */         arrayOfString1 = new String[arrayOfString2.length];
/* 214 */         for (int i = 0; i < arrayOfString2.length; i++) {
/* 215 */           j = arrayOfString2[i].lastIndexOf("/");
/* 216 */           arrayOfString1[i] = arrayOfString2[i].substring(j + 1);
/*     */         }
/*     */       }
/*     */     }
/* 220 */     else if ((isMac()) || (isSysV())) {
/* 221 */       arrayOfString1 = getAllPrinterNamesSysV();
/*     */     } else {
/* 223 */       arrayOfString1 = getAllPrinterNamesBSD();
/*     */     }
/*     */ 
/* 227 */     if (arrayOfString1 == null) {
/* 228 */       if (this.defaultPrintService != null) {
/* 229 */         this.printServices = new PrintService[1];
/* 230 */         this.printServices[0] = this.defaultPrintService;
/*     */       } else {
/* 232 */         this.printServices = null;
/*     */       }
/* 234 */       return;
/*     */     }
/*     */ 
/* 237 */     ArrayList localArrayList = new ArrayList();
/* 238 */     int j = -1;
/* 239 */     for (int k = 0; k < arrayOfString1.length; k++) {
/* 240 */       if (arrayOfString1[k] != null)
/*     */       {
/* 243 */         if ((this.defaultPrintService != null) && (arrayOfString1[k].equals(this.defaultPrintService.getName())))
/*     */         {
/* 245 */           localArrayList.add(this.defaultPrintService);
/* 246 */           j = localArrayList.size() - 1;
/*     */         }
/* 248 */         else if (this.printServices == null) {
/* 249 */           IPPPrintService.debug_println(debugPrefix + "total# of printers = " + arrayOfString1.length);
/*     */ 
/* 252 */           if (CUPSPrinter.isCupsRunning()) {
/*     */             try {
/* 254 */               localArrayList.add(new IPPPrintService(arrayOfString1[k], arrayOfString2[k], true));
/*     */             }
/*     */             catch (Exception localException1)
/*     */             {
/* 258 */               IPPPrintService.debug_println(debugPrefix + " getAllPrinters Exception " + localException1);
/*     */             }
/*     */ 
/*     */           }
/*     */           else
/*     */           {
/* 264 */             localArrayList.add(new UnixPrintService(arrayOfString1[k]));
/*     */           }
/*     */         }
/*     */         else {
/* 268 */           for (int m = 0; m < this.printServices.length; m++) {
/* 269 */             if ((this.printServices[m] != null) && (arrayOfString1[k].equals(this.printServices[m].getName())))
/*     */             {
/* 271 */               localArrayList.add(this.printServices[m]);
/* 272 */               this.printServices[m] = null;
/* 273 */               break;
/*     */             }
/*     */           }
/*     */ 
/* 277 */           if (m == this.printServices.length) {
/* 278 */             if (CUPSPrinter.isCupsRunning()) {
/*     */               try {
/* 280 */                 localArrayList.add(new IPPPrintService(arrayOfString1[k], arrayOfString2[k], true));
/*     */               }
/*     */               catch (Exception localException2)
/*     */               {
/* 285 */                 IPPPrintService.debug_println(debugPrefix + " getAllPrinters Exception " + localException2);
/*     */               }
/*     */ 
/*     */             }
/*     */             else
/*     */             {
/* 291 */               localArrayList.add(new UnixPrintService(arrayOfString1[k]));
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 299 */     if (this.printServices != null) {
/* 300 */       for (k = 0; k < this.printServices.length; k++) {
/* 301 */         if (((this.printServices[k] instanceof UnixPrintService)) && (!this.printServices[k].equals(this.defaultPrintService)))
/*     */         {
/* 303 */           ((UnixPrintService)this.printServices[k]).invalidateService();
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 309 */     if ((j == -1) && (this.defaultPrintService != null))
/*     */     {
/* 311 */       localArrayList.add(this.defaultPrintService);
/* 312 */       j = localArrayList.size() - 1;
/*     */     }
/*     */ 
/* 315 */     this.printServices = ((PrintService[])localArrayList.toArray(new PrintService[0]));
/*     */ 
/* 319 */     if (j > 0) {
/* 320 */       PrintService localPrintService = this.printServices[0];
/* 321 */       this.printServices[0] = this.printServices[j];
/* 322 */       this.printServices[j] = localPrintService;
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean matchesAttributes(PrintService paramPrintService, PrintServiceAttributeSet paramPrintServiceAttributeSet)
/*     */   {
/* 329 */     Attribute[] arrayOfAttribute = paramPrintServiceAttributeSet.toArray();
/*     */ 
/* 331 */     for (int i = 0; i < arrayOfAttribute.length; i++) {
/* 332 */       PrintServiceAttribute localPrintServiceAttribute = paramPrintService.getAttribute(arrayOfAttribute[i].getCategory());
/*     */ 
/* 334 */       if ((localPrintServiceAttribute == null) || (!localPrintServiceAttribute.equals(arrayOfAttribute[i]))) {
/* 335 */         return false;
/*     */       }
/*     */     }
/* 338 */     return true;
/*     */   }
/*     */ 
/*     */   private boolean checkPrinterName(String paramString)
/*     */   {
/* 347 */     for (int i = 0; i < paramString.length(); i++) {
/* 348 */       char c = paramString.charAt(i);
/* 349 */       if ((!Character.isLetterOrDigit(c)) && (c != '-') && (c != '_') && (c != '.') && (c != '/'))
/*     */       {
/* 353 */         return false;
/*     */       }
/*     */     }
/* 356 */     return true;
/*     */   }
/*     */ 
/*     */   private PrintService getServiceByName(PrinterName paramPrinterName)
/*     */   {
/* 364 */     String str = paramPrinterName.getValue();
/* 365 */     PrintService localPrintService = null;
/* 366 */     if ((str == null) || (str.equals("")) || (!checkPrinterName(str))) {
/* 367 */       return null;
/*     */     }
/* 369 */     if ((isMac()) || (isSysV()))
/* 370 */       localPrintService = getNamedPrinterNameSysV(str);
/*     */     else {
/* 372 */       localPrintService = getNamedPrinterNameBSD(str);
/*     */     }
/* 374 */     return localPrintService;
/*     */   }
/*     */ 
/*     */   private PrintService[] getPrintServices(PrintServiceAttributeSet paramPrintServiceAttributeSet)
/*     */   {
/* 380 */     if ((paramPrintServiceAttributeSet == null) || (paramPrintServiceAttributeSet.isEmpty())) {
/* 381 */       return getPrintServices();
/*     */     }
/*     */ 
/* 392 */     PrinterName localPrinterName = (PrinterName)paramPrintServiceAttributeSet.get(PrinterName.class);
/*     */     PrintService localPrintService1;
/* 394 */     if ((localPrinterName != null) && ((localPrintService1 = getDefaultPrintService()) != null))
/*     */     {
/* 400 */       localObject = (PrinterName)localPrintService1.getAttribute(PrinterName.class);
/*     */ 
/* 403 */       if ((localObject != null) && (localPrinterName.equals(localObject))) {
/* 404 */         if (matchesAttributes(localPrintService1, paramPrintServiceAttributeSet)) {
/* 405 */           arrayOfPrintService = new PrintService[1];
/* 406 */           arrayOfPrintService[0] = localPrintService1;
/* 407 */           return arrayOfPrintService;
/*     */         }
/* 409 */         return new PrintService[0];
/*     */       }
/*     */ 
/* 413 */       PrintService localPrintService2 = getServiceByName(localPrinterName);
/* 414 */       if ((localPrintService2 != null) && (matchesAttributes(localPrintService2, paramPrintServiceAttributeSet)))
/*     */       {
/* 416 */         arrayOfPrintService = new PrintService[1];
/* 417 */         arrayOfPrintService[0] = localPrintService2;
/* 418 */         return arrayOfPrintService;
/*     */       }
/* 420 */       return new PrintService[0];
/*     */     }
/*     */ 
/* 425 */     Object localObject = new Vector();
/* 426 */     PrintService[] arrayOfPrintService = getPrintServices();
/* 427 */     for (int i = 0; i < arrayOfPrintService.length; i++) {
/* 428 */       if (matchesAttributes(arrayOfPrintService[i], paramPrintServiceAttributeSet)) {
/* 429 */         ((Vector)localObject).add(arrayOfPrintService[i]);
/*     */       }
/*     */     }
/* 432 */     arrayOfPrintService = new PrintService[((Vector)localObject).size()];
/* 433 */     for (i = 0; i < arrayOfPrintService.length; i++) {
/* 434 */       arrayOfPrintService[i] = ((PrintService)((Vector)localObject).elementAt(i));
/*     */     }
/* 436 */     return arrayOfPrintService;
/*     */   }
/*     */ 
/*     */   public PrintService[] getPrintServices(DocFlavor paramDocFlavor, AttributeSet paramAttributeSet)
/*     */   {
/* 446 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 447 */     if (localSecurityManager != null) {
/* 448 */       localSecurityManager.checkPrintJobAccess();
/*     */     }
/* 450 */     HashPrintRequestAttributeSet localHashPrintRequestAttributeSet = null;
/* 451 */     HashPrintServiceAttributeSet localHashPrintServiceAttributeSet = null;
/*     */ 
/* 453 */     if ((paramAttributeSet != null) && (!paramAttributeSet.isEmpty()))
/*     */     {
/* 455 */       localHashPrintRequestAttributeSet = new HashPrintRequestAttributeSet();
/* 456 */       localHashPrintServiceAttributeSet = new HashPrintServiceAttributeSet();
/*     */ 
/* 458 */       localObject = paramAttributeSet.toArray();
/* 459 */       for (int i = 0; i < localObject.length; i++) {
/* 460 */         if ((localObject[i] instanceof PrintRequestAttribute))
/* 461 */           localHashPrintRequestAttributeSet.add(localObject[i]);
/* 462 */         else if ((localObject[i] instanceof PrintServiceAttribute)) {
/* 463 */           localHashPrintServiceAttributeSet.add(localObject[i]);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 468 */     Object localObject = getPrintServices(localHashPrintServiceAttributeSet);
/* 469 */     if (localObject.length == 0) {
/* 470 */       return localObject;
/*     */     }
/*     */ 
/* 473 */     if (CUPSPrinter.isCupsRunning()) {
/* 474 */       localArrayList = new ArrayList();
/* 475 */       for (int j = 0; j < localObject.length; j++)
/*     */         try {
/* 477 */           if (localObject[j].getUnsupportedAttributes(paramDocFlavor, localHashPrintRequestAttributeSet) == null)
/*     */           {
/* 479 */             localArrayList.add(localObject[j]);
/*     */           }
/*     */         }
/*     */         catch (IllegalArgumentException localIllegalArgumentException) {
/*     */         }
/* 484 */       localObject = new PrintService[localArrayList.size()];
/* 485 */       return (PrintService[])localArrayList.toArray((Object[])localObject);
/*     */     }
/*     */ 
/* 491 */     ArrayList localArrayList = localObject[0];
/* 492 */     if (((paramDocFlavor == null) || (localArrayList.isDocFlavorSupported(paramDocFlavor))) && (localArrayList.getUnsupportedAttributes(paramDocFlavor, localHashPrintRequestAttributeSet) == null))
/*     */     {
/* 496 */       return localObject;
/*     */     }
/* 498 */     return new PrintService[0];
/*     */   }
/*     */ 
/*     */   public MultiDocPrintService[] getMultiDocPrintServices(DocFlavor[] paramArrayOfDocFlavor, AttributeSet paramAttributeSet)
/*     */   {
/* 509 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 510 */     if (localSecurityManager != null) {
/* 511 */       localSecurityManager.checkPrintJobAccess();
/*     */     }
/* 513 */     return new MultiDocPrintService[0];
/*     */   }
/*     */ 
/*     */   public synchronized PrintService getDefaultPrintService()
/*     */   {
/* 518 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 519 */     if (localSecurityManager != null) {
/* 520 */       localSecurityManager.checkPrintJobAccess();
/*     */     }
/*     */ 
/* 524 */     this.defaultPrintService = null;
/*     */ 
/* 526 */     IPPPrintService.debug_println("isRunning ? " + CUPSPrinter.isCupsRunning());
/*     */ 
/* 528 */     if (CUPSPrinter.isCupsRunning()) {
/* 529 */       this.defaultPrinter = CUPSPrinter.getDefaultPrinter();
/*     */     }
/* 531 */     else if ((isMac()) || (isSysV()))
/* 532 */       this.defaultPrinter = getDefaultPrinterNameSysV();
/*     */     else {
/* 534 */       this.defaultPrinter = getDefaultPrinterNameBSD();
/*     */     }
/*     */ 
/* 537 */     if (this.defaultPrinter == null) {
/* 538 */       return null;
/*     */     }
/* 540 */     this.defaultPrintService = null;
/* 541 */     if (this.printServices != null) {
/* 542 */       for (int i = 0; i < this.printServices.length; i++) {
/* 543 */         if (this.defaultPrinter.equals(this.printServices[i].getName())) {
/* 544 */           this.defaultPrintService = this.printServices[i];
/* 545 */           break;
/*     */         }
/*     */       }
/*     */     }
/* 549 */     if (this.defaultPrintService == null) {
/* 550 */       if (CUPSPrinter.isCupsRunning())
/*     */         try {
/* 552 */           IPPPrintService localIPPPrintService = new IPPPrintService(this.defaultPrinter, new URL("http://" + CUPSPrinter.getServer() + ":" + CUPSPrinter.getPort() + "/" + this.defaultPrinter));
/*     */ 
/* 558 */           this.defaultPrintService = localIPPPrintService;
/*     */         }
/*     */         catch (Exception localException) {
/*     */         }
/* 562 */       else this.defaultPrintService = new UnixPrintService(this.defaultPrinter);
/*     */ 
/*     */     }
/*     */ 
/* 566 */     return this.defaultPrintService;
/*     */   }
/*     */ 
/*     */   public synchronized void getServicesInbackground(BackgroundLookupListener paramBackgroundLookupListener)
/*     */   {
/* 571 */     if (this.printServices != null) {
/* 572 */       paramBackgroundLookupListener.notifyServices(this.printServices);
/*     */     }
/* 574 */     else if (this.lookupListeners == null) {
/* 575 */       this.lookupListeners = new Vector();
/* 576 */       this.lookupListeners.add(paramBackgroundLookupListener);
/* 577 */       Thread localThread = new Thread(this);
/* 578 */       localThread.start();
/*     */     } else {
/* 580 */       this.lookupListeners.add(paramBackgroundLookupListener);
/*     */     }
/*     */   }
/*     */ 
/*     */   private PrintService[] copyOf(PrintService[] paramArrayOfPrintService)
/*     */   {
/* 590 */     if ((paramArrayOfPrintService == null) || (paramArrayOfPrintService.length == 0)) {
/* 591 */       return paramArrayOfPrintService;
/*     */     }
/* 593 */     PrintService[] arrayOfPrintService = new PrintService[paramArrayOfPrintService.length];
/* 594 */     System.arraycopy(paramArrayOfPrintService, 0, arrayOfPrintService, 0, paramArrayOfPrintService.length);
/* 595 */     return arrayOfPrintService;
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/* 600 */     PrintService[] arrayOfPrintService = getPrintServices();
/* 601 */     synchronized (this)
/*     */     {
/* 603 */       for (int i = 0; i < this.lookupListeners.size(); i++) {
/* 604 */         BackgroundLookupListener localBackgroundLookupListener = (BackgroundLookupListener)this.lookupListeners.elementAt(i);
/*     */ 
/* 606 */         localBackgroundLookupListener.notifyServices(copyOf(arrayOfPrintService));
/*     */       }
/* 608 */       this.lookupListeners = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   private String getDefaultPrinterNameBSD() {
/* 613 */     if (cmdIndex == -1) {
/* 614 */       cmdIndex = getBSDCommandIndex();
/*     */     }
/* 616 */     String[] arrayOfString = execCmd(this.lpcFirstCom[cmdIndex]);
/* 617 */     if ((arrayOfString == null) || (arrayOfString.length == 0)) {
/* 618 */       return null;
/*     */     }
/*     */ 
/* 621 */     if ((cmdIndex == 1) && (arrayOfString[0].startsWith("missingprinter")))
/*     */     {
/* 623 */       return null;
/*     */     }
/* 625 */     return arrayOfString[0];
/*     */   }
/*     */ 
/*     */   private PrintService getNamedPrinterNameBSD(String paramString) {
/* 629 */     if (cmdIndex == -1) {
/* 630 */       cmdIndex = getBSDCommandIndex();
/*     */     }
/* 632 */     String str = "/usr/sbin/lpc status " + paramString + this.lpcNameCom[cmdIndex];
/* 633 */     String[] arrayOfString = execCmd(str);
/*     */ 
/* 635 */     if ((arrayOfString == null) || (!arrayOfString[0].equals(paramString))) {
/* 636 */       return null;
/*     */     }
/* 638 */     return new UnixPrintService(paramString);
/*     */   }
/*     */ 
/*     */   private String[] getAllPrinterNamesBSD() {
/* 642 */     if (cmdIndex == -1) {
/* 643 */       cmdIndex = getBSDCommandIndex();
/*     */     }
/* 645 */     String[] arrayOfString = execCmd(this.lpcAllCom[cmdIndex]);
/* 646 */     if ((arrayOfString == null) || (arrayOfString.length == 0)) {
/* 647 */       return null;
/*     */     }
/* 649 */     return arrayOfString;
/*     */   }
/*     */ 
/*     */   static String getDefaultPrinterNameSysV() {
/* 653 */     String str1 = "lp";
/* 654 */     String str2 = "/usr/bin/lpstat -d";
/*     */ 
/* 656 */     String[] arrayOfString = execCmd(str2);
/* 657 */     if ((arrayOfString == null) || (arrayOfString.length == 0)) {
/* 658 */       return str1;
/*     */     }
/* 660 */     int i = arrayOfString[0].indexOf(":");
/* 661 */     if ((i == -1) || (arrayOfString[0].length() <= i + 1)) {
/* 662 */       return null;
/*     */     }
/* 664 */     String str3 = arrayOfString[0].substring(i + 1).trim();
/* 665 */     if (str3.length() == 0) {
/* 666 */       return null;
/*     */     }
/* 668 */     return str3;
/*     */   }
/*     */ 
/*     */   private PrintService getNamedPrinterNameSysV(String paramString)
/*     */   {
/* 676 */     String str = "/usr/bin/lpstat -v " + paramString;
/* 677 */     String[] arrayOfString = execCmd(str);
/*     */ 
/* 679 */     if ((arrayOfString == null) || (arrayOfString[0].indexOf("unknown printer") > 0)) {
/* 680 */       return null;
/*     */     }
/* 682 */     return new UnixPrintService(paramString);
/*     */   }
/*     */ 
/*     */   private String[] getAllPrinterNamesSysV()
/*     */   {
/* 687 */     String str1 = "lp";
/* 688 */     String str2 = "/usr/bin/lpstat -v|/usr/bin/expand|/usr/bin/cut -f3 -d' ' |/usr/bin/cut -f1 -d':' | /usr/bin/sort";
/*     */ 
/* 690 */     String[] arrayOfString = execCmd(str2);
/* 691 */     ArrayList localArrayList = new ArrayList();
/* 692 */     for (int i = 0; i < arrayOfString.length; i++) {
/* 693 */       if ((!arrayOfString[i].equals("_default")) && (!arrayOfString[i].equals(str1)) && (!arrayOfString[i].equals("")))
/*     */       {
/* 696 */         localArrayList.add(arrayOfString[i]);
/*     */       }
/*     */     }
/* 699 */     return (String[])localArrayList.toArray(new String[localArrayList.size()]);
/*     */   }
/*     */ 
/*     */   static String[] execCmd(String paramString) {
/* 703 */     ArrayList localArrayList = null;
/*     */     try {
/* 705 */       String[] arrayOfString = new String[3];
/* 706 */       if (isSysV()) {
/* 707 */         arrayOfString[0] = "/usr/bin/sh";
/* 708 */         arrayOfString[1] = "-c";
/* 709 */         arrayOfString[2] = ("env LC_ALL=C " + paramString);
/*     */       } else {
/* 711 */         arrayOfString[0] = "/bin/sh";
/* 712 */         arrayOfString[1] = "-c";
/* 713 */         arrayOfString[2] = ("LC_ALL=C " + paramString);
/*     */       }
/*     */ 
/* 716 */       localArrayList = (ArrayList)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*     */       {
/*     */         public Object run()
/*     */           throws IOException
/*     */         {
/* 721 */           BufferedReader localBufferedReader = null;
/* 722 */           File localFile = Files.createTempFile("prn", "xc", new FileAttribute[0]).toFile();
/* 723 */           this.val$cmd[2] = (this.val$cmd[2] + ">" + localFile.getAbsolutePath());
/*     */ 
/* 725 */           Process localProcess = Runtime.getRuntime().exec(this.val$cmd);
/*     */           try {
/* 727 */             int i = 0;
/* 728 */             while (i == 0)
/*     */               try {
/* 730 */                 localProcess.waitFor();
/* 731 */                 i = 1;
/*     */               }
/*     */               catch (InterruptedException localInterruptedException)
/*     */               {
/*     */               }
/* 736 */             if (localProcess.exitValue() == 0) {
/* 737 */               FileReader localFileReader = new FileReader(localFile);
/* 738 */               localBufferedReader = new BufferedReader(localFileReader);
/*     */ 
/* 740 */               ArrayList localArrayList1 = new ArrayList();
/*     */               String str;
/* 742 */               while ((str = localBufferedReader.readLine()) != null) {
/* 743 */                 localArrayList1.add(str);
/*     */               }
/* 745 */               return localArrayList1;
/*     */             }
/*     */           } finally {
/* 748 */             localFile.delete();
/*     */ 
/* 750 */             if (localBufferedReader != null) {
/* 751 */               localBufferedReader.close();
/*     */             }
/* 753 */             localProcess.getInputStream().close();
/* 754 */             localProcess.getErrorStream().close();
/* 755 */             localProcess.getOutputStream().close();
/*     */           }
/* 757 */           return null;
/*     */         } } );
/*     */     }
/*     */     catch (PrivilegedActionException localPrivilegedActionException) {
/*     */     }
/* 762 */     if (localArrayList == null) {
/* 763 */       return new String[0];
/*     */     }
/* 765 */     return (String[])localArrayList.toArray(new String[localArrayList.size()]);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  85 */     String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.print.polling"));
/*     */ 
/*  88 */     if (str1 != null) {
/*  89 */       if (str1.equalsIgnoreCase("true"))
/*  90 */         pollServices = true;
/*  91 */       else if (str1.equalsIgnoreCase("false")) {
/*  92 */         pollServices = false;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 100 */     String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.print.minRefreshTime"));
/*     */ 
/* 104 */     if (str2 != null) {
/*     */       try {
/* 106 */         minRefreshTime = new Integer(str2).intValue();
/*     */       } catch (NumberFormatException localNumberFormatException) {
/*     */       }
/* 109 */       if (minRefreshTime < 120)
/* 110 */         minRefreshTime = 120;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class PrinterChangeListener extends Thread
/*     */   {
/*     */     private PrinterChangeListener()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/*     */       while (true)
/*     */       {
/*     */         try
/*     */         {
/* 775 */           UnixPrintServiceLookup.this.refreshServices();
/*     */         } catch (Exception localException) {
/* 777 */           IPPPrintService.debug_println(UnixPrintServiceLookup.debugPrefix + "Exception in refresh thread.");
/*     */           return;
/*     */         }
/*     */         int i;
/* 781 */         if ((UnixPrintServiceLookup.this.printServices != null) && (UnixPrintServiceLookup.this.printServices.length > UnixPrintServiceLookup.minRefreshTime))
/*     */         {
/* 784 */           i = UnixPrintServiceLookup.this.printServices.length;
/*     */         }
/* 786 */         else i = UnixPrintServiceLookup.minRefreshTime;
/*     */         try
/*     */         {
/* 789 */           sleep(i * 1000);
/*     */         }
/*     */         catch (InterruptedException localInterruptedException)
/*     */         {
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.print.UnixPrintServiceLookup
 * JD-Core Version:    0.6.2
 */