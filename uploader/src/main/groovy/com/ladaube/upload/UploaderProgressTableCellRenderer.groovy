package com.ladaube.upload

import java.awt.Color
import java.awt.Component
import java.awt.Font
import javax.swing.JLabel
import javax.swing.JTable
import javax.swing.table.TableCellRenderer
import javax.swing.JProgressBar

class UploaderProgressTableCellRenderer extends JProgressBar implements TableCellRenderer {

    private UploaderUI uploaderUI

    def UploaderProgressTableCellRenderer(uploaderUI) {
        this.uploaderUI = uploaderUI;
        def f = new JLabel().getFont()
        this.setFont(new Font(f.name, f.style, f.size - 2))
    }

    Component getTableCellRendererComponent(JTable table, Object val,
                                            boolean isSelected, boolean hasFocus,
                                            int row, int column) {

        UploaderTableModel model = table.model
        def uploadStatus = model.getUploadStatus(row)
        if (uploadStatus) {
            this.setMaximum(uploadStatus.max.intValue())
            this.setValue(uploadStatus.value.intValue())
            this.setStringPainted(false)
        } else {
            this.setMaximum(1)
            this.setValue(0)
            this.setStringPainted(true)
            this.setString(UploaderTableModel.STATUSES[val])
        }
        return this
    }


}
