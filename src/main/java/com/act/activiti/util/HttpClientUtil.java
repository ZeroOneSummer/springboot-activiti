package com.act.activiti.util;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;

/****
 *
 * @author Pen 2015-12
 */
public class HttpClientUtil {
	private final static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

	private static PoolingHttpClientConnectionManager cm;
	private static RequestConfig requestConfig;
	private static LaxRedirectStrategy redirectStrategy;
	private static String EMPTY_STR = "";
	public final static String UTF8 = "UTF-8";

	private static CloseableHttpClient httpclient = null;
	
	private static IdleConnectionMonitorThread scanThread = null;
	/**
	 * 从连接池中获取到连接的最长时间
	 */
	private final static int SOCKETTIMEOUT = 60000;
	/**
	 * 设置超时时间
	 */
	private final static int CONNECTTIMEOUT = 60000;
	/**
	 * 最大链接数
	 */
	private final static int MAXTOTAL = 100;
	/**
	 * 设置每个主机地址的并发数
	 */
	private final static int MAXPERROUTE = 2;
	/**
	 * 提交请求前测试连接是否可用
	 */
	private final static int CONNECTION_REQUEST_TIMEOUT = 500;
	
	static {
		if (cm == null) {
			cm = new PoolingHttpClientConnectionManager();
			// 整个连接池最大连接数
			cm.setMaxTotal(MAXTOTAL);
			// 每路由最大连接数，默认值是2
			cm.setDefaultMaxPerRoute(MAXPERROUTE);
			requestConfig = RequestConfig.custom()
					.setSocketTimeout(SOCKETTIMEOUT)
					.setConnectTimeout(CONNECTTIMEOUT)
					.setCookieSpec(CookieSpecs.IGNORE_COOKIES)
					.setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
					.build();
			//设置重定向策略
			redirectStrategy = new LaxRedirectStrategy();
			
			
			httpclient = HttpClients.custom()
					.setConnectionManager(cm)
					.setDefaultRequestConfig(requestConfig)
					.setRedirectStrategy(redirectStrategy)
					.setConnectionManagerShared(true).build();
			// 扫描无效连接的线程
			scanThread = new IdleConnectionMonitorThread(cm);
			scanThread.start();
		}
	}
	
	
	/**
	 * @param url
	 * @return
	 */
	public static String httpGetRequest(final String url) {
		HttpGet httpGet = new HttpGet(url);
		try {
			httpGet.setProtocolVersion(HttpVersion.HTTP_1_0);
			httpGet.addHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_CLOSE);
			httpGet.setConfig(requestConfig);
			return getResult(httpGet);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return EMPTY_STR;
		} finally {
			httpGet.releaseConnection();
		}
		
	}
	
	public static String httpGetRequest(final String url, final Map<String, Object> params) throws Exception {
		ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
		URIBuilder ub = new URIBuilder().setPath(url).setParameters(pairs);
		HttpGet httpGet = new HttpGet(ub.build());
		try {
			return getResult(httpGet);
		} catch (Exception e) {
			throw new Exception("HTTP POST REQUEST ERROR: " + e.getMessage());
		} finally {
			httpGet.releaseConnection();
		}
	}
	
	public final static String httpGetRequest(final String url, final Map<String, ?> headers,
	                                          final Map<String, ?> params) throws Exception {
		ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
		URIBuilder ub = new URIBuilder().setPath(url).setParameters(pairs);
		HttpGet httpGet = new HttpGet(ub.build());
		try {
			for (Map.Entry<String, ?> param : headers.entrySet()) {
				httpGet.addHeader(param.getKey(), (String) param.getValue());
			}
			return getResult(httpGet);
		} catch (Exception e) {
			logger.error("HTTP POST REQUEST ERROR： " + e.getMessage());
			throw new Exception("HTTP GET REQUEST ERROR: " + e.getMessage());
		} finally {
			httpGet.releaseConnection();
		}
	}
	
	public static String httpPostRequest(final String url, final Map<String, ?> params) throws Exception {
		HttpPost httpPost = new HttpPost(url);
		try {
			httpPost.setConfig(requestConfig);
			ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
			httpPost.setEntity(new UrlEncodedFormEntity(pairs, UTF8));
			return getResult(httpPost);
		} catch (Exception e) {
			logger.error("HTTP POST REQUEST ERROR： " + e.getMessage());
			throw new Exception("HTTP POST REQUEST ERROR: " + e.getMessage());
		} finally {
			httpPost.releaseConnection();
		}
	}
	
	public static String httpPostRequest(final String url, final String params, final String charset) throws Exception {
		HttpPost httpPost = null;
		try {
			httpPost = new HttpPost(url);
			httpPost.setProtocolVersion(HttpVersion.HTTP_1_0);
			httpPost.addHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_CLOSE);
			httpPost.setConfig(requestConfig);
			httpPost.setEntity(new StringEntity(params, charset));
			return getResult(httpPost);
		} catch (Exception e) {
			logger.info("HTTP POST REQUEST ERROR" + e);
			throw new Exception("HTTP POST REQUEST ERROR: " + e.getMessage());
		} finally {
			httpPost.releaseConnection();
		}
	}
	
	public static String httpPostRequestHead(final String url, final String params, final String charset) throws Exception {
		HttpPost httpPost = null;
		try {
			httpPost = new HttpPost(url);
			httpPost.setProtocolVersion(HttpVersion.HTTP_1_0);
			httpPost.addHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_CLOSE);
			httpPost.setConfig(requestConfig);
			httpPost.setEntity(new StringEntity(params, charset));
			return getResultAndHead(httpPost);
		} catch (Exception e) {
			logger.info("HTTP POST REQUEST ERROR" + e);
			throw new Exception("HTTP POST REQUEST ERROR");
		} finally {
			httpPost.releaseConnection();
		}
	}
	
	public static String httpPostRequest(final String url, final String params, final String charset, final Map<String, String> header) throws Exception {
		HttpPost httpPost = null;
		try {
			httpPost = new HttpPost(url);
			httpPost.setProtocolVersion(HttpVersion.HTTP_1_0);
			httpPost.addHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_CLOSE);
			httpPost.setConfig(requestConfig);
			for (String key : header.keySet()) {
				httpPost.setHeader(key, header.get(key));
			}
			StringEntity stringEntity = new StringEntity(params, charset);
			stringEntity.setContentType("application/json");
			httpPost.setEntity(stringEntity);
			return getResult(httpPost);
		} catch (Exception e) {
			logger.info("HTTP POST REQUEST ERROR" + e);
			throw new Exception("HTTP POST REQUEST ERROR: " + e.getMessage());
		} finally {
			httpPost.releaseConnection();
		}
	}
	
	public static int httpPostStatusCode(final String url, final String params, final String charset) throws Exception {
		HttpPost httpPost = new HttpPost(url);
		try {
			httpPost.setProtocolVersion(HttpVersion.HTTP_1_0);
			httpPost.addHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_CLOSE);
			httpPost.setConfig(requestConfig);
			httpPost.setEntity(new StringEntity(params, charset));
			return getStatusCode(httpPost);
		} catch (Exception e) {
			logger.info("HTTP POST REQUEST ERROR" + e);
			throw new Exception("HTTP POST REQUEST ERROR");
			
		} finally {
			httpPost.releaseConnection();
		}
	}
	
	/**
	 * send post Request
	 *
	 * @param url
	 * @param headers
	 * @return String
	 * @throws UnsupportedEncodingException
	 */
	public static String httpPostRequest(final String url, final Map<String, ?> headers, final Map<String, ?> params, String charset) throws Exception {
		HttpPost httpPost = new HttpPost(url);
		try {
			httpPost.setProtocolVersion(HttpVersion.HTTP_1_0);
			httpPost.addHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_CLOSE);
			httpPost.setConfig(requestConfig);
			for (Map.Entry<String, ?> param : headers.entrySet()) {
				httpPost.addHeader(param.getKey(), (String) param.getValue());
			}
			ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
			httpPost.setEntity(new UrlEncodedFormEntity(pairs, charset));
			return getResult(httpPost);
		} catch (Exception e) {
			logger.info("HTTP POST REQUEST ERROR" + e);
			throw new RuntimeException("HTTP POST REQUEST ERROR: " + e.getMessage());
		} finally {
			httpPost.releaseConnection();
		}
	}
	
	
	private static ArrayList<NameValuePair> covertParams2NVPS(final Map<String, ?> params) {
		ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
		for (Map.Entry<String, ?> param : params.entrySet()) {
			pairs.add(new BasicNameValuePair(param.getKey(), (String) param.getValue()));
		}
		return pairs;
	}
	
	/**
	 * 处理Http请求
	 *
	 * @param request
	 * @return
	 */
	private final static String getResult(final HttpRequestBase request) throws Exception {
		CloseableHttpResponse response = null;
		try {
			response = httpclient.execute(request);
			HttpEntity entity = response.getEntity();
			return (entity == null) ? EMPTY_STR : EntityUtils.toString(entity);
		} catch (Exception e) {
			logger.info("HTTP POST REQUEST ERROR" + e);
			throw new RuntimeException("HTTP POST REQUEST ERROR: " + e.getMessage());
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}
	
	/**
	 * 处理Http请求
	 *
	 * @param request
	 * @return
	 */
	private static String getResultAndHead(final HttpRequestBase request) throws Exception {
		CloseableHttpResponse response = null;
		try {
			response = httpclient.execute(request);
			HttpEntity entity = response.getEntity();
			if (null == entity) {
				return EMPTY_STR;
			}
			String resultStr = EntityUtils.toString(entity);
			logger.info("result: " + resultStr);
			JSONArray result = JSONObject.parseArray(resultStr);
			result.add(response.getAllHeaders());
			return result.toJSONString();
		} catch (Exception e) {
			logger.info("HTTP POST REQUEST ERROR" + e);
			throw new RuntimeException("HTTP POST REQUEST ERROR: " + e.getMessage());
		} finally {
			if (null != response) {
				response.close();
			}
		}
	}
	
	/**
	 * 处理Http请求
	 *
	 * @param request
	 * @return
	 */
	private static int getStatusCode(final HttpRequestBase request) throws Exception {
		CloseableHttpResponse response = null;
		try {
			response = httpclient.execute(request);
			return response.getStatusLine().getStatusCode();
		} catch (ClientProtocolException e) {
			logger.error("ClientProtocolException" + e.getMessage());
			throw new RuntimeException("HTTP POST REQUEST ERROR: " + e.getMessage());
		} catch (IOException e) {
			logger.error("IOException" + e.getMessage());
			throw new RuntimeException("HTTP POST REQUEST ERROR: " + e.getMessage());
		} finally {
			if (null != response) {
				response.close();
			}
			
		}
	}
	
}