package pl.edu.mimuw.students.wosiu.scraper;

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
		URL targetURL = null;
		try {
			URI uri = new URI(url);
			targetURL = uri.toURL();
		} catch (URISyntaxException | MalformedURLException | IllegalArgumentException e) {
			throw new ConnectionException("Incorrect url: " + url + ". \nError: " + e.toString());
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
		in = in.trim();
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

	// TODO tests
	public static String urlStripEncode(String in) {
		in = stripAccents(in.trim());
		in = urlEncode(in);
		return in;
	}

	// TODO tests
	public static String urlEncode(String in, Character... valid) {
		in = in.trim();

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

		/*****************************************************************
		 *   Licensed to the Apache Software Foundation (ASF) under one
		 *  or more contributor license agreements.  See the NOTICE file
		 *  distributed with this work for additional information
		 *  regarding copyright ownership.  The ASF licenses this file
		 *  to you under the Apache License, Version 2.0 (the
		 *  "License"); you may not use this file except in compliance
		 *  with the License.  You may obtain a copy of the License at
		 *
		 *    http://www.apache.org/licenses/LICENSE-2.0
		 *
		 *  Unless required by applicable law or agreed to in writing,
		 *  software distributed under the License is distributed on an
		 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
		 *  KIND, either express or implied.  See the License for the
		 *  specific language governing permissions and limitations
		 *  under the License.
		 ****************************************************************/


	/**
	 * The ROT-47 password encoder passes the text of the database password
	 * through a simple Caesar cipher to obscure the password text.  The ROT-47
	 * cipher is similar to the ROT-13 cipher, but processes numbers and symbols
	 * as well. See the Wikipedia entry on
	 * <a href="http://en.wikipedia.org/wiki/Rot-13">ROT13</a>
	 * for more information on this topic.
	 *
	 * @author Michael Gentry
	 * @since 3.0
	 */
	public static class Rot47PasswordEncoder {
		/* (non-Javadoc)
		   * @see org.apache.cayenne.conf.PasswordEncoding#decodePassword(java.lang.String, java.lang.String)
		   */
		public static String decodePassword(String encodedPassword, String key) {
			return rotate(encodedPassword);
		}

		/* (non-Javadoc)
		   * @see org.apache.cayenne.conf.PasswordEncoding#encodePassword(java.lang.String, java.lang.String)
		   */
		public static String encodePassword(String normalPassword, String key) {
			return rotate(normalPassword);
		}

		/**
		 * Applies a ROT-47 Caesar cipher to the supplied value.  Each letter in
		 * the supplied value is substituted with a new value rotated by 47 places.
		 * See <a href="http://en.wikipedia.org/wiki/ROT13">ROT13</a> for more
		 * information (there is a subsection for ROT-47).
		 * <p>
		 * A Unix command to perform a ROT-47 cipher is:
		 * <pre>tr '!-~' 'P-~!-O'</pre>
		 *
		 * @param value The text to be rotated.
		 * @return The rotated text.
		 */
		public static String rotate(String value) {
			int length = value.length();
			StringBuilder result = new StringBuilder();

			for (int i = 0; i < length; i++) {
				char c = value.charAt(i);

				// Process letters, numbers, and symbols -- ignore spaces.
				if (c != ' ') {
					// Add 47 (it is ROT-47, after all).
					c += 47;

					// If character is now above printable range, make it printable.
					// Range of printable characters is ! (33) to ~ (126).  A value
					// of 127 (just above ~) would therefore get rotated down to a
					// 33 (the !).  The value 94 comes from 127 - 33 = 94, which is
					// therefore the value that needs to be subtracted from the
					// non-printable character to put it into the correct printable
					// range.
					if (c > '~')
						c -= 94;
				}

				result.append(c);
			}

			return result.toString();
		}

	}

}
