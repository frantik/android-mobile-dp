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
 * Comment Information
 * 
 * @author 작은광명
 * 
 */
public class Comment
{
    
    /** 댓글 id */
    private String id;
    
    /** 댓글 작성자 id */
    private String userId;
    
    /** 댓글 작성자 닉네임 */
    private String userName;
    
    /** 댓글 작성자 이미지 */
    private String avatarUrl;
    
    /** 댓글 내용 */
    private String content;
    
    /** 댓글 작성일 */
    private String date;
    
    /** 댓글 추천 수 */
    private String recommendCount;
    
    /** 댓글 고유번호 */
    private String commentId;
    
    /** 덧글의 상위 댓글 고유번호 */
    private String parentId;
    
    // ///////////////////////////////////////////////////////////////////////////////
    //
    // Getter/Setter
    //
    // ///////////////////////////////////////////////////////////////////////////////
    public String getId()
    {
        return id;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public String getUserId()
    {
        return userId;
    }
    
    public void setUserId(String userId)
    {
        this.userId = userId;
    }
    
    public String getUserName()
    {
        return userName;
    }
    
    public void setUserName(String userName)
    {
        this.userName = userName;
    }
    
    public String getAvatarUrl()
    {
        return avatarUrl;
    }
    
    public void setAvatarUrl(String avatarUrl)
    {
        this.avatarUrl = avatarUrl;
    }
    
    public String getContent()
    {
        return content;
    }
    
    public void setContent(String content)
    {
        this.content = content;
    }
    
    public String getDate()
    {
        return date;
    }
    
    public void setDate(String date)
    {
        this.date = date;
    }
    
    public String getRecommendCount()
    {
        return recommendCount;
    }
    
    public void setRecommendCount(String recommendCount)
    {
        this.recommendCount = recommendCount;
    }
    
    public String getCommentId()
    {
        return commentId;
    }
    
    public void setCommentId(String commentId)
    {
        this.commentId = commentId;
    }
    
    public String getParentId()
    {
        return parentId;
    }
    
    public void setParentId(String parentId)
    {
        this.parentId = parentId;
    }
    
}
