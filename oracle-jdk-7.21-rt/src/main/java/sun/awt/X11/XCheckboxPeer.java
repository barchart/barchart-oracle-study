/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.Checkbox;
/*     */ import java.awt.CheckboxGroup;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Insets;
/*     */ import java.awt.Polygon;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.Shape;
/*     */ import java.awt.SystemColor;
/*     */ import java.awt.event.FocusEvent;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.peer.CheckboxPeer;
/*     */ import javax.swing.plaf.basic.BasicGraphicsUtils;
/*     */ import sun.awt.X11GraphicsConfig;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ class XCheckboxPeer extends XComponentPeer
/*     */   implements CheckboxPeer
/*     */ {
/*  39 */   private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.X11.XCheckboxPeer");
/*     */ 
/*  41 */   private static final Insets focusInsets = new Insets(0, 0, 0, 0);
/*  42 */   private static final Insets borderInsets = new Insets(2, 2, 2, 2);
/*     */   private static final int checkBoxInsetFromText = 2;
/*     */   private static final double MASTER_SIZE = 128.0D;
/*  49 */   private static final Polygon MASTER_CHECKMARK = new Polygon(new int[] { 1, 25, 56, 124, 124, 85, 64 }, new int[] { 59, 35, 67, 0, 12, 66, 123 }, 7);
/*     */   private Shape myCheckMark;
/*  56 */   private Color focusColor = SystemColor.windowText;
/*     */   private boolean pressed;
/*     */   private boolean armed;
/*     */   private boolean selected;
/*     */   private Rectangle textRect;
/*     */   private Rectangle focusRect;
/*     */   private int checkBoxSize;
/*     */   private int cbX;
/*     */   private int cbY;
/*     */   String label;
/*     */   CheckboxGroup checkBoxGroup;
/*     */ 
/*     */   XCheckboxPeer(Checkbox paramCheckbox)
/*     */   {
/*  72 */     super(paramCheckbox);
/*  73 */     this.pressed = false;
/*  74 */     this.armed = false;
/*  75 */     this.selected = paramCheckbox.getState();
/*  76 */     this.label = paramCheckbox.getLabel();
/*  77 */     if (this.label == null) {
/*  78 */       this.label = "";
/*     */     }
/*  80 */     this.checkBoxGroup = paramCheckbox.getCheckboxGroup();
/*  81 */     updateMotifColors(getPeerBackground());
/*     */   }
/*     */ 
/*     */   public void preInit(XCreateWindowParams paramXCreateWindowParams)
/*     */   {
/*  87 */     this.textRect = new Rectangle();
/*  88 */     this.focusRect = new Rectangle();
/*  89 */     super.preInit(paramXCreateWindowParams);
/*     */   }
/*     */   public boolean isFocusable() {
/*  92 */     return true;
/*     */   }
/*     */ 
/*     */   public void focusGained(FocusEvent paramFocusEvent) {
/*  96 */     super.focusGained(paramFocusEvent);
/*  97 */     repaint();
/*     */   }
/*     */ 
/*     */   public void focusLost(FocusEvent paramFocusEvent)
/*     */   {
/* 102 */     super.focusLost(paramFocusEvent);
/* 103 */     repaint();
/*     */   }
/*     */ 
/*     */   void handleJavaKeyEvent(KeyEvent paramKeyEvent)
/*     */   {
/* 108 */     int i = paramKeyEvent.getID();
/* 109 */     switch (i) {
/*     */     case 401:
/* 111 */       keyPressed(paramKeyEvent);
/* 112 */       break;
/*     */     case 402:
/* 114 */       keyReleased(paramKeyEvent);
/* 115 */       break;
/*     */     case 400:
/* 117 */       keyTyped(paramKeyEvent);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void keyTyped(KeyEvent paramKeyEvent) {
/*     */   }
/*     */ 
/*     */   public void keyPressed(KeyEvent paramKeyEvent) {
/* 125 */     if (paramKeyEvent.getKeyCode() == 32)
/*     */     {
/* 130 */       action(!this.selected);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void keyReleased(KeyEvent paramKeyEvent)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setLabel(String paramString) {
/* 139 */     if (paramString == null)
/* 140 */       this.label = "";
/*     */     else {
/* 142 */       this.label = paramString;
/*     */     }
/* 144 */     layout();
/* 145 */     repaint();
/*     */   }
/*     */ 
/*     */   void handleJavaMouseEvent(MouseEvent paramMouseEvent) {
/* 149 */     super.handleJavaMouseEvent(paramMouseEvent);
/* 150 */     int i = paramMouseEvent.getID();
/* 151 */     switch (i) {
/*     */     case 501:
/* 153 */       mousePressed(paramMouseEvent);
/* 154 */       break;
/*     */     case 502:
/* 156 */       mouseReleased(paramMouseEvent);
/* 157 */       break;
/*     */     case 504:
/* 159 */       mouseEntered(paramMouseEvent);
/* 160 */       break;
/*     */     case 505:
/* 162 */       mouseExited(paramMouseEvent);
/* 163 */       break;
/*     */     case 500:
/* 165 */       mouseClicked(paramMouseEvent);
/*     */     case 503:
/*     */     }
/*     */   }
/*     */ 
/*     */   public void mousePressed(MouseEvent paramMouseEvent) {
/* 171 */     if (XToolkit.isLeftMouseButton(paramMouseEvent)) {
/* 172 */       Checkbox localCheckbox = (Checkbox)paramMouseEvent.getSource();
/*     */ 
/* 174 */       if (localCheckbox.contains(paramMouseEvent.getX(), paramMouseEvent.getY())) {
/* 175 */         if (log.isLoggable(400)) {
/* 176 */           log.finer("mousePressed() on " + this.target.getName() + " : armed = " + this.armed + ", pressed = " + this.pressed + ", selected = " + this.selected + ", enabled = " + isEnabled());
/*     */         }
/*     */ 
/* 179 */         if (!isEnabled())
/*     */         {
/* 181 */           return;
/*     */         }
/* 183 */         if (!this.armed) {
/* 184 */           this.armed = true;
/*     */         }
/* 186 */         this.pressed = true;
/* 187 */         repaint();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void mouseReleased(MouseEvent paramMouseEvent) {
/* 193 */     if (log.isLoggable(400)) {
/* 194 */       log.finer("mouseReleased() on " + this.target.getName() + ": armed = " + this.armed + ", pressed = " + this.pressed + ", selected = " + this.selected + ", enabled = " + isEnabled());
/*     */     }
/*     */ 
/* 197 */     int i = 0;
/* 198 */     if (XToolkit.isLeftMouseButton(paramMouseEvent))
/*     */     {
/* 200 */       if (this.armed)
/*     */       {
/* 204 */         i = 1;
/*     */       }
/* 206 */       this.pressed = false;
/* 207 */       this.armed = false;
/* 208 */       if (i != 0) {
/* 209 */         action(!this.selected);
/*     */       }
/*     */       else
/* 212 */         repaint();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void mouseEntered(MouseEvent paramMouseEvent)
/*     */   {
/* 218 */     if (log.isLoggable(400)) {
/* 219 */       log.finer("mouseEntered() on " + this.target.getName() + ": armed = " + this.armed + ", pressed = " + this.pressed + ", selected = " + this.selected + ", enabled = " + isEnabled());
/*     */     }
/*     */ 
/* 222 */     if (this.pressed) {
/* 223 */       this.armed = true;
/* 224 */       repaint();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void mouseExited(MouseEvent paramMouseEvent) {
/* 229 */     if (log.isLoggable(400)) {
/* 230 */       log.finer("mouseExited() on " + this.target.getName() + ": armed = " + this.armed + ", pressed = " + this.pressed + ", selected = " + this.selected + ", enabled = " + isEnabled());
/*     */     }
/*     */ 
/* 233 */     if (this.armed) {
/* 234 */       this.armed = false;
/* 235 */       repaint();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void mouseClicked(MouseEvent paramMouseEvent)
/*     */   {
/*     */   }
/*     */ 
/*     */   public Dimension getMinimumSize()
/*     */   {
/* 249 */     FontMetrics localFontMetrics = getFontMetrics(getPeerFont());
/*     */ 
/* 251 */     int i = localFontMetrics.stringWidth(this.label) + getCheckboxSize(localFontMetrics) + 4 + 8;
/* 252 */     int j = Math.max(localFontMetrics.getHeight() + 8, 15);
/*     */ 
/* 254 */     return new Dimension(i, j);
/*     */   }
/*     */ 
/*     */   private int getCheckboxSize(FontMetrics paramFontMetrics)
/*     */   {
/* 260 */     return paramFontMetrics.getHeight() * 76 / 100 - 1;
/*     */   }
/*     */ 
/*     */   public void setBackground(Color paramColor) {
/* 264 */     updateMotifColors(paramColor);
/* 265 */     super.setBackground(paramColor);
/*     */   }
/*     */ 
/*     */   public void layout()
/*     */   {
/* 272 */     Dimension localDimension = getPeerSize();
/* 273 */     Font localFont = getPeerFont();
/* 274 */     FontMetrics localFontMetrics = getFontMetrics(localFont);
/* 275 */     String str = this.label;
/*     */ 
/* 277 */     this.checkBoxSize = getCheckboxSize(localFontMetrics);
/*     */ 
/* 281 */     this.cbX = (borderInsets.left + 2);
/* 282 */     this.cbY = (localDimension.height / 2 - this.checkBoxSize / 2);
/* 283 */     int i = borderInsets.left + 4 + this.checkBoxSize;
/*     */ 
/* 287 */     this.textRect.width = localFontMetrics.stringWidth(str == null ? "" : str);
/* 288 */     this.textRect.height = localFontMetrics.getHeight();
/*     */ 
/* 290 */     this.textRect.x = Math.max(i, localDimension.width / 2 - this.textRect.width / 2);
/* 291 */     this.textRect.y = ((localDimension.height - this.textRect.height) / 2);
/*     */ 
/* 293 */     this.focusRect.x = focusInsets.left;
/* 294 */     this.focusRect.y = focusInsets.top;
/* 295 */     this.focusRect.width = (localDimension.width - (focusInsets.left + focusInsets.right) - 1);
/* 296 */     this.focusRect.height = (localDimension.height - (focusInsets.top + focusInsets.bottom) - 1);
/*     */ 
/* 298 */     double d = this.checkBoxSize;
/* 299 */     this.myCheckMark = AffineTransform.getScaleInstance(d / 128.0D, d / 128.0D).createTransformedShape(MASTER_CHECKMARK);
/*     */   }
/*     */ 
/*     */   public void paint(Graphics paramGraphics)
/*     */   {
/* 304 */     if (paramGraphics != null)
/*     */     {
/* 306 */       Dimension localDimension = getPeerSize();
/* 307 */       Font localFont = getPeerFont();
/*     */ 
/* 309 */       flush();
/* 310 */       paramGraphics.setColor(getPeerBackground());
/* 311 */       paramGraphics.fillRect(0, 0, localDimension.width, localDimension.height);
/*     */ 
/* 313 */       if (this.label != null) {
/* 314 */         paramGraphics.setFont(localFont);
/* 315 */         paintText(paramGraphics, this.textRect, this.label);
/*     */       }
/*     */ 
/* 318 */       if (hasFocus()) {
/* 319 */         paintFocus(paramGraphics, this.focusRect.x, this.focusRect.y, this.focusRect.width, this.focusRect.height);
/*     */       }
/*     */ 
/* 327 */       if (this.checkBoxGroup == null) {
/* 328 */         paintCheckbox(paramGraphics, this.cbX, this.cbY, this.checkBoxSize, this.checkBoxSize);
/*     */       }
/*     */       else {
/* 331 */         paintRadioButton(paramGraphics, this.cbX, this.cbY, this.checkBoxSize, this.checkBoxSize);
/*     */       }
/*     */     }
/*     */ 
/* 335 */     flush();
/*     */   }
/*     */ 
/*     */   public void paintCheckbox(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/* 341 */     int i = 0;
/* 342 */     BufferedImage localBufferedImage = null;
/* 343 */     Graphics2D localGraphics2D = null;
/* 344 */     int j = paramInt1;
/* 345 */     int k = paramInt2;
/* 346 */     if (!(paramGraphics instanceof Graphics2D))
/*     */     {
/* 351 */       localBufferedImage = this.graphicsConfig.createCompatibleImage(paramInt3, paramInt4);
/* 352 */       localGraphics2D = localBufferedImage.createGraphics();
/* 353 */       i = 1;
/* 354 */       j = 0;
/* 355 */       k = 0;
/*     */     }
/*     */     else {
/* 358 */       localGraphics2D = (Graphics2D)paramGraphics;
/*     */     }
/*     */     try {
/* 361 */       drawMotif3DRect(localGraphics2D, j, k, paramInt3 - 1, paramInt4 - 1, this.armed | this.selected);
/*     */ 
/* 364 */       localGraphics2D.setColor((this.armed | this.selected) ? this.selectColor : getPeerBackground());
/* 365 */       localGraphics2D.fillRect(j + 1, k + 1, paramInt3 - 2, paramInt4 - 2);
/*     */ 
/* 367 */       if ((this.armed | this.selected))
/*     */       {
/* 371 */         localGraphics2D.setColor(getPeerForeground());
/*     */ 
/* 373 */         AffineTransform localAffineTransform = localGraphics2D.getTransform();
/* 374 */         localGraphics2D.setTransform(AffineTransform.getTranslateInstance(j, k));
/* 375 */         localGraphics2D.fill(this.myCheckMark);
/* 376 */         localGraphics2D.setTransform(localAffineTransform);
/*     */       }
/*     */     } finally {
/* 379 */       if (i != 0) {
/* 380 */         localGraphics2D.dispose();
/*     */       }
/*     */     }
/* 383 */     if (i != 0)
/* 384 */       paramGraphics.drawImage(localBufferedImage, paramInt1, paramInt2, null);
/*     */   }
/*     */ 
/*     */   public void setFont(Font paramFont) {
/* 388 */     super.setFont(paramFont);
/* 389 */     this.target.repaint();
/*     */   }
/*     */ 
/*     */   public void paintRadioButton(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/* 394 */     paramGraphics.setColor((this.armed | this.selected) ? this.darkShadow : this.lightShadow);
/* 395 */     paramGraphics.drawArc(paramInt1 - 1, paramInt2 - 1, paramInt3 + 2, paramInt4 + 2, 45, 180);
/*     */ 
/* 397 */     paramGraphics.setColor((this.armed | this.selected) ? this.lightShadow : this.darkShadow);
/* 398 */     paramGraphics.drawArc(paramInt1 - 1, paramInt2 - 1, paramInt3 + 2, paramInt4 + 2, 45, -180);
/*     */ 
/* 400 */     if ((this.armed | this.selected)) {
/* 401 */       paramGraphics.setColor(this.selectColor);
/* 402 */       paramGraphics.fillArc(paramInt1 + 1, paramInt2 + 1, paramInt3 - 1, paramInt4 - 1, 0, 360);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void paintText(Graphics paramGraphics, Rectangle paramRectangle, String paramString) {
/* 407 */     FontMetrics localFontMetrics = paramGraphics.getFontMetrics();
/*     */ 
/* 409 */     int i = -1;
/*     */ 
/* 411 */     if (isEnabled())
/*     */     {
/* 413 */       paramGraphics.setColor(getPeerForeground());
/* 414 */       BasicGraphicsUtils.drawStringUnderlineCharAt(paramGraphics, paramString, i, paramRectangle.x, paramRectangle.y + localFontMetrics.getAscent());
/*     */     }
/*     */     else
/*     */     {
/* 418 */       paramGraphics.setColor(getPeerBackground().brighter());
/*     */ 
/* 420 */       BasicGraphicsUtils.drawStringUnderlineCharAt(paramGraphics, paramString, i, paramRectangle.x, paramRectangle.y + localFontMetrics.getAscent());
/*     */ 
/* 422 */       paramGraphics.setColor(getPeerBackground().darker());
/* 423 */       BasicGraphicsUtils.drawStringUnderlineCharAt(paramGraphics, paramString, i, paramRectangle.x - 1, paramRectangle.y + localFontMetrics.getAscent() - 1);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void paintFocus(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/* 430 */     paramGraphics.setColor(this.focusColor);
/* 431 */     paramGraphics.drawRect(paramInt1, paramInt2, paramInt3, paramInt4);
/*     */   }
/*     */ 
/*     */   public void setState(boolean paramBoolean) {
/* 435 */     if (this.selected != paramBoolean) {
/* 436 */       this.selected = paramBoolean;
/* 437 */       repaint();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setCheckboxGroup(CheckboxGroup paramCheckboxGroup) {
/* 442 */     this.checkBoxGroup = paramCheckboxGroup;
/* 443 */     repaint();
/*     */   }
/*     */ 
/*     */   void action(boolean paramBoolean)
/*     */   {
/* 450 */     final Checkbox localCheckbox = (Checkbox)this.target;
/* 451 */     final boolean bool = paramBoolean;
/* 452 */     XToolkit.executeOnEventHandlerThread(localCheckbox, new Runnable() {
/*     */       public void run() {
/* 454 */         CheckboxGroup localCheckboxGroup = XCheckboxPeer.this.checkBoxGroup;
/*     */ 
/* 459 */         if ((localCheckboxGroup != null) && (localCheckboxGroup.getSelectedCheckbox() == localCheckbox) && (localCheckbox.getState()))
/*     */         {
/* 462 */           localCheckbox.setState(true);
/* 463 */           return;
/*     */         }
/*     */ 
/* 466 */         localCheckbox.setState(bool);
/* 467 */         XCheckboxPeer.this.notifyStateChanged(bool);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   void notifyStateChanged(boolean paramBoolean) {
/* 473 */     Checkbox localCheckbox = (Checkbox)this.target;
/* 474 */     ItemEvent localItemEvent = new ItemEvent(localCheckbox, 701, localCheckbox.getLabel(), paramBoolean ? 1 : 2);
/*     */ 
/* 478 */     postEvent(localItemEvent);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XCheckboxPeer
 * JD-Core Version:    0.6.2
 */