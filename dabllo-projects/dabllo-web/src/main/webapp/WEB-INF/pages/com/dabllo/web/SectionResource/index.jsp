<%@ page language="java" pageEncoding="UTF-8"%><%@ include file="/WEB-INF/commons/taglibs.jsp"%>
<title>资讯｜大菠萝</title>
<c:if test="${not empty  topicSection}">
  <div class="row row-space-top-4">
    <div class="col-md-12">
      <div class="page-header">${topicSection.title}</div>
    </div>
  </div>
  <div class="row">
    <c:forEach var="item" items="${topicSection.topics}">
      <div class="col-md-2 col-sm-4 col-xs-6" data-id="${item.id}">
        <c:choose>
          <c:when test="${not empty item.image}">
            <figure class="panel panel-default item">
              <div class="cover">
                <a href="/topic/${item.id}" target="_blank"><img alt="${item.title}" src="http://wfenxiang.b0.upaiyun.com/${item.image}!M"></a>
              </div>
              <figcaption class="title">
                <a href="/topic/${item.id}" target="_blank">${item.title}</a>
              </figcaption>
            </figure>
          </c:when>
          <c:otherwise>
            <figure class="panel panel-default item">
              <div class="cover">
                <div class="alt">
                  <a href="/topic/${item.id}" target="_blank">${item.title}</a>
                </div>
              </div>
              <figcaption class="content">
                <a href="/topic/${item.id}" target="_blank">${item.description}</a>
              </figcaption>
            </figure>
          </c:otherwise>
        </c:choose>
      </div>
    </c:forEach>
  </div>
</c:if>

<c:forEach var="section" items="${sections}">
  <c:if test="${not empty section.items}">
    <div class="row row-space-top-4">
      <div class="col-md-12">
        <div class="page-header">${section.title}</div>
      </div>
    </div>
    <div class="row">
      <c:forEach var="item" items="${section.items}">
        <div class="col-md-2 col-sm-4 col-xs-6" data-id="${item.id}">
          <c:choose>
            <c:when test="${not empty item.image}">
              <figure class="panel panel-default item">
                <div class="cover">
                  <a href="/item/${item.id}" target="_blank"><img alt="${item.title}" src="http://wfenxiang.b0.upaiyun.com/${item.image}!M"></a>
                </div>
                <figcaption class="title">
                  <a href="/item/${item.id}" target="_blank">${item.title}</a>
                </figcaption>
              </figure>
            </c:when>
            <c:otherwise>
              <figure class="panel panel-default item">
                <div class="cover">
                  <div class="alt">
                    <a href="/item/${item.id}" target="_blank">${item.title}</a>
                  </div>
                </div>
                <figcaption class="content">
                  <a href="/item/${item.id}" target="_blank">${item.description}</a>
                </figcaption>
              </figure>
            </c:otherwise>
          </c:choose>
        </div>
      </c:forEach>
    </div>
  </c:if>
</c:forEach>

<c:if test="${not empty  discussionSection}">
  <div class="row row-space-top-4">
    <div class="col-md-12">
      <div class="page-header">${discussionSection.title}</div>
    </div>
  </div>
  <div class="row">
    <c:forEach var="item" items="${discussionSection.discussions}">
      <div class="col-md-2 col-sm-4 col-xs-6" data-id="${item.id}">
        <figure class="panel panel-default item">
          <div class="cover">
            <div class="alt">
              <a href="/discussion/${item.id}" target="_blank">${item.title}</a>
            </div>
          </div>
          <figcaption class="content">
            <a href="/item/${item.properties.itemId}" target="_blank">${item.properties.itemTitle}</a>
          </figcaption>
        </figure>
      </div>
    </c:forEach>
  </div>
</c:if>