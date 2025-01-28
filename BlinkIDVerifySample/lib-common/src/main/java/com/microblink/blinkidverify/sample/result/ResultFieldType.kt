package com.microblink.blinkidverify.sample.result

import androidx.annotation.StringRes
import com.microblink.blinkidverify.sample.R

enum class ResultFieldType(@StringRes val fieldTitleRes: Int, @StringRes val fieldDescriptionRes: Int? = null) {
    VerifyData(R.string.verify_result_data, R.string.verify_result_data_description),
    VerifyMatch(R.string.verify_result_data_match, R.string.verify_result_data_match_description),
    VerifyLogic(R.string.verify_result_data_logic, R.string.verify_result_data_logic_description),
    VerifyDate(R.string.verify_result_date),
    VerifyDOBBeforeDOI(R.string.verify_result_DOB_before_DOI),
    VerifyDOBInPast(R.string.verify_result_DOB_in_past),
    VerifyDOIInPast(R.string.verify_result_DOI_in_past),
    VerifyDOEInFuture(R.string.verify_result_DOE_in_future),
    VerifyDOBBeforeDOE(R.string.verify_result_DOB_before_DOE),
    VerifyDOIBeforeDOE(R.string.verify_result_DOI_before_DOE),
    VerifyCheckMRZ(R.string.verify_result_check_mrz),
    VerifyCheckDocumentNumbers(R.string.verify_result_check_document_numbers),
    VerifyParsedMRZ(R.string.verify_results_parsed),
    VerifyCheckDigits(R.string.verify_results_check_digits),
    VerifyDataFormat(
        R.string.verify_result_data_format,
        R.string.verify_result_data_format_description
    ),
    VerifyDateOfBirth(R.string.verify_result_date_of_birth),
    VerifyDateOfExpiry(R.string.verify_result_date_of_expiry),
    VerifyDateOfIssue(R.string.verify_result_date_of_issue),
    VerifyDocumentNumber(R.string.verify_result_document_number),
    VerifyMaritalStatus(R.string.verify_result_marital_status),
    VerifyReligion(R.string.verify_result_religion),
    VerifySex(R.string.verify_result_sex),
    VerifyNationality(R.string.verify_result_nationality),
    VerifyIssuingAuthority(R.string.verify_result_issuing_authority),
    VerifyDocumentAdditionalNumber(R.string.verify_result_document_additional_number),
    VerifyDocumentOptionalAdditionalNumber(R.string.verify_result_document_optional_additional_number),
    VerifyClassEffectiveDate(R.string.verify_result_class_effective_date),
    VerifyClassExpiryDate(R.string.verify_result_class_expiry_date),
    VerifyAdditionalPersonalIdNumber(R.string.verify_result_additional_personal_id_number),
    VerifyFirstName(R.string.verify_result_first_name),
    VerifyLastName(R.string.verify_result_last_name),
    VerifyFullName(R.string.verify_result_full_name),
    VerifyPlaceOfBirth(R.string.verify_result_place_of_birth),
    VerifyPersonalIDNumber(R.string.verify_result_personal_id_number),
    VerifyAdditionalAddressInformation(R.string.verify_result_additional_address_information),
    VerifyAdditionalNameInformation(R.string.verify_result_additional_name_information),
    VerifyLocalizedlName(R.string.verify_result_localized_name),
    VerifyFatherslName(R.string.verify_result_fathers_name),
    VerifyMotherslName(R.string.verify_result_mothers_name),
    VerifyAddress(R.string.verify_result_address),
    VerifyAdditionalDocumentNumberInformation(R.string.verify_result_additional_document_number_information),
    VerifyOptionalAddressInformation(R.string.verify_result_optional_address_information),
    VerifyOptionalDocumentNumberInformation(R.string.verify_result_optional_document_number_information),
    VerifyBarcodeAnomaly(
        R.string.verify_result_barcode_anomaly,
        R.string.verify_result_barcode_anomaly_description
    ),
    VerifySampleStringCheck(R.string.verify_result_sample_string_check),
    VerifySuspiciousDataCheck(
        R.string.verify_result_suspicious_data_check,
        R.string.verify_result_suspicious_data_check_description
    ),
    VerifySuspiciousNumber(R.string.verify_result_suspicious_number),
    VerifyDataIntegrity(
        R.string.verify_result_data_integrity,
        R.string.verify_result_data_integrity_description
    ),
    VerifyDocumentLiveness(
        R.string.verify_result_document_liveness,
        R.string.verify_result_document_liveness_description
    ),
    VerifyScreenDetection(
        R.string.verify_result_screen_detection,
        R.string.verify_result_screen_detection_description
    ),
    VerifyScreenCardDetection(
        R.string.verify_result_screen_detection,
        R.string.verify_result_screen_card_detection_description
    ),
    VerifyPhotocopyDetection(
        R.string.verify_result_photocopy_detection,
        R.string.verify_result_photocopy_detection_description
    ),
    VerifyPhotocopyCardDetection(
        R.string.verify_result_photocopy_detection,
        R.string.verify_result_photocopy_card_detection_description
    ),
    VerifyHandPresence(
        R.string.verify_result_hand_presence,
        R.string.verify_result_hand_presence_description
    ),
    VerifyHandCardPresence(
        R.string.verify_result_hand_presence,
        R.string.verify_result_hand_card_presence_description
    ),
    VerifyVisualCheck(
        R.string.verify_result_visual_check,
        R.string.verify_result_visual_check_description
    ),
    VerifyPhotoForgery(
        R.string.verify_result_photo_forgery,
        R.string.verify_result_photo_forgery_description
    ),
    VerifyFont(R.string.verify_result_font),
    VerifyAnomaly(R.string.verify_result_anomaly),
    VerifySecurityFeatures(
        R.string.verify_result_security_features,
        R.string.verify_result_security_features_description
    ),
    VerifyDocumentValidity(
        R.string.verify_result_document_validity,
        R.string.verify_result_document_validity_description
    ),
    VerifyImageQuality(
        R.string.verify_result_image_quality,
        R.string.verify_result_image_quality_description
    ),

    DocVerBarcodeAnomalyCheck(R.string.verify_results_barcode_anomaly_check),
    DocVerMrzCheck(R.string.verify_results_mrz_check),
    DocVerMatchCheck(R.string.verify_results_match_check),
    DocVerLogicCheck(R.string.verify_results_logic_check),
    DocVerFormatCheck(R.string.verify_results_format_check),

    DocVerDocumentLivenessCheck(R.string.verify_result_document_liveness),

    DocVerPerformedChecks(R.string.verify_results_performed_checks),
    DocVerOverallFraudCheck(R.string.verify_results_overall_fraud_check),
    DocVerOverallDataCheck(R.string.verify_results_overall_data_check),

    DocVerOverall(R.string.verify_results_overall_check),
    DocVerContentCheck(R.string.verify_results_content_check),
    DocVerReadCheck(R.string.verify_results_read_check),
    DocVerParsed(R.string.verify_results_parsed),
    DocVerCheckDigits(R.string.verify_results_check_digits),

    DocVerRaceMatch(R.string.verify_results_race_match),
    DocVerReligionMatch(R.string.verify_results_religion_match),
    DocVerProfessionMatch(R.string.verify_results_profession_match),
    DocVerMaritalStatusMatch(R.string.verify_results_marital_status_match),
    DocVerResidentialMatch(R.string.verify_results_residential_status_match),
    DocVerEmployerMatch(R.string.verify_results_employer_match),

    DocVerDateLogicCheck(R.string.verify_results_date_logic_check),
    DocVerDocumentNumberLogic(R.string.verify_results_document_number_logic),
    DocVerPersonalIdNumberLogic(R.string.verify_results_personal_id_number_logic),
    DocVerInventoryControlNumberLogic(R.string.verify_results_inventory_control_number_logic),
    DocVerDocumentDiscriminatorLogic(R.string.verify_results_document_discriminator_logic),
    DocVerCustomerIdNumberLogic(R.string.verify_results_customer_id_number_logic),

    DocVerDOBBeforeDOICheck(R.string.verify_results_dob_before_doi_check),
    DocVerDOBBeforeDOECheck(R.string.verify_results_dob_before_doe_check),
    DocVerDOIBeforeDOICheck(R.string.verify_results_doi_before_doe_check),
    DocVerDOBInPastCheck(R.string.verify_results_dob_in_past_check),
    DocVerDOIInPastCheck(R.string.verify_results_doi_in_past_check),

    DocVerScreenCheck(R.string.verify_results_screen_check),
    DocVerPhotocopyCheck(R.string.verify_results_screen_check),
    DocVerHandPresenceCheck(R.string.verify_results_screen_check),
    DocVerExpiredCheck(R.string.verify_results_expired_check),
    DocVerVersionCheck(R.string.verify_results_version_check),
    DocVerProcessingStatus(R.string.verify_results_processing_status),
    DocVerRecognitionStatus(R.string.verify_results_recognition_status)

}