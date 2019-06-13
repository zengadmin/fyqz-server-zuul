package com.fyqz;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@EnableSwagger2Doc
@EnableZuulProxy
@EnableEurekaClient
@EnableDiscoveryClient
@SpringBootApplication
public class FyqzServerZuulApplication {
	public static void main(String[] args) {
		System.out.println("================================================== 开始启动 zuul网关服务 =============================================================");
		System.out.println("请在控制台指定zuul网关服务的端口号 —— [端口号随意指定，注意不要与本机端口号出现冲突即可]");
		Scanner scanner = new Scanner(System.in);
		String port = scanner.nextLine(); //让用户指定端口号
		new SpringApplicationBuilder(FyqzServerZuulApplication.class).properties("server.port=" + port).run(args);//启动项目
		System.out.println("================================================== zuul网关服务 启动成功 =============================================================");
	}

	@Component
	@Primary
	class DocumentationConfig implements SwaggerResourcesProvider {
		@Override
		public List<SwaggerResource> get() {
			List resources = new ArrayList<>();
			resources.add(swaggerResource("api-user", "/api-user/v2/api-docs", "2.0"));
			resources.add(swaggerResource("api-chess", "/api-chess/v2/api-docs", "2.0"));
			resources.add(swaggerResource("api-scenario", "/api-scenario/v2/api-docs", "2.0"));
			return resources;
		}
		private SwaggerResource swaggerResource(String name, String location, String version) {
			SwaggerResource swaggerResource = new SwaggerResource();
			swaggerResource.setName(name);
			swaggerResource.setLocation(location);
			swaggerResource.setSwaggerVersion(version);
			return swaggerResource;
		}
	}
}
