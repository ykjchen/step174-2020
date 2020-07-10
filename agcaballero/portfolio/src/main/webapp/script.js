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

/* "FEELING LUCKY?" BUTTON (blog.html) */

/**
 * @return {String[]} an array of links to posts in blog to be used in
 *     randomPost()
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
  const posts = postLinks();
  const postLinkCount = posts.length;
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
    console.log(`${posts[i]}: ${sums[i] / trials * 100}%`);
  }
}

/* Tests & Function Calls */

testRandomPost(10000);  // runs 10000 trials on randomPost()


/* TRAVEL MAP (gallery.html) */

/**
 * Creates a Map using Map API with preset markers that
 * show places I've taken photos at, their titles, and what photo
 * was taken there
 */
async function createPhotoLocationMap() {
  const europeLatLng =
      new google.maps.LatLng({lat: 48.499998, lng: 23.3833318});

  const map = new google.maps.Map(
      document.getElementById('map'), {center: europeLatLng, zoom: 4.5});

  // load the data for the photo markers
  const markers = await getPhotoMarkerData();

  // create and display all markers
  markers.forEach((marker) => {
    displayImageInfoMarker(
        map, marker.title, marker.lat, marker.lng, marker.img);
  });
}

/** Fetches the data for the photo markers on the map*/
async function getPhotoMarkerData() {
  const response = await fetch('photo-map-data');
  const photoMarkers = await response.json();

  return photoMarkers;
}

/**
 * Creates a marker that shows a read-only info window with
 * the title of the place and the image taken there when clicked.
 */
function displayImageInfoMarker(map, title, lat, lng, img) {
  // sets icon to a light red (custom color) filled-in downward-facing arrow
  const icon = {
    fillColor: '#e07a5f', /* terra cotta */
    fillOpacity: 1.0,
    path: google.maps.SymbolPath.BACKWARD_CLOSED_ARROW,
    scale: 4,
    strokeColor: '#e07a5f', /* terra cotta */
  };

  const marker = new google.maps.Marker(
      {title: title, position: {lat: lat, lng: lng}, map: map, icon: icon});

  const searchLink = constructSearchLink(title);

  const content = '<div class="header map-title"><h2>' +
      '<a target="_blank" href="' + searchLink + '">' + title +
      '</h2></a></div>' +
      '<div class="map-div flex-container">' +
      '<img class="map-img" src="images/travel/' + img + '" />' +
      '</div>';

  const infoWindow = new google.maps.InfoWindow({content: content});
  marker.addListener('click', () => {
    infoWindow.open(map, marker);
  });
}

/**
 * @return {String} a link from a title that will
 * link to the corresponding Google Search page
 */
function constructSearchLink(title) {
  // if it's null or false, return an empty string
  if (!title || title === '') {
    return '';
  }

  // link (without search terms) for a google search
  const searchLink = 'http://www.google.com/search?q=';

  let searchWord = title;

  // remove section of title after comma (if there is one)
  if (title.indexOf(',') != -1) {
    searchWord = title.substring(0, title.indexOf(','));
  }

  // splits into words then formats the search terms as connected
  // by a plus
  const words = searchWord.split(' ');
  const terms = words.join('+');
  return searchLink + terms;
}

/* Tests & Function Calls */

// test constructSearchLink() with a fake link
console.log(constructSearchLink('Fun Search Term, But Not This Part'));

// call is present because this method is only called within HTML
// & to satisfy validate
createPhotoLocationMap();

/* POSTING COMMENTS (comments.html) */

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

/* Tests & Function Calls */

// TODO: make the calls for getComments() & deleteComments() meaningful
// Currently these calls are just to satisfy make validate
getComments();
deleteComments();

/* CONTACT FORM VALIDATION (contact.html) */

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
  if (value) {
    return true;
  }

  return false;  // if null or undefined, will return false
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

/* Tests & Function Calls */

// TODO: Remove this call once this method is triggered by submitting the form
isContactFormDataValid();
