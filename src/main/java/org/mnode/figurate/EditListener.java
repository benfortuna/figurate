/**
 * This file is part of Base Modules.
 *
 * Copyright (c) 2010, Ben Fortuna [fortuna@micronode.com]
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

package org.mnode.figurate;

import javax.swing.JComponent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import org.jvnet.substance.SubstanceLookAndFeel;

/**
 * @author Ben
 *
 */
public class EditListener extends UndoManager implements DocumentListener {

    private static final long serialVersionUID = 1L;
    
    private final JComponent component;
    
    public EditListener(JComponent component) {
        this.component = component;
    }
    
    @Override
    public void undoableEditHappened(UndoableEditEvent e) {
        component.putClientProperty(SubstanceLookAndFeel.WINDOW_MODIFIED, true);
        super.undoableEditHappened(e);
    }
    
    /**
     * {@inheritDoc}
     */
    public void changedUpdate(DocumentEvent e) {
        if (canUndoAny()) {
            component.putClientProperty(SubstanceLookAndFeel.WINDOW_MODIFIED, true);
        }
        else {
            component.putClientProperty(SubstanceLookAndFeel.WINDOW_MODIFIED, false);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void insertUpdate(DocumentEvent e) {
        if (canUndoAny()) {
            component.putClientProperty(SubstanceLookAndFeel.WINDOW_MODIFIED, true);
        }
        else {
            component.putClientProperty(SubstanceLookAndFeel.WINDOW_MODIFIED, false);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeUpdate(DocumentEvent e) {
        if (canUndoAny()) {
            component.putClientProperty(SubstanceLookAndFeel.WINDOW_MODIFIED, true);
        }
        else {
            component.putClientProperty(SubstanceLookAndFeel.WINDOW_MODIFIED, false);
        }
    }
    
    private boolean canUndoAny() {
        for (UndoableEdit edit : edits) {
            if (edit.canUndo()) {
                return true;
            }
        }
        return false;
    }

}
