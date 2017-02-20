package com.dabllo.service;

import java.util.List;

import com.dabllo.model.Article;
import com.dabllo.util.Pair;

/**
 * @author mixueqiang
 * @since Aug 19, 2016
 */
public interface ArticleService {

  Pair<Long, List<Article>> getArticlesByTopic(long topicId, long offset, int size);

}
