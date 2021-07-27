package cn.darkjrong.email;

import cn.darkjrong.email.domain.EmailAccount;
import cn.darkjrong.email.domain.EmailTo;
import cn.darkjrong.email.exception.ExceptionEnum;
import cn.darkjrong.email.exception.MailException;
import cn.darkjrong.spring.boot.autoconfigure.EmailFactoryBean;
import cn.darkjrong.spring.boot.autoconfigure.EmailProperties;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import org.apache.commons.mail.*;
import org.apache.commons.mail.resolver.DataSourceCompositeResolver;
import org.apache.commons.mail.resolver.DataSourceFileResolver;
import org.apache.commons.mail.resolver.DataSourceUrlResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.List;


/**
 * 电子邮件工具类
 *
 * @author Rong.Jia
 * @date 2021/07/26 13:11:22
 */
public class EmailUtil {

    private static final String PROTOCOL_REG = "^(http|https|ftp)://.*$";
    private static final Logger logger = LoggerFactory.getLogger(EmailUtil.class);

    /**
     * 发送文本邮件
     *
     * @param subject      主题
     * @param message      消息
     * @param toEmails     收件人
     * @param emailAccount 账号信息
     * @return {@link String} 邮件消息ID
     * @throws MailException 电子邮件异常
     */
    public static String sendText(EmailAccount emailAccount, String subject, String message,
                                  List<EmailTo> toEmails) throws MailException {
        return sendText(emailAccount, subject, message, toEmails, Collections.emptyList());
    }

    /**
     * 发送文本邮件
     *
     * @param subject 主题
     * @param message 消息
     * @param toEmail 收件人
     * @param emailAccount 账号信息
     * @return {@link String} 邮件消息ID
     * @throws MailException 电子邮件异常
     */
    public static String sendText(EmailAccount emailAccount, String subject, String message,
                                  EmailTo toEmail) throws MailException {

        Assert.notNull(toEmail, ExceptionEnum.THE_RECEIVER_CANNOT_BE_EMPTY.getValue());
        Assert.notNull(toEmail.getMail(), ExceptionEnum.THE_RECEIVER_CANNOT_BE_EMPTY.getValue());

        return sendText(emailAccount, subject, message, Collections.singletonList(toEmail));
    }

    /**
     * 发送文本邮件
     *
     * @param subject  主题
     * @param message  消息
     * @param toEmails 收件邮箱
     * @param emailAccount 账号信息
     * @param ccEmails 抄送邮箱
     * @return {@link String} 邮件消息ID
     * @throws MailException 电子邮件异常
     */
    public static String sendText(EmailAccount emailAccount, String subject, String message,
                                  List<EmailTo> toEmails,
                                  List<EmailTo> ccEmails) throws MailException {
        return sendText(emailAccount, subject, message, toEmails, ccEmails, Collections.emptyList());
    }

    /**
     * 发送文本邮件
     *
     * @param subject   主题
     * @param message   消息
     * @param toEmails  收件人
     * @param bccEmails 密送邮箱
     * @param ccEmails  抄送邮箱
     * @param emailAccount 账号信息
     * @return {@link String} 邮件消息ID
     * @throws MailException 电子邮件异常
     */
    public static String sendText(EmailAccount emailAccount, String subject, String message,
                                  List<EmailTo> toEmails,
                                  List<EmailTo> ccEmails,
                                  List<EmailTo> bccEmails) throws MailException {
        return sendText(emailAccount, subject, message, toEmails, ccEmails, bccEmails, Collections.emptyList());
    }

    /**
     * 发送文本邮件
     *
     * @param subject     主题
     * @param message     消息
     * @param toEmails    收件人
     * @param bccEmails   bcc邮箱
     * @param ccEmails    抄送邮箱
     * @param emailAccount 账号信息
     * @param replyEmails 回复邮箱
     * @return {@link String} 邮件消息ID
     * @throws MailException 电子邮件异常
     */
    public static String sendText(EmailAccount emailAccount, String subject, String message,
                                  List<EmailTo> toEmails,
                                  List<EmailTo> ccEmails,
                                  List<EmailTo> bccEmails,
                                  List<EmailTo> replyEmails) throws MailException {

        EmailTemplate emailTemplate = getTemplate(emailAccount);
        return emailTemplate.sendText(subject, message, toEmails, ccEmails, bccEmails, replyEmails);
    }

    /**
     * 发送文件邮件
     *
     * @param subject    主题
     * @param message    消息
     * @param attachment 文件
     * @param emailAccount 账号信息
     * @param toEmails   收件邮件
     * @return {@link String} 邮件消息ID
     * @throws MailException 电子邮件异常
     */
    public static String sendFile(EmailAccount emailAccount, String subject, String message,
                                  File attachment,
                                  List<EmailTo> toEmails) throws MailException {

        Assert.isTrue(EmailUtil.checkFileExists(attachment.getPath()), ExceptionEnum.ATTACHMENT_DOES_NOT_EXIST.getValue());

        return sendFile(emailAccount, subject, message, attachment, toEmails, Collections.emptyList());
    }

    /**
     * 发送文件邮件
     *
     * @param subject    主题
     * @param message    消息
     * @param attachment 文件
     * @param emailAccount 账号信息
     * @param toEmail    收件邮件
     * @return {@link String} 邮件消息ID
     * @throws MailException 电子邮件异常
     */
    public static String sendFile(EmailAccount emailAccount, String subject, String message,
                                  File attachment,
                                  EmailTo toEmail) throws MailException {

        Assert.isTrue(EmailUtil.checkFileExists(attachment.getPath()), ExceptionEnum.ATTACHMENT_DOES_NOT_EXIST.getValue());
        Assert.notNull(toEmail, ExceptionEnum.THE_RECEIVER_CANNOT_BE_EMPTY.getValue());
        Assert.notNull(toEmail.getMail(), ExceptionEnum.THE_RECEIVER_CANNOT_BE_EMPTY.getValue());

        return sendFile(emailAccount, subject, message, attachment, Collections.singletonList(toEmail));
    }

    /**
     * 发送文件邮件
     *
     * @param subject     主题
     * @param message     消息
     * @param attachment  文件
     * @param emailAccount 账号信息
     * @param toEmails    收件邮件
     * @param bccEmails   抄送邮箱
     * @param ccEmails    抄送邮箱
     * @param replyEmails 回复邮箱
     * @return {@link String} 邮件消息ID
     * @throws MailException 电子邮件异常
     */
    public static String sendFile(EmailAccount emailAccount, String subject, String message,
                                  File attachment,
                                  List<EmailTo> toEmails,
                                  List<EmailTo> ccEmails,
                                  List<EmailTo> bccEmails,
                                  List<EmailTo> replyEmails) throws MailException {
        Assert.isTrue(EmailUtil.checkFileExists(attachment.getPath()), ExceptionEnum.ATTACHMENT_DOES_NOT_EXIST.getValue());
        return sendFile(emailAccount, subject, message, attachment.getPath(), toEmails, ccEmails, bccEmails, replyEmails);
    }

    /**
     * 发送文件邮件
     *
     * @param subject    主题
     * @param message    消息
     * @param attachment 文件
     * @param toEmails   邮件
     * @param emailAccount 账号信息
     * @param bccEmails  密送邮箱
     * @param ccEmails   抄送邮箱
     * @return {@link String} 邮件消息ID
     * @throws MailException 电子邮件异常
     */
    public static String sendFile(EmailAccount emailAccount, String subject, String message,
                                  File attachment,
                                  List<EmailTo> toEmails,
                                  List<EmailTo> ccEmails,
                                  List<EmailTo> bccEmails) throws MailException {

        Assert.isTrue(EmailUtil.checkFileExists(attachment.getPath()), ExceptionEnum.ATTACHMENT_DOES_NOT_EXIST.getValue());

        return sendFile(emailAccount, subject, message, attachment,
                toEmails, ccEmails, bccEmails, Collections.emptyList());
    }

    /**
     * 发送文件邮件
     *
     * @param subject    主题
     * @param message    消息
     * @param attachment 文件
     * @param toEmails   邮件
     * @param ccEmails   抄送邮箱
     * @param emailAccount 账号信息
     * @return {@link String} 邮件消息ID
     * @throws MailException 电子邮件异常
     */
    public static String sendFile(EmailAccount emailAccount, String subject, String message,
                                  File attachment,
                                  List<EmailTo> toEmails,
                                  List<EmailTo> ccEmails) throws MailException {

        Assert.isTrue(EmailUtil.checkFileExists(attachment.getPath()), ExceptionEnum.ATTACHMENT_DOES_NOT_EXIST.getValue());

        return sendFile(emailAccount, subject, message, attachment, toEmails, ccEmails, Collections.emptyList());
    }

    /**
     * 发送文件邮件
     *
     * @param subject     主题
     * @param message     消息
     * @param attachment  附件
     * @param toEmails    邮件
     * @param bccEmails   密送邮箱
     * @param ccEmails    抄送邮箱
     * @param replyEmails 回复邮箱
     * @param emailAccount 账号信息
     * @return {@link String} 邮件消息ID
     * @throws MailException 电子邮件异常
     */
    public static String sendFile(EmailAccount emailAccount, String subject, String message,
                                  String attachment, List<EmailTo> toEmails,
                                  List<EmailTo> ccEmails,
                                  List<EmailTo> bccEmails,
                                  List<EmailTo> replyEmails) throws MailException {

        Assert.isTrue(EmailUtil.checkFileExists(attachment), ExceptionEnum.ATTACHMENT_DOES_NOT_EXIST.getValue());

        EmailTemplate emailTemplate = getTemplate(emailAccount);
        return emailTemplate.sendFile(subject, message, attachment, toEmails, ccEmails, bccEmails, replyEmails);
    }

    /**
     * 发送文件邮件
     *
     * @param subject    主题
     * @param message    消息
     * @param attachment 附件
     * @param toEmails   邮件
     * @param bccEmails  密送邮箱
     * @param ccEmails   抄送邮箱
     * @param emailAccount 账号信息
     * @return {@link String} 邮件消息ID
     * @throws MailException 电子邮件异常
     */
    public static String sendFile(EmailAccount emailAccount, String subject, String message,
                                  String attachment, List<EmailTo> toEmails,
                                  List<EmailTo> ccEmails,
                                  List<EmailTo> bccEmails) throws MailException {

        Assert.isTrue(EmailUtil.checkFileExists(attachment), ExceptionEnum.ATTACHMENT_DOES_NOT_EXIST.getValue());

        return sendFile(emailAccount, subject, message, attachment,
                toEmails, ccEmails, bccEmails, Collections.emptyList());
    }

    /**
     * 发送文件邮件
     *
     * @param subject    主题
     * @param message    消息
     * @param attachment 附件
     * @param toEmails   邮件
     * @param ccEmails   抄送邮箱
     * @param emailAccount 账号信息
     * @return {@link String} 邮件消息ID
     * @throws MailException 电子邮件异常
     */
    public static String sendFile(EmailAccount emailAccount, String subject, String message,
                                  String attachment,
                                  List<EmailTo> toEmails,
                                  List<EmailTo> ccEmails) throws MailException {
        Assert.isTrue(EmailUtil.checkFileExists(attachment), ExceptionEnum.ATTACHMENT_DOES_NOT_EXIST.getValue());
        return sendFile(emailAccount, subject, message, attachment,
                toEmails, ccEmails, Collections.emptyList());
    }

    /**
     * 发送文件邮件
     *
     * @param subject    主题
     * @param message    消息
     * @param attachment 附件
     * @param toEmails   邮件
     * @param emailAccount 账号信息
     * @return {@link String} 邮件消息ID
     * @throws MailException 电子邮件异常
     */
    public static String sendFile(EmailAccount emailAccount, String subject, String message,
                                  String attachment,
                                  List<EmailTo> toEmails) throws MailException {
        Assert.isTrue(EmailUtil.checkFileExists(attachment), ExceptionEnum.ATTACHMENT_DOES_NOT_EXIST.getValue());
        return sendFile(emailAccount, subject, message, attachment, toEmails, Collections.emptyList());
    }

    /**
     * 发送文件邮件
     *
     * @param subject    主题
     * @param message    消息
     * @param attachment 附件
     * @param toEmail    邮件
     * @param emailAccount 账号信息
     * @return {@link String} 邮件消息ID
     * @throws MailException 电子邮件异常
     */
    public static String sendFile(EmailAccount emailAccount, String subject, String message,
                                  String attachment, EmailTo toEmail) throws MailException {
        Assert.isTrue(EmailUtil.checkFileExists(attachment), ExceptionEnum.ATTACHMENT_DOES_NOT_EXIST.getValue());
        Assert.notNull(toEmail, ExceptionEnum.THE_RECEIVER_CANNOT_BE_EMPTY.getValue());
        Assert.notNull(toEmail.getMail(), ExceptionEnum.THE_RECEIVER_CANNOT_BE_EMPTY.getValue());
        return sendFile(emailAccount, subject, message, attachment, Collections.singletonList(toEmail));
    }

    /**
     * 发送html
     *
     * @param subject  主题
     * @param html     超文本标记语言
     * @param toEmails 邮件
     * @param emailAccount 账号信息
     * @return {@link String} 邮件消息ID
     * @throws MailException 电子邮件异常
     */
    public static String sendHtml(EmailAccount emailAccount, String subject, String html,
                                  List<EmailTo> toEmails) throws MailException {

        return sendHtml(emailAccount, subject, html, toEmails, Collections.emptyList());
    }

    /**
     * 发送html
     *
     * @param subject 主题
     * @param html    超文本标记语言
     * @param toEmail 邮件
     * @param emailAccount 账号信息
     * @return {@link String} 邮件消息ID
     * @throws MailException 电子邮件异常
     */
    public static String sendHtml(EmailAccount emailAccount, String subject, String html, EmailTo toEmail) throws MailException {

        Assert.notBlank(html, ExceptionEnum.THE_MESSAGE_CANNOT_BE_EMPTY.getValue());
        Assert.notNull(toEmail, ExceptionEnum.THE_RECEIVER_CANNOT_BE_EMPTY.getValue());
        Assert.notNull(toEmail.getMail(), ExceptionEnum.THE_RECEIVER_CANNOT_BE_EMPTY.getValue());

        return sendHtml(emailAccount, subject, html, Collections.singletonList(toEmail), Collections.emptyList());
    }

    /**
     * 发送html
     *
     * @param subject     主题
     * @param html        超文本标记语言
     * @param toEmails    邮件
     * @param bccEmails   密送邮箱
     * @param ccEmails    抄送邮箱
     * @param replyEmails 回复邮箱
     * @param emailAccount 账号信息
     * @return {@link String} 邮件消息ID
     * @throws MailException 电子邮件异常
     */
    public static String sendHtml(EmailAccount emailAccount,
                                  String subject, String html,
                                  List<EmailTo> toEmails,
                                  List<EmailTo> ccEmails,
                                  List<EmailTo> bccEmails,
                                  List<EmailTo> replyEmails) throws MailException {

        Assert.notBlank(html, ExceptionEnum.THE_MESSAGE_CANNOT_BE_EMPTY.getValue());

        EmailTemplate emailTemplate = getTemplate(emailAccount);
        return emailTemplate.sendHtml(subject, html, toEmails, ccEmails, bccEmails, replyEmails);

    }

    /**
     * 发送html
     *
     * @param subject   主题
     * @param html      超文本标记语言
     * @param toEmails  邮件
     * @param bccEmails 密送邮箱
     * @param ccEmails  抄送邮箱
     * @param emailAccount 账号信息
     * @return {@link String} 邮件消息ID
     * @throws MailException 电子邮件异常
     */
    public static String sendHtml(EmailAccount emailAccount, String subject, String html,
                                  List<EmailTo> toEmails,
                                  List<EmailTo> ccEmails,
                                  List<EmailTo> bccEmails) throws MailException {

        return sendHtml(emailAccount, subject, html, toEmails, ccEmails, bccEmails, Collections.emptyList());
    }

    /**
     * 发送html
     *
     * @param subject  主题
     * @param html     超文本标记语言
     * @param toEmails 邮件
     * @param ccEmails 抄送送邮箱
     * @param emailAccount 账号信息
     * @return {@link String} 邮件消息ID
     * @throws MailException 电子邮件异常
     */
    public static String sendHtml(EmailAccount emailAccount, String subject, String html,
                                  List<EmailTo> toEmails,
                                  List<EmailTo> ccEmails) throws MailException {

        return sendHtml(emailAccount, subject, html, toEmails, ccEmails, Collections.emptyList());
    }

    /**
     * 发送文本邮件
     *
     * @param subject  主题
     * @param message  消息
     * @param toEmails 收件人
     * @param date 发送时间
     * @param emailAccount 账号信息
     * @return {@link String} 邮件消息ID
     * @throws MailException 电子邮件异常
     */
    public static String sendText(EmailAccount emailAccount, String subject, String message,
                           List<EmailTo> toEmails, Date date) throws MailException {
        return sendText(emailAccount, subject, message, toEmails, Collections.emptyList(), date);
    }

    /**
     * 发送文本邮件
     *
     * @param subject  主题
     * @param message  消息
     * @param toEmail 收件人
     * @param emailAccount 账号信息
     * @param date 发送时间
     * @return {@link String} 邮件消息ID
     * @throws MailException 电子邮件异常
     */
    public static String sendText(EmailAccount emailAccount, String subject, String message,
                           EmailTo toEmail, Date date) throws MailException {

        Assert.notNull(toEmail, ExceptionEnum.THE_RECEIVER_CANNOT_BE_EMPTY.getValue());
        Assert.notNull(toEmail.getMail(), ExceptionEnum.THE_RECEIVER_CANNOT_BE_EMPTY.getValue());

        return sendText(emailAccount, subject, message, Collections.singletonList(toEmail), date);
    }

    /**
     * 发送文本邮件
     *
     * @param subject   主题
     * @param message   消息
     * @param toEmails  收件邮箱
     * @param ccEmails 抄送邮箱
     * @param date 发送时间
     * @param emailAccount 账号信息
     * @return {@link String} 邮件消息ID
     * @throws MailException 电子邮件异常
     */
    public static String sendText(EmailAccount emailAccount, String subject, String message,
                           List<EmailTo> toEmails,
                           List<EmailTo> ccEmails, Date date) throws MailException {
        return sendText(emailAccount, subject, message, toEmails, ccEmails, Collections.emptyList(), date);
    }

    /**
     * 发送文本邮件
     *
     * @param subject   主题
     * @param message   消息
     * @param toEmails  收件人
     * @param bccEmails 密送邮箱
     * @param ccEmails  抄送邮箱
     * @param date 发送时间
     * @param emailAccount 账号信息
     * @return {@link String} 邮件消息ID
     * @throws MailException 电子邮件异常
     */
    public static String sendText(EmailAccount emailAccount, String subject, String message,
                           List<EmailTo> toEmails,
                           List<EmailTo> ccEmails,
                           List<EmailTo> bccEmails, Date date) throws MailException {
        return sendText(emailAccount, subject, message, toEmails, ccEmails, bccEmails, Collections.emptyList(), date);
    }

    /**
     * 发送文本邮件
     *
     * @param subject     主题
     * @param message     消息
     * @param toEmails    收件人
     * @param bccEmails   bcc邮箱
     * @param ccEmails    抄送邮箱
     * @param replyEmails 回复邮箱
     * @param date 发送时间
     * @param emailAccount 账号信息
     * @return {@link String} 邮件消息ID
     * @throws MailException 电子邮件异常
     */
    public static String sendText(EmailAccount emailAccount, String subject, String message,
                           List<EmailTo> toEmails,
                           List<EmailTo> ccEmails,
                           List<EmailTo> bccEmails,
                           List<EmailTo> replyEmails, Date date) throws MailException {

        EmailTemplate template = getTemplate(emailAccount);
        return template.sendText(subject, message, toEmails, ccEmails, bccEmails, replyEmails, date);
    }

    /**
     * 发送文件邮件
     *
     * @param subject    主题
     * @param message    消息
     * @param attachment 文件
     * @param toEmails   收件邮件
     * @param date 发送时间
     * @param emailAccount 账号信息
     * @return {@link String} 邮件消息ID
     * @throws MailException 电子邮件异常
     */
    public static String sendFile(EmailAccount emailAccount, String subject, String message,
                           File attachment,
                           List<EmailTo> toEmails, Date date) throws MailException {

        Assert.isTrue(EmailUtil.checkFileExists(attachment.getPath()), ExceptionEnum.ATTACHMENT_DOES_NOT_EXIST.getValue());

        return sendFile(emailAccount, subject, message, attachment, toEmails, Collections.emptyList(), date);
    }

    /**
     * 发送文件邮件
     *
     * @param subject    主题
     * @param message    消息
     * @param attachment 文件
     * @param date 发送时间
     * @param toEmail   收件邮件
     * @param emailAccount 账号信息
     * @return {@link String} 邮件消息ID
     * @throws MailException 电子邮件异常
     */
    public static String sendFile(EmailAccount emailAccount, String subject, String message,
                           File attachment,
                           EmailTo toEmail, Date date) throws MailException {

        Assert.isTrue(EmailUtil.checkFileExists(attachment.getPath()), ExceptionEnum.ATTACHMENT_DOES_NOT_EXIST.getValue());
        Assert.notNull(toEmail, ExceptionEnum.THE_RECEIVER_CANNOT_BE_EMPTY.getValue());
        Assert.notNull(toEmail.getMail(), ExceptionEnum.THE_RECEIVER_CANNOT_BE_EMPTY.getValue());

        return sendFile(emailAccount, subject, message, attachment, Collections.singletonList(toEmail), date);
    }

    /**
     * 发送文件邮件
     *
     * @param subject     主题
     * @param message     消息
     * @param attachment  文件
     * @param toEmails    收件邮件
     * @param bccEmails   抄送邮箱
     * @param ccEmails    抄送邮箱
     * @param date 发送时间
     * @param emailAccount 账号信息
     * @param replyEmails 回复邮箱
     * @return {@link String} 邮件消息ID
     * @throws MailException 电子邮件异常
     */
    public static String sendFile(EmailAccount emailAccount, String subject, String message,
                           File attachment,
                           List<EmailTo> toEmails,
                           List<EmailTo> ccEmails,
                           List<EmailTo> bccEmails,
                           List<EmailTo> replyEmails,
                           Date date) throws MailException {
        Assert.isTrue(EmailUtil.checkFileExists(attachment.getPath()), ExceptionEnum.ATTACHMENT_DOES_NOT_EXIST.getValue());
        return sendFile(emailAccount, subject, message, attachment.getPath(),
                toEmails, ccEmails, bccEmails, replyEmails, date);
    }

    /**
     * 发送文件邮件
     *
     * @param subject    主题
     * @param message    消息
     * @param attachment 文件
     * @param toEmails   邮件
     * @param bccEmails  密送邮箱
     * @param ccEmails   抄送邮箱
     * @param date 发送时间
     * @param emailAccount 账号信息
     * @return {@link String} 邮件消息ID
     * @throws MailException 电子邮件异常
     */
    public static String sendFile(EmailAccount emailAccount, String subject, String message,
                           File attachment,
                           List<EmailTo> toEmails,
                           List<EmailTo> ccEmails,
                           List<EmailTo> bccEmails, Date date) throws MailException {

        Assert.isTrue(EmailUtil.checkFileExists(attachment.getPath()), ExceptionEnum.ATTACHMENT_DOES_NOT_EXIST.getValue());

        return sendFile(emailAccount, subject, message, attachment,
                toEmails, ccEmails, bccEmails, Collections.emptyList(), date);
    }

    /**
     * 发送文件邮件
     *
     * @param subject    主题
     * @param message    消息
     * @param attachment 文件
     * @param toEmails   邮件
     * @param date 发送时间
     * @param emailAccount 账号信息
     * @param ccEmails  抄送邮箱
     * @return {@link String} 邮件消息ID
     * @throws MailException 电子邮件异常
     */
    public static String sendFile(EmailAccount emailAccount, String subject, String message,
                           File attachment,
                           List<EmailTo> toEmails,
                           List<EmailTo> ccEmails, Date date) throws MailException {

        Assert.isTrue(EmailUtil.checkFileExists(attachment.getPath()), ExceptionEnum.ATTACHMENT_DOES_NOT_EXIST.getValue());

        return sendFile(emailAccount, subject, message, attachment,
                toEmails, ccEmails, Collections.emptyList(), date);
    }

    /**
     * 发送文件邮件
     *
     * @param subject     主题
     * @param message     消息
     * @param attachment  附件
     * @param toEmails    邮件
     * @param bccEmails   密送邮箱
     * @param emailAccount 账号信息
     * @param ccEmails    抄送邮箱
     * @param date 发送时间
     * @param replyEmails 回复邮箱
     * @return {@link String} 邮件消息ID
     * @throws MailException 电子邮件异常
     */
    public static String sendFile(EmailAccount emailAccount, String subject, String message,
                           String attachment, List<EmailTo> toEmails,
                           List<EmailTo> ccEmails,
                           List<EmailTo> bccEmails,
                           List<EmailTo> replyEmails, Date date) throws MailException {

        Assert.isTrue(EmailUtil.checkFileExists(attachment), ExceptionEnum.ATTACHMENT_DOES_NOT_EXIST.getValue());

        return getTemplate(emailAccount).sendFile(subject, message, attachment, toEmails, ccEmails, bccEmails, replyEmails, date);
    }

    /**
     * 发送文件邮件
     *
     * @param subject    主题
     * @param message    消息
     * @param attachment 附件
     * @param emailAccount 账号信息
     * @param toEmails   邮件
     * @param bccEmails  密送邮箱
     * @param ccEmails   抄送邮箱
     * @param date 发送时间
     * @return {@link String} 邮件消息ID
     * @throws MailException 电子邮件异常
     */
    public static String sendFile(EmailAccount emailAccount, String subject, String message,
                           String attachment, List<EmailTo> toEmails,
                           List<EmailTo> ccEmails,
                           List<EmailTo> bccEmails, Date date) throws MailException {

        Assert.isTrue(EmailUtil.checkFileExists(attachment), ExceptionEnum.ATTACHMENT_DOES_NOT_EXIST.getValue());

        return sendFile(emailAccount, subject, message, attachment,
                toEmails, ccEmails, bccEmails, Collections.emptyList(), date);
    }

    /**
     * 发送文件邮件
     *
     * @param subject    主题
     * @param message    消息
     * @param attachment 附件
     * @param toEmails   邮件
     * @param ccEmails  抄送邮箱
     * @param date 发送时间
     * @param emailAccount 账号信息
     * @return {@link String} 邮件消息ID
     * @throws MailException 电子邮件异常
     */
    public static String sendFile(EmailAccount emailAccount, String subject, String message,
                           String attachment,
                           List<EmailTo> toEmails,
                           List<EmailTo> ccEmails, Date date) throws MailException {
        Assert.isTrue(EmailUtil.checkFileExists(attachment), ExceptionEnum.ATTACHMENT_DOES_NOT_EXIST.getValue());
        return sendFile(emailAccount, subject, message, attachment,
                toEmails, ccEmails, Collections.emptyList(), date);
    }

    /**
     * 发送文件
     * 发送文件邮件
     *
     * @param subject    主题
     * @param message    消息
     * @param attachment 附件
     * @param toEmails   邮件
     * @param date 发送时间
     * @param emailAccount 账号信息
     * @return {@link String} 邮件消息ID
     * @throws MailException 电子邮件异常
     */
    public static String sendFile(EmailAccount emailAccount, String subject, String message,
                           String attachment,
                           List<EmailTo> toEmails, Date date) throws MailException {
        Assert.isTrue(EmailUtil.checkFileExists(attachment), ExceptionEnum.ATTACHMENT_DOES_NOT_EXIST.getValue());
        return sendFile(emailAccount, subject, message, attachment, toEmails, Collections.emptyList(), date);
    }

    /**
     * 发送文件
     * 发送文件邮件
     *
     * @param subject    主题
     * @param message    消息
     * @param attachment 附件
     * @param toEmail   邮件
     * @param emailAccount 账号信息
     * @param date 发送时间
     * @return {@link String} 邮件消息ID
     * @throws MailException 电子邮件异常
     */
    public static String sendFile(EmailAccount emailAccount, String subject, String message,
                           String attachment, EmailTo toEmail, Date date) throws MailException {
        Assert.isTrue(EmailUtil.checkFileExists(attachment), ExceptionEnum.ATTACHMENT_DOES_NOT_EXIST.getValue());
        Assert.notNull(toEmail, ExceptionEnum.THE_RECEIVER_CANNOT_BE_EMPTY.getValue());
        Assert.notNull(toEmail.getMail(), ExceptionEnum.THE_RECEIVER_CANNOT_BE_EMPTY.getValue());
        return sendFile(emailAccount, subject, message, attachment, Collections.singletonList(toEmail), date);
    }

    /**
     * 发送html
     *
     * @param subject  主题
     * @param html     超文本标记语言
     * @param toEmails 邮件
     * @param date 发送时间
     * @param emailAccount 账号信息
     * @return {@link String} 邮件消息ID
     * @throws MailException 电子邮件异常
     */
    public static String sendHtml(EmailAccount emailAccount, String subject, String html,
                           List<EmailTo> toEmails, Date date) throws MailException {

        return sendHtml(emailAccount, subject, html, toEmails, Collections.emptyList(), date);
    }

    /**
     * 发送html
     *
     * @param subject  主题
     * @param html     超文本标记语言
     * @param toEmail 邮件
     * @param date 发送时间
     * @param emailAccount 账号信息
     * @return {@link String} 邮件消息ID
     * @throws MailException 电子邮件异常
     */
    public static String sendHtml(EmailAccount emailAccount, String subject, String html, EmailTo toEmail, Date date) throws MailException {

        Assert.notBlank(html, ExceptionEnum.THE_MESSAGE_CANNOT_BE_EMPTY.getValue());
        Assert.notNull(toEmail, ExceptionEnum.THE_RECEIVER_CANNOT_BE_EMPTY.getValue());
        Assert.notNull(toEmail.getMail(), ExceptionEnum.THE_RECEIVER_CANNOT_BE_EMPTY.getValue());

        return sendHtml(emailAccount, subject, html, Collections.singletonList(toEmail), Collections.emptyList(), date);
    }

    /**
     * 发送html
     *
     * @param subject     主题
     * @param html        超文本标记语言
     * @param toEmails    邮件
     * @param bccEmails   密送邮箱
     * @param ccEmails    抄送邮箱
     * @param replyEmails 回复邮箱
     * @param date 发送时间
     * @param emailAccount 账号信息
     * @return {@link String} 邮件消息ID
     * @throws MailException 电子邮件异常
     */
    public static String sendHtml(EmailAccount emailAccount, String subject, String html,
                           List<EmailTo> toEmails,
                           List<EmailTo> ccEmails,
                           List<EmailTo> bccEmails,
                           List<EmailTo> replyEmails, Date date) throws MailException {

        Assert.notBlank(html, ExceptionEnum.THE_MESSAGE_CANNOT_BE_EMPTY.getValue());

       return getTemplate(emailAccount).sendHtml(subject, html, toEmails, ccEmails, bccEmails, replyEmails, date);
    }

    /**
     * 发送html
     *
     * @param subject   主题
     * @param html      超文本标记语言
     * @param toEmails  邮件
     * @param bccEmails 密送邮箱
     * @param ccEmails  抄送邮箱
     * @param emailAccount 账号信息
     * @param date 发送时间
     * @return {@link String} 邮件消息ID
     * @throws MailException 电子邮件异常
     */
    public static String sendHtml(EmailAccount emailAccount, String subject, String html,
                           List<EmailTo> toEmails,
                           List<EmailTo> ccEmails,
                           List<EmailTo> bccEmails, Date date) throws MailException {

        return sendHtml(emailAccount, subject, html, toEmails, ccEmails, bccEmails, Collections.emptyList(), date);
    }

    /**
     * 发送html
     *
     * @param subject   主题
     * @param html      超文本标记语言
     * @param toEmails  邮件
     * @param ccEmails 抄送送邮箱
     * @param emailAccount 账号信息
     * @param date 发送时间
     * @return {@link String} 邮件消息ID
     * @throws MailException 电子邮件异常
     */
    public static String sendHtml(EmailAccount emailAccount, String subject, String html,
                           List<EmailTo> toEmails,
                           List<EmailTo> ccEmails, Date date) throws MailException {
        return sendHtml(emailAccount, subject, html, toEmails, ccEmails, Collections.emptyList(), date);
    }

































    /**
     * 获取数据源解析器
     *
     * @return {@link DataSourceResolver[]} 数据源解析器
     * @throws MalformedURLException URL  异常
     */
    protected static DataSourceResolver[] getDataSourceResolvers() throws MalformedURLException {

        return new DataSourceResolver[]{
                new DataSourceFileResolver(),
                new DataSourceUrlResolver(new URL("http://")),
                new DataSourceUrlResolver(new URL("https://"))};
    }

    /**
     * 设置端口
     *
     * @param email     电子邮件对象
     * @param sslEnable 是否开启SSL协议
     * @param port      端口
     */
    protected static void setPort(Email email, Boolean sslEnable, Integer port) {
        if (sslEnable) {
            email.setSslSmtpPort(Convert.toStr(port));
        } else {
            email.setSmtpPort(port);
        }
    }

    /**
     * 设置消息
     *
     * @param email   电子邮件
     * @param message 消息
     * @throws EmailException 添加异常
     */
    protected static void setMsg(Email email, String message) throws EmailException {
        if (!(email instanceof HtmlEmail)) {
            Assert.notBlank(message, ExceptionEnum.THE_MESSAGE_CANNOT_BE_EMPTY.getValue());
            email.setMsg(message);
        }
    }

    /**
     * 添加接收者
     *
     * @param email    电子邮件
     * @param toEmails 接收者
     * @throws EmailException 添加异常
     */
    protected static void addTo(Email email, List<EmailTo> toEmails) throws EmailException {

        Assert.notEmpty(toEmails, ExceptionEnum.THE_RECEIVER_CANNOT_BE_EMPTY.getValue());

        int index = 0;

        for (EmailTo toEmail : toEmails) {
            if (StrUtil.isNotBlank(toEmail.getMail())) {
                email.addTo(toEmail.getMail(), toEmail.getName(), toEmail.getCharset());
                index++;
            }
        }

        Assert.isFalse(index == 0, ExceptionEnum.THE_RECEIVER_CANNOT_BE_EMPTY.getValue());

    }

    /**
     * 添加抄送者
     *
     * @param email    电子邮件
     * @param ccEmails 抄送者
     * @throws EmailException 添加异常
     */
    protected static void addCc(Email email, List<EmailTo> ccEmails) throws EmailException {
        if (CollectionUtil.isNotEmpty(ccEmails)) {
            for (EmailTo ccEmail : ccEmails) {
                email.addCc(ccEmail.getMail(), ccEmail.getName(), ccEmail.getCharset());
            }
        }
    }

    /**
     * 添加密送者
     *
     * @param email     电子邮件
     * @param bccEmails 密送者
     * @throws EmailException 添加异常
     */
    protected static void addBcc(Email email, List<EmailTo> bccEmails) throws EmailException {
        if (CollectionUtil.isNotEmpty(bccEmails)) {
            for (EmailTo bccEmail : bccEmails) {
                email.addBcc(bccEmail.getMail(), bccEmail.getName(), bccEmail.getCharset());
            }
        }
    }

    /**
     * 添加回复者
     *
     * @param email       电子邮件
     * @param replyEmails 回复者
     */
    protected static void addReply(Email email, List<EmailTo> replyEmails) throws EmailException {
        if (CollectionUtil.isNotEmpty(replyEmails)) {
            for (EmailTo replyEmail : replyEmails) {
                email.addReplyTo(replyEmail.getMail(), replyEmail.getName(), replyEmail.getCharset());
            }
        }
    }

    /**
     * 添加pop3
     *
     * @param email 电子邮件
     * @param pop3  pop3
     */
    protected static void addPop3(Email email, EmailProperties.Pop3 pop3) {
        if (pop3.getPopBeforeSmtp()) {
            email.setPopBeforeSmtp(pop3.getPopBeforeSmtp(),
                    pop3.getPopHost(), pop3.getPopUsername(),
                    pop3.getPopPassword());
        }
    }

    /**
     * 设置退回
     *
     * @param email        电子邮件
     * @param bounceEnable 是否开启邮件退回
     * @param from         退回人
     */
    protected static void setBounce(Email email, Boolean bounceEnable, String from) {
        if (bounceEnable) {
            email.setBounceAddress(from);
        }
    }

    /**
     * 检查文件是否存在
     *
     * @param filePath 文件路径
     * @return boolean 是否存在
     */
    protected static boolean checkFileExists(String filePath) {
        try {

            if (ReUtil.isMatch(PROTOCOL_REG, filePath)) {
                HttpURLConnection.setFollowRedirects(false);
                HttpURLConnection con = (HttpURLConnection) new URL(filePath).openConnection();
                con.setRequestMethod("HEAD");

                long totalSize = Long.parseLong(con.getHeaderField("Content-Length"));

                return (con.getResponseCode() == HttpURLConnection.HTTP_OK) && totalSize > 0L;
            } else {
                return FileUtil.exist(filePath);
            }
        } catch (Exception e) {
            logger.error("checkFileExists {}", e.getMessage());
        }

        return false;
    }

    /**
     * 检查在线文件
     *
     * @param filePath 文件路径
     * @return boolean 是否是在线文件
     */
    protected static boolean checkOnlineFile(String filePath) {
        return ReUtil.isMatch(PROTOCOL_REG, filePath);
    }

    /**
     * 获取邮箱操作
     *
     * @param emailAccount 电子邮件帐户信息
     * @return {@link EmailTemplate} 邮箱操作
     */
    private static EmailTemplate getTemplate(EmailAccount emailAccount) {

        Assert.notNull(emailAccount, ExceptionEnum.THE_EMAIL_ACCOUNT_INFORMATION_CANNOT_BE_EMPTY.getValue());

        EmailProperties emailProperties = new EmailProperties();
        BeanUtil.copyProperties(emailAccount, emailProperties);
        EmailProperties.Pop3 pop3 = new EmailProperties.Pop3();
        BeanUtil.copyProperties(emailAccount.getPop3(), pop3);
        emailProperties.setPop3(pop3);

        EmailFactoryBean emailFactoryBean = new EmailFactoryBean(emailProperties);
        emailFactoryBean.afterPropertiesSet();

        return emailFactoryBean.getObject();
    }

    /**
     * 设置日期
     *
     * @param email 电子邮件
     * @param date  日期
     */
    protected static void setDate(Email email, Date date) {

        if (ObjectUtil.isNotNull(date)) {
            if (DateUtil.compare(date, new Date()) < 0) {
                date = new Date();
            }
            email.setSentDate(date);
        }
    }



}
