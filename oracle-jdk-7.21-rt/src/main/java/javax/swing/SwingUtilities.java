/*      */ package javax.swing;
/*      */ 
/*      */ import java.applet.Applet;
/*      */ import java.awt.Component;
/*      */ import java.awt.ComponentOrientation;
/*      */ import java.awt.Container;
/*      */ import java.awt.EventQueue;
/*      */ import java.awt.FontMetrics;
/*      */ import java.awt.Frame;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.GraphicsEnvironment;
/*      */ import java.awt.HeadlessException;
/*      */ import java.awt.IllegalComponentStateException;
/*      */ import java.awt.Image;
/*      */ import java.awt.Insets;
/*      */ import java.awt.KeyboardFocusManager;
/*      */ import java.awt.Point;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.Toolkit;
/*      */ import java.awt.Window;
/*      */ import java.awt.dnd.DropTarget;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.KeyEvent;
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.awt.event.MouseWheelEvent;
/*      */ import java.awt.event.WindowEvent;
/*      */ import java.awt.event.WindowListener;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.security.AccessController;
/*      */ import javax.accessibility.Accessible;
/*      */ import javax.accessibility.AccessibleComponent;
/*      */ import javax.accessibility.AccessibleContext;
/*      */ import javax.accessibility.AccessibleStateSet;
/*      */ import javax.swing.event.MenuDragMouseEvent;
/*      */ import javax.swing.plaf.UIResource;
/*      */ import javax.swing.text.View;
/*      */ import sun.awt.AppContext;
/*      */ import sun.security.action.GetPropertyAction;
/*      */ import sun.swing.SwingUtilities2;
/*      */ import sun.swing.UIAction;
/*      */ 
/*      */ public class SwingUtilities
/*      */   implements SwingConstants
/*      */ {
/*   58 */   private static boolean canAccessEventQueue = false;
/*   59 */   private static boolean eventQueueTested = false;
/*      */   private static boolean suppressDropSupport;
/*      */   private static boolean checkedSuppressDropSupport;
/* 1756 */   private static final Object sharedOwnerFrameKey = new StringBuffer("SwingUtilities.sharedOwnerFrame");
/*      */ 
/*      */   private static boolean getSuppressDropTarget()
/*      */   {
/*   79 */     if (!checkedSuppressDropSupport) {
/*   80 */       suppressDropSupport = Boolean.valueOf((String)AccessController.doPrivileged(new GetPropertyAction("suppressSwingDropSupport"))).booleanValue();
/*      */ 
/*   83 */       checkedSuppressDropSupport = true;
/*      */     }
/*   85 */     return suppressDropSupport;
/*      */   }
/*      */ 
/*      */   static void installSwingDropTargetAsNecessary(Component paramComponent, TransferHandler paramTransferHandler)
/*      */   {
/*   95 */     if (!getSuppressDropTarget()) {
/*   96 */       DropTarget localDropTarget = paramComponent.getDropTarget();
/*   97 */       if ((localDropTarget == null) || ((localDropTarget instanceof UIResource)))
/*   98 */         if (paramTransferHandler == null)
/*   99 */           paramComponent.setDropTarget(null);
/*  100 */         else if (!GraphicsEnvironment.isHeadless())
/*  101 */           paramComponent.setDropTarget(new TransferHandler.SwingDropTarget(paramComponent));
/*      */     }
/*      */   }
/*      */ 
/*      */   public static final boolean isRectangleContainingRectangle(Rectangle paramRectangle1, Rectangle paramRectangle2)
/*      */   {
/*  111 */     return (paramRectangle2.x >= paramRectangle1.x) && (paramRectangle2.x + paramRectangle2.width <= paramRectangle1.x + paramRectangle1.width) && (paramRectangle2.y >= paramRectangle1.y) && (paramRectangle2.y + paramRectangle2.height <= paramRectangle1.y + paramRectangle1.height);
/*      */   }
/*      */ 
/*      */   public static Rectangle getLocalBounds(Component paramComponent)
/*      */   {
/*  119 */     Rectangle localRectangle = new Rectangle(paramComponent.getBounds());
/*  120 */     localRectangle.x = (localRectangle.y = 0);
/*  121 */     return localRectangle;
/*      */   }
/*      */ 
/*      */   public static Window getWindowAncestor(Component paramComponent)
/*      */   {
/*  137 */     for (Container localContainer = paramComponent.getParent(); localContainer != null; localContainer = localContainer.getParent()) {
/*  138 */       if ((localContainer instanceof Window)) {
/*  139 */         return (Window)localContainer;
/*      */       }
/*      */     }
/*  142 */     return null;
/*      */   }
/*      */ 
/*      */   static Point convertScreenLocationToParent(Container paramContainer, int paramInt1, int paramInt2)
/*      */   {
/*  150 */     for (Container localContainer = paramContainer; localContainer != null; localContainer = localContainer.getParent()) {
/*  151 */       if ((localContainer instanceof Window)) {
/*  152 */         Point localPoint = new Point(paramInt1, paramInt2);
/*      */ 
/*  154 */         convertPointFromScreen(localPoint, paramContainer);
/*  155 */         return localPoint;
/*      */       }
/*      */     }
/*  158 */     throw new Error("convertScreenLocationToParent: no window ancestor");
/*      */   }
/*      */ 
/*      */   public static Point convertPoint(Component paramComponent1, Point paramPoint, Component paramComponent2)
/*      */   {
/*  174 */     if ((paramComponent1 == null) && (paramComponent2 == null))
/*  175 */       return paramPoint;
/*  176 */     if (paramComponent1 == null) {
/*  177 */       paramComponent1 = getWindowAncestor(paramComponent2);
/*  178 */       if (paramComponent1 == null)
/*  179 */         throw new Error("Source component not connected to component tree hierarchy");
/*      */     }
/*  181 */     Point localPoint = new Point(paramPoint);
/*  182 */     convertPointToScreen(localPoint, paramComponent1);
/*  183 */     if (paramComponent2 == null) {
/*  184 */       paramComponent2 = getWindowAncestor(paramComponent1);
/*  185 */       if (paramComponent2 == null)
/*  186 */         throw new Error("Destination component not connected to component tree hierarchy");
/*      */     }
/*  188 */     convertPointFromScreen(localPoint, paramComponent2);
/*  189 */     return localPoint;
/*      */   }
/*      */ 
/*      */   public static Point convertPoint(Component paramComponent1, int paramInt1, int paramInt2, Component paramComponent2)
/*      */   {
/*  203 */     Point localPoint = new Point(paramInt1, paramInt2);
/*  204 */     return convertPoint(paramComponent1, localPoint, paramComponent2);
/*      */   }
/*      */ 
/*      */   public static Rectangle convertRectangle(Component paramComponent1, Rectangle paramRectangle, Component paramComponent2)
/*      */   {
/*  218 */     Point localPoint = new Point(paramRectangle.x, paramRectangle.y);
/*  219 */     localPoint = convertPoint(paramComponent1, localPoint, paramComponent2);
/*  220 */     return new Rectangle(localPoint.x, localPoint.y, paramRectangle.width, paramRectangle.height);
/*      */   }
/*      */ 
/*      */   public static Container getAncestorOfClass(Class<?> paramClass, Component paramComponent)
/*      */   {
/*  230 */     if ((paramComponent == null) || (paramClass == null)) {
/*  231 */       return null;
/*      */     }
/*  233 */     Container localContainer = paramComponent.getParent();
/*  234 */     while ((localContainer != null) && (!paramClass.isInstance(localContainer)))
/*  235 */       localContainer = localContainer.getParent();
/*  236 */     return localContainer;
/*      */   }
/*      */ 
/*      */   public static Container getAncestorNamed(String paramString, Component paramComponent)
/*      */   {
/*  245 */     if ((paramComponent == null) || (paramString == null)) {
/*  246 */       return null;
/*      */     }
/*  248 */     Container localContainer = paramComponent.getParent();
/*  249 */     while ((localContainer != null) && (!paramString.equals(localContainer.getName())))
/*  250 */       localContainer = localContainer.getParent();
/*  251 */     return localContainer;
/*      */   }
/*      */ 
/*      */   public static Component getDeepestComponentAt(Component paramComponent, int paramInt1, int paramInt2)
/*      */   {
/*  267 */     if (!paramComponent.contains(paramInt1, paramInt2)) {
/*  268 */       return null;
/*      */     }
/*  270 */     if ((paramComponent instanceof Container)) {
/*  271 */       Component[] arrayOfComponent1 = ((Container)paramComponent).getComponents();
/*  272 */       for (Component localComponent : arrayOfComponent1) {
/*  273 */         if ((localComponent != null) && (localComponent.isVisible())) {
/*  274 */           Point localPoint = localComponent.getLocation();
/*  275 */           if ((localComponent instanceof Container))
/*  276 */             localComponent = getDeepestComponentAt(localComponent, paramInt1 - localPoint.x, paramInt2 - localPoint.y);
/*      */           else {
/*  278 */             localComponent = localComponent.getComponentAt(paramInt1 - localPoint.x, paramInt2 - localPoint.y);
/*      */           }
/*  280 */           if ((localComponent != null) && (localComponent.isVisible())) {
/*  281 */             return localComponent;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  286 */     return paramComponent;
/*      */   }
/*      */ 
/*      */   public static MouseEvent convertMouseEvent(Component paramComponent1, MouseEvent paramMouseEvent, Component paramComponent2)
/*      */   {
/*  306 */     Point localPoint = convertPoint(paramComponent1, new Point(paramMouseEvent.getX(), paramMouseEvent.getY()), paramComponent2);
/*      */     Component localComponent;
/*  311 */     if (paramComponent2 != null)
/*  312 */       localComponent = paramComponent2;
/*      */     else
/*  314 */       localComponent = paramComponent1;
/*      */     Object localObject2;
/*      */     Object localObject1;
/*  317 */     if ((paramMouseEvent instanceof MouseWheelEvent)) {
/*  318 */       localObject2 = (MouseWheelEvent)paramMouseEvent;
/*  319 */       localObject1 = new MouseWheelEvent(localComponent, ((MouseWheelEvent)localObject2).getID(), ((MouseWheelEvent)localObject2).getWhen(), ((MouseWheelEvent)localObject2).getModifiers() | ((MouseWheelEvent)localObject2).getModifiersEx(), localPoint.x, localPoint.y, ((MouseWheelEvent)localObject2).getXOnScreen(), ((MouseWheelEvent)localObject2).getYOnScreen(), ((MouseWheelEvent)localObject2).getClickCount(), ((MouseWheelEvent)localObject2).isPopupTrigger(), ((MouseWheelEvent)localObject2).getScrollType(), ((MouseWheelEvent)localObject2).getScrollAmount(), ((MouseWheelEvent)localObject2).getWheelRotation());
/*      */     }
/*  333 */     else if ((paramMouseEvent instanceof MenuDragMouseEvent)) {
/*  334 */       localObject2 = (MenuDragMouseEvent)paramMouseEvent;
/*  335 */       localObject1 = new MenuDragMouseEvent(localComponent, ((MenuDragMouseEvent)localObject2).getID(), ((MenuDragMouseEvent)localObject2).getWhen(), ((MenuDragMouseEvent)localObject2).getModifiers() | ((MenuDragMouseEvent)localObject2).getModifiersEx(), localPoint.x, localPoint.y, ((MenuDragMouseEvent)localObject2).getXOnScreen(), ((MenuDragMouseEvent)localObject2).getYOnScreen(), ((MenuDragMouseEvent)localObject2).getClickCount(), ((MenuDragMouseEvent)localObject2).isPopupTrigger(), ((MenuDragMouseEvent)localObject2).getPath(), ((MenuDragMouseEvent)localObject2).getMenuSelectionManager());
/*      */     }
/*      */     else
/*      */     {
/*  349 */       localObject1 = new MouseEvent(localComponent, paramMouseEvent.getID(), paramMouseEvent.getWhen(), paramMouseEvent.getModifiers() | paramMouseEvent.getModifiersEx(), localPoint.x, localPoint.y, paramMouseEvent.getXOnScreen(), paramMouseEvent.getYOnScreen(), paramMouseEvent.getClickCount(), paramMouseEvent.isPopupTrigger(), 0);
/*      */     }
/*      */ 
/*  361 */     return localObject1;
/*      */   }
/*      */ 
/*      */   public static void convertPointToScreen(Point paramPoint, Component paramComponent)
/*      */   {
/*      */     do
/*      */     {
/*      */       int i;
/*      */       int j;
/*  377 */       if ((paramComponent instanceof JComponent)) {
/*  378 */         i = paramComponent.getX();
/*  379 */         j = paramComponent.getY();
/*  380 */       } else if (((paramComponent instanceof Applet)) || ((paramComponent instanceof Window)))
/*      */       {
/*      */         try {
/*  383 */           Point localPoint = paramComponent.getLocationOnScreen();
/*  384 */           i = localPoint.x;
/*  385 */           j = localPoint.y;
/*      */         } catch (IllegalComponentStateException localIllegalComponentStateException) {
/*  387 */           i = paramComponent.getX();
/*  388 */           j = paramComponent.getY();
/*      */         }
/*      */       } else {
/*  391 */         i = paramComponent.getX();
/*  392 */         j = paramComponent.getY();
/*      */       }
/*      */ 
/*  395 */       paramPoint.x += i;
/*  396 */       paramPoint.y += j;
/*      */ 
/*  398 */       if (((paramComponent instanceof Window)) || ((paramComponent instanceof Applet)))
/*      */         break;
/*  400 */       paramComponent = paramComponent.getParent();
/*  401 */     }while (paramComponent != null);
/*      */   }
/*      */ 
/*      */   public static void convertPointFromScreen(Point paramPoint, Component paramComponent)
/*      */   {
/*      */     do
/*      */     {
/*      */       int i;
/*      */       int j;
/*  416 */       if ((paramComponent instanceof JComponent)) {
/*  417 */         i = paramComponent.getX();
/*  418 */         j = paramComponent.getY();
/*  419 */       } else if (((paramComponent instanceof Applet)) || ((paramComponent instanceof Window)))
/*      */       {
/*      */         try {
/*  422 */           Point localPoint = paramComponent.getLocationOnScreen();
/*  423 */           i = localPoint.x;
/*  424 */           j = localPoint.y;
/*      */         } catch (IllegalComponentStateException localIllegalComponentStateException) {
/*  426 */           i = paramComponent.getX();
/*  427 */           j = paramComponent.getY();
/*      */         }
/*      */       } else {
/*  430 */         i = paramComponent.getX();
/*  431 */         j = paramComponent.getY();
/*      */       }
/*      */ 
/*  434 */       paramPoint.x -= i;
/*  435 */       paramPoint.y -= j;
/*      */ 
/*  437 */       if (((paramComponent instanceof Window)) || ((paramComponent instanceof Applet)))
/*      */         break;
/*  439 */       paramComponent = paramComponent.getParent();
/*  440 */     }while (paramComponent != null);
/*      */   }
/*      */ 
/*      */   public static Window windowForComponent(Component paramComponent)
/*      */   {
/*  457 */     return getWindowAncestor(paramComponent);
/*      */   }
/*      */ 
/*      */   public static boolean isDescendingFrom(Component paramComponent1, Component paramComponent2)
/*      */   {
/*  464 */     if (paramComponent1 == paramComponent2)
/*  465 */       return true;
/*  466 */     for (Container localContainer = paramComponent1.getParent(); localContainer != null; localContainer = localContainer.getParent())
/*  467 */       if (localContainer == paramComponent2)
/*  468 */         return true;
/*  469 */     return false;
/*      */   }
/*      */ 
/*      */   public static Rectangle computeIntersection(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Rectangle paramRectangle)
/*      */   {
/*  489 */     int i = paramInt1 > paramRectangle.x ? paramInt1 : paramRectangle.x;
/*  490 */     int j = paramInt1 + paramInt3 < paramRectangle.x + paramRectangle.width ? paramInt1 + paramInt3 : paramRectangle.x + paramRectangle.width;
/*  491 */     int k = paramInt2 > paramRectangle.y ? paramInt2 : paramRectangle.y;
/*  492 */     int m = paramInt2 + paramInt4 < paramRectangle.y + paramRectangle.height ? paramInt2 + paramInt4 : paramRectangle.y + paramRectangle.height;
/*      */ 
/*  494 */     paramRectangle.x = i;
/*  495 */     paramRectangle.y = k;
/*  496 */     paramRectangle.width = (j - i);
/*  497 */     paramRectangle.height = (m - k);
/*      */ 
/*  500 */     if ((paramRectangle.width < 0) || (paramRectangle.height < 0)) {
/*  501 */       paramRectangle.x = (paramRectangle.y = paramRectangle.width = paramRectangle.height = 0);
/*      */     }
/*      */ 
/*  504 */     return paramRectangle;
/*      */   }
/*      */ 
/*      */   public static Rectangle computeUnion(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Rectangle paramRectangle)
/*      */   {
/*  520 */     int i = paramInt1 < paramRectangle.x ? paramInt1 : paramRectangle.x;
/*  521 */     int j = paramInt1 + paramInt3 > paramRectangle.x + paramRectangle.width ? paramInt1 + paramInt3 : paramRectangle.x + paramRectangle.width;
/*  522 */     int k = paramInt2 < paramRectangle.y ? paramInt2 : paramRectangle.y;
/*  523 */     int m = paramInt2 + paramInt4 > paramRectangle.y + paramRectangle.height ? paramInt2 + paramInt4 : paramRectangle.y + paramRectangle.height;
/*      */ 
/*  525 */     paramRectangle.x = i;
/*  526 */     paramRectangle.y = k;
/*  527 */     paramRectangle.width = (j - i);
/*  528 */     paramRectangle.height = (m - k);
/*  529 */     return paramRectangle;
/*      */   }
/*      */ 
/*      */   public static Rectangle[] computeDifference(Rectangle paramRectangle1, Rectangle paramRectangle2)
/*      */   {
/*  538 */     if ((paramRectangle2 == null) || (!paramRectangle1.intersects(paramRectangle2)) || (isRectangleContainingRectangle(paramRectangle2, paramRectangle1))) {
/*  539 */       return new Rectangle[0];
/*      */     }
/*      */ 
/*  542 */     Rectangle localRectangle1 = new Rectangle();
/*  543 */     Rectangle localRectangle2 = null; Rectangle localRectangle3 = null; Rectangle localRectangle4 = null; Rectangle localRectangle5 = null;
/*      */ 
/*  545 */     int i = 0;
/*      */ 
/*  548 */     if (isRectangleContainingRectangle(paramRectangle1, paramRectangle2)) {
/*  549 */       localRectangle1.x = paramRectangle1.x; localRectangle1.y = paramRectangle1.y; localRectangle1.width = (paramRectangle2.x - paramRectangle1.x); localRectangle1.height = paramRectangle1.height;
/*  550 */       if ((localRectangle1.width > 0) && (localRectangle1.height > 0)) {
/*  551 */         localRectangle2 = new Rectangle(localRectangle1);
/*  552 */         i++;
/*      */       }
/*      */ 
/*  555 */       localRectangle1.x = paramRectangle2.x; localRectangle1.y = paramRectangle1.y; localRectangle1.width = paramRectangle2.width; localRectangle1.height = (paramRectangle2.y - paramRectangle1.y);
/*  556 */       if ((localRectangle1.width > 0) && (localRectangle1.height > 0)) {
/*  557 */         localRectangle3 = new Rectangle(localRectangle1);
/*  558 */         i++;
/*      */       }
/*      */ 
/*  561 */       localRectangle1.x = paramRectangle2.x; paramRectangle2.y += paramRectangle2.height; localRectangle1.width = paramRectangle2.width;
/*  562 */       localRectangle1.height = (paramRectangle1.y + paramRectangle1.height - (paramRectangle2.y + paramRectangle2.height));
/*  563 */       if ((localRectangle1.width > 0) && (localRectangle1.height > 0)) {
/*  564 */         localRectangle4 = new Rectangle(localRectangle1);
/*  565 */         i++;
/*      */       }
/*      */ 
/*  568 */       paramRectangle2.x += paramRectangle2.width; localRectangle1.y = paramRectangle1.y; localRectangle1.width = (paramRectangle1.x + paramRectangle1.width - (paramRectangle2.x + paramRectangle2.width));
/*  569 */       localRectangle1.height = paramRectangle1.height;
/*  570 */       if ((localRectangle1.width > 0) && (localRectangle1.height > 0)) {
/*  571 */         localRectangle5 = new Rectangle(localRectangle1);
/*  572 */         i++;
/*      */       }
/*      */ 
/*      */     }
/*  576 */     else if ((paramRectangle2.x <= paramRectangle1.x) && (paramRectangle2.y <= paramRectangle1.y)) {
/*  577 */       if (paramRectangle2.x + paramRectangle2.width > paramRectangle1.x + paramRectangle1.width)
/*      */       {
/*  579 */         localRectangle1.x = paramRectangle1.x; paramRectangle2.y += paramRectangle2.height;
/*  580 */         localRectangle1.width = paramRectangle1.width; localRectangle1.height = (paramRectangle1.y + paramRectangle1.height - (paramRectangle2.y + paramRectangle2.height));
/*  581 */         if ((localRectangle1.width > 0) && (localRectangle1.height > 0)) {
/*  582 */           localRectangle2 = localRectangle1;
/*  583 */           i++;
/*      */         }
/*  585 */       } else if (paramRectangle2.y + paramRectangle2.height > paramRectangle1.y + paramRectangle1.height) {
/*  586 */         localRectangle1.setBounds(paramRectangle2.x + paramRectangle2.width, paramRectangle1.y, paramRectangle1.x + paramRectangle1.width - (paramRectangle2.x + paramRectangle2.width), paramRectangle1.height);
/*      */ 
/*  588 */         if ((localRectangle1.width > 0) && (localRectangle1.height > 0)) {
/*  589 */           localRectangle2 = localRectangle1;
/*  590 */           i++;
/*      */         }
/*      */       } else {
/*  593 */         localRectangle1.setBounds(paramRectangle2.x + paramRectangle2.width, paramRectangle1.y, paramRectangle1.x + paramRectangle1.width - (paramRectangle2.x + paramRectangle2.width), paramRectangle2.y + paramRectangle2.height - paramRectangle1.y);
/*      */ 
/*  596 */         if ((localRectangle1.width > 0) && (localRectangle1.height > 0)) {
/*  597 */           localRectangle2 = new Rectangle(localRectangle1);
/*  598 */           i++;
/*      */         }
/*      */ 
/*  601 */         localRectangle1.setBounds(paramRectangle1.x, paramRectangle2.y + paramRectangle2.height, paramRectangle1.width, paramRectangle1.y + paramRectangle1.height - (paramRectangle2.y + paramRectangle2.height));
/*      */ 
/*  603 */         if ((localRectangle1.width > 0) && (localRectangle1.height > 0)) {
/*  604 */           localRectangle3 = new Rectangle(localRectangle1);
/*  605 */           i++;
/*      */         }
/*      */       }
/*  608 */     } else if ((paramRectangle2.x <= paramRectangle1.x) && (paramRectangle2.y + paramRectangle2.height >= paramRectangle1.y + paramRectangle1.height)) {
/*  609 */       if (paramRectangle2.x + paramRectangle2.width > paramRectangle1.x + paramRectangle1.width) {
/*  610 */         localRectangle1.setBounds(paramRectangle1.x, paramRectangle1.y, paramRectangle1.width, paramRectangle2.y - paramRectangle1.y);
/*  611 */         if ((localRectangle1.width > 0) && (localRectangle1.height > 0)) {
/*  612 */           localRectangle2 = localRectangle1;
/*  613 */           i++;
/*      */         }
/*      */       } else {
/*  616 */         localRectangle1.setBounds(paramRectangle1.x, paramRectangle1.y, paramRectangle1.width, paramRectangle2.y - paramRectangle1.y);
/*  617 */         if ((localRectangle1.width > 0) && (localRectangle1.height > 0)) {
/*  618 */           localRectangle2 = new Rectangle(localRectangle1);
/*  619 */           i++;
/*      */         }
/*  621 */         localRectangle1.setBounds(paramRectangle2.x + paramRectangle2.width, paramRectangle2.y, paramRectangle1.x + paramRectangle1.width - (paramRectangle2.x + paramRectangle2.width), paramRectangle1.y + paramRectangle1.height - paramRectangle2.y);
/*      */ 
/*  624 */         if ((localRectangle1.width > 0) && (localRectangle1.height > 0)) {
/*  625 */           localRectangle3 = new Rectangle(localRectangle1);
/*  626 */           i++;
/*      */         }
/*      */       }
/*  629 */     } else if (paramRectangle2.x <= paramRectangle1.x) {
/*  630 */       if (paramRectangle2.x + paramRectangle2.width >= paramRectangle1.x + paramRectangle1.width) {
/*  631 */         localRectangle1.setBounds(paramRectangle1.x, paramRectangle1.y, paramRectangle1.width, paramRectangle2.y - paramRectangle1.y);
/*  632 */         if ((localRectangle1.width > 0) && (localRectangle1.height > 0)) {
/*  633 */           localRectangle2 = new Rectangle(localRectangle1);
/*  634 */           i++;
/*      */         }
/*      */ 
/*  637 */         localRectangle1.setBounds(paramRectangle1.x, paramRectangle2.y + paramRectangle2.height, paramRectangle1.width, paramRectangle1.y + paramRectangle1.height - (paramRectangle2.y + paramRectangle2.height));
/*      */ 
/*  639 */         if ((localRectangle1.width > 0) && (localRectangle1.height > 0)) {
/*  640 */           localRectangle3 = new Rectangle(localRectangle1);
/*  641 */           i++;
/*      */         }
/*      */       } else {
/*  644 */         localRectangle1.setBounds(paramRectangle1.x, paramRectangle1.y, paramRectangle1.width, paramRectangle2.y - paramRectangle1.y);
/*  645 */         if ((localRectangle1.width > 0) && (localRectangle1.height > 0)) {
/*  646 */           localRectangle2 = new Rectangle(localRectangle1);
/*  647 */           i++;
/*      */         }
/*      */ 
/*  650 */         localRectangle1.setBounds(paramRectangle2.x + paramRectangle2.width, paramRectangle2.y, paramRectangle1.x + paramRectangle1.width - (paramRectangle2.x + paramRectangle2.width), paramRectangle2.height);
/*      */ 
/*  653 */         if ((localRectangle1.width > 0) && (localRectangle1.height > 0)) {
/*  654 */           localRectangle3 = new Rectangle(localRectangle1);
/*  655 */           i++;
/*      */         }
/*      */ 
/*  658 */         localRectangle1.setBounds(paramRectangle1.x, paramRectangle2.y + paramRectangle2.height, paramRectangle1.width, paramRectangle1.y + paramRectangle1.height - (paramRectangle2.y + paramRectangle2.height));
/*      */ 
/*  660 */         if ((localRectangle1.width > 0) && (localRectangle1.height > 0)) {
/*  661 */           localRectangle4 = new Rectangle(localRectangle1);
/*  662 */           i++;
/*      */         }
/*      */       }
/*  665 */     } else if ((paramRectangle2.x <= paramRectangle1.x + paramRectangle1.width) && (paramRectangle2.x + paramRectangle2.width > paramRectangle1.x + paramRectangle1.width)) {
/*  666 */       if ((paramRectangle2.y <= paramRectangle1.y) && (paramRectangle2.y + paramRectangle2.height > paramRectangle1.y + paramRectangle1.height)) {
/*  667 */         localRectangle1.setBounds(paramRectangle1.x, paramRectangle1.y, paramRectangle2.x - paramRectangle1.x, paramRectangle1.height);
/*  668 */         if ((localRectangle1.width > 0) && (localRectangle1.height > 0)) {
/*  669 */           localRectangle2 = localRectangle1;
/*  670 */           i++;
/*      */         }
/*  672 */       } else if (paramRectangle2.y <= paramRectangle1.y) {
/*  673 */         localRectangle1.setBounds(paramRectangle1.x, paramRectangle1.y, paramRectangle2.x - paramRectangle1.x, paramRectangle2.y + paramRectangle2.height - paramRectangle1.y);
/*      */ 
/*  675 */         if ((localRectangle1.width > 0) && (localRectangle1.height > 0)) {
/*  676 */           localRectangle2 = new Rectangle(localRectangle1);
/*  677 */           i++;
/*      */         }
/*      */ 
/*  680 */         localRectangle1.setBounds(paramRectangle1.x, paramRectangle2.y + paramRectangle2.height, paramRectangle1.width, paramRectangle1.y + paramRectangle1.height - (paramRectangle2.y + paramRectangle2.height));
/*      */ 
/*  682 */         if ((localRectangle1.width > 0) && (localRectangle1.height > 0)) {
/*  683 */           localRectangle3 = new Rectangle(localRectangle1);
/*  684 */           i++;
/*      */         }
/*  686 */       } else if (paramRectangle2.y + paramRectangle2.height > paramRectangle1.y + paramRectangle1.height) {
/*  687 */         localRectangle1.setBounds(paramRectangle1.x, paramRectangle1.y, paramRectangle1.width, paramRectangle2.y - paramRectangle1.y);
/*  688 */         if ((localRectangle1.width > 0) && (localRectangle1.height > 0)) {
/*  689 */           localRectangle2 = new Rectangle(localRectangle1);
/*  690 */           i++;
/*      */         }
/*      */ 
/*  693 */         localRectangle1.setBounds(paramRectangle1.x, paramRectangle2.y, paramRectangle2.x - paramRectangle1.x, paramRectangle1.y + paramRectangle1.height - paramRectangle2.y);
/*      */ 
/*  695 */         if ((localRectangle1.width > 0) && (localRectangle1.height > 0)) {
/*  696 */           localRectangle3 = new Rectangle(localRectangle1);
/*  697 */           i++;
/*      */         }
/*      */       } else {
/*  700 */         localRectangle1.setBounds(paramRectangle1.x, paramRectangle1.y, paramRectangle1.width, paramRectangle2.y - paramRectangle1.y);
/*  701 */         if ((localRectangle1.width > 0) && (localRectangle1.height > 0)) {
/*  702 */           localRectangle2 = new Rectangle(localRectangle1);
/*  703 */           i++;
/*      */         }
/*      */ 
/*  706 */         localRectangle1.setBounds(paramRectangle1.x, paramRectangle2.y, paramRectangle2.x - paramRectangle1.x, paramRectangle2.height);
/*      */ 
/*  708 */         if ((localRectangle1.width > 0) && (localRectangle1.height > 0)) {
/*  709 */           localRectangle3 = new Rectangle(localRectangle1);
/*  710 */           i++;
/*      */         }
/*      */ 
/*  713 */         localRectangle1.setBounds(paramRectangle1.x, paramRectangle2.y + paramRectangle2.height, paramRectangle1.width, paramRectangle1.y + paramRectangle1.height - (paramRectangle2.y + paramRectangle2.height));
/*      */ 
/*  715 */         if ((localRectangle1.width > 0) && (localRectangle1.height > 0)) {
/*  716 */           localRectangle4 = new Rectangle(localRectangle1);
/*  717 */           i++;
/*      */         }
/*      */       }
/*  720 */     } else if ((paramRectangle2.x >= paramRectangle1.x) && (paramRectangle2.x + paramRectangle2.width <= paramRectangle1.x + paramRectangle1.width)) {
/*  721 */       if ((paramRectangle2.y <= paramRectangle1.y) && (paramRectangle2.y + paramRectangle2.height > paramRectangle1.y + paramRectangle1.height)) {
/*  722 */         localRectangle1.setBounds(paramRectangle1.x, paramRectangle1.y, paramRectangle2.x - paramRectangle1.x, paramRectangle1.height);
/*  723 */         if ((localRectangle1.width > 0) && (localRectangle1.height > 0)) {
/*  724 */           localRectangle2 = new Rectangle(localRectangle1);
/*  725 */           i++;
/*      */         }
/*  727 */         localRectangle1.setBounds(paramRectangle2.x + paramRectangle2.width, paramRectangle1.y, paramRectangle1.x + paramRectangle1.width - (paramRectangle2.x + paramRectangle2.width), paramRectangle1.height);
/*      */ 
/*  729 */         if ((localRectangle1.width > 0) && (localRectangle1.height > 0)) {
/*  730 */           localRectangle3 = new Rectangle(localRectangle1);
/*  731 */           i++;
/*      */         }
/*  733 */       } else if (paramRectangle2.y <= paramRectangle1.y) {
/*  734 */         localRectangle1.setBounds(paramRectangle1.x, paramRectangle1.y, paramRectangle2.x - paramRectangle1.x, paramRectangle1.height);
/*  735 */         if ((localRectangle1.width > 0) && (localRectangle1.height > 0)) {
/*  736 */           localRectangle2 = new Rectangle(localRectangle1);
/*  737 */           i++;
/*      */         }
/*      */ 
/*  740 */         localRectangle1.setBounds(paramRectangle2.x, paramRectangle2.y + paramRectangle2.height, paramRectangle2.width, paramRectangle1.y + paramRectangle1.height - (paramRectangle2.y + paramRectangle2.height));
/*      */ 
/*  743 */         if ((localRectangle1.width > 0) && (localRectangle1.height > 0)) {
/*  744 */           localRectangle3 = new Rectangle(localRectangle1);
/*  745 */           i++;
/*      */         }
/*      */ 
/*  748 */         localRectangle1.setBounds(paramRectangle2.x + paramRectangle2.width, paramRectangle1.y, paramRectangle1.x + paramRectangle1.width - (paramRectangle2.x + paramRectangle2.width), paramRectangle1.height);
/*      */ 
/*  750 */         if ((localRectangle1.width > 0) && (localRectangle1.height > 0)) {
/*  751 */           localRectangle4 = new Rectangle(localRectangle1);
/*  752 */           i++;
/*      */         }
/*      */       } else {
/*  755 */         localRectangle1.setBounds(paramRectangle1.x, paramRectangle1.y, paramRectangle2.x - paramRectangle1.x, paramRectangle1.height);
/*  756 */         if ((localRectangle1.width > 0) && (localRectangle1.height > 0)) {
/*  757 */           localRectangle2 = new Rectangle(localRectangle1);
/*  758 */           i++;
/*      */         }
/*      */ 
/*  761 */         localRectangle1.setBounds(paramRectangle2.x, paramRectangle1.y, paramRectangle2.width, paramRectangle2.y - paramRectangle1.y);
/*      */ 
/*  763 */         if ((localRectangle1.width > 0) && (localRectangle1.height > 0)) {
/*  764 */           localRectangle3 = new Rectangle(localRectangle1);
/*  765 */           i++;
/*      */         }
/*      */ 
/*  768 */         localRectangle1.setBounds(paramRectangle2.x + paramRectangle2.width, paramRectangle1.y, paramRectangle1.x + paramRectangle1.width - (paramRectangle2.x + paramRectangle2.width), paramRectangle1.height);
/*      */ 
/*  770 */         if ((localRectangle1.width > 0) && (localRectangle1.height > 0)) {
/*  771 */           localRectangle4 = new Rectangle(localRectangle1);
/*  772 */           i++;
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  778 */     Rectangle[] arrayOfRectangle = new Rectangle[i];
/*  779 */     i = 0;
/*  780 */     if (localRectangle2 != null)
/*  781 */       arrayOfRectangle[(i++)] = localRectangle2;
/*  782 */     if (localRectangle3 != null)
/*  783 */       arrayOfRectangle[(i++)] = localRectangle3;
/*  784 */     if (localRectangle4 != null)
/*  785 */       arrayOfRectangle[(i++)] = localRectangle4;
/*  786 */     if (localRectangle5 != null)
/*  787 */       arrayOfRectangle[(i++)] = localRectangle5;
/*  788 */     return arrayOfRectangle;
/*      */   }
/*      */ 
/*      */   public static boolean isLeftMouseButton(MouseEvent paramMouseEvent)
/*      */   {
/*  798 */     return (paramMouseEvent.getModifiers() & 0x10) != 0;
/*      */   }
/*      */ 
/*      */   public static boolean isMiddleMouseButton(MouseEvent paramMouseEvent)
/*      */   {
/*  808 */     return (paramMouseEvent.getModifiers() & 0x8) == 8;
/*      */   }
/*      */ 
/*      */   public static boolean isRightMouseButton(MouseEvent paramMouseEvent)
/*      */   {
/*  818 */     return (paramMouseEvent.getModifiers() & 0x4) == 4;
/*      */   }
/*      */ 
/*      */   public static int computeStringWidth(FontMetrics paramFontMetrics, String paramString)
/*      */   {
/*  833 */     return SwingUtilities2.stringWidth(null, paramFontMetrics, paramString);
/*      */   }
/*      */ 
/*      */   public static String layoutCompoundLabel(JComponent paramJComponent, FontMetrics paramFontMetrics, String paramString, Icon paramIcon, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Rectangle paramRectangle1, Rectangle paramRectangle2, Rectangle paramRectangle3, int paramInt5)
/*      */   {
/*  857 */     int i = 1;
/*  858 */     int j = paramInt2;
/*  859 */     int k = paramInt4;
/*      */ 
/*  861 */     if ((paramJComponent != null) && 
/*  862 */       (!paramJComponent.getComponentOrientation().isLeftToRight())) {
/*  863 */       i = 0;
/*      */     }
/*      */ 
/*  869 */     switch (paramInt2) {
/*      */     case 10:
/*  871 */       j = i != 0 ? 2 : 4;
/*  872 */       break;
/*      */     case 11:
/*  874 */       j = i != 0 ? 4 : 2;
/*      */     }
/*      */ 
/*  880 */     switch (paramInt4) {
/*      */     case 10:
/*  882 */       k = i != 0 ? 2 : 4;
/*  883 */       break;
/*      */     case 11:
/*  885 */       k = i != 0 ? 4 : 2;
/*      */     }
/*      */ 
/*  889 */     return layoutCompoundLabelImpl(paramJComponent, paramFontMetrics, paramString, paramIcon, paramInt1, j, paramInt3, k, paramRectangle1, paramRectangle2, paramRectangle3, paramInt5);
/*      */   }
/*      */ 
/*      */   public static String layoutCompoundLabel(FontMetrics paramFontMetrics, String paramString, Icon paramIcon, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Rectangle paramRectangle1, Rectangle paramRectangle2, Rectangle paramRectangle3, int paramInt5)
/*      */   {
/*  926 */     return layoutCompoundLabelImpl(null, paramFontMetrics, paramString, paramIcon, paramInt1, paramInt2, paramInt3, paramInt4, paramRectangle1, paramRectangle2, paramRectangle3, paramInt5);
/*      */   }
/*      */ 
/*      */   private static String layoutCompoundLabelImpl(JComponent paramJComponent, FontMetrics paramFontMetrics, String paramString, Icon paramIcon, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Rectangle paramRectangle1, Rectangle paramRectangle2, Rectangle paramRectangle3, int paramInt5)
/*      */   {
/*  961 */     if (paramIcon != null) {
/*  962 */       paramRectangle2.width = paramIcon.getIconWidth();
/*  963 */       paramRectangle2.height = paramIcon.getIconHeight();
/*      */     }
/*      */     else {
/*  966 */       paramRectangle2.width = (paramRectangle2.height = 0);
/*      */     }
/*      */ 
/*  974 */     int i = (paramString == null) || (paramString.equals("")) ? 1 : 0;
/*  975 */     int j = 0;
/*  976 */     int k = 0;
/*      */     int m;
/*  983 */     if (i != 0) {
/*  984 */       paramRectangle3.width = (paramRectangle3.height = 0);
/*  985 */       paramString = "";
/*  986 */       m = 0;
/*      */     }
/*      */     else
/*      */     {
/*  990 */       m = paramIcon == null ? 0 : paramInt5;
/*      */ 
/*  992 */       if (paramInt4 == 0) {
/*  993 */         n = paramRectangle1.width;
/*      */       }
/*      */       else {
/*  996 */         n = paramRectangle1.width - (paramRectangle2.width + m);
/*      */       }
/*  998 */       Object localObject = paramJComponent != null ? (View)paramJComponent.getClientProperty("html") : null;
/*  999 */       if (localObject != null) {
/* 1000 */         paramRectangle3.width = Math.min(n, (int)localObject.getPreferredSpan(0));
/*      */ 
/* 1002 */         paramRectangle3.height = ((int)localObject.getPreferredSpan(1));
/*      */       } else {
/* 1004 */         paramRectangle3.width = SwingUtilities2.stringWidth(paramJComponent, paramFontMetrics, paramString);
/* 1005 */         j = SwingUtilities2.getLeftSideBearing(paramJComponent, paramFontMetrics, paramString);
/* 1006 */         if (j < 0)
/*      */         {
/* 1017 */           paramRectangle3.width -= j;
/*      */         }
/* 1019 */         if (paramRectangle3.width > n) {
/* 1020 */           paramString = SwingUtilities2.clipString(paramJComponent, paramFontMetrics, paramString, n);
/*      */ 
/* 1022 */           paramRectangle3.width = SwingUtilities2.stringWidth(paramJComponent, paramFontMetrics, paramString);
/*      */         }
/* 1024 */         paramRectangle3.height = paramFontMetrics.getHeight();
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1033 */     if (paramInt3 == 1) {
/* 1034 */       if (paramInt4 != 0) {
/* 1035 */         paramRectangle3.y = 0;
/*      */       }
/*      */       else {
/* 1038 */         paramRectangle3.y = (-(paramRectangle3.height + m));
/*      */       }
/*      */     }
/* 1041 */     else if (paramInt3 == 0) {
/* 1042 */       paramRectangle3.y = (paramRectangle2.height / 2 - paramRectangle3.height / 2);
/*      */     }
/* 1045 */     else if (paramInt4 != 0) {
/* 1046 */       paramRectangle3.y = (paramRectangle2.height - paramRectangle3.height);
/*      */     }
/*      */     else {
/* 1049 */       paramRectangle3.y = (paramRectangle2.height + m);
/*      */     }
/*      */ 
/* 1053 */     if (paramInt4 == 2) {
/* 1054 */       paramRectangle3.x = (-(paramRectangle3.width + m));
/*      */     }
/* 1056 */     else if (paramInt4 == 0) {
/* 1057 */       paramRectangle3.x = (paramRectangle2.width / 2 - paramRectangle3.width / 2);
/*      */     }
/*      */     else {
/* 1060 */       paramRectangle3.x = (paramRectangle2.width + m);
/*      */     }
/*      */ 
/* 1074 */     int n = Math.min(paramRectangle2.x, paramRectangle3.x);
/* 1075 */     int i1 = Math.max(paramRectangle2.x + paramRectangle2.width, paramRectangle3.x + paramRectangle3.width) - n;
/*      */ 
/* 1077 */     int i2 = Math.min(paramRectangle2.y, paramRectangle3.y);
/* 1078 */     int i3 = Math.max(paramRectangle2.y + paramRectangle2.height, paramRectangle3.y + paramRectangle3.height) - i2;
/*      */     int i5;
/* 1083 */     if (paramInt1 == 1) {
/* 1084 */       i5 = paramRectangle1.y - i2;
/*      */     }
/* 1086 */     else if (paramInt1 == 0) {
/* 1087 */       i5 = paramRectangle1.y + paramRectangle1.height / 2 - (i2 + i3 / 2);
/*      */     }
/*      */     else
/* 1090 */       i5 = paramRectangle1.y + paramRectangle1.height - (i2 + i3);
/*      */     int i4;
/* 1093 */     if (paramInt2 == 2) {
/* 1094 */       i4 = paramRectangle1.x - n;
/*      */     }
/* 1096 */     else if (paramInt2 == 4) {
/* 1097 */       i4 = paramRectangle1.x + paramRectangle1.width - (n + i1);
/*      */     }
/*      */     else {
/* 1100 */       i4 = paramRectangle1.x + paramRectangle1.width / 2 - (n + i1 / 2);
/*      */     }
/*      */ 
/* 1107 */     paramRectangle3.x += i4;
/* 1108 */     paramRectangle3.y += i5;
/*      */ 
/* 1110 */     paramRectangle2.x += i4;
/* 1111 */     paramRectangle2.y += i5;
/*      */ 
/* 1113 */     if (j < 0)
/*      */     {
/* 1116 */       paramRectangle3.x -= j;
/*      */ 
/* 1118 */       paramRectangle3.width += j;
/*      */     }
/* 1120 */     if (k > 0) {
/* 1121 */       paramRectangle3.width -= k;
/*      */     }
/*      */ 
/* 1124 */     return paramString;
/*      */   }
/*      */ 
/*      */   public static void paintComponent(Graphics paramGraphics, Component paramComponent, Container paramContainer, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/* 1176 */     getCellRendererPane(paramComponent, paramContainer).paintComponent(paramGraphics, paramComponent, paramContainer, paramInt1, paramInt2, paramInt3, paramInt4, false);
/*      */   }
/*      */ 
/*      */   public static void paintComponent(Graphics paramGraphics, Component paramComponent, Container paramContainer, Rectangle paramRectangle)
/*      */   {
/* 1194 */     paintComponent(paramGraphics, paramComponent, paramContainer, paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height);
/*      */   }
/*      */ 
/*      */   private static CellRendererPane getCellRendererPane(Component paramComponent, Container paramContainer)
/*      */   {
/* 1204 */     Object localObject = paramComponent.getParent();
/* 1205 */     if ((localObject instanceof CellRendererPane)) {
/* 1206 */       if (((Container)localObject).getParent() != paramContainer)
/* 1207 */         paramContainer.add((Component)localObject);
/*      */     }
/*      */     else {
/* 1210 */       localObject = new CellRendererPane();
/* 1211 */       ((Container)localObject).add(paramComponent);
/* 1212 */       paramContainer.add((Component)localObject);
/*      */     }
/* 1214 */     return (CellRendererPane)localObject;
/*      */   }
/*      */ 
/*      */   public static void updateComponentTreeUI(Component paramComponent)
/*      */   {
/* 1223 */     updateComponentTreeUI0(paramComponent);
/* 1224 */     paramComponent.invalidate();
/* 1225 */     paramComponent.validate();
/* 1226 */     paramComponent.repaint();
/*      */   }
/*      */ 
/*      */   private static void updateComponentTreeUI0(Component paramComponent)
/*      */   {
/*      */     Object localObject2;
/* 1230 */     if ((paramComponent instanceof JComponent)) {
/* 1231 */       localObject1 = (JComponent)paramComponent;
/* 1232 */       ((JComponent)localObject1).updateUI();
/* 1233 */       localObject2 = ((JComponent)localObject1).getComponentPopupMenu();
/* 1234 */       if (localObject2 != null) {
/* 1235 */         updateComponentTreeUI((Component)localObject2);
/*      */       }
/*      */     }
/* 1238 */     Object localObject1 = null;
/* 1239 */     if ((paramComponent instanceof JMenu)) {
/* 1240 */       localObject1 = ((JMenu)paramComponent).getMenuComponents();
/*      */     }
/* 1242 */     else if ((paramComponent instanceof Container)) {
/* 1243 */       localObject1 = ((Container)paramComponent).getComponents();
/*      */     }
/* 1245 */     if (localObject1 != null)
/* 1246 */       for (Component localComponent : localObject1)
/* 1247 */         updateComponentTreeUI0(localComponent);
/*      */   }
/*      */ 
/*      */   public static void invokeLater(Runnable paramRunnable)
/*      */   {
/* 1290 */     EventQueue.invokeLater(paramRunnable);
/*      */   }
/*      */ 
/*      */   public static void invokeAndWait(Runnable paramRunnable)
/*      */     throws InterruptedException, InvocationTargetException
/*      */   {
/* 1349 */     EventQueue.invokeAndWait(paramRunnable);
/*      */   }
/*      */ 
/*      */   public static boolean isEventDispatchThread()
/*      */   {
/* 1362 */     return EventQueue.isDispatchThread();
/*      */   }
/*      */ 
/*      */   public static int getAccessibleIndexInParent(Component paramComponent)
/*      */   {
/* 1382 */     return paramComponent.getAccessibleContext().getAccessibleIndexInParent();
/*      */   }
/*      */ 
/*      */   public static Accessible getAccessibleAt(Component paramComponent, Point paramPoint)
/*      */   {
/* 1394 */     if ((paramComponent instanceof Container))
/* 1395 */       return paramComponent.getAccessibleContext().getAccessibleComponent().getAccessibleAt(paramPoint);
/* 1396 */     if ((paramComponent instanceof Accessible)) {
/* 1397 */       Accessible localAccessible = (Accessible)paramComponent;
/* 1398 */       if (localAccessible != null) {
/* 1399 */         AccessibleContext localAccessibleContext = localAccessible.getAccessibleContext();
/* 1400 */         if (localAccessibleContext != null)
/*      */         {
/* 1403 */           int i = localAccessibleContext.getAccessibleChildrenCount();
/* 1404 */           for (int j = 0; j < i; j++) {
/* 1405 */             localAccessible = localAccessibleContext.getAccessibleChild(j);
/* 1406 */             if (localAccessible != null) {
/* 1407 */               localAccessibleContext = localAccessible.getAccessibleContext();
/* 1408 */               if (localAccessibleContext != null) {
/* 1409 */                 AccessibleComponent localAccessibleComponent = localAccessibleContext.getAccessibleComponent();
/* 1410 */                 if ((localAccessibleComponent != null) && (localAccessibleComponent.isShowing())) {
/* 1411 */                   Point localPoint1 = localAccessibleComponent.getLocation();
/* 1412 */                   Point localPoint2 = new Point(paramPoint.x - localPoint1.x, paramPoint.y - localPoint1.y);
/*      */ 
/* 1414 */                   if (localAccessibleComponent.contains(localPoint2)) {
/* 1415 */                     return localAccessible;
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 1423 */       return (Accessible)paramComponent;
/*      */     }
/* 1425 */     return null;
/*      */   }
/*      */ 
/*      */   public static AccessibleStateSet getAccessibleStateSet(Component paramComponent)
/*      */   {
/* 1440 */     return paramComponent.getAccessibleContext().getAccessibleStateSet();
/*      */   }
/*      */ 
/*      */   public static int getAccessibleChildrenCount(Component paramComponent)
/*      */   {
/* 1455 */     return paramComponent.getAccessibleContext().getAccessibleChildrenCount();
/*      */   }
/*      */ 
/*      */   public static Accessible getAccessibleChild(Component paramComponent, int paramInt)
/*      */   {
/* 1469 */     return paramComponent.getAccessibleContext().getAccessibleChild(paramInt);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public static Component findFocusOwner(Component paramComponent)
/*      */   {
/* 1488 */     Component localComponent = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
/*      */ 
/* 1492 */     for (Object localObject = localComponent; localObject != null; 
/* 1493 */       localObject = (localObject instanceof Window) ? null : ((Component)localObject).getParent())
/*      */     {
/* 1495 */       if (localObject == paramComponent) {
/* 1496 */         return localComponent;
/*      */       }
/*      */     }
/*      */ 
/* 1500 */     return null;
/*      */   }
/*      */ 
/*      */   public static JRootPane getRootPane(Component paramComponent)
/*      */   {
/* 1509 */     if ((paramComponent instanceof RootPaneContainer)) {
/* 1510 */       return ((RootPaneContainer)paramComponent).getRootPane();
/*      */     }
/* 1512 */     for (; paramComponent != null; paramComponent = paramComponent.getParent()) {
/* 1513 */       if ((paramComponent instanceof JRootPane)) {
/* 1514 */         return (JRootPane)paramComponent;
/*      */       }
/*      */     }
/* 1517 */     return null;
/*      */   }
/*      */ 
/*      */   public static Component getRoot(Component paramComponent)
/*      */   {
/* 1526 */     Object localObject1 = null;
/* 1527 */     for (Object localObject2 = paramComponent; localObject2 != null; localObject2 = ((Component)localObject2).getParent()) {
/* 1528 */       if ((localObject2 instanceof Window)) {
/* 1529 */         return localObject2;
/*      */       }
/* 1531 */       if ((localObject2 instanceof Applet)) {
/* 1532 */         localObject1 = localObject2;
/*      */       }
/*      */     }
/* 1535 */     return localObject1;
/*      */   }
/*      */ 
/*      */   static JComponent getPaintingOrigin(JComponent paramJComponent) {
/* 1539 */     Object localObject = paramJComponent;
/* 1540 */     while (((localObject = ((Container)localObject).getParent()) instanceof JComponent)) {
/* 1541 */       JComponent localJComponent = (JComponent)localObject;
/* 1542 */       if (localJComponent.isPaintingOrigin()) {
/* 1543 */         return localJComponent;
/*      */       }
/*      */     }
/* 1546 */     return null;
/*      */   }
/*      */ 
/*      */   public static boolean processKeyBindings(KeyEvent paramKeyEvent)
/*      */   {
/* 1566 */     if (paramKeyEvent != null) {
/* 1567 */       if (paramKeyEvent.isConsumed()) {
/* 1568 */         return false;
/*      */       }
/*      */ 
/* 1571 */       Object localObject = paramKeyEvent.getComponent();
/* 1572 */       boolean bool = paramKeyEvent.getID() == 401;
/*      */ 
/* 1574 */       if (!isValidKeyEventForKeyBindings(paramKeyEvent)) {
/* 1575 */         return false;
/*      */       }
/*      */ 
/* 1579 */       while (localObject != null) {
/* 1580 */         if ((localObject instanceof JComponent)) {
/* 1581 */           return ((JComponent)localObject).processKeyBindings(paramKeyEvent, bool);
/*      */         }
/*      */ 
/* 1584 */         if (((localObject instanceof Applet)) || ((localObject instanceof Window)))
/*      */         {
/* 1588 */           return JComponent.processKeyBindingsForAllComponents(paramKeyEvent, (Container)localObject, bool);
/*      */         }
/*      */ 
/* 1591 */         localObject = ((Component)localObject).getParent();
/*      */       }
/*      */     }
/* 1594 */     return false;
/*      */   }
/*      */ 
/*      */   static boolean isValidKeyEventForKeyBindings(KeyEvent paramKeyEvent)
/*      */   {
/* 1602 */     return true;
/*      */   }
/*      */ 
/*      */   public static boolean notifyAction(Action paramAction, KeyStroke paramKeyStroke, KeyEvent paramKeyEvent, Object paramObject, int paramInt)
/*      */   {
/* 1625 */     if (paramAction == null) {
/* 1626 */       return false;
/*      */     }
/* 1628 */     if ((paramAction instanceof UIAction)) {
/* 1629 */       if (!((UIAction)paramAction).isEnabled(paramObject)) {
/* 1630 */         return false;
/*      */       }
/*      */     }
/* 1633 */     else if (!paramAction.isEnabled()) {
/* 1634 */       return false;
/*      */     }
/*      */ 
/* 1640 */     Object localObject = paramAction.getValue("ActionCommandKey");
/*      */     int i;
/* 1641 */     if ((localObject == null) && ((paramAction instanceof JComponent.ActionStandin)))
/*      */     {
/* 1644 */       i = 1;
/*      */     }
/*      */     else
/* 1647 */       i = 0;
/*      */     String str;
/* 1653 */     if (localObject != null) {
/* 1654 */       str = localObject.toString();
/*      */     }
/* 1656 */     else if ((i == 0) && (paramKeyEvent.getKeyChar() != 65535)) {
/* 1657 */       str = String.valueOf(paramKeyEvent.getKeyChar());
/*      */     }
/*      */     else
/*      */     {
/* 1662 */       str = null;
/*      */     }
/* 1664 */     paramAction.actionPerformed(new ActionEvent(paramObject, 1001, str, paramKeyEvent.getWhen(), paramInt));
/*      */ 
/* 1667 */     return true;
/*      */   }
/*      */ 
/*      */   public static void replaceUIInputMap(JComponent paramJComponent, int paramInt, InputMap paramInputMap)
/*      */   {
/* 1680 */     Object localObject = paramJComponent.getInputMap(paramInt, paramInputMap != null);
/*      */ 
/* 1682 */     while (localObject != null) {
/* 1683 */       InputMap localInputMap = ((InputMap)localObject).getParent();
/* 1684 */       if ((localInputMap == null) || ((localInputMap instanceof UIResource))) {
/* 1685 */         ((InputMap)localObject).setParent(paramInputMap);
/* 1686 */         return;
/*      */       }
/* 1688 */       localObject = localInputMap;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void replaceUIActionMap(JComponent paramJComponent, ActionMap paramActionMap)
/*      */   {
/* 1702 */     Object localObject = paramJComponent.getActionMap(paramActionMap != null);
/*      */ 
/* 1704 */     while (localObject != null) {
/* 1705 */       ActionMap localActionMap = ((ActionMap)localObject).getParent();
/* 1706 */       if ((localActionMap == null) || ((localActionMap instanceof UIResource))) {
/* 1707 */         ((ActionMap)localObject).setParent(paramActionMap);
/* 1708 */         return;
/*      */       }
/* 1710 */       localObject = localActionMap;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static InputMap getUIInputMap(JComponent paramJComponent, int paramInt)
/*      */   {
/* 1724 */     Object localObject = paramJComponent.getInputMap(paramInt, false);
/* 1725 */     while (localObject != null) {
/* 1726 */       InputMap localInputMap = ((InputMap)localObject).getParent();
/* 1727 */       if ((localInputMap instanceof UIResource)) {
/* 1728 */         return localInputMap;
/*      */       }
/* 1730 */       localObject = localInputMap;
/*      */     }
/* 1732 */     return null;
/*      */   }
/*      */ 
/*      */   public static ActionMap getUIActionMap(JComponent paramJComponent)
/*      */   {
/* 1743 */     Object localObject = paramJComponent.getActionMap(false);
/* 1744 */     while (localObject != null) {
/* 1745 */       ActionMap localActionMap = ((ActionMap)localObject).getParent();
/* 1746 */       if ((localActionMap instanceof UIResource)) {
/* 1747 */         return localActionMap;
/*      */       }
/* 1749 */       localObject = localActionMap;
/*      */     }
/* 1751 */     return null;
/*      */   }
/*      */ 
/*      */   static Frame getSharedOwnerFrame()
/*      */     throws HeadlessException
/*      */   {
/* 1831 */     Object localObject = (Frame)appContextGet(sharedOwnerFrameKey);
/*      */ 
/* 1833 */     if (localObject == null) {
/* 1834 */       localObject = new SharedOwnerFrame();
/* 1835 */       appContextPut(sharedOwnerFrameKey, localObject);
/*      */     }
/*      */ 
/* 1838 */     return localObject;
/*      */   }
/*      */ 
/*      */   static WindowListener getSharedOwnerFrameShutdownListener()
/*      */     throws HeadlessException
/*      */   {
/* 1849 */     Frame localFrame = getSharedOwnerFrame();
/* 1850 */     return (WindowListener)localFrame;
/*      */   }
/*      */ 
/*      */   static Object appContextGet(Object paramObject)
/*      */   {
/* 1861 */     return AppContext.getAppContext().get(paramObject);
/*      */   }
/*      */ 
/*      */   static void appContextPut(Object paramObject1, Object paramObject2) {
/* 1865 */     AppContext.getAppContext().put(paramObject1, paramObject2);
/*      */   }
/*      */ 
/*      */   static void appContextRemove(Object paramObject) {
/* 1869 */     AppContext.getAppContext().remove(paramObject);
/*      */   }
/*      */ 
/*      */   static Class<?> loadSystemClass(String paramString) throws ClassNotFoundException
/*      */   {
/* 1874 */     return Class.forName(paramString, true, Thread.currentThread().getContextClassLoader());
/*      */   }
/*      */ 
/*      */   static boolean isLeftToRight(Component paramComponent)
/*      */   {
/* 1884 */     return paramComponent.getComponentOrientation().isLeftToRight();
/*      */   }
/*      */   private SwingUtilities() {
/* 1887 */     throw new Error("SwingUtilities is just a container for static methods");
/*      */   }
/*      */ 
/*      */   static boolean doesIconReferenceImage(Icon paramIcon, Image paramImage)
/*      */   {
/* 1895 */     Object localObject = (paramIcon != null) && ((paramIcon instanceof ImageIcon)) ? ((ImageIcon)paramIcon).getImage() : null;
/*      */ 
/* 1897 */     return localObject == paramImage;
/*      */   }
/*      */ 
/*      */   static int findDisplayedMnemonicIndex(String paramString, int paramInt)
/*      */   {
/* 1910 */     if ((paramString == null) || (paramInt == 0)) {
/* 1911 */       return -1;
/*      */     }
/*      */ 
/* 1914 */     int i = Character.toUpperCase((char)paramInt);
/* 1915 */     int j = Character.toLowerCase((char)paramInt);
/*      */ 
/* 1917 */     int k = paramString.indexOf(i);
/* 1918 */     int m = paramString.indexOf(j);
/*      */ 
/* 1920 */     if (k == -1)
/* 1921 */       return m;
/* 1922 */     if (m == -1) {
/* 1923 */       return k;
/*      */     }
/* 1925 */     return m < k ? m : k;
/*      */   }
/*      */ 
/*      */   public static Rectangle calculateInnerArea(JComponent paramJComponent, Rectangle paramRectangle)
/*      */   {
/* 1948 */     if (paramJComponent == null) {
/* 1949 */       return null;
/*      */     }
/* 1951 */     Rectangle localRectangle = paramRectangle;
/* 1952 */     Insets localInsets = paramJComponent.getInsets();
/*      */ 
/* 1954 */     if (localRectangle == null) {
/* 1955 */       localRectangle = new Rectangle();
/*      */     }
/*      */ 
/* 1958 */     localRectangle.x = localInsets.left;
/* 1959 */     localRectangle.y = localInsets.top;
/* 1960 */     localRectangle.width = (paramJComponent.getWidth() - localInsets.left - localInsets.right);
/* 1961 */     localRectangle.height = (paramJComponent.getHeight() - localInsets.top - localInsets.bottom);
/*      */ 
/* 1963 */     return localRectangle;
/*      */   }
/*      */ 
/*      */   static void updateRendererOrEditorUI(Object paramObject) {
/* 1967 */     if (paramObject == null) {
/* 1968 */       return;
/*      */     }
/*      */ 
/* 1971 */     Component localComponent = null;
/*      */ 
/* 1973 */     if ((paramObject instanceof Component)) {
/* 1974 */       localComponent = (Component)paramObject;
/*      */     }
/* 1976 */     if ((paramObject instanceof DefaultCellEditor)) {
/* 1977 */       localComponent = ((DefaultCellEditor)paramObject).getComponent();
/*      */     }
/*      */ 
/* 1980 */     if (localComponent != null)
/* 1981 */       updateComponentTreeUI(localComponent);
/*      */   }
/*      */ 
/*      */   public static Container getUnwrappedParent(Component paramComponent)
/*      */   {
/* 2002 */     Container localContainer = paramComponent.getParent();
/* 2003 */     while ((localContainer instanceof JLayer)) {
/* 2004 */       localContainer = localContainer.getParent();
/*      */     }
/* 2006 */     return localContainer;
/*      */   }
/*      */ 
/*      */   public static Component getUnwrappedView(JViewport paramJViewport)
/*      */   {
/* 2033 */     Component localComponent = paramJViewport.getView();
/* 2034 */     while ((localComponent instanceof JLayer)) {
/* 2035 */       localComponent = ((JLayer)localComponent).getView();
/*      */     }
/* 2037 */     return localComponent;
/*      */   }
/*      */ 
/*      */   static Container getValidateRoot(Container paramContainer, boolean paramBoolean)
/*      */   {
/* 2061 */     Container localContainer = null;
/*      */ 
/* 2063 */     for (; paramContainer != null; paramContainer = paramContainer.getParent())
/*      */     {
/* 2065 */       if ((!paramContainer.isDisplayable()) || ((paramContainer instanceof CellRendererPane))) {
/* 2066 */         return null;
/*      */       }
/* 2068 */       if (paramContainer.isValidateRoot()) {
/* 2069 */         localContainer = paramContainer;
/* 2070 */         break;
/*      */       }
/*      */     }
/*      */ 
/* 2074 */     if (localContainer == null) {
/* 2075 */       return null;
/*      */     }
/*      */ 
/* 2078 */     for (; paramContainer != null; paramContainer = paramContainer.getParent()) {
/* 2079 */       if ((!paramContainer.isDisplayable()) || ((paramBoolean) && (!paramContainer.isVisible()))) {
/* 2080 */         return null;
/*      */       }
/* 2082 */       if (((paramContainer instanceof Window)) || ((paramContainer instanceof Applet))) {
/* 2083 */         return localContainer;
/*      */       }
/*      */     }
/*      */ 
/* 2087 */     return null;
/*      */   }
/*      */ 
/*      */   static class SharedOwnerFrame extends Frame
/*      */     implements WindowListener
/*      */   {
/*      */     public void addNotify()
/*      */     {
/* 1761 */       super.addNotify();
/* 1762 */       installListeners();
/*      */     }
/*      */ 
/*      */     void installListeners()
/*      */     {
/* 1769 */       Window[] arrayOfWindow1 = getOwnedWindows();
/* 1770 */       for (Window localWindow : arrayOfWindow1)
/* 1771 */         if (localWindow != null) {
/* 1772 */           localWindow.removeWindowListener(this);
/* 1773 */           localWindow.addWindowListener(this);
/*      */         }
/*      */     }
/*      */ 
/*      */     public void windowClosed(WindowEvent paramWindowEvent)
/*      */     {
/* 1783 */       synchronized (getTreeLock()) {
/* 1784 */         Window[] arrayOfWindow1 = getOwnedWindows();
/* 1785 */         for (Window localWindow : arrayOfWindow1) {
/* 1786 */           if (localWindow != null) {
/* 1787 */             if (localWindow.isDisplayable()) {
/* 1788 */               return;
/*      */             }
/* 1790 */             localWindow.removeWindowListener(this);
/*      */           }
/*      */         }
/* 1793 */         dispose();
/*      */       }
/*      */     }
/*      */     public void windowOpened(WindowEvent paramWindowEvent) {
/*      */     }
/*      */     public void windowClosing(WindowEvent paramWindowEvent) {
/*      */     }
/*      */     public void windowIconified(WindowEvent paramWindowEvent) {
/*      */     }
/*      */     public void windowDeiconified(WindowEvent paramWindowEvent) {
/*      */     }
/*      */     public void windowActivated(WindowEvent paramWindowEvent) {
/*      */     }
/*      */ 
/*      */     public void windowDeactivated(WindowEvent paramWindowEvent) {
/*      */     }
/*      */ 
/*      */     public void show() {
/*      */     }
/*      */ 
/*      */     public void dispose() {
/*      */       try { getToolkit().getSystemEventQueue();
/* 1815 */         super.dispose();
/*      */       }
/*      */       catch (Exception localException)
/*      */       {
/*      */       }
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.SwingUtilities
 * JD-Core Version:    0.6.2
 */