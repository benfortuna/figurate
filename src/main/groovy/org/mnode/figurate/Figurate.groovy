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

import java.awt.Color
import java.awt.Font
import java.awt.Insets
import java.awt.BorderLayout
import java.awt.FlowLayout
import javax.swing.DefaultComboBoxModel
import javax.swing.DefaultListModel
import javax.swing.JFrame
import javax.swing.JTabbedPane
import javax.swing.JScrollPane
import javax.swing.UIManager
import javax.swing.filechooser.FileSystemView
import groovy.swing.SwingXBuilder
import groovy.swing.LookAndFeelHelper
import org.jvnet.substance.SubstanceLookAndFeel
import org.jvnet.substance.api.SubstanceConstants
import org.jvnet.flamingo.bcb.*
import org.jvnet.flamingo.bcb.core.BreadcrumbFileSelector

 /**
  * @author fortuna
  *
  */
@Grapes([
    @Grab(group='org.codehaus.griffon.swingxbuilder', module='swingxbuilder', version='0.1.6'),
    @Grab(group='net.java.dev.substance', module='substance', version='5.3'),
    @Grab(group='net.java.dev.substance', module='substance-swingx', version='5.3'),
    @Grab(group='org.swinglabs', module='swingx', version='0.9.2'),
    @Grab(group='jgoodies', module='forms', version='1.0.5'),
    @Grab(group='org.codehaus.griffon.flamingobuilder', module='flamingobuilder', version='0.2'),
    @Grab(group='net.java.dev.flamingo', module='flamingo', version='4.2'),
    @Grab(group='org.apache.xmlgraphics', module='batik-awt-util', version='1.7')])
public class Figurate {
     
     
     static void main(def args) {
         UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0))
         UIManager.put(org.jvnet.lafwidget.LafWidget.ANIMATION_KIND, org.jvnet.lafwidget.utils.LafConstants.AnimationKind.FAST.derive(2))
        LookAndFeelHelper.instance.addLookAndFeelAlias('substance5', 'org.jvnet.substance.skin.SubstanceNebulaLookAndFeel')
         def swing = new SwingXBuilder()
         swing.edt {
             lookAndFeel('substance5') //, 'system')
         }

         def headingFont = new Font('Arial', Font.PLAIN, 14)
         def textFont = new Font('Courier', Font.PLAIN, 12)
         def tabCount = 0
         def newTab = { 
             def breadcrumbBar = new BreadcrumbFileSelector()
             swing.panel(name: 'New Tab', id: 'tab' + tabCount) { //, tabIcon: imageIcon('/note.png')) {
                     borderLayout()
                     widget(breadcrumbBar, constraints: BorderLayout.NORTH)
//                     panel(constraints: BorderLayout.WEST) {
                     splitPane {
                         scrollPane(constraints: "left", border: null) {
                             list(id: 'fileList')
                         }
                         scrollPane(constraints: "right", border: null) {
                             editorPane(font: textFont)
                         }
                     }
                 }
//             }
             //(tab + tabCount).putClientProperty(SubstanceLookAndFeel.TABBED_PANE_CLOSE_BUTTONS_PROPERTY, true)
             /*
             breadcrumbBar.model.addPathListener({
                //public void breadcrumbPathEvent(BreadcrumbPathEvent event) {
//                    swing.edt() {
                  def fileModel = new DefaultListModel()
                  for (item in breadcrumbBar.model.items) {
                    fileModel.addElement(item.data.name)
                  }
                  fileList.setModel(fileModel)
                //}}
             } as BreadcrumbPathListener)*/
         }
         
         swing.edt {
             frame(title: 'Figurate', defaultCloseOperation: JFrame.DISPOSE_ON_CLOSE,
             size: [800, 600], show: true, locationRelativeTo: null) {
             
//             lookAndFeel("system")
             actions() {
                 action(id: 'newFileAction', name: 'New', accelerator: shortcut('N'), closure: {
                     tabs.add(newTab())
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
         tabbedPane(tabLayoutPolicy: JTabbedPane.SCROLL_TAB_LAYOUT, id: 'tabs') {
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
           }
                tabs.putClientProperty(SubstanceLookAndFeel.TABBED_PANE_CONTENT_BORDER_KIND , SubstanceConstants.TabContentPaneBorderKind.SINGLE_FULL)
         }
     }
     
}
