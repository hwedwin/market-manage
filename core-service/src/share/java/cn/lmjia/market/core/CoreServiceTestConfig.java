package cn.lmjia.market.core;

import cn.lmjia.market.core.config.CoreConfig;
import com.huotu.vefification.test.VerificationCodeTestConfig;
import me.jiangcai.chanpay.event.TradeEvent;
import me.jiangcai.chanpay.test.ChanpayTestSpringConfig;
import me.jiangcai.lib.test.config.H2DataSourceConfig;
import me.jiangcai.payment.PayableOrder;
import me.jiangcai.payment.chanpay.entity.ChanpayPayOrder;
import me.jiangcai.payment.chanpay.service.ChanpayPaymentForm;
import me.jiangcai.payment.entity.PayOrder;
import me.jiangcai.payment.exception.SystemMaintainException;
import me.jiangcai.payment.test.PaymentTestConfig;
import me.jiangcai.payment.test.service.MockPayToggle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.io.IOException;
import java.security.SignatureException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author CJ
 */
@Configuration
@ImportResource("classpath:/datasource_local.xml")
@PropertySource("classpath:/test_wx.properties")
@Import({CoreConfig.class, ChanpayTestSpringConfig.class, PaymentTestConfig.class, VerificationCodeTestConfig.class})
@ComponentScan("cn.lmjia.market.core.test")
public class CoreServiceTestConfig extends H2DataSourceConfig implements WebMvcConfigurer {

    private static final Log log = LogFactory.getLog(CoreServiceTestConfig.class);

    @Bean
    @Primary
    public ChanpayPaymentForm chanpayPaymentForm() {
        return new ChanpayPaymentForm() {
            @Override
            public void tradeUpdate(TradeEvent event) throws IOException, SignatureException {

            }

            @Override
            public PayOrder newPayOrder(HttpServletRequest request, PayableOrder order, Map<String, Object> additionalParameters) throws SystemMaintainException {
                log.debug("准备创建金额为" + order.getOrderDueAmount() + "的" + order.getOrderProductName() + "订单");
                if (order.getOrderDueAmount().intValue() == 0)
                    throw new IllegalStateException("错误的金额：" + order.getOrderDueAmount());
                ChanpayPayOrder chanpayPayOrder = new ChanpayPayOrder();
                chanpayPayOrder.setPlatformId(UUID.randomUUID().toString());
                if (additionalParameters != null && additionalParameters.containsKey("desktop")) {
                    // successUri
                    // 给一个uri 直接302 successUri
                    chanpayPayOrder.setUrl("/redirectSuccessUri");
                } else
                    chanpayPayOrder.setUrl(UUID.randomUUID().toString());
                return chanpayPayOrder;
            }
        };
    }

    @Bean
    public MockPayToggle mockPayToggle() {
        return (payableOrder, payOrder) -> {
            if (MockSetting.AutoPay)
                return 1;
            return null;
        };
    }

    @Bean
    public DataSource dataSource() {
        return memDataSource("cn/lmjia/market");
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {

    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {

    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {

    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {

    }

    @Override
    public void addFormatters(FormatterRegistry registry) {

    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {

    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {

    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        // 即使没有安全系统；依然可以根据 AuthenticationPrincipal 获取当前登录状态
        argumentResolvers.add(new AuthenticationPrincipalArgumentResolver());
    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {

    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {

    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {

    }

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {

    }

    @Override
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {

    }

    @Override
    public Validator getValidator() {
        return null;
    }

    @Override
    public MessageCodesResolver getMessageCodesResolver() {
        return null;
    }
}