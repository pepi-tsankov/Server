/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import Connection.MessageObject;
import org.J3dPackage.Point3d;
import org.game.World;

/**
 *
 * @author Userr
 */
public class GameServer {
    public static AtomicBoolean running=new AtomicBoolean(true);
    public static Vector<Socket> playerSockets =new Vector();
    public static Vector<Thread> playerThreads =new Vector();
    public static Vector<ObjectOutputStream> outputStreams=new Vector();
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Choose a port for the socket between 0 and 9999");
        int port=0;
        byte s[]={0,0,0,0,0,0,0,0,0,0,0};
        System.in.read(s);
        String b=new String(s);
        b=b.replace((char)10, '\0');
        b=b.trim();
        if(b.length()>4){System.err.println("FATAL ERROR: illegal size for socket, must be less than 9999");System.exit(1);}
        for(char by :b.toCharArray()){
            if((by>'9'||by<'0')&&by!=0&&by!=10){
                System.err.println("FATAL ERROR: found characters that are not between 0 and 9");
                System.exit(1);
            }else{
                if(by!=0&&by!=10){
                    port*=10;
                    port+=by-'0';
                }
            }
        }
        ServerSocket gameServerSocket=new ServerSocket(port);
        Thread t=new Thread(){
            @Override
            public void run(){
                while(running.get()){
                    byte b[]={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
                    try {
                        System.in.read(b);
                    } catch (IOException ex) {
                        Logger.getLogger(GameServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    String s=new String(b);
                    s=s.replace((char)10, '\0');
                    s=s.trim();
                    if(s.compareToIgnoreCase("exit".trim())==0){
                        running.set(false);
                        try {
                            gameServerSocket.close();
                        } catch (IOException ex) {
                            Logger.getLogger(GameServer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        };
        t.start();
        System.out.println("server started.");
        System.out.println("on port "+gameServerSocket.getLocalPort());
        while(running.get()){
            try{
                playerSockets.add(gameServerSocket.accept());
                System.out.println("client joined");
                playerThreads.add(new Thread(){
                   Socket s=playerSockets.lastElement();
                    ObjectInputStream in;
                    ObjectOutputStream out;
                   @Override
                   public void run(){
                       int message=0;
                       try {
                           out=new ObjectOutputStream(s.getOutputStream());
                           out.flush();
                           in=new ObjectInputStream(s.getInputStream());
                           outputStreams.add(out);
                       } catch (IOException ex) {
                           Logger.getLogger(GameServer.class.getName()).log(Level.SEVERE, null, ex);
                       }
                       while(running.get()){
                           if(s.isClosed()) return;
                           MessageObject ob=null;
                               Vector<Object> objects=new Vector();
                               Vector<Class> classes=new Vector();
                               Vector<String> sideMessages=new Vector();
                            try {
                                System.out.println("waiting for message");
                                ob=(MessageObject)in.readObject();
                                System.out.println("recieved message");
                            } catch (Exception ex) {ex.printStackTrace();}
                            if(ob!=null){
                                for(int i=0;i<ob.sideMessage.size();i++){
                                    if(ob.sideMessage.get(i).equals("getChunk")){
                                        if(World.isChunkLoaded((Point3d)ob.object_.get(i))){
                                            objects.add(World.getchunk((Point3d)ob.object_.get(i)));
                                            sideMessages.add("chunk");
                                        }else{
                                            World.CreateChunk((Point3d)ob.object_.get(i));
                                            objects.add(World.getchunk((Point3d)ob.object_.get(i)));
                                            sideMessages.add("chunk");
                                        }
                                    }
                                }
                                if(!sideMessages.isEmpty()){
                                    System.out.println("send a message"+(message++));
                                    MessageObject mo=new MessageObject(null,null,null);
                                    mo.class_=classes;
                                    mo.object_=objects;
                                    mo.sideMessage=sideMessages;
                                    try {
                                        out.writeObject(mo);
                                        out.flush();
                                    } catch (IOException ex) {
                                        Logger.getLogger(GameServer.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            }
                       }
                   }
                });
                playerThreads.lastElement().start();
            }catch (SocketException ex){}
        }
        gameServerSocket.close();
        System.out.println("stopped server.");
    }
    public static void notifyAllPlayers(MessageObject o) throws IOException{
        for (ObjectOutputStream out : outputStreams) {
            out.writeObject(o);
            out.flush();
        }
    }
}
