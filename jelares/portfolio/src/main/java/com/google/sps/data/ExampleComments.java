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

import java.util.List;
import java.util.ArrayList;

/** Class containing numComments example comments */
public final class ExampleComments {

  private final List<String> comments;
  private int numComments;

  /**
  * a mutable list of numComments comments
  * 
  * Abstraction Function(comments) =  list of numComments comments such that comments.get(i)
  *                                   represents comment i where each comment is a string of text.
  * Representation Invariant: comments.size() == n
  * Saftey from Rep Exposure: all fields are final and never returned
  */

  public ExampleComments(List<String> comments){
    this.comments = new ArrayList<>(comments);
    this.numComments = comments.size();
  }

  /** Get the number of comments */
  public int getNumComments(){
    return numComments;
  }

  /**
  * Get the ith example comment
  *
  * @param commentIndex the number of the comment to return, must be <= numComments
  * @return the comment in comments with index commentIndex
  */
  public String getComment(int commentIndex) {
    return comments.get(commentIndex);
  }

  /**
  * Add a new comment to the list
  * 
  * @param comment the comment to add
  */
  public void addComment(String comment){
    comments.add(comment);
    numComments++;
  }
}