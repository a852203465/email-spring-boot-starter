package cn.darkjrong.email.exception;

import lombok.Getter;

import javax.swing.text.html.HTML;

/**
 * 异常枚举
 *
 * @author Rong.Jia
 * @date 2021/07/27 08:50:51
 */
@Getter
public enum ExceptionEnum {


    // 附件不存在
    ATTACHMENT_DOES_NOT_EXIST("attachment does not exist"),

    // 接收者不能为空
    THE_RECEIVER_CANNOT_BE_EMPTY("The receiver cannot be empty"),

    // 消息不能为空
    THE_MESSAGE_CANNOT_BE_EMPTY("The message cannot be empty"),

    // 你的邮件客户端不支持html邮件
    YOUR_EMAIL_CLIENT_DOES_NOT_SUPPORT_HTML_MESSAGES("你的邮件客户端不支持html邮件"),

    // 主题不能为空
    THE_TOPIC_CANNOT_BE_EMPTY("The topic cannot be empty"),

    // 属性不能为空
    THE_PROPERTY_CANNOT_BE_EMPTY("'%s' cannot be empty"),

    // 邮箱账号信息不能为空
    THE_EMAIL_ACCOUNT_INFORMATION_CANNOT_BE_EMPTY("The email account information cannot be empty"),






















    ;


    ExceptionEnum(String value) {
        this.value = value;
    }

    private String value;









}
