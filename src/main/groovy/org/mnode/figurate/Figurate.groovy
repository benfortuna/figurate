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

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.text.*;
import java.awt.Color
import java.awt.Font
import java.awt.Insets
import java.awt.BorderLayout
import java.awt.FlowLayout
import java.awt.event.MouseEvent
import javax.swing.DefaultComboBoxModel
import javax.swing.DefaultListModel
import javax.swing.DefaultListCellRenderer
import javax.swing.JFrame
import javax.swing.JTabbedPane
import javax.swing.JScrollPane
import javax.swing.JList
import javax.swing.UIManager
import javax.swing.filechooser.FileSystemView
import groovy.beans.Bindable
import groovy.swing.SwingXBuilder
import groovy.swing.LookAndFeelHelper
import org.jvnet.substance.SubstanceLookAndFeel
import org.jvnet.substance.api.SubstanceConstants
import org.jvnet.substance.api.SubstanceConstants.TabCloseKind
import org.jvnet.substance.api.tabbed.TabCloseCallback
import org.jvnet.flamingo.bcb.*
import org.jvnet.flamingo.bcb.core.BreadcrumbFileSelector
import org.mnode.base.views.tracker.TrackerRegistry;

 /**
  * @author fortuna
  *
  */
/*@Grapes([
    @Grab(group='org.codehaus.griffon.swingxbuilder', module='swingxbuilder', version='0.1.6'),
    @Grab(group='net.java.dev.substance', module='substance', version='5.3'),
    @Grab(group='net.java.dev.substance', module='substance-swingx', version='5.3'),
    //@Grab(group='org.swinglabs', module='swingx', version='0.9.2'),
    @Grab(group='org.mnode.base', module='base-views', version='0.0.1-SNAPSHOT'),
    @Grab(group='jgoodies', module='forms', version='1.0.5'),
    @Grab(group='org.codehaus.griffon.flamingobuilder', module='flamingobuilder', version='0.2'),
    @Grab(group='net.java.dev.flamingo', module='flamingo', version='4.2'),
    @Grab(group='org.apache.xmlgraphics', module='batik-awt-util', version='1.7')])
    */
class Figurate {
     
     static void main(args) {
         UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0))
         UIManager.put(org.jvnet.lafwidget.LafWidget.ANIMATION_KIND, org.jvnet.lafwidget.utils.LafConstants.AnimationKind.FAST.derive(2))
         LookAndFeelHelper.instance.addLookAndFeelAlias('substance5', 'org.jvnet.substance.skin.SubstanceNebulaLookAndFeel')
        
         def swing = new SwingXBuilder()
         swing.edt {
             lookAndFeel('substance5', 'system')
         }

         def headingFont = new Font('Arial', Font.PLAIN, 14)
         def textFont = new Font('Courier', Font.PLAIN, 12)
//         def tabCount = 0
         def newTab = { tabFile ->
//             def breadcrumbBar = new BreadcrumbFileSelector()
//             def userDir = new File(System.getProperty("user.dir"))
//             breadcrumbBar.setPath(userDir)
             
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
                             editPane.text = tabFile.text
                             editPane.caretPosition = 0
                         }
//                     }
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
             newPanel.putClientProperty("figurate.file", tabFile.absolutePath)
             return newPanel
         }
         
         swing.edt {
             frame(title: 'Figurate', id: 'figurateFrame', defaultCloseOperation: JFrame.DISPOSE_ON_CLOSE,
                     size: [800, 600], show: false, locationRelativeTo: null) {
             
//                 lookAndFeel('substance5', 'system')
                 
                 actions() {
                     action(id: 'newFileAction', name: 'New', accelerator: shortcut('N'), closure: {
                         doLater {
                             tabs.add(newTab())
                         }
                     })
                     action(id: 'openFileAction', name: 'Open', accelerator: shortcut('O'))
                     action(id: 'closeTabAction', name: 'Close Tab', accelerator: shortcut('W'))
                     action(id: 'closeAllTabsAction', name: 'Close All Tabs', accelerator: shortcut('shift W'))
                     action(id: 'printAction', name: 'Print', accelerator: shortcut('P'))
                     action(id: 'exitAction', name: 'Exit', accelerator: shortcut('Q'), closure: { dispose() })
    
                     action(id: 'onlineHelpAction', name: 'Online Help', accelerator: 'F1')
                 }
                 
                 menuBar() {
                     menu(text: "File", mnemonic: 'F') {
                         menuItem(newFileAction)
                         menuItem(openFileAction)
                         separator()
                         menuItem(closeTabAction)
                         menuItem(text: "Close Other Tabs")
                         menuItem(closeAllTabsAction)
                         separator()
                         menuItem(printAction)
                         separator()
                         menuItem(exitAction)
                     }
                     menu(text: "Edit", mnemonic: 'E') {
                         menuItem(text: "Undo")
                         menuItem(text: "Redo")
                         separator()
                         menuItem(text: "Cut")
                         menuItem(text: "Copy")
                         menuItem(text: "Paste")
                         menuItem(text: "Delete")
                         separator()
                         menuItem(text: "Preferences")
                     }
                     menu(text: "View", mnemonic: 'V') {
                         menuItem(text: "Status Bar")
                     }
                     menu(text: "Tools", mnemonic: 'T') {
                         menu(text: "Search") {
                             menuItem(text: "New Search..")
                         }
                     }
                     menu(text: "Help", mnemonic: 'H') {
                         menuItem(onlineHelpAction)
                         menuItem(text: "Tips")
                     separator()
                         menuItem(text: "About")
                     }
                 }
                 
                 borderLayout()
                 
                 def breadcrumbBar = new BreadcrumbFileSelector()
                 def userDir = new File(System.getProperty("user.dir"))
                 breadcrumbBar.setPath(userDir)
                 
                 panel(constraints: BorderLayout.NORTH) {
                     borderLayout()
                     toggleButton(id: 'showPathButton', constraints: BorderLayout.WEST, text: 'Path')
                     panel(id: 'togglePathPane') {
                         cardLayout()
                         panel(constraints: 'breadcrumb') {
                             borderLayout()
                             widget(breadcrumbBar)
                         }
                         textField(id: 'pathField', constraints: 'path')
                         pathField.putClientProperty(org.jvnet.lafwidget.LafWidget.TEXT_SELECT_ON_FOCUS, true)
                         pathField.putClientProperty(org.jvnet.lafwidget.LafWidget.TEXT_FLIP_SELECT_ON_ESCAPE, true)
                         pathField.putClientProperty(org.jvnet.lafwidget.LafWidget.TEXT_EDIT_CONTEXT_MENU, true)
                         pathField.actionPerformed = {
                             breadcrumbBar.path = new File(pathField.text)
                         }
                     }
                     showPathButton.putClientProperty(SubstanceLookAndFeel.BUTTON_NO_MIN_SIZE_PROPERTY, true)
                     showPathButton.actionPerformed = {
                         if (showPathButton.selected) {
                             togglePathPane.layout.show(togglePathPane, 'path')
                         }
                         else {
                             togglePathPane.layout.show(togglePathPane, 'breadcrumb')
                         }                         
                     }
                 }
                 
                 splitPane(oneTouchExpandable: true, dividerLocation: 0) {
                     scrollPane(constraints: "left", border: null) {
                         list(id: 'fileList')
                         fileList.cellRenderer = new FileListCellRenderer()
                     }
                     tabbedPane(constraints: "right", tabLayoutPolicy: JTabbedPane.SCROLL_TAB_LAYOUT, id: 'tabs') {
                         panel(name: 'Home', tabIcon: imageIcon('/note.png')) {
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
                     }
                     tabs.putClientProperty(SubstanceLookAndFeel.TABBED_PANE_CONTENT_BORDER_KIND, SubstanceConstants.TabContentPaneBorderKind.SINGLE_FULL)
                     tabs.putClientProperty(SubstanceLookAndFeel.TABBED_PANE_CLOSE_CALLBACK, new TabCloseCallbackImpl())
                     tabs.putClientProperty(org.jvnet.lafwidget.LafWidget.TABBED_PANE_PREVIEW_PAINTER, org.jvnet.lafwidget.utils.LafConstants.TabOverviewKind.GRID)
                     tabs.stateChanged = {
                        breadcrumbBar.path = new File(tabs.selectedComponent.getClientProperty("figurate.file")).parentFile
                     }
                 }
                 def fileModel = new DefaultListModel()
                 def files = userDir.listFiles()
                 def comparator = new FileComparator()
                 Arrays.sort(files, comparator)
                 for (file in files) {
                     fileModel.addElement(file)
                 }
                 fileList.setModel(fileModel)
                 breadcrumbBar.model.addPathListener(new BreadcrumbPathListenerImpl({
                     doLater() {
                         userDir = breadcrumbBar.model.getItem(breadcrumbBar.model.itemCount - 1).data
                         fileModel = new DefaultListModel()
                         files = userDir.listFiles()
                         Arrays.sort(files, comparator)
                         for (file in files) {
                             fileModel.addElement(file)
                         }
                         fileList.selectedIndex = -1
                         fileList.setModel(fileModel)
                         pathField.text = userDir.absolutePath
                     }
                 }))
                 
                 fileList.valueChanged = { e ->
                     if (fileList.selectedValue && !e.valueIsAdjusting) {
                         def selectedFile = fileList.selectedValue
                         if (selectedFile.isDirectory()) {
                             fileList.selectedIndex = -1
                             breadcrumbBar.setPath(selectedFile)
                         }
                         else {
//                             editPane.text = selectedFile.text
//                             editPane.caretPosition = 0
                            def tab = newTab(selectedFile)
                            tabs.add(tab)
                            tabs.setIconAt(tabs.indexOfComponent(tab), FileSystemView.fileSystemView.getSystemIcon(selectedFile))
                            tabs.selectedComponent = tab
//                            tabs.add(panel(name: selectedFile.name, id: selectedFile.absolutePath,
//                                    tabIcon: FileSystemView.fileSystemView.getSystemIcon(selectedFile)))
                         }
                     }
//                     else {
//                         editPane.text = null
//                     }
                 }
             }
             TrackerRegistry.instance.register(figurateFrame, 'figurateFrame');
             figurateFrame.visible = true
         }
     }
}

class FigurateModel {
    @Bindable Map activeDocument = null  
    List openDocuments = []  
    DocumentState state = new DocumentState()  
}  
  
@Bindable class DocumentState {  
    boolean isDirty = false  
}

class FileListCellRenderer extends DefaultListCellRenderer {
    def fsv = FileSystemView.fileSystemView
    
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
        setIcon(fsv.getSystemIcon(value))
        setText(fsv.getSystemDisplayName(value))
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
    public int compare(File f1, File f2) {
        if (f1.directory && !f2.directory) {
            return Integer.MIN_VALUE
        }
        else if (f2.directory && !f1.directory) {
            return Integer.MAX_VALUE
        }
        return f1.name.compareToIgnoreCase(f2.name)
    }
    
    public int compare(Object o1, Object o2) {
        return compare((File) o1, (File) o2)
    }
}
