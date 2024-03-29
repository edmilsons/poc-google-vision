package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {                                    
    @Bean
    public Docket api() { 
        return new Docket(DocumentationType.SWAGGER_2)  
          .select()                                  
          .apis(RequestHandlerSelectors.any())              
          .paths(PathSelectors.any())                          
          .build();
//          .securitySchemes(Arrays.asList(securityScheme()))
//          .securityContexts(Arrays.asList(securityContext()));                                          
    }
    
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("swagger-ui.html")
//          .addResourceLocations("classpath:/META-INF/resources/");
//     
//        registry.addResourceHandler("/webjars/**")
//          .addResourceLocations("classpath:/META-INF/resources/webjars/");
//    }
//    
//    @Bean
//    public SecurityConfiguration security() {
//        return SecurityConfigurationBuilder.builder()
//            .clientId(CLIENT_ID)
//            .clientSecret(CLIENT_SECRET)
//            .scopeSeparator(" ")
//            .useBasicAuthenticationWithAccessCodeGrant(true)
//            .build();
//    }
//    
//    private SecurityScheme securityScheme() {
//        GrantType grantType = new AuthorizationCodeGrantBuilder()
//            .tokenEndpoint(new TokenEndpoint(AUTH_SERVER + "/token", "oauthtoken"))
//            .tokenRequestEndpoint(
//              new TokenRequestEndpoint(AUTH_SERVER + "/authorize", CLIENT_ID, CLIENT_SECRET))
//            .build();
//     
//        SecurityScheme oauth = new OAuthBuilder().name("spring_oauth")
//            .grantTypes(Arrays.asList(grantType))
//            .scopes(Arrays.asList(scopes()))
//            .build();
//        return oauth;
//    }
//    
//    private AuthorizationScope[] scopes() {
//        AuthorizationScope[] scopes = { 
//          new AuthorizationScope("read", "for read operations"), 
//          new AuthorizationScope("write", "for write operations"), 
//          new AuthorizationScope("foo", "Access foo API") };
//        return scopes;
//    }
//    
//    private SecurityContext securityContext() {
//        return SecurityContext.builder()
//          .securityReferences(
//            Arrays.asList(new SecurityReference("spring_oauth", scopes())))
//          .forPaths(PathSelectors.regex("/foos.*"))
//          .build();
//    }
}
