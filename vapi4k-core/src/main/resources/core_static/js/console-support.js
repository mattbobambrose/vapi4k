/*
 * Copyright © 2024 Matthew Ambrose (mattbobambrose@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *
 */

var isScrolling = true;
const maxSize = 4000;
const trimSize = 500;

function scrollToBottom() {
  const scrollingDiv = document.querySelector('#scrolling-div');
  scrollingDiv.scroll({
    top: scrollingDiv.scrollHeight,
    left: 0,
    behavior: 'smooth'
  });

  const mainDiv = document.querySelector('#main-div');
  let lines = mainDiv.innerText.split('\n');
  const numLines = lines.length;
  if (numLines > maxSize) {
    // Remove the first trimSize lines
    lines.splice(0, trimSize);
    mainDiv.innerText = lines.join('\n');
    console.log(`Trimmed ${trimSize} lines`);
  }

  // console.log(`Before length = ${numLines}`);
}

function toggleScrolling() {
  const element = document.querySelector('#live-tail-button');
  if (element.innerHTML === "Paused") {
    element.innerHTML = "Live Tail";
    isScrolling = true;
    scrollToBottom();
  } else {
    element.innerHTML = "Paused";
    isScrolling = false;
  }
}

document.body.addEventListener(
  'htmx:oobAfterSwap',
  function (event) {
    if (event.detail.target.id === `main-div`) {
      if (isScrolling) {
        scrollToBottom();
      }
    }
  }
);
