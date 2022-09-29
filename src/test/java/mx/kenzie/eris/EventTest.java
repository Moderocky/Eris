package mx.kenzie.eris;

import mx.kenzie.eris.api.Event;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class EventTest {
    
    @Test
    public void test() throws Throwable {
        final Class<?>[] classes = EventTest.classes();
        for (final Class<?> type : classes) {
            assert type.getEnclosingClass() != type || Event.class.isAssignableFrom(type) : "Non-event class in event package.";
            if (!Event.class.isAssignableFrom(type)) continue;
            assert Bot.EVENT_LIST.containsValue(type) : "Unregistered event " + type.getSimpleName();
        }
    }
    
    private static Class<?>[] classes() throws IOException {
        final Set<Class<?>> classes = new LinkedHashSet<>();
        try (final JarFile jarFile = new JarFile(new File("target/eris-1.0.0.jar"))) {
            final Enumeration<JarEntry> thing = jarFile.entries();
            while (thing.hasMoreElements()) {
                final JarEntry entry = thing.nextElement();
                if (entry.getName().endsWith(".class")) {
                    final String name = entry.getName()
                        .replace("/", ".")
                        .replace(".class", "");
                    if (!name.startsWith("mx.kenzie.eris.api.event")) continue;
                    try {
                        classes.add(Class.forName(name));
                    } catch (ClassNotFoundException ex) {
                        System.err.println("Unable to find '" + name + '\'');
                    }
                }
            }
            return classes.toArray(new Class[0]);
        }
    }
    
    @Test
    public void missing() {
        final String list = """
            Hello	Defines the heartbeat interval
            Ready	Contains the initial state information
            Resumed	Response to Resume
            Reconnect	Server is going away, client should reconnect to gateway and resume
            Invalid Session	Failure response to Identify or Resume or invalid active session
            Application Command Permissions Update	Application command permission was updated
            Auto Moderation Rule Create	Auto Moderation rule was created
            Auto Moderation Rule Update	Auto Moderation rule was updated
            Auto Moderation Rule Delete	Auto Moderation rule was deleted
            Auto Moderation Action Execution	Auto Moderation rule was triggered and an action was executed (e.g. a message was blocked)
            Channel Create	New guild channel created
            Channel Update	Channel was updated
            Channel Delete	Channel was deleted
            Channel Pins Update	Message was pinned or unpinned
            Thread Create	Thread created, also sent when being added to a private thread
            Thread Update	Thread was updated
            Thread Delete	Thread was deleted
            Thread List Sync	Sent when gaining access to a channel, contains all active threads in that channel
            Thread Member Update	Thread member for the current user was updated
            Thread Members Update	Some user(s) were added to or removed from a thread
            Guild Create	Lazy-load for unavailable guild, guild became available, or user joined a new guild
            Guild Update	Guild was updated
            Guild Delete	Guild became unavailable, or user left/was removed from a guild
            Guild Ban Add	User was banned from a guild
            Guild Ban Remove	User was unbanned from a guild
            Guild Emojis Update	Guild emojis were updated
            Guild Stickers Update	Guild stickers were updated
            Guild Integrations Update	Guild integration was updated
            Guild Member Add	New user joined a guild
            Guild Member Remove	User was removed from a guild
            Guild Member Update	Guild member was updated
            Guild Members Chunk	Response to Request Guild Members
            Guild Role Create	Guild role was created
            Guild Role Update	Guild role was updated
            Guild Role Delete	Guild role was deleted
            Guild Scheduled Event Create	Guild scheduled event was created
            Guild Scheduled Event Update	Guild scheduled event was updated
            Guild Scheduled Event Delete	Guild scheduled event was deleted
            Guild Scheduled Event User Add	User subscribed to a guild scheduled event
            Guild Scheduled Event User Remove	User unsubscribed from a guild scheduled event
            Integration Create	Guild integration was created
            Integration Update	Guild integration was updated
            Integration Delete	Guild integration was deleted
            Interaction Create	User used an interaction, such as an Application Command
            Invite Create	Invite to a channel was created
            Invite Delete	Invite to a channel was deleted
            Message Create	Message was created
            Message Update	Message was edited
            Message Delete	Message was deleted
            Message Delete Bulk	Multiple messages were deleted at once
            Message Reaction Add	User reacted to a message
            Message Reaction Remove	User removed a reaction from a message
            Message Reaction Remove All	All reactions were explicitly removed from a message
            Message Reaction Remove Emoji	All reactions for a given emoji were explicitly removed from a message
            Presence Update	User was updated
            Stage Instance Create	Stage instance was created
            Stage Instance Update	Stage instance was updated
            Stage Instance Delete	Stage instance was deleted or closed
            Typing Start	User started typing in a channel
            User Update	Properties about the user changed
            Voice State Update	Someone joined, left, or moved a voice channel
            Voice Server Update	Guild's voice server was updated
            Webhooks Update	Guild channel webhook was created, update, or deleted
            """;
        final String[] lines = list.split("\n");
        final List<String> missing = new ArrayList<>();
        for (final String line : lines) {
            final String tag = line.split("\t")[0].trim().toUpperCase().replace(" ", "_");
            if (tag.isEmpty()) continue;
//            System.out.println("| " + line.split("\t")[0].trim() + " | " + Bot.EVENT_LIST.get(tag).getSimpleName() + " |");
            // For documentation :)
            if (!Bot.EVENT_LIST.containsKey(tag)) missing.add(tag);
        }
        assert missing.size() == 0 : "Event handlers missing for " + missing;
    }
    
}
