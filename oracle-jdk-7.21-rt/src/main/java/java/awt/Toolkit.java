/*      */ package java.awt;
/*      */ 
/*      */ import java.awt.datatransfer.Clipboard;
/*      */ import java.awt.dnd.DragGestureEvent;
/*      */ import java.awt.dnd.DragGestureListener;
/*      */ import java.awt.dnd.DragGestureRecognizer;
/*      */ import java.awt.dnd.DragSource;
/*      */ import java.awt.dnd.InvalidDnDOperationException;
/*      */ import java.awt.dnd.peer.DragSourceContextPeer;
/*      */ import java.awt.event.AWTEventListener;
/*      */ import java.awt.event.AWTEventListenerProxy;
/*      */ import java.awt.font.TextAttribute;
/*      */ import java.awt.im.InputMethodHighlight;
/*      */ import java.awt.image.ColorModel;
/*      */ import java.awt.image.ImageObserver;
/*      */ import java.awt.image.ImageProducer;
/*      */ import java.awt.peer.ButtonPeer;
/*      */ import java.awt.peer.CanvasPeer;
/*      */ import java.awt.peer.CheckboxMenuItemPeer;
/*      */ import java.awt.peer.CheckboxPeer;
/*      */ import java.awt.peer.ChoicePeer;
/*      */ import java.awt.peer.DesktopPeer;
/*      */ import java.awt.peer.DialogPeer;
/*      */ import java.awt.peer.FileDialogPeer;
/*      */ import java.awt.peer.FontPeer;
/*      */ import java.awt.peer.FramePeer;
/*      */ import java.awt.peer.LabelPeer;
/*      */ import java.awt.peer.LightweightPeer;
/*      */ import java.awt.peer.ListPeer;
/*      */ import java.awt.peer.MenuBarPeer;
/*      */ import java.awt.peer.MenuItemPeer;
/*      */ import java.awt.peer.MenuPeer;
/*      */ import java.awt.peer.MouseInfoPeer;
/*      */ import java.awt.peer.PanelPeer;
/*      */ import java.awt.peer.PopupMenuPeer;
/*      */ import java.awt.peer.ScrollPanePeer;
/*      */ import java.awt.peer.ScrollbarPeer;
/*      */ import java.awt.peer.TextAreaPeer;
/*      */ import java.awt.peer.TextFieldPeer;
/*      */ import java.awt.peer.WindowPeer;
/*      */ import java.beans.PropertyChangeEvent;
/*      */ import java.beans.PropertyChangeListener;
/*      */ import java.beans.PropertyChangeSupport;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.InputStream;
/*      */ import java.net.URL;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.util.ArrayList;
/*      */ import java.util.EventListener;
/*      */ import java.util.HashMap;
/*      */ import java.util.Map;
/*      */ import java.util.MissingResourceException;
/*      */ import java.util.Properties;
/*      */ import java.util.ResourceBundle;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.WeakHashMap;
/*      */ import sun.awt.AppContext;
/*      */ import sun.awt.HeadlessToolkit;
/*      */ import sun.awt.NullComponentPeer;
/*      */ import sun.awt.PeerEvent;
/*      */ import sun.awt.SunToolkit;
/*      */ import sun.awt.UngrabEvent;
/*      */ import sun.security.action.LoadLibraryAction;
/*      */ import sun.security.util.SecurityConstants.AWT;
/*      */ import sun.util.CoreResourceBundleControl;
/*      */ import sun.util.logging.PlatformLogger;
/*      */ 
/*      */ public abstract class Toolkit
/*      */ {
/*      */   private static LightweightPeer lightweightMarker;
/*      */   private static Toolkit toolkit;
/*      */   private static String atNames;
/*      */   private static ResourceBundle resources;
/*      */   private static boolean loaded;
/*      */   protected final Map<String, Object> desktopProperties;
/*      */   protected final PropertyChangeSupport desktopPropsSupport;
/* 1981 */   private static final PlatformLogger log = PlatformLogger.getLogger("java.awt.Toolkit");
/*      */   private static final int LONG_BITS = 64;
/*      */   private int[] calls;
/*      */   private static volatile long enabledOnToolkitMask;
/*      */   private AWTEventListener eventListener;
/*      */   private WeakHashMap listener2SelectiveListener;
/*      */ 
/*      */   public Toolkit()
/*      */   {
/* 1926 */     this.desktopProperties = new HashMap();
/*      */ 
/* 1928 */     this.desktopPropsSupport = createPropertyChangeSupport(this);
/*      */ 
/* 1984 */     this.calls = new int[64];
/*      */ 
/* 1986 */     this.eventListener = null;
/* 1987 */     this.listener2SelectiveListener = new WeakHashMap();
/*      */   }
/*      */ 
/*      */   protected abstract DesktopPeer createDesktopPeer(Desktop paramDesktop)
/*      */     throws HeadlessException;
/*      */ 
/*      */   protected abstract ButtonPeer createButton(Button paramButton)
/*      */     throws HeadlessException;
/*      */ 
/*      */   protected abstract TextFieldPeer createTextField(TextField paramTextField)
/*      */     throws HeadlessException;
/*      */ 
/*      */   protected abstract LabelPeer createLabel(Label paramLabel)
/*      */     throws HeadlessException;
/*      */ 
/*      */   protected abstract ListPeer createList(List paramList)
/*      */     throws HeadlessException;
/*      */ 
/*      */   protected abstract CheckboxPeer createCheckbox(Checkbox paramCheckbox)
/*      */     throws HeadlessException;
/*      */ 
/*      */   protected abstract ScrollbarPeer createScrollbar(Scrollbar paramScrollbar)
/*      */     throws HeadlessException;
/*      */ 
/*      */   protected abstract ScrollPanePeer createScrollPane(ScrollPane paramScrollPane)
/*      */     throws HeadlessException;
/*      */ 
/*      */   protected abstract TextAreaPeer createTextArea(TextArea paramTextArea)
/*      */     throws HeadlessException;
/*      */ 
/*      */   protected abstract ChoicePeer createChoice(Choice paramChoice)
/*      */     throws HeadlessException;
/*      */ 
/*      */   protected abstract FramePeer createFrame(Frame paramFrame)
/*      */     throws HeadlessException;
/*      */ 
/*      */   protected abstract CanvasPeer createCanvas(Canvas paramCanvas);
/*      */ 
/*      */   protected abstract PanelPeer createPanel(Panel paramPanel);
/*      */ 
/*      */   protected abstract WindowPeer createWindow(Window paramWindow)
/*      */     throws HeadlessException;
/*      */ 
/*      */   protected abstract DialogPeer createDialog(Dialog paramDialog)
/*      */     throws HeadlessException;
/*      */ 
/*      */   protected abstract MenuBarPeer createMenuBar(MenuBar paramMenuBar)
/*      */     throws HeadlessException;
/*      */ 
/*      */   protected abstract MenuPeer createMenu(Menu paramMenu)
/*      */     throws HeadlessException;
/*      */ 
/*      */   protected abstract PopupMenuPeer createPopupMenu(PopupMenu paramPopupMenu)
/*      */     throws HeadlessException;
/*      */ 
/*      */   protected abstract MenuItemPeer createMenuItem(MenuItem paramMenuItem)
/*      */     throws HeadlessException;
/*      */ 
/*      */   protected abstract FileDialogPeer createFileDialog(FileDialog paramFileDialog)
/*      */     throws HeadlessException;
/*      */ 
/*      */   protected abstract CheckboxMenuItemPeer createCheckboxMenuItem(CheckboxMenuItem paramCheckboxMenuItem)
/*      */     throws HeadlessException;
/*      */ 
/*      */   protected MouseInfoPeer getMouseInfoPeer()
/*      */   {
/*  420 */     throw new UnsupportedOperationException("Not implemented");
/*      */   }
/*      */ 
/*      */   protected LightweightPeer createComponent(Component paramComponent)
/*      */   {
/*  433 */     if (lightweightMarker == null) {
/*  434 */       lightweightMarker = new NullComponentPeer();
/*      */     }
/*  436 */     return lightweightMarker;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   protected abstract FontPeer getFontPeer(String paramString, int paramInt);
/*      */ 
/*      */   protected void loadSystemColors(int[] paramArrayOfInt)
/*      */     throws HeadlessException
/*      */   {
/*  469 */     GraphicsEnvironment.checkHeadless();
/*      */   }
/*      */ 
/*      */   public void setDynamicLayout(boolean paramBoolean)
/*      */     throws HeadlessException
/*      */   {
/*  504 */     GraphicsEnvironment.checkHeadless();
/*      */   }
/*      */ 
/*      */   protected boolean isDynamicLayoutSet()
/*      */     throws HeadlessException
/*      */   {
/*  528 */     GraphicsEnvironment.checkHeadless();
/*      */ 
/*  530 */     if (this != getDefaultToolkit()) {
/*  531 */       return getDefaultToolkit().isDynamicLayoutSet();
/*      */     }
/*  533 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isDynamicLayoutActive()
/*      */     throws HeadlessException
/*      */   {
/*  565 */     GraphicsEnvironment.checkHeadless();
/*      */ 
/*  567 */     if (this != getDefaultToolkit()) {
/*  568 */       return getDefaultToolkit().isDynamicLayoutActive();
/*      */     }
/*  570 */     return false;
/*      */   }
/*      */ 
/*      */   public abstract Dimension getScreenSize()
/*      */     throws HeadlessException;
/*      */ 
/*      */   public abstract int getScreenResolution()
/*      */     throws HeadlessException;
/*      */ 
/*      */   public Insets getScreenInsets(GraphicsConfiguration paramGraphicsConfiguration)
/*      */     throws HeadlessException
/*      */   {
/*  610 */     GraphicsEnvironment.checkHeadless();
/*  611 */     if (this != getDefaultToolkit()) {
/*  612 */       return getDefaultToolkit().getScreenInsets(paramGraphicsConfiguration);
/*      */     }
/*  614 */     return new Insets(0, 0, 0, 0);
/*      */   }
/*      */ 
/*      */   public abstract ColorModel getColorModel()
/*      */     throws HeadlessException;
/*      */ 
/*      */   @Deprecated
/*      */   public abstract String[] getFontList();
/*      */ 
/*      */   @Deprecated
/*      */   public abstract FontMetrics getFontMetrics(Font paramFont);
/*      */ 
/*      */   public abstract void sync();
/*      */ 
/*      */   private static void initAssistiveTechnologies()
/*      */   {
/*  705 */     String str = File.separator;
/*  706 */     final Properties localProperties = new Properties();
/*      */ 
/*  709 */     atNames = (String)AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public Object run()
/*      */       {
/*      */         try
/*      */         {
/*  715 */           File localFile1 = new File(System.getProperty("user.home") + this.val$sep + ".accessibility.properties");
/*      */ 
/*  718 */           localObject = new FileInputStream(localFile1);
/*      */ 
/*  722 */           localProperties.load((InputStream)localObject);
/*  723 */           ((FileInputStream)localObject).close();
/*      */         }
/*      */         catch (Exception localException1)
/*      */         {
/*      */         }
/*      */ 
/*  731 */         if (localProperties.size() == 0) {
/*      */           try {
/*  733 */             File localFile2 = new File(System.getProperty("java.home") + this.val$sep + "lib" + this.val$sep + "accessibility.properties");
/*      */ 
/*  736 */             localObject = new FileInputStream(localFile2);
/*      */ 
/*  740 */             localProperties.load((InputStream)localObject);
/*  741 */             ((FileInputStream)localObject).close();
/*      */           }
/*      */           catch (Exception localException2)
/*      */           {
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  750 */         String str = System.getProperty("javax.accessibility.screen_magnifier_present");
/*  751 */         if (str == null) {
/*  752 */           str = localProperties.getProperty("screen_magnifier_present", null);
/*  753 */           if (str != null) {
/*  754 */             System.setProperty("javax.accessibility.screen_magnifier_present", str);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  761 */         Object localObject = System.getProperty("javax.accessibility.assistive_technologies");
/*  762 */         if (localObject == null) {
/*  763 */           localObject = localProperties.getProperty("assistive_technologies", null);
/*  764 */           if (localObject != null) {
/*  765 */             System.setProperty("javax.accessibility.assistive_technologies", (String)localObject);
/*      */           }
/*      */         }
/*  768 */         return localObject;
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   private static void loadAssistiveTechnologies()
/*      */   {
/*  794 */     if (atNames != null) {
/*  795 */       ClassLoader localClassLoader = ClassLoader.getSystemClassLoader();
/*  796 */       StringTokenizer localStringTokenizer = new StringTokenizer(atNames, " ,");
/*      */ 
/*  798 */       while (localStringTokenizer.hasMoreTokens()) {
/*  799 */         String str = localStringTokenizer.nextToken();
/*      */         try
/*      */         {
/*      */           Class localClass;
/*  802 */           if (localClassLoader != null)
/*  803 */             localClass = localClassLoader.loadClass(str);
/*      */           else {
/*  805 */             localClass = Class.forName(str);
/*      */           }
/*  807 */           localClass.newInstance();
/*      */         } catch (ClassNotFoundException localClassNotFoundException) {
/*  809 */           throw new AWTError("Assistive Technology not found: " + str);
/*      */         }
/*      */         catch (InstantiationException localInstantiationException) {
/*  812 */           throw new AWTError("Could not instantiate Assistive Technology: " + str);
/*      */         }
/*      */         catch (IllegalAccessException localIllegalAccessException) {
/*  815 */           throw new AWTError("Could not access Assistive Technology: " + str);
/*      */         }
/*      */         catch (Exception localException) {
/*  818 */           throw new AWTError("Error trying to install Assistive Technology: " + str + " " + localException);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public static synchronized Toolkit getDefaultToolkit()
/*      */   {
/*  855 */     if (toolkit == null)
/*      */     {
/*      */       try
/*      */       {
/*  860 */         Compiler.disable();
/*      */ 
/*  862 */         AccessController.doPrivileged(new PrivilegedAction()
/*      */         {
/*      */           public Object run() {
/*  865 */             String str = null;
/*  866 */             Class localClass = null;
/*      */             try {
/*  868 */               str = System.getProperty("awt.toolkit");
/*      */               try {
/*  870 */                 localClass = Class.forName(str);
/*      */               } catch (ClassNotFoundException localClassNotFoundException1) {
/*  872 */                 ClassLoader localClassLoader = ClassLoader.getSystemClassLoader();
/*  873 */                 if (localClassLoader != null) {
/*      */                   try {
/*  875 */                     localClass = localClassLoader.loadClass(str);
/*      */                   } catch (ClassNotFoundException localClassNotFoundException2) {
/*  877 */                     throw new AWTError("Toolkit not found: " + str);
/*      */                   }
/*      */                 }
/*      */               }
/*  881 */               if (localClass != null) {
/*  882 */                 Toolkit.access$002((Toolkit)localClass.newInstance());
/*  883 */                 if (GraphicsEnvironment.isHeadless())
/*  884 */                   Toolkit.access$002(new HeadlessToolkit(Toolkit.toolkit));
/*      */               }
/*      */             }
/*      */             catch (InstantiationException localInstantiationException) {
/*  888 */               throw new AWTError("Could not instantiate Toolkit: " + str);
/*      */             } catch (IllegalAccessException localIllegalAccessException) {
/*  890 */               throw new AWTError("Could not access Toolkit: " + str);
/*      */             }
/*  892 */             return null;
/*      */           }
/*      */         });
/*  895 */         loadAssistiveTechnologies();
/*      */       }
/*      */       finally {
/*  898 */         Compiler.enable();
/*      */       }
/*      */     }
/*  901 */     return toolkit;
/*      */   }
/*      */ 
/*      */   public abstract Image getImage(String paramString);
/*      */ 
/*      */   public abstract Image getImage(URL paramURL);
/*      */ 
/*      */   public abstract Image createImage(String paramString);
/*      */ 
/*      */   public abstract Image createImage(URL paramURL);
/*      */ 
/*      */   public abstract boolean prepareImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver);
/*      */ 
/*      */   public abstract int checkImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver);
/*      */ 
/*      */   public abstract Image createImage(ImageProducer paramImageProducer);
/*      */ 
/*      */   public Image createImage(byte[] paramArrayOfByte)
/*      */   {
/* 1124 */     return createImage(paramArrayOfByte, 0, paramArrayOfByte.length);
/*      */   }
/*      */ 
/*      */   public abstract Image createImage(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
/*      */ 
/*      */   public abstract PrintJob getPrintJob(Frame paramFrame, String paramString, Properties paramProperties);
/*      */ 
/*      */   public PrintJob getPrintJob(Frame paramFrame, String paramString, JobAttributes paramJobAttributes, PageAttributes paramPageAttributes)
/*      */   {
/* 1234 */     if (this != getDefaultToolkit()) {
/* 1235 */       return getDefaultToolkit().getPrintJob(paramFrame, paramString, paramJobAttributes, paramPageAttributes);
/*      */     }
/*      */ 
/* 1239 */     return getPrintJob(paramFrame, paramString, null);
/*      */   }
/*      */ 
/*      */   public abstract void beep();
/*      */ 
/*      */   public abstract Clipboard getSystemClipboard()
/*      */     throws HeadlessException;
/*      */ 
/*      */   public Clipboard getSystemSelection()
/*      */     throws HeadlessException
/*      */   {
/* 1349 */     GraphicsEnvironment.checkHeadless();
/*      */ 
/* 1351 */     if (this != getDefaultToolkit()) {
/* 1352 */       return getDefaultToolkit().getSystemSelection();
/*      */     }
/* 1354 */     GraphicsEnvironment.checkHeadless();
/* 1355 */     return null;
/*      */   }
/*      */ 
/*      */   public int getMenuShortcutKeyMask()
/*      */     throws HeadlessException
/*      */   {
/* 1380 */     GraphicsEnvironment.checkHeadless();
/*      */ 
/* 1382 */     return 2;
/*      */   }
/*      */ 
/*      */   public boolean getLockingKeyState(int paramInt)
/*      */     throws UnsupportedOperationException
/*      */   {
/* 1407 */     GraphicsEnvironment.checkHeadless();
/*      */ 
/* 1409 */     if ((paramInt != 20) && (paramInt != 144) && (paramInt != 145) && (paramInt != 262))
/*      */     {
/* 1411 */       throw new IllegalArgumentException("invalid key for Toolkit.getLockingKeyState");
/*      */     }
/* 1413 */     throw new UnsupportedOperationException("Toolkit.getLockingKeyState");
/*      */   }
/*      */ 
/*      */   public void setLockingKeyState(int paramInt, boolean paramBoolean)
/*      */     throws UnsupportedOperationException
/*      */   {
/* 1441 */     GraphicsEnvironment.checkHeadless();
/*      */ 
/* 1443 */     if ((paramInt != 20) && (paramInt != 144) && (paramInt != 145) && (paramInt != 262))
/*      */     {
/* 1445 */       throw new IllegalArgumentException("invalid key for Toolkit.setLockingKeyState");
/*      */     }
/* 1447 */     throw new UnsupportedOperationException("Toolkit.setLockingKeyState");
/*      */   }
/*      */ 
/*      */   protected static Container getNativeContainer(Component paramComponent)
/*      */   {
/* 1455 */     return paramComponent.getNativeContainer();
/*      */   }
/*      */ 
/*      */   public Cursor createCustomCursor(Image paramImage, Point paramPoint, String paramString)
/*      */     throws IndexOutOfBoundsException, HeadlessException
/*      */   {
/* 1482 */     if (this != getDefaultToolkit()) {
/* 1483 */       return getDefaultToolkit().createCustomCursor(paramImage, paramPoint, paramString);
/*      */     }
/*      */ 
/* 1486 */     return new Cursor(0);
/*      */   }
/*      */ 
/*      */   public Dimension getBestCursorSize(int paramInt1, int paramInt2)
/*      */     throws HeadlessException
/*      */   {
/* 1516 */     GraphicsEnvironment.checkHeadless();
/*      */ 
/* 1519 */     if (this != getDefaultToolkit()) {
/* 1520 */       return getDefaultToolkit().getBestCursorSize(paramInt1, paramInt2);
/*      */     }
/*      */ 
/* 1523 */     return new Dimension(0, 0);
/*      */   }
/*      */ 
/*      */   public int getMaximumCursorColors()
/*      */     throws HeadlessException
/*      */   {
/* 1545 */     GraphicsEnvironment.checkHeadless();
/*      */ 
/* 1548 */     if (this != getDefaultToolkit()) {
/* 1549 */       return getDefaultToolkit().getMaximumCursorColors();
/*      */     }
/* 1551 */     return 0;
/*      */   }
/*      */ 
/*      */   public boolean isFrameStateSupported(int paramInt)
/*      */     throws HeadlessException
/*      */   {
/* 1596 */     GraphicsEnvironment.checkHeadless();
/*      */ 
/* 1598 */     if (this != getDefaultToolkit()) {
/* 1599 */       return getDefaultToolkit().isFrameStateSupported(paramInt);
/*      */     }
/*      */ 
/* 1602 */     return paramInt == 0;
/*      */   }
/*      */ 
/*      */   private static native void initIDs();
/*      */ 
/*      */   static void loadLibraries()
/*      */   {
/* 1647 */     if (!loaded) {
/* 1648 */       AccessController.doPrivileged(new LoadLibraryAction("awt"));
/*      */ 
/* 1650 */       loaded = true;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static String getProperty(String paramString1, String paramString2)
/*      */   {
/* 1682 */     if (resources != null)
/*      */       try {
/* 1684 */         return resources.getString(paramString1);
/*      */       }
/*      */       catch (MissingResourceException localMissingResourceException)
/*      */       {
/*      */       }
/* 1689 */     return paramString2;
/*      */   }
/*      */ 
/*      */   public final EventQueue getSystemEventQueue()
/*      */   {
/* 1715 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 1716 */     if (localSecurityManager != null) {
/* 1717 */       localSecurityManager.checkAwtEventQueueAccess();
/*      */     }
/* 1719 */     return getSystemEventQueueImpl();
/*      */   }
/*      */ 
/*      */   protected abstract EventQueue getSystemEventQueueImpl();
/*      */ 
/*      */   static EventQueue getEventQueue()
/*      */   {
/* 1732 */     return getDefaultToolkit().getSystemEventQueueImpl();
/*      */   }
/*      */ 
/*      */   public abstract DragSourceContextPeer createDragSourceContextPeer(DragGestureEvent paramDragGestureEvent)
/*      */     throws InvalidDnDOperationException;
/*      */ 
/*      */   public <T extends DragGestureRecognizer> T createDragGestureRecognizer(Class<T> paramClass, DragSource paramDragSource, Component paramComponent, int paramInt, DragGestureListener paramDragGestureListener)
/*      */   {
/* 1765 */     return null;
/*      */   }
/*      */ 
/*      */   public final synchronized Object getDesktopProperty(String paramString)
/*      */   {
/* 1782 */     if ((this instanceof HeadlessToolkit)) {
/* 1783 */       return ((HeadlessToolkit)this).getUnderlyingToolkit().getDesktopProperty(paramString);
/*      */     }
/*      */ 
/* 1787 */     if (this.desktopProperties.isEmpty()) {
/* 1788 */       initializeDesktopProperties();
/*      */     }
/*      */ 
/* 1794 */     if (paramString.equals("awt.dynamicLayoutSupported")) {
/* 1795 */       localObject = lazilyLoadDesktopProperty(paramString);
/* 1796 */       return localObject;
/*      */     }
/*      */ 
/* 1799 */     Object localObject = this.desktopProperties.get(paramString);
/*      */ 
/* 1801 */     if (localObject == null) {
/* 1802 */       localObject = lazilyLoadDesktopProperty(paramString);
/*      */ 
/* 1804 */       if (localObject != null) {
/* 1805 */         setDesktopProperty(paramString, localObject);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1810 */     if ((localObject instanceof RenderingHints)) {
/* 1811 */       localObject = ((RenderingHints)localObject).clone();
/*      */     }
/*      */ 
/* 1814 */     return localObject;
/*      */   }
/*      */ 
/*      */   protected final void setDesktopProperty(String paramString, Object paramObject)
/*      */   {
/* 1826 */     if ((this instanceof HeadlessToolkit)) {
/* 1827 */       ((HeadlessToolkit)this).getUnderlyingToolkit().setDesktopProperty(paramString, paramObject);
/*      */       return;
/*      */     }
/*      */     Object localObject1;
/* 1833 */     synchronized (this) {
/* 1834 */       localObject1 = this.desktopProperties.get(paramString);
/* 1835 */       this.desktopProperties.put(paramString, paramObject);
/*      */     }
/*      */ 
/* 1840 */     if ((localObject1 != null) || (paramObject != null))
/* 1841 */       this.desktopPropsSupport.firePropertyChange(paramString, localObject1, paramObject);
/*      */   }
/*      */ 
/*      */   protected Object lazilyLoadDesktopProperty(String paramString)
/*      */   {
/* 1849 */     return null;
/*      */   }
/*      */ 
/*      */   protected void initializeDesktopProperties()
/*      */   {
/*      */   }
/*      */ 
/*      */   public void addPropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener)
/*      */   {
/* 1872 */     this.desktopPropsSupport.addPropertyChangeListener(paramString, paramPropertyChangeListener);
/*      */   }
/*      */ 
/*      */   public void removePropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener)
/*      */   {
/* 1890 */     this.desktopPropsSupport.removePropertyChangeListener(paramString, paramPropertyChangeListener);
/*      */   }
/*      */ 
/*      */   public PropertyChangeListener[] getPropertyChangeListeners()
/*      */   {
/* 1907 */     return this.desktopPropsSupport.getPropertyChangeListeners();
/*      */   }
/*      */ 
/*      */   public PropertyChangeListener[] getPropertyChangeListeners(String paramString)
/*      */   {
/* 1923 */     return this.desktopPropsSupport.getPropertyChangeListeners(paramString);
/*      */   }
/*      */ 
/*      */   public boolean isAlwaysOnTopSupported()
/*      */   {
/* 1942 */     return true;
/*      */   }
/*      */ 
/*      */   public abstract boolean isModalityTypeSupported(Dialog.ModalityType paramModalityType);
/*      */ 
/*      */   public abstract boolean isModalExclusionTypeSupported(Dialog.ModalExclusionType paramModalExclusionType);
/*      */ 
/*      */   private static AWTEventListener deProxyAWTEventListener(AWTEventListener paramAWTEventListener)
/*      */   {
/* 1995 */     AWTEventListener localAWTEventListener = paramAWTEventListener;
/*      */ 
/* 1997 */     if (localAWTEventListener == null) {
/* 1998 */       return null;
/*      */     }
/*      */ 
/* 2002 */     if ((paramAWTEventListener instanceof AWTEventListenerProxy)) {
/* 2003 */       localAWTEventListener = (AWTEventListener)((AWTEventListenerProxy)paramAWTEventListener).getListener();
/*      */     }
/* 2005 */     return localAWTEventListener;
/*      */   }
/*      */ 
/*      */   public void addAWTEventListener(AWTEventListener paramAWTEventListener, long paramLong)
/*      */   {
/* 2043 */     AWTEventListener localAWTEventListener = deProxyAWTEventListener(paramAWTEventListener);
/*      */ 
/* 2045 */     if (localAWTEventListener == null) {
/* 2046 */       return;
/*      */     }
/* 2048 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 2049 */     if (localSecurityManager != null) {
/* 2050 */       localSecurityManager.checkPermission(SecurityConstants.AWT.ALL_AWT_EVENTS_PERMISSION);
/*      */     }
/* 2052 */     synchronized (this) {
/* 2053 */       SelectiveAWTEventListener localSelectiveAWTEventListener = (SelectiveAWTEventListener)this.listener2SelectiveListener.get(localAWTEventListener);
/*      */ 
/* 2056 */       if (localSelectiveAWTEventListener == null)
/*      */       {
/* 2058 */         localSelectiveAWTEventListener = new SelectiveAWTEventListener(localAWTEventListener, paramLong);
/*      */ 
/* 2060 */         this.listener2SelectiveListener.put(localAWTEventListener, localSelectiveAWTEventListener);
/* 2061 */         this.eventListener = ToolkitEventMulticaster.add(this.eventListener, localSelectiveAWTEventListener);
/*      */       }
/*      */ 
/* 2065 */       localSelectiveAWTEventListener.orEventMasks(paramLong);
/*      */ 
/* 2067 */       enabledOnToolkitMask |= paramLong;
/*      */ 
/* 2069 */       long l = paramLong;
/* 2070 */       for (int i = 0; i < 64; i++)
/*      */       {
/* 2072 */         if (l == 0L) {
/*      */           break;
/*      */         }
/* 2075 */         if ((l & 1L) != 0L) {
/* 2076 */           this.calls[i] += 1;
/*      */         }
/* 2078 */         l >>>= 1;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void removeAWTEventListener(AWTEventListener paramAWTEventListener)
/*      */   {
/* 2112 */     AWTEventListener localAWTEventListener = deProxyAWTEventListener(paramAWTEventListener);
/*      */ 
/* 2114 */     if (paramAWTEventListener == null) {
/* 2115 */       return;
/*      */     }
/* 2117 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 2118 */     if (localSecurityManager != null) {
/* 2119 */       localSecurityManager.checkPermission(SecurityConstants.AWT.ALL_AWT_EVENTS_PERMISSION);
/*      */     }
/*      */ 
/* 2122 */     synchronized (this) {
/* 2123 */       SelectiveAWTEventListener localSelectiveAWTEventListener = (SelectiveAWTEventListener)this.listener2SelectiveListener.get(localAWTEventListener);
/*      */ 
/* 2126 */       if (localSelectiveAWTEventListener != null) {
/* 2127 */         this.listener2SelectiveListener.remove(localAWTEventListener);
/* 2128 */         int[] arrayOfInt = localSelectiveAWTEventListener.getCalls();
/* 2129 */         for (int i = 0; i < 64; i++) {
/* 2130 */           this.calls[i] -= arrayOfInt[i];
/* 2131 */           assert (this.calls[i] >= 0) : "Negative Listeners count";
/*      */ 
/* 2133 */           if (this.calls[i] == 0) {
/* 2134 */             enabledOnToolkitMask &= (1L << i ^ 0xFFFFFFFF);
/*      */           }
/*      */         }
/*      */       }
/* 2138 */       this.eventListener = ToolkitEventMulticaster.remove(this.eventListener, localSelectiveAWTEventListener == null ? localAWTEventListener : localSelectiveAWTEventListener);
/*      */     }
/*      */   }
/*      */ 
/*      */   static boolean enabledOnToolkit(long paramLong)
/*      */   {
/* 2144 */     return (enabledOnToolkitMask & paramLong) != 0L;
/*      */   }
/*      */ 
/*      */   synchronized int countAWTEventListeners(long paramLong) {
/* 2148 */     if ((log.isLoggable(500)) && 
/* 2149 */       (paramLong == 0L)) {
/* 2150 */       log.fine("Assertion (eventMask != 0) failed");
/*      */     }
/*      */ 
/* 2154 */     for (int i = 0; 
/* 2155 */       paramLong != 0L; i++) paramLong >>>= 1;
/*      */ 
/* 2157 */     i--;
/* 2158 */     return this.calls[i];
/*      */   }
/*      */ 
/*      */   public AWTEventListener[] getAWTEventListeners()
/*      */   {
/* 2188 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 2189 */     if (localSecurityManager != null) {
/* 2190 */       localSecurityManager.checkPermission(SecurityConstants.AWT.ALL_AWT_EVENTS_PERMISSION);
/*      */     }
/* 2192 */     synchronized (this) {
/* 2193 */       EventListener[] arrayOfEventListener = ToolkitEventMulticaster.getListeners(this.eventListener, AWTEventListener.class);
/*      */ 
/* 2195 */       AWTEventListener[] arrayOfAWTEventListener = new AWTEventListener[arrayOfEventListener.length];
/* 2196 */       for (int i = 0; i < arrayOfEventListener.length; i++) {
/* 2197 */         SelectiveAWTEventListener localSelectiveAWTEventListener = (SelectiveAWTEventListener)arrayOfEventListener[i];
/* 2198 */         AWTEventListener localAWTEventListener = localSelectiveAWTEventListener.getListener();
/*      */ 
/* 2202 */         arrayOfAWTEventListener[i] = new AWTEventListenerProxy(localSelectiveAWTEventListener.getEventMask(), localAWTEventListener);
/*      */       }
/* 2204 */       return arrayOfAWTEventListener;
/*      */     }
/*      */   }
/*      */ 
/*      */   public AWTEventListener[] getAWTEventListeners(long paramLong)
/*      */   {
/* 2240 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 2241 */     if (localSecurityManager != null) {
/* 2242 */       localSecurityManager.checkPermission(SecurityConstants.AWT.ALL_AWT_EVENTS_PERMISSION);
/*      */     }
/* 2244 */     synchronized (this) {
/* 2245 */       EventListener[] arrayOfEventListener = ToolkitEventMulticaster.getListeners(this.eventListener, AWTEventListener.class);
/*      */ 
/* 2247 */       ArrayList localArrayList = new ArrayList(arrayOfEventListener.length);
/*      */ 
/* 2249 */       for (int i = 0; i < arrayOfEventListener.length; i++) {
/* 2250 */         SelectiveAWTEventListener localSelectiveAWTEventListener = (SelectiveAWTEventListener)arrayOfEventListener[i];
/* 2251 */         if ((localSelectiveAWTEventListener.getEventMask() & paramLong) == paramLong)
/*      */         {
/* 2253 */           localArrayList.add(new AWTEventListenerProxy(localSelectiveAWTEventListener.getEventMask(), localSelectiveAWTEventListener.getListener()));
/*      */         }
/*      */       }
/*      */ 
/* 2257 */       return (AWTEventListener[])localArrayList.toArray(new AWTEventListener[0]);
/*      */     }
/*      */   }
/*      */ 
/*      */   void notifyAWTEventListeners(AWTEvent paramAWTEvent)
/*      */   {
/* 2272 */     if ((this instanceof HeadlessToolkit)) {
/* 2273 */       ((HeadlessToolkit)this).getUnderlyingToolkit().notifyAWTEventListeners(paramAWTEvent);
/*      */ 
/* 2275 */       return;
/*      */     }
/*      */ 
/* 2278 */     AWTEventListener localAWTEventListener = this.eventListener;
/* 2279 */     if (localAWTEventListener != null)
/* 2280 */       localAWTEventListener.eventDispatched(paramAWTEvent);
/*      */   }
/*      */ 
/*      */   public abstract Map<TextAttribute, ?> mapInputMethodHighlight(InputMethodHighlight paramInputMethodHighlight)
/*      */     throws HeadlessException;
/*      */ 
/*      */   private static PropertyChangeSupport createPropertyChangeSupport(Toolkit paramToolkit)
/*      */   {
/* 2453 */     if (((paramToolkit instanceof SunToolkit)) || ((paramToolkit instanceof HeadlessToolkit))) {
/* 2454 */       return new DesktopPropertyChangeSupport(paramToolkit);
/*      */     }
/* 2456 */     return new PropertyChangeSupport(paramToolkit);
/*      */   }
/*      */ 
/*      */   public boolean areExtraMouseButtonsEnabled()
/*      */     throws HeadlessException
/*      */   {
/* 2604 */     GraphicsEnvironment.checkHeadless();
/*      */ 
/* 2606 */     return getDefaultToolkit().areExtraMouseButtonsEnabled();
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/* 1645 */     loaded = false;
/*      */ 
/* 1655 */     AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public Object run() {
/*      */         try {
/* 1659 */           Toolkit.access$102(ResourceBundle.getBundle("sun.awt.resources.awt", CoreResourceBundleControl.getRBControlInstance()));
/*      */         }
/*      */         catch (MissingResourceException localMissingResourceException)
/*      */         {
/*      */         }
/*      */ 
/* 1665 */         return null;
/*      */       }
/*      */     });
/* 1670 */     loadLibraries();
/* 1671 */     initAssistiveTechnologies();
/* 1672 */     if (!GraphicsEnvironment.isHeadless())
/* 1673 */       initIDs();
/*      */   }
/*      */ 
/*      */   private static class DesktopPropertyChangeSupport extends PropertyChangeSupport
/*      */   {
/* 2461 */     private static final StringBuilder PROP_CHANGE_SUPPORT_KEY = new StringBuilder("desktop property change support key");
/*      */     private final Object source;
/*      */ 
/*      */     public DesktopPropertyChangeSupport(Object paramObject)
/*      */     {
/* 2466 */       super();
/* 2467 */       this.source = paramObject;
/*      */     }
/*      */ 
/*      */     public synchronized void addPropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener)
/*      */     {
/* 2475 */       PropertyChangeSupport localPropertyChangeSupport = (PropertyChangeSupport)AppContext.getAppContext().get(PROP_CHANGE_SUPPORT_KEY);
/*      */ 
/* 2477 */       if (null == localPropertyChangeSupport) {
/* 2478 */         localPropertyChangeSupport = new PropertyChangeSupport(this.source);
/* 2479 */         AppContext.getAppContext().put(PROP_CHANGE_SUPPORT_KEY, localPropertyChangeSupport);
/*      */       }
/* 2481 */       localPropertyChangeSupport.addPropertyChangeListener(paramString, paramPropertyChangeListener);
/*      */     }
/*      */ 
/*      */     public synchronized void removePropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener)
/*      */     {
/* 2489 */       PropertyChangeSupport localPropertyChangeSupport = (PropertyChangeSupport)AppContext.getAppContext().get(PROP_CHANGE_SUPPORT_KEY);
/*      */ 
/* 2491 */       if (null != localPropertyChangeSupport)
/* 2492 */         localPropertyChangeSupport.removePropertyChangeListener(paramString, paramPropertyChangeListener);
/*      */     }
/*      */ 
/*      */     public synchronized PropertyChangeListener[] getPropertyChangeListeners()
/*      */     {
/* 2499 */       PropertyChangeSupport localPropertyChangeSupport = (PropertyChangeSupport)AppContext.getAppContext().get(PROP_CHANGE_SUPPORT_KEY);
/*      */ 
/* 2501 */       if (null != localPropertyChangeSupport) {
/* 2502 */         return localPropertyChangeSupport.getPropertyChangeListeners();
/*      */       }
/* 2504 */       return new PropertyChangeListener[0];
/*      */     }
/*      */ 
/*      */     public synchronized PropertyChangeListener[] getPropertyChangeListeners(String paramString)
/*      */     {
/* 2511 */       PropertyChangeSupport localPropertyChangeSupport = (PropertyChangeSupport)AppContext.getAppContext().get(PROP_CHANGE_SUPPORT_KEY);
/*      */ 
/* 2513 */       if (null != localPropertyChangeSupport) {
/* 2514 */         return localPropertyChangeSupport.getPropertyChangeListeners(paramString);
/*      */       }
/* 2516 */       return new PropertyChangeListener[0];
/*      */     }
/*      */ 
/*      */     public synchronized void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
/*      */     {
/* 2522 */       PropertyChangeSupport localPropertyChangeSupport = (PropertyChangeSupport)AppContext.getAppContext().get(PROP_CHANGE_SUPPORT_KEY);
/*      */ 
/* 2524 */       if (null == localPropertyChangeSupport) {
/* 2525 */         localPropertyChangeSupport = new PropertyChangeSupport(this.source);
/* 2526 */         AppContext.getAppContext().put(PROP_CHANGE_SUPPORT_KEY, localPropertyChangeSupport);
/*      */       }
/* 2528 */       localPropertyChangeSupport.addPropertyChangeListener(paramPropertyChangeListener);
/*      */     }
/*      */ 
/*      */     public synchronized void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
/*      */     {
/* 2533 */       PropertyChangeSupport localPropertyChangeSupport = (PropertyChangeSupport)AppContext.getAppContext().get(PROP_CHANGE_SUPPORT_KEY);
/*      */ 
/* 2535 */       if (null != localPropertyChangeSupport)
/* 2536 */         localPropertyChangeSupport.removePropertyChangeListener(paramPropertyChangeListener);
/*      */     }
/*      */ 
/*      */     public void firePropertyChange(final PropertyChangeEvent paramPropertyChangeEvent)
/*      */     {
/* 2546 */       Object localObject1 = paramPropertyChangeEvent.getOldValue();
/* 2547 */       Object localObject2 = paramPropertyChangeEvent.getNewValue();
/* 2548 */       String str = paramPropertyChangeEvent.getPropertyName();
/* 2549 */       if ((localObject1 != null) && (localObject2 != null) && (localObject1.equals(localObject2))) {
/* 2550 */         return;
/*      */       }
/* 2552 */       Runnable local1 = new Runnable() {
/*      */         public void run() {
/* 2554 */           PropertyChangeSupport localPropertyChangeSupport = (PropertyChangeSupport)AppContext.getAppContext().get(Toolkit.DesktopPropertyChangeSupport.PROP_CHANGE_SUPPORT_KEY);
/*      */ 
/* 2556 */           if (null != localPropertyChangeSupport)
/* 2557 */             localPropertyChangeSupport.firePropertyChange(paramPropertyChangeEvent);
/*      */         }
/*      */       };
/* 2561 */       AppContext localAppContext1 = AppContext.getAppContext();
/* 2562 */       for (AppContext localAppContext2 : AppContext.getAppContexts())
/* 2563 */         if ((null != localAppContext2) && (!localAppContext2.isDisposed()))
/*      */         {
/* 2566 */           if (localAppContext1 == localAppContext2) {
/* 2567 */             local1.run();
/*      */           } else {
/* 2569 */             PeerEvent localPeerEvent = new PeerEvent(this.source, local1, 2L);
/* 2570 */             SunToolkit.postEvent(localAppContext2, localPeerEvent);
/*      */           }
/*      */         }
/*      */     }
/*      */   }
/*      */ 
/*      */   private class SelectiveAWTEventListener
/*      */     implements AWTEventListener
/*      */   {
/*      */     AWTEventListener listener;
/*      */     private long eventMask;
/* 2331 */     int[] calls = new int[64];
/*      */ 
/* 2333 */     public AWTEventListener getListener() { return this.listener; } 
/* 2334 */     public long getEventMask() { return this.eventMask; } 
/* 2335 */     public int[] getCalls() { return this.calls; }
/*      */ 
/*      */     public void orEventMasks(long paramLong) {
/* 2338 */       this.eventMask |= paramLong;
/*      */ 
/* 2340 */       for (int i = 0; i < 64; i++)
/*      */       {
/* 2342 */         if (paramLong == 0L) {
/*      */           break;
/*      */         }
/* 2345 */         if ((paramLong & 1L) != 0L) {
/* 2346 */           this.calls[i] += 1;
/*      */         }
/* 2348 */         paramLong >>>= 1;
/*      */       }
/*      */     }
/*      */ 
/*      */     SelectiveAWTEventListener(AWTEventListener paramLong, long arg3) {
/* 2353 */       this.listener = paramLong;
/*      */       Object localObject;
/* 2354 */       this.eventMask = localObject;
/*      */     }
/*      */ 
/*      */     public void eventDispatched(AWTEvent paramAWTEvent) {
/* 2358 */       long l1 = 0L;
/* 2359 */       if ((((l1 = this.eventMask & 1L) != 0L) && (paramAWTEvent.id >= 100) && (paramAWTEvent.id <= 103)) || (((l1 = this.eventMask & 0x2) != 0L) && (paramAWTEvent.id >= 300) && (paramAWTEvent.id <= 301)) || (((l1 = this.eventMask & 0x4) != 0L) && (paramAWTEvent.id >= 1004) && (paramAWTEvent.id <= 1005)) || (((l1 = this.eventMask & 0x8) != 0L) && (paramAWTEvent.id >= 400) && (paramAWTEvent.id <= 402)) || (((l1 = this.eventMask & 0x20000) != 0L) && (paramAWTEvent.id == 507)) || (((l1 = this.eventMask & 0x20) != 0L) && ((paramAWTEvent.id == 503) || (paramAWTEvent.id == 506))) || (((l1 = this.eventMask & 0x10) != 0L) && (paramAWTEvent.id != 503) && (paramAWTEvent.id != 506) && (paramAWTEvent.id != 507) && (paramAWTEvent.id >= 500) && (paramAWTEvent.id <= 507)) || (((l1 = this.eventMask & 0x40) != 0L) && (paramAWTEvent.id >= 200) && (paramAWTEvent.id <= 209)) || (((l1 = this.eventMask & 0x80) != 0L) && (paramAWTEvent.id >= 1001) && (paramAWTEvent.id <= 1001)) || (((l1 = this.eventMask & 0x100) != 0L) && (paramAWTEvent.id >= 601) && (paramAWTEvent.id <= 601)) || (((l1 = this.eventMask & 0x200) != 0L) && (paramAWTEvent.id >= 701) && (paramAWTEvent.id <= 701)) || (((l1 = this.eventMask & 0x400) != 0L) && (paramAWTEvent.id >= 900) && (paramAWTEvent.id <= 900)) || (((l1 = this.eventMask & 0x800) != 0L) && (paramAWTEvent.id >= 1100) && (paramAWTEvent.id <= 1101)) || (((l1 = this.eventMask & 0x2000) != 0L) && (paramAWTEvent.id >= 800) && (paramAWTEvent.id <= 801)) || (((l1 = this.eventMask & 0x4000) != 0L) && (paramAWTEvent.id >= 1200) && (paramAWTEvent.id <= 1200)) || (((l1 = this.eventMask & 0x8000) != 0L) && (paramAWTEvent.id == 1400)) || (((l1 = this.eventMask & 0x10000) != 0L) && ((paramAWTEvent.id == 1401) || (paramAWTEvent.id == 1402))) || (((l1 = this.eventMask & 0x40000) != 0L) && (paramAWTEvent.id == 209)) || (((l1 = this.eventMask & 0x80000) != 0L) && ((paramAWTEvent.id == 207) || (paramAWTEvent.id == 208))) || (((l1 = this.eventMask & 0x80000000) != 0L) && ((paramAWTEvent instanceof UngrabEvent))))
/*      */       {
/* 2423 */         int i = 0;
/* 2424 */         for (long l2 = l1; l2 != 0L; i++) l2 >>>= 1;
/*      */ 
/* 2426 */         i--;
/*      */ 
/* 2429 */         for (int j = 0; j < this.calls[i]; j++)
/* 2430 */           this.listener.eventDispatched(paramAWTEvent);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class ToolkitEventMulticaster extends AWTEventMulticaster
/*      */     implements AWTEventListener
/*      */   {
/*      */     ToolkitEventMulticaster(AWTEventListener paramAWTEventListener1, AWTEventListener paramAWTEventListener2)
/*      */     {
/* 2289 */       super(paramAWTEventListener2);
/*      */     }
/*      */ 
/*      */     static AWTEventListener add(AWTEventListener paramAWTEventListener1, AWTEventListener paramAWTEventListener2)
/*      */     {
/* 2294 */       if (paramAWTEventListener1 == null) return paramAWTEventListener2;
/* 2295 */       if (paramAWTEventListener2 == null) return paramAWTEventListener1;
/* 2296 */       return new ToolkitEventMulticaster(paramAWTEventListener1, paramAWTEventListener2);
/*      */     }
/*      */ 
/*      */     static AWTEventListener remove(AWTEventListener paramAWTEventListener1, AWTEventListener paramAWTEventListener2)
/*      */     {
/* 2301 */       return (AWTEventListener)removeInternal(paramAWTEventListener1, paramAWTEventListener2);
/*      */     }
/*      */ 
/*      */     protected EventListener remove(EventListener paramEventListener)
/*      */     {
/* 2310 */       if (paramEventListener == this.a) return this.b;
/* 2311 */       if (paramEventListener == this.b) return this.a;
/* 2312 */       AWTEventListener localAWTEventListener1 = (AWTEventListener)removeInternal(this.a, paramEventListener);
/* 2313 */       AWTEventListener localAWTEventListener2 = (AWTEventListener)removeInternal(this.b, paramEventListener);
/* 2314 */       if ((localAWTEventListener1 == this.a) && (localAWTEventListener2 == this.b)) {
/* 2315 */         return this;
/*      */       }
/* 2317 */       return add(localAWTEventListener1, localAWTEventListener2);
/*      */     }
/*      */ 
/*      */     public void eventDispatched(AWTEvent paramAWTEvent) {
/* 2321 */       ((AWTEventListener)this.a).eventDispatched(paramAWTEvent);
/* 2322 */       ((AWTEventListener)this.b).eventDispatched(paramAWTEvent);
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.Toolkit
 * JD-Core Version:    0.6.2
 */