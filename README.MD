# Mascarpone Demo Drools

A simple gender classification service built with Quarkus, combining Drools rules engine and AI-powered image recognition capabilities.

![Mascarpone demo Preview](https://i.imgur.com/Mdqgr3w.gif)

## Project Overview

This project implements a gender classification service that leverages multiple technologies to provide accurate and reliable gender detection from images while maintaining strict content policies.

### Key Features

- Image-based gender classification
- AI-powered image analysis using OpenAI's GPT-4 Vision
- Rule-based validation using Drools
- Content policy enforcement
- Fault tolerance and circuit breaker patterns
- RESTful API endpoints

## Technical Stack

- **Framework**: Quarkus 3.15.4
- **Java Version**: 21 
- **Key Dependencies**:
    - Drools 10.0.0 for rule engine
    - LangChain4j for AI integration with OpenAI
    - Quarkus REST for API endpoints
    - SmallRye Fault Tolerance for resilience
    - Hibernate Validator for validation

## Architecture

### Components

1. **REST API Layer** (`GenderRuleResource`)
    - Handles HTTP requests
    - Processes multipart form data for image uploads
    - Coordinates between services

2. **AI Service** (`ImageClassifierService`)
    - Integrates with OpenAI's vision models
    - Performs image classification
    - Enforces content policies
    - Implements fault tolerance patterns

3. **Rules Engine** (`DroolsGenderValidator`)
    - Validates and processes gender classification results
    - Applies business rules to classification outcomes

## API Details

### Gender Classification Endpoint
- **Path**: `/api/v1/gender`
- **Method**: POST
- **Content-Type**: multipart/form-data
- **Request Parameter**:
    - `image`: File upload (image file)
- **Response Format**:
```json
{
  "gender": "M|F|N|X",
  "description": "Classification description",
  "confidence": 0.95
}
```
- **Gender Codes**:
    - `M`: Male
    - `F`: Female
    - `N`: Not a person
    - `X`: Content policy violation

## Content Policy Details

The service enforces strict content moderation:

1. **Prohibited Content**:
    - Nudity and sexually explicit content
    - Graphic violence
    - Hate symbols
    - Discriminatory imagery
    - Harmful content
    - **Special case**: Broccoli (treated as prohibited content)

2. **Content Validation Process**:
    - Initial broccoli detection
    - Inappropriate content screening
    - Gender classification (if content is appropriate)

3. **Response Handling**:
    - Appropriate content: Normal gender classification
    - Prohibited content: Returns 'X' with explanation
    - Broccoli detection: Immediate rejection

## Configuration

### Application Properties
```properties
quarkus.http.port=8081
drools.kbase.genderKB.packages=it.andrearaponi.gender
drools.kbase.genderKB.ksession=gender
quarkus.langchain4j.openai.api-key=${OPENAI_API_KEY}
quarkus.langchain4j.openai.chat-model.model-name=gpt-4o-mini
quarkus.langchain4j.openai.timeout=300s
quarkus.langchain4j.openai.chat-model.multi-modal=true
```

## Fault Tolerance Implementation

The service implements robust resilience patterns:

1. **Retry Mechanism**:
    - Maximum retries: 5
    - Delay between retries: 1 second
    - `@Retry(maxRetries = 5, delay = 1000)`

2. **Circuit Breaker**:
    - Request threshold: 10 requests
    - Failure ratio: 0.6 (60%)
    - Delay: 10 seconds
    - `@CircuitBreaker(requestVolumeThreshold = 10, failureRatio = 0.6, delay = 10000)`

3. **Timeout Protection**:
    - Timeout: 5 seconds
    - `@Timeout(5000)`

## Building and Running

### Development Mode
```bash
./mvnw compile quarkus:dev
```
Access the Dev UI at: http://localhost:8081/q/dev/

### Production Build
```bash
./mvnw package
```
Run with: `java -jar target/quarkus-app/quarkus-run.jar`

### Native Build
```bash
./mvnw package -Pnative
```
Or with container build:
```bash
./mvnw package -Pnative -Dquarkus.native.container-build=true
```


## Security Considerations

1. **Image Validation**:
    - Content policy enforcement
    - File type verification
    - Size limitations

2. **Service Protection**:
    - Circuit breaker implementation
    - Timeout controls
    - Rate limiting

3. **Error Handling**:
    - Graceful degradation
    - Proper error messages
    - Secure error responses

