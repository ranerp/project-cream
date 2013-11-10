package models;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import play.Logger;
import play.Play;
import play.libs.Akka;
import play.libs.F;
import play.mvc.WebSocket;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import static java.util.concurrent.TimeUnit.*;

import static akka.pattern.Patterns.ask;

public class ImageReceiver extends UntypedActor {

    private static ActorRef receiver = Akka.system().actorOf(Props.create(ImageReceiver.class));

    private static String basePath = Play.application().path().getPath();
    private static String folderPath = basePath + "\\public\\images\\";

    public static void start(WebSocket.In<byte[]> in, WebSocket.Out<byte[]> out) throws Exception {

        final String uniqueID = UUID.randomUUID().toString();
        String result = (String) Await.result(ask(receiver, new Start(uniqueID, out), 1000), Duration.create(1, SECONDS));

        if ("OK".equals(result)) {
            in.onMessage(new F.Callback<byte[]>() {
                @Override
                public void invoke(byte[] payload) throws Throwable {
                    receiver.tell(new Send(uniqueID, payload), null);
                }
            });

            in.onClose(new F.Callback0() {
                @Override
                public void invoke() throws Throwable {
                    receiver.tell(new Quit(uniqueID), null);
                }
            });
        } else {
            String error = "Could not connect";
            out.write(error.getBytes());
        }
    }
    Map<String, BufferedOutputStream> senders = new HashMap<>();

    public void onReceive(Object message) throws Exception {
        if (message instanceof Start) {
            Start start = (Start)message;

            File file = new File(start.imagePath);
            if(!file.exists())
                file.createNewFile();


            final int BUFFER_SIZE = 255 / 2 * 1024;
            FileOutputStream fileOutput = new FileOutputStream(file);
            BufferedOutputStream bufOutput = new BufferedOutputStream(fileOutput, BUFFER_SIZE);
            senders.put(start.id, bufOutput);
            getSender().tell("OK", getSelf());

        } else if (message instanceof Send) {
            Send send = (Send)message;

            write(send.id, send.payload);
        } else if (message instanceof Quit) {
            Quit quit = (Quit)message;
            senders.get(quit.id).close();
            senders.remove(quit.id);
        }
    }

    public void write(String id, byte[] payload) {
        try {
            senders.get(id).write(payload, 0, payload.length);
        } catch (IOException e) {
            Logger.debug(e.toString());
        }
    }

    public static class Start {
        final String id;
        final String imagePath;
        final WebSocket.Out<byte[]> channel;

        public Start(String id, WebSocket.Out<byte[]> channel) {
            this.id = id;
            this.imagePath = folderPath + id + ".jpg";
            this.channel = channel;
        }
    }

    public static class Send {
        final String id;
        final byte[] payload;

        public Send(String id, byte[] payload) {
            this.id = id;
            this.payload = payload;
        }
    }

    public static class Quit {
        final String id;

        public Quit(String id) {
            this.id = id;
        }
    }
}
