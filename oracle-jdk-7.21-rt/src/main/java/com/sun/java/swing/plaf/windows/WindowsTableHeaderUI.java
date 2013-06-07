/*     */ package com.sun.java.swing.plaf.windows;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Insets;
/*     */ import javax.swing.Icon;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.SortOrder;
/*     */ import javax.swing.UIManager;
/*     */ import javax.swing.border.Border;
/*     */ import javax.swing.border.EmptyBorder;
/*     */ import javax.swing.plaf.ComponentUI;
/*     */ import javax.swing.plaf.UIResource;
/*     */ import javax.swing.plaf.basic.BasicTableHeaderUI;
/*     */ import javax.swing.table.JTableHeader;
/*     */ import javax.swing.table.TableCellRenderer;
/*     */ import javax.swing.table.TableColumn;
/*     */ import sun.swing.SwingUtilities2;
/*     */ import sun.swing.table.DefaultTableCellHeaderRenderer;
/*     */ 
/*     */ public class WindowsTableHeaderUI extends BasicTableHeaderUI
/*     */ {
/*     */   private TableCellRenderer originalHeaderRenderer;
/*     */ 
/*     */   public static ComponentUI createUI(JComponent paramJComponent)
/*     */   {
/*  45 */     return new WindowsTableHeaderUI();
/*     */   }
/*     */ 
/*     */   public void installUI(JComponent paramJComponent) {
/*  49 */     super.installUI(paramJComponent);
/*     */ 
/*  51 */     if (XPStyle.getXP() != null) {
/*  52 */       this.originalHeaderRenderer = this.header.getDefaultRenderer();
/*  53 */       if ((this.originalHeaderRenderer instanceof UIResource))
/*  54 */         this.header.setDefaultRenderer(new XPDefaultRenderer());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void uninstallUI(JComponent paramJComponent)
/*     */   {
/*  60 */     if ((this.header.getDefaultRenderer() instanceof XPDefaultRenderer)) {
/*  61 */       this.header.setDefaultRenderer(this.originalHeaderRenderer);
/*     */     }
/*  63 */     super.uninstallUI(paramJComponent);
/*     */   }
/*     */ 
/*     */   protected void rolloverColumnUpdated(int paramInt1, int paramInt2)
/*     */   {
/*  68 */     if (XPStyle.getXP() != null) {
/*  69 */       this.header.repaint(this.header.getHeaderRect(paramInt1));
/*  70 */       this.header.repaint(this.header.getHeaderRect(paramInt2));
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class IconBorder
/*     */     implements Border, UIResource
/*     */   {
/*     */     private final Icon icon;
/*     */     private final int top;
/*     */     private final int left;
/*     */     private final int bottom;
/*     */     private final int right;
/*     */ 
/*     */     public IconBorder(Icon paramIcon, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */     {
/* 226 */       this.icon = paramIcon;
/* 227 */       this.top = paramInt1;
/* 228 */       this.left = paramInt2;
/* 229 */       this.bottom = paramInt3;
/* 230 */       this.right = paramInt4;
/*     */     }
/*     */     public Insets getBorderInsets(Component paramComponent) {
/* 233 */       return new Insets(this.icon.getIconHeight() + this.top, this.left, this.bottom, this.right);
/*     */     }
/*     */     public boolean isBorderOpaque() {
/* 236 */       return false;
/*     */     }
/*     */ 
/*     */     public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/* 240 */       this.icon.paintIcon(paramComponent, paramGraphics, paramInt1 + this.left + (paramInt3 - this.left - this.right - this.icon.getIconWidth()) / 2, paramInt2 + this.top);
/*     */     }
/*     */   }
/*     */ 
/*     */   private class XPDefaultRenderer extends DefaultTableCellHeaderRenderer
/*     */   {
/*     */     XPStyle.Skin skin;
/*     */     boolean isSelected;
/*     */     boolean hasFocus;
/*     */     boolean hasRollover;
/*     */     int column;
/*     */ 
/*     */     XPDefaultRenderer()
/*     */     {
/*  80 */       setHorizontalAlignment(10);
/*     */     }
/*     */ 
/*     */     public Component getTableCellRendererComponent(JTable paramJTable, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2)
/*     */     {
/*  86 */       super.getTableCellRendererComponent(paramJTable, paramObject, paramBoolean1, paramBoolean2, paramInt1, paramInt2);
/*     */ 
/*  88 */       this.isSelected = paramBoolean1;
/*  89 */       this.hasFocus = paramBoolean2;
/*  90 */       this.column = paramInt2;
/*  91 */       this.hasRollover = (paramInt2 == WindowsTableHeaderUI.this.getRolloverColumn());
/*  92 */       if (this.skin == null) {
/*  93 */         this.skin = XPStyle.getXP().getSkin(WindowsTableHeaderUI.this.header, TMSchema.Part.HP_HEADERITEM);
/*     */       }
/*  95 */       Insets localInsets = this.skin.getContentMargin();
/*  96 */       Object localObject = null;
/*  97 */       int i = 0;
/*  98 */       int j = 0;
/*  99 */       int k = 0;
/* 100 */       int m = 0;
/* 101 */       if (localInsets != null) {
/* 102 */         i = localInsets.top;
/* 103 */         j = localInsets.left;
/* 104 */         k = localInsets.bottom;
/* 105 */         m = localInsets.right;
/*     */       }
/*     */ 
/* 113 */       j += 5;
/* 114 */       k += 4;
/* 115 */       m += 5;
/*     */       Icon localIcon;
/* 121 */       if ((WindowsLookAndFeel.isOnVista()) && ((((localIcon = getIcon()) instanceof UIResource)) || (localIcon == null)))
/*     */       {
/* 124 */         i++;
/* 125 */         setIcon(null);
/* 126 */         localIcon = null;
/* 127 */         SortOrder localSortOrder = getColumnSortOrder(paramJTable, paramInt2);
/*     */ 
/* 129 */         if (localSortOrder != null) {
/* 130 */           switch (WindowsTableHeaderUI.1.$SwitchMap$javax$swing$SortOrder[localSortOrder.ordinal()]) {
/*     */           case 1:
/* 132 */             localIcon = UIManager.getIcon("Table.ascendingSortIcon");
/*     */ 
/* 134 */             break;
/*     */           case 2:
/* 136 */             localIcon = UIManager.getIcon("Table.descendingSortIcon");
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 141 */         if (localIcon != null) {
/* 142 */           k = localIcon.getIconHeight();
/* 143 */           localObject = new WindowsTableHeaderUI.IconBorder(localIcon, i, j, k, m);
/*     */         }
/*     */         else {
/* 146 */           localIcon = UIManager.getIcon("Table.ascendingSortIcon");
/*     */ 
/* 148 */           int n = localIcon != null ? localIcon.getIconHeight() : 0;
/*     */ 
/* 150 */           if (n != 0) {
/* 151 */             k = n;
/*     */           }
/* 153 */           localObject = new EmptyBorder(n + i, j, k, m);
/*     */         }
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 159 */         i += 3;
/* 160 */         localObject = new EmptyBorder(i, j, k, m);
/*     */       }
/*     */ 
/* 163 */       setBorder((Border)localObject);
/* 164 */       return this;
/*     */     }
/*     */ 
/*     */     public void paint(Graphics paramGraphics) {
/* 168 */       Dimension localDimension = getSize();
/* 169 */       TMSchema.State localState = TMSchema.State.NORMAL;
/* 170 */       TableColumn localTableColumn = WindowsTableHeaderUI.this.header.getDraggedColumn();
/* 171 */       if ((localTableColumn != null) && (this.column == SwingUtilities2.convertColumnIndexToView(WindowsTableHeaderUI.this.header.getColumnModel(), localTableColumn.getModelIndex())))
/*     */       {
/* 174 */         localState = TMSchema.State.PRESSED;
/* 175 */       } else if ((this.isSelected) || (this.hasFocus) || (this.hasRollover)) {
/* 176 */         localState = TMSchema.State.HOT;
/*     */       }
/*     */ 
/* 179 */       if (WindowsLookAndFeel.isOnVista()) {
/* 180 */         SortOrder localSortOrder = getColumnSortOrder(WindowsTableHeaderUI.this.header.getTable(), this.column);
/* 181 */         if (localSortOrder != null) {
/* 182 */           switch (WindowsTableHeaderUI.1.$SwitchMap$javax$swing$SortOrder[localSortOrder.ordinal()])
/*     */           {
/*     */           case 1:
/*     */           case 2:
/* 186 */             switch (WindowsTableHeaderUI.1.$SwitchMap$com$sun$java$swing$plaf$windows$TMSchema$State[localState.ordinal()]) {
/*     */             case 1:
/* 188 */               localState = TMSchema.State.SORTEDNORMAL;
/* 189 */               break;
/*     */             case 2:
/* 191 */               localState = TMSchema.State.SORTEDPRESSED;
/* 192 */               break;
/*     */             case 3:
/* 194 */               localState = TMSchema.State.SORTEDHOT;
/* 195 */             }break;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 204 */       this.skin.paintSkin(paramGraphics, 0, 0, localDimension.width - 1, localDimension.height - 1, localState);
/* 205 */       super.paint(paramGraphics);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.java.swing.plaf.windows.WindowsTableHeaderUI
 * JD-Core Version:    0.6.2
 */