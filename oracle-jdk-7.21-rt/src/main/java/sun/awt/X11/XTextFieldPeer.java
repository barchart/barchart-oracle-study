/*     */ package sun.awt.X11;
/*     */ 
/*     */ import com.sun.java.swing.plaf.motif.MotifPasswordFieldUI;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Insets;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.SystemColor;
/*     */ import java.awt.TextField;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.FocusEvent;
/*     */ import java.awt.event.InputMethodEvent;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.TextEvent;
/*     */ import java.awt.im.InputMethodRequests;
/*     */ import java.awt.peer.ComponentPeer;
/*     */ import java.awt.peer.TextFieldPeer;
/*     */ import java.util.HashMap;
/*     */ import javax.swing.InputMap;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JPasswordField;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.SwingUtilities;
/*     */ import javax.swing.TransferHandler;
/*     */ import javax.swing.UIDefaults;
/*     */ import javax.swing.border.Border;
/*     */ import javax.swing.event.DocumentEvent;
/*     */ import javax.swing.event.DocumentListener;
/*     */ import javax.swing.plaf.UIResource;
/*     */ import javax.swing.text.Caret;
/*     */ import javax.swing.text.DefaultCaret;
/*     */ import javax.swing.text.Document;
/*     */ import javax.swing.text.JTextComponent;
/*     */ import sun.awt.AWTAccessor;
/*     */ import sun.awt.AWTAccessor.ClientPropertyKeyAccessor;
/*     */ import sun.awt.AWTAccessor.ComponentAccessor;
/*     */ import sun.awt.CausedFocusEvent;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ public class XTextFieldPeer extends XComponentPeer
/*     */   implements TextFieldPeer
/*     */ {
/*  61 */   private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.X11.XTextField");
/*     */   String text;
/*     */   XAWTTextField xtext;
/*     */   boolean firstChangeSkipped;
/*     */   private static final int PADDING = 16;
/*     */ 
/*     */   public XTextFieldPeer(TextField paramTextField)
/*     */   {
/*  69 */     super(paramTextField);
/*     */ 
/*  71 */     this.firstChangeSkipped = false;
/*  72 */     this.text = paramTextField.getText();
/*  73 */     this.xtext = new XAWTTextField(this.text, this, paramTextField.getParent());
/*  74 */     this.xtext.getDocument().addDocumentListener(this.xtext);
/*  75 */     this.xtext.setCursor(paramTextField.getCursor());
/*  76 */     paramTextField.enableInputMethods(true);
/*  77 */     this.xtext.enableInputMethods(true);
/*  78 */     XToolkit.specialPeerMap.put(this.xtext, this);
/*     */ 
/*  80 */     TextField localTextField = paramTextField;
/*  81 */     initTextField();
/*  82 */     setText(localTextField.getText());
/*  83 */     if (localTextField.echoCharIsSet())
/*  84 */       setEchoChar(localTextField.getEchoChar());
/*     */     else {
/*  86 */       setEchoChar('\000');
/*     */     }
/*  88 */     int i = localTextField.getSelectionStart();
/*  89 */     int j = localTextField.getSelectionEnd();
/*     */ 
/*  91 */     if (j > i) {
/*  92 */       select(i, j);
/*     */     }
/*     */ 
/*  98 */     int k = Math.min(j, this.text.length());
/*  99 */     setCaretPosition(k);
/*     */ 
/* 101 */     setEditable(localTextField.isEditable());
/*     */ 
/* 104 */     this.firstChangeSkipped = true;
/*     */   }
/*     */ 
/*     */   public void dispose() {
/* 108 */     XToolkit.specialPeerMap.remove(this.xtext);
/* 109 */     this.xtext.removeNotify();
/* 110 */     super.dispose();
/*     */   }
/*     */ 
/*     */   void initTextField() {
/* 114 */     setVisible(this.target.isVisible());
/*     */ 
/* 116 */     setBounds(this.x, this.y, this.width, this.height, 3);
/*     */ 
/* 118 */     AWTAccessor.ComponentAccessor localComponentAccessor = AWTAccessor.getComponentAccessor();
/* 119 */     this.foreground = localComponentAccessor.getForeground(this.target);
/* 120 */     if (this.foreground == null) {
/* 121 */       this.foreground = SystemColor.textText;
/*     */     }
/* 123 */     setForeground(this.foreground);
/*     */ 
/* 125 */     this.background = localComponentAccessor.getBackground(this.target);
/* 126 */     if (this.background == null) {
/* 127 */       if (((TextField)this.target).isEditable()) this.background = SystemColor.text; else
/* 128 */         this.background = SystemColor.control;
/*     */     }
/* 130 */     setBackground(this.background);
/*     */ 
/* 132 */     if (!this.target.isBackgroundSet())
/*     */     {
/* 135 */       localComponentAccessor.setBackground(this.target, this.background);
/*     */     }
/* 137 */     if (!this.target.isForegroundSet()) {
/* 138 */       this.target.setForeground(SystemColor.textText);
/*     */     }
/*     */ 
/* 141 */     setFont(this.font);
/*     */   }
/*     */ 
/*     */   public void setEditable(boolean paramBoolean)
/*     */   {
/* 149 */     if (this.xtext != null) {
/* 150 */       this.xtext.setEditable(paramBoolean);
/* 151 */       this.xtext.repaint();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setEnabled(boolean paramBoolean)
/*     */   {
/* 159 */     super.setEnabled(paramBoolean);
/* 160 */     if (this.xtext != null) {
/* 161 */       this.xtext.setEnabled(paramBoolean);
/* 162 */       this.xtext.repaint();
/*     */     }
/*     */   }
/*     */ 
/*     */   public InputMethodRequests getInputMethodRequests()
/*     */   {
/* 171 */     if (this.xtext != null) return this.xtext.getInputMethodRequests();
/* 172 */     return null;
/*     */   }
/*     */ 
/*     */   void handleJavaInputMethodEvent(InputMethodEvent paramInputMethodEvent)
/*     */   {
/* 177 */     if (this.xtext != null)
/* 178 */       this.xtext.processInputMethodEventImpl(paramInputMethodEvent);
/*     */   }
/*     */ 
/*     */   public void setEchoChar(char paramChar)
/*     */   {
/* 186 */     if (this.xtext != null) {
/* 187 */       this.xtext.setEchoChar(paramChar);
/* 188 */       this.xtext.putClientProperty("JPasswordField.cutCopyAllowed", this.xtext.echoCharIsSet() ? Boolean.FALSE : Boolean.TRUE);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getSelectionStart()
/*     */   {
/* 197 */     return this.xtext.getSelectionStart();
/*     */   }
/*     */ 
/*     */   public int getSelectionEnd()
/*     */   {
/* 204 */     return this.xtext.getSelectionEnd();
/*     */   }
/*     */ 
/*     */   public String getText()
/*     */   {
/* 211 */     return this.xtext.getText();
/*     */   }
/*     */ 
/*     */   public void setText(String paramString)
/*     */   {
/* 218 */     setXAWTTextField(paramString);
/* 219 */     repaint();
/*     */   }
/*     */ 
/*     */   protected boolean setXAWTTextField(String paramString) {
/* 223 */     this.text = paramString;
/* 224 */     if (this.xtext != null)
/*     */     {
/* 229 */       this.xtext.getDocument().removeDocumentListener(this.xtext);
/* 230 */       this.xtext.setText(paramString);
/* 231 */       if (this.firstChangeSkipped) {
/* 232 */         postEvent(new TextEvent(this.target, 900));
/*     */       }
/* 234 */       this.xtext.getDocument().addDocumentListener(this.xtext);
/* 235 */       this.xtext.setCaretPosition(0);
/*     */     }
/* 237 */     return true;
/*     */   }
/*     */ 
/*     */   public void setCaretPosition(int paramInt)
/*     */   {
/* 245 */     if (this.xtext != null) this.xtext.setCaretPosition(paramInt);
/*     */   }
/*     */ 
/*     */   public void setEchoCharacter(char paramChar)
/*     */   {
/* 253 */     setEchoChar(paramChar);
/*     */   }
/*     */ 
/*     */   void repaintText() {
/* 257 */     this.xtext.repaintNow();
/*     */   }
/*     */ 
/*     */   public void setBackground(Color paramColor) {
/* 261 */     if (log.isLoggable(500)) log.fine("target=" + this.target + ", old=" + this.background + ", new=" + paramColor);
/* 262 */     this.background = paramColor;
/* 263 */     if (this.xtext != null) {
/* 264 */       this.xtext.setBackground(paramColor);
/* 265 */       this.xtext.setSelectedTextColor(paramColor);
/*     */     }
/* 267 */     repaintText();
/*     */   }
/*     */ 
/*     */   public void setForeground(Color paramColor) {
/* 271 */     this.foreground = paramColor;
/* 272 */     if (this.xtext != null) {
/* 273 */       this.xtext.setForeground(this.foreground);
/* 274 */       this.xtext.setSelectionColor(this.foreground);
/* 275 */       this.xtext.setCaretColor(this.foreground);
/*     */     }
/* 277 */     repaintText();
/*     */   }
/*     */ 
/*     */   public void setFont(Font paramFont) {
/* 281 */     synchronized (getStateLock()) {
/* 282 */       this.font = paramFont;
/* 283 */       if (this.xtext != null) {
/* 284 */         this.xtext.setFont(this.font);
/*     */       }
/*     */     }
/* 287 */     this.xtext.validate();
/*     */   }
/*     */ 
/*     */   public Dimension preferredSize(int paramInt)
/*     */   {
/* 295 */     return getPreferredSize(paramInt);
/*     */   }
/*     */ 
/*     */   public void deselect()
/*     */   {
/* 302 */     int i = this.xtext.getSelectionStart();
/* 303 */     int j = this.xtext.getSelectionEnd();
/* 304 */     if (i != j)
/* 305 */       this.xtext.select(i, i);
/*     */   }
/*     */ 
/*     */   public int getCaretPosition()
/*     */   {
/* 315 */     return this.xtext.getCaretPosition();
/*     */   }
/*     */ 
/*     */   public void select(int paramInt1, int paramInt2)
/*     */   {
/* 324 */     this.xtext.select(paramInt1, paramInt2);
/*     */ 
/* 327 */     this.xtext.repaint();
/*     */   }
/*     */ 
/*     */   public Dimension getMinimumSize()
/*     */   {
/* 332 */     return this.xtext.getMinimumSize();
/*     */   }
/*     */ 
/*     */   public Dimension getPreferredSize() {
/* 336 */     return this.xtext.getPreferredSize();
/*     */   }
/*     */ 
/*     */   public Dimension getPreferredSize(int paramInt) {
/* 340 */     return getMinimumSize(paramInt);
/*     */   }
/*     */ 
/*     */   public Dimension getMinimumSize(int paramInt)
/*     */   {
/* 346 */     Font localFont = this.xtext.getFont();
/* 347 */     FontMetrics localFontMetrics = this.xtext.getFontMetrics(localFont);
/* 348 */     return new Dimension(localFontMetrics.charWidth('0') * paramInt + 10, localFontMetrics.getMaxDescent() + localFontMetrics.getMaxAscent() + 16);
/*     */   }
/*     */ 
/*     */   public boolean isFocusable()
/*     */   {
/* 354 */     return true;
/*     */   }
/*     */ 
/*     */   public void action(long paramLong, int paramInt)
/*     */   {
/* 360 */     postEvent(new ActionEvent(this.target, 1001, this.text, paramLong, paramInt));
/*     */   }
/*     */ 
/*     */   protected void disposeImpl()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void repaint()
/*     */   {
/* 371 */     if (this.xtext != null) this.xtext.repaint(); 
/*     */   }
/*     */ 
/*     */   public void paint(Graphics paramGraphics)
/*     */   {
/* 375 */     if (this.xtext != null) this.xtext.paint(paramGraphics);
/*     */   }
/*     */ 
/*     */   public void print(Graphics paramGraphics)
/*     */   {
/* 380 */     if (this.xtext != null)
/* 381 */       this.xtext.print(paramGraphics);
/*     */   }
/*     */ 
/*     */   public void focusLost(FocusEvent paramFocusEvent)
/*     */   {
/* 386 */     super.focusLost(paramFocusEvent);
/* 387 */     this.xtext.forwardFocusLost(paramFocusEvent);
/*     */   }
/*     */ 
/*     */   public void focusGained(FocusEvent paramFocusEvent) {
/* 391 */     super.focusGained(paramFocusEvent);
/* 392 */     this.xtext.forwardFocusGained(paramFocusEvent);
/*     */   }
/*     */ 
/*     */   void handleJavaKeyEvent(KeyEvent paramKeyEvent) {
/* 396 */     AWTAccessor.getComponentAccessor().processEvent(this.xtext, paramKeyEvent);
/*     */   }
/*     */ 
/*     */   public void handleJavaMouseEvent(MouseEvent paramMouseEvent)
/*     */   {
/* 401 */     super.handleJavaMouseEvent(paramMouseEvent);
/* 402 */     if (this.xtext != null) {
/* 403 */       paramMouseEvent.setSource(this.xtext);
/* 404 */       int i = paramMouseEvent.getID();
/* 405 */       if ((i == 506) || (i == 503))
/* 406 */         this.xtext.processMouseMotionEventImpl(paramMouseEvent);
/*     */       else
/* 408 */         this.xtext.processMouseEventImpl(paramMouseEvent);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Dimension minimumSize()
/*     */   {
/* 417 */     return getMinimumSize();
/*     */   }
/*     */ 
/*     */   public Dimension minimumSize(int paramInt)
/*     */   {
/* 424 */     return getMinimumSize(paramInt);
/*     */   }
/*     */ 
/*     */   public void setVisible(boolean paramBoolean) {
/* 428 */     super.setVisible(paramBoolean);
/* 429 */     if (this.xtext != null) this.xtext.setVisible(paramBoolean); 
/*     */   }
/*     */ 
/*     */   public void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
/*     */   {
/* 433 */     super.setBounds(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
/* 434 */     if (this.xtext != null)
/*     */     {
/* 443 */       int i = paramInt1;
/* 444 */       int j = paramInt2;
/* 445 */       Container localContainer = this.target.getParent();
/*     */ 
/* 448 */       while (localContainer.isLightweight()) {
/* 449 */         i -= localContainer.getX();
/* 450 */         j -= localContainer.getY();
/* 451 */         localContainer = localContainer.getParent();
/*     */       }
/* 453 */       this.xtext.setBounds(i, j, paramInt3, paramInt4);
/* 454 */       this.xtext.validate();
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getIndexAtPoint(int paramInt1, int paramInt2)
/*     */   {
/* 464 */     return -1; } 
/* 465 */   public Rectangle getCharacterBounds(int paramInt) { return null; } 
/* 466 */   public long filterEvents(long paramLong) { return 0L; }
/*     */ 
/*     */ 
/*     */   class AWTTextFieldUI extends MotifPasswordFieldUI
/*     */   {
/*     */     JTextField jtf;
/*     */ 
/*     */     AWTTextFieldUI()
/*     */     {
/*     */     }
/*     */ 
/*     */     protected String getPropertyPrefix()
/*     */     {
/* 500 */       JTextComponent localJTextComponent = getComponent();
/* 501 */       if (((localJTextComponent instanceof JPasswordField)) && (((JPasswordField)localJTextComponent).echoCharIsSet())) {
/* 502 */         return "PasswordField";
/*     */       }
/* 504 */       return "TextField";
/*     */     }
/*     */ 
/*     */     public void installUI(JComponent paramJComponent)
/*     */     {
/* 509 */       super.installUI(paramJComponent);
/*     */ 
/* 511 */       this.jtf = ((JTextField)paramJComponent);
/*     */ 
/* 513 */       JTextField localJTextField = this.jtf;
/*     */ 
/* 515 */       UIDefaults localUIDefaults = XToolkit.getUIDefaults();
/*     */ 
/* 517 */       String str = getPropertyPrefix();
/* 518 */       Font localFont = localJTextField.getFont();
/* 519 */       if ((localFont == null) || ((localFont instanceof UIResource))) {
/* 520 */         localJTextField.setFont(localUIDefaults.getFont(str + ".font"));
/*     */       }
/*     */ 
/* 523 */       Color localColor1 = localJTextField.getBackground();
/* 524 */       if ((localColor1 == null) || ((localColor1 instanceof UIResource))) {
/* 525 */         localJTextField.setBackground(localUIDefaults.getColor(str + ".background"));
/*     */       }
/*     */ 
/* 528 */       Color localColor2 = localJTextField.getForeground();
/* 529 */       if ((localColor2 == null) || ((localColor2 instanceof UIResource))) {
/* 530 */         localJTextField.setForeground(localUIDefaults.getColor(str + ".foreground"));
/*     */       }
/*     */ 
/* 533 */       Color localColor3 = localJTextField.getCaretColor();
/* 534 */       if ((localColor3 == null) || ((localColor3 instanceof UIResource))) {
/* 535 */         localJTextField.setCaretColor(localUIDefaults.getColor(str + ".caretForeground"));
/*     */       }
/*     */ 
/* 538 */       Color localColor4 = localJTextField.getSelectionColor();
/* 539 */       if ((localColor4 == null) || ((localColor4 instanceof UIResource))) {
/* 540 */         localJTextField.setSelectionColor(localUIDefaults.getColor(str + ".selectionBackground"));
/*     */       }
/*     */ 
/* 543 */       Color localColor5 = localJTextField.getSelectedTextColor();
/* 544 */       if ((localColor5 == null) || ((localColor5 instanceof UIResource))) {
/* 545 */         localJTextField.setSelectedTextColor(localUIDefaults.getColor(str + ".selectionForeground"));
/*     */       }
/*     */ 
/* 548 */       Color localColor6 = localJTextField.getDisabledTextColor();
/* 549 */       if ((localColor6 == null) || ((localColor6 instanceof UIResource))) {
/* 550 */         localJTextField.setDisabledTextColor(localUIDefaults.getColor(str + ".inactiveForeground"));
/*     */       }
/*     */ 
/* 553 */       Border localBorder = localJTextField.getBorder();
/* 554 */       if ((localBorder == null) || ((localBorder instanceof UIResource))) {
/* 555 */         localJTextField.setBorder(localUIDefaults.getBorder(str + ".border"));
/*     */       }
/*     */ 
/* 558 */       Insets localInsets = localJTextField.getMargin();
/* 559 */       if ((localInsets == null) || ((localInsets instanceof UIResource)))
/* 560 */         localJTextField.setMargin(localUIDefaults.getInsets(str + ".margin"));
/*     */     }
/*     */ 
/*     */     protected void installKeyboardActions()
/*     */     {
/* 565 */       super.installKeyboardActions();
/*     */ 
/* 567 */       JTextComponent localJTextComponent = getComponent();
/*     */ 
/* 569 */       UIDefaults localUIDefaults = XToolkit.getUIDefaults();
/*     */ 
/* 571 */       String str = getPropertyPrefix();
/*     */ 
/* 573 */       InputMap localInputMap = (InputMap)localUIDefaults.get(str + ".focusInputMap");
/*     */ 
/* 575 */       if (localInputMap != null)
/* 576 */         SwingUtilities.replaceUIInputMap(localJTextComponent, 0, localInputMap);
/*     */     }
/*     */ 
/*     */     protected Caret createCaret()
/*     */     {
/* 582 */       return new XTextFieldPeer.XAWTCaret(XTextFieldPeer.this);
/*     */     }
/*     */   }
/*     */   class XAWTCaret extends DefaultCaret {
/*     */     XAWTCaret() {
/*     */     }
/* 588 */     public void focusGained(FocusEvent paramFocusEvent) { super.focusGained(paramFocusEvent);
/* 589 */       getComponent().repaint(); }
/*     */ 
/*     */     public void focusLost(FocusEvent paramFocusEvent)
/*     */     {
/* 593 */       super.focusLost(paramFocusEvent);
/* 594 */       getComponent().repaint();
/*     */     }
/*     */ 
/*     */     public void setSelectionVisible(boolean paramBoolean)
/*     */     {
/* 601 */       if (paramBoolean) {
/* 602 */         super.setSelectionVisible(paramBoolean);
/*     */       }
/*     */       else
/* 605 */         setDot(getDot());
/*     */     }
/*     */   }
/*     */ 
/*     */   class XAWTTextField extends JPasswordField
/*     */     implements ActionListener, DocumentListener
/*     */   {
/* 615 */     boolean isFocused = false;
/*     */     XComponentPeer peer;
/*     */ 
/*     */     public XAWTTextField(String paramXComponentPeer, XComponentPeer paramContainer, Container arg4)
/*     */     {
/* 620 */       super();
/* 621 */       this.peer = paramContainer;
/* 622 */       setDoubleBuffered(true);
/* 623 */       setFocusable(false);
/*     */       Container localContainer;
/* 624 */       AWTAccessor.getComponentAccessor().setParent(this, localContainer);
/* 625 */       setBackground(paramContainer.getPeerBackground());
/* 626 */       setForeground(paramContainer.getPeerForeground());
/* 627 */       setFont(paramContainer.getPeerFont());
/* 628 */       setCaretPosition(0);
/* 629 */       addActionListener(this);
/* 630 */       addNotify();
/*     */     }
/*     */ 
/*     */     public void actionPerformed(ActionEvent paramActionEvent)
/*     */     {
/* 635 */       this.peer.postEvent(new ActionEvent(this.peer.target, 1001, getText(), paramActionEvent.getWhen(), paramActionEvent.getModifiers()));
/*     */     }
/*     */ 
/*     */     public void insertUpdate(DocumentEvent paramDocumentEvent)
/*     */     {
/* 644 */       if (this.peer != null)
/* 645 */         this.peer.postEvent(new TextEvent(this.peer.target, 900));
/*     */     }
/*     */ 
/*     */     public void removeUpdate(DocumentEvent paramDocumentEvent)
/*     */     {
/* 651 */       if (this.peer != null)
/* 652 */         this.peer.postEvent(new TextEvent(this.peer.target, 900));
/*     */     }
/*     */ 
/*     */     public void changedUpdate(DocumentEvent paramDocumentEvent)
/*     */     {
/* 658 */       if (this.peer != null)
/* 659 */         this.peer.postEvent(new TextEvent(this.peer.target, 900));
/*     */     }
/*     */ 
/*     */     public ComponentPeer getPeer()
/*     */     {
/* 665 */       return this.peer;
/*     */     }
/*     */ 
/*     */     public void repaintNow()
/*     */     {
/* 670 */       paintImmediately(getBounds());
/*     */     }
/*     */ 
/*     */     public Graphics getGraphics() {
/* 674 */       return this.peer.getGraphics();
/*     */     }
/*     */ 
/*     */     public void updateUI() {
/* 678 */       XTextFieldPeer.AWTTextFieldUI localAWTTextFieldUI = new XTextFieldPeer.AWTTextFieldUI(XTextFieldPeer.this);
/* 679 */       setUI(localAWTTextFieldUI);
/*     */     }
/*     */ 
/*     */     void forwardFocusGained(FocusEvent paramFocusEvent)
/*     */     {
/* 684 */       this.isFocused = true;
/* 685 */       FocusEvent localFocusEvent = CausedFocusEvent.retarget(paramFocusEvent, this);
/* 686 */       super.processFocusEvent(localFocusEvent);
/*     */     }
/*     */ 
/*     */     void forwardFocusLost(FocusEvent paramFocusEvent)
/*     */     {
/* 692 */       this.isFocused = false;
/* 693 */       FocusEvent localFocusEvent = CausedFocusEvent.retarget(paramFocusEvent, this);
/* 694 */       super.processFocusEvent(localFocusEvent);
/*     */     }
/*     */ 
/*     */     public boolean hasFocus()
/*     */     {
/* 699 */       return this.isFocused;
/*     */     }
/*     */ 
/*     */     public void processInputMethodEventImpl(InputMethodEvent paramInputMethodEvent)
/*     */     {
/* 704 */       processInputMethodEvent(paramInputMethodEvent);
/*     */     }
/*     */ 
/*     */     public void processMouseEventImpl(MouseEvent paramMouseEvent) {
/* 708 */       processMouseEvent(paramMouseEvent);
/*     */     }
/*     */ 
/*     */     public void processMouseMotionEventImpl(MouseEvent paramMouseEvent) {
/* 712 */       processMouseMotionEvent(paramMouseEvent);
/*     */     }
/*     */ 
/*     */     public void setTransferHandler(TransferHandler paramTransferHandler)
/*     */     {
/* 718 */       TransferHandler localTransferHandler = (TransferHandler)getClientProperty(AWTAccessor.getClientPropertyKeyAccessor().getJComponent_TRANSFER_HANDLER());
/*     */ 
/* 721 */       putClientProperty(AWTAccessor.getClientPropertyKeyAccessor().getJComponent_TRANSFER_HANDLER(), paramTransferHandler);
/*     */ 
/* 725 */       firePropertyChange("transferHandler", localTransferHandler, paramTransferHandler);
/*     */     }
/*     */ 
/*     */     public void setEchoChar(char paramChar) {
/* 729 */       super.setEchoChar(paramChar);
/* 730 */       ((XTextFieldPeer.AWTTextFieldUI)this.ui).installKeyboardActions();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XTextFieldPeer
 * JD-Core Version:    0.6.2
 */