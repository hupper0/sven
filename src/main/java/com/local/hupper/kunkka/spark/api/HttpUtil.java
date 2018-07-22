package com.local.hupper.kunkka.spark.api;


import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by lvhongpeng on 2017/9/5.
 */
public class HttpUtil {

    private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    private HttpRequestBase request; //请求对象
    private EntityBuilder builder; //Post, put请求的参数
    private URIBuilder uriBuilder; //get, delete请求的参数
    private LayeredConnectionSocketFactory socketFactory; //连接工厂
    private HttpClientBuilder clientBuilder; //构建httpclient
    private CloseableHttpClient httpClient;
    private CookieStore cookieStore; //cookie存储器
    private Builder config; //请求的相关配置
    private boolean isHttps; //是否是https请求
    private int type; //请求类型1-post, 2-get, 3-put, 4-delete


    private HttpUtil(HttpRequestBase request) {
        this.request = request;

        this.clientBuilder = HttpClientBuilder.create();
        this.isHttps = request.getURI().getScheme().equalsIgnoreCase("https");
        this.config = RequestConfig.custom().setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY);
        this.cookieStore = new BasicCookieStore();

        if (request instanceof HttpPost) {
            this.type = 1;
            this.builder = EntityBuilder.create().setParameters(new ArrayList<NameValuePair>());

        } else if (request instanceof HttpGet) {
            this.type = 2;
            this.uriBuilder = new URIBuilder();

        } else if (request instanceof HttpPut) {
            this.type = 3;
            this.builder = EntityBuilder.create().setParameters(new ArrayList<NameValuePair>());

        } else if (request instanceof HttpDelete) {
            this.type = 4;
            this.uriBuilder = new URIBuilder();
        }
    }

    //json转换器
    public static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.setSerializationInclusion(Include.NON_DEFAULT);
        // 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        mapper.getFactory().enable(Feature.ALLOW_COMMENTS);
        mapper.getFactory().enable(Feature.ALLOW_SINGLE_QUOTES);
    }


    private HttpUtil(HttpRequestBase request, HttpUtil clientUtils) {
        this(request);
        this.httpClient = clientUtils.httpClient;
        this.config = clientUtils.config;
        this.setHeaders(clientUtils.getAllHeaders());
        this.SetCookieStore(clientUtils.cookieStore);
    }

    private static HttpUtil create(HttpRequestBase request) {
        return new HttpUtil(request);
    }

    private static HttpUtil create(HttpRequestBase request, HttpUtil clientUtils) {
        return new HttpUtil(request, clientUtils);
    }


    /**
     * 创建post请求
     *
     * @param url
     * @return
     */
    public static HttpUtil post(String url) {
        return create(new HttpPost(url));
    }


    /**
     * @param url
     * @return
     */
    public static HttpUtil get(String url) {
        return create(new HttpGet(url));
    }

    /**
     * @param url
     * @return
     */
    public static HttpUtil put(String url) {
        return create(new HttpPut(url));
    }

    /**
     * @param url
     * @return
     */
    public static HttpUtil delete(String url) {
        return create(new HttpDelete(url));
    }

    /**
     * @param uri
     * @return
     */
    public static HttpUtil post(URI uri) {
        return create(new HttpPost(uri));
    }

    /**
     * @param uri
     * @return
     */
    public static HttpUtil get(URI uri) {
        return create(new HttpGet(uri));
    }

    /**
     * @param uri
     * @return
     */
    public static HttpUtil put(URI uri) {
        return create(new HttpPut(uri));
    }

    /**
     * @param uri
     * @return
     */
    public static HttpUtil delete(URI uri) {
        return create(new HttpDelete(uri));
    }

    /**
     * @param url
     * @param clientUtils
     * @return
     */
    public static HttpUtil post(String url, HttpUtil clientUtils) {
        return create(new HttpPost(url), clientUtils);
    }

    /**
     * @param url
     * @param clientUtils
     * @return
     */
    public static HttpUtil get(String url, HttpUtil clientUtils) {
        return create(new HttpGet(url), clientUtils);
    }


    /**
     * put
     *
     * @param url
     * @param clientUtils
     * @return
     */
    public static HttpUtil put(String url, HttpUtil clientUtils) {
        return create(new HttpPut(url), clientUtils);
    }


    /**
     * delete
     *
     * @param url
     * @param clientUtils
     * @return
     */
    public static HttpUtil delete(String url, HttpUtil clientUtils) {
        return create(new HttpDelete(url), clientUtils);
    }


    /**
     * post
     *
     * @param uri
     * @param clientUtils
     * @return
     */
    public static HttpUtil post(URI uri, HttpUtil clientUtils) {
        return create(new HttpPost(uri), clientUtils);
    }


    /**
     * get
     *
     * @param uri
     * @param clientUtils
     * @return
     */
    public static HttpUtil get(URI uri, HttpUtil clientUtils) {
        return create(new HttpGet(uri), clientUtils);
    }

    /**
     * @param uri
     * @param clientUtils
     * @return
     */
    public static HttpUtil put(URI uri, HttpUtil clientUtils) {
        return create(new HttpPut(uri), clientUtils);
    }

    /**
     * @param uri
     * @param clientUtils
     * @return
     */
    public static HttpUtil delete(URI uri, HttpUtil clientUtils) {
        return create(new HttpDelete(uri), clientUtils);
    }

    /**
     * 添加参数
     *
     * @param parameters
     * @return
     * @author lvhongpeng
     */
    public HttpUtil setParameters(final NameValuePair... parameters) {
        if (builder != null) {
            builder.setParameters(parameters);
        } else {
            uriBuilder.setParameters(Arrays.asList(parameters));
        }
        return this;
    }

    /**
     * 添加参数
     *
     * @param name
     * @param value
     * @return
     * @author lvhongpeng
     */
    public HttpUtil addParameter(final String name, final String value) {
        if (builder != null) {
            builder.getParameters().add(new BasicNameValuePair(name, value));
        } else {
            uriBuilder.addParameter(name, value);
        }
        return this;
    }

    /**
     * 添加参数
     *
     * @param parameters
     * @return
     */
    public HttpUtil addParameters(final NameValuePair... parameters) {
        if (builder != null) {
            builder.getParameters().addAll(Arrays.asList(parameters));
        } else {
            uriBuilder.addParameters(Arrays.asList(parameters));
        }
        return this;
    }

    /**
     * 设置请求参数,会覆盖之前的参数
     *
     * @param parameters
     * @return
     */
    public HttpUtil setParameters(final Map<String, String> parameters) {
        NameValuePair[] values = new NameValuePair[parameters.size()];
        int i = 0;

        for (Entry<String, String> parameter : parameters.entrySet()) {
            values[i++] = new BasicNameValuePair(parameter.getKey(), parameter.getValue());
        }

        setParameters(values);
        return this;
    }

    /**
     * 设置请求参数,会覆盖之前的参数
     *
     * @param file
     * @return
     */
    public HttpUtil setParameter(final File file) {
        if (builder != null) {
            builder.setFile(file);
        } else {
            throw new UnsupportedOperationException();
        }
        return this;
    }

    /**
     * 设置请求参数,会覆盖之前的参数
     *
     * @param binary
     * @return
     */
    public HttpUtil setParameter(final byte[] binary) {
        if (builder != null) {
            builder.setBinary(binary);
        } else {
            throw new UnsupportedOperationException();
        }
        return this;
    }

    /**
     * 设置请求参数,会覆盖之前的参数
     *
     * @param serializable
     * @return
     */
    public HttpUtil setParameter(final Serializable serializable) {
        if (builder != null) {
            builder.setSerializable(serializable);
        } else {
            throw new UnsupportedOperationException();
        }
        return this;
    }

    /**
     * 设置参数为Json对象
     *
     * @param parameter 参数对象
     * @return
     */
    public HttpUtil setParameterJson(final Object parameter) {
        if (builder != null) {
            try {
                builder.setBinary(mapper.writeValueAsBytes(parameter));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        } else {
            throw new UnsupportedOperationException();
        }
        return this;
    }

    /**
     * 设置请求参数,会覆盖之前的参数
     *
     * @param stream
     * @return
     */
    public HttpUtil setParameter(final InputStream stream) {
        if (builder != null) {
            builder.setStream(stream);
        } else {
            throw new UnsupportedOperationException();
        }
        return this;
    }

    /**
     * 设置请求参数,会覆盖之前的参数
     *
     * @param text
     * @return
     */
    public HttpUtil setParameter(final String text) {
        if (builder != null) {
            builder.setText(text);
        } else {
            uriBuilder.setParameters(URLEncodedUtils.parse(text, Consts.UTF_8));
        }
        return this;
    }

    /**
     * 设置内容编码
     *
     * @param encoding
     * @return
     */
    public HttpUtil setContentEncoding(final String encoding) {
        if (builder != null) builder.setContentEncoding(encoding);
        return this;
    }

    /**
     * 设置ContentType
     *
     * @param contentType
     * @return
     */
    public HttpUtil setContentType(ContentType contentType) {
        if (builder != null) builder.setContentType(contentType);
        return this;
    }

    /**
     * 设置ContentType
     *
     * @param mimeType
     * @param charset  内容编码
     * @return
     */
    public HttpUtil setContentType(final String mimeType, final Charset charset) {
        if (builder != null) builder.setContentType(ContentType.create(mimeType, charset));
        return this;
    }

    /**
     * 添加参数
     *
     * @param parameters
     * @return
     */
    public HttpUtil addParameters(Map<String, String> parameters) {
        List<NameValuePair> values = new ArrayList(parameters.size());

        for (Entry<String, String> parameter : parameters.entrySet()) {
            values.add(new BasicNameValuePair(parameter.getKey(), parameter.getValue()));
        }

        if (builder != null) {
            builder.getParameters().addAll(values);
        } else {
            uriBuilder.addParameters(values);
        }
        return this;
    }

    /**
     * 添加Header
     *
     * @param name
     * @param value
     * @return
     */
    public HttpUtil addHeader(String name, String value) {
        request.addHeader(name, value);
        return this;
    }

    /**
     * 添加Header
     *
     * @param headers
     * @return
     */
    public HttpUtil addHeaders(Map<String, String> headers) {
        for (Entry<String, String> header : headers.entrySet()) {
            request.addHeader(header.getKey(), header.getValue());
        }

        return this;
    }


    /**
     * 设置Header,会覆盖所有之前的Header
     *
     * @param headers
     * @return
     */
    public HttpUtil setHeaders(Map<String, String> headers) {
        Header[] headerArray = new Header[headers.size()];
        int i = 0;

        for (Entry<String, String> header : headers.entrySet()) {
            headerArray[i++] = new BasicHeader(header.getKey(), header.getValue());
        }
        request.setHeaders(headerArray);
        return this;
    }

    public HttpUtil setHeaders(Header[] headers) {
        request.setHeaders(headers);
        return this;
    }

    /**
     * 获取所有Header
     *
     * @return
     */
    public Header[] getAllHeaders() {
        return request.getAllHeaders();
    }

    /**
     * 移除指定name的Header列表
     *
     * @param name
     */
    public HttpUtil removeHeaders(String name) {
        request.removeHeaders(name);
        return this;
    }

    /**
     * 移除指定的Header
     *
     * @param header
     */
    public HttpUtil removeHeader(Header header) {
        request.removeHeader(header);
        return this;
    }

    /**
     * 移除指定的Header
     *
     * @param name
     * @param value
     */
    public HttpUtil removeHeader(String name, String value) {
        request.removeHeader(new BasicHeader(name, value));
        return this;
    }

    /**
     * 是否存在指定name的Header
     *
     * @param name
     * @return
     */
    public boolean containsHeader(String name) {
        return request.containsHeader(name);
    }

    /**
     * 获取Header的迭代器
     *
     * @return
     */
    public HeaderIterator headerIterator() {
        return request.headerIterator();
    }

    /**
     * 获取协议版本信息
     *
     * @return
     */
    public ProtocolVersion getProtocolVersion() {
        return request.getProtocolVersion();
    }

    /**
     * 获取请求Url
     *
     * @return
     */
    public URI getURI() {
        return request.getURI();
    }

    /**
     * 设置请求Url
     *
     * @return
     */
    public HttpUtil setURI(URI uri) {
        request.setURI(uri);
        return this;
    }

    /**
     * 设置请求Url
     *
     * @return
     */
    public HttpUtil setURI(String uri) {
        return setURI(URI.create(uri));
    }

    /**
     * 设置一个CookieStore
     *
     * @param cookieStore
     * @return
     */
    public HttpUtil SetCookieStore(CookieStore cookieStore) {
        if (cookieStore == null) return this;
        this.cookieStore = cookieStore;
        return this;
    }

    /**
     * 添加Cookie
     *
     * @param cookies
     * @return
     */
    public HttpUtil addCookie(Cookie... cookies) {
        if (cookies == null) return this;

        for (int i = 0; i < cookies.length; i++) {
            cookieStore.addCookie(cookies[i]);
        }
        return this;
    }

    /**
     * 设置网络代理
     *
     * @param hostname
     * @param port
     * @return
     */
    public HttpUtil setProxy(String hostname, int port) {
        HttpHost proxy = new HttpHost(hostname, port);
        return setProxy(proxy);
    }

    /**
     * 设置网络代理
     *
     * @param hostname
     * @param port
     * @param schema
     * @return
     */
    public HttpUtil setProxy(String hostname, int port, String schema) {
        HttpHost proxy = new HttpHost(hostname, port, schema);
        return setProxy(proxy);
    }

    /**
     * 设置网络代理
     *
     * @param address
     * @return
     */
    public HttpUtil setProxy(InetAddress address) {
        HttpHost proxy = new HttpHost(address);
        return setProxy(proxy);
    }

    /**
     * 设置网络代理
     *
     * @param host
     * @return
     */
    public HttpUtil setProxy(HttpHost host) {
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(host);
        clientBuilder.setRoutePlanner(routePlanner);
        return this;
    }

//    /**
//     * 设置双向认证的JKS
//     *
//     * @param jksFilePath jks文件路径
//     * @param password    密码
//     * @return
//     */
//    public HttpUtil setJKS(String jksFilePath, String password) {
//        return setJKS(new File(jksFilePath), password);
//    }
//
//    /**
//     * 设置双向认证的JKS
//     *
//     * @param jksFile  jks文件
//     * @param password 密码
//     * @return
//     */
//    public HttpUtil setJKS(File jksFile, String password) {
//        try (InputStream instream = new FileInputStream(jksFile)) {
//            return setJKS(instream, password);
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//            throw new RuntimeException(e.getMessage(), e);
//        }
//    }

    /**
     * 设置双向认证的JKS, 不会关闭InputStream
     *
     * @param instream jks流
     * @param password 密码
     * @return
     */
    public HttpUtil setJKS(InputStream instream, String password) {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(instream, password.toCharArray());
            return setJKS(keyStore);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 设置双向认证的JKS
     *
     * @param keyStore jks
     * @return
     */
    public HttpUtil setJKS(KeyStore keyStore) {
        try {
            SSLContext sslContext = SSLContexts.custom().useTLS().loadTrustMaterial(keyStore).build();
            socketFactory = new SSLConnectionSocketFactory(sslContext);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }

        return this;
    }

    /**
     * 设置Socket超时时间,单位:ms
     *
     * @param socketTimeout
     * @return
     */
    public HttpUtil setSocketTimeout(int socketTimeout) {
        config.setSocketTimeout(socketTimeout);
        return this;
    }

    /**
     * 设置连接超时时间,单位:ms
     *
     * @param connectTimeout
     * @return
     */
    public HttpUtil setConnectTimeout(int connectTimeout) {
        config.setConnectTimeout(connectTimeout);
        return this;
    }

    /**
     * 设置请求超时时间,单位:ms
     *
     * @param connectionRequestTimeout
     * @return
     */
    public HttpUtil setConnectionRequestTimeout(int connectionRequestTimeout) {
        config.setConnectionRequestTimeout(connectionRequestTimeout);
        return this;
    }

    /**
     * 设置是否允许服务端循环重定向
     *
     * @param circularRedirectsAllowed
     * @return
     */
    public HttpUtil setCircularRedirectsAllowed(boolean circularRedirectsAllowed) {
        config.setCircularRedirectsAllowed(circularRedirectsAllowed);
        return this;
    }

    /**
     * 设置是否启用调转
     *
     * @param redirectsEnabled
     * @return
     */
    public HttpUtil setRedirectsEnabled(boolean redirectsEnabled) {
        config.setRedirectsEnabled(redirectsEnabled);
        return this;
    }

    /**
     * 设置重定向的次数
     *
     * @param maxRedirects
     * @return
     */
    public HttpUtil maxRedirects(int maxRedirects) {
        config.setMaxRedirects(maxRedirects);
        return this;
    }

    /**
     * 执行请求
     *
     * @return
     * @date 2015年7月17日
     */
    public ResponseWrap execute() {
        settingRequest();
        if (httpClient == null) {
            httpClient = clientBuilder.build();
        }

        try {
            HttpClientContext context = HttpClientContext.create();
            CloseableHttpResponse response = httpClient.execute(request, context);
            return new ResponseWrap(httpClient, request, response, context, mapper);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 执行请求
     *
     * @param responseHandler
     * @return
     */
    public <T> T execute(final ResponseHandler<? extends T> responseHandler) {
        settingRequest();
        if (httpClient == null) httpClient = clientBuilder.build();

        try {
            return httpClient.execute(request, responseHandler);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    /**
     * 关闭连接
     *
     * @date 2015年7月18日
     */
    @SuppressWarnings("deprecation")
    public void shutdown() {
        httpClient.getConnectionManager().shutdown();
    }

    /**
     * 获取LayeredConnectionSocketFactory 使用ssl单向认证
     *
     * @return
     * @date 2015年7月17日
     */
    private LayeredConnectionSocketFactory getSSLSocketFactory() {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                // 信任所有
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();

            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            return sslsf;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void settingRequest() {
        URI uri = null;
        if (uriBuilder != null && uriBuilder.getQueryParams().size() != 0) {
            try {
                uri = uriBuilder.setPath(request.getURI().toString()).build();
            } catch (URISyntaxException e) {
                logger.warn(e.getMessage(), e);
            }
        }

        HttpEntity httpEntity;

        switch (type) {
            case 1:
                httpEntity = builder.build();
                if (httpEntity.getContentLength() > 0) ((HttpPost) request).setEntity(builder.build());
                break;

            case 2:
                HttpGet get = ((HttpGet) request);
                if (uri != null) get.setURI(uri);
                break;

            case 3:
                httpEntity = builder.build();
                if (httpEntity.getContentLength() > 0) ((HttpPut) request).setEntity(httpEntity);
                break;

            case 4:
                HttpDelete delete = ((HttpDelete) request);
                if (uri != null) delete.setURI(uri);

                break;
        }

        if (isHttps && socketFactory != null) {
            clientBuilder.setSSLSocketFactory(socketFactory);

        } else if (isHttps) {
            clientBuilder.setSSLSocketFactory(getSSLSocketFactory());
        }

        clientBuilder.setDefaultCookieStore(cookieStore);
        request.setConfig(config.build());
    }


}
