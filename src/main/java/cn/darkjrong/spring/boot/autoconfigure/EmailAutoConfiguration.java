package cn.darkjrong.spring.boot.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 电子邮件自动配置类
 *
 * @author Rong.Jia
 * @date 2021/07/26 11:10:23
 */
@Configuration
@ConditionalOnProperty(prefix = "email", name = "enabled", havingValue = "true")
@ConditionalOnClass({EmailProperties.class})
@EnableConfigurationProperties({EmailProperties.class})
public class EmailAutoConfiguration {

    private final EmailProperties emailProperties;

    public EmailAutoConfiguration(EmailProperties emailProperties) {
        this.emailProperties = emailProperties;
    }

    @Bean
    public EmailFactoryBean emailFactoryBean() {
        return new EmailFactoryBean(emailProperties);
    }






















}
