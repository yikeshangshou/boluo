<%@ page language="java" pageEncoding="UTF-8"%><%@ include file="/WEB-INF/commons/taglibs.jsp"%>
<title>主题｜大菠萝</title>
<div class="row row-space-top-4">
  <div class="col-md-12">
    <div class="bs-glyphicons topics" id="topics">
      <ul class="bs-glyphicons-list">
        <!-- <li><a href="/topic/create"><span class="glyphicon-class">新建主题</span></a></li> -->
        <c:forEach var="item" items="${topics}">
          <li><a href="/topic/${item.id}"><span class="glyphicon-class">${item.title}</span></a></li>
        </c:forEach>
        <li></li>
      </ul>
    </div>
  </div>
</div>