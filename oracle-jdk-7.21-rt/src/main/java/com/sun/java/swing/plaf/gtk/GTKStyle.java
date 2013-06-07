/*      */ package com.sun.java.swing.plaf.gtk;
/*      */ 
/*      */ import java.awt.Color;
/*      */ import java.awt.Component;
/*      */ import java.awt.ComponentOrientation;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.Font;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.Image;
/*      */ import java.awt.Insets;
/*      */ import java.awt.Toolkit;
/*      */ import java.awt.image.BufferedImage;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.util.HashMap;
/*      */ import java.util.Map;
/*      */ import javax.swing.Icon;
/*      */ import javax.swing.ImageIcon;
/*      */ import javax.swing.JButton;
/*      */ import javax.swing.JComponent;
/*      */ import javax.swing.JScrollBar;
/*      */ import javax.swing.JScrollPane;
/*      */ import javax.swing.JSeparator;
/*      */ import javax.swing.JToolBar;
/*      */ import javax.swing.ListCellRenderer;
/*      */ import javax.swing.UIDefaults;
/*      */ import javax.swing.UIDefaults.LazyValue;
/*      */ import javax.swing.UIManager;
/*      */ import javax.swing.plaf.ColorUIResource;
/*      */ import javax.swing.plaf.DimensionUIResource;
/*      */ import javax.swing.plaf.FontUIResource;
/*      */ import javax.swing.plaf.UIResource;
/*      */ import javax.swing.plaf.synth.ColorType;
/*      */ import javax.swing.plaf.synth.Region;
/*      */ import javax.swing.plaf.synth.SynthContext;
/*      */ import javax.swing.plaf.synth.SynthGraphicsUtils;
/*      */ import javax.swing.plaf.synth.SynthPainter;
/*      */ import javax.swing.plaf.synth.SynthStyle;
/*      */ import sun.awt.AppContext;
/*      */ import sun.awt.UNIXToolkit;
/*      */ import sun.swing.SwingUtilities2;
/*      */ import sun.swing.plaf.synth.SynthIcon;
/*      */ 
/*      */ class GTKStyle extends SynthStyle
/*      */   implements GTKConstants
/*      */ {
/*      */   private static final String ICON_PROPERTY_PREFIX = "gtk.icon.";
/*   59 */   static final Color BLACK_COLOR = new ColorUIResource(Color.BLACK);
/*   60 */   static final Color WHITE_COLOR = new ColorUIResource(Color.WHITE);
/*      */ 
/*   62 */   static final Font DEFAULT_FONT = new FontUIResource("sansserif", 0, 10);
/*      */ 
/*   64 */   static final Insets BUTTON_DEFAULT_BORDER_INSETS = new Insets(1, 1, 1, 1);
/*      */ 
/*   66 */   private static final GTKGraphicsUtils GTK_GRAPHICS = new GTKGraphicsUtils();
/*      */ 
/* 1109 */   private static final Map<String, String> CLASS_SPECIFIC_MAP = new HashMap();
/*      */   private static final Map<String, GTKStockIcon> ICONS_MAP;
/*      */   private final Font font;
/*      */   private final int widgetType;
/*      */   private final int xThickness;
/*      */   private final int yThickness;
/*      */ 
/*      */   private static native int nativeGetXThickness(int paramInt);
/*      */ 
/*      */   private static native int nativeGetYThickness(int paramInt);
/*      */ 
/*      */   private static native int nativeGetColorForState(int paramInt1, int paramInt2, int paramInt3);
/*      */ 
/*      */   private static native Object nativeGetClassValue(int paramInt, String paramString);
/*      */ 
/*      */   private static native String nativeGetPangoFontName(int paramInt);
/*      */ 
/*      */   GTKStyle(Font paramFont, GTKEngine.WidgetType paramWidgetType)
/*      */   {
/*   93 */     this.widgetType = paramWidgetType.ordinal();
/*      */     String str;
/*   96 */     synchronized (UNIXToolkit.GTK_LOCK) {
/*   97 */       this.xThickness = nativeGetXThickness(this.widgetType);
/*   98 */       this.yThickness = nativeGetYThickness(this.widgetType);
/*   99 */       str = nativeGetPangoFontName(this.widgetType);
/*      */     }
/*      */ 
/*  102 */     ??? = null;
/*  103 */     if (str != null) {
/*  104 */       ??? = PangoFonts.lookupFont(str);
/*      */     }
/*  106 */     if (??? != null)
/*  107 */       this.font = ((Font)???);
/*  108 */     else if (paramFont != null)
/*  109 */       this.font = paramFont;
/*      */     else
/*  111 */       this.font = DEFAULT_FONT;
/*      */   }
/*      */ 
/*      */   public void installDefaults(SynthContext paramSynthContext)
/*      */   {
/*  117 */     super.installDefaults(paramSynthContext);
/*  118 */     if (!paramSynthContext.getRegion().isSubregion())
/*  119 */       paramSynthContext.getComponent().putClientProperty(SwingUtilities2.AA_TEXT_PROPERTY_KEY, GTKLookAndFeel.aaTextInfo);
/*      */   }
/*      */ 
/*      */   public SynthGraphicsUtils getGraphicsUtils(SynthContext paramSynthContext)
/*      */   {
/*  127 */     return GTK_GRAPHICS;
/*      */   }
/*      */ 
/*      */   public SynthPainter getPainter(SynthContext paramSynthContext)
/*      */   {
/*  139 */     return GTKPainter.INSTANCE;
/*      */   }
/*      */ 
/*      */   protected Color getColorForState(SynthContext paramSynthContext, ColorType paramColorType) {
/*  143 */     if ((paramColorType == ColorType.FOCUS) || (paramColorType == GTKColorType.BLACK)) {
/*  144 */       return BLACK_COLOR;
/*      */     }
/*  146 */     if (paramColorType == GTKColorType.WHITE) {
/*  147 */       return WHITE_COLOR;
/*      */     }
/*      */ 
/*  150 */     Region localRegion = paramSynthContext.getRegion();
/*  151 */     int i = paramSynthContext.getComponentState();
/*  152 */     i = GTKLookAndFeel.synthStateToGTKState(localRegion, i);
/*      */ 
/*  154 */     if ((paramColorType == ColorType.TEXT_FOREGROUND) && ((localRegion == Region.BUTTON) || (localRegion == Region.CHECK_BOX) || (localRegion == Region.CHECK_BOX_MENU_ITEM) || (localRegion == Region.MENU) || (localRegion == Region.MENU_ITEM) || (localRegion == Region.RADIO_BUTTON) || (localRegion == Region.RADIO_BUTTON_MENU_ITEM) || (localRegion == Region.TABBED_PANE_TAB) || (localRegion == Region.TOGGLE_BUTTON) || (localRegion == Region.TOOL_TIP) || (localRegion == Region.MENU_ITEM_ACCELERATOR) || (localRegion == Region.TABBED_PANE_TAB)))
/*      */     {
/*  167 */       paramColorType = ColorType.FOREGROUND;
/*  168 */     } else if ((localRegion == Region.TABLE) || (localRegion == Region.LIST) || (localRegion == Region.TREE) || (localRegion == Region.TREE_CELL))
/*      */     {
/*  172 */       if (paramColorType == ColorType.FOREGROUND) {
/*  173 */         paramColorType = ColorType.TEXT_FOREGROUND;
/*  174 */         if (i == 4)
/*  175 */           i = 512;
/*      */       }
/*  177 */       else if (paramColorType == ColorType.BACKGROUND) {
/*  178 */         paramColorType = ColorType.TEXT_BACKGROUND;
/*      */       }
/*      */     }
/*      */ 
/*  182 */     return getStyleSpecificColor(paramSynthContext, i, paramColorType);
/*      */   }
/*      */ 
/*      */   private Color getStyleSpecificColor(SynthContext paramSynthContext, int paramInt, ColorType paramColorType)
/*      */   {
/*  192 */     paramInt = GTKLookAndFeel.synthStateToGTKStateType(paramInt).ordinal();
/*  193 */     synchronized (UNIXToolkit.GTK_LOCK) {
/*  194 */       int i = nativeGetColorForState(this.widgetType, paramInt, paramColorType.getID());
/*      */ 
/*  196 */       return new ColorUIResource(i);
/*      */     }
/*      */   }
/*      */ 
/*      */   Color getGTKColor(int paramInt, ColorType paramColorType) {
/*  201 */     return getGTKColor(null, paramInt, paramColorType);
/*      */   }
/*      */ 
/*      */   Color getGTKColor(SynthContext paramSynthContext, int paramInt, ColorType paramColorType)
/*      */   {
/*  213 */     if (paramSynthContext != null) {
/*  214 */       JComponent localJComponent = paramSynthContext.getComponent();
/*  215 */       Region localRegion = paramSynthContext.getRegion();
/*      */ 
/*  217 */       paramInt = GTKLookAndFeel.synthStateToGTKState(localRegion, paramInt);
/*  218 */       if ((!localRegion.isSubregion()) && ((paramInt & 0x1) != 0))
/*      */       {
/*      */         Color localColor;
/*  220 */         if ((paramColorType == ColorType.BACKGROUND) || (paramColorType == ColorType.TEXT_BACKGROUND))
/*      */         {
/*  222 */           localColor = localJComponent.getBackground();
/*  223 */           if (!(localColor instanceof UIResource)) {
/*  224 */             return localColor;
/*      */           }
/*      */         }
/*  227 */         else if ((paramColorType == ColorType.FOREGROUND) || (paramColorType == ColorType.TEXT_FOREGROUND))
/*      */         {
/*  229 */           localColor = localJComponent.getForeground();
/*  230 */           if (!(localColor instanceof UIResource)) {
/*  231 */             return localColor;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  237 */     return getStyleSpecificColor(paramSynthContext, paramInt, paramColorType);
/*      */   }
/*      */ 
/*      */   public Color getColor(SynthContext paramSynthContext, ColorType paramColorType)
/*      */   {
/*  242 */     JComponent localJComponent = paramSynthContext.getComponent();
/*  243 */     Region localRegion = paramSynthContext.getRegion();
/*  244 */     int i = paramSynthContext.getComponentState();
/*      */ 
/*  246 */     if (localJComponent.getName() == "Table.cellRenderer") {
/*  247 */       if (paramColorType == ColorType.BACKGROUND) {
/*  248 */         return localJComponent.getBackground();
/*      */       }
/*  250 */       if (paramColorType == ColorType.FOREGROUND) {
/*  251 */         return localJComponent.getForeground();
/*      */       }
/*      */     }
/*      */ 
/*  255 */     if ((localRegion == Region.LABEL) && (paramColorType == ColorType.TEXT_FOREGROUND)) {
/*  256 */       paramColorType = ColorType.FOREGROUND;
/*      */     }
/*      */ 
/*  260 */     if ((!localRegion.isSubregion()) && ((i & 0x1) != 0)) {
/*  261 */       if (paramColorType == ColorType.BACKGROUND) {
/*  262 */         return localJComponent.getBackground();
/*      */       }
/*  264 */       if (paramColorType == ColorType.FOREGROUND) {
/*  265 */         return localJComponent.getForeground();
/*      */       }
/*  267 */       if (paramColorType == ColorType.TEXT_FOREGROUND)
/*      */       {
/*  272 */         Color localColor = localJComponent.getForeground();
/*  273 */         if ((localColor != null) && (!(localColor instanceof UIResource))) {
/*  274 */           return localColor;
/*      */         }
/*      */       }
/*      */     }
/*  278 */     return getColorForState(paramSynthContext, paramColorType);
/*      */   }
/*      */ 
/*      */   protected Font getFontForState(SynthContext paramSynthContext) {
/*  282 */     return this.font;
/*      */   }
/*      */ 
/*      */   int getXThickness()
/*      */   {
/*  291 */     return this.xThickness;
/*      */   }
/*      */ 
/*      */   int getYThickness()
/*      */   {
/*  300 */     return this.yThickness;
/*      */   }
/*      */ 
/*      */   public Insets getInsets(SynthContext paramSynthContext, Insets paramInsets)
/*      */   {
/*  314 */     Region localRegion = paramSynthContext.getRegion();
/*  315 */     JComponent localJComponent = paramSynthContext.getComponent();
/*  316 */     String str = localRegion.isSubregion() ? null : localJComponent.getName();
/*      */ 
/*  318 */     if (paramInsets == null)
/*  319 */       paramInsets = new Insets(0, 0, 0, 0);
/*      */     else {
/*  321 */       paramInsets.top = (paramInsets.bottom = paramInsets.left = paramInsets.right = 0);
/*      */     }
/*      */ 
/*  324 */     if ((localRegion == Region.ARROW_BUTTON) || (localRegion == Region.BUTTON) || (localRegion == Region.TOGGLE_BUTTON))
/*      */     {
/*  326 */       if (("Spinner.previousButton" == str) || ("Spinner.nextButton" == str))
/*      */       {
/*  328 */         return getSimpleInsets(paramSynthContext, paramInsets, 1);
/*      */       }
/*  330 */       return getButtonInsets(paramSynthContext, paramInsets);
/*      */     }
/*      */ 
/*  333 */     if ((localRegion == Region.CHECK_BOX) || (localRegion == Region.RADIO_BUTTON)) {
/*  334 */       return getRadioInsets(paramSynthContext, paramInsets);
/*      */     }
/*  336 */     if (localRegion == Region.MENU_BAR) {
/*  337 */       return getMenuBarInsets(paramSynthContext, paramInsets);
/*      */     }
/*  339 */     if ((localRegion == Region.MENU) || (localRegion == Region.MENU_ITEM) || (localRegion == Region.CHECK_BOX_MENU_ITEM) || (localRegion == Region.RADIO_BUTTON_MENU_ITEM))
/*      */     {
/*  343 */       return getMenuItemInsets(paramSynthContext, paramInsets);
/*      */     }
/*  345 */     if (localRegion == Region.FORMATTED_TEXT_FIELD) {
/*  346 */       return getTextFieldInsets(paramSynthContext, paramInsets);
/*      */     }
/*  348 */     if (localRegion == Region.INTERNAL_FRAME) {
/*  349 */       paramInsets = Metacity.INSTANCE.getBorderInsets(paramSynthContext, paramInsets);
/*      */     }
/*  351 */     else if (localRegion == Region.LABEL) {
/*  352 */       if ("TableHeader.renderer" == str) {
/*  353 */         return getButtonInsets(paramSynthContext, paramInsets);
/*      */       }
/*  355 */       if ((localJComponent instanceof ListCellRenderer)) {
/*  356 */         return getTextFieldInsets(paramSynthContext, paramInsets);
/*      */       }
/*  358 */       if ("Tree.cellRenderer" == str)
/*  359 */         return getSimpleInsets(paramSynthContext, paramInsets, 1);
/*      */     }
/*      */     else {
/*  362 */       if (localRegion == Region.OPTION_PANE) {
/*  363 */         return getSimpleInsets(paramSynthContext, paramInsets, 6);
/*      */       }
/*  365 */       if (localRegion == Region.POPUP_MENU) {
/*  366 */         return getSimpleInsets(paramSynthContext, paramInsets, 2);
/*      */       }
/*  368 */       if ((localRegion == Region.PROGRESS_BAR) || (localRegion == Region.SLIDER) || (localRegion == Region.TABBED_PANE) || (localRegion == Region.TABBED_PANE_CONTENT) || (localRegion == Region.TOOL_BAR) || (localRegion == Region.TOOL_BAR_DRAG_WINDOW) || (localRegion == Region.TOOL_TIP))
/*      */       {
/*  373 */         return getThicknessInsets(paramSynthContext, paramInsets);
/*      */       }
/*  375 */       if (localRegion == Region.SCROLL_BAR) {
/*  376 */         return getScrollBarInsets(paramSynthContext, paramInsets);
/*      */       }
/*  378 */       if (localRegion == Region.SLIDER_TRACK) {
/*  379 */         return getSliderTrackInsets(paramSynthContext, paramInsets);
/*      */       }
/*  381 */       if (localRegion == Region.TABBED_PANE_TAB) {
/*  382 */         return getTabbedPaneTabInsets(paramSynthContext, paramInsets);
/*      */       }
/*  384 */       if ((localRegion == Region.TEXT_FIELD) || (localRegion == Region.PASSWORD_FIELD)) {
/*  385 */         if (str == "Tree.cellEditor") {
/*  386 */           return getSimpleInsets(paramSynthContext, paramInsets, 1);
/*      */         }
/*  388 */         return getTextFieldInsets(paramSynthContext, paramInsets);
/*  389 */       }if ((localRegion == Region.SEPARATOR) || (localRegion == Region.POPUP_MENU_SEPARATOR) || (localRegion == Region.TOOL_BAR_SEPARATOR))
/*      */       {
/*  392 */         return getSeparatorInsets(paramSynthContext, paramInsets);
/*  393 */       }if (localRegion == GTKEngine.CustomRegion.TITLED_BORDER)
/*  394 */         return getThicknessInsets(paramSynthContext, paramInsets);
/*      */     }
/*  396 */     return paramInsets;
/*      */   }
/*      */ 
/*      */   private Insets getButtonInsets(SynthContext paramSynthContext, Insets paramInsets)
/*      */   {
/*  402 */     int i = 1;
/*  403 */     int j = getClassSpecificIntValue(paramSynthContext, "focus-line-width", 1);
/*  404 */     int k = getClassSpecificIntValue(paramSynthContext, "focus-padding", 1);
/*  405 */     int m = getXThickness();
/*  406 */     int n = getYThickness();
/*  407 */     int i1 = j + k + m + i;
/*  408 */     int i2 = j + k + n + i;
/*  409 */     paramInsets.left = (paramInsets.right = i1);
/*  410 */     paramInsets.top = (paramInsets.bottom = i2);
/*      */ 
/*  412 */     JComponent localJComponent = paramSynthContext.getComponent();
/*  413 */     if (((localJComponent instanceof JButton)) && (!(localJComponent.getParent() instanceof JToolBar)) && (((JButton)localJComponent).isDefaultCapable()))
/*      */     {
/*  422 */       Insets localInsets = getClassSpecificInsetsValue(paramSynthContext, "default-border", BUTTON_DEFAULT_BORDER_INSETS);
/*      */ 
/*  424 */       paramInsets.left += localInsets.left;
/*  425 */       paramInsets.right += localInsets.right;
/*  426 */       paramInsets.top += localInsets.top;
/*  427 */       paramInsets.bottom += localInsets.bottom;
/*      */     }
/*      */ 
/*  430 */     return paramInsets;
/*      */   }
/*      */ 
/*      */   private Insets getRadioInsets(SynthContext paramSynthContext, Insets paramInsets)
/*      */   {
/*  439 */     int i = getClassSpecificIntValue(paramSynthContext, "focus-line-width", 1);
/*      */ 
/*  441 */     int j = getClassSpecificIntValue(paramSynthContext, "focus-padding", 1);
/*      */ 
/*  443 */     int k = i + j;
/*      */ 
/*  449 */     paramInsets.top = k;
/*  450 */     paramInsets.bottom = k;
/*  451 */     if (paramSynthContext.getComponent().getComponentOrientation().isLeftToRight()) {
/*  452 */       paramInsets.left = 0;
/*  453 */       paramInsets.right = k;
/*      */     } else {
/*  455 */       paramInsets.left = k;
/*  456 */       paramInsets.right = 0;
/*      */     }
/*      */ 
/*  459 */     return paramInsets;
/*      */   }
/*      */ 
/*      */   private Insets getMenuBarInsets(SynthContext paramSynthContext, Insets paramInsets)
/*      */   {
/*  465 */     int i = getClassSpecificIntValue(paramSynthContext, "internal-padding", 1);
/*      */ 
/*  467 */     int j = getXThickness();
/*  468 */     int k = getYThickness();
/*  469 */     paramInsets.left = (paramInsets.right = j + i);
/*  470 */     paramInsets.top = (paramInsets.bottom = k + i);
/*  471 */     return paramInsets;
/*      */   }
/*      */ 
/*      */   private Insets getMenuItemInsets(SynthContext paramSynthContext, Insets paramInsets)
/*      */   {
/*  477 */     int i = getClassSpecificIntValue(paramSynthContext, "horizontal-padding", 3);
/*      */ 
/*  479 */     int j = getXThickness();
/*  480 */     int k = getYThickness();
/*  481 */     paramInsets.left = (paramInsets.right = j + i);
/*  482 */     paramInsets.top = (paramInsets.bottom = k);
/*  483 */     return paramInsets;
/*      */   }
/*      */ 
/*      */   private Insets getThicknessInsets(SynthContext paramSynthContext, Insets paramInsets) {
/*  487 */     paramInsets.left = (paramInsets.right = getXThickness());
/*  488 */     paramInsets.top = (paramInsets.bottom = getYThickness());
/*  489 */     return paramInsets;
/*      */   }
/*      */ 
/*      */   private Insets getSeparatorInsets(SynthContext paramSynthContext, Insets paramInsets) {
/*  493 */     int i = 0;
/*  494 */     if (paramSynthContext.getRegion() == Region.POPUP_MENU_SEPARATOR) {
/*  495 */       i = getClassSpecificIntValue(paramSynthContext, "horizontal-padding", 3);
/*      */     }
/*      */ 
/*  498 */     paramInsets.right = (paramInsets.left = getXThickness() + i);
/*  499 */     paramInsets.top = (paramInsets.bottom = getYThickness());
/*  500 */     return paramInsets;
/*      */   }
/*      */ 
/*      */   private Insets getSliderTrackInsets(SynthContext paramSynthContext, Insets paramInsets) {
/*  504 */     int i = getClassSpecificIntValue(paramSynthContext, "focus-line-width", 1);
/*  505 */     int j = getClassSpecificIntValue(paramSynthContext, "focus-padding", 1);
/*  506 */     paramInsets.top = (paramInsets.bottom = paramInsets.left = paramInsets.right = i + j);
/*      */ 
/*  508 */     return paramInsets;
/*      */   }
/*      */ 
/*      */   private Insets getSimpleInsets(SynthContext paramSynthContext, Insets paramInsets, int paramInt) {
/*  512 */     paramInsets.top = (paramInsets.bottom = paramInsets.right = paramInsets.left = paramInt);
/*  513 */     return paramInsets;
/*      */   }
/*      */ 
/*      */   private Insets getTabbedPaneTabInsets(SynthContext paramSynthContext, Insets paramInsets) {
/*  517 */     int i = getXThickness();
/*  518 */     int j = getYThickness();
/*  519 */     int k = getClassSpecificIntValue(paramSynthContext, "focus-line-width", 1);
/*  520 */     int m = 2;
/*      */ 
/*  522 */     paramInsets.left = (paramInsets.right = k + m + i);
/*  523 */     paramInsets.top = (paramInsets.bottom = k + m + j);
/*  524 */     return paramInsets;
/*      */   }
/*      */ 
/*      */   private Insets getTextFieldInsets(SynthContext paramSynthContext, Insets paramInsets)
/*      */   {
/*  529 */     paramInsets = getClassSpecificInsetsValue(paramSynthContext, "inner-border", getSimpleInsets(paramSynthContext, paramInsets, 2));
/*      */ 
/*  532 */     int i = getXThickness();
/*  533 */     int j = getYThickness();
/*  534 */     boolean bool = getClassSpecificBoolValue(paramSynthContext, "interior-focus", true);
/*      */ 
/*  536 */     int k = 0;
/*      */ 
/*  538 */     if (!bool) {
/*  539 */       k = getClassSpecificIntValue(paramSynthContext, "focus-line-width", 1);
/*      */     }
/*      */ 
/*  542 */     paramInsets.left += k + i;
/*  543 */     paramInsets.right += k + i;
/*  544 */     paramInsets.top += k + j;
/*  545 */     paramInsets.bottom += k + j;
/*  546 */     return paramInsets;
/*      */   }
/*      */ 
/*      */   private Insets getScrollBarInsets(SynthContext paramSynthContext, Insets paramInsets) {
/*  550 */     int i = getClassSpecificIntValue(paramSynthContext, "trough-border", 1);
/*      */ 
/*  552 */     paramInsets.left = (paramInsets.right = paramInsets.top = paramInsets.bottom = i);
/*      */ 
/*  554 */     JComponent localJComponent = paramSynthContext.getComponent();
/*      */     int j;
/*  555 */     if ((localJComponent.getParent() instanceof JScrollPane))
/*      */     {
/*  559 */       j = getClassSpecificIntValue(GTKEngine.WidgetType.SCROLL_PANE, "scrollbar-spacing", 3);
/*      */ 
/*  562 */       if (((JScrollBar)localJComponent).getOrientation() == 0) {
/*  563 */         paramInsets.top += j;
/*      */       }
/*  565 */       else if (localJComponent.getComponentOrientation().isLeftToRight())
/*  566 */         paramInsets.left += j;
/*      */       else {
/*  568 */         paramInsets.right += j;
/*      */       }
/*      */ 
/*      */     }
/*  574 */     else if (localJComponent.isFocusable()) {
/*  575 */       j = getClassSpecificIntValue(paramSynthContext, "focus-line-width", 1);
/*      */ 
/*  577 */       int k = getClassSpecificIntValue(paramSynthContext, "focus-padding", 1);
/*      */ 
/*  579 */       int m = j + k;
/*  580 */       paramInsets.left += m;
/*  581 */       paramInsets.right += m;
/*  582 */       paramInsets.top += m;
/*  583 */       paramInsets.bottom += m;
/*      */     }
/*      */ 
/*  586 */     return paramInsets;
/*      */   }
/*      */ 
/*      */   private static Object getClassSpecificValue(GTKEngine.WidgetType paramWidgetType, String paramString)
/*      */   {
/*  602 */     synchronized (UNIXToolkit.GTK_LOCK) {
/*  603 */       return nativeGetClassValue(paramWidgetType.ordinal(), paramString);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static int getClassSpecificIntValue(GTKEngine.WidgetType paramWidgetType, String paramString, int paramInt)
/*      */   {
/*  620 */     Object localObject = getClassSpecificValue(paramWidgetType, paramString);
/*  621 */     if ((localObject instanceof Number)) {
/*  622 */       return ((Number)localObject).intValue();
/*      */     }
/*  624 */     return paramInt;
/*      */   }
/*      */ 
/*      */   Object getClassSpecificValue(String paramString)
/*      */   {
/*  635 */     synchronized (UNIXToolkit.GTK_LOCK) {
/*  636 */       return nativeGetClassValue(this.widgetType, paramString);
/*      */     }
/*      */   }
/*      */ 
/*      */   int getClassSpecificIntValue(SynthContext paramSynthContext, String paramString, int paramInt)
/*      */   {
/*  652 */     Object localObject = getClassSpecificValue(paramString);
/*      */ 
/*  654 */     if ((localObject instanceof Number)) {
/*  655 */       return ((Number)localObject).intValue();
/*      */     }
/*  657 */     return paramInt;
/*      */   }
/*      */ 
/*      */   Insets getClassSpecificInsetsValue(SynthContext paramSynthContext, String paramString, Insets paramInsets)
/*      */   {
/*  672 */     Object localObject = getClassSpecificValue(paramString);
/*      */ 
/*  674 */     if ((localObject instanceof Insets)) {
/*  675 */       return (Insets)localObject;
/*      */     }
/*  677 */     return paramInsets;
/*      */   }
/*      */ 
/*      */   boolean getClassSpecificBoolValue(SynthContext paramSynthContext, String paramString, boolean paramBoolean)
/*      */   {
/*  692 */     Object localObject = getClassSpecificValue(paramString);
/*      */ 
/*  694 */     if ((localObject instanceof Boolean)) {
/*  695 */       return ((Boolean)localObject).booleanValue();
/*      */     }
/*  697 */     return paramBoolean;
/*      */   }
/*      */ 
/*      */   public boolean isOpaque(SynthContext paramSynthContext)
/*      */   {
/*  710 */     Region localRegion = paramSynthContext.getRegion();
/*  711 */     if ((localRegion == Region.COMBO_BOX) || (localRegion == Region.DESKTOP_PANE) || (localRegion == Region.DESKTOP_ICON) || (localRegion == Region.EDITOR_PANE) || (localRegion == Region.FORMATTED_TEXT_FIELD) || (localRegion == Region.INTERNAL_FRAME) || (localRegion == Region.LIST) || (localRegion == Region.MENU_BAR) || (localRegion == Region.PANEL) || (localRegion == Region.PASSWORD_FIELD) || (localRegion == Region.POPUP_MENU) || (localRegion == Region.PROGRESS_BAR) || (localRegion == Region.ROOT_PANE) || (localRegion == Region.SCROLL_PANE) || (localRegion == Region.SPINNER) || (localRegion == Region.SPLIT_PANE_DIVIDER) || (localRegion == Region.TABLE) || (localRegion == Region.TEXT_AREA) || (localRegion == Region.TEXT_FIELD) || (localRegion == Region.TEXT_PANE) || (localRegion == Region.TOOL_BAR_DRAG_WINDOW) || (localRegion == Region.TOOL_TIP) || (localRegion == Region.TREE) || (localRegion == Region.VIEWPORT))
/*      */     {
/*  735 */       return true;
/*      */     }
/*  737 */     JComponent localJComponent = paramSynthContext.getComponent();
/*  738 */     String str = localJComponent.getName();
/*  739 */     if ((str == "ComboBox.renderer") || (str == "ComboBox.listRenderer")) {
/*  740 */       return true;
/*      */     }
/*  742 */     return false;
/*      */   }
/*      */ 
/*      */   public Object get(SynthContext paramSynthContext, Object paramObject)
/*      */   {
/*  748 */     String str1 = (String)CLASS_SPECIFIC_MAP.get(paramObject);
/*  749 */     if (str1 != null) {
/*  750 */       Object localObject1 = getClassSpecificValue(str1);
/*  751 */       if (localObject1 != null) {
/*  752 */         return localObject1;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  757 */     if (paramObject == "ScrollPane.viewportBorderInsets") {
/*  758 */       return getThicknessInsets(paramSynthContext, new Insets(0, 0, 0, 0));
/*      */     }
/*  760 */     if (paramObject == "Slider.tickColor") {
/*  761 */       return getColorForState(paramSynthContext, ColorType.FOREGROUND);
/*      */     }
/*  763 */     if (paramObject == "ScrollBar.minimumThumbSize") {
/*  764 */       int i = getClassSpecificIntValue(paramSynthContext, "min-slider-length", 21);
/*      */ 
/*  766 */       JScrollBar localJScrollBar = (JScrollBar)paramSynthContext.getComponent();
/*  767 */       if (localJScrollBar.getOrientation() == 0) {
/*  768 */         return new DimensionUIResource(i, 0);
/*      */       }
/*  770 */       return new DimensionUIResource(0, i);
/*      */     }
/*      */ 
/*  773 */     if (paramObject == "Separator.thickness") {
/*  774 */       JSeparator localJSeparator = (JSeparator)paramSynthContext.getComponent();
/*  775 */       if (localJSeparator.getOrientation() == 0) {
/*  776 */         return Integer.valueOf(getYThickness());
/*      */       }
/*  778 */       return Integer.valueOf(getXThickness());
/*      */     }
/*      */ 
/*  781 */     if (paramObject == "ToolBar.separatorSize") {
/*  782 */       int j = getClassSpecificIntValue(GTKEngine.WidgetType.TOOL_BAR, "space-size", 12);
/*      */ 
/*  784 */       return new DimensionUIResource(j, j);
/*      */     }
/*      */     Object localObject2;
/*  786 */     if (paramObject == "ScrollBar.buttonSize") {
/*  787 */       localObject2 = (JScrollBar)paramSynthContext.getComponent().getParent();
/*  788 */       int m = ((JScrollBar)localObject2).getOrientation() == 0 ? 1 : 0;
/*  789 */       GTKEngine.WidgetType localWidgetType = m != 0 ? GTKEngine.WidgetType.HSCROLL_BAR : GTKEngine.WidgetType.VSCROLL_BAR;
/*      */ 
/*  791 */       int i2 = getClassSpecificIntValue(localWidgetType, "slider-width", 14);
/*  792 */       int i3 = getClassSpecificIntValue(localWidgetType, "stepper-size", 14);
/*  793 */       return m != 0 ? new DimensionUIResource(i3, i2) : new DimensionUIResource(i2, i3);
/*      */     }
/*      */     int i1;
/*  797 */     if (paramObject == "ArrowButton.size") {
/*  798 */       localObject2 = paramSynthContext.getComponent().getName();
/*  799 */       if ((localObject2 != null) && (((String)localObject2).startsWith("Spinner")))
/*      */       {
/*      */         String str2;
/*  805 */         synchronized (UNIXToolkit.GTK_LOCK) {
/*  806 */           str2 = nativeGetPangoFontName(GTKEngine.WidgetType.SPINNER.ordinal());
/*      */         }
/*      */ 
/*  809 */         i1 = str2 != null ? PangoFonts.getFontSize(str2) : 10;
/*      */ 
/*  811 */         return Integer.valueOf(i1 + getXThickness() * 2);
/*      */       }
/*      */ 
/*      */     }
/*  818 */     else if (("CheckBox.iconTextGap".equals(paramObject)) || ("RadioButton.iconTextGap".equals(paramObject)))
/*      */     {
/*  825 */       int k = getClassSpecificIntValue(paramSynthContext, "indicator-spacing", 2);
/*      */ 
/*  827 */       int n = getClassSpecificIntValue(paramSynthContext, "focus-line-width", 1);
/*      */ 
/*  829 */       i1 = getClassSpecificIntValue(paramSynthContext, "focus-padding", 1);
/*      */ 
/*  831 */       return Integer.valueOf(k + n + i1);
/*      */     }
/*      */ 
/*  835 */     GTKStockIcon localGTKStockIcon = null;
/*  836 */     synchronized (ICONS_MAP) {
/*  837 */       localGTKStockIcon = (GTKStockIcon)ICONS_MAP.get(paramObject);
/*      */     }
/*      */ 
/*  840 */     if (localGTKStockIcon != null) {
/*  841 */       return localGTKStockIcon;
/*      */     }
/*      */ 
/*  845 */     if (paramObject != "engine")
/*      */     {
/*  849 */       ??? = UIManager.get(paramObject);
/*  850 */       if (paramObject == "Table.rowHeight") {
/*  851 */         i1 = getClassSpecificIntValue(paramSynthContext, "focus-line-width", 0);
/*      */ 
/*  853 */         if ((??? == null) && (i1 > 0)) {
/*  854 */           ??? = Integer.valueOf(16 + 2 * i1);
/*      */         }
/*      */       }
/*  857 */       return ???;
/*      */     }
/*      */ 
/*  862 */     return null;
/*      */   }
/*      */ 
/*      */   private Icon getStockIcon(SynthContext paramSynthContext, String paramString, int paramInt) {
/*  866 */     GTKConstants.TextDirection localTextDirection = GTKConstants.TextDirection.LTR;
/*      */ 
/*  868 */     if (paramSynthContext != null) {
/*  869 */       localObject = paramSynthContext.getComponent().getComponentOrientation();
/*      */ 
/*  872 */       if ((localObject != null) && (!((ComponentOrientation)localObject).isLeftToRight())) {
/*  873 */         localTextDirection = GTKConstants.TextDirection.RTL;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  879 */     Object localObject = getStyleSpecificIcon(paramString, localTextDirection, paramInt);
/*  880 */     if (localObject != null) {
/*  881 */       return localObject;
/*      */     }
/*      */ 
/*  886 */     String str = "gtk.icon." + paramString + '.' + paramInt + '.' + (localTextDirection == GTKConstants.TextDirection.RTL ? "rtl" : "ltr");
/*      */ 
/*  888 */     Image localImage = (Image)Toolkit.getDefaultToolkit().getDesktopProperty(str);
/*      */ 
/*  890 */     if (localImage != null) {
/*  891 */       return new ImageIcon(localImage);
/*      */     }
/*      */ 
/*  897 */     return null;
/*      */   }
/*      */ 
/*      */   private Icon getStyleSpecificIcon(String paramString, GTKConstants.TextDirection paramTextDirection, int paramInt)
/*      */   {
/*  903 */     UNIXToolkit localUNIXToolkit = (UNIXToolkit)Toolkit.getDefaultToolkit();
/*  904 */     BufferedImage localBufferedImage = localUNIXToolkit.getStockIcon(this.widgetType, paramString, paramInt, paramTextDirection.ordinal(), null);
/*      */ 
/*  906 */     return localBufferedImage != null ? new ImageIcon(localBufferedImage) : null;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/* 1110 */     CLASS_SPECIFIC_MAP.put("Slider.thumbHeight", "slider-width");
/* 1111 */     CLASS_SPECIFIC_MAP.put("Slider.trackBorder", "trough-border");
/* 1112 */     CLASS_SPECIFIC_MAP.put("SplitPane.size", "handle-size");
/* 1113 */     CLASS_SPECIFIC_MAP.put("Tree.expanderSize", "expander-size");
/* 1114 */     CLASS_SPECIFIC_MAP.put("ScrollBar.thumbHeight", "slider-width");
/* 1115 */     CLASS_SPECIFIC_MAP.put("ScrollBar.width", "slider-width");
/* 1116 */     CLASS_SPECIFIC_MAP.put("TextArea.caretForeground", "cursor-color");
/* 1117 */     CLASS_SPECIFIC_MAP.put("TextArea.caretAspectRatio", "cursor-aspect-ratio");
/* 1118 */     CLASS_SPECIFIC_MAP.put("TextField.caretForeground", "cursor-color");
/* 1119 */     CLASS_SPECIFIC_MAP.put("TextField.caretAspectRatio", "cursor-aspect-ratio");
/* 1120 */     CLASS_SPECIFIC_MAP.put("PasswordField.caretForeground", "cursor-color");
/* 1121 */     CLASS_SPECIFIC_MAP.put("PasswordField.caretAspectRatio", "cursor-aspect-ratio");
/* 1122 */     CLASS_SPECIFIC_MAP.put("FormattedTextField.caretForeground", "cursor-color");
/* 1123 */     CLASS_SPECIFIC_MAP.put("FormattedTextField.caretAspectRatio", "cursor-aspect-");
/* 1124 */     CLASS_SPECIFIC_MAP.put("TextPane.caretForeground", "cursor-color");
/* 1125 */     CLASS_SPECIFIC_MAP.put("TextPane.caretAspectRatio", "cursor-aspect-ratio");
/* 1126 */     CLASS_SPECIFIC_MAP.put("EditorPane.caretForeground", "cursor-color");
/* 1127 */     CLASS_SPECIFIC_MAP.put("EditorPane.caretAspectRatio", "cursor-aspect-ratio");
/*      */ 
/* 1129 */     ICONS_MAP = new HashMap();
/* 1130 */     ICONS_MAP.put("FileChooser.cancelIcon", new GTKStockIcon("gtk-cancel", 4));
/* 1131 */     ICONS_MAP.put("FileChooser.okIcon", new GTKStockIcon("gtk-ok", 4));
/* 1132 */     ICONS_MAP.put("OptionPane.errorIcon", new GTKStockIcon("gtk-dialog-error", 6));
/* 1133 */     ICONS_MAP.put("OptionPane.informationIcon", new GTKStockIcon("gtk-dialog-info", 6));
/* 1134 */     ICONS_MAP.put("OptionPane.warningIcon", new GTKStockIcon("gtk-dialog-warning", 6));
/* 1135 */     ICONS_MAP.put("OptionPane.questionIcon", new GTKStockIcon("gtk-dialog-question", 6));
/* 1136 */     ICONS_MAP.put("OptionPane.yesIcon", new GTKStockIcon("gtk-yes", 4));
/* 1137 */     ICONS_MAP.put("OptionPane.noIcon", new GTKStockIcon("gtk-no", 4));
/* 1138 */     ICONS_MAP.put("OptionPane.cancelIcon", new GTKStockIcon("gtk-cancel", 4));
/* 1139 */     ICONS_MAP.put("OptionPane.okIcon", new GTKStockIcon("gtk-ok", 4));
/*      */   }
/*      */ 
/*      */   static class GTKLazyValue
/*      */     implements UIDefaults.LazyValue
/*      */   {
/*      */     private String className;
/*      */     private String methodName;
/*      */ 
/*      */     GTKLazyValue(String paramString)
/*      */     {
/* 1079 */       this(paramString, null);
/*      */     }
/*      */ 
/*      */     GTKLazyValue(String paramString1, String paramString2) {
/* 1083 */       this.className = paramString1;
/* 1084 */       this.methodName = paramString2;
/*      */     }
/*      */ 
/*      */     public Object createValue(UIDefaults paramUIDefaults) {
/*      */       try {
/* 1089 */         Class localClass = Class.forName(this.className, true, Thread.currentThread().getContextClassLoader());
/*      */ 
/* 1092 */         if (this.methodName == null) {
/* 1093 */           return localClass.newInstance();
/*      */         }
/* 1095 */         Method localMethod = localClass.getMethod(this.methodName, (Class[])null);
/*      */ 
/* 1097 */         return localMethod.invoke(localClass, (Object[])null);
/*      */       } catch (ClassNotFoundException localClassNotFoundException) {
/*      */       } catch (IllegalAccessException localIllegalAccessException) {
/*      */       } catch (InvocationTargetException localInvocationTargetException) {
/*      */       } catch (NoSuchMethodException localNoSuchMethodException) {
/*      */       } catch (InstantiationException localInstantiationException) {
/*      */       }
/* 1104 */       return null;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class GTKStockIcon extends SynthIcon
/*      */   {
/*      */     private String key;
/*      */     private int size;
/*      */     private boolean loadedLTR;
/*      */     private boolean loadedRTL;
/*      */     private Icon ltrIcon;
/*      */     private Icon rtlIcon;
/*      */     private SynthStyle style;
/*      */ 
/*      */     GTKStockIcon(String paramString, int paramInt)
/*      */     {
/* 1001 */       this.key = paramString;
/* 1002 */       this.size = paramInt;
/*      */     }
/*      */ 
/*      */     public void paintIcon(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */     {
/* 1007 */       Icon localIcon = getIcon(paramSynthContext);
/*      */ 
/* 1009 */       if (localIcon != null)
/* 1010 */         if (paramSynthContext == null) {
/* 1011 */           localIcon.paintIcon(null, paramGraphics, paramInt1, paramInt2);
/*      */         }
/*      */         else
/* 1014 */           localIcon.paintIcon(paramSynthContext.getComponent(), paramGraphics, paramInt1, paramInt2);
/*      */     }
/*      */ 
/*      */     public int getIconWidth(SynthContext paramSynthContext)
/*      */     {
/* 1020 */       Icon localIcon = getIcon(paramSynthContext);
/*      */ 
/* 1022 */       if (localIcon != null) {
/* 1023 */         return localIcon.getIconWidth();
/*      */       }
/* 1025 */       return 0;
/*      */     }
/*      */ 
/*      */     public int getIconHeight(SynthContext paramSynthContext) {
/* 1029 */       Icon localIcon = getIcon(paramSynthContext);
/*      */ 
/* 1031 */       if (localIcon != null) {
/* 1032 */         return localIcon.getIconHeight();
/*      */       }
/* 1034 */       return 0;
/*      */     }
/*      */ 
/*      */     private Icon getIcon(SynthContext paramSynthContext) {
/* 1038 */       if (paramSynthContext != null) {
/* 1039 */         ComponentOrientation localComponentOrientation = paramSynthContext.getComponent().getComponentOrientation();
/*      */ 
/* 1041 */         SynthStyle localSynthStyle = paramSynthContext.getStyle();
/*      */ 
/* 1043 */         if (localSynthStyle != this.style) {
/* 1044 */           this.style = localSynthStyle;
/* 1045 */           this.loadedLTR = (this.loadedRTL = 0);
/*      */         }
/* 1047 */         if ((localComponentOrientation == null) || (localComponentOrientation.isLeftToRight())) {
/* 1048 */           if (!this.loadedLTR) {
/* 1049 */             this.loadedLTR = true;
/* 1050 */             this.ltrIcon = ((GTKStyle)paramSynthContext.getStyle()).getStockIcon(paramSynthContext, this.key, this.size);
/*      */           }
/*      */ 
/* 1053 */           return this.ltrIcon;
/*      */         }
/* 1055 */         if (!this.loadedRTL) {
/* 1056 */           this.loadedRTL = true;
/* 1057 */           this.rtlIcon = ((GTKStyle)paramSynthContext.getStyle()).getStockIcon(paramSynthContext, this.key, this.size);
/*      */         }
/*      */ 
/* 1060 */         return this.rtlIcon;
/*      */       }
/* 1062 */       return this.ltrIcon;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class GTKStockIconInfo
/*      */   {
/*      */     private static Map<String, Integer> ICON_TYPE_MAP;
/*  911 */     private static final Object ICON_SIZE_KEY = new StringBuffer("IconSize");
/*      */ 
/*      */     private static Dimension[] getIconSizesMap() {
/*  914 */       AppContext localAppContext = AppContext.getAppContext();
/*  915 */       Dimension[] arrayOfDimension = (Dimension[])localAppContext.get(ICON_SIZE_KEY);
/*      */ 
/*  917 */       if (arrayOfDimension == null) {
/*  918 */         arrayOfDimension = new Dimension[7];
/*  919 */         arrayOfDimension[0] = null;
/*  920 */         arrayOfDimension[1] = new Dimension(16, 16);
/*  921 */         arrayOfDimension[2] = new Dimension(18, 18);
/*  922 */         arrayOfDimension[3] = new Dimension(24, 24);
/*  923 */         arrayOfDimension[4] = new Dimension(20, 20);
/*  924 */         arrayOfDimension[5] = new Dimension(32, 32);
/*  925 */         arrayOfDimension[6] = new Dimension(48, 48);
/*  926 */         localAppContext.put(ICON_SIZE_KEY, arrayOfDimension);
/*      */       }
/*  928 */       return arrayOfDimension;
/*      */     }
/*      */ 
/*      */     public static Dimension getIconSize(int paramInt)
/*      */     {
/*  938 */       Dimension[] arrayOfDimension = getIconSizesMap();
/*  939 */       return (paramInt >= 0) && (paramInt < arrayOfDimension.length) ? arrayOfDimension[paramInt] : null;
/*      */     }
/*      */ 
/*      */     public static void setIconSize(int paramInt1, int paramInt2, int paramInt3)
/*      */     {
/*  952 */       Dimension[] arrayOfDimension = getIconSizesMap();
/*  953 */       if ((paramInt1 >= 0) && (paramInt1 < arrayOfDimension.length))
/*  954 */         arrayOfDimension[paramInt1] = new Dimension(paramInt2, paramInt3);
/*      */     }
/*      */ 
/*      */     public static int getIconType(String paramString)
/*      */     {
/*  966 */       if (paramString == null) {
/*  967 */         return -100;
/*      */       }
/*  969 */       if (ICON_TYPE_MAP == null) {
/*  970 */         initIconTypeMap();
/*      */       }
/*  972 */       Integer localInteger = (Integer)ICON_TYPE_MAP.get(paramString);
/*  973 */       return localInteger != null ? localInteger.intValue() : -100;
/*      */     }
/*      */ 
/*      */     private static void initIconTypeMap() {
/*  977 */       ICON_TYPE_MAP = new HashMap();
/*  978 */       ICON_TYPE_MAP.put("gtk-menu", Integer.valueOf(1));
/*  979 */       ICON_TYPE_MAP.put("gtk-small-toolbar", Integer.valueOf(2));
/*  980 */       ICON_TYPE_MAP.put("gtk-large-toolbar", Integer.valueOf(3));
/*  981 */       ICON_TYPE_MAP.put("gtk-button", Integer.valueOf(4));
/*  982 */       ICON_TYPE_MAP.put("gtk-dnd", Integer.valueOf(5));
/*  983 */       ICON_TYPE_MAP.put("gtk-dialog", Integer.valueOf(6));
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.java.swing.plaf.gtk.GTKStyle
 * JD-Core Version:    0.6.2
 */