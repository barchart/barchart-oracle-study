package com.sun.deploy.panel;

import com.sun.deploy.config.Config;
import com.sun.deploy.config.JREInfo;
import com.sun.deploy.config.Platform;
import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.ui.DialogTemplate;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class JreDialog extends JDialog
  implements ActionListener, ChangeListener, ListSelectionListener
{
  private final JButton _findBtn = makeButton("deploy.jre.find.button");
  private final JButton _addBtn = makeButton("deploy.jre.add.button");
  private final JButton _removeBtn = makeButton("deploy.jre.remove.button");
  private final JButton _okBtn = new JButton(ResourceManager.getMessage("deploy.jre.ok.button"));
  private final JButton _cancelBtn;
  private final JreTableModel _sysTableModel;
  private final JreTableModel _userTableModel;
  private final JTabbedPane _tabbedPane;
  private final JTable _userTable;
  private final JTable _sysTable;
  private final JScrollPane _userTab;
  private final JScrollPane _systemTab;

  public JreDialog(Frame paramFrame, boolean paramBoolean)
  {
    super(paramFrame, paramBoolean);
    this._okBtn.addActionListener(this);
    this._cancelBtn = new JButton(ResourceManager.getMessage("deploy.jre.cancel.button"));
    this._cancelBtn.addActionListener(this);
    this._tabbedPane = new JTabbedPane();
    this._userTableModel = new JreTableModel(false);
    this._userTable = new JreTable(this._userTableModel);
    this._sysTableModel = new JreTableModel(true);
    this._sysTable = new JreTable(this._sysTableModel);
    this._userTab = new JScrollPane(this._userTable);
    this._systemTab = new JScrollPane(this._sysTable);
    initComponents();
  }

  private void initComponents()
  {
    setTitle(ResourceManager.getMessage("deploy.jre.title"));
    addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent paramAnonymousWindowEvent)
      {
        JreDialog.this.closeDialog();
      }
    });
    this._findBtn.setToolTipText(ResourceManager.getMessage("deploy.jre.find_btn.tooltip"));
    this._addBtn.setToolTipText(ResourceManager.getMessage("deploy.jre.add_btn.tooltip"));
    this._removeBtn.setToolTipText(ResourceManager.getMessage("deploy.jre.remove_btn.tooltip"));
    JButton[] arrayOfJButton1 = { this._findBtn, this._addBtn, this._removeBtn };
    DialogTemplate.resizeButtons(arrayOfJButton1);
    JPanel localJPanel = new JPanel();
    localJPanel.setLayout(new BorderLayout());
    localJPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
    Dimension localDimension = new Dimension(500, 150);
    LineBorder localLineBorder = new LineBorder(this._userTable.getForeground());
    this._userTable.setBorder(localLineBorder);
    this._userTable.setPreferredScrollableViewportSize(localDimension);
    this._sysTable.setBorder(localLineBorder);
    this._sysTable.setPreferredScrollableViewportSize(localDimension);
    this._tabbedPane.addTab(ResourceManager.getMessage("cert.dialog.user.level"), this._userTab);
    this._tabbedPane.addTab(ResourceManager.getMessage("cert.dialog.system.level"), this._systemTab);
    this._tabbedPane.setSelectedIndex(0);
    this._tabbedPane.addChangeListener(this);
    localJPanel.add(this._tabbedPane, "North");
    if (Platform.get().canUseAlternateJREs())
    {
      localObject = Box.createHorizontalBox();
      ((Box)localObject).add(Box.createHorizontalGlue());
      ((Box)localObject).add(this._findBtn);
      ((Box)localObject).add(Box.createHorizontalStrut(5));
      ((Box)localObject).add(this._addBtn);
      ((Box)localObject).add(Box.createHorizontalStrut(5));
      ((Box)localObject).add(this._removeBtn);
      localJPanel.add((Component)localObject, "South");
    }
    getContentPane().add(localJPanel, "North");
    Object localObject = new JPanel();
    ((JPanel)localObject).setLayout(new FlowLayout(2, 10, 5));
    JButton[] arrayOfJButton2 = { this._okBtn, this._cancelBtn };
    DialogTemplate.resizeButtons(arrayOfJButton2);
    ((JPanel)localObject).add(this._okBtn);
    ((JPanel)localObject).add(this._cancelBtn);
    getContentPane().add((Component)localObject, "South");
    getRootPane().setDefaultButton(this._okBtn);
    enableButtons();
    pack();
    this._userTable.getSelectionModel().addListSelectionListener(this);
    this._userTable.getColumnModel().getColumn(3).setCellRenderer(new PathRenderer());
    this._userTable.getColumnModel().getColumn(3).setCellEditor(new PathEditor());
    getRootPane().getInputMap(2).put(KeyStroke.getKeyStroke(27, 0), "cancel");
    getRootPane().getActionMap().put("cancel", new AbstractAction()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        JreDialog.this.closeDialog();
      }
    });
    this._userTable.getTableHeader().setFocusable(false);
    this._sysTable.getTableHeader().setFocusable(false);
  }

  private JButton makeButton(String paramString)
  {
    JButton localJButton = new JButton(ResourceManager.getMessage(paramString));
    localJButton.setMnemonic(ResourceManager.getMnemonic(paramString));
    localJButton.addActionListener(this);
    return localJButton;
  }

  public void valueChanged(ListSelectionEvent paramListSelectionEvent)
  {
    enableButtons();
  }

  public void stateChanged(ChangeEvent paramChangeEvent)
  {
    enableButtons();
  }

  private void enableButtons()
  {
    Component localComponent = this._tabbedPane.getSelectedComponent();
    if (localComponent != null)
      if (localComponent.equals(this._userTab))
      {
        int i = 0;
        int j = 0;
        for (int k = 0; k < this._userTableModel.getRowCount(); k++)
          if (this._userTable.isRowSelected(k))
          {
            i = 1;
            if (this._userTableModel.getJRE(k).isRegistered())
              j = 1;
          }
        this._removeBtn.setEnabled((i != 0) && (j == 0));
        this._findBtn.setEnabled(true);
        this._addBtn.setEnabled(true);
      }
      else
      {
        this._removeBtn.setEnabled(false);
        this._findBtn.setEnabled(false);
        this._addBtn.setEnabled(false);
      }
  }

  private void closeDialog()
  {
    setVisible(false);
    dispose();
  }

  public void actionPerformed(ActionEvent paramActionEvent)
  {
    JButton localJButton = (JButton)paramActionEvent.getSource();
    JTable localJTable = getSelectedTable();
    JreTableModel localJreTableModel = (JreTableModel)localJTable.getModel();
    if (localJButton == this._findBtn)
    {
      findJREs();
    }
    else if (localJButton == this._addBtn)
    {
      localJreTableModel.add(new JREInfo(null, null, null, null, null, Config.getOSName(), Config.getOSArch(), true, false), false, true);
      int i = localJreTableModel.getRowCount() - 1;
      localJTable.requestFocus();
      localJTable.setRowSelectionInterval(i, i);
    }
    else if (localJButton == this._removeBtn)
    {
      localJreTableModel.remove(this._userTable.getSelectedRows());
    }
    else if (localJButton == this._okBtn)
    {
      apply();
      ControlPanel.propertyHasChanged();
      closeDialog();
    }
    else if (localJButton == this._cancelBtn)
    {
      closeDialog();
    }
  }

  private JTable getSelectedTable()
  {
    return this._tabbedPane.getSelectedComponent() == this._userTab ? this._userTable : this._sysTable;
  }

  private void apply()
  {
    JTable[] arrayOfJTable = { this._userTable, this._sysTable };
    for (int i = 0; i < arrayOfJTable.length; i++)
    {
      if ((arrayOfJTable[i].equals(this._userTable)) && (arrayOfJTable[i].isEditing()))
        arrayOfJTable[i].getCellEditor().stopCellEditing();
      JreTableModel localJreTableModel = (JreTableModel)arrayOfJTable[i].getModel();
      localJreTableModel.validateAndSave();
    }
  }

  private void findJREs()
  {
    JTable localJTable = getSelectedTable();
    JreTableModel localJreTableModel = (JreTableModel)localJTable.getModel();
    try
    {
      JREInfo[] arrayOfJREInfo = JreFindDialog.search(this);
      if (arrayOfJREInfo != null)
        for (int i = 0; i < arrayOfJREInfo.length; i++)
          localJreTableModel.add(arrayOfJREInfo[i], true, true);
    }
    catch (Exception localException)
    {
      Trace.ignoredException(localException);
    }
  }

  static class JreTable extends JTable
  {
    private boolean _systemTable = false;
    private String[] columnToolTips;

    public JreTable(JreTableModel paramJreTableModel)
    {
      super();
      this._systemTable = paramJreTableModel.isSystem();
      this.columnModel.getColumn(0).setPreferredWidth(60);
      this.columnModel.getColumn(1).setPreferredWidth(60);
      this.columnModel.getColumn(2).setPreferredWidth(80);
      this.columnModel.getColumn(3).setPreferredWidth(120);
      this.columnModel.getColumn(4).setPreferredWidth(120);
      ArrayList localArrayList = new ArrayList();
      localArrayList.add(ResourceManager.getMessage("jretable.platform.tooltip"));
      localArrayList.add(ResourceManager.getMessage("jretable.product.tooltip"));
      localArrayList.add(ResourceManager.getMessage("jretable.location.tooltip"));
      localArrayList.add(ResourceManager.getMessage("jretable.path.tooltip"));
      localArrayList.add(ResourceManager.getMessage("jretable.vmargs.tooltip"));
      if (Platform.get().canUseAlternateJREs())
      {
        this.columnModel.getColumn(5).setPreferredWidth(50);
        localArrayList.add(ResourceManager.getMessage("jretable.enable.tooltip"));
      }
      this.columnToolTips = ((String[])localArrayList.toArray(new String[localArrayList.size()]));
    }

    protected JTableHeader createDefaultTableHeader()
    {
      return new JTableHeader(this.columnModel)
      {
        public String getToolTipText(MouseEvent paramAnonymousMouseEvent)
        {
          Object localObject = null;
          Point localPoint = paramAnonymousMouseEvent.getPoint();
          int i = this.columnModel.getColumnIndexAtX(localPoint.x);
          int j = this.columnModel.getColumn(i).getModelIndex();
          return JreDialog.JreTable.this.columnToolTips[j];
        }
      };
    }

    public String getToolTipText(MouseEvent paramMouseEvent)
    {
      String str = null;
      Point localPoint = paramMouseEvent.getPoint();
      int i = rowAtPoint(localPoint);
      int j = columnAtPoint(localPoint);
      int k = convertColumnIndexToModel(j);
      if ((k == 2) || (k == 4))
        str = (String)getValueAt(i, j);
      else if ((k == 3) && (this._systemTable))
        str = (String)getValueAt(i, j);
      else
        str = super.getToolTipText(paramMouseEvent);
      if ("".equals(str))
        return null;
      return str;
    }
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.panel.JreDialog
 * JD-Core Version:    0.6.2
 */