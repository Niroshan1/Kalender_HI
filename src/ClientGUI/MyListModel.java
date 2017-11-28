/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ClientGUI;
import Terminkalender.LauncherInterface;
import Terminkalender.Terminkalender;
import java.io.Serializable;
import java.util.LinkedList;
import javax.swing.AbstractListModel;

/**
 *
 * @author Edwrard Nana
 */
public class MyListModel extends AbstractListModel {
    
      private final LinkedList<String> list;

  public MyListModel(LinkedList<String> list) {
    this.list = list;
  }

      @Override
  public Object getElementAt(int index) {
    return list.get(index);
  }

      @Override
  public int getSize() {
    return list.size();
  }
    
}
