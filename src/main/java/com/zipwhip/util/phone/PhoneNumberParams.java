package com.zipwhip.util.phone;

/**
 * User: Ali Serghini
 * Date: 9/24/12
 * Time: 11:17 AM
 */
public class PhoneNumberParams {
    private final String phoneNumber;
    private final String regionCode;

    public PhoneNumberParams(String phoneNumber, String regionCode) {
        this.phoneNumber = phoneNumber;
        // Default to unknown
        this.regionCode = regionCode == null ? "ZZ" : regionCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getRegionCode() {
        return regionCode;
    }

    @Override
    public String toString() {
        return "PhoneNumberParams{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", regionCode='" + regionCode + '\'' +
                '}';
    }
}
