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
import javax.swing.border.TitledBorder;


import drugstock.biz.BizContradrug;
import drugstock.biz.BizDrugSyurui;
import drugstock.cmn.DefaultJDialog;
import drugstock.cmn.MsgDlg;
import drugstock.component.BButton;
import com.borland.jbcl.layout.XYConstraints;
import com.borland.jbcl.layout.XYLayout;
import drugstock.model.SyuruiCdNm;

/**
 * 「薬剤区分設定」画面処理
 */

public class DrugSyuruiDlg extends DefaultJDialog {

	private XYLayout xYLayout2 = new XYLayout();
	private JLabel codePanel3 = new JLabel();
	private JTextField codeText = new JTextField(); // ソフト内コード
	private JLabel orcaNoPanel = new JLabel();
	private JTextField orcaNoText = new JTextField(); // 日レセ種類コード
	private JLabel codePanel1 = new JLabel();
	private JTextField nameText = new JTextField(); // 名称テキスト

	private BButton okButton = new BButton();
	private BButton cancelButton = new BButton();
	private JLabel jLabel1 = new JLabel();
	private TitledBorder titledBorder1;
	private JRadioButton modeRadio1 = new JRadioButton(); // 処理 新規
	private JRadioButton modeRadio2 = new JRadioButton(); // 処理 修正
	private JRadioButton modeRadio3 = new JRadioButton(); // 処理 削除
	private ButtonGroup modeGrp = new ButtonGroup(); // 処理グループ
	private JComboBox codeCombo = new JComboBox(); // データ選択

	private int imode; // 0:新規 1:修正 2:削除
	private boolean codeComb_busy = false; // データ選択コンボ アイテム設定中

	SyuruiCdNm cItem[] = null;

	public DrugSyuruiDlg(Frame frame, String title, boolean modal) {
		super(frame, title, modal);
		try {
			this.setTitle(title);
			jbInit();
			pack();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public DrugSyuruiDlg() {
		this(null, "薬剤区分登録", false);
	}

	private void jbInit() throws Exception {

		orcaNoText.setFont(new Font("Dialog", 0, 16));
		codeText.setFont(new Font("Dialog", 0, 16));
		nameText.setFont(new Font("Dialog", 0, 16));

		titledBorder1 = new TitledBorder(BorderFactory.createEtchedBorder(
		        Color.white, new Color(148, 145, 140)), "処理");

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
		codePanel1.setFont(new Font("Dialog", 0, 16));
		codePanel3.setFont(new Font("Dialog", 0, 16));
		orcaNoPanel.setFont(new Font("Dialog", 0, 16));
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

		codePanel3.setText("薬剤コード");
		codeText.setText("jTextField1");

		orcaNoText.setText("jTextField1");

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
		codePanel1.setText("区分名称");
		nameText.setText("jTextField1");
		orcaNoPanel.setText("日レセコード");
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
		orcaNoText.setDisabledTextColor(Color.black);

		this.getContentPane().add(modeRadio1, new XYConstraints(20, 32, 70, 24));
		this.getContentPane().add(modeRadio2, new XYConstraints(89, 32, 70, 24));
		this.getContentPane().add(modeRadio3,
		        new XYConstraints(157, 32, 70, 24));
		this.getContentPane().add(codeCombo,
		        new XYConstraints(231, 30, 161, -1));
		this.getContentPane().add(jLabel1, new XYConstraints(10, 14, 392, 52));

		this.getContentPane().add(codePanel3, new XYConstraints(23, 83, -1, -1));
		this.getContentPane().add(orcaNoPanel,
		        new XYConstraints(23, 112, -1, -1));
		this.getContentPane().add(codePanel1,
		        new XYConstraints(23, 142, -1, -1));

		this.getContentPane().add(codeText, new XYConstraints(140, 81, 105, 25));
		this.getContentPane().add(orcaNoText,
		        new XYConstraints(140, 110, 105, 25));
		this.getContentPane().add(nameText,
		        new XYConstraints(140, 139, 241, 25));

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
		codeCombo.setEnabled(false);
		codeText.setEnabled(true);
		nameText.setEnabled(true);
		orcaNoText.setEnabled(true);
		codeCombo.setBackground(Color.lightGray);
		codeText.setBackground(Color.white);
		nameText.setBackground(Color.white);
		orcaNoText.setBackground(Color.white);
		codeText.requestFocus();
		imode = 0;
	}

	// 処理モード（修正）
	void modeRadio2_actionPerformed(ActionEvent e) {
		if (imode == 1)
			return;
		inputItem_clear();
		codeCombo.setEnabled(true);
		codeText.setEnabled(true);
		nameText.setEnabled(true);
		orcaNoText.setEnabled(true);
		codeCombo.setBackground(Color.white);
		codeText.setBackground(Color.white);
		nameText.setBackground(Color.white);
		orcaNoText.setBackground(Color.white);
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
		orcaNoText.setEnabled(false);
		codeCombo.setBackground(Color.white);
		codeText.setBackground(Color.lightGray);
		nameText.setBackground(Color.lightGray);
		orcaNoText.setBackground(Color.lightGray);
		imode = 2;
		codeCombo_Itemset();
		codeCombo.requestFocus();
	}

	// 確定ボタン
	void okButton_actionPerformed() {
		MsgDlg msgdlg = new MsgDlg(this);

		String cd = codeText.getText();
		String nm = nameText.getText();
		String snm = orcaNoText.getText();

		// 薬剤区分コードチェック
		if (cd.equals("") == true) {
			msgdlg.msgdsp("薬剤区分コードを指定してください。", MsgDlg.ERROR_MESSAGE);
			codeText.requestFocus();
			return;
		}
		if (cd.length() > 2) {
			msgdlg.msgdsp("薬剤区分コードは２桁以内を入力してください。", MsgDlg.ERROR_MESSAGE);
			codeText.requestFocus();
			return;
		}
		// 薬剤区分名チェック
		if (nm.equals("") == true) {
			msgdlg.msgdsp("薬剤区分名を入力してください。", MsgDlg.ERROR_MESSAGE);
			nameText.requestFocus();
			return;
		}
		if (nm.length() > 20) {
			msgdlg.msgdsp("薬剤区分名は２０文字以内を入力してください。", MsgDlg.ERROR_MESSAGE);
			nameText.requestFocus();
			return;
		}
		// 略称業者名チェック
		if (snm.equals("") == true) {
			msgdlg.msgdsp("日レセコードを入力してください。", MsgDlg.ERROR_MESSAGE);
			orcaNoText.requestFocus();
			return;
		}
		if (snm.length() > 2) {
			msgdlg.msgdsp("日レセコードは２文字以内を入力してください。", MsgDlg.ERROR_MESSAGE);
			orcaNoText.requestFocus();
			return;
		}

		String status = "";
		SyuruiCdNm item = new SyuruiCdNm(cd, snm, nm);
		BizDrugSyurui biz = new BizDrugSyurui();
		if (imode == 0) {
			// 新規登録
			status = biz.insSyuruiCdNm(item);
			if (status == "FOUND") {
				msgdlg.msgdsp("この薬剤種類コードは登録済みです。", MsgDlg.ERROR_MESSAGE);
			}
			if (status == "OK") {
				inputItem_clear();
			}
		} else if (imode == 1) {
			// 修正登録
			SyuruiCdNm selectItem = cItem[codeCombo.getSelectedIndex()];
			status = biz.updtSyuruiCdNm(selectItem, item);
			if (status == "FOUND") {
				msgdlg.msgdsp("修正した情報は、いずれかの要素が重複しています。", MsgDlg.ERROR_MESSAGE);
			}
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
			status = biz.delSyuruiCdNm(cd);
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

	// 薬剤種類選択コンボ
	void codeCombo_actionPerformed(ActionEvent e) {
		if (codeComb_busy == true)
			return;
		int i = codeCombo.getSelectedIndex();
		if (i >= 0) {
			codeText.setText(cItem[i].getCode()); // コードテキスト
			orcaNoText.setText(cItem[i].getOrcaCd()); // 日レセコード
			nameText.setText(cItem[i].getName()); // 名称テキスト
		}
	}

	// 業者選択コンボボックスに業者名を設定
	private void codeCombo_Itemset() {
		codeComb_busy = true; // コンボに設定中
		codeCombo.removeAllItems();
		// 修正・削除用のコンボに値をセット
		// CodeName item[] = null;
		BizContradrug biz = new BizContradrug();
		cItem = biz.getMed_kind_list();
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
		orcaNoText.setText(""); // 日レセコードテキスト
	}
}