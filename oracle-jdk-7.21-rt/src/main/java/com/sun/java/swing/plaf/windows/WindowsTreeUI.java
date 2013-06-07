/*     */ package com.sun.java.swing.plaf.windows;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Rectangle;
/*     */ import java.io.Serializable;
/*     */ import javax.swing.Icon;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JTree;
/*     */ import javax.swing.plaf.ComponentUI;
/*     */ import javax.swing.plaf.basic.BasicTreeUI;
/*     */ import javax.swing.tree.DefaultTreeCellRenderer;
/*     */ import javax.swing.tree.TreeCellRenderer;
/*     */ 
/*     */ public class WindowsTreeUI extends BasicTreeUI
/*     */ {
/*     */   protected static final int HALF_SIZE = 4;
/*     */   protected static final int SIZE = 9;
/*     */ 
/*     */   public static ComponentUI createUI(JComponent paramJComponent)
/*     */   {
/*  60 */     return new WindowsTreeUI();
/*     */   }
/*     */ 
/*     */   protected void ensureRowsAreVisible(int paramInt1, int paramInt2)
/*     */   {
/*  69 */     if ((this.tree != null) && (paramInt1 >= 0) && (paramInt2 < getRowCount(this.tree))) {
/*  70 */       Rectangle localRectangle1 = this.tree.getVisibleRect();
/*     */       Rectangle localRectangle2;
/*  71 */       if (paramInt1 == paramInt2) {
/*  72 */         localRectangle2 = getPathBounds(this.tree, getPathForRow(this.tree, paramInt1));
/*     */ 
/*  75 */         if (localRectangle2 != null) {
/*  76 */           localRectangle2.x = localRectangle1.x;
/*  77 */           localRectangle2.width = localRectangle1.width;
/*  78 */           this.tree.scrollRectToVisible(localRectangle2);
/*     */         }
/*     */       }
/*     */       else {
/*  82 */         localRectangle2 = getPathBounds(this.tree, getPathForRow(this.tree, paramInt1));
/*     */ 
/*  84 */         if (localRectangle2 != null) {
/*  85 */           Rectangle localRectangle3 = localRectangle2;
/*  86 */           int i = localRectangle2.y;
/*  87 */           int j = i + localRectangle1.height;
/*     */ 
/*  89 */           for (int k = paramInt1 + 1; k <= paramInt2; k++) {
/*  90 */             localRectangle3 = getPathBounds(this.tree, getPathForRow(this.tree, k));
/*     */ 
/*  92 */             if ((localRectangle3 != null) && (localRectangle3.y + localRectangle3.height > j)) {
/*  93 */               k = paramInt2;
/*     */             }
/*     */           }
/*  96 */           this.tree.scrollRectToVisible(new Rectangle(localRectangle1.x, i, 1, localRectangle3.y + localRectangle3.height - i));
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected TreeCellRenderer createDefaultCellRenderer()
/*     */   {
/* 112 */     return new WindowsTreeCellRenderer();
/*     */   }
/*     */ 
/*     */   public static class CollapsedIcon extends WindowsTreeUI.ExpandedIcon
/*     */   {
/*     */     public static Icon createCollapsedIcon()
/*     */     {
/* 179 */       return new CollapsedIcon();
/*     */     }
/*     */ 
/*     */     public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2) {
/* 183 */       XPStyle.Skin localSkin = getSkin(paramComponent);
/* 184 */       if (localSkin != null) {
/* 185 */         localSkin.paintSkin(paramGraphics, paramInt1, paramInt2, TMSchema.State.CLOSED);
/*     */       } else {
/* 187 */         super.paintIcon(paramComponent, paramGraphics, paramInt1, paramInt2);
/* 188 */         paramGraphics.drawLine(paramInt1 + 4, paramInt2 + 2, paramInt1 + 4, paramInt2 + 6);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class ExpandedIcon
/*     */     implements Icon, Serializable
/*     */   {
/*     */     public static Icon createExpandedIcon()
/*     */     {
/* 128 */       return new ExpandedIcon();
/*     */     }
/*     */ 
/*     */     XPStyle.Skin getSkin(Component paramComponent) {
/* 132 */       XPStyle localXPStyle = XPStyle.getXP();
/* 133 */       return localXPStyle != null ? localXPStyle.getSkin(paramComponent, TMSchema.Part.TVP_GLYPH) : null;
/*     */     }
/*     */ 
/*     */     public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2) {
/* 137 */       XPStyle.Skin localSkin = getSkin(paramComponent);
/* 138 */       if (localSkin != null) {
/* 139 */         localSkin.paintSkin(paramGraphics, paramInt1, paramInt2, TMSchema.State.OPENED);
/* 140 */         return;
/*     */       }
/*     */ 
/* 143 */       Color localColor = paramComponent.getBackground();
/*     */ 
/* 145 */       if (localColor != null)
/* 146 */         paramGraphics.setColor(localColor);
/*     */       else
/* 148 */         paramGraphics.setColor(Color.white);
/* 149 */       paramGraphics.fillRect(paramInt1, paramInt2, 8, 8);
/* 150 */       paramGraphics.setColor(Color.gray);
/* 151 */       paramGraphics.drawRect(paramInt1, paramInt2, 8, 8);
/* 152 */       paramGraphics.setColor(Color.black);
/* 153 */       paramGraphics.drawLine(paramInt1 + 2, paramInt2 + 4, paramInt1 + 6, paramInt2 + 4);
/*     */     }
/*     */ 
/*     */     public int getIconWidth() {
/* 157 */       XPStyle.Skin localSkin = getSkin(null);
/* 158 */       return localSkin != null ? localSkin.getWidth() : 9;
/*     */     }
/*     */ 
/*     */     public int getIconHeight() {
/* 162 */       XPStyle.Skin localSkin = getSkin(null);
/* 163 */       return localSkin != null ? localSkin.getHeight() : 9;
/*     */     }
/*     */   }
/*     */ 
/*     */   public class WindowsTreeCellRenderer extends DefaultTreeCellRenderer
/*     */   {
/*     */     public WindowsTreeCellRenderer()
/*     */     {
/*     */     }
/*     */ 
/*     */     public Component getTreeCellRendererComponent(JTree paramJTree, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt, boolean paramBoolean4)
/*     */     {
/* 208 */       super.getTreeCellRendererComponent(paramJTree, paramObject, paramBoolean1, paramBoolean2, paramBoolean3, paramInt, paramBoolean4);
/*     */ 
/* 212 */       if (!paramJTree.isEnabled()) {
/* 213 */         setEnabled(false);
/* 214 */         if (paramBoolean3)
/* 215 */           setDisabledIcon(getLeafIcon());
/* 216 */         else if (paramBoolean1)
/* 217 */           setDisabledIcon(getOpenIcon());
/*     */         else
/* 219 */           setDisabledIcon(getClosedIcon());
/*     */       }
/*     */       else
/*     */       {
/* 223 */         setEnabled(true);
/* 224 */         if (paramBoolean3)
/* 225 */           setIcon(getLeafIcon());
/* 226 */         else if (paramBoolean1)
/* 227 */           setIcon(getOpenIcon());
/*     */         else {
/* 229 */           setIcon(getClosedIcon());
/*     */         }
/*     */       }
/* 232 */       return this;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.java.swing.plaf.windows.WindowsTreeUI
 * JD-Core Version:    0.6.2
 */