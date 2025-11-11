package DClients;

import java.util.Arrays;
import java.util.List;

public class ClientData {
    public static List<Client> getClients() {
        return Arrays.asList(
                new Client(101, "Dupont", "Paris", 150000),
                new Client(102, "Martin", "Lyon", 80000),
                new Client(103, "Durand", "Lyon", 95000),
                new Client(104, "Dubois", "Marseille", 1200000),
                new Client(105, "Bernard", "Paris", 60000),
                new Client(106, "Petit", "Lille", 45000)
        );
    }
}
