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

package assistants

import com.vapi4k.api.assistant.Assistant
import com.vapi4k.api.voice.enums.AzureVoiceIdType
import com.vapi4k.api.voice.enums.CartesiaVoiceLanguageType
import com.vapi4k.api.voice.enums.CartesiaVoiceModelType
import com.vapi4k.api.voice.enums.DeepGramVoiceIdType
import com.vapi4k.api.voice.enums.ElevenLabsVoiceModelType
import com.vapi4k.api.voice.enums.LMNTVoiceIdType
import com.vapi4k.api.voice.enums.NeetsVoiceIdType
import com.vapi4k.api.voice.enums.OpenAIVoiceIdType
import com.vapi4k.api.voice.enums.PlayHTVoiceIdType
import com.vapi4k.api.voice.enums.RimeAIVoiceModelType

object Voices {
  fun Assistant.azureExample() {
    azureVoice {
      voiceIdType = AzureVoiceIdType.BRIAN
    }
  }

  fun Assistant.cartesiaExample() {
    cartesiaVoice {
      modelType = CartesiaVoiceModelType.SONIC_ENGLISH
      languageType = CartesiaVoiceLanguageType.ENGLISH
    }
  }

  fun Assistant.deepgramExample() {
    deepgramVoice {
      voiceIdType = DeepGramVoiceIdType.ASTERIA
    }
  }

  fun Assistant.elevenLabsExample() {
    elevenLabsVoice {
      modelType = ElevenLabsVoiceModelType.ELEVEN_TURBO_V2_5
    }
  }

  fun Assistant.lmntExample() {
    lmntVoice {
      voiceIdType = LMNTVoiceIdType.DANIEL
    }
  }

  fun Assistant.neetsExample() {
    neetsVoice {
      voiceIdType = NeetsVoiceIdType.VITS
    }
  }

  fun Assistant.openAIExample() {
    openAIVoice {
      voiceIdType = OpenAIVoiceIdType.ONYX
    }
  }

  fun Assistant.playHTExample() {
    playHTVoice {
      voiceIdType = PlayHTVoiceIdType.DONNA
    }
  }

  fun Assistant.rimeAIExample() {
    rimeAIVoice {
      modelType = RimeAIVoiceModelType.MIST
    }
  }
}
