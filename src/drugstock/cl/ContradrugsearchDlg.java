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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;


import drugstock.biz.BizContradrug;
import drugstock.cmn.DefaultJDialog;
import drugstock.cmn.FocusTraversalPolicyOrder;
import drugstock.cmn.MsgDlg;
import drugstock.component.BButton;
import com.borland.jbcl.layout.XYConstraints;
import com.borland.jbcl.layout.XYLayout;
import drugstock.model.ContItem;
import drugstock.model.OrcaMedicine;
import drugstock.model.SyuruiCdNm;

/**
 * <p>
 * 「薬剤設定」内「薬剤検索」画面処理
 * </p>
 */

public class ContradrugsearchDlg extends DefaultJDialog {

	private int iSearchMode = 0; // 0:新規用 1:修正用 2:削除用
	private String sGyousyaid = null; // 業者ID
	private String sGyousyacd = null; // 業者CD
	private String sGyousyanm = null; // 業者名
	private String sKakuteimode = null; // 確定モード "ok"、"cancel"
	private String sKakuteicd = null; // 確定コード 日レセ薬剤CDまたは品番

	SyuruiCdNm syuruiCdNm[] = null; // 薬剤類コンボのコード、ネーム
	ContItem contitem[] = null; // 業種別薬剤品目のリスト
	OrcaMedicine orcamedicine[] = null; // 日レセ薬剤のリスト

	private XYLayout xYLayout2 = new XYLayout();
	private ButtonGroup taxGrp = new ButtonGroup();
	private BButton okButton = new BButton();
	private BButton cancelButton = new BButton();
	private TitledBorder titledBorder1;
	private TitledBorder titledBorder2;
	private ButtonGroup modeGrp = new ButtonGroup();
	private JComboBox syuruiCombo = new JComboBox();
	private JLabel jLabel2 = new JLabel();
	private JTextField kananmText = new JTextField();
	private BButton searchButton = new BButton();
	private JLabel jLabel3 = new JLabel();
	private JLabel jLabel4 = new JLabel();
	private JTextField cdText = new JTextField();
	private JLabel cdnmjLabel = new JLabel();
	private JLabel jLabel1 = new JLabel();
	private JTextField gyousyacdText = new JTextField();
	private JTextField gyousyanmText = new JTextField();

	private DefaultTableModel schDefaultTableModel = new DefaultTableModel();
	private JTable schTable = new JTable(schDefaultTableModel);
	private JScrollPane schScrPane = new JScrollPane(schTable);
	private JLabel jLabelSarchOver1 = new JLabel();
	private JLabel jLabelSarchOver2 = new JLabel();
	// 薬剤操作モード 04.04.02 onuki
	private static int MODE_NEW = 0; // 新規
	private static int MODE_EXT = 1; // 修正
	private static int MODE_DEL = 2; // 削除
	private static int MODE_ORG_NEW = 3; // 独自薬剤・新規
	private static int MODE_ORG_EXT = 4; // 独自薬剤・修正
	private static int MODE_ORG_DEL = 5; // 独自薬剤・削除
	private static int MODE_INPUT = 6; // 伝票入力／院内処理

	public ContradrugsearchDlg(Frame frame, String title, boolean modal) {
		super(frame, title, modal);

		try {
			this.setTitle(title);
			jbInit();
			pack();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public ContradrugsearchDlg() {
		this(null, "業者別薬剤登録：薬剤検索", false);
	}

	/**
	 * 処理モードをもとに、薬剤選択処理を開始します。
	 * 
	 * @param searchMode
	 *            処理モード
	 */
	public void setInitmode(int searchMode, String gyoushaID) {

		iSearchMode = searchMode; // 0:新規用 1:修正用 2:削除用
		// 独自薬剤追加 3:新規用 4:修正用 5:削除用
		sGyousyaid = gyoushaID;
		String title = this.getTitle();
		if ((iSearchMode == MODE_NEW) || (iSearchMode == MODE_ORG_NEW)) {
			title = title + "（新規）";
			cdnmjLabel.setText("日レセ薬剤CD");
			String[] column_hed = { "日レセCD", "名称", "種類", "有効終了日" };
			schDefaultTableModel.setColumnIdentifiers(column_hed);
			schTable.getColumnModel().getColumn(0).setPreferredWidth(50);
			schTable.getColumnModel().getColumn(1).setPreferredWidth(170);
			schTable.getColumnModel().getColumn(2).setPreferredWidth(5);
			schTable.getColumnModel().getColumn(3).setPreferredWidth(40);
		} else {
			if ((iSearchMode == MODE_EXT) || (iSearchMode == MODE_ORG_EXT)) {
				title = title + "（修正）";
			} else if ((iSearchMode == MODE_DEL)
			        || (iSearchMode == MODE_ORG_DEL)) {
				title = title + "（削除）";
			} else if (iSearchMode == MODE_INPUT) {
				title = title + "（入力）";
			}
			cdnmjLabel.setText("品番");
			String[] column_hed = { "品番", "名称", "種類" };
			schDefaultTableModel.setColumnIdentifiers(column_hed);

			TableColumn column = null;
			schTable.getColumnModel().getColumn(0).setPreferredWidth(50);
			schTable.getColumnModel().getColumn(1).setPreferredWidth(150);
			schTable.getColumnModel().getColumn(2).setPreferredWidth(30);
		}
		this.setTitle(title);
	}

	/**
	 * 薬剤選択処理でのボタン押下を検出します。
	 * 
	 * @param 薬剤を選択して「確定」ボタンが押下されれば"ok"、
	 *            「戻る」押下、または薬剤を選択せず「確定」ボタンが押下されれば"cancel"を返します。
	 */
	public String getKakuteimode() {
		return sKakuteimode;
	}

	/**
	 * 薬剤選択処理で選択された薬剤情報を返します。
	 * 
	 * @param 薬剤情報のオブジェクトを返します。
	 */
	public Object getKakuteiDat() {
		Object retobj = null;
		if (schTable.getSelectedRow() >= 0) {
			if ((iSearchMode == MODE_NEW) || (iSearchMode == MODE_ORG_NEW)) {
				// 新規
				retobj = orcamedicine[schTable.getSelectedRow()];
			} else {
				// 修正,削除,入力
				retobj = contitem[schTable.getSelectedRow()];
			}
		}
		return retobj;
	}

	private void jbInit() throws Exception {
		int contrH = -30;
		titledBorder1 = new TitledBorder(BorderFactory.createEtchedBorder(
		        Color.white, new Color(148, 145, 140)), "処理区分");
		titledBorder2 = new TitledBorder(BorderFactory.createEtchedBorder(
		        Color.white, new Color(148, 145, 140)), "検索条件");

		jLabel2.setToolTipText("");
		jLabel2.setFont(new Font("Dialog", 0, 16));
		jLabel2.setBorder(titledBorder2);
		kananmText.setFont(new Font("Dialog", 0, 16));
		kananmText.setText("");
		searchButton.setFont(new Font("Dialog", 0, 16));
		searchButton.setText("検索");
		searchButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				searchButton_actionPerformed(e);
			}
		});
		jLabel3.setFont(new Font("Dialog", 0, 16));
		jLabel3.setText("名称部分検索");
		jLabel4.setFont(new Font("Dialog", 0, 16));
		jLabel4.setText("種類");
		cdText.setFont(new Font("Dialog", 0, 16));
		cdText.setText("");
		cdnmjLabel.setFont(new Font("Dialog", 0, 16));
		cdnmjLabel.setText("cd");

		okButton.setFont(new Font("Dialog", 0, 16));
		okButton.setText("決定");
		okButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				okButton_actionPerformed(e);
			}
		});
		cancelButton.setFont(new Font("Dialog", 0, 16));
		cancelButton.setText("キャンセル");
		cancelButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				cancelButton_actionPerformed(e);
			}
		});
		this.getContentPane().setLayout(xYLayout2);
		xYLayout2.setWidth(525);
		xYLayout2.setHeight(444 + contrH);
		syuruiCombo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// codeCombo_actionPerformed(e);
			}
		});
		jLabel1.setFont(new Font("Dialog", 0, 16));
		jLabel1.setText("業者");
		gyousyacdText.setBackground(Color.lightGray);
		gyousyacdText.setFont(new Font("Dialog", 0, 16));
		gyousyacdText.setText("");
		gyousyanmText.setBackground(Color.lightGray);
		gyousyanmText.setFont(new Font("Dialog", 0, 16));
		gyousyanmText.setText("");

		schDefaultTableModel.addColumn("CD");
		schDefaultTableModel.addColumn("カナ名称");
		schDefaultTableModel.addColumn("種類");
		schTable.setFont(new Font("Dialog", 0, 16));

		schTable.setToolTipText("");
		schScrPane.setFont(new Font("Dialog", 0, 16));
		schTable.getTableHeader().setFont(new Font("Dialog", 0, 16));

		syuruiCombo.setFont(new Font("Dialog", 0, 16));
		jLabelSarchOver1.setText("１００以上の薬剤が存在します。");
		jLabelSarchOver1.setFont(new Font("Dialog", 0, 16));
		jLabelSarchOver2.setFont(new Font("Dialog", 0, 16));
		jLabelSarchOver2.setText("検索条件の指定を行い絞り込んでください。");
		// this.getContentPane().add(jLabel1, new XYConstraints( 26, 17+contrH,
		// 54, -1));
		// this.getContentPane().add(gyousyanmText, new XYConstraints(176, 17,
		// 202, 24));
		// this.getContentPane().add(gyousyacdText, new XYConstraints(86, 17,
		// 86, 24));
		this.getContentPane().add(schScrPane,
		        new XYConstraints(14, 208 + contrH, 490, 125));
		this.getContentPane().add(searchButton,
		        new XYConstraints(222, 158 + contrH, 88, 28));
		this.getContentPane().add(cdnmjLabel,
		        new XYConstraints(38, 129 + contrH, 120, -1));
		this.getContentPane().add(cdText,
		        new XYConstraints(168, 128 + contrH, 105, 24));
		this.getContentPane().add(syuruiCombo,
		        new XYConstraints(168, 100 + contrH, 101, 24));
		this.getContentPane().add(jLabel4,
		        new XYConstraints(38, 103 + contrH, -1, -1));
		this.getContentPane().add(kananmText,
		        new XYConstraints(168, 72 + contrH, 315, 24));
		this.getContentPane().add(jLabel3,
		        new XYConstraints(37, 74 + contrH, -1, -1));
		this.getContentPane().add(jLabel2,
		        new XYConstraints(15, 47 + contrH, 483, 149));
		this.getContentPane().add(okButton,
		        new XYConstraints(84, 395 + contrH, 132, 31));
		this.getContentPane().add(cancelButton,
		        new XYConstraints(307, 395 + contrH, 132, 31));
		this.getContentPane().add(jLabelSarchOver1,
		        new XYConstraints(96, 337 + contrH, 283, 25));
		this.getContentPane().add(jLabelSarchOver2,
		        new XYConstraints(97, 360 + contrH, 360, 24));

		Component order[] = new Component[] { kananmText, syuruiCombo, cdText,
		        searchButton, okButton, cancelButton };
		FocusTraversalPolicyOrder policy = new FocusTraversalPolicyOrder(order);
		super.setFocusTraversalPolicy(policy);

		jLabelSarchOver1.setVisible(false);
		jLabelSarchOver2.setVisible(false);

		// 種類コンボセット
		BizContradrug bizsyurui = new BizContradrug();
		syuruiCdNm = bizsyurui.getMed_kind_list();
		syuruiCombo.removeAllItems();
		syuruiCombo.addItem(""); // 空白フィールド設定
		if (syuruiCdNm != null) {
			for (int i = 0; i < syuruiCdNm.length; i++) {
				syuruiCombo.addItem(syuruiCdNm[i].name);
			}
		}

		// 初期値表示
		/*
		 * inputItem_clear(); imode = 99; modeRadio1_actionPerformed(null);
		 */
	}

	/**
	 * ウィンドウが開かれたときのイベントをオーバーライドします。
	 */
	protected void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_OPENED) {
			kananmText.requestFocus();
		}
	}

	// 決定ボタン
	void okButton_actionPerformed(ActionEvent e) {
		MsgDlg msgdlg = new MsgDlg(this);
		if (schTable.getSelectedRow() >= 0) {
			sKakuteimode = "ok";
		} else {
			sKakuteimode = "cancel";
		}

		dispose();
	}

	// 戻るボタン
	void cancelButton_actionPerformed(ActionEvent e) {
		sKakuteimode = "cancel";
		dispose();
	}

	// 検索ボタン
	void searchButton_actionPerformed(ActionEvent e) {
		BizContradrug biz = new BizContradrug();
		if ((iSearchMode == MODE_NEW) || (iSearchMode == MODE_ORG_NEW)) {
			// 新規用の検索
			int i = syuruiCombo.getSelectedIndex();
			String syurui = "";
			if (i > 0) {
				syurui = syuruiCdNm[i - 1].orcacd;
			}
			// orcamedicine = biz.getOrca_medicine_list(sGyousyaid,
			orcamedicine = biz.getOrca_medicine_list(kananmText.getText(),
			        cdText.getText(), syurui);
			if (orcamedicine != null) {
				int cellwidth[] = new int[4];
				for (i = 0; i < 4; ++i) {
					cellwidth[i] = schTable.getColumnModel().getColumn(i).getPreferredWidth();
				}
				Vector vcolum = new Vector();
				Vector vdata = new Vector();
				for (i = 0; i < schTable.getColumnCount(); i++) {
					vcolum.addElement(schTable.getColumnName(i));
				}
				for (i = 0; i < orcamedicine.length; i++) {
					Vector vcdata = new Vector();
					vcdata.addElement(orcamedicine[i].orca_med_cd);
					// かな名称を正式名称に変更
					// vcdata.addElement(orcamedicine[i].med_kn);
					vcdata.addElement(orcamedicine[i].med_nm);
					vcdata.addElement(orcamedicine[i].med_kind_name);
					vcdata.addElement(orcamedicine[i].day_to);
					vdata.addElement(vcdata);
				}
				schDefaultTableModel.setDataVector(vdata, vcolum);
				for (i = 0; i < 4; ++i) {
					schTable.getColumnModel().getColumn(i).setPreferredWidth(
					        cellwidth[i]);
				}
				schTable.setColumnSelectionInterval(1, 2);
			}
		} else {
			// 修正、削除用の検索
			int i = syuruiCombo.getSelectedIndex();
			String syurui = "";
			if (i > 0) {
				syurui = syuruiCdNm[i - 1].code;
			}
			// 独自薬剤修正／削除のとき：独自薬剤のみリスト表示 04.04.05. onuki
			boolean isOrg = false;
			if ((iSearchMode == MODE_ORG_EXT) || (iSearchMode == MODE_ORG_DEL)) {
				isOrg = true;
			}
			// 伝票入力／院内処理のとき：業者を特定してリスト表示
			String tmpGyousha = "NONE";
			boolean isDenpyo = false;
			if (iSearchMode == MODE_INPUT) {
				tmpGyousha = sGyousyaid;
				isDenpyo = true;
			}
			contitem = biz.getCont_item_list(tmpGyousha,
			        // contitem = biz.getCont_item_list(//"NONE",
			        kananmText.getText(), cdText.getText(), syurui, isOrg,
			        isDenpyo);
			if (contitem != null) {
				int cellwidth[] = new int[3];
				for (i = 0; i < 3; ++i) {
					cellwidth[i] = schTable.getColumnModel().getColumn(i).getPreferredWidth();
				}
				Vector vcolum = new Vector();
				Vector vdata = new Vector();
				for (i = 0; i < schTable.getColumnCount(); i++) {
					vcolum.addElement(schTable.getColumnName(i));
				}
				for (i = 0; i < contitem.length; i++) {
					Vector vcdata = new Vector();
					vcdata.addElement(contitem[i].item_no);
					// かな名称を正式名称に変更 04.02.03. onuki
					// vcdata.addElement(contitem[i].med_kn);
					vcdata.addElement(contitem[i].med_nm);
					vcdata.addElement(contitem[i].med_kind_name);
					vdata.addElement(vcdata);
				}
				schDefaultTableModel.setDataVector(vdata, vcolum);
				for (i = 0; i < 3; ++i) {
					schTable.getColumnModel().getColumn(i).setPreferredWidth(
					        cellwidth[i]);
				}
			}
		}
		if (biz.getSarcOverSts().equals("OVER")) {
			jLabelSarchOver1.setVisible(true);
			jLabelSarchOver2.setVisible(true);
		} else {
			jLabelSarchOver1.setVisible(false);
			jLabelSarchOver2.setVisible(false);
		}
	}
}
