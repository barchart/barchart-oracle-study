/*     */ package sun.awt.motif;
/*     */ 
/*     */ import java.awt.AWTException;
/*     */ import java.awt.Button;
/*     */ import java.awt.Canvas;
/*     */ import java.awt.Checkbox;
/*     */ import java.awt.CheckboxMenuItem;
/*     */ import java.awt.Choice;
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.Cursor;
/*     */ import java.awt.Desktop;
/*     */ import java.awt.Dialog;
/*     */ import java.awt.Dialog.ModalExclusionType;
/*     */ import java.awt.Dialog.ModalityType;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.FileDialog;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Frame;
/*     */ import java.awt.GraphicsConfiguration;
/*     */ import java.awt.GraphicsDevice;
/*     */ import java.awt.GraphicsEnvironment;
/*     */ import java.awt.HeadlessException;
/*     */ import java.awt.Image;
/*     */ import java.awt.Insets;
/*     */ import java.awt.JobAttributes;
/*     */ import java.awt.KeyboardFocusManager;
/*     */ import java.awt.Label;
/*     */ import java.awt.List;
/*     */ import java.awt.Menu;
/*     */ import java.awt.MenuBar;
/*     */ import java.awt.MenuItem;
/*     */ import java.awt.PageAttributes;
/*     */ import java.awt.Panel;
/*     */ import java.awt.Point;
/*     */ import java.awt.PopupMenu;
/*     */ import java.awt.PrintJob;
/*     */ import java.awt.Robot;
/*     */ import java.awt.ScrollPane;
/*     */ import java.awt.Scrollbar;
/*     */ import java.awt.SystemTray;
/*     */ import java.awt.TextArea;
/*     */ import java.awt.TextField;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.TrayIcon;
/*     */ import java.awt.Window;
/*     */ import java.awt.datatransfer.Clipboard;
/*     */ import java.awt.dnd.DragGestureEvent;
/*     */ import java.awt.dnd.DragGestureListener;
/*     */ import java.awt.dnd.DragGestureRecognizer;
/*     */ import java.awt.dnd.DragSource;
/*     */ import java.awt.dnd.InvalidDnDOperationException;
/*     */ import java.awt.dnd.peer.DragSourceContextPeer;
/*     */ import java.awt.im.InputMethodHighlight;
/*     */ import java.awt.im.spi.InputMethodDescriptor;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.awt.peer.ButtonPeer;
/*     */ import java.awt.peer.CanvasPeer;
/*     */ import java.awt.peer.CheckboxMenuItemPeer;
/*     */ import java.awt.peer.CheckboxPeer;
/*     */ import java.awt.peer.ChoicePeer;
/*     */ import java.awt.peer.DesktopPeer;
/*     */ import java.awt.peer.DialogPeer;
/*     */ import java.awt.peer.FileDialogPeer;
/*     */ import java.awt.peer.FontPeer;
/*     */ import java.awt.peer.FramePeer;
/*     */ import java.awt.peer.KeyboardFocusManagerPeer;
/*     */ import java.awt.peer.LabelPeer;
/*     */ import java.awt.peer.ListPeer;
/*     */ import java.awt.peer.MenuBarPeer;
/*     */ import java.awt.peer.MenuItemPeer;
/*     */ import java.awt.peer.MenuPeer;
/*     */ import java.awt.peer.PanelPeer;
/*     */ import java.awt.peer.PopupMenuPeer;
/*     */ import java.awt.peer.RobotPeer;
/*     */ import java.awt.peer.ScrollPanePeer;
/*     */ import java.awt.peer.ScrollbarPeer;
/*     */ import java.awt.peer.SystemTrayPeer;
/*     */ import java.awt.peer.TextAreaPeer;
/*     */ import java.awt.peer.TextFieldPeer;
/*     */ import java.awt.peer.TrayIconPeer;
/*     */ import java.awt.peer.WindowPeer;
/*     */ import java.io.PrintStream;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import sun.awt.AWTAutoShutdown;
/*     */ import sun.awt.SunToolkit;
/*     */ import sun.awt.SunToolkit.OperationTimedOut;
/*     */ import sun.awt.UNIXToolkit;
/*     */ import sun.awt.X11GraphicsConfig;
/*     */ import sun.awt.XSettings;
/*     */ import sun.misc.PerformanceLogger;
/*     */ import sun.print.PrintJob2D;
/*     */ import sun.security.action.GetBooleanAction;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ public class MToolkit extends UNIXToolkit
/*     */   implements Runnable
/*     */ {
/*  80 */   private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.motif.MToolkit");
/*     */ 
/*  88 */   protected static boolean dynamicLayoutSetting = false;
/*     */   private boolean loadedXSettings;
/*     */   private XSettings xs;
/*     */   static final X11GraphicsConfig config;
/* 123 */   private static final boolean motifdnd = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("awt.dnd.motifdnd"))).booleanValue();
/*     */   static ColorModel screenmodel;
/*     */   private static final String prefix = "DnD.Cursor.";
/*     */   private static final String postfix = ".32x32";
/*     */   private static final String dndPrefix = "DnD.";
/*     */   private static final long WORKAROUND_SLEEP = 100L;
/*     */   public static final int UNDETERMINED_WM = 1;
/*     */   public static final int NO_WM = 2;
/*     */   public static final int OTHER_WM = 3;
/*     */   public static final int OPENLOOK_WM = 4;
/*     */   public static final int MOTIF_WM = 5;
/*     */   public static final int CDE_WM = 6;
/*     */   public static final int ENLIGHTEN_WM = 7;
/*     */   public static final int KDE2_WM = 8;
/*     */   public static final int SAWFISH_WM = 9;
/*     */   public static final int ICE_WM = 10;
/*     */   public static final int METACITY_WM = 11;
/*     */   public static final int COMPIZ_WM = 12;
/*     */   public static final int LG3D_WM = 13;
/*     */ 
/*     */   public MToolkit()
/*     */   {
/* 131 */     if (PerformanceLogger.loggingEnabled()) {
/* 132 */       PerformanceLogger.setTime("MToolkit construction");
/*     */     }
/* 134 */     if (!GraphicsEnvironment.isHeadless()) {
/* 135 */       String str = null;
/*     */ 
/* 137 */       StackTraceElement[] arrayOfStackTraceElement = new Throwable().getStackTrace();
/* 138 */       int i = arrayOfStackTraceElement.length - 1;
/* 139 */       if (i >= 0) {
/* 140 */         str = arrayOfStackTraceElement[i].getClassName();
/*     */       }
/* 142 */       if ((str == null) || (str.equals(""))) {
/* 143 */         str = "AWT";
/*     */       }
/*     */ 
/* 146 */       init(str);
/*     */ 
/* 149 */       Thread localThread = new Thread(this, "AWT-Motif");
/* 150 */       localThread.setPriority(6);
/* 151 */       localThread.setDaemon(true);
/*     */ 
/* 153 */       PrivilegedAction local1 = new PrivilegedAction() {
/*     */         public Void run() {
/* 155 */           Object localObject = Thread.currentThread().getThreadGroup();
/* 156 */           ThreadGroup localThreadGroup = ((ThreadGroup)localObject).getParent();
/*     */ 
/* 158 */           while (localThreadGroup != null) {
/* 159 */             localObject = localThreadGroup;
/* 160 */             localThreadGroup = ((ThreadGroup)localObject).getParent();
/*     */           }
/* 162 */           Thread localThread = new Thread((ThreadGroup)localObject, new Runnable() {
/*     */             public void run() {
/* 164 */               MToolkit.this.shutdown();
/*     */             }
/*     */           }
/*     */           , "Shutdown-Thread");
/*     */ 
/* 167 */           localThread.setContextClassLoader(null);
/* 168 */           Runtime.getRuntime().addShutdownHook(localThread);
/* 169 */           return null;
/*     */         }
/*     */       };
/* 172 */       AccessController.doPrivileged(local1);
/*     */ 
/* 179 */       AWTAutoShutdown.notifyToolkitThreadBusy();
/*     */ 
/* 181 */       localThread.start();
/*     */     }
/*     */   }
/*     */ 
/*     */   public native void init(String paramString);
/*     */ 
/*     */   public native void run();
/*     */ 
/*     */   private native void shutdown();
/*     */ 
/*     */   public ButtonPeer createButton(Button paramButton)
/*     */   {
/* 197 */     return null;
/*     */   }
/*     */ 
/*     */   public TextFieldPeer createTextField(TextField paramTextField)
/*     */   {
/* 204 */     return null;
/*     */   }
/*     */ 
/*     */   public LabelPeer createLabel(Label paramLabel)
/*     */   {
/* 211 */     return null;
/*     */   }
/*     */ 
/*     */   public ListPeer createList(List paramList)
/*     */   {
/* 218 */     return null;
/*     */   }
/*     */ 
/*     */   public CheckboxPeer createCheckbox(Checkbox paramCheckbox)
/*     */   {
/* 225 */     return null;
/*     */   }
/*     */ 
/*     */   public ScrollbarPeer createScrollbar(Scrollbar paramScrollbar)
/*     */   {
/* 232 */     return null;
/*     */   }
/*     */ 
/*     */   public ScrollPanePeer createScrollPane(ScrollPane paramScrollPane)
/*     */   {
/* 239 */     return null;
/*     */   }
/*     */ 
/*     */   public TextAreaPeer createTextArea(TextArea paramTextArea)
/*     */   {
/* 246 */     return null;
/*     */   }
/*     */ 
/*     */   public ChoicePeer createChoice(Choice paramChoice)
/*     */   {
/* 253 */     return null;
/*     */   }
/*     */ 
/*     */   public FramePeer createFrame(Frame paramFrame)
/*     */   {
/* 260 */     return null;
/*     */   }
/*     */ 
/*     */   public CanvasPeer createCanvas(Canvas paramCanvas)
/*     */   {
/* 267 */     return null;
/*     */   }
/*     */ 
/*     */   public PanelPeer createPanel(Panel paramPanel)
/*     */   {
/* 274 */     return null;
/*     */   }
/*     */ 
/*     */   public WindowPeer createWindow(Window paramWindow)
/*     */   {
/* 281 */     return null;
/*     */   }
/*     */ 
/*     */   public DialogPeer createDialog(Dialog paramDialog)
/*     */   {
/* 288 */     return null;
/*     */   }
/*     */ 
/*     */   public FileDialogPeer createFileDialog(FileDialog paramFileDialog)
/*     */   {
/* 295 */     return null;
/*     */   }
/*     */ 
/*     */   public MenuBarPeer createMenuBar(MenuBar paramMenuBar)
/*     */   {
/* 302 */     return null;
/*     */   }
/*     */ 
/*     */   public MenuPeer createMenu(Menu paramMenu)
/*     */   {
/* 309 */     return null;
/*     */   }
/*     */ 
/*     */   public PopupMenuPeer createPopupMenu(PopupMenu paramPopupMenu)
/*     */   {
/* 316 */     return null;
/*     */   }
/*     */ 
/*     */   public MenuItemPeer createMenuItem(MenuItem paramMenuItem)
/*     */   {
/* 323 */     return null;
/*     */   }
/*     */ 
/*     */   public CheckboxMenuItemPeer createCheckboxMenuItem(CheckboxMenuItem paramCheckboxMenuItem)
/*     */   {
/* 330 */     return null;
/*     */   }
/*     */ 
/*     */   public KeyboardFocusManagerPeer createKeyboardFocusManagerPeer(KeyboardFocusManager paramKeyboardFocusManager) {
/* 334 */     return null;
/*     */   }
/*     */ 
/*     */   public FontPeer getFontPeer(String paramString, int paramInt)
/*     */   {
/* 347 */     return new MFontPeer(paramString, paramInt);
/*     */   }
/*     */ 
/*     */   public void setDynamicLayout(boolean paramBoolean)
/*     */   {
/* 357 */     dynamicLayoutSetting = paramBoolean;
/*     */   }
/*     */ 
/*     */   protected boolean isDynamicLayoutSet() {
/* 361 */     return dynamicLayoutSetting;
/*     */   }
/*     */ 
/*     */   protected native boolean isDynamicLayoutSupportedNative();
/*     */ 
/*     */   public boolean isDynamicLayoutActive()
/*     */   {
/* 370 */     return isDynamicLayoutSupportedNative();
/*     */   }
/*     */ 
/*     */   public native boolean isFrameStateSupported(int paramInt);
/*     */ 
/*     */   public TrayIconPeer createTrayIcon(TrayIcon paramTrayIcon) throws HeadlessException {
/* 376 */     return null;
/*     */   }
/*     */ 
/*     */   public SystemTrayPeer createSystemTray(SystemTray paramSystemTray) throws HeadlessException {
/* 380 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean isTraySupported() {
/* 384 */     return false;
/*     */   }
/*     */ 
/*     */   static native ColorModel makeColorModel();
/*     */ 
/*     */   static ColorModel getStaticColorModel()
/*     */   {
/* 391 */     if (screenmodel == null) {
/* 392 */       screenmodel = config.getColorModel();
/*     */     }
/* 394 */     return screenmodel;
/*     */   }
/*     */ 
/*     */   public ColorModel getColorModel() {
/* 398 */     return getStaticColorModel();
/*     */   }
/*     */ 
/*     */   public native int getScreenResolution();
/*     */ 
/*     */   public Insets getScreenInsets(GraphicsConfiguration paramGraphicsConfiguration) {
/* 404 */     return new Insets(0, 0, 0, 0);
/*     */   }
/*     */ 
/*     */   protected native int getScreenWidth();
/*     */ 
/*     */   protected native int getScreenHeight();
/*     */ 
/*     */   public FontMetrics getFontMetrics(Font paramFont)
/*     */   {
/* 419 */     return super.getFontMetrics(paramFont);
/*     */   }
/*     */ 
/*     */   public PrintJob getPrintJob(Frame paramFrame, String paramString, Properties paramProperties)
/*     */   {
/* 425 */     if (GraphicsEnvironment.isHeadless()) {
/* 426 */       throw new IllegalArgumentException();
/*     */     }
/*     */ 
/* 429 */     PrintJob2D localPrintJob2D = new PrintJob2D(paramFrame, paramString, paramProperties);
/*     */ 
/* 431 */     if (!localPrintJob2D.printDialog()) {
/* 432 */       localPrintJob2D = null;
/*     */     }
/*     */ 
/* 435 */     return localPrintJob2D;
/*     */   }
/*     */ 
/*     */   public PrintJob getPrintJob(Frame paramFrame, String paramString, JobAttributes paramJobAttributes, PageAttributes paramPageAttributes)
/*     */   {
/* 443 */     if (GraphicsEnvironment.isHeadless()) {
/* 444 */       throw new IllegalArgumentException();
/*     */     }
/*     */ 
/* 447 */     PrintJob2D localPrintJob2D = new PrintJob2D(paramFrame, paramString, paramJobAttributes, paramPageAttributes);
/*     */ 
/* 450 */     if (!localPrintJob2D.printDialog()) {
/* 451 */       localPrintJob2D = null;
/*     */     }
/*     */ 
/* 454 */     return localPrintJob2D;
/*     */   }
/*     */ 
/*     */   public native void beep();
/*     */ 
/*     */   public Clipboard getSystemClipboard()
/*     */   {
/* 470 */     return null;
/*     */   }
/*     */ 
/*     */   public Clipboard getSystemSelection()
/*     */   {
/* 484 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean getLockingKeyState(int paramInt) {
/* 488 */     if ((paramInt != 20) && (paramInt != 144) && (paramInt != 145) && (paramInt != 262))
/*     */     {
/* 490 */       throw new IllegalArgumentException("invalid key for Toolkit.getLockingKeyState");
/*     */     }
/* 492 */     return getLockingKeyStateNative(paramInt);
/*     */   }
/*     */ 
/*     */   public native boolean getLockingKeyStateNative(int paramInt);
/*     */ 
/*     */   public native void loadSystemColors(int[] paramArrayOfInt);
/*     */ 
/*     */   public static Container getNativeContainer(Component paramComponent)
/*     */   {
/* 504 */     return Toolkit.getNativeContainer(paramComponent);
/*     */   }
/*     */ 
/*     */   protected static final Object targetToPeer(Object paramObject) {
/* 508 */     return SunToolkit.targetToPeer(paramObject);
/*     */   }
/*     */ 
/*     */   protected static final void targetDisposedPeer(Object paramObject1, Object paramObject2) {
/* 512 */     SunToolkit.targetDisposedPeer(paramObject1, paramObject2);
/*     */   }
/*     */ 
/*     */   public DragSourceContextPeer createDragSourceContextPeer(DragGestureEvent paramDragGestureEvent)
/*     */     throws InvalidDnDOperationException
/*     */   {
/* 521 */     return null;
/*     */   }
/*     */ 
/*     */   public <T extends DragGestureRecognizer> T createDragGestureRecognizer(Class<T> paramClass, DragSource paramDragSource, Component paramComponent, int paramInt, DragGestureListener paramDragGestureListener)
/*     */   {
/* 532 */     return null;
/*     */   }
/*     */ 
/*     */   public InputMethodDescriptor getInputMethodAdapterDescriptor()
/*     */     throws AWTException
/*     */   {
/* 539 */     return null;
/*     */   }
/*     */ 
/*     */   public Map mapInputMethodHighlight(InputMethodHighlight paramInputMethodHighlight)
/*     */   {
/* 546 */     return null;
/*     */   }
/*     */ 
/*     */   public Cursor createCustomCursor(Image paramImage, Point paramPoint, String paramString)
/*     */     throws IndexOutOfBoundsException
/*     */   {
/* 554 */     return null;
/*     */   }
/*     */ 
/*     */   public Dimension getBestCursorSize(int paramInt1, int paramInt2)
/*     */   {
/* 561 */     return null;
/*     */   }
/*     */ 
/*     */   public int getMaximumCursorColors()
/*     */   {
/* 566 */     return 2;
/*     */   }
/*     */ 
/*     */   protected Object lazilyLoadDesktopProperty(String paramString)
/*     */   {
/* 574 */     if (paramString.startsWith("DnD.Cursor.")) {
/* 575 */       String str = paramString.substring("DnD.Cursor.".length(), paramString.length()) + ".32x32";
/*     */       try
/*     */       {
/* 578 */         return Cursor.getSystemCustomCursor(str);
/*     */       } catch (AWTException localAWTException) {
/* 580 */         System.err.println("cannot load system cursor: " + str);
/*     */ 
/* 582 */         return null;
/*     */       }
/*     */     }
/*     */ 
/* 586 */     if (paramString.equals("awt.dynamicLayoutSupported")) {
/* 587 */       return lazilyLoadDynamicLayoutSupportedProperty(paramString);
/*     */     }
/*     */ 
/* 590 */     if ((!this.loadedXSettings) && ((paramString.startsWith("gnome.")) || (paramString.equals("awt.font.desktophints")) || (paramString.startsWith("DnD."))))
/*     */     {
/* 594 */       this.loadedXSettings = true;
/* 595 */       if (!GraphicsEnvironment.isHeadless()) {
/* 596 */         loadXSettings();
/* 597 */         this.desktopProperties.put("awt.font.desktophints", SunToolkit.getDesktopFontHints());
/*     */ 
/* 599 */         return this.desktopProperties.get(paramString);
/*     */       }
/*     */     }
/*     */ 
/* 603 */     return super.lazilyLoadDesktopProperty(paramString);
/*     */   }
/*     */ 
/*     */   protected Boolean lazilyLoadDynamicLayoutSupportedProperty(String paramString)
/*     */   {
/* 611 */     boolean bool = isDynamicLayoutSupportedNative();
/*     */ 
/* 613 */     if (log.isLoggable(400)) {
/* 614 */       log.finer("nativeDynamic == " + bool);
/*     */     }
/*     */ 
/* 617 */     return Boolean.valueOf(bool);
/*     */   }
/*     */ 
/*     */   private native int getMulticlickTime();
/*     */ 
/*     */   protected void initializeDesktopProperties() {
/* 623 */     this.desktopProperties.put("DnD.Autoscroll.initialDelay", Integer.valueOf(50));
/* 624 */     this.desktopProperties.put("DnD.Autoscroll.interval", Integer.valueOf(50));
/* 625 */     this.desktopProperties.put("DnD.Autoscroll.cursorHysteresis", Integer.valueOf(5));
/*     */ 
/* 635 */     if (!GraphicsEnvironment.isHeadless()) {
/* 636 */       this.desktopProperties.put("awt.multiClickInterval", Integer.valueOf(getMulticlickTime()));
/*     */ 
/* 638 */       this.desktopProperties.put("awt.mouse.numButtons", Integer.valueOf(getNumberOfButtons()));
/*     */     }
/*     */   }
/*     */ 
/*     */   public RobotPeer createRobot(Robot paramRobot, GraphicsDevice paramGraphicsDevice)
/*     */   {
/* 646 */     return null;
/*     */   }
/*     */ 
/*     */   static boolean useMotifDnD() {
/* 650 */     return motifdnd;
/*     */   }
/*     */ 
/*     */   private native void loadXSettings();
/*     */ 
/*     */   private void parseXSettings(int paramInt, byte[] paramArrayOfByte)
/*     */   {
/* 680 */     if (this.xs == null) {
/* 681 */       this.xs = new XSettings();
/*     */     }
/*     */ 
/* 684 */     Map localMap = this.xs.update(paramArrayOfByte);
/* 685 */     if ((localMap == null) || (localMap.isEmpty())) {
/* 686 */       return;
/*     */     }
/*     */ 
/* 689 */     Iterator localIterator = localMap.entrySet().iterator();
/* 690 */     while (localIterator.hasNext()) {
/* 691 */       localObject1 = (Map.Entry)localIterator.next();
/* 692 */       String str = (String)((Map.Entry)localObject1).getKey();
/*     */ 
/* 694 */       str = "gnome." + str;
/* 695 */       setDesktopProperty(str, ((Map.Entry)localObject1).getValue());
/*     */     }
/*     */ 
/* 705 */     setDesktopProperty("awt.font.desktophints", SunToolkit.getDesktopFontHints());
/*     */ 
/* 708 */     Object localObject1 = null;
/* 709 */     synchronized (this) {
/* 710 */       localObject1 = (Integer)this.desktopProperties.get("gnome.Net/DndDragThreshold");
/*     */     }
/* 712 */     if (localObject1 != null)
/* 713 */       setDesktopProperty("DnD.gestureMotionThreshold", localObject1);
/*     */   }
/*     */ 
/*     */   protected boolean needsXEmbedImpl()
/*     */   {
/* 718 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isModalityTypeSupported(Dialog.ModalityType paramModalityType) {
/* 722 */     return (paramModalityType == Dialog.ModalityType.MODELESS) || (paramModalityType == Dialog.ModalityType.APPLICATION_MODAL);
/*     */   }
/*     */ 
/*     */   public boolean isModalExclusionTypeSupported(Dialog.ModalExclusionType paramModalExclusionType)
/*     */   {
/* 727 */     return paramModalExclusionType == Dialog.ModalExclusionType.NO_EXCLUDE;
/*     */   }
/*     */ 
/*     */   private native boolean isSyncUpdated();
/*     */ 
/*     */   private native boolean isSyncFailed();
/*     */ 
/*     */   private native int getEventNumber();
/*     */ 
/*     */   private native void updateSyncSelection();
/*     */ 
/*     */   protected boolean syncNativeQueue(long paramLong)
/*     */   {
/* 740 */     awtLock();
/*     */     try {
/* 742 */       long l1 = getEventNumber();
/* 743 */       updateSyncSelection();
/*     */ 
/* 746 */       long l2 = System.currentTimeMillis();
/* 747 */       while ((!isSyncUpdated()) && (!isSyncFailed())) {
/*     */         try {
/* 749 */           awtLockWait(paramLong);
/*     */         } catch (InterruptedException localInterruptedException1) {
/* 751 */           throw new RuntimeException(localInterruptedException1);
/*     */         }
/*     */ 
/* 755 */         if ((System.currentTimeMillis() - l2 > paramLong) && (paramLong >= 0L)) {
/* 756 */           throw new SunToolkit.OperationTimedOut();
/*     */         }
/*     */       }
/* 759 */       if ((isSyncFailed()) && (getEventNumber() - l1 == 1L)) {
/* 760 */         awtUnlock();
/*     */         try {
/* 762 */           Thread.sleep(100L);
/*     */         } catch (InterruptedException localInterruptedException2) {
/* 764 */           throw new RuntimeException(localInterruptedException2);
/*     */         }
/*     */         finally {
/*     */         }
/*     */       }
/* 769 */       return getEventNumber() - l1 > 2L;
/*     */     } finally {
/* 771 */       awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void grab(Window paramWindow) {
/* 776 */     WindowPeer localWindowPeer = (WindowPeer)paramWindow.getPeer();
/* 777 */     if (localWindowPeer != null)
/* 778 */       nativeGrab(localWindowPeer);
/*     */   }
/*     */ 
/*     */   public void ungrab(Window paramWindow)
/*     */   {
/* 783 */     WindowPeer localWindowPeer = (WindowPeer)paramWindow.getPeer();
/* 784 */     if (localWindowPeer != null)
/* 785 */       nativeUnGrab(localWindowPeer);
/*     */   }
/*     */ 
/*     */   private native void nativeGrab(WindowPeer paramWindowPeer);
/*     */ 
/*     */   private native void nativeUnGrab(WindowPeer paramWindowPeer);
/*     */ 
/*     */   public boolean isDesktopSupported() {
/* 793 */     return false;
/*     */   }
/*     */ 
/*     */   public DesktopPeer createDesktopPeer(Desktop paramDesktop) throws HeadlessException
/*     */   {
/* 798 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public static int getWMID()
/*     */   {
/* 817 */     String str = getWMName();
/*     */ 
/* 819 */     if ("NO_WM".equals(str))
/* 820 */       return 2;
/* 821 */     if ("OTHER_WM".equals(str))
/* 822 */       return 3;
/* 823 */     if ("ENLIGHTEN_WM".equals(str))
/* 824 */       return 7;
/* 825 */     if ("KDE2_WM".equals(str))
/* 826 */       return 8;
/* 827 */     if ("SAWFISH_WM".equals(str))
/* 828 */       return 9;
/* 829 */     if ("ICE_WM".equals(str))
/* 830 */       return 10;
/* 831 */     if ("METACITY_WM".equals(str))
/* 832 */       return 11;
/* 833 */     if ("OPENLOOK_WM".equals(str))
/* 834 */       return 4;
/* 835 */     if ("MOTIF_WM".equals(str))
/* 836 */       return 5;
/* 837 */     if ("CDE_WM".equals(str))
/* 838 */       return 6;
/* 839 */     if ("COMPIZ_WM".equals(str))
/* 840 */       return 12;
/* 841 */     if ("LG3D_WM".equals(str)) {
/* 842 */       return 13;
/*     */     }
/* 844 */     return 1;
/*     */   }
/*     */ 
/*     */   private static native String getWMName();
/*     */ 
/*     */   static
/*     */   {
/* 114 */     if (GraphicsEnvironment.isHeadless())
/* 115 */       config = null;
/*     */     else
/* 117 */       config = (X11GraphicsConfig)GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.motif.MToolkit
 * JD-Core Version:    0.6.2
 */