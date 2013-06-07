/*     */ package javax.swing;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Vector;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;
/*     */ import javax.swing.event.EventListenerList;
/*     */ import sun.awt.AppContext;
/*     */ 
/*     */ public class MenuSelectionManager
/*     */ {
/*  40 */   private Vector<MenuElement> selection = new Vector();
/*     */   private static final boolean TRACE = false;
/*     */   private static final boolean VERBOSE = false;
/*     */   private static final boolean DEBUG = false;
/*  47 */   private static final StringBuilder MENU_SELECTION_MANAGER_KEY = new StringBuilder("javax.swing.MenuSelectionManager");
/*     */ 
/*  74 */   protected transient ChangeEvent changeEvent = null;
/*  75 */   protected EventListenerList listenerList = new EventListenerList();
/*     */ 
/*     */   public static MenuSelectionManager defaultManager()
/*     */   {
/*  56 */     synchronized (MENU_SELECTION_MANAGER_KEY) {
/*  57 */       AppContext localAppContext = AppContext.getAppContext();
/*  58 */       MenuSelectionManager localMenuSelectionManager = (MenuSelectionManager)localAppContext.get(MENU_SELECTION_MANAGER_KEY);
/*     */ 
/*  60 */       if (localMenuSelectionManager == null) {
/*  61 */         localMenuSelectionManager = new MenuSelectionManager();
/*  62 */         localAppContext.put(MENU_SELECTION_MANAGER_KEY, localMenuSelectionManager);
/*     */       }
/*     */ 
/*  65 */       return localMenuSelectionManager;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setSelectedPath(MenuElement[] paramArrayOfMenuElement)
/*     */   {
/*  90 */     int k = this.selection.size();
/*  91 */     int m = 0;
/*     */ 
/*  93 */     if (paramArrayOfMenuElement == null) {
/*  94 */       paramArrayOfMenuElement = new MenuElement[0];
/*     */     }
/*     */ 
/* 102 */     int i = 0; for (int j = paramArrayOfMenuElement.length; (i < j) && 
/* 103 */       (i < k) && (this.selection.elementAt(i) == paramArrayOfMenuElement[i]); i++)
/*     */     {
/* 104 */       m++;
/*     */     }
/*     */ 
/* 109 */     for (i = k - 1; i >= m; i--) {
/* 110 */       MenuElement localMenuElement = (MenuElement)this.selection.elementAt(i);
/* 111 */       this.selection.removeElementAt(i);
/* 112 */       localMenuElement.menuSelectionChanged(false);
/*     */     }
/*     */ 
/* 115 */     i = m; for (j = paramArrayOfMenuElement.length; i < j; i++) {
/* 116 */       if (paramArrayOfMenuElement[i] != null) {
/* 117 */         this.selection.addElement(paramArrayOfMenuElement[i]);
/* 118 */         paramArrayOfMenuElement[i].menuSelectionChanged(true);
/*     */       }
/*     */     }
/*     */ 
/* 122 */     fireStateChanged();
/*     */   }
/*     */ 
/*     */   public MenuElement[] getSelectedPath()
/*     */   {
/* 131 */     MenuElement[] arrayOfMenuElement = new MenuElement[this.selection.size()];
/*     */ 
/* 133 */     int i = 0; for (int j = this.selection.size(); i < j; i++)
/* 134 */       arrayOfMenuElement[i] = ((MenuElement)this.selection.elementAt(i));
/* 135 */     return arrayOfMenuElement;
/*     */   }
/*     */ 
/*     */   public void clearSelectedPath()
/*     */   {
/* 143 */     if (this.selection.size() > 0)
/* 144 */       setSelectedPath(null);
/*     */   }
/*     */ 
/*     */   public void addChangeListener(ChangeListener paramChangeListener)
/*     */   {
/* 154 */     this.listenerList.add(ChangeListener.class, paramChangeListener);
/*     */   }
/*     */ 
/*     */   public void removeChangeListener(ChangeListener paramChangeListener)
/*     */   {
/* 163 */     this.listenerList.remove(ChangeListener.class, paramChangeListener);
/*     */   }
/*     */ 
/*     */   public ChangeListener[] getChangeListeners()
/*     */   {
/* 175 */     return (ChangeListener[])this.listenerList.getListeners(ChangeListener.class);
/*     */   }
/*     */ 
/*     */   protected void fireStateChanged()
/*     */   {
/* 187 */     Object[] arrayOfObject = this.listenerList.getListenerList();
/*     */ 
/* 190 */     for (int i = arrayOfObject.length - 2; i >= 0; i -= 2)
/* 191 */       if (arrayOfObject[i] == ChangeListener.class)
/*     */       {
/* 193 */         if (this.changeEvent == null)
/* 194 */           this.changeEvent = new ChangeEvent(this);
/* 195 */         ((ChangeListener)arrayOfObject[(i + 1)]).stateChanged(this.changeEvent);
/*     */       }
/*     */   }
/*     */ 
/*     */   public void processMouseEvent(MouseEvent paramMouseEvent)
/*     */   {
/* 218 */     Point localPoint = paramMouseEvent.getPoint();
/*     */ 
/* 220 */     Component localComponent2 = paramMouseEvent.getComponent();
/*     */ 
/* 222 */     if ((localComponent2 != null) && (!localComponent2.isShowing()))
/*     */     {
/* 225 */       return;
/*     */     }
/*     */ 
/* 228 */     int i4 = paramMouseEvent.getID();
/* 229 */     int i5 = paramMouseEvent.getModifiers();
/*     */ 
/* 231 */     if (((i4 == 504) || (i4 == 505)) && ((i5 & 0x1C) != 0))
/*     */     {
/* 235 */       return;
/*     */     }
/*     */ 
/* 238 */     if (localComponent2 != null) {
/* 239 */       SwingUtilities.convertPointToScreen(localPoint, localComponent2);
/*     */     }
/*     */ 
/* 242 */     int i = localPoint.x;
/* 243 */     int j = localPoint.y;
/*     */ 
/* 245 */     Vector localVector = (Vector)this.selection.clone();
/* 246 */     int i3 = localVector.size();
/* 247 */     int i6 = 0;
/* 248 */     for (int k = i3 - 1; (k >= 0) && (i6 == 0); k--) {
/* 249 */       MenuElement localMenuElement = (MenuElement)localVector.elementAt(k);
/* 250 */       MenuElement[] arrayOfMenuElement1 = localMenuElement.getSubElements();
/*     */ 
/* 252 */       MenuElement[] arrayOfMenuElement2 = null;
/* 253 */       int m = 0; for (int n = arrayOfMenuElement1.length; (m < n) && (i6 == 0); m++)
/* 254 */         if (arrayOfMenuElement1[m] != null)
/*     */         {
/* 256 */           Component localComponent1 = arrayOfMenuElement1[m].getComponent();
/* 257 */           if (localComponent1.isShowing())
/*     */           {
/*     */             int i1;
/*     */             int i2;
/* 259 */             if ((localComponent1 instanceof JComponent)) {
/* 260 */               i1 = localComponent1.getWidth();
/* 261 */               i2 = localComponent1.getHeight();
/*     */             } else {
/* 263 */               Rectangle localRectangle = localComponent1.getBounds();
/* 264 */               i1 = localRectangle.width;
/* 265 */               i2 = localRectangle.height;
/*     */             }
/* 267 */             localPoint.x = i;
/* 268 */             localPoint.y = j;
/* 269 */             SwingUtilities.convertPointFromScreen(localPoint, localComponent1);
/*     */ 
/* 274 */             if ((localPoint.x >= 0) && (localPoint.x < i1) && (localPoint.y >= 0) && (localPoint.y < i2))
/*     */             {
/* 277 */               if (arrayOfMenuElement2 == null) {
/* 278 */                 arrayOfMenuElement2 = new MenuElement[k + 2];
/* 279 */                 for (int i7 = 0; i7 <= k; i7++)
/* 280 */                   arrayOfMenuElement2[i7] = ((MenuElement)localVector.elementAt(i7));
/*     */               }
/* 282 */               arrayOfMenuElement2[(k + 1)] = arrayOfMenuElement1[m];
/* 283 */               MenuElement[] arrayOfMenuElement3 = getSelectedPath();
/*     */ 
/* 286 */               if ((arrayOfMenuElement3[(arrayOfMenuElement3.length - 1)] != arrayOfMenuElement2[(k + 1)]) && ((arrayOfMenuElement3.length < 2) || (arrayOfMenuElement3[(arrayOfMenuElement3.length - 2)] != arrayOfMenuElement2[(k + 1)])))
/*     */               {
/* 291 */                 localObject = arrayOfMenuElement3[(arrayOfMenuElement3.length - 1)].getComponent();
/*     */ 
/* 293 */                 MouseEvent localMouseEvent1 = new MouseEvent((Component)localObject, 505, paramMouseEvent.getWhen(), paramMouseEvent.getModifiers(), localPoint.x, localPoint.y, paramMouseEvent.getXOnScreen(), paramMouseEvent.getYOnScreen(), paramMouseEvent.getClickCount(), paramMouseEvent.isPopupTrigger(), 0);
/*     */ 
/* 301 */                 arrayOfMenuElement3[(arrayOfMenuElement3.length - 1)].processMouseEvent(localMouseEvent1, arrayOfMenuElement2, this);
/*     */ 
/* 304 */                 MouseEvent localMouseEvent2 = new MouseEvent(localComponent1, 504, paramMouseEvent.getWhen(), paramMouseEvent.getModifiers(), localPoint.x, localPoint.y, paramMouseEvent.getXOnScreen(), paramMouseEvent.getYOnScreen(), paramMouseEvent.getClickCount(), paramMouseEvent.isPopupTrigger(), 0);
/*     */ 
/* 313 */                 arrayOfMenuElement1[m].processMouseEvent(localMouseEvent2, arrayOfMenuElement2, this);
/*     */               }
/* 315 */               Object localObject = new MouseEvent(localComponent1, paramMouseEvent.getID(), paramMouseEvent.getWhen(), paramMouseEvent.getModifiers(), localPoint.x, localPoint.y, paramMouseEvent.getXOnScreen(), paramMouseEvent.getYOnScreen(), paramMouseEvent.getClickCount(), paramMouseEvent.isPopupTrigger(), 0);
/*     */ 
/* 322 */               arrayOfMenuElement1[m].processMouseEvent((MouseEvent)localObject, arrayOfMenuElement2, this);
/* 323 */               i6 = 1;
/* 324 */               paramMouseEvent.consume();
/*     */             }
/*     */           }
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/* 331 */   private void printMenuElementArray(MenuElement[] paramArrayOfMenuElement) { printMenuElementArray(paramArrayOfMenuElement, false); }
/*     */ 
/*     */   private void printMenuElementArray(MenuElement[] paramArrayOfMenuElement, boolean paramBoolean)
/*     */   {
/* 335 */     System.out.println("Path is(");
/*     */ 
/* 337 */     int i = 0; for (int j = paramArrayOfMenuElement.length; i < j; i++) {
/* 338 */       for (int k = 0; k <= i; k++)
/* 339 */         System.out.print("  ");
/* 340 */       MenuElement localMenuElement = paramArrayOfMenuElement[i];
/* 341 */       if ((localMenuElement instanceof JMenuItem))
/* 342 */         System.out.println(((JMenuItem)localMenuElement).getText() + ", ");
/* 343 */       else if ((localMenuElement instanceof JMenuBar))
/* 344 */         System.out.println("JMenuBar, ");
/* 345 */       else if ((localMenuElement instanceof JPopupMenu))
/* 346 */         System.out.println("JPopupMenu, ");
/* 347 */       else if (localMenuElement == null)
/* 348 */         System.out.println("NULL , ");
/*     */       else {
/* 350 */         System.out.println("" + localMenuElement + ", ");
/*     */       }
/*     */     }
/* 353 */     System.out.println(")");
/*     */ 
/* 355 */     if (paramBoolean == true)
/* 356 */       Thread.dumpStack();
/*     */   }
/*     */ 
/*     */   public Component componentForPoint(Component paramComponent, Point paramPoint)
/*     */   {
/* 373 */     Point localPoint = paramPoint;
/*     */ 
/* 383 */     SwingUtilities.convertPointToScreen(localPoint, paramComponent);
/*     */ 
/* 385 */     int i = localPoint.x;
/* 386 */     int j = localPoint.y;
/*     */ 
/* 388 */     Vector localVector = (Vector)this.selection.clone();
/* 389 */     int i3 = localVector.size();
/* 390 */     for (int k = i3 - 1; k >= 0; k--) {
/* 391 */       MenuElement localMenuElement = (MenuElement)localVector.elementAt(k);
/* 392 */       MenuElement[] arrayOfMenuElement = localMenuElement.getSubElements();
/*     */ 
/* 394 */       int m = 0; for (int n = arrayOfMenuElement.length; m < n; m++)
/* 395 */         if (arrayOfMenuElement[m] != null)
/*     */         {
/* 397 */           Component localComponent = arrayOfMenuElement[m].getComponent();
/* 398 */           if (localComponent.isShowing())
/*     */           {
/*     */             int i1;
/*     */             int i2;
/* 400 */             if ((localComponent instanceof JComponent)) {
/* 401 */               i1 = localComponent.getWidth();
/* 402 */               i2 = localComponent.getHeight();
/*     */             } else {
/* 404 */               Rectangle localRectangle = localComponent.getBounds();
/* 405 */               i1 = localRectangle.width;
/* 406 */               i2 = localRectangle.height;
/*     */             }
/* 408 */             localPoint.x = i;
/* 409 */             localPoint.y = j;
/* 410 */             SwingUtilities.convertPointFromScreen(localPoint, localComponent);
/*     */ 
/* 415 */             if ((localPoint.x >= 0) && (localPoint.x < i1) && (localPoint.y >= 0) && (localPoint.y < i2))
/* 416 */               return localComponent;
/*     */           }
/*     */         }
/*     */     }
/* 420 */     return null;
/*     */   }
/*     */ 
/*     */   public void processKeyEvent(KeyEvent paramKeyEvent)
/*     */   {
/* 430 */     MenuElement[] arrayOfMenuElement1 = new MenuElement[0];
/* 431 */     arrayOfMenuElement1 = (MenuElement[])this.selection.toArray(arrayOfMenuElement1);
/* 432 */     int i = arrayOfMenuElement1.length;
/*     */ 
/* 435 */     if (i < 1) {
/* 436 */       return;
/*     */     }
/*     */ 
/* 439 */     for (int j = i - 1; j >= 0; j--) {
/* 440 */       MenuElement localMenuElement = arrayOfMenuElement1[j];
/* 441 */       MenuElement[] arrayOfMenuElement3 = localMenuElement.getSubElements();
/* 442 */       arrayOfMenuElement2 = null;
/*     */ 
/* 444 */       for (int k = 0; k < arrayOfMenuElement3.length; k++) {
/* 445 */         if ((arrayOfMenuElement3[k] != null) && (arrayOfMenuElement3[k].getComponent().isShowing()) && (arrayOfMenuElement3[k].getComponent().isEnabled()))
/*     */         {
/* 450 */           if (arrayOfMenuElement2 == null) {
/* 451 */             arrayOfMenuElement2 = new MenuElement[j + 2];
/* 452 */             System.arraycopy(arrayOfMenuElement1, 0, arrayOfMenuElement2, 0, j + 1);
/*     */           }
/* 454 */           arrayOfMenuElement2[(j + 1)] = arrayOfMenuElement3[k];
/* 455 */           arrayOfMenuElement3[k].processKeyEvent(paramKeyEvent, arrayOfMenuElement2, this);
/* 456 */           if (paramKeyEvent.isConsumed()) {
/* 457 */             return;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 463 */     MenuElement[] arrayOfMenuElement2 = new MenuElement[1];
/* 464 */     arrayOfMenuElement2[0] = arrayOfMenuElement1[0];
/* 465 */     arrayOfMenuElement2[0].processKeyEvent(paramKeyEvent, arrayOfMenuElement2, this);
/* 466 */     if (paramKeyEvent.isConsumed());
/*     */   }
/*     */ 
/*     */   public boolean isComponentPartOfCurrentMenu(Component paramComponent)
/*     */   {
/* 475 */     if (this.selection.size() > 0) {
/* 476 */       MenuElement localMenuElement = (MenuElement)this.selection.elementAt(0);
/* 477 */       return isComponentPartOfCurrentMenu(localMenuElement, paramComponent);
/*     */     }
/* 479 */     return false;
/*     */   }
/*     */ 
/*     */   private boolean isComponentPartOfCurrentMenu(MenuElement paramMenuElement, Component paramComponent)
/*     */   {
/* 486 */     if (paramMenuElement == null) {
/* 487 */       return false;
/*     */     }
/* 489 */     if (paramMenuElement.getComponent() == paramComponent) {
/* 490 */       return true;
/*     */     }
/* 492 */     MenuElement[] arrayOfMenuElement = paramMenuElement.getSubElements();
/* 493 */     int i = 0; for (int j = arrayOfMenuElement.length; i < j; i++) {
/* 494 */       if (isComponentPartOfCurrentMenu(arrayOfMenuElement[i], paramComponent)) {
/* 495 */         return true;
/*     */       }
/*     */     }
/* 498 */     return false;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.MenuSelectionManager
 * JD-Core Version:    0.6.2
 */