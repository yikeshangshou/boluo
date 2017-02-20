<%@ page language="java" pageEncoding="UTF-8"%><%@ include file="/WEB-INF/commons/taglibs.jsp"%>
<title>关注的主题｜大菠萝</title>
<div class="row row-space-top-4">
  <div class="col-md-7">
    <div class="topics">
      <c:forEach var="topic" items="${topics}">
        <div class="panel panel-default topic row-space-top-1" data-id="${topic.id}">
          <div class="panel-heading">
            <a class="title" href="/topic/${topic.id}" target="_blank">${topic.title}</a>
          </div>
          <div class="panel-body entry">
            <c:if test="${not empty topic.image}">
              <div class="cover">
                <img src="http://wfenxiang.b0.upaiyun.com/${topic.image}!M">
              </div>
            </c:if>
          </div>
          <div class="panel-footer entry-footer">
            <p class="tagline">
              <c:if test="${topic.userId gt 0}">
                <a href="/user/${topic.username}" target="_blank">${topic.username}</a>
              </c:if>
              <span class="time"><jsp:useBean id="createDate" class="java.util.Date" /> <jsp:setProperty name="createDate" property="time" value="${topic.createTime}" /> <fmt:formatDate
                  value="${createDate}" pattern="yyyy-MM-dd HH:mm" /></span>
              <c:if test="${topic.userCount gt 0}">
                <span>${topic.userCount}人参与</span>
              </c:if>
              <c:if test="${topic.feedCount gt 0}">
                <span>${topic.feedCount}条动态</span>
              </c:if>
            </p>
            <ul class="operations hide">
              <li class="op-comment"><a href="javascript:void(0);" class="comments">暂无评论</a></li>
              <li class="op-share"><a href="javascript:void(0);">分享</a></li>
              <li class="op-save"><a href="javascript:void(0);">收藏</a></li>
              <li class="op-report"><a href="javascript:void(0)" class="report">举报</a></li>
            </ul>
          </div>
        </div>
      </c:forEach>
      <div class="pagination">
        <a class="btn btn-primary" href="/following">首页</a>
        <c:if test="${fn:length(topics) eq 5 }">
          <a class="btn btn-primary col-space-3" href="/following?offset=${offset}">下一页</a>
        </c:if>
      </div>
    </div>
  </div>
</div>