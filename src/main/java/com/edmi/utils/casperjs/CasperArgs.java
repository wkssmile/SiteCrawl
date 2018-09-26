package com.edmi.utils.casperjs;

/**
 * 
 * casperjs参数类
 * 
 * @author yi.zhang
 *
 */
public class CasperArgs {
	private String jsfile;
	private String url;
	private String item;
	private Integer timeout;

	public void setJsFile(String jsfile) {
		this.jsfile = jsfile;
	}

	public String getJsFile() {
		return this.jsfile;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return this.url;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getItem() {
		return this.item;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	public Integer getTimeout() {
		return this.timeout;
	}
}

