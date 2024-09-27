# Getting Started

## Install IntelliJ

In order to develop Vapi4k applications, you need to have IntelliJ installed on your machine.
You can download the Community Edition from the [JetBrains Toolbox App](https://www.jetbrains.com/toolbox-app/).

## Create a vapi4k repository

<procedure title="Create a vapi4k repository ">
    <step>
        <p>Go to the [vapi4k-template](https://github.com/pambrose/vapi4k-template) page.</p>
    </step>
    <step>
        <p>Click on <shortcut>Use this template</shortcut> <shortcut>Create a new repository</shortcut> options.</p>
    </step>
    <step>
        <p>Give the new repository a name.</p>
    </step>
    <step>
        <p>Click on the <shortcut>Create repository</shortcut> option.</p>
    </step>
</procedure>

## Clone your vapi4k repository

You can clone your new repository within IntelliJ or from the command line:

<procedure title="Clone your new repository within IntelliJ">
    <step>
        <p>Go to your vapi4k repository page on Github.</p>
    </step>
    <step>
        <p>Click on the <shortcut>Code</shortcut> option.</p>
    </step>
    <step>
        <p>Copy either the <shortcut>HTTPS</shortcut> or <shortcut>SSH</shortcut> repository url. </p>
    </step>
    <step>
        <p>Open IntelliJ</p>
    </step>
    <step>
        <p>Click on the <shortcut>File</shortcut> <shortcut>New</shortcut> <shortcut>Project from Version Control...</shortcut> options</p>
    </step>
    <step>
        <p>Paste the repository url and choose the desired <shortcut>Directory</shortcut>.</p>
    </step>
</procedure>


<procedure title="Clone your new repository with git on the CLI">
    <step>
        <p>Go to your vapi4k repository and click on the <shortcut>Code</shortcut> option.</p>
    </step>
    <step>
        <p>Copy either the <shortcut>HTTPS</shortcut> or <shortcut>SSH</shortcut> repository url. </p>
    </step>
    <step>
        <p>Open a new Terminal window</p>
    </step>
    <step>
        <p>Go to the directory where you have your git repos and type: </p>
    </step>

```bash
git clone <repository_url>
```

</procedure>

## Configure IntelliJ

### Install the EnvFile plugin

The [EnvFile](https://plugins.jetbrains.com/plugin/7861-envfile) plugin allows you to load environment variables from a
file when running your applications. It simplifies
local development with `ngrok`.

<procedure title="Install the EnvFile plugin">
    <step>
        <p>Start IntelliJ</p>
    </step>
    <step>
        <p>Select the <shortcut>IntelliJ IDEA</shortcut> <shortcut>Settings...</shortcut> menu options.</p>
    </step>
    <step>
        <p>Click on the <shortcut>Plugins</shortcut> option. </p>
    </step>
    <step>
        <p>Click on the <shortcut>Marketplace</shortcut> option and type <b>EnvFile</b> in the search field. </p>
    </step>
    <step>
        <p>Enable the <shortcut>EnvFile</shortcut> option.</p>
    </step>
</procedure>

### Install ngrok

The [ngrok](https://ngrok.com/) proxy allows you to expose your local vapi4k ktor server to the internet.
It is very useful for developing vapi4k apps because it greatly decreases your iteration time.
It is also useful for watching the traffic between your app and the vapi.ai platform.
To install ngrok, go to the [ngrok download page](https://ngrok.com/download) and follow the instructions for
your operating system.

### Running ngrok

To run ngrok, open a terminal window and type:

```bash
ngrok http 8080
```

Copy the ngrok `Web Interface` url and paste it into your browser. You will see the ngrok dashboard and all messages
exchanged between your app and the vapi.ai platform.

Copy the ngrok `Forwarding` url into your copy buffer. You will need this url when you run your vapi4k app.

#### Create a file with env var values

The vapi4k server requires some environment variables to be defined to connect to the vapi.ai platform.

<procedure title="Create a secrets.env file">
    <step>
        <p>Start IntelliJ</p>
    </step>
    <step>
        <p>Open your vapi4k application.</p>
    </step>
    <step>
        <p>Ctrl-click on the <shortcut>/secrets</shortcut> folder.</p>
    </step>
    <step>
        <p>Choose the <shortcut>New</shortcut> <shortcut>File</shortcut></p>
    </step>
    <step>
        <p>Create a file named <b>secrets.env</b>.</p>
    </step>

The `secrets.env` file should contain values for these environment variables:

* VAPI_PRIVATE_KEY
* VAPI_PUBLIC_KEY
* VAPI_PHONE_NUMBER_ID
* VAPI4K_BASE_URL

The first three values are obtained from the vapi.ai platform. The last value is the ngrok `Forwarding` url.

The `secrets/secrets.env` should look something like this:

```bash
VAPI_PRIVATE_KEY=f3ff6277-8d9b-8873-eec7-743786e2aa42
VAPI_PUBLIC_KEY=l9lf6233-9s9f-9173-egc1-433786e2aa98
VAPI_PHONE_NUMBER_ID=8b151b80-5fff-4df9-ad67-993189409d4c
VAPI4K_BASE_URL=https://fdee-73-71-109-432.ngrok-free.app
```

> **Keeping secrets.env private**
>
> Make sure `secrets.env` is not added to your repository. The `secrets` folder is already in the .gitignore file.
>
>
{style="warning"}

</procedure>

### Configure vapi.ai

<procedure title="Create a vapi.ai account">
    <step>
        <p>Create a <a href = "https://dashboard.vapi.ai">vapi account</a>.</p>
    </step>
    <step>
        <p>Go to the <a href = "https://dashboard.vapi.ai">vapi dashboard</a>.</p>
    </step>
    <step>
        <p>Click on <shortcut>Platform</shortcut> <shortcut>Phone Numbers</shortcut> and then
            <shortcut>Buy Number</shortcut> to get a phone number.</p>
    </step>
    <step>
        <p>Click on the green organization button in the lower left.</p>
    </step>
    <step>
        <p>Click on the <shortcut>Settings</shortcut> option and assign the <shortcut>Server URL</shortcut>.</p>
    </step>

The `Server URL` is a combination of the `VAPI4K_BASE_URL` value and an `/inboundCall/serverPath` value.

The `/inboundCall` indicates that the url corresponds to a vapi4k `inboundCallApplication{}` declaration.

The `serverPath` value is defined in the `inboundCallApplication{}` declaration and
defaults to `/vapi4k`.

The `Sever URL` should look something like this:

```bash
https://c7dc-2601-644-8722-6250-a138-5443-c3c5-eb1d.ngrok-free.app/inboundCall/vapi4k
```

</procedure>

### Run your vapi4k-template app

<procedure title="Run your app">
    <step>
        <p>Open <b>src/main/kotlin/com/myapp/Application.kt</b>.</p>
    </step>
    <step>
        <p>Click on the green arrow to the left of <code>fun main()</code> to run the server.</p>
    </step>
    <step>
        <p>Now run the server with the secrets.env by clicking on the 3 vertical dots in the Run
          panel and click on <shortcut>Modify Run Configuration...</shortcut>.</p>
    </step>
    <step>
        <p>Check the <shortcut>Enable Env File</shortcut> option and assign the <shortcut>Server URL</shortcut>.</p>
    </step>
    <step>
        <p>Scroll down and click on the <shortcut>➕</shortcut> button, then click on <shortcut>.env file</shortcut>
          and then select the <shortcut>secrets.env</shortcut> created above.</p>
    </step>
    <step>
        <p>Restart the server and you should see the log saying the <shortcut>Vapi4kServer is started at</shortcut>
          with the ngrok VAPI4K_BASE_URL value.</p>
    </step>
    <step>
        <p>Click on the VAPI4K_BASE_URL value to open the vapi4k admin page.</p>
    </step>
</procedure>

You should now be able to make a call to your vapi.ai phone number and see the call in the admin log.
