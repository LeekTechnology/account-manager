package com.wx.account.Message.messagepackage;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 图文消息
 * Created by supermrl on 2019/1/19.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class NewsMessage extends BaseMessage{

    //图文消息个数,限制为10条以内
    private int ArticleCount;

    //多条图文消息信息，默认第一个item为大图
    private List<Article> Articles;
}
