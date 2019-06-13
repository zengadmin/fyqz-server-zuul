/*
 * Copyright (c) 2018. paascloud.net All Rights Reserved.
 * 项目名称：paascloud快速搭建企业级分布式微服务平台
 * 类名称：UacFallbackProvider.java
 * 创建人：刘兆明
 * 联系方式：paascloud.net@gmail.com
 * 开源地址: https://github.com/paascloud
 * 博客地址: http://blog.paascloud.net
 * 项目官网: http://paascloud.net
 */

package com.fyqz.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;


@Component
@Slf4j
public class FallbackProvider implements org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider {

	@Override
	public String getRoute() {
		return "*";
	}

	@Override
	public ClientHttpResponse fallbackResponse(String s, Throwable throwable) {
		return this.response(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	private ClientHttpResponse response(final HttpStatus status) {
		return new ClientHttpResponse() {
			@Override
			public HttpStatus getStatusCode() throws IOException {
				return status;
			}

			@Override
			public int getRawStatusCode() throws IOException {
				return status.value();
			}

			@Override
			public String getStatusText() throws IOException {
				return status.getReasonPhrase();
			}

			@Override
			public void close() {
			}

			@Override
			public InputStream getBody() throws IOException {
				return new ByteArrayInputStream("服务不可用，请稍后再试。".getBytes());
			}

			@Override
			public HttpHeaders getHeaders() {
				// headers设定
				HttpHeaders headers = new HttpHeaders();
				MediaType mt = new MediaType("application", "json", Charset.forName("UTF-8"));
				headers.setContentType(mt);
				return headers;
			}
		};
	}
}
