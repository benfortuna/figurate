/**
 * Copyright (c) 2010, Ben Fortuna
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  o Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 *  o Neither the name of Ben Fortuna nor the names of any other contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.mnode.figurate


import static org.jdesktop.swingx.JXStatusBar.Constraint.ResizeBehavior.*

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop 
import java.awt.event.KeyEvent;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RTextAreaEditorKit.BeginRecordingMacroAction;
import org.fife.ui.rtextarea.RTextAreaEditorKit.TimeDateAction;
import javax.swing.JScrollPane;



import org.fife.ui.rtextarea.RTextAreaEditorKit;

import javax.swing.Action;


import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;

import org.fife.ui.rsyntaxtextarea.FileLocation;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaEditorKit.DecreaseFontSizeAction;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaEditorKit.IncreaseFontSizeAction;
import org.jdesktop.swingx.JXStatusBar;
import org.mnode.ousia.HyperlinkBrowser;
import org.mnode.ousia.OusiaBuilder;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.pushingpixels.substance.api.SubstanceConstants;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;

import eu.medsea.mimeutil.MimeUtil;

MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.ExtensionMimeDetector")
UIManager.put(SubstanceLookAndFeel.TABBED_PANE_CONTENT_BORDER_KIND, SubstanceConstants.TabContentPaneBorderKind.SINGLE_FULL)

def newEditors = 0

def ousia = new OusiaBuilder()

def newEditor = { file ->
	
	String id
	if (file) {
		id = file.name
	}
	else {
		id = ++newEditors
	}
	
	ousia.build {
		
		sp = rSyntaxScrollPane {
			editor = textEditorPane(marginLineEnabled: true)
			editor.with {
				addHyperlinkListener(new HyperlinkBrowser())
				caretUpdate = {
					def line = getLineOfOffset(caretPosition) + 1
					def column = caretPosition - getLineStartOffset(line - 1)
					def lineCount = lineCount
					def lineLength = getLineEndOffset(line - 1) - getLineStartOffset(line - 1)
					caretPositionStatus.text = "${line}:${column} (${lineCount}:${lineLength})"
				}
				
				if (file) {
					editable = readOnly
	                load(FileLocation.create(file), null)
	                syntaxEditingStyle = MimeUtil.getMimeTypes(file).iterator().next()
					syntaxStatus.text = syntaxEditingStyle
	                caretPosition = 0
					
	                frame.title = "${fileFullPath} - ${rs('Figurate')}"
				}
				else {
	                frame.title = "Untitled ${id} - ${rs('Figurate')}"
				}
			}
			bind(source: viewWordWrap, sourceProperty:'selected', target: editor, targetProperty: 'lineWrap')
			bind(source: viewWhitespace, sourceProperty:'selected', target: editor, targetProperty: 'whitespaceVisible')
		}
		bind(source: viewLineNumbers, sourceProperty:'selected', target: sp, targetProperty: 'lineNumbersEnabled')
		
		sp.with {
			gutter.bookmarkingEnabled = true
			gutter.bookmarkIcon = imageIcon('/bookmark.png')
			putClientProperty 'figurate.id', id
		}
		return sp
	}
}

ousia.edt {
	lookAndFeel('system', 'substance-mariner')
//	lookAndFeel('substance-mariner')
	
    actions {
		action id: 'newEditorAction', name: rs('New'), accelerator: shortcut('N'), closure: {
			editor = newEditor()
			id = editor.getClientProperty('figurate.id')
			def content = windowManager.contentManager.addContent(id, "Untitled ${id}", null, editor, null)
			content.selected = true
//			windowManager.registerToolWindow id, "Untitled ${id}", null, editor, ToolWindowAnchor.BOTTOM
//			windowManager.getToolWindow(id).available = true
		}
		
        action id: 'openFileAction', name: rs('Open'), accelerator: shortcut('O'), closure: {
             if (chooser.showOpenDialog() == JFileChooser.APPROVE_OPTION) {
                 doLater {
					def editor = newEditor(chooser.selectedFile)
					id = editor.getClientProperty('figurate.id')
					def icon = FileSystemView.fileSystemView.getSystemIcon(chooser.selectedFile)
					def content = windowManager.contentManager.addContent(id, chooser.selectedFile.name, icon, editor, chooser.selectedFile.absolutePath)
					content.selected = true
//					windowManager.registerToolWindow id, id, null, editor, ToolWindowAnchor.BOTTOM
//					windowManager.getToolWindow(id).available = true
                 }
             }
         }
        
        action id: 'exitAction', name: rs('Exit'), accelerator: shortcut('Q'), closure: {
            System.exit(0)
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
                separator()
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
                menuItem(increaseFontAction)
                menuItem(decreaseFontAction)
                separator()
                checkBoxMenuItem(text: rs("Word Wrap"), id: 'viewWordWrap')
                checkBoxMenuItem(text: rs("Whitespace"), id: 'viewWhitespace')
                checkBoxMenuItem(text: rs("Line Numbers"), id: 'viewLineNumbers')
//                checkBoxMenuItem(text: "Tab Names", id: 'viewTabNames')
                separator()
                checkBoxMenuItem(text: rs("Status Bar"), id: 'viewStatusBar')
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
            }
            menu(text: rs("Help"), mnemonic: 'H') {
                menuItem(onlineHelpAction)
//                menuItem(showTipsAction)
				separator()
                menuItem(aboutAction)
            }
        }

		toolWindowManager(id: 'windowManager') {
		}
				
		statusBar(constraints: BorderLayout.SOUTH, id: 'statusBar') {
			label(text: rs('Ready'), constraints: new JXStatusBar.Constraint(FILL))
			label(text: '1:0', id: 'caretPositionStatus')
			label(text: 'text/plain', id: 'syntaxStatus')
			bind(source: viewStatusBar, sourceProperty:'selected', target:statusBar, targetProperty:'visible')
		}
    }

	windowManager.contentManager.addContent "New", "Untitled 1", null, newEditor(), null
//	windowManager.registerToolWindow "New", "Untitled 1", null, newEditor(), ToolWindowAnchor.BOTTOM
//	windowManager.getToolWindow("New").available = true
}
