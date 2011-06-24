package com.ladaube.upload

import groovy.swing.SwingBuilder

import java.awt.BorderLayout as BL
import javax.swing.WindowConstants as WC
import javax.swing.BorderFactory
import javax.swing.JOptionPane
import java.awt.FlowLayout
import java.awt.GridLayout
import java.awt.Dimension
import javax.swing.SwingConstants
import javax.swing.UIManager
import javax.swing.UIManager.LookAndFeelInfo
import javax.swing.ImageIcon
import java.awt.Color
import javax.swing.JLabel
import java.awt.Font
import javax.swing.JFileChooser

class UploaderUI {

  def url
  def username
  def password
  String baseDir

  private def swing = new SwingBuilder()
  private def frame
  private Uploader uploader

  int nbUploaded = 0
  int nbSkipped = 0
  int nbErrors = 0

  private def createBanner(String labelId, String labelText) {
    URL u = getClass().getResource('/banner.jpg')
    def icon = new ImageIcon(u, 'Logo')    
    def font = new JLabel().getFont()    
    swing.label(constraints: BL.NORTH, icon: icon, background: Color.WHITE, opaque: true)
    swing.label(constraints: BL.SOUTH,
            id: labelId,
            text: labelText,
            background: Color.WHITE,
            opaque: true,
            font: new Font(font.name, font.style, font.size + 1),
            border: BorderFactory.createEmptyBorder(4,4,4,4))
  }

  private void handleEvent(BaseEvent uploadEvent) {
    if (uploadEvent instanceof AuthenticationEvent) {
      if (!uploadEvent.success) {
        JOptionPane.showMessageDialog(frame,
            "Unable to authenticate.\nPlease check the connection details and try again." ,
            "Authentication failed",
            JOptionPane.ERROR_MESSAGE);
      }
    } else {
      swing.daTable.model.handleUploadEvent(uploadEvent)
      if (uploadEvent instanceof ErrorEvent) {
        nbErrors++
      } else if (uploadEvent instanceof HttpPostEvent) {
        if (uploadEvent.isCompleted && uploadEvent.isTrack) {
          nbUploaded++
        }
      } else if (uploadEvent instanceof MD5CheckEvent) {
        if (uploadEvent.trackAlreadyPresent) {
          nbSkipped++
        }
      }
      setStatusText("Uploading... $nbUploaded track(s) uploaded, $nbSkipped skipped, $nbErrors errors")      
    }
  }

  void display() {
    swing.doLater {
      try {
          for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
              if ("Nimbus".equals(info.getName())) {
                  UIManager.setLookAndFeel(info.getClassName());
                  break;
              }
          }
      } catch (Exception e) {
          println 'Nimbus is not available.' 
      }

      frame = swing.frame(
              id: 'mainFrame',
              title : "LaDaube upload tool",
              defaultCloseOperation: WC.EXIT_ON_CLOSE,
              resizable: false) {
        panel(layout: new BL()) {
          // north : banner
          panel(constraints: BL.NORTH, layout: new BL()) {
            createBanner('loginLabel', 'LaDaube Upload Tool - enter your credentials...')
          }
          // center: login info
          panel(constraints: BL.CENTER, layout: new GridLayout(3, 2), border: BorderFactory.createEmptyBorder(10,10,0,10)) {
            label(text:'Server URL', horizontalAlignment: SwingConstants.RIGHT)
            textField(id:'tfServerUrl', preferredSize: new Dimension(200, 20), text: url)
            label(text:'Username', horizontalAlignment: SwingConstants.RIGHT)
            textField(id:'tfUserName', text: username)
            label(text:'Password', horizontalAlignment: SwingConstants.RIGHT)
            passwordField(id:'tfPassword', text: password)
          }
          // south: buttons
          panel(constraints: BL.SOUTH, layout: new FlowLayout(FlowLayout.RIGHT)) {
            button('Next...', actionPerformed: {
              doLater {
                url = swing.tfServerUrl.text
                username = swing.tfUserName.text
                password = swing.tfPassword.text

                // create uploader and attempt to authenticate
                uploader = new Uploader().
                  url(url).
                  username(username).
                  password(password).
                  addListeners([{ uploadEvent ->
                          this.handleEvent(uploadEvent)
                        }]).
                  login()
                if (uploader.isAuthenticated()) {
                  // logged in, next page
                  frame.contentPane.removeAll()
                  frame.contentPane = createMainPanel()
                  frame.title = "LaDaube upload tool - $username@$url"
                  frame.resizable = true
                  frame.pack()
                }
              }
            })
            button('Cancel', actionPerformed: {
              doLater {
                frame.dispose()
              }
            })

          }
        }
      }
      def icon = new ImageIcon(getClass().getResource('/icon.jpg'), 'icon')
      frame.setIconImage(icon.getImage())
      frame.pack()
      frame.setVisible(true)
    }
  }

  private def setStatusText(String s) {
    swing.labelStatus.text = s
  }

  private def setStatusBusy(boolean busy) {
    def icon = null
    if (busy) {
      URL u = getClass().getResource('/busy.gif')
      icon = new ImageIcon(u, 'busy')
    }
    swing.labelStatus.setIcon(icon)
  }

  private def createMainPanel() {
    def panel = swing.panel(layout: new BL()) {
      panel(constraints: BL.NORTH, layout: new BL()) {
        createBanner('labelStatus', 'Select the base folder to import from, and click the upload button...')
      }

      panel(constraints: BL.CENTER, layout: new BL()) {
        panel(constraints: BL.NORTH, layout: new BL()) {
          // north pane : base dir selection + buttons
          panel(layout: new BL(), constraints: BL.NORTH, border: BorderFactory.createEmptyBorder(5,5,5,0)) {
            panel(layout:new BL(), constraints: BL.CENTER) {
              label(text:'Base directory', constraints: BL.WEST)
              button(constraints:BL.CENTER, text: 'choose folder...', id:'btnChooseFolder', actionPerformed: {
                  def chooser = swing.fileChooser(
                    dialogTitle: 'Select a folder on your disk',
                    fileSelectionMode:JFileChooser.DIRECTORIES_ONLY)
                  int retVal = chooser.showOpenDialog(swing.mainFrame)
                  if (retVal == JFileChooser.APPROVE_OPTION) {
                      File f = chooser.getSelectedFile()
                      baseDir = f.absolutePath
                      swing.btnChooseFolder.text = baseDir
                      swing.btnUp.enabled = true
                  }
              })
            }
            panel(layout:new BL(), constraints: BL.EAST) {
              panel(constraints: BL.CENTER, layout: new BL()) {
                button(constraints: BL.CENTER, id: 'btnUp', text: 'Upload', enabled: false, actionPerformed: {
                  if (!baseDir) {
                      throw new IllegalStateException("You must select a base folder");
                  }
                  swing.btnUp.enabled = false
                  swing.btnCancel.enabled = true
                  doOutside {
                    if (uploader==null) {
                      throw new IllegalStateException("uploader is null")
                    }
                    swing.daTable.model.clear()
                    nbUploaded = 0
                    nbErrors = 0
                    nbSkipped = 0
                    // call uploader
                    setStatusText('Uploading...')
                    uploader = new Uploader().
                      url(url).
                      username(username).
                      password(password).
                      baseDir(new File(baseDir)).
                      addListeners([
                            { uploadEvent ->
                              swing.daTable.model.handleUploadEvent(uploadEvent)
                              if (uploadEvent instanceof ErrorEvent) {
                                nbErrors++
                              } else if (uploadEvent instanceof HttpPostEvent) {
                                if (uploadEvent.isCompleted && uploadEvent.isTrack) {
                                  nbUploaded++
                                }
                              } else if (uploadEvent instanceof MD5CheckEvent) {
                                if (uploadEvent.trackAlreadyPresent) {
                                  nbSkipped++
                                }
                              }
                              setStatusText("Uploading... $nbUploaded track(s) uploaded, $nbSkipped skipped, $nbErrors errors")
                            },
                            { uploadEvent ->
                              println "4debug: $uploadEvent"
                            }
                      ])
                    try {
                      setStatusBusy(true)
                      uploader.upload()
                    } catch(FileNotFoundException e) {
                      setStatusText('Error : ' + e.message)
                      JOptionPane.showMessageDialog(frame,
                          "The specified file does not exist :\n$e.message",
                          "Uploader error",
                          JOptionPane.ERROR_MESSAGE);
                    } catch(Exception e) {
                      setStatusText('Error : ' + e.message)
                      e.printStackTrace()
                      JOptionPane.showMessageDialog(frame,
                          e.getMessage(),
                          "Uploader error",
                          JOptionPane.ERROR_MESSAGE);
                    } finally {
                      setStatusBusy(false)
                    }
                    doLater {
                      swing.btnUp.enabled = true
                      swing.btnCancel.enabled = false
                    }
                    setStatusText("Finished : $nbUploaded track(s) uploaded, $nbSkipped skipped, $nbErrors errors")
                  }
                })
                button(constraints: BL.EAST, id: 'btnCancel', text: 'Stop', enabled: false, actionPerformed: {
                  if (uploader) {
                    swing.btnCancel.enabled = false
                    uploader.stop()
                    setStatusText('Stopping upload...')
                  }
                })
              }                            
            }
          }
        }
        panel(constraints: BL.CENTER, layout: new BL()) {
          // center pane : logs
          scrollPane(constraints: BL.CENTER) {
            table(id: 'daTable', model: new UploaderTableModel())
          }
        }
      }
    }

    def cellRenderer = new UploaderTableCellRenderer(this)
    def cm = swing.daTable.columnModel
    cm.getColumn(0).setHeaderValue('File')
    cm.getColumn(0).setPreferredWidth(300)
    cm.getColumn(0).setCellRenderer(cellRenderer)
    cm.getColumn(1).setHeaderValue('Status')
    cm.getColumn(1).setCellRenderer(cellRenderer)

    return panel
  }

  private static final String LAF = "org.jvnet.substance.skin.SubstanceBusinessLookAndFeel";

  public static void main(String[] args) {    
    UploaderUI ui = new UploaderUI(url: 'http://babz.hd.free.fr/ladaube', username: '', password: '')
    ui.display()
  }
}
