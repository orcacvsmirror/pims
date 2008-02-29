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
import java.awt.Container;
import java.awt.KeyboardFocusManager;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 * メッセージ表示用汎用クラス
 */

public class MsgDlg {

	private String title;
	private Container msgContainer = null;

	public static final int INFORMATION_MESSAGE = JOptionPane.INFORMATION_MESSAGE;
	public static final int ERROR_MESSAGE = JOptionPane.ERROR_MESSAGE;
	public static final int WARNING_MESSAGE = JOptionPane.WARNING_MESSAGE;
	public static final int QUESTION_MESSAGE = JOptionPane.QUESTION_MESSAGE;
	public static final int DEFAULT_OPTION = JOptionPane.DEFAULT_OPTION;
	public static final int OK_CANCEL_OPTION = JOptionPane.OK_CANCEL_OPTION;
	public static final int YES_NO_OPTION = JOptionPane.YES_NO_OPTION;
	public static final int YES_NO_CANCEL_OPTION = JOptionPane.YES_NO_CANCEL_OPTION;

	/* コンストラクタ */
	public MsgDlg(JDialog parentdlg) {
		title = parentdlg.getTitle();
		msgContainer = parentdlg.getContentPane();
		this.setForwardKey(parentdlg);
		this.setBackwardKey(parentdlg);
	}

	public MsgDlg() {
		title = "";
		msgContainer = new Container();
	}

	/**
	 * メッセージダイアログを表示します。
	 * 
	 * @param smsg
	 *            メッセージダイアログの説明文
	 * @param msgtype
	 *            JOptionPaneの定数フィールド値(メッセージ)
	 */
	public void msgdsp(String smsg, int msgtype) {
		if (msgtype != INFORMATION_MESSAGE && msgtype != ERROR_MESSAGE
		        && msgtype != WARNING_MESSAGE && msgtype != QUESTION_MESSAGE) {
			msgtype = INFORMATION_MESSAGE;
		}
		JOptionPane msgOptPane = new JOptionPane(smsg, msgtype);
		JDialog msgDialog = msgOptPane.createDialog(msgContainer, title);
		msgDialog.setVisible(true);
	}

	/**
	 * メッセージダイアログを表示します。
	 * 
	 * @param smsg
	 *            メッセージダイアログの説明文
	 * @param msgtype
	 *            JOptionPaneの定数フィールド値(メッセージ)
	 * @param msgoption
	 *            JOptionPaneの定数フィールド値(オプション)
	 */
	public int msgdsp(String smsg, int msgtype, int msgoption) {
		if (msgtype != OK_CANCEL_OPTION && msgtype != ERROR_MESSAGE
		        && msgtype != WARNING_MESSAGE && msgtype != QUESTION_MESSAGE) {
			msgtype = INFORMATION_MESSAGE;
		}
		if (msgoption != INFORMATION_MESSAGE && msgoption != YES_NO_OPTION
		        && msgoption != YES_NO_CANCEL_OPTION) {
			msgoption = DEFAULT_OPTION;
		}
		JOptionPane msgOptPane = new JOptionPane(smsg, msgtype, msgoption);
		JDialog msgDialog = msgOptPane.createDialog(msgContainer, title);
		msgDialog.setVisible(true);
		Object selectedValue = msgOptPane.getValue();
		if (selectedValue == null) {
			return -1;
		} else {
			String sel = selectedValue.toString();
			return Integer.parseInt(sel);
		}
	}

	private void setForwardKey(JDialog dlg) {
		Set hashSet = new HashSet();
		hashSet.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ENTER, 0));
		hashSet.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_TAB, 0));
		dlg.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
		        hashSet);
	}

	private void setBackwardKey(JDialog dlg) {
		Set hashSet = new HashSet();
		hashSet.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ENTER,
		        InputEvent.SHIFT_DOWN_MASK));
		hashSet.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_TAB,
		        InputEvent.SHIFT_DOWN_MASK));
		dlg.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
		        hashSet);
	}

}