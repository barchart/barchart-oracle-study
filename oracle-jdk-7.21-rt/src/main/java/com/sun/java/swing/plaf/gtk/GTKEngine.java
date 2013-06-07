/*     */ package com.sun.java.swing.plaf.gtk;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.ComponentOrientation;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Image;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.awt.image.DataBufferInt;
/*     */ import java.awt.image.DirectColorModel;
/*     */ import java.awt.image.Raster;
/*     */ import java.awt.image.WritableRaster;
/*     */ import java.util.HashMap;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JPopupMenu;
/*     */ import javax.swing.JProgressBar;
/*     */ import javax.swing.JScrollBar;
/*     */ import javax.swing.JSeparator;
/*     */ import javax.swing.JSlider;
/*     */ import javax.swing.JSplitPane;
/*     */ import javax.swing.JToolBar;
/*     */ import javax.swing.ListCellRenderer;
/*     */ import javax.swing.plaf.synth.ColorType;
/*     */ import javax.swing.plaf.synth.Region;
/*     */ import javax.swing.plaf.synth.SynthContext;
/*     */ import sun.awt.UNIXToolkit;
/*     */ import sun.awt.image.SunWritableRaster;
/*     */ import sun.swing.ImageCache;
/*     */ 
/*     */ class GTKEngine
/*     */ {
/*  59 */   static final GTKEngine INSTANCE = new GTKEngine();
/*     */   private static final int CACHE_SIZE = 50;
/*     */   private static HashMap<Region, Object> regionToWidgetTypeMap;
/* 116 */   private ImageCache cache = new ImageCache(50);
/*     */   private int x0;
/*     */   private int y0;
/*     */   private int w0;
/*     */   private int h0;
/*     */   private Graphics graphics;
/*     */   private Object[] cacheArgs;
/* 524 */   private static final ColorModel[] COLOR_MODELS = { new DirectColorModel(24, 16711680, 65280, 255, 0), new DirectColorModel(25, 16711680, 65280, 255, 16777216), ColorModel.getRGBdefault() };
/*     */ 
/* 533 */   private static final int[][] BAND_OFFSETS = { { 16711680, 65280, 255 }, { 16711680, 65280, 255, 16777216 }, { 16711680, 65280, 255, -16777216 } };
/*     */ 
/*     */   private native void native_paint_arrow(int paramInt1, int paramInt2, int paramInt3, String paramString, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8);
/*     */ 
/*     */   private native void native_paint_box(int paramInt1, int paramInt2, int paramInt3, String paramString, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9);
/*     */ 
/*     */   private native void native_paint_box_gap(int paramInt1, int paramInt2, int paramInt3, String paramString, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10);
/*     */ 
/*     */   private native void native_paint_check(int paramInt1, int paramInt2, String paramString, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
/*     */ 
/*     */   private native void native_paint_expander(int paramInt1, int paramInt2, String paramString, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7);
/*     */ 
/*     */   private native void native_paint_extension(int paramInt1, int paramInt2, int paramInt3, String paramString, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8);
/*     */ 
/*     */   private native void native_paint_flat_box(int paramInt1, int paramInt2, int paramInt3, String paramString, int paramInt4, int paramInt5, int paramInt6, int paramInt7, boolean paramBoolean);
/*     */ 
/*     */   private native void native_paint_focus(int paramInt1, int paramInt2, String paramString, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
/*     */ 
/*     */   private native void native_paint_handle(int paramInt1, int paramInt2, int paramInt3, String paramString, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8);
/*     */ 
/*     */   private native void native_paint_hline(int paramInt1, int paramInt2, String paramString, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
/*     */ 
/*     */   private native void native_paint_option(int paramInt1, int paramInt2, String paramString, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
/*     */ 
/*     */   private native void native_paint_shadow(int paramInt1, int paramInt2, int paramInt3, String paramString, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9);
/*     */ 
/*     */   private native void native_paint_slider(int paramInt1, int paramInt2, int paramInt3, String paramString, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8);
/*     */ 
/*     */   private native void native_paint_vline(int paramInt1, int paramInt2, String paramString, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
/*     */ 
/*     */   private native void native_paint_background(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
/*     */ 
/*     */   private native Object native_get_gtk_setting(int paramInt);
/*     */ 
/*     */   private native void nativeSetRangeValue(int paramInt, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4);
/*     */ 
/*     */   private native void nativeStartPainting(int paramInt1, int paramInt2);
/*     */ 
/*     */   private native int nativeFinishPainting(int[] paramArrayOfInt, int paramInt1, int paramInt2);
/*     */ 
/*     */   private native void native_switch_theme();
/*     */ 
/*     */   static WidgetType getWidgetType(JComponent paramJComponent, Region paramRegion)
/*     */   {
/* 275 */     Object localObject1 = regionToWidgetTypeMap.get(paramRegion);
/*     */ 
/* 277 */     if ((localObject1 instanceof WidgetType)) {
/* 278 */       return (WidgetType)localObject1;
/*     */     }
/*     */ 
/* 281 */     WidgetType[] arrayOfWidgetType = (WidgetType[])localObject1;
/* 282 */     if (paramJComponent == null) {
/* 283 */       return arrayOfWidgetType[0];
/*     */     }
/*     */ 
/* 286 */     if ((paramJComponent instanceof JScrollBar))
/* 287 */       return ((JScrollBar)paramJComponent).getOrientation() == 0 ? arrayOfWidgetType[0] : arrayOfWidgetType[1];
/*     */     Object localObject2;
/* 289 */     if ((paramJComponent instanceof JSeparator)) {
/* 290 */       localObject2 = (JSeparator)paramJComponent;
/*     */ 
/* 295 */       if ((((JSeparator)localObject2).getParent() instanceof JPopupMenu))
/* 296 */         return WidgetType.POPUP_MENU_SEPARATOR;
/* 297 */       if ((((JSeparator)localObject2).getParent() instanceof JToolBar)) {
/* 298 */         return WidgetType.TOOL_BAR_SEPARATOR;
/*     */       }
/*     */ 
/* 301 */       return ((JSeparator)localObject2).getOrientation() == 0 ? arrayOfWidgetType[0] : arrayOfWidgetType[1];
/*     */     }
/* 303 */     if ((paramJComponent instanceof JSlider)) {
/* 304 */       return ((JSlider)paramJComponent).getOrientation() == 0 ? arrayOfWidgetType[0] : arrayOfWidgetType[1];
/*     */     }
/* 306 */     if ((paramJComponent instanceof JProgressBar)) {
/* 307 */       return ((JProgressBar)paramJComponent).getOrientation() == 0 ? arrayOfWidgetType[0] : arrayOfWidgetType[1];
/*     */     }
/* 309 */     if ((paramJComponent instanceof JSplitPane)) {
/* 310 */       return ((JSplitPane)paramJComponent).getOrientation() == 1 ? arrayOfWidgetType[1] : arrayOfWidgetType[0];
/*     */     }
/* 312 */     if (paramRegion == Region.LABEL)
/*     */     {
/* 318 */       if ((paramJComponent instanceof ListCellRenderer)) {
/* 319 */         return arrayOfWidgetType[1];
/*     */       }
/* 321 */       return arrayOfWidgetType[0];
/*     */     }
/* 323 */     if (paramRegion == Region.TEXT_FIELD) {
/* 324 */       localObject2 = paramJComponent.getName();
/* 325 */       if ((localObject2 != null) && (((String)localObject2).startsWith("ComboBox"))) {
/* 326 */         return arrayOfWidgetType[1];
/*     */       }
/* 328 */       return arrayOfWidgetType[0];
/*     */     }
/* 330 */     if (paramRegion == Region.FORMATTED_TEXT_FIELD) {
/* 331 */       localObject2 = paramJComponent.getName();
/* 332 */       if ((localObject2 != null) && (((String)localObject2).startsWith("Spinner"))) {
/* 333 */         return arrayOfWidgetType[1];
/*     */       }
/* 335 */       return arrayOfWidgetType[0];
/*     */     }
/* 337 */     if (paramRegion == Region.ARROW_BUTTON) {
/* 338 */       if ((paramJComponent.getParent() instanceof JScrollBar)) {
/* 339 */         localObject2 = (Integer)paramJComponent.getClientProperty("__arrow_direction__");
/*     */ 
/* 341 */         int i = localObject2 != null ? ((Integer)localObject2).intValue() : 7;
/*     */ 
/* 343 */         switch (i) {
/*     */         case 7:
/* 345 */           return WidgetType.HSCROLL_BAR_BUTTON_LEFT;
/*     */         case 3:
/* 347 */           return WidgetType.HSCROLL_BAR_BUTTON_RIGHT;
/*     */         case 1:
/* 349 */           return WidgetType.VSCROLL_BAR_BUTTON_UP;
/*     */         case 5:
/* 351 */           return WidgetType.VSCROLL_BAR_BUTTON_DOWN;
/*     */         case 2:
/*     */         case 4:
/* 353 */         case 6: } return null;
/*     */       }
/* 355 */       if ((paramJComponent.getParent() instanceof JComboBox)) {
/* 356 */         return WidgetType.COMBO_BOX_ARROW_BUTTON;
/*     */       }
/* 358 */       return WidgetType.SPINNER_ARROW_BUTTON;
/*     */     }
/*     */ 
/* 362 */     return null;
/*     */   }
/*     */ 
/*     */   private static int getTextDirection(SynthContext paramSynthContext) {
/* 366 */     GTKConstants.TextDirection localTextDirection = GTKConstants.TextDirection.NONE;
/* 367 */     JComponent localJComponent = paramSynthContext.getComponent();
/* 368 */     if (localJComponent != null) {
/* 369 */       ComponentOrientation localComponentOrientation = localJComponent.getComponentOrientation();
/* 370 */       if (localComponentOrientation != null) {
/* 371 */         localTextDirection = localComponentOrientation.isLeftToRight() ? GTKConstants.TextDirection.LTR : GTKConstants.TextDirection.RTL;
/*     */       }
/*     */     }
/*     */ 
/* 375 */     return localTextDirection.ordinal();
/*     */   }
/*     */ 
/*     */   public void paintArrow(Graphics paramGraphics, SynthContext paramSynthContext, Region paramRegion, int paramInt1, GTKConstants.ShadowType paramShadowType, GTKConstants.ArrowType paramArrowType, String paramString, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
/*     */   {
/* 382 */     paramInt1 = GTKLookAndFeel.synthStateToGTKStateType(paramInt1).ordinal();
/* 383 */     int i = getWidgetType(paramSynthContext.getComponent(), paramRegion).ordinal();
/* 384 */     native_paint_arrow(i, paramInt1, paramShadowType.ordinal(), paramString, paramInt2 - this.x0, paramInt3 - this.y0, paramInt4, paramInt5, paramArrowType.ordinal());
/*     */   }
/*     */ 
/*     */   public void paintBox(Graphics paramGraphics, SynthContext paramSynthContext, Region paramRegion, int paramInt1, GTKConstants.ShadowType paramShadowType, String paramString, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
/*     */   {
/* 392 */     int i = GTKLookAndFeel.synthStateToGTKStateType(paramInt1).ordinal();
/*     */ 
/* 394 */     int j = paramSynthContext.getComponentState();
/* 395 */     int k = getTextDirection(paramSynthContext);
/* 396 */     int m = getWidgetType(paramSynthContext.getComponent(), paramRegion).ordinal();
/* 397 */     native_paint_box(m, i, paramShadowType.ordinal(), paramString, paramInt2 - this.x0, paramInt3 - this.y0, paramInt4, paramInt5, j, k);
/*     */   }
/*     */ 
/*     */   public void paintBoxGap(Graphics paramGraphics, SynthContext paramSynthContext, Region paramRegion, int paramInt1, GTKConstants.ShadowType paramShadowType, String paramString, int paramInt2, int paramInt3, int paramInt4, int paramInt5, GTKConstants.PositionType paramPositionType, int paramInt6, int paramInt7)
/*     */   {
/* 406 */     paramInt1 = GTKLookAndFeel.synthStateToGTKStateType(paramInt1).ordinal();
/* 407 */     int i = getWidgetType(paramSynthContext.getComponent(), paramRegion).ordinal();
/* 408 */     native_paint_box_gap(i, paramInt1, paramShadowType.ordinal(), paramString, paramInt2 - this.x0, paramInt3 - this.y0, paramInt4, paramInt5, paramPositionType.ordinal(), paramInt6, paramInt7);
/*     */   }
/*     */ 
/*     */   public void paintCheck(Graphics paramGraphics, SynthContext paramSynthContext, Region paramRegion, String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/* 415 */     int i = paramSynthContext.getComponentState();
/* 416 */     int j = getWidgetType(paramSynthContext.getComponent(), paramRegion).ordinal();
/* 417 */     native_paint_check(j, i, paramString, paramInt1 - this.x0, paramInt2 - this.y0, paramInt3, paramInt4);
/*     */   }
/*     */ 
/*     */   public void paintExpander(Graphics paramGraphics, SynthContext paramSynthContext, Region paramRegion, int paramInt1, GTKConstants.ExpanderStyle paramExpanderStyle, String paramString, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
/*     */   {
/* 424 */     paramInt1 = GTKLookAndFeel.synthStateToGTKStateType(paramInt1).ordinal();
/* 425 */     int i = getWidgetType(paramSynthContext.getComponent(), paramRegion).ordinal();
/* 426 */     native_paint_expander(i, paramInt1, paramString, paramInt2 - this.x0, paramInt3 - this.y0, paramInt4, paramInt5, paramExpanderStyle.ordinal());
/*     */   }
/*     */ 
/*     */   public void paintExtension(Graphics paramGraphics, SynthContext paramSynthContext, Region paramRegion, int paramInt1, GTKConstants.ShadowType paramShadowType, String paramString, int paramInt2, int paramInt3, int paramInt4, int paramInt5, GTKConstants.PositionType paramPositionType, int paramInt6)
/*     */   {
/* 434 */     paramInt1 = GTKLookAndFeel.synthStateToGTKStateType(paramInt1).ordinal();
/* 435 */     int i = getWidgetType(paramSynthContext.getComponent(), paramRegion).ordinal();
/* 436 */     native_paint_extension(i, paramInt1, paramShadowType.ordinal(), paramString, paramInt2 - this.x0, paramInt3 - this.y0, paramInt4, paramInt5, paramPositionType.ordinal());
/*     */   }
/*     */ 
/*     */   public void paintFlatBox(Graphics paramGraphics, SynthContext paramSynthContext, Region paramRegion, int paramInt1, GTKConstants.ShadowType paramShadowType, String paramString, int paramInt2, int paramInt3, int paramInt4, int paramInt5, ColorType paramColorType)
/*     */   {
/* 444 */     paramInt1 = GTKLookAndFeel.synthStateToGTKStateType(paramInt1).ordinal();
/* 445 */     int i = getWidgetType(paramSynthContext.getComponent(), paramRegion).ordinal();
/* 446 */     native_paint_flat_box(i, paramInt1, paramShadowType.ordinal(), paramString, paramInt2 - this.x0, paramInt3 - this.y0, paramInt4, paramInt5, paramSynthContext.getComponent().hasFocus());
/*     */   }
/*     */ 
/*     */   public void paintFocus(Graphics paramGraphics, SynthContext paramSynthContext, Region paramRegion, int paramInt1, String paramString, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
/*     */   {
/* 454 */     paramInt1 = GTKLookAndFeel.synthStateToGTKStateType(paramInt1).ordinal();
/* 455 */     int i = getWidgetType(paramSynthContext.getComponent(), paramRegion).ordinal();
/* 456 */     native_paint_focus(i, paramInt1, paramString, paramInt2 - this.x0, paramInt3 - this.y0, paramInt4, paramInt5);
/*     */   }
/*     */ 
/*     */   public void paintHandle(Graphics paramGraphics, SynthContext paramSynthContext, Region paramRegion, int paramInt1, GTKConstants.ShadowType paramShadowType, String paramString, int paramInt2, int paramInt3, int paramInt4, int paramInt5, GTKConstants.Orientation paramOrientation)
/*     */   {
/* 463 */     paramInt1 = GTKLookAndFeel.synthStateToGTKStateType(paramInt1).ordinal();
/* 464 */     int i = getWidgetType(paramSynthContext.getComponent(), paramRegion).ordinal();
/* 465 */     native_paint_handle(i, paramInt1, paramShadowType.ordinal(), paramString, paramInt2 - this.x0, paramInt3 - this.y0, paramInt4, paramInt5, paramOrientation.ordinal());
/*     */   }
/*     */ 
/*     */   public void paintHline(Graphics paramGraphics, SynthContext paramSynthContext, Region paramRegion, int paramInt1, String paramString, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
/*     */   {
/* 472 */     paramInt1 = GTKLookAndFeel.synthStateToGTKStateType(paramInt1).ordinal();
/* 473 */     int i = getWidgetType(paramSynthContext.getComponent(), paramRegion).ordinal();
/* 474 */     native_paint_hline(i, paramInt1, paramString, paramInt2 - this.x0, paramInt3 - this.y0, paramInt4, paramInt5);
/*     */   }
/*     */ 
/*     */   public void paintOption(Graphics paramGraphics, SynthContext paramSynthContext, Region paramRegion, String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/* 480 */     int i = paramSynthContext.getComponentState();
/* 481 */     int j = getWidgetType(paramSynthContext.getComponent(), paramRegion).ordinal();
/* 482 */     native_paint_option(j, i, paramString, paramInt1 - this.x0, paramInt2 - this.y0, paramInt3, paramInt4);
/*     */   }
/*     */ 
/*     */   public void paintShadow(Graphics paramGraphics, SynthContext paramSynthContext, Region paramRegion, int paramInt1, GTKConstants.ShadowType paramShadowType, String paramString, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
/*     */   {
/* 489 */     int i = GTKLookAndFeel.synthStateToGTKStateType(paramInt1).ordinal();
/*     */ 
/* 491 */     int j = paramSynthContext.getComponentState();
/* 492 */     int k = getTextDirection(paramSynthContext);
/* 493 */     int m = getWidgetType(paramSynthContext.getComponent(), paramRegion).ordinal();
/* 494 */     native_paint_shadow(m, i, paramShadowType.ordinal(), paramString, paramInt2 - this.x0, paramInt3 - this.y0, paramInt4, paramInt5, j, k);
/*     */   }
/*     */ 
/*     */   public void paintSlider(Graphics paramGraphics, SynthContext paramSynthContext, Region paramRegion, int paramInt1, GTKConstants.ShadowType paramShadowType, String paramString, int paramInt2, int paramInt3, int paramInt4, int paramInt5, GTKConstants.Orientation paramOrientation)
/*     */   {
/* 502 */     paramInt1 = GTKLookAndFeel.synthStateToGTKStateType(paramInt1).ordinal();
/* 503 */     int i = getWidgetType(paramSynthContext.getComponent(), paramRegion).ordinal();
/* 504 */     native_paint_slider(i, paramInt1, paramShadowType.ordinal(), paramString, paramInt2 - this.x0, paramInt3 - this.y0, paramInt4, paramInt5, paramOrientation.ordinal());
/*     */   }
/*     */ 
/*     */   public void paintVline(Graphics paramGraphics, SynthContext paramSynthContext, Region paramRegion, int paramInt1, String paramString, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
/*     */   {
/* 511 */     paramInt1 = GTKLookAndFeel.synthStateToGTKStateType(paramInt1).ordinal();
/* 512 */     int i = getWidgetType(paramSynthContext.getComponent(), paramRegion).ordinal();
/* 513 */     native_paint_vline(i, paramInt1, paramString, paramInt2 - this.x0, paramInt3 - this.y0, paramInt4, paramInt5);
/*     */   }
/*     */ 
/*     */   public void paintBackground(Graphics paramGraphics, SynthContext paramSynthContext, Region paramRegion, int paramInt1, Color paramColor, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
/*     */   {
/* 519 */     paramInt1 = GTKLookAndFeel.synthStateToGTKStateType(paramInt1).ordinal();
/* 520 */     int i = getWidgetType(paramSynthContext.getComponent(), paramRegion).ordinal();
/* 521 */     native_paint_background(i, paramInt1, paramInt2 - this.x0, paramInt3 - this.y0, paramInt4, paramInt5);
/*     */   }
/*     */ 
/*     */   public boolean paintCachedImage(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object[] paramArrayOfObject)
/*     */   {
/* 548 */     if ((paramInt3 <= 0) || (paramInt4 <= 0)) {
/* 549 */       return true;
/*     */     }
/*     */ 
/* 553 */     Image localImage = this.cache.getImage(getClass(), null, paramInt3, paramInt4, paramArrayOfObject);
/* 554 */     if (localImage != null) {
/* 555 */       paramGraphics.drawImage(localImage, paramInt1, paramInt2, null);
/* 556 */       return true;
/*     */     }
/* 558 */     return false;
/*     */   }
/*     */ 
/*     */   public void startPainting(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object[] paramArrayOfObject)
/*     */   {
/* 566 */     nativeStartPainting(paramInt3, paramInt4);
/* 567 */     this.x0 = paramInt1;
/* 568 */     this.y0 = paramInt2;
/* 569 */     this.w0 = paramInt3;
/* 570 */     this.h0 = paramInt4;
/* 571 */     this.graphics = paramGraphics;
/* 572 */     this.cacheArgs = paramArrayOfObject;
/*     */   }
/*     */ 
/*     */   public void finishPainting()
/*     */   {
/* 580 */     finishPainting(true);
/*     */   }
/*     */ 
/*     */   public void finishPainting(boolean paramBoolean)
/*     */   {
/* 589 */     DataBufferInt localDataBufferInt = new DataBufferInt(this.w0 * this.h0);
/*     */ 
/* 592 */     int i = nativeFinishPainting(SunWritableRaster.stealData(localDataBufferInt, 0), this.w0, this.h0);
/*     */ 
/* 595 */     SunWritableRaster.markDirty(localDataBufferInt);
/*     */ 
/* 597 */     int[] arrayOfInt = BAND_OFFSETS[(i - 1)];
/* 598 */     WritableRaster localWritableRaster = Raster.createPackedRaster(localDataBufferInt, this.w0, this.h0, this.w0, arrayOfInt, null);
/*     */ 
/* 601 */     ColorModel localColorModel = COLOR_MODELS[(i - 1)];
/* 602 */     BufferedImage localBufferedImage = new BufferedImage(localColorModel, localWritableRaster, false, null);
/* 603 */     if (paramBoolean) {
/* 604 */       this.cache.setImage(getClass(), null, this.w0, this.h0, this.cacheArgs, localBufferedImage);
/*     */     }
/* 606 */     this.graphics.drawImage(localBufferedImage, this.x0, this.y0, null);
/*     */   }
/*     */ 
/*     */   public void themeChanged()
/*     */   {
/* 613 */     synchronized (UNIXToolkit.GTK_LOCK) {
/* 614 */       native_switch_theme();
/*     */     }
/* 616 */     this.cache.flush();
/*     */   }
/*     */ 
/*     */   public Object getSetting(Settings paramSettings)
/*     */   {
/* 621 */     synchronized (UNIXToolkit.GTK_LOCK) {
/* 622 */       return native_get_gtk_setting(paramSettings.ordinal());
/*     */     }
/*     */   }
/*     */ 
/*     */   void setRangeValue(SynthContext paramSynthContext, Region paramRegion, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
/*     */   {
/* 632 */     int i = getWidgetType(paramSynthContext.getComponent(), paramRegion).ordinal();
/* 633 */     nativeSetRangeValue(i, paramDouble1, paramDouble2, paramDouble3, paramDouble4);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 178 */     Toolkit.getDefaultToolkit();
/*     */ 
/* 181 */     regionToWidgetTypeMap = new HashMap(50);
/* 182 */     regionToWidgetTypeMap.put(Region.ARROW_BUTTON, new WidgetType[] { WidgetType.SPINNER_ARROW_BUTTON, WidgetType.COMBO_BOX_ARROW_BUTTON, WidgetType.HSCROLL_BAR_BUTTON_LEFT, WidgetType.HSCROLL_BAR_BUTTON_RIGHT, WidgetType.VSCROLL_BAR_BUTTON_UP, WidgetType.VSCROLL_BAR_BUTTON_DOWN });
/*     */ 
/* 189 */     regionToWidgetTypeMap.put(Region.BUTTON, WidgetType.BUTTON);
/* 190 */     regionToWidgetTypeMap.put(Region.CHECK_BOX, WidgetType.CHECK_BOX);
/* 191 */     regionToWidgetTypeMap.put(Region.CHECK_BOX_MENU_ITEM, WidgetType.CHECK_BOX_MENU_ITEM);
/*     */ 
/* 193 */     regionToWidgetTypeMap.put(Region.COLOR_CHOOSER, WidgetType.COLOR_CHOOSER);
/* 194 */     regionToWidgetTypeMap.put(Region.FILE_CHOOSER, WidgetType.OPTION_PANE);
/* 195 */     regionToWidgetTypeMap.put(Region.COMBO_BOX, WidgetType.COMBO_BOX);
/* 196 */     regionToWidgetTypeMap.put(Region.DESKTOP_ICON, WidgetType.DESKTOP_ICON);
/* 197 */     regionToWidgetTypeMap.put(Region.DESKTOP_PANE, WidgetType.DESKTOP_PANE);
/* 198 */     regionToWidgetTypeMap.put(Region.EDITOR_PANE, WidgetType.EDITOR_PANE);
/* 199 */     regionToWidgetTypeMap.put(Region.FORMATTED_TEXT_FIELD, new WidgetType[] { WidgetType.FORMATTED_TEXT_FIELD, WidgetType.SPINNER_TEXT_FIELD });
/*     */ 
/* 201 */     regionToWidgetTypeMap.put(GTKRegion.HANDLE_BOX, WidgetType.HANDLE_BOX);
/* 202 */     regionToWidgetTypeMap.put(Region.INTERNAL_FRAME, WidgetType.INTERNAL_FRAME);
/*     */ 
/* 204 */     regionToWidgetTypeMap.put(Region.INTERNAL_FRAME_TITLE_PANE, WidgetType.INTERNAL_FRAME_TITLE_PANE);
/*     */ 
/* 206 */     regionToWidgetTypeMap.put(Region.LABEL, new WidgetType[] { WidgetType.LABEL, WidgetType.COMBO_BOX_TEXT_FIELD });
/*     */ 
/* 208 */     regionToWidgetTypeMap.put(Region.LIST, WidgetType.LIST);
/* 209 */     regionToWidgetTypeMap.put(Region.MENU, WidgetType.MENU);
/* 210 */     regionToWidgetTypeMap.put(Region.MENU_BAR, WidgetType.MENU_BAR);
/* 211 */     regionToWidgetTypeMap.put(Region.MENU_ITEM, WidgetType.MENU_ITEM);
/* 212 */     regionToWidgetTypeMap.put(Region.MENU_ITEM_ACCELERATOR, WidgetType.MENU_ITEM_ACCELERATOR);
/*     */ 
/* 214 */     regionToWidgetTypeMap.put(Region.OPTION_PANE, WidgetType.OPTION_PANE);
/* 215 */     regionToWidgetTypeMap.put(Region.PANEL, WidgetType.PANEL);
/* 216 */     regionToWidgetTypeMap.put(Region.PASSWORD_FIELD, WidgetType.PASSWORD_FIELD);
/*     */ 
/* 218 */     regionToWidgetTypeMap.put(Region.POPUP_MENU, WidgetType.POPUP_MENU);
/* 219 */     regionToWidgetTypeMap.put(Region.POPUP_MENU_SEPARATOR, WidgetType.POPUP_MENU_SEPARATOR);
/*     */ 
/* 221 */     regionToWidgetTypeMap.put(Region.PROGRESS_BAR, new WidgetType[] { WidgetType.HPROGRESS_BAR, WidgetType.VPROGRESS_BAR });
/*     */ 
/* 223 */     regionToWidgetTypeMap.put(Region.RADIO_BUTTON, WidgetType.RADIO_BUTTON);
/* 224 */     regionToWidgetTypeMap.put(Region.RADIO_BUTTON_MENU_ITEM, WidgetType.RADIO_BUTTON_MENU_ITEM);
/*     */ 
/* 226 */     regionToWidgetTypeMap.put(Region.ROOT_PANE, WidgetType.ROOT_PANE);
/* 227 */     regionToWidgetTypeMap.put(Region.SCROLL_BAR, new WidgetType[] { WidgetType.HSCROLL_BAR, WidgetType.VSCROLL_BAR });
/*     */ 
/* 229 */     regionToWidgetTypeMap.put(Region.SCROLL_BAR_THUMB, new WidgetType[] { WidgetType.HSCROLL_BAR_THUMB, WidgetType.VSCROLL_BAR_THUMB });
/*     */ 
/* 231 */     regionToWidgetTypeMap.put(Region.SCROLL_BAR_TRACK, new WidgetType[] { WidgetType.HSCROLL_BAR_TRACK, WidgetType.VSCROLL_BAR_TRACK });
/*     */ 
/* 233 */     regionToWidgetTypeMap.put(Region.SCROLL_PANE, WidgetType.SCROLL_PANE);
/* 234 */     regionToWidgetTypeMap.put(Region.SEPARATOR, new WidgetType[] { WidgetType.HSEPARATOR, WidgetType.VSEPARATOR });
/*     */ 
/* 236 */     regionToWidgetTypeMap.put(Region.SLIDER, new WidgetType[] { WidgetType.HSLIDER, WidgetType.VSLIDER });
/*     */ 
/* 238 */     regionToWidgetTypeMap.put(Region.SLIDER_THUMB, new WidgetType[] { WidgetType.HSLIDER_THUMB, WidgetType.VSLIDER_THUMB });
/*     */ 
/* 240 */     regionToWidgetTypeMap.put(Region.SLIDER_TRACK, new WidgetType[] { WidgetType.HSLIDER_TRACK, WidgetType.VSLIDER_TRACK });
/*     */ 
/* 242 */     regionToWidgetTypeMap.put(Region.SPINNER, WidgetType.SPINNER);
/* 243 */     regionToWidgetTypeMap.put(Region.SPLIT_PANE, WidgetType.SPLIT_PANE);
/* 244 */     regionToWidgetTypeMap.put(Region.SPLIT_PANE_DIVIDER, new WidgetType[] { WidgetType.HSPLIT_PANE_DIVIDER, WidgetType.VSPLIT_PANE_DIVIDER });
/*     */ 
/* 246 */     regionToWidgetTypeMap.put(Region.TABBED_PANE, WidgetType.TABBED_PANE);
/* 247 */     regionToWidgetTypeMap.put(Region.TABBED_PANE_CONTENT, WidgetType.TABBED_PANE_CONTENT);
/*     */ 
/* 249 */     regionToWidgetTypeMap.put(Region.TABBED_PANE_TAB, WidgetType.TABBED_PANE_TAB);
/*     */ 
/* 251 */     regionToWidgetTypeMap.put(Region.TABBED_PANE_TAB_AREA, WidgetType.TABBED_PANE_TAB_AREA);
/*     */ 
/* 253 */     regionToWidgetTypeMap.put(Region.TABLE, WidgetType.TABLE);
/* 254 */     regionToWidgetTypeMap.put(Region.TABLE_HEADER, WidgetType.TABLE_HEADER);
/* 255 */     regionToWidgetTypeMap.put(Region.TEXT_AREA, WidgetType.TEXT_AREA);
/* 256 */     regionToWidgetTypeMap.put(Region.TEXT_FIELD, new WidgetType[] { WidgetType.TEXT_FIELD, WidgetType.COMBO_BOX_TEXT_FIELD });
/*     */ 
/* 258 */     regionToWidgetTypeMap.put(Region.TEXT_PANE, WidgetType.TEXT_PANE);
/* 259 */     regionToWidgetTypeMap.put(CustomRegion.TITLED_BORDER, WidgetType.TITLED_BORDER);
/* 260 */     regionToWidgetTypeMap.put(Region.TOGGLE_BUTTON, WidgetType.TOGGLE_BUTTON);
/* 261 */     regionToWidgetTypeMap.put(Region.TOOL_BAR, WidgetType.TOOL_BAR);
/* 262 */     regionToWidgetTypeMap.put(Region.TOOL_BAR_CONTENT, WidgetType.TOOL_BAR);
/* 263 */     regionToWidgetTypeMap.put(Region.TOOL_BAR_DRAG_WINDOW, WidgetType.TOOL_BAR_DRAG_WINDOW);
/*     */ 
/* 265 */     regionToWidgetTypeMap.put(Region.TOOL_BAR_SEPARATOR, WidgetType.TOOL_BAR_SEPARATOR);
/*     */ 
/* 267 */     regionToWidgetTypeMap.put(Region.TOOL_TIP, WidgetType.TOOL_TIP);
/* 268 */     regionToWidgetTypeMap.put(Region.TREE, WidgetType.TREE);
/* 269 */     regionToWidgetTypeMap.put(Region.TREE_CELL, WidgetType.TREE_CELL);
/* 270 */     regionToWidgetTypeMap.put(Region.VIEWPORT, WidgetType.VIEWPORT);
/*     */   }
/*     */ 
/*     */   static class CustomRegion extends Region
/*     */   {
/* 107 */     static Region TITLED_BORDER = new CustomRegion("TitledBorder");
/*     */ 
/*     */     private CustomRegion(String paramString) {
/* 110 */       super(null, false);
/*     */     }
/*     */   }
/*     */ 
/*     */   static enum Settings
/*     */   {
/*  95 */     GTK_FONT_NAME, 
/*  96 */     GTK_ICON_SIZES;
/*     */   }
/*     */ 
/*     */   static enum WidgetType
/*     */   {
/*  66 */     BUTTON, CHECK_BOX, CHECK_BOX_MENU_ITEM, COLOR_CHOOSER, 
/*  67 */     COMBO_BOX, COMBO_BOX_ARROW_BUTTON, COMBO_BOX_TEXT_FIELD, 
/*  68 */     DESKTOP_ICON, DESKTOP_PANE, EDITOR_PANE, FORMATTED_TEXT_FIELD, 
/*  69 */     HANDLE_BOX, HPROGRESS_BAR, 
/*  70 */     HSCROLL_BAR, HSCROLL_BAR_BUTTON_LEFT, HSCROLL_BAR_BUTTON_RIGHT, 
/*  71 */     HSCROLL_BAR_TRACK, HSCROLL_BAR_THUMB, 
/*  72 */     HSEPARATOR, HSLIDER, HSLIDER_TRACK, HSLIDER_THUMB, HSPLIT_PANE_DIVIDER, 
/*  73 */     INTERNAL_FRAME, INTERNAL_FRAME_TITLE_PANE, IMAGE, LABEL, LIST, MENU, 
/*  74 */     MENU_BAR, MENU_ITEM, MENU_ITEM_ACCELERATOR, OPTION_PANE, PANEL, 
/*  75 */     PASSWORD_FIELD, POPUP_MENU, POPUP_MENU_SEPARATOR, 
/*  76 */     RADIO_BUTTON, RADIO_BUTTON_MENU_ITEM, ROOT_PANE, SCROLL_PANE, 
/*  77 */     SPINNER, SPINNER_ARROW_BUTTON, SPINNER_TEXT_FIELD, 
/*  78 */     SPLIT_PANE, TABBED_PANE, TABBED_PANE_TAB_AREA, TABBED_PANE_CONTENT, 
/*  79 */     TABBED_PANE_TAB, TABLE, TABLE_HEADER, TEXT_AREA, TEXT_FIELD, TEXT_PANE, 
/*  80 */     TITLED_BORDER, 
/*  81 */     TOGGLE_BUTTON, TOOL_BAR, TOOL_BAR_DRAG_WINDOW, TOOL_BAR_SEPARATOR, 
/*  82 */     TOOL_TIP, TREE, TREE_CELL, VIEWPORT, VPROGRESS_BAR, 
/*  83 */     VSCROLL_BAR, VSCROLL_BAR_BUTTON_UP, VSCROLL_BAR_BUTTON_DOWN, 
/*  84 */     VSCROLL_BAR_TRACK, VSCROLL_BAR_THUMB, 
/*  85 */     VSEPARATOR, VSLIDER, VSLIDER_TRACK, VSLIDER_THUMB, 
/*  86 */     VSPLIT_PANE_DIVIDER;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.java.swing.plaf.gtk.GTKEngine
 * JD-Core Version:    0.6.2
 */