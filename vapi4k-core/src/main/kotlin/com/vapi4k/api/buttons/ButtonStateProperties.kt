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

package com.vapi4k.api.buttons

interface ButtonStateProperties {
  var color: String
  var type: String
  var title: String
  var subtitle: String
  var icon: String
}


/*
"buttonConfig": {
                  "position": "bottom-left",
                  "offset": "40px",
                  "width": "50px",
                  "height": "50px",
                  "idle": {
                      "color": "rgb(93, 254, 202)",
                      "type": "pill",
                      "title": "Have a quick question?",
                      "subtitle": "Talk with our AI assistant",
                      "icon": "https://unpkg.com/lucide-static@0.321.0/icons/phone.svg"
                  },
                  "loading": {
                      "color": "rgb(93, 124, 202)",
                      "type": "pill",
                      "title": "Connecting...",
                      "subtitle": "Please wait",
                      "icon": "https://unpkg.com/lucide-static@0.321.0/icons/loader-2.svg"
                  },
                  "active": {
                      "color": "rgb(255, 0, 0)",
                      "type": "pill",
                      "title": "Call is in progress...",
                      "subtitle": "End the call.",
                      "icon": "https://unpkg.com/lucide-static@0.321.0/icons/phone-off.svg"
                  }
              }
 */
