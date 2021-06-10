import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class userApp {

        private static Socket clientSocket;
        private static ObjectInputStream in;
        private static ObjectOutputStream out;

        /**
         Основний скрипт послідовних кроків авторизації
         */
        public static void main(String[] args) {
            try {
                try {
                    clientSocket = new Socket("localhost", 4004);

                    dataTransfer message = new dataTransfer("79283245676", 69.61, 60.62);
                    System.out.println(exchange(message, clientSocket));

                    Thread.sleep(500);

                    clientSocket = new Socket("localhost", 4005);
                    message = new Pair("get_token", "");
                    Pair<String, String> rcv_msg = exchange(message, clientSocket);
                    System.out.println(rcv_msg);

                    String token = (String) rcv_msg.getValue();
                    System.out.println(token);

                    clientSocket = new Socket("localhost", 4004);
                    message = new Pair("code", token);
                    System.out.println(exchange(message, clientSocket));

                } finally { // закриття потоків та сокету
                    System.out.println("Виконання скрипту авторизації закінчено");
                    clientSocket.close();
                    in.close();
                    out.close();
                }
            } catch (IOException | ClassNotFoundException | InterruptedException e) {  // обробка виключення
                System.err.println(e);
            }

        }

        /**
         * Відправка повідомлення на сервер, отримання повідомлення
         * @param output
         * @param clientSocket
         * @return
         * @throws IOException
         * @throws ClassNotFoundException
         */
        public static dataTransfer exchange(dataTransfer output, Socket clientSocket) throws IOException, ClassNotFoundException {
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.writeObject(output);
            out.flush();

            in = new ObjectInputStream(clientSocket.getInputStream());
            dataTransfer answer = (dataTransfer) in.readObject();

            return answer;
        }
    }


