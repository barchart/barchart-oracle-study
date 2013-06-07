/*      */ package javax.swing.plaf.basic;
/*      */ 
/*      */ import java.awt.Color;
/*      */ import java.awt.Component;
/*      */ import java.awt.Component.BaselineResizeBehavior;
/*      */ import java.awt.Container;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.EventQueue;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.Insets;
/*      */ import java.awt.LayoutManager;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ import java.awt.event.FocusEvent;
/*      */ import java.awt.event.FocusListener;
/*      */ import java.awt.event.ItemEvent;
/*      */ import java.awt.event.ItemListener;
/*      */ import java.awt.event.KeyAdapter;
/*      */ import java.awt.event.KeyEvent;
/*      */ import java.awt.event.KeyListener;
/*      */ import java.awt.event.MouseListener;
/*      */ import java.awt.event.MouseMotionListener;
/*      */ import java.beans.PropertyChangeEvent;
/*      */ import java.beans.PropertyChangeListener;
/*      */ import javax.accessibility.Accessible;
/*      */ import javax.accessibility.AccessibleContext;
/*      */ import javax.swing.Action;
/*      */ import javax.swing.ActionMap;
/*      */ import javax.swing.CellRendererPane;
/*      */ import javax.swing.ComboBoxEditor;
/*      */ import javax.swing.ComboBoxModel;
/*      */ import javax.swing.DefaultListCellRenderer;
/*      */ import javax.swing.InputMap;
/*      */ import javax.swing.JButton;
/*      */ import javax.swing.JComboBox;
/*      */ import javax.swing.JComboBox.KeySelectionManager;
/*      */ import javax.swing.JComponent;
/*      */ import javax.swing.JList;
/*      */ import javax.swing.JPanel;
/*      */ import javax.swing.JRootPane;
/*      */ import javax.swing.KeyStroke;
/*      */ import javax.swing.ListCellRenderer;
/*      */ import javax.swing.LookAndFeel;
/*      */ import javax.swing.SwingUtilities;
/*      */ import javax.swing.UIManager;
/*      */ import javax.swing.event.ListDataEvent;
/*      */ import javax.swing.event.ListDataListener;
/*      */ import javax.swing.plaf.ComboBoxUI;
/*      */ import javax.swing.plaf.ComponentUI;
/*      */ import javax.swing.plaf.UIResource;
/*      */ import javax.swing.text.Position.Bias;
/*      */ import sun.awt.AppContext;
/*      */ import sun.swing.DefaultLookup;
/*      */ import sun.swing.UIAction;
/*      */ 
/*      */ public class BasicComboBoxUI extends ComboBoxUI
/*      */ {
/*      */   protected JComboBox comboBox;
/*      */   protected boolean hasFocus;
/*      */   private boolean isTableCellEditor;
/*      */   private static final String IS_TABLE_CELL_EDITOR = "JComboBox.isTableCellEditor";
/*      */   protected JList listBox;
/*      */   protected CellRendererPane currentValuePane;
/*      */   protected ComboPopup popup;
/*      */   protected Component editor;
/*      */   protected JButton arrowButton;
/*      */   protected KeyListener keyListener;
/*      */   protected FocusListener focusListener;
/*      */   protected PropertyChangeListener propertyChangeListener;
/*      */   protected ItemListener itemListener;
/*      */   protected MouseListener popupMouseListener;
/*      */   protected MouseMotionListener popupMouseMotionListener;
/*      */   protected KeyListener popupKeyListener;
/*      */   protected ListDataListener listDataListener;
/*      */   private Handler handler;
/*      */   private long timeFactor;
/*      */   private long lastTime;
/*      */   private long time;
/*      */   JComboBox.KeySelectionManager keySelectionManager;
/*      */   protected boolean isMinimumSizeDirty;
/*      */   protected Dimension cachedMinimumSize;
/*      */   private boolean isDisplaySizeDirty;
/*      */   private Dimension cachedDisplaySize;
/*  177 */   private static final Object COMBO_UI_LIST_CELL_RENDERER_KEY = new StringBuffer("DefaultListCellRendererKey");
/*      */ 
/*  180 */   static final StringBuffer HIDE_POPUP_KEY = new StringBuffer("HidePopupKey");
/*      */   private boolean sameBaseline;
/*      */   protected boolean squareButton;
/*      */   protected Insets padding;
/*      */ 
/*      */   public BasicComboBoxUI()
/*      */   {
/*   70 */     this.hasFocus = false;
/*      */ 
/*   74 */     this.isTableCellEditor = false;
/*      */ 
/*   82 */     this.currentValuePane = new CellRendererPane();
/*      */ 
/*  150 */     this.timeFactor = 1000L;
/*      */ 
/*  156 */     this.lastTime = 0L;
/*  157 */     this.time = 0L;
/*      */ 
/*  165 */     this.isMinimumSizeDirty = true;
/*      */ 
/*  168 */     this.cachedMinimumSize = new Dimension(0, 0);
/*      */ 
/*  171 */     this.isDisplaySizeDirty = true;
/*      */ 
/*  174 */     this.cachedDisplaySize = new Dimension(0, 0);
/*      */ 
/*  195 */     this.squareButton = true;
/*      */   }
/*      */ 
/*      */   private static ListCellRenderer getDefaultListCellRenderer()
/*      */   {
/*  208 */     Object localObject = (ListCellRenderer)AppContext.getAppContext().get(COMBO_UI_LIST_CELL_RENDERER_KEY);
/*      */ 
/*  211 */     if (localObject == null) {
/*  212 */       localObject = new DefaultListCellRenderer();
/*  213 */       AppContext.getAppContext().put(COMBO_UI_LIST_CELL_RENDERER_KEY, new DefaultListCellRenderer());
/*      */     }
/*      */ 
/*  216 */     return localObject;
/*      */   }
/*      */ 
/*      */   static void loadActionMap(LazyActionMap paramLazyActionMap)
/*      */   {
/*  223 */     paramLazyActionMap.put(new Actions("hidePopup"));
/*  224 */     paramLazyActionMap.put(new Actions("pageDownPassThrough"));
/*  225 */     paramLazyActionMap.put(new Actions("pageUpPassThrough"));
/*  226 */     paramLazyActionMap.put(new Actions("homePassThrough"));
/*  227 */     paramLazyActionMap.put(new Actions("endPassThrough"));
/*  228 */     paramLazyActionMap.put(new Actions("selectNext"));
/*  229 */     paramLazyActionMap.put(new Actions("selectNext2"));
/*  230 */     paramLazyActionMap.put(new Actions("togglePopup"));
/*  231 */     paramLazyActionMap.put(new Actions("spacePopup"));
/*  232 */     paramLazyActionMap.put(new Actions("selectPrevious"));
/*  233 */     paramLazyActionMap.put(new Actions("selectPrevious2"));
/*  234 */     paramLazyActionMap.put(new Actions("enterPressed"));
/*      */   }
/*      */ 
/*      */   public static ComponentUI createUI(JComponent paramJComponent)
/*      */   {
/*  242 */     return new BasicComboBoxUI();
/*      */   }
/*      */ 
/*      */   public void installUI(JComponent paramJComponent)
/*      */   {
/*  247 */     this.isMinimumSizeDirty = true;
/*      */ 
/*  249 */     this.comboBox = ((JComboBox)paramJComponent);
/*  250 */     installDefaults();
/*  251 */     this.popup = createPopup();
/*  252 */     this.listBox = this.popup.getList();
/*      */ 
/*  255 */     Boolean localBoolean = (Boolean)paramJComponent.getClientProperty("JComboBox.isTableCellEditor");
/*  256 */     if (localBoolean != null) {
/*  257 */       this.isTableCellEditor = (localBoolean.equals(Boolean.TRUE));
/*      */     }
/*      */ 
/*  260 */     if ((this.comboBox.getRenderer() == null) || ((this.comboBox.getRenderer() instanceof UIResource))) {
/*  261 */       this.comboBox.setRenderer(createRenderer());
/*      */     }
/*      */ 
/*  264 */     if ((this.comboBox.getEditor() == null) || ((this.comboBox.getEditor() instanceof UIResource))) {
/*  265 */       this.comboBox.setEditor(createEditor());
/*      */     }
/*      */ 
/*  268 */     installListeners();
/*  269 */     installComponents();
/*      */ 
/*  271 */     this.comboBox.setLayout(createLayoutManager());
/*      */ 
/*  273 */     this.comboBox.setRequestFocusEnabled(true);
/*      */ 
/*  275 */     installKeyboardActions();
/*      */ 
/*  277 */     this.comboBox.putClientProperty("doNotCancelPopup", HIDE_POPUP_KEY);
/*      */ 
/*  279 */     if ((this.keySelectionManager == null) || ((this.keySelectionManager instanceof UIResource))) {
/*  280 */       this.keySelectionManager = new DefaultKeySelectionManager();
/*      */     }
/*  282 */     this.comboBox.setKeySelectionManager(this.keySelectionManager);
/*      */   }
/*      */ 
/*      */   public void uninstallUI(JComponent paramJComponent)
/*      */   {
/*  287 */     setPopupVisible(this.comboBox, false);
/*  288 */     this.popup.uninstallingUI();
/*      */ 
/*  290 */     uninstallKeyboardActions();
/*      */ 
/*  292 */     this.comboBox.setLayout(null);
/*      */ 
/*  294 */     uninstallComponents();
/*  295 */     uninstallListeners();
/*  296 */     uninstallDefaults();
/*      */ 
/*  298 */     if ((this.comboBox.getRenderer() == null) || ((this.comboBox.getRenderer() instanceof UIResource))) {
/*  299 */       this.comboBox.setRenderer(null);
/*      */     }
/*      */ 
/*  302 */     ComboBoxEditor localComboBoxEditor = this.comboBox.getEditor();
/*  303 */     if ((localComboBoxEditor instanceof UIResource)) {
/*  304 */       if (localComboBoxEditor.getEditorComponent().hasFocus())
/*      */       {
/*  306 */         this.comboBox.requestFocusInWindow();
/*      */       }
/*  308 */       this.comboBox.setEditor(null);
/*      */     }
/*      */ 
/*  311 */     if ((this.keySelectionManager instanceof UIResource)) {
/*  312 */       this.comboBox.setKeySelectionManager(null);
/*      */     }
/*      */ 
/*  315 */     this.handler = null;
/*  316 */     this.keyListener = null;
/*  317 */     this.focusListener = null;
/*  318 */     this.listDataListener = null;
/*  319 */     this.propertyChangeListener = null;
/*  320 */     this.popup = null;
/*  321 */     this.listBox = null;
/*  322 */     this.comboBox = null;
/*      */   }
/*      */ 
/*      */   protected void installDefaults()
/*      */   {
/*  330 */     LookAndFeel.installColorsAndFont(this.comboBox, "ComboBox.background", "ComboBox.foreground", "ComboBox.font");
/*      */ 
/*  334 */     LookAndFeel.installBorder(this.comboBox, "ComboBox.border");
/*  335 */     LookAndFeel.installProperty(this.comboBox, "opaque", Boolean.TRUE);
/*      */ 
/*  337 */     Long localLong = (Long)UIManager.get("ComboBox.timeFactor");
/*  338 */     this.timeFactor = (localLong == null ? 1000L : localLong.longValue());
/*      */ 
/*  341 */     Boolean localBoolean = (Boolean)UIManager.get("ComboBox.squareButton");
/*  342 */     this.squareButton = (localBoolean == null ? true : localBoolean.booleanValue());
/*      */ 
/*  344 */     this.padding = UIManager.getInsets("ComboBox.padding");
/*      */   }
/*      */ 
/*      */   protected void installListeners()
/*      */   {
/*  352 */     if ((this.itemListener = createItemListener()) != null) {
/*  353 */       this.comboBox.addItemListener(this.itemListener);
/*      */     }
/*  355 */     if ((this.propertyChangeListener = createPropertyChangeListener()) != null) {
/*  356 */       this.comboBox.addPropertyChangeListener(this.propertyChangeListener);
/*      */     }
/*  358 */     if ((this.keyListener = createKeyListener()) != null) {
/*  359 */       this.comboBox.addKeyListener(this.keyListener);
/*      */     }
/*  361 */     if ((this.focusListener = createFocusListener()) != null) {
/*  362 */       this.comboBox.addFocusListener(this.focusListener);
/*      */     }
/*  364 */     if ((this.popupMouseListener = this.popup.getMouseListener()) != null) {
/*  365 */       this.comboBox.addMouseListener(this.popupMouseListener);
/*      */     }
/*  367 */     if ((this.popupMouseMotionListener = this.popup.getMouseMotionListener()) != null) {
/*  368 */       this.comboBox.addMouseMotionListener(this.popupMouseMotionListener);
/*      */     }
/*  370 */     if ((this.popupKeyListener = this.popup.getKeyListener()) != null) {
/*  371 */       this.comboBox.addKeyListener(this.popupKeyListener);
/*      */     }
/*      */ 
/*  374 */     if ((this.comboBox.getModel() != null) && 
/*  375 */       ((this.listDataListener = createListDataListener()) != null))
/*  376 */       this.comboBox.getModel().addListDataListener(this.listDataListener);
/*      */   }
/*      */ 
/*      */   protected void uninstallDefaults()
/*      */   {
/*  386 */     LookAndFeel.installColorsAndFont(this.comboBox, "ComboBox.background", "ComboBox.foreground", "ComboBox.font");
/*      */ 
/*  390 */     LookAndFeel.uninstallBorder(this.comboBox);
/*      */   }
/*      */ 
/*      */   protected void uninstallListeners()
/*      */   {
/*  399 */     if (this.keyListener != null) {
/*  400 */       this.comboBox.removeKeyListener(this.keyListener);
/*      */     }
/*  402 */     if (this.itemListener != null) {
/*  403 */       this.comboBox.removeItemListener(this.itemListener);
/*      */     }
/*  405 */     if (this.propertyChangeListener != null) {
/*  406 */       this.comboBox.removePropertyChangeListener(this.propertyChangeListener);
/*      */     }
/*  408 */     if (this.focusListener != null) {
/*  409 */       this.comboBox.removeFocusListener(this.focusListener);
/*      */     }
/*  411 */     if (this.popupMouseListener != null) {
/*  412 */       this.comboBox.removeMouseListener(this.popupMouseListener);
/*      */     }
/*  414 */     if (this.popupMouseMotionListener != null) {
/*  415 */       this.comboBox.removeMouseMotionListener(this.popupMouseMotionListener);
/*      */     }
/*  417 */     if (this.popupKeyListener != null) {
/*  418 */       this.comboBox.removeKeyListener(this.popupKeyListener);
/*      */     }
/*  420 */     if ((this.comboBox.getModel() != null) && 
/*  421 */       (this.listDataListener != null))
/*  422 */       this.comboBox.getModel().removeListDataListener(this.listDataListener);
/*      */   }
/*      */ 
/*      */   protected ComboPopup createPopup()
/*      */   {
/*  434 */     return new BasicComboPopup(this.comboBox);
/*      */   }
/*      */ 
/*      */   protected KeyListener createKeyListener()
/*      */   {
/*  445 */     return getHandler();
/*      */   }
/*      */ 
/*      */   protected FocusListener createFocusListener()
/*      */   {
/*  455 */     return getHandler();
/*      */   }
/*      */ 
/*      */   protected ListDataListener createListDataListener()
/*      */   {
/*  466 */     return getHandler();
/*      */   }
/*      */ 
/*      */   protected ItemListener createItemListener()
/*      */   {
/*  480 */     return null;
/*      */   }
/*      */ 
/*      */   protected PropertyChangeListener createPropertyChangeListener()
/*      */   {
/*  491 */     return getHandler();
/*      */   }
/*      */ 
/*      */   protected LayoutManager createLayoutManager()
/*      */   {
/*  501 */     return getHandler();
/*      */   }
/*      */ 
/*      */   protected ListCellRenderer createRenderer()
/*      */   {
/*  513 */     return new BasicComboBoxRenderer.UIResource();
/*      */   }
/*      */ 
/*      */   protected ComboBoxEditor createEditor()
/*      */   {
/*  525 */     return new BasicComboBoxEditor.UIResource();
/*      */   }
/*      */ 
/*      */   private Handler getHandler()
/*      */   {
/*  532 */     if (this.handler == null) {
/*  533 */       this.handler = new Handler(null);
/*      */     }
/*  535 */     return this.handler;
/*      */   }
/*      */ 
/*      */   private void updateToolTipTextForChildren()
/*      */   {
/*  644 */     Component[] arrayOfComponent = this.comboBox.getComponents();
/*  645 */     for (int i = 0; i < arrayOfComponent.length; i++)
/*  646 */       if ((arrayOfComponent[i] instanceof JComponent))
/*  647 */         ((JComponent)arrayOfComponent[i]).setToolTipText(this.comboBox.getToolTipText());
/*      */   }
/*      */ 
/*      */   protected void installComponents()
/*      */   {
/*  694 */     this.arrowButton = createArrowButton();
/*  695 */     this.comboBox.add(this.arrowButton);
/*      */ 
/*  697 */     if (this.arrowButton != null) {
/*  698 */       configureArrowButton();
/*      */     }
/*      */ 
/*  701 */     if (this.comboBox.isEditable()) {
/*  702 */       addEditor();
/*      */     }
/*      */ 
/*  705 */     this.comboBox.add(this.currentValuePane);
/*      */   }
/*      */ 
/*      */   protected void uninstallComponents()
/*      */   {
/*  714 */     if (this.arrowButton != null) {
/*  715 */       unconfigureArrowButton();
/*      */     }
/*  717 */     if (this.editor != null) {
/*  718 */       unconfigureEditor();
/*      */     }
/*  720 */     this.comboBox.removeAll();
/*  721 */     this.arrowButton = null;
/*      */   }
/*      */ 
/*      */   public void addEditor()
/*      */   {
/*  734 */     removeEditor();
/*  735 */     this.editor = this.comboBox.getEditor().getEditorComponent();
/*  736 */     if (this.editor != null) {
/*  737 */       configureEditor();
/*  738 */       this.comboBox.add(this.editor);
/*  739 */       if (this.comboBox.isFocusOwner())
/*      */       {
/*  741 */         this.editor.requestFocusInWindow();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void removeEditor()
/*      */   {
/*  753 */     if (this.editor != null) {
/*  754 */       unconfigureEditor();
/*  755 */       this.comboBox.remove(this.editor);
/*  756 */       this.editor = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void configureEditor()
/*      */   {
/*  768 */     this.editor.setEnabled(this.comboBox.isEnabled());
/*      */ 
/*  770 */     this.editor.setFocusable(this.comboBox.isFocusable());
/*      */ 
/*  772 */     this.editor.setFont(this.comboBox.getFont());
/*      */ 
/*  774 */     if (this.focusListener != null) {
/*  775 */       this.editor.addFocusListener(this.focusListener);
/*      */     }
/*      */ 
/*  778 */     this.editor.addFocusListener(getHandler());
/*      */ 
/*  780 */     this.comboBox.getEditor().addActionListener(getHandler());
/*      */ 
/*  782 */     if ((this.editor instanceof JComponent)) {
/*  783 */       ((JComponent)this.editor).putClientProperty("doNotCancelPopup", HIDE_POPUP_KEY);
/*      */ 
/*  785 */       ((JComponent)this.editor).setInheritsPopupMenu(true);
/*      */     }
/*      */ 
/*  788 */     this.comboBox.configureEditor(this.comboBox.getEditor(), this.comboBox.getSelectedItem());
/*      */ 
/*  790 */     this.editor.addPropertyChangeListener(this.propertyChangeListener);
/*      */   }
/*      */ 
/*      */   protected void unconfigureEditor()
/*      */   {
/*  800 */     if (this.focusListener != null) {
/*  801 */       this.editor.removeFocusListener(this.focusListener);
/*      */     }
/*      */ 
/*  804 */     this.editor.removePropertyChangeListener(this.propertyChangeListener);
/*  805 */     this.editor.removeFocusListener(getHandler());
/*  806 */     this.comboBox.getEditor().removeActionListener(getHandler());
/*      */   }
/*      */ 
/*      */   public void configureArrowButton()
/*      */   {
/*  816 */     if (this.arrowButton != null) {
/*  817 */       this.arrowButton.setEnabled(this.comboBox.isEnabled());
/*  818 */       this.arrowButton.setFocusable(this.comboBox.isFocusable());
/*  819 */       this.arrowButton.setRequestFocusEnabled(false);
/*  820 */       this.arrowButton.addMouseListener(this.popup.getMouseListener());
/*  821 */       this.arrowButton.addMouseMotionListener(this.popup.getMouseMotionListener());
/*  822 */       this.arrowButton.resetKeyboardActions();
/*  823 */       this.arrowButton.putClientProperty("doNotCancelPopup", HIDE_POPUP_KEY);
/*  824 */       this.arrowButton.setInheritsPopupMenu(true);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void unconfigureArrowButton()
/*      */   {
/*  835 */     if (this.arrowButton != null) {
/*  836 */       this.arrowButton.removeMouseListener(this.popup.getMouseListener());
/*  837 */       this.arrowButton.removeMouseMotionListener(this.popup.getMouseMotionListener());
/*      */     }
/*      */   }
/*      */ 
/*      */   protected JButton createArrowButton()
/*      */   {
/*  848 */     BasicArrowButton localBasicArrowButton = new BasicArrowButton(5, UIManager.getColor("ComboBox.buttonBackground"), UIManager.getColor("ComboBox.buttonShadow"), UIManager.getColor("ComboBox.buttonDarkShadow"), UIManager.getColor("ComboBox.buttonHighlight"));
/*      */ 
/*  853 */     localBasicArrowButton.setName("ComboBox.arrowButton");
/*  854 */     return localBasicArrowButton;
/*      */   }
/*      */ 
/*      */   public boolean isPopupVisible(JComboBox paramJComboBox)
/*      */   {
/*  870 */     return this.popup.isVisible();
/*      */   }
/*      */ 
/*      */   public void setPopupVisible(JComboBox paramJComboBox, boolean paramBoolean)
/*      */   {
/*  877 */     if (paramBoolean)
/*  878 */       this.popup.show();
/*      */     else
/*  880 */       this.popup.hide();
/*      */   }
/*      */ 
/*      */   public boolean isFocusTraversable(JComboBox paramJComboBox)
/*      */   {
/*  889 */     return !this.comboBox.isEditable();
/*      */   }
/*      */ 
/*      */   public void paint(Graphics paramGraphics, JComponent paramJComponent)
/*      */   {
/*  901 */     this.hasFocus = this.comboBox.hasFocus();
/*  902 */     if (!this.comboBox.isEditable()) {
/*  903 */       Rectangle localRectangle = rectangleForCurrentValue();
/*  904 */       paintCurrentValueBackground(paramGraphics, localRectangle, this.hasFocus);
/*  905 */       paintCurrentValue(paramGraphics, localRectangle, this.hasFocus);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Dimension getPreferredSize(JComponent paramJComponent)
/*      */   {
/*  911 */     return getMinimumSize(paramJComponent);
/*      */   }
/*      */ 
/*      */   public Dimension getMinimumSize(JComponent paramJComponent)
/*      */   {
/*  919 */     if (!this.isMinimumSizeDirty) {
/*  920 */       return new Dimension(this.cachedMinimumSize);
/*      */     }
/*  922 */     Dimension localDimension = getDisplaySize();
/*  923 */     Insets localInsets = getInsets();
/*      */ 
/*  925 */     int i = localDimension.height;
/*  926 */     int j = this.squareButton ? i : this.arrowButton.getPreferredSize().width;
/*      */ 
/*  928 */     localDimension.height += localInsets.top + localInsets.bottom;
/*  929 */     localDimension.width += localInsets.left + localInsets.right + j;
/*      */ 
/*  931 */     this.cachedMinimumSize.setSize(localDimension.width, localDimension.height);
/*  932 */     this.isMinimumSizeDirty = false;
/*      */ 
/*  934 */     return new Dimension(localDimension);
/*      */   }
/*      */ 
/*      */   public Dimension getMaximumSize(JComponent paramJComponent)
/*      */   {
/*  939 */     return new Dimension(32767, 32767);
/*      */   }
/*      */ 
/*      */   public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2)
/*      */   {
/*  952 */     super.getBaseline(paramJComponent, paramInt1, paramInt2);
/*  953 */     int i = -1;
/*      */ 
/*  955 */     getDisplaySize();
/*  956 */     if (this.sameBaseline) {
/*  957 */       Insets localInsets = paramJComponent.getInsets();
/*  958 */       paramInt2 = paramInt2 - localInsets.top - localInsets.bottom;
/*  959 */       if (!this.comboBox.isEditable()) {
/*  960 */         Object localObject1 = this.comboBox.getRenderer();
/*  961 */         if (localObject1 == null) {
/*  962 */           localObject1 = new DefaultListCellRenderer();
/*      */         }
/*  964 */         Object localObject2 = null;
/*  965 */         Object localObject3 = this.comboBox.getPrototypeDisplayValue();
/*  966 */         if (localObject3 != null) {
/*  967 */           localObject2 = localObject3;
/*      */         }
/*  969 */         else if (this.comboBox.getModel().getSize() > 0)
/*      */         {
/*  972 */           localObject2 = this.comboBox.getModel().getElementAt(0);
/*      */         }
/*  974 */         if (localObject2 == null)
/*  975 */           localObject2 = " ";
/*  976 */         else if (((localObject2 instanceof String)) && ("".equals(localObject2))) {
/*  977 */           localObject2 = " ";
/*      */         }
/*  979 */         Component localComponent = ((ListCellRenderer)localObject1).getListCellRendererComponent(this.listBox, localObject2, -1, false, false);
/*      */ 
/*  982 */         if ((localComponent instanceof JComponent)) {
/*  983 */           localComponent.setFont(this.comboBox.getFont());
/*      */         }
/*  985 */         i = localComponent.getBaseline(paramInt1, paramInt2);
/*      */       }
/*      */       else {
/*  988 */         i = this.editor.getBaseline(paramInt1, paramInt2);
/*      */       }
/*  990 */       if (i > 0) {
/*  991 */         i += localInsets.top;
/*      */       }
/*      */     }
/*  994 */     return i;
/*      */   }
/*      */ 
/*      */   public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent paramJComponent)
/*      */   {
/* 1008 */     super.getBaselineResizeBehavior(paramJComponent);
/*      */ 
/* 1010 */     getDisplaySize();
/* 1011 */     if (this.comboBox.isEditable()) {
/* 1012 */       return this.editor.getBaselineResizeBehavior();
/*      */     }
/* 1014 */     if (this.sameBaseline) {
/* 1015 */       Object localObject1 = this.comboBox.getRenderer();
/* 1016 */       if (localObject1 == null) {
/* 1017 */         localObject1 = new DefaultListCellRenderer();
/*      */       }
/* 1019 */       Object localObject2 = null;
/* 1020 */       Object localObject3 = this.comboBox.getPrototypeDisplayValue();
/* 1021 */       if (localObject3 != null) {
/* 1022 */         localObject2 = localObject3;
/*      */       }
/* 1024 */       else if (this.comboBox.getModel().getSize() > 0)
/*      */       {
/* 1027 */         localObject2 = this.comboBox.getModel().getElementAt(0);
/*      */       }
/* 1029 */       if (localObject2 != null) {
/* 1030 */         Component localComponent = ((ListCellRenderer)localObject1).getListCellRendererComponent(this.listBox, localObject2, -1, false, false);
/*      */ 
/* 1033 */         return localComponent.getBaselineResizeBehavior();
/*      */       }
/*      */     }
/* 1036 */     return Component.BaselineResizeBehavior.OTHER;
/*      */   }
/*      */ 
/*      */   public int getAccessibleChildrenCount(JComponent paramJComponent)
/*      */   {
/* 1042 */     if (this.comboBox.isEditable()) {
/* 1043 */       return 2;
/*      */     }
/*      */ 
/* 1046 */     return 1;
/*      */   }
/*      */ 
/*      */   public Accessible getAccessibleChild(JComponent paramJComponent, int paramInt)
/*      */   {
/*      */     AccessibleContext localAccessibleContext;
/* 1055 */     switch (paramInt) {
/*      */     case 0:
/* 1057 */       if ((this.popup instanceof Accessible)) {
/* 1058 */         localAccessibleContext = ((Accessible)this.popup).getAccessibleContext();
/* 1059 */         localAccessibleContext.setAccessibleParent(this.comboBox);
/* 1060 */         return (Accessible)this.popup;
/*      */       }
/*      */       break;
/*      */     case 1:
/* 1064 */       if ((this.comboBox.isEditable()) && ((this.editor instanceof Accessible)))
/*      */       {
/* 1066 */         localAccessibleContext = ((Accessible)this.editor).getAccessibleContext();
/* 1067 */         localAccessibleContext.setAccessibleParent(this.comboBox);
/* 1068 */         return (Accessible)this.editor;
/*      */       }
/*      */       break;
/*      */     }
/* 1072 */     return null;
/*      */   }
/*      */ 
/*      */   protected boolean isNavigationKey(int paramInt)
/*      */   {
/* 1091 */     return (paramInt == 38) || (paramInt == 40) || (paramInt == 224) || (paramInt == 225);
/*      */   }
/*      */ 
/*      */   private boolean isNavigationKey(int paramInt1, int paramInt2)
/*      */   {
/* 1096 */     InputMap localInputMap = this.comboBox.getInputMap(1);
/* 1097 */     KeyStroke localKeyStroke = KeyStroke.getKeyStroke(paramInt1, paramInt2);
/*      */ 
/* 1099 */     if ((localInputMap != null) && (localInputMap.get(localKeyStroke) != null)) {
/* 1100 */       return true;
/*      */     }
/* 1102 */     return false;
/*      */   }
/*      */ 
/*      */   protected void selectNextPossibleValue()
/*      */   {
/*      */     int i;
/* 1112 */     if (this.comboBox.isPopupVisible()) {
/* 1113 */       i = this.listBox.getSelectedIndex();
/*      */     }
/*      */     else {
/* 1116 */       i = this.comboBox.getSelectedIndex();
/*      */     }
/*      */ 
/* 1119 */     if (i < this.comboBox.getModel().getSize() - 1) {
/* 1120 */       this.listBox.setSelectedIndex(i + 1);
/* 1121 */       this.listBox.ensureIndexIsVisible(i + 1);
/* 1122 */       if (!this.isTableCellEditor) {
/* 1123 */         this.comboBox.setSelectedIndex(i + 1);
/*      */       }
/* 1125 */       this.comboBox.repaint();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void selectPreviousPossibleValue()
/*      */   {
/*      */     int i;
/* 1136 */     if (this.comboBox.isPopupVisible()) {
/* 1137 */       i = this.listBox.getSelectedIndex();
/*      */     }
/*      */     else {
/* 1140 */       i = this.comboBox.getSelectedIndex();
/*      */     }
/*      */ 
/* 1143 */     if (i > 0) {
/* 1144 */       this.listBox.setSelectedIndex(i - 1);
/* 1145 */       this.listBox.ensureIndexIsVisible(i - 1);
/* 1146 */       if (!this.isTableCellEditor) {
/* 1147 */         this.comboBox.setSelectedIndex(i - 1);
/*      */       }
/* 1149 */       this.comboBox.repaint();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void toggleOpenClose()
/*      */   {
/* 1157 */     setPopupVisible(this.comboBox, !isPopupVisible(this.comboBox));
/*      */   }
/*      */ 
/*      */   protected Rectangle rectangleForCurrentValue()
/*      */   {
/* 1164 */     int i = this.comboBox.getWidth();
/* 1165 */     int j = this.comboBox.getHeight();
/* 1166 */     Insets localInsets = getInsets();
/* 1167 */     int k = j - (localInsets.top + localInsets.bottom);
/* 1168 */     if (this.arrowButton != null) {
/* 1169 */       k = this.arrowButton.getWidth();
/*      */     }
/* 1171 */     if (BasicGraphicsUtils.isLeftToRight(this.comboBox)) {
/* 1172 */       return new Rectangle(localInsets.left, localInsets.top, i - (localInsets.left + localInsets.right + k), j - (localInsets.top + localInsets.bottom));
/*      */     }
/*      */ 
/* 1177 */     return new Rectangle(localInsets.left + k, localInsets.top, i - (localInsets.left + localInsets.right + k), j - (localInsets.top + localInsets.bottom));
/*      */   }
/*      */ 
/*      */   protected Insets getInsets()
/*      */   {
/* 1187 */     return this.comboBox.getInsets();
/*      */   }
/*      */ 
/*      */   public void paintCurrentValue(Graphics paramGraphics, Rectangle paramRectangle, boolean paramBoolean)
/*      */   {
/* 1203 */     ListCellRenderer localListCellRenderer = this.comboBox.getRenderer();
/*      */     Component localComponent;
/* 1206 */     if ((paramBoolean) && (!isPopupVisible(this.comboBox))) {
/* 1207 */       localComponent = localListCellRenderer.getListCellRendererComponent(this.listBox, this.comboBox.getSelectedItem(), -1, true, false);
/*      */     }
/*      */     else
/*      */     {
/* 1214 */       localComponent = localListCellRenderer.getListCellRendererComponent(this.listBox, this.comboBox.getSelectedItem(), -1, false, false);
/*      */ 
/* 1219 */       localComponent.setBackground(UIManager.getColor("ComboBox.background"));
/*      */     }
/* 1221 */     localComponent.setFont(this.comboBox.getFont());
/* 1222 */     if ((paramBoolean) && (!isPopupVisible(this.comboBox))) {
/* 1223 */       localComponent.setForeground(this.listBox.getSelectionForeground());
/* 1224 */       localComponent.setBackground(this.listBox.getSelectionBackground());
/*      */     }
/* 1227 */     else if (this.comboBox.isEnabled()) {
/* 1228 */       localComponent.setForeground(this.comboBox.getForeground());
/* 1229 */       localComponent.setBackground(this.comboBox.getBackground());
/*      */     }
/*      */     else {
/* 1232 */       localComponent.setForeground(DefaultLookup.getColor(this.comboBox, this, "ComboBox.disabledForeground", null));
/*      */ 
/* 1234 */       localComponent.setBackground(DefaultLookup.getColor(this.comboBox, this, "ComboBox.disabledBackground", null));
/*      */     }
/*      */ 
/* 1240 */     boolean bool = false;
/* 1241 */     if ((localComponent instanceof JPanel)) {
/* 1242 */       bool = true;
/*      */     }
/*      */ 
/* 1245 */     int i = paramRectangle.x; int j = paramRectangle.y; int k = paramRectangle.width; int m = paramRectangle.height;
/* 1246 */     if (this.padding != null) {
/* 1247 */       i = paramRectangle.x + this.padding.left;
/* 1248 */       j = paramRectangle.y + this.padding.top;
/* 1249 */       k = paramRectangle.width - (this.padding.left + this.padding.right);
/* 1250 */       m = paramRectangle.height - (this.padding.top + this.padding.bottom);
/*      */     }
/*      */ 
/* 1253 */     this.currentValuePane.paintComponent(paramGraphics, localComponent, this.comboBox, i, j, k, m, bool);
/*      */   }
/*      */ 
/*      */   public void paintCurrentValueBackground(Graphics paramGraphics, Rectangle paramRectangle, boolean paramBoolean)
/*      */   {
/* 1260 */     Color localColor = paramGraphics.getColor();
/* 1261 */     if (this.comboBox.isEnabled()) {
/* 1262 */       paramGraphics.setColor(DefaultLookup.getColor(this.comboBox, this, "ComboBox.background", null));
/*      */     }
/*      */     else {
/* 1265 */       paramGraphics.setColor(DefaultLookup.getColor(this.comboBox, this, "ComboBox.disabledBackground", null));
/*      */     }
/* 1267 */     paramGraphics.fillRect(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height);
/* 1268 */     paramGraphics.setColor(localColor);
/*      */   }
/*      */ 
/*      */   void repaintCurrentValue()
/*      */   {
/* 1275 */     Rectangle localRectangle = rectangleForCurrentValue();
/* 1276 */     this.comboBox.repaint(localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height);
/*      */   }
/*      */ 
/*      */   protected Dimension getDefaultSize()
/*      */   {
/* 1297 */     Dimension localDimension = getSizeForComponent(getDefaultListCellRenderer().getListCellRendererComponent(this.listBox, " ", -1, false, false));
/*      */ 
/* 1299 */     return new Dimension(localDimension.width, localDimension.height);
/*      */   }
/*      */ 
/*      */   protected Dimension getDisplaySize()
/*      */   {
/* 1315 */     if (!this.isDisplaySizeDirty) {
/* 1316 */       return new Dimension(this.cachedDisplaySize);
/*      */     }
/* 1318 */     Dimension localDimension1 = new Dimension();
/*      */ 
/* 1320 */     Object localObject1 = this.comboBox.getRenderer();
/* 1321 */     if (localObject1 == null) {
/* 1322 */       localObject1 = new DefaultListCellRenderer();
/*      */     }
/*      */ 
/* 1325 */     this.sameBaseline = true;
/*      */ 
/* 1327 */     Object localObject2 = this.comboBox.getPrototypeDisplayValue();
/*      */     Object localObject3;
/* 1328 */     if (localObject2 != null)
/*      */     {
/* 1330 */       localDimension1 = getSizeForComponent(((ListCellRenderer)localObject1).getListCellRendererComponent(this.listBox, localObject2, -1, false, false));
/*      */     }
/*      */     else
/*      */     {
/* 1336 */       localObject3 = this.comboBox.getModel();
/* 1337 */       int i = ((ComboBoxModel)localObject3).getSize();
/* 1338 */       int j = -1;
/*      */ 
/* 1343 */       if (i > 0) {
/* 1344 */         for (int k = 0; k < i; k++)
/*      */         {
/* 1347 */           Object localObject4 = ((ComboBoxModel)localObject3).getElementAt(k);
/* 1348 */           Component localComponent = ((ListCellRenderer)localObject1).getListCellRendererComponent(this.listBox, localObject4, -1, false, false);
/*      */ 
/* 1350 */           Dimension localDimension2 = getSizeForComponent(localComponent);
/* 1351 */           if ((this.sameBaseline) && (localObject4 != null) && ((!(localObject4 instanceof String)) || (!"".equals(localObject4))))
/*      */           {
/* 1353 */             int m = localComponent.getBaseline(localDimension2.width, localDimension2.height);
/* 1354 */             if (m == -1) {
/* 1355 */               this.sameBaseline = false;
/*      */             }
/* 1357 */             else if (j == -1) {
/* 1358 */               j = m;
/*      */             }
/* 1360 */             else if (j != m) {
/* 1361 */               this.sameBaseline = false;
/*      */             }
/*      */           }
/* 1364 */           localDimension1.width = Math.max(localDimension1.width, localDimension2.width);
/* 1365 */           localDimension1.height = Math.max(localDimension1.height, localDimension2.height);
/*      */         }
/*      */       } else {
/* 1368 */         localDimension1 = getDefaultSize();
/* 1369 */         if (this.comboBox.isEditable()) {
/* 1370 */           localDimension1.width = 100;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1375 */     if (this.comboBox.isEditable()) {
/* 1376 */       localObject3 = this.editor.getPreferredSize();
/* 1377 */       localDimension1.width = Math.max(localDimension1.width, ((Dimension)localObject3).width);
/* 1378 */       localDimension1.height = Math.max(localDimension1.height, ((Dimension)localObject3).height);
/*      */     }
/*      */ 
/* 1382 */     if (this.padding != null) {
/* 1383 */       localDimension1.width += this.padding.left + this.padding.right;
/* 1384 */       localDimension1.height += this.padding.top + this.padding.bottom;
/*      */     }
/*      */ 
/* 1388 */     this.cachedDisplaySize.setSize(localDimension1.width, localDimension1.height);
/* 1389 */     this.isDisplaySizeDirty = false;
/*      */ 
/* 1391 */     return localDimension1;
/*      */   }
/*      */ 
/*      */   protected Dimension getSizeForComponent(Component paramComponent)
/*      */   {
/* 1406 */     this.currentValuePane.add(paramComponent);
/* 1407 */     paramComponent.setFont(this.comboBox.getFont());
/* 1408 */     Dimension localDimension = paramComponent.getPreferredSize();
/* 1409 */     this.currentValuePane.remove(paramComponent);
/* 1410 */     return localDimension;
/*      */   }
/*      */ 
/*      */   protected void installKeyboardActions()
/*      */   {
/* 1428 */     InputMap localInputMap = getInputMap(1);
/* 1429 */     SwingUtilities.replaceUIInputMap(this.comboBox, 1, localInputMap);
/*      */ 
/* 1433 */     LazyActionMap.installLazyActionMap(this.comboBox, BasicComboBoxUI.class, "ComboBox.actionMap");
/*      */   }
/*      */ 
/*      */   InputMap getInputMap(int paramInt)
/*      */   {
/* 1438 */     if (paramInt == 1) {
/* 1439 */       return (InputMap)DefaultLookup.get(this.comboBox, this, "ComboBox.ancestorInputMap");
/*      */     }
/*      */ 
/* 1442 */     return null;
/*      */   }
/*      */ 
/*      */   boolean isTableCellEditor() {
/* 1446 */     return this.isTableCellEditor;
/*      */   }
/*      */ 
/*      */   protected void uninstallKeyboardActions()
/*      */   {
/* 1453 */     SwingUtilities.replaceUIInputMap(this.comboBox, 1, null);
/*      */ 
/* 1455 */     SwingUtilities.replaceUIActionMap(this.comboBox, null); } 
/*      */   private static class Actions extends UIAction { private static final String HIDE = "hidePopup";
/*      */     private static final String DOWN = "selectNext";
/*      */     private static final String DOWN_2 = "selectNext2";
/*      */     private static final String TOGGLE = "togglePopup";
/*      */     private static final String TOGGLE_2 = "spacePopup";
/*      */     private static final String UP = "selectPrevious";
/*      */     private static final String UP_2 = "selectPrevious2";
/*      */     private static final String ENTER = "enterPressed";
/*      */     private static final String PAGE_DOWN = "pageDownPassThrough";
/*      */     private static final String PAGE_UP = "pageUpPassThrough";
/*      */     private static final String HOME = "homePassThrough";
/*      */     private static final String END = "endPassThrough";
/*      */ 
/* 1477 */     Actions(String paramString) { super(); }
/*      */ 
/*      */     public void actionPerformed(ActionEvent paramActionEvent)
/*      */     {
/* 1481 */       String str = getName();
/* 1482 */       JComboBox localJComboBox = (JComboBox)paramActionEvent.getSource();
/* 1483 */       BasicComboBoxUI localBasicComboBoxUI = (BasicComboBoxUI)BasicLookAndFeel.getUIOfType(localJComboBox.getUI(), BasicComboBoxUI.class);
/*      */ 
/* 1485 */       if (str == "hidePopup") {
/* 1486 */         localJComboBox.firePopupMenuCanceled();
/* 1487 */         localJComboBox.setPopupVisible(false);
/*      */       }
/* 1489 */       else if ((str == "pageDownPassThrough") || (str == "pageUpPassThrough") || (str == "homePassThrough") || (str == "endPassThrough"))
/*      */       {
/* 1491 */         int i = getNextIndex(localJComboBox, str);
/* 1492 */         if ((i >= 0) && (i < localJComboBox.getItemCount())) {
/* 1493 */           localJComboBox.setSelectedIndex(i);
/*      */         }
/*      */       }
/* 1496 */       else if (str == "selectNext") {
/* 1497 */         if (localJComboBox.isShowing()) {
/* 1498 */           if (localJComboBox.isPopupVisible()) {
/* 1499 */             if (localBasicComboBoxUI != null)
/* 1500 */               localBasicComboBoxUI.selectNextPossibleValue();
/*      */           }
/*      */           else {
/* 1503 */             localJComboBox.setPopupVisible(true);
/*      */           }
/*      */         }
/*      */       }
/* 1507 */       else if (str == "selectNext2")
/*      */       {
/* 1511 */         if (localJComboBox.isShowing()) {
/* 1512 */           if (((localJComboBox.isEditable()) || ((localBasicComboBoxUI != null) && (localBasicComboBoxUI.isTableCellEditor()))) && (!localJComboBox.isPopupVisible()))
/*      */           {
/* 1515 */             localJComboBox.setPopupVisible(true);
/*      */           }
/* 1517 */           else if (localBasicComboBoxUI != null) {
/* 1518 */             localBasicComboBoxUI.selectNextPossibleValue();
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/* 1523 */       else if ((str == "togglePopup") || (str == "spacePopup")) {
/* 1524 */         if ((localBasicComboBoxUI != null) && ((str == "togglePopup") || (!localJComboBox.isEditable()))) {
/* 1525 */           if (localBasicComboBoxUI.isTableCellEditor())
/*      */           {
/* 1528 */             localJComboBox.setSelectedIndex(localBasicComboBoxUI.popup.getList().getSelectedIndex());
/*      */           }
/*      */           else
/*      */           {
/* 1532 */             localJComboBox.setPopupVisible(!localJComboBox.isPopupVisible());
/*      */           }
/*      */         }
/*      */       }
/* 1536 */       else if (str == "selectPrevious") {
/* 1537 */         if (localBasicComboBoxUI != null) {
/* 1538 */           if (localBasicComboBoxUI.isPopupVisible(localJComboBox)) {
/* 1539 */             localBasicComboBoxUI.selectPreviousPossibleValue();
/*      */           }
/* 1541 */           else if (DefaultLookup.getBoolean(localJComboBox, localBasicComboBoxUI, "ComboBox.showPopupOnNavigation", false))
/*      */           {
/* 1543 */             localBasicComboBoxUI.setPopupVisible(localJComboBox, true);
/*      */           }
/*      */         }
/*      */       }
/* 1547 */       else if (str == "selectPrevious2")
/*      */       {
/* 1550 */         if ((localJComboBox.isShowing()) && (localBasicComboBoxUI != null)) {
/* 1551 */           if ((localJComboBox.isEditable()) && (!localJComboBox.isPopupVisible()))
/* 1552 */             localJComboBox.setPopupVisible(true);
/*      */           else {
/* 1554 */             localBasicComboBoxUI.selectPreviousPossibleValue();
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/* 1559 */       else if (str == "enterPressed")
/*      */       {
/*      */         Object localObject1;
/* 1560 */         if (localJComboBox.isPopupVisible())
/*      */         {
/* 1562 */           boolean bool = UIManager.getBoolean("ComboBox.isEnterSelectablePopup");
/*      */ 
/* 1564 */           if ((!localJComboBox.isEditable()) || (bool) || (localBasicComboBoxUI.isTableCellEditor))
/*      */           {
/* 1566 */             localObject1 = localBasicComboBoxUI.popup.getList().getSelectedValue();
/* 1567 */             if (localObject1 != null)
/*      */             {
/* 1572 */               localJComboBox.getEditor().setItem(localObject1);
/* 1573 */               localJComboBox.setSelectedItem(localObject1);
/*      */             }
/*      */           }
/* 1576 */           localJComboBox.setPopupVisible(false);
/*      */         }
/*      */         else
/*      */         {
/* 1580 */           if ((localBasicComboBoxUI.isTableCellEditor) && (!localJComboBox.isEditable())) {
/* 1581 */             localJComboBox.setSelectedItem(localJComboBox.getSelectedItem());
/*      */           }
/*      */ 
/* 1586 */           JRootPane localJRootPane = SwingUtilities.getRootPane(localJComboBox);
/* 1587 */           if (localJRootPane != null) {
/* 1588 */             localObject1 = localJRootPane.getInputMap(2);
/* 1589 */             ActionMap localActionMap = localJRootPane.getActionMap();
/* 1590 */             if ((localObject1 != null) && (localActionMap != null)) {
/* 1591 */               Object localObject2 = ((InputMap)localObject1).get(KeyStroke.getKeyStroke(10, 0));
/* 1592 */               if (localObject2 != null) {
/* 1593 */                 Action localAction = localActionMap.get(localObject2);
/* 1594 */                 if (localAction != null)
/* 1595 */                   localAction.actionPerformed(new ActionEvent(localJRootPane, paramActionEvent.getID(), paramActionEvent.getActionCommand(), paramActionEvent.getWhen(), paramActionEvent.getModifiers()));
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     private int getNextIndex(JComboBox paramJComboBox, String paramString)
/*      */     {
/*      */       int i;
/*      */       int j;
/* 1607 */       if (paramString == "pageUpPassThrough") {
/* 1608 */         i = paramJComboBox.getMaximumRowCount();
/* 1609 */         j = paramJComboBox.getSelectedIndex() - i;
/* 1610 */         return j < 0 ? 0 : j;
/*      */       }
/* 1612 */       if (paramString == "pageDownPassThrough") {
/* 1613 */         i = paramJComboBox.getMaximumRowCount();
/* 1614 */         j = paramJComboBox.getSelectedIndex() + i;
/* 1615 */         int k = paramJComboBox.getItemCount();
/* 1616 */         return j < k ? j : k - 1;
/*      */       }
/* 1618 */       if (paramString == "homePassThrough") {
/* 1619 */         return 0;
/*      */       }
/* 1621 */       if (paramString == "endPassThrough") {
/* 1622 */         return paramJComboBox.getItemCount() - 1;
/*      */       }
/* 1624 */       return paramJComboBox.getSelectedIndex();
/*      */     }
/*      */ 
/*      */     public boolean isEnabled(Object paramObject) {
/* 1628 */       if (getName() == "hidePopup") {
/* 1629 */         return (paramObject != null) && (((JComboBox)paramObject).isPopupVisible());
/*      */       }
/* 1631 */       return true;
/*      */     }
/*      */   }
/*      */ 
/*      */   public class ComboBoxLayoutManager
/*      */     implements LayoutManager
/*      */   {
/*      */     public ComboBoxLayoutManager()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void addLayoutComponent(String paramString, Component paramComponent)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void removeLayoutComponent(Component paramComponent)
/*      */     {
/*      */     }
/*      */ 
/*      */     public Dimension preferredLayoutSize(Container paramContainer)
/*      */     {
/*  667 */       return BasicComboBoxUI.this.getHandler().preferredLayoutSize(paramContainer);
/*      */     }
/*      */ 
/*      */     public Dimension minimumLayoutSize(Container paramContainer) {
/*  671 */       return BasicComboBoxUI.this.getHandler().minimumLayoutSize(paramContainer);
/*      */     }
/*      */ 
/*      */     public void layoutContainer(Container paramContainer) {
/*  675 */       BasicComboBoxUI.this.getHandler().layoutContainer(paramContainer);
/*      */     }
/*      */   }
/*      */ 
/*      */   class DefaultKeySelectionManager
/*      */     implements JComboBox.KeySelectionManager, UIResource
/*      */   {
/* 1931 */     private String prefix = "";
/* 1932 */     private String typedString = "";
/*      */ 
/*      */     DefaultKeySelectionManager() {  } 
/* 1935 */     public int selectionForKey(char paramChar, ComboBoxModel paramComboBoxModel) { if (BasicComboBoxUI.this.lastTime == 0L) {
/* 1936 */         this.prefix = "";
/* 1937 */         this.typedString = "";
/*      */       }
/* 1939 */       int i = 1;
/*      */ 
/* 1941 */       int j = BasicComboBoxUI.this.comboBox.getSelectedIndex();
/* 1942 */       if (BasicComboBoxUI.this.time - BasicComboBoxUI.this.lastTime < BasicComboBoxUI.this.timeFactor) {
/* 1943 */         this.typedString += paramChar;
/* 1944 */         if ((this.prefix.length() == 1) && (paramChar == this.prefix.charAt(0)))
/*      */         {
/* 1947 */           j++;
/*      */         }
/* 1949 */         else this.prefix = this.typedString; 
/*      */       }
/*      */       else
/*      */       {
/* 1952 */         j++;
/* 1953 */         this.typedString = ("" + paramChar);
/* 1954 */         this.prefix = this.typedString;
/*      */       }
/* 1956 */       BasicComboBoxUI.this.lastTime = BasicComboBoxUI.this.time;
/*      */ 
/* 1958 */       if ((j < 0) || (j >= paramComboBoxModel.getSize())) {
/* 1959 */         i = 0;
/* 1960 */         j = 0;
/*      */       }
/* 1962 */       int k = BasicComboBoxUI.this.listBox.getNextMatch(this.prefix, j, Position.Bias.Forward);
/*      */ 
/* 1964 */       if ((k < 0) && (i != 0)) {
/* 1965 */         k = BasicComboBoxUI.this.listBox.getNextMatch(this.prefix, 0, Position.Bias.Forward);
/*      */       }
/*      */ 
/* 1968 */       return k;
/*      */     }
/*      */   }
/*      */ 
/*      */   public class FocusHandler
/*      */     implements FocusListener
/*      */   {
/*      */     public FocusHandler()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void focusGained(FocusEvent paramFocusEvent)
/*      */     {
/*  573 */       BasicComboBoxUI.this.getHandler().focusGained(paramFocusEvent);
/*      */     }
/*      */ 
/*      */     public void focusLost(FocusEvent paramFocusEvent) {
/*  577 */       BasicComboBoxUI.this.getHandler().focusLost(paramFocusEvent);
/*      */     }
/*      */   }
/*      */ 
/*      */   private class Handler
/*      */     implements ActionListener, FocusListener, KeyListener, LayoutManager, ListDataListener, PropertyChangeListener
/*      */   {
/*      */     private Handler()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
/*      */     {
/* 1649 */       String str = paramPropertyChangeEvent.getPropertyName();
/* 1650 */       if (paramPropertyChangeEvent.getSource() == BasicComboBoxUI.this.editor)
/*      */       {
/* 1654 */         if ("border".equals(str)) {
/* 1655 */           BasicComboBoxUI.this.isMinimumSizeDirty = true;
/* 1656 */           BasicComboBoxUI.this.isDisplaySizeDirty = true;
/* 1657 */           BasicComboBoxUI.this.comboBox.revalidate();
/*      */         }
/*      */       } else {
/* 1660 */         JComboBox localJComboBox = (JComboBox)paramPropertyChangeEvent.getSource();
/* 1661 */         if (str == "model") {
/* 1662 */           ComboBoxModel localComboBoxModel1 = (ComboBoxModel)paramPropertyChangeEvent.getNewValue();
/* 1663 */           ComboBoxModel localComboBoxModel2 = (ComboBoxModel)paramPropertyChangeEvent.getOldValue();
/*      */ 
/* 1665 */           if ((localComboBoxModel2 != null) && (BasicComboBoxUI.this.listDataListener != null)) {
/* 1666 */             localComboBoxModel2.removeListDataListener(BasicComboBoxUI.this.listDataListener);
/*      */           }
/*      */ 
/* 1669 */           if ((localComboBoxModel1 != null) && (BasicComboBoxUI.this.listDataListener != null)) {
/* 1670 */             localComboBoxModel1.addListDataListener(BasicComboBoxUI.this.listDataListener);
/*      */           }
/*      */ 
/* 1673 */           if (BasicComboBoxUI.this.editor != null) {
/* 1674 */             localJComboBox.configureEditor(localJComboBox.getEditor(), localJComboBox.getSelectedItem());
/*      */           }
/* 1676 */           BasicComboBoxUI.this.isMinimumSizeDirty = true;
/* 1677 */           BasicComboBoxUI.this.isDisplaySizeDirty = true;
/* 1678 */           localJComboBox.revalidate();
/* 1679 */           localJComboBox.repaint();
/*      */         }
/* 1681 */         else if ((str == "editor") && (localJComboBox.isEditable())) {
/* 1682 */           BasicComboBoxUI.this.addEditor();
/* 1683 */           localJComboBox.revalidate();
/*      */         }
/* 1685 */         else if (str == "editable") {
/* 1686 */           if (localJComboBox.isEditable()) {
/* 1687 */             localJComboBox.setRequestFocusEnabled(false);
/* 1688 */             BasicComboBoxUI.this.addEditor();
/*      */           } else {
/* 1690 */             localJComboBox.setRequestFocusEnabled(true);
/* 1691 */             BasicComboBoxUI.this.removeEditor();
/*      */           }
/* 1693 */           BasicComboBoxUI.this.updateToolTipTextForChildren();
/* 1694 */           localJComboBox.revalidate();
/*      */         }
/*      */         else
/*      */         {
/*      */           boolean bool;
/* 1696 */           if (str == "enabled") {
/* 1697 */             bool = localJComboBox.isEnabled();
/* 1698 */             if (BasicComboBoxUI.this.editor != null)
/* 1699 */               BasicComboBoxUI.this.editor.setEnabled(bool);
/* 1700 */             if (BasicComboBoxUI.this.arrowButton != null)
/* 1701 */               BasicComboBoxUI.this.arrowButton.setEnabled(bool);
/* 1702 */             localJComboBox.repaint();
/*      */           }
/* 1704 */           else if (str == "focusable") {
/* 1705 */             bool = localJComboBox.isFocusable();
/* 1706 */             if (BasicComboBoxUI.this.editor != null)
/* 1707 */               BasicComboBoxUI.this.editor.setFocusable(bool);
/* 1708 */             if (BasicComboBoxUI.this.arrowButton != null)
/* 1709 */               BasicComboBoxUI.this.arrowButton.setFocusable(bool);
/* 1710 */             localJComboBox.repaint();
/*      */           }
/* 1712 */           else if (str == "maximumRowCount") {
/* 1713 */             if (BasicComboBoxUI.this.isPopupVisible(localJComboBox)) {
/* 1714 */               BasicComboBoxUI.this.setPopupVisible(localJComboBox, false);
/* 1715 */               BasicComboBoxUI.this.setPopupVisible(localJComboBox, true);
/*      */             }
/*      */           }
/* 1718 */           else if (str == "font") {
/* 1719 */             BasicComboBoxUI.this.listBox.setFont(localJComboBox.getFont());
/* 1720 */             if (BasicComboBoxUI.this.editor != null) {
/* 1721 */               BasicComboBoxUI.this.editor.setFont(localJComboBox.getFont());
/*      */             }
/* 1723 */             BasicComboBoxUI.this.isMinimumSizeDirty = true;
/* 1724 */             BasicComboBoxUI.this.isDisplaySizeDirty = true;
/* 1725 */             localJComboBox.validate();
/*      */           }
/* 1727 */           else if (str == "ToolTipText") {
/* 1728 */             BasicComboBoxUI.this.updateToolTipTextForChildren();
/*      */           }
/* 1730 */           else if (str == "JComboBox.isTableCellEditor") {
/* 1731 */             Boolean localBoolean = (Boolean)paramPropertyChangeEvent.getNewValue();
/* 1732 */             BasicComboBoxUI.this.isTableCellEditor = (localBoolean.equals(Boolean.TRUE));
/*      */           }
/* 1734 */           else if (str == "prototypeDisplayValue") {
/* 1735 */             BasicComboBoxUI.this.isMinimumSizeDirty = true;
/* 1736 */             BasicComboBoxUI.this.isDisplaySizeDirty = true;
/* 1737 */             localJComboBox.revalidate();
/*      */           }
/* 1739 */           else if (str == "renderer") {
/* 1740 */             BasicComboBoxUI.this.isMinimumSizeDirty = true;
/* 1741 */             BasicComboBoxUI.this.isDisplaySizeDirty = true;
/* 1742 */             localJComboBox.revalidate();
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     public void keyPressed(KeyEvent paramKeyEvent)
/*      */     {
/* 1757 */       if (BasicComboBoxUI.this.isNavigationKey(paramKeyEvent.getKeyCode(), paramKeyEvent.getModifiers())) {
/* 1758 */         BasicComboBoxUI.this.lastTime = 0L;
/* 1759 */       } else if ((BasicComboBoxUI.this.comboBox.isEnabled()) && (BasicComboBoxUI.this.comboBox.getModel().getSize() != 0) && (isTypeAheadKey(paramKeyEvent)) && (paramKeyEvent.getKeyChar() != 65535))
/*      */       {
/* 1761 */         BasicComboBoxUI.this.time = paramKeyEvent.getWhen();
/* 1762 */         if (BasicComboBoxUI.this.comboBox.selectWithKeyChar(paramKeyEvent.getKeyChar()))
/* 1763 */           paramKeyEvent.consume();
/*      */       }
/*      */     }
/*      */ 
/*      */     public void keyTyped(KeyEvent paramKeyEvent)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void keyReleased(KeyEvent paramKeyEvent) {
/*      */     }
/*      */ 
/*      */     private boolean isTypeAheadKey(KeyEvent paramKeyEvent) {
/* 1775 */       return (!paramKeyEvent.isAltDown()) && (!BasicGraphicsUtils.isMenuShortcutKeyDown(paramKeyEvent));
/*      */     }
/*      */ 
/*      */     public void focusGained(FocusEvent paramFocusEvent)
/*      */     {
/* 1786 */       ComboBoxEditor localComboBoxEditor = BasicComboBoxUI.this.comboBox.getEditor();
/*      */ 
/* 1788 */       if ((localComboBoxEditor != null) && (paramFocusEvent.getSource() == localComboBoxEditor.getEditorComponent()))
/*      */       {
/* 1790 */         return;
/*      */       }
/* 1792 */       BasicComboBoxUI.this.hasFocus = true;
/* 1793 */       BasicComboBoxUI.this.comboBox.repaint();
/*      */ 
/* 1795 */       if ((BasicComboBoxUI.this.comboBox.isEditable()) && (BasicComboBoxUI.this.editor != null))
/* 1796 */         BasicComboBoxUI.this.editor.requestFocus();
/*      */     }
/*      */ 
/*      */     public void focusLost(FocusEvent paramFocusEvent)
/*      */     {
/* 1801 */       ComboBoxEditor localComboBoxEditor = BasicComboBoxUI.this.comboBox.getEditor();
/* 1802 */       if ((localComboBoxEditor != null) && (paramFocusEvent.getSource() == localComboBoxEditor.getEditorComponent()))
/*      */       {
/* 1804 */         Object localObject1 = localComboBoxEditor.getItem();
/*      */ 
/* 1806 */         Object localObject2 = BasicComboBoxUI.this.comboBox.getSelectedItem();
/* 1807 */         if ((!paramFocusEvent.isTemporary()) && (localObject1 != null)) if (!localObject1.equals(localObject2 == null ? "" : localObject2))
/*      */           {
/* 1809 */             BasicComboBoxUI.this.comboBox.actionPerformed(new ActionEvent(localComboBoxEditor, 0, "", EventQueue.getMostRecentEventTime(), 0));
/*      */           }
/*      */ 
/*      */ 
/*      */       }
/*      */ 
/* 1815 */       BasicComboBoxUI.this.hasFocus = false;
/* 1816 */       if (!paramFocusEvent.isTemporary()) {
/* 1817 */         BasicComboBoxUI.this.setPopupVisible(BasicComboBoxUI.this.comboBox, false);
/*      */       }
/* 1819 */       BasicComboBoxUI.this.comboBox.repaint();
/*      */     }
/*      */ 
/*      */     public void contentsChanged(ListDataEvent paramListDataEvent)
/*      */     {
/* 1828 */       if ((paramListDataEvent.getIndex0() != -1) || (paramListDataEvent.getIndex1() != -1)) {
/* 1829 */         BasicComboBoxUI.this.isMinimumSizeDirty = true;
/* 1830 */         BasicComboBoxUI.this.comboBox.revalidate();
/*      */       }
/*      */ 
/* 1835 */       if ((BasicComboBoxUI.this.comboBox.isEditable()) && (BasicComboBoxUI.this.editor != null)) {
/* 1836 */         BasicComboBoxUI.this.comboBox.configureEditor(BasicComboBoxUI.this.comboBox.getEditor(), BasicComboBoxUI.this.comboBox.getSelectedItem());
/*      */       }
/*      */ 
/* 1840 */       BasicComboBoxUI.this.isDisplaySizeDirty = true;
/* 1841 */       BasicComboBoxUI.this.comboBox.repaint();
/*      */     }
/*      */ 
/*      */     public void intervalAdded(ListDataEvent paramListDataEvent) {
/* 1845 */       contentsChanged(paramListDataEvent);
/*      */     }
/*      */ 
/*      */     public void intervalRemoved(ListDataEvent paramListDataEvent) {
/* 1849 */       contentsChanged(paramListDataEvent);
/*      */     }
/*      */ 
/*      */     public void addLayoutComponent(String paramString, Component paramComponent)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void removeLayoutComponent(Component paramComponent)
/*      */     {
/*      */     }
/*      */ 
/*      */     public Dimension preferredLayoutSize(Container paramContainer)
/*      */     {
/* 1864 */       return paramContainer.getPreferredSize();
/*      */     }
/*      */ 
/*      */     public Dimension minimumLayoutSize(Container paramContainer) {
/* 1868 */       return paramContainer.getMinimumSize();
/*      */     }
/*      */ 
/*      */     public void layoutContainer(Container paramContainer) {
/* 1872 */       JComboBox localJComboBox = (JComboBox)paramContainer;
/* 1873 */       int i = localJComboBox.getWidth();
/* 1874 */       int j = localJComboBox.getHeight();
/*      */ 
/* 1876 */       Insets localInsets = BasicComboBoxUI.this.getInsets();
/* 1877 */       int k = j - (localInsets.top + localInsets.bottom);
/* 1878 */       int m = k;
/*      */       Object localObject;
/* 1879 */       if (BasicComboBoxUI.this.arrowButton != null) {
/* 1880 */         localObject = BasicComboBoxUI.this.arrowButton.getInsets();
/* 1881 */         m = BasicComboBoxUI.this.squareButton ? k : BasicComboBoxUI.this.arrowButton.getPreferredSize().width + ((Insets)localObject).left + ((Insets)localObject).right;
/*      */       }
/*      */ 
/* 1887 */       if (BasicComboBoxUI.this.arrowButton != null) {
/* 1888 */         if (BasicGraphicsUtils.isLeftToRight(localJComboBox)) {
/* 1889 */           BasicComboBoxUI.this.arrowButton.setBounds(i - (localInsets.right + m), localInsets.top, m, k);
/*      */         }
/*      */         else {
/* 1892 */           BasicComboBoxUI.this.arrowButton.setBounds(localInsets.left, localInsets.top, m, k);
/*      */         }
/*      */       }
/*      */ 
/* 1896 */       if (BasicComboBoxUI.this.editor != null) {
/* 1897 */         localObject = BasicComboBoxUI.this.rectangleForCurrentValue();
/* 1898 */         BasicComboBoxUI.this.editor.setBounds((Rectangle)localObject);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void actionPerformed(ActionEvent paramActionEvent)
/*      */     {
/* 1912 */       Object localObject = BasicComboBoxUI.this.comboBox.getEditor().getItem();
/* 1913 */       if (localObject != null) {
/* 1914 */         if ((!BasicComboBoxUI.this.comboBox.isPopupVisible()) && (!localObject.equals(BasicComboBoxUI.this.comboBox.getSelectedItem()))) {
/* 1915 */           BasicComboBoxUI.this.comboBox.setSelectedItem(BasicComboBoxUI.this.comboBox.getEditor().getItem());
/*      */         }
/* 1917 */         ActionMap localActionMap = BasicComboBoxUI.this.comboBox.getActionMap();
/* 1918 */         if (localActionMap != null) {
/* 1919 */           Action localAction = localActionMap.get("enterPressed");
/* 1920 */           if (localAction != null)
/* 1921 */             localAction.actionPerformed(new ActionEvent(BasicComboBoxUI.this.comboBox, paramActionEvent.getID(), paramActionEvent.getActionCommand(), paramActionEvent.getModifiers()));
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public class ItemHandler
/*      */     implements ItemListener
/*      */   {
/*      */     public ItemHandler()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void itemStateChanged(ItemEvent paramItemEvent)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   public class KeyHandler extends KeyAdapter
/*      */   {
/*      */     public KeyHandler()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void keyPressed(KeyEvent paramKeyEvent)
/*      */     {
/*  559 */       BasicComboBoxUI.this.getHandler().keyPressed(paramKeyEvent);
/*      */     }
/*      */   }
/*      */ 
/*      */   public class ListDataHandler
/*      */     implements ListDataListener
/*      */   {
/*      */     public ListDataHandler()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void contentsChanged(ListDataEvent paramListDataEvent)
/*      */     {
/*  593 */       BasicComboBoxUI.this.getHandler().contentsChanged(paramListDataEvent);
/*      */     }
/*      */ 
/*      */     public void intervalAdded(ListDataEvent paramListDataEvent) {
/*  597 */       BasicComboBoxUI.this.getHandler().intervalAdded(paramListDataEvent);
/*      */     }
/*      */ 
/*      */     public void intervalRemoved(ListDataEvent paramListDataEvent) {
/*  601 */       BasicComboBoxUI.this.getHandler().intervalRemoved(paramListDataEvent);
/*      */     }
/*      */   }
/*      */ 
/*      */   public class PropertyChangeHandler
/*      */     implements PropertyChangeListener
/*      */   {
/*      */     public PropertyChangeHandler()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
/*      */     {
/*  636 */       BasicComboBoxUI.this.getHandler().propertyChange(paramPropertyChangeEvent);
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.plaf.basic.BasicComboBoxUI
 * JD-Core Version:    0.6.2
 */