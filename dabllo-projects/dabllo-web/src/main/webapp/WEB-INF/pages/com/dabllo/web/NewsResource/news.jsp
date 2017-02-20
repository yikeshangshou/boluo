<%@ page language="java" pageEncoding="UTF-8"%><%@ include file="/WEB-INF/commons/taglibs.jsp"%>
<title>资讯－${date}｜大菠萝</title>
<div class="row row-space-top-4">
  <div class="col-md-6 col-md-offset-3 col-sm-8 col-sm-offset-2 col-xs-12">
    <div class="news" id="news">
      <c:forEach var="item" items="${news}">
        <div class="panel panel-default row-space-top-2 item" data-id="${item.id}" data-link="${item.link}" data-title="${item.title}">
          <div class="panel-heading">
            <span>${item.title}</span>
            <c:if test="${not empty item.link}">
              <a href="${item.link}" target="_blank"><span class="glyphicon glyphicon-link" aria-hidden="true"></span></a>
            </c:if>
          </div>
          <div class="panel-body entry">
            <c:choose>
              <c:when test="${not empty item.images}">
                <c:forEach var="image" items="${item.images}">
                  <div class="cover">
                    <img src="http://wfenxiang.b0.upaiyun.com/${image}!M">
                  </div>
                </c:forEach>
              </c:when>
              <c:when test="${not empty item.image}">
                <div class="cover">
                  <img src="http://wfenxiang.b0.upaiyun.com/${item.image}!M">
                </div>
              </c:when>
            </c:choose>
          </div>
        </div>
      </c:forEach>
    </div>
  </div>
</div>

<script>
  $(function() {
  });
</script>