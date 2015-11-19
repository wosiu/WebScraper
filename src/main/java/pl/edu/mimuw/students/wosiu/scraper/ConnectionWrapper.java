package pl.edu.mimuw.students.wosiu.scraper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

public class ConnectionWrapper {
	private Integer connectionTimeoutMs = null;
	private Integer readTimeoutMs = null;

	public void setConnectionTimeoutMs(Integer connectionTimeoutMs) {
		this.connectionTimeoutMs = connectionTimeoutMs;
	}

	public void setReadTimeoutMs(Integer readTimeoutMs) {
		this.readTimeoutMs = readTimeoutMs;
	}

	public void setProxy(Proxy proxy) {
		this.proxy = proxy;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	private Proxy proxy = null;
	private String userAgent = null;

	public HttpURLConnection connect(URL targetURL) throws IOException {
		HttpURLConnection uc;

		if (proxy != null) {
			uc = (HttpURLConnection) targetURL.openConnection(proxy);
		} else {
			uc = (HttpURLConnection) targetURL.openConnection();
		}

		if (userAgent != null) {
			uc.setRequestProperty("User-Agent", userAgent);
		}

		if (connectionTimeoutMs != null) {
			uc.setConnectTimeout(connectionTimeoutMs);
		}

		if (readTimeoutMs != null) {
			uc.setReadTimeout(readTimeoutMs);
		}

		uc.connect();
		return uc;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (proxy != null) {
			builder.append("proxy: " + proxy.toString() + ",");
		}
		return builder.toString();
	}
}
