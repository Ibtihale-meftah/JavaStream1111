package DClients;
import java.util.*;
import java.util.stream.*;

public class main {
    public static void main(String[] args) {
        List<Client> clients = ClientData.getClients();

        // Exercice 1 : Filtrage et Tri
        List<Client> lyonClients = clients.stream()
                .filter(c -> "Lyon".equals(c.getAdresse()))
                .sorted(Comparator.comparing(Client::getChiffreAffaire).reversed())
                .collect(Collectors.toList());
        System.out.println("1. Clients de Lyon (CA décroissant) :");
        lyonClients.forEach(System.out::println);

        // Exercice 2 : Transformation et Collecte
        Map<Integer, String> idToNameMap = clients.stream()
                .collect(Collectors.toMap(Client::getIdClient, Client::getNom));
        System.out.println("\n2. Map ID -> Nom :");
        idToNameMap.forEach((id, nom) -> System.out.println("ID " + id + " : " + nom));
        // Exercice 3 : Agrégation
        double caTotalParis = clients.stream()
                .filter(c -> "Paris".equals(c.getAdresse()))
                .mapToDouble(Client::getChiffreAffaire)
                .sum();
        System.out.printf("\n3. CA total des clients de Paris : %.2f\n", caTotalParis);

        // Exercice 4 : Regroupement
        Map<String, Double> caMoyenParVille = clients.stream()
                .collect(Collectors.groupingBy(
                        Client::getAdresse,
                        Collectors.averagingDouble(Client::getChiffreAffaire)
                ));
        System.out.println("\n4. CA moyen par ville :");
        caMoyenParVille.forEach((ville, moyenne) -> System.out.printf("%s : %.2f\n", ville, moyenne));

        // Exercice 5 : Vérification et Transformation
        boolean caSup50k = clients.stream()
                .allMatch(c -> c.getChiffreAffaire() > 50000);
        System.out.println("\n5. Tous les clients ont un CA > 50 000 ? " + caSup50k);
        if (caSup50k) {
            List<String> nomsMajuscules = clients.stream()
                    .map(Client::getNom)
                    .map(String::toUpperCase)
                    .collect(Collectors.toList());
            System.out.println("Noms en majuscules : " + nomsMajuscules);
        }
    }

    }
