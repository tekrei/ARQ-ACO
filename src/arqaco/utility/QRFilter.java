package arqaco.utility;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * File filter for project specific query file
 * with *.qr extension
 * 
 * @author E. Guzel Kalayci
 * 
 */

public class QRFilter extends FileFilter implements java.io.FileFilter {

	public static QRFilter QRFF = new QRFilter();

	@Override
	public boolean accept(File file) {
		if (file.isDirectory()) {
			return true;
		}
		String name = file.getName().toLowerCase();
		if (name.endsWith(".qr")) {
			return true;
		}
		return false;
	}

	@Override
	public String getDescription() {
		return "Application Specific QueryInfo file type (.qr)";
	}

}