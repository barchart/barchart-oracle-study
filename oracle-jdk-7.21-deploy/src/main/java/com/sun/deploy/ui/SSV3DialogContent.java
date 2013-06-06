package com.sun.deploy.ui;

import com.sun.deploy.config.Platform;
import com.sun.deploy.panel.JSmartTextArea;
import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.trace.Trace;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

public class SSV3DialogContent
{
  private static final int MAX_PROMPT_WIDTH = 400;
  private static final int MAX_URL_WIDTH = 280;
  private AppInfo ainfo;
  private String mastheadKey;
  private String mainTextKey;
  private String locationKey;
  private String promptKey;
  private String multiPromptKey;
  private String multiTextKey;
  private String runTextKey;
  private String updateTextKey;
  private String cancelTextKey;
  private String alwaysTextKey;
  private URL updateURL;
  private int userAnswer = -1;
  private JPanel contentPane;
  private DialogTemplate template;
  private JCheckBox alwaysCheckbox;
  private JCheckBox multiClickCheckBox;
  private JButton runButton;
  private JButton updateButton;
  private JButton cancelButton;
  private AbstractAction cancelAction;
  private static Font ssvSmallFont;
  private static Font ssvSmallBoldFont;
  private static Font ssvFont;
  private static Font ssvBoldFont;
  private static Font ssvBigFont;
  private static Font ssvBigBoldFont;

  public SSV3DialogContent(AppInfo paramAppInfo, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, String paramString8, String paramString9, String paramString10, String paramString11, URL paramURL, DialogTemplate paramDialogTemplate)
  {
    setSSVFonts();
    this.template = paramDialogTemplate;
    this.ainfo = paramAppInfo;
    this.mastheadKey = paramString2;
    this.mainTextKey = paramString3;
    this.locationKey = paramString4;
    this.promptKey = paramString5;
    this.multiPromptKey = paramString6;
    this.multiTextKey = paramString7;
    this.runTextKey = paramString8;
    this.updateTextKey = paramString9;
    this.cancelTextKey = paramString10;
    this.alwaysTextKey = paramString11;
    this.updateURL = paramURL;
    initComponents();
  }

  public JPanel getContent()
  {
    return this.contentPane;
  }

  public int getAnswer()
  {
    if (this.userAnswer == -1)
      this.userAnswer = 1;
    return this.userAnswer | ((this.userAnswer == 0) && (this.alwaysCheckbox.isSelected()) ? 2 : 0);
  }

  private void initComponents()
  {
    this.contentPane = createContentPane();
    this.contentPane.add(createMastHead(), "North");
    JComponent localJComponent = createMainContent();
    localJComponent.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
    this.contentPane.add(localJComponent, "Center");
    this.contentPane.add(createOkCancelPanel(), "South");
  }

  private JPanel createContentPane()
  {
    JPanel localJPanel = new JPanel();
    localJPanel.setOpaque(false);
    localJPanel.setBorder(BorderFactory.createEmptyBorder(16, 24, 23, 15));
    localJPanel.setOpaque(false);
    localJPanel.setLayout(new BorderLayout());
    return localJPanel;
  }

  private JComponent createMastHead()
  {
    Component localComponent = null;
    JLabel localJLabel = new JLabel(getMessage(this.mastheadKey));
    localJLabel.setFont(ssvBigBoldFont);
    localJLabel.setOpaque(false);
    Box localBox = Box.createVerticalBox();
    localBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
    if (localComponent != null)
      localBox.add(localComponent);
    localBox.add(localJLabel);
    return localBox;
  }

  private JComponent createMainContent()
  {
    JComponent localJComponent = createWarningPanel();
    JEditorPane localJEditorPane = createPrompt();
    Box localBox = Box.createVerticalBox();
    localBox.add(localJComponent);
    if (this.multiTextKey == null)
      localBox.add(localJEditorPane);
    return localBox;
  }

  private JEditorPane createPrompt()
  {
    JEditorPane localJEditorPane = new JEditorPane();
    localJEditorPane.setEditorKit(new PromptEditorKit(null));
    localJEditorPane.setContentType("text/html");
    localJEditorPane.setText(html(getMessage(this.promptKey)));
    localJEditorPane.setPreferredSize(getPromptPreferredSize(getMessage(this.promptKey)));
    localJEditorPane.setEditable(false);
    localJEditorPane.setFocusable(false);
    localJEditorPane.setOpaque(false);
    return localJEditorPane;
  }

  private String html(String paramString)
  {
    String str = paramString.replaceAll("(" + getMessage(this.runTextKey) + ")|(" + getMessage(this.updateTextKey) + ")|(" + getMessage(this.cancelTextKey) + ")", "<b>$0</b>");
    return "<html><body>" + str + "</body></html>";
  }

  private Dimension getPromptPreferredSize(String paramString)
  {
    JEditorPane localJEditorPane = new JEditorPane();
    localJEditorPane.setSize(400, 32767);
    localJEditorPane.setText(paramString);
    Dimension localDimension = localJEditorPane.getPreferredSize();
    int i = 3;
    if (localDimension.height > ssvSmallFont.getSize() + 4)
      i += 3;
    localDimension.height += i;
    return localDimension;
  }

  private JComponent createWarningPanel()
  {
    JPanel localJPanel = new JPanel();
    int i = this.multiTextKey == null ? 16 : 0;
    localJPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, i, 0));
    localJPanel.setOpaque(false);
    localJPanel.setLayout(new BorderLayout());
    localJPanel.add(createShieldIcon(), "West");
    Box localBox = Box.createVerticalBox();
    localBox.setOpaque(false);
    JSmartTextArea localJSmartTextArea = new JSmartTextArea(getMessage(this.mainTextKey));
    localJSmartTextArea.setFont(ssvBoldFont);
    localJSmartTextArea.setLineWrap(true);
    localJSmartTextArea.setWrapStyleWord(true);
    localJSmartTextArea.setOpaque(false);
    localJSmartTextArea.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
    localBox.add(localJSmartTextArea);
    localBox.add(createLocationPanel());
    localJPanel.add(localBox, "Center");
    return localJPanel;
  }

  private JComponent createShieldIcon()
  {
    JLabel localJLabel = new JLabel();
    localJLabel.setOpaque(false);
    localJLabel.setIcon(ResourceManager.getIcon("yellowShield48.image"));
    localJLabel.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 20));
    Box localBox = Box.createVerticalBox();
    localBox.add(localJLabel);
    localBox.add(Box.createVerticalGlue());
    return localBox;
  }

  private JComponent createLocationPanel()
  {
    Box localBox = Box.createHorizontalBox();
    localBox.setOpaque(false);
    JLabel localJLabel1 = new JLabel(getMessage(this.locationKey));
    localJLabel1.setOpaque(false);
    localJLabel1.setFont(ssvSmallBoldFont);
    JLabel localJLabel2 = new JLabel(this.ainfo.getDisplayFrom());
    localJLabel2.setPreferredSize(new Dimension(280, ssvSmallFont.getSize() + 3));
    localJLabel2.putClientProperty("html.disable", Boolean.TRUE);
    localJLabel2.setOpaque(false);
    localBox.add(Box.createRigidArea(new Dimension(16, 1)));
    localBox.add(localJLabel1);
    localBox.add(Box.createRigidArea(new Dimension(6, 1)));
    localBox.add(localJLabel2);
    localBox.add(Box.createHorizontalGlue());
    return localBox;
  }

  private JComponent createOkCancelPanel()
  {
    JPanel localJPanel = new JPanel();
    localJPanel.setLayout(new FlowLayout(2));
    this.runButton = makeButton(this.runTextKey);
    this.runButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        SSV3DialogContent.this.runAction();
      }
    });
    localJPanel.add(this.runButton);
    if (this.updateTextKey != null)
    {
      this.updateButton = makeButton(this.updateTextKey);
      this.updateButton.addActionListener(new AbstractAction()
      {
        public void actionPerformed(ActionEvent paramAnonymousActionEvent)
        {
          SSV3DialogContent.this.updateAction();
        }
      });
      localJPanel.add(this.updateButton);
    }
    this.cancelButton = makeButton(this.cancelTextKey);
    this.cancelAction = new AbstractAction()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        SSV3DialogContent.this.cancelAction();
      }
    };
    this.cancelButton.addActionListener(this.cancelAction);
    this.template.getDialogInterface().setCancelAction(this.cancelAction);
    JButton[] arrayOfJButton = null;
    if (this.updateButton == null)
    {
      arrayOfJButton = new JButton[] { this.runButton, this.cancelButton };
      this.template.getDialogInterface().setInitialFocusComponent(this.cancelButton);
      this.template.getDialogInterface().setDefaultButton(this.cancelButton);
    }
    else
    {
      arrayOfJButton = new JButton[] { this.runButton, this.updateButton, this.cancelButton };
      this.template.getDialogInterface().setInitialFocusComponent(this.updateButton);
      this.template.getDialogInterface().setDefaultButton(this.updateButton);
    }
    DialogTemplate.resizeButtons(arrayOfJButton);
    localJPanel.add(this.cancelButton);
    localJPanel.setOpaque(false);
    localJPanel.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
    Box localBox = Box.createVerticalBox();
    createMultiSelection(localBox);
    localBox.add(localJPanel);
    this.alwaysCheckbox = new JCheckBox();
    setupCheckbox(localBox, this.alwaysTextKey, this.alwaysCheckbox);
    return localBox;
  }

  private void createMultiSelection(Box paramBox)
  {
    if ((this.multiPromptKey != null) && (this.multiTextKey != null))
    {
      this.runButton.setEnabled(false);
      JEditorPane localJEditorPane = new JEditorPane();
      localJEditorPane.setEditorKit(new PromptEditorKit(null));
      localJEditorPane.setContentType("text/html");
      String str = getMessage("dialog.security.risk.warning");
      localJEditorPane.setText("<html><body><b><font color=\"CC0000\">" + str + "</font><br><br>" + getMessage(this.multiPromptKey) + "</b></body></html>");
      localJEditorPane.setEditable(false);
      localJEditorPane.setFocusable(false);
      localJEditorPane.setOpaque(false);
      localJEditorPane.setFont(ssvSmallBoldFont);
      Box localBox = Box.createHorizontalBox();
      localBox.add(localJEditorPane);
      localBox.add(Box.createHorizontalGlue());
      localBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
      paramBox.add(localBox);
      this.multiClickCheckBox = new JCheckBox();
      setupCheckbox(paramBox, this.multiTextKey, this.multiClickCheckBox);
      this.multiClickCheckBox.addActionListener(new AbstractAction()
      {
        public void actionPerformed(ActionEvent paramAnonymousActionEvent)
        {
          SSV3DialogContent.this.runButton.setEnabled(SSV3DialogContent.this.multiClickCheckBox.isSelected());
          if (SSV3DialogContent.this.multiClickCheckBox.isSelected())
          {
            SSV3DialogContent.this.template.getDialogInterface().setDefaultButton(SSV3DialogContent.this.runButton);
            SSV3DialogContent.this.runButton.requestFocusInWindow();
          }
          else if (SSV3DialogContent.this.updateButton != null)
          {
            SSV3DialogContent.this.template.getDialogInterface().setDefaultButton(SSV3DialogContent.this.updateButton);
            SSV3DialogContent.this.updateButton.requestFocusInWindow();
          }
          else if (SSV3DialogContent.this.cancelButton != null)
          {
            SSV3DialogContent.this.template.getDialogInterface().setDefaultButton(SSV3DialogContent.this.cancelButton);
            SSV3DialogContent.this.cancelButton.requestFocusInWindow();
          }
        }
      });
    }
  }

  private void setupCheckbox(Box paramBox, String paramString, JCheckBox paramJCheckBox)
  {
    if (paramString != null)
    {
      paramJCheckBox.setOpaque(false);
      paramJCheckBox.setText(getMessage(paramString));
      paramJCheckBox.setMnemonic(getShortCut(paramString));
      Box localBox = Box.createHorizontalBox();
      localBox.add(paramJCheckBox);
      localBox.add(Box.createHorizontalGlue());
      paramBox.add(localBox);
    }
  }

  private JButton makeButton(String paramString)
  {
    JButton localJButton = new JButton(getMessage(paramString));
    int i = getShortCut(paramString);
    if (i > 0)
      localJButton.setMnemonic(i);
    localJButton.setOpaque(false);
    return localJButton;
  }

  private void runAction()
  {
    this.userAnswer = 0;
    closeDialog();
  }

  private void updateAction()
  {
    try
    {
      Platform.get().showDocument(this.updateURL.toString());
    }
    catch (Exception localException)
    {
      Trace.ignored(localException);
    }
  }

  private void closeDialog()
  {
    this.template.setVisible(false);
  }

  private void cancelAction()
  {
    this.userAnswer = 1;
    closeDialog();
  }

  private String getMessage(String paramString)
  {
    if (paramString == null)
      return null;
    return ResourceManager.getMessage(paramString);
  }

  private int getShortCut(String paramString)
  {
    return ResourceManager.getAcceleratorKey(paramString);
  }

  private static void setSSVFonts()
  {
    if (ssvFont == null)
    {
      JLabel localJLabel = new JLabel();
      int i = ResourceManager.getUIFont().getSize();
      int j = i;
      int k = i + 1;
      int m = i + 4;
      ssvSmallFont = localJLabel.getFont().deriveFont(0, j);
      ssvSmallBoldFont = localJLabel.getFont().deriveFont(1, j);
      ssvFont = localJLabel.getFont().deriveFont(0, k);
      ssvBoldFont = localJLabel.getFont().deriveFont(1, k);
      ssvBigFont = localJLabel.getFont().deriveFont(0, m);
      ssvBigBoldFont = localJLabel.getFont().deriveFont(1, m);
    }
  }

  private static String displayPropertiesToCSS(String paramString, Font paramFont)
  {
    StringBuilder localStringBuilder = new StringBuilder(paramString + " {");
    if (paramFont != null)
    {
      localStringBuilder.append(" font-family: ");
      localStringBuilder.append(paramFont.getFamily());
      localStringBuilder.append(" ; ");
      localStringBuilder.append(" font-size: ");
      localStringBuilder.append(paramFont.getSize());
      localStringBuilder.append("pt ;");
      if (paramFont.isBold())
        localStringBuilder.append(" font-weight: 700 ; ");
    }
    localStringBuilder.append(" }");
    return localStringBuilder.toString();
  }

  private static class PromptEditorKit extends HTMLEditorKit
  {
    private StyleSheet _defaultStyles;

    private PromptEditorKit()
    {
    }

    public StyleSheet getStyleSheet()
    {
      if (this._defaultStyles == null)
      {
        this._defaultStyles = new StyleSheet();
        this._defaultStyles.addStyleSheet(super.getStyleSheet());
        this._defaultStyles.addStyleSheet(createStyleSheetFromString("body { margin-top: 0; margin-bottom: 0; margin-left: 0; margin-right: 0 }"));
        this._defaultStyles.addStyleSheet(createStyleSheetFromString(SSV3DialogContent.displayPropertiesToCSS("body", SSV3DialogContent.ssvSmallFont)));
        this._defaultStyles.addStyleSheet(createStyleSheetFromString(SSV3DialogContent.displayPropertiesToCSS("b", SSV3DialogContent.ssvBoldFont)));
      }
      return this._defaultStyles;
    }

    private StyleSheet createStyleSheetFromString(String paramString)
    {
      StyleSheet localStyleSheet = new StyleSheet();
      StringReader localStringReader = null;
      try
      {
        localStringReader = new StringReader(paramString);
        localStyleSheet.loadRules(localStringReader, null);
      }
      catch (IOException localIOException)
      {
        Trace.printException(localIOException);
      }
      finally
      {
        if (localStringReader != null)
          localStringReader.close();
      }
      return localStyleSheet;
    }

    PromptEditorKit(SSV3DialogContent.1 param1)
    {
      this();
    }
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.ui.SSV3DialogContent
 * JD-Core Version:    0.6.2
 */