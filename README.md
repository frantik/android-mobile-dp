DVD PRIME Android Mobile Application
============

![Logo](gitsite/static/dp-logo.png)

[dvdprime.com][1] 접속용 안드로이드 앱입니다.

Use Libraries
--------

* #####ActionBarSherlock
액션바 지원을 위해서 [ActionBarSherlock][2]을 사용합니다.
프로젝트의 라이브러리에 참조해주세요.

* #####ActionBar-PullToRefresh
당겨서 새로고침은 액션바의 [PullToRefresh][3]를 사용하였습니다.
구글에서 사용하는 방식과 유사한 방식으로 제공하기 위해서 도입했습니다.

* #####Volley
기본 통신 모듈은 안드로이드의 [Volley][4]를 사용합니다.

* #####Flickr
이미지 업로드는 [Flickr][5]를 사용합니다.
DP 서버가 이미지 업로드를 제대로 지원 못하고 있어서 플리커에 업로드 후 URL를 첨부하는 방식입니다.

* #####Google Analytics
각 통계정보는 [Google Anaytics][6]를 사용합니다.
모바일 앱 사용량 통계를 지원해줍니다.

License
-------

    Copyright 2013 Kwangmyung Choi

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.



 [1]: http://www.dvdprime.com
 [2]: http://actionbarsherlock.com/
 [3]: https://github.com/chrisbanes/ActionBar-PullToRefresh
 [4]: https://android.googlesource.com/platform/frameworks/volley
 [5]: http://www.flickr.com/services/api/
 [6]: http://www.google.com/analytics/