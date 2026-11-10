## 🧩 TD : Principe de substitution de Liskov (LSP) et application du principe Ouvert/Fermé (OCP)

### 🎯 Objectif

Approfondir la compréhension du **principe de substitution de Liskov (LSP)** et réfléchir à la manière d’y **réintroduire le principe Ouvert/Fermé (OCP)** pour obtenir un code extensible et cohérent.

---

### 📦 Contexte

Le projet fourni met en œuvre deux formes géométriques simples : un **rectangle** et un **carré**.
Leur relation actuelle illustre une situation classique en conception orientée objet où l’héritage peut poser problème.

À première vue, le carré semble être un cas particulier du rectangle. Pourtant, cette relation soulève des incohérences dans le comportement attendu des objets et remet en cause la validité du polymorphisme.

---

### 🧠 Travail proposé

1. **Analyser la hiérarchie de classes** fournie et comprendre les contraintes imposées par la relation entre rectangle et carré.
2. **Observer le comportement** de la méthode de calcul d’aire et déterminer ce qui remet en question le principe de substitution de Liskov.
3. **Réfléchir à la manière de rétablir la cohérence du modèle**, en évitant les effets indésirables liés à l’héritage.
4. **Introduire une abstraction adaptée** pour permettre l’ajout d’autres formes géométriques sans altérer le code existant.

---

### 💡 Indications

- Chaque forme doit pouvoir être utilisée de manière interchangeable sans modifier le comportement attendu.
- Les invariants propres à chaque type de forme doivent être respectés.
- Une bonne conception doit permettre d’**ajouter de nouvelles formes** sans modifier le code existant.

---

### 🧾 Livrable attendu

Une version du projet où la conception respecte le **LSP** et prépare l’extension du modèle conformément au **OCP**.
Le comportement global doit rester cohérent et les nouvelles formes doivent pouvoir être intégrées naturellement.
