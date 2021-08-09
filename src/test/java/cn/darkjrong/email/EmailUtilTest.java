package cn.darkjrong.email;

import cn.darkjrong.email.domain.EmailTo;
import cn.darkjrong.spring.boot.autoconfigure.EmailProperties;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 电子邮件工具类测试类
 *
 * @author Rong.Jia
 * @date 2021/07/26 13:36:38
 */
public class EmailUtilTest {

    private static EmailTemplate emailTemplate;

    private static List<EmailTo> toList = new ArrayList<>();
    private static List<EmailTo> bccList = new ArrayList<>();
    private static List<EmailTo> ccList = new ArrayList<>();
    private static List<EmailTo> replyList = new ArrayList<>();

    static {

        EmailTo to = new EmailTo();
        to.setMail("rong.jia@xdcplus.com");
        to.setName("贾荣1");
        toList.add(to);

        EmailTo cc = new EmailTo();
        cc.setMail("martin.ji@xdcplus.com");
        cc.setName("嵇浩");
        ccList.add(cc);

        EmailTo bcc = new EmailTo();
        bcc.setMail("fish.fei@xdcplus.com");
        bcc.setName("费戌辉");
        bccList.add(bcc);

        EmailTo reply = new EmailTo();
        reply.setMail("black.hua@xdcplus.com");
        reply.setName("华俊龙");
        replyList.add(reply);

        EmailProperties emailProperties = new EmailProperties();
        emailProperties.setEnabled(Boolean.TRUE);
        emailProperties.setHost("smtp.qq.com");
        emailProperties.setPort(465);

        emailProperties.setSslEnable(Boolean.TRUE);
        emailProperties.setDebug(Boolean.FALSE);
        emailProperties.setName("贾荣");
        emailProperties.setUsername("85465@qq.com");
        emailProperties.setPassword("2312312");

        emailTemplate = new EmailTemplate(emailProperties);

    }

    @Test
    public void senText() {

        String sendText = emailTemplate.sendText( "对接测试", "This is a test mail ... :-)", toList);

        System.out.println(sendText);

    }

    @Test
    public void sendFile() {

        File file = new File("F:\\我的图片\\1.jpeg");

        String sendFile = emailTemplate.sendFile("对接测试发送文件", "This is a test mail ... :-)", file, toList);

        System.out.println(sendFile);

    }

    @Test
    public void sendHtml() {

        String html = ".... <img src=\"http://www.apache.org/images/feather.gif\"> ....";
        String sendHtml = emailTemplate.sendHtml("对接测试发送HTML", html, toList);
        System.out.println(sendHtml);

    }















}
