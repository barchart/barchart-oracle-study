/*     */ package com.sun.java.swing.plaf.gtk;
/*     */ 
/*     */ import java.awt.ComponentOrientation;
/*     */ import java.awt.Font;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JMenu;
/*     */ import javax.swing.JScrollBar;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JToolBar;
/*     */ import javax.swing.UIManager;
/*     */ import javax.swing.plaf.synth.Region;
/*     */ import javax.swing.plaf.synth.SynthStyle;
/*     */ import javax.swing.plaf.synth.SynthStyleFactory;
/*     */ 
/*     */ class GTKStyleFactory extends SynthStyleFactory
/*     */ {
/*     */   private final Map<Object, GTKStyle> stylesCache;
/*     */   private Font defaultFont;
/*     */ 
/*     */   GTKStyleFactory()
/*     */   {
/*  51 */     this.stylesCache = new HashMap();
/*     */   }
/*     */ 
/*     */   public synchronized SynthStyle getStyle(JComponent paramJComponent, Region paramRegion)
/*     */   {
/*  62 */     GTKEngine.WidgetType localWidgetType = GTKEngine.getWidgetType(paramJComponent, paramRegion);
/*     */ 
/*  64 */     Object localObject1 = null;
/*     */     boolean bool2;
/*     */     boolean bool3;
/*  65 */     if (paramRegion == Region.SCROLL_BAR)
/*     */     {
/*  69 */       if (paramJComponent != null) {
/*  70 */         JScrollBar localJScrollBar = (JScrollBar)paramJComponent;
/*  71 */         bool2 = localJScrollBar.getParent() instanceof JScrollPane;
/*  72 */         bool3 = localJScrollBar.getOrientation() == 0;
/*  73 */         boolean bool4 = localJScrollBar.getComponentOrientation().isLeftToRight();
/*  74 */         boolean bool5 = localJScrollBar.isFocusable();
/*  75 */         localObject1 = new ComplexKey(localWidgetType, new Object[] { Boolean.valueOf(bool2), Boolean.valueOf(bool3), Boolean.valueOf(bool4), Boolean.valueOf(bool5) });
/*     */       }
/*     */     }
/*  78 */     else if ((paramRegion == Region.CHECK_BOX) || (paramRegion == Region.RADIO_BUTTON))
/*     */     {
/*  81 */       if (paramJComponent != null) {
/*  82 */         boolean bool1 = paramJComponent.getComponentOrientation().isLeftToRight();
/*  83 */         localObject1 = new ComplexKey(localWidgetType, new Object[] { Boolean.valueOf(bool1) });
/*     */       }
/*     */     }
/*  86 */     else if (paramRegion == Region.BUTTON)
/*     */     {
/*  89 */       if (paramJComponent != null) {
/*  90 */         localObject2 = (JButton)paramJComponent;
/*  91 */         bool2 = ((JButton)localObject2).getParent() instanceof JToolBar;
/*  92 */         bool3 = ((JButton)localObject2).isDefaultCapable();
/*  93 */         localObject1 = new ComplexKey(localWidgetType, new Object[] { Boolean.valueOf(bool2), Boolean.valueOf(bool3) });
/*     */       }
/*  95 */     } else if ((paramRegion == Region.MENU) && 
/*  96 */       ((paramJComponent instanceof JMenu)) && (((JMenu)paramJComponent).isTopLevelMenu()) && (UIManager.getBoolean("Menu.useMenuBarForTopLevelMenus")))
/*     */     {
/*  98 */       localWidgetType = GTKEngine.WidgetType.MENU_BAR;
/*     */     }
/*     */ 
/* 102 */     if (localObject1 == null)
/*     */     {
/* 104 */       localObject1 = localWidgetType;
/*     */     }
/*     */ 
/* 107 */     Object localObject2 = (GTKStyle)this.stylesCache.get(localObject1);
/* 108 */     if (localObject2 == null) {
/* 109 */       localObject2 = new GTKStyle(this.defaultFont, localWidgetType);
/* 110 */       this.stylesCache.put(localObject1, localObject2);
/*     */     }
/*     */ 
/* 113 */     return localObject2;
/*     */   }
/*     */ 
/*     */   void initStyles(Font paramFont) {
/* 117 */     this.defaultFont = paramFont;
/* 118 */     this.stylesCache.clear();
/*     */   }
/*     */ 
/*     */   private static class ComplexKey
/*     */   {
/*     */     private final GTKEngine.WidgetType wt;
/*     */     private final Object[] args;
/*     */ 
/*     */     ComplexKey(GTKEngine.WidgetType paramWidgetType, Object[] paramArrayOfObject)
/*     */     {
/* 132 */       this.wt = paramWidgetType;
/* 133 */       this.args = paramArrayOfObject;
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 138 */       int i = this.wt.hashCode();
/* 139 */       if (this.args != null) {
/* 140 */         for (Object localObject : this.args) {
/* 141 */           i = i * 29 + (localObject == null ? 0 : localObject.hashCode());
/*     */         }
/*     */       }
/* 144 */       return i;
/*     */     }
/*     */ 
/*     */     public boolean equals(Object paramObject)
/*     */     {
/* 149 */       if (!(paramObject instanceof ComplexKey)) {
/* 150 */         return false;
/*     */       }
/* 152 */       ComplexKey localComplexKey = (ComplexKey)paramObject;
/* 153 */       if (this.wt == localComplexKey.wt) {
/* 154 */         if ((this.args == null) && (localComplexKey.args == null)) {
/* 155 */           return true;
/*     */         }
/* 157 */         if ((this.args != null) && (localComplexKey.args != null) && (this.args.length == localComplexKey.args.length))
/*     */         {
/* 160 */           for (int i = 0; i < this.args.length; i++) {
/* 161 */             Object localObject1 = this.args[i];
/* 162 */             Object localObject2 = localComplexKey.args[i];
/* 163 */             if (localObject1 == null ? localObject2 != null : !localObject1.equals(localObject2)) {
/* 164 */               return false;
/*     */             }
/*     */           }
/* 167 */           return true;
/*     */         }
/*     */       }
/* 170 */       return false;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 175 */       String str = "ComplexKey[wt=" + this.wt;
/* 176 */       if (this.args != null) {
/* 177 */         str = str + ",args=[";
/* 178 */         for (int i = 0; i < this.args.length; i++) {
/* 179 */           str = str + this.args[i];
/* 180 */           if (i < this.args.length - 1) str = str + ",";
/*     */         }
/* 182 */         str = str + "]";
/*     */       }
/* 184 */       str = str + "]";
/* 185 */       return str;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.java.swing.plaf.gtk.GTKStyleFactory
 * JD-Core Version:    0.6.2
 */