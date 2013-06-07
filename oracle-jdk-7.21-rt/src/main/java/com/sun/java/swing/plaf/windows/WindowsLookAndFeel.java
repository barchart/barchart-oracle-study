/*      */ package com.sun.java.swing.plaf.windows;
/*      */ 
/*      */ import java.awt.Color;
/*      */ import java.awt.Component;
/*      */ import java.awt.Container;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.Font;
/*      */ import java.awt.FontMetrics;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.HeadlessException;
/*      */ import java.awt.Image;
/*      */ import java.awt.Insets;
/*      */ import java.awt.KeyboardFocusManager;
/*      */ import java.awt.Toolkit;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.image.BufferedImage;
/*      */ import java.awt.image.FilteredImageSource;
/*      */ import java.awt.image.RGBImageFilter;
/*      */ import java.security.AccessController;
/*      */ import javax.swing.AbstractAction;
/*      */ import javax.swing.Action;
/*      */ import javax.swing.BorderFactory;
/*      */ import javax.swing.Icon;
/*      */ import javax.swing.ImageIcon;
/*      */ import javax.swing.JComponent;
/*      */ import javax.swing.JRootPane;
/*      */ import javax.swing.LayoutStyle;
/*      */ import javax.swing.LayoutStyle.ComponentPlacement;
/*      */ import javax.swing.LookAndFeel;
/*      */ import javax.swing.MenuSelectionManager;
/*      */ import javax.swing.UIDefaults;
/*      */ import javax.swing.UIDefaults.ActiveValue;
/*      */ import javax.swing.UIDefaults.LazyInputMap;
/*      */ import javax.swing.UIDefaults.LazyValue;
/*      */ import javax.swing.UIManager;
/*      */ import javax.swing.border.Border;
/*      */ import javax.swing.border.EmptyBorder;
/*      */ import javax.swing.plaf.BorderUIResource.CompoundBorderUIResource;
/*      */ import javax.swing.plaf.ColorUIResource;
/*      */ import javax.swing.plaf.FontUIResource;
/*      */ import javax.swing.plaf.InsetsUIResource;
/*      */ import javax.swing.plaf.UIResource;
/*      */ import javax.swing.plaf.basic.BasicLookAndFeel;
/*      */ import sun.awt.OSInfo;
/*      */ import sun.awt.OSInfo.OSType;
/*      */ import sun.awt.OSInfo.WindowsVersion;
/*      */ import sun.awt.shell.ShellFolder;
/*      */ import sun.font.FontUtilities;
/*      */ import sun.security.action.GetPropertyAction;
/*      */ import sun.swing.DefaultLayoutStyle;
/*      */ import sun.swing.ImageIconUIResource;
/*      */ import sun.swing.StringUIClientPropertyKey;
/*      */ import sun.swing.SwingLazyValue;
/*      */ import sun.swing.SwingUtilities2;
/*      */ import sun.swing.SwingUtilities2.AATextInfo;
/*      */ 
/*      */ public class WindowsLookAndFeel extends BasicLookAndFeel
/*      */ {
/*  103 */   static final Object HI_RES_DISABLED_ICON_CLIENT_KEY = new StringUIClientPropertyKey("WindowsLookAndFeel.generateHiResDisabledIcon");
/*      */   private boolean updatePending;
/*      */   private boolean useSystemFontSettings;
/*      */   private boolean useSystemFontSizeSettings;
/*      */   private DesktopProperty themeActive;
/*      */   private DesktopProperty dllName;
/*      */   private DesktopProperty colorName;
/*      */   private DesktopProperty sizeName;
/*      */   private DesktopProperty aaSettings;
/*      */   private transient LayoutStyle style;
/*      */   private int baseUnitX;
/*      */   private int baseUnitY;
/* 1950 */   private static boolean isMnemonicHidden = true;
/*      */ 
/* 1954 */   private static boolean isClassicWindows = false;
/*      */ 
/*      */   public WindowsLookAndFeel()
/*      */   {
/*  107 */     this.updatePending = false;
/*      */ 
/*  109 */     this.useSystemFontSettings = true;
/*      */   }
/*      */ 
/*      */   public String getName()
/*      */   {
/*  130 */     return "Windows";
/*      */   }
/*      */ 
/*      */   public String getDescription() {
/*  134 */     return "The Microsoft Windows Look and Feel";
/*      */   }
/*      */ 
/*      */   public String getID() {
/*  138 */     return "Windows";
/*      */   }
/*      */ 
/*      */   public boolean isNativeLookAndFeel() {
/*  142 */     return OSInfo.getOSType() == OSInfo.OSType.WINDOWS;
/*      */   }
/*      */ 
/*      */   public boolean isSupportedLookAndFeel() {
/*  146 */     return isNativeLookAndFeel();
/*      */   }
/*      */ 
/*      */   public void initialize() {
/*  150 */     super.initialize();
/*      */ 
/*  155 */     if (OSInfo.getWindowsVersion().compareTo(OSInfo.WINDOWS_95) <= 0) {
/*  156 */       isClassicWindows = true;
/*      */     } else {
/*  158 */       isClassicWindows = false;
/*  159 */       XPStyle.invalidateStyle();
/*      */     }
/*      */ 
/*  166 */     String str = (String)AccessController.doPrivileged(new GetPropertyAction("swing.useSystemFontSettings"));
/*      */ 
/*  168 */     this.useSystemFontSettings = ((str == null) || (Boolean.valueOf(str).booleanValue()));
/*      */ 
/*  171 */     if (this.useSystemFontSettings) {
/*  172 */       Object localObject = UIManager.get("Application.useSystemFontSettings");
/*      */ 
/*  174 */       this.useSystemFontSettings = ((localObject == null) || (Boolean.TRUE.equals(localObject)));
/*      */     }
/*      */ 
/*  177 */     KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor(WindowsRootPaneUI.altProcessor);
/*      */   }
/*      */ 
/*      */   protected void initClassDefaults(UIDefaults paramUIDefaults)
/*      */   {
/*  193 */     super.initClassDefaults(paramUIDefaults);
/*      */ 
/*  197 */     Object[] arrayOfObject = { "ButtonUI", "com.sun.java.swing.plaf.windows.WindowsButtonUI", "CheckBoxUI", "com.sun.java.swing.plaf.windows.WindowsCheckBoxUI", "CheckBoxMenuItemUI", "com.sun.java.swing.plaf.windows.WindowsCheckBoxMenuItemUI", "LabelUI", "com.sun.java.swing.plaf.windows.WindowsLabelUI", "RadioButtonUI", "com.sun.java.swing.plaf.windows.WindowsRadioButtonUI", "RadioButtonMenuItemUI", "com.sun.java.swing.plaf.windows.WindowsRadioButtonMenuItemUI", "ToggleButtonUI", "com.sun.java.swing.plaf.windows.WindowsToggleButtonUI", "ProgressBarUI", "com.sun.java.swing.plaf.windows.WindowsProgressBarUI", "SliderUI", "com.sun.java.swing.plaf.windows.WindowsSliderUI", "SeparatorUI", "com.sun.java.swing.plaf.windows.WindowsSeparatorUI", "SplitPaneUI", "com.sun.java.swing.plaf.windows.WindowsSplitPaneUI", "SpinnerUI", "com.sun.java.swing.plaf.windows.WindowsSpinnerUI", "TabbedPaneUI", "com.sun.java.swing.plaf.windows.WindowsTabbedPaneUI", "TextAreaUI", "com.sun.java.swing.plaf.windows.WindowsTextAreaUI", "TextFieldUI", "com.sun.java.swing.plaf.windows.WindowsTextFieldUI", "PasswordFieldUI", "com.sun.java.swing.plaf.windows.WindowsPasswordFieldUI", "TextPaneUI", "com.sun.java.swing.plaf.windows.WindowsTextPaneUI", "EditorPaneUI", "com.sun.java.swing.plaf.windows.WindowsEditorPaneUI", "TreeUI", "com.sun.java.swing.plaf.windows.WindowsTreeUI", "ToolBarUI", "com.sun.java.swing.plaf.windows.WindowsToolBarUI", "ToolBarSeparatorUI", "com.sun.java.swing.plaf.windows.WindowsToolBarSeparatorUI", "ComboBoxUI", "com.sun.java.swing.plaf.windows.WindowsComboBoxUI", "TableHeaderUI", "com.sun.java.swing.plaf.windows.WindowsTableHeaderUI", "InternalFrameUI", "com.sun.java.swing.plaf.windows.WindowsInternalFrameUI", "DesktopPaneUI", "com.sun.java.swing.plaf.windows.WindowsDesktopPaneUI", "DesktopIconUI", "com.sun.java.swing.plaf.windows.WindowsDesktopIconUI", "FileChooserUI", "com.sun.java.swing.plaf.windows.WindowsFileChooserUI", "MenuUI", "com.sun.java.swing.plaf.windows.WindowsMenuUI", "MenuItemUI", "com.sun.java.swing.plaf.windows.WindowsMenuItemUI", "MenuBarUI", "com.sun.java.swing.plaf.windows.WindowsMenuBarUI", "PopupMenuUI", "com.sun.java.swing.plaf.windows.WindowsPopupMenuUI", "PopupMenuSeparatorUI", "com.sun.java.swing.plaf.windows.WindowsPopupMenuSeparatorUI", "ScrollBarUI", "com.sun.java.swing.plaf.windows.WindowsScrollBarUI", "RootPaneUI", "com.sun.java.swing.plaf.windows.WindowsRootPaneUI" };
/*      */ 
/*  234 */     paramUIDefaults.putDefaults(arrayOfObject);
/*      */   }
/*      */ 
/*      */   protected void initSystemColorDefaults(UIDefaults paramUIDefaults)
/*      */   {
/*  247 */     String[] arrayOfString = { "desktop", "#005C5C", "activeCaption", "#000080", "activeCaptionText", "#FFFFFF", "activeCaptionBorder", "#C0C0C0", "inactiveCaption", "#808080", "inactiveCaptionText", "#C0C0C0", "inactiveCaptionBorder", "#C0C0C0", "window", "#FFFFFF", "windowBorder", "#000000", "windowText", "#000000", "menu", "#C0C0C0", "menuPressedItemB", "#000080", "menuPressedItemF", "#FFFFFF", "menuText", "#000000", "text", "#C0C0C0", "textText", "#000000", "textHighlight", "#000080", "textHighlightText", "#FFFFFF", "textInactiveText", "#808080", "control", "#C0C0C0", "controlText", "#000000", "controlHighlight", "#C0C0C0", "controlLtHighlight", "#FFFFFF", "controlShadow", "#808080", "controlDkShadow", "#000000", "scrollbar", "#E0E0E0", "info", "#FFFFE1", "infoText", "#000000" };
/*      */ 
/*  280 */     loadSystemColors(paramUIDefaults, arrayOfString, isNativeLookAndFeel());
/*      */   }
/*      */ 
/*      */   private void initResourceBundle(UIDefaults paramUIDefaults)
/*      */   {
/*  288 */     paramUIDefaults.addResourceBundle("com.sun.java.swing.plaf.windows.resources.windows");
/*      */   }
/*      */ 
/*      */   protected void initComponentDefaults(UIDefaults paramUIDefaults)
/*      */   {
/*  295 */     super.initComponentDefaults(paramUIDefaults);
/*      */ 
/*  297 */     initResourceBundle(paramUIDefaults);
/*      */ 
/*  300 */     Integer localInteger1 = Integer.valueOf(12);
/*  301 */     Integer localInteger2 = Integer.valueOf(0);
/*  302 */     Integer localInteger3 = Integer.valueOf(1);
/*      */ 
/*  304 */     SwingLazyValue localSwingLazyValue1 = new SwingLazyValue("javax.swing.plaf.FontUIResource", null, new Object[] { "Dialog", localInteger2, localInteger1 });
/*      */ 
/*  309 */     SwingLazyValue localSwingLazyValue2 = new SwingLazyValue("javax.swing.plaf.FontUIResource", null, new Object[] { "SansSerif", localInteger2, localInteger1 });
/*      */ 
/*  313 */     SwingLazyValue localSwingLazyValue3 = new SwingLazyValue("javax.swing.plaf.FontUIResource", null, new Object[] { "Monospaced", localInteger2, localInteger1 });
/*      */ 
/*  317 */     SwingLazyValue localSwingLazyValue4 = new SwingLazyValue("javax.swing.plaf.FontUIResource", null, new Object[] { "Dialog", localInteger3, localInteger1 });
/*      */ 
/*  324 */     ColorUIResource localColorUIResource1 = new ColorUIResource(Color.red);
/*  325 */     ColorUIResource localColorUIResource2 = new ColorUIResource(Color.black);
/*  326 */     ColorUIResource localColorUIResource3 = new ColorUIResource(Color.white);
/*  327 */     ColorUIResource localColorUIResource4 = new ColorUIResource(Color.gray);
/*  328 */     ColorUIResource localColorUIResource5 = new ColorUIResource(Color.darkGray);
/*  329 */     ColorUIResource localColorUIResource6 = localColorUIResource5;
/*      */ 
/*  334 */     isClassicWindows = OSInfo.getWindowsVersion().compareTo(OSInfo.WINDOWS_95) <= 0;
/*      */ 
/*  337 */     Icon localIcon1 = WindowsTreeUI.ExpandedIcon.createExpandedIcon();
/*      */ 
/*  339 */     Icon localIcon2 = WindowsTreeUI.CollapsedIcon.createCollapsedIcon();
/*      */ 
/*  343 */     UIDefaults.LazyInputMap localLazyInputMap1 = new UIDefaults.LazyInputMap(new Object[] { "control C", "copy-to-clipboard", "control V", "paste-from-clipboard", "control X", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", "shift DELETE", "cut-to-clipboard", "control A", "select-all", "control BACK_SLASH", "unselect", "shift LEFT", "selection-backward", "shift RIGHT", "selection-forward", "control LEFT", "caret-previous-word", "control RIGHT", "caret-next-word", "control shift LEFT", "selection-previous-word", "control shift RIGHT", "selection-next-word", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", "shift END", "selection-end-line", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", "ctrl DELETE", "delete-next-word", "ctrl BACK_SPACE", "delete-previous-word", "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", "KP_LEFT", "caret-backward", "ENTER", "notify-field-accept", "control shift O", "toggle-componentOrientation" });
/*      */ 
/*  379 */     UIDefaults.LazyInputMap localLazyInputMap2 = new UIDefaults.LazyInputMap(new Object[] { "control C", "copy-to-clipboard", "control V", "paste-from-clipboard", "control X", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", "shift DELETE", "cut-to-clipboard", "control A", "select-all", "control BACK_SLASH", "unselect", "shift LEFT", "selection-backward", "shift RIGHT", "selection-forward", "control LEFT", "caret-begin-line", "control RIGHT", "caret-end-line", "control shift LEFT", "selection-begin-line", "control shift RIGHT", "selection-end-line", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", "shift END", "selection-end-line", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", "KP_LEFT", "caret-backward", "ENTER", "notify-field-accept", "control shift O", "toggle-componentOrientation" });
/*      */ 
/*  413 */     UIDefaults.LazyInputMap localLazyInputMap3 = new UIDefaults.LazyInputMap(new Object[] { "control C", "copy-to-clipboard", "control V", "paste-from-clipboard", "control X", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", "shift DELETE", "cut-to-clipboard", "shift LEFT", "selection-backward", "shift RIGHT", "selection-forward", "control LEFT", "caret-previous-word", "control RIGHT", "caret-next-word", "control shift LEFT", "selection-previous-word", "control shift RIGHT", "selection-next-word", "control A", "select-all", "control BACK_SLASH", "unselect", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", "shift END", "selection-end-line", "control HOME", "caret-begin", "control END", "caret-end", "control shift HOME", "selection-begin", "control shift END", "selection-end", "UP", "caret-up", "DOWN", "caret-down", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", "ctrl DELETE", "delete-next-word", "ctrl BACK_SPACE", "delete-previous-word", "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", "KP_LEFT", "caret-backward", "PAGE_UP", "page-up", "PAGE_DOWN", "page-down", "shift PAGE_UP", "selection-page-up", "shift PAGE_DOWN", "selection-page-down", "ctrl shift PAGE_UP", "selection-page-left", "ctrl shift PAGE_DOWN", "selection-page-right", "shift UP", "selection-up", "shift DOWN", "selection-down", "ENTER", "insert-break", "TAB", "insert-tab", "control T", "next-link-action", "control shift T", "previous-link-action", "control SPACE", "activate-link-action", "control shift O", "toggle-componentOrientation" });
/*      */ 
/*  467 */     String str = "+";
/*      */ 
/*  469 */     DesktopProperty localDesktopProperty1 = new DesktopProperty("win.3d.backgroundColor", paramUIDefaults.get("control"));
/*      */ 
/*  472 */     DesktopProperty localDesktopProperty2 = new DesktopProperty("win.3d.lightColor", paramUIDefaults.get("controlHighlight"));
/*      */ 
/*  475 */     DesktopProperty localDesktopProperty3 = new DesktopProperty("win.3d.highlightColor", paramUIDefaults.get("controlLtHighlight"));
/*      */ 
/*  478 */     DesktopProperty localDesktopProperty4 = new DesktopProperty("win.3d.shadowColor", paramUIDefaults.get("controlShadow"));
/*      */ 
/*  481 */     DesktopProperty localDesktopProperty5 = new DesktopProperty("win.3d.darkShadowColor", paramUIDefaults.get("controlDkShadow"));
/*      */ 
/*  484 */     DesktopProperty localDesktopProperty6 = new DesktopProperty("win.button.textColor", paramUIDefaults.get("controlText"));
/*      */ 
/*  487 */     DesktopProperty localDesktopProperty7 = new DesktopProperty("win.menu.backgroundColor", paramUIDefaults.get("menu"));
/*      */ 
/*  490 */     DesktopProperty localDesktopProperty8 = new DesktopProperty("win.menubar.backgroundColor", paramUIDefaults.get("menu"));
/*      */ 
/*  493 */     DesktopProperty localDesktopProperty9 = new DesktopProperty("win.menu.textColor", paramUIDefaults.get("menuText"));
/*      */ 
/*  496 */     DesktopProperty localDesktopProperty10 = new DesktopProperty("win.item.highlightColor", paramUIDefaults.get("textHighlight"));
/*      */ 
/*  499 */     DesktopProperty localDesktopProperty11 = new DesktopProperty("win.item.highlightTextColor", paramUIDefaults.get("textHighlightText"));
/*      */ 
/*  502 */     DesktopProperty localDesktopProperty12 = new DesktopProperty("win.frame.backgroundColor", paramUIDefaults.get("window"));
/*      */ 
/*  505 */     DesktopProperty localDesktopProperty13 = new DesktopProperty("win.frame.textColor", paramUIDefaults.get("windowText"));
/*      */ 
/*  508 */     DesktopProperty localDesktopProperty14 = new DesktopProperty("win.frame.sizingBorderWidth", Integer.valueOf(1));
/*      */ 
/*  511 */     DesktopProperty localDesktopProperty15 = new DesktopProperty("win.frame.captionHeight", Integer.valueOf(18));
/*      */ 
/*  514 */     DesktopProperty localDesktopProperty16 = new DesktopProperty("win.frame.captionButtonWidth", Integer.valueOf(16));
/*      */ 
/*  517 */     DesktopProperty localDesktopProperty17 = new DesktopProperty("win.frame.captionButtonHeight", Integer.valueOf(16));
/*      */ 
/*  520 */     DesktopProperty localDesktopProperty18 = new DesktopProperty("win.text.grayedTextColor", paramUIDefaults.get("textInactiveText"));
/*      */ 
/*  523 */     DesktopProperty localDesktopProperty19 = new DesktopProperty("win.scrollbar.backgroundColor", paramUIDefaults.get("scrollbar"));
/*      */ 
/*  527 */     XPColorValue localXPColorValue = new XPColorValue(TMSchema.Part.EP_EDIT, null, TMSchema.Prop.FILLCOLOR, localDesktopProperty12);
/*      */ 
/*  537 */     DesktopProperty localDesktopProperty20 = localDesktopProperty1;
/*  538 */     DesktopProperty localDesktopProperty21 = localDesktopProperty1;
/*      */ 
/*  540 */     Object localObject1 = localSwingLazyValue1;
/*  541 */     Object localObject2 = localSwingLazyValue3;
/*  542 */     Object localObject3 = localSwingLazyValue1;
/*  543 */     Object localObject4 = localSwingLazyValue1;
/*  544 */     Object localObject5 = localSwingLazyValue4;
/*  545 */     Object localObject6 = localSwingLazyValue2;
/*  546 */     Object localObject7 = localObject3;
/*      */ 
/*  548 */     DesktopProperty localDesktopProperty22 = new DesktopProperty("win.scrollbar.width", Integer.valueOf(16));
/*      */ 
/*  550 */     DesktopProperty localDesktopProperty23 = new DesktopProperty("win.menu.height", null);
/*      */ 
/*  552 */     DesktopProperty localDesktopProperty24 = new DesktopProperty("win.item.hotTrackingOn", Boolean.valueOf(true));
/*      */ 
/*  554 */     DesktopProperty localDesktopProperty25 = new DesktopProperty("win.menu.keyboardCuesOn", Boolean.TRUE);
/*      */ 
/*  556 */     if (this.useSystemFontSettings) {
/*  557 */       localObject1 = getDesktopFontValue("win.menu.font", localObject1);
/*  558 */       localObject2 = getDesktopFontValue("win.ansiFixed.font", localObject2);
/*  559 */       localObject3 = getDesktopFontValue("win.defaultGUI.font", localObject3);
/*  560 */       localObject4 = getDesktopFontValue("win.messagebox.font", localObject4);
/*  561 */       localObject5 = getDesktopFontValue("win.frame.captionFont", localObject5);
/*  562 */       localObject7 = getDesktopFontValue("win.icon.font", localObject7);
/*  563 */       localObject6 = getDesktopFontValue("win.tooltip.font", localObject6);
/*      */ 
/*  571 */       localObject8 = SwingUtilities2.AATextInfo.getAATextInfo(true);
/*  572 */       paramUIDefaults.put(SwingUtilities2.AA_TEXT_PROPERTY_KEY, localObject8);
/*  573 */       this.aaSettings = new FontDesktopProperty("awt.font.desktophints");
/*      */     }
/*      */ 
/*  576 */     if (this.useSystemFontSizeSettings) {
/*  577 */       localObject1 = new WindowsFontSizeProperty("win.menu.font.height", "Dialog", 0, 12);
/*  578 */       localObject2 = new WindowsFontSizeProperty("win.ansiFixed.font.height", "Monospaced", 0, 12);
/*      */ 
/*  580 */       localObject3 = new WindowsFontSizeProperty("win.defaultGUI.font.height", "Dialog", 0, 12);
/*  581 */       localObject4 = new WindowsFontSizeProperty("win.messagebox.font.height", "Dialog", 0, 12);
/*  582 */       localObject5 = new WindowsFontSizeProperty("win.frame.captionFont.height", "Dialog", 1, 12);
/*  583 */       localObject6 = new WindowsFontSizeProperty("win.tooltip.font.height", "SansSerif", 0, 12);
/*  584 */       localObject7 = new WindowsFontSizeProperty("win.icon.font.height", "Dialog", 0, 12);
/*      */     }
/*      */ 
/*  588 */     if ((!(this instanceof WindowsClassicLookAndFeel)) && (OSInfo.getOSType() == OSInfo.OSType.WINDOWS) && (OSInfo.getWindowsVersion().compareTo(OSInfo.WINDOWS_XP) >= 0) && (AccessController.doPrivileged(new GetPropertyAction("swing.noxp")) == null))
/*      */     {
/*  595 */       this.themeActive = new TriggerDesktopProperty("win.xpstyle.themeActive");
/*  596 */       this.dllName = new TriggerDesktopProperty("win.xpstyle.dllName");
/*  597 */       this.colorName = new TriggerDesktopProperty("win.xpstyle.colorName");
/*  598 */       this.sizeName = new TriggerDesktopProperty("win.xpstyle.sizeName");
/*      */     }
/*      */ 
/*  602 */     Object localObject8 = { "AuditoryCues.playList", null, "Application.useSystemFontSettings", Boolean.valueOf(this.useSystemFontSettings), "TextField.focusInputMap", localLazyInputMap1, "PasswordField.focusInputMap", localLazyInputMap2, "TextArea.focusInputMap", localLazyInputMap3, "TextPane.focusInputMap", localLazyInputMap3, "EditorPane.focusInputMap", localLazyInputMap3, "Button.font", localObject3, "Button.background", localDesktopProperty1, "Button.foreground", localDesktopProperty6, "Button.shadow", localDesktopProperty4, "Button.darkShadow", localDesktopProperty5, "Button.light", localDesktopProperty2, "Button.highlight", localDesktopProperty3, "Button.disabledForeground", localDesktopProperty18, "Button.disabledShadow", localDesktopProperty3, "Button.focus", localColorUIResource2, "Button.dashedRectGapX", new XPValue(Integer.valueOf(3), Integer.valueOf(5)), "Button.dashedRectGapY", new XPValue(Integer.valueOf(3), Integer.valueOf(4)), "Button.dashedRectGapWidth", new XPValue(Integer.valueOf(6), Integer.valueOf(10)), "Button.dashedRectGapHeight", new XPValue(Integer.valueOf(6), Integer.valueOf(8)), "Button.textShiftOffset", new XPValue(Integer.valueOf(0), Integer.valueOf(1)), "Button.showMnemonics", localDesktopProperty25, "Button.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "SPACE", "pressed", "released SPACE", "released" }), "Caret.width", new DesktopProperty("win.caret.width", null), "CheckBox.font", localObject3, "CheckBox.interiorBackground", localDesktopProperty12, "CheckBox.background", localDesktopProperty1, "CheckBox.foreground", localDesktopProperty13, "CheckBox.shadow", localDesktopProperty4, "CheckBox.darkShadow", localDesktopProperty5, "CheckBox.light", localDesktopProperty2, "CheckBox.highlight", localDesktopProperty3, "CheckBox.focus", localColorUIResource2, "CheckBox.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "SPACE", "pressed", "released SPACE", "released" }), "CheckBox.totalInsets", new Insets(4, 4, 4, 4), "CheckBoxMenuItem.font", localObject1, "CheckBoxMenuItem.background", localDesktopProperty7, "CheckBoxMenuItem.foreground", localDesktopProperty9, "CheckBoxMenuItem.selectionForeground", localDesktopProperty11, "CheckBoxMenuItem.selectionBackground", localDesktopProperty10, "CheckBoxMenuItem.acceleratorForeground", localDesktopProperty9, "CheckBoxMenuItem.acceleratorSelectionForeground", localDesktopProperty11, "CheckBoxMenuItem.commandSound", "win.sound.menuCommand", "ComboBox.font", localObject3, "ComboBox.background", localDesktopProperty12, "ComboBox.foreground", localDesktopProperty13, "ComboBox.buttonBackground", localDesktopProperty1, "ComboBox.buttonShadow", localDesktopProperty4, "ComboBox.buttonDarkShadow", localDesktopProperty5, "ComboBox.buttonHighlight", localDesktopProperty3, "ComboBox.selectionBackground", localDesktopProperty10, "ComboBox.selectionForeground", localDesktopProperty11, "ComboBox.editorBorder", new XPValue(new EmptyBorder(1, 2, 1, 1), new EmptyBorder(1, 4, 1, 4)), "ComboBox.disabledBackground", new XPColorValue(TMSchema.Part.CP_COMBOBOX, TMSchema.State.DISABLED, TMSchema.Prop.FILLCOLOR, localDesktopProperty21), "ComboBox.disabledForeground", new XPColorValue(TMSchema.Part.CP_COMBOBOX, TMSchema.State.DISABLED, TMSchema.Prop.TEXTCOLOR, localDesktopProperty18), "ComboBox.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ESCAPE", "hidePopup", "PAGE_UP", "pageUpPassThrough", "PAGE_DOWN", "pageDownPassThrough", "HOME", "homePassThrough", "END", "endPassThrough", "DOWN", "selectNext2", "KP_DOWN", "selectNext2", "UP", "selectPrevious2", "KP_UP", "selectPrevious2", "ENTER", "enterPressed", "F4", "togglePopup", "alt DOWN", "togglePopup", "alt KP_DOWN", "togglePopup", "alt UP", "togglePopup", "alt KP_UP", "togglePopup" }), "Desktop.background", new DesktopProperty("win.desktop.backgroundColor", paramUIDefaults.get("desktop")), "Desktop.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ctrl F5", "restore", "ctrl F4", "close", "ctrl F7", "move", "ctrl F8", "resize", "RIGHT", "right", "KP_RIGHT", "right", "LEFT", "left", "KP_LEFT", "left", "UP", "up", "KP_UP", "up", "DOWN", "down", "KP_DOWN", "down", "ESCAPE", "escape", "ctrl F9", "minimize", "ctrl F10", "maximize", "ctrl F6", "selectNextFrame", "ctrl TAB", "selectNextFrame", "ctrl alt F6", "selectNextFrame", "shift ctrl alt F6", "selectPreviousFrame", "ctrl F12", "navigateNext", "shift ctrl F12", "navigatePrevious" }), "DesktopIcon.width", Integer.valueOf(160), "EditorPane.font", localObject3, "EditorPane.background", localDesktopProperty12, "EditorPane.foreground", localDesktopProperty13, "EditorPane.selectionBackground", localDesktopProperty10, "EditorPane.selectionForeground", localDesktopProperty11, "EditorPane.caretForeground", localDesktopProperty13, "EditorPane.inactiveForeground", localDesktopProperty18, "EditorPane.inactiveBackground", localDesktopProperty12, "EditorPane.disabledBackground", localDesktopProperty21, "FileChooser.homeFolderIcon", new LazyWindowsIcon(null, "icons/HomeFolder.gif"), "FileChooser.listFont", localObject7, "FileChooser.listViewBackground", new XPColorValue(TMSchema.Part.LVP_LISTVIEW, null, TMSchema.Prop.FILLCOLOR, localDesktopProperty12), "FileChooser.listViewBorder", new XPBorderValue(TMSchema.Part.LVP_LISTVIEW, new SwingLazyValue("javax.swing.plaf.BorderUIResource", "getLoweredBevelBorderUIResource")), "FileChooser.listViewIcon", new LazyWindowsIcon("fileChooserIcon ListView", "icons/ListView.gif"), "FileChooser.listViewWindowsStyle", Boolean.TRUE, "FileChooser.detailsViewIcon", new LazyWindowsIcon("fileChooserIcon DetailsView", "icons/DetailsView.gif"), "FileChooser.viewMenuIcon", new LazyWindowsIcon("fileChooserIcon ViewMenu", "icons/ListView.gif"), "FileChooser.upFolderIcon", new LazyWindowsIcon("fileChooserIcon UpFolder", "icons/UpFolder.gif"), "FileChooser.newFolderIcon", new LazyWindowsIcon("fileChooserIcon NewFolder", "icons/NewFolder.gif"), "FileChooser.useSystemExtensionHiding", Boolean.TRUE, "FileChooser.lookInLabelMnemonic", Integer.valueOf(73), "FileChooser.fileNameLabelMnemonic", Integer.valueOf(78), "FileChooser.filesOfTypeLabelMnemonic", Integer.valueOf(84), "FileChooser.usesSingleFilePane", Boolean.TRUE, "FileChooser.noPlacesBar", new DesktopProperty("win.comdlg.noPlacesBar", Boolean.FALSE), "FileChooser.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ESCAPE", "cancelSelection", "F2", "editFileName", "F5", "refresh", "BACK_SPACE", "Go Up" }), "FileView.directoryIcon", SwingUtilities2.makeIcon(getClass(), WindowsLookAndFeel.class, "icons/Directory.gif"), "FileView.fileIcon", SwingUtilities2.makeIcon(getClass(), WindowsLookAndFeel.class, "icons/File.gif"), "FileView.computerIcon", SwingUtilities2.makeIcon(getClass(), WindowsLookAndFeel.class, "icons/Computer.gif"), "FileView.hardDriveIcon", SwingUtilities2.makeIcon(getClass(), WindowsLookAndFeel.class, "icons/HardDrive.gif"), "FileView.floppyDriveIcon", SwingUtilities2.makeIcon(getClass(), WindowsLookAndFeel.class, "icons/FloppyDrive.gif"), "FormattedTextField.font", localObject3, "InternalFrame.titleFont", localObject5, "InternalFrame.titlePaneHeight", localDesktopProperty15, "InternalFrame.titleButtonWidth", localDesktopProperty16, "InternalFrame.titleButtonHeight", localDesktopProperty17, "InternalFrame.titleButtonToolTipsOn", localDesktopProperty24, "InternalFrame.borderColor", localDesktopProperty1, "InternalFrame.borderShadow", localDesktopProperty4, "InternalFrame.borderDarkShadow", localDesktopProperty5, "InternalFrame.borderHighlight", localDesktopProperty3, "InternalFrame.borderLight", localDesktopProperty2, "InternalFrame.borderWidth", localDesktopProperty14, "InternalFrame.minimizeIconBackground", localDesktopProperty1, "InternalFrame.resizeIconHighlight", localDesktopProperty2, "InternalFrame.resizeIconShadow", localDesktopProperty4, "InternalFrame.activeBorderColor", new DesktopProperty("win.frame.activeBorderColor", paramUIDefaults.get("windowBorder")), "InternalFrame.inactiveBorderColor", new DesktopProperty("win.frame.inactiveBorderColor", paramUIDefaults.get("windowBorder")), "InternalFrame.activeTitleBackground", new DesktopProperty("win.frame.activeCaptionColor", paramUIDefaults.get("activeCaption")), "InternalFrame.activeTitleGradient", new DesktopProperty("win.frame.activeCaptionGradientColor", paramUIDefaults.get("activeCaption")), "InternalFrame.activeTitleForeground", new DesktopProperty("win.frame.captionTextColor", paramUIDefaults.get("activeCaptionText")), "InternalFrame.inactiveTitleBackground", new DesktopProperty("win.frame.inactiveCaptionColor", paramUIDefaults.get("inactiveCaption")), "InternalFrame.inactiveTitleGradient", new DesktopProperty("win.frame.inactiveCaptionGradientColor", paramUIDefaults.get("inactiveCaption")), "InternalFrame.inactiveTitleForeground", new DesktopProperty("win.frame.inactiveCaptionTextColor", paramUIDefaults.get("inactiveCaptionText")), "InternalFrame.maximizeIcon", WindowsIconFactory.createFrameMaximizeIcon(), "InternalFrame.minimizeIcon", WindowsIconFactory.createFrameMinimizeIcon(), "InternalFrame.iconifyIcon", WindowsIconFactory.createFrameIconifyIcon(), "InternalFrame.closeIcon", WindowsIconFactory.createFrameCloseIcon(), "InternalFrame.icon", new SwingLazyValue("com.sun.java.swing.plaf.windows.WindowsInternalFrameTitlePane$ScalableIconUIResource", new Object[][] { { SwingUtilities2.makeIcon(getClass(), BasicLookAndFeel.class, "icons/JavaCup16.png"), SwingUtilities2.makeIcon(getClass(), WindowsLookAndFeel.class, "icons/JavaCup32.png") } }), "InternalFrame.closeSound", "win.sound.close", "InternalFrame.maximizeSound", "win.sound.maximize", "InternalFrame.minimizeSound", "win.sound.minimize", "InternalFrame.restoreDownSound", "win.sound.restoreDown", "InternalFrame.restoreUpSound", "win.sound.restoreUp", "InternalFrame.windowBindings", { "shift ESCAPE", "showSystemMenu", "ctrl SPACE", "showSystemMenu", "ESCAPE", "hideSystemMenu" }, "Label.font", localObject3, "Label.background", localDesktopProperty1, "Label.foreground", localDesktopProperty13, "Label.disabledForeground", localDesktopProperty18, "Label.disabledShadow", localDesktopProperty3, "List.font", localObject3, "List.background", localDesktopProperty12, "List.foreground", localDesktopProperty13, "List.selectionBackground", localDesktopProperty10, "List.selectionForeground", localDesktopProperty11, "List.lockToPositionOnScroll", Boolean.TRUE, "List.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "ctrl C", "copy", "ctrl V", "paste", "ctrl X", "cut", "COPY", "copy", "PASTE", "paste", "CUT", "cut", "control INSERT", "copy", "shift INSERT", "paste", "shift DELETE", "cut", "UP", "selectPreviousRow", "KP_UP", "selectPreviousRow", "shift UP", "selectPreviousRowExtendSelection", "shift KP_UP", "selectPreviousRowExtendSelection", "ctrl shift UP", "selectPreviousRowExtendSelection", "ctrl shift KP_UP", "selectPreviousRowExtendSelection", "ctrl UP", "selectPreviousRowChangeLead", "ctrl KP_UP", "selectPreviousRowChangeLead", "DOWN", "selectNextRow", "KP_DOWN", "selectNextRow", "shift DOWN", "selectNextRowExtendSelection", "shift KP_DOWN", "selectNextRowExtendSelection", "ctrl shift DOWN", "selectNextRowExtendSelection", "ctrl shift KP_DOWN", "selectNextRowExtendSelection", "ctrl DOWN", "selectNextRowChangeLead", "ctrl KP_DOWN", "selectNextRowChangeLead", "LEFT", "selectPreviousColumn", "KP_LEFT", "selectPreviousColumn", "shift LEFT", "selectPreviousColumnExtendSelection", "shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl shift LEFT", "selectPreviousColumnExtendSelection", "ctrl shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl LEFT", "selectPreviousColumnChangeLead", "ctrl KP_LEFT", "selectPreviousColumnChangeLead", "RIGHT", "selectNextColumn", "KP_RIGHT", "selectNextColumn", "shift RIGHT", "selectNextColumnExtendSelection", "shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl shift RIGHT", "selectNextColumnExtendSelection", "ctrl shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl RIGHT", "selectNextColumnChangeLead", "ctrl KP_RIGHT", "selectNextColumnChangeLead", "HOME", "selectFirstRow", "shift HOME", "selectFirstRowExtendSelection", "ctrl shift HOME", "selectFirstRowExtendSelection", "ctrl HOME", "selectFirstRowChangeLead", "END", "selectLastRow", "shift END", "selectLastRowExtendSelection", "ctrl shift END", "selectLastRowExtendSelection", "ctrl END", "selectLastRowChangeLead", "PAGE_UP", "scrollUp", "shift PAGE_UP", "scrollUpExtendSelection", "ctrl shift PAGE_UP", "scrollUpExtendSelection", "ctrl PAGE_UP", "scrollUpChangeLead", "PAGE_DOWN", "scrollDown", "shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl PAGE_DOWN", "scrollDownChangeLead", "ctrl A", "selectAll", "ctrl SLASH", "selectAll", "ctrl BACK_SLASH", "clearSelection", "SPACE", "addToSelection", "ctrl SPACE", "toggleAndAnchor", "shift SPACE", "extendTo", "ctrl shift SPACE", "moveSelectionTo" }), "PopupMenu.font", localObject1, "PopupMenu.background", localDesktopProperty7, "PopupMenu.foreground", localDesktopProperty9, "PopupMenu.popupSound", "win.sound.menuPopup", "PopupMenu.consumeEventOnClose", Boolean.TRUE, "Menu.font", localObject1, "Menu.foreground", localDesktopProperty9, "Menu.background", localDesktopProperty7, "Menu.useMenuBarBackgroundForTopLevel", Boolean.TRUE, "Menu.selectionForeground", localDesktopProperty11, "Menu.selectionBackground", localDesktopProperty10, "Menu.acceleratorForeground", localDesktopProperty9, "Menu.acceleratorSelectionForeground", localDesktopProperty11, "Menu.menuPopupOffsetX", Integer.valueOf(0), "Menu.menuPopupOffsetY", Integer.valueOf(0), "Menu.submenuPopupOffsetX", Integer.valueOf(-4), "Menu.submenuPopupOffsetY", Integer.valueOf(-3), "Menu.crossMenuMnemonic", Boolean.FALSE, "Menu.preserveTopLevelSelection", Boolean.TRUE, "MenuBar.font", localObject1, "MenuBar.background", new XPValue(localDesktopProperty8, localDesktopProperty7), "MenuBar.foreground", localDesktopProperty9, "MenuBar.shadow", localDesktopProperty4, "MenuBar.highlight", localDesktopProperty3, "MenuBar.height", localDesktopProperty23, "MenuBar.rolloverEnabled", localDesktopProperty24, "MenuBar.windowBindings", { "F10", "takeFocus" }, "MenuItem.font", localObject1, "MenuItem.acceleratorFont", localObject1, "MenuItem.foreground", localDesktopProperty9, "MenuItem.background", localDesktopProperty7, "MenuItem.selectionForeground", localDesktopProperty11, "MenuItem.selectionBackground", localDesktopProperty10, "MenuItem.disabledForeground", localDesktopProperty18, "MenuItem.acceleratorForeground", localDesktopProperty9, "MenuItem.acceleratorSelectionForeground", localDesktopProperty11, "MenuItem.acceleratorDelimiter", str, "MenuItem.commandSound", "win.sound.menuCommand", "MenuItem.disabledAreNavigable", Boolean.TRUE, "RadioButton.font", localObject3, "RadioButton.interiorBackground", localDesktopProperty12, "RadioButton.background", localDesktopProperty1, "RadioButton.foreground", localDesktopProperty13, "RadioButton.shadow", localDesktopProperty4, "RadioButton.darkShadow", localDesktopProperty5, "RadioButton.light", localDesktopProperty2, "RadioButton.highlight", localDesktopProperty3, "RadioButton.focus", localColorUIResource2, "RadioButton.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "SPACE", "pressed", "released SPACE", "released" }), "RadioButton.totalInsets", new Insets(4, 4, 4, 4), "RadioButtonMenuItem.font", localObject1, "RadioButtonMenuItem.foreground", localDesktopProperty9, "RadioButtonMenuItem.background", localDesktopProperty7, "RadioButtonMenuItem.selectionForeground", localDesktopProperty11, "RadioButtonMenuItem.selectionBackground", localDesktopProperty10, "RadioButtonMenuItem.disabledForeground", localDesktopProperty18, "RadioButtonMenuItem.acceleratorForeground", localDesktopProperty9, "RadioButtonMenuItem.acceleratorSelectionForeground", localDesktopProperty11, "RadioButtonMenuItem.commandSound", "win.sound.menuCommand", "OptionPane.font", localObject4, "OptionPane.messageFont", localObject4, "OptionPane.buttonFont", localObject4, "OptionPane.background", localDesktopProperty1, "OptionPane.foreground", localDesktopProperty13, "OptionPane.buttonMinimumWidth", new XPDLUValue(50, 50, 3), "OptionPane.messageForeground", localDesktopProperty6, "OptionPane.errorIcon", new LazyWindowsIcon("optionPaneIcon Error", "icons/Error.gif"), "OptionPane.informationIcon", new LazyWindowsIcon("optionPaneIcon Information", "icons/Inform.gif"), "OptionPane.questionIcon", new LazyWindowsIcon("optionPaneIcon Question", "icons/Question.gif"), "OptionPane.warningIcon", new LazyWindowsIcon("optionPaneIcon Warning", "icons/Warn.gif"), "OptionPane.windowBindings", { "ESCAPE", "close" }, "OptionPane.errorSound", "win.sound.hand", "OptionPane.informationSound", "win.sound.asterisk", "OptionPane.questionSound", "win.sound.question", "OptionPane.warningSound", "win.sound.exclamation", "FormattedTextField.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "ctrl C", "copy-to-clipboard", "ctrl V", "paste-from-clipboard", "ctrl X", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", "shift DELETE", "cut-to-clipboard", "shift LEFT", "selection-backward", "shift KP_LEFT", "selection-backward", "shift RIGHT", "selection-forward", "shift KP_RIGHT", "selection-forward", "ctrl LEFT", "caret-previous-word", "ctrl KP_LEFT", "caret-previous-word", "ctrl RIGHT", "caret-next-word", "ctrl KP_RIGHT", "caret-next-word", "ctrl shift LEFT", "selection-previous-word", "ctrl shift KP_LEFT", "selection-previous-word", "ctrl shift RIGHT", "selection-next-word", "ctrl shift KP_RIGHT", "selection-next-word", "ctrl A", "select-all", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", "shift END", "selection-end-line", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", "ctrl DELETE", "delete-next-word", "ctrl BACK_SPACE", "delete-previous-word", "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", "KP_LEFT", "caret-backward", "ENTER", "notify-field-accept", "ctrl BACK_SLASH", "unselect", "control shift O", "toggle-componentOrientation", "ESCAPE", "reset-field-edit", "UP", "increment", "KP_UP", "increment", "DOWN", "decrement", "KP_DOWN", "decrement" }), "FormattedTextField.inactiveBackground", localDesktopProperty20, "FormattedTextField.disabledBackground", localDesktopProperty21, "Panel.font", localObject3, "Panel.background", localDesktopProperty1, "Panel.foreground", localDesktopProperty13, "PasswordField.font", localObject3, "PasswordField.background", localXPColorValue, "PasswordField.foreground", localDesktopProperty13, "PasswordField.inactiveForeground", localDesktopProperty18, "PasswordField.inactiveBackground", localDesktopProperty20, "PasswordField.disabledBackground", localDesktopProperty21, "PasswordField.selectionBackground", localDesktopProperty10, "PasswordField.selectionForeground", localDesktopProperty11, "PasswordField.caretForeground", localDesktopProperty13, "PasswordField.echoChar", new XPValue(new Character('●'), new Character('*')), "ProgressBar.font", localObject3, "ProgressBar.foreground", localDesktopProperty10, "ProgressBar.background", localDesktopProperty1, "ProgressBar.shadow", localDesktopProperty4, "ProgressBar.highlight", localDesktopProperty3, "ProgressBar.selectionForeground", localDesktopProperty1, "ProgressBar.selectionBackground", localDesktopProperty10, "ProgressBar.cellLength", Integer.valueOf(7), "ProgressBar.cellSpacing", Integer.valueOf(2), "ProgressBar.indeterminateInsets", new Insets(3, 3, 3, 3), "RootPane.defaultButtonWindowKeyBindings", { "ENTER", "press", "released ENTER", "release", "ctrl ENTER", "press", "ctrl released ENTER", "release" }, "ScrollBar.background", localDesktopProperty19, "ScrollBar.foreground", localDesktopProperty1, "ScrollBar.track", localColorUIResource3, "ScrollBar.trackForeground", localDesktopProperty19, "ScrollBar.trackHighlight", localColorUIResource2, "ScrollBar.trackHighlightForeground", localColorUIResource6, "ScrollBar.thumb", localDesktopProperty1, "ScrollBar.thumbHighlight", localDesktopProperty3, "ScrollBar.thumbDarkShadow", localDesktopProperty5, "ScrollBar.thumbShadow", localDesktopProperty4, "ScrollBar.width", localDesktopProperty22, "ScrollBar.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "RIGHT", "positiveUnitIncrement", "KP_RIGHT", "positiveUnitIncrement", "DOWN", "positiveUnitIncrement", "KP_DOWN", "positiveUnitIncrement", "PAGE_DOWN", "positiveBlockIncrement", "ctrl PAGE_DOWN", "positiveBlockIncrement", "LEFT", "negativeUnitIncrement", "KP_LEFT", "negativeUnitIncrement", "UP", "negativeUnitIncrement", "KP_UP", "negativeUnitIncrement", "PAGE_UP", "negativeBlockIncrement", "ctrl PAGE_UP", "negativeBlockIncrement", "HOME", "minScroll", "END", "maxScroll" }), "ScrollPane.font", localObject3, "ScrollPane.background", localDesktopProperty1, "ScrollPane.foreground", localDesktopProperty6, "ScrollPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "RIGHT", "unitScrollRight", "KP_RIGHT", "unitScrollRight", "DOWN", "unitScrollDown", "KP_DOWN", "unitScrollDown", "LEFT", "unitScrollLeft", "KP_LEFT", "unitScrollLeft", "UP", "unitScrollUp", "KP_UP", "unitScrollUp", "PAGE_UP", "scrollUp", "PAGE_DOWN", "scrollDown", "ctrl PAGE_UP", "scrollLeft", "ctrl PAGE_DOWN", "scrollRight", "ctrl HOME", "scrollHome", "ctrl END", "scrollEnd" }), "Separator.background", localDesktopProperty3, "Separator.foreground", localDesktopProperty4, "Slider.font", localObject3, "Slider.foreground", localDesktopProperty1, "Slider.background", localDesktopProperty1, "Slider.highlight", localDesktopProperty3, "Slider.shadow", localDesktopProperty4, "Slider.focus", localDesktopProperty5, "Slider.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "RIGHT", "positiveUnitIncrement", "KP_RIGHT", "positiveUnitIncrement", "DOWN", "negativeUnitIncrement", "KP_DOWN", "negativeUnitIncrement", "PAGE_DOWN", "negativeBlockIncrement", "LEFT", "negativeUnitIncrement", "KP_LEFT", "negativeUnitIncrement", "UP", "positiveUnitIncrement", "KP_UP", "positiveUnitIncrement", "PAGE_UP", "positiveBlockIncrement", "HOME", "minScroll", "END", "maxScroll" }), "Spinner.font", localObject3, "Spinner.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "UP", "increment", "KP_UP", "increment", "DOWN", "decrement", "KP_DOWN", "decrement" }), "SplitPane.background", localDesktopProperty1, "SplitPane.highlight", localDesktopProperty3, "SplitPane.shadow", localDesktopProperty4, "SplitPane.darkShadow", localDesktopProperty5, "SplitPane.dividerSize", Integer.valueOf(5), "SplitPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "UP", "negativeIncrement", "DOWN", "positiveIncrement", "LEFT", "negativeIncrement", "RIGHT", "positiveIncrement", "KP_UP", "negativeIncrement", "KP_DOWN", "positiveIncrement", "KP_LEFT", "negativeIncrement", "KP_RIGHT", "positiveIncrement", "HOME", "selectMin", "END", "selectMax", "F8", "startResize", "F6", "toggleFocus", "ctrl TAB", "focusOutForward", "ctrl shift TAB", "focusOutBackward" }), "TabbedPane.tabsOverlapBorder", new XPValue(Boolean.TRUE, Boolean.FALSE), "TabbedPane.tabInsets", new XPValue(new InsetsUIResource(1, 4, 1, 4), new InsetsUIResource(0, 4, 1, 4)), "TabbedPane.tabAreaInsets", new XPValue(new InsetsUIResource(3, 2, 2, 2), new InsetsUIResource(3, 2, 0, 2)), "TabbedPane.font", localObject3, "TabbedPane.background", localDesktopProperty1, "TabbedPane.foreground", localDesktopProperty6, "TabbedPane.highlight", localDesktopProperty3, "TabbedPane.light", localDesktopProperty2, "TabbedPane.shadow", localDesktopProperty4, "TabbedPane.darkShadow", localDesktopProperty5, "TabbedPane.focus", localDesktopProperty6, "TabbedPane.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "RIGHT", "navigateRight", "KP_RIGHT", "navigateRight", "LEFT", "navigateLeft", "KP_LEFT", "navigateLeft", "UP", "navigateUp", "KP_UP", "navigateUp", "DOWN", "navigateDown", "KP_DOWN", "navigateDown", "ctrl DOWN", "requestFocusForVisibleComponent", "ctrl KP_DOWN", "requestFocusForVisibleComponent" }), "TabbedPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ctrl TAB", "navigateNext", "ctrl shift TAB", "navigatePrevious", "ctrl PAGE_DOWN", "navigatePageDown", "ctrl PAGE_UP", "navigatePageUp", "ctrl UP", "requestFocus", "ctrl KP_UP", "requestFocus" }), "Table.font", localObject3, "Table.foreground", localDesktopProperty6, "Table.background", localDesktopProperty12, "Table.highlight", localDesktopProperty3, "Table.light", localDesktopProperty2, "Table.shadow", localDesktopProperty4, "Table.darkShadow", localDesktopProperty5, "Table.selectionForeground", localDesktopProperty11, "Table.selectionBackground", localDesktopProperty10, "Table.gridColor", localColorUIResource4, "Table.focusCellBackground", localDesktopProperty12, "Table.focusCellForeground", localDesktopProperty6, "Table.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ctrl C", "copy", "ctrl V", "paste", "ctrl X", "cut", "COPY", "copy", "PASTE", "paste", "CUT", "cut", "control INSERT", "copy", "shift INSERT", "paste", "shift DELETE", "cut", "RIGHT", "selectNextColumn", "KP_RIGHT", "selectNextColumn", "shift RIGHT", "selectNextColumnExtendSelection", "shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl shift RIGHT", "selectNextColumnExtendSelection", "ctrl shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl RIGHT", "selectNextColumnChangeLead", "ctrl KP_RIGHT", "selectNextColumnChangeLead", "LEFT", "selectPreviousColumn", "KP_LEFT", "selectPreviousColumn", "shift LEFT", "selectPreviousColumnExtendSelection", "shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl shift LEFT", "selectPreviousColumnExtendSelection", "ctrl shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl LEFT", "selectPreviousColumnChangeLead", "ctrl KP_LEFT", "selectPreviousColumnChangeLead", "DOWN", "selectNextRow", "KP_DOWN", "selectNextRow", "shift DOWN", "selectNextRowExtendSelection", "shift KP_DOWN", "selectNextRowExtendSelection", "ctrl shift DOWN", "selectNextRowExtendSelection", "ctrl shift KP_DOWN", "selectNextRowExtendSelection", "ctrl DOWN", "selectNextRowChangeLead", "ctrl KP_DOWN", "selectNextRowChangeLead", "UP", "selectPreviousRow", "KP_UP", "selectPreviousRow", "shift UP", "selectPreviousRowExtendSelection", "shift KP_UP", "selectPreviousRowExtendSelection", "ctrl shift UP", "selectPreviousRowExtendSelection", "ctrl shift KP_UP", "selectPreviousRowExtendSelection", "ctrl UP", "selectPreviousRowChangeLead", "ctrl KP_UP", "selectPreviousRowChangeLead", "HOME", "selectFirstColumn", "shift HOME", "selectFirstColumnExtendSelection", "ctrl shift HOME", "selectFirstRowExtendSelection", "ctrl HOME", "selectFirstRow", "END", "selectLastColumn", "shift END", "selectLastColumnExtendSelection", "ctrl shift END", "selectLastRowExtendSelection", "ctrl END", "selectLastRow", "PAGE_UP", "scrollUpChangeSelection", "shift PAGE_UP", "scrollUpExtendSelection", "ctrl shift PAGE_UP", "scrollLeftExtendSelection", "ctrl PAGE_UP", "scrollLeftChangeSelection", "PAGE_DOWN", "scrollDownChangeSelection", "shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl shift PAGE_DOWN", "scrollRightExtendSelection", "ctrl PAGE_DOWN", "scrollRightChangeSelection", "TAB", "selectNextColumnCell", "shift TAB", "selectPreviousColumnCell", "ENTER", "selectNextRowCell", "shift ENTER", "selectPreviousRowCell", "ctrl A", "selectAll", "ctrl SLASH", "selectAll", "ctrl BACK_SLASH", "clearSelection", "ESCAPE", "cancel", "F2", "startEditing", "SPACE", "addToSelection", "ctrl SPACE", "toggleAndAnchor", "shift SPACE", "extendTo", "ctrl shift SPACE", "moveSelectionTo", "F8", "focusHeader" }), "Table.sortIconHighlight", localDesktopProperty4, "Table.sortIconLight", localColorUIResource3, "TableHeader.font", localObject3, "TableHeader.foreground", localDesktopProperty6, "TableHeader.background", localDesktopProperty1, "TableHeader.focusCellBackground", new XPValue(XPValue.NULL_VALUE, localDesktopProperty12), "TextArea.font", localObject2, "TextArea.background", localDesktopProperty12, "TextArea.foreground", localDesktopProperty13, "TextArea.inactiveForeground", localDesktopProperty18, "TextArea.inactiveBackground", localDesktopProperty12, "TextArea.disabledBackground", localDesktopProperty21, "TextArea.selectionBackground", localDesktopProperty10, "TextArea.selectionForeground", localDesktopProperty11, "TextArea.caretForeground", localDesktopProperty13, "TextField.font", localObject3, "TextField.background", localXPColorValue, "TextField.foreground", localDesktopProperty13, "TextField.shadow", localDesktopProperty4, "TextField.darkShadow", localDesktopProperty5, "TextField.light", localDesktopProperty2, "TextField.highlight", localDesktopProperty3, "TextField.inactiveForeground", localDesktopProperty18, "TextField.inactiveBackground", localDesktopProperty20, "TextField.disabledBackground", localDesktopProperty21, "TextField.selectionBackground", localDesktopProperty10, "TextField.selectionForeground", localDesktopProperty11, "TextField.caretForeground", localDesktopProperty13, "TextPane.font", localObject3, "TextPane.background", localDesktopProperty12, "TextPane.foreground", localDesktopProperty13, "TextPane.selectionBackground", localDesktopProperty10, "TextPane.selectionForeground", localDesktopProperty11, "TextPane.inactiveBackground", localDesktopProperty12, "TextPane.disabledBackground", localDesktopProperty21, "TextPane.caretForeground", localDesktopProperty13, "TitledBorder.font", localObject3, "TitledBorder.titleColor", new XPColorValue(TMSchema.Part.BP_GROUPBOX, null, TMSchema.Prop.TEXTCOLOR, localDesktopProperty13), "ToggleButton.font", localObject3, "ToggleButton.background", localDesktopProperty1, "ToggleButton.foreground", localDesktopProperty6, "ToggleButton.shadow", localDesktopProperty4, "ToggleButton.darkShadow", localDesktopProperty5, "ToggleButton.light", localDesktopProperty2, "ToggleButton.highlight", localDesktopProperty3, "ToggleButton.focus", localDesktopProperty6, "ToggleButton.textShiftOffset", Integer.valueOf(1), "ToggleButton.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "SPACE", "pressed", "released SPACE", "released" }), "ToolBar.font", localObject1, "ToolBar.background", localDesktopProperty1, "ToolBar.foreground", localDesktopProperty6, "ToolBar.shadow", localDesktopProperty4, "ToolBar.darkShadow", localDesktopProperty5, "ToolBar.light", localDesktopProperty2, "ToolBar.highlight", localDesktopProperty3, "ToolBar.dockingBackground", localDesktopProperty1, "ToolBar.dockingForeground", localColorUIResource1, "ToolBar.floatingBackground", localDesktopProperty1, "ToolBar.floatingForeground", localColorUIResource5, "ToolBar.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "UP", "navigateUp", "KP_UP", "navigateUp", "DOWN", "navigateDown", "KP_DOWN", "navigateDown", "LEFT", "navigateLeft", "KP_LEFT", "navigateLeft", "RIGHT", "navigateRight", "KP_RIGHT", "navigateRight" }), "ToolBar.separatorSize", null, "ToolTip.font", localObject6, "ToolTip.background", new DesktopProperty("win.tooltip.backgroundColor", paramUIDefaults.get("info")), "ToolTip.foreground", new DesktopProperty("win.tooltip.textColor", paramUIDefaults.get("infoText")), "ToolTipManager.enableToolTipMode", "activeApplication", "Tree.selectionBorderColor", localColorUIResource2, "Tree.drawDashedFocusIndicator", Boolean.TRUE, "Tree.lineTypeDashed", Boolean.TRUE, "Tree.font", localObject3, "Tree.background", localDesktopProperty12, "Tree.foreground", localDesktopProperty13, "Tree.hash", localColorUIResource4, "Tree.leftChildIndent", Integer.valueOf(8), "Tree.rightChildIndent", Integer.valueOf(11), "Tree.textForeground", localDesktopProperty13, "Tree.textBackground", localDesktopProperty12, "Tree.selectionForeground", localDesktopProperty11, "Tree.selectionBackground", localDesktopProperty10, "Tree.expandedIcon", localIcon1, "Tree.collapsedIcon", localIcon2, "Tree.openIcon", new ActiveWindowsIcon("win.icon.shellIconBPP", "shell32Icon 5", "icons/TreeOpen.gif"), "Tree.closedIcon", new ActiveWindowsIcon("win.icon.shellIconBPP", "shell32Icon 4", "icons/TreeClosed.gif"), "Tree.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "ADD", "expand", "SUBTRACT", "collapse", "ctrl C", "copy", "ctrl V", "paste", "ctrl X", "cut", "COPY", "copy", "PASTE", "paste", "CUT", "cut", "control INSERT", "copy", "shift INSERT", "paste", "shift DELETE", "cut", "UP", "selectPrevious", "KP_UP", "selectPrevious", "shift UP", "selectPreviousExtendSelection", "shift KP_UP", "selectPreviousExtendSelection", "ctrl shift UP", "selectPreviousExtendSelection", "ctrl shift KP_UP", "selectPreviousExtendSelection", "ctrl UP", "selectPreviousChangeLead", "ctrl KP_UP", "selectPreviousChangeLead", "DOWN", "selectNext", "KP_DOWN", "selectNext", "shift DOWN", "selectNextExtendSelection", "shift KP_DOWN", "selectNextExtendSelection", "ctrl shift DOWN", "selectNextExtendSelection", "ctrl shift KP_DOWN", "selectNextExtendSelection", "ctrl DOWN", "selectNextChangeLead", "ctrl KP_DOWN", "selectNextChangeLead", "RIGHT", "selectChild", "KP_RIGHT", "selectChild", "LEFT", "selectParent", "KP_LEFT", "selectParent", "PAGE_UP", "scrollUpChangeSelection", "shift PAGE_UP", "scrollUpExtendSelection", "ctrl shift PAGE_UP", "scrollUpExtendSelection", "ctrl PAGE_UP", "scrollUpChangeLead", "PAGE_DOWN", "scrollDownChangeSelection", "shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl PAGE_DOWN", "scrollDownChangeLead", "HOME", "selectFirst", "shift HOME", "selectFirstExtendSelection", "ctrl shift HOME", "selectFirstExtendSelection", "ctrl HOME", "selectFirstChangeLead", "END", "selectLast", "shift END", "selectLastExtendSelection", "ctrl shift END", "selectLastExtendSelection", "ctrl END", "selectLastChangeLead", "F2", "startEditing", "ctrl A", "selectAll", "ctrl SLASH", "selectAll", "ctrl BACK_SLASH", "clearSelection", "ctrl LEFT", "scrollLeft", "ctrl KP_LEFT", "scrollLeft", "ctrl RIGHT", "scrollRight", "ctrl KP_RIGHT", "scrollRight", "SPACE", "addToSelection", "ctrl SPACE", "toggleAndAnchor", "shift SPACE", "extendTo", "ctrl shift SPACE", "moveSelectionTo" }), "Tree.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ESCAPE", "cancel" }), "Viewport.font", localObject3, "Viewport.background", localDesktopProperty1, "Viewport.foreground", localDesktopProperty13 };
/*      */ 
/* 1584 */     paramUIDefaults.putDefaults((Object[])localObject8);
/* 1585 */     paramUIDefaults.putDefaults(getLazyValueDefaults());
/* 1586 */     initVistaComponentDefaults(paramUIDefaults);
/*      */   }
/*      */ 
/*      */   static boolean isOnVista() {
/* 1590 */     return (OSInfo.getOSType() == OSInfo.OSType.WINDOWS) && (OSInfo.getWindowsVersion().compareTo(OSInfo.WINDOWS_VISTA) >= 0);
/*      */   }
/*      */ 
/*      */   private void initVistaComponentDefaults(UIDefaults paramUIDefaults)
/*      */   {
/* 1595 */     if (!isOnVista()) {
/* 1596 */       return;
/*      */     }
/*      */ 
/* 1599 */     String[] arrayOfString = { "MenuItem", "Menu", "CheckBoxMenuItem", "RadioButtonMenuItem" };
/*      */ 
/* 1603 */     Object[] arrayOfObject = new Object[arrayOfString.length * 2];
/*      */ 
/* 1606 */     int i = 0;
/*      */     String str1;
/*      */     Object localObject1;
/* 1606 */     for (int j = 0; i < arrayOfString.length; i++) {
/* 1607 */       str1 = arrayOfString[i] + ".opaque";
/* 1608 */       localObject1 = paramUIDefaults.get(str1);
/* 1609 */       arrayOfObject[(j++)] = str1;
/* 1610 */       arrayOfObject[(j++)] = new XPValue(Boolean.FALSE, localObject1);
/*      */     }
/*      */ 
/* 1613 */     paramUIDefaults.putDefaults(arrayOfObject);
/*      */ 
/* 1619 */     i = 0; for (j = 0; i < arrayOfString.length; i++) {
/* 1620 */       str1 = arrayOfString[i] + ".acceleratorSelectionForeground";
/* 1621 */       localObject1 = paramUIDefaults.get(str1);
/* 1622 */       arrayOfObject[(j++)] = str1;
/* 1623 */       arrayOfObject[(j++)] = new XPValue(paramUIDefaults.getColor(arrayOfString[i] + ".acceleratorForeground"), localObject1);
/*      */     }
/*      */ 
/* 1629 */     paramUIDefaults.putDefaults(arrayOfObject);
/*      */ 
/* 1632 */     WindowsIconFactory.VistaMenuItemCheckIconFactory localVistaMenuItemCheckIconFactory = WindowsIconFactory.getMenuItemCheckIconFactory();
/*      */ 
/* 1634 */     j = 0;
/*      */     Object localObject2;
/* 1634 */     for (int k = 0; j < arrayOfString.length; j++) {
/* 1635 */       localObject1 = arrayOfString[j] + ".checkIconFactory";
/* 1636 */       localObject2 = paramUIDefaults.get(localObject1);
/* 1637 */       arrayOfObject[(k++)] = localObject1;
/* 1638 */       arrayOfObject[(k++)] = new XPValue(localVistaMenuItemCheckIconFactory, localObject2);
/*      */     }
/*      */ 
/* 1641 */     paramUIDefaults.putDefaults(arrayOfObject);
/*      */ 
/* 1643 */     j = 0; for (k = 0; j < arrayOfString.length; j++) {
/* 1644 */       localObject1 = arrayOfString[j] + ".checkIcon";
/* 1645 */       localObject2 = paramUIDefaults.get(localObject1);
/* 1646 */       arrayOfObject[(k++)] = localObject1;
/* 1647 */       arrayOfObject[(k++)] = new XPValue(localVistaMenuItemCheckIconFactory.getIcon(arrayOfString[j]), localObject2);
/*      */     }
/*      */ 
/* 1651 */     paramUIDefaults.putDefaults(arrayOfObject);
/*      */ 
/* 1655 */     j = 0; for (k = 0; j < arrayOfString.length; j++) {
/* 1656 */       localObject1 = arrayOfString[j] + ".evenHeight";
/* 1657 */       localObject2 = paramUIDefaults.get(localObject1);
/* 1658 */       arrayOfObject[(k++)] = localObject1;
/* 1659 */       arrayOfObject[(k++)] = new XPValue(Boolean.TRUE, localObject2);
/*      */     }
/* 1661 */     paramUIDefaults.putDefaults(arrayOfObject);
/*      */ 
/* 1664 */     InsetsUIResource localInsetsUIResource = new InsetsUIResource(0, 0, 0, 0);
/* 1665 */     k = 0;
/*      */     Object localObject3;
/* 1665 */     for (int m = 0; k < arrayOfString.length; k++) {
/* 1666 */       localObject2 = arrayOfString[k] + ".margin";
/* 1667 */       localObject3 = paramUIDefaults.get(localObject2);
/* 1668 */       arrayOfObject[(m++)] = localObject2;
/* 1669 */       arrayOfObject[(m++)] = new XPValue(localInsetsUIResource, localObject3);
/*      */     }
/* 1671 */     paramUIDefaults.putDefaults(arrayOfObject);
/*      */ 
/* 1674 */     Integer localInteger1 = Integer.valueOf(0);
/*      */ 
/* 1676 */     m = 0;
/*      */     Object localObject4;
/* 1676 */     for (int n = 0; m < arrayOfString.length; m++) {
/* 1677 */       localObject3 = arrayOfString[m] + ".checkIconOffset";
/* 1678 */       localObject4 = paramUIDefaults.get(localObject3);
/* 1679 */       arrayOfObject[(n++)] = localObject3;
/* 1680 */       arrayOfObject[(n++)] = new XPValue(localInteger1, localObject4);
/*      */     }
/*      */ 
/* 1683 */     paramUIDefaults.putDefaults(arrayOfObject);
/*      */ 
/* 1686 */     Integer localInteger2 = Integer.valueOf(WindowsPopupMenuUI.getSpanBeforeGutter() + WindowsPopupMenuUI.getGutterWidth() + WindowsPopupMenuUI.getSpanAfterGutter());
/*      */ 
/* 1689 */     n = 0;
/*      */     Object localObject5;
/* 1689 */     for (int i1 = 0; n < arrayOfString.length; n++) {
/* 1690 */       localObject4 = arrayOfString[n] + ".afterCheckIconGap";
/* 1691 */       localObject5 = paramUIDefaults.get(localObject4);
/* 1692 */       arrayOfObject[(i1++)] = localObject4;
/* 1693 */       arrayOfObject[(i1++)] = new XPValue(localInteger2, localObject5);
/*      */     }
/*      */ 
/* 1696 */     paramUIDefaults.putDefaults(arrayOfObject);
/*      */ 
/* 1699 */     UIDefaults.ActiveValue local1 = new UIDefaults.ActiveValue() {
/*      */       public Object createValue(UIDefaults paramAnonymousUIDefaults) {
/* 1701 */         return Integer.valueOf(WindowsIconFactory.VistaMenuItemCheckIconFactory.getIconWidth() + WindowsPopupMenuUI.getSpanBeforeGutter() + WindowsPopupMenuUI.getGutterWidth() + WindowsPopupMenuUI.getSpanAfterGutter());
/*      */       }
/*      */     };
/* 1707 */     i1 = 0; for (int i2 = 0; i1 < arrayOfString.length; i1++) {
/* 1708 */       localObject5 = arrayOfString[i1] + ".minimumTextOffset";
/* 1709 */       Object localObject6 = paramUIDefaults.get(localObject5);
/* 1710 */       arrayOfObject[(i2++)] = localObject5;
/* 1711 */       arrayOfObject[(i2++)] = new XPValue(local1, localObject6);
/*      */     }
/* 1713 */     paramUIDefaults.putDefaults(arrayOfObject);
/*      */ 
/* 1718 */     String str2 = "PopupMenu.border";
/*      */ 
/* 1720 */     XPBorderValue localXPBorderValue = new XPBorderValue(TMSchema.Part.MENU, new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getInternalFrameBorder"), BorderFactory.createEmptyBorder(2, 2, 2, 2));
/*      */ 
/* 1725 */     paramUIDefaults.put(str2, localXPBorderValue);
/*      */ 
/* 1729 */     paramUIDefaults.put("Table.ascendingSortIcon", new XPValue(new SkinIcon(TMSchema.Part.HP_HEADERSORTARROW, TMSchema.State.SORTEDDOWN), new SwingLazyValue("sun.swing.plaf.windows.ClassicSortArrowIcon", null, new Object[] { Boolean.TRUE })));
/*      */ 
/* 1734 */     paramUIDefaults.put("Table.descendingSortIcon", new XPValue(new SkinIcon(TMSchema.Part.HP_HEADERSORTARROW, TMSchema.State.SORTEDUP), new SwingLazyValue("sun.swing.plaf.windows.ClassicSortArrowIcon", null, new Object[] { Boolean.FALSE })));
/*      */   }
/*      */ 
/*      */   private Object getDesktopFontValue(String paramString, Object paramObject)
/*      */   {
/* 1749 */     if (this.useSystemFontSettings) {
/* 1750 */       return new WindowsFontProperty(paramString, paramObject);
/*      */     }
/* 1752 */     return null;
/*      */   }
/*      */ 
/*      */   private Object[] getLazyValueDefaults()
/*      */   {
/* 1761 */     XPBorderValue localXPBorderValue1 = new XPBorderValue(TMSchema.Part.BP_PUSHBUTTON, new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getButtonBorder"));
/*      */ 
/* 1767 */     XPBorderValue localXPBorderValue2 = new XPBorderValue(TMSchema.Part.EP_EDIT, new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getTextFieldBorder"));
/*      */ 
/* 1773 */     XPValue localXPValue1 = new XPValue(new InsetsUIResource(2, 2, 2, 2), new InsetsUIResource(1, 1, 1, 1));
/*      */ 
/* 1777 */     XPBorderValue localXPBorderValue3 = new XPBorderValue(TMSchema.Part.EP_EDIT, localXPBorderValue2, new EmptyBorder(2, 2, 2, 2));
/*      */ 
/* 1781 */     XPValue localXPValue2 = new XPValue(new InsetsUIResource(1, 1, 1, 1), null);
/*      */ 
/* 1785 */     XPBorderValue localXPBorderValue4 = new XPBorderValue(TMSchema.Part.CP_COMBOBOX, localXPBorderValue2);
/*      */ 
/* 1788 */     SwingLazyValue localSwingLazyValue1 = new SwingLazyValue("com.sun.java.swing.plaf.windows.WindowsBorders", "getFocusCellHighlightBorder");
/*      */ 
/* 1792 */     SwingLazyValue localSwingLazyValue2 = new SwingLazyValue("javax.swing.plaf.BorderUIResource", "getEtchedBorderUIResource");
/*      */ 
/* 1796 */     SwingLazyValue localSwingLazyValue3 = new SwingLazyValue("com.sun.java.swing.plaf.windows.WindowsBorders", "getInternalFrameBorder");
/*      */ 
/* 1800 */     SwingLazyValue localSwingLazyValue4 = new SwingLazyValue("javax.swing.plaf.BorderUIResource", "getLoweredBevelBorderUIResource");
/*      */ 
/* 1805 */     SwingLazyValue localSwingLazyValue5 = new SwingLazyValue("javax.swing.plaf.basic.BasicBorders$MarginBorder");
/*      */ 
/* 1808 */     SwingLazyValue localSwingLazyValue6 = new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getMenuBarBorder");
/*      */ 
/* 1813 */     XPBorderValue localXPBorderValue5 = new XPBorderValue(TMSchema.Part.MENU, new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getInternalFrameBorder"));
/*      */ 
/* 1819 */     SwingLazyValue localSwingLazyValue7 = new SwingLazyValue("com.sun.java.swing.plaf.windows.WindowsBorders", "getProgressBarBorder");
/*      */ 
/* 1823 */     SwingLazyValue localSwingLazyValue8 = new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getRadioButtonBorder");
/*      */ 
/* 1827 */     XPBorderValue localXPBorderValue6 = new XPBorderValue(TMSchema.Part.LBP_LISTBOX, localXPBorderValue2);
/*      */ 
/* 1830 */     XPBorderValue localXPBorderValue7 = new XPBorderValue(TMSchema.Part.LBP_LISTBOX, localSwingLazyValue4);
/*      */ 
/* 1833 */     SwingLazyValue localSwingLazyValue9 = new SwingLazyValue("com.sun.java.swing.plaf.windows.WindowsBorders", "getTableHeaderBorder");
/*      */ 
/* 1838 */     SwingLazyValue localSwingLazyValue10 = new SwingLazyValue("com.sun.java.swing.plaf.windows.WindowsBorders", "getToolBarBorder");
/*      */ 
/* 1843 */     SwingLazyValue localSwingLazyValue11 = new SwingLazyValue("javax.swing.plaf.BorderUIResource", "getBlackLineBorderUIResource");
/*      */ 
/* 1849 */     SwingLazyValue localSwingLazyValue12 = new SwingLazyValue("com.sun.java.swing.plaf.windows.WindowsIconFactory", "getCheckBoxIcon");
/*      */ 
/* 1853 */     SwingLazyValue localSwingLazyValue13 = new SwingLazyValue("com.sun.java.swing.plaf.windows.WindowsIconFactory", "getRadioButtonIcon");
/*      */ 
/* 1857 */     SwingLazyValue localSwingLazyValue14 = new SwingLazyValue("com.sun.java.swing.plaf.windows.WindowsIconFactory", "getRadioButtonMenuItemIcon");
/*      */ 
/* 1861 */     SwingLazyValue localSwingLazyValue15 = new SwingLazyValue("com.sun.java.swing.plaf.windows.WindowsIconFactory", "getMenuItemCheckIcon");
/*      */ 
/* 1865 */     SwingLazyValue localSwingLazyValue16 = new SwingLazyValue("com.sun.java.swing.plaf.windows.WindowsIconFactory", "getMenuItemArrowIcon");
/*      */ 
/* 1869 */     SwingLazyValue localSwingLazyValue17 = new SwingLazyValue("com.sun.java.swing.plaf.windows.WindowsIconFactory", "getMenuArrowIcon");
/*      */ 
/* 1874 */     Object[] arrayOfObject = { "Button.border", localXPBorderValue1, "CheckBox.border", localSwingLazyValue8, "ComboBox.border", localXPBorderValue4, "DesktopIcon.border", localSwingLazyValue3, "FormattedTextField.border", localXPBorderValue2, "FormattedTextField.margin", localXPValue1, "InternalFrame.border", localSwingLazyValue3, "List.focusCellHighlightBorder", localSwingLazyValue1, "Table.focusCellHighlightBorder", localSwingLazyValue1, "Menu.border", localSwingLazyValue5, "MenuBar.border", localSwingLazyValue6, "MenuItem.border", localSwingLazyValue5, "PasswordField.border", localXPBorderValue2, "PasswordField.margin", localXPValue1, "PopupMenu.border", localXPBorderValue5, "ProgressBar.border", localSwingLazyValue7, "RadioButton.border", localSwingLazyValue8, "ScrollPane.border", localXPBorderValue6, "Spinner.border", localXPBorderValue3, "Spinner.arrowButtonInsets", localXPValue2, "Spinner.arrowButtonSize", new Dimension(17, 9), "Table.scrollPaneBorder", localXPBorderValue7, "TableHeader.cellBorder", localSwingLazyValue9, "TextArea.margin", localXPValue1, "TextField.border", localXPBorderValue2, "TextField.margin", localXPValue1, "TitledBorder.border", new XPBorderValue(TMSchema.Part.BP_GROUPBOX, localSwingLazyValue2), "ToggleButton.border", localSwingLazyValue8, "ToolBar.border", localSwingLazyValue10, "ToolTip.border", localSwingLazyValue11, "CheckBox.icon", localSwingLazyValue12, "Menu.arrowIcon", localSwingLazyValue17, "MenuItem.checkIcon", localSwingLazyValue15, "MenuItem.arrowIcon", localSwingLazyValue16, "RadioButton.icon", localSwingLazyValue13, "RadioButtonMenuItem.checkIcon", localSwingLazyValue14, "InternalFrame.layoutTitlePaneAtOrigin", new XPValue(Boolean.TRUE, Boolean.FALSE), "Table.ascendingSortIcon", new XPValue(new SwingLazyValue("sun.swing.icon.SortArrowIcon", null, new Object[] { Boolean.TRUE, "Table.sortIconColor" }), new SwingLazyValue("sun.swing.plaf.windows.ClassicSortArrowIcon", null, new Object[] { Boolean.TRUE })), "Table.descendingSortIcon", new XPValue(new SwingLazyValue("sun.swing.icon.SortArrowIcon", null, new Object[] { Boolean.FALSE, "Table.sortIconColor" }), new SwingLazyValue("sun.swing.plaf.windows.ClassicSortArrowIcon", null, new Object[] { Boolean.FALSE })) };
/*      */ 
/* 1933 */     return arrayOfObject;
/*      */   }
/*      */ 
/*      */   public void uninitialize() {
/* 1937 */     super.uninitialize();
/*      */ 
/* 1939 */     if (WindowsPopupMenuUI.mnemonicListener != null) {
/* 1940 */       MenuSelectionManager.defaultManager().removeChangeListener(WindowsPopupMenuUI.mnemonicListener);
/*      */     }
/*      */ 
/* 1943 */     KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventPostProcessor(WindowsRootPaneUI.altProcessor);
/*      */ 
/* 1945 */     DesktopProperty.flushUnreferencedProperties();
/*      */   }
/*      */ 
/*      */   public static void setMnemonicHidden(boolean paramBoolean)
/*      */   {
/* 1966 */     if (UIManager.getBoolean("Button.showMnemonics") == true)
/*      */     {
/* 1968 */       isMnemonicHidden = false;
/*      */     }
/* 1970 */     else isMnemonicHidden = paramBoolean;
/*      */   }
/*      */ 
/*      */   public static boolean isMnemonicHidden()
/*      */   {
/* 1983 */     if (UIManager.getBoolean("Button.showMnemonics") == true)
/*      */     {
/* 1985 */       isMnemonicHidden = false;
/*      */     }
/* 1987 */     return isMnemonicHidden;
/*      */   }
/*      */ 
/*      */   public static boolean isClassicWindows()
/*      */   {
/* 2001 */     return isClassicWindows;
/*      */   }
/*      */ 
/*      */   public void provideErrorFeedback(Component paramComponent)
/*      */   {
/* 2026 */     super.provideErrorFeedback(paramComponent);
/*      */   }
/*      */ 
/*      */   public LayoutStyle getLayoutStyle()
/*      */   {
/* 2033 */     Object localObject = this.style;
/* 2034 */     if (localObject == null) {
/* 2035 */       localObject = new WindowsLayoutStyle(null);
/* 2036 */       this.style = ((LayoutStyle)localObject);
/*      */     }
/* 2038 */     return localObject;
/*      */   }
/*      */ 
/*      */   protected Action createAudioAction(Object paramObject)
/*      */   {
/* 2063 */     if (paramObject != null) {
/* 2064 */       String str1 = (String)paramObject;
/* 2065 */       String str2 = (String)UIManager.get(paramObject);
/* 2066 */       return new AudioAction(str1, str2);
/*      */     }
/* 2068 */     return null;
/*      */   }
/*      */ 
/*      */   static void repaintRootPane(Component paramComponent)
/*      */   {
/* 2073 */     JRootPane localJRootPane = null;
/* 2074 */     for (; paramComponent != null; paramComponent = paramComponent.getParent()) {
/* 2075 */       if ((paramComponent instanceof JRootPane)) {
/* 2076 */         localJRootPane = (JRootPane)paramComponent;
/*      */       }
/*      */     }
/*      */ 
/* 2080 */     if (localJRootPane != null)
/* 2081 */       localJRootPane.repaint();
/*      */     else
/* 2083 */       paramComponent.repaint();
/*      */   }
/*      */ 
/*      */   private int dluToPixels(int paramInt1, int paramInt2)
/*      */   {
/* 2548 */     if (this.baseUnitX == 0) {
/* 2549 */       calculateBaseUnits();
/*      */     }
/* 2551 */     if ((paramInt2 == 3) || (paramInt2 == 7))
/*      */     {
/* 2553 */       return paramInt1 * this.baseUnitX / 4;
/*      */     }
/* 2555 */     assert ((paramInt2 == 1) || (paramInt2 == 5));
/*      */ 
/* 2557 */     return paramInt1 * this.baseUnitY / 8;
/*      */   }
/*      */ 
/*      */   private void calculateBaseUnits()
/*      */   {
/* 2566 */     FontMetrics localFontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(UIManager.getFont("Button.font"));
/*      */ 
/* 2568 */     this.baseUnitX = localFontMetrics.stringWidth("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
/*      */ 
/* 2570 */     this.baseUnitX = ((this.baseUnitX / 26 + 1) / 2);
/*      */ 
/* 2572 */     this.baseUnitY = (localFontMetrics.getAscent() + localFontMetrics.getDescent() - 1);
/*      */   }
/*      */ 
/*      */   public Icon getDisabledIcon(JComponent paramJComponent, Icon paramIcon)
/*      */   {
/* 2585 */     if ((paramIcon != null) && (paramJComponent != null) && (Boolean.TRUE.equals(paramJComponent.getClientProperty(HI_RES_DISABLED_ICON_CLIENT_KEY))) && (paramIcon.getIconWidth() > 0) && (paramIcon.getIconHeight() > 0))
/*      */     {
/* 2590 */       BufferedImage localBufferedImage = new BufferedImage(paramIcon.getIconWidth(), paramIcon.getIconWidth(), 2);
/*      */ 
/* 2592 */       paramIcon.paintIcon(paramJComponent, localBufferedImage.getGraphics(), 0, 0);
/* 2593 */       RGBGrayFilter localRGBGrayFilter = new RGBGrayFilter();
/* 2594 */       FilteredImageSource localFilteredImageSource = new FilteredImageSource(localBufferedImage.getSource(), localRGBGrayFilter);
/* 2595 */       Image localImage = paramJComponent.createImage(localFilteredImageSource);
/* 2596 */       return new ImageIconUIResource(localImage);
/*      */     }
/* 2598 */     return super.getDisabledIcon(paramJComponent, paramIcon);
/*      */   }
/*      */ 
/*      */   private class ActiveWindowsIcon
/*      */     implements UIDefaults.ActiveValue
/*      */   {
/*      */     private Icon icon;
/*      */     private String nativeImageName;
/*      */     private String fallbackName;
/*      */     private DesktopProperty desktopProperty;
/*      */ 
/*      */     ActiveWindowsIcon(String paramString1, String paramString2, String arg4)
/*      */     {
/* 2160 */       this.nativeImageName = paramString2;
/*      */       Object localObject;
/* 2161 */       this.fallbackName = localObject;
/*      */ 
/* 2163 */       if ((OSInfo.getOSType() == OSInfo.OSType.WINDOWS) && (OSInfo.getWindowsVersion().compareTo(OSInfo.WINDOWS_XP) < 0))
/*      */       {
/* 2167 */         this.desktopProperty = new WindowsLookAndFeel.TriggerDesktopProperty(paramString1, WindowsLookAndFeel.this) {
/*      */           protected void updateUI() {
/* 2169 */             WindowsLookAndFeel.ActiveWindowsIcon.this.icon = null;
/* 2170 */             super.updateUI();
/*      */           }
/*      */         };
/*      */       }
/*      */     }
/*      */ 
/*      */     public Object createValue(UIDefaults paramUIDefaults)
/*      */     {
/*      */       Object localObject;
/* 2178 */       if (this.icon == null) {
/* 2179 */         localObject = (Image)ShellFolder.get(this.nativeImageName);
/* 2180 */         if (localObject != null) {
/* 2181 */           this.icon = new ImageIconUIResource((Image)localObject);
/*      */         }
/*      */       }
/* 2184 */       if ((this.icon == null) && (this.fallbackName != null)) {
/* 2185 */         localObject = (UIDefaults.LazyValue)SwingUtilities2.makeIcon(WindowsLookAndFeel.class, BasicLookAndFeel.class, this.fallbackName);
/*      */ 
/* 2188 */         this.icon = ((Icon)((UIDefaults.LazyValue)localObject).createValue(paramUIDefaults));
/*      */       }
/* 2190 */       return this.icon;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class AudioAction extends AbstractAction
/*      */   {
/*      */     private Runnable audioRunnable;
/*      */     private String audioResource;
/*      */ 
/*      */     public AudioAction(String paramString1, String paramString2)
/*      */     {
/* 2106 */       super();
/* 2107 */       this.audioResource = paramString2;
/*      */     }
/*      */     public void actionPerformed(ActionEvent paramActionEvent) {
/* 2110 */       if (this.audioRunnable == null) {
/* 2111 */         this.audioRunnable = ((Runnable)Toolkit.getDefaultToolkit().getDesktopProperty(this.audioResource));
/*      */       }
/* 2113 */       if (this.audioRunnable != null)
/*      */       {
/* 2116 */         new Thread(this.audioRunnable).start();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private class FontDesktopProperty extends WindowsLookAndFeel.TriggerDesktopProperty
/*      */   {
/*      */     FontDesktopProperty(String arg2)
/*      */     {
/* 2474 */       super(str);
/*      */     }
/*      */ 
/*      */     protected void updateUI() {
/* 2478 */       SwingUtilities2.AATextInfo localAATextInfo = SwingUtilities2.AATextInfo.getAATextInfo(true);
/* 2479 */       UIDefaults localUIDefaults = UIManager.getLookAndFeelDefaults();
/* 2480 */       localUIDefaults.put(SwingUtilities2.AA_TEXT_PROPERTY_KEY, localAATextInfo);
/* 2481 */       super.updateUI();
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class LazyWindowsIcon
/*      */     implements UIDefaults.LazyValue
/*      */   {
/*      */     private String nativeImage;
/*      */     private String resource;
/*      */ 
/*      */     LazyWindowsIcon(String paramString1, String paramString2)
/*      */     {
/* 2130 */       this.nativeImage = paramString1;
/* 2131 */       this.resource = paramString2;
/*      */     }
/*      */ 
/*      */     public Object createValue(UIDefaults paramUIDefaults) {
/* 2135 */       if (this.nativeImage != null) {
/* 2136 */         Image localImage = (Image)ShellFolder.get(this.nativeImage);
/* 2137 */         if (localImage != null) {
/* 2138 */           return new ImageIcon(localImage);
/*      */         }
/*      */       }
/* 2141 */       return SwingUtilities2.makeIcon(getClass(), WindowsLookAndFeel.class, this.resource);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class RGBGrayFilter extends RGBImageFilter
/*      */   {
/*      */     public RGBGrayFilter()
/*      */     {
/* 2603 */       this.canFilterIndexColorModel = true;
/*      */     }
/*      */ 
/*      */     public int filterRGB(int paramInt1, int paramInt2, int paramInt3) {
/* 2607 */       float f1 = ((paramInt3 >> 16 & 0xFF) / 255.0F + (paramInt3 >> 8 & 0xFF) / 255.0F + (paramInt3 & 0xFF) / 255.0F) / 3.0F;
/*      */ 
/* 2611 */       float f2 = (paramInt3 >> 24 & 0xFF) / 255.0F;
/*      */ 
/* 2613 */       f1 = Math.min(1.0F, (1.0F - f1) / 2.857143F + f1);
/*      */ 
/* 2615 */       int i = (int)(f2 * 255.0F) << 24 | (int)(f1 * 255.0F) << 16 | (int)(f1 * 255.0F) << 8 | (int)(f1 * 255.0F);
/*      */ 
/* 2619 */       return i;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class SkinIcon
/*      */     implements Icon, UIResource
/*      */   {
/*      */     private final TMSchema.Part part;
/*      */     private final TMSchema.State state;
/*      */ 
/*      */     SkinIcon(TMSchema.Part paramPart, TMSchema.State paramState)
/*      */     {
/* 2201 */       this.part = paramPart;
/* 2202 */       this.state = paramState;
/*      */     }
/*      */ 
/*      */     public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
/*      */     {
/* 2211 */       XPStyle localXPStyle = XPStyle.getXP();
/* 2212 */       assert (localXPStyle != null);
/* 2213 */       if (localXPStyle != null) {
/* 2214 */         XPStyle.Skin localSkin = localXPStyle.getSkin(null, this.part);
/* 2215 */         localSkin.paintSkin(paramGraphics, paramInt1, paramInt2, this.state);
/*      */       }
/*      */     }
/*      */ 
/*      */     public int getIconWidth()
/*      */     {
/* 2225 */       int i = 0;
/* 2226 */       XPStyle localXPStyle = XPStyle.getXP();
/* 2227 */       assert (localXPStyle != null);
/* 2228 */       if (localXPStyle != null) {
/* 2229 */         XPStyle.Skin localSkin = localXPStyle.getSkin(null, this.part);
/* 2230 */         i = localSkin.getWidth();
/*      */       }
/* 2232 */       return i;
/*      */     }
/*      */ 
/*      */     public int getIconHeight()
/*      */     {
/* 2241 */       int i = 0;
/* 2242 */       XPStyle localXPStyle = XPStyle.getXP();
/* 2243 */       if (localXPStyle != null) {
/* 2244 */         XPStyle.Skin localSkin = localXPStyle.getSkin(null, this.part);
/* 2245 */         i = localSkin.getHeight();
/*      */       }
/* 2247 */       return i;
/*      */     }
/*      */   }
/*      */ 
/*      */   private class TriggerDesktopProperty extends DesktopProperty
/*      */   {
/*      */     TriggerDesktopProperty(String arg2)
/*      */     {
/* 2457 */       super(null);
/*      */ 
/* 2461 */       getValueFromDesktop();
/*      */     }
/*      */ 
/*      */     protected void updateUI() {
/* 2465 */       super.updateUI();
/*      */ 
/* 2468 */       getValueFromDesktop();
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class WindowsFontProperty extends DesktopProperty
/*      */   {
/*      */     WindowsFontProperty(String paramString, Object paramObject)
/*      */     {
/* 2258 */       super(paramObject);
/*      */     }
/*      */ 
/*      */     public void invalidate(LookAndFeel paramLookAndFeel) {
/* 2262 */       if ("win.defaultGUI.font.height".equals(getKey())) {
/* 2263 */         ((WindowsLookAndFeel)paramLookAndFeel).style = null;
/*      */       }
/* 2265 */       super.invalidate(paramLookAndFeel);
/*      */     }
/*      */ 
/*      */     protected Object configureValue(Object paramObject) {
/* 2269 */       if ((paramObject instanceof Font)) {
/* 2270 */         Object localObject = (Font)paramObject;
/* 2271 */         if ("MS Sans Serif".equals(((Font)localObject).getName()))
/*      */         {
/* 2272 */           int i = ((Font)localObject).getSize();
/*      */           int j;
/*      */           try
/*      */           {
/* 2278 */             j = Toolkit.getDefaultToolkit().getScreenResolution();
/*      */           } catch (HeadlessException localHeadlessException) {
/* 2280 */             j = 96;
/*      */           }
/* 2282 */           if (Math.round(i * 72.0F / j) < 8) {
/* 2283 */             i = Math.round(8 * j / 72.0F);
/*      */           }
/* 2285 */           FontUIResource localFontUIResource = new FontUIResource("Microsoft Sans Serif", ((Font)localObject).getStyle(), i);
/*      */ 
/* 2287 */           if ((localFontUIResource.getName() != null) && (localFontUIResource.getName().equals(localFontUIResource.getFamily())))
/*      */           {
/* 2289 */             localObject = localFontUIResource;
/* 2290 */           } else if (i != ((Font)localObject).getSize()) {
/* 2291 */             localObject = new FontUIResource("MS Sans Serif", ((Font)localObject).getStyle(), i);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 2296 */         if (FontUtilities.fontSupportsDefaultEncoding((Font)localObject)) {
/* 2297 */           if (!(localObject instanceof UIResource)) {
/* 2298 */             localObject = new FontUIResource((Font)localObject);
/*      */           }
/*      */         }
/*      */         else {
/* 2302 */           localObject = FontUtilities.getCompositeFontUIResource((Font)localObject);
/*      */         }
/* 2304 */         return localObject;
/*      */       }
/*      */ 
/* 2307 */       return super.configureValue(paramObject);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class WindowsFontSizeProperty extends DesktopProperty
/*      */   {
/*      */     private String fontName;
/*      */     private int fontSize;
/*      */     private int fontStyle;
/*      */ 
/*      */     WindowsFontSizeProperty(String paramString1, String paramString2, int paramInt1, int paramInt2)
/*      */     {
/* 2323 */       super(null);
/* 2324 */       this.fontName = paramString2;
/* 2325 */       this.fontSize = paramInt2;
/* 2326 */       this.fontStyle = paramInt1;
/*      */     }
/*      */ 
/*      */     protected Object configureValue(Object paramObject) {
/* 2330 */       if (paramObject == null) {
/* 2331 */         paramObject = new FontUIResource(this.fontName, this.fontStyle, this.fontSize);
/*      */       }
/* 2333 */       else if ((paramObject instanceof Integer)) {
/* 2334 */         paramObject = new FontUIResource(this.fontName, this.fontStyle, ((Integer)paramObject).intValue());
/*      */       }
/*      */ 
/* 2337 */       return paramObject;
/*      */     }
/*      */   }
/*      */ 
/*      */   private class WindowsLayoutStyle extends DefaultLayoutStyle
/*      */   {
/*      */     private WindowsLayoutStyle()
/*      */     {
/*      */     }
/*      */ 
/*      */     public int getPreferredGap(JComponent paramJComponent1, JComponent paramJComponent2, LayoutStyle.ComponentPlacement paramComponentPlacement, int paramInt, Container paramContainer)
/*      */     {
/* 2493 */       super.getPreferredGap(paramJComponent1, paramJComponent2, paramComponentPlacement, paramInt, paramContainer);
/*      */ 
/* 2496 */       switch (WindowsLookAndFeel.2.$SwitchMap$javax$swing$LayoutStyle$ComponentPlacement[paramComponentPlacement.ordinal()])
/*      */       {
/*      */       case 1:
/* 2499 */         if ((paramInt == 3) || (paramInt == 7))
/*      */         {
/* 2501 */           int i = getIndent(paramJComponent1, paramInt);
/* 2502 */           if (i > 0) {
/* 2503 */             return i;
/*      */           }
/* 2505 */           return 10;
/*      */         }
/*      */ 
/*      */       case 2:
/* 2509 */         if (isLabelAndNonlabel(paramJComponent1, paramJComponent2, paramInt))
/*      */         {
/* 2519 */           return getButtonGap(paramJComponent1, paramJComponent2, paramInt, WindowsLookAndFeel.this.dluToPixels(3, paramInt));
/*      */         }
/*      */ 
/* 2523 */         return getButtonGap(paramJComponent1, paramJComponent2, paramInt, WindowsLookAndFeel.this.dluToPixels(4, paramInt));
/*      */       case 3:
/* 2527 */         return getButtonGap(paramJComponent1, paramJComponent2, paramInt, WindowsLookAndFeel.this.dluToPixels(7, paramInt));
/*      */       }
/*      */ 
/* 2530 */       return 0;
/*      */     }
/*      */ 
/*      */     public int getContainerGap(JComponent paramJComponent, int paramInt, Container paramContainer)
/*      */     {
/* 2537 */       super.getContainerGap(paramJComponent, paramInt, paramContainer);
/* 2538 */       return getButtonGap(paramJComponent, paramInt, WindowsLookAndFeel.this.dluToPixels(7, paramInt));
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class XPBorderValue extends WindowsLookAndFeel.XPValue
/*      */   {
/*      */     private final Border extraMargin;
/*      */ 
/*      */     XPBorderValue(TMSchema.Part paramPart, Object paramObject)
/*      */     {
/* 2396 */       this(paramPart, paramObject, null);
/*      */     }
/*      */ 
/*      */     XPBorderValue(TMSchema.Part paramPart, Object paramObject, Border paramBorder) {
/* 2400 */       super(paramObject);
/* 2401 */       this.extraMargin = paramBorder;
/*      */     }
/*      */ 
/*      */     public Object getXPValue(UIDefaults paramUIDefaults) {
/* 2405 */       Border localBorder = XPStyle.getXP().getBorder(null, (TMSchema.Part)this.xpValue);
/* 2406 */       if (this.extraMargin != null) {
/* 2407 */         return new BorderUIResource.CompoundBorderUIResource(localBorder, this.extraMargin);
/*      */       }
/*      */ 
/* 2410 */       return localBorder;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class XPColorValue extends WindowsLookAndFeel.XPValue
/*      */   {
/*      */     XPColorValue(TMSchema.Part paramPart, TMSchema.State paramState, TMSchema.Prop paramProp, Object paramObject) {
/* 2417 */       super(paramObject);
/*      */     }
/*      */ 
/*      */     public Object getXPValue(UIDefaults paramUIDefaults) {
/* 2421 */       XPColorValueKey localXPColorValueKey = (XPColorValueKey)this.xpValue;
/* 2422 */       return XPStyle.getXP().getColor(localXPColorValueKey.skin, localXPColorValueKey.prop, null);
/*      */     }
/*      */     private static class XPColorValueKey {
/*      */       XPStyle.Skin skin;
/*      */       TMSchema.Prop prop;
/*      */ 
/*      */       XPColorValueKey(TMSchema.Part paramPart, TMSchema.State paramState, TMSchema.Prop paramProp) {
/* 2430 */         this.skin = new XPStyle.Skin(paramPart, paramState);
/* 2431 */         this.prop = paramProp;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private class XPDLUValue extends WindowsLookAndFeel.XPValue {
/*      */     private int direction;
/*      */ 
/*      */     XPDLUValue(int paramInt1, int paramInt2, int arg4) {
/* 2440 */       super(Integer.valueOf(paramInt2));
/*      */       int i;
/* 2441 */       this.direction = i;
/*      */     }
/*      */ 
/*      */     public Object getXPValue(UIDefaults paramUIDefaults) {
/* 2445 */       int i = WindowsLookAndFeel.this.dluToPixels(((Integer)this.xpValue).intValue(), this.direction);
/* 2446 */       return Integer.valueOf(i);
/*      */     }
/*      */ 
/*      */     public Object getClassicValue(UIDefaults paramUIDefaults) {
/* 2450 */       int i = WindowsLookAndFeel.this.dluToPixels(((Integer)this.classicValue).intValue(), this.direction);
/* 2451 */       return Integer.valueOf(i);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class XPValue
/*      */     implements UIDefaults.ActiveValue
/*      */   {
/*      */     protected Object classicValue;
/*      */     protected Object xpValue;
/* 2350 */     private static final Object NULL_VALUE = new Object();
/*      */ 
/*      */     XPValue(Object paramObject1, Object paramObject2) {
/* 2353 */       this.xpValue = paramObject1;
/* 2354 */       this.classicValue = paramObject2;
/*      */     }
/*      */ 
/*      */     public Object createValue(UIDefaults paramUIDefaults) {
/* 2358 */       Object localObject = null;
/* 2359 */       if (XPStyle.getXP() != null) {
/* 2360 */         localObject = getXPValue(paramUIDefaults);
/*      */       }
/*      */ 
/* 2363 */       if (localObject == null)
/* 2364 */         localObject = getClassicValue(paramUIDefaults);
/* 2365 */       else if (localObject == NULL_VALUE) {
/* 2366 */         localObject = null;
/*      */       }
/*      */ 
/* 2369 */       return localObject;
/*      */     }
/*      */ 
/*      */     protected Object getXPValue(UIDefaults paramUIDefaults) {
/* 2373 */       return recursiveCreateValue(this.xpValue, paramUIDefaults);
/*      */     }
/*      */ 
/*      */     protected Object getClassicValue(UIDefaults paramUIDefaults) {
/* 2377 */       return recursiveCreateValue(this.classicValue, paramUIDefaults);
/*      */     }
/*      */ 
/*      */     private Object recursiveCreateValue(Object paramObject, UIDefaults paramUIDefaults) {
/* 2381 */       if ((paramObject instanceof UIDefaults.LazyValue)) {
/* 2382 */         paramObject = ((UIDefaults.LazyValue)paramObject).createValue(paramUIDefaults);
/*      */       }
/* 2384 */       if ((paramObject instanceof UIDefaults.ActiveValue)) {
/* 2385 */         return ((UIDefaults.ActiveValue)paramObject).createValue(paramUIDefaults);
/*      */       }
/* 2387 */       return paramObject;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.java.swing.plaf.windows.WindowsLookAndFeel
 * JD-Core Version:    0.6.2
 */