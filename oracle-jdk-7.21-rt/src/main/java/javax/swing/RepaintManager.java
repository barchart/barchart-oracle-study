/*      */ package javax.swing;
/*      */ 
/*      */ import com.sun.java.swing.SwingUtilities3;
/*      */ import java.applet.Applet;
/*      */ import java.awt.Component;
/*      */ import java.awt.Container;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.EventQueue;
/*      */ import java.awt.Frame;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.GraphicsConfiguration;
/*      */ import java.awt.GraphicsDevice;
/*      */ import java.awt.GraphicsEnvironment;
/*      */ import java.awt.HeadlessException;
/*      */ import java.awt.Image;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.Toolkit;
/*      */ import java.awt.Window;
/*      */ import java.awt.event.InvocationEvent;
/*      */ import java.awt.image.VolatileImage;
/*      */ import java.security.AccessControlContext;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.IdentityHashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.atomic.AtomicInteger;
/*      */ import sun.awt.AWTAccessor;
/*      */ import sun.awt.AWTAccessor.ComponentAccessor;
/*      */ import sun.awt.AWTAccessor.WindowAccessor;
/*      */ import sun.awt.AppContext;
/*      */ import sun.awt.DisplayChangedListener;
/*      */ import sun.awt.SunToolkit;
/*      */ import sun.java2d.SunGraphicsEnvironment;
/*      */ import sun.misc.JavaSecurityAccess;
/*      */ import sun.misc.SharedSecrets;
/*      */ import sun.security.action.GetPropertyAction;
/*      */ 
/*      */ public class RepaintManager
/*      */ {
/*      */   static final boolean HANDLE_TOP_LEVEL_PAINT;
/*      */   private static final short BUFFER_STRATEGY_NOT_SPECIFIED = 0;
/*      */   private static final short BUFFER_STRATEGY_SPECIFIED_ON = 1;
/*      */   private static final short BUFFER_STRATEGY_SPECIFIED_OFF = 2;
/*      */   private static final short BUFFER_STRATEGY_TYPE;
/*   79 */   private Map<GraphicsConfiguration, VolatileImage> volatileMap = new HashMap(1);
/*      */   private Map<Container, Rectangle> hwDirtyComponents;
/*      */   private Map<Component, Rectangle> dirtyComponents;
/*      */   private Map<Component, Rectangle> tmpDirtyComponents;
/*      */   private List<Component> invalidComponents;
/*      */   private List<Runnable> runnableList;
/*  102 */   boolean doubleBufferingEnabled = true;
/*      */   private Dimension doubleBufferMaxSize;
/*      */   DoubleBufferInfo standardDoubleBuffer;
/*      */   private PaintManager paintManager;
/*  120 */   private static final Object repaintManagerKey = RepaintManager.class;
/*      */ 
/*  123 */   static boolean volatileImageBufferEnabled = true;
/*      */   private static boolean nativeDoubleBuffering;
/*      */   private static final int VOLATILE_LOOP_MAX = 2;
/*  136 */   private int paintDepth = 0;
/*      */   private short bufferStrategyType;
/*      */   private boolean painting;
/*      */   private JComponent repaintRoot;
/*      */   private Thread paintThread;
/*      */   private final ProcessingRunnable processingRunnable;
/*  177 */   private static final JavaSecurityAccess javaSecurityAccess = SharedSecrets.getJavaSecurityAccess();
/*      */ 
/*  860 */   Rectangle tmp = new Rectangle();
/*      */ 
/*      */   public static RepaintManager currentManager(Component paramComponent)
/*      */   {
/*  228 */     return currentManager(AppContext.getAppContext());
/*      */   }
/*      */ 
/*      */   static RepaintManager currentManager(AppContext paramAppContext)
/*      */   {
/*  237 */     RepaintManager localRepaintManager = (RepaintManager)paramAppContext.get(repaintManagerKey);
/*  238 */     if (localRepaintManager == null) {
/*  239 */       localRepaintManager = new RepaintManager(BUFFER_STRATEGY_TYPE);
/*  240 */       paramAppContext.put(repaintManagerKey, localRepaintManager);
/*      */     }
/*  242 */     return localRepaintManager;
/*      */   }
/*      */ 
/*      */   public static RepaintManager currentManager(JComponent paramJComponent)
/*      */   {
/*  256 */     return currentManager(paramJComponent);
/*      */   }
/*      */ 
/*      */   public static void setCurrentManager(RepaintManager paramRepaintManager)
/*      */   {
/*  267 */     if (paramRepaintManager != null)
/*  268 */       SwingUtilities.appContextPut(repaintManagerKey, paramRepaintManager);
/*      */     else
/*  270 */       SwingUtilities.appContextRemove(repaintManagerKey);
/*      */   }
/*      */ 
/*      */   public RepaintManager()
/*      */   {
/*  284 */     this((short)2);
/*      */   }
/*      */ 
/*      */   private RepaintManager(short paramShort)
/*      */   {
/*  290 */     this.doubleBufferingEnabled = (!nativeDoubleBuffering);
/*  291 */     synchronized (this) {
/*  292 */       this.dirtyComponents = new IdentityHashMap();
/*  293 */       this.tmpDirtyComponents = new IdentityHashMap();
/*  294 */       this.bufferStrategyType = paramShort;
/*  295 */       this.hwDirtyComponents = new IdentityHashMap();
/*      */     }
/*  297 */     this.processingRunnable = new ProcessingRunnable(null);
/*      */   }
/*      */ 
/*      */   private void displayChanged() {
/*  301 */     clearImages();
/*      */   }
/*      */ 
/*      */   public synchronized void addInvalidComponent(JComponent paramJComponent)
/*      */   {
/*  314 */     RepaintManager localRepaintManager = getDelegate(paramJComponent);
/*  315 */     if (localRepaintManager != null) {
/*  316 */       localRepaintManager.addInvalidComponent(paramJComponent);
/*  317 */       return;
/*      */     }
/*  319 */     Container localContainer = SwingUtilities.getValidateRoot(paramJComponent, true);
/*      */ 
/*  322 */     if (localContainer == null) {
/*  323 */       return;
/*      */     }
/*      */ 
/*  330 */     if (this.invalidComponents == null) {
/*  331 */       this.invalidComponents = new ArrayList();
/*      */     }
/*      */     else {
/*  334 */       int i = this.invalidComponents.size();
/*  335 */       for (int j = 0; j < i; j++) {
/*  336 */         if (localContainer == this.invalidComponents.get(j)) {
/*  337 */           return;
/*      */         }
/*      */       }
/*      */     }
/*  341 */     this.invalidComponents.add(localContainer);
/*      */ 
/*  345 */     scheduleProcessingRunnable();
/*      */   }
/*      */ 
/*      */   public synchronized void removeInvalidComponent(JComponent paramJComponent)
/*      */   {
/*  355 */     RepaintManager localRepaintManager = getDelegate(paramJComponent);
/*  356 */     if (localRepaintManager != null) {
/*  357 */       localRepaintManager.removeInvalidComponent(paramJComponent);
/*  358 */       return;
/*      */     }
/*  360 */     if (this.invalidComponents != null) {
/*  361 */       int i = this.invalidComponents.indexOf(paramJComponent);
/*  362 */       if (i != -1)
/*  363 */         this.invalidComponents.remove(i);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void addDirtyRegion0(Container paramContainer, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  379 */     if ((paramInt3 <= 0) || (paramInt4 <= 0) || (paramContainer == null)) {
/*  380 */       return;
/*      */     }
/*      */ 
/*  383 */     if ((paramContainer.getWidth() <= 0) || (paramContainer.getHeight() <= 0)) {
/*  384 */       return;
/*      */     }
/*      */ 
/*  387 */     if (extendDirtyRegion(paramContainer, paramInt1, paramInt2, paramInt3, paramInt4))
/*      */     {
/*  390 */       return;
/*      */     }
/*      */ 
/*  399 */     Object localObject1 = null;
/*      */ 
/*  405 */     for (Container localContainer = paramContainer; localContainer != null; localContainer = localContainer.getParent()) {
/*  406 */       if ((!localContainer.isVisible()) || (localContainer.getPeer() == null)) {
/*  407 */         return;
/*      */       }
/*  409 */       if (((localContainer instanceof Window)) || ((localContainer instanceof Applet)))
/*      */       {
/*  411 */         if (((localContainer instanceof Frame)) && ((((Frame)localContainer).getExtendedState() & 0x1) == 1))
/*      */         {
/*  414 */           return;
/*      */         }
/*  416 */         localObject1 = localContainer;
/*  417 */         break;
/*      */       }
/*      */     }
/*      */ 
/*  421 */     if (localObject1 == null) return;
/*      */ 
/*  423 */     synchronized (this) {
/*  424 */       if (extendDirtyRegion(paramContainer, paramInt1, paramInt2, paramInt3, paramInt4))
/*      */       {
/*  427 */         return;
/*      */       }
/*  429 */       this.dirtyComponents.put(paramContainer, new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4));
/*      */     }
/*      */ 
/*  434 */     scheduleProcessingRunnable();
/*      */   }
/*      */ 
/*      */   public void addDirtyRegion(JComponent paramJComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  451 */     RepaintManager localRepaintManager = getDelegate(paramJComponent);
/*  452 */     if (localRepaintManager != null) {
/*  453 */       localRepaintManager.addDirtyRegion(paramJComponent, paramInt1, paramInt2, paramInt3, paramInt4);
/*  454 */       return;
/*      */     }
/*  456 */     addDirtyRegion0(paramJComponent, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */   }
/*      */ 
/*      */   public void addDirtyRegion(Window paramWindow, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  474 */     addDirtyRegion0(paramWindow, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */   }
/*      */ 
/*      */   public void addDirtyRegion(Applet paramApplet, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  490 */     addDirtyRegion0(paramApplet, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */   }
/*      */ 
/*      */   void scheduleHeavyWeightPaints()
/*      */   {
/*      */     Map localMap;
/*  496 */     synchronized (this) {
/*  497 */       if (this.hwDirtyComponents.size() == 0) {
/*  498 */         return;
/*      */       }
/*  500 */       localMap = this.hwDirtyComponents;
/*  501 */       this.hwDirtyComponents = new IdentityHashMap();
/*      */     }
/*  503 */     for (??? = localMap.keySet().iterator(); ((Iterator)???).hasNext(); ) { Container localContainer = (Container)((Iterator)???).next();
/*  504 */       Rectangle localRectangle = (Rectangle)localMap.get(localContainer);
/*  505 */       if ((localContainer instanceof Window)) {
/*  506 */         addDirtyRegion((Window)localContainer, localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height);
/*      */       }
/*  509 */       else if ((localContainer instanceof Applet)) {
/*  510 */         addDirtyRegion((Applet)localContainer, localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height);
/*      */       }
/*      */       else
/*      */       {
/*  514 */         addDirtyRegion0(localContainer, localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   void nativeAddDirtyRegion(AppContext paramAppContext, Container paramContainer, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  526 */     if ((paramInt3 > 0) && (paramInt4 > 0)) {
/*  527 */       synchronized (this) {
/*  528 */         Rectangle localRectangle = (Rectangle)this.hwDirtyComponents.get(paramContainer);
/*  529 */         if (localRectangle == null) {
/*  530 */           this.hwDirtyComponents.put(paramContainer, new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4));
/*      */         }
/*      */         else {
/*  533 */           this.hwDirtyComponents.put(paramContainer, SwingUtilities.computeUnion(paramInt1, paramInt2, paramInt3, paramInt4, localRectangle));
/*      */         }
/*      */       }
/*      */ 
/*  537 */       scheduleProcessingRunnable(paramAppContext);
/*      */     }
/*      */   }
/*      */ 
/*      */   void nativeQueueSurfaceDataRunnable(AppContext paramAppContext, final Component paramComponent, final Runnable paramRunnable)
/*      */   {
/*  548 */     synchronized (this) {
/*  549 */       if (this.runnableList == null) {
/*  550 */         this.runnableList = new LinkedList();
/*      */       }
/*  552 */       this.runnableList.add(new Runnable() {
/*      */         public void run() {
/*  554 */           AccessControlContext localAccessControlContext1 = AccessController.getContext();
/*  555 */           AccessControlContext localAccessControlContext2 = AWTAccessor.getComponentAccessor().getAccessControlContext(paramComponent);
/*      */ 
/*  557 */           RepaintManager.javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction() {
/*      */             public Void run() {
/*  559 */               RepaintManager.1.this.val$r.run();
/*  560 */               return null;
/*      */             }
/*      */           }
/*      */           , localAccessControlContext1, localAccessControlContext2);
/*      */         }
/*      */ 
/*      */       });
/*      */     }
/*      */ 
/*  566 */     scheduleProcessingRunnable(paramAppContext);
/*      */   }
/*      */ 
/*      */   private synchronized boolean extendDirtyRegion(Component paramComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  577 */     Rectangle localRectangle = (Rectangle)this.dirtyComponents.get(paramComponent);
/*  578 */     if (localRectangle != null)
/*      */     {
/*  582 */       SwingUtilities.computeUnion(paramInt1, paramInt2, paramInt3, paramInt4, localRectangle);
/*  583 */       return true;
/*      */     }
/*  585 */     return false;
/*      */   }
/*      */ 
/*      */   public Rectangle getDirtyRegion(JComponent paramJComponent)
/*      */   {
/*  593 */     RepaintManager localRepaintManager = getDelegate(paramJComponent);
/*  594 */     if (localRepaintManager != null)
/*  595 */       return localRepaintManager.getDirtyRegion(paramJComponent);
/*      */     Rectangle localRectangle;
/*  598 */     synchronized (this) {
/*  599 */       localRectangle = (Rectangle)this.dirtyComponents.get(paramJComponent);
/*      */     }
/*  601 */     if (localRectangle == null) {
/*  602 */       return new Rectangle(0, 0, 0, 0);
/*      */     }
/*  604 */     return new Rectangle(localRectangle);
/*      */   }
/*      */ 
/*      */   public void markCompletelyDirty(JComponent paramJComponent)
/*      */   {
/*  612 */     RepaintManager localRepaintManager = getDelegate(paramJComponent);
/*  613 */     if (localRepaintManager != null) {
/*  614 */       localRepaintManager.markCompletelyDirty(paramJComponent);
/*  615 */       return;
/*      */     }
/*  617 */     addDirtyRegion(paramJComponent, 0, 0, 2147483647, 2147483647);
/*      */   }
/*      */ 
/*      */   public void markCompletelyClean(JComponent paramJComponent)
/*      */   {
/*  625 */     RepaintManager localRepaintManager = getDelegate(paramJComponent);
/*  626 */     if (localRepaintManager != null) {
/*  627 */       localRepaintManager.markCompletelyClean(paramJComponent);
/*  628 */       return;
/*      */     }
/*  630 */     synchronized (this) {
/*  631 */       this.dirtyComponents.remove(paramJComponent);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isCompletelyDirty(JComponent paramJComponent)
/*      */   {
/*  642 */     RepaintManager localRepaintManager = getDelegate(paramJComponent);
/*  643 */     if (localRepaintManager != null) {
/*  644 */       return localRepaintManager.isCompletelyDirty(paramJComponent);
/*      */     }
/*      */ 
/*  648 */     Rectangle localRectangle = getDirtyRegion(paramJComponent);
/*  649 */     if ((localRectangle.width == 2147483647) && (localRectangle.height == 2147483647))
/*      */     {
/*  651 */       return true;
/*      */     }
/*  653 */     return false;
/*      */   }
/*      */ 
/*      */   public void validateInvalidComponents()
/*      */   {
/*      */     List localList;
/*  663 */     synchronized (this) {
/*  664 */       if (this.invalidComponents == null) {
/*  665 */         return;
/*      */       }
/*  667 */       localList = this.invalidComponents;
/*  668 */       this.invalidComponents = null;
/*      */     }
/*  670 */     ??? = localList.size();
/*  671 */     for (Object localObject2 = 0; localObject2 < ???; localObject2++) {
/*  672 */       final Component localComponent = (Component)localList.get(localObject2);
/*  673 */       AccessControlContext localAccessControlContext1 = AccessController.getContext();
/*  674 */       AccessControlContext localAccessControlContext2 = AWTAccessor.getComponentAccessor().getAccessControlContext(localComponent);
/*      */ 
/*  676 */       javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction()
/*      */       {
/*      */         public Void run() {
/*  679 */           localComponent.validate();
/*  680 */           return null;
/*      */         }
/*      */       }
/*      */       , localAccessControlContext1, localAccessControlContext2);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void prePaintDirtyRegions()
/*      */   {
/*      */     Map localMap;
/*      */     List localList;
/*  696 */     synchronized (this) {
/*  697 */       localMap = this.dirtyComponents;
/*  698 */       localList = this.runnableList;
/*  699 */       this.runnableList = null;
/*      */     }
/*  701 */     if (localList != null) {
/*  702 */       for (??? = localList.iterator(); ((Iterator)???).hasNext(); ) { Runnable localRunnable = (Runnable)((Iterator)???).next();
/*  703 */         localRunnable.run();
/*      */       }
/*      */     }
/*  706 */     paintDirtyRegions();
/*  707 */     if (localMap.size() > 0)
/*      */     {
/*  710 */       paintDirtyRegions(localMap);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void updateWindows(Map<Component, Rectangle> paramMap) {
/*  715 */     Toolkit localToolkit = Toolkit.getDefaultToolkit();
/*  716 */     if ((!(localToolkit instanceof SunToolkit)) || (!((SunToolkit)localToolkit).needUpdateWindow()))
/*      */     {
/*  719 */       return;
/*      */     }
/*      */ 
/*  722 */     HashSet localHashSet = new HashSet();
/*  723 */     Set localSet = paramMap.keySet();
/*  724 */     for (Iterator localIterator = localSet.iterator(); localIterator.hasNext(); ) {
/*  725 */       localObject = (Component)localIterator.next();
/*  726 */       Window localWindow = (localObject instanceof Window) ? (Window)localObject : SwingUtilities.getWindowAncestor((Component)localObject);
/*      */ 
/*  729 */       if ((localWindow != null) && (!localWindow.isOpaque()))
/*      */       {
/*  732 */         localHashSet.add(localWindow);
/*      */       }
/*      */     }
/*  736 */     Object localObject;
/*  736 */     for (localIterator = localHashSet.iterator(); localIterator.hasNext(); ) { localObject = (Window)localIterator.next();
/*  737 */       AWTAccessor.getWindowAccessor().updateWindow((Window)localObject); }
/*      */   }
/*      */ 
/*      */   boolean isPainting()
/*      */   {
/*  742 */     return this.painting;
/*      */   }
/*      */ 
/*      */   public void paintDirtyRegions()
/*      */   {
/*  751 */     synchronized (this) {
/*  752 */       Map localMap = this.tmpDirtyComponents;
/*  753 */       this.tmpDirtyComponents = this.dirtyComponents;
/*  754 */       this.dirtyComponents = localMap;
/*  755 */       this.dirtyComponents.clear();
/*      */     }
/*  757 */     paintDirtyRegions(this.tmpDirtyComponents);
/*      */   }
/*      */ 
/*      */   private void paintDirtyRegions(final Map<Component, Rectangle> paramMap)
/*      */   {
/*  763 */     if (paramMap.isEmpty()) {
/*  764 */       return;
/*      */     }
/*      */ 
/*  767 */     final ArrayList localArrayList = new ArrayList(paramMap.size());
/*      */ 
/*  770 */     for (Object localObject1 = paramMap.keySet().iterator(); ((Iterator)localObject1).hasNext(); ) { Component localComponent1 = (Component)((Iterator)localObject1).next();
/*  771 */       collectDirtyComponents(paramMap, localComponent1, localArrayList);
/*      */     }
/*      */ 
/*  774 */     localObject1 = new AtomicInteger(localArrayList.size());
/*  775 */     this.painting = true;
/*      */     try {
/*  777 */       for (int i = 0; i < ((AtomicInteger)localObject1).get(); i++) {
/*  778 */         final int j = i;
/*  779 */         final Component localComponent2 = (Component)localArrayList.get(i);
/*      */ 
/*  781 */         AccessControlContext localAccessControlContext1 = AccessController.getContext();
/*  782 */         AccessControlContext localAccessControlContext2 = AWTAccessor.getComponentAccessor().getAccessControlContext(localComponent2);
/*      */ 
/*  784 */         javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction() {
/*      */           public Void run() {
/*  786 */             Rectangle localRectangle = (Rectangle)paramMap.get(localComponent2);
/*      */ 
/*  788 */             int i = localComponent2.getHeight();
/*  789 */             int j = localComponent2.getWidth();
/*  790 */             SwingUtilities.computeIntersection(0, 0, j, i, localRectangle);
/*      */ 
/*  795 */             if ((localComponent2 instanceof JComponent)) {
/*  796 */               ((JComponent)localComponent2).paintImmediately(localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height);
/*      */             }
/*  799 */             else if (localComponent2.isShowing()) {
/*  800 */               Graphics localGraphics = JComponent.safelyGetGraphics(localComponent2, localComponent2);
/*      */ 
/*  804 */               if (localGraphics != null) {
/*  805 */                 localGraphics.setClip(localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height);
/*      */                 try {
/*  807 */                   localComponent2.paint(localGraphics);
/*      */                 } finally {
/*  809 */                   localGraphics.dispose();
/*      */                 }
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/*  815 */             if (RepaintManager.this.repaintRoot != null) {
/*  816 */               RepaintManager.this.adjustRoots(RepaintManager.this.repaintRoot, localArrayList, j + 1);
/*  817 */               this.val$count.set(localArrayList.size());
/*  818 */               RepaintManager.this.paintManager.isRepaintingRoot = true;
/*  819 */               RepaintManager.this.repaintRoot.paintImmediately(0, 0, RepaintManager.this.repaintRoot.getWidth(), RepaintManager.this.repaintRoot.getHeight());
/*      */ 
/*  821 */               RepaintManager.this.paintManager.isRepaintingRoot = false;
/*      */ 
/*  823 */               RepaintManager.this.repaintRoot = null;
/*      */             }
/*      */ 
/*  826 */             return null;
/*      */           }
/*      */         }
/*      */         , localAccessControlContext1, localAccessControlContext2);
/*      */       }
/*      */ 
/*      */     }
/*      */     finally
/*      */     {
/*  831 */       this.painting = false;
/*      */     }
/*      */ 
/*  834 */     updateWindows(paramMap);
/*      */ 
/*  836 */     paramMap.clear();
/*      */   }
/*      */ 
/*      */   private void adjustRoots(JComponent paramJComponent, List<Component> paramList, int paramInt)
/*      */   {
/*  846 */     for (int i = paramList.size() - 1; i >= paramInt; i--) {
/*  847 */       Object localObject = (Component)paramList.get(i);
/*      */ 
/*  849 */       while ((localObject != paramJComponent) && (localObject != null) && ((localObject instanceof JComponent)))
/*      */       {
/*  852 */         localObject = ((Component)localObject).getParent();
/*      */       }
/*  854 */       if (localObject == paramJComponent)
/*  855 */         paramList.remove(i);
/*      */     }
/*      */   }
/*      */ 
/*      */   void collectDirtyComponents(Map<Component, Rectangle> paramMap, Component paramComponent, List<Component> paramList)
/*      */   {
/*      */     Object localObject2;
/*  875 */     Object localObject1 = localObject2 = paramComponent;
/*      */ 
/*  877 */     int n = paramComponent.getX();
/*  878 */     int i1 = paramComponent.getY();
/*  879 */     int i2 = paramComponent.getWidth();
/*  880 */     int i3 = paramComponent.getHeight();
/*      */     int k;
/*  882 */     int i = k = 0;
/*      */     int m;
/*  883 */     int j = m = 0;
/*  884 */     this.tmp.setBounds((Rectangle)paramMap.get(paramComponent));
/*      */ 
/*  888 */     SwingUtilities.computeIntersection(0, 0, i2, i3, this.tmp);
/*      */ 
/*  890 */     if (this.tmp.isEmpty())
/*      */     {
/*  892 */       return;
/*      */     }
/*      */ 
/*  896 */     while ((localObject1 instanceof JComponent))
/*      */     {
/*  899 */       Container localContainer = ((Component)localObject1).getParent();
/*  900 */       if (localContainer == null) {
/*      */         break;
/*      */       }
/*  903 */       localObject1 = localContainer;
/*      */ 
/*  905 */       i += n;
/*  906 */       j += i1;
/*  907 */       this.tmp.setLocation(this.tmp.x + n, this.tmp.y + i1);
/*      */ 
/*  909 */       n = ((Component)localObject1).getX();
/*  910 */       i1 = ((Component)localObject1).getY();
/*  911 */       i2 = ((Component)localObject1).getWidth();
/*  912 */       i3 = ((Component)localObject1).getHeight();
/*  913 */       this.tmp = SwingUtilities.computeIntersection(0, 0, i2, i3, this.tmp);
/*      */ 
/*  915 */       if (this.tmp.isEmpty())
/*      */       {
/*  917 */         return;
/*      */       }
/*      */ 
/*  920 */       if (paramMap.get(localObject1) != null) {
/*  921 */         localObject2 = localObject1;
/*  922 */         k = i;
/*  923 */         m = j;
/*      */       }
/*      */     }
/*      */ 
/*  927 */     if (paramComponent != localObject2)
/*      */     {
/*  929 */       this.tmp.setLocation(this.tmp.x + k - i, this.tmp.y + m - j);
/*      */ 
/*  931 */       Rectangle localRectangle = (Rectangle)paramMap.get(localObject2);
/*  932 */       SwingUtilities.computeUnion(this.tmp.x, this.tmp.y, this.tmp.width, this.tmp.height, localRectangle);
/*      */     }
/*      */ 
/*  938 */     if (!paramList.contains(localObject2))
/*  939 */       paramList.add(localObject2);
/*      */   }
/*      */ 
/*      */   public synchronized String toString()
/*      */   {
/*  950 */     StringBuffer localStringBuffer = new StringBuffer();
/*  951 */     if (this.dirtyComponents != null)
/*  952 */       localStringBuffer.append("" + this.dirtyComponents);
/*  953 */     return localStringBuffer.toString();
/*      */   }
/*      */ 
/*      */   public Image getOffscreenBuffer(Component paramComponent, int paramInt1, int paramInt2)
/*      */   {
/*  966 */     RepaintManager localRepaintManager = getDelegate(paramComponent);
/*  967 */     if (localRepaintManager != null) {
/*  968 */       return localRepaintManager.getOffscreenBuffer(paramComponent, paramInt1, paramInt2);
/*      */     }
/*  970 */     return _getOffscreenBuffer(paramComponent, paramInt1, paramInt2);
/*      */   }
/*      */ 
/*      */   public Image getVolatileOffscreenBuffer(Component paramComponent, int paramInt1, int paramInt2)
/*      */   {
/*  987 */     RepaintManager localRepaintManager = getDelegate(paramComponent);
/*  988 */     if (localRepaintManager != null) {
/*  989 */       return localRepaintManager.getVolatileOffscreenBuffer(paramComponent, paramInt1, paramInt2);
/*      */     }
/*      */ 
/*  994 */     Window localWindow = (paramComponent instanceof Window) ? (Window)paramComponent : SwingUtilities.getWindowAncestor(paramComponent);
/*  995 */     if (!localWindow.isOpaque()) {
/*  996 */       localObject = Toolkit.getDefaultToolkit();
/*  997 */       if (((localObject instanceof SunToolkit)) && (((SunToolkit)localObject).needUpdateWindow())) {
/*  998 */         return null;
/*      */       }
/*      */     }
/*      */ 
/* 1002 */     Object localObject = paramComponent.getGraphicsConfiguration();
/* 1003 */     if (localObject == null) {
/* 1004 */       localObject = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
/*      */     }
/*      */ 
/* 1007 */     Dimension localDimension = getDoubleBufferMaximumSize();
/* 1008 */     int i = paramInt1 > localDimension.width ? localDimension.width : paramInt1 < 1 ? 1 : paramInt1;
/*      */ 
/* 1010 */     int j = paramInt2 > localDimension.height ? localDimension.height : paramInt2 < 1 ? 1 : paramInt2;
/*      */ 
/* 1012 */     VolatileImage localVolatileImage = (VolatileImage)this.volatileMap.get(localObject);
/* 1013 */     if ((localVolatileImage == null) || (localVolatileImage.getWidth() < i) || (localVolatileImage.getHeight() < j))
/*      */     {
/* 1015 */       if (localVolatileImage != null) {
/* 1016 */         localVolatileImage.flush();
/*      */       }
/* 1018 */       localVolatileImage = ((GraphicsConfiguration)localObject).createCompatibleVolatileImage(i, j);
/* 1019 */       this.volatileMap.put(localObject, localVolatileImage);
/*      */     }
/* 1021 */     return localVolatileImage;
/*      */   }
/*      */ 
/*      */   private Image _getOffscreenBuffer(Component paramComponent, int paramInt1, int paramInt2) {
/* 1025 */     Dimension localDimension = getDoubleBufferMaximumSize();
/*      */ 
/* 1030 */     Window localWindow = (paramComponent instanceof Window) ? (Window)paramComponent : SwingUtilities.getWindowAncestor(paramComponent);
/* 1031 */     if (!localWindow.isOpaque()) {
/* 1032 */       localObject = Toolkit.getDefaultToolkit();
/* 1033 */       if (((localObject instanceof SunToolkit)) && (((SunToolkit)localObject).needUpdateWindow())) {
/* 1034 */         return null;
/*      */       }
/*      */     }
/*      */ 
/* 1038 */     if (this.standardDoubleBuffer == null) {
/* 1039 */       this.standardDoubleBuffer = new DoubleBufferInfo(null);
/*      */     }
/* 1041 */     DoubleBufferInfo localDoubleBufferInfo = this.standardDoubleBuffer;
/*      */ 
/* 1043 */     int i = paramInt1 > localDimension.width ? localDimension.width : paramInt1 < 1 ? 1 : paramInt1;
/*      */ 
/* 1045 */     int j = paramInt2 > localDimension.height ? localDimension.height : paramInt2 < 1 ? 1 : paramInt2;
/*      */ 
/* 1048 */     if ((localDoubleBufferInfo.needsReset) || ((localDoubleBufferInfo.image != null) && ((localDoubleBufferInfo.size.width < i) || (localDoubleBufferInfo.size.height < j))))
/*      */     {
/* 1051 */       localDoubleBufferInfo.needsReset = false;
/* 1052 */       if (localDoubleBufferInfo.image != null) {
/* 1053 */         localDoubleBufferInfo.image.flush();
/* 1054 */         localDoubleBufferInfo.image = null;
/*      */       }
/* 1056 */       i = Math.max(localDoubleBufferInfo.size.width, i);
/* 1057 */       j = Math.max(localDoubleBufferInfo.size.height, j);
/*      */     }
/*      */ 
/* 1060 */     Object localObject = localDoubleBufferInfo.image;
/*      */ 
/* 1062 */     if (localDoubleBufferInfo.image == null) {
/* 1063 */       localObject = paramComponent.createImage(i, j);
/* 1064 */       localDoubleBufferInfo.size = new Dimension(i, j);
/* 1065 */       if ((paramComponent instanceof JComponent)) {
/* 1066 */         ((JComponent)paramComponent).setCreatedDoubleBuffer(true);
/* 1067 */         localDoubleBufferInfo.image = ((Image)localObject);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1074 */     return localObject;
/*      */   }
/*      */ 
/*      */   public void setDoubleBufferMaximumSize(Dimension paramDimension)
/*      */   {
/* 1080 */     this.doubleBufferMaxSize = paramDimension;
/* 1081 */     if (this.doubleBufferMaxSize == null)
/* 1082 */       clearImages();
/*      */     else
/* 1084 */       clearImages(paramDimension.width, paramDimension.height);
/*      */   }
/*      */ 
/*      */   private void clearImages()
/*      */   {
/* 1089 */     clearImages(0, 0);
/*      */   }
/*      */ 
/*      */   private void clearImages(int paramInt1, int paramInt2) {
/* 1093 */     if ((this.standardDoubleBuffer != null) && (this.standardDoubleBuffer.image != null) && (
/* 1094 */       (this.standardDoubleBuffer.image.getWidth(null) > paramInt1) || (this.standardDoubleBuffer.image.getHeight(null) > paramInt2)))
/*      */     {
/* 1096 */       this.standardDoubleBuffer.image.flush();
/* 1097 */       this.standardDoubleBuffer.image = null;
/*      */     }
/*      */ 
/* 1101 */     Iterator localIterator = this.volatileMap.keySet().iterator();
/* 1102 */     while (localIterator.hasNext()) {
/* 1103 */       GraphicsConfiguration localGraphicsConfiguration = (GraphicsConfiguration)localIterator.next();
/* 1104 */       VolatileImage localVolatileImage = (VolatileImage)this.volatileMap.get(localGraphicsConfiguration);
/* 1105 */       if ((localVolatileImage.getWidth() > paramInt1) || (localVolatileImage.getHeight() > paramInt2)) {
/* 1106 */         localVolatileImage.flush();
/* 1107 */         localIterator.remove();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public Dimension getDoubleBufferMaximumSize()
/*      */   {
/* 1118 */     if (this.doubleBufferMaxSize == null) {
/*      */       try {
/* 1120 */         Rectangle localRectangle = new Rectangle();
/* 1121 */         GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
/*      */ 
/* 1123 */         for (GraphicsDevice localGraphicsDevice : localGraphicsEnvironment.getScreenDevices()) {
/* 1124 */           GraphicsConfiguration localGraphicsConfiguration = localGraphicsDevice.getDefaultConfiguration();
/* 1125 */           localRectangle = localRectangle.union(localGraphicsConfiguration.getBounds());
/*      */         }
/* 1127 */         this.doubleBufferMaxSize = new Dimension(localRectangle.width, localRectangle.height);
/*      */       }
/*      */       catch (HeadlessException localHeadlessException) {
/* 1130 */         this.doubleBufferMaxSize = new Dimension(2147483647, 2147483647);
/*      */       }
/*      */     }
/* 1133 */     return this.doubleBufferMaxSize;
/*      */   }
/*      */ 
/*      */   public void setDoubleBufferingEnabled(boolean paramBoolean)
/*      */   {
/* 1146 */     this.doubleBufferingEnabled = paramBoolean;
/* 1147 */     PaintManager localPaintManager = getPaintManager();
/* 1148 */     if ((!paramBoolean) && (localPaintManager.getClass() != PaintManager.class))
/* 1149 */       setPaintManager(new PaintManager());
/*      */   }
/*      */ 
/*      */   public boolean isDoubleBufferingEnabled()
/*      */   {
/* 1165 */     return this.doubleBufferingEnabled;
/*      */   }
/*      */ 
/*      */   void resetDoubleBuffer()
/*      */   {
/* 1174 */     if (this.standardDoubleBuffer != null)
/* 1175 */       this.standardDoubleBuffer.needsReset = true;
/*      */   }
/*      */ 
/*      */   void resetVolatileDoubleBuffer(GraphicsConfiguration paramGraphicsConfiguration)
/*      */   {
/* 1183 */     Image localImage = (Image)this.volatileMap.remove(paramGraphicsConfiguration);
/* 1184 */     if (localImage != null)
/* 1185 */       localImage.flush();
/*      */   }
/*      */ 
/*      */   boolean useVolatileDoubleBuffer()
/*      */   {
/* 1194 */     return volatileImageBufferEnabled;
/*      */   }
/*      */ 
/*      */   private synchronized boolean isPaintingThread()
/*      */   {
/* 1202 */     return Thread.currentThread() == this.paintThread;
/*      */   }
/*      */ 
/*      */   void paint(JComponent paramJComponent1, JComponent paramJComponent2, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/* 1226 */     PaintManager localPaintManager = getPaintManager();
/* 1227 */     if (!isPaintingThread())
/*      */     {
/* 1231 */       if (localPaintManager.getClass() != PaintManager.class) {
/* 1232 */         localPaintManager = new PaintManager();
/* 1233 */         localPaintManager.repaintManager = this;
/*      */       }
/*      */     }
/* 1236 */     if (!localPaintManager.paint(paramJComponent1, paramJComponent2, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4))
/*      */     {
/* 1238 */       paramGraphics.setClip(paramInt1, paramInt2, paramInt3, paramInt4);
/* 1239 */       paramJComponent1.paintToOffscreen(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt1 + paramInt3, paramInt2 + paramInt4);
/*      */     }
/*      */   }
/*      */ 
/*      */   void copyArea(JComponent paramJComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean)
/*      */   {
/* 1251 */     getPaintManager().copyArea(paramJComponent, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramBoolean);
/*      */   }
/*      */ 
/*      */   void beginPaint()
/*      */   {
/* 1271 */     int i = 0;
/*      */ 
/* 1273 */     Thread localThread = Thread.currentThread();
/*      */     int j;
/* 1274 */     synchronized (this) {
/* 1275 */       j = this.paintDepth;
/* 1276 */       if ((this.paintThread == null) || (localThread == this.paintThread)) {
/* 1277 */         this.paintThread = localThread;
/* 1278 */         this.paintDepth += 1;
/*      */       } else {
/* 1280 */         i = 1;
/*      */       }
/*      */     }
/* 1283 */     if ((i == 0) && (j == 0))
/* 1284 */       getPaintManager().beginPaint();
/*      */   }
/*      */ 
/*      */   void endPaint()
/*      */   {
/* 1292 */     if (isPaintingThread()) {
/* 1293 */       PaintManager localPaintManager = null;
/* 1294 */       synchronized (this) {
/* 1295 */         if (--this.paintDepth == 0) {
/* 1296 */           localPaintManager = getPaintManager();
/*      */         }
/*      */       }
/* 1299 */       if (localPaintManager != null) {
/* 1300 */         localPaintManager.endPaint();
/* 1301 */         synchronized (this) {
/* 1302 */           this.paintThread = null;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   boolean show(Container paramContainer, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/* 1316 */     return getPaintManager().show(paramContainer, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */   }
/*      */ 
/*      */   void doubleBufferingChanged(JRootPane paramJRootPane)
/*      */   {
/* 1324 */     getPaintManager().doubleBufferingChanged(paramJRootPane);
/*      */   }
/*      */ 
/*      */   void setPaintManager(PaintManager paramPaintManager)
/*      */   {
/* 1335 */     if (paramPaintManager == null)
/* 1336 */       paramPaintManager = new PaintManager();
/*      */     PaintManager localPaintManager;
/* 1339 */     synchronized (this) {
/* 1340 */       localPaintManager = this.paintManager;
/* 1341 */       this.paintManager = paramPaintManager;
/* 1342 */       paramPaintManager.repaintManager = this;
/*      */     }
/* 1344 */     if (localPaintManager != null)
/* 1345 */       localPaintManager.dispose();
/*      */   }
/*      */ 
/*      */   private synchronized PaintManager getPaintManager()
/*      */   {
/* 1350 */     if (this.paintManager == null) {
/* 1351 */       BufferStrategyPaintManager localBufferStrategyPaintManager = null;
/* 1352 */       if ((this.doubleBufferingEnabled) && (!nativeDoubleBuffering)) {
/* 1353 */         switch (this.bufferStrategyType) {
/*      */         case 0:
/* 1355 */           Toolkit localToolkit = Toolkit.getDefaultToolkit();
/* 1356 */           if ((localToolkit instanceof SunToolkit)) {
/* 1357 */             SunToolkit localSunToolkit = (SunToolkit)localToolkit;
/* 1358 */             if (localSunToolkit.useBufferPerWindow())
/* 1359 */               localBufferStrategyPaintManager = new BufferStrategyPaintManager();
/*      */           }
/* 1361 */           break;
/*      */         case 1:
/* 1364 */           localBufferStrategyPaintManager = new BufferStrategyPaintManager();
/* 1365 */           break;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1371 */       setPaintManager(localBufferStrategyPaintManager);
/*      */     }
/* 1373 */     return this.paintManager;
/*      */   }
/*      */ 
/*      */   private void scheduleProcessingRunnable() {
/* 1377 */     scheduleProcessingRunnable(AppContext.getAppContext());
/*      */   }
/*      */ 
/*      */   private void scheduleProcessingRunnable(AppContext paramAppContext) {
/* 1381 */     if (this.processingRunnable.markPending()) {
/* 1382 */       Toolkit localToolkit = Toolkit.getDefaultToolkit();
/* 1383 */       if ((localToolkit instanceof SunToolkit)) {
/* 1384 */         SunToolkit.getSystemEventQueueImplPP(paramAppContext).postEvent(new InvocationEvent(Toolkit.getDefaultToolkit(), this.processingRunnable));
/*      */       }
/*      */       else
/*      */       {
/* 1388 */         Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(new InvocationEvent(Toolkit.getDefaultToolkit(), this.processingRunnable));
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private RepaintManager getDelegate(Component paramComponent)
/*      */   {
/* 1655 */     RepaintManager localRepaintManager = SwingUtilities3.getDelegateRepaintManager(paramComponent);
/* 1656 */     if (this == localRepaintManager) {
/* 1657 */       localRepaintManager = null;
/*      */     }
/* 1659 */     return localRepaintManager;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  182 */     volatileImageBufferEnabled = "true".equals(AccessController.doPrivileged(new GetPropertyAction("swing.volatileImageBufferEnabled", "true")));
/*      */ 
/*  185 */     boolean bool = GraphicsEnvironment.isHeadless();
/*  186 */     if ((volatileImageBufferEnabled) && (bool)) {
/*  187 */       volatileImageBufferEnabled = false;
/*      */     }
/*  189 */     nativeDoubleBuffering = "true".equals(AccessController.doPrivileged(new GetPropertyAction("awt.nativeDoubleBuffering")));
/*      */ 
/*  191 */     String str = (String)AccessController.doPrivileged(new GetPropertyAction("swing.bufferPerWindow"));
/*      */ 
/*  193 */     if (bool) {
/*  194 */       BUFFER_STRATEGY_TYPE = 2;
/*      */     }
/*  196 */     else if (str == null) {
/*  197 */       BUFFER_STRATEGY_TYPE = 0;
/*      */     }
/*  199 */     else if ("true".equals(str)) {
/*  200 */       BUFFER_STRATEGY_TYPE = 1;
/*      */     }
/*      */     else {
/*  203 */       BUFFER_STRATEGY_TYPE = 2;
/*      */     }
/*  205 */     HANDLE_TOP_LEVEL_PAINT = "true".equals(AccessController.doPrivileged(new GetPropertyAction("swing.handleTopLevelPaint", "true")));
/*      */ 
/*  207 */     GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
/*      */ 
/*  209 */     if ((localGraphicsEnvironment instanceof SunGraphicsEnvironment))
/*  210 */       ((SunGraphicsEnvironment)localGraphicsEnvironment).addDisplayChangedListener(new DisplayChangedHandler(null));
/*      */   }
/*      */ 
/*      */   private static final class DisplayChangedHandler
/*      */     implements DisplayChangedListener
/*      */   {
/*      */     public void displayChanged()
/*      */     {
/* 1584 */       scheduleDisplayChanges();
/*      */     }
/*      */ 
/*      */     public void paletteChanged()
/*      */     {
/*      */     }
/*      */ 
/*      */     private void scheduleDisplayChanges()
/*      */     {
/* 1593 */       for (Iterator localIterator = AppContext.getAppContexts().iterator(); localIterator.hasNext(); ) { Object localObject1 = localIterator.next();
/* 1594 */         AppContext localAppContext = (AppContext)localObject1;
/* 1595 */         synchronized (localAppContext) {
/* 1596 */           if (!localAppContext.isDisposed()) {
/* 1597 */             EventQueue localEventQueue = (EventQueue)localAppContext.get(AppContext.EVENT_QUEUE_KEY);
/*      */ 
/* 1599 */             if (localEventQueue != null)
/* 1600 */               localEventQueue.postEvent(new InvocationEvent(Toolkit.getDefaultToolkit(), new RepaintManager.DisplayChangedRunnable(null)));
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static final class DisplayChangedRunnable
/*      */     implements Runnable
/*      */   {
/*      */     public void run()
/*      */     {
/* 1613 */       RepaintManager.currentManager((JComponent)null).displayChanged();
/*      */     }
/*      */   }
/*      */ 
/*      */   private class DoubleBufferInfo
/*      */   {
/*      */     public Image image;
/*      */     public Dimension size;
/* 1571 */     public boolean needsReset = false;
/*      */ 
/*      */     private DoubleBufferInfo()
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   static class PaintManager
/*      */   {
/*      */     protected RepaintManager repaintManager;
/*      */     boolean isRepaintingRoot;
/*      */ 
/*      */     public boolean paint(JComponent paramJComponent1, JComponent paramJComponent2, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */     {
/* 1426 */       boolean bool = false;
/*      */       Image localImage;
/* 1428 */       if ((this.repaintManager.useVolatileDoubleBuffer()) && ((localImage = getValidImage(this.repaintManager.getVolatileOffscreenBuffer(paramJComponent2, paramInt3, paramInt4))) != null))
/*      */       {
/* 1431 */         VolatileImage localVolatileImage = (VolatileImage)localImage;
/* 1432 */         GraphicsConfiguration localGraphicsConfiguration = paramJComponent2.getGraphicsConfiguration();
/*      */ 
/* 1434 */         for (int i = 0; (!bool) && (i < 2); 
/* 1435 */           i++) {
/* 1436 */           if (localVolatileImage.validate(localGraphicsConfiguration) == 2)
/*      */           {
/* 1438 */             this.repaintManager.resetVolatileDoubleBuffer(localGraphicsConfiguration);
/* 1439 */             localImage = this.repaintManager.getVolatileOffscreenBuffer(paramJComponent2, paramInt3, paramInt4);
/*      */ 
/* 1441 */             localVolatileImage = (VolatileImage)localImage;
/*      */           }
/* 1443 */           paintDoubleBuffered(paramJComponent1, localVolatileImage, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */ 
/* 1445 */           bool = !localVolatileImage.contentsLost();
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1450 */       if ((!bool) && ((localImage = getValidImage(this.repaintManager.getOffscreenBuffer(paramJComponent2, paramInt3, paramInt4))) != null))
/*      */       {
/* 1453 */         paintDoubleBuffered(paramJComponent1, localImage, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */ 
/* 1455 */         bool = true;
/*      */       }
/* 1457 */       return bool;
/*      */     }
/*      */ 
/*      */     public void copyArea(JComponent paramJComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean)
/*      */     {
/* 1465 */       paramGraphics.copyArea(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
/*      */     }
/*      */ 
/*      */     public void beginPaint()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void endPaint()
/*      */     {
/*      */     }
/*      */ 
/*      */     public boolean show(Container paramContainer, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */     {
/* 1486 */       return false;
/*      */     }
/*      */ 
/*      */     public void doubleBufferingChanged(JRootPane paramJRootPane)
/*      */     {
/*      */     }
/*      */ 
/*      */     protected void paintDoubleBuffered(JComponent paramJComponent, Image paramImage, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */     {
/* 1502 */       Graphics localGraphics = paramImage.getGraphics();
/* 1503 */       int i = Math.min(paramInt3, paramImage.getWidth(null));
/* 1504 */       int j = Math.min(paramInt4, paramImage.getHeight(null));
/*      */       try
/*      */       {
/* 1508 */         int k = paramInt1; for (int n = paramInt1 + paramInt3; k < n; k += i) {
/* 1509 */           int m = paramInt2; for (int i1 = paramInt2 + paramInt4; m < i1; m += j) {
/* 1510 */             localGraphics.translate(-k, -m);
/* 1511 */             localGraphics.setClip(k, m, i, j);
/* 1512 */             paramJComponent.paintToOffscreen(localGraphics, k, m, i, j, n, i1);
/* 1513 */             paramGraphics.setClip(k, m, i, j);
/* 1514 */             paramGraphics.drawImage(paramImage, k, m, paramJComponent);
/* 1515 */             localGraphics.translate(k, m);
/*      */           }
/*      */         }
/*      */       } finally {
/* 1519 */         localGraphics.dispose();
/*      */       }
/*      */     }
/*      */ 
/*      */     private Image getValidImage(Image paramImage)
/*      */     {
/* 1528 */       if ((paramImage != null) && (paramImage.getWidth(null) > 0) && (paramImage.getHeight(null) > 0))
/*      */       {
/* 1530 */         return paramImage;
/*      */       }
/* 1532 */       return null;
/*      */     }
/*      */ 
/*      */     protected void repaintRoot(JComponent paramJComponent)
/*      */     {
/* 1542 */       assert (this.repaintManager.repaintRoot == null);
/* 1543 */       if (this.repaintManager.painting) {
/* 1544 */         this.repaintManager.repaintRoot = paramJComponent;
/*      */       }
/*      */       else
/* 1547 */         paramJComponent.repaint();
/*      */     }
/*      */ 
/*      */     protected boolean isRepaintingRoot()
/*      */     {
/* 1556 */       return this.isRepaintingRoot;
/*      */     }
/*      */ 
/*      */     protected void dispose()
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   private final class ProcessingRunnable
/*      */     implements Runnable
/*      */   {
/*      */     private boolean pending;
/*      */ 
/*      */     private ProcessingRunnable()
/*      */     {
/*      */     }
/*      */ 
/*      */     public synchronized boolean markPending()
/*      */     {
/* 1630 */       if (!this.pending) {
/* 1631 */         this.pending = true;
/* 1632 */         return true;
/*      */       }
/* 1634 */       return false;
/*      */     }
/*      */ 
/*      */     public void run() {
/* 1638 */       synchronized (this) {
/* 1639 */         this.pending = false;
/*      */       }
/*      */ 
/* 1648 */       RepaintManager.this.scheduleHeavyWeightPaints();
/*      */ 
/* 1650 */       RepaintManager.this.validateInvalidComponents();
/* 1651 */       RepaintManager.this.prePaintDirtyRegions();
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.RepaintManager
 * JD-Core Version:    0.6.2
 */