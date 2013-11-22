package controllers;

import models.ImageReceiver;
import models.S3File;
import play.*;
import play.db.ebean.Model;
import play.mvc.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import views.html.*;

public class Application extends Controller {


    public static Result index() {
        List<S3File> uploads = new Model.Finder<UUID, S3File>(UUID.class, S3File.class).all();
        List<Integer> countUploads = new ArrayList<Integer>();
        for(int i = 0; i < uploads.size(); i++)
            countUploads.add(i);

        return ok(index.render(uploads, countUploads));
    }

    public static WebSocket<byte[]> receiveImage() {
        return new WebSocket<byte[]>() {

            @Override
            public void onReady(WebSocket.In<byte[]> in, WebSocket.Out<byte[]> out) {
                try {
                    ImageReceiver.start(in, out);
                } catch (Exception e) {
                    Logger.debug(e.toString());
                }
            }
        };
    }

}
