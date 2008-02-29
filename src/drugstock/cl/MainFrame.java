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
package drugstock.cl;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * メインフレーム表示
 */

public class MainFrame extends JFrame {

	private JPanel contentPane;
	private DrugStockPanel mainPanel = null;
	private HedderPanel headPanel = null;

	private BorderLayout borderLayout1 = new BorderLayout();

	/**
	 * フレームを生成します。
	 */
	public MainFrame() {
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// コンポーネントの初期化
	private void jbInit() throws Exception {
		// setIconImage(Toolkit.getDefaultToolkit().createImage(MainFrame.class.getResource("[アイコン]")));
		contentPane = (JPanel)this.getContentPane();
		contentPane.setLayout(borderLayout1);

		// メインパネルの構築
		mainPanel = new DrugStockPanel(this);
		headPanel = new HedderPanel();

		contentPane.add(mainPanel, BorderLayout.CENTER);
		contentPane.add(headPanel, BorderLayout.NORTH);
		this.setSize(new Dimension(783, 530));

		this.setTitle("薬剤在庫管理システム ver.03.16【メニュー】");
	}

	/**
	 * ウィンドウが開かれたときのイベントをオーバーライドします。
	 */
	protected void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			System.exit(0);
		}
	}
}
