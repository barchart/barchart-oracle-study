package com.sun.deploy.panel;

import com.sun.deploy.config.Config;
import com.sun.deploy.resources.ResourceManager;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class SecurityPanel extends JPanel
  implements SecurityProperties.JavaEnableListener
{
  private JCheckBox enableBrowserCheckbox;
  private JLabel disabledForUserOnlyLabel;

  public SecurityPanel()
  {
    initComponents();
    SecurityProperties.addJavaEnableListener(this);
  }

  public final void initComponents()
  {
    setLayout(new BorderLayout());
    setBorder(new EmptyBorder(new Insets(5, 5, 5, 5)));
    Box localBox = Box.createVerticalBox();
    localBox.add(createEnableJavaInBrowser());
    localBox.add(createSecurityLevelPanel());
    JPanel localJPanel = createCertificationPanel();
    localBox.add(localJPanel);
    add(localBox, "North");
    this.enableBrowserCheckbox.setEnabled(!Config.get().isPropertyLocked("deployment.webjava.enabled"));
  }

  private void certsBtnActionPerformed(ActionEvent paramActionEvent)
  {
    CertificatesDialog localCertificatesDialog = new CertificatesDialog((JFrame)getTopLevelAncestor(), true);
    localCertificatesDialog.setLocationRelativeTo(this);
    localCertificatesDialog.setVisible(true);
  }

  private String getMessage(String paramString)
  {
    return ResourceManager.getMessage(paramString);
  }

  private JPanel createSecurityLevelPanel()
  {
    return new SecurityLevelPanel();
  }

  private JPanel createCertificationPanel()
  {
    JPanel localJPanel1 = new JPanel();
    localJPanel1.setLayout(new BorderLayout());
    JPanel localJPanel2 = new JPanel();
    localJPanel2.setLayout(new FlowLayout(2));
    JButton localJButton = new JButton(getMessage("security.certificates.button.text"));
    localJButton.setMnemonic(ResourceManager.getMnemonic("security.certificates.button.text"));
    localJButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        SecurityPanel.this.certsBtnActionPerformed(paramAnonymousActionEvent);
      }
    });
    localJButton.setToolTipText(getMessage("security.certs_btn.tooltip"));
    localJPanel2.add(localJButton);
    localJPanel1.add(localJPanel2, "South");
    return localJPanel1;
  }

  private void decreaseFont(JComponent paramJComponent, int paramInt)
  {
    Font localFont = paramJComponent.getFont();
    paramJComponent.setFont(new Font(localFont.getName(), localFont.getStyle(), localFont.getSize() - paramInt));
  }

  private JComponent createEnableJavaInBrowser()
  {
    Box localBox = Box.createHorizontalBox();
    this.enableBrowserCheckbox = new JCheckBox(ResourceManager.getMessage("deployment.security.enable.java.browser"));
    this.enableBrowserCheckbox.setMnemonic(ResourceManager.getAcceleratorKey("deployment.security.enable.java.browser"));
    this.enableBrowserCheckbox.setSelected(SecurityProperties.isJavaInBrowserEnabled());
    this.enableBrowserCheckbox.addActionListener(new AbstractAction()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        boolean bool = SecurityPanel.this.enableBrowserCheckbox.isSelected();
        SecurityProperties.enableJavaInBrowser(bool);
        SecurityPanel.this.setTextForUserDisabledLabel();
      }
    });
    localBox.add(this.enableBrowserCheckbox);
    this.disabledForUserOnlyLabel = new JLabel("");
    decreaseFont(this.disabledForUserOnlyLabel, 2);
    setTextForUserDisabledLabel();
    localBox.add(this.disabledForUserOnlyLabel);
    localBox.add(Box.createHorizontalGlue());
    return localBox;
  }

  private void setTextForUserDisabledLabel()
  {
    if (SecurityProperties.isJavaInBrowserDisabledForUser())
      this.disabledForUserOnlyLabel.setText(ResourceManager.getMessage("deployment.security.java.browser.user.disabled"));
    else
      this.disabledForUserOnlyLabel.setText("");
  }

  public void javaEnableChanged(SecurityProperties.JavaEnableEvent paramJavaEnableEvent)
  {
    setTextForUserDisabledLabel();
    this.enableBrowserCheckbox.setSelected(paramJavaEnableEvent.isEnabled());
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.panel.SecurityPanel
 * JD-Core Version:    0.6.2
 */