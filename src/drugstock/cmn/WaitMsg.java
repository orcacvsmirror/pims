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

/**
 * ウェイトメッセージ表示時の処理
 */
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JDialog;

public class WaitMsg {

	private String msg1 = "";
	private String msg2 = "";
	private WaitMsgDlg dlg = null;

	/* コンストラクタ */
	public WaitMsg(JDialog parentdlg) {
		dlg = new WaitMsgDlg();
	}

	public WaitMsg() {
		dlg = new WaitMsgDlg();
	}

	/**
	 * ウェイトのあるメッセージダイアログを表示します。 計算待ち、帳票印刷待ちに使用します。
	 */
	public void msgdsp() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = dlg.getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		dlg.setLocation((screenSize.width - frameSize.width) / 2,
		        (screenSize.height - frameSize.height) / 2);
		dlg.setModal(false);
		dlg.setMsg1(msg1);
		dlg.setMsg2(msg2);
		dlg.show();
		this.sleep(500);
	}

	/**
	 * ウェイトダイアログの一行目を設定します。
	 * 
	 * @param 一行目の表示文字列
	 */
	public void setMsg1(String msg) {
		this.msg1 = msg;
	}

	/**
	 * ウェイトダイアログの二行目を設定します。
	 * 
	 * @param 二行目の表示文字列
	 */
	public void setMsg2(String msg) {
		this.msg2 = msg;
	}

	/**
	 * ウェイトダイアログを返します。
	 */
	public WaitMsgDlg getDlg() {
		return dlg;
	}

	/**
	 * ウェイトダイアログを閉じます。
	 * 
	 * @param 二行目の表示文字列
	 */
	public void destroy() {
		dlg.destroy();
	}

	/**
	 * 指定時間ウェイトします。
	 * 
	 * @param msec
	 *            指定時間(単位：msec)
	 */
	private synchronized void sleep(long msec) {
		try {
			wait(msec);
		} catch (InterruptedException e) {
		}
	}

}