import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class MainServer {
    public static void main(String[] args) {
        try {
            String serverIp = "192.168.20.163"; // 🔹 Cambia a la IP de tu Mac
            LocateRegistry.createRegistry(1099);
            GameRMI server = new GameServer();
            Naming.rebind("rmi://" + serverIp + "/GameBattleship", server);

            System.out.println("✅ Servidor listo en rmi://" + serverIp + "/GameBattleship");
            System.out.println("Esperando jugadores...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
