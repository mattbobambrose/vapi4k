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

package utils

import com.vapi4k.api.json.get
import com.vapi4k.api.json.keys
import com.vapi4k.api.json.stringValue
import com.vapi4k.api.json.toJsonElement
import com.vapi4k.api.json.toJsonString

object JsonElements {
  @JvmStatic
  fun main(args: Array<String>) {
    jsonElementExample()
  }

  fun jsonElementExample() {
    val json = """
      {
        "person": {
          "first": "Bill",
          "last": "Lambert",
          "address": {
            "street": "123 Main",
            "city": "Tusla"
          }
        }
      }
    """

    // Convert the json string to a JsonElement
    val je = json.toJsonElement()

    println(je.keys) // [person]

    println(je["person"].keys) // [first, last, address]

    println(je["person.address"].keys) // [street, city]

    // Get the value of the "first" key using the stringValue extension property
    val first = je["person.first"].stringValue
    println(first) // Bill

    // Get the value of the "last" key using the stringValue extension function
    val last = je.stringValue("person.last")
    println(last) // Lambert

    // Get the value of the "address.street" key
    val street = je.stringValue("person.address.street")
    println(street) // 123 Main

    // Get the value of the "address.street" key using the get function and the vararg keys parameter
    val city = je["person", "address", "city"].stringValue
    println(city) // Tulsa

    println(je.toJsonString())
    /*
      Outputs:
      {
        "person": {
          "first": "Bill",
          "last": "Lambert",
          "address": {
            "street": "123 Main",
            "city": "Tusla"
          }
        }
      }
     */
  }
}
