var configurationChanged = function(deviceWidth) {
	var movieTmpl = '<div class="movieBg">';
	movieTmpl += '<img src="{img}" class="movieImg">';
	movieTmpl += '<a href="{url}"><img src="../img/play-button.png" class="moviePlay"></a>';
	movieTmpl += '</div>',
	buttonTmpl = '<p class="viewMovie" style="padding-top:5px;padding-bottom:40px;"><a href="{url}" class="btn btn-primary btn-sm pull-right"><span class="glyphicon glyphicon-expand"></span> 위 동영상 링크로 보기</a></p>';
	// template
	var dptmpl = function(template, data) {
		return template.replace(/\{([\w\.]*)\}/g, function(str, key) {
			var keys = key.split("."), value = data[keys.shift()];
			$.each(keys, function() {
				value = value[this];
			});
			return (value === null || value === undefined) ? "" : value;
		});
	};
	// movie tag
	var makeMovieTag = function(url, vid) {
		var img = 'http://img.youtube.com/vi/';
		if (url.indexOf('youtube.com') > 0) {
			img += vid + '/0.jpg';
		} else {
			img = '../img/play-background.jpg';
		}
		return dptmpl(movieTmpl, {url: url, img: img});
	};
	// movie button
	var makeMovieBtn = function(url) {
		return dptmpl(buttonTmpl, {url: url});
	}
	
	var _width = 0;
	if (deviceWidth) {
		_width = deviceWidth - (deviceWidth * 0.02);
	} else {
		_width = DP.getViewport()['width'] - (DP.getViewport()['width'] * 0.02);
	}
	
	// 기존 링크 보기 링크 버튼 제거
	$('p.viewMovie').remove();
	// 본문 오브젝트 정리
	$('font').each(function() {
		try {
			if ($(this).hasAttr('size') && Number($(this).attr('size')) < 3) {
				$(this).removeAttr('size');
			}
		} catch(e) {
			$(this).removeAttr('size');
		}
	});
	$('table').attr('width', '100%');
	$('div,td,span').each(function() {
		$(this).css('word-break', 'break-all').css('max-width', _width);
		if ($(this).width() > _width) {
			$(this).removeAttr('width');
		}
	});
	$('img:not(.avartar)')
	.removeAttr('style')
	.removeAttr('width')
	.removeAttr('height')
	.css({
		'max-width' : _width,
		'height' : 'auto'
	});
	$('[src]').each(function() {
		var _vid = null, _url, _src = $(this).attr('src');
		if (this.nodeName === 'IFRAME') {
			// YouTube일 경우
			if (_src.indexOf('youtube.com') > 0) {
				_vid = _src.indexOf('?')>0?_src.split('/').pop().split('?').shift():_src.split('/').pop();
				_url = 'http://www.youtube.com/watch?v=' + _vid;
			}
			// 다음팟일 경우
			else if (_src.indexOf('videofarm.daum.net') > 0) {
				_vid = _src.findParam('vid');
				_url = 'http://tvpot.daum.net/v/' + _vid;
			}
			// Vimeo일 경우
			else if (_src.indexOf('player.vimeo.com') > 0) {
				_vid = _src.indexOf('?')>0?_src.split('/').pop().split('?').shift():_src.split('/').pop();
				_url = 'http://vimeo.com/' + _vid;
			}
			// Pandora일 경우
			else if (_src.indexOf('pandora.tv') > 0) {
				_vid = _src.findParam('prgid');
				_url = 'http://www.pandora.tv/my.' + _src.findParam('userid') + '/' + _vid;
			}
			// 네이버 tvcast
			else if (_src.startsWith('http://serviceapi.rmcnmv.naver.com')) {
				_vid = _src;
				_url = _src;
			}
			
			if (_vid) {
//				$(this).before(makeMovieTag(_url, _vid)).remove();
				$(this).after(makeMovieBtn(_url));
			}
		}
		else if (this.nodeName === 'EMBED') {
			// YouTube일 경우
			if (_src.indexOf('youtube.com') > 0) {
				_vid = _src.indexOf('?')>0?_src.split('/').pop().split('?').shift():_src.split('/').pop();
				_url = 'http://www.youtube.com/watch?v=' + _vid;
			}
			// 다음팟일 경우
			else if (_src.indexOf('videofarm.daum.net') > 0) {
				_vid = $(this).attr('flashvars').findParam('vid');
				_url = 'http://tvpot.daum.net/v/' + _vid;
			}
			// Pandora일 경우
			else if (_src.indexOf('pandora.tv') > 0) {
				_vid = _src.findParam('prgid');
				_url = 'http://www.pandora.tv/my.' + _src.findParam('userid') + '/' + _vid;
			}
			// Nate일 경우
			else if (_src.indexOf('v.nate.com') > 0) {
				if ($(this).attr('flashvars')) {
					_vid = $(this).attr('flashvars').findParam('vs_keys').split('|').pop();
					_url = 'http://pann.nate.com/video/' + _vid;
				}
			}
			// 네이버 tvcast일 경우
			else if (_src.startsWith('http://serviceapi.rmcnmv.naver.com')) {
				if ($(this).hasAttr('flashvars')) {
					_vid = $(this).attr('flashvars');
					_url = 'http://serviceapi.rmcnmv.naver.com/flash/outKeyPlayer.nhn?' + _vid;
				}
			}
			
			if (_vid) {
//				$(this).parent().before(makeMovieTag(_url, _vid)).remove();
				if ($(this).parent().get(0).tagName == 'OBJECT') {
					$(this).parent().after(makeMovieBtn(_url));
				} else {
					$(this).after(makeMovieBtn(_url));
				}
			}
		}
		else if (this.nodeName === 'OBJECT') {
			// Nate일 경우
			if (_src.indexOf('v.nate.com') > 0) {
				_vid = _src.split('|').pop().split('/').shift();
				_url = 'http://pann.nate.com/video/' + _vid;
			}
			
			if (_vid) {
//				$(this).parent().before(makeMovieTag(_url, _vid)).remove();
				$(this).after(makeMovieBtn(_url));
			}
		}
	});
	$('iframe,object,embed').each(function() {
		$(this).attr('height',$(this).height() * (_width / $(this).width())).attr('width', _width);
	});

	// 댓글 오브젝트 정리
	$('#comment_list img:not(.avartar)')
	.attr('width', '100%')
	.removeAttr('style')
	.removeAttr('height');
	$('#comment_list').find('iframe,object,embed').each(function() {
		var _cmtWidth = _width - 70; //58;
		$(this).attr('height',$(this).height() * (_cmtWidth / $(this).width())).attr('width', _cmtWidth);
	});
	$('table[rel=childCmtTable]').each(function() {
		if (!$(this).find('tr').length) {
			$(this).remove();
		}
	});
	
	// 댓글 터치
//	$('td').hammer().on('touch', '.cmt', function(e) {
//		$(this).addClass('warning');
//	}).on('release', '.cmt', function(e) {
//		$(this).removeClass('warning');
//	});
	
	// 댓글 클릭
	$('button').on('click', function(e) {
		e.preventDefault();
		try {
			var _depth = $(this).data('depth'), _cmtid = $(this).data('cmtid'), _cmtno = $(this).data('cmtno'), _memberId = new Array();
			_memberId.push($('#viewContent').data('id'));
			_memberId.push(_cmtid);
			$.each($(this).parents('table').next('table').find('button'), function(i, data) {
				_memberId.push($(this).data('cmtid'));
			});
			if (_depth) {
				Android.showCommentMenu(_depth, _cmtid, $.trim(_cmtno), _memberId.join(','));
			}
		} catch(e) {}
	});
	
	// 댓글 쓰기 버튼 클릭
	$('#writeComment').on('click', function(e) {
		e.preventDefault();
		try {
			Android.clickWriteComment($('#viewContent').data('id'));
		} catch(e) {}
	});
	
	// 스크롤 타겟 정보가 있을 경우 이동한다.
	var targetCmtId = $('#target_comment_id').val();
	if (targetCmtId.length) {
		setTimeout(function() {
			$.smoothScroll({
				offset : -30,
				scrollTarget : '#comment_' + targetCmtId,
				afterScroll : function() {
					$('#comment_' + targetCmtId).effect('highlight', {}, 5000);
				}
			});
		}, 300);
	}

};

(function($) {
	try {
		Android.setViewport(DP.getViewport()['width'], DP.getViewport()['height']);
	} catch (e) {}
	// init webview code
	configurationChanged();
})(jQuery);
