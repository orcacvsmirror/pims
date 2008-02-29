package drugstock.prt;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import drugstock.cmn.PropRead;

final class PrintUtil {
	static void printForm(final String formName, final String pageFileName) throws IOException {
		final List commandList = new ArrayList();
		commandList.add("red2ps");
		commandList.add(formName);
		commandList.add(pageFileName);

		final PropRead prop = new PropRead();
		String printerInfo = prop.getPropPrinter("printer_info");
		if (printerInfo != null) {
			printerInfo = printerInfo.trim();
			if (printerInfo.length() > 0) {
				commandList.add("-p");
				commandList.add(printerInfo);
			}
		}

		final Runtime runtime = Runtime.getRuntime();
		System.err.println("Executing: " + commandList);
		final Process process = runtime.exec((String[])commandList.toArray(new String[] {}));
		final InputStream is = process.getInputStream();
		while (true) {
			try {
				process.exitValue();
				break;
			} catch (IllegalThreadStateException e) {
			}
		}
	}

}
