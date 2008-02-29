package drugstock.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

public class XYLayout implements LayoutManager2 {

	private int width;
	private int height;
	private final Map constraintsMap;
	private static final XYConstraints DEFAULT_CONSTRAINTS = new XYConstraints();

	public XYLayout() {
		constraintsMap = new HashMap();
	}

	public XYLayout(final int width, final int height) {
		this();
		this.width = width;
		this.height = height;
	}

	public void setWidth(final int width) {
		this.width = width;
	}

	public void setHeight(final int height) {
		this.height = height;
	}

	public void addLayoutComponent(final String s, final Component component1) {
		// do nothing
	}

	public void removeLayoutComponent(final Component component) {
		constraintsMap.remove(component);
	}

	public Dimension preferredLayoutSize(final Container target) {
		return getLayoutSize(target, true);
	}

	public Dimension minimumLayoutSize(final Container target) {
		return getLayoutSize(target, false);
	}

	public void layoutContainer(final Container target) {
		final Insets insets = target.getInsets();
		for (int i = 0, count = target.getComponentCount(); i < count; i++) {
			final Component component = target.getComponent(i);
			if ( ! component.isVisible()) {
				continue;
			}
			final Rectangle r = getComponentBounds(component, true);
			component.setBounds(insets.left + r.x, insets.top + r.y, r.width, r.height);
		}
	}

	public void addLayoutComponent(final Component component, final Object constraints) {
		if (constraints instanceof XYConstraints) {
			constraintsMap.put(component, constraints);
		}
	}

	public Dimension maximumLayoutSize(final Container target) {
		return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	public float getLayoutAlignmentX(final Container target) {
		return 0.5F;
	}

	public float getLayoutAlignmentY(final Container target) {
		return 0.5F;
	}

	public void invalidateLayout(final Container container) {
		// do nothing
	}

	private Rectangle getComponentBounds(final Component component, final boolean doPreferred) {
		final XYConstraints constraints;
		if (constraintsMap.containsKey(component)) {
			constraints = (XYConstraints)constraintsMap.get(component);
		} else {
			constraints = DEFAULT_CONSTRAINTS;
		}
		final Rectangle r = new Rectangle(constraints.x, constraints.y, constraints.width, constraints.height);
		if (r.width <= 0 || r.height <= 0) {
			final Dimension d = doPreferred ? component.getPreferredSize() : component.getMinimumSize();
			if (r.width <= 0) {
				r.width = d.width;
			}
			if (r.height <= 0) {
				r.height = d.height;
			}
		}
		return r;
	}

	private Dimension getLayoutSize(final Container target, final boolean doPreferred) {
		final Dimension d = new Dimension(0, 0);
		if (width <= 0 || height <= 0) {
			for (int i = 0, count = target.getComponentCount(); i < count; i++) {
				final Component component = target.getComponent(i);
				if ( ! component.isVisible()) {
					continue;
				}
				final Rectangle r = getComponentBounds(component, doPreferred);
				d.width = Math.max(d.width, r.x + r.width);
				d.height = Math.max(d.height, r.y + r.height);
			}

		}
		if (width > 0) {
			d.width = width;
		}
		if (height > 0) {
			d.height = height;
		}
		final Insets insets = target.getInsets();
		d.width += insets.left + insets.right;
		d.height += insets.top + insets.bottom;
		return d;
	}
}
