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
package com.dvdprime.mobile.android.model;

/**
 * BBS Information
 * 
 * @author 작은광명
 * 
 */
public class Bbs {
    /** 자동 생성된 키 */
    private String id;

    /** 고유번호 ID */
    private String uniqId;

    /** 대분류 번호 */
    private int topId;

    /** 카테고리 번호 */
    private int catId;

    /** 게시판 번호 */
    private int bbsId;

    /** 게시판 그룹명 */
    private String groupTitle;

    /** 게시판 제목 */
    private String title;

    /** 게시판의 Major */
    private String major;

    /** 게시판의 Minor */
    private String minor;

    /** 게시판 번호 */
    private String masterId;

    /** 게시판 URL */
    private String targetUrl;

    /** 로그인 필요여부 */
    private int loginCheck;
    
    /** 즐겨찾기 포함 여부 */
    private int isFavorite;

    // ///////////////////////////////////////////////////////////////////////////////
    //
    // Getter/Setter
    //
    // ///////////////////////////////////////////////////////////////////////////////
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUniqId() {
        return uniqId;
    }

    public void setUniqId(String uniqId) {
        this.uniqId = uniqId;
    }

    public int getTopId() {
        return topId;
    }

    public void setTopId(int topId) {
        this.topId = topId;
    }

    public int getCatId() {
        return catId;
    }

    public void setCatId(int catId) {
        this.catId = catId;
    }

    public int getBbsId() {
        return bbsId;
    }

    public void setBbsId(int bbsId) {
        this.bbsId = bbsId;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    @Override
    public String toString() {
        return "Bbs [id=" + id + ", uniqId=" + uniqId + ", topId=" + topId + ", catId=" + catId + ", bbsId=" + bbsId + ", groupTitle=" + groupTitle + ", title=" + title + ", major=" + major + ", minor=" + minor + ", masterId=" + masterId + ", targetUrl=" + targetUrl + ", loginCheck=" + loginCheck + "]";
    }

    public String getMinor() {
        return minor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }

    public String getMasterId() {
        return masterId;
    }

    public void setMasterId(String masterId) {
        this.masterId = masterId;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public int getLoginCheck() {
        return loginCheck;
    }

    public void setLoginCheck(int loginCheck) {
        this.loginCheck = loginCheck;
    }

    public int getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(int isFavorite) {
        this.isFavorite = isFavorite;
    }

}
