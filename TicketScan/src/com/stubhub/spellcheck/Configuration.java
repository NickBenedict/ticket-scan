package com.stubhub.spellcheck;

import java.net.Proxy.Type;

public class Configuration {
    private String proxyHost;
    private int proxyPort;
    private String proxyScheme;

    private boolean proxyEnabled = false;

    public void setProxy(String proxyHost, int proxyPort, String proxyScheme) {
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        this.proxyScheme = proxyScheme;
        proxyEnabled = true;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public Type getProxyScheme() {
        return Type.valueOf( proxyScheme.toUpperCase() );
    }

    public boolean isProxyEnabled() {
        return proxyEnabled;
    }
}
