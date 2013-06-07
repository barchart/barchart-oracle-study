/*      */ package javax.swing.text;
/*      */ 
/*      */ import java.awt.Component;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.HeadlessException;
/*      */ import java.awt.Point;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.Toolkit;
/*      */ import java.awt.datatransfer.Clipboard;
/*      */ import java.awt.datatransfer.ClipboardOwner;
/*      */ import java.awt.datatransfer.StringSelection;
/*      */ import java.awt.datatransfer.Transferable;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ import java.awt.event.FocusEvent;
/*      */ import java.awt.event.FocusListener;
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.awt.event.MouseListener;
/*      */ import java.awt.event.MouseMotionListener;
/*      */ import java.beans.PropertyChangeEvent;
/*      */ import java.beans.PropertyChangeListener;
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.util.EventListener;
/*      */ import javax.swing.Action;
/*      */ import javax.swing.ActionMap;
/*      */ import javax.swing.JComponent;
/*      */ import javax.swing.JPasswordField;
/*      */ import javax.swing.LookAndFeel;
/*      */ import javax.swing.SwingUtilities;
/*      */ import javax.swing.Timer;
/*      */ import javax.swing.TransferHandler;
/*      */ import javax.swing.UIManager;
/*      */ import javax.swing.event.ChangeEvent;
/*      */ import javax.swing.event.ChangeListener;
/*      */ import javax.swing.event.DocumentEvent;
/*      */ import javax.swing.event.DocumentListener;
/*      */ import javax.swing.event.EventListenerList;
/*      */ import javax.swing.plaf.TextUI;
/*      */ import sun.swing.SwingUtilities2;
/*      */ 
/*      */ public class DefaultCaret extends Rectangle
/*      */   implements Caret, FocusListener, MouseListener, MouseMotionListener
/*      */ {
/*      */   public static final int UPDATE_WHEN_ON_EDT = 0;
/*      */   public static final int NEVER_UPDATE = 1;
/*      */   public static final int ALWAYS_UPDATE = 2;
/* 1544 */   protected EventListenerList listenerList = new EventListenerList();
/*      */ 
/* 1552 */   protected transient ChangeEvent changeEvent = null;
/*      */   JTextComponent component;
/* 1558 */   int updatePolicy = 0;
/*      */   boolean visible;
/*      */   boolean active;
/*      */   int dot;
/*      */   int mark;
/*      */   Object selectionTag;
/*      */   boolean selectionVisible;
/*      */   Timer flasher;
/*      */   Point magicCaretPosition;
/*      */   transient Position.Bias dotBias;
/*      */   transient Position.Bias markBias;
/*      */   boolean dotLTR;
/*      */   boolean markLTR;
/* 1571 */   transient Handler handler = new Handler();
/* 1572 */   private transient int[] flagXPoints = new int[3];
/* 1573 */   private transient int[] flagYPoints = new int[3];
/*      */   private transient NavigationFilter.FilterBypass filterBypass;
/* 1575 */   private static transient Action selectWord = null;
/* 1576 */   private static transient Action selectLine = null;
/*      */   private boolean ownsSelection;
/*      */   private boolean forceCaretPositionChange;
/*      */   private transient boolean shouldHandleRelease;
/* 1603 */   private transient MouseEvent selectedWordEvent = null;
/*      */ 
/* 1608 */   private int caretWidth = -1;
/* 1609 */   private float aspectRatio = -1.0F;
/*      */ 
/*      */   public void setUpdatePolicy(int paramInt)
/*      */   {
/*  203 */     this.updatePolicy = paramInt;
/*      */   }
/*      */ 
/*      */   public int getUpdatePolicy()
/*      */   {
/*  220 */     return this.updatePolicy;
/*      */   }
/*      */ 
/*      */   protected final JTextComponent getComponent()
/*      */   {
/*  230 */     return this.component;
/*      */   }
/*      */ 
/*      */   protected final synchronized void repaint()
/*      */   {
/*  244 */     if (this.component != null)
/*  245 */       this.component.repaint(this.x, this.y, this.width, this.height);
/*      */   }
/*      */ 
/*      */   protected synchronized void damage(Rectangle paramRectangle)
/*      */   {
/*  260 */     if (paramRectangle != null) {
/*  261 */       int i = getCaretWidth(paramRectangle.height);
/*  262 */       this.x = (paramRectangle.x - 4 - (i >> 1));
/*  263 */       this.y = paramRectangle.y;
/*  264 */       this.width = (9 + i);
/*  265 */       this.height = paramRectangle.height;
/*  266 */       repaint();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void adjustVisibility(Rectangle paramRectangle)
/*      */   {
/*  281 */     if (this.component == null) {
/*  282 */       return;
/*      */     }
/*  284 */     if (SwingUtilities.isEventDispatchThread())
/*  285 */       this.component.scrollRectToVisible(paramRectangle);
/*      */     else
/*  287 */       SwingUtilities.invokeLater(new SafeScroller(paramRectangle));
/*      */   }
/*      */ 
/*      */   protected Highlighter.HighlightPainter getSelectionPainter()
/*      */   {
/*  297 */     return DefaultHighlighter.DefaultPainter;
/*      */   }
/*      */ 
/*      */   protected void positionCaret(MouseEvent paramMouseEvent)
/*      */   {
/*  307 */     Point localPoint = new Point(paramMouseEvent.getX(), paramMouseEvent.getY());
/*  308 */     Position.Bias[] arrayOfBias = new Position.Bias[1];
/*  309 */     int i = this.component.getUI().viewToModel(this.component, localPoint, arrayOfBias);
/*  310 */     if (arrayOfBias[0] == null)
/*  311 */       arrayOfBias[0] = Position.Bias.Forward;
/*  312 */     if (i >= 0)
/*  313 */       setDot(i, arrayOfBias[0]);
/*      */   }
/*      */ 
/*      */   protected void moveCaret(MouseEvent paramMouseEvent)
/*      */   {
/*  326 */     Point localPoint = new Point(paramMouseEvent.getX(), paramMouseEvent.getY());
/*  327 */     Position.Bias[] arrayOfBias = new Position.Bias[1];
/*  328 */     int i = this.component.getUI().viewToModel(this.component, localPoint, arrayOfBias);
/*  329 */     if (arrayOfBias[0] == null)
/*  330 */       arrayOfBias[0] = Position.Bias.Forward;
/*  331 */     if (i >= 0)
/*  332 */       moveDot(i, arrayOfBias[0]);
/*      */   }
/*      */ 
/*      */   public void focusGained(FocusEvent paramFocusEvent)
/*      */   {
/*  347 */     if (this.component.isEnabled()) {
/*  348 */       if (this.component.isEditable()) {
/*  349 */         setVisible(true);
/*      */       }
/*  351 */       setSelectionVisible(true);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void focusLost(FocusEvent paramFocusEvent)
/*      */   {
/*  364 */     setVisible(false);
/*  365 */     setSelectionVisible((this.ownsSelection) || (paramFocusEvent.isTemporary()));
/*      */   }
/*      */ 
/*      */   private void selectWord(MouseEvent paramMouseEvent)
/*      */   {
/*  373 */     if ((this.selectedWordEvent != null) && (this.selectedWordEvent.getX() == paramMouseEvent.getX()) && (this.selectedWordEvent.getY() == paramMouseEvent.getY()))
/*      */     {
/*  377 */       return;
/*      */     }
/*  379 */     Action localAction = null;
/*  380 */     ActionMap localActionMap = getComponent().getActionMap();
/*  381 */     if (localActionMap != null) {
/*  382 */       localAction = localActionMap.get("select-word");
/*      */     }
/*  384 */     if (localAction == null) {
/*  385 */       if (selectWord == null) {
/*  386 */         selectWord = new DefaultEditorKit.SelectWordAction();
/*      */       }
/*  388 */       localAction = selectWord;
/*      */     }
/*  390 */     localAction.actionPerformed(new ActionEvent(getComponent(), 1001, null, paramMouseEvent.getWhen(), paramMouseEvent.getModifiers()));
/*      */ 
/*  392 */     this.selectedWordEvent = paramMouseEvent;
/*      */   }
/*      */ 
/*      */   public void mouseClicked(MouseEvent paramMouseEvent)
/*      */   {
/*  406 */     int i = SwingUtilities2.getAdjustedClickCount(getComponent(), paramMouseEvent);
/*      */ 
/*  408 */     if (!paramMouseEvent.isConsumed())
/*      */     {
/*      */       Object localObject1;
/*      */       Object localObject2;
/*  409 */       if (SwingUtilities.isLeftMouseButton(paramMouseEvent))
/*      */       {
/*  411 */         if (i == 1) {
/*  412 */           this.selectedWordEvent = null;
/*  413 */         } else if ((i == 2) && (SwingUtilities2.canEventAccessSystemClipboard(paramMouseEvent)))
/*      */         {
/*  415 */           selectWord(paramMouseEvent);
/*  416 */           this.selectedWordEvent = null;
/*  417 */         } else if ((i == 3) && (SwingUtilities2.canEventAccessSystemClipboard(paramMouseEvent)))
/*      */         {
/*  419 */           localObject1 = null;
/*  420 */           localObject2 = getComponent().getActionMap();
/*  421 */           if (localObject2 != null) {
/*  422 */             localObject1 = ((ActionMap)localObject2).get("select-line");
/*      */           }
/*  424 */           if (localObject1 == null) {
/*  425 */             if (selectLine == null) {
/*  426 */               selectLine = new DefaultEditorKit.SelectLineAction();
/*      */             }
/*  428 */             localObject1 = selectLine;
/*      */           }
/*  430 */           ((Action)localObject1).actionPerformed(new ActionEvent(getComponent(), 1001, null, paramMouseEvent.getWhen(), paramMouseEvent.getModifiers()));
/*      */         }
/*      */       }
/*  433 */       else if (SwingUtilities.isMiddleMouseButton(paramMouseEvent))
/*      */       {
/*  435 */         if ((i == 1) && (this.component.isEditable()) && (this.component.isEnabled()) && (SwingUtilities2.canEventAccessSystemClipboard(paramMouseEvent)))
/*      */         {
/*  438 */           localObject1 = (JTextComponent)paramMouseEvent.getSource();
/*  439 */           if (localObject1 != null)
/*      */             try {
/*  441 */               localObject2 = ((JTextComponent)localObject1).getToolkit();
/*  442 */               Clipboard localClipboard = ((Toolkit)localObject2).getSystemSelection();
/*  443 */               if (localClipboard != null)
/*      */               {
/*  445 */                 adjustCaret(paramMouseEvent);
/*  446 */                 TransferHandler localTransferHandler = ((JTextComponent)localObject1).getTransferHandler();
/*  447 */                 if (localTransferHandler != null) {
/*  448 */                   Transferable localTransferable = null;
/*      */                   try
/*      */                   {
/*  451 */                     localTransferable = localClipboard.getContents(null);
/*      */                   }
/*      */                   catch (IllegalStateException localIllegalStateException) {
/*  454 */                     UIManager.getLookAndFeel().provideErrorFeedback((Component)localObject1);
/*      */                   }
/*      */ 
/*  457 */                   if (localTransferable != null) {
/*  458 */                     localTransferHandler.importData((JComponent)localObject1, localTransferable);
/*      */                   }
/*      */                 }
/*  461 */                 adjustFocus(true);
/*      */               }
/*      */             }
/*      */             catch (HeadlessException localHeadlessException)
/*      */             {
/*      */             }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void mousePressed(MouseEvent paramMouseEvent)
/*      */   {
/*  485 */     int i = SwingUtilities2.getAdjustedClickCount(getComponent(), paramMouseEvent);
/*      */ 
/*  487 */     if (SwingUtilities.isLeftMouseButton(paramMouseEvent))
/*  488 */       if (paramMouseEvent.isConsumed()) {
/*  489 */         this.shouldHandleRelease = true;
/*      */       } else {
/*  491 */         this.shouldHandleRelease = false;
/*  492 */         adjustCaretAndFocus(paramMouseEvent);
/*  493 */         if ((i == 2) && (SwingUtilities2.canEventAccessSystemClipboard(paramMouseEvent)))
/*      */         {
/*  495 */           selectWord(paramMouseEvent);
/*      */         }
/*      */       }
/*      */   }
/*      */ 
/*      */   void adjustCaretAndFocus(MouseEvent paramMouseEvent)
/*      */   {
/*  502 */     adjustCaret(paramMouseEvent);
/*  503 */     adjustFocus(false);
/*      */   }
/*      */ 
/*      */   private void adjustCaret(MouseEvent paramMouseEvent)
/*      */   {
/*  510 */     if (((paramMouseEvent.getModifiers() & 0x1) != 0) && (getDot() != -1))
/*      */     {
/*  512 */       moveCaret(paramMouseEvent);
/*  513 */     } else if (!paramMouseEvent.isPopupTrigger())
/*  514 */       positionCaret(paramMouseEvent);
/*      */   }
/*      */ 
/*      */   private void adjustFocus(boolean paramBoolean)
/*      */   {
/*  524 */     if ((this.component != null) && (this.component.isEnabled()) && (this.component.isRequestFocusEnabled()))
/*      */     {
/*  526 */       if (paramBoolean) {
/*  527 */         this.component.requestFocusInWindow();
/*      */       }
/*      */       else
/*  530 */         this.component.requestFocus();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void mouseReleased(MouseEvent paramMouseEvent)
/*      */   {
/*  542 */     if ((!paramMouseEvent.isConsumed()) && (this.shouldHandleRelease) && (SwingUtilities.isLeftMouseButton(paramMouseEvent)))
/*      */     {
/*  546 */       adjustCaretAndFocus(paramMouseEvent);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void mouseEntered(MouseEvent paramMouseEvent)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void mouseExited(MouseEvent paramMouseEvent)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void mouseDragged(MouseEvent paramMouseEvent)
/*      */   {
/*  581 */     if ((!paramMouseEvent.isConsumed()) && (SwingUtilities.isLeftMouseButton(paramMouseEvent)))
/*  582 */       moveCaret(paramMouseEvent);
/*      */   }
/*      */ 
/*      */   public void mouseMoved(MouseEvent paramMouseEvent)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void paint(Graphics paramGraphics)
/*      */   {
/*  614 */     if (isVisible())
/*      */       try {
/*  616 */         TextUI localTextUI = this.component.getUI();
/*  617 */         Rectangle localRectangle1 = localTextUI.modelToView(this.component, this.dot, this.dotBias);
/*      */ 
/*  619 */         if ((localRectangle1 == null) || ((localRectangle1.width == 0) && (localRectangle1.height == 0))) {
/*  620 */           return;
/*      */         }
/*  622 */         if ((this.width > 0) && (this.height > 0) && (!_contains(localRectangle1.x, localRectangle1.y, localRectangle1.width, localRectangle1.height)))
/*      */         {
/*  626 */           Rectangle localRectangle2 = paramGraphics.getClipBounds();
/*      */ 
/*  628 */           if ((localRectangle2 != null) && (!localRectangle2.contains(this)))
/*      */           {
/*  631 */             repaint();
/*      */           }
/*      */ 
/*  636 */           damage(localRectangle1);
/*      */         }
/*  638 */         paramGraphics.setColor(this.component.getCaretColor());
/*  639 */         int i = getCaretWidth(localRectangle1.height);
/*  640 */         localRectangle1.x -= (i >> 1);
/*  641 */         paramGraphics.fillRect(localRectangle1.x, localRectangle1.y, i, localRectangle1.height);
/*      */ 
/*  648 */         Document localDocument = this.component.getDocument();
/*  649 */         if ((localDocument instanceof AbstractDocument)) {
/*  650 */           Element localElement = ((AbstractDocument)localDocument).getBidiRootElement();
/*  651 */           if ((localElement != null) && (localElement.getElementCount() > 1))
/*      */           {
/*  653 */             this.flagXPoints[0] = (localRectangle1.x + (this.dotLTR ? i : 0));
/*  654 */             this.flagYPoints[0] = localRectangle1.y;
/*  655 */             this.flagXPoints[1] = this.flagXPoints[0];
/*  656 */             this.flagYPoints[1] = (this.flagYPoints[0] + 4);
/*  657 */             this.flagXPoints[2] = (this.flagXPoints[0] + (this.dotLTR ? 4 : -4));
/*  658 */             this.flagYPoints[2] = this.flagYPoints[0];
/*  659 */             paramGraphics.fillPolygon(this.flagXPoints, this.flagYPoints, 3);
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (BadLocationException localBadLocationException)
/*      */       {
/*      */       }
/*      */   }
/*      */ 
/*      */   public void install(JTextComponent paramJTextComponent)
/*      */   {
/*  681 */     this.component = paramJTextComponent;
/*  682 */     Document localDocument = paramJTextComponent.getDocument();
/*  683 */     this.dot = (this.mark = 0);
/*  684 */     this.dotLTR = (this.markLTR = 1);
/*  685 */     this.dotBias = (this.markBias = Position.Bias.Forward);
/*  686 */     if (localDocument != null) {
/*  687 */       localDocument.addDocumentListener(this.handler);
/*      */     }
/*  689 */     paramJTextComponent.addPropertyChangeListener(this.handler);
/*  690 */     paramJTextComponent.addFocusListener(this);
/*  691 */     paramJTextComponent.addMouseListener(this);
/*  692 */     paramJTextComponent.addMouseMotionListener(this);
/*      */ 
/*  696 */     if (this.component.hasFocus()) {
/*  697 */       focusGained(null);
/*      */     }
/*      */ 
/*  700 */     Number localNumber = (Number)paramJTextComponent.getClientProperty("caretAspectRatio");
/*  701 */     if (localNumber != null)
/*  702 */       this.aspectRatio = localNumber.floatValue();
/*      */     else {
/*  704 */       this.aspectRatio = -1.0F;
/*      */     }
/*      */ 
/*  707 */     Integer localInteger = (Integer)paramJTextComponent.getClientProperty("caretWidth");
/*  708 */     if (localInteger != null)
/*  709 */       this.caretWidth = localInteger.intValue();
/*      */     else
/*  711 */       this.caretWidth = -1;
/*      */   }
/*      */ 
/*      */   public void deinstall(JTextComponent paramJTextComponent)
/*      */   {
/*  724 */     paramJTextComponent.removeMouseListener(this);
/*  725 */     paramJTextComponent.removeMouseMotionListener(this);
/*  726 */     paramJTextComponent.removeFocusListener(this);
/*  727 */     paramJTextComponent.removePropertyChangeListener(this.handler);
/*  728 */     Document localDocument = paramJTextComponent.getDocument();
/*  729 */     if (localDocument != null) {
/*  730 */       localDocument.removeDocumentListener(this.handler);
/*      */     }
/*  732 */     synchronized (this) {
/*  733 */       this.component = null;
/*      */     }
/*  735 */     if (this.flasher != null)
/*  736 */       this.flasher.stop();
/*      */   }
/*      */ 
/*      */   public void addChangeListener(ChangeListener paramChangeListener)
/*      */   {
/*  750 */     this.listenerList.add(ChangeListener.class, paramChangeListener);
/*      */   }
/*      */ 
/*      */   public void removeChangeListener(ChangeListener paramChangeListener)
/*      */   {
/*  760 */     this.listenerList.remove(ChangeListener.class, paramChangeListener);
/*      */   }
/*      */ 
/*      */   public ChangeListener[] getChangeListeners()
/*      */   {
/*  777 */     return (ChangeListener[])this.listenerList.getListeners(ChangeListener.class);
/*      */   }
/*      */ 
/*      */   protected void fireStateChanged()
/*      */   {
/*  790 */     Object[] arrayOfObject = this.listenerList.getListenerList();
/*      */ 
/*  793 */     for (int i = arrayOfObject.length - 2; i >= 0; i -= 2)
/*  794 */       if (arrayOfObject[i] == ChangeListener.class)
/*      */       {
/*  796 */         if (this.changeEvent == null)
/*  797 */           this.changeEvent = new ChangeEvent(this);
/*  798 */         ((ChangeListener)arrayOfObject[(i + 1)]).stateChanged(this.changeEvent);
/*      */       }
/*      */   }
/*      */ 
/*      */   public <T extends EventListener> T[] getListeners(Class<T> paramClass)
/*      */   {
/*  840 */     return this.listenerList.getListeners(paramClass);
/*      */   }
/*      */ 
/*      */   public void setSelectionVisible(boolean paramBoolean)
/*      */   {
/*  849 */     if (paramBoolean != this.selectionVisible) {
/*  850 */       this.selectionVisible = paramBoolean;
/*      */       Highlighter localHighlighter;
/*  851 */       if (this.selectionVisible)
/*      */       {
/*  853 */         localHighlighter = this.component.getHighlighter();
/*  854 */         if ((this.dot != this.mark) && (localHighlighter != null) && (this.selectionTag == null)) {
/*  855 */           int i = Math.min(this.dot, this.mark);
/*  856 */           int j = Math.max(this.dot, this.mark);
/*  857 */           Highlighter.HighlightPainter localHighlightPainter = getSelectionPainter();
/*      */           try {
/*  859 */             this.selectionTag = localHighlighter.addHighlight(i, j, localHighlightPainter);
/*      */           } catch (BadLocationException localBadLocationException) {
/*  861 */             this.selectionTag = null;
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*  866 */       else if (this.selectionTag != null) {
/*  867 */         localHighlighter = this.component.getHighlighter();
/*  868 */         localHighlighter.removeHighlight(this.selectionTag);
/*  869 */         this.selectionTag = null;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isSelectionVisible()
/*      */   {
/*  881 */     return this.selectionVisible;
/*      */   }
/*      */ 
/*      */   public boolean isActive()
/*      */   {
/*  899 */     return this.active;
/*      */   }
/*      */ 
/*      */   public boolean isVisible()
/*      */   {
/*  920 */     return this.visible;
/*      */   }
/*      */ 
/*      */   public void setVisible(boolean paramBoolean)
/*      */   {
/*  961 */     if (this.component != null) {
/*  962 */       this.active = paramBoolean;
/*  963 */       TextUI localTextUI = this.component.getUI();
/*  964 */       if (this.visible != paramBoolean) {
/*  965 */         this.visible = paramBoolean;
/*      */         try
/*      */         {
/*  968 */           Rectangle localRectangle = localTextUI.modelToView(this.component, this.dot, this.dotBias);
/*  969 */           damage(localRectangle);
/*      */         }
/*      */         catch (BadLocationException localBadLocationException) {
/*      */         }
/*      */       }
/*      */     }
/*  975 */     if (this.flasher != null)
/*  976 */       if (this.visible)
/*  977 */         this.flasher.start();
/*      */       else
/*  979 */         this.flasher.stop();
/*      */   }
/*      */ 
/*      */   public void setBlinkRate(int paramInt)
/*      */   {
/*  991 */     if (paramInt != 0) {
/*  992 */       if (this.flasher == null) {
/*  993 */         this.flasher = new Timer(paramInt, this.handler);
/*      */       }
/*  995 */       this.flasher.setDelay(paramInt);
/*      */     }
/*  997 */     else if (this.flasher != null) {
/*  998 */       this.flasher.stop();
/*  999 */       this.flasher.removeActionListener(this.handler);
/* 1000 */       this.flasher = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getBlinkRate()
/*      */   {
/* 1013 */     return this.flasher == null ? 0 : this.flasher.getDelay();
/*      */   }
/*      */ 
/*      */   public int getDot()
/*      */   {
/* 1023 */     return this.dot;
/*      */   }
/*      */ 
/*      */   public int getMark()
/*      */   {
/* 1034 */     return this.mark;
/*      */   }
/*      */ 
/*      */   public void setDot(int paramInt)
/*      */   {
/* 1047 */     setDot(paramInt, Position.Bias.Forward);
/*      */   }
/*      */ 
/*      */   public void moveDot(int paramInt)
/*      */   {
/* 1059 */     moveDot(paramInt, Position.Bias.Forward);
/*      */   }
/*      */ 
/*      */   public void moveDot(int paramInt, Position.Bias paramBias)
/*      */   {
/* 1075 */     if (paramBias == null) {
/* 1076 */       throw new IllegalArgumentException("null bias");
/*      */     }
/*      */ 
/* 1079 */     if (!this.component.isEnabled())
/*      */     {
/* 1081 */       setDot(paramInt, paramBias);
/* 1082 */       return;
/*      */     }
/* 1084 */     if (paramInt != this.dot) {
/* 1085 */       NavigationFilter localNavigationFilter = this.component.getNavigationFilter();
/*      */ 
/* 1087 */       if (localNavigationFilter != null) {
/* 1088 */         localNavigationFilter.moveDot(getFilterBypass(), paramInt, paramBias);
/*      */       }
/*      */       else
/* 1091 */         handleMoveDot(paramInt, paramBias);
/*      */     }
/*      */   }
/*      */ 
/*      */   void handleMoveDot(int paramInt, Position.Bias paramBias)
/*      */   {
/* 1097 */     changeCaretPosition(paramInt, paramBias);
/*      */ 
/* 1099 */     if (this.selectionVisible) {
/* 1100 */       Highlighter localHighlighter = this.component.getHighlighter();
/* 1101 */       if (localHighlighter != null) {
/* 1102 */         int i = Math.min(paramInt, this.mark);
/* 1103 */         int j = Math.max(paramInt, this.mark);
/*      */ 
/* 1106 */         if (i == j) {
/* 1107 */           if (this.selectionTag != null) {
/* 1108 */             localHighlighter.removeHighlight(this.selectionTag);
/* 1109 */             this.selectionTag = null;
/*      */           }
/*      */         }
/*      */         else
/*      */           try {
/* 1114 */             if (this.selectionTag != null) {
/* 1115 */               localHighlighter.changeHighlight(this.selectionTag, i, j);
/*      */             } else {
/* 1117 */               Highlighter.HighlightPainter localHighlightPainter = getSelectionPainter();
/* 1118 */               this.selectionTag = localHighlighter.addHighlight(i, j, localHighlightPainter);
/*      */             }
/*      */           } catch (BadLocationException localBadLocationException) {
/* 1121 */             throw new StateInvariantError("Bad caret position");
/*      */           }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setDot(int paramInt, Position.Bias paramBias)
/*      */   {
/* 1140 */     if (paramBias == null) {
/* 1141 */       throw new IllegalArgumentException("null bias");
/*      */     }
/*      */ 
/* 1144 */     NavigationFilter localNavigationFilter = this.component.getNavigationFilter();
/*      */ 
/* 1146 */     if (localNavigationFilter != null) {
/* 1147 */       localNavigationFilter.setDot(getFilterBypass(), paramInt, paramBias);
/*      */     }
/*      */     else
/* 1150 */       handleSetDot(paramInt, paramBias);
/*      */   }
/*      */ 
/*      */   void handleSetDot(int paramInt, Position.Bias paramBias)
/*      */   {
/* 1156 */     Document localDocument = this.component.getDocument();
/* 1157 */     if (localDocument != null) {
/* 1158 */       paramInt = Math.min(paramInt, localDocument.getLength());
/*      */     }
/* 1160 */     paramInt = Math.max(paramInt, 0);
/*      */ 
/* 1163 */     if (paramInt == 0) {
/* 1164 */       paramBias = Position.Bias.Forward;
/*      */     }
/* 1166 */     this.mark = paramInt;
/* 1167 */     if ((this.dot != paramInt) || (this.dotBias != paramBias) || (this.selectionTag != null) || (this.forceCaretPositionChange))
/*      */     {
/* 1169 */       changeCaretPosition(paramInt, paramBias);
/*      */     }
/* 1171 */     this.markBias = this.dotBias;
/* 1172 */     this.markLTR = this.dotLTR;
/* 1173 */     Highlighter localHighlighter = this.component.getHighlighter();
/* 1174 */     if ((localHighlighter != null) && (this.selectionTag != null)) {
/* 1175 */       localHighlighter.removeHighlight(this.selectionTag);
/* 1176 */       this.selectionTag = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   public Position.Bias getDotBias()
/*      */   {
/* 1187 */     return this.dotBias;
/*      */   }
/*      */ 
/*      */   public Position.Bias getMarkBias()
/*      */   {
/* 1197 */     return this.markBias;
/*      */   }
/*      */ 
/*      */   boolean isDotLeftToRight() {
/* 1201 */     return this.dotLTR;
/*      */   }
/*      */ 
/*      */   boolean isMarkLeftToRight() {
/* 1205 */     return this.markLTR;
/*      */   }
/*      */ 
/*      */   boolean isPositionLTR(int paramInt, Position.Bias paramBias) {
/* 1209 */     Document localDocument = this.component.getDocument();
/* 1210 */     if ((localDocument instanceof AbstractDocument)) {
/* 1211 */       if (paramBias == Position.Bias.Backward) { paramInt--; if (paramInt < 0)
/* 1212 */           paramInt = 0; }
/* 1213 */       return ((AbstractDocument)localDocument).isLeftToRight(paramInt, paramInt);
/*      */     }
/* 1215 */     return true;
/*      */   }
/*      */ 
/*      */   Position.Bias guessBiasForOffset(int paramInt, Position.Bias paramBias, boolean paramBoolean)
/*      */   {
/* 1229 */     if (paramBoolean != isPositionLTR(paramInt, paramBias)) {
/* 1230 */       paramBias = Position.Bias.Backward;
/*      */     }
/* 1232 */     else if ((paramBias != Position.Bias.Backward) && (paramBoolean != isPositionLTR(paramInt, Position.Bias.Backward)))
/*      */     {
/* 1234 */       paramBias = Position.Bias.Backward;
/*      */     }
/* 1236 */     if ((paramBias == Position.Bias.Backward) && (paramInt > 0))
/*      */       try {
/* 1238 */         Segment localSegment = new Segment();
/* 1239 */         this.component.getDocument().getText(paramInt - 1, 1, localSegment);
/* 1240 */         if ((localSegment.count > 0) && (localSegment.array[localSegment.offset] == '\n'))
/* 1241 */           paramBias = Position.Bias.Forward;
/*      */       }
/*      */       catch (BadLocationException localBadLocationException)
/*      */       {
/*      */       }
/* 1246 */     return paramBias;
/*      */   }
/*      */ 
/*      */   void changeCaretPosition(int paramInt, Position.Bias paramBias)
/*      */   {
/* 1260 */     repaint();
/*      */ 
/* 1264 */     if ((this.flasher != null) && (this.flasher.isRunning())) {
/* 1265 */       this.visible = true;
/* 1266 */       this.flasher.restart();
/*      */     }
/*      */ 
/* 1270 */     this.dot = paramInt;
/* 1271 */     this.dotBias = paramBias;
/* 1272 */     this.dotLTR = isPositionLTR(paramInt, paramBias);
/* 1273 */     fireStateChanged();
/*      */ 
/* 1275 */     updateSystemSelection();
/*      */ 
/* 1277 */     setMagicCaretPosition(null);
/*      */ 
/* 1284 */     Runnable local1 = new Runnable() {
/*      */       public void run() {
/* 1286 */         DefaultCaret.this.repaintNewCaret();
/*      */       }
/*      */     };
/* 1289 */     SwingUtilities.invokeLater(local1);
/*      */   }
/*      */ 
/*      */   void repaintNewCaret()
/*      */   {
/* 1299 */     if (this.component != null) {
/* 1300 */       TextUI localTextUI = this.component.getUI();
/* 1301 */       Document localDocument = this.component.getDocument();
/* 1302 */       if ((localTextUI != null) && (localDocument != null))
/*      */       {
/*      */         Rectangle localRectangle;
/*      */         try
/*      */         {
/* 1307 */           localRectangle = localTextUI.modelToView(this.component, this.dot, this.dotBias);
/*      */         } catch (BadLocationException localBadLocationException) {
/* 1309 */           localRectangle = null;
/*      */         }
/* 1311 */         if (localRectangle != null) {
/* 1312 */           adjustVisibility(localRectangle);
/*      */ 
/* 1314 */           if (getMagicCaretPosition() == null) {
/* 1315 */             setMagicCaretPosition(new Point(localRectangle.x, localRectangle.y));
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1320 */         damage(localRectangle);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void updateSystemSelection() {
/* 1326 */     if (!SwingUtilities2.canCurrentEventAccessSystemClipboard()) {
/* 1327 */       return;
/*      */     }
/* 1329 */     if ((this.dot != this.mark) && (this.component != null)) {
/* 1330 */       Clipboard localClipboard = getSystemSelection();
/* 1331 */       if (localClipboard != null)
/*      */       {
/*      */         String str;
/* 1333 */         if (((this.component instanceof JPasswordField)) && (this.component.getClientProperty("JPasswordField.cutCopyAllowed") != Boolean.TRUE))
/*      */         {
/* 1337 */           StringBuilder localStringBuilder = null;
/* 1338 */           char c = ((JPasswordField)this.component).getEchoChar();
/* 1339 */           int i = Math.min(getDot(), getMark());
/* 1340 */           int j = Math.max(getDot(), getMark());
/* 1341 */           for (int k = i; k < j; k++) {
/* 1342 */             if (localStringBuilder == null) {
/* 1343 */               localStringBuilder = new StringBuilder();
/*      */             }
/* 1345 */             localStringBuilder.append(c);
/*      */           }
/* 1347 */           str = localStringBuilder != null ? localStringBuilder.toString() : null;
/*      */         } else {
/* 1349 */           str = this.component.getSelectedText();
/*      */         }
/*      */         try {
/* 1352 */           localClipboard.setContents(new StringSelection(str), getClipboardOwner());
/*      */ 
/* 1355 */           this.ownsSelection = true;
/*      */         }
/*      */         catch (IllegalStateException localIllegalStateException)
/*      */         {
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private Clipboard getSystemSelection()
/*      */   {
/*      */     try {
/* 1367 */       return this.component.getToolkit().getSystemSelection();
/*      */     }
/*      */     catch (HeadlessException localHeadlessException) {
/*      */     }
/*      */     catch (SecurityException localSecurityException) {
/*      */     }
/* 1373 */     return null;
/*      */   }
/*      */ 
/*      */   private ClipboardOwner getClipboardOwner() {
/* 1377 */     return this.handler;
/*      */   }
/*      */ 
/*      */   private void ensureValidPosition()
/*      */   {
/* 1387 */     int i = this.component.getDocument().getLength();
/* 1388 */     if ((this.dot > i) || (this.mark > i))
/*      */     {
/* 1392 */       handleSetDot(i, Position.Bias.Forward);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setMagicCaretPosition(Point paramPoint)
/*      */   {
/* 1406 */     this.magicCaretPosition = paramPoint;
/*      */   }
/*      */ 
/*      */   public Point getMagicCaretPosition()
/*      */   {
/* 1416 */     return this.magicCaretPosition;
/*      */   }
/*      */ 
/*      */   public boolean equals(Object paramObject)
/*      */   {
/* 1430 */     return this == paramObject;
/*      */   }
/*      */ 
/*      */   public String toString() {
/* 1434 */     String str = "Dot=(" + this.dot + ", " + this.dotBias + ")";
/* 1435 */     str = str + " Mark=(" + this.mark + ", " + this.markBias + ")";
/* 1436 */     return str;
/*      */   }
/*      */ 
/*      */   private NavigationFilter.FilterBypass getFilterBypass() {
/* 1440 */     if (this.filterBypass == null) {
/* 1441 */       this.filterBypass = new DefaultFilterBypass(null);
/*      */     }
/* 1443 */     return this.filterBypass;
/*      */   }
/*      */ 
/*      */   private boolean _contains(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/* 1449 */     int i = this.width;
/* 1450 */     int j = this.height;
/* 1451 */     if ((i | j | paramInt3 | paramInt4) < 0)
/*      */     {
/* 1453 */       return false;
/*      */     }
/*      */ 
/* 1456 */     int k = this.x;
/* 1457 */     int m = this.y;
/* 1458 */     if ((paramInt1 < k) || (paramInt2 < m)) {
/* 1459 */       return false;
/*      */     }
/* 1461 */     if (paramInt3 > 0) {
/* 1462 */       i += k;
/* 1463 */       paramInt3 += paramInt1;
/* 1464 */       if (paramInt3 <= paramInt1)
/*      */       {
/* 1469 */         if ((i >= k) || (paramInt3 > i)) return false;
/*      */ 
/*      */       }
/* 1474 */       else if ((i >= k) && (paramInt3 > i)) return false;
/*      */ 
/*      */     }
/* 1477 */     else if (k + i < paramInt1) {
/* 1478 */       return false;
/*      */     }
/* 1480 */     if (paramInt4 > 0) {
/* 1481 */       j += m;
/* 1482 */       paramInt4 += paramInt2;
/* 1483 */       if (paramInt4 <= paramInt2) {
/* 1484 */         if ((j >= m) || (paramInt4 > j)) return false;
/*      */       }
/* 1486 */       else if ((j >= m) && (paramInt4 > j)) return false;
/*      */ 
/*      */     }
/* 1489 */     else if (m + j < paramInt2) {
/* 1490 */       return false;
/*      */     }
/* 1492 */     return true;
/*      */   }
/*      */ 
/*      */   int getCaretWidth(int paramInt) {
/* 1496 */     if (this.aspectRatio > -1.0F) {
/* 1497 */       return (int)(this.aspectRatio * paramInt) + 1;
/*      */     }
/*      */ 
/* 1500 */     if (this.caretWidth > -1) {
/* 1501 */       return this.caretWidth;
/*      */     }
/* 1503 */     Object localObject = UIManager.get("Caret.width");
/* 1504 */     if ((localObject instanceof Integer)) {
/* 1505 */       return ((Integer)localObject).intValue();
/*      */     }
/* 1507 */     return 1;
/*      */   }
/*      */ 
/*      */   private void readObject(ObjectInputStream paramObjectInputStream)
/*      */     throws ClassNotFoundException, IOException
/*      */   {
/* 1517 */     paramObjectInputStream.defaultReadObject();
/* 1518 */     this.handler = new Handler();
/* 1519 */     if (!paramObjectInputStream.readBoolean()) {
/* 1520 */       this.dotBias = Position.Bias.Forward;
/*      */     }
/*      */     else {
/* 1523 */       this.dotBias = Position.Bias.Backward;
/*      */     }
/* 1525 */     if (!paramObjectInputStream.readBoolean()) {
/* 1526 */       this.markBias = Position.Bias.Forward;
/*      */     }
/*      */     else
/* 1529 */       this.markBias = Position.Bias.Backward;
/*      */   }
/*      */ 
/*      */   private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException
/*      */   {
/* 1534 */     paramObjectOutputStream.defaultWriteObject();
/* 1535 */     paramObjectOutputStream.writeBoolean(this.dotBias == Position.Bias.Backward);
/* 1536 */     paramObjectOutputStream.writeBoolean(this.markBias == Position.Bias.Backward);
/*      */   }
/*      */ 
/*      */   private class DefaultFilterBypass extends NavigationFilter.FilterBypass
/*      */   {
/*      */     private DefaultFilterBypass()
/*      */     {
/*      */     }
/*      */ 
/*      */     public Caret getCaret()
/*      */     {
/* 1908 */       return DefaultCaret.this;
/*      */     }
/*      */ 
/*      */     public void setDot(int paramInt, Position.Bias paramBias) {
/* 1912 */       DefaultCaret.this.handleSetDot(paramInt, paramBias);
/*      */     }
/*      */ 
/*      */     public void moveDot(int paramInt, Position.Bias paramBias) {
/* 1916 */       DefaultCaret.this.handleMoveDot(paramInt, paramBias);
/*      */     }
/*      */   }
/*      */ 
/*      */   class Handler
/*      */     implements PropertyChangeListener, DocumentListener, ActionListener, ClipboardOwner
/*      */   {
/*      */     Handler()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void actionPerformed(ActionEvent paramActionEvent)
/*      */     {
/* 1639 */       if ((DefaultCaret.this.width == 0) || (DefaultCaret.this.height == 0))
/*      */       {
/* 1642 */         if (DefaultCaret.this.component != null) {
/* 1643 */           TextUI localTextUI = DefaultCaret.this.component.getUI();
/*      */           try {
/* 1645 */             Rectangle localRectangle = localTextUI.modelToView(DefaultCaret.this.component, DefaultCaret.this.dot, DefaultCaret.this.dotBias);
/*      */ 
/* 1647 */             if ((localRectangle != null) && (localRectangle.width != 0) && (localRectangle.height != 0))
/* 1648 */               DefaultCaret.this.damage(localRectangle);
/*      */           }
/*      */           catch (BadLocationException localBadLocationException) {
/*      */           }
/*      */         }
/*      */       }
/* 1654 */       DefaultCaret.this.visible = (!DefaultCaret.this.visible);
/* 1655 */       DefaultCaret.this.repaint();
/*      */     }
/*      */ 
/*      */     public void insertUpdate(DocumentEvent paramDocumentEvent)
/*      */     {
/* 1668 */       if ((DefaultCaret.this.getUpdatePolicy() == 1) || ((DefaultCaret.this.getUpdatePolicy() == 0) && (!SwingUtilities.isEventDispatchThread())))
/*      */       {
/* 1672 */         if (((paramDocumentEvent.getOffset() <= DefaultCaret.this.dot) || (paramDocumentEvent.getOffset() <= DefaultCaret.this.mark)) && (DefaultCaret.this.selectionTag != null)) {
/*      */           try
/*      */           {
/* 1675 */             DefaultCaret.this.component.getHighlighter().changeHighlight(DefaultCaret.this.selectionTag, Math.min(DefaultCaret.this.dot, DefaultCaret.this.mark), Math.max(DefaultCaret.this.dot, DefaultCaret.this.mark));
/*      */           }
/*      */           catch (BadLocationException localBadLocationException1) {
/* 1678 */             localBadLocationException1.printStackTrace();
/*      */           }
/*      */         }
/* 1681 */         return;
/*      */       }
/* 1683 */       int i = paramDocumentEvent.getOffset();
/* 1684 */       int j = paramDocumentEvent.getLength();
/* 1685 */       int k = DefaultCaret.this.dot;
/* 1686 */       int m = 0;
/*      */ 
/* 1688 */       if ((paramDocumentEvent instanceof AbstractDocument.UndoRedoDocumentEvent)) {
/* 1689 */         DefaultCaret.this.setDot(i + j);
/* 1690 */         return;
/*      */       }
/* 1692 */       if (k >= i) {
/* 1693 */         k += j;
/* 1694 */         m = (short)(m | 0x1);
/*      */       }
/* 1696 */       int n = DefaultCaret.this.mark;
/* 1697 */       if (n >= i) {
/* 1698 */         n += j;
/* 1699 */         m = (short)(m | 0x2);
/*      */       }
/*      */ 
/* 1702 */       if (m != 0) {
/* 1703 */         Position.Bias localBias = DefaultCaret.this.dotBias;
/* 1704 */         if (DefaultCaret.this.dot == i) { Document localDocument = DefaultCaret.this.component.getDocument();
/*      */           int i1;
/*      */           try {
/* 1708 */             Segment localSegment = new Segment();
/* 1709 */             localDocument.getText(k - 1, 1, localSegment);
/* 1710 */             i1 = (localSegment.count > 0) && (localSegment.array[localSegment.offset] == '\n') ? 1 : 0;
/*      */           }
/*      */           catch (BadLocationException localBadLocationException2) {
/* 1713 */             i1 = 0;
/*      */           }
/* 1715 */           if (i1 != 0)
/* 1716 */             localBias = Position.Bias.Forward;
/*      */           else {
/* 1718 */             localBias = Position.Bias.Backward;
/*      */           }
/*      */         }
/* 1721 */         if (n == k) {
/* 1722 */           DefaultCaret.this.setDot(k, localBias);
/* 1723 */           DefaultCaret.this.ensureValidPosition();
/*      */         }
/*      */         else {
/* 1726 */           DefaultCaret.this.setDot(n, DefaultCaret.this.markBias);
/* 1727 */           if (DefaultCaret.this.getDot() == n)
/*      */           {
/* 1731 */             DefaultCaret.this.moveDot(k, localBias);
/*      */           }
/* 1733 */           DefaultCaret.this.ensureValidPosition();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     public void removeUpdate(DocumentEvent paramDocumentEvent)
/*      */     {
/* 1746 */       if ((DefaultCaret.this.getUpdatePolicy() == 1) || ((DefaultCaret.this.getUpdatePolicy() == 0) && (!SwingUtilities.isEventDispatchThread())))
/*      */       {
/* 1750 */         i = DefaultCaret.this.component.getDocument().getLength();
/* 1751 */         DefaultCaret.this.dot = Math.min(DefaultCaret.this.dot, i);
/* 1752 */         DefaultCaret.this.mark = Math.min(DefaultCaret.this.mark, i);
/* 1753 */         if (((paramDocumentEvent.getOffset() < DefaultCaret.this.dot) || (paramDocumentEvent.getOffset() < DefaultCaret.this.mark)) && (DefaultCaret.this.selectionTag != null)) {
/*      */           try
/*      */           {
/* 1756 */             DefaultCaret.this.component.getHighlighter().changeHighlight(DefaultCaret.this.selectionTag, Math.min(DefaultCaret.this.dot, DefaultCaret.this.mark), Math.max(DefaultCaret.this.dot, DefaultCaret.this.mark));
/*      */           }
/*      */           catch (BadLocationException localBadLocationException) {
/* 1759 */             localBadLocationException.printStackTrace();
/*      */           }
/*      */         }
/* 1762 */         return;
/*      */       }
/* 1764 */       int i = paramDocumentEvent.getOffset();
/* 1765 */       int j = i + paramDocumentEvent.getLength();
/* 1766 */       int k = DefaultCaret.this.dot;
/* 1767 */       int m = 0;
/* 1768 */       int n = DefaultCaret.this.mark;
/* 1769 */       int i1 = 0;
/*      */ 
/* 1771 */       if ((paramDocumentEvent instanceof AbstractDocument.UndoRedoDocumentEvent)) {
/* 1772 */         DefaultCaret.this.setDot(i);
/* 1773 */         return;
/*      */       }
/* 1775 */       if (k >= j) {
/* 1776 */         k -= j - i;
/* 1777 */         if (k == j)
/* 1778 */           m = 1;
/*      */       }
/* 1780 */       else if (k >= i) {
/* 1781 */         k = i;
/* 1782 */         m = 1;
/*      */       }
/* 1784 */       if (n >= j) {
/* 1785 */         n -= j - i;
/* 1786 */         if (n == j)
/* 1787 */           i1 = 1;
/*      */       }
/* 1789 */       else if (n >= i) {
/* 1790 */         n = i;
/* 1791 */         i1 = 1;
/*      */       }
/* 1793 */       if (n == k) {
/* 1794 */         DefaultCaret.this.forceCaretPositionChange = true;
/*      */         try {
/* 1796 */           DefaultCaret.this.setDot(k, DefaultCaret.this.guessBiasForOffset(k, DefaultCaret.this.dotBias, DefaultCaret.this.dotLTR));
/*      */         }
/*      */         finally {
/* 1799 */           DefaultCaret.this.forceCaretPositionChange = false;
/*      */         }
/* 1801 */         DefaultCaret.this.ensureValidPosition();
/*      */       } else {
/* 1803 */         Position.Bias localBias1 = DefaultCaret.this.dotBias;
/* 1804 */         Position.Bias localBias2 = DefaultCaret.this.markBias;
/* 1805 */         if (m != 0) {
/* 1806 */           localBias1 = DefaultCaret.this.guessBiasForOffset(k, localBias1, DefaultCaret.this.dotLTR);
/*      */         }
/* 1808 */         if (i1 != 0) {
/* 1809 */           localBias2 = DefaultCaret.this.guessBiasForOffset(DefaultCaret.this.mark, localBias2, DefaultCaret.this.markLTR);
/*      */         }
/* 1811 */         DefaultCaret.this.setDot(n, localBias2);
/* 1812 */         if (DefaultCaret.this.getDot() == n)
/*      */         {
/* 1815 */           DefaultCaret.this.moveDot(k, localBias1);
/*      */         }
/* 1817 */         DefaultCaret.this.ensureValidPosition();
/*      */       }
/*      */     }
/*      */ 
/*      */     public void changedUpdate(DocumentEvent paramDocumentEvent)
/*      */     {
/* 1828 */       if ((DefaultCaret.this.getUpdatePolicy() == 1) || ((DefaultCaret.this.getUpdatePolicy() == 0) && (!SwingUtilities.isEventDispatchThread())))
/*      */       {
/* 1831 */         return;
/*      */       }
/* 1833 */       if ((paramDocumentEvent instanceof AbstractDocument.UndoRedoDocumentEvent))
/* 1834 */         DefaultCaret.this.setDot(paramDocumentEvent.getOffset() + paramDocumentEvent.getLength());
/*      */     }
/*      */ 
/*      */     public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
/*      */     {
/* 1845 */       Object localObject1 = paramPropertyChangeEvent.getOldValue();
/* 1846 */       Object localObject2 = paramPropertyChangeEvent.getNewValue();
/* 1847 */       if (((localObject1 instanceof Document)) || ((localObject2 instanceof Document))) {
/* 1848 */         DefaultCaret.this.setDot(0);
/* 1849 */         if (localObject1 != null) {
/* 1850 */           ((Document)localObject1).removeDocumentListener(this);
/*      */         }
/* 1852 */         if (localObject2 != null)
/* 1853 */           ((Document)localObject2).addDocumentListener(this);
/*      */       }
/*      */       else
/*      */       {
/*      */         Object localObject3;
/* 1855 */         if ("enabled".equals(paramPropertyChangeEvent.getPropertyName())) {
/* 1856 */           localObject3 = (Boolean)paramPropertyChangeEvent.getNewValue();
/* 1857 */           if (DefaultCaret.this.component.isFocusOwner())
/* 1858 */             if (localObject3 == Boolean.TRUE) {
/* 1859 */               if (DefaultCaret.this.component.isEditable()) {
/* 1860 */                 DefaultCaret.this.setVisible(true);
/*      */               }
/* 1862 */               DefaultCaret.this.setSelectionVisible(true);
/*      */             } else {
/* 1864 */               DefaultCaret.this.setVisible(false);
/* 1865 */               DefaultCaret.this.setSelectionVisible(false);
/*      */             }
/*      */         }
/* 1868 */         else if ("caretWidth".equals(paramPropertyChangeEvent.getPropertyName())) {
/* 1869 */           localObject3 = (Integer)paramPropertyChangeEvent.getNewValue();
/* 1870 */           if (localObject3 != null)
/* 1871 */             DefaultCaret.this.caretWidth = ((Integer)localObject3).intValue();
/*      */           else {
/* 1873 */             DefaultCaret.this.caretWidth = -1;
/*      */           }
/* 1875 */           DefaultCaret.this.repaint();
/* 1876 */         } else if ("caretAspectRatio".equals(paramPropertyChangeEvent.getPropertyName())) {
/* 1877 */           localObject3 = (Number)paramPropertyChangeEvent.getNewValue();
/* 1878 */           if (localObject3 != null)
/* 1879 */             DefaultCaret.this.aspectRatio = ((Number)localObject3).floatValue();
/*      */           else {
/* 1881 */             DefaultCaret.this.aspectRatio = -1.0F;
/*      */           }
/* 1883 */           DefaultCaret.this.repaint();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     public void lostOwnership(Clipboard paramClipboard, Transferable paramTransferable)
/*      */     {
/* 1896 */       if (DefaultCaret.this.ownsSelection) {
/* 1897 */         DefaultCaret.this.ownsSelection = false;
/* 1898 */         if ((DefaultCaret.this.component != null) && (!DefaultCaret.this.component.hasFocus()))
/* 1899 */           DefaultCaret.this.setSelectionVisible(false);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   class SafeScroller
/*      */     implements Runnable
/*      */   {
/*      */     Rectangle r;
/*      */ 
/*      */     SafeScroller(Rectangle arg2)
/*      */     {
/*      */       Object localObject;
/* 1614 */       this.r = localObject;
/*      */     }
/*      */ 
/*      */     public void run() {
/* 1618 */       if (DefaultCaret.this.component != null)
/* 1619 */         DefaultCaret.this.component.scrollRectToVisible(this.r);
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.text.DefaultCaret
 * JD-Core Version:    0.6.2
 */