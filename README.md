# Restaurant Chatbot - Spring Boot API

A production-ready AI chatbot API built with Spring Boot and Claude AI (Anthropic).

## Quick Start (5 minutes)

### Prerequisites
- Java 11+
- Maven 3.6+

### Step 1: Clone/Download this project
```bash
cd restaurant-chatbot
```

### Step 2: Set your API key (when ready to test with real API)
```bash
# Linux/Mac
export ANTHROPIC_API_KEY=sk-ant-api03-your-key-here

# Windows
set ANTHROPIC_API_KEY=sk-ant-api03-your-key-here
```

### Step 3: Build and run
```bash
mvn clean install
mvn spring-boot:run
```

The API will start at: **http://localhost:8080**

## API Endpoints

### 1. Chat Endpoint (POST)
```
POST http://localhost:8080/api/chat
Content-Type: application/json

{
  "messages": [
    {
      "role": "user",
      "content": "What are your hours?"
    }
  ]
}
```

**Response:**
```json
{
  "reply": "Bella Roma is open Mon-Thu 11am-10pm, Fri-Sat 11am-11pm, and Sun 12pm-10pm."
}
```

### 2. Health Check (GET)
```
GET http://localhost:8080/api/health
```

**Response:**
```json
{
  "status": "OK",
  "service": "Restaurant Chatbot API"
}
```

## Testing with cURL

```bash
# Test if server is running
curl http://localhost:8080/api/health

# Send a chat message
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{
    "messages": [
      {
        "role": "user",
        "content": "What are your vegetarian options?"
      }
    ]
  }'
```

## Project Structure

```
restaurant-chatbot/
├── pom.xml                                    # Maven dependencies
├── src/main/
│   ├── java/com/chatbot/
│   │   ├── RestaurantChatbotApplication.java # Main app
│   │   ├── config/
│   │   │   └── CorsConfig.java               # CORS setup
│   │   ├── controller/
│   │   │   └── ChatController.java           # REST endpoints
│   │   ├── service/
│   │   │   └── AnthropicService.java         # API logic
│   │   └── dto/
│   │       ├── ChatRequest.java
│   │       └── ChatResponse.java
│   └── resources/
│       └── application.properties             # Configuration
└── README.md
```

## Configuration

Edit `application.properties` to customize:

```properties
# Server port
server.port=8080

# Anthropic API
anthropic.api.key=${ANTHROPIC_API_KEY:}  # Set via environment variable
anthropic.model=claude-haiku-4-5-20251001
anthropic.max-tokens=500

# CORS - allows requests from any origin (change for production!)
spring.web.cors.allowed-origins=*
```

## Customizing for Your Client

Open `AnthropicService.java` and update the `SYSTEM_PROMPT` variable with:
- Restaurant name
- Address & phone
- Hours
- Menu items
- Special instructions

Example for a medical clinic:
```java
private static final String SYSTEM_PROMPT = """
You are a helpful AI assistant for Dr. Smith's Dental Clinic...
""";
```

## Without API Key (Testing Locally)

You can test the API structure without an API key:

1. Run the application as-is
2. Send test requests to `/api/chat`
3. You'll get an error message: "ANTHROPIC_API_KEY environment variable not set"
4. This proves the API structure works — once you add the key, it'll call Claude

## Deployment (Later)

When you're ready to deploy:

### Option 1: Railway.app (recommended for you)
1. Push to GitHub
2. Connect to Railway
3. Add `ANTHROPIC_API_KEY` as environment variable
4. Deploy — done!

### Option 2: Docker
```bash
docker build -t restaurant-chatbot .
docker run -e ANTHROPIC_API_KEY=your-key -p 8080:8080 restaurant-chatbot
```

### Option 3: Traditional Server
```bash
java -jar target/restaurant-chatbot-1.0.0.jar
```

## Troubleshooting

**Q: "ANTHROPIC_API_KEY environment variable not set"**
- A: You need to set the environment variable before running. The error is expected until you get your API key.

**Q: Port 8080 already in use?**
- A: Change in `application.properties`: `server.port=8081`

**Q: Getting CORS errors?**
- A: CORS is already enabled. If still issues, check the browser console.

## Next Steps

1. ✅ Run this locally and test the API structure
2. 🔑 Get your Anthropic API key ($5.90 purchase)
3. 🧪 Set the environment variable and test with real Claude responses
4. 🎨 Build the frontend chat widget (we'll help with this)
5. 💼 Customize for your first client
6. 🚀 Deploy to Railway and start earning!

## Support

- Anthropic API docs: https://docs.anthropic.com
- Spring Boot docs: https://spring.io/projects/spring-boot
- Questions? Check AnthropicService.java for detailed comments

Good luck! 🚀
