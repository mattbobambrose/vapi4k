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

## Configuring IntelliJ

### Install the EnvFile plugin

The [EnvFile](https://plugins.jetbrains.com/plugin/7861-envfile) plugin allows you to load environment variables from a
file when running your applications. It simplifies
local development with ```ngrok```.

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

[ngrok](https://ngrok.com/) is a tool that allows you to expose your local server to the
internet. It is very useful for developing vapi4k apps because it greatly decreases your iteration time.
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

#### Create a `secrets.env` file

<procedure title="Create secrets.env file">
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

The secrets.env file should contain values for these env vars:

* VAPI_PRIVATE_KEY
* VAPI_PUBLIC_KEY
* VAPI_PHONE_NUMBER_ID
* VAPI4K_BASE_URL

It should look something like this:

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
