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

*No third-party libraries were harmed in the making of this.*

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

#### Fields
Entities have exposed, mutable fields rather than methods. \
This is a design choice:
1. Entity classes are data-stores, not high-security API.
2. Some entities have 30+ fields - adding methods for each would increase bloat.
3. Users are __supposed__ to edit entity objects in order to update/patch them.

#### Requesting Entities
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

The `Lazy` framework has an ancillary benefit: it has the smallest possible time-requirement.
```java 
final User user = api.getUser(012345678910L);
final Guild guild = api.getGuild(109876543210L);
for (int x = 0; x < 10000; x++) {
    // do something slow here, e.g. RegEx
    // both Guild + User are populating in the background
}
user.await();
guild.await();
// this has taken the smallest possible time for both
// Guild + User to be ready
```
This allows you to have minimal wait-times for multiple entities to be resolved without needing to use Java's complex `CompletableFuture`s.

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

## Data Acquisition

Almost all data must be acquired from Discord's API before being used.

Although Eris does not force developers to use a particular format or method, some are recommended for simplicity or safety.

### Lazy Acquisition
Most Discord entities are provided as `Lazy` objects. \
You can see [here](#entities) for an introduction to how these work.

When using a `Lazy` entity the data may be acquired using `lazy.await()` which will **block** the current thread until **all** data is received.
You may also wait for the data using any method that calls `await` internally, e.g. `lazy.successful()`.

> Note: be careful about using `lazy.await()` on objects that were not acquired directly from the API - these may have no completion goal and so will block indefinitely.

To check whether `Lazy` acquisition is successful, a `lazy.successful()` block method is provided. \
If there is an error it may be found and thrown/read using `lazy.error()`.

### Future Acquisition
Rather than using the provided `Lazy` API to read data in-situ, it is possible to use Java's `CompletableFuture` system instead.

These may be used from a `Lazy` object directly (e.g. `lazy.whenReady()...`) however this is not advised in pooled environments since it busy-waits on a background thread.

Alternatively, the data can be acquired from the API's `request` methods directly. Since Eris contains the Argo JSON library, the `InputStream` can be read and converted.
This is not advised for beginner users, since the data will have to be marshalled correctly.

## Handling Entities in Bulk

It will be necessary to handle data - and its corresponding entities - in bulk.
An example of this would be acquiring large chunks of the member list or message history. \
While Discord does limit these to X results per call (e.g. `100` for messages) this is still a large chunk of data to read into memory all at once.

> Reading 100 `Message`s into memory would use: \
> ~16 bytes for the Message object \
> ~8 bytes of primitive field data \
> ~72 bytes of addresses for message data (assuming these are already cached) \
> ++ String data from each message \
> = 12kb of memory, excluding message content.

Ideally, we do not want to be using 12kb+ of memory at a time.

Fortunately, bulk calls provide three ways of dealing with their data.

#### 1. Lazy List
If all 100 messages need to be in memory at once (e.g. sort/search/store/etc.) they can be acquired as a `LazyList`.

```java 
final LazyList<Message> messages = channel.getMessages()
    .limit(100).get();
messages.await();
messages...
```
This will use the most memory, but all messages will be available at once.

| Pros                                                | Cons                                                        |
|-----------------------------------------------------|-------------------------------------------------------------|
| Can act on all the messages.                        | All of the messages go into memory.                         |
| Able to iterate all the messages at once.           | Have to wait for all messages to arrive before reading one. |
| Can do other tasks while the messages are arriving. |                                                             |


#### 2. Background Consumer
If the messages do not need to be consumed immediately, they may be dealt with in the background using a consumer.

```java 
channel.getMessages().limit(100)
    .forEach(message -> System.out.println(message.content));
```
This will use the least memory: once an entity is assembled from JSON it is immediately consumed. \
After the consumer has finished - providing the user is not storing a strong reference - the entity object is destroyed. \
However, there is a short delay (nanoseconds) between messages since they are consumed while the data stream is still incoming.

| Pros                                                              | Cons                                              |
|-------------------------------------------------------------------|---------------------------------------------------|
| Low-memory: objects are discarded after the consumer is finished. | Can act on only one message at a time.            |
| Fast: consumers are run as the data is read.                      | Cannot look ahead/behind at other messages.       |
| Background: doesn't block the current thread.                     | Slow consumers will slow down the incoming data.  |
|                                                                   | The parser must wait for each consumer to finish. |


> Note: this is not a standard for-each.

#### 3. In-situ Iteration
The message helper object can be treated as an `Iterable` (like a list) and looped.

```java 
for (final Message message : channel.getMessages().limit(100)) {
    System.out.println(message.content);
}
```
This is the fastest and lowest-memory approach, but it blocks the current thread.

Messages are passed across a transferring queue and then iterated. This allows the messages to be read on the current thread.


| Pros                                                    | Cons                                        |
|---------------------------------------------------------|---------------------------------------------|
| Low-memory: objects are discarded after each iteration. | Cannot look ahead/behind at other messages. |
| Low-CPU: no heavy consumers or lambdas required.        |                                             |
| In-situ: entities are in the current method.            |                                             |
| Speedy: incoming entities are queued in the background. |                                             |


## Caching
Unlike other discord libraries, Eris operates only a very limited cache. \
Eris is designed to function in low-memory environments, so caching large amounts of potentially-unnecessary data would be inappropriate.

Some ID-based entities (Users, Guilds, Channels) are cached when requested.
This cache will be maintained only as long as the user keeps a reference to that entity somewhere.
This is to prevent having to wait to reacquire entities in multiple places at the same time. \
In order to use this cache longer-term, simply store the entity references somewhere within your own project to stop them being garbage-collected from the cache.

Every time a cached entity is requested, the cached version will be returned but a call will be sent to the Discord API for an updated copy. \
You do **not** need to `await` the updated copy before using the entity, however in some cases you may wish to if the cached copy is old.

Some API methods allow a user to provide their own copy of an entity. This copy may be different from the cached version. \
If the method returns the same type of entity it will always return the cached version (pending the result from Discord's API.) \
The user-provided entity may supplant the cached version and be returned if:
1. The cached version is marked outdated or incomplete.
2. The cached version has no strong references and due to be garbage-collected.
3. The cached version is implemented at a lower-level (e.g. raw `Channel` vs `Thread`.)

## Chain Responses
Discord's message component system encourages chaining multiple actions together. \
E.g. `command -> modal -> message -> button press -> result`

However, Discord's API is structured around regular listeners and callbacks. \
To avoid users needing to create 5+ temporary listeners for a single interaction chain,
the API provides a helper process for these "one-and-done" callbacks.

### Appropriate Settings
The one-and-done response system is not designed for regular or repeatable interactions like commands.
It is appropriate for interactions which are:
- Unique, such as a user's modal input.
- Require data from the previous step, e.g. a 5-page form.

It is not appropriate for interactions which are:
- Not identifiably unique, such as commands.
- Designed to be triggered more than once like a permanent button.

### Preparing the Response

Some interactive components have an `expectResult` method.
This tells the API you are intending to use the component for a one-and-done response.

Some components have a static `auto` creator method that will **pre-trigger** this.


#### Button Example
An example of responding to a button is below.
```java 
final Button button = Button.auto("My Button");
final Message message; // store for later
channel.send(message = new Message("Hello!", button);
button.await(50000); // wait for somebody to press the button
// a time-out is always appropriate
// if the user does not press the button this would hang indefinitely
    
if (button.cancelled()) return; // the user didn't press the button or the interaction expired
final Interaction press = button.result(); // this is the interaction event
press.respond(new Message("You pressed the button!").withFlag(MessageFlags.EPHEMERAL));
// this can be triggered only once

message.delete(); // get rid of the button so nobody else presses it!
```
> In this example a button-message is sent to a channel.
> The **first** time a user presses the button, the bot will respond.
> After the bot has responded, the button will be deleted.
> If nobody presses the button within the 50-second time window, the interaction will not respond.

#### Modal Example
This complex example shows creating a modal and processing its result.

```java 
bot.registerCommand(Command.slash("bean", "My bean command."), interaction -> {
    final Modal modal = Modal.auto("My Modal", new TextInput("text", "Write something!"));
    interaction.respond(modal);
    
    modal.await(30000);
    if (modal.cancelled()) return;
    final Interaction result = modal.result();
    final String text = result.data.getInputValue("text");
    final Button button = Button.auto("My Button");
    result.respond(new Message("You wrote: `" + text + "`", button).withFlag(MessageFlags.EPHEMERAL));
    
    button.await(50000);
    if (button.cancelled()) return;
    final Interaction press = button.result();
    press.respond(new Message("You pressed the button!").withFlag(MessageFlags.EPHEMERAL));
});
```

> In this example, a global command is registered.
> When the command is run, it sends a unique modal to the user asking for a text response.
> The interaction waits 30 seconds for the user to fill in the text.
> A message is sent to the user telling them what they wrote, with a button.
> If the user presses the button it will respond with a new message.

<img width="339" alt="image" src="https://user-images.githubusercontent.com/14147477/180742795-81b8abea-986d-4db2-9938-94414b2c9a85.png">




## Bypassing the API
Users are not forced into using the provided API methods or even the `Entity` data objects.

The API provides ways to send raw requests to Discord and read their data as an `InputStream`.

You may also unregister the payload-listener that creates wrapped events and interpret payloads directly from the gateway socket.


## Dependencies

- **Argo** by me, found [here](https://github.com/Moderocky/Argo).
