/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.Button;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Insets;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.SystemColor;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.FocusEvent;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.peer.ButtonPeer;
/*     */ import javax.swing.SwingUtilities;
/*     */ import javax.swing.plaf.basic.BasicGraphicsUtils;
/*     */ 
/*     */ public class XButtonPeer extends XComponentPeer
/*     */   implements ButtonPeer
/*     */ {
/*     */   boolean pressed;
/*     */   boolean armed;
/*     */   private Insets focusInsets;
/*     */   private Insets borderInsets;
/*     */   private Insets contentAreaInsets;
/*     */   private static final String propertyPrefix = "Button.";
/*  47 */   protected Color focusColor = SystemColor.windowText;
/*     */ 
/*  49 */   private boolean disposed = false;
/*     */   String label;
/*     */ 
/*     */   protected String getPropertyPrefix()
/*     */   {
/*  54 */     return "Button.";
/*     */   }
/*     */ 
/*     */   void preInit(XCreateWindowParams paramXCreateWindowParams) {
/*  58 */     super.preInit(paramXCreateWindowParams);
/*  59 */     this.borderInsets = new Insets(2, 2, 2, 2);
/*  60 */     this.focusInsets = new Insets(0, 0, 0, 0);
/*  61 */     this.contentAreaInsets = new Insets(3, 3, 3, 3);
/*     */   }
/*     */ 
/*     */   public XButtonPeer(Button paramButton)
/*     */   {
/*  66 */     super(paramButton);
/*  67 */     this.pressed = false;
/*  68 */     this.armed = false;
/*  69 */     this.label = paramButton.getLabel();
/*  70 */     updateMotifColors(getPeerBackground());
/*     */   }
/*     */ 
/*     */   public void dispose() {
/*  74 */     synchronized (this.target)
/*     */     {
/*  76 */       this.disposed = true;
/*     */     }
/*  78 */     super.dispose();
/*     */   }
/*     */ 
/*     */   public boolean isFocusable() {
/*  82 */     return true;
/*     */   }
/*     */ 
/*     */   public void setLabel(String paramString) {
/*  86 */     this.label = paramString;
/*  87 */     repaint();
/*     */   }
/*     */ 
/*     */   public void paint(Graphics paramGraphics) {
/*  91 */     paint(paramGraphics, this.target);
/*     */   }
/*     */ 
/*     */   public void setBackground(Color paramColor) {
/*  95 */     updateMotifColors(paramColor);
/*  96 */     super.setBackground(paramColor);
/*     */   }
/*     */ 
/*     */   void handleJavaMouseEvent(MouseEvent paramMouseEvent) {
/* 100 */     super.handleJavaMouseEvent(paramMouseEvent);
/* 101 */     int i = paramMouseEvent.getID();
/* 102 */     switch (i) {
/*     */     case 501:
/* 104 */       if (XToolkit.isLeftMouseButton(paramMouseEvent)) {
/* 105 */         Button localButton = (Button)paramMouseEvent.getSource();
/*     */ 
/* 107 */         if (localButton.contains(paramMouseEvent.getX(), paramMouseEvent.getY())) {
/* 108 */           if (!isEnabled())
/*     */           {
/* 110 */             return;
/*     */           }
/* 112 */           this.pressed = true;
/* 113 */           this.armed = true;
/* 114 */           repaint();
/*     */         }
/*     */       }
/* 116 */       break;
/*     */     case 502:
/* 121 */       if (XToolkit.isLeftMouseButton(paramMouseEvent)) {
/* 122 */         if (this.armed)
/*     */         {
/* 124 */           action(paramMouseEvent.getWhen(), paramMouseEvent.getModifiers());
/*     */         }
/* 126 */         this.pressed = false;
/* 127 */         this.armed = false;
/* 128 */         repaint(); } break;
/*     */     case 504:
/* 134 */       if (this.pressed)
/* 135 */         this.armed = true; break;
/*     */     case 505:
/* 141 */       this.armed = false;
/*     */     case 503:
/*     */     }
/*     */   }
/*     */ 
/*     */   public void action(long paramLong, int paramInt)
/*     */   {
/* 153 */     postEvent(new ActionEvent(this.target, 1001, ((Button)this.target).getActionCommand(), paramLong, paramInt));
/*     */   }
/*     */ 
/*     */   public void focusGained(FocusEvent paramFocusEvent)
/*     */   {
/* 160 */     super.focusGained(paramFocusEvent);
/* 161 */     repaint();
/*     */   }
/*     */ 
/*     */   public void focusLost(FocusEvent paramFocusEvent) {
/* 165 */     super.focusLost(paramFocusEvent);
/* 166 */     repaint();
/*     */   }
/*     */ 
/*     */   void handleJavaKeyEvent(KeyEvent paramKeyEvent) {
/* 170 */     int i = paramKeyEvent.getID();
/* 171 */     switch (i) {
/*     */     case 401:
/* 173 */       if (paramKeyEvent.getKeyCode() == 32)
/*     */       {
/* 175 */         this.pressed = true;
/* 176 */         this.armed = true;
/* 177 */         repaint();
/* 178 */         action(paramKeyEvent.getWhen(), paramKeyEvent.getModifiers()); } break;
/*     */     case 402:
/* 184 */       if (paramKeyEvent.getKeyCode() == 32)
/*     */       {
/* 186 */         this.pressed = false;
/* 187 */         this.armed = false;
/* 188 */         repaint();
/*     */       }
/*     */       break;
/*     */     }
/*     */   }
/*     */ 
/*     */   public Dimension getMinimumSize()
/*     */   {
/* 198 */     FontMetrics localFontMetrics = getFontMetrics(getPeerFont());
/* 199 */     if (this.label == null) {
/* 200 */       this.label = "";
/*     */     }
/* 202 */     return new Dimension(localFontMetrics.stringWidth(this.label) + 14, localFontMetrics.getHeight() + 8);
/*     */   }
/*     */ 
/*     */   public Dimension minimumSize()
/*     */   {
/* 210 */     return getMinimumSize();
/*     */   }
/*     */ 
/*     */   public void paint(Graphics paramGraphics, Component paramComponent)
/*     */   {
/* 220 */     if ((!this.disposed) && (paramGraphics != null))
/*     */     {
/* 222 */       Dimension localDimension = getPeerSize();
/*     */ 
/* 224 */       paramGraphics.setColor(getPeerBackground());
/* 225 */       paramGraphics.fillRect(0, 0, localDimension.width, localDimension.height);
/* 226 */       paintBorder(paramGraphics, this.borderInsets.left, this.borderInsets.top, localDimension.width - (this.borderInsets.left + this.borderInsets.right), localDimension.height - (this.borderInsets.top + this.borderInsets.bottom));
/*     */ 
/* 231 */       FontMetrics localFontMetrics = paramGraphics.getFontMetrics();
/*     */ 
/* 235 */       Rectangle localRectangle1 = new Rectangle();
/* 236 */       Rectangle localRectangle3 = new Rectangle();
/* 237 */       Rectangle localRectangle2 = new Rectangle();
/*     */ 
/* 240 */       localRectangle3.width = (localDimension.width - (this.contentAreaInsets.left + this.contentAreaInsets.right));
/* 241 */       localRectangle3.height = (localDimension.height - (this.contentAreaInsets.top + this.contentAreaInsets.bottom));
/*     */ 
/* 243 */       localRectangle3.x = this.contentAreaInsets.left;
/* 244 */       localRectangle3.y = this.contentAreaInsets.right;
/* 245 */       String str1 = this.label != null ? this.label : "";
/*     */ 
/* 248 */       String str2 = SwingUtilities.layoutCompoundLabel(localFontMetrics, str1, null, 0, 0, 0, 0, localRectangle3, localRectangle2, localRectangle1, 0);
/*     */ 
/* 254 */       Font localFont = getPeerFont();
/*     */ 
/* 256 */       paramGraphics.setFont(localFont);
/*     */ 
/* 259 */       if ((this.pressed) && (this.armed)) {
/* 260 */         paintButtonPressed(paramGraphics, this.target);
/*     */       }
/*     */ 
/* 263 */       paintText(paramGraphics, this.target, localRectangle1, str2);
/*     */ 
/* 265 */       if (hasFocus())
/*     */       {
/* 267 */         paintFocus(paramGraphics, this.focusInsets.left, this.focusInsets.top, localDimension.width - (this.focusInsets.left + this.focusInsets.right) - 1, localDimension.height - (this.focusInsets.top + this.focusInsets.bottom) - 1);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 273 */     flush();
/*     */   }
/*     */ 
/*     */   public void paintBorder(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/* 277 */     drawMotif3DRect(paramGraphics, paramInt1, paramInt2, paramInt3 - 1, paramInt4 - 1, this.pressed);
/*     */   }
/*     */ 
/*     */   public void setFont(Font paramFont) {
/* 281 */     super.setFont(paramFont);
/* 282 */     this.target.repaint();
/*     */   }
/*     */   protected void paintFocus(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/* 285 */     paramGraphics.setColor(this.focusColor);
/* 286 */     paramGraphics.drawRect(paramInt1, paramInt2, paramInt3, paramInt4);
/*     */   }
/*     */ 
/*     */   protected void paintButtonPressed(Graphics paramGraphics, Component paramComponent) {
/* 290 */     Dimension localDimension = getPeerSize();
/* 291 */     paramGraphics.setColor(this.selectColor);
/* 292 */     paramGraphics.fillRect(this.contentAreaInsets.left, this.contentAreaInsets.top, localDimension.width - (this.contentAreaInsets.left + this.contentAreaInsets.right), localDimension.height - (this.contentAreaInsets.top + this.contentAreaInsets.bottom));
/*     */   }
/*     */ 
/*     */   protected void paintText(Graphics paramGraphics, Component paramComponent, Rectangle paramRectangle, String paramString)
/*     */   {
/* 299 */     FontMetrics localFontMetrics = paramGraphics.getFontMetrics();
/*     */ 
/* 301 */     int i = -1;
/*     */ 
/* 304 */     if (isEnabled())
/*     */     {
/* 306 */       paramGraphics.setColor(getPeerForeground());
/* 307 */       BasicGraphicsUtils.drawStringUnderlineCharAt(paramGraphics, paramString, i, paramRectangle.x, paramRectangle.y + localFontMetrics.getAscent());
/*     */     }
/*     */     else
/*     */     {
/* 311 */       paramGraphics.setColor(getPeerBackground().brighter());
/*     */ 
/* 313 */       BasicGraphicsUtils.drawStringUnderlineCharAt(paramGraphics, paramString, i, paramRectangle.x, paramRectangle.y + localFontMetrics.getAscent());
/*     */ 
/* 315 */       paramGraphics.setColor(paramComponent.getBackground().darker());
/* 316 */       BasicGraphicsUtils.drawStringUnderlineCharAt(paramGraphics, paramString, i, paramRectangle.x - 1, paramRectangle.y + localFontMetrics.getAscent() - 1);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XButtonPeer
 * JD-Core Version:    0.6.2
 */