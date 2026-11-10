🧩 TP : Interface Segregation Principle (ISP)

🎯 Objectif du TP

Mettre en pratique le principe de ségrégation des interfaces (Interface Segregation Principle — ISP) du paradigme SOLID.

Principe : Les clients ne doivent pas être forcés de dépendre d’interfaces qu’ils n’utilisent pas.

⸻

🧠 Contexte

Le projet manipule des entités stockées en mémoire :
• User : possède un nom et une date de dernière connexion.
• Order : représente une commande, avec une date et une valeur totale.

Toutes les entités héritent de Entity, et une interface commune PersistenceService<T extends Entity> définit les opérations de persistance.

public interface PersistenceService<T extends Entity> {
    void save(T entity);
    void delete(T entity);
    T findById(Long id);
    List<T> findByName(String name);
}

⸻

🚨 Problème

Cette interface impose la méthode findByName à toutes les classes qui l’implémentent, y compris celles pour lesquelles cette méthode n’a aucun sens (Order n’a pas de nom par exemple).

Cela viole le principe de ségrégation des interfaces (ISP) : une classe ne doit pas être forcée d’implémenter des méthodes dont elle n’a pas besoin.

⸻

🧩 Travail à effectuer

Créez une nouvelle classe OrderPersistenceService qui implémente correctement PersistenceService<Order> en respectant le principe SOLID.

L’objectif est de comprendre comment limiter la portée d’une interface pour que chaque classe implémente uniquement ce qui lui est utile.

⸻

✅ Résultat attendu

Votre nouvelle classe doit :
• Implémenter uniquement les méthodes pertinentes pour Order ;
• Respecter la cohérence du contrat PersistenceService ;
• Illustrer le principe de ségrégation des interfaces.
