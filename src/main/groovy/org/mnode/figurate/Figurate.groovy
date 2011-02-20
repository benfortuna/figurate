/**
 * This file is part of Figurate.
 *
 * Copyright (c) 2011, Ben Fortuna [fortuna@micronode.com]
 *
 * Figurate is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Figurate is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Figurate.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.mnode.figurate

import java.awt.BorderLayout
import java.awt.Color
import java.awt.Desktop
import java.awt.FlowLayout
import java.awt.GraphicsEnvironment
import java.awt.event.KeyEvent

import javax.swing.Action
import javax.swing.JFileChooser
import javax.swing.JFrame
import javax.swing.JOptionPane
import javax.swing.JScrollPane
import javax.swing.SwingConstants
import javax.swing.UIManager
import javax.swing.filechooser.FileSystemView
import javax.swing.text.JTextComponent

import org.fife.ui.rsyntaxtextarea.FileLocation
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaEditorKit.DecreaseFontSizeAction
import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaEditorKit.IncreaseFontSizeAction
import org.fife.ui.rsyntaxtextarea.spell.SpellingParser
import org.fife.ui.rtextarea.RTextArea
import org.fife.ui.rtextarea.RTextAreaEditorKit
import org.fife.ui.rtextarea.RTextAreaEditorKit.BeginRecordingMacroAction
import org.fife.ui.rtextarea.RTextAreaEditorKit.TimeDateAction
import org.jdesktop.swingx.JXStatusBar
import org.jdesktop.swingx.JXStatusBar.Constraint.ResizeBehavior
import org.mnode.ousia.OusiaBuilder
import org.noos.xing.mydoggy.ContentManagerUIListener
import org.noos.xing.mydoggy.ToolWindowAnchor
import org.pushingpixels.flamingo.api.common.JCommandButton.CommandButtonKind
import org.pushingpixels.substance.api.SubstanceConstants
import org.pushingpixels.substance.api.SubstanceLookAndFeel

import eu.medsea.mimeutil.MimeUtil

def initialFile
if (args) {
	initialFile = args[0]
}
//println initialFile

try {
	s = new Socket('localhost', 4444)
	if (initialFile) {
		s.withStreams { input, output ->
//			println "Open ${initialFile}"
			output << initialFile
		}
	}
	System.exit(0)
}
catch (Exception e) {
}

new File(System.getProperty("user.home"), ".figurate/logs").mkdirs()

MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.ExtensionMimeDetector")
UIManager.put(SubstanceLookAndFeel.TABBED_PANE_CONTENT_BORDER_KIND, SubstanceConstants.TabContentPaneBorderKind.SINGLE_FULL)

// spellchecker..
def spellingParser
dictionary = new File('english_dic-1.0.zip')
if (dictionary.exists()) {
	spellingParser = SpellingParser.createEnglishSpellingParser(dictionary, true)
	spellingParser.userDictionary = new File("${System.getProperty('user.home')}/.figurate", 'user.dic')
}

def editors = []

def ousia = new OusiaBuilder()

def updateCaretStatus = { textArea ->
	textArea.with {
		def line = getLineOfOffset(caretPosition) + 1
		def column = caretPosition - getLineStartOffset(line - 1)
		def lineCount = lineCount
		def lineLength = getLineEndOffset(line - 1) - getLineStartOffset(line - 1)
		ousia.caretPositionStatus.text = "${line}:${column} (${lineCount}:${lineLength})"
	}
}

def newEditor = { file ->
	
	def editor = new Editor(file)
	editor.sp.textArea.caretUpdate = { updateCaretStatus(editor.sp.textArea) }
	editor.sp.textArea.focusGained = {
		ousia.build {
			frame.title = "${editor.getClientProperty('figurate.id')} - ${rs('Figurate')}"
			syntaxStatus.text = editor.sp.textArea.syntaxEditingStyle
			updateCaretStatus(editor.sp.textArea)
		}
	}

	ousia.build {
		if (toolsSpellChecker.selected) {
			editor.spellingParser = spellingParser
		}
		bind(source: viewWordWrap, sourceProperty:'selected', target: editor.sp.textArea, targetProperty: 'lineWrap')
		bind(source: viewWhitespace, sourceProperty:'selected', target: editor.sp.textArea, targetProperty: 'whitespaceVisible')
		bind(source: viewLineNumbers, sourceProperty:'selected', target: editor.sp, targetProperty: 'lineNumbersEnabled')
//		bind(source: toolsBookmarks, sourceProperty:'selected', target: editor.sp.gutter, targetProperty: 'bookmarkingEnabled')
		bind(source: toolsSpellChecker, sourceProperty:'selected', target: editor, targetProperty: 'spellingParser', converter: { if (it) spellingParser })
	}
	editors << editor
	return editor
}

def openFile = { file ->
     ousia.doLater {
		def content = windowManager.contentManager.getContent(file.name)
		if (!content) {
	        def editor = newEditor(file)
	        id = editor.getClientProperty('figurate.id')
	        def icon = paddedIcon(FileSystemView.fileSystemView.getSystemIcon(file), size: [width: 16, height: 22])
	        content = windowManager.contentManager.addContent(id, file.name, icon, editor, file.absolutePath)
		}
        content.selected = true
     }
}

def openUrl = { url ->
	 ousia.doLater {
		def content = windowManager.contentManager.getContent(url.file)
		if (!content) {
			def editor = newEditor(url)
			id = editor.getClientProperty('figurate.id')
//			def icon = paddedIcon(FileSystemView.fileSystemView.getSystemIcon(file), size: [width: 16, height: 22])
			content = windowManager.contentManager.addContent(id, url.file.split('/')[-1], null, editor, url.path)
		}
		content.selected = true
	 }
}

def tailFile = { file ->
	 ousia.doLater {
		def tailWindow = windowManager.getToolWindow('Tail')
		if (!tailWindow) {
			id = file.name
			def icon = paddedIcon(FileSystemView.fileSystemView.getSystemIcon(file), size: [width: 16, height: 22])
			tailWindow = windowManager.registerToolWindow('Tail', file.name, icon, panel(), ToolWindowAnchor.BOTTOM)
			tailWindow.available = true
		}
		else {
			tailWindow.addToolWindowTab(file.name, panel())
		}
		tailWindow.active = true
	 }
}

Thread.start {
	def server = new ServerSocket(4444)
	while(true) {
		try {
			server.accept { socket ->
				socket.withStreams { input, output ->
					def reader = input.newReader()
					def buffer = reader.readLine()
					if (buffer) {
//						println "server received: $buffer"
//						println 'opening file..'
						openFile(new File(buffer))
					}
				}
			}
		}
		finally {
			ousia.doLater {
				frame.visible = true
			}
		}
	}
}

ousia.edt {
	lookAndFeel('system', 'substance-mariner')
//	lookAndFeel('substance-mariner')
	
    actions {
		action id: 'newEditorAction', name: rs('New'), accelerator: shortcut('N'), closure: {
			editor = newEditor()
			id = editor.getClientProperty('figurate.id')
			def content = windowManager.contentManager.addContent(id, "${id}", null, editor, null)
			content.selected = true
//			windowManager.registerToolWindow id, "Untitled ${id}", null, editor, ToolWindowAnchor.BOTTOM
//			windowManager.getToolWindow(id).available = true
		}
		
        action id: 'openFileAction', name: rs('Open'), accelerator: shortcut('O'), closure: {
             if (chooser.showOpenDialog() == JFileChooser.APPROVE_OPTION) {
				 openFile(chooser.selectedFile)
             }
        }
		
		action id: 'openUrlAction', name: rs('Open URL..'), closure: {
			url = JOptionPane.showInputDialog(frame, rs('URL'))
			if (url) {
				openUrl(new URL(url))
			}
		}

		action id: 'tailFileAction', name: rs('Tail..'), closure: {
			 if (chooser.showOpenDialog() == JFileChooser.APPROVE_OPTION) {
				 tailFile(chooser.selectedFile)
			 }
		}
		
		action id: 'saveFileAction', name: rs('Save'), accelerator: shortcut('S'), closure: {
			editor = JTextComponent.focusedComponent
			if (new File(editor.fileFullPath).exists()) {
				editor.save()
				messageStatus.text = "${editor.fileName} ${rs('saved.')}"
			}
			else {
				chooser.selectedFile = new File(editor.fileFullPath)
				if (chooser.showSaveDialog() == JFileChooser.APPROVE_OPTION) {
					editor.saveAs(FileLocation.create(chooser.selectedFile))
					editor.putClientProperty 'figurate.id', editor.fileName
					messageStatus.text = "${editor.fileName} ${rs('saved.')}"
				}
			}
		}
		
		action id: 'saveAsFileAction', name: rs('Save As..'), closure: {
			editor = JTextComponent.focusedComponent
			chooser.selectedFile = new File(editor.fileFullPath)
			if (chooser.showSaveDialog() == JFileChooser.APPROVE_OPTION) {
				editor.saveAs(FileLocation.create(chooser.selectedFile))
				editor.putClientProperty 'figurate.id', editor.fileName
				messageStatus.text = "${editor.fileName} ${rs('saved.')}"
			}
		}
		
		action id: 'saveCopyFileAction', name: rs('Save a Copy..'), closure: {
			editor = JTextComponent.focusedComponent
			chooser.selectedFile = new File(editor.fileFullPath)
			if (chooser.showSaveDialog() == JFileChooser.APPROVE_OPTION) {
				chooser.selectedFile.text = editor.text
				messageStatus.text = "${chooser.selectedFile.name} ${rs('saved.')}"
			}
		}
		
		action id: 'printAction', name: rs('Print..'), closure: {
			editor = JTextComponent.focusedComponent
			if (new File(editor.fileFullPath).exists()) {
				Desktop.desktop.print new File(editor.fileFullPath)
			}
		}
		
        action id: 'exitAction', name: rs('Exit'), accelerator: shortcut('Q'), closure: {
            System.exit(0)
        }
		
		def screenEnv = GraphicsEnvironment.localGraphicsEnvironment.defaultScreenDevice
		action id: 'fullScreenAction', name: rs('Fullscreen'), accelerator: 'F11', enabled: screenEnv.fullScreenSupported, closure: {
			// toggle..
			if (screenEnv.fullScreenWindow) {
				frame.dispose()
				frame.undecorated = false
				frame.resizable = true
				screenEnv.fullScreenWindow = null
				frame.visible = true
			}
			else {
				frame.dispose()
				frame.undecorated = true
				frame.resizable = false
				screenEnv.fullScreenWindow = frame
			}
		}
		
		def editorKitActions = [:]
		new RSyntaxTextArea().actions.each {
			editorKitActions[it.getValue(Action.NAME)] = it
		}

		action(new IncreaseFontSizeAction(), id: 'increaseFontAction', name: rs('Increase Font Size'), accelerator: shortcut(KeyEvent.VK_EQUALS))
		action(new DecreaseFontSizeAction(), id: 'decreaseFontAction', name: rs('Decrease Font Size'), accelerator: shortcut(KeyEvent.VK_MINUS))
		
		action(editorKitActions.get(RTextAreaEditorKit.rtaUpperSelectionCaseAction), id: 'upperCaseAction', name: rs('Upper Case'), accelerator: shortcut("shift U"))
		action(editorKitActions.get(RTextAreaEditorKit.rtaLowerSelectionCaseAction), id: 'lowerCaseAction', name: rs('Lower Case'), accelerator: shortcut("shift L"))
		action(editorKitActions.get(RTextAreaEditorKit.rtaInvertSelectionCaseAction), id: 'invertCaseAction', name: rs('Invert Case'), accelerator: shortcut("shift I"))
		
		action(new BeginRecordingMacroAction(), id: 'beginMacroAction', name: rs('Begin Recording'))
		action(editorKitActions.get(RTextAreaEditorKit.rtaEndRecordingMacroAction), id: 'endMacroAction', name: rs('End Recording'))
		action(editorKitActions.get(RTextAreaEditorKit.rtaPlaybackLastMacroAction), id: 'playLastMacroAction', name: rs('Playback Last'), accelerator: shortcut("shift P"))

		action(new TimeDateAction(), id: 'timeDateAction', name: rs('Date / Time'))
		
//		action id: 'toggleSpellCheckerAction', name: rs('Spell Checker'), closure: {
//			editors.each {
//				it.textArea.with {
//					if (parserCount > 0) {
//						clearParsers()
//					}
//					else {
//						addParser spellingParser
//					}
//				}
//			}
//		}
		
		action id: 'openBrowserAction', name: rs('Open in Browser'), closure: {
			editor = JTextComponent.focusedComponent
			def url = new File(editor.fileFullPath).toURL()
			Desktop.getDesktop().browse(url.toURI())
		}

		action id: 'onlineHelpAction', name: rs('Online Help'), accelerator: 'F1', closure: {
			Desktop.desktop.browse(URI.create('http://basetools.org/figurate'))
		}
		
		action id: 'aboutAction', name: rs('About'), closure: {
			dialog(title: rs('About Figurate'), size: [350, 250], show: true, owner: frame, modal: true, locationRelativeTo: frame) {
				borderLayout()
				label(text: "${rs('Figurate')} 1.0", constraints: BorderLayout.NORTH, border: emptyBorder(10))
				panel(constraints: BorderLayout.CENTER, border: emptyBorder(10)) {
					borderLayout()
					scrollPane(horizontalScrollBarPolicy: JScrollPane.HORIZONTAL_SCROLLBAR_NEVER, border: null) {
						table(gridColor: Color.LIGHT_GRAY) {
							def systemProps = []
							for (propName in System.properties.keySet()) {
								systemProps.add([property: propName, value: System.properties.getProperty(propName)])
							}
							tableModel(list: systemProps) {
								propertyColumn(header: rs('Property'), propertyName:'property')
								propertyColumn(header: rs('Value'), propertyName:'value')
							}
						}
					}
				}
			}
		}
    }
    
    fileChooser(id: 'chooser')
    
    frame(title: rs('Figurate'), size: [640, 480], show: true, locationRelativeTo: null,
		 defaultCloseOperation: JFrame.EXIT_ON_CLOSE, id: 'frame', iconImage: imageIcon('/logo.png', id: 'logoIcon').image) {
		 
        menuBar {
            menu(text: rs('File'), mnemonic: 'F') {
				menuItem(newEditorAction)
                menuItem(openFileAction)
                menuItem(openUrlAction)
//                menuItem(tailFileAction)
                menuItem(saveFileAction)
                menuItem(saveAsFileAction)
                menuItem(saveCopyFileAction)
                separator()
                menuItem(printAction)
                menuItem(exitAction)
            }
			menu(text: rs('Edit'), mnemonic: 'E') {
			    // XXX: hack to initialise text actions..
//			    new RTextArea()
			    menuItem(RTextArea.getAction(RTextArea.UNDO_ACTION))
			    menuItem(RTextArea.getAction(RTextArea.REDO_ACTION))
			    separator()
			    menuItem(RTextArea.getAction(RTextArea.CUT_ACTION))
			    menuItem(RTextArea.getAction(RTextArea.COPY_ACTION))
			    menuItem(RTextArea.getAction(RTextArea.PASTE_ACTION))
			    menuItem(RTextArea.getAction(RTextArea.DELETE_ACTION))
			    separator()
			    menuItem(RTextArea.getAction(RTextArea.SELECT_ALL_ACTION))
//			    separator()
//			    menuItem(text: "Preferences")
			}
            menu(text: rs('View'), mnemonic: 'V') {
				menu(rs('Sidebar')) {
					checkBoxMenuItem(text: rs("File Explorer"), id: 'viewFileExplorer', accelerator: shortcut('E'))
				}
                separator()
                menuItem(increaseFontAction)
                menuItem(decreaseFontAction)
                separator()
                checkBoxMenuItem(text: rs("Word Wrap"), id: 'viewWordWrap')
                checkBoxMenuItem(text: rs("Whitespace"), id: 'viewWhitespace')
                checkBoxMenuItem(text: rs("Line Numbers"), id: 'viewLineNumbers')
//                checkBoxMenuItem(text: "Tab Names", id: 'viewTabNames')
                separator()
                checkBoxMenuItem(text: rs("Tool Bar"), id: 'viewToolBar')
                checkBoxMenuItem(text: rs("Status Bar"), id: 'viewStatusBar')
                checkBoxMenuItem(fullScreenAction)
            }
            menu(text: rs("Tools"), mnemonic: 'T') {
                menu(text: rs("Transform")) {
                    menuItem(upperCaseAction)
                    menuItem(lowerCaseAction)
                    menuItem(invertCaseAction)
                }
                menu(text: rs("Insert")) {
                    menuItem(timeDateAction)
                }
                menu(text: rs("Macro")) {
                    menuItem(beginMacroAction)
                    menuItem(endMacroAction)
                    menuItem(playLastMacroAction)
                }
//				checkBoxMenuItem(rs('Bookmarks'), id: 'toolsBookmarks')
				checkBoxMenuItem(rs('Spell Checker'), id: 'toolsSpellChecker', enabled: bind {spellingParser != null})
				menuItem(openBrowserAction)
            }
            menu(text: rs("Help"), mnemonic: 'H') {
                menuItem(onlineHelpAction)
//                menuItem(showTipsAction)
				separator()
                menuItem(aboutAction)
            }
        }

		panel(constraints: BorderLayout.NORTH, id: 'toolBar', border: emptyBorder([0, 20, 0, 0])) {
			flowLayout(alignment: FlowLayout.LEADING)
/*			
			resizableIcon('/back.svg', size: [20, 20], id: 'backIcon')
			resizableIcon('/forward.svg', size: [20, 20], id: 'forwardIcon')
			resizableIcon('/reload.svg', size: [20, 20], id: 'reloadIcon')
			
			commandButtonStrip(displayState: CommandButtonDisplayState.FIT_TO_ICON) {
				commandButton rs('Back'), icon: backIcon, commandButtonKind: CommandButtonKind.ACTION_ONLY
				commandButton rs('Forward'), icon: forwardIcon
			}
			commandButton(rs('Reload'), icon: reloadIcon, displayState: CommandButtonDisplayState.FIT_TO_ICON, , commandButtonKind: CommandButtonKind.ACTION_ONLY)
			comboBox(id: 'syntaxStyleCombo', model: new DefaultComboBoxModel(['text/plain']))
*/
			bind(source: viewToolBar, sourceProperty:'selected', target:toolBar, targetProperty:'visible')
		}
		
		toolWindowManager(id: 'windowManager') {
			windowManager.contentManager.contentManagerUI.addContentManagerUIListener(
				[contentUIRemoving: { e ->
					if (e.contentUI.content.component.sp.textArea.isDirty()) {
						def filename = e.contentUI.content.component.sp.textArea.fileName
		                def selection = JOptionPane.showConfirmDialog(windowManager, "Save changes to ${filename}?")
		                if (selection == JOptionPane.YES_OPTION) {
//		                    file.text = tabComponent.getClientProperty('figurate.textArea').text
		                }
		                else if (selection == JOptionPane.CANCEL_OPTION) {
		                    return false
		                }
					}
					return true
				},
				contentUIDetached: {}
			] as ContentManagerUIListener)
		}
				
		statusBar(constraints: BorderLayout.SOUTH, id: 'statusBar') {
			label(text: rs('Ready'), id: 'messageStatus', constraints: new JXStatusBar.Constraint(ResizeBehavior.FILL))
			label(text: '1:0', id: 'caretPositionStatus', horizontalAlignment: SwingConstants.CENTER, toolTipText: 'Cursor position (line:column)', constraints: new JXStatusBar.Constraint(80))
			label(text: 'text/plain', id: 'syntaxStatus', horizontalAlignment: SwingConstants.CENTER, toolTipText: 'Syntax Highlighting', constraints: new JXStatusBar.Constraint(80))
			bind(source: viewStatusBar, sourceProperty:'selected', target:statusBar, targetProperty:'visible')
		}
    }

	if (initialFile) {
		openFile(new File(initialFile))
	}
	else {
//		windowManager.contentManager.addContent "New", "Untitled 1", null, newEditor(), null
		editor = newEditor()
		id = editor.getClientProperty('figurate.id')
		def content = windowManager.contentManager.addContent(id, "${id}", null, editor, null)
		content.selected = true
		editor.sp.textArea.requestFocus()
	}
	
    def explorer = new FileTreePanel()
    explorer.tree.mouseClicked = { e ->
        def selected = e.source.selectionPath?.lastPathComponent
        if (e.clickCount == 2 && selected?.leaf) {
			openFile(selected.file)
        }
	}
	def explorerWindow = windowManager.registerToolWindow("File Explorer", "Filesystem", null, explorer, ToolWindowAnchor.LEFT)
	bind(source: viewFileExplorer, sourceProperty:'selected', target:explorerWindow, targetProperty:'available')
}
