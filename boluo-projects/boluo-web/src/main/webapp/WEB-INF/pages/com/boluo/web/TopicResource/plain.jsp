<%@ page language="java" pageEncoding="UTF-8"%><%@ include file="/WEB-INF/commons/taglibs.jsp"%>
<head>
<meta property="og:type" content="article" />
<meta property="og:url" content="http://www.aqwa.cn/topic/${topic.id}" />
<meta property="og:title" content="${topic.title}" />
<meta property="og:image" content="http://wfenxiang.b0.upaiyun.com/${topic.image}!M" />
<meta property="og:description" content="${topic.description}" />
</head>
<title>专题：${topic.title}｜大菠萝</title>

<div class="container">
  <div class="row">
    <div class="panel panel-default">
      <div class="panel-body">
        <div class="row-space-2">
          <h3>${topic.title}</h3>
          <h5>在大菠萝资讯，本月一共收录关于${topic.title}的${itemCount}条重要资讯，${articleCount}篇分析文章。实时收到关于${topic.title}的推送，请下载大菠萝资讯。</h5>
        </div>
        <div class="row-space-5">
          <c:forEach var="item" items="${items}" begin="0" end="7" step="1">
            <jsp:useBean id="createDate" class="java.util.Date" />
            <jsp:setProperty name="createDate" property="time" value="${item.createTime}" />
            <h5 class="row-space-top-2">
              <fmt:formatDate value="${createDate}" pattern="[MM-dd]" />
              ${item.title}
            </h5>
            <p>${item.description}</p>
          </c:forEach>
        </div>

        <div class="row-space-5">
          <h5>${topic.title}的其他资讯</h5>
          <c:forEach var="item" items="${items}" begin="8" end="15" step="1">
            <ul>
              <li>${item.title}</li>
            </ul>
          </c:forEach>
        </div>

        <div class="row-space-5">
          <h5>这一月关于${topic.title}的分析文章</h5>
          <c:forEach var="article" items="${articles}">
            <ul>
              <li>${article.title}</li>
            </ul>
          </c:forEach>
        </div>

        <c:if test="${not empty discussions}">
          <div class="row-space-5">
            <h5>有${fn:length(discussions)}个关于${topic.title}的讨论正在大菠萝资讯热议</h5>
            <c:forEach var="discussion" items="${discussions}">
              <ul>
                <li>${discussion.title}</li>
              </ul>
            </c:forEach>
          </div>
        </c:if>

        <div class="row-space-5">
          <p>更好的阅读体验，查看所有资讯详情，AppStore搜索大菠萝资讯。</p>
        </div>

        <div class="row">
          <div class="col-md-8 col-md-offset-2 col-sm-10 col-sm-offset-1 col-xs-12">
            <img alt="大菠萝资讯" src="http://wfenxiang.b0.upaiyun.com/static/m-qr-code.png">
          </div>
        </div>
      </div>

    </div>
  </div>
</div>