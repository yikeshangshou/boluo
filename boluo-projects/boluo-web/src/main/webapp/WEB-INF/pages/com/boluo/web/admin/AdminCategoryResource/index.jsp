<%@ page language="java" pageEncoding="UTF-8"%><%@ include file="/WEB-INF/commons/taglibs.jsp"%>
<div class="container row-space-top-2">
  <ol class="breadcrumb">
    <li class="active">类别</li>
    <li><a href="#" title="新增类别" data-toggle="modal" data-target="#AddModal"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span></a></li>
  </ol>
  <div class="row">
    <div class="col-md-12">
      <table class="table table-bordered table-condensed table-hover" id="category-list">
        <thead>
          <tr>
            <th>ID</th>
            <th>Slug</th>
            <th>名称</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="item" items="${categories}">
            <tr data-id="${item.id}" data-parentId="${item.parentId}">
              <td>${item.id}</td>
              <td>${item.slug}</td>
              <td>${item.name}</td>
              <td>${item.status}</td>
              <td><a data-toggle="modal" data-target="#UpdateModal" href="#" title="编辑类别"><i class="glyphicon glyphicon-pencil"></i></a><a class="col-space-1 op-delete" data-id="${item.id}"
                href="#" title="删除类别"><i class="glyphicon glyphicon-trash"></i></a></td>
            </tr>
          </c:forEach>
        </tbody>
      </table>

      <ul class="pagination">
        <li><a href="/admin/category?page=1">首页</a></li>
        <c:forEach var="page" items="${pages}">
          <c:choose>
            <c:when test="${page eq currentPage}">
              <li class="active"><a href="#">${page}</a></li>
            </c:when>
            <c:otherwise>
              <li><a href="/admin/category?page=${page}">${page}</a></li>
            </c:otherwise>
          </c:choose>
        </c:forEach>
        <li><a href="/admin/category?page=${lastPage}">尾页</a></li>
      </ul>
    </div>
  </div>
</div>

<div class="modal fade" id="AddModal" tabindex="-1" role="dialog" aria-labelledby="AddModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">
          <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
        </button>
        <h4 class="modal-title" id="AddModalLabel">添加类别</h4>
      </div>
      <form id="category-form" name="category-form" action="/admin/category/save" method="post" class="form-horizontal" role="form">
        <div class="modal-body">
          <div class="form-group">
            <label for="parenId" class="col-md-2 control-label">父级类别</label>
            <div class="col-md-8">
              <select id="parentId" name="parentId" class="form-control">
                <option value="0">根类目</option>
                <c:forEach var="item" items="${rootCategories}">
                  <option value="${item.id}">${item.name}</option>
                </c:forEach>
              </select>
            </div>
          </div>
          <div class="form-group">
            <label for="slug" class="col-md-2 control-label">Slug</label>
            <div class="col-md-8">
              <input type="text" id="slug" name="slug" class="form-control">
            </div>
          </div>
          <div class="form-group">
            <label for="name" class="col-md-2 control-label">类别名称</label>
            <div class="col-md-8">
              <input type="text" id="name" name="name" class="form-control">
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button type="submit" class="btn btn-success">提交</button>
          <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
        </div>
      </form>
    </div>
  </div>
</div>

<div class="modal fade" id="UpdateModal" tabindex="-1" role="dialog" aria-labelledby="UpdateModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">
          <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
        </button>
        <h4 class="modal-title" id="UpdateModalLabel">编辑类别</h4>
      </div>
      <form id="category-edit-form" name="category-edit-form" action="/admin/category" method="post" class="form-horizontal" role="form">
        <input type="hidden" id="editId" name="id" class="form-control">
        <div class="modal-body">
          <div class="form-group">
            <label for="parenId" class="col-md-2 control-label">父级类别</label>
            <div class="col-md-8">
              <select id="editParentId" name="parentId" class="form-control">
                <option value="0">根类目</option>
                <c:forEach var="item" items="${rootCategories}">
                  <option value="${item.id}">${item.name}</option>
                </c:forEach>
              </select>
            </div>
          </div>
          <div class="form-group">
            <label for="slug" class="col-md-2 control-label">Slug</label>
            <div class="col-md-8">
              <input type="text" id="editSlug" name="slug" class="form-control">
            </div>
          </div>
          <div class="form-group">
            <label for="name" class="col-md-2 control-label">类别名称</label>
            <div class="col-md-8">
              <input type="text" id="editName" name="name" class="form-control">
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button type="submit" class="btn btn-success">提交</button>
          <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
        </div>
      </form>
    </div>
  </div>
</div>

<script>
  $(function() {
    $('#UpdateModal').on('show.bs.modal', function(e) {
      var $this = $(e.relatedTarget);
      var $category = $this.parent().parent();

      $('#editId').val($category.attr('data-id'));
      $('#editSlug').val($category.children().eq(1).text());
      $('#editName').val($category.children().eq(2).text());

      var parentId = $category.attr('data-parentId');
      $("#editParentId option").each(function() {
        if ($(this).val() == parentId) {
          $(this).attr("selected", "selected");
        }
      });
    });

    $('.op-delete').click(function() {
      var id = $(this).attr('data-id');

      if (confirm('确定删除？')) {
        $.getJSON('/admin/category/' + id + '/delete', {}, function(resp) {
          if (resp && resp.e == 0) {
            $('tr[data-id=' + id + ']').remove();

          } else {
            Message.error('删除失败！', true);
          }
        });
      } else {
        return false;
      }
    });
  });

  $(function() {
    $('#category-form').validate({
      rules : {
        name : {
          required : true
        }
      },
      messages : {
        name : {
          required : "请输入类别名称！",
        }
      },
      submitHandler : function(form) {
        $('tr').removeClass('highlight');
        if (!$(form).valid()) {
          $('.error').eq(0).focus();
          return false;
        }
        $(form).ajaxSubmit({
          success : function(resp) {
            if (resp && resp.e == 0) {
              Message.info('类别创建成功！', true, $('.form-group:last'));
              setTimeout(function() {
                $("#AddModal").modal("hide");
              }, 1500);

              var category = resp.r;
              var tr = '<tr data-id="'+category.id+'" data-parent-id="'+category.parentId+'" class="highlight">';
              tr += '<td>' + category.id + '</td>';
              tr += '<td>' + category.slug + '</td>';
              tr += '<td>' + category.name + '</td>';
              tr += '<td>' + category.status + '</td>';
              tr += '<td><a href="#" title="编辑类别" data-toggle="modal" data-target="#UpdateModal" id="editCategory">';
              tr += '<i class="glyphicon glyphicon-pencil"></i></a>';
              tr += '&nbsp;&nbsp;<a href="#" title="删除类目"><i class="glyphicon glyphicon-trash"></i></a></td>';
              tr += '</tr>';
              $(tr).prependTo('#category-list tbody');

            } else {
              Message.error('类别创建失败：' + resp.m, false, $('.form-group:last'));
            }
          },
          error : function() {
            Message.error('类别创建失败！', false, $('.form-group:last'));
          }
        });
      }
    });

    $('#category-edit-form').validate({
      rules : {
        name : {
          required : true
        }
      },
      messages : {
        name : {
          required : "请输入类别名称！",
        }
      },
      submitHandler : function(form) {
        $('tr').removeClass('highlight');
        if (!$(form).valid()) {
          $('.error').eq(0).focus();
          return false;
        }

        $(form).ajaxSubmit({
          success : function(resp) {
            if (resp && resp.e == 0) {
              Message.info('类别更新成功！', true, $('.form-group:last'));
              setTimeout(function() {
                $("#UpdateModal").modal("hide");
              }, 1500);

              var category = resp.r;
              var categoryId = category.id;
              var $tr = $('tr[data-id=' + categoryId + ']');
              $tr.addClass('highlight');
              $tr.children().eq(1).text(category.slug);
              $tr.children().eq(2).text(category.name);
              $tr.children().eq(3).text(category.status);

            } else {
              Message.error('类别更新失败：' + resp.m, false, $('.form-group:last'));
            }
          },
          error : function() {
            Message.error('类别更新失败！', false, $('.form-group:last'));
          }
        });
      }
    });

  });
</script>