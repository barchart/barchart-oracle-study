package com.sun.deploy.panel;

import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.ui.DialogTemplate;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

public abstract class BasicDialog extends JDialog
{
  private final boolean setCancelToDefault;
  private JButton cancelButton;

  public BasicDialog(JFrame paramJFrame, String paramString, boolean paramBoolean)
  {
    super(paramJFrame, paramString, true);
    this.setCancelToDefault = paramBoolean;
    initComponents();
  }

  public BasicDialog(JFrame paramJFrame, String paramString)
  {
    this(paramJFrame, paramString, false);
  }

  private void initComponents()
  {
    JPanel localJPanel = new JPanel();
    addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent paramAnonymousWindowEvent)
      {
        BasicDialog.this.closeDialog();
      }
    });
    localJPanel.setLayout(new BorderLayout());
    localJPanel.setBorder(new EmptyBorder(new Insets(16, 16, 10, 10)));
    localJPanel.add(createContentPanel(), "Center");
    localJPanel.add(createOkCancelPanel(), "South");
    add(localJPanel);
    pack();
    setResizable(false);
  }

  protected abstract void okAction();

  protected abstract JComponent createContentPanel();

  protected void cancelAction()
  {
    closeDialog();
  }

  protected void closeDialog()
  {
    setVisible(false);
    dispose();
  }

  protected static String getMessage(String paramString)
  {
    return ResourceManager.getMessage(paramString);
  }

  protected static JButton makeButton(String paramString)
  {
    JButton localJButton = new JButton(getMessage(paramString));
    return localJButton;
  }

  protected JComponent createOkCancelPanel()
  {
    JPanel localJPanel = new JPanel();
    localJPanel.setLayout(new FlowLayout(2));
    JButton localJButton = makeButton("common.ok_btn");
    localJButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        BasicDialog.this.okAction();
      }
    });
    localJPanel.add(localJButton);
    this.cancelButton = makeButton("common.cancel_btn");
    AbstractAction local3 = new AbstractAction()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        BasicDialog.this.cancelAction();
      }
    };
    this.cancelButton.addActionListener(local3);
    getRootPane().getInputMap(2).put(KeyStroke.getKeyStroke(27, 0), "cancel");
    getRootPane().getActionMap().put("cancel", local3);
    JButton[] arrayOfJButton = { localJButton, this.cancelButton };
    DialogTemplate.resizeButtons(arrayOfJButton);
    localJPanel.add(this.cancelButton);
    if (this.setCancelToDefault)
    {
      getRootPane().setDefaultButton(this.cancelButton);
      this.cancelButton.requestFocusInWindow();
    }
    else
    {
      getRootPane().setDefaultButton(localJButton);
      localJButton.requestFocusInWindow();
    }
    enterPressesWhenFocused(localJButton);
    enterPressesWhenFocused(this.cancelButton);
    return localJPanel;
  }

  public void pack()
  {
    super.pack();
    if (this.setCancelToDefault)
      this.cancelButton.requestFocusInWindow();
  }

  private void enterPressesWhenFocused(JButton paramJButton)
  {
    paramJButton.registerKeyboardAction(paramJButton.getActionForKeyStroke(KeyStroke.getKeyStroke(32, 0, false)), KeyStroke.getKeyStroke(10, 0, false), 0);
    paramJButton.registerKeyboardAction(paramJButton.getActionForKeyStroke(KeyStroke.getKeyStroke(32, 0, true)), KeyStroke.getKeyStroke(10, 0, true), 0);
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.panel.BasicDialog
 * JD-Core Version:    0.6.2
 */