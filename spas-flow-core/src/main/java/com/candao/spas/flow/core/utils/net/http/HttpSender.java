package com.candao.spas.flow.core.utils.net.http;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class HttpSender {
    // 默认超时时间
    private static int DEFAULT_TIMEOUT_SECONDS = 5;
    private static final int HTTP_POOL_MAX_TOTAL = 400;
    private static final int HTTP_POOL_DEFAULT_MAX_PER_ROUTE = 40;
    // 默认编码
    private static final String DEFAULT_CHARSET = "UTF-8";
    private static AtomicLong seqId = new AtomicLong(0);
    private static PoolingHttpClientConnectionManager connManager;

    static {
        // 初始化连接池
        // //忽略证书
        Registry<ConnectionSocketFactory> registry = null;
        try {
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
                    NoopHostnameVerifier.INSTANCE);
            PlainConnectionSocketFactory plainConnectionSocketFactory = new PlainConnectionSocketFactory();
            registry = RegistryBuilder.<ConnectionSocketFactory>create().register("https", sslsf)
                    .register("http", plainConnectionSocketFactory).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        connManager = new PoolingHttpClientConnectionManager(registry);
        connManager.setMaxTotal(HTTP_POOL_MAX_TOTAL);
        connManager.setDefaultMaxPerRoute(HTTP_POOL_DEFAULT_MAX_PER_ROUTE);
    }

    private static CloseableHttpClient getHttpClient() {
//		return HttpClients.createDefault();
        return HttpClients.custom().setConnectionManager(connManager).setConnectionManagerShared(true).build();
    }

    /**
     * 发起http请求（json格式）
     *
     * @param urlStr
     * @param json   json格式参数
     * @return
     * @throws Exception
     */
    public static HttpResult postRequest(String urlStr, String json) {
        return postRequest(urlStr, json, "application/json");
    }

    /**
     * json格式请求，可设置超时时间
     *
     * @param urlStr
     * @param json
     * @param timeoutSeconds
     * @return
     */
    public static HttpResult postRequest(String urlStr, String json, int timeoutSeconds) {
        return postRequest(urlStr, json, "application/json", timeoutSeconds);
    }

    /**
     * json格式请求，可设置超时时间，自定义contentType
     *
     * @param urlStr
     * @param json
     * @param contentType
     * @return
     */
    public static HttpResult postRequest(String urlStr, String json, String contentType) {
        return postRequest(urlStr, json, contentType, DEFAULT_TIMEOUT_SECONDS);
    }

    /**
     * json格式请求，可设置超时时间，自定义contentType和超时时间
     *
     * @param urlStr
     * @param json
     * @param contentType
     * @param timeoutSeconds
     * @return
     */
    public static HttpResult postRequest(String urlStr, String json, String contentType, int timeoutSeconds) {
        Map<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Content-Type", contentType);
        headerMap.put("User-Agent", "androidClient");
        return postRequest(urlStr, headerMap, json, timeoutSeconds);
    }

    /**
     * 发起http post请求 （json+header）
     *
     * @param urlStr
     * @param header
     * @param json
     * @return
     */
    public static HttpResult postRequest(String urlStr, Map<String, String> header, String json) {
        return postRequest(urlStr, header, json, DEFAULT_TIMEOUT_SECONDS);
    }

    /**
     * 发起http post请求 （json+header+timeoutSeconds）
     *
     * @param urlStr
     * @param header         传指定header信息
     * @param json
     * @param timeoutSeconds 指定超时时间
     * @return
     */
    public static HttpResult postRequest(String urlStr, Map<String, String> header, String json, int timeoutSeconds) {
        long curId = seqId.incrementAndGet();
        try {
//			log.info("Http " + urlStr);// + "data=" + URLEncoder.encode(json, "utf-8"));
            log.info("HttpRequest " + curId + " " + urlStr + "?param=" + json);
            HttpPost httpPost = new HttpPost(urlStr);
            for (String headerKey : header.keySet()) {
                httpPost.addHeader(headerKey, header.get(headerKey));
            }
            if (StringUtils.isNotBlank(json)) {
                httpPost.setEntity(new StringEntity(json, "utf-8"));
            }
            return doRequest(httpPost, urlStr, timeoutSeconds, curId);
        } catch (Exception e) {
            log.error("HttpRequest " + curId + "请求异常  ", e);
        }
        return null;
    }

    /**
     * 发起http请求，contentType为x-www-form-urlencoded（map传参方式）
     *
     * @param urlStr
     * @param params
     * @return
     */
    public static HttpResult postRequest(String urlStr, Map<String, Object> params) {
        return postRequest(urlStr, params, DEFAULT_TIMEOUT_SECONDS);
    }

    /**
     * 发起http请求，contentType为x-www-form-urlencoded（map传参方式+timeoutSeconds）
     *
     * @param urlStr
     * @param params         参数
     * @param timeoutSeconds 指定超时时间
     * @return
     * @throws Exception
     */
    public static HttpResult postRequest(String urlStr, Map<String, Object> params, int timeoutSeconds) {
        Map<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Content-Type", "application/x-www-form-urlencoded");
        headerMap.put("User-Agent", "androidClient");
        return postRequest(urlStr, headerMap, params, timeoutSeconds);
    }

    /**
     * 发起http请求（map传参方式带header）
     *
     * @param urlStr
     * @param header
     * @param params
     * @return
     */
    public static HttpResult postRequest(String urlStr, Map<String, String> header, Map<String, Object> params) {
        return postRequest(urlStr, header, params, DEFAULT_TIMEOUT_SECONDS);
    }

    /**
     * 发起http请求（map传参方式带header+timeoutSeconds）
     *
     * @param urlStr
     * @param header
     * @param params
     * @return
     */
    public static HttpResult postRequest(String urlStr, Map<String, String> header, Map<String, Object> params,
                                         int timeoutSeconds) {
        try {
            long curId = seqId.incrementAndGet();
            log.info("HttpRequest " + curId + " " + urlStr + "?param=" + params.toString());
            HttpPost httpPost = new HttpPost(urlStr);
            for (String headerKey : header.keySet()) {
                httpPost.addHeader(headerKey, header.get(headerKey));
            }
            List<NameValuePair> data = new ArrayList<NameValuePair>();
            if (params != null && params.size() > 0) {
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    data.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
                }
            }
            // 将表单的值放入postMethod中
            httpPost.setEntity(new UrlEncodedFormEntity(data, "utf-8"));
            return doRequest(httpPost, urlStr, timeoutSeconds, curId);
        } catch (Exception e) {
            log.error("getRequest failed", e);
        }
        return null;
    }

    /**
     * 发起http请求（文件传参）
     *
     * @param urlStr
     * @param params
     * @param fileData
     * @param filename
     * @return
     */
    public static HttpResult postRequest(String urlStr, Map<String, Object> params, File fileData, String filename) {
        return postRequest(urlStr, params, fileData, filename, DEFAULT_TIMEOUT_SECONDS);
    }

    /**
     * 发起http请求（文件传参+timeoutSeconds）
     *
     * @param urlStr
     * @param params
     * @param fileData
     * @param filename
     * @return
     */
    public static HttpResult postRequest(String urlStr, Map<String, Object> params, File fileData, String filename,
                                         int timeoutSeconds) {
        try {
            long curId = seqId.incrementAndGet();
            log.info("HttpRequest " + curId + " " + urlStr + "?param=" + params.toString());
            HttpPost httpPost = new HttpPost(urlStr);
            // 设置文件
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            multipartEntityBuilder.addBinaryBody(filename, fileData);
            // 设置param参数
            List<NameValuePair> data = new ArrayList<NameValuePair>();
            if (params != null && params.size() > 0) {
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    data.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
                }
            }
            URLEncodedUtils.format(data, "UTF-8");
            Iterator<NameValuePair> it = data.iterator();
            while (it.hasNext()) {
                NameValuePair param = (NameValuePair) it.next();
                multipartEntityBuilder.addTextBody(param.getName(), param.getValue());
            }
            // 将表单的值放入postMethod中
            httpPost.setEntity(multipartEntityBuilder.build());
            return doRequest(httpPost, urlStr, timeoutSeconds, curId);
        } catch (Exception e) {
            log.error("getRequest failed", e);
        }
        return null;
    }

    /**
     * 发起http get请求（map传参方式）
     *
     * @param urlStr
     * @param params
     * @return
     */
    public static HttpResult getRequest(String urlStr, Map<String, Object> params) {
        return getRequest(urlStr, params, DEFAULT_TIMEOUT_SECONDS);
    }

    /**
     * 发起http get请求（map传参方式+timeoutSeconds）
     *
     * @param urlStr
     * @param params
     * @return
     */
    public static HttpResult getRequest(String urlStr, Map<String, Object> params, int timeoutSeconds) {
        try {
            return getRequest(urlStr + getUrlParamsByMap(params), timeoutSeconds);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 发起http请求（get方式）
     *
     * @param urlStr
     * @return
     */
    public static HttpResult getRequest(String urlStr) {
        return getRequest(urlStr, DEFAULT_TIMEOUT_SECONDS);
    }

    /**
     * 发起http请求（get方式+timeoutSeconds）
     *
     * @param urlStr
     * @return
     * @throws Exception
     */
    public static HttpResult getRequest(String urlStr, int timeoutSeconds) {
        try {
            long curId = seqId.incrementAndGet();
            log.info("HttpRequest " + curId + " " + urlStr);
            HttpGet httpGet = new HttpGet(urlStr);
            return doRequest(httpGet, urlStr, timeoutSeconds, curId);
        } catch (Exception e) {
            log.error("getRequest failed", e);
        }
        return null;
    }

    /**
     * 发起http get请求（map传参+header）
     *
     * @param urlStr
     * @param header
     * @param params
     * @return
     */
    public static HttpResult getRequest(String urlStr, Map<String, String> header, Map<String, Object> params) {
        return getRequest(urlStr, header, params, DEFAULT_TIMEOUT_SECONDS);
    }

    /**
     * 发起http get请求（map传参+header+timeoutSeconds）
     *
     * @param urlStr
     * @param header
     * @param params
     * @param timeoutSeconds
     * @return
     */
    public static HttpResult getRequest(String urlStr, Map<String, String> header, Map<String, Object> params,
                                        int timeoutSeconds) {
        try {
            long curId = seqId.incrementAndGet();
            log.info("HttpRequest " + curId + " " + urlStr + "?param=" + params.toString());
            HttpGet httpGet = new HttpGet(urlStr + getUrlParamsByMap(params));
            for (String headerKey : header.keySet()) {
                httpGet.addHeader(headerKey, header.get(headerKey));
            }
            return doRequest(httpGet, urlStr, timeoutSeconds, curId);
        } catch (Exception e) {
            log.error("getRequest failed", e);
        }
        return null;
    }

    /**
     * 设置请求头及配置(目前header只有logId,后续可扩展传HttpHeader参数)
     *
     * @param httpRequest    HttpPost或HttpGet
     * @param timeoutSeconds 请求超时秒数
     */
    private static void setHeaderAndConfs(HttpRequestBase httpRequest, int timeoutSeconds) {
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeoutSeconds * 1000)
                .setConnectTimeout(timeoutSeconds * 1000).setConnectionRequestTimeout(timeoutSeconds * 1000).build();// 设置请求和传输超时时间
        httpRequest.setConfig(requestConfig);
    }

    /**
     * 默认Http请求
     *
     * @param httpPost
     * @param url
     * @param timeoutSeconds
     * @param curId
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    private static HttpResult defaultRequest(HttpRequestBase httpPost, String url, int timeoutSeconds, long curId)
            throws ClientProtocolException, IOException {
        HttpResult httpResult = new HttpResult();
        long startTime = System.currentTimeMillis();
        // 获取HttpClient
        CloseableHttpClient httpClient = getHttpClient();
        long getHttpClientEndTime = System.currentTimeMillis();
        log.info("Http getHttpClient " + curId + "  时间:" + (getHttpClientEndTime - startTime));
        // 设置请求头及配置
        setHeaderAndConfs(httpPost, timeoutSeconds);
        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
        try {
            httpResult.statusCode = httpResponse.getStatusLine().getStatusCode();
            log.info("Http Resp " + curId + " 响应状态码:" + httpResult.statusCode);
            if (httpResult.statusCode == HttpStatus.SC_OK) {
                HttpEntity entity = httpResponse.getEntity();
                String retString = EntityUtils.toString(entity, DEFAULT_CHARSET);
                long endTime = System.currentTimeMillis();
                log.info("Http Resp " + curId + " " + retString + "  响应时间:" + (endTime - getHttpClientEndTime));
                EntityUtils.consume(entity);
                httpResult.content = retString;
            }
        } finally {
            try {
                httpResponse.close();
                httpClient.close();
            } catch (IOException e) {
                log.error(curId + " 请求异常 ", e);
            }
        }
        return httpResult;
    }

    public static HttpResult doRequest(HttpRequestBase httpPost, String url, int timeoutSeconds, long curId)
            throws ClientProtocolException, IOException {
        try {
            return defaultRequest(httpPost, url, timeoutSeconds, curId);
        } catch (NoHttpResponseException e) {
            log.error(curId + " 请求异常 ", e);
            log.info("NoHttpResponseException异常,进行重试。HttpRequest " + curId + " " + url);
            return defaultRequest(httpPost, url, timeoutSeconds, curId);
        }
    }

    /**
     * 通过get方法下载文件
     */
    public static int httpDownLoad(final String path, String url) throws IOException {
        CloseableHttpClient client = getHttpClient();
        HttpGet get = new HttpGet(url);
        get.setConfig(
                RequestConfig.custom().setSocketTimeout(60 * 10 * 1000).setConnectTimeout(60 * 10 * 1000).build());
        ResponseHandler<Integer> handler = new ResponseHandler<Integer>() {
            @Override
            public Integer handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {
                int code = httpResponse.getStatusLine().getStatusCode();
                if (code == 200) {
                    File file = new File(path);
                    byte[] b = EntityUtils.toByteArray(httpResponse.getEntity());
                    try (OutputStream out = new FileOutputStream(file)) {
                        out.write(b);
                    }
                }
                return code;
            }
        };
        return client.execute(get, handler);
    }

    /**
     * 将map转成get请求的参数
     *
     * @param map
     * @return
     * @throws Exception
     */
    public static String getUrlParamsByMap(Map<String, Object> map) throws Exception {
        if (map == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        sb.append("?");
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            sb.append(entry.getKey() + "=" + URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
            sb.append("&");
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = s.substring(0, s.lastIndexOf("&"));
        }
        return s;
    }

    /**
     * 将url参数转换成map
     *
     * @param param
     * @return
     * @throws UnsupportedEncodingException
     */
    public static Map<String, String> getUrlParams(String param) throws UnsupportedEncodingException {
        Map<String, String> map = new HashMap<String, String>();
        if (StringUtils.isBlank(param)) {
            return map;
        }
        String[] params = param.split("&");
        for (int i = 0; i < params.length; i++) {
            String[] p = params[i].split("=");
            if (p.length == 2) {
                map.put(p[0], p[1]);
            }
        }
        return map;
    }
}