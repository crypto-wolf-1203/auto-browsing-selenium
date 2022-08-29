package com.pplvn.proxy;

import java.io.IOException;

public abstract class AbstractProxyClient {
    public abstract String get(String url) throws IOException;

    public abstract void save(String url, String output) throws IOException;

    public abstract void post(String url, String body, String contentType, String output) throws IOException;

    public abstract String post(String url, String body, String contentType) throws IOException;
}
