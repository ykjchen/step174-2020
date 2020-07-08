// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.data;

import java.util.ArrayList;
import java.util.List;

/** Class containing a numPosts-large batch of posts */
public final class PostBatch {
  private final List<String> posts;
  private int numPosts;

  /**
   * This ADT represents a mutable list of numPosts posts.
   *
   * Abstraction Function(posts) =  list of numPosts posts such that posts.get(i)
   *                                represents post i where each post is a string of text.
   * Representation Invariant: posts.size() == numPosts
   * Saftey from Rep Exposure: all fields are never returned, numPosts only increased through
   * mutator methods.
   */

  public PostBatch(List<String> posts) {
    this.posts = new ArrayList<>(posts);
    this.numPosts = posts.size();
  }

  /** Get the number of posts */
  public int getNumPosts() {
    return numPosts;
  }

  /** Get all posts as a string array */
  public String[] getPostArray() {
    String[] postArrayString = new String[numPosts];

    for (int i = 0; i < numPosts; i++) {
      postArrayString[i] = posts.get(i);
    }

    return postArrayString;
  }

  /**
   * Get the ith example post
   *
   * @param postIndex the number of the post to return, must be <= numPosts
   * @return the post in posts with index postIndex
   */
  public String getPost(int postIndex) {
    return posts.get(postIndex);
  }

  /**
   * Add a new post to the list
   *
   * @param post the post to add
   */
  public void addPost(String post) {
    posts.add(post);
    numPosts++;
  }
}
