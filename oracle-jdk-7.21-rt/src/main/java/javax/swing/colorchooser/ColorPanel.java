/*     */ package javax.swing.colorchooser;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.ContainerOrderFocusTraversalPolicy;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.Insets;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import javax.swing.AbstractButton;
/*     */ import javax.swing.ButtonGroup;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JRadioButton;
/*     */ import javax.swing.border.EmptyBorder;
/*     */ 
/*     */ final class ColorPanel extends JPanel
/*     */   implements ActionListener
/*     */ {
/*  43 */   private final SlidingSpinner[] spinners = new SlidingSpinner[5];
/*  44 */   private final float[] values = new float[this.spinners.length];
/*     */   private final ColorModel model;
/*     */   private Color color;
/*  48 */   private int x = 1;
/*  49 */   private int y = 2;
/*     */   private int z;
/*     */ 
/*     */   ColorPanel(ColorModel paramColorModel)
/*     */   {
/*  53 */     super(new GridBagLayout());
/*     */ 
/*  55 */     GridBagConstraints localGridBagConstraints = new GridBagConstraints();
/*  56 */     localGridBagConstraints.fill = 2;
/*     */ 
/*  58 */     localGridBagConstraints.gridx = 1;
/*  59 */     ButtonGroup localButtonGroup = new ButtonGroup();
/*  60 */     EmptyBorder localEmptyBorder = null;
/*  61 */     for (int i = 0; i < this.spinners.length; i++)
/*     */     {
/*     */       Object localObject;
/*  62 */       if (i < 3) {
/*  63 */         localObject = new JRadioButton();
/*  64 */         if (i == 0) {
/*  65 */           Insets localInsets = ((JRadioButton)localObject).getInsets();
/*  66 */           localInsets.left = ((JRadioButton)localObject).getPreferredSize().width;
/*  67 */           localEmptyBorder = new EmptyBorder(localInsets);
/*  68 */           ((JRadioButton)localObject).setSelected(true);
/*  69 */           localGridBagConstraints.insets.top = 5;
/*     */         }
/*  71 */         add((Component)localObject, localGridBagConstraints);
/*  72 */         localButtonGroup.add((AbstractButton)localObject);
/*  73 */         ((JRadioButton)localObject).setActionCommand(Integer.toString(i));
/*  74 */         ((JRadioButton)localObject).addActionListener(this);
/*  75 */         this.spinners[i] = new SlidingSpinner(this, (JComponent)localObject);
/*     */       }
/*     */       else {
/*  78 */         localObject = new JLabel();
/*  79 */         add((Component)localObject, localGridBagConstraints);
/*  80 */         ((JLabel)localObject).setBorder(localEmptyBorder);
/*  81 */         ((JLabel)localObject).setFocusable(false);
/*  82 */         this.spinners[i] = new SlidingSpinner(this, (JComponent)localObject);
/*     */       }
/*     */     }
/*  85 */     localGridBagConstraints.gridx = 2;
/*  86 */     localGridBagConstraints.weightx = 1.0D;
/*  87 */     localGridBagConstraints.insets.top = 0;
/*  88 */     localGridBagConstraints.insets.left = 5;
/*     */     SlidingSpinner localSlidingSpinner;
/*  89 */     for (localSlidingSpinner : this.spinners) {
/*  90 */       add(localSlidingSpinner.getSlider(), localGridBagConstraints);
/*  91 */       localGridBagConstraints.insets.top = 5;
/*     */     }
/*  93 */     localGridBagConstraints.gridx = 3;
/*  94 */     localGridBagConstraints.weightx = 0.0D;
/*  95 */     localGridBagConstraints.insets.top = 0;
/*  96 */     for (localSlidingSpinner : this.spinners) {
/*  97 */       add(localSlidingSpinner.getSpinner(), localGridBagConstraints);
/*  98 */       localGridBagConstraints.insets.top = 5;
/*     */     }
/* 100 */     setFocusTraversalPolicy(new ContainerOrderFocusTraversalPolicy());
/* 101 */     setFocusTraversalPolicyProvider(true);
/* 102 */     setFocusable(false);
/*     */ 
/* 104 */     this.model = paramColorModel;
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent paramActionEvent) {
/*     */     try {
/* 109 */       this.z = Integer.parseInt(paramActionEvent.getActionCommand());
/* 110 */       this.y = (this.z != 2 ? 2 : 1);
/* 111 */       this.x = (this.z != 0 ? 0 : 1);
/* 112 */       getParent().repaint();
/*     */     }
/*     */     catch (NumberFormatException localNumberFormatException) {
/*     */     }
/*     */   }
/*     */ 
/*     */   void buildPanel() {
/* 119 */     int i = this.model.getCount();
/* 120 */     this.spinners[4].setVisible(i > 4);
/* 121 */     for (int j = 0; j < i; j++) {
/* 122 */       JComponent localJComponent = this.spinners[j].getLabel();
/*     */       Object localObject;
/* 123 */       if ((localJComponent instanceof JRadioButton)) {
/* 124 */         localObject = (JRadioButton)localJComponent;
/* 125 */         ((JRadioButton)localObject).setText(this.model.getLabel(this, j));
/*     */       }
/* 127 */       else if ((localJComponent instanceof JLabel)) {
/* 128 */         localObject = (JLabel)localJComponent;
/* 129 */         ((JLabel)localObject).setText(this.model.getLabel(this, j));
/*     */       }
/* 131 */       this.spinners[j].setRange(this.model.getMinimum(j), this.model.getMaximum(j));
/* 132 */       this.spinners[j].setValue(this.values[j]);
/*     */     }
/*     */   }
/*     */ 
/*     */   void colorChanged() {
/* 137 */     this.color = new Color(getColor(0), true);
/* 138 */     Container localContainer = getParent();
/* 139 */     if ((localContainer instanceof ColorChooserPanel)) {
/* 140 */       ColorChooserPanel localColorChooserPanel = (ColorChooserPanel)localContainer;
/* 141 */       localColorChooserPanel.setSelectedColor(this.color);
/* 142 */       localColorChooserPanel.repaint();
/*     */     }
/*     */   }
/*     */ 
/*     */   float getValueX() {
/* 147 */     return this.spinners[this.x].getValue();
/*     */   }
/*     */ 
/*     */   float getValueY() {
/* 151 */     return 1.0F - this.spinners[this.y].getValue();
/*     */   }
/*     */ 
/*     */   float getValueZ() {
/* 155 */     return 1.0F - this.spinners[this.z].getValue();
/*     */   }
/*     */ 
/*     */   void setValue(float paramFloat) {
/* 159 */     this.spinners[this.z].setValue(1.0F - paramFloat);
/* 160 */     colorChanged();
/*     */   }
/*     */ 
/*     */   void setValue(float paramFloat1, float paramFloat2) {
/* 164 */     this.spinners[this.x].setValue(paramFloat1);
/* 165 */     this.spinners[this.y].setValue(1.0F - paramFloat2);
/* 166 */     colorChanged();
/*     */   }
/*     */ 
/*     */   int getColor(float paramFloat) {
/* 170 */     setDefaultValue(this.x);
/* 171 */     setDefaultValue(this.y);
/* 172 */     this.values[this.z] = (1.0F - paramFloat);
/* 173 */     return getColor(3);
/*     */   }
/*     */ 
/*     */   int getColor(float paramFloat1, float paramFloat2) {
/* 177 */     this.values[this.x] = paramFloat1;
/* 178 */     this.values[this.y] = (1.0F - paramFloat2);
/* 179 */     setValue(this.z);
/* 180 */     return getColor(3);
/*     */   }
/*     */ 
/*     */   void setColor(Color paramColor) {
/* 184 */     if (!paramColor.equals(this.color)) {
/* 185 */       this.color = paramColor;
/* 186 */       this.model.setColor(paramColor.getRGB(), this.values);
/* 187 */       for (int i = 0; i < this.model.getCount(); i++)
/* 188 */         this.spinners[i].setValue(this.values[i]);
/*     */     }
/*     */   }
/*     */ 
/*     */   private int getColor(int paramInt)
/*     */   {
/* 194 */     while (paramInt < this.model.getCount()) {
/* 195 */       setValue(paramInt++);
/*     */     }
/* 197 */     return this.model.getColor(this.values);
/*     */   }
/*     */ 
/*     */   private void setValue(int paramInt) {
/* 201 */     this.values[paramInt] = this.spinners[paramInt].getValue();
/*     */   }
/*     */ 
/*     */   private void setDefaultValue(int paramInt) {
/* 205 */     float f = this.model.getDefault(paramInt);
/* 206 */     this.values[paramInt] = (f < 0.0F ? this.spinners[paramInt].getValue() : f);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.colorchooser.ColorPanel
 * JD-Core Version:    0.6.2
 */