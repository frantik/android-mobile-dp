/**
 * Copyright 2013 작은광명
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dvdprime.mobile.android.parser;

import static com.dvdprime.mobile.android.util.LogUtil.LOGD;
import static com.dvdprime.mobile.android.util.LogUtil.makeLogTag;

import java.util.List;

import net.htmlparser.jericho.Attribute;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import com.dvdprime.mobile.android.constants.Config;
import com.dvdprime.mobile.android.provider.EventBusProvider;
import com.dvdprime.mobile.android.util.PrefUtil;
import com.dvdprime.mobile.android.util.StringUtil;

/**
 * Document View Parser
 * 
 * @author 작은광명
 * 
 */
public class DocumentViewParser {
    /** TAG */
    private static final String TAG = makeLogTag(DocumentListParser.class.getSimpleName());

    /**
     * Parsed HTML String
     */
    private String html;

    /**
     * Scroll Target Id
     */
    private String targetKey;

    public DocumentViewParser(String html, String targetKey) {
        this.html = html;
        this.targetKey = targetKey == null ? "" : targetKey;
    }

    public String parse() {
        StringBuffer sb = new StringBuffer();
        Source source = new Source(html);
        String accountId = PrefUtil.getInstance().getString("account_id", "");

        for (Element element : source.getAllElements(HTMLElementName.TD)) {
            Attribute attribute = element.getAttributes().get("width");
            if (attribute != null && attribute.getValue().equals("670")) {
                List<Element> contentList = element.getAllElements(HTMLElementName.TABLE);
                // 상단 제목 라인 테이블 추출
                Element el = contentList.get(1);
                // 작성자 프로필 이미지 추출
                String profileUrl = null;
                for (Element imgEl : el.getAllElements(HTMLElementName.IMG)) {
                    if (imgEl.getAttributes() != null && imgEl.getAttributeValue("width").equals("55")) {
                        profileUrl = Config.getAbsoluteUrl(imgEl.getAttributeValue("src"));
                        EventBusProvider.getInstance().post(profileUrl);
                        break;
                    }
                }
                // 제목 추출
                String title = el.getFirstElement("span").getTextExtractor().toString();
                // 아이디 추출
                String id = StringUtil.substringBefore(StringUtil.substringAfter(el.getFirstElement("a").getAttributeValue("onclick"), "'"), "'");
                // 닉네임 추출
                String nick = el.getFirstElement("a").getTextExtractor().toString();
                if ((StringUtil.isNotBlank(title)) && (StringUtil.isNotBlank(id)) && (StringUtil.isNotBlank(nick))) {
                    EventBusProvider.getInstance().post(id + "|" + nick + "|" + title);
                }
                // 등록일
                String temp = el.getFirstElement("font").getTextExtractor().toString();
                String date = StringUtil.substringBefore(StringUtil.substringAfter(temp, ":"), "조");
                String viewCnt = StringUtil.substringAfterLast(temp, ":");

                // 내용
                String content = contentList.get(7).getFirstElement("div").getContent().toString();

                // 태그
                Element tagElement = contentList.get(8).getFirstElement("bgcolor", "#F5F5F5", false);
                String tag = tagElement == null ? "" : tagElement.getTextExtractor().toString();

                // 댓글
                String comment = "";
                StringBuffer cmtList = new StringBuffer();

                for (Element table : source.getAllElements("width", "650", true)) {
                    if (table.getName().equals("table") && table.getAttributeValue("align") == null) {
                        List<Element> ids = table.getAllElements("class", "recom_id", false);

                        if (ids != null && ids.size() > 1) {
                            String cmtNo = StringUtil.substringBefore(StringUtil.substringAfter(((Element) ids.get(1)).getAttributeValue("onClick"), "("), ",");
                            String cmtId = StringUtil.substringBetween(((Element) ids.get(0)).getAttributeValue("onClick"), "'", "'");
                            String cmtNick = table.getFirstElement("strong").getTextExtractor().toString();
                            String cmtDate = table.getFirstElement("font").getTextExtractor().toString();
                            String cmtImage = "";
                            Element cmtImageElement = table.getFirstElement("width", "55", false);
                            if (cmtImageElement != null) {
                                cmtImage = cmtImageElement.getAttributeValue("src");
                            }
                            String cmtContent = table.getFirstElementByClass("recom").getContent().toString();
                            StringBuffer childCmt = new StringBuffer();
                            List<Element> childCmtList = table.getAllElements("width", "577", false);
                            for (Element child : childCmtList) {
                                if (child.getName().equals("table")) {
                                    List<Element> childNoList = child.getAllElements("a");
                                    String childNo = "0";
                                    if (childNoList != null && childNoList.get(childNoList.size() - 1).getAttributeValue("onClick") != null) {
                                        childNo = StringUtil.substringBetween(childNoList.get(childNoList.size() - 1).getAttributeValue("onClick"), "(", ")");
                                    }
                                    String childId = StringUtil.substringBetween(child.getFirstElement("class", "recom_id", false).getAttributeValue("onClick"), "'", "'");
                                    String childNick = child.getFirstElement("strong").getTextExtractor().toString();
                                    String childDate = child.getFirstElement("font").getTextExtractor().toString();
                                    String childContent = child.getFirstElement("color", "black", false).toString();
                                    String childDisplay = StringUtil.equals(childId, accountId) ? "" : "display:none;";
                                    childCmt.append(StringUtil.format(getChildCommentTemplate(), new String[] { childNo, childNick, childDate, childNo, childId, childDisplay, childContent }));
                                }
                            }
                            cmtList.append(StringUtil.format(getParentCommentTemplate(), new String[] { cmtImage, cmtNo, cmtNick, cmtDate, cmtNo, cmtId, cmtContent, childCmt.toString() }));
                        }
                    }
                }
                if (cmtList.length() > 0) {
                    comment = StringUtil.format(getCommentTemplate(), cmtList.toString());
                }

                // 최종 데이터 세팅
                sb.append(StringUtil.format(getContentTemplate(), new Object[] { title, id, content, date, viewCnt, tag, comment, targetKey }));

                if (Config.DEBUG) {
                    LOGD(TAG, "prifileUrl: " + profileUrl);
                    LOGD(TAG, "title: " + title);
                    LOGD(TAG, "nick: " + nick);
                    LOGD(TAG, "date: " + date);
                    LOGD(TAG, "viewCnt: " + viewCnt);
                    LOGD(TAG, "content: " + content);
                    LOGD(TAG, "comment: " + comment);
                    LOGD(TAG, "html: \n" + sb.toString());
                }
                break;
            }
        }

        return sb.toString();
    }

    private String getChildCommentTemplate() {
        StringBuffer sb = new StringBuffer();
        sb.append("<tr id='comment_{0}'>");
        sb.append("<td style='padding:5px 0 5px; 3px;'><p><span class='label label-default'>{1}</span> ");
        sb.append("<span class='pull-right' style='padding-right: 3px; margin-top: 3px; font-size: 10px; color: #888'>{2}</span></p>");
        sb.append("<button type='button' data-depth='2' data-cmtno='{3}' data-cmtid='{4}' class='btn btn-default btn-xs pull-right' style='margin: 3px;{5}''><span class='glyphicon glyphicon-plus'></span></button>");
        sb.append("{6}");
        sb.append("</td>");
        sb.append("</tr>");
        return sb.toString();
    }

    private String getParentCommentTemplate() {
        StringBuffer sb = new StringBuffer();
        sb.append("<tr>");
        sb.append("<td  width='58'><img src='{0}' style='width: 48px; height: 48px;' class='avartar img-rounded'></td>");
        sb.append("<td style='padding: 0px;'>");
        sb.append("<table id='comment_{1}' class='table table-condensed' style='background-color: rgba(0, 0, 0, 0); margin-bottom: 0px;'>");
        sb.append("<tbody><tr><td style='border: none !important; padding: 0px; padding-right: 3px;'>");
        sb.append("<p><span class='label label-default'>{2}</span> ");
        sb.append("<span class='pull-right' style='margin-top: 3px; font-size: 10px; color: #888'>{3}</span></p>");
        sb.append("<button type='button' data-depth='1' data-cmtno='{4} ' data-cmtid='{5}' class='btn btn-default btn-xs pull-right' style='margin: 3px 0px 3px 3px;''><span class='glyphicon glyphicon-plus'></span></button>");
        sb.append("{6}");
        sb.append("</td></tr></tbody></table>");
        sb.append("<table class='table table-condensed' style='background-color:rgba(0, 0, 0, 0); margin-bottom: 0px;' rel='childCmtTable'>");
        sb.append("     <tbody>{7}</tbody>");
        sb.append("</table>");
        sb.append("</td>");
        sb.append("</tr>");
        return sb.toString();
    }

    private String getCommentTemplate() {
        StringBuffer sb = new StringBuffer();
        sb.append("<!-- 댓글 목록 -->");
        sb.append("<table id='comment_list' class='table table-striped table-condensed'>");
        sb.append("<tbody>");
        sb.append("{0}");
        sb.append("</tbody>");
        sb.append("</table>");
        return sb.toString();
    }

    private String getContentTemplate() {
        StringBuffer sb = new StringBuffer();
        sb.append("<!DOCTYPE html>");
        sb.append("<html lang='ko'>");
        sb.append("<head>");
        sb.append("<meta http-equiv='Content-Type' content='text/html;charset=utf-8'>");
        sb.append("<meta http-equiv='cache-control' content='no-cache'>");
        sb.append("<meta http-equiv='expires' content='0'>");
        sb.append("<meta http-equiv='pragma' content='no-cache'>");
        sb.append("<meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no'>");
        sb.append("<link href='/assets/css/bootstrap.min.css' rel='stylesheet' media='screen'>");
        sb.append("<style type='text/css'>");
        sb.append(".movieBg{ width: 100%; position: relative; overflow: hidden; margin: 0 auto; }");
        sb.append(".movieImg{ display: block; margin: 0 auto; }");
        sb.append(".moviePlay{ position: absolute; width: 147px; height: 147px; background-image: url(../img/playbutton.png) no-repeat left top; top: 50%; left: 50%; margin-top: -70px; margin-left: -77px; z-index: 10; }");
        sb.append("</style>");
        sb.append("</head>");
        sb.append("<body style='overflow-x:hidden'>");
        sb.append("<div class='container' style='width:98%;margin-left:1%;margin-right:1%;padding-right:0px;padding-left:0px;'>");
        sb.append("<div class='alert alert-info' style='margin-top:1px;'>");
        sb.append("<p class='text-center' style='font-size:14px;margin-bottom:0px'><strong>{0}</strong></p>");
        sb.append("</div>");
        sb.append("<div id='viewContent' style='padding-bottom:20px;' data-id='{1}'>");
        sb.append("{2}");
        sb.append("</div>");
        sb.append("<table class='table table-condensed' style='width: 100%;'>");
        sb.append("<tbody>");
        sb.append("<tr>");
        sb.append("<td><small><span class='label label-default'>일시</span> <span id='viewDate' style='padding:.25em .6em; vertical-align:bottom;'>{3}</span></small></td>");
        sb.append("<td><small><span class='label label-default'>조회수</span> <span style='padding:.25em .6em; vertical-align:bottom;'>{4}</span></small></td>");
        sb.append("</tr>");
        sb.append("<tr>");
        sb.append("<td colspan='2'><small><span class='label label-default'>태그</span> <span id='viewTag' style='padding:.25em .6em; vertical-align:bottom;'>{5}</span></small></td>");
        sb.append("</tr>");
        sb.append("</tbody>");
        sb.append("</table>");
        sb.append("<div class='alert alert-warning' style='margin-bottom:1px;'>나도 한마디</div>");
        sb.append("{6}");
        sb.append("<div style='padding:3px 5% 5% 5%;'>");
        sb.append("<button type='button' id='writeComment' class='btn btn-default btn-lg btn-block'>댓글 쓰기</button>");
        sb.append("</div>");
        sb.append("<input type='hidden' id='target_comment_id' value='{7}'>");
        sb.append("<script src='/assets/js/fastclick.min.js'></script>");
        sb.append("<script src='/assets/js/jquery-2.0.3.min.js'></script>");
        sb.append("<script src='/assets/js/hammer-1.0.5.min.js'></script>");
        sb.append("<script src='/assets/js/jquery.hammer-1.0.5.min.js'></script>");
        sb.append("<script src='/assets/js/jquery.smooth-scroll.min.js'></script>");
        sb.append("<script src='/assets/js/jquery-ui-1.10.3.custom.min.js'></script>");
        sb.append("<script src='/assets/js/bootstrap.min.js'></script>");
        sb.append("<script src='/assets/js/dp.js'></script>");
        sb.append("<script src='/assets/js/view.js'></script>");
        sb.append("</body>");
        sb.append("</html>");

        return sb.toString();
    }

}