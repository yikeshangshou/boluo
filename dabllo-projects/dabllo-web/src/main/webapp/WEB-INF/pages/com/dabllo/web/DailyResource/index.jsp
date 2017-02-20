<%@ page language="java" pageEncoding="UTF-8"%><%@ include file="/WEB-INF/commons/taglibs.jsp"%>

<title>资讯日报｜大菠萝</title>
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
  <div class="row row-space-4 row-space-top-2">
    <div class="col-md-6 col-md-offset-3 col-sm-10 col-sm-offset-1 col-xs-12">
      <div class="panel m-list-container items">
        <div class="panel-heading m-heading">
          <span class="m-title">资讯日报&nbsp;${date}</span>
        </div>
        <c:forEach var="item" items="${rankedItems}">
          <a href="/item/${item.id}" target="_blank">
            <div class="panel m-panel" data-id="${item.id}">
              <div class="m-body">
                <div class="main">${item.title}</div>
                <div class="bottom"></div>
              </div>
            </div>
          </a>
        </c:forEach>
      </div>
    </div>
  </div>

  <c:if test="${not empty hotItems}">
    <div class="row row-space-4 row-space-top-2">
      <div class="col-md-6 col-md-offset-3 col-sm-10 col-sm-offset-1 col-xs-12">
        <div class="panel m-list-container items">
          <div class="panel-heading m-heading">
            <span class="m-title">热议资讯</span>
          </div>
          <c:forEach var="item" items="${hotItems}">
            <a href="/item/${item.id}" target="_blank">
              <div class="panel m-panel" data-id="${item.id}">
                <div class="m-body">
                  <div class="main">${item.title}</div>
                  <div class="bottom"></div>
                </div>
              </div>
            </a>
          </c:forEach>
        </div>
      </div>
    </div>
  </c:if>

  <div class="row row-space-top-2">
    <div class="col-md-6 col-md-offset-3 col-sm-10 col-sm-offset-1 col-xs-12">
      <div class="panel qr-code">
        <img src="http://wfenxiang.b0.upaiyun.com/static/m-qr-code.png">
      </div>
    </div>
  </div>

</div>

<script src="/js/m/global.js?v=20160811"></script>