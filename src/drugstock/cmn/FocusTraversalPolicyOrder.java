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
package drugstock.cmn;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.util.Arrays;
import java.util.List;

/**
 * フォーカス移動順序設定用クラス
 */

public class FocusTraversalPolicyOrder extends FocusTraversalPolicy {

	// Component order[] = new Component[]{} ;
	Component order[];
	List list;

	public FocusTraversalPolicyOrder() {
		super();
	}

	public FocusTraversalPolicyOrder(Component[] inOrder) {
		super();
		order = inOrder;
		list = Arrays.asList(order);
	}

	public Component getFirstComponent(Container focusCycleRoot) {
		return order[0];
	}

	public Component getLastComponent(Container focusCycleRoot) {
		return order[order.length - 1];
	}

	public Component getComponentAfter(Container focusCycleRoot,
	        Component aComponent) {
		return getComponentMove(aComponent, true);
	}

	public Component getComponentBefore(Container focusCycleRoot,
	        Component aComponent) {
		return getComponentMove(aComponent, false);
	}

	public Component getDefaultComponent(Container focusCycleRoot) {
		return order[0];
	}

	private Component getComponentMove(Component aComponent, boolean isAfter) {
		Component tmpCom = getTmpComponentMove(aComponent, isAfter);
		// System.out.println(tmpCom.isEnabled()) ;
		int count = 0;
		while (isComponentOK(tmpCom) == false) {
			tmpCom = getTmpComponentMove(tmpCom, isAfter);
			count++;
			if (count > order.length)
				break;
			// System.out.println(count+"!!"+order.length) ;
		}
		return tmpCom;
	}

	private Component getTmpComponentMove(Component aComponent, boolean isAfter) {
		int index = list.indexOf(aComponent);
		Component tmpCom = aComponent;
		if (isAfter) {
			tmpCom = order[(index + 1) % order.length];
		} else {
			tmpCom = order[(index - 1 + order.length) % order.length];
		}
		return tmpCom;
	}

	private boolean isComponentOK(Component aComponent) {
		return aComponent.isEnabled() && aComponent.isVisible();
	}

}
