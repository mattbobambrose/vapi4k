# Deployment Options

## ngrok

Using [`ngrok`](https://ngrok.com/) for local development makes it easy to test your application.
Instructions for using `ngrok` are in the [Getting Started](GettingStarted.md) guide.

## Heroku

[Heroku](https://www.heroku.com) is a cloud platform where you can deploy your vapi4k applications.

Install the Heroku CLI with these [instructions](https://devcenter.heroku.com/articles/heroku-cli).

<procedure title="Create a Heroku Application">
    <step>
        <p>Open a terminal window and go to the root directory of your vapi4k application. </p>
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
>
{style="warning"}

> JDK Version
>
> The `java.runtime.version` value in the `system.properties` file must match the `jvmToolchain` value in the
> `build.gradle.kts` file.
>
{style="note"}

## Dockerfile and Digital Ocean


