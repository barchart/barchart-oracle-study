/*     */ package com.sun.java.swing.plaf.windows;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.GradientPaint;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Insets;
/*     */ import java.awt.LayoutManager;
/*     */ import java.awt.Paint;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.beans.PropertyChangeEvent;
/*     */ import java.beans.PropertyChangeListener;
/*     */ import java.beans.PropertyVetoException;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.Icon;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JInternalFrame;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JMenuItem;
/*     */ import javax.swing.JPopupMenu;
/*     */ import javax.swing.JSeparator;
/*     */ import javax.swing.LookAndFeel;
/*     */ import javax.swing.UIDefaults.LazyValue;
/*     */ import javax.swing.UIManager;
/*     */ import javax.swing.border.Border;
/*     */ import javax.swing.plaf.UIResource;
/*     */ import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
/*     */ import javax.swing.plaf.basic.BasicInternalFrameTitlePane.PropertyChangeHandler;
/*     */ import javax.swing.plaf.basic.BasicInternalFrameTitlePane.TitlePaneLayout;
/*     */ import sun.swing.SwingUtilities2;
/*     */ 
/*     */ public class WindowsInternalFrameTitlePane extends BasicInternalFrameTitlePane
/*     */ {
/*     */   private Color selectedTitleGradientColor;
/*     */   private Color notSelectedTitleGradientColor;
/*     */   private JPopupMenu systemPopupMenu;
/*     */   private JLabel systemLabel;
/*     */   private Font titleFont;
/*     */   private int titlePaneHeight;
/*     */   private int buttonWidth;
/*     */   private int buttonHeight;
/*     */   private boolean hotTrackingOn;
/*     */ 
/*     */   public WindowsInternalFrameTitlePane(JInternalFrame paramJInternalFrame)
/*     */   {
/*  56 */     super(paramJInternalFrame);
/*     */   }
/*     */ 
/*     */   protected void addSubComponents() {
/*  60 */     add(this.systemLabel);
/*  61 */     add(this.iconButton);
/*  62 */     add(this.maxButton);
/*  63 */     add(this.closeButton);
/*     */   }
/*     */ 
/*     */   protected void installDefaults() {
/*  67 */     super.installDefaults();
/*     */ 
/*  69 */     this.titlePaneHeight = UIManager.getInt("InternalFrame.titlePaneHeight");
/*  70 */     this.buttonWidth = (UIManager.getInt("InternalFrame.titleButtonWidth") - 4);
/*  71 */     this.buttonHeight = (UIManager.getInt("InternalFrame.titleButtonHeight") - 4);
/*     */ 
/*  73 */     Object localObject1 = UIManager.get("InternalFrame.titleButtonToolTipsOn");
/*  74 */     this.hotTrackingOn = ((localObject1 instanceof Boolean) ? ((Boolean)localObject1).booleanValue() : true);
/*     */     Object localObject2;
/*  77 */     if (XPStyle.getXP() != null)
/*     */     {
/*  81 */       this.buttonWidth = this.buttonHeight;
/*  82 */       localObject2 = XPStyle.getPartSize(TMSchema.Part.WP_CLOSEBUTTON, TMSchema.State.NORMAL);
/*  83 */       if ((localObject2 != null) && (((Dimension)localObject2).width != 0) && (((Dimension)localObject2).height != 0))
/*  84 */         this.buttonWidth = ((int)(this.buttonWidth * ((Dimension)localObject2).width / ((Dimension)localObject2).height));
/*     */     }
/*     */     else {
/*  87 */       this.buttonWidth += 2;
/*  88 */       this.selectedTitleGradientColor = UIManager.getColor("InternalFrame.activeTitleGradient");
/*     */ 
/*  90 */       this.notSelectedTitleGradientColor = UIManager.getColor("InternalFrame.inactiveTitleGradient");
/*     */ 
/*  92 */       localObject2 = UIManager.getColor("InternalFrame.activeBorderColor");
/*     */ 
/*  94 */       setBorder(BorderFactory.createLineBorder((Color)localObject2, 1));
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void uninstallListeners()
/*     */   {
/* 100 */     super.uninstallListeners();
/*     */   }
/*     */ 
/*     */   protected void createButtons() {
/* 104 */     super.createButtons();
/* 105 */     if (XPStyle.getXP() != null) {
/* 106 */       this.iconButton.setContentAreaFilled(false);
/* 107 */       this.maxButton.setContentAreaFilled(false);
/* 108 */       this.closeButton.setContentAreaFilled(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void setButtonIcons() {
/* 113 */     super.setButtonIcons();
/*     */ 
/* 115 */     if (!this.hotTrackingOn) {
/* 116 */       this.iconButton.setToolTipText(null);
/* 117 */       this.maxButton.setToolTipText(null);
/* 118 */       this.closeButton.setToolTipText(null);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void paintComponent(Graphics paramGraphics)
/*     */   {
/* 124 */     XPStyle localXPStyle = XPStyle.getXP();
/*     */ 
/* 126 */     paintTitleBackground(paramGraphics);
/*     */ 
/* 128 */     String str1 = this.frame.getTitle();
/* 129 */     if (str1 != null) {
/* 130 */       boolean bool = this.frame.isSelected();
/* 131 */       Font localFont1 = paramGraphics.getFont();
/* 132 */       Font localFont2 = this.titleFont != null ? this.titleFont : getFont();
/* 133 */       paramGraphics.setFont(localFont2);
/*     */ 
/* 136 */       FontMetrics localFontMetrics = SwingUtilities2.getFontMetrics(this.frame, paramGraphics, localFont2);
/* 137 */       int i = (getHeight() + localFontMetrics.getAscent() - localFontMetrics.getLeading() - localFontMetrics.getDescent()) / 2;
/*     */ 
/* 140 */       Rectangle localRectangle = new Rectangle(0, 0, 0, 0);
/* 141 */       if (this.frame.isIconifiable())
/* 142 */         localRectangle = this.iconButton.getBounds();
/* 143 */       else if (this.frame.isMaximizable())
/* 144 */         localRectangle = this.maxButton.getBounds();
/* 145 */       else if (this.frame.isClosable()) {
/* 146 */         localRectangle = this.closeButton.getBounds();
/*     */       }
/*     */ 
/* 151 */       int m = 2;
/*     */       int j;
/*     */       int k;
/* 152 */       if (WindowsGraphicsUtils.isLeftToRight(this.frame)) {
/* 153 */         if (localRectangle.x == 0) {
/* 154 */           localRectangle.x = (this.frame.getWidth() - this.frame.getInsets().right);
/*     */         }
/* 156 */         j = this.systemLabel.getX() + this.systemLabel.getWidth() + m;
/* 157 */         if (localXPStyle != null) {
/* 158 */           j += 2;
/*     */         }
/* 160 */         k = localRectangle.x - j - m;
/*     */       } else {
/* 162 */         if (localRectangle.x == 0) {
/* 163 */           localRectangle.x = this.frame.getInsets().left;
/*     */         }
/* 165 */         k = SwingUtilities2.stringWidth(this.frame, localFontMetrics, str1);
/* 166 */         int n = localRectangle.x + localRectangle.width + m;
/* 167 */         if (localXPStyle != null) {
/* 168 */           n += 2;
/*     */         }
/* 170 */         int i1 = this.systemLabel.getX() - m - n;
/* 171 */         if (i1 > k) {
/* 172 */           j = this.systemLabel.getX() - m - k;
/*     */         } else {
/* 174 */           j = n;
/* 175 */           k = i1;
/*     */         }
/*     */       }
/* 178 */       str1 = getTitle(this.frame.getTitle(), localFontMetrics, k);
/*     */ 
/* 180 */       if (localXPStyle != null) {
/* 181 */         String str2 = null;
/* 182 */         if (bool) {
/* 183 */           str2 = localXPStyle.getString(this, TMSchema.Part.WP_CAPTION, TMSchema.State.ACTIVE, TMSchema.Prop.TEXTSHADOWTYPE);
/*     */         }
/*     */ 
/* 186 */         if ("single".equalsIgnoreCase(str2)) {
/* 187 */           Point localPoint = localXPStyle.getPoint(this, TMSchema.Part.WP_WINDOW, TMSchema.State.ACTIVE, TMSchema.Prop.TEXTSHADOWOFFSET);
/*     */ 
/* 189 */           Color localColor = localXPStyle.getColor(this, TMSchema.Part.WP_WINDOW, TMSchema.State.ACTIVE, TMSchema.Prop.TEXTSHADOWCOLOR, null);
/*     */ 
/* 191 */           if ((localPoint != null) && (localColor != null)) {
/* 192 */             paramGraphics.setColor(localColor);
/* 193 */             SwingUtilities2.drawString(this.frame, paramGraphics, str1, j + localPoint.x, i + localPoint.y);
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 199 */       paramGraphics.setColor(bool ? this.selectedTextColor : this.notSelectedTextColor);
/* 200 */       SwingUtilities2.drawString(this.frame, paramGraphics, str1, j, i);
/* 201 */       paramGraphics.setFont(localFont1);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Dimension getPreferredSize() {
/* 206 */     return getMinimumSize();
/*     */   }
/*     */ 
/*     */   public Dimension getMinimumSize() {
/* 210 */     Dimension localDimension = new Dimension(super.getMinimumSize());
/* 211 */     localDimension.height = (this.titlePaneHeight + 2);
/*     */ 
/* 213 */     XPStyle localXPStyle = XPStyle.getXP();
/* 214 */     if (localXPStyle != null)
/*     */     {
/* 217 */       if (this.frame.isMaximum())
/* 218 */         localDimension.height -= 1;
/*     */       else {
/* 220 */         localDimension.height += 3;
/*     */       }
/*     */     }
/* 223 */     return localDimension;
/*     */   }
/*     */ 
/*     */   protected void paintTitleBackground(Graphics paramGraphics) {
/* 227 */     XPStyle localXPStyle = XPStyle.getXP();
/*     */     Object localObject1;
/*     */     Object localObject2;
/*     */     Object localObject3;
/* 228 */     if (localXPStyle != null) {
/* 229 */       localObject1 = this.frame.isMaximum() ? TMSchema.Part.WP_MAXCAPTION : this.frame.isIcon() ? TMSchema.Part.WP_MINCAPTION : TMSchema.Part.WP_CAPTION;
/*     */ 
/* 232 */       localObject2 = this.frame.isSelected() ? TMSchema.State.ACTIVE : TMSchema.State.INACTIVE;
/* 233 */       localObject3 = localXPStyle.getSkin(this, (TMSchema.Part)localObject1);
/* 234 */       ((XPStyle.Skin)localObject3).paintSkin(paramGraphics, 0, 0, getWidth(), getHeight(), (TMSchema.State)localObject2);
/*     */     } else {
/* 236 */       localObject1 = (Boolean)LookAndFeel.getDesktopPropertyValue("win.frame.captionGradientsOn", Boolean.valueOf(false));
/*     */ 
/* 238 */       if ((((Boolean)localObject1).booleanValue()) && ((paramGraphics instanceof Graphics2D))) {
/* 239 */         localObject2 = (Graphics2D)paramGraphics;
/* 240 */         localObject3 = ((Graphics2D)localObject2).getPaint();
/*     */ 
/* 242 */         boolean bool = this.frame.isSelected();
/* 243 */         int i = getWidth();
/*     */         GradientPaint localGradientPaint;
/* 245 */         if (bool) {
/* 246 */           localGradientPaint = new GradientPaint(0.0F, 0.0F, this.selectedTitleColor, (int)(i * 0.75D), 0.0F, this.selectedTitleGradientColor);
/*     */ 
/* 250 */           ((Graphics2D)localObject2).setPaint(localGradientPaint);
/*     */         } else {
/* 252 */           localGradientPaint = new GradientPaint(0.0F, 0.0F, this.notSelectedTitleColor, (int)(i * 0.75D), 0.0F, this.notSelectedTitleGradientColor);
/*     */ 
/* 256 */           ((Graphics2D)localObject2).setPaint(localGradientPaint);
/*     */         }
/* 258 */         ((Graphics2D)localObject2).fillRect(0, 0, getWidth(), getHeight());
/* 259 */         ((Graphics2D)localObject2).setPaint((Paint)localObject3);
/*     */       } else {
/* 261 */         super.paintTitleBackground(paramGraphics);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void assembleSystemMenu() {
/* 267 */     this.systemPopupMenu = new JPopupMenu();
/* 268 */     addSystemMenuItems(this.systemPopupMenu);
/* 269 */     enableActions();
/* 270 */     this.systemLabel = new JLabel(this.frame.getFrameIcon()) {
/*     */       protected void paintComponent(Graphics paramAnonymousGraphics) {
/* 272 */         int i = 0;
/* 273 */         int j = 0;
/* 274 */         int k = getWidth();
/* 275 */         int m = getHeight();
/* 276 */         paramAnonymousGraphics = paramAnonymousGraphics.create();
/* 277 */         if (isOpaque()) {
/* 278 */           paramAnonymousGraphics.setColor(getBackground());
/* 279 */           paramAnonymousGraphics.fillRect(0, 0, k, m);
/*     */         }
/* 281 */         Icon localIcon = getIcon();
/*     */         int n;
/*     */         int i1;
/* 284 */         if ((localIcon != null) && ((n = localIcon.getIconWidth()) > 0) && ((i1 = localIcon.getIconHeight()) > 0))
/*     */         {
/*     */           double d;
/* 290 */           if (n > i1)
/*     */           {
/* 292 */             j = (m - k * i1 / n) / 2;
/* 293 */             d = k / n;
/*     */           }
/*     */           else {
/* 296 */             i = (k - m * n / i1) / 2;
/* 297 */             d = m / i1;
/*     */           }
/* 299 */           ((Graphics2D)paramAnonymousGraphics).translate(i, j);
/* 300 */           ((Graphics2D)paramAnonymousGraphics).scale(d, d);
/* 301 */           localIcon.paintIcon(this, paramAnonymousGraphics, 0, 0);
/*     */         }
/* 303 */         paramAnonymousGraphics.dispose();
/*     */       }
/*     */     };
/* 306 */     this.systemLabel.addMouseListener(new MouseAdapter() {
/*     */       public void mouseClicked(MouseEvent paramAnonymousMouseEvent) {
/* 308 */         if ((paramAnonymousMouseEvent.getClickCount() == 2) && (WindowsInternalFrameTitlePane.this.frame.isClosable()) && (!WindowsInternalFrameTitlePane.this.frame.isIcon()))
/*     */         {
/* 310 */           WindowsInternalFrameTitlePane.this.systemPopupMenu.setVisible(false);
/* 311 */           WindowsInternalFrameTitlePane.this.frame.doDefaultCloseAction();
/*     */         }
/*     */         else {
/* 314 */           super.mouseClicked(paramAnonymousMouseEvent);
/*     */         }
/*     */       }
/*     */ 
/*     */       public void mousePressed(MouseEvent paramAnonymousMouseEvent) {
/*     */         try { WindowsInternalFrameTitlePane.this.frame.setSelected(true);
/*     */         } catch (PropertyVetoException localPropertyVetoException) {
/*     */         }
/* 322 */         WindowsInternalFrameTitlePane.this.showSystemPopupMenu(paramAnonymousMouseEvent.getComponent());
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   protected void addSystemMenuItems(JPopupMenu paramJPopupMenu) {
/* 328 */     JMenuItem localJMenuItem = paramJPopupMenu.add(this.restoreAction);
/* 329 */     localJMenuItem.setMnemonic('R');
/* 330 */     localJMenuItem = paramJPopupMenu.add(this.moveAction);
/* 331 */     localJMenuItem.setMnemonic('M');
/* 332 */     localJMenuItem = paramJPopupMenu.add(this.sizeAction);
/* 333 */     localJMenuItem.setMnemonic('S');
/* 334 */     localJMenuItem = paramJPopupMenu.add(this.iconifyAction);
/* 335 */     localJMenuItem.setMnemonic('n');
/* 336 */     localJMenuItem = paramJPopupMenu.add(this.maximizeAction);
/* 337 */     localJMenuItem.setMnemonic('x');
/* 338 */     this.systemPopupMenu.add(new JSeparator());
/* 339 */     localJMenuItem = paramJPopupMenu.add(this.closeAction);
/* 340 */     localJMenuItem.setMnemonic('C');
/*     */   }
/*     */ 
/*     */   protected void showSystemMenu() {
/* 344 */     showSystemPopupMenu(this.systemLabel);
/*     */   }
/*     */ 
/*     */   private void showSystemPopupMenu(Component paramComponent) {
/* 348 */     Dimension localDimension = new Dimension();
/* 349 */     Border localBorder = this.frame.getBorder();
/* 350 */     if (localBorder != null) {
/* 351 */       localDimension.width += localBorder.getBorderInsets(this.frame).left + localBorder.getBorderInsets(this.frame).right;
/*     */ 
/* 353 */       localDimension.height += localBorder.getBorderInsets(this.frame).bottom + localBorder.getBorderInsets(this.frame).top;
/*     */     }
/*     */ 
/* 356 */     if (!this.frame.isIcon()) {
/* 357 */       this.systemPopupMenu.show(paramComponent, getX() - localDimension.width, getY() + getHeight() - localDimension.height);
/*     */     }
/*     */     else
/*     */     {
/* 361 */       this.systemPopupMenu.show(paramComponent, getX() - localDimension.width, getY() - this.systemPopupMenu.getPreferredSize().height - localDimension.height);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected PropertyChangeListener createPropertyChangeListener()
/*     */   {
/* 369 */     return new WindowsPropertyChangeHandler();
/*     */   }
/*     */ 
/*     */   protected LayoutManager createLayout() {
/* 373 */     return new WindowsTitlePaneLayout();
/*     */   }
/*     */ 
/*     */   public static class ScalableIconUIResource
/*     */     implements Icon, UIResource
/*     */   {
/*     */     private static final int SIZE = 16;
/*     */     private Icon[] icons;
/*     */ 
/*     */     public ScalableIconUIResource(Object[] paramArrayOfObject)
/*     */     {
/* 498 */       this.icons = new UIResource[paramArrayOfObject.length];
/*     */ 
/* 500 */       for (int i = 0; i < paramArrayOfObject.length; i++)
/* 501 */         if ((paramArrayOfObject[i] instanceof UIDefaults.LazyValue))
/* 502 */           this.icons[i] = ((UIResource)((UIDefaults.LazyValue)paramArrayOfObject[i]).createValue(null));
/*     */         else
/* 504 */           this.icons[i] = ((UIResource)paramArrayOfObject[i]);
/*     */     }
/*     */ 
/*     */     protected Icon getBestIcon(int paramInt)
/*     */     {
/* 513 */       if ((this.icons != null) && (this.icons.length > 0)) {
/* 514 */         int i = 0;
/* 515 */         int j = 2147483647;
/* 516 */         for (int k = 0; k < this.icons.length; k++) {
/* 517 */           Icon localIcon = this.icons[k];
/*     */           int m;
/* 519 */           if ((localIcon != null) && ((m = localIcon.getIconWidth()) > 0)) {
/* 520 */             int n = Math.abs(m - paramInt);
/* 521 */             if (n < j) {
/* 522 */               j = n;
/* 523 */               i = k;
/*     */             }
/*     */           }
/*     */         }
/* 527 */         return this.icons[i];
/*     */       }
/* 529 */       return null;
/*     */     }
/*     */ 
/*     */     public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
/*     */     {
/* 534 */       Graphics2D localGraphics2D = (Graphics2D)paramGraphics.create();
/*     */ 
/* 537 */       int i = getIconWidth();
/* 538 */       double d1 = localGraphics2D.getTransform().getScaleX();
/* 539 */       Icon localIcon = getBestIcon((int)(i * d1));
/*     */       int j;
/* 541 */       if ((localIcon != null) && ((j = localIcon.getIconWidth()) > 0))
/*     */       {
/* 543 */         double d2 = i / j;
/* 544 */         localGraphics2D.translate(paramInt1, paramInt2);
/* 545 */         localGraphics2D.scale(d2, d2);
/* 546 */         localIcon.paintIcon(paramComponent, localGraphics2D, 0, 0);
/*     */       }
/* 548 */       localGraphics2D.dispose();
/*     */     }
/*     */ 
/*     */     public int getIconWidth() {
/* 552 */       return 16;
/*     */     }
/*     */ 
/*     */     public int getIconHeight() {
/* 556 */       return 16;
/*     */     }
/*     */   }
/*     */ 
/*     */   public class WindowsPropertyChangeHandler extends BasicInternalFrameTitlePane.PropertyChangeHandler
/*     */   {
/*     */     public WindowsPropertyChangeHandler()
/*     */     {
/* 463 */       super();
/*     */     }
/* 465 */     public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) { String str = paramPropertyChangeEvent.getPropertyName();
/*     */ 
/* 468 */       if (("frameIcon".equals(str)) && (WindowsInternalFrameTitlePane.this.systemLabel != null))
/*     */       {
/* 470 */         WindowsInternalFrameTitlePane.this.systemLabel.setIcon(WindowsInternalFrameTitlePane.this.frame.getFrameIcon());
/*     */       }
/*     */ 
/* 473 */       super.propertyChange(paramPropertyChangeEvent);
/*     */     }
/*     */   }
/*     */ 
/*     */   public class WindowsTitlePaneLayout extends BasicInternalFrameTitlePane.TitlePaneLayout
/*     */   {
/* 377 */     private Insets captionMargin = null;
/* 378 */     private Insets contentMargin = null;
/* 379 */     private XPStyle xp = XPStyle.getXP();
/*     */ 
/* 381 */     WindowsTitlePaneLayout() { super();
/* 382 */       if (this.xp != null) {
/* 383 */         WindowsInternalFrameTitlePane localWindowsInternalFrameTitlePane = WindowsInternalFrameTitlePane.this;
/* 384 */         this.captionMargin = this.xp.getMargin(localWindowsInternalFrameTitlePane, TMSchema.Part.WP_CAPTION, null, TMSchema.Prop.CAPTIONMARGINS);
/* 385 */         this.contentMargin = this.xp.getMargin(localWindowsInternalFrameTitlePane, TMSchema.Part.WP_CAPTION, null, TMSchema.Prop.CONTENTMARGINS);
/*     */       }
/* 387 */       if (this.captionMargin == null) {
/* 388 */         this.captionMargin = new Insets(0, 2, 0, 2);
/*     */       }
/* 390 */       if (this.contentMargin == null)
/* 391 */         this.contentMargin = new Insets(0, 0, 0, 0);
/*     */     }
/*     */ 
/*     */     private int layoutButton(JComponent paramJComponent, TMSchema.Part paramPart, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean)
/*     */     {
/* 398 */       if (!paramBoolean) {
/* 399 */         paramInt1 -= paramInt3;
/*     */       }
/* 401 */       paramJComponent.setBounds(paramInt1, paramInt2, paramInt3, paramInt4);
/* 402 */       if (paramBoolean)
/* 403 */         paramInt1 += paramInt3 + 2;
/*     */       else {
/* 405 */         paramInt1 -= 2;
/*     */       }
/* 407 */       return paramInt1;
/*     */     }
/*     */ 
/*     */     public void layoutContainer(Container paramContainer) {
/* 411 */       boolean bool = WindowsGraphicsUtils.isLeftToRight(WindowsInternalFrameTitlePane.this.frame);
/*     */ 
/* 413 */       int k = WindowsInternalFrameTitlePane.this.getWidth();
/* 414 */       int m = WindowsInternalFrameTitlePane.this.getHeight();
/*     */ 
/* 418 */       int n = this.xp != null ? (m - 2) * 6 / 10 : m - 4;
/*     */       int i;
/* 419 */       if (this.xp != null)
/* 420 */         i = bool ? this.captionMargin.left + 2 : k - this.captionMargin.right - 2;
/*     */       else {
/* 422 */         i = bool ? this.captionMargin.left : k - this.captionMargin.right;
/*     */       }
/* 424 */       int j = (m - n) / 2;
/* 425 */       layoutButton(WindowsInternalFrameTitlePane.this.systemLabel, TMSchema.Part.WP_SYSBUTTON, i, j, n, n, 0, bool);
/*     */ 
/* 430 */       if (this.xp != null) {
/* 431 */         i = bool ? k - this.captionMargin.right - 2 : this.captionMargin.left + 2;
/* 432 */         j = 1;
/* 433 */         if (WindowsInternalFrameTitlePane.this.frame.isMaximum())
/* 434 */           j++;
/*     */         else
/* 436 */           j += 5;
/*     */       }
/*     */       else {
/* 439 */         i = bool ? k - this.captionMargin.right : this.captionMargin.left;
/* 440 */         j = (m - WindowsInternalFrameTitlePane.this.buttonHeight) / 2;
/*     */       }
/*     */ 
/* 443 */       if (WindowsInternalFrameTitlePane.this.frame.isClosable()) {
/* 444 */         i = layoutButton(WindowsInternalFrameTitlePane.this.closeButton, TMSchema.Part.WP_CLOSEBUTTON, i, j, WindowsInternalFrameTitlePane.this.buttonWidth, WindowsInternalFrameTitlePane.this.buttonHeight, 2, !bool);
/*     */       }
/*     */ 
/* 449 */       if (WindowsInternalFrameTitlePane.this.frame.isMaximizable()) {
/* 450 */         i = layoutButton(WindowsInternalFrameTitlePane.this.maxButton, TMSchema.Part.WP_MAXBUTTON, i, j, WindowsInternalFrameTitlePane.this.buttonWidth, WindowsInternalFrameTitlePane.this.buttonHeight, this.xp != null ? 2 : 0, !bool);
/*     */       }
/*     */ 
/* 455 */       if (WindowsInternalFrameTitlePane.this.frame.isIconifiable())
/* 456 */         layoutButton(WindowsInternalFrameTitlePane.this.iconButton, TMSchema.Part.WP_MINBUTTON, i, j, WindowsInternalFrameTitlePane.this.buttonWidth, WindowsInternalFrameTitlePane.this.buttonHeight, 0, !bool);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.java.swing.plaf.windows.WindowsInternalFrameTitlePane
 * JD-Core Version:    0.6.2
 */