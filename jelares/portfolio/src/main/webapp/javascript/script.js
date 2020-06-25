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
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings =
      ['Hello!', "Bonjour!", "Hola!"];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
} 

/**
 * Sticky Nav Function
 */
function stickyNav() {

    // Get the navbar
    let navbar = document.getElementById("navbar");

    // Get the offset position of the navbar
    let sticky = navbar.offsetTop;

    // Add the sticky class to the navbar when you reach its scroll position. Remove "sticky" when you leave the scroll position
    if (window.pageYOffset >= sticky) {
        navbar.classList.add("sticky")
    } else {
        navbar.classList.remove("sticky");
    }
}

/**
 * Sticky Nav Function
 */
function stickyNav() {

    // Get the navbar
    let navbar = document.getElementById("navbar");

    // Get the offset position of the navbar
    let sticky = navbar.offsetTop;

    // Add the sticky class to the navbar when you reach its scroll position. Remove "sticky" when you leave the scroll position
    if (window.pageYOffset >= sticky) {
        navbar.classList.add("sticky")
    } else {
        navbar.classList.remove("sticky");
    }
}

/**
 * Parallax elements scrolling function
*/
function parallaxElements() {
    const targets = document.querySelectorAll('.parallax-landing');

    for(let index = 0; index < targets.length; index++) {

        let targetOffsetMiddle = targets[index].offsetTop + Math.floor(targets[index].clientHeight/2);
        let windowOffsetMiddle = window.pageYOffset + Math.floor(window.innerHeight/2);

        if(windowOffsetMiddle >= targetOffsetMiddle) {

            let pos = (windowOffsetMiddle - targetOffsetMiddle) * targets[index].dataset.rate;

            if(targets[index].dataset.direction === 'vertical') {
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
 * Logic for landing page parallax words effect + sticky navbar : scrolling logic
 */
window.addEventListener('scroll', function(e) {

    // stickyNav();
    parallaxElements();

});
