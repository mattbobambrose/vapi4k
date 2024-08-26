const defaultHeaders = {
    Accept: "application/json"
}

async function fetchJson(fetchMethod, url, userHeaders, jsonBody) {
    try {
        var initArg = {
            method: "POST",
            headers: {...defaultHeaders, ...userHeaders},
        }

        // Add json body if one is present and it is a post request
        if (jsonBody && fetchMethod && fetchMethod.toUpperCase() === "POST") {
            initArg.body = JSON.stringify(jsonBody)
        }

        const response = await fetch(url, initArg);

        if (response.ok) {
            return await response.json()
        } else {
            console.error('Error:', Error(`HTTP error status: ${response.status}`));
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

function loadVapiButton(vapi4kUrl, serverSecret, vapiApiKey, method, postArgs) {
    fetchJson(method, vapi4kUrl, {"x-vapi-secret": serverSecret}, postArgs,)
        .then((response) => {
            (function (document, t) {
                var elem = document.createElement(t);
                elem.src = "https://cdn.jsdelivr.net/gh/VapiAI/html-script-tag@latest/dist/assets/index.js";
                elem.defer = true;
                elem.async = true;
                var scriptTag = document.getElementsByTagName(t)[0];
                scriptTag.parentNode.insertBefore(elem, scriptTag);
                elem.onload = function () {
                    const config = buildVapiConfig(response, vapiApiKey);
                    const vapi = window.vapiSDK.run(config);
                    logVapiEvents(vapi);
                };
            })(document, "script");
        });
}

function buildVapiConfig(response, vapiApiKey) {
    var vapiConfig = {
        apiKey: vapiApiKey
    }

    if (response.assistant) {
        vapiConfig.assistant = response.assistant
    } else if (response.squad) {
        vapiConfig.squad = response.squad
    } else if (response.assistantId) {
        vapiConfig.assistantId = response.assistantId
    } else if (response.squadId) {
        vapiConfig.squadId = response.squadId
    } else {
        console.error('Error:', Error(`Assistant, Squad, AssistantId, or SquadId not found in response`));
    }

    if (response.assistantOverrides) {
        vapiConfig.assistantOverrides = response.assistantOverrides
    }

    if (response.buttonConfig) {
        vapiConfig.config = response.buttonConfig
    }

    return vapiConfig
}

function logVapiEvents(vapi) {
    if (vapi) {
        vapi.on("call-start", () => {
            console.log("Call has started.");
        });

        vapi.on("call-end", () => {
            console.log("Call has ended.");
        });

        vapi.on("speech-start", () => {
            console.log("Assistant speech has started.");
        });

        vapi.on("speech-end", () => {
            console.log("Assistant speech has ended.");
            // vapi.say("Our time's up, goodbye!", true)
        });

        vapi.on("message", (message) => {
            console.log(message);
        });

        // vapi.on("volume-level", (volume) => {
        //   console.log(`Assistant volume level: ${volume}`);
        // });

        vapi.on("error", (e) => {
            console.error(e);
        });
    }
}
