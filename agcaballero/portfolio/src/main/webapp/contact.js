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

/* JS Functions for Contact Me Page
 * Features: contact form validation
 */

// CONTACT FORM VALIDATION

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
