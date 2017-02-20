$(function() {
  $('.fileupload').fileupload({
    url : '/api/v1/image',
    dataType : 'json',
    add : function(e, data) {
      data.submit();
    },
    done : function(e, data) {
      var resp = data.result;
      if (resp && resp.e == 0) {
        var image = resp.r;
        var path = image.path;
        var url = image.url;

        var images = $('input[name=image]', $('#feed-form')).val();
        images = (images == null || images.length == 0) ? path : (images + ',' + path);
        $('input[name=image]', $('#feed-form')).val(images);
        var imageHtml = '<div class="col-md-3 col-xs-6"><a href="#" class="thumbnail">';
        imageHtml += '<img alt="' + path + '" src="' + url + '" />';
        imageHtml += '</a></div>';
        $('.image-container').prepend(imageHtml);
      }
    },
    progressall : function(e, data) {
      // update progress.
      var progress = parseInt(data.loaded / data.total * 100, 10);
      $('#progress .progress-bar').css('width', progress + '%');
    }
  }).prop('disabled', !$.support.fileInput).parent().addClass($.support.fileInput ? undefined : 'disabled');

  $('#category').change(function() {
    $('#topicId option').each(function() {
      $(this).remove();
    });

    var category = $(this).val();
    $.getJSON('/api/v1/topic', {
      category : category
    }, function(resp) {
      if (resp && resp.e == 0) {
        var topics = resp.r;
        if (topics.length > 0) {
          $('<option value="0">请选择主题</option>').appendTo('#topicId');
          for ( var index in topics) {
            var topic = topics[index];
            $('<option value="' + topic.id + '">' + topic.title + '</option>').appendTo('#topicId');
          }

        } else {
          $('<option value="-1">该类别下暂无主题</option>').appendTo('#topicId');
        }
      }
    });
  });

  $('#feed-form').validate({
    rules : {
      title : {
        required : true,
        maxlength : 200
      },
      link : {
        url : true,
        maxlength : 400
      },
      topicId : {
        required : true
      }
    },
    messages : {
      title : {
        required : '请输入动态。',
        maxlength : $.format('动态不能超过{0}个字符。')
      },
      link : {
        url : '请输入一个正确的链接。',
        maxlength : $.format('链接不能超过{0}个字符。')
      },
      topicId : {
        required : '请选择主题。'
      }
    },
    submitHandler : function(form) {
      if (!$(form).valid()) {
        $('.error').eq(0).focus();
        return false;
      }

      var topicId = $('#topicId').val();
      $(form).ajaxSubmit({
        success : function(resp) {
          if (resp && resp.e == 0) {
            Message.info('提交成功！', true, $('.form-group:last'));
            $('input,textarea', $(form)).val('');
            setTimeout(function() {
              window.location.href = '/topic/' + topicId;
            }, 3000);

          } else {
            Message.error('提交失败：' + resp.m, true, $('.form-group:last'));
          }
        },
        error : function() {
          Message.error('提交失败，请稍后重试！', true, $('.form-group:last'));
        }
      });
    }
  });

});