/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Label;
/*     */ import java.awt.peer.LabelPeer;
/*     */ 
/*     */ class XLabelPeer extends XComponentPeer
/*     */   implements LabelPeer
/*     */ {
/*     */   static final int TEXT_XPAD = 8;
/*     */   static final int TEXT_YPAD = 6;
/*     */   String label;
/*     */   int alignment;
/*     */   FontMetrics cachedFontMetrics;
/*     */   Font oldfont;
/*     */ 
/*     */   FontMetrics getFontMetrics()
/*     */   {
/*  46 */     if (this.cachedFontMetrics != null)
/*  47 */       return this.cachedFontMetrics;
/*  48 */     return getFontMetrics(getPeerFont());
/*     */   }
/*     */ 
/*     */   void preInit(XCreateWindowParams paramXCreateWindowParams)
/*     */   {
/*  53 */     super.preInit(paramXCreateWindowParams);
/*  54 */     Label localLabel = (Label)this.target;
/*  55 */     this.label = localLabel.getText();
/*  56 */     if (this.label == null) {
/*  57 */       this.label = "";
/*     */     }
/*  59 */     this.alignment = localLabel.getAlignment();
/*     */   }
/*     */ 
/*     */   XLabelPeer(Label paramLabel) {
/*  63 */     super(paramLabel);
/*     */   }
/*     */ 
/*     */   public Dimension getMinimumSize()
/*     */   {
/*  70 */     FontMetrics localFontMetrics = getFontMetrics();
/*     */     int i;
/*     */     try
/*     */     {
/*  73 */       i = localFontMetrics.stringWidth(this.label);
/*     */     }
/*     */     catch (NullPointerException localNullPointerException) {
/*  76 */       i = 0;
/*     */     }
/*  78 */     return new Dimension(i + 8, localFontMetrics.getAscent() + localFontMetrics.getMaxDescent() + 6);
/*     */   }
/*     */ 
/*     */   public void paint(Graphics paramGraphics)
/*     */   {
/*  89 */     int i = 0;
/*  90 */     int j = 0;
/*  91 */     paramGraphics.setColor(getPeerBackground());
/*  92 */     paramGraphics.fillRect(0, 0, this.width, this.height);
/*     */ 
/*  94 */     Font localFont = getPeerFont();
/*  95 */     paramGraphics.setFont(localFont);
/*  96 */     FontMetrics localFontMetrics = paramGraphics.getFontMetrics();
/*     */ 
/*  98 */     if (this.cachedFontMetrics == null)
/*     */     {
/* 100 */       this.cachedFontMetrics = localFontMetrics;
/*     */     }
/* 104 */     else if (this.oldfont != localFont) {
/* 105 */       this.cachedFontMetrics = localFontMetrics;
/*     */     }
/*     */ 
/* 108 */     switch (this.alignment) {
/*     */     case 0:
/* 110 */       i = 2;
/* 111 */       j = (this.height + localFontMetrics.getMaxAscent() - localFontMetrics.getMaxDescent()) / 2;
/* 112 */       break;
/*     */     case 2:
/* 114 */       i = this.width - (localFontMetrics.stringWidth(this.label) + 2);
/* 115 */       j = (this.height + localFontMetrics.getMaxAscent() - localFontMetrics.getMaxDescent()) / 2;
/* 116 */       break;
/*     */     case 1:
/* 118 */       i = (this.width - localFontMetrics.stringWidth(this.label)) / 2;
/* 119 */       j = (this.height + localFontMetrics.getMaxAscent() - localFontMetrics.getMaxDescent()) / 2;
/*     */     }
/*     */ 
/* 122 */     if (isEnabled()) {
/* 123 */       paramGraphics.setColor(getPeerForeground());
/* 124 */       paramGraphics.drawString(this.label, i, j);
/*     */     }
/*     */     else {
/* 127 */       paramGraphics.setColor(getPeerBackground().brighter());
/* 128 */       paramGraphics.drawString(this.label, i, j);
/* 129 */       paramGraphics.setColor(getPeerBackground().darker());
/* 130 */       paramGraphics.drawString(this.label, i - 1, j - 1);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setText(String paramString) {
/* 135 */     this.label = paramString;
/* 136 */     if (this.label == null) {
/* 137 */       this.label = "";
/*     */     }
/* 139 */     repaint();
/*     */   }
/*     */   public void setFont(Font paramFont) {
/* 142 */     super.setFont(paramFont);
/* 143 */     this.target.repaint();
/*     */   }
/*     */ 
/*     */   public void setAlignment(int paramInt) {
/* 147 */     this.alignment = paramInt;
/* 148 */     repaint();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XLabelPeer
 * JD-Core Version:    0.6.2
 */