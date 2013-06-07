/*      */ package java.awt;
/*      */ 
/*      */ import java.awt.event.FocusEvent;
/*      */ import java.awt.event.KeyEvent;
/*      */ import java.awt.event.WindowEvent;
/*      */ import java.awt.peer.KeyboardFocusManagerPeer;
/*      */ import java.awt.peer.LightweightPeer;
/*      */ import java.beans.PropertyChangeListener;
/*      */ import java.beans.PropertyChangeSupport;
/*      */ import java.beans.PropertyVetoException;
/*      */ import java.beans.VetoableChangeListener;
/*      */ import java.beans.VetoableChangeSupport;
/*      */ import java.io.PrintStream;
/*      */ import java.lang.ref.WeakReference;
/*      */ import java.lang.reflect.Field;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.util.Collections;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.WeakHashMap;
/*      */ import sun.awt.AWTAccessor;
/*      */ import sun.awt.AWTAccessor.KeyboardFocusManagerAccessor;
/*      */ import sun.awt.AppContext;
/*      */ import sun.awt.CausedFocusEvent;
/*      */ import sun.awt.CausedFocusEvent.Cause;
/*      */ import sun.awt.KeyboardFocusManagerPeerProvider;
/*      */ import sun.awt.SunToolkit;
/*      */ import sun.util.logging.PlatformLogger;
/*      */ 
/*      */ public abstract class KeyboardFocusManager
/*      */   implements KeyEventDispatcher, KeyEventPostProcessor
/*      */ {
/*      */   private static final PlatformLogger focusLog;
/*      */   transient KeyboardFocusManagerPeer peer;
/*  164 */   private static final PlatformLogger log = PlatformLogger.getLogger("java.awt.KeyboardFocusManager");
/*      */   public static final int FORWARD_TRAVERSAL_KEYS = 0;
/*      */   public static final int BACKWARD_TRAVERSAL_KEYS = 1;
/*      */   public static final int UP_CYCLE_TRAVERSAL_KEYS = 2;
/*      */   public static final int DOWN_CYCLE_TRAVERSAL_KEYS = 3;
/*      */   static final int TRAVERSAL_KEY_LENGTH = 4;
/*      */   private static Component focusOwner;
/*      */   private static Component permanentFocusOwner;
/*      */   private static Window focusedWindow;
/*      */   private static Window activeWindow;
/*  322 */   private FocusTraversalPolicy defaultPolicy = new DefaultFocusTraversalPolicy();
/*      */ 
/*  328 */   private static final String[] defaultFocusTraversalKeyPropertyNames = { "forwardDefaultFocusTraversalKeys", "backwardDefaultFocusTraversalKeys", "upCycleDefaultFocusTraversalKeys", "downCycleDefaultFocusTraversalKeys" };
/*      */ 
/*  338 */   private static final AWTKeyStroke[][] defaultFocusTraversalKeyStrokes = { { AWTKeyStroke.getAWTKeyStroke(9, 0, false), AWTKeyStroke.getAWTKeyStroke(9, 130, false) }, { AWTKeyStroke.getAWTKeyStroke(9, 65, false), AWTKeyStroke.getAWTKeyStroke(9, 195, false) }, new AWTKeyStroke[0], new AWTKeyStroke[0] };
/*      */ 
/*  359 */   private Set[] defaultFocusTraversalKeys = new Set[4];
/*      */   private static Container currentFocusCycleRoot;
/*      */   private VetoableChangeSupport vetoableSupport;
/*      */   private PropertyChangeSupport changeSupport;
/*      */   private LinkedList keyEventDispatchers;
/*      */   private LinkedList keyEventPostProcessors;
/*  401 */   private static Map mostRecentFocusOwners = new WeakHashMap();
/*      */   private static final String notPrivileged = "this KeyboardFocusManager is not installed in the current thread's context";
/*      */   private static AWTPermission replaceKeyboardFocusManagerPermission;
/*  417 */   transient SequencedEvent currentSequencedEvent = null;
/*      */ 
/* 2211 */   private static LinkedList<HeavyweightFocusRequest> heavyweightRequests = new LinkedList();
/*      */   private static LinkedList<LightweightFocusRequest> currentLightweightRequests;
/*      */   private static boolean clearingCurrentLightweightRequests;
/* 2215 */   private static boolean allowSyncFocusRequests = true;
/* 2216 */   private static Component newFocusOwner = null;
/*      */   private static volatile boolean disableRestoreFocus;
/*      */   static final int SNFH_FAILURE = 0;
/*      */   static final int SNFH_SUCCESS_HANDLED = 1;
/*      */   static final int SNFH_SUCCESS_PROCEED = 2;
/*      */   static Field proxyActive;
/*      */ 
/*      */   private static native void initIDs();
/*      */ 
/*      */   public static KeyboardFocusManager getCurrentKeyboardFocusManager()
/*      */   {
/*  216 */     return getCurrentKeyboardFocusManager(AppContext.getAppContext());
/*      */   }
/*      */ 
/*      */   static synchronized KeyboardFocusManager getCurrentKeyboardFocusManager(AppContext paramAppContext)
/*      */   {
/*  222 */     Object localObject = (KeyboardFocusManager)paramAppContext.get(KeyboardFocusManager.class);
/*      */ 
/*  224 */     if (localObject == null) {
/*  225 */       localObject = new DefaultKeyboardFocusManager();
/*  226 */       paramAppContext.put(KeyboardFocusManager.class, localObject);
/*      */     }
/*  228 */     return localObject;
/*      */   }
/*      */ 
/*      */   public static void setCurrentKeyboardFocusManager(KeyboardFocusManager paramKeyboardFocusManager)
/*      */     throws SecurityException
/*      */   {
/*  251 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  252 */     if (localSecurityManager != null) {
/*  253 */       if (replaceKeyboardFocusManagerPermission == null) {
/*  254 */         replaceKeyboardFocusManagerPermission = new AWTPermission("replaceKeyboardFocusManager");
/*      */       }
/*      */ 
/*  257 */       localSecurityManager.checkPermission(replaceKeyboardFocusManagerPermission);
/*      */     }
/*      */ 
/*  261 */     KeyboardFocusManager localKeyboardFocusManager = null;
/*      */ 
/*  263 */     synchronized (KeyboardFocusManager.class) {
/*  264 */       AppContext localAppContext = AppContext.getAppContext();
/*      */ 
/*  266 */       if (paramKeyboardFocusManager != null) {
/*  267 */         localKeyboardFocusManager = getCurrentKeyboardFocusManager(localAppContext);
/*      */ 
/*  269 */         localAppContext.put(KeyboardFocusManager.class, paramKeyboardFocusManager);
/*      */       } else {
/*  271 */         localKeyboardFocusManager = getCurrentKeyboardFocusManager(localAppContext);
/*  272 */         localAppContext.remove(KeyboardFocusManager.class);
/*      */       }
/*      */     }
/*      */ 
/*  276 */     if (localKeyboardFocusManager != null) {
/*  277 */       localKeyboardFocusManager.firePropertyChange("managingFocus", Boolean.TRUE, Boolean.FALSE);
/*      */     }
/*      */ 
/*  281 */     if (paramKeyboardFocusManager != null)
/*  282 */       paramKeyboardFocusManager.firePropertyChange("managingFocus", Boolean.FALSE, Boolean.TRUE);
/*      */   }
/*      */ 
/*      */   final void setCurrentSequencedEvent(SequencedEvent paramSequencedEvent)
/*      */   {
/*  420 */     synchronized (SequencedEvent.class) {
/*  421 */       assert ((paramSequencedEvent == null) || (this.currentSequencedEvent == null));
/*  422 */       this.currentSequencedEvent = paramSequencedEvent;
/*      */     }
/*      */   }
/*      */ 
/*      */   final SequencedEvent getCurrentSequencedEvent() {
/*  427 */     synchronized (SequencedEvent.class) {
/*  428 */       return this.currentSequencedEvent;
/*      */     }
/*      */   }
/*      */ 
/*      */   static Set initFocusTraversalKeysSet(String paramString, Set paramSet) {
/*  433 */     StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ",");
/*  434 */     while (localStringTokenizer.hasMoreTokens()) {
/*  435 */       paramSet.add(AWTKeyStroke.getAWTKeyStroke(localStringTokenizer.nextToken()));
/*      */     }
/*  437 */     return paramSet.isEmpty() ? Collections.EMPTY_SET : Collections.unmodifiableSet(paramSet);
/*      */   }
/*      */ 
/*      */   public KeyboardFocusManager()
/*      */   {
/*  446 */     for (int i = 0; i < 4; i++) {
/*  447 */       HashSet localHashSet = new HashSet();
/*  448 */       for (int j = 0; j < defaultFocusTraversalKeyStrokes[i].length; j++) {
/*  449 */         localHashSet.add(defaultFocusTraversalKeyStrokes[i][j]);
/*      */       }
/*  451 */       this.defaultFocusTraversalKeys[i] = (localHashSet.isEmpty() ? Collections.EMPTY_SET : Collections.unmodifiableSet(localHashSet));
/*      */     }
/*      */ 
/*  455 */     initPeer();
/*      */   }
/*      */ 
/*      */   private void initPeer() {
/*  459 */     Toolkit localToolkit = Toolkit.getDefaultToolkit();
/*  460 */     KeyboardFocusManagerPeerProvider localKeyboardFocusManagerPeerProvider = (KeyboardFocusManagerPeerProvider)localToolkit;
/*  461 */     this.peer = localKeyboardFocusManagerPeerProvider.createKeyboardFocusManagerPeer(this);
/*      */   }
/*      */ 
/*      */   public Component getFocusOwner()
/*      */   {
/*  479 */     synchronized (KeyboardFocusManager.class) {
/*  480 */       if (focusOwner == null) {
/*  481 */         return null;
/*      */       }
/*      */ 
/*  484 */       return focusOwner.appContext == AppContext.getAppContext() ? focusOwner : null;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected Component getGlobalFocusOwner()
/*      */     throws SecurityException
/*      */   {
/*  510 */     synchronized (KeyboardFocusManager.class) {
/*  511 */       checkCurrentKFMSecurity();
/*  512 */       return focusOwner;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void setGlobalFocusOwner(Component paramComponent)
/*      */   {
/*  541 */     Component localComponent = null;
/*  542 */     int i = 0;
/*      */ 
/*  544 */     if ((paramComponent == null) || (paramComponent.isFocusable())) {
/*  545 */       synchronized (KeyboardFocusManager.class) {
/*  546 */         checkCurrentKFMSecurity();
/*      */ 
/*  548 */         localComponent = getFocusOwner();
/*      */         try
/*      */         {
/*  551 */           fireVetoableChange("focusOwner", localComponent, paramComponent);
/*      */         }
/*      */         catch (PropertyVetoException localPropertyVetoException)
/*      */         {
/*  555 */           return;
/*      */         }
/*      */ 
/*  558 */         focusOwner = paramComponent;
/*      */ 
/*  560 */         if ((paramComponent != null) && ((getCurrentFocusCycleRoot() == null) || (!paramComponent.isFocusCycleRoot(getCurrentFocusCycleRoot()))))
/*      */         {
/*  564 */           Container localContainer = paramComponent.getFocusCycleRootAncestor();
/*      */ 
/*  566 */           if ((localContainer == null) && ((paramComponent instanceof Window)))
/*      */           {
/*  568 */             localContainer = (Container)paramComponent;
/*      */           }
/*  570 */           if (localContainer != null) {
/*  571 */             setGlobalCurrentFocusCycleRoot(localContainer);
/*      */           }
/*      */         }
/*      */ 
/*  575 */         i = 1;
/*      */       }
/*      */     }
/*      */ 
/*  579 */     if (i != 0)
/*  580 */       firePropertyChange("focusOwner", localComponent, paramComponent);
/*      */   }
/*      */ 
/*      */   public void clearGlobalFocusOwner()
/*      */   {
/*  597 */     synchronized (KeyboardFocusManager.class) {
/*  598 */       checkCurrentKFMSecurity();
/*      */     }
/*  600 */     if (!GraphicsEnvironment.isHeadless())
/*      */     {
/*  603 */       Toolkit.getDefaultToolkit();
/*      */ 
/*  605 */       _clearGlobalFocusOwner();
/*      */     }
/*      */   }
/*      */ 
/*  609 */   private void _clearGlobalFocusOwner() { Window localWindow = markClearGlobalFocusOwner();
/*  610 */     this.peer.clearGlobalFocusOwner(localWindow); }
/*      */ 
/*      */   Component getNativeFocusOwner()
/*      */   {
/*  614 */     return this.peer.getCurrentFocusOwner();
/*      */   }
/*      */ 
/*      */   void setNativeFocusOwner(Component paramComponent) {
/*  618 */     if (focusLog.isLoggable(300)) {
/*  619 */       focusLog.finest("Calling peer {0} setCurrentFocusOwner for {1}", new Object[] { String.valueOf(this.peer), String.valueOf(paramComponent) });
/*      */     }
/*      */ 
/*  622 */     this.peer.setCurrentFocusOwner(paramComponent);
/*      */   }
/*      */ 
/*      */   Window getNativeFocusedWindow() {
/*  626 */     return this.peer.getCurrentFocusedWindow();
/*      */   }
/*      */ 
/*      */   public Component getPermanentFocusOwner()
/*      */   {
/*  644 */     synchronized (KeyboardFocusManager.class) {
/*  645 */       if (permanentFocusOwner == null) {
/*  646 */         return null;
/*      */       }
/*      */ 
/*  649 */       return permanentFocusOwner.appContext == AppContext.getAppContext() ? permanentFocusOwner : null;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected Component getGlobalPermanentFocusOwner()
/*      */     throws SecurityException
/*      */   {
/*  678 */     synchronized (KeyboardFocusManager.class) {
/*  679 */       checkCurrentKFMSecurity();
/*  680 */       return permanentFocusOwner;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void setGlobalPermanentFocusOwner(Component paramComponent)
/*      */   {
/*  710 */     Component localComponent = null;
/*  711 */     int i = 0;
/*      */ 
/*  713 */     if ((paramComponent == null) || (paramComponent.isFocusable())) {
/*  714 */       synchronized (KeyboardFocusManager.class) {
/*  715 */         checkCurrentKFMSecurity();
/*      */ 
/*  717 */         localComponent = getPermanentFocusOwner();
/*      */         try
/*      */         {
/*  720 */           fireVetoableChange("permanentFocusOwner", localComponent, paramComponent);
/*      */         }
/*      */         catch (PropertyVetoException localPropertyVetoException)
/*      */         {
/*  725 */           return;
/*      */         }
/*      */ 
/*  728 */         permanentFocusOwner = paramComponent;
/*      */ 
/*  730 */         setMostRecentFocusOwner(paramComponent);
/*      */ 
/*  733 */         i = 1;
/*      */       }
/*      */     }
/*      */ 
/*  737 */     if (i != 0)
/*  738 */       firePropertyChange("permanentFocusOwner", localComponent, paramComponent);
/*      */   }
/*      */ 
/*      */   public Window getFocusedWindow()
/*      */   {
/*  754 */     synchronized (KeyboardFocusManager.class) {
/*  755 */       if (focusedWindow == null) {
/*  756 */         return null;
/*      */       }
/*      */ 
/*  759 */       return focusedWindow.appContext == AppContext.getAppContext() ? focusedWindow : null;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected Window getGlobalFocusedWindow()
/*      */     throws SecurityException
/*      */   {
/*  781 */     synchronized (KeyboardFocusManager.class) {
/*  782 */       checkCurrentKFMSecurity();
/*  783 */       return focusedWindow;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void setGlobalFocusedWindow(Window paramWindow)
/*      */   {
/*  809 */     Window localWindow = null;
/*  810 */     int i = 0;
/*      */ 
/*  812 */     if ((paramWindow == null) || (paramWindow.isFocusableWindow())) {
/*  813 */       synchronized (KeyboardFocusManager.class) {
/*  814 */         checkCurrentKFMSecurity();
/*      */ 
/*  816 */         localWindow = getFocusedWindow();
/*      */         try
/*      */         {
/*  819 */           fireVetoableChange("focusedWindow", localWindow, paramWindow);
/*      */         }
/*      */         catch (PropertyVetoException localPropertyVetoException)
/*      */         {
/*  823 */           return;
/*      */         }
/*      */ 
/*  826 */         focusedWindow = paramWindow;
/*  827 */         i = 1;
/*      */       }
/*      */     }
/*      */ 
/*  831 */     if (i != 0)
/*  832 */       firePropertyChange("focusedWindow", localWindow, paramWindow);
/*      */   }
/*      */ 
/*      */   public Window getActiveWindow()
/*      */   {
/*  851 */     synchronized (KeyboardFocusManager.class) {
/*  852 */       if (activeWindow == null) {
/*  853 */         return null;
/*      */       }
/*      */ 
/*  856 */       return activeWindow.appContext == AppContext.getAppContext() ? activeWindow : null;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected Window getGlobalActiveWindow()
/*      */     throws SecurityException
/*      */   {
/*  881 */     synchronized (KeyboardFocusManager.class) {
/*  882 */       checkCurrentKFMSecurity();
/*  883 */       return activeWindow;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void setGlobalActiveWindow(Window paramWindow)
/*      */   {
/*      */     Window localWindow;
/*  911 */     synchronized (KeyboardFocusManager.class) {
/*  912 */       checkCurrentKFMSecurity();
/*      */ 
/*  914 */       localWindow = getActiveWindow();
/*  915 */       if (focusLog.isLoggable(400)) {
/*  916 */         focusLog.finer("Setting global active window to " + paramWindow + ", old active " + localWindow);
/*      */       }
/*      */       try
/*      */       {
/*  920 */         fireVetoableChange("activeWindow", localWindow, paramWindow);
/*      */       }
/*      */       catch (PropertyVetoException localPropertyVetoException)
/*      */       {
/*  924 */         return;
/*      */       }
/*      */ 
/*  927 */       activeWindow = paramWindow;
/*      */     }
/*      */ 
/*  930 */     firePropertyChange("activeWindow", localWindow, paramWindow);
/*      */   }
/*      */ 
/*      */   public synchronized FocusTraversalPolicy getDefaultFocusTraversalPolicy()
/*      */   {
/*  944 */     return this.defaultPolicy;
/*      */   }
/*      */ 
/*      */   public void setDefaultFocusTraversalPolicy(FocusTraversalPolicy paramFocusTraversalPolicy)
/*      */   {
/*  965 */     if (paramFocusTraversalPolicy == null)
/*  966 */       throw new IllegalArgumentException("default focus traversal policy cannot be null");
/*      */     FocusTraversalPolicy localFocusTraversalPolicy;
/*  971 */     synchronized (this) {
/*  972 */       localFocusTraversalPolicy = this.defaultPolicy;
/*  973 */       this.defaultPolicy = paramFocusTraversalPolicy;
/*      */     }
/*      */ 
/*  976 */     firePropertyChange("defaultFocusTraversalPolicy", localFocusTraversalPolicy, paramFocusTraversalPolicy);
/*      */   }
/*      */ 
/*      */   public void setDefaultFocusTraversalKeys(int paramInt, Set<? extends AWTKeyStroke> paramSet)
/*      */   {
/* 1071 */     if ((paramInt < 0) || (paramInt >= 4)) {
/* 1072 */       throw new IllegalArgumentException("invalid focus traversal key identifier");
/*      */     }
/* 1074 */     if (paramSet == null)
/* 1075 */       throw new IllegalArgumentException("cannot set null Set of default focus traversal keys");
/*      */     Set localSet;
/* 1080 */     synchronized (this) {
/* 1081 */       for (Iterator localIterator = paramSet.iterator(); localIterator.hasNext(); ) {
/* 1082 */         Object localObject1 = localIterator.next();
/*      */ 
/* 1084 */         if (localObject1 == null) {
/* 1085 */           throw new IllegalArgumentException("cannot set null focus traversal key");
/*      */         }
/*      */ 
/* 1090 */         if (!(localObject1 instanceof AWTKeyStroke)) {
/* 1091 */           throw new IllegalArgumentException("object is expected to be AWTKeyStroke");
/*      */         }
/* 1093 */         AWTKeyStroke localAWTKeyStroke = (AWTKeyStroke)localObject1;
/*      */ 
/* 1095 */         if (localAWTKeyStroke.getKeyChar() != 65535) {
/* 1096 */           throw new IllegalArgumentException("focus traversal keys cannot map to KEY_TYPED events");
/*      */         }
/*      */ 
/* 1101 */         for (int i = 0; i < 4; i++) {
/* 1102 */           if (i != paramInt)
/*      */           {
/* 1106 */             if (this.defaultFocusTraversalKeys[i].contains(localAWTKeyStroke)) {
/* 1107 */               throw new IllegalArgumentException("focus traversal keys must be unique for a Component");
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 1112 */       localSet = this.defaultFocusTraversalKeys[paramInt];
/* 1113 */       this.defaultFocusTraversalKeys[paramInt] = Collections.unmodifiableSet(new HashSet(paramSet));
/*      */     }
/*      */ 
/* 1117 */     firePropertyChange(defaultFocusTraversalKeyPropertyNames[paramInt], localSet, paramSet);
/*      */   }
/*      */ 
/*      */   public Set<AWTKeyStroke> getDefaultFocusTraversalKeys(int paramInt)
/*      */   {
/* 1148 */     if ((paramInt < 0) || (paramInt >= 4)) {
/* 1149 */       throw new IllegalArgumentException("invalid focus traversal key identifier");
/*      */     }
/*      */ 
/* 1153 */     return this.defaultFocusTraversalKeys[paramInt];
/*      */   }
/*      */ 
/*      */   public Container getCurrentFocusCycleRoot()
/*      */   {
/* 1173 */     synchronized (KeyboardFocusManager.class) {
/* 1174 */       if (currentFocusCycleRoot == null) {
/* 1175 */         return null;
/*      */       }
/*      */ 
/* 1178 */       return currentFocusCycleRoot.appContext == AppContext.getAppContext() ? currentFocusCycleRoot : null;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected Container getGlobalCurrentFocusCycleRoot()
/*      */     throws SecurityException
/*      */   {
/* 1207 */     synchronized (KeyboardFocusManager.class) {
/* 1208 */       checkCurrentKFMSecurity();
/* 1209 */       return currentFocusCycleRoot;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setGlobalCurrentFocusCycleRoot(Container paramContainer)
/*      */   {
/*      */     Container localContainer;
/* 1232 */     synchronized (KeyboardFocusManager.class) {
/* 1233 */       checkCurrentKFMSecurity();
/*      */ 
/* 1235 */       localContainer = getCurrentFocusCycleRoot();
/* 1236 */       currentFocusCycleRoot = paramContainer;
/*      */     }
/*      */ 
/* 1239 */     firePropertyChange("currentFocusCycleRoot", localContainer, paramContainer);
/*      */   }
/*      */ 
/*      */   public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
/*      */   {
/* 1275 */     if (paramPropertyChangeListener != null)
/* 1276 */       synchronized (this) {
/* 1277 */         if (this.changeSupport == null) {
/* 1278 */           this.changeSupport = new PropertyChangeSupport(this);
/*      */         }
/* 1280 */         this.changeSupport.addPropertyChangeListener(paramPropertyChangeListener);
/*      */       }
/*      */   }
/*      */ 
/*      */   public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
/*      */   {
/* 1298 */     if (paramPropertyChangeListener != null)
/* 1299 */       synchronized (this) {
/* 1300 */         if (this.changeSupport != null)
/* 1301 */           this.changeSupport.removePropertyChangeListener(paramPropertyChangeListener);
/*      */       }
/*      */   }
/*      */ 
/*      */   public synchronized PropertyChangeListener[] getPropertyChangeListeners()
/*      */   {
/* 1322 */     if (this.changeSupport == null) {
/* 1323 */       this.changeSupport = new PropertyChangeSupport(this);
/*      */     }
/* 1325 */     return this.changeSupport.getPropertyChangeListeners();
/*      */   }
/*      */ 
/*      */   public void addPropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener)
/*      */   {
/* 1362 */     if (paramPropertyChangeListener != null)
/* 1363 */       synchronized (this) {
/* 1364 */         if (this.changeSupport == null) {
/* 1365 */           this.changeSupport = new PropertyChangeSupport(this);
/*      */         }
/* 1367 */         this.changeSupport.addPropertyChangeListener(paramString, paramPropertyChangeListener);
/*      */       }
/*      */   }
/*      */ 
/*      */   public void removePropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener)
/*      */   {
/* 1388 */     if (paramPropertyChangeListener != null)
/* 1389 */       synchronized (this) {
/* 1390 */         if (this.changeSupport != null)
/* 1391 */           this.changeSupport.removePropertyChangeListener(paramString, paramPropertyChangeListener);
/*      */       }
/*      */   }
/*      */ 
/*      */   public synchronized PropertyChangeListener[] getPropertyChangeListeners(String paramString)
/*      */   {
/* 1411 */     if (this.changeSupport == null) {
/* 1412 */       this.changeSupport = new PropertyChangeSupport(this);
/*      */     }
/* 1414 */     return this.changeSupport.getPropertyChangeListeners(paramString);
/*      */   }
/*      */ 
/*      */   protected void firePropertyChange(String paramString, Object paramObject1, Object paramObject2)
/*      */   {
/* 1429 */     if (paramObject1 == paramObject2) {
/* 1430 */       return;
/*      */     }
/* 1432 */     PropertyChangeSupport localPropertyChangeSupport = this.changeSupport;
/* 1433 */     if (localPropertyChangeSupport != null)
/* 1434 */       localPropertyChangeSupport.firePropertyChange(paramString, paramObject1, paramObject2);
/*      */   }
/*      */ 
/*      */   public void addVetoableChangeListener(VetoableChangeListener paramVetoableChangeListener)
/*      */   {
/* 1456 */     if (paramVetoableChangeListener != null)
/* 1457 */       synchronized (this) {
/* 1458 */         if (this.vetoableSupport == null) {
/* 1459 */           this.vetoableSupport = new VetoableChangeSupport(this);
/*      */         }
/*      */ 
/* 1462 */         this.vetoableSupport.addVetoableChangeListener(paramVetoableChangeListener);
/*      */       }
/*      */   }
/*      */ 
/*      */   public void removeVetoableChangeListener(VetoableChangeListener paramVetoableChangeListener)
/*      */   {
/* 1480 */     if (paramVetoableChangeListener != null)
/* 1481 */       synchronized (this) {
/* 1482 */         if (this.vetoableSupport != null)
/* 1483 */           this.vetoableSupport.removeVetoableChangeListener(paramVetoableChangeListener);
/*      */       }
/*      */   }
/*      */ 
/*      */   public synchronized VetoableChangeListener[] getVetoableChangeListeners()
/*      */   {
/* 1504 */     if (this.vetoableSupport == null) {
/* 1505 */       this.vetoableSupport = new VetoableChangeSupport(this);
/*      */     }
/* 1507 */     return this.vetoableSupport.getVetoableChangeListeners();
/*      */   }
/*      */ 
/*      */   public void addVetoableChangeListener(String paramString, VetoableChangeListener paramVetoableChangeListener)
/*      */   {
/* 1530 */     if (paramVetoableChangeListener != null)
/* 1531 */       synchronized (this) {
/* 1532 */         if (this.vetoableSupport == null) {
/* 1533 */           this.vetoableSupport = new VetoableChangeSupport(this);
/*      */         }
/*      */ 
/* 1536 */         this.vetoableSupport.addVetoableChangeListener(paramString, paramVetoableChangeListener);
/*      */       }
/*      */   }
/*      */ 
/*      */   public void removeVetoableChangeListener(String paramString, VetoableChangeListener paramVetoableChangeListener)
/*      */   {
/* 1557 */     if (paramVetoableChangeListener != null)
/* 1558 */       synchronized (this) {
/* 1559 */         if (this.vetoableSupport != null)
/* 1560 */           this.vetoableSupport.removeVetoableChangeListener(paramString, paramVetoableChangeListener);
/*      */       }
/*      */   }
/*      */ 
/*      */   public synchronized VetoableChangeListener[] getVetoableChangeListeners(String paramString)
/*      */   {
/* 1581 */     if (this.vetoableSupport == null) {
/* 1582 */       this.vetoableSupport = new VetoableChangeSupport(this);
/*      */     }
/* 1584 */     return this.vetoableSupport.getVetoableChangeListeners(paramString);
/*      */   }
/*      */ 
/*      */   protected void fireVetoableChange(String paramString, Object paramObject1, Object paramObject2)
/*      */     throws PropertyVetoException
/*      */   {
/* 1607 */     if (paramObject1 == paramObject2) {
/* 1608 */       return;
/*      */     }
/* 1610 */     VetoableChangeSupport localVetoableChangeSupport = this.vetoableSupport;
/*      */ 
/* 1612 */     if (localVetoableChangeSupport != null)
/* 1613 */       localVetoableChangeSupport.fireVetoableChange(paramString, paramObject1, paramObject2);
/*      */   }
/*      */ 
/*      */   public void addKeyEventDispatcher(KeyEventDispatcher paramKeyEventDispatcher)
/*      */   {
/* 1641 */     if (paramKeyEventDispatcher != null)
/* 1642 */       synchronized (this) {
/* 1643 */         if (this.keyEventDispatchers == null) {
/* 1644 */           this.keyEventDispatchers = new LinkedList();
/*      */         }
/* 1646 */         this.keyEventDispatchers.add(paramKeyEventDispatcher);
/*      */       }
/*      */   }
/*      */ 
/*      */   public void removeKeyEventDispatcher(KeyEventDispatcher paramKeyEventDispatcher)
/*      */   {
/* 1672 */     if (paramKeyEventDispatcher != null)
/* 1673 */       synchronized (this) {
/* 1674 */         if (this.keyEventDispatchers != null)
/* 1675 */           this.keyEventDispatchers.remove(paramKeyEventDispatcher);
/*      */       }
/*      */   }
/*      */ 
/*      */   protected synchronized List<KeyEventDispatcher> getKeyEventDispatchers()
/*      */   {
/* 1697 */     return this.keyEventDispatchers != null ? (List)this.keyEventDispatchers.clone() : null;
/*      */   }
/*      */ 
/*      */   public void addKeyEventPostProcessor(KeyEventPostProcessor paramKeyEventPostProcessor)
/*      */   {
/* 1729 */     if (paramKeyEventPostProcessor != null)
/* 1730 */       synchronized (this) {
/* 1731 */         if (this.keyEventPostProcessors == null) {
/* 1732 */           this.keyEventPostProcessors = new LinkedList();
/*      */         }
/* 1734 */         this.keyEventPostProcessors.add(paramKeyEventPostProcessor);
/*      */       }
/*      */   }
/*      */ 
/*      */   public void removeKeyEventPostProcessor(KeyEventPostProcessor paramKeyEventPostProcessor)
/*      */   {
/* 1762 */     if (paramKeyEventPostProcessor != null)
/* 1763 */       synchronized (this) {
/* 1764 */         if (this.keyEventPostProcessors != null)
/* 1765 */           this.keyEventPostProcessors.remove(paramKeyEventPostProcessor);
/*      */       }
/*      */   }
/*      */ 
/*      */   protected List<KeyEventPostProcessor> getKeyEventPostProcessors()
/*      */   {
/* 1788 */     return this.keyEventPostProcessors != null ? (List)this.keyEventPostProcessors.clone() : null;
/*      */   }
/*      */ 
/*      */   static void setMostRecentFocusOwner(Component paramComponent)
/*      */   {
/* 1796 */     Object localObject = paramComponent;
/* 1797 */     while ((localObject != null) && (!(localObject instanceof Window))) {
/* 1798 */       localObject = ((Component)localObject).parent;
/*      */     }
/* 1800 */     if (localObject != null)
/* 1801 */       setMostRecentFocusOwner((Window)localObject, paramComponent);
/*      */   }
/*      */ 
/*      */   static synchronized void setMostRecentFocusOwner(Window paramWindow, Component paramComponent)
/*      */   {
/* 1810 */     WeakReference localWeakReference = null;
/* 1811 */     if (paramComponent != null) {
/* 1812 */       localWeakReference = new WeakReference(paramComponent);
/*      */     }
/* 1814 */     mostRecentFocusOwners.put(paramWindow, localWeakReference);
/*      */   }
/*      */ 
/*      */   static void clearMostRecentFocusOwner(Component paramComponent)
/*      */   {
/* 1819 */     if (paramComponent == null)
/*      */       return;
/*      */     Container localContainer;
/* 1823 */     synchronized (paramComponent.getTreeLock()) {
/* 1824 */       localContainer = paramComponent.getParent();
/* 1825 */       while ((localContainer != null) && (!(localContainer instanceof Window))) {
/* 1826 */         localContainer = localContainer.getParent();
/*      */       }
/*      */     }
/*      */ 
/* 1830 */     synchronized (KeyboardFocusManager.class) {
/* 1831 */       if ((localContainer != null) && (getMostRecentFocusOwner((Window)localContainer) == paramComponent))
/*      */       {
/* 1834 */         setMostRecentFocusOwner((Window)localContainer, null);
/*      */       }
/*      */ 
/* 1837 */       if (localContainer != null) {
/* 1838 */         Window localWindow = (Window)localContainer;
/* 1839 */         if (localWindow.getTemporaryLostComponent() == paramComponent)
/* 1840 */           localWindow.setTemporaryLostComponent(null);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   static synchronized Component getMostRecentFocusOwner(Window paramWindow)
/*      */   {
/* 1851 */     WeakReference localWeakReference = (WeakReference)mostRecentFocusOwners.get(paramWindow);
/*      */ 
/* 1853 */     return localWeakReference == null ? null : (Component)localWeakReference.get();
/*      */   }
/*      */ 
/*      */   public abstract boolean dispatchEvent(AWTEvent paramAWTEvent);
/*      */ 
/*      */   public final void redispatchEvent(Component paramComponent, AWTEvent paramAWTEvent)
/*      */   {
/* 1894 */     paramAWTEvent.focusManagerIsDispatching = true;
/* 1895 */     paramComponent.dispatchEvent(paramAWTEvent);
/* 1896 */     paramAWTEvent.focusManagerIsDispatching = false;
/*      */   }
/*      */ 
/*      */   public abstract boolean dispatchKeyEvent(KeyEvent paramKeyEvent);
/*      */ 
/*      */   public abstract boolean postProcessKeyEvent(KeyEvent paramKeyEvent);
/*      */ 
/*      */   public abstract void processKeyEvent(Component paramComponent, KeyEvent paramKeyEvent);
/*      */ 
/*      */   protected abstract void enqueueKeyEvents(long paramLong, Component paramComponent);
/*      */ 
/*      */   protected abstract void dequeueKeyEvents(long paramLong, Component paramComponent);
/*      */ 
/*      */   protected abstract void discardKeyEvents(Component paramComponent);
/*      */ 
/*      */   public abstract void focusNextComponent(Component paramComponent);
/*      */ 
/*      */   public abstract void focusPreviousComponent(Component paramComponent);
/*      */ 
/*      */   public abstract void upFocusCycle(Component paramComponent);
/*      */ 
/*      */   public abstract void downFocusCycle(Container paramContainer);
/*      */ 
/*      */   public final void focusNextComponent()
/*      */   {
/* 2050 */     Component localComponent = getFocusOwner();
/* 2051 */     if (localComponent != null)
/* 2052 */       focusNextComponent(localComponent);
/*      */   }
/*      */ 
/*      */   public final void focusPreviousComponent()
/*      */   {
/* 2060 */     Component localComponent = getFocusOwner();
/* 2061 */     if (localComponent != null)
/* 2062 */       focusPreviousComponent(localComponent);
/*      */   }
/*      */ 
/*      */   public final void upFocusCycle()
/*      */   {
/* 2076 */     Component localComponent = getFocusOwner();
/* 2077 */     if (localComponent != null)
/* 2078 */       upFocusCycle(localComponent);
/*      */   }
/*      */ 
/*      */   public final void downFocusCycle()
/*      */   {
/* 2092 */     Component localComponent = getFocusOwner();
/* 2093 */     if ((localComponent instanceof Container))
/* 2094 */       downFocusCycle((Container)localComponent);
/*      */   }
/*      */ 
/*      */   void dumpRequests()
/*      */   {
/* 2102 */     System.err.println(">>> Requests dump, time: " + System.currentTimeMillis());
/* 2103 */     synchronized (heavyweightRequests) {
/* 2104 */       for (HeavyweightFocusRequest localHeavyweightFocusRequest : heavyweightRequests) {
/* 2105 */         System.err.println(">>> Req: " + localHeavyweightFocusRequest);
/*      */       }
/*      */     }
/* 2108 */     System.err.println("");
/*      */   }
/*      */ 
/*      */   static boolean processSynchronousLightweightTransfer(Component paramComponent1, Component paramComponent2, boolean paramBoolean1, boolean paramBoolean2, long paramLong)
/*      */   {
/* 2227 */     Window localWindow = SunToolkit.getContainingWindow(paramComponent1);
/* 2228 */     if ((localWindow == null) || (!localWindow.syncLWRequests)) {
/* 2229 */       return false;
/*      */     }
/* 2231 */     if (paramComponent2 == null)
/*      */     {
/* 2235 */       paramComponent2 = paramComponent1;
/*      */     }
/*      */ 
/* 2238 */     KeyboardFocusManager localKeyboardFocusManager = getCurrentKeyboardFocusManager(SunToolkit.targetToAppContext(paramComponent2));
/*      */ 
/* 2240 */     FocusEvent localFocusEvent1 = null;
/* 2241 */     FocusEvent localFocusEvent2 = null;
/* 2242 */     Component localComponent = localKeyboardFocusManager.getGlobalFocusOwner();
/*      */ 
/* 2244 */     synchronized (heavyweightRequests) {
/* 2245 */       HeavyweightFocusRequest localHeavyweightFocusRequest = getLastHWRequest();
/* 2246 */       if ((localHeavyweightFocusRequest == null) && (paramComponent1 == localKeyboardFocusManager.getNativeFocusOwner()) && (allowSyncFocusRequests))
/*      */       {
/* 2251 */         if (paramComponent2 == localComponent)
/*      */         {
/* 2253 */           return true;
/*      */         }
/*      */ 
/* 2260 */         localKeyboardFocusManager.enqueueKeyEvents(paramLong, paramComponent2);
/*      */ 
/* 2262 */         localHeavyweightFocusRequest = new HeavyweightFocusRequest(paramComponent1, paramComponent2, paramBoolean1, CausedFocusEvent.Cause.UNKNOWN);
/*      */ 
/* 2265 */         heavyweightRequests.add(localHeavyweightFocusRequest);
/*      */ 
/* 2267 */         if (localComponent != null) {
/* 2268 */           localFocusEvent1 = new FocusEvent(localComponent, 1005, paramBoolean1, paramComponent2);
/*      */         }
/*      */ 
/* 2273 */         localFocusEvent2 = new FocusEvent(paramComponent2, 1004, paramBoolean1, localComponent);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2278 */     boolean bool1 = false;
/* 2279 */     boolean bool2 = clearingCurrentLightweightRequests;
/*      */ 
/* 2281 */     Throwable localThrowable = null;
/*      */     try {
/* 2283 */       clearingCurrentLightweightRequests = false;
/* 2284 */       synchronized (Component.LOCK)
/*      */       {
/* 2286 */         if ((localFocusEvent1 != null) && (localComponent != null)) {
/* 2287 */           localFocusEvent1.isPosted = true;
/* 2288 */           localThrowable = dispatchAndCatchException(localThrowable, localComponent, localFocusEvent1);
/* 2289 */           bool1 = true;
/*      */         }
/*      */ 
/* 2292 */         if ((localFocusEvent2 != null) && (paramComponent2 != null)) {
/* 2293 */           localFocusEvent2.isPosted = true;
/* 2294 */           localThrowable = dispatchAndCatchException(localThrowable, paramComponent2, localFocusEvent2);
/* 2295 */           bool1 = true;
/*      */         }
/*      */       }
/*      */     } finally {
/* 2299 */       clearingCurrentLightweightRequests = bool2;
/*      */     }
/* 2301 */     if ((localThrowable instanceof RuntimeException))
/* 2302 */       throw ((RuntimeException)localThrowable);
/* 2303 */     if ((localThrowable instanceof Error)) {
/* 2304 */       throw ((Error)localThrowable);
/*      */     }
/* 2306 */     return bool1;
/*      */   }
/*      */ 
/*      */   static int shouldNativelyFocusHeavyweight(Component paramComponent1, Component paramComponent2, boolean paramBoolean1, boolean paramBoolean2, long paramLong, CausedFocusEvent.Cause paramCause)
/*      */   {
/* 2331 */     if (log.isLoggable(500)) {
/* 2332 */       if (paramComponent1 == null) {
/* 2333 */         log.fine("Assertion (heavyweight != null) failed");
/*      */       }
/* 2335 */       if (paramLong == 0L) {
/* 2336 */         log.fine("Assertion (time != 0) failed");
/*      */       }
/*      */     }
/*      */ 
/* 2340 */     if (paramComponent2 == null)
/*      */     {
/* 2344 */       paramComponent2 = paramComponent1;
/*      */     }
/*      */ 
/* 2347 */     KeyboardFocusManager localKeyboardFocusManager1 = getCurrentKeyboardFocusManager(SunToolkit.targetToAppContext(paramComponent2));
/*      */ 
/* 2349 */     KeyboardFocusManager localKeyboardFocusManager2 = getCurrentKeyboardFocusManager();
/* 2350 */     Component localComponent1 = localKeyboardFocusManager2.getGlobalFocusOwner();
/* 2351 */     Component localComponent2 = localKeyboardFocusManager2.getNativeFocusOwner();
/* 2352 */     Window localWindow = localKeyboardFocusManager2.getNativeFocusedWindow();
/* 2353 */     if (focusLog.isLoggable(400)) {
/* 2354 */       focusLog.finer("SNFH for {0} in {1}", new Object[] { String.valueOf(paramComponent2), String.valueOf(paramComponent1) });
/*      */     }
/*      */ 
/* 2357 */     if (focusLog.isLoggable(300)) {
/* 2358 */       focusLog.finest("0. Current focus owner {0}", new Object[] { String.valueOf(localComponent1) });
/*      */ 
/* 2360 */       focusLog.finest("0. Native focus owner {0}", new Object[] { String.valueOf(localComponent2) });
/*      */ 
/* 2362 */       focusLog.finest("0. Native focused window {0}", new Object[] { String.valueOf(localWindow) });
/*      */     }
/*      */ 
/* 2365 */     synchronized (heavyweightRequests) {
/* 2366 */       HeavyweightFocusRequest localHeavyweightFocusRequest = getLastHWRequest();
/* 2367 */       if (focusLog.isLoggable(300)) {
/* 2368 */         focusLog.finest("Request {0}", new Object[] { String.valueOf(localHeavyweightFocusRequest) });
/*      */       }
/* 2370 */       if ((localHeavyweightFocusRequest == null) && (paramComponent1 == localComponent2))
/*      */       {
/* 2373 */         if (paramComponent2 == localComponent1)
/*      */         {
/* 2375 */           if (focusLog.isLoggable(300)) {
/* 2376 */             focusLog.finest("1. SNFH_FAILURE for {0}", new Object[] { String.valueOf(paramComponent2) });
/*      */           }
/* 2378 */           return 0;
/*      */         }
/*      */ 
/* 2385 */         localKeyboardFocusManager1.enqueueKeyEvents(paramLong, paramComponent2);
/*      */ 
/* 2387 */         localHeavyweightFocusRequest = new HeavyweightFocusRequest(paramComponent1, paramComponent2, paramBoolean1, paramCause);
/*      */ 
/* 2390 */         heavyweightRequests.add(localHeavyweightFocusRequest);
/*      */ 
/* 2392 */         if (localComponent1 != null) {
/* 2393 */           localCausedFocusEvent = new CausedFocusEvent(localComponent1, 1005, paramBoolean1, paramComponent2, paramCause);
/*      */ 
/* 2399 */           SunToolkit.postEvent(localComponent1.appContext, localCausedFocusEvent);
/*      */         }
/*      */ 
/* 2402 */         CausedFocusEvent localCausedFocusEvent = new CausedFocusEvent(paramComponent2, 1004, paramBoolean1, localComponent1, paramCause);
/*      */ 
/* 2407 */         SunToolkit.postEvent(paramComponent2.appContext, localCausedFocusEvent);
/*      */ 
/* 2409 */         if (focusLog.isLoggable(300))
/* 2410 */           focusLog.finest("2. SNFH_HANDLED for {0}", new Object[] { String.valueOf(paramComponent2) });
/* 2411 */         return 1;
/* 2412 */       }if ((localHeavyweightFocusRequest != null) && (localHeavyweightFocusRequest.heavyweight == paramComponent1))
/*      */       {
/* 2418 */         if (localHeavyweightFocusRequest.addLightweightRequest(paramComponent2, paramBoolean1, paramCause))
/*      */         {
/* 2420 */           localKeyboardFocusManager1.enqueueKeyEvents(paramLong, paramComponent2);
/*      */         }
/*      */ 
/* 2423 */         if (focusLog.isLoggable(300)) {
/* 2424 */           focusLog.finest("3. SNFH_HANDLED for lightweight" + paramComponent2 + " in " + paramComponent1);
/*      */         }
/* 2426 */         return 1;
/*      */       }
/* 2428 */       if (!paramBoolean2)
/*      */       {
/* 2434 */         if (localHeavyweightFocusRequest == HeavyweightFocusRequest.CLEAR_GLOBAL_FOCUS_OWNER)
/*      */         {
/* 2437 */           int i = heavyweightRequests.size();
/* 2438 */           localHeavyweightFocusRequest = i >= 2 ? (HeavyweightFocusRequest)heavyweightRequests.get(i - 2) : null;
/*      */         }
/*      */ 
/* 2442 */         if (focusedWindowChanged(paramComponent1, localHeavyweightFocusRequest != null ? localHeavyweightFocusRequest.heavyweight : localWindow))
/*      */         {
/* 2446 */           if (focusLog.isLoggable(300))
/* 2447 */             focusLog.finest("4. SNFH_FAILURE for " + paramComponent2);
/* 2448 */           return 0;
/*      */         }
/*      */       }
/*      */ 
/* 2452 */       localKeyboardFocusManager1.enqueueKeyEvents(paramLong, paramComponent2);
/* 2453 */       heavyweightRequests.add(new HeavyweightFocusRequest(paramComponent1, paramComponent2, paramBoolean1, paramCause));
/*      */ 
/* 2456 */       if (focusLog.isLoggable(300))
/* 2457 */         focusLog.finest("5. SNFH_PROCEED for " + paramComponent2);
/* 2458 */       return 2;
/*      */     }
/*      */   }
/*      */ 
/*      */   static Window markClearGlobalFocusOwner()
/*      */   {
/* 2473 */     Window localWindow = getCurrentKeyboardFocusManager().getNativeFocusedWindow();
/*      */ 
/* 2476 */     synchronized (heavyweightRequests) {
/* 2477 */       HeavyweightFocusRequest localHeavyweightFocusRequest = getLastHWRequest();
/* 2478 */       if (localHeavyweightFocusRequest == HeavyweightFocusRequest.CLEAR_GLOBAL_FOCUS_OWNER)
/*      */       {
/* 2482 */         return null;
/*      */       }
/*      */ 
/* 2485 */       heavyweightRequests.add(HeavyweightFocusRequest.CLEAR_GLOBAL_FOCUS_OWNER);
/*      */ 
/* 2488 */       Object localObject1 = localHeavyweightFocusRequest != null ? SunToolkit.getContainingWindow(localHeavyweightFocusRequest.heavyweight) : localWindow;
/*      */ 
/* 2491 */       while ((localObject1 != null) && (!(localObject1 instanceof Frame)) && (!(localObject1 instanceof Dialog)))
/*      */       {
/* 2495 */         localObject1 = ((Component)localObject1).getParent_NoClientCode();
/*      */       }
/*      */ 
/* 2498 */       return (Window)localObject1;
/*      */     }
/*      */   }
/*      */ 
/* 2502 */   Component getCurrentWaitingRequest(Component paramComponent) { synchronized (heavyweightRequests) {
/* 2503 */       HeavyweightFocusRequest localHeavyweightFocusRequest = getFirstHWRequest();
/* 2504 */       if ((localHeavyweightFocusRequest != null) && 
/* 2505 */         (localHeavyweightFocusRequest.heavyweight == paramComponent)) {
/* 2506 */         LightweightFocusRequest localLightweightFocusRequest = (LightweightFocusRequest)localHeavyweightFocusRequest.lightweightRequests.getFirst();
/*      */ 
/* 2508 */         if (localLightweightFocusRequest != null) {
/* 2509 */           return localLightweightFocusRequest.component;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 2514 */     return null; }
/*      */ 
/*      */   static boolean isAutoFocusTransferEnabled()
/*      */   {
/* 2518 */     synchronized (heavyweightRequests) {
/* 2519 */       return (heavyweightRequests.size() == 0) && (!disableRestoreFocus) && (null == currentLightweightRequests);
/*      */     }
/*      */   }
/*      */ 
/*      */   static boolean isAutoFocusTransferEnabledFor(Component paramComponent)
/*      */   {
/* 2526 */     return (isAutoFocusTransferEnabled()) && (paramComponent.isAutoFocusTransferOnDisposal());
/*      */   }
/*      */ 
/*      */   private static Throwable dispatchAndCatchException(Throwable paramThrowable, Component paramComponent, FocusEvent paramFocusEvent)
/*      */   {
/* 2536 */     Object localObject = null;
/*      */     try {
/* 2538 */       paramComponent.dispatchEvent(paramFocusEvent);
/*      */     } catch (RuntimeException localRuntimeException) {
/* 2540 */       localObject = localRuntimeException;
/*      */     } catch (Error localError) {
/* 2542 */       localObject = localError;
/*      */     }
/* 2544 */     if (localObject != null) {
/* 2545 */       if (paramThrowable != null) {
/* 2546 */         handleException(paramThrowable);
/*      */       }
/* 2548 */       return localObject;
/*      */     }
/* 2550 */     return paramThrowable;
/*      */   }
/*      */ 
/*      */   private static void handleException(Throwable paramThrowable) {
/* 2554 */     paramThrowable.printStackTrace();
/*      */   }
/*      */ 
/*      */   static void processCurrentLightweightRequests() {
/* 2558 */     KeyboardFocusManager localKeyboardFocusManager = getCurrentKeyboardFocusManager();
/* 2559 */     LinkedList localLinkedList = null;
/*      */ 
/* 2561 */     Component localComponent1 = localKeyboardFocusManager.getGlobalFocusOwner();
/* 2562 */     if ((localComponent1 != null) && (localComponent1.appContext != AppContext.getAppContext()))
/*      */     {
/* 2568 */       return;
/*      */     }
/*      */ 
/* 2571 */     synchronized (heavyweightRequests) {
/* 2572 */       if (currentLightweightRequests != null) {
/* 2573 */         clearingCurrentLightweightRequests = true;
/* 2574 */         disableRestoreFocus = true;
/* 2575 */         localLinkedList = currentLightweightRequests;
/* 2576 */         allowSyncFocusRequests = localLinkedList.size() < 2;
/* 2577 */         currentLightweightRequests = null;
/*      */       }
/*      */       else {
/* 2580 */         return;
/*      */       }
/*      */     }
/*      */ 
/* 2584 */     ??? = null;
/*      */     try {
/* 2586 */       if (localLinkedList != null) {
/* 2587 */         localComponent2 = null;
/* 2588 */         localComponent3 = null;
/*      */ 
/* 2590 */         for (localIterator = localLinkedList.iterator(); localIterator.hasNext(); )
/*      */         {
/* 2592 */           localComponent3 = localKeyboardFocusManager.getGlobalFocusOwner();
/* 2593 */           LightweightFocusRequest localLightweightFocusRequest = (LightweightFocusRequest)localIterator.next();
/*      */ 
/* 2605 */           if (!localIterator.hasNext()) {
/* 2606 */             disableRestoreFocus = false;
/*      */           }
/*      */ 
/* 2609 */           CausedFocusEvent localCausedFocusEvent1 = null;
/*      */ 
/* 2615 */           if (localComponent3 != null) {
/* 2616 */             localCausedFocusEvent1 = new CausedFocusEvent(localComponent3, 1005, localLightweightFocusRequest.temporary, localLightweightFocusRequest.component, localLightweightFocusRequest.cause);
/*      */           }
/*      */ 
/* 2621 */           CausedFocusEvent localCausedFocusEvent2 = new CausedFocusEvent(localLightweightFocusRequest.component, 1004, localLightweightFocusRequest.temporary, localComponent3 == null ? localComponent2 : localComponent3, localLightweightFocusRequest.cause);
/*      */ 
/* 2628 */           if (localComponent3 != null) {
/* 2629 */             localCausedFocusEvent1.isPosted = true;
/* 2630 */             ??? = dispatchAndCatchException((Throwable)???, localComponent3, localCausedFocusEvent1);
/*      */           }
/*      */ 
/* 2633 */           localCausedFocusEvent2.isPosted = true;
/* 2634 */           ??? = dispatchAndCatchException((Throwable)???, localLightweightFocusRequest.component, localCausedFocusEvent2);
/*      */ 
/* 2636 */           if (localKeyboardFocusManager.getGlobalFocusOwner() == localLightweightFocusRequest.component)
/* 2637 */             localComponent2 = localLightweightFocusRequest.component;
/*      */         }
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/*      */       Component localComponent2;
/*      */       Component localComponent3;
/*      */       Iterator localIterator;
/* 2642 */       clearingCurrentLightweightRequests = false;
/* 2643 */       disableRestoreFocus = false;
/* 2644 */       localLinkedList = null;
/* 2645 */       allowSyncFocusRequests = true;
/*      */     }
/* 2647 */     if ((??? instanceof RuntimeException))
/* 2648 */       throw ((RuntimeException)???);
/* 2649 */     if ((??? instanceof Error))
/* 2650 */       throw ((Error)???);
/*      */   }
/*      */ 
/*      */   static FocusEvent retargetUnexpectedFocusEvent(FocusEvent paramFocusEvent)
/*      */   {
/* 2655 */     synchronized (heavyweightRequests)
/*      */     {
/* 2660 */       if (removeFirstRequest()) {
/* 2661 */         return (FocusEvent)retargetFocusEvent(paramFocusEvent);
/*      */       }
/*      */ 
/* 2664 */       Component localComponent1 = paramFocusEvent.getComponent();
/* 2665 */       Component localComponent2 = paramFocusEvent.getOppositeComponent();
/* 2666 */       boolean bool = false;
/* 2667 */       if ((paramFocusEvent.getID() == 1005) && ((localComponent2 == null) || (isTemporary(localComponent2, localComponent1))))
/*      */       {
/* 2670 */         bool = true;
/*      */       }
/* 2672 */       return new CausedFocusEvent(localComponent1, paramFocusEvent.getID(), bool, localComponent2, CausedFocusEvent.Cause.NATIVE_SYSTEM);
/*      */     }
/*      */   }
/*      */ 
/*      */   static FocusEvent retargetFocusGained(FocusEvent paramFocusEvent)
/*      */   {
/* 2678 */     assert (paramFocusEvent.getID() == 1004);
/*      */ 
/* 2680 */     Component localComponent1 = getCurrentKeyboardFocusManager().getGlobalFocusOwner();
/*      */ 
/* 2682 */     Component localComponent2 = paramFocusEvent.getComponent();
/* 2683 */     Component localComponent3 = paramFocusEvent.getOppositeComponent();
/* 2684 */     Component localComponent4 = getHeavyweight(localComponent2);
/*      */ 
/* 2686 */     synchronized (heavyweightRequests) {
/* 2687 */       HeavyweightFocusRequest localHeavyweightFocusRequest = getFirstHWRequest();
/*      */ 
/* 2689 */       if (localHeavyweightFocusRequest == HeavyweightFocusRequest.CLEAR_GLOBAL_FOCUS_OWNER)
/*      */       {
/* 2691 */         return retargetUnexpectedFocusEvent(paramFocusEvent);
/*      */       }
/*      */ 
/* 2694 */       if ((localComponent2 != null) && (localComponent4 == null) && (localHeavyweightFocusRequest != null))
/*      */       {
/* 2698 */         if (localComponent2 == localHeavyweightFocusRequest.getFirstLightweightRequest().component)
/*      */         {
/* 2700 */           localComponent2 = localHeavyweightFocusRequest.heavyweight;
/* 2701 */           localComponent4 = localComponent2;
/*      */         }
/*      */       }
/* 2704 */       if ((localHeavyweightFocusRequest != null) && (localComponent4 == localHeavyweightFocusRequest.heavyweight))
/*      */       {
/* 2710 */         heavyweightRequests.removeFirst();
/*      */ 
/* 2712 */         LightweightFocusRequest localLightweightFocusRequest = (LightweightFocusRequest)localHeavyweightFocusRequest.lightweightRequests.removeFirst();
/*      */ 
/* 2715 */         Component localComponent5 = localLightweightFocusRequest.component;
/* 2716 */         if (localComponent1 != null)
/*      */         {
/* 2728 */           newFocusOwner = localComponent5;
/*      */         }
/*      */ 
/* 2731 */         boolean bool = (localComponent3 == null) || (isTemporary(localComponent5, localComponent3)) ? false : localLightweightFocusRequest.temporary;
/*      */ 
/* 2736 */         if (localHeavyweightFocusRequest.lightweightRequests.size() > 0) {
/* 2737 */           currentLightweightRequests = localHeavyweightFocusRequest.lightweightRequests;
/*      */ 
/* 2739 */           EventQueue.invokeLater(new Runnable() {
/*      */             public void run() {
/* 2741 */               KeyboardFocusManager.processCurrentLightweightRequests();
/*      */             }
/*      */ 
/*      */           });
/*      */         }
/*      */ 
/* 2748 */         return new CausedFocusEvent(localComponent5, 1004, bool, localComponent3, localLightweightFocusRequest.cause);
/*      */       }
/*      */ 
/* 2753 */       if ((localComponent1 != null) && (localComponent1.getContainingWindow() == localComponent2) && ((localHeavyweightFocusRequest == null) || (localComponent2 != localHeavyweightFocusRequest.heavyweight)))
/*      */       {
/* 2761 */         return new CausedFocusEvent(localComponent1, 1004, false, null, CausedFocusEvent.Cause.ACTIVATION);
/*      */       }
/*      */ 
/* 2765 */       return retargetUnexpectedFocusEvent(paramFocusEvent);
/*      */     }
/*      */   }
/*      */ 
/*      */   static FocusEvent retargetFocusLost(FocusEvent paramFocusEvent) {
/* 2770 */     assert (paramFocusEvent.getID() == 1005);
/*      */ 
/* 2772 */     Component localComponent1 = getCurrentKeyboardFocusManager().getGlobalFocusOwner();
/*      */ 
/* 2774 */     Component localComponent2 = paramFocusEvent.getOppositeComponent();
/* 2775 */     Component localComponent3 = getHeavyweight(localComponent2);
/*      */ 
/* 2777 */     synchronized (heavyweightRequests) {
/* 2778 */       HeavyweightFocusRequest localHeavyweightFocusRequest = getFirstHWRequest();
/*      */ 
/* 2780 */       if (localHeavyweightFocusRequest == HeavyweightFocusRequest.CLEAR_GLOBAL_FOCUS_OWNER)
/*      */       {
/* 2782 */         if (localComponent1 != null)
/*      */         {
/* 2784 */           heavyweightRequests.removeFirst();
/* 2785 */           return new CausedFocusEvent(localComponent1, 1005, false, null, CausedFocusEvent.Cause.CLEAR_GLOBAL_FOCUS_OWNER);
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 2792 */         if (localComponent2 == null)
/*      */         {
/* 2795 */           if (localComponent1 != null) {
/* 2796 */             return new CausedFocusEvent(localComponent1, 1005, true, null, CausedFocusEvent.Cause.ACTIVATION);
/*      */           }
/*      */ 
/* 2800 */           return paramFocusEvent;
/*      */         }
/* 2802 */         if ((localHeavyweightFocusRequest != null) && ((localComponent3 == localHeavyweightFocusRequest.heavyweight) || ((localComponent3 == null) && (localComponent2 == localHeavyweightFocusRequest.getFirstLightweightRequest().component))))
/*      */         {
/* 2807 */           if (localComponent1 == null) {
/* 2808 */             return paramFocusEvent;
/*      */           }
/*      */ 
/* 2817 */           LightweightFocusRequest localLightweightFocusRequest = (LightweightFocusRequest)localHeavyweightFocusRequest.lightweightRequests.getFirst();
/*      */ 
/* 2820 */           boolean bool = isTemporary(localComponent2, localComponent1) ? true : localLightweightFocusRequest.temporary;
/*      */ 
/* 2824 */           return new CausedFocusEvent(localComponent1, 1005, bool, localLightweightFocusRequest.component, localLightweightFocusRequest.cause);
/*      */         }
/* 2826 */         if (focusedWindowChanged(localComponent2, localComponent1))
/*      */         {
/* 2829 */           if ((!paramFocusEvent.isTemporary()) && (localComponent1 != null))
/*      */           {
/* 2831 */             paramFocusEvent = new CausedFocusEvent(localComponent1, 1005, true, localComponent2, CausedFocusEvent.Cause.ACTIVATION);
/*      */           }
/*      */ 
/* 2834 */           return paramFocusEvent;
/*      */         }
/*      */       }
/* 2837 */       return retargetUnexpectedFocusEvent(paramFocusEvent);
/*      */     }
/*      */   }
/*      */ 
/*      */   static AWTEvent retargetFocusEvent(AWTEvent paramAWTEvent) {
/* 2842 */     if (clearingCurrentLightweightRequests) {
/* 2843 */       return paramAWTEvent;
/*      */     }
/*      */ 
/* 2846 */     KeyboardFocusManager localKeyboardFocusManager = getCurrentKeyboardFocusManager();
/* 2847 */     if (focusLog.isLoggable(400)) {
/* 2848 */       if (((paramAWTEvent instanceof FocusEvent)) || ((paramAWTEvent instanceof WindowEvent))) {
/* 2849 */         focusLog.finer(">>> {0}", new Object[] { String.valueOf(paramAWTEvent) });
/*      */       }
/* 2851 */       if ((focusLog.isLoggable(400)) && ((paramAWTEvent instanceof KeyEvent))) {
/* 2852 */         focusLog.finer("    focus owner is {0}", new Object[] { String.valueOf(localKeyboardFocusManager.getGlobalFocusOwner()) });
/*      */ 
/* 2854 */         focusLog.finer(">>> {0}", new Object[] { String.valueOf(paramAWTEvent) });
/*      */       }
/*      */     }
/*      */ 
/* 2858 */     synchronized (heavyweightRequests)
/*      */     {
/* 2869 */       if ((newFocusOwner != null) && (paramAWTEvent.getID() == 1005))
/*      */       {
/* 2872 */         FocusEvent localFocusEvent = (FocusEvent)paramAWTEvent;
/*      */ 
/* 2874 */         if ((localKeyboardFocusManager.getGlobalFocusOwner() == localFocusEvent.getComponent()) && (localFocusEvent.getOppositeComponent() == newFocusOwner))
/*      */         {
/* 2877 */           newFocusOwner = null;
/* 2878 */           return paramAWTEvent;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 2883 */     processCurrentLightweightRequests();
/*      */ 
/* 2885 */     switch (paramAWTEvent.getID()) {
/*      */     case 1004:
/* 2887 */       paramAWTEvent = retargetFocusGained((FocusEvent)paramAWTEvent);
/* 2888 */       break;
/*      */     case 1005:
/* 2891 */       paramAWTEvent = retargetFocusLost((FocusEvent)paramAWTEvent);
/* 2892 */       break;
/*      */     }
/*      */ 
/* 2897 */     return paramAWTEvent;
/*      */   }
/*      */ 
/*      */   void clearMarkers()
/*      */   {
/*      */   }
/*      */ 
/*      */   static boolean removeFirstRequest()
/*      */   {
/* 2910 */     KeyboardFocusManager localKeyboardFocusManager = getCurrentKeyboardFocusManager();
/*      */ 
/* 2913 */     synchronized (heavyweightRequests) {
/* 2914 */       HeavyweightFocusRequest localHeavyweightFocusRequest = getFirstHWRequest();
/*      */ 
/* 2916 */       if (localHeavyweightFocusRequest != null) {
/* 2917 */         heavyweightRequests.removeFirst();
/* 2918 */         if (localHeavyweightFocusRequest.lightweightRequests != null) {
/* 2919 */           Iterator localIterator = localHeavyweightFocusRequest.lightweightRequests.iterator();
/*      */ 
/* 2921 */           while (localIterator.hasNext())
/*      */           {
/* 2923 */             localKeyboardFocusManager.dequeueKeyEvents(-1L, ((LightweightFocusRequest)localIterator.next()).component);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2931 */       if (heavyweightRequests.size() == 0) {
/* 2932 */         localKeyboardFocusManager.clearMarkers();
/*      */       }
/* 2934 */       return heavyweightRequests.size() > 0;
/*      */     }
/*      */   }
/*      */ 
/* 2938 */   static void removeLastFocusRequest(Component paramComponent) { if ((log.isLoggable(500)) && 
/* 2939 */       (paramComponent == null)) {
/* 2940 */       log.fine("Assertion (heavyweight != null) failed");
/*      */     }
/*      */ 
/* 2944 */     KeyboardFocusManager localKeyboardFocusManager = getCurrentKeyboardFocusManager();
/*      */ 
/* 2946 */     synchronized (heavyweightRequests) {
/* 2947 */       HeavyweightFocusRequest localHeavyweightFocusRequest = getLastHWRequest();
/* 2948 */       if ((localHeavyweightFocusRequest != null) && (localHeavyweightFocusRequest.heavyweight == paramComponent))
/*      */       {
/* 2950 */         heavyweightRequests.removeLast();
/*      */       }
/*      */ 
/* 2954 */       if (heavyweightRequests.size() == 0)
/* 2955 */         localKeyboardFocusManager.clearMarkers();
/*      */     }
/*      */   }
/*      */ 
/*      */   private static boolean focusedWindowChanged(Component paramComponent1, Component paramComponent2)
/*      */   {
/* 2961 */     Window localWindow1 = SunToolkit.getContainingWindow(paramComponent1);
/* 2962 */     Window localWindow2 = SunToolkit.getContainingWindow(paramComponent2);
/* 2963 */     if ((localWindow1 == null) && (localWindow2 == null)) {
/* 2964 */       return true;
/*      */     }
/* 2966 */     if (localWindow1 == null) {
/* 2967 */       return true;
/*      */     }
/* 2969 */     if (localWindow2 == null) {
/* 2970 */       return true;
/*      */     }
/* 2972 */     return localWindow1 != localWindow2;
/*      */   }
/*      */ 
/*      */   private static boolean isTemporary(Component paramComponent1, Component paramComponent2) {
/* 2976 */     Window localWindow1 = SunToolkit.getContainingWindow(paramComponent1);
/* 2977 */     Window localWindow2 = SunToolkit.getContainingWindow(paramComponent2);
/* 2978 */     if ((localWindow1 == null) && (localWindow2 == null)) {
/* 2979 */       return false;
/*      */     }
/* 2981 */     if (localWindow1 == null) {
/* 2982 */       return true;
/*      */     }
/* 2984 */     if (localWindow2 == null) {
/* 2985 */       return false;
/*      */     }
/* 2987 */     return localWindow1 != localWindow2;
/*      */   }
/*      */ 
/*      */   static Component getHeavyweight(Component paramComponent) {
/* 2991 */     if ((paramComponent == null) || (paramComponent.getPeer() == null))
/* 2992 */       return null;
/* 2993 */     if ((paramComponent.getPeer() instanceof LightweightPeer)) {
/* 2994 */       return paramComponent.getNativeContainer();
/*      */     }
/* 2996 */     return paramComponent;
/*      */   }
/*      */ 
/*      */   private static boolean isProxyActiveImpl(KeyEvent paramKeyEvent)
/*      */   {
/* 3003 */     if (proxyActive == null) {
/* 3004 */       proxyActive = (Field)AccessController.doPrivileged(new PrivilegedAction() {
/*      */         public Object run() {
/* 3006 */           Field localField = null;
/*      */           try {
/* 3008 */             localField = KeyEvent.class.getDeclaredField("isProxyActive");
/* 3009 */             if (localField != null)
/* 3010 */               localField.setAccessible(true);
/*      */           }
/*      */           catch (NoSuchFieldException localNoSuchFieldException) {
/* 3013 */             if (!$assertionsDisabled) throw new AssertionError();
/*      */           }
/* 3015 */           return localField;
/*      */         }
/*      */       });
/*      */     }
/*      */     try
/*      */     {
/* 3021 */       return proxyActive.getBoolean(paramKeyEvent);
/*      */     } catch (IllegalAccessException localIllegalAccessException) {
/* 3023 */       if (!$assertionsDisabled) throw new AssertionError();
/*      */     }
/* 3025 */     return false;
/*      */   }
/*      */ 
/*      */   static boolean isProxyActive(KeyEvent paramKeyEvent)
/*      */   {
/* 3030 */     if (!GraphicsEnvironment.isHeadless()) {
/* 3031 */       return isProxyActiveImpl(paramKeyEvent);
/*      */     }
/* 3033 */     return false;
/*      */   }
/*      */ 
/*      */   private static HeavyweightFocusRequest getLastHWRequest()
/*      */   {
/* 3038 */     synchronized (heavyweightRequests) {
/* 3039 */       return heavyweightRequests.size() > 0 ? (HeavyweightFocusRequest)heavyweightRequests.getLast() : null;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static HeavyweightFocusRequest getFirstHWRequest()
/*      */   {
/* 3046 */     synchronized (heavyweightRequests) {
/* 3047 */       return heavyweightRequests.size() > 0 ? (HeavyweightFocusRequest)heavyweightRequests.getFirst() : null;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void checkCurrentKFMSecurity()
/*      */   {
/* 3054 */     if (this != getCurrentKeyboardFocusManager()) {
/* 3055 */       if (focusLog.isLoggable(400)) {
/* 3056 */         focusLog.finer("This manager is " + this + ", current is " + getCurrentKeyboardFocusManager());
/*      */       }
/*      */ 
/* 3059 */       throw new SecurityException("this KeyboardFocusManager is not installed in the current thread's context");
/*      */     }
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  112 */     focusLog = PlatformLogger.getLogger("java.awt.focus.KeyboardFocusManager");
/*      */ 
/*  116 */     Toolkit.loadLibraries();
/*  117 */     if (!GraphicsEnvironment.isHeadless()) {
/*  118 */       initIDs();
/*      */     }
/*  120 */     AWTAccessor.setKeyboardFocusManagerAccessor(new AWTAccessor.KeyboardFocusManagerAccessor()
/*      */     {
/*      */       public int shouldNativelyFocusHeavyweight(Component paramAnonymousComponent1, Component paramAnonymousComponent2, boolean paramAnonymousBoolean1, boolean paramAnonymousBoolean2, long paramAnonymousLong, CausedFocusEvent.Cause paramAnonymousCause)
/*      */       {
/*  129 */         return KeyboardFocusManager.shouldNativelyFocusHeavyweight(paramAnonymousComponent1, paramAnonymousComponent2, paramAnonymousBoolean1, paramAnonymousBoolean2, paramAnonymousLong, paramAnonymousCause);
/*      */       }
/*      */ 
/*      */       public boolean processSynchronousLightweightTransfer(Component paramAnonymousComponent1, Component paramAnonymousComponent2, boolean paramAnonymousBoolean1, boolean paramAnonymousBoolean2, long paramAnonymousLong)
/*      */       {
/*  138 */         return KeyboardFocusManager.processSynchronousLightweightTransfer(paramAnonymousComponent1, paramAnonymousComponent2, paramAnonymousBoolean1, paramAnonymousBoolean2, paramAnonymousLong);
/*      */       }
/*      */ 
/*      */       public void removeLastFocusRequest(Component paramAnonymousComponent) {
/*  142 */         KeyboardFocusManager.removeLastFocusRequest(paramAnonymousComponent);
/*      */       }
/*      */       public void setMostRecentFocusOwner(Window paramAnonymousWindow, Component paramAnonymousComponent) {
/*  145 */         KeyboardFocusManager.setMostRecentFocusOwner(paramAnonymousWindow, paramAnonymousComponent);
/*      */       }
/*      */       public KeyboardFocusManager getCurrentKeyboardFocusManager(AppContext paramAnonymousAppContext) {
/*  148 */         return KeyboardFocusManager.getCurrentKeyboardFocusManager(paramAnonymousAppContext);
/*      */       }
/*      */       public Container getCurrentFocusCycleRoot() {
/*  151 */         return KeyboardFocusManager.currentFocusCycleRoot;
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   private static final class HeavyweightFocusRequest
/*      */   {
/*      */     final Component heavyweight;
/*      */     final LinkedList<KeyboardFocusManager.LightweightFocusRequest> lightweightRequests;
/* 2131 */     static final HeavyweightFocusRequest CLEAR_GLOBAL_FOCUS_OWNER = new HeavyweightFocusRequest();
/*      */ 
/*      */     private HeavyweightFocusRequest()
/*      */     {
/* 2135 */       this.heavyweight = null;
/* 2136 */       this.lightweightRequests = null;
/*      */     }
/*      */ 
/*      */     HeavyweightFocusRequest(Component paramComponent1, Component paramComponent2, boolean paramBoolean, CausedFocusEvent.Cause paramCause)
/*      */     {
/* 2141 */       if ((KeyboardFocusManager.log.isLoggable(500)) && 
/* 2142 */         (paramComponent1 == null)) {
/* 2143 */         KeyboardFocusManager.log.fine("Assertion (heavyweight != null) failed");
/*      */       }
/*      */ 
/* 2147 */       this.heavyweight = paramComponent1;
/* 2148 */       this.lightweightRequests = new LinkedList();
/* 2149 */       addLightweightRequest(paramComponent2, paramBoolean, paramCause);
/*      */     }
/*      */ 
/*      */     boolean addLightweightRequest(Component paramComponent, boolean paramBoolean, CausedFocusEvent.Cause paramCause) {
/* 2153 */       if (KeyboardFocusManager.log.isLoggable(500)) {
/* 2154 */         if (this == CLEAR_GLOBAL_FOCUS_OWNER) {
/* 2155 */           KeyboardFocusManager.log.fine("Assertion (this != HeavyweightFocusRequest.CLEAR_GLOBAL_FOCUS_OWNER) failed");
/*      */         }
/* 2157 */         if (paramComponent == null) {
/* 2158 */           KeyboardFocusManager.log.fine("Assertion (descendant != null) failed");
/*      */         }
/*      */       }
/*      */ 
/* 2162 */       Object localObject = this.lightweightRequests.size() > 0 ? ((KeyboardFocusManager.LightweightFocusRequest)this.lightweightRequests.getLast()).component : null;
/*      */ 
/* 2166 */       if (paramComponent != localObject)
/*      */       {
/* 2168 */         this.lightweightRequests.add(new KeyboardFocusManager.LightweightFocusRequest(paramComponent, paramBoolean, paramCause));
/*      */ 
/* 2170 */         return true;
/*      */       }
/* 2172 */       return false;
/*      */     }
/*      */ 
/*      */     KeyboardFocusManager.LightweightFocusRequest getFirstLightweightRequest()
/*      */     {
/* 2177 */       if (this == CLEAR_GLOBAL_FOCUS_OWNER) {
/* 2178 */         return null;
/*      */       }
/* 2180 */       return (KeyboardFocusManager.LightweightFocusRequest)this.lightweightRequests.getFirst();
/*      */     }
/*      */     public String toString() {
/* 2183 */       int i = 1;
/* 2184 */       String str = "HeavyweightFocusRequest[heavweight=" + this.heavyweight + ",lightweightRequests=";
/*      */ 
/* 2186 */       if (this.lightweightRequests == null) {
/* 2187 */         str = str + null;
/*      */       } else {
/* 2189 */         str = str + "[";
/*      */ 
/* 2191 */         for (KeyboardFocusManager.LightweightFocusRequest localLightweightFocusRequest : this.lightweightRequests) {
/* 2192 */           if (i != 0)
/* 2193 */             i = 0;
/*      */           else {
/* 2195 */             str = str + ",";
/*      */           }
/* 2197 */           str = str + localLightweightFocusRequest;
/*      */         }
/* 2199 */         str = str + "]";
/*      */       }
/* 2201 */       str = str + "]";
/* 2202 */       return str;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static final class LightweightFocusRequest
/*      */   {
/*      */     final Component component;
/*      */     final boolean temporary;
/*      */     final CausedFocusEvent.Cause cause;
/*      */ 
/*      */     LightweightFocusRequest(Component paramComponent, boolean paramBoolean, CausedFocusEvent.Cause paramCause)
/*      */     {
/* 2117 */       this.component = paramComponent;
/* 2118 */       this.temporary = paramBoolean;
/* 2119 */       this.cause = paramCause;
/*      */     }
/*      */     public String toString() {
/* 2122 */       return "LightweightFocusRequest[component=" + this.component + ",temporary=" + this.temporary + ", cause=" + this.cause + "]";
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.KeyboardFocusManager
 * JD-Core Version:    0.6.2
 */