/*     */ package com.sun.java.swing.plaf.windows;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Graphics;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.UIManager;
/*     */ import javax.swing.plaf.ComponentUI;
/*     */ import javax.swing.plaf.UIResource;
/*     */ import javax.swing.plaf.basic.BasicSpinnerUI;
/*     */ 
/*     */ public class WindowsSpinnerUI extends BasicSpinnerUI
/*     */ {
/*     */   public static ComponentUI createUI(JComponent paramJComponent)
/*     */   {
/*  42 */     return new WindowsSpinnerUI();
/*     */   }
/*     */ 
/*     */   public void paint(Graphics paramGraphics, JComponent paramJComponent)
/*     */   {
/*  50 */     if (XPStyle.getXP() != null) {
/*  51 */       paintXPBackground(paramGraphics, paramJComponent);
/*     */     }
/*  53 */     super.paint(paramGraphics, paramJComponent);
/*     */   }
/*     */ 
/*     */   private TMSchema.State getXPState(JComponent paramJComponent) {
/*  57 */     TMSchema.State localState = TMSchema.State.NORMAL;
/*  58 */     if (!paramJComponent.isEnabled()) {
/*  59 */       localState = TMSchema.State.DISABLED;
/*     */     }
/*  61 */     return localState;
/*     */   }
/*     */ 
/*     */   private void paintXPBackground(Graphics paramGraphics, JComponent paramJComponent) {
/*  65 */     XPStyle localXPStyle = XPStyle.getXP();
/*  66 */     XPStyle.Skin localSkin = localXPStyle.getSkin(paramJComponent, TMSchema.Part.EP_EDIT);
/*  67 */     TMSchema.State localState = getXPState(paramJComponent);
/*  68 */     localSkin.paintSkin(paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight(), localState);
/*     */   }
/*     */ 
/*     */   protected Component createPreviousButton() {
/*  72 */     if (XPStyle.getXP() != null) {
/*  73 */       XPStyle.GlyphButton localGlyphButton = new XPStyle.GlyphButton(this.spinner, TMSchema.Part.SPNP_DOWN);
/*  74 */       Dimension localDimension = UIManager.getDimension("Spinner.arrowButtonSize");
/*  75 */       localGlyphButton.setPreferredSize(localDimension);
/*  76 */       localGlyphButton.setRequestFocusEnabled(false);
/*  77 */       installPreviousButtonListeners(localGlyphButton);
/*  78 */       return localGlyphButton;
/*     */     }
/*  80 */     return super.createPreviousButton();
/*     */   }
/*     */ 
/*     */   protected Component createNextButton() {
/*  84 */     if (XPStyle.getXP() != null) {
/*  85 */       XPStyle.GlyphButton localGlyphButton = new XPStyle.GlyphButton(this.spinner, TMSchema.Part.SPNP_UP);
/*  86 */       Dimension localDimension = UIManager.getDimension("Spinner.arrowButtonSize");
/*  87 */       localGlyphButton.setPreferredSize(localDimension);
/*  88 */       localGlyphButton.setRequestFocusEnabled(false);
/*  89 */       installNextButtonListeners(localGlyphButton);
/*  90 */       return localGlyphButton;
/*     */     }
/*  92 */     return super.createNextButton();
/*     */   }
/*     */ 
/*     */   private UIResource getUIResource(Object[] paramArrayOfObject) {
/*  96 */     for (int i = 0; i < paramArrayOfObject.length; i++) {
/*  97 */       if ((paramArrayOfObject[i] instanceof UIResource)) {
/*  98 */         return (UIResource)paramArrayOfObject[i];
/*     */       }
/*     */     }
/* 101 */     return null;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.java.swing.plaf.windows.WindowsSpinnerUI
 * JD-Core Version:    0.6.2
 */