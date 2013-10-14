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
 * Document Information
 * 
 * @author 작은광명
 * 
 */
public class Document {

    /** 게시물 ID */
    private String id;

    /** 게시물 번호 */
    private String no;

    /** 게시물 제목 */
    private String title;

    /** 게시물 URL */
    private String url;

    /** 게시물 작성자 ID */
    private String userId;

    /** 게시물 작성자 닉네임 */
    private String userName;

    /** 게시물 작성일자 */
    private String date;

    /** 댓글 수 */
    private String commentCount;

    /** 추천 수 */
    private String recommendCount;

    /** 조회 수 */
    private String visitCount;

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

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }

    public String getRecommendCount() {
        return recommendCount;
    }

    public void setRecommendCount(String recommendCount) {
        this.recommendCount = recommendCount;
    }

    public String getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(String visitCount) {
        this.visitCount = visitCount;
    }

    @Override
    public String toString() {
        return "Document [id=" + id + ", no=" + no + ", title=" + title + ", url=" + url + ", userId=" + userId + ", userName=" + userName + ", date=" + date + ", commentCount=" + commentCount + ", recommendCount=" + recommendCount + ", visitCount=" + visitCount + "]";
    }

}
