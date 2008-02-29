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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowEvent;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;


import drugstock.biz.BizContractor;
import drugstock.biz.BizContradrug;
import drugstock.biz.BizStocinput;
import drugstock.cmn.Common;
import drugstock.cmn.DefaultJDialog;
import drugstock.cmn.FocusTraversalPolicyOrder;
import drugstock.cmn.PropRead;
import drugstock.cmn.Sprintf;
import drugstock.cmn.MsgDlg;
import drugstock.component.BButton;
import drugstock.db.ComDatabase;

import com.borland.jbcl.layout.XYConstraints;
import com.borland.jbcl.layout.XYLayout;
import drugstock.model.CodeName;
import drugstock.model.ContItem;
import drugstock.model.OrcaMedicine;
import drugstock.model.Stocking;

/**
 * 「伝票入力」画面処理
 */

public class StocinputDlg extends DefaultJDialog {

	// パネルが開かれた時に最初にフォーカスを当てるコンポーネント
	protected Component firstFocusComponent = null;
	Common com = new Common();

	class Stocinpmei implements Serializable {

		public long stc_id; // 仕入ＮＯ
		public String stc_cd; // 仕入区分
		public String item_no; // 品番
		public String item_nm; // 品名
		public long konpou; // 梱包数
		public double konpouiri; // 梱包入数
		public long housou; // 包装数
		public double housouiri; // 包装入数
		public double bara; // バラ数
		public double barairi; // バラ入数
		public double sousuu; // 総数
		public double tanka; // 単価
		public double kingaku; // 金額
		// public int kingaku; // 金額
		public double nebiki; // 値引額
		// public double kounyuugaku; // 購入額
		public double kounyuugaku; // 購入額
		public String tax_flg; // 税区分
		public double zeikin; // 消費税
		public double zeikomi; // 税込金額
		public double nebikiritu; // 値引率

		public Stocinpmei() {
		};

		public Stocinpmei(long stc_id, // 仕入ＮＯ
		        String stc_cd, // 仕入区分
		        String item_no, // 品番
		        String item_nm, // 品名
		        long konpou, // 梱包数
		        double konpouiri, // 梱包入数
		        long housou, // 包装数
		        double housouiri, // 包装入数
		        double bara, // バラ数
		        double barairi, // バラ入数
		        double sousuu, // 総数
		        double tanka, // 単価
		        double kingaku, // 金額
		        double nebiki, // 値引額
		        double kounyuugaku, // 購入額
		        String tax_flg, // 税区分
		        double zeikin, // 消費税
		        double zeikomi, // 税込金額
		        double nebikiritu) { // 値引率
			this.stc_id = stc_id; // 仕入ＮＯ
			this.stc_cd = stc_cd; // 仕入区分
			this.item_no = item_no; // 品番
			this.item_nm = item_nm; // 品名
			this.konpou = konpou; // 梱包数
			this.konpouiri = konpouiri; // 梱包入数
			this.housou = housou; // 包装数
			this.housouiri = housouiri; // 包装入数
			this.bara = bara; // バラ数
			this.barairi = barairi; // バラ入数
			this.sousuu = sousuu; // 総数
			this.tanka = tanka; // 単価
			// this.kingaku = kingaku; // 金額
			this.kingaku = kingaku; // 金額
			this.nebiki = nebiki; // 値引額
			// this.kounyuugaku = kounyuugaku; // 購入額
			this.kounyuugaku = kounyuugaku; // 購入額
			this.tax_flg = tax_flg; // 税区分
			this.zeikin = zeikin; // 消費税
			this.zeikomi = zeikomi; // 税込金額
			this.nebikiritu = nebikiritu; // 値引率
		}

		// 入力項目クリア（品番以降をクリア）
		public void inpItmClr() {
			this.item_no = ""; // 品番
			this.item_nm = ""; // 品名
			this.konpou = 0; // 梱包数
			this.konpouiri = 0.0; // 梱包入数
			this.housou = 0; // 包装数
			this.housouiri = 0.0; // 包装入数
			this.bara = 0.0; // バラ数
			this.barairi = 0.0; // バラ入数
			this.sousuu = 0.0; // 総数
			this.tanka = 0.0; // 単価
			this.kingaku = 0.0; // 金額
			this.nebiki = 0.0; // 値引額
			this.kounyuugaku = 0.0; // 購入額
			this.tax_flg = "1"; // 税区分
			this.zeikin = 0.0; // 消費税
			this.zeikomi = 0.0; // 税込金額
		}
	}

	// 画面入力用ワークエリア
	private Stocinpmei tmp_stockinp = new Stocinpmei();
	private boolean errorMsg = true;

	// 検索フラグ
	private boolean bSearch = false;
	// 明細フラグ
	private boolean bMeisai = false;

	private boolean gyousyaCombo_busy = false; // 業者選択コンボ アイテム設定中
	private boolean kubunCombo_busy = false; // 仕入区分選択コンボ アイテム設定中
	private boolean ziiCombo_busy = false; // 税区分選択コンボ アイテム設定中
	private CodeName gyousyaCdNm[] = null; // 業者コンボのコード、ネーム
	private Vector vStoclist = new Vector(); // 仕入明細
	private String kubunlst[][] = { { "仕入", "1" }, { "返品", "9" },
	        { "値引き", "5" } };
	private String zeilst[][] = { { "あり", "1" }, { "なし", "0" } };
	private int edit_point; // 編集中の明細ポイント
	private String stock_unit_price_calc; // 単価計算方法 0：単価入力、1：割戻し計算

	private XYLayout xYLayout2 = new XYLayout();
	private ButtonGroup taxGrp = new ButtonGroup();
	private BButton okButton = new BButton();
	private BButton cancelButton = new BButton();
	private TitledBorder titledBorder1;
	private TitledBorder titledBorder2;
	private ButtonGroup modeGrp = new ButtonGroup();
	private JComboBox gyousyaCombo = new JComboBox();
	private JTextField hinbanText = new JTextField();
	private BButton searchButton = new BButton();
	private JLabel jLabel3 = new JLabel();
	private JLabel jLabel4 = new JLabel();
	private JLabel jLabel1 = new JLabel();
	private JTextField gyousyacdText = new JTextField();
	private JTextField hinnmText = new JTextField();
	private DefaultTableModel schDefaultTableModel = new DefaultTableModel() {

		// 表項目の入力不可 04.03.23 onuki
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};

	private JTable schTable = new JTable(schDefaultTableModel);
	private JScrollPane schScrPane = new JScrollPane(schTable);
	private JLabel jLabel5 = new JLabel();
	private JTextField yyyyText = new JTextField();
	private BButton hinsearchButton = new BButton();
	private JComboBox kubunCombo = new JComboBox();
	private JComboBox zeiCombo = new JComboBox();
	private JLabel jLabel9 = new JLabel();
	private JLabel jLabel10 = new JLabel();
	private JLabel jLabel11 = new JLabel();
	private JLabel jLabel12 = new JLabel();
	private JLabel jLabel13 = new JLabel();
	private JLabel jLabel14 = new JLabel();
	private JLabel jLabel15 = new JLabel();
	private JTextField konpouText = new JTextField();
	private JTextField housouText = new JTextField();
	private JTextField baraText = new JTextField();
	private JTextField tannkaText = new JTextField();
	private JTextField kingakuText = new JTextField();
	private JTextField nebikiText = new JTextField();
	private JTextField zeiText = new JTextField();
	private BButton delButton = new BButton();
	private JTextField kounyuuText = new JTextField();
	private JLabel jLabel18 = new JLabel();
	private JTextField keikingakuText = new JTextField();
	private JLabel jLabel19 = new JLabel();
	private JLabel jLabel110 = new JLabel();
	private JLabel jLabel111 = new JLabel();
	private JTextField keinebikiText = new JTextField();
	private JLabel jLabel112 = new JLabel();
	private JTextField keikounyuuText = new JTextField();
	private JLabel jLabel113 = new JLabel();
	private JTextField keizeiText = new JTextField();
	private JLabel jLabel114 = new JLabel();
	private JTextField keizeikomiText = new JTextField();
	private JLabel jLabel20 = new JLabel();
	private JTextField konpouiriText = new JTextField();
	private JTextField housouiriText = new JTextField();
	private JTextField sousuuText = new JTextField();
	private JLabel jLabel6 = new JLabel();
	private JLabel jLabel7 = new JLabel();
	private JLabel jLabel16 = new JLabel();
	private BButton clerButton = new BButton();
	private BButton meiButton = new BButton();
	private JTextField zeikomiText = new JTextField();
	private JLabel jLabel115 = new JLabel();
	private JLabel jLabel8 = new JLabel();
	private JTextField mmText = new JTextField();
	private JTextField ddText = new JTextField();
	private JLabel jLabel17 = new JLabel();
	private JLabel jLabel116 = new JLabel();

	private JLabel jLabel_slip1 = new JLabel();
	private JLabel jLabel_slip2 = new JLabel();
	private JTextField slipNoText = new JTextField();
	private JTextField slipNameText = new JTextField();

	private JLabel jLabelSarchOver = new JLabel();
	private static int INPUT = 0;
	private static int MEISAI = 0;
	private static int MODE_INPUT = 6; // 伝票入力／院内処理

	// 移動 04.02.09 onuki
	// 小数点以下の処理変数をファイルから読み込み
	PropRead prop = new PropRead();
	String down_to_decimal = prop.getProp("down_to_decimal");

	//

	public StocinputDlg(Frame frame, String title, boolean modal) {
		super(frame, title, modal);

		try {
			this.setTitle(title);
			jbInit();
			pack();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public StocinputDlg() {
		this(null, "伝票入力", false);
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

		hinbanText.setFont(new Font("Dialog", 0, 16));
		// hinbanText.setFont(new Color.red);
		hinbanText.setText("");
		hinbanText.addFocusListener(new FocusAdapter() {

			public void focusLost(FocusEvent e) {
				hinbanText_focusLost(e);
			}
			// public void focusGained(FocusEvent e) {
			// hinbanText_focusGained(e);
			// }
		});
		searchButton.setFont(new Font("Dialog", 0, 16));
		searchButton.setActionCommand("決定");
		searchButton.setText("決定");
		searchButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				searchButton_actionPerformed(e);
			}
		});

		jLabel3.setFont(new Font("Dialog", 0, 16));
		jLabel3.setText("梱包数");
		jLabel4.setFont(new Font("Dialog", 0, 16));
		jLabel4.setText("区分");
		// 確定ボタン
		okButton.setFont(new Font("Dialog", 0, 16));
		okButton.setText("確定");
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
		this.getContentPane().setLayout(xYLayout2);
		xYLayout2.setWidth(924);
		xYLayout2.setHeight(637);
		gyousyaCombo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				gyousyaCombo_actionPerformed(e);
			}
		});
		jLabel1.setFont(new Font("Dialog", 0, 16));
		jLabel1.setText("業者");
		gyousyacdText.setBackground(Color.lightGray);
		gyousyacdText.setFont(new Font("Dialog", 0, 16));
		gyousyacdText.setText("");
		gyousyacdText.setEditable(false);
		hinnmText.setBackground(Color.lightGray);
		hinnmText.setFont(new Font("Dialog", 0, 16));
		hinnmText.setText("");
		hinnmText.setEditable(false);

		String[] column_hed = { "区分", "薬剤品番", "薬剤品名", "総数", "単価", "金額", "税込金額" };
		// 院内／業者の仕入れ区分のため、表の幅を広げる 04.02.04 onuki
		// int[] column_width = {20,70,150,50,50,50,50,50};
		int[] column_width = { 40, 50, 200, 50, 50, 50, 50 };
		schDefaultTableModel.setColumnIdentifiers(column_hed);
		for (int i = 0; i < column_width.length; i++) {
			schTable.getColumnModel().getColumn(i).setPreferredWidth(
			        column_width[i]);
		}

		schTable.setFont(new Font("Dialog", 0, 16));
		schTable.setToolTipText("");
		schScrPane.setFont(new Font("Dialog", 0, 16));
		schTable.getTableHeader().setFont(new Font("Dialog", 0, 16));
		schTable.setToolTipText("");

		jLabel5.setFont(new Font("Dialog", 0, 16));
		jLabel5.setText("日付");
		// 日付テキスト年
		yyyyText.setDisabledTextColor(Color.black);
		yyyyText.setFont(new Font("Dialog", 0, 16));
		yyyyText.addFocusListener(new FocusAdapter() {

			public void focusGained(FocusEvent e) {
				yyyyText_focusGained(e);
			}
		});
		yyyyText.setText("");
		hinsearchButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				hinsearchButton_actionPerformed(e);
			}
		});
		hinsearchButton.setFont(new Font("Dialog", 0, 16));
		hinsearchButton.setText("薬剤品番");
		kubunCombo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				kubunCombo_actionPerformed(e);
			}
		});
		zeiCombo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				zeiCombo_actionPerformed(e);
			}
		});
		jLabel9.setFont(new Font("Dialog", 0, 16));
		jLabel9.setText("包装数");
		jLabel10.setFont(new Font("Dialog", 0, 16));
		jLabel10.setText("バラ数");
		jLabel11.setFont(new Font("Dialog", 0, 16));
		jLabel11.setText("単価");
		jLabel12.setFont(new Font("Dialog", 0, 16));
		jLabel12.setText("金額");
		jLabel13.setFont(new Font("Dialog", 0, 16));
		jLabel13.setText("値引額");
		jLabel14.setFont(new Font("Dialog", 0, 16));
		jLabel14.setText("消費税");
		jLabel15.setFont(new Font("Dialog", 0, 16));
		jLabel15.setText("税");
		konpouText.setFont(new Font("Dialog", 0, 16));
		konpouText.setHorizontalAlignment(SwingConstants.RIGHT);
		konpouText.addFocusListener(new FocusAdapter() {

			public void focusLost(FocusEvent e) {
				konpouText_focusLost(e);
			}

			public void focusGained(FocusEvent e) {
				konpouText_focusGained(e);
			}
		});
		konpouText.setText("");
		housouText.setFont(new Font("Dialog", 0, 16));
		housouText.setHorizontalAlignment(SwingConstants.RIGHT);
		housouText.addFocusListener(new FocusAdapter() {

			public void focusLost(FocusEvent e) {
				housouText_focusLost(e);
			}

			public void focusGained(FocusEvent e) {
				housouText_focusGained(e);
			}
		});
		housouText.setText("");
		baraText.setFont(new Font("Dialog", 0, 16));
		baraText.setHorizontalAlignment(SwingConstants.RIGHT);
		baraText.setText("");
		// 単価テキスト
		tannkaText.setFont(new Font("Dialog", 0, 16));
		tannkaText.setHorizontalAlignment(SwingConstants.RIGHT);
		tannkaText.addFocusListener(new FocusAdapter() {

			public void focusLost(FocusEvent e) {
				tannkaText_focusLost(e);
			}

			public void focusGained(FocusEvent e) {
				tannkaText_focusGained(e);
			}
		});
		tannkaText.setText("");

		// 金額テキスト
		kingakuText.setText("");
		kingakuText.setHorizontalAlignment(SwingConstants.RIGHT);
		kingakuText.setFont(new Font("Dialog", 0, 16));
		kingakuText.addFocusListener(new FocusAdapter() {

			public void focusLost(FocusEvent e) {
				kingakuText_focusLost(e);
			}

			public void focusGained(FocusEvent e) {
				kingakuText_focusGained(e);
			}
		});

		// 単価、金額 入力制御
		if (stock_unit_price_calc.equals("1")) {
			tannkaText.setBackground(Color.lightGray);
			tannkaText.setEditable(false);
		} else {
			kingakuText.setBackground(Color.lightGray);
			kingakuText.setEditable(false);
		}
		// 値引きテキスト
		nebikiText.setFont(new Font("Dialog", 0, 16));
		nebikiText.setHorizontalAlignment(SwingConstants.RIGHT);
		nebikiText.addFocusListener(new FocusAdapter() {

			public void focusLost(FocusEvent e) {
				nebikiText_focusLost(e);
			}

			public void focusGained(FocusEvent e) {
				nebikiText_focusGained(e);
			}
		});
		// 消費税テキスト
		zeiText.setFont(new Font("Dialog", 0, 16));
		zeiText.setHorizontalAlignment(SwingConstants.RIGHT);
		zeiText.addFocusListener(new FocusAdapter() {

			public void focusLost(FocusEvent e) {
				zeiText_focusLost(e);
			}

			public void focusGained(FocusEvent e) {
				zeiText_focusGained(e);
			}
		});
		zeiText.setText("");
		// 削除ボタン
		delButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				delButton_actionPerformed(e);
			}
		});

		delButton.setFont(new Font("Dialog", 0, 16));
		delButton.setText("削除");
		keikingakuText.setBackground(Color.lightGray);
		keikingakuText.setFont(new Font("Dialog", 0, 16));
		keikingakuText.setHorizontalAlignment(SwingConstants.RIGHT);
		keikingakuText.setText("");
		keikingakuText.setEditable(false);
		jLabel19.setFont(new Font("Dialog", 0, 16));
		jLabel19.setText("金額");
		jLabel110.setFont(new Font("Dialog", 0, 16));
		jLabel110.setText("合　計");
		jLabel111.setFont(new Font("Dialog", 0, 16));
		jLabel111.setText("値引額");
		keinebikiText.setHorizontalAlignment(SwingConstants.RIGHT);
		keinebikiText.setText("");
		keinebikiText.setBackground(Color.lightGray);
		keinebikiText.setFont(new Font("Dialog", 0, 16));
		keinebikiText.setEditable(false);
		jLabel113.setFont(new Font("Dialog", 0, 16));
		jLabel113.setText("消費税額");
		keizeiText.setBackground(Color.lightGray);
		keizeiText.setFont(new Font("Dialog", 0, 16));
		keizeiText.setHorizontalAlignment(SwingConstants.RIGHT);
		keizeiText.setText("");
		keizeiText.setEditable(false);
		jLabel114.setFont(new Font("Dialog", 0, 16));
		jLabel114.setText("税込金額");
		keizeikomiText.setBackground(Color.lightGray);
		keizeikomiText.setFont(new Font("Dialog", 0, 16));
		keizeikomiText.setHorizontalAlignment(SwingConstants.RIGHT);
		keizeikomiText.setText("");
		keizeikomiText.setEditable(false);
		jLabel20.setFont(new Font("Dialog", 0, 16));
		jLabel20.setText("薬剤品名");
		gyousyaCombo.setFont(new Font("Dialog", 0, 16));
		schScrPane.setFont(new Font("Dialog", 0, 16));
		kubunCombo.setFont(new Font("Dialog", 0, 16));
		zeiCombo.setFont(new Font("Dialog", 0, 16));
		konpouiriText.setHorizontalAlignment(SwingConstants.RIGHT);
		konpouiriText.setText("");
		konpouiriText.setFont(new Font("Dialog", 0, 16));
		konpouiriText.setBackground(Color.lightGray);
		konpouiriText.setEditable(false);
		housouiriText.setBackground(Color.lightGray);
		housouiriText.setFont(new Font("Dialog", 0, 16));
		housouiriText.setHorizontalAlignment(SwingConstants.RIGHT);
		housouiriText.setText("");
		housouiriText.setEditable(false);
		sousuuText.setText("");
		sousuuText.setHorizontalAlignment(SwingConstants.RIGHT);
		sousuuText.setFont(new Font("Dialog", 0, 16));
		sousuuText.setBackground(Color.lightGray);
		sousuuText.setEditable(false);
		jLabel6.setText("入数");
		jLabel6.setFont(new Font("Dialog", 0, 16));
		jLabel7.setFont(new Font("Dialog", 0, 16));
		jLabel7.setText("入数");
		jLabel16.setText("総数");
		jLabel16.setFont(new Font("Dialog", 0, 16));
		// クリアボタン
		clerButton.setText("クリア");
		clerButton.setFont(new Font("Dialog", 0, 16));
		clerButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				clerButton_actionPerformed(e);
			}
		});
		// 明細決定ボタン
		meiButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				meiButton_actionPerformed(e);
			}
		});
		meiButton.setFont(new Font("Dialog", 0, 16));
		meiButton.setText("明細決定");
		// 税込み金額表示テキスト
		zeikomiText.setEditable(false);
		zeikomiText.setText("");
		zeikomiText.setHorizontalAlignment(SwingConstants.RIGHT);
		zeikomiText.setFont(new Font("Dialog", 0, 16));
		zeikomiText.setBackground(Color.lightGray);
		jLabel115.setText("税込金額");
		jLabel115.setFont(new Font("Dialog", 0, 16));

		// ロストフォーカスイベント
		baraText.addFocusListener(new FocusAdapter() {

			public void focusLost(FocusEvent evt) {
				baraText_focusLost(evt);
			}

			public void focusGained(FocusEvent e) {
				baraText_focusGained(e);
			}
		});

		// 日付テキスト月
		mmText.setDisabledTextColor(Color.black);
		mmText.setText("");
		mmText.setFont(new Font("Dialog", 0, 16));
		mmText.addFocusListener(new FocusAdapter() {

			public void focusGained(FocusEvent e) {
				mmText_focusGained(e);
			}
		});
		// 日付テキスト日
		ddText.setDisabledTextColor(Color.black);
		ddText.setText("");
		ddText.setFont(new Font("Dialog", 0, 16));
		ddText.addFocusListener(new FocusAdapter() {

			public void focusGained(FocusEvent e) {
				ddText_focusGained(e);
			}
		});
		jLabel8.setText("年");
		jLabel8.setFont(new Font("Dialog", 0, 16));
		jLabel17.setText("月");
		jLabel17.setFont(new Font("Dialog", 0, 16));
		jLabel116.setText("日");
		jLabel116.setFont(new Font("Dialog", 0, 16));

		// 伝票表示テキスト
		slipNoText.setEditable(true);
		slipNoText.setText("");
		slipNoText.setHorizontalAlignment(SwingConstants.RIGHT);
		slipNoText.setFont(new Font("Dialog", 0, 16));
		slipNoText.setBackground(Color.white);
		jLabel_slip1.setText("伝票");
		jLabel_slip1.setFont(new Font("Dialog", 0, 16));

		// 期限切れ注意
		jLabelSarchOver.setText("薬剤の有効期限が切れています！");
		jLabelSarchOver.setFont(new Font("Dialog", 0, 16));
		jLabelSarchOver.setVisible(false);

		// 業者
		this.getContentPane().add(jLabel1, new XYConstraints(38, 19, 39, -1));
		this.getContentPane().add(gyousyaCombo,
		        new XYConstraints(75, 17, 180, 23));
		this.getContentPane().add(gyousyacdText,
		        new XYConstraints(259, 17, 86, 23));
		// 伝票
		this.getContentPane().add(jLabel_slip1,
		        new XYConstraints(363, 19, 40, -1));
		this.getContentPane().add(slipNoText,
		        new XYConstraints(402, 17, 100, 23));
		// 日付
		this.getContentPane().add(jLabel5, new XYConstraints(363, 59, 40, -1));
		this.getContentPane().add(yyyyText, new XYConstraints(402, 57, 44, 23));
		this.getContentPane().add(jLabel8, new XYConstraints(452, 59, 22, -1));
		this.getContentPane().add(mmText, new XYConstraints(478, 57, 31, 23));
		this.getContentPane().add(jLabel17, new XYConstraints(514, 59, 22, -1));
		this.getContentPane().add(ddText, new XYConstraints(538, 57, 31, 23));
		this.getContentPane().add(jLabel116, new XYConstraints(575, 58, 22, -1));
		// 上ボタン
		this.getContentPane().add(searchButton,
		        new XYConstraints(619, 50, 93, 38));
		this.getContentPane().add(clerButton,
		        new XYConstraints(721, 50, 97, 38));
		// 表
		this.getContentPane().add(schScrPane,
		        new XYConstraints(29, 96, 870, 296));
		// 明細決定
		this.getContentPane().add(meiButton,
		        new XYConstraints(30, 396, 103, 31));
		this.getContentPane().add(jLabelSarchOver,
		        new XYConstraints(152, 402, 300, 23));
		// 合計金額
		this.getContentPane().add(jLabel110,
		        new XYConstraints(600, 417, -1, -1));
		this.getContentPane().add(jLabel19, new XYConstraints(674, 395, -1, -1));
		this.getContentPane().add(jLabel114,
		        new XYConstraints(780, 395, -1, -1));
		this.getContentPane().add(keikingakuText,
		        new XYConstraints(660, 416, 116, 23));
		this.getContentPane().add(keizeikomiText,
		        new XYConstraints(778, 416, 116, 23));
		// 入力列 １行目
		this.getContentPane().add(jLabel4, new XYConstraints(32, 452, 39, -1));
		this.getContentPane().add(kubunCombo,
		        new XYConstraints(31, 473, 98, 23));
		this.getContentPane().add(hinsearchButton,
		        new XYConstraints(131, 446, 104, 25));
		this.getContentPane().add(hinbanText,
		        new XYConstraints(130, 473, 106, 23));
		this.getContentPane().add(hinnmText,
		        new XYConstraints(236, 473, 242, 23));
		this.getContentPane().add(jLabel20, new XYConstraints(239, 451, -1, -1));
		this.getContentPane().add(konpouText,
		        new XYConstraints(479, 473, 65, 23));
		this.getContentPane().add(jLabel3, new XYConstraints(484, 452, -1, -1));
		this.getContentPane().add(jLabel6, new XYConstraints(545, 452, -1, -1));
		this.getContentPane().add(konpouiriText,
		        new XYConstraints(544, 473, 49, 23));
		this.getContentPane().add(housouText,
		        new XYConstraints(594, 473, 64, 23));
		this.getContentPane().add(jLabel9, new XYConstraints(598, 452, -1, -1));
		this.getContentPane().add(housouiriText,
		        new XYConstraints(658, 473, 51, 23));
		this.getContentPane().add(jLabel7, new XYConstraints(659, 452, -1, -1));
		this.getContentPane().add(jLabel16, new XYConstraints(789, 452, -1, -1));
		this.getContentPane().add(sousuuText,
		        new XYConstraints(787, 473, 104, 23));
		this.getContentPane().add(jLabel10, new XYConstraints(713, 453, -1, -1));
		this.getContentPane().add(baraText, new XYConstraints(710, 473, 77, 23));

		// 入力列 ２行目
		this.getContentPane().add(cancelButton,
		        new XYConstraints(793, 579, 97, 38));
		this.getContentPane().add(okButton, new XYConstraints(581, 580, 97, 38));
		this.getContentPane().add(delButton,
		        new XYConstraints(687, 580, 97, 38));
		this.getContentPane().add(zeiText, new XYConstraints(684, 524, 90, 23));
		this.getContentPane().add(zeikomiText,
		        new XYConstraints(776, 524, 116, 23));
		this.getContentPane().add(tannkaText,
		        new XYConstraints(305, 525, 90, 23));
		this.getContentPane().add(kingakuText,
		        new XYConstraints(397, 525, 116, 23));
		this.getContentPane().add(nebikiText,
		        new XYConstraints(515, 524, 90, 23));
		this.getContentPane().add(zeiCombo, new XYConstraints(612, 524, 71, 23));
		this.getContentPane().add(jLabel13, new XYConstraints(516, 504, -1, -1));
		this.getContentPane().add(jLabel15, new XYConstraints(617, 502, -1, -1));
		this.getContentPane().add(jLabel14, new XYConstraints(687, 503, -1, -1));
		this.getContentPane().add(jLabel115,
		        new XYConstraints(778, 503, -1, -1));
		this.getContentPane().add(jLabel12, new XYConstraints(399, 504, -1, -1));
		this.getContentPane().add(jLabel11, new XYConstraints(308, 505, -1, -1));

		Component tmpCom;
		if (stock_unit_price_calc.equals("1")) {
			// 金額入力（単価計算）の場合
			tmpCom = kingakuText; // バラ数入力テキスト
		} else {
			// 単価入力（金額計算）の場合
			tmpCom = tannkaText; // 単価入力テキスト
		}
		Component order[] = new Component[] { gyousyaCombo, slipNoText,
		        searchButton, clerButton, meiButton, hinsearchButton,
		        kubunCombo, hinbanText, konpouText, housouText, baraText,
		        tmpCom, nebikiText,
		        // tannkaText, kingakuText, nebikiText,
		        zeiCombo, zeiText, okButton, delButton, cancelButton };
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

		// 仕入区分コンボセット
		kubunCombo_busy = true;
		kubunCombo.setEditable(false);
		kubunCombo.removeAllItems();
		for (int i = 0; i < kubunlst.length; i++) {
			kubunCombo.addItem(kubunlst[i][0]);
		}
		kubunCombo_busy = false;

		// 税区分コンボセット
		ziiCombo_busy = true;
		zeiCombo.setEditable(false);
		zeiCombo.removeAllItems();
		for (int i = 0; i < zeilst.length; i++) {
			zeiCombo.addItem(zeilst[i][0]);
		}
		ziiCombo_busy = false;

		// 日付テキストにシステム日付を表示
		SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
		yyyyText.setText(f.format(new Date()).substring(0, 4));
		mmText.setText(f.format(new Date()).substring(4, 6));
		ddText.setText(f.format(new Date()).substring(6, 8));
		// 先頭の業者を選択状態にする
		if (gyousyaCdNm.length > 0) {
			gyousyacdText.setText(gyousyaCdNm[0].getCode());
		}

		// クリア状態する
		clerButton_actionPerformed(new ActionEvent(clerButton, 0, ""));

		firstFocusComponent = gyousyaCombo;
	}

	/**
	 * ウィンドウが開かれたときのイベントをオーバーライドします。
	 */
	protected void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_OPENED) {
			// hinbanText.requestFocus();
		}
	}

	// 確定ボタン
	void okButton_actionPerformed(ActionEvent e) {
		MsgDlg msgdlg = new MsgDlg(this);
		// チェック処理
		if (hinbanText.getText().equals("")) {
			msgdlg.msgdsp("薬剤が指定されていません。", MsgDlg.ERROR_MESSAGE);
			hinbanText.requestFocus();
			return;
		}
		// 値引きのみ、数量なしでも可
		if ((tmp_stockinp.sousuu == 0)
		        && ((tmp_stockinp.stc_cd.equals("1")) || (tmp_stockinp.stc_cd.equals("9")))) {
			msgdlg.msgdsp("仕入数が０です。", MsgDlg.ERROR_MESSAGE);
			baraText.requestFocus();
			return;
		}
		// 入力部の色を初期化
		inputInit();
		// 明細セーブ用のワークを生成
		Stocinpmei stocinpmei = new Stocinpmei();
		StocinpmeiCopy(tmp_stockinp, stocinpmei); // 入力ワーク用へコピー
		// 登録の為のmodelを生成
		Stocking stocking = new Stocking(); // 登録用のmodelを確保
		stocking.stc_id = String.valueOf(stocinpmei.stc_id); // 仕入ＮＯ
		// 仕入日
		stocking.stc_date = yyyyText.getText();
		if (mmText.getText().length() < 2)
			stocking.stc_date = stocking.stc_date + "0" + mmText.getText();
		else
			stocking.stc_date = stocking.stc_date + mmText.getText();
		if (ddText.getText().length() < 2)
			stocking.stc_date = stocking.stc_date + "0" + ddText.getText();
		else
			stocking.stc_date = stocking.stc_date + ddText.getText();
		stocking.slip_no = slipNoText.getText(); // 伝票番号
		stocking.stc_cd = kubunlst[kubunCombo.getSelectedIndex()][1]; // 仕入区分
		stocking.cont_id = gyousyaCdNm[gyousyaCombo.getSelectedIndex()].getid(); // 業者区分
		stocking.item_no = hinbanText.getText(); // 品番
		stocking.stc_unit = String.valueOf(stocinpmei.tanka); // 仕入単価
		stocking.tax_flg = zeilst[zeiCombo.getSelectedIndex()][1]; // 税区分
		// if(stocking.stc_cd.equals("9")){
		if ((stocking.stc_cd.equals("5")) || (stocking.stc_cd.equals("9"))) {
			// 仕入区分：返品or値引きの場合は×-1
			stocking.stc_num = String.valueOf(stocinpmei.sousuu * -1); // 仕入数量
			stocking.amount = String.valueOf(stocinpmei.kingaku * -1); // 金額（単価×仕入数量）
			stocking.discount = String.valueOf(stocinpmei.nebiki * -1); // 値引
			stocking.stc_amount = String.valueOf(stocinpmei.kounyuugaku * -1); // 購入金額
			stocking.tax = String.valueOf(stocinpmei.zeikin * -1); // 税金額
			stocking.pack3_num = String.valueOf(stocinpmei.konpou * -1); // 仕入梱包数
			stocking.pack2_num = String.valueOf(stocinpmei.housou * -1); // 仕入包装数
			stocking.pack1_num = String.valueOf(stocinpmei.bara * -1); // 仕入バラ数
		} else {
			stocking.stc_num = String.valueOf(stocinpmei.sousuu); // 仕入数量
			stocking.amount = String.valueOf(stocinpmei.kingaku); // 金額（単価×仕入数量）
			stocking.discount = String.valueOf(stocinpmei.nebiki); // 値引
			stocking.stc_amount = String.valueOf(stocinpmei.kounyuugaku); // 購入金額
			stocking.tax = String.valueOf(stocinpmei.zeikin); // 税金額
			stocking.pack3_num = String.valueOf(stocinpmei.konpou); // 仕入梱包数
			stocking.pack2_num = String.valueOf(stocinpmei.housou); // 仕入包装数
			stocking.pack1_num = String.valueOf(stocinpmei.bara); // 仕入バラ数
		}

		// 登録処理
		BizStocinput biz = new BizStocinput();
		if (stocinpmei.stc_id < 0) {
			// 新規登録
			String input_stc_id = null;
			input_stc_id = biz.insStocking(stocking); // 仕入ＤＢへ追加
			if (input_stc_id.equals("NG")) {
				msgdlg.msgdsp("新規登録が失敗しました。", MsgDlg.ERROR_MESSAGE);
			} else {
				// 区分を確定時のままにする(1)
				String tmp_stc_cd = stocinpmei.stc_cd;
				stocinpmei.stc_id = Long.parseLong(input_stc_id); // 仕入ＮＯ
				vStoclist.setElementAt(stocinpmei, edit_point); // セーブ明細の書き換え
				dspSiire_Table_1mei(edit_point, 0); // 仕入明細テーブルへ書き換えた明細を表示
				// 新規用セーブ明細に追加
				// 区分を確定時のままにする(2)
				stocinpmei = new Stocinpmei(-1, tmp_stc_cd, "", "", 0, 0, 0, 0,
				        0, 0, 0, 0, 0, 0, 0, "1", 0, 0, 0);
				vStoclist.addElement(stocinpmei);
				edit_point = vStoclist.size() - 1; // 編集対象の明細位置を退避（新規明細）
				dspSiire_Table_1mei(edit_point, 1); // 仕入明細テーブルへ新規明細を追加表示
				// tmp_stockinp = (Stocinpmei)vStoclist.elementAt(edit_point);
				// // 入力ワーク用へコピー
				StocinpmeiCopy((Stocinpmei)vStoclist.get(edit_point),
				        tmp_stockinp); // 入力ワーク用へコピー
				dspSiire_inputarea((Stocinpmei)vStoclist.elementAt(edit_point)); // 仕入明細入力エリア表示（新規明細）
				int rowmax = schTable.getRowCount();
				schTable.changeSelection(rowmax - 1, 0, false, false); // 新規行を選択状態とする
				kubunCombo.requestFocus(); // フォーカスを区分に移動
			}
		} else {
			// 修正登録
			String status;
			status = biz.updtStocking(stocking); // 仕入ＤＢを更新
			if (status.equals("NG")) {
				msgdlg.msgdsp("修正登録が失敗しました。", MsgDlg.ERROR_MESSAGE);
			} else {
				vStoclist.setElementAt(stocinpmei, edit_point); // セーブ明細の書き換え
				dspSiire_Table_1mei(edit_point, 0); // 仕入明細テーブルへ書き換えた明細を表示
				// tmp_stockinp = (Stocinpmei)vStoclist.elementAt(edit_point);
				// // 入力ワーク用へコピー
				StocinpmeiCopy((Stocinpmei)vStoclist.get(edit_point),
				        tmp_stockinp); // 入力ワーク用へコピー
				dspSiire_inputarea((Stocinpmei)vStoclist.elementAt(edit_point)); // 仕入明細入力エリア表示
				hinbanText.requestFocus(); // フォーカスを品番に移動
			}
		}
		recalcTotalArea(); // 合計表示

	}

	// 削除ボタン
	void delButton_actionPerformed(ActionEvent e) {
		MsgDlg msgdlg = new MsgDlg(this);

		// 削除確認
		int msgsts = msgdlg.msgdsp("削除してよろしいですか？", MsgDlg.QUESTION_MESSAGE,
		        MsgDlg.YES_NO_OPTION);
		if (msgsts == 1)
			return;

		Stocinpmei stocinpmei = new Stocinpmei();
		StocinpmeiCopy((Stocinpmei)vStoclist.get(edit_point), stocinpmei);

		BizStocinput biz = new BizStocinput();
		String status;
		status = biz.delStocking(String.valueOf(stocinpmei.stc_id)); // 仕入ＤＢへ削除更新
		if (status.equals("NG")) {
			msgdlg.msgdsp("削除が失敗しました。", MsgDlg.ERROR_MESSAGE);
		} else {
			vStoclist.remove(edit_point); // セーブ明細から削除
			schDefaultTableModel.removeRow(edit_point); // グリッドから削除
			dspSiire_inputarea((Stocinpmei)vStoclist.elementAt(edit_point)); // 仕入明細入力エリア表示（削除した次の明細）
			schTable.changeSelection(edit_point, 0, false, false); // 新規行を選択状態とする
			// tmp_stockinp = (Stocinpmei)vStoclist.elementAt(edit_point); //
			// 入力ワーク用へコピー
			StocinpmeiCopy((Stocinpmei)vStoclist.get(edit_point), tmp_stockinp); // 入力ワーク用へコピー
			hinbanText.requestFocus(); // フォーカスを品番に移動
		}

		recalcTotalArea(); // 合計表示
	}

	// 戻るボタン
	void cancelButton_actionPerformed(ActionEvent e) {
		dispose();
	}

	// 決定ボタン
	void searchButton_actionPerformed(ActionEvent e) {
		MsgDlg msgdlg = new MsgDlg(this);

		String sy;
		String sm;
		String sd;
		try {
			sy = yyyyText.getText();
			sm = mmText.getText();
			sd = ddText.getText();
		} catch (StringIndexOutOfBoundsException ser) {
			msgdlg.msgdsp("日付を正しく入力してください。", MsgDlg.ERROR_MESSAGE);
			yyyyText.requestFocus();
			return;
		}
		int iy;
		int im;
		int id;
		try {
			iy = Integer.parseInt(sy);
			im = Integer.parseInt(sm);
			id = Integer.parseInt(sd);
		} catch (NumberFormatException ier) {
			msgdlg.msgdsp("日付を正しく入力してください。", MsgDlg.ERROR_MESSAGE);
			yyyyText.requestFocus();
			return;
		}
		try {
			Calendar cal = Calendar.getInstance();
			cal.setLenient(false);
			cal.set(iy, im - 1, id); // この−１がミソ
			cal.get(Calendar.YEAR);
		} catch (IllegalArgumentException cer) {
			msgdlg.msgdsp("日付を正しく入力してください。", MsgDlg.ERROR_MESSAGE);
			yyyyText.requestFocus();
			return;
		}

		gyousyaCombo.setEnabled(false); // 業者選択コンボ
		// gyousyacdText // 選択業者表示テキスト
		slipNoText.setEnabled(false); // 伝票テキスト
		yyyyText.setEnabled(false); // 日付テキスト
		mmText.setEnabled(false);
		ddText.setEnabled(false);
		searchButton.setEnabled(false); // 決定ボタン
		clerButton.setEnabled(true); // クリアボタン
		meiButton.setEnabled(true); // 明細指定ボタン
		keikingakuText.setText(""); // 計：金額表示テキスト
		keinebikiText.setText(""); // 計：値引額表示テキスト
		keikounyuuText.setText(""); // 計：購入金額表示テキスト
		keizeiText.setText(""); // 計：税金額表示テキスト
		keizeikomiText.setText(""); // 計：税込金額表示テキスト
		kubunCombo.setEnabled(true); // 仕入区分選択コンボ
		kubunCombo.setSelectedIndex(0);
		hinbanText.setText(""); // 薬剤品番入力テキスト
		hinbanText.setEnabled(true);
		hinnmText.setText(""); // 薬剤品名表示テキスト
		konpouText.setText(""); // 梱包数入力テキスト
		konpouText.setEnabled(true);
		konpouiriText.setText(""); // 梱包入数表示テキスト
		housouText.setText(""); // 包装数入力テキスト
		housouText.setEnabled(true);
		housouiriText.setText(""); // 包装入数表示テキスト
		baraText.setText(""); // バラ数入力テキスト
		baraText.setEnabled(true);
		// barairiText.setText(""); // バラ入数表示テキスト
		sousuuText.setText(""); // 総数表示テキスト
		// 単価、金額テキスト
		if (stock_unit_price_calc.equals("1")) {
			kingakuText.setEnabled(true);
		} else {
			tannkaText.setEnabled(true);
		}
		tannkaText.setText(""); // 単価入力テキスト
		kingakuText.setText(""); // 金額表示テキスト
		nebikiText.setText(""); // 値引額入力テキスト
		nebikiText.setEnabled(true);
		// kounyuuText.setText(""); // 購入金額表示テキスト
		zeiCombo.setEnabled(true); // 税金区分選択コンボ
		ziiCombo_busy = true;
		zeiCombo.setSelectedIndex(0);
		ziiCombo_busy = false;
		zeiText.setText(""); // 税金額入力テキスト
		zeiText.setEnabled(true);
		zeikomiText.setText(""); // 税込金額表示テキスト

		hinsearchButton.setEnabled(true); // 薬剤品番検索ボタン
		okButton.setEnabled(true); // 確定ボタン
		delButton.setEnabled(false); // 削除ボタン

		// 明細格納エリアをクリア
		vStoclist = new Vector();
		// 仕入明細リストの取得
		// 業者コード
		String cont_id_srach = gyousyaCdNm[gyousyaCombo.getSelectedIndex()].getid();
		String cont_tax_flag = getContTaxFlag(cont_id_srach);
		// 仕入日
		String stc_date_srach = yyyyText.getText();
		if (mmText.getText().length() < 2)
			stc_date_srach = stc_date_srach + "0" + mmText.getText();
		else
			stc_date_srach = stc_date_srach + mmText.getText();
		if (ddText.getText().length() < 2)
			stc_date_srach = stc_date_srach + "0" + ddText.getText();
		else
			stc_date_srach = stc_date_srach + ddText.getText();
		// 伝票番号
		String slip_no_srach = slipNoText.getText();
		// データの取り込み
		BizStocinput biz = new BizStocinput();
		Stocking stocking[] = biz.getStockingData(cont_id_srach,
		        stc_date_srach, slip_no_srach);
		// 仕入明細を明細格納エリアに格納
		if (stocking != null) {
			for (int i = 0; stocking.length > i; i++) {
				// 明細に、該当仕入区分以外を表示しない
				boolean isStc_cd = true;
				if (stocking[i].stc_cd.equals("2"))
					isStc_cd = false;
				if (stocking[i].stc_cd.equals("3"))
					isStc_cd = false;
				if (stocking[i].stc_cd.equals("4"))
					isStc_cd = false;
				if (stocking[i].stc_cd.equals("8"))
					isStc_cd = false;
				if (isStc_cd) {
					// 明細セーブ用のワークを生成
					long stc_id = Long.parseLong(stocking[i].stc_id); // 仕入ＮＯ
					String stc_cd = stocking[i].stc_cd; // 仕入区分
					String item_no = stocking[i].item_no; // 品番
					String item_nm = stocking[i].med_nm; // 品名
					long konpou = Long.parseLong(stocking[i].pack3_num); // 梱包数
					double konpouiri = Double.parseDouble(stocking[i].pack_unit3); // 梱包入数
					long housou = Long.parseLong(stocking[i].pack2_num); // 包装数
					double housouiri = Double.parseDouble(stocking[i].pack_unit2); // 包装入数
					double bara = Double.parseDouble(stocking[i].pack1_num); // バラ数
					double barairi = Double.parseDouble(stocking[i].pack_unit1); // バラ入数
					// double barairi = 1; // バラ入数 //未使用
					double sousuu = Double.parseDouble(stocking[i].stc_num); // 総数
					double tanka = Double.parseDouble(stocking[i].stc_unit); // 単価
					double kingaku = Double.parseDouble(stocking[i].amount); // 金額
					double nebiki = Double.parseDouble(stocking[i].discount); // 値引額
					double kounyuugaku = Double.parseDouble(stocking[i].stc_amount); // 購入額
					String tax_flg = stocking[i].tax_flg; // 税区分
					double zeikin = Double.parseDouble(stocking[i].tax); // 消費税
					double nebikiritu = Double.parseDouble(stocking[i].discountritu); // 値引率
					// 返品の表示はマイナス
					if ((stc_cd.equals("9")) || (stc_cd.equals("5"))) {
						// 仕入区分：返品or値引きの場合 数量金額に× -1
						if (konpou != 0)
							konpou *= -1; // 梱包数
						if (housou != 0)
							housou *= -1; // 包装数
						if (bara != 0)
							bara *= -1; // バラ数
						if (sousuu != 0)
							sousuu *= -1; // 総数
						if (kingaku != 0)
							kingaku *= -1; // 金額
						if (nebiki != 0)
							nebiki *= -1; // 値引額
						if (kounyuugaku != 0)
							kounyuugaku *= -1; // 購入額
						if (zeikin != 0)
							zeikin *= -1; // 消費税
					}
					double zeikomi = kounyuugaku + zeikin; // 税込金額

					Stocinpmei wk = new Stocinpmei(stc_id, // 仕入ＮＯ
					        stc_cd, // 仕入区分
					        item_no, // 品番
					        item_nm, // 品名
					        konpou, // 梱包数
					        konpouiri, // 梱包入数
					        housou, // 包装数
					        housouiri, // 包装入数
					        bara, // バラ数
					        barairi, // バラ入数
					        sousuu, // 総数
					        tanka, // 単価
					        kingaku, // 金額
					        nebiki, // 値引額
					        kounyuugaku, // 購入額
					        tax_flg, // 税区分
					        zeikin, // 消費税
					        zeikomi, // 税込金額
					        nebikiritu); // 値引率
					vStoclist.addElement(wk);
				}
			}
		}
		// 新規入力用の明細を１明細追加
		Stocinpmei stocinpmei = new Stocinpmei(-1, "1", "", "", 0, 0, 0, 0, 0,
		        0, 0, 0, 0, 0, 0, cont_tax_flag, 0, 0, 0);
		vStoclist.addElement(stocinpmei);
		// グリッドに明細を表示
		dspSiire_Table();
		recalcTotalArea(); // 合計表示
		edit_point = vStoclist.size() - 1; // 編集対象の明細位置を退避
		dspSiire_inputarea((Stocinpmei)vStoclist.elementAt(edit_point)); // 仕入明細入力エリア表示
		// tmp_stockinp = (Stocinpmei)vStoclist.elementAt(edit_point); //
		// 入力ワーク用へコピー
		StocinpmeiCopy((Stocinpmei)vStoclist.get(edit_point), tmp_stockinp); // 入力ワーク用へコピー
		schTable.changeSelection(edit_point, 0, false, false); // 新規行を選択状態とする
		kubunCombo.requestFocus(); // フォーカスを区分に移動

		bSearch = true;
	}

	private String getContTaxFlag(String cont_id_srach) {
		ComDatabase db = new ComDatabase();
		Connection conn = db.getConnection();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select tax_flg from m_contractor where cont_id = ?";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, cont_id_srach);
			rs = stmt.executeQuery();
			while (rs.next()) {
				return rs.getString("tax_flg");
			}
		} catch (SQLException e) {
			System.out.println("StocinputDlg getContTaxFlag SQLException"
			        + e.toString());
		} finally {
			db.closeAllResource(rs, stmt, conn);
		}
		return "1";
	}

	// 業者選択コンボ
	void gyousyaCombo_actionPerformed(ActionEvent e) {
		if (gyousyaCombo_busy == true)
			return;
		int i = gyousyaCombo.getSelectedIndex();
		gyousyacdText.setText(gyousyaCdNm[i].getCode());
		// inputItem_clear();
	}

	// 品番検索ボタン
	void hinsearchButton_actionPerformed(ActionEvent e) {
		ContradrugsearchDlg dlg = new ContradrugsearchDlg();
		dlg.setInitmode(MODE_INPUT,
		        gyousyaCdNm[gyousyaCombo.getSelectedIndex()].getid());
		// ,gyousyacdText.getText(),(String)gyousyaCombo.getSelectedItem());
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
			ContItem contitem = (ContItem)dlg.getKakuteiDat(); // 業種別薬剤品目データ
			// システム日付と薬剤期限を比較
			if (cautionKigen(contitem.orca_med_cd, contitem.item_no, INPUT) == false) {
				// 入力部の色を初期化 2004.07.09 onuki
				inputInit();
			}
			// cautionKigen(contitem.orca_med_cd, contitem.item_no, INPUT) ;
			tmp_stockinp.item_no = contitem.item_no;
			tmp_stockinp.item_nm = contitem.med_nm;
			tmp_stockinp.barairi = Double.parseDouble(contitem.pack_unit1);
			// tmp_stockinp.barairi = 1.0;
			tmp_stockinp.housouiri = Double.parseDouble(contitem.pack_unit2);
			tmp_stockinp.konpouiri = Double.parseDouble(contitem.pack_unit3);
			tmp_stockinp.nebiki = Double.parseDouble(contitem.discount);
			tmp_stockinp.tanka = Double.parseDouble(contitem.unit_price);
			tmp_stockinp.tax_flg = gyousyaCdNm[gyousyaCombo.getSelectedIndex()].getZeikbn();
			tmp_stockinp.nebikiritu = Double.parseDouble(contitem.discount);
			tannkaText.setText(Sprintf.format(12, 2, tmp_stockinp.tanka)); // 単価を表示
			recalcInputArea(0, 0); // 計算・入力エリアに明細表示
			konpouText.requestFocus(); // 梱包数入力テキストに移動
			// }
		} else {
			hinbanText.requestFocus(); // 薬剤品番入力テキストに移動
		}

	}

	// 仕入区分選択時のイベント
	void kubunCombo_actionPerformed(ActionEvent e) {
		if (kubunCombo_busy == true)
			return;
		tmp_stockinp.stc_cd = kubunlst[kubunCombo.getSelectedIndex()][1];
		// 仕入のみエラーとするため、区分選択ごとに入力部を初期化
		inputInit();
		tmp_stockinp.inpItmClr(); // 品番以降の項目をクリア
		dspSiire_inputarea(tmp_stockinp); // 入力エリアにクリア明細表示
		hinbanText.requestFocus();
	}

	// 税コンボ選択
	void zeiCombo_actionPerformed(ActionEvent e) {
		if (ziiCombo_busy == true)
			return;
		if (zeiCombo.getSelectedIndex() == 0) {
			zeiText.setEnabled(true);
			recalcInputArea(1, 0);
		} else {
			zeiText.setEnabled(false);
			zeikomiText.setText("0.00");
			recalcInputArea(1, 1);
		}
	}

	void schTable_actionPerformed(ActionEvent e) {
	}

	// 仕入明細テーブル表示
	private void dspSiire_Table() {
		int i;
		int cellwidth[] = new int[50];
		for (i = 0; i < schTable.getColumnCount(); ++i) {
			cellwidth[i] = schTable.getColumnModel().getColumn(i).getPreferredWidth();
		}

		Vector vcolum = new Vector();
		Vector vdata = new Vector();
		for (i = 0; i < schTable.getColumnCount(); i++) {
			vcolum.addElement(schTable.getColumnName(i));
		}

		for (i = 0; i < vStoclist.size(); i++) {
			Stocinpmei stocinpmei = new Stocinpmei();
			StocinpmeiCopy((Stocinpmei)vStoclist.elementAt(i), stocinpmei);

			Vector vcdata = new Vector();
			if (stocinpmei.stc_id == -1) {
				vcdata.addElement("新規");
			} else {
				int j;
				for (j = 0; kubunlst.length > j; j++) {
					if (kubunlst[j][1].equals(stocinpmei.stc_cd))
						break;
				}
				if (j < kubunlst.length)
					vcdata.addElement(kubunlst[j][0]);
				else
					vcdata.addElement("");
			}
			if (down_to_decimal == null)
				down_to_decimal = "0";
			if (down_to_decimal.equals("1") == false)
				down_to_decimal = "0";

			int intDownDecimal = 2;
			if (down_to_decimal.equals("1")) {
				intDownDecimal = 0;
			}
			vcdata.addElement(stocinpmei.item_no); // 薬剤番号
			vcdata.addElement(stocinpmei.item_nm); // 薬剤名称
			vcdata.addElement(Sprintf.formatCanma(12, 3, stocinpmei.sousuu)); // 総数
			vcdata.addElement(Sprintf.formatCanma(12, 2, stocinpmei.tanka)); // 単価
			vcdata.addElement(Sprintf.formatCanma(12, intDownDecimal,
			        stocinpmei.kingaku)); // 金額
			// vcdata.addElement(Sprintf.formatCanma(12,
			// intDownDecimal,stocinpmei.nebiki)); // 値引き額
			vcdata.addElement(Sprintf.formatCanma(12, intDownDecimal,
			        stocinpmei.zeikomi)); // 税込金額
			vdata.addElement(vcdata);
		}
		schDefaultTableModel.setDataVector(vdata, vcolum);
		for (i = 0; i < schTable.getColumnCount(); ++i) {
			schTable.getColumnModel().getColumn(i).setPreferredWidth(
			        cellwidth[i]);
		}
		// テーブル右詰項目の定義
		DefaultTableCellRenderer rrend = new DefaultTableCellRenderer();
		rrend.setHorizontalAlignment(SwingConstants.RIGHT);
		schTable.getColumnModel().getColumn(3).setCellRenderer(rrend);
		schTable.getColumnModel().getColumn(4).setCellRenderer(rrend);
		schTable.getColumnModel().getColumn(5).setCellRenderer(rrend);
		schTable.getColumnModel().getColumn(6).setCellRenderer(rrend);
		// schTable.getColumnModel().getColumn(7).setCellRenderer(rrend);
	}

	// 仕入明細テーブル１明細再表示
	void dspSiire_Table_1mei(int meipoint, int mode) { // mode 0:再表示 1:明細追加表示
		Stocinpmei stocinpmei = new Stocinpmei();
		StocinpmeiCopy((Stocinpmei)vStoclist.elementAt(meipoint), stocinpmei);
		Vector vcdata = new Vector();
		if (stocinpmei.stc_id == -1) {
			vcdata.addElement("新規");
		} else {
			int j;
			for (j = 0; kubunlst.length > j; j++) {
				if (kubunlst[j][1].equals(stocinpmei.stc_cd))
					break;
			}
			if (j < kubunlst.length)
				vcdata.addElement(kubunlst[j][0]);
			else
				vcdata.addElement("");
		}
		if (down_to_decimal == null)
			down_to_decimal = "0";
		if (down_to_decimal.equals("1") == false)
			down_to_decimal = "0";

		int intDownDecimal = 2;
		if (down_to_decimal.equals("1")) {
			intDownDecimal = 0;
		}

		vcdata.addElement(stocinpmei.item_no); // 薬剤番号
		vcdata.addElement(stocinpmei.item_nm); // 薬剤名称
		vcdata.addElement(Sprintf.formatCanma(12, 3, stocinpmei.sousuu)); // 総数
		vcdata.addElement(Sprintf.formatCanma(12, 2, stocinpmei.tanka)); // 単価
		vcdata.addElement(Sprintf.formatCanma(12, intDownDecimal,
		        stocinpmei.kingaku)); // 金額
		vcdata.addElement(Sprintf.formatCanma(12, intDownDecimal,
		        stocinpmei.zeikomi)); // 税込金額

		if (mode == 0) {
			// 再表示
			for (int i = 0; i < vcdata.size(); i++) {
				schDefaultTableModel.setValueAt(vcdata.elementAt(i), meipoint,
				        i);
			}
		} else {
			// １明細追加表示
			schDefaultTableModel.addRow(vcdata);
		}
	}

	// 仕入明細入力エリア表示
	private void dspSiire_inputarea(Stocinpmei stocinpmei) {
		if (down_to_decimal == null)
			down_to_decimal = "0";
		if (down_to_decimal.equals("1") == false)
			down_to_decimal = "0";

		int intDownDecimal = 2;
		if (down_to_decimal.equals("1")) {
			intDownDecimal = 0;
		}

		// 仕入区分
		int j;
		for (j = 0; kubunlst.length > j; j++) {
			if (kubunlst[j][1].equals(stocinpmei.stc_cd))
				break;
		}
		if (j < kubunlst.length) {
			kubunCombo_busy = true;
			kubunCombo.setSelectedIndex(j);
			kubunCombo_busy = false;
		} else {
			kubunCombo_busy = true;
			kubunCombo.setSelectedIndex(0);
			kubunCombo_busy = false;
		}
		hinbanText.setText(stocinpmei.item_no); // 薬剤品番入力テキスト
		hinnmText.setText(stocinpmei.item_nm); // 薬剤品名表示テキスト
		konpouText.setText(String.valueOf(stocinpmei.konpou)); // 梱包数入力テキスト
		konpouiriText.setText(String.valueOf(stocinpmei.konpouiri)); // 梱包入数表示テキスト
		housouText.setText(String.valueOf(stocinpmei.housou)); // 包装数入力テキスト
		housouiriText.setText(String.valueOf(stocinpmei.housouiri)); // 包装入数表示テキスト
		baraText.setText(Sprintf.format(12, 3, stocinpmei.bara)); // バラ数入力テキスト
		sousuuText.setText(Sprintf.formatCanma(12, 3, stocinpmei.sousuu)); // 総数表示テキスト

		if (stock_unit_price_calc.equals("1")) {
			tannkaText.setText(Sprintf.formatCanma(12, 2, stocinpmei.tanka)); // 単価入力テキスト
			kingakuText.setText(Sprintf.format(12, intDownDecimal,
			        stocinpmei.kingaku)); // 金額表示テキスト
		} else {
			tannkaText.setText(Sprintf.format(12, 2, stocinpmei.tanka)); // 単価入力テキスト
			kingakuText.setText(Sprintf.formatCanma(12, intDownDecimal,
			        stocinpmei.kingaku)); // 金額表示テキスト
		}

		nebikiText.setText(Sprintf.format(12, intDownDecimal, stocinpmei.nebiki)); // 値引額入力テキスト
		kounyuuText.setText(Sprintf.formatCanma(12, intDownDecimal,
		        stocinpmei.kounyuugaku)); // 購入金額表示テキスト

		// 税金区分選択コンボ
		for (j = 0; zeilst.length > j; j++) {
			if (zeilst[j][1].equals(stocinpmei.tax_flg))
				break;
		}
		if (j < zeilst.length) {
			ziiCombo_busy = true;
			zeiCombo.setSelectedIndex(j);
			ziiCombo_busy = false;
		} else {
			ziiCombo_busy = true;
			zeiCombo.setSelectedIndex(0);
			ziiCombo_busy = false;
		}
		if (zeiCombo.getSelectedIndex() == 0)
			zeiText.setEnabled(true);
		else
			zeiText.setEnabled(false);

		zeiText.setText(Sprintf.format(12, intDownDecimal, stocinpmei.zeikin)); // 税金額入力テキスト
		zeikomiText.setText(Sprintf.formatCanma(12, intDownDecimal,
		        stocinpmei.zeikomi)); // 税込金額表示テキスト

		// 削除ボタン
		if (stocinpmei.stc_id == -1)
			delButton.setEnabled(false);
		else
			delButton.setEnabled(true);
	}

	// クリアボタン
	void clerButton_actionPerformed(ActionEvent e) {

		// 入力部の色を初期化
		inputInit();

		gyousyaCombo.setEnabled(true);
		slipNoText.setEnabled(true);

		yyyyText.setEnabled(true);
		mmText.setEnabled(true);
		ddText.setEnabled(true);

		searchButton.setEnabled(true);
		clerButton.setEnabled(false);
		meiButton.setEnabled(false);
		keikingakuText.setText("");
		keinebikiText.setText("");
		keikounyuuText.setText("");
		keizeiText.setText("");
		keizeikomiText.setText("");
		kubunCombo.setEnabled(false);
		kubunCombo_busy = true;
		kubunCombo.setSelectedIndex(0);
		kubunCombo_busy = false;
		hinbanText.setText("");
		hinbanText.setEnabled(false);
		hinnmText.setText("");
		konpouText.setText("");
		konpouText.setEnabled(false);
		konpouiriText.setText("");
		housouText.setText("");
		housouText.setEnabled(false);
		housouiriText.setText("");
		baraText.setText("");
		baraText.setEnabled(false);
		// barairiText.setText("");
		sousuuText.setText("");
		tannkaText.setText("");
		kingakuText.setText("");
		// 単価、金額テキスト
		if (stock_unit_price_calc.equals("1")) {
			kingakuText.setEnabled(false);
		} else {
			tannkaText.setEnabled(false);
		}
		nebikiText.setText("");
		nebikiText.setEnabled(false);
		kounyuuText.setText("");
		zeiCombo.setEnabled(false);
		ziiCombo_busy = true;
		zeiCombo.setSelectedIndex(0);
		ziiCombo_busy = false;
		zeiText.setText("");
		zeiText.setEnabled(false);
		zeikomiText.setText("");

		hinsearchButton.setEnabled(false);
		okButton.setEnabled(false);
		delButton.setEnabled(false);

		int i;
		for (i = schDefaultTableModel.getRowCount(); i > 0; --i) {
			schDefaultTableModel.removeRow(i - 1);
		}

		bSearch = false;
		bMeisai = false;
		gyousyaCombo.requestFocus(); // フォーカスを区分に移動
	}

	// 明細決定ボタン
	void meiButton_actionPerformed(ActionEvent e) {
		MsgDlg msgdlg = new MsgDlg(this);
		// 明細切替確認
		Stocinpmei stocinpmei = new Stocinpmei();
		StocinpmeiCopy((Stocinpmei)vStoclist.elementAt(edit_point), stocinpmei);
		boolean editf = false;
		if (tmp_stockinp.stc_cd.equals(stocinpmei.stc_cd) == false)
			editf = true; // 仕入区分
		if (tmp_stockinp.item_no.equals(stocinpmei.item_no) == false)
			editf = true; // 品番
		if (tmp_stockinp.konpouiri != stocinpmei.konpouiri)
			editf = true; // 梱包入数
		if (tmp_stockinp.housouiri != stocinpmei.housouiri)
			editf = true; // 包装入数
		if (tmp_stockinp.bara != stocinpmei.bara)
			editf = true; // バラ数
		if (tmp_stockinp.tanka != stocinpmei.tanka)
			editf = true; // 単価
		if (tmp_stockinp.nebiki != stocinpmei.nebiki)
			editf = true; // 値引額
		if (tmp_stockinp.tax_flg.equals(stocinpmei.tax_flg) == false)
			editf = true; // 税区分
		if (tmp_stockinp.zeikin != stocinpmei.zeikin)
			editf = true; // 消費税

		if (editf) {
			int msgsts = msgdlg.msgdsp("編集中の明細内容が失われます。\n明細を切替てよろしいですか？",
			        MsgDlg.QUESTION_MESSAGE, MsgDlg.YES_NO_OPTION);
			if (msgsts == 1) {
				schTable.changeSelection(edit_point, 0, false, false); // 元の行を選択状態とする
				return;
			}
		}
		if (schTable.getSelectedRow() >= 0) {
			edit_point = schTable.getSelectedRow(); // 編集中の明細ポイント
			// tmp_stockinp = (Stocinpmei)vStoclist.elementAt(edit_point); //
			// 入力ワーク用へコピー
			StocinpmeiCopy((Stocinpmei)vStoclist.get(edit_point), tmp_stockinp); // 入力ワーク用へコピー
			dspSiire_inputarea(tmp_stockinp); // 仕入明細入力エリア表示
			kubunCombo.requestFocus(); // フォーカスを区分に移動
		}
		// System.out.println( tmp_stockinp.item_no+";"+stocinpmei.item_no ) ;
		// 入力部の色を初期化 2004.07.09 onuki
		BizContradrug biz = new BizContradrug();
		ContItem contitem = biz.getCont_item(gyousyaCdNm[gyousyaCombo.getSelectedIndex()].getid(), tmp_stockinp.item_no);
		inputInit();
		cautionKigen(contitem.orca_med_cd, contitem.item_no, INPUT);
	}

	// 合計部分の再描画処理
	private void recalcTotalArea() {
		double keikingaku = 0.0;
		double keinebiki = 0.0;
		double keikounyuu = 0.0;
		double keizei = 0.0;
		double keizeikomi = 0.0;
		int icnt = vStoclist.size();
		for (int i = 0; i < icnt; i++) {
			Stocinpmei stocinpmei = (Stocinpmei)vStoclist.elementAt(i);
			// 返品、値引きは合計するとき減算する 04.03.29 onuki
			int iFlagMinus = 1;
			if ((stocinpmei.stc_cd.equals("9"))
			        || (stocinpmei.stc_cd.equals("5"))) {
				iFlagMinus = -1;
			}
			keikingaku += stocinpmei.kingaku * iFlagMinus;
			keinebiki += stocinpmei.nebiki * iFlagMinus;
			keikounyuu += stocinpmei.kounyuugaku * iFlagMinus;
			keizei += stocinpmei.zeikin * iFlagMinus;
			keizeikomi += stocinpmei.zeikomi * iFlagMinus;
		}
		// 04.02.09 onuki
		if (down_to_decimal == null)
			down_to_decimal = "0";
		if (down_to_decimal.equals("1") == false)
			down_to_decimal = "0";

		int intDownDecimal = 2;
		if (down_to_decimal.equals("1")) {
			intDownDecimal = 0;
		}
		keikingakuText.setText(Sprintf.formatCanma(12, intDownDecimal,
		        keikingaku));
		keinebikiText.setText(Sprintf.formatCanma(12, intDownDecimal, keinebiki));
		keikounyuuText.setText(Sprintf.formatCanma(12, intDownDecimal,
		        keikounyuu));
		keizeiText.setText(Sprintf.formatCanma(12, intDownDecimal, keizei));
		keizeikomiText.setText(Sprintf.formatCanma(12, intDownDecimal,
		        keizeikomi));
	}

	// 入力内容から、入力部分の再描画処理
	private void recalcInputArea(int nmode, int zmode) {

		// 値引動計算:nmode 0:する 1:しない, 税自動計算:zmode 0:する 1:しない
		MsgDlg msgdlg = new MsgDlg(this);
		tmp_stockinp.konpou = Long.parseLong(konpouText.getText());
		tmp_stockinp.housou = Long.parseLong(housouText.getText());
		tmp_stockinp.bara = Double.parseDouble(baraText.getText());
		tmp_stockinp.nebiki = Double.parseDouble(nebikiText.getText());
		tmp_stockinp.tax_flg = zeilst[zeiCombo.getSelectedIndex()][1];
		tmp_stockinp.zeikin = Double.parseDouble(zeiText.getText());

		double atot = (tmp_stockinp.konpou * (tmp_stockinp.konpouiri
		        * tmp_stockinp.housouiri * tmp_stockinp.barairi))
		        + (tmp_stockinp.housou * tmp_stockinp.housouiri * tmp_stockinp.barairi)
		        + (tmp_stockinp.bara * tmp_stockinp.barairi);
		tmp_stockinp.sousuu = atot;

		double kingaku = 0;
		if (down_to_decimal == null)
			down_to_decimal = "0";
		if (down_to_decimal.equals("1") == false)
			down_to_decimal = "0";

		kingaku = com.getRoundDouble(kingaku, down_to_decimal);

		if (stock_unit_price_calc.equals("1")) {
			// 単価計算
			kingaku = Double.parseDouble(kingakuText.getText());
			kingaku = com.getRoundDouble(kingaku, down_to_decimal);
			if (atot != 0) {
				// tmp_stockinp.tanka = kingaku / atot + 0.005;
				tmp_stockinp.tanka = kingaku / atot;
			}
		} else {
			// 金額計算
			tmp_stockinp.tanka = Double.parseDouble(tannkaText.getText());
			kingaku = atot * tmp_stockinp.tanka;
			kingaku = com.getRoundDouble(kingaku, down_to_decimal);

		}
		tmp_stockinp.kingaku = kingaku;

		double nebiki = tmp_stockinp.nebiki;
		if (nmode == 0) { // 値引額自動計算の場合
			nebiki = kingaku * (tmp_stockinp.nebikiritu / 100);
		}
		nebiki = com.getRoundDouble(nebiki, down_to_decimal);
		tmp_stockinp.nebiki = nebiki;

		double nebikigokingaku = kingaku - nebiki;
		tmp_stockinp.kounyuugaku = nebikigokingaku;
		double syouhizei = 0;
		if (tmp_stockinp.tax_flg.equals("1")) {
			syouhizei = tmp_stockinp.zeikin;
			if (zmode == 0) { // 消費税自動計算の場合
				syouhizei = nebikigokingaku * 0.05;
			}
		}
		syouhizei = com.getRoundDouble(syouhizei, down_to_decimal);

		tmp_stockinp.zeikin = syouhizei;
		double kounyuu = nebikigokingaku + syouhizei;
		tmp_stockinp.zeikomi = kounyuu;

		dspSiire_inputarea(tmp_stockinp); // 入力エリアに明細表示
	}

	// 梱包数ロストフォーカス
	void konpouText_focusLost(FocusEvent e) {
		if (!errorMsg)
			return;
		MsgDlg msgdlg = new MsgDlg(this);
		try {
			long konpou = Long.parseLong(konpouText.getText());
			if (tmp_stockinp.konpou == konpou)
				return; // 変化がなければなにもしない
		} catch (Exception er) {
			errorMsg = false;
			konpouText.requestFocus();
			msgdlg.msgdsp("梱包数の入力が不正です\n数値入力してください。", MsgDlg.ERROR_MESSAGE);
			errorMsg = true;
			return;
		}
		recalcInputArea(0, 0);
	}

	// 包装数ロストフォーカス
	void housouText_focusLost(FocusEvent e) {
		if (!errorMsg)
			return;
		MsgDlg msgdlg = new MsgDlg(this);
		try {
			long housou = Long.parseLong(housouText.getText());
			if (tmp_stockinp.housou == housou)
				return; // 変化がなければなにもしない
		} catch (Exception er) {
			errorMsg = false;
			housouText.requestFocus();
			msgdlg.msgdsp("包装数の入力が不正です\n数値入力してください。", MsgDlg.ERROR_MESSAGE);
			errorMsg = true;
			return;
		}
		recalcInputArea(0, 0);
	}

	// バラ数ロストフォーカス
	void baraText_focusLost(FocusEvent e) {
		if (!errorMsg)
			return;
		MsgDlg msgdlg = new MsgDlg(this);
		try {
			double bara = Double.parseDouble(baraText.getText());
			if (tmp_stockinp.bara == bara)
				return; // 変化がなければなにもしない
		} catch (Exception er) {
			errorMsg = false;
			baraText.requestFocus();
			msgdlg.msgdsp("バラ数の入力が不正です\n数値入力してください。", MsgDlg.ERROR_MESSAGE);
			errorMsg = true;
			return;
		}
		recalcInputArea(0, 0);
	}

	// 単価ロストフォーカス
	void tannkaText_focusLost(FocusEvent e) {
		if (!errorMsg)
			return;
		MsgDlg msgdlg = new MsgDlg(this);
		try {
			double tanka = Double.parseDouble(tannkaText.getText());
			if (tmp_stockinp.tanka == tanka)
				return; // 変化がなければなにもしない
		} catch (Exception er) {
			errorMsg = false;
			tannkaText.requestFocus();
			msgdlg.msgdsp("単価の入力が不正です\n数値入力してください。", MsgDlg.ERROR_MESSAGE);
			errorMsg = true;
			return;
		}
		recalcInputArea(0, 0);
	}

	// 金額ロストフォーカス
	void kingakuText_focusLost(FocusEvent e) {
		if (!errorMsg)
			return;
		MsgDlg msgdlg = new MsgDlg(this);
		try {
			double kingaku = Double.parseDouble(kingakuText.getText());
			if (tmp_stockinp.kingaku == kingaku)
				return; // 変化がなければなにもしない
			// 値引きのみ、数量なしでも可
			if ((tmp_stockinp.sousuu == 0)
			        && ((tmp_stockinp.stc_cd.equals("1")) || (tmp_stockinp.stc_cd.equals("9")))) {
				errorMsg = false;
				baraText.requestFocus();
				msgdlg.msgdsp("数量が入力されていません。", MsgDlg.ERROR_MESSAGE);
				kingakuText.setText("0");
				errorMsg = true;
				return;
			}
		} catch (Exception er) {
			errorMsg = false;
			kingakuText.requestFocus();
			msgdlg.msgdsp("金額の入力が不正です\n数値入力してください。", MsgDlg.ERROR_MESSAGE);
			errorMsg = true;
			return;
		}
		recalcInputArea(0, 0);
	}

	// 値引きロストフォーカス
	void nebikiText_focusLost(FocusEvent e) {
		if (!errorMsg)
			return;
		MsgDlg msgdlg = new MsgDlg(this);
		try {
			double nebiki = Double.parseDouble(nebikiText.getText());
			if (tmp_stockinp.nebiki == nebiki)
				return; // 変化がなければなにもしない
		} catch (Exception er) {
			errorMsg = false;
			nebikiText.requestFocus();
			msgdlg.msgdsp("値引きの入力が不正です\n数値入力してください。", MsgDlg.ERROR_MESSAGE);
			errorMsg = true;
			return;
		}
		recalcInputArea(1, 0);
	}

	// 税金ロストフォーカス
	void zeiText_focusLost(FocusEvent e) {
		if (!errorMsg)
			return;
		MsgDlg msgdlg = new MsgDlg(this);
		try {
			double zeikin = Double.parseDouble(zeiText.getText());
			if (tmp_stockinp.zeikin == zeikin)
				return; // 変化がなければなにもしない
		} catch (Exception er) {
			errorMsg = false;
			zeiText.requestFocus();
			msgdlg.msgdsp("消費税の入力が不正です\n数値入力してください。", MsgDlg.ERROR_MESSAGE);
			errorMsg = true;
			return;
		}
		recalcInputArea(1, 1);
	}

	// 品番のロストフォーカス
	void hinbanText_focusLost(FocusEvent e) {
		MsgDlg msgdlg = new MsgDlg(this);

		String itemNo = hinbanText.getText();
		if (tmp_stockinp.item_no.equals(itemNo) == false) {
			if (itemNo.length() == 0) {
				tmp_stockinp.inpItmClr(); // 品番以降の項目をクリア
				dspSiire_inputarea(tmp_stockinp); // 入力エリアにクリア明細表示
			} else {
				BizContradrug biz = new BizContradrug();
				ContItem contitem = biz.getCont_item(gyousyaCdNm[gyousyaCombo.getSelectedIndex()].getid(), itemNo);
				if (contitem == null) {
					// 品番が見付からない→短縮番号検索
					String orcaMedCd = biz.get_orcaMedCd_from_itemNo(itemNo);
					itemNo = biz.get_itemNo_orcaMedCd(orcaMedCd);
					// System.out.println(itemNo) ;
					contitem = biz.getCont_item(gyousyaCdNm[gyousyaCombo.getSelectedIndex()].getid(), itemNo);
				}
				// 短縮番号もない→薬剤未登録表示
				if (contitem == null) {
					hinbanText.setText("");
					msgdlg.msgdsp("薬剤が登録されていません。", MsgDlg.ERROR_MESSAGE);
					tmp_stockinp.inpItmClr(); // 品番以降の項目をクリア
					dspSiire_inputarea(tmp_stockinp); // 入力エリアにクリア明細表示
					hinbanText.requestFocus();
					return;
				} else {
					// システム日付と薬剤期限を比較
					if (cautionKigen(contitem.orca_med_cd, itemNo, INPUT) == false) {
						inputInit();
					}
					tmp_stockinp.item_no = contitem.item_no;
					tmp_stockinp.item_nm = contitem.med_nm;
					tmp_stockinp.barairi = Double.parseDouble(contitem.pack_unit1);
					tmp_stockinp.housouiri = Double.parseDouble(contitem.pack_unit2);
					tmp_stockinp.konpouiri = Double.parseDouble(contitem.pack_unit3);
					tmp_stockinp.nebiki = Double.parseDouble(contitem.discount);
					tmp_stockinp.tanka = Double.parseDouble(contitem.unit_price);
					tmp_stockinp.tax_flg = gyousyaCdNm[gyousyaCombo.getSelectedIndex()].getZeikbn();
					tmp_stockinp.nebikiritu = Double.parseDouble(contitem.discount);
					tannkaText.setText(Sprintf.format(12, 2, tmp_stockinp.tanka)); // 単価を表示
					recalcInputArea(0, 0); // 計算・入力エリアに明細表示
				}
			}
		}
	}

	void yyyyText_focusGained(FocusEvent e) {
		yyyyText.selectAll();
	}

	void mmText_focusGained(FocusEvent e) {
		mmText.selectAll();
	}

	void ddText_focusGained(FocusEvent e) {
		ddText.selectAll();
	}

	void hinbanText_focusGained(FocusEvent e) {
		hinbanText.selectAll();
	}

	void konpouText_focusGained(FocusEvent e) {
		konpouText.selectAll();
	}

	void housouText_focusGained(FocusEvent e) {
		housouText.selectAll();
	}

	void baraText_focusGained(FocusEvent e) {
		baraText.selectAll();
	}

	void tannkaText_focusGained(FocusEvent e) {
		tannkaText.selectAll();
	}

	void kingakuText_focusGained(FocusEvent e) {
		kingakuText.selectAll();
	}

	void nebikiText_focusGained(FocusEvent e) {
		nebikiText.selectAll();
	}

	void zeiText_focusGained(FocusEvent e) {
		zeiText.selectAll();
	}

	private void StocinpmeiCopy(Stocinpmei a, Stocinpmei b) {
		b.stc_id = a.stc_id; // 仕入ＮＯ
		b.stc_cd = a.stc_cd; // 仕入区分
		b.item_no = a.item_no; // 品番
		b.item_nm = a.item_nm; // 品名
		b.konpou = a.konpou; // 梱包数
		b.konpouiri = a.konpouiri; // 梱包入数
		b.housou = a.housou; // 包装数
		b.housouiri = a.housouiri; // 包装入数
		b.bara = a.bara; // バラ数
		b.barairi = a.barairi; // バラ入数
		b.sousuu = a.sousuu; // 総数
		b.tanka = a.tanka; // 単価
		b.kingaku = a.kingaku; // 金額
		b.nebiki = a.nebiki; // 値引額
		b.kounyuugaku = a.kounyuugaku; // 購入額
		b.tax_flg = a.tax_flg; // 税区分
		b.zeikin = a.zeikin; // 消費税
		b.zeikomi = a.zeikomi; // 税込金額
		b.nebikiritu = a.nebikiritu; // 値引率
	}

	// 返品などのマイナス用変換クラス
	private void StocinpmeiCopy_minus(Stocinpmei a, Stocinpmei b) {
		b.stc_id = a.stc_id; // 仕入ＮＯ
		b.stc_cd = a.stc_cd; // 仕入区分
		b.item_no = a.item_no; // 品番
		b.item_nm = a.item_nm; // 品名
		b.konpou = a.konpou; // 梱包数
		b.konpouiri = a.konpouiri; // 梱包入数
		b.housou = a.housou; // 包装数
		b.housouiri = a.housouiri; // 包装入数
		b.bara = a.bara; // バラ数
		b.barairi = a.barairi; // バラ入数
		b.sousuu = a.sousuu; // 総数
		b.tanka = a.tanka; // 単価
		b.kingaku = a.kingaku; // 金額
		b.nebiki = a.nebiki; // 値引額
		b.kounyuugaku = a.kounyuugaku; // 購入額
		b.tax_flg = a.tax_flg; // 税区分
		b.zeikin = a.zeikin; // 消費税
		b.zeikomi = a.zeikomi; // 税込金額
		b.nebikiritu = a.nebikiritu; // 値引率

		if ((b.stc_cd.equals("5")) || (b.stc_cd.equals("9"))) {
			// 仕入区分：返品or値引きの場合 数量金額に× -1
			if (b.konpou != 0)
				b.konpou *= -1; // 梱包数
			if (b.housou != 0)
				b.housou *= -1; // 包装数
			if (b.bara != 0)
				b.bara *= -1; // バラ数
			if (b.sousuu != 0)
				b.sousuu *= -1; // 総数
			if (b.kingaku != 0)
				b.kingaku *= -1; // 金額
			if (b.nebiki != 0)
				b.nebiki *= -1; // 値引額
			if (b.kounyuugaku != 0)
				b.kounyuugaku *= -1; // 購入額
			if (b.zeikin != 0)
				b.zeikin *= -1; // 消費税
			if (b.zeikomi != 0)
				b.zeikomi *= -1; // 税込金額
		}
	}

	// 薬剤の有効期限が終了していたら、注意を促す
	private boolean cautionKigen(String orca_med_cd, String itemNo, int flag) {
		boolean caution = false;
		MsgDlg msgdlg = new MsgDlg(this);
		BizContradrug biz = new BizContradrug();

		OrcaMedicine orcamed = biz.getOrca_medicine(orca_med_cd);
		int orcamedInt = Integer.parseInt(orcamed.day_to);
		SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
		int dateInt = Integer.parseInt(f.format(new Date()));
		// System.out.println(kubunlst[kubunCombo.getSelectedIndex()][1]) ;

		if (dateInt > orcamedInt)
		// &&(kubunlst[kubunCombo.getSelectedIndex()][1].equals("1")) )
		{
			caution = true;
			if (flag == INPUT)
				inputYellow();
		}
		return caution;
	}

	private void inputYellow() {
		hinbanText.setBackground(Color.yellow);
		hinnmText.setBackground(Color.yellow);
		jLabelSarchOver.setVisible(true);
	}

	private void inputInit() {
		hinbanText.setBackground(Color.white);
		hinnmText.setBackground(Color.lightGray);
		jLabelSarchOver.setVisible(false);
	}

	public void paint(Graphics g) {
		if (firstFocusComponent != null) {
			firstFocusComponent.requestFocus();
			firstFocusComponent = null;
		}
		super.paint(g);
	}

}
