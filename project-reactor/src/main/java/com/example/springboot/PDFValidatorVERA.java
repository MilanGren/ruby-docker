package com.example.springboot;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.verapdf.core.EncryptedPdfException;
import org.verapdf.core.ModelParsingException;
import org.verapdf.core.ValidationException;
import org.verapdf.pdfa.Foundries;
import org.verapdf.pdfa.PDFAParser;
import org.verapdf.pdfa.PDFAValidator;
import org.verapdf.pdfa.VeraGreenfieldFoundryProvider;
import org.verapdf.pdfa.flavours.PDFAFlavour;
import org.verapdf.pdfa.results.ValidationResult;

import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;

@Service
public class PDFValidatorVERA {

    private static final Logger log = LoggerFactory.getLogger(PDFValidatorVERA.class);

    @Value("${max.number.of.errors:20}")
    private int maxNumberOfErrors;

    public PDFValidatorVERA() {
        VeraGreenfieldFoundryProvider.initialise();
    }

    public void validate(InputStream istream, String askedFlavourId, ResultPdfaValidation result) throws PDFValidationException {

        // case insensitive aks
        if (PDFAFlavour.byFlavourId(askedFlavourId) == PDFAFlavour.NO_FLAVOUR) {
            throw new PDFValidationException(askedFlavourId + " is not available in VERA defined flavours");
        }

        PDFAFlavour flavour = PDFAFlavour.fromString(askedFlavourId);
        try (PDFAValidator validator = Foundries.defaultInstance().createValidator(flavour, false);
             PDFAParser parser = Foundries.defaultInstance().createParser(istream, flavour)
        ) {
            ValidationResult validationResult = validator.validate(parser);
            if (validationResult.isCompliant()) {
                result.create(true, askedFlavourId, flavour.getPart().getId());
            } else {
                result.create(false, askedFlavourId, PDFAFlavour.NO_FLAVOUR.getPart().getId());
                validationResult.getTestAssertions().subList(0, maxNumberOfErrors).forEach(t -> {
                    log.error("given pdf is not valid; log of assertions: {}\n\t{}", t.getRuleId(), t.getMessage());
                });
            }
        } catch (ModelParsingException | EncryptedPdfException | ValidationException | NoSuchElementException e) {
            throw new PDFValidationException("pdf validation exception", e);
        } catch (IOException e) {
            throw new PDFValidationException("Cannot validator and parser", e);
        }

    }

}
