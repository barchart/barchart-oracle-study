package com.sun.deploy.panel;

import com.sun.deploy.Environment;
import com.sun.deploy.config.Config;
import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.ui.AboutDialog;
import com.sun.deploy.ui.DialogTemplate;
import com.sun.deploy.ui.FancyButton;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class GeneralPanel extends JPanel
  implements SecurityProperties.JavaEnableListener
{
  private JLabel javaEnabledStatus;
  JButton cacheViewBtn;

  public GeneralPanel()
  {
    initComponents();
  }

  private void initComponents()
  {
    setBorder(new EmptyBorder(new Insets(15, 15, 0, 15)));
    setLayout(new BorderLayout());
    Box localBox = Box.createVerticalBox();
    JLabel localJLabel1 = new JLabel(getMessage("general.about.border"));
    localJLabel1.setAlignmentX(0.0F);
    localBox.add(localJLabel1);
    localBox.add(Box.createVerticalStrut(10));
    JSmartTextArea localJSmartTextArea1 = new JSmartTextArea(getMessage("general.about.text"));
    localJSmartTextArea1.setAlignmentX(0.0F);
    JPanel localJPanel1 = new JPanel();
    localJPanel1.setLayout(new FlowLayout(2));
    localJPanel1.setAlignmentX(0.0F);
    JButton localJButton1 = new JButton(getMessage("general.about.btn"));
    localJButton1.setMnemonic(ResourceManager.getMnemonic("general.about.btn"));
    localJButton1.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        GeneralPanel.this.aboutBtnActionPerformed(paramAnonymousActionEvent);
      }
    });
    localJButton1.setToolTipText(getMessage("general.about.btn.tooltip"));
    localJPanel1.add(localJButton1);
    localBox.add(localJSmartTextArea1);
    localBox.add(localJPanel1);
    JLabel localJLabel2 = new JLabel(getMessage("general.network.border.text"));
    localJLabel2.setAlignmentX(0.0F);
    localBox.add(localJLabel2);
    localBox.add(Box.createVerticalStrut(10));
    JPanel localJPanel2 = new JPanel();
    localJPanel2.setAlignmentX(0.0F);
    localJPanel2.setLayout(new FlowLayout(2));
    JButton localJButton2 = makeButton("general.network.settings.text");
    localJButton2.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        GeneralPanel.this.networkSettingsBtnActionPerformed(paramAnonymousActionEvent);
      }
    });
    localJButton2.setToolTipText(getMessage("network.settings.btn.tooltip"));
    localJPanel2.add(localJButton2);
    JSmartTextArea localJSmartTextArea2 = new JSmartTextArea(getMessage("general.network.desc.text"));
    localJSmartTextArea2.setAlignmentX(0.0F);
    localBox.add(localJSmartTextArea2);
    localBox.add(localJPanel2);
    JLabel localJLabel3 = new JLabel(getMessage("general.cache.border.text"));
    localJLabel3.setAlignmentX(0.0F);
    localBox.add(localJLabel3);
    localBox.add(Box.createVerticalStrut(10));
    JPanel localJPanel3 = new JPanel();
    localJPanel3.setAlignmentX(0.0F);
    localJPanel3.setLayout(new FlowLayout(2));
    this.cacheViewBtn = makeButton("general.cache.view.text");
    this.cacheViewBtn.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        GeneralPanel.this.viewBtnAction();
      }
    });
    JButton localJButton3 = makeButton("general.cache.settings.text");
    localJButton3.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        GeneralPanel.this.tempFilesSettingsBtnActionPerformed(paramAnonymousActionEvent);
      }
    });
    localJButton3.setToolTipText(getMessage("temp.files.settings.btn.tooltip"));
    JButton[] arrayOfJButton = { this.cacheViewBtn, localJButton3 };
    DialogTemplate.resizeButtons(arrayOfJButton);
    JSmartTextArea localJSmartTextArea3 = new JSmartTextArea(getMessage("general.cache.desc.text"));
    localJSmartTextArea3.setAlignmentX(0.0F);
    localJPanel3.add(localJButton3);
    localJPanel3.add(this.cacheViewBtn);
    localBox.add(localJSmartTextArea3);
    localBox.add(localJPanel3);
    localBox.add(createJavaEnabledPanel());
    add(localBox, "North");
  }

  private JComponent createJavaEnabledPanel()
  {
    Box localBox = Box.createHorizontalBox();
    localBox.setAlignmentX(0.0F);
    String str = SecurityProperties.isJavaInBrowserEnabled() ? "deployment.general.java.enabled" : "deployment.general.java.disabled";
    this.javaEnabledStatus = new JLabel(ResourceManager.getMessage(str));
    this.javaEnabledStatus.setAlignmentX(0.0F);
    FancyButton localFancyButton = new FancyButton(ResourceManager.getMessage("deployment.general.security.link"), ResourceManager.getMnemonic("deployment.general.security.link"), Color.BLUE);
    localFancyButton.addActionListener(new AbstractAction()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        ControlPanel.showPanel("security");
      }
    });
    localBox.add(this.javaEnabledStatus);
    localBox.add(Box.createRigidArea(new Dimension(15, 15)));
    localBox.add(localFancyButton);
    return localBox;
  }

  private void aboutBtnActionPerformed(ActionEvent paramActionEvent)
  {
    new AboutDialog((JFrame)getTopLevelAncestor(), true).setVisible(true);
  }

  void viewBtnAction()
  {
    String str = Environment.getDeploymentHomePath() + File.separator + "lib" + File.separator + "javaws.jar";
    URL[] arrayOfURL = new URL[1];
    try
    {
      arrayOfURL[0] = new URL("file", null, -1, str);
      Thread localThread = Thread.currentThread();
      URLClassLoader localURLClassLoader = new URLClassLoader(arrayOfURL, localThread.getContextClassLoader());
      localThread.setContextClassLoader(localURLClassLoader);
      Class localClass = localURLClassLoader.loadClass("com.sun.javaws.ui.CacheViewer");
      JFrame localJFrame = (JFrame)getTopLevelAncestor();
      Class[] arrayOfClass = { new JFrame().getClass() };
      Method localMethod = localClass.getMethod("showCacheViewer", arrayOfClass);
      if (!Modifier.isStatic(localMethod.getModifiers()))
        throw new NoSuchMethodException("com.sun.javaws.ui.CacheViewer.showCacheViewer");
      localMethod.setAccessible(true);
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = localJFrame;
      localMethod.invoke(null, arrayOfObject);
    }
    catch (Throwable localThrowable)
    {
      Trace.ignored(localThrowable);
    }
  }

  private void tempFilesSettingsBtnActionPerformed(ActionEvent paramActionEvent)
  {
    CacheSettingsDialog localCacheSettingsDialog = new CacheSettingsDialog((JFrame)getTopLevelAncestor(), true);
    localCacheSettingsDialog.pack();
    localCacheSettingsDialog.setLocationRelativeTo(this);
    localCacheSettingsDialog.setVisible(true);
  }

  private void networkSettingsBtnActionPerformed(ActionEvent paramActionEvent)
  {
    NetworkSettingsDialog localNetworkSettingsDialog = new NetworkSettingsDialog((JFrame)getTopLevelAncestor(), true);
    localNetworkSettingsDialog.pack();
    localNetworkSettingsDialog.setLocationRelativeTo(this);
    localNetworkSettingsDialog.setVisible(true);
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

  void enableViewButton(boolean paramBoolean)
  {
    if (!paramBoolean)
    {
      this.cacheViewBtn.setToolTipText(getMessage("general.cache.view.tooltip.unapplied"));
      this.cacheViewBtn.setEnabled(false);
    }
    else if (!Config.getBooleanProperty("deployment.cache.enabled"))
    {
      this.cacheViewBtn.setToolTipText(getMessage("general.cache.view.tooltip.disabled"));
      this.cacheViewBtn.setEnabled(false);
    }
    else
    {
      this.cacheViewBtn.setToolTipText(getMessage("general.cache.view.tooltip"));
      this.cacheViewBtn.setEnabled(true);
    }
  }

  public void javaEnableChanged(SecurityProperties.JavaEnableEvent paramJavaEnableEvent)
  {
    if (this.javaEnabledStatus != null)
    {
      String str = paramJavaEnableEvent.isEnabled() ? "deployment.general.java.enabled" : "deployment.general.java.disabled";
      this.javaEnabledStatus.setText(ResourceManager.getMessage(str));
      this.javaEnabledStatus.invalidate();
    }
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.panel.GeneralPanel
 * JD-Core Version:    0.6.2
 */