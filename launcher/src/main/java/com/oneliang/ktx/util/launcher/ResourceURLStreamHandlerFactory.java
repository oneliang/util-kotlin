package com.oneliang.ktx.util.launcher;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

public class ResourceURLStreamHandlerFactory implements URLStreamHandlerFactory {
	private final ClassLoader classLoader;
	private URLStreamHandlerFactory chainFac;

	public ResourceURLStreamHandlerFactory(ClassLoader cl) {
		this.classLoader = cl;
	}

	public URLStreamHandler createURLStreamHandler(String protocol) {
		if ("rsrc".equals(protocol))
			return new ResourceURLStreamHandler(this.classLoader);
		if (this.chainFac != null)
			return this.chainFac.createURLStreamHandler(protocol);
		return null;
	}

	public void setURLStreamHandlerFactory(URLStreamHandlerFactory fac) {
		this.chainFac = fac;
	}
}