/*
 *******************************************************************
 * Project code name "ORCA"
 * 日医標準レセプトソフト（JMA standard receipt software）
 * Copyright(C) 2002 JMA (Japan Medical Association)
 *
 * This program is part of "JMA standard receipt software".
 *
 *     This program is distributed in the hope that it will be useful
 * for further advancement in medical care, according to JMA Open
 * Source License, but WITHOUT ANY WARRANTY.
 *     Everyone is granted permission to use, copy, modify and
 * redistribute this program, but only under the conditions described
 * in the JMA Open Source License. You should have received a copy of
 * this license along with this program. If not, stop using this
 * program and contact JMA, 2-28-16 Honkomagome, Bunkyo-ku, Tokyo,
 * 113-8621, Japan.
 ********************************************************************
 */
package drugstock.cmn;

import java.awt.AWTKeyStroke;
import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JDialog;

/**
 * フォーカス制御用クラス
 */

public class DefaultJDialog extends JDialog {

	public DefaultJDialog() {
		super();
	}

	public DefaultJDialog(Frame frame, String title, boolean modal) {
		super(frame, title, modal);

		this.setForwardKey();
		// this.setBackwardKey() ;
	}

	private void setForwardKey() {
		Set hashSet = new HashSet();
		hashSet.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ENTER, 0));
		hashSet.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_TAB, 0));
		this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
		        hashSet);
	}

	private void setBackwardKey() {
		Set hashSet = new HashSet();
		hashSet.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ENTER,
		        InputEvent.SHIFT_DOWN_MASK));
		hashSet.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_TAB,
		        InputEvent.SHIFT_DOWN_MASK));
		this.setFocusTraversalKeys(
		        KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, hashSet);
	}
}
