/*      */ package sun.print;
/*      */ 
/*      */ import java.awt.BorderLayout;
/*      */ import java.awt.Component;
/*      */ import java.awt.Container;
/*      */ import java.awt.Dialog;
/*      */ import java.awt.FlowLayout;
/*      */ import java.awt.Frame;
/*      */ import java.awt.GraphicsConfiguration;
/*      */ import java.awt.GridBagConstraints;
/*      */ import java.awt.GridBagLayout;
/*      */ import java.awt.Insets;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ import java.awt.event.FocusEvent;
/*      */ import java.awt.event.FocusListener;
/*      */ import java.awt.event.ItemEvent;
/*      */ import java.awt.event.ItemListener;
/*      */ import java.awt.event.WindowAdapter;
/*      */ import java.awt.event.WindowEvent;
/*      */ import java.io.File;
/*      */ import java.io.FilePermission;
/*      */ import java.io.IOException;
/*      */ import java.lang.reflect.Field;
/*      */ import java.net.URI;
/*      */ import java.net.URISyntaxException;
/*      */ import java.net.URL;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.text.DecimalFormat;
/*      */ import java.util.Locale;
/*      */ import java.util.MissingResourceException;
/*      */ import java.util.ResourceBundle;
/*      */ import java.util.Vector;
/*      */ import javax.accessibility.AccessibleContext;
/*      */ import javax.print.DocFlavor;
/*      */ import javax.print.PrintService;
/*      */ import javax.print.ServiceUIFactory;
/*      */ import javax.print.attribute.Attribute;
/*      */ import javax.print.attribute.AttributeSet;
/*      */ import javax.print.attribute.HashPrintRequestAttributeSet;
/*      */ import javax.print.attribute.PrintRequestAttributeSet;
/*      */ import javax.print.attribute.PrintServiceAttribute;
/*      */ import javax.print.attribute.standard.Chromaticity;
/*      */ import javax.print.attribute.standard.Copies;
/*      */ import javax.print.attribute.standard.CopiesSupported;
/*      */ import javax.print.attribute.standard.Destination;
/*      */ import javax.print.attribute.standard.JobName;
/*      */ import javax.print.attribute.standard.JobPriority;
/*      */ import javax.print.attribute.standard.JobSheets;
/*      */ import javax.print.attribute.standard.Media;
/*      */ import javax.print.attribute.standard.MediaPrintableArea;
/*      */ import javax.print.attribute.standard.MediaSize;
/*      */ import javax.print.attribute.standard.MediaSizeName;
/*      */ import javax.print.attribute.standard.MediaTray;
/*      */ import javax.print.attribute.standard.OrientationRequested;
/*      */ import javax.print.attribute.standard.PageRanges;
/*      */ import javax.print.attribute.standard.PrintQuality;
/*      */ import javax.print.attribute.standard.PrinterInfo;
/*      */ import javax.print.attribute.standard.PrinterIsAcceptingJobs;
/*      */ import javax.print.attribute.standard.PrinterMakeAndModel;
/*      */ import javax.print.attribute.standard.RequestingUserName;
/*      */ import javax.print.attribute.standard.SheetCollate;
/*      */ import javax.print.attribute.standard.Sides;
/*      */ import javax.swing.AbstractAction;
/*      */ import javax.swing.AbstractButton;
/*      */ import javax.swing.ActionMap;
/*      */ import javax.swing.BorderFactory;
/*      */ import javax.swing.ButtonGroup;
/*      */ import javax.swing.Icon;
/*      */ import javax.swing.ImageIcon;
/*      */ import javax.swing.InputMap;
/*      */ import javax.swing.JButton;
/*      */ import javax.swing.JCheckBox;
/*      */ import javax.swing.JComboBox;
/*      */ import javax.swing.JDialog;
/*      */ import javax.swing.JFileChooser;
/*      */ import javax.swing.JFormattedTextField;
/*      */ import javax.swing.JLabel;
/*      */ import javax.swing.JOptionPane;
/*      */ import javax.swing.JPanel;
/*      */ import javax.swing.JRadioButton;
/*      */ import javax.swing.JRootPane;
/*      */ import javax.swing.JSpinner;
/*      */ import javax.swing.JSpinner.NumberEditor;
/*      */ import javax.swing.JTabbedPane;
/*      */ import javax.swing.JTextField;
/*      */ import javax.swing.KeyStroke;
/*      */ import javax.swing.SpinnerNumberModel;
/*      */ import javax.swing.border.EmptyBorder;
/*      */ import javax.swing.event.ChangeEvent;
/*      */ import javax.swing.event.ChangeListener;
/*      */ import javax.swing.event.PopupMenuEvent;
/*      */ import javax.swing.event.PopupMenuListener;
/*      */ import javax.swing.text.NumberFormatter;
/*      */ 
/*      */ public class ServiceDialog extends JDialog
/*      */   implements ActionListener
/*      */ {
/*      */   public static final int WAITING = 0;
/*      */   public static final int APPROVE = 1;
/*      */   public static final int CANCEL = 2;
/*      */   private static final String strBundle = "sun.print.resources.serviceui";
/*  101 */   private static final Insets panelInsets = new Insets(6, 6, 6, 6);
/*  102 */   private static final Insets compInsets = new Insets(3, 6, 3, 6);
/*      */   private static ResourceBundle messageRB;
/*      */   private JTabbedPane tpTabs;
/*      */   private JButton btnCancel;
/*      */   private JButton btnApprove;
/*      */   private PrintService[] services;
/*      */   private int defaultServiceIndex;
/*      */   private PrintRequestAttributeSet asOriginal;
/*      */   private HashPrintRequestAttributeSet asCurrent;
/*      */   private PrintService psCurrent;
/*      */   private DocFlavor docFlavor;
/*      */   private int status;
/*      */   private ValidatingFileChooser jfc;
/*      */   private GeneralPanel pnlGeneral;
/*      */   private PageSetupPanel pnlPageSetup;
/*      */   private AppearancePanel pnlAppearance;
/*  121 */   private boolean isAWT = false;
/*      */ 
/*  531 */   static Class _keyEventClazz = null;
/*      */ 
/*      */   public ServiceDialog(GraphicsConfiguration paramGraphicsConfiguration, int paramInt1, int paramInt2, PrintService[] paramArrayOfPrintService, int paramInt3, DocFlavor paramDocFlavor, PrintRequestAttributeSet paramPrintRequestAttributeSet, Dialog paramDialog)
/*      */   {
/*  141 */     super(paramDialog, getMsg("dialog.printtitle"), true, paramGraphicsConfiguration);
/*  142 */     initPrintDialog(paramInt1, paramInt2, paramArrayOfPrintService, paramInt3, paramDocFlavor, paramPrintRequestAttributeSet);
/*      */   }
/*      */ 
/*      */   public ServiceDialog(GraphicsConfiguration paramGraphicsConfiguration, int paramInt1, int paramInt2, PrintService[] paramArrayOfPrintService, int paramInt3, DocFlavor paramDocFlavor, PrintRequestAttributeSet paramPrintRequestAttributeSet, Frame paramFrame)
/*      */   {
/*  160 */     super(paramFrame, getMsg("dialog.printtitle"), true, paramGraphicsConfiguration);
/*  161 */     initPrintDialog(paramInt1, paramInt2, paramArrayOfPrintService, paramInt3, paramDocFlavor, paramPrintRequestAttributeSet);
/*      */   }
/*      */ 
/*      */   void initPrintDialog(int paramInt1, int paramInt2, PrintService[] paramArrayOfPrintService, int paramInt3, DocFlavor paramDocFlavor, PrintRequestAttributeSet paramPrintRequestAttributeSet)
/*      */   {
/*  175 */     this.services = paramArrayOfPrintService;
/*  176 */     this.defaultServiceIndex = paramInt3;
/*  177 */     this.asOriginal = paramPrintRequestAttributeSet;
/*  178 */     this.asCurrent = new HashPrintRequestAttributeSet(paramPrintRequestAttributeSet);
/*  179 */     this.psCurrent = paramArrayOfPrintService[paramInt3];
/*  180 */     this.docFlavor = paramDocFlavor;
/*  181 */     SunPageSelection localSunPageSelection = (SunPageSelection)paramPrintRequestAttributeSet.get(SunPageSelection.class);
/*      */ 
/*  183 */     if (localSunPageSelection != null) {
/*  184 */       this.isAWT = true;
/*      */     }
/*      */ 
/*  187 */     Container localContainer = getContentPane();
/*  188 */     localContainer.setLayout(new BorderLayout());
/*      */ 
/*  190 */     this.tpTabs = new JTabbedPane();
/*  191 */     this.tpTabs.setBorder(new EmptyBorder(5, 5, 5, 5));
/*      */ 
/*  193 */     String str1 = getMsg("tab.general");
/*  194 */     int i = getVKMnemonic("tab.general");
/*  195 */     this.pnlGeneral = new GeneralPanel();
/*  196 */     this.tpTabs.add(str1, this.pnlGeneral);
/*  197 */     this.tpTabs.setMnemonicAt(0, i);
/*      */ 
/*  199 */     String str2 = getMsg("tab.pagesetup");
/*  200 */     int j = getVKMnemonic("tab.pagesetup");
/*  201 */     this.pnlPageSetup = new PageSetupPanel();
/*  202 */     this.tpTabs.add(str2, this.pnlPageSetup);
/*  203 */     this.tpTabs.setMnemonicAt(1, j);
/*      */ 
/*  205 */     String str3 = getMsg("tab.appearance");
/*  206 */     int k = getVKMnemonic("tab.appearance");
/*  207 */     this.pnlAppearance = new AppearancePanel();
/*  208 */     this.tpTabs.add(str3, this.pnlAppearance);
/*  209 */     this.tpTabs.setMnemonicAt(2, k);
/*      */ 
/*  211 */     localContainer.add(this.tpTabs, "Center");
/*      */ 
/*  213 */     updatePanels();
/*      */ 
/*  215 */     JPanel localJPanel = new JPanel(new FlowLayout(4));
/*  216 */     this.btnApprove = createExitButton("button.print", this);
/*  217 */     localJPanel.add(this.btnApprove);
/*  218 */     getRootPane().setDefaultButton(this.btnApprove);
/*  219 */     this.btnCancel = createExitButton("button.cancel", this);
/*  220 */     handleEscKey(this.btnCancel);
/*  221 */     localJPanel.add(this.btnCancel);
/*  222 */     localContainer.add(localJPanel, "South");
/*      */ 
/*  224 */     addWindowListener(new WindowAdapter() {
/*      */       public void windowClosing(WindowEvent paramAnonymousWindowEvent) {
/*  226 */         ServiceDialog.this.dispose(2);
/*      */       }
/*      */     });
/*  230 */     getAccessibleContext().setAccessibleDescription(getMsg("dialog.printtitle"));
/*  231 */     setResizable(false);
/*  232 */     setLocation(paramInt1, paramInt2);
/*  233 */     pack();
/*      */   }
/*      */ 
/*      */   public ServiceDialog(GraphicsConfiguration paramGraphicsConfiguration, int paramInt1, int paramInt2, PrintService paramPrintService, DocFlavor paramDocFlavor, PrintRequestAttributeSet paramPrintRequestAttributeSet, Dialog paramDialog)
/*      */   {
/*  246 */     super(paramDialog, getMsg("dialog.pstitle"), true, paramGraphicsConfiguration);
/*  247 */     initPageDialog(paramInt1, paramInt2, paramPrintService, paramDocFlavor, paramPrintRequestAttributeSet);
/*      */   }
/*      */ 
/*      */   public ServiceDialog(GraphicsConfiguration paramGraphicsConfiguration, int paramInt1, int paramInt2, PrintService paramPrintService, DocFlavor paramDocFlavor, PrintRequestAttributeSet paramPrintRequestAttributeSet, Frame paramFrame)
/*      */   {
/*  260 */     super(paramFrame, getMsg("dialog.pstitle"), true, paramGraphicsConfiguration);
/*  261 */     initPageDialog(paramInt1, paramInt2, paramPrintService, paramDocFlavor, paramPrintRequestAttributeSet);
/*      */   }
/*      */ 
/*      */   void initPageDialog(int paramInt1, int paramInt2, PrintService paramPrintService, DocFlavor paramDocFlavor, PrintRequestAttributeSet paramPrintRequestAttributeSet)
/*      */   {
/*  273 */     this.psCurrent = paramPrintService;
/*  274 */     this.docFlavor = paramDocFlavor;
/*  275 */     this.asOriginal = paramPrintRequestAttributeSet;
/*  276 */     this.asCurrent = new HashPrintRequestAttributeSet(paramPrintRequestAttributeSet);
/*      */ 
/*  278 */     Container localContainer = getContentPane();
/*  279 */     localContainer.setLayout(new BorderLayout());
/*      */ 
/*  281 */     this.pnlPageSetup = new PageSetupPanel();
/*  282 */     localContainer.add(this.pnlPageSetup, "Center");
/*      */ 
/*  284 */     this.pnlPageSetup.updateInfo();
/*      */ 
/*  286 */     JPanel localJPanel = new JPanel(new FlowLayout(4));
/*  287 */     this.btnApprove = createExitButton("button.ok", this);
/*  288 */     localJPanel.add(this.btnApprove);
/*  289 */     getRootPane().setDefaultButton(this.btnApprove);
/*  290 */     this.btnCancel = createExitButton("button.cancel", this);
/*  291 */     handleEscKey(this.btnCancel);
/*  292 */     localJPanel.add(this.btnCancel);
/*  293 */     localContainer.add(localJPanel, "South");
/*      */ 
/*  295 */     addWindowListener(new WindowAdapter() {
/*      */       public void windowClosing(WindowEvent paramAnonymousWindowEvent) {
/*  297 */         ServiceDialog.this.dispose(2);
/*      */       }
/*      */     });
/*  301 */     getAccessibleContext().setAccessibleDescription(getMsg("dialog.pstitle"));
/*  302 */     setResizable(false);
/*  303 */     setLocation(paramInt1, paramInt2);
/*  304 */     pack();
/*      */   }
/*      */ 
/*      */   private void handleEscKey(JButton paramJButton)
/*      */   {
/*  311 */     AbstractAction local3 = new AbstractAction() {
/*      */       public void actionPerformed(ActionEvent paramAnonymousActionEvent) {
/*  313 */         ServiceDialog.this.dispose(2);
/*      */       }
/*      */     };
/*  316 */     KeyStroke localKeyStroke = KeyStroke.getKeyStroke(27, 0);
/*      */ 
/*  318 */     InputMap localInputMap = paramJButton.getInputMap(2);
/*      */ 
/*  320 */     ActionMap localActionMap = paramJButton.getActionMap();
/*      */ 
/*  322 */     if ((localInputMap != null) && (localActionMap != null)) {
/*  323 */       localInputMap.put(localKeyStroke, "cancel");
/*  324 */       localActionMap.put("cancel", local3);
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getStatus()
/*      */   {
/*  334 */     return this.status;
/*      */   }
/*      */ 
/*      */   public PrintRequestAttributeSet getAttributes()
/*      */   {
/*  343 */     if (this.status == 1) {
/*  344 */       return this.asCurrent;
/*      */     }
/*  346 */     return this.asOriginal;
/*      */   }
/*      */ 
/*      */   public PrintService getPrintService()
/*      */   {
/*  356 */     if (this.status == 1) {
/*  357 */       return this.psCurrent;
/*      */     }
/*  359 */     return null;
/*      */   }
/*      */ 
/*      */   public void dispose(int paramInt)
/*      */   {
/*  368 */     this.status = paramInt;
/*      */ 
/*  370 */     super.dispose();
/*      */   }
/*      */ 
/*      */   public void actionPerformed(ActionEvent paramActionEvent) {
/*  374 */     Object localObject = paramActionEvent.getSource();
/*  375 */     boolean bool = false;
/*      */ 
/*  377 */     if (localObject == this.btnApprove) {
/*  378 */       bool = true;
/*      */ 
/*  380 */       if (this.pnlGeneral != null) {
/*  381 */         if (this.pnlGeneral.isPrintToFileRequested())
/*  382 */           bool = showFileChooser();
/*      */         else {
/*  384 */           this.asCurrent.remove(Destination.class);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  389 */     dispose(bool ? 1 : 2);
/*      */   }
/*      */ 
/*      */   private boolean showFileChooser()
/*      */   {
/*  397 */     Destination localDestination1 = Destination.class;
/*      */ 
/*  399 */     Destination localDestination2 = (Destination)this.asCurrent.get(localDestination1);
/*  400 */     if (localDestination2 == null) {
/*  401 */       localDestination2 = (Destination)this.asOriginal.get(localDestination1);
/*  402 */       if (localDestination2 == null) {
/*  403 */         localDestination2 = (Destination)this.psCurrent.getDefaultAttributeValue(localDestination1);
/*      */ 
/*  408 */         if (localDestination2 == null)
/*      */           try {
/*  410 */             localDestination2 = new Destination(new URI("file:out.prn"));
/*      */           }
/*      */           catch (URISyntaxException localURISyntaxException)
/*      */           {
/*      */           }
/*      */       }
/*      */     }
/*      */     File localFile;
/*  418 */     if (localDestination2 != null)
/*      */       try {
/*  420 */         localFile = new File(localDestination2.getURI());
/*      */       }
/*      */       catch (Exception localException1) {
/*  423 */         localFile = new File("out.prn");
/*      */       }
/*      */     else {
/*  426 */       localFile = new File("out.prn");
/*      */     }
/*      */ 
/*  429 */     ValidatingFileChooser localValidatingFileChooser = new ValidatingFileChooser(null);
/*  430 */     localValidatingFileChooser.setApproveButtonText(getMsg("button.ok"));
/*  431 */     localValidatingFileChooser.setDialogTitle(getMsg("dialog.printtofile"));
/*  432 */     localValidatingFileChooser.setDialogType(1);
/*  433 */     localValidatingFileChooser.setSelectedFile(localFile);
/*      */ 
/*  435 */     int i = localValidatingFileChooser.showDialog(this, null);
/*  436 */     if (i == 0) {
/*  437 */       localFile = localValidatingFileChooser.getSelectedFile();
/*      */       try
/*      */       {
/*  440 */         this.asCurrent.add(new Destination(localFile.toURI()));
/*      */       } catch (Exception localException2) {
/*  442 */         this.asCurrent.remove(localDestination1);
/*      */       }
/*      */     } else {
/*  445 */       this.asCurrent.remove(localDestination1);
/*      */     }
/*      */ 
/*  448 */     return i == 0;
/*      */   }
/*      */ 
/*      */   private void updatePanels()
/*      */   {
/*  455 */     this.pnlGeneral.updateInfo();
/*  456 */     this.pnlPageSetup.updateInfo();
/*  457 */     this.pnlAppearance.updateInfo();
/*      */   }
/*      */ 
/*      */   public static void initResource()
/*      */   {
/*  464 */     AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public Object run() {
/*      */         try {
/*  468 */           ServiceDialog.access$102(ResourceBundle.getBundle("sun.print.resources.serviceui"));
/*  469 */           return null; } catch (MissingResourceException localMissingResourceException) {
/*      */         }
/*  471 */         throw new Error("Fatal: Resource for ServiceUI is missing");
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public static String getMsg(String paramString)
/*      */   {
/*      */     try
/*      */     {
/*  484 */       return removeMnemonics(messageRB.getString(paramString)); } catch (MissingResourceException localMissingResourceException) {
/*      */     }
/*  486 */     throw new Error("Fatal: Resource for ServiceUI is broken; there is no " + paramString + " key in resource");
/*      */   }
/*      */ 
/*      */   private static String removeMnemonics(String paramString)
/*      */   {
/*  492 */     int i = paramString.indexOf('&');
/*  493 */     int j = paramString.length();
/*  494 */     if ((i < 0) || (i == j - 1)) {
/*  495 */       return paramString;
/*      */     }
/*  497 */     int k = paramString.indexOf('&', i + 1);
/*  498 */     if (k == i + 1) {
/*  499 */       if (k + 1 == j) {
/*  500 */         return paramString.substring(0, i + 1);
/*      */       }
/*  502 */       return paramString.substring(0, i + 1) + removeMnemonics(paramString.substring(k + 1));
/*      */     }
/*      */ 
/*  506 */     if (i == 0) {
/*  507 */       return removeMnemonics(paramString.substring(1));
/*      */     }
/*  509 */     return paramString.substring(0, i) + removeMnemonics(paramString.substring(i + 1));
/*      */   }
/*      */ 
/*      */   private static char getMnemonic(String paramString)
/*      */   {
/*  518 */     String str = messageRB.getString(paramString).replace("&&", "");
/*  519 */     int i = str.indexOf('&');
/*  520 */     if ((0 <= i) && (i < str.length() - 1)) {
/*  521 */       char c = str.charAt(i + 1);
/*  522 */       return Character.toUpperCase(c);
/*      */     }
/*  524 */     return '\000';
/*      */   }
/*      */ 
/*      */   private static int getVKMnemonic(String paramString)
/*      */   {
/*  533 */     String str1 = String.valueOf(getMnemonic(paramString));
/*  534 */     if ((str1 == null) || (str1.length() != 1)) {
/*  535 */       return 0;
/*      */     }
/*  537 */     String str2 = "VK_" + str1.toUpperCase();
/*      */     try
/*      */     {
/*  540 */       if (_keyEventClazz == null) {
/*  541 */         _keyEventClazz = Class.forName("java.awt.event.KeyEvent", true, ServiceDialog.class.getClassLoader());
/*      */       }
/*      */ 
/*  544 */       Field localField = _keyEventClazz.getDeclaredField(str2);
/*  545 */       return localField.getInt(null);
/*      */     }
/*      */     catch (Exception localException) {
/*      */     }
/*  549 */     return 0;
/*      */   }
/*      */ 
/*      */   private static URL getImageResource(String paramString)
/*      */   {
/*  556 */     URL localURL = (URL)AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public Object run() {
/*  559 */         URL localURL = ServiceDialog.class.getResource("resources/" + this.val$key);
/*      */ 
/*  561 */         return localURL;
/*      */       }
/*      */     });
/*  565 */     if (localURL == null) {
/*  566 */       throw new Error("Fatal: Resource for ServiceUI is broken; there is no " + paramString + " key in resource");
/*      */     }
/*      */ 
/*  570 */     return localURL;
/*      */   }
/*      */ 
/*      */   private static JButton createButton(String paramString, ActionListener paramActionListener)
/*      */   {
/*  577 */     JButton localJButton = new JButton(getMsg(paramString));
/*  578 */     localJButton.setMnemonic(getMnemonic(paramString));
/*  579 */     localJButton.addActionListener(paramActionListener);
/*      */ 
/*  581 */     return localJButton;
/*      */   }
/*      */ 
/*      */   private static JButton createExitButton(String paramString, ActionListener paramActionListener)
/*      */   {
/*  588 */     String str = getMsg(paramString);
/*  589 */     JButton localJButton = new JButton(str);
/*  590 */     localJButton.addActionListener(paramActionListener);
/*  591 */     localJButton.getAccessibleContext().setAccessibleDescription(str);
/*  592 */     return localJButton;
/*      */   }
/*      */ 
/*      */   private static JCheckBox createCheckBox(String paramString, ActionListener paramActionListener)
/*      */   {
/*  599 */     JCheckBox localJCheckBox = new JCheckBox(getMsg(paramString));
/*  600 */     localJCheckBox.setMnemonic(getMnemonic(paramString));
/*  601 */     localJCheckBox.addActionListener(paramActionListener);
/*      */ 
/*  603 */     return localJCheckBox;
/*      */   }
/*      */ 
/*      */   private static JRadioButton createRadioButton(String paramString, ActionListener paramActionListener)
/*      */   {
/*  613 */     JRadioButton localJRadioButton = new JRadioButton(getMsg(paramString));
/*  614 */     localJRadioButton.setMnemonic(getMnemonic(paramString));
/*  615 */     localJRadioButton.addActionListener(paramActionListener);
/*      */ 
/*  617 */     return localJRadioButton;
/*      */   }
/*      */ 
/*      */   public static void showNoPrintService(GraphicsConfiguration paramGraphicsConfiguration)
/*      */   {
/*  625 */     Frame localFrame = new Frame(paramGraphicsConfiguration);
/*  626 */     JOptionPane.showMessageDialog(localFrame, getMsg("dialog.noprintermsg"));
/*      */ 
/*  628 */     localFrame.dispose();
/*      */   }
/*      */ 
/*      */   private static void addToGB(Component paramComponent, Container paramContainer, GridBagLayout paramGridBagLayout, GridBagConstraints paramGridBagConstraints)
/*      */   {
/*  639 */     paramGridBagLayout.setConstraints(paramComponent, paramGridBagConstraints);
/*  640 */     paramContainer.add(paramComponent);
/*      */   }
/*      */ 
/*      */   private static void addToBG(AbstractButton paramAbstractButton, Container paramContainer, ButtonGroup paramButtonGroup)
/*      */   {
/*  649 */     paramButtonGroup.add(paramAbstractButton);
/*  650 */     paramContainer.add(paramAbstractButton);
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  125 */     initResource();
/*      */   }
/*      */ 
/*      */   private class AppearancePanel extends JPanel
/*      */   {
/*      */     private ServiceDialog.ChromaticityPanel pnlChromaticity;
/*      */     private ServiceDialog.QualityPanel pnlQuality;
/*      */     private ServiceDialog.JobAttributesPanel pnlJobAttributes;
/*      */     private ServiceDialog.SidesPanel pnlSides;
/*      */ 
/*      */     public AppearancePanel()
/*      */     {
/* 2255 */       GridBagLayout localGridBagLayout = new GridBagLayout();
/* 2256 */       GridBagConstraints localGridBagConstraints = new GridBagConstraints();
/*      */ 
/* 2258 */       setLayout(localGridBagLayout);
/*      */ 
/* 2260 */       localGridBagConstraints.fill = 1;
/* 2261 */       localGridBagConstraints.insets = ServiceDialog.panelInsets;
/* 2262 */       localGridBagConstraints.weightx = 1.0D;
/* 2263 */       localGridBagConstraints.weighty = 1.0D;
/*      */ 
/* 2265 */       localGridBagConstraints.gridwidth = -1;
/* 2266 */       this.pnlChromaticity = new ServiceDialog.ChromaticityPanel(ServiceDialog.this);
/* 2267 */       ServiceDialog.addToGB(this.pnlChromaticity, this, localGridBagLayout, localGridBagConstraints);
/*      */ 
/* 2269 */       localGridBagConstraints.gridwidth = 0;
/* 2270 */       this.pnlQuality = new ServiceDialog.QualityPanel(ServiceDialog.this);
/* 2271 */       ServiceDialog.addToGB(this.pnlQuality, this, localGridBagLayout, localGridBagConstraints);
/*      */ 
/* 2273 */       localGridBagConstraints.gridwidth = 1;
/* 2274 */       this.pnlSides = new ServiceDialog.SidesPanel(ServiceDialog.this);
/* 2275 */       ServiceDialog.addToGB(this.pnlSides, this, localGridBagLayout, localGridBagConstraints);
/*      */ 
/* 2277 */       localGridBagConstraints.gridwidth = 0;
/* 2278 */       this.pnlJobAttributes = new ServiceDialog.JobAttributesPanel(ServiceDialog.this);
/* 2279 */       ServiceDialog.addToGB(this.pnlJobAttributes, this, localGridBagLayout, localGridBagConstraints);
/*      */     }
/*      */ 
/*      */     public void updateInfo()
/*      */     {
/* 2284 */       this.pnlChromaticity.updateInfo();
/* 2285 */       this.pnlQuality.updateInfo();
/* 2286 */       this.pnlSides.updateInfo();
/* 2287 */       this.pnlJobAttributes.updateInfo();
/*      */     }
/*      */   }
/*      */ 
/*      */   private class ChromaticityPanel extends JPanel implements ActionListener
/*      */   {
/* 2294 */     private final String strTitle = ServiceDialog.getMsg("border.chromaticity");
/*      */     private JRadioButton rbMonochrome;
/*      */     private JRadioButton rbColor;
/*      */ 
/*      */     public ChromaticityPanel() {
/* 2300 */       GridBagLayout localGridBagLayout = new GridBagLayout();
/* 2301 */       GridBagConstraints localGridBagConstraints = new GridBagConstraints();
/*      */ 
/* 2303 */       setLayout(localGridBagLayout);
/* 2304 */       setBorder(BorderFactory.createTitledBorder(this.strTitle));
/*      */ 
/* 2306 */       localGridBagConstraints.fill = 1;
/* 2307 */       localGridBagConstraints.gridwidth = 0;
/* 2308 */       localGridBagConstraints.weighty = 1.0D;
/*      */ 
/* 2310 */       ButtonGroup localButtonGroup = new ButtonGroup();
/* 2311 */       this.rbMonochrome = ServiceDialog.createRadioButton("radiobutton.monochrome", this);
/* 2312 */       this.rbMonochrome.setSelected(true);
/* 2313 */       localButtonGroup.add(this.rbMonochrome);
/* 2314 */       ServiceDialog.addToGB(this.rbMonochrome, this, localGridBagLayout, localGridBagConstraints);
/* 2315 */       this.rbColor = ServiceDialog.createRadioButton("radiobutton.color", this);
/* 2316 */       localButtonGroup.add(this.rbColor);
/* 2317 */       ServiceDialog.addToGB(this.rbColor, this, localGridBagLayout, localGridBagConstraints);
/*      */     }
/*      */ 
/*      */     public void actionPerformed(ActionEvent paramActionEvent) {
/* 2321 */       Object localObject = paramActionEvent.getSource();
/*      */ 
/* 2324 */       if (localObject == this.rbMonochrome)
/* 2325 */         ServiceDialog.this.asCurrent.add(Chromaticity.MONOCHROME);
/* 2326 */       else if (localObject == this.rbColor)
/* 2327 */         ServiceDialog.this.asCurrent.add(Chromaticity.COLOR);
/*      */     }
/*      */ 
/*      */     public void updateInfo()
/*      */     {
/* 2332 */       Chromaticity localChromaticity1 = Chromaticity.class;
/* 2333 */       boolean bool1 = false;
/* 2334 */       boolean bool2 = false;
/*      */ 
/* 2336 */       if (ServiceDialog.this.isAWT) {
/* 2337 */         bool1 = true;
/* 2338 */         bool2 = true;
/*      */       }
/* 2340 */       else if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(localChromaticity1)) {
/* 2341 */         localObject = ServiceDialog.this.psCurrent.getSupportedAttributeValues(localChromaticity1, ServiceDialog.this.docFlavor, ServiceDialog.this.asCurrent);
/*      */ 
/* 2346 */         if ((localObject instanceof Chromaticity[])) {
/* 2347 */           Chromaticity[] arrayOfChromaticity = (Chromaticity[])localObject;
/*      */ 
/* 2349 */           for (int i = 0; i < arrayOfChromaticity.length; i++) {
/* 2350 */             Chromaticity localChromaticity2 = arrayOfChromaticity[i];
/*      */ 
/* 2352 */             if (localChromaticity2 == Chromaticity.MONOCHROME)
/* 2353 */               bool1 = true;
/* 2354 */             else if (localChromaticity2 == Chromaticity.COLOR) {
/* 2355 */               bool2 = true;
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2362 */       this.rbMonochrome.setEnabled(bool1);
/* 2363 */       this.rbColor.setEnabled(bool2);
/*      */ 
/* 2365 */       Object localObject = (Chromaticity)ServiceDialog.this.asCurrent.get(localChromaticity1);
/* 2366 */       if (localObject == null) {
/* 2367 */         localObject = (Chromaticity)ServiceDialog.this.psCurrent.getDefaultAttributeValue(localChromaticity1);
/* 2368 */         if (localObject == null) {
/* 2369 */           localObject = Chromaticity.MONOCHROME;
/*      */         }
/*      */       }
/*      */ 
/* 2373 */       if (localObject == Chromaticity.MONOCHROME)
/* 2374 */         this.rbMonochrome.setSelected(true);
/*      */       else
/* 2376 */         this.rbColor.setSelected(true);
/*      */     }
/*      */   }
/*      */ 
/*      */   private class CopiesPanel extends JPanel
/*      */     implements ActionListener, ChangeListener
/*      */   {
/* 1152 */     private final String strTitle = ServiceDialog.getMsg("border.copies");
/*      */     private SpinnerNumberModel snModel;
/*      */     private JSpinner spinCopies;
/*      */     private JLabel lblCopies;
/*      */     private JCheckBox cbCollate;
/*      */     private boolean scSupported;
/*      */ 
/*      */     public CopiesPanel()
/*      */     {
/* 1162 */       GridBagLayout localGridBagLayout = new GridBagLayout();
/* 1163 */       GridBagConstraints localGridBagConstraints = new GridBagConstraints();
/*      */ 
/* 1165 */       setLayout(localGridBagLayout);
/* 1166 */       setBorder(BorderFactory.createTitledBorder(this.strTitle));
/*      */ 
/* 1168 */       localGridBagConstraints.fill = 2;
/* 1169 */       localGridBagConstraints.insets = ServiceDialog.compInsets;
/*      */ 
/* 1171 */       this.lblCopies = new JLabel(ServiceDialog.getMsg("label.numcopies"), 11);
/* 1172 */       this.lblCopies.setDisplayedMnemonic(ServiceDialog.getMnemonic("label.numcopies"));
/* 1173 */       this.lblCopies.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("label.numcopies"));
/*      */ 
/* 1175 */       ServiceDialog.addToGB(this.lblCopies, this, localGridBagLayout, localGridBagConstraints);
/*      */ 
/* 1177 */       this.snModel = new SpinnerNumberModel(1, 1, 999, 1);
/* 1178 */       this.spinCopies = new JSpinner(this.snModel);
/* 1179 */       this.lblCopies.setLabelFor(this.spinCopies);
/*      */ 
/* 1181 */       ((JSpinner.NumberEditor)this.spinCopies.getEditor()).getTextField().setColumns(3);
/* 1182 */       this.spinCopies.addChangeListener(this);
/* 1183 */       localGridBagConstraints.gridwidth = 0;
/* 1184 */       ServiceDialog.addToGB(this.spinCopies, this, localGridBagLayout, localGridBagConstraints);
/*      */ 
/* 1186 */       this.cbCollate = ServiceDialog.createCheckBox("checkbox.collate", this);
/* 1187 */       this.cbCollate.setEnabled(false);
/* 1188 */       ServiceDialog.addToGB(this.cbCollate, this, localGridBagLayout, localGridBagConstraints);
/*      */     }
/*      */ 
/*      */     public void actionPerformed(ActionEvent paramActionEvent) {
/* 1192 */       if (this.cbCollate.isSelected())
/* 1193 */         ServiceDialog.this.asCurrent.add(SheetCollate.COLLATED);
/*      */       else
/* 1195 */         ServiceDialog.this.asCurrent.add(SheetCollate.UNCOLLATED);
/*      */     }
/*      */ 
/*      */     public void stateChanged(ChangeEvent paramChangeEvent)
/*      */     {
/* 1200 */       updateCollateCB();
/*      */ 
/* 1202 */       ServiceDialog.this.asCurrent.add(new Copies(this.snModel.getNumber().intValue()));
/*      */     }
/*      */ 
/*      */     private void updateCollateCB() {
/* 1206 */       int i = this.snModel.getNumber().intValue();
/* 1207 */       if (ServiceDialog.this.isAWT)
/* 1208 */         this.cbCollate.setEnabled(true);
/*      */       else
/* 1210 */         this.cbCollate.setEnabled((i > 1) && (this.scSupported));
/*      */     }
/*      */ 
/*      */     public void updateInfo()
/*      */     {
/* 1215 */       Copies localCopies1 = Copies.class;
/* 1216 */       CopiesSupported localCopiesSupported1 = CopiesSupported.class;
/* 1217 */       SheetCollate localSheetCollate1 = SheetCollate.class;
/* 1218 */       boolean bool = false;
/* 1219 */       this.scSupported = false;
/*      */ 
/* 1222 */       if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(localCopies1)) {
/* 1223 */         bool = true;
/*      */       }
/* 1225 */       CopiesSupported localCopiesSupported2 = (CopiesSupported)ServiceDialog.this.psCurrent.getSupportedAttributeValues(localCopies1, null, null);
/*      */ 
/* 1228 */       if (localCopiesSupported2 == null) {
/* 1229 */         localCopiesSupported2 = new CopiesSupported(1, 999);
/*      */       }
/* 1231 */       Copies localCopies2 = (Copies)ServiceDialog.this.asCurrent.get(localCopies1);
/* 1232 */       if (localCopies2 == null) {
/* 1233 */         localCopies2 = (Copies)ServiceDialog.this.psCurrent.getDefaultAttributeValue(localCopies1);
/* 1234 */         if (localCopies2 == null) {
/* 1235 */           localCopies2 = new Copies(1);
/*      */         }
/*      */       }
/* 1238 */       this.spinCopies.setEnabled(bool);
/* 1239 */       this.lblCopies.setEnabled(bool);
/*      */ 
/* 1241 */       int[][] arrayOfInt = localCopiesSupported2.getMembers();
/*      */       int i;
/*      */       int j;
/* 1243 */       if ((arrayOfInt.length > 0) && (arrayOfInt[0].length > 0)) {
/* 1244 */         i = arrayOfInt[0][0];
/* 1245 */         j = arrayOfInt[0][1];
/*      */       } else {
/* 1247 */         i = 1;
/* 1248 */         j = 2147483647;
/*      */       }
/* 1250 */       this.snModel.setMinimum(new Integer(i));
/* 1251 */       this.snModel.setMaximum(new Integer(j));
/*      */ 
/* 1253 */       int k = localCopies2.getValue();
/* 1254 */       if ((k < i) || (k > j)) {
/* 1255 */         k = i;
/*      */       }
/* 1257 */       this.snModel.setValue(new Integer(k));
/*      */ 
/* 1260 */       if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(localSheetCollate1)) {
/* 1261 */         this.scSupported = true;
/*      */       }
/* 1263 */       SheetCollate localSheetCollate2 = (SheetCollate)ServiceDialog.this.asCurrent.get(localSheetCollate1);
/* 1264 */       if (localSheetCollate2 == null) {
/* 1265 */         localSheetCollate2 = (SheetCollate)ServiceDialog.this.psCurrent.getDefaultAttributeValue(localSheetCollate1);
/* 1266 */         if (localSheetCollate2 == null) {
/* 1267 */           localSheetCollate2 = SheetCollate.UNCOLLATED;
/*      */         }
/*      */       }
/* 1270 */       this.cbCollate.setSelected(localSheetCollate2 == SheetCollate.COLLATED);
/* 1271 */       updateCollateCB();
/*      */     }
/*      */   }
/*      */ 
/*      */   private class GeneralPanel extends JPanel
/*      */   {
/*      */     private ServiceDialog.PrintServicePanel pnlPrintService;
/*      */     private ServiceDialog.PrintRangePanel pnlPrintRange;
/*      */     private ServiceDialog.CopiesPanel pnlCopies;
/*      */ 
/*      */     public GeneralPanel()
/*      */     {
/*  669 */       GridBagLayout localGridBagLayout = new GridBagLayout();
/*  670 */       GridBagConstraints localGridBagConstraints = new GridBagConstraints();
/*      */ 
/*  672 */       setLayout(localGridBagLayout);
/*      */ 
/*  674 */       localGridBagConstraints.fill = 1;
/*  675 */       localGridBagConstraints.insets = ServiceDialog.panelInsets;
/*  676 */       localGridBagConstraints.weightx = 1.0D;
/*  677 */       localGridBagConstraints.weighty = 1.0D;
/*      */ 
/*  679 */       localGridBagConstraints.gridwidth = 0;
/*  680 */       this.pnlPrintService = new ServiceDialog.PrintServicePanel(ServiceDialog.this);
/*  681 */       ServiceDialog.addToGB(this.pnlPrintService, this, localGridBagLayout, localGridBagConstraints);
/*      */ 
/*  683 */       localGridBagConstraints.gridwidth = -1;
/*  684 */       this.pnlPrintRange = new ServiceDialog.PrintRangePanel(ServiceDialog.this);
/*  685 */       ServiceDialog.addToGB(this.pnlPrintRange, this, localGridBagLayout, localGridBagConstraints);
/*      */ 
/*  687 */       localGridBagConstraints.gridwidth = 0;
/*  688 */       this.pnlCopies = new ServiceDialog.CopiesPanel(ServiceDialog.this);
/*  689 */       ServiceDialog.addToGB(this.pnlCopies, this, localGridBagLayout, localGridBagConstraints);
/*      */     }
/*      */ 
/*      */     public boolean isPrintToFileRequested() {
/*  693 */       return this.pnlPrintService.isPrintToFileSelected();
/*      */     }
/*      */ 
/*      */     public void updateInfo() {
/*  697 */       this.pnlPrintService.updateInfo();
/*  698 */       this.pnlPrintRange.updateInfo();
/*  699 */       this.pnlCopies.updateInfo();
/*      */     }
/*      */   }
/*      */ 
/*      */   private class IconRadioButton extends JPanel
/*      */   {
/*      */     private JRadioButton rb;
/*      */     private JLabel lbl;
/*      */ 
/*      */     public IconRadioButton(String paramString1, String paramBoolean, boolean paramButtonGroup, ButtonGroup paramActionListener, ActionListener arg6)
/*      */     {
/* 2780 */       super();
/* 2781 */       final URL localURL = ServiceDialog.getImageResource(paramBoolean);
/* 2782 */       Icon localIcon = (Icon)AccessController.doPrivileged(new PrivilegedAction()
/*      */       {
/*      */         public Object run() {
/* 2785 */           ImageIcon localImageIcon = new ImageIcon(localURL);
/* 2786 */           return localImageIcon;
/*      */         }
/*      */       });
/* 2789 */       this.lbl = new JLabel(localIcon);
/* 2790 */       add(this.lbl);
/*      */       ActionListener localActionListener;
/* 2792 */       this.rb = ServiceDialog.createRadioButton(paramString1, localActionListener);
/* 2793 */       this.rb.setSelected(paramButtonGroup);
/* 2794 */       ServiceDialog.addToBG(this.rb, this, paramActionListener);
/*      */     }
/*      */ 
/*      */     public void addActionListener(ActionListener paramActionListener) {
/* 2798 */       this.rb.addActionListener(paramActionListener);
/*      */     }
/*      */ 
/*      */     public boolean isSameAs(Object paramObject) {
/* 2802 */       return this.rb == paramObject;
/*      */     }
/*      */ 
/*      */     public void setEnabled(boolean paramBoolean) {
/* 2806 */       this.rb.setEnabled(paramBoolean);
/* 2807 */       this.lbl.setEnabled(paramBoolean);
/*      */     }
/*      */ 
/*      */     public boolean isSelected() {
/* 2811 */       return this.rb.isSelected();
/*      */     }
/*      */ 
/*      */     public void setSelected(boolean paramBoolean) {
/* 2815 */       this.rb.setSelected(paramBoolean);
/*      */     }
/*      */   }
/*      */ 
/*      */   private class JobAttributesPanel extends JPanel
/*      */     implements ActionListener, ChangeListener, FocusListener
/*      */   {
/* 2588 */     private final String strTitle = ServiceDialog.getMsg("border.jobattributes");
/*      */     private JLabel lblPriority;
/*      */     private JLabel lblJobName;
/*      */     private JLabel lblUserName;
/*      */     private JSpinner spinPriority;
/*      */     private SpinnerNumberModel snModel;
/*      */     private JCheckBox cbJobSheets;
/*      */     private JTextField tfJobName;
/*      */     private JTextField tfUserName;
/*      */ 
/*      */     public JobAttributesPanel()
/*      */     {
/* 2598 */       GridBagLayout localGridBagLayout = new GridBagLayout();
/* 2599 */       GridBagConstraints localGridBagConstraints = new GridBagConstraints();
/*      */ 
/* 2601 */       setLayout(localGridBagLayout);
/* 2602 */       setBorder(BorderFactory.createTitledBorder(this.strTitle));
/*      */ 
/* 2604 */       localGridBagConstraints.fill = 0;
/* 2605 */       localGridBagConstraints.insets = ServiceDialog.compInsets;
/* 2606 */       localGridBagConstraints.weighty = 1.0D;
/*      */ 
/* 2608 */       this.cbJobSheets = ServiceDialog.createCheckBox("checkbox.jobsheets", this);
/* 2609 */       localGridBagConstraints.anchor = 21;
/* 2610 */       ServiceDialog.addToGB(this.cbJobSheets, this, localGridBagLayout, localGridBagConstraints);
/*      */ 
/* 2612 */       JPanel localJPanel = new JPanel();
/* 2613 */       this.lblPriority = new JLabel(ServiceDialog.getMsg("label.priority"), 11);
/* 2614 */       this.lblPriority.setDisplayedMnemonic(ServiceDialog.getMnemonic("label.priority"));
/*      */ 
/* 2616 */       localJPanel.add(this.lblPriority);
/* 2617 */       this.snModel = new SpinnerNumberModel(1, 1, 100, 1);
/* 2618 */       this.spinPriority = new JSpinner(this.snModel);
/* 2619 */       this.lblPriority.setLabelFor(this.spinPriority);
/*      */ 
/* 2621 */       ((JSpinner.NumberEditor)this.spinPriority.getEditor()).getTextField().setColumns(3);
/* 2622 */       this.spinPriority.addChangeListener(this);
/* 2623 */       localJPanel.add(this.spinPriority);
/* 2624 */       localGridBagConstraints.anchor = 22;
/* 2625 */       localGridBagConstraints.gridwidth = 0;
/* 2626 */       localJPanel.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("label.priority"));
/*      */ 
/* 2628 */       ServiceDialog.addToGB(localJPanel, this, localGridBagLayout, localGridBagConstraints);
/*      */ 
/* 2630 */       localGridBagConstraints.fill = 2;
/* 2631 */       localGridBagConstraints.anchor = 10;
/* 2632 */       localGridBagConstraints.weightx = 0.0D;
/* 2633 */       localGridBagConstraints.gridwidth = 1;
/* 2634 */       char c1 = ServiceDialog.getMnemonic("label.jobname");
/* 2635 */       this.lblJobName = new JLabel(ServiceDialog.getMsg("label.jobname"), 11);
/* 2636 */       this.lblJobName.setDisplayedMnemonic(c1);
/* 2637 */       ServiceDialog.addToGB(this.lblJobName, this, localGridBagLayout, localGridBagConstraints);
/* 2638 */       localGridBagConstraints.weightx = 1.0D;
/* 2639 */       localGridBagConstraints.gridwidth = 0;
/* 2640 */       this.tfJobName = new JTextField();
/* 2641 */       this.lblJobName.setLabelFor(this.tfJobName);
/* 2642 */       this.tfJobName.addFocusListener(this);
/* 2643 */       this.tfJobName.setFocusAccelerator(c1);
/* 2644 */       this.tfJobName.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("label.jobname"));
/*      */ 
/* 2646 */       ServiceDialog.addToGB(this.tfJobName, this, localGridBagLayout, localGridBagConstraints);
/*      */ 
/* 2648 */       localGridBagConstraints.weightx = 0.0D;
/* 2649 */       localGridBagConstraints.gridwidth = 1;
/* 2650 */       char c2 = ServiceDialog.getMnemonic("label.username");
/* 2651 */       this.lblUserName = new JLabel(ServiceDialog.getMsg("label.username"), 11);
/* 2652 */       this.lblUserName.setDisplayedMnemonic(c2);
/* 2653 */       ServiceDialog.addToGB(this.lblUserName, this, localGridBagLayout, localGridBagConstraints);
/* 2654 */       localGridBagConstraints.gridwidth = 0;
/* 2655 */       this.tfUserName = new JTextField();
/* 2656 */       this.lblUserName.setLabelFor(this.tfUserName);
/* 2657 */       this.tfUserName.addFocusListener(this);
/* 2658 */       this.tfUserName.setFocusAccelerator(c2);
/* 2659 */       this.tfUserName.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("label.username"));
/*      */ 
/* 2661 */       ServiceDialog.addToGB(this.tfUserName, this, localGridBagLayout, localGridBagConstraints);
/*      */     }
/*      */ 
/*      */     public void actionPerformed(ActionEvent paramActionEvent) {
/* 2665 */       if (this.cbJobSheets.isSelected())
/* 2666 */         ServiceDialog.this.asCurrent.add(JobSheets.STANDARD);
/*      */       else
/* 2668 */         ServiceDialog.this.asCurrent.add(JobSheets.NONE);
/*      */     }
/*      */ 
/*      */     public void stateChanged(ChangeEvent paramChangeEvent)
/*      */     {
/* 2673 */       ServiceDialog.this.asCurrent.add(new JobPriority(this.snModel.getNumber().intValue()));
/*      */     }
/*      */ 
/*      */     public void focusLost(FocusEvent paramFocusEvent) {
/* 2677 */       Object localObject = paramFocusEvent.getSource();
/*      */ 
/* 2679 */       if (localObject == this.tfJobName) {
/* 2680 */         ServiceDialog.this.asCurrent.add(new JobName(this.tfJobName.getText(), Locale.getDefault()));
/*      */       }
/* 2682 */       else if (localObject == this.tfUserName)
/* 2683 */         ServiceDialog.this.asCurrent.add(new RequestingUserName(this.tfUserName.getText(), Locale.getDefault()));
/*      */     }
/*      */ 
/*      */     public void focusGained(FocusEvent paramFocusEvent)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void updateInfo() {
/* 2691 */       JobSheets localJobSheets1 = JobSheets.class;
/* 2692 */       JobPriority localJobPriority1 = JobPriority.class;
/* 2693 */       JobName localJobName1 = JobName.class;
/* 2694 */       RequestingUserName localRequestingUserName1 = RequestingUserName.class;
/* 2695 */       boolean bool1 = false;
/* 2696 */       boolean bool2 = false;
/* 2697 */       boolean bool3 = false;
/* 2698 */       boolean bool4 = false;
/*      */ 
/* 2701 */       if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(localJobSheets1)) {
/* 2702 */         bool1 = true;
/*      */       }
/* 2704 */       JobSheets localJobSheets2 = (JobSheets)ServiceDialog.this.asCurrent.get(localJobSheets1);
/* 2705 */       if (localJobSheets2 == null) {
/* 2706 */         localJobSheets2 = (JobSheets)ServiceDialog.this.psCurrent.getDefaultAttributeValue(localJobSheets1);
/* 2707 */         if (localJobSheets2 == null) {
/* 2708 */           localJobSheets2 = JobSheets.NONE;
/*      */         }
/*      */       }
/* 2711 */       this.cbJobSheets.setSelected(localJobSheets2 != JobSheets.NONE);
/* 2712 */       this.cbJobSheets.setEnabled(bool1);
/*      */ 
/* 2715 */       if ((!ServiceDialog.this.isAWT) && (ServiceDialog.this.psCurrent.isAttributeCategorySupported(localJobPriority1))) {
/* 2716 */         bool2 = true;
/*      */       }
/* 2718 */       JobPriority localJobPriority2 = (JobPriority)ServiceDialog.this.asCurrent.get(localJobPriority1);
/* 2719 */       if (localJobPriority2 == null) {
/* 2720 */         localJobPriority2 = (JobPriority)ServiceDialog.this.psCurrent.getDefaultAttributeValue(localJobPriority1);
/* 2721 */         if (localJobPriority2 == null) {
/* 2722 */           localJobPriority2 = new JobPriority(1);
/*      */         }
/*      */       }
/* 2725 */       int i = localJobPriority2.getValue();
/* 2726 */       if ((i < 1) || (i > 100)) {
/* 2727 */         i = 1;
/*      */       }
/* 2729 */       this.snModel.setValue(new Integer(i));
/* 2730 */       this.lblPriority.setEnabled(bool2);
/* 2731 */       this.spinPriority.setEnabled(bool2);
/*      */ 
/* 2734 */       if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(localJobName1)) {
/* 2735 */         bool3 = true;
/*      */       }
/* 2737 */       JobName localJobName2 = (JobName)ServiceDialog.this.asCurrent.get(localJobName1);
/* 2738 */       if (localJobName2 == null) {
/* 2739 */         localJobName2 = (JobName)ServiceDialog.this.psCurrent.getDefaultAttributeValue(localJobName1);
/* 2740 */         if (localJobName2 == null) {
/* 2741 */           localJobName2 = new JobName("", Locale.getDefault());
/*      */         }
/*      */       }
/* 2744 */       this.tfJobName.setText(localJobName2.getValue());
/* 2745 */       this.tfJobName.setEnabled(bool3);
/* 2746 */       this.lblJobName.setEnabled(bool3);
/*      */ 
/* 2749 */       if ((!ServiceDialog.this.isAWT) && (ServiceDialog.this.psCurrent.isAttributeCategorySupported(localRequestingUserName1))) {
/* 2750 */         bool4 = true;
/*      */       }
/* 2752 */       RequestingUserName localRequestingUserName2 = (RequestingUserName)ServiceDialog.this.asCurrent.get(localRequestingUserName1);
/* 2753 */       if (localRequestingUserName2 == null) {
/* 2754 */         localRequestingUserName2 = (RequestingUserName)ServiceDialog.this.psCurrent.getDefaultAttributeValue(localRequestingUserName1);
/* 2755 */         if (localRequestingUserName2 == null) {
/* 2756 */           localRequestingUserName2 = new RequestingUserName("", Locale.getDefault());
/*      */         }
/*      */       }
/* 2759 */       this.tfUserName.setText(localRequestingUserName2.getValue());
/* 2760 */       this.tfUserName.setEnabled(bool4);
/* 2761 */       this.lblUserName.setEnabled(bool4);
/*      */     }
/*      */   }
/*      */ 
/*      */   private class MarginsPanel extends JPanel
/*      */     implements ActionListener, FocusListener
/*      */   {
/* 1326 */     private final String strTitle = ServiceDialog.getMsg("border.margins");
/*      */     private JFormattedTextField leftMargin;
/*      */     private JFormattedTextField rightMargin;
/*      */     private JFormattedTextField topMargin;
/*      */     private JFormattedTextField bottomMargin;
/*      */     private JLabel lblLeft;
/*      */     private JLabel lblRight;
/*      */     private JLabel lblTop;
/*      */     private JLabel lblBottom;
/* 1330 */     private int units = 1000;
/*      */ 
/* 1332 */     private float lmVal = -1.0F; private float rmVal = -1.0F; private float tmVal = -1.0F; private float bmVal = -1.0F;
/*      */     private Float lmObj;
/*      */     private Float rmObj;
/*      */     private Float tmObj;
/*      */     private Float bmObj;
/*      */ 
/*      */     public MarginsPanel()
/*      */     {
/* 1339 */       GridBagLayout localGridBagLayout = new GridBagLayout();
/* 1340 */       GridBagConstraints localGridBagConstraints = new GridBagConstraints();
/* 1341 */       localGridBagConstraints.fill = 2;
/* 1342 */       localGridBagConstraints.weightx = 1.0D;
/* 1343 */       localGridBagConstraints.weighty = 0.0D;
/* 1344 */       localGridBagConstraints.insets = ServiceDialog.compInsets;
/*      */ 
/* 1346 */       setLayout(localGridBagLayout);
/* 1347 */       setBorder(BorderFactory.createTitledBorder(this.strTitle));
/*      */ 
/* 1349 */       String str1 = "label.millimetres";
/* 1350 */       String str2 = Locale.getDefault().getCountry();
/* 1351 */       if ((str2 != null) && ((str2.equals("")) || (str2.equals(Locale.US.getCountry())) || (str2.equals(Locale.CANADA.getCountry()))))
/*      */       {
/* 1355 */         str1 = "label.inches";
/* 1356 */         this.units = 25400;
/*      */       }
/* 1358 */       String str3 = ServiceDialog.getMsg(str1);
/*      */       DecimalFormat localDecimalFormat;
/* 1361 */       if (this.units == 1000) {
/* 1362 */         localDecimalFormat = new DecimalFormat("###.##");
/* 1363 */         localDecimalFormat.setMaximumIntegerDigits(3);
/*      */       } else {
/* 1365 */         localDecimalFormat = new DecimalFormat("##.##");
/* 1366 */         localDecimalFormat.setMaximumIntegerDigits(2);
/*      */       }
/*      */ 
/* 1369 */       localDecimalFormat.setMinimumFractionDigits(1);
/* 1370 */       localDecimalFormat.setMaximumFractionDigits(2);
/* 1371 */       localDecimalFormat.setMinimumIntegerDigits(1);
/* 1372 */       localDecimalFormat.setParseIntegerOnly(false);
/* 1373 */       localDecimalFormat.setDecimalSeparatorAlwaysShown(true);
/* 1374 */       NumberFormatter localNumberFormatter = new NumberFormatter(localDecimalFormat);
/* 1375 */       localNumberFormatter.setMinimum(new Float(0.0F));
/* 1376 */       localNumberFormatter.setMaximum(new Float(999.0F));
/* 1377 */       localNumberFormatter.setAllowsInvalid(true);
/* 1378 */       localNumberFormatter.setCommitsOnValidEdit(true);
/*      */ 
/* 1380 */       this.leftMargin = new JFormattedTextField(localNumberFormatter);
/* 1381 */       this.leftMargin.addFocusListener(this);
/* 1382 */       this.leftMargin.addActionListener(this);
/* 1383 */       this.leftMargin.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("label.leftmargin"));
/*      */ 
/* 1385 */       this.rightMargin = new JFormattedTextField(localNumberFormatter);
/* 1386 */       this.rightMargin.addFocusListener(this);
/* 1387 */       this.rightMargin.addActionListener(this);
/* 1388 */       this.rightMargin.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("label.rightmargin"));
/*      */ 
/* 1390 */       this.topMargin = new JFormattedTextField(localNumberFormatter);
/* 1391 */       this.topMargin.addFocusListener(this);
/* 1392 */       this.topMargin.addActionListener(this);
/* 1393 */       this.topMargin.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("label.topmargin"));
/*      */ 
/* 1395 */       this.topMargin = new JFormattedTextField(localNumberFormatter);
/* 1396 */       this.bottomMargin = new JFormattedTextField(localNumberFormatter);
/* 1397 */       this.bottomMargin.addFocusListener(this);
/* 1398 */       this.bottomMargin.addActionListener(this);
/* 1399 */       this.bottomMargin.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("label.bottommargin"));
/*      */ 
/* 1401 */       this.topMargin = new JFormattedTextField(localNumberFormatter);
/* 1402 */       localGridBagConstraints.gridwidth = -1;
/* 1403 */       this.lblLeft = new JLabel(ServiceDialog.getMsg("label.leftmargin") + " " + str3, 10);
/*      */ 
/* 1405 */       this.lblLeft.setDisplayedMnemonic(ServiceDialog.getMnemonic("label.leftmargin"));
/* 1406 */       this.lblLeft.setLabelFor(this.leftMargin);
/* 1407 */       ServiceDialog.addToGB(this.lblLeft, this, localGridBagLayout, localGridBagConstraints);
/*      */ 
/* 1409 */       localGridBagConstraints.gridwidth = 0;
/* 1410 */       this.lblRight = new JLabel(ServiceDialog.getMsg("label.rightmargin") + " " + str3, 10);
/*      */ 
/* 1412 */       this.lblRight.setDisplayedMnemonic(ServiceDialog.getMnemonic("label.rightmargin"));
/* 1413 */       this.lblRight.setLabelFor(this.rightMargin);
/* 1414 */       ServiceDialog.addToGB(this.lblRight, this, localGridBagLayout, localGridBagConstraints);
/*      */ 
/* 1416 */       localGridBagConstraints.gridwidth = -1;
/* 1417 */       ServiceDialog.addToGB(this.leftMargin, this, localGridBagLayout, localGridBagConstraints);
/*      */ 
/* 1419 */       localGridBagConstraints.gridwidth = 0;
/* 1420 */       ServiceDialog.addToGB(this.rightMargin, this, localGridBagLayout, localGridBagConstraints);
/*      */ 
/* 1423 */       ServiceDialog.addToGB(new JPanel(), this, localGridBagLayout, localGridBagConstraints);
/*      */ 
/* 1425 */       localGridBagConstraints.gridwidth = -1;
/* 1426 */       this.lblTop = new JLabel(ServiceDialog.getMsg("label.topmargin") + " " + str3, 10);
/*      */ 
/* 1428 */       this.lblTop.setDisplayedMnemonic(ServiceDialog.getMnemonic("label.topmargin"));
/* 1429 */       this.lblTop.setLabelFor(this.topMargin);
/* 1430 */       ServiceDialog.addToGB(this.lblTop, this, localGridBagLayout, localGridBagConstraints);
/*      */ 
/* 1432 */       localGridBagConstraints.gridwidth = 0;
/* 1433 */       this.lblBottom = new JLabel(ServiceDialog.getMsg("label.bottommargin") + " " + str3, 10);
/*      */ 
/* 1435 */       this.lblBottom.setDisplayedMnemonic(ServiceDialog.getMnemonic("label.bottommargin"));
/* 1436 */       this.lblBottom.setLabelFor(this.bottomMargin);
/* 1437 */       ServiceDialog.addToGB(this.lblBottom, this, localGridBagLayout, localGridBagConstraints);
/*      */ 
/* 1439 */       localGridBagConstraints.gridwidth = -1;
/* 1440 */       ServiceDialog.addToGB(this.topMargin, this, localGridBagLayout, localGridBagConstraints);
/*      */ 
/* 1442 */       localGridBagConstraints.gridwidth = 0;
/* 1443 */       ServiceDialog.addToGB(this.bottomMargin, this, localGridBagLayout, localGridBagConstraints);
/*      */     }
/*      */ 
/*      */     public void actionPerformed(ActionEvent paramActionEvent)
/*      */     {
/* 1448 */       Object localObject = paramActionEvent.getSource();
/* 1449 */       updateMargins(localObject);
/*      */     }
/*      */ 
/*      */     public void focusLost(FocusEvent paramFocusEvent) {
/* 1453 */       Object localObject = paramFocusEvent.getSource();
/* 1454 */       updateMargins(localObject);
/*      */     }
/*      */ 
/*      */     public void focusGained(FocusEvent paramFocusEvent)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void updateMargins(Object paramObject)
/*      */     {
/* 1465 */       if (!(paramObject instanceof JFormattedTextField)) {
/* 1466 */         return;
/*      */       }
/* 1468 */       Object localObject = (JFormattedTextField)paramObject;
/* 1469 */       Float localFloat1 = (Float)((JFormattedTextField)localObject).getValue();
/* 1470 */       if (localFloat1 == null) {
/* 1471 */         return;
/*      */       }
/* 1473 */       if ((localObject == this.leftMargin) && (localFloat1.equals(this.lmObj))) {
/* 1474 */         return;
/*      */       }
/* 1476 */       if ((localObject == this.rightMargin) && (localFloat1.equals(this.rmObj))) {
/* 1477 */         return;
/*      */       }
/* 1479 */       if ((localObject == this.topMargin) && (localFloat1.equals(this.tmObj))) {
/* 1480 */         return;
/*      */       }
/* 1482 */       if ((localObject == this.bottomMargin) && (localFloat1.equals(this.bmObj))) {
/* 1483 */         return;
/*      */       }
/*      */ 
/* 1487 */       localObject = (Float)this.leftMargin.getValue();
/* 1488 */       localFloat1 = (Float)this.rightMargin.getValue();
/* 1489 */       Float localFloat2 = (Float)this.topMargin.getValue();
/* 1490 */       Float localFloat3 = (Float)this.bottomMargin.getValue();
/*      */ 
/* 1492 */       float f1 = ((Float)localObject).floatValue();
/* 1493 */       float f2 = localFloat1.floatValue();
/* 1494 */       float f3 = localFloat2.floatValue();
/* 1495 */       float f4 = localFloat3.floatValue();
/*      */ 
/* 1498 */       OrientationRequested localOrientationRequested1 = OrientationRequested.class;
/* 1499 */       OrientationRequested localOrientationRequested2 = (OrientationRequested)ServiceDialog.this.asCurrent.get(localOrientationRequested1);
/*      */ 
/* 1502 */       if (localOrientationRequested2 == null)
/* 1503 */         localOrientationRequested2 = (OrientationRequested)ServiceDialog.this.psCurrent.getDefaultAttributeValue(localOrientationRequested1);
/*      */       float f5;
/* 1508 */       if (localOrientationRequested2 == OrientationRequested.REVERSE_PORTRAIT) {
/* 1509 */         f5 = f1; f1 = f2; f2 = f5;
/* 1510 */         f5 = f3; f3 = f4; f4 = f5;
/* 1511 */       } else if (localOrientationRequested2 == OrientationRequested.LANDSCAPE) {
/* 1512 */         f5 = f1;
/* 1513 */         f1 = f3;
/* 1514 */         f3 = f2;
/* 1515 */         f2 = f4;
/* 1516 */         f4 = f5;
/* 1517 */       } else if (localOrientationRequested2 == OrientationRequested.REVERSE_LANDSCAPE) {
/* 1518 */         f5 = f1;
/* 1519 */         f1 = f4;
/* 1520 */         f4 = f2;
/* 1521 */         f2 = f3;
/* 1522 */         f3 = f5;
/*      */       }
/*      */       MediaPrintableArea localMediaPrintableArea;
/* 1525 */       if ((localMediaPrintableArea = validateMargins(f1, f2, f3, f4)) != null) {
/* 1526 */         ServiceDialog.this.asCurrent.add(localMediaPrintableArea);
/* 1527 */         this.lmVal = f1;
/* 1528 */         this.rmVal = f2;
/* 1529 */         this.tmVal = f3;
/* 1530 */         this.bmVal = f4;
/* 1531 */         this.lmObj = ((Float)localObject);
/* 1532 */         this.rmObj = localFloat1;
/* 1533 */         this.tmObj = localFloat2;
/* 1534 */         this.bmObj = localFloat3;
/*      */       } else {
/* 1536 */         if ((this.lmObj == null) || (this.rmObj == null) || (this.tmObj == null) || (this.rmObj == null))
/*      */         {
/* 1538 */           return;
/*      */         }
/* 1540 */         this.leftMargin.setValue(this.lmObj);
/* 1541 */         this.rightMargin.setValue(this.rmObj);
/* 1542 */         this.topMargin.setValue(this.tmObj);
/* 1543 */         this.bottomMargin.setValue(this.bmObj);
/*      */       }
/*      */     }
/*      */ 
/*      */     private MediaPrintableArea validateMargins(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
/*      */     {
/* 1562 */       MediaPrintableArea localMediaPrintableArea1 = MediaPrintableArea.class;
/*      */ 
/* 1564 */       MediaPrintableArea localMediaPrintableArea2 = null;
/* 1565 */       MediaSize localMediaSize = null;
/*      */ 
/* 1567 */       Media localMedia = (Media)ServiceDialog.this.asCurrent.get(Media.class);
/* 1568 */       if ((localMedia == null) || (!(localMedia instanceof MediaSizeName)))
/* 1569 */         localMedia = (Media)ServiceDialog.this.psCurrent.getDefaultAttributeValue(Media.class);
/*      */       Object localObject1;
/* 1571 */       if ((localMedia != null) && ((localMedia instanceof MediaSizeName))) {
/* 1572 */         localObject1 = (MediaSizeName)localMedia;
/* 1573 */         localMediaSize = MediaSize.getMediaSizeForName((MediaSizeName)localObject1);
/*      */       }
/* 1575 */       if (localMediaSize == null) {
/* 1576 */         localMediaSize = new MediaSize(8.5F, 11.0F, 25400);
/*      */       }
/*      */ 
/* 1579 */       if (localMedia != null) {
/* 1580 */         localObject1 = new HashPrintRequestAttributeSet(ServiceDialog.this.asCurrent);
/*      */ 
/* 1582 */         ((PrintRequestAttributeSet)localObject1).add(localMedia);
/*      */ 
/* 1584 */         Object localObject2 = ServiceDialog.this.psCurrent.getSupportedAttributeValues(localMediaPrintableArea1, ServiceDialog.this.docFlavor, (AttributeSet)localObject1);
/*      */ 
/* 1588 */         if (((localObject2 instanceof MediaPrintableArea[])) && (((MediaPrintableArea[])localObject2).length > 0))
/*      */         {
/* 1590 */           localMediaPrintableArea2 = ((MediaPrintableArea[])(MediaPrintableArea[])localObject2)[0];
/*      */         }
/*      */       }
/*      */ 
/* 1594 */       if (localMediaPrintableArea2 == null) {
/* 1595 */         localMediaPrintableArea2 = new MediaPrintableArea(0.0F, 0.0F, localMediaSize.getX(this.units), localMediaSize.getY(this.units), this.units);
/*      */       }
/*      */ 
/* 1601 */       float f1 = localMediaSize.getX(this.units);
/* 1602 */       float f2 = localMediaSize.getY(this.units);
/* 1603 */       float f3 = paramFloat1;
/* 1604 */       float f4 = paramFloat3;
/* 1605 */       float f5 = f1 - paramFloat1 - paramFloat2;
/* 1606 */       float f6 = f2 - paramFloat3 - paramFloat4;
/*      */ 
/* 1608 */       if ((f5 <= 0.0F) || (f6 <= 0.0F) || (f3 < 0.0F) || (f4 < 0.0F) || (f3 < localMediaPrintableArea2.getX(this.units)) || (f5 > localMediaPrintableArea2.getWidth(this.units)) || (f4 < localMediaPrintableArea2.getY(this.units)) || (f6 > localMediaPrintableArea2.getHeight(this.units)))
/*      */       {
/* 1611 */         return null;
/*      */       }
/* 1613 */       return new MediaPrintableArea(paramFloat1, paramFloat3, f5, f6, this.units);
/*      */     }
/*      */ 
/*      */     public void updateInfo()
/*      */     {
/* 1632 */       if (ServiceDialog.this.isAWT) {
/* 1633 */         this.leftMargin.setEnabled(false);
/* 1634 */         this.rightMargin.setEnabled(false);
/* 1635 */         this.topMargin.setEnabled(false);
/* 1636 */         this.bottomMargin.setEnabled(false);
/* 1637 */         this.lblLeft.setEnabled(false);
/* 1638 */         this.lblRight.setEnabled(false);
/* 1639 */         this.lblTop.setEnabled(false);
/* 1640 */         this.lblBottom.setEnabled(false);
/* 1641 */         return;
/*      */       }
/*      */ 
/* 1644 */       MediaPrintableArea localMediaPrintableArea1 = MediaPrintableArea.class;
/* 1645 */       MediaPrintableArea localMediaPrintableArea2 = (MediaPrintableArea)ServiceDialog.this.asCurrent.get(localMediaPrintableArea1);
/*      */ 
/* 1647 */       MediaPrintableArea localMediaPrintableArea3 = null;
/* 1648 */       MediaSize localMediaSize = null;
/*      */ 
/* 1650 */       Media localMedia = (Media)ServiceDialog.this.asCurrent.get(Media.class);
/* 1651 */       if ((localMedia == null) || (!(localMedia instanceof MediaSizeName)))
/* 1652 */         localMedia = (Media)ServiceDialog.this.psCurrent.getDefaultAttributeValue(Media.class);
/*      */       Object localObject1;
/* 1654 */       if ((localMedia != null) && ((localMedia instanceof MediaSizeName))) {
/* 1655 */         localObject1 = (MediaSizeName)localMedia;
/* 1656 */         localMediaSize = MediaSize.getMediaSizeForName((MediaSizeName)localObject1);
/*      */       }
/* 1658 */       if (localMediaSize == null) {
/* 1659 */         localMediaSize = new MediaSize(8.5F, 11.0F, 25400);
/*      */       }
/*      */ 
/* 1662 */       if (localMedia != null) {
/* 1663 */         localObject1 = new HashPrintRequestAttributeSet(ServiceDialog.this.asCurrent);
/*      */ 
/* 1665 */         ((PrintRequestAttributeSet)localObject1).add(localMedia);
/*      */ 
/* 1667 */         Object localObject2 = ServiceDialog.this.psCurrent.getSupportedAttributeValues(localMediaPrintableArea1, ServiceDialog.this.docFlavor, (AttributeSet)localObject1);
/*      */ 
/* 1671 */         if (((localObject2 instanceof MediaPrintableArea[])) && (((MediaPrintableArea[])localObject2).length > 0))
/*      */         {
/* 1673 */           localMediaPrintableArea3 = ((MediaPrintableArea[])(MediaPrintableArea[])localObject2)[0];
/*      */         }
/* 1675 */         else if ((localObject2 instanceof MediaPrintableArea)) {
/* 1676 */           localMediaPrintableArea3 = (MediaPrintableArea)localObject2;
/*      */         }
/*      */       }
/* 1679 */       if (localMediaPrintableArea3 == null) {
/* 1680 */         localMediaPrintableArea3 = new MediaPrintableArea(0.0F, 0.0F, localMediaSize.getX(this.units), localMediaSize.getY(this.units), this.units);
/*      */       }
/*      */ 
/* 1696 */       float f1 = localMediaSize.getX(25400);
/* 1697 */       float f2 = localMediaSize.getY(25400);
/* 1698 */       float f3 = 5.0F;
/*      */       float f4;
/* 1700 */       if (f1 > f3)
/* 1701 */         f4 = 1.0F;
/*      */       else
/* 1703 */         f4 = f1 / f3;
/*      */       float f5;
/* 1705 */       if (f2 > f3)
/* 1706 */         f5 = 1.0F;
/*      */       else {
/* 1708 */         f5 = f2 / f3;
/*      */       }
/*      */ 
/* 1711 */       if (localMediaPrintableArea2 == null) {
/* 1712 */         localMediaPrintableArea2 = new MediaPrintableArea(f4, f5, f1 - 2.0F * f4, f2 - 2.0F * f5, 25400);
/*      */ 
/* 1715 */         ServiceDialog.this.asCurrent.add(localMediaPrintableArea2);
/*      */       }
/* 1717 */       float f6 = localMediaPrintableArea2.getX(this.units);
/* 1718 */       float f7 = localMediaPrintableArea2.getY(this.units);
/* 1719 */       float f8 = localMediaPrintableArea2.getWidth(this.units);
/* 1720 */       float f9 = localMediaPrintableArea2.getHeight(this.units);
/* 1721 */       float f10 = localMediaPrintableArea3.getX(this.units);
/* 1722 */       float f11 = localMediaPrintableArea3.getY(this.units);
/* 1723 */       float f12 = localMediaPrintableArea3.getWidth(this.units);
/* 1724 */       float f13 = localMediaPrintableArea3.getHeight(this.units);
/*      */ 
/* 1727 */       int i = 0;
/*      */ 
/* 1742 */       f1 = localMediaSize.getX(this.units);
/* 1743 */       f2 = localMediaSize.getY(this.units);
/* 1744 */       if (this.lmVal >= 0.0F) {
/* 1745 */         i = 1;
/*      */ 
/* 1747 */         if (this.lmVal + this.rmVal > f1)
/*      */         {
/* 1749 */           if (f8 > f12) {
/* 1750 */             f8 = f12;
/*      */           }
/*      */ 
/* 1753 */           f6 = (f1 - f8) / 2.0F;
/*      */         } else {
/* 1755 */           f6 = this.lmVal >= f10 ? this.lmVal : f10;
/* 1756 */           f8 = f1 - f6 - this.rmVal;
/*      */         }
/* 1758 */         if (this.tmVal + this.bmVal > f2) {
/* 1759 */           if (f9 > f13) {
/* 1760 */             f9 = f13;
/*      */           }
/* 1762 */           f7 = (f2 - f9) / 2.0F;
/*      */         } else {
/* 1764 */           f7 = this.tmVal >= f11 ? this.tmVal : f11;
/* 1765 */           f9 = f2 - f7 - this.bmVal;
/*      */         }
/*      */       }
/* 1768 */       if (f6 < f10) {
/* 1769 */         i = 1;
/* 1770 */         f6 = f10;
/*      */       }
/* 1772 */       if (f7 < f11) {
/* 1773 */         i = 1;
/* 1774 */         f7 = f11;
/*      */       }
/* 1776 */       if (f8 > f12) {
/* 1777 */         i = 1;
/* 1778 */         f8 = f12;
/*      */       }
/* 1780 */       if (f9 > f13) {
/* 1781 */         i = 1;
/* 1782 */         f9 = f13;
/*      */       }
/*      */ 
/* 1785 */       if ((f6 + f8 > f10 + f12) || (f8 <= 0.0F)) {
/* 1786 */         i = 1;
/* 1787 */         f6 = f10;
/* 1788 */         f8 = f12;
/*      */       }
/* 1790 */       if ((f7 + f9 > f11 + f13) || (f9 <= 0.0F)) {
/* 1791 */         i = 1;
/* 1792 */         f7 = f11;
/* 1793 */         f9 = f13;
/*      */       }
/*      */ 
/* 1796 */       if (i != 0) {
/* 1797 */         localMediaPrintableArea2 = new MediaPrintableArea(f6, f7, f8, f9, this.units);
/* 1798 */         ServiceDialog.this.asCurrent.add(localMediaPrintableArea2);
/*      */       }
/*      */ 
/* 1804 */       this.lmVal = f6;
/* 1805 */       this.tmVal = f7;
/* 1806 */       this.rmVal = (localMediaSize.getX(this.units) - f6 - f8);
/* 1807 */       this.bmVal = (localMediaSize.getY(this.units) - f7 - f9);
/*      */ 
/* 1809 */       this.lmObj = new Float(this.lmVal);
/* 1810 */       this.rmObj = new Float(this.rmVal);
/* 1811 */       this.tmObj = new Float(this.tmVal);
/* 1812 */       this.bmObj = new Float(this.bmVal);
/*      */ 
/* 1818 */       OrientationRequested localOrientationRequested1 = OrientationRequested.class;
/* 1819 */       OrientationRequested localOrientationRequested2 = (OrientationRequested)ServiceDialog.this.asCurrent.get(localOrientationRequested1);
/*      */ 
/* 1822 */       if (localOrientationRequested2 == null)
/* 1823 */         localOrientationRequested2 = (OrientationRequested)ServiceDialog.this.psCurrent.getDefaultAttributeValue(localOrientationRequested1);
/*      */       Float localFloat;
/* 1829 */       if (localOrientationRequested2 == OrientationRequested.REVERSE_PORTRAIT) {
/* 1830 */         localFloat = this.lmObj; this.lmObj = this.rmObj; this.rmObj = localFloat;
/* 1831 */         localFloat = this.tmObj; this.tmObj = this.bmObj; this.bmObj = localFloat;
/* 1832 */       } else if (localOrientationRequested2 == OrientationRequested.LANDSCAPE) {
/* 1833 */         localFloat = this.lmObj;
/* 1834 */         this.lmObj = this.bmObj;
/* 1835 */         this.bmObj = this.rmObj;
/* 1836 */         this.rmObj = this.tmObj;
/* 1837 */         this.tmObj = localFloat;
/* 1838 */       } else if (localOrientationRequested2 == OrientationRequested.REVERSE_LANDSCAPE) {
/* 1839 */         localFloat = this.lmObj;
/* 1840 */         this.lmObj = this.tmObj;
/* 1841 */         this.tmObj = this.rmObj;
/* 1842 */         this.rmObj = this.bmObj;
/* 1843 */         this.bmObj = localFloat;
/*      */       }
/*      */ 
/* 1846 */       this.leftMargin.setValue(this.lmObj);
/* 1847 */       this.rightMargin.setValue(this.rmObj);
/* 1848 */       this.topMargin.setValue(this.tmObj);
/* 1849 */       this.bottomMargin.setValue(this.bmObj);
/*      */     }
/*      */   }
/*      */ 
/*      */   private class MediaPanel extends JPanel implements ItemListener
/*      */   {
/* 1855 */     private final String strTitle = ServiceDialog.getMsg("border.media");
/*      */     private JLabel lblSize;
/*      */     private JLabel lblSource;
/*      */     private JComboBox cbSize;
/*      */     private JComboBox cbSource;
/* 1858 */     private Vector sizes = new Vector();
/* 1859 */     private Vector sources = new Vector();
/* 1860 */     private ServiceDialog.MarginsPanel pnlMargins = null;
/*      */ 
/*      */     public MediaPanel()
/*      */     {
/* 1865 */       GridBagLayout localGridBagLayout = new GridBagLayout();
/* 1866 */       GridBagConstraints localGridBagConstraints = new GridBagConstraints();
/*      */ 
/* 1868 */       setLayout(localGridBagLayout);
/* 1869 */       setBorder(BorderFactory.createTitledBorder(this.strTitle));
/*      */ 
/* 1871 */       this.cbSize = new JComboBox();
/* 1872 */       this.cbSource = new JComboBox();
/*      */ 
/* 1874 */       localGridBagConstraints.fill = 1;
/* 1875 */       localGridBagConstraints.insets = ServiceDialog.compInsets;
/* 1876 */       localGridBagConstraints.weighty = 1.0D;
/*      */ 
/* 1878 */       localGridBagConstraints.weightx = 0.0D;
/* 1879 */       this.lblSize = new JLabel(ServiceDialog.getMsg("label.size"), 11);
/* 1880 */       this.lblSize.setDisplayedMnemonic(ServiceDialog.getMnemonic("label.size"));
/* 1881 */       this.lblSize.setLabelFor(this.cbSize);
/* 1882 */       ServiceDialog.addToGB(this.lblSize, this, localGridBagLayout, localGridBagConstraints);
/* 1883 */       localGridBagConstraints.weightx = 1.0D;
/* 1884 */       localGridBagConstraints.gridwidth = 0;
/* 1885 */       ServiceDialog.addToGB(this.cbSize, this, localGridBagLayout, localGridBagConstraints);
/*      */ 
/* 1887 */       localGridBagConstraints.weightx = 0.0D;
/* 1888 */       localGridBagConstraints.gridwidth = 1;
/* 1889 */       this.lblSource = new JLabel(ServiceDialog.getMsg("label.source"), 11);
/* 1890 */       this.lblSource.setDisplayedMnemonic(ServiceDialog.getMnemonic("label.source"));
/* 1891 */       this.lblSource.setLabelFor(this.cbSource);
/* 1892 */       ServiceDialog.addToGB(this.lblSource, this, localGridBagLayout, localGridBagConstraints);
/* 1893 */       localGridBagConstraints.gridwidth = 0;
/* 1894 */       ServiceDialog.addToGB(this.cbSource, this, localGridBagLayout, localGridBagConstraints);
/*      */     }
/*      */ 
/*      */     private String getMediaName(String paramString)
/*      */     {
/*      */       try
/*      */       {
/* 1901 */         String str = paramString.replace(' ', '-');
/* 1902 */         str = str.replace('#', 'n');
/*      */ 
/* 1904 */         return ServiceDialog.messageRB.getString(str); } catch (MissingResourceException localMissingResourceException) {
/*      */       }
/* 1906 */       return paramString;
/*      */     }
/*      */ 
/*      */     public void itemStateChanged(ItemEvent paramItemEvent)
/*      */     {
/* 1911 */       Object localObject1 = paramItemEvent.getSource();
/*      */ 
/* 1913 */       if (paramItemEvent.getStateChange() == 1)
/*      */       {
/*      */         int i;
/*      */         Object localObject2;
/* 1914 */         if (localObject1 == this.cbSize) {
/* 1915 */           i = this.cbSize.getSelectedIndex();
/*      */ 
/* 1917 */           if ((i >= 0) && (i < this.sizes.size())) {
/* 1918 */             if ((this.cbSource.getItemCount() > 1) && (this.cbSource.getSelectedIndex() >= 1))
/*      */             {
/* 1921 */               int j = this.cbSource.getSelectedIndex() - 1;
/* 1922 */               localObject2 = (MediaTray)this.sources.get(j);
/* 1923 */               ServiceDialog.this.asCurrent.add(new SunAlternateMedia((Media)localObject2));
/*      */             }
/* 1925 */             ServiceDialog.this.asCurrent.add((MediaSizeName)this.sizes.get(i));
/*      */           }
/* 1927 */         } else if (localObject1 == this.cbSource) {
/* 1928 */           i = this.cbSource.getSelectedIndex();
/*      */ 
/* 1930 */           if ((i >= 1) && (i < this.sources.size() + 1)) {
/* 1931 */             ServiceDialog.this.asCurrent.remove(SunAlternateMedia.class);
/* 1932 */             MediaTray localMediaTray = (MediaTray)this.sources.get(i - 1);
/* 1933 */             localObject2 = (Media)ServiceDialog.this.asCurrent.get(Media.class);
/* 1934 */             if ((localObject2 == null) || ((localObject2 instanceof MediaTray))) {
/* 1935 */               ServiceDialog.this.asCurrent.add(localMediaTray);
/* 1936 */             } else if ((localObject2 instanceof MediaSizeName)) {
/* 1937 */               MediaSizeName localMediaSizeName = (MediaSizeName)localObject2;
/* 1938 */               Media localMedia = (Media)ServiceDialog.this.psCurrent.getDefaultAttributeValue(Media.class);
/* 1939 */               if (((localMedia instanceof MediaSizeName)) && (localMedia.equals(localMediaSizeName))) {
/* 1940 */                 ServiceDialog.this.asCurrent.add(localMediaTray);
/*      */               }
/*      */               else
/*      */               {
/* 1945 */                 ServiceDialog.this.asCurrent.add(new SunAlternateMedia(localMediaTray));
/*      */               }
/*      */             }
/* 1948 */           } else if (i == 0) {
/* 1949 */             ServiceDialog.this.asCurrent.remove(SunAlternateMedia.class);
/* 1950 */             if (this.cbSize.getItemCount() > 0) {
/* 1951 */               int k = this.cbSize.getSelectedIndex();
/* 1952 */               ServiceDialog.this.asCurrent.add((MediaSizeName)this.sizes.get(k));
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/* 1957 */         if (this.pnlMargins != null)
/* 1958 */           this.pnlMargins.updateInfo();
/*      */       }
/*      */     }
/*      */ 
/*      */     public void addMediaListener(ServiceDialog.MarginsPanel paramMarginsPanel)
/*      */     {
/* 1966 */       this.pnlMargins = paramMarginsPanel;
/*      */     }
/*      */     public void updateInfo() {
/* 1969 */       Media localMedia1 = Media.class;
/* 1970 */       SunAlternateMedia localSunAlternateMedia = SunAlternateMedia.class;
/* 1971 */       boolean bool1 = false;
/*      */ 
/* 1973 */       this.cbSize.removeItemListener(this);
/* 1974 */       this.cbSize.removeAllItems();
/* 1975 */       this.cbSource.removeItemListener(this);
/* 1976 */       this.cbSource.removeAllItems();
/* 1977 */       this.cbSource.addItem(getMediaName("auto-select"));
/*      */ 
/* 1979 */       this.sizes.clear();
/* 1980 */       this.sources.clear();
/*      */       Object localObject2;
/*      */       Object localObject3;
/* 1982 */       if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(localMedia1)) {
/* 1983 */         bool1 = true;
/*      */ 
/* 1985 */         Object localObject1 = ServiceDialog.this.psCurrent.getSupportedAttributeValues(localMedia1, ServiceDialog.this.docFlavor, ServiceDialog.this.asCurrent);
/*      */ 
/* 1990 */         if ((localObject1 instanceof Media[])) {
/* 1991 */           localObject2 = (Media[])localObject1;
/*      */ 
/* 1993 */           for (int i = 0; i < localObject2.length; i++) {
/* 1994 */             localObject3 = localObject2[i];
/*      */ 
/* 1996 */             if ((localObject3 instanceof MediaSizeName)) {
/* 1997 */               this.sizes.add(localObject3);
/* 1998 */               this.cbSize.addItem(getMediaName(((Media)localObject3).toString()));
/* 1999 */             } else if ((localObject3 instanceof MediaTray)) {
/* 2000 */               this.sources.add(localObject3);
/* 2001 */               this.cbSource.addItem(getMediaName(((Media)localObject3).toString()));
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 2007 */       boolean bool2 = (bool1) && (this.sizes.size() > 0);
/* 2008 */       this.lblSize.setEnabled(bool2);
/* 2009 */       this.cbSize.setEnabled(bool2);
/*      */ 
/* 2011 */       if (ServiceDialog.this.isAWT) {
/* 2012 */         this.cbSource.setEnabled(false);
/* 2013 */         this.lblSource.setEnabled(false);
/*      */       } else {
/* 2015 */         this.cbSource.setEnabled(bool1);
/*      */       }
/*      */ 
/* 2018 */       if (bool1)
/*      */       {
/* 2020 */         localObject2 = (Media)ServiceDialog.this.asCurrent.get(localMedia1);
/*      */ 
/* 2023 */         Media localMedia2 = (Media)ServiceDialog.this.psCurrent.getDefaultAttributeValue(localMedia1);
/* 2024 */         if ((localMedia2 instanceof MediaSizeName)) {
/* 2025 */           this.cbSize.setSelectedIndex(this.sizes.size() > 0 ? this.sizes.indexOf(localMedia2) : -1);
/*      */         }
/*      */ 
/* 2028 */         if ((localObject2 == null) || (!ServiceDialog.this.psCurrent.isAttributeValueSupported((Attribute)localObject2, ServiceDialog.this.docFlavor, ServiceDialog.this.asCurrent)))
/*      */         {
/* 2032 */           localObject2 = localMedia2;
/*      */ 
/* 2034 */           if ((localObject2 == null) && 
/* 2035 */             (this.sizes.size() > 0)) {
/* 2036 */             localObject2 = (Media)this.sizes.get(0);
/*      */           }
/*      */ 
/* 2039 */           if (localObject2 != null) {
/* 2040 */             ServiceDialog.this.asCurrent.add((Attribute)localObject2);
/*      */           }
/*      */         }
/* 2043 */         if (localObject2 != null) {
/* 2044 */           if ((localObject2 instanceof MediaSizeName)) {
/* 2045 */             localObject3 = (MediaSizeName)localObject2;
/* 2046 */             this.cbSize.setSelectedIndex(this.sizes.indexOf(localObject3));
/* 2047 */           } else if ((localObject2 instanceof MediaTray)) {
/* 2048 */             localObject3 = (MediaTray)localObject2;
/* 2049 */             this.cbSource.setSelectedIndex(this.sources.indexOf(localObject3) + 1);
/*      */           }
/*      */         } else {
/* 2052 */           this.cbSize.setSelectedIndex(this.sizes.size() > 0 ? 0 : -1);
/* 2053 */           this.cbSource.setSelectedIndex(0);
/*      */         }
/*      */ 
/* 2056 */         localObject3 = (SunAlternateMedia)ServiceDialog.this.asCurrent.get(localSunAlternateMedia);
/*      */         MediaTray localMediaTray;
/* 2057 */         if (localObject3 != null) {
/* 2058 */           Media localMedia3 = ((SunAlternateMedia)localObject3).getMedia();
/* 2059 */           if ((localMedia3 instanceof MediaTray)) {
/* 2060 */             localMediaTray = (MediaTray)localMedia3;
/* 2061 */             this.cbSource.setSelectedIndex(this.sources.indexOf(localMediaTray) + 1);
/*      */           }
/*      */         }
/*      */ 
/* 2065 */         int j = this.cbSize.getSelectedIndex();
/* 2066 */         if ((j >= 0) && (j < this.sizes.size())) {
/* 2067 */           ServiceDialog.this.asCurrent.add((MediaSizeName)this.sizes.get(j));
/*      */         }
/*      */ 
/* 2070 */         j = this.cbSource.getSelectedIndex();
/* 2071 */         if ((j >= 1) && (j < this.sources.size() + 1)) {
/* 2072 */           localMediaTray = (MediaTray)this.sources.get(j - 1);
/* 2073 */           if ((localObject2 instanceof MediaTray))
/* 2074 */             ServiceDialog.this.asCurrent.add(localMediaTray);
/*      */           else {
/* 2076 */             ServiceDialog.this.asCurrent.add(new SunAlternateMedia(localMediaTray));
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2082 */       this.cbSize.addItemListener(this);
/* 2083 */       this.cbSource.addItemListener(this);
/*      */     }
/*      */   }
/*      */ 
/*      */   private class OrientationPanel extends JPanel
/*      */     implements ActionListener
/*      */   {
/* 2090 */     private final String strTitle = ServiceDialog.getMsg("border.orientation");
/*      */     private ServiceDialog.IconRadioButton rbPortrait;
/*      */     private ServiceDialog.IconRadioButton rbLandscape;
/*      */     private ServiceDialog.IconRadioButton rbRevPortrait;
/*      */     private ServiceDialog.IconRadioButton rbRevLandscape;
/* 2093 */     private ServiceDialog.MarginsPanel pnlMargins = null;
/*      */ 
/*      */     public OrientationPanel()
/*      */     {
/* 2098 */       GridBagLayout localGridBagLayout = new GridBagLayout();
/* 2099 */       GridBagConstraints localGridBagConstraints = new GridBagConstraints();
/*      */ 
/* 2101 */       setLayout(localGridBagLayout);
/* 2102 */       setBorder(BorderFactory.createTitledBorder(this.strTitle));
/*      */ 
/* 2104 */       localGridBagConstraints.fill = 1;
/* 2105 */       localGridBagConstraints.insets = ServiceDialog.compInsets;
/* 2106 */       localGridBagConstraints.weighty = 1.0D;
/* 2107 */       localGridBagConstraints.gridwidth = 0;
/*      */ 
/* 2109 */       ButtonGroup localButtonGroup = new ButtonGroup();
/* 2110 */       this.rbPortrait = new ServiceDialog.IconRadioButton(ServiceDialog.this, "radiobutton.portrait", "orientPortrait.png", true, localButtonGroup, this);
/*      */ 
/* 2113 */       this.rbPortrait.addActionListener(this);
/* 2114 */       ServiceDialog.addToGB(this.rbPortrait, this, localGridBagLayout, localGridBagConstraints);
/* 2115 */       this.rbLandscape = new ServiceDialog.IconRadioButton(ServiceDialog.this, "radiobutton.landscape", "orientLandscape.png", false, localButtonGroup, this);
/*      */ 
/* 2118 */       this.rbLandscape.addActionListener(this);
/* 2119 */       ServiceDialog.addToGB(this.rbLandscape, this, localGridBagLayout, localGridBagConstraints);
/* 2120 */       this.rbRevPortrait = new ServiceDialog.IconRadioButton(ServiceDialog.this, "radiobutton.revportrait", "orientRevPortrait.png", false, localButtonGroup, this);
/*      */ 
/* 2123 */       this.rbRevPortrait.addActionListener(this);
/* 2124 */       ServiceDialog.addToGB(this.rbRevPortrait, this, localGridBagLayout, localGridBagConstraints);
/* 2125 */       this.rbRevLandscape = new ServiceDialog.IconRadioButton(ServiceDialog.this, "radiobutton.revlandscape", "orientRevLandscape.png", false, localButtonGroup, this);
/*      */ 
/* 2128 */       this.rbRevLandscape.addActionListener(this);
/* 2129 */       ServiceDialog.addToGB(this.rbRevLandscape, this, localGridBagLayout, localGridBagConstraints);
/*      */     }
/*      */ 
/*      */     public void actionPerformed(ActionEvent paramActionEvent) {
/* 2133 */       Object localObject = paramActionEvent.getSource();
/*      */ 
/* 2135 */       if (this.rbPortrait.isSameAs(localObject))
/* 2136 */         ServiceDialog.this.asCurrent.add(OrientationRequested.PORTRAIT);
/* 2137 */       else if (this.rbLandscape.isSameAs(localObject))
/* 2138 */         ServiceDialog.this.asCurrent.add(OrientationRequested.LANDSCAPE);
/* 2139 */       else if (this.rbRevPortrait.isSameAs(localObject))
/* 2140 */         ServiceDialog.this.asCurrent.add(OrientationRequested.REVERSE_PORTRAIT);
/* 2141 */       else if (this.rbRevLandscape.isSameAs(localObject)) {
/* 2142 */         ServiceDialog.this.asCurrent.add(OrientationRequested.REVERSE_LANDSCAPE);
/*      */       }
/*      */ 
/* 2145 */       if (this.pnlMargins != null)
/* 2146 */         this.pnlMargins.updateInfo();
/*      */     }
/*      */ 
/*      */     void addOrientationListener(ServiceDialog.MarginsPanel paramMarginsPanel)
/*      */     {
/* 2152 */       this.pnlMargins = paramMarginsPanel;
/*      */     }
/*      */ 
/*      */     public void updateInfo() {
/* 2156 */       OrientationRequested localOrientationRequested = OrientationRequested.class;
/* 2157 */       boolean bool1 = false;
/* 2158 */       boolean bool2 = false;
/* 2159 */       boolean bool3 = false;
/* 2160 */       boolean bool4 = false;
/*      */       Object localObject2;
/* 2162 */       if (ServiceDialog.this.isAWT) {
/* 2163 */         bool1 = true;
/* 2164 */         bool2 = true;
/*      */       }
/* 2166 */       else if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(localOrientationRequested)) {
/* 2167 */         localObject1 = ServiceDialog.this.psCurrent.getSupportedAttributeValues(localOrientationRequested, ServiceDialog.this.docFlavor, ServiceDialog.this.asCurrent);
/*      */ 
/* 2172 */         if ((localObject1 instanceof OrientationRequested[])) {
/* 2173 */           localObject2 = (OrientationRequested[])localObject1;
/*      */ 
/* 2176 */           for (int i = 0; i < localObject2.length; i++) {
/* 2177 */             Object localObject3 = localObject2[i];
/*      */ 
/* 2179 */             if (localObject3 == OrientationRequested.PORTRAIT)
/* 2180 */               bool1 = true;
/* 2181 */             else if (localObject3 == OrientationRequested.LANDSCAPE)
/* 2182 */               bool2 = true;
/* 2183 */             else if (localObject3 == OrientationRequested.REVERSE_PORTRAIT)
/* 2184 */               bool3 = true;
/* 2185 */             else if (localObject3 == OrientationRequested.REVERSE_LANDSCAPE) {
/* 2186 */               bool4 = true;
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2193 */       this.rbPortrait.setEnabled(bool1);
/* 2194 */       this.rbLandscape.setEnabled(bool2);
/* 2195 */       this.rbRevPortrait.setEnabled(bool3);
/* 2196 */       this.rbRevLandscape.setEnabled(bool4);
/*      */ 
/* 2198 */       Object localObject1 = (OrientationRequested)ServiceDialog.this.asCurrent.get(localOrientationRequested);
/* 2199 */       if ((localObject1 == null) || (!ServiceDialog.this.psCurrent.isAttributeValueSupported((Attribute)localObject1, ServiceDialog.this.docFlavor, ServiceDialog.this.asCurrent)))
/*      */       {
/* 2202 */         localObject1 = (OrientationRequested)ServiceDialog.this.psCurrent.getDefaultAttributeValue(localOrientationRequested);
/*      */ 
/* 2204 */         if ((localObject1 != null) && (!ServiceDialog.this.psCurrent.isAttributeValueSupported((Attribute)localObject1, ServiceDialog.this.docFlavor, ServiceDialog.this.asCurrent)))
/*      */         {
/* 2206 */           localObject1 = null;
/* 2207 */           localObject2 = ServiceDialog.this.psCurrent.getSupportedAttributeValues(localOrientationRequested, ServiceDialog.this.docFlavor, ServiceDialog.this.asCurrent);
/*      */ 
/* 2211 */           if ((localObject2 instanceof OrientationRequested[])) {
/* 2212 */             OrientationRequested[] arrayOfOrientationRequested = (OrientationRequested[])localObject2;
/*      */ 
/* 2214 */             if (arrayOfOrientationRequested.length > 1)
/*      */             {
/* 2216 */               localObject1 = arrayOfOrientationRequested[0];
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/* 2221 */         if (localObject1 == null) {
/* 2222 */           localObject1 = OrientationRequested.PORTRAIT;
/*      */         }
/* 2224 */         ServiceDialog.this.asCurrent.add((Attribute)localObject1);
/*      */       }
/*      */ 
/* 2227 */       if (localObject1 == OrientationRequested.PORTRAIT)
/* 2228 */         this.rbPortrait.setSelected(true);
/* 2229 */       else if (localObject1 == OrientationRequested.LANDSCAPE)
/* 2230 */         this.rbLandscape.setSelected(true);
/* 2231 */       else if (localObject1 == OrientationRequested.REVERSE_PORTRAIT)
/* 2232 */         this.rbRevPortrait.setSelected(true);
/*      */       else
/* 2234 */         this.rbRevLandscape.setSelected(true);
/*      */     }
/*      */   }
/*      */ 
/*      */   private class PageSetupPanel extends JPanel
/*      */   {
/*      */     private ServiceDialog.MediaPanel pnlMedia;
/*      */     private ServiceDialog.OrientationPanel pnlOrientation;
/*      */     private ServiceDialog.MarginsPanel pnlMargins;
/*      */ 
/*      */     public PageSetupPanel()
/*      */     {
/* 1291 */       GridBagLayout localGridBagLayout = new GridBagLayout();
/* 1292 */       GridBagConstraints localGridBagConstraints = new GridBagConstraints();
/*      */ 
/* 1294 */       setLayout(localGridBagLayout);
/*      */ 
/* 1296 */       localGridBagConstraints.fill = 1;
/* 1297 */       localGridBagConstraints.insets = ServiceDialog.panelInsets;
/* 1298 */       localGridBagConstraints.weightx = 1.0D;
/* 1299 */       localGridBagConstraints.weighty = 1.0D;
/*      */ 
/* 1301 */       localGridBagConstraints.gridwidth = 0;
/* 1302 */       this.pnlMedia = new ServiceDialog.MediaPanel(ServiceDialog.this);
/* 1303 */       ServiceDialog.addToGB(this.pnlMedia, this, localGridBagLayout, localGridBagConstraints);
/*      */ 
/* 1305 */       this.pnlOrientation = new ServiceDialog.OrientationPanel(ServiceDialog.this);
/* 1306 */       localGridBagConstraints.gridwidth = -1;
/* 1307 */       ServiceDialog.addToGB(this.pnlOrientation, this, localGridBagLayout, localGridBagConstraints);
/*      */ 
/* 1309 */       this.pnlMargins = new ServiceDialog.MarginsPanel(ServiceDialog.this);
/* 1310 */       this.pnlOrientation.addOrientationListener(this.pnlMargins);
/* 1311 */       this.pnlMedia.addMediaListener(this.pnlMargins);
/* 1312 */       localGridBagConstraints.gridwidth = 0;
/* 1313 */       ServiceDialog.addToGB(this.pnlMargins, this, localGridBagLayout, localGridBagConstraints);
/*      */     }
/*      */ 
/*      */     public void updateInfo() {
/* 1317 */       this.pnlMedia.updateInfo();
/* 1318 */       this.pnlOrientation.updateInfo();
/* 1319 */       this.pnlMargins.updateInfo();
/*      */     }
/*      */   }
/*      */ 
/*      */   private class PrintRangePanel extends JPanel
/*      */     implements ActionListener, FocusListener
/*      */   {
/*  940 */     private final String strTitle = ServiceDialog.getMsg("border.printrange");
/*  941 */     private final PageRanges prAll = new PageRanges(1, 2147483647);
/*      */     private JRadioButton rbAll;
/*      */     private JRadioButton rbPages;
/*      */     private JRadioButton rbSelect;
/*      */     private JFormattedTextField tfRangeFrom;
/*      */     private JFormattedTextField tfRangeTo;
/*      */     private JLabel lblRangeTo;
/*      */     private boolean prSupported;
/*      */ 
/*      */     public PrintRangePanel()
/*      */     {
/*  950 */       GridBagLayout localGridBagLayout = new GridBagLayout();
/*  951 */       GridBagConstraints localGridBagConstraints = new GridBagConstraints();
/*      */ 
/*  953 */       setLayout(localGridBagLayout);
/*  954 */       setBorder(BorderFactory.createTitledBorder(this.strTitle));
/*      */ 
/*  956 */       localGridBagConstraints.fill = 1;
/*  957 */       localGridBagConstraints.insets = ServiceDialog.compInsets;
/*  958 */       localGridBagConstraints.gridwidth = 0;
/*      */ 
/*  960 */       ButtonGroup localButtonGroup = new ButtonGroup();
/*  961 */       JPanel localJPanel1 = new JPanel(new FlowLayout(3));
/*  962 */       this.rbAll = ServiceDialog.createRadioButton("radiobutton.rangeall", this);
/*  963 */       this.rbAll.setSelected(true);
/*  964 */       localButtonGroup.add(this.rbAll);
/*  965 */       localJPanel1.add(this.rbAll);
/*  966 */       ServiceDialog.addToGB(localJPanel1, this, localGridBagLayout, localGridBagConstraints);
/*      */ 
/*  981 */       JPanel localJPanel2 = new JPanel(new FlowLayout(3));
/*  982 */       this.rbPages = ServiceDialog.createRadioButton("radiobutton.rangepages", this);
/*  983 */       localButtonGroup.add(this.rbPages);
/*  984 */       localJPanel2.add(this.rbPages);
/*  985 */       DecimalFormat localDecimalFormat = new DecimalFormat("####0");
/*  986 */       localDecimalFormat.setMinimumFractionDigits(0);
/*  987 */       localDecimalFormat.setMaximumFractionDigits(0);
/*  988 */       localDecimalFormat.setMinimumIntegerDigits(0);
/*  989 */       localDecimalFormat.setMaximumIntegerDigits(5);
/*  990 */       localDecimalFormat.setParseIntegerOnly(true);
/*  991 */       localDecimalFormat.setDecimalSeparatorAlwaysShown(false);
/*  992 */       NumberFormatter localNumberFormatter1 = new NumberFormatter(localDecimalFormat);
/*  993 */       localNumberFormatter1.setMinimum(new Integer(1));
/*  994 */       localNumberFormatter1.setMaximum(new Integer(2147483647));
/*  995 */       localNumberFormatter1.setAllowsInvalid(true);
/*  996 */       localNumberFormatter1.setCommitsOnValidEdit(true);
/*  997 */       this.tfRangeFrom = new JFormattedTextField(localNumberFormatter1);
/*  998 */       this.tfRangeFrom.setColumns(4);
/*  999 */       this.tfRangeFrom.setEnabled(false);
/* 1000 */       this.tfRangeFrom.addActionListener(this);
/* 1001 */       this.tfRangeFrom.addFocusListener(this);
/* 1002 */       this.tfRangeFrom.setFocusLostBehavior(3);
/*      */ 
/* 1004 */       this.tfRangeFrom.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("radiobutton.rangepages"));
/*      */ 
/* 1006 */       localJPanel2.add(this.tfRangeFrom);
/* 1007 */       this.lblRangeTo = new JLabel(ServiceDialog.getMsg("label.rangeto"));
/* 1008 */       this.lblRangeTo.setEnabled(false);
/* 1009 */       localJPanel2.add(this.lblRangeTo);
/*      */       NumberFormatter localNumberFormatter2;
/*      */       try
/*      */       {
/* 1012 */         localNumberFormatter2 = (NumberFormatter)localNumberFormatter1.clone();
/*      */       } catch (CloneNotSupportedException localCloneNotSupportedException) {
/* 1014 */         localNumberFormatter2 = new NumberFormatter();
/*      */       }
/* 1016 */       this.tfRangeTo = new JFormattedTextField(localNumberFormatter2);
/* 1017 */       this.tfRangeTo.setColumns(4);
/* 1018 */       this.tfRangeTo.setEnabled(false);
/* 1019 */       this.tfRangeTo.addFocusListener(this);
/* 1020 */       this.tfRangeTo.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("label.rangeto"));
/*      */ 
/* 1022 */       localJPanel2.add(this.tfRangeTo);
/* 1023 */       ServiceDialog.addToGB(localJPanel2, this, localGridBagLayout, localGridBagConstraints);
/*      */     }
/*      */ 
/*      */     public void actionPerformed(ActionEvent paramActionEvent) {
/* 1027 */       Object localObject = paramActionEvent.getSource();
/* 1028 */       SunPageSelection localSunPageSelection = SunPageSelection.ALL;
/*      */ 
/* 1030 */       setupRangeWidgets();
/*      */ 
/* 1032 */       if (localObject == this.rbAll) {
/* 1033 */         ServiceDialog.this.asCurrent.add(this.prAll);
/* 1034 */       } else if (localObject == this.rbSelect) {
/* 1035 */         localSunPageSelection = SunPageSelection.SELECTION;
/* 1036 */       } else if ((localObject == this.rbPages) || (localObject == this.tfRangeFrom) || (localObject == this.tfRangeTo))
/*      */       {
/* 1039 */         updateRangeAttribute();
/* 1040 */         localSunPageSelection = SunPageSelection.RANGE;
/*      */       }
/*      */ 
/* 1043 */       if (ServiceDialog.this.isAWT)
/* 1044 */         ServiceDialog.this.asCurrent.add(localSunPageSelection);
/*      */     }
/*      */ 
/*      */     public void focusLost(FocusEvent paramFocusEvent)
/*      */     {
/* 1049 */       Object localObject = paramFocusEvent.getSource();
/*      */ 
/* 1051 */       if ((localObject == this.tfRangeFrom) || (localObject == this.tfRangeTo))
/* 1052 */         updateRangeAttribute();
/*      */     }
/*      */ 
/*      */     public void focusGained(FocusEvent paramFocusEvent) {
/*      */     }
/*      */ 
/*      */     private void setupRangeWidgets() {
/* 1059 */       boolean bool = (this.rbPages.isSelected()) && (this.prSupported);
/* 1060 */       this.tfRangeFrom.setEnabled(bool);
/* 1061 */       this.tfRangeTo.setEnabled(bool);
/* 1062 */       this.lblRangeTo.setEnabled(bool);
/*      */     }
/*      */ 
/*      */     private void updateRangeAttribute() {
/* 1066 */       String str1 = this.tfRangeFrom.getText();
/* 1067 */       String str2 = this.tfRangeTo.getText();
/*      */       int i;
/*      */       try
/*      */       {
/* 1073 */         i = Integer.parseInt(str1);
/*      */       } catch (NumberFormatException localNumberFormatException1) {
/* 1075 */         i = 1;
/*      */       }
/*      */       int j;
/*      */       try {
/* 1079 */         j = Integer.parseInt(str2);
/*      */       } catch (NumberFormatException localNumberFormatException2) {
/* 1081 */         j = i;
/*      */       }
/*      */ 
/* 1084 */       if (i < 1) {
/* 1085 */         i = 1;
/* 1086 */         this.tfRangeFrom.setValue(new Integer(1));
/*      */       }
/*      */ 
/* 1089 */       if (j < i) {
/* 1090 */         j = i;
/* 1091 */         this.tfRangeTo.setValue(new Integer(i));
/*      */       }
/*      */ 
/* 1094 */       PageRanges localPageRanges = new PageRanges(i, j);
/* 1095 */       ServiceDialog.this.asCurrent.add(localPageRanges);
/*      */     }
/*      */ 
/*      */     public void updateInfo() {
/* 1099 */       PageRanges localPageRanges1 = PageRanges.class;
/* 1100 */       this.prSupported = false;
/*      */ 
/* 1102 */       if ((ServiceDialog.this.psCurrent.isAttributeCategorySupported(localPageRanges1)) || (ServiceDialog.this.isAWT))
/*      */       {
/* 1104 */         this.prSupported = true;
/*      */       }
/*      */ 
/* 1107 */       SunPageSelection localSunPageSelection = SunPageSelection.ALL;
/* 1108 */       int i = 1;
/* 1109 */       int j = 1;
/*      */ 
/* 1111 */       PageRanges localPageRanges2 = (PageRanges)ServiceDialog.this.asCurrent.get(localPageRanges1);
/* 1112 */       if ((localPageRanges2 != null) && 
/* 1113 */         (!localPageRanges2.equals(this.prAll))) {
/* 1114 */         localSunPageSelection = SunPageSelection.RANGE;
/*      */ 
/* 1116 */         int[][] arrayOfInt = localPageRanges2.getMembers();
/* 1117 */         if ((arrayOfInt.length > 0) && (arrayOfInt[0].length > 1))
/*      */         {
/* 1119 */           i = arrayOfInt[0][0];
/* 1120 */           j = arrayOfInt[0][1];
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1125 */       if (ServiceDialog.this.isAWT) {
/* 1126 */         localSunPageSelection = (SunPageSelection)ServiceDialog.this.asCurrent.get(SunPageSelection.class);
/*      */       }
/*      */ 
/* 1130 */       if (localSunPageSelection == SunPageSelection.ALL)
/* 1131 */         this.rbAll.setSelected(true);
/* 1132 */       else if (localSunPageSelection != SunPageSelection.SELECTION)
/*      */       {
/* 1139 */         this.rbPages.setSelected(true);
/*      */       }
/* 1141 */       this.tfRangeFrom.setValue(new Integer(i));
/* 1142 */       this.tfRangeTo.setValue(new Integer(j));
/* 1143 */       this.rbAll.setEnabled(this.prSupported);
/* 1144 */       this.rbPages.setEnabled(this.prSupported);
/* 1145 */       setupRangeWidgets();
/*      */     }
/*      */   }
/*      */ 
/*      */   private class PrintServicePanel extends JPanel
/*      */     implements ActionListener, ItemListener, PopupMenuListener
/*      */   {
/*  706 */     private final String strTitle = ServiceDialog.getMsg("border.printservice");
/*      */     private FilePermission printToFilePermission;
/*      */     private JButton btnProperties;
/*      */     private JCheckBox cbPrintToFile;
/*      */     private JComboBox cbName;
/*      */     private JLabel lblType;
/*      */     private JLabel lblStatus;
/*      */     private JLabel lblInfo;
/*      */     private ServiceUIFactory uiFactory;
/*  713 */     private boolean changedService = false;
/*      */     private boolean filePermission;
/*      */ 
/*      */     public PrintServicePanel()
/*      */     {
/*  719 */       this.uiFactory = ServiceDialog.this.psCurrent.getServiceUIFactory();
/*      */ 
/*  721 */       GridBagLayout localGridBagLayout = new GridBagLayout();
/*  722 */       GridBagConstraints localGridBagConstraints = new GridBagConstraints();
/*      */ 
/*  724 */       setLayout(localGridBagLayout);
/*  725 */       setBorder(BorderFactory.createTitledBorder(this.strTitle));
/*      */ 
/*  727 */       String[] arrayOfString = new String[ServiceDialog.this.services.length];
/*  728 */       for (int i = 0; i < arrayOfString.length; i++) {
/*  729 */         arrayOfString[i] = ServiceDialog.this.services[i].getName();
/*      */       }
/*  731 */       this.cbName = new JComboBox(arrayOfString);
/*  732 */       this.cbName.setSelectedIndex(ServiceDialog.this.defaultServiceIndex);
/*  733 */       this.cbName.addItemListener(this);
/*  734 */       this.cbName.addPopupMenuListener(this);
/*      */ 
/*  736 */       localGridBagConstraints.fill = 1;
/*  737 */       localGridBagConstraints.insets = ServiceDialog.compInsets;
/*      */ 
/*  739 */       localGridBagConstraints.weightx = 0.0D;
/*  740 */       JLabel localJLabel = new JLabel(ServiceDialog.getMsg("label.psname"), 11);
/*  741 */       localJLabel.setDisplayedMnemonic(ServiceDialog.getMnemonic("label.psname"));
/*  742 */       localJLabel.setLabelFor(this.cbName);
/*  743 */       ServiceDialog.addToGB(localJLabel, this, localGridBagLayout, localGridBagConstraints);
/*  744 */       localGridBagConstraints.weightx = 1.0D;
/*  745 */       localGridBagConstraints.gridwidth = -1;
/*  746 */       ServiceDialog.addToGB(this.cbName, this, localGridBagLayout, localGridBagConstraints);
/*  747 */       localGridBagConstraints.weightx = 0.0D;
/*  748 */       localGridBagConstraints.gridwidth = 0;
/*  749 */       this.btnProperties = ServiceDialog.createButton("button.properties", this);
/*  750 */       ServiceDialog.addToGB(this.btnProperties, this, localGridBagLayout, localGridBagConstraints);
/*      */ 
/*  752 */       localGridBagConstraints.weighty = 1.0D;
/*  753 */       this.lblStatus = addLabel(ServiceDialog.getMsg("label.status"), localGridBagLayout, localGridBagConstraints);
/*  754 */       this.lblStatus.setLabelFor(null);
/*      */ 
/*  756 */       this.lblType = addLabel(ServiceDialog.getMsg("label.pstype"), localGridBagLayout, localGridBagConstraints);
/*  757 */       this.lblType.setLabelFor(null);
/*      */ 
/*  759 */       localGridBagConstraints.gridwidth = 1;
/*  760 */       ServiceDialog.addToGB(new JLabel(ServiceDialog.getMsg("label.info"), 11), this, localGridBagLayout, localGridBagConstraints);
/*      */ 
/*  762 */       localGridBagConstraints.gridwidth = -1;
/*  763 */       this.lblInfo = new JLabel();
/*  764 */       this.lblInfo.setLabelFor(null);
/*      */ 
/*  766 */       ServiceDialog.addToGB(this.lblInfo, this, localGridBagLayout, localGridBagConstraints);
/*      */ 
/*  768 */       localGridBagConstraints.gridwidth = 0;
/*  769 */       this.cbPrintToFile = ServiceDialog.createCheckBox("checkbox.printtofile", this);
/*  770 */       ServiceDialog.addToGB(this.cbPrintToFile, this, localGridBagLayout, localGridBagConstraints);
/*      */ 
/*  772 */       this.filePermission = allowedToPrintToFile();
/*      */     }
/*      */ 
/*      */     public boolean isPrintToFileSelected() {
/*  776 */       return this.cbPrintToFile.isSelected();
/*      */     }
/*      */ 
/*      */     private JLabel addLabel(String paramString, GridBagLayout paramGridBagLayout, GridBagConstraints paramGridBagConstraints)
/*      */     {
/*  782 */       paramGridBagConstraints.gridwidth = 1;
/*  783 */       ServiceDialog.addToGB(new JLabel(paramString, 11), this, paramGridBagLayout, paramGridBagConstraints);
/*      */ 
/*  785 */       paramGridBagConstraints.gridwidth = 0;
/*  786 */       JLabel localJLabel = new JLabel();
/*  787 */       ServiceDialog.addToGB(localJLabel, this, paramGridBagLayout, paramGridBagConstraints);
/*      */ 
/*  789 */       return localJLabel;
/*      */     }
/*      */ 
/*      */     public void actionPerformed(ActionEvent paramActionEvent) {
/*  793 */       Object localObject = paramActionEvent.getSource();
/*      */ 
/*  795 */       if ((localObject == this.btnProperties) && 
/*  796 */         (this.uiFactory != null)) {
/*  797 */         JDialog localJDialog = (JDialog)this.uiFactory.getUI(3, "javax.swing.JDialog");
/*      */ 
/*  801 */         if (localJDialog != null) {
/*  802 */           localJDialog.show();
/*      */         }
/*      */         else
/*      */         {
/*  806 */           this.btnProperties.setEnabled(false);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     public void itemStateChanged(ItemEvent paramItemEvent)
/*      */     {
/*  813 */       if (paramItemEvent.getStateChange() == 1) {
/*  814 */         int i = this.cbName.getSelectedIndex();
/*      */ 
/*  816 */         if ((i >= 0) && (i < ServiceDialog.this.services.length) && 
/*  817 */           (!ServiceDialog.this.services[i].equals(ServiceDialog.this.psCurrent))) {
/*  818 */           ServiceDialog.this.psCurrent = ServiceDialog.this.services[i];
/*  819 */           this.uiFactory = ServiceDialog.this.psCurrent.getServiceUIFactory();
/*  820 */           this.changedService = true;
/*      */ 
/*  822 */           Destination localDestination = (Destination)ServiceDialog.this.asOriginal.get(Destination.class);
/*      */ 
/*  825 */           if (((localDestination != null) || (isPrintToFileSelected())) && (ServiceDialog.this.psCurrent.isAttributeCategorySupported(Destination.class)))
/*      */           {
/*  829 */             if (localDestination != null) {
/*  830 */               ServiceDialog.this.asCurrent.add(localDestination);
/*      */             } else {
/*  832 */               localDestination = (Destination)ServiceDialog.this.psCurrent.getDefaultAttributeValue(Destination.class);
/*      */ 
/*  838 */               if (localDestination == null) {
/*      */                 try {
/*  840 */                   localDestination = new Destination(new URI("file:out.prn"));
/*      */                 }
/*      */                 catch (URISyntaxException localURISyntaxException)
/*      */                 {
/*      */                 }
/*      */               }
/*  846 */               if (localDestination != null)
/*  847 */                 ServiceDialog.this.asCurrent.add(localDestination);
/*      */             }
/*      */           }
/*      */           else
/*  851 */             ServiceDialog.this.asCurrent.remove(Destination.class);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     public void popupMenuWillBecomeVisible(PopupMenuEvent paramPopupMenuEvent)
/*      */     {
/*  859 */       this.changedService = false;
/*      */     }
/*      */ 
/*      */     public void popupMenuWillBecomeInvisible(PopupMenuEvent paramPopupMenuEvent) {
/*  863 */       if (this.changedService) {
/*  864 */         this.changedService = false;
/*  865 */         ServiceDialog.this.updatePanels();
/*      */       }
/*      */     }
/*      */ 
/*      */     public void popupMenuCanceled(PopupMenuEvent paramPopupMenuEvent)
/*      */     {
/*      */     }
/*      */ 
/*      */     private boolean allowedToPrintToFile()
/*      */     {
/*      */       try
/*      */       {
/*  877 */         throwPrintToFile();
/*  878 */         return true; } catch (SecurityException localSecurityException) {
/*      */       }
/*  880 */       return false;
/*      */     }
/*      */ 
/*      */     private void throwPrintToFile()
/*      */     {
/*  890 */       SecurityManager localSecurityManager = System.getSecurityManager();
/*  891 */       if (localSecurityManager != null) {
/*  892 */         if (this.printToFilePermission == null) {
/*  893 */           this.printToFilePermission = new FilePermission("<<ALL FILES>>", "read,write");
/*      */         }
/*      */ 
/*  896 */         localSecurityManager.checkPermission(this.printToFilePermission);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void updateInfo() {
/*  901 */       Destination localDestination1 = Destination.class;
/*  902 */       int i = 0;
/*  903 */       int j = 0;
/*  904 */       int k = this.filePermission ? allowedToPrintToFile() : 0;
/*      */ 
/*  908 */       if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(localDestination1)) {
/*  909 */         i = 1;
/*      */       }
/*  911 */       Destination localDestination2 = (Destination)ServiceDialog.this.asCurrent.get(localDestination1);
/*  912 */       if (localDestination2 != null) {
/*  913 */         j = 1;
/*      */       }
/*  915 */       this.cbPrintToFile.setEnabled((i != 0) && (k != 0));
/*  916 */       this.cbPrintToFile.setSelected((j != 0) && (k != 0) && (i != 0));
/*      */ 
/*  920 */       PrintServiceAttribute localPrintServiceAttribute1 = ServiceDialog.this.psCurrent.getAttribute(PrinterMakeAndModel.class);
/*  921 */       if (localPrintServiceAttribute1 != null) {
/*  922 */         this.lblType.setText(localPrintServiceAttribute1.toString());
/*      */       }
/*  924 */       PrintServiceAttribute localPrintServiceAttribute2 = ServiceDialog.this.psCurrent.getAttribute(PrinterIsAcceptingJobs.class);
/*      */ 
/*  926 */       if (localPrintServiceAttribute2 != null) {
/*  927 */         this.lblStatus.setText(ServiceDialog.getMsg(localPrintServiceAttribute2.toString()));
/*      */       }
/*  929 */       PrintServiceAttribute localPrintServiceAttribute3 = ServiceDialog.this.psCurrent.getAttribute(PrinterInfo.class);
/*  930 */       if (localPrintServiceAttribute3 != null) {
/*  931 */         this.lblInfo.setText(localPrintServiceAttribute3.toString());
/*      */       }
/*  933 */       this.btnProperties.setEnabled(this.uiFactory != null);
/*      */     }
/*      */   }
/*      */ 
/*      */   private class QualityPanel extends JPanel
/*      */     implements ActionListener
/*      */   {
/* 2384 */     private final String strTitle = ServiceDialog.getMsg("border.quality");
/*      */     private JRadioButton rbDraft;
/*      */     private JRadioButton rbNormal;
/*      */     private JRadioButton rbHigh;
/*      */ 
/*      */     public QualityPanel()
/*      */     {
/* 2390 */       GridBagLayout localGridBagLayout = new GridBagLayout();
/* 2391 */       GridBagConstraints localGridBagConstraints = new GridBagConstraints();
/*      */ 
/* 2393 */       setLayout(localGridBagLayout);
/* 2394 */       setBorder(BorderFactory.createTitledBorder(this.strTitle));
/*      */ 
/* 2396 */       localGridBagConstraints.fill = 1;
/* 2397 */       localGridBagConstraints.gridwidth = 0;
/* 2398 */       localGridBagConstraints.weighty = 1.0D;
/*      */ 
/* 2400 */       ButtonGroup localButtonGroup = new ButtonGroup();
/* 2401 */       this.rbDraft = ServiceDialog.createRadioButton("radiobutton.draftq", this);
/* 2402 */       localButtonGroup.add(this.rbDraft);
/* 2403 */       ServiceDialog.addToGB(this.rbDraft, this, localGridBagLayout, localGridBagConstraints);
/* 2404 */       this.rbNormal = ServiceDialog.createRadioButton("radiobutton.normalq", this);
/* 2405 */       this.rbNormal.setSelected(true);
/* 2406 */       localButtonGroup.add(this.rbNormal);
/* 2407 */       ServiceDialog.addToGB(this.rbNormal, this, localGridBagLayout, localGridBagConstraints);
/* 2408 */       this.rbHigh = ServiceDialog.createRadioButton("radiobutton.highq", this);
/* 2409 */       localButtonGroup.add(this.rbHigh);
/* 2410 */       ServiceDialog.addToGB(this.rbHigh, this, localGridBagLayout, localGridBagConstraints);
/*      */     }
/*      */ 
/*      */     public void actionPerformed(ActionEvent paramActionEvent) {
/* 2414 */       Object localObject = paramActionEvent.getSource();
/*      */ 
/* 2416 */       if (localObject == this.rbDraft)
/* 2417 */         ServiceDialog.this.asCurrent.add(PrintQuality.DRAFT);
/* 2418 */       else if (localObject == this.rbNormal)
/* 2419 */         ServiceDialog.this.asCurrent.add(PrintQuality.NORMAL);
/* 2420 */       else if (localObject == this.rbHigh)
/* 2421 */         ServiceDialog.this.asCurrent.add(PrintQuality.HIGH);
/*      */     }
/*      */ 
/*      */     public void updateInfo()
/*      */     {
/* 2426 */       PrintQuality localPrintQuality1 = PrintQuality.class;
/* 2427 */       boolean bool1 = false;
/* 2428 */       boolean bool2 = false;
/* 2429 */       boolean bool3 = false;
/*      */ 
/* 2431 */       if (ServiceDialog.this.isAWT) {
/* 2432 */         bool1 = true;
/* 2433 */         bool2 = true;
/* 2434 */         bool3 = true;
/*      */       }
/* 2436 */       else if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(localPrintQuality1)) {
/* 2437 */         localObject = ServiceDialog.this.psCurrent.getSupportedAttributeValues(localPrintQuality1, ServiceDialog.this.docFlavor, ServiceDialog.this.asCurrent);
/*      */ 
/* 2442 */         if ((localObject instanceof PrintQuality[])) {
/* 2443 */           PrintQuality[] arrayOfPrintQuality = (PrintQuality[])localObject;
/*      */ 
/* 2445 */           for (int i = 0; i < arrayOfPrintQuality.length; i++) {
/* 2446 */             PrintQuality localPrintQuality2 = arrayOfPrintQuality[i];
/*      */ 
/* 2448 */             if (localPrintQuality2 == PrintQuality.DRAFT)
/* 2449 */               bool1 = true;
/* 2450 */             else if (localPrintQuality2 == PrintQuality.NORMAL)
/* 2451 */               bool2 = true;
/* 2452 */             else if (localPrintQuality2 == PrintQuality.HIGH) {
/* 2453 */               bool3 = true;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 2459 */       this.rbDraft.setEnabled(bool1);
/* 2460 */       this.rbNormal.setEnabled(bool2);
/* 2461 */       this.rbHigh.setEnabled(bool3);
/*      */ 
/* 2463 */       Object localObject = (PrintQuality)ServiceDialog.this.asCurrent.get(localPrintQuality1);
/* 2464 */       if (localObject == null) {
/* 2465 */         localObject = (PrintQuality)ServiceDialog.this.psCurrent.getDefaultAttributeValue(localPrintQuality1);
/* 2466 */         if (localObject == null) {
/* 2467 */           localObject = PrintQuality.NORMAL;
/*      */         }
/*      */       }
/*      */ 
/* 2471 */       if (localObject == PrintQuality.DRAFT)
/* 2472 */         this.rbDraft.setSelected(true);
/* 2473 */       else if (localObject == PrintQuality.NORMAL)
/* 2474 */         this.rbNormal.setSelected(true);
/*      */       else
/* 2476 */         this.rbHigh.setSelected(true);
/*      */     }
/*      */   }
/*      */ 
/*      */   private class SidesPanel extends JPanel implements ActionListener
/*      */   {
/* 2485 */     private final String strTitle = ServiceDialog.getMsg("border.sides");
/*      */     private ServiceDialog.IconRadioButton rbOneSide;
/*      */     private ServiceDialog.IconRadioButton rbTumble;
/*      */     private ServiceDialog.IconRadioButton rbDuplex;
/*      */ 
/*      */     public SidesPanel() {
/* 2491 */       GridBagLayout localGridBagLayout = new GridBagLayout();
/* 2492 */       GridBagConstraints localGridBagConstraints = new GridBagConstraints();
/*      */ 
/* 2494 */       setLayout(localGridBagLayout);
/* 2495 */       setBorder(BorderFactory.createTitledBorder(this.strTitle));
/*      */ 
/* 2497 */       localGridBagConstraints.fill = 1;
/* 2498 */       localGridBagConstraints.insets = ServiceDialog.compInsets;
/* 2499 */       localGridBagConstraints.weighty = 1.0D;
/* 2500 */       localGridBagConstraints.gridwidth = 0;
/*      */ 
/* 2502 */       ButtonGroup localButtonGroup = new ButtonGroup();
/* 2503 */       this.rbOneSide = new ServiceDialog.IconRadioButton(ServiceDialog.this, "radiobutton.oneside", "oneside.png", true, localButtonGroup, this);
/*      */ 
/* 2506 */       this.rbOneSide.addActionListener(this);
/* 2507 */       ServiceDialog.addToGB(this.rbOneSide, this, localGridBagLayout, localGridBagConstraints);
/* 2508 */       this.rbTumble = new ServiceDialog.IconRadioButton(ServiceDialog.this, "radiobutton.tumble", "tumble.png", false, localButtonGroup, this);
/*      */ 
/* 2511 */       this.rbTumble.addActionListener(this);
/* 2512 */       ServiceDialog.addToGB(this.rbTumble, this, localGridBagLayout, localGridBagConstraints);
/* 2513 */       this.rbDuplex = new ServiceDialog.IconRadioButton(ServiceDialog.this, "radiobutton.duplex", "duplex.png", false, localButtonGroup, this);
/*      */ 
/* 2516 */       this.rbDuplex.addActionListener(this);
/* 2517 */       localGridBagConstraints.gridwidth = 0;
/* 2518 */       ServiceDialog.addToGB(this.rbDuplex, this, localGridBagLayout, localGridBagConstraints);
/*      */     }
/*      */ 
/*      */     public void actionPerformed(ActionEvent paramActionEvent) {
/* 2522 */       Object localObject = paramActionEvent.getSource();
/*      */ 
/* 2524 */       if (this.rbOneSide.isSameAs(localObject))
/* 2525 */         ServiceDialog.this.asCurrent.add(Sides.ONE_SIDED);
/* 2526 */       else if (this.rbTumble.isSameAs(localObject))
/* 2527 */         ServiceDialog.this.asCurrent.add(Sides.TUMBLE);
/* 2528 */       else if (this.rbDuplex.isSameAs(localObject))
/* 2529 */         ServiceDialog.this.asCurrent.add(Sides.DUPLEX);
/*      */     }
/*      */ 
/*      */     public void updateInfo()
/*      */     {
/* 2534 */       Sides localSides1 = Sides.class;
/* 2535 */       boolean bool1 = false;
/* 2536 */       boolean bool2 = false;
/* 2537 */       boolean bool3 = false;
/*      */ 
/* 2539 */       if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(localSides1)) {
/* 2540 */         localObject = ServiceDialog.this.psCurrent.getSupportedAttributeValues(localSides1, ServiceDialog.this.docFlavor, ServiceDialog.this.asCurrent);
/*      */ 
/* 2545 */         if ((localObject instanceof Sides[])) {
/* 2546 */           Sides[] arrayOfSides = (Sides[])localObject;
/*      */ 
/* 2548 */           for (int i = 0; i < arrayOfSides.length; i++) {
/* 2549 */             Sides localSides2 = arrayOfSides[i];
/*      */ 
/* 2551 */             if (localSides2 == Sides.ONE_SIDED)
/* 2552 */               bool1 = true;
/* 2553 */             else if (localSides2 == Sides.TUMBLE)
/* 2554 */               bool2 = true;
/* 2555 */             else if (localSides2 == Sides.DUPLEX) {
/* 2556 */               bool3 = true;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 2561 */       this.rbOneSide.setEnabled(bool1);
/* 2562 */       this.rbTumble.setEnabled(bool2);
/* 2563 */       this.rbDuplex.setEnabled(bool3);
/*      */ 
/* 2565 */       Object localObject = (Sides)ServiceDialog.this.asCurrent.get(localSides1);
/* 2566 */       if (localObject == null) {
/* 2567 */         localObject = (Sides)ServiceDialog.this.psCurrent.getDefaultAttributeValue(localSides1);
/* 2568 */         if (localObject == null) {
/* 2569 */           localObject = Sides.ONE_SIDED;
/*      */         }
/*      */       }
/*      */ 
/* 2573 */       if (localObject == Sides.ONE_SIDED)
/* 2574 */         this.rbOneSide.setSelected(true);
/* 2575 */       else if (localObject == Sides.TUMBLE)
/* 2576 */         this.rbTumble.setSelected(true);
/*      */       else
/* 2578 */         this.rbDuplex.setSelected(true);
/*      */     }
/*      */   }
/*      */ 
/*      */   private class ValidatingFileChooser extends JFileChooser
/*      */   {
/*      */     private ValidatingFileChooser()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void approveSelection()
/*      */     {
/* 2826 */       File localFile1 = getSelectedFile();
/*      */       boolean bool;
/*      */       try
/*      */       {
/* 2830 */         bool = localFile1.exists();
/*      */       } catch (SecurityException localSecurityException1) {
/* 2832 */         bool = false;
/*      */       }
/*      */ 
/* 2835 */       if (bool)
/*      */       {
/* 2837 */         int i = JOptionPane.showConfirmDialog(this, ServiceDialog.getMsg("dialog.overwrite"), ServiceDialog.getMsg("dialog.owtitle"), 0);
/*      */ 
/* 2841 */         if (i != 0) {
/* 2842 */           return;
/*      */         }
/*      */       }
/*      */       try
/*      */       {
/* 2847 */         if (localFile1.createNewFile())
/* 2848 */           localFile1.delete();
/*      */       }
/*      */       catch (IOException localIOException) {
/* 2851 */         JOptionPane.showMessageDialog(this, ServiceDialog.getMsg("dialog.writeerror") + " " + localFile1, ServiceDialog.getMsg("dialog.owtitle"), 2);
/*      */ 
/* 2855 */         return;
/*      */       }
/*      */       catch (SecurityException localSecurityException2)
/*      */       {
/*      */       }
/*      */ 
/* 2862 */       File localFile2 = localFile1.getParentFile();
/* 2863 */       if (((localFile1.exists()) && ((!localFile1.isFile()) || (!localFile1.canWrite()))) || ((localFile2 != null) && ((!localFile2.exists()) || ((localFile2.exists()) && (!localFile2.canWrite())))))
/*      */       {
/* 2867 */         JOptionPane.showMessageDialog(this, ServiceDialog.getMsg("dialog.writeerror") + " " + localFile1, ServiceDialog.getMsg("dialog.owtitle"), 2);
/*      */ 
/* 2871 */         return;
/*      */       }
/*      */ 
/* 2874 */       super.approveSelection();
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.print.ServiceDialog
 * JD-Core Version:    0.6.2
 */