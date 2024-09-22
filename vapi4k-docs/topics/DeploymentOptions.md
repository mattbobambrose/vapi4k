# Deployment Options

## ngrok

Using [`ngrok`]() for local development makes it easy to test your application.

1) Download and install `ngrok` from [ngrok.com](https://ngrok.com)
2) Run `ngrok http 8080` to expose your local server to the internet
3) Assign `VAPI4K_BASE_URL` to the ngrok `Forwarding` URL
4) Run your application

## Dockerfile and Digital Ocean

## Heroku

1) `brew tap heroku/brew && brew install heroku`
2) `heroku login`
3) `heroku create <optional_name>`
4) `git push heroku main`

"IP address mismatch" is a known issue when using iCloud Private Relay on a Mac and disabling it would resolve the
error.

system.properties must match the version in

