<%@ page language="java" pageEncoding="UTF-8"%><%@ include file="/WEB-INF/commons/taglibs.jsp"%>
<div class="container row-space-top-2">
  <ol class="breadcrumb">
    <li class="active">用户管理</li>
  </ol>
  <div class="row">
    <div class="col-md-12">
      <table class="table table-bordered table-condensed table-hover" id="user-list">
        <thead>
          <tr>
            <th>ID</th>
            <th>手机号／用户名</th>
            <th>注册时间</th>
            <th>绑定状态</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="item" items="${users}">
            <tr data-id="${item.id}">
              <td>${item.id}</td>
              <td>${item.phone}／${item.username}</td>
              <td><jsp:useBean id="createDate" class="java.util.Date" /><jsp:setProperty name="createDate" property="time" value="${item.createTime}" /><fmt:formatDate value="${createDate}"
                  pattern="yyyy-MM-dd HH:mm" /></td>
              <td>${item.bindStatus}</td>
              <td><c:choose>
                  <c:when test="${1 eq item.status or -3 eq item.status}">
                    <select class="op-choose-status" name="status" style="width: 80px;">
                      <option value="1" <c:if test="${1 eq item.status}">selected</c:if>>可见</option>
                      <option value="-3" <c:if test="${-3 eq item.status}">selected</c:if>>封禁</option>
                    </select>
                  </c:when>
                  <c:otherwise>${item.status}</c:otherwise>
                </c:choose></td>
              <td><a href="/admin/user/${item.id}" title="查看个人资料">查看</a></td>
            </tr>
          </c:forEach>
        </tbody>
      </table>

      <ul class="pagination">
        <li><a href="/admin/user?status=${status}&bindStatus=${bindStatus}&page=1">首页</a></li>
        <c:forEach var="page" items="${pages}">
          <c:choose>
            <c:when test="${page eq currentPage}">
              <li class="active"><a href="#">${page}</a></li>
            </c:when>
            <c:otherwise>
              <li><a href="/admin/user?status=${status}&bindStatus=${bindStatus}&page=${page}">${page}</a></li>
            </c:otherwise>
          </c:choose>
        </c:forEach>
        <li><a href="/admin/user?status=${status}&bindStatus=${bindStatus}&page=${lastPage}">尾页</a></li>
      </ul>
    </div>
  </div>
</div>

<script>
  $(function() {
    $('.op-choose-status').change(function() {
      var id = $(this).parent().parent().attr('data-id');
      var value = $(this).val();

      $.getJSON('/admin/user/' + id + '/update_status', {
        value : value
      }, function(resp) {
        if (!resp || resp.e != 0) {
          alert('更新失败！');
        }
      });
    });

  });
</script>