/*     */ package com.sun.java.swing.plaf.gtk;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Rectangle;
/*     */ import javax.swing.AbstractButton;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.plaf.synth.Region;
/*     */ import javax.swing.plaf.synth.SynthContext;
/*     */ import javax.swing.plaf.synth.SynthGraphicsUtils;
/*     */ import javax.swing.plaf.synth.SynthStyle;
/*     */ 
/*     */ class GTKGraphicsUtils extends SynthGraphicsUtils
/*     */ {
/*     */   public void paintText(SynthContext paramSynthContext, Graphics paramGraphics, String paramString, int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/*  39 */     if ((paramString == null) || (paramString.length() <= 0))
/*     */     {
/*  41 */       return;
/*     */     }
/*     */ 
/*  44 */     if (paramSynthContext.getRegion() == Region.INTERNAL_FRAME_TITLE_PANE)
/*     */     {
/*  47 */       return;
/*     */     }
/*  49 */     int i = paramSynthContext.getComponentState();
/*     */     Object localObject;
/*  50 */     if ((i & 0x8) == 8)
/*     */     {
/*  52 */       localObject = paramGraphics.getColor();
/*  53 */       paramGraphics.setColor(paramSynthContext.getStyle().getColor(paramSynthContext, GTKColorType.WHITE));
/*     */ 
/*  55 */       paramInt1++;
/*  56 */       paramInt2++;
/*  57 */       super.paintText(paramSynthContext, paramGraphics, paramString, paramInt1, paramInt2, paramInt3);
/*     */ 
/*  59 */       paramGraphics.setColor((Color)localObject);
/*  60 */       paramInt1--;
/*  61 */       paramInt2--;
/*  62 */       super.paintText(paramSynthContext, paramGraphics, paramString, paramInt1, paramInt2, paramInt3);
/*     */     }
/*     */     else {
/*  65 */       localObject = GTKLookAndFeel.getGtkThemeName();
/*  66 */       if ((localObject != null) && (((String)localObject).startsWith("blueprint")) && (shouldShadowText(paramSynthContext.getRegion(), i)))
/*     */       {
/*  69 */         paramGraphics.setColor(Color.BLACK);
/*  70 */         super.paintText(paramSynthContext, paramGraphics, paramString, paramInt1 + 1, paramInt2 + 1, paramInt3);
/*  71 */         paramGraphics.setColor(Color.WHITE);
/*     */       }
/*     */ 
/*  74 */       super.paintText(paramSynthContext, paramGraphics, paramString, paramInt1, paramInt2, paramInt3);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void paintText(SynthContext paramSynthContext, Graphics paramGraphics, String paramString, Rectangle paramRectangle, int paramInt)
/*     */   {
/*  91 */     if ((paramString == null) || (paramString.length() <= 0))
/*     */     {
/*  93 */       return;
/*     */     }
/*     */ 
/*  96 */     Region localRegion = paramSynthContext.getRegion();
/*  97 */     if (((localRegion == Region.RADIO_BUTTON) || (localRegion == Region.CHECK_BOX) || (localRegion == Region.TABBED_PANE_TAB)) && ((paramSynthContext.getComponentState() & 0x100) != 0))
/*     */     {
/* 102 */       JComponent localJComponent = paramSynthContext.getComponent();
/* 103 */       if ((!(localJComponent instanceof AbstractButton)) || (((AbstractButton)localJComponent).isFocusPainted()))
/*     */       {
/* 112 */         int i = paramSynthContext.getComponentState();
/* 113 */         GTKStyle localGTKStyle = (GTKStyle)paramSynthContext.getStyle();
/* 114 */         int j = localGTKStyle.getClassSpecificIntValue(paramSynthContext, "focus-line-width", 1);
/*     */ 
/* 117 */         int k = localGTKStyle.getClassSpecificIntValue(paramSynthContext, "focus-padding", 1);
/*     */ 
/* 120 */         int m = j + k;
/* 121 */         int n = paramRectangle.x - m;
/* 122 */         int i1 = paramRectangle.y - m;
/* 123 */         int i2 = paramRectangle.width + 2 * m;
/* 124 */         int i3 = paramRectangle.height + 2 * m;
/*     */ 
/* 126 */         Color localColor = paramGraphics.getColor();
/* 127 */         GTKPainter.INSTANCE.paintFocus(paramSynthContext, paramGraphics, localRegion, i, "checkbutton", n, i1, i2, i3);
/*     */ 
/* 130 */         paramGraphics.setColor(localColor);
/*     */       }
/*     */     }
/* 133 */     super.paintText(paramSynthContext, paramGraphics, paramString, paramRectangle, paramInt);
/*     */   }
/*     */ 
/*     */   private static boolean shouldShadowText(Region paramRegion, int paramInt) {
/* 137 */     int i = GTKLookAndFeel.synthStateToGTKState(paramRegion, paramInt);
/* 138 */     return (i == 2) && ((paramRegion == Region.MENU) || (paramRegion == Region.MENU_ITEM) || (paramRegion == Region.CHECK_BOX_MENU_ITEM) || (paramRegion == Region.RADIO_BUTTON_MENU_ITEM));
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.java.swing.plaf.gtk.GTKGraphicsUtils
 * JD-Core Version:    0.6.2
 */