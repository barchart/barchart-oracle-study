/*      */ package sun.awt.X11;
/*      */ 
/*      */ import com.sun.java.swing.plaf.motif.MotifTextAreaUI;
/*      */ import java.awt.Color;
/*      */ import java.awt.Component;
/*      */ import java.awt.ComponentOrientation;
/*      */ import java.awt.Container;
/*      */ import java.awt.Cursor;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.Font;
/*      */ import java.awt.FontMetrics;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.Insets;
/*      */ import java.awt.Point;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.SystemColor;
/*      */ import java.awt.TextArea;
/*      */ import java.awt.Window;
/*      */ import java.awt.event.FocusEvent;
/*      */ import java.awt.event.FocusListener;
/*      */ import java.awt.event.InputMethodEvent;
/*      */ import java.awt.event.KeyEvent;
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.awt.event.MouseWheelEvent;
/*      */ import java.awt.event.TextEvent;
/*      */ import java.awt.im.InputMethodRequests;
/*      */ import java.awt.peer.ComponentPeer;
/*      */ import java.awt.peer.TextAreaPeer;
/*      */ import java.beans.PropertyChangeEvent;
/*      */ import java.beans.PropertyChangeListener;
/*      */ import java.util.HashMap;
/*      */ import javax.swing.ButtonModel;
/*      */ import javax.swing.InputMap;
/*      */ import javax.swing.JButton;
/*      */ import javax.swing.JComponent;
/*      */ import javax.swing.JScrollBar;
/*      */ import javax.swing.JScrollPane;
/*      */ import javax.swing.JScrollPane.ScrollBar;
/*      */ import javax.swing.JTextArea;
/*      */ import javax.swing.JViewport;
/*      */ import javax.swing.SwingUtilities;
/*      */ import javax.swing.TransferHandler;
/*      */ import javax.swing.UIDefaults;
/*      */ import javax.swing.border.AbstractBorder;
/*      */ import javax.swing.border.Border;
/*      */ import javax.swing.border.CompoundBorder;
/*      */ import javax.swing.border.EmptyBorder;
/*      */ import javax.swing.event.DocumentEvent;
/*      */ import javax.swing.event.DocumentListener;
/*      */ import javax.swing.plaf.BorderUIResource.CompoundBorderUIResource;
/*      */ import javax.swing.plaf.UIResource;
/*      */ import javax.swing.plaf.basic.BasicArrowButton;
/*      */ import javax.swing.plaf.basic.BasicScrollBarUI;
/*      */ import javax.swing.plaf.basic.BasicScrollPaneUI;
/*      */ import javax.swing.text.Caret;
/*      */ import javax.swing.text.DefaultCaret;
/*      */ import javax.swing.text.Document;
/*      */ import javax.swing.text.JTextComponent;
/*      */ import sun.awt.AWTAccessor;
/*      */ import sun.awt.AWTAccessor.ClientPropertyKeyAccessor;
/*      */ import sun.awt.AWTAccessor.ComponentAccessor;
/*      */ import sun.awt.CausedFocusEvent;
/*      */ import sun.awt.SunToolkit;
/*      */ 
/*      */ class XTextAreaPeer extends XComponentPeer
/*      */   implements TextAreaPeer
/*      */ {
/*      */   boolean editable;
/*      */   AWTTextPane textPane;
/*      */   AWTTextArea jtext;
/*      */   boolean firstChangeSkipped;
/*   75 */   private final JavaMouseEventHandler javaMouseEventHandler = new JavaMouseEventHandler(this);
/*      */ 
/*      */   public long filterEvents(long paramLong)
/*      */   {
/*   81 */     Thread.dumpStack();
/*   82 */     return 0L;
/*      */   }
/*      */ 
/*      */   public Rectangle getCharacterBounds(int paramInt)
/*      */   {
/*   87 */     Thread.dumpStack();
/*   88 */     return null;
/*      */   }
/*      */ 
/*      */   public int getIndexAtPoint(int paramInt1, int paramInt2) {
/*   92 */     Thread.dumpStack();
/*   93 */     return 0;
/*      */   }
/*      */ 
/*      */   XTextAreaPeer(TextArea paramTextArea)
/*      */   {
/*  101 */     super(paramTextArea);
/*      */ 
/*  105 */     this.target = paramTextArea;
/*      */ 
/*  108 */     paramTextArea.enableInputMethods(true);
/*      */ 
/*  110 */     this.firstChangeSkipped = false;
/*  111 */     String str = paramTextArea.getText();
/*  112 */     this.jtext = new AWTTextArea(str, this);
/*  113 */     this.jtext.setWrapStyleWord(true);
/*  114 */     this.jtext.getDocument().addDocumentListener(this.jtext);
/*  115 */     XToolkit.specialPeerMap.put(this.jtext, this);
/*  116 */     this.jtext.enableInputMethods(true);
/*  117 */     this.textPane = new AWTTextPane(this.jtext, this, paramTextArea.getParent());
/*      */ 
/*  119 */     setBounds(this.x, this.y, this.width, this.height, 3);
/*  120 */     this.textPane.setVisible(true);
/*  121 */     this.textPane.validate();
/*      */ 
/*  123 */     AWTAccessor.ComponentAccessor localComponentAccessor = AWTAccessor.getComponentAccessor();
/*  124 */     this.foreground = localComponentAccessor.getForeground(paramTextArea);
/*  125 */     if (this.foreground == null) {
/*  126 */       this.foreground = SystemColor.textText;
/*      */     }
/*  128 */     setForeground(this.foreground);
/*      */ 
/*  130 */     this.background = localComponentAccessor.getBackground(paramTextArea);
/*  131 */     if (this.background == null) {
/*  132 */       if (paramTextArea.isEditable()) this.background = SystemColor.text; else
/*  133 */         this.background = SystemColor.control;
/*      */     }
/*  135 */     setBackground(this.background);
/*      */ 
/*  137 */     if (!paramTextArea.isBackgroundSet())
/*      */     {
/*  140 */       localComponentAccessor.setBackground(paramTextArea, this.background);
/*      */     }
/*  142 */     if (!paramTextArea.isForegroundSet()) {
/*  143 */       paramTextArea.setForeground(SystemColor.textText);
/*      */     }
/*      */ 
/*  146 */     setFont(this.font);
/*      */ 
/*  148 */     int i = paramTextArea.getSelectionStart();
/*  149 */     int j = paramTextArea.getSelectionEnd();
/*      */ 
/*  151 */     if (j > i) {
/*  152 */       select(i, j);
/*      */     }
/*      */ 
/*  158 */     int k = Math.min(j, str.length());
/*  159 */     setCaretPosition(k);
/*      */ 
/*  161 */     setEditable(paramTextArea.isEditable());
/*      */ 
/*  163 */     setScrollBarVisibility();
/*      */ 
/*  165 */     setTextImpl(paramTextArea.getText());
/*      */ 
/*  168 */     this.firstChangeSkipped = true;
/*      */   }
/*      */ 
/*      */   public void dispose() {
/*  172 */     XToolkit.specialPeerMap.remove(this.jtext);
/*  173 */     this.jtext.removeNotify();
/*  174 */     this.textPane.removeNotify();
/*  175 */     super.dispose();
/*      */   }
/*      */ 
/*      */   public void pSetCursor(Cursor paramCursor, boolean paramBoolean)
/*      */   {
/*  188 */     Point localPoint1 = getLocationOnScreen();
/*  189 */     if ((paramBoolean) || (this.javaMouseEventHandler == null) || (localPoint1 == null))
/*      */     {
/*  193 */       super.pSetCursor(paramCursor, true);
/*  194 */       return;
/*      */     }
/*      */ 
/*  197 */     Point localPoint2 = new Point();
/*  198 */     ((XGlobalCursorManager)XGlobalCursorManager.getCursorManager()).getCursorPos(localPoint2);
/*      */ 
/*  200 */     Point localPoint3 = new Point(localPoint2.x - localPoint1.x, localPoint2.y - localPoint1.y);
/*      */ 
/*  202 */     this.javaMouseEventHandler.setPointerToUnderPoint(localPoint3);
/*  203 */     this.javaMouseEventHandler.setCursor();
/*      */   }
/*      */ 
/*      */   void setScrollBarVisibility() {
/*  207 */     int i = ((TextArea)this.target).getScrollbarVisibility();
/*  208 */     this.jtext.setLineWrap(false);
/*      */ 
/*  210 */     if (i == 3) {
/*  211 */       this.textPane.setHorizontalScrollBarPolicy(31);
/*  212 */       this.textPane.setVerticalScrollBarPolicy(21);
/*  213 */       this.jtext.setLineWrap(true);
/*      */     }
/*  215 */     else if (i == 0)
/*      */     {
/*  217 */       this.textPane.setHorizontalScrollBarPolicy(32);
/*  218 */       this.textPane.setVerticalScrollBarPolicy(22);
/*      */     }
/*  220 */     else if (i == 1) {
/*  221 */       this.textPane.setHorizontalScrollBarPolicy(31);
/*  222 */       this.textPane.setVerticalScrollBarPolicy(22);
/*  223 */       this.jtext.setLineWrap(true);
/*      */     }
/*  225 */     else if (i == 2) {
/*  226 */       this.textPane.setVerticalScrollBarPolicy(21);
/*  227 */       this.textPane.setHorizontalScrollBarPolicy(32);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Dimension getMinimumSize()
/*      */   {
/*  235 */     return getMinimumSize(10, 60);
/*      */   }
/*      */ 
/*      */   public Dimension getPreferredSize(int paramInt1, int paramInt2) {
/*  239 */     return getMinimumSize(paramInt1, paramInt2);
/*      */   }
/*      */ 
/*      */   public Dimension getMinimumSize(int paramInt1, int paramInt2)
/*      */   {
/*  254 */     int i = 0;
/*  255 */     int j = 0;
/*      */ 
/*  257 */     JScrollBar localJScrollBar1 = this.textPane.getVerticalScrollBar();
/*  258 */     if (localJScrollBar1 != null) {
/*  259 */       i = localJScrollBar1.getMinimumSize().width;
/*      */     }
/*      */ 
/*  262 */     JScrollBar localJScrollBar2 = this.textPane.getHorizontalScrollBar();
/*  263 */     if (localJScrollBar2 != null) {
/*  264 */       j = localJScrollBar2.getMinimumSize().height;
/*      */     }
/*      */ 
/*  267 */     Font localFont = this.jtext.getFont();
/*  268 */     FontMetrics localFontMetrics = this.jtext.getFontMetrics(localFont);
/*      */ 
/*  270 */     return new Dimension(localFontMetrics.charWidth('0') * paramInt2 + i, localFontMetrics.getHeight() * paramInt1 + j);
/*      */   }
/*      */ 
/*      */   public boolean isFocusable()
/*      */   {
/*  275 */     return true;
/*      */   }
/*      */ 
/*      */   public void setVisible(boolean paramBoolean) {
/*  279 */     super.setVisible(paramBoolean);
/*  280 */     if (this.textPane != null)
/*  281 */       this.textPane.setVisible(paramBoolean);
/*      */   }
/*      */ 
/*      */   void repaintText() {
/*  285 */     this.jtext.repaintNow();
/*      */   }
/*      */ 
/*      */   public void focusGained(FocusEvent paramFocusEvent) {
/*  289 */     super.focusGained(paramFocusEvent);
/*  290 */     this.jtext.forwardFocusGained(paramFocusEvent);
/*      */   }
/*      */ 
/*      */   public void focusLost(FocusEvent paramFocusEvent) {
/*  294 */     super.focusLost(paramFocusEvent);
/*  295 */     this.jtext.forwardFocusLost(paramFocusEvent);
/*      */   }
/*      */ 
/*      */   public void repaint()
/*      */   {
/*  305 */     if (this.textPane != null)
/*      */     {
/*  307 */       this.textPane.repaint();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void paint(Graphics paramGraphics) {
/*  312 */     if (this.textPane != null)
/*  313 */       this.textPane.paint(paramGraphics);
/*      */   }
/*      */ 
/*      */   public void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
/*      */   {
/*  318 */     super.setBounds(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
/*  319 */     if (this.textPane != null)
/*      */     {
/*  328 */       int i = paramInt1;
/*  329 */       int j = paramInt2;
/*  330 */       Container localContainer = this.target.getParent();
/*      */ 
/*  333 */       while (localContainer.isLightweight()) {
/*  334 */         i -= localContainer.getX();
/*  335 */         j -= localContainer.getY();
/*  336 */         localContainer = localContainer.getParent();
/*      */       }
/*  338 */       this.textPane.setBounds(i, j, paramInt3, paramInt4);
/*  339 */       this.textPane.validate();
/*      */     }
/*      */   }
/*      */ 
/*      */   void handleJavaKeyEvent(KeyEvent paramKeyEvent) {
/*  344 */     AWTAccessor.getComponentAccessor().processEvent(this.jtext, paramKeyEvent);
/*      */   }
/*      */   public boolean handlesWheelScrolling() {
/*  347 */     return true;
/*      */   }
/*      */   void handleJavaMouseWheelEvent(MouseWheelEvent paramMouseWheelEvent) {
/*  350 */     AWTAccessor.getComponentAccessor().processEvent(this.textPane, paramMouseWheelEvent);
/*      */   }
/*      */ 
/*      */   public void handleJavaMouseEvent(MouseEvent paramMouseEvent) {
/*  354 */     super.handleJavaMouseEvent(paramMouseEvent);
/*  355 */     this.javaMouseEventHandler.handle(paramMouseEvent);
/*      */   }
/*      */ 
/*      */   void handleJavaInputMethodEvent(InputMethodEvent paramInputMethodEvent) {
/*  359 */     if (this.jtext != null)
/*  360 */       this.jtext.processInputMethodEventPublic(paramInputMethodEvent);
/*      */   }
/*      */ 
/*      */   public void select(int paramInt1, int paramInt2)
/*      */   {
/*  367 */     this.jtext.select(paramInt1, paramInt2);
/*      */ 
/*  370 */     this.jtext.repaint();
/*      */   }
/*      */ 
/*      */   public void setBackground(Color paramColor) {
/*  374 */     super.setBackground(paramColor);
/*      */ 
/*  378 */     if (this.jtext != null) {
/*  379 */       this.jtext.setBackground(paramColor);
/*  380 */       this.jtext.setSelectedTextColor(paramColor);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setForeground(Color paramColor)
/*      */   {
/*  386 */     super.setForeground(paramColor);
/*      */ 
/*  390 */     if (this.jtext != null) {
/*  391 */       this.jtext.setForeground(this.foreground);
/*  392 */       this.jtext.setSelectionColor(this.foreground);
/*  393 */       this.jtext.setCaretColor(this.foreground);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setFont(Font paramFont)
/*      */   {
/*  399 */     super.setFont(paramFont);
/*      */ 
/*  403 */     if (this.jtext != null) {
/*  404 */       this.jtext.setFont(this.font);
/*      */     }
/*  406 */     this.textPane.validate();
/*      */   }
/*      */ 
/*      */   public void setEditable(boolean paramBoolean)
/*      */   {
/*  414 */     this.editable = paramBoolean;
/*  415 */     if (this.jtext != null) this.jtext.setEditable(paramBoolean);
/*  416 */     repaintText();
/*      */   }
/*      */ 
/*      */   public void setEnabled(boolean paramBoolean)
/*      */   {
/*  423 */     super.setEnabled(paramBoolean);
/*  424 */     if (this.jtext != null) {
/*  425 */       this.jtext.setEnabled(paramBoolean);
/*  426 */       this.jtext.repaint();
/*      */     }
/*      */   }
/*      */ 
/*      */   public InputMethodRequests getInputMethodRequests()
/*      */   {
/*  434 */     if (this.jtext != null) return this.jtext.getInputMethodRequests();
/*  435 */     return null;
/*      */   }
/*      */ 
/*      */   public int getSelectionStart()
/*      */   {
/*  442 */     return this.jtext.getSelectionStart();
/*      */   }
/*      */ 
/*      */   public int getSelectionEnd()
/*      */   {
/*  449 */     return this.jtext.getSelectionEnd();
/*      */   }
/*      */ 
/*      */   public String getText()
/*      */   {
/*  456 */     return this.jtext.getText();
/*      */   }
/*      */ 
/*      */   public void setText(String paramString)
/*      */   {
/*  463 */     setTextImpl(paramString);
/*  464 */     repaintText();
/*      */   }
/*      */ 
/*      */   protected boolean setTextImpl(String paramString) {
/*  468 */     if (this.jtext != null)
/*      */     {
/*  472 */       if ((this.jtext.getDocument().getLength() == 0) && (paramString.length() == 0)) {
/*  473 */         return true;
/*      */       }
/*      */ 
/*  480 */       this.jtext.getDocument().removeDocumentListener(this.jtext);
/*  481 */       this.jtext.setText(paramString);
/*  482 */       if (this.firstChangeSkipped) {
/*  483 */         postEvent(new TextEvent(this.target, 900));
/*      */       }
/*  485 */       this.jtext.getDocument().addDocumentListener(this.jtext);
/*      */     }
/*  487 */     return true;
/*      */   }
/*      */ 
/*      */   public void insert(String paramString, int paramInt)
/*      */   {
/*  495 */     if (this.jtext != null) {
/*  496 */       int i = (paramInt >= this.jtext.getDocument().getLength()) && (this.jtext.getDocument().getLength() != 0) ? 1 : 0;
/*  497 */       this.jtext.insert(paramString, paramInt);
/*  498 */       this.textPane.validate();
/*  499 */       if (i != 0) {
/*  500 */         JScrollBar localJScrollBar = this.textPane.getVerticalScrollBar();
/*  501 */         if (localJScrollBar != null)
/*  502 */           localJScrollBar.setValue(localJScrollBar.getMaximum() - localJScrollBar.getVisibleAmount());
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void replaceRange(String paramString, int paramInt1, int paramInt2)
/*      */   {
/*  513 */     if (this.jtext != null)
/*      */     {
/*  518 */       this.jtext.getDocument().removeDocumentListener(this.jtext);
/*  519 */       this.jtext.replaceRange(paramString, paramInt1, paramInt2);
/*  520 */       postEvent(new TextEvent(this.target, 900));
/*  521 */       this.jtext.getDocument().addDocumentListener(this.jtext);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setCaretPosition(int paramInt)
/*      */   {
/*  530 */     this.jtext.setCaretPosition(paramInt);
/*      */   }
/*      */ 
/*      */   public int getCaretPosition()
/*      */   {
/*  538 */     return this.jtext.getCaretPosition();
/*      */   }
/*      */ 
/*      */   public void insertText(String paramString, int paramInt)
/*      */   {
/*  546 */     insert(paramString, paramInt);
/*      */   }
/*      */ 
/*      */   public void replaceText(String paramString, int paramInt1, int paramInt2)
/*      */   {
/*  554 */     replaceRange(paramString, paramInt1, paramInt2);
/*      */   }
/*      */ 
/*      */   public Dimension minimumSize(int paramInt1, int paramInt2)
/*      */   {
/*  562 */     return getMinimumSize(paramInt1, paramInt2);
/*      */   }
/*      */ 
/*      */   public Dimension preferredSize(int paramInt1, int paramInt2)
/*      */   {
/*  570 */     return getPreferredSize(paramInt1, paramInt2);
/*      */   }
/*      */ 
/*      */   class AWTTextArea extends JTextArea
/*      */     implements DocumentListener
/*      */   {
/*  946 */     boolean isFocused = false;
/*      */     XTextAreaPeer peer;
/*      */ 
/*      */     public AWTTextArea(String paramXTextAreaPeer, XTextAreaPeer arg3)
/*      */     {
/*  950 */       super();
/*  951 */       setFocusable(false);
/*      */       Object localObject;
/*  952 */       this.peer = localObject;
/*      */     }
/*      */ 
/*      */     public void insertUpdate(DocumentEvent paramDocumentEvent) {
/*  956 */       if (this.peer != null)
/*  957 */         this.peer.postEvent(new TextEvent(this.peer.target, 900));
/*      */     }
/*      */ 
/*      */     public void removeUpdate(DocumentEvent paramDocumentEvent)
/*      */     {
/*  963 */       if (this.peer != null)
/*  964 */         this.peer.postEvent(new TextEvent(this.peer.target, 900));
/*      */     }
/*      */ 
/*      */     public void changedUpdate(DocumentEvent paramDocumentEvent)
/*      */     {
/*  970 */       if (this.peer != null)
/*  971 */         this.peer.postEvent(new TextEvent(this.peer.target, 900));
/*      */     }
/*      */ 
/*      */     void forwardFocusGained(FocusEvent paramFocusEvent)
/*      */     {
/*  977 */       this.isFocused = true;
/*  978 */       FocusEvent localFocusEvent = CausedFocusEvent.retarget(paramFocusEvent, this);
/*  979 */       super.processFocusEvent(localFocusEvent);
/*      */     }
/*      */ 
/*      */     void forwardFocusLost(FocusEvent paramFocusEvent)
/*      */     {
/*  984 */       this.isFocused = false;
/*  985 */       FocusEvent localFocusEvent = CausedFocusEvent.retarget(paramFocusEvent, this);
/*  986 */       super.processFocusEvent(localFocusEvent);
/*      */     }
/*      */ 
/*      */     public boolean hasFocus() {
/*  990 */       return this.isFocused;
/*      */     }
/*      */ 
/*      */     public void repaintNow() {
/*  994 */       paintImmediately(getBounds());
/*      */     }
/*      */ 
/*      */     public void processMouseEventPublic(MouseEvent paramMouseEvent) {
/*  998 */       processMouseEvent(paramMouseEvent);
/*      */     }
/*      */ 
/*      */     public void processMouseMotionEventPublic(MouseEvent paramMouseEvent) {
/* 1002 */       processMouseMotionEvent(paramMouseEvent);
/*      */     }
/*      */ 
/*      */     public void processInputMethodEventPublic(InputMethodEvent paramInputMethodEvent) {
/* 1006 */       processInputMethodEvent(paramInputMethodEvent);
/*      */     }
/*      */ 
/*      */     public void updateUI() {
/* 1010 */       XTextAreaPeer.AWTTextAreaUI localAWTTextAreaUI = new XTextAreaPeer.AWTTextAreaUI(XTextAreaPeer.this);
/* 1011 */       setUI(localAWTTextAreaUI);
/*      */     }
/*      */ 
/*      */     public void setTransferHandler(TransferHandler paramTransferHandler)
/*      */     {
/* 1017 */       TransferHandler localTransferHandler = (TransferHandler)getClientProperty(AWTAccessor.getClientPropertyKeyAccessor().getJComponent_TRANSFER_HANDLER());
/*      */ 
/* 1020 */       putClientProperty(AWTAccessor.getClientPropertyKeyAccessor().getJComponent_TRANSFER_HANDLER(), paramTransferHandler);
/*      */ 
/* 1024 */       firePropertyChange("transferHandler", localTransferHandler, paramTransferHandler);
/*      */     }
/*      */   }
/*      */ 
/*      */   class AWTTextAreaUI extends MotifTextAreaUI
/*      */   {
/*      */     JTextArea jta;
/*      */ 
/*      */     AWTTextAreaUI()
/*      */     {
/*      */     }
/*      */ 
/*      */     protected String getPropertyPrefix()
/*      */     {
/*  583 */       return "TextArea";
/*      */     }
/*      */     public void installUI(JComponent paramJComponent) {
/*  586 */       super.installUI(paramJComponent);
/*      */ 
/*  588 */       this.jta = ((JTextArea)paramJComponent);
/*      */ 
/*  590 */       JTextArea localJTextArea = this.jta;
/*      */ 
/*  592 */       UIDefaults localUIDefaults = XToolkit.getUIDefaults();
/*      */ 
/*  594 */       String str = getPropertyPrefix();
/*  595 */       Font localFont = localJTextArea.getFont();
/*  596 */       if ((localFont == null) || ((localFont instanceof UIResource))) {
/*  597 */         localJTextArea.setFont(localUIDefaults.getFont(str + ".font"));
/*      */       }
/*      */ 
/*  600 */       Color localColor1 = localJTextArea.getBackground();
/*  601 */       if ((localColor1 == null) || ((localColor1 instanceof UIResource))) {
/*  602 */         localJTextArea.setBackground(localUIDefaults.getColor(str + ".background"));
/*      */       }
/*      */ 
/*  605 */       Color localColor2 = localJTextArea.getForeground();
/*  606 */       if ((localColor2 == null) || ((localColor2 instanceof UIResource))) {
/*  607 */         localJTextArea.setForeground(localUIDefaults.getColor(str + ".foreground"));
/*      */       }
/*      */ 
/*  610 */       Color localColor3 = localJTextArea.getCaretColor();
/*  611 */       if ((localColor3 == null) || ((localColor3 instanceof UIResource))) {
/*  612 */         localJTextArea.setCaretColor(localUIDefaults.getColor(str + ".caretForeground"));
/*      */       }
/*      */ 
/*  615 */       Color localColor4 = localJTextArea.getSelectionColor();
/*  616 */       if ((localColor4 == null) || ((localColor4 instanceof UIResource))) {
/*  617 */         localJTextArea.setSelectionColor(localUIDefaults.getColor(str + ".selectionBackground"));
/*      */       }
/*      */ 
/*  620 */       Color localColor5 = localJTextArea.getSelectedTextColor();
/*  621 */       if ((localColor5 == null) || ((localColor5 instanceof UIResource))) {
/*  622 */         localJTextArea.setSelectedTextColor(localUIDefaults.getColor(str + ".selectionForeground"));
/*      */       }
/*      */ 
/*  625 */       Color localColor6 = localJTextArea.getDisabledTextColor();
/*  626 */       if ((localColor6 == null) || ((localColor6 instanceof UIResource))) {
/*  627 */         localJTextArea.setDisabledTextColor(localUIDefaults.getColor(str + ".inactiveForeground"));
/*      */       }
/*      */ 
/*  630 */       XTextAreaPeer.BevelBorder localBevelBorder = new XTextAreaPeer.BevelBorder(false, SystemColor.controlDkShadow, SystemColor.controlLtHighlight);
/*  631 */       localJTextArea.setBorder(new BorderUIResource.CompoundBorderUIResource(localBevelBorder, new EmptyBorder(2, 2, 2, 2)));
/*      */ 
/*  634 */       Insets localInsets = localJTextArea.getMargin();
/*  635 */       if ((localInsets == null) || ((localInsets instanceof UIResource)))
/*  636 */         localJTextArea.setMargin(localUIDefaults.getInsets(str + ".margin"));
/*      */     }
/*      */ 
/*      */     protected void installKeyboardActions()
/*      */     {
/*  641 */       super.installKeyboardActions();
/*      */ 
/*  643 */       JTextComponent localJTextComponent = getComponent();
/*      */ 
/*  645 */       UIDefaults localUIDefaults = XToolkit.getUIDefaults();
/*      */ 
/*  647 */       String str = getPropertyPrefix();
/*      */ 
/*  649 */       InputMap localInputMap = (InputMap)localUIDefaults.get(str + ".focusInputMap");
/*      */ 
/*  651 */       if (localInputMap != null)
/*  652 */         SwingUtilities.replaceUIInputMap(localJTextComponent, 0, localInputMap);
/*      */     }
/*      */ 
/*      */     protected Caret createCaret()
/*      */     {
/*  658 */       return new XTextAreaPeer.XAWTCaret(XTextAreaPeer.this);
/*      */     }
/*      */   }
/*      */ 
/*      */   private class AWTTextPane extends JScrollPane
/*      */     implements FocusListener
/*      */   {
/*      */     JTextArea jtext;
/*      */     XWindow xwin;
/* 1138 */     Color control = SystemColor.control;
/* 1139 */     Color focus = SystemColor.activeCaptionBorder;
/*      */ 
/*      */     public AWTTextPane(JTextArea paramXWindow, XWindow paramContainer, Container arg4) {
/* 1142 */       super();
/* 1143 */       this.xwin = paramContainer;
/* 1144 */       setDoubleBuffered(true);
/* 1145 */       paramXWindow.addFocusListener(this);
/*      */       Container localContainer;
/* 1146 */       AWTAccessor.getComponentAccessor().setParent(this, localContainer);
/* 1147 */       setViewportBorder(new XTextAreaPeer.BevelBorder(false, SystemColor.controlDkShadow, SystemColor.controlLtHighlight));
/* 1148 */       this.jtext = paramXWindow;
/* 1149 */       setFocusable(false);
/* 1150 */       addNotify();
/*      */     }
/*      */ 
/*      */     public void focusGained(FocusEvent paramFocusEvent) {
/* 1154 */       Graphics localGraphics = getGraphics();
/* 1155 */       Rectangle localRectangle = getViewportBorderBounds();
/* 1156 */       localGraphics.setColor(this.focus);
/* 1157 */       localGraphics.drawRect(localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height);
/* 1158 */       localGraphics.dispose();
/*      */     }
/*      */ 
/*      */     public void focusLost(FocusEvent paramFocusEvent) {
/* 1162 */       Graphics localGraphics = getGraphics();
/* 1163 */       Rectangle localRectangle = getViewportBorderBounds();
/* 1164 */       localGraphics.setColor(this.control);
/* 1165 */       localGraphics.drawRect(localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height);
/* 1166 */       localGraphics.dispose();
/*      */     }
/*      */ 
/*      */     public Window getRealParent() {
/* 1170 */       return (Window)this.xwin.target;
/*      */     }
/*      */ 
/*      */     public ComponentPeer getPeer() {
/* 1174 */       return (ComponentPeer)this.xwin;
/*      */     }
/*      */ 
/*      */     public void updateUI() {
/* 1178 */       XTextAreaPeer.XAWTScrollPaneUI localXAWTScrollPaneUI = new XTextAreaPeer.XAWTScrollPaneUI(XTextAreaPeer.this);
/* 1179 */       setUI(localXAWTScrollPaneUI);
/*      */     }
/*      */ 
/*      */     public JScrollBar createVerticalScrollBar() {
/* 1183 */       return new XAWTScrollBar(1);
/*      */     }
/*      */ 
/*      */     public JScrollBar createHorizontalScrollBar() {
/* 1187 */       return new XAWTScrollBar(0);
/*      */     }
/*      */ 
/*      */     public JTextArea getTextArea() {
/* 1191 */       return this.jtext;
/*      */     }
/*      */ 
/*      */     public Graphics getGraphics() {
/* 1195 */       return this.xwin.getGraphics();
/*      */     }
/*      */ 
/*      */     class XAWTScrollBar extends JScrollPane.ScrollBar
/*      */     {
/*      */       public XAWTScrollBar(int arg2)
/*      */       {
/* 1202 */         super(i);
/* 1203 */         setFocusable(false);
/*      */       }
/*      */ 
/*      */       public void updateUI() {
/* 1207 */         XTextAreaPeer.XAWTScrollBarUI localXAWTScrollBarUI = new XTextAreaPeer.XAWTScrollBarUI(XTextAreaPeer.this);
/* 1208 */         setUI(localXAWTScrollBarUI); } 
/*      */     }
/*      */   }
/*      */ 
/* 1214 */   static class BevelBorder extends AbstractBorder implements UIResource { private Color darkShadow = SystemColor.controlDkShadow;
/* 1215 */     private Color lightShadow = SystemColor.controlLtHighlight;
/* 1216 */     private Color control = SystemColor.controlShadow;
/*      */     private boolean isRaised;
/*      */ 
/* 1220 */     public BevelBorder(boolean paramBoolean, Color paramColor1, Color paramColor2) { this.isRaised = paramBoolean;
/* 1221 */       this.darkShadow = paramColor1;
/* 1222 */       this.lightShadow = paramColor2; }
/*      */ 
/*      */     public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */     {
/* 1226 */       paramGraphics.setColor(this.isRaised ? this.lightShadow : this.darkShadow);
/* 1227 */       paramGraphics.drawLine(paramInt1, paramInt2, paramInt1 + paramInt3 - 1, paramInt2);
/* 1228 */       paramGraphics.drawLine(paramInt1, paramInt2 + paramInt4 - 1, paramInt1, paramInt2 + 1);
/*      */ 
/* 1230 */       paramGraphics.setColor(this.control);
/* 1231 */       paramGraphics.drawLine(paramInt1 + 1, paramInt2 + 1, paramInt1 + paramInt3 - 2, paramInt2 + 1);
/* 1232 */       paramGraphics.drawLine(paramInt1 + 1, paramInt2 + paramInt4 - 1, paramInt1 + 1, paramInt2 + 1);
/*      */ 
/* 1234 */       paramGraphics.setColor(this.isRaised ? this.darkShadow : this.lightShadow);
/* 1235 */       paramGraphics.drawLine(paramInt1 + 1, paramInt2 + paramInt4 - 1, paramInt1 + paramInt3 - 1, paramInt2 + paramInt4 - 1);
/* 1236 */       paramGraphics.drawLine(paramInt1 + paramInt3 - 1, paramInt2 + paramInt4 - 1, paramInt1 + paramInt3 - 1, paramInt2 + 1);
/*      */ 
/* 1238 */       paramGraphics.setColor(this.control);
/* 1239 */       paramGraphics.drawLine(paramInt1 + 1, paramInt2 + paramInt4 - 2, paramInt1 + paramInt3 - 2, paramInt2 + paramInt4 - 2);
/* 1240 */       paramGraphics.drawLine(paramInt1 + paramInt3 - 2, paramInt2 + paramInt4 - 2, paramInt1 + paramInt3 - 2, paramInt2 + 1);
/*      */     }
/*      */ 
/*      */     public Insets getBorderInsets(Component paramComponent) {
/* 1244 */       return getBorderInsets(paramComponent, new Insets(0, 0, 0, 0));
/*      */     }
/*      */ 
/*      */     public Insets getBorderInsets(Component paramComponent, Insets paramInsets) {
/* 1248 */       paramInsets.top = (paramInsets.left = paramInsets.bottom = paramInsets.right = 2);
/* 1249 */       return paramInsets;
/*      */     }
/*      */ 
/*      */     public boolean isOpaque(Component paramComponent) {
/* 1253 */       return true;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static final class JavaMouseEventHandler
/*      */   {
/*      */     private final XTextAreaPeer outer;
/* 1274 */     private final Pointer current = new Pointer(null);
/* 1275 */     private boolean grabbed = false;
/*      */ 
/*      */     JavaMouseEventHandler(XTextAreaPeer paramXTextAreaPeer) {
/* 1278 */       this.outer = paramXTextAreaPeer;
/*      */     }
/*      */ 
/*      */     void handle(MouseEvent paramMouseEvent)
/*      */     {
/* 1295 */       if (!this.grabbed)
/*      */       {
/* 1297 */         setPointerToUnderPoint(paramMouseEvent.getPoint());
/*      */       }
/* 1299 */       dispatch(paramMouseEvent);
/* 1300 */       boolean bool = this.grabbed;
/* 1301 */       grabbed_update(paramMouseEvent);
/* 1302 */       if ((bool) && (!this.grabbed)) {
/* 1303 */         setPointerToUnderPoint(paramMouseEvent.getPoint());
/*      */       }
/* 1305 */       setCursor();
/*      */     }
/*      */ 
/*      */     private void dispatch(MouseEvent paramMouseEvent)
/*      */     {
/* 1313 */       switch (XTextAreaPeer.1.$SwitchMap$sun$awt$X11$XTextAreaPeer$JavaMouseEventHandler$Pointer$Type[this.current.getType().ordinal()])
/*      */       {
/*      */       case 1:
/* 1316 */         Point localPoint1 = toViewportChildLocalSpace(this.outer.textPane.getViewport(), paramMouseEvent.getPoint());
/*      */ 
/* 1318 */         XTextAreaPeer.AWTTextArea localAWTTextArea = this.outer.jtext;
/* 1319 */         MouseEvent localMouseEvent = newMouseEvent(localAWTTextArea, localPoint1, paramMouseEvent);
/* 1320 */         int i = localMouseEvent.getID();
/* 1321 */         if ((i == 503) || (i == 506))
/* 1322 */           localAWTTextArea.processMouseMotionEventPublic(localMouseEvent);
/*      */         else {
/* 1324 */           localAWTTextArea.processMouseEventPublic(localMouseEvent);
/*      */         }
/* 1326 */         break;
/*      */       case 2:
/*      */       case 3:
/* 1337 */         Object localObject = this.current.getBar();
/* 1338 */         Point localPoint2 = toLocalSpace((Component)localObject, paramMouseEvent.getPoint());
/* 1339 */         if (this.current.getType() == XTextAreaPeer.JavaMouseEventHandler.Pointer.Type.BUTTON) {
/* 1340 */           localObject = this.current.getButton();
/* 1341 */           localPoint2 = toLocalSpace((Component)localObject, localPoint2);
/*      */         }
/* 1343 */         AWTAccessor.getComponentAccessor().processEvent((Component)localObject, newMouseEvent((Component)localObject, localPoint2, paramMouseEvent));
/*      */       }
/*      */     }
/*      */ 
/*      */     private static MouseEvent newMouseEvent(Component paramComponent, Point paramPoint, MouseEvent paramMouseEvent)
/*      */     {
/* 1351 */       MouseEvent localMouseEvent1 = paramMouseEvent;
/* 1352 */       MouseEvent localMouseEvent2 = new MouseEvent(paramComponent, localMouseEvent1.getID(), localMouseEvent1.getWhen(), localMouseEvent1.getModifiersEx() | localMouseEvent1.getModifiers(), paramPoint.x, paramPoint.y, localMouseEvent1.getXOnScreen(), localMouseEvent1.getYOnScreen(), localMouseEvent1.getClickCount(), localMouseEvent1.isPopupTrigger(), localMouseEvent1.getButton());
/*      */ 
/* 1362 */       SunToolkit.setSystemGenerated(localMouseEvent2);
/* 1363 */       return localMouseEvent2;
/*      */     }
/*      */ 
/*      */     private void setCursor() {
/* 1367 */       if (this.current.getType() == XTextAreaPeer.JavaMouseEventHandler.Pointer.Type.TEXT)
/*      */       {
/* 1371 */         this.outer.pSetCursor(this.outer.target.getCursor(), true);
/*      */       }
/*      */       else
/*      */       {
/* 1379 */         this.outer.pSetCursor(this.outer.textPane.getCursor(), true);
/*      */       }
/*      */     }
/*      */ 
/*      */     private void grabbed_update(MouseEvent paramMouseEvent)
/*      */     {
/* 1404 */       this.grabbed = ((paramMouseEvent.getModifiersEx() & 0x1C00) != 0);
/*      */     }
/*      */ 
/*      */     private static Point toLocalSpace(Component paramComponent, Point paramPoint)
/*      */     {
/* 1411 */       Point localPoint1 = paramPoint;
/* 1412 */       Point localPoint2 = paramComponent.getLocation();
/* 1413 */       return new Point(localPoint1.x - localPoint2.x, localPoint1.y - localPoint2.y);
/*      */     }
/*      */ 
/*      */     private static Point toViewportChildLocalSpace(JViewport paramJViewport, Point paramPoint) {
/* 1417 */       Point localPoint1 = toLocalSpace(paramJViewport, paramPoint);
/* 1418 */       Point localPoint2 = paramJViewport.getViewPosition();
/* 1419 */       localPoint1.x += localPoint2.x;
/* 1420 */       localPoint1.y += localPoint2.y;
/* 1421 */       return localPoint1;
/*      */     }
/*      */ 
/*      */     private void setPointerToUnderPoint(Point paramPoint) {
/* 1425 */       if (this.outer.textPane.getViewport().getBounds().contains(paramPoint)) {
/* 1426 */         this.current.setText();
/*      */       }
/* 1428 */       else if (!setPointerIfPointOverScrollbar(this.outer.textPane.getVerticalScrollBar(), paramPoint))
/*      */       {
/* 1431 */         if (!setPointerIfPointOverScrollbar(this.outer.textPane.getHorizontalScrollBar(), paramPoint))
/*      */         {
/* 1434 */           this.current.setNone();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     private boolean setPointerIfPointOverScrollbar(JScrollBar paramJScrollBar, Point paramPoint) {
/* 1440 */       if (!paramJScrollBar.getBounds().contains(paramPoint)) {
/* 1441 */         return false;
/*      */       }
/* 1443 */       this.current.setBar(paramJScrollBar);
/* 1444 */       Point localPoint = toLocalSpace(paramJScrollBar, paramPoint);
/*      */ 
/* 1446 */       XTextAreaPeer.XAWTScrollBarUI localXAWTScrollBarUI = (XTextAreaPeer.XAWTScrollBarUI)paramJScrollBar.getUI();
/*      */ 
/* 1449 */       if (!setPointerIfPointOverButton(localXAWTScrollBarUI.getIncreaseButton(), localPoint)) {
/* 1450 */         setPointerIfPointOverButton(localXAWTScrollBarUI.getDecreaseButton(), localPoint);
/*      */       }
/*      */ 
/* 1453 */       return true;
/*      */     }
/*      */ 
/*      */     private boolean setPointerIfPointOverButton(JButton paramJButton, Point paramPoint) {
/* 1457 */       if (!paramJButton.getBounds().contains(paramPoint)) {
/* 1458 */         return false;
/*      */       }
/* 1460 */       this.current.setButton(paramJButton);
/* 1461 */       return true;
/*      */     }
/*      */     private static final class Pointer { private Type type;
/*      */       private JScrollBar bar;
/*      */       private JButton button;
/*      */ 
/* 1469 */       Type getType() { return this.type; }
/*      */ 
/*      */       boolean isNone() {
/* 1472 */         return this.type == Type.NONE;
/*      */       }
/*      */       JScrollBar getBar() {
/* 1475 */         int i = (this.type == Type.BAR) || (this.type == Type.BUTTON) ? 1 : 0;
/* 1476 */         assert (i != 0);
/* 1477 */         return i != 0 ? this.bar : null;
/*      */       }
/*      */       JButton getButton() {
/* 1480 */         int i = this.type == Type.BUTTON ? 1 : 0;
/* 1481 */         assert (i != 0);
/* 1482 */         return i != 0 ? this.button : null;
/*      */       }
/*      */       void setNone() {
/* 1485 */         this.type = Type.NONE;
/*      */       }
/*      */       void setText() {
/* 1488 */         this.type = Type.TEXT;
/*      */       }
/*      */       void setBar(JScrollBar paramJScrollBar) {
/* 1491 */         this.bar = paramJScrollBar;
/* 1492 */         this.type = Type.BAR;
/*      */       }
/*      */       void setButton(JButton paramJButton) {
/* 1495 */         this.button = paramJButton;
/* 1496 */         this.type = Type.BUTTON;
/*      */       }
/*      */ 
/*      */       static enum Type
/*      */       {
/* 1466 */         NONE, TEXT, BAR, BUTTON;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   class XAWTCaret extends DefaultCaret
/*      */   {
/*      */     XAWTCaret()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void focusGained(FocusEvent paramFocusEvent)
/*      */     {
/*  666 */       super.focusGained(paramFocusEvent);
/*  667 */       getComponent().repaint();
/*      */     }
/*      */ 
/*      */     public void focusLost(FocusEvent paramFocusEvent) {
/*  671 */       super.focusLost(paramFocusEvent);
/*  672 */       getComponent().repaint();
/*      */     }
/*      */ 
/*      */     public void setSelectionVisible(boolean paramBoolean)
/*      */     {
/*  679 */       if (paramBoolean) {
/*  680 */         super.setSelectionVisible(paramBoolean);
/*      */       }
/*      */       else
/*  683 */         setDot(getDot());
/*      */     }
/*      */   }
/*      */ 
/*      */   class XAWTScrollBarButton extends BasicArrowButton
/*      */   {
/*  691 */     UIDefaults uidefaults = XToolkit.getUIDefaults();
/*  692 */     private Color darkShadow = SystemColor.controlShadow;
/*  693 */     private Color lightShadow = SystemColor.controlLtHighlight;
/*  694 */     private Color buttonBack = this.uidefaults.getColor("ScrollBar.track");
/*      */ 
/*      */     public XAWTScrollBarButton(int arg2)
/*      */     {
/*  698 */       super();
/*      */ 
/*  700 */       switch (i) {
/*      */       case 1:
/*      */       case 3:
/*      */       case 5:
/*      */       case 7:
/*  705 */         this.direction = i;
/*  706 */         break;
/*      */       case 2:
/*      */       case 4:
/*      */       case 6:
/*      */       default:
/*  708 */         throw new IllegalArgumentException("invalid direction");
/*      */       }
/*      */ 
/*  711 */       setRequestFocusEnabled(false);
/*  712 */       setOpaque(true);
/*  713 */       setBackground(this.uidefaults.getColor("ScrollBar.thumb"));
/*  714 */       setForeground(this.uidefaults.getColor("ScrollBar.foreground"));
/*      */     }
/*      */ 
/*      */     public Dimension getPreferredSize() {
/*  718 */       switch (this.direction) {
/*      */       case 1:
/*      */       case 5:
/*  721 */         return new Dimension(11, 12);
/*      */       case 2:
/*      */       case 3:
/*      */       case 4:
/*      */       case 6:
/*  725 */       case 7: } return new Dimension(12, 11);
/*      */     }
/*      */ 
/*      */     public Dimension getMinimumSize()
/*      */     {
/*  730 */       return getPreferredSize();
/*      */     }
/*      */ 
/*      */     public Dimension getMaximumSize() {
/*  734 */       return getPreferredSize();
/*      */     }
/*      */ 
/*      */     public boolean isFocusTraversable() {
/*  738 */       return false;
/*      */     }
/*      */ 
/*      */     public void paint(Graphics paramGraphics)
/*      */     {
/*  743 */       int i = getWidth();
/*  744 */       int j = getHeight();
/*      */ 
/*  746 */       if (isOpaque()) {
/*  747 */         paramGraphics.setColor(this.buttonBack);
/*  748 */         paramGraphics.fillRect(0, 0, i, j);
/*      */       }
/*      */ 
/*  751 */       boolean bool = getModel().isPressed();
/*  752 */       Color localColor1 = bool ? this.darkShadow : this.lightShadow;
/*  753 */       Color localColor2 = bool ? this.lightShadow : this.darkShadow;
/*  754 */       Color localColor3 = getBackground();
/*      */ 
/*  756 */       int k = i / 2;
/*  757 */       int m = j / 2;
/*  758 */       int n = Math.min(i, j);
/*      */       int i1;
/*      */       int i2;
/*      */       int i3;
/*  760 */       switch (this.direction) {
/*      */       case 1:
/*  762 */         paramGraphics.setColor(localColor1);
/*  763 */         paramGraphics.drawLine(k, 0, k, 0);
/*  764 */         i1 = k - 1; i2 = 1; for (i3 = 1; i2 <= n - 2; i2 += 2) {
/*  765 */           paramGraphics.setColor(localColor1);
/*  766 */           paramGraphics.drawLine(i1, i2, i1, i2);
/*  767 */           if (i2 >= n - 2) {
/*  768 */             paramGraphics.drawLine(i1, i2 + 1, i1, i2 + 1);
/*      */           }
/*  770 */           paramGraphics.setColor(localColor3);
/*  771 */           paramGraphics.drawLine(i1 + 1, i2, i1 + i3, i2);
/*  772 */           if (i2 < n - 2) {
/*  773 */             paramGraphics.drawLine(i1, i2 + 1, i1 + i3 + 1, i2 + 1);
/*      */           }
/*  775 */           paramGraphics.setColor(localColor2);
/*  776 */           paramGraphics.drawLine(i1 + i3 + 1, i2, i1 + i3 + 1, i2);
/*  777 */           if (i2 >= n - 2) {
/*  778 */             paramGraphics.drawLine(i1 + 1, i2 + 1, i1 + i3 + 1, i2 + 1);
/*      */           }
/*  780 */           i3 += 2;
/*  781 */           i1--;
/*      */         }
/*  783 */         break;
/*      */       case 5:
/*  786 */         paramGraphics.setColor(localColor2);
/*  787 */         paramGraphics.drawLine(k, n, k, n);
/*  788 */         i1 = k - 1; i2 = n - 1; for (i3 = 1; i2 >= 1; i2 -= 2) {
/*  789 */           paramGraphics.setColor(localColor1);
/*  790 */           paramGraphics.drawLine(i1, i2, i1, i2);
/*  791 */           if (i2 <= 2) {
/*  792 */             paramGraphics.drawLine(i1, i2 - 1, i1 + i3 + 1, i2 - 1);
/*      */           }
/*  794 */           paramGraphics.setColor(localColor3);
/*  795 */           paramGraphics.drawLine(i1 + 1, i2, i1 + i3, i2);
/*  796 */           if (i2 > 2) {
/*  797 */             paramGraphics.drawLine(i1, i2 - 1, i1 + i3 + 1, i2 - 1);
/*      */           }
/*  799 */           paramGraphics.setColor(localColor2);
/*  800 */           paramGraphics.drawLine(i1 + i3 + 1, i2, i1 + i3 + 1, i2);
/*      */ 
/*  802 */           i3 += 2;
/*  803 */           i1--;
/*      */         }
/*  805 */         break;
/*      */       case 3:
/*  808 */         paramGraphics.setColor(localColor1);
/*  809 */         paramGraphics.drawLine(n, m, n, m);
/*  810 */         i1 = m - 1; i2 = n - 1; for (i3 = 1; i2 >= 1; i2 -= 2) {
/*  811 */           paramGraphics.setColor(localColor1);
/*  812 */           paramGraphics.drawLine(i2, i1, i2, i1);
/*  813 */           if (i2 <= 2) {
/*  814 */             paramGraphics.drawLine(i2 - 1, i1, i2 - 1, i1 + i3 + 1);
/*      */           }
/*  816 */           paramGraphics.setColor(localColor3);
/*  817 */           paramGraphics.drawLine(i2, i1 + 1, i2, i1 + i3);
/*  818 */           if (i2 > 2) {
/*  819 */             paramGraphics.drawLine(i2 - 1, i1, i2 - 1, i1 + i3 + 1);
/*      */           }
/*  821 */           paramGraphics.setColor(localColor2);
/*  822 */           paramGraphics.drawLine(i2, i1 + i3 + 1, i2, i1 + i3 + 1);
/*      */ 
/*  824 */           i3 += 2;
/*  825 */           i1--;
/*      */         }
/*  827 */         break;
/*      */       case 7:
/*  830 */         paramGraphics.setColor(localColor2);
/*  831 */         paramGraphics.drawLine(0, m, 0, m);
/*  832 */         i1 = m - 1; i2 = 1; for (i3 = 1; i2 <= n - 2; i2 += 2) {
/*  833 */           paramGraphics.setColor(localColor1);
/*  834 */           paramGraphics.drawLine(i2, i1, i2, i1);
/*  835 */           if (i2 >= n - 2) {
/*  836 */             paramGraphics.drawLine(i2 + 1, i1, i2 + 1, i1);
/*      */           }
/*  838 */           paramGraphics.setColor(localColor3);
/*  839 */           paramGraphics.drawLine(i2, i1 + 1, i2, i1 + i3);
/*  840 */           if (i2 < n - 2) {
/*  841 */             paramGraphics.drawLine(i2 + 1, i1, i2 + 1, i1 + i3 + 1);
/*      */           }
/*  843 */           paramGraphics.setColor(localColor2);
/*  844 */           paramGraphics.drawLine(i2, i1 + i3 + 1, i2, i1 + i3 + 1);
/*  845 */           if (i2 >= n - 2) {
/*  846 */             paramGraphics.drawLine(i2 + 1, i1 + 1, i2 + 1, i1 + i3 + 1);
/*      */           }
/*  848 */           i3 += 2;
/*  849 */           i1--;
/*      */         }
/*      */       case 2:
/*      */       case 4:
/*      */       case 6:
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   class XAWTScrollBarUI extends BasicScrollBarUI
/*      */   {
/*      */     public XAWTScrollBarUI()
/*      */     {
/*      */     }
/*      */ 
/*      */     protected void installDefaults() {
/*  865 */       super.installDefaults();
/*  866 */       this.scrollbar.setBorder(new XTextAreaPeer.BevelBorder(false, SystemColor.controlDkShadow, SystemColor.controlLtHighlight));
/*      */     }
/*      */ 
/*      */     protected void configureScrollBarColors() {
/*  870 */       UIDefaults localUIDefaults = XToolkit.getUIDefaults();
/*  871 */       Color localColor1 = this.scrollbar.getBackground();
/*  872 */       if ((localColor1 == null) || ((localColor1 instanceof UIResource))) {
/*  873 */         this.scrollbar.setBackground(localUIDefaults.getColor("ScrollBar.background"));
/*      */       }
/*      */ 
/*  876 */       Color localColor2 = this.scrollbar.getForeground();
/*  877 */       if ((localColor2 == null) || ((localColor2 instanceof UIResource))) {
/*  878 */         this.scrollbar.setForeground(localUIDefaults.getColor("ScrollBar.foreground"));
/*      */       }
/*      */ 
/*  881 */       this.thumbHighlightColor = localUIDefaults.getColor("ScrollBar.thumbHighlight");
/*  882 */       this.thumbLightShadowColor = localUIDefaults.getColor("ScrollBar.thumbShadow");
/*  883 */       this.thumbDarkShadowColor = localUIDefaults.getColor("ScrollBar.thumbDarkShadow");
/*  884 */       this.thumbColor = localUIDefaults.getColor("ScrollBar.thumb");
/*  885 */       this.trackColor = localUIDefaults.getColor("ScrollBar.track");
/*      */ 
/*  887 */       this.trackHighlightColor = localUIDefaults.getColor("ScrollBar.trackHighlight");
/*      */     }
/*      */ 
/*      */     protected JButton createDecreaseButton(int paramInt)
/*      */     {
/*  892 */       XTextAreaPeer.XAWTScrollBarButton localXAWTScrollBarButton = new XTextAreaPeer.XAWTScrollBarButton(XTextAreaPeer.this, paramInt);
/*  893 */       return localXAWTScrollBarButton;
/*      */     }
/*      */ 
/*      */     protected JButton createIncreaseButton(int paramInt)
/*      */     {
/*  898 */       XTextAreaPeer.XAWTScrollBarButton localXAWTScrollBarButton = new XTextAreaPeer.XAWTScrollBarButton(XTextAreaPeer.this, paramInt);
/*  899 */       return localXAWTScrollBarButton;
/*      */     }
/*      */ 
/*      */     public JButton getDecreaseButton() {
/*  903 */       return this.decrButton;
/*      */     }
/*      */ 
/*      */     public JButton getIncreaseButton() {
/*  907 */       return this.incrButton;
/*      */     }
/*      */ 
/*      */     public void paint(Graphics paramGraphics, JComponent paramJComponent) {
/*  911 */       paintTrack(paramGraphics, paramJComponent, getTrackBounds());
/*  912 */       Rectangle localRectangle = getThumbBounds();
/*  913 */       paintThumb(paramGraphics, paramJComponent, localRectangle);
/*      */     }
/*      */ 
/*      */     public void paintThumb(Graphics paramGraphics, JComponent paramJComponent, Rectangle paramRectangle)
/*      */     {
/*  918 */       if (!this.scrollbar.isEnabled()) {
/*  919 */         return;
/*      */       }
/*      */ 
/*  922 */       if (paramRectangle.isEmpty()) {
/*  923 */         paramRectangle = getTrackBounds();
/*      */       }
/*  925 */       int i = paramRectangle.width;
/*  926 */       int j = paramRectangle.height;
/*      */ 
/*  928 */       paramGraphics.translate(paramRectangle.x, paramRectangle.y);
/*  929 */       paramGraphics.setColor(this.thumbColor);
/*  930 */       paramGraphics.fillRect(0, 0, i - 1, j - 1);
/*      */ 
/*  932 */       paramGraphics.setColor(this.thumbHighlightColor);
/*  933 */       paramGraphics.drawLine(0, 0, 0, j - 1);
/*  934 */       paramGraphics.drawLine(1, 0, i - 1, 0);
/*      */ 
/*  936 */       paramGraphics.setColor(this.thumbLightShadowColor);
/*  937 */       paramGraphics.drawLine(1, j - 1, i - 1, j - 1);
/*  938 */       paramGraphics.drawLine(i - 1, 1, i - 1, j - 2);
/*      */ 
/*  940 */       paramGraphics.translate(-paramRectangle.x, -paramRectangle.y);
/*      */     }
/*      */   }
/*      */ 
/*      */   class XAWTScrollPaneUI extends BasicScrollPaneUI
/*      */   {
/* 1031 */     private final Border vsbMarginBorderR = new EmptyBorder(0, 2, 0, 0);
/* 1032 */     private final Border vsbMarginBorderL = new EmptyBorder(0, 0, 0, 2);
/* 1033 */     private final Border hsbMarginBorder = new EmptyBorder(2, 0, 0, 0);
/*      */     private Border vsbBorder;
/*      */     private Border hsbBorder;
/*      */     private PropertyChangeListener propertyChangeHandler;
/*      */ 
/*      */     XAWTScrollPaneUI()
/*      */     {
/*      */     }
/*      */ 
/*      */     protected void installListeners(JScrollPane paramJScrollPane)
/*      */     {
/* 1041 */       super.installListeners(paramJScrollPane);
/* 1042 */       this.propertyChangeHandler = createPropertyChangeHandler();
/* 1043 */       paramJScrollPane.addPropertyChangeListener(this.propertyChangeHandler);
/*      */     }
/*      */ 
/*      */     public void paint(Graphics paramGraphics, JComponent paramJComponent) {
/* 1047 */       Border localBorder = this.scrollpane.getViewportBorder();
/* 1048 */       if (localBorder != null) {
/* 1049 */         Rectangle localRectangle = this.scrollpane.getViewportBorderBounds();
/* 1050 */         localBorder.paintBorder(this.scrollpane, paramGraphics, localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height);
/*      */       }
/*      */     }
/*      */ 
/*      */     protected void uninstallListeners(JScrollPane paramJScrollPane) {
/* 1055 */       super.uninstallListeners(paramJScrollPane);
/* 1056 */       paramJScrollPane.removePropertyChangeListener(this.propertyChangeHandler);
/*      */     }
/*      */ 
/*      */     private PropertyChangeListener createPropertyChangeHandler() {
/* 1060 */       return new PropertyChangeListener() {
/*      */         public void propertyChange(PropertyChangeEvent paramAnonymousPropertyChangeEvent) {
/* 1062 */           String str = paramAnonymousPropertyChangeEvent.getPropertyName();
/*      */ 
/* 1064 */           if (str.equals("componentOrientation")) {
/* 1065 */             JScrollPane localJScrollPane = (JScrollPane)paramAnonymousPropertyChangeEvent.getSource();
/* 1066 */             JScrollBar localJScrollBar = localJScrollPane.getVerticalScrollBar();
/* 1067 */             if (localJScrollBar != null) {
/* 1068 */               if (XTextAreaPeer.XAWTScrollPaneUI.this.isLeftToRight(localJScrollPane)) {
/* 1069 */                 XTextAreaPeer.XAWTScrollPaneUI.this.vsbBorder = new CompoundBorder(new EmptyBorder(0, 4, 0, -4), localJScrollBar.getBorder());
/*      */               }
/*      */               else {
/* 1072 */                 XTextAreaPeer.XAWTScrollPaneUI.this.vsbBorder = new CompoundBorder(new EmptyBorder(0, -4, 0, 4), localJScrollBar.getBorder());
/*      */               }
/*      */ 
/* 1075 */               localJScrollBar.setBorder(XTextAreaPeer.XAWTScrollPaneUI.this.vsbBorder);
/*      */             }
/*      */           }
/*      */         } } ;
/*      */     }
/*      */ 
/*      */     boolean isLeftToRight(Component paramComponent) {
/* 1082 */       return paramComponent.getComponentOrientation().isLeftToRight();
/*      */     }
/*      */ 
/*      */     protected void installDefaults(JScrollPane paramJScrollPane)
/*      */     {
/* 1087 */       Border localBorder = paramJScrollPane.getBorder();
/* 1088 */       UIDefaults localUIDefaults = XToolkit.getUIDefaults();
/* 1089 */       paramJScrollPane.setBorder(localUIDefaults.getBorder("ScrollPane.border"));
/* 1090 */       paramJScrollPane.setBackground(localUIDefaults.getColor("ScrollPane.background"));
/* 1091 */       paramJScrollPane.setViewportBorder(localUIDefaults.getBorder("TextField.border"));
/* 1092 */       JScrollBar localJScrollBar1 = paramJScrollPane.getVerticalScrollBar();
/* 1093 */       if (localJScrollBar1 != null) {
/* 1094 */         if (isLeftToRight(paramJScrollPane)) {
/* 1095 */           this.vsbBorder = new CompoundBorder(this.vsbMarginBorderR, localJScrollBar1.getBorder());
/*      */         }
/*      */         else
/*      */         {
/* 1099 */           this.vsbBorder = new CompoundBorder(this.vsbMarginBorderL, localJScrollBar1.getBorder());
/*      */         }
/*      */ 
/* 1102 */         localJScrollBar1.setBorder(this.vsbBorder);
/*      */       }
/*      */ 
/* 1105 */       JScrollBar localJScrollBar2 = paramJScrollPane.getHorizontalScrollBar();
/* 1106 */       if (localJScrollBar2 != null) {
/* 1107 */         this.hsbBorder = new CompoundBorder(this.hsbMarginBorder, localJScrollBar2.getBorder());
/* 1108 */         localJScrollBar2.setBorder(this.hsbBorder);
/*      */       }
/*      */     }
/*      */ 
/*      */     protected void uninstallDefaults(JScrollPane paramJScrollPane) {
/* 1113 */       super.uninstallDefaults(paramJScrollPane);
/*      */ 
/* 1115 */       JScrollBar localJScrollBar1 = this.scrollpane.getVerticalScrollBar();
/* 1116 */       if (localJScrollBar1 != null) {
/* 1117 */         if (localJScrollBar1.getBorder() == this.vsbBorder) {
/* 1118 */           localJScrollBar1.setBorder(null);
/*      */         }
/* 1120 */         this.vsbBorder = null;
/*      */       }
/*      */ 
/* 1123 */       JScrollBar localJScrollBar2 = this.scrollpane.getHorizontalScrollBar();
/* 1124 */       if (localJScrollBar2 != null) {
/* 1125 */         if (localJScrollBar2.getBorder() == this.hsbBorder) {
/* 1126 */           localJScrollBar2.setBorder(null);
/*      */         }
/* 1128 */         this.hsbBorder = null;
/*      */       }
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XTextAreaPeer
 * JD-Core Version:    0.6.2
 */