package com.oneliang.ktx.util.launcher;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class ResourceURLStreamHandler extends URLStreamHandler {
	private final ClassLoader classLoader;

	public ResourceURLStreamHandler(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	protected URLConnection openConnection(URL u) throws IOException {
		return new ResourceURLConnection(u, this.classLoader);
	}

	protected void parseURL(URL url, String spec, int start, int limit) {
		String file;
		if (spec.startsWith("rsrc:")) {
			file = spec.substring(5);
		} else {
			if (url.getFile().equals("./")) {
				file = spec;
			} else {
				if (url.getFile().endsWith("/"))
					file = url.getFile() + spec;
				else
					file = spec;
			}
		}
		setURL(url, "rsrc", "", -1, null, null, file, null, null);
	}
}