package com.boluo.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.boluo.Constants;
import com.boluo.dao.mapper.ArticleRowMapper;
import com.boluo.model.Article;
import com.boluo.model.Entity;
import com.boluo.service.ArticleService;
import com.boluo.util.Pair;

/**
 * @author mixueqiang
 * @since Aug 19, 2016
 */
@Service
public class ArticleServiceImpl extends BaseService implements ArticleService {

  @Override
  public Pair<Long, List<Article>> getArticlesByTopic(long topicId, long offset, int size) {
    // 已经没有下一页数据。
    if (offset < 0) {
      return new Pair<Long, List<Article>>(-1L, Collections.<Article> emptyList());
    }

    offset = offset > 0 ? offset : Long.MAX_VALUE;
    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("topicId", topicId);
    condition.put("status", Constants.STATUS_ENABLED);
    List<Entity> entities = entityDao.findByOffset("topic_article", condition, "createTime", offset, size);

    if (CollectionUtils.isEmpty(entities)) {
      // 没有查询到数据，直接返回。
      return new Pair<Long, List<Article>>(-1L, Collections.<Article> emptyList());
    }

    // 查询数据。
    List<Article> articles = new ArrayList<Article>();
    for (Entity entity : entities) {
      Article article = entityDao.get("article", entity.getLong("articleId"), ArticleRowMapper.getInstance());
      if (article != null && article.isEnabled()) {
        articles.add(article);
      }

      // 更新offset：取最小的一个 id 为下一次查询的 offset。
      long createTime = entity.getLong("createTime");
      offset = offset > createTime ? createTime : offset;
    }

    return new Pair<Long, List<Article>>(offset, articles);
  }

}
