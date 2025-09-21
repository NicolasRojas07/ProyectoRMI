import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class MainServer {
    public static void main(String[] args) {
        try {
            System.setProperty("java.rmi.server.hostname", "192.168.20.163");

            LocateRegistry.createRegistry(1099);

            GameRMI server = new GameServer();

            Naming.rebind("rmi://192.168.20.163/GameBattleship", server);

            System.out.println("Servidor listo en rmi://192.168.20.163/GameBattleship");
            System.out.println("Esperando jugadores...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
