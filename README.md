# apache-commons email 封装

## 1. 使用方式
 - 自己下载install引入使用

```xml
<dependency>
    <groupId>cn.darkjrong</groupId>
    <artifactId>email-spring-boot-starter</artifactId>
    <version>1.0</version>
</dependency>
```

### 1. Spring starter方式使用
 - yml配置，必须配置enabled: true，否则默认false不起作用
```yaml
email:
  enabled: true
  host: smtp.qq.com
  port: 465
  username: 8524211265@qq.com
  password: 12312312312
  name: 123213
  charset: utf-8
  connection-timeout: 3000
  timeout: 3000
  ssl-enable: true
  bounce-enable: false
  debug: false
  send-partial: false
  ssl-check-server-identity: false
  start-tls-enabled: false
  start-tls-required: false
  pop3:
    pop-before-smtp: false
    pop-username: 12312312312@qq.com
    pop-password: 312312123123
    pop-host: pop.qq.com
```

#### 1. API 注入
```java
    
    @Autowired
    private EmailTemplate emailTemplate;

```

### 2. 工具类方式使用
```java
            @Test
            public void sendHtml() {
       
               EmailAccount emailAccount = new EmailAccount();
               emailAccount.setHost("smtp.qq.com");
               emailAccount.setPassword("12321321321");
               emailAccount.setUsername("12312321com");
               emailAccount.setPort(465);
               emailAccount.setName("加热桶");
       
               List<EmailTo> toList = new ArrayList<>();
       
               EmailTo to = new EmailTo();
               to.setMail("1232132@qq.com");
               to.setName("312312");
               toList.add(to);
       
               String htmlEmailTemplate = "<html>The apache logo - <img src=\"http://www.apache.org/images/asf_logo_wide.gif\"></html>";
               System.out.println(emailTemplate.sendHtml(emailAccount, "测试发送html", htmlEmailTemplate, toList));
            }
```































