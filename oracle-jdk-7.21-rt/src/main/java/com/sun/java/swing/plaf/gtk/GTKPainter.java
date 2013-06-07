/*      */ package com.sun.java.swing.plaf.gtk;
/*      */ 
/*      */ import java.awt.Component;
/*      */ import java.awt.ComponentOrientation;
/*      */ import java.awt.Container;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.Insets;
/*      */ import java.awt.Rectangle;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import javax.swing.AbstractButton;
/*      */ import javax.swing.JButton;
/*      */ import javax.swing.JComboBox;
/*      */ import javax.swing.JComponent;
/*      */ import javax.swing.JLabel;
/*      */ import javax.swing.JScrollBar;
/*      */ import javax.swing.JSlider;
/*      */ import javax.swing.JSplitPane;
/*      */ import javax.swing.JTabbedPane;
/*      */ import javax.swing.JToggleButton;
/*      */ import javax.swing.JToolBar;
/*      */ import javax.swing.JToolBar.Separator;
/*      */ import javax.swing.ListCellRenderer;
/*      */ import javax.swing.border.AbstractBorder;
/*      */ import javax.swing.plaf.LabelUI;
/*      */ import javax.swing.plaf.UIResource;
/*      */ import javax.swing.plaf.synth.ColorType;
/*      */ import javax.swing.plaf.synth.Region;
/*      */ import javax.swing.plaf.synth.SynthContext;
/*      */ import javax.swing.plaf.synth.SynthLookAndFeel;
/*      */ import javax.swing.plaf.synth.SynthPainter;
/*      */ import javax.swing.plaf.synth.SynthStyle;
/*      */ import javax.swing.plaf.synth.SynthUI;
/*      */ import sun.awt.UNIXToolkit;
/*      */ 
/*      */ class GTKPainter extends SynthPainter
/*      */ {
/*   56 */   private static final GTKConstants.PositionType[] POSITIONS = { GTKConstants.PositionType.BOTTOM, GTKConstants.PositionType.RIGHT, GTKConstants.PositionType.TOP, GTKConstants.PositionType.LEFT };
/*      */ 
/*   61 */   private static final GTKConstants.ShadowType[] SHADOWS = { GTKConstants.ShadowType.NONE, GTKConstants.ShadowType.IN, GTKConstants.ShadowType.OUT, GTKConstants.ShadowType.ETCHED_IN, GTKConstants.ShadowType.OUT };
/*      */ 
/*   66 */   private static final GTKEngine ENGINE = GTKEngine.INSTANCE;
/*   67 */   static final GTKPainter INSTANCE = new GTKPainter();
/*      */ 
/*      */   private String getName(SynthContext paramSynthContext)
/*      */   {
/*   73 */     return paramSynthContext.getRegion().isSubregion() ? null : paramSynthContext.getComponent().getName();
/*      */   }
/*      */ 
/*      */   public void paintCheckBoxBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*   79 */     paintRadioButtonBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */   }
/*      */ 
/*      */   public void paintCheckBoxMenuItemBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*   84 */     paintRadioButtonMenuItemBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */   }
/*      */ 
/*      */   public void paintFormattedTextFieldBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*   91 */     paintTextBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */   }
/*      */ 
/*      */   public void paintToolBarDragWindowBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  100 */     paintToolBarBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */   }
/*      */ 
/*      */   public void paintToolBarBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  110 */     Region localRegion = paramSynthContext.getRegion();
/*  111 */     int i = paramSynthContext.getComponentState();
/*  112 */     int j = GTKLookAndFeel.synthStateToGTKState(localRegion, i);
/*  113 */     int k = ((JToolBar)paramSynthContext.getComponent()).getOrientation();
/*  114 */     synchronized (UNIXToolkit.GTK_LOCK) {
/*  115 */       if (!ENGINE.paintCachedImage(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localRegion, Integer.valueOf(i), Integer.valueOf(k) }))
/*      */       {
/*  118 */         ENGINE.startPainting(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localRegion, Integer.valueOf(i), Integer.valueOf(k) });
/*  119 */         ENGINE.paintBox(paramGraphics, paramSynthContext, localRegion, j, GTKConstants.ShadowType.OUT, "handlebox_bin", paramInt1, paramInt2, paramInt3, paramInt4);
/*      */ 
/*  121 */         ENGINE.finishPainting();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void paintToolBarContentBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  129 */     Region localRegion = paramSynthContext.getRegion();
/*  130 */     int i = ((JToolBar)paramSynthContext.getComponent()).getOrientation();
/*  131 */     synchronized (UNIXToolkit.GTK_LOCK) {
/*  132 */       if (!ENGINE.paintCachedImage(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localRegion, Integer.valueOf(i) })) {
/*  133 */         ENGINE.startPainting(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localRegion, Integer.valueOf(i) });
/*  134 */         ENGINE.paintBox(paramGraphics, paramSynthContext, localRegion, 1, GTKConstants.ShadowType.OUT, "toolbar", paramInt1, paramInt2, paramInt3, paramInt4);
/*      */ 
/*  136 */         ENGINE.finishPainting();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void paintPasswordFieldBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  147 */     paintTextBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */   }
/*      */ 
/*      */   public void paintTextFieldBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  155 */     if (getName(paramSynthContext) == "Tree.cellEditor")
/*  156 */       paintTreeCellEditorBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */     else
/*  158 */       paintTextBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */   }
/*      */ 
/*      */   public void paintRadioButtonBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  169 */     Region localRegion = paramSynthContext.getRegion();
/*  170 */     int i = GTKLookAndFeel.synthStateToGTKState(localRegion, paramSynthContext.getComponentState());
/*      */ 
/*  172 */     if (i == 2)
/*  173 */       synchronized (UNIXToolkit.GTK_LOCK) {
/*  174 */         if (!ENGINE.paintCachedImage(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localRegion })) {
/*  175 */           ENGINE.startPainting(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localRegion });
/*  176 */           ENGINE.paintFlatBox(paramGraphics, paramSynthContext, localRegion, 2, GTKConstants.ShadowType.ETCHED_OUT, "checkbutton", paramInt1, paramInt2, paramInt3, paramInt4, ColorType.BACKGROUND);
/*      */ 
/*  179 */           ENGINE.finishPainting();
/*      */         }
/*      */       }
/*      */   }
/*      */ 
/*      */   public void paintRadioButtonMenuItemBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  192 */     Region localRegion = paramSynthContext.getRegion();
/*  193 */     int i = GTKLookAndFeel.synthStateToGTKState(localRegion, paramSynthContext.getComponentState());
/*      */ 
/*  195 */     if (i == 2)
/*  196 */       synchronized (UNIXToolkit.GTK_LOCK) {
/*  197 */         if (!ENGINE.paintCachedImage(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localRegion })) {
/*  198 */           GTKConstants.ShadowType localShadowType = GTKLookAndFeel.is2_2() ? GTKConstants.ShadowType.NONE : GTKConstants.ShadowType.OUT;
/*      */ 
/*  200 */           ENGINE.startPainting(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localRegion });
/*  201 */           ENGINE.paintBox(paramGraphics, paramSynthContext, localRegion, i, localShadowType, "menuitem", paramInt1, paramInt2, paramInt3, paramInt4);
/*      */ 
/*  203 */           ENGINE.finishPainting();
/*      */         }
/*      */       }
/*      */   }
/*      */ 
/*      */   public void paintLabelBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  215 */     String str = getName(paramSynthContext);
/*  216 */     JComponent localJComponent = paramSynthContext.getComponent();
/*  217 */     Container localContainer = localJComponent.getParent();
/*      */ 
/*  219 */     if ((str == "TableHeader.renderer") || (str == "GTKFileChooser.directoryListLabel") || (str == "GTKFileChooser.fileListLabel"))
/*      */     {
/*  223 */       paintButtonBackgroundImpl(paramSynthContext, paramGraphics, Region.BUTTON, "button", paramInt1, paramInt2, paramInt3, paramInt4, true, false, false, false);
/*      */     }
/*  231 */     else if (((localJComponent instanceof ListCellRenderer)) && (localContainer != null) && ((localContainer.getParent() instanceof JComboBox)))
/*      */     {
/*  234 */       paintTextBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void paintInternalFrameBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  244 */     Metacity.INSTANCE.paintFrameBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */   }
/*      */ 
/*      */   public void paintDesktopPaneBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  254 */     fillArea(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, ColorType.BACKGROUND);
/*      */   }
/*      */ 
/*      */   public void paintDesktopIconBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  263 */     Metacity.INSTANCE.paintFrameBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */   }
/*      */ 
/*      */   public void paintButtonBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  268 */     String str = getName(paramSynthContext);
/*  269 */     if ((str != null) && (str.startsWith("InternalFrameTitlePane."))) {
/*  270 */       Metacity.INSTANCE.paintButtonBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */     }
/*      */     else {
/*  273 */       AbstractButton localAbstractButton = (AbstractButton)paramSynthContext.getComponent();
/*  274 */       boolean bool1 = (localAbstractButton.isContentAreaFilled()) && (localAbstractButton.isBorderPainted());
/*      */ 
/*  276 */       boolean bool2 = localAbstractButton.isFocusPainted();
/*  277 */       boolean bool3 = ((localAbstractButton instanceof JButton)) && (((JButton)localAbstractButton).isDefaultCapable());
/*      */ 
/*  279 */       boolean bool4 = localAbstractButton.getParent() instanceof JToolBar;
/*  280 */       paintButtonBackgroundImpl(paramSynthContext, paramGraphics, Region.BUTTON, "button", paramInt1, paramInt2, paramInt3, paramInt4, bool1, bool2, bool3, bool4);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void paintButtonBackgroundImpl(SynthContext paramSynthContext, Graphics paramGraphics, Region paramRegion, String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
/*      */   {
/*  289 */     int i = paramSynthContext.getComponentState();
/*  290 */     synchronized (UNIXToolkit.GTK_LOCK) {
/*  291 */       if (ENGINE.paintCachedImage(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { paramRegion, Integer.valueOf(i), paramString, Boolean.valueOf(paramBoolean1), Boolean.valueOf(paramBoolean2), Boolean.valueOf(paramBoolean3), Boolean.valueOf(paramBoolean4) }))
/*      */       {
/*  293 */         return;
/*      */       }
/*  295 */       ENGINE.startPainting(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { paramRegion, Integer.valueOf(i), paramString, Boolean.valueOf(paramBoolean1), Boolean.valueOf(paramBoolean2), Boolean.valueOf(paramBoolean3), Boolean.valueOf(paramBoolean4) });
/*      */ 
/*  299 */       GTKStyle localGTKStyle = (GTKStyle)paramSynthContext.getStyle();
/*  300 */       if ((paramBoolean3) && (!paramBoolean4)) {
/*  301 */         Insets localInsets = localGTKStyle.getClassSpecificInsetsValue(paramSynthContext, "default-border", GTKStyle.BUTTON_DEFAULT_BORDER_INSETS);
/*      */ 
/*  305 */         if ((paramBoolean1) && ((i & 0x400) != 0)) {
/*  306 */           ENGINE.paintBox(paramGraphics, paramSynthContext, paramRegion, 1, GTKConstants.ShadowType.IN, "buttondefault", paramInt1, paramInt2, paramInt3, paramInt4);
/*      */         }
/*      */ 
/*  309 */         paramInt1 += localInsets.left;
/*  310 */         paramInt2 += localInsets.top;
/*  311 */         paramInt3 -= localInsets.left + localInsets.right;
/*  312 */         paramInt4 -= localInsets.top + localInsets.bottom;
/*      */       }
/*      */ 
/*  315 */       boolean bool = localGTKStyle.getClassSpecificBoolValue(paramSynthContext, "interior-focus", true);
/*      */ 
/*  317 */       int j = localGTKStyle.getClassSpecificIntValue(paramSynthContext, "focus-line-width", 1);
/*      */ 
/*  319 */       int k = localGTKStyle.getClassSpecificIntValue(paramSynthContext, "focus-padding", 1);
/*      */ 
/*  322 */       int m = j + k;
/*  323 */       int n = localGTKStyle.getXThickness();
/*  324 */       int i1 = localGTKStyle.getYThickness();
/*      */ 
/*  327 */       if ((!bool) && ((i & 0x100) == 256))
/*      */       {
/*  329 */         paramInt1 += m;
/*  330 */         paramInt2 += m;
/*  331 */         paramInt3 -= 2 * m;
/*  332 */         paramInt4 -= 2 * m;
/*      */       }
/*      */ 
/*  335 */       int i2 = GTKLookAndFeel.synthStateToGTKState(paramRegion, i);
/*      */       int i3;
/*  337 */       if (paramBoolean4)
/*      */       {
/*  340 */         i3 = (i2 != 1) && (i2 != 8) ? 1 : 0;
/*      */       }
/*      */       else
/*      */       {
/*  346 */         i3 = (paramBoolean1) || (i2 != 1) ? 1 : 0;
/*      */       }
/*      */ 
/*  350 */       if (i3 != 0) {
/*  351 */         GTKConstants.ShadowType localShadowType = GTKConstants.ShadowType.OUT;
/*  352 */         if ((i & 0x204) != 0)
/*      */         {
/*  354 */           localShadowType = GTKConstants.ShadowType.IN;
/*      */         }
/*  356 */         ENGINE.paintBox(paramGraphics, paramSynthContext, paramRegion, i2, localShadowType, paramString, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */       }
/*      */ 
/*  361 */       if ((paramBoolean2) && ((i & 0x100) != 0)) {
/*  362 */         if (bool) {
/*  363 */           paramInt1 += n + k;
/*  364 */           paramInt2 += i1 + k;
/*  365 */           paramInt3 -= 2 * (n + k);
/*  366 */           paramInt4 -= 2 * (i1 + k);
/*      */         } else {
/*  368 */           paramInt1 -= m;
/*  369 */           paramInt2 -= m;
/*  370 */           paramInt3 += 2 * m;
/*  371 */           paramInt4 += 2 * m;
/*      */         }
/*  373 */         ENGINE.paintFocus(paramGraphics, paramSynthContext, paramRegion, i2, paramString, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */       }
/*  375 */       ENGINE.finishPainting();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void paintArrowButtonForeground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
/*      */   {
/*  385 */     Region localRegion = paramSynthContext.getRegion();
/*  386 */     JComponent localJComponent = paramSynthContext.getComponent();
/*  387 */     String str1 = localJComponent.getName();
/*      */ 
/*  389 */     GTKConstants.ArrowType localArrowType = null;
/*  390 */     switch (paramInt5) {
/*      */     case 1:
/*  392 */       localArrowType = GTKConstants.ArrowType.UP; break;
/*      */     case 5:
/*  394 */       localArrowType = GTKConstants.ArrowType.DOWN; break;
/*      */     case 3:
/*  396 */       localArrowType = GTKConstants.ArrowType.RIGHT; break;
/*      */     case 7:
/*  398 */       localArrowType = GTKConstants.ArrowType.LEFT;
/*      */     case 2:
/*      */     case 4:
/*  401 */     case 6: } String str2 = "arrow";
/*  402 */     if ((str1 == "ScrollBar.button") || (str1 == "TabbedPane.button")) {
/*  403 */       if ((localArrowType == GTKConstants.ArrowType.UP) || (localArrowType == GTKConstants.ArrowType.DOWN))
/*  404 */         str2 = "vscrollbar";
/*      */       else
/*  406 */         str2 = "hscrollbar";
/*      */     }
/*  408 */     else if ((str1 == "Spinner.nextButton") || (str1 == "Spinner.previousButton"))
/*      */     {
/*  410 */       str2 = "spinbutton";
/*  411 */     } else if ((str1 != "ComboBox.arrowButton") && 
/*  412 */       (!$assertionsDisabled)) throw new AssertionError("unexpected name: " + str1);
/*      */ 
/*  415 */     int i = GTKLookAndFeel.synthStateToGTKState(localRegion, paramSynthContext.getComponentState());
/*      */ 
/*  417 */     GTKConstants.ShadowType localShadowType = i == 4 ? GTKConstants.ShadowType.IN : GTKConstants.ShadowType.OUT;
/*      */ 
/*  419 */     synchronized (UNIXToolkit.GTK_LOCK) {
/*  420 */       if (ENGINE.paintCachedImage(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { Integer.valueOf(i), str1, Integer.valueOf(paramInt5) }))
/*      */       {
/*  422 */         return;
/*      */       }
/*  424 */       ENGINE.startPainting(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { Integer.valueOf(i), str1, Integer.valueOf(paramInt5) });
/*  425 */       ENGINE.paintArrow(paramGraphics, paramSynthContext, localRegion, i, localShadowType, localArrowType, str2, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */ 
/*  427 */       ENGINE.finishPainting();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void paintArrowButtonBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  433 */     Region localRegion = paramSynthContext.getRegion();
/*  434 */     AbstractButton localAbstractButton = (AbstractButton)paramSynthContext.getComponent();
/*      */ 
/*  436 */     String str1 = localAbstractButton.getName();
/*  437 */     String str2 = "button";
/*  438 */     int i = 0;
/*  439 */     if ((str1 == "ScrollBar.button") || (str1 == "TabbedPane.button")) {
/*  440 */       Integer localInteger = (Integer)localAbstractButton.getClientProperty("__arrow_direction__");
/*      */ 
/*  442 */       i = localInteger != null ? localInteger.intValue() : 7;
/*      */ 
/*  444 */       switch (i) { case 2:
/*      */       case 3:
/*      */       case 4:
/*      */       case 6:
/*      */       case 7:
/*      */       default:
/*  448 */         str2 = "hscrollbar";
/*  449 */         break;
/*      */       case 1:
/*      */       case 5:
/*  452 */         str2 = "vscrollbar";
/*      */       }
/*      */     }
/*  455 */     else if (str1 == "Spinner.previousButton") {
/*  456 */       str2 = "spinbutton_down";
/*  457 */     } else if (str1 == "Spinner.nextButton") {
/*  458 */       str2 = "spinbutton_up";
/*  459 */     } else if ((str1 != "ComboBox.arrowButton") && 
/*  460 */       (!$assertionsDisabled)) { throw new AssertionError("unexpected name: " + str1); }
/*      */ 
/*      */ 
/*  463 */     int j = paramSynthContext.getComponentState();
/*  464 */     synchronized (UNIXToolkit.GTK_LOCK) {
/*  465 */       if (ENGINE.paintCachedImage(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localRegion, Integer.valueOf(j), str2, Integer.valueOf(i) }))
/*      */       {
/*  468 */         return;
/*      */       }
/*  470 */       ENGINE.startPainting(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localRegion, Integer.valueOf(j), str2, Integer.valueOf(i) });
/*      */ 
/*  473 */       if (str2.startsWith("spin"))
/*      */       {
/*  481 */         k = localAbstractButton.getParent().isEnabled() ? 1 : 8;
/*      */ 
/*  483 */         int m = str2 == "spinbutton_up" ? paramInt2 : paramInt2 - paramInt4;
/*  484 */         int n = paramInt4 * 2;
/*  485 */         ENGINE.paintBox(paramGraphics, paramSynthContext, localRegion, k, GTKConstants.ShadowType.IN, "spinbutton", paramInt1, m, paramInt3, n);
/*      */       }
/*      */ 
/*  490 */       int k = GTKLookAndFeel.synthStateToGTKState(localRegion, j);
/*  491 */       GTKConstants.ShadowType localShadowType = GTKConstants.ShadowType.OUT;
/*  492 */       if ((k & 0x204) != 0)
/*      */       {
/*  495 */         localShadowType = GTKConstants.ShadowType.IN;
/*      */       }
/*  497 */       ENGINE.paintBox(paramGraphics, paramSynthContext, localRegion, k, localShadowType, str2, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */ 
/*  501 */       ENGINE.finishPainting();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void paintListBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  512 */     fillArea(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, GTKColorType.TEXT_BACKGROUND);
/*      */   }
/*      */ 
/*      */   public void paintMenuBarBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  517 */     Region localRegion = paramSynthContext.getRegion();
/*  518 */     synchronized (UNIXToolkit.GTK_LOCK) {
/*  519 */       if (ENGINE.paintCachedImage(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localRegion })) {
/*  520 */         return;
/*      */       }
/*  522 */       GTKStyle localGTKStyle = (GTKStyle)paramSynthContext.getStyle();
/*  523 */       int i = localGTKStyle.getClassSpecificIntValue(paramSynthContext, "shadow-type", 2);
/*      */ 
/*  525 */       GTKConstants.ShadowType localShadowType = SHADOWS[i];
/*  526 */       int j = GTKLookAndFeel.synthStateToGTKState(localRegion, paramSynthContext.getComponentState());
/*      */ 
/*  528 */       ENGINE.startPainting(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localRegion });
/*  529 */       ENGINE.paintBox(paramGraphics, paramSynthContext, localRegion, j, localShadowType, "menubar", paramInt1, paramInt2, paramInt3, paramInt4);
/*      */ 
/*  531 */       ENGINE.finishPainting();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void paintMenuBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  541 */     paintMenuItemBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */   }
/*      */ 
/*      */   public void paintMenuItemBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  548 */     int i = GTKLookAndFeel.synthStateToGTKState(paramSynthContext.getRegion(), paramSynthContext.getComponentState());
/*      */ 
/*  550 */     if (i == 2) {
/*  551 */       Region localRegion = Region.MENU_ITEM;
/*  552 */       synchronized (UNIXToolkit.GTK_LOCK) {
/*  553 */         if (!ENGINE.paintCachedImage(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localRegion })) {
/*  554 */           GTKConstants.ShadowType localShadowType = GTKLookAndFeel.is2_2() ? GTKConstants.ShadowType.NONE : GTKConstants.ShadowType.OUT;
/*      */ 
/*  556 */           ENGINE.startPainting(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localRegion });
/*  557 */           ENGINE.paintBox(paramGraphics, paramSynthContext, localRegion, i, localShadowType, "menuitem", paramInt1, paramInt2, paramInt3, paramInt4);
/*      */ 
/*  559 */           ENGINE.finishPainting();
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void paintPopupMenuBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  567 */     Region localRegion = paramSynthContext.getRegion();
/*  568 */     int i = GTKLookAndFeel.synthStateToGTKState(localRegion, paramSynthContext.getComponentState());
/*      */ 
/*  570 */     synchronized (UNIXToolkit.GTK_LOCK) {
/*  571 */       if (ENGINE.paintCachedImage(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localRegion, Integer.valueOf(i) })) {
/*  572 */         return;
/*      */       }
/*  574 */       ENGINE.startPainting(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localRegion, Integer.valueOf(i) });
/*  575 */       ENGINE.paintBox(paramGraphics, paramSynthContext, localRegion, i, GTKConstants.ShadowType.OUT, "menu", paramInt1, paramInt2, paramInt3, paramInt4);
/*      */ 
/*  578 */       GTKStyle localGTKStyle = (GTKStyle)paramSynthContext.getStyle();
/*  579 */       int j = localGTKStyle.getXThickness();
/*  580 */       int k = localGTKStyle.getYThickness();
/*  581 */       ENGINE.paintBackground(paramGraphics, paramSynthContext, localRegion, i, localGTKStyle.getGTKColor(paramSynthContext, i, GTKColorType.BACKGROUND), paramInt1 + j, paramInt2 + k, paramInt3 - j - j, paramInt4 - k - k);
/*      */ 
/*  585 */       ENGINE.finishPainting();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void paintProgressBarBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  592 */     Region localRegion = paramSynthContext.getRegion();
/*  593 */     synchronized (UNIXToolkit.GTK_LOCK) {
/*  594 */       if (!ENGINE.paintCachedImage(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localRegion })) {
/*  595 */         ENGINE.startPainting(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localRegion });
/*  596 */         ENGINE.paintBox(paramGraphics, paramSynthContext, localRegion, 1, GTKConstants.ShadowType.IN, "trough", paramInt1, paramInt2, paramInt3, paramInt4);
/*      */ 
/*  598 */         ENGINE.finishPainting();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void paintProgressBarForeground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
/*      */   {
/*  606 */     Region localRegion = paramSynthContext.getRegion();
/*  607 */     synchronized (UNIXToolkit.GTK_LOCK)
/*      */     {
/*  612 */       if ((paramInt3 <= 0) || (paramInt4 <= 0)) {
/*  613 */         return;
/*      */       }
/*  615 */       ENGINE.startPainting(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localRegion, "fg" });
/*  616 */       ENGINE.paintBox(paramGraphics, paramSynthContext, localRegion, 2, GTKConstants.ShadowType.OUT, "bar", paramInt1, paramInt2, paramInt3, paramInt4);
/*      */ 
/*  618 */       ENGINE.finishPainting(false);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void paintViewportBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  624 */     Region localRegion = paramSynthContext.getRegion();
/*  625 */     synchronized (UNIXToolkit.GTK_LOCK) {
/*  626 */       if (!ENGINE.paintCachedImage(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localRegion })) {
/*  627 */         ENGINE.startPainting(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localRegion });
/*  628 */         ENGINE.paintShadow(paramGraphics, paramSynthContext, localRegion, 1, GTKConstants.ShadowType.IN, "scrolled_window", paramInt1, paramInt2, paramInt3, paramInt4);
/*      */ 
/*  630 */         ENGINE.finishPainting();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void paintSeparatorBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
/*      */   {
/*  639 */     Region localRegion = paramSynthContext.getRegion();
/*  640 */     int i = paramSynthContext.getComponentState();
/*  641 */     JComponent localJComponent = paramSynthContext.getComponent();
/*      */     String str;
/*  654 */     if ((localJComponent instanceof JToolBar.Separator))
/*      */     {
/*  677 */       str = "toolbar";
/*  678 */       float f = 0.2F;
/*  679 */       JToolBar.Separator localSeparator = (JToolBar.Separator)localJComponent;
/*  680 */       Dimension localDimension = localSeparator.getSeparatorSize();
/*  681 */       GTKStyle localGTKStyle = (GTKStyle)paramSynthContext.getStyle();
/*  682 */       if (paramInt5 == 0) {
/*  683 */         paramInt1 += (int)(paramInt3 * f);
/*  684 */         paramInt3 -= (int)(paramInt3 * f * 2.0F);
/*  685 */         paramInt2 += (localDimension.height - localGTKStyle.getYThickness()) / 2;
/*      */       } else {
/*  687 */         paramInt2 += (int)(paramInt4 * f);
/*  688 */         paramInt4 -= (int)(paramInt4 * f * 2.0F);
/*  689 */         paramInt1 += (localDimension.width - localGTKStyle.getXThickness()) / 2;
/*      */       }
/*      */     }
/*      */     else {
/*  693 */       str = "separator";
/*  694 */       Insets localInsets = localJComponent.getInsets();
/*  695 */       paramInt1 += localInsets.left;
/*  696 */       paramInt2 += localInsets.top;
/*  697 */       if (paramInt5 == 0)
/*  698 */         paramInt3 -= localInsets.left + localInsets.right;
/*      */       else {
/*  700 */         paramInt4 -= localInsets.top + localInsets.bottom;
/*      */       }
/*      */     }
/*      */ 
/*  704 */     synchronized (UNIXToolkit.GTK_LOCK) {
/*  705 */       if (!ENGINE.paintCachedImage(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localRegion, Integer.valueOf(i), str, Integer.valueOf(paramInt5) }))
/*      */       {
/*  707 */         ENGINE.startPainting(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localRegion, Integer.valueOf(i), str, Integer.valueOf(paramInt5) });
/*      */ 
/*  709 */         if (paramInt5 == 0) {
/*  710 */           ENGINE.paintHline(paramGraphics, paramSynthContext, localRegion, i, str, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */         }
/*      */         else {
/*  713 */           ENGINE.paintVline(paramGraphics, paramSynthContext, localRegion, i, str, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */         }
/*      */ 
/*  716 */         ENGINE.finishPainting();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void paintSliderTrackBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  724 */     Region localRegion = paramSynthContext.getRegion();
/*  725 */     int i = paramSynthContext.getComponentState();
/*      */ 
/*  729 */     int j = (i & 0x100) != 0 ? 1 : 0;
/*  730 */     int k = 0;
/*  731 */     if (j != 0) {
/*  732 */       localObject1 = (GTKStyle)paramSynthContext.getStyle();
/*  733 */       k = ((GTKStyle)localObject1).getClassSpecificIntValue(paramSynthContext, "focus-line-width", 1) + ((GTKStyle)localObject1).getClassSpecificIntValue(paramSynthContext, "focus-padding", 1);
/*      */ 
/*  737 */       paramInt1 -= k;
/*  738 */       paramInt2 -= k;
/*  739 */       paramInt3 += k * 2;
/*  740 */       paramInt4 += k * 2;
/*      */     }
/*      */ 
/*  745 */     Object localObject1 = (JSlider)paramSynthContext.getComponent();
/*  746 */     double d1 = ((JSlider)localObject1).getValue();
/*  747 */     double d2 = ((JSlider)localObject1).getMinimum();
/*  748 */     double d3 = ((JSlider)localObject1).getMaximum();
/*  749 */     double d4 = 20.0D;
/*      */ 
/*  751 */     synchronized (UNIXToolkit.GTK_LOCK)
/*      */     {
/*  757 */       if ((paramInt3 <= 0) || (paramInt4 <= 0)) {
/*  758 */         return;
/*      */       }
/*  760 */       ENGINE.startPainting(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localRegion, Integer.valueOf(i), Double.valueOf(d1) });
/*  761 */       int m = GTKLookAndFeel.synthStateToGTKState(localRegion, i);
/*  762 */       ENGINE.setRangeValue(paramSynthContext, localRegion, d1, d2, d3, d4);
/*  763 */       ENGINE.paintBox(paramGraphics, paramSynthContext, localRegion, m, GTKConstants.ShadowType.IN, "trough", paramInt1 + k, paramInt2 + k, paramInt3 - 2 * k, paramInt4 - 2 * k);
/*      */ 
/*  766 */       if (j != 0) {
/*  767 */         ENGINE.paintFocus(paramGraphics, paramSynthContext, localRegion, 1, "trough", paramInt1, paramInt2, paramInt3, paramInt4);
/*      */       }
/*      */ 
/*  770 */       ENGINE.finishPainting(false);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void paintSliderThumbBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
/*      */   {
/*  776 */     Region localRegion = paramSynthContext.getRegion();
/*  777 */     int i = GTKLookAndFeel.synthStateToGTKState(localRegion, paramSynthContext.getComponentState());
/*      */ 
/*  779 */     synchronized (UNIXToolkit.GTK_LOCK) {
/*  780 */       if (!ENGINE.paintCachedImage(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localRegion, Integer.valueOf(i), Integer.valueOf(paramInt5) })) {
/*  781 */         GTKConstants.Orientation localOrientation = paramInt5 == 0 ? GTKConstants.Orientation.HORIZONTAL : GTKConstants.Orientation.VERTICAL;
/*      */ 
/*  783 */         String str = paramInt5 == 0 ? "hscale" : "vscale";
/*      */ 
/*  785 */         ENGINE.startPainting(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localRegion, Integer.valueOf(i), Integer.valueOf(paramInt5) });
/*  786 */         ENGINE.paintSlider(paramGraphics, paramSynthContext, localRegion, i, GTKConstants.ShadowType.OUT, str, paramInt1, paramInt2, paramInt3, paramInt4, localOrientation);
/*      */ 
/*  788 */         ENGINE.finishPainting();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void paintSpinnerBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void paintSplitPaneDividerBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  808 */     Region localRegion = paramSynthContext.getRegion();
/*  809 */     int i = GTKLookAndFeel.synthStateToGTKState(localRegion, paramSynthContext.getComponentState());
/*      */ 
/*  811 */     JSplitPane localJSplitPane = (JSplitPane)paramSynthContext.getComponent();
/*  812 */     GTKConstants.Orientation localOrientation = localJSplitPane.getOrientation() == 1 ? GTKConstants.Orientation.VERTICAL : GTKConstants.Orientation.HORIZONTAL;
/*      */ 
/*  815 */     synchronized (UNIXToolkit.GTK_LOCK) {
/*  816 */       if (!ENGINE.paintCachedImage(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localRegion, Integer.valueOf(i), localOrientation }))
/*      */       {
/*  818 */         ENGINE.startPainting(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localRegion, Integer.valueOf(i), localOrientation });
/*  819 */         ENGINE.paintHandle(paramGraphics, paramSynthContext, localRegion, i, GTKConstants.ShadowType.OUT, "paned", paramInt1, paramInt2, paramInt3, paramInt4, localOrientation);
/*      */ 
/*  821 */         ENGINE.finishPainting();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void paintSplitPaneDragDivider(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
/*      */   {
/*  829 */     paintSplitPaneDividerForeground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
/*      */   }
/*      */ 
/*      */   public void paintTabbedPaneContentBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  834 */     JTabbedPane localJTabbedPane = (JTabbedPane)paramSynthContext.getComponent();
/*  835 */     int i = localJTabbedPane.getSelectedIndex();
/*  836 */     GTKConstants.PositionType localPositionType = GTKLookAndFeel.SwingOrientationConstantToGTK(localJTabbedPane.getTabPlacement());
/*      */ 
/*  839 */     int j = 0;
/*  840 */     int k = 0;
/*  841 */     if (i != -1) {
/*  842 */       localObject1 = localJTabbedPane.getBoundsAt(i);
/*      */ 
/*  844 */       if ((localPositionType == GTKConstants.PositionType.TOP) || (localPositionType == GTKConstants.PositionType.BOTTOM))
/*      */       {
/*  847 */         j = ((Rectangle)localObject1).x - paramInt1;
/*  848 */         k = ((Rectangle)localObject1).width;
/*      */       }
/*      */       else {
/*  851 */         j = ((Rectangle)localObject1).y - paramInt2;
/*  852 */         k = ((Rectangle)localObject1).height;
/*      */       }
/*      */     }
/*      */ 
/*  856 */     Object localObject1 = paramSynthContext.getRegion();
/*  857 */     int m = GTKLookAndFeel.synthStateToGTKState((Region)localObject1, paramSynthContext.getComponentState());
/*      */ 
/*  859 */     synchronized (UNIXToolkit.GTK_LOCK) {
/*  860 */       if (!ENGINE.paintCachedImage(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localObject1, Integer.valueOf(m), localPositionType, Integer.valueOf(j), Integer.valueOf(k) }))
/*      */       {
/*  862 */         ENGINE.startPainting(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localObject1, Integer.valueOf(m), localPositionType, Integer.valueOf(j), Integer.valueOf(k) });
/*      */ 
/*  864 */         ENGINE.paintBoxGap(paramGraphics, paramSynthContext, (Region)localObject1, m, GTKConstants.ShadowType.OUT, "notebook", paramInt1, paramInt2, paramInt3, paramInt4, localPositionType, j, k);
/*      */ 
/*  866 */         ENGINE.finishPainting();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void paintTabbedPaneTabBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
/*      */   {
/*  875 */     Region localRegion = paramSynthContext.getRegion();
/*  876 */     int i = paramSynthContext.getComponentState();
/*  877 */     int j = (i & 0x200) != 0 ? 1 : 4;
/*      */ 
/*  879 */     JTabbedPane localJTabbedPane = (JTabbedPane)paramSynthContext.getComponent();
/*  880 */     int k = localJTabbedPane.getTabPlacement();
/*      */ 
/*  882 */     synchronized (UNIXToolkit.GTK_LOCK) {
/*  883 */       if (!ENGINE.paintCachedImage(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localRegion, Integer.valueOf(j), Integer.valueOf(k), Integer.valueOf(paramInt5) }))
/*      */       {
/*  885 */         GTKConstants.PositionType localPositionType = POSITIONS[(k - 1)];
/*  886 */         ENGINE.startPainting(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localRegion, Integer.valueOf(j), Integer.valueOf(k), Integer.valueOf(paramInt5) });
/*      */ 
/*  888 */         ENGINE.paintExtension(paramGraphics, paramSynthContext, localRegion, j, GTKConstants.ShadowType.OUT, "tab", paramInt1, paramInt2, paramInt3, paramInt4, localPositionType, paramInt5);
/*      */ 
/*  890 */         ENGINE.finishPainting();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void paintTextPaneBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  900 */     paintTextAreaBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */   }
/*      */ 
/*      */   public void paintEditorPaneBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  908 */     paintTextAreaBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */   }
/*      */ 
/*      */   public void paintTextAreaBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  917 */     fillArea(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, GTKColorType.TEXT_BACKGROUND);
/*      */   }
/*      */ 
/*      */   private void paintTextBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  928 */     JComponent localJComponent = paramSynthContext.getComponent();
/*  929 */     GTKStyle localGTKStyle = (GTKStyle)paramSynthContext.getStyle();
/*  930 */     Region localRegion = paramSynthContext.getRegion();
/*  931 */     int i = paramSynthContext.getComponentState();
/*  932 */     synchronized (UNIXToolkit.GTK_LOCK) {
/*  933 */       if (ENGINE.paintCachedImage(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localRegion, Integer.valueOf(i) })) {
/*  934 */         return;
/*      */       }
/*      */ 
/*  937 */       int j = GTKLookAndFeel.synthStateToGTKState(localRegion, i);
/*  938 */       int k = 0;
/*  939 */       boolean bool = localGTKStyle.getClassSpecificBoolValue(paramSynthContext, "interior-focus", true);
/*      */ 
/*  941 */       if ((!bool) && ((i & 0x100) != 0)) {
/*  942 */         k = localGTKStyle.getClassSpecificIntValue(paramSynthContext, "focus-line-width", 1);
/*      */ 
/*  944 */         paramInt1 += k;
/*  945 */         paramInt2 += k;
/*  946 */         paramInt3 -= 2 * k;
/*  947 */         paramInt4 -= 2 * k;
/*      */       }
/*      */ 
/*  950 */       int m = localGTKStyle.getXThickness();
/*  951 */       int n = localGTKStyle.getYThickness();
/*      */ 
/*  953 */       ENGINE.startPainting(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localRegion, Integer.valueOf(i) });
/*  954 */       ENGINE.paintShadow(paramGraphics, paramSynthContext, localRegion, j, GTKConstants.ShadowType.IN, "entry", paramInt1, paramInt2, paramInt3, paramInt4);
/*      */ 
/*  956 */       ENGINE.paintFlatBox(paramGraphics, paramSynthContext, localRegion, j, GTKConstants.ShadowType.NONE, "entry_bg", paramInt1 + m, paramInt2 + n, paramInt3 - 2 * m, paramInt4 - 2 * n, ColorType.TEXT_BACKGROUND);
/*      */ 
/*  964 */       if (k > 0) {
/*  965 */         paramInt1 -= k;
/*  966 */         paramInt2 -= k;
/*  967 */         paramInt3 += 2 * k;
/*  968 */         paramInt4 += 2 * k;
/*  969 */         ENGINE.paintFocus(paramGraphics, paramSynthContext, localRegion, j, "entry", paramInt1, paramInt2, paramInt3, paramInt4);
/*      */       }
/*      */ 
/*  972 */       ENGINE.finishPainting();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void paintTreeCellEditorBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  978 */     Region localRegion = paramSynthContext.getRegion();
/*  979 */     int i = GTKLookAndFeel.synthStateToGTKState(localRegion, paramSynthContext.getComponentState());
/*      */ 
/*  981 */     synchronized (UNIXToolkit.GTK_LOCK) {
/*  982 */       if (!ENGINE.paintCachedImage(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localRegion, Integer.valueOf(i) })) {
/*  983 */         ENGINE.startPainting(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localRegion, Integer.valueOf(i) });
/*  984 */         ENGINE.paintFlatBox(paramGraphics, paramSynthContext, localRegion, i, GTKConstants.ShadowType.NONE, "entry_bg", paramInt1, paramInt2, paramInt3, paramInt4, ColorType.TEXT_BACKGROUND);
/*      */ 
/*  986 */         ENGINE.finishPainting();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void paintRootPaneBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  998 */     fillArea(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, GTKColorType.BACKGROUND);
/*      */   }
/*      */ 
/*      */   public void paintToggleButtonBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/* 1007 */     Region localRegion = paramSynthContext.getRegion();
/* 1008 */     JToggleButton localJToggleButton = (JToggleButton)paramSynthContext.getComponent();
/* 1009 */     boolean bool1 = (localJToggleButton.isContentAreaFilled()) && (localJToggleButton.isBorderPainted());
/*      */ 
/* 1011 */     boolean bool2 = localJToggleButton.isFocusPainted();
/* 1012 */     boolean bool3 = localJToggleButton.getParent() instanceof JToolBar;
/* 1013 */     paintButtonBackgroundImpl(paramSynthContext, paramGraphics, localRegion, "button", paramInt1, paramInt2, paramInt3, paramInt4, bool1, bool2, false, bool3);
/*      */   }
/*      */ 
/*      */   public void paintScrollBarBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/* 1025 */     Region localRegion = paramSynthContext.getRegion();
/* 1026 */     boolean bool = (paramSynthContext.getComponentState() & 0x100) != 0;
/*      */ 
/* 1028 */     synchronized (UNIXToolkit.GTK_LOCK) {
/* 1029 */       if (ENGINE.paintCachedImage(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localRegion, Boolean.valueOf(bool) })) {
/* 1030 */         return;
/*      */       }
/* 1032 */       ENGINE.startPainting(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localRegion, Boolean.valueOf(bool) });
/*      */ 
/* 1039 */       Insets localInsets = paramSynthContext.getComponent().getInsets();
/* 1040 */       GTKStyle localGTKStyle = (GTKStyle)paramSynthContext.getStyle();
/* 1041 */       int i = localGTKStyle.getClassSpecificIntValue(paramSynthContext, "trough-border", 1);
/*      */ 
/* 1043 */       localInsets.left -= i;
/* 1044 */       localInsets.right -= i;
/* 1045 */       localInsets.top -= i;
/* 1046 */       localInsets.bottom -= i;
/*      */ 
/* 1048 */       ENGINE.paintBox(paramGraphics, paramSynthContext, localRegion, 4, GTKConstants.ShadowType.IN, "trough", paramInt1 + localInsets.left, paramInt2 + localInsets.top, paramInt3 - localInsets.left - localInsets.right, paramInt4 - localInsets.top - localInsets.bottom);
/*      */ 
/* 1055 */       if (bool) {
/* 1056 */         ENGINE.paintFocus(paramGraphics, paramSynthContext, localRegion, 1, "trough", paramInt1, paramInt2, paramInt3, paramInt4);
/*      */       }
/*      */ 
/* 1059 */       ENGINE.finishPainting();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void paintScrollBarThumbBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
/*      */   {
/* 1069 */     Region localRegion = paramSynthContext.getRegion();
/* 1070 */     int i = GTKLookAndFeel.synthStateToGTKState(localRegion, paramSynthContext.getComponentState());
/*      */ 
/* 1082 */     JScrollBar localJScrollBar = (JScrollBar)paramSynthContext.getComponent();
/* 1083 */     boolean bool = (localJScrollBar.getOrientation() == 0) && (!localJScrollBar.getComponentOrientation().isLeftToRight());
/*      */ 
/* 1086 */     double d1 = 0.0D;
/* 1087 */     double d2 = 100.0D;
/* 1088 */     double d3 = 20.0D;
/*      */     double d4;
/* 1090 */     if (localJScrollBar.getMaximum() - localJScrollBar.getMinimum() == localJScrollBar.getVisibleAmount())
/*      */     {
/* 1093 */       d4 = 0.0D;
/* 1094 */       d3 = 100.0D;
/* 1095 */     } else if (localJScrollBar.getValue() == localJScrollBar.getMinimum())
/*      */     {
/* 1097 */       d4 = bool ? 100.0D : 0.0D;
/* 1098 */     } else if (localJScrollBar.getValue() >= localJScrollBar.getMaximum() - localJScrollBar.getVisibleAmount())
/*      */     {
/* 1100 */       d4 = bool ? 0.0D : 100.0D;
/*      */     }
/*      */     else {
/* 1103 */       d4 = 50.0D;
/*      */     }
/*      */ 
/* 1106 */     synchronized (UNIXToolkit.GTK_LOCK) {
/* 1107 */       if (!ENGINE.paintCachedImage(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localRegion, Integer.valueOf(i), Integer.valueOf(paramInt5), Double.valueOf(d4), Double.valueOf(d3), Boolean.valueOf(bool) }))
/*      */       {
/* 1110 */         ENGINE.startPainting(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localRegion, Integer.valueOf(i), Integer.valueOf(paramInt5), Double.valueOf(d4), Double.valueOf(d3), Boolean.valueOf(bool) });
/*      */ 
/* 1112 */         GTKConstants.Orientation localOrientation = paramInt5 == 0 ? GTKConstants.Orientation.HORIZONTAL : GTKConstants.Orientation.VERTICAL;
/*      */ 
/* 1114 */         ENGINE.setRangeValue(paramSynthContext, localRegion, d4, d1, d2, d3);
/* 1115 */         ENGINE.paintSlider(paramGraphics, paramSynthContext, localRegion, i, GTKConstants.ShadowType.OUT, "slider", paramInt1, paramInt2, paramInt3, paramInt4, localOrientation);
/*      */ 
/* 1117 */         ENGINE.finishPainting();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void paintToolTipBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/* 1127 */     Region localRegion = paramSynthContext.getRegion();
/* 1128 */     synchronized (UNIXToolkit.GTK_LOCK) {
/* 1129 */       if (!ENGINE.paintCachedImage(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localRegion })) {
/* 1130 */         ENGINE.startPainting(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localRegion });
/* 1131 */         ENGINE.paintFlatBox(paramGraphics, paramSynthContext, localRegion, 1, GTKConstants.ShadowType.OUT, "tooltip", paramInt1, paramInt2, paramInt3, paramInt4, ColorType.BACKGROUND);
/*      */ 
/* 1134 */         ENGINE.finishPainting();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void paintTreeCellBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/* 1145 */     Region localRegion = paramSynthContext.getRegion();
/* 1146 */     int i = paramSynthContext.getComponentState();
/* 1147 */     int j = GTKLookAndFeel.synthStateToGTKState(localRegion, i);
/* 1148 */     synchronized (UNIXToolkit.GTK_LOCK) {
/* 1149 */       if (!ENGINE.paintCachedImage(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localRegion, Integer.valueOf(i) })) {
/* 1150 */         ENGINE.startPainting(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localRegion, Integer.valueOf(i) });
/*      */ 
/* 1153 */         ENGINE.paintFlatBox(paramGraphics, paramSynthContext, localRegion, j, GTKConstants.ShadowType.NONE, "cell_odd", paramInt1, paramInt2, paramInt3, paramInt4, ColorType.TEXT_BACKGROUND);
/*      */ 
/* 1155 */         ENGINE.finishPainting();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void paintTreeCellFocus(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/* 1162 */     Region localRegion = Region.TREE_CELL;
/* 1163 */     int i = paramSynthContext.getComponentState();
/* 1164 */     paintFocus(paramSynthContext, paramGraphics, localRegion, i, "treeview", paramInt1, paramInt2, paramInt3, paramInt4);
/*      */   }
/*      */ 
/*      */   public void paintTreeBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/* 1174 */     fillArea(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, GTKColorType.TEXT_BACKGROUND);
/*      */   }
/*      */ 
/*      */   public void paintViewportBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/* 1187 */     fillArea(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, GTKColorType.TEXT_BACKGROUND);
/*      */   }
/*      */ 
/*      */   void paintFocus(SynthContext paramSynthContext, Graphics paramGraphics, Region paramRegion, int paramInt1, String paramString, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
/*      */   {
/* 1192 */     int i = GTKLookAndFeel.synthStateToGTKState(paramRegion, paramInt1);
/* 1193 */     synchronized (UNIXToolkit.GTK_LOCK) {
/* 1194 */       if (!ENGINE.paintCachedImage(paramGraphics, paramInt2, paramInt3, paramInt4, paramInt5, new Object[] { paramRegion, Integer.valueOf(i), "focus" })) {
/* 1195 */         ENGINE.startPainting(paramGraphics, paramInt2, paramInt3, paramInt4, paramInt5, new Object[] { paramRegion, Integer.valueOf(i), "focus" });
/* 1196 */         ENGINE.paintFocus(paramGraphics, paramSynthContext, paramRegion, i, paramString, paramInt2, paramInt3, paramInt4, paramInt5);
/* 1197 */         ENGINE.finishPainting();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   void paintMetacityElement(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, String paramString, int paramInt2, int paramInt3, int paramInt4, int paramInt5, GTKConstants.ShadowType paramShadowType, GTKConstants.ArrowType paramArrowType)
/*      */   {
/* 1205 */     synchronized (UNIXToolkit.GTK_LOCK) {
/* 1206 */       if (!ENGINE.paintCachedImage(paramGraphics, paramInt2, paramInt3, paramInt4, paramInt5, new Object[] { Integer.valueOf(paramInt1), paramString, paramShadowType, paramArrowType }))
/*      */       {
/* 1208 */         ENGINE.startPainting(paramGraphics, paramInt2, paramInt3, paramInt4, paramInt5, new Object[] { Integer.valueOf(paramInt1), paramString, paramShadowType, paramArrowType });
/*      */ 
/* 1210 */         if (paramString == "metacity-arrow") {
/* 1211 */           ENGINE.paintArrow(paramGraphics, paramSynthContext, Region.INTERNAL_FRAME_TITLE_PANE, paramInt1, paramShadowType, paramArrowType, "", paramInt2, paramInt3, paramInt4, paramInt5);
/*      */         }
/* 1214 */         else if (paramString == "metacity-box") {
/* 1215 */           ENGINE.paintBox(paramGraphics, paramSynthContext, Region.INTERNAL_FRAME_TITLE_PANE, paramInt1, paramShadowType, "", paramInt2, paramInt3, paramInt4, paramInt5);
/*      */         }
/* 1218 */         else if (paramString == "metacity-vline") {
/* 1219 */           ENGINE.paintVline(paramGraphics, paramSynthContext, Region.INTERNAL_FRAME_TITLE_PANE, paramInt1, "", paramInt2, paramInt3, paramInt4, paramInt5);
/*      */         }
/*      */ 
/* 1222 */         ENGINE.finishPainting();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   void paintIcon(SynthContext paramSynthContext, Graphics paramGraphics, Method paramMethod, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/* 1229 */     int i = paramSynthContext.getComponentState();
/* 1230 */     synchronized (UNIXToolkit.GTK_LOCK) {
/* 1231 */       if (!ENGINE.paintCachedImage(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { Integer.valueOf(i), paramMethod })) {
/* 1232 */         ENGINE.startPainting(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { Integer.valueOf(i), paramMethod });
/*      */         try {
/* 1234 */           paramMethod.invoke(this, new Object[] { paramSynthContext, paramGraphics, Integer.valueOf(i), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), Integer.valueOf(paramInt4) });
/*      */         } catch (IllegalAccessException localIllegalAccessException) {
/* 1236 */           if (!$assertionsDisabled) throw new AssertionError(); 
/*      */         }
/* 1238 */         catch (InvocationTargetException localInvocationTargetException) { if (!$assertionsDisabled) throw new AssertionError();
/*      */         }
/* 1240 */         ENGINE.finishPainting();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   void paintIcon(SynthContext paramSynthContext, Graphics paramGraphics, Method paramMethod, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object paramObject)
/*      */   {
/* 1247 */     int i = paramSynthContext.getComponentState();
/* 1248 */     synchronized (UNIXToolkit.GTK_LOCK) {
/* 1249 */       if (!ENGINE.paintCachedImage(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { Integer.valueOf(i), paramMethod, paramObject }))
/*      */       {
/* 1251 */         ENGINE.startPainting(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { Integer.valueOf(i), paramMethod, paramObject });
/*      */         try
/*      */         {
/* 1254 */           paramMethod.invoke(this, new Object[] { paramSynthContext, paramGraphics, Integer.valueOf(i), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), Integer.valueOf(paramInt4), paramObject });
/*      */         }
/*      */         catch (IllegalAccessException localIllegalAccessException) {
/* 1257 */           if (!$assertionsDisabled) throw new AssertionError(); 
/*      */         }
/* 1259 */         catch (InvocationTargetException localInvocationTargetException) { if (!$assertionsDisabled) throw new AssertionError();
/*      */         }
/* 1261 */         ENGINE.finishPainting();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void paintTreeExpandedIcon(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
/*      */   {
/* 1270 */     ENGINE.paintExpander(paramGraphics, paramSynthContext, Region.TREE, GTKLookAndFeel.synthStateToGTKState(paramSynthContext.getRegion(), paramInt1), GTKConstants.ExpanderStyle.EXPANDED, "treeview", paramInt2, paramInt3, paramInt4, paramInt5);
/*      */   }
/*      */ 
/*      */   public void paintTreeCollapsedIcon(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
/*      */   {
/* 1277 */     ENGINE.paintExpander(paramGraphics, paramSynthContext, Region.TREE, GTKLookAndFeel.synthStateToGTKState(paramSynthContext.getRegion(), paramInt1), GTKConstants.ExpanderStyle.COLLAPSED, "treeview", paramInt2, paramInt3, paramInt4, paramInt5);
/*      */   }
/*      */ 
/*      */   public void paintCheckBoxIcon(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
/*      */   {
/* 1284 */     GTKStyle localGTKStyle = (GTKStyle)paramSynthContext.getStyle();
/* 1285 */     int i = localGTKStyle.getClassSpecificIntValue(paramSynthContext, "indicator-size", 13);
/*      */ 
/* 1287 */     int j = localGTKStyle.getClassSpecificIntValue(paramSynthContext, "indicator-spacing", 2);
/*      */ 
/* 1290 */     ENGINE.paintCheck(paramGraphics, paramSynthContext, Region.CHECK_BOX, "checkbutton", paramInt2 + j, paramInt3 + j, i, i);
/*      */   }
/*      */ 
/*      */   public void paintRadioButtonIcon(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
/*      */   {
/* 1296 */     GTKStyle localGTKStyle = (GTKStyle)paramSynthContext.getStyle();
/* 1297 */     int i = localGTKStyle.getClassSpecificIntValue(paramSynthContext, "indicator-size", 13);
/*      */ 
/* 1299 */     int j = localGTKStyle.getClassSpecificIntValue(paramSynthContext, "indicator-spacing", 2);
/*      */ 
/* 1302 */     ENGINE.paintOption(paramGraphics, paramSynthContext, Region.RADIO_BUTTON, "radiobutton", paramInt2 + j, paramInt3 + j, i, i);
/*      */   }
/*      */ 
/*      */   public void paintMenuArrowIcon(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, GTKConstants.ArrowType paramArrowType)
/*      */   {
/* 1308 */     int i = GTKLookAndFeel.synthStateToGTKState(paramSynthContext.getRegion(), paramInt1);
/*      */ 
/* 1310 */     GTKConstants.ShadowType localShadowType = GTKConstants.ShadowType.OUT;
/* 1311 */     if (i == 2) {
/* 1312 */       localShadowType = GTKConstants.ShadowType.IN;
/*      */     }
/* 1314 */     ENGINE.paintArrow(paramGraphics, paramSynthContext, Region.MENU_ITEM, i, localShadowType, paramArrowType, "menuitem", paramInt2 + 3, paramInt3 + 3, 7, 7);
/*      */   }
/*      */ 
/*      */   public void paintCheckBoxMenuItemCheckIcon(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
/*      */   {
/* 1321 */     GTKStyle localGTKStyle = (GTKStyle)paramSynthContext.getStyle();
/* 1322 */     int i = localGTKStyle.getClassSpecificIntValue(paramSynthContext, "indicator-size", 12);
/*      */ 
/* 1325 */     ENGINE.paintCheck(paramGraphics, paramSynthContext, Region.CHECK_BOX_MENU_ITEM, "check", paramInt2 + 1, paramInt3 + 1, i, i);
/*      */   }
/*      */ 
/*      */   public void paintRadioButtonMenuItemCheckIcon(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
/*      */   {
/* 1334 */     GTKStyle localGTKStyle = (GTKStyle)paramSynthContext.getStyle();
/* 1335 */     int i = localGTKStyle.getClassSpecificIntValue(paramSynthContext, "indicator-size", 12);
/*      */ 
/* 1338 */     ENGINE.paintOption(paramGraphics, paramSynthContext, Region.RADIO_BUTTON_MENU_ITEM, "option", paramInt2 + 1, paramInt3 + 1, i, i);
/*      */   }
/*      */ 
/*      */   public void paintToolBarHandleIcon(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, GTKConstants.Orientation paramOrientation)
/*      */   {
/* 1346 */     int i = GTKLookAndFeel.synthStateToGTKState(paramSynthContext.getRegion(), paramInt1);
/*      */ 
/* 1354 */     paramOrientation = paramOrientation == GTKConstants.Orientation.HORIZONTAL ? GTKConstants.Orientation.VERTICAL : GTKConstants.Orientation.HORIZONTAL;
/*      */ 
/* 1357 */     ENGINE.paintHandle(paramGraphics, paramSynthContext, Region.TOOL_BAR, i, GTKConstants.ShadowType.OUT, "handlebox", paramInt2, paramInt3, paramInt4, paramInt5, paramOrientation);
/*      */   }
/*      */ 
/*      */   public void paintAscendingSortIcon(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
/*      */   {
/* 1363 */     ENGINE.paintArrow(paramGraphics, paramSynthContext, Region.TABLE, 1, GTKConstants.ShadowType.IN, GTKConstants.ArrowType.UP, "arrow", paramInt2, paramInt3, paramInt4, paramInt5);
/*      */   }
/*      */ 
/*      */   public void paintDescendingSortIcon(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
/*      */   {
/* 1369 */     ENGINE.paintArrow(paramGraphics, paramSynthContext, Region.TABLE, 1, GTKConstants.ShadowType.IN, GTKConstants.ArrowType.DOWN, "arrow", paramInt2, paramInt3, paramInt4, paramInt5);
/*      */   }
/*      */ 
/*      */   private void fillArea(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorType paramColorType)
/*      */   {
/* 1379 */     if (paramSynthContext.getComponent().isOpaque()) {
/* 1380 */       Region localRegion = paramSynthContext.getRegion();
/* 1381 */       int i = GTKLookAndFeel.synthStateToGTKState(localRegion, paramSynthContext.getComponentState());
/*      */ 
/* 1383 */       GTKStyle localGTKStyle = (GTKStyle)paramSynthContext.getStyle();
/*      */ 
/* 1385 */       paramGraphics.setColor(localGTKStyle.getGTKColor(paramSynthContext, i, paramColorType));
/* 1386 */       paramGraphics.fillRect(paramInt1, paramInt2, paramInt3, paramInt4);
/*      */     }
/*      */   }
/*      */ 
/*      */   static class ListTableFocusBorder extends AbstractBorder implements UIResource
/*      */   {
/*      */     private boolean selectedCell;
/*      */     private boolean focusedCell;
/*      */ 
/*      */     public static ListTableFocusBorder getSelectedCellBorder()
/*      */     {
/* 1398 */       return new ListTableFocusBorder(true, true);
/*      */     }
/*      */ 
/*      */     public static ListTableFocusBorder getUnselectedCellBorder() {
/* 1402 */       return new ListTableFocusBorder(false, true);
/*      */     }
/*      */ 
/*      */     public static ListTableFocusBorder getNoFocusCellBorder() {
/* 1406 */       return new ListTableFocusBorder(false, false);
/*      */     }
/*      */ 
/*      */     public ListTableFocusBorder(boolean paramBoolean1, boolean paramBoolean2) {
/* 1410 */       this.selectedCell = paramBoolean1;
/* 1411 */       this.focusedCell = paramBoolean2;
/*      */     }
/*      */ 
/*      */     private SynthContext getContext(Component paramComponent) {
/* 1415 */       SynthContext localSynthContext = null;
/*      */ 
/* 1417 */       LabelUI localLabelUI = null;
/* 1418 */       if ((paramComponent instanceof JLabel)) {
/* 1419 */         localLabelUI = ((JLabel)paramComponent).getUI();
/*      */       }
/*      */ 
/* 1422 */       if ((localLabelUI instanceof SynthUI)) {
/* 1423 */         localSynthContext = ((SynthUI)localLabelUI).getContext((JComponent)paramComponent);
/*      */       }
/*      */ 
/* 1426 */       return localSynthContext;
/*      */     }
/*      */ 
/*      */     public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */     {
/* 1431 */       if (this.focusedCell) {
/* 1432 */         SynthContext localSynthContext = getContext(paramComponent);
/* 1433 */         int i = this.selectedCell ? 512 : 257;
/*      */ 
/* 1436 */         if (localSynthContext != null)
/* 1437 */           GTKPainter.INSTANCE.paintFocus(localSynthContext, paramGraphics, Region.TABLE, i, "", paramInt1, paramInt2, paramInt3, paramInt4);
/*      */       }
/*      */     }
/*      */ 
/*      */     public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
/*      */     {
/* 1444 */       SynthContext localSynthContext = getContext(paramComponent);
/*      */ 
/* 1446 */       if (localSynthContext != null) {
/* 1447 */         paramInsets = localSynthContext.getStyle().getInsets(localSynthContext, paramInsets);
/*      */       }
/*      */ 
/* 1450 */       return paramInsets;
/*      */     }
/*      */ 
/*      */     public boolean isBorderOpaque() {
/* 1454 */       return true;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class TitledBorder extends AbstractBorder
/*      */     implements UIResource
/*      */   {
/*      */     public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */     {
/* 1463 */       SynthContext localSynthContext = getContext((JComponent)paramComponent);
/* 1464 */       Region localRegion = localSynthContext.getRegion();
/* 1465 */       int i = localSynthContext.getComponentState();
/* 1466 */       int j = GTKLookAndFeel.synthStateToGTKState(localRegion, i);
/*      */ 
/* 1468 */       synchronized (UNIXToolkit.GTK_LOCK) {
/* 1469 */         if (!GTKPainter.ENGINE.paintCachedImage(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localRegion })) {
/* 1470 */           GTKPainter.ENGINE.startPainting(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { localRegion });
/* 1471 */           GTKPainter.ENGINE.paintShadow(paramGraphics, localSynthContext, localRegion, j, GTKConstants.ShadowType.ETCHED_IN, "frame", paramInt1, paramInt2, paramInt3, paramInt4);
/*      */ 
/* 1473 */           GTKPainter.ENGINE.finishPainting();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     public Insets getBorderInsets(Component paramComponent, Insets paramInsets) {
/* 1479 */       SynthContext localSynthContext = getContext((JComponent)paramComponent);
/* 1480 */       return localSynthContext.getStyle().getInsets(localSynthContext, paramInsets);
/*      */     }
/*      */ 
/*      */     public boolean isBorderOpaque() {
/* 1484 */       return true;
/*      */     }
/*      */ 
/*      */     private SynthStyle getStyle(JComponent paramJComponent) {
/* 1488 */       return SynthLookAndFeel.getStyle(paramJComponent, GTKEngine.CustomRegion.TITLED_BORDER);
/*      */     }
/*      */ 
/*      */     private SynthContext getContext(JComponent paramJComponent) {
/* 1492 */       int i = 1024;
/* 1493 */       return new SynthContext(paramJComponent, GTKEngine.CustomRegion.TITLED_BORDER, getStyle(paramJComponent), i);
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.java.swing.plaf.gtk.GTKPainter
 * JD-Core Version:    0.6.2
 */