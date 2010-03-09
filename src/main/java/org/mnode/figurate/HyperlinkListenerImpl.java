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

import java.awt.Desktop;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.jdesktop.swingx.JXErrorPane;

/**
 * @author Ben
 *
 */
public class HyperlinkListenerImpl implements HyperlinkListener {

    /**
     * {@inheritDoc}
     */
    public void hyperlinkUpdate(HyperlinkEvent e) {
        try {
            Desktop.getDesktop().browse(e.getURL().toURI());
        } catch (Exception ex) {
            JXErrorPane.showDialog(ex);
        }
    }

}
