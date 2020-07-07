// Copyright 2019 Google LLC
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

/** 
 * @return {String[]} an array of links to posts in blog to be used in randomPost() 
 */
function postLinks() {
  return ['#post1-lnk', '#post2-lnk', '#post3-lnk', '#post4-lnk'];
}

/** 
 * @return {String} the id (link) to a random post on the blog 
 */
function randomPost() {
  // array with all post links
  const posts = postLinks();

  // returns a random link
  return posts[Math.floor(Math.random() * posts.length)];
}

/**
 * Tests randomPost() by verifying it spits out only one of the random post
 * options & logging the percentage of times it returns each
 * @param {number} trials optional number of trials to run to test random post
 */
function testRandomPost(trials = 10000) {
  const postLinks = postLinks();
  const postLinkCount = postLinks.length;
  const sums = new Array(postLinkCount);
  for (let i = 0; i < postLinkCount; i++) {
    sums[i] = 0;
  }

  for (let i = 0; i < trials; i++) {
    switch (randomPost()) {
      case '#post1-lnk':
        sums[0]++;
        break;
      case '#post2-lnk':
        sums[1]++;
        break;
      case '#post3-lnk':
        sums[2]++;
        break;
      case '#post4-lnk':
        sums[3]++;
        break;
      default:
        console.log('Illegal response found.');
        return;
    }
  }

  /* Logs percentage of times each one occurred; should be ~ 25% */
  for (let i = 0; i < postLinkCount; i++) {
    console.log(`${postLinks[i]}: ${sums[i] / trials * 100}%`);
  }
}

testRandomPost(10000);  // runs 10000 trials on randomPost()

/** Gets comments from data tag and updates "Comments" page with it */
async function getComments(maxComments = 50) {
  const data = await fetch('/data?max-comments=' + maxComments);
  const comments = await data.text();
  document.getElementById('comments-display').innerHTML = comments;
}

/** Deletes comments from page and removes them */
async function deleteComments() {
  const request = new Request('/delete-data', {method: 'post'});
  await fetch(request);
  await getComments();
}

// TODO: make the calls for getComments() & deleteComments() meaningful
// Currently these calls are just to satisfy make validate
getComments();
deleteComments();

/** 
* Checks validity the information from the "Contact Me" form 
* @return {boolean} True if all fields from form are valid, false otherwise
*/
function isContactFormDataValid() {
  // here are fields that need more validation than just checking if not empty
  const email = document.getElementById(email);

  const fields = [
    document.getElementById('first-name'),
    document.getElementById('last-name'),
    email,
    document.getElementById('subject'),
    document.getElementById('body'),
  ];

  // checks if any field is empty, if so returns false
  for (let i = 0; i < fields.length; i++) {
    if (isNotEmpty(fields[i].value)) continue;

    return false;
  }

  return isEmail(email.value);
}

/** 
 * @return {boolean} True if value is not null or undefined, false if it is.
 * @param {any} value the value to be evaluated as valid or invalid
 */
function isNotEmpty(value) {
  if(value) 
    return true;
  else 
    return false; // if null or undefined, will return false
}


/** 
 * @return {boolean} True if email is valid, false otherwise
 * @param {any} email the email to be checked for validity
 */
function isEmail(email) {
  const regex = new RegExp([
    '^(([^<>()[].,;:s@"]+(.[^<>()[].,;:s@"]+)*)',
    '|(".+"))@(([[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.',
    '[0-9]{1,3}])|(([a-zA-Z-0-9]+.)+',
    '[a-zA-Z]{2,}))$',
  ].join(''));

  return regex.test(String(email).toLowerCase());
}

// TODO: Remove this call once this method is triggered by submitting the form
isContactFormDataValid();

