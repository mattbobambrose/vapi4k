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

const maxSize = 4000;
const trimSize = 500;
let isScrolling = true;
let liveTailTooltip = new bootstrap.Tooltip(document.querySelector('#live-tail-button'));

function setLiveTailTooltip(title) {
  liveTailTooltip.setContent({'.tooltip-inner': title});
}

function scrollToBottom() {
  const scrollingDiv = document.querySelector('#console-div');
  scrollingDiv.scroll({
    top: scrollingDiv.scrollHeight,
    left: 0,
    behavior: 'smooth'
  });

  const mainDiv = document.querySelector('#log-div');
  let lines = mainDiv.innerText.split('\n');
  const numLines = lines.length;
  if (numLines > maxSize) {
    // Remove the first trimSize lines
    lines.splice(0, trimSize);
    mainDiv.innerText = lines.join('\n');
    console.log(`Trimmed ${trimSize} lines`);
  }
}

function toggleScrolling() {
  const icon = document.querySelector('#live-tail-icon');
  if (isScrolling === false) {
    setLiveTailTooltip('Live tail');
    icon.classList.remove('bi-play-fill');
    icon.classList.add('bi-pause-fill');
    isScrolling = true;
    scrollToBottom();
  } else {
    setLiveTailTooltip('Paused. Click to resume live tail.');
    icon.classList.remove('bi-pause-fill');
    icon.classList.add('bi-play-fill');
    isScrolling = false;
  }
  //setupTooltips();
}

(() => {
  'use strict'

  document.body.addEventListener(
    'htmx:oobAfterSwap',
    function (event) {
      if (event.detail.target.id === `log-div`) {
        if (isScrolling) {
          scrollToBottom();
        }
      }
    }
  );
})()

function displayLogging() {
  document.querySelector('#console-div').classList.remove('hidden');
  document.querySelector('#live-tail-div').classList.remove('hidden');
  document.querySelector('#main-div').classList.add('hidden');
  document.querySelector('#sys-info-div').classList.add('hidden');

  if (isScrolling)
    scrollToBottom();
}

function displayAdmin() {
  document.querySelector('#main-div').classList.remove('hidden');
  document.querySelector('#sys-info-div').classList.add('hidden');
  document.querySelector('#console-div').classList.add('hidden');
  document.querySelector('#live-tail-div').classList.add('hidden');
}

function displaySysInfo() {
  document.querySelector('#sys-info-div').classList.remove('hidden');
  document.querySelector('#main-div').classList.add('hidden');
  document.querySelector('#console-div').classList.add('hidden');
  document.querySelector('#live-tail-div').classList.add('hidden');
}

function setupTooltips() {
  var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
  var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
    return new bootstrap.Tooltip(tooltipTriggerEl)
  })
}
