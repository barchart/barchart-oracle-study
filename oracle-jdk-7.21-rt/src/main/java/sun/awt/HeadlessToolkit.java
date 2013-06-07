/*     */ package sun.awt;
/*     */ 
/*     */ import java.awt.AWTException;
/*     */ import java.awt.Button;
/*     */ import java.awt.Canvas;
/*     */ import java.awt.Checkbox;
/*     */ import java.awt.CheckboxMenuItem;
/*     */ import java.awt.Choice;
/*     */ import java.awt.Component;
/*     */ import java.awt.Cursor;
/*     */ import java.awt.Desktop;
/*     */ import java.awt.Dialog;
/*     */ import java.awt.Dialog.ModalExclusionType;
/*     */ import java.awt.Dialog.ModalityType;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.EventQueue;
/*     */ import java.awt.FileDialog;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Frame;
/*     */ import java.awt.GraphicsConfiguration;
/*     */ import java.awt.GraphicsDevice;
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
/*     */ import java.awt.event.AWTEventListener;
/*     */ import java.awt.im.InputMethodHighlight;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.awt.image.ImageObserver;
/*     */ import java.awt.image.ImageProducer;
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
/*     */ import java.beans.PropertyChangeListener;
/*     */ import java.io.PrintStream;
/*     */ import java.net.URL;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public class HeadlessToolkit extends Toolkit
/*     */   implements ComponentFactory, KeyboardFocusManagerPeerProvider
/*     */ {
/*     */   private Toolkit tk;
/*     */   private ComponentFactory componentFactory;
/*     */ 
/*     */   public HeadlessToolkit(Toolkit paramToolkit)
/*     */   {
/*  53 */     this.tk = paramToolkit;
/*  54 */     if ((paramToolkit instanceof KeyboardFocusManagerPeerProvider))
/*  55 */       this.componentFactory = ((KeyboardFocusManagerPeerProvider)paramToolkit);
/*     */   }
/*     */ 
/*     */   public Toolkit getUnderlyingToolkit()
/*     */   {
/*  60 */     return this.tk;
/*     */   }
/*     */ 
/*     */   public CanvasPeer createCanvas(Canvas paramCanvas)
/*     */   {
/*  70 */     return (CanvasPeer)createComponent(paramCanvas);
/*     */   }
/*     */ 
/*     */   public PanelPeer createPanel(Panel paramPanel) {
/*  74 */     return (PanelPeer)createComponent(paramPanel);
/*     */   }
/*     */ 
/*     */   public WindowPeer createWindow(Window paramWindow)
/*     */     throws HeadlessException
/*     */   {
/*  83 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public FramePeer createFrame(Frame paramFrame) throws HeadlessException
/*     */   {
/*  88 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public DialogPeer createDialog(Dialog paramDialog) throws HeadlessException
/*     */   {
/*  93 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public ButtonPeer createButton(Button paramButton) throws HeadlessException
/*     */   {
/*  98 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public TextFieldPeer createTextField(TextField paramTextField) throws HeadlessException
/*     */   {
/* 103 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public ChoicePeer createChoice(Choice paramChoice) throws HeadlessException
/*     */   {
/* 108 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public LabelPeer createLabel(Label paramLabel) throws HeadlessException
/*     */   {
/* 113 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public ListPeer createList(List paramList) throws HeadlessException
/*     */   {
/* 118 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public CheckboxPeer createCheckbox(Checkbox paramCheckbox) throws HeadlessException
/*     */   {
/* 123 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public ScrollbarPeer createScrollbar(Scrollbar paramScrollbar) throws HeadlessException
/*     */   {
/* 128 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public ScrollPanePeer createScrollPane(ScrollPane paramScrollPane) throws HeadlessException
/*     */   {
/* 133 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public TextAreaPeer createTextArea(TextArea paramTextArea) throws HeadlessException
/*     */   {
/* 138 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public FileDialogPeer createFileDialog(FileDialog paramFileDialog) throws HeadlessException
/*     */   {
/* 143 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public MenuBarPeer createMenuBar(MenuBar paramMenuBar) throws HeadlessException
/*     */   {
/* 148 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public MenuPeer createMenu(Menu paramMenu) throws HeadlessException
/*     */   {
/* 153 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public PopupMenuPeer createPopupMenu(PopupMenu paramPopupMenu) throws HeadlessException
/*     */   {
/* 158 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public MenuItemPeer createMenuItem(MenuItem paramMenuItem) throws HeadlessException
/*     */   {
/* 163 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public CheckboxMenuItemPeer createCheckboxMenuItem(CheckboxMenuItem paramCheckboxMenuItem) throws HeadlessException
/*     */   {
/* 168 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public DragSourceContextPeer createDragSourceContextPeer(DragGestureEvent paramDragGestureEvent)
/*     */     throws InvalidDnDOperationException
/*     */   {
/* 174 */     throw new InvalidDnDOperationException("Headless environment");
/*     */   }
/*     */ 
/*     */   public RobotPeer createRobot(Robot paramRobot, GraphicsDevice paramGraphicsDevice) throws AWTException, HeadlessException
/*     */   {
/* 179 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public KeyboardFocusManagerPeer createKeyboardFocusManagerPeer(KeyboardFocusManager paramKeyboardFocusManager)
/*     */   {
/* 184 */     return new KeyboardFocusManagerPeer() {
/*     */       public Window getCurrentFocusedWindow() {
/* 186 */         return null; } 
/*     */       public void setCurrentFocusOwner(Component paramAnonymousComponent) {  } 
/* 188 */       public Component getCurrentFocusOwner() { return null; }
/*     */ 
/*     */       public void clearGlobalFocusOwner(Window paramAnonymousWindow) {
/*     */       } } ;
/*     */   }
/*     */ 
/*     */   public TrayIconPeer createTrayIcon(TrayIcon paramTrayIcon) throws HeadlessException {
/* 195 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public SystemTrayPeer createSystemTray(SystemTray paramSystemTray) throws HeadlessException
/*     */   {
/* 200 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public boolean isTraySupported() {
/* 204 */     return false;
/*     */   }
/*     */ 
/*     */   public GlobalCursorManager getGlobalCursorManager() throws HeadlessException
/*     */   {
/* 209 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   protected void loadSystemColors(int[] paramArrayOfInt)
/*     */     throws HeadlessException
/*     */   {
/* 217 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public ColorModel getColorModel() throws HeadlessException
/*     */   {
/* 222 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public int getScreenResolution() throws HeadlessException
/*     */   {
/* 227 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public Map mapInputMethodHighlight(InputMethodHighlight paramInputMethodHighlight) throws HeadlessException
/*     */   {
/* 232 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public int getMenuShortcutKeyMask() throws HeadlessException
/*     */   {
/* 237 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public boolean getLockingKeyState(int paramInt) throws UnsupportedOperationException
/*     */   {
/* 242 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public void setLockingKeyState(int paramInt, boolean paramBoolean) throws UnsupportedOperationException
/*     */   {
/* 247 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public Cursor createCustomCursor(Image paramImage, Point paramPoint, String paramString) throws IndexOutOfBoundsException, HeadlessException
/*     */   {
/* 252 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public Dimension getBestCursorSize(int paramInt1, int paramInt2) throws HeadlessException
/*     */   {
/* 257 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public int getMaximumCursorColors() throws HeadlessException
/*     */   {
/* 262 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public <T extends DragGestureRecognizer> T createDragGestureRecognizer(Class<T> paramClass, DragSource paramDragSource, Component paramComponent, int paramInt, DragGestureListener paramDragGestureListener)
/*     */   {
/* 270 */     return null;
/*     */   }
/*     */ 
/*     */   public int getScreenHeight() throws HeadlessException
/*     */   {
/* 275 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public int getScreenWidth() throws HeadlessException
/*     */   {
/* 280 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public Dimension getScreenSize() throws HeadlessException
/*     */   {
/* 285 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public Insets getScreenInsets(GraphicsConfiguration paramGraphicsConfiguration) throws HeadlessException
/*     */   {
/* 290 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public void setDynamicLayout(boolean paramBoolean) throws HeadlessException
/*     */   {
/* 295 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   protected boolean isDynamicLayoutSet() throws HeadlessException
/*     */   {
/* 300 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public boolean isDynamicLayoutActive() throws HeadlessException
/*     */   {
/* 305 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public Clipboard getSystemClipboard() throws HeadlessException
/*     */   {
/* 310 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public PrintJob getPrintJob(Frame paramFrame, String paramString, JobAttributes paramJobAttributes, PageAttributes paramPageAttributes)
/*     */   {
/* 319 */     if (paramFrame != null)
/*     */     {
/* 321 */       throw new HeadlessException();
/*     */     }
/* 323 */     throw new NullPointerException("frame must not be null");
/*     */   }
/*     */ 
/*     */   public PrintJob getPrintJob(Frame paramFrame, String paramString, Properties paramProperties)
/*     */   {
/* 328 */     if (paramFrame != null)
/*     */     {
/* 330 */       throw new HeadlessException();
/*     */     }
/* 332 */     throw new NullPointerException("frame must not be null");
/*     */   }
/*     */ 
/*     */   public void sync()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void beep()
/*     */   {
/* 345 */     System.out.write(7);
/*     */   }
/*     */ 
/*     */   public EventQueue getSystemEventQueueImpl()
/*     */   {
/* 352 */     return SunToolkit.getSystemEventQueueImplPP();
/*     */   }
/*     */ 
/*     */   public int checkImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver)
/*     */   {
/* 359 */     return this.tk.checkImage(paramImage, paramInt1, paramInt2, paramImageObserver);
/*     */   }
/*     */ 
/*     */   public boolean prepareImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver)
/*     */   {
/* 364 */     return this.tk.prepareImage(paramImage, paramInt1, paramInt2, paramImageObserver);
/*     */   }
/*     */ 
/*     */   public Image getImage(String paramString) {
/* 368 */     return this.tk.getImage(paramString);
/*     */   }
/*     */ 
/*     */   public Image getImage(URL paramURL) {
/* 372 */     return this.tk.getImage(paramURL);
/*     */   }
/*     */ 
/*     */   public Image createImage(String paramString) {
/* 376 */     return this.tk.createImage(paramString);
/*     */   }
/*     */ 
/*     */   public Image createImage(URL paramURL) {
/* 380 */     return this.tk.createImage(paramURL);
/*     */   }
/*     */ 
/*     */   public Image createImage(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
/* 384 */     return this.tk.createImage(paramArrayOfByte, paramInt1, paramInt2);
/*     */   }
/*     */ 
/*     */   public Image createImage(ImageProducer paramImageProducer) {
/* 388 */     return this.tk.createImage(paramImageProducer);
/*     */   }
/*     */ 
/*     */   public Image createImage(byte[] paramArrayOfByte) {
/* 392 */     return this.tk.createImage(paramArrayOfByte);
/*     */   }
/*     */ 
/*     */   public FontPeer getFontPeer(String paramString, int paramInt)
/*     */   {
/* 400 */     if (this.componentFactory != null) {
/* 401 */       return this.componentFactory.getFontPeer(paramString, paramInt);
/*     */     }
/* 403 */     return null;
/*     */   }
/*     */ 
/*     */   public FontMetrics getFontMetrics(Font paramFont) {
/* 407 */     return this.tk.getFontMetrics(paramFont);
/*     */   }
/*     */ 
/*     */   public String[] getFontList() {
/* 411 */     return this.tk.getFontList();
/*     */   }
/*     */ 
/*     */   public void addPropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener)
/*     */   {
/* 420 */     this.tk.addPropertyChangeListener(paramString, paramPropertyChangeListener);
/*     */   }
/*     */ 
/*     */   public void removePropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener)
/*     */   {
/* 425 */     this.tk.removePropertyChangeListener(paramString, paramPropertyChangeListener);
/*     */   }
/*     */ 
/*     */   public boolean isModalityTypeSupported(Dialog.ModalityType paramModalityType)
/*     */   {
/* 432 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isModalExclusionTypeSupported(Dialog.ModalExclusionType paramModalExclusionType) {
/* 436 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isAlwaysOnTopSupported()
/*     */   {
/* 443 */     return false;
/*     */   }
/*     */ 
/*     */   public void addAWTEventListener(AWTEventListener paramAWTEventListener, long paramLong)
/*     */   {
/* 452 */     this.tk.addAWTEventListener(paramAWTEventListener, paramLong);
/*     */   }
/*     */ 
/*     */   public void removeAWTEventListener(AWTEventListener paramAWTEventListener) {
/* 456 */     this.tk.removeAWTEventListener(paramAWTEventListener);
/*     */   }
/*     */ 
/*     */   public AWTEventListener[] getAWTEventListeners() {
/* 460 */     return this.tk.getAWTEventListeners();
/*     */   }
/*     */ 
/*     */   public AWTEventListener[] getAWTEventListeners(long paramLong) {
/* 464 */     return this.tk.getAWTEventListeners(paramLong);
/*     */   }
/*     */ 
/*     */   public boolean isDesktopSupported() {
/* 468 */     return false;
/*     */   }
/*     */ 
/*     */   public DesktopPeer createDesktopPeer(Desktop paramDesktop) throws HeadlessException
/*     */   {
/* 473 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public boolean areExtraMouseButtonsEnabled() throws HeadlessException {
/* 477 */     throw new HeadlessException();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.HeadlessToolkit
 * JD-Core Version:    0.6.2
 */