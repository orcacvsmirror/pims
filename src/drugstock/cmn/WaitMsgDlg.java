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

import java.awt.Font;
import java.awt.Frame;
import java.awt.SystemColor;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;

import com.borland.jbcl.layout.XYConstraints;
import com.borland.jbcl.layout.XYLayout;

/**
 * メッセージ表示時のウェイト処理：表示部
 */

public class WaitMsgDlg extends JDialog {

	private JPanel panel1 = new JPanel();
	private XYLayout xYLayout1 = new XYLayout();
	private JTextPane msg1Pane = new JTextPane();
	private JTextPane msg2Pane = new JTextPane();

	private String msg1 = "";
	private String msg2 = "";
	private XYLayout xYLayout2 = new XYLayout();
	private XYLayout xYLayout3 = new XYLayout();
	private XYLayout xYLayout4 = new XYLayout();

	public WaitMsgDlg(Frame frame, String title, boolean modal) {
		super(frame, title, modal);
		try {
			jbInit();
			pack();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public WaitMsgDlg() {
		this(null, "", false);
	}

	private void jbInit() throws Exception {
		panel1.setLayout(xYLayout1);
		msg1Pane.setBackground(SystemColor.activeCaptionBorder);
		msg1Pane.setFont(new Font("Serif", 0, 16));
		msg1Pane.setEditable(false);
		msg2Pane.setBackground(SystemColor.activeCaptionBorder);
		msg2Pane.setFont(new Font("Serif", 0, 16));
		msg2Pane.setEditable(false);
		msg2Pane.setText("しばらく、お待ちください。");
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.setTitle("お待ちください。");
		this.getContentPane().setLayout(xYLayout4);
		xYLayout4.setWidth(389);
		xYLayout4.setHeight(67);
		getContentPane().add(panel1, new XYConstraints(0, 0, -1, 61));
		panel1.add(msg1Pane, new XYConstraints(0, 0, 375, -1));
		panel1.add(msg2Pane, new XYConstraints(0, 31, 375, -1));

		msg1Pane.setText(msg1);
		msg1Pane.setText("現在処理中...");
	}

	public void setMsg1(String msg) {
		msg1 = msg;
		msg1Pane.setText(msg1);
	}

	public void setMsg2(String msg) {
		msg2 = msg;
		msg2Pane.setText(msg2);
	}

	public void destroy() {
		dispose();
	}
}