Eris
=====

### Opus #19

A library for interacting with the Discord API at a very low level.

### Description

Eris provides simple access to the Discord API without smothering data in layers of needless, slow wrappers. \
It also avoids forcing users into particular design choices (futures, lambdas.)

The library is designed to be:
1. Simple, so that a beginner can use it.
2. Non-restrictive, so that users are not forced to use it in a certain way.
3. Modifiable, so that users can access it at any level.
4. Small, so that users do not need to shade megabytes of needless third-party libraries.

## Maven Information
```xml
<repository>
    <id>kenzie</id>
    <name>Kenzie's Repository</name>
    <url>https://repo.kenzie.mx/releases</url>
</repository>
``` 

```xml
<dependency>
    <groupId>mx.kenzie</groupId>
    <artifactId>eris</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Design

The default design pattern has two features: listeners and entities. \
Both of these are optional, and advanced users can use a different design pattern that better suits their project.

### Listeners

Listeners can anticipate Discord **events** (dispatches.) \
Advanced users can listen to raw gateway **payloads** and handle the data manually.

```java 
final Bot bot = new Bot("token");

bot.registerListener(Ready.class, ready -> {
    System.out.println("The bot has successfully logged in!");
    System.out.println("It is called " + ready.user.getTag());
});
```

### Entities

Entities represent Discord objects (users, guilds, members, etc.)
The entity structure corresponds directly to its raw data.

Most entities have a `Snowflake` ID by which they can be retrieved.

```java 
final User user = api.getUser(196709350469795841L);
final User user = api.getUser("196709350469795841");
// Both ID formats are supported.
```

Entities are `Lazy`. \
Their data may not be immediately available after creation, but the object can still be used.

To ensure that a `Lazy` entity has finished acquiring its data, use the `entity.await()` method. \
This will **block** the current thread. An alternative `entity.<CompletableFuture>whenReady()` method is available.

```java 
final User user = api.getUser(196709350469795841L);
assert user.id != null; // This property is already available.
user.await(); // Wait for all data to be loaded.
assert user.name != null; // This property is now available.
```

```java 
final User pending = api.getUser(196709350469795841L);
pending.<User>whenReady().thenAccept(user -> System.out.println(user.username + " is ready!"));
```

This dual structure allows programs to use the most suitable design pattern.

### Updating Entities

It is safe to cache or store Discord entities. \
After retrieving a stored entity it should be updated before being used to make sure all data is accurate.

A Discord entity may need to be updated if:
1. You retrieved a partial entity (e.g. partial member list in a `Guild` request) and want more data.
2. The entity was changed by Discord (e.g. a user changes their name.)
3. You made an entity object yourself (e.g. loaded cached `Guild` data from a file.)

If you ask Discord to `PATCH` (change) an entity it will be updated automatically on completion.

### Creating Entities

For beginners, it is better practice to ask the `DiscordAPI` to do it for you. \
This API object can be obtained from your `Bot` instance.

For advanced users it is safe to manually create most `Entity` objects manually, however you will need to request their data from Discord. \
Before requesting the data you **must** set its snowflake `id` field.

Please note that any helper methods on the entity (e.g. `channel.send(...)`) will not function until you update the entity or attach a `DiscordAPI` object.

### Generic Entities

Many utility methods will accept a generic `I` entity, e.g. `api.getBan(IGuild, IUser)`.

These generic `I` entities have two special rules:
1. They can accept an object relating to the ID of the entity rather than the entity itself. \
    E.g. you can provide a guild's ID in String/long form instead of a Guild object.
2. If you use an object, it **does not** need to be finished, since only the ID is required. \
    E.g. you can provide the product of `api.getUser(...)` without `await`ing it first.

These objects are not strictly checked outside a test environment.
Most methods will specify which types are permitted.

## Dependencies

- **Argo** by me, found [here](https://github.com/Moderocky/Argo).
