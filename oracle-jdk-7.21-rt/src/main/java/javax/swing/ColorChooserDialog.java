/*     */ package javax.swing;
/*     */ 
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dialog;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.Frame;
/*     */ import java.awt.HeadlessException;
/*     */ import java.awt.Window;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.ComponentAdapter;
/*     */ import java.awt.event.ComponentEvent;
/*     */ import java.awt.event.WindowAdapter;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.io.Serializable;
/*     */ import java.util.Locale;
/*     */ import sun.swing.SwingUtilities2;
/*     */ 
/*     */ class ColorChooserDialog extends JDialog
/*     */ {
/*     */   private Color initialColor;
/*     */   private JColorChooser chooserPane;
/*     */   private JButton cancelButton;
/*     */ 
/*     */   public ColorChooserDialog(Dialog paramDialog, String paramString, boolean paramBoolean, Component paramComponent, JColorChooser paramJColorChooser, ActionListener paramActionListener1, ActionListener paramActionListener2)
/*     */     throws HeadlessException
/*     */   {
/* 616 */     super(paramDialog, paramString, paramBoolean);
/* 617 */     initColorChooserDialog(paramComponent, paramJColorChooser, paramActionListener1, paramActionListener2);
/*     */   }
/*     */ 
/*     */   public ColorChooserDialog(Frame paramFrame, String paramString, boolean paramBoolean, Component paramComponent, JColorChooser paramJColorChooser, ActionListener paramActionListener1, ActionListener paramActionListener2)
/*     */     throws HeadlessException
/*     */   {
/* 624 */     super(paramFrame, paramString, paramBoolean);
/* 625 */     initColorChooserDialog(paramComponent, paramJColorChooser, paramActionListener1, paramActionListener2);
/*     */   }
/*     */ 
/*     */   protected void initColorChooserDialog(Component paramComponent, JColorChooser paramJColorChooser, ActionListener paramActionListener1, ActionListener paramActionListener2)
/*     */   {
/* 632 */     this.chooserPane = paramJColorChooser;
/*     */ 
/* 634 */     Locale localLocale = getLocale();
/* 635 */     String str1 = UIManager.getString("ColorChooser.okText", localLocale);
/* 636 */     String str2 = UIManager.getString("ColorChooser.cancelText", localLocale);
/* 637 */     String str3 = UIManager.getString("ColorChooser.resetText", localLocale);
/*     */ 
/* 639 */     Container localContainer = getContentPane();
/* 640 */     localContainer.setLayout(new BorderLayout());
/* 641 */     localContainer.add(paramJColorChooser, "Center");
/*     */ 
/* 646 */     JPanel localJPanel = new JPanel();
/* 647 */     localJPanel.setLayout(new FlowLayout(1));
/* 648 */     JButton localJButton1 = new JButton(str1);
/* 649 */     getRootPane().setDefaultButton(localJButton1);
/* 650 */     localJButton1.setActionCommand("OK");
/* 651 */     localJButton1.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent paramAnonymousActionEvent) {
/* 653 */         ColorChooserDialog.this.hide();
/*     */       }
/*     */     });
/* 656 */     if (paramActionListener1 != null) {
/* 657 */       localJButton1.addActionListener(paramActionListener1);
/*     */     }
/* 659 */     localJPanel.add(localJButton1);
/*     */ 
/* 661 */     this.cancelButton = new JButton(str2);
/*     */ 
/* 664 */     AbstractAction local2 = new AbstractAction() {
/*     */       public void actionPerformed(ActionEvent paramAnonymousActionEvent) {
/* 666 */         ((AbstractButton)paramAnonymousActionEvent.getSource()).fireActionPerformed(paramAnonymousActionEvent);
/*     */       }
/*     */     };
/* 669 */     KeyStroke localKeyStroke = KeyStroke.getKeyStroke(27, 0);
/* 670 */     InputMap localInputMap = this.cancelButton.getInputMap(2);
/*     */ 
/* 672 */     ActionMap localActionMap = this.cancelButton.getActionMap();
/* 673 */     if ((localInputMap != null) && (localActionMap != null)) {
/* 674 */       localInputMap.put(localKeyStroke, "cancel");
/* 675 */       localActionMap.put("cancel", local2);
/*     */     }
/*     */ 
/* 679 */     this.cancelButton.setActionCommand("cancel");
/* 680 */     this.cancelButton.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent paramAnonymousActionEvent) {
/* 682 */         ColorChooserDialog.this.hide();
/*     */       }
/*     */     });
/* 685 */     if (paramActionListener2 != null) {
/* 686 */       this.cancelButton.addActionListener(paramActionListener2);
/*     */     }
/* 688 */     localJPanel.add(this.cancelButton);
/*     */ 
/* 690 */     JButton localJButton2 = new JButton(str3);
/* 691 */     localJButton2.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent paramAnonymousActionEvent) {
/* 693 */         ColorChooserDialog.this.reset();
/*     */       }
/*     */     });
/* 696 */     int i = SwingUtilities2.getUIDefaultsInt("ColorChooser.resetMnemonic", localLocale, -1);
/* 697 */     if (i != -1) {
/* 698 */       localJButton2.setMnemonic(i);
/*     */     }
/* 700 */     localJPanel.add(localJButton2);
/* 701 */     localContainer.add(localJPanel, "South");
/*     */ 
/* 703 */     if (JDialog.isDefaultLookAndFeelDecorated()) {
/* 704 */       boolean bool = UIManager.getLookAndFeel().getSupportsWindowDecorations();
/*     */ 
/* 706 */       if (bool) {
/* 707 */         getRootPane().setWindowDecorationStyle(5);
/*     */       }
/*     */     }
/* 710 */     applyComponentOrientation((paramComponent == null ? getRootPane() : paramComponent).getComponentOrientation());
/*     */ 
/* 712 */     pack();
/* 713 */     setLocationRelativeTo(paramComponent);
/*     */ 
/* 715 */     addWindowListener(new Closer());
/*     */   }
/*     */ 
/*     */   public void show() {
/* 719 */     this.initialColor = this.chooserPane.getColor();
/* 720 */     super.show();
/*     */   }
/*     */ 
/*     */   public void reset() {
/* 724 */     this.chooserPane.setColor(this.initialColor);
/*     */   }
/*     */   class Closer extends WindowAdapter implements Serializable {
/*     */     Closer() {
/*     */     }
/* 729 */     public void windowClosing(WindowEvent paramWindowEvent) { ColorChooserDialog.this.cancelButton.doClick(0);
/* 730 */       Window localWindow = paramWindowEvent.getWindow();
/* 731 */       localWindow.hide(); }
/*     */   }
/*     */ 
/*     */   static class DisposeOnClose extends ComponentAdapter implements Serializable
/*     */   {
/*     */     public void componentHidden(ComponentEvent paramComponentEvent) {
/* 737 */       Window localWindow = (Window)paramComponentEvent.getComponent();
/* 738 */       localWindow.dispose();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.ColorChooserDialog
 * JD-Core Version:    0.6.2
 */