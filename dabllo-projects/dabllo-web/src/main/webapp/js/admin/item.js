function bindRemoveTopic() {
  $('.remove-topic').click(function() {
    var topicId = $(this).parent().attr('data-topic-id');
    var itemId = $(this).parent().attr('data-item-id');

    if (confirm('确定要从这个专题中删除吗？')) {
      $.getJSON('/admin/item/remove_topic', {
        itemId : itemId,
        topicId : topicId
      }, function(resp) {
        if (resp && resp.e == 0) {
          $('.list-group-item[data-topic-id=' + topicId + ']', $('#TopicsModal')).remove();
          Message.info('删除成功。', true, $('.form-group:last', $('#TopicsModal')));

        } else {
          Message.error('删除失败！', false, $('.form-group:last', $('#TopicsModal')));
        }
      });
    }

    return false;
  });
}

$(function() {
  // 清除缓存。
  $('.op-refresh-cache').click(function() {
    var time = $(this).attr('data-time');

    if (confirm('确定要刷新缓存吗？')) {
      $.getJSON('/admin/system/reset_cache', {
        time : time
      }, function(resp) {
        if (resp && resp.e == 0) {
          Message.info('刷新成功。', true);

        } else {
          Message.error('刷新失败！', true);
        }
      });
    }

    return false;
  });

  // 删除。
  $('.op-delete').click(function() {
    var id = $(this).attr('data-id');
    var value = -3;

    if (confirm('确定删除吗？')) {
      $.getJSON('/admin/item/' + id + '/update_status', {
        value : value
      }, function(resp) {
        if (resp && resp.e == 0) {
          $('#item_' + id).remove();

        } else {
          Message.error('删除失败！', true);
        }
      });
    }

    return false;
  });

  $('.switch-hot').click(function() {
    var id = $(this).attr('data-id');
    var $icon = $('span', $(this));

    if (confirm('确定修改热议状态？')) {
      var value = 0;
      if ($icon.hasClass('glyphicon-volume-off')) {
        value = 1;
      } else if ($icon.hasClass('glyphicon-volume-up')) {
        value = 0;
      }

      $.getJSON('/admin/item/' + id + '/fire', {
        value : value
      }, function(resp) {
        if (resp && resp.e == 0) {
          if (resp.r == 1) {
            $icon.removeClass('glyphicon-volume-off').addClass('glyphicon-volume-up');

          } else {
            $icon.removeClass('glyphicon-volume-up').addClass('glyphicon-volume-off');
          }

        } else {
          Message.error('标记失败！', true);
        }
      });
    }

    return false;
  });
});

$(function() {
  // 编辑。
  $('#EditModal').on('show.bs.modal', function(e) {
    var $this = $(e.relatedTarget);
    var $item = $this.parent().parent().parent();

    $('#edit-itemId').val($item.attr('data-id'));
    $('#edit-title').val($item.attr('data-title'));
    $('#edit-description').val($item.attr('data-description'));
    $('#edit-link').val($item.attr('data-link'));
    $('#edit-image').val($item.attr('data-image'));
    if ($item.attr('data-imageUrl')) {
      $('.image-container img').attr('src', $item.attr('data-imageUrl')).attr('style', '');
    }
  });

  $('#EditModal').on('hidden.bs.modal', function(e) {
    $('input,textarea', $('#EditModal')).val('');
  });

  // 查看链接。
  $('.op-view-page').click(function() {
    var link = $('#edit-link').val();
    window.open(link, '_blank');

    return false;
  });

  // 下载图片处理。
  $('.op-get-image').click(function() {
    var imageUrl = $('#edit-imageUrl').val();
    if (imageUrl == null || imageUrl == '') {
      Message.error('图片链接不可以为空！', false, $('.form-group:last', $('#EditModal')));
      $('#edit-imageUrl').focus();
      return false;
    }

    $.getJSON('/api/v1/image', {
      imageUrl : imageUrl
    }, function(resp) {
      if (resp && resp.e == 0) {
        var image = resp.r;
        var path = image.path;
        var url = image.url;

        $('#edit-image').val(path);
        $('.image-container img').attr('src', url).attr('style', '');
        Message.info('图片获取完成，请检查图片是否适合使用。', false, $('.form-group:last', $('#EditModal')));

      } else {
        Message.error('图片获取失败！', false, $('.form-group:last', $('#EditModal')));
      }
    });

    return false;
  });

  $('#edit-form').validate({
    rules : {
      title : {
        required : true,
        minlength : 5,
        maxlength : 32
      },
      description : {
        maxlength : 400
      },
      link : {
        required : true
      },
      image : {
        required : true
      }
    },
    messages : {
      title : {
        required : '请输入标题。',
        minlength : $.format('标题不能少于{0}个字。'),
        maxlength : $.format('标题不能超过{0}个字。')
      },
      description : {
        maxlength : $.format('描述不能超过{0}个字。')
      },
      link : {
        required : '请输入链接。'
      },
      image : {
        required : '请输入图片链接。'
      }
    },
    submitHandler : function(form) {
      if (!$(form).valid()) {
        $('.error').eq(0).focus();
        return false;
      }

      var id = $('#edit-itemId').val();
      $(form).ajaxSubmit({
        success : function(resp) {
          if (resp && resp.e == 0) {
            var item = resp.r;
            var $panel = $('.panel', $('#item_' + id));
            if (item.title) {
              $panel.attr('data-title', item.title);
              $('.title', $panel).text(item.title);
            }
            if (item.description) {
              $panel.attr('data-description', item.description);
            }
            if (item.link) {
              $panel.attr('data-link', item.link);
            }
            if (item.image) {
              $panel.attr('data-image', item.image);
              $('.cover img', $panel).attr('src', item.imageUrl).attr('style', '');
            }

            Message.info('更新成功。', true, $('.form-group:last', $('#edit-form')));
            setTimeout(function() {
              $('#EditModal').modal('hide');
            }, 1500);

          } else {
            Message.error('更新失败：' + resp.m, false, $('.form-group:last', $('#edit-form')));
          }
        },
        error : function() {
          Message.error('更新失败！', false, $('.form-group:last', $('#edit-form')));
        }
      });
    }
  });
});

$(function() {
  // 管理标签准备数据。
  $('#TopicsModal').on('show.bs.modal', function(e) {
    var $this = $(e.relatedTarget);
    var $item = $this.parent().parent().parent();
    var itemId = $item.attr('data-id');
    $('#topic-itemId').val(itemId);

    $.getJSON('/api/v1/item/' + itemId + '/topics', {}, function(resp) {
      if (resp && resp.e == 0) {
        var topics = resp.r;
        if (topics && topics.length > 0) {
          for (var index = 0; index < topics.length; index++) {
            var topic = topics[index];
            var html = '<li class="list-group-item" data-topic-id="' + topic.id + '" data-item-id="' + itemId + '">';
            html += '<span>' + topic.title + '</span>';
            html += '<a href="javascript:void(0);" class="pull-right remove-topic">';
            html += '<span class="glyphicon glyphicon-remove" aria-hidden="true"></span>';
            html += '</a></li>';

            $(html).appendTo('#topic-list');
          }

          bindRemoveTopic();

        } else {
          $('<li class="list-group-item"><span>暂时没有加入专题。</span></li>').appendTo('#topic-list');
        }

      } else {
        $('<li class="list-group-item"><span>数据加载失败，请刷新页面再尝试。</span></li>').appendTo('#topic-list');
      }
    });
  });

  // 管理标签清理数据。
  $('#TopicsModal').on('hidden.bs.modal', function(e) {
    $('#topic-title').val('');
    $('#topic-list').html('');
  });

  $('#topic-form').validate({
    rules : {
      title : {
        required : true,
        minlength : 2,
        maxlength : 10
      }
    },
    messages : {
      title : {
        required : '请输入专题名称。',
        minlength : $.format('专题名称不能少于{0}个字。'),
        maxlength : $.format('专题名称不能超过{0}个字。')
      }
    },
    submitHandler : function(form) {
      if (!$(form).valid()) {
        $('.error').eq(0).focus();
        return false;
      }

      $(form).ajaxSubmit({
        success : function(resp) {
          if (resp && resp.e == 0) {
            Message.info('加入专题成功。', true, $('.form-group:last', $('#topic-form')));
            $('input', $('#topic-form')).val('');
            $('#topic-list').html('');
            setTimeout(function() {
              $('#TopicsModal').modal('hide');
            }, 1500);

          } else {
            Message.error('加入专题失败：' + resp.m, false, $('.form-group:last', $('#topic-form')));
          }
        },
        error : function() {
          Message.error('加入专题失败！', false, $('.form-group:last', $('#topic-form')));
        }
      });
    }
  });

});