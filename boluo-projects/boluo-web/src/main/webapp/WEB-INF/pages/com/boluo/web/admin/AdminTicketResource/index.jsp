<%@ page language="java" pageEncoding="UTF-8"%><%@ include file="/WEB-INF/commons/taglibs.jsp"%>
<div class="container row-space-top-2">
  <ol class="breadcrumb">
    <li class="active">工单处理</li>
  </ol>
  <div class="row">
    <div class="col-md-12">
      <table class="table table-bordered table-condensed table-hover" id="ticket-list">
        <thead>
          <tr>
            <th>ID</th>
            <th>联系方式</th>
            <th style="width: 50%;">意见反馈</th>
            <th>时间</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="item" items="${tickets}">
            <tr data-id="${item.id}">
              <td>${item.id}</td>
              <td>${item.contact}</td>
              <td>${item.content}</td>
              <td><jsp:useBean id="createDate" class="java.util.Date" /><jsp:setProperty name="createDate" property="time" value="${item.createTime}" /><fmt:formatDate value="${createDate}"
                  pattern="yyyy-MM-dd HH:mm" /></td>
              <td><c:choose>
                  <c:when test="${1 eq item.status or -3 eq item.status}">
                    <select class="op-choose-status" name="status" style="width: 80px;">
                      <option value="1" <c:if test="${1 eq item.status}">selected</c:if>>未处理</option>
                      <option value="-3" <c:if test="${-3 eq item.status}">selected</c:if>>已处理</option>
                    </select>
                  </c:when>
                  <c:otherwise>${item.status}</c:otherwise>
                </c:choose></td>
              <td></td>
            </tr>
          </c:forEach>
        </tbody>
      </table>

      <ul class="pagination">
        <li><a href="/admin/ticket?status=${status}&page=1">首页</a></li>
        <c:forEach var="page" items="${pages}">
          <c:choose>
            <c:when test="${page eq currentPage}">
              <li class="active"><a href="#">${page}</a></li>
            </c:when>
            <c:otherwise>
              <li><a href="/admin/ticket?status=${status}&page=${page}">${page}</a></li>
            </c:otherwise>
          </c:choose>
        </c:forEach>
        <li><a href="/admin/ticket?status=${status}&page=${lastPage}">尾页</a></li>
      </ul>
    </div>
  </div>
</div>

<script>
  $(function() {
    $('.op-choose-status').change(function() {
      var id = $(this).parent().parent().attr('data-id');
      var value = $(this).val();

      $.getJSON('/admin/ticket/' + id + '/update_status', {
        value : value
      }, function(resp) {
        if (!resp || resp.e != 0) {
          alert('更新失败！');
        }
      });
    });

  });
</script>