import javafx.util.Pair;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;


public class ServerOne {

    private static Socket clientSocket; // сокет для комунікації з клієнтом
    private static ServerSocket server; // серверсокет
    private static ObjectInputStream in; // потік читання з сокету
    private static ObjectOutputStream out; // потік запису в сокет

    final static String correct_login = "correct_login";
    final static String correct_password = "correct_password";

    static String Token;

    public static void main(String[] args) {
        try {
            try {
                server = new ServerSocket(4004); // серверсокет слухає порт 4004
                System.out.println("Сервер запущено!"); // друк повідомленя, що сервер запущено

                // нескінчений цикл
                //вихід якщо отримано токен
                while (true) {
                    try {
                        clientSocket = server.accept(); // чекає, поки буде отриманj запит
                        //потім починається обробка повідомлення

                        in = new ObjectInputStream(clientSocket.getInputStream());
                        Pair question = (Pair) in.readObject(); // чекає, поки клієнтом буде надіслане повідомлення

                        // відправка повідомлення та дії в залежності від отриманого повідомлення
                        out = new ObjectOutputStream(clientSocket.getOutputStream());
                        if (((String) question.getKey()).equals("login")) {
                            System.out.println("Login get");
                            if (check_login((String) question.getValue())) {
                                out.writeObject(new Pair("response", "input password"));
                            } else {
                                out.writeObject(new Pair("response", "wrong login"));
                                out.flush();
                                break;
                            }

                        } else if (((String) question.getKey()).equals("password")) {
                            System.out.println("Password get");
                            if (check_password((String) question.getValue())) {
                                out.writeObject(new Pair("response", "input code"));
                                generate_code();
                            } else {
                                out.writeObject(new Pair("response", "wrong password"));
                                out.flush();
                                break;
                            }

                        } else if (((String) question.getKey()).equals("code")) {
                            System.out.println("Temporary code get");
                            if (check_code((String) question.getValue())) {
                                out.writeObject(new Pair("response", "authorized"));
                            } else {
                                out.writeObject(new Pair("response", "wrong token"));
                            }
                            out.flush();
                            MessageSender.sendMessage(new Pair("close", ""));
                            break;
                        }
                        out.flush();

                    } finally {
                        in.close();
                        out.close();
                    }
                }
            } finally {
                clientSocket.close();
                System.out.println("Сервер закрыто!");
                server.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println(e);
        }
    }

    /**
     * Перевірка коректності логіну
     * Використовується заглушка. Може бути використаний будь який алгоритм
     *
     * @param login логін, що перевіряється
     * @return true, якщо логін коректний
     */
    private static boolean check_login(String login) {
        return login.equals(ServerOne.correct_login);
    }

    /**
     * Перевірка коректності паролю
     * Використовується заглушка. Може бути використаний будь який алгоритм
     *
     * @param password пароль, що перевіряється
     * @return true, якщо пароль коректний
     */
    private static boolean check_password(String password) {
        return password.equals(ServerOne.correct_password);
    }

    /**
     * Генерація кода, відправка його на другий сервер
     */
    private static void generate_code() {
        Token = String.valueOf(new Random().nextInt(10000));
        TokenSender.sendToken(Token);
    }

    private static boolean check_code(String code) {
        return (Token.equals(code));
    }

    /**
     * Клас, що відповідає за надсилання токену на другий сервер
     */
    private static class TokenSender {

        /**
         * Відсилання токену на другий сервер
         * @param token - токен, що відсилається
         */
        public static void sendToken(String token) {
            final Pair message = new Pair("save_token", token);
            MessageSender.sendMessage(message);
        }
    }

    private static class MessageSender{
        private static Socket clientSocket; //сокет для общения
        private static ObjectOutputStream out; // поток записи в сокет

        /**
         * Відсилання повідомлення на другий сервер, без отримання відгуку
         * @param message - повідомлення, що відсилається
         */
        public static void sendMessage(Pair message) {
            try {
                try {
                    clientSocket = new Socket("localhost", 4005); // этой строкой мы запрашиваем
                    //  у сервера доступ на соединение

                    // и отправлять
                    out = new ObjectOutputStream(clientSocket.getOutputStream());
                    out.writeObject(message);
                    out.flush();

                } finally { // в любом случае необходимо закрыть сокет и потоки
                    clientSocket.close();
                    out.close();
                }
            } catch (IOException e) {
                System.err.println(e);
            }

        }
    }
}


