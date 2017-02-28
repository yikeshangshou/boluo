<%@ page language="java" pageEncoding="UTF-8"%><%@ include file="/WEB-INF/commons/taglibs.jsp"%>
<div class="container row-space-top-2">
  <ol class="breadcrumb">
    <li class="active">专题管理</li>
  </ol>

  <div class="row row-space-top-4">
    <c:forEach var="item" items="${topics}">
      <div class="col-md-3 col-sm-4 col-xs-6" id="topic_${item.id}">
        <div class="panel panel-default" data-description="${item.description}" data-id="${item.id}" data-image="${item.image}"
          <c:if test="${not empty item.image}">data-imageUrl="http://wfenxiang.b0.upaiyun.com/${item.image}!M"</c:if> data-link="${item.link}" data-name="${item.name}" data-title="${item.title}">
          <div class="panel-heading">
            <strong>${item.name}</strong>
            <jsp:useBean id="createDate" class="java.util.Date" />
            <jsp:setProperty name="createDate" property="time" value="${item.createTime}" />
            <fmt:formatDate value="${createDate}" pattern="MM-dd HH:mm" />
            <span class="pull-right">${item.properties.timeBefore}</span>
          </div>
          <div class="panel-body">
            <div class="feed-box">
              <div class="cover">
                <c:choose>
                  <c:when test="${not empty item.image}">
                    <a href="/topic/${item.id}" target="_blank"><img src="http://wfenxiang.b0.upaiyun.com/${item.image}!M"></a>
                  </c:when>
                  <c:otherwise>
                    <a href="/topic/${item.id}" target="_blank"><img data-src="holder.js/160x80"></a>
                  </c:otherwise>
                </c:choose>
              </div>
              <div class="title">
                <a href="/topic/${item.id}" target="_blank" title="查看专题">${item.title}</a>
                <c:if test="${not empty item.link}">
                  <a href="${item.link}" target="_blank" title="查看原文"><span class="glyphicon glyphicon-link" aria-hidden="true"></span></a>
                </c:if>
              </div>
            </div>
          </div>
          <div class="panel-footer">
            <a class="col-space-1 op-delete" data-id="${item.id}" href="#" title="删除"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></a>
            <div class="pull-right">
              <a data-toggle="modal" data-target="#EditModal" href="#" title="编辑专题"><span class="glyphicon glyphicon-pencil" aria-hidden="true"></span></a>
              <!--  -->
              <a class="switch-latest" data-id="${item.id}" href="#" title="标记最新"><c:choose>
                  <c:when test="${1 eq item.properties.latest}">
                    <span class="glyphicon glyphicon-ice-lolly" aria-hidden="true"></span>
                  </c:when>
                  <c:otherwise>
                    <span class="glyphicon glyphicon-ice-lolly-tasted" aria-hidden="true"></span>
                  </c:otherwise>
                </c:choose></a>
              <!--  -->
              <a class="switch-hot" data-id="${item.id}" href="#" title="标记热议"><c:choose>
                  <c:when test="${1 eq item.properties.hot}">
                    <span class="glyphicon glyphicon-volume-up" aria-hidden="true"></span>
                  </c:when>
                  <c:otherwise>
                    <span class="glyphicon glyphicon-volume-off" aria-hidden="true"></span>
                  </c:otherwise>
                </c:choose></a>
              <!--  -->
              <%-- <a href="/admin/topic/${item.id}" target="_blank" title="查看新闻统计"><span class="glyphicon glyphicon-stats" aria-hidden="true"></span></a> --%>
            </div>
          </div>
        </div>
      </div>
    </c:forEach>
  </div>

  <div class="row">
    <div class="col-md-12">
      <ul class="pagination">
        <li><a href="/admin/topic?status=${status}&selected=${selected}&page=1">首页</a></li>
        <c:forEach var="page" items="${pages}">
          <c:choose>
            <c:when test="${page eq currentPage}">
              <li class="active"><a href="#">${page}</a></li>
            </c:when>
            <c:otherwise>
              <li><a href="/admin/topic?status=${status}&selected=${selected}&page=${page}">${page}</a></li>
            </c:otherwise>
          </c:choose>
        </c:forEach>
        <li><a href="/admin/topic?status=${status}&selected=${selected}&page=${lastPage}">尾页</a></li>
      </ul>
    </div>
  </div>
</div>

<div class="modal" id="EditModal" tabindex="-1" role="dialog" aria-labelledby="EditModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">
          <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
        </button>
        <h4 class="modal-title" id="EditModalLabel">编辑主题</h4>
      </div>
      <form action="/admin/topic/update" class="form-horizontal" id="edit-form" method="post" name="edit-form" role="form">
        <div class="modal-body">
          <div class="form-group">
            <div class="col-md-6 image-container">
              <img class="thumbnail" data-src="holder.js/270x135">
            </div>
          </div>
          <div class="form-group">
            <div class="col-md-12">
              <input class="form-control" id="edit-title" name="title" placeholder="标题" type="text">
            </div>
          </div>
          <div class="form-group">
            <div class="col-md-12">
              <textarea class="form-control" id="edit-description" name="description" placeholder="描述" rows="3"></textarea>
            </div>
          </div>

          <div class="form-group">
            <div class="col-md-10">
              <input class="form-control" id="edit-link" name="link" placeholder="链接">
            </div>
            <div class="col-md-2">
              <a class="btn btn-default op-view-page" href="#">查看</a>
            </div>
          </div>
          <div class="form-group">
            <div class="col-md-10">
              <input class="form-control" id="edit-imageUrl" name="imageUrl" placeholder="图片链接">
            </div>
            <div class="col-md-2">
              <a class="btn btn-default op-get-image" href="#">获取</a>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <input id="edit-topicId" name="topicId" type="hidden"><input id="edit-image" name="image" type="hidden">
          <button class="btn btn-success" type="submit">提交</button>
          <button class="btn btn-default" data-dismiss="modal" type="button">关闭</button>
        </div>
      </form>
    </div>
  </div>
</div>

<script src="/libs/holder.min.js"></script>
<script src="/js/admin/topic.js?v=20160818"></script>