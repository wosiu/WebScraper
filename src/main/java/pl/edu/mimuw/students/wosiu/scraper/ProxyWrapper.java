package pl.edu.mimuw.students.wosiu.scraper;

import java.net.InetSocketAddress;
import java.net.Proxy;

public class ProxyWrapper {
	private Proxy proxy;
	private String country;
	private Integer speed = null;
	private Integer connectionTime = null;
	//private Integer lastUpdate = null;

	public Proxy getProxy() {
		return proxy;
	}

	public void setHTTPProxy(String ip, int port) {
		this.proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
	}

	public void setSocksProxy(String ip, int port) {
		this.proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(ip, port));
	}

	public void setProxy(Proxy proxy) {
		this.proxy = proxy;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Integer getConnectionTime() {
		return connectionTime;
	}

	public void setConnectionTime(Integer connectionTime) {
		this.connectionTime = connectionTime;
	}

	public Integer getSpeed() {
		return speed;
	}

	public void setSpeed(Integer speed) {
		this.speed = speed;
	}


}
