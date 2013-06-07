/*     */ package javax.swing;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.FocusTraversalPolicy;
/*     */ import java.beans.PropertyVetoException;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
/*     */ import javax.accessibility.Accessible;
/*     */ import javax.accessibility.AccessibleContext;
/*     */ import javax.accessibility.AccessibleRole;
/*     */ import javax.swing.plaf.ComponentUI;
/*     */ import javax.swing.plaf.DesktopPaneUI;
/*     */ 
/*     */ public class JDesktopPane extends JLayeredPane
/*     */   implements Accessible
/*     */ {
/*     */   private static final String uiClassID = "DesktopPaneUI";
/*     */   transient DesktopManager desktopManager;
/*  99 */   private transient JInternalFrame selectedFrame = null;
/*     */   public static final int LIVE_DRAG_MODE = 0;
/*     */   public static final int OUTLINE_DRAG_MODE = 1;
/* 119 */   private int dragMode = 0;
/* 120 */   private boolean dragModeSet = false;
/*     */   private transient List<JInternalFrame> framesCache;
/* 122 */   private boolean componentOrderCheckingEnabled = true;
/* 123 */   private boolean componentOrderChanged = false;
/*     */ 
/*     */   public JDesktopPane()
/*     */   {
/* 129 */     setUIProperty("opaque", Boolean.TRUE);
/* 130 */     setFocusCycleRoot(true);
/*     */ 
/* 132 */     setFocusTraversalPolicy(new LayoutFocusTraversalPolicy() {
/*     */       public Component getDefaultComponent(Container paramAnonymousContainer) {
/* 134 */         JInternalFrame[] arrayOfJInternalFrame1 = JDesktopPane.this.getAllFrames();
/* 135 */         Component localComponent = null;
/* 136 */         for (JInternalFrame localJInternalFrame : arrayOfJInternalFrame1) {
/* 137 */           localComponent = localJInternalFrame.getFocusTraversalPolicy().getDefaultComponent(localJInternalFrame);
/* 138 */           if (localComponent != null) {
/*     */             break;
/*     */           }
/*     */         }
/* 142 */         return localComponent;
/*     */       }
/*     */     });
/* 145 */     updateUI();
/*     */   }
/*     */ 
/*     */   public DesktopPaneUI getUI()
/*     */   {
/* 155 */     return (DesktopPaneUI)this.ui;
/*     */   }
/*     */ 
/*     */   public void setUI(DesktopPaneUI paramDesktopPaneUI)
/*     */   {
/* 170 */     super.setUI(paramDesktopPaneUI);
/*     */   }
/*     */ 
/*     */   public void setDragMode(int paramInt)
/*     */   {
/* 191 */     int i = this.dragMode;
/* 192 */     this.dragMode = paramInt;
/* 193 */     firePropertyChange("dragMode", i, this.dragMode);
/* 194 */     this.dragModeSet = true;
/*     */   }
/*     */ 
/*     */   public int getDragMode()
/*     */   {
/* 205 */     return this.dragMode;
/*     */   }
/*     */ 
/*     */   public DesktopManager getDesktopManager()
/*     */   {
/* 213 */     return this.desktopManager;
/*     */   }
/*     */ 
/*     */   public void setDesktopManager(DesktopManager paramDesktopManager)
/*     */   {
/* 229 */     DesktopManager localDesktopManager = this.desktopManager;
/* 230 */     this.desktopManager = paramDesktopManager;
/* 231 */     firePropertyChange("desktopManager", localDesktopManager, this.desktopManager);
/*     */   }
/*     */ 
/*     */   public void updateUI()
/*     */   {
/* 242 */     setUI((DesktopPaneUI)UIManager.getUI(this));
/*     */   }
/*     */ 
/*     */   public String getUIClassID()
/*     */   {
/* 254 */     return "DesktopPaneUI";
/*     */   }
/*     */ 
/*     */   public JInternalFrame[] getAllFrames()
/*     */   {
/* 264 */     return (JInternalFrame[])getAllFrames(this).toArray(new JInternalFrame[0]);
/*     */   }
/*     */ 
/*     */   private static Collection<JInternalFrame> getAllFrames(Container paramContainer)
/*     */   {
/* 269 */     ArrayList localArrayList = new ArrayList();
/* 270 */     int j = paramContainer.getComponentCount();
/* 271 */     for (int i = 0; i < j; i++) {
/* 272 */       Component localComponent = paramContainer.getComponent(i);
/* 273 */       if ((localComponent instanceof JInternalFrame)) {
/* 274 */         localArrayList.add((JInternalFrame)localComponent);
/* 275 */       } else if ((localComponent instanceof JInternalFrame.JDesktopIcon)) {
/* 276 */         JInternalFrame localJInternalFrame = ((JInternalFrame.JDesktopIcon)localComponent).getInternalFrame();
/* 277 */         if (localJInternalFrame != null)
/* 278 */           localArrayList.add(localJInternalFrame);
/*     */       }
/* 280 */       else if ((localComponent instanceof Container)) {
/* 281 */         localArrayList.addAll(getAllFrames((Container)localComponent));
/*     */       }
/*     */     }
/* 284 */     return localArrayList;
/*     */   }
/*     */ 
/*     */   public JInternalFrame getSelectedFrame()
/*     */   {
/* 297 */     return this.selectedFrame;
/*     */   }
/*     */ 
/*     */   public void setSelectedFrame(JInternalFrame paramJInternalFrame)
/*     */   {
/* 313 */     this.selectedFrame = paramJInternalFrame;
/*     */   }
/*     */ 
/*     */   public JInternalFrame[] getAllFramesInLayer(int paramInt)
/*     */   {
/* 326 */     Collection localCollection = getAllFrames(this);
/* 327 */     Iterator localIterator = localCollection.iterator();
/* 328 */     while (localIterator.hasNext()) {
/* 329 */       if (((JInternalFrame)localIterator.next()).getLayer() != paramInt) {
/* 330 */         localIterator.remove();
/*     */       }
/*     */     }
/* 333 */     return (JInternalFrame[])localCollection.toArray(new JInternalFrame[0]);
/*     */   }
/*     */ 
/*     */   private List<JInternalFrame> getFrames()
/*     */   {
/* 338 */     TreeSet localTreeSet = new TreeSet();
/* 339 */     for (int i = 0; i < getComponentCount(); i++) {
/* 340 */       Object localObject = getComponent(i);
/* 341 */       if ((localObject instanceof JInternalFrame)) {
/* 342 */         localTreeSet.add(new ComponentPosition((JInternalFrame)localObject, getLayer((Component)localObject), i));
/*     */       }
/* 345 */       else if ((localObject instanceof JInternalFrame.JDesktopIcon)) {
/* 346 */         localObject = ((JInternalFrame.JDesktopIcon)localObject).getInternalFrame();
/* 347 */         localTreeSet.add(new ComponentPosition((JInternalFrame)localObject, getLayer((Component)localObject), i));
/*     */       }
/*     */     }
/*     */ 
/* 351 */     ArrayList localArrayList = new ArrayList(localTreeSet.size());
/*     */ 
/* 353 */     for (ComponentPosition localComponentPosition : localTreeSet) {
/* 354 */       localArrayList.add(localComponentPosition.component);
/*     */     }
/* 356 */     return localArrayList;
/*     */   }
/*     */ 
/*     */   private JInternalFrame getNextFrame(JInternalFrame paramJInternalFrame, boolean paramBoolean)
/*     */   {
/* 381 */     verifyFramesCache();
/* 382 */     if (paramJInternalFrame == null) {
/* 383 */       return getTopInternalFrame();
/*     */     }
/* 385 */     int i = this.framesCache.indexOf(paramJInternalFrame);
/* 386 */     if ((i == -1) || (this.framesCache.size() == 1))
/*     */     {
/* 388 */       return null;
/*     */     }
/* 390 */     if (paramBoolean)
/*     */     {
/* 392 */       i++; if (i == this.framesCache.size())
/*     */       {
/* 394 */         i = 0;
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 399 */       i--; if (i == -1)
/*     */       {
/* 401 */         i = this.framesCache.size() - 1;
/*     */       }
/*     */     }
/* 404 */     return (JInternalFrame)this.framesCache.get(i);
/*     */   }
/*     */ 
/*     */   JInternalFrame getNextFrame(JInternalFrame paramJInternalFrame) {
/* 408 */     return getNextFrame(paramJInternalFrame, true);
/*     */   }
/*     */ 
/*     */   private JInternalFrame getTopInternalFrame() {
/* 412 */     if (this.framesCache.size() == 0) {
/* 413 */       return null;
/*     */     }
/* 415 */     return (JInternalFrame)this.framesCache.get(0);
/*     */   }
/*     */ 
/*     */   private void updateFramesCache() {
/* 419 */     this.framesCache = getFrames();
/*     */   }
/*     */ 
/*     */   private void verifyFramesCache()
/*     */   {
/* 424 */     if (this.componentOrderChanged) {
/* 425 */       this.componentOrderChanged = false;
/* 426 */       updateFramesCache();
/*     */     }
/*     */   }
/*     */ 
/*     */   public JInternalFrame selectFrame(boolean paramBoolean)
/*     */   {
/* 441 */     JInternalFrame localJInternalFrame1 = getSelectedFrame();
/* 442 */     JInternalFrame localJInternalFrame2 = getNextFrame(localJInternalFrame1, paramBoolean);
/* 443 */     if (localJInternalFrame2 == null) {
/* 444 */       return null;
/*     */     }
/*     */ 
/* 448 */     setComponentOrderCheckingEnabled(false);
/* 449 */     if ((paramBoolean) && (localJInternalFrame1 != null))
/* 450 */       localJInternalFrame1.moveToBack();
/*     */     try {
/* 452 */       localJInternalFrame2.setSelected(true); } catch (PropertyVetoException localPropertyVetoException) {
/*     */     }
/* 454 */     setComponentOrderCheckingEnabled(true);
/* 455 */     return localJInternalFrame2;
/*     */   }
/*     */ 
/*     */   void setComponentOrderCheckingEnabled(boolean paramBoolean)
/*     */   {
/* 466 */     this.componentOrderCheckingEnabled = paramBoolean;
/*     */   }
/*     */ 
/*     */   protected void addImpl(Component paramComponent, Object paramObject, int paramInt)
/*     */   {
/* 474 */     super.addImpl(paramComponent, paramObject, paramInt);
/* 475 */     if ((this.componentOrderCheckingEnabled) && (
/* 476 */       ((paramComponent instanceof JInternalFrame)) || ((paramComponent instanceof JInternalFrame.JDesktopIcon))))
/*     */     {
/* 478 */       this.componentOrderChanged = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void remove(int paramInt)
/*     */   {
/* 488 */     if (this.componentOrderCheckingEnabled) {
/* 489 */       Component localComponent = getComponent(paramInt);
/* 490 */       if (((localComponent instanceof JInternalFrame)) || ((localComponent instanceof JInternalFrame.JDesktopIcon)))
/*     */       {
/* 492 */         this.componentOrderChanged = true;
/*     */       }
/*     */     }
/* 495 */     super.remove(paramInt);
/*     */   }
/*     */ 
/*     */   public void removeAll()
/*     */   {
/* 503 */     if (this.componentOrderCheckingEnabled) {
/* 504 */       int i = getComponentCount();
/* 505 */       for (int j = 0; j < i; j++) {
/* 506 */         Component localComponent = getComponent(j);
/* 507 */         if (((localComponent instanceof JInternalFrame)) || ((localComponent instanceof JInternalFrame.JDesktopIcon)))
/*     */         {
/* 509 */           this.componentOrderChanged = true;
/* 510 */           break;
/*     */         }
/*     */       }
/*     */     }
/* 514 */     super.removeAll();
/*     */   }
/*     */ 
/*     */   public void setComponentZOrder(Component paramComponent, int paramInt)
/*     */   {
/* 522 */     super.setComponentZOrder(paramComponent, paramInt);
/* 523 */     if ((this.componentOrderCheckingEnabled) && (
/* 524 */       ((paramComponent instanceof JInternalFrame)) || ((paramComponent instanceof JInternalFrame.JDesktopIcon))))
/*     */     {
/* 526 */       this.componentOrderChanged = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void writeObject(ObjectOutputStream paramObjectOutputStream)
/*     */     throws IOException
/*     */   {
/* 536 */     paramObjectOutputStream.defaultWriteObject();
/* 537 */     if (getUIClassID().equals("DesktopPaneUI")) {
/* 538 */       byte b = JComponent.getWriteObjCounter(this);
/* 539 */       b = (byte)(b - 1); JComponent.setWriteObjCounter(this, b);
/* 540 */       if ((b == 0) && (this.ui != null))
/* 541 */         this.ui.installUI(this);
/*     */     }
/*     */   }
/*     */ 
/*     */   void setUIProperty(String paramString, Object paramObject)
/*     */   {
/* 547 */     if (paramString == "dragMode") {
/* 548 */       if (!this.dragModeSet) {
/* 549 */         setDragMode(((Integer)paramObject).intValue());
/* 550 */         this.dragModeSet = false;
/*     */       }
/*     */     }
/* 553 */     else super.setUIProperty(paramString, paramObject);
/*     */   }
/*     */ 
/*     */   protected String paramString()
/*     */   {
/* 567 */     String str = this.desktopManager != null ? this.desktopManager.toString() : "";
/*     */ 
/* 570 */     return super.paramString() + ",desktopManager=" + str;
/*     */   }
/*     */ 
/*     */   public AccessibleContext getAccessibleContext()
/*     */   {
/* 589 */     if (this.accessibleContext == null) {
/* 590 */       this.accessibleContext = new AccessibleJDesktopPane();
/*     */     }
/* 592 */     return this.accessibleContext;
/*     */   }
/*     */ 
/*     */   protected class AccessibleJDesktopPane extends JComponent.AccessibleJComponent
/*     */   {
/*     */     protected AccessibleJDesktopPane()
/*     */     {
/* 610 */       super();
/*     */     }
/*     */ 
/*     */     public AccessibleRole getAccessibleRole()
/*     */     {
/* 620 */       return AccessibleRole.DESKTOP_PANE;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class ComponentPosition
/*     */     implements Comparable<ComponentPosition>
/*     */   {
/*     */     private final JInternalFrame component;
/*     */     private final int layer;
/*     */     private final int zOrder;
/*     */ 
/*     */     ComponentPosition(JInternalFrame paramJInternalFrame, int paramInt1, int paramInt2)
/*     */     {
/* 366 */       this.component = paramJInternalFrame;
/* 367 */       this.layer = paramInt1;
/* 368 */       this.zOrder = paramInt2;
/*     */     }
/*     */ 
/*     */     public int compareTo(ComponentPosition paramComponentPosition) {
/* 372 */       int i = paramComponentPosition.layer - this.layer;
/* 373 */       if (i == 0) {
/* 374 */         return this.zOrder - paramComponentPosition.zOrder;
/*     */       }
/* 376 */       return i;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.JDesktopPane
 * JD-Core Version:    0.6.2
 */