package org.mnode.figurate

import javax.swing.JPanel;

import org.fife.ui.rsyntaxtextarea.FileLocation;
import org.fife.ui.rsyntaxtextarea.parser.Parser;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.mnode.ousia.HyperlinkBrowser;
import org.mnode.ousia.OusiaBuilder;

import eu.medsea.mimeutil.MimeUtil;

class Editor extends JPanel {
	
	static int newEditors = 0
	
	RTextScrollPane sp
	
	OusiaBuilder ousia
			
	Editor(File file) {
	
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
			textArea.addParser parser
		}
		else {
			textArea.clearParsers()
		}
	}
}