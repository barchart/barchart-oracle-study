package com.sun.deploy.panel;

import com.sun.deploy.Environment;
import com.sun.deploy.config.Config;
import com.sun.deploy.config.JCPConfig;
import com.sun.deploy.config.JCPStoreConfig;
import com.sun.deploy.config.OSType;
import com.sun.deploy.config.Platform;
import com.sun.deploy.config.PluginClientConfig;
import com.sun.deploy.config.SecuritySettings;
import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.services.ServiceManager;
import com.sun.deploy.si.DeploySIListener;
import com.sun.deploy.si.SingleInstanceImpl;
import com.sun.deploy.si.SingleInstanceManager;
import com.sun.deploy.trace.FileTraceListener;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import com.sun.deploy.ui.AboutDialog;
import com.sun.deploy.ui.DialogTemplate;
import com.sun.deploy.uitoolkit.ToolkitStore;
import com.sun.deploy.util.DeployUIManager;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

public class ControlPanel extends JFrame
  implements DeploySIListener, PropertyChangeListener
{
  private static final int DIALOG_NONE = 0;
  private static final int DIALOG_JPI_SETTINGS = 1;
  private static final int DIALOG_BROWSER_SETTINGS = 2;
  private static final int DIALOG_IE_ERROR = 3;
  private static final int DIALOG_MOZ_ERROR = 4;
  private boolean _applyButtonAction = false;
  private static final String JCP_ID = "JavaControlPanel";
  private static JTabbedPane tabbedPane;
  private static GeneralPanel generalPanel;
  private SecurityPanel securityPanel;
  private JavaPanel javaPanel;
  private UpdatePanelImpl updatePanelImpl;
  private AdvancedPanel advancedPanel;
  private SingleInstanceImpl _sil = new SingleInstanceImpl();
  private JPanel decisionPanel;
  private JButton okButton;
  private static JButton applyButton;
  private JButton cancelButton;

  public ControlPanel()
  {
    this._sil.addSingleInstanceListener(this, "JavaControlPanel");
    Config.setInstance(new JCPConfig(this));
    initTrace();
    this.updatePanelImpl = UpdatePanelFactory.getInstance();
    initComponents();
    Vector localVector = Platform.get().getInstalledJREList();
    if (localVector != null)
      Config.get().storeInstalledJREs(localVector);
    this._applyButtonAction = false;
    apply();
    this._applyButtonAction = true;
  }

  private void initComponents()
  {
    tabbedPane = new JTabbedPane();
    this.securityPanel = new SecurityPanel();
    this.javaPanel = new JavaPanel();
    generalPanel = new GeneralPanel();
    SecurityProperties.addJavaEnableListener(generalPanel);
    Platform.get().initBrowserSettings();
    Config.get().getJqs();
    Config.get().getJavaPlugin();
    this.advancedPanel = new AdvancedPanel()
    {
      public Dimension getPreferredSize()
      {
        return new Dimension(0, 0);
      }
    };
    setTitle(getMessage("control.panel.title"));
    addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent paramAnonymousWindowEvent)
      {
        ControlPanel.this.exitForm();
      }
    });
    tabbedPane.setName(getMessage("control.panel.general"));
    tabbedPane.addTab(getMessage("control.panel.general"), generalPanel);
    JPanel localJPanel = this.updatePanelImpl.getPanel();
    if (localJPanel != null)
      tabbedPane.addTab(getMessage("control.panel.update"), localJPanel);
    tabbedPane.addTab(getMessage("control.panel.java"), this.javaPanel);
    tabbedPane.addTab(getMessage("control.panel.security"), this.securityPanel);
    getContentPane().add(tabbedPane, "Center");
    this.decisionPanel = new JPanel();
    this.decisionPanel.setLayout(new FlowLayout(2));
    this.okButton = new JButton(getMessage("common.ok_btn"));
    this.okButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        ControlPanel.this.okBtnActionPerformed(paramAnonymousActionEvent);
      }
    });
    this.okButton.setToolTipText(getMessage("cpl.ok_btn.tooltip"));
    this.decisionPanel.add(this.okButton);
    this.cancelButton = new JButton(getMessage("common.cancel_btn"));
    AbstractAction local4 = new AbstractAction()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        ControlPanel.this.cancelBtnActionPerformed(paramAnonymousActionEvent);
      }
    };
    this.cancelButton.addActionListener(local4);
    getRootPane().getInputMap(2).put(KeyStroke.getKeyStroke(27, 0), "cancel");
    getRootPane().getActionMap().put("cancel", local4);
    this.cancelButton.setToolTipText(getMessage("cpl.cancel_btn.tooltip"));
    this.decisionPanel.add(this.cancelButton);
    applyButton = makeButton("common.apply_btn");
    applyButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        ControlPanel.this.applyBtnActionPerformed(paramAnonymousActionEvent);
      }
    });
    applyButton.setToolTipText(getMessage("cpl.apply_btn.tooltip"));
    this.decisionPanel.add(applyButton);
    JButton[] arrayOfJButton = { this.okButton, this.cancelButton, applyButton };
    DialogTemplate.resizeButtons(arrayOfJButton);
    getContentPane().add(this.decisionPanel, "South");
    getContentPane().invalidate();
    getRootPane().setDefaultButton(this.okButton);
    tabbedPane.addTab(getMessage("control.panel.advanced"), this.advancedPanel);
    applyButton.setEnabled(false);
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        ControlPanel.this.resetBounds();
      }
    });
    if (OSType.isMac())
    {
      setOpacityOfAllComponents(generalPanel);
      if (this.updatePanelImpl.getPanel() != null)
        setOpacityOfAllComponents(this.updatePanelImpl.getPanel());
      setOpacityOfAllComponents(this.javaPanel);
      setOpacityOfAllComponents(this.securityPanel);
      setOpacityOfAllComponents(this.advancedPanel);
    }
    tabbedPane.addPropertyChangeListener(this);
    checkPreferredSizes(tabbedPane);
  }

  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    if (paramPropertyChangeEvent.getPropertyName().equals("font"))
      SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          ControlPanel.this.resetBounds();
        }
      });
  }

  public void resetBounds()
  {
    pack();
    if (!OSType.isMac())
    {
      Dimension localDimension1 = getPreferredSize();
      Dimension localDimension2 = Toolkit.getDefaultToolkit().getScreenSize();
      localDimension1.width = Math.min(localDimension1.width, localDimension2.width);
      localDimension1.height = Math.min(localDimension1.height, localDimension2.height);
      if (localDimension1.width > 440)
      {
        int i = localDimension1.width * localDimension1.height;
        localDimension1.width = 440;
        localDimension1.height = (i / 440 + 1);
      }
      setSize(localDimension1.width, localDimension1.height);
    }
    com.sun.deploy.ui.UIFactory.placeWindow(this);
    setResizable(false);
  }

  private void okBtnActionPerformed(ActionEvent paramActionEvent)
  {
    apply();
    exitForm();
  }

  private void applyBtnActionPerformed(ActionEvent paramActionEvent)
  {
    apply();
    enableApplyButton(false);
  }

  private void apply()
  {
    this.updatePanelImpl.saveSettings();
    SecurityProperties.saveLevelProperites();
    SecurityProperties.JavaEnableManager localJavaEnableManager = SecurityProperties.prepareToSaveJavaEnabled();
    localJavaEnableManager.saveIfEnabling();
    boolean bool1 = (localJavaEnableManager.isJavaEnableChanging()) || (!this._applyButtonAction);
    AdvancedProperties.NewPluginEnableManager localNewPluginEnableManager = AdvancedProperties.prepareToSaveNewPluginEnabled();
    localNewPluginEnableManager.saveIfChanging(bool1);
    boolean bool2 = localNewPluginEnableManager.isDialogShown();
    int i = Platform.get().applyBrowserSettings();
    if (!localJavaEnableManager.isJavaEnableChanging())
    {
      String str1;
      String str3;
      switch (i)
      {
      case 1:
        str1 = ResourceManager.getString("common.ok_btn");
        String str2 = ResourceManager.getString("common.cancel_btn");
        ToolkitStore.getUI().showMessageDialog(null, null, 3, null, ResourceManager.getMessage("browser.settings.alert.text"), null, null, str1, str2, null);
        break;
      case 2:
        if ((this._applyButtonAction) && (!bool2))
          ToolkitStore.getUI().showMessageDialog(null, null, 1, ResourceManager.getMessage("browser.settings.success.caption"), ResourceManager.getMessage("browser.settings.success.masthead"), ResourceManager.getMessage("browser.settings.success.text"), null, null, null, null);
        break;
      case 3:
        if (this._applyButtonAction)
        {
          str1 = ResourceManager.getString("common.ok_btn");
          str3 = ResourceManager.getString("common.detail.button");
          ToolkitStore.getUI().showMessageDialog(null, null, 0, ResourceManager.getMessage("browser.settings.fail.caption"), ResourceManager.getMessage("browser.settings.fail.masthead"), ResourceManager.getMessage("browser.settings.fail.ie.text"), null, str1, str3, null);
        }
        break;
      case 4:
        if (this._applyButtonAction)
        {
          str1 = ResourceManager.getString("common.ok_btn");
          str3 = ResourceManager.getString("common.detail.button");
          ToolkitStore.getUI().showMessageDialog(null, null, 0, ResourceManager.getMessage("browser.settings.fail.caption"), ResourceManager.getMessage("browser.settings.fail.masthead"), ResourceManager.getMessage("browser.settings.fail.moz.text"), null, str1, str3, null);
        }
        break;
      case 0:
      }
    }
    localJavaEnableManager.saveIfDisabling();
    Platform.get().setJqsSettings(Config.getBooleanProperty("java.quick.starter"));
    this.advancedPanel.reset();
  }

  private void cancelBtnActionPerformed(ActionEvent paramActionEvent)
  {
    exitForm();
  }

  private void exitForm()
  {
    this._sil.removeSingleInstanceListener(this);
    System.exit(0);
  }

  public static void main(String[] paramArrayOfString)
  {
    int i = Config.getOSName().indexOf("Windows") != -1 ? 1 : 0;
    int j = Config.getOSName().indexOf("OS X") != -1 ? 1 : 0;
    if (i != 0)
      ServiceManager.setService(33024);
    else if (j != 0)
      ServiceManager.setService(40960);
    else
      ServiceManager.setService(36864);
    if ((paramArrayOfString.length == 2) && (paramArrayOfString[0].equals("-setSecurityLevel")))
      try
      {
        int k = Integer.parseInt(paramArrayOfString[1]);
        if ((k > 0) && (k <= 3))
          SecuritySettings.setInstallerRecommendedSecurityLevel(3 - k);
      }
      catch (NumberFormatException localNumberFormatException)
      {
      }
      finally
      {
        System.exit(0);
      }
    String str2;
    if (((paramArrayOfString.length == 2) || (paramArrayOfString.length == 3)) && (paramArrayOfString[0].equals("-systemConfig")))
    {
      str1 = paramArrayOfString[1];
      try
      {
        str2 = paramArrayOfString.length == 3 ? paramArrayOfString[2] : null;
        SecuritySettings.setSystemDeploymentProperty(str1, str2);
      }
      finally
      {
        System.exit(0);
      }
    }
    if (((paramArrayOfString.length == 2) || (paramArrayOfString.length == 3)) && (paramArrayOfString[0].equals("-userConfig")))
    {
      str1 = paramArrayOfString[1];
      try
      {
        str2 = paramArrayOfString.length == 3 ? paramArrayOfString[2] : null;
        PluginClientConfig localPluginClientConfig = new PluginClientConfig();
        Config.setInstance(localPluginClientConfig);
        Config.setStringProperty(str1, str2);
        localPluginClientConfig.storeConfig();
      }
      finally
      {
        System.exit(0);
      }
    }
    if ((paramArrayOfString.length == 1) && (paramArrayOfString[0].equals("-store")))
    {
      Config.setInstance(new JCPStoreConfig());
      initTrace();
      Platform.get().setJqsSettings(Config.getBooleanProperty("java.quick.starter"));
      Platform.get().setJavaPluginSettings(Config.getBooleanProperty("deployment.jpi.mode.new"));
      Platform.get().applyBrowserSettings();
      System.exit(0);
    }
    String str1 = "";
    for (int m = 0; m < paramArrayOfString.length; m++)
      str1 = str1 + (m > 0 ? " " : "") + paramArrayOfString[m];
    if ((SingleInstanceManager.isServerRunning("JavaControlPanel")) && (SingleInstanceManager.connectToServer(str1)))
      System.exit(0);
    ControlPanel localControlPanel = new ControlPanel();
    if ((paramArrayOfString.length == 2) && (paramArrayOfString[0].equals("-tab")))
    {
      if (paramArrayOfString[1].equals("about"))
      {
        showAbout(localControlPanel);
        System.exit(0);
      }
      int n = getTabIndex(paramArrayOfString[1]);
      tabbedPane.setSelectedIndex(n);
    }
    SwingUtilities.invokeLater(new Runnable()
    {
      private final ControlPanel val$cpl;
      private final String[] val$args;

      public void run()
      {
        this.val$cpl.resetBounds();
        this.val$cpl.setVisible(true);
        if ((this.val$args.length == 1) && (this.val$args[0].equals("-viewer")) && (ControlPanel.generalPanel.cacheViewBtn.isEnabled()))
          ControlPanel.generalPanel.viewBtnAction();
      }
    });
  }

  public void newActivation(String[] paramArrayOfString)
  {
    if ((paramArrayOfString.length > 0) && (paramArrayOfString[0].startsWith("-tab")))
    {
      String str = paramArrayOfString[0].substring(4).trim();
      if (str.equals("about"))
        showAbout(this);
      else
        tabbedPane.setSelectedIndex(getTabIndex(str));
    }
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        ControlPanel.this.setExtendedState(ControlPanel.this.getExtendedState() & 0xFFFFFFFE);
        ControlPanel.this.toFront();
      }
    });
  }

  public Object getSingleInstanceListener()
  {
    return this;
  }

  public static void showPanel(JPanel paramJPanel)
  {
    tabbedPane.setSelectedComponent(paramJPanel);
  }

  public static void showPanel(String paramString)
  {
    int i = getTabIndex(paramString);
    if (i >= 0)
      tabbedPane.setSelectedIndex(i);
  }

  private static int getTabIndex(String paramString)
  {
    int i = 0;
    String str = paramString.trim();
    UpdatePanelImpl localUpdatePanelImpl = UpdatePanelFactory.getInstance();
    int j = localUpdatePanelImpl.getPanel() != null ? 1 : 0;
    if (str.equals("general"))
      i = 0;
    else if (str.equals("update"))
    {
      if (j != 0)
        i = 1;
    }
    else if (str.equals("java"))
      i = j != 0 ? 2 : 1;
    else if (str.equals("security"))
      i = j != 0 ? 3 : 2;
    else if (str.equals("advanced"))
      i = j != 0 ? 4 : 3;
    return i;
  }

  private static void initTrace()
  {
    if (Config.getBooleanProperty("deployment.trace"))
    {
      File localFile = new File(Config.getStringProperty("deployment.user.logdir"));
      if ((localFile.exists()) && (localFile.isDirectory()))
      {
        FileTraceListener localFileTraceListener = new FileTraceListener(new File(localFile, "jcp.trace"), false);
        Trace.addTraceListener(localFileTraceListener);
      }
      Trace.redirectStdioStderr();
      Trace.setEnabled(TraceLevel.BASIC, true);
      Trace.setEnabled(TraceLevel.TEMP, true);
    }
  }

  private String getMessage(String paramString)
  {
    return ResourceManager.getMessage(paramString);
  }

  public JButton makeButton(String paramString)
  {
    JButton localJButton = new JButton(getMessage(paramString));
    localJButton.setMnemonic(ResourceManager.getMnemonic(paramString));
    return localJButton;
  }

  public static void propertyHasChanged()
  {
    enableApplyButton(true);
  }

  public static void propertyChanged(boolean paramBoolean)
  {
    enableApplyButton(paramBoolean);
  }

  static void setOpacityOfAllComponents(JComponent paramJComponent)
  {
    if ((paramJComponent == null) || ((paramJComponent instanceof JScrollPane)))
      return;
    paramJComponent.setOpaque(false);
    Component[] arrayOfComponent = paramJComponent.getComponents();
    for (int i = 0; i < arrayOfComponent.length; i++)
    {
      Component localComponent = arrayOfComponent[i];
      if ((localComponent instanceof JComponent))
        setOpacityOfAllComponents((JComponent)localComponent);
    }
  }

  private static void enableApplyButton(boolean paramBoolean)
  {
    if (applyButton != null)
      applyButton.setEnabled(paramBoolean);
    if (generalPanel != null)
      generalPanel.enableViewButton(!paramBoolean);
  }

  private static void checkPreferredSizes(Component paramComponent)
  {
    Dimension localDimension = paramComponent.getPreferredSize();
    if ((paramComponent instanceof Container))
    {
      Component[] arrayOfComponent = ((Container)paramComponent).getComponents();
      for (int i = 0; i < arrayOfComponent.length; i++)
        checkPreferredSizes(arrayOfComponent[i]);
    }
  }

  private static void showAbout(ControlPanel paramControlPanel)
  {
    new AboutDialog(paramControlPanel, true).setVisible(true);
  }

  static
  {
    Environment.setEnvironmentType(2);
    DeployUIManager.setLookAndFeel();
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.panel.ControlPanel
 * JD-Core Version:    0.6.2
 */