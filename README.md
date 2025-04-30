# 📄 Fixed-Length File Processor (met JMS DLQ)

Een Spring Boot applicatie die regels uit een tekstbestand met vaste veldlengtes verwerkt. Correcte regels worden als JSON naar een JMS queue gestuurd. Foutieve regels worden naar een aparte **Dead Letter Queue** gestuurd met foutinformatie.

## 🧩 Functionaliteit

- Verwerking van regels met **vaste breedte** (fixed length)
- Velden per regel:
    - `trackId`: String (36 karakters)
    - `pdfNaam`: String (40 karakters)
    - `bestemming`: String (3 karakters)
    - `aantalPaginas`: Integer (maximaal 2 posities als string, dus bijv. `1`, `12`)
- Verwerking met behulp van **Univocity Parsers**
- Gebruik van **Lombok** voor boilerplate code (getters/setters)
- Correcte regels worden als JSON verstuurd naar JMS queue: `univocity.queue`
- Foutieve regels (bijvoorbeeld invalid integers of onjuiste lengte) worden:
    - Verstuurd als JSON naar `univocity.dlq`
    - Bevatten foutmelding en timestamp

## 📁 Bestandsformaat

Elke regel bevat exact 81 tekens:
```
<36 tekens><40 tekens><3 tekens><1-3 cijferig getal als string>
````

**Voorbeeldregels:**
```
e26f4212-1f67-4c9c-9fcb-858db5f16209e26f4212-1f67-4c9c-9fcb-858db5f16209.pdfEUR1
1731fa92-cd42-4330-b2d3-ca69fcd78f611731fa92-cd42-4330-b2d3-ca69fcd78f61.pdfNLD2
0f2d7ec9-f09f-4d08-992b-77cb1908d88e0f2d7ec9-f09f-4d08-992b-77cb1908d88e.pdfROW3
````

## 🚀 Uitvoeren

Zorg er voor dat ActiveMQ is op gestart en vervolgens kan je onderstaande uitvoeren.
``` bash
mvn spring-boot:run
````

## ⚙️ Technologieën

- Spring Boot
- Spring JMS
- Univocity Parsers (voor fixed-width parsing)
- Jackson (JSON serialisatie)
- Lombok
- SLF4J / Logback
- JUnit 5 en Mockito (voor testen)

## 📨 Queues

| Queue             | Doel                                  |
|-------------------|----------------------------------------|
| `univocity.queue` | Bevat correct verwerkte JSON-records   |
| `univocity.dlq`   | Bevat foutieve regels + foutdetails    |


## 📦 Voorbeeld Correct verwerkt JSON-bericht

```json
{
  "trackId": "fe3502b9-68a8-47ad-9154-5a1b8131a9de",
  "pdfNaam": "fe3502b9-68a8-47ad-9154-5a1b8131a9de.pdf",
  "bestemming": "EUR",
  "aantalPaginas": 4
}
````

## 📦 Voorbeeld DLQ JSON-bericht

```json
{
  "failedLine": "1731fa92-cd42-4330-b2d3-ca69fcd78f611731fa92-cd42-4330-b2d3-ca69fcd78f61.pdfNLDGH",
  "error": "For input string: \"GH\"",
  "timestamp": "2025-04-30T05:50:46.404704500Z"
}
````

## 🧪 Testen
Unit tests aanwezig voor:

- Verwerking van correcte records
- Foutafhandeling en validatie
- Verificatie van berichten naar juiste queues

Gebruikte libraries:

- JUnit 5
- Mockito
