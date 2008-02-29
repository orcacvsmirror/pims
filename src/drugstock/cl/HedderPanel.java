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

import java.awt.BorderLayout;
import java.awt.SystemColor;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

/**
 * ウィンドウヘッダ表示処理
 */

public class HedderPanel extends JPanel {

	private BorderLayout borderLayout1 = new BorderLayout();
	private JLabel jLabel1 = new JLabel();

	public HedderPanel() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void jbInit() throws Exception {
		jLabel1.setForeground(SystemColor.window);
		jLabel1.setText("薬剤在庫管理システム");
		this.setBackground(UIManager.getColor("Desktop.background"));
		this.setForeground(SystemColor.activeCaptionText);
		this.setLayout(borderLayout1);
		this.add(jLabel1, BorderLayout.NORTH);
	}
}