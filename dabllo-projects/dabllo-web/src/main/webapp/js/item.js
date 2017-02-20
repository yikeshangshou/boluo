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

        var images = $('input[name=image]', $('#item-form')).val();
        images = (images == null || images.length == 0) ? path : (images + ',' + path);
        $('input[name=image]', $('#item-form')).val(images);
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

  $('#item-form').validate({
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
        url : true,
        maxlength : 400
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
        url : '请输入一个正确的链接。',
        maxlength : $.format('链接不能超过{0}个字。')
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
            Message.info('提交成功，您可以继续投稿资讯。', true, $('.form-group:last'));
            $('input,textarea', $(form)).val('');
            $('div', $('.image-container')).each(function() {
              if ($(this).attr('id') != 'image-finder') {
                $(this).remove();
              }
            });

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