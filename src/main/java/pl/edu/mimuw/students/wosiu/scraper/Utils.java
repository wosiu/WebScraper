package pl.edu.mimuw.students.wosiu.scraper;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class Utils {

	public static String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36";

	public static URL stringToURL(String url) throws ConnectionException {
		url = url.trim();
		URL targetURL = null;
		try {
			targetURL = new URL(url);
		} catch (MalformedURLException | IllegalArgumentException e) {
			throw new ConnectionException("Incorrect url: '" + url + "': " + e.toString());
		}

		return targetURL;
	}

	public static URL getRedirectUrl(String url) {
		URL orignal = null;
		try {
			orignal = new URL(url);
		} catch (MalformedURLException e) {
			return null;
		}
		URL redirectedUrl = orignal;
		HttpURLConnection con = null;
		try {
			con = (HttpURLConnection) orignal.openConnection();
			con.setInstanceFollowRedirects(true);
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setConnectTimeout(3000);
			con.setReadTimeout(1500);
			con.connect();
			InputStream is = con.getInputStream();
			is.close();
			redirectedUrl = con.getURL();
			con.disconnect();
		} catch (IOException e) {
			//nie wazne dla nas (http 403)
		}

		return redirectedUrl;
	}

	public static URL redirect2(String url) {
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) (new URL(url).openConnection());
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			conn.connect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int responseCode = 0;
		try {
			responseCode = conn.getResponseCode();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (responseCode == 301) {
			String location = conn.getHeaderField("Location");
			try {
				conn = (HttpURLConnection) (new URL(location).openConnection());
			} catch (IOException e) {
				e.printStackTrace();
			}
			conn.setInstanceFollowRedirects(false);
			conn.setConnectTimeout(3000);
			conn.setReadTimeout(3000);
			try {
				conn.connect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return conn.getURL();
	}

	public static String stripNonEnglish(String in) {
		return in.replaceAll("[^\\w\\s]", "").replaceAll("\\s+", " ").trim();
	}

	// TODO tests
	public static String stripAccents(String in) {
		return org.apache.commons.lang3.StringUtils.stripAccents(in);
	}

	public static String urlEncodeSpecial(String in, Character... valid) {
		in = fixSpaces(in);
		String patternToMatch = "[\\!\"'#$%&()+,/:;<=>?@[]^_{|}`~]+ "; //.-* are ok for URLEncode.encode UTF8
		StringBuilder builder = new StringBuilder();
		List<Character>ok = Arrays.asList(valid);

		for (Character c : in.toCharArray()) {
			if (ok.contains(c)) {
				builder.append(c);
			} else if (patternToMatch.indexOf(c) == -1) {
				builder.append(c);
			} else {
				try {
					builder.append(URLEncoder.encode(c.toString(), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					builder.append(c);
				}
			}
		}
		return builder.toString();
	}

	public static String fixSpaces(String in) {
		return in.replace((char)160, ' ').replaceAll(" +", " ").trim();
	}

	// TODO tests
	public static String urlStripEncode(String in) {
		in = fixSpaces(in);
		in = stripAccents(in);
		in = urlEncode(in);
		return in;
	}

	// TODO tests
	public static String urlEncode(String in, Character... valid) {
		in = fixSpaces(in);

		if (valid.length == 0) {
			try {
				URLEncoder.encode(in, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				return in;
			}
		}

		StringBuilder builder = new StringBuilder();
		List<Character>ok = Arrays.asList(valid);

		for (Character c : in.toCharArray()) {
			if (ok.contains(c)) {
				builder.append(c);
			} else {
				try {
					builder.append(URLEncoder.encode(c.toString(), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					builder.append(c);
				}
			}
		}
		return builder.toString();
	}

		public static String encodeROT47(String value) {
			StringBuilder result = new StringBuilder();

			for (int i = 0; i < value.length(); i++) {
				char c = value.charAt(i);

				if (c != ' ') {
					c += 47;
					if (c > '~') {
						c -= 94;
					}
				}

				result.append(c);
			}

			return result.toString();
		}


	public static class NumUnits {
		public NumUnits(float number, String unit) {
			this.number = number;
			this.unit = unit;
		}
		public float number;
		public String unit;
		@Override
		public String toString() {
			return number + " " + unit;
		}
	}

	public static NumUnits convertToSI(String amountUnit) {
		amountUnit = amountUnit.toLowerCase().replace(" ", "").replace(",",".");
		if (StringUtils.isBlank(amountUnit)) {
			return null;
		}
		String amountOrg = amountUnit.replaceAll("[^0-9.]*", "");
		Float amountOrgFl = null;
		try {
			amountOrgFl = Float.parseFloat(amountOrg);
		} catch (NumberFormatException e) {
			return null;
		}
		String unitOrg = amountUnit.replaceAll("[^a-z]*", "");
//		if (StringUtils.isBlank(unitOrg)) {
//			return null;
//		}
		NumUnits res = new NumUnits(amountOrgFl, unitOrg);
		switch (unitOrg) {
			case "l": return res;
			case "kg": return res;
			case "m": return res;
			case "szt": return res;
			case "sztuk": return new NumUnits(amountOrgFl, "szt");
			case "sz": return new NumUnits(amountOrgFl, "szt");
			case "ml": return new NumUnits(amountOrgFl/1000, "l");
			case "g": return new NumUnits(amountOrgFl/1000, "kg");
			case "dag": return new NumUnits(amountOrgFl/100, "kg");
			case "dkg": return new NumUnits(amountOrgFl/100, "kg");
			case "cm": return new NumUnits(amountOrgFl/100, "m");
			case "mm": return new NumUnits(amountOrgFl/1000, "m");
			case "dm": return new NumUnits(amountOrgFl/10, "m");
			default: return res;
		}
	}
}
