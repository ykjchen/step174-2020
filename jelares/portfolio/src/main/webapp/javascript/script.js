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
 * Changes the text of an element out of random choices
 */
function addRandomText(textChoices, elementId) {
  // console.log("In addRandomText");

  // Pick a random text.
  const textChoice =
      textChoices[Math.floor(Math.random() * textChoices.length)];

  // Add it to the page.
  const textContainer1 = document.getElementById(elementId + '1');
  const textContainer2 = document.getElementById(elementId + '2');

  if (textContainer1.style.opacity === '0' &&
      textContainer2.style.opacity === '0') {
    textContainer1.innerText = textChoice;
    textContainer2.style.opacity = 0;
    textContainer1.style.opacity = 1;

  } else if (textContainer1.style.opacity === '1') {
    textContainer2.innerText = textChoice;
    textContainer1.style.opacity = 0;
    textContainer2.style.opacity = 1;

  } else {
    textContainer1.innerText = textChoice;
    textContainer2.style.opacity = 0;
    textContainer1.style.opacity = 1;
  }
}

// Random Greeting
function addRandomGreeting() {
  addRandomText(
      [
        'Hola', 'Bonjour', 'Hello', 'Hej', 'Hallo', 'Greetings', 'Hey',
        'Welcome'
      ],
      'greeting-container');
}

// Random photo subjects
function addRandomPhoto() {
  addRandomText(
      [
        'mountains', 'nature', 'the sea', 'my pets', 'cities', 'people',
        'the world', 'kindness', 'the crazy', 'the new', 'the unbelievable'
      ],
      'photo-subject-container');
}

// Random photo subjects
function addRandomProject() {
  addRandomText(
      ['us', 'me', 'you', 'humanity', 'friends', 'family', 'fun', 'learning'],
      'project-subject-container');
}

/**
 * Sticky Nav Function
 */
function stickyNav() {
  // Get the navbar
  let navbar = document.getElementById('navbar');

  // Get the offset position of the navbar
  let sticky = navbar.offsetTop;

  // Add the sticky class to the navbar when you reach its scroll position.
  // Remove "sticky" when you leave the scroll position
  if (window.pageYOffset >= sticky) {
    navbar.classList.add('sticky');
  } else {
    navbar.classList.remove('sticky');
  }
}

/**
 * Sticky Nav Function
 */
function disappearingNav() {
  // Get the navbar
  let navbar = document.getElementById('navbar');

  // Get the offset position of the navbar
  let toDisappear = navbar.offsetTop;

  // Add the sticky class to the navbar when you reach its scroll position.
  // Remove "sticky" when you leave the scroll position
  if (window.pageYOffset >= toDisappear) {
    navbar.classList.add('sticky');
  } else {
    navbar.classList.remove('sticky');
  }
}

/**
 * Parallax elements scrolling function
 */
function parallaxElements() {
  const targets = document.querySelectorAll('.parallax-landing');

  for (let index = 0; index < targets.length; index++) {
    let targetOffsetMiddle =
        targets[index].offsetTop + Math.floor(targets[index].clientHeight / 2);
    let windowOffsetMiddle =
        window.pageYOffset + Math.floor(window.innerHeight / 2);

    if (windowOffsetMiddle >= targetOffsetMiddle) {
      let pos = (windowOffsetMiddle - targetOffsetMiddle) *
          targets[index].dataset.rate;

      if (targets[index].dataset.direction === 'vertical') {
        targets[index].style.transform = 'translate3d(0px, ' + pos + 'px, 0px)';
      } else {
        targets[index].style.transform = 'translate3d(' + pos + 'px, 0px, 0px)';
      }

    } else {
      targets[index].style.transform = 'translate3d(0px, 0px, 0px)';
    }
  }
}

/**
 * Logic for lightbox gallery
 */
$(document).ready(function() {
  let height = $('#gallery').height();
  $('#gallery').height(height);
  let gHeightToWidth = $('#gallery').height() / $('#gallery').width();

  $(function() {
    let selectedClass = '';

    $('.filter').click(function() {
      selectedClass = $(this).attr('data-rel');
      $('#gallery').fadeTo(100, 0.1);
      $('#gallery div')
          .not('.' + selectedClass)
          .fadeOut()
          .removeClass('animation');

      setTimeout(function() {
        $('.' + selectedClass).fadeIn().addClass('animation');
        $('#gallery').fadeTo(300, 1);
      }, 300);
    });
  });

  $(window).resize(function() {
    $('#gallery').height(gHeightToWidth * $('#gallery').width());
  });
});


/**
 * Logic for interval functions
 */
// To-add

/**
 * Logic for landing page parallax words effect + sticky navbar : scrolling
 * logic
 */
window.addEventListener('scroll', function(e) {
  // stickyNav();
  parallaxElements();
});
