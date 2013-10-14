(function($) {
  var _undefined = null, _window = this, _hm = null, _hash = {}, _jwindow = $(window), _jdocument = $(document), _browser = navigator.userAgent.toLowerCase(), _location = document.location, 
  DP = _window.DP = function() {
    $b = _hm.browser, $l = _hm.location, $h = _hm.hash;
  };

  _hm = _window.DP.prototype = {
  objExtend : function(child, parent, overrides) {
    if (parent && child) {
      var F = function() {
      };
      F.prototype = parent.prototype;
      child.prototype = new F();
    }
    if (overrides)
      for ( var i in overrides)
        child.prototype[i] = overrides[i];
  },
  coreExtend : function(source) {
    this.objExtend(_hm, null, source);
  },
  getOffset : function(e) {
    return {
    'top' : e.pageY ? e.pageY : e.clientY + document.body.scrollTop + document.documentElement.scrollTop,
    'left' : e.pageX ? e.pageX : e.clientX + document.body.scrollLeft + document.documentElement.scrollLeft
    };
  },
  getPageScroll : function() {
    var xScroll = 0, yScroll = 0;
    if (self.pageYOffset) {
      yScroll = self.pageYOffset;
      xScroll = self.pageXOffset;
    } else if (document.documentElement && document.documentElement.scrollTop) { // Explorer 6 Strict
      yScroll = document.documentElement.scrollTop;
      xScroll = document.documentElement.scrollLeft;
    } else if (document.body) {// all other Explorers
      yScroll = document.body.scrollTop;
      xScroll = document.body.scrollLeft;
    }
    return new Array(xScroll, yScroll);
  },
  getPageHeight : function() {
    return _jdocument.height();
  },
  getPageWidth : function() {
    return _jdocument.width();
  },
  getViewport : function() {
    return {
      'width' : _jwindow.width(),
      'height' : _jwindow.height()
    };
  },
  getPosition : function(obj) {
    var _position = obj.offset();
    return {
    'top' : _position.top,
    'left' : _position.left,
    'width' : _position.left + obj.width()
    };
  },
  getArgs : function() {
    var args = new Object();
    var query = _location.search.substring(1);
    var pairs = query.split("&");
    for ( var i = 0; i < pairs.length; i++) {
      var pos = pairs[i].indexOf('=');
      if (pos == -1)
        continue;
      var argname = pairs[i].substring(0, pos);
      var value = pairs[i].substring(pos + 1);
      value = decodeURIComponent(value);
      args[argname] = value;
    }
    return args;
  },
  unionArray : function(array1, array2) {
    try {
      return array1.concat(array2).reduce(function(result, value) {
        result = [].concat(result);
        return (!result.some(function(match) {
          return JSON.stringify(match) === JSON.stringify(value);
        }) ? result.concat(value) : result);
      });
    } catch (e) {
      return $.merge(array1, array2);
    }
  },
  // loading javascript
  loadJS : function(url) {
    var script = document.createElement("script");
    script.type = 'text/javascript';
    script.charset = 'utf-8';
    script.src = url;
    document.body.appendChild(script);
  },
  // loading style sheet
  loadCSS : function(url) {
    var css = document.createElement("link");
    css.rel = "stylesheet";
    css.type = "text/css";
    css.href = url;
    document.head.appendChild(css);
  }
  };

  /* String extend */
  _hm.objExtend(String, null, {
  isError : function() {
    return this.indexOf('@@') != -1 ? this.substr(2) : false;
  },
  isSpecial : function() {
    var limit_char = /[~!\#$^&*\=+|:;?"<,.>']/g;
    return (limit_char.test(this)) ? true : false;
  },
  isEmail : function() {
    return (/^[_a-zA-Z0-9-\.\-]+@[\.a-zA-Z0-9-\-]+\.[a-zA-Z\-]+$/.test(this)) ? true : false;
  },
  isNumber : function() {
    return (/^\d+$/.test(this)) ? true : false;
  },
  isBlank : function() {
    return /^\s*$/.test(this);
  },
  isMobilePhone : function() {
    return (/^01[016789]/g.test(this)) ? true : false;
  },
  autoLink : function() {
    if (this)
      return this.replace(/((http|https):\/\/([a-z0-9-]+\.[a-zA-Z0-9:&#@=_~%;\?\/\.\+-]+))/, '<a href="$1" target="_blank" title="$1">$3</a>');
    else
      return this;
  },
  hasWhiteSpace : function() {
    return /\s/g.test(this);
  },
  getTimeAgo : function() {
    var inputDate = new Date(parseInt(this, 10)), currentYear = new Date().format('yy'), inputYear = inputDate.format('yy'), nDiff = (new Date().getTime() / 1000) - (inputDate.getTime() / 1000), nDayDiff = Math.floor(nDiff / 86400);
    if (isNaN(nDayDiff) || nDayDiff < 0)
      return '';
    return nDayDiff == 0 && (nDiff < 60 && parseInt(nDiff) + '초전' || nDiff < 120 && '1분전' || nDiff < 3600 && Math.floor(nDiff / 60) + '분전' || nDiff < 7200 && '1시간전' || nDiff < 86400 && Math.floor(nDiff / 3600) + '시간전') || (nDayDiff < 8 && nDayDiff + '일전') || (currentYear == inputYear && inputDate.format('MM/dd') || currentYear != inputYear && inputDate.format('yy/MM/dd'));
  },
  getNumber : function() {
    return String(Number(this.replace(/[^0-9]/g, '')));
  },
  getStripSpecial : function() {
    var specialChars = /[-~!\#$^&*\=+|:;?"<,.>']/;
    return this.split(specialChars).join('');
  },
  getKillWhiteSpace : function() {
    return this.replace(/\s/g, '');
  },
  getReduceWhiteSpace : function() {
    return this.replace(/\s+/g, ' ');
  },
  getSearchKeyword : function() {
    var searchExceptChars = /[%\#\=+*:;?\[\]\{\}]/;
    return this.split(searchExceptChars).join('');
  },
  getCommify : function() {
    var pattern = /(-?[0-9]+)([0-9]{3})/, str = this;
    while (pattern.test(str))
      str = str.replace(pattern, "$1,$2");
    return str;
  },
  getPhoneFormat : function() {
    return this.replace(/(^02.{0}|^01.{1}|[0-9]{3})([0-9]+)([0-9]{4})/, "$1-$2-$3");
  },
  limitCharacters : function(maxLength) {
    var str = this;
    var len = 0;
    for ( var i = 0; i < str.length; i++) {
      len += (str.charCodeAt(i) > 128) ? 2 : 1;
      if (len > maxLength) {
        return str.substring(0, i).toString();
      }
    }
    return str;
  },
  getBytes : function() {
    try {
      var code, bytes = 0, str = this;
      var len = str.length;
      for ( var i = 0; i < len; i++) {
        code = str.charCodeAt(i);
        if (code > 128)
          bytes += 2;
        else if (code > 63 && code < 91)
          bytes += 1.5;
        else
          bytes += 1;
      }
      return bytes;
    } finally {
      code = null, bytes = null, str = null;
    }
  },
  /**
   * 참고 : http://okjsp.pe.kr/seq/30371 한글을 2바이트 씩 계산하여 입력받은 문자열이 DB에 저장될 때 총
   * 몇바이트를 차지하는지 계산한다. 엔터(\r\n)는 2바이트를 차지한다.
   * 
   * @param val :
   *          입력받은 문자열
   */
  getLen : function() {
    var val = this;
    // 입력받은 문자열을 escape() 를 이용하여 변환한다.
    // 변환한 문자열 중 유니코드(한글 등)는 공통적으로 %uxxxx로 변환된다.
    var temp_estr = escape(val);
    var s_index = 0;
    var e_index = 0;
    var temp_str = "";
    var cnt = 0;
    // 문자열 중에서 유니코드를 찾아 제거하면서 갯수를 센다.
    while ((e_index = temp_estr.indexOf("%u", s_index)) >= 0) // 제거할 문자열이 존재한다면
    {
      temp_str += temp_estr.substring(s_index, e_index);
      s_index = e_index + 6;
      cnt++;
    }
    temp_str += temp_estr.substring(s_index);
    temp_str = unescape(temp_str); // 원래 문자열로 바꾼다.
    // 유니코드는 2바이트 씩 계산하고 나머지는 1바이트씩 계산한다.
    return Number((cnt * 2) + temp_str.length);
  },
  /* cut string */
  getCut : function(len, last) {
    var str = this;
    var l = 0;
    for ( var i = 0; i < str.length; i++) {
      l += (str.charCodeAt(i) > 128) ? 2 : 1;
      if (l > len) {
        if (typeof last != 'undefined' && last != null)
          return str.substring(0, i) + last;
        else
          return str.substring(0, i).toString();
      }
    }
    return String(str);
  },
  removeOneTag : function(str, tag) {
    var op, tp, cp, lt, gt, copy;
    op = 0, lt = "&lt;", gt = "&gt;";
    str = str.replace(/</g, "&lt;");
    str = str.replace(/>/g, "&gt;");
    str = str.replace(/\//g, "&#8260;");
    copy = str;
    str = str.toLowerCase();
    while ((op = str.indexOf(lt + tag, op)) != -1) {
      tp = str.substring(op + lt.length + tag.length, op + lt.length + tag.length + 1);
      if (isAlNum(tp)) {
        op = op + lt.length + tag.length + 1;
        continue;
      }
      if ((cp = str.indexOf(lt + "&#8260;" + tag + gt, op)) == -1) {
        tp = str.indexOf(gt, op);
        str = str.substring(0, op) + str.substring(tp + 4, str.length);
        copy = copy.substring(0, op) + copy.substring(tp + 4, copy.length);
      } else {
        if ((tag == "script") || (tag == "style") || (tag == "object")) {
          tp = str.indexOf(gt, op);
          str = str.substring(0, op) + str.substring(cp + tag.length + 9 + 6, str.length);
          copy = copy.substring(0, op) + copy.substring(cp + tag.length + 9 + 6, copy.length);
        } else {
          tp = str.indexOf(gt, op);
          str = str.substring(0, op) + str.substring(tp + 4, cp) + str.substring(cp + tag.length + 9 + 6, str.length);
          copy = copy.substring(0, op) + copy.substring(tp + 4, cp) + copy.substring(cp + tag.length + 9 + 6, copy.length);
        }
      }
    }
    return copy;
  },
  removeTags : function(str, myTags) {
    var tags = [ '!--', '!doctype', 'isindex', 'script', 'blockquote', 'style', 'input', 'plaintext', 'body', 'colgroup', 'fieldset', 'frameset', 'multicol', 'noframes', 'noscript', 'optgroup', 'textarea', 'basefont', 'acronym', 'address', 'caption', 'comment', 'listing', 'marquee', 'noembed', 'nolayer', 'bgsound', 'applet', 'button', 'center', 'iframe', 'ilayer', 'legend', 'nextid', 'object', 'option', 'select', 'server', 'spacer', 'strike', 'strong', 'keygen', 'blink', 'embed', 'label', 'layer', 'small', 'table', 'tbody', 'tfoot', 'thead', 'title', 'param', 'frame', 'abbr', 'area', 'cite', 'code', 'font', 'form', 'head', 'html', 'menu', 'nobr', 'ruby', 'samp', 'span', 'base', 'link', 'meta', 'bdo', 'big', 'del', 'dfn', 'dir', 'div', 'ins', 'kbd', 'map', 'pre', 'sub', 'sup', 'var', 'xmp', 'img', 'col', 'wbr', 'br', 'dd', 'dl', 'dt', 'em', 'h1', 'h2', 'h3', 'h4', 'h5', 'h6', 'li', 'ol', 'rb', 'rp', 'rt', 'td', 'th', 'tr', 'tt', 'ul', 'hr', 'a', 'b', 'i', 'p', 'q', 's', 'u' ];
    if (myTags) {
      for ( var i = 0; i < myTags.length; i++) {
        for ( var j = 0; j < tags.length; j++) {
          if (myTags[i] == tags[j]) {
            tags.splice(j, 1);
            break;
          }
        }
      }
    }
    for ( var i = 0; i < tags.length; i++) {
      str = this.removeOneTag(str, tags[i]);
    }
    return str.replace(/&lt;/g, "<").replace(/&gt;/g, ">").replace(/&#8260;/g, "\/");
  },
  startsWith : function(str) {
    return this.indexOf(str) == 0;
  },
  zf : function(len) {
    return "0".getCut(len - this.length) + this;
  },
  unionParam : function() {
    var url = this, param, p;
    param = url.indexOf('?') > 0 ? url.slice(url.indexOf('?') + 1).split('&') : '';

    for ( var i = 0; i < param.length; i++) {
      p = param[i].split('=');
      _hm.Json.parameters[p[0]] = decodeURIComponent($.trim(p[1]));
    }
  },
  extParam : function() {
    var p = {}, url = this, param, tmp;
    param = url.indexOf('?') > 0 ? url.slice(url.indexOf('?') + 1).split('&') : '';

    for ( var i = 0; i < param.length; i++) {
      tmp = param[i].split('=');
      p[tmp[0]] = decodeURIComponent($.trim(tmp[1]));
    }
    return p;
  },
  findParam : function(need) {
    var t = this.split('?').pop(), param = t.split('&'), idx = param.length;
    while (idx--) {
      var val = param[idx].split('=');
      if (val[0] == need) {
        return val[1];
        break;
      }
    }
  }
  
  });
  
  /* Number extend */
  _hm.objExtend(Number, null, {
    zf : function(len) {
      return this.toString().zf(len);
    }
  });

  /* Array extend */
  _hm.objExtend(Array, null, {
    remove : function(from, to) {
      var rest = this.slice((to || from) + 1 || this.length);
      this.length = from < 0 ? this.length + from : from;
      return this.push.apply(this, rest);
    }
  });

  /* Date extend */
  _hm.objExtend(Date, null, {
    /**
     * //2011년 09월 11일 오후 03시 45분 42초 console.log(new Date().format("yyyy년 MM월
     * dd일 a/p hh시 mm분 ss초")); //2011-09-11 console.log(new
     * Date().format("yyyy-MM-dd")); //'11 09.11 console.log(new
     * Date().format("'yy MM.dd")); //2011-09-11 일요일 console.log(new
     * Date().format("yyyy-MM-dd E")); //현재년도 : 2011 console.log("현재년도 : " + new
     * Date().format("yyyy"));
     * 
     * @param {Object}
     *          f
     */
    format : function(f) {
      if (!this.valueOf())
        return " ";
      var weekName = [ "일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일" ];
      var d = this;

      return f.replace(/(yyyy|yy|MM|dd|E|hh|mm|ss|a\/p)/gi, function($1) {
        switch ($1) {
        case "yyyy":
          return d.getFullYear();
        case "yy":
          return (d.getFullYear() % 1000).zf(2);
        case "MM":
          return (d.getMonth() + 1).zf(2);
        case "dd":
          return d.getDate().zf(2);
        case "E":
          return weekName[d.getDay()];
        case "HH":
          return d.getHours().zf(2);
        case "hh":
          return ((h = d.getHours() % 12) ? h : 12).zf(2);
        case "mm":
          return d.getMinutes().zf(2);
        case "ss":
          return d.getSeconds().zf(2);
        case "a/p":
          return d.getHours() < 12 ? "오전" : "오후";
        default:
          return $1;
        }
      });
    }
  });

  // event extends
  $.fn.extend({
  hasAttr : function(name) {
    return this.attr(name) !== undefined && this.attr(name) !== false;
  },
  attrCount : function() {
    var count = 0;
    if (this.length > 0) {
      $this = this[0];
      for ( var prop in $this) {
        if ($this.hasOwnProperty(prop)) {
          count = count + 1;
        }
      }
    }
    return count;
  },
  equals : function(selector) {
    return $(this).get(0) == $(selector).get(0);
  },
  toHtmlString : function() {
    return $('<div></div>').html($(this).clone()).html();
  },
  getStyleObject : function() {
    var dom = this.get(0);
    var style;
    var returns = {};
    if (window.getComputedStyle) {
      var camelize = function(a, b) {
        return b.toUpperCase();
      };
      style = window.getComputedStyle(dom, null);
      for ( var i = 0, l = style.length; i < l; i++) {
        var prop = style[i];
        var camel = prop.replace(/\-([a-z])/g, camelize);
        var val = style.getPropertyValue(prop);
        returns[camel] = val;
      };
      return returns;
    };
    if (style = dom.currentStyle) {
      for ( var prop in style) {
        returns[prop] = style[prop];
      };
      return returns;
    };
    return this.css();
  }
  });

})(jQuery);
var DP = new DP;