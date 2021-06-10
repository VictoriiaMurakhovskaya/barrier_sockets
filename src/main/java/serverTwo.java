import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class ServerTwo {

    private static Socket clientSocket; // сокет для комунікації з клієнтом
    private static ServerSocket server; // серверсокет
    private static ObjectInputStream in; // потік читання з сокету
    private static ObjectOutputStream out; // потік запису в сокет

    static String Token;

    public static void main(String[] args) {
        try {
            try {
                server = new ServerSocket(4005); // серверсокет слухає порт 4005
                System.out.println("Сервер запущено!"); // друк повідомленя, що сервер запущено

                // нескінчений цикл
                while (true) {
                    try {
                        clientSocket = server.accept(); // чекає, поки буде отримано запит
                        //потім починається обробка повідомлення

                        in = new ObjectInputStream(clientSocket.getInputStream());
                        Pair question = (Pair) in.readObject(); // чекає, поки клієнтом буде надіслане повідомлення

                        // відправка повідомлення та дії в залежності від отриманого повідомлення

                        if (((String) question.getKey()).equals("save_token")) {
                            System.out.println("Token get");
                            Token = (String) question.getValue();

                        } else if (((String) question.getKey()).equals("get_token")) {
                            System.out.println("Token sent");
                            out = new ObjectOutputStream(clientSocket.getOutputStream());
                            out.writeObject(new Pair("token", Token));
                            out.flush();
                            out.close();
                        } else if (((String) question.getKey()).equals("close")) {
                            System.out.println("Close server");
                            break;
                        }

                    } finally {
                        // закриття in потоку, який відкривається завжди
                        in.close();
                    }
                }
            } finally {
                clientSocket.close();
                System.out.println("Сервер закрыто!");
                server.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            for (StackTraceElement st : e.getStackTrace()) {
                {
                    System.err.println(st);

                }
                ;
            }
        }
    }
}

