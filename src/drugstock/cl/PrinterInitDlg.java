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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;


import drugstock.cmn.DefaultJDialog;
import drugstock.cmn.FocusTraversalPolicyOrder;
import drugstock.cmn.PropRead;
import drugstock.cmn.MsgDlg;
import drugstock.component.BButton;
import com.borland.jbcl.layout.XYConstraints;
import com.borland.jbcl.layout.XYLayout;

/**
 * 「プリンタ設定」画面処理
 */

public class PrinterInitDlg extends DefaultJDialog {

	private JPanel panel1 = new JPanel();
	private XYLayout xYLayout1 = new XYLayout();
	private XYLayout xYLayout2 = new XYLayout();
	private JLabel jLabel1 = new JLabel();
	private JTextPane titlePanel = new JTextPane();
	JTextField printText = new JTextField();
	private JLabel jLabel3 = new JLabel();
	private BButton okButton = new BButton();
	private BButton cancelButton = new BButton();

	private boolean bOk = false;

	/**
	 * パネルが開かれた時に最初にフォーカスを当てるコンポーネント
	 */
	protected Component firstFocusComponent = null;

	public PrinterInitDlg(Frame frame, String title, boolean modal) {
		super(frame, title, modal);
		try {
			jbInit();
			pack();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public PrinterInitDlg() {
		this(null, "プリンタ設定", false);
	}

	private void jbInit() throws Exception {
		panel1.setLayout(xYLayout2);
		this.getContentPane().setLayout(xYLayout1);
		titlePanel.setBackground(UIManager.getColor("PasswordField.selectionBackground"));
		titlePanel.setFont(new Font("Dialog", 0, 16));
		titlePanel.setForeground(Color.white);
		titlePanel.setEditable(false);
		titlePanel.setText("プリンタ設定");
		xYLayout1.setWidth(442);
		xYLayout1.setHeight(200);
		jLabel1.setFont(new Font("Dialog", 0, 16));
		jLabel1.setText("プリンタ名");
		okButton.setFont(new Font("Dialog", 0, 16));
		okButton.setText("設定");
		okButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				okButton_actionPerformed(e);
			}
		});

		cancelButton.setFont(new Font("Dialog", 0, 16));
		cancelButton.setText("取消");
		cancelButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				cancelButton_actionPerformed(e);
			}
		});
		panel1.setFont(new Font("Dialog", 0, 14));
		this.setFont(new Font("Dialog", 0, 14));
		this.setTitle("プリンタ設定");
		printText.setFont(new Font("Dialog", 0, 16));
		printText.addFocusListener(new FocusAdapter() {

			public void focusGained(FocusEvent e) {
				printText.selectAll();
			}
		});

		getContentPane().add(panel1, new XYConstraints(0, 0, -1, -1));
		this.getContentPane().add(okButton, new XYConstraints(96, 155, 155, -1));
		this.getContentPane().add(cancelButton,
		        new XYConstraints(269, 154, -1, -1));
		this.getContentPane().add(printText,
		        new XYConstraints(176, 73, 150, -1));
		this.getContentPane().add(titlePanel, new XYConstraints(0, 0, 443, 33));
		this.getContentPane().add(jLabel1, new XYConstraints(57, 75, -1, -1));

		Component order[] = new Component[] { printText, okButton, cancelButton };
		FocusTraversalPolicyOrder policy = new FocusTraversalPolicyOrder(order);
		super.setFocusTraversalPolicy(policy);

		PropRead prop = new PropRead();
		String printer_info = prop.getPropPrinter("printer_info");
		// 現在設定されているプリンタ名を初期表示
		printText.setText(printer_info);

	}

	/**
	 * タイトルを設定します。
	 */
	public void setPaneTitle(String title) {
		titlePanel.setText(title);
	}

	/**
	 * 実行ボタン
	 */
	void okButton_actionPerformed(ActionEvent e) {
		MsgDlg msgdlg = new MsgDlg(this);
		if (printText.getText().equals("")) {
			msgdlg.msgdsp("プリンタ名が入力されていません。", MsgDlg.ERROR_MESSAGE);
			printText.requestFocus();
			return;
		}
		// 設定確認
		int msgsts = msgdlg.msgdsp("プリンタを設定します。よろしいですか？",
		        MsgDlg.QUESTION_MESSAGE, MsgDlg.YES_NO_OPTION);
		if (msgsts == 1)
			return;

		bOk = true;
		dispose();
	}

	/**
	 * 取消しボタン
	 */
	void cancelButton_actionPerformed(ActionEvent e) {
		bOk = false;
		dispose();
	}

	/**
	 * 決定OR取消しの取得
	 * 
	 * @return 決定ならtrue、取消ならfalseを返します。
	 */
	public boolean IsOK() {
		return bOk;
	}

	/**
	 * プリンタ名を取得します。
	 */
	public String getPrinter() {
		String sRet = "";
		if (IsOK()) {
			return printText.getText();
		}
		return sRet;
	}

	public void paint(Graphics g) {
		if (firstFocusComponent != null) {
			firstFocusComponent.requestFocus();
			firstFocusComponent = null;
		}
		super.paint(g);
	}
}