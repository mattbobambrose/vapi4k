# Deployment Options

## ngrok

Using [`ngrok`](https://ngrok.com/) for local development makes it easy to test your application.
Instructions for using `ngrok` are in the [Setup](Setup.md) guide.

## Heroku

[Heroku](https://www.heroku.com) is a cloud platform where you can deploy your Vapi4k applications.

Install the Heroku CLI with these [instructions](https://devcenter.heroku.com/articles/heroku-cli).

<procedure title="Create a Heroku Application">
    <step>
        <p>Open a terminal window and go to the root directory of your Vapi4k application. </p>
    </step>
    <step>
        <p>Log in to Heroku with the command <code>heroku login</code>. </p>
    </step>
    <step>
        <p>Create a new Heroku app with the command <code>heroku create 'optional_name'</code>. </p>
    </step>
    <step>
        <p>Push your application to Heroku with the command <code>git push heroku main</code>. </p>
    </step>
    <step>
        <p>Open your application with the command <code>heroku open</code>. </p>
    </step>
</procedure>

> **IP address mismatch** when trying to login to Heroku
>
> If you get an **IP address mismatch** error when trying to login to Heroku from OSX, try
> disabling the iCloud Private Relay option in `System Settings`➡️`Apple Account`️️➡️`iCloud+ Features`.
> You might have to restart your browser after making this change.
>
{style="warning"}

> JDK Version
>
> The `java.runtime.version` value in the `system.properties` file must match the `jvmToolchain` value in the
> `build.gradle.kts` file.
>
{style="note"}

## Dockerfile

You can deploy your Vapi4k application using a Docker container.
The `vapi4k-template` project includes a `Dockerfile` that you can use to build a Docker image.

Install and run Docker Desktop following these [instructions](https://www.docker.com/products/docker-desktop/).

Create a Docker account [here](https://app.docker.com/signup).

Assign your docker username and image name to the `IMAGE_NAME` variable in the `Dockerfile`.

The default jar filename is `vapi4k-template.jar` but you can change it to whatever name you like.
The name has to be changed in the `Dockerfile` and `build.gradle.kts` file.

Create a Docker image with the following command:

```bash
make build-docker
```

Push the Docker image with the following command:

```bash
make push-docker
```

## Digital Ocean

[Digital Ocean](https://www.digitalocean.com)  makes it easy to deploy a Docker container
with their [App Platform](https://www.digitalocean.com/products/app-platform) product.

