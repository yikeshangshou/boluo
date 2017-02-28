<%@ page language="java" pageEncoding="UTF-8"%><%@ include file="/WEB-INF/commons/taglibs.jsp"%>
<title>发现｜大菠萝</title>
<div class="row row-space-top-4">
  <div class="col-md-8">
    <div class="feeds" id="feeds">
      <c:forEach var="item" items="${feeds}">
        <div class="panel panel-default row-space-top-2 feed" data-id="${item.id}" data-link="${item.link}">
          <div class="panel-heading">
            <div>
              <a href="/topic/${item.properties.topicId}" target="_blank">${item.properties.topicTitle}</a>
            </div>
            <div>
              <span>${item.title}</span>
              <c:if test="${not empty item.link}">
                <a href="${item.link}" target="_blank"><span class="glyphicon glyphicon-link" aria-hidden="true"></span></a>
              </c:if>
            </div>
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
          <div class="panel-footer entry-footer">
            <p class="avatars">
              <%-- <jsp:useBean id="createDate" class="java.util.Date" />
              <span class="time"><jsp:setProperty name="createDate" property="time" value="${item.createTime}" /> <fmt:formatDate value="${createDate}" pattern="yyyy-MM-dd HH:mm" /></span> --%>
              <c:if test="${item.userId gt 0}">
                <a href="/user/${item.userId}" target="_blank" title="${item.username}"><img src="http://wfenxiang.b0.upaiyun.com/${item.properties.userAvatar}!S"></a>
              </c:if>
            </p>
            <ul class="operations">
              <li class="op-like"><a class="like" data-id="${item.id}" href="javascript:void(0);" title="喜欢"><c:choose>
                    <c:when test="${1 eq item.likeStatus}">
                      <span class="glyphicon glyphicon-heart" aria-hidden="true"></span>
                    </c:when>
                    <c:otherwise>
                      <span class="glyphicon glyphicon-heart-empty" aria-hidden="true"></span>
                    </c:otherwise>
                  </c:choose><span class="likeCount">${item.likeCount}</span></a></li>
              <li class="op-comment"><a class="comment" data-id="${item.id}" href="/feed/${item.id}" title="评论"><span class="glyphicon glyphicon-comment" aria-hidden="true"></span><span>${item.commentCount}</span></a></li>
              <li class="op-share"><a class="share" data-id="${item.id}" href="javascript:void(0);" title="分享"><span class="glyphicon glyphicon-share-alt" aria-hidden="true"></span></a></li>
              <!-- <li class="op-report"><a class="report" data-id="${item.id}" href="javascript:void(0)" title="举报"><span class="glyphicon glyphicon-bell" aria-hidden="true"></span></a></li> -->
            </ul>
          </div>
        </div>
      </c:forEach>
    </div>
  </div>
  <div class="col-md-4">
    <div class="panel panel-default item row-space-top-2" data-id="${item.id}" data-link="${item.link}">
      <div class="panel-heading">
        <span>热门主题</span><a class="pull-right" href="/topic"><span class="glyphicon glyphicon-th" aria-hidden="true"></span><span>所有主题</span></a>
      </div>
    </div>
    <div class="list-group" id="topics"></div>
  </div>
</div>

<script>
  $(function() {
    $.getJSON('/api/v1/topic', {
      size : 5
    }, function(resp) {
      if (resp && resp.e == 0) {
        var topics = resp.r;
        for (var i = 0; i < topics.length; i++) {
          var topic = topics[i];
          var html = '<a href="/topic/' + topic.id + '" class="list-group-item"><span class="badge">' + topic.feedCount
              + '</span>' + topic.title + '</a>';

          $(html).appendTo('#topics');
        }
      }
    });

  });
</script>
<script src="/js/feed_operations.js?v=20160629"></script>