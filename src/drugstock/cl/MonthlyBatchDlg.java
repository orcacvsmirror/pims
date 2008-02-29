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
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;


import drugstock.biz.BizMonthlyBatch;
import drugstock.cmn.DefaultJDialog;
import drugstock.cmn.FocusTraversalPolicyOrder;
import drugstock.cmn.MsgDlg;
import drugstock.component.BButton;
import com.borland.jbcl.layout.XYConstraints;
import com.borland.jbcl.layout.XYLayout;
import drugstock.model.CodeName;
import drugstock.model.SyuruiCdNm;

/**
 * 「月次処理」画面処理
 */

public class MonthlyBatchDlg extends DefaultJDialog {

	private JPanel panel1 = new JPanel();
	private XYLayout xYLayout1 = new XYLayout();
	private XYLayout xYLayout2 = new XYLayout();
	private JTextPane titlePanel = new JTextPane();
	private JLabel jLabel1 = new JLabel();
	JTextField yyyyText = new JTextField();
	private JLabel jLabel2 = new JLabel();
	JTextField mmText = new JTextField();
	private JLabel jLabel3 = new JLabel();
	private BButton okButton = new BButton();
	private BButton cancelButton = new BButton();

	public CodeName contItem[] = null; // 業者
	public SyuruiCdNm syuruiCdNm[] = null; // 薬剤類コンボのコード、ネーム
	private boolean bOk = false;
	private int iContSel = 0; // 業者選択ポインタ
	private int iKindSel = 0; // 選択ポインタ

	/**
	 * パネルが開かれた時に最初にフォーカスを当てるコンポーネント
	 */
	protected Component firstFocusComponent = null;

	public MonthlyBatchDlg(Frame frame, String title, boolean modal) {
		super(frame, title, modal);
		try {
			jbInit();
			pack();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public MonthlyBatchDlg() {
		this(null, "月次処理【帳票出力準備】", false);
	}

	private void jbInit() throws Exception {
		panel1.setLayout(xYLayout2);
		this.getContentPane().setLayout(xYLayout1);
		titlePanel.setBackground(UIManager.getColor("PasswordField.selectionBackground"));
		titlePanel.setFont(new Font("Dialog", 0, 16));
		titlePanel.setForeground(Color.white);
		titlePanel.setEditable(false);
		titlePanel.setText("月次処理【帳票出力準備】");
		xYLayout1.setWidth(442);
		xYLayout1.setHeight(269);
		jLabel1.setFont(new Font("Dialog", 0, 16));
		jLabel1.setText("集計年月");
		jLabel2.setFont(new Font("Dialog", 0, 16));
		jLabel2.setText("年");
		jLabel3.setFont(new Font("Dialog", 0, 16));
		jLabel3.setText("月");
		okButton.setFont(new Font("Dialog", 0, 16));
		okButton.setText("実行");
		okButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				okButton_actionPerformed(e);
			}
		});
		mmText.setFont(new Font("Dialog", 0, 16));
		mmText.setToolTipText("");
		mmText.addFocusListener(new FocusAdapter() {

			public void focusGained(FocusEvent e) {
				mmText.selectAll();
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
		this.setTitle("月次処理【帳票出力準備】");
		yyyyText.setFont(new Font("Dialog", 0, 16));
		yyyyText.addFocusListener(new FocusAdapter() {

			public void focusGained(FocusEvent e) {
				yyyyText.selectAll();
			}
		});

		getContentPane().add(panel1, new XYConstraints(0, 0, -1, -1));
		this.getContentPane().add(jLabel1, new XYConstraints(36, 75, -1, -1));
		this.getContentPane().add(okButton, new XYConstraints(96, 205, 155, -1));
		this.getContentPane().add(cancelButton,
		        new XYConstraints(269, 204, -1, -1));
		this.getContentPane().add(yyyyText, new XYConstraints(106, 73, 44, -1));
		this.getContentPane().add(jLabel2, new XYConstraints(154, 76, -1, -1));
		this.getContentPane().add(mmText, new XYConstraints(170, 74, 35, -1));
		this.getContentPane().add(jLabel3, new XYConstraints(209, 77, -1, -1));
		this.getContentPane().add(titlePanel, new XYConstraints(0, 0, 443, 33));

		Component order[] = new Component[] { yyyyText, mmText, okButton,
		        cancelButton };
		FocusTraversalPolicyOrder policy = new FocusTraversalPolicyOrder(order);
		super.setFocusTraversalPolicy(policy);

		BizMonthlyBatch biz = new BizMonthlyBatch();
		Date nowDate = new Date();
		SimpleDateFormat fmt1 = new SimpleDateFormat("yyyy");
		yyyyText.setText(fmt1.format(nowDate));
		SimpleDateFormat fmt2 = new SimpleDateFormat("MM");
		mmText.setText(fmt2.format(nowDate));

		firstFocusComponent = yyyyText;

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
		Integer iyyyy = null;
		Integer imm = null;
		if (yyyyText.getText().equals("")) {
			MsgDlg msgdlg = new MsgDlg(this);
			msgdlg.msgdsp("年が入力されていません。", MsgDlg.ERROR_MESSAGE);
			yyyyText.requestFocus();
			return;
		}
		if (mmText.getText().equals("")) {
			MsgDlg msgdlg = new MsgDlg(this);
			msgdlg.msgdsp("月が入力されていません。", MsgDlg.ERROR_MESSAGE);
			mmText.requestFocus();
			return;
		}
		// 年月のチェック
		try {
			iyyyy = new Integer(yyyyText.getText());
		} catch (Exception err) {
			MsgDlg msgdlg = new MsgDlg(this);
			msgdlg.msgdsp("年は数値入力してください。", MsgDlg.ERROR_MESSAGE);
			yyyyText.requestFocus();
			return;
		}
		try {
			imm = new Integer(mmText.getText());
		} catch (Exception err) {
			MsgDlg msgdlg = new MsgDlg(this);
			msgdlg.msgdsp("月は数値入力してください。", MsgDlg.ERROR_MESSAGE);
			mmText.requestFocus();
			return;
		}
		int inen = iyyyy.intValue();
		int ituki = imm.intValue();

		if (1900 <= inen && inen <= 2100) {
		} else {
			MsgDlg msgdlg = new MsgDlg(this);
			msgdlg.msgdsp("年の値が不正です。", MsgDlg.ERROR_MESSAGE);
			yyyyText.requestFocus();
			return;
		}

		if (1 <= ituki && ituki <= 12) {
		} else {
			MsgDlg msgdlg = new MsgDlg(this);
			msgdlg.msgdsp("月の値が不正です。", MsgDlg.ERROR_MESSAGE);
			mmText.requestFocus();
			return;
		}

		if (ituki < 10)
			mmText.setText("0" + String.valueOf(ituki));

		// 在庫データの存在チェック
		BizMonthlyBatch biz = new BizMonthlyBatch();
		String sDate = null;
		// 前月のチェック
		ituki -= 1;
		if (ituki < 1) {
			inen -= 1;
			ituki = 12;
		}
		sDate = String.valueOf(inen);
		if (ituki < 10)
			sDate = sDate + "0" + String.valueOf(ituki);
		else
			sDate = sDate + String.valueOf(ituki);

		if (!biz.isData(sDate)) {
			MsgDlg msgdlg = new MsgDlg(this);
			if (0 == msgdlg.msgdsp(
			        "前月データがありません。\n集計を中断するなら「はい」、続行するなら「いいえ」を押してください。",
			        MsgDlg.ERROR_MESSAGE, MsgDlg.YES_NO_OPTION)) {
				return;
			}
		}

		// 当月データの存在チェック
		inen = iyyyy.intValue();
		ituki = imm.intValue();
		sDate = String.valueOf(inen);
		if (ituki < 10)
			sDate = sDate + "0" + String.valueOf(ituki);
		else
			sDate = sDate + String.valueOf(ituki);
		if (biz.isData(sDate)) {
			MsgDlg msgdlg = new MsgDlg(this);
			if (0 == msgdlg.msgdsp(
			        "指定した年月のデータはすでに存在します。\n集計を中断するなら「はい」、続行するなら「いいえ」を押してください。",
			        MsgDlg.ERROR_MESSAGE, MsgDlg.YES_NO_OPTION)) {
				return;
			}
		}

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
	 * 入力年を取得します。
	 */
	public String getYear() {
		String sRet = "";
		if (IsOK()) {
			return yyyyText.getText();
		}
		return sRet;
	}

	/**
	 * 入力月を取得します。
	 */
	public String getMonth() {
		String sRet = "";
		if (IsOK()) {
			return mmText.getText();
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
