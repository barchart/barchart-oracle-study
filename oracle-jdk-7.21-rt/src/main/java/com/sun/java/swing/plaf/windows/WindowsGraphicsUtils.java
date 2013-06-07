/*     */ package com.sun.java.swing.plaf.windows;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.ComponentOrientation;
/*     */ import java.awt.Container;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.Window;
/*     */ import javax.swing.AbstractButton;
/*     */ import javax.swing.ButtonModel;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JMenu;
/*     */ import javax.swing.JMenuItem;
/*     */ import javax.swing.UIManager;
/*     */ import javax.swing.plaf.UIResource;
/*     */ import sun.swing.SwingUtilities2;
/*     */ 
/*     */ public class WindowsGraphicsUtils
/*     */ {
/*     */   public static void paintText(Graphics paramGraphics, AbstractButton paramAbstractButton, Rectangle paramRectangle, String paramString, int paramInt)
/*     */   {
/*  59 */     FontMetrics localFontMetrics = SwingUtilities2.getFontMetrics(paramAbstractButton, paramGraphics);
/*     */ 
/*  61 */     int i = paramAbstractButton.getDisplayedMnemonicIndex();
/*     */ 
/*  63 */     if (WindowsLookAndFeel.isMnemonicHidden() == true) {
/*  64 */       i = -1;
/*     */     }
/*     */ 
/*  67 */     XPStyle localXPStyle = XPStyle.getXP();
/*  68 */     if ((localXPStyle != null) && (!(paramAbstractButton instanceof JMenuItem))) {
/*  69 */       paintXPText(paramAbstractButton, paramGraphics, paramRectangle.x + paramInt, paramRectangle.y + localFontMetrics.getAscent() + paramInt, paramString, i);
/*     */     }
/*     */     else
/*     */     {
/*  73 */       paintClassicText(paramAbstractButton, paramGraphics, paramRectangle.x + paramInt, paramRectangle.y + localFontMetrics.getAscent() + paramInt, paramString, i);
/*     */     }
/*     */   }
/*     */ 
/*     */   static void paintClassicText(AbstractButton paramAbstractButton, Graphics paramGraphics, int paramInt1, int paramInt2, String paramString, int paramInt3)
/*     */   {
/*  81 */     ButtonModel localButtonModel = paramAbstractButton.getModel();
/*     */ 
/*  84 */     Color localColor1 = paramAbstractButton.getForeground();
/*  85 */     if (localButtonModel.isEnabled())
/*     */     {
/*  87 */       if (((!(paramAbstractButton instanceof JMenuItem)) || (!localButtonModel.isArmed())) && ((!(paramAbstractButton instanceof JMenu)) || ((!localButtonModel.isSelected()) && (!localButtonModel.isRollover()))))
/*     */       {
/*  94 */         paramGraphics.setColor(paramAbstractButton.getForeground());
/*     */       }
/*  96 */       SwingUtilities2.drawStringUnderlineCharAt(paramAbstractButton, paramGraphics, paramString, paramInt3, paramInt1, paramInt2);
/*     */     } else {
/*  98 */       localColor1 = UIManager.getColor("Button.shadow");
/*  99 */       Color localColor2 = UIManager.getColor("Button.disabledShadow");
/* 100 */       if (localButtonModel.isArmed()) {
/* 101 */         localColor1 = UIManager.getColor("Button.disabledForeground");
/*     */       } else {
/* 103 */         if (localColor2 == null) {
/* 104 */           localColor2 = paramAbstractButton.getBackground().darker();
/*     */         }
/* 106 */         paramGraphics.setColor(localColor2);
/* 107 */         SwingUtilities2.drawStringUnderlineCharAt(paramAbstractButton, paramGraphics, paramString, paramInt3, paramInt1 + 1, paramInt2 + 1);
/*     */       }
/*     */ 
/* 110 */       if (localColor1 == null) {
/* 111 */         localColor1 = paramAbstractButton.getBackground().brighter();
/*     */       }
/* 113 */       paramGraphics.setColor(localColor1);
/* 114 */       SwingUtilities2.drawStringUnderlineCharAt(paramAbstractButton, paramGraphics, paramString, paramInt3, paramInt1, paramInt2);
/*     */     }
/*     */   }
/*     */ 
/*     */   static void paintXPText(AbstractButton paramAbstractButton, Graphics paramGraphics, int paramInt1, int paramInt2, String paramString, int paramInt3)
/*     */   {
/* 120 */     TMSchema.Part localPart = WindowsButtonUI.getXPButtonType(paramAbstractButton);
/* 121 */     TMSchema.State localState = WindowsButtonUI.getXPButtonState(paramAbstractButton);
/* 122 */     paintXPText(paramAbstractButton, localPart, localState, paramGraphics, paramInt1, paramInt2, paramString, paramInt3);
/*     */   }
/*     */ 
/*     */   static void paintXPText(AbstractButton paramAbstractButton, TMSchema.Part paramPart, TMSchema.State paramState, Graphics paramGraphics, int paramInt1, int paramInt2, String paramString, int paramInt3)
/*     */   {
/* 127 */     XPStyle localXPStyle = XPStyle.getXP();
/* 128 */     Color localColor1 = paramAbstractButton.getForeground();
/*     */ 
/* 130 */     if ((localColor1 instanceof UIResource)) {
/* 131 */       localColor1 = localXPStyle.getColor(paramAbstractButton, paramPart, paramState, TMSchema.Prop.TEXTCOLOR, paramAbstractButton.getForeground());
/*     */ 
/* 135 */       if ((paramPart == TMSchema.Part.TP_BUTTON) && (paramState == TMSchema.State.DISABLED)) {
/* 136 */         localObject = localXPStyle.getColor(paramAbstractButton, paramPart, TMSchema.State.NORMAL, TMSchema.Prop.TEXTCOLOR, paramAbstractButton.getForeground());
/*     */ 
/* 138 */         if (localColor1.equals(localObject)) {
/* 139 */           localColor1 = localXPStyle.getColor(paramAbstractButton, TMSchema.Part.BP_PUSHBUTTON, paramState, TMSchema.Prop.TEXTCOLOR, localColor1);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 145 */       Object localObject = localXPStyle.getTypeEnum(paramAbstractButton, paramPart, paramState, TMSchema.Prop.TEXTSHADOWTYPE);
/*     */ 
/* 147 */       if ((localObject == TMSchema.TypeEnum.TST_SINGLE) || (localObject == TMSchema.TypeEnum.TST_CONTINUOUS))
/*     */       {
/* 149 */         Color localColor2 = localXPStyle.getColor(paramAbstractButton, paramPart, paramState, TMSchema.Prop.TEXTSHADOWCOLOR, Color.black);
/*     */ 
/* 151 */         Point localPoint = localXPStyle.getPoint(paramAbstractButton, paramPart, paramState, TMSchema.Prop.TEXTSHADOWOFFSET);
/* 152 */         if (localPoint != null) {
/* 153 */           paramGraphics.setColor(localColor2);
/* 154 */           SwingUtilities2.drawStringUnderlineCharAt(paramAbstractButton, paramGraphics, paramString, paramInt3, paramInt1 + localPoint.x, paramInt2 + localPoint.y);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 161 */     paramGraphics.setColor(localColor1);
/* 162 */     SwingUtilities2.drawStringUnderlineCharAt(paramAbstractButton, paramGraphics, paramString, paramInt3, paramInt1, paramInt2);
/*     */   }
/*     */ 
/*     */   static boolean isLeftToRight(Component paramComponent) {
/* 166 */     return paramComponent.getComponentOrientation().isLeftToRight();
/*     */   }
/*     */ 
/*     */   static void repaintMnemonicsInWindow(Window paramWindow)
/*     */   {
/* 174 */     if ((paramWindow == null) || (!paramWindow.isShowing())) {
/* 175 */       return;
/*     */     }
/*     */ 
/* 178 */     Window[] arrayOfWindow = paramWindow.getOwnedWindows();
/* 179 */     for (int i = 0; i < arrayOfWindow.length; i++) {
/* 180 */       repaintMnemonicsInWindow(arrayOfWindow[i]);
/*     */     }
/*     */ 
/* 183 */     repaintMnemonicsInContainer(paramWindow);
/*     */   }
/*     */ 
/*     */   static void repaintMnemonicsInContainer(Container paramContainer)
/*     */   {
/* 192 */     for (int i = 0; i < paramContainer.getComponentCount(); i++) {
/* 193 */       Component localComponent = paramContainer.getComponent(i);
/* 194 */       if ((localComponent != null) && (localComponent.isVisible()))
/*     */       {
/* 197 */         if (((localComponent instanceof AbstractButton)) && (((AbstractButton)localComponent).getMnemonic() != 0))
/*     */         {
/* 199 */           localComponent.repaint();
/*     */         }
/* 201 */         else if (((localComponent instanceof JLabel)) && (((JLabel)localComponent).getDisplayedMnemonic() != 0))
/*     */         {
/* 203 */           localComponent.repaint();
/*     */         }
/* 206 */         else if ((localComponent instanceof Container))
/* 207 */           repaintMnemonicsInContainer((Container)localComponent);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.java.swing.plaf.windows.WindowsGraphicsUtils
 * JD-Core Version:    0.6.2
 */