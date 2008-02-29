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
import java.awt.event.WindowEvent;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;


import drugstock.biz.BizContractor;
import drugstock.cmn.Common;
import drugstock.cmn.DefaultJDialog;
import drugstock.cmn.FocusTraversalPolicyOrder;
import drugstock.cmn.PropRead;
import drugstock.cmn.MsgDlg;
import drugstock.component.BButton;
import drugstock.display.DispDeadStockList;
import drugstock.display.DispStockListDenpyoDetail;
import drugstock.display.DispStockListDenpyoSum;
import drugstock.display.DispStockListHacchu;
import drugstock.display.DispStockListInvent;
import drugstock.display.DispStockListJunbi;
import drugstock.display.DispStockListKanja;
import drugstock.display.DispStockListMedMaster;
import drugstock.display.DispStockListSaeki;
import drugstock.display.DispStockListSum;
import drugstock.display.DispStockListZaiko;
import com.borland.jbcl.layout.XYConstraints;
import com.borland.jbcl.layout.XYLayout;
import drugstock.model.CodeName;
import drugstock.model.DeadStockListMdl;
import drugstock.model.StockListDenpyoDetailMdl;
import drugstock.model.StockListDenpyoSumMdl;
import drugstock.model.StockListHacchuMdl;
import drugstock.model.StockListInventMdl;
import drugstock.model.StockListJunbiMdl;
import drugstock.model.StockListKanjaMdl;
import drugstock.model.StockListMedMasterMdl;
import drugstock.model.StockListSaekiMdl;
import drugstock.model.StockListSumMdl;
import drugstock.model.StockListZaikoMdl;

/**
 * 各種帳票データ表示／画面処理
 */

public class DispTableDlg extends DefaultJDialog {

	// パネルが開かれた時に最初にフォーカスを当てるコンポーネント
	protected Component firstFocusComponent = null;
	Common com = new Common();

	// 画面入力用ワークエリア
	private boolean errorMsg = true;

	// 検索フラグ
	private boolean bSearch = false;
	// 明細フラグ
	private boolean bMeisai = false;

	private boolean gyousyaCombo_busy = false; // 業者選択コンボ アイテム設定中
	private boolean syuruiCombo_busy = false; // 薬剤種類コンボ アイテム設定中
	private CodeName gyousyaCdNm[] = null; // 業者コンボのコード、ネーム
	// public SyuruiCdNm syuruiCdNm[] = null; // 薬剤種類コンボのコード、ネーム
	private Vector vStoclist = new Vector(); // 仕入明細
	private int edit_point; // 編集中の明細ポイント
	private String stock_unit_price_calc; // 単価計算方法 0：単価入力、1：割戻し計算

	private XYLayout xYLayout2 = new XYLayout();
	private ButtonGroup taxGrp = new ButtonGroup();
	private BButton csvButton = new BButton();
	private BButton okButton = new BButton();
	private BButton cancelButton = new BButton();
	private TitledBorder titledBorder1;
	private TitledBorder titledBorder2;
	private JComboBox gyousyaCombo = new JComboBox();
	private JComboBox syuruiCombo = new JComboBox();
	private BButton searchButton = new BButton();
	private JTextField syuruicdText = new JTextField();

	private BButton clerButton = new BButton();
	private JLabel jLabel1 = new JLabel();
	private JLabel jLabel5 = new JLabel();
	// private JLabel jLabel8 = new JLabel();
	// private JLabel jLabel17 = new JLabel();
	private JLabel jLabelSaeki = new JLabel();
	private JLabel jLabelPeriod = new JLabel();
	private JLabel jLabelDead = new JLabel();
	private JLabel jLabelKanjaJoken = new JLabel();
	private JLabel jLabelKanja0 = new JLabel();
	private JLabel jLabelKanja1 = new JLabel();
	private JLabel jLabelKanja2 = new JLabel();
	private JLabel jLabelKanja3 = new JLabel();
	private JLabel jLabelKanja4 = new JLabel();
	// private JTextField yyyyText = new JTextField();
	// private JTextField mmText = new JTextField();
	private ButtonGroup modeGrp = new ButtonGroup(); // 並び順グループ
	private JRadioButton modeRadioSmall = new JRadioButton(); // 小さい順 ラジオボタン
	private JRadioButton modeRadioBig = new JRadioButton(); // 大きい順 ラジオボタン

	private DefaultTableModel schDefaultTableModel = new DefaultTableModel() {

		public boolean isCellEditable(int row, int column) {
			return false; // 入力不可
		}
	};

	private JTable schTable = new JTable(schDefaultTableModel);
	private JScrollPane schScrPane = new JScrollPane(schTable);

	private String dateStr = null;
	private String fromDateStr = null;
	private String toDateStr = null;
	private String deadDateStr = null;
	private String[] column_head = null;
	private int[] column_width = null;

	private String printFlg = "nop";
	private boolean isOrderSmall = true;
	private int flgPrt = 0;

	private String med_code_name_0 = null;
	private String med_code_name_1 = null;
	private String med_code_name_2 = null;
	private String med_code_name_3 = null;
	private String med_code_name_4 = null;

	// 薬剤印刷モード 04.04.05 onuki
	public static int PRT_JUNBI = 1; // 棚卸準備一覧表
	public static int PRT_SAEKI = 2; // 差益高分析表
	public static int PRT_DEAD_STOCK = 3; // デッドストックリスト
	public static int PRT_HACCHU = 4; // 発注薬剤一覧
	public static int PRT_KANJA = 5; // 患者別使用量一覧
	public static int PRT_DENPYO = 6; // 伝票一覧
	public static int PRT_DENPYO_DETAIL = 61; // 伝票一覧
	public static int PRT_DENPYO_SUM = 62; // 伝票一覧
	public static int PRT_ZAIKO = 8; // 在庫一覧表
	public static int PRT_INVENT = 9; // 棚卸一覧表
	public static int PRT_MED_MASTER = 10; // 薬剤マスタ一覧表
	public static int PRT_SUM = 20; // 薬剤マスタ一覧表
	public static int PRT_SHIYO = 99; // 使用高分析表

	// 移動 04.02.09 onuki
	// 小数点以下の処理変数をファイルから読み込み
	PropRead prop = new PropRead();
	String down_to_decimal = prop.getProp("down_to_decimal");

	//

	public DispTableDlg(Frame frame, String title, boolean modal) {
		super(frame, title, modal);
	}

	public DispTableDlg() {
		this(null, "印刷データ表示", false);
	}

	public DispTableDlg(Hashtable ht, String[] inColumnHead,
	        int[] inColumnWidth, int inFlgPrt) {
		this(null, "印刷データ表示", false);
		dateStr = (String)ht.get("nengetu");
		fromDateStr = (String)ht.get("fromDate");
		toDateStr = (String)ht.get("toDate");
		deadDateStr = (String)ht.get("deadDate");
		med_code_name_0 = (String)ht.get("med_code_name_0");
		med_code_name_1 = (String)ht.get("med_code_name_1");
		med_code_name_2 = (String)ht.get("med_code_name_2");
		med_code_name_3 = (String)ht.get("med_code_name_3");
		med_code_name_4 = (String)ht.get("med_code_name_4");
		column_head = inColumnHead;
		column_width = inColumnWidth;
		flgPrt = inFlgPrt;

		try {
			jbInit();
			pack();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void jbInit() throws Exception {
		// 単価、金額算出プロパティ取得
		// PropRead prop = new PropRead();
		stock_unit_price_calc = prop.getProp("stock_unit_price_calc");
		if (stock_unit_price_calc == null)
			stock_unit_price_calc = "0";
		if (stock_unit_price_calc.equals("1") == false)
			stock_unit_price_calc = "0";

		titledBorder1 = new TitledBorder(BorderFactory.createEtchedBorder(
		        Color.white, new Color(148, 145, 140)), "処理区分");
		titledBorder2 = new TitledBorder(BorderFactory.createEtchedBorder(
		        Color.white, new Color(148, 145, 140)), "");

		searchButton.setFont(new Font("Dialog", 0, 16));
		searchButton.setActionCommand("決定");
		searchButton.setText("決定");
		searchButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				searchButton_actionPerformed(e);
			}
		});

		// CSV出力ボタン
		csvButton.setFont(new Font("Dialog", 0, 16));
		csvButton.setText("CSV出力");
		csvButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				csvButton_actionPerformed(e);
			}
		});
		// 印刷ボタン
		okButton.setFont(new Font("Dialog", 0, 16));
		okButton.setText("帳票印刷");
		okButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				okButton_actionPerformed(e);
			}
		});
		// 戻るボタン
		cancelButton.setFont(new Font("Dialog", 0, 16));
		cancelButton.setText("戻る");
		cancelButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				cancelButton_actionPerformed(e);
			}
		});
		int vPlus = 35;
		int vMinus = 0;
		if (flgPrt == PRT_KANJA) {
			vPlus = 110;
			vMinus = 75;
		}
		this.getContentPane().setLayout(xYLayout2);
		xYLayout2.setWidth(1024);
		xYLayout2.setHeight(590 + vPlus);
		syuruiCombo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				syuruiCombo_actionPerformed(e);
			}
		});
		jLabel1.setFont(new Font("Dialog", 0, 16));
		jLabel1.setText("並び順");

		// int[] column_width = {20,20,250,40,40,5,40};
		schDefaultTableModel.setColumnIdentifiers(column_head);
		for (int i = 0; i < column_width.length; i++) {
			schTable.getColumnModel().getColumn(i).setPreferredWidth(
			        column_width[i]);
		}
		schTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		schTable.setFont(new Font("Dialog", 0, 16));
		schTable.setToolTipText("");
		schScrPane.setFont(new Font("Dialog", 0, 16));
		schTable.getTableHeader().setFont(new Font("Dialog", 0, 16));
		schTable.setToolTipText("");

		// 日付テキスト年
		dateStr = Common.getWareki2(Integer.parseInt(dateStr.substring(0, 4)))
		        + "年" + dateStr.substring(4, 6) + "月";
		jLabel5.setFont(new Font("Dialog", 0, 16));
		jLabel5.setText("表示年月：" + dateStr);

		// デッドストック基準年月
		if (deadDateStr == null)
			deadDateStr = "20000000";
		deadDateStr = Common.getWareki2(Integer.parseInt(deadDateStr.substring(
		        0, 4)))
		        + "年"
		        + deadDateStr.substring(4, 6)
		        + "月"
		        + deadDateStr.substring(6, 8) + "日";
		jLabelDead.setFont(new Font("Dialog", 0, 16));
		jLabelDead.setText(deadDateStr + "以降に出庫のない薬剤を表示");
		jLabelDead.setVisible(false);
		if (flgPrt == PRT_DEAD_STOCK) {
			jLabelDead.setVisible(true);
		}

		gyousyaCombo.setFont(new Font("Dialog", 0, 16));
		syuruiCombo.setFont(new Font("Dialog", 0, 16));
		// クリアボタン
		clerButton.setText("取消");
		clerButton.setFont(new Font("Dialog", 0, 16));
		clerButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				clerButton_actionPerformed(e);
			}
		});

		jLabelKanjaJoken.setText("条件");
		jLabelKanja0.setText("薬剤１：" + med_code_name_0);
		jLabelKanja1.setText("薬剤２：" + med_code_name_1);
		jLabelKanja2.setText("薬剤３：" + med_code_name_2);
		jLabelKanja3.setText("薬剤４：" + med_code_name_3);
		jLabelKanja4.setText("薬剤５：" + med_code_name_4);
		jLabelKanjaJoken.setFont(new Font("Dialog", 0, 16));
		jLabelKanja0.setFont(new Font("Dialog", 0, 16));
		jLabelKanja1.setFont(new Font("Dialog", 0, 16));
		jLabelKanja2.setFont(new Font("Dialog", 0, 16));
		jLabelKanja3.setFont(new Font("Dialog", 0, 16));
		jLabelKanja4.setFont(new Font("Dialog", 0, 16));
		jLabelKanjaJoken.setVisible(false);
		jLabelKanja0.setVisible(false);
		jLabelKanja1.setVisible(false);
		jLabelKanja2.setVisible(false);
		jLabelKanja3.setVisible(false);
		jLabelKanja4.setVisible(false);
		if (flgPrt == PRT_KANJA) {
			jLabelKanjaJoken.setVisible(true);
			jLabelKanja0.setVisible(true);
			jLabelKanja1.setVisible(true);
			jLabelKanja2.setVisible(true);
			jLabelKanja3.setVisible(true);
			jLabelKanja4.setVisible(true);
		}

		jLabelSaeki.setText("A：使用高順位，B：差益高順位");
		jLabelSaeki.setFont(new Font("Dialog", 0, 16));
		jLabelSaeki.setVisible(false);
		if (flgPrt == PRT_SAEKI) {
			jLabelSaeki.setVisible(true);
		}

		jLabelPeriod.setText("期間：" + fromDateStr + "〜" + toDateStr);
		jLabelPeriod.setFont(new Font("Dialog", 0, 16));
		jLabelPeriod.setVisible(false);
		if ((flgPrt == PRT_DENPYO_DETAIL) || (flgPrt == PRT_DENPYO_SUM)) {
			jLabelPeriod.setVisible(true);
			jLabel5.setVisible(false);
		}

		// 並び順 ラジオボタン
		modeGrp.add(modeRadioSmall);
		modeGrp.add(modeRadioBig);
		modeRadioSmall.setFont(new Font("Dialog", 0, 16));
		modeRadioSmall.setText("小さい順");
		modeRadioSmall.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				modeRadioSmall_actionPerformed(e);
			}
		});
		modeRadioBig.setFont(new Font("Dialog", 0, 16));
		modeRadioBig.setText("大きい順");
		modeRadioBig.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				modeRadioBig_actionPerformed(e);
			}
		});

		// 業者、日付
		this.getContentPane().add(jLabel1, new XYConstraints(38, 19, 80, -1));
		this.getContentPane().add(syuruiCombo,
		        new XYConstraints(110, 17, 120, 23));
		this.getContentPane().add(jLabel5, new XYConstraints(323, 19, 300, -1));
		this.getContentPane().add(modeRadioSmall,
		        new XYConstraints(40, 50, 100, -1));
		this.getContentPane().add(modeRadioBig,
		        new XYConstraints(150, 50, 100, -1));
		this.getContentPane().add(jLabelKanjaJoken,
		        new XYConstraints(323, 45, 100, 25));
		this.getContentPane().add(jLabelPeriod,
		        new XYConstraints(300, 45, 400, 25));
		this.getContentPane().add(jLabelDead,
		        new XYConstraints(300, 45, 600, 25));
		this.getContentPane().add(jLabelKanja0,
		        new XYConstraints(402, 45, 400, 25));
		this.getContentPane().add(jLabelKanja1,
		        new XYConstraints(402, 65, 400, 25));
		this.getContentPane().add(jLabelKanja2,
		        new XYConstraints(402, 85, 400, 25));
		this.getContentPane().add(jLabelKanja3,
		        new XYConstraints(402, 105, 400, 25));
		this.getContentPane().add(jLabelKanja4,
		        new XYConstraints(402, 125, 400, 25));
		// ボタン１
		this.getContentPane().add(searchButton,
		        new XYConstraints(750, 10, 97, 38));
		this.getContentPane().add(clerButton,
		        new XYConstraints(860, 10, 97, 38));
		// 表
		this.getContentPane().add(schScrPane,
		        new XYConstraints(10, 56 + vPlus, 1000, 480));
		// ボタン２
		this.getContentPane().add(csvButton,
		        new XYConstraints(620, 540 + vPlus, 110, 38));
		this.getContentPane().add(okButton,
		        new XYConstraints(740, 540 + vPlus, 110, 38));
		this.getContentPane().add(cancelButton,
		        new XYConstraints(860, 540 + vPlus, 90, 38));
		this.getContentPane().add(jLabelSaeki,
		        new XYConstraints(38, 540 + vPlus, 300, -1));

		// Tab フォーカス移動制御
		Component order[] = new Component[] { syuruiCombo, searchButton,
		        clerButton, csvButton, okButton, cancelButton };
		FocusTraversalPolicyOrder policy = new FocusTraversalPolicyOrder(order);
		super.setFocusTraversalPolicy(policy);

		// 業者コンボセット
		gyousyaCombo.setEditable(false);
		gyousyaCombo_busy = true;
		BizContractor biz = new BizContractor();
		gyousyaCdNm = biz.getCodeName();
		gyousyaCombo.removeAllItems();
		if (gyousyaCdNm != null) {
			for (int i = 0; i < gyousyaCdNm.length; i++) {
				gyousyaCombo.addItem(gyousyaCdNm[i].getName());
			}
		}
		gyousyaCombo_busy = false;

		// 薬剤種類コンボセット
		syuruiCombo.setEditable(false);
		syuruiCombo_busy = true;
		syuruiCombo.removeAllItems();
		for (int i = 0; i < column_head.length; i++) {
			syuruiCombo.addItem(column_head[i]);
		}

		firstFocusComponent = syuruiCombo;
		initProcess();
	}

	/**
	 * ウィンドウが開かれたときのイベントをオーバーライドします。
	 */
	protected void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_OPENED) {
		}
	}

	// 印刷ボタン
	void okButton_actionPerformed(ActionEvent e) {
		// 戻り確認；表の行数が０以外ならば確認画面表示
		if (schTable.getRowCount() != 0) {
			MsgDlg msgdlg = new MsgDlg(this);
			int msgsts = msgdlg.msgdsp("表示内容を印刷します。", MsgDlg.QUESTION_MESSAGE,
			        MsgDlg.YES_NO_OPTION);
			if (msgsts == 1) {
				return;
			} else {
				printFlg = "print"; // 印刷する
			}
		}
		dispose();
	}

	// CSV出力ボタン
	void csvButton_actionPerformed(ActionEvent e) {
		// 戻り確認；表の行数が０以外ならば確認画面表示
		if (schTable.getRowCount() != 0) {
			MsgDlg msgdlg = new MsgDlg(this);
			int msgsts = msgdlg.msgdsp("表示内容をCSV出力します。",
			        MsgDlg.QUESTION_MESSAGE, MsgDlg.YES_NO_OPTION);
			if (msgsts == 1) {
				return;
			} else {
				printFlg = "csv"; // 印刷する
			}
		}
		dispose();
	}

	// 戻るボタン
	void cancelButton_actionPerformed(ActionEvent e) {
		// 戻り確認；表の行数が０以外ならば確認画面表示
		if (schTable.getRowCount() != 0) {
			MsgDlg msgdlg = new MsgDlg(this);
			int msgsts = msgdlg.msgdsp("印刷せずに、メニューに戻ります。よろしいですか？",
			        MsgDlg.QUESTION_MESSAGE, MsgDlg.YES_NO_OPTION);
			if (msgsts == 1) {
				return;
			} else {
				printFlg = "nop"; // 印刷しない
			}
		}
		dispose();
	}

	// 種類コンボセット
	void syuruiCombo_actionPerformed(ActionEvent e) {
		if (syuruiCombo_busy == true)
			return;
		int i = syuruiCombo.getSelectedIndex();
	}

	// 小さい順ラジオボタン
	void modeRadioSmall_actionPerformed(ActionEvent e) {
		isOrderSmall = true;
	}

	// 大きい順ラジオボタン
	void modeRadioBig_actionPerformed(ActionEvent e) {
		isOrderSmall = false;
	}

	void schTable_actionPerformed(ActionEvent e) {
	}

	// 棚卸準備一覧表
	private StockListJunbiMdl[] itemJunbi = null;

	public void setItemJunbi(StockListJunbiMdl[] inItem) {
		itemJunbi = inItem;
	}

	public StockListJunbiMdl[] getItemJunbi() {
		return itemJunbi;
	}

	// 差益高分析表
	private StockListSaekiMdl[] itemSaeki = null;

	public void setItemSaeki(StockListSaekiMdl[] inItem) {
		itemSaeki = inItem;
	}

	public StockListSaekiMdl[] getItemSaeki() {
		return itemSaeki;
	}

	// デッドストックリスト
	private DeadStockListMdl[] itemDead = null;

	public void setItemDead(DeadStockListMdl[] inItem) {
		itemDead = inItem;
	}

	public DeadStockListMdl[] getItemDead() {
		return itemDead;
	}

	// 発注薬剤一覧表
	private StockListHacchuMdl[] itemHacchu = null;

	public void setItemHacchu(StockListHacchuMdl[] inItem) {
		itemHacchu = inItem;
	}

	public StockListHacchuMdl[] getItemHacchu() {
		return itemHacchu;
	}

	// 使用患者一覧表
	private StockListKanjaMdl[] itemKanja = null;

	public void setItemKanja(StockListKanjaMdl[] inItem) {
		itemKanja = inItem;
	}

	public StockListKanjaMdl[] getItemKanja() {
		return itemKanja;
	}

	// 伝票一覧表
	private StockListDenpyoDetailMdl[] itemDenpyoDetail = null;

	public void setItemDenpyoDetail(StockListDenpyoDetailMdl[] inItem) {
		itemDenpyoDetail = inItem;
	}

	public StockListDenpyoDetailMdl[] getItemDenpyoDetail() {
		return itemDenpyoDetail;
	}

	// 伝票一覧表・合計
	private StockListDenpyoSumMdl[] itemDenpyoSum = null;

	public void setItemDenpyoSum(StockListDenpyoSumMdl[] inItem) {
		itemDenpyoSum = inItem;
	}

	public StockListDenpyoSumMdl[] getItemDenpyoSum() {
		return itemDenpyoSum;
	}

	// 在庫一覧表
	private StockListZaikoMdl[] itemZaiko = null;

	public void setItemZaiko(StockListZaikoMdl[] inItem) {
		itemZaiko = inItem;
	}

	public StockListZaikoMdl[] getItemZaiko() {
		return itemZaiko;
	}

	// 棚卸一覧表
	private StockListInventMdl[] itemInvent = null;

	public void setItemInvent(StockListInventMdl[] inItem) {
		itemInvent = inItem;
	}

	public StockListInventMdl[] getItemInvent() {
		return itemInvent;
	}

	// 薬剤マスタ
	private StockListMedMasterMdl[] itemMedMaster = null;

	public void setItemMedMaster(StockListMedMasterMdl[] inItem) {
		itemMedMaster = inItem;
	}

	public StockListMedMasterMdl[] getItemMedMaster() {
		return itemMedMaster;
	}

	// 合計帳票
	private StockListSumMdl[] itemSum = null;

	public void setItemSum(StockListSumMdl[] inItem) {
		itemSum = inItem;
	}

	public StockListSumMdl[] getItemSum() {
		return itemSum;
	}

	// 決定ボタン：印刷データテーブル表示
	void searchButton_actionPerformed(ActionEvent e) {
		syuruiCombo.setEnabled(false);
		modeRadioSmall.setEnabled(false);
		modeRadioBig.setEnabled(false);
		// yyyyText.setEnabled(false);
		// mmText.setEnabled(false);
		searchButton.setEnabled(true);
		clerButton.setEnabled(true);
		okButton.setEnabled(true);
		csvButton.setEnabled(true);

		int comboSelect = syuruiCombo.getSelectedIndex();
		// System.out.println(column_head[comboSelect]) ;

		int cellwidth[] = new int[50];
		for (int i = 0; i < schTable.getColumnCount(); ++i) {
			cellwidth[i] = schTable.getColumnModel().getColumn(i).getPreferredWidth();
		}

		Vector vcolum = new Vector();
		Vector vdata = new Vector();
		for (int i = 0; i < schTable.getColumnCount(); i++) {
			vcolum.addElement(schTable.getColumnName(i));
		}

		// データ制御
		DispStockListJunbi dslJunbi = new DispStockListJunbi();
		DispStockListSaeki dslSaeki = new DispStockListSaeki();
		DispDeadStockList dslDead = new DispDeadStockList();
		DispStockListHacchu dslHacchu = new DispStockListHacchu();
		DispStockListKanja dslKanja = new DispStockListKanja();
		DispStockListDenpyoDetail dslDenpyoDetail = new DispStockListDenpyoDetail();
		DispStockListDenpyoSum dslDenpyoSum = new DispStockListDenpyoSum();
		DispStockListZaiko dslZaiko = new DispStockListZaiko();
		DispStockListInvent dslInvent = new DispStockListInvent();
		DispStockListMedMaster dslMedMaster = new DispStockListMedMaster();
		DispStockListSum dslSum = new DispStockListSum();

		if (flgPrt == PRT_JUNBI) {
			itemJunbi = dslJunbi.getTmpItem(itemJunbi, isOrderSmall,
			        comboSelect);
			vdata = dslJunbi.getVdata(itemJunbi);
		} else if (flgPrt == PRT_SAEKI) {
			itemSaeki = dslSaeki.getTmpItem(itemSaeki, isOrderSmall,
			        comboSelect);
			vdata = dslSaeki.getVdata(itemSaeki);
		} else if (flgPrt == PRT_DEAD_STOCK) {
			itemDead = dslDead.getTmpItem(itemDead, isOrderSmall, comboSelect);
			vdata = dslDead.getVdata(itemDead);
		} else if (flgPrt == PRT_HACCHU) {
			itemHacchu = dslHacchu.getTmpItem(itemHacchu, isOrderSmall,
			        comboSelect);
			vdata = dslHacchu.getVdata(itemHacchu);
		} else if (flgPrt == PRT_KANJA) {
			itemKanja = dslKanja.getTmpItem(itemKanja, isOrderSmall,
			        comboSelect);
			vdata = dslKanja.getVdata(itemKanja);
		} else if (flgPrt == PRT_DENPYO_DETAIL) {
			itemDenpyoDetail = dslDenpyoDetail.getTmpItem(itemDenpyoDetail,
			        isOrderSmall, comboSelect);
			vdata = dslDenpyoDetail.getVdata(itemDenpyoDetail);
		} else if (flgPrt == PRT_DENPYO_SUM) {
			itemDenpyoSum = dslDenpyoSum.getTmpItem(itemDenpyoSum,
			        isOrderSmall, comboSelect);
			vdata = dslDenpyoSum.getVdata(itemDenpyoSum);
		} else if (flgPrt == PRT_ZAIKO) {
			itemZaiko = dslZaiko.getTmpItem(itemZaiko, isOrderSmall,
			        comboSelect);
			vdata = dslZaiko.getVdata(itemZaiko);
		} else if (flgPrt == PRT_INVENT) {
			itemInvent = dslInvent.getTmpItem(itemInvent, isOrderSmall,
			        comboSelect);
			vdata = dslInvent.getVdata(itemInvent);
		} else if (flgPrt == PRT_MED_MASTER) {
			itemMedMaster = dslMedMaster.getTmpItem(itemMedMaster,
			        isOrderSmall, comboSelect);
			vdata = dslMedMaster.getVdata(itemMedMaster);
		} else if (flgPrt == PRT_SUM) {
			itemSum = dslSum.getTmpItem(itemSum, isOrderSmall, comboSelect);
			vdata = dslSum.getVdata(itemSum);
		}

		schDefaultTableModel.setDataVector(vdata, vcolum);
		for (int i = 0; i < schTable.getColumnCount(); ++i) {
			schTable.getColumnModel().getColumn(i).setPreferredWidth(
			        cellwidth[i]);
		}

		if (flgPrt == PRT_JUNBI) {
			schTable = dslJunbi.getCellRenderer(schTable);
		} else if (flgPrt == PRT_SAEKI) {
			schTable = dslSaeki.getCellRenderer(schTable);
		} else if (flgPrt == PRT_DEAD_STOCK) {
			schTable = dslDead.getCellRenderer(schTable);
		} else if (flgPrt == PRT_HACCHU) {
			schTable = dslHacchu.getCellRenderer(schTable);
		} else if (flgPrt == PRT_KANJA) {
			schTable = dslKanja.getCellRenderer(schTable);
		} else if (flgPrt == PRT_DENPYO_DETAIL) {
			schTable = dslDenpyoDetail.getCellRenderer(schTable);
		} else if (flgPrt == PRT_DENPYO_SUM) {
			schTable = dslDenpyoSum.getCellRenderer(schTable);
		} else if (flgPrt == PRT_ZAIKO) {
			schTable = dslZaiko.getCellRenderer(schTable);
		} else if (flgPrt == PRT_INVENT) {
			schTable = dslInvent.getCellRenderer(schTable);
		} else if (flgPrt == PRT_MED_MASTER) {
			schTable = dslMedMaster.getCellRenderer(schTable);
		} else if (flgPrt == PRT_SUM) {
			schTable = dslSum.getCellRenderer(schTable);
		}
	}

	// 取消ボタン
	void clerButton_actionPerformed(ActionEvent e) {
		initProcess();
		return;
	}

	// 初期化処理
	private void initProcess() {

		syuruiCombo.setEnabled(true);
		modeRadioSmall.setEnabled(true);
		modeRadioBig.setEnabled(true);
		if (flgPrt == PRT_SUM) {
			syuruiCombo.setEnabled(false);
			modeRadioSmall.setEnabled(false);
			modeRadioBig.setEnabled(false);
		}
		// yyyyText.setEnabled(false);
		// mmText.setEnabled(false);
		searchButton.setEnabled(true);
		clerButton.setEnabled(false);
		okButton.setEnabled(false);

		for (int i = schDefaultTableModel.getRowCount(); i > 0; --i) {
			schDefaultTableModel.removeRow(i - 1);
		}

		bSearch = false;
		bMeisai = false;
	}

	// 印刷可否の変数を返す
	public String getPrintFlg() {
		return printFlg;
	}

	// 入力内容から、入力部分の再描画処理
	private void recalcTotalArea() {
		double keikingaku = 0.0;
		double keinebiki = 0.0;
		double keikounyuu = 0.0;
		double keizei = 0.0;
		double keizeikomi = 0.0;
		int icnt = vStoclist.size();
		// 04.02.09 onuki
		if (down_to_decimal == null)
			down_to_decimal = "0";
		if (down_to_decimal.equals("1") == false)
			down_to_decimal = "0";

		int intDownDecimal = 2;
		if (down_to_decimal.equals("1")) {
			intDownDecimal = 0;
		}

	}

	public void paint(Graphics g) {
		if (firstFocusComponent != null) {
			firstFocusComponent.requestFocus();
			firstFocusComponent = null;
		}
		super.paint(g);
	}

}
