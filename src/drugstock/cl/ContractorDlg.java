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
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;


import drugstock.biz.BizContractor;
import drugstock.cmn.DefaultJDialog;
import drugstock.cmn.MsgDlg;
import drugstock.component.BButton;
import com.borland.jbcl.layout.XYConstraints;
import com.borland.jbcl.layout.XYLayout;
import drugstock.model.CodeName;

/**
 * 「業者設定」画面処理
 */

public class ContractorDlg extends DefaultJDialog {

	private XYLayout xYLayout2 = new XYLayout();

	private JLabel codePanel1 = new JLabel();

	private JTextField nameText = new JTextField(); // 名称テキスト

	private JLabel ryakuPanel = new JLabel();

	private JLabel codePanel3 = new JLabel();

	private JTextField codeText = new JTextField(); // コードテキスト

	private JTextField ryakuText = new JTextField(); // 略称テキスト

	private JLabel nebikiPanel = new JLabel();

	private JTextField nebikiText = new JTextField(); // 値引率テキスト

	private JLabel nebikiPanel1 = new JLabel();

	private JRadioButton taxRadio1 = new JRadioButton(); // 消費税区分 あり

	private JRadioButton taxRadio2 = new JRadioButton(); // 消費税区分 なし

	private ButtonGroup taxGrp = new ButtonGroup(); // 消費税区分グループ

	private BButton okButton = new BButton();

	private BButton cancelButton = new BButton();

	private JLabel jLabel1 = new JLabel();

	private TitledBorder titledBorder1;

	private JRadioButton modeRadio1 = new JRadioButton(); // 処理区分 新規

	private JRadioButton modeRadio2 = new JRadioButton(); // 処理区分 修正

	private JRadioButton modeRadio3 = new JRadioButton(); // 処理区分 削除

	private ButtonGroup modeGrp = new ButtonGroup(); // 処理区分ｸﾞﾙ-ﾌﾟ

	private JComboBox codeCombo = new JComboBox(); // データ選択

	private int imode; // 0:新規 1:修正 2:削除

	private boolean codeComb_busy = false; // データ選択コンボ アイテム設定中

	CodeName cItem[] = null;

	private JLabel nebikiPanel2 = new JLabel();// データ選択コンボの一覧

	public ContractorDlg(Frame frame, String title, boolean modal) {
		super(frame, title, modal);
		try {
			this.setTitle(title);
			jbInit();
			pack();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public ContractorDlg() {
		this(null, "業者登録", false);
	}

	private void jbInit() throws Exception {

		taxRadio1.setFont(new Font("Dialog", 0, 16));
		ryakuText.setFont(new Font("Dialog", 0, 16));
		codeText.setFont(new Font("Dialog", 0, 16));
		nameText.setFont(new Font("Dialog", 0, 16));
		nebikiText.setFont(new Font("Dialog", 0, 16));

		titledBorder1 = new TitledBorder(BorderFactory.createEtchedBorder(
		        Color.white, new Color(148, 145, 140)), "処理区分");

		modeRadio1.setFont(new Font("Dialog", 0, 16));
		modeRadio1.setText("新規");
		modeRadio1.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				modeRadio1_actionPerformed(e);
			}
		});
		modeRadio2.setFont(new Font("Dialog", 0, 16));
		modeRadio2.setText("修正");
		modeRadio2.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				modeRadio2_actionPerformed(e);
			}
		});
		modeRadio3.setFont(new Font("Dialog", 0, 16));
		modeRadio3.setText("削除");
		modeRadio3.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				modeRadio3_actionPerformed(e);
			}
		});
		nebikiPanel2.setFont(new Font("Dialog", 0, 16));
		nebikiPanel2.setText("%");
		codePanel1.setFont(new Font("Dialog", 0, 16));
		codePanel3.setFont(new Font("Dialog", 0, 16));
		ryakuPanel.setFont(new Font("Dialog", 0, 16));
		nebikiPanel.setFont(new Font("Dialog", 0, 16));
		nebikiPanel1.setFont(new Font("Dialog", 0, 16));
		taxRadio2.setFont(new Font("Dialog", 0, 16));
		codeCombo.setFont(new Font("Dialog", 0, 16));
		codeCombo.setAutoscrolls(true);
		jLabel1.setFont(new Font("Dialog", 0, 16));
		okButton.setFont(new Font("Dialog", 0, 16));
		okButton.setToolTipText("");
		okButton.setActionCommand("確定");
		cancelButton.setFont(new Font("Dialog", 0, 16));
		cancelButton.setActionCommand("戻る");
		modeGrp.add(modeRadio1);
		modeGrp.add(modeRadio2);
		modeGrp.add(modeRadio3);

		codePanel3.setText("業者コード");
		codeText.setText("jTextField1");

		ryakuText.setText("jTextField1");
		nebikiPanel.setText("値引率");
		nebikiText.setText("");
		nebikiText.setHorizontalAlignment(SwingConstants.RIGHT);
		nebikiPanel1.setText("消費税区分");
		taxRadio1.setText("あり");
		taxRadio1.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				taxRadio1_actionPerformed(e);
			}
		});
		taxRadio2.setText("なし");
		taxRadio2.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				taxRadio2_actionPerformed(e);
			}
		});
		taxGrp.add(taxRadio1);
		taxGrp.add(taxRadio2);

		okButton.setText("確定");
		okButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				okButton_actionPerformed();
			}
		});
		cancelButton.setText("戻る");
		cancelButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				cancelButton_actionPerformed();
			}
		});
		this.getContentPane().setLayout(xYLayout2);
		codePanel1.setText("業者名称");
		nameText.setText("jTextField1");
		ryakuPanel.setText("業者略称");
		jLabel1.setBorder(titledBorder1);
		jLabel1.setToolTipText("");
		xYLayout2.setWidth(411);
		xYLayout2.setHeight(292);
		codeCombo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				codeCombo_actionPerformed(e);
			}
		});
		codeText.setDisabledTextColor(Color.black);
		nameText.setDisabledTextColor(Color.black);
		ryakuText.setDisabledTextColor(Color.black);
		nebikiText.setDisabledTextColor(Color.black);

		this.getContentPane().add(modeRadio1, new XYConstraints(20, 32, 70, 24));
		this.getContentPane().add(modeRadio2, new XYConstraints(89, 32, 70, 24));
		this.getContentPane().add(modeRadio3,
		        new XYConstraints(157, 32, 70, 24));
		this.getContentPane().add(codeCombo,
		        new XYConstraints(231, 30, 161, -1));
		this.getContentPane().add(jLabel1, new XYConstraints(10, 14, 392, 52));

		this.getContentPane().add(codePanel3, new XYConstraints(23, 83, -1, -1));
		this.getContentPane().add(codePanel1,
		        new XYConstraints(23, 112, -1, -1));
		this.getContentPane().add(ryakuPanel,
		        new XYConstraints(23, 142, -1, -1));
		this.getContentPane().add(nebikiPanel,
		        new XYConstraints(23, 172, -1, -1));
		this.getContentPane().add(nebikiPanel1,
		        new XYConstraints(23, 202, -1, -1));

		this.getContentPane().add(codeText, new XYConstraints(116, 81, 105, 25));
		this.getContentPane().add(nameText,
		        new XYConstraints(116, 110, 241, 25));
		this.getContentPane().add(ryakuText,
		        new XYConstraints(116, 139, 217, 25));
		this.getContentPane().add(nebikiText,
		        new XYConstraints(116, 167, 78, 25));
		this.getContentPane().add(taxRadio1,
		        new XYConstraints(125, 202, 69, 24));
		this.getContentPane().add(taxRadio2,
		        new XYConstraints(196, 202, 69, 24));
		this.getContentPane().add(nebikiPanel2,
		        new XYConstraints(197, 171, -1, -1));

		this.getContentPane().add(okButton, new XYConstraints(37, 242, 107, 31));
		this.getContentPane().add(cancelButton,
		        new XYConstraints(260, 242, 107, 31));

		// 初期値表示
		modeRadio1.setSelected(true);
		inputItem_clear();
		imode = 99;
		modeRadio1_actionPerformed(null);
	}

	/**
	 * ウィンドウが開かれたときのイベントをオーバーライドします。
	 */
	protected void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);

		if (e.getID() == WindowEvent.WINDOW_OPENED) {
			codeText.requestFocus();
		}
	}

	// 処理モード（新規）
	void modeRadio1_actionPerformed(ActionEvent e) {
		if (imode == 0)
			return;
		inputItem_clear();
		codeText.setText(""); // コードテキスト
		codeCombo.setEnabled(false);
		codeText.setEnabled(true);
		nameText.setEnabled(true);
		ryakuText.setEnabled(true);
		nebikiText.setEnabled(true);
		taxRadio1.setEnabled(true);
		taxRadio2.setEnabled(true);
		codeCombo.setBackground(Color.lightGray);
		codeText.setBackground(Color.white);
		nameText.setBackground(Color.white);
		ryakuText.setBackground(Color.white);
		nebikiText.setBackground(Color.white);
		codeText.requestFocus();
		imode = 0;
	}

	// 処理モード（修正）
	void modeRadio2_actionPerformed(ActionEvent e) {
		if (imode == 1)
			return;
		inputItem_clear();
		codeCombo.setEnabled(true);
		codeText.setEnabled(false);
		nameText.setEnabled(true);
		ryakuText.setEnabled(true);
		nebikiText.setEnabled(true);
		taxRadio1.setEnabled(true);
		taxRadio2.setEnabled(true);
		codeCombo.setBackground(Color.white);
		codeText.setBackground(Color.lightGray);
		nameText.setBackground(Color.white);
		ryakuText.setBackground(Color.white);
		nebikiText.setBackground(Color.white);
		imode = 1;
		codeCombo_Itemset();
		codeCombo.requestFocus();
	}

	// 処理モード（削除）
	void modeRadio3_actionPerformed(ActionEvent e) {
		if (imode == 2)
			return;
		inputItem_clear();
		codeCombo.setEnabled(true);
		codeText.setEnabled(false);
		nameText.setEnabled(false);
		ryakuText.setEnabled(false);
		nebikiText.setEnabled(false);
		taxRadio1.setEnabled(false);
		taxRadio2.setEnabled(false);
		codeCombo.setBackground(Color.white);
		codeText.setBackground(Color.lightGray);
		nameText.setBackground(Color.lightGray);
		ryakuText.setBackground(Color.lightGray);
		nebikiText.setBackground(Color.lightGray);
		imode = 2;
		codeCombo_Itemset();
		codeCombo.requestFocus();
	}

	// 消費税あり
	void taxRadio1_actionPerformed(ActionEvent e) {
	}

	// 消費税なし
	void taxRadio2_actionPerformed(ActionEvent e) {
	}

	// 確定ボタン
	void okButton_actionPerformed() {
		MsgDlg msgdlg = new MsgDlg(this);

		String cd = codeText.getText();
		String nm = nameText.getText();
		String snm = ryakuText.getText();
		String nbk = nebikiText.getText();
		String tx;
		if (taxRadio1.isSelected() == true)
			tx = "1";
		else
			tx = "0";
		// 業者コードチェック
		if (cd.equals("") == true) {
			msgdlg.msgdsp("業者コードを指定してください。", MsgDlg.ERROR_MESSAGE);
			codeText.requestFocus();
			return;
		}
		// try{
		// long l = Long.parseLong(cd);
		// cd = String.valueOf(l);
		// codeText.setText(cd);
		// }
		// catch(NumberFormatException er){
		// msgdlg.msgdsp("業者コードに整数値を入力してください。",msgDlg.ERROR_MESSAGE);
		// codeText.requestFocus();
		// return;
		// }
		if (cd.length() > 10) {
			msgdlg.msgdsp("業者コードは１０桁以内を入力してください。", MsgDlg.ERROR_MESSAGE);
			codeText.requestFocus();
			return;
		}
		// 業者名チェック
		if (nm.equals("") == true) {
			msgdlg.msgdsp("業者名を入力してください。", MsgDlg.ERROR_MESSAGE);
			nameText.requestFocus();
			return;
		}
		if (nm.length() > 50) {
			msgdlg.msgdsp("業者名は５０文字以内を入力してください。", MsgDlg.ERROR_MESSAGE);
			nameText.requestFocus();
			return;
		}
		// 略称業者名チェック
		if (snm.equals("") == true) {
			msgdlg.msgdsp("略称業者名を入力してください。", MsgDlg.ERROR_MESSAGE);
			ryakuText.requestFocus();
			return;
		}
		if (snm.length() > 50) {
			msgdlg.msgdsp("略称業者名は５０文字以内を入力してください。", MsgDlg.ERROR_MESSAGE);
			ryakuText.requestFocus();
			return;
		}
		// 値引率チェック
		try {
			int i = Integer.parseInt(nbk);
			nbk = String.valueOf(i);
			if (i < 0 || i > 100) {
				msgdlg.msgdsp("値引率は０〜１００を入力してください。", MsgDlg.ERROR_MESSAGE);
				nebikiText.requestFocus();
				return;
			}
		} catch (NumberFormatException er) {
			msgdlg.msgdsp("値引率は整数値を入力してください。", MsgDlg.ERROR_MESSAGE);
			nebikiText.requestFocus();
			return;
		}

		String status = "";
		if (imode == 0) {
			// 新規登録
			CodeName item = new CodeName("0", cd, nm, snm, nbk, tx);
			BizContractor biz = new BizContractor();
			status = biz.insContractor(item);
			if (status == "FOUND") {
				msgdlg.msgdsp("この業者コードは登録済みです。", MsgDlg.ERROR_MESSAGE);
			}
			if (status == "OK") {
				inputItem_clear();
			}
		} else if (imode == 1) {
			// 修正登録
			CodeName item = new CodeName("0", cd, nm, snm, nbk, tx);
			BizContractor biz = new BizContractor();
			status = biz.updtContractor(item);
			if (status == "OK") {
				inputItem_clear();
			}
		} else {
			// 削除登録
			// 削除確認
			int msgsts = msgdlg.msgdsp("削除してよろしいですか？", MsgDlg.QUESTION_MESSAGE,
			        MsgDlg.YES_NO_OPTION);
			if (msgsts == 1)
				return;
			BizContractor biz = new BizContractor();
			status = biz.delContractor(cd);
			if (status == "OK") {
				inputItem_clear();
			}
		}
		if (status == "OK") {
			codeCombo_Itemset(); // 業者選択コンボボックスに業者名を設定
		}

	}

	// 戻るボタン
	void cancelButton_actionPerformed() {
		dispose();
	}

	// 業者選択コンボ
	void codeCombo_actionPerformed(ActionEvent e) {
		if (codeComb_busy == true)
			return;
		int i = codeCombo.getSelectedIndex();
		if (i >= 0) {
			BizContractor biz = new BizContractor();
			CodeName item = biz.getContractor(cItem[i].getCode());
			codeText.setText(item.getCode()); // コードテキスト
			nameText.setText(item.getName()); // 名称テキスト
			ryakuText.setText(item.getNamekn()); // カナ名称テキスト
			nebikiText.setText(item.getNebikiritu()); // 値引率テキスト
			if (item.getZeikbn().equals("0"))
				taxRadio2.setSelected(true);
			else
				taxRadio1.setSelected(true);
		}
	}

	// 業者選択コンボボックスに業者名を設定
	private void codeCombo_Itemset() {
		codeComb_busy = true; // コンボに設定中
		codeCombo.removeAllItems();
		// 修正・削除用のコンボに値をセット
		// CodeName item[] = null;
		BizContractor biz = new BizContractor();
		cItem = biz.getCodeName();
		if (cItem != null) {
			for (int i = 0; i < cItem.length; i++) {
				codeCombo.addItem(cItem[i].getName());
			}
		}
		codeComb_busy = false;
	}

	// 入力項目クリア
	private void inputItem_clear() {
		codeText.setText(""); // コードテキスト
		nameText.setText(""); // 名称テキスト
		ryakuText.setText(""); // 略称テキスト
		nebikiText.setText("0"); // 値引率テキスト
		taxRadio1.setSelected(true); // 消費税区分 あり
	}
}
