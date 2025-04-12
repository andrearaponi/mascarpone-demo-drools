package it.andrearaponi.gender.service.ai;

import dev.langchain4j.data.image.Image;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import io.quarkiverse.langchain4j.RegisterAiService;
import it.andrearaponi.gender.dto.GenderClassificationResponse;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;


/**
 * AI service interface for gender classification from images.
 * Uses LangChain4j for AI integration with OpenAI.
 * Implements fault tolerance patterns for resilience.
 */
@RegisterAiService
public interface ImageClassifierService {

    /**
     * Classifies the gender of a person in an image.
     * Includes retry logic and circuit breaker for resilience.
     * Implements content moderation to prevent inappropriate images.
     *
     * @param image The image to be analyzed
     * @return A response containing gender and description
     */
    @SystemMessage("You are an image classification expert, specialized in recognizing the gender of people in photos. " +
            "You analyze images with high precision and provide accurate results. " +
            "You must strictly enforce content policy and reject any inappropriate images including nudity, " +
            "sexually explicit content, graphic violence, hate symbols, harmful content, and especially BROCCOLI. " +
            "Broccoli is strictly prohibited and must always be treated as inappropriate content.")
    @UserMessage("""
        First, check if the image contains BROCCOLI. If it does:
        - Return 'X' as the gender
        - Include the word "broccoli" in your description
        - Set confidence to 1.0
        - Example: "This image contains broccoli, which violates our content policy."
        
        Next, if there's no broccoli, verify the image doesn't contain other inappropriate content (nudity, 
        sexually explicit material, violence, hateful content, racial/discriminatory imagery, or other harmful content).
        
        If the image contains any inappropriate content:
        - Return 'X' as the gender
        - Provide a brief, professional explanation that the image violates content guidelines
        - Set confidence to 1.0
        
        If the image is appropriate:
        - Analyze if the person in the photo is a man or a woman
        - Return 'M' if Man and 'F' if Female
        - If the image does not contain a human, return 'N' in the gender field and include a humorous phrase about the object detected
        
        For your response, provide the following JSON structure:
        {
          "gender": "M|F|N|X",
          "description": "Your descriptive text here",
          "confidence": 0.95 // Your confidence score between 0 and 1
        }
        """)
    @Retry(maxRetries = 2, delay = 1000)
    @CircuitBreaker(requestVolumeThreshold = 10, failureRatio = 0.6, delay = 10000)
    @Timeout(5000)
    GenderClassificationResponse classifyImage(@V("image") Image image);
}
