/*      */ package com.sun.java.swing.plaf.gtk;
/*      */ 
/*      */ import java.awt.AWTEvent;
/*      */ import java.awt.Color;
/*      */ import java.awt.Component;
/*      */ import java.awt.Container;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.FocusTraversalPolicy;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.Graphics2D;
/*      */ import java.awt.GridBagConstraints;
/*      */ import java.awt.GridBagLayout;
/*      */ import java.awt.Image;
/*      */ import java.awt.Insets;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.awt.image.BufferedImage;
/*      */ import javax.swing.AbstractAction;
/*      */ import javax.swing.ActionMap;
/*      */ import javax.swing.Box;
/*      */ import javax.swing.Icon;
/*      */ import javax.swing.InputMap;
/*      */ import javax.swing.JColorChooser;
/*      */ import javax.swing.JComponent;
/*      */ import javax.swing.JFormattedTextField;
/*      */ import javax.swing.JLabel;
/*      */ import javax.swing.JPanel;
/*      */ import javax.swing.JSeparator;
/*      */ import javax.swing.JSpinner;
/*      */ import javax.swing.JSpinner.DefaultEditor;
/*      */ import javax.swing.JTextField;
/*      */ import javax.swing.KeyStroke;
/*      */ import javax.swing.SpinnerNumberModel;
/*      */ import javax.swing.SwingUtilities;
/*      */ import javax.swing.UIDefaults;
/*      */ import javax.swing.UIManager;
/*      */ import javax.swing.colorchooser.AbstractColorChooserPanel;
/*      */ import javax.swing.colorchooser.ColorSelectionModel;
/*      */ import javax.swing.event.ChangeEvent;
/*      */ import javax.swing.event.ChangeListener;
/*      */ import javax.swing.plaf.ActionMapUIResource;
/*      */ 
/*      */ class GTKColorChooserPanel extends AbstractColorChooserPanel
/*      */   implements ChangeListener
/*      */ {
/*      */   private static final float PI_3 = 1.047198F;
/*      */   private ColorTriangle triangle;
/*      */   private JLabel lastLabel;
/*      */   private JLabel label;
/*      */   private JSpinner hueSpinner;
/*      */   private JSpinner saturationSpinner;
/*      */   private JSpinner valueSpinner;
/*      */   private JSpinner redSpinner;
/*      */   private JSpinner greenSpinner;
/*      */   private JSpinner blueSpinner;
/*      */   private JTextField colorNameTF;
/*      */   private boolean settingColor;
/*      */   private float hue;
/*      */   private float saturation;
/*      */   private float brightness;
/*      */   private static final int FLAGS_CHANGED_ANGLE = 1;
/*      */   private static final int FLAGS_DRAGGING = 2;
/*      */   private static final int FLAGS_DRAGGING_TRIANGLE = 4;
/*      */   private static final int FLAGS_SETTING_COLOR = 8;
/*      */   private static final int FLAGS_FOCUSED_WHEEL = 16;
/*      */   private static final int FLAGS_FOCUSED_TRIANGLE = 32;
/*      */ 
/*      */   static void compositeRequestFocus(Component paramComponent, boolean paramBoolean)
/*      */   {
/*   74 */     if ((paramComponent instanceof Container)) {
/*   75 */       Container localContainer = (Container)paramComponent;
/*      */       Object localObject2;
/*   76 */       if (localContainer.isFocusCycleRoot()) {
/*   77 */         localObject1 = localContainer.getFocusTraversalPolicy();
/*      */ 
/*   79 */         localObject2 = ((FocusTraversalPolicy)localObject1).getDefaultComponent(localContainer);
/*   80 */         if (localObject2 != null) {
/*   81 */           ((Component)localObject2).requestFocus();
/*   82 */           return;
/*      */         }
/*      */       }
/*   85 */       Object localObject1 = localContainer.getFocusCycleRootAncestor();
/*   86 */       if (localObject1 != null) {
/*   87 */         localObject2 = ((Container)localObject1).getFocusTraversalPolicy();
/*      */         Component localComponent;
/*   91 */         if (paramBoolean) {
/*   92 */           localComponent = ((FocusTraversalPolicy)localObject2).getComponentAfter((Container)localObject1, localContainer);
/*      */         }
/*      */         else {
/*   95 */           localComponent = ((FocusTraversalPolicy)localObject2).getComponentBefore((Container)localObject1, localContainer);
/*      */         }
/*   97 */         if (localComponent != null) {
/*   98 */           localComponent.requestFocus();
/*   99 */           return;
/*      */         }
/*      */       }
/*      */     }
/*  103 */     paramComponent.requestFocus();
/*      */   }
/*      */ 
/*      */   public String getDisplayName()
/*      */   {
/*  111 */     return (String)UIManager.get("GTKColorChooserPanel.nameText");
/*      */   }
/*      */ 
/*      */   public int getMnemonic()
/*      */   {
/*  118 */     String str = (String)UIManager.get("GTKColorChooserPanel.mnemonic");
/*      */ 
/*  120 */     if (str != null)
/*      */       try {
/*  122 */         return Integer.parseInt(str);
/*      */       }
/*      */       catch (NumberFormatException localNumberFormatException)
/*      */       {
/*      */       }
/*  127 */     return -1;
/*      */   }
/*      */ 
/*      */   public int getDisplayedMnemonicIndex()
/*      */   {
/*  134 */     String str = (String)UIManager.get("GTKColorChooserPanel.displayedMnemonicIndex");
/*      */ 
/*  137 */     if (str != null)
/*      */       try {
/*  139 */         return Integer.parseInt(str);
/*      */       }
/*      */       catch (NumberFormatException localNumberFormatException)
/*      */       {
/*      */       }
/*  144 */     return -1;
/*      */   }
/*      */ 
/*      */   public Icon getSmallDisplayIcon() {
/*  148 */     return null;
/*      */   }
/*      */ 
/*      */   public Icon getLargeDisplayIcon() {
/*  152 */     return null;
/*      */   }
/*      */ 
/*      */   public void uninstallChooserPanel(JColorChooser paramJColorChooser) {
/*  156 */     super.uninstallChooserPanel(paramJColorChooser);
/*  157 */     removeAll();
/*      */   }
/*      */ 
/*      */   protected void buildChooser()
/*      */   {
/*  164 */     this.triangle = new ColorTriangle();
/*  165 */     this.triangle.setName("GTKColorChooserPanel.triangle");
/*      */ 
/*  169 */     this.label = new OpaqueLabel(null);
/*  170 */     this.label.setName("GTKColorChooserPanel.colorWell");
/*  171 */     this.label.setOpaque(true);
/*  172 */     this.label.setMinimumSize(new Dimension(67, 32));
/*  173 */     this.label.setPreferredSize(new Dimension(67, 32));
/*  174 */     this.label.setMaximumSize(new Dimension(67, 32));
/*      */ 
/*  178 */     this.lastLabel = new OpaqueLabel(null);
/*  179 */     this.lastLabel.setName("GTKColorChooserPanel.lastColorWell");
/*  180 */     this.lastLabel.setOpaque(true);
/*  181 */     this.lastLabel.setMinimumSize(new Dimension(67, 32));
/*  182 */     this.lastLabel.setPreferredSize(new Dimension(67, 32));
/*  183 */     this.lastLabel.setMaximumSize(new Dimension(67, 32));
/*      */ 
/*  185 */     this.hueSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 360, 1));
/*  186 */     configureSpinner(this.hueSpinner, "GTKColorChooserPanel.hueSpinner");
/*  187 */     this.saturationSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 255, 1));
/*  188 */     configureSpinner(this.saturationSpinner, "GTKColorChooserPanel.saturationSpinner");
/*      */ 
/*  190 */     this.valueSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 255, 1));
/*  191 */     configureSpinner(this.valueSpinner, "GTKColorChooserPanel.valueSpinner");
/*  192 */     this.redSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 255, 1));
/*  193 */     configureSpinner(this.redSpinner, "GTKColorChooserPanel.redSpinner");
/*  194 */     this.greenSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 255, 1));
/*  195 */     configureSpinner(this.greenSpinner, "GTKColorChooserPanel.greenSpinner");
/*  196 */     this.blueSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 255, 1));
/*  197 */     configureSpinner(this.blueSpinner, "GTKColorChooserPanel.blueSpinner");
/*      */ 
/*  199 */     this.colorNameTF = new JTextField(8);
/*      */ 
/*  201 */     setLayout(new GridBagLayout());
/*      */ 
/*  203 */     add(this, "GTKColorChooserPanel.hue", this.hueSpinner, -1, -1);
/*  204 */     add(this, "GTKColorChooserPanel.red", this.redSpinner, -1, -1);
/*  205 */     add(this, "GTKColorChooserPanel.saturation", this.saturationSpinner, -1, -1);
/*  206 */     add(this, "GTKColorChooserPanel.green", this.greenSpinner, -1, -1);
/*  207 */     add(this, "GTKColorChooserPanel.value", this.valueSpinner, -1, -1);
/*  208 */     add(this, "GTKColorChooserPanel.blue", this.blueSpinner, -1, -1);
/*      */ 
/*  210 */     add(new JSeparator(0), new GridBagConstraints(1, 3, 4, 1, 1.0D, 0.0D, 21, 2, new Insets(14, 0, 0, 0), 0, 0));
/*      */ 
/*  215 */     add(this, "GTKColorChooserPanel.colorName", this.colorNameTF, 0, 4);
/*      */ 
/*  217 */     add(this.triangle, new GridBagConstraints(0, 0, 1, 5, 0.0D, 0.0D, 21, 0, new Insets(14, 20, 2, 9), 0, 0));
/*      */ 
/*  221 */     Box localBox = Box.createHorizontalBox();
/*  222 */     localBox.add(this.lastLabel);
/*  223 */     localBox.add(this.label);
/*  224 */     add(localBox, new GridBagConstraints(0, 5, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
/*      */ 
/*  228 */     add(new JSeparator(0), new GridBagConstraints(0, 6, 5, 1, 1.0D, 0.0D, 21, 2, new Insets(12, 0, 0, 0), 0, 0));
/*      */   }
/*      */ 
/*      */   private void configureSpinner(JSpinner paramJSpinner, String paramString)
/*      */   {
/*  238 */     paramJSpinner.addChangeListener(this);
/*  239 */     paramJSpinner.setName(paramString);
/*  240 */     JComponent localJComponent = paramJSpinner.getEditor();
/*  241 */     if ((localJComponent instanceof JSpinner.DefaultEditor)) {
/*  242 */       JFormattedTextField localJFormattedTextField = ((JSpinner.DefaultEditor)localJComponent).getTextField();
/*      */ 
/*  245 */       localJFormattedTextField.setFocusLostBehavior(1);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void add(Container paramContainer, String paramString, JComponent paramJComponent, int paramInt1, int paramInt2)
/*      */   {
/*  254 */     JLabel localJLabel = new JLabel(UIManager.getString(paramString + "Text", getLocale()));
/*      */ 
/*  256 */     String str = (String)UIManager.get(paramString + "Mnemonic", getLocale());
/*      */ 
/*  258 */     if (str != null) {
/*      */       try {
/*  260 */         localJLabel.setDisplayedMnemonic(Integer.parseInt(str));
/*      */       } catch (NumberFormatException localNumberFormatException1) {
/*      */       }
/*  263 */       localObject = (String)UIManager.get(paramString + "MnemonicIndex", getLocale());
/*      */ 
/*  266 */       if (localObject != null)
/*      */         try {
/*  268 */           localJLabel.setDisplayedMnemonicIndex(Integer.parseInt((String)localObject));
/*      */         }
/*      */         catch (NumberFormatException localNumberFormatException2)
/*      */         {
/*      */         }
/*      */     }
/*  274 */     localJLabel.setLabelFor(paramJComponent);
/*  275 */     if (paramInt1 < 0) {
/*  276 */       paramInt1 = paramContainer.getComponentCount() % 4;
/*      */     }
/*  278 */     if (paramInt2 < 0) {
/*  279 */       paramInt2 = paramContainer.getComponentCount() / 4;
/*      */     }
/*  281 */     Object localObject = new GridBagConstraints(paramInt1 + 1, paramInt2, 1, 1, 0.0D, 0.0D, 24, 0, new Insets(4, 0, 0, 4), 0, 0);
/*      */ 
/*  284 */     if (paramInt2 == 0) {
/*  285 */       ((GridBagConstraints)localObject).insets.top = 14;
/*      */     }
/*  287 */     paramContainer.add(localJLabel, localObject);
/*  288 */     localObject.gridx += 1;
/*  289 */     paramContainer.add(paramJComponent, localObject);
/*      */   }
/*      */ 
/*      */   public void updateChooser()
/*      */   {
/*  296 */     if (!this.settingColor) {
/*  297 */       this.lastLabel.setBackground(getColorFromModel());
/*  298 */       setColor(getColorFromModel(), true, true, false);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void setRed(int paramInt)
/*      */   {
/*  306 */     setRGB(paramInt << 16 | getColor().getGreen() << 8 | getColor().getBlue());
/*      */   }
/*      */ 
/*      */   private void setGreen(int paramInt)
/*      */   {
/*  313 */     setRGB(getColor().getRed() << 16 | paramInt << 8 | getColor().getBlue());
/*      */   }
/*      */ 
/*      */   private void setBlue(int paramInt)
/*      */   {
/*  320 */     setRGB(getColor().getRed() << 16 | getColor().getGreen() << 8 | paramInt);
/*      */   }
/*      */ 
/*      */   private void setHue(float paramFloat, boolean paramBoolean)
/*      */   {
/*  328 */     setHSB(paramFloat, this.saturation, this.brightness);
/*  329 */     if (paramBoolean) {
/*  330 */       this.settingColor = true;
/*  331 */       this.hueSpinner.setValue(Integer.valueOf((int)(paramFloat * 360.0F)));
/*  332 */       this.settingColor = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   private float getHue()
/*      */   {
/*  340 */     return this.hue;
/*      */   }
/*      */ 
/*      */   private void setSaturation(float paramFloat)
/*      */   {
/*  347 */     setHSB(this.hue, paramFloat, this.brightness);
/*      */   }
/*      */ 
/*      */   private float getSaturation()
/*      */   {
/*  354 */     return this.saturation;
/*      */   }
/*      */ 
/*      */   private void setBrightness(float paramFloat)
/*      */   {
/*  361 */     setHSB(this.hue, this.saturation, paramFloat);
/*      */   }
/*      */ 
/*      */   private float getBrightness()
/*      */   {
/*  368 */     return this.brightness;
/*      */   }
/*      */ 
/*      */   private void setSaturationAndBrightness(float paramFloat1, float paramFloat2, boolean paramBoolean)
/*      */   {
/*  376 */     setHSB(this.hue, paramFloat1, paramFloat2);
/*  377 */     if (paramBoolean) {
/*  378 */       this.settingColor = true;
/*  379 */       this.saturationSpinner.setValue(Integer.valueOf((int)(paramFloat1 * 255.0F)));
/*  380 */       this.valueSpinner.setValue(Integer.valueOf((int)(paramFloat2 * 255.0F)));
/*  381 */       this.settingColor = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void setRGB(int paramInt)
/*      */   {
/*  389 */     Color localColor = new Color(paramInt);
/*      */ 
/*  391 */     setColor(localColor, false, true, true);
/*      */ 
/*  393 */     this.settingColor = true;
/*  394 */     this.hueSpinner.setValue(Integer.valueOf((int)(this.hue * 360.0F)));
/*  395 */     this.saturationSpinner.setValue(Integer.valueOf((int)(this.saturation * 255.0F)));
/*  396 */     this.valueSpinner.setValue(Integer.valueOf((int)(this.brightness * 255.0F)));
/*  397 */     this.settingColor = false;
/*      */   }
/*      */ 
/*      */   private void setHSB(float paramFloat1, float paramFloat2, float paramFloat3)
/*      */   {
/*  404 */     Color localColor = Color.getHSBColor(paramFloat1, paramFloat2, paramFloat3);
/*      */ 
/*  406 */     this.hue = paramFloat1;
/*  407 */     this.saturation = paramFloat2;
/*  408 */     this.brightness = paramFloat3;
/*  409 */     setColor(localColor, false, false, true);
/*      */ 
/*  411 */     this.settingColor = true;
/*  412 */     this.redSpinner.setValue(Integer.valueOf(localColor.getRed()));
/*  413 */     this.greenSpinner.setValue(Integer.valueOf(localColor.getGreen()));
/*  414 */     this.blueSpinner.setValue(Integer.valueOf(localColor.getBlue()));
/*  415 */     this.settingColor = false;
/*      */   }
/*      */ 
/*      */   private void setColor(Color paramColor, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
/*      */   {
/*  430 */     if (paramColor == null) {
/*  431 */       paramColor = Color.BLACK;
/*      */     }
/*      */ 
/*  434 */     this.settingColor = true;
/*      */ 
/*  436 */     if (paramBoolean2) {
/*  437 */       localObject = Color.RGBtoHSB(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), null);
/*      */ 
/*  439 */       this.hue = localObject[0];
/*  440 */       this.saturation = localObject[1];
/*  441 */       this.brightness = localObject[2];
/*      */     }
/*      */ 
/*  444 */     if (paramBoolean3) {
/*  445 */       localObject = getColorSelectionModel();
/*  446 */       if (localObject != null) {
/*  447 */         ((ColorSelectionModel)localObject).setSelectedColor(paramColor);
/*      */       }
/*      */     }
/*      */ 
/*  451 */     this.triangle.setColor(this.hue, this.saturation, this.brightness);
/*  452 */     this.label.setBackground(paramColor);
/*      */ 
/*  455 */     Object localObject = Integer.toHexString(paramColor.getRGB() & 0xFFFFFF | 0x1000000);
/*      */ 
/*  457 */     this.colorNameTF.setText("#" + ((String)localObject).substring(1));
/*      */ 
/*  459 */     if (paramBoolean1) {
/*  460 */       this.redSpinner.setValue(Integer.valueOf(paramColor.getRed()));
/*  461 */       this.greenSpinner.setValue(Integer.valueOf(paramColor.getGreen()));
/*  462 */       this.blueSpinner.setValue(Integer.valueOf(paramColor.getBlue()));
/*      */ 
/*  464 */       this.hueSpinner.setValue(Integer.valueOf((int)(this.hue * 360.0F)));
/*  465 */       this.saturationSpinner.setValue(Integer.valueOf((int)(this.saturation * 255.0F)));
/*  466 */       this.valueSpinner.setValue(Integer.valueOf((int)(this.brightness * 255.0F)));
/*      */     }
/*  468 */     this.settingColor = false;
/*      */   }
/*      */ 
/*      */   public Color getColor() {
/*  472 */     return this.label.getBackground();
/*      */   }
/*      */ 
/*      */   public void stateChanged(ChangeEvent paramChangeEvent)
/*      */   {
/*  479 */     if (this.settingColor) {
/*  480 */       return;
/*      */     }
/*  482 */     Color localColor = getColor();
/*      */ 
/*  484 */     if (paramChangeEvent.getSource() == this.hueSpinner) {
/*  485 */       setHue(((Number)this.hueSpinner.getValue()).floatValue() / 360.0F, false);
/*      */     }
/*  487 */     else if (paramChangeEvent.getSource() == this.saturationSpinner) {
/*  488 */       setSaturation(((Number)this.saturationSpinner.getValue()).floatValue() / 255.0F);
/*      */     }
/*  491 */     else if (paramChangeEvent.getSource() == this.valueSpinner) {
/*  492 */       setBrightness(((Number)this.valueSpinner.getValue()).floatValue() / 255.0F);
/*      */     }
/*  495 */     else if (paramChangeEvent.getSource() == this.redSpinner) {
/*  496 */       setRed(((Number)this.redSpinner.getValue()).intValue());
/*      */     }
/*  498 */     else if (paramChangeEvent.getSource() == this.greenSpinner) {
/*  499 */       setGreen(((Number)this.greenSpinner.getValue()).intValue());
/*      */     }
/*  501 */     else if (paramChangeEvent.getSource() == this.blueSpinner)
/*  502 */       setBlue(((Number)this.blueSpinner.getValue()).intValue());
/*      */   }
/*      */ 
/*      */   private static class ColorAction extends AbstractAction
/*      */   {
/*      */     private int type;
/*      */ 
/*      */     ColorAction(String paramString, int paramInt)
/*      */     {
/* 1236 */       super();
/* 1237 */       this.type = paramInt;
/*      */     }
/*      */ 
/*      */     public void actionPerformed(ActionEvent paramActionEvent) {
/* 1241 */       GTKColorChooserPanel.ColorTriangle localColorTriangle = (GTKColorChooserPanel.ColorTriangle)paramActionEvent.getSource();
/*      */ 
/* 1243 */       if (localColorTriangle.isWheelFocused()) {
/* 1244 */         float f = localColorTriangle.getGTKColorChooserPanel().getHue();
/*      */ 
/* 1246 */         switch (this.type) {
/*      */         case 0:
/*      */         case 2:
/* 1249 */           localColorTriangle.incrementHue(true);
/* 1250 */           break;
/*      */         case 1:
/*      */         case 3:
/* 1253 */           localColorTriangle.incrementHue(false);
/* 1254 */           break;
/*      */         case 4:
/* 1256 */           localColorTriangle.focusTriangle();
/* 1257 */           break;
/*      */         case 5:
/* 1259 */           GTKColorChooserPanel.compositeRequestFocus(localColorTriangle, false);
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 1264 */         int i = 0;
/* 1265 */         int j = 0;
/*      */ 
/* 1267 */         switch (this.type)
/*      */         {
/*      */         case 0:
/* 1270 */           j--;
/* 1271 */           break;
/*      */         case 1:
/* 1274 */           j++;
/* 1275 */           break;
/*      */         case 2:
/* 1278 */           i--;
/* 1279 */           break;
/*      */         case 3:
/* 1282 */           i++;
/* 1283 */           break;
/*      */         case 4:
/* 1285 */           GTKColorChooserPanel.compositeRequestFocus(localColorTriangle, true);
/* 1286 */           return;
/*      */         case 5:
/* 1288 */           localColorTriangle.focusWheel();
/* 1289 */           return;
/*      */         }
/* 1291 */         localColorTriangle.adjustSB(localColorTriangle.getColorX() + i, localColorTriangle.getColorY() + j, true);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private class ColorTriangle extends JPanel
/*      */   {
/*      */     private Image wheelImage;
/*      */     private Image triangleImage;
/*      */     private double angle;
/*      */     private int flags;
/*      */     private int circleX;
/*      */     private int circleY;
/*      */ 
/*      */     public ColorTriangle()
/*      */     {
/*  570 */       enableEvents(4L);
/*  571 */       enableEvents(16L);
/*  572 */       enableEvents(32L);
/*      */ 
/*  574 */       setMinimumSize(new Dimension(getWheelRadius() * 2 + 2, getWheelRadius() * 2 + 2));
/*      */ 
/*  576 */       setPreferredSize(new Dimension(getWheelRadius() * 2 + 2, getWheelRadius() * 2 + 2));
/*      */ 
/*  580 */       setFocusTraversalKeysEnabled(false);
/*      */ 
/*  583 */       getInputMap().put(KeyStroke.getKeyStroke("UP"), "up");
/*  584 */       getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "down");
/*  585 */       getInputMap().put(KeyStroke.getKeyStroke("LEFT"), "left");
/*  586 */       getInputMap().put(KeyStroke.getKeyStroke("RIGHT"), "right");
/*      */ 
/*  588 */       getInputMap().put(KeyStroke.getKeyStroke("KP_UP"), "up");
/*  589 */       getInputMap().put(KeyStroke.getKeyStroke("KP_DOWN"), "down");
/*  590 */       getInputMap().put(KeyStroke.getKeyStroke("KP_LEFT"), "left");
/*  591 */       getInputMap().put(KeyStroke.getKeyStroke("KP_RIGHT"), "right");
/*      */ 
/*  593 */       getInputMap().put(KeyStroke.getKeyStroke("TAB"), "focusNext");
/*  594 */       getInputMap().put(KeyStroke.getKeyStroke("shift TAB"), "focusLast");
/*      */ 
/*  596 */       Object localObject = (ActionMap)UIManager.get("GTKColorChooserPanel.actionMap");
/*      */ 
/*  599 */       if (localObject == null) {
/*  600 */         localObject = new ActionMapUIResource();
/*  601 */         ((ActionMap)localObject).put("left", new GTKColorChooserPanel.ColorAction("left", 2));
/*  602 */         ((ActionMap)localObject).put("right", new GTKColorChooserPanel.ColorAction("right", 3));
/*  603 */         ((ActionMap)localObject).put("up", new GTKColorChooserPanel.ColorAction("up", 0));
/*  604 */         ((ActionMap)localObject).put("down", new GTKColorChooserPanel.ColorAction("down", 1));
/*  605 */         ((ActionMap)localObject).put("focusNext", new GTKColorChooserPanel.ColorAction("focusNext", 4));
/*  606 */         ((ActionMap)localObject).put("focusLast", new GTKColorChooserPanel.ColorAction("focusLast", 5));
/*  607 */         UIManager.getLookAndFeelDefaults().put("GTKColorChooserPanel.actionMap", localObject);
/*      */       }
/*      */ 
/*  610 */       SwingUtilities.replaceUIActionMap(this, (ActionMap)localObject);
/*      */     }
/*      */ 
/*      */     GTKColorChooserPanel getGTKColorChooserPanel()
/*      */     {
/*  617 */       return GTKColorChooserPanel.this;
/*      */     }
/*      */ 
/*      */     void focusWheel()
/*      */     {
/*  624 */       setFocusType(1);
/*      */     }
/*      */ 
/*      */     void focusTriangle()
/*      */     {
/*  631 */       setFocusType(2);
/*      */     }
/*      */ 
/*      */     boolean isWheelFocused()
/*      */     {
/*  638 */       return isSet(16);
/*      */     }
/*      */ 
/*      */     public void setColor(float paramFloat1, float paramFloat2, float paramFloat3)
/*      */     {
/*  645 */       if (isSet(8)) {
/*  646 */         return;
/*      */       }
/*      */ 
/*  649 */       setAngleFromHue(paramFloat1);
/*  650 */       setSaturationAndBrightness(paramFloat2, paramFloat3);
/*      */     }
/*      */ 
/*      */     public Color getColor()
/*      */     {
/*  657 */       return GTKColorChooserPanel.this.getColor();
/*      */     }
/*      */ 
/*      */     int getColorX()
/*      */     {
/*  664 */       return this.circleX + getIndicatorSize() / 2 - getWheelXOrigin();
/*      */     }
/*      */ 
/*      */     int getColorY()
/*      */     {
/*  671 */       return this.circleY + getIndicatorSize() / 2 - getWheelYOrigin();
/*      */     }
/*      */ 
/*      */     protected void processEvent(AWTEvent paramAWTEvent) {
/*  675 */       if ((paramAWTEvent.getID() == 501) || (((isSet(2)) || (isSet(4))) && (paramAWTEvent.getID() == 506)))
/*      */       {
/*  680 */         int i = getWheelRadius();
/*  681 */         int j = ((MouseEvent)paramAWTEvent).getX() - i;
/*  682 */         int k = ((MouseEvent)paramAWTEvent).getY() - i;
/*      */ 
/*  684 */         if (!hasFocus()) {
/*  685 */           requestFocus();
/*      */         }
/*  687 */         if (!isSet(4)) if (adjustHue(j, k, paramAWTEvent.getID() == 501))
/*      */           {
/*  689 */             setFlag(2, true);
/*  690 */             setFocusType(1); break label164;
/*      */           }
/*  692 */         if (adjustSB(j, k, paramAWTEvent.getID() == 501))
/*      */         {
/*  694 */           setFlag(4, true);
/*  695 */           setFocusType(2);
/*      */         }
/*      */         else {
/*  698 */           setFocusType(2);
/*      */         }
/*      */       } else {
/*  701 */         label164: if (paramAWTEvent.getID() == 502)
/*      */         {
/*  703 */           setFlag(4, false);
/*  704 */           setFlag(2, false);
/*      */         }
/*  706 */         else if (paramAWTEvent.getID() == 1005)
/*      */         {
/*  708 */           setFocusType(0);
/*      */         }
/*  710 */         else if (paramAWTEvent.getID() == 1004)
/*      */         {
/*  713 */           if ((!isSet(32)) && (!isSet(16)))
/*      */           {
/*  715 */             setFlag(16, true);
/*  716 */             setFocusType(1);
/*      */           }
/*  718 */           repaint();
/*      */         }
/*      */       }
/*  720 */       super.processEvent(paramAWTEvent);
/*      */     }
/*      */ 
/*      */     public void paintComponent(Graphics paramGraphics) {
/*  724 */       super.paintComponent(paramGraphics);
/*      */ 
/*  727 */       int i = getWheelRadius();
/*  728 */       int j = getWheelWidth();
/*  729 */       Image localImage = getImage(i);
/*  730 */       paramGraphics.drawImage(localImage, getWheelXOrigin() - i, getWheelYOrigin() - i, null);
/*      */ 
/*  734 */       if ((hasFocus()) && (isSet(16))) {
/*  735 */         paramGraphics.setColor(Color.BLACK);
/*  736 */         paramGraphics.drawOval(getWheelXOrigin() - i, getWheelYOrigin() - i, 2 * i, 2 * i);
/*      */ 
/*  738 */         paramGraphics.drawOval(getWheelXOrigin() - i + j, getWheelYOrigin() - i + j, 2 * (i - j), 2 * (i - j));
/*      */       }
/*      */ 
/*  744 */       if ((Math.toDegrees(6.283185307179586D - this.angle) <= 20.0D) || (Math.toDegrees(6.283185307179586D - this.angle) >= 201.0D))
/*      */       {
/*  746 */         paramGraphics.setColor(Color.WHITE);
/*      */       }
/*      */       else {
/*  749 */         paramGraphics.setColor(Color.BLACK);
/*      */       }
/*  751 */       int k = (int)(Math.cos(this.angle) * i);
/*  752 */       int m = (int)(Math.sin(this.angle) * i);
/*  753 */       int n = (int)(Math.cos(this.angle) * (i - j));
/*  754 */       int i1 = (int)(Math.sin(this.angle) * (i - j));
/*  755 */       paramGraphics.drawLine(k + i, m + i, n + i, i1 + i);
/*      */ 
/*  759 */       if ((hasFocus()) && (isSet(32))) {
/*  760 */         Graphics localGraphics = paramGraphics.create();
/*  761 */         int i2 = getTriangleCircumscribedRadius();
/*  762 */         int i3 = (int)(3 * i2 / Math.sqrt(3.0D));
/*  763 */         localGraphics.translate(getWheelXOrigin(), getWheelYOrigin());
/*  764 */         ((Graphics2D)localGraphics).rotate(this.angle + 1.570796326794897D);
/*  765 */         localGraphics.setColor(Color.BLACK);
/*  766 */         localGraphics.drawLine(0, -i2, i3 / 2, i2 / 2);
/*  767 */         localGraphics.drawLine(i3 / 2, i2 / 2, -i3 / 2, i2 / 2);
/*  768 */         localGraphics.drawLine(-i3 / 2, i2 / 2, 0, -i2);
/*  769 */         localGraphics.dispose();
/*      */       }
/*      */ 
/*  773 */       paramGraphics.setColor(Color.BLACK);
/*  774 */       paramGraphics.drawOval(this.circleX, this.circleY, getIndicatorSize() - 1, getIndicatorSize() - 1);
/*      */ 
/*  776 */       paramGraphics.setColor(Color.WHITE);
/*  777 */       paramGraphics.drawOval(this.circleX + 1, this.circleY + 1, getIndicatorSize() - 3, getIndicatorSize() - 3);
/*      */     }
/*      */ 
/*      */     private Image getImage(int paramInt)
/*      */     {
/*  785 */       if ((!isSet(1)) && (this.wheelImage != null) && (this.wheelImage.getWidth(null) == paramInt * 2))
/*      */       {
/*  787 */         return this.wheelImage;
/*      */       }
/*  789 */       if ((this.wheelImage == null) || (this.wheelImage.getWidth(null) != paramInt)) {
/*  790 */         this.wheelImage = getWheelImage(paramInt);
/*      */       }
/*  792 */       int i = getTriangleCircumscribedRadius();
/*  793 */       int j = (int)(i * 3.0D / 2.0D);
/*  794 */       int k = (int)(2 * j / Math.sqrt(3.0D));
/*  795 */       if ((this.triangleImage == null) || (this.triangleImage.getWidth(null) != k)) {
/*  796 */         this.triangleImage = new BufferedImage(k, k, 2);
/*      */       }
/*      */ 
/*  799 */       Graphics localGraphics = this.triangleImage.getGraphics();
/*  800 */       localGraphics.setColor(new Color(0, 0, 0, 0));
/*  801 */       localGraphics.fillRect(0, 0, k, k);
/*  802 */       localGraphics.translate(k / 2, 0);
/*  803 */       paintTriangle(localGraphics, j, getColor());
/*  804 */       localGraphics.translate(-k / 2, 0);
/*  805 */       localGraphics.dispose();
/*      */ 
/*  807 */       localGraphics = this.wheelImage.getGraphics();
/*  808 */       localGraphics.setColor(new Color(0, 0, 0, 0));
/*  809 */       localGraphics.fillOval(getWheelWidth(), getWheelWidth(), 2 * (paramInt - getWheelWidth()), 2 * (paramInt - getWheelWidth()));
/*      */ 
/*  813 */       double d = Math.toRadians(-30.0D) + this.angle;
/*  814 */       localGraphics.translate(paramInt, paramInt);
/*  815 */       ((Graphics2D)localGraphics).rotate(d);
/*  816 */       localGraphics.drawImage(this.triangleImage, -k / 2, getWheelWidth() - paramInt, null);
/*      */ 
/*  818 */       ((Graphics2D)localGraphics).rotate(-d);
/*  819 */       localGraphics.translate(k / 2, paramInt - getWheelWidth());
/*      */ 
/*  821 */       setFlag(1, false);
/*      */ 
/*  823 */       return this.wheelImage;
/*      */     }
/*      */ 
/*      */     private void paintTriangle(Graphics paramGraphics, int paramInt, Color paramColor) {
/*  827 */       float[] arrayOfFloat = Color.RGBtoHSB(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), null);
/*      */ 
/*  830 */       float f1 = arrayOfFloat[0];
/*  831 */       double d = paramInt;
/*  832 */       for (int i = 0; i < paramInt; i++) {
/*  833 */         int j = (int)(i * Math.tan(Math.toRadians(30.0D)));
/*  834 */         float f2 = j * 2;
/*  835 */         if (j > 0) {
/*  836 */           float f3 = (float)(i / d);
/*  837 */           for (int k = -j; k <= j; k++) {
/*  838 */             float f4 = k / f2 + 0.5F;
/*  839 */             paramGraphics.setColor(Color.getHSBColor(f1, f4, f3));
/*  840 */             paramGraphics.fillRect(k, i, 1, 1);
/*      */           }
/*      */         }
/*      */         else {
/*  844 */           paramGraphics.setColor(paramColor);
/*  845 */           paramGraphics.fillRect(0, i, 1, 1);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     private Image getWheelImage(int paramInt)
/*      */     {
/*  857 */       int i = paramInt - getWheelWidth();
/*  858 */       int j = paramInt * 2;
/*  859 */       BufferedImage localBufferedImage = new BufferedImage(j, j, 2);
/*      */ 
/*  862 */       for (int k = -paramInt; k < paramInt; k++) {
/*  863 */         int m = k * k;
/*  864 */         for (int n = -paramInt; n < paramInt; n++) {
/*  865 */           double d = Math.sqrt(m + n * n);
/*      */ 
/*  867 */           if ((d < paramInt) && (d > i)) {
/*  868 */             int i1 = colorWheelLocationToRGB(n, k, d) | 0xFF000000;
/*      */ 
/*  870 */             localBufferedImage.setRGB(n + paramInt, k + paramInt, i1);
/*      */           }
/*      */         }
/*      */       }
/*  874 */       this.wheelImage = localBufferedImage;
/*  875 */       return this.wheelImage;
/*      */     }
/*      */ 
/*      */     boolean adjustSB(int paramInt1, int paramInt2, boolean paramBoolean)
/*      */     {
/*  891 */       int i = getWheelRadius() - getWheelWidth();
/*  892 */       int j = 0;
/*      */ 
/*  894 */       paramInt2 = -paramInt2;
/*  895 */       if ((paramBoolean) && ((paramInt1 < -i) || (paramInt1 > i) || (paramInt2 < -i) || (paramInt2 > i)))
/*      */       {
/*  897 */         return false;
/*      */       }
/*      */ 
/*  900 */       int k = i * 3 / 2;
/*  901 */       double d1 = Math.cos(this.angle) * paramInt1 - Math.sin(this.angle) * paramInt2;
/*  902 */       double d2 = Math.sin(this.angle) * paramInt1 + Math.cos(this.angle) * paramInt2;
/*  903 */       if (d1 < -(i / 2)) {
/*  904 */         if (paramBoolean) {
/*  905 */           return false;
/*      */         }
/*  907 */         d1 = -i / 2;
/*  908 */         j = 1;
/*      */       }
/*  910 */       else if ((int)d1 > i) {
/*  911 */         if (paramBoolean) {
/*  912 */           return false;
/*      */         }
/*  914 */         d1 = i;
/*  915 */         j = 1;
/*      */       }
/*      */ 
/*  918 */       int m = (int)((k - d1 - i / 2.0D) * Math.tan(Math.toRadians(30.0D)));
/*      */ 
/*  920 */       if (d2 <= -m) {
/*  921 */         if (paramBoolean) {
/*  922 */           return false;
/*      */         }
/*  924 */         d2 = -m;
/*  925 */         j = 1;
/*      */       }
/*  927 */       else if (d2 > m) {
/*  928 */         if (paramBoolean) {
/*  929 */           return false;
/*      */         }
/*  931 */         d2 = m;
/*  932 */         j = 1;
/*      */       }
/*      */ 
/*  935 */       double d3 = Math.cos(Math.toRadians(-30.0D)) * d1 - Math.sin(Math.toRadians(-30.0D)) * d2;
/*      */ 
/*  937 */       double d4 = Math.sin(Math.toRadians(-30.0D)) * d1 + Math.cos(Math.toRadians(-30.0D)) * d2;
/*      */ 
/*  939 */       float f1 = Math.min(1.0F, (float)((i - d4) / k));
/*      */ 
/*  941 */       float f2 = (float)(Math.tan(Math.toRadians(30.0D)) * (i - d4));
/*  942 */       float f3 = Math.min(1.0F, (float)(d3 / f2 / 2.0D + 0.5D));
/*      */ 
/*  944 */       setFlag(8, true);
/*  945 */       if (j != 0) {
/*  946 */         setSaturationAndBrightness(f3, f1);
/*      */       }
/*      */       else {
/*  949 */         setSaturationAndBrightness(f3, f1, paramInt1 + getWheelXOrigin(), getWheelYOrigin() - paramInt2);
/*      */       }
/*      */ 
/*  952 */       GTKColorChooserPanel.this.setSaturationAndBrightness(f3, f1, true);
/*      */ 
/*  954 */       setFlag(8, false);
/*  955 */       return true;
/*      */     }
/*      */ 
/*      */     private void setSaturationAndBrightness(float paramFloat1, float paramFloat2)
/*      */     {
/*  962 */       int i = getTriangleCircumscribedRadius();
/*  963 */       int j = i * 3 / 2;
/*  964 */       double d1 = paramFloat2 * j;
/*  965 */       double d2 = d1 * Math.tan(Math.toRadians(30.0D));
/*  966 */       double d3 = 2.0D * d2 * paramFloat1 - d2;
/*  967 */       d1 -= i;
/*  968 */       double d4 = Math.cos(Math.toRadians(-60.0D) - this.angle) * d1 - Math.sin(Math.toRadians(-60.0D) - this.angle) * d3;
/*      */ 
/*  970 */       double d5 = Math.sin(Math.toRadians(-60.0D) - this.angle) * d1 + Math.cos(Math.toRadians(-60.0D) - this.angle) * d3;
/*      */ 
/*  972 */       int k = (int)d4 + getWheelXOrigin();
/*  973 */       int m = getWheelYOrigin() - (int)d5;
/*      */ 
/*  975 */       setSaturationAndBrightness(paramFloat1, paramFloat2, k, m);
/*      */     }
/*      */ 
/*      */     private void setSaturationAndBrightness(float paramFloat1, float paramFloat2, int paramInt1, int paramInt2)
/*      */     {
/*  984 */       paramInt1 -= getIndicatorSize() / 2;
/*  985 */       paramInt2 -= getIndicatorSize() / 2;
/*      */ 
/*  987 */       int i = Math.min(paramInt1, this.circleX);
/*  988 */       int j = Math.min(paramInt2, this.circleY);
/*      */ 
/*  990 */       repaint(i, j, Math.max(this.circleX, paramInt1) - i + getIndicatorSize() + 1, Math.max(this.circleY, paramInt2) - j + getIndicatorSize() + 1);
/*      */ 
/*  993 */       this.circleX = paramInt1;
/*  994 */       this.circleY = paramInt2;
/*      */     }
/*      */ 
/*      */     private boolean adjustHue(int paramInt1, int paramInt2, boolean paramBoolean)
/*      */     {
/* 1010 */       double d1 = Math.sqrt(paramInt1 * paramInt1 + paramInt2 * paramInt2);
/* 1011 */       int i = getWheelRadius();
/*      */ 
/* 1013 */       if ((!paramBoolean) || ((d1 >= i - getWheelWidth()) && (d1 < i)))
/*      */       {
/*      */         double d2;
/* 1016 */         if (paramInt1 == 0) {
/* 1017 */           if (paramInt2 > 0) {
/* 1018 */             d2 = 1.570796326794897D;
/*      */           }
/*      */           else
/* 1021 */             d2 = 4.71238898038469D;
/*      */         }
/*      */         else
/*      */         {
/* 1025 */           d2 = Math.atan(paramInt2 / paramInt1);
/* 1026 */           if (paramInt1 < 0) {
/* 1027 */             d2 += 3.141592653589793D;
/*      */           }
/* 1029 */           else if (d2 < 0.0D) {
/* 1030 */             d2 += 6.283185307179586D;
/*      */           }
/*      */         }
/* 1033 */         setFlag(8, true);
/* 1034 */         GTKColorChooserPanel.this.setHue((float)(1.0D - d2 / 3.141592653589793D / 2.0D), true);
/* 1035 */         setFlag(8, false);
/* 1036 */         setHueAngle(d2);
/* 1037 */         setSaturationAndBrightness(GTKColorChooserPanel.this.getSaturation(), GTKColorChooserPanel.this.getBrightness());
/* 1038 */         return true;
/*      */       }
/* 1040 */       return false;
/*      */     }
/*      */ 
/*      */     private void setAngleFromHue(float paramFloat)
/*      */     {
/* 1047 */       setHueAngle((1.0D - paramFloat) * 3.141592653589793D * 2.0D);
/*      */     }
/*      */ 
/*      */     private void setHueAngle(double paramDouble)
/*      */     {
/* 1054 */       double d = this.angle;
/*      */ 
/* 1056 */       this.angle = paramDouble;
/* 1057 */       if (paramDouble != d) {
/* 1058 */         setFlag(1, true);
/* 1059 */         repaint();
/*      */       }
/*      */     }
/*      */ 
/*      */     private int getIndicatorSize()
/*      */     {
/* 1067 */       return 8;
/*      */     }
/*      */ 
/*      */     private int getTriangleCircumscribedRadius()
/*      */     {
/* 1074 */       return 72;
/*      */     }
/*      */ 
/*      */     private int getWheelXOrigin()
/*      */     {
/* 1081 */       return 85;
/*      */     }
/*      */ 
/*      */     private int getWheelYOrigin()
/*      */     {
/* 1088 */       return 85;
/*      */     }
/*      */ 
/*      */     private int getWheelWidth()
/*      */     {
/* 1095 */       return 13;
/*      */     }
/*      */ 
/*      */     private void setFocusType(int paramInt)
/*      */     {
/* 1102 */       if (paramInt == 0) {
/* 1103 */         setFlag(16, false);
/* 1104 */         setFlag(32, false);
/* 1105 */         repaint();
/*      */       }
/*      */       else {
/* 1108 */         int i = 16;
/* 1109 */         int j = 32;
/*      */ 
/* 1111 */         if (paramInt == 2) {
/* 1112 */           i = 32;
/* 1113 */           j = 16;
/*      */         }
/* 1115 */         if (!isSet(i)) {
/* 1116 */           setFlag(i, true);
/* 1117 */           repaint();
/* 1118 */           setFlag(j, false);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     private int getWheelRadius()
/*      */     {
/* 1129 */       return 85;
/*      */     }
/*      */ 
/*      */     private void setFlag(int paramInt, boolean paramBoolean)
/*      */     {
/* 1136 */       if (paramBoolean) {
/* 1137 */         this.flags |= paramInt;
/*      */       }
/*      */       else
/* 1140 */         this.flags &= (paramInt ^ 0xFFFFFFFF);
/*      */     }
/*      */ 
/*      */     private boolean isSet(int paramInt)
/*      */     {
/* 1148 */       return (this.flags & paramInt) == paramInt;
/*      */     }
/*      */ 
/*      */     private int colorWheelLocationToRGB(int paramInt1, int paramInt2, double paramDouble)
/*      */     {
/* 1162 */       double d = Math.acos(paramInt1 / paramDouble);
/*      */       int i;
/* 1165 */       if (d < 1.047197580337524D) {
/* 1166 */         if (paramInt2 < 0)
/*      */         {
/* 1168 */           i = 0xFF0000 | Math.min(255, (int)(255.0D * d / 1.047197580337524D)) << 8;
/*      */         }
/*      */         else
/*      */         {
/* 1173 */           i = 0xFF0000 | Math.min(255, (int)(255.0D * d / 1.047197580337524D));
/*      */         }
/*      */ 
/*      */       }
/* 1177 */       else if (d < 2.094395160675049D) {
/* 1178 */         d -= 1.047197580337524D;
/* 1179 */         if (paramInt2 < 0)
/*      */         {
/* 1181 */           i = 0xFF00 | Math.max(0, 255 - (int)(255.0D * d / 1.047197580337524D)) << 16;
/*      */         }
/*      */         else
/*      */         {
/* 1186 */           i = 0xFF | Math.max(0, 255 - (int)(255.0D * d / 1.047197580337524D)) << 16;
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 1191 */         d -= 2.094395160675049D;
/* 1192 */         if (paramInt2 < 0)
/*      */         {
/* 1194 */           i = 0xFF00 | Math.min(255, (int)(255.0D * d / 1.047197580337524D));
/*      */         }
/*      */         else
/*      */         {
/* 1199 */           i = 0xFF | Math.min(255, (int)(255.0D * d / 1.047197580337524D)) << 8;
/*      */         }
/*      */       }
/*      */ 
/* 1203 */       return i;
/*      */     }
/*      */ 
/*      */     void incrementHue(boolean paramBoolean)
/*      */     {
/* 1210 */       float f = GTKColorChooserPanel.this.triangle.getGTKColorChooserPanel().getHue();
/*      */ 
/* 1212 */       if (paramBoolean) {
/* 1213 */         f += 0.002777778F;
/*      */       }
/*      */       else {
/* 1216 */         f -= 0.002777778F;
/*      */       }
/* 1218 */       if (f > 1.0F) {
/* 1219 */         f -= 1.0F;
/*      */       }
/* 1221 */       else if (f < 0.0F) {
/* 1222 */         f += 1.0F;
/*      */       }
/* 1224 */       getGTKColorChooserPanel().setHue(f, true);
/*      */     }
/*      */   }
/*      */ 
/*      */   private class OpaqueLabel extends JLabel
/*      */   {
/*      */     private OpaqueLabel()
/*      */     {
/*      */     }
/*      */ 
/*      */     public boolean isOpaque()
/*      */     {
/* 1300 */       return true;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.java.swing.plaf.gtk.GTKColorChooserPanel
 * JD-Core Version:    0.6.2
 */