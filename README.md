# fStats API
<a title="Fabric Language Kotlin" href="https://minecraft.curseforge.com/projects/fabric-language-kotlin" target="_blank" rel="noopener noreferrer"><img style="display: block; margin-left: auto; margin-right: auto;" src="https://i.imgur.com/c1DH9VL.png" alt="" width="230" /></a>
<img src="https://i.imgur.com/iaETp3c.png" alt="" width="200" >
<img src="https://i.imgur.com/Ol1Tcf8.png" alt="" width="200" >

## Description
Cross-side library for metric collecting for developers

## Adding the dependency
```gradle
repositories {
    maven {
        url = "https://api.modrinth.com/maven"
    }
}

dependencies {
    // Option 1: Include fStats API to project for it available within your own jar IT'S ONLY ~9KB!
    include(modImplementation("maven.modrinth", "fstatsapi", "<version>"))
    
    // Option 2: Depend on fStats API, but require that users install it manually
    modImplementation("maven.modrinth", "fstatsapi", "<version>")
}
```
```json5
"depends": {
    "fabricloader": "*",
    ...
    //Also add dependency in your fabric.mod.json 
    "fstatsapi": "*"
},
```
## Usage
First think that you need todo is register on https://fstats.dev/, create project and get it projectId

### Init
```java
public static fstats = FStatsApi(projectId, "your_mod_id");
```

### Client side metric
```java
public class ClientEnvClass implements ClientModInitializer {
    ClientLifecycleEvents.CLIENT_STARTED.register(client ->
        fstats.sendClientData(client)
    );
}
```

### Server side metric
```java
public class GlobalOrServerEnvClass implements ModInitializer {
    ServerLifecycleEvents.SERVER_STARTED.register(server ->
        fstats.sendServerData(server)
    );
}
```

### Exception capturing
```java
try {
    //Something what can be broke 
} catch(Exception exception) {
    fstats.sendExceptionData(exception);
}
```
