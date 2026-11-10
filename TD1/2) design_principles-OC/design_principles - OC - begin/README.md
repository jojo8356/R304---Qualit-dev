## 🧩 TD : Application du principe Ouvert/Fermé (Open/Closed Principle - OCP)

### 🎯 Objectif

Mettre en pratique le **principe Ouvert/Fermé (OCP)** : permettre l’ajout de nouveaux comportements **sans modifier** le code existant.

---

### 📦 Contexte

Le projet fourni contient deux classes dont les rôles présentent un **chevauchement**. Cette redondance crée un risque de devoir **modifier** plusieurs endroits du code lors de l’ajout d’une variante, ce qui **contrevient au OCP**.

---

### 🧠 Travail demandé

1. **Identifier** les deux classes redondantes et décrire en quoi leur conception actuelle expose à des modifications futures.
InternetSessionHistory & CallHistory -> memes types memes méthodes
2. **Mettre en évidence** le comportement commun et le **contrat**.


abstract class 
3. **Introduire une abstraction** claire (interface ou type parent) et **réorganiser** la hiérarchie afin que de nouvelles variantes puissent être **ajoutées** sans modifier le code existant.
4. **Adapter le code client** pour dépendre de l’abstraction plutôt que des classes concrètes.
5. **Vérifier** que le comportement fonctionnel reste identique après refactorisation.

---

### 💡 Indications

- Les longues chaînes de `if`/`switch` sur des types concrets signalent souvent une violation du OCP.
- Préférez **polymorphisme** et **dépendance envers une abstraction**.
- Le **contrat** doit être centralisé dans un type commun ; les variations doivent se faire par **ajout** de nouvelles implémentations.

---

### 🧾 Livrable attendu

- Un projet refactorisé où l’ajout d’une nouvelle variante se fait **uniquement par ajout d’une classe**, sans modifier les classes existantes.
- Un court **README** (5–10 lignes) expliquant comment une nouvelle variante pourrait être ajoutée dans votre design.
