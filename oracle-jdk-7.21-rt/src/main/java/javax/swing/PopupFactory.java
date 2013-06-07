/*     */ package javax.swing;
/*     */ 
/*     */ import java.applet.Applet;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.GraphicsConfiguration;
/*     */ import java.awt.GraphicsEnvironment;
/*     */ import java.awt.Insets;
/*     */ import java.awt.Panel;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.Window;
/*     */ import java.awt.event.WindowAdapter;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class PopupFactory
/*     */ {
/*  64 */   private static final Object SharedInstanceKey = new StringBuffer("PopupFactory.SharedInstanceKey");
/*     */   private static final int MAX_CACHE_SIZE = 5;
/*     */   static final int LIGHT_WEIGHT_POPUP = 0;
/*     */   static final int MEDIUM_WEIGHT_POPUP = 1;
/*     */   static final int HEAVY_WEIGHT_POPUP = 2;
/*     */   private int popupType;
/*     */ 
/*     */   public PopupFactory()
/*     */   {
/*  90 */     this.popupType = 0;
/*     */   }
/*     */ 
/*     */   public static void setSharedInstance(PopupFactory paramPopupFactory)
/*     */   {
/* 104 */     if (paramPopupFactory == null) {
/* 105 */       throw new IllegalArgumentException("PopupFactory can not be null");
/*     */     }
/* 107 */     SwingUtilities.appContextPut(SharedInstanceKey, paramPopupFactory);
/*     */   }
/*     */ 
/*     */   public static PopupFactory getSharedInstance()
/*     */   {
/* 117 */     PopupFactory localPopupFactory = (PopupFactory)SwingUtilities.appContextGet(SharedInstanceKey);
/*     */ 
/* 120 */     if (localPopupFactory == null) {
/* 121 */       localPopupFactory = new PopupFactory();
/* 122 */       setSharedInstance(localPopupFactory);
/*     */     }
/* 124 */     return localPopupFactory;
/*     */   }
/*     */ 
/*     */   void setPopupType(int paramInt)
/*     */   {
/* 133 */     this.popupType = paramInt;
/*     */   }
/*     */ 
/*     */   int getPopupType()
/*     */   {
/* 140 */     return this.popupType;
/*     */   }
/*     */ 
/*     */   public Popup getPopup(Component paramComponent1, Component paramComponent2, int paramInt1, int paramInt2)
/*     */     throws IllegalArgumentException
/*     */   {
/* 164 */     if (paramComponent2 == null) {
/* 165 */       throw new IllegalArgumentException("Popup.getPopup must be passed non-null contents");
/*     */     }
/*     */ 
/* 169 */     int i = getPopupType(paramComponent1, paramComponent2, paramInt1, paramInt2);
/* 170 */     Popup localPopup = getPopup(paramComponent1, paramComponent2, paramInt1, paramInt2, i);
/*     */ 
/* 172 */     if (localPopup == null)
/*     */     {
/* 174 */       localPopup = getPopup(paramComponent1, paramComponent2, paramInt1, paramInt2, 2);
/*     */     }
/* 176 */     return localPopup;
/*     */   }
/*     */ 
/*     */   private int getPopupType(Component paramComponent1, Component paramComponent2, int paramInt1, int paramInt2)
/*     */   {
/* 184 */     int i = getPopupType();
/*     */ 
/* 186 */     if ((paramComponent1 == null) || (invokerInHeavyWeightPopup(paramComponent1))) {
/* 187 */       i = 2;
/*     */     }
/* 189 */     else if ((i == 0) && (!(paramComponent2 instanceof JToolTip)) && (!(paramComponent2 instanceof JPopupMenu)))
/*     */     {
/* 192 */       i = 1;
/*     */     }
/*     */ 
/* 198 */     Object localObject = paramComponent1;
/* 199 */     while (localObject != null) {
/* 200 */       if (((localObject instanceof JComponent)) && 
/* 201 */         (((JComponent)localObject).getClientProperty(ClientPropertyKey.PopupFactory_FORCE_HEAVYWEIGHT_POPUP) == Boolean.TRUE))
/*     */       {
/* 203 */         i = 2;
/* 204 */         break;
/*     */       }
/*     */ 
/* 207 */       localObject = ((Component)localObject).getParent();
/*     */     }
/*     */ 
/* 210 */     return i;
/*     */   }
/*     */ 
/*     */   private Popup getPopup(Component paramComponent1, Component paramComponent2, int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/* 219 */     if (GraphicsEnvironment.isHeadless()) {
/* 220 */       return getHeadlessPopup(paramComponent1, paramComponent2, paramInt1, paramInt2);
/*     */     }
/*     */ 
/* 223 */     switch (paramInt3) {
/*     */     case 0:
/* 225 */       return getLightWeightPopup(paramComponent1, paramComponent2, paramInt1, paramInt2);
/*     */     case 1:
/* 227 */       return getMediumWeightPopup(paramComponent1, paramComponent2, paramInt1, paramInt2);
/*     */     case 2:
/* 229 */       return getHeavyWeightPopup(paramComponent1, paramComponent2, paramInt1, paramInt2);
/*     */     }
/* 231 */     return null;
/*     */   }
/*     */ 
/*     */   private Popup getHeadlessPopup(Component paramComponent1, Component paramComponent2, int paramInt1, int paramInt2)
/*     */   {
/* 239 */     return HeadlessPopup.getHeadlessPopup(paramComponent1, paramComponent2, paramInt1, paramInt2);
/*     */   }
/*     */ 
/*     */   private Popup getLightWeightPopup(Component paramComponent1, Component paramComponent2, int paramInt1, int paramInt2)
/*     */   {
/* 247 */     return LightWeightPopup.getLightWeightPopup(paramComponent1, paramComponent2, paramInt1, paramInt2);
/*     */   }
/*     */ 
/*     */   private Popup getMediumWeightPopup(Component paramComponent1, Component paramComponent2, int paramInt1, int paramInt2)
/*     */   {
/* 256 */     return MediumWeightPopup.getMediumWeightPopup(paramComponent1, paramComponent2, paramInt1, paramInt2);
/*     */   }
/*     */ 
/*     */   private Popup getHeavyWeightPopup(Component paramComponent1, Component paramComponent2, int paramInt1, int paramInt2)
/*     */   {
/* 265 */     if (GraphicsEnvironment.isHeadless()) {
/* 266 */       return getMediumWeightPopup(paramComponent1, paramComponent2, paramInt1, paramInt2);
/*     */     }
/* 268 */     return HeavyWeightPopup.getHeavyWeightPopup(paramComponent1, paramComponent2, paramInt1, paramInt2);
/*     */   }
/*     */ 
/*     */   private boolean invokerInHeavyWeightPopup(Component paramComponent)
/*     */   {
/* 277 */     if (paramComponent != null)
/*     */     {
/* 279 */       for (Container localContainer = paramComponent.getParent(); localContainer != null; localContainer = localContainer.getParent())
/*     */       {
/* 281 */         if ((localContainer instanceof Popup.HeavyWeightWindow)) {
/* 282 */           return true;
/*     */         }
/*     */       }
/*     */     }
/* 286 */     return false;
/*     */   }
/*     */ 
/*     */   private static class ContainerPopup extends Popup
/*     */   {
/*     */     Component owner;
/*     */     int x;
/*     */     int y;
/*     */ 
/*     */     public void hide()
/*     */     {
/* 487 */       Component localComponent = getComponent();
/*     */ 
/* 489 */       if (localComponent != null) {
/* 490 */         Container localContainer = localComponent.getParent();
/*     */ 
/* 492 */         if (localContainer != null) {
/* 493 */           Rectangle localRectangle = localComponent.getBounds();
/*     */ 
/* 495 */           localContainer.remove(localComponent);
/* 496 */           localContainer.repaint(localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height);
/*     */         }
/*     */       }
/*     */ 
/* 500 */       this.owner = null;
/*     */     }
/*     */     public void pack() {
/* 503 */       Component localComponent = getComponent();
/*     */ 
/* 505 */       if (localComponent != null)
/* 506 */         localComponent.setSize(localComponent.getPreferredSize());
/*     */     }
/*     */ 
/*     */     void reset(Component paramComponent1, Component paramComponent2, int paramInt1, int paramInt2)
/*     */     {
/* 512 */       if (((paramComponent1 instanceof JFrame)) || ((paramComponent1 instanceof JDialog)) || ((paramComponent1 instanceof JWindow)))
/*     */       {
/* 516 */         paramComponent1 = ((RootPaneContainer)paramComponent1).getLayeredPane();
/*     */       }
/* 518 */       super.reset(paramComponent1, paramComponent2, paramInt1, paramInt2);
/*     */ 
/* 520 */       this.x = paramInt1;
/* 521 */       this.y = paramInt2;
/* 522 */       this.owner = paramComponent1;
/*     */     }
/*     */ 
/*     */     boolean overlappedByOwnedWindow() {
/* 526 */       Component localComponent = getComponent();
/* 527 */       if ((this.owner != null) && (localComponent != null)) {
/* 528 */         Window localWindow1 = SwingUtilities.getWindowAncestor(this.owner);
/* 529 */         if (localWindow1 == null) {
/* 530 */           return false;
/*     */         }
/* 532 */         Window[] arrayOfWindow1 = localWindow1.getOwnedWindows();
/* 533 */         if (arrayOfWindow1 != null) {
/* 534 */           Rectangle localRectangle = localComponent.getBounds();
/* 535 */           for (Window localWindow2 : arrayOfWindow1) {
/* 536 */             if ((localWindow2.isVisible()) && (localRectangle.intersects(localWindow2.getBounds())))
/*     */             {
/* 539 */               return true;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 544 */       return false;
/*     */     }
/*     */ 
/*     */     boolean fitsOnScreen()
/*     */     {
/* 552 */       boolean bool = false;
/* 553 */       Component localComponent = getComponent();
/* 554 */       if ((this.owner != null) && (localComponent != null)) {
/* 555 */         int i = localComponent.getWidth();
/* 556 */         int j = localComponent.getHeight();
/*     */ 
/* 558 */         Container localContainer = (Container)SwingUtilities.getRoot(this.owner);
/*     */         Rectangle localRectangle1;
/*     */         Object localObject;
/* 559 */         if (((localContainer instanceof JFrame)) || ((localContainer instanceof JDialog)) || ((localContainer instanceof JWindow)))
/*     */         {
/* 563 */           localRectangle1 = localContainer.getBounds();
/* 564 */           localObject = localContainer.getInsets();
/* 565 */           localRectangle1.x += ((Insets)localObject).left;
/* 566 */           localRectangle1.y += ((Insets)localObject).top;
/* 567 */           localRectangle1.width -= ((Insets)localObject).left + ((Insets)localObject).right;
/* 568 */           localRectangle1.height -= ((Insets)localObject).top + ((Insets)localObject).bottom;
/*     */ 
/* 570 */           if (JPopupMenu.canPopupOverlapTaskBar()) {
/* 571 */             GraphicsConfiguration localGraphicsConfiguration = localContainer.getGraphicsConfiguration();
/*     */ 
/* 573 */             Rectangle localRectangle2 = getContainerPopupArea(localGraphicsConfiguration);
/* 574 */             bool = localRectangle1.intersection(localRectangle2).contains(this.x, this.y, i, j);
/*     */           }
/*     */           else {
/* 577 */             bool = localRectangle1.contains(this.x, this.y, i, j);
/*     */           }
/*     */         }
/* 580 */         else if ((localContainer instanceof JApplet)) {
/* 581 */           localRectangle1 = localContainer.getBounds();
/* 582 */           localObject = localContainer.getLocationOnScreen();
/* 583 */           localRectangle1.x = ((Point)localObject).x;
/* 584 */           localRectangle1.y = ((Point)localObject).y;
/* 585 */           bool = localRectangle1.contains(this.x, this.y, i, j);
/*     */         }
/*     */       }
/* 588 */       return bool;
/*     */     }
/*     */ 
/*     */     Rectangle getContainerPopupArea(GraphicsConfiguration paramGraphicsConfiguration)
/*     */     {
/* 593 */       Toolkit localToolkit = Toolkit.getDefaultToolkit();
/*     */       Rectangle localRectangle;
/*     */       Insets localInsets;
/* 595 */       if (paramGraphicsConfiguration != null)
/*     */       {
/* 598 */         localRectangle = paramGraphicsConfiguration.getBounds();
/* 599 */         localInsets = localToolkit.getScreenInsets(paramGraphicsConfiguration);
/*     */       }
/*     */       else {
/* 602 */         localRectangle = new Rectangle(localToolkit.getScreenSize());
/* 603 */         localInsets = new Insets(0, 0, 0, 0);
/*     */       }
/*     */ 
/* 606 */       localRectangle.x += localInsets.left;
/* 607 */       localRectangle.y += localInsets.top;
/* 608 */       localRectangle.width -= localInsets.left + localInsets.right;
/* 609 */       localRectangle.height -= localInsets.top + localInsets.bottom;
/* 610 */       return localRectangle;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class HeadlessPopup extends PopupFactory.ContainerPopup
/*     */   {
/*     */     private HeadlessPopup()
/*     */     {
/* 618 */       super();
/*     */     }
/*     */     static Popup getHeadlessPopup(Component paramComponent1, Component paramComponent2, int paramInt1, int paramInt2) {
/* 621 */       HeadlessPopup localHeadlessPopup = new HeadlessPopup();
/* 622 */       localHeadlessPopup.reset(paramComponent1, paramComponent2, paramInt1, paramInt2);
/* 623 */       return localHeadlessPopup;
/*     */     }
/*     */ 
/*     */     Component createComponent(Component paramComponent) {
/* 627 */       return new Panel(new BorderLayout());
/*     */     }
/*     */ 
/*     */     public void show()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void hide()
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class HeavyWeightPopup extends Popup
/*     */   {
/* 294 */     private static final Object heavyWeightPopupCacheKey = new StringBuffer("PopupFactory.heavyWeightPopupCache");
/*     */ 
/*     */     static Popup getHeavyWeightPopup(Component paramComponent1, Component paramComponent2, int paramInt1, int paramInt2)
/*     */     {
/* 303 */       Window localWindow = paramComponent1 != null ? SwingUtilities.getWindowAncestor(paramComponent1) : null;
/*     */ 
/* 305 */       HeavyWeightPopup localHeavyWeightPopup = null;
/*     */ 
/* 307 */       if (localWindow != null) {
/* 308 */         localHeavyWeightPopup = getRecycledHeavyWeightPopup(localWindow);
/*     */       }
/*     */ 
/* 311 */       int i = 0;
/*     */       Object localObject;
/* 312 */       if ((paramComponent2 != null) && (paramComponent2.isFocusable()) && 
/* 313 */         ((paramComponent2 instanceof JPopupMenu))) {
/* 314 */         localObject = (JPopupMenu)paramComponent2;
/* 315 */         Component[] arrayOfComponent1 = ((JPopupMenu)localObject).getComponents();
/* 316 */         for (Component localComponent : arrayOfComponent1) {
/* 317 */           if ((!(localComponent instanceof MenuElement)) && (!(localComponent instanceof JSeparator)))
/*     */           {
/* 319 */             i = 1;
/* 320 */             break;
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 326 */       if ((localHeavyWeightPopup == null) || (((JWindow)localHeavyWeightPopup.getComponent()).getFocusableWindowState() != i))
/*     */       {
/* 330 */         if (localHeavyWeightPopup != null)
/*     */         {
/* 333 */           localHeavyWeightPopup._dispose();
/*     */         }
/*     */ 
/* 336 */         localHeavyWeightPopup = new HeavyWeightPopup();
/*     */       }
/*     */ 
/* 339 */       localHeavyWeightPopup.reset(paramComponent1, paramComponent2, paramInt1, paramInt2);
/*     */ 
/* 341 */       if (i != 0) {
/* 342 */         localObject = (JWindow)localHeavyWeightPopup.getComponent();
/* 343 */         ((JWindow)localObject).setFocusableWindowState(true);
/*     */ 
/* 346 */         ((JWindow)localObject).setName("###focusableSwingPopup###");
/*     */       }
/*     */ 
/* 349 */       return localHeavyWeightPopup;
/*     */     }
/*     */ 
/*     */     private static HeavyWeightPopup getRecycledHeavyWeightPopup(Window paramWindow)
/*     */     {
/* 359 */       synchronized (HeavyWeightPopup.class)
/*     */       {
/* 361 */         Map localMap = getHeavyWeightPopupCache();
/*     */         List localList;
/* 363 */         if (localMap.containsKey(paramWindow))
/* 364 */           localList = (List)localMap.get(paramWindow);
/*     */         else {
/* 366 */           return null;
/*     */         }
/* 368 */         if (localList.size() > 0) {
/* 369 */           HeavyWeightPopup localHeavyWeightPopup = (HeavyWeightPopup)localList.get(0);
/* 370 */           localList.remove(0);
/* 371 */           return localHeavyWeightPopup;
/*     */         }
/* 373 */         return null;
/*     */       }
/*     */     }
/*     */ 
/*     */     private static Map<Window, List<HeavyWeightPopup>> getHeavyWeightPopupCache()
/*     */     {
/* 383 */       synchronized (HeavyWeightPopup.class) {
/* 384 */         Object localObject1 = (Map)SwingUtilities.appContextGet(heavyWeightPopupCacheKey);
/*     */ 
/* 387 */         if (localObject1 == null) {
/* 388 */           localObject1 = new HashMap(2);
/* 389 */           SwingUtilities.appContextPut(heavyWeightPopupCacheKey, localObject1);
/*     */         }
/*     */ 
/* 392 */         return localObject1;
/*     */       }
/*     */     }
/*     */ 
/*     */     private static void recycleHeavyWeightPopup(HeavyWeightPopup paramHeavyWeightPopup)
/*     */     {
/* 400 */       synchronized (HeavyWeightPopup.class)
/*     */       {
/* 402 */         Window localWindow1 = SwingUtilities.getWindowAncestor(paramHeavyWeightPopup.getComponent());
/*     */ 
/* 404 */         Map localMap = getHeavyWeightPopupCache();
/*     */ 
/* 406 */         if (((localWindow1 instanceof Popup.DefaultFrame)) || (!localWindow1.isVisible()))
/*     */         {
/* 413 */           paramHeavyWeightPopup._dispose();
/*     */           return;
/*     */         }
/*     */         Object localObject1;
/* 415 */         if (localMap.containsKey(localWindow1)) {
/* 416 */           localObject1 = (List)localMap.get(localWindow1);
/*     */         } else {
/* 418 */           localObject1 = new ArrayList();
/* 419 */           localMap.put(localWindow1, localObject1);
/*     */ 
/* 421 */           Window localWindow2 = localWindow1;
/*     */ 
/* 423 */           localWindow2.addWindowListener(new WindowAdapter()
/*     */           {
/*     */             public void windowClosed(WindowEvent paramAnonymousWindowEvent)
/*     */             {
/*     */               List localList;
/* 427 */               synchronized (PopupFactory.HeavyWeightPopup.class) {
/* 428 */                 Map localMap = PopupFactory.HeavyWeightPopup.access$000();
/*     */ 
/* 431 */                 localList = (List)localMap.remove(this.val$w);
/*     */               }
/* 433 */               if (localList != null) {
/* 434 */                 for (int i = localList.size() - 1; 
/* 435 */                   i >= 0; i--) {
/* 436 */                   ((PopupFactory.HeavyWeightPopup)localList.get(i))._dispose();
/*     */                 }
/*     */               }
/*     */             }
/*     */           });
/*     */         }
/*     */ 
/* 443 */         if (((List)localObject1).size() < 5)
/* 444 */           ((List)localObject1).add(paramHeavyWeightPopup);
/*     */         else
/* 446 */           paramHeavyWeightPopup._dispose();
/*     */       }
/*     */     }
/*     */ 
/*     */     public void hide()
/*     */     {
/* 455 */       super.hide();
/* 456 */       recycleHeavyWeightPopup(this);
/*     */     }
/*     */ 
/*     */     void dispose()
/*     */     {
/*     */     }
/*     */ 
/*     */     void _dispose()
/*     */     {
/* 468 */       super.dispose();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class LightWeightPopup extends PopupFactory.ContainerPopup
/*     */   {
/* 641 */     private static final Object lightWeightPopupCacheKey = new StringBuffer("PopupFactory.lightPopupCache");
/*     */ 
/*     */     private LightWeightPopup()
/*     */     {
/* 640 */       super();
/*     */     }
/*     */ 
/*     */     static Popup getLightWeightPopup(Component paramComponent1, Component paramComponent2, int paramInt1, int paramInt2)
/*     */     {
/* 651 */       LightWeightPopup localLightWeightPopup = getRecycledLightWeightPopup();
/*     */ 
/* 653 */       if (localLightWeightPopup == null) {
/* 654 */         localLightWeightPopup = new LightWeightPopup();
/*     */       }
/* 656 */       localLightWeightPopup.reset(paramComponent1, paramComponent2, paramInt1, paramInt2);
/* 657 */       if ((!localLightWeightPopup.fitsOnScreen()) || (localLightWeightPopup.overlappedByOwnedWindow()))
/*     */       {
/* 659 */         localLightWeightPopup.hide();
/* 660 */         return null;
/*     */       }
/* 662 */       return localLightWeightPopup;
/*     */     }
/*     */ 
/*     */     private static List<LightWeightPopup> getLightWeightPopupCache()
/*     */     {
/* 669 */       Object localObject = (List)SwingUtilities.appContextGet(lightWeightPopupCacheKey);
/*     */ 
/* 671 */       if (localObject == null) {
/* 672 */         localObject = new ArrayList();
/* 673 */         SwingUtilities.appContextPut(lightWeightPopupCacheKey, localObject);
/*     */       }
/* 675 */       return localObject;
/*     */     }
/*     */ 
/*     */     private static void recycleLightWeightPopup(LightWeightPopup paramLightWeightPopup)
/*     */     {
/* 682 */       synchronized (LightWeightPopup.class) {
/* 683 */         List localList = getLightWeightPopupCache();
/* 684 */         if (localList.size() < 5)
/* 685 */           localList.add(paramLightWeightPopup);
/*     */       }
/*     */     }
/*     */ 
/*     */     private static LightWeightPopup getRecycledLightWeightPopup()
/*     */     {
/* 695 */       synchronized (LightWeightPopup.class) {
/* 696 */         List localList = getLightWeightPopupCache();
/* 697 */         if (localList.size() > 0) {
/* 698 */           LightWeightPopup localLightWeightPopup = (LightWeightPopup)localList.get(0);
/* 699 */           localList.remove(0);
/* 700 */           return localLightWeightPopup;
/*     */         }
/* 702 */         return null;
/*     */       }
/*     */     }
/*     */ 
/*     */     public void hide()
/*     */     {
/* 712 */       super.hide();
/*     */ 
/* 714 */       Container localContainer = (Container)getComponent();
/*     */ 
/* 716 */       localContainer.removeAll();
/* 717 */       recycleLightWeightPopup(this);
/*     */     }
/*     */     public void show() {
/* 720 */       Object localObject1 = null;
/*     */ 
/* 722 */       if (this.owner != null) {
/* 723 */         localObject1 = (this.owner instanceof Container) ? (Container)this.owner : this.owner.getParent();
/*     */       }
/*     */ 
/* 727 */       for (Object localObject2 = localObject1; localObject2 != null; localObject2 = ((Container)localObject2).getParent()) {
/* 728 */         if ((localObject2 instanceof JRootPane)) {
/* 729 */           if (!(((Container)localObject2).getParent() instanceof JInternalFrame))
/*     */           {
/* 732 */             localObject1 = ((JRootPane)localObject2).getLayeredPane();
/*     */           }
/*     */         }
/* 735 */         else if ((localObject2 instanceof Window)) {
/* 736 */           if (localObject1 == null)
/* 737 */             localObject1 = localObject2;
/*     */         }
/*     */         else {
/* 740 */           if ((localObject2 instanceof JApplet))
/*     */           {
/*     */             break;
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 748 */       localObject2 = SwingUtilities.convertScreenLocationToParent((Container)localObject1, this.x, this.y);
/*     */ 
/* 750 */       Component localComponent = getComponent();
/*     */ 
/* 752 */       localComponent.setLocation(((Point)localObject2).x, ((Point)localObject2).y);
/* 753 */       if ((localObject1 instanceof JLayeredPane))
/* 754 */         ((Container)localObject1).add(localComponent, JLayeredPane.POPUP_LAYER, 0);
/*     */       else
/* 756 */         ((Container)localObject1).add(localComponent);
/*     */     }
/*     */ 
/*     */     Component createComponent(Component paramComponent)
/*     */     {
/* 761 */       JPanel localJPanel = new JPanel(new BorderLayout(), true);
/*     */ 
/* 763 */       localJPanel.setOpaque(true);
/* 764 */       return localJPanel;
/*     */     }
/*     */ 
/*     */     void reset(Component paramComponent1, Component paramComponent2, int paramInt1, int paramInt2)
/*     */     {
/* 776 */       super.reset(paramComponent1, paramComponent2, paramInt1, paramInt2);
/*     */ 
/* 778 */       JComponent localJComponent = (JComponent)getComponent();
/*     */ 
/* 780 */       localJComponent.setOpaque(paramComponent2.isOpaque());
/* 781 */       localJComponent.setLocation(paramInt1, paramInt2);
/* 782 */       localJComponent.add(paramComponent2, "Center");
/* 783 */       paramComponent2.invalidate();
/* 784 */       pack();
/*     */     }
/*     */   }
/*     */   private static class MediumWeightPopup extends PopupFactory.ContainerPopup {
/* 793 */     private static final Object mediumWeightPopupCacheKey = new StringBuffer("PopupFactory.mediumPopupCache");
/*     */     private JRootPane rootPane;
/*     */ 
/* 792 */     private MediumWeightPopup() { super(); }
/*     */ 
/*     */ 
/*     */     static Popup getMediumWeightPopup(Component paramComponent1, Component paramComponent2, int paramInt1, int paramInt2)
/*     */     {
/* 807 */       MediumWeightPopup localMediumWeightPopup = getRecycledMediumWeightPopup();
/*     */ 
/* 809 */       if (localMediumWeightPopup == null) {
/* 810 */         localMediumWeightPopup = new MediumWeightPopup();
/*     */       }
/* 812 */       localMediumWeightPopup.reset(paramComponent1, paramComponent2, paramInt1, paramInt2);
/* 813 */       if ((!localMediumWeightPopup.fitsOnScreen()) || (localMediumWeightPopup.overlappedByOwnedWindow()))
/*     */       {
/* 815 */         localMediumWeightPopup.hide();
/* 816 */         return null;
/*     */       }
/* 818 */       return localMediumWeightPopup;
/*     */     }
/*     */ 
/*     */     private static List<MediumWeightPopup> getMediumWeightPopupCache()
/*     */     {
/* 825 */       Object localObject = (List)SwingUtilities.appContextGet(mediumWeightPopupCacheKey);
/*     */ 
/* 828 */       if (localObject == null) {
/* 829 */         localObject = new ArrayList();
/* 830 */         SwingUtilities.appContextPut(mediumWeightPopupCacheKey, localObject);
/*     */       }
/* 832 */       return localObject;
/*     */     }
/*     */ 
/*     */     private static void recycleMediumWeightPopup(MediumWeightPopup paramMediumWeightPopup)
/*     */     {
/* 839 */       synchronized (MediumWeightPopup.class) {
/* 840 */         List localList = getMediumWeightPopupCache();
/* 841 */         if (localList.size() < 5)
/* 842 */           localList.add(paramMediumWeightPopup);
/*     */       }
/*     */     }
/*     */ 
/*     */     private static MediumWeightPopup getRecycledMediumWeightPopup()
/*     */     {
/* 852 */       synchronized (MediumWeightPopup.class) {
/* 853 */         List localList = getMediumWeightPopupCache();
/* 854 */         if (localList.size() > 0) {
/* 855 */           MediumWeightPopup localMediumWeightPopup = (MediumWeightPopup)localList.get(0);
/* 856 */           localList.remove(0);
/* 857 */           return localMediumWeightPopup;
/*     */         }
/* 859 */         return null;
/*     */       }
/*     */     }
/*     */ 
/*     */     public void hide()
/*     */     {
/* 869 */       super.hide();
/* 870 */       this.rootPane.getContentPane().removeAll();
/* 871 */       recycleMediumWeightPopup(this);
/*     */     }
/*     */     public void show() {
/* 874 */       Component localComponent = getComponent();
/* 875 */       Object localObject = null;
/*     */ 
/* 877 */       if (this.owner != null) {
/* 878 */         localObject = this.owner.getParent();
/*     */       }
/*     */ 
/* 885 */       while ((!(localObject instanceof Window)) && (!(localObject instanceof Applet)) && (localObject != null))
/*     */       {
/* 887 */         localObject = ((Container)localObject).getParent();
/*     */       }
/*     */       Point localPoint;
/* 893 */       if ((localObject instanceof RootPaneContainer)) {
/* 894 */         localObject = ((RootPaneContainer)localObject).getLayeredPane();
/* 895 */         localPoint = SwingUtilities.convertScreenLocationToParent((Container)localObject, this.x, this.y);
/*     */ 
/* 897 */         localComponent.setVisible(false);
/* 898 */         localComponent.setLocation(localPoint.x, localPoint.y);
/* 899 */         ((Container)localObject).add(localComponent, JLayeredPane.POPUP_LAYER, 0);
/*     */       }
/*     */       else {
/* 902 */         localPoint = SwingUtilities.convertScreenLocationToParent((Container)localObject, this.x, this.y);
/*     */ 
/* 905 */         localComponent.setLocation(localPoint.x, localPoint.y);
/* 906 */         localComponent.setVisible(false);
/* 907 */         ((Container)localObject).add(localComponent);
/*     */       }
/* 909 */       localComponent.setVisible(true);
/*     */     }
/*     */ 
/*     */     Component createComponent(Component paramComponent) {
/* 913 */       MediumWeightComponent localMediumWeightComponent = new MediumWeightComponent();
/*     */ 
/* 915 */       this.rootPane = new JRootPane();
/*     */ 
/* 920 */       this.rootPane.setOpaque(true);
/* 921 */       localMediumWeightComponent.add(this.rootPane, "Center");
/* 922 */       return localMediumWeightComponent;
/*     */     }
/*     */ 
/*     */     void reset(Component paramComponent1, Component paramComponent2, int paramInt1, int paramInt2)
/*     */     {
/* 930 */       super.reset(paramComponent1, paramComponent2, paramInt1, paramInt2);
/*     */ 
/* 932 */       Component localComponent = getComponent();
/*     */ 
/* 934 */       localComponent.setLocation(paramInt1, paramInt2);
/* 935 */       this.rootPane.getContentPane().add(paramComponent2, "Center");
/* 936 */       paramComponent2.invalidate();
/* 937 */       localComponent.validate();
/* 938 */       pack();
/*     */     }
/*     */ 
/*     */     private static class MediumWeightComponent extends Panel
/*     */       implements SwingHeavyWeight
/*     */     {
/*     */       MediumWeightComponent()
/*     */       {
/* 947 */         super();
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.PopupFactory
 * JD-Core Version:    0.6.2
 */