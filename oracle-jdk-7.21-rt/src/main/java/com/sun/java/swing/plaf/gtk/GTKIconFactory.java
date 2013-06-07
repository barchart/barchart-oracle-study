/*     */ package com.sun.java.swing.plaf.gtk;
/*     */ 
/*     */ import java.awt.ComponentOrientation;
/*     */ import java.awt.Graphics;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.swing.Icon;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JToolBar;
/*     */ import javax.swing.plaf.UIResource;
/*     */ import javax.swing.plaf.synth.Region;
/*     */ import javax.swing.plaf.synth.SynthContext;
/*     */ import javax.swing.plaf.synth.SynthLookAndFeel;
/*     */ import javax.swing.plaf.synth.SynthStyle;
/*     */ import javax.swing.plaf.synth.SynthStyleFactory;
/*     */ import sun.swing.plaf.synth.SynthIcon;
/*     */ 
/*     */ class GTKIconFactory
/*     */ {
/*     */   static final int CHECK_ICON_EXTRA_INSET = 1;
/*     */   static final int DEFAULT_ICON_SPACING = 2;
/*     */   static final int DEFAULT_ICON_SIZE = 13;
/*     */   static final int DEFAULT_TOGGLE_MENU_ITEM_SIZE = 12;
/*     */   private static final String RADIO_BUTTON_ICON = "paintRadioButtonIcon";
/*     */   private static final String CHECK_BOX_ICON = "paintCheckBoxIcon";
/*     */   private static final String MENU_ARROW_ICON = "paintMenuArrowIcon";
/*     */   private static final String CHECK_BOX_MENU_ITEM_CHECK_ICON = "paintCheckBoxMenuItemCheckIcon";
/*     */   private static final String RADIO_BUTTON_MENU_ITEM_CHECK_ICON = "paintRadioButtonMenuItemCheckIcon";
/*     */   private static final String TREE_EXPANDED_ICON = "paintTreeExpandedIcon";
/*     */   private static final String TREE_COLLAPSED_ICON = "paintTreeCollapsedIcon";
/*     */   private static final String ASCENDING_SORT_ICON = "paintAscendingSortIcon";
/*     */   private static final String DESCENDING_SORT_ICON = "paintDescendingSortIcon";
/*     */   private static final String TOOL_BAR_HANDLE_ICON = "paintToolBarHandleIcon";
/*  61 */   private static Map<String, DelegatingIcon> iconsPool = Collections.synchronizedMap(new HashMap());
/*     */ 
/*     */   private static DelegatingIcon getIcon(String paramString)
/*     */   {
/*  65 */     Object localObject = (DelegatingIcon)iconsPool.get(paramString);
/*  66 */     if (localObject == null) {
/*  67 */       if ((paramString == "paintTreeCollapsedIcon") || (paramString == "paintTreeExpandedIcon"))
/*     */       {
/*  70 */         localObject = new SynthExpanderIcon(paramString);
/*     */       }
/*  72 */       else if (paramString == "paintToolBarHandleIcon") {
/*  73 */         localObject = new ToolBarHandleIcon();
/*     */       }
/*  75 */       else if (paramString == "paintMenuArrowIcon") {
/*  76 */         localObject = new MenuArrowIcon();
/*     */       }
/*     */       else {
/*  79 */         localObject = new DelegatingIcon(paramString);
/*     */       }
/*  81 */       iconsPool.put(paramString, localObject);
/*     */     }
/*  83 */     return localObject;
/*     */   }
/*     */ 
/*     */   public static Icon getAscendingSortIcon()
/*     */   {
/*  90 */     return getIcon("paintAscendingSortIcon");
/*     */   }
/*     */ 
/*     */   public static Icon getDescendingSortIcon() {
/*  94 */     return getIcon("paintDescendingSortIcon");
/*     */   }
/*     */ 
/*     */   public static SynthIcon getTreeExpandedIcon()
/*     */   {
/* 101 */     return getIcon("paintTreeExpandedIcon");
/*     */   }
/*     */ 
/*     */   public static SynthIcon getTreeCollapsedIcon() {
/* 105 */     return getIcon("paintTreeCollapsedIcon");
/*     */   }
/*     */ 
/*     */   public static SynthIcon getRadioButtonIcon()
/*     */   {
/* 112 */     return getIcon("paintRadioButtonIcon");
/*     */   }
/*     */ 
/*     */   public static SynthIcon getCheckBoxIcon()
/*     */   {
/* 119 */     return getIcon("paintCheckBoxIcon");
/*     */   }
/*     */ 
/*     */   public static SynthIcon getMenuArrowIcon()
/*     */   {
/* 126 */     return getIcon("paintMenuArrowIcon");
/*     */   }
/*     */ 
/*     */   public static SynthIcon getCheckBoxMenuItemCheckIcon() {
/* 130 */     return getIcon("paintCheckBoxMenuItemCheckIcon");
/*     */   }
/*     */ 
/*     */   public static SynthIcon getRadioButtonMenuItemCheckIcon() {
/* 134 */     return getIcon("paintRadioButtonMenuItemCheckIcon");
/*     */   }
/*     */ 
/*     */   public static SynthIcon getToolBarHandleIcon()
/*     */   {
/* 141 */     return getIcon("paintToolBarHandleIcon");
/*     */   }
/*     */ 
/*     */   static void resetIcons() {
/* 145 */     synchronized (iconsPool) {
/* 146 */       for (DelegatingIcon localDelegatingIcon : iconsPool.values())
/* 147 */         localDelegatingIcon.resetIconDimensions();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class DelegatingIcon extends SynthIcon
/*     */     implements UIResource
/*     */   {
/* 154 */     private static final Class[] PARAM_TYPES = { SynthContext.class, Graphics.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE };
/*     */     private Object method;
/* 160 */     int iconDimension = -1;
/*     */ 
/*     */     DelegatingIcon(String paramString) {
/* 163 */       this.method = paramString;
/*     */     }
/*     */ 
/*     */     public void paintIcon(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */     {
/* 168 */       if (paramSynthContext != null)
/* 169 */         GTKPainter.INSTANCE.paintIcon(paramSynthContext, paramGraphics, getMethod(), paramInt1, paramInt2, paramInt3, paramInt4);
/*     */     }
/*     */ 
/*     */     public int getIconWidth(SynthContext paramSynthContext)
/*     */     {
/* 175 */       return getIconDimension(paramSynthContext);
/*     */     }
/*     */ 
/*     */     public int getIconHeight(SynthContext paramSynthContext) {
/* 179 */       return getIconDimension(paramSynthContext);
/*     */     }
/*     */ 
/*     */     void resetIconDimensions() {
/* 183 */       this.iconDimension = -1;
/*     */     }
/*     */ 
/*     */     protected Method getMethod() {
/* 187 */       if ((this.method instanceof String)) {
/* 188 */         this.method = resolveMethod((String)this.method);
/*     */       }
/* 190 */       return (Method)this.method;
/*     */     }
/*     */ 
/*     */     protected Class[] getMethodParamTypes() {
/* 194 */       return PARAM_TYPES;
/*     */     }
/*     */ 
/*     */     private Method resolveMethod(String paramString) {
/*     */       try {
/* 199 */         return GTKPainter.class.getMethod(paramString, getMethodParamTypes());
/*     */       } catch (NoSuchMethodException localNoSuchMethodException) {
/* 201 */         if (!$assertionsDisabled) throw new AssertionError();
/*     */       }
/* 203 */       return null;
/*     */     }
/*     */ 
/*     */     int getIconDimension(SynthContext paramSynthContext) {
/* 207 */       if (this.iconDimension >= 0) {
/* 208 */         return this.iconDimension;
/*     */       }
/*     */ 
/* 211 */       if (paramSynthContext == null) {
/* 212 */         return 13;
/*     */       }
/*     */ 
/* 215 */       Region localRegion = paramSynthContext.getRegion();
/* 216 */       GTKStyle localGTKStyle = (GTKStyle)paramSynthContext.getStyle();
/* 217 */       this.iconDimension = localGTKStyle.getClassSpecificIntValue(paramSynthContext, "indicator-size", (localRegion == Region.CHECK_BOX_MENU_ITEM) || (localRegion == Region.RADIO_BUTTON_MENU_ITEM) ? 12 : 13);
/*     */ 
/* 223 */       if ((localRegion == Region.CHECK_BOX) || (localRegion == Region.RADIO_BUTTON)) {
/* 224 */         this.iconDimension += 2 * localGTKStyle.getClassSpecificIntValue(paramSynthContext, "indicator-spacing", 2);
/*     */       }
/* 226 */       else if ((localRegion == Region.CHECK_BOX_MENU_ITEM) || (localRegion == Region.RADIO_BUTTON_MENU_ITEM))
/*     */       {
/* 228 */         this.iconDimension += 2;
/*     */       }
/* 230 */       return this.iconDimension;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class MenuArrowIcon extends GTKIconFactory.DelegatingIcon
/*     */   {
/* 326 */     private static final Class[] PARAM_TYPES = { SynthContext.class, Graphics.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, GTKConstants.ArrowType.class };
/*     */ 
/*     */     public MenuArrowIcon()
/*     */     {
/* 332 */       super();
/*     */     }
/*     */ 
/*     */     protected Class[] getMethodParamTypes() {
/* 336 */       return PARAM_TYPES;
/*     */     }
/*     */ 
/*     */     public void paintIcon(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */     {
/* 341 */       if (paramSynthContext != null) {
/* 342 */         GTKConstants.ArrowType localArrowType = GTKConstants.ArrowType.RIGHT;
/* 343 */         if (!paramSynthContext.getComponent().getComponentOrientation().isLeftToRight()) {
/* 344 */           localArrowType = GTKConstants.ArrowType.LEFT;
/*     */         }
/* 346 */         GTKPainter.INSTANCE.paintIcon(paramSynthContext, paramGraphics, getMethod(), paramInt1, paramInt2, paramInt3, paramInt4, localArrowType);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class SynthExpanderIcon extends GTKIconFactory.DelegatingIcon
/*     */   {
/*     */     SynthExpanderIcon(String paramString)
/*     */     {
/* 236 */       super();
/*     */     }
/*     */ 
/*     */     public void paintIcon(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */     {
/* 241 */       if (paramSynthContext != null) {
/* 242 */         super.paintIcon(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
/* 243 */         updateSizeIfNecessary(paramSynthContext);
/*     */       }
/*     */     }
/*     */ 
/*     */     int getIconDimension(SynthContext paramSynthContext) {
/* 248 */       updateSizeIfNecessary(paramSynthContext);
/* 249 */       return this.iconDimension == -1 ? 13 : this.iconDimension;
/*     */     }
/*     */ 
/*     */     private void updateSizeIfNecessary(SynthContext paramSynthContext)
/*     */     {
/* 254 */       if ((this.iconDimension == -1) && (paramSynthContext != null))
/* 255 */         this.iconDimension = paramSynthContext.getStyle().getInt(paramSynthContext, "Tree.expanderSize", 10);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class ToolBarHandleIcon extends GTKIconFactory.DelegatingIcon
/*     */   {
/* 265 */     private static final Class[] PARAM_TYPES = { SynthContext.class, Graphics.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, GTKConstants.Orientation.class };
/*     */     private SynthStyle style;
/*     */ 
/*     */     public ToolBarHandleIcon()
/*     */     {
/* 273 */       super();
/*     */     }
/*     */ 
/*     */     protected Class[] getMethodParamTypes() {
/* 277 */       return PARAM_TYPES;
/*     */     }
/*     */ 
/*     */     public void paintIcon(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */     {
/* 282 */       if (paramSynthContext != null) {
/* 283 */         JToolBar localJToolBar = (JToolBar)paramSynthContext.getComponent();
/* 284 */         GTKConstants.Orientation localOrientation = localJToolBar.getOrientation() == 0 ? GTKConstants.Orientation.HORIZONTAL : GTKConstants.Orientation.VERTICAL;
/*     */ 
/* 288 */         if (this.style == null) {
/* 289 */           this.style = SynthLookAndFeel.getStyleFactory().getStyle(paramSynthContext.getComponent(), GTKRegion.HANDLE_BOX);
/*     */         }
/*     */ 
/* 292 */         paramSynthContext = new SynthContext(localJToolBar, GTKRegion.HANDLE_BOX, this.style, 1);
/*     */ 
/* 295 */         GTKPainter.INSTANCE.paintIcon(paramSynthContext, paramGraphics, getMethod(), paramInt1, paramInt2, paramInt3, paramInt4, localOrientation);
/*     */       }
/*     */     }
/*     */ 
/*     */     public int getIconWidth(SynthContext paramSynthContext)
/*     */     {
/* 301 */       if (paramSynthContext == null) {
/* 302 */         return 10;
/*     */       }
/* 304 */       if (((JToolBar)paramSynthContext.getComponent()).getOrientation() == 0)
/*     */       {
/* 306 */         return 10;
/*     */       }
/* 308 */       return paramSynthContext.getComponent().getWidth();
/*     */     }
/*     */ 
/*     */     public int getIconHeight(SynthContext paramSynthContext)
/*     */     {
/* 313 */       if (paramSynthContext == null) {
/* 314 */         return 10;
/*     */       }
/* 316 */       if (((JToolBar)paramSynthContext.getComponent()).getOrientation() == 0)
/*     */       {
/* 318 */         return paramSynthContext.getComponent().getHeight();
/*     */       }
/* 320 */       return 10;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.java.swing.plaf.gtk.GTKIconFactory
 * JD-Core Version:    0.6.2
 */