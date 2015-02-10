package arqaco.utility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Logger for the project
 * 
 * @author E. Guzel Kalayci
 * 
 */
public class SWANTLogger {

	private final static SimpleDateFormat sdf = new SimpleDateFormat(
			"(dd-MM-yy_HHmmss)");

	private static final String LINE_SEPARATOR = System
			.getProperty("line.separator");

	private static Logger logger = Logger.getAnonymousLogger();

	static {
		logger.setUseParentHandlers(false);
		Formatter f = new BasicFormatter();
		Handler h = new ConsoleHandler();
		h.setFormatter(f);
		logger.addHandler(h);
		try {
			FileHandler fh = new FileHandler("logs/" + getCurrentTime()
					+ ".txt");
			fh.setFormatter(f);
			logger.addHandler(fh);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Level l = Level.SEVERE;
		h.setLevel(l);
		logger.setLevel(l);
	}

	public static void info(String str) {
		logger.info(str + LINE_SEPARATOR);
	}

	public static void severe() {
		logger.severe(LINE_SEPARATOR);
	}

	public static void severe(String str) {
		logger.severe(str);
	}

	public static void severen(String str) {
		logger.severe(str + LINE_SEPARATOR);
	}

	public static void fine(String str) {
		logger.fine(str + LINE_SEPARATOR);
	}

	public static String getCurrentTime() {
		return sdf.format(Calendar.getInstance().getTime());
	}

}

class BasicFormatter extends Formatter {

	@Override
	public String format(LogRecord record) {
		StringBuffer buf = new StringBuffer();

		// buf.append(record.getLevel()).append(":").append(record.getMessage()).append(LINE_SEPARATOR);
		buf.append(record.getMessage());
		return buf.toString();
	}
}
