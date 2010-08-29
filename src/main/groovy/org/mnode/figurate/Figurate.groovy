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
import org.fife.ui.rsyntaxtextarea.FileLocation;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaEditorKit.DecreaseFontSizeAction;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaEditorKit.IncreaseFontSizeAction;
import org.jdesktop.swingx.JXStatusBar;
import org.mnode.ousia.HyperlinkBrowser;
import org.mnode.ousia.OusiaBuilder;

import eu.medsea.mimeutil.MimeUtil;

MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.ExtensionMimeDetector")

new OusiaBuilder().edt {
    lookAndFeel('substance-mariner')
    
    actions {
        action id: 'openFileAction', name: 'Open', accelerator: shortcut('O'), closure: {
             if (chooser.showOpenDialog() == JFileChooser.APPROVE_OPTION) {
                 doLater {
                    editor.load(FileLocation.create(chooser.selectedFile), null)
                    editor.syntaxEditingStyle = MimeUtil.getMimeTypes(chooser.selectedFile).iterator().next()
					syntaxStatus.text = editor.syntaxEditingStyle
                    editor.caretPosition = 0
                    frame.title = "${editor.fileFullPath} - Figurate"
                 }
             }
         }
        
        action id: 'exitAction', name: 'Exit', accelerator: shortcut('Q'), closure: {
            System.exit(0)
        }
		
		def editorKitActions = [:]
		new RSyntaxTextArea().actions.each {
			editorKitActions[it.getValue(Action.NAME)] = it
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
		
		action id: 'onlineHelpAction', name: 'Online Help', accelerator: 'F1', closure: {
			Desktop.desktop.browse(URI.create('http://basetools.org/figurate'))
		}
		
		action id: 'aboutAction', name: 'About', closure: {
			dialog(title: 'About Figurate', size: [350, 250], show: true, owner: frame, modal: true, locationRelativeTo: frame) {
				borderLayout()
				label(text: 'Figurate 1.0', constraints: BorderLayout.NORTH, border: emptyBorder(10))
				panel(constraints: BorderLayout.CENTER, border: emptyBorder(10)) {
					borderLayout()
					scrollPane(horizontalScrollBarPolicy: JScrollPane.HORIZONTAL_SCROLLBAR_NEVER, border: null) {
						table() {
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
		}
    }
    
    fileChooser(id: 'chooser')
    
    frame(title: 'Figurate', size: [640, 480], show: true, locationRelativeTo: null,
		 defaultCloseOperation: JFrame.EXIT_ON_CLOSE, id: 'frame', iconImage: imageIcon('/logo.png', id: 'logoIcon').image) {
		 
        menuBar {
            menu(text: "File", mnemonic: 'F') {
                menuItem(openFileAction)
                separator()
                menuItem(exitAction)
            }
			menu(text: "Edit", mnemonic: 'E') {
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
            menu(text: "View", mnemonic: 'V') {
                menuItem(increaseFontAction)
                menuItem(decreaseFontAction)
//                separator()
//                checkBoxMenuItem(text: "Word Wrap", id: 'viewWordWrap')
//                checkBoxMenuItem(text: "Whitespace", id: 'viewWhitespace')
//                checkBoxMenuItem(text: "Line Numbers", id: 'viewLineNumbers')
//                checkBoxMenuItem(text: "Tab Names", id: 'viewTabNames')
//                separator()
//                checkBoxMenuItem(text: "Status Bar", id: 'viewStatusBar')
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
//                menuItem(showTipsAction)
				separator()
                menuItem(aboutAction)
            }
        }
        
        rSyntaxScrollPane(id: 'sp') {
            sp.gutter.bookmarkingEnabled = true
            sp.gutter.bookmarkIcon = imageIcon('/bookmark.png')
            textEditorPane(marginLineEnabled: true, id: 'editor') {
				editor.with {
					addHyperlinkListener(new HyperlinkBrowser())
					caretUpdate = {
						def line = getLineOfOffset(caretPosition) + 1
						def column = caretPosition - getLineStartOffset(line - 1)
						def lineCount = lineCount
						def lineLength = getLineEndOffset(line - 1) - getLineStartOffset(line - 1)
						caretPositionStatus.text = "${line}:${column} (${lineCount}:${lineLength})"
					}
				}
            }
        }
		
		statusBar(constraints: BorderLayout.SOUTH) {
			label(text: 'Ready', constraints: new JXStatusBar.Constraint(FILL))
			label(text: '1:0', id: 'caretPositionStatus')
			label(text: 'text/plain', id: 'syntaxStatus')
		}
    }
}
