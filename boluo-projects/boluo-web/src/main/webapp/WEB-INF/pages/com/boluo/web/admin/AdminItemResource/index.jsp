<%@ page language="java" pageEncoding="UTF-8"%><%@ include file="/WEB-INF/commons/taglibs.jsp"%>
<div class="container row-space-top-2">
  <ol class="breadcrumb">
    <li class="active">资讯管理</li>
  </ol>
  <div class="row">
    <div class="col-md-12 col-sm-12 col-xs-12">
      <a class="btn btn-primary op-refresh-cache" data-time="latest" href="#"><span class="glyphicon glyphicon-refresh" aria-hidden="true"></span>刷新最新缓存</a>
      <!--  -->
      <a class="btn btn-primary col-space-2 op-refresh-cache" data-time="hot" href="#"><span class="glyphicon glyphicon-refresh" aria-hidden="true"></span>刷新热度上升最快缓存</a>
      <!--  -->
      <a class="btn btn-primary col-space-2 op-refresh-cache" data-time="24h" href="#"><span class="glyphicon glyphicon-refresh" aria-hidden="true"></span>刷新24小时最热缓存</a>
      <!--  -->
      <a class="btn btn-primary col-space-2 op-refresh-cache" data-time="3d" href="#"><span class="glyphicon glyphicon-refresh" aria-hidden="true"></span>刷新3d缓存</a>
      <!--  -->
      <a class="btn btn-primary col-space-2 op-refresh-cache" data-time="7d" href="#"><span class="glyphicon glyphicon-refresh" aria-hidden="true"></span>刷新7d缓存</a>
    </div>
  </div>

  <div class="row row-space-top-4">
    <c:forEach var="item" items="${items}">
      <div class="col-md-3 col-sm-4 col-xs-6" id="item_${item.id}">
        <div class="panel panel-default" data-description="${item.description}" data-id="${item.id}" data-image="${item.image}"
          <c:if test="${not empty item.image}">data-imageUrl="http://wfenxiang.b0.upaiyun.com/${item.image}!M"</c:if> data-link="${item.link}" data-title="${item.title}">
          <div class="panel-heading">
            <strong>${item.source}</strong>
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
                    <a href="/item/${item.id}" target="_blank"><img src="http://wfenxiang.b0.upaiyun.com/${item.image}!M"></a>
                  </c:when>
                  <c:otherwise>
                    <a href="/item/${item.id}" target="_blank"><img data-src="holder.js/160x80"></a>
                  </c:otherwise>
                </c:choose>
              </div>
              <div class="title">
                <a href="/item/${item.id}" target="_blank" title="查看资讯">${item.title}</a><a href="${item.link}" target="_blank" title="查看原文"><span class="glyphicon glyphicon-link"
                  aria-hidden="true"></span></a>
              </div>
            </div>
          </div>
          <div class="panel-footer">
            <a class="col-space-1 op-delete" data-id="${item.id}" href="#" title="删除"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></a>
            <div class="pull-right">
              <a data-toggle="modal" data-target="#EditModal" href="#" title="编辑资讯"><span class="glyphicon glyphicon-pencil" aria-hidden="true"></span></a>
              <!--  -->
              <a data-toggle="modal" data-target="#TopicsModal" href="#" title="管理专题"><span class="glyphicon glyphicon-tags" aria-hidden="true"></span></a>
              <!--  -->
              <a class="switch-hot" data-id="${item.id}" href="#" title="热议"><c:choose>
                  <c:when test="${1 eq item.hot}">
                    <span class="glyphicon glyphicon-volume-up" aria-hidden="true"></span>
                  </c:when>
                  <c:otherwise>
                    <span class="glyphicon glyphicon-volume-off" aria-hidden="true"></span>
                  </c:otherwise>
                </c:choose></a>
              <!--  -->
              <%-- <a href="/admin/item/${item.id}" target="_blank" title="查看新闻统计"><span class="glyphicon glyphicon-stats" aria-hidden="true"></span></a> --%>
            </div>
          </div>
        </div>
      </div>
    </c:forEach>
  </div>

  <div class="row">
    <div class="col-md-12">
      <ul class="pagination">
        <li><a href="/admin/item?page=1">首页</a></li>
        <c:forEach var="page" items="${requestScope.pages}">
          <c:choose>
            <c:when test="${page eq currentPage}">
              <li class="active"><a href="#">${page}</a></li>
            </c:when>
            <c:otherwise>
              <li><a href="/admin/item?page=${page}">${page}</a></li>
            </c:otherwise>
          </c:choose>
        </c:forEach>
        <li><a href="/admin/item?page=${lastPage}">尾页</a></li>
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
        <h4 class="modal-title" id="EditModalLabel">编辑资讯</h4>
      </div>
      <form action="/admin/item/update" class="form-horizontal" id="edit-form" method="post" name="edit-form" role="form">
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
          <input id="edit-itemId" name="itemId" type="hidden"><input id="edit-image" name="image" type="hidden">
          <button type="submit" class="btn btn-success">提交</button>
          <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
        </div>
      </form>
    </div>
  </div>
</div>

<div class="modal" id="TopicsModal" tabindex="-1" role="dialog" aria-labelledby="TopicsModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">
          <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
        </button>
        <h4 class="modal-title" id="TopicsModalLabel">管理专题</h4>
      </div>
      <div class="modal-body">
        <div class="form-group">
          <label for="topic-list">所属专题列表</label>
          <div class="topic-list" id="topic-list"></div>
        </div>
        <form action="/admin/item/add_topic" id="topic-form" method="post" name="topic-form" role="form">
          <div class="form-group">
            <label for="topic-title">加入专题</label><input class="form-control" id="topic-title" name="title">
          </div>
          <div class="form-group">
            <input id="topic-itemId" name="itemId" type="hidden">
            <button type="submit" class="btn btn-success">提交</button>
          </div>
        </form>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
      </div>
    </div>
  </div>
</div>

<script src="/libs/holder.min.js"></script>
<script src="/js/admin/item.js?v=20160816"></script>