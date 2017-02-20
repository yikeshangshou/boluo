<%@ page language="java" pageEncoding="UTF-8"%><%@ include file="/WEB-INF/commons/taglibs.jsp"%>
<div class="container margin-top-large">
  <div class="row row-space-top-4">
    <c:forEach var="item" items="${items}">
      <div class="col-md-4 col-sm-6 col-xs-6 item" id="item_${item.id}">
        <div class="panel panel-default" data-id="${item.id}" data-title="${item.title}" data-description="${item.description}">
          <div class="panel-heading">
            <strong>${item.source}</strong>
            <jsp:useBean id="createDate" class="java.util.Date" />
            <jsp:setProperty name="createDate" property="time" value="${item.createTime}" />
            <fmt:formatDate value="${createDate}" pattern="MM-dd HH:mm" />
            <span class="pull-right"><a href="${item.link}" target="_blank" title="查看原文"><span class="glyphicon glyphicon-link" aria-hidden="true">原文</span></a></span>
          </div>
          <div class="panel-body">
            <div class="feed-box">
              <div class="cover">
                <c:choose>
                  <c:when test="${not empty item.image}">
                    <img src="http://wfenxiang.b0.upaiyun.com/${item.image}!M">
                  </c:when>
                  <c:otherwise>
                    <img data-src="holder.js/160x80">
                  </c:otherwise>
                </c:choose>
              </div>
              <div class="title">${item.title}</div>
            </div>
          </div>
          <div class="panel-footer"></div>
        </div>
      </div>
    </c:forEach>
  </div>
</div>

<script src="/libs/holder.min.js"></script>