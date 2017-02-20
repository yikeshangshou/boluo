<%@ page language="java" pageEncoding="UTF-8"%><%@ include file="/WEB-INF/commons/taglibs.jsp"%>
<head>
<meta property="og:type" content="article" />
<meta property="og:url" content="http://www.aqwa.cn/topic/${topic.id}" />
<meta property="og:title" content="${topic.title}" />
<meta property="og:image" content="http://wfenxiang.b0.upaiyun.com/${topic.image}!M" />
<meta property="og:description" content="${topic.description}" />
</head>
<title>专题：${topic.title}｜大菠萝</title>
<header class="container m-header">
  <div class="row">
    <div class="col-md-6 col-md-offset-3 col-sm-10 col-sm-offset-1 logo">
      <a class="m-download" href="#"><img alt="大菠萝-轻松获取热门资讯" src="http://wfenxiang.b0.upaiyun.com/static/m-header.png"></a>
    </div>
    <div class="col-md-6 col-md-offset-3 col-sm-10 col-sm-offset-1 header-tip" style="display: none;">
      <div class="panel m-panel">
        <div class="m-body">
          <button type="button" class="close">
            <span aria-hidden="true">×</span><span class="sr-only">Close</span>
          </button>
          <p>
            请点击右上角<img src="http://wfenxiang.b0.upaiyun.com/static/icon-dot.png">
          </p>
          <p>选择“在Safari中打开”。</p>
        </div>
      </div>
    </div>
  </div>
</header>

<div class="container">
  <div class="row row-space-top-1">
    <div class="col-md-6 col-md-offset-3 col-sm-10 col-sm-offset-1 col-xs-12">
      <div class="page-header">
        <h3>专题：${topic.title}</h3>
        <input id="topicId" type="hidden" value="${topic.id}">
      </div>
      <div class="content">${topic.description}下载大菠萝资讯，实时收到关于${topic.title}的推送。</div>
    </div>
  </div>

  <div class="row row-space-top-2">
    <div class="col-md-6 col-md-offset-3 col-sm-10 col-sm-offset-1 col-xs-12">
      <div class="panel m-list-container items">
        <div class="panel-heading m-heading">
          <span class="m-title">资讯</span>
        </div>
        <c:forEach var="item" items="${items}">
          <a href="/item/${item.id}">
            <div class="panel m-panel" data-id="${item.id}">
              <div class="m-body">
                <jsp:useBean id="createDate" class="java.util.Date" />
                <jsp:setProperty name="createDate" property="time" value="${item.createTime}" />
                <div class="title">
                  <fmt:formatDate value="${createDate}" pattern="[MM-dd]" />
                  ${item.title}
                </div>
                <p>${item.description}</p>
                <div class="bottom row-space-top-1 m-tip m-actions">
                  <span>${item.properties.discussionCount}条讨论</span>
                </div>
              </div>
            </div>
          </a>
        </c:forEach>
        <div class="panel m-panel" id="notification-panel" style="display: none;">
          <div class="m-body"></div>
        </div>
        <div class="panel m-panel m-panel-download">
          <div class="m-body">
            <a class="btn btn-blue m-download" href="#">下载大菠萝查看更多资讯和讨论</a><a class="col-space-2 icon-share social-share" data-target="weibo"><img alt="分享到新浪微博"
              src="http://wfenxiang.b0.upaiyun.com/static/icons/icon-weibo.png"></a>
          </div>
        </div>
      </div>
    </div>
  </div>

  <div class="row row-space-top-2">
    <div class="col-md-6 col-md-offset-3 col-sm-10 col-sm-offset-1 col-xs-12">
      <div class="panel qr-code">
        <img src="http://wfenxiang.b0.upaiyun.com/static/m-qr-code.png">
      </div>
    </div>
  </div>

</div>

<script src="/js/m/global.js?v=20160811"></script>