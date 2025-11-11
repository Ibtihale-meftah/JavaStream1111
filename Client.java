package DClients;

import java.util.Objects;

public class Client {
    private final int idClient;
    private final String nom;
    private final String adresse; // Ville
    private final double chiffreAffaire;

    public Client(int idClient, String nom, String adresse, double chiffreAffaire) {
        this.idClient = idClient;
        this.nom = nom;
        this.adresse = adresse;
        this.chiffreAffaire = chiffreAffaire;
    }

    public int getIdClient() {
        return idClient;
    }

    public String getNom() {
        return nom;
    }

    public String getAdresse() {
        return adresse;
    }

    public double getChiffreAffaire() {
        return chiffreAffaire;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return idClient == client.idClient && Double.compare(chiffreAffaire, client.chiffreAffaire) == 0 && Objects.equals(nom, client.nom) && Objects.equals(adresse, client.adresse);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idClient, nom, adresse, chiffreAffaire);
    }

    @Override
    public String toString() {
        return "Client{" +
                "idClient=" + idClient +
                ", nom='" + nom + '\'' +
                ", adresse='" + adresse + '\'' +
                ", chiffreAffaire=" + chiffreAffaire +
                '}';
    }
}

