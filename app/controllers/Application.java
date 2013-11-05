package controllers;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import play.*;
import play.libs.F;
import play.mvc.*;

import views.html.*;

import java.lang.reflect.Array;

public class Application extends Controller {

    public static WebSocket<byte[]> index() {

        return new WebSocket<byte[]>() {

            @Override
            public void onReady(WebSocket.In<byte[]> in, WebSocket.Out<byte[]> out) {
                in.onMessage(new F.Callback<byte[]>() {
                    @Override
                    public void invoke(byte[] s) throws Throwable {
                        System.out.println(s.toString());
                    }
                });

                in.onClose(new F.Callback0() {
                    @Override
                    public void invoke() throws Throwable {
                        System.out.println("Disconnected");
                    }
                });

                String str = "Hello android";
                try {
                    byte[] payload = Hex.decodeHex(str.toCharArray());
                    out.write(payload);
                } catch (DecoderException e) {
                    System.out.println(e.toString());
                }
            }
        };
    }

}
