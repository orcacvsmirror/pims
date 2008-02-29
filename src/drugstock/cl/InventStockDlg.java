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
import drugstock.biz.BizInventStock;
import drugstock.cmn.Common;
import drugstock.cmn.DefaultJDialog;
import drugstock.cmn.FocusTraversalPolicyOrder;
import drugstock.cmn.PropRead;
import drugstock.cmn.Sprintf;
import drugstock.cmn.MsgDlg;
import drugstock.component.BButton;
import com.borland.jbcl.layout.XYConstraints;
import com.borland.jbcl.layout.XYLayout;
import drugstock.model.CodeName;
import drugstock.model.ContItem;
import drugstock.model.InventStockFactor;
import drugstock.model.SyuruiCdNm;

/**
 * 「棚卸処理」画面処理
 */

public class InventStockDlg extends DefaultJDialog {

	// パネルが開かれた時に最初にフォーカスを当てるコンポーネント
	protected Component firstFocusComponent = null;
	Common com = new Common();

	class InventStockTmp {

		public String yyyymm; // 年月
		// public String cont_nm; // 業者名
		public String item_no; // 薬剤品番
		public String med_nm; // 薬剤名
		public double stock_theory; // 理論在庫
		public double stock_truth; // 実在庫
		public String flag_truth; // 実在庫フラグ

		public InventStockTmp() {
		};

		public InventStockTmp(String yyyymm, // 年月
		        // String cont_nm, // 業者名
		        String item_no, // 薬剤品番
		        String med_nm, // 薬剤名
		        double stock_theory, // 理論在庫
		        double stock_truth, // 実在庫
		        String flag_truth // 実在庫フラグ
		) {

			this.yyyymm = yyyymm; // 年月
			// this.cont_nm = cont_nm; // 業者名
			this.item_no = item_no; // 薬剤品番
			this.med_nm = med_nm; // 薬剤名
			this.stock_theory = stock_theory; // 理論在庫
			this.stock_truth = stock_truth; // 実在庫
			this.flag_truth = flag_truth; // 実在庫フラグ

		}
	}

	// 画面入力用ワークエリア
	private boolean errorMsg = true;

	// 検索フラグ
	private boolean bSearch = false;
	// 明細フラグ
	private boolean bMeisai = false;

	private boolean gyousyaCombo_busy = false; // 業者選択コンボ アイテム設定中
	private boolean syuruiCombo_busy = false; // 薬剤種類コンボ アイテム設定中
	private CodeName gyousyaCdNm[] = null; // 業者コンボのコード、ネーム
	public SyuruiCdNm syuruiCdNm[] = null; // 薬剤種類コンボのコード、ネーム
	private Vector vStoclist = new Vector(); // 仕入明細
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
	private JComboBox syuruiCombo = new JComboBox();
	private BButton searchButton = new BButton();
	private JTextField syuruicdText = new JTextField();

	// public final static int CONT_NAME = 0; // 業者名
	public final static int MED_NO = 0; // 薬剤番号
	public final static int MED_NAME = 1; // 薬剤名
	public final static int STOCK_THEORY = 2; // 理論在庫
	public final static int STOCK_TRUTH = 3; // 実在庫
	public final static int TRUTH_FLAG = 4; // 実在庫フラグ
	public final static int INPUT_STOCK = 5; // 実在庫入力値

	private DefaultTableModel schDefaultTableModel = new DefaultTableModel() {

		// 表項目の実在庫以外入力不可 04.03.23 onuki
		public boolean isCellEditable(int row, int column) {
			if (column != INPUT_STOCK)
				return false;
			else
				return true;
		}
	};

	private JTable schTable = new JTable(schDefaultTableModel);
	private JScrollPane schScrPane = new JScrollPane(schTable);
	private BButton clerButton = new BButton();
	private JLabel jLabel1 = new JLabel();
	private JLabel jLabel5 = new JLabel();
	private JLabel jLabel8 = new JLabel();
	private JLabel jLabel17 = new JLabel();
	private JTextField yyyyText = new JTextField();
	private JTextField mmText = new JTextField();

	// 移動 04.02.09 onuki
	// 小数点以下の処理変数をファイルから読み込み
	PropRead prop = new PropRead();
	String down_to_decimal = prop.getProp("down_to_decimal");

	//

	public InventStockDlg(Frame frame, String title, boolean modal) {
		super(frame, title, modal);

		try {
			this.setTitle(title);
			jbInit();
			pack();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public InventStockDlg() {
		this(null, "棚卸処理", false);
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

		// 確定ボタン
		okButton.setFont(new Font("Dialog", 0, 16));
		okButton.setText("登録");
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
		xYLayout2.setHeight(587);
		syuruiCombo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				syuruiCombo_actionPerformed(e);
			}
		});
		jLabel1.setFont(new Font("Dialog", 0, 16));
		jLabel1.setText("薬剤区分");

		// String[] column_hed = {"業者", "薬剤品番", "薬剤品名",
		// "理論在庫","実在庫","登録済","入力"};
		String[] column_hed = { "薬剤品番", "薬剤品名", "理論在庫", "実在庫", "登録済", "入力" };

		int[] column_width = { 20, 250, 40, 40, 5, 40 };
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
		jLabel5.setText("入力年月");
		// 日付テキスト年
		yyyyText.setDisabledTextColor(Color.black);
		yyyyText.setFont(new Font("Dialog", 0, 16));
		yyyyText.addFocusListener(new FocusAdapter() {

			public void focusGained(FocusEvent e) {
				yyyyText_focusGained(e);
			}
		});
		yyyyText.setText("");

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

		// 日付テキスト月
		mmText.setDisabledTextColor(Color.black);
		mmText.setText("");
		mmText.setFont(new Font("Dialog", 0, 16));
		mmText.addFocusListener(new FocusAdapter() {

			public void focusGained(FocusEvent e) {
				mmText_focusGained(e);
			}
		});

		jLabel8.setText("年");
		jLabel8.setFont(new Font("Dialog", 0, 16));
		jLabel17.setText("月");
		jLabel17.setFont(new Font("Dialog", 0, 16));
		// 業者、日付
		this.getContentPane().add(jLabel1, new XYConstraints(38, 19, 80, -1));
		this.getContentPane().add(syuruiCombo,
		        new XYConstraints(110, 17, 120, 23));
		this.getContentPane().add(jLabel5, new XYConstraints(323, 19, 80, -1));
		this.getContentPane().add(yyyyText, new XYConstraints(402, 17, 44, 23));
		this.getContentPane().add(jLabel8, new XYConstraints(452, 19, 22, -1));
		this.getContentPane().add(mmText, new XYConstraints(478, 17, 31, 23));
		this.getContentPane().add(jLabel17, new XYConstraints(514, 19, 22, -1));
		// ボタン１
		this.getContentPane().add(searchButton,
		        new XYConstraints(619, 10, 97, 38));
		this.getContentPane().add(clerButton,
		        new XYConstraints(729, 10, 97, 38));
		// 表
		this.getContentPane().add(schScrPane,
		        new XYConstraints(29, 56, 870, 480));
		// ボタン２
		this.getContentPane().add(okButton, new XYConstraints(681, 540, 97, 38));
		this.getContentPane().add(cancelButton,
		        new XYConstraints(793, 539, 97, 38));

		// Tab フォーカス移動制御
		Component order[] = new Component[] { syuruiCombo, yyyyText, mmText,
		        searchButton, clerButton, okButton, cancelButton };
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
		BizContradrug bizsyurui = new BizContradrug();
		syuruiCdNm = bizsyurui.getMed_kind_list();
		syuruiCombo.removeAllItems();
		if (syuruiCdNm != null) {
			for (int i = 0; i < syuruiCdNm.length; i++) {
				syuruiCombo.addItem(syuruiCdNm[i].name);
			}
		}

		// 日付テキストにシステム日付を表示
		SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
		yyyyText.setText(f.format(new Date()).substring(0, 4));
		mmText.setText(f.format(new Date()).substring(4, 6));

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

	// 確定ボタン
	void okButton_actionPerformed(ActionEvent e) {
		MsgDlg msgdlg = new MsgDlg(this);
		// 取消確認
		int msgsts = msgdlg.msgdsp("入力内容を登録します。", MsgDlg.QUESTION_MESSAGE,
		        MsgDlg.YES_NO_OPTION);
		if (msgsts == 1)
			return;

		// 仕入月
		String stc_yyyymm = yyyyText.getText();
		if (mmText.getText().length() < 2)
			stc_yyyymm = stc_yyyymm + "0" + mmText.getText();
		else
			stc_yyyymm = stc_yyyymm + mmText.getText();

		int maxTblRow = schTable.getRowCount();
		String medTblValue[] = new String[maxTblRow];
		String inputTblValue[] = new String[maxTblRow];
		InventStockTmp inventStockTmp = new InventStockTmp();
		int i = 0;

		try {

			if (maxTblRow >= 0) {
				for (i = 0; i < maxTblRow; i++) {
					medTblValue[i] = (String)schTable.getValueAt(i, MED_NO);
					String tmp = (String)schTable.getValueAt(i, INPUT_STOCK);

					errorMsg = false;
					if (tmp.equals("")) {
						inputTblValue[i] = "----";// 空白：UPDATEなし
					} else {
						// チェック処理
						double tmpB = Double.parseDouble(tmp);
						if (Double.isNaN(tmpB)) {
							msgdlg.msgdsp("入力が不正です\n数値入力してください。",
							        MsgDlg.ERROR_MESSAGE);
							errorMsg = true;
						} else {
							inputTblValue[i] = Sprintf.format(12, 3, tmpB);
						}
					}
					if (errorMsg)
						break;

				}
				edit_point = schTable.getSelectedRow(); // 編集中の明細ポイント
				BizInventStock biz = new BizInventStock();
				biz.inputInventStock(medTblValue, inputTblValue, stc_yyyymm);

				initProcess();

				searchButton.requestFocus(); // フォーカスを区分に移動
			}

		} catch (NumberFormatException cer) {
			msgdlg.msgdsp("入力が不正です\n数値入力してください。", MsgDlg.ERROR_MESSAGE);
			// 作業位置：エラー箇所にカーソル移動
			schTable.changeSelection(i, INPUT_STOCK, false, false);
			return;
		}

	}

	// 戻るボタン
	void cancelButton_actionPerformed(ActionEvent e) {
		// 戻り確認；表の行数が０以外ならば確認画面表示
		if (schTable.getRowCount() != 0) {
			MsgDlg msgdlg = new MsgDlg(this);
			int msgsts = msgdlg.msgdsp("入力内容をクリアし、メニューに戻ります。よろしいですか？",
			        MsgDlg.QUESTION_MESSAGE, MsgDlg.YES_NO_OPTION);
			if (msgsts == 1)
				return;
		}
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
		} catch (StringIndexOutOfBoundsException ser) {
			msgdlg.msgdsp("日付を正しく入力してください。", MsgDlg.ERROR_MESSAGE);
			yyyyText.requestFocus();
			return;
		}
		int iy;
		int im;
		try {
			iy = Integer.parseInt(sy);
			im = Integer.parseInt(sm);
		} catch (NumberFormatException ier) {
			msgdlg.msgdsp("日付を正しく入力してください。", MsgDlg.ERROR_MESSAGE);
			yyyyText.requestFocus();
			return;
		}
		try {
			Calendar cal = Calendar.getInstance();
			cal.setLenient(false);
			cal.get(Calendar.YEAR);
		} catch (IllegalArgumentException cer) {
			msgdlg.msgdsp("日付を正しく入力してください。", MsgDlg.ERROR_MESSAGE);
			yyyyText.requestFocus();
			return;
		}

		syuruiCombo.setEnabled(false);
		yyyyText.setEnabled(false); // 日付テキスト
		mmText.setEnabled(false);
		searchButton.setEnabled(false); // 決定ボタン
		clerButton.setEnabled(true); // クリアボタン
		okButton.setEnabled(true); // 確定ボタン

		// 明細格納エリアをクリア
		vStoclist = new Vector();
		// 仕入明細リストの取得
		// 仕入日
		String stc_month_srach = yyyyText.getText();
		if (mmText.getText().length() < 2)
			stc_month_srach = stc_month_srach + "0" + mmText.getText();
		else
			stc_month_srach = stc_month_srach + mmText.getText();

		String cont_id_srach = gyousyaCdNm[gyousyaCombo.getSelectedIndex()].getid(); // 業者区分
		String med_syurui_srach = syuruiCdNm[syuruiCombo.getSelectedIndex()].code; // 薬剤種類
		BizInventStock bizI = new BizInventStock();
		InventStockFactor inventStockFactor[] = bizI.getInventStockFactor(
		        med_syurui_srach, stc_month_srach);
		// 仕入明細を明細格納エリアに格納
		if (inventStockFactor != null) {
			for (int i = 0; inventStockFactor.length > i; i++) {
				if (true) {
					// 明細セーブ用のワークを生成
					String yyyymm = inventStockFactor[i].yyyymm;
					// String cont_nm = inventStockFactor[i].cont_nm; // 業者コード
					String item_no = inventStockFactor[i].item_no; // 品番
					String med_nm = inventStockFactor[i].med_nm; // 品名
					double stock_theory = Double.parseDouble(inventStockFactor[i].stock_theory);// 理論在庫
					double stock_truth = Double.parseDouble(inventStockFactor[i].stock_truth); // 実在庫
					String flag_truth = inventStockFactor[i].flag_truth; // 実在庫フラグ

					InventStockTmp wk = new InventStockTmp(yyyymm, // 年月
					        // cont_nm, // 業者
					        item_no, // 品番
					        med_nm, // 品名
					        stock_theory, // 理論在庫
					        stock_truth, // 実在庫
					        flag_truth // 実在庫フラグ
					);
					vStoclist.addElement(wk);
				}
			}
		}

		dspSiire_TableInvent();
		edit_point = vStoclist.size() - 1; // 編集対象の明細位置を退避
		// 決定後、表先頭にフォーカス移動 04.03.22 onuki
		firstFocusComponent = schScrPane; // フォーカスを表に移動
		schTable.changeSelection(0, INPUT_STOCK, false, false); // 表内フォーカスを先頭行に移動

		bSearch = true;
	}

	// 種類コンボセット
	void syuruiCombo_actionPerformed(ActionEvent e) {
		if (syuruiCombo_busy == true)
			return;
		int i = syuruiCombo.getSelectedIndex();
	}

	// 品番検索ボタン
	void hinsearchButton_actionPerformed(ActionEvent e) {
		ContradrugsearchDlg dlg = new ContradrugsearchDlg();
		dlg.setInitmode(1, gyousyaCdNm[gyousyaCombo.getSelectedIndex()].getid());
		// ,gyousyacdText.getText(),(String)gyousyaCombo.getSelectedItem());
		// dlg.setInitmode(1,gyousyaCdNm[gyousyaCombo.getSelectedIndex()].getid(),"01",
		// (String)gyousyaCombo.getSelectedItem());
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
		}

	}

	void schTable_actionPerformed(ActionEvent e) {
	}

	// 棚卸明細テーブル表示 04.03.19 onuki
	void dspSiire_TableInvent() {
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

		InventStockTmp inventStockTmp = new InventStockTmp();
		String tmp_flag_truth[] = new String[vStoclist.size()];

		for (i = 0; i < vStoclist.size(); i++) {
			// InventStockTmp inventStockTmp = new InventStockTmp();
			InventStockCopy((InventStockTmp)vStoclist.elementAt(i),
			        inventStockTmp);
			tmp_flag_truth[i] = inventStockTmp.flag_truth;

			Vector vcdata = new Vector();
			vcdata.addElement(inventStockTmp.item_no); // 薬剤番号
			vcdata.addElement(inventStockTmp.med_nm); // 薬剤名称
			vcdata.addElement(Sprintf.formatCanma(12, 3,
			        inventStockTmp.stock_theory)); // 理論在庫
			vcdata.addElement(Sprintf.formatCanma(12, 3,
			        inventStockTmp.stock_truth)); // 実在庫
			if (inventStockTmp.flag_truth.equals("1")) {// 実在庫フラグ
				vcdata.addElement("○"); // 登録済
			} else {
				vcdata.addElement("×"); // 未登録
			}

			vcdata.addElement(""); // 実在庫入力
			vdata.addElement(vcdata);
		}
		schDefaultTableModel.setDataVector(vdata, vcolum);
		for (i = 0; i < schTable.getColumnCount(); ++i) {
			schTable.getColumnModel().getColumn(i).setPreferredWidth(
			        cellwidth[i]);
		}
		// テーブル右詰項目の定義
		DefaultTableCellRenderer rrend = new DefaultTableCellRenderer();
		DefaultTableCellRenderer crend = new DefaultTableCellRenderer();
		crend.setHorizontalAlignment(SwingConstants.CENTER);
		schTable.getColumnModel().getColumn(STOCK_THEORY).setCellRenderer(rrend);
		schTable.getColumnModel().getColumn(STOCK_TRUTH).setCellRenderer(rrend);
		schTable.getColumnModel().getColumn(TRUTH_FLAG).setCellRenderer(crend);
		schTable.getColumnModel().getColumn(INPUT_STOCK).setCellRenderer(rrend);

	}

	// 仕入明細テーブル表示
	void dspSiire_Table() {
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

			Vector vcdata = new Vector();
			// 04.02.09 onuki
			if (down_to_decimal == null)
				down_to_decimal = "0";
			if (down_to_decimal.equals("1") == false)
				down_to_decimal = "0";

			int intDownDecimal = 2;
			if (down_to_decimal.equals("1")) {
				intDownDecimal = 0;
			}
			//

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

	}

	// 取消ボタン
	void clerButton_actionPerformed(ActionEvent e) {
		// gyousyaCombo.setEnabled(true);
		// 取消確認
		MsgDlg msgdlg = new MsgDlg(this);
		int msgsts = msgdlg.msgdsp("入力内容をクリアします。よろしいですか？",
		        MsgDlg.QUESTION_MESSAGE, MsgDlg.YES_NO_OPTION);
		if (msgsts == 1)
			return;

		initProcess();
	}

	// 初期化処理
	void initProcess() {

		syuruiCombo.setEnabled(true);

		yyyyText.setEnabled(true);
		mmText.setEnabled(true);

		searchButton.setEnabled(true);
		clerButton.setEnabled(false);
		okButton.setEnabled(false);

		int i;
		for (i = schDefaultTableModel.getRowCount(); i > 0; --i) {
			schDefaultTableModel.removeRow(i - 1);
		}

		bSearch = false;
		bMeisai = false;
	}

	// 入力内容から、入力部分の再描画処理
	void recalcTotalArea() {
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

	void yyyyText_focusGained(FocusEvent e) {
		yyyyText.selectAll();
	}

	void mmText_focusGained(FocusEvent e) {
		mmText.selectAll();
	}

	void InventStockCopy(InventStockTmp a, InventStockTmp b) {
		b.yyyymm = a.yyyymm; // 年月
		// b.cont_nm = a.cont_nm; // 業者コード
		b.item_no = a.item_no; // 薬剤品番
		b.med_nm = a.med_nm; // 薬剤名
		b.stock_theory = a.stock_theory; // 理論在庫
		b.stock_truth = a.stock_truth; // 実在庫
		b.flag_truth = a.flag_truth; // 実在庫フラグ
	}

	public void paint(Graphics g) {
		if (firstFocusComponent != null) {
			firstFocusComponent.requestFocus();
			firstFocusComponent = null;
		}
		super.paint(g);
	}

}
