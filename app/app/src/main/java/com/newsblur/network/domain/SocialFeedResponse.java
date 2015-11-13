package com.newsblur.network.domain;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;
import com.newsblur.domain.Feed;
import com.newsblur.domain.Story;
import com.newsblur.domain.UserProfile;

public class SocialFeedResponse implements Serializable
{

  private static final long serialVersionUID = 1L;

  @SerializedName("stories")
  public Story[]            stories;

  @SerializedName("feeds")
  public Feed[]             feeds;

  @SerializedName("user_profiles")
  public UserProfile[]      userProfiles;

  public boolean            authenticated;

}