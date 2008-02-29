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

import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;


import drugstock.batch.Backup;
import drugstock.batch.MonthlyBatch;
import drugstock.batch.OrcaDrugImport;
import drugstock.batch.OrcaDrugImportInit;
import drugstock.batch.OrcaMedBatch;
import drugstock.batch.PrinterInit;
import drugstock.cmn.FocusTraversalPolicyOrder;
import drugstock.cmn.PropRead;
import drugstock.cmn.MsgDlg;
import drugstock.component.BButton;
import com.borland.jbcl.layout.XYConstraints;
import com.borland.jbcl.layout.XYLayout;
import drugstock.prt.DeadStockList;
import drugstock.prt.StockListDenpyo;
import drugstock.prt.StockListHacchu;
import drugstock.prt.StockListInvent;
import drugstock.prt.StockListJunbi;
import drugstock.prt.StockListKanja;
import drugstock.prt.StockListMedMaster;
import drugstock.prt.StockListSaeki;
import drugstock.prt.StockListZaiko;

/**
 * メニュー画面表示
 */

public class DrugStockPanel extends JPanel {

	private XYLayout xYLayout1 = new XYLayout();
	private TitledBorder titledBorder1;
	private TitledBorder titledBorder2;
	BButton OrcaUpdateButton1 = new BButton();
	BButton OrcaUpdateButton2 = new BButton();
	BButton OrcaUpdateButton4 = new BButton();
	private JLabel jLabel2 = new JLabel();
	BButton OrcaUpdateButton_hosp = new BButton();
	BButton OrcaUpdateButton_invent = new BButton();
	BButton PrinterInitButton = new BButton();
	//
	BButton jButton1 = new BButton();
	private JLabel jLabel3 = new JLabel();
	BButton ListButtonJunbi = new BButton();
	BButton drugImportButton = new BButton();
	BButton drugImportInitButton = new BButton();
	BButton drugSyuruiButton = new BButton();
	BButton ListButtonSaeki = new BButton();
	BButton ListButtonMedMaster = new BButton();
	private BButton ListButtonDead = new BButton();
	private BButton ListButtonHacchu = new BButton();
	BButton ListButtonKanja = new BButton();
	BButton ListButtonDenpyo = new BButton();
	BButton ListButtonZaiko = new BButton();
	BButton ListButtonInvent = new BButton();
	private Border border1;
	private JLabel jLabel4 = new JLabel();
	private JLabel jLabel5 = new JLabel();
	private JLabel jLabel6 = new JLabel();
	private JLabel jLabel7 = new JLabel();
	BButton OrcaUpdateButton7 = new BButton();
	BButton OrcaUpdateButton8 = new BButton();
	BButton OrcaUpdateButton9 = new BButton();
	private Border border2;
	private TitledBorder titledBorder3;
	private Border border3;
	private TitledBorder titledBorder4;
	private Border border4;
	private TitledBorder titledBorder5;
	private Border border5;
	private TitledBorder titledBorder6;

	public DrugStockPanel() {
	}

	public DrugStockPanel(JFrame frame) {
		super();
		try {
			jbInit(frame);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void jbInit(JFrame frame) throws Exception {

		// イベントが発生するコンポーネントにリスナを付けて、イベント管理をする
		DrugStockPanelEventListener eventListener = new DrugStockPanelEventListener();

		xYLayout1.setWidth(782);
		xYLayout1.setHeight(432);
		this.setLayout(xYLayout1);
		this.setToolTipText("");

		// 設定ファイルのメンテナンスモードフラグを取得
		PropRead prop = new PropRead();
		String maintenance_mode = prop.getProp("maintenance_mode");
		if ((maintenance_mode == null)
		        || (maintenance_mode.equals("1") == false)) {
			maintenance_mode = "0";
		}

		// メンテナンスモードの枠設定
		if (maintenance_mode.equals("1")) {
			titledBorder1 = new TitledBorder(BorderFactory.createEtchedBorder(
			        Color.red, new Color(165, 163, 151)), "初期設定");
		} else {
			titledBorder1 = new TitledBorder(BorderFactory.createEtchedBorder(
			        Color.white, new Color(165, 163, 151)), "基本設定");
		}

		titledBorder2 = new TitledBorder(BorderFactory.createEtchedBorder(
		        Color.white, new Color(165, 163, 151)), "帳票出力業務");
		border1 = BorderFactory.createBevelBorder(BevelBorder.RAISED,
		        Color.white, Color.white, new Color(148, 145, 140), new Color(
		                103, 101, 98));
		border2 = BorderFactory.createEtchedBorder(Color.white, new Color(165,
		        163, 151));
		titledBorder3 = new TitledBorder(border2, "入力業務");
		border3 = BorderFactory.createEtchedBorder(Color.white, new Color(165,
		        163, 151));
		titledBorder4 = new TitledBorder(border3, "月次業務");
		border4 = BorderFactory.createEtchedBorder(Color.white, new Color(165,
		        163, 151));
		titledBorder5 = new TitledBorder(border4, "期末業務");
		border5 = BorderFactory.createEtchedBorder(Color.white, new Color(165,
		        163, 151));
		titledBorder6 = new TitledBorder(border5, "バックアップ業務");
		jButton1.setFont(new Font("Dialog", 0, 16));
		jButton1.setText("終了");
		jButton1.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				jButton1_actionPerformed(e);
			}
		});
		jLabel2.setBorder(titledBorder1);
		jLabel2.setToolTipText("");
		jLabel2.setDisplayedMnemonic('0');
		jLabel2.setHorizontalAlignment(SwingConstants.LEFT);
		jLabel2.setHorizontalTextPosition(SwingConstants.LEFT);
		jLabel2.setVerticalAlignment(SwingConstants.TOP);

		// ///////////////////////////////

		OrcaUpdateButton1.setText("業者設定");
		OrcaUpdateButton1.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				OrcaUpdateButton1_actionPerformed(e);
			}
		});

		OrcaUpdateButton4.setFont(new Font("Dialog", 0, 16));
		OrcaUpdateButton4.setToolTipText("");
		OrcaUpdateButton4.setText("薬剤設定");
		OrcaUpdateButton4.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				OrcaUpdateButton4_actionPerformed(e);
			}
		});

		PrinterInitButton.setFont(new Font("Dialog", 0, 16));
		PrinterInitButton.setToolTipText("");
		PrinterInitButton.setText("プリンタ設定");
		PrinterInitButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				PrinterInitButton_actionPerformed(e);
			}
		});

		drugImportButton.setFont(new Font("Dialog", 0, 16));
		drugImportButton.setText("薬剤情報更新");
		drugImportButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				drugImportButton_actionPerformed(e);
			}
		});

		drugImportInitButton.setFont(new Font("Dialog", 0, 16));
		drugImportInitButton.setText("初期薬剤取込");
		drugImportInitButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				drugImportInitButton_actionPerformed(e);
			}
		});

		drugSyuruiButton.setFont(new Font("Dialog", 0, 16));
		drugSyuruiButton.setText("薬剤区分設定");
		drugSyuruiButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				drugSyuruiButton_actionPerformed(e);
			}
		});

		// メンテナンスモードの表示／非表示設定
		if (maintenance_mode.equals("1")) {
			OrcaUpdateButton4.setVisible(false);
			PrinterInitButton.setVisible(false);
			drugImportButton.setVisible(true);
			OrcaUpdateButton1.setVisible(true);
			drugSyuruiButton.setVisible(true);
			drugImportInitButton.setVisible(true);
		} else {
			OrcaUpdateButton4.setVisible(true);
			PrinterInitButton.setVisible(true);
			drugImportButton.setVisible(true);
			OrcaUpdateButton1.setVisible(false);
			drugSyuruiButton.setVisible(false);
			drugImportInitButton.setVisible(false);
		}

		// ///////////////////////////////
		OrcaUpdateButton1.setFont(new Font("Dialog", 0, 16));
		OrcaUpdateButton1.setToolTipText("");
		OrcaUpdateButton2.setFont(new Font("Dialog", 0, 16));
		OrcaUpdateButton2.setToolTipText("");
		OrcaUpdateButton2.setText("伝票入力");
		OrcaUpdateButton2.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				OrcaUpdateButton2_actionPerformed(e);
			}
		});

		OrcaUpdateButton_hosp.setFont(new Font("Dialog", 0, 16));
		OrcaUpdateButton_hosp.setToolTipText("");
		OrcaUpdateButton_hosp.setText("院内処理");
		OrcaUpdateButton_hosp.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				OrcaUpdateButton_hosp_actionPerformed(e);
			}
		});

		// ///////////////////////////////
		jLabel3.setVerticalAlignment(SwingConstants.TOP);
		jLabel3.setHorizontalTextPosition(SwingConstants.LEFT);
		jLabel3.setHorizontalAlignment(SwingConstants.LEFT);
		jLabel3.setDisplayedMnemonic('0');
		jLabel3.setToolTipText("");
		jLabel3.setBorder(titledBorder2);
		ListButtonJunbi.setText("棚卸準備一覧表");
		ListButtonJunbi.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				ListButtonJunbi_actionPerformed(e);
			}
		});
		ListButtonJunbi.setFont(new Font("Dialog", 0, 16));
		ListButtonJunbi.setToolTipText("");
		ListButtonSaeki.setFont(new Font("Dialog", 0, 16));
		ListButtonSaeki.setToolTipText("");
		ListButtonSaeki.setText("品目別差益高分析表");
		ListButtonSaeki.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				ListButtonSaeki_actionPerformed(e);
			}
		});
		// ListButtonMedMaster.setText("品目別使用量一覧表");
		ListButtonMedMaster.setText("薬剤マスタ一覧表");
		ListButtonMedMaster.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				ListButtonMedMaster_actionPerformed(e);
			}
		});
		ListButtonDead.setFont(new Font("Dialog", 0, 16));
		ListButtonDead.setToolTipText("");
		ListButtonDead.setText("デッドストックリスト");
		ListButtonDead.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				ListButtonDead_actionPerformed(e);
			}
		});
		ListButtonHacchu.setFont(new Font("Dialog", 0, 16));
		ListButtonHacchu.setToolTipText("");
		ListButtonHacchu.setText("薬剤発注リスト");
		ListButtonHacchu.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				ListButtonHacchu_actionPerformed(e);
			}
		});
		ListButtonMedMaster.setFont(new Font("Dialog", 0, 16));
		ListButtonMedMaster.setToolTipText("");
		ListButtonKanja.setFont(new Font("Dialog", 0, 16));
		ListButtonKanja.setToolTipText("");
		ListButtonKanja.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				ListButtonKanja_actionPerformed(e);
			}
		});
		ListButtonKanja.setText("指定品目使用患者一覧表");
		jLabel4.setVerticalAlignment(SwingConstants.TOP);
		jLabel4.setHorizontalTextPosition(SwingConstants.LEFT);
		jLabel4.setHorizontalAlignment(SwingConstants.LEFT);
		jLabel4.setDisplayedMnemonic('0');
		jLabel4.setToolTipText("");
		jLabel4.setBorder(titledBorder3);
		ListButtonDenpyo.setText("仕入先別伝票一覧表");
		ListButtonDenpyo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				ListButtonDenpyo_actionPerformed(e);
			}
		});
		ListButtonDenpyo.setFont(new Font("Dialog", 0, 16));
		ListButtonDenpyo.setToolTipText("");
		jLabel5.setVerticalAlignment(SwingConstants.TOP);
		jLabel5.setHorizontalTextPosition(SwingConstants.LEFT);
		jLabel5.setHorizontalAlignment(SwingConstants.LEFT);
		jLabel5.setDisplayedMnemonic('0');
		jLabel5.setToolTipText("");
		jLabel5.setBorder(titledBorder4);
		OrcaUpdateButton7.setText("薬剤使用量取込");
		OrcaUpdateButton7.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				OrcaUpdateButton7_actionPerformed(e);
			}
		});
		OrcaUpdateButton7.setFont(new Font("Dialog", 0, 16));
		OrcaUpdateButton7.setToolTipText("");
		jLabel6.setBorder(titledBorder5);
		jLabel6.setToolTipText("");
		jLabel6.setDisplayedMnemonic('0');
		jLabel6.setHorizontalAlignment(SwingConstants.LEFT);
		jLabel6.setHorizontalTextPosition(SwingConstants.LEFT);
		jLabel6.setVerticalAlignment(SwingConstants.TOP);
		OrcaUpdateButton8.setText("月次処理");
		OrcaUpdateButton8.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				OrcaUpdateButton8_actionPerformed(e);
			}
		});

		// 04.03.16 onuki
		OrcaUpdateButton_invent.setFont(new Font("Dialog", 0, 16));
		OrcaUpdateButton_invent.setToolTipText("");
		OrcaUpdateButton_invent.setText("棚卸処理");
		OrcaUpdateButton_invent.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				OrcaUpdateButton_invent_actionPerformed(e);
			}
		});

		OrcaUpdateButton8.setFont(new Font("Dialog", 0, 16));
		OrcaUpdateButton8.setToolTipText("");
		jLabel7.setVerticalAlignment(SwingConstants.TOP);
		jLabel7.setHorizontalTextPosition(SwingConstants.LEFT);
		jLabel7.setHorizontalAlignment(SwingConstants.LEFT);
		jLabel7.setDisplayedMnemonic('0');
		jLabel7.setToolTipText("");
		jLabel7.setBorder(titledBorder6);
		OrcaUpdateButton9.setText("データバックアップ");
		OrcaUpdateButton9.setFont(new Font("Dialog", 0, 16));
		OrcaUpdateButton9.setToolTipText("");
		OrcaUpdateButton9.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				OrcaUpdateButton9_actionPerformed(e);
			}
		});

		ListButtonZaiko.setFont(new Font("Dialog", 0, 16));
		ListButtonZaiko.setToolTipText("");
		ListButtonZaiko.setText("在庫一覧表");
		ListButtonZaiko.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				ListButtonZaiko_actionPerformed(e);
			}
		});
		// 棚卸一覧表 04.04.06 onuki
		ListButtonInvent.setFont(new Font("Dialog", 0, 16));
		ListButtonInvent.setToolTipText("");
		ListButtonInvent.setText("棚卸一覧表");
		ListButtonInvent.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				ListButtonInvent_actionPerformed(e);
			}
		});

		// メンテナンスモード
		this.add(OrcaUpdateButton1, new XYConstraints(21, 45, 224, -1)); // 「業者マスタ」ボタン
		this.add(drugSyuruiButton, new XYConstraints(21, 85, 224, -1));
		this.add(drugImportButton, new XYConstraints(21, 125, 224, -1));
		this.add(drugImportInitButton, new XYConstraints(21, 165, 224, -1));
		// 通常モード
		this.add(OrcaUpdateButton4, new XYConstraints(21, 45, 224, -1)); // 「業者別品目マスタ」ボタン
		this.add(PrinterInitButton, new XYConstraints(21, 85, 224, -1));
		//
		this.add(jLabel2, new XYConstraints(10, 20, 249, 205)); // マスタメンテナンスフレーム
		this.add(OrcaUpdateButton2, new XYConstraints(21, 280, 224, -1)); // 「伝票入力」ボタン
		this.add(OrcaUpdateButton_hosp, new XYConstraints(21, 330, 224, -1)); // 「院内処理」ボタン
		this.add(jLabel4, new XYConstraints(10, 243, 249, 165));// 入力業務フレーム

		this.add(ListButtonJunbi, new XYConstraints(280, 45, 224, -1));
		this.add(ListButtonSaeki, new XYConstraints(280, 85, 224, 33));
		this.add(ListButtonMedMaster, new XYConstraints(280, 125, 224, 33));
		this.add(ListButtonDead, new XYConstraints(280, 165, 224, 33));
		this.add(ListButtonHacchu, new XYConstraints(280, 205, 224, 33));
		this.add(ListButtonKanja, new XYConstraints(280, 245, 224, 33));
		this.add(ListButtonDenpyo, new XYConstraints(280, 285, 224, 33));
		this.add(ListButtonZaiko, new XYConstraints(279, 325, 224, 33));
		this.add(jLabel3, new XYConstraints(266, 20, 249, 389));

		this.add(OrcaUpdateButton7, new XYConstraints(532, 45, 224, -1));
		this.add(OrcaUpdateButton8, new XYConstraints(535, 100, 224, -1)); // 在庫マスタ集計
		this.add(jLabel5, new XYConstraints(520, 20, 249, 144)); // 日レセデータ取込業務フレーム
		this.add(OrcaUpdateButton_invent, new XYConstraints(535, 193, 224, -1)); // 棚卸入力
		this.add(ListButtonInvent, new XYConstraints(535, 248, 224, 33)); // 棚卸一覧表
		this.add(jLabel6, new XYConstraints(523, 164, 249, 150)); // 月次業務フレーム
		this.add(OrcaUpdateButton9, new XYConstraints(536, 353, 224, -1));
		this.add(jButton1, new XYConstraints(533, 419, 224, -1));
		this.add(jLabel7, new XYConstraints(523, 325, 249, 84));

		Component firstCom = OrcaUpdateButton4;
		// メンテナンスモードの非表示設定
		if (maintenance_mode.equals("1")) {
			firstCom = OrcaUpdateButton1;
		}

		Component order[] = new Component[] { firstCom, PrinterInitButton,
		        drugSyuruiButton, drugImportButton, drugImportInitButton,
		        OrcaUpdateButton2, OrcaUpdateButton_hosp, ListButtonJunbi,
		        ListButtonSaeki, ListButtonMedMaster, ListButtonDead,
		        ListButtonHacchu, ListButtonKanja, ListButtonDenpyo,
		        ListButtonZaiko, OrcaUpdateButton7, OrcaUpdateButton8,
		        OrcaUpdateButton_invent, ListButtonInvent, OrcaUpdateButton9,
		        jButton1 };
		FocusTraversalPolicyOrder policy = new FocusTraversalPolicyOrder(order);
		frame.setFocusTraversalPolicy(policy);

		Set hashSet = new HashSet();
		hashSet.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ENTER, 0));
		hashSet.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_TAB, 0));
		this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
		        hashSet);
		// hashSet.clear() ;
		// hashSet.add( AWTKeyStroke.getAWTKeyStroke( KeyEvent.VK_ENTER,
		// InputEvent.SHIFT_DOWN_MASK) );
		// hashSet.add( AWTKeyStroke.getAWTKeyStroke( KeyEvent.VK_TAB,
		// InputEvent.SHIFT_DOWN_MASK ) );
		// this.setFocusTraversalKeys(
		// KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, hashSet ) ;
	}

	/* 業者登録 */
	void OrcaUpdateButton1_actionPerformed(ActionEvent e) {
		ContractorDlg dlg = new ContractorDlg();
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
	}

	/* 業者別品目登録 */
	void OrcaUpdateButton4_actionPerformed(ActionEvent e) {
		ContradrugDlg dlg = new ContradrugDlg();
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
	}

	/**
	 * 日レセ薬剤の取り込み処理
	 */
	void drugImportButton_actionPerformed(ActionEvent e) {
		MsgDlg msgdlg = new MsgDlg();
		if (0 == msgdlg.msgdsp("日レセ薬剤を取り込み、薬剤情報を更新します。よろしいですか？",
		        MsgDlg.INFORMATION_MESSAGE, MsgDlg.YES_NO_OPTION)) {
			OrcaDrugImport imp = new OrcaDrugImport();
			imp.start();
		}
	}

	/**
	 * 薬剤区分の設定処理
	 */
	void drugSyuruiButton_actionPerformed(ActionEvent e) {
		DrugSyuruiDlg dlg = new DrugSyuruiDlg();
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
	}

	/**
	 * CSVファイルより、初期日レセ薬剤の取り込み処理
	 */
	void drugImportInitButton_actionPerformed(ActionEvent e) {
		OrcaDrugImportInit imp = new OrcaDrugImportInit();
		imp.start();
	}

	/* プリンタ設定 */
	void PrinterInitButton_actionPerformed(ActionEvent e) {
		PrinterInit bat = new PrinterInit();
		bat.start();
	}

	/* 棚卸準備一覧表 */
	void ListButtonJunbi_actionPerformed(ActionEvent e) {
		StockListJunbi list = new StockListJunbi();
		list.start();
	}

	/* 品目別差益高分析表 */
	void ListButtonSaeki_actionPerformed(ActionEvent e) {
		StockListSaeki list = new StockListSaeki();
		list.start();
	}

	/* 薬剤マスタ一覧 */
	void ListButtonMedMaster_actionPerformed(ActionEvent e) {
		StockListMedMaster list = new StockListMedMaster();
		list.start();
	}

	/* デッドストックリスト */
	void ListButtonDead_actionPerformed(ActionEvent e) {
		DeadStockList list = new DeadStockList();
		list.start();
	}

	/* 発注点未満 薬剤リスト */
	void ListButtonHacchu_actionPerformed(ActionEvent e) {
		StockListHacchu list = new StockListHacchu();
		list.start();
	}

	/* 指定品目使用患者一覧表 */
	void ListButtonKanja_actionPerformed(ActionEvent e) {
		StockListKanja list = new StockListKanja();
		list.start();
	}

	/* 仕入先別伝票一覧表 */
	void ListButtonDenpyo_actionPerformed(ActionEvent e) {
		StockListDenpyo list = new StockListDenpyo();
		list.start();
	}

	/**
	 * 在庫一覧表
	 */
	void ListButtonZaiko_actionPerformed(ActionEvent e) {
		StockListZaiko list = new StockListZaiko();
		list.start();
	}

	/**
	 * 棚卸一覧表 04.04.06 onuki
	 */
	void ListButtonInvent_actionPerformed(ActionEvent e) {
		StockListInvent list = new StockListInvent();
		list.start();
	}

	/**
	 * 日レセ診療データ取込
	 */
	void OrcaUpdateButton7_actionPerformed(ActionEvent e) {
		OrcaMedBatch bat = new OrcaMedBatch();
		bat.start();
	}

	/**
	 * 月次処理
	 */
	void OrcaUpdateButton8_actionPerformed(ActionEvent e) {
		MonthlyBatch bat = new MonthlyBatch();
		bat.start();
	}

	/**
	 * バックアップ処理
	 */
	void OrcaUpdateButton9_actionPerformed(ActionEvent e) {
		MsgDlg msgdlg = new MsgDlg();
		if (0 == msgdlg.msgdsp("バックアップを開始します。よろしいですか？",
		        MsgDlg.INFORMATION_MESSAGE, MsgDlg.YES_NO_OPTION)) {
			Backup imp = new Backup();
			imp.start();
		}
	}

	/**
	 * 仕入れ
	 */
	void OrcaUpdateButton2_actionPerformed(ActionEvent e) {
		StocinputDlg dlg = new StocinputDlg();
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
	}

	/**
	 * 院内処理
	 */
	void OrcaUpdateButton_hosp_actionPerformed(ActionEvent e) {
		HospProcDlg dlg = new HospProcDlg();
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
	}

	/**
	 * 棚卸処理
	 */
	void OrcaUpdateButton_invent_actionPerformed(ActionEvent e) {
		InventStockDlg dlg = new InventStockDlg();
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
	}

	/**
	 * 終了
	 */
	void jButton1_actionPerformed(ActionEvent e) {
		System.exit(0);
	}

	/**
	 * イベントリスナ定義
	 */
	class DrugStockPanelEventListener implements ActionListener, KeyListener {

		/**
		 * イベントリスナコンストラクタ
		 */
		public DrugStockPanelEventListener() {
		}

		/**
		 * アクションイベント取得
		 * 
		 * @param event
		 *            イベント
		 */
		public void actionPerformed(ActionEvent event) {
		}

		/**
		 * キータイプイベントの取得
		 * 
		 * @param event
		 *            イベント
		 */
		public void keyTyped(KeyEvent event) {
		}

		/**
		 * キープレスイベントの取得
		 * 
		 * @param event
		 *            イベント
		 */
		public void keyPressed(KeyEvent event) {
			Object eventObject = event.getSource();

			if (event.getKeyCode() == KeyEvent.VK_ENTER) {
				if (eventObject == OrcaUpdateButton1) {
					OrcaUpdateButton4.requestFocus();
				} else if (eventObject == OrcaUpdateButton4) {
					PrinterInitButton.requestFocus();
				} else if (eventObject == PrinterInitButton) {
					drugImportButton.requestFocus();
				} else if (eventObject == drugImportButton) {
					drugSyuruiButton.requestFocus();
				} else if (eventObject == drugSyuruiButton) {
					drugImportInitButton.requestFocus();
				} else if (eventObject == drugImportInitButton) {
					OrcaUpdateButton2.requestFocus();
				} else if (eventObject == OrcaUpdateButton2) {
					OrcaUpdateButton_hosp.requestFocus();
				} else if (eventObject == OrcaUpdateButton_hosp) {
					//
					ListButtonJunbi.requestFocus();
				} else if (eventObject == ListButtonJunbi) {
					ListButtonSaeki.requestFocus();
				} else if (eventObject == ListButtonSaeki) {
					ListButtonMedMaster.requestFocus();
				} else if (eventObject == ListButtonMedMaster) {
					ListButtonKanja.requestFocus();
				} else if (eventObject == ListButtonKanja) {
					ListButtonDenpyo.requestFocus();
				} else if (eventObject == ListButtonDenpyo) {
					ListButtonZaiko.requestFocus();
				} else if (eventObject == ListButtonZaiko) {
					OrcaUpdateButton7.requestFocus();
				} else if (eventObject == OrcaUpdateButton7) {
					OrcaUpdateButton8.requestFocus();
				} else if (eventObject == OrcaUpdateButton8) {
					// 04.03.18 onuki
					OrcaUpdateButton_invent.requestFocus();
				} else if (eventObject == OrcaUpdateButton_invent) {
					ListButtonInvent.requestFocus();
				} else if (eventObject == ListButtonInvent) {
					OrcaUpdateButton9.requestFocus();
				} else if (eventObject == OrcaUpdateButton9) {
					jButton1.requestFocus();
				} else if (eventObject == jButton1) {
					OrcaUpdateButton1.requestFocus();
				}
			}
		}

		/**
		 * キーリリースイベントの取得
		 * 
		 * @param event
		 *            イベント
		 */
		public void keyReleased(KeyEvent event) {
		}
	};
}