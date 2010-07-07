package com.ladaube.upload

import javax.swing.table.TableCellRenderer
import java.awt.Component
import javax.swing.JTable
import javax.swing.JLabel
import java.awt.Color
import java.awt.Font

class UploaderTableCellRenderer extends JLabel implements TableCellRenderer {

  private UploaderUI uploaderUI

  def UploaderTableCellRenderer(uploaderUI) {
    this.uploaderUI = uploaderUI;
    def f = new JLabel().getFont()
    this.setFont(new Font(f.name, f.style, f.size - 2))
  }

  Component getTableCellRendererComponent(JTable table, Object val,
                            boolean isSelected, boolean hasFocus,
                            int row, int column) {

    def model = table.model
    def rowStatus = model.getStatusForRow(row)
    if (rowStatus==4) {
      // already there
      this.setForeground(Color.GRAY)
    } else if (rowStatus==0 || rowStatus==2) {
      // uploaded
      this.setForeground(new Color(0, 150, 0))
    } else if (rowStatus==6) {
      // error
      this.setForeground(new Color(150, 0, 0))
    } else {
      this.setForeground(Color.BLACK)  
    }

    if (column==1) {
      val = UploaderTableModel.STATUSES[val]
    } else {
      if (uploaderUI.baseDir!=null) {
        val = val.substring(uploaderUI.baseDir.length())
      }
    }
    this.setText(val)
    return this
  }


}
