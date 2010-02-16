/**

* This file is part of Base Modules.
 *
 * Copyright (c) 2009, Ben Fortuna [fortuna@micronode.com]
 *
 * Base Modules is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Base Modules is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Base Modules.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.mnode.figurate

import static java.lang.Math.min;
import static java.lang.Math.max;
import java.awt.SystemTray
import java.awt.PopupMenu
import java.awt.MenuItem
import java.awt.event.MouseEvent;
import java.awt.TrayIcon
import java.awt.Component;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.text.*;
import java.awt.Color
import java.awt.Font
import java.awt.Insets
import java.awt.Dimension
import java.awt.BorderLayout
import java.awt.FlowLayout
import java.awt.Desktop
import java.awt.FlowLayout
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import javax.swing.Action
import javax.swing.DefaultComboBoxModel
import javax.swing.DefaultListModel
import javax.swing.DefaultListCellRenderer
import javax.swing.Icon
import javax.swing.JFrame
import javax.swing.JComboBox
import javax.swing.JFileChooser
import javax.swing.JTabbedPane
import javax.swing.JScrollPane
import javax.swing.JList
import javax.swing.DefaultComboBoxModel
import javax.swing.UIManager
import javax.swing.filechooser.FileSystemView
import javax.swing.event.HyperlinkListener
import javax.swing.event.HyperlinkEvent
import groovy.beans.Bindable
import groovy.swing.SwingXBuilder
import groovy.swing.LookAndFeelHelper
import org.jvnet.substance.SubstanceLookAndFeel
import org.jvnet.substance.api.SubstanceConstants
import org.jvnet.substance.api.SubstanceConstants.TabCloseKind
import org.jvnet.substance.api.tabbed.TabCloseCallback
import org.jvnet.lafwidget.tabbed.DefaultTabPreviewPainter
import org.jvnet.flamingo.bcb.*
import org.jvnet.flamingo.bcb.core.BreadcrumbFileSelector
import org.jvnet.flamingo.common.JCommandButton
import org.jvnet.flamingo.common.JCommandButtonStrip
import org.jvnet.flamingo.common.JCommandToggleButton
import org.jvnet.flamingo.common.CommandToggleButtonGroup
import org.jvnet.flamingo.common.CommandButtonDisplayState
import org.jvnet.flamingo.svg.SvgBatikResizableIcon
import org.mnode.base.views.tracker.TrackerRegistry;
import org.fife.ui.rtextarea.RTextScrollPane
import org.fife.ui.rtextarea.Gutter
import org.fife.ui.rtextarea.RTextArea
import org.fife.ui.rtextarea.RTextAreaEditorKit
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rsyntaxtextarea.SyntaxConstants
import com.xduke.xswing.DataTipManager
import org.jdesktop.swingx.JXStatusBar
import org.jdesktop.swingx.JXStatusBar.Constraint
import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaEditorKit.IncreaseFontSizeAction
import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaEditorKit.DecreaseFontSizeAction
import org.fife.ui.rtextarea.RTextAreaEditorKit.TimeDateAction
import org.fife.ui.rtextarea.RTextAreaEditorKit.BeginRecordingMacroAction
import org.springframework.context.support.ClassPathXmlApplicationContext

 /**
  * @author fortuna
  *
  */
  /*
@Grapes([
    @Grab(group='org.springframework', module='spring-context', version='3.0.0.RELEASE'),
    @Grab(group='org.codehaus.griffon.swingxbuilder', module='swingxbuilder', version='0.1.6'),
    @Grab(group='net.java.dev.substance', module='substance', version='5.3'),
    @Grab(group='net.java.dev.substance', module='substance-swingx', version='5.3'),
    //@Grab(group='org.swinglabs', module='swingx', version='0.9.2'),
    @Grab(group='org.mnode.base', module='base-views', version='0.0.1-SNAPSHOT'),
    //@Grab(group='jgoodies', module='forms', version='1.0.5'),
    //@Grab(group='org.codehaus.griffon.flamingobuilder', module='flamingobuilder', version='0.2'),
    @Grab(group='net.java.dev.flamingo', module='flamingo', version='4.2'),
    @Grab(group='org.apache.xmlgraphics', module='batik-awt-util', version='1.7'),
    @Grab(group='org.apache.xmlgraphics', module='batik-swing', version='1.7'),
    @Grab(group='org.apache.xmlgraphics', module='batik-transcoder', version='1.7'),
    @Grab(group='net.java.dev.datatips', module='datatips', version='20091219'),
    @Grab(group='com.fifesoft.rsyntaxtextarea', module='rsyntaxtextarea', version='1.4.0')])
    */
class Figurate {
     
    static void close(def frame, def exit) {
        if (exit) {
            System.exit(0)
        }
        else {
            frame.visible = false
        }
    }
    
    static void main(args) {
    
        // try to connect to existing control..
        //def context = new ClassPathXmlApplicationContext("/FigurateControlProxy.xml", Figurate.class)
        //def control = context.getBean("FigurateControl")
        
        //if (!control) {
        //    context = new ClassPathXmlApplicationContext("/FigurateControl.xml", Figurate.class)
        //    control = context.getBean("FigurateControl")
        //}
        
         UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0))
         UIManager.put(org.jvnet.lafwidget.LafWidget.ANIMATION_KIND, org.jvnet.lafwidget.utils.LafConstants.AnimationKind.FAST.derive(2))
         //UIManager.put(org.jvnet.lafwidget.LafWidget.TABBED_PANE_PREVIEW_PAINTER, new DefaultTabPreviewPainter())
         LookAndFeelHelper.instance.addLookAndFeelAlias('substance5', 'org.jvnet.substance.skin.SubstanceNebulaLookAndFeel')
         LookAndFeelHelper.instance.addLookAndFeelAlias('seaglass', 'com.seaglasslookandfeel.SeaGlassLookAndFeel')
         
         def swing = new SwingXBuilder()
         swing.registerBeanFactory('comboBox', MaxWidthComboBox.class)
         swing.registerBeanFactory('fileBreadcrumbBar', MaxWidthBreadcrumbFileSelector.class)
         //swing.registerBeanFactory('syntaxTextArea', RSyntaxTextArea.class)

         // mac-specific settings..
         if (System.getProperty("os.name")  =~ /^Mac/) {
             System.setProperty("apple.laf.useScreenMenuBar", "true");
             System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Figurate");

             swing.edt {
                 lookAndFeel('system')
                 def menuBarUI = UIManager.get('MenuBarUI')
                 def menuUI = UIManager.get('MenuUI')
                 def menuItemUI = UIManager.get('MenuItemUI')
                 def checkBoxMenuItemUI = UIManager.get('CheckBoxMenuItemUI')
                 def radioButtonMenuItemUI = UIManager.get('RadioButtonMenuItemUI')
                 def popupMenuUI = UIManager.get('PopupMenuUI')
                 
                 lookAndFeel('seaglass', 'substance5', 'system')
                 UIManager.put('MenuBarUI', menuBarUI)
                 UIManager.put('MenuUI', menuUI)
                 UIManager.put('MenuItemUI', menuItemUI)
                 UIManager.put('CheckBoxMenuItemUI', checkBoxMenuItemUI)
                 UIManager.put('RadioButtonMenuItemUI', radioButtonMenuItemUI)
                 UIManager.put('PopupMenuUI', popupMenuUI)
             }
         }
         else {
             swing.edt {
                 lookAndFeel('seaglass', 'substance5', 'system')
             }
         }

         def headingFont = new Font('Arial', Font.PLAIN, 14)
         def textFont = new Font('Courier', Font.PLAIN, 12)
         def newFileCount = 0
         def navController = new NavController()
         def newTab = { tabFile ->
//             def breadcrumbBar = new BreadcrumbFileSelector()
//             def userDir = new File(System.getProperty("user.dir"))
//             breadcrumbBar.setPath(userDir)
             
             if (!tabFile) {
                 tabFile = new File(FileSystemView.fileSystemView.homeDirectory, "Unsaved File ${++newFileCount}")
             }

             RSyntaxTextArea textArea = new RSyntaxTextArea();
             
             //@Bindable String tabName = 'New Tab'
             def newPanel = swing.panel(name: tabFile.name, id: tabFile.absolutePath) {//,
//                     tabIcon: FileSystemView.fileSystemView.getSystemIcon(tabFile)) {
                     borderLayout()
//                     widget(breadcrumbBar, constraints: BorderLayout.NORTH)
//                     panel(constraints: BorderLayout.WEST) {
//                     splitPane(oneTouchExpandable: true, dividerLocation: 0) {
//                         scrollPane(constraints: "left", border: null) {
//                             list(id: 'fileList')
//                             fileList.cellRenderer = new FileListCellRenderer()
//                             fileList.valueChanged = {
//                                 if (fileList.selectedValue) {
//                                     def selectedFile = fileList.selectedValue new File(userDir, fileList.selectedValue)
//                                     if (selectedFile.isDirectory()) {
//                                         fileList.selectedIndex = -1
//                                         breadcrumbBar.setPath(selectedFile)
//                                     }
//                                     else {
//                                         editPane.text = selectedFile.text
//                                         editPane.caretPosition = 0
//                                         tab0.name = fileList.selectedValue
//                                         tab0.invalidate()
//                                         tab0.repaint()
//                                     }
//                                 }
//                                 else {
//                                     editPane.text = null
//                                 }
//                             }
//                         }

                    //syntaxTextArea(id: 'textArea', marginLineEnabled: true, whitespaceVisible: true, font: textFont)
                    //textArea.marginLineColor = Color.RED
                        if (tabFile.name =~ /\.java$/) {
                            textArea.syntaxEditingStyle = SyntaxConstants.SYNTAX_STYLE_JAVA
                        }
                        else if (tabFile.name =~ /\.groovy$/) {
                            textArea.syntaxEditingStyle = SyntaxConstants.SYNTAX_STYLE_GROOVY
                        }
                        else if (tabFile.name =~ /(?i)\.(properties|ini)$/) {
                            textArea.syntaxEditingStyle = SyntaxConstants.SYNTAX_STYLE_PROPERTIES_FILE
                        }
                        else if (tabFile.name =~ /\.(xml|xsl|xsd|rdf|xul|svg)$/) {
                            textArea.syntaxEditingStyle = SyntaxConstants.SYNTAX_STYLE_XML
//                            textArea.closeMarkupTags = true
                        }
                        else if (tabFile.name =~ /\.(html|htm)$/) {
                            textArea.syntaxEditingStyle = SyntaxConstants.SYNTAX_STYLE_HTML
//                            textArea.closeMarkupTags = true
                        }
                        else if (tabFile.name =~ /\.css$/) {
                            textArea.syntaxEditingStyle = SyntaxConstants.SYNTAX_STYLE_CSS
                        }
                        else if (tabFile.name =~ /\.sql$/) {
                            textArea.syntaxEditingStyle = SyntaxConstants.SYNTAX_STYLE_SQL
                        }
                        else if (tabFile.name =~ /\.js$/) {
                            textArea.syntaxEditingStyle = SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT
                        }
                        else if (tabFile.name =~ /(?i)\.(bat|cmd)$/) {
                            textArea.syntaxEditingStyle = SyntaxConstants.SYNTAX_STYLE_WINDOWS_BATCH
                        }
                        else if (tabFile.name =~ /\.(sh|.sh|.*rc|bash.*|profile)$/) {
                            textArea.syntaxEditingStyle = SyntaxConstants.SYNTAX_STYLE_UNIX_SHELL
                        }
                        else if (tabFile.name =~ /\.rb$/) {
                            textArea.syntaxEditingStyle = SyntaxConstants.SYNTAX_STYLE_RUBY
                        }
                        else if (tabFile.name =~ /\.py$/) {
                            textArea.syntaxEditingStyle = SyntaxConstants.SYNTAX_STYLE_PYTHON
                        }
                        else if (tabFile.name =~ /\.php$/) {
                            textArea.syntaxEditingStyle = SyntaxConstants.SYNTAX_STYLE_PHP
                        }
                        else if (tabFile.name =~ /\.jsp$/) {
                            textArea.syntaxEditingStyle = SyntaxConstants.SYNTAX_STYLE_JSP
                        }
                        else if (tabFile.name =~ /^Makefile/) {
                            textArea.syntaxEditingStyle = SyntaxConstants.SYNTAX_STYLE_MAKEFILE
                        }
                        else if (tabFile.name =~ /(?i)\.(cpp|cxx|h)$/) {
                            textArea.syntaxEditingStyle = SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS
                        }
                        else if (tabFile.name =~ /(?i)\.cs$/) {
                            textArea.syntaxEditingStyle = SyntaxConstants.SYNTAX_STYLE_CSHARP
                        }
                        else if (tabFile.name =~ /(?i)\.c$/) {
                            textArea.syntaxEditingStyle = SyntaxConstants.SYNTAX_STYLE_C
                        }
                        else if (tabFile.name =~ /(?i)\.pl$/) {
                            textArea.syntaxEditingStyle = SyntaxConstants.SYNTAX_STYLE_PERL
                        }
//                        else {
//                            textArea.whitespaceVisible = true
//                        }
                    textArea.marginLineEnabled = true
                    textArea.font = textFont
                    textArea.markAllHighlightColor = new Color(textArea.markAllHighlightColor.red, textArea.markAllHighlightColor.green, textArea.markAllHighlightColor.blue, 64)
                        textArea.addHyperlinkListener(new HyperlinkListenerImpl())
                        RTextScrollPane sp = new RTextScrollPane(textArea);
                        sp.gutter.bookmarkingEnabled = true
                        sp.gutter.bookmarkIcon = imageIcon('/bookmark.png', id: 'bookmarkIcon')
                        widget(sp)
                        doLater {
                            if (tabFile.exists()) {
                                textArea.text = tabFile.text
                                textArea.caretPosition = 0
                                textArea.discardAllEdits()
                            }
                        }
                        
                        textArea.focusGained = {
                            splitPane.dividerLocation = 0
                        }
                        
                        textArea.caretUpdate = {
                            def line = textArea.getLineOfOffset(textArea.caretPosition) + 1
                            def column = textArea.caretPosition - textArea.getLineStartOffset(line - 1)
                            def lineCount = textArea.lineCount
                            def lineLength = textArea.getLineEndOffset(line - 1) - textArea.getLineStartOffset(line - 1)
                            caretPosLabel.text = "${line}:${column} (${lineCount}:${lineLength})"
                            //updateCaretPosLabel(textArea, caretPosLabel)
                        }
                        /* XXX: this is buggy..
                        textArea.keyPressed = { e ->
                            if (e.keyCode == KeyEvent.VK_SHIFT && textArea.selectedText) {
                                textArea.markAll("(?<=\\W)${textArea.selectedText}(?=\\W)", true, false, true)
                            }
                        }
                        textArea.keyReleased = { e ->
                            if (e.keyCode == KeyEvent.VK_SHIFT && textArea.selectedText) {
                                textArea.clearMarkAllHighlights()
                            }
                        }
                        */
                        
                        bind(source: viewWordWrap, sourceProperty:'selected', target: textArea, targetProperty: 'lineWrap')
                        bind(source: viewWhitespace, sourceProperty:'selected', target: textArea, targetProperty: 'whitespaceVisible')
                        bind(source: viewLineNumbers, sourceProperty:'selected', target: sp, targetProperty: 'lineNumbersEnabled')

                        //sp.gutter.addLineTrackingIcon(0, imageIcon('F:/images/icons/logo.png'))
                        /*
                    }
                    else {
                         scrollPane(border: null) {
                             editorPane(id: 'editPane', font: textFont)
                             editPane.editorKit = new NumberedEditorKit()
                             def lineHighlighter = new LineHighlightPainter(new Color(230, 230, 230))
                             editPane.caretUpdate = { event ->
                                 def posStart = min(Utilities.getRowStart(editPane, event.dot), Utilities.getRowStart(editPane, event.mark))
                                 def posEnd = max(Utilities.getRowEnd(editPane, event.dot), Utilities.getRowEnd(editPane, event.mark))
                                 
                                 //Element elem = Utilities.getParagraphElement(editPane, event.dot)
                                 //posStart = elem.startOffset
                                 //posEnd = elem.endOffset
                                 
                                 def vetoHighlight = false
                                 //println posStart + '-' + posEnd
                                 for (highlight in editPane.highlighter.highlights) {
                                   if (highlight.painter == lineHighlighter) {
                                     editPane.highlighter.removeHighlight(highlight)
                                   }
                                   //else if (highlight.painter.startOffset <= posStart || highlight.painter.endOffset >= posEnd) {
                                   //    vetoHighlight = true
                                   //}
                                 }
                                 //println vetoHighlight
                                 if (!vetoHighlight) {
                                     editPane.highlighter.addHighlight(posStart, posEnd, lineHighlighter)
                                 }
                             }
                         }
                         doLater {
                             if (tabFile.exists()) {
                                 editPane.text = tabFile.text
                                 editPane.caretPosition = 0
                             }
                         }
                     }
                     */
//                  def fileModel = new DefaultListModel()
//                  for (file in userDir.listFiles()) {
//                    fileModel.addElement(file)
//                  }
//                  fileList.setModel(fileModel)
//             breadcrumbBar.model.addPathListener(new BreadcrumbPathListenerImpl({ //event -> println "${event}" }))
//                    swing.edt() {
//                    userDir = breadcrumbBar.model.getItem(breadcrumbBar.model.itemCount - 1).data
//                  fileModel = new DefaultListModel()
//                  for (file in userDir.listFiles()) {
//                    fileModel.addElement(file)
//                  }
//                    fileList.selectedIndex = -1
//                  fileList.setModel(fileModel)
//                }
//             }))
//                 }
             }

             newPanel.putClientProperty(SubstanceLookAndFeel.TABBED_PANE_CLOSE_BUTTONS_PROPERTY, true)
             newPanel.putClientProperty("figurate.file", tabFile)
             newPanel.putClientProperty("figurate.textArea", textArea)
             return newPanel
         }
         
         def openTab = { tabs, file ->
             if (file) {
                 if (tabs.tabCount > 0) {
                     for (i in 0..tabs.tabCount - 1) {
                         if (tabs.getComponentAt(i).getClientProperty('figurate.file').absolutePath == file.absolutePath) {
                             navController.addMark(tabs.selectedComponent)
                             tabs.selectedComponent = tabs.getComponentAt(i)
                             return
                         }
                     }
                 }
                 
                 def tab = newTab(file)
                 tabs.add(tab)
                 tabs.setIconAt(tabs.indexOfComponent(tab), new OverlayIcon(FileSystemView.fileSystemView.getSystemIcon(file), 16, 18))
                 tabs.setToolTipTextAt(tabs.indexOfComponent(tab), file.absolutePath)
                 navController.addMark(tabs.selectedComponent)
                 tabs.selectedComponent = tab
             }
         }
         
         def updatePath = { breadcrumbBar, pathField, newPath ->
             breadcrumbBar.path = newPath
             if (pathField.selectedItem != newPath) {
                 pathField.model.removeElement(newPath)
                 pathField.model.insertElementAt(newPath, 0)
                 pathField.selectedItem = newPath
             }
         }

        def updateCaretPosLabel = { textArea, caretPosLabel ->
            def line = textArea.getLineOfOffset(textArea.caretPosition) + 1
            def column = textArea.caretPosition - textArea.getLineStartOffset(line - 1)
            def lineCount = textArea.lineCount
            def lineLength = textArea.getLineEndOffset(line - 1) - textArea.getLineStartOffset(line - 1)
            caretPosLabel.text = "${line}:${column} (${lineCount}:${lineLength})"
        }
        
         swing.edt {
             frame(title: 'Figurate', id: 'figurateFrame', defaultCloseOperation: JFrame.DO_NOTHING_ON_CLOSE,
                     size: [800, 600], show: false, locationRelativeTo: null, iconImage: imageIcon('/logo.png', id: 'logoIcon').image) {
             
//                 lookAndFeel('substance5', 'system')
                 
                 actions() {
                     action(id: 'newFileAction', name: 'New', accelerator: shortcut('N'), closure: {
                         doLater {
                             def tab = newTab()
                             tabs.add(tab)
//                             tabs.setIconAt(tabs.indexOfComponent(tab), FileSystemView.fileSystemView.getSystemIcon(file))
//                             tabs.setToolTipTextAt(tabs.indexOfComponent(tab), file.absolutePath)
                             tabs.selectedComponent = tab
                         }
                     })
                     action(id: 'openFileAction', name: 'Open', accelerator: shortcut('O'), closure: {
                         if (chooser.showOpenDialog() == JFileChooser.APPROVE_OPTION) {
                             doLater {
                                //def tab = newTab(chooser.selectedFile)
                                //tabs.add(tab)
                                //tabs.setIconAt(tabs.indexOfComponent(tab), FileSystemView.fileSystemView.getSystemIcon(chooser.selectedFile))
                                //tabs.selectedComponent = tab
                                openTab(tabs, chooser.selectedFile)
                             }
                         }
                     })
                     action(id: 'saveFileAction', name: 'Save', accelerator: shortcut('S'), closure: {
                         def editor = navController.currentMark.editor
                         if (editor.getClientProperty('figurate.file').exists()) {
                             editor.getClientProperty('figurate.file').text = editor.getClientProperty('figurate.textArea').text
                         }
                         else if (chooser.showSaveDialog() == JFileChooser.APPROVE_OPTION) {
                             chooser.selectedFile.text = editor.getClientProperty('figurate.textArea').text
                             editor.putClientProperty('figurate.file', chooser.selectedFile)
                             editor.name = chooser.selectedFile.name
                             tabs.setTitleAt(tabs.selectedIndex, editor.name)
                         }
                     })
                     action(id: 'saveAsFileAction', name: 'Save As..', closure: {
                         def editor = navController.currentMark.editor
                         if (editor.getClientProperty('figurate.file').exists()) {
                             chooser.selectedFile = editor.getClientProperty('figurate.file')
                         }
                         if (chooser.showSaveDialog() == JFileChooser.APPROVE_OPTION) {
                             chooser.selectedFile.text = editor.getClientProperty('figurate.textArea').text
                             editor.putClientProperty('figurate.file', chooser.selectedFile)
                             editor.name = chooser.selectedFile.name
                             tabs.setTitleAt(tabs.selectedIndex, editor.name)
                         }
                     })
                     action(id: 'closeTabAction', name: 'Close Tab', accelerator: shortcut('W'))
                     action(id: 'closeAllTabsAction', name: 'Close All Tabs', accelerator: shortcut('shift W'))
                     action(id: 'printAction', name: 'Print', accelerator: shortcut('P'))
                     action(id: 'exitAction', name: 'Exit', accelerator: shortcut('Q'), closure: { close(figurateFrame, true) })
                     
                     def editorKitActions = [:]
                     for (action in new RSyntaxTextArea().actions) {
                         editorKitActions.put(action.getValue(Action.NAME), action)
                     }

                     action(new IncreaseFontSizeAction(), id: 'increaseFontAction', name: 'Increase Font Size', accelerator: shortcut(KeyEvent.VK_EQUALS))
                     action(new DecreaseFontSizeAction(), id: 'decreaseFontAction', name: 'Decrease Font Size', accelerator: shortcut(KeyEvent.VK_MINUS))
                     
                     action(editorKitActions.get(RTextAreaEditorKit.rtaUpperSelectionCaseAction), id: 'upperCaseAction', name: 'Upper Case', accelerator: shortcut("shift U"))
                     action(editorKitActions.get(RTextAreaEditorKit.rtaLowerSelectionCaseAction), id: 'lowerCaseAction', name: 'Lower Case', accelerator: shortcut("shift L"))
                     action(editorKitActions.get(RTextAreaEditorKit.rtaInvertSelectionCaseAction), id: 'invertCaseAction', name: 'Invert Case', accelerator: shortcut("shift I"))
                     
                     action(new BeginRecordingMacroAction(), id: 'beginMacroAction', name: 'Begin Recording')
                     action(editorKitActions.get(RTextAreaEditorKit.rtaEndRecordingMacroAction), id: 'endMacroAction', name: 'End Recording')
                     action(editorKitActions.get(RTextAreaEditorKit.rtaPlaybackLastMacroAction), id: 'playLastMacroAction', name: 'Playback Last', accelerator: shortcut("shift P"))

                     action(new TimeDateAction(), id: 'timeDateAction', name: 'Date / Time')
                     
                     action(id: 'onlineHelpAction', name: 'Online Help', accelerator: 'F1', closure: { Desktop.desktop.browse(URI.create('http://basetools.org/figurate')) })
                     action(id: 'showTipsAction', name: 'Tips', closure: { tips.showDialog(figurateFrame) })
                     action(id: 'aboutAction', name: 'About', closure: {
                         dialog(title: 'About Figurate', size: [350, 250], show: true, owner: figurateFrame, modal: true, locationRelativeTo: figurateFrame) {
                             borderLayout()
                             label(text: 'Figurate 1.0', constraints: BorderLayout.NORTH, border: emptyBorder(10))
                             panel(constraints: BorderLayout.CENTER, border: emptyBorder(10)) {
                                 borderLayout()
                                 scrollPane(horizontalScrollBarPolicy: JScrollPane.HORIZONTAL_SCROLLBAR_NEVER, border: null) {
                                     table(editable: false) {
                                         def systemProps = []
                                         for (propName in System.properties.keySet()) {
                                             systemProps.add([property: propName, value: System.properties.getProperty(propName)])
                                         }
                                         tableModel(list: systemProps) {
                                             propertyColumn(header:'Property', propertyName:'property')
                                             propertyColumn(header:'Value', propertyName:'value')
                                         }
                                     }
                                 }
                             }
                         }
                     })
                 }
                 
                 fileChooser(id: 'chooser')
                 
                 tipOfTheDay(id: 'tips', model: defaultTipModel(tips: [
                     defaultTip(name: 'test', tip: '<html><em>testing</em>')
                 ]))

                 menuBar() {
                     menu(text: "File", mnemonic: 'F') {
                         menuItem(newFileAction)
                         menuItem(openFileAction)
                         separator()
                         menuItem(closeTabAction)
                         menuItem(text: "Close Other Tabs")
                         menuItem(closeAllTabsAction)
                         separator()
                         menuItem(saveFileAction)
                         menuItem(saveAsFileAction)
                         menuItem(text: "Save All")
                         separator()
                         menuItem(printAction)
                         separator()
                         menuItem(exitAction)
                     }
                     menu(text: "Edit", mnemonic: 'E') {
                         // XXX: hack to initialise text actions..
                         new RTextArea()
                         menuItem(RTextArea.getAction(RTextArea.UNDO_ACTION))
                         menuItem(RTextArea.getAction(RTextArea.REDO_ACTION))
                         separator()
                         menuItem(RTextArea.getAction(RTextArea.CUT_ACTION))
                         menuItem(RTextArea.getAction(RTextArea.COPY_ACTION))
                         menuItem(RTextArea.getAction(RTextArea.PASTE_ACTION))
                         menuItem(RTextArea.getAction(RTextArea.DELETE_ACTION))
                         separator()
                         menuItem(RTextArea.getAction(RTextArea.SELECT_ALL_ACTION))
                         separator()
                         menuItem(text: "Preferences")
                     }
                     menu(text: "View", mnemonic: 'V') {
                         menuItem(increaseFontAction)
                         menuItem(decreaseFontAction)
                         separator()
                         checkBoxMenuItem(text: "Word Wrap", id: 'viewWordWrap')
                         checkBoxMenuItem(text: "Whitespace", id: 'viewWhitespace')
                         checkBoxMenuItem(text: "Line Numbers", id: 'viewLineNumbers')
                         separator()
                         checkBoxMenuItem(text: "Status Bar", id: 'viewStatusBar')
                     }
                     menu(text: "Tools", mnemonic: 'T') {
                         menu(text: "Transform") {
                             menuItem(upperCaseAction)
                             menuItem(lowerCaseAction)
                             menuItem(invertCaseAction)
                         }
                         menu(text: "Insert") {
                             menuItem(timeDateAction)
                         }
                         menu(text: "Macro") {
                             menuItem(beginMacroAction)
                             menuItem(endMacroAction)
                             menuItem(playLastMacroAction)
                         }
                     }
                     menu(text: "Help", mnemonic: 'H') {
                         menuItem(onlineHelpAction)
                         menuItem(showTipsAction)
                     separator()
                         menuItem(aboutAction)
                     }
                 }
                 
                 borderLayout()
                 
                 //def breadcrumbBar = new BreadcrumbFileSelector()
                 //fileBreadcrumbBar(id: 'breadcrumbBar')
                 
                 def userDir = FileSystemView.fileSystemView.homeDirectory //new File(System.getProperty("user.dir"))
                 //breadcrumbBar.path = userDir
                 
                 hbox(constraints: BorderLayout.NORTH, border:emptyBorder([5, 3, 3, 3])) {
                     //flowLayout(alignment: FlowLayout.LEADING)

                     def navButtons = new JCommandButtonStrip()
                     navButtons.displayState = CommandButtonDisplayState.FIT_TO_ICON
                     //navButtons.preferredSize = new java.awt.Dimension(50, 5)
                     def backIcon = SvgBatikResizableIcon.getSvgIcon(Figurate.class.getResource('/back.svg'), new java.awt.Dimension(20, 20))
                     def backButton = new JCommandButton(backIcon) //'Back')
//                     bind(source: navController, sourceProperty: 'currentMark', target: backButton, targetProperty: 'enabled', converter: { it?.previous != null })
                     //backButton enabled: bind { navController.currentMark && navController.currentMark.previous }
                     backButton.actionPerformed = { navController.setCurrentMark navController.currentMark?.previous }
                     navButtons.add(backButton)
                     
                     def forwardIcon = SvgBatikResizableIcon.getSvgIcon(Figurate.class.getResource('/forward.svg'), new java.awt.Dimension(20, 20))
                     def forwardButton = new JCommandButton(forwardIcon) //'Forward')
                     //forwardButton.enabled = bind { navController.currentMark && navController.currentMark?.next }
//                     bind(source: navController, sourceProperty: 'currentMark', target: forwardButton, targetProperty: 'enabled', converter: { it?.next != null })
                     forwardButton.actionPerformed = { navController.setCurrentMark navController.currentMark?.next }
                     navButtons.add(forwardButton)
                     widget(navButtons)
                     hstrut(5)
                     
                     def reloadIcon = SvgBatikResizableIcon.getSvgIcon(Figurate.class.getResource('/reload.svg'), new java.awt.Dimension(16, 16))
                     def reloadButton = new JCommandButton(reloadIcon) //'Reload')
                     //reloadButton.preferredSize = new java.awt.Dimension(40, 5)
                     widget(reloadButton)
                     hstrut(3)
                     
                     def findIcon = SvgBatikResizableIcon.getSvgIcon(Figurate.class.getResource('/find.svg'), new java.awt.Dimension(16, 16))
                     def findButton = new JCommandToggleButton(findIcon) //'Find')
                     //findButton.preferredSize = new java.awt.Dimension(30, 5)
                     widget(findButton)
                     hstrut(3)
                     
                     //toggleButton(id: 'showPathButton', constraints: BorderLayout.WEST, text: 'Path')
                     def pathIcon = SvgBatikResizableIcon.getSvgIcon(Figurate.class.getResource('/path.svg'), new java.awt.Dimension(16, 16))
                     def showPathButton = new JCommandToggleButton(pathIcon) //'Path')
                     //showPathButton.preferredSize = new java.awt.Dimension(30, 5)
                     
                     //def showPathButtonGroup = new CommandToggleButtonGroup()
                     //showPathButtonGroup.add(showPathButton)
                     widget(showPathButton)
                     hstrut(3)
                     
                     panel(id: 'togglePathPane') {
                         cardLayout()
                         hbox(constraints: 'breadcrumb') {
                             //borderLayout()
                             //widget(breadcrumbBar)
                             fileBreadcrumbBar(id: 'breadcrumbBar', path: userDir)
                         }
                         hbox(constraints: 'path') {
                             //borderLayout()
                             //textField(id: 'pathField', constraints: 'path')
                             //def pathFieldModel = new DefaultComboBoxModel()
                             comboBox(id: 'pathField', editable: true, renderer: new PathListCellRenderer()) //, model: pathFieldModel)
                             //def pathField = new MaxWidthComboBox()
                             //pathField.editable = true
                             //pathField.renderer = new PathListCellRenderer()
                             pathField.putClientProperty(org.jvnet.lafwidget.LafWidget.TEXT_SELECT_ON_FOCUS, true)
                             pathField.putClientProperty(org.jvnet.lafwidget.LafWidget.TEXT_FLIP_SELECT_ON_ESCAPE, true)
                             pathField.putClientProperty(org.jvnet.lafwidget.LafWidget.TEXT_EDIT_CONTEXT_MENU, true)
                             widget(pathField)
                             //pathField.maximumSize = new java.awt.Dimension(Short.MAX_VALUE, 20)
                             pathField.actionPerformed = {
                                 if (pathField.selectedItem) {
                                     def newPath = pathField.selectedItem //new File(pathField.editor.item)
                                     if (newPath instanceof String) {
                                         newPath = new File(newPath)
                                     }
                                     if (newPath.exists() && breadcrumbBar.model.getItem(breadcrumbBar.model.itemCount - 1).data != newPath) {
                                         updatePath(breadcrumbBar, pathField, newPath)
                                         //pathField.model.removeElement(newPath)
                                         //pathField.model.insertElementAt(newPath, 0)
                                         //pathField.selectedItem = newPath
                                         //breadcrumbBar.path = newPath
                                     }
                                     else {
                                         pathField.selectedItem = breadcrumbBar.model.getItem(breadcrumbBar.model.itemCount - 1).data
                                     }
                                 }
                             }
                         }
                     }
                     showPathButton.putClientProperty(SubstanceLookAndFeel.BUTTON_NO_MIN_SIZE_PROPERTY, true)
                     showPathButton.actionPerformed = {
                         if (showPathButton.actionModel.selected) {
                             togglePathPane.layout.show(togglePathPane, 'path')
                         }
                         else {
                             togglePathPane.layout.show(togglePathPane, 'breadcrumb')
                         }                         
                     }
                 }
                 
                 splitPane(id: 'splitPane', oneTouchExpandable: true, dividerLocation: 0) {
                     tabbedPane(constraints: "left", tabPlacement: JTabbedPane.BOTTOM, id: 'navTabs') {
                         panel(name: 'File System') {
                             borderLayout()
                             label(text: 'Folder Contents', constraints: BorderLayout.NORTH)
                             scrollPane(border: null) {
                                 list(id: 'fileList')
                                 fileList.cellRenderer = new FileListCellRenderer()
//                                 fileList.autoCreateRowSorter = true
                                 fileList.comparator = new FileComparator()
//                                 fileList.filterEnabled = true
                                 DataTipManager.get().register(fileList)
                                 def fileModel = new DefaultListModel()
                                 def files = FileSystemView.fileSystemView.getFiles(userDir, false)
//                                 def comparator = new FileComparator()
                                 Arrays.sort(files, fileList.comparator)
                                 for (file in files) {
                                     fileModel.addElement(file)
                                 }
                                 fileList.model = fileModel
                             }
                         }
                         panel(name: 'Bookmarks') {
                             borderLayout()
                             label(text: 'Bookmarks', constraints: BorderLayout.NORTH)
                             scrollPane(border: null) {
                                 tree(id: 'bookmarkTree', rootVisible: false, )
                             }
                         }
                     }
                     navTabs.putClientProperty(SubstanceLookAndFeel.TABBED_PANE_CONTENT_BORDER_KIND, SubstanceConstants.TabContentPaneBorderKind.SINGLE_FULL)
                     tabbedPane(constraints: "right", tabLayoutPolicy: JTabbedPane.SCROLL_TAB_LAYOUT, id: 'tabs') {
                         /*
                         panel(name: 'Home', tabIcon: imageIcon('F:/images/icons/liquidicity/note.png')) {
                             borderLayout()
                             vbox(constraints: BorderLayout.CENTER) {
                                 panel(constraints: BorderLayout.CENTER, border: emptyBorder(10)) {
                                     borderLayout()
                                     label('Recent Files', font: headingFont, constraints: BorderLayout.NORTH)
                                     table(constraints: BorderLayout.CENTER)
                                 }
                                 vglue()
                             }
                             vbox(constraints: BorderLayout.EAST) {
                                 panel(constraints: BorderLayout.NORTH, border: emptyBorder(10)) {
                                     borderLayout()
                                     label('Search', font: headingFont, constraints: BorderLayout.NORTH)
                                     panel(constraints: BorderLayout.CENTER) {
                                         borderLayout()
                                         textField('Enter query', font: headingFont,  foreground: Color.LIGHT_GRAY, border: null, constraints: BorderLayout.NORTH)
                                         vbox(constraints: BorderLayout.CENTER) {
                                             hyperlink('192.168.0.1')
                                             hyperlink('hdparm=1')
                                             vglue()
                                         }
                                     }
                                 }
                                 panel(constraints: BorderLayout.SOUTH, border: emptyBorder(10)) {
                                     borderLayout()
                                     label('Tags', font: headingFont, constraints: BorderLayout.NORTH)
                                     panel(constraints: BorderLayout.CENTER) {
                                         hyperlink('network')
                                         hyperlink('font', font: headingFont)
                                         hyperlink('printer settings')
                                     }
                                 }
                                 vglue()
                             }
                         }
                         */
                         newTab()
                         navController.addMark(tabs.selectedComponent)
                     }
                     tabs.putClientProperty(SubstanceLookAndFeel.TABBED_PANE_CONTENT_BORDER_KIND, SubstanceConstants.TabContentPaneBorderKind.SINGLE_FULL)
                     tabs.putClientProperty(SubstanceLookAndFeel.TABBED_PANE_CLOSE_CALLBACK, new TabCloseCallbackImpl())
                     tabs.putClientProperty(org.jvnet.lafwidget.LafWidget.TABBED_PANE_PREVIEW_PAINTER, new DefaultTabPreviewPainter())

                     tabs.stateChanged = {
                         if (tabs.selectedComponent) {
                             navController.addMark(tabs.selectedComponent)
                             
                             def tabFile = tabs.selectedComponent.getClientProperty("figurate.file")
                            def newPath = tabFile.parentFile
                             if (newPath.exists() && breadcrumbBar.model.getItem(breadcrumbBar.model.itemCount - 1).data != newPath) {
                                 //breadcrumbBar.path = newPath
                                 //pathField.model.removeElement(newPath)
                                 //pathField.model.insertElementAt(newPath, 0)
                                 //pathField.selectedItem = newPath
                                 updatePath(breadcrumbBar, pathField, newPath)
                             }
                             
                             // update syntax status..
                             syntaxLabel.text = tabs.selectedComponent.getClientProperty("figurate.textArea").syntaxEditingStyle
                             updateCaretPosLabel(tabs.selectedComponent.getClientProperty("figurate.textArea"), caretPosLabel)
                             
                             if (tabFile.exists()) {
                                 lastModLabel.text = new Date(tabFile.lastModified()).dateTimeString
                             }
                             else {
                                 lastModLabel.text = '<unsaved>'
                             }
                         }
                         else {
                             breadcrumbBar.path = FileSystemView.fileSystemView.homeDirectory //new File(System.getProperty("user.dir"))
                             syntaxLabel.text = 'none'
                             lastModLabel.text = ''
                         }
                     }
                 }
                 breadcrumbBar.model.addPathListener(new BreadcrumbPathListenerImpl({
                     doLater() {
                         userDir = breadcrumbBar.model.getItem(breadcrumbBar.model.itemCount - 1).data
                         def fileModel = new DefaultListModel()
                         def files = FileSystemView.fileSystemView.getFiles(userDir, false)
                         Arrays.sort(files, fileList.comparator)
                         for (file in files) {
                             fileModel.addElement(file)
                         }
                         fileList.selectedIndex = -1
                         fileList.setModel(fileModel)
                         //pathField.text = userDir.absolutePath
                         if (pathField.selectedItem != userDir) {
                             pathField.model.removeElement(userDir)
                             pathField.model.insertElementAt(userDir, 0)
                             pathField.selectedItem = userDir
                         }
                         
                         if (splitPane.dividerLocation == 0) {
                             //splitPane.dividerLocation = fileList.preferredSize.width + 20
                             splitPane.resetToPreferredSizes()
                         }
                     }
                 }))
                 
                 fileList.valueChanged = { e ->
                     if (fileList.selectedValue && !e.valueIsAdjusting) {
                         def selectedFile = fileList.selectedValue
                         if (selectedFile.isDirectory()) {
                             fileList.selectedIndex = -1
                             //breadcrumbBar.path = selectedFile
                             //pathField.model.removeElement(selectedFile)
                             //pathField.model.insertElementAt(selectedFile, 0)
                             //pathField.selectedItem = selectedFile
                             updatePath(breadcrumbBar, pathField, selectedFile)
                         }
                         else {
//                             editPane.text = selectedFile.text
//                             editPane.caretPosition = 0
                            //def tab = newTab(selectedFile)
                            //tabs.add(tab)
                            //tabs.setIconAt(tabs.indexOfComponent(tab), FileSystemView.fileSystemView.getSystemIcon(selectedFile))
                            //tabs.selectedComponent = tab
                            openTab(tabs, selectedFile)
//                            tabs.add(panel(name: selectedFile.name, id: selectedFile.absolutePath,
//                                    tabIcon: FileSystemView.fileSystemView.getSystemIcon(selectedFile)))
                         }
                     }
//                     else {
//                         editPane.text = null
//                     }
                 }
                 
                 if (SystemTray.isSupported()) {
                     TrayIcon trayIcon = new TrayIcon(imageIcon('/bookmark.gif').image, 'Figurate')
                     trayIcon.imageAutoSize = false
                     trayIcon.mousePressed = { event ->
                         if (event.button == MouseEvent.BUTTON1) {
                             figurateFrame.visible = true
                         }
                     }
                     
                     PopupMenu popupMenu = new PopupMenu('Figurate')
                     MenuItem openMenuItem = new MenuItem('Open')
                     openMenuItem.actionPerformed = {
                        figurateFrame.visible = true
                     }
                     popupMenu.add(openMenuItem)
                     //openMenuItem.addNotify()
                     //openMenuItem.font = openMenuItem.font.deriveFont(Font.BOLD)
                     popupMenu.addSeparator()
                     MenuItem exitMenuItem = new MenuItem('Exit')
                     exitMenuItem.actionPerformed = {
                         close(figurateFrame, true)
                     }
                     popupMenu.add(exitMenuItem)
                     trayIcon.popupMenu = popupMenu
                     
                     SystemTray.systemTray.add(trayIcon)
                 }
                 
                statusBar(constraints: BorderLayout.SOUTH, border:emptyBorder([5, 3, 3, 3]), id: 'fStatusBar') {
                    label(id: 'statusMessage', text: 'Ready', constraints: new JXStatusBar.Constraint(JXStatusBar.Constraint.ResizeBehavior.FILL))
                    label(id: 'caretPosLabel', text: '1:0')
                    label(id: 'syntaxLabel', text: 'text/plain') //, constraints: new JXStatusBar.Constraint(50))
                    label(id: 'lastModLabel', text: '<Unsaved>')
                }
                bind(source: viewStatusBar, sourceProperty:'selected', target:fStatusBar, targetProperty:'visible')
             }
             TrackerRegistry.instance.register(figurateFrame, 'figurateFrame');
             figurateFrame.windowClosing = {
                     close(figurateFrame, !SystemTray.isSupported())
             }
             figurateFrame.windowStateChanged = {
                 if ((figurateFrame.extendedState & Frame.MAXIMIZED_BOTH) != 0) {
                     splitPane.dividerLocation = -1
                 }
             }
             figurateFrame.visible = true
         }
     }
}

class NavController {
    //def history = []
    //@Bindable Boolean hasPrevious = false
    //@Bindable Boolean hasNext = false
    @Bindable HistoryMark currentMark
    
    void addMark(def editor) {
        //println "Adding mark.. ${!history.isEmpty()}"
        def mark = new HistoryMark()
        mark.editor = editor
        mark.cursorPos = editor.getClientProperty('figurate.textArea').caretPosition
        //hasPrevious = !history.isEmpty()
        //hasNext = false
        //history.add(mark)
        mark.previous = currentMark
        if (currentMark) {
          currentMark.next = mark
        }
        setCurrentMark mark
    }
    
    void setCurrentMark(def mark) {
        currentMark = mark
    }
}

class HistoryMark {
    def editor
    def cursorPos
    def previous
    def next
}
/*
class FigurateModel {
    @Bindable Map activeDocument = null  
    List openDocuments = []  
    DocumentState state = new DocumentState()  
}  
  
@Bindable class DocumentState {  
    boolean isDirty = false  
}
*/

class FileListCellRenderer extends DefaultListCellRenderer {
    def fsv = FileSystemView.fileSystemView
    
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
        if (value.exists()) {
            setIcon(fsv.getSystemIcon(value))
            setText(fsv.getSystemDisplayName(value))
        }
        else {
            setIcon(null)
        }
        return this
    }
}

class PathListCellRenderer extends DefaultListCellRenderer {
    def fsv = FileSystemView.fileSystemView
    
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
        if (value.exists()) {
            setIcon(fsv.getSystemIcon(value))
            setText(value.absolutePath)
        }
        else {
            setIcon(null)
        }
        return this
    }
}

class BreadcrumbPathListenerImpl implements BreadcrumbPathListener {
    def closure
    
    BreadcrumbPathListenerImpl(c) {
      closure = c
    }
    
    void breadcrumbPathEvent(BreadcrumbPathEvent event) {
        closure()
    }
}

class HyperlinkListenerImpl implements HyperlinkListener {

    void hyperlinkUpdate(HyperlinkEvent e) {
        Desktop.desktop.browse(e.URL.toURI())
    }
}

class NumberedEditorKit extends StyledEditorKit {
    public ViewFactory getViewFactory() {
        return new NumberedViewFactory();
    }
}

class NumberedViewFactory implements ViewFactory {
    public View create(Element elem) {
        String kind = elem.getName();
        if (kind != null)
            if (kind.equals(AbstractDocument.ContentElementName)) {
                return new LabelView(elem);
            }
            else if (kind.equals(AbstractDocument.ParagraphElementName)) {
//              return new ParagraphView(elem);
                return new NumberedParagraphView(elem);
            }
            else if (kind.equals(AbstractDocument.SectionElementName)) {
                return new NoWrapBoxView(elem, View.Y_AXIS);
            }
            else if (kind.equals(StyleConstants.ComponentElementName)) {
                return new ComponentView(elem);
            }
            else if (kind.equals(StyleConstants.IconElementName)) {
                return new IconView(elem);
            }
        // default to text display
        return new LabelView(elem);
    }
}

class NoWrapBoxView extends BoxView {
        public NoWrapBoxView(Element elem, int axis)
        {
            super(elem, axis);
        }
 
        public void layout(int width, int height)
        {
            super.layout(32768, height);
        }
        
        public float getMinimumSpan(int axis) {
            return super.getPreferredSpan(axis);
        }
}
    
class NumberedParagraphView extends ParagraphView {
    public static short NUMBERS_WIDTH=25;

    public NumberedParagraphView(Element e) {
        super(e);
        short top = 0;
        short left = 0;
        short bottom = 0;
        short right = 0;
        this.setInsets(top, left, bottom, right);
    }

    protected void setInsets(short top, short left, short bottom,
                             short right) {
        super.setInsets(top,(short)(left+NUMBERS_WIDTH),bottom,right);
    }

    public void paintChild(Graphics g, Rectangle r, int n) {
        super.paintChild(g, r, n);
        int previousLineCount = getPreviousLineCount();
        int numberX = r.x - getLeftInset();
        int numberY = r.y + r.height - 5;
        g.color = Color.LIGHT_GRAY
        g.drawString(Integer.toString(previousLineCount + n + 1), numberX, numberY);
    }

    public int getPreviousLineCount() {
        int lineCount = 0;
        View parent = this.getParent();
        int count = parent.getViewCount();
        for (int i = 0; i < count; i++) {
            if (parent.getView(i) == this) {
                break;
            }
            else {
                lineCount += parent.getView(i).getViewCount();
            }
        }
        return lineCount;
    }
}

    
class LineHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {
        
        public LineHighlightPainter(Color colour)
        {
            super(colour);
        }
        
        void paint(Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c) {
            super.paint(g, offs0, offs1, bounds, c);
        }
}

class TabCloseCallbackImpl implements TabCloseCallback {

      public TabCloseKind onAreaClick(JTabbedPane tabbedPane, int tabIndex, MouseEvent mouseEvent) {
        if (mouseEvent.getButton() != MouseEvent.BUTTON2)
          return TabCloseKind.NONE;
        if (mouseEvent.isShiftDown()) {
          return TabCloseKind.ALL;
        }
        return TabCloseKind.THIS;
      }

      public TabCloseKind onCloseButtonClick(JTabbedPane tabbedPane,
          int tabIndex, MouseEvent mouseEvent) {
        if (mouseEvent.isAltDown()) {
          return TabCloseKind.ALL_BUT_THIS;
        }
        if (mouseEvent.isShiftDown()) {
          return TabCloseKind.ALL;
        }
        return TabCloseKind.THIS;
      }

      public String getAreaTooltip(JTabbedPane tabbedPane, int tabIndex) {
        return null;
      }

      public String getCloseButtonTooltip(JTabbedPane tabbedPane,
          int tabIndex) {
        StringBuffer result = new StringBuffer();
        result.append("<html><body>");
        result.append("Mouse click closes <b>"
            + tabbedPane.getTitleAt(tabIndex) + "</b> tab");
        result
            .append("<br><b>Alt</b>-Mouse click closes all tabs but <b>"
                + tabbedPane.getTitleAt(tabIndex) + "</b> tab");
        result.append("<br><b>Shift</b>-Mouse click closes all tabs");
        result.append("</body></html>");
        return result.toString();
      }
}

class FileComparator implements Comparator<File> {
    public int compare(def f1, def f2) {
        if (f1.directory && !f2.directory) {
            return Integer.MIN_VALUE
        }
        else if (f2.directory && !f1.directory) {
            return Integer.MAX_VALUE
        }
        return f1.name.compareToIgnoreCase(f2.name)
    }
}

class MaxWidthComboBox extends JComboBox {

  public Dimension getMaximumSize() {
      Dimension maxSize = getPreferredSize()
      maxSize.width = Short.MAX_VALUE
      return maxSize
  }
}

class MaxWidthBreadcrumbFileSelector extends BreadcrumbFileSelector {

  public Dimension getMaximumSize() {
      Dimension maxSize = getPreferredSize()
      maxSize.width = Short.MAX_VALUE
      return maxSize
  }
}

class OverlayIcon implements Icon {
    
    int height
    int width
    Icon baseIcon
    
    OverlayIcon(Icon icon, int width, int height) {
        baseIcon = icon
        this.width = width
        this.height = height
    }
    
    int getIconHeight() {
        return height
    }
    
    int getIconWidth() {
        return width
    }
    
    void paintIcon(Component c, Graphics g, int x, int y) {
        baseIcon.paintIcon(c, g, (int) (x + (width - baseIcon.iconWidth) / 2), (int) (y + (height - baseIcon.iconHeight) / 2))
    }
}

class FigurateControl {
}
