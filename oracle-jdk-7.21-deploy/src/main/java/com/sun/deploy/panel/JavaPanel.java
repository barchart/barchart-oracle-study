package com.sun.deploy.panel;

import com.sun.deploy.resources.ResourceManager;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class JavaPanel extends JPanel
  implements SecurityProperties.JavaEnableListener
{
  private JSmartTextArea jreTextArea;
  private JButton jreSettingsBtn;

  public JavaPanel()
  {
    initComponents();
  }

  private void initComponents()
  {
    setLayout(new BorderLayout());
    setBorder(new EmptyBorder(new Insets(15, 15, 15, 15)));
    Box localBox = Box.createVerticalBox();
    this.jreTextArea = new JSmartTextArea(getMessage("java.panel.jre.text"));
    JPanel localJPanel = new JPanel();
    localJPanel.setLayout(new FlowLayout(2));
    this.jreSettingsBtn = new JButton(getMessage("java.panel.jre_view_btn"));
    this.jreSettingsBtn.setMnemonic(ResourceManager.getMnemonic("java.panel.jre_view_btn"));
    this.jreSettingsBtn.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        JavaPanel.this.jreSettingsBtnActionPerformed(paramAnonymousActionEvent);
      }
    });
    setStateForSettingsButton();
    localJPanel.add(this.jreSettingsBtn);
    localBox.add(this.jreTextArea);
    localBox.add(localJPanel);
    add(localBox, "North");
    SecurityProperties.addJavaEnableListener(this);
  }

  private void jreSettingsBtnActionPerformed(ActionEvent paramActionEvent)
  {
    JreDialog localJreDialog = new JreDialog((JFrame)getTopLevelAncestor(), true);
    localJreDialog.setLocationRelativeTo(this);
    localJreDialog.setVisible(true);
  }

  private String getMessage(String paramString)
  {
    return ResourceManager.getMessage(paramString);
  }

  public void javaEnableChanged(SecurityProperties.JavaEnableEvent paramJavaEnableEvent)
  {
    setStateForSettingsButton();
  }

  private void setStateForSettingsButton()
  {
    if (SecurityProperties.isJavaInBrowserEnabled())
    {
      this.jreSettingsBtn.setToolTipText(getMessage("java.panel.jre_view_btn.tooltip"));
      this.jreSettingsBtn.setEnabled(true);
    }
    else
    {
      this.jreSettingsBtn.setToolTipText(getMessage("deployment.general.java.disabled"));
      this.jreSettingsBtn.setEnabled(false);
    }
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.panel.JavaPanel
 * JD-Core Version:    0.6.2
 */