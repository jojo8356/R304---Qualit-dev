# 🧩 TP : Inversion de dépendances (Dependency Inversion Principle - DIP)

## 🌟 Objectif du TP

Ce TD a pour but de comprendre et d’appliquer le **principe d’inversion de dépendances** du paradigme **SOLID**.

> **Rappel du DIP :**
> Les modules de haut niveau ne doivent pas dépendre des modules de bas niveau.
> Les deux doivent dépendre d’abstractions.
> Les abstractions ne doivent pas dépendre des détails, ce sont les détails qui doivent dépendre des abstractions.

Nous allons observer un cas concret où une classe de “haut niveau” (`MessagePrinter`) dépend directement de détails d’implémentation (`JSONFormatter`, `FileWriter`, etc.), et corriger ce design pour le rendre plus flexible, testable et conforme au DIP.

---

## 🧠 Contexte

Nous avons une petite application qui imprime des messages.
Un message (`Message`) contient du texte et doit être **formaté** puis **écrit quelque part** (fichier, console, etc.).

### Architecture actuelle (code de départ)

- `MessagePrinter` : écrit le message formaté dans un fichier.
- `JSONFormatter` : formate le message en JSON.
- `Message` : simple classe contenant le texte du message.

Le problème :

```java
public void writeMessage(Message msg, String fileName) throws IOException {
    Formatter formatter = new JSONFormatter(); // dépendance directe à une implémentation
    try(PrintWriter writer = new PrintWriter(new FileWriter(fileName))) { // idem
        writer.println(formatter.format(msg));
        writer.flush();
    }
}
```

---

## 🚨 Problème de conception

- `MessagePrinter` **décide lui-même** du _format_ (`JSONFormatter`)
  🔘 elle dépend d’une **implémentation concrète**.

- `MessagePrinter` **décide aussi** du _support de sortie_ (`FileWriter`)
  🔘 elle dépend encore d’une **implémentation concrète**.

- Si demain on veut écrire le message :
  - en texte brut au lieu de JSON,
  - sur la console au lieu d’un fichier,
    il faut **modifier le code de `MessagePrinter`**.

C’est une violation du principe **Open/Closed** (le code n’est pas ouvert à l’extension sans modification)
et surtout du **Dependency Inversion Principle**.

---

## 🧩 Objectif de la refactorisation

Faire en sorte que :

- `MessagePrinter` ne connaisse plus **aucune classe concrète** de formatage ou d’écriture,
- ces dépendances soient **injectées** depuis l’extérieur,
- le code devienne **extensible** sans modification.

---

## 🔯 Étapes du travail à faire

### 1. Identifier les dépendances à inverser

Dans `MessagePrinter`, repérez :

- la création du `Formatter` (`new JSONFormatter()`),
- la création du `PrintWriter` (`new FileWriter(fileName)`).

Ce sont ces deux **points de couplage** qu’il faut casser.

---

### 2. Créer une abstraction pour le formatage

Créez une **interface `Formatter`** qui définira une méthode générique de formatage :

```java
public interface Formatter {
    String format(Message message);
}
```

Puis faites en sorte que `JSONFormatter` **implémente** cette interface.

---

### 3. Modifier `MessagePrinter` pour dépendre de l’abstraction

La méthode de `MessagePrinter` ne doit plus **instancier** ses dépendances :

```java
public void writeMessage(Message msg, Formatter formatter, PrintWriter writer) throws IOException {
    writer.println(formatter.format(msg));
    writer.flush();
}
```

---

### 4. Choisir les implémentations dans `Main`

C’est le **programme principal** (`Main`) qui doit désormais assembler les pièces du puzzle.

Exemple :

```java
Message msg = new Message("This is a message again");
MessagePrinter printer = new MessagePrinter();

try (PrintWriter writer = new PrintWriter(System.out)) {
    printer.writeMessage(msg, new JSONFormatter(), writer);
}
```

Ici :

- le **formateur** est choisi (JSON, XML, texte brut, …),
- la **destination** est choisie (fichier, console, mémoire…),
- `MessagePrinter` ne dépend plus d’aucune de ces classes concrètes.

---

## ✅ Résultat attendu

- Le code compile et fonctionne comme avant.
- Il est maintenant possible :
  - d’ajouter un `TextFormatter`, `XMLFormatter`, etc.
  - d’envoyer la sortie vers un autre flux (console, réseau…)
  - sans modifier la classe `MessagePrinter`.

Exemple de test :

```java
try (PrintWriter writer = new PrintWriter(new FileWriter("out.json"))) {
    printer.writeMessage(msg, new JSONFormatter(), writer);
}

try (PrintWriter writer = new PrintWriter(System.out)) {
    printer.writeMessage(msg, new TextFormatter(), writer);
}
```

---

## 🧩 Pour aller plus loin

👉 Réfléchissez à ces questions :

- Pourquoi le `main` est-il le bon endroit pour assembler les dépendances ?
- En quoi ce design facilite-t-il le test unitaire ?
- Comment pourriez-vous injecter ces dépendances automatiquement avec un framework (ex : Spring, Guice) ?

---

## 💾 Ce que vous aurez appris

- Comprendre la différence entre **module haut niveau** (logique métier) et **module bas niveau** (détails techniques).
- Identifier une **violation du DIP**.
- Corriger le couplage entre classes par **injection de dépendances**.
- Distinguer la **composition** (dans `main`) du **comportement métier** (dans `MessagePrinter`).

---

### 💬 Rappel final

> "Les abstractions ne doivent pas dépendre des détails.
> Ce sont les détails qui doivent dépendre des abstractions."
> — _Robert C. Martin (Uncle Bob)_

---

🧠 **Conseil :**
Si vous hésitez sur ce qui est “haut niveau” ou “bas niveau”, demandez-vous :

> "Si je change de technologie (ex : fichier → base de données),
> quel code devrais-je modifier ?"
> → Celui qui **ne devrait pas bouger** est _haut niveau_.
