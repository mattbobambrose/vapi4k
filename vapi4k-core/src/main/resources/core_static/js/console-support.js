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

function scrollToBottom() {
  const element = document.querySelector('#scrolling-div');
  // element.scrollTop = element.scrollHeight;
  element.scroll({
    top: element.scrollHeight,
    left: 0,
    behavior: 'smooth'
  });
}

document.body.addEventListener(
  'htmx:oobAfterSwap',
  function (event) {
    if (event.detail.target.id === `main-div`) {
      // console.log('calling scrolling to bottom');
      scrollToBottom();
    }
  }
);
