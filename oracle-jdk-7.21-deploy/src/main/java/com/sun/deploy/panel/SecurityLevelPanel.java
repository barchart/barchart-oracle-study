package com.sun.deploy.panel;

import com.sun.deploy.config.Config;
import com.sun.deploy.config.Platform;
import com.sun.deploy.resources.ResourceManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Hashtable;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SecurityLevelPanel extends JPanel
  implements SecurityProperties.JavaEnableListener, AdvancedProperties.NewPluginEnableListener
{
  private JSlider slider;
  private JSmartTextArea text;
  private boolean sliderStateChanged;

  public SecurityLevelPanel()
  {
    initComponents();
  }

  private void initComponents()
  {
    setBorder(new EmptyBorder(new Insets(5, 15, 5, 15)));
    Box localBox = Box.createVerticalBox();
    localBox.add(createSecurityLabel());
    JComponent localJComponent = getSliderPanel();
    localBox.add(localJComponent);
    localBox.add(createTextComponent());
    add(localBox);
  }

  private JComponent createSecurityLabel()
  {
    Box localBox = Box.createHorizontalBox();
    JLabel localJLabel = new JLabel(ResourceManager.getMessage("deployment.security.level.title"));
    localBox.add(Box.createRigidArea(new Dimension(10, localJLabel.getPreferredSize().height)));
    localBox.add(localJLabel);
    localBox.add(Box.createHorizontalGlue());
    return localBox;
  }

  private JComponent createTextComponent()
  {
    Box localBox = Box.createHorizontalBox();
    this.text = new JSmartTextArea("");
    int i = this.text.getPreferredSize().height * 4;
    this.text.setPreferredSize(new Dimension(this.text.preferred_width, i));
    boolean bool = Platform.get().getJavaPluginSettings();
    updateTextComponent(bool);
    localBox.add(this.text);
    return localBox;
  }

  private JComponent getSliderPanel()
  {
    JPanel localJPanel = new JPanel();
    Box localBox = Box.createHorizontalBox();
    JComponent localJComponent1 = createSlider();
    Dimension localDimension = localJComponent1.getPreferredSize();
    localJPanel.add(localJComponent1);
    JComponent localJComponent2 = createLevelIconsPanel(localDimension);
    localBox.add(localJComponent2);
    localBox.add(localJComponent1);
    localJPanel.add(localBox);
    return localJPanel;
  }

  private JComponent createLevelIconsPanel(Dimension paramDimension)
  {
    Box localBox = Box.createVerticalBox();
    int i = (int)(paramDimension.getHeight() / 5.0D);
    ImageIcon localImageIcon = ResourceManager.getIcon("security.images.secure.lock");
    int j = 32;
    JLabel localJLabel = new JLabel(localImageIcon);
    localJLabel.setPreferredSize(new Dimension(j, i));
    localBox.add(localJLabel);
    localBox.add(Box.createGlue());
    localBox.add(Box.createRigidArea(new Dimension(j, i)));
    localBox.add(Box.createGlue());
    localBox.add(Box.createRigidArea(new Dimension(j, i)));
    localBox.add(Box.createGlue());
    return localBox;
  }

  private JComponent createSlider()
  {
    this.slider = new JSlider();
    this.slider.setOrientation(1);
    this.slider.setMinimum(SecurityLevel.Medium.getSliderSetting());
    this.slider.setMaximum(SecurityLevel.VeryHigh.getSliderSetting());
    Hashtable localHashtable = new Hashtable();
    SecurityLevel localSecurityLevel = SecurityLevel.VeryHigh;
    localHashtable.put(new Integer(localSecurityLevel.getSliderSetting()), new JLabel(localSecurityLevel.getName()));
    localSecurityLevel = SecurityLevel.High;
    localHashtable.put(new Integer(localSecurityLevel.getSliderSetting()), new JLabel(localSecurityLevel.getName()));
    localSecurityLevel = SecurityLevel.Medium;
    localHashtable.put(new Integer(localSecurityLevel.getSliderSetting()), new JLabel(localSecurityLevel.getName()));
    JPanel localJPanel = new JPanel();
    localJPanel.setLayout(new BorderLayout());
    this.slider.setLabelTable(localHashtable);
    this.slider.setPaintTicks(true);
    this.slider.setPaintLabels(true);
    this.slider.setSnapToTicks(true);
    this.slider.setMinorTickSpacing(1);
    this.slider.setValue(SecurityProperties.getProposedSecurityLevel().getSliderSetting());
    Dimension localDimension = this.slider.getPreferredSize();
    this.slider.setPreferredSize(new Dimension(localDimension.width + 40, localDimension.height - 20));
    this.slider.addChangeListener(new ChangeListener()
    {
      public void stateChanged(ChangeEvent paramAnonymousChangeEvent)
      {
        SecurityLevelPanel.this.sliderStateChanged = true;
      }
    });
    this.slider.addMouseListener(new MouseAdapter()
    {
      public void mouseReleased(MouseEvent paramAnonymousMouseEvent)
      {
        SecurityLevelPanel.this.setSlider();
        SecurityLevelPanel.this.sliderStateChanged = false;
      }
    });
    this.slider.addKeyListener(new KeyAdapter()
    {
      public void keyReleased(KeyEvent paramAnonymousKeyEvent)
      {
        SecurityLevelPanel.this.setSlider();
        SecurityLevelPanel.this.sliderStateChanged = false;
      }
    });
    boolean bool = Platform.get().getJavaPluginSettings();
    enableSecuritySlider(SecurityProperties.isJavaInBrowserEnabled(), bool);
    SecurityProperties.addJavaEnableListener(this);
    AdvancedProperties.addStatusChangedListener(this);
    return this.slider;
  }

  private void enableSecuritySlider(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.slider.setEnabled((paramBoolean1) && (paramBoolean2) && (!Config.get().isPropertyLocked("deployment.security.level")));
    this.slider.invalidate();
  }

  private void updateTextComponent(boolean paramBoolean)
  {
    if ((!paramBoolean) && (SecurityProperties.isJavaInBrowserEnabled()))
      this.text.setText(AdvancedProperties.getOldPluginSliderDescription());
    else
      this.text.setText(SecurityProperties.getProposedSecurityLevel().getDescription());
  }

  public void javaEnableChanged(SecurityProperties.JavaEnableEvent paramJavaEnableEvent)
  {
    enableSecuritySlider(paramJavaEnableEvent.isEnabled(), Platform.get().getJavaPluginSettings());
    updateTextComponent(Platform.get().getJavaPluginSettings());
  }

  public void newPluginEnableChanged(AdvancedProperties.NewPluginEnableEvent paramNewPluginEnableEvent)
  {
    enableSecuritySlider(SecurityProperties.isJavaInBrowserEnabled(), paramNewPluginEnableEvent.isEnabled());
    updateTextComponent(paramNewPluginEnableEvent.isEnabled());
  }

  private void setSlider()
  {
    try
    {
      SecurityLevel localSecurityLevel1 = SecurityProperties.getProposedSecurityLevel();
      int i = this.slider.getValue();
      SecurityLevel localSecurityLevel2 = SecurityLevel.getSliderSetting(i);
      if ((this.sliderStateChanged) && (localSecurityLevel2 != localSecurityLevel1))
      {
        SecurityProperties.setProposedSecurityLevel(i);
        this.text.setText(localSecurityLevel2.getDescription());
      }
    }
    finally
    {
      this.sliderStateChanged = false;
    }
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.panel.SecurityLevelPanel
 * JD-Core Version:    0.6.2
 */