<%@ page language="java" pageEncoding="UTF-8"%><%@ include file="/WEB-INF/commons/taglibs.jsp"%>
<div class="container row-space-top-2">
  <ol class="breadcrumb">
    <li class="active">讨论管理</li>
  </ol>
  <div class="row">
    <div class="col-md-12">
      <table class="table table-bordered table-condensed table-hover" id="discussion-list">
        <thead>
          <tr>
            <th>ID）时间</th>
            <th width="60%">讨论标题</th>
            <th>链接</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="item" items="${discussions}">
            <tr data-id="${item.id}">
              <td>${item.id}）<jsp:useBean id="createDate" class="java.util.Date" /> <jsp:setProperty name="createDate" property="time" value="${item.createTime}" /> <fmt:formatDate
                  value="${createDate}" pattern="MM-dd HH:mm" /></td>
              <td>${item.title}<c:if test="${item.userId gt 0}">（${item.userId}）</c:if></td>
              <td><a href="/item/${item.itemId}" target="_blank">资讯</a><a class="col-space-1" href="/discussion/${item.id}" target="_blank">讨论</a><a class="col-space-1"
                href="/admin/reply?discussionId=${item.id}" target="_blank">观点管理</a></td>
              <td><c:choose>
                  <c:when test="${1 eq item.status or 0 eq item.status or -3 eq item.status}">
                    <select class="op-choose-status" name="status" style="width: 80px;">
                      <option value="1" <c:if test="${1 eq item.status}">selected</c:if>>可见</option>
                      <option value="0" <c:if test="${0 eq item.status}">selected</c:if>>隐藏</option>
                      <option value="-3" <c:if test="${-3 eq item.status}">selected</c:if>>删除</option>
                    </select>
                  </c:when>
                  <c:otherwise>${item.status}</c:otherwise>
                </c:choose></td>
            </tr>
          </c:forEach>
        </tbody>
      </table>

      <ul class="pagination">
        <li><a href="/admin/discussion?status=${status}&itemId=${itemId}&page=1">首页</a></li>
        <c:forEach var="page" items="${pages}">
          <c:choose>
            <c:when test="${page eq currentPage}">
              <li class="active"><a href="#">${page}</a></li>
            </c:when>
            <c:otherwise>
              <li><a href="/admin/discussion?status=${status}&itemId=${itemId}&page=${page}">${page}</a></li>
            </c:otherwise>
          </c:choose>
        </c:forEach>
        <li><a href="/admin/discussion?status=${status}&itemId=${itemId}&page=${lastPage}">尾页</a></li>
      </ul>
    </div>
  </div>
</div>

<script>
  $(function() {
    $('.op-choose-status').change(function() {
      var id = $(this).parent().parent().attr('data-id');
      var value = $(this).val();

      $.getJSON('/admin/discussion/' + id + '/update_status', {
        value : value
      }, function(resp) {
        if (!resp || resp.e != 0) {
          alert('更新失败！');
        }
      });
    });

  });
</script>