/*     */ package sun.swing;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Image;
/*     */ import java.awt.Insets;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.beans.PropertyChangeEvent;
/*     */ import java.beans.PropertyChangeListener;
/*     */ import java.io.File;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.ButtonGroup;
/*     */ import javax.swing.Icon;
/*     */ import javax.swing.ImageIcon;
/*     */ import javax.swing.JFileChooser;
/*     */ import javax.swing.JToggleButton;
/*     */ import javax.swing.JToolBar;
/*     */ import javax.swing.UIManager;
/*     */ import javax.swing.border.BevelBorder;
/*     */ import javax.swing.border.EmptyBorder;
/*     */ import javax.swing.filechooser.FileSystemView;
/*     */ import sun.awt.OSInfo;
/*     */ import sun.awt.OSInfo.OSType;
/*     */ import sun.awt.OSInfo.WindowsVersion;
/*     */ import sun.awt.shell.ShellFolder;
/*     */ 
/*     */ public class WindowsPlacesBar extends JToolBar
/*     */   implements ActionListener, PropertyChangeListener
/*     */ {
/*     */   JFileChooser fc;
/*     */   JToggleButton[] buttons;
/*     */   ButtonGroup buttonGroup;
/*     */   File[] files;
/*     */   final Dimension buttonSize;
/*     */ 
/*     */   public WindowsPlacesBar(JFileChooser paramJFileChooser, boolean paramBoolean)
/*     */   {
/*  59 */     super(1);
/*  60 */     this.fc = paramJFileChooser;
/*  61 */     setFloatable(false);
/*  62 */     putClientProperty("JToolBar.isRollover", Boolean.TRUE);
/*     */ 
/*  64 */     int i = (OSInfo.getOSType() == OSInfo.OSType.WINDOWS) && (OSInfo.getWindowsVersion().compareTo(OSInfo.WINDOWS_XP) >= 0) ? 1 : 0;
/*     */ 
/*  67 */     if (paramBoolean) {
/*  68 */       this.buttonSize = new Dimension(83, 69);
/*  69 */       putClientProperty("XPStyle.subAppName", "placesbar");
/*  70 */       setBorder(new EmptyBorder(1, 1, 1, 1));
/*     */     }
/*     */     else {
/*  73 */       this.buttonSize = new Dimension(83, i != 0 ? 65 : 54);
/*  74 */       setBorder(new BevelBorder(1, UIManager.getColor("ToolBar.highlight"), UIManager.getColor("ToolBar.background"), UIManager.getColor("ToolBar.darkShadow"), UIManager.getColor("ToolBar.shadow")));
/*     */     }
/*     */ 
/*  80 */     Color localColor = new Color(UIManager.getColor("ToolBar.shadow").getRGB());
/*  81 */     setBackground(localColor);
/*  82 */     FileSystemView localFileSystemView = paramJFileChooser.getFileSystemView();
/*     */ 
/*  84 */     this.files = ((File[])AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public File[] run() {
/*  86 */         return (File[])ShellFolder.get("fileChooserShortcutPanelFolders");
/*     */       }
/*     */     }));
/*  90 */     this.buttons = new JToggleButton[this.files.length];
/*  91 */     this.buttonGroup = new ButtonGroup();
/*  92 */     for (int j = 0; j < this.files.length; j++) {
/*  93 */       if (localFileSystemView.isFileSystemRoot(this.files[j]))
/*     */       {
/*  95 */         this.files[j] = localFileSystemView.createFileObject(this.files[j].getAbsolutePath());
/*     */       }
/*     */ 
/*  98 */       String str = localFileSystemView.getSystemDisplayName(this.files[j]);
/*  99 */       int k = str.lastIndexOf(File.separatorChar);
/* 100 */       if ((k >= 0) && (k < str.length() - 1))
/* 101 */         str = str.substring(k + 1);
/*     */       Object localObject2;
/*     */       Object localObject1;
/* 104 */       if ((this.files[j] instanceof ShellFolder))
/*     */       {
/* 106 */         localObject2 = (ShellFolder)this.files[j];
/* 107 */         Image localImage = ((ShellFolder)localObject2).getIcon(true);
/*     */ 
/* 109 */         if (localImage == null)
/*     */         {
/* 111 */           localImage = (Image)ShellFolder.get("shell32LargeIcon 1");
/*     */         }
/*     */ 
/* 114 */         localObject1 = localImage == null ? null : new ImageIcon(localImage, ((ShellFolder)localObject2).getFolderType());
/*     */       } else {
/* 116 */         localObject1 = localFileSystemView.getSystemIcon(this.files[j]);
/*     */       }
/* 118 */       this.buttons[j] = new JToggleButton(str, (Icon)localObject1);
/* 119 */       if (i != 0) {
/* 120 */         this.buttons[j].setText("<html><center>" + str + "</center></html>");
/*     */       }
/* 122 */       if (paramBoolean) {
/* 123 */         this.buttons[j].putClientProperty("XPStyle.subAppName", "placesbar");
/*     */       } else {
/* 125 */         localObject2 = new Color(UIManager.getColor("List.selectionForeground").getRGB());
/* 126 */         this.buttons[j].setContentAreaFilled(false);
/* 127 */         this.buttons[j].setForeground((Color)localObject2);
/*     */       }
/* 129 */       this.buttons[j].setMargin(new Insets(3, 2, 1, 2));
/* 130 */       this.buttons[j].setFocusPainted(false);
/* 131 */       this.buttons[j].setIconTextGap(0);
/* 132 */       this.buttons[j].setHorizontalTextPosition(0);
/* 133 */       this.buttons[j].setVerticalTextPosition(3);
/* 134 */       this.buttons[j].setAlignmentX(0.5F);
/* 135 */       this.buttons[j].setPreferredSize(this.buttonSize);
/* 136 */       this.buttons[j].setMaximumSize(this.buttonSize);
/* 137 */       this.buttons[j].addActionListener(this);
/* 138 */       add(this.buttons[j]);
/* 139 */       if ((j < this.files.length - 1) && (paramBoolean)) {
/* 140 */         add(Box.createRigidArea(new Dimension(1, 1)));
/*     */       }
/* 142 */       this.buttonGroup.add(this.buttons[j]);
/*     */     }
/* 144 */     doDirectoryChanged(paramJFileChooser.getCurrentDirectory());
/*     */   }
/*     */ 
/*     */   protected void doDirectoryChanged(File paramFile) {
/* 148 */     for (int i = 0; i < this.buttons.length; i++) {
/* 149 */       JToggleButton localJToggleButton = this.buttons[i];
/* 150 */       if (this.files[i].equals(paramFile)) {
/* 151 */         localJToggleButton.setSelected(true);
/* 152 */         break;
/* 153 */       }if (localJToggleButton.isSelected())
/*     */       {
/* 156 */         this.buttonGroup.remove(localJToggleButton);
/* 157 */         localJToggleButton.setSelected(false);
/* 158 */         this.buttonGroup.add(localJToggleButton);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
/* 164 */     String str = paramPropertyChangeEvent.getPropertyName();
/* 165 */     if (str == "directoryChanged")
/* 166 */       doDirectoryChanged(this.fc.getCurrentDirectory());
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent paramActionEvent)
/*     */   {
/* 171 */     JToggleButton localJToggleButton = (JToggleButton)paramActionEvent.getSource();
/* 172 */     for (int i = 0; i < this.buttons.length; i++)
/* 173 */       if (localJToggleButton == this.buttons[i]) {
/* 174 */         this.fc.setCurrentDirectory(this.files[i]);
/* 175 */         break;
/*     */       }
/*     */   }
/*     */ 
/*     */   public Dimension getPreferredSize()
/*     */   {
/* 181 */     Dimension localDimension1 = super.getMinimumSize();
/* 182 */     Dimension localDimension2 = super.getPreferredSize();
/* 183 */     int i = localDimension1.height;
/* 184 */     if ((this.buttons != null) && (this.buttons.length > 0) && (this.buttons.length < 5)) {
/* 185 */       JToggleButton localJToggleButton = this.buttons[0];
/* 186 */       if (localJToggleButton != null) {
/* 187 */         int j = 5 * (localJToggleButton.getPreferredSize().height + 1);
/* 188 */         if (j > i) {
/* 189 */           i = j;
/*     */         }
/*     */       }
/*     */     }
/* 193 */     if (i > localDimension2.height) {
/* 194 */       localDimension2 = new Dimension(localDimension2.width, i);
/*     */     }
/* 196 */     return localDimension2;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.swing.WindowsPlacesBar
 * JD-Core Version:    0.6.2
 */