# Sii Task for LAT 2025
## Requirements
- Java SDK 19 or higher
- ExchangeRate-API key
## Configuration
1. Clone
```
git clone https://github.com/AleKobek/Sii-Task-for-LAT-2025.git
cd Sii-Task-for-LAT-2025
```
2. Configure exchange rate API key
<p>Paste Your exchange rate API key into apiKey variable in "src/main/java/edu/pja/sii_lat/config/WebClientConfig.java"</p>

```String apiKey = "YOUR_KEY"```

<p>H2 console link = http://localhost:8080/h2-console/</p>

## Rest api endpoints
1. Create a new fundraising event.

`POST /events`
```
{
  "req": {
    "name": "Event1",
    "fundsCurrencyCode": "USD"
  }
}
```

2. Register a new collection box.

`POST /collectionBoxes`

3. List all collection boxes. Include information if the box is assigned (but don’t expose to what
fundraising event) and if it is empty or not (but don’t expose the actual value in the box).

`GET /collectionBoxes`

4. Unregister (remove) a collection box (e.g. in case it was damaged or stolen)

`DELETE /collectionBoxes/{boxId}`

5. Assign the collection box to an existing fundraising event.

`PUT /collectionBoxes/assign`

```
{
  "req":{
    "idBox": 1,
    "idEvent": 1
  }
}
```

6. Put (add) some money inside the collection box.

`PUT /collectionBoxes/deposit`

```
{
  "req":{
    "boxId": 1,
    "amount": 100.0,
    "fundsCurrencyCode": "PLN"
  }
}
```

7. Empty the collection box i.e. “transfer” money from the box to the fundraising event’s account.

`PUT /collectionBoxes/{boxId}`

8. Display a financial report with all fundraising events and the sum of their accounts

`GET /events/financialReport`

## Testing
<p>Service components are tested manually using "manualServiceTests" function in "src/main/java/edu/pja/sii_lat/DataInitializer.java", because at some point mocks suddenly stopped working (even after using when().thenReturn(), mocks return nulls or empty collections). Automatic tests are prepared in "src/test/java/edu/pja/sii_lat/service" package, but they fail.</p> 
