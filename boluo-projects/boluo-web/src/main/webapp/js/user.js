var wait = 60;
function countdown(target) {
  if (wait == 0) {
    target.removeAttr('disabled');
    target.text("获取验证码");
    wait = 60;

  } else {
    target.attr('disabled', 'disabled');
    target.text("重新发送（" + wait + "）");
    wait--;
    setTimeout(function() {
      countdown(target);
    }, 1000)
  }
}

function showFormFieldError(target, message) {
  var html = '<label class="error" for="' + target + '" >' + message + '</label>';
  $('#' + target).parent().append(html);
}

$(function() {
  $('.btn-send-sms', $('#signup-form')).click(function() {
    var $btn = $(this);
    $btn.attr('disabled', 'disabled');
    $('.error', $('#signup-form')).remove();

    var phone = $('input[name=phone]', $('#signup-form')).val();
    if (phone == null || phone.length != 11) {
      $btn.removeAttr('disabled');
      showFormFieldError('phone', '请输入一个有效的11位手机号。');
      return false;
    }

    var captchaCode = $('input[name=captchaCode]', $('#signup-form')).val();
    if (captchaCode == null || captchaCode.length == 0) {
      $btn.removeAttr('disabled');
      showFormFieldError('captchaCode', '请输入验证码。');
      return false;
    }

    $.getJSON('/api/v1/sms/send', {
      phone : phone,
      captchaCode : captchaCode,
      type : 'register'
    }, function(resp) {
      if (resp && resp.e == 0) {
        $btn.text('验证码已发送！');
        setTimeout(function() {
          countdown($btn);
        }, 200);

      } else {
        $btn.removeAttr('disabled');
        if (resp.e == 100 || resp.e == 101) {
          showFormFieldError('captchaCode', resp.m);
        }
      }
    });

    return false;
  });

  $('#signin-form').validate({
    rules : {
      phone : {
        required : true
      },
      password : {
        required : true
      }
    },
    messages : {
      phone : {
        required : '请输入手机号。'
      },
      password : {
        required : '请输入登录密码。',
      }
    },
    submitHandler : function(form) {
      if (!$(form).valid()) {
        $('.error').eq(0).focus();
        return false;
      }

      var md5 = $('#password').val();
      for (var i = 0; i < 5; i++) {
        md5 = $.md5(md5);
      }
      $('#password').val(md5);

      $(form).ajaxSubmit({
        success : function(resp) {
          if (resp && resp.e == 0) {
            Message.info('登录成功。', true, $('.form-group:last'));
            var to = resp.r;
            setTimeout(function() {
              window.location.href = ((!to || to == '') ? '/' : to);
            }, 1500);

          } else {
            Message.error('登录失败：' + resp.m, true, $('.form-group:last'));
            $('#password').val('').focus();
          }
        },
        error : function() {
          Message.error('登录失败！', true, $('.form-group:last'));
          $('#password').val('').focus();
        }
      });
    }
  });

  $('#signup-form').validate({
    rules : {
      phone : {
        required : true
      },
      securityCode : {
        required : true
      },
      password : {
        required : true,
        minlength : 8,
        maxlength : 16
      },
      confirmPassword : {
        required : true,
        equalTo : "#password"
      }
    },
    messages : {
      phone : {
        required : '请输入手机号。'
      },
      securityCode : {
        required : '请输入验证码。'
      },
      password : {
        required : '请输入密码。',
        minlength : $.format('密码长度必须大于{0}位。'),
        maxlength : $.format('密码长度不能超过{0}位。')
      },
      confirmPassword : {
        required : '请重新输入一次密码。',
        equalTo : '两次输入的密码不一致。'
      }
    },
    submitHandler : function(form) {
      if (!$(form).valid()) {
        $('.error').eq(0).focus();
        $('#password').val('');
        $('#confirmPassword').val('');
        return false;
      }

      var md5 = $('#password').val();
      for (var i = 0; i < 3; i++) {
        md5 = $.md5(md5);
      }
      $('#password').val(md5);

      md5 = $('#confirmPassword').val();
      for (var i = 0; i < 3; i++) {
        md5 = $.md5(md5);
      }
      $('#confirmPassword').val(md5);

      $(form).ajaxSubmit({
        success : function(resp) {
          if (resp && resp.e == 0) {
            Message.info('注册成功，3秒后将自动跳转到登录页面。', false, $('.form-group:last', $(form)));
            setTimeout(function() {
              window.location.href = '/signin';
            }, 3000);

          } else {
            Message.error('注册失败：' + resp.m, false, $('.form-group:last', $(form)));
          }
        },
        error : function() {
          Message.error('注册失败！', false, $('.form-group:last', $(form)));
        }
      });
    }
  });

});