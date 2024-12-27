# HistoryX
## Contents
1. [Setup](https://github.com/willrees23/historyx-api#setup)
2. [How to use](https://github.com/willrees23/historyx-api#how-to-use)

## Setup
**You can see a full plugin example here: https://github.com/willrees23/historyx-api-example**

### Adding as a dependency
HistoryX's API is distributed through GitHub packages. You can see all packages on [this page](https://github.com/willrees23/historyx-api/packages/).

You will need to use Maven or Gradle to add the API as a dependency.

Adding a repository is not needed, and make sure that `<scope>` is set to `provided`!

``` xml
<dependency>
    <groupId>dev.wand.HistoryX</groupId>
    <artifactId>api</artifactId>
    <version>{version}</version>
    <scope>provided</scope>
</dependency>
```
### Editing the plugin.yml
You need to make sure that your plugin depends on HistoryX.

You can do this by adding `depends: [ HistoryX ]` to your plugin.yml.

Here's a full example:
```yaml
name: HistoryXAPI-Example
version: '1.0.0'
main: dev.wand.historyXAPIExample.HistoryXAPIExample
api-version: '1.13'
depends: [ HistoryX ]
```

Now you're ready to start using the API!

## How to use
### Creating your extender
