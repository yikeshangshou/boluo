$(function() {
  var itemId = $('#itemId').val();

  $('.m-download').click(function() {
    var userAgent = window.navigator.userAgent.toLowerCase();
    if (userAgent.indexOf('micromessenger') > 0 || userAgent.indexOf('weibo') > 0) {
      $('.header-tip').show();

    } else {
      location.href = 'http://itunes.apple.com/cn/app/da-bo-luo-fen-xiang-ni-guan/id1130904670?mt=8';
    }
    return false;
  });

  $('.close').click(function(e) {
    $('.header-tip').hide();
    return false;
  });

  $('.social-share').click(function() {
    var target = $(this).attr('data-target');
    var url = $('meta[property="og:url"]').attr('content');
    var title = $('meta[property="og:title"]').attr('content');
    title = encodeURIComponent(title + ' 快来一起讨论一下这条资讯吧。');
    var pic = $('meta[property="og:image"]').attr('content');

    if (target == 'weibo') {
      var link = 'http://service.weibo.com/share/share.php?appkey=4029652859&url=';
      link += url + '&title=' + title + '&pic=' + pic;
      location.href = link;
    }
  });

});