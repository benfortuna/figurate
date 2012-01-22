/**
 * This file is part of Figurate.
 *
 * Copyright (c) 2012, Ben Fortuna [fortuna@micronode.com]
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

import javax.swing.JPanel

import org.fife.ui.rsyntaxtextarea.FileLocation
import org.fife.ui.rsyntaxtextarea.parser.Parser
import org.fife.ui.rtextarea.RTextScrollPane
import org.mnode.ousia.HyperlinkBrowser
import org.mnode.ousia.OusiaBuilder

import eu.medsea.mimeutil.MimeUtil

class Editor extends JPanel {
	
	static int newEditors = 0
	
	RTextScrollPane sp
	
	OusiaBuilder ousia
			
	Editor(def file) {
	
		ousia = new OusiaBuilder()
		layout = ousia.borderLayout()
		sp = ousia.rSyntaxScrollPane {
			textArea = textEditorPane(marginLineEnabled: true)
			textArea.with {
				addHyperlinkListener(new HyperlinkBrowser())
				
				if (file) {
//					editable = !readOnly
	                load(FileLocation.create(file), null)
	                syntaxEditingStyle = MimeUtil.getMimeTypes(file).iterator().next()
	                caretPosition = 0
				}

				// reset undo history..
				discardAllEdits()
			}
		}
		
		sp.with {
			gutter.bookmarkIcon = ousia.imageIcon('/bookmark.png')
			gutter.bookmarkingEnabled = true
		}

		add sp
	
		if (file) {
			putClientProperty 'figurate.id', sp.textArea.fileName
		}
		else {
			putClientProperty 'figurate.id', "Untitled ${++newEditors}"
		}
	}
	
	void setSpellingParser(Parser parser) {
		if (parser) {
			sp.textArea.addParser parser
		}
		else {
			sp.textArea.clearParsers()
		}
	}
}