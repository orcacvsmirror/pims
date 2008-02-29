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
package drugstock.batch;

import java.awt.Dimension;
import java.awt.Toolkit;

import drugstock.cl.PrinterInitDlg;
import drugstock.cmn.PropRead;

/**
 * プリンタ設定処理<BR>
 * <BR>
 * プリンタ名をプリンタ設定ファイルから読み込み、<BR>
 * 出力プリンタとして設定します。<BR>
 */

public class PrinterInit implements Runnable {

	PrinterInitDlg dlg = null;

	Thread thread;

	public PrinterInit() {
		dlg = new PrinterInitDlg();
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
		dlg.setModal(true);
		dlg.show();
		if (dlg.IsOK()) {
			thread = new Thread(this);
		}
	}

	public void start() {
		if (thread != null)
			thread.start();
	}

	public void stop() {
		thread = null;
	}

	public void run() {
		try {
			PropRead prop = new PropRead();
			prop.setPropPrinter("printer_info", dlg.getPrinter());
		} catch (Exception e) {
		}
	}
}
