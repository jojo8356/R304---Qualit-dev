# OCP — Open/Closed Principle : 12 avant / après à imprimer dans la tête

> **Ouvert à l'extension, fermé à la modification.**
> Ajouter un comportement = **ajouter du code** (un fichier, une ligne dans un registre).
> → Modifier le moins possible de code **déjà écrit et déjà testé**.

**Le test des 5 minutes** (à te répéter à chaque `if`) :
> « Demain on ajoute un 5ᵉ type / un 6ᵉ pays / un 7ᵉ format.
> Est-ce que je **crée un fichier**… ou est-ce que je **rouvre une fonction qui marche déjà** ? »
> Si réponse 2 → il manque une couture (un *seam*).

---

## 0. La carte mentale : quel « avant » se transforme en quel « après »

| Avant (le symptôme) | Après (la couture) | Exemple |
|---|---|---|
| `if / else if` en chaîne sur un champ (`type`, `pays`, `format`) | Table de correspondance + interface | 1, 2, 5, 8 |
| `switch` sur un discriminant | Registre / polymorphisme | 3, 6, 7 |
| Champs booléens en cascade (`options.audit`, `options.email`…) | Liste d'étapes (pipeline) | 10 |
| Code de chaque variante collé dans une grosse fonction | Une classe/fonction par variante | 4, 9 |
| Valeurs métier codées en dur dans la logique | Règles pilotées par les **données** (config, JSON) | 4, 12 |
| `if (event === "...")` en dur | Handlers enregistrés | 11 |

**Les 4 formes de couture** (les seules à connaître) :
1. **Interface + implémentations** (polymorphisme)
2. **Registre / Map** (`Map<clé, stratégie>`)
3. **Liste d'étapes** (pipeline / middleware)
4. **Données** (tableau de règles, JSON, config)

---

## 1. Remises client — le cas d'école

### ❌ Avant

```ts
function calculerPrixFinal(typeClient: string, montant: number): number {
  if (typeClient === "standard") return montant;
  if (typeClient === "premium") return montant * 0.9;
  if (typeClient === "vip") return montant * 0.8;
  if (typeClient === "association") return montant * 0.85;
  throw new Error("type de client inconnu: " + typeClient);
}
```

Ce qui fait mal : on rouvre une fonction testée pour chaque nouveau type ; le `throw` est loin du `if` ; la règle « association » et la règle « vip » ne peuvent pas avoir de tests isolés.

### ✅ Après

```ts
interface Remise {
  readonly type: string;
  appliquer(montant: number): number;
}

export const REMISES: Remise[] = [
  { type: "standard",    appliquer: (m) => m },
  { type: "premium",     appliquer: (m) => m * 0.9 },
  { type: "vip",         appliquer: (m) => m * 0.8 },
  { type: "association", appliquer: (m) => m * 0.85 },
];

const parType = new Map(REMISES.map((r) => [r.type, r]));

export function calculerPrixFinal(typeClient: string, montant: number): number {
  const remise = parType.get(typeClient);
  if (!remise) throw new Error("type de client inconnu: " + typeClient);
  return remise.appliquer(montant);
}
```

**Nouveau client `"etudiant"`** → une ligne de données (ou un fichier `remise-etudiant.ts` importé dans le registre). `calculerPrixFinal` ne bouge plus jamais. Testable une par une.

---

## 2. Export de rapport — un format = un fichier

### ❌ Avant

```ts
function exporter(format: string, lignes: Ligne[]): string {
  if (format === "csv") {
    return lignes.map((l) => l.join(",")).join("\n");
  } else if (format === "json") {
    return JSON.stringify(lignes);
  } else if (format === "xml") {
    return "<rows>" + lignes.map((l) => "<row>" + l.join("|") + "</row>").join("") + "</rows>";
  }
  throw new Error("format non supporté");
}
```

### ✅ Après

```ts
interface Exportateur {
  readonly format: string;
  readonly extension: string;
  exporter(lignes: Ligne[]): string;
}

export const EXPORTATEURS: Exportateur[] = [exportateurCsv, exportateurJson, exportateurXml];
const parFormat = new Map(EXPORTATEURS.map((e) => [e.format, e]));

function exporter(format: string, lignes: Ligne[]): string {
  const e = parFormat.get(format);
  if (!e) throw new Error("format non supporté: " + format);
  return e.exporter(lignes);
}
```

`exportateur-xlsx.ts` arrive plus tard → **zéro** modification de `exporter()`, et le fichier est testable seul.

---

## 3. Moyens de paiement — `switch` → interface

### ❌ Avant

```ts
async function payer(methode: string, montant: number, devise: string) {
  switch (methode) {
    case "cb":
      if (devise !== "EUR") throw new Error("CB: euros uniquement");
      return await stripe.charge(montant, devise);
    case "paypal":
      return await paypal.pay({ amount: montant, currency: devise });
    case "virement":
      await db.insert({ montant, devise, statut: "en_attente" });
      return { statut: "en_attente" };
    default:
      throw new Error("méthode inconnue");
  }
}
```

### ✅ Après

```ts
interface MoyenPaiement {
  readonly code: string;
  supports(devise: string): boolean;
  payer(montant: number, devise: string): Promise<Recu>;
}

class PaiementCarte implements MoyenPaiement {
  readonly code = "cb";
  supports(devise: string) { return devise === "EUR"; }
  async payer(montant: number, devise: string) { return stripe.charge(montant, devise); }
}

class PaiementVirement implements MoyenPaiement {
  readonly code = "virement";
  supports(_: string) { return true; }
  async payer(montant: number, devise: string) {
    await db.insert({ montant, devise, statut: "en_attente" });
    return { statut: "en_attente" };
  }
}

// Le seul code "à rouvrir" quand on ajoute un moyen : cette liste de 1 ligne.
const MOYENS: MoyenPaiement[] = [new PaiementCarte(), new PaiementPaypal(), new PaiementVirement()];

async function payer(code: string, montant: number, devise: string) {
  const moyen = MOYENS.find((m) => m.code === code);
  if (!moyen) throw new Error("méthode inconnue: " + code);
  if (!moyen.supports(devise)) throw new Error(`${code} ne gère pas ${devise}`);
  return moyen.payer(montant, devise);
}
```

Bonus : les préconditions (« que EUR ») deviennent une méthode testable, plus une ligne perdue dans un `case`.

---

## 4. Frais de port — OCP *par les data* (souvent le meilleur)

### ❌ Avant

```ts
function fraisDePort(pays: string, poidsKg: number, totalPanier: number): number {
  if (pays === "FR") {
    if (totalPanier > 50) return 0;
    return 4.9 + Math.max(0, Math.ceil(poidsKg - 1)) * 1.5;
  }
  if (pays === "BE" || pays === "LU") return 7.9 + poidsKg * 2;
  if (pays === "CH") return 12 + poidsKg * 2.5;
  return 19.9 + poidsKg * 3;
}
```

Chaque nouveau pays = on rouvre la fonction. Les tarifs sont **dans** le code : la logistique ne peut pas les changer.

### ✅ Après

```ts
interface ReglePort {
  readonly zones: string[];      // ["FR"]
  readonly franchise: number;    // 50
  readonly base: number;         // 4.9
  readonly parKg: number;        // 1.5
}

export const REGLES_PORT: ReglePort[] = [
  { zones: ["FR"],         franchise: 50, base: 4.9,  parKg: 1.5 },
  { zones: ["BE", "LU"],   franchise: 0,  base: 7.9,  parKg: 2.0 },
  { zones: ["CH"],         franchise: 0,  base: 12,   parKg: 2.5 },
];
export const REGLE_DEFAUT: ReglePort = { zones: [], franchise: 0, base: 19.9, parKg: 3 };

function fraisDePort(pays: string, poidsKg: number, totalPanier: number): number {
  const regle = REGLES_PORT.find((r) => r.zones.includes(pays)) ?? REGLE_DEFAUT;
  if (totalPanier >= regle.franchise && regle.franchise > 0) return 0;
  return regle.base + Math.ceil(poidsKg) * regle.parKg;
}
```

Ici l'extension ne coûte **aucune ligne de code** : c'est un changement de données (voire un JSON ou la base). À chaque fois que la variation est *quantitative* (tarifs, seuils, durées), OCP = **sortir les nombres du code**, pas créer des classes.

⚠️ Le jour où un pays a une règle *de forme* différente (poids volumétrique, transporteur tiers), là tu remplaces la struct par une interface `ReglePort.calculer()`. Les deux se combinent.

---

## 5. Notifications — `if` sur le canal

### ❌ Avant

```ts
async function notifier(user: User, canal: string, message: string) {
  if (canal === "email") await sendgrid.send(user.email, message);
  else if (canal === "sms") await twilio.send(user.phone, message);
  else if (canal === "push") await fcm.push(user.deviceToken, message);
  else throw new Error("canal inconnu");
}
```

### ✅ Après

```ts
interface Canal {
  readonly code: string;
  envoyer(user: User, message: string): Promise<void>;
}

const CANAUX: Canal[] = [canalEmail, canalSms, canalPush];
const parCode = new Map(CANAUX.map((c) => [c.code, c]));

async function notifier(user: User, codes: string[], message: string) {
  const canaux = codes.map((c) => parCode.get(c) ?? throwInconnu(c));
  await Promise.all(canaux.map((c) => c.envoyer(user, message)));
}
```

Le canal « Slack » (demandé dans 3 mois) = un fichier de 10 lignes + 1 ajout au tableau.

---

## 6. Formes géométriques — le « classique », mais avec l'alternative

### ❌ Avant

```ts
function aire(forme: Forme): number {
  switch (forme.kind) {
    case "cercle":    return Math.PI * forme.r ** 2;
    case "rectangle": return forme.l * forme.h;
    case "triangle":  return (forme.b * forme.h) / 2;
    default: throw new Error("forme inconnue");
  }
}
```

### ✅ Après (polymorphisme)

```ts
interface Forme { aire(): number; }

class Cercle    implements Forme { constructor(private r: number) {}          aire() { return Math.PI * this.r ** 2; } }
class Rectangle implements Forme { constructor(private l: number, private h: number) {} aire() { return this.l * this.h; } }
class Triangle  implements Forme { constructor(private b: number, private h: number) {} aire() { return (this.b * this.h) / 2; } }
```

### ✅ Après (variante fonctionnelle, plus légère)

```ts
const AIRES: Record<string, (f: any) => number> = {
  cercle:    (f) => Math.PI * f.r ** 2,
  rectangle: (f) => f.l * f.h,
  triangle:  (f) => (f.b * f.h) / 2,
};
const aire = (f: any) => (AIRES[f.kind] ?? throwInconnu(f.kind))(f);
```

Même propriété OCP (ajouter une forme = ajouter une entrée), zéro classe. **En TS/JS, la table de fonctions est souvent le bon « après »** : moins d'indirection, même souplesse.

---

## 7. Import de fichiers — interface + `accepte()`

### ❌ Avant

```ts
function importer(contenu: string, mime: string): Ligne[] {
  if (mime === "text/csv") {
    return contenu.split("\n").slice(1).map((l) => l.split(","));
  }
  if (mime === "application/xml") {
    /* 30 lignes de parsing */
  }
  if (mime === "application/vnd.ms-excel") {
    /* 40 lignes de parsing */
  }
  throw new Error("format non géré");
}
```

### ✅ Après

```ts
interface Importateur {
  accepte(mime: string): boolean;
  convertir(contenu: string): Ligne[];
}

class ImportateurCsv implements Importateur {
  accepte(mime: string) { return mime === "text/csv"; }
  convertir(contenu: string) { return contenu.split("\n").slice(1).map((l) => l.split(",")); }
}

const IMPORTATEURS: Importateur[] = [new ImportateurCsv(), new ImportateurXml(), new ImportateurXls()];

function importer(contenu: string, mime: string): Ligne[] {
  const i = IMPORTATEURS.find((x) => x.accepte(mime));
  if (!i) throw new Error("format non géré: " + mime);
  return i.convertir(contenu);
}
```

`accepte()` au lieu d'une clé : utile quand le critère n'est pas une simple égalité (plusieurs MIME, sniffing de contenu).

---

## 8. TVA / taxes par pays — la variante « don't repeat the seam »

### ❌ Avant

```ts
function tva(pays: string, montant: number, produit: string): number {
  if (pays === "FR") return montant * (produit === "livre" ? 0.055 : 0.2);
  if (pays === "DE") return montant * (produit === "livre" ? 0.07 : 0.19);
  if (pays === "CH") return montant * 0.081;
  return 0;
}
```

### ✅ Après

```ts
interface RegimeTva {
  readonly pays: string;
  taux(produit: string): number;
}

class RegimeTvaFr implements RegimeTva {
  readonly pays = "FR";
  taux(produit: string) { return produit === "livre" ? 0.055 : 0.2; }
}

const REGIMES = new Map<string, RegimeTva>([["FR", new RegimeTvaFr()], ["DE", new RegimeTvaDe()], ["CH", new RegimeTvaCh()]]);

const tva = (pays: string, montant: number, produit: string) =>
  montant * (REGIMES.get(pays)?.taux(produit) ?? 0);
```

Et si les taux changent chaque année, `taux()` lit une table de données datée → **le code ne bouge pas, la config oui**.

---

## 9. Plans d'abonnement — capacités plutôt que `if (plan === …)`

### ❌ Avant

```ts
function peutUtiliser(plan: string, fonctionnalite: string): boolean {
  if (plan === "free") return ["lecture"].includes(fonctionnalite);
  if (plan === "pro") return ["lecture", "export_pdf"].includes(fonctionnalite);
  if (plan === "business") return !["sso"].includes(fonctionnalite);
  if (plan === "enterprise") return true;
  return false;
}
```

On recopie la liste partout dans l'app (`if (plan === "business" || plan === "enterprise")`) → chaque nouveau plan se propage en 12 endroits.

### ✅ Après

```ts
interface Plan {
  readonly code: string;
  readonly capacites: ReadonlySet<string>;
  readonly quotas: { sieges: number; stockageGo: number };
}

const PLANS: Plan[] = [
  { code: "free",       capacites: new Set(["lecture"]),                    quotas: { sieges: 1,  stockageGo: 1 } },
  { code: "pro",        capacites: new Set(["lecture", "export_pdf"]),      quotas: { sieges: 5,  stockageGo: 50 } },
  { code: "business",   capacites: new Set(["lecture", "export_pdf", "api"]), quotas: { sieges: 50, stockageGo: 500 } },
  { code: "enterprise", capacites: new Set(["*"]),                          quotas: { sieges: Infinity, stockageGo: Infinity } },
];

const plan = (code: string) => PLANS.find((p) => p.code === code) ?? throwInconnu(code);

export const peutUtiliser = (code: string, f: string) =>
  plan(code).capacites.has("*") || plan(code).capacites.has(f);
```

Le reste de l'app n'appelle **que** `peutUtiliser(...)`. Ajouter un plan = 1 ligne de données.

---

## 10. Traitement de commande — booléens → pipeline d'étapes

### ❌ Avant

```ts
async function traiterCommande(cmd: Commande, options: { audit?: boolean; email?: boolean; slack?: boolean }) {
  await valider(cmd);
  await enregistrer(cmd);
  if (options.audit) await ecrireAudit(cmd);
  if (options.email) await envoyerEmail(cmd);
  if (options.slack) await notifierSlack(cmd);
  if (options.facture) { /* ajouté le mois dernier… */ }
  // … et on rouvre cette fonction à chaque nouvelle étape
}
```

### ✅ Après

```ts
interface Etape {
  readonly nom: string;
  executer(cmd: Commande): Promise<void>;
}

const ETAPES: Etape[] = [etapeValidation, etapePersistance, etapeAudit, etapeEmail, etapeFacture];

async function traiterCommande(cmd: Commande) {
  for (const etape of ETAPES) await etape.executer(cmd);
}
```

Nouvelle étape (webhook CRM, anti-fraude…) = **nouveau fichier + 1 ligne dans le tableau**. La fonction d'orchestration est gelée. C'est de l'OCP par la **liste d'étapes**.

---

## 11. Événements / handlers — `if (event === …)`

### ❌ Avant

```ts
async function onEvent(e: { type: string; data: any }) {
  if (e.type === "user.created") await envoyerBienvenue(e.data);
  else if (e.type === "user.deleted") await purgerDonnees(e.data);
  else if (e.type === "order.paid") await facturer(e.data);
  else if (e.type === "order.refunded") await rembourser(e.data);
  else console.log("event ignoré", e.type);
}
```

### ✅ Après

```ts
type Handler = (data: any) => Promise<void>;

const HANDLERS = new Map<string, Handler[]>();

export function on(type: string, h: Handler) {
  HANDLERS.set(type, [...(HANDLERS.get(type) ?? []), h]);
}

// Chaque module s'enregistre lui-même à l'import (plugin)
on("user.created", envoyerBienvenue);
on("order.paid", facturer);

export async function onEvent(e: { type: string; data: any }) {
  const hs = HANDLERS.get(e.type) ?? [];
  if (hs.length === 0) console.log("event ignoré", e.type);
  await Promise.all(hs.map((h) => h(e.data)));
}
```

Les modules métier n'ont plus besoin de se connaître → **OCP au niveau de l'application** (même mécanique que les plugins/middlewares d'Express, les event bus, etc.).

---

## 12. Jeu : armes & items — séparer **contenu** et **comportement**

### ❌ Avant

```ts
function degats(arme: string): number {
  if (arme === "epee") return 12;
  if (arme === "hache") return 18;
  if (arme === "arc") return 9;
  if (arme === "baguette") return 15;
  return 1;
}
```

### ✅ Après

```ts
// 1. Le contenu (stats) vit dans les données
interface Arme { nom: string; degats: number; effet?: (cible: Cible) => void }

const ARMES: Arme[] = [
  { nom: "epee", degats: 12 },
  { nom: "hache", degats: 18 },
  { nom: "arc", degats: 9, effet: (c) => c.saigner(2) },
  { nom: "baguette", degats: 15, effet: (c) => c.etourdir(1) },
];
const armes = new Map(ARMES.map((a) => [a.nom, a]));

// 2. Le comportement d'application reste unique
function frapper(nomArme: string, cible: Cible) {
  const a = armes.get(nomArme) ?? { nom: "poings", degats: 1 };
  cible.subir(a.degats);
  a.effet?.(cible);
}
```

Règle : **les données pour ce qui varie en valeur, le code pour ce qui varie en logique**. Un designer peut ajouter 50 armes sans qu'un dev touche au moteur.

---

## 13. Contre-exemple : l'OCP mal mis (le piège à connaître)

### ❌ Avant (sur-abstraction)

```ts
interface RemiseStrategyFactoryProvider { getStrategy(type: string): RemiseStrategy; }
abstract class AbstractRemiseStrategy implements RemiseStrategy { /* 60 lignes */ }
```

…pour un système qui n'a **qu'une** remise, et aucune deuxième prévue.

### ✅ Après (YAGNI assumé)

```ts
const REMISE_PREMIUM = 0.9;
export const prixPremium = (m: number) => m * REMISE_PREMIUM;
```

**Les 3 règles pour ne pas sur-abstraire :**
1. **Règle de 2** : 1 seule variante → direct. La 2ᵉ *réelle* arrive → on coupe. (Se préparer, c'est repérer l'axe de changement, pas écrire l'indirection.)
2. **Roadmap** : si le 3ᵉ cas est déjà au backlog → l'interface est un investissement, pas une fiction.
3. **Mesure le coût** : chaque couture ajoute un niveau d'indirection + un fichier. Une table de données bat 3 classes.

Et **« fermé à la modification » ≠ zéro modification** : ajouter une ligne au registre, c'est acceptable. Le but réel c'est *ne pas rouvrir la logique qui marche*.

---

## 14. Comment reconnaître qu'il faut le faire (5 signaux)

1. Tu ajoutes un `else if` à une chaîne qui en a déjà 3.
2. Tu vois la **même** condition (`if plan === "business" || …`) recopiée dans plusieurs fichiers.
3. Un bug dans un `case` force à relire les 9 autres `case`.
4. Un non-dev (logistique, marketing, ops) a besoin de changer une **valeur** → elle doit sortir du code.
5. Une des branches devient grosse au point qu'on veut la tester seule.

## 15. Les 4 questions de contrôle (auto-évaluation)

1. **Quel** est l'axe de variation ? (type ? pays ? format ? étape ?)
2. Où est **une seule** ligne à modifier pour étendre ? (idéalement un registre ou des données)
3. Est-ce que j'ai ajouté une **indirection utile** (2ᵉ cas réel) ou du bruit ?
4. Est-ce que `nouveau cas = nouveau fichier + 0 modification de logique` ? Si non, quelle couture manque ?

---

## 16. Flashcards (relis juste ça avant un entretien)

- **OCP en une phrase ?** Ouvert à l'extension, fermé à la modification.
- **Le test rapide ?** « Pour le 5ᵉ cas, j'ajoute un fichier ou je rouvre une fonction testée ? »
- **Le piège classique ?** Le `if/else if` sur un champ `type` répété dans tout le projet.
- **Les 4 coutures ?** Interface, registre (Map), liste d'étapes, données.
- **Contre quoi échanger l'OCP ?** Une indirection : d'où « règle de 2 » + YAGNI.
- **OCP vs DRY ?** OCP = ne pas rouvrir la logique ; DRY = ne pas dupliquer la connaissance. Les registres servent les deux.
