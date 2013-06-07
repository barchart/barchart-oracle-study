/*    */ package com.sun.java.swing.plaf.windows;
/*    */ 
/*    */ import java.awt.Container;
/*    */ import java.awt.Dimension;
/*    */ import java.awt.Font;
/*    */ import java.awt.FontMetrics;
/*    */ import java.awt.Graphics;
/*    */ import javax.swing.JComponent;
/*    */ import javax.swing.plaf.ComponentUI;
/*    */ import javax.swing.plaf.basic.BasicPopupMenuSeparatorUI;
/*    */ 
/*    */ public class WindowsPopupMenuSeparatorUI extends BasicPopupMenuSeparatorUI
/*    */ {
/*    */   public static ComponentUI createUI(JComponent paramJComponent)
/*    */   {
/* 48 */     return new WindowsPopupMenuSeparatorUI();
/*    */   }
/*    */ 
/*    */   public void paint(Graphics paramGraphics, JComponent paramJComponent) {
/* 52 */     Dimension localDimension = paramJComponent.getSize();
/*    */     int i;
/* 53 */     if (WindowsMenuItemUI.isVistaPainting()) {
/* 54 */       i = 1;
/* 55 */       Container localContainer = paramJComponent.getParent();
/* 56 */       if ((localContainer instanceof JComponent)) {
/* 57 */         localObject = ((JComponent)localContainer).getClientProperty(WindowsPopupMenuUI.GUTTER_OFFSET_KEY);
/*    */ 
/* 60 */         if ((localObject instanceof Integer))
/*    */         {
/* 66 */           i = ((Integer)localObject).intValue() - paramJComponent.getX();
/* 67 */           i += WindowsPopupMenuUI.getGutterWidth();
/*    */         }
/*    */       }
/* 70 */       Object localObject = XPStyle.getXP().getSkin(paramJComponent, TMSchema.Part.MP_POPUPSEPARATOR);
/* 71 */       int j = ((XPStyle.Skin)localObject).getHeight();
/* 72 */       int k = (localDimension.height - j) / 2;
/* 73 */       ((XPStyle.Skin)localObject).paintSkin(paramGraphics, i, k, localDimension.width - i - 1, j, TMSchema.State.NORMAL);
/*    */     } else {
/* 75 */       i = localDimension.height / 2;
/* 76 */       paramGraphics.setColor(paramJComponent.getForeground());
/* 77 */       paramGraphics.drawLine(1, i - 1, localDimension.width - 2, i - 1);
/*    */ 
/* 79 */       paramGraphics.setColor(paramJComponent.getBackground());
/* 80 */       paramGraphics.drawLine(1, i, localDimension.width - 2, i);
/*    */     }
/*    */   }
/*    */ 
/*    */   public Dimension getPreferredSize(JComponent paramJComponent) {
/* 85 */     int i = 0;
/* 86 */     Font localFont = paramJComponent.getFont();
/* 87 */     if (localFont != null) {
/* 88 */       i = paramJComponent.getFontMetrics(localFont).getHeight();
/*    */     }
/*    */ 
/* 91 */     return new Dimension(0, i / 2 + 2);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.java.swing.plaf.windows.WindowsPopupMenuSeparatorUI
 * JD-Core Version:    0.6.2
 */