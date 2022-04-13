package cn.cxnxs.oauth.config;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Value("${swagger.enable}")
    private boolean enable;

    @Bean
    public Docket createRestApi(){

        ParameterBuilder aParameterBuilder = new ParameterBuilder();
        aParameterBuilder
                //参数类型支持header, cookie, body, query etc
                .parameterType("header")
                //参数名
                .name("token")
                //默认值
                .defaultValue("bb036ef90904bac39f3e226f2689eaf15d02f8c3a78caec6")
                .description("校验码")
                //指定参数值的类型
                .modelRef(new ModelRef("string"))
                //非必需，这里是全局配置
                .required(false)
                .build();
        List<Parameter> aParameters = new ArrayList<>();
        aParameters.add(aParameterBuilder.build());

        return new Docket(DocumentationType.SWAGGER_2)
                .enable(enable)
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .build()
                .globalOperationParameters(aParameters)
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("权限认证服务器")
                .description("基于oauth2.0权限认证服务器")
                .version("1.0")
                .build();
    }

}
