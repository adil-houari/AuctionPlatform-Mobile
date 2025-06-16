[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-24ddc0f5d75046c5622901739e7c5dd533143b0c8e959d652212380cedb1ea36.svg)](https://classroom.github.com/a/rZqLCngE)
# Project Mobile Development

## Doelstellingen

- De student kan op een zelfstandige manier de code schrijven om een software-project te realiseren
- De student kan een gebruiksvriendelijke mobiele applicatie maken op basis van business criteria.
- De student kan een externe databron aanspreken.

## Context

Voor de opdracht schrijf je een mobiele app voor een online veilingsplatform. Via de app kan een gebruiker een artikel te koop aanbieden en kunnen andere gebruikers een bod uitbrengen op dit artikel. Daarnaast kan een gebruiker de biedingen op zijn/haar items opvolgen en een overzicht raadplegen van de artikels die hij/zij gekocht heeft.

Deze opdracht is een uitbreiding op de opdracht voor het opo Application Development (dag-onderwijs). Om jullie niet volledig afhankelijk te laten zijn van deze opdracht, is er reeds een gelijkaardige API (zie onderaan voor de toelichting) voorzien die jullie kunnen gebruiken.

## Opdracht

Schrijf een mobiele Android app waarbij je kan interageren met de gegeven api.

Voorzie in je app onderstaande functionaliteiten:

- Lijst van veilingsartikelen tonen
  - Filter op maximale prijs
  - Filter op categorie
- Inloggen/uitloggen
- Een nieuw veilingsartikel toevoegen
- Details van een veilingsartikel tonen
- Een bod plaatsen op een item
  - De reeds gedane biedingen opvragen
- Verkoop van een item annuleren
- Verkochte items raadplegen
- Gekochte items raadplegen
- Gekochte items betalen

## Technische vereisten

Houd bij het uitwerken van het project rekening met de volgende (minimale) vereisten:

- Je maakt een Android applicatie die gebruik maakt van Compose.
- Je maakt gebruik van een eigen gekozen theme aan de hand van Material Design 3
- Je maakt gebruik van ViewModels.
- Je maakt gebruik van de geziene navigatie manieren.
- Je maakt gebruik van RetroFit om de api calls uit te voeren.
- Je maakt gebruik van SharedPreferences.
- Voorzie een duidelijke structuur in je project files
- Alle teksten worden in de strings.xml file opgeslagen (geen hardcoded strings)
- Hou rekening met de mogelijk error statussen die de api kan terug geven

## App flow

### Items

Bij het openen van de applicatie toon je een scherm met alle veilingartikels. Zorg ervoor dat wanneer de items nog aan het laden zijn of niet geladen kunnen worden dat de juiste laad of error states getoond worden. Voorzie hierbij de mogelijkheid om te refreshen.

Zorg ervoor dat je kan filteren op categorie en op maximale prijs. Dit kan je eventueel doen aan de hand van een dialog.

#### Navigatie van uit dit scherm

Je kan vanuit dit scherm navigeren naar:

- Detail pagina van een item
- Nieuw item toevoegen
- Login scherm

### Detail pagina van een item

Wanneer een gebruiker de details van een item wil zien klikt deze op een artikel. In de detail pagina wordt alle beschikbare info getoond. Laad ook de biedingen in voor het item. Hou er rekening mee dat niet ingelogde gebruikers geen biedingen kunnen zien.
Vanuit dit scherm moet het ook mogelijk zijn om een bod te doen op een item (let op hiervoor dien je ingelogd te zijn)

Wanneer de ingelogde gebruiker de verkoper is van het item, kan deze niet bieden op het item. Wel kan de gebruiker de verkoop van het item annuleren.

#### Navigatie van uit dit scherm:

Je kan vanuit dit scherm navigeren naar:

- overzicht scherm met items
- Zorg ervoor dat een gebruiker dit item kan delen met andere personen via een bericht.

### Nieuw item toevoegen

Zorg dat alle informatie invulbaar is. Let op, elke gebruiker heeft een accounttype (Free, Gold, Platinum). Free accounts kunnen geen eindtijd voor de verkoop instellen (dit is by default exact 3 dagen na starttijdstip). Gold en Platinum gebruikers mogen vrij kiezen, maar dit is minimum 12 uur na het starttijdstip.

#### Navigatie van uit dit scherm:

Je kan vanuit dit scherm navigeren naar:

- overzicht scherm met items

### Login/Logout

Voorzie een scherm om in te loggen. Dit scherm moet raadpleegbaar zijn via het items scherm.
Je mag ook optioneel dit scherm tonen wanneer de gebruiker acties wil uitvoeren waarvoor deze ingelogd moet zijn.

Wanneer de gebruiker inlogt sla je zijn gebruikersnaam en wachtwoord op indien dit correct is. Deze ga je later nog nodig hebben. Bij de login call krijgt de gebruiker een JWT-token. Dit token heb je nodig voor de api-calls waarvoor je ingelogd moet zijn. Een JWT-token is 3 uur geldig. Na 3 uur moet je dus de logincall opnieuw uitvoeren waarbij je een nieuw token krijgt.

#### Technisch

Om dit technisch werkend te krijgen zijn er 2 opties:

1. Voor elke api call met autorisatie voer je een login call uit zodat je een nieuw token hebt dat sowieso werkt.
2. Je slaat het jwt-token op en gebruikt dit in je API call. Indien de call faalt met een autorisatie error, voer je de login call opnieuw uit om een nieuw token aan te vragen

Om uit te loggen is het eenvoudig, je verwijderd de gebruikersnaam, wachtwoord en jwt token die je hebt opgeslagen.

#### Navigatie van uit dit scherm

Je kan vanuit dit scherm navigeren naar:

- overzicht scherm met items

### Uitbreiding

Indien je met bovenstaande klaar bent, en tijd hebt, kan je de app vervolledigen met de features waarbij een gebruiker zijn verkochte items en aangekochte items kan raadplegen, alsook de betaalstatus kan raadplegen en wijzigen.

## API

Maak gebruik van je eigen API ontwikkeld voor het vak Application Development (voor studenten Dag opleiding Programmeren)
Of maak gebruik van de door de docent gemaakte API (disclaimer: de api is gemaakt op basis van de opdracht voor het vak Application Development maar kan afwijken van de opdracht voor het vak Application Development. )

De api is raadpleegbaar via https://odiseeauction.azurewebsites.net/. Hier kan je de api ook testen met behulp van swagger.

Calls prefixt met een 🔐 verwachten dat de gebruiker is ingelogd. Hierbij moet je dus een token meegeven.

### Authentication

| url                          | method | beschrijving                    |
| ---------------------------- | ------ | ------------------------------- |
| /api/Authentication/register | POST   | Registreren van een nieuwe user |
| /api/Authentication/login    | POST   | Inloggen van een gebruiker      |

### Kopers

| url                                                      | method | beschrijving                                             |
| -------------------------------------------------------- | ------ | -------------------------------------------------------- |
| 🔐 /api/auction/Buyers/{userName}/items                  | GET    | Geeft de items terug die door een gebruiker gekocht zijn |
| 🔐 /api/auction/Buyers/{userName}/items/{itemId}/payment | POST   | Simuleert een betaling van een gebruiker voor een item   |

### Categorieën

| url                     | method | beschrijving                             |
| ----------------------- | ------ | ---------------------------------------- |
| /api/auction/Categories | GET    | Geeft alle beschikbare categorieën terug |

### Items

| url                                     | method | beschrijving                              |
| --------------------------------------- | ------ | ----------------------------------------- |
| 🔐 /api/auction/Items                   | POST   | Nieuw veiling artikel toevoegen           |
| 🔐 /api/auction/Items/{itemId}/cancel   | DELETE | Artikel cancelen                          |
| 🔐 /api/auction/Items/{itemId}/biddings | GET    | Biedingen van een artikel opvragen        |
| /api/auction/Items/search               | GET    | Alle items opvragen eventueel met filters |
| 🔐 /api/auction/Items/{itemId}/bids     | POST   | Bieden op een item                        |

### Sellers

| url                                      | method | beschrijving               |
| ---------------------------------------- | ------ | -------------------------- |
| 🔐 /api/auction/Sellers/{username}/items | GET    | Alle verkochte items tonen |
  

  