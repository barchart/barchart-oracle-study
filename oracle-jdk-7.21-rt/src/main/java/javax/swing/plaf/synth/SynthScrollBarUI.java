/*     */ package javax.swing.plaf.synth;
/*     */ 
/*     */ import java.awt.ComponentOrientation;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Insets;
/*     */ import java.awt.Rectangle;
/*     */ import java.beans.PropertyChangeEvent;
/*     */ import java.beans.PropertyChangeListener;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JScrollBar;
/*     */ import javax.swing.plaf.ComponentUI;
/*     */ import javax.swing.plaf.UIResource;
/*     */ import javax.swing.plaf.basic.BasicScrollBarUI;
/*     */ 
/*     */ public class SynthScrollBarUI extends BasicScrollBarUI
/*     */   implements PropertyChangeListener, SynthUI
/*     */ {
/*     */   private SynthStyle style;
/*     */   private SynthStyle thumbStyle;
/*     */   private SynthStyle trackStyle;
/*     */   private boolean validMinimumThumbSize;
/*     */ 
/*     */   public static ComponentUI createUI(JComponent paramJComponent)
/*     */   {
/*  52 */     return new SynthScrollBarUI();
/*     */   }
/*     */ 
/*     */   protected void installDefaults()
/*     */   {
/*  60 */     this.trackHighlight = 0;
/*  61 */     if ((this.scrollbar.getLayout() == null) || ((this.scrollbar.getLayout() instanceof UIResource)))
/*     */     {
/*  63 */       this.scrollbar.setLayout(this);
/*     */     }
/*  65 */     configureScrollBarColors();
/*  66 */     updateStyle(this.scrollbar);
/*     */   }
/*     */ 
/*     */   protected void configureScrollBarColors()
/*     */   {
/*     */   }
/*     */ 
/*     */   private void updateStyle(JScrollBar paramJScrollBar)
/*     */   {
/*  77 */     SynthStyle localSynthStyle = this.style;
/*  78 */     SynthContext localSynthContext = getContext(paramJScrollBar, 1);
/*  79 */     this.style = SynthLookAndFeel.updateStyle(localSynthContext, this);
/*  80 */     if (this.style != localSynthStyle) {
/*  81 */       this.scrollBarWidth = this.style.getInt(localSynthContext, "ScrollBar.thumbHeight", 14);
/*  82 */       this.minimumThumbSize = ((Dimension)this.style.get(localSynthContext, "ScrollBar.minimumThumbSize"));
/*     */ 
/*  84 */       if (this.minimumThumbSize == null) {
/*  85 */         this.minimumThumbSize = new Dimension();
/*  86 */         this.validMinimumThumbSize = false;
/*     */       }
/*     */       else {
/*  89 */         this.validMinimumThumbSize = true;
/*     */       }
/*  91 */       this.maximumThumbSize = ((Dimension)this.style.get(localSynthContext, "ScrollBar.maximumThumbSize"));
/*     */ 
/*  93 */       if (this.maximumThumbSize == null) {
/*  94 */         this.maximumThumbSize = new Dimension(4096, 4097);
/*     */       }
/*     */ 
/*  97 */       this.incrGap = this.style.getInt(localSynthContext, "ScrollBar.incrementButtonGap", 0);
/*  98 */       this.decrGap = this.style.getInt(localSynthContext, "ScrollBar.decrementButtonGap", 0);
/*     */ 
/* 103 */       String str = (String)this.scrollbar.getClientProperty("JComponent.sizeVariant");
/*     */ 
/* 105 */       if (str != null) {
/* 106 */         if ("large".equals(str)) {
/* 107 */           this.scrollBarWidth = ((int)(this.scrollBarWidth * 1.15D));
/* 108 */           this.incrGap = ((int)(this.incrGap * 1.15D));
/* 109 */           this.decrGap = ((int)(this.decrGap * 1.15D));
/* 110 */         } else if ("small".equals(str)) {
/* 111 */           this.scrollBarWidth = ((int)(this.scrollBarWidth * 0.857D));
/* 112 */           this.incrGap = ((int)(this.incrGap * 0.857D));
/* 113 */           this.decrGap = ((int)(this.decrGap * 0.857D));
/* 114 */         } else if ("mini".equals(str)) {
/* 115 */           this.scrollBarWidth = ((int)(this.scrollBarWidth * 0.714D));
/* 116 */           this.incrGap = ((int)(this.incrGap * 0.714D));
/* 117 */           this.decrGap = ((int)(this.decrGap * 0.714D));
/*     */         }
/*     */       }
/*     */ 
/* 121 */       if (localSynthStyle != null) {
/* 122 */         uninstallKeyboardActions();
/* 123 */         installKeyboardActions();
/*     */       }
/*     */     }
/* 126 */     localSynthContext.dispose();
/*     */ 
/* 128 */     localSynthContext = getContext(paramJScrollBar, Region.SCROLL_BAR_TRACK, 1);
/* 129 */     this.trackStyle = SynthLookAndFeel.updateStyle(localSynthContext, this);
/* 130 */     localSynthContext.dispose();
/*     */ 
/* 132 */     localSynthContext = getContext(paramJScrollBar, Region.SCROLL_BAR_THUMB, 1);
/* 133 */     this.thumbStyle = SynthLookAndFeel.updateStyle(localSynthContext, this);
/* 134 */     localSynthContext.dispose();
/*     */   }
/*     */ 
/*     */   protected void installListeners()
/*     */   {
/* 142 */     super.installListeners();
/* 143 */     this.scrollbar.addPropertyChangeListener(this);
/*     */   }
/*     */ 
/*     */   protected void uninstallListeners()
/*     */   {
/* 151 */     super.uninstallListeners();
/* 152 */     this.scrollbar.removePropertyChangeListener(this);
/*     */   }
/*     */ 
/*     */   protected void uninstallDefaults()
/*     */   {
/* 160 */     SynthContext localSynthContext = getContext(this.scrollbar, 1);
/* 161 */     this.style.uninstallDefaults(localSynthContext);
/* 162 */     localSynthContext.dispose();
/* 163 */     this.style = null;
/*     */ 
/* 165 */     localSynthContext = getContext(this.scrollbar, Region.SCROLL_BAR_TRACK, 1);
/* 166 */     this.trackStyle.uninstallDefaults(localSynthContext);
/* 167 */     localSynthContext.dispose();
/* 168 */     this.trackStyle = null;
/*     */ 
/* 170 */     localSynthContext = getContext(this.scrollbar, Region.SCROLL_BAR_THUMB, 1);
/* 171 */     this.thumbStyle.uninstallDefaults(localSynthContext);
/* 172 */     localSynthContext.dispose();
/* 173 */     this.thumbStyle = null;
/*     */ 
/* 175 */     super.uninstallDefaults();
/*     */   }
/*     */ 
/*     */   public SynthContext getContext(JComponent paramJComponent)
/*     */   {
/* 183 */     return getContext(paramJComponent, SynthLookAndFeel.getComponentState(paramJComponent));
/*     */   }
/*     */ 
/*     */   private SynthContext getContext(JComponent paramJComponent, int paramInt) {
/* 187 */     return SynthContext.getContext(SynthContext.class, paramJComponent, SynthLookAndFeel.getRegion(paramJComponent), this.style, paramInt);
/*     */   }
/*     */ 
/*     */   private SynthContext getContext(JComponent paramJComponent, Region paramRegion)
/*     */   {
/* 192 */     return getContext(paramJComponent, paramRegion, getComponentState(paramJComponent, paramRegion));
/*     */   }
/*     */ 
/*     */   private SynthContext getContext(JComponent paramJComponent, Region paramRegion, int paramInt) {
/* 196 */     SynthStyle localSynthStyle = this.trackStyle;
/*     */ 
/* 198 */     if (paramRegion == Region.SCROLL_BAR_THUMB) {
/* 199 */       localSynthStyle = this.thumbStyle;
/*     */     }
/* 201 */     return SynthContext.getContext(SynthContext.class, paramJComponent, paramRegion, localSynthStyle, paramInt);
/*     */   }
/*     */ 
/*     */   private int getComponentState(JComponent paramJComponent, Region paramRegion)
/*     */   {
/* 206 */     if ((paramRegion == Region.SCROLL_BAR_THUMB) && (isThumbRollover()) && (paramJComponent.isEnabled()))
/*     */     {
/* 208 */       return 2;
/*     */     }
/* 210 */     return SynthLookAndFeel.getComponentState(paramJComponent);
/*     */   }
/*     */ 
/*     */   public boolean getSupportsAbsolutePositioning()
/*     */   {
/* 218 */     SynthContext localSynthContext = getContext(this.scrollbar);
/* 219 */     boolean bool = this.style.getBoolean(localSynthContext, "ScrollBar.allowsAbsolutePositioning", false);
/*     */ 
/* 221 */     localSynthContext.dispose();
/* 222 */     return bool;
/*     */   }
/*     */ 
/*     */   public void update(Graphics paramGraphics, JComponent paramJComponent)
/*     */   {
/* 239 */     SynthContext localSynthContext = getContext(paramJComponent);
/*     */ 
/* 241 */     SynthLookAndFeel.update(localSynthContext, paramGraphics);
/* 242 */     localSynthContext.getPainter().paintScrollBarBackground(localSynthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight(), this.scrollbar.getOrientation());
/*     */ 
/* 245 */     paint(localSynthContext, paramGraphics);
/* 246 */     localSynthContext.dispose();
/*     */   }
/*     */ 
/*     */   public void paint(Graphics paramGraphics, JComponent paramJComponent)
/*     */   {
/* 260 */     SynthContext localSynthContext = getContext(paramJComponent);
/*     */ 
/* 262 */     paint(localSynthContext, paramGraphics);
/* 263 */     localSynthContext.dispose();
/*     */   }
/*     */ 
/*     */   protected void paint(SynthContext paramSynthContext, Graphics paramGraphics)
/*     */   {
/* 274 */     SynthContext localSynthContext = getContext(this.scrollbar, Region.SCROLL_BAR_TRACK);
/*     */ 
/* 276 */     paintTrack(localSynthContext, paramGraphics, getTrackBounds());
/* 277 */     localSynthContext.dispose();
/*     */ 
/* 279 */     localSynthContext = getContext(this.scrollbar, Region.SCROLL_BAR_THUMB);
/* 280 */     paintThumb(localSynthContext, paramGraphics, getThumbBounds());
/* 281 */     localSynthContext.dispose();
/*     */   }
/*     */ 
/*     */   public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/* 290 */     paramSynthContext.getPainter().paintScrollBarBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, this.scrollbar.getOrientation());
/*     */   }
/*     */ 
/*     */   protected void paintTrack(SynthContext paramSynthContext, Graphics paramGraphics, Rectangle paramRectangle)
/*     */   {
/* 303 */     SynthLookAndFeel.updateSubregion(paramSynthContext, paramGraphics, paramRectangle);
/* 304 */     paramSynthContext.getPainter().paintScrollBarTrackBackground(paramSynthContext, paramGraphics, paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height, this.scrollbar.getOrientation());
/*     */ 
/* 307 */     paramSynthContext.getPainter().paintScrollBarTrackBorder(paramSynthContext, paramGraphics, paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height, this.scrollbar.getOrientation());
/*     */   }
/*     */ 
/*     */   protected void paintThumb(SynthContext paramSynthContext, Graphics paramGraphics, Rectangle paramRectangle)
/*     */   {
/* 321 */     SynthLookAndFeel.updateSubregion(paramSynthContext, paramGraphics, paramRectangle);
/* 322 */     int i = this.scrollbar.getOrientation();
/* 323 */     paramSynthContext.getPainter().paintScrollBarThumbBackground(paramSynthContext, paramGraphics, paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height, i);
/*     */ 
/* 326 */     paramSynthContext.getPainter().paintScrollBarThumbBorder(paramSynthContext, paramGraphics, paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height, i);
/*     */   }
/*     */ 
/*     */   public Dimension getPreferredSize(JComponent paramJComponent)
/*     */   {
/* 349 */     Insets localInsets = paramJComponent.getInsets();
/* 350 */     return this.scrollbar.getOrientation() == 1 ? new Dimension(this.scrollBarWidth + localInsets.left + localInsets.right, 48) : new Dimension(48, this.scrollBarWidth + localInsets.top + localInsets.bottom);
/*     */   }
/*     */ 
/*     */   protected Dimension getMinimumThumbSize()
/*     */   {
/* 360 */     if (!this.validMinimumThumbSize) {
/* 361 */       if (this.scrollbar.getOrientation() == 1) {
/* 362 */         this.minimumThumbSize.width = this.scrollBarWidth;
/* 363 */         this.minimumThumbSize.height = 7;
/*     */       } else {
/* 365 */         this.minimumThumbSize.width = 7;
/* 366 */         this.minimumThumbSize.height = this.scrollBarWidth;
/*     */       }
/*     */     }
/* 369 */     return this.minimumThumbSize;
/*     */   }
/*     */ 
/*     */   protected JButton createDecreaseButton(int paramInt)
/*     */   {
/* 377 */     SynthArrowButton local1 = new SynthArrowButton(paramInt)
/*     */     {
/*     */       public boolean contains(int paramAnonymousInt1, int paramAnonymousInt2) {
/* 380 */         if (SynthScrollBarUI.this.decrGap < 0) {
/* 381 */           int i = getWidth();
/* 382 */           int j = getHeight();
/* 383 */           if (SynthScrollBarUI.this.scrollbar.getOrientation() == 1)
/*     */           {
/* 386 */             j += SynthScrollBarUI.this.decrGap;
/*     */           }
/*     */           else
/*     */           {
/* 390 */             i += SynthScrollBarUI.this.decrGap;
/*     */           }
/* 392 */           return (paramAnonymousInt1 >= 0) && (paramAnonymousInt1 < i) && (paramAnonymousInt2 >= 0) && (paramAnonymousInt2 < j);
/*     */         }
/* 394 */         return super.contains(paramAnonymousInt1, paramAnonymousInt2);
/*     */       }
/*     */     };
/* 397 */     local1.setName("ScrollBar.button");
/* 398 */     return local1;
/*     */   }
/*     */ 
/*     */   protected JButton createIncreaseButton(int paramInt)
/*     */   {
/* 406 */     SynthArrowButton local2 = new SynthArrowButton(paramInt)
/*     */     {
/*     */       public boolean contains(int paramAnonymousInt1, int paramAnonymousInt2) {
/* 409 */         if (SynthScrollBarUI.this.incrGap < 0) {
/* 410 */           int i = getWidth();
/* 411 */           int j = getHeight();
/* 412 */           if (SynthScrollBarUI.this.scrollbar.getOrientation() == 1)
/*     */           {
/* 415 */             j += SynthScrollBarUI.this.incrGap;
/* 416 */             paramAnonymousInt2 += SynthScrollBarUI.this.incrGap;
/*     */           }
/*     */           else
/*     */           {
/* 420 */             i += SynthScrollBarUI.this.incrGap;
/* 421 */             paramAnonymousInt1 += SynthScrollBarUI.this.incrGap;
/*     */           }
/* 423 */           return (paramAnonymousInt1 >= 0) && (paramAnonymousInt1 < i) && (paramAnonymousInt2 >= 0) && (paramAnonymousInt2 < j);
/*     */         }
/* 425 */         return super.contains(paramAnonymousInt1, paramAnonymousInt2);
/*     */       }
/*     */     };
/* 428 */     local2.setName("ScrollBar.button");
/* 429 */     return local2;
/*     */   }
/*     */ 
/*     */   protected void setThumbRollover(boolean paramBoolean)
/*     */   {
/* 437 */     if (isThumbRollover() != paramBoolean) {
/* 438 */       this.scrollbar.repaint(getThumbBounds());
/* 439 */       super.setThumbRollover(paramBoolean);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void updateButtonDirections() {
/* 444 */     int i = this.scrollbar.getOrientation();
/* 445 */     if (this.scrollbar.getComponentOrientation().isLeftToRight()) {
/* 446 */       ((SynthArrowButton)this.incrButton).setDirection(i == 0 ? 3 : 5);
/*     */ 
/* 448 */       ((SynthArrowButton)this.decrButton).setDirection(i == 0 ? 7 : 1);
/*     */     }
/*     */     else
/*     */     {
/* 452 */       ((SynthArrowButton)this.incrButton).setDirection(i == 0 ? 7 : 5);
/*     */ 
/* 454 */       ((SynthArrowButton)this.decrButton).setDirection(i == 0 ? 3 : 1);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
/*     */   {
/* 463 */     String str = paramPropertyChangeEvent.getPropertyName();
/*     */ 
/* 465 */     if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent)) {
/* 466 */       updateStyle((JScrollBar)paramPropertyChangeEvent.getSource());
/*     */     }
/*     */ 
/* 469 */     if ("orientation" == str) {
/* 470 */       updateButtonDirections();
/*     */     }
/* 472 */     else if ("componentOrientation" == str)
/* 473 */       updateButtonDirections();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.plaf.synth.SynthScrollBarUI
 * JD-Core Version:    0.6.2
 */