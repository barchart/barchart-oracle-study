/*     */ package com.sun.java.swing.plaf.windows;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.GraphicsConfiguration;
/*     */ import java.awt.Image;
/*     */ import java.awt.Insets;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.DataBufferInt;
/*     */ import java.awt.image.WritableRaster;
/*     */ import java.security.AccessController;
/*     */ import java.util.HashMap;
/*     */ import javax.swing.AbstractButton;
/*     */ import javax.swing.ButtonModel;
/*     */ import javax.swing.CellRendererPane;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JRadioButton;
/*     */ import javax.swing.JToolBar;
/*     */ import javax.swing.SwingUtilities;
/*     */ import javax.swing.UIManager;
/*     */ import javax.swing.border.AbstractBorder;
/*     */ import javax.swing.border.Border;
/*     */ import javax.swing.border.EmptyBorder;
/*     */ import javax.swing.border.LineBorder;
/*     */ import javax.swing.plaf.ColorUIResource;
/*     */ import javax.swing.plaf.InsetsUIResource;
/*     */ import javax.swing.plaf.UIResource;
/*     */ import javax.swing.text.JTextComponent;
/*     */ import sun.awt.image.SunWritableRaster;
/*     */ import sun.awt.windows.ThemeReader;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ import sun.swing.CachedPainter;
/*     */ 
/*     */ class XPStyle
/*     */ {
/*     */   private static XPStyle xp;
/*     */   private static SkinPainter skinPainter;
/*     */   private static Boolean themeActive;
/*     */   private HashMap<String, Border> borderMap;
/*     */   private HashMap<String, Color> colorMap;
/*     */   private boolean flatMenus;
/*     */ 
/*     */   static synchronized void invalidateStyle()
/*     */   {
/*  88 */     xp = null;
/*  89 */     themeActive = null;
/*  90 */     skinPainter.flush();
/*     */   }
/*     */ 
/*     */   static synchronized XPStyle getXP()
/*     */   {
/*  99 */     if (themeActive == null) {
/* 100 */       Toolkit localToolkit = Toolkit.getDefaultToolkit();
/* 101 */       themeActive = (Boolean)localToolkit.getDesktopProperty("win.xpstyle.themeActive");
/*     */ 
/* 103 */       if (themeActive == null) {
/* 104 */         themeActive = Boolean.FALSE;
/*     */       }
/* 106 */       if (themeActive.booleanValue()) {
/* 107 */         GetPropertyAction localGetPropertyAction = new GetPropertyAction("swing.noxp");
/*     */ 
/* 109 */         if ((AccessController.doPrivileged(localGetPropertyAction) == null) && (ThemeReader.isThemed()) && (!(UIManager.getLookAndFeel() instanceof WindowsClassicLookAndFeel)))
/*     */         {
/* 114 */           xp = new XPStyle();
/*     */         }
/*     */       }
/*     */     }
/* 118 */     return xp;
/*     */   }
/*     */ 
/*     */   static boolean isVista() {
/* 122 */     XPStyle localXPStyle = getXP();
/* 123 */     return (localXPStyle != null) && (localXPStyle.isSkinDefined(null, TMSchema.Part.CP_DROPDOWNBUTTONRIGHT));
/*     */   }
/*     */ 
/*     */   String getString(Component paramComponent, TMSchema.Part paramPart, TMSchema.State paramState, TMSchema.Prop paramProp)
/*     */   {
/* 138 */     return getTypeEnumName(paramComponent, paramPart, paramState, paramProp);
/*     */   }
/*     */ 
/*     */   TMSchema.TypeEnum getTypeEnum(Component paramComponent, TMSchema.Part paramPart, TMSchema.State paramState, TMSchema.Prop paramProp) {
/* 142 */     int i = ThemeReader.getEnum(paramPart.getControlName(paramComponent), paramPart.getValue(), TMSchema.State.getValue(paramPart, paramState), paramProp.getValue());
/*     */ 
/* 145 */     return TMSchema.TypeEnum.getTypeEnum(paramProp, i);
/*     */   }
/*     */ 
/*     */   private static String getTypeEnumName(Component paramComponent, TMSchema.Part paramPart, TMSchema.State paramState, TMSchema.Prop paramProp) {
/* 149 */     int i = ThemeReader.getEnum(paramPart.getControlName(paramComponent), paramPart.getValue(), TMSchema.State.getValue(paramPart, paramState), paramProp.getValue());
/*     */ 
/* 152 */     if (i == -1) {
/* 153 */       return null;
/*     */     }
/* 155 */     return TMSchema.TypeEnum.getTypeEnum(paramProp, i).getName();
/*     */   }
/*     */ 
/*     */   int getInt(Component paramComponent, TMSchema.Part paramPart, TMSchema.State paramState, TMSchema.Prop paramProp, int paramInt)
/*     */   {
/* 168 */     return ThemeReader.getInt(paramPart.getControlName(paramComponent), paramPart.getValue(), TMSchema.State.getValue(paramPart, paramState), paramProp.getValue());
/*     */   }
/*     */ 
/*     */   Dimension getDimension(Component paramComponent, TMSchema.Part paramPart, TMSchema.State paramState, TMSchema.Prop paramProp)
/*     */   {
/* 183 */     return ThemeReader.getPosition(paramPart.getControlName(paramComponent), paramPart.getValue(), TMSchema.State.getValue(paramPart, paramState), paramProp.getValue());
/*     */   }
/*     */ 
/*     */   Point getPoint(Component paramComponent, TMSchema.Part paramPart, TMSchema.State paramState, TMSchema.Prop paramProp)
/*     */   {
/* 199 */     Dimension localDimension = ThemeReader.getPosition(paramPart.getControlName(paramComponent), paramPart.getValue(), TMSchema.State.getValue(paramPart, paramState), paramProp.getValue());
/*     */ 
/* 202 */     if (localDimension != null) {
/* 203 */       return new Point(localDimension.width, localDimension.height);
/*     */     }
/* 205 */     return null;
/*     */   }
/*     */ 
/*     */   Insets getMargin(Component paramComponent, TMSchema.Part paramPart, TMSchema.State paramState, TMSchema.Prop paramProp)
/*     */   {
/* 220 */     return ThemeReader.getThemeMargins(paramPart.getControlName(paramComponent), paramPart.getValue(), TMSchema.State.getValue(paramPart, paramState), paramProp.getValue());
/*     */   }
/*     */ 
/*     */   synchronized Color getColor(Skin paramSkin, TMSchema.Prop paramProp, Color paramColor)
/*     */   {
/* 233 */     String str = paramSkin.toString() + "." + paramProp.name();
/* 234 */     TMSchema.Part localPart = paramSkin.part;
/* 235 */     Object localObject = (Color)this.colorMap.get(str);
/* 236 */     if (localObject == null) {
/* 237 */       localObject = ThemeReader.getColor(localPart.getControlName(null), localPart.getValue(), TMSchema.State.getValue(localPart, paramSkin.state), paramProp.getValue());
/*     */ 
/* 240 */       if (localObject != null) {
/* 241 */         localObject = new ColorUIResource((Color)localObject);
/* 242 */         this.colorMap.put(str, localObject);
/*     */       }
/*     */     }
/* 245 */     return localObject != null ? localObject : paramColor;
/*     */   }
/*     */ 
/*     */   Color getColor(Component paramComponent, TMSchema.Part paramPart, TMSchema.State paramState, TMSchema.Prop paramProp, Color paramColor) {
/* 249 */     return getColor(new Skin(paramComponent, paramPart, paramState), paramProp, paramColor);
/*     */   }
/*     */ 
/*     */   synchronized Border getBorder(Component paramComponent, TMSchema.Part paramPart)
/*     */   {
/* 262 */     if (paramPart == TMSchema.Part.MENU)
/*     */     {
/* 264 */       if (this.flatMenus)
/*     */       {
/* 268 */         return new XPFillBorder(UIManager.getColor("InternalFrame.borderShadow"), 1);
/*     */       }
/*     */ 
/* 271 */       return null;
/*     */     }
/*     */ 
/* 274 */     Skin localSkin = new Skin(paramComponent, paramPart, null);
/* 275 */     Object localObject = (Border)this.borderMap.get(localSkin.string);
/* 276 */     if (localObject == null) {
/* 277 */       String str = getTypeEnumName(paramComponent, paramPart, null, TMSchema.Prop.BGTYPE);
/* 278 */       if ("borderfill".equalsIgnoreCase(str)) {
/* 279 */         int i = getInt(paramComponent, paramPart, null, TMSchema.Prop.BORDERSIZE, 1);
/* 280 */         Color localColor = getColor(localSkin, TMSchema.Prop.BORDERCOLOR, Color.black);
/* 281 */         localObject = new XPFillBorder(localColor, i);
/* 282 */         if (paramPart == TMSchema.Part.CP_COMBOBOX)
/* 283 */           localObject = new XPStatefulFillBorder(localColor, i, paramPart, TMSchema.Prop.BORDERCOLOR);
/*     */       }
/* 285 */       else if ("imagefile".equalsIgnoreCase(str)) {
/* 286 */         Insets localInsets = getMargin(paramComponent, paramPart, null, TMSchema.Prop.SIZINGMARGINS);
/* 287 */         if (localInsets != null) {
/* 288 */           if (getBoolean(paramComponent, paramPart, null, TMSchema.Prop.BORDERONLY))
/* 289 */             localObject = new XPImageBorder(paramComponent, paramPart);
/* 290 */           else if (paramPart == TMSchema.Part.CP_COMBOBOX) {
/* 291 */             localObject = new EmptyBorder(1, 1, 1, 1);
/*     */           }
/* 293 */           else if (paramPart == TMSchema.Part.TP_BUTTON)
/* 294 */             localObject = new XPEmptyBorder(new Insets(3, 3, 3, 3));
/*     */           else {
/* 296 */             localObject = new XPEmptyBorder(localInsets);
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 301 */       if (localObject != null) {
/* 302 */         this.borderMap.put(localSkin.string, localObject);
/*     */       }
/*     */     }
/* 305 */     return localObject;
/*     */   }
/*     */ 
/*     */   boolean isSkinDefined(Component paramComponent, TMSchema.Part paramPart)
/*     */   {
/* 441 */     return (paramPart.getValue() == 0) || (ThemeReader.isThemePartDefined(paramPart.getControlName(paramComponent), paramPart.getValue(), 0));
/*     */   }
/*     */ 
/*     */   synchronized Skin getSkin(Component paramComponent, TMSchema.Part paramPart)
/*     */   {
/* 454 */     assert (isSkinDefined(paramComponent, paramPart)) : ("part " + paramPart + " is not defined");
/* 455 */     return new Skin(paramComponent, paramPart, null);
/*     */   }
/*     */ 
/*     */   long getThemeTransitionDuration(Component paramComponent, TMSchema.Part paramPart, TMSchema.State paramState1, TMSchema.State paramState2, TMSchema.Prop paramProp)
/*     */   {
/* 461 */     return ThemeReader.getThemeTransitionDuration(paramPart.getControlName(paramComponent), paramPart.getValue(), TMSchema.State.getValue(paramPart, paramState1), TMSchema.State.getValue(paramPart, paramState2), paramProp != null ? paramProp.getValue() : 0);
/*     */   }
/*     */ 
/*     */   private XPStyle()
/*     */   {
/* 726 */     this.flatMenus = getSysBoolean(TMSchema.Prop.FLATMENUS);
/*     */ 
/* 728 */     this.colorMap = new HashMap();
/* 729 */     this.borderMap = new HashMap();
/*     */   }
/*     */ 
/*     */   private boolean getBoolean(Component paramComponent, TMSchema.Part paramPart, TMSchema.State paramState, TMSchema.Prop paramProp)
/*     */   {
/* 735 */     return ThemeReader.getBoolean(paramPart.getControlName(paramComponent), paramPart.getValue(), TMSchema.State.getValue(paramPart, paramState), paramProp.getValue());
/*     */   }
/*     */ 
/*     */   static Dimension getPartSize(TMSchema.Part paramPart, TMSchema.State paramState)
/*     */   {
/* 743 */     return ThemeReader.getPartSize(paramPart.getControlName(null), paramPart.getValue(), TMSchema.State.getValue(paramPart, paramState));
/*     */   }
/*     */ 
/*     */   private static boolean getSysBoolean(TMSchema.Prop paramProp)
/*     */   {
/* 749 */     return ThemeReader.getSysBoolean("window", paramProp.getValue());
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  71 */     skinPainter = new SkinPainter();
/*     */ 
/*  73 */     themeActive = null;
/*     */ 
/*  81 */     invalidateStyle();
/*     */   }
/*     */ 
/*     */   static class GlyphButton extends JButton
/*     */   {
/*     */     private XPStyle.Skin skin;
/*     */ 
/*     */     public GlyphButton(Component paramComponent, TMSchema.Part paramPart)
/*     */     {
/* 681 */       XPStyle localXPStyle = XPStyle.getXP();
/* 682 */       this.skin = localXPStyle.getSkin(paramComponent, paramPart);
/* 683 */       setBorder(null);
/* 684 */       setContentAreaFilled(false);
/* 685 */       setMinimumSize(new Dimension(5, 5));
/* 686 */       setPreferredSize(new Dimension(16, 16));
/* 687 */       setMaximumSize(new Dimension(2147483647, 2147483647));
/*     */     }
/*     */ 
/*     */     public boolean isFocusTraversable() {
/* 691 */       return false;
/*     */     }
/*     */ 
/*     */     protected TMSchema.State getState() {
/* 695 */       TMSchema.State localState = TMSchema.State.NORMAL;
/* 696 */       if (!isEnabled())
/* 697 */         localState = TMSchema.State.DISABLED;
/* 698 */       else if (getModel().isPressed())
/* 699 */         localState = TMSchema.State.PRESSED;
/* 700 */       else if (getModel().isRollover()) {
/* 701 */         localState = TMSchema.State.HOT;
/*     */       }
/* 703 */       return localState;
/*     */     }
/*     */ 
/*     */     public void paintComponent(Graphics paramGraphics) {
/* 707 */       Dimension localDimension = getSize();
/* 708 */       this.skin.paintSkin(paramGraphics, 0, 0, localDimension.width, localDimension.height, getState());
/*     */     }
/*     */ 
/*     */     public void setPart(Component paramComponent, TMSchema.Part paramPart) {
/* 712 */       XPStyle localXPStyle = XPStyle.getXP();
/* 713 */       this.skin = localXPStyle.getSkin(paramComponent, paramPart);
/* 714 */       revalidate();
/* 715 */       repaint();
/*     */     }
/*     */ 
/*     */     protected void paintBorder(Graphics paramGraphics)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   static class Skin
/*     */   {
/*     */     final Component component;
/*     */     final TMSchema.Part part;
/*     */     final TMSchema.State state;
/*     */     private final String string;
/* 479 */     private Dimension size = null;
/*     */ 
/*     */     Skin(Component paramComponent, TMSchema.Part paramPart) {
/* 482 */       this(paramComponent, paramPart, null);
/*     */     }
/*     */ 
/*     */     Skin(TMSchema.Part paramPart, TMSchema.State paramState) {
/* 486 */       this(null, paramPart, paramState);
/*     */     }
/*     */ 
/*     */     Skin(Component paramComponent, TMSchema.Part paramPart, TMSchema.State paramState) {
/* 490 */       this.component = paramComponent;
/* 491 */       this.part = paramPart;
/* 492 */       this.state = paramState;
/*     */ 
/* 494 */       String str = paramPart.getControlName(paramComponent) + "." + paramPart.name();
/* 495 */       if (paramState != null) {
/* 496 */         str = str + "(" + paramState.name() + ")";
/*     */       }
/* 498 */       this.string = str;
/*     */     }
/*     */ 
/*     */     Insets getContentMargin()
/*     */     {
/* 505 */       int i = 100;
/* 506 */       int j = 100;
/*     */ 
/* 508 */       return ThemeReader.getThemeBackgroundContentMargins(this.part.getControlName(null), this.part.getValue(), 0, i, j);
/*     */     }
/*     */ 
/*     */     private int getWidth(TMSchema.State paramState)
/*     */     {
/* 514 */       if (this.size == null) {
/* 515 */         this.size = XPStyle.getPartSize(this.part, paramState);
/*     */       }
/* 517 */       return this.size.width;
/*     */     }
/*     */ 
/*     */     int getWidth() {
/* 521 */       return getWidth(this.state != null ? this.state : TMSchema.State.NORMAL);
/*     */     }
/*     */ 
/*     */     private int getHeight(TMSchema.State paramState) {
/* 525 */       if (this.size == null) {
/* 526 */         this.size = XPStyle.getPartSize(this.part, paramState);
/*     */       }
/* 528 */       return this.size.height;
/*     */     }
/*     */ 
/*     */     int getHeight() {
/* 532 */       return getHeight(this.state != null ? this.state : TMSchema.State.NORMAL);
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 536 */       return this.string;
/*     */     }
/*     */ 
/*     */     public boolean equals(Object paramObject) {
/* 540 */       return ((paramObject instanceof Skin)) && (((Skin)paramObject).string.equals(this.string));
/*     */     }
/*     */ 
/*     */     public int hashCode() {
/* 544 */       return this.string.hashCode();
/*     */     }
/*     */ 
/*     */     void paintSkin(Graphics paramGraphics, int paramInt1, int paramInt2, TMSchema.State paramState)
/*     */     {
/* 555 */       if (paramState == null) {
/* 556 */         paramState = this.state;
/*     */       }
/* 558 */       paintSkin(paramGraphics, paramInt1, paramInt2, getWidth(paramState), getHeight(paramState), paramState);
/*     */     }
/*     */ 
/*     */     void paintSkin(Graphics paramGraphics, Rectangle paramRectangle, TMSchema.State paramState)
/*     */     {
/* 569 */       paintSkin(paramGraphics, paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height, paramState);
/*     */     }
/*     */ 
/*     */     void paintSkin(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, TMSchema.State paramState)
/*     */     {
/* 585 */       if ((ThemeReader.isGetThemeTransitionDurationDefined()) && ((this.component instanceof JComponent)) && (SwingUtilities.getAncestorOfClass(CellRendererPane.class, this.component) == null))
/*     */       {
/* 589 */         AnimationController.paintSkin((JComponent)this.component, this, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramState);
/*     */       }
/*     */       else
/* 592 */         paintSkinRaw(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramState);
/*     */     }
/*     */ 
/*     */     void paintSkinRaw(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, TMSchema.State paramState)
/*     */     {
/* 610 */       XPStyle.skinPainter.paint(null, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { this, paramState });
/*     */     }
/*     */ 
/*     */     void paintSkin(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, TMSchema.State paramState, boolean paramBoolean)
/*     */     {
/* 628 */       if ((paramBoolean) && ("borderfill".equals(XPStyle.getTypeEnumName(this.component, this.part, paramState, TMSchema.Prop.BGTYPE))))
/*     */       {
/* 630 */         return;
/*     */       }
/* 632 */       XPStyle.skinPainter.paint(null, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { this, paramState });
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class SkinPainter extends CachedPainter {
/*     */     SkinPainter() {
/* 638 */       super();
/* 639 */       flush();
/*     */     }
/*     */ 
/*     */     public void flush() {
/* 643 */       super.flush();
/*     */     }
/*     */ 
/*     */     protected void paintToImage(Component paramComponent, Image paramImage, Graphics paramGraphics, int paramInt1, int paramInt2, Object[] paramArrayOfObject)
/*     */     {
/* 648 */       int i = 0;
/* 649 */       XPStyle.Skin localSkin = (XPStyle.Skin)paramArrayOfObject[0];
/* 650 */       TMSchema.Part localPart = localSkin.part;
/* 651 */       TMSchema.State localState = (TMSchema.State)paramArrayOfObject[1];
/* 652 */       if (localState == null) {
/* 653 */         localState = localSkin.state;
/*     */       }
/* 655 */       if (paramComponent == null) {
/* 656 */         paramComponent = localSkin.component;
/*     */       }
/* 658 */       BufferedImage localBufferedImage = (BufferedImage)paramImage;
/*     */ 
/* 660 */       WritableRaster localWritableRaster = localBufferedImage.getRaster();
/* 661 */       DataBufferInt localDataBufferInt = (DataBufferInt)localWritableRaster.getDataBuffer();
/*     */ 
/* 664 */       ThemeReader.paintBackground(SunWritableRaster.stealData(localDataBufferInt, 0), localPart.getControlName(paramComponent), localPart.getValue(), TMSchema.State.getValue(localPart, localState), 0, 0, paramInt1, paramInt2, paramInt1);
/*     */ 
/* 668 */       SunWritableRaster.markDirty(localDataBufferInt);
/*     */     }
/*     */ 
/*     */     protected Image createImage(Component paramComponent, int paramInt1, int paramInt2, GraphicsConfiguration paramGraphicsConfiguration, Object[] paramArrayOfObject)
/*     */     {
/* 673 */       return new BufferedImage(paramInt1, paramInt2, 2);
/*     */     }
/*     */   }
/*     */ 
/*     */   private class XPEmptyBorder extends EmptyBorder
/*     */     implements UIResource
/*     */   {
/*     */     XPEmptyBorder(Insets arg2)
/*     */     {
/* 404 */       super(localObject.left + 2, localObject.bottom + 2, localObject.right + 2);
/*     */     }
/*     */ 
/*     */     public Insets getBorderInsets(Component paramComponent, Insets paramInsets) {
/* 408 */       paramInsets = super.getBorderInsets(paramComponent, paramInsets);
/*     */ 
/* 410 */       Object localObject = null;
/* 411 */       if ((paramComponent instanceof AbstractButton)) {
/* 412 */         Insets localInsets = ((AbstractButton)paramComponent).getMargin();
/*     */ 
/* 415 */         if (((paramComponent.getParent() instanceof JToolBar)) && (!(paramComponent instanceof JRadioButton)) && (!(paramComponent instanceof JCheckBox)) && ((localInsets instanceof InsetsUIResource)))
/*     */         {
/* 419 */           paramInsets.top -= 2;
/* 420 */           paramInsets.left -= 2;
/* 421 */           paramInsets.bottom -= 2;
/* 422 */           paramInsets.right -= 2;
/*     */         } else {
/* 424 */           localObject = localInsets;
/*     */         }
/* 426 */       } else if ((paramComponent instanceof JToolBar)) {
/* 427 */         localObject = ((JToolBar)paramComponent).getMargin();
/* 428 */       } else if ((paramComponent instanceof JTextComponent)) {
/* 429 */         localObject = ((JTextComponent)paramComponent).getMargin();
/*     */       }
/* 431 */       if (localObject != null) {
/* 432 */         paramInsets.top = (((Insets)localObject).top + 2);
/* 433 */         paramInsets.left = (((Insets)localObject).left + 2);
/* 434 */         paramInsets.bottom = (((Insets)localObject).bottom + 2);
/* 435 */         paramInsets.right = (((Insets)localObject).right + 2);
/*     */       }
/* 437 */       return paramInsets;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class XPFillBorder extends LineBorder
/*     */     implements UIResource
/*     */   {
/*     */     XPFillBorder(Color paramInt, int arg3)
/*     */     {
/* 310 */       super(i);
/*     */     }
/*     */ 
/*     */     public Insets getBorderInsets(Component paramComponent, Insets paramInsets) {
/* 314 */       Insets localInsets = null;
/*     */ 
/* 320 */       if ((paramComponent instanceof AbstractButton))
/* 321 */         localInsets = ((AbstractButton)paramComponent).getMargin();
/* 322 */       else if ((paramComponent instanceof JToolBar))
/* 323 */         localInsets = ((JToolBar)paramComponent).getMargin();
/* 324 */       else if ((paramComponent instanceof JTextComponent)) {
/* 325 */         localInsets = ((JTextComponent)paramComponent).getMargin();
/*     */       }
/* 327 */       paramInsets.top = ((localInsets != null ? localInsets.top : 0) + this.thickness);
/* 328 */       paramInsets.left = ((localInsets != null ? localInsets.left : 0) + this.thickness);
/* 329 */       paramInsets.bottom = ((localInsets != null ? localInsets.bottom : 0) + this.thickness);
/* 330 */       paramInsets.right = ((localInsets != null ? localInsets.right : 0) + this.thickness);
/*     */ 
/* 332 */       return paramInsets;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class XPImageBorder extends AbstractBorder
/*     */     implements UIResource
/*     */   {
/*     */     XPStyle.Skin skin;
/*     */ 
/*     */     XPImageBorder(Component paramPart, TMSchema.Part arg3)
/*     */     {
/*     */       TMSchema.Part localPart;
/* 367 */       this.skin = XPStyle.this.getSkin(paramPart, localPart);
/*     */     }
/*     */ 
/*     */     public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */     {
/* 372 */       this.skin.paintSkin(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
/*     */     }
/*     */ 
/*     */     public Insets getBorderInsets(Component paramComponent, Insets paramInsets) {
/* 376 */       Insets localInsets1 = null;
/* 377 */       Insets localInsets2 = this.skin.getContentMargin();
/* 378 */       if (localInsets2 == null) {
/* 379 */         localInsets2 = new Insets(0, 0, 0, 0);
/*     */       }
/*     */ 
/* 386 */       if ((paramComponent instanceof AbstractButton))
/* 387 */         localInsets1 = ((AbstractButton)paramComponent).getMargin();
/* 388 */       else if ((paramComponent instanceof JToolBar))
/* 389 */         localInsets1 = ((JToolBar)paramComponent).getMargin();
/* 390 */       else if ((paramComponent instanceof JTextComponent)) {
/* 391 */         localInsets1 = ((JTextComponent)paramComponent).getMargin();
/*     */       }
/* 393 */       paramInsets.top = ((localInsets1 != null ? localInsets1.top : 0) + localInsets2.top);
/* 394 */       paramInsets.left = ((localInsets1 != null ? localInsets1.left : 0) + localInsets2.left);
/* 395 */       paramInsets.bottom = ((localInsets1 != null ? localInsets1.bottom : 0) + localInsets2.bottom);
/* 396 */       paramInsets.right = ((localInsets1 != null ? localInsets1.right : 0) + localInsets2.right);
/*     */ 
/* 398 */       return paramInsets;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class XPStatefulFillBorder extends XPStyle.XPFillBorder
/*     */   {
/*     */     private final TMSchema.Part part;
/*     */     private final TMSchema.Prop prop;
/*     */ 
/*     */     XPStatefulFillBorder(Color paramInt, int paramPart, TMSchema.Part paramProp, TMSchema.Prop arg5)
/*     */     {
/* 340 */       super(paramInt, paramPart);
/* 341 */       this.part = paramProp;
/*     */       Object localObject;
/* 342 */       this.prop = localObject;
/*     */     }
/*     */ 
/*     */     public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/* 346 */       TMSchema.State localState = TMSchema.State.NORMAL;
/*     */ 
/* 349 */       if ((paramComponent instanceof JComboBox)) {
/* 350 */         JComboBox localJComboBox = (JComboBox)paramComponent;
/*     */ 
/* 353 */         if ((localJComboBox.getUI() instanceof WindowsComboBoxUI)) {
/* 354 */           WindowsComboBoxUI localWindowsComboBoxUI = (WindowsComboBoxUI)localJComboBox.getUI();
/* 355 */           localState = localWindowsComboBoxUI.getXPComboBoxState(localJComboBox);
/*     */         }
/*     */       }
/* 358 */       this.lineColor = XPStyle.this.getColor(paramComponent, this.part, localState, this.prop, Color.black);
/* 359 */       super.paintBorder(paramComponent, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.java.swing.plaf.windows.XPStyle
 * JD-Core Version:    0.6.2
 */