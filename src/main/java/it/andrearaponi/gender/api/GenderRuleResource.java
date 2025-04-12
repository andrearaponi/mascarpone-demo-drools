package it.andrearaponi.gender.api;

import dev.langchain4j.data.image.Image;
import it.andrearaponi.gender.exception.ImageValidationException;
import it.andrearaponi.gender.rules.DroolsGenderValidator;
import it.andrearaponi.gender.dto.GenderClassificationResponse;
import it.andrearaponi.gender.service.ai.ImageClassifierService;
import it.andrearaponi.gender.util.ImageConverter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import java.io.IOException;
import java.nio.file.Files;

@Path("/api/v1/gender")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
public class GenderRuleResource {

    private static final Logger LOG = Logger.getLogger(GenderRuleResource.class);
    private final DroolsGenderValidator validator;
    private final ImageClassifierService imageClassifierService;
    private final ImageConverter imageConverter;



    @Inject
    public GenderRuleResource(DroolsGenderValidator validator, ImageClassifierService imageClassifierService, ImageConverter imageConverter) {
        this.validator = validator;
        this.imageClassifierService = imageClassifierService;
        this.imageConverter = imageConverter;
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response detectGender(@RestForm("image") FileUpload image) throws IOException {

        if (image == null) {
            // Use specific exception for validation
            throw new ImageValidationException("Image file is missing");
        }
        if (image.uploadedFile() == null || !Files.exists(image.uploadedFile())) {
            // Use specific exception for validation
            throw new ImageValidationException("Image file is empty or does not exist");
        }

        LOG.debug(String.format("Processing image upload of type: %s", image.contentType()));

        // Convert the uploaded file to an Image object
        Image langchainImage = imageConverter.convertToLangChainImage(image);

        // Invoke the AI service to classify the image
        long startClassification = System.currentTimeMillis();
        GenderClassificationResponse classificationResult = imageClassifierService.classifyImage(langchainImage);
        long classificationTime = System.currentTimeMillis() - startClassification;
        LOG.debug(String.format("Image classification completed in %d ms", classificationTime));

        // Validate the classification result using Drools
        long startValidation = System.currentTimeMillis();
        GenderClassificationResponse result = validator.process(classificationResult);
        long validationTime = System.currentTimeMillis() - startValidation;
        LOG.info(String.format("Gender classification processed in %d ms", validationTime));

        // Return the classification result as a structured response
        return Response.ok(result).build();
    }
}