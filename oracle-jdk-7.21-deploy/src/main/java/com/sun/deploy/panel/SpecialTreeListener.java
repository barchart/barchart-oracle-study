package com.sun.deploy.panel;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class SpecialTreeListener extends KeyAdapter
{
  public void keyPressed(KeyEvent paramKeyEvent)
  {
    if ((paramKeyEvent.getSource() instanceof JTree))
    {
      JTree localJTree = (JTree)paramKeyEvent.getSource();
      TreePath localTreePath = localJTree.getSelectionPath();
      switch (paramKeyEvent.getKeyCode())
      {
      case 32:
        if ((localTreePath != null) && ((localTreePath.getLastPathComponent() instanceof IProperty)))
        {
          IProperty localIProperty = (IProperty)localTreePath.getLastPathComponent();
          if ((localIProperty instanceof ToggleProperty))
          {
            TreeModel localTreeModel = localJTree.getModel();
            if ("true".equalsIgnoreCase(localIProperty.getValue()))
              localTreeModel.valueForPathChanged(localTreePath, "false");
            else
              localTreeModel.valueForPathChanged(localTreePath, "true");
          }
          if ((localIProperty instanceof RadioProperty))
            ((RadioProperty)localIProperty).setValue(localIProperty.getValue());
          localJTree.repaint();
        }
        break;
      }
    }
  }

  public void keyReleased(KeyEvent paramKeyEvent)
  {
    if ((paramKeyEvent.getSource() instanceof JTree))
    {
      JTree localJTree = (JTree)paramKeyEvent.getSource();
      TreePath localTreePath = localJTree.getSelectionPath();
      switch (paramKeyEvent.getKeyCode())
      {
      case 40:
        if ((localTreePath != null) && ((localTreePath.getLastPathComponent() instanceof TextFieldProperty)))
          localJTree.startEditingAtPath(localTreePath);
        break;
      case 39:
        if ((localTreePath != null) && ((localTreePath.getLastPathComponent() instanceof TextFieldProperty)))
          localJTree.startEditingAtPath(localTreePath);
        break;
      }
    }
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.panel.SpecialTreeListener
 * JD-Core Version:    0.6.2
 */