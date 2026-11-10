## 🧩 TD : Application du principe de responsabilité unique (SRP)

### 🎯 Objectif

Mettre en pratique le **principe de responsabilité unique (Single Responsibility Principle)**, selon lequel **une classe ne doit avoir qu’une seule raison de changer**.

---

### 📦 Contexte

Le projet fourni implémente un petit service Java de **gestion d’utilisateurs**. Il contient plusieurs classes : certaines gèrent la validation, d’autres la persistance…
Mais **une classe** en particulier viole le SRP : elle prend en charge **plusieurs responsabilités** qui devraient être séparées.

---

### 🧠 Travail demandé

1. **Identifier** la classe qui ne respecte pas le SRP.
UserController
2. **Analyser** les différentes responsabilités qu’elle assume.
UserValidator
UserPersistanceService
3. **Refactorer** le code en créant des classes dédiées à chaque rôle.
4. **Adapter le reste du code** pour utiliser ces nouvelles classes sans modifier le comportement global du programme.

---

### 💡 Indications

- Une même classe ne doit pas s’occuper **à la fois** de la logique métier, de la validation et de la persistance.
- Après refactorisation, le code doit rester **fonctionnellement identique**.
- Lisez attentivement la méthode principale pour comprendre la logique métier avant de séparer les rôles.

---

### 🧾 Livrable attendu

Un projet Java refactorisé où chaque classe a **une responsabilité unique** et **une seule raison d’évoluer**.
