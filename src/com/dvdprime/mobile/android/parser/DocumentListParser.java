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

import static com.dvdprime.mobile.android.util.LogUtil.*;

import java.util.ArrayList;
import java.util.List;

import net.htmlparser.jericho.Attribute;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;
import android.os.AsyncTask;

import com.dvdprime.mobile.android.constants.Config;
import com.dvdprime.mobile.android.constants.PrefKeys;
import com.dvdprime.mobile.android.model.Document;
import com.dvdprime.mobile.android.model.Filter;
import com.dvdprime.mobile.android.provider.EventBusProvider;
import com.dvdprime.mobile.android.util.GsonUtil;
import com.dvdprime.mobile.android.util.PrefUtil;
import com.dvdprime.mobile.android.util.StringUtil;

/**
 * Document List Parser
 * 
 * @author 작은광명
 * 
 */
public class DocumentListParser extends AsyncTask<Void, Void, Void> {
    /** TAG */
    private static final String TAG = makeLogTag(DocumentListParser.class.getSimpleName());

    /**
     * Parsed HTML String
     */
    private String html;

    /**
     * Block Member Id List
     */
    private List<String> filterList = null;

    public DocumentListParser(String html) {
        this.html = html;

        if (PrefUtil.getInstance().getString(PrefKeys.FILTERS, null) != null) {
            filterList = new ArrayList<String>();
            List<Filter> filters = GsonUtil.getArrayList(PrefUtil.getInstance().getString(PrefKeys.FILTERS, ""), Filter.class);
            for (Filter filter : filters) {
                filterList.add(filter.getTargetId());
            }
        }
    }

    public List<Document> parse() {
        List<Document> mResult = null;
        Source source = new Source(html);

        for (Element element : source.getAllElements(HTMLElementName.FORM)) {
            Attribute attribute = element.getAttributes().get("name");
            if (attribute != null && attribute.getValue().equals("frmAdmin")) {
                mResult = new ArrayList<Document>();
                for (Element tr : element.getAllElements(HTMLElementName.TR)) {
                    if (!tr.getAttributes().isEmpty()) {
                        List<Element> tdList = tr.getAllElements(HTMLElementName.TD);
                        if (tdList != null && !tdList.isEmpty() && tdList.get(0).getAttributes().isEmpty()) {
                            Document doc = new Document();
                            Element title = tdList.get(3).getFirstElement("a");
                            if (title == null) {
                                continue;
                            }
                            Element name = tdList.get(4).getFirstElement("p");
                            Element recommend = tdList.get(6);
                            Element visitCount = tdList.get(7);
                            String no = StringUtil.trim(tdList.get(1).getContent().toString());
                            if (StringUtil.contains(no, "ico_hot")) {
                                no = "HOT";
                            } else if (StringUtil.contains(no, "ico_cool")) {
                                no = "COOL";
                            }
                            String commentCount = tdList.get(3).getTextExtractor().toString();
                            if (!StringUtil.equals(commentCount, doc.getTitle())) {
                                commentCount = StringUtil.substringBefore(StringUtil.substringAfterLast(commentCount, "("), ")");
                            } else {
                                commentCount = "0";
                            }
                            if (recommend.getFirstElement("font") != null) {
                                doc.setRecommendCount(recommend.getFirstElement("font").getTextExtractor().toString());
                            } else {
                                doc.setRecommendCount(StringUtil.defaultIfBlank(recommend.getTextExtractor().toString(), "0"));
                            }
                            doc.setId(StringUtil.substringBetween(title.getAttributeValue("href"), "bbslist_id=", "&"));
                            doc.setNo(no);
                            doc.setUrl(StringUtil.substring(title.getAttributeValue("href"), 1));
                            doc.setTitle(title.getTextExtractor().toString());
                            doc.setCommentCount(commentCount);
                            doc.setUserId(StringUtil.substringBefore(StringUtil.substringAfter(name.getAttributeValue("onclick"), "('"), "'"));
                            doc.setUserName(name.getTextExtractor().toString());
                            doc.setDate(tdList.get(5).getTextExtractor().toString());
                            if (visitCount.getFirstElement("font") != null) {
                                doc.setVisitCount(visitCount.getFirstElement("font").getTextExtractor().toString());
                            } else {
                                doc.setVisitCount(visitCount.getTextExtractor().toString());
                            }
                            
                            if (filterList == null || !filterList.contains(doc.getUserId())) {
                                mResult.add(doc);
                            }

                            if (Config.DEBUG) {
                                LOGD(TAG, doc.toString());
                            }
                        }
                    }
                }

            }
        }

        return mResult;
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (params != null) {
            List<Document> mResult = parse();
            if (mResult != null && !mResult.isEmpty()) {
                EventBusProvider.getInstance().post(mResult);
            }

        }
        return null;
    }

}
