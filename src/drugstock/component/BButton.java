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
package drugstock.component;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;

/**
 * エンターキーでフォーカス移動 シフトキー＋エンターキーでクリック動作をします。
 */
public class BButton extends JButton {

	/**
	 * コンストラクタ
	 */
	public BButton() {
		super();
		addListener();
		this.setDefaultCapable(false);
	}

	/**
	 * エンターキーのリスナを設定します
	 */
	private void addListener() {
		KeyAdapter lis = new KeyAdapter() {

			public void keyPressed(KeyEvent evt) {
				if (evt.getKeyChar() == KeyEvent.VK_ENTER) {
					if (evt.isShiftDown()) {
						doClick();
					} else {
						// FocusManager.getCurrentManager().focusNextComponent(BButton.this);
					}
				}
			}
		};
		this.addKeyListener(lis);
	}

}