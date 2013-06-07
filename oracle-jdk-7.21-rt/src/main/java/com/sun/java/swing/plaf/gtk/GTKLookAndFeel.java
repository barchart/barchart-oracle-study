/*      */ package com.sun.java.swing.plaf.gtk;
/*      */ 
/*      */ import java.awt.Color;
/*      */ import java.awt.Component;
/*      */ import java.awt.ComponentOrientation;
/*      */ import java.awt.Container;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.Font;
/*      */ import java.awt.Toolkit;
/*      */ import java.awt.Window;
/*      */ import java.beans.PropertyChangeEvent;
/*      */ import java.beans.PropertyChangeListener;
/*      */ import java.io.File;
/*      */ import java.io.PrintStream;
/*      */ import java.lang.ref.ReferenceQueue;
/*      */ import java.lang.ref.WeakReference;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.util.Locale;
/*      */ import javax.swing.JComponent;
/*      */ import javax.swing.LayoutStyle;
/*      */ import javax.swing.LayoutStyle.ComponentPlacement;
/*      */ import javax.swing.SwingUtilities;
/*      */ import javax.swing.UIDefaults;
/*      */ import javax.swing.UIDefaults.ActiveValue;
/*      */ import javax.swing.UIDefaults.LazyInputMap;
/*      */ import javax.swing.UIDefaults.LazyValue;
/*      */ import javax.swing.UIManager;
/*      */ import javax.swing.colorchooser.AbstractColorChooserPanel;
/*      */ import javax.swing.plaf.ComponentUI;
/*      */ import javax.swing.plaf.DimensionUIResource;
/*      */ import javax.swing.plaf.InsetsUIResource;
/*      */ import javax.swing.plaf.synth.Region;
/*      */ import javax.swing.plaf.synth.SynthLookAndFeel;
/*      */ import sun.awt.OSInfo.OSType;
/*      */ import sun.awt.SunToolkit;
/*      */ import sun.awt.UNIXToolkit;
/*      */ import sun.java2d.SunGraphicsEnvironment;
/*      */ import sun.security.action.GetPropertyAction;
/*      */ import sun.swing.DefaultLayoutStyle;
/*      */ import sun.swing.SwingLazyValue;
/*      */ import sun.swing.SwingUtilities2;
/*      */ import sun.swing.SwingUtilities2.AATextInfo;
/*      */ 
/*      */ public class GTKLookAndFeel extends SynthLookAndFeel
/*      */ {
/*      */   private static final boolean IS_22;
/*      */   static Object aaTextInfo;
/*      */   private static boolean isSunCJK;
/*      */   private static boolean gtkAAFontSettingsCond;
/*      */   private Font fallbackFont;
/*      */   private boolean inInitialize;
/*      */   private boolean pclInstalled;
/*      */   private GTKStyleFactory styleFactory;
/*      */   private static String gtkThemeName;
/* 1474 */   static ReferenceQueue<GTKLookAndFeel> queue = new ReferenceQueue();
/*      */ 
/*      */   static boolean is2_2()
/*      */   {
/*  156 */     return IS_22;
/*      */   }
/*      */ 
/*      */   static GTKConstants.PositionType SwingOrientationConstantToGTK(int paramInt)
/*      */   {
/*  163 */     switch (paramInt) {
/*      */     case 2:
/*  165 */       return GTKConstants.PositionType.LEFT;
/*      */     case 4:
/*  167 */       return GTKConstants.PositionType.RIGHT;
/*      */     case 1:
/*  169 */       return GTKConstants.PositionType.TOP;
/*      */     case 3:
/*  171 */       return GTKConstants.PositionType.BOTTOM;
/*      */     }
/*  173 */     if (!$assertionsDisabled) throw new AssertionError("Unknown orientation: " + paramInt);
/*  174 */     return GTKConstants.PositionType.TOP;
/*      */   }
/*      */ 
/*      */   static GTKConstants.StateType synthStateToGTKStateType(int paramInt)
/*      */   {
/*      */     GTKConstants.StateType localStateType;
/*  183 */     switch (paramInt) {
/*      */     case 4:
/*  185 */       localStateType = GTKConstants.StateType.ACTIVE;
/*  186 */       break;
/*      */     case 2:
/*  188 */       localStateType = GTKConstants.StateType.PRELIGHT;
/*  189 */       break;
/*      */     case 512:
/*  191 */       localStateType = GTKConstants.StateType.SELECTED;
/*  192 */       break;
/*      */     case 8:
/*  194 */       localStateType = GTKConstants.StateType.INSENSITIVE;
/*  195 */       break;
/*      */     case 1:
/*      */     default:
/*  198 */       localStateType = GTKConstants.StateType.NORMAL;
/*      */     }
/*      */ 
/*  201 */     return localStateType;
/*      */   }
/*      */ 
/*      */   static int synthStateToGTKState(Region paramRegion, int paramInt)
/*      */   {
/*  218 */     if ((paramInt & 0x4) != 0) {
/*  219 */       if ((paramRegion == Region.RADIO_BUTTON) || (paramRegion == Region.CHECK_BOX) || (paramRegion == Region.MENU) || (paramRegion == Region.MENU_ITEM) || (paramRegion == Region.RADIO_BUTTON_MENU_ITEM) || (paramRegion == Region.CHECK_BOX_MENU_ITEM) || (paramRegion == Region.SPLIT_PANE))
/*      */       {
/*  226 */         paramInt = 2;
/*      */       }
/*  228 */       else paramInt = 4;
/*      */ 
/*      */     }
/*  231 */     else if (paramRegion == Region.TABBED_PANE_TAB) {
/*  232 */       if ((paramInt & 0x8) != 0) {
/*  233 */         paramInt = 8;
/*      */       }
/*  235 */       else if ((paramInt & 0x200) != 0)
/*  236 */         paramInt = 1;
/*      */       else {
/*  238 */         paramInt = 4;
/*      */       }
/*      */     }
/*  241 */     else if ((paramInt & 0x200) != 0) {
/*  242 */       if (paramRegion == Region.MENU)
/*  243 */         paramInt = 2;
/*  244 */       else if ((paramRegion == Region.RADIO_BUTTON) || (paramRegion == Region.TOGGLE_BUTTON) || (paramRegion == Region.RADIO_BUTTON_MENU_ITEM) || (paramRegion == Region.CHECK_BOX_MENU_ITEM) || (paramRegion == Region.CHECK_BOX) || (paramRegion == Region.BUTTON))
/*      */       {
/*  250 */         if ((paramInt & 0x8) != 0) {
/*  251 */           paramInt = 8;
/*      */         }
/*  256 */         else if ((paramInt & 0x2) != 0)
/*  257 */           paramInt = 2;
/*      */         else
/*  259 */           paramInt = 4;
/*      */       }
/*      */       else {
/*  262 */         paramInt = 512;
/*      */       }
/*      */ 
/*      */     }
/*  266 */     else if ((paramInt & 0x2) != 0) {
/*  267 */       paramInt = 2;
/*      */     }
/*  269 */     else if ((paramInt & 0x8) != 0) {
/*  270 */       paramInt = 8;
/*      */     }
/*  273 */     else if (paramRegion == Region.SLIDER_TRACK)
/*  274 */       paramInt = 4;
/*      */     else {
/*  276 */       paramInt = 1;
/*      */     }
/*      */ 
/*  279 */     return paramInt;
/*      */   }
/*      */ 
/*      */   static boolean isText(Region paramRegion)
/*      */   {
/*  284 */     return (paramRegion == Region.TEXT_FIELD) || (paramRegion == Region.FORMATTED_TEXT_FIELD) || (paramRegion == Region.LIST) || (paramRegion == Region.PASSWORD_FIELD) || (paramRegion == Region.SPINNER) || (paramRegion == Region.TABLE) || (paramRegion == Region.TEXT_AREA) || (paramRegion == Region.TEXT_FIELD) || (paramRegion == Region.TEXT_PANE) || (paramRegion == Region.TREE);
/*      */   }
/*      */ 
/*      */   public UIDefaults getDefaults()
/*      */   {
/*  298 */     UIDefaults localUIDefaults = super.getDefaults();
/*      */ 
/*  301 */     localUIDefaults.put("TabbedPane.isTabRollover", Boolean.FALSE);
/*      */ 
/*  304 */     localUIDefaults.put("Synth.doNotSetTextAA", Boolean.valueOf(true));
/*      */ 
/*  306 */     initResourceBundle(localUIDefaults);
/*      */ 
/*  309 */     initSystemColorDefaults(localUIDefaults);
/*  310 */     initComponentDefaults(localUIDefaults);
/*  311 */     installPropertyChangeListeners();
/*  312 */     return localUIDefaults;
/*      */   }
/*      */ 
/*      */   private void installPropertyChangeListeners() {
/*  316 */     if (!this.pclInstalled) {
/*  317 */       Toolkit localToolkit = Toolkit.getDefaultToolkit();
/*  318 */       WeakPCL localWeakPCL = new WeakPCL(this, localToolkit, "gnome.Net/ThemeName");
/*  319 */       localToolkit.addPropertyChangeListener(localWeakPCL.getKey(), localWeakPCL);
/*  320 */       localWeakPCL = new WeakPCL(this, localToolkit, "gnome.Gtk/FontName");
/*  321 */       localToolkit.addPropertyChangeListener(localWeakPCL.getKey(), localWeakPCL);
/*  322 */       localWeakPCL = new WeakPCL(this, localToolkit, "gnome.Xft/DPI");
/*  323 */       localToolkit.addPropertyChangeListener(localWeakPCL.getKey(), localWeakPCL);
/*      */ 
/*  325 */       flushUnreferenced();
/*  326 */       this.pclInstalled = true;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void initResourceBundle(UIDefaults paramUIDefaults) {
/*  331 */     paramUIDefaults.addResourceBundle("com.sun.java.swing.plaf.gtk.resources.gtk");
/*      */   }
/*      */ 
/*      */   protected void initComponentDefaults(UIDefaults paramUIDefaults)
/*      */   {
/*  337 */     super.initComponentDefaults(paramUIDefaults);
/*      */ 
/*  339 */     Integer localInteger1 = Integer.valueOf(0);
/*  340 */     SwingLazyValue localSwingLazyValue = new SwingLazyValue("javax.swing.plaf.BorderUIResource$EmptyBorderUIResource", new Object[] { localInteger1, localInteger1, localInteger1, localInteger1 });
/*      */ 
/*  343 */     GTKStyle.GTKLazyValue localGTKLazyValue1 = new GTKStyle.GTKLazyValue("com.sun.java.swing.plaf.gtk.GTKPainter$ListTableFocusBorder", "getUnselectedCellBorder");
/*      */ 
/*  346 */     GTKStyle.GTKLazyValue localGTKLazyValue2 = new GTKStyle.GTKLazyValue("com.sun.java.swing.plaf.gtk.GTKPainter$ListTableFocusBorder", "getSelectedCellBorder");
/*      */ 
/*  349 */     GTKStyle.GTKLazyValue localGTKLazyValue3 = new GTKStyle.GTKLazyValue("com.sun.java.swing.plaf.gtk.GTKPainter$ListTableFocusBorder", "getNoFocusCellBorder");
/*      */ 
/*  353 */     GTKStyleFactory localGTKStyleFactory = (GTKStyleFactory)getStyleFactory();
/*  354 */     GTKStyle localGTKStyle1 = (GTKStyle)localGTKStyleFactory.getStyle(null, Region.TREE);
/*  355 */     Color localColor1 = localGTKStyle1.getGTKColor(1, GTKColorType.TEXT_BACKGROUND);
/*      */ 
/*  357 */     Color localColor2 = localGTKStyle1.getGTKColor(1, GTKColorType.BACKGROUND);
/*      */ 
/*  359 */     Color localColor3 = localGTKStyle1.getGTKColor(1, GTKColorType.FOREGROUND);
/*      */ 
/*  365 */     GTKStyle localGTKStyle2 = (GTKStyle)localGTKStyleFactory.getStyle(null, Region.PROGRESS_BAR);
/*      */ 
/*  367 */     int i = localGTKStyle2.getXThickness();
/*  368 */     int j = localGTKStyle2.getYThickness();
/*  369 */     int k = 150 - i * 2;
/*  370 */     int m = 20 - j * 2;
/*  371 */     int n = 22 - i * 2;
/*  372 */     int i1 = 80 - j * 2;
/*      */ 
/*  374 */     Integer localInteger2 = Integer.valueOf(500);
/*  375 */     InsetsUIResource localInsetsUIResource1 = new InsetsUIResource(0, 0, 0, 0);
/*      */ 
/*  377 */     Double localDouble = new Double(0.025D);
/*  378 */     Color localColor4 = paramUIDefaults.getColor("caretColor");
/*  379 */     Color localColor5 = paramUIDefaults.getColor("controlText");
/*      */ 
/*  381 */     UIDefaults.LazyInputMap localLazyInputMap1 = new UIDefaults.LazyInputMap(new Object[] { "ctrl C", "copy-to-clipboard", "ctrl V", "paste-from-clipboard", "ctrl X", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", "shift DELETE", "cut-to-clipboard", "shift LEFT", "selection-backward", "shift KP_LEFT", "selection-backward", "shift RIGHT", "selection-forward", "shift KP_RIGHT", "selection-forward", "ctrl LEFT", "caret-previous-word", "ctrl KP_LEFT", "caret-previous-word", "ctrl RIGHT", "caret-next-word", "ctrl KP_RIGHT", "caret-next-word", "ctrl shift LEFT", "selection-previous-word", "ctrl shift KP_LEFT", "selection-previous-word", "ctrl shift RIGHT", "selection-next-word", "ctrl shift KP_RIGHT", "selection-next-word", "ctrl A", "select-all", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", "shift END", "selection-end-line", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", "ctrl DELETE", "delete-next-word", "ctrl BACK_SPACE", "delete-previous-word", "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", "KP_LEFT", "caret-backward", "ENTER", "notify-field-accept", "ctrl BACK_SLASH", "unselect", "control shift O", "toggle-componentOrientation" });
/*      */ 
/*  423 */     UIDefaults.LazyInputMap localLazyInputMap2 = new UIDefaults.LazyInputMap(new Object[] { "ctrl C", "copy-to-clipboard", "ctrl V", "paste-from-clipboard", "ctrl X", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", "shift DELETE", "cut-to-clipboard", "shift LEFT", "selection-backward", "shift KP_LEFT", "selection-backward", "shift RIGHT", "selection-forward", "shift KP_RIGHT", "selection-forward", "ctrl LEFT", "caret-begin-line", "ctrl KP_LEFT", "caret-begin-line", "ctrl RIGHT", "caret-end-line", "ctrl KP_RIGHT", "caret-end-line", "ctrl shift LEFT", "selection-begin-line", "ctrl shift KP_LEFT", "selection-begin-line", "ctrl shift RIGHT", "selection-end-line", "ctrl shift KP_RIGHT", "selection-end-line", "ctrl A", "select-all", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", "shift END", "selection-end-line", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", "KP_LEFT", "caret-backward", "ENTER", "notify-field-accept", "ctrl BACK_SLASH", "unselect", "control shift O", "toggle-componentOrientation" });
/*      */ 
/*  463 */     InsetsUIResource localInsetsUIResource2 = new InsetsUIResource(3, 3, 3, 3);
/*      */ 
/*  465 */     UIDefaults.LazyInputMap localLazyInputMap3 = new UIDefaults.LazyInputMap(new Object[] { "ctrl C", "copy-to-clipboard", "ctrl V", "paste-from-clipboard", "ctrl X", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", "shift DELETE", "cut-to-clipboard", "shift LEFT", "selection-backward", "shift KP_LEFT", "selection-backward", "shift RIGHT", "selection-forward", "shift KP_RIGHT", "selection-forward", "ctrl LEFT", "caret-previous-word", "ctrl KP_LEFT", "caret-previous-word", "ctrl RIGHT", "caret-next-word", "ctrl KP_RIGHT", "caret-next-word", "ctrl shift LEFT", "selection-previous-word", "ctrl shift KP_LEFT", "selection-previous-word", "ctrl shift RIGHT", "selection-next-word", "ctrl shift KP_RIGHT", "selection-next-word", "ctrl A", "select-all", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", "shift END", "selection-end-line", "UP", "caret-up", "KP_UP", "caret-up", "DOWN", "caret-down", "KP_DOWN", "caret-down", "PAGE_UP", "page-up", "PAGE_DOWN", "page-down", "shift PAGE_UP", "selection-page-up", "shift PAGE_DOWN", "selection-page-down", "ctrl shift PAGE_UP", "selection-page-left", "ctrl shift PAGE_DOWN", "selection-page-right", "shift UP", "selection-up", "shift KP_UP", "selection-up", "shift DOWN", "selection-down", "shift KP_DOWN", "selection-down", "ENTER", "insert-break", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", "ctrl DELETE", "delete-next-word", "ctrl BACK_SPACE", "delete-previous-word", "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", "KP_LEFT", "caret-backward", "TAB", "insert-tab", "ctrl BACK_SLASH", "unselect", "ctrl HOME", "caret-begin", "ctrl END", "caret-end", "ctrl shift HOME", "selection-begin", "ctrl shift END", "selection-end", "ctrl T", "next-link-action", "ctrl shift T", "previous-link-action", "ctrl SPACE", "activate-link-action", "control shift O", "toggle-componentOrientation" });
/*      */ 
/*  542 */     Object[] arrayOfObject = { "ArrowButton.size", Integer.valueOf(13), "Button.defaultButtonFollowsFocus", Boolean.FALSE, "Button.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "SPACE", "pressed", "released SPACE", "released", "ENTER", "pressed", "released ENTER", "released" }), "Button.font", new UIDefaults.LazyValue()
/*      */     {
/*      */       private Region region;
/*      */ 
/*      */       public Object createValue(UIDefaults paramAnonymousUIDefaults)
/*      */       {
/*  536 */         GTKStyleFactory localGTKStyleFactory = (GTKStyleFactory)SynthLookAndFeel.getStyleFactory();
/*  537 */         GTKStyle localGTKStyle = (GTKStyle)localGTKStyleFactory.getStyle(null, this.region);
/*  538 */         return localGTKStyle.getFontForState(null);
/*      */       }
/*      */     }
/*      */     , "Button.margin", localInsetsUIResource1, "CheckBox.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "SPACE", "pressed", "released SPACE", "released" }), "CheckBox.icon", new GTKStyle.GTKLazyValue("com.sun.java.swing.plaf.gtk.GTKIconFactory", "getCheckBoxIcon"), "CheckBox.font", new UIDefaults.LazyValue()
/*      */     {
/*      */       private Region region;
/*      */ 
/*      */       public Object createValue(UIDefaults paramAnonymousUIDefaults)
/*      */       {
/*  536 */         GTKStyleFactory localGTKStyleFactory = (GTKStyleFactory)SynthLookAndFeel.getStyleFactory();
/*  537 */         GTKStyle localGTKStyle = (GTKStyle)localGTKStyleFactory.getStyle(null, this.region);
/*  538 */         return localGTKStyle.getFontForState(null);
/*      */       }
/*      */     }
/*      */     , "CheckBox.margin", localInsetsUIResource1, "CheckBoxMenuItem.arrowIcon", null, "CheckBoxMenuItem.checkIcon", new GTKStyle.GTKLazyValue("com.sun.java.swing.plaf.gtk.GTKIconFactory", "getCheckBoxMenuItemCheckIcon"), "CheckBoxMenuItem.font", new UIDefaults.LazyValue()
/*      */     {
/*      */       private Region region;
/*      */ 
/*      */       public Object createValue(UIDefaults paramAnonymousUIDefaults)
/*      */       {
/*  536 */         GTKStyleFactory localGTKStyleFactory = (GTKStyleFactory)SynthLookAndFeel.getStyleFactory();
/*  537 */         GTKStyle localGTKStyle = (GTKStyle)localGTKStyleFactory.getStyle(null, this.region);
/*  538 */         return localGTKStyle.getFontForState(null);
/*      */       }
/*      */     }
/*      */     , "CheckBoxMenuItem.margin", localInsetsUIResource1, "CheckBoxMenuItem.alignAcceleratorText", Boolean.FALSE, "ColorChooser.showPreviewPanelText", Boolean.FALSE, "ColorChooser.panels", new UIDefaults.ActiveValue()
/*      */     {
/*      */       public Object createValue(UIDefaults paramAnonymousUIDefaults)
/*      */       {
/*  581 */         return new AbstractColorChooserPanel[] { new GTKColorChooserPanel() };
/*      */       }
/*      */     }
/*      */     , "ColorChooser.font", new UIDefaults.LazyValue()
/*      */     {
/*      */       private Region region;
/*      */ 
/*      */       public Object createValue(UIDefaults paramAnonymousUIDefaults)
/*      */       {
/*  536 */         GTKStyleFactory localGTKStyleFactory = (GTKStyleFactory)SynthLookAndFeel.getStyleFactory();
/*  537 */         GTKStyle localGTKStyle = (GTKStyle)localGTKStyleFactory.getStyle(null, this.region);
/*  538 */         return localGTKStyle.getFontForState(null);
/*      */       }
/*      */     }
/*      */     , "ComboBox.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ESCAPE", "hidePopup", "PAGE_UP", "pageUpPassThrough", "PAGE_DOWN", "pageDownPassThrough", "HOME", "homePassThrough", "END", "endPassThrough", "DOWN", "selectNext", "KP_DOWN", "selectNext", "alt DOWN", "togglePopup", "alt KP_DOWN", "togglePopup", "alt UP", "togglePopup", "alt KP_UP", "togglePopup", "SPACE", "spacePopup", "ENTER", "enterPressed", "UP", "selectPrevious", "KP_UP", "selectPrevious" }), "ComboBox.font", new UIDefaults.LazyValue()
/*      */     {
/*      */       private Region region;
/*      */ 
/*      */       public Object createValue(UIDefaults paramAnonymousUIDefaults)
/*      */       {
/*  536 */         GTKStyleFactory localGTKStyleFactory = (GTKStyleFactory)SynthLookAndFeel.getStyleFactory();
/*  537 */         GTKStyle localGTKStyle = (GTKStyle)localGTKStyleFactory.getStyle(null, this.region);
/*  538 */         return localGTKStyle.getFontForState(null);
/*      */       }
/*      */     }
/*      */     , "ComboBox.isEnterSelectablePopup", Boolean.TRUE, "EditorPane.caretForeground", localColor4, "EditorPane.caretAspectRatio", localDouble, "EditorPane.caretBlinkRate", localInteger2, "EditorPane.margin", localInsetsUIResource2, "EditorPane.focusInputMap", localLazyInputMap3, "EditorPane.font", new UIDefaults.LazyValue()
/*      */     {
/*      */       private Region region;
/*      */ 
/*      */       public Object createValue(UIDefaults paramAnonymousUIDefaults)
/*      */       {
/*  536 */         GTKStyleFactory localGTKStyleFactory = (GTKStyleFactory)SynthLookAndFeel.getStyleFactory();
/*  537 */         GTKStyle localGTKStyle = (GTKStyle)localGTKStyleFactory.getStyle(null, this.region);
/*  538 */         return localGTKStyle.getFontForState(null);
/*      */       }
/*      */     }
/*      */     , "FileChooser.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ESCAPE", "cancelSelection", "ctrl ENTER", "approveSelection" }), "FileChooserUI", "com.sun.java.swing.plaf.gtk.GTKLookAndFeel", "FormattedTextField.caretForeground", localColor4, "FormattedTextField.caretAspectRatio", localDouble, "FormattedTextField.caretBlinkRate", localInteger2, "FormattedTextField.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "ctrl C", "copy-to-clipboard", "ctrl V", "paste-from-clipboard", "ctrl X", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", "shift DELETE", "cut-to-clipboard", "shift LEFT", "selection-backward", "shift KP_LEFT", "selection-backward", "shift RIGHT", "selection-forward", "shift KP_RIGHT", "selection-forward", "ctrl LEFT", "caret-previous-word", "ctrl KP_LEFT", "caret-previous-word", "ctrl RIGHT", "caret-next-word", "ctrl KP_RIGHT", "caret-next-word", "ctrl shift LEFT", "selection-previous-word", "ctrl shift KP_LEFT", "selection-previous-word", "ctrl shift RIGHT", "selection-next-word", "ctrl shift KP_RIGHT", "selection-next-word", "ctrl A", "select-all", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", "shift END", "selection-end-line", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", "ctrl DELETE", "delete-next-word", "ctrl BACK_SPACE", "delete-previous-word", "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", "KP_LEFT", "caret-backward", "ENTER", "notify-field-accept", "ctrl BACK_SLASH", "unselect", "control shift O", "toggle-componentOrientation", "ESCAPE", "reset-field-edit", "UP", "increment", "KP_UP", "increment", "DOWN", "decrement", "KP_DOWN", "decrement" }), "FormattedTextField.font", new UIDefaults.LazyValue()
/*      */     {
/*      */       private Region region;
/*      */ 
/*      */       public Object createValue(UIDefaults paramAnonymousUIDefaults)
/*      */       {
/*  536 */         GTKStyleFactory localGTKStyleFactory = (GTKStyleFactory)SynthLookAndFeel.getStyleFactory();
/*  537 */         GTKStyle localGTKStyle = (GTKStyle)localGTKStyleFactory.getStyle(null, this.region);
/*  538 */         return localGTKStyle.getFontForState(null);
/*      */       }
/*      */     }
/*      */     , "InternalFrameTitlePane.titlePaneLayout", new GTKStyle.GTKLazyValue("com.sun.java.swing.plaf.gtk.Metacity", "getTitlePaneLayout"), "InternalFrame.windowBindings", { "shift ESCAPE", "showSystemMenu", "ctrl SPACE", "showSystemMenu", "ESCAPE", "hideSystemMenu" }, "InternalFrame.layoutTitlePaneAtOrigin", Boolean.TRUE, "InternalFrame.useTaskBar", Boolean.TRUE, "InternalFrameTitlePane.iconifyButtonOpacity", null, "InternalFrameTitlePane.maximizeButtonOpacity", null, "InternalFrameTitlePane.closeButtonOpacity", null, "Label.font", new UIDefaults.LazyValue()
/*      */     {
/*      */       private Region region;
/*      */ 
/*      */       public Object createValue(UIDefaults paramAnonymousUIDefaults)
/*      */       {
/*  536 */         GTKStyleFactory localGTKStyleFactory = (GTKStyleFactory)SynthLookAndFeel.getStyleFactory();
/*  537 */         GTKStyle localGTKStyle = (GTKStyle)localGTKStyleFactory.getStyle(null, this.region);
/*  538 */         return localGTKStyle.getFontForState(null);
/*      */       }
/*      */     }
/*      */     , "List.background", localColor1, "List.focusCellHighlightBorder", localGTKLazyValue1, "List.focusSelectedCellHighlightBorder", localGTKLazyValue2, "List.noFocusBorder", localGTKLazyValue3, "List.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "ctrl C", "copy", "ctrl V", "paste", "ctrl X", "cut", "COPY", "copy", "PASTE", "paste", "CUT", "cut", "control INSERT", "copy", "shift INSERT", "paste", "shift DELETE", "cut", "UP", "selectPreviousRow", "KP_UP", "selectPreviousRow", "shift UP", "selectPreviousRowExtendSelection", "shift KP_UP", "selectPreviousRowExtendSelection", "ctrl shift UP", "selectPreviousRowExtendSelection", "ctrl shift KP_UP", "selectPreviousRowExtendSelection", "ctrl UP", "selectPreviousRowChangeLead", "ctrl KP_UP", "selectPreviousRowChangeLead", "DOWN", "selectNextRow", "KP_DOWN", "selectNextRow", "shift DOWN", "selectNextRowExtendSelection", "shift KP_DOWN", "selectNextRowExtendSelection", "ctrl shift DOWN", "selectNextRowExtendSelection", "ctrl shift KP_DOWN", "selectNextRowExtendSelection", "ctrl DOWN", "selectNextRowChangeLead", "ctrl KP_DOWN", "selectNextRowChangeLead", "LEFT", "selectPreviousColumn", "KP_LEFT", "selectPreviousColumn", "shift LEFT", "selectPreviousColumnExtendSelection", "shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl shift LEFT", "selectPreviousColumnExtendSelection", "ctrl shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl LEFT", "selectPreviousColumnChangeLead", "ctrl KP_LEFT", "selectPreviousColumnChangeLead", "RIGHT", "selectNextColumn", "KP_RIGHT", "selectNextColumn", "shift RIGHT", "selectNextColumnExtendSelection", "shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl shift RIGHT", "selectNextColumnExtendSelection", "ctrl shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl RIGHT", "selectNextColumnChangeLead", "ctrl KP_RIGHT", "selectNextColumnChangeLead", "HOME", "selectFirstRow", "shift HOME", "selectFirstRowExtendSelection", "ctrl shift HOME", "selectFirstRowExtendSelection", "ctrl HOME", "selectFirstRowChangeLead", "END", "selectLastRow", "shift END", "selectLastRowExtendSelection", "ctrl shift END", "selectLastRowExtendSelection", "ctrl END", "selectLastRowChangeLead", "PAGE_UP", "scrollUp", "shift PAGE_UP", "scrollUpExtendSelection", "ctrl shift PAGE_UP", "scrollUpExtendSelection", "ctrl PAGE_UP", "scrollUpChangeLead", "PAGE_DOWN", "scrollDown", "shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl PAGE_DOWN", "scrollDownChangeLead", "ctrl A", "selectAll", "ctrl SLASH", "selectAll", "ctrl BACK_SLASH", "clearSelection", "SPACE", "addToSelection", "ctrl SPACE", "toggleAndAnchor", "shift SPACE", "extendTo", "ctrl shift SPACE", "moveSelectionTo" }), "List.focusInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[] { "LEFT", "selectNextColumn", "KP_LEFT", "selectNextColumn", "shift LEFT", "selectNextColumnExtendSelection", "shift KP_LEFT", "selectNextColumnExtendSelection", "ctrl shift LEFT", "selectNextColumnExtendSelection", "ctrl shift KP_LEFT", "selectNextColumnExtendSelection", "ctrl LEFT", "selectNextColumnChangeLead", "ctrl KP_LEFT", "selectNextColumnChangeLead", "RIGHT", "selectPreviousColumn", "KP_RIGHT", "selectPreviousColumn", "shift RIGHT", "selectPreviousColumnExtendSelection", "shift KP_RIGHT", "selectPreviousColumnExtendSelection", "ctrl shift RIGHT", "selectPreviousColumnExtendSelection", "ctrl shift KP_RIGHT", "selectPreviousColumnExtendSelection", "ctrl RIGHT", "selectPreviousColumnChangeLead", "ctrl KP_RIGHT", "selectPreviousColumnChangeLead" }), "List.font", new UIDefaults.LazyValue()
/*      */     {
/*      */       private Region region;
/*      */ 
/*      */       public Object createValue(UIDefaults paramAnonymousUIDefaults)
/*      */       {
/*  536 */         GTKStyleFactory localGTKStyleFactory = (GTKStyleFactory)SynthLookAndFeel.getStyleFactory();
/*  537 */         GTKStyle localGTKStyle = (GTKStyle)localGTKStyleFactory.getStyle(null, this.region);
/*  538 */         return localGTKStyle.getFontForState(null);
/*      */       }
/*      */     }
/*      */     , "List.rendererUseUIBorder", Boolean.FALSE, "Menu.shortcutKeys", { 8 }, "Menu.arrowIcon", new GTKStyle.GTKLazyValue("com.sun.java.swing.plaf.gtk.GTKIconFactory", "getMenuArrowIcon"), "Menu.checkIcon", null, "Menu.font", new UIDefaults.LazyValue()
/*      */     {
/*      */       private Region region;
/*      */ 
/*      */       public Object createValue(UIDefaults paramAnonymousUIDefaults)
/*      */       {
/*  536 */         GTKStyleFactory localGTKStyleFactory = (GTKStyleFactory)SynthLookAndFeel.getStyleFactory();
/*  537 */         GTKStyle localGTKStyle = (GTKStyle)localGTKStyleFactory.getStyle(null, this.region);
/*  538 */         return localGTKStyle.getFontForState(null);
/*      */       }
/*      */     }
/*      */     , "Menu.margin", localInsetsUIResource1, "Menu.cancelMode", "hideMenuTree", "Menu.alignAcceleratorText", Boolean.FALSE, "Menu.useMenuBarForTopLevelMenus", Boolean.TRUE, "MenuBar.windowBindings", { "F10", "takeFocus" }, "MenuBar.font", new UIDefaults.LazyValue()
/*      */     {
/*      */       private Region region;
/*      */ 
/*      */       public Object createValue(UIDefaults paramAnonymousUIDefaults)
/*      */       {
/*  536 */         GTKStyleFactory localGTKStyleFactory = (GTKStyleFactory)SynthLookAndFeel.getStyleFactory();
/*  537 */         GTKStyle localGTKStyle = (GTKStyle)localGTKStyleFactory.getStyle(null, this.region);
/*  538 */         return localGTKStyle.getFontForState(null);
/*      */       }
/*      */     }
/*      */     , "MenuItem.arrowIcon", null, "MenuItem.checkIcon", null, "MenuItem.font", new UIDefaults.LazyValue()
/*      */     {
/*      */       private Region region;
/*      */ 
/*      */       public Object createValue(UIDefaults paramAnonymousUIDefaults)
/*      */       {
/*  536 */         GTKStyleFactory localGTKStyleFactory = (GTKStyleFactory)SynthLookAndFeel.getStyleFactory();
/*  537 */         GTKStyle localGTKStyle = (GTKStyle)localGTKStyleFactory.getStyle(null, this.region);
/*  538 */         return localGTKStyle.getFontForState(null);
/*      */       }
/*      */     }
/*      */     , "MenuItem.margin", localInsetsUIResource1, "MenuItem.alignAcceleratorText", Boolean.FALSE, "OptionPane.setButtonMargin", Boolean.FALSE, "OptionPane.sameSizeButtons", Boolean.TRUE, "OptionPane.buttonOrientation", new Integer(4), "OptionPane.minimumSize", new DimensionUIResource(262, 90), "OptionPane.buttonPadding", new Integer(10), "OptionPane.windowBindings", { "ESCAPE", "close" }, "OptionPane.buttonClickThreshhold", new Integer(500), "OptionPane.isYesLast", Boolean.TRUE, "OptionPane.font", new UIDefaults.LazyValue()
/*      */     {
/*      */       private Region region;
/*      */ 
/*      */       public Object createValue(UIDefaults paramAnonymousUIDefaults)
/*      */       {
/*  536 */         GTKStyleFactory localGTKStyleFactory = (GTKStyleFactory)SynthLookAndFeel.getStyleFactory();
/*  537 */         GTKStyle localGTKStyle = (GTKStyle)localGTKStyleFactory.getStyle(null, this.region);
/*  538 */         return localGTKStyle.getFontForState(null);
/*      */       }
/*      */     }
/*      */     , "Panel.font", new UIDefaults.LazyValue()
/*      */     {
/*      */       private Region region;
/*      */ 
/*      */       public Object createValue(UIDefaults paramAnonymousUIDefaults)
/*      */       {
/*  536 */         GTKStyleFactory localGTKStyleFactory = (GTKStyleFactory)SynthLookAndFeel.getStyleFactory();
/*  537 */         GTKStyle localGTKStyle = (GTKStyle)localGTKStyleFactory.getStyle(null, this.region);
/*  538 */         return localGTKStyle.getFontForState(null);
/*      */       }
/*      */     }
/*      */     , "PasswordField.caretForeground", localColor4, "PasswordField.caretAspectRatio", localDouble, "PasswordField.caretBlinkRate", localInteger2, "PasswordField.margin", localInsetsUIResource1, "PasswordField.focusInputMap", localLazyInputMap2, "PasswordField.font", new UIDefaults.LazyValue()
/*      */     {
/*      */       private Region region;
/*      */ 
/*      */       public Object createValue(UIDefaults paramAnonymousUIDefaults)
/*      */       {
/*  536 */         GTKStyleFactory localGTKStyleFactory = (GTKStyleFactory)SynthLookAndFeel.getStyleFactory();
/*  537 */         GTKStyle localGTKStyle = (GTKStyle)localGTKStyleFactory.getStyle(null, this.region);
/*  538 */         return localGTKStyle.getFontForState(null);
/*      */       }
/*      */     }
/*      */     , "PopupMenu.consumeEventOnClose", Boolean.TRUE, "PopupMenu.selectedWindowInputMapBindings", { "ESCAPE", "cancel", "DOWN", "selectNext", "KP_DOWN", "selectNext", "UP", "selectPrevious", "KP_UP", "selectPrevious", "LEFT", "selectParent", "KP_LEFT", "selectParent", "RIGHT", "selectChild", "KP_RIGHT", "selectChild", "ENTER", "return", "SPACE", "return" }, "PopupMenu.selectedWindowInputMapBindings.RightToLeft", { "LEFT", "selectChild", "KP_LEFT", "selectChild", "RIGHT", "selectParent", "KP_RIGHT", "selectParent" }, "PopupMenu.font", new UIDefaults.LazyValue()
/*      */     {
/*      */       private Region region;
/*      */ 
/*      */       public Object createValue(UIDefaults paramAnonymousUIDefaults)
/*      */       {
/*  536 */         GTKStyleFactory localGTKStyleFactory = (GTKStyleFactory)SynthLookAndFeel.getStyleFactory();
/*  537 */         GTKStyle localGTKStyle = (GTKStyle)localGTKStyleFactory.getStyle(null, this.region);
/*  538 */         return localGTKStyle.getFontForState(null);
/*      */       }
/*      */     }
/*      */     , "ProgressBar.horizontalSize", new DimensionUIResource(k, m), "ProgressBar.verticalSize", new DimensionUIResource(n, i1), "ProgressBar.font", new UIDefaults.LazyValue()
/*      */     {
/*      */       private Region region;
/*      */ 
/*      */       public Object createValue(UIDefaults paramAnonymousUIDefaults)
/*      */       {
/*  536 */         GTKStyleFactory localGTKStyleFactory = (GTKStyleFactory)SynthLookAndFeel.getStyleFactory();
/*  537 */         GTKStyle localGTKStyle = (GTKStyle)localGTKStyleFactory.getStyle(null, this.region);
/*  538 */         return localGTKStyle.getFontForState(null);
/*      */       }
/*      */     }
/*      */     , "RadioButton.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "SPACE", "pressed", "released SPACE", "released", "RETURN", "pressed" }), "RadioButton.icon", new GTKStyle.GTKLazyValue("com.sun.java.swing.plaf.gtk.GTKIconFactory", "getRadioButtonIcon"), "RadioButton.font", new UIDefaults.LazyValue()
/*      */     {
/*      */       private Region region;
/*      */ 
/*      */       public Object createValue(UIDefaults paramAnonymousUIDefaults)
/*      */       {
/*  536 */         GTKStyleFactory localGTKStyleFactory = (GTKStyleFactory)SynthLookAndFeel.getStyleFactory();
/*  537 */         GTKStyle localGTKStyle = (GTKStyle)localGTKStyleFactory.getStyle(null, this.region);
/*  538 */         return localGTKStyle.getFontForState(null);
/*      */       }
/*      */     }
/*      */     , "RadioButton.margin", localInsetsUIResource1, "RadioButtonMenuItem.arrowIcon", null, "RadioButtonMenuItem.checkIcon", new GTKStyle.GTKLazyValue("com.sun.java.swing.plaf.gtk.GTKIconFactory", "getRadioButtonMenuItemCheckIcon"), "RadioButtonMenuItem.font", new UIDefaults.LazyValue()
/*      */     {
/*      */       private Region region;
/*      */ 
/*      */       public Object createValue(UIDefaults paramAnonymousUIDefaults)
/*      */       {
/*  536 */         GTKStyleFactory localGTKStyleFactory = (GTKStyleFactory)SynthLookAndFeel.getStyleFactory();
/*  537 */         GTKStyle localGTKStyle = (GTKStyle)localGTKStyleFactory.getStyle(null, this.region);
/*  538 */         return localGTKStyle.getFontForState(null);
/*      */       }
/*      */     }
/*      */     , "RadioButtonMenuItem.margin", localInsetsUIResource1, "RadioButtonMenuItem.alignAcceleratorText", Boolean.FALSE, "RootPane.defaultButtonWindowKeyBindings", { "ENTER", "press", "released ENTER", "release", "ctrl ENTER", "press", "ctrl released ENTER", "release" }, "ScrollBar.squareButtons", Boolean.FALSE, "ScrollBar.thumbHeight", Integer.valueOf(14), "ScrollBar.width", Integer.valueOf(16), "ScrollBar.minimumThumbSize", new Dimension(8, 8), "ScrollBar.maximumThumbSize", new Dimension(4096, 4096), "ScrollBar.allowsAbsolutePositioning", Boolean.TRUE, "ScrollBar.alwaysShowThumb", Boolean.TRUE, "ScrollBar.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "RIGHT", "positiveUnitIncrement", "KP_RIGHT", "positiveUnitIncrement", "DOWN", "positiveUnitIncrement", "KP_DOWN", "positiveUnitIncrement", "PAGE_DOWN", "positiveBlockIncrement", "LEFT", "negativeUnitIncrement", "KP_LEFT", "negativeUnitIncrement", "UP", "negativeUnitIncrement", "KP_UP", "negativeUnitIncrement", "PAGE_UP", "negativeBlockIncrement", "HOME", "minScroll", "END", "maxScroll" }), "ScrollBar.ancestorInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[] { "RIGHT", "negativeUnitIncrement", "KP_RIGHT", "negativeUnitIncrement", "LEFT", "positiveUnitIncrement", "KP_LEFT", "positiveUnitIncrement" }), "Spinner.disableOnBoundaryValues", Boolean.TRUE, "ScrollPane.fillUpperCorner", Boolean.TRUE, "ScrollPane.fillLowerCorner", Boolean.TRUE, "ScrollPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "RIGHT", "unitScrollRight", "KP_RIGHT", "unitScrollRight", "DOWN", "unitScrollDown", "KP_DOWN", "unitScrollDown", "LEFT", "unitScrollLeft", "KP_LEFT", "unitScrollLeft", "UP", "unitScrollUp", "KP_UP", "unitScrollUp", "PAGE_UP", "scrollUp", "PAGE_DOWN", "scrollDown", "ctrl PAGE_UP", "scrollLeft", "ctrl PAGE_DOWN", "scrollRight", "ctrl HOME", "scrollHome", "ctrl END", "scrollEnd" }), "ScrollPane.ancestorInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[] { "ctrl PAGE_UP", "scrollRight", "ctrl PAGE_DOWN", "scrollLeft" }), "ScrollPane.font", new UIDefaults.LazyValue()
/*      */     {
/*      */       private Region region;
/*      */ 
/*      */       public Object createValue(UIDefaults paramAnonymousUIDefaults)
/*      */       {
/*  536 */         GTKStyleFactory localGTKStyleFactory = (GTKStyleFactory)SynthLookAndFeel.getStyleFactory();
/*  537 */         GTKStyle localGTKStyle = (GTKStyle)localGTKStyleFactory.getStyle(null, this.region);
/*  538 */         return localGTKStyle.getFontForState(null);
/*      */       }
/*      */     }
/*      */     , "Separator.insets", localInsetsUIResource1, "Separator.thickness", Integer.valueOf(2), "Slider.paintValue", Boolean.TRUE, "Slider.thumbWidth", Integer.valueOf(30), "Slider.thumbHeight", Integer.valueOf(14), "Slider.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "RIGHT", "positiveUnitIncrement", "KP_RIGHT", "positiveUnitIncrement", "DOWN", "negativeUnitIncrement", "KP_DOWN", "negativeUnitIncrement", "PAGE_DOWN", "negativeBlockIncrement", "LEFT", "negativeUnitIncrement", "KP_LEFT", "negativeUnitIncrement", "UP", "positiveUnitIncrement", "KP_UP", "positiveUnitIncrement", "PAGE_UP", "positiveBlockIncrement", "HOME", "minScroll", "END", "maxScroll" }), "Slider.focusInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[] { "RIGHT", "negativeUnitIncrement", "KP_RIGHT", "negativeUnitIncrement", "LEFT", "positiveUnitIncrement", "KP_LEFT", "positiveUnitIncrement" }), "Slider.onlyLeftMouseButtonDrag", Boolean.FALSE, "Spinner.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "UP", "increment", "KP_UP", "increment", "DOWN", "decrement", "KP_DOWN", "decrement" }), "Spinner.font", new UIDefaults.LazyValue()
/*      */     {
/*      */       private Region region;
/*      */ 
/*      */       public Object createValue(UIDefaults paramAnonymousUIDefaults)
/*      */       {
/*  536 */         GTKStyleFactory localGTKStyleFactory = (GTKStyleFactory)SynthLookAndFeel.getStyleFactory();
/*  537 */         GTKStyle localGTKStyle = (GTKStyle)localGTKStyleFactory.getStyle(null, this.region);
/*  538 */         return localGTKStyle.getFontForState(null);
/*      */       }
/*      */     }
/*      */     , "Spinner.editorAlignment", Integer.valueOf(10), "SplitPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "UP", "negativeIncrement", "DOWN", "positiveIncrement", "LEFT", "negativeIncrement", "RIGHT", "positiveIncrement", "KP_UP", "negativeIncrement", "KP_DOWN", "positiveIncrement", "KP_LEFT", "negativeIncrement", "KP_RIGHT", "positiveIncrement", "HOME", "selectMin", "END", "selectMax", "F8", "startResize", "F6", "toggleFocus", "ctrl TAB", "focusOutForward", "ctrl shift TAB", "focusOutBackward" }), "SplitPane.size", Integer.valueOf(7), "SplitPane.oneTouchOffset", Integer.valueOf(2), "SplitPane.oneTouchButtonSize", Integer.valueOf(5), "SplitPane.supportsOneTouchButtons", Boolean.FALSE, "TabbedPane.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "RIGHT", "navigateRight", "KP_RIGHT", "navigateRight", "LEFT", "navigateLeft", "KP_LEFT", "navigateLeft", "UP", "navigateUp", "KP_UP", "navigateUp", "DOWN", "navigateDown", "KP_DOWN", "navigateDown", "ctrl DOWN", "requestFocusForVisibleComponent", "ctrl KP_DOWN", "requestFocusForVisibleComponent", "SPACE", "selectTabWithFocus" }), "TabbedPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ctrl TAB", "navigateNext", "ctrl shift TAB", "navigatePrevious", "ctrl PAGE_DOWN", "navigatePageDown", "ctrl PAGE_UP", "navigatePageUp", "ctrl UP", "requestFocus", "ctrl KP_UP", "requestFocus" }), "TabbedPane.labelShift", Integer.valueOf(3), "TabbedPane.selectedLabelShift", Integer.valueOf(3), "TabbedPane.font", new UIDefaults.LazyValue()
/*      */     {
/*      */       private Region region;
/*      */ 
/*      */       public Object createValue(UIDefaults paramAnonymousUIDefaults)
/*      */       {
/*  536 */         GTKStyleFactory localGTKStyleFactory = (GTKStyleFactory)SynthLookAndFeel.getStyleFactory();
/*  537 */         GTKStyle localGTKStyle = (GTKStyle)localGTKStyleFactory.getStyle(null, this.region);
/*  538 */         return localGTKStyle.getFontForState(null);
/*      */       }
/*      */     }
/*      */     , "TabbedPane.selectedTabPadInsets", new InsetsUIResource(2, 2, 0, 1), "Table.scrollPaneBorder", localSwingLazyValue, "Table.background", localColor1, "Table.focusCellBackground", localColor2, "Table.focusCellForeground", localColor3, "Table.focusCellHighlightBorder", localGTKLazyValue1, "Table.focusSelectedCellHighlightBorder", localGTKLazyValue2, "Table.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ctrl C", "copy", "ctrl V", "paste", "ctrl X", "cut", "COPY", "copy", "PASTE", "paste", "CUT", "cut", "control INSERT", "copy", "shift INSERT", "paste", "shift DELETE", "cut", "RIGHT", "selectNextColumn", "KP_RIGHT", "selectNextColumn", "shift RIGHT", "selectNextColumnExtendSelection", "shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl shift RIGHT", "selectNextColumnExtendSelection", "ctrl shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl RIGHT", "selectNextColumnChangeLead", "ctrl KP_RIGHT", "selectNextColumnChangeLead", "LEFT", "selectPreviousColumn", "KP_LEFT", "selectPreviousColumn", "shift LEFT", "selectPreviousColumnExtendSelection", "shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl shift LEFT", "selectPreviousColumnExtendSelection", "ctrl shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl LEFT", "selectPreviousColumnChangeLead", "ctrl KP_LEFT", "selectPreviousColumnChangeLead", "DOWN", "selectNextRow", "KP_DOWN", "selectNextRow", "shift DOWN", "selectNextRowExtendSelection", "shift KP_DOWN", "selectNextRowExtendSelection", "ctrl shift DOWN", "selectNextRowExtendSelection", "ctrl shift KP_DOWN", "selectNextRowExtendSelection", "ctrl DOWN", "selectNextRowChangeLead", "ctrl KP_DOWN", "selectNextRowChangeLead", "UP", "selectPreviousRow", "KP_UP", "selectPreviousRow", "shift UP", "selectPreviousRowExtendSelection", "shift KP_UP", "selectPreviousRowExtendSelection", "ctrl shift UP", "selectPreviousRowExtendSelection", "ctrl shift KP_UP", "selectPreviousRowExtendSelection", "ctrl UP", "selectPreviousRowChangeLead", "ctrl KP_UP", "selectPreviousRowChangeLead", "HOME", "selectFirstColumn", "shift HOME", "selectFirstColumnExtendSelection", "ctrl shift HOME", "selectFirstRowExtendSelection", "ctrl HOME", "selectFirstRow", "END", "selectLastColumn", "shift END", "selectLastColumnExtendSelection", "ctrl shift END", "selectLastRowExtendSelection", "ctrl END", "selectLastRow", "PAGE_UP", "scrollUpChangeSelection", "shift PAGE_UP", "scrollUpExtendSelection", "ctrl shift PAGE_UP", "scrollLeftExtendSelection", "ctrl PAGE_UP", "scrollLeftChangeSelection", "PAGE_DOWN", "scrollDownChangeSelection", "shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl shift PAGE_DOWN", "scrollRightExtendSelection", "ctrl PAGE_DOWN", "scrollRightChangeSelection", "TAB", "selectNextColumnCell", "shift TAB", "selectPreviousColumnCell", "ENTER", "selectNextRowCell", "shift ENTER", "selectPreviousRowCell", "ctrl A", "selectAll", "ctrl SLASH", "selectAll", "ctrl BACK_SLASH", "clearSelection", "ESCAPE", "cancel", "F2", "startEditing", "SPACE", "addToSelection", "ctrl SPACE", "toggleAndAnchor", "shift SPACE", "extendTo", "ctrl shift SPACE", "moveSelectionTo", "F8", "focusHeader" }), "Table.ancestorInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[] { "RIGHT", "selectPreviousColumn", "KP_RIGHT", "selectPreviousColumn", "shift RIGHT", "selectPreviousColumnExtendSelection", "shift KP_RIGHT", "selectPreviousColumnExtendSelection", "ctrl shift RIGHT", "selectPreviousColumnExtendSelection", "ctrl shift KP_RIGHT", "selectPreviousColumnExtendSelection", "shift RIGHT", "selectPreviousColumnChangeLead", "shift KP_RIGHT", "selectPreviousColumnChangeLead", "LEFT", "selectNextColumn", "KP_LEFT", "selectNextColumn", "shift LEFT", "selectNextColumnExtendSelection", "shift KP_LEFT", "selectNextColumnExtendSelection", "ctrl shift LEFT", "selectNextColumnExtendSelection", "ctrl shift KP_LEFT", "selectNextColumnExtendSelection", "ctrl LEFT", "selectNextColumnChangeLead", "ctrl KP_LEFT", "selectNextColumnChangeLead", "ctrl PAGE_UP", "scrollRightChangeSelection", "ctrl PAGE_DOWN", "scrollLeftChangeSelection", "ctrl shift PAGE_UP", "scrollRightExtendSelection", "ctrl shift PAGE_DOWN", "scrollLeftExtendSelection" }), "Table.font", new UIDefaults.LazyValue()
/*      */     {
/*      */       private Region region;
/*      */ 
/*      */       public Object createValue(UIDefaults paramAnonymousUIDefaults)
/*      */       {
/*  536 */         GTKStyleFactory localGTKStyleFactory = (GTKStyleFactory)SynthLookAndFeel.getStyleFactory();
/*  537 */         GTKStyle localGTKStyle = (GTKStyle)localGTKStyleFactory.getStyle(null, this.region);
/*  538 */         return localGTKStyle.getFontForState(null);
/*      */       }
/*      */     }
/*      */     , "Table.ascendingSortIcon", new GTKStyle.GTKLazyValue("com.sun.java.swing.plaf.gtk.GTKIconFactory", "getAscendingSortIcon"), "Table.descendingSortIcon", new GTKStyle.GTKLazyValue("com.sun.java.swing.plaf.gtk.GTKIconFactory", "getDescendingSortIcon"), "TableHeader.font", new UIDefaults.LazyValue()
/*      */     {
/*      */       private Region region;
/*      */ 
/*      */       public Object createValue(UIDefaults paramAnonymousUIDefaults)
/*      */       {
/*  536 */         GTKStyleFactory localGTKStyleFactory = (GTKStyleFactory)SynthLookAndFeel.getStyleFactory();
/*  537 */         GTKStyle localGTKStyle = (GTKStyle)localGTKStyleFactory.getStyle(null, this.region);
/*  538 */         return localGTKStyle.getFontForState(null);
/*      */       }
/*      */     }
/*      */     , "TableHeader.alignSorterArrow", Boolean.TRUE, "TextArea.caretForeground", localColor4, "TextArea.caretAspectRatio", localDouble, "TextArea.caretBlinkRate", localInteger2, "TextArea.margin", localInsetsUIResource1, "TextArea.focusInputMap", localLazyInputMap3, "TextArea.font", new UIDefaults.LazyValue()
/*      */     {
/*      */       private Region region;
/*      */ 
/*      */       public Object createValue(UIDefaults paramAnonymousUIDefaults)
/*      */       {
/*  536 */         GTKStyleFactory localGTKStyleFactory = (GTKStyleFactory)SynthLookAndFeel.getStyleFactory();
/*  537 */         GTKStyle localGTKStyle = (GTKStyle)localGTKStyleFactory.getStyle(null, this.region);
/*  538 */         return localGTKStyle.getFontForState(null);
/*      */       }
/*      */     }
/*      */     , "TextField.caretForeground", localColor4, "TextField.caretAspectRatio", localDouble, "TextField.caretBlinkRate", localInteger2, "TextField.margin", localInsetsUIResource1, "TextField.focusInputMap", localLazyInputMap1, "TextField.font", new UIDefaults.LazyValue()
/*      */     {
/*      */       private Region region;
/*      */ 
/*      */       public Object createValue(UIDefaults paramAnonymousUIDefaults)
/*      */       {
/*  536 */         GTKStyleFactory localGTKStyleFactory = (GTKStyleFactory)SynthLookAndFeel.getStyleFactory();
/*  537 */         GTKStyle localGTKStyle = (GTKStyle)localGTKStyleFactory.getStyle(null, this.region);
/*  538 */         return localGTKStyle.getFontForState(null);
/*      */       }
/*      */     }
/*      */     , "TextPane.caretForeground", localColor4, "TextPane.caretAspectRatio", localDouble, "TextPane.caretBlinkRate", localInteger2, "TextPane.margin", localInsetsUIResource2, "TextPane.focusInputMap", localLazyInputMap3, "TextPane.font", new UIDefaults.LazyValue()
/*      */     {
/*      */       private Region region;
/*      */ 
/*      */       public Object createValue(UIDefaults paramAnonymousUIDefaults)
/*      */       {
/*  536 */         GTKStyleFactory localGTKStyleFactory = (GTKStyleFactory)SynthLookAndFeel.getStyleFactory();
/*  537 */         GTKStyle localGTKStyle = (GTKStyle)localGTKStyleFactory.getStyle(null, this.region);
/*  538 */         return localGTKStyle.getFontForState(null);
/*      */       }
/*      */     }
/*      */     , "TitledBorder.titleColor", localColor5, "TitledBorder.border", new UIDefaults.LazyValue()
/*      */     {
/*      */       public Object createValue(UIDefaults paramAnonymousUIDefaults)
/*      */       {
/* 1193 */         return new GTKPainter.TitledBorder();
/*      */       }
/*      */     }
/*      */     , "ToggleButton.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "SPACE", "pressed", "released SPACE", "released" }), "ToggleButton.font", new UIDefaults.LazyValue()
/*      */     {
/*      */       private Region region;
/*      */ 
/*      */       public Object createValue(UIDefaults paramAnonymousUIDefaults)
/*      */       {
/*  536 */         GTKStyleFactory localGTKStyleFactory = (GTKStyleFactory)SynthLookAndFeel.getStyleFactory();
/*  537 */         GTKStyle localGTKStyle = (GTKStyle)localGTKStyleFactory.getStyle(null, this.region);
/*  538 */         return localGTKStyle.getFontForState(null);
/*      */       }
/*      */     }
/*      */     , "ToggleButton.margin", localInsetsUIResource1, "ToolBar.separatorSize", new DimensionUIResource(10, 10), "ToolBar.handleIcon", new UIDefaults.ActiveValue()
/*      */     {
/*      */       public Object createValue(UIDefaults paramAnonymousUIDefaults)
/*      */       {
/* 1209 */         return GTKIconFactory.getToolBarHandleIcon();
/*      */       }
/*      */     }
/*      */     , "ToolBar.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "UP", "navigateUp", "KP_UP", "navigateUp", "DOWN", "navigateDown", "KP_DOWN", "navigateDown", "LEFT", "navigateLeft", "KP_LEFT", "navigateLeft", "RIGHT", "navigateRight", "KP_RIGHT", "navigateRight" }), "ToolBar.font", new UIDefaults.LazyValue()
/*      */     {
/*      */       private Region region;
/*      */ 
/*      */       public Object createValue(UIDefaults paramAnonymousUIDefaults)
/*      */       {
/*  536 */         GTKStyleFactory localGTKStyleFactory = (GTKStyleFactory)SynthLookAndFeel.getStyleFactory();
/*  537 */         GTKStyle localGTKStyle = (GTKStyle)localGTKStyleFactory.getStyle(null, this.region);
/*  538 */         return localGTKStyle.getFontForState(null);
/*      */       }
/*      */     }
/*      */     , "ToolTip.font", new UIDefaults.LazyValue()
/*      */     {
/*      */       private Region region;
/*      */ 
/*      */       public Object createValue(UIDefaults paramAnonymousUIDefaults)
/*      */       {
/*  536 */         GTKStyleFactory localGTKStyleFactory = (GTKStyleFactory)SynthLookAndFeel.getStyleFactory();
/*  537 */         GTKStyle localGTKStyle = (GTKStyle)localGTKStyleFactory.getStyle(null, this.region);
/*  538 */         return localGTKStyle.getFontForState(null);
/*      */       }
/*      */     }
/*      */     , "Tree.padding", Integer.valueOf(4), "Tree.background", localColor1, "Tree.drawHorizontalLines", Boolean.FALSE, "Tree.drawVerticalLines", Boolean.FALSE, "Tree.rowHeight", Integer.valueOf(-1), "Tree.scrollsOnExpand", Boolean.FALSE, "Tree.expanderSize", Integer.valueOf(10), "Tree.repaintWholeRow", Boolean.TRUE, "Tree.closedIcon", null, "Tree.leafIcon", null, "Tree.openIcon", null, "Tree.expandedIcon", new GTKStyle.GTKLazyValue("com.sun.java.swing.plaf.gtk.GTKIconFactory", "getTreeExpandedIcon"), "Tree.collapsedIcon", new GTKStyle.GTKLazyValue("com.sun.java.swing.plaf.gtk.GTKIconFactory", "getTreeCollapsedIcon"), "Tree.leftChildIndent", Integer.valueOf(2), "Tree.rightChildIndent", Integer.valueOf(12), "Tree.scrollsHorizontallyAndVertically", Boolean.FALSE, "Tree.drawsFocusBorder", Boolean.TRUE, "Tree.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "ctrl C", "copy", "ctrl V", "paste", "ctrl X", "cut", "COPY", "copy", "PASTE", "paste", "CUT", "cut", "control INSERT", "copy", "shift INSERT", "paste", "shift DELETE", "cut", "UP", "selectPrevious", "KP_UP", "selectPrevious", "shift UP", "selectPreviousExtendSelection", "shift KP_UP", "selectPreviousExtendSelection", "ctrl shift UP", "selectPreviousExtendSelection", "ctrl shift KP_UP", "selectPreviousExtendSelection", "ctrl UP", "selectPreviousChangeLead", "ctrl KP_UP", "selectPreviousChangeLead", "DOWN", "selectNext", "KP_DOWN", "selectNext", "shift DOWN", "selectNextExtendSelection", "shift KP_DOWN", "selectNextExtendSelection", "ctrl shift DOWN", "selectNextExtendSelection", "ctrl shift KP_DOWN", "selectNextExtendSelection", "ctrl DOWN", "selectNextChangeLead", "ctrl KP_DOWN", "selectNextChangeLead", "RIGHT", "selectChild", "KP_RIGHT", "selectChild", "LEFT", "selectParent", "KP_LEFT", "selectParent", "typed +", "expand", "typed -", "collapse", "BACK_SPACE", "moveSelectionToParent", "PAGE_UP", "scrollUpChangeSelection", "shift PAGE_UP", "scrollUpExtendSelection", "ctrl shift PAGE_UP", "scrollUpExtendSelection", "ctrl PAGE_UP", "scrollUpChangeLead", "PAGE_DOWN", "scrollDownChangeSelection", "shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl PAGE_DOWN", "scrollDownChangeLead", "HOME", "selectFirst", "shift HOME", "selectFirstExtendSelection", "ctrl shift HOME", "selectFirstExtendSelection", "ctrl HOME", "selectFirstChangeLead", "END", "selectLast", "shift END", "selectLastExtendSelection", "ctrl shift END", "selectLastExtendSelection", "ctrl END", "selectLastChangeLead", "F2", "startEditing", "ctrl A", "selectAll", "ctrl SLASH", "selectAll", "ctrl BACK_SLASH", "clearSelection", "ctrl LEFT", "scrollLeft", "ctrl KP_LEFT", "scrollLeft", "ctrl RIGHT", "scrollRight", "ctrl KP_RIGHT", "scrollRight", "SPACE", "addToSelection", "ctrl SPACE", "toggleAndAnchor", "shift SPACE", "extendTo", "ctrl shift SPACE", "moveSelectionTo" }), "Tree.focusInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[] { "RIGHT", "selectParent", "KP_RIGHT", "selectParent", "LEFT", "selectChild", "KP_LEFT", "selectChild" }), "Tree.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ESCAPE", "cancel" }), "Tree.font", new UIDefaults.LazyValue()
/*      */     {
/*      */       private Region region;
/*      */ 
/*      */       public Object createValue(UIDefaults paramAnonymousUIDefaults)
/*      */       {
/*  536 */         GTKStyleFactory localGTKStyleFactory = (GTKStyleFactory)SynthLookAndFeel.getStyleFactory();
/*  537 */         GTKStyle localGTKStyle = (GTKStyle)localGTKStyleFactory.getStyle(null, this.region);
/*  538 */         return localGTKStyle.getFontForState(null);
/*      */       }
/*      */     }
/*      */     , "Viewport.font", new UIDefaults.LazyValue()
/*      */     {
/*      */       private Region region;
/*      */ 
/*      */       public Object createValue(UIDefaults paramAnonymousUIDefaults)
/*      */       {
/*  536 */         GTKStyleFactory localGTKStyleFactory = (GTKStyleFactory)SynthLookAndFeel.getStyleFactory();
/*  537 */         GTKStyle localGTKStyle = (GTKStyle)localGTKStyleFactory.getStyle(null, this.region);
/*  538 */         return localGTKStyle.getFontForState(null);
/*      */       }
/*      */     }
/*      */      };
/* 1326 */     paramUIDefaults.putDefaults(arrayOfObject);
/*      */ 
/* 1328 */     if (this.fallbackFont != null) {
/* 1329 */       paramUIDefaults.put("TitledBorder.font", this.fallbackFont);
/*      */     }
/* 1331 */     paramUIDefaults.put(SwingUtilities2.AA_TEXT_PROPERTY_KEY, aaTextInfo);
/*      */   }
/*      */ 
/*      */   protected void initSystemColorDefaults(UIDefaults paramUIDefaults) {
/* 1335 */     GTKStyleFactory localGTKStyleFactory = (GTKStyleFactory)getStyleFactory();
/* 1336 */     GTKStyle localGTKStyle1 = (GTKStyle)localGTKStyleFactory.getStyle(null, Region.INTERNAL_FRAME);
/*      */ 
/* 1338 */     paramUIDefaults.put("window", localGTKStyle1.getGTKColor(1, GTKColorType.BACKGROUND));
/*      */ 
/* 1340 */     paramUIDefaults.put("windowText", localGTKStyle1.getGTKColor(1, GTKColorType.TEXT_FOREGROUND));
/*      */ 
/* 1343 */     GTKStyle localGTKStyle2 = (GTKStyle)localGTKStyleFactory.getStyle(null, Region.TEXT_FIELD);
/* 1344 */     paramUIDefaults.put("text", localGTKStyle2.getGTKColor(1, GTKColorType.TEXT_BACKGROUND));
/*      */ 
/* 1346 */     paramUIDefaults.put("textText", localGTKStyle2.getGTKColor(1, GTKColorType.TEXT_FOREGROUND));
/*      */ 
/* 1348 */     paramUIDefaults.put("textHighlight", localGTKStyle2.getGTKColor(512, GTKColorType.TEXT_BACKGROUND));
/*      */ 
/* 1351 */     paramUIDefaults.put("textHighlightText", localGTKStyle2.getGTKColor(512, GTKColorType.TEXT_FOREGROUND));
/*      */ 
/* 1354 */     paramUIDefaults.put("textInactiveText", localGTKStyle2.getGTKColor(8, GTKColorType.TEXT_FOREGROUND));
/*      */ 
/* 1357 */     Object localObject = localGTKStyle2.getClassSpecificValue("cursor-color");
/*      */ 
/* 1359 */     if (localObject == null) {
/* 1360 */       localObject = GTKStyle.BLACK_COLOR;
/*      */     }
/* 1362 */     paramUIDefaults.put("caretColor", localObject);
/*      */ 
/* 1364 */     GTKStyle localGTKStyle3 = (GTKStyle)localGTKStyleFactory.getStyle(null, Region.MENU_ITEM);
/* 1365 */     paramUIDefaults.put("menu", localGTKStyle3.getGTKColor(1, GTKColorType.BACKGROUND));
/*      */ 
/* 1367 */     paramUIDefaults.put("menuText", localGTKStyle3.getGTKColor(1, GTKColorType.TEXT_FOREGROUND));
/*      */ 
/* 1370 */     GTKStyle localGTKStyle4 = (GTKStyle)localGTKStyleFactory.getStyle(null, Region.SCROLL_BAR);
/* 1371 */     paramUIDefaults.put("scrollbar", localGTKStyle4.getGTKColor(1, GTKColorType.BACKGROUND));
/*      */ 
/* 1374 */     GTKStyle localGTKStyle5 = (GTKStyle)localGTKStyleFactory.getStyle(null, Region.OPTION_PANE);
/* 1375 */     paramUIDefaults.put("info", localGTKStyle5.getGTKColor(1, GTKColorType.BACKGROUND));
/*      */ 
/* 1377 */     paramUIDefaults.put("infoText", localGTKStyle5.getGTKColor(1, GTKColorType.TEXT_FOREGROUND));
/*      */ 
/* 1380 */     GTKStyle localGTKStyle6 = (GTKStyle)localGTKStyleFactory.getStyle(null, Region.DESKTOP_PANE);
/* 1381 */     paramUIDefaults.put("desktop", localGTKStyle6.getGTKColor(1, GTKColorType.BACKGROUND));
/*      */ 
/* 1388 */     GTKStyle localGTKStyle7 = (GTKStyle)localGTKStyleFactory.getStyle(null, Region.LABEL);
/* 1389 */     Color localColor = localGTKStyle7.getGTKColor(1, GTKColorType.BACKGROUND);
/*      */ 
/* 1391 */     paramUIDefaults.put("control", localColor);
/* 1392 */     paramUIDefaults.put("controlHighlight", localColor);
/* 1393 */     paramUIDefaults.put("controlText", localGTKStyle7.getGTKColor(1, GTKColorType.TEXT_FOREGROUND));
/*      */ 
/* 1395 */     paramUIDefaults.put("controlLtHighlight", localGTKStyle7.getGTKColor(1, GTKColorType.LIGHT));
/*      */ 
/* 1397 */     paramUIDefaults.put("controlShadow", localGTKStyle7.getGTKColor(1, GTKColorType.DARK));
/*      */ 
/* 1399 */     paramUIDefaults.put("controlDkShadow", localGTKStyle7.getGTKColor(1, GTKColorType.BLACK));
/*      */ 
/* 1401 */     paramUIDefaults.put("light", localGTKStyle7.getGTKColor(1, GTKColorType.LIGHT));
/*      */ 
/* 1403 */     paramUIDefaults.put("mid", localGTKStyle7.getGTKColor(1, GTKColorType.MID));
/*      */ 
/* 1405 */     paramUIDefaults.put("dark", localGTKStyle7.getGTKColor(1, GTKColorType.DARK));
/*      */ 
/* 1407 */     paramUIDefaults.put("black", localGTKStyle7.getGTKColor(1, GTKColorType.BLACK));
/*      */ 
/* 1409 */     paramUIDefaults.put("white", localGTKStyle7.getGTKColor(1, GTKColorType.WHITE));
/*      */   }
/*      */ 
/*      */   public static ComponentUI createUI(JComponent paramJComponent)
/*      */   {
/* 1417 */     String str = paramJComponent.getUIClassID().intern();
/*      */ 
/* 1419 */     if (str == "FileChooserUI") {
/* 1420 */       return GTKFileChooserUI.createUI(paramJComponent);
/*      */     }
/* 1422 */     return SynthLookAndFeel.createUI(paramJComponent);
/*      */   }
/*      */ 
/*      */   static String getGtkThemeName()
/*      */   {
/* 1429 */     return gtkThemeName;
/*      */   }
/*      */ 
/*      */   static boolean isLeftToRight(Component paramComponent) {
/* 1433 */     return paramComponent.getComponentOrientation().isLeftToRight();
/*      */   }
/*      */ 
/*      */   public void initialize()
/*      */   {
/* 1444 */     Toolkit localToolkit = Toolkit.getDefaultToolkit();
/* 1445 */     if (((localToolkit instanceof UNIXToolkit)) && (!((UNIXToolkit)localToolkit).loadGTK()))
/*      */     {
/* 1448 */       throw new InternalError("Unable to load native GTK libraries");
/*      */     }
/*      */ 
/* 1451 */     super.initialize();
/* 1452 */     this.inInitialize = true;
/* 1453 */     loadStyles();
/* 1454 */     this.inInitialize = false;
/*      */ 
/* 1470 */     gtkAAFontSettingsCond = (!isSunCJK) && (SwingUtilities2.isLocalDisplay());
/* 1471 */     aaTextInfo = SwingUtilities2.AATextInfo.getAATextInfo(gtkAAFontSettingsCond);
/*      */   }
/*      */ 
/*      */   private static void flushUnreferenced()
/*      */   {
/*      */     WeakPCL localWeakPCL;
/* 1479 */     while ((localWeakPCL = (WeakPCL)queue.poll()) != null)
/* 1480 */       localWeakPCL.dispose();
/*      */   }
/*      */ 
/*      */   public boolean isSupportedLookAndFeel()
/*      */   {
/* 1537 */     Toolkit localToolkit = Toolkit.getDefaultToolkit();
/* 1538 */     return ((localToolkit instanceof SunToolkit)) && (((SunToolkit)localToolkit).isNativeGTKAvailable());
/*      */   }
/*      */ 
/*      */   public boolean isNativeLookAndFeel()
/*      */   {
/* 1543 */     return true;
/*      */   }
/*      */ 
/*      */   public String getDescription() {
/* 1547 */     return "GTK look and feel";
/*      */   }
/*      */ 
/*      */   public String getName() {
/* 1551 */     return "GTK look and feel";
/*      */   }
/*      */ 
/*      */   public String getID() {
/* 1555 */     return "GTK";
/*      */   }
/*      */ 
/*      */   protected void loadSystemColors(UIDefaults paramUIDefaults, String[] paramArrayOfString, boolean paramBoolean)
/*      */   {
/* 1561 */     super.loadSystemColors(paramUIDefaults, paramArrayOfString, false);
/*      */   }
/*      */ 
/*      */   private void loadStyles() {
/* 1565 */     gtkThemeName = (String)Toolkit.getDefaultToolkit().getDesktopProperty("gnome.Net/ThemeName");
/*      */ 
/* 1568 */     setStyleFactory(getGTKStyleFactory());
/*      */ 
/* 1572 */     if (!this.inInitialize) {
/* 1573 */       UIDefaults localUIDefaults = UIManager.getLookAndFeelDefaults();
/* 1574 */       initSystemColorDefaults(localUIDefaults);
/* 1575 */       initComponentDefaults(localUIDefaults);
/*      */     }
/*      */   }
/*      */ 
/*      */   private GTKStyleFactory getGTKStyleFactory()
/*      */   {
/* 1581 */     GTKEngine localGTKEngine = GTKEngine.INSTANCE;
/* 1582 */     Object localObject1 = localGTKEngine.getSetting(GTKEngine.Settings.GTK_ICON_SIZES);
/* 1583 */     if (((localObject1 instanceof String)) && 
/* 1584 */       (!configIconSizes((String)localObject1))) {
/* 1585 */       System.err.println("Error parsing gtk-icon-sizes string: '" + localObject1 + "'");
/*      */     }
/*      */ 
/* 1590 */     Object localObject2 = Toolkit.getDefaultToolkit().getDesktopProperty("gnome.Gtk/FontName");
/*      */ 
/* 1593 */     if (!(localObject2 instanceof String)) {
/* 1594 */       localObject2 = localGTKEngine.getSetting(GTKEngine.Settings.GTK_FONT_NAME);
/* 1595 */       if (!(localObject2 instanceof String)) {
/* 1596 */         localObject2 = "sans 10";
/*      */       }
/*      */     }
/*      */ 
/* 1600 */     if (this.styleFactory == null) {
/* 1601 */       this.styleFactory = new GTKStyleFactory();
/*      */     }
/*      */ 
/* 1604 */     Font localFont = PangoFonts.lookupFont((String)localObject2);
/* 1605 */     this.fallbackFont = localFont;
/* 1606 */     this.styleFactory.initStyles(localFont);
/*      */ 
/* 1608 */     return this.styleFactory;
/*      */   }
/*      */ 
/*      */   private boolean configIconSizes(String paramString) {
/* 1612 */     String[] arrayOfString1 = paramString.split(":");
/* 1613 */     for (int i = 0; i < arrayOfString1.length; i++) {
/* 1614 */       String[] arrayOfString2 = arrayOfString1[i].split("=");
/*      */ 
/* 1616 */       if (arrayOfString2.length != 2) {
/* 1617 */         return false;
/*      */       }
/*      */ 
/* 1620 */       String str1 = arrayOfString2[0].trim().intern();
/* 1621 */       if (str1.length() < 1) {
/* 1622 */         return false;
/*      */       }
/*      */ 
/* 1625 */       arrayOfString2 = arrayOfString2[1].split(",");
/*      */ 
/* 1627 */       if (arrayOfString2.length != 2) {
/* 1628 */         return false;
/*      */       }
/*      */ 
/* 1631 */       String str2 = arrayOfString2[0].trim();
/* 1632 */       String str3 = arrayOfString2[1].trim();
/*      */ 
/* 1634 */       if ((str2.length() < 1) || (str3.length() < 1)) {
/* 1635 */         return false;
/*      */       }
/*      */       int j;
/*      */       int k;
/*      */       try
/*      */       {
/* 1642 */         j = Integer.parseInt(str2);
/* 1643 */         k = Integer.parseInt(str3);
/*      */       } catch (NumberFormatException localNumberFormatException) {
/* 1645 */         return false;
/*      */       }
/*      */ 
/* 1648 */       if ((j > 0) && (k > 0)) {
/* 1649 */         int m = GTKStyle.GTKStockIconInfo.getIconType(str1);
/* 1650 */         GTKStyle.GTKStockIconInfo.setIconSize(m, j, k);
/*      */       } else {
/* 1652 */         System.err.println("Invalid size in gtk-icon-sizes: " + j + "," + k);
/*      */       }
/*      */     }
/*      */ 
/* 1656 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean shouldUpdateStyleOnAncestorChanged()
/*      */   {
/* 1669 */     return true;
/*      */   }
/*      */ 
/*      */   public LayoutStyle getLayoutStyle()
/*      */   {
/* 1676 */     return GnomeLayoutStyle.INSTANCE;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  105 */     gtkThemeName = "Default";
/*      */ 
/*  110 */     String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("swing.gtk.version"));
/*      */ 
/*  112 */     if (str1 != null) {
/*  113 */       IS_22 = str1.equals("2.2");
/*      */     }
/*      */     else {
/*  116 */       IS_22 = true;
/*      */     }
/*      */ 
/*  119 */     String str2 = Locale.getDefault().getLanguage();
/*  120 */     int i = (Locale.CHINESE.getLanguage().equals(str2)) || (Locale.JAPANESE.getLanguage().equals(str2)) || (Locale.KOREAN.getLanguage().equals(str2)) ? 1 : 0;
/*      */ 
/*  125 */     if (i != 0) {
/*  126 */       boolean bool = false;
/*  127 */       switch (sun.awt.OSInfo.getOSType()) {
/*      */       case SOLARIS:
/*  129 */         bool = true;
/*  130 */         break;
/*      */       case LINUX:
/*  133 */         Boolean localBoolean = (Boolean)AccessController.doPrivileged(new PrivilegedAction()
/*      */         {
/*      */           public Boolean run() {
/*  136 */             File localFile = new File("/etc/sun-release");
/*  137 */             return Boolean.valueOf(localFile.exists());
/*      */           }
/*      */         });
/*  140 */         bool = localBoolean.booleanValue();
/*      */       }
/*  142 */       if ((bool) && (!SunGraphicsEnvironment.isOpenSolaris))
/*  143 */         isSunCJK = true;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class GnomeLayoutStyle extends DefaultLayoutStyle
/*      */   {
/* 1688 */     private static GnomeLayoutStyle INSTANCE = new GnomeLayoutStyle();
/*      */ 
/*      */     public int getPreferredGap(JComponent paramJComponent1, JComponent paramJComponent2, LayoutStyle.ComponentPlacement paramComponentPlacement, int paramInt, Container paramContainer)
/*      */     {
/* 1695 */       super.getPreferredGap(paramJComponent1, paramJComponent2, paramComponentPlacement, paramInt, paramContainer);
/*      */ 
/* 1698 */       switch (GTKLookAndFeel.5.$SwitchMap$javax$swing$LayoutStyle$ComponentPlacement[paramComponentPlacement.ordinal()]) {
/*      */       case 1:
/* 1700 */         if ((paramInt == 3) || (paramInt == 7))
/*      */         {
/* 1704 */           return 12;
/*      */         }
/*      */ 
/*      */       case 2:
/* 1719 */         if (isLabelAndNonlabel(paramJComponent1, paramJComponent2, paramInt)) {
/* 1720 */           return 12;
/*      */         }
/* 1722 */         return 6;
/*      */       case 3:
/* 1724 */         return 12;
/*      */       }
/* 1726 */       return 0;
/*      */     }
/*      */ 
/*      */     public int getContainerGap(JComponent paramJComponent, int paramInt, Container paramContainer)
/*      */     {
/* 1733 */       super.getContainerGap(paramJComponent, paramInt, paramContainer);
/*      */ 
/* 1737 */       return 12;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class WeakPCL extends WeakReference<GTKLookAndFeel>
/*      */     implements PropertyChangeListener
/*      */   {
/*      */     private Toolkit kit;
/*      */     private String key;
/*      */ 
/*      */     WeakPCL(GTKLookAndFeel paramGTKLookAndFeel, Toolkit paramToolkit, String paramString)
/*      */     {
/* 1490 */       super(GTKLookAndFeel.queue);
/* 1491 */       this.kit = paramToolkit;
/* 1492 */       this.key = paramString;
/*      */     }
/*      */     public String getKey() {
/* 1495 */       return this.key;
/*      */     }
/*      */     public void propertyChange(final PropertyChangeEvent paramPropertyChangeEvent) {
/* 1498 */       final GTKLookAndFeel localGTKLookAndFeel = (GTKLookAndFeel)get();
/*      */ 
/* 1500 */       if ((localGTKLookAndFeel == null) || (UIManager.getLookAndFeel() != localGTKLookAndFeel))
/*      */       {
/* 1503 */         dispose();
/*      */       }
/*      */       else
/*      */       {
/* 1508 */         SwingUtilities.invokeLater(new Runnable() {
/*      */           public void run() {
/* 1510 */             String str = paramPropertyChangeEvent.getPropertyName();
/*      */ 
/* 1517 */             if ("gnome.Net/ThemeName".equals(str)) {
/* 1518 */               GTKEngine.INSTANCE.themeChanged();
/* 1519 */               GTKIconFactory.resetIcons();
/*      */             }
/* 1521 */             localGTKLookAndFeel.loadStyles();
/* 1522 */             Window[] arrayOfWindow = Window.getWindows();
/* 1523 */             for (int i = 0; i < arrayOfWindow.length; i++)
/* 1524 */               SynthLookAndFeel.updateStyles(arrayOfWindow[i]);
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */ 
/*      */     void dispose()
/*      */     {
/* 1532 */       this.kit.removePropertyChangeListener(this.key, this);
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.java.swing.plaf.gtk.GTKLookAndFeel
 * JD-Core Version:    0.6.2
 */