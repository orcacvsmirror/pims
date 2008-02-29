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

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;


import drugstock.biz.BizContractor;
import drugstock.biz.BizContradrug;
import drugstock.cmn.DefaultJDialog;
import drugstock.cmn.MsgDlg;
import drugstock.component.BButton;
import com.borland.jbcl.layout.XYConstraints;
import com.borland.jbcl.layout.XYLayout;
import drugstock.model.CodeName;
import drugstock.model.ContItem;
import drugstock.model.ContractorInItemMdl;
import drugstock.model.OrcaMedicine;
import drugstock.model.SyuruiCdNm;
import drugstock.prt.StockListMedMaster;

/**
 * <p>
 * 「薬剤設定」画面処理
 * </p>
 */

public class ContradrugDlg extends DefaultJDialog {

	private int imode; // 0:新規 1:修正 2:削除
	private boolean gyousyaCombo_busy = false; // 業者選択コンボ アイテム設定中

	String gyoushaStr[] = null; // 選択業者リスト
	String masterStr[] = null; // 全業者リスト(固定)
	String masterOutStr[] = null; // 全業者リスト(選択業者との重複排除)
	SyuruiCdNm syuruiCdNm[] = null; // 薬剤種類コンボのコード、ネーム

	String tanshukuStr[] = null; // 短縮番号リスト
	String tanshukuReturnStr = null; // 短縮番号変更前の品番

	private XYLayout xYLayout1 = new XYLayout();
	private TitledBorder titledBorder1;
	private TitledBorder titledBorder_org;
	private TitledBorder titledBorder2;
	private JLabel jLabel_org = new JLabel();
	private JLabel jLabel1 = new JLabel();
	private JLabel jLabel2 = new JLabel();
	private JLabel jLabel3 = new JLabel();
	private JLabel jLabel4 = new JLabel();
	private JLabel jLabel5 = new JLabel();
	private JLabel jLabel6 = new JLabel();
	private JLabel jLabel7 = new JLabel();
	private JLabel jLabel8 = new JLabel();
	private JLabel jLabel9 = new JLabel();
	private JLabel jLabel10 = new JLabel();
	private JLabel jLabel11 = new JLabel();
	private JLabel jLabel12 = new JLabel();
	private JLabel jLabel13 = new JLabel();
	private JLabel jLabelHacchu = new JLabel();
	private JRadioButton modeRadio1 = new JRadioButton(); // 処理区分 新規
	private JRadioButton modeRadio2 = new JRadioButton(); // 処理区分 修正
	private JRadioButton modeRadio3 = new JRadioButton(); // 処理区分 削除

	private JRadioButton modeRadio_org_new = new JRadioButton(); // 独自薬剤 新規
	private JRadioButton modeRadio_org_ext = new JRadioButton(); // 独自薬剤 修正
	private JRadioButton modeRadio_org_del = new JRadioButton(); // 独自薬剤 削除

	private ButtonGroup modeGrp = new ButtonGroup(); // 処理区分グループ
	private ButtonGroup taxGrp = new ButtonGroup();
	private BButton drugsearchButton = new BButton();
	private BButton okButton = new BButton();
	private BButton cancelButton = new BButton();
	// マスタ印刷 04.04.09 onuki
	private BButton printButton = new BButton();
	private BButton tanshukuButton = new BButton();
	private BButton tanshukuReturnButton = new BButton();

	private JComboBox gyousyaCombo = new JComboBox();
	private JComboBox syuruiCombo = new JComboBox();
	private JTextField gyousyacdText = new JTextField();
	private JTextField orcadrugcdText = new JTextField();
	private JTextField hinbanText = new JTextField();
	private JTextField nameText = new JTextField();
	private JTextField kananmText = new JTextField();
	private JTextField konpoutniText = new JTextField();
	private JTextField housoutniText = new JTextField();
	private JTextField baratniText = new JTextField();
	private JTextField saisinntnkText = new JTextField();
	private JTextField nebikirituText = new JTextField();
	private JTextField hacchuText = new JTextField();
	private JLabel jLabel14 = new JLabel();
	private JLabel jLabel15 = new JLabel();
	private JLabel jLabelTanshuku = new JLabel();
	// '04.03.16 onuki
	private JTextField unitText = new JTextField();
	private JTextField yakkaText = new JTextField();
	private JComboBox tanshukuCombo = new JComboBox();
	private JComboBox shukkoCombo = new JComboBox();
	private JLabel jLabelUnit = new JLabel();
	private JLabel jLabelYakka = new JLabel();
	private JLabel jLabelYakkaYen = new JLabel();
	private JLabel jLabelShukko = new JLabel();
	private String shukko_flag = "0"; // 自動出庫 0:あり 1:なし
	// 薬剤操作モード 04.04.02 onuki
	private static int MODE_NEW = 0; // 新規
	private static int MODE_EXT = 1; // 修正
	private static int MODE_DEL = 2; // 削除
	private static int MODE_ORG_NEW = 3; // 独自薬剤・新規
	private static int MODE_ORG_EXT = 4; // 独自薬剤・修正
	private static int MODE_ORG_DEL = 5; // 独自薬剤・削除
	// 修正前の品番を待避 04.04.05 onuki
	private String tmp_item_no = "";

	private JList jList_master = new JList();
	private JList jList_gyousha = new JList();
	private JScrollPane sch_master = new JScrollPane(jList_master);
	private JScrollPane sch_gyousha = new JScrollPane(jList_gyousha);
	private BButton gyoushaAddButton = new BButton();
	private BButton gyoushaDelButton = new BButton();
	// private int listH = 90 ; // 業者マスタリスト縦幅
	private int listH = 160; // 業者マスタリスト縦幅

	public ContradrugDlg(Frame frame, String title, boolean modal) {
		super(frame, title, modal);
		try {
			this.setTitle(title);
			jbInit();
			pack();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public ContradrugDlg() {
		this(null, "薬剤設定", false);
	}

	private void jbInit() throws Exception {
		nebikirituText.setFont(new Font("Dialog", 0, 16));
		nebikirituText.setHorizontalAlignment(SwingConstants.RIGHT);
		saisinntnkText.setFont(new Font("Dialog", 0, 16));
		saisinntnkText.setHorizontalAlignment(SwingConstants.RIGHT);
		// baratniText.setFont(new Font("Dialog", 0, 16));
		// baratniText.setHorizontalAlignment(JTextField.RIGHT);
		housoutniText.setFont(new Font("Dialog", 0, 16));
		housoutniText.setHorizontalAlignment(SwingConstants.RIGHT);
		nameText.setFont(new Font("Dialog", 0, 16));
		kananmText.setFont(new Font("Dialog", 0, 16));
		konpoutniText.setFont(new Font("Dialog", 0, 16));
		konpoutniText.setHorizontalAlignment(SwingConstants.RIGHT);
		hinbanText.setFont(new Font("Dialog", 0, 16));
		orcadrugcdText.setFont(new Font("Dialog", 0, 16));
		syuruiCombo.setFont(new Font("Dialog", 0, 16));
		syuruiCombo.setAutoscrolls(true);
		hacchuText.setFont(new Font("Dialog", 0, 16));
		hacchuText.setHorizontalAlignment(SwingConstants.RIGHT);
		jList_master.setFont(new Font("Dialog", 0, 16));
		jList_gyousha.setFont(new Font("Dialog", 0, 16));

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
		modeRadio_org_new.setFont(new Font("Dialog", 0, 16));
		modeRadio_org_new.setText("新規");
		modeRadio_org_new.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				modeRadio_org_new_actionPerformed(e);
			}
		});
		modeRadio_org_ext.setFont(new Font("Dialog", 0, 16));
		modeRadio_org_ext.setText("修正");
		modeRadio_org_ext.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				modeRadio_org_ext_actionPerformed(e);
			}
		});
		modeRadio_org_del.setFont(new Font("Dialog", 0, 16));
		modeRadio_org_del.setText("削除");
		modeRadio_org_del.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				modeRadio_org_del_actionPerformed(e);
			}
		});

		xYLayout1.setWidth(579);
		xYLayout1.setHeight(475 + listH);

		titledBorder1 = new TitledBorder(BorderFactory.createEtchedBorder(
		        Color.white, new Color(148, 145, 140)), "一般薬剤");
		titledBorder_org = new TitledBorder(BorderFactory.createEtchedBorder(
		        Color.white, new Color(148, 145, 140)), "独自薬剤");
		titledBorder2 = new TitledBorder(BorderFactory.createEtchedBorder(
		        Color.white, new Color(148, 145, 140)), "薬剤指定");
		jLabel1.setToolTipText("");
		jLabel1.setFont(new Font("Dialog", 0, 16));
		jLabel1.setBorder(titledBorder1);
		// 04.04.01
		jLabel_org.setToolTipText("");
		jLabel_org.setFont(new Font("Dialog", 0, 16));
		jLabel_org.setBorder(titledBorder_org);

		jLabel2.setFont(new Font("Dialog", 0, 16));
		jLabel2.setText("卸業者");
		jLabel3.setToolTipText("");
		jLabel3.setFont(new Font("Dialog", 0, 16));
		jLabel3.setBorder(titledBorder2);
		jLabel4.setFont(new Font("Dialog", 0, 16));
		jLabel4.setText("日レセ薬剤CD");
		jLabel5.setFont(new Font("Dialog", 0, 16));
		jLabel5.setText("品番");
		jLabelTanshuku.setFont(new Font("Dialog", 0, 16));
		jLabelTanshuku.setText("短縮コード");
		jLabel6.setFont(new Font("Dialog", 0, 16));
		jLabel6.setText("薬剤名称");
		jLabel7.setFont(new Font("Dialog", 0, 16));
		jLabel7.setText("薬剤カナ名");
		jLabel8.setFont(new Font("Dialog", 0, 16));
		jLabel8.setText("薬剤区分");
		jLabel9.setFont(new Font("Dialog", 0, 16));
		jLabel9.setToolTipText("");
		jLabel9.setText("梱包数単位");
		jLabel10.setFont(new Font("Dialog", 0, 16));
		jLabel10.setText("包装数単位");
		// jLabel11.setFont(new Font("Dialog", 0, 16));
		// jLabel11.setText("バラ数単位");
		jLabel12.setFont(new Font("Dialog", 0, 16));
		jLabel12.setText("最新納入単価");
		konpoutniText.setText("0");
		housoutniText.setText("0");
		// baratniText.setText("0");
		saisinntnkText.setText("0");
		nebikirituText.setText("0");
		hacchuText.setText("0");
		jLabel13.setFont(new Font("Dialog", 0, 16));
		jLabel13.setText("単品値引率");
		jLabelHacchu.setFont(new Font("Dialog", 0, 16));
		jLabelHacchu.setText("発注用P点");
		gyousyacdText.setBackground(Color.lightGray);
		gyousyacdText.setFont(new Font("Dialog", 0, 16));
		jLabel14.setFont(new Font("Dialog", 0, 16));
		jLabel14.setText("%");
		jLabel15.setFont(new Font("Dialog", 0, 16));
		jLabel15.setText("円");
		gyousyaCombo.setFont(new Font("Dialog", 0, 16));
		gyousyaCombo.setAutoscrolls(true);
		okButton.setFont(new Font("Dialog", 0, 16));
		cancelButton.setFont(new Font("Dialog", 0, 16));
		printButton.setFont(new Font("Dialog", 0, 16));
		drugsearchButton.setFont(new Font("Dialog", 0, 16));
		tanshukuButton.setFont(new Font("Dialog", 0, 16));
		tanshukuReturnButton.setFont(new Font("Dialog", 0, 16));
		modeGrp.add(modeRadio1);
		modeGrp.add(modeRadio2);
		modeGrp.add(modeRadio3);
		modeGrp.add(modeRadio_org_new);
		modeGrp.add(modeRadio_org_ext);
		modeGrp.add(modeRadio_org_del);
		gyousyacdText.setText("");
		orcadrugcdText.setText("");
		hinbanText.setText("");
		kananmText.setText("");
		nameText.setText("");

		// '04.03.16 onuki
		yakkaText.setFont(new Font("Dialog", 0, 16));
		yakkaText.setHorizontalAlignment(SwingConstants.RIGHT);
		unitText.setFont(new Font("Dialog", 0, 16));
		unitText.setHorizontalAlignment(SwingConstants.RIGHT);
		tanshukuCombo.setFont(new Font("Dialog", 0, 16));
		tanshukuCombo.setAutoscrolls(true);
		shukkoCombo.setFont(new Font("Dialog", 0, 16));
		shukkoCombo.setAutoscrolls(true);
		jLabelUnit.setFont(new Font("Dialog", 0, 16));
		jLabelUnit.setText("単位");
		jLabelYakka.setFont(new Font("Dialog", 0, 16));
		jLabelYakka.setText("薬価");
		jLabelYakkaYen.setFont(new Font("Dialog", 0, 16));
		jLabelYakkaYen.setText("円");
		jLabelShukko.setFont(new Font("Dialog", 0, 16));
		jLabelShukko.setText("自動出庫");

		drugsearchButton.setText("薬剤検索");
		drugsearchButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				drugsearchButton_actionPerformed(e);
			}
		});

		tanshukuButton.setText("↑設定");
		tanshukuButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				tanshukuButton_actionPerformed(e);
			}
		});

		tanshukuReturnButton.setText("戻す");
		tanshukuReturnButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				tanshukuReturnButton_actionPerformed(e);
			}
		});

		okButton.setText("確定");
		okButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				okButton_actionPerformed(e);
			}
		});
		cancelButton.setText("戻る");
		cancelButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				cancelButton_actionPerformed(e);
			}
		});
		printButton.setText("マスタ印刷");
		printButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				printButton_actionPerformed(e);
			}
		});
		// マスタ印刷をトップに以降、ここでのボタンを不可視に
		printButton.setVisible(false);
		// gyousyaCombo.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent e) {
		// gyousyaCombo_actionPerformed(e);
		// }
		// });

		// 短縮コードコンボ動作
		tanshukuCombo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// tanshukuCombo_actionPerformed(e);
			}
		});

		// 自動出庫コンボ動作
		shukkoCombo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				shukkoCombo_actionPerformed(e);
			}
		});

		gyoushaAddButton.setFont(new Font("Dialog", 0, 16));
		gyoushaAddButton.setText("＜追加 ");
		gyoushaAddButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				gyoushaAddButton_actionPerformed(e);
			}
		});
		gyoushaDelButton.setFont(new Font("Dialog", 0, 16));
		gyoushaDelButton.setText(" 削除＞");
		gyoushaDelButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				gyoushaDelButton_actionPerformed(e);
			}
		});

		orcadrugcdText.setDisabledTextColor(Color.black); // 日レセ薬剤CD
		hinbanText.setDisabledTextColor(Color.black); // 品番
		nameText.setDisabledTextColor(Color.black); // 薬剤名称
		kananmText.setDisabledTextColor(Color.black); // 薬剤名称カナ
		konpoutniText.setDisabledTextColor(Color.black); // 梱包単位数
		housoutniText.setDisabledTextColor(Color.black); // 包装単位数
		// baratniText.setDisabledTextColor(Color.black); // バラ単位数
		saisinntnkText.setDisabledTextColor(Color.black); // 単価
		nebikirituText.setDisabledTextColor(Color.black); // 値引率
		unitText.setDisabledTextColor(Color.black); // 単価
		yakkaText.setDisabledTextColor(Color.black); // 値引率

		this.getContentPane().setLayout(xYLayout1);
		this.getContentPane().add(jLabel13,
		        new XYConstraints(32, 385 + listH, -1, -1));
		this.getContentPane().add(jLabel6,
		        new XYConstraints(32, 199 + listH, -1, -1));
		this.getContentPane().add(jLabel7,
		        new XYConstraints(32, 236 + listH, -1, -1));
		this.getContentPane().add(jLabel8,
		        new XYConstraints(32, 273 + listH, -1, -1));
		this.getContentPane().add(jLabel12,
		        new XYConstraints(32, 311 + listH, -1, -1));
		this.getContentPane().add(jLabel9,
		        new XYConstraints(32, 348 + listH, -1, -1));
		// this.getContentPane().add(cancelButton, new XYConstraints(299, 427,
		// 97, 31));
		// this.getContentPane().add(okButton, new XYConstraints(102, 428, 97,
		// 31));
		this.getContentPane().add(printButton,
		        new XYConstraints(32, 428 + listH, 147, 31));
		//
		this.getContentPane().add(cancelButton,
		        new XYConstraints(449, 427 + listH, 97, 31));
		this.getContentPane().add(okButton,
		        new XYConstraints(342, 428 + listH, 97, 31));
		this.getContentPane().add(hacchuText,
		        new XYConstraints(132, 420 + listH, 78, 24));
		this.getContentPane().add(jLabelHacchu,
		        new XYConstraints(32, 420 + listH, -1, 24));
		this.getContentPane().add(saisinntnkText,
		        new XYConstraints(132, 311 + listH, 78, 24));
		this.getContentPane().add(nebikirituText,
		        new XYConstraints(132, 384 + listH, 48, 24));
		this.getContentPane().add(nameText,
		        new XYConstraints(131, 198 + listH, 357, 24));
		this.getContentPane().add(konpoutniText,
		        new XYConstraints(132, 346 + listH, 78, 24));
		this.getContentPane().add(syuruiCombo,
		        new XYConstraints(131, 273 + listH, -1, 24));
		// '04.03.16 onuki
		this.getContentPane().add(unitText,
		        new XYConstraints(391, 273 + listH, 78, 24));
		this.getContentPane().add(jLabelUnit,
		        new XYConstraints(301, 273 + listH, -1, 24));
		this.getContentPane().add(yakkaText,
		        new XYConstraints(391, 311 + listH, 78, 24));
		this.getContentPane().add(jLabelYakka,
		        new XYConstraints(301, 311 + listH, -1, 24));
		this.getContentPane().add(jLabelYakkaYen,
		        new XYConstraints(471, 311 + listH, -1, 24));
		this.getContentPane().add(shukkoCombo,
		        new XYConstraints(391, 384 + listH, -1, 24));
		this.getContentPane().add(jLabelShukko,
		        new XYConstraints(301, 384 + listH, -1, 24));
		//
		this.getContentPane().add(kananmText,
		        new XYConstraints(131, 236 + listH, 217, 24));
		this.getContentPane().add(jLabel15,
		        new XYConstraints(218, 313 + listH, -1, -1));
		this.getContentPane().add(jLabel10,
		        new XYConstraints(301, 348 + listH, -1, -1));
		this.getContentPane().add(jLabel11,
		        new XYConstraints(397, 346 + listH, -1, -1));
		// this.getContentPane().add(baratniText, new XYConstraints(485, 345,
		// 75, 24));
		this.getContentPane().add(housoutniText,
		        new XYConstraints(391, 346 + listH, 78, 24));

		this.getContentPane().add(orcadrugcdText,
		        new XYConstraints(301, 95, 170, 24));
		this.getContentPane().add(jLabel4, new XYConstraints(190, 97, -1, -1));
		this.getContentPane().add(hinbanText,
		        new XYConstraints(301, 128, 170, 24));
		this.getContentPane().add(jLabel5, new XYConstraints(191, 130, -1, -1));
		this.getContentPane().add(tanshukuButton,
		        new XYConstraints(301, 161, 80, 26));
		this.getContentPane().add(tanshukuReturnButton,
		        new XYConstraints(391, 161, 80, 26));
		this.getContentPane().add(tanshukuCombo,
		        new XYConstraints(301, 196, 170, 24));
		this.getContentPane().add(jLabelTanshuku,
		        new XYConstraints(191, 198, -1, -1));
		this.getContentPane().add(drugsearchButton,
		        new XYConstraints(47, 92, 123, 26));
		this.getContentPane().add(jLabel3,
		        new XYConstraints(26, 74, 507, 90 + 70)); // 薬剤フレーム
		// this.getContentPane().add(gyousyacdText, new XYConstraints(311,
		// 75+listH, 86, 24));
		// this.getContentPane().add(gyousyaCombo, new XYConstraints(130,
		// 75+listH, 173, 24));
		this.getContentPane().add(jLabel14,
		        new XYConstraints(185, 386 + listH, -1, -1));
		this.getContentPane().add(modeRadio1, new XYConstraints(38, 34, 69, 24));
		this.getContentPane().add(modeRadio2,
		        new XYConstraints(111, 34, 69, 24));
		this.getContentPane().add(modeRadio3,
		        new XYConstraints(186, 34, 69, 24));
		this.getContentPane().add(modeRadio_org_new,
		        new XYConstraints(320, 34, 69, 24));
		this.getContentPane().add(modeRadio_org_ext,
		        new XYConstraints(393, 34, 69, 24));
		this.getContentPane().add(modeRadio_org_del,
		        new XYConstraints(466, 34, 69, 24));

		this.getContentPane().add(jLabel1, new XYConstraints(27, 10, 243, 60)); // 処理区分フレーム
		this.getContentPane().add(jLabel_org,
		        new XYConstraints(300, 10, 243, 60)); // 独自薬剤フレーム

		this.getContentPane().add(jLabel2,
		        new XYConstraints(35, 76 + listH, -1, -1));
		this.getContentPane().add(sch_gyousha,
		        new XYConstraints(35, 100 + listH, 173, 80));
		this.getContentPane().add(sch_master,
		        new XYConstraints(320, 100 + listH, 173, 80));
		this.getContentPane().add(gyoushaAddButton,
		        new XYConstraints(210, 110 + listH, 100, 26));
		this.getContentPane().add(gyoushaDelButton,
		        new XYConstraints(210, 140 + listH, 100, 26));

		// 業者マスタリスト
		gyousyaCombo_busy = true;
		BizContractor biz = new BizContractor();
		CodeName masterCdNm[] = biz.getCodeName();
		masterStr = this.outputList(masterCdNm, jList_master);
		masterOutStr = masterStr;
		gyousyaCombo_busy = false;
		initList();

		// 種類コンボセット
		BizContradrug bizsyurui = new BizContradrug();
		syuruiCdNm = bizsyurui.getMed_kind_list();
		syuruiCombo.removeAllItems();
		if (syuruiCdNm != null) {
			for (int i = 0; i < syuruiCdNm.length; i++) {
				syuruiCombo.addItem(syuruiCdNm[i].name);
			}
		}

		// 自動出庫コンボセット
		shukkoCombo.removeAllItems();
		shukkoCombo.addItem("あり");
		shukkoCombo.addItem("なし");

		// 短縮コードコンボセット
		tanshukuCombo.removeAllItems();
		shukkoCombo.addItem("未設定");

		gyousyacdText.setEnabled(false);

		// 初期値表示
		modeRadio1.setSelected(true);
		inputItem_clear();
		imode = 99;
		modeRadio1_actionPerformed(null);

		// if(gyousyaCdNm.length > 0){
		// gyousyacdText.setText(gyousyaCdNm[0].getCode());
		// }
	}

	class ContradrugEL implements KeyListener {

		public void keyTyped(KeyEvent e) {
			;
		}

		public void keyPressed(KeyEvent e) {
			;
		}

		public void keyReleased(KeyEvent e) {
			int key = e.getKeyCode();
			if (e.getID() == KeyEvent.KEY_RELEASED) {
				if (e.getKeyChar() == '\n') {
					try {
						Robot robot = new Robot();
						robot.keyPress(KeyEvent.VK_TAB);
					} catch (AWTException eee) {
					}
				}
			}
		}
	}

	/**
	 * ウィンドウが開かれたときのイベントをオーバーライドします。
	 */
	protected void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_OPENED) {
			gyousyaCombo.requestFocus();
		}
	}

	// 処理モード（新規）
	void modeRadio1_actionPerformed(ActionEvent e) {
		if (imode == MODE_NEW)
			return;
		int isInputList[] = { 1, 0, 0, 1, 0 };

		inputItem_clear();
		setItem_init(isInputList);

		imode = MODE_NEW;
	}

	// 処理モード（修正）
	void modeRadio2_actionPerformed(ActionEvent e) {
		if (imode == MODE_EXT)
			return;
		int isInputList[] = { 1, 0, 0, 1, 0 };

		inputItem_clear();
		setItem_init(isInputList);

		imode = MODE_EXT;
	}

	// 処理モード（削除）
	void modeRadio3_actionPerformed(ActionEvent e) {
		if (imode == MODE_DEL)
			return;
		int isInputList[] = { 1, 1, 1, 1, 1 };

		inputItem_clear();
		setItem_init(isInputList);

		imode = MODE_DEL;
	}

	// 独自薬剤（登録）
	void modeRadio_org_new_actionPerformed(ActionEvent e) {
		if (imode == MODE_ORG_NEW)
			return;
		int isInputList[] = { 0, 0, 0, 0, 1 };

		inputItem_clear(false);
		setItem_init(isInputList);

		orcadrugcdText.setText("XXX"); // 日レセ薬剤CD
		imode = MODE_ORG_NEW;
	}

	// 独自薬剤（修正）
	void modeRadio_org_ext_actionPerformed(ActionEvent e) {
		if (imode == MODE_ORG_EXT)
			return;
		int isInputList[] = { 0, 0, 0, 0, 1 };

		inputItem_clear();
		setItem_init(isInputList);

		imode = MODE_ORG_EXT;
	}

	// 独自薬剤（削除）
	void modeRadio_org_del_actionPerformed(ActionEvent e) {
		if (imode == MODE_ORG_DEL)
			return;
		int isInputList[] = { 1, 1, 1, 1, 1 };

		inputItem_clear();
		setItem_init(isInputList);

		imode = MODE_ORG_DEL;
	}

	// 確定ボタン
	void okButton_actionPerformed(ActionEvent e) {
		MsgDlg msgdlg = new MsgDlg(this);
		// 業者リストのチェック
		if (gyoushaStr.length == 0) {
			msgdlg.msgdsp("業者コードを指定してください。", MsgDlg.ERROR_MESSAGE);
			gyoushaAddButton.requestFocus();
			return;
		}
		// ＯＲＣＡ薬剤ＣＤ指定有無チェック
		if (orcadrugcdText.getText().equals("")) {
			msgdlg.msgdsp("ＯＲＣＡ薬剤ＣＤを指定してください。", MsgDlg.ERROR_MESSAGE);
			drugsearchButton.requestFocus();
			return;
		}
		// 品番入力チェック
		if (hinbanText.getText().equals("")) {
			msgdlg.msgdsp("品番を指定してください。", MsgDlg.ERROR_MESSAGE);
			hinbanText.requestFocus();
			return;
		}
		if (hinbanText.getText().length() > 20) {
			msgdlg.msgdsp("品番は20桁以下です。", MsgDlg.ERROR_MESSAGE);
			hinbanText.requestFocus();
			return;
		}
		// 薬剤名称
		if (nameText.getText().equals("") == true) {
			msgdlg.msgdsp("薬剤名称を入力してください。", MsgDlg.ERROR_MESSAGE);
			nameText.requestFocus();
			return;
		}
		if (nameText.getText().length() > 60) {
			msgdlg.msgdsp("薬剤名称は６０文字以内を入力してください。", MsgDlg.ERROR_MESSAGE);
			nameText.requestFocus();
			return;
		}
		// カナ薬剤名称
		if (kananmText.getText().equals("") == true) {
			msgdlg.msgdsp("カナ薬剤名称を入力してください。", MsgDlg.ERROR_MESSAGE);
			kananmText.requestFocus();
			return;
		}
		if (kananmText.getText().length() > 60) {
			msgdlg.msgdsp("カナ薬剤名称は６０文字以内を入力してください。", MsgDlg.ERROR_MESSAGE);
			kananmText.requestFocus();
			return;
		}
		// 梱包単位;
		try {
			double dd = Double.parseDouble(konpoutniText.getText());
			if (dd < 0 || dd > 999999.99) {
				msgdlg.msgdsp("梱包単位０〜９９９９９９．９９を入力してください。",
				                MsgDlg.ERROR_MESSAGE);
				konpoutniText.requestFocus();
				return;
			}
		} catch (NumberFormatException er) {
			msgdlg.msgdsp("梱包単位は数値を入力してください。", MsgDlg.ERROR_MESSAGE);
			konpoutniText.requestFocus();
			return;
		}

		// 包装単位
		try {
			double dd = Double.parseDouble(housoutniText.getText());
			if (dd < 0 || dd > 999999.99) {
				msgdlg.msgdsp("包装単位０〜９９９９９９．９９を入力してください。",
				                MsgDlg.ERROR_MESSAGE);
				housoutniText.requestFocus();
				return;
			}
		} catch (NumberFormatException er) {
			msgdlg.msgdsp("梱包単位は数値を入力してください。", MsgDlg.ERROR_MESSAGE);
			housoutniText.requestFocus();
			return;
		}

		// 最新単価
		try {
			double dd = Double.parseDouble(saisinntnkText.getText());
			if (dd < 0 || dd > 9999999.99) {
				msgdlg.msgdsp("最新単価０〜９９９９９９９．９９を入力してください。",
				        MsgDlg.ERROR_MESSAGE);
				saisinntnkText.requestFocus();
				return;
			}
		} catch (NumberFormatException er) {
			msgdlg.msgdsp("最新単価は数値を入力してください。", MsgDlg.ERROR_MESSAGE);
			saisinntnkText.requestFocus();
			return;
		}

		// 薬価 '04.03.16 onuki
		try {
			double dd = Double.parseDouble(yakkaText.getText());
			if (dd < 0 || dd > 999999.99) {
				msgdlg.msgdsp("薬価０〜９９９９９９．９９を入力してください。", MsgDlg.ERROR_MESSAGE);
				yakkaText.requestFocus();
				return;
			}
		} catch (NumberFormatException er) {
			msgdlg.msgdsp("薬価は数値を入力してください。", MsgDlg.ERROR_MESSAGE);
			yakkaText.requestFocus();
			return;
		}

		// 単位 '04.03.16 onuki
		/*
		 * if( (unitText.getText().equals("") == true)&& (imode == MODE_ORG_NEW) ||
		 * (imode == MODE_ORG_EXT) || (imode == MODE_ORG_DEL) ){
		 * msgdlg.msgdsp("単位を入力してください。",msgDlg.ERROR_MESSAGE);
		 * unitText.requestFocus(); return; }
		 */
		if (unitText.getText().length() > 10) {
			msgdlg.msgdsp("単位は１０文字以内を入力してください。", MsgDlg.ERROR_MESSAGE);
			unitText.requestFocus();
			return;
		}

		// 値引率チェック
		try {
			int i = Integer.parseInt(nebikirituText.getText());
			if (i < 0 || i > 100) {
				msgdlg.msgdsp("値引率は０〜１００を入力してください。", MsgDlg.ERROR_MESSAGE);
				nebikirituText.requestFocus();
				return;
			}
		} catch (NumberFormatException er) {
			msgdlg.msgdsp("値引率は整数値を入力してください。", MsgDlg.ERROR_MESSAGE);
			nebikirituText.requestFocus();
			return;
		}

		// 発注点チェック
		try {
			double dd = Double.parseDouble(hacchuText.getText());
			if (dd < 0 || dd > 9999999.99) {
				msgdlg.msgdsp("発注点は０〜９９９９９９９．９９を入力してください。",
				        MsgDlg.ERROR_MESSAGE);
				hacchuText.requestFocus();
				return;
			}
		} catch (NumberFormatException er) {
			msgdlg.msgdsp("発注点は数値を入力してください。", MsgDlg.ERROR_MESSAGE);
			hacchuText.requestFocus();
			return;
		}

		// 選択業者のCodeNameを作成
		CodeName masterCdNm[] = new BizContractor().getCodeName();

		CodeName inGyoushaCdnm[] = new CodeName[gyoushaStr.length];
		int j = 0;
		for (int i = 0; i < masterStr.length; i++) {
			while (masterStr[i].equals(gyoushaStr[j]) == false)
				i++;
			if (i >= masterStr.length)
				break;
			inGyoushaCdnm[j] = masterCdNm[i];
			j++;
			if (j >= gyoushaStr.length)
				break;
		}

		// String cont_id = inGyoushaCdnm[0].getid(); // 業者ＩＤ
		String orca_med_cd = orcadrugcdText.getText(); // ＯＲＣＡ薬剤ＣＤ
		String item_no = hinbanText.getText(); // 品番
		String med_nm = nameText.getText(); // 薬剤名称
		String med_kn = kananmText.getText(); // 薬剤名称カナ
		String med_kind1 = syuruiCdNm[syuruiCombo.getSelectedIndex()].code; // 薬剤種類１
		String med_kind_name = ""; // 薬剤種類
		// String med_kind2 = ""; // 薬剤種類２
		String med_kind2 = shukko_flag.toString(); // 薬剤種類２
		String med_kind3 = ""; // 薬剤種類３
		String pack_unit3 = konpoutniText.getText(); // 梱包単位
		String pack_unit2 = housoutniText.getText(); // 包装単位
		String pack_unit1 = baratniText.getText(); // バラ単位
		String unit_price = saisinntnkText.getText(); // 最新納入単価
		String discount = String.valueOf(Integer.parseInt(nebikirituText.getText())); // 単品値引率
		String unit_nm = unitText.getText(); // 単位名
		String med_price = yakkaText.getText(); // 薬価
		String hacchu_p = hacchuText.getText(); // 発注用P点

		ContItem[] itemArray = new ContItem[gyoushaStr.length];
		for (int i = 0; i < gyoushaStr.length; i++) {
			String cont_id = inGyoushaCdnm[i].getid(); // 業者ＩＤ
			ContItem itemTmp = new ContItem(cont_id, // 業者ＩＤ
			        orca_med_cd, // ＯＲＣＡ薬剤ＣＤ
			        item_no, // 品番
			        med_nm, // 薬剤名称
			        med_kn, // 薬剤名称カナ
			        med_kind1, // 薬剤種類１
			        med_kind_name, // 薬剤種類１名称
			        med_kind2, // 薬剤種類２
			        med_kind3, // 薬剤種類３
			        pack_unit3, // 梱包単位
			        pack_unit2, // 包装単位
			        pack_unit1, // バラ単位
			        unit_price, // 最新納入単価
			        discount, // 単品値引率
			        unit_nm, // 単位名
			        med_price, // 薬価
			        hacchu_p); // 発注用P点
			itemArray[i] = itemTmp;

		}
		ContItem item = itemArray[0];

		// 業者情報
		ContractorInItemMdl cItem[] = new ContractorInItemMdl[gyoushaStr.length];
		for (int i = 0; i < cItem.length; i++) {
			ContractorInItemMdl tmpItem = new ContractorInItemMdl(item_no, // 品番
			        Integer.toString(i + 1), // 番号
			        inGyoushaCdnm[i].getid()); // 業者ＩＤ
			cItem[i] = tmpItem;
		}

		BizContradrug biz = new BizContradrug();
		String status = null;
		if (imode == MODE_NEW) {
			// 新規登録
			status = biz.insCont_item(itemArray, false);
			if (status == "FOUND") {
				msgdlg.msgdsp("この品番は使用済みです。", MsgDlg.ERROR_MESSAGE);
				hinbanText.requestFocus();
			}
		} else if (imode == MODE_EXT) {
			// 修正登録：
			status = biz.isFoundItem(itemArray[0]);
			// biz.updtCont_item(itemArray, tmp_item_no, false) ;
			// 品番が登録済で、かつ旧品番と新品番が異なる場合
			if ((status == "FOUND")
			        && (item.item_no.equals(tmp_item_no) == false)) {
				msgdlg.msgdsp("この品番は使用済みです。", MsgDlg.ERROR_MESSAGE);
				hinbanText.requestFocus();
			} else {
				status = biz.delCont_item(item_no);
				status = biz.insCont_item(itemArray, false);
				// biz.updtCont_item(itemArray, tmp_item_no, false) ;
			}
		} else if (imode == MODE_DEL) {
			// 削除確認
			int msgsts = msgdlg.msgdsp("削除してよろしいですか？", MsgDlg.QUESTION_MESSAGE,
			        MsgDlg.YES_NO_OPTION);
			if (msgsts == 1)
				return;
			status = biz.delCont_item(item_no);
		} else if (imode == MODE_ORG_NEW) {
			// 独自薬剤・新規登録
			status = biz.insOrca_med(item);
			if (status == "NAME_FOUND") {
				msgdlg.msgdsp("この薬剤名は使用済みです。", MsgDlg.ERROR_MESSAGE);
				hinbanText.requestFocus();
			} else if (status == "HINBAN_FOUND") {
				msgdlg.msgdsp("この品番は使用済みです。", MsgDlg.ERROR_MESSAGE);
				hinbanText.requestFocus();
			} else {
				status = biz.insCont_item(itemArray, true);
				if (status == "FOUND") {
					msgdlg.msgdsp("この品番は使用済みです。", MsgDlg.ERROR_MESSAGE);
					hinbanText.requestFocus();
				}
			}
		} else if (imode == MODE_ORG_EXT) {
			// 独自薬剤・修正登録
			status = biz.updtOrca_med(item, tmp_item_no);
			if (status == "FOUND") {
				msgdlg.msgdsp("この品番は使用済みです。", MsgDlg.ERROR_MESSAGE);
				hinbanText.requestFocus();
			} else {
				status = biz.delCont_item(item_no);
				status = biz.insCont_item(itemArray, true);
			}
		} else if (imode == MODE_ORG_DEL) {
			// 独自薬剤・削除確認
			int msgsts = msgdlg.msgdsp("削除してよろしいですか？", MsgDlg.QUESTION_MESSAGE,
			        MsgDlg.YES_NO_OPTION);
			if (msgsts == 1)
				return;
			status = biz.delOrca_med(orca_med_cd);
			if (status == "OK") {
				status = biz.delCont_item(item_no);
			}
		}
		if (status == "OK") {
			inputItem_clear();
		}
		initList();
	}

	// 戻るボタン
	void cancelButton_actionPerformed(ActionEvent e) {
		dispose();
	}

	// マスタ印刷ボタン
	void printButton_actionPerformed(ActionEvent e) {
		StockListMedMaster list = new StockListMedMaster();
		list.start();
	}

	// 自動出庫選択コンボ
	void shukkoCombo_actionPerformed(ActionEvent e) {
		if (shukkoCombo.getSelectedIndex() == 1) {
			shukko_flag = "1";
		} else {
			shukko_flag = "0";
		}
	}

	// 短縮コード選択コンボ
	void tanshukuCombo_actionPerformed(ActionEvent e) {
		if (tanshukuCombo.getSelectedIndex() == 1) {
			// shukko_flag = "1" ;
		} else {
			// shukko_flag = "0" ;
		}
	}

	// 業者リスト追加ボタン '04.07.22 onuki
	void gyoushaAddButton_actionPerformed(ActionEvent e) {
		if (jList_master.isSelectionEmpty() == false) {
			int gyoushaLen = gyoushaStr.length;
			String gyoushaNew[] = new String[gyoushaLen + 1];
			String masterTmp = (String)jList_master.getSelectedValue();
			for (int i = 0; i < gyoushaLen; i++) {
				gyoushaNew[i] = gyoushaStr[i];
			}
			gyoushaNew[gyoushaLen] = masterTmp;

			gyoushaStr = orderGyoushaList(gyoushaNew);
			jList_gyousha.setListData(gyoushaStr);

			masterOutStr = this.changeMasterList();
			jList_master.setListData(masterOutStr);
		}
	}

	// 業者リスト削除ボタン '04.07.22 onuki
	void gyoushaDelButton_actionPerformed(ActionEvent e) {
		if (jList_gyousha.isSelectionEmpty() == false) {
			int gyoushaLen = gyoushaStr.length;
			String gyoushaNew[] = new String[gyoushaLen - 1];
			String gyoushaTmp = (String)jList_gyousha.getSelectedValue();
			int j = 0;
			for (int i = 0; i < gyoushaLen; i++) {
				if (gyoushaStr[i].equals(gyoushaTmp)) {
					i++;
				}
				if (i >= gyoushaLen)
					break;
				gyoushaNew[j] = gyoushaStr[i];
				j++;
			}
			gyoushaStr = gyoushaNew;
			jList_gyousha.setListData(gyoushaStr);

			masterOutStr = this.changeMasterList();
			jList_master.setListData(masterOutStr);
		}
	}

	// 入力項目クリア 04.04.01 onuki
	private void setItem_init(int[] isInputList) {

		boolean isBoolean[] = new boolean[isInputList.length];
		Color isColor[] = new Color[isInputList.length];
		for (int i = 0; i < isInputList.length; i++) {
			if (isInputList[i] == 0) {
				isBoolean[i] = true;
				isColor[i] = Color.white;
			} else {
				isBoolean[i] = false;
				isColor[i] = Color.lightGray;
			}
		}
		initList();

		jList_gyousha.setEnabled(isBoolean[1]); // 卸業者リスト
		jList_master.setEnabled(isBoolean[1]); // 全体業者リスト
		gyoushaAddButton.setEnabled(isBoolean[1]); // 業者追加ボタン
		gyoushaDelButton.setEnabled(isBoolean[1]); // 業者削除ボタン
		orcadrugcdText.setEnabled(false); // 日レセ薬剤CD
		hinbanText.setEnabled(isBoolean[0]); // 品番
		tanshukuCombo.setEnabled(isBoolean[4]); // 短縮コード
		nameText.setEnabled(isBoolean[0]); // 薬剤名称
		kananmText.setEnabled(isBoolean[0]); // 薬剤名称カナ
		konpoutniText.setEnabled(isBoolean[1]); // 梱包単位数
		housoutniText.setEnabled(isBoolean[1]); // 包装単位数
		saisinntnkText.setEnabled(isBoolean[2]); // 単価
		nebikirituText.setEnabled(isBoolean[1]); // 値引率
		syuruiCombo.setEnabled(isBoolean[1]); // 薬剤種類
		shukkoCombo.setEnabled(isBoolean[1]); // 自動出庫
		unitText.setEnabled(isBoolean[3]); // 単位
		yakkaText.setEnabled(isBoolean[3]); // 薬価
		hacchuText.setEnabled(isBoolean[2]); // 発注用P点

		jList_gyousha.setBackground(isColor[1]); // 卸業者リスト
		jList_master.setBackground(isColor[1]); // 全体業者リスト
		orcadrugcdText.setBackground(Color.lightGray); // 日レセ薬剤CD
		hinbanText.setBackground(isColor[0]); // 品番
		tanshukuCombo.setBackground(isColor[4]); // 短縮コード
		nameText.setBackground(isColor[0]); // 薬剤名称
		kananmText.setBackground(isColor[0]); // 薬剤名称カナ
		konpoutniText.setBackground(isColor[1]); // 梱包単位数
		housoutniText.setBackground(isColor[1]); // 包装単位数
		saisinntnkText.setBackground(isColor[2]); // 単価
		nebikirituText.setBackground(isColor[1]); // 値引率
		syuruiCombo.setBackground(isColor[1]); // 薬剤種類
		shukkoCombo.setBackground(isColor[1]); // 自動出庫
		unitText.setBackground(isColor[3]); // 単位
		yakkaText.setBackground(isColor[3]); // 薬価
		hacchuText.setBackground(isColor[2]); // 発注用P点

		konpoutniText.setText("0.000"); // 梱包数
		housoutniText.setText("0.000"); // 包装数
		baratniText.setText("1.000"); // バラ数単位
		nebikirituText.setText("0"); // 値引率
		tanshukuCombo.setSelectedIndex(-1);// 短縮コード
		shukkoCombo.setSelectedIndex(0);// 自動出庫フラグ
		hacchuText.setText("0.000"); // 発注用P点

	}

	// 入力項目クリア
	private void inputItem_clear() {// コードテキスト
		// 薬剤検索ボタン>独自薬剤登録以外：表示でtrue
		inputItem_clear(true);
	}

	// 入力項目クリア
	private void inputItem_clear(boolean isDrugSearch) {
		// 薬剤検索ボタン>独自薬剤登録：不可、それ以外：表示
		drugsearchButton.setEnabled(isDrugSearch);
		orcadrugcdText.setText(""); // 日レセ薬剤CD
		hinbanText.setText(""); // 品番
		nameText.setText("");
		kananmText.setText("");
		konpoutniText.setText("");
		housoutniText.setText("");
		saisinntnkText.setText("");
		nebikirituText.setText("");
		hacchuText.setText("");
		syuruiCombo.setSelectedIndex(-1);
		unitText.setText("");
		yakkaText.setText("");
		shukkoCombo.setSelectedIndex(-1);
		tanshukuButton.setEnabled(false);
		tanshukuReturnButton.setEnabled(false);
		tanshukuStr = null;
		tanshukuCombo.removeAllItems();
		// tanshukuCombo.setSelectedIndex(-1);
	}

	void drugsearchButton_actionPerformed(ActionEvent e) {
		MsgDlg msgdlg = new MsgDlg(this);
		ContradrugsearchDlg dlg = new ContradrugsearchDlg();
		dlg.setInitmode(imode, "1");
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
		if (dlg.getKakuteimode().equals("ok")) {
			if ((imode == MODE_NEW) || (imode == MODE_ORG_NEW)) {
				OrcaMedicine orcamedicine = (OrcaMedicine)dlg.getKakuteiDat(); // 日レセ薬剤データ
				new_data_set(orcamedicine.orca_med_cd);
			} else {
				ContItem contitem = (ContItem)dlg.getKakuteiDat(); // 業種別薬剤品目データ
				extant_data_set("NONE", contitem.item_no);
				// 修正前の品番を待避 04.04.05 onuki
				if ((imode == MODE_EXT) || (imode == MODE_ORG_EXT)) {
					tmp_item_no = contitem.item_no;
				}
			}
		}
	}

	void tanshukuButton_actionPerformed(ActionEvent e) {
		String tanshukuCD = (String)tanshukuCombo.getSelectedItem();
		// 短縮コードリストが空でない、かつ"未登録"でない場合
		if ((tanshukuCD != null) && (tanshukuCD.equals("未登録") == false)) {
			hinbanText.setText(tanshukuCD);
		}
		// }
	}

	void tanshukuReturnButton_actionPerformed(ActionEvent e) {
		hinbanText.setText(tanshukuReturnStr);
	}

	private int new_data_set(String orca_med_cd) { // 0:ok -1:データなし

		BizContradrug biz = new BizContradrug();
		OrcaMedicine orcamedicine = biz.getOrca_medicine(orca_med_cd);
		if (orcamedicine == null)
			return -1;
		orcadrugcdText.setText(orcamedicine.orca_med_cd); // 日レセ薬剤CD
		// 品番
		if (orcamedicine.item_no == "") {
			hinbanText.setText("");
			hinbanText.setEnabled(true);
			hinbanText.setBackground(Color.white);
			hinbanText.requestFocus();
		} else {
			hinbanText.setText(orcamedicine.item_no);
			hinbanText.setEnabled(false);
			hinbanText.setBackground(Color.lightGray);
			nameText.requestFocus();
		}
		nameText.setText(orcamedicine.med_nm);
		kananmText.setText(orcamedicine.med_kn);
		konpoutniText.setText("0.000");
		housoutniText.setText("0.000");
		baratniText.setText("1.000");
		saisinntnkText.setText("0");
		nebikirituText.setText("0");
		hacchuText.setText("0.000");
		syuruiCombo.setSelectedIndex(-1);
		for (int i = 0; i < syuruiCdNm.length; i++) {
			if (syuruiCdNm[i].orcacd.equals(orcamedicine.med_kind)) {
				syuruiCombo.setSelectedIndex(i);
				break;
			}
		}
		tanshukuCombo.removeAllItems();
		tanshukuCombo.setSelectedIndex(-1);
		tanshukuStr = biz.get_ORCADB_tanshuku(orcamedicine.orca_med_cd);
		if (tanshukuStr == null) {
			tanshukuCombo.addItem("未登録");
			tanshukuCombo.setEnabled(false);
		} else {
			for (int i = 0; i < tanshukuStr.length; i++) {
				tanshukuCombo.addItem(tanshukuStr[i]);
			}
			// 新規登録の場合は、短縮コードは参照のみで、登録させない
			// tanshukuButton.setEnabled(true);
			tanshukuCombo.setEnabled(true);
		}

		shukkoCombo.setSelectedIndex(0);
		unitText.setText(orcamedicine.unit_nm);
		yakkaText.setText(orcamedicine.med_price);
		return 0;
	}

	private int extant_data_set(String cont_id, String item_no) { // 0:ok
		// -1:データなし
		BizContradrug biz = new BizContradrug();
		ContItem contitem = biz.getCont_item(cont_id, item_no);

		// 業者リスト修正
		CodeName gyousyaCdNm[] = biz.getCodeName(item_no);
		gyoushaStr = this.outputList(gyousyaCdNm, jList_gyousha);
		masterOutStr = this.changeMasterList();
		jList_master.setListData(masterOutStr);

		if (contitem == null)
			return -1;
		// 独自薬剤登録時は日レセ薬剤XXX
		if (imode == MODE_ORG_NEW) {
			orcadrugcdText.setText("XXX"); // 日レセ薬剤CD
		} else {
			orcadrugcdText.setText(contitem.orca_med_cd); // 日レセ薬剤CD
		}
		hinbanText.setText(contitem.item_no); // 品番
		// 品番を修正可能にする 04.04.01 onuki
		if ((imode != MODE_DEL) && (imode != MODE_ORG_DEL)) {
			hinbanText.setEnabled(true);
			hinbanText.setBackground(Color.white);
			nameText.requestFocus();
		}
		nameText.setText(contitem.med_nm);
		kananmText.setText(contitem.med_kn);
		konpoutniText.setText(contitem.pack_unit3);
		housoutniText.setText(contitem.pack_unit2);
		baratniText.setText(contitem.pack_unit1);
		saisinntnkText.setText(contitem.unit_price);
		nebikirituText.setText(contitem.discount);
		syuruiCombo.setSelectedIndex(-1);
		hacchuText.setText(contitem.hacchu_p);
		for (int i = 0; i < syuruiCdNm.length; i++) {
			if (syuruiCdNm[i].code.equals(contitem.med_kind1)) {
				syuruiCombo.setSelectedIndex(i);
				break;
			}
		}
		// 自動出庫コンボ設定 '04.03.16 onuki
		if (contitem.med_kind2.equals("1")) {
			shukkoCombo.setSelectedIndex(1);
		} else {
			shukkoCombo.setSelectedIndex(0);
		}

		tanshukuCombo.removeAllItems();
		tanshukuCombo.setSelectedIndex(-1);
		String orcaMedCd = biz.get_orcaMedCd_itemNo(item_no);
		tanshukuStr = biz.get_ORCADB_tanshuku(orcaMedCd);
		if (tanshukuStr == null) {
			tanshukuCombo.addItem("未登録");
			tanshukuCombo.setEnabled(false);
		} else {
			for (int i = 0; i < tanshukuStr.length; i++) {
				tanshukuCombo.addItem(tanshukuStr[i]);
			}
			tanshukuButton.setEnabled(true);
			tanshukuReturnButton.setEnabled(true);
			tanshukuCombo.setEnabled(true);
			tanshukuReturnStr = hinbanText.getText();
		}

		// 薬価、単位を日レセ薬剤DBから取得 04.04.01 onuki
		OrcaMedicine orcamedicine = biz.getOrca_medicine(contitem.orca_med_cd);
		unitText.setText(orcamedicine.unit_nm);
		yakkaText.setText(orcamedicine.med_price);

		return 0;
	}

	// 全業者マスタのうち、選択業者の重複項目を削除する
	private String[] changeMasterList() {
		String[] outStr = new String[masterStr.length - gyoushaStr.length];

		int count = 0;
		// 総当たりで、選択業者と全業者を比較
		for (int i = 0; i < masterStr.length; i++) {
			boolean isEquals = false;
			for (int j = 0; j < gyoushaStr.length; j++) {
				// 業者名が一致したらループ脱出
				if (gyoushaStr[j].equals(masterStr[i])) {
					isEquals = true;
					break;
				}
			}
			// 業者名が一致しなかったら、出力文字列配列に含める
			if (isEquals == false) {
				outStr[count] = masterStr[i];
				count++;
			}
		}

		return outStr;
	}

	// 選択業者の並び順を、全業者マスタの並び順に準拠させる
	private String[] orderGyoushaList(String[] inStr) {
		boolean[] masterDummy = new boolean[masterStr.length];
		// 総当たりで、選択業者と全業者(ダミー)を比較
		for (int i = 0; i < masterStr.length; i++) {
			masterDummy[i] = false;
			for (int j = 0; j < inStr.length; j++) {
				// 業者名が一致したらループ脱出
				if (inStr[j].equals(masterStr[i])) {
					masterDummy[i] = true;
					break;
				}
			}
		}

		String[] outStr = new String[inStr.length];
		int j = 0;
		for (int i = 0; i < masterStr.length; i++) {
			while (masterDummy[i] == false)
				i++;
			if (i >= masterStr.length)
				break;
			outStr[j] = masterStr[i];
			j++;
			if (j >= inStr.length)
				break;
		}

		return outStr;
	}

	private void initList() {
		// 選択業者リストの初期化
		gyoushaStr = new String[0];
		jList_gyousha.setListData(gyoushaStr);
		// 全業者リストの初期化
		jList_master.setListData(masterStr);
	}

	private String[] outputList(CodeName[] cdNm, JList jList) {
		String[] str = new String[cdNm.length];
		if (cdNm != null) {
			str = this.cdNm2Str(cdNm);
			jList.setListData(str);
		}
		return str;
	}

	private String[] cdNm2Str(CodeName[] cdNm) {
		String[] str = new String[cdNm.length];
		if (cdNm != null) {
			for (int i = 0; i < cdNm.length; i++) {
				str[i] = cdNm[i].getName();
			}
		}
		return str;
	}
}