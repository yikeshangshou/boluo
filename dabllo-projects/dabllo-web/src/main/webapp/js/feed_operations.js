$(function() {
  // Like
  $('.like', $('.feed')).click(function() {
    var $icon = $('.glyphicon', $(this));
    var $likeCount = $('.likeCount', $(this));

    var feedId = $(this).attr('data-id');
    var value = 1;
    if ($icon.hasClass('glyphicon-heart')) {
      value = 0;
    }

    $.getJSON('/api/v1/feed/' + feedId + '/like', {
      value : value
    }, function(resp) {
      if (resp && resp.e == 0) {
        var likeStatus = resp.r;
        var currentCount = parseInt($likeCount.text());
        if (resp.r == 1) {
          $icon.removeClass('glyphicon-heart-empty').addClass('glyphicon-heart');
          $likeCount.text(currentCount >= 0 ? (currentCount + 1) : 1);

        } else {
          $icon.removeClass('glyphicon-heart').addClass('glyphicon-heart-empty');
          $likeCount.text(currentCount > 1 ? (currentCount - 1) : 0);
        }
      } else {
        alert(resp.m);
      }
    });
  });

  // Comment
  $('.comment', $('.feed')).click(function() {
    alert("Comment");
  });

  // Share
  $('.share', $('.feed')).click(function() {
    alert("Share");
  });
});