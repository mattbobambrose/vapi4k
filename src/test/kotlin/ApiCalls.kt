import com.vapi4k.dsl.assistant.VapiApi.Companion.vapiApi

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

object ApiCalls {

  @JvmStatic
  fun main(args: Array<String>) {

//    val request = "{}".toJsonElement()
//    val assistant = assistant(request) {
//      firstMessage = "This is the first message"
//    }
//
//
    val api = vapiApi(System.getenv("VAPI_API_KEY"))

    val callResp =
      api.phone {
        call {

        }
      }

    val saveRep =
      api.save {
        call {}
      }

    val listResp = api.list()

    val delResp = api.delete("123-445-666")
//
//    api.create(assistant)
//
//    api.create {
//      assistant {
//
//      }
//    }
//
//    api.list(ASSISTANT)
//    api.delete(ASSISTANT, "123-445-666")
//
//
  }

}
