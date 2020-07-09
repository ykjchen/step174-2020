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
 * Delete comments and re-get Data
 */
async function deleteCommentData() {
  await fetch(new Request('/delete-data', {method: 'post'}));
  await getCommentData;
  window.location.reload();
}

/** Creates an <li> element containing text. */
function createCommentList(inputText) {
  const listElement = document.createElement('li');
  listElement.innerText = inputText;
  return listElement;
}

google.charts.load('current', {'packages': ['corechart']});
google.charts.setOnLoadCallback(drawChart);

/** Fetches skill data and uses it to create a chart. */
function drawChart() {
  fetch('/endorse-data')
      .then(response => response.json())
      .then((skillVotes) => {
        const data = new google.visualization.DataTable();
        data.addColumn('string', 'Skill');
        data.addColumn('number', 'Votes');
        Object.keys(skillVotes).forEach((skill) => {
          data.addRow([skill, skillVotes[skill]]);
        });

        const options = {
          'title': 'Endorse a Skill!',
          'width': 600,
          'height': 500,
          'alignment': 'center'
        };

        const chart = new google.visualization.ColumnChart(
            document.getElementById('chart-container'));
        chart.draw(data, options);
      });
}

/**
 * Fetches comments for display.
 */
function getCommentData(commentsLimit = 20) {
  fetch('/data?comment-count=' + commentsLimit)  // sends a request to /data
      .then(response => response.json())         // parses the response as JSON
      .then((commentsPackage) => {  // now we can reference the fields as an
                                    // object!
        const commentsElement = document.getElementById('quote-container');
        commentsElement.innerHTML = '';
        for (var increment = 0; increment < commentsPackage.length;
             increment += 1) {
          console.log(commentsPackage);
          commentsElement.appendChild(
              createCommentList(commentsPackage[increment]));
        }
      });
}
