# Introduction

Eris is built on two concepts: the **data-holding object** and the **lazy receiver**. \
Understanding both is important for using the API.

## Data-Holding Objects

Types in the `mx.kenzie.eris.api` and `mx.kenzie.eris.data` packages are typically data-holders.
These can be identified as they are subclasses of `mx.kenzie.eris.data.Payload`.

The `Payload` class is designed for objects that reflect a certain JSON schema.
Their fields are usually in `lower_snake_case` and public (or at least protected),
although some may also have helper access methods.

These classes are directly mapped to JSON data when sending or receiving from Discord's API.

A simple type such as:

```java
import mx.kenzie.eris.data.Payload;

class Thing extends Payload {

    public String name = "hello";
    public int number_of_spoons = 3;

}
```

will be directly mapped to its JSON equivalent:

```json
{
  "name": "hello",
  "number_of_spoons": 3
}
```

This works in both directions: objects dispatched to Discord (e.g. a Member object for editing a guild member)
are serialised into their JSON equivalent, and the response from Discord can be mapped onto a new or existing Payload
subtype.

It is important to note that changes to these objects (e.g. `name = "foo"`) are not
reflected on the server-side, but the objects can be fed to API methods (like `updateMember`).

## Lazy Receivers

Requests and responses are dealt with _in situ_ by design, rather than via lambdas or call-backs.

```jshelllanguage
final Message greeting = new Message("hello!");
user.sendMessage(greeting); 
// the reply is dispatched here
// your code continues as normal

greeting.await(); 
// this line will hold until the Discord API responds
```

When the `await` is called, the current thread is paused and un-mounted while it waits for a result, 
so the program may continue with other tasks in the background.

Once the `await` resolves (either successfully or with a failure response) your method will be notified and continue automatically.

The success/failure status can be tested after this point.

### Infinite Wait

Occasionally, an `await` method may hang indefinitely.
This can occur if:
1. Discord acknowledges our connection but then endlessly tries to transmit data, meaning the connection is never considered 'resolved'. This should never normally occur.
2. You are waiting for an object without dispatching it. \
This typically happens due to user error, such as by creating a `new Message` and then `await`ing it without having sent it first.
Obviously, Discord will never respond since no request was made.
3. You are waiting for an object that was not the direct result of a request. \
Many responses from the Discord API contain sub-data, e.g. a `Message` having a `User` author field.
In this case the `User` is sub-data and not the subject of its own request, so `await`ing it may block indefinitely.

To avoid these, it is advised to:
- Check if the needed data is available before waiting, since it may be present from a previous request.
- Re-request data (e.g. a `User` record) if the origin of an existing one is unclear.
- Add a time-out to `await` calls, and then check the cancelled/success state.
