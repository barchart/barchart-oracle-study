/*     */ package sun.awt;
/*     */ 
/*     */ import java.awt.AWTException;
/*     */ import java.awt.Button;
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
/*     */ import java.awt.FileDialog;
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
/*     */ import java.awt.Point;
/*     */ import java.awt.PopupMenu;
/*     */ import java.awt.PrintJob;
/*     */ import java.awt.Robot;
/*     */ import java.awt.ScrollPane;
/*     */ import java.awt.Scrollbar;
/*     */ import java.awt.SystemTray;
/*     */ import java.awt.TextArea;
/*     */ import java.awt.TextField;
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
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public class HToolkit extends SunToolkit
/*     */   implements ComponentFactory
/*     */ {
/*     */   public WindowPeer createWindow(Window paramWindow)
/*     */     throws HeadlessException
/*     */   {
/*  56 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public FramePeer createFrame(Frame paramFrame) throws HeadlessException
/*     */   {
/*  61 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public DialogPeer createDialog(Dialog paramDialog) throws HeadlessException
/*     */   {
/*  66 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public ButtonPeer createButton(Button paramButton) throws HeadlessException
/*     */   {
/*  71 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public TextFieldPeer createTextField(TextField paramTextField) throws HeadlessException
/*     */   {
/*  76 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public ChoicePeer createChoice(Choice paramChoice) throws HeadlessException
/*     */   {
/*  81 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public LabelPeer createLabel(Label paramLabel) throws HeadlessException
/*     */   {
/*  86 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public ListPeer createList(List paramList) throws HeadlessException
/*     */   {
/*  91 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public CheckboxPeer createCheckbox(Checkbox paramCheckbox) throws HeadlessException
/*     */   {
/*  96 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public ScrollbarPeer createScrollbar(Scrollbar paramScrollbar) throws HeadlessException
/*     */   {
/* 101 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public ScrollPanePeer createScrollPane(ScrollPane paramScrollPane) throws HeadlessException
/*     */   {
/* 106 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public TextAreaPeer createTextArea(TextArea paramTextArea) throws HeadlessException
/*     */   {
/* 111 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public FileDialogPeer createFileDialog(FileDialog paramFileDialog) throws HeadlessException
/*     */   {
/* 116 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public MenuBarPeer createMenuBar(MenuBar paramMenuBar) throws HeadlessException
/*     */   {
/* 121 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public MenuPeer createMenu(Menu paramMenu) throws HeadlessException
/*     */   {
/* 126 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public PopupMenuPeer createPopupMenu(PopupMenu paramPopupMenu) throws HeadlessException
/*     */   {
/* 131 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public MenuItemPeer createMenuItem(MenuItem paramMenuItem) throws HeadlessException
/*     */   {
/* 136 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public CheckboxMenuItemPeer createCheckboxMenuItem(CheckboxMenuItem paramCheckboxMenuItem) throws HeadlessException
/*     */   {
/* 141 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public DragSourceContextPeer createDragSourceContextPeer(DragGestureEvent paramDragGestureEvent)
/*     */     throws InvalidDnDOperationException
/*     */   {
/* 147 */     throw new InvalidDnDOperationException("Headless environment");
/*     */   }
/*     */ 
/*     */   public RobotPeer createRobot(Robot paramRobot, GraphicsDevice paramGraphicsDevice) throws AWTException, HeadlessException
/*     */   {
/* 152 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public KeyboardFocusManagerPeer createKeyboardFocusManagerPeer(KeyboardFocusManager paramKeyboardFocusManager)
/*     */   {
/* 157 */     return new KeyboardFocusManagerPeer() {
/*     */       public Window getCurrentFocusedWindow() {
/* 159 */         return null; } 
/*     */       public void setCurrentFocusOwner(Component paramAnonymousComponent) {  } 
/* 161 */       public Component getCurrentFocusOwner() { return null; }
/*     */ 
/*     */       public void clearGlobalFocusOwner(Window paramAnonymousWindow) {
/*     */       } } ;
/*     */   }
/*     */ 
/*     */   public TrayIconPeer createTrayIcon(TrayIcon paramTrayIcon) throws HeadlessException {
/* 168 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public SystemTrayPeer createSystemTray(SystemTray paramSystemTray) throws HeadlessException
/*     */   {
/* 173 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public boolean isTraySupported() {
/* 177 */     return false;
/*     */   }
/*     */ 
/*     */   public GlobalCursorManager getGlobalCursorManager() throws HeadlessException
/*     */   {
/* 182 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   protected void loadSystemColors(int[] paramArrayOfInt)
/*     */     throws HeadlessException
/*     */   {
/* 190 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public ColorModel getColorModel() throws HeadlessException
/*     */   {
/* 195 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public int getScreenResolution() throws HeadlessException
/*     */   {
/* 200 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public Map mapInputMethodHighlight(InputMethodHighlight paramInputMethodHighlight) throws HeadlessException
/*     */   {
/* 205 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public int getMenuShortcutKeyMask() throws HeadlessException
/*     */   {
/* 210 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public boolean getLockingKeyState(int paramInt) throws UnsupportedOperationException
/*     */   {
/* 215 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public void setLockingKeyState(int paramInt, boolean paramBoolean) throws UnsupportedOperationException
/*     */   {
/* 220 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public Cursor createCustomCursor(Image paramImage, Point paramPoint, String paramString) throws IndexOutOfBoundsException, HeadlessException
/*     */   {
/* 225 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public Dimension getBestCursorSize(int paramInt1, int paramInt2) throws HeadlessException
/*     */   {
/* 230 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public int getMaximumCursorColors() throws HeadlessException
/*     */   {
/* 235 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public <T extends DragGestureRecognizer> T createDragGestureRecognizer(Class<T> paramClass, DragSource paramDragSource, Component paramComponent, int paramInt, DragGestureListener paramDragGestureListener)
/*     */   {
/* 243 */     return null;
/*     */   }
/*     */ 
/*     */   public int getScreenHeight() throws HeadlessException
/*     */   {
/* 248 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public int getScreenWidth() throws HeadlessException
/*     */   {
/* 253 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public Dimension getScreenSize() throws HeadlessException
/*     */   {
/* 258 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public Insets getScreenInsets(GraphicsConfiguration paramGraphicsConfiguration) throws HeadlessException
/*     */   {
/* 263 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public void setDynamicLayout(boolean paramBoolean) throws HeadlessException
/*     */   {
/* 268 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   protected boolean isDynamicLayoutSet() throws HeadlessException
/*     */   {
/* 273 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public boolean isDynamicLayoutActive() throws HeadlessException
/*     */   {
/* 278 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public Clipboard getSystemClipboard() throws HeadlessException
/*     */   {
/* 283 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public PrintJob getPrintJob(Frame paramFrame, String paramString, JobAttributes paramJobAttributes, PageAttributes paramPageAttributes)
/*     */   {
/* 292 */     if (paramFrame != null)
/*     */     {
/* 294 */       throw new HeadlessException();
/*     */     }
/* 296 */     throw new IllegalArgumentException("PrintJob not supported in a headless environment");
/*     */   }
/*     */ 
/*     */   public PrintJob getPrintJob(Frame paramFrame, String paramString, Properties paramProperties)
/*     */   {
/* 302 */     if (paramFrame != null)
/*     */     {
/* 304 */       throw new HeadlessException();
/*     */     }
/* 306 */     throw new IllegalArgumentException("PrintJob not supported in a headless environment");
/*     */   }
/*     */ 
/*     */   public void sync()
/*     */   {
/*     */   }
/*     */ 
/*     */   protected boolean syncNativeQueue(long paramLong)
/*     */   {
/* 319 */     return false;
/*     */   }
/*     */ 
/*     */   public void beep()
/*     */   {
/* 324 */     System.out.write(7);
/*     */   }
/*     */ 
/*     */   public FontPeer getFontPeer(String paramString, int paramInt)
/*     */   {
/* 332 */     return (FontPeer)null;
/*     */   }
/*     */ 
/*     */   public boolean isModalityTypeSupported(Dialog.ModalityType paramModalityType)
/*     */   {
/* 339 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isModalExclusionTypeSupported(Dialog.ModalExclusionType paramModalExclusionType) {
/* 343 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isDesktopSupported() {
/* 347 */     return false;
/*     */   }
/*     */ 
/*     */   public DesktopPeer createDesktopPeer(Desktop paramDesktop) throws HeadlessException
/*     */   {
/* 352 */     throw new HeadlessException();
/*     */   }
/*     */ 
/*     */   public boolean isWindowOpacityControlSupported() {
/* 356 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isWindowShapingSupported() {
/* 360 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isWindowTranslucencySupported() {
/* 364 */     return false;
/*     */   }
/*     */   public void grab(Window paramWindow) {
/*     */   }
/*     */   public void ungrab(Window paramWindow) {
/*     */   }
/*     */   protected boolean syncNativeQueue() {
/* 371 */     return false;
/*     */   }
/*     */ 
/*     */   public InputMethodDescriptor getInputMethodAdapterDescriptor() throws AWTException
/*     */   {
/* 376 */     return (InputMethodDescriptor)null;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.HToolkit
 * JD-Core Version:    0.6.2
 */