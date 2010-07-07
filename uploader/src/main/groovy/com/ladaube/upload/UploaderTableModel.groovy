package com.ladaube.upload

import javax.swing.table.AbstractTableModel

class UploaderTableModel extends AbstractTableModel {

  static final STATUSES = [
          'Track uploaded',
          'Uploading track...',
          'Image uploaded',
          'Uploading image...',
          'Track already present, not uploaded',
          'Track not present, ready for upload',
          'Error'
  ]

  private def filesAndStatuses = [:]
  private def fileList = []

  void clear() {
    filesAndStatuses = [:]
    fileList = []
  }

  int getRowCount() {
    return fileList.size()
  }

  int getColumnCount() {
    return 2
  }

  int getStatusForRow(int row) {
    return filesAndStatuses[fileList[row]]
  }

  Object getValueAt(int row, int col) {
    if (col==0) {
      return fileList[row]
    } else if (col==1) {
      return filesAndStatuses[fileList[row]]
    } else {
      return null
    }
  }

  void handleUploadEvent(BaseEvent evt) {
    String fileName = null
    Integer status = -1
    if (evt instanceof HttpPostEvent) {
      
      // file uploads

      HttpPostEvent pe = (HttpPostEvent)evt
      fileName = pe.fileName
      if (pe.isTrack) {
        // track
        status = pe.isCompleted ? 0 : 1
      } else {
        // image
        status = pe.isCompleted ? 2 : 3
      }
    } else if (evt instanceof MD5CheckEvent) {

      // MD5 checks

      MD5CheckEvent ce = (MD5CheckEvent)evt
      fileName = ce.fileName
      status = ce.trackAlreadyPresent ? 4 : 5
    } else if (evt instanceof ErrorEvent) {

      // upload errors

      ErrorEvent ee = (ErrorEvent)evt
      fileName = ee.fileName
      status = 6
    }

    if (fileName) {
      // we have file name and status, update the model
      if (fileList.indexOf(fileName)==-1) {
        fileList.add(0, fileName)
      }
      filesAndStatuses.remove(fileName)
      filesAndStatuses[fileName] = status

      // fire data changed
      fireTableDataChanged()
    }
  }

}
