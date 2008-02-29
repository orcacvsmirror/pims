package drugstock.layout;

public class XYConstraints {

	final int x;
	final int y;
	final int width;
	final int height;

	public XYConstraints() {
		this(0, 0, 0, 0);
	}

	public XYConstraints(final int x, final int y, final int width, final int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

}
