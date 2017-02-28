package com.boluo.service;

import java.util.List;

import com.boluo.model.Article;
import com.boluo.util.Pair;

/**
 * @author mixueqiang
 * @since Aug 19, 2016
 */
public interface ArticleService {

  Pair<Long, List<Article>> getArticlesByTopic(long topicId, long offset, int size);

}
