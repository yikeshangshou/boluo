<%@ page language="java" pageEncoding="UTF-8"%><%@ include file="/WEB-INF/commons/taglibs.jsp"%>
<div class="container row-space-top-2">
  <ol class="breadcrumb"></ol>
  <div class="row row-space-top-4">
    <c:forEach var="item" items="${feeds}">
      <div class="col-md-3 col-sm-4 col-xs-6 feed" id="feed_${item.id}">
        <div class="panel panel-default" data-description="${item.description}" data-id="${item.id}" data-link="${item.link}" data-title="${item.title}">
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
                    <a href="${item.link}" target="_blank" title="查看原新闻"><img src="http://wfenxiang.b0.upaiyun.com/${item.image}!M"></a>
                  </c:when>
                  <c:otherwise>
                    <a href="${item.link}" target="_blank" title="查看原新闻"><img data-src="holder.js/160x80"></a>
                  </c:otherwise>
                </c:choose>
              </div>
              <div class="title">
                <a href="${item.link}" target="_blank" title="查看原新闻">${item.title}</a>
              </div>
            </div>
          </div>
          <div class="panel-footer">
            <a data-toggle="modal" data-target="#RemoveModal" href="#" title="审核未通过"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></a>
            <div class="pull-right">
              <a class="col-space-1" data-toggle="modal" data-target="#PublishModal" href="#" title="审核通过"><span class="glyphicon glyphicon-check" aria-hidden="true"></span></a>
            </div>
          </div>
        </div>
      </div>
    </c:forEach>
  </div>
  <div class="row">
    <div class="col-md-12">
      <ul class="pagination">
        <li><a href="/admin/feed/user?page=1">首页</a></li>
        <c:forEach var="page" items="${requestScope.pages}">
          <c:choose>
            <c:when test="${page eq currentPage}">
              <li class="active"><a href="#">${page}</a></li>
            </c:when>
            <c:otherwise>
              <li><a href="/admin/feed/user?page=${page}">${page}</a></li>
            </c:otherwise>
          </c:choose>
        </c:forEach>
        <li><a href="/admin/feed/user?page=${lastPage}">尾页</a></li>
      </ul>
    </div>
  </div>
</div>

<div class="modal" id="RemoveModal" tabindex="-1" role="dialog" aria-labelledby="RemoveModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">
          <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
        </button>
        <h4 class="modal-title" id="RemoveModalLabel">审核资讯：未通过</h4>
      </div>
      <form action="/admin/feed/remove" class="form-horizontal" id="remove-form" method="post" name="remove-form" role="form">
        <div class="modal-body">
          <div class="form-group">
            <label class="col-md-2 control-label" for="title">标题</label>
            <div class="col-md-8">
              <textarea class="form-control" disabled="disabled" id="remove-title" name="title" rows="2"></textarea>
            </div>
          </div>
          <div class="form-group">
            <label class="col-md-2 control-label" for="remove-reason">理由</label>
            <div class="col-md-8">
              <select class="form-control" id="remove-reason" name="reason">
                <option value="1">已有类似的资讯。</option>
                <option value="3">已过时的资讯。</option>
                <option value="2">不是资讯，分析、心得等文章暂不在大菠萝的投稿范围。</option>
                <option value="4">不是资讯，请投稿有价值的资讯内容。</option>
              </select>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <input id="remove-feedId" name="feedId" type="hidden">
          <button type="submit" class="btn btn-success">提交</button>
          <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
        </div>
      </form>
    </div>
  </div>
</div>

<div class="modal" id="PublishModal" tabindex="-1" role="dialog" aria-labelledby="PublishModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">
          <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
        </button>
        <h4 class="modal-title" id="PublishModalLabel">审核资讯：通过并投递</h4>
      </div>
      <form action="/admin/feed/publish" class="form-horizontal" id="publish-form" method="post" name="publish-form" role="form">
        <div class="modal-body">
          <div class="form-group">
            <div class="col-md-6">
              <select class="form-control" id="publish-type" name="type">
                <option value="0">资讯</option>
                <option value="1">分析文章</option>
              </select>
            </div>
            <div class="col-md-6 image-container">
              <img class="thumbnail" data-src="holder.js/270x135">
            </div>
          </div>
          <div class="form-group">
            <div class="col-md-12">
              <input class="form-control" id="publish-title" name="title" placeholder="标题">
            </div>
          </div>
          <div class="form-group">
            <div class="col-md-12">
              <textarea class="form-control" id="publish-description" name="description" placeholder="描述" rows="6"></textarea>
            </div>
          </div>
          <div class="form-group">
            <div class="col-md-10">
              <input class="form-control" id="publish-link" name="link" placeholder="链接">
            </div>
            <div class="col-md-2">
              <a class="btn btn-default op-view-page" href="#">查看</a>
            </div>
          </div>
          <div class="form-group">
            <div class="col-md-10">
              <input class="form-control" id="publish-imageUrl" name="imageUrl" placeholder="图片链接">
            </div>
            <div class="col-md-2">
              <a class="btn btn-default op-get-image" href="#">获取</a>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <input id="publish-feedId" name="feedId" type="hidden"><input id="publish-image" name="image" type="hidden">
          <button type="submit" class="btn btn-success">提交</button>
          <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
        </div>
      </form>
    </div>
  </div>
</div>

<script src="/libs/holder.min.js"></script>
<script src="/js/admin/feed.js?v=20160812"></script>