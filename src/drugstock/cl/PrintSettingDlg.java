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
import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;


import drugstock.biz.BizContractor;
import drugstock.biz.BizContradrug;
import drugstock.cmn.Common;
import drugstock.cmn.DefaultJDialog;
import drugstock.cmn.MsgDlg;
import drugstock.component.BButton;
import com.borland.jbcl.layout.XYConstraints;
import com.borland.jbcl.layout.XYLayout;
import drugstock.model.CodeName;
import drugstock.model.ItemName;
import drugstock.model.SyuruiCdNm;

/**
 * 帳票印刷基本処理
 */

public class PrintSettingDlg extends DefaultJDialog {

	private JPanel panel1 = new JPanel();
	private XYLayout xYLayout1 = new XYLayout();
	private XYLayout xYLayout2 = new XYLayout();
	private JTextPane titlePanel = new JTextPane();
	private JLabel jLabel1 = new JLabel();
	private JTextField yyyyText = new JTextField();
	private JLabel jLabel2 = new JLabel();
	private JTextField mmText = new JTextField();
	private JLabel jLabel3 = new JLabel();
	private JLabel jLabel4 = new JLabel();
	private JComboBox conterCombo = new JComboBox();
	private JLabel jLabel5 = new JLabel();
	private JComboBox drugKindCombo = new JComboBox();
	// 印刷順位選択 04.02.27 onuki
	private JLabel jLabel6 = new JLabel();
	private JComboBox printRankCombo = new JComboBox();
	private int iRankSel = 0;
	// 詳細／合計選択 04.03.24 onuki
	private JLabel jLabel_ds = new JLabel();
	private JComboBox detailSumCombo = new JComboBox();
	private int iDetailSel = 0;
	//

	private BButton csvButton = new BButton();
	private BButton okButton = new BButton();
	private BButton tableButton = new BButton();
	private BButton cancelButton = new BButton();

	public CodeName contItem[] = null; // 業者
	public SyuruiCdNm syuruiCdNm[] = null; // 薬剤類コンボのコード、ネーム
	private String AndOr_flg = "AND"; // 指定品目使用患者一覧用条件フラグ

	private boolean bOk = false; // 印刷フラグ
	boolean bTable = false; // テーブル表示フラグ

	private int iContSel = 0; // 業者選択ポインタ
	private int iKindSel = 0;
	private JTextField mmText1 = new JTextField();
	private JTextField yyyyText1 = new JTextField();
	private JTextField yyyyText2 = new JTextField();
	private JLabel jLabel7 = new JLabel();
	private JTextField yyyyText3 = new JTextField();
	private JLabel jLabel8 = new JLabel();
	private JLabel jLabel9 = new JLabel();
	private JLabel jLabel10 = new JLabel();
	private JTextField yyyyText_F = new JTextField();
	private JTextField mmText_F = new JTextField();
	private JTextField ddText_F = new JTextField();
	private JLabel jLabel11 = new JLabel();
	private JLabel jLabel12 = new JLabel();
	private JLabel jLabel13 = new JLabel();
	private JLabel jLabel14 = new JLabel();
	private JTextField yyyyText_T = new JTextField();
	private JTextField mmText_T = new JTextField();
	private JTextField ddText_T = new JTextField();
	private JTextField itemText1 = new JTextField();
	private JTextField itemText2 = new JTextField();
	private JTextField itemText3 = new JTextField();
	private JTextField itemText4 = new JTextField();
	private JTextField itemText5 = new JTextField();
	private JRadioButton andButton = new JRadioButton();
	private JRadioButton orButton = new JRadioButton();// 選択ポインタ

	/*
	 * 利用不可モードフラグ
	 */
	private boolean yyyymmDis = false;
	private boolean FromToDis = false;
	private boolean ConterDis = false;
	private boolean DrugKindDis = false;
	private boolean PrintRankDis = false;
	private boolean DetailSumDis = false;
	private boolean ItemNoDis = false;

	private boolean ret = true;

	private JLabel jLabel17 = new JLabel();
	private TitledBorder titledBorder1;
	private TitledBorder titledBorder2;
	private ButtonGroup buttonGroup1 = new ButtonGroup();
	private JLabel itemText1_nm = new JLabel();
	private JLabel itemText2_nm = new JLabel();
	private JLabel itemText3_nm = new JLabel();
	private JLabel itemText4_nm = new JLabel();
	private JLabel itemText5_nm = new JLabel();

	private int syori_Mode;
	private String printFlg = "nop";

	// パネルが開かれた時に最初にフォーカスを当てるコンポーネント
	protected Component firstFocusComponent = null;

	public PrintSettingDlg(Frame frame, String title, boolean modal) {
		super(frame, title, modal);
		try {
			jbInit();
			pack();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public PrintSettingDlg() {
		this(null, "", false);
	}

	private void jbInit() throws Exception {
		titledBorder1 = new TitledBorder(BorderFactory.createEtchedBorder(
		        Color.white, new Color(148, 145, 140)), "品番");
		titledBorder2 = new TitledBorder(BorderFactory.createMatteBorder(4, 4,
		        4, 4, Color.lightGray), "");
		panel1.setLayout(xYLayout2);
		this.getContentPane().setLayout(xYLayout1);
		titlePanel.setBackground(Color.lightGray);
		titlePanel.setFont(new Font("Dialog", 0, 16));
		titlePanel.setToolTipText("");
		titlePanel.setText("ああああ");
		xYLayout1.setWidth(498);
		xYLayout1.setHeight(522);
		jLabel1.setFont(new Font("Dialog", 0, 16));
		jLabel1.setText("集計年月");
		jLabel2.setFont(new Font("Dialog", 0, 16));
		jLabel2.setText("年");
		jLabel3.setFont(new Font("Dialog", 0, 16));
		jLabel3.setText("月");
		jLabel4.setFont(new Font("Dialog", 0, 16));
		jLabel4.setText("業者");
		jLabel5.setFont(new Font("Dialog", 0, 16));
		jLabel5.setText("薬剤区分");
		// 印刷順位選択 04.02.27 onuki
		jLabel6.setFont(new Font("Dialog", 0, 16));
		jLabel6.setText("印刷");
		jLabel_ds.setFont(new Font("Dialog", 0, 16));
		jLabel_ds.setText("詳細");
		csvButton.setFont(new Font("Dialog", 0, 16));
		csvButton.setText("CSV出力");
		csvButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				bTable = false;
				csvButton_actionPerformed(e);
			}
		});
		okButton.setFont(new Font("Dialog", 0, 16));
		okButton.setText("帳票印刷");
		okButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				bTable = false;
				okButton_actionPerformed(e);
			}
		});
		tableButton.setFont(new Font("Dialog", 0, 16));
		tableButton.setText("画面表示");
		tableButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				bTable = true;
				okButton_actionPerformed(e);
			}
		});

		mmText.setFont(new Font("Dialog", 0, 16));
		mmText.addFocusListener(new FocusAdapter() {

			public void focusGained(FocusEvent e) {
				mmText_focusGained(e);
			}
		});
		mmText.setToolTipText("");
		cancelButton.setFont(new Font("Dialog", 0, 16));
		cancelButton.setText("戻る");
		cancelButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				cancelButton_actionPerformed(e);
			}
		});
		panel1.setFont(new Font("Dialog", 0, 14));
		this.setFont(new Font("Dialog", 0, 14));
		this.setTitle("処理条件入力画面");
		yyyyText.setFont(new Font("Dialog", 0, 16));
		yyyyText.addFocusListener(new FocusAdapter() {

			public void focusGained(FocusEvent e) {
				yyyyText_focusGained(e);
			}
		});
		mmText1.setToolTipText("");
		mmText1.setFont(new Font("Dialog", 0, 16));
		yyyyText1.setFont(new Font("Dialog", 0, 16));
		yyyyText2.setFont(new Font("Dialog", 0, 16));
		jLabel7.setFont(new Font("Dialog", 0, 16));
		jLabel7.setText("対象範囲");
		yyyyText3.setFont(new Font("Dialog", 0, 16));
		jLabel8.setFont(new Font("Dialog", 0, 16));
		jLabel8.setText("年");
		jLabel9.setFont(new Font("Dialog", 0, 16));
		jLabel9.setText("月");
		jLabel10.setFont(new Font("Dialog", 0, 16));
		jLabel10.setText("日");
		yyyyText_F.setFont(new Font("Dialog", 0, 16));
		yyyyText_F.addFocusListener(new FocusAdapter() {

			public void focusGained(FocusEvent e) {
				yyyyText_F_focusGained(e);
			}
		});
		yyyyText_F.setText("");
		mmText_F.setFont(new Font("Dialog", 0, 16));
		mmText_F.addFocusListener(new FocusAdapter() {

			public void focusGained(FocusEvent e) {
				mmText_F_focusGained(e);
			}
		});
		mmText_F.setText("");
		ddText_F.setFont(new Font("Dialog", 0, 16));
		ddText_F.addFocusListener(new FocusAdapter() {

			public void focusGained(FocusEvent e) {
				ddText_F_focusGained(e);
			}
		});
		ddText_F.setText("");
		jLabel11.setFont(new Font("Dialog", 0, 16));
		jLabel11.setText("〜");
		jLabel12.setFont(new Font("Dialog", 0, 16));
		jLabel12.setText("月");
		jLabel13.setFont(new Font("Dialog", 0, 16));
		jLabel13.setText("日");
		jLabel14.setFont(new Font("Dialog", 0, 16));
		jLabel14.setText("年");
		yyyyText_T.setFont(new Font("Dialog", 0, 16));
		yyyyText_T.addFocusListener(new FocusAdapter() {

			public void focusGained(FocusEvent e) {
				yyyyText_T_focusGained(e);
			}
		});
		yyyyText_T.setText("");
		mmText_T.setFont(new Font("Dialog", 0, 16));
		mmText_T.addFocusListener(new FocusAdapter() {

			public void focusGained(FocusEvent e) {
				mmText_T_focusGained(e);
			}
		});
		mmText_T.setText("");
		ddText_T.setFont(new Font("Dialog", 0, 16));
		ddText_T.addFocusListener(new FocusAdapter() {

			public void focusGained(FocusEvent e) {
				ddText_T_focusGained(e);
			}
		});
		ddText_T.setText("");
		itemText1.setFont(new Font("Dialog", 0, 16));
		itemText1.addFocusListener(new FocusAdapter() {

			public void focusLost(FocusEvent e) {
				itemText1_focusLost(e);
			}

			public void focusGained(FocusEvent e) {
				itemText1_focusGained(e);
			}
		});
		itemText2.setFont(new Font("Dialog", 0, 16));
		itemText2.addFocusListener(new FocusAdapter() {

			public void focusLost(FocusEvent e) {
				itemText2_focusLost(e);
			}

			public void focusGained(FocusEvent e) {
				itemText2_focusGained(e);
			}
		});
		itemText2.setText("");
		itemText3.setFont(new Font("Dialog", 0, 16));
		itemText3.addFocusListener(new FocusAdapter() {

			public void focusLost(FocusEvent e) {
				itemText3_focusLost(e);
			}

			public void focusGained(FocusEvent e) {
				itemText3_focusGained(e);
			}
		});
		itemText3.setText("");
		itemText4.setFont(new Font("Dialog", 0, 16));
		itemText4.addFocusListener(new FocusAdapter() {

			public void focusLost(FocusEvent e) {
				itemText4_focusLost(e);
			}

			public void focusGained(FocusEvent e) {
				itemText4_focusGained(e);
			}
		});
		itemText4.setText("");
		itemText5.setFont(new Font("Dialog", 0, 16));
		itemText5.addFocusListener(new FocusAdapter() {

			public void focusLost(FocusEvent e) {
				itemText5_focusLost(e);
			}

			public void focusGained(FocusEvent e) {
				itemText5_focusGained(e);
			}
		});
		itemText5.setText("");
		jLabel17.setFont(new Font("Dialog", 0, 16));
		jLabel17.setBorder(titledBorder1);
		jLabel17.setVerticalAlignment(SwingConstants.TOP);
		jLabel17.setVerticalTextPosition(SwingConstants.TOP);
		andButton.setFont(new Font("Dialog", 0, 16));
		andButton.setToolTipText("");
		andButton.setText("AND");
		andButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				andButton_actionPerformed(e);
			}
		});

		orButton.setFont(new Font("Dialog", 0, 16));
		orButton.setText("OR");
		orButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				orButton_actionPerformed(e);
			}
		});
		itemText1_nm.setFont(new Font("Dialog", 0, 16));
		itemText2_nm.setFont(new Font("Dialog", 0, 16));
		itemText3_nm.setFont(new Font("Dialog", 0, 16));
		itemText4_nm.setFont(new Font("Dialog", 0, 16));
		itemText5_nm.setFont(new Font("Dialog", 0, 16));
		this.getContentPane().add(panel1, new XYConstraints(0, 0, -1, -1));
		this.getContentPane().add(jLabel5, new XYConstraints(30, 204, -1, -1));
		this.getContentPane().add(drugKindCombo,
		        new XYConstraints(100, 202, -1, -1));
		// 印刷順位選択 04.02.27 onuki
		this.getContentPane().add(jLabel6, new XYConstraints(262, 206, -1, -1));
		this.getContentPane().add(printRankCombo,
		        new XYConstraints(300, 202, -1, -1));
		// 詳細／合計選択 04.03.24 onuki
		this.getContentPane().add(jLabel_ds,
		        new XYConstraints(262, 158, -1, -1));
		this.getContentPane().add(detailSumCombo,
		        new XYConstraints(300, 158, -1, -1));
		//

		this.getContentPane().add(titlePanel,
		        new XYConstraints(103, 23, -1, -1));
		this.getContentPane().add(jLabel1, new XYConstraints(30, 72, -1, -1));
		this.getContentPane().add(yyyyText, new XYConstraints(100, 70, 46, -1));
		this.getContentPane().add(jLabel2, new XYConstraints(151, 73, -1, -1));
		this.getContentPane().add(mmText, new XYConstraints(171, 71, 27, -1));
		this.getContentPane().add(jLabel3, new XYConstraints(200, 74, -1, -1));

		this.getContentPane().add(jLabel7, new XYConstraints(30, 116, -1, -1));
		this.getContentPane().add(yyyyText_F,
		        new XYConstraints(100, 114, 46, -1));
		this.getContentPane().add(jLabel8, new XYConstraints(151, 116, -1, -1));
		this.getContentPane().add(mmText_F, new XYConstraints(171, 114, 27, -1));
		this.getContentPane().add(jLabel9, new XYConstraints(202, 116, -1, -1));
		this.getContentPane().add(jLabel10, new XYConstraints(255, 116, -1, -1));
		// this.getContentPane().add(yyyyText_F, new XYConstraints(120, 150, 46,
		// -1));
		this.getContentPane().add(jLabel13, new XYConstraints(453, 116, -1, -1));
		this.getContentPane().add(jLabel12, new XYConstraints(400, 116, -1, -1));
		this.getContentPane().add(yyyyText_T,
		        new XYConstraints(298, 114, 46, -1));
		this.getContentPane().add(jLabel11, new XYConstraints(275, 117, -1, -1));
		this.getContentPane().add(ddText_F, new XYConstraints(222, 114, 27, -1));
		this.getContentPane().add(mmText_T, new XYConstraints(368, 114, 27, -1));
		this.getContentPane().add(jLabel14, new XYConstraints(350, 116, -1, -1));
		this.getContentPane().add(ddText_T, new XYConstraints(420, 114, 27, -1));

		this.getContentPane().add(conterCombo,
		        new XYConstraints(100, 158, -1, -1));
		this.getContentPane().add(jLabel4, new XYConstraints(30, 160, -1, -1));
		this.getContentPane().add(jLabel17,
		        new XYConstraints(27, 242, 450, 225));

		this.getContentPane().add(itemText5, new XYConstraints(46, 432, 58, -1));
		this.getContentPane().add(itemText4, new XYConstraints(46, 403, 58, -1));
		this.getContentPane().add(itemText3, new XYConstraints(46, 374, 58, -1));
		this.getContentPane().add(itemText2, new XYConstraints(46, 345, 58, -1));
		this.getContentPane().add(itemText1, new XYConstraints(46, 316, 58, -1));
		this.getContentPane().add(orButton, new XYConstraints(48, 285, -1, -1));
		this.getContentPane().add(andButton, new XYConstraints(48, 258, -1, -1));
		this.getContentPane().add(itemText1_nm,
		        new XYConstraints(110, 320, -1, -1));
		this.getContentPane().add(itemText2_nm,
		        new XYConstraints(110, 349, -1, -1));
		this.getContentPane().add(itemText3_nm,
		        new XYConstraints(110, 377, -1, -1));
		this.getContentPane().add(itemText4_nm,
		        new XYConstraints(110, 406, -1, -1));
		this.getContentPane().add(itemText5_nm,
		        new XYConstraints(110, 436, -1, -1));

		this.getContentPane().add(csvButton,
		        new XYConstraints(20, 481, 110, -1));
		this.getContentPane().add(okButton,
		        new XYConstraints(140, 481, 110, -1));
		this.getContentPane().add(tableButton,
		        new XYConstraints(260, 481, 110, -1));
		this.getContentPane().add(cancelButton,
		        new XYConstraints(380, 480, 100, -1));

		codeCombo_Itemset();
		kindCombo_Itemset();
		rankCombo_Itemset();
		detailCombo_Itemset();

		titlePanel.setEnabled(false);
		buttonGroup1.add(andButton);
		buttonGroup1.add(orButton);

		firstFocusComponent = yyyyText;
	}

	// 業者選択コンボボックスに業者名を設定
	private void codeCombo_Itemset() {
		conterCombo.setFont(new Font("Dialog", 0, 16));
		conterCombo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				conterCombo_actionPerformed(e);
			}
		});
		conterCombo.removeAllItems();
		conterCombo.addItem("全指定");
		// 修正・削除用のコンボに値をセット
		BizContractor biz = new BizContractor();
		contItem = biz.getCodeName();
		if (contItem != null) {
			for (int i = 0; i < contItem.length; i++) {
				conterCombo.addItem(contItem[i].getName());
			}
		}
		iContSel = 0;
	}

	// 種類コンボセット
	private void kindCombo_Itemset() {
		drugKindCombo.setFont(new Font("Dialog", 0, 16));
		drugKindCombo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				drugKindCombo_actionPerformed(e);
			}
		});
		BizContradrug bizsyurui = new BizContradrug();
		syuruiCdNm = bizsyurui.getMed_kind_list();
		drugKindCombo.removeAllItems();
		drugKindCombo.addItem("全指定"); // 空白フィールド設定
		if (syuruiCdNm != null) {
			for (int i = 0; i < syuruiCdNm.length; i++) {
				drugKindCombo.addItem(syuruiCdNm[i].name);
			}
		}
		iKindSel = 0;
	}

	// 印刷順位選択 04.02.27 onuki
	// 印刷順コンボセット
	private void rankCombo_Itemset() {
		printRankCombo.setFont(new Font("Dialog", 0, 16));
		printRankCombo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				printRankCombo_actionPerformed(e);
			}
		});
		printRankCombo.addItem("使用高");
		printRankCombo.addItem("差益高");
		iRankSel = 0;
	}

	// 詳細／合計コンボセット
	private void detailCombo_Itemset() {
		detailSumCombo.setFont(new Font("Dialog", 0, 16));
		detailSumCombo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				detailSumCombo_actionPerformed(e);
			}
		});
		detailSumCombo.addItem("詳細");
		detailSumCombo.addItem("合計");
		iDetailSel = 0;
	}

	/**
	 * 集計年月利用不可
	 */
	public void disableyyyymm() {
		yyyyText.setEnabled(false);
		mmText.setEnabled(false);
		yyyyText.setBackground(Color.lightGray);
		mmText.setBackground(Color.lightGray);
		yyyymmDis = true;
	}

	/**
	 * 対象範囲利用不可
	 */
	public void disableFromTo() {
		yyyyText_F.setEnabled(false);
		mmText_F.setEnabled(false);
		ddText_F.setEnabled(false);
		yyyyText_T.setEnabled(false);
		mmText_T.setEnabled(false);
		ddText_T.setEnabled(false);
		yyyyText_F.setBackground(Color.lightGray);
		mmText_F.setBackground(Color.lightGray);
		ddText_F.setBackground(Color.lightGray);
		yyyyText_T.setBackground(Color.lightGray);
		mmText_T.setBackground(Color.lightGray);
		ddText_T.setBackground(Color.lightGray);
		FromToDis = true;
	}

	/**
	 * 業者利用不可
	 */
	public void disableConter() {
		conterCombo.setEnabled(false);
		ConterDis = true;
	}

	/**
	 * 薬剤種別利用不可
	 */
	public void disableDrugKind() {
		drugKindCombo.setEnabled(false);
		DrugKindDis = true;
	}

	/**
	 * 印刷順利用不可
	 */
	public void disablePrintRank() {
		printRankCombo.setEnabled(false);
		PrintRankDis = true;
	}

	/**
	 * 詳細／合計利用不可
	 */
	public void disableDetailSum() {
		detailSumCombo.setEnabled(false);
		DetailSumDis = true;
	}

	/**
	 * 品番利用不可
	 */
	public void disableItemNo() {
		itemText1.setEnabled(false);
		itemText2.setEnabled(false);
		itemText3.setEnabled(false);
		itemText4.setEnabled(false);
		itemText5.setEnabled(false);
		itemText1.setBackground(Color.lightGray);
		itemText2.setBackground(Color.lightGray);
		itemText3.setBackground(Color.lightGray);
		itemText4.setBackground(Color.lightGray);
		itemText5.setBackground(Color.lightGray);
		andButton.setEnabled(false);
		orButton.setEnabled(false);
		ItemNoDis = true;
	}

	/**
	 * タイトルの設定
	 */
	public void setPaneTitle(String title) {
		titlePanel.setText(title);
	}

	/**
	 * 印刷フラグ参照
	 */
	public String getPrintFlg() {
		return printFlg;
	}

	/**
	 * 印刷開始ボタン
	 */
	void okButton_actionPerformed(ActionEvent e) {
		printFlg = "print";
		this.printPrepare();
	}

	/**
	 * CSV出力開始ボタン
	 */
	void csvButton_actionPerformed(ActionEvent e) {
		printFlg = "csv";
		this.printPrepare();
	}

	private void printPrepare() {
		ret = true;

		if (yyyymmDis != true) {
			String Chkyyyy;
			String Chkmm;
			Chkyyyy = yyyyText.getText();
			Chkmm = mmText.getText();
			Common com = new Common();
			ret = com.DateCheck(Chkyyyy, Chkmm, "1");

			if (ret == false) {
				yyyyText.requestFocus();
			}
		}

		if (ret != false && FromToDis != true) {
			String F_yy;
			String F_mm;
			String F_dd;
			String T_yy;
			String T_mm;
			String T_dd;

			Common com = new Common();

			F_yy = yyyyText_F.getText();
			F_mm = mmText_F.getText();
			F_dd = ddText_F.getText();
			ret = com.DateCheck(F_yy, F_mm, F_dd);
			if (ret == false) {
				yyyyText_F.requestFocus();
			} else {
				T_yy = yyyyText_T.getText();
				T_mm = mmText_T.getText();
				T_dd = ddText_T.getText();
				ret = com.DateCheck(T_yy, T_mm, T_dd);
				if (ret == false) {
					yyyyText_T.requestFocus();
				} else {
					ret = com.FromToDateCheck(F_yy, F_mm, F_dd, T_yy, T_mm,
					        T_dd);
					if (ret == false) {
						yyyyText_F.requestFocus();
					}
				}
			}
		}

		String cd1 = itemText1.getText();
		String cd2 = itemText2.getText();
		String cd3 = itemText3.getText();
		String cd4 = itemText4.getText();
		String cd5 = itemText5.getText();

		if (ret != false && ItemNoDis != true) {

			if (cd1.length() == 0 && cd2.length() == 0 && cd3.length() == 0
			        && cd4.length() == 0 && cd5.length() == 0) {
				MsgDlg msgdlg = new MsgDlg(this);
				msgdlg.msgdsp("品番が指定されていません。", MsgDlg.ERROR_MESSAGE);
				ret = false;
				itemText1.requestFocus();
			}
		}
		if (ret != false && ItemNoDis != true && cd1.length() > 0) {
			if (cd1.equals(cd2) || cd1.equals(cd3) || cd1.equals(cd4)
			        || cd1.equals(cd5)) {
				MsgDlg msgdlg = new MsgDlg(this);
				msgdlg.msgdsp("品番が重複しています。", MsgDlg.ERROR_MESSAGE);
				ret = false;
				itemText1.requestFocus();
			}
		}
		if (ret != false && ItemNoDis != true && cd2.length() > 0) {
			if (cd2.equals(cd1) || cd2.equals(cd3) || cd2.equals(cd4)
			        || cd2.equals(cd5)) {
				MsgDlg msgdlg = new MsgDlg(this);
				msgdlg.msgdsp("品番が重複しています。", MsgDlg.ERROR_MESSAGE);
				ret = false;
				itemText2.requestFocus();
			}
		}
		if (ret != false && ItemNoDis != true && cd3.length() > 0) {
			if (cd3.equals(cd1) || cd3.equals(cd2) || cd3.equals(cd4)
			        || cd3.equals(cd5)) {
				MsgDlg msgdlg = new MsgDlg(this);
				msgdlg.msgdsp("品番が重複しています。", MsgDlg.ERROR_MESSAGE);
				ret = false;
				itemText3.requestFocus();
			}
		}
		if (ret != false && ItemNoDis != true && cd4.length() > 0) {
			if (cd4.equals(cd1) || cd4.equals(cd2) || cd4.equals(cd3)
			        || cd4.equals(cd5)) {
				MsgDlg msgdlg = new MsgDlg(this);
				msgdlg.msgdsp("品番が重複しています。", MsgDlg.ERROR_MESSAGE);
				ret = false;
				itemText4.requestFocus();
			}
		}
		if (ret != false && ItemNoDis != true && cd5.length() > 0) {
			if (cd5.equals(cd1) || cd5.equals(cd2) || cd5.equals(cd3)
			        || cd5.equals(cd4)) {
				MsgDlg msgdlg = new MsgDlg(this);
				msgdlg.msgdsp("品番が重複しています。", MsgDlg.ERROR_MESSAGE);
				ret = false;
				itemText5.requestFocus();
			}
		}

		if (ret != false) {
			bOk = true;
			dispose();
		}
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
	 */
	public boolean IsOK() {
		return bOk;
	}

	/**
	 * テーブル表示フラグの取得
	 */
	public boolean IsTable() {
		return bTable;
	}

	/**
	 * 入力年の取得
	 */
	public String getYear() {
		String sRet = "";
		if (IsOK()) {
			return yyyyText.getText();
		}
		return sRet;
	}

	/**
	 * 入力月の取得
	 */
	public String getMonth() {
		String sRet = "";
		if (IsOK()) {
			return mmText.getText();
		}
		return sRet;
	}

	/**
	 * 対象範囲（From年）の取得
	 */
	public String getFromYear() {
		String sRet = "";
		if (IsOK()) {
			return yyyyText_F.getText();
		}
		return sRet;
	}

	/**
	 * 対象範囲（From月）の取得
	 */
	public String getFromMonth() {
		String sRet = "";
		if (IsOK()) {
			return mmText_F.getText();
		}
		return sRet;
	}

	/**
	 * 対象範囲（From日）の取得
	 */
	public String getFromDay() {
		String sRet = "";
		if (IsOK()) {
			return ddText_F.getText();
		}
		return sRet;
	}

	/**
	 * 対象範囲（To年）の取得
	 */
	public String getToYear() {
		String sRet = "";
		if (IsOK()) {
			return yyyyText_T.getText();
		}
		return sRet;
	}

	/**
	 * 対象範囲（To月）の取得
	 */
	public String getToMonth() {
		String sRet = "";
		if (IsOK()) {
			return mmText_T.getText();
		}
		return sRet;
	}

	/**
	 * 対象範囲（To日）の取得
	 */
	public String getToDay() {
		String sRet = "";
		if (IsOK()) {
			return ddText_T.getText();
		}
		return sRet;
	}

	/**
	 * 選択業者の取得（ID）
	 */
	public String getContractor() {
		String sRet = "0";
		if (IsOK()) {
			if (iContSel == 0) {
				return sRet;
			} else {
				return contItem[iContSel - 1].getid();
			}
		}
		return sRet;
	}

	/**
	 * 選択業者のINDEX
	 */
	public int getContSel() {
		return iContSel;
	}

	/**
	 * 薬剤区分の取得（ID)
	 */
	public String getDrug() {
		String sRet = "0";
		if (IsOK()) {
			if (iKindSel == 0) {
				return sRet;
			} else {
				return syuruiCdNm[iKindSel - 1].code;
			}
		}
		return sRet;
	}

	/**
	 * 薬剤区分のINDEX
	 */
	public int getiKindSel() {
		return iKindSel;
	}

	/**
	 * 印刷順番の取得（ID)
	 */
	public String getRank() {
		String sRet = "shiyou";
		if (IsOK()) {
			if (iRankSel == 0) {
				return sRet;
			} else {
				return "saeki";
			}
		}
		return sRet;
	}

	/**
	 * 印刷順番のINDEX (onuki)
	 */
	public int getiRankSel() {
		return iRankSel;
	}

	/**
	 * 印刷順番の取得（ID)
	 */
	public String getDetail() {
		String sRet = "detail";
		if (IsOK()) {
			if (iDetailSel == 0) {
				return sRet;
			} else {
				return "sum";
			}
		}
		return sRet;
	}

	/**
	 * 印刷順番のINDEX (onuki)
	 */
	public int getiDetailSel() {
		return iDetailSel;
	}

	/**
	 * 品番の条件取得
	 */
	public String getAndOr() {

		return AndOr_flg;

	}

	/**
	 * 品番の取得
	 */
	public ItemName getItemNo(int i) {

		ItemName cItem = new ItemName();

		switch (i) {
		case 1:
			if (itemText1.getText() != "") {
				cItem.code = itemText1.getText();
				cItem.name = itemText1_nm.getText();
			}
			break;
		case 2:
			if (itemText2.getText() != "") {
				cItem.code = itemText2.getText();
				cItem.name = itemText2_nm.getText();
			}
			break;
		case 3:
			if (itemText3.getText() != "") {
				cItem.code = itemText3.getText();
				cItem.name = itemText3_nm.getText();
			}
			break;
		case 4:
			if (itemText4.getText() != "") {
				cItem.code = itemText4.getText();
				cItem.name = itemText4_nm.getText();
			}
			break;
		case 5:
			if (itemText5.getText() != "") {
				cItem.code = itemText5.getText();
				cItem.name = itemText5_nm.getText();
			}
			break;

		}

		return cItem;

	}

	/**
	 * 業者選択の取得
	 */
	void conterCombo_actionPerformed(ActionEvent e) {
		iContSel = conterCombo.getSelectedIndex();
	}

	/**
	 * 薬剤種別選択の取得
	 */
	void drugKindCombo_actionPerformed(ActionEvent e) {
		iKindSel = drugKindCombo.getSelectedIndex();
	}

	/**
	 * 印刷順選択の取得
	 */
	void printRankCombo_actionPerformed(ActionEvent e) {
		iRankSel = printRankCombo.getSelectedIndex();
	}

	/**
	 * 詳細／合計選択の取得
	 */
	void detailSumCombo_actionPerformed(ActionEvent e) {
		iDetailSel = detailSumCombo.getSelectedIndex();
	}

	/**
	 * 処理モード設定
	 */
	public void SetSyoriMode(int mode) {
		syori_Mode = mode;
	}

	/**
	 * 抽出画面初期表示
	 */
	public void Init_Scr_Disp() {
		Date nowDate = new Date();
		SimpleDateFormat fmt1 = new SimpleDateFormat("yyyy");
		SimpleDateFormat fmt2 = new SimpleDateFormat("MM");

		if (yyyymmDis == true) {
			yyyyText.setText("");
			mmText.setText("");
		} else {
			yyyyText.setText(fmt1.format(nowDate));
			mmText.setText(fmt2.format(nowDate));
		}

		if (FromToDis == true) {
			yyyyText_F.setText("");
			mmText_F.setText("");
			ddText_F.setText("");
			yyyyText_T.setText("");
			mmText_T.setText("");
			ddText_T.setText("");
		} else {
			yyyyText_F.setText(fmt1.format(nowDate));
			mmText_F.setText(fmt2.format(nowDate));
			ddText_F.setText("01");
			yyyyText_T.setText(fmt1.format(nowDate));
			mmText_T.setText(fmt2.format(nowDate));

			Calendar cal = Calendar.getInstance();
			int yy;
			yy = Integer.parseInt(yyyyText_F.getText());
			int mm;
			mm = Integer.parseInt(mmText_F.getText()) - 1;
			cal.set(yy, mm, 1);
			yy = cal.get(Calendar.YEAR);
			mm = cal.get(Calendar.MONTH) + 1;
			int dd = cal.getActualMaximum(Calendar.DATE);
			String Last_dd = new String(new Long(dd).toString());
			ddText_T.setText(Last_dd);

		}

		if (ItemNoDis != true) {
			andButton.setSelected(true);
			AndOr_flg = "AND";
		}
	}

	// 品番1のロストフォーカス
	void itemText1_focusLost(FocusEvent e) {
		itemTextCommon_focusLost(itemText1, itemText1_nm);
	}

	// 品番2のロストフォーカス
	void itemText2_focusLost(FocusEvent e) {
		itemTextCommon_focusLost(itemText2, itemText2_nm);
	}

	// 品番3のロストフォーカス
	void itemText3_focusLost(FocusEvent e) {
		itemTextCommon_focusLost(itemText3, itemText3_nm);
	}

	// 品番4のロストフォーカス
	void itemText4_focusLost(FocusEvent e) {
		itemTextCommon_focusLost(itemText4, itemText4_nm);
	}

	// 品番5のロストフォーカス
	void itemText5_focusLost(FocusEvent e) {
		itemTextCommon_focusLost(itemText5, itemText5_nm);
	}

	// 品番のロストフォーカス／共通メソッド
	void itemTextCommon_focusLost(JTextField itemText, JLabel itemText_nm) {
		String itemNo = itemText.getText();
		if (itemNo.length() > 0) {
			BizContradrug biz = new BizContradrug();
			String item_nm = biz.get_item_nm(itemNo);
			// 品番が見付からない→短縮番号検索
			if (item_nm.equals("未登録")) {
				String orcaMedCd = biz.get_orcaMedCd_from_itemNo(itemNo);
				itemNo = biz.get_itemNo_orcaMedCd(orcaMedCd);
				itemText.setText(itemNo);
				item_nm = biz.get_item_nm(itemNo);
			}
			itemText_nm.setText(item_nm);
		} else {
			itemText_nm.setText("");

		}
	}

	void yyyyText_focusGained(FocusEvent e) {
		yyyyText.selectAll();
	}

	void mmText_focusGained(FocusEvent e) {
		mmText.selectAll();
	}

	void yyyyText_F_focusGained(FocusEvent e) {
		yyyyText_F.selectAll();
	}

	void yyyyText_T_focusGained(FocusEvent e) {
		yyyyText_T.selectAll();
	}

	void mmText_F_focusGained(FocusEvent e) {
		mmText_F.selectAll();
	}

	void mmText_T_focusGained(FocusEvent e) {
		mmText_T.selectAll();
	}

	void ddText_F_focusGained(FocusEvent e) {
		ddText_F.selectAll();
	}

	void ddText_T_focusGained(FocusEvent e) {
		ddText_T.selectAll();
	}

	void itemText1_focusGained(FocusEvent e) {
		itemText1.selectAll();
	}

	void itemText2_focusGained(FocusEvent e) {
		itemText2.selectAll();
	}

	void itemText3_focusGained(FocusEvent e) {
		itemText3.selectAll();
	}

	void itemText4_focusGained(FocusEvent e) {
		itemText4.selectAll();
	}

	void itemText5_focusGained(FocusEvent e) {
		itemText5.selectAll();
	}

	void andButton_actionPerformed(ActionEvent e) {
		AndOr_flg = "AND";
	}

	void orButton_actionPerformed(ActionEvent e) {
		AndOr_flg = "OR";
	}

	public void paint(Graphics g) {
		if (firstFocusComponent != null) {
			if (yyyymmDis == true) {
				if (FromToDis == true) {
					firstFocusComponent = conterCombo;
				} else {
					firstFocusComponent = yyyyText_F;
				}
			}
			firstFocusComponent.requestFocus();
			firstFocusComponent = null;
		}
		super.paint(g);
	}
}