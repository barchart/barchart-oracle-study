/*     */ package com.sun.java.swing.plaf.windows;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.ComponentOrientation;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Insets;
/*     */ import java.awt.LayoutManager;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.event.KeyListener;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.MouseListener;
/*     */ import java.beans.PropertyChangeEvent;
/*     */ import java.beans.PropertyChangeListener;
/*     */ import javax.swing.ButtonModel;
/*     */ import javax.swing.CellRendererPane;
/*     */ import javax.swing.ComboBoxEditor;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JList;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.ListCellRenderer;
/*     */ import javax.swing.UIManager;
/*     */ import javax.swing.border.Border;
/*     */ import javax.swing.border.EmptyBorder;
/*     */ import javax.swing.plaf.ComponentUI;
/*     */ import javax.swing.plaf.basic.BasicComboBoxEditor.UIResource;
/*     */ import javax.swing.plaf.basic.BasicComboBoxRenderer.UIResource;
/*     */ import javax.swing.plaf.basic.BasicComboBoxUI;
/*     */ import javax.swing.plaf.basic.BasicComboBoxUI.ComboBoxLayoutManager;
/*     */ import javax.swing.plaf.basic.BasicComboPopup;
/*     */ import javax.swing.plaf.basic.BasicComboPopup.InvocationKeyHandler;
/*     */ import javax.swing.plaf.basic.ComboPopup;
/*     */ import sun.swing.DefaultLookup;
/*     */ import sun.swing.StringUIClientPropertyKey;
/*     */ 
/*     */ public class WindowsComboBoxUI extends BasicComboBoxUI
/*     */ {
/*  60 */   private static final MouseListener rolloverListener = new MouseAdapter()
/*     */   {
/*     */     private void handleRollover(MouseEvent paramAnonymousMouseEvent, boolean paramAnonymousBoolean) {
/*  63 */       JComboBox localJComboBox = getComboBox(paramAnonymousMouseEvent);
/*  64 */       WindowsComboBoxUI localWindowsComboBoxUI = getWindowsComboBoxUI(paramAnonymousMouseEvent);
/*  65 */       if ((localJComboBox == null) || (localWindowsComboBoxUI == null)) {
/*  66 */         return;
/*     */       }
/*  68 */       if (!localJComboBox.isEditable())
/*     */       {
/*  71 */         ButtonModel localButtonModel = null;
/*  72 */         if (localWindowsComboBoxUI.arrowButton != null) {
/*  73 */           localButtonModel = localWindowsComboBoxUI.arrowButton.getModel();
/*     */         }
/*  75 */         if (localButtonModel != null) {
/*  76 */           localButtonModel.setRollover(paramAnonymousBoolean);
/*     */         }
/*     */       }
/*  79 */       localWindowsComboBoxUI.isRollover = paramAnonymousBoolean;
/*  80 */       localJComboBox.repaint();
/*     */     }
/*     */ 
/*     */     public void mouseEntered(MouseEvent paramAnonymousMouseEvent) {
/*  84 */       handleRollover(paramAnonymousMouseEvent, true);
/*     */     }
/*     */ 
/*     */     public void mouseExited(MouseEvent paramAnonymousMouseEvent) {
/*  88 */       handleRollover(paramAnonymousMouseEvent, false);
/*     */     }
/*     */ 
/*     */     private JComboBox getComboBox(MouseEvent paramAnonymousMouseEvent) {
/*  92 */       Object localObject = paramAnonymousMouseEvent.getSource();
/*  93 */       JComboBox localJComboBox = null;
/*  94 */       if ((localObject instanceof JComboBox))
/*  95 */         localJComboBox = (JComboBox)localObject;
/*  96 */       else if ((localObject instanceof WindowsComboBoxUI.XPComboBoxButton)) {
/*  97 */         localJComboBox = ((WindowsComboBoxUI.XPComboBoxButton)localObject).getWindowsComboBoxUI().comboBox;
/*     */       }
/*     */ 
/* 100 */       return localJComboBox;
/*     */     }
/*     */ 
/*     */     private WindowsComboBoxUI getWindowsComboBoxUI(MouseEvent paramAnonymousMouseEvent) {
/* 104 */       JComboBox localJComboBox = getComboBox(paramAnonymousMouseEvent);
/* 105 */       WindowsComboBoxUI localWindowsComboBoxUI = null;
/* 106 */       if ((localJComboBox != null) && ((localJComboBox.getUI() instanceof WindowsComboBoxUI)))
/*     */       {
/* 108 */         localWindowsComboBoxUI = (WindowsComboBoxUI)localJComboBox.getUI();
/*     */       }
/* 110 */       return localWindowsComboBoxUI;
/*     */     }
/*  60 */   };
/*     */   private boolean isRollover;
/* 116 */   private static final PropertyChangeListener componentOrientationListener = new PropertyChangeListener()
/*     */   {
/*     */     public void propertyChange(PropertyChangeEvent paramAnonymousPropertyChangeEvent) {
/* 119 */       String str = paramAnonymousPropertyChangeEvent.getPropertyName();
/* 120 */       Object localObject = null;
/* 121 */       if (("componentOrientation" == str) && (((localObject = paramAnonymousPropertyChangeEvent.getSource()) instanceof JComboBox)) && ((((JComboBox)localObject).getUI() instanceof WindowsComboBoxUI)))
/*     */       {
/* 125 */         JComboBox localJComboBox = (JComboBox)localObject;
/* 126 */         WindowsComboBoxUI localWindowsComboBoxUI = (WindowsComboBoxUI)localJComboBox.getUI();
/* 127 */         if ((localWindowsComboBoxUI.arrowButton instanceof WindowsComboBoxUI.XPComboBoxButton))
/* 128 */           ((WindowsComboBoxUI.XPComboBoxButton)localWindowsComboBoxUI.arrowButton).setPart(localJComboBox.getComponentOrientation() == ComponentOrientation.RIGHT_TO_LEFT ? TMSchema.Part.CP_DROPDOWNBUTTONLEFT : TMSchema.Part.CP_DROPDOWNBUTTONRIGHT);
/*     */       }
/*     */     }
/* 116 */   };
/*     */ 
/*     */   public WindowsComboBoxUI()
/*     */   {
/* 114 */     this.isRollover = false;
/*     */   }
/*     */ 
/*     */   public static ComponentUI createUI(JComponent paramJComponent)
/*     */   {
/* 139 */     return new WindowsComboBoxUI();
/*     */   }
/*     */ 
/*     */   public void installUI(JComponent paramJComponent) {
/* 143 */     super.installUI(paramJComponent);
/* 144 */     this.isRollover = false;
/* 145 */     this.comboBox.setRequestFocusEnabled(true);
/* 146 */     if ((XPStyle.getXP() != null) && (this.arrowButton != null))
/*     */     {
/* 149 */       this.comboBox.addMouseListener(rolloverListener);
/* 150 */       this.arrowButton.addMouseListener(rolloverListener);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void uninstallUI(JComponent paramJComponent) {
/* 155 */     this.comboBox.removeMouseListener(rolloverListener);
/* 156 */     if (this.arrowButton != null) {
/* 157 */       this.arrowButton.removeMouseListener(rolloverListener);
/*     */     }
/* 159 */     super.uninstallUI(paramJComponent);
/*     */   }
/*     */ 
/*     */   protected void installListeners()
/*     */   {
/* 168 */     super.installListeners();
/* 169 */     XPStyle localXPStyle = XPStyle.getXP();
/*     */ 
/* 171 */     if ((localXPStyle != null) && (localXPStyle.isSkinDefined(this.comboBox, TMSchema.Part.CP_DROPDOWNBUTTONRIGHT)))
/*     */     {
/* 173 */       this.comboBox.addPropertyChangeListener("componentOrientation", componentOrientationListener);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void uninstallListeners()
/*     */   {
/* 184 */     super.uninstallListeners();
/* 185 */     this.comboBox.removePropertyChangeListener("componentOrientation", componentOrientationListener);
/*     */   }
/*     */ 
/*     */   protected void configureEditor()
/*     */   {
/* 194 */     super.configureEditor();
/* 195 */     if (XPStyle.getXP() != null)
/* 196 */       this.editor.addMouseListener(rolloverListener);
/*     */   }
/*     */ 
/*     */   protected void unconfigureEditor()
/*     */   {
/* 205 */     super.unconfigureEditor();
/* 206 */     this.editor.removeMouseListener(rolloverListener);
/*     */   }
/*     */ 
/*     */   public void paint(Graphics paramGraphics, JComponent paramJComponent)
/*     */   {
/* 214 */     if (XPStyle.getXP() != null) {
/* 215 */       paintXPComboBoxBackground(paramGraphics, paramJComponent);
/*     */     }
/* 217 */     super.paint(paramGraphics, paramJComponent);
/*     */   }
/*     */ 
/*     */   TMSchema.State getXPComboBoxState(JComponent paramJComponent) {
/* 221 */     TMSchema.State localState = TMSchema.State.NORMAL;
/* 222 */     if (!paramJComponent.isEnabled())
/* 223 */       localState = TMSchema.State.DISABLED;
/* 224 */     else if (isPopupVisible(this.comboBox))
/* 225 */       localState = TMSchema.State.PRESSED;
/* 226 */     else if (this.isRollover) {
/* 227 */       localState = TMSchema.State.HOT;
/*     */     }
/* 229 */     return localState;
/*     */   }
/*     */ 
/*     */   private void paintXPComboBoxBackground(Graphics paramGraphics, JComponent paramJComponent) {
/* 233 */     XPStyle localXPStyle = XPStyle.getXP();
/* 234 */     TMSchema.State localState = getXPComboBoxState(paramJComponent);
/* 235 */     XPStyle.Skin localSkin = null;
/* 236 */     if ((!this.comboBox.isEditable()) && (localXPStyle.isSkinDefined(paramJComponent, TMSchema.Part.CP_READONLY)))
/*     */     {
/* 238 */       localSkin = localXPStyle.getSkin(paramJComponent, TMSchema.Part.CP_READONLY);
/*     */     }
/* 240 */     if (localSkin == null) {
/* 241 */       localSkin = localXPStyle.getSkin(paramJComponent, TMSchema.Part.CP_COMBOBOX);
/*     */     }
/* 243 */     localSkin.paintSkin(paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight(), localState);
/*     */   }
/*     */ 
/*     */   public void paintCurrentValue(Graphics paramGraphics, Rectangle paramRectangle, boolean paramBoolean)
/*     */   {
/* 257 */     XPStyle localXPStyle = XPStyle.getXP();
/* 258 */     if (localXPStyle != null) {
/* 259 */       paramRectangle.x += 2;
/* 260 */       paramRectangle.y += 2;
/* 261 */       paramRectangle.width -= 4;
/* 262 */       paramRectangle.height -= 4;
/*     */     } else {
/* 264 */       paramRectangle.x += 1;
/* 265 */       paramRectangle.y += 1;
/* 266 */       paramRectangle.width -= 2;
/* 267 */       paramRectangle.height -= 2;
/*     */     }
/* 269 */     if ((!this.comboBox.isEditable()) && (localXPStyle != null) && (localXPStyle.isSkinDefined(this.comboBox, TMSchema.Part.CP_READONLY)))
/*     */     {
/* 276 */       ListCellRenderer localListCellRenderer = this.comboBox.getRenderer();
/*     */       Component localComponent;
/* 278 */       if ((paramBoolean) && (!isPopupVisible(this.comboBox))) {
/* 279 */         localComponent = localListCellRenderer.getListCellRendererComponent(this.listBox, this.comboBox.getSelectedItem(), -1, true, false);
/*     */       }
/*     */       else
/*     */       {
/* 286 */         localComponent = localListCellRenderer.getListCellRendererComponent(this.listBox, this.comboBox.getSelectedItem(), -1, false, false);
/*     */       }
/*     */ 
/* 293 */       localComponent.setFont(this.comboBox.getFont());
/* 294 */       if (this.comboBox.isEnabled()) {
/* 295 */         localComponent.setForeground(this.comboBox.getForeground());
/* 296 */         localComponent.setBackground(this.comboBox.getBackground());
/*     */       } else {
/* 298 */         localComponent.setForeground(DefaultLookup.getColor(this.comboBox, this, "ComboBox.disabledForeground", null));
/*     */ 
/* 300 */         localComponent.setBackground(DefaultLookup.getColor(this.comboBox, this, "ComboBox.disabledBackground", null));
/*     */       }
/*     */ 
/* 303 */       boolean bool = false;
/* 304 */       if ((localComponent instanceof JPanel)) {
/* 305 */         bool = true;
/*     */       }
/* 307 */       this.currentValuePane.paintComponent(paramGraphics, localComponent, this.comboBox, paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height, bool);
/*     */     }
/*     */     else
/*     */     {
/* 311 */       super.paintCurrentValue(paramGraphics, paramRectangle, paramBoolean);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void paintCurrentValueBackground(Graphics paramGraphics, Rectangle paramRectangle, boolean paramBoolean)
/*     */   {
/* 321 */     if (XPStyle.getXP() == null)
/* 322 */       super.paintCurrentValueBackground(paramGraphics, paramRectangle, paramBoolean);
/*     */   }
/*     */ 
/*     */   public Dimension getMinimumSize(JComponent paramJComponent)
/*     */   {
/* 327 */     Dimension localDimension = super.getMinimumSize(paramJComponent);
/* 328 */     if (XPStyle.getXP() != null)
/* 329 */       localDimension.width += 5;
/*     */     else {
/* 331 */       localDimension.width += 4;
/*     */     }
/* 333 */     localDimension.height += 2;
/* 334 */     return localDimension;
/*     */   }
/*     */ 
/*     */   protected LayoutManager createLayoutManager()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: new 131	com/sun/java/swing/plaf/windows/WindowsComboBoxUI$3
/*     */     //   3: dup
/*     */     //   4: aload_0
/*     */     //   5: invokespecial 283	com/sun/java/swing/plaf/windows/WindowsComboBoxUI$3:<init>	(Lcom/sun/java/swing/plaf/windows/WindowsComboBoxUI;)V
/*     */     //   8: areturn
/*     */   }
/*     */ 
/*     */   protected void installKeyboardActions()
/*     */   {
/* 363 */     super.installKeyboardActions();
/*     */   }
/*     */ 
/*     */   protected ComboPopup createPopup() {
/* 367 */     return super.createPopup();
/*     */   }
/*     */ 
/*     */   protected ComboBoxEditor createEditor()
/*     */   {
/* 379 */     return new WindowsComboBoxEditor();
/*     */   }
/*     */ 
/*     */   protected ListCellRenderer createRenderer()
/*     */   {
/* 388 */     XPStyle localXPStyle = XPStyle.getXP();
/* 389 */     if ((localXPStyle != null) && (localXPStyle.isSkinDefined(this.comboBox, TMSchema.Part.CP_READONLY))) {
/* 390 */       return new WindowsComboBoxRenderer(null);
/*     */     }
/* 392 */     return super.createRenderer();
/*     */   }
/*     */ 
/*     */   protected JButton createArrowButton()
/*     */   {
/* 403 */     if (XPStyle.getXP() != null) {
/* 404 */       return new XPComboBoxButton();
/*     */     }
/* 406 */     return super.createArrowButton();
/*     */   }
/*     */ 
/*     */   public static class WindowsComboBoxEditor extends BasicComboBoxEditor.UIResource
/*     */   {
/*     */     protected JTextField createEditorComponent()
/*     */     {
/* 491 */       JTextField localJTextField = super.createEditorComponent();
/* 492 */       Border localBorder = (Border)UIManager.get("ComboBox.editorBorder");
/* 493 */       if (localBorder != null) {
/* 494 */         localJTextField.setBorder(localBorder);
/*     */       }
/* 496 */       localJTextField.setOpaque(false);
/* 497 */       return localJTextField;
/*     */     }
/*     */ 
/*     */     public void setItem(Object paramObject) {
/* 501 */       super.setItem(paramObject);
/* 502 */       if (this.editor.hasFocus())
/* 503 */         this.editor.selectAll();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class WindowsComboBoxRenderer extends BasicComboBoxRenderer.UIResource
/*     */   {
/* 514 */     private static final Object BORDER_KEY = new StringUIClientPropertyKey("BORDER_KEY");
/*     */ 
/* 516 */     private static final Border NULL_BORDER = new EmptyBorder(0, 0, 0, 0);
/*     */ 
/*     */     public Component getListCellRendererComponent(JList paramJList, Object paramObject, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
/*     */     {
/* 527 */       Component localComponent = super.getListCellRendererComponent(paramJList, paramObject, paramInt, paramBoolean1, paramBoolean2);
/*     */ 
/* 530 */       if ((localComponent instanceof JComponent)) {
/* 531 */         JComponent localJComponent = (JComponent)localComponent;
/*     */         Object localObject;
/* 532 */         if ((paramInt == -1) && (paramBoolean1)) {
/* 533 */           localObject = localJComponent.getBorder();
/* 534 */           WindowsBorders.DashedBorder localDashedBorder = new WindowsBorders.DashedBorder(paramJList.getForeground());
/*     */ 
/* 536 */           localJComponent.setBorder(localDashedBorder);
/*     */ 
/* 538 */           if (localJComponent.getClientProperty(BORDER_KEY) == null) {
/* 539 */             localJComponent.putClientProperty(BORDER_KEY, localObject == null ? NULL_BORDER : localObject);
/*     */           }
/*     */ 
/*     */         }
/* 543 */         else if ((localJComponent.getBorder() instanceof WindowsBorders.DashedBorder))
/*     */         {
/* 545 */           localObject = localJComponent.getClientProperty(BORDER_KEY);
/* 546 */           if ((localObject instanceof Border)) {
/* 547 */             localJComponent.setBorder(localObject == NULL_BORDER ? null : (Border)localObject);
/*     */           }
/*     */ 
/* 551 */           localJComponent.putClientProperty(BORDER_KEY, null);
/*     */         }
/*     */ 
/* 554 */         if (paramInt == -1) {
/* 555 */           localJComponent.setOpaque(false);
/* 556 */           localJComponent.setForeground(paramJList.getForeground());
/*     */         } else {
/* 558 */           localJComponent.setOpaque(true);
/*     */         }
/*     */       }
/* 561 */       return localComponent;
/*     */     }
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   protected class WindowsComboPopup extends BasicComboPopup
/*     */   {
/*     */     public WindowsComboPopup(JComboBox arg2)
/*     */     {
/* 465 */       super();
/*     */     }
/*     */ 
/*     */     protected KeyListener createKeyListener() {
/* 469 */       return new InvocationKeyHandler();
/*     */     }
/*     */ 
/*     */     protected class InvocationKeyHandler extends BasicComboPopup.InvocationKeyHandler {
/*     */       protected InvocationKeyHandler() {
/* 474 */         super();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private class XPComboBoxButton extends XPStyle.GlyphButton
/*     */   {
/*     */     public XPComboBoxButton()
/*     */     {
/* 412 */       super(WindowsComboBoxUI.this.comboBox.getComponentOrientation() == ComponentOrientation.RIGHT_TO_LEFT ? TMSchema.Part.CP_DROPDOWNBUTTONLEFT : !XPStyle.getXP().isSkinDefined(WindowsComboBoxUI.this.comboBox, TMSchema.Part.CP_DROPDOWNBUTTONRIGHT) ? TMSchema.Part.CP_DROPDOWNBUTTON : TMSchema.Part.CP_DROPDOWNBUTTONRIGHT);
/*     */ 
/* 419 */       setRequestFocusEnabled(false);
/*     */     }
/*     */ 
/*     */     protected TMSchema.State getState()
/*     */     {
/* 425 */       TMSchema.State localState = super.getState();
/* 426 */       if ((localState != TMSchema.State.DISABLED) && (WindowsComboBoxUI.this.comboBox != null) && (!WindowsComboBoxUI.this.comboBox.isEditable()) && (XPStyle.getXP().isSkinDefined(WindowsComboBoxUI.this.comboBox, TMSchema.Part.CP_DROPDOWNBUTTONRIGHT)))
/*     */       {
/* 434 */         localState = TMSchema.State.NORMAL;
/*     */       }
/* 436 */       return localState;
/*     */     }
/*     */ 
/*     */     public Dimension getPreferredSize() {
/* 440 */       return new Dimension(17, 21);
/*     */     }
/*     */ 
/*     */     void setPart(TMSchema.Part paramPart) {
/* 444 */       setPart(WindowsComboBoxUI.this.comboBox, paramPart);
/*     */     }
/*     */ 
/*     */     WindowsComboBoxUI getWindowsComboBoxUI() {
/* 448 */       return WindowsComboBoxUI.this;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.java.swing.plaf.windows.WindowsComboBoxUI
 * JD-Core Version:    0.6.2
 */