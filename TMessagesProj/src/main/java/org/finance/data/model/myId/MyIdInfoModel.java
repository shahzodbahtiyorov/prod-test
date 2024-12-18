package org.finance.data.model.myId;

import com.google.gson.annotations.SerializedName;

/** @noinspection ALL*/
public class MyIdInfoModel {

    @SerializedName("address")
    private Address address;

    @SerializedName("contacts")
    private Contacts contacts;

    @SerializedName("doc_data")
    private DocData docData;

    @SerializedName("common_data")
    private CommonData commonData;

    @SerializedName("authentication_method")
    private String authenticationMethod;

    // Getters
    public Address getAddress() {
        return address;
    }

    public Contacts getContacts() {
        return contacts;
    }

    public DocData getDocData() {
        return docData;
    }

    public CommonData getCommonData() {
        return commonData;
    }

    public String getAuthenticationMethod() {
        return authenticationMethod;
    }

    public static class Address {
        @SerializedName("permanent_address")
        private String permanentAddress;

        @SerializedName("temporary_address")
        private String temporaryAddress;

        @SerializedName("permanent_registration")
        private Registration permanentRegistration;

        @SerializedName("temporary_registration")
        private Registration temporaryRegistration;

        // Getters
        public String getPermanentAddress() {
            return permanentAddress;
        }

        public String getTemporaryAddress() {
            return temporaryAddress;
        }

        public Registration getPermanentRegistration() {
            return permanentRegistration;
        }

        public Registration getTemporaryRegistration() {
            return temporaryRegistration;
        }
    }

    public static class Registration {
        @SerializedName("region")
        private String region;

        @SerializedName("address")
        private String address;

        @SerializedName("country")
        private String country;

        @SerializedName("cadastre")
        private String cadastre;

        @SerializedName("district")
        private String district;

        @SerializedName("date_from")
        private String dateFrom;

        @SerializedName("date_till")
        private String dateTill;

        @SerializedName("region_id")
        private String regionId;

        @SerializedName("country_id")
        private String countryId;

        @SerializedName("district_id")
        private String districtId;

        @SerializedName("region_id_cbu")
        private String regionIdCbu;

        @SerializedName("country_id_cbu")
        private String countryIdCbu;

        @SerializedName("district_id_cbu")
        private String districtIdCbu;

        // Getters
        public String getRegion() {
            return region;
        }

        public String getAddress() {
            return address;
        }

        public String getCountry() {
            return country;
        }

        public String getCadastre() {
            return cadastre;
        }

        public String getDistrict() {
            return district;
        }

        public String getDateFrom() {
            return dateFrom;
        }

        public String getDateTill() {
            return dateTill;
        }

        public String getRegionId() {
            return regionId;
        }

        public String getCountryId() {
            return countryId;
        }

        public String getDistrictId() {
            return districtId;
        }

        public String getRegionIdCbu() {
            return regionIdCbu;
        }

        public String getCountryIdCbu() {
            return countryIdCbu;
        }

        public String getDistrictIdCbu() {
            return districtIdCbu;
        }
    }

    public static class Contacts {
        @SerializedName("email")
        private String email;

        @SerializedName("phone")
        private String phone;

        // Getters
        public String getEmail() {
            return email;
        }

        public String getPhone() {
            return phone;
        }
    }

    public static class DocData {
        @SerializedName("doc_type")
        private String docType;

        @SerializedName("issued_by")
        private String issuedBy;

        @SerializedName("pass_data")
        private String passData;

        @SerializedName("doc_type_id")
        private String docTypeId;

        @SerializedName("expiry_date")
        private String expiryDate;

        @SerializedName("issued_date")
        private String issuedDate;

        @SerializedName("issued_by_id")
        private String issuedById;

        @SerializedName("doc_type_id_cbu")
        private String docTypeIdCbu;

        // Getters
        public String getDocType() {
            return docType;
        }

        public String getIssuedBy() {
            return issuedBy;
        }

        public String getPassData() {
            return passData;
        }

        public String getDocTypeId() {
            return docTypeId;
        }

        public String getExpiryDate() {
            return expiryDate;
        }

        public String getIssuedDate() {
            return issuedDate;
        }

        public String getIssuedById() {
            return issuedById;
        }

        public String getDocTypeIdCbu() {
            return docTypeIdCbu;
        }
    }

    public static class CommonData {
        @SerializedName("pinfl")
        private String pinfl;

        @SerializedName("gender")
        private String gender;

        @SerializedName("doc_type")
        private String docType;

        @SerializedName("sdk_hash")
        private String sdkHash;

        @SerializedName("last_name")
        private String lastName;

        @SerializedName("birth_date")
        private String birthDate;

        @SerializedName("first_name")
        private String firstName;

        @SerializedName("birth_place")
        private String birthPlace;

        @SerializedName("citizenship")
        private String citizenship;

        @SerializedName("doc_type_id")
        private String docTypeId;

        @SerializedName("middle_name")
        private String middleName;

        @SerializedName("nationality")
        private String nationality;

        @SerializedName("last_name_en")
        private String lastNameEn;

        @SerializedName("birth_country")
        private String birthCountry;

        @SerializedName("first_name_en")
        private String firstNameEn;

        @SerializedName("citizenship_id")
        private String citizenshipId;

        @SerializedName("nationality_id")
        private String nationalityId;

        @SerializedName("doc_type_id_cbu")
        private String docTypeIdCbu;

        @SerializedName("birth_country_id")
        private String birthCountryId;

        @SerializedName("citizenship_id_cbu")
        private String citizenshipIdCbu;

        @SerializedName("nationality_id_cbu")
        private String nationalityIdCbu;

        @SerializedName("last_update_address")
        private String lastUpdateAddress;

        @SerializedName("birth_country_id_cbu")
        private String birthCountryIdCbu;

        @SerializedName("last_update_pass_data")
        private String lastUpdatePassData;

        // Getters
        public String getPinfl() {
            return pinfl;
        }

        public String getGender() {
            return gender;
        }

        public String getDocType() {
            return docType;
        }

        public String getSdkHash() {
            return sdkHash;
        }

        public String getLastName() {
            return lastName;
        }

        public String getBirthDate() {
            return birthDate;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getBirthPlace() {
            return birthPlace;
        }

        public String getCitizenship() {
            return citizenship;
        }

        public String getDocTypeId() {
            return docTypeId;
        }

        public String getMiddleName() {
            return middleName;
        }

        public String getNationality() {
            return nationality;
        }

        public String getLastNameEn() {
            return lastNameEn;
        }

        public String getBirthCountry() {
            return birthCountry;
        }

        public String getFirstNameEn() {
            return firstNameEn;
        }

        public String getCitizenshipId() {
            return citizenshipId;
        }

        public String getNationalityId() {
            return nationalityId;
        }

        public String getDocTypeIdCbu() {
            return docTypeIdCbu;
        }

        public String getBirthCountryId() {
            return birthCountryId;
        }

        public String getCitizenshipIdCbu() {
            return citizenshipIdCbu;
        }

        public String getNationalityIdCbu() {
            return nationalityIdCbu;
        }

        public String getLastUpdateAddress() {
            return lastUpdateAddress;
        }

        public String getBirthCountryIdCbu() {
            return birthCountryIdCbu;
        }

        public String getLastUpdatePassData() {
            return lastUpdatePassData;
        }
    }
}
